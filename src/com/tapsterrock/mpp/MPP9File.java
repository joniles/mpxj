/*
 * file:       MPP9File.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       22/05/2003
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package com.tapsterrock.mpp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import com.tapsterrock.mpx.AccrueType;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.CurrencySettings;
import com.tapsterrock.mpx.DateTimeSettings;
import com.tapsterrock.mpx.DefaultSettings;
import com.tapsterrock.mpx.MPXCalendar;
import com.tapsterrock.mpx.MPXCalendarException;
import com.tapsterrock.mpx.MPXCalendarHours;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXException;
import com.tapsterrock.mpx.MPXRate;
import com.tapsterrock.mpx.Priority;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.ResourceAssignment;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;

/**
 * This class is used to represent a Microsoft Project MPP9 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP9File
{
   /**
    * This method is used to process an MPP9 file. This is the file format
    * used by Project 2000, 2002, and 2003.
    *
    * @param file parent MPP file
    * @param root Root of the POI file system.
    * @throws MPXException Normally thrown on dat validation errors
    */
   static void process (MPPFile file, DirectoryEntry root)
      throws MPXException, IOException
   {
      //
      // Retrieve the high level document properties
      //
      Props9 props9 = new Props9 (new DocumentInputStream (((DocumentEntry)root.getEntry("Props9"))));

      //
      // Test for password protection. In the single byte retrieved here:
      //
      // 0x00 = no password
      // 0x01 = protection password has been supplied
      // 0x02 = write reservation password has been supplied
      // 0x03 = both passwords have been supplied
      //
      if ((props9.getByte(Props.PASSWORD_FLAG) & 0x01) != 0)
      {
         throw new MPXException (MPXException.PASSWORD_PROTECTED);
      }

      //
      // Retrieve the project directory
      //
      DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   19");


      DirectoryEntry outlineCodeDir = (DirectoryEntry)projectDir.getEntry ("TBkndOutlCode");
      VarMeta outlineCodeVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("VarMeta"))));
      Var2Data outlineCodeVarData = new Var2Data (outlineCodeVarMeta, new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("Var2Data"))));

      //
      // Extract the required data from the MPP file
      //
      HashMap resourceMap = new HashMap ();

      processPropertyData (file, projectDir);
      processCalendarData (file, projectDir, resourceMap);
      processResourceData (file, projectDir, outlineCodeVarData, resourceMap);
      processTaskData (file, projectDir, outlineCodeVarData);
      processConstraintData (file, projectDir);
      processAssignmentData (file, projectDir);

      projectDir = (DirectoryEntry)root.getEntry ("   29");
      processViewData (file, projectDir);
      processTableData (file, projectDir);
   }


   /**
    * This method extracts and collates global property data.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws IOException
    */
   private static void processPropertyData (MPPFile file,  DirectoryEntry projectDir)
      throws IOException
   {
      Props9 props = new Props9 (new DocumentInputStream (((DocumentEntry)projectDir.getEntry("Props"))));

      DateTimeSettings dts = file.getDateTimeSettings();
      dts.setDefaultStartTime(props.getTime(Props.START_TIME));
      dts.setDefaultEndTime(props.getTime(Props.END_TIME));

      DefaultSettings ds = file.getDefaultSettings();
      //ds.setDefaultDurationIsFixed();
      ds.setDefaultDurationUnits(MPPUtility.getDurationUnits(props.getShort(Props.DURATION_UNITS)));
      ds.setDefaultHoursInDay(((float)props.getInt(Props.HOURS_PER_DAY))/60);
      ds.setDefaultHoursInWeek(((float)props.getInt(Props.HOURS_PER_WEEK))/60);
      ds.setDefaultOvertimeRate(new MPXRate (props.getDouble(Props.OVERTIME_RATE), TimeUnit.HOURS));
      ds.setDefaultStandardRate(new MPXRate (props.getDouble(Props.STANDARD_RATE), TimeUnit.HOURS));
      ds.setDefaultWorkUnits(MPPUtility.getWorkUnits(props.getShort(Props.WORK_UNITS)));
      ds.setSplitInProgressTasks(props.getBoolean(Props.SPLIT_TASKS));
      ds.setUpdatingTaskStatusUpdatesResourceStatus(props.getBoolean(Props.TASK_UPDATES_RESOURCE));

      CurrencySettings cs = file.getCurrencySettings();
      cs.setCurrencyDigits(props.getShort(Props.CURRENCY_DIGITS));
      cs.setCurrencySymbol(props.getUnicodeString(Props.CURRENCY_SYMBOL));
      //cs.setDecimalSeparator();
      cs.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(Props.CURRENCY_PLACEMENT)));
      //cs.setThousandsSeparator();

      processTaskFieldNameAliases(file, props.getByteArray(Props.TASK_FIELD_NAME_ALIASES));
      processResourceFieldNameAliases(file, props.getByteArray(Props.RESOURCE_FIELD_NAME_ALIASES));
   }

   /**
    * Retrieve any task field aliases defined in the MPP file.
    *
    * @param file Parent MPX file
    * @param data task field name alias data
    */
   private static void processTaskFieldNameAliases (MPPFile file, byte[] data)
   {
      if (data != null)
      {
         int offset = 0;
         String alias;
         ArrayList aliases = new ArrayList();

         while (offset < data.length)
         {
            alias = MPPUtility.getUnicodeString(data, offset);
            aliases.add(alias);
            offset += (alias.length()+1)*2;
         }

         file.setTaskFieldAlias(Task.TEXT1, (String)aliases.get(118));
         file.setTaskFieldAlias(Task.TEXT2, (String)aliases.get(119));
         file.setTaskFieldAlias(Task.TEXT3, (String)aliases.get(120));
         file.setTaskFieldAlias(Task.TEXT4, (String)aliases.get(121));
         file.setTaskFieldAlias(Task.TEXT5, (String)aliases.get(122));
         file.setTaskFieldAlias(Task.TEXT6, (String)aliases.get(123));
         file.setTaskFieldAlias(Task.TEXT7, (String)aliases.get(124));
         file.setTaskFieldAlias(Task.TEXT8, (String)aliases.get(125));
         file.setTaskFieldAlias(Task.TEXT9, (String)aliases.get(126));
         file.setTaskFieldAlias(Task.TEXT10, (String)aliases.get(127 ));
         file.setTaskFieldAlias(Task.START1, (String)aliases.get(128));
         file.setTaskFieldAlias(Task.FINISH1, (String)aliases.get(129));
         file.setTaskFieldAlias(Task.START2, (String)aliases.get(130));
         file.setTaskFieldAlias(Task.FINISH2, (String)aliases.get(131));
         file.setTaskFieldAlias(Task.START3, (String)aliases.get(132));
         file.setTaskFieldAlias(Task.FINISH3, (String)aliases.get(133));
         file.setTaskFieldAlias(Task.START4, (String)aliases.get(134));
         file.setTaskFieldAlias(Task.FINISH4, (String)aliases.get(135));
         file.setTaskFieldAlias(Task.START5, (String)aliases.get(136));
         file.setTaskFieldAlias(Task.FINISH5, (String)aliases.get(137));
         file.setTaskFieldAlias(Task.START6, (String)aliases.get(138));
         file.setTaskFieldAlias(Task.FINISH6, (String)aliases.get(139));
         file.setTaskFieldAlias(Task.START7, (String)aliases.get(140));
         file.setTaskFieldAlias(Task.FINISH7, (String)aliases.get(141));
         file.setTaskFieldAlias(Task.START8, (String)aliases.get(142));
         file.setTaskFieldAlias(Task.FINISH8, (String)aliases.get(143));
         file.setTaskFieldAlias(Task.START9, (String)aliases.get(144));
         file.setTaskFieldAlias(Task.FINISH9, (String)aliases.get(145));
         file.setTaskFieldAlias(Task.START10, (String)aliases.get(146));
         file.setTaskFieldAlias(Task.FINISH10, (String)aliases.get(147));
         file.setTaskFieldAlias(Task.NUMBER1, (String)aliases.get(149));
         file.setTaskFieldAlias(Task.NUMBER2, (String)aliases.get(150));
         file.setTaskFieldAlias(Task.NUMBER3, (String)aliases.get(151));
         file.setTaskFieldAlias(Task.NUMBER4, (String)aliases.get(152));
         file.setTaskFieldAlias(Task.NUMBER5, (String)aliases.get(153));
         file.setTaskFieldAlias(Task.NUMBER6, (String)aliases.get(154));
         file.setTaskFieldAlias(Task.NUMBER7, (String)aliases.get(155));
         file.setTaskFieldAlias(Task.NUMBER8, (String)aliases.get(156));
         file.setTaskFieldAlias(Task.NUMBER9, (String)aliases.get(157));
         file.setTaskFieldAlias(Task.NUMBER10, (String)aliases.get(158));
         file.setTaskFieldAlias(Task.DURATION1, (String)aliases.get(159));
         file.setTaskFieldAlias(Task.DURATION2, (String)aliases.get(161));
         file.setTaskFieldAlias(Task.DURATION3, (String)aliases.get(163));
         file.setTaskFieldAlias(Task.DURATION4, (String)aliases.get(165));
         file.setTaskFieldAlias(Task.DURATION5, (String)aliases.get(167));
         file.setTaskFieldAlias(Task.DURATION6, (String)aliases.get(169));
         file.setTaskFieldAlias(Task.DURATION7, (String)aliases.get(171));
         file.setTaskFieldAlias(Task.DURATION8, (String)aliases.get(173));
         file.setTaskFieldAlias(Task.DURATION9, (String)aliases.get(175));
         file.setTaskFieldAlias(Task.DURATION10, (String)aliases.get(177));
         file.setTaskFieldAlias(Task.DATE1, (String)aliases.get(184));
         file.setTaskFieldAlias(Task.DATE2, (String)aliases.get(185));
         file.setTaskFieldAlias(Task.DATE3, (String)aliases.get(186));
         file.setTaskFieldAlias(Task.DATE4, (String)aliases.get(187));
         file.setTaskFieldAlias(Task.DATE5, (String)aliases.get(188));
         file.setTaskFieldAlias(Task.DATE6, (String)aliases.get(189));
         file.setTaskFieldAlias(Task.DATE7, (String)aliases.get(190));
         file.setTaskFieldAlias(Task.DATE8, (String)aliases.get(191));
         file.setTaskFieldAlias(Task.DATE9, (String)aliases.get(192));
         file.setTaskFieldAlias(Task.DATE10, (String)aliases.get(193));
         file.setTaskFieldAlias(Task.TEXT11, (String)aliases.get(194));
         file.setTaskFieldAlias(Task.TEXT12, (String)aliases.get(195));
         file.setTaskFieldAlias(Task.TEXT13, (String)aliases.get(196));
         file.setTaskFieldAlias(Task.TEXT14, (String)aliases.get(197));
         file.setTaskFieldAlias(Task.TEXT15, (String)aliases.get(198));
         file.setTaskFieldAlias(Task.TEXT16, (String)aliases.get(199));
         file.setTaskFieldAlias(Task.TEXT17, (String)aliases.get(200));
         file.setTaskFieldAlias(Task.TEXT18, (String)aliases.get(201));
         file.setTaskFieldAlias(Task.TEXT19, (String)aliases.get(202));
         file.setTaskFieldAlias(Task.TEXT20, (String)aliases.get(203));
         file.setTaskFieldAlias(Task.TEXT21, (String)aliases.get(204));
         file.setTaskFieldAlias(Task.TEXT22, (String)aliases.get(205));
         file.setTaskFieldAlias(Task.TEXT23, (String)aliases.get(206));
         file.setTaskFieldAlias(Task.TEXT24, (String)aliases.get(207));
         file.setTaskFieldAlias(Task.TEXT25, (String)aliases.get(208));
         file.setTaskFieldAlias(Task.TEXT26, (String)aliases.get(209));
         file.setTaskFieldAlias(Task.TEXT27, (String)aliases.get(210));
         file.setTaskFieldAlias(Task.TEXT28, (String)aliases.get(211));
         file.setTaskFieldAlias(Task.TEXT29, (String)aliases.get(212));
         file.setTaskFieldAlias(Task.TEXT30, (String)aliases.get(213));
         file.setTaskFieldAlias(Task.NUMBER11, (String)aliases.get(214));
         file.setTaskFieldAlias(Task.NUMBER12, (String)aliases.get(215));
         file.setTaskFieldAlias(Task.NUMBER13, (String)aliases.get(216));
         file.setTaskFieldAlias(Task.NUMBER14, (String)aliases.get(217));
         file.setTaskFieldAlias(Task.NUMBER15, (String)aliases.get(218));
         file.setTaskFieldAlias(Task.NUMBER16, (String)aliases.get(219));
         file.setTaskFieldAlias(Task.NUMBER17, (String)aliases.get(220));
         file.setTaskFieldAlias(Task.NUMBER18, (String)aliases.get(221));
         file.setTaskFieldAlias(Task.NUMBER19, (String)aliases.get(222));
         file.setTaskFieldAlias(Task.NUMBER20, (String)aliases.get(223));
         file.setTaskFieldAlias(Task.OUTLINECODE1, (String)aliases.get(227));
         file.setTaskFieldAlias(Task.OUTLINECODE2, (String)aliases.get(228));
         file.setTaskFieldAlias(Task.OUTLINECODE3, (String)aliases.get(229));
         file.setTaskFieldAlias(Task.OUTLINECODE4, (String)aliases.get(230));
         file.setTaskFieldAlias(Task.OUTLINECODE5, (String)aliases.get(231));
         file.setTaskFieldAlias(Task.OUTLINECODE6, (String)aliases.get(232));
         file.setTaskFieldAlias(Task.OUTLINECODE7, (String)aliases.get(233));
         file.setTaskFieldAlias(Task.OUTLINECODE8, (String)aliases.get(234));
         file.setTaskFieldAlias(Task.OUTLINECODE9, (String)aliases.get(235));
         file.setTaskFieldAlias(Task.OUTLINECODE10, (String)aliases.get(236));
         file.setTaskFieldAlias(Task.FLAG1, (String)aliases.get(237));
         file.setTaskFieldAlias(Task.FLAG2, (String)aliases.get(238));
         file.setTaskFieldAlias(Task.FLAG3, (String)aliases.get(239));
         file.setTaskFieldAlias(Task.FLAG4, (String)aliases.get(240));
         file.setTaskFieldAlias(Task.FLAG5, (String)aliases.get(241));
         file.setTaskFieldAlias(Task.FLAG6, (String)aliases.get(242));
         file.setTaskFieldAlias(Task.FLAG7, (String)aliases.get(243));
         file.setTaskFieldAlias(Task.FLAG8, (String)aliases.get(244));
         file.setTaskFieldAlias(Task.FLAG9, (String)aliases.get(245));
         file.setTaskFieldAlias(Task.FLAG10, (String)aliases.get(246));
         file.setTaskFieldAlias(Task.FLAG11, (String)aliases.get(247));
         file.setTaskFieldAlias(Task.FLAG12, (String)aliases.get(248));
         file.setTaskFieldAlias(Task.FLAG13, (String)aliases.get(249));
         file.setTaskFieldAlias(Task.FLAG14, (String)aliases.get(250));
         file.setTaskFieldAlias(Task.FLAG15, (String)aliases.get(251));
         file.setTaskFieldAlias(Task.FLAG16, (String)aliases.get(252));
         file.setTaskFieldAlias(Task.FLAG17, (String)aliases.get(253));
         file.setTaskFieldAlias(Task.FLAG18, (String)aliases.get(254));
         file.setTaskFieldAlias(Task.FLAG19, (String)aliases.get(255));
         file.setTaskFieldAlias(Task.FLAG20, (String)aliases.get(256));
         file.setTaskFieldAlias(Task.COST1, (String)aliases.get(278));
         file.setTaskFieldAlias(Task.COST2, (String)aliases.get(279));
         file.setTaskFieldAlias(Task.COST3, (String)aliases.get(280));
         file.setTaskFieldAlias(Task.COST4, (String)aliases.get(281));
         file.setTaskFieldAlias(Task.COST5, (String)aliases.get(282));
         file.setTaskFieldAlias(Task.COST6, (String)aliases.get(283));
         file.setTaskFieldAlias(Task.COST7, (String)aliases.get(284));
         file.setTaskFieldAlias(Task.COST8, (String)aliases.get(285));
         file.setTaskFieldAlias(Task.COST9, (String)aliases.get(286));
         file.setTaskFieldAlias(Task.COST10, (String)aliases.get(287));
      }
   }

   /**
    * Retrieve any resource field aliases defined in the MPP file.
    *
    * @param file Parent MPX file
    * @param data resource field name alias data
    */
   private static void processResourceFieldNameAliases (MPPFile file, byte[] data)
   {
      if (data != null)
      {
         int offset = 0;
         String alias;
         ArrayList aliases = new ArrayList();

         while (offset < data.length)
         {
            alias = MPPUtility.getUnicodeString(data, offset);
            aliases.add(alias);
            offset += (alias.length()+1)*2;
         }

         file.setResourceFieldAlias(Resource.TEXT1, (String)aliases.get(52));
         file.setResourceFieldAlias(Resource.TEXT2, (String)aliases.get(53));
         file.setResourceFieldAlias(Resource.TEXT3, (String)aliases.get(54));
         file.setResourceFieldAlias(Resource.TEXT4, (String)aliases.get(55));
         file.setResourceFieldAlias(Resource.TEXT5, (String)aliases.get(56));
         file.setResourceFieldAlias(Resource.TEXT6, (String)aliases.get(57));
         file.setResourceFieldAlias(Resource.TEXT7, (String)aliases.get(58));
         file.setResourceFieldAlias(Resource.TEXT8, (String)aliases.get(59));
         file.setResourceFieldAlias(Resource.TEXT9, (String)aliases.get(60));
         file.setResourceFieldAlias(Resource.TEXT10, (String)aliases.get(61));
         file.setResourceFieldAlias(Resource.TEXT11, (String)aliases.get(62));
         file.setResourceFieldAlias(Resource.TEXT12, (String)aliases.get(63));
         file.setResourceFieldAlias(Resource.TEXT13, (String)aliases.get(64));
         file.setResourceFieldAlias(Resource.TEXT14, (String)aliases.get(65));
         file.setResourceFieldAlias(Resource.TEXT15, (String)aliases.get(66));
         file.setResourceFieldAlias(Resource.TEXT16, (String)aliases.get(67));
         file.setResourceFieldAlias(Resource.TEXT17, (String)aliases.get(68));
         file.setResourceFieldAlias(Resource.TEXT18, (String)aliases.get(69));
         file.setResourceFieldAlias(Resource.TEXT19, (String)aliases.get(70));
         file.setResourceFieldAlias(Resource.TEXT20, (String)aliases.get(71));
         file.setResourceFieldAlias(Resource.TEXT21, (String)aliases.get(72));
         file.setResourceFieldAlias(Resource.TEXT22, (String)aliases.get(73));
         file.setResourceFieldAlias(Resource.TEXT23, (String)aliases.get(74));
         file.setResourceFieldAlias(Resource.TEXT24, (String)aliases.get(75));
         file.setResourceFieldAlias(Resource.TEXT25, (String)aliases.get(76));
         file.setResourceFieldAlias(Resource.TEXT26, (String)aliases.get(77));
         file.setResourceFieldAlias(Resource.TEXT27, (String)aliases.get(78));
         file.setResourceFieldAlias(Resource.TEXT28, (String)aliases.get(79));
         file.setResourceFieldAlias(Resource.TEXT29, (String)aliases.get(80));
         file.setResourceFieldAlias(Resource.TEXT30, (String)aliases.get(81));
         file.setResourceFieldAlias(Resource.START1, (String)aliases.get(82));
         file.setResourceFieldAlias(Resource.START2, (String)aliases.get(83));
         file.setResourceFieldAlias(Resource.START3, (String)aliases.get(84));
         file.setResourceFieldAlias(Resource.START4, (String)aliases.get(85));
         file.setResourceFieldAlias(Resource.START5, (String)aliases.get(86));
         file.setResourceFieldAlias(Resource.START6, (String)aliases.get(87));
         file.setResourceFieldAlias(Resource.START7, (String)aliases.get(88));
         file.setResourceFieldAlias(Resource.START8, (String)aliases.get(89));
         file.setResourceFieldAlias(Resource.START9, (String)aliases.get(90));
         file.setResourceFieldAlias(Resource.START10, (String)aliases.get(91));
         file.setResourceFieldAlias(Resource.FINISH1, (String)aliases.get(92));
         file.setResourceFieldAlias(Resource.FINISH2, (String)aliases.get(93));
         file.setResourceFieldAlias(Resource.FINISH3, (String)aliases.get(94));
         file.setResourceFieldAlias(Resource.FINISH4, (String)aliases.get(95));
         file.setResourceFieldAlias(Resource.FINISH5, (String)aliases.get(96));
         file.setResourceFieldAlias(Resource.FINISH6, (String)aliases.get(97));
         file.setResourceFieldAlias(Resource.FINISH7, (String)aliases.get(98));
         file.setResourceFieldAlias(Resource.FINISH8, (String)aliases.get(99));
         file.setResourceFieldAlias(Resource.FINISH9, (String)aliases.get(100));
         file.setResourceFieldAlias(Resource.FINISH10, (String)aliases.get(101));
         file.setResourceFieldAlias(Resource.NUMBER1, (String)aliases.get(102));
         file.setResourceFieldAlias(Resource.NUMBER2, (String)aliases.get(103));
         file.setResourceFieldAlias(Resource.NUMBER3, (String)aliases.get(104));
         file.setResourceFieldAlias(Resource.NUMBER4, (String)aliases.get(105));
         file.setResourceFieldAlias(Resource.NUMBER5, (String)aliases.get(106));
         file.setResourceFieldAlias(Resource.NUMBER6, (String)aliases.get(107));
         file.setResourceFieldAlias(Resource.NUMBER7, (String)aliases.get(108));
         file.setResourceFieldAlias(Resource.NUMBER8, (String)aliases.get(109));
         file.setResourceFieldAlias(Resource.NUMBER9, (String)aliases.get(110));
         file.setResourceFieldAlias(Resource.NUMBER10, (String)aliases.get(111));
         file.setResourceFieldAlias(Resource.NUMBER11, (String)aliases.get(112));
         file.setResourceFieldAlias(Resource.NUMBER12, (String)aliases.get(113));
         file.setResourceFieldAlias(Resource.NUMBER13, (String)aliases.get(114));
         file.setResourceFieldAlias(Resource.NUMBER14, (String)aliases.get(115));
         file.setResourceFieldAlias(Resource.NUMBER15, (String)aliases.get(116));
         file.setResourceFieldAlias(Resource.NUMBER16, (String)aliases.get(117));
         file.setResourceFieldAlias(Resource.NUMBER17, (String)aliases.get(118));
         file.setResourceFieldAlias(Resource.NUMBER18, (String)aliases.get(119));
         file.setResourceFieldAlias(Resource.NUMBER19, (String)aliases.get(120));
         file.setResourceFieldAlias(Resource.NUMBER20, (String)aliases.get(121));
         file.setResourceFieldAlias(Resource.DURATION1, (String)aliases.get(122));
         file.setResourceFieldAlias(Resource.DURATION2, (String)aliases.get(123));
         file.setResourceFieldAlias(Resource.DURATION3, (String)aliases.get(124));
         file.setResourceFieldAlias(Resource.DURATION4, (String)aliases.get(125));
         file.setResourceFieldAlias(Resource.DURATION5, (String)aliases.get(126));
         file.setResourceFieldAlias(Resource.DURATION6, (String)aliases.get(127));
         file.setResourceFieldAlias(Resource.DURATION7, (String)aliases.get(128));
         file.setResourceFieldAlias(Resource.DURATION8, (String)aliases.get(129));
         file.setResourceFieldAlias(Resource.DURATION9, (String)aliases.get(130));
         file.setResourceFieldAlias(Resource.DURATION10, (String)aliases.get(131));
         file.setResourceFieldAlias(Resource.DATE1, (String)aliases.get(145));
         file.setResourceFieldAlias(Resource.DATE2, (String)aliases.get(146));
         file.setResourceFieldAlias(Resource.DATE3, (String)aliases.get(147));
         file.setResourceFieldAlias(Resource.DATE4, (String)aliases.get(148));
         file.setResourceFieldAlias(Resource.DATE5, (String)aliases.get(149));
         file.setResourceFieldAlias(Resource.DATE6, (String)aliases.get(150));
         file.setResourceFieldAlias(Resource.DATE7, (String)aliases.get(151));
         file.setResourceFieldAlias(Resource.DATE8, (String)aliases.get(152));
         file.setResourceFieldAlias(Resource.DATE9, (String)aliases.get(153));
         file.setResourceFieldAlias(Resource.DATE10, (String)aliases.get(154));
         file.setResourceFieldAlias(Resource.OUTLINECODE1, (String)aliases.get(155));
         file.setResourceFieldAlias(Resource.OUTLINECODE2, (String)aliases.get(156));
         file.setResourceFieldAlias(Resource.OUTLINECODE3, (String)aliases.get(157));
         file.setResourceFieldAlias(Resource.OUTLINECODE4, (String)aliases.get(158));
         file.setResourceFieldAlias(Resource.OUTLINECODE5, (String)aliases.get(159));
         file.setResourceFieldAlias(Resource.OUTLINECODE6, (String)aliases.get(160));
         file.setResourceFieldAlias(Resource.OUTLINECODE7, (String)aliases.get(161));
         file.setResourceFieldAlias(Resource.OUTLINECODE8, (String)aliases.get(162));
         file.setResourceFieldAlias(Resource.OUTLINECODE9, (String)aliases.get(163));
         file.setResourceFieldAlias(Resource.OUTLINECODE10, (String)aliases.get(164));
         file.setResourceFieldAlias(Resource.FLAG10, (String)aliases.get(165));
         file.setResourceFieldAlias(Resource.FLAG1, (String)aliases.get(166));
         file.setResourceFieldAlias(Resource.FLAG2, (String)aliases.get(167));
         file.setResourceFieldAlias(Resource.FLAG3, (String)aliases.get(168));
         file.setResourceFieldAlias(Resource.FLAG4, (String)aliases.get(169));
         file.setResourceFieldAlias(Resource.FLAG5, (String)aliases.get(170));
         file.setResourceFieldAlias(Resource.FLAG6, (String)aliases.get(171));
         file.setResourceFieldAlias(Resource.FLAG7, (String)aliases.get(172));
         file.setResourceFieldAlias(Resource.FLAG8, (String)aliases.get(173));
         file.setResourceFieldAlias(Resource.FLAG9, (String)aliases.get(174));
         file.setResourceFieldAlias(Resource.FLAG11, (String)aliases.get(175));
         file.setResourceFieldAlias(Resource.FLAG12, (String)aliases.get(176));
         file.setResourceFieldAlias(Resource.FLAG13, (String)aliases.get(177));
         file.setResourceFieldAlias(Resource.FLAG14, (String)aliases.get(178));
         file.setResourceFieldAlias(Resource.FLAG15, (String)aliases.get(179));
         file.setResourceFieldAlias(Resource.FLAG16, (String)aliases.get(180));
         file.setResourceFieldAlias(Resource.FLAG17, (String)aliases.get(181));
         file.setResourceFieldAlias(Resource.FLAG18, (String)aliases.get(182));
         file.setResourceFieldAlias(Resource.FLAG19, (String)aliases.get(183));
         file.setResourceFieldAlias(Resource.FLAG20, (String)aliases.get(184));
         file.setResourceFieldAlias(Resource.COST1, (String)aliases.get(207));
         file.setResourceFieldAlias(Resource.COST2, (String)aliases.get(208));
         file.setResourceFieldAlias(Resource.COST3, (String)aliases.get(209));
         file.setResourceFieldAlias(Resource.COST4, (String)aliases.get(210));
         file.setResourceFieldAlias(Resource.COST5, (String)aliases.get(211));
         file.setResourceFieldAlias(Resource.COST6, (String)aliases.get(212));
         file.setResourceFieldAlias(Resource.COST7, (String)aliases.get(213));
         file.setResourceFieldAlias(Resource.COST8, (String)aliases.get(214));
         file.setResourceFieldAlias(Resource.COST9, (String)aliases.get(215));
         file.setResourceFieldAlias(Resource.COST10, (String)aliases.get(216));
      }
   }

   /**
    * This method maps the task unique identifiers to their index number
    * within the FixedData block. Note that although we have previously been
    * treating the task ID as a 4 byte integer, here it appears to need
    * to be treated as a 2 byte integer in order for us to match it with
    * the 2 byte unqiue ID values held in the task VarMeta data.
    *
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @return Mapping between task identifiers and block position
    */
   private static TreeMap createTaskMap (FixedMeta taskFixedMeta, FixedData taskFixedData)
   {
      TreeMap taskMap = new TreeMap ();
      int itemCount = taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);
         if (data != null && data.length > 4)
         {
            uniqueID = MPPUtility.getShort(data, 0);
            taskMap.put(new Integer (uniqueID), new Integer (loop));
         }
      }

      return (taskMap);
   }


   /**
    * This method maps the resource unique identifiers to their index number
    * within the FixedData block.
    *
    * @param rscFixedMeta resource fixed meta data
    * @param rscFixedData resource fixed data
    * @return map of resource IDs to resource data
    */
   private static TreeMap createResourceMap (FixedMeta rscFixedMeta, FixedData rscFixedData)
   {
      TreeMap resourceMap = new TreeMap ();
      int itemCount = rscFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = rscFixedData.getByteArrayValue(loop);
         if (data != null && data.length > 4)
         {
            uniqueID = MPPUtility.getShort (data, 0);
            resourceMap.put(new Integer (uniqueID), new Integer (loop));
         }
      }

      return (resourceMap);
   }
   
   /**
    * The format of the calandar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    *
    * @param file parent MPP file
    * @param projectDir root project directory
    * @param resourceMap map of resource IDs to resource data
    * @throws IOException
    */
   private static void processCalendarData (MPPFile file,  DirectoryEntry projectDir, HashMap resourceMap)
      throws MPXException, IOException
   {
      DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");
      VarMeta calVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));
      Var2Data calVarData = new Var2Data (calVarMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("Var2Data"))));
      FixedMeta calFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixedMeta"))), 10);
      FixedData calFixedData = new FixedData (calFixedMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixedData"))));

      HashMap calendarMap = new HashMap ();
      int items = calFixedData.getItemCount();
      byte[] fixedData;
      byte[] varData;
      Integer calendarID;
      int baseCalendarID;
      Integer resourceID;
      int offset;
      MPXCalendar cal;

      for (int loop=0; loop < items; loop++)
      {
         fixedData = calFixedData.getByteArrayValue(loop);
         if (fixedData.length >= 8)
         {
            offset = 0;

            //
            // Bug 890909, here we ensure that we have a complete 12 byte
            // block before attempting to process the data.
            //
            while (offset+12 <= fixedData.length)
            {
               calendarID = new Integer (MPPUtility.getInt (fixedData, offset+0));

               if (calendarMap.containsKey(calendarID) == false)
               {
                  baseCalendarID = MPPUtility.getInt(fixedData, offset+4);
                  varData = calVarData.getByteArray (calendarID, CALENDAR_DATA);

                  if (baseCalendarID == -1)
                  {
                     if (varData != null)
                     {
                        cal = file.addBaseCalendar();
                     }
                     else
                     {
                        cal = file.addDefaultBaseCalendar();
                     }

                     cal.setName(calVarData.getUnicodeString (calendarID, CALENDAR_NAME));
                  }
                  else
                  {
                     if (varData != null)
                     {
                        cal = file.mppAddResourceCalendar();
                     }
                     else
                     {
                        cal = file.mppAddDefaultResourceCalendar();
                     }

                     cal.setBaseCalendarName(Integer.toString(baseCalendarID));
                     resourceID = new Integer (MPPUtility.getInt(fixedData, offset+8));
                     resourceMap.put (resourceID, cal);
                  }

                  cal.setUniqueID(calendarID.intValue());

                  if (varData != null)
                  {
                     processCalendarHours (varData, cal);
                     processCalendarExceptions (varData, cal);
                  }

                  calendarMap.put (calendarID, cal);
               }

               offset += 12;
            }
         }
      }

      updateBaseCalendarNames (calendarMap);
   }

   /**
    * For a given set of calendar data, this method sets the working
    * day status for each day, and if present, sets the hours for that
    * day.
    *
    * @param data calendar data block
    * @param cal calendar instance
    * @throws MPXException
    */
   private static void processCalendarHours (byte[] data, MPXCalendar cal)
      throws MPXException
   {
      int offset;
      MPXCalendarHours hours;
      MPXCalendarException exception;
      String name;

      int periodCount;
      int index;
      int defaultFlag;
      Date start;
      long duration;
      int exceptionCount;

      //
      // Configure default time ranges
      //
      SimpleDateFormat df = new SimpleDateFormat ("HH:mm");
      Date defaultStart1;
      Date defaultEnd1;
      Date defaultStart2;
      Date defaultEnd2;

      try
      {
         defaultStart1 = df.parse ("08:00");
         defaultEnd1 = df.parse ("12:00");
         defaultStart2 = df.parse ("13:00");
         defaultEnd2 = df.parse ("17:00");
      }

      catch (ParseException ex)
      {
         throw new MPXException (MPXException.INVALID_FORMAT, ex);
      }

      for (index=0; index < 7; index++)
      {
         offset = 4 + (60 * index);
         defaultFlag = MPPUtility.getShort (data, offset);

         if (defaultFlag == 1)
         {
            if (cal.isBaseCalendar() == true)
            {
               cal.setWorkingDay(index+1, DEFAULT_WORKING_WEEK[index]);
               if (cal.isWorkingDay(index+1) == true)
               {
                  hours = cal.addCalendarHours(index+1);
                  hours.setFromTime1(defaultStart1);
                  hours.setToTime1(defaultEnd1);
                  hours.setFromTime2(defaultStart2);
                  hours.setToTime2(defaultEnd2);
               }
            }
            else
            {
               cal.setWorkingDay(index+1, MPXCalendar.DEFAULT);
            }
         }
         else
         {
            periodCount = MPPUtility.getShort (data, offset+2);
            if (periodCount == 0)
            {
               cal.setWorkingDay(index+1, false);
            }
            else
            {
               cal.setWorkingDay(index+1, true);
               hours = cal.addCalendarHours(index+1);

               start = MPPUtility.getTime (data, offset + 8);
               duration = MPPUtility.getDuration (data, offset + 20);
               hours.setFromTime1(start);
               hours.setToTime1(new Date (start.getTime()+duration));

               if (periodCount > 1)
               {
                  start = MPPUtility.getTime (data, offset + 10);
                  duration = MPPUtility.getDuration (data, offset + 24);
                  hours.setFromTime2(start);
                  hours.setToTime2(new Date (start.getTime()+duration));

                  if (periodCount > 2)
                  {
                     start = MPPUtility.getTime (data, offset + 12);
                     duration = MPPUtility.getDuration (data, offset + 28);
                     hours.setFromTime3(start);
                     hours.setToTime3(new Date (start.getTime()+duration));
                  }
               }

               // Note that MPP defines 5 time ranges, the additional
               // start times are at offsets 14, 16 and the additional
               // durations are at offsets 32 and 36.
            }
         }
      }
   }


   /**
    * This method extracts any exceptions associated with a calendar.
    *
    * @param data calendar data block
    * @param cal calendar instance
    * @throws MPXException
    */
   private static void processCalendarExceptions (byte[] data, MPXCalendar cal)
      throws MPXException
   {
      //
      // Handle any exceptions
      //
      int exceptionCount = MPPUtility.getShort (data, 0);

      if (exceptionCount != 0)
      {
         int index;
         int offset;
         MPXCalendarException exception;
         long duration;
         int periodCount;
         Date start;

         for (index=0; index < exceptionCount; index++)
         {
            offset = 4 + (60 * 7) + (index * 64);
            exception = cal.addCalendarException();
            exception.setFromDate(MPPUtility.getDate (data, offset));
            exception.setToDate(MPPUtility.getDate (data, offset+2));

            periodCount = MPPUtility.getShort (data, offset+6);
            if (periodCount == 0)
            {
               exception.setWorking (false);
            }
            else
            {
               exception.setWorking (true);

               start = MPPUtility.getTime (data, offset+12);
               duration = MPPUtility.getDuration (data, offset+24);
               exception.setFromTime1(start);
               exception.setToTime1(new Date (start.getTime() + duration));

               if (periodCount > 1)
               {
                  start = MPPUtility.getTime (data, offset+14);
                  duration = MPPUtility.getDuration (data, offset+28);
                  exception.setFromTime2(start);
                  exception.setToTime2(new Date (start.getTime() + duration));

                  if (periodCount > 2)
                  {
                     start = MPPUtility.getTime (data, offset+16);
                     duration = MPPUtility.getDuration (data, offset+32);
                     exception.setFromTime3(start);
                     exception.setToTime3(new Date (start.getTime() + duration));
                  }
               }
               //
               // Note that MPP defines 5 time ranges rather than 3
               //
            }
         }
      }
   }


   /**
    * The way calendars are stored in an MPP8 file means that there
    * can be forward references between the base calendar unique ID for a
    * derived calendar, and the base calendar itself. To get around this,
    * we initially populatethe base calendar name attribute with the
    * base calendar unique ID, and now in this method we can convert those
    * ID values into the correct names.
    *
    * @param map map of calendar ID values and calendar objects
    */
   private static void updateBaseCalendarNames (HashMap map)
   {
      Iterator iter = map.keySet().iterator();
      MPXCalendar cal;
      MPXCalendar baseCal;
      String baseCalendarName;

      while (iter.hasNext() == true)
      {
         cal = (MPXCalendar)map.get(iter.next());
         baseCalendarName = cal.getBaseCalendarName();
         if (baseCalendarName != null)
         {
            baseCal = (MPXCalendar)map.get(new Integer (baseCalendarName));
            if (baseCal != null)
            {
               cal.setBaseCalendarName(baseCal.getName());
            }
         }
      }
   }

   /**
    * This method extracts and collates task data. The code below
    * goes through the modifier methods of the Task class in alphabetical
    * order extracting the data from the MPP file. Where there is no
    * mapping (e.g. the field is calculated on the fly, or we can't
    * find it in the data) the line is commented out.
    *
    * The missing boolean attributes are probably represented in the Props
    * section of the task data, which we have yet to decode.
    *
    * @param file parent MPP file
    * @param projectDir root project directory
    * @param outlineCodeVarData outline code data
    * @throws MPXException
    * @throws IOException
    */
   private static void processTaskData (MPPFile file,  DirectoryEntry projectDir, Var2Data outlineCodeVarData)
      throws MPXException, IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
      VarMeta taskVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data (taskVarMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData (taskFixedMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedData"))));

      TreeMap taskMap = createTaskMap (taskFixedMeta, taskFixedData);
      Integer[] uniqueid = taskVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Task task;

      RTFUtility rtf = new RTFUtility ();
      String notes;

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];

         offset = (Integer)taskMap.get(id);
         if (taskFixedData.isValidOffset(offset) == false)
         {
            continue;
         }

         data = taskFixedData.getByteArrayValue(offset.intValue());
         if (data.length < MINIMUM_EXPECTED_TASK_SIZE)
         {
            continue;
         }

         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());

         task = file.addTask();
         task.setActualCost(new Double (MPPUtility.getDouble (data, 216) / 100));
         task.setActualDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 66), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 100));
         task.setActualOvertimeCost (new Double(taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_COST)));
         task.setActualOvertimeWork(new MPXDuration (taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_WORK)/60000, TimeUnit.HOURS));
         task.setActualStart(MPPUtility.getTimestamp (data, 96));
         task.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 184)/60000, TimeUnit.HOURS));
         //task.setACWP(); // Calculated value
         //task.setAssignment(); // Calculated value
         //task.setAssignmentDelay(); // Calculated value
         //task.setAssignmentUnits(); // Calculated value
         task.setBaselineCost(new Double (MPPUtility.getDouble (data, 232) / 100));
         task.setBaselineDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationUnits (MPPUtility.getShort (data, 78))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 108));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 104));
         task.setBaselineWork(new MPXDuration (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));

