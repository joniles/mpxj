/*
 * file:       MPP12Reader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2005
 * date:       05/12/2005
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

package net.sf.mpxj.mpp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.Column;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Table;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.View;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.utility.Pair;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;


/**
 * This class is used to represent a Microsoft Project MPP12 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP12Reader implements MPPVariantReader
{
   /**
    * This method is used to process an MPP12 file. This is the file format
    * used by Project 12.
    *
    * @param reader parent file reader
    * @param file parent MPP file
    * @param root Root of the POI file system.
    * @throws net.sf.mpxj.MPXJException Normally thrown on dat validation errors
    */
   public void process (MPPReader reader, ProjectFile file, DirectoryEntry root)
      throws MPXJException, IOException
   {
      m_reader = reader;
      
      //
      // Retrieve the high level document properties
      //
      Props12 props12 = new Props12 (new DocumentInputStream (((DocumentEntry)root.getEntry("Props12"))));
      //System.out.println(props12);

      //
      // Test for password protection. In the single byte retrieved here:
      //
      // 0x00 = no password
      // 0x01 = protection password has been supplied
      // 0x02 = write reservation password has been supplied
      // 0x03 = both passwords have been supplied
      //
      if ((props12.getByte(Props.PASSWORD_FLAG) & 0x01) != 0)
      {
         throw new MPXJException (MPXJException.PASSWORD_PROTECTED);
      }

      //
      // Retrieve the project directory
      //
      DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   112");


      DirectoryEntry outlineCodeDir = (DirectoryEntry)projectDir.getEntry ("TBkndOutlCode");
      VarMeta outlineCodeVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("VarMeta"))));
      Var2Data outlineCodeVarData = new Var2Data (outlineCodeVarMeta, new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("Var2Data"))));

      //
      // Extract the required data from the MPP file
      //
      HashMap resourceMap = new HashMap ();

      processPropertyData (file, root, projectDir);
      processCalendarData (file, projectDir, resourceMap);
      processResourceData (file, projectDir, outlineCodeVarData, resourceMap);
      processTaskData (file, projectDir, outlineCodeVarData);
      processConstraintData (file, projectDir);
      processAssignmentData (file, projectDir);

      projectDir = (DirectoryEntry)root.getEntry ("   212");
      processViewPropertyData(file, projectDir);
      processTableData (file, projectDir);
      processViewData (file, projectDir);
   }

   /**
    * This method extracts and collates global property data.
    *
    * @param file Parent MPX file
    * @param rootDir root direcory of the file
    * @param projectDir Project data directory
    * @throws java.io.IOException
    */
   private void processPropertyData (ProjectFile file,  DirectoryEntry rootDir, DirectoryEntry projectDir)
      throws IOException, MPXJException
   {
      Props12 props = new Props12 (new DocumentInputStream (((DocumentEntry)projectDir.getEntry("Props"))));
      //MPPUtility.fileHexDump("c:\\temp\\props.txt", props.toString().getBytes());

      ProjectHeader ph = file.getProjectHeader();
      ph.setScheduleFrom(ScheduleFrom.getInstance(1-props.getShort(Props.SCHEDULE_FROM)));
      ph.setCalendarName(props.getUnicodeString(Props.DEFAULT_CALENDAR_NAME));
      ph.setDefaultStartTime(props.getTime(Props.START_TIME));
      ph.setDefaultEndTime(props.getTime(Props.END_TIME));

      //ph.setDefaultDurationIsFixed();
      ph.setDefaultDurationUnits(MPPUtility.getDurationTimeUnits(props.getShort(Props.DURATION_UNITS)));
      ph.setDefaultHoursInDay(new Float(((float)props.getInt(Props.HOURS_PER_DAY))/60));
      ph.setDefaultHoursInWeek(new Float(((float)props.getInt(Props.HOURS_PER_WEEK))/60));
      ph.setDefaultOvertimeRate(new Rate (props.getDouble(Props.OVERTIME_RATE), TimeUnit.HOURS));
      ph.setDefaultStandardRate(new Rate (props.getDouble(Props.STANDARD_RATE), TimeUnit.HOURS));
      ph.setDefaultWorkUnits(MPPUtility.getWorkTimeUnits(props.getShort(Props.WORK_UNITS)));
      ph.setSplitInProgressTasks(props.getBoolean(Props.SPLIT_TASKS));
      ph.setUpdatingTaskStatusUpdatesResourceStatus(props.getBoolean(Props.TASK_UPDATES_RESOURCE));

      ph.setCurrencyDigits(new Integer(props.getShort(Props.CURRENCY_DIGITS)));
      ph.setCurrencySymbol(props.getUnicodeString(Props.CURRENCY_SYMBOL));
      //ph.setDecimalSeparator();
      ph.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(Props.CURRENCY_PLACEMENT)));
      //ph.setThousandsSeparator();

      processTaskFieldNameAliases(file, props.getByteArray(Props.TASK_FIELD_NAME_ALIASES));
      processResourceFieldNameAliases(file, props.getByteArray(Props.RESOURCE_FIELD_NAME_ALIASES));

      SummaryInformation summary = new SummaryInformation (rootDir);
      ph.setProjectTitle(summary.getProjectTitle());
      ph.setSubject(summary.getSubject());
      ph.setAuthor(summary.getAuthor());
      ph.setKeywords(summary.getKeywords());
      ph.setComments(summary.getComments());
      ph.setCompany(summary.getCompany());
      ph.setManager(summary.getManager());
      ph.setCategory(summary.getCategory());
      ph.setRevision(summary.getRevision());
      ph.setDocumentSummaryInformation(summary.getDocumentSummaryInformation());

      ph.setCalculateMultipleCriticalPaths(props.getBoolean(Props.CALCULATE_MULTIPLE_CRITICAL_PATHS));

      processSubProjectData(file, props);
   }

   /**
    * Read sub project data from the file, and add it to a hash map
    * indexed by task ID.
    *
    * @param file parent file
    * @param props file properties
    */
   private void processSubProjectData (ProjectFile file, Props12 props)
   {
      byte[] subProjData = props.getByteArray(Props.SUBPROJECT_DATA);

      //System.out.println (MPPUtility.hexdump(subProjData, true, 16, ""));
      //MPPUtility.fileHexDump("c:\\temp\\dump.txt", subProjData);

      if (subProjData != null)
      {
         int offset = 0;
         int itemHeaderOffset;
         int uniqueIDOffset;
         int filePathOffset;
         int fileNameOffset;
         SubProject sp;

         byte[] itemHeader = new byte[20];

         /*int blockSize = MPPUtility.getInt(subProjData, offset);*/
         offset += 4;

         /*int unknown = MPPUtility.getInt(subProjData, offset);*/
         offset += 4;

         int itemCountOffset = MPPUtility.getInt(subProjData, offset);
         offset += 4;

         while (offset < itemCountOffset)
         {
            itemHeaderOffset = MPPUtility.getShort(subProjData, offset);
            offset += 4;

            MPPUtility.getByteArray(subProjData, itemHeaderOffset, itemHeader.length, itemHeader, 0);

            //System.out.println (MPPUtility.hexdump(itemHeader, false, 16, ""));
            //System.out.println (offset);

            switch (itemHeader[16])
            {
               //
               // Project name or file name strings, repeated twice
               //
               case 0x00:
               {
                  offset += 8;
                  break;
               }

               //
               // task unique ID, 8 bytes, path, file name
               //
               case 0x09:
               case 0x11:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // sometimes offset of a task ID?
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);                  
                  m_taskSubProjects.put(sp.getUniqueID(), sp);
                  break;
               }



               //
               // task unique ID, path, unknown, file name
               //
               case (byte)0x81:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // unknown offset to 2 bytes of data?
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);                  
                  m_taskSubProjects.put(sp.getUniqueID(), sp);
                  break;
               }

               //
               // task unique ID, path, file name
               //
               case 0x01:
               case 0x08:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);                  
                  m_taskSubProjects.put(sp.getUniqueID(), sp);
                  break;
               }

               //
               // resource unique ID, path, file name
               //
               case 0x05:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);                  
                  file.setResourceSubProject(sp);
                  break;
               }

               //
               // path, file name
               //
               case 0x02:
               case 0x04:
               {
                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset);                  
                  file.setResourceSubProject(sp);
                  break;
               }

               //
               // Appears when a subproject is collapsed
               //
               case (byte)0x80:
               {
                  offset += 12;
                  break;
               }

               //
               // Any other value, assume 12 bytes to handle old/deleted data?
               //
               default:
               {
                  offset += 12;
                  break;
               }
            }
         }
      }
   }

   /**
    * Method used to read the sub project details from a byte array.
    * 
    * @param data byte array 
    * @param uniqueIDOffset offset of unique ID
    * @param filePathOffset offset of file path
    * @param fileNameOffset offset of file name
    * @return new SubProject instance
    */
   private SubProject readSubProject (byte[] data, int uniqueIDOffset, int filePathOffset, int fileNameOffset)
   {
      SubProject sp = new SubProject ();      
      
      if (uniqueIDOffset != -1)
      {
         sp.setUniqueID(new Integer(MPPUtility.getInt(data, uniqueIDOffset)));
      }

      //
      // First block header
      //
      filePathOffset += 18;
      
      //
      // String size as a 4 byte int
      //
      filePathOffset += 4;
      
      //
      // Full DOS path
      //
      sp.setDosFullPath(MPPUtility.getString(data, filePathOffset));
      filePathOffset += (sp.getDosFullPath().length()+1);
      
      //
      // 24 byte block
      //
      filePathOffset += 24;
      
      //
      // 4 byte block size
      //
      int size = MPPUtility.getInt(data, filePathOffset);      
      filePathOffset +=4;
      if (size == 0)
      {
         sp.setFullPath(sp.getDosFullPath());
      }
      else
      {
         //
         // 4 byte unicode string size in bytes
         //
         size = MPPUtility.getInt(data, filePathOffset);
         filePathOffset += 4;
         
         //
         // 2 byte data
         //
         filePathOffset += 2;
         
         //
         // Unicode string
         //
         sp.setFullPath(MPPUtility.getUnicodeString(data, filePathOffset, size));
         filePathOffset += size;
      }
      
      //
      // Second block header
      //
      fileNameOffset += 18;
      
      //
      // String size as a 4 byte int
      //
      fileNameOffset += 4;
      
      //
      // DOS file name
      //
      sp.setDosFileName(MPPUtility.getString(data, fileNameOffset));
      fileNameOffset += (sp.getDosFileName().length()+1);
      
      //
      // 24 byte block
      //
      fileNameOffset += 24;
      
      //
      // 4 byte block size
      //
      size = MPPUtility.getInt(data, fileNameOffset);
      fileNameOffset +=4;
      
      if (size == 0)
      {
         sp.setFileName(sp.getDosFileName());
      }
      else
      {
         //
         // 4 byte unicode string size in bytes
         //
         size = MPPUtility.getInt(data, fileNameOffset);
         fileNameOffset += 4;
   
         //
         // 2 byte data
         //
         fileNameOffset += 2;
         
         //
         // Unicode string
         //
         sp.setFileName(MPPUtility.getUnicodeString(data, fileNameOffset, size));
         fileNameOffset += size;      
      }      
      
      //System.out.println(this.toString());
      
      return (sp);
   }

   /**
    * This method process the data held in the props file specific to the
    * visual appearance of the project data.
    *
    * @param file parent file
    * @param projectDir project directory
    */
   private void processViewPropertyData (ProjectFile file,  DirectoryEntry projectDir)
      throws IOException
   {
      Props12 props = new Props12 (new DocumentInputStream (((DocumentEntry)projectDir.getEntry("Props"))));
      byte[] data = props.getByteArray(Props.FONT_BASES);
      if (data != null)
      {
         processBaseFonts (file, data);
      }
   }

   /**
    * Create an index of base font numbers and their associated base
    * font instances.
    *
    * @param file parent file
    * @param data property data
    */
   private void processBaseFonts (ProjectFile file, byte[] data)
   {
      int offset = 0;

      int blockCount = MPPUtility.getShort(data, 0);
      offset +=2;

      int size;
      String name;

      for (int loop=0; loop < blockCount; loop++)
      {
         /*unknownAttribute = MPPUtility.getShort(data, offset);*/
         offset += 2;

         size = MPPUtility.getShort(data, offset);
         offset += 2;

         name = MPPUtility.getUnicodeString(data, offset);
         offset += 64;

         if (name.length() != 0)
         {
            FontBase fontBase = new FontBase(new Integer(loop), name, size);
            m_fontBases.put(fontBase.getIndex(), fontBase);
         }
      }
   }

   /**
    * Retrieve any task field aliases defined in the MPP file.
    *
    * @param file Parent MPX file
    * @param data task field name alias data
    */
   private void processTaskFieldNameAliases (ProjectFile file, byte[] data)
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
   private void processResourceFieldNameAliases (ProjectFile file, byte[] data)
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
    * within the FixedData block.
    *
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @return Mapping between task identifiers and block position
    */
   private TreeMap createTaskMap (FixedMeta taskFixedMeta, FixedData taskFixedData)
   {
      TreeMap taskMap = new TreeMap ();
      int itemCount = taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;
      Integer key;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);
         if (data != null && data.length >= MINIMUM_EXPECTED_TASK_SIZE)
         {
            uniqueID = MPPUtility.getInt(data, 0);
            key = new Integer(uniqueID);
            if (taskMap.containsKey(key) == false)
            {
               taskMap.put(key, new Integer (loop));
            }
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
   private TreeMap createResourceMap (FixedMeta rscFixedMeta, FixedData rscFixedData)
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
    * @throws java.io.IOException
    */
   private void processCalendarData (ProjectFile file,  DirectoryEntry projectDir, HashMap resourceMap)
      throws MPXJException, IOException
   {
      DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");

      MPPUtility.fileHexDump("c:\\temp\\varmeta.txt", new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));
      
      VarMeta calVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));

      DocumentEntry v2d = (DocumentEntry)calDir.getEntry("Var2Data");
      DocumentInputStream dis=  new DocumentInputStream (v2d);
      Var2Data calVarData = new Var2Data (calVarMeta, dis);

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
      ProjectCalendar cal;
      List baseCalendars = new LinkedList();

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
                        cal = file.getResourceCalendar();
                     }
                     else
                     {
                        cal = file.getDefaultResourceCalendar();
                     }

                     baseCalendars.add(new Pair(cal, new Integer(baseCalendarID)));
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

      updateBaseCalendarNames (baseCalendars, calendarMap);
   }

   /**
    * For a given set of calendar data, this method sets the working
    * day status for each day, and if present, sets the hours for that
    * day.
    *
    * @param data calendar data block
    * @param cal calendar instance
    * @throws net.sf.mpxj.MPXJException
    */
   private void processCalendarHours (byte[] data, ProjectCalendar cal)
      throws MPXJException
   {
      int offset;
      ProjectCalendarHours hours;
      int periodCount;
      int periodIndex;
      int index;
      int defaultFlag;
      Date start;
      long duration;
      Day day;

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
         throw new MPXJException (MPXJException.INVALID_FORMAT, ex);
      }

      for (index=0; index < 7; index++)
      {
         offset = 4 + (60 * index);
         defaultFlag = MPPUtility.getShort (data, offset);
         day = Day.getInstance(index+1);

         if (defaultFlag == 1)
         {
            if (cal.isBaseCalendar() == true)
            {
               cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
               if (cal.isWorkingDay(day) == true)
               {
                  hours = cal.addCalendarHours(Day.getInstance(index+1));
                  hours.addDateRange(new DateRange(defaultStart1, defaultEnd1));
                  hours.addDateRange(new DateRange(defaultStart2, defaultEnd2));
               }
            }
            else
            {
               cal.setWorkingDay(day, ProjectCalendar.DEFAULT);
            }
         }
         else
         {
            periodCount = MPPUtility.getShort (data, offset+2);
            if (periodCount == 0)
            {
               cal.setWorkingDay(day, false);
            }
            else
            {
               cal.setWorkingDay(day, true);
               hours = cal.addCalendarHours(Day.getInstance(index+1));

               for (periodIndex=0; periodIndex < periodCount; periodIndex++)
               {
                  start = MPPUtility.getTime (data, offset + 8 + (periodIndex * 2));
                  duration = MPPUtility.getDuration (data, offset + 20 + (periodIndex * 4));
                  hours.addDateRange(new DateRange (start, new Date (start.getTime()+duration)));
               }
            }
         }
      }
   }


   /**
    * This method extracts any exceptions associated with a calendar.
    *
    * @param data calendar data block
    * @param cal calendar instance
    * @throws net.sf.mpxj.MPXJException
    */
   private void processCalendarExceptions (byte[] data, ProjectCalendar cal)
      throws MPXJException
   {
      //
      // Handle any exceptions
      //
      int exceptionCount = MPPUtility.getShort (data, 0);

      if (exceptionCount != 0)
      {
         int index;
         int offset;
         ProjectCalendarException exception;
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
    * The way calendars are stored in an MPP12 file means that there
    * can be forward references between the base calendar unique ID for a
    * derived calendar, and the base calendar itself. To get around this,
    * we initially populate the base calendar name attribute with the
    * base calendar unique ID, and now in this method we can convert those
    * ID values into the correct names.
    *
    * @param baseCalendars list of calendars and base calendar IDs
    * @param map map of calendar ID values and calendar objects
    */
   private void updateBaseCalendarNames (List baseCalendars, HashMap map)
   {
      Iterator iter = baseCalendars.iterator();
      Pair pair;
      ProjectCalendar cal;
      Integer baseCalendarID;
      ProjectCalendar baseCal;

      while (iter.hasNext() == true)
      {
         pair = (Pair)iter.next();
         cal = (ProjectCalendar)pair.getFirst();
         baseCalendarID = (Integer)pair.getSecond();

         baseCal = (ProjectCalendar)map.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setBaseCalendar(baseCal);
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
    * @throws net.sf.mpxj.MPXJException
    * @throws java.io.IOException
    */
   private void processTaskData (ProjectFile file,  DirectoryEntry projectDir, Var2Data outlineCodeVarData)
      throws MPXJException, IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
      VarMeta taskVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data (taskVarMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData (taskFixedMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedData"))));
      //System.out.println(taskFixedData);
      //System.out.println(taskVarData);

      TreeMap taskMap = createTaskMap (taskFixedMeta, taskFixedData);
      Integer[] uniqueid = taskVarMeta.getUniqueIdentifierArray();
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
         //System.out.println (MPPUtility.hexdump(data, false, 16, ""));
         //System.out.println (MPPUtility.hexdump(metaData, false, 16, ""));

         task = file.addTask();
         task.setActualCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 216) / 100));
         task.setActualDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 66), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 100));
         task.setActualOvertimeCost (NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_COST)));
         task.setActualOvertimeWork(Duration.getInstance (taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_WORK)/60000, TimeUnit.HOURS));
         task.setActualStart(MPPUtility.getTimestamp (data, 96));
         task.setActualWork(Duration.getInstance (MPPUtility.getDouble (data, 184)/60000, TimeUnit.HOURS));
         //task.setACWP(); // Calculated value
         //task.setAssignment(); // Calculated value
         //task.setAssignmentDelay(); // Calculated value
         //task.setAssignmentUnits(); // Calculated value
         task.setBaselineCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 232) / 100));
         task.setBaselineDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationTimeUnits (MPPUtility.getShort (data, 78))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 108));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 104));
         task.setBaselineWork(Duration.getInstance (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));

