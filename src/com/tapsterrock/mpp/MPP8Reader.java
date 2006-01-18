/*
 * file:       MPP8Reader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       08/05/2003
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import com.tapsterrock.mpx.AccrueType;
import com.tapsterrock.mpx.Column;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.DateRange;
import com.tapsterrock.mpx.Day;
import com.tapsterrock.mpx.MPXCalendar;
import com.tapsterrock.mpx.MPXCalendarException;
import com.tapsterrock.mpx.MPXCalendarHours;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXException;
import com.tapsterrock.mpx.ProjectFile;
import com.tapsterrock.mpx.MPXRate;
import com.tapsterrock.mpx.NumberUtility;
import com.tapsterrock.mpx.Pair;
import com.tapsterrock.mpx.Priority;
import com.tapsterrock.mpx.ProjectHeader;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.RelationType;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.ResourceAssignment;
import com.tapsterrock.mpx.ScheduleFrom;
import com.tapsterrock.mpx.Table;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TaskType;
import com.tapsterrock.mpx.TimeUnit;
import com.tapsterrock.mpx.View;

/**
 * This class is used to represent a Microsoft Project MPP8 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP8Reader implements MPPVariantReader
{
   /**
    * This method is used to process an MPP8 file. This is the file format
    * used by Project 98.
    *
    * @param reader parent file reader
    * @param file Parent MPX file
    * @param root Root of the POI file system.
    * @throws MPXException
    * @throws IOException
    */
   public void process (MPPReader reader, ProjectFile file, DirectoryEntry root)
      throws MPXException, IOException
   {
      m_reader = reader;
      
      //
      // Set the file type
      //
      file.setMppFileType(8);
      
      HashMap calendarMap = new HashMap ();

      DirectoryEntry projectDir = (DirectoryEntry)root.getEntry ("   1");

      processPropertyData (file, root, projectDir);

      processCalendarData (file, projectDir, calendarMap);

      processResourceData (file, projectDir, calendarMap);

      processTaskData (file, projectDir);

      processConstraintData (file, projectDir);

      processAssignmentData (file, projectDir);


      projectDir = (DirectoryEntry)root.getEntry ("   2");

      processViewData (file, projectDir);

      processTableData (file, projectDir);
   }


   /**
    * This method extracts and collates global property data.
    *
    * @param file Parent MPX file
    * @param rootDir root direcory of the file
    * @param projectDir Project data directory
    * @throws IOException
    */
   private void processPropertyData (ProjectFile file,  DirectoryEntry rootDir, DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      Props8 props = new Props8 (new DocumentInputStream (((DocumentEntry)projectDir.getEntry("Props"))));

      ProjectHeader ph = file.getProjectHeader();
      ph.setScheduleFrom(ScheduleFrom.getInstance(1-props.getShort(Props.SCHEDULE_FROM)));
      ph.setCalendarName(props.getUnicodeString(Props.DEFAULT_CALENDAR_NAME));
      ph.setDefaultStartTime(props.getTime(Props.START_TIME));
      ph.setDefaultEndTime (props.getTime(Props.END_TIME));


      //ds.setDefaultDurationIsFixed();
      ph.setDefaultDurationUnits(MPPUtility.getDurationTimeUnits(props.getShort(Props.DURATION_UNITS)));
      ph.setDefaultHoursInDay(new Float(((float)props.getInt(Props.HOURS_PER_DAY))/60));
      ph.setDefaultHoursInWeek(new Float(((float)props.getInt(Props.HOURS_PER_WEEK))/60));
      ph.setDefaultOvertimeRate(new MPXRate (props.getDouble(Props.OVERTIME_RATE), TimeUnit.HOURS));
      ph.setDefaultStandardRate(new MPXRate (props.getDouble(Props.STANDARD_RATE), TimeUnit.HOURS));
      ph.setDefaultWorkUnits(MPPUtility.getWorkTimeUnits(props.getShort(Props.WORK_UNITS)));
      ph.setSplitInProgressTasks(props.getBoolean(Props.SPLIT_TASKS));
      ph.setUpdatingTaskStatusUpdatesResourceStatus(props.getBoolean(Props.TASK_UPDATES_RESOURCE));

      ph.setCurrencyDigits(new Integer(props.getShort(Props.CURRENCY_DIGITS)));
      ph.setCurrencySymbol(props.getUnicodeString(Props.CURRENCY_SYMBOL));
      //ph.setDecimalSeparator();
      ph.setSymbolPosition(MPPUtility.getSymbolPosition(props.getShort(Props.CURRENCY_PLACEMENT)));
      //cs.setThousandsSeparator();

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
   }

   /**
    * This method extracts and collates calendar data.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @param calendarMap map of calendar IDs and calendar instances
    * @throws MPXException
    * @throws IOException
    */
   private void processCalendarData (ProjectFile file,  DirectoryEntry projectDir, HashMap calendarMap)
      throws MPXException, IOException
   {
      DirectoryEntry calDir = (DirectoryEntry)projectDir.getEntry ("TBkndCal");
      FixFix calendarFixedData = new FixFix (36, new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixFix   0"))));
      FixDeferFix calendarVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixDeferFix   0"))));

      MPXCalendar cal;
      MPXCalendarHours hours;
      MPXCalendarException exception;
      String name;
      byte[] baseData;
      byte[] extData;

      int periodCount;
      int index;
      int offset;
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

      int calendars = calendarFixedData.getItemCount();
      int calendarID;
      int baseCalendarID;
      int periodIndex;
      Day day;
      List baseCalendars = new LinkedList();
      
      for (int loop=0; loop < calendars; loop++)
      {
         baseData = calendarFixedData.getByteArrayValue(loop);
         calendarID = MPPUtility.getInt(baseData, 0);
         baseCalendarID = MPPUtility.getInt(baseData, 4);
         name = calendarVarData.getUnicodeString(getOffset(baseData, 20));

         //
         // Uncommenting the call to this method is useful when trying
         // to determine the function of unknown task data.
         //
         //dumpUnknownData (name + " " + MPPUtility.getInt(baseData), UNKNOWN_CALENDAR_DATA, baseData);

         //
         // Skip calendars with negative ID values
         //
         if (calendarID < 0)
         {
            continue;
         }

         //
         // Populate the basic calendar
         //
         ExtendedData ed = new ExtendedData (calendarVarData, getOffset(baseData,32));
         offset = -1 - ed.getInt(new Integer (8));

         if (offset == -1)
         {
            if (baseCalendarID > 0)
            {
               cal = file.getDefaultResourceCalendar ();
               baseCalendars.add(new Pair(cal, new Integer(baseCalendarID)));
            }
            else
            {
               cal = file.addDefaultBaseCalendar();
               cal.setName (name);
            }
         }
         else
         {
            if (baseCalendarID > 0)
            {
               cal = file.getResourceCalendar ();
               baseCalendars.add(new Pair(cal, new Integer(baseCalendarID)));
            }
            else
            {
               cal = file.addBaseCalendar();
               cal.setName (name);
            }

            cal.setUniqueID(calendarID);

            extData = calendarVarData.getByteArray(offset);

            for (index=0; index < 7; index++)
            {
               offset = 4 + (40 * index);

               defaultFlag = MPPUtility.getShort (extData, offset);
               day = Day.getInstance(index+1);
               
               if (defaultFlag == 1)
               {                  
                  cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
                  if (cal.isWorkingDay(day) == true)
                  {
                     hours = cal.addCalendarHours(com.tapsterrock.mpx.Day.getInstance(index+1));
                     hours.addDateRange(new DateRange (defaultStart1, defaultEnd1));
                     hours.addDateRange(new DateRange (defaultStart2, defaultEnd2));
                  }
               }
               else
               {
                  periodCount = MPPUtility.getShort (extData, offset+2);
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
                        start = MPPUtility.getTime (extData, offset + 8 + (periodIndex * 2));
                        duration = MPPUtility.getDuration (extData, offset + 16 + (periodIndex * 4));
                        hours.addDateRange(new DateRange (start, new Date (start.getTime()+duration)));
                     }
                  }
               }
            }

            //
            // Handle any exceptions
            //
            exceptionCount = MPPUtility.getShort (extData, 0);
            if (exceptionCount != 0)
            {
               for (index=0; index < exceptionCount; index++)
               {
                  offset = 4 + (40 * 7) + (index * 44);
                  exception = cal.addCalendarException();
                  exception.setFromDate(MPPUtility.getDate (extData, offset));
                  exception.setToDate(MPPUtility.getDate (extData, offset+2));

                  periodCount = MPPUtility.getShort (extData, offset+6);
                  if (periodCount == 0)
                  {
                     exception.setWorking (false);
                  }
                  else
                  {
                     exception.setWorking (true);

                     start = MPPUtility.getTime (extData, offset+12);
                     duration = MPPUtility.getDuration (extData, offset+20);
                     exception.setFromTime1(start);
                     exception.setToTime1(new Date (start.getTime() + duration));

                     if (periodCount > 1)
                     {
                        start = MPPUtility.getTime (extData, offset+14);
                        duration = MPPUtility.getDuration (extData, offset+24);
                        exception.setFromTime2(start);
                        exception.setToTime2(new Date (start.getTime() + duration));

                        if (periodCount > 2)
                        {
                           start = MPPUtility.getTime (extData, offset+16);
                           duration = MPPUtility.getDuration (extData, offset+28);
                           exception.setFromTime3(start);
                           exception.setToTime3(new Date (start.getTime() + duration));
                        }
                     }
                  }
               }
            }
         }

         calendarMap.put(new Integer (calendarID), cal);
      }

      updateBaseCalendarNames (baseCalendars, calendarMap);
   }

   /**
    * The way calendars are stored in an MPP8 file means that there
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
      MPXCalendar cal;
      Integer baseCalendarID;
      MPXCalendar baseCal;
      
      while (iter.hasNext() == true)
      {
         pair = (Pair)iter.next();
         cal = (MPXCalendar)pair.getFirst();
         baseCalendarID = (Integer)pair.getSecond();
         
         baseCal = (MPXCalendar)map.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setBaseCalendar(baseCal);
         }
      }
   }

   /**
    * This method extracts and collates task data.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */
   private void processTaskData (ProjectFile file,  DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry)projectDir.getEntry ("TBkndTask");
      FixFix taskFixedData = new FixFix (316, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixFix   0"))));
      if (taskFixedData.getDiff() != 0)
      {
         taskFixedData = new FixFix (366, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixFix   0"))));
      }

      FixDeferFix taskVarData = null;
      ExtendedData taskExtData = null;

      int tasks = taskFixedData.getItemCount();
      byte[] data;
      int uniqueID;
      int id;
      int deleted;
      Task task;
      String notes;
      RTFUtility rtf = new RTFUtility ();
      byte[] flags = new byte[3];

      for (int loop=0; loop < tasks; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);

         //
         // Test for a valid unique id
         //
         uniqueID = MPPUtility.getInt(data, 0);
         if (uniqueID < 1)
         {
            continue;
         }

         //
         // Test to ensure this task has not been deleted.
         // This appears to be a set of flags rather than a single value.
         // The data I have seen to date shows deleted tasks having values of
         // 0x0001 and 0x0002. Valid tasks have had values of 0x0000, 0x0914,
         // 0x0040 and 0x004A.
         //
         deleted = MPPUtility.getShort(data, 272);
         if ((deleted & 0xC0) == 0 && (deleted & 0x03) != 0)
         {
            continue;
         }

         //
         // Load the var data if we have not already done so
         //
         if (taskVarData == null)
         {
            taskVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixDeferFix   0"))));
         }

         //
         // Blank rows can be present in MPP files. The following flag
         // appears to indicate that a row is blank, and should be
         // ignored.
         //
         if ((data[8] & 0x01)  != 0)
         {
            continue;
         }

         taskExtData = new ExtendedData (taskVarData, getOffset(data, 312));

         id = MPPUtility.getInt (data, 4);
         flags[0] = (byte)(data[268] & data[303]);
         flags[1] = (byte)(data[269] & data[304]);
         flags[2] = (byte)(data[270] & data[305]);

         task = file.addTask();

         task.setActualCost(NumberUtility.getDouble (((double)MPPUtility.getLong6(data, 234)) / 100));
         task.setActualDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 72))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 108));
         task.setActualOvertimeCost(NumberUtility.getDouble (((double)MPPUtility.getLong6(data, 210)) / 100));
         task.setActualOvertimeWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 192))/100, TimeUnit.HOURS));
         task.setActualStart(MPPUtility.getTimestamp (data, 104));
         task.setActualWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 180))/100, TimeUnit.HOURS));
         //task.setACWP(); // Calculated value
         //task.setAssignment(); // Calculated value
         //task.setAssignmentDelay(); // Calculated value
         //task.setAssignmentUnits(); // Calculated value
         task.setBaselineCost(NumberUtility.getDouble ((double)MPPUtility.getLong6 (data, 246) / 100));
         task.setBaselineDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 82), MPPUtility.getDurationTimeUnits (MPPUtility.getShort (data, 72))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 116));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 112));
         task.setBaselineWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 174))/100, TimeUnit.HOURS));
         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate (MPPUtility.getTimestamp (data, 120));
         task.setConstraintType (ConstraintType.getInstance (MPPUtility.getShort (data, 88)));
         task.setContact(taskExtData.getUnicodeString (TASK_CONTACT));
         task.setCost(NumberUtility.getDouble (((double)MPPUtility.getLong6(data, 222)) / 100));
         task.setCost1(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST1)) / 100));
         task.setCost2(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST2)) / 100));
         task.setCost3(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST3)) / 100));
         task.setCost4(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST4)) / 100));
         task.setCost5(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST5)) / 100));
         task.setCost6(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST6)) / 100));
         task.setCost7(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST7)) / 100));
         task.setCost8(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST8)) / 100));
         task.setCost9(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST9)) / 100));
         task.setCost10(NumberUtility.getDouble (((double)taskExtData.getLong (TASK_COST10)) / 100));
         //task.setCostRateTable(); // Calculated value
         //task.setCostVariance(); // Populated below
         task.setCreateDate(MPPUtility.getTimestamp (data, 138));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         task.setDate1(taskExtData.getTimestamp(TASK_DATE1));
         task.setDate2(taskExtData.getTimestamp(TASK_DATE2));
         task.setDate3(taskExtData.getTimestamp(TASK_DATE3));
         task.setDate4(taskExtData.getTimestamp(TASK_DATE4));
         task.setDate5(taskExtData.getTimestamp(TASK_DATE5));
         task.setDate6(taskExtData.getTimestamp(TASK_DATE6));
         task.setDate7(taskExtData.getTimestamp(TASK_DATE7));
         task.setDate8(taskExtData.getTimestamp(TASK_DATE8));
         task.setDate9(taskExtData.getTimestamp(TASK_DATE9));
         task.setDate10(taskExtData.getTimestamp(TASK_DATE10));
         //task.setDelay(); // No longer supported by MS Project?
         task.setDuration (MPPUtility.getAdjustedDuration (file, MPPUtility.getInt (data, 68), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 72))));
         task.setDuration1(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION1), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION1_UNITS))));
         task.setDuration2(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION2), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION2_UNITS))));
         task.setDuration3(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION3), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION3_UNITS))));
         task.setDuration4(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION4), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION4_UNITS))));
         task.setDuration5(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION5), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION5_UNITS))));
         task.setDuration6(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION6), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION6_UNITS))));
         task.setDuration7(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION7), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION7_UNITS))));
         task.setDuration8(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION8), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION8_UNITS))));
         task.setDuration9(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION9), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION9_UNITS))));
         task.setDuration10(MPPUtility.getAdjustedDuration (file, taskExtData.getInt (TASK_DURATION10), MPPUtility.getDurationTimeUnits(taskExtData.getShort (TASK_DURATION10_UNITS))));
         //task.setDurationVariance(); // Calculated value
         task.setEarlyFinish (MPPUtility.getTimestamp (data, 20));
         task.setEarlyStart (MPPUtility.getTimestamp (data, 96));
         task.setEffortDriven((data[17] & 0x08) != 0);
         //task.setExternalTask(); // Calculated value
         task.setFinish (MPPUtility.getTimestamp (data, 20));
         task.setFinish1(taskExtData.getTimestamp(TASK_FINISH1));
         task.setFinish2(taskExtData.getTimestamp(TASK_FINISH2));
         task.setFinish3(taskExtData.getTimestamp(TASK_FINISH3));
         task.setFinish4(taskExtData.getTimestamp(TASK_FINISH4));
         task.setFinish5(taskExtData.getTimestamp(TASK_FINISH5));
         task.setFinish6(taskExtData.getTimestamp(TASK_FINISH6));
         task.setFinish7(taskExtData.getTimestamp(TASK_FINISH7));
         task.setFinish8(taskExtData.getTimestamp(TASK_FINISH8));
         task.setFinish9(taskExtData.getTimestamp(TASK_FINISH9));
         task.setFinish10(taskExtData.getTimestamp(TASK_FINISH10));
         //task.setFinishVariance(); // Calculated value
         task.setFixedCost(NumberUtility.getDouble (((double)MPPUtility.getLong6(data, 228)) / 100));
         task.setFixedCostAccrual(AccrueType.getInstance (MPPUtility.getShort (data, 136)));
         task.setFlag1((flags[0] & 0x02) != 0);
         task.setFlag2((flags[0] & 0x04) != 0);
         task.setFlag3((flags[0] & 0x08) != 0);
         task.setFlag4((flags[0] & 0x10) != 0);
         task.setFlag5((flags[0] & 0x20) != 0);
         task.setFlag6((flags[0] & 0x40) != 0);
         task.setFlag7((flags[0] & 0x80) != 0);
         task.setFlag8((flags[1] & 0x01) != 0);
         task.setFlag9((flags[1] & 0x02) != 0);
         task.setFlag10((flags[1] & 0x04) != 0);
         task.setFlag11((flags[1] & 0x08) != 0);
         task.setFlag12((flags[1] & 0x10) != 0);
         task.setFlag13((flags[1] & 0x20) != 0);
         task.setFlag14((flags[1] & 0x40) != 0);
         task.setFlag15((flags[1] & 0x80) != 0);
         task.setFlag16((flags[2] & 0x01) != 0);
         task.setFlag17((flags[2] & 0x02) != 0);
         task.setFlag18((flags[2] & 0x04) != 0);
         task.setFlag19((flags[2] & 0x08) != 0);
         task.setFlag20((flags[2] & 0x10) != 0); // note that this is not correct
         //task.setFreeSlack();  // Calculated value
         task.setHideBar((data[16] & 0x01) != 0);
         processHyperlinkData (task, taskVarData.getByteArray(-1 - taskExtData.getInt(TASK_HYPERLINK)));
         task.setID (id);
         //task.setIndicators(); // Calculated value
         task.setLateFinish (MPPUtility.getTimestamp (data, 160));
         task.setLateStart (MPPUtility.getTimestamp (data, 24));
         task.setLevelAssignments((data[19] & 0x10) != 0);
         task.setLevelingCanSplit((data[19] & 0x08) != 0);
         task.setLevelingDelay (MPPUtility.getDuration (((double)MPPUtility.getInt (data, 90))/3, MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 94))));
         //task.setLinkedFields();  // Calculated value
         task.setMarked((data[13] & 0x02) != 0);
         task.setMilestone((data[12] & 0x01) != 0);
         task.setName(taskVarData.getUnicodeString (getOffset(data, 264)));
         task.setNumber1(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER1)));
         task.setNumber2(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER2)));
         task.setNumber3(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER3)));
         task.setNumber4(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER4)));
         task.setNumber5(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER5)));
         task.setNumber6(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER6)));
         task.setNumber7(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER7)));
         task.setNumber8(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER8)));
         task.setNumber9(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER9)));
         task.setNumber10(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER10)));
         task.setNumber11(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER11)));
         task.setNumber12(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER12)));
         task.setNumber13(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER13)));
         task.setNumber14(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER14)));
         task.setNumber15(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER15)));
         task.setNumber16(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER16)));
         task.setNumber17(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER17)));
         task.setNumber18(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER18)));
         task.setNumber19(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER19)));
         task.setNumber20(NumberUtility.getDouble (taskExtData.getDouble(TASK_NUMBER20)));
         //task.setObjects(); // Calculated value
         task.setOutlineLevel (MPPUtility.getShort (data, 48));
         //task.setOutlineNumber(); // Calculated value
         //task.setOverallocated(); // Calculated value
         task.setOvertimeCost (NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 204))/100));
         //task.setOvertimeWork(); // Calculated value
         //task.getPredecessors(); // Calculated value
         task.setPercentageComplete(NumberUtility.getDouble(MPPUtility.getShort(data, 130)));
         task.setPercentageWorkComplete(NumberUtility.getDouble(MPPUtility.getShort(data, 132)));
         task.setPreleveledFinish (MPPUtility.getTimestamp(data, 148));
         task.setPreleveledStart (MPPUtility.getTimestamp(data, 144));
         task.setPriority(Priority.getInstance((MPPUtility.getShort (data, 128)+1)*100));
         //task.setProject(); // Calculated value
         //task.setRecurring(); // Calculated value
         //task.setRegularWork(); // Calculated value
         task.setRemainingCost(NumberUtility.getDouble (((double)MPPUtility.getLong6(data, 240)) / 100));
         task.setRemainingDuration (MPPUtility.getDuration (MPPUtility.getInt (data, 78), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 72))));
         task.setRemainingOvertimeCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 216))/100));
         task.setRemainingOvertimeWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 198))/100, TimeUnit.HOURS));
         task.setRemainingWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 186))/100, TimeUnit.HOURS));
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         //task.setResourcePhonetics(); // Calculated value from resource
         //task.setResponsePending(); // Calculated value
         task.setResume(MPPUtility.getTimestamp(data, 32));
         //task.setResumeNoEarlierThan(); // Not in MSP98?
         task.setRollup((data[15] & 0x04) != 0);
         task.setStart (MPPUtility.getTimestamp (data, 96));
         task.setStart1(taskExtData.getTimestamp(TASK_START1));
         task.setStart2(taskExtData.getTimestamp(TASK_START2));
         task.setStart3(taskExtData.getTimestamp(TASK_START3));
         task.setStart4(taskExtData.getTimestamp(TASK_START4));
         task.setStart5(taskExtData.getTimestamp(TASK_START5));
         task.setStart6(taskExtData.getTimestamp(TASK_START6));
         task.setStart7(taskExtData.getTimestamp(TASK_START7));
         task.setStart8(taskExtData.getTimestamp(TASK_START8));
         task.setStart9(taskExtData.getTimestamp(TASK_START9));
         task.setStart10(taskExtData.getTimestamp(TASK_START10));
         //task.setStartVariance(); // Calculated value
         task.setStop(MPPUtility.getTimestamp (data, 124));
         //task.setSubprojectFile();
         //task.setSubprojectReadOnly();
         //task.setSuccessors(); // Calculated value
         //task.setSummary(); // Automatically generated by MPXJ
         //task.setSV(); // Calculated value
         //task.teamStatusPending(); // Calculated value
         task.setText1(taskExtData.getUnicodeString(TASK_TEXT1));
         task.setText2(taskExtData.getUnicodeString(TASK_TEXT2));
         task.setText3(taskExtData.getUnicodeString(TASK_TEXT3));
         task.setText4(taskExtData.getUnicodeString(TASK_TEXT4));
         task.setText5(taskExtData.getUnicodeString(TASK_TEXT5));
         task.setText6(taskExtData.getUnicodeString(TASK_TEXT6));
         task.setText7(taskExtData.getUnicodeString(TASK_TEXT7));
         task.setText8(taskExtData.getUnicodeString(TASK_TEXT8));
         task.setText9(taskExtData.getUnicodeString(TASK_TEXT9));
         task.setText10(taskExtData.getUnicodeString(TASK_TEXT10));
         task.setText11(taskExtData.getUnicodeString(TASK_TEXT11));
         task.setText12(taskExtData.getUnicodeString(TASK_TEXT12));
         task.setText13(taskExtData.getUnicodeString(TASK_TEXT13));
         task.setText14(taskExtData.getUnicodeString(TASK_TEXT14));
         task.setText15(taskExtData.getUnicodeString(TASK_TEXT15));
         task.setText16(taskExtData.getUnicodeString(TASK_TEXT16));
         task.setText17(taskExtData.getUnicodeString(TASK_TEXT17));
         task.setText18(taskExtData.getUnicodeString(TASK_TEXT18));
         task.setText19(taskExtData.getUnicodeString(TASK_TEXT19));
         task.setText20(taskExtData.getUnicodeString(TASK_TEXT20));
         task.setText21(taskExtData.getUnicodeString(TASK_TEXT21));
         task.setText22(taskExtData.getUnicodeString(TASK_TEXT22));
         task.setText23(taskExtData.getUnicodeString(TASK_TEXT23));
         task.setText24(taskExtData.getUnicodeString(TASK_TEXT24));
         task.setText25(taskExtData.getUnicodeString(TASK_TEXT25));
         task.setText26(taskExtData.getUnicodeString(TASK_TEXT26));
         task.setText27(taskExtData.getUnicodeString(TASK_TEXT27));
         task.setText28(taskExtData.getUnicodeString(TASK_TEXT28));
         task.setText29(taskExtData.getUnicodeString(TASK_TEXT29));
         task.setText30(taskExtData.getUnicodeString(TASK_TEXT30));
         //task.setTotalSlack(); // Calculated value
         task.setType(TaskType.getInstance(MPPUtility.getShort(data, 134)));
         task.setUniqueID(uniqueID);
         //task.setUniqueIDPredecessors(); // Calculated value
         //task.setUniqueIDSuccessors(); // Calculated value
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskExtData.getUnicodeString (TASK_WBS));
         task.setWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 168))/100, TimeUnit.HOURS));
         //task.setWorkContour(); // Calculated from resource
         //task.setWorkVariance(); // Calculated value

         //
         // Set the MPX file fixed flag
         //
         task.setFixed(task.getType() == TaskType.FIXED_DURATION);
         
         //
         // Retrieve the task notes.
         //
         notes = taskExtData.getString (TASK_NOTES);
         if (notes != null)
         {
            if (m_reader.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip (notes);
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
         
         file.fireTaskReadEvent(task);
         
         //
         // Uncommenting the call to this method is useful when trying
         // to determine the function of unknown task data.
         //
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
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws IOException
    */
   private void processConstraintData (ProjectFile file, DirectoryEntry projectDir)
      throws IOException
   {
      //
      // Locate the directory containing the constraints
      //
      DirectoryEntry consDir;

      try
      {
         consDir = (DirectoryEntry)projectDir.getEntry ("TBkndCons");
      }

      catch (FileNotFoundException ex)
      {
         consDir = null;
      }

      //
      // It appears possible that valid MPP8 files can be generated without
      // this directory, so only process constraints if the directory
      // exists.
      //
      if (consDir != null)
      {
         FixFix consFixedData = new FixFix (36, new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixFix   0"))));

         int count = consFixedData.getItemCount();
         byte[] data;
         Task task1;
         Task task2;
         Relation rel;
         TimeUnit durationUnits;
         int taskID1;
         int taskID2;

         for (int loop=0; loop < count; loop++)
         {
            data = consFixedData.getByteArrayValue(loop);

            if (MPPUtility.getInt(data, 28) == 0)
            {
               taskID1 = MPPUtility.getInt (data, 12);
               taskID2 = MPPUtility.getInt (data, 16);

               if (taskID1 != taskID2)
               {
                  task1 = file.getTaskByUniqueID (taskID1);
                  task2 = file.getTaskByUniqueID (taskID2);
                  if (task1 != null && task2 != null)
                  {
                     rel = task2.addPredecessor(task1);
                     rel.setType (RelationType.getInstance(MPPUtility.getShort(data, 20)));
                     durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 22));
                     rel.setDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 24), durationUnits));
                  }
               }
            }
         }
      }
   }


   /**
    * This method extracts and collates resource data.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @param calendarMap map of calendar IDs and calendar instances
    * @throws MPXException
    * @throws IOException
    */
   private void processResourceData (ProjectFile file, DirectoryEntry projectDir, HashMap calendarMap)
      throws MPXException, IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry)projectDir.getEntry ("TBkndRsc");
      FixFix rscFixedData = new FixFix (196, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixFix   0"))));
      FixDeferFix rscVarData = null;
      ExtendedData rscExtData = null;

      int resources = rscFixedData.getItemCount();
      byte[] data;
      int id;
      Resource resource;
      String notes;
      RTFUtility rtf = new RTFUtility ();
      MPXCalendar calendar;

      for (int loop=0; loop < resources; loop++)
      {
         data = rscFixedData.getByteArrayValue(loop);

         //
         // Test for a valid unique id
         //
         id = MPPUtility.getInt(data, 0);
         if (id < 1)
         {
            continue;
         }

         //
         // Blank rows can be present in MPP files. The following flag
         // appears to indicate that a row is blank, and should be
         // ignored.
         //
         if ((data[8] & 0x01)  != 0)
         {
            continue;
         }

         //
         // Test to ensure this resource has not been deleted
         // This may be an array of bit flags, as per the task
         // record. I have yet to see data to support this, so
         // the simple non-zero test remains.
         //
         if (MPPUtility.getShort(data, 164) != 0)
         {
            continue;
         }

         //
         // Load the var data if we have not already done so
         //
         if (rscVarData == null)
         {
            rscVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixDeferFix   0"))));
         }

         rscExtData = new ExtendedData (rscVarData, getOffset(data, 192));

         resource = file.addResource();

         resource.setAccrueAt(AccrueType.getInstance (MPPUtility.getShort (data, 20)));
         resource.setActualCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 114))/100));
         resource.setActualOvertimeCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 144))/100));
         resource.setActualWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 62))/100, TimeUnit.HOURS));
         resource.setAvailableFrom(MPPUtility.getTimestamp(data, 28));
         resource.setAvailableTo(MPPUtility.getTimestamp(data, 32));
         //resource.setBaseCalendar();
         resource.setBaselineCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 126))/100));
         resource.setBaselineWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 68))/100, TimeUnit.HOURS));
         resource.setCode (rscExtData.getUnicodeString (RESOURCE_CODE));
         resource.setCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 120))/100));
         resource.setCost1(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST1)) / 100));
         resource.setCost2(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST2)) / 100));
         resource.setCost3(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST3)) / 100));
         resource.setCost4(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST4)) / 100));
         resource.setCost5(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST5)) / 100));
         resource.setCost6(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST6)) / 100));
         resource.setCost7(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST7)) / 100));
         resource.setCost8(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST8)) / 100));
         resource.setCost9(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST9)) / 100));
         resource.setCost10(NumberUtility.getDouble (((double)rscExtData.getLong (RESOURCE_COST10)) / 100));
         resource.setCostPerUse(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 80))/100));
         resource.setDate1(rscExtData.getTimestamp (RESOURCE_DATE1));
         resource.setDate2(rscExtData.getTimestamp (RESOURCE_DATE2));
         resource.setDate3(rscExtData.getTimestamp (RESOURCE_DATE3));
         resource.setDate4(rscExtData.getTimestamp (RESOURCE_DATE4));
         resource.setDate5(rscExtData.getTimestamp (RESOURCE_DATE5));
         resource.setDate6(rscExtData.getTimestamp (RESOURCE_DATE6));
         resource.setDate7(rscExtData.getTimestamp (RESOURCE_DATE7));
         resource.setDate8(rscExtData.getTimestamp (RESOURCE_DATE8));
         resource.setDate9(rscExtData.getTimestamp (RESOURCE_DATE9));
         resource.setDate10(rscExtData.getTimestamp (RESOURCE_DATE10));
         resource.setDuration1(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION1), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION1_UNITS))));
         resource.setDuration2(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION2), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION2_UNITS))));
         resource.setDuration3(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION3), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION3_UNITS))));
         resource.setDuration4(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION4), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION4_UNITS))));
         resource.setDuration5(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION5), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION5_UNITS))));
         resource.setDuration6(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION6), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION6_UNITS))));
         resource.setDuration7(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION7), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION7_UNITS))));
         resource.setDuration8(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION8), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION8_UNITS))));
         resource.setDuration9(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION9), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION9_UNITS))));
         resource.setDuration10(MPPUtility.getDuration (rscExtData.getInt (RESOURCE_DURATION10), MPPUtility.getDurationTimeUnits(rscExtData.getShort (RESOURCE_DURATION10_UNITS))));
         resource.setEmailAddress(rscExtData.getUnicodeString (RESOURCE_EMAIL));
         resource.setFinish1(rscExtData.getTimestamp (RESOURCE_FINISH1));
         resource.setFinish2(rscExtData.getTimestamp (RESOURCE_FINISH2));
         resource.setFinish3(rscExtData.getTimestamp (RESOURCE_FINISH3));
         resource.setFinish4(rscExtData.getTimestamp (RESOURCE_FINISH4));
         resource.setFinish5(rscExtData.getTimestamp (RESOURCE_FINISH5));
         resource.setFinish6(rscExtData.getTimestamp (RESOURCE_FINISH6));
         resource.setFinish7(rscExtData.getTimestamp (RESOURCE_FINISH7));
         resource.setFinish8(rscExtData.getTimestamp (RESOURCE_FINISH8));
         resource.setFinish9(rscExtData.getTimestamp (RESOURCE_FINISH9));
         resource.setFinish10(rscExtData.getTimestamp (RESOURCE_FINISH10));
         resource.setGroup(rscExtData.getUnicodeString (RESOURCE_GROUP));
         resource.setID (MPPUtility.getInt (data, 4));
         resource.setInitials (rscVarData.getUnicodeString(getOffset (data, 160)));
         //resource.setLinkedFields(); // Calculated value
         resource.setMaxUnits(NumberUtility.getDouble(((double)MPPUtility.getInt(data, 52))/100));
         resource.setName (rscVarData.getUnicodeString(getOffset (data, 156)));
         resource.setNumber1(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER1)));
         resource.setNumber2(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER2)));
         resource.setNumber3(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER3)));
         resource.setNumber4(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER4)));
         resource.setNumber5(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER5)));
         resource.setNumber6(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER6)));
         resource.setNumber7(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER7)));
         resource.setNumber8(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER8)));
         resource.setNumber9(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER9)));
         resource.setNumber10(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER10)));
         resource.setNumber11(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER11)));
         resource.setNumber12(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER12)));
         resource.setNumber13(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER13)));
         resource.setNumber14(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER14)));
         resource.setNumber15(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER15)));
         resource.setNumber16(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER16)));
         resource.setNumber17(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER17)));
         resource.setNumber18(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER18)));
         resource.setNumber19(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER19)));
         resource.setNumber20(NumberUtility.getDouble (rscExtData.getDouble(RESOURCE_NUMBER20)));
         //resource.setObjects(); // Calculated value
         //resource.setOverallocated(); // Calculated value
         resource.setOvertimeCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 138))/100));
         resource.setOvertimeRate(new MPXRate (MPPUtility.getDouble(data, 44), TimeUnit.HOURS));
         resource.setOvertimeWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 74))/100, TimeUnit.HOURS));
         resource.setPeakUnits(NumberUtility.getDouble(((double)MPPUtility.getInt(data, 110))/100));
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 92))/100, TimeUnit.HOURS));
         resource.setRemainingCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 132))/100));
         resource.setRemainingOvertimeCost(NumberUtility.getDouble(((double)MPPUtility.getLong6(data, 150))/100));
         resource.setRemainingWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 86))/100, TimeUnit.HOURS));
         resource.setStandardRate(new MPXRate (MPPUtility.getDouble(data, 36), TimeUnit.HOURS));
         resource.setStart1(rscExtData.getTimestamp (RESOURCE_START1));
         resource.setStart2(rscExtData.getTimestamp (RESOURCE_START2));
         resource.setStart3(rscExtData.getTimestamp (RESOURCE_START3));
         resource.setStart4(rscExtData.getTimestamp (RESOURCE_START4));
         resource.setStart5(rscExtData.getTimestamp (RESOURCE_START5));
         resource.setStart6(rscExtData.getTimestamp (RESOURCE_START6));
         resource.setStart7(rscExtData.getTimestamp (RESOURCE_START7));
         resource.setStart8(rscExtData.getTimestamp (RESOURCE_START8));
         resource.setStart9(rscExtData.getTimestamp (RESOURCE_START9));
         resource.setStart10(rscExtData.getTimestamp (RESOURCE_START10));
         resource.setText1(rscExtData.getUnicodeString (RESOURCE_TEXT1));
         resource.setText2(rscExtData.getUnicodeString (RESOURCE_TEXT2));
         resource.setText3(rscExtData.getUnicodeString (RESOURCE_TEXT3));
         resource.setText4(rscExtData.getUnicodeString (RESOURCE_TEXT4));
         resource.setText5(rscExtData.getUnicodeString (RESOURCE_TEXT5));
         resource.setText6(rscExtData.getUnicodeString (RESOURCE_TEXT6));
         resource.setText7(rscExtData.getUnicodeString (RESOURCE_TEXT7));
         resource.setText8(rscExtData.getUnicodeString (RESOURCE_TEXT8));
         resource.setText9(rscExtData.getUnicodeString (RESOURCE_TEXT9));
         resource.setText10(rscExtData.getUnicodeString (RESOURCE_TEXT10));
         resource.setText11(rscExtData.getUnicodeString (RESOURCE_TEXT11));
         resource.setText12(rscExtData.getUnicodeString (RESOURCE_TEXT12));
         resource.setText13(rscExtData.getUnicodeString (RESOURCE_TEXT13));
         resource.setText14(rscExtData.getUnicodeString (RESOURCE_TEXT14));
         resource.setText15(rscExtData.getUnicodeString (RESOURCE_TEXT15));
         resource.setText16(rscExtData.getUnicodeString (RESOURCE_TEXT16));
         resource.setText17(rscExtData.getUnicodeString (RESOURCE_TEXT17));
         resource.setText18(rscExtData.getUnicodeString (RESOURCE_TEXT18));
         resource.setText19(rscExtData.getUnicodeString (RESOURCE_TEXT19));
         resource.setText20(rscExtData.getUnicodeString (RESOURCE_TEXT20));
         resource.setText21(rscExtData.getUnicodeString (RESOURCE_TEXT21));
         resource.setText22(rscExtData.getUnicodeString (RESOURCE_TEXT22));
         resource.setText23(rscExtData.getUnicodeString (RESOURCE_TEXT23));
         resource.setText24(rscExtData.getUnicodeString (RESOURCE_TEXT24));
         resource.setText25(rscExtData.getUnicodeString (RESOURCE_TEXT25));
         resource.setText26(rscExtData.getUnicodeString (RESOURCE_TEXT26));
         resource.setText27(rscExtData.getUnicodeString (RESOURCE_TEXT27));
         resource.setText28(rscExtData.getUnicodeString (RESOURCE_TEXT28));
         resource.setText29(rscExtData.getUnicodeString (RESOURCE_TEXT29));
         resource.setText30(rscExtData.getUnicodeString (RESOURCE_TEXT30));
         resource.setUniqueID(id);
         resource.setWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 56))/100, TimeUnit.HOURS));

         //
         // Attach the resource calendar
         //
         calendar = (MPXCalendar)calendarMap.get(new Integer (MPPUtility.getInt(data, 24)));
         resource.setResourceCalendar(calendar);
         
         //
         // Retrieve the resource notes.
         //
         notes = rscExtData.getString (RESOURCE_NOTES);
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
            resource.setWorkVariance(MPXDuration.getInstance (resource.getWork().getDuration() - resource.getBaselineWork().getDuration(), TimeUnit.HOURS));
         }
         
         file.fireResourceReadEvent(resource);
      }
   }


   /**
    * This method extracts and collates resource assignment data.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws MPXException
    * @throws IOException
    */
   private void processAssignmentData (ProjectFile file, DirectoryEntry projectDir)
      throws MPXException, IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry)projectDir.getEntry ("TBkndAssn");
      FixFix assnFixedData = new FixFix (204, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixFix   0"))));
      if (assnFixedData.getDiff() != 0 || (assnFixedData.getSize() % 238 == 0 && testAssignmentTasks(file, assnFixedData) == false))
      {
         assnFixedData = new FixFix (238, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixFix   0"))));
      }

      ResourceAssignment assignment;

      int count = assnFixedData.getItemCount();
      byte[] data;
      Task task;
      Resource resource;
      FixDeferFix assnVarData = null;

      for (int loop=0; loop < count; loop++)
      {
         if (assnVarData == null)
         {
            assnVarData = new FixDeferFix (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixDeferFix   0"))));
         }

         data = assnFixedData.getByteArrayValue(loop);

         task = file.getTaskByUniqueID (MPPUtility.getInt (data, 16));
         resource = file.getResourceByUniqueID (MPPUtility.getInt (data, 20));
         if (task != null && resource != null)
         {
            assignment = task.addResourceAssignment (resource);
            assignment.setActualCost(NumberUtility.getDouble (MPPUtility.getLong6(data, 138)/100));
            assignment.setActualWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 96))/100, TimeUnit.HOURS));
            assignment.setCost(NumberUtility.getDouble (MPPUtility.getLong6(data, 132)/100));
            //assignment.setDelay(); // Not sure what this field maps on to in MSP
            assignment.setFinish(MPPUtility.getTimestamp(data, 28));
            assignment.setOvertimeWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 90))/100, TimeUnit.HOURS));
            //assignment.setPlannedCost(); // Not sure what this field maps on to in MSP
            //assignment.setPlannedWork(); // Not sure what this field maps on to in MSP
            assignment.setRemainingWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 114))/100, TimeUnit.HOURS));
            assignment.setStart(MPPUtility.getTimestamp(data, 24));
            assignment.setUnits(new Double(((double)MPPUtility.getShort(data, 80))/100));
            assignment.setWork(MPPUtility.getDuration(((double)MPPUtility.getLong6(data, 84))/100, TimeUnit.HOURS));

            //
            // Uncommenting the call to this method is useful when trying
            // to determine the function of unknown assignment data.
            //
            //dumpUnknownData (task.getName() + " " + resource.getName(), UNKNOWN_ASSIGNMENT_DATA, data);
         }
      }
   }

   /**
    * It appears that its is possible for task assignment data blocks to be
    * one of two sizes, 204 or 238 bytes. In most cases, simply dividing the
    * overall block size by these values will determine which of these is
    * the one to use, i.e. the one that returns a remainder of zero.
    *
    * Unfortunately it is possible that an overall block size will appear which
    * can be divided exactly by both of these values. In this case we call this
    * method to perform a "rule of thumb" test to determine if the selected
    * block size is correct. From observation it appears that assignment data
    * will always have a valid resource or task associated with it. If both
    * values are invalid, then we assume that we are not using the correct
    * block size.
    *
    * As stated above, this is a "rule of thumb" test, and it is quite likely
    * that we will encounter cases which incorrectly fail this test. We'll
    * just have to keep looking for a better way to determine the correct
    * block size!
    *
    * @param file Parent MPP file
    * @param assnFixedData Task assignment fixed data
    * @return boolean flag
    */
   private boolean testAssignmentTasks (ProjectFile file, FixFix assnFixedData)
   {
      boolean result = true;
      int count = assnFixedData.getItemCount();
      byte[] data;
      Task task;
      Resource resource;

      for (int loop=0; loop < count; loop++)
      {
         data = assnFixedData.getByteArrayValue(loop);
         task = file.getTaskByUniqueID (MPPUtility.getInt (data, 16));
         resource = file.getResourceByUniqueID (MPPUtility.getInt (data, 20));

         if (task == null && resource == null)
         {
            result = false;
            break;
         }
      }

      return (result);
   }

   /**
    * This method extracts view data from the MPP file.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws IOException
    */
   private void processViewData (ProjectFile file, DirectoryEntry projectDir)
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)projectDir.getEntry ("CV_iew");
      FixFix ff = new FixFix (138, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixFix   0"))));
      int items = ff.getItemCount();
      byte[] data;
      View view;

      for (int loop=0; loop < items; loop++)
      {
         data = ff.getByteArrayValue(loop);
         view = new View8 (data);
         file.addView(view);
      }
   }

   /**
    * This method extracts table data from the MPP file.
    *
    * @param file Parent MPX file
    * @param projectDir Project data directory
    * @throws IOException
    */
   private void processTableData (ProjectFile file, DirectoryEntry projectDir)
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)projectDir.getEntry ("CTable");
      FixFix ff = new FixFix (126, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixFix   0"))));
      FixDeferFix fdf = new FixDeferFix (new DocumentInputStream (((DocumentEntry)dir.getEntry("FixDeferFix   0"))));
      int items = ff.getItemCount();
      byte[] data;
      byte[] extendedData;
      byte[] columnData;
      Table table;
      String name;
      StringBuffer sb = new StringBuffer();

      for (int loop=0; loop < items; loop++)
      {
         data = ff.getByteArrayValue(loop);
         table = new Table ();

         table.setID(MPPUtility.getInt(data, 0));

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

         table.setName(MPPUtility.removeAmpersands(name));
         file.addTable(table);

         extendedData = fdf.getByteArray(getOffset(data, 122));
         if (extendedData != null)
         {
            columnData = fdf.getByteArray(getOffset(extendedData, 8));
            processColumnData (table, columnData);
         }
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
      int columnCount = MPPUtility.getShort(data, 4)+1;
      int index = 8;
      int columnTitleOffset;
      Column  column;
      int alignment;
      
      for (int loop=0; loop < columnCount; loop++)
      {
         column = new Column ();

         if (loop==0)
         {
            if (MPPUtility.getShort(data, index) == ResourceField.ID_VALUE)
            {
               table.setResourceFlag(true);
            }
            else
            {
               table.setResourceFlag(false);
            }
         }
         
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

   /**
    * This method is used to extract a value from a fixed data block,
    * which represents an offset into a variable data block.
    *
    * @param data Fixed data block
    * @param offset Offset in fixed data block
    * @return Offset in var data block
    */
   private int getOffset (byte[] data, int offset)
   {
      return (-1 - MPPUtility.getInt(data, offset));
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
//      {8, 12}, // includes known flags
//      {36, 12},
//      {50, 18},
//      {86, 2},
//      {142, 2},
//      {144, 4},
//      {148, 4},
//      {152, 4},
//      {164, 4},
//      {268, 4}, // includes known flags
//      {274, 32}, // includes known flags
//      {306, 6}
//   };
//
//   private static final int[][] UNKNOWN_CALENDAR_DATA = new int[][]
//   {
//      {8, 12},
//      {24, 8}
//   };
//
//   private static final int[][] UNKNOWN_ASSIGNMENT_DATA = new int[][]
//   {
//     {4, 12},
//     {32, 79},
//     {82, 2},
//     {102, 6},
//     {108, 6},
//     {120, 12},
//     {102, 6},
//     {144, 12},
//     {162, 42}
//   };

   private MPPReader m_reader;
   
   /**
    * Task data types.
    */
   private static final Integer TASK_WBS = new Integer (104);
   private static final Integer TASK_CONTACT = new Integer (105);

   private static final Integer TASK_TEXT1 = new Integer (106);
   private static final Integer TASK_TEXT2 = new Integer (107);
   private static final Integer TASK_TEXT3 = new Integer (108);
   private static final Integer TASK_TEXT4 = new Integer (109);
   private static final Integer TASK_TEXT5 = new Integer (110);
   private static final Integer TASK_TEXT6 = new Integer (111);
   private static final Integer TASK_TEXT7 = new Integer (112);
   private static final Integer TASK_TEXT8 = new Integer (113);
   private static final Integer TASK_TEXT9 = new Integer (114);
   private static final Integer TASK_TEXT10 = new Integer (115);

   private static final Integer TASK_START1 = new Integer (116);
   private static final Integer TASK_FINISH1 = new Integer (117);
   private static final Integer TASK_START2 = new Integer (118);
   private static final Integer TASK_FINISH2 = new Integer (119);
   private static final Integer TASK_START3 = new Integer (120);
   private static final Integer TASK_FINISH3 = new Integer (121);
   private static final Integer TASK_START4 = new Integer (122);
   private static final Integer TASK_FINISH4 = new Integer (123);
   private static final Integer TASK_START5 = new Integer (124);
   private static final Integer TASK_FINISH5 = new Integer (125);
   private static final Integer TASK_START6 = new Integer (126);
   private static final Integer TASK_FINISH6 = new Integer (127);
   private static final Integer TASK_START7 = new Integer (128);
   private static final Integer TASK_FINISH7 = new Integer (129);
   private static final Integer TASK_START8 = new Integer (130);
   private static final Integer TASK_FINISH8 = new Integer (131);
   private static final Integer TASK_START9 = new Integer (132);
   private static final Integer TASK_FINISH9 = new Integer (133);
   private static final Integer TASK_START10 = new Integer (134);
   private static final Integer TASK_FINISH10 = new Integer (135);

   private static final Integer TASK_NUMBER1 = new Integer (137);
   private static final Integer TASK_NUMBER2 = new Integer (138);
   private static final Integer TASK_NUMBER3 = new Integer (139);
   private static final Integer TASK_NUMBER4 = new Integer (140);
   private static final Integer TASK_NUMBER5 = new Integer (141);
   private static final Integer TASK_NUMBER6 = new Integer (142);
   private static final Integer TASK_NUMBER7 = new Integer (143);
   private static final Integer TASK_NUMBER8 = new Integer (144);
   private static final Integer TASK_NUMBER9 = new Integer (145);
   private static final Integer TASK_NUMBER10 = new Integer (146);

   private static final Integer TASK_DURATION1 = new Integer (147);
   private static final Integer TASK_DURATION1_UNITS = new Integer (148);
   private static final Integer TASK_DURATION2 = new Integer (149);
   private static final Integer TASK_DURATION2_UNITS = new Integer (150);
   private static final Integer TASK_DURATION3 = new Integer (151);
   private static final Integer TASK_DURATION3_UNITS = new Integer (152);
   private static final Integer TASK_DURATION4 = new Integer (153);
   private static final Integer TASK_DURATION4_UNITS = new Integer (154);
   private static final Integer TASK_DURATION5 = new Integer (155);
   private static final Integer TASK_DURATION5_UNITS = new Integer (156);
   private static final Integer TASK_DURATION6 = new Integer (157);
   private static final Integer TASK_DURATION6_UNITS = new Integer (158);
   private static final Integer TASK_DURATION7 = new Integer (159);
   private static final Integer TASK_DURATION7_UNITS = new Integer (160);
   private static final Integer TASK_DURATION8 = new Integer (161);
   private static final Integer TASK_DURATION8_UNITS = new Integer (162);
   private static final Integer TASK_DURATION9 = new Integer (163);
   private static final Integer TASK_DURATION9_UNITS = new Integer (164);
   private static final Integer TASK_DURATION10 = new Integer (165);
   private static final Integer TASK_DURATION10_UNITS = new Integer (166);

   private static final Integer TASK_DATE1 = new Integer (174);
   private static final Integer TASK_DATE2 = new Integer (175);
   private static final Integer TASK_DATE3 = new Integer (176);
   private static final Integer TASK_DATE4 = new Integer (177);
   private static final Integer TASK_DATE5 = new Integer (178);
   private static final Integer TASK_DATE6 = new Integer (179);
   private static final Integer TASK_DATE7 = new Integer (180);
   private static final Integer TASK_DATE8 = new Integer (181);
   private static final Integer TASK_DATE9 = new Integer (182);
   private static final Integer TASK_DATE10 = new Integer (183);

   private static final Integer TASK_TEXT11 = new Integer (184);
   private static final Integer TASK_TEXT12 = new Integer (185);
   private static final Integer TASK_TEXT13 = new Integer (186);
   private static final Integer TASK_TEXT14 = new Integer (187);
   private static final Integer TASK_TEXT15 = new Integer (188);
   private static final Integer TASK_TEXT16 = new Integer (189);
   private static final Integer TASK_TEXT17 = new Integer (190);
   private static final Integer TASK_TEXT18 = new Integer (191);
   private static final Integer TASK_TEXT19 = new Integer (192);
   private static final Integer TASK_TEXT20 = new Integer (193);
   private static final Integer TASK_TEXT21 = new Integer (194);
   private static final Integer TASK_TEXT22 = new Integer (195);
   private static final Integer TASK_TEXT23 = new Integer (196);
   private static final Integer TASK_TEXT24 = new Integer (197);
   private static final Integer TASK_TEXT25 = new Integer (198);
   private static final Integer TASK_TEXT26 = new Integer (199);
   private static final Integer TASK_TEXT27 = new Integer (200);
   private static final Integer TASK_TEXT28 = new Integer (201);
   private static final Integer TASK_TEXT29 = new Integer (202);
   private static final Integer TASK_TEXT30 = new Integer (203);

   private static final Integer TASK_NUMBER11 = new Integer (204);
   private static final Integer TASK_NUMBER12 = new Integer (205);
   private static final Integer TASK_NUMBER13 = new Integer (206);
   private static final Integer TASK_NUMBER14 = new Integer (207);
   private static final Integer TASK_NUMBER15 = new Integer (208);
   private static final Integer TASK_NUMBER16 = new Integer (209);
   private static final Integer TASK_NUMBER17 = new Integer (210);
   private static final Integer TASK_NUMBER18 = new Integer (211);
   private static final Integer TASK_NUMBER19 = new Integer (212);
   private static final Integer TASK_NUMBER20 = new Integer (213);

   private static final Integer TASK_HYPERLINK = new Integer (236);

   private static final Integer TASK_COST1 = new Integer (237);
   private static final Integer TASK_COST2 = new Integer (238);
   private static final Integer TASK_COST3 = new Integer (239);
   private static final Integer TASK_COST4 = new Integer (240);
   private static final Integer TASK_COST5 = new Integer (241);
   private static final Integer TASK_COST6 = new Integer (242);
   private static final Integer TASK_COST7 = new Integer (243);
   private static final Integer TASK_COST8 = new Integer (244);
   private static final Integer TASK_COST9 = new Integer (245);
   private static final Integer TASK_COST10 = new Integer (246);

   private static final Integer TASK_NOTES = new Integer (247);

   /**
    * Resource data types.
    */
   private static final Integer RESOURCE_GROUP = new Integer (61);
   private static final Integer RESOURCE_CODE = new Integer (62);
   private static final Integer RESOURCE_EMAIL = new Integer (63);

   private static final Integer RESOURCE_TEXT1 = new Integer (64);
   private static final Integer RESOURCE_TEXT2 = new Integer (65);
   private static final Integer RESOURCE_TEXT3 = new Integer (66);
   private static final Integer RESOURCE_TEXT4 = new Integer (67);
   private static final Integer RESOURCE_TEXT5 = new Integer (68);
   private static final Integer RESOURCE_TEXT6 = new Integer (69);
   private static final Integer RESOURCE_TEXT7 = new Integer (70);
   private static final Integer RESOURCE_TEXT8 = new Integer (71);
   private static final Integer RESOURCE_TEXT9 = new Integer (72);
   private static final Integer RESOURCE_TEXT10 = new Integer (73);
   private static final Integer RESOURCE_TEXT11 = new Integer (74);
   private static final Integer RESOURCE_TEXT12 = new Integer (75);
   private static final Integer RESOURCE_TEXT13 = new Integer (76);
   private static final Integer RESOURCE_TEXT14 = new Integer (77);
   private static final Integer RESOURCE_TEXT15 = new Integer (78);
   private static final Integer RESOURCE_TEXT16 = new Integer (79);
   private static final Integer RESOURCE_TEXT17 = new Integer (80);
   private static final Integer RESOURCE_TEXT18 = new Integer (81);
   private static final Integer RESOURCE_TEXT19 = new Integer (82);
   private static final Integer RESOURCE_TEXT20 = new Integer (83);
   private static final Integer RESOURCE_TEXT21 = new Integer (84);
   private static final Integer RESOURCE_TEXT22 = new Integer (85);
   private static final Integer RESOURCE_TEXT23 = new Integer (86);
   private static final Integer RESOURCE_TEXT24 = new Integer (87);
   private static final Integer RESOURCE_TEXT25 = new Integer (88);
   private static final Integer RESOURCE_TEXT26 = new Integer (89);
   private static final Integer RESOURCE_TEXT27 = new Integer (90);
   private static final Integer RESOURCE_TEXT28 = new Integer (91);
   private static final Integer RESOURCE_TEXT29 = new Integer (92);
   private static final Integer RESOURCE_TEXT30 = new Integer (93);

   private static final Integer RESOURCE_START1 = new Integer (94);
   private static final Integer RESOURCE_START2 = new Integer (95);
   private static final Integer RESOURCE_START3 = new Integer (96);
   private static final Integer RESOURCE_START4 = new Integer (97);
   private static final Integer RESOURCE_START5 = new Integer (98);
   private static final Integer RESOURCE_START6 = new Integer (99);
   private static final Integer RESOURCE_START7 = new Integer (100);
   private static final Integer RESOURCE_START8 = new Integer (101);
   private static final Integer RESOURCE_START9 = new Integer (102);
   private static final Integer RESOURCE_START10 = new Integer (103);

   private static final Integer RESOURCE_FINISH1 = new Integer (104);
   private static final Integer RESOURCE_FINISH2 = new Integer (105);
   private static final Integer RESOURCE_FINISH3 = new Integer (106);
   private static final Integer RESOURCE_FINISH4 = new Integer (107);
   private static final Integer RESOURCE_FINISH5 = new Integer (108);
   private static final Integer RESOURCE_FINISH6 = new Integer (109);
   private static final Integer RESOURCE_FINISH7 = new Integer (110);
   private static final Integer RESOURCE_FINISH8 = new Integer (111);
   private static final Integer RESOURCE_FINISH9 = new Integer (112);
   private static final Integer RESOURCE_FINISH10 = new Integer (113);

   private static final Integer RESOURCE_NUMBER1 = new Integer (114);
   private static final Integer RESOURCE_NUMBER2 = new Integer (115);
   private static final Integer RESOURCE_NUMBER3 = new Integer (116);
   private static final Integer RESOURCE_NUMBER4 = new Integer (117);
   private static final Integer RESOURCE_NUMBER5 = new Integer (118);
   private static final Integer RESOURCE_NUMBER6 = new Integer (119);
   private static final Integer RESOURCE_NUMBER7 = new Integer (120);
   private static final Integer RESOURCE_NUMBER8 = new Integer (121);
   private static final Integer RESOURCE_NUMBER9 = new Integer (122);
   private static final Integer RESOURCE_NUMBER10 = new Integer (123);
   private static final Integer RESOURCE_NUMBER11 = new Integer (124);
   private static final Integer RESOURCE_NUMBER12 = new Integer (125);
   private static final Integer RESOURCE_NUMBER13 = new Integer (126);
   private static final Integer RESOURCE_NUMBER14 = new Integer (127);
   private static final Integer RESOURCE_NUMBER15 = new Integer (128);
   private static final Integer RESOURCE_NUMBER16 = new Integer (129);
   private static final Integer RESOURCE_NUMBER17 = new Integer (130);
   private static final Integer RESOURCE_NUMBER18 = new Integer (131);
   private static final Integer RESOURCE_NUMBER19 = new Integer (132);
   private static final Integer RESOURCE_NUMBER20 = new Integer (133);

   private static final Integer RESOURCE_DURATION1 = new Integer (134);
   private static final Integer RESOURCE_DURATION2 = new Integer (135);
   private static final Integer RESOURCE_DURATION3 = new Integer (136);
   private static final Integer RESOURCE_DURATION4 = new Integer (137);
   private static final Integer RESOURCE_DURATION5 = new Integer (138);
   private static final Integer RESOURCE_DURATION6 = new Integer (139);
   private static final Integer RESOURCE_DURATION7 = new Integer (140);
   private static final Integer RESOURCE_DURATION8 = new Integer (141);
   private static final Integer RESOURCE_DURATION9 = new Integer (142);
   private static final Integer RESOURCE_DURATION10 = new Integer (143);

   private static final Integer RESOURCE_DURATION1_UNITS = new Integer (144);
   private static final Integer RESOURCE_DURATION2_UNITS = new Integer (145);
   private static final Integer RESOURCE_DURATION3_UNITS = new Integer (146);
   private static final Integer RESOURCE_DURATION4_UNITS = new Integer (147);
   private static final Integer RESOURCE_DURATION5_UNITS = new Integer (148);
   private static final Integer RESOURCE_DURATION6_UNITS = new Integer (149);
   private static final Integer RESOURCE_DURATION7_UNITS = new Integer (150);
   private static final Integer RESOURCE_DURATION8_UNITS = new Integer (151);
   private static final Integer RESOURCE_DURATION9_UNITS = new Integer (152);
   private static final Integer RESOURCE_DURATION10_UNITS = new Integer (153);

   private static final Integer RESOURCE_DATE1 = new Integer (157);
   private static final Integer RESOURCE_DATE2 = new Integer (158);
   private static final Integer RESOURCE_DATE3 = new Integer (159);
   private static final Integer RESOURCE_DATE4 = new Integer (160);
   private static final Integer RESOURCE_DATE5 = new Integer (161);
   private static final Integer RESOURCE_DATE6 = new Integer (162);
   private static final Integer RESOURCE_DATE7 = new Integer (163);
   private static final Integer RESOURCE_DATE8 = new Integer (164);
   private static final Integer RESOURCE_DATE9 = new Integer (165);
   private static final Integer RESOURCE_DATE10 = new Integer (166);

   private static final Integer RESOURCE_NOTES = new Integer (169);

   private static final Integer RESOURCE_COST1 = new Integer (170);
   private static final Integer RESOURCE_COST2 = new Integer (171);
   private static final Integer RESOURCE_COST3 = new Integer (172);
   private static final Integer RESOURCE_COST4 = new Integer (173);
   private static final Integer RESOURCE_COST5 = new Integer (174);
   private static final Integer RESOURCE_COST6 = new Integer (175);
   private static final Integer RESOURCE_COST7 = new Integer (176);
   private static final Integer RESOURCE_COST8 = new Integer (177);
   private static final Integer RESOURCE_COST9 = new Integer (178);
   private static final Integer RESOURCE_COST10 = new Integer (179);

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
}