// From MS Project 2003
//         task.setBaseline1Cost(new Double (MPPUtility.getDouble (data, 232) / 100));
//         task.setBaseline1Duration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationUnits (MPPUtility.getShort (data, 78))));
//         task.setBaseline1Finish(MPPUtility.getTimestamp (data, 108));
//         task.setBaseline1Start(MPPUtility.getTimestamp (data, 104));
//         task.setBaseline1Work(new MPXDuration (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));
// to...
//         task.setBaseline10Cost(new Double (MPPUtility.getDouble (data, 232) / 100));
//         task.setBaseline10Duration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationUnits (MPPUtility.getShort (data, 78))));
//         task.setBaseline10Finish(MPPUtility.getTimestamp (data, 108));
//         task.setBaseline10Start(MPPUtility.getTimestamp (data, 104));
//         task.setBaseline10Work(new MPXDuration (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));


         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate (MPPUtility.getTimestamp (data, 112));
         task.setConstraintType (ConstraintType.getInstance (MPPUtility.getShort (data, 80)));
         task.setContact(taskVarData.getUnicodeString (id, TASK_CONTACT));
         task.setCost(new Double (MPPUtility.getDouble(data, 200) / 100));
         //task.setCostRateTable(); // Calculated value
         //task.setCostVariance(); // Populated below
         task.setCost1(new Double (taskVarData.getDouble (id, TASK_COST1) / 100));
         task.setCost2(new Double (taskVarData.getDouble (id, TASK_COST2) / 100));
         task.setCost3(new Double (taskVarData.getDouble (id, TASK_COST3) / 100));
         task.setCost4(new Double (taskVarData.getDouble (id, TASK_COST4) / 100));
         task.setCost5(new Double (taskVarData.getDouble (id, TASK_COST5) / 100));
         task.setCost6(new Double (taskVarData.getDouble (id, TASK_COST6) / 100));
         task.setCost7(new Double (taskVarData.getDouble (id, TASK_COST7) / 100));
         task.setCost8(new Double (taskVarData.getDouble (id, TASK_COST8) / 100));
         task.setCost9(new Double (taskVarData.getDouble (id, TASK_COST9) / 100));
         task.setCost10(new Double (taskVarData.getDouble (id, TASK_COST10) / 100));