// From MS Project 2003
//         task.setBaseline1Cost(NumberUtility.getDouble (MPPUtility.getDouble (data, 232) / 100));
//         task.setBaseline1Duration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationTimeUnits (MPPUtility.getShort (data, 78))));
//         task.setBaseline1Finish(MPPUtility.getTimestamp (data, 108));
//         task.setBaseline1Start(MPPUtility.getTimestamp (data, 104));
//         task.setBaseline1Work(MPXDuration.getInstance (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));
// to...
//         task.setBaseline10Cost(NumberUtility.getDouble (MPPUtility.getDouble (data, 232) / 100));
//         task.setBaseline10Duration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationTimeUnits (MPPUtility.getShort (data, 78))));
//         task.setBaseline10Finish(MPPUtility.getTimestamp (data, 108));
//         task.setBaseline10Start(MPPUtility.getTimestamp (data, 104));
//         task.setBaseline10Work(MPXDuration.getInstance (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));


         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate (MPPUtility.getTimestamp (data, 112));
         task.setConstraintType (ConstraintType.getInstance (MPPUtility.getShort (data, 80)));
         task.setContact(taskVarData.getUnicodeString (id, TASK_CONTACT));
         task.setCost(NumberUtility.getDouble (MPPUtility.getDouble(data, 200) / 100));
         //task.setCostRateTable(); // Calculated value
         //task.setCostVariance(); // Populated below
         task.setCost1(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST1) / 100));
         task.setCost2(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST2) / 100));
         task.setCost3(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST3) / 100));
         task.setCost4(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST4) / 100));
         task.setCost5(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST5) / 100));
         task.setCost6(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST6) / 100));
         task.setCost7(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST7) / 100));
         task.setCost8(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST8) / 100));
         task.setCost9(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST9) / 100));
         task.setCost10(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST10) / 100));

