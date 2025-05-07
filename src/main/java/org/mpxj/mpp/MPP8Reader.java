/*
 * file:       MPP8Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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

package org.mpxj.mpp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mpxj.Availability;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.LocalTimeRange;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import org.mpxj.AccrueType;
import org.mpxj.Column;
import org.mpxj.ConstraintType;
import java.time.DayOfWeek;

import org.mpxj.EventManager;
import org.mpxj.MPXJException;
import org.mpxj.Priority;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.RtfNotes;
import org.mpxj.Table;
import org.mpxj.TableContainer;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.View;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.Pair;

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
    */
   @Override public void process(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException
   {
      try
      {
         populateMemberData(file, root);
         processProjectProperties();

         if (!reader.getReadPropertiesOnly())
         {
            processCalendarData();
            processResourceData();
            processTaskData();
            processConstraintData();
            processAssignmentData();

            if (reader.getReadPresentationData())
            {
               processViewPropertyData();
               processViewData();
               processTableData();
            }
         }
      }

      finally
      {
         clearMemberData();
      }
   }

   /**
    * Populate member data used by the rest of the reader.
    *
    * @param file parent MPP file
    * @param root Root of the POI file system.
    */
   private void populateMemberData(ProjectFile file, DirectoryEntry root) throws IOException
   {
      m_root = root;
      m_file = file;
      m_eventManager = file.getEventManager();

      m_calendarMap = new HashMap<>();
      m_projectDir = (DirectoryEntry) root.getEntry("   1");
      m_viewDir = (DirectoryEntry) root.getEntry("   2");
      m_projectProps = new Props8(m_file, new DocumentInputStream(((DocumentEntry) m_projectDir.getEntry("Props"))));

      m_file.getProjectProperties().setMppFileType(Integer.valueOf(8));
   }

   /**
    * Clear transient member data.
    */
   private void clearMemberData()
   {
      m_root = null;
      m_eventManager = null;
      m_file = null;
      m_calendarMap = null;
      m_projectDir = null;
      m_viewDir = null;
      m_projectProps = null;
   }

   /**
    * Process the project properties data.
    */
   private void processProjectProperties() throws MPXJException
   {
      ProjectPropertiesReader reader = new ProjectPropertiesReader();
      reader.process(m_file, m_projectProps, m_root);
   }

   /**
    * This method process the data held in the props file specific to the
    * visual appearance of the project data.
    */
   private void processViewPropertyData() throws IOException
   {
      if (m_viewDir.hasEntry("Props"))
      {
         Props8 props = new Props8(m_file, new DocumentInputStream(((DocumentEntry) m_viewDir.getEntry("Props"))));
         ProjectProperties properties = m_file.getProjectProperties();
         properties.setShowProjectSummaryTask(props.getBoolean(Props.SHOW_PROJECT_SUMMARY_TASK));
      }
   }

   /**
    * This method extracts and collates calendar data.
    */
   private void processCalendarData() throws IOException
   {
      DirectoryEntry calDir = (DirectoryEntry) m_projectDir.getEntry("TBkndCal");
      FixFix calendarFixedData = new FixFix(36, new DocumentInputStream(((DocumentEntry) calDir.getEntry("FixFix   0"))));
      FixDeferFix calendarVarData = new FixDeferFix(new DocumentInputStream(((DocumentEntry) calDir.getEntry("FixDeferFix   0"))));

      ProjectCalendar cal;
      ProjectCalendarHours hours;
      ProjectCalendarException exception;
      String name;
      byte[] baseData;
      byte[] extData;

      int periodCount;
      int index;
      int offset;
      int defaultFlag;
      long duration;
      int exceptionCount;

      //
      // Configure default time ranges
      //
      int calendars = calendarFixedData.getItemCount();
      int calendarID;
      int baseCalendarID;
      int periodIndex;
      DayOfWeek day;
      List<Pair<ProjectCalendar, Integer>> baseCalendars = new ArrayList<>();

      for (int loop = 0; loop < calendars; loop++)
      {
         baseData = calendarFixedData.getByteArrayValue(loop);
         calendarID = ByteArrayHelper.getInt(baseData, 0);
         baseCalendarID = ByteArrayHelper.getInt(baseData, 4);
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
         ExtendedData ed = new ExtendedData(calendarVarData, getOffset(baseData, 32));
         offset = -1 - ed.getInt(Integer.valueOf(8));

         if (offset == -1)
         {
            if (baseCalendarID > 0)
            {
               cal = m_file.addDefaultDerivedCalendar();
               baseCalendars.add(new Pair<>(cal, Integer.valueOf(baseCalendarID)));
            }
            else
            {
               cal = m_file.addDefaultBaseCalendar();
               cal.setName(name);
            }

            cal.setUniqueID(Integer.valueOf(calendarID));
         }
         else
         {
            if (baseCalendarID > 0)
            {
               cal = m_file.addCalendar();
               baseCalendars.add(new Pair<>(cal, Integer.valueOf(baseCalendarID)));
            }
            else
            {
               cal = m_file.addCalendar();
               cal.setName(name);
            }

            cal.setUniqueID(Integer.valueOf(calendarID));

            extData = calendarVarData.getByteArray(offset);

            for (index = 0; index < 7; index++)
            {
               offset = 4 + (40 * index);

               defaultFlag = ByteArrayHelper.getShort(extData, offset);
               day = DayOfWeekHelper.getInstance(index + 1);

               if (defaultFlag == 1)
               {
                  cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
                  if (cal.isWorkingDay(day))
                  {
                     hours = cal.addCalendarHours(DayOfWeekHelper.getInstance(index + 1));
                     hours.add(new LocalTimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0)));
                     hours.add(new LocalTimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)));
                  }
                  else
                  {
                     if (baseCalendarID <= 0)
                     {
                        cal.addCalendarHours(DayOfWeekHelper.getInstance(index + 1));
                     }
                  }
               }
               else
               {
                  periodCount = ByteArrayHelper.getShort(extData, offset + 2);
                  hours = cal.addCalendarHours(DayOfWeekHelper.getInstance(index + 1));
                  if (periodCount == 0)
                  {
                     cal.setWorkingDay(day, false);
                  }
                  else
                  {
                     cal.setWorkingDay(day, true);

                     for (periodIndex = 0; periodIndex < periodCount; periodIndex++)
                     {
                        LocalTime start = MPPUtility.getTime(extData, offset + 8 + (periodIndex * 2));
                        duration = MPPUtility.getDuration(extData, offset + 16 + (periodIndex * 4));
                        LocalTime end = start.plus(duration, ChronoUnit.MILLIS);
                        hours.add(new LocalTimeRange(start, end));
                     }
                  }
               }
            }

            //
            // Handle any exceptions
            //
            exceptionCount = ByteArrayHelper.getShort(extData, 0);
            if (exceptionCount != 0)
            {
               for (index = 0; index < exceptionCount; index++)
               {
                  offset = 4 + (40 * 7) + (index * 44);

                  LocalDate fromDate = LocalDateHelper.getLocalDate(MPPUtility.getDate(extData, offset));
                  LocalDate toDate = LocalDateHelper.getLocalDate(MPPUtility.getDate(extData, offset + 2));
                  exception = cal.addCalendarException(fromDate, toDate);

                  periodCount = ByteArrayHelper.getShort(extData, offset + 6);
                  if (periodCount != 0)
                  {
                     for (int exceptionPeriodIndex = 0; exceptionPeriodIndex < periodCount; exceptionPeriodIndex++)
                     {
                        LocalTime start = MPPUtility.getTime(extData, offset + 12 + (exceptionPeriodIndex * 2));
                        duration = MPPUtility.getDuration(extData, offset + 20 + (exceptionPeriodIndex * 4));
                        LocalTime end = start.plus(duration, ChronoUnit.MILLIS);
                        exception.add(new LocalTimeRange(start, end));
                     }
                  }
               }
            }
         }

         m_calendarMap.put(Integer.valueOf(calendarID), cal);
         m_eventManager.fireCalendarReadEvent(cal);
      }

      updateBaseCalendarNames(baseCalendars);

      ProjectCalendar projectDefaultCalendar = m_file.getCalendars().getByName(m_projectProps.getUnicodeString(Props.DEFAULT_CALENDAR_NAME));
      if (projectDefaultCalendar == null)
      {
         projectDefaultCalendar = m_file.getCalendars().findOrCreateDefaultCalendar();
      }
      m_file.getProjectProperties().setDefaultCalendar(projectDefaultCalendar);
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
    */
   private void updateBaseCalendarNames(List<Pair<ProjectCalendar, Integer>> baseCalendars)
   {
      for (Pair<ProjectCalendar, Integer> pair : baseCalendars)
      {
         ProjectCalendar cal = pair.getFirst();
         Integer baseCalendarID = pair.getSecond();
         ProjectCalendar baseCal = m_calendarMap.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setParent(baseCal);
         }
      }
   }

   /**
    * This method extracts and collates task data.
    */
   private void processTaskData() throws IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      FixFix taskFixedData = new FixFix(316, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixFix   0"))));
      if (taskFixedData.getDiff() != 0)
      {
         taskFixedData = new FixFix(366, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixFix   0"))));
      }

      FixDeferFix taskVarData = null;
      ExtendedData taskExtData;

      int tasks = taskFixedData.getItemCount();
      byte[] data;
      int uniqueID;
      int id;
      int deleted;
      Task task;
      boolean autoWBS = true;
      byte[] flags = new byte[3];
      RecurringTaskReader recurringTaskReader = null;
      ProjectProperties properties = m_file.getProjectProperties();
      TimeUnit defaultProjectTimeUnits = properties.getDefaultDurationUnits();
      HyperlinkReader hyperlinkReader = new HyperlinkReader();
      hyperlinkReader.setHasScreenTip(false);

      for (int loop = 0; loop < tasks; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);

         //
         // Test for a valid unique id
         //
         uniqueID = ByteArrayHelper.getInt(data, 0);
         if (uniqueID < 1)
         {
            continue;
         }

         //
         // Test to ensure this task has not been deleted.
         // This appears to be a set of flags rather than a single value.
         // The data I have seen to date shows deleted tasks having values of
         // 0x0001 and 0x0002. Valid tasks have had values of 0x0000, 0x0914,
         // 0x0040, 0x004A, 0x203D and 0x0031
         //
         deleted = ByteArrayHelper.getShort(data, 272);
         if ((deleted & 0xC0) == 0 && (deleted & 0x03) != 0 && deleted != 0x0031 && deleted != 0x203D)
         {
            continue;
         }

         //
         // Load the var data if we have not already done so
         //
         if (taskVarData == null)
         {
            taskVarData = new FixDeferFix(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixDeferFix   0"))));
         }

         //
         // Blank rows can be present in MPP files. The following flag
         // appears to indicate that a row is blank, and should be
         // ignored.
         //
         if ((data[8] & 0x01) != 0)
         {
            continue;
         }

         taskExtData = new ExtendedData(taskVarData, getOffset(data, 312));
         byte[] recurringData = taskExtData.getByteArray(TASK_RECURRING_DATA);

         id = ByteArrayHelper.getInt(data, 4);
         flags[0] = (byte) (data[268] & data[303]);
         flags[1] = (byte) (data[269] & data[304]);
         flags[2] = (byte) (data[270] & data[305]);

         task = m_file.addTask();

         task.setActualCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 234)) / 100));
         task.setActualDuration(MPPUtility.getAdjustedDuration(properties, ByteArrayHelper.getInt(data, 74), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, 72), defaultProjectTimeUnits)));
         task.setActualFinish(MPPUtility.getTimestamp(data, 108));
         task.setActualOvertimeCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 210)) / 100));
         task.setActualOvertimeWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 192)) / 100, TimeUnit.HOURS));
         task.setActualStart(MPPUtility.getTimestamp(data, 104));
         task.setActualWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 180)) / 100, TimeUnit.HOURS));
         //task.setACWP(); // Calculated value
         //task.setAssignment(); // Calculated value
         //task.setAssignmentDelay(); // Calculated value
         //task.setAssignmentUnits(); // Calculated value
         task.setBaselineCost(NumberHelper.getDouble((double) MPPUtility.getLong6(data, 246) / 100));
         task.setBaselineDuration(MPPUtility.getAdjustedDuration(properties, ByteArrayHelper.getInt(data, 82), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, 72), defaultProjectTimeUnits)));
         task.setBaselineFinish(MPPUtility.getTimestamp(data, 116));
         task.setBaselineStart(MPPUtility.getTimestamp(data, 112));
         task.setBaselineWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 174)) / 100, TimeUnit.HOURS));
         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate(MPPUtility.getTimestamp(data, 120));
         task.setConstraintType(ConstraintType.getInstance(ByteArrayHelper.getShort(data, 88)));
         task.setContact(taskExtData.getUnicodeString(TASK_CONTACT));
         task.setCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 222)) / 100));
         task.setCost(1, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST1)) / 100));
         task.setCost(2, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST2)) / 100));
         task.setCost(3, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST3)) / 100));
         task.setCost(4, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST4)) / 100));
         task.setCost(5, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST5)) / 100));
         task.setCost(6, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST6)) / 100));
         task.setCost(7, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST7)) / 100));
         task.setCost(8, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST8)) / 100));
         task.setCost(9, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST9)) / 100));
         task.setCost(10, NumberHelper.getDouble(((double) taskExtData.getLong(TASK_COST10)) / 100));
         //task.setCostRateTable(); // Calculated value
         //task.setCostVariance(); // Populated below
         task.setCreateDate(MPPUtility.getTimestamp(data, 138));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         task.setDate(1, taskExtData.getTimestamp(TASK_DATE1));
         task.setDate(2, taskExtData.getTimestamp(TASK_DATE2));
         task.setDate(3, taskExtData.getTimestamp(TASK_DATE3));
         task.setDate(4, taskExtData.getTimestamp(TASK_DATE4));
         task.setDate(5, taskExtData.getTimestamp(TASK_DATE5));
         task.setDate(6, taskExtData.getTimestamp(TASK_DATE6));
         task.setDate(7, taskExtData.getTimestamp(TASK_DATE7));
         task.setDate(8, taskExtData.getTimestamp(TASK_DATE8));
         task.setDate(9, taskExtData.getTimestamp(TASK_DATE9));
         task.setDate(10, taskExtData.getTimestamp(TASK_DATE10));
         //task.setDelay(); // No longer supported by MS Project?
         task.setDuration(MPPUtility.getAdjustedDuration(properties, ByteArrayHelper.getInt(data, 68), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, 72), defaultProjectTimeUnits)));
         task.setDuration(1, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION1), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION1_UNITS), defaultProjectTimeUnits)));
         task.setDuration(2, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION2), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION2_UNITS), defaultProjectTimeUnits)));
         task.setDuration(3, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION3), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION3_UNITS), defaultProjectTimeUnits)));
         task.setDuration(4, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION4), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION4_UNITS), defaultProjectTimeUnits)));
         task.setDuration(5, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION5), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION5_UNITS), defaultProjectTimeUnits)));
         task.setDuration(6, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION6), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION6_UNITS), defaultProjectTimeUnits)));
         task.setDuration(7, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION7), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION7_UNITS), defaultProjectTimeUnits)));
         task.setDuration(8, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION8), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION8_UNITS), defaultProjectTimeUnits)));
         task.setDuration(9, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION9), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION9_UNITS), defaultProjectTimeUnits)));
         task.setDuration(10, MPPUtility.getAdjustedDuration(properties, taskExtData.getInt(TASK_DURATION10), MPPUtility.getDurationTimeUnits(taskExtData.getShort(TASK_DURATION10_UNITS), defaultProjectTimeUnits)));
         //task.setDurationVariance(); // Calculated value
         task.setEarlyFinish(MPPUtility.getTimestamp(data, 20));
         task.setEarlyStart(MPPUtility.getTimestamp(data, 96));
         task.setEffortDriven((data[17] & 0x08) != 0);
         //task.setExternalTask(); // Calculated value
         task.setFinish(MPPUtility.getTimestamp(data, 20));
         task.setFinish(1, taskExtData.getTimestamp(TASK_FINISH1));
         task.setFinish(2, taskExtData.getTimestamp(TASK_FINISH2));
         task.setFinish(3, taskExtData.getTimestamp(TASK_FINISH3));
         task.setFinish(4, taskExtData.getTimestamp(TASK_FINISH4));
         task.setFinish(5, taskExtData.getTimestamp(TASK_FINISH5));
         task.setFinish(6, taskExtData.getTimestamp(TASK_FINISH6));
         task.setFinish(7, taskExtData.getTimestamp(TASK_FINISH7));
         task.setFinish(8, taskExtData.getTimestamp(TASK_FINISH8));
         task.setFinish(9, taskExtData.getTimestamp(TASK_FINISH9));
         task.setFinish(10, taskExtData.getTimestamp(TASK_FINISH10));
         //task.setFinishVariance(); // Calculated value
         task.setFixedCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 228)) / 100));
         task.setFixedCostAccrual(AccrueType.getInstance(ByteArrayHelper.getShort(data, 136)));
         task.setFlag(1, (flags[0] & 0x02) != 0);
         task.setFlag(2, (flags[0] & 0x04) != 0);
         task.setFlag(3, (flags[0] & 0x08) != 0);
         task.setFlag(4, (flags[0] & 0x10) != 0);
         task.setFlag(5, (flags[0] & 0x20) != 0);
         task.setFlag(6, (flags[0] & 0x40) != 0);
         task.setFlag(7, (flags[0] & 0x80) != 0);
         task.setFlag(8, (flags[1] & 0x01) != 0);
         task.setFlag(9, (flags[1] & 0x02) != 0);
         task.setFlag(10, (flags[1] & 0x04) != 0);
         task.setFlag(11, (flags[1] & 0x08) != 0);
         task.setFlag(12, (flags[1] & 0x10) != 0);
         task.setFlag(13, (flags[1] & 0x20) != 0);
         task.setFlag(14, (flags[1] & 0x40) != 0);
         task.setFlag(15, (flags[1] & 0x80) != 0);
         task.setFlag(16, (flags[2] & 0x01) != 0);
         task.setFlag(17, (flags[2] & 0x02) != 0);
         task.setFlag(18, (flags[2] & 0x04) != 0);
         task.setFlag(19, (flags[2] & 0x08) != 0);
         task.setFlag(20, (flags[2] & 0x10) != 0); // note that this is not correct
         //task.setFreeSlack();  // Calculated value
         task.setHideBar((data[16] & 0x01) != 0);
         hyperlinkReader.read(task, taskVarData.getByteArray(-1 - taskExtData.getInt(TASK_HYPERLINK)));
         task.setID(Integer.valueOf(id));
         //task.setIndicators(); // Calculated value
         task.setLateFinish(MPPUtility.getTimestamp(data, 160));
         task.setLateStart(MPPUtility.getTimestamp(data, 24));
         task.setLevelAssignments((data[19] & 0x10) != 0);
         task.setLevelingCanSplit((data[19] & 0x08) != 0);
         task.setLevelingDelay(MPPUtility.getDuration(((double) ByteArrayHelper.getInt(data, 90)) / 3, MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, 94), defaultProjectTimeUnits)));
         //task.setLinkedFields();  // Calculated value
         task.setMarked((data[13] & 0x02) != 0);
         task.setMilestone((data[12] & 0x01) != 0);
         task.setName(taskVarData.getUnicodeString(getOffset(data, 264)));
         task.setNumber(1, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER1)));
         task.setNumber(2, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER2)));
         task.setNumber(3, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER3)));
         task.setNumber(4, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER4)));
         task.setNumber(5, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER5)));
         task.setNumber(6, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER6)));
         task.setNumber(7, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER7)));
         task.setNumber(8, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER8)));
         task.setNumber(9, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER9)));
         task.setNumber(10, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER10)));
         task.setNumber(11, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER11)));
         task.setNumber(12, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER12)));
         task.setNumber(13, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER13)));
         task.setNumber(14, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER14)));
         task.setNumber(15, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER15)));
         task.setNumber(16, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER16)));
         task.setNumber(17, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER17)));
         task.setNumber(18, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER18)));
         task.setNumber(19, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER19)));
         task.setNumber(20, NumberHelper.getDouble(taskExtData.getDouble(TASK_NUMBER20)));
         //task.setObjects(); // Calculated value
         task.setOutlineLevel(Integer.valueOf(ByteArrayHelper.getShort(data, 48)));
         //task.setOutlineNumber(); // Calculated value
         //task.setOverallocated(); // Calculated value
         task.setOvertimeCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 204)) / 100));
         //task.setOvertimeWork(); // Calculated value
         //task.getPredecessors(); // Calculated value
         task.setPercentageComplete(MPPUtility.getPercentage(data, 130));
         task.setPercentageWorkComplete(MPPUtility.getPercentage(data, 132));
         task.setPreleveledFinish(MPPUtility.getTimestamp(data, 148));
         task.setPreleveledStart(MPPUtility.getTimestamp(data, 144));
         task.setPriority(Priority.getInstance((ByteArrayHelper.getShort(data, 128) + 1) * 100));
         //task.setProject(); // Calculated value
         task.setRecurring(ByteArrayHelper.getShort(data, 142) != 0);
         //task.setRegularWork(); // Calculated value
         task.setRemainingCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 240)) / 100));
         task.setRemainingDuration(MPPUtility.getAdjustedDuration(properties, ByteArrayHelper.getInt(data, 78), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, 72), defaultProjectTimeUnits)));
         task.setRemainingOvertimeCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 216)) / 100));
         task.setRemainingOvertimeWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 198)) / 100, TimeUnit.HOURS));
         task.setRemainingWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 186)) / 100, TimeUnit.HOURS));
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         //task.setResourcePhonetics(); // Calculated value from resource
         //task.setResponsePending(); // Calculated value
         task.setResume(MPPUtility.getTimestamp(data, 32));
         //task.setResumeNoEarlierThan(); // Not in MSP98?
         task.setRollup((data[15] & 0x04) != 0);
         task.setStart(MPPUtility.getTimestamp(data, 96));
         task.setStart(1, taskExtData.getTimestamp(TASK_START1));
         task.setStart(2, taskExtData.getTimestamp(TASK_START2));
         task.setStart(3, taskExtData.getTimestamp(TASK_START3));
         task.setStart(4, taskExtData.getTimestamp(TASK_START4));
         task.setStart(5, taskExtData.getTimestamp(TASK_START5));
         task.setStart(6, taskExtData.getTimestamp(TASK_START6));
         task.setStart(7, taskExtData.getTimestamp(TASK_START7));
         task.setStart(8, taskExtData.getTimestamp(TASK_START8));
         task.setStart(9, taskExtData.getTimestamp(TASK_START9));
         task.setStart(10, taskExtData.getTimestamp(TASK_START10));
         //task.setStartVariance(); // Calculated value
         task.setStop(MPPUtility.getTimestamp(data, 124));
         //task.setSubprojectFile();
         //task.setSubprojectReadOnly();
         //task.setSuccessors(); // Calculated value
         //task.setSummary(); // Automatically generated by MPXJ
         //task.setSV(); // Calculated value
         //task.teamStatusPending(); // Calculated value
         task.setText(1, taskExtData.getUnicodeString(TASK_TEXT1));
         task.setText(2, taskExtData.getUnicodeString(TASK_TEXT2));
         task.setText(3, taskExtData.getUnicodeString(TASK_TEXT3));
         task.setText(4, taskExtData.getUnicodeString(TASK_TEXT4));
         task.setText(5, taskExtData.getUnicodeString(TASK_TEXT5));
         task.setText(6, taskExtData.getUnicodeString(TASK_TEXT6));
         task.setText(7, taskExtData.getUnicodeString(TASK_TEXT7));
         task.setText(8, taskExtData.getUnicodeString(TASK_TEXT8));
         task.setText(9, taskExtData.getUnicodeString(TASK_TEXT9));
         task.setText(10, taskExtData.getUnicodeString(TASK_TEXT10));
         task.setText(11, taskExtData.getUnicodeString(TASK_TEXT11));
         task.setText(12, taskExtData.getUnicodeString(TASK_TEXT12));
         task.setText(13, taskExtData.getUnicodeString(TASK_TEXT13));
         task.setText(14, taskExtData.getUnicodeString(TASK_TEXT14));
         task.setText(15, taskExtData.getUnicodeString(TASK_TEXT15));
         task.setText(16, taskExtData.getUnicodeString(TASK_TEXT16));
         task.setText(17, taskExtData.getUnicodeString(TASK_TEXT17));
         task.setText(18, taskExtData.getUnicodeString(TASK_TEXT18));
         task.setText(19, taskExtData.getUnicodeString(TASK_TEXT19));
         task.setText(20, taskExtData.getUnicodeString(TASK_TEXT20));
         task.setText(21, taskExtData.getUnicodeString(TASK_TEXT21));
         task.setText(22, taskExtData.getUnicodeString(TASK_TEXT22));
         task.setText(23, taskExtData.getUnicodeString(TASK_TEXT23));
         task.setText(24, taskExtData.getUnicodeString(TASK_TEXT24));
         task.setText(25, taskExtData.getUnicodeString(TASK_TEXT25));
         task.setText(26, taskExtData.getUnicodeString(TASK_TEXT26));
         task.setText(27, taskExtData.getUnicodeString(TASK_TEXT27));
         task.setText(28, taskExtData.getUnicodeString(TASK_TEXT28));
         task.setText(29, taskExtData.getUnicodeString(TASK_TEXT29));
         task.setText(30, taskExtData.getUnicodeString(TASK_TEXT30));
         //task.setTotalSlack(); // Calculated value
         task.setType(TaskTypeHelper.getInstance(ByteArrayHelper.getShort(data, 134)));
         task.setUniqueID(Integer.valueOf(uniqueID));
         //task.setUniqueIDPredecessors(); // Calculated value
         //task.setUniqueIDSuccessors(); // Calculated value
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskExtData.getUnicodeString(TASK_WBS));
         task.setWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 168)) / 100, TimeUnit.HOURS));
         //task.setWorkContour(); // Calculated from resource
         //task.setWorkVariance(); // Calculated value

         //
         // Retrieve task recurring data
         //
         if (recurringData != null)
         {
            if (recurringTaskReader == null)
            {
               recurringTaskReader = new RecurringTaskReader(properties);
            }
            recurringTaskReader.processRecurringTask(task, recurringData);
         }

         //
         // Retrieve the task notes.
         //
         setTaskNotes(task, data, taskExtData, taskVarData);

         //
         // If we have a WBS value from the MPP file, don't autogenerate
         //
         if (task.getWBS() != null)
         {
            autoWBS = false;
         }

         m_eventManager.fireTaskReadEvent(task);

         //
         // Uncommenting the call to this method is useful when trying
         // to determine the function of unknown task data.
         //
         //dumpUnknownData (task.getName(), UNKNOWN_TASK_DATA, data);
      }

      //
      // Enable auto WBS if necessary
      //
      m_file.getProjectConfig().setAutoWBS(autoWBS);
   }

   /**
    * There appear to be two ways of representing task notes in an MPP8 file.
    * This method tries to determine which has been used.
    *
    * @param task task
    * @param data task data
    * @param taskExtData extended task data
    * @param taskVarData task var data
    */
   private void setTaskNotes(Task task, byte[] data, ExtendedData taskExtData, FixDeferFix taskVarData)
   {
      String notes = taskExtData.getString(TASK_NOTES);
      if (notes == null && data.length == 366)
      {
         byte[] offsetData = taskVarData.getByteArray(getOffset(data, 362));
         if (offsetData != null && offsetData.length >= 12)
         {
            notes = taskVarData.getString(getOffset(offsetData, 8));

            // We do pick up some random stuff with this approach, and
            // we don't know enough about the file format to know when to ignore it
            // so we'll use a heuristic here to ignore anything that
            // doesn't look like RTF.
            if (notes != null && notes.indexOf('{') == -1)
            {
               notes = null;
            }
         }
      }

      if (notes != null)
      {
         task.setNotesObject(new RtfNotes(notes));
      }
   }

   /**
    * This method extracts and collates constraint data.
    */
   private void processConstraintData() throws IOException
   {
      //
      // Locate the directory containing the constraints
      //
      DirectoryEntry consDir;

      try
      {
         consDir = (DirectoryEntry) m_projectDir.getEntry("TBkndCons");
      }

      catch (FileNotFoundException ex)
      {
         m_file.addIgnoredError(ex);
         consDir = null;
      }

      //
      // It appears possible that valid MPP8 files can be generated without
      // this directory, so only process constraints if the directory
      // exists.
      //
      if (consDir != null)
      {
         FixFix consFixedData = new FixFix(36, new DocumentInputStream(((DocumentEntry) consDir.getEntry("FixFix   0"))));
         int count = consFixedData.getItemCount();

         for (int loop = 0; loop < count; loop++)
         {
            byte[] data = consFixedData.getByteArrayValue(loop);

            if (ByteArrayHelper.getInt(data, 28) == 0)
            {
               int taskID1 = ByteArrayHelper.getInt(data, 12);
               int taskID2 = ByteArrayHelper.getInt(data, 16);

               if (taskID1 != taskID2)
               {
                  Task task1 = m_file.getTaskByUniqueID(Integer.valueOf(taskID1));
                  Task task2 = m_file.getTaskByUniqueID(Integer.valueOf(taskID2));
                  if (task1 != null && task2 != null)
                  {
                     Relation relation = task2.addPredecessor(new Relation.Builder()
                        .predecessorTask(task1)
                        .type(RelationType.getInstance(ByteArrayHelper.getShort(data, 20)))
                        .lag(MPPUtility.getDuration(ByteArrayHelper.getInt(data, 24), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, 22))))
                        .uniqueID(Integer.valueOf(ByteArrayHelper.getInt(data, 0))));
                     m_eventManager.fireRelationReadEvent(relation);
                  }
               }
            }
         }
      }
   }

   /**
    * This method extracts and collates resource data.
    */
   private void processResourceData() throws IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry) m_projectDir.getEntry("TBkndRsc");
      FixFix rscFixedData = new FixFix(196, new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixFix   0"))));
      FixDeferFix rscVarData = null;
      ExtendedData rscExtData;

      int resources = rscFixedData.getItemCount();
      byte[] data;
      int id;
      Resource resource;
      String notes;
      ProjectCalendar calendar;

      for (int loop = 0; loop < resources; loop++)
      {
         data = rscFixedData.getByteArrayValue(loop);

         //
         // Test for a valid unique id
         //
         id = ByteArrayHelper.getInt(data, 0);
         if (id < 1)
         {
            continue;
         }

         //
         // Blank rows can be present in MPP files. The following flag
         // appears to indicate that a row is blank, and should be
         // ignored.
         //
         if ((data[8] & 0x01) != 0)
         {
            continue;
         }

         //
         // Test to ensure this resource has not been deleted
         // This may be an array of bit flags, as per the task
         // record. I have yet to see data to support this, so
         // the simple non-zero test remains.
         //
         if (ByteArrayHelper.getShort(data, 164) != 0)
         {
            continue;
         }

         //
         // Load the var data if we have not already done so
         //
         if (rscVarData == null)
         {
            rscVarData = new FixDeferFix(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixDeferFix   0"))));
         }

         rscExtData = new ExtendedData(rscVarData, getOffset(data, 192));

         resource = m_file.addResource();

         resource.setAccrueAt(AccrueType.getInstance(ByteArrayHelper.getShort(data, 20)));
         resource.setActualCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 114)) / 100));
         resource.setActualOvertimeCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 144)) / 100));
         resource.setActualWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 62)) / 100, TimeUnit.HOURS));
         //resource.setBaseCalendar();
         resource.setBaselineCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 126)) / 100));
         resource.setBaselineWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 68)) / 100, TimeUnit.HOURS));
         resource.setCode(rscExtData.getUnicodeString(RESOURCE_CODE));
         resource.setCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 120)) / 100));
         resource.setCost(1, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST1)) / 100));
         resource.setCost(2, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST2)) / 100));
         resource.setCost(3, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST3)) / 100));
         resource.setCost(4, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST4)) / 100));
         resource.setCost(5, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST5)) / 100));
         resource.setCost(6, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST6)) / 100));
         resource.setCost(7, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST7)) / 100));
         resource.setCost(8, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST8)) / 100));
         resource.setCost(9, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST9)) / 100));
         resource.setCost(10, NumberHelper.getDouble(((double) rscExtData.getLong(RESOURCE_COST10)) / 100));
         resource.setDate(1, rscExtData.getTimestamp(RESOURCE_DATE1));
         resource.setDate(2, rscExtData.getTimestamp(RESOURCE_DATE2));
         resource.setDate(3, rscExtData.getTimestamp(RESOURCE_DATE3));
         resource.setDate(4, rscExtData.getTimestamp(RESOURCE_DATE4));
         resource.setDate(5, rscExtData.getTimestamp(RESOURCE_DATE5));
         resource.setDate(6, rscExtData.getTimestamp(RESOURCE_DATE6));
         resource.setDate(7, rscExtData.getTimestamp(RESOURCE_DATE7));
         resource.setDate(8, rscExtData.getTimestamp(RESOURCE_DATE8));
         resource.setDate(9, rscExtData.getTimestamp(RESOURCE_DATE9));
         resource.setDate(10, rscExtData.getTimestamp(RESOURCE_DATE10));
         resource.setDuration(1, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION1), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION1_UNITS))));
         resource.setDuration(2, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION2), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION2_UNITS))));
         resource.setDuration(3, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION3), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION3_UNITS))));
         resource.setDuration(4, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION4), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION4_UNITS))));
         resource.setDuration(5, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION5), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION5_UNITS))));
         resource.setDuration(6, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION6), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION6_UNITS))));
         resource.setDuration(7, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION7), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION7_UNITS))));
         resource.setDuration(8, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION8), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION8_UNITS))));
         resource.setDuration(9, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION9), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION9_UNITS))));
         resource.setDuration(10, MPPUtility.getDuration(rscExtData.getInt(RESOURCE_DURATION10), MPPUtility.getDurationTimeUnits(rscExtData.getShort(RESOURCE_DURATION10_UNITS))));
         resource.setEmailAddress(rscExtData.getUnicodeString(RESOURCE_EMAIL));
         resource.setFinish(1, rscExtData.getTimestamp(RESOURCE_FINISH1));
         resource.setFinish(2, rscExtData.getTimestamp(RESOURCE_FINISH2));
         resource.setFinish(3, rscExtData.getTimestamp(RESOURCE_FINISH3));
         resource.setFinish(4, rscExtData.getTimestamp(RESOURCE_FINISH4));
         resource.setFinish(5, rscExtData.getTimestamp(RESOURCE_FINISH5));
         resource.setFinish(6, rscExtData.getTimestamp(RESOURCE_FINISH6));
         resource.setFinish(7, rscExtData.getTimestamp(RESOURCE_FINISH7));
         resource.setFinish(8, rscExtData.getTimestamp(RESOURCE_FINISH8));
         resource.setFinish(9, rscExtData.getTimestamp(RESOURCE_FINISH9));
         resource.setFinish(10, rscExtData.getTimestamp(RESOURCE_FINISH10));
         resource.setGroup(rscExtData.getUnicodeString(RESOURCE_GROUP));
         resource.setID(Integer.valueOf(ByteArrayHelper.getInt(data, 4)));
         resource.setInitials(rscVarData.getUnicodeString(getOffset(data, 160)));
         //resource.setLinkedFields(); // Calculated value
         resource.setName(rscVarData.getUnicodeString(getOffset(data, 156)));
         resource.setNumber(1, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER1)));
         resource.setNumber(2, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER2)));
         resource.setNumber(3, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER3)));
         resource.setNumber(4, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER4)));
         resource.setNumber(5, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER5)));
         resource.setNumber(6, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER6)));
         resource.setNumber(7, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER7)));
         resource.setNumber(8, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER8)));
         resource.setNumber(9, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER9)));
         resource.setNumber(10, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER10)));
         resource.setNumber(11, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER11)));
         resource.setNumber(12, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER12)));
         resource.setNumber(13, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER13)));
         resource.setNumber(14, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER14)));
         resource.setNumber(15, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER15)));
         resource.setNumber(16, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER16)));
         resource.setNumber(17, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER17)));
         resource.setNumber(18, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER18)));
         resource.setNumber(19, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER19)));
         resource.setNumber(20, NumberHelper.getDouble(rscExtData.getDouble(RESOURCE_NUMBER20)));
         //resource.setObjects(); // Calculated value
         //resource.setOverallocated(); // Calculated value
         resource.setOvertimeCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 138)) / 100));
         resource.setOvertimeWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 74)) / 100, TimeUnit.HOURS));
         resource.setPeakUnits(NumberHelper.getDouble(((double) ByteArrayHelper.getInt(data, 110)) / 100));
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 92)) / 100, TimeUnit.HOURS));
         resource.setRemainingCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 132)) / 100));
         resource.setRemainingOvertimeCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 150)) / 100));
         resource.setRemainingWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 86)) / 100, TimeUnit.HOURS));
         resource.setStart(1, rscExtData.getTimestamp(RESOURCE_START1));
         resource.setStart(2, rscExtData.getTimestamp(RESOURCE_START2));
         resource.setStart(3, rscExtData.getTimestamp(RESOURCE_START3));
         resource.setStart(4, rscExtData.getTimestamp(RESOURCE_START4));
         resource.setStart(5, rscExtData.getTimestamp(RESOURCE_START5));
         resource.setStart(6, rscExtData.getTimestamp(RESOURCE_START6));
         resource.setStart(7, rscExtData.getTimestamp(RESOURCE_START7));
         resource.setStart(8, rscExtData.getTimestamp(RESOURCE_START8));
         resource.setStart(9, rscExtData.getTimestamp(RESOURCE_START9));
         resource.setStart(10, rscExtData.getTimestamp(RESOURCE_START10));
         resource.setText(1, rscExtData.getUnicodeString(RESOURCE_TEXT1));
         resource.setText(2, rscExtData.getUnicodeString(RESOURCE_TEXT2));
         resource.setText(3, rscExtData.getUnicodeString(RESOURCE_TEXT3));
         resource.setText(4, rscExtData.getUnicodeString(RESOURCE_TEXT4));
         resource.setText(5, rscExtData.getUnicodeString(RESOURCE_TEXT5));
         resource.setText(6, rscExtData.getUnicodeString(RESOURCE_TEXT6));
         resource.setText(7, rscExtData.getUnicodeString(RESOURCE_TEXT7));
         resource.setText(8, rscExtData.getUnicodeString(RESOURCE_TEXT8));
         resource.setText(9, rscExtData.getUnicodeString(RESOURCE_TEXT9));
         resource.setText(10, rscExtData.getUnicodeString(RESOURCE_TEXT10));
         resource.setText(11, rscExtData.getUnicodeString(RESOURCE_TEXT11));
         resource.setText(12, rscExtData.getUnicodeString(RESOURCE_TEXT12));
         resource.setText(13, rscExtData.getUnicodeString(RESOURCE_TEXT13));
         resource.setText(14, rscExtData.getUnicodeString(RESOURCE_TEXT14));
         resource.setText(15, rscExtData.getUnicodeString(RESOURCE_TEXT15));
         resource.setText(16, rscExtData.getUnicodeString(RESOURCE_TEXT16));
         resource.setText(17, rscExtData.getUnicodeString(RESOURCE_TEXT17));
         resource.setText(18, rscExtData.getUnicodeString(RESOURCE_TEXT18));
         resource.setText(19, rscExtData.getUnicodeString(RESOURCE_TEXT19));
         resource.setText(20, rscExtData.getUnicodeString(RESOURCE_TEXT20));
         resource.setText(21, rscExtData.getUnicodeString(RESOURCE_TEXT21));
         resource.setText(22, rscExtData.getUnicodeString(RESOURCE_TEXT22));
         resource.setText(23, rscExtData.getUnicodeString(RESOURCE_TEXT23));
         resource.setText(24, rscExtData.getUnicodeString(RESOURCE_TEXT24));
         resource.setText(25, rscExtData.getUnicodeString(RESOURCE_TEXT25));
         resource.setText(26, rscExtData.getUnicodeString(RESOURCE_TEXT26));
         resource.setText(27, rscExtData.getUnicodeString(RESOURCE_TEXT27));
         resource.setText(28, rscExtData.getUnicodeString(RESOURCE_TEXT28));
         resource.setText(29, rscExtData.getUnicodeString(RESOURCE_TEXT29));
         resource.setText(30, rscExtData.getUnicodeString(RESOURCE_TEXT30));
         resource.setUniqueID(Integer.valueOf(id));
         resource.setWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 56)) / 100, TimeUnit.HOURS));

         //
         // Attach the resource calendar
         //
         calendar = m_calendarMap.get(Integer.valueOf(ByteArrayHelper.getInt(data, 24)));
         resource.setCalendar(calendar);

         //
         // Retrieve the resource notes.
         //
         notes = rscExtData.getString(RESOURCE_NOTES);
         if (notes != null)
         {
            resource.setNotesObject(new RtfNotes(notes));
         }

         Number costPerUse = NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 80)) / 100);
         Rate standardRate = new Rate(MPPUtility.getDouble(data, 36), TimeUnit.HOURS);
         Rate overtimeRate = new Rate(MPPUtility.getDouble(data, 44), TimeUnit.HOURS);

         CostRateTable costRateTable = new CostRateTable();
         costRateTable.add(new CostRateTableEntry(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, costPerUse, standardRate, overtimeRate));
         resource.setCostRateTable(0, costRateTable);

         LocalDateTime availableFrom = MPPUtility.getTimestamp(data, 28);
         LocalDateTime availableTo = MPPUtility.getTimestamp(data, 32);
         availableFrom = availableFrom == null ? LocalDateTimeHelper.START_DATE_NA : availableFrom;
         availableTo = availableTo == null ? LocalDateTimeHelper.END_DATE_NA : availableTo;
         resource.getAvailability().add(new Availability(availableFrom, availableTo, NumberHelper.getDouble(((double) ByteArrayHelper.getInt(data, 52)) / 100)));

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * This method extracts and collates resource assignment data.
    */
   private void processAssignmentData() throws IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry) m_projectDir.getEntry("TBkndAssn");
      FixFix assnFixedData = new FixFix(204, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixFix   0"))));
      if (assnFixedData.getDiff() != 0 || (assnFixedData.getSize() % 238 == 0 && !testAssignmentTasks(assnFixedData)))
      {
         assnFixedData = new FixFix(238, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixFix   0"))));
      }

      int count = assnFixedData.getItemCount();
      FixDeferFix assnVarData = null;

      for (int loop = 0; loop < count; loop++)
      {
         if (assnVarData == null)
         {
            assnVarData = new FixDeferFix(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixDeferFix   0"))));
         }

         byte[] data = assnFixedData.getByteArrayValue(loop);

         //
         // Check that the deleted flag isn't set
         //
         if (MPPUtility.getByte(data, 168) != 0x02)
         {
            Task task = m_file.getTaskByUniqueID(Integer.valueOf(ByteArrayHelper.getInt(data, 16)));
            Resource resource = m_file.getResourceByUniqueID(Integer.valueOf(ByteArrayHelper.getInt(data, 20)));

            if (task != null && resource != null)
            {
               ResourceAssignment assignment = task.getExistingResourceAssignment(resource);
               if (assignment == null)
               {
                  assignment = task.addResourceAssignment(resource);
               }

               assignment.setActualCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 138)) / 100));
               assignment.setActualWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 96)) / 100, TimeUnit.HOURS));
               assignment.setCost(NumberHelper.getDouble(((double) MPPUtility.getLong6(data, 132)) / 100));
               //assignment.setDelay(); // Not sure what this field maps on to in MSP
               assignment.setFinish(MPPUtility.getTimestamp(data, 28));
               assignment.setOvertimeWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 90)) / 100, TimeUnit.HOURS));
               //assignment.setPlannedCost(); // Not sure what this field maps on to in MSP
               //assignment.setPlannedWork(); // Not sure what this field maps on to in MSP
               assignment.setRemainingWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 114)) / 100, TimeUnit.HOURS));
               assignment.setStart(MPPUtility.getTimestamp(data, 24));
               assignment.setUniqueID(Integer.valueOf(ByteArrayHelper.getInt(data, 0)));
               assignment.setUnits(Double.valueOf(((double) ByteArrayHelper.getShort(data, 80)) / 100));
               assignment.setWork(MPPUtility.getDuration(((double) MPPUtility.getLong6(data, 84)) / 100, TimeUnit.HOURS));

               m_eventManager.fireAssignmentReadEvent(assignment);
            }
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
    * @param assnFixedData Task assignment fixed data
    * @return boolean flag
    */
   private boolean testAssignmentTasks(FixFix assnFixedData)
   {
      boolean result = true;
      int count = assnFixedData.getItemCount();
      byte[] data;
      Task task;
      Resource resource;

      for (int loop = 0; loop < count; loop++)
      {
         data = assnFixedData.getByteArrayValue(loop);
         task = m_file.getTaskByUniqueID(Integer.valueOf(ByteArrayHelper.getInt(data, 16)));
         resource = m_file.getResourceByUniqueID(Integer.valueOf(ByteArrayHelper.getInt(data, 20)));

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
    */
   private void processViewData() throws IOException
   {
      if (m_viewDir.hasEntry("CV_iew"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CV_iew");
         FixFix ff = new FixFix(138, new DocumentInputStream(((DocumentEntry) dir.getEntry("FixFix   0"))));
         int items = ff.getItemCount();
         byte[] data;
         View view;

         for (int loop = 0; loop < items; loop++)
         {
            data = ff.getByteArrayValue(loop);
            view = new View8(m_file, data);
            m_file.getViews().add(view);
         }
      }
   }

   /**
    * This method extracts table data from the MPP file.
    */
   private void processTableData() throws IOException
   {
      if (m_viewDir.hasEntry("CTable"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CTable");
         FixFix ff = new FixFix(126, new DocumentInputStream(((DocumentEntry) dir.getEntry("FixFix   0"))));
         FixDeferFix fdf = new FixDeferFix(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixDeferFix   0"))));
         int items = ff.getItemCount();
         StringBuilder sb = new StringBuilder();
         TableContainer container = m_file.getTables();

         for (int loop = 0; loop < items; loop++)
         {
            byte[] data = ff.getByteArrayValue(loop);
            Table table = new Table();

            table.setID(ByteArrayHelper.getInt(data, 0));

            String name = MPPUtility.getUnicodeString(data, 4);
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

            table.setName(MPPUtility.removeAmpersands(name));
            container.add(table);

            byte[] extendedData = fdf.getByteArray(getOffset(data, 122));
            if (extendedData != null)
            {
               byte[] columnData = fdf.getByteArray(getOffset(extendedData, 8));
               processColumnData(table, columnData);
            }

            //System.out.println(table);
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
   private void processColumnData(Table table, byte[] data)
   {
      int columnCount = ByteArrayHelper.getShort(data, 4) + 1;
      int index = 8;
      int columnTitleOffset;
      Column column;
      int alignment;

      for (int loop = 0; loop < columnCount; loop++)
      {
         column = new Column(m_file);

         if (loop == 0)
         {
            table.setResourceFlag(ByteArrayHelper.getShort(data, index) == 0);
         }

         column.setFieldType(FieldTypeHelper.getInstance(m_file, ByteArrayHelper.getInt(data, index)));
         column.setWidth(MPPUtility.getByte(data, index + 4));

         columnTitleOffset = ByteArrayHelper.getShort(data, index + 6);
         if (columnTitleOffset != 0)
         {
            column.setTitle(MPPUtility.getUnicodeString(data, columnTitleOffset));
         }

         alignment = MPPUtility.getByte(data, index + 8);
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

         alignment = MPPUtility.getByte(data, index + 10);
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
   private int getOffset(byte[] data, int offset)
   {
      return (-1 - ByteArrayHelper.getInt(data, offset));
   }

   //   private static void dumpUnknownData (String name, int[][] spec, byte[] data)
   //   {
   //      System.out.println (name);
   //      for (int loop=0; loop < spec.length; loop++)
   //      {
   //         System.out.println (spec[loop][0] + ": "+ ByteArrayHelper.hexdump(data, spec[loop][0], spec[loop][1], false));
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

   private ProjectFile m_file;
   private EventManager m_eventManager;
   private HashMap<Integer, ProjectCalendar> m_calendarMap;
   private DirectoryEntry m_root;
   private DirectoryEntry m_projectDir;
   private DirectoryEntry m_viewDir;
   private Props8 m_projectProps;

   /**
    * Task data types.
    */
   private static final Integer TASK_WBS = Integer.valueOf(104);
   private static final Integer TASK_CONTACT = Integer.valueOf(105);

   private static final Integer TASK_TEXT1 = Integer.valueOf(106);
   private static final Integer TASK_TEXT2 = Integer.valueOf(107);
   private static final Integer TASK_TEXT3 = Integer.valueOf(108);
   private static final Integer TASK_TEXT4 = Integer.valueOf(109);
   private static final Integer TASK_TEXT5 = Integer.valueOf(110);
   private static final Integer TASK_TEXT6 = Integer.valueOf(111);
   private static final Integer TASK_TEXT7 = Integer.valueOf(112);
   private static final Integer TASK_TEXT8 = Integer.valueOf(113);
   private static final Integer TASK_TEXT9 = Integer.valueOf(114);
   private static final Integer TASK_TEXT10 = Integer.valueOf(115);

   private static final Integer TASK_START1 = Integer.valueOf(116);
   private static final Integer TASK_FINISH1 = Integer.valueOf(117);
   private static final Integer TASK_START2 = Integer.valueOf(118);
   private static final Integer TASK_FINISH2 = Integer.valueOf(119);
   private static final Integer TASK_START3 = Integer.valueOf(120);
   private static final Integer TASK_FINISH3 = Integer.valueOf(121);
   private static final Integer TASK_START4 = Integer.valueOf(122);
   private static final Integer TASK_FINISH4 = Integer.valueOf(123);
   private static final Integer TASK_START5 = Integer.valueOf(124);
   private static final Integer TASK_FINISH5 = Integer.valueOf(125);
   private static final Integer TASK_START6 = Integer.valueOf(126);
   private static final Integer TASK_FINISH6 = Integer.valueOf(127);
   private static final Integer TASK_START7 = Integer.valueOf(128);
   private static final Integer TASK_FINISH7 = Integer.valueOf(129);
   private static final Integer TASK_START8 = Integer.valueOf(130);
   private static final Integer TASK_FINISH8 = Integer.valueOf(131);
   private static final Integer TASK_START9 = Integer.valueOf(132);
   private static final Integer TASK_FINISH9 = Integer.valueOf(133);
   private static final Integer TASK_START10 = Integer.valueOf(134);
   private static final Integer TASK_FINISH10 = Integer.valueOf(135);

   private static final Integer TASK_NUMBER1 = Integer.valueOf(137);
   private static final Integer TASK_NUMBER2 = Integer.valueOf(138);
   private static final Integer TASK_NUMBER3 = Integer.valueOf(139);
   private static final Integer TASK_NUMBER4 = Integer.valueOf(140);
   private static final Integer TASK_NUMBER5 = Integer.valueOf(141);
   private static final Integer TASK_NUMBER6 = Integer.valueOf(142);
   private static final Integer TASK_NUMBER7 = Integer.valueOf(143);
   private static final Integer TASK_NUMBER8 = Integer.valueOf(144);
   private static final Integer TASK_NUMBER9 = Integer.valueOf(145);
   private static final Integer TASK_NUMBER10 = Integer.valueOf(146);

   private static final Integer TASK_DURATION1 = Integer.valueOf(147);
   private static final Integer TASK_DURATION1_UNITS = Integer.valueOf(148);
   private static final Integer TASK_DURATION2 = Integer.valueOf(149);
   private static final Integer TASK_DURATION2_UNITS = Integer.valueOf(150);
   private static final Integer TASK_DURATION3 = Integer.valueOf(151);
   private static final Integer TASK_DURATION3_UNITS = Integer.valueOf(152);
   private static final Integer TASK_DURATION4 = Integer.valueOf(153);
   private static final Integer TASK_DURATION4_UNITS = Integer.valueOf(154);
   private static final Integer TASK_DURATION5 = Integer.valueOf(155);
   private static final Integer TASK_DURATION5_UNITS = Integer.valueOf(156);
   private static final Integer TASK_DURATION6 = Integer.valueOf(157);
   private static final Integer TASK_DURATION6_UNITS = Integer.valueOf(158);
   private static final Integer TASK_DURATION7 = Integer.valueOf(159);
   private static final Integer TASK_DURATION7_UNITS = Integer.valueOf(160);
   private static final Integer TASK_DURATION8 = Integer.valueOf(161);
   private static final Integer TASK_DURATION8_UNITS = Integer.valueOf(162);
   private static final Integer TASK_DURATION9 = Integer.valueOf(163);
   private static final Integer TASK_DURATION9_UNITS = Integer.valueOf(164);
   private static final Integer TASK_DURATION10 = Integer.valueOf(165);
   private static final Integer TASK_DURATION10_UNITS = Integer.valueOf(166);

   private static final Integer TASK_RECURRING_DATA = Integer.valueOf(168);

   private static final Integer TASK_DATE1 = Integer.valueOf(174);
   private static final Integer TASK_DATE2 = Integer.valueOf(175);
   private static final Integer TASK_DATE3 = Integer.valueOf(176);
   private static final Integer TASK_DATE4 = Integer.valueOf(177);
   private static final Integer TASK_DATE5 = Integer.valueOf(178);
   private static final Integer TASK_DATE6 = Integer.valueOf(179);
   private static final Integer TASK_DATE7 = Integer.valueOf(180);
   private static final Integer TASK_DATE8 = Integer.valueOf(181);
   private static final Integer TASK_DATE9 = Integer.valueOf(182);
   private static final Integer TASK_DATE10 = Integer.valueOf(183);

   private static final Integer TASK_TEXT11 = Integer.valueOf(184);
   private static final Integer TASK_TEXT12 = Integer.valueOf(185);
   private static final Integer TASK_TEXT13 = Integer.valueOf(186);
   private static final Integer TASK_TEXT14 = Integer.valueOf(187);
   private static final Integer TASK_TEXT15 = Integer.valueOf(188);
   private static final Integer TASK_TEXT16 = Integer.valueOf(189);
   private static final Integer TASK_TEXT17 = Integer.valueOf(190);
   private static final Integer TASK_TEXT18 = Integer.valueOf(191);
   private static final Integer TASK_TEXT19 = Integer.valueOf(192);
   private static final Integer TASK_TEXT20 = Integer.valueOf(193);
   private static final Integer TASK_TEXT21 = Integer.valueOf(194);
   private static final Integer TASK_TEXT22 = Integer.valueOf(195);
   private static final Integer TASK_TEXT23 = Integer.valueOf(196);
   private static final Integer TASK_TEXT24 = Integer.valueOf(197);
   private static final Integer TASK_TEXT25 = Integer.valueOf(198);
   private static final Integer TASK_TEXT26 = Integer.valueOf(199);
   private static final Integer TASK_TEXT27 = Integer.valueOf(200);
   private static final Integer TASK_TEXT28 = Integer.valueOf(201);
   private static final Integer TASK_TEXT29 = Integer.valueOf(202);
   private static final Integer TASK_TEXT30 = Integer.valueOf(203);

   private static final Integer TASK_NUMBER11 = Integer.valueOf(204);
   private static final Integer TASK_NUMBER12 = Integer.valueOf(205);
   private static final Integer TASK_NUMBER13 = Integer.valueOf(206);
   private static final Integer TASK_NUMBER14 = Integer.valueOf(207);
   private static final Integer TASK_NUMBER15 = Integer.valueOf(208);
   private static final Integer TASK_NUMBER16 = Integer.valueOf(209);
   private static final Integer TASK_NUMBER17 = Integer.valueOf(210);
   private static final Integer TASK_NUMBER18 = Integer.valueOf(211);
   private static final Integer TASK_NUMBER19 = Integer.valueOf(212);
   private static final Integer TASK_NUMBER20 = Integer.valueOf(213);

   private static final Integer TASK_HYPERLINK = Integer.valueOf(236);

   private static final Integer TASK_COST1 = Integer.valueOf(237);
   private static final Integer TASK_COST2 = Integer.valueOf(238);
   private static final Integer TASK_COST3 = Integer.valueOf(239);
   private static final Integer TASK_COST4 = Integer.valueOf(240);
   private static final Integer TASK_COST5 = Integer.valueOf(241);
   private static final Integer TASK_COST6 = Integer.valueOf(242);
   private static final Integer TASK_COST7 = Integer.valueOf(243);
   private static final Integer TASK_COST8 = Integer.valueOf(244);
   private static final Integer TASK_COST9 = Integer.valueOf(245);
   private static final Integer TASK_COST10 = Integer.valueOf(246);

   private static final Integer TASK_NOTES = Integer.valueOf(247);

   /**
    * Resource data types.
    */
   private static final Integer RESOURCE_GROUP = Integer.valueOf(61);
   private static final Integer RESOURCE_CODE = Integer.valueOf(62);
   private static final Integer RESOURCE_EMAIL = Integer.valueOf(63);

   private static final Integer RESOURCE_TEXT1 = Integer.valueOf(64);
   private static final Integer RESOURCE_TEXT2 = Integer.valueOf(65);
   private static final Integer RESOURCE_TEXT3 = Integer.valueOf(66);
   private static final Integer RESOURCE_TEXT4 = Integer.valueOf(67);
   private static final Integer RESOURCE_TEXT5 = Integer.valueOf(68);
   private static final Integer RESOURCE_TEXT6 = Integer.valueOf(69);
   private static final Integer RESOURCE_TEXT7 = Integer.valueOf(70);
   private static final Integer RESOURCE_TEXT8 = Integer.valueOf(71);
   private static final Integer RESOURCE_TEXT9 = Integer.valueOf(72);
   private static final Integer RESOURCE_TEXT10 = Integer.valueOf(73);
   private static final Integer RESOURCE_TEXT11 = Integer.valueOf(74);
   private static final Integer RESOURCE_TEXT12 = Integer.valueOf(75);
   private static final Integer RESOURCE_TEXT13 = Integer.valueOf(76);
   private static final Integer RESOURCE_TEXT14 = Integer.valueOf(77);
   private static final Integer RESOURCE_TEXT15 = Integer.valueOf(78);
   private static final Integer RESOURCE_TEXT16 = Integer.valueOf(79);
   private static final Integer RESOURCE_TEXT17 = Integer.valueOf(80);
   private static final Integer RESOURCE_TEXT18 = Integer.valueOf(81);
   private static final Integer RESOURCE_TEXT19 = Integer.valueOf(82);
   private static final Integer RESOURCE_TEXT20 = Integer.valueOf(83);
   private static final Integer RESOURCE_TEXT21 = Integer.valueOf(84);
   private static final Integer RESOURCE_TEXT22 = Integer.valueOf(85);
   private static final Integer RESOURCE_TEXT23 = Integer.valueOf(86);
   private static final Integer RESOURCE_TEXT24 = Integer.valueOf(87);
   private static final Integer RESOURCE_TEXT25 = Integer.valueOf(88);
   private static final Integer RESOURCE_TEXT26 = Integer.valueOf(89);
   private static final Integer RESOURCE_TEXT27 = Integer.valueOf(90);
   private static final Integer RESOURCE_TEXT28 = Integer.valueOf(91);
   private static final Integer RESOURCE_TEXT29 = Integer.valueOf(92);
   private static final Integer RESOURCE_TEXT30 = Integer.valueOf(93);

   private static final Integer RESOURCE_START1 = Integer.valueOf(94);
   private static final Integer RESOURCE_START2 = Integer.valueOf(95);
   private static final Integer RESOURCE_START3 = Integer.valueOf(96);
   private static final Integer RESOURCE_START4 = Integer.valueOf(97);
   private static final Integer RESOURCE_START5 = Integer.valueOf(98);
   private static final Integer RESOURCE_START6 = Integer.valueOf(99);
   private static final Integer RESOURCE_START7 = Integer.valueOf(100);
   private static final Integer RESOURCE_START8 = Integer.valueOf(101);
   private static final Integer RESOURCE_START9 = Integer.valueOf(102);
   private static final Integer RESOURCE_START10 = Integer.valueOf(103);

   private static final Integer RESOURCE_FINISH1 = Integer.valueOf(104);
   private static final Integer RESOURCE_FINISH2 = Integer.valueOf(105);
   private static final Integer RESOURCE_FINISH3 = Integer.valueOf(106);
   private static final Integer RESOURCE_FINISH4 = Integer.valueOf(107);
   private static final Integer RESOURCE_FINISH5 = Integer.valueOf(108);
   private static final Integer RESOURCE_FINISH6 = Integer.valueOf(109);
   private static final Integer RESOURCE_FINISH7 = Integer.valueOf(110);
   private static final Integer RESOURCE_FINISH8 = Integer.valueOf(111);
   private static final Integer RESOURCE_FINISH9 = Integer.valueOf(112);
   private static final Integer RESOURCE_FINISH10 = Integer.valueOf(113);

   private static final Integer RESOURCE_NUMBER1 = Integer.valueOf(114);
   private static final Integer RESOURCE_NUMBER2 = Integer.valueOf(115);
   private static final Integer RESOURCE_NUMBER3 = Integer.valueOf(116);
   private static final Integer RESOURCE_NUMBER4 = Integer.valueOf(117);
   private static final Integer RESOURCE_NUMBER5 = Integer.valueOf(118);
   private static final Integer RESOURCE_NUMBER6 = Integer.valueOf(119);
   private static final Integer RESOURCE_NUMBER7 = Integer.valueOf(120);
   private static final Integer RESOURCE_NUMBER8 = Integer.valueOf(121);
   private static final Integer RESOURCE_NUMBER9 = Integer.valueOf(122);
   private static final Integer RESOURCE_NUMBER10 = Integer.valueOf(123);
   private static final Integer RESOURCE_NUMBER11 = Integer.valueOf(124);
   private static final Integer RESOURCE_NUMBER12 = Integer.valueOf(125);
   private static final Integer RESOURCE_NUMBER13 = Integer.valueOf(126);
   private static final Integer RESOURCE_NUMBER14 = Integer.valueOf(127);
   private static final Integer RESOURCE_NUMBER15 = Integer.valueOf(128);
   private static final Integer RESOURCE_NUMBER16 = Integer.valueOf(129);
   private static final Integer RESOURCE_NUMBER17 = Integer.valueOf(130);
   private static final Integer RESOURCE_NUMBER18 = Integer.valueOf(131);
   private static final Integer RESOURCE_NUMBER19 = Integer.valueOf(132);
   private static final Integer RESOURCE_NUMBER20 = Integer.valueOf(133);

   private static final Integer RESOURCE_DURATION1 = Integer.valueOf(134);
   private static final Integer RESOURCE_DURATION2 = Integer.valueOf(135);
   private static final Integer RESOURCE_DURATION3 = Integer.valueOf(136);
   private static final Integer RESOURCE_DURATION4 = Integer.valueOf(137);
   private static final Integer RESOURCE_DURATION5 = Integer.valueOf(138);
   private static final Integer RESOURCE_DURATION6 = Integer.valueOf(139);
   private static final Integer RESOURCE_DURATION7 = Integer.valueOf(140);
   private static final Integer RESOURCE_DURATION8 = Integer.valueOf(141);
   private static final Integer RESOURCE_DURATION9 = Integer.valueOf(142);
   private static final Integer RESOURCE_DURATION10 = Integer.valueOf(143);

   private static final Integer RESOURCE_DURATION1_UNITS = Integer.valueOf(144);
   private static final Integer RESOURCE_DURATION2_UNITS = Integer.valueOf(145);
   private static final Integer RESOURCE_DURATION3_UNITS = Integer.valueOf(146);
   private static final Integer RESOURCE_DURATION4_UNITS = Integer.valueOf(147);
   private static final Integer RESOURCE_DURATION5_UNITS = Integer.valueOf(148);
   private static final Integer RESOURCE_DURATION6_UNITS = Integer.valueOf(149);
   private static final Integer RESOURCE_DURATION7_UNITS = Integer.valueOf(150);
   private static final Integer RESOURCE_DURATION8_UNITS = Integer.valueOf(151);
   private static final Integer RESOURCE_DURATION9_UNITS = Integer.valueOf(152);
   private static final Integer RESOURCE_DURATION10_UNITS = Integer.valueOf(153);

   private static final Integer RESOURCE_DATE1 = Integer.valueOf(157);
   private static final Integer RESOURCE_DATE2 = Integer.valueOf(158);
   private static final Integer RESOURCE_DATE3 = Integer.valueOf(159);
   private static final Integer RESOURCE_DATE4 = Integer.valueOf(160);
   private static final Integer RESOURCE_DATE5 = Integer.valueOf(161);
   private static final Integer RESOURCE_DATE6 = Integer.valueOf(162);
   private static final Integer RESOURCE_DATE7 = Integer.valueOf(163);
   private static final Integer RESOURCE_DATE8 = Integer.valueOf(164);
   private static final Integer RESOURCE_DATE9 = Integer.valueOf(165);
   private static final Integer RESOURCE_DATE10 = Integer.valueOf(166);

   private static final Integer RESOURCE_NOTES = Integer.valueOf(169);

   private static final Integer RESOURCE_COST1 = Integer.valueOf(170);
   private static final Integer RESOURCE_COST2 = Integer.valueOf(171);
   private static final Integer RESOURCE_COST3 = Integer.valueOf(172);
   private static final Integer RESOURCE_COST4 = Integer.valueOf(173);
   private static final Integer RESOURCE_COST5 = Integer.valueOf(174);
   private static final Integer RESOURCE_COST6 = Integer.valueOf(175);
   private static final Integer RESOURCE_COST7 = Integer.valueOf(176);
   private static final Integer RESOURCE_COST8 = Integer.valueOf(177);
   private static final Integer RESOURCE_COST9 = Integer.valueOf(178);
   private static final Integer RESOURCE_COST10 = Integer.valueOf(179);

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