// From MS Project 2003
//         task.setCPI();

         task.setCreated(MPPUtility.getTimestamp (data, 130));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         //task.setCVPercent(); // Calculate value
         task.setDate1(taskVarData.getTimestamp (id, TASK_DATE1));
         task.setDate2(taskVarData.getTimestamp (id, TASK_DATE2));
         task.setDate3(taskVarData.getTimestamp (id, TASK_DATE3));
         task.setDate4(taskVarData.getTimestamp (id, TASK_DATE4));
         task.setDate5(taskVarData.getTimestamp (id, TASK_DATE5));
         task.setDate6(taskVarData.getTimestamp (id, TASK_DATE6));
         task.setDate7(taskVarData.getTimestamp (id, TASK_DATE7));
         task.setDate8(taskVarData.getTimestamp (id, TASK_DATE8));
         task.setDate9(taskVarData.getTimestamp (id, TASK_DATE9));
         task.setDate10(taskVarData.getTimestamp (id, TASK_DATE10));
         task.setDeadline (MPPUtility.getTimestamp (data, 164));
         //task.setDelay(); // No longer supported by MS Project?
         task.setDuration (MPPUtility.getDuration (MPPUtility.getInt (data, 60), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 64))));
         //task.setDurationVariance(); // Calculated value
         task.setDuration1(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION1), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION1_UNITS))));
         task.setDuration2(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION2), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION2_UNITS))));
         task.setDuration3(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION3), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION3_UNITS))));
         task.setDuration4(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION4), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION4_UNITS))));
         task.setDuration5(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION5), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION5_UNITS))));
         task.setDuration6(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION6), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION6_UNITS))));
         task.setDuration7(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION7), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION7_UNITS))));
         task.setDuration8(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION8), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION8_UNITS))));
         task.setDuration9(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION9), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION9_UNITS))));
         task.setDuration10(MPPUtility.getDuration (taskVarData.getInt(id, TASK_DURATION10), MPPUtility.getDurationUnits(taskVarData.getShort(id, TASK_DURATION10_UNITS))));