// From MS Project 2003
//         task.setCPI();

         task.setCreateDate(MPPUtility.getTimestamp (data, 130));
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
         task.setDuration (MPPUtility.getAdjustedDuration (file, MPPUtility.getInt (data, 60), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         //task.setDurationVariance(); // Calculated value
         task.setDuration1(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION1), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION1_UNITS))));
         task.setDuration2(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION2), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION2_UNITS))));
         task.setDuration3(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION3), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION3_UNITS))));
         task.setDuration4(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION4), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION4_UNITS))));
         task.setDuration5(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION5), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION5_UNITS))));
         task.setDuration6(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION6), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION6_UNITS))));
         task.setDuration7(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION7), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION7_UNITS))));
         task.setDuration8(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION8), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION8_UNITS))));
         task.setDuration9(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION9), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION9_UNITS))));
         task.setDuration10(MPPUtility.getAdjustedDuration (file, taskVarData.getInt(id, TASK_DURATION10), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION10_UNITS))));
//       From MS Project 2003
//         task.setEAC();
         task.setEarlyFinish (MPPUtility.getTimestamp (data, 8));
         task.setEarlyStart (MPPUtility.getTimestamp (data, 88));
//       From MS Project 2003
//         task.setEarnedValueMethod();
         task.setEffortDriven((metaData[11] & 0x10) != 0);
         task.setEstimated(getDurationEstimated(MPPUtility.getShort (data, 64)));
         task.setExpanded(((metaData[12] & 0x02) == 0));
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
         task.setFixedCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 208) / 100));
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
         task.setLateFinish(MPPUtility.getTimestamp(data, 92));
         task.setLateStart(MPPUtility.getTimestamp(data, 12));
         task.setLevelAssignments((metaData[13] & 0x04) != 0);
         task.setLevelingCanSplit((metaData[13] & 0x02) != 0);
         task.setLevelingDelay (MPPUtility.getDuration (((double)MPPUtility.getInt (data, 82))/3, MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 86))));
         //task.setLinkedFields();  // Calculated value
         task.setMarked((metaData[9] & 0x40) != 0);
         task.setMilestone((metaData[8] & 0x20) != 0);
         task.setName(taskVarData.getUnicodeString (id, TASK_NAME));
         task.setNumber1(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER1)));
         task.setNumber2(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER2)));
         task.setNumber3(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER3)));
         task.setNumber4(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER4)));
         task.setNumber5(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER5)));
         task.setNumber6(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER6)));
         task.setNumber7(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER7)));
         task.setNumber8(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER8)));
         task.setNumber9(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER9)));
         task.setNumber10(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER10)));
         task.setNumber11(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER11)));
         task.setNumber12(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER12)));
         task.setNumber13(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER13)));
         task.setNumber14(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER14)));
         task.setNumber15(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER15)));
         task.setNumber16(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER16)));
         task.setNumber17(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER17)));
         task.setNumber18(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER18)));
         task.setNumber19(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER19)));
         task.setNumber20(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER20)));
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
         task.setOvertimeCost(NumberUtility.getDouble(taskVarData.getDouble(id, TASK_OVERTIME_COST)));
         //task.setOvertimeWork(); // Calculated value?
         //task.getPredecessors(); // Calculated value
         task.setPercentageComplete(NumberUtility.getDouble(MPPUtility.getShort(data, 122)));
         task.setPercentageWorkComplete(NumberUtility.getDouble(MPPUtility.getShort(data, 124)));
