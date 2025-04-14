/*
 * file:       FastTrackReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2017
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

package org.mpxj.fasttrack;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.UnitOfMeasureContainer;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.reader.AbstractProjectFileReader;

// TODO:
// 1. Handle multiple bars per activity
// 2. Parse the Task "created" attribute
// 3. Resource rates
// 4. Task and resource hyperlinks
// 5. Project header data
// 6. Calendars
// 7. Handle resources with embedded commas in their names
// 8. Parse the task and resource binary data blocks

/**
 * Reads FastTrack FTS files.
 */
public final class FastTrackReader extends AbstractProjectFileReader
{
   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         m_data = FastTrackData.getInstance();
         // Uncomment this to write debug data to a log file
         //m_data.setLogFile("c:/temp/project1.txt");
         m_data.process(file);
         if (!m_data.getSupported())
         {
            return null;
         }
         return read();
      }
      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
      finally
      {
         m_data = null;
         FastTrackData.clearInstance();
      }
   }

   /**
    * Read FTS file data from the configured source and return a populated ProjectFile instance.
    *
    * @return ProjectFile instance
    */
   private ProjectFile read()
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoTaskID(false);
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoWBS(false);
      config.setAutoOutlineNumber(false);

      m_project.getProjectProperties().setFileApplication("FastTrack");
      m_project.getProjectProperties().setFileType("FTS");

      addListenersToProject(m_project);

      // processProject();
      processCalendars();
      processResources();
      processTasks();
      processDependencies();
      processAssignments();
      rollupValues();
      m_project.readComplete();

      return m_project;
   }

   private void processCalendars()
   {
      ProjectCalendar defaultCalendar = m_project.addDefaultBaseCalendar();
      m_project.getProjectProperties().setDefaultCalendar(defaultCalendar);
   }

   /**
    * Process resource data.
    */
   private void processResources()
   {
      FastTrackTable table = m_data.getTable(FastTrackTableType.RESOURCES);
      UnitOfMeasureContainer uom = m_project.getUnitsOfMeasure();

      for (MapRow row : table)
      {
         int uniqueID = row.getInt(ResourceField.RESOURCE_ID);
         if (uniqueID <= 0)
         {
            continue;
         }

         Resource resource = m_project.addResource();
         resource.setCode(row.getString(ResourceField.CODE));
         resource.setEmailAddress(row.getString(ResourceField.EMAIL_ADDRESS));
         resource.setFlag(1, row.getBoolean(ResourceField.FLAG_1));
         resource.setFlag(2, row.getBoolean(ResourceField.FLAG_2));
         resource.setFlag(3, row.getBoolean(ResourceField.FLAG_3));
         resource.setFlag(4, row.getBoolean(ResourceField.FLAG_4));
         resource.setFlag(5, row.getBoolean(ResourceField.FLAG_5));
         resource.setFlag(6, row.getBoolean(ResourceField.FLAG_6));
         resource.setFlag(7, row.getBoolean(ResourceField.FLAG_7));
         resource.setFlag(8, row.getBoolean(ResourceField.FLAG_8));
         resource.setFlag(9, row.getBoolean(ResourceField.FLAG_9));
         resource.setFlag(10, row.getBoolean(ResourceField.FLAG_10));
         resource.setFlag(11, row.getBoolean(ResourceField.FLAG_11));
         resource.setFlag(12, row.getBoolean(ResourceField.FLAG_12));
         resource.setFlag(13, row.getBoolean(ResourceField.FLAG_13));
         resource.setFlag(14, row.getBoolean(ResourceField.FLAG_14));
         resource.setFlag(15, row.getBoolean(ResourceField.FLAG_15));
         resource.setFlag(16, row.getBoolean(ResourceField.FLAG_16));
         resource.setFlag(17, row.getBoolean(ResourceField.FLAG_17));
         resource.setFlag(18, row.getBoolean(ResourceField.FLAG_18));
         resource.setFlag(19, row.getBoolean(ResourceField.FLAG_19));
         resource.setFlag(20, row.getBoolean(ResourceField.FLAG_20));
         resource.setGroup(row.getString(ResourceField.GROUP));
         resource.setGUID(row.getUUID(ResourceField._RESOURCE_GUID));
         resource.setInitials(row.getString(ResourceField.INITIALS));
         resource.setUnitOfMeasure(uom.getOrCreateByAbbreviation(row.getString(ResourceField.MATERIAL_LABEL)));
         resource.setName(row.getString(ResourceField.RESOURCE_NAME));
         resource.setNotes(row.getString(ResourceField.RESOURCE_NOTES));
         resource.setNumber(1, row.getDouble(ResourceField.NUMBER_1));
         resource.setNumber(2, row.getDouble(ResourceField.NUMBER_2));
         resource.setNumber(3, row.getDouble(ResourceField.NUMBER_3));
         resource.setNumber(4, row.getDouble(ResourceField.NUMBER_4));
         resource.setNumber(5, row.getDouble(ResourceField.NUMBER_5));
         resource.setNumber(6, row.getDouble(ResourceField.NUMBER_6));
         resource.setNumber(7, row.getDouble(ResourceField.NUMBER_7));
         resource.setNumber(8, row.getDouble(ResourceField.NUMBER_8));
         resource.setNumber(9, row.getDouble(ResourceField.NUMBER_9));
         resource.setNumber(10, row.getDouble(ResourceField.NUMBER_10));
         resource.setNumber(11, row.getDouble(ResourceField.NUMBER_11));
         resource.setNumber(12, row.getDouble(ResourceField.NUMBER_12));
         resource.setNumber(13, row.getDouble(ResourceField.NUMBER_13));
         resource.setNumber(14, row.getDouble(ResourceField.NUMBER_14));
         resource.setNumber(15, row.getDouble(ResourceField.NUMBER_15));
         resource.setNumber(16, row.getDouble(ResourceField.NUMBER_16));
         resource.setNumber(17, row.getDouble(ResourceField.NUMBER_17));
         resource.setNumber(18, row.getDouble(ResourceField.NUMBER_18));
         resource.setNumber(19, row.getDouble(ResourceField.NUMBER_19));
         resource.setNumber(20, row.getDouble(ResourceField.NUMBER_20));
         resource.setText(1, row.getString(ResourceField.TEXT_1));
         resource.setText(2, row.getString(ResourceField.TEXT_2));
         resource.setText(3, row.getString(ResourceField.TEXT_3));
         resource.setText(4, row.getString(ResourceField.TEXT_4));
         resource.setText(5, row.getString(ResourceField.TEXT_5));
         resource.setText(6, row.getString(ResourceField.TEXT_6));
         resource.setText(7, row.getString(ResourceField.TEXT_7));
         resource.setText(8, row.getString(ResourceField.TEXT_8));
         resource.setText(9, row.getString(ResourceField.TEXT_9));
         resource.setText(10, row.getString(ResourceField.TEXT_10));
         resource.setText(11, row.getString(ResourceField.TEXT_11));
         resource.setText(12, row.getString(ResourceField.TEXT_12));
         resource.setText(13, row.getString(ResourceField.TEXT_13));
         resource.setText(14, row.getString(ResourceField.TEXT_14));
         resource.setText(15, row.getString(ResourceField.TEXT_15));
         resource.setText(16, row.getString(ResourceField.TEXT_16));
         resource.setText(17, row.getString(ResourceField.TEXT_17));
         resource.setText(18, row.getString(ResourceField.TEXT_18));
         resource.setText(19, row.getString(ResourceField.TEXT_19));
         resource.setText(20, row.getString(ResourceField.TEXT_20));
         resource.setText(21, row.getString(ResourceField.TEXT_21));
         resource.setText(22, row.getString(ResourceField.TEXT_22));
         resource.setText(23, row.getString(ResourceField.TEXT_23));
         resource.setText(24, row.getString(ResourceField.TEXT_24));
         resource.setText(25, row.getString(ResourceField.TEXT_25));
         resource.setText(26, row.getString(ResourceField.TEXT_26));
         resource.setText(27, row.getString(ResourceField.TEXT_27));
         resource.setText(28, row.getString(ResourceField.TEXT_28));
         resource.setText(29, row.getString(ResourceField.TEXT_29));
         resource.setText(30, row.getString(ResourceField.TEXT_30));
         resource.setUniqueID(Integer.valueOf(uniqueID));

         CostRateTable costRateTable = new CostRateTable();
         costRateTable.add(new CostRateTableEntry(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, row.getCurrency(ResourceField.PER_USE_COST)));
         resource.setCostRateTable(0, costRateTable);

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Process task data.
    */
   private void processTasks()
   {
      int uniqueID = 0;

      FastTrackTable activities = m_data.getTable(FastTrackTableType.ACTIVITIES);
      for (MapRow row : activities)
      {
         ++uniqueID;

         Integer id = row.getInteger(ActivityField.ACTIVITY_ROW_ID);
         if (id == null || id.intValue() < 1)
         {
            continue;
         }

         Task task = m_project.addTask();
         task.setName(row.getString(ActivityField.ACTIVITY_NAME));
         task.setID(id);
         task.setUniqueID(Integer.valueOf(uniqueID));
         //  Activity Row Number
         task.setFlag(1, row.getBoolean(ActivityField.FLAG_1));
         task.setFlag(2, row.getBoolean(ActivityField.FLAG_2));
         task.setFlag(3, row.getBoolean(ActivityField.FLAG_3));
         task.setFlag(4, row.getBoolean(ActivityField.FLAG_4));
         task.setFlag(5, row.getBoolean(ActivityField.FLAG_5));
         task.setFlag(6, row.getBoolean(ActivityField.FLAG_6));
         task.setFlag(7, row.getBoolean(ActivityField.FLAG_7));
         task.setFlag(8, row.getBoolean(ActivityField.FLAG_8));
         task.setFlag(9, row.getBoolean(ActivityField.FLAG_9));
         task.setFlag(10, row.getBoolean(ActivityField.FLAG_10));
         task.setFlag(11, row.getBoolean(ActivityField.FLAG_11));
         task.setFlag(12, row.getBoolean(ActivityField.FLAG_12));
         task.setFlag(13, row.getBoolean(ActivityField.FLAG_13));
         task.setFlag(14, row.getBoolean(ActivityField.FLAG_14));
         task.setFlag(15, row.getBoolean(ActivityField.FLAG_15));
         task.setFlag(16, row.getBoolean(ActivityField.FLAG_16));
         task.setFlag(17, row.getBoolean(ActivityField.FLAG_17));
         task.setFlag(18, row.getBoolean(ActivityField.FLAG_18));
         task.setFlag(19, row.getBoolean(ActivityField.FLAG_19));
         task.setFlag(20, row.getBoolean(ActivityField.FLAG_20));
         //   Parent Tree
         task.setText(1, row.getString(ActivityField.TEXT_1));
         task.setText(2, row.getString(ActivityField.TEXT_2));
         task.setText(3, row.getString(ActivityField.TEXT_3));
         task.setText(4, row.getString(ActivityField.TEXT_4));
         task.setText(5, row.getString(ActivityField.TEXT_5));
         task.setText(6, row.getString(ActivityField.TEXT_6));
         task.setText(7, row.getString(ActivityField.TEXT_7));
         task.setText(8, row.getString(ActivityField.TEXT_8));
         task.setText(9, row.getString(ActivityField.TEXT_9));
         task.setText(10, row.getString(ActivityField.TEXT_10));
         task.setText(11, row.getString(ActivityField.TEXT_11));
         task.setText(12, row.getString(ActivityField.TEXT_12));
         task.setText(13, row.getString(ActivityField.TEXT_13));
         task.setText(14, row.getString(ActivityField.TEXT_14));
         task.setText(15, row.getString(ActivityField.TEXT_15));
         task.setText(16, row.getString(ActivityField.TEXT_16));
         task.setText(17, row.getString(ActivityField.TEXT_17));
         task.setText(18, row.getString(ActivityField.TEXT_18));
         task.setText(19, row.getString(ActivityField.TEXT_19));
         task.setText(20, row.getString(ActivityField.TEXT_20));
         task.setText(21, row.getString(ActivityField.TEXT_21));
         task.setText(22, row.getString(ActivityField.TEXT_22));
         task.setText(23, row.getString(ActivityField.TEXT_23));
         task.setText(24, row.getString(ActivityField.TEXT_24));
         task.setText(25, row.getString(ActivityField.TEXT_25));
         task.setText(26, row.getString(ActivityField.TEXT_26));
         task.setText(27, row.getString(ActivityField.TEXT_27));
         task.setText(28, row.getString(ActivityField.TEXT_28));
         task.setText(29, row.getString(ActivityField.TEXT_29));
         task.setText(30, row.getString(ActivityField.TEXT_30));

         task.setWBS(row.getString(ActivityField.WBS));
         task.setGUID(row.getUUID(ActivityField._ACTIVITY_GUID));
         task.setOutlineLevel(getOutlineLevel(task));

         task.setNotes(row.getString(ActivityField.NOTES));

         m_eventManager.fireTaskReadEvent(task);
      }

      FastTrackTable table = m_data.getTable(FastTrackTableType.ACTBARS);
      Set<Task> tasksWithBars = new HashSet<>();

      for (MapRow row : table)
      {
         if (row.getInt(ActBarField.BAR_ID) < 1)
         {
            continue;
         }

         Task task = m_project.getTaskByUniqueID(row.getInteger(ActBarField._ACTIVITY));
         if (task == null || tasksWithBars.contains(task))
         {
            continue;
         }
         tasksWithBars.add(task);

         // % Used
         task.setActualDuration(row.getDuration(ActBarField.ACTUAL_DURATION));
         task.setActualFinish(row.getTimestamp(ActBarField.ACTUAL_FINISH_DATE, ActBarField.ACTUAL_FINISH_TIME));
         task.setActualStart(row.getTimestamp(ActBarField.ACTUAL_START_DATE, ActBarField.ACTUAL_START_TIME));
         // Attendees
         task.setBaselineCost(1, row.getCurrency(ActBarField.BASELINE_COST_1));
         task.setBaselineCost(2, row.getCurrency(ActBarField.BASELINE_COST_2));
         task.setBaselineCost(3, row.getCurrency(ActBarField.BASELINE_COST_3));
         task.setBaselineCost(4, row.getCurrency(ActBarField.BASELINE_COST_4));
         task.setBaselineCost(5, row.getCurrency(ActBarField.BASELINE_COST_5));
         task.setBaselineCost(6, row.getCurrency(ActBarField.BASELINE_COST_6));
         task.setBaselineCost(7, row.getCurrency(ActBarField.BASELINE_COST_7));
         task.setBaselineCost(8, row.getCurrency(ActBarField.BASELINE_COST_8));
         task.setBaselineCost(9, row.getCurrency(ActBarField.BASELINE_COST_9));
         task.setBaselineCost(10, row.getCurrency(ActBarField.BASELINE_COST_10));
         task.setBaselineDuration(1, row.getDuration(ActBarField.BASELINE_DURATION_1));
         task.setBaselineDuration(2, row.getDuration(ActBarField.BASELINE_DURATION_2));
         task.setBaselineDuration(3, row.getDuration(ActBarField.BASELINE_DURATION_3));
         task.setBaselineDuration(4, row.getDuration(ActBarField.BASELINE_DURATION_4));
         task.setBaselineDuration(5, row.getDuration(ActBarField.BASELINE_DURATION_5));
         task.setBaselineDuration(6, row.getDuration(ActBarField.BASELINE_DURATION_6));
         task.setBaselineDuration(7, row.getDuration(ActBarField.BASELINE_DURATION_7));
         task.setBaselineDuration(8, row.getDuration(ActBarField.BASELINE_DURATION_8));
         task.setBaselineDuration(9, row.getDuration(ActBarField.BASELINE_DURATION_9));
         task.setBaselineDuration(10, row.getDuration(ActBarField.BASELINE_DURATION_10));
         task.setBaselineFinish(1, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_1, ActBarField.BASELINE_FINISH_TIME_1));
         task.setBaselineFinish(2, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_2, ActBarField.BASELINE_FINISH_TIME_2));
         task.setBaselineFinish(3, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_3, ActBarField.BASELINE_FINISH_TIME_3));
         task.setBaselineFinish(4, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_4, ActBarField.BASELINE_FINISH_TIME_4));
         task.setBaselineFinish(5, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_5, ActBarField.BASELINE_FINISH_TIME_5));
         task.setBaselineFinish(6, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_6, ActBarField.BASELINE_FINISH_TIME_6));
         task.setBaselineFinish(7, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_7, ActBarField.BASELINE_FINISH_TIME_7));
         task.setBaselineFinish(8, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_8, ActBarField.BASELINE_FINISH_TIME_8));
         task.setBaselineFinish(9, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_9, ActBarField.BASELINE_FINISH_TIME_9));
         task.setBaselineFinish(10, row.getTimestamp(ActBarField.BASELINE_FINISH_DATE_10, ActBarField.BASELINE_FINISH_TIME_10));
         task.setBaselineStart(1, row.getTimestamp(ActBarField.BASELINE_START_DATE_1, ActBarField.BASELINE_START_TIME_1));
         task.setBaselineStart(2, row.getTimestamp(ActBarField.BASELINE_START_DATE_2, ActBarField.BASELINE_START_TIME_2));
         task.setBaselineStart(3, row.getTimestamp(ActBarField.BASELINE_START_DATE_3, ActBarField.BASELINE_START_TIME_3));
         task.setBaselineStart(4, row.getTimestamp(ActBarField.BASELINE_START_DATE_4, ActBarField.BASELINE_START_TIME_4));
         task.setBaselineStart(5, row.getTimestamp(ActBarField.BASELINE_START_DATE_5, ActBarField.BASELINE_START_TIME_5));
         task.setBaselineStart(6, row.getTimestamp(ActBarField.BASELINE_START_DATE_6, ActBarField.BASELINE_START_TIME_6));
         task.setBaselineStart(7, row.getTimestamp(ActBarField.BASELINE_START_DATE_7, ActBarField.BASELINE_START_TIME_7));
         task.setBaselineStart(8, row.getTimestamp(ActBarField.BASELINE_START_DATE_8, ActBarField.BASELINE_START_TIME_8));
         task.setBaselineStart(9, row.getTimestamp(ActBarField.BASELINE_START_DATE_9, ActBarField.BASELINE_START_TIME_9));
         task.setBaselineStart(10, row.getTimestamp(ActBarField.BASELINE_START_DATE_10, ActBarField.BASELINE_START_TIME_10));
         task.setBaselineWork(1, row.getWork(ActBarField.BASELINE_WORK_1));
         task.setBaselineWork(2, row.getWork(ActBarField.BASELINE_WORK_2));
         task.setBaselineWork(3, row.getWork(ActBarField.BASELINE_WORK_3));
         task.setBaselineWork(4, row.getWork(ActBarField.BASELINE_WORK_4));
         task.setBaselineWork(5, row.getWork(ActBarField.BASELINE_WORK_5));
         task.setBaselineWork(6, row.getWork(ActBarField.BASELINE_WORK_6));
         task.setBaselineWork(7, row.getWork(ActBarField.BASELINE_WORK_7));
         task.setBaselineWork(8, row.getWork(ActBarField.BASELINE_WORK_8));
         task.setBaselineWork(9, row.getWork(ActBarField.BASELINE_WORK_9));
         task.setBaselineWork(10, row.getWork(ActBarField.BASELINE_WORK_10));
         task.setConstraintDate(row.getTimestamp(ActBarField.CONSTRAINT_DATE, ActBarField.CONSTRAINT_TIME));
         task.setCost(1, row.getCurrency(ActBarField.COST_1));
         task.setCost(2, row.getCurrency(ActBarField.COST_2));
         task.setCost(3, row.getCurrency(ActBarField.COST_3));
         task.setCost(4, row.getCurrency(ActBarField.COST_4));
         task.setCost(5, row.getCurrency(ActBarField.COST_5));
         task.setCost(6, row.getCurrency(ActBarField.COST_6));
         task.setCost(7, row.getCurrency(ActBarField.COST_7));
         task.setCost(8, row.getCurrency(ActBarField.COST_8));
         task.setCost(9, row.getCurrency(ActBarField.COST_9));
         task.setCost(10, row.getCurrency(ActBarField.COST_10));
         // Created
         task.setDate(1, row.getTimestamp(ActBarField.DATE_1));
         task.setDate(2, row.getTimestamp(ActBarField.DATE_2));
         task.setDate(3, row.getTimestamp(ActBarField.DATE_3));
         task.setDate(4, row.getTimestamp(ActBarField.DATE_4));
         task.setDate(5, row.getTimestamp(ActBarField.DATE_5));
         task.setDate(6, row.getTimestamp(ActBarField.DATE_6));
         task.setDate(7, row.getTimestamp(ActBarField.DATE_7));
         task.setDate(8, row.getTimestamp(ActBarField.DATE_8));
         task.setDate(9, row.getTimestamp(ActBarField.DATE_9));
         task.setDate(10, row.getTimestamp(ActBarField.DATE_10));
         task.setBaselineDuration(row.getDuration(ActBarField.DURATION));
         task.setDuration(1, row.getDuration(ActBarField.DURATION_1));
         task.setDuration(2, row.getDuration(ActBarField.DURATION_2));
         task.setDuration(3, row.getDuration(ActBarField.DURATION_3));
         task.setDuration(4, row.getDuration(ActBarField.DURATION_4));
         task.setDuration(5, row.getDuration(ActBarField.DURATION_5));
         task.setDuration(6, row.getDuration(ActBarField.DURATION_6));
         task.setDuration(7, row.getDuration(ActBarField.DURATION_7));
         task.setDuration(8, row.getDuration(ActBarField.DURATION_8));
         task.setDuration(9, row.getDuration(ActBarField.DURATION_9));
         task.setDuration(10, row.getDuration(ActBarField.DURATION_10));
         task.setEarlyFinish(row.getTimestamp(ActBarField.EARLY_FINISH_DATE, ActBarField.EARLY_FINISH_TIME));
         task.setEarlyStart(row.getTimestamp(ActBarField.EARLY_START_DATE, ActBarField.EARLY_START_TIME));
         task.setEffortDriven(row.getBoolean(ActBarField.EFFORT_DRIVEN));
         task.setBaselineFinish(row.getTimestamp(ActBarField.FINISH_DATE, ActBarField.FINISH_TIME));
         task.setFinish(1, row.getTimestamp(ActBarField.FINISH_DATE_1, ActBarField.FINISH_TIME_1));
         task.setFinish(2, row.getTimestamp(ActBarField.FINISH_DATE_2, ActBarField.FINISH_TIME_2));
         task.setFinish(3, row.getTimestamp(ActBarField.FINISH_DATE_3, ActBarField.FINISH_TIME_3));
         task.setFinish(4, row.getTimestamp(ActBarField.FINISH_DATE_4, ActBarField.FINISH_TIME_4));
         task.setFinish(5, row.getTimestamp(ActBarField.FINISH_DATE_5, ActBarField.FINISH_TIME_5));
         task.setFinish(6, row.getTimestamp(ActBarField.FINISH_DATE_6, ActBarField.FINISH_TIME_6));
         task.setFinish(7, row.getTimestamp(ActBarField.FINISH_DATE_7, ActBarField.FINISH_TIME_7));
         task.setFinish(8, row.getTimestamp(ActBarField.FINISH_DATE_8, ActBarField.FINISH_TIME_8));
         task.setFinish(9, row.getTimestamp(ActBarField.FINISH_DATE_9, ActBarField.FINISH_TIME_9));
         task.setFinish(10, row.getTimestamp(ActBarField.FINISH_DATE_10, ActBarField.FINISH_TIME_10));
         task.setFixedCost(row.getCurrency(ActBarField.FIXED_COST));
         // Fixed Duration
         task.setIgnoreResourceCalendar(row.getBoolean(ActBarField.IGNORE_RESOURCE_CALENDARS));
         task.setLateFinish(row.getTimestamp(ActBarField.LATE_FINISH_DATE, ActBarField.LATE_FINISH_TIME));
         task.setLateStart(row.getTimestamp(ActBarField.LATE_START_DATE, ActBarField.LATE_START_TIME));
         task.setNumber(1, row.getDouble(ActBarField.NUMBER_1));
         task.setNumber(2, row.getDouble(ActBarField.NUMBER_2));
         task.setNumber(3, row.getDouble(ActBarField.NUMBER_3));
         task.setNumber(4, row.getDouble(ActBarField.NUMBER_4));
         task.setNumber(5, row.getDouble(ActBarField.NUMBER_5));
         task.setNumber(6, row.getDouble(ActBarField.NUMBER_6));
         task.setNumber(7, row.getDouble(ActBarField.NUMBER_7));
         task.setNumber(8, row.getDouble(ActBarField.NUMBER_8));
         task.setNumber(9, row.getDouble(ActBarField.NUMBER_9));
         task.setNumber(10, row.getDouble(ActBarField.NUMBER_10));
         task.setNumber(11, row.getDouble(ActBarField.NUMBER_11));
         task.setNumber(12, row.getDouble(ActBarField.NUMBER_12));
         task.setNumber(13, row.getDouble(ActBarField.NUMBER_13));
         task.setNumber(14, row.getDouble(ActBarField.NUMBER_14));
         task.setNumber(15, row.getDouble(ActBarField.NUMBER_15));
         task.setNumber(16, row.getDouble(ActBarField.NUMBER_16));
         task.setNumber(17, row.getDouble(ActBarField.NUMBER_17));
         task.setNumber(18, row.getDouble(ActBarField.NUMBER_18));
         task.setNumber(19, row.getDouble(ActBarField.NUMBER_19));
         task.setNumber(20, row.getDouble(ActBarField.NUMBER_20));
         task.setPercentageComplete(row.getDouble(ActBarField.PERCENT_COMPLETE));
         // Priority
         // Resource Cost
         task.setResourceNames(row.getString(ActBarField.RESOURCES_ASSIGNED));
         task.setDuration(row.getDuration(ActBarField.REVISED_DURATION));
         task.setFinish(row.getTimestamp(ActBarField.REVISED_FINISH_DATE, ActBarField.REVISED_FINISH_TIME));
         task.setStart(row.getTimestamp(ActBarField.REVISED_START_DATE, ActBarField.REVISED_START_TIME));
         task.setBaselineStart(row.getTimestamp(ActBarField.START_DATE, ActBarField.START_TIME));

         task.setStart(1, row.getTimestamp(ActBarField.START_DATE_1, ActBarField.START_TIME_1));
         task.setStart(2, row.getTimestamp(ActBarField.START_DATE_2, ActBarField.START_TIME_2));
         task.setStart(3, row.getTimestamp(ActBarField.START_DATE_3, ActBarField.START_TIME_3));
         task.setStart(4, row.getTimestamp(ActBarField.START_DATE_4, ActBarField.START_TIME_4));
         task.setStart(5, row.getTimestamp(ActBarField.START_DATE_5, ActBarField.START_TIME_5));
         task.setStart(6, row.getTimestamp(ActBarField.START_DATE_6, ActBarField.START_TIME_6));
         task.setStart(7, row.getTimestamp(ActBarField.START_DATE_7, ActBarField.START_TIME_7));
         task.setStart(8, row.getTimestamp(ActBarField.START_DATE_8, ActBarField.START_TIME_8));
         task.setStart(9, row.getTimestamp(ActBarField.START_DATE_9, ActBarField.START_TIME_9));
         task.setStart(10, row.getTimestamp(ActBarField.START_DATE_10, ActBarField.START_TIME_10));
         // Task Calendar
         // Total Cost
         // Total Resource Duration
         task.setWork(row.getWork(ActBarField.WORK));
         // _Activity
         // _BarBits
         // _BarStl
         // _yOffset

         if (task.getStart() == null)
         {
            task.setStart(task.getBaselineStart());
         }

         if (task.getFinish() == null)
         {
            task.setFinish(task.getBaselineFinish());
         }

         task.setStartSlack(row.getDuration(ActBarField.START_FLOAT));
         task.setFinishSlack(row.getDuration(ActBarField.FINISH_FLOAT));
         task.setFreeSlack(row.getDuration(ActBarField.FREE_FLOAT));
         task.setTotalSlack(row.getDuration(ActBarField.TOTAL_FLOAT));

         // The value in this field does not appear to be accurate.
         // Allowing the critical flag to be calculated produces
         // results which match FastTrack.
         //task.setCritical(row.getBoolean(ActBarField.CRITICAL));
      }

      m_project.updateStructure();
   }

   /**
    * Process task dependencies.
    */
   private void processDependencies()
   {
      Set<Task> tasksWithBars = new HashSet<>();
      FastTrackTable table = m_data.getTable(FastTrackTableType.ACTBARS);
      for (MapRow row : table)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger(ActBarField._ACTIVITY));
         if (task == null || tasksWithBars.contains(task))
         {
            continue;
         }
         tasksWithBars.add(task);

         String predecessors = row.getString(ActBarField.PREDECESSORS);
         if (predecessors == null || predecessors.isEmpty())
         {
            continue;
         }

         for (String predecessor : predecessors.split(", "))
         {
            Matcher matcher = RELATION_REGEX.matcher(predecessor);
            if (!matcher.matches())
            {
               continue;
            }

            Integer id = Integer.valueOf(matcher.group(1));
            RelationType type = RELATION_TYPE_MAP.getOrDefault(matcher.group(3), RelationType.FINISH_START);

            String sign = matcher.group(4);
            double lag = NumberHelper.getDouble(matcher.group(5));
            if ("-".equals(sign))
            {
               lag = -lag;
            }

            Task targetTask = m_project.getTaskByID(id);
            if (targetTask != null)
            {
               Duration lagDuration = Duration.getInstance(lag, m_data.getDurationTimeUnit());
               Relation relation = task.addPredecessor(new Relation.Builder()
                  .predecessorTask(targetTask)
                  .type(type)
                  .lag(lagDuration));
               m_eventManager.fireRelationReadEvent(relation);
            }
         }
      }
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments()
   {
      Set<Task> tasksWithBars = new HashSet<>();
      FastTrackTable table = m_data.getTable(FastTrackTableType.ACTBARS);
      Map<String, Resource> resources = new HashMap<>();
      for (Resource resource : m_project.getResources())
      {
         resources.put(resource.getName(), resource);
      }

      for (MapRow row : table)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger(ActBarField._ACTIVITY));
         if (task == null || tasksWithBars.contains(task))
         {
            continue;
         }
         tasksWithBars.add(task);

         String assignments = row.getString(ActBarField.RESOURCES_ASSIGNED);
         if (assignments == null || assignments.isEmpty())
         {
            continue;
         }

         for (String assignment : assignments.split(", "))
         {
            if (assignment.isEmpty())
            {
               continue;
            }

            Matcher matcher = ASSIGNMENT_REGEX.matcher(assignment);
            if (!matcher.matches())
            {
               continue;
            }

            Resource resource = resources.get(matcher.group(1));
            if (resource != null)
            {
               ResourceAssignment ra = task.addResourceAssignment(resource);
               String units = matcher.group(2);
               if (units != null)
               {
                  ra.setUnits(Integer.valueOf(units));
               }
               m_eventManager.fireAssignmentReadEvent(ra);
            }
         }
      }
   }

   /**
    * Extract the outline level from a task's WBS attribute.
    *
    * @param task Task instance
    * @return outline level
    */
   private Integer getOutlineLevel(Task task)
   {
      String value = task.getWBS();
      Integer result = Integer.valueOf(1);
      if (value != null && !value.isEmpty())
      {
         String[] path = WBS_SPLIT_REGEX.split(value);
         result = Integer.valueOf(path.length);
      }
      return result;
   }

   private void rollupValues()
   {
      m_project.getChildTasks().forEach(this::rollupDates);
   }

   private void rollupDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         int finished = 0;
         LocalDateTime startDate = parentTask.getStart();
         LocalDateTime finishDate = parentTask.getFinish();
         LocalDateTime actualStartDate = parentTask.getActualStart();
         LocalDateTime actualFinishDate = parentTask.getActualFinish();
         LocalDateTime earlyStartDate = parentTask.getEarlyStart();
         LocalDateTime earlyFinishDate = parentTask.getEarlyFinish();
         LocalDateTime lateStartDate = parentTask.getLateStart();
         LocalDateTime lateFinishDate = parentTask.getLateFinish();
         LocalDateTime baselineStartDate = parentTask.getBaselineStart();
         LocalDateTime baselineFinishDate = parentTask.getBaselineFinish();

         boolean critical = false;

         for (Task task : parentTask.getChildTasks())
         {
            rollupDates(task);

            startDate = LocalDateTimeHelper.min(startDate, task.getStart());
            finishDate = LocalDateTimeHelper.max(finishDate, task.getFinish());
            actualStartDate = LocalDateTimeHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = LocalDateTimeHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getEarlyFinish());
            lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getLateFinish());
            baselineStartDate = LocalDateTimeHelper.min(baselineStartDate, task.getBaselineStart());
            baselineFinishDate = LocalDateTimeHelper.max(baselineFinishDate, task.getBaselineFinish());

            if (task.getActualFinish() != null)
            {
               ++finished;
            }

            critical = critical || task.getCritical();
         }

         parentTask.setStart(startDate);
         parentTask.setFinish(finishDate);
         parentTask.setActualStart(actualStartDate);
         parentTask.setEarlyStart(earlyStartDate);
         parentTask.setEarlyFinish(earlyFinishDate);
         parentTask.setLateStart(lateStartDate);
         parentTask.setLateFinish(lateFinishDate);
         parentTask.setBaselineStart(baselineStartDate);
         parentTask.setBaselineFinish(baselineFinishDate);

         //
         // Only if all child tasks have actual finish dates do we
         // set the actual finish date on the parent task.
         //
         if (finished == parentTask.getChildTasks().size())
         {
            parentTask.setActualFinish(actualFinishDate);
            parentTask.setPercentageComplete(NumberHelper.getDouble(100.0));
            parentTask.setActualDuration(m_project.getDefaultCalendar().getWork(parentTask.getActualStart(), parentTask.getActualFinish(), TimeUnit.HOURS));
         }

         if (parentTask.getStart() != null && parentTask.getFinish() != null)
         {
            parentTask.setDuration(m_project.getDefaultCalendar().getWork(parentTask.getStart(), parentTask.getFinish(), TimeUnit.HOURS));
         }

         // Force total slack calculation to avoid overwriting the critical flag
         parentTask.getTotalSlack();
         parentTask.setCritical(critical);
      }
   }

   private FastTrackData m_data;
   private ProjectFile m_project;
   private EventManager m_eventManager;

   private static final Pattern WBS_SPLIT_REGEX = Pattern.compile("(\\.|-|\\+|/|,|:|;|~|\\\\|\\| )");
   private static final Pattern RELATION_REGEX = Pattern.compile("(\\d+)(:\\d+)?(FS|SF|SS|FF)*([-+])*(\\d+\\.\\d+)*");
   private static final Pattern ASSIGNMENT_REGEX = Pattern.compile("([^\\[]+)(?:\\[(-?\\d+)%]|\\[.+])?");

   private static final Map<String, RelationType> RELATION_TYPE_MAP = new HashMap<>();
   static
   {
      RELATION_TYPE_MAP.put("FS", RelationType.FINISH_START);
      RELATION_TYPE_MAP.put("FF", RelationType.FINISH_FINISH);
      RELATION_TYPE_MAP.put("SS", RelationType.START_START);
      RELATION_TYPE_MAP.put("SF", RelationType.START_FINISH);
   }
}