//       From MS Project 2003
//         task.setEAC();
         //task.setEarlyFinish(); // Calculated value
         //task.setEarlyStart(); // Calculated value
//       From MS Project 2003
//         task.setEarnedValueMethod();
         task.setEffortDriven((metaData[11] & 0x10) != 0);
         task.setEstimated(getDurationEstimated(MPPUtility.getShort (data, 64)));
         //task.setExternalTask(); // Calculated value
         task.setFinish (MPPUtility.getTimestamp (data, 8));
//       From MS Project 2003
//         task.setFinishSlack();
         //task.setFinishVariance(); // Calculated value
         task.setFinish1(taskVarData.getTimestamp (id, TASK_FINISH1));
         task.setFinish2(taskVarData.getTimestamp (id, TASK_FINISH2));
         task.setFinish3(taskVarData.getTimestamp (id, TASK_FINISH3));
         task.setFinish4(taskVarData.getTimestamp (id, TASK_FINISH4));
         task.setFinish5(taskVarData.getTimestamp (id, TASK_FINISH5));
         task.setFinish6(taskVarData.getTimestamp (id, TASK_FINISH6));
         task.setFinish7(taskVarData.getTimestamp (id, TASK_FINISH7));
         task.setFinish8(taskVarData.getTimestamp (id, TASK_FINISH8));
         task.setFinish9(taskVarData.getTimestamp (id, TASK_FINISH9));
         task.setFinish10(taskVarData.getTimestamp (id, TASK_FINISH10));
         //task.setFixed(); // Unsure of mapping from MPX->MSP2K
         task.setFixedCost(new Double (MPPUtility.getDouble (data, 208) / 100));
         task.setFixedCostAccrual(AccrueType.getInstance(MPPUtility.getShort(data, 128)));
         task.setFlag1((metaData[37] & 0x20) != 0);
         task.setFlag2((metaData[37] & 0x40) != 0);
         task.setFlag3((metaData[37] & 0x80) != 0);
         task.setFlag4((metaData[38] & 0x01) != 0);
         task.setFlag5((metaData[38] & 0x02) != 0);
         task.setFlag6((metaData[38] & 0x04) != 0);
         task.setFlag7((metaData[38] & 0x08) != 0);
         task.setFlag8((metaData[38] & 0x10) != 0);
         task.setFlag9((metaData[38] & 0x20) != 0);
         task.setFlag10((metaData[38] & 0x40) != 0);
         task.setFlag11((metaData[38] & 0x80) != 0);
         task.setFlag12((metaData[39] & 0x01) != 0);
         task.setFlag13((metaData[39] & 0x02) != 0);
         task.setFlag14((metaData[39] & 0x04) != 0);
         task.setFlag15((metaData[39] & 0x08) != 0);
         task.setFlag16((metaData[39] & 0x10) != 0);
         task.setFlag17((metaData[39] & 0x20) != 0);
         task.setFlag18((metaData[39] & 0x40) != 0);
         task.setFlag19((metaData[39] & 0x80) != 0);
         task.setFlag20((metaData[40] & 0x01) != 0);
         //task.setFreeSlack();  // Calculated value
//       From MS Project 2003
//         task.setGroupBySummary();
         task.setHideBar((metaData[10] & 0x80) != 0);
         processHyperlinkData (task, taskVarData.getByteArray(id, TASK_HYPERLINK));
         task.setID (MPPUtility.getInt (data, 4));
//       From MS Project 2003
//         task.setIgnoreResourceCalendar();
         //task.setIndicators(); // Calculated value
         //task.setLateFinish();  // Calculated value
         //task.setLateStart();  // Calculated value
         task.setLevelAssignments((metaData[13] & 0x04) != 0);
         task.setLevelingCanSplit((metaData[13] & 0x02) != 0);
         task.setLevelingDelay (MPPUtility.getDuration (((double)MPPUtility.getInt (data, 82))/3, MPPUtility.getDurationUnits(MPPUtility.getShort (data, 86))));
         //task.setLinkedFields();  // Calculated value
         //task.setMarked();
         task.setMilestone((metaData[8] & 0x20) != 0);
         task.setName(taskVarData.getUnicodeString (id, TASK_NAME));
         task.setNumber1(new Double (taskVarData.getDouble(id, TASK_NUMBER1)));
         task.setNumber2(new Double (taskVarData.getDouble(id, TASK_NUMBER2)));
         task.setNumber3(new Double (taskVarData.getDouble(id, TASK_NUMBER3)));
         task.setNumber4(new Double (taskVarData.getDouble(id, TASK_NUMBER4)));
         task.setNumber5(new Double (taskVarData.getDouble(id, TASK_NUMBER5)));
         task.setNumber6(new Double (taskVarData.getDouble(id, TASK_NUMBER6)));
         task.setNumber7(new Double (taskVarData.getDouble(id, TASK_NUMBER7)));
         task.setNumber8(new Double (taskVarData.getDouble(id, TASK_NUMBER8)));
         task.setNumber9(new Double (taskVarData.getDouble(id, TASK_NUMBER9)));
         task.setNumber10(new Double (taskVarData.getDouble(id, TASK_NUMBER10)));
         task.setNumber11(new Double (taskVarData.getDouble(id, TASK_NUMBER11)));
         task.setNumber12(new Double (taskVarData.getDouble(id, TASK_NUMBER12)));
         task.setNumber13(new Double (taskVarData.getDouble(id, TASK_NUMBER13)));
         task.setNumber14(new Double (taskVarData.getDouble(id, TASK_NUMBER14)));
         task.setNumber15(new Double (taskVarData.getDouble(id, TASK_NUMBER15)));
         task.setNumber16(new Double (taskVarData.getDouble(id, TASK_NUMBER16)));
         task.setNumber17(new Double (taskVarData.getDouble(id, TASK_NUMBER17)));
         task.setNumber18(new Double (taskVarData.getDouble(id, TASK_NUMBER18)));
         task.setNumber19(new Double (taskVarData.getDouble(id, TASK_NUMBER19)));
         task.setNumber20(new Double (taskVarData.getDouble(id, TASK_NUMBER20)));
         //task.setObjects(); // Calculated value
         task.setOutlineCode1(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE1)), OUTLINECODE_DATA));
         task.setOutlineCode2(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE2)), OUTLINECODE_DATA));
         task.setOutlineCode3(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE3)), OUTLINECODE_DATA));
         task.setOutlineCode4(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE4)), OUTLINECODE_DATA));
         task.setOutlineCode5(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE5)), OUTLINECODE_DATA));
         task.setOutlineCode6(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE6)), OUTLINECODE_DATA));
         task.setOutlineCode7(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE7)), OUTLINECODE_DATA));
         task.setOutlineCode8(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE8)), OUTLINECODE_DATA));
         task.setOutlineCode9(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE9)), OUTLINECODE_DATA));
         task.setOutlineCode10(outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE10)), OUTLINECODE_DATA));
         task.setOutlineLevel (MPPUtility.getShort (data, 40));
         //task.setOutlineNumber(); // Calculated value
         //task.setOverallocated(); // Calculated value
         task.setOvertimeCost(new Double(taskVarData.getDouble(id, TASK_OVERTIME_COST)));
         //task.setOvertimeWork(); // Calculated value?
         //task.getPredecessors(); // Calculated value
         task.setPercentageComplete(MPPUtility.getShort(data, 122));
         task.setPercentageWorkComplete(MPPUtility.getShort(data, 124));