//       From MS Project 2003
//         task.setPhysicalPercentComplete();
         task.setPreleveledFinish(MPPUtility.getTimestamp(data, 140));
         task.setPreleveledStart(MPPUtility.getTimestamp(data, 136));
         task.setPriority(Priority.getInstance(MPPUtility.getShort (data, 120)));
         //task.setProject(); // Calculated value
         //task.setRecurring(); // Calculated value
         //task.setRegularWork(); // Calculated value
         task.setRemainingCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 224)/100));
         task.setRemainingDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 70), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         task.setRemainingOvertimeCost(NumberUtility.getDouble(taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_COST)));
         task.setRemainingOvertimeWork(Duration.getInstance (taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_WORK)/60000, TimeUnit.HOURS));
         task.setRemainingWork(Duration.getInstance (MPPUtility.getDouble (data, 192)/60000, TimeUnit.HOURS));
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
         task.setSubprojectTasksUniqueIDOffset(new Integer (taskVarData.getInt(id, TASK_SUBPROJECT_TASKS_UNIQUEID_OFFSET)));
         task.setSubprojectTaskUniqueID(new Integer (taskVarData.getInt(id, TASK_SUBPROJECTTASKID)));
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
         task.setType(TaskType.getInstance(MPPUtility.getShort(data, 126)));
         task.setUniqueID(MPPUtility.getInt(data, 0));
         //task.setUniqueIDPredecessors(); // Calculated value
         //task.setUniqueIDSuccessors(); // Calculated value
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskVarData.getUnicodeString (id, TASK_WBS));
         //task.setWBSPredecessors(); // Calculated value
         //task.setWBSSuccessors(); // Calculated value
         task.setWork(Duration.getInstance (MPPUtility.getDouble (data, 168)/60000, TimeUnit.HOURS));
         //task.setWorkContour(); // Calculated from resource
         //task.setWorkVariance(); // Calculated value

         //
         // Set the MPX file fixed flag
         //
         task.setFixed(task.getType() == TaskType.FIXED_DURATION);

         //
         // Adjust the start and finish dates if the task
         // is constrained to start as late as possible.
         //
         if (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE)
         {
            task.setStart(task.getLateStart());
            task.setFinish(task.getLateFinish());
         }

         //
         // Retrieve the task notes.
         //
         notes = taskVarData.getString (id, TASK_NOTES);
         if (notes != null)
         {
            if (m_reader.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }

            task.setNotes(notes);
         }

         //
         // Calculate the cost variance
         //
         if (task.getCost() != null && task.getBaselineCost() != null)
         {
            task.setCostVariance(NumberUtility.getDouble(task.getCost().doubleValue() - task.getBaselineCost().doubleValue()));
         }

         //
         // Set the calendar name
         //
         int calendarID = MPPUtility.getInt(data, 160);
         if (calendarID != -1)
         {
            ProjectCalendar calendar = file.getBaseCalendarByUniqueID(calendarID);
            if (calendar != null)
            {
               task.setCalendar(calendar);
            }
         }

         //
         // Set the sub project flag
         //
         task.setSubProject((SubProject)m_taskSubProjects.get(task.getUniqueID()));

         file.fireTaskReadEvent(task);
         
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
   private void processHyperlinkData (Task task, byte[] data)
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
    * This method extracts and collates constraint data.
    *
    * @param file parent MPP file
    * @param projectDir root project directory
    * @throws java.io.IOException
    */
   private void processConstraintData (ProjectFile file,  DirectoryEntry projectDir)
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
      TimeUnit durationUnits;
      int taskID1;
      int taskID2;
      int constraintID;
      int lastConstraintID = -1;
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
               constraintID = MPPUtility.getInt (data, 0);
               if (constraintID > lastConstraintID)
               {
                  lastConstraintID = constraintID;
                  taskID1 = MPPUtility.getInt (data, 4);
                  taskID2 = MPPUtility.getInt (data, 8);

                  if (taskID1 != taskID2)
                  {
                     task1 = file.getTaskByUniqueID (taskID1);
                     task2 = file.getTaskByUniqueID (taskID2);

                     if (task1 != null && task2 != null)
                     {
                        rel = task2.addPredecessor(task1);
                        rel.setType (RelationType.getInstance(MPPUtility.getShort(data, 12)));
                        durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 14));
                        rel.setDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 16), durationUnits));
                     }
                  }
               }
            }
         }
      }
   }


   /**
    * This method extracts and collates resource data.
    *
    * @param file parent MPP file
    * @param projectDir root project directory
    * @param outlineCodeVarData outline code data
    * @param resourceCalendarMap map of resource IDs to resource data
    * @throws net.sf.mpxj.MPXJException
    * @throws java.io.IOException
    */
   private void processResourceData (ProjectFile file,  DirectoryEntry projectDir, Var2Data outlineCodeVarData, HashMap resourceCalendarMap)
      throws MPXJException, IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry)projectDir.getEntry ("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data (rscVarMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData (rscFixedMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedData"))));

      TreeMap resourceMap = createResourceMap (rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifierArray();
      Integer id;
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
         resource.setActualCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 132)/100));
         resource.setActualOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 172)/100));
         resource.setActualWork(Duration.getInstance (MPPUtility.getDouble (data, 60)/60000, TimeUnit.HOURS));
         resource.setAvailableFrom(MPPUtility.getTimestamp(data, 20));
         resource.setAvailableTo(MPPUtility.getTimestamp(data, 24));
         //resource.setBaseCalendar();
         resource.setBaselineCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 148)/100));
         resource.setBaselineWork(Duration.getInstance (MPPUtility.getDouble (data, 68)/60000, TimeUnit.HOURS));
         resource.setCode (rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 140)/100));
         resource.setCost1(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST1) / 100));
         resource.setCost2(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST2) / 100));
         resource.setCost3(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST3) / 100));
         resource.setCost4(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST4) / 100));
         resource.setCost5(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST5) / 100));
         resource.setCost6(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST6) / 100));
         resource.setCost7(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST7) / 100));
         resource.setCost8(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST8) / 100));
         resource.setCost9(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST9) / 100));
         resource.setCost10(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST10) / 100));
         resource.setCostPerUse(NumberUtility.getDouble(MPPUtility.getDouble(data, 84)/100));
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
         resource.setDuration1(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION1), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION1_UNITS))));
         resource.setDuration2(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION2), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION2_UNITS))));
         resource.setDuration3(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION3), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION3_UNITS))));
         resource.setDuration4(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION4), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION4_UNITS))));
         resource.setDuration5(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION5), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION5_UNITS))));
         resource.setDuration6(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION6), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION6_UNITS))));
         resource.setDuration7(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION7), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION7_UNITS))));
         resource.setDuration8(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION8), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION8_UNITS))));
         resource.setDuration9(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION9), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION9_UNITS))));
         resource.setDuration10(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION10), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION10_UNITS))));
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
         resource.setMaxUnits(NumberUtility.getDouble(MPPUtility.getDouble(data, 44)/100));
         resource.setName (rscVarData.getUnicodeString (id, RESOURCE_NAME));
         resource.setNumber1(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER1)));
         resource.setNumber2(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER2)));
         resource.setNumber3(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER3)));
         resource.setNumber4(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER4)));
         resource.setNumber5(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER5)));
         resource.setNumber6(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER6)));
         resource.setNumber7(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER7)));
         resource.setNumber8(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER8)));
         resource.setNumber9(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER9)));
         resource.setNumber10(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER10)));
         resource.setNumber11(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER11)));
         resource.setNumber12(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER12)));
         resource.setNumber13(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER13)));
         resource.setNumber14(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER14)));
         resource.setNumber15(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER15)));
         resource.setNumber16(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER16)));
         resource.setNumber17(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER17)));
         resource.setNumber18(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER18)));
         resource.setNumber19(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER19)));
         resource.setNumber20(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER20)));
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
         resource.setOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 164)/100));
         resource.setOvertimeRate(new Rate (MPPUtility.getDouble(data, 36), TimeUnit.HOURS));
         resource.setOvertimeWork(Duration.getInstance (MPPUtility.getDouble (data, 76)/60000, TimeUnit.HOURS));
         resource.setPeakUnits(NumberUtility.getDouble(MPPUtility.getDouble(data, 124)/100));
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(Duration.getInstance (MPPUtility.getDouble (data, 100)/60000, TimeUnit.HOURS));
         resource.setRemainingCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 156)/100));
         resource.setRemainingOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 180)/100));
         resource.setRemainingWork(Duration.getInstance (MPPUtility.getDouble (data, 92)/60000, TimeUnit.HOURS));
         resource.setStandardRate(new Rate (MPPUtility.getDouble(data, 28), TimeUnit.HOURS));
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
         resource.setSubprojectResourceUniqueID(new Integer (rscVarData.getInt(id, RESOURCE_SUBPROJECTRESOURCEID)));
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
         resource.setType((MPPUtility.getShort(data, 14)==0?ResourceType.WORK:ResourceType.MATERIAL));
         resource.setUniqueID(id.intValue());
         resource.setWork(Duration.getInstance (MPPUtility.getDouble (data, 52)/60000, TimeUnit.HOURS));

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
            if (m_reader.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }

            resource.setNotes(notes);
         }

         //
         // Calculate the cost variance
         //
         if (resource.getCost() != null && resource.getBaselineCost() != null)
         {
            resource.setCostVariance(NumberUtility.getDouble(resource.getCost().doubleValue() - resource.getBaselineCost().doubleValue()));
         }

         //
         // Calculate the work variance
         //
         if (resource.getWork() != null && resource.getBaselineWork() != null)
         {
            resource.setWorkVariance(Duration.getInstance (resource.getWork().getDuration() - resource.getBaselineWork().getDuration(), TimeUnit.HOURS));
         }

         //
         // Configure the resource calendar
         //
         resource.setResourceCalendar((ProjectCalendar)resourceCalendarMap.get(id));
         
         file.fireResourceReadEvent(resource);
      }
   }


   /**
    * This method extracts and collates resource assignment data.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXJException
    * @throws IOException
    */
   private void processAssignmentData (ProjectFile file,  DirectoryEntry projectDir)
      throws MPXJException, IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry)projectDir.getEntry ("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data (assnVarMeta, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData (142, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedData"))));      
      Set set = assnVarMeta.getUniqueIdentifierSet();
      int count = assnFixedMeta.getItemCount();
      
      for (int loop=0; loop < count; loop++)
      {
         byte[] meta = assnFixedMeta.getByteArrayValue(loop);
         if (meta[0] != 0)
         {
            continue;
         }

         int offset = MPPUtility.getInt(meta, 4);
         byte[] data = assnFixedData.getByteArrayValue(assnFixedData.getIndexFromOffset(offset));
         if (data == null)
         {
            continue;
         }
         
         int id = MPPUtility.getInt(data, 0);
         final Integer varDataId = new Integer(id);
         if (set.contains(varDataId) == false)
         {
            continue;
         }

         int taskID = MPPUtility.getInt (data, 4);
         Task task = file.getTaskByUniqueID (taskID);
                  
         if (task != null)
         {
            byte[] incompleteWork = assnVarData.getByteArray(assnVarMeta.getOffset(varDataId, INCOMPLETE_WORK));
            byte[] completeWork = assnVarData.getByteArray(assnVarMeta.getOffset(varDataId, COMPLETE_WORK));
            processSplitData(task, completeWork, incompleteWork);
                                    
            int resourceID = MPPUtility.getInt (data, 8);
            Resource resource = file.getResourceByUniqueID (resourceID);
            
            if (resource != null)
            {
               ResourceAssignment assignment = task.addResourceAssignment (resource);
               assignment.setActualCost(NumberUtility.getDouble (MPPUtility.getDouble(data, 110)/100));
               assignment.setActualWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 70))/100, TimeUnit.HOURS));
               assignment.setCost(NumberUtility.getDouble (MPPUtility.getDouble(data, 102)/100));
               assignment.setDelay(MPPUtility.getDuration(MPPUtility.getShort(data, 24), TimeUnit.HOURS));
               assignment.setFinish(MPPUtility.getTimestamp(data, 16));
               //assignment.setOvertimeWork(); // Can't find in data block
               //assignment.setPlannedCost(); // Not sure what this field maps on to in MSP
               //assignment.setPlannedWork(); // Not sure what this field maps on to in MSP
               assignment.setRemainingWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 86))/100, TimeUnit.HOURS));
               assignment.setStart(MPPUtility.getTimestamp(data, 12));
               assignment.setUnits(new Double((MPPUtility.getDouble(data, 54))/100));
               assignment.setWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 62))/100, TimeUnit.HOURS));
               
               if (incompleteWork != null)
               {
                  assignment.setWorkContour(WorkContour.getInstance(MPPUtility.getShort(incompleteWork, 28)));
               }            
            }
         }
      }
   }

   /**
    * The task split data is represented in two blocks of data, one representing
    * the completed time, and the other representing the incomplete time.
    * 
    * The completed task split data is stored in a block with a 32 byte header, 
    * followed by 20 byte blocks, each representing one split. The first two 
    * bytes of the header contains a count of the number of 20 byte blocks.
    * 
    * The incomplete task split data is represented as a 44 byte header 
    * (which also contains unrelated assignment information, such as the 
    * work contour) followed by a list of 28 byte blocks, each block representing 
    * one split. The first two bytes of the header contains a count of the
    * number of 28 byte blocks.
    * 
    * @param task parent task
    * @param completeHours completed split data
    * @param incompleteHours incomplete split data
    */
   private void processSplitData (Task task, byte[] completeHours, byte[] incompleteHours)
   {
      LinkedList splits = new LinkedList ();
      
      if (completeHours != null)
      {
         int splitCount = MPPUtility.getShort(completeHours, 0);         
         if (splitCount != 0)
         {            
            int offset = 32;
            for (int loop=0; loop < splitCount; loop++)
            {
               double splitTime = MPPUtility.getInt(completeHours, offset);
               if (splitTime != 0)
               {
                  splitTime /= 4800;              
                  Duration splitDuration = Duration.getInstance(splitTime, TimeUnit.HOURS);
                  splits.add(splitDuration);
               }
               offset += 20;                  
            }         
            
            double splitTime = MPPUtility.getInt(completeHours, 24);
            splitTime /= 4800;
            Duration splitDuration = Duration.getInstance(splitTime, TimeUnit.HOURS);               
            splits.add(splitDuration);
         }
      }
    
      if (incompleteHours != null)
      {
         int splitCount = MPPUtility.getShort(incompleteHours, 0);
         
         //
         // Deal with the case where the final task split is partially complete
         //
         if (splitCount == 0 && incompleteHours.length >= (44+28))
         {
            splitCount = 1;
         }
         
         if (splitCount != 0)
         {
            double timeOffset = 0;
            if (splits.isEmpty() == false)
            {
               if (splitCount % 2 != 0)
               {
                  timeOffset = ((Duration)splits.removeLast()).getDuration();
               }
               else
               {
                  timeOffset = ((Duration)splits.getLast()).getDuration();
               }               
            }
            
            int offset = 44;
            for (int loop=0; loop < splitCount; loop++)
            {
               double splitTime = MPPUtility.getInt(incompleteHours, offset+24);
               splitTime /= 4800;
               splitTime += timeOffset;
               Duration splitDuration = Duration.getInstance(splitTime, TimeUnit.HOURS);
               splits.add(splitDuration);
               offset += 28;
            }
         }
      }
      
      if (splits.isEmpty() == false)
      {
         task.setSplits(splits);         
      }
   }

   /**
    * This method is used to determine if a duration is estimated.
    *
    * @param type Duration units value
    * @return boolean Estimated flag
    */
   private boolean getDurationEstimated (int type)
   {
      return ((type & DURATION_CONFIRMED_MASK) != 0);
   }

   /**
    * This method extracts view data from the MPP file.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws java.io.IOException
    */
   private void processViewData (ProjectFile file, DirectoryEntry projectDir)
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)projectDir.getEntry ("CV_iew");
      VarMeta viewVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data viewVarData = new Var2Data (viewVarMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
      FixedData ff = new FixedData (122, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData"))));
      int items = ff.getItemCount();
      View view;
      ViewFactory factory = new DefaultViewFactory ();

      //System.out.println(viewVarMeta);
      //System.out.println(viewVarData);

      for (int loop=0; loop < items; loop++)
      {
         view = factory.createView(file, ff.getByteArrayValue(loop), viewVarData, m_fontBases);
         file.addView(view);
      }
   }

   /**
    * This method extracts table data from the MPP file.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws java.io.IOException
    */
   private void processTableData (ProjectFile file, DirectoryEntry projectDir)
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)projectDir.getEntry ("CTable");
      FixedData fixedData = new FixedData (110, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData"))));
      VarMeta varMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));

      int items = fixedData.getItemCount();
      byte[] data;
      Table table;

      for (int loop=0; loop < items; loop++)
      {
         data = fixedData.getByteArrayValue(loop);

         table = new Table ();

         table.setID(MPPUtility.getInt(data, 0));
         table.setResourceFlag(MPPUtility.getShort(data, 108) == 1);
         table.setName(MPPUtility.removeAmpersands(MPPUtility.getUnicodeString(data, 4)));
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
   private void processColumnData (Table table, byte[] data)
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
            column = new Column ();

            if (table.getResourceFlag() == false)
            {
               column.setFieldType (TaskField.getInstance(MPPUtility.getShort(data, index)));
            }
            else
            {
               column.setFieldType (ResourceField.getInstance(MPPUtility.getShort(data, index)));
            }

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

   private MPPReader m_reader;
   private Map m_fontBases = new HashMap();
   private Map m_taskSubProjects = new HashMap ();
   
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
   
   private static final Integer TASK_SUBPROJECT_TASKS_UNIQUEID_OFFSET = new Integer (8);   
   private static final Integer TASK_SUBPROJECTTASKID = new Integer (9);

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

   private static final Integer RESOURCE_SUBPROJECTRESOURCEID = new Integer (102);

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
   private static final Integer INCOMPLETE_WORK = new Integer(7);
   private static final Integer COMPLETE_WORK = new Integer(9);
   
   /**
    * Mask used to isolate confirmed flag from the duration units field.
    */
   private static final int DURATION_CONFIRMED_MASK = 0x20;

   /**
    * Default working week.
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