//       From MS Project 2003
//         task.setPhysicalPercentComplete();
         task.setPreleveledFinish(MPPUtility.getTimestamp(data, 140));
         task.setPreleveledStart(MPPUtility.getTimestamp(data, 136));
         task.setPriority(getPriority (MPPUtility.getShort (data, 120)));
         //task.setProject(); // Calculated value
         //task.setRecurring(); // Calculated value
         //task.setRegularWork(); // Calculated value
         task.setRemainingCost(new Double (MPPUtility.getDouble (data, 224)/100));
         task.setRemainingDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 70), MPPUtility.getDurationUnits(MPPUtility.getShort (data, 64))));
         task.setRemainingOvertimeCost(new Double(taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_COST)));
         task.setRemainingOvertimeWork(new MPXDuration (taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_WORK)/60000, TimeUnit.HOURS));
         task.setRemainingWork(new MPXDuration (MPPUtility.getDouble (data, 192)/60000, TimeUnit.HOURS));
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         //task.setResourcePhonetics(); // Calculated value from resource
//       From MS Project 2003
//         task.setResourceType();
         //task.setResponsePending(); // Calculated value
         task.setResume(MPPUtility.getTimestamp(data, 20));
         //task.setResumeNoEarlierThan(); // No mapping in MSP2K?
         task.setRollup((metaData[10] & 0x08) != 0);
//       From MS Project 2003
//         task.setSPI();
         task.setStart (MPPUtility.getTimestamp (data, 88));
//       From MS Project 2003
//         task.setStartSlack();
         //task.setStartVariance(); // Calculated value
         task.setStart1(taskVarData.getTimestamp (id, TASK_START1));
         task.setStart2(taskVarData.getTimestamp (id, TASK_START2));
         task.setStart3(taskVarData.getTimestamp (id, TASK_START3));
         task.setStart4(taskVarData.getTimestamp (id, TASK_START4));
         task.setStart5(taskVarData.getTimestamp (id, TASK_START5));
         task.setStart6(taskVarData.getTimestamp (id, TASK_START6));
         task.setStart7(taskVarData.getTimestamp (id, TASK_START7));
         task.setStart8(taskVarData.getTimestamp (id, TASK_START8));
         task.setStart9(taskVarData.getTimestamp (id, TASK_START9));
         task.setStart10(taskVarData.getTimestamp (id, TASK_START10));
//       From MS Project 2003
//         task.setStatus();
//         task.setStatusIndicator();
         task.setStop(MPPUtility.getTimestamp (data, 16));
         //task.setSubprojectFile();
         //task.setSubprojectReadOnly();
         //task.setSuccessors(); // Calculated value
         //task.setSummary(); // Automatically generated by MPXJ
         //task.setSV(); // Calculated value
//       From MS Project 2003
//         task.setSVPercent();
//         task.setTCPI();
         //task.setTeamStatusPending(); // Calculated value
         task.setText1(taskVarData.getUnicodeString (id, TASK_TEXT1));
         task.setText2(taskVarData.getUnicodeString (id, TASK_TEXT2));
         task.setText3(taskVarData.getUnicodeString (id, TASK_TEXT3));
         task.setText4(taskVarData.getUnicodeString (id, TASK_TEXT4));
         task.setText5(taskVarData.getUnicodeString (id, TASK_TEXT5));
         task.setText6(taskVarData.getUnicodeString (id, TASK_TEXT6));
         task.setText7(taskVarData.getUnicodeString (id, TASK_TEXT7));
         task.setText8(taskVarData.getUnicodeString (id, TASK_TEXT8));
         task.setText9(taskVarData.getUnicodeString (id, TASK_TEXT9));
         task.setText10(taskVarData.getUnicodeString (id, TASK_TEXT10));
         task.setText11(taskVarData.getUnicodeString (id, TASK_TEXT11));
         task.setText12(taskVarData.getUnicodeString (id, TASK_TEXT12));
         task.setText13(taskVarData.getUnicodeString (id, TASK_TEXT13));
         task.setText14(taskVarData.getUnicodeString (id, TASK_TEXT14));
         task.setText15(taskVarData.getUnicodeString (id, TASK_TEXT15));
         task.setText16(taskVarData.getUnicodeString (id, TASK_TEXT16));
         task.setText17(taskVarData.getUnicodeString (id, TASK_TEXT17));
         task.setText18(taskVarData.getUnicodeString (id, TASK_TEXT18));
         task.setText19(taskVarData.getUnicodeString (id, TASK_TEXT19));
         task.setText20(taskVarData.getUnicodeString (id, TASK_TEXT20));
         task.setText21(taskVarData.getUnicodeString (id, TASK_TEXT21));
         task.setText22(taskVarData.getUnicodeString (id, TASK_TEXT22));
         task.setText23(taskVarData.getUnicodeString (id, TASK_TEXT23));
         task.setText24(taskVarData.getUnicodeString (id, TASK_TEXT24));
         task.setText25(taskVarData.getUnicodeString (id, TASK_TEXT25));
         task.setText26(taskVarData.getUnicodeString (id, TASK_TEXT26));
         task.setText27(taskVarData.getUnicodeString (id, TASK_TEXT27));
         task.setText28(taskVarData.getUnicodeString (id, TASK_TEXT28));
         task.setText29(taskVarData.getUnicodeString (id, TASK_TEXT29));
         task.setText30(taskVarData.getUnicodeString (id, TASK_TEXT30));
         //task.setTotalSlack(); // Calculated value
         task.setType(MPPUtility.getShort(data, 126));
         task.setUniqueID(MPPUtility.getInt(data, 0));
         //task.setUniqueIDPredecessors(); // Calculated value
         //task.setUniqueIDSuccessors(); // Calculated value
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskVarData.getUnicodeString (id, TASK_WBS));
         //task.setWBSPredecessors(); // Calculated value
         //task.setWBSSuccessors(); // Calculated value
         task.setWork(new MPXDuration (MPPUtility.getDouble (data, 168)/60000, TimeUnit.HOURS));
         //task.setWorkContour(); // Calculated from resource
         //task.setWorkVariance(); // Calculated value

         //
         // Retrieve the task notes.
         //
         notes = taskVarData.getString (id, TASK_NOTES);
         if (notes != null)
         {
            if (file.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }

            task.addTaskNotes(notes);
         }

         //
         // Calculate the cost variance
         //
         if (task.getCost() != null && task.getBaselineCost() != null)
         {
            task.setCostVariance(new Double(task.getCost().doubleValue() - task.getBaselineCost().doubleValue()));
         }

         //
         // Set the calendar name
         //
         int calendarID = MPPUtility.getInt(data, 160);
         if (calendarID != -1)
         {
            MPXCalendar calendar = file.getBaseCalendarByUniqueID(calendarID);
            if (calendar != null)
            {
               task.setCalendarName(calendar.getName());
            }
         }
                 
         //dumpUnknownData (task.getName(), UNKNOWN_TASK_DATA, data);
      }
   }

   /**
    * This method is used to extract the task hyperlink attributes
    * from a block of data and call the appropriate modifier methods
    * to configure the specified task object.
    *
    * @param task task instance
    * @param data hyperlink data block
    */
   private static void processHyperlinkData (Task task, byte[] data)
   {
      if (data != null)
      {
         int offset = 12;
         String hyperlink;
         String address;
         String subaddress;

         offset += 12;
         hyperlink = MPPUtility.getUnicodeString(data, offset);
         offset += ((hyperlink.length()+1) * 2);

         offset += 12;
         address = MPPUtility.getUnicodeString(data, offset);
         offset += ((address.length()+1) * 2);

         offset += 12;
         subaddress = MPPUtility.getUnicodeString(data, offset);

         task.setHyperlink(hyperlink);
         task.setHyperlinkAddress(address);
         task.setHyperlinkSubAddress(subaddress);
      }
   }

   /**
    * This method extracts and collates constraint data
    *
    * @param file parent MPP file
    * @param projectDir root project directory
    * @throws IOException
    */
   private static void processConstraintData (MPPFile file,  DirectoryEntry projectDir)
      throws IOException
   {
      DirectoryEntry consDir = (DirectoryEntry)projectDir.getEntry ("TBkndCons");
      FixedMeta consFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedMeta"))), 10);
      FixedData consFixedData = new FixedData (20, new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedData"))));

      int count = consFixedMeta.getItemCount();
      int index;
      byte[] data;
      Task task1;
      Task task2;
      Relation rel;
      int durationUnits;
      int taskID1;
      int taskID2;
      byte[] metaData;

      for (int loop=0; loop < count; loop++)
      {
         metaData = consFixedMeta.getByteArrayValue(loop);

         if (MPPUtility.getInt(metaData, 0) == 0)
         {
            index = consFixedData.getIndexFromOffset(MPPUtility.getInt(metaData, 4));
            if (index != -1)
            {
               data = consFixedData.getByteArrayValue(index);
               taskID1 = MPPUtility.getInt (data, 4);
               taskID2 = MPPUtility.getInt (data, 8);

               if (taskID1 != taskID2)
               {
                  task1 = file.getTaskByUniqueID (taskID1);
                  task2 = file.getTaskByUniqueID (taskID2);
                  if (task1 != null && task2 != null)
                  {
                     rel = task2.addPredecessor(task1);
                     rel.setType (MPPUtility.getShort(data, 12));
                     durationUnits = MPPUtility.getDurationUnits(MPPUtility.getShort (data, 14));
                     rel.setDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 16), durationUnits));
                  }
               }
            }
         }
      }
   }


   /**
    * This method extracts and collates resource data
    *
    * @param file parent MPP file
    * @param projectDir root project directory
    * @param outlineCodeVarData outline code data
    * @param resourceCalendarMap map of resource IDs to resource data
    * @throws MPXException
    * @throws IOException
    */
   private static void processResourceData (MPPFile file,  DirectoryEntry projectDir, Var2Data outlineCodeVarData, HashMap resourceCalendarMap)
      throws MPXException, IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry)projectDir.getEntry ("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data (rscVarMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData (rscFixedMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedData"))));

      TreeMap resourceMap = createResourceMap (rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifiers();
      Integer id;
      Integer calendarID;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Resource resource;

      RTFUtility rtf = new RTFUtility ();
      String notes;

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         offset = (Integer)resourceMap.get(id);
         if (rscFixedData.isValidOffset(offset) == false)
         {
            continue;
         }

         data = rscFixedData.getByteArrayValue(offset.intValue());
         if (data.length < MINIMUM_EXPECTED_RESOURCE_SIZE)
         {
            continue;
         }
         
         resource = file.addResource();

         resource.setAccrueAt(AccrueType.getInstance (MPPUtility.getShort (data, 12)));
         resource.setActualCost(new Double(MPPUtility.getDouble(data, 132)/100));
         resource.setActualOvertimeCost(new Double(MPPUtility.getDouble(data, 172)/100));
         resource.setActualWork(new MPXDuration (MPPUtility.getDouble (data, 60)/60000, TimeUnit.HOURS));
         resource.setAvailableFrom(MPPUtility.getTimestamp(data, 20));
         resource.setAvailableTo(MPPUtility.getTimestamp(data, 24));
         //resource.setBaseCalendar();
         resource.setBaselineCost(new Double(MPPUtility.getDouble(data, 148)/100));
         resource.setBaselineWork(new MPXDuration (MPPUtility.getDouble (data, 68)/60000, TimeUnit.HOURS));
         resource.setCode (rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setCost(new Double(MPPUtility.getDouble(data, 140)/100));
         resource.setCost1(new Double (rscVarData.getDouble (id, RESOURCE_COST1) / 100));
         resource.setCost2(new Double (rscVarData.getDouble (id, RESOURCE_COST2) / 100));
         resource.setCost3(new Double (rscVarData.getDouble (id, RESOURCE_COST3) / 100));
         resource.setCost4(new Double (rscVarData.getDouble (id, RESOURCE_COST4) / 100));
         resource.setCost5(new Double (rscVarData.getDouble (id, RESOURCE_COST5) / 100));
         resource.setCost6(new Double (rscVarData.getDouble (id, RESOURCE_COST6) / 100));
         resource.setCost7(new Double (rscVarData.getDouble (id, RESOURCE_COST7) / 100));
         resource.setCost8(new Double (rscVarData.getDouble (id, RESOURCE_COST8) / 100));
         resource.setCost9(new Double (rscVarData.getDouble (id, RESOURCE_COST9) / 100));
         resource.setCost10(new Double (rscVarData.getDouble (id, RESOURCE_COST10) / 100));
         resource.setCostPerUse(new Double(MPPUtility.getDouble(data, 84)/100));
         resource.setDate1(rscVarData.getTimestamp (id, RESOURCE_DATE1));
         resource.setDate2(rscVarData.getTimestamp (id, RESOURCE_DATE2));
         resource.setDate3(rscVarData.getTimestamp (id, RESOURCE_DATE3));
         resource.setDate4(rscVarData.getTimestamp (id, RESOURCE_DATE4));
         resource.setDate5(rscVarData.getTimestamp (id, RESOURCE_DATE5));
         resource.setDate6(rscVarData.getTimestamp (id, RESOURCE_DATE6));
         resource.setDate7(rscVarData.getTimestamp (id, RESOURCE_DATE7));
         resource.setDate8(rscVarData.getTimestamp (id, RESOURCE_DATE8));
         resource.setDate9(rscVarData.getTimestamp (id, RESOURCE_DATE9));
         resource.setDate10(rscVarData.getTimestamp (id, RESOURCE_DATE10));
         resource.setDuration1(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION1), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION1_UNITS))));
         resource.setDuration2(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION2), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION2_UNITS))));
         resource.setDuration3(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION3), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION3_UNITS))));
         resource.setDuration4(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION4), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION4_UNITS))));
         resource.setDuration5(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION5), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION5_UNITS))));
         resource.setDuration6(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION6), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION6_UNITS))));
         resource.setDuration7(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION7), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION7_UNITS))));
         resource.setDuration8(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION8), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION8_UNITS))));
         resource.setDuration9(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION9), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION9_UNITS))));
         resource.setDuration10(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION10), MPPUtility.getDurationUnits(rscVarData.getShort(id, RESOURCE_DURATION10_UNITS))));
         resource.setEmailAddress(rscVarData.getUnicodeString (id, RESOURCE_EMAIL));
         resource.setFinish1(rscVarData.getTimestamp (id, RESOURCE_FINISH1));
         resource.setFinish2(rscVarData.getTimestamp (id, RESOURCE_FINISH2));
         resource.setFinish3(rscVarData.getTimestamp (id, RESOURCE_FINISH3));
         resource.setFinish4(rscVarData.getTimestamp (id, RESOURCE_FINISH4));
         resource.setFinish5(rscVarData.getTimestamp (id, RESOURCE_FINISH5));
         resource.setFinish6(rscVarData.getTimestamp (id, RESOURCE_FINISH6));
         resource.setFinish7(rscVarData.getTimestamp (id, RESOURCE_FINISH7));
         resource.setFinish8(rscVarData.getTimestamp (id, RESOURCE_FINISH8));
         resource.setFinish9(rscVarData.getTimestamp (id, RESOURCE_FINISH9));
         resource.setFinish10(rscVarData.getTimestamp (id, RESOURCE_FINISH10));
         resource.setGroup(rscVarData.getUnicodeString (id, RESOURCE_GROUP));
         resource.setID (MPPUtility.getInt (data, 4));
         resource.setInitials (rscVarData.getUnicodeString (id, RESOURCE_INITIALS));
         //resource.setLinkedFields(); // Calculated value
         resource.setMaxUnits(MPPUtility.getDouble(data, 44)/100);
         resource.setName (rscVarData.getUnicodeString (id, RESOURCE_NAME));
         resource.setNumber1(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER1)));
         resource.setNumber2(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER2)));
         resource.setNumber3(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER3)));
         resource.setNumber4(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER4)));
         resource.setNumber5(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER5)));
         resource.setNumber6(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER6)));
         resource.setNumber7(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER7)));
         resource.setNumber8(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER8)));
         resource.setNumber9(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER9)));
         resource.setNumber10(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER10)));
         resource.setNumber11(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER11)));
         resource.setNumber12(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER12)));
         resource.setNumber13(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER13)));
         resource.setNumber14(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER14)));
         resource.setNumber15(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER15)));
         resource.setNumber16(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER16)));
         resource.setNumber17(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER17)));
         resource.setNumber18(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER18)));
         resource.setNumber19(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER19)));
         resource.setNumber20(new Double (rscVarData.getDouble(id, RESOURCE_NUMBER20)));
         //resource.setObjects(); // Calculated value
         resource.setOutlineCode1(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE1)), OUTLINECODE_DATA));
         resource.setOutlineCode2(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE2)), OUTLINECODE_DATA));
         resource.setOutlineCode3(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE3)), OUTLINECODE_DATA));
         resource.setOutlineCode4(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE4)), OUTLINECODE_DATA));
         resource.setOutlineCode5(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE5)), OUTLINECODE_DATA));
         resource.setOutlineCode6(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE6)), OUTLINECODE_DATA));
         resource.setOutlineCode7(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE7)), OUTLINECODE_DATA));
         resource.setOutlineCode8(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE8)), OUTLINECODE_DATA));
         resource.setOutlineCode9(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE9)), OUTLINECODE_DATA));
         resource.setOutlineCode10(outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE10)), OUTLINECODE_DATA));
         //resource.setOverallocated(); // Calculated value
         resource.setOvertimeCost(new Double(MPPUtility.getDouble(data, 164)/100));
         resource.setOvertimeRate(new MPXRate (MPPUtility.getDouble(data, 36), TimeUnit.HOURS));
         resource.setOvertimeWork(new MPXDuration (MPPUtility.getDouble (data, 76)/60000, TimeUnit.HOURS));
         resource.setPeak(MPPUtility.getDouble(data, 124)/100);
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(new MPXDuration (MPPUtility.getDouble (data, 100)/60000, TimeUnit.HOURS));
         resource.setRemainingCost(new Double(MPPUtility.getDouble(data, 156)/100));
         resource.setRemainingOvertimeCost(new Double(MPPUtility.getDouble(data, 180)/100));
         resource.setRemainingWork(new MPXDuration (MPPUtility.getDouble (data, 92)/60000, TimeUnit.HOURS));
         resource.setStandardRate(new MPXRate (MPPUtility.getDouble(data, 28), TimeUnit.HOURS));
         resource.setStart1(rscVarData.getTimestamp (id, RESOURCE_START1));
         resource.setStart2(rscVarData.getTimestamp (id, RESOURCE_START2));
         resource.setStart3(rscVarData.getTimestamp (id, RESOURCE_START3));
         resource.setStart4(rscVarData.getTimestamp (id, RESOURCE_START4));
         resource.setStart5(rscVarData.getTimestamp (id, RESOURCE_START5));
         resource.setStart6(rscVarData.getTimestamp (id, RESOURCE_START6));
         resource.setStart7(rscVarData.getTimestamp (id, RESOURCE_START7));
         resource.setStart8(rscVarData.getTimestamp (id, RESOURCE_START8));
         resource.setStart9(rscVarData.getTimestamp (id, RESOURCE_START9));
         resource.setStart10(rscVarData.getTimestamp (id, RESOURCE_START10));
         resource.setText1(rscVarData.getUnicodeString (id, RESOURCE_TEXT1));
         resource.setText2(rscVarData.getUnicodeString (id, RESOURCE_TEXT2));
         resource.setText3(rscVarData.getUnicodeString (id, RESOURCE_TEXT3));
         resource.setText4(rscVarData.getUnicodeString (id, RESOURCE_TEXT4));
         resource.setText5(rscVarData.getUnicodeString (id, RESOURCE_TEXT5));
         resource.setText6(rscVarData.getUnicodeString (id, RESOURCE_TEXT6));
         resource.setText7(rscVarData.getUnicodeString (id, RESOURCE_TEXT7));
         resource.setText8(rscVarData.getUnicodeString (id, RESOURCE_TEXT8));
         resource.setText9(rscVarData.getUnicodeString (id, RESOURCE_TEXT9));
         resource.setText10(rscVarData.getUnicodeString (id, RESOURCE_TEXT10));
         resource.setText11(rscVarData.getUnicodeString (id, RESOURCE_TEXT11));
         resource.setText12(rscVarData.getUnicodeString (id, RESOURCE_TEXT12));
         resource.setText13(rscVarData.getUnicodeString (id, RESOURCE_TEXT13));
         resource.setText14(rscVarData.getUnicodeString (id, RESOURCE_TEXT14));
         resource.setText15(rscVarData.getUnicodeString (id, RESOURCE_TEXT15));
         resource.setText16(rscVarData.getUnicodeString (id, RESOURCE_TEXT16));
         resource.setText17(rscVarData.getUnicodeString (id, RESOURCE_TEXT17));
         resource.setText18(rscVarData.getUnicodeString (id, RESOURCE_TEXT18));
         resource.setText19(rscVarData.getUnicodeString (id, RESOURCE_TEXT19));
         resource.setText20(rscVarData.getUnicodeString (id, RESOURCE_TEXT20));
         resource.setText21(rscVarData.getUnicodeString (id, RESOURCE_TEXT21));
         resource.setText22(rscVarData.getUnicodeString (id, RESOURCE_TEXT22));
         resource.setText23(rscVarData.getUnicodeString (id, RESOURCE_TEXT23));
         resource.setText24(rscVarData.getUnicodeString (id, RESOURCE_TEXT24));
         resource.setText25(rscVarData.getUnicodeString (id, RESOURCE_TEXT25));
         resource.setText26(rscVarData.getUnicodeString (id, RESOURCE_TEXT26));
         resource.setText27(rscVarData.getUnicodeString (id, RESOURCE_TEXT27));
         resource.setText28(rscVarData.getUnicodeString (id, RESOURCE_TEXT28));
         resource.setText29(rscVarData.getUnicodeString (id, RESOURCE_TEXT29));
         resource.setText30(rscVarData.getUnicodeString (id, RESOURCE_TEXT30));
         resource.setType((MPPUtility.getShort(data, 14)==0?new Integer(1):new Integer(0)));
         resource.setUniqueID(id.intValue());
         resource.setWork(new MPXDuration (MPPUtility.getDouble (data, 52)/60000, TimeUnit.HOURS));

         metaData = rscFixedMeta.getByteArrayValue(offset.intValue());
         resource.setFlag1((metaData[28] & 0x40) != 0);
         resource.setFlag2((metaData[28] & 0x80) != 0);
         resource.setFlag3((metaData[29] & 0x01) != 0);
         resource.setFlag4((metaData[29] & 0x02) != 0);
         resource.setFlag5((metaData[29] & 0x04) != 0);
         resource.setFlag6((metaData[29] & 0x08) != 0);
         resource.setFlag7((metaData[29] & 0x10) != 0);
         resource.setFlag8((metaData[29] & 0x20) != 0);
         resource.setFlag9((metaData[29] & 0x40) != 0);
         resource.setFlag10((metaData[28] & 0x20) != 0);
         resource.setFlag11((metaData[29] & 0x20) != 0);
         resource.setFlag12((metaData[30] & 0x01) != 0);
         resource.setFlag13((metaData[30] & 0x02) != 0);
         resource.setFlag14((metaData[30] & 0x04) != 0);
         resource.setFlag15((metaData[30] & 0x08) != 0);
         resource.setFlag16((metaData[30] & 0x10) != 0);
         resource.setFlag17((metaData[30] & 0x20) != 0);
         resource.setFlag18((metaData[30] & 0x40) != 0);
         resource.setFlag19((metaData[30] & 0x80) != 0);
         resource.setFlag20((metaData[31] & 0x01) != 0);

         notes = rscVarData.getString (id, RESOURCE_NOTES);
         if (notes != null)
         {
            if (file.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }

            resource.addResourceNotes(notes);
         }

         //
         // Calculate the cost variance
         //
         if (resource.getCost() != null && resource.getBaselineCost() != null)
         {
            resource.setCostVariance(resource.getCost().doubleValue() - resource.getBaselineCost().doubleValue());
         }

         //
         // Calculate the work variance
         //
         if (resource.getWork() != null && resource.getBaselineWork() != null)
         {
            resource.setWorkVariance(new MPXDuration (resource.getWork().getDuration() - resource.getBaselineWork().getDuration(), TimeUnit.HOURS));
         }

         //
         // Configure the resource calendar
         //
         file.mppAttachResourceCalendar(resource, (MPXCalendar)resourceCalendarMap.get(id));
      }
   }


   /**
    * This method extracts and collates resource assignment data
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */
   private static void processAssignmentData (MPPFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry)projectDir.getEntry ("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data (assnVarMeta, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData (142, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedData"))));

      int count = assnFixedMeta.getItemCount();
      byte[] meta;
      byte[] data;
      Task task;
      Resource resource;
      ResourceAssignment assignment;
      int offset;
      
      for (int loop=0; loop < count; loop++)
      {
         meta = assnFixedMeta.getByteArrayValue(loop);
         if (meta[0] != 0)
         {         
            break;
         }
         
         offset = MPPUtility.getInt(meta, 4);            
         data = assnFixedData.getByteArrayValue(assnFixedData.getIndexFromOffset(offset));                          
         
         task = file.getTaskByUniqueID (MPPUtility.getInt (data, 4));
         resource = file.getResourceByUniqueID (MPPUtility.getInt (data, 8));
         
         if (task != null && resource != null)
         {
            assignment = task.addResourceAssignment (resource);
            assignment.setActualCost(new Double (MPPUtility.getDouble(data, 110)/100));
            assignment.setActualWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 70))/100, TimeUnit.HOURS));
            assignment.setCost(new Double (MPPUtility.getDouble(data, 102)/100));
            //assignment.setDelay(); // Not sure what this field maps on to in MSP
            assignment.setFinish(MPPUtility.getTimestamp(data, 16));
            //assignment.setOvertimeWork(); // Can't find in data block
            //assignment.setPlannedCost(); // Not sure what this field maps on to in MSP
            //assignment.setPlannedWork(); // Not sure what this field maps on to in MSP
            assignment.setRemainingWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 86))/100, TimeUnit.HOURS));
            assignment.setStart(MPPUtility.getTimestamp(data, 12));
            assignment.setUnits((MPPUtility.getDouble(data, 54))/100);
            assignment.setWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 62))/100, TimeUnit.HOURS));
         }
      }
   }

   /**
    * This method is used to determine if a duration is estimated.
    *
    * @param type Duration units value
    * @return boolean Estimated flag
    */
   private static boolean getDurationEstimated (int type)
   {
      return ((type & DURATION_CONFIRMED_MASK) != 0);
   }




   /**
    * This method converts between the numeric priority value
    * used in versions of MSP after MSP98 and the 10 priority
    * levels defined by the MPX standard.
    *
    * @param priority value read from MPP file
    * @return Priority object instance
    */
   private static Priority getPriority (int priority)
   {
      int result;

      if (priority >= 1000)
      {
         result = Priority.DO_NOT_LEVEL;
      }
      else
      {
         result = (priority-1) / 100;
      }

      return (Priority.getInstance (result));
   }

   /**
    * This method extracts view data from the MPP file
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws IOException
    */
   private static void processViewData (MPPFile file, DirectoryEntry projectDir)
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)projectDir.getEntry ("CV_iew");
      FixedData ff = new FixedData (122, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData"))));
      int items = ff.getItemCount();
      byte[] data;
      View view;
      String name;
      StringBuffer sb = new StringBuffer();

      for (int loop=0; loop < items; loop++)
      {
         data = ff.getByteArrayValue(loop);
         view = new View ();

         view.setID(MPPUtility.getInt(data, 0));
         name = MPPUtility.getUnicodeString(data, 4);

         if (name != null)
         {
            if (name.indexOf('&') != -1)
            {
               sb.setLength(0);
               int index = 0;
               char c;

               while (index < name.length())
               {
                  c = name.charAt(index);
                  if (c != '&')
                  {
                     sb.append(c);
                  }
                  ++index;
               }

               name = sb.toString();
            }
         }

         view.setName(name);
         file.addView(view);
      }
   }

   /**
    * This method extracts table data from the MPP file
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws IOException
    */
   private static void processTableData (MPPFile file, DirectoryEntry projectDir)
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)projectDir.getEntry ("CTable");
      FixedData fixedData = new FixedData (110, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData"))));
      VarMeta varMeta = new VarMeta (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));

      int items = fixedData.getItemCount();
      byte[] data;
      Table table;
      String name;
      StringBuffer sb = new StringBuffer();

      for (int loop=0; loop < items; loop++)
      {
         data = fixedData.getByteArrayValue(loop);

         table = new Table ();

         table.setID(MPPUtility.getInt(data, 0));
         table.setResourceFlag(MPPUtility.getShort(data, 108) == 1);
         name = MPPUtility.getUnicodeString(data, 4);

         if (name != null)
         {
            if (name.indexOf('&') != -1)
            {
               sb.setLength(0);
               int index = 0;
               char c;

               while (index < name.length())
               {
                  c = name.charAt(index);
                  if (c != '&')
                  {
                     sb.append(c);
                  }
                  ++index;
               }

               name = sb.toString();
            }
         }

         table.setName(name);
         file.addTable(table);

         processColumnData (table, varData.getByteArray(varMeta.getOffset(new Integer(table.getID()), TABLE_COLUMN_DATA)));
      }
   }

   /**
    * This method processes the column data associated with the
    * current table.
    *
    * @param table current table
    * @param data raw column data
    */
   private static void processColumnData (Table table, byte[] data)
   {
      if (data != null)
      {
         int columnCount = MPPUtility.getShort(data, 4)+1;
         int index = 8;
         int columnTitleOffset;
         Column  column;
         int alignment;

         for (int loop=0; loop < columnCount; loop++)
         {
            column = new Column (table);

            column.setFieldType (MPPUtility.getShort(data, index));
            column.setWidth (MPPUtility.getByte(data, index+4));

            columnTitleOffset = MPPUtility.getShort(data, index+6);
            if (columnTitleOffset != 0)
            {
               column.setTitle(MPPUtility.getUnicodeString(data, columnTitleOffset));
            }

            alignment = MPPUtility.getByte(data, index+8);
            if (alignment == 32)
            {
               column.setAlignTitle(Column.ALIGN_LEFT);
            }
            else
            {
               if (alignment == 33)
               {
                  column.setAlignTitle(Column.ALIGN_CENTER);
               }
               else
               {
                  column.setAlignTitle(Column.ALIGN_RIGHT);
               }
            }

            alignment = MPPUtility.getByte(data, index+10);
            if (alignment == 32)
            {
               column.setAlignData(Column.ALIGN_LEFT);
            }
            else
            {
               if (alignment == 33)
               {
                  column.setAlignData(Column.ALIGN_CENTER);
               }
               else
               {
                  column.setAlignData(Column.ALIGN_RIGHT);
               }
            }

            table.addColumn(column);
            index += 12;
         }
      }
   }


//   private static void dumpUnknownData (String name, int[][] spec, byte[] data)
//   {
//      System.out.println (name);
//      for (int loop=0; loop < spec.length; loop++)
//      {
//         System.out.println (spec[loop][0] + ": "+ MPPUtility.hexdump(data, spec[loop][0], spec[loop][1], false));
//      }
//      System.out.println ();
//   }
//
//   private static final int[][] UNKNOWN_TASK_DATA = new int[][]
//   {
//      {36, 4},
//      {42, 18},
//      {134, 14},
//      {156, 4},
//   };

//   private static final int[][] UNKNOWN_RESOURCE_DATA = new int[][]
//   {
//      {14, 6},
//      {108, 16},
//   };


   /**
    * Calendar data types.
    */
   private static final Integer CALENDAR_NAME = new Integer (1);
   private static final Integer CALENDAR_DATA = new Integer (3);

   /**
    * Task data types.
    */
   private static final Integer TASK_ACTUAL_OVERTIME_WORK = new Integer (3);
   private static final Integer TASK_REMAINING_OVERTIME_WORK = new Integer (4);
   private static final Integer TASK_OVERTIME_COST = new Integer (5);
   private static final Integer TASK_ACTUAL_OVERTIME_COST = new Integer (6);
   private static final Integer TASK_REMAINING_OVERTIME_COST = new Integer (7);

   private static final Integer TASK_WBS = new Integer (10);
   private static final Integer TASK_NAME = new Integer (11);
   private static final Integer TASK_CONTACT = new Integer (12);

   private static final Integer TASK_TEXT1 = new Integer (14);
   private static final Integer TASK_TEXT2 = new Integer (15);
   private static final Integer TASK_TEXT3 = new Integer (16);
   private static final Integer TASK_TEXT4 = new Integer (17);
   private static final Integer TASK_TEXT5 = new Integer (18);
   private static final Integer TASK_TEXT6 = new Integer (19);
   private static final Integer TASK_TEXT7 = new Integer (20);
   private static final Integer TASK_TEXT8 = new Integer (21);
   private static final Integer TASK_TEXT9 = new Integer (22);
   private static final Integer TASK_TEXT10 = new Integer (23);

   private static final Integer TASK_START1 = new Integer (24);
   private static final Integer TASK_FINISH1 = new Integer (25);
   private static final Integer TASK_START2 = new Integer (26);
   private static final Integer TASK_FINISH2 = new Integer (27);
   private static final Integer TASK_START3 = new Integer (28);
   private static final Integer TASK_FINISH3 = new Integer (29);
   private static final Integer TASK_START4 = new Integer (30);
   private static final Integer TASK_FINISH4 = new Integer (31);
   private static final Integer TASK_START5 = new Integer (32);
   private static final Integer TASK_FINISH5 = new Integer (33);
   private static final Integer TASK_START6 = new Integer (34);
   private static final Integer TASK_FINISH6 = new Integer (35);
   private static final Integer TASK_START7 = new Integer (36);
   private static final Integer TASK_FINISH7 = new Integer (37);
   private static final Integer TASK_START8 = new Integer (38);
   private static final Integer TASK_FINISH8 = new Integer (39);
   private static final Integer TASK_START9 = new Integer (40);
   private static final Integer TASK_FINISH9 = new Integer (41);
   private static final Integer TASK_START10 = new Integer (42);
   private static final Integer TASK_FINISH10 = new Integer (43);

   private static final Integer TASK_NUMBER1 = new Integer (45);
   private static final Integer TASK_NUMBER2 = new Integer (46);
   private static final Integer TASK_NUMBER3 = new Integer (47);
   private static final Integer TASK_NUMBER4 = new Integer (48);
   private static final Integer TASK_NUMBER5 = new Integer (49);
   private static final Integer TASK_NUMBER6 = new Integer (50);
   private static final Integer TASK_NUMBER7 = new Integer (51);
   private static final Integer TASK_NUMBER8 = new Integer (52);
   private static final Integer TASK_NUMBER9 = new Integer (53);
   private static final Integer TASK_NUMBER10 = new Integer (54);

   private static final Integer TASK_DURATION1 = new Integer (55);
   private static final Integer TASK_DURATION1_UNITS = new Integer (56);
   private static final Integer TASK_DURATION2 = new Integer (57);
   private static final Integer TASK_DURATION2_UNITS = new Integer (58);
   private static final Integer TASK_DURATION3 = new Integer (59);
   private static final Integer TASK_DURATION3_UNITS = new Integer (60);
   private static final Integer TASK_DURATION4 = new Integer (61);
   private static final Integer TASK_DURATION4_UNITS = new Integer (62);
   private static final Integer TASK_DURATION5 = new Integer (63);
   private static final Integer TASK_DURATION5_UNITS = new Integer (64);
   private static final Integer TASK_DURATION6 = new Integer (65);
   private static final Integer TASK_DURATION6_UNITS = new Integer (66);
   private static final Integer TASK_DURATION7 = new Integer (67);
   private static final Integer TASK_DURATION7_UNITS = new Integer (68);
   private static final Integer TASK_DURATION8 = new Integer (69);
   private static final Integer TASK_DURATION8_UNITS = new Integer (70);
   private static final Integer TASK_DURATION9 = new Integer (71);
   private static final Integer TASK_DURATION9_UNITS = new Integer (72);
   private static final Integer TASK_DURATION10 = new Integer (73);
   private static final Integer TASK_DURATION10_UNITS = new Integer (74);

   private static final Integer TASK_DATE1 = new Integer (80);
   private static final Integer TASK_DATE2 = new Integer (81);
   private static final Integer TASK_DATE3 = new Integer (82);
   private static final Integer TASK_DATE4 = new Integer (83);
   private static final Integer TASK_DATE5 = new Integer (84);
   private static final Integer TASK_DATE6 = new Integer (85);
   private static final Integer TASK_DATE7 = new Integer (86);
   private static final Integer TASK_DATE8 = new Integer (87);
   private static final Integer TASK_DATE9 = new Integer (88);
   private static final Integer TASK_DATE10 = new Integer (89);

   private static final Integer TASK_TEXT11 = new Integer (90);
   private static final Integer TASK_TEXT12 = new Integer (91);
   private static final Integer TASK_TEXT13 = new Integer (92);
   private static final Integer TASK_TEXT14 = new Integer (93);
   private static final Integer TASK_TEXT15 = new Integer (94);
   private static final Integer TASK_TEXT16 = new Integer (95);
   private static final Integer TASK_TEXT17 = new Integer (96);
   private static final Integer TASK_TEXT18 = new Integer (97);
   private static final Integer TASK_TEXT19 = new Integer (98);
   private static final Integer TASK_TEXT20 = new Integer (99);
   private static final Integer TASK_TEXT21 = new Integer (100);
   private static final Integer TASK_TEXT22 = new Integer (101);
   private static final Integer TASK_TEXT23 = new Integer (102);
   private static final Integer TASK_TEXT24 = new Integer (103);
   private static final Integer TASK_TEXT25 = new Integer (104);
   private static final Integer TASK_TEXT26 = new Integer (105);
   private static final Integer TASK_TEXT27 = new Integer (106);
   private static final Integer TASK_TEXT28 = new Integer (107);
   private static final Integer TASK_TEXT29 = new Integer (108);
   private static final Integer TASK_TEXT30 = new Integer (109);

   private static final Integer TASK_NUMBER11 = new Integer (110);
   private static final Integer TASK_NUMBER12 = new Integer (111);
   private static final Integer TASK_NUMBER13 = new Integer (112);
   private static final Integer TASK_NUMBER14 = new Integer (113);
   private static final Integer TASK_NUMBER15 = new Integer (114);
   private static final Integer TASK_NUMBER16 = new Integer (115);
   private static final Integer TASK_NUMBER17 = new Integer (116);
   private static final Integer TASK_NUMBER18 = new Integer (117);
   private static final Integer TASK_NUMBER19 = new Integer (118);
   private static final Integer TASK_NUMBER20 = new Integer (119);

   private static final Integer TASK_OUTLINECODE1 = new Integer (123);
   private static final Integer TASK_OUTLINECODE2 = new Integer (124);
   private static final Integer TASK_OUTLINECODE3 = new Integer (125);
   private static final Integer TASK_OUTLINECODE4 = new Integer (126);
   private static final Integer TASK_OUTLINECODE5 = new Integer (127);
   private static final Integer TASK_OUTLINECODE6 = new Integer (128);
   private static final Integer TASK_OUTLINECODE7 = new Integer (129);
   private static final Integer TASK_OUTLINECODE8 = new Integer (130);
   private static final Integer TASK_OUTLINECODE9 = new Integer (131);
   private static final Integer TASK_OUTLINECODE10 = new Integer (132);

   private static final Integer TASK_HYPERLINK = new Integer (133);

   private static final Integer TASK_COST1 = new Integer (134);
   private static final Integer TASK_COST2 = new Integer (135);
   private static final Integer TASK_COST3 = new Integer (136);
   private static final Integer TASK_COST4 = new Integer (137);
   private static final Integer TASK_COST5 = new Integer (138);
   private static final Integer TASK_COST6 = new Integer (139);
   private static final Integer TASK_COST7 = new Integer (140);
   private static final Integer TASK_COST8 = new Integer (141);
   private static final Integer TASK_COST9 = new Integer (142);
   private static final Integer TASK_COST10 = new Integer (143);

   private static final Integer TASK_NOTES = new Integer (144);

   /**
    * Resource data types.
    */
   private static final Integer RESOURCE_NAME = new Integer (1);
   private static final Integer RESOURCE_INITIALS = new Integer (3);
   private static final Integer RESOURCE_GROUP = new Integer (4);
   private static final Integer RESOURCE_CODE = new Integer (5);
   private static final Integer RESOURCE_EMAIL = new Integer (6);

   private static final Integer RESOURCE_TEXT1 = new Integer (10);
   private static final Integer RESOURCE_TEXT2 = new Integer (11);
   private static final Integer RESOURCE_TEXT3 = new Integer (12);
   private static final Integer RESOURCE_TEXT4 = new Integer (13);
   private static final Integer RESOURCE_TEXT5 = new Integer (14);
   private static final Integer RESOURCE_TEXT6 = new Integer (15);
   private static final Integer RESOURCE_TEXT7 = new Integer (16);
   private static final Integer RESOURCE_TEXT8 = new Integer (17);
   private static final Integer RESOURCE_TEXT9 = new Integer (18);
   private static final Integer RESOURCE_TEXT10 = new Integer (19);
   private static final Integer RESOURCE_TEXT11 = new Integer (20);
   private static final Integer RESOURCE_TEXT12 = new Integer (21);
   private static final Integer RESOURCE_TEXT13 = new Integer (22);
   private static final Integer RESOURCE_TEXT14 = new Integer (23);
   private static final Integer RESOURCE_TEXT15 = new Integer (24);
   private static final Integer RESOURCE_TEXT16 = new Integer (25);
   private static final Integer RESOURCE_TEXT17 = new Integer (26);
   private static final Integer RESOURCE_TEXT18 = new Integer (27);
   private static final Integer RESOURCE_TEXT19 = new Integer (28);
   private static final Integer RESOURCE_TEXT20 = new Integer (29);
   private static final Integer RESOURCE_TEXT21 = new Integer (30);
   private static final Integer RESOURCE_TEXT22 = new Integer (31);
   private static final Integer RESOURCE_TEXT23 = new Integer (32);
   private static final Integer RESOURCE_TEXT24 = new Integer (33);
   private static final Integer RESOURCE_TEXT25 = new Integer (34);
   private static final Integer RESOURCE_TEXT26 = new Integer (35);
   private static final Integer RESOURCE_TEXT27 = new Integer (36);
   private static final Integer RESOURCE_TEXT28 = new Integer (37);
   private static final Integer RESOURCE_TEXT29 = new Integer (38);
   private static final Integer RESOURCE_TEXT30 = new Integer (39);

   private static final Integer RESOURCE_START1 = new Integer (40);
   private static final Integer RESOURCE_START2 = new Integer (41);
   private static final Integer RESOURCE_START3 = new Integer (42);
   private static final Integer RESOURCE_START4 = new Integer (43);
   private static final Integer RESOURCE_START5 = new Integer (44);
   private static final Integer RESOURCE_START6 = new Integer (45);
   private static final Integer RESOURCE_START7 = new Integer (46);
   private static final Integer RESOURCE_START8 = new Integer (47);
   private static final Integer RESOURCE_START9 = new Integer (48);
   private static final Integer RESOURCE_START10 = new Integer (49);

   private static final Integer RESOURCE_FINISH1 = new Integer (50);
   private static final Integer RESOURCE_FINISH2 = new Integer (51);
   private static final Integer RESOURCE_FINISH3 = new Integer (52);
   private static final Integer RESOURCE_FINISH4 = new Integer (53);
   private static final Integer RESOURCE_FINISH5 = new Integer (54);
   private static final Integer RESOURCE_FINISH6 = new Integer (55);
   private static final Integer RESOURCE_FINISH7 = new Integer (56);
   private static final Integer RESOURCE_FINISH8 = new Integer (57);
   private static final Integer RESOURCE_FINISH9 = new Integer (58);
   private static final Integer RESOURCE_FINISH10 = new Integer (59);

   private static final Integer RESOURCE_NUMBER1 = new Integer (60);
   private static final Integer RESOURCE_NUMBER2 = new Integer (61);
   private static final Integer RESOURCE_NUMBER3 = new Integer (62);
   private static final Integer RESOURCE_NUMBER4 = new Integer (63);
   private static final Integer RESOURCE_NUMBER5 = new Integer (64);
   private static final Integer RESOURCE_NUMBER6 = new Integer (65);
   private static final Integer RESOURCE_NUMBER7 = new Integer (66);
   private static final Integer RESOURCE_NUMBER8 = new Integer (67);
   private static final Integer RESOURCE_NUMBER9 = new Integer (68);
   private static final Integer RESOURCE_NUMBER10 = new Integer (69);
   private static final Integer RESOURCE_NUMBER11 = new Integer (70);
   private static final Integer RESOURCE_NUMBER12 = new Integer (71);
   private static final Integer RESOURCE_NUMBER13 = new Integer (72);
   private static final Integer RESOURCE_NUMBER14 = new Integer (73);
   private static final Integer RESOURCE_NUMBER15 = new Integer (74);
   private static final Integer RESOURCE_NUMBER16 = new Integer (75);
   private static final Integer RESOURCE_NUMBER17 = new Integer (76);
   private static final Integer RESOURCE_NUMBER18 = new Integer (77);
   private static final Integer RESOURCE_NUMBER19 = new Integer (78);
   private static final Integer RESOURCE_NUMBER20 = new Integer (79);

   private static final Integer RESOURCE_DURATION1 = new Integer (80);
   private static final Integer RESOURCE_DURATION2 = new Integer (81);
   private static final Integer RESOURCE_DURATION3 = new Integer (82);
   private static final Integer RESOURCE_DURATION4 = new Integer (83);
   private static final Integer RESOURCE_DURATION5 = new Integer (84);
   private static final Integer RESOURCE_DURATION6 = new Integer (85);
   private static final Integer RESOURCE_DURATION7 = new Integer (86);
   private static final Integer RESOURCE_DURATION8 = new Integer (87);
   private static final Integer RESOURCE_DURATION9 = new Integer (88);
   private static final Integer RESOURCE_DURATION10 = new Integer (89);

   private static final Integer RESOURCE_DURATION1_UNITS = new Integer (90);
   private static final Integer RESOURCE_DURATION2_UNITS = new Integer (91);
   private static final Integer RESOURCE_DURATION3_UNITS = new Integer (92);
   private static final Integer RESOURCE_DURATION4_UNITS = new Integer (93);
   private static final Integer RESOURCE_DURATION5_UNITS = new Integer (94);
   private static final Integer RESOURCE_DURATION6_UNITS = new Integer (95);
   private static final Integer RESOURCE_DURATION7_UNITS = new Integer (96);
   private static final Integer RESOURCE_DURATION8_UNITS = new Integer (97);
   private static final Integer RESOURCE_DURATION9_UNITS = new Integer (98);
   private static final Integer RESOURCE_DURATION10_UNITS = new Integer (99);

   private static final Integer RESOURCE_DATE1 = new Integer (103);
   private static final Integer RESOURCE_DATE2 = new Integer (104);
   private static final Integer RESOURCE_DATE3 = new Integer (105);
   private static final Integer RESOURCE_DATE4 = new Integer (106);
   private static final Integer RESOURCE_DATE5 = new Integer (107);
   private static final Integer RESOURCE_DATE6 = new Integer (108);
   private static final Integer RESOURCE_DATE7 = new Integer (109);
   private static final Integer RESOURCE_DATE8 = new Integer (110);
   private static final Integer RESOURCE_DATE9 = new Integer (111);
   private static final Integer RESOURCE_DATE10 = new Integer (112);

   private static final Integer RESOURCE_OUTLINECODE1 = new Integer (113);
   private static final Integer RESOURCE_OUTLINECODE2 = new Integer (114);
   private static final Integer RESOURCE_OUTLINECODE3 = new Integer (115);
   private static final Integer RESOURCE_OUTLINECODE4 = new Integer (116);
   private static final Integer RESOURCE_OUTLINECODE5 = new Integer (117);
   private static final Integer RESOURCE_OUTLINECODE6 = new Integer (118);
   private static final Integer RESOURCE_OUTLINECODE7 = new Integer (119);
   private static final Integer RESOURCE_OUTLINECODE8 = new Integer (120);
   private static final Integer RESOURCE_OUTLINECODE9 = new Integer (121);
   private static final Integer RESOURCE_OUTLINECODE10 = new Integer (122);

   private static final Integer RESOURCE_NOTES = new Integer (124);

   private static final Integer RESOURCE_COST1 = new Integer (125);
   private static final Integer RESOURCE_COST2 = new Integer (126);
   private static final Integer RESOURCE_COST3 = new Integer (127);
   private static final Integer RESOURCE_COST4 = new Integer (128);
   private static final Integer RESOURCE_COST5 = new Integer (129);
   private static final Integer RESOURCE_COST6 = new Integer (130);
   private static final Integer RESOURCE_COST7 = new Integer (131);
   private static final Integer RESOURCE_COST8 = new Integer (132);
   private static final Integer RESOURCE_COST9 = new Integer (133);
   private static final Integer RESOURCE_COST10 = new Integer (134);

   private static final Integer TABLE_COLUMN_DATA = new Integer (1);
   private static final Integer OUTLINECODE_DATA = new Integer (1);

   /**
    * Mask used to isolate confirmed flag from the duration units field.
    */
   private static final int DURATION_CONFIRMED_MASK = 0x20;

   /**
    * Default working week
    */
   private static final boolean[] DEFAULT_WORKING_WEEK =
   {
      false,
      true,
      true,
      true,
      true,
      true,
      false
   };

   private static final int MINIMUM_EXPECTED_TASK_SIZE = 240;
   private static final int MINIMUM_EXPECTED_RESOURCE_SIZE = 188;
}
