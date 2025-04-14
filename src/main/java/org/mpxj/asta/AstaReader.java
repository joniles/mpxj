/*
 * file:       AstaReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       07/04/2011
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

package org.mpxj.asta;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeContainer;
import org.mpxj.ActivityCodeValue;
import org.mpxj.ActivityType;
import org.mpxj.Availability;
import org.mpxj.ChildTaskContainer;
import org.mpxj.ConstraintType;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.DataType;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.LocalDateRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarDays;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.Task;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.CustomFieldContainer;
import org.mpxj.UnitOfMeasureContainer;
import org.mpxj.UserDefinedField;
import org.mpxj.UserDefinedFieldContainer;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.LocalTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ObjectSequence;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
final class AstaReader
{
   /**
    * Constructor.
    */
   public AstaReader()
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();

      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoAssignmentUniqueID(false);
      config.setAutoCalendarUniqueID(false);
      config.setAutoRelationUniqueID(false);
      config.setBaselineStrategy(AstaBaselineStrategy.INSTANCE);

      m_project.getProjectProperties().setFileApplication("Asta");
      m_project.getProjectProperties().setFileType("PP");
   }

   /**
    * Retrieves the project data read from this file.
    *
    * @return project data
    */
   public ProjectFile getProject()
   {
      return m_project;
   }

   /**
    * Process project properties.
    *
    * @param schemaVersion schema version
    * @param projectSummary project properties data.
    * @param userSettings user settings
    * @param progressPeriods progress period data.
    */
   public void processProjectProperties(Integer schemaVersion, Row projectSummary, Row userSettings, List<Row> progressPeriods)
   {
      ProjectProperties ph = m_project.getProjectProperties();
      ph.setApplicationVersion(schemaVersion);
      final Integer currentProgressPeriodID;

      if (projectSummary != null)
      {
         ph.setDuration(projectSummary.getDuration("DURATION"));
         ph.setStartDate(projectSummary.getDate("PROJECT_START"));
         ph.setFinishDate(projectSummary.getDate("PROJECT_END"));
         ph.setName(projectSummary.getString("SHORT_NAME"));
         ph.setAuthor(projectSummary.getString("PROJECT_BY"));
         //DURATION_TIME_UNIT
         ph.setLastSaved(projectSummary.getDate("LAST_EDITED_DATE"));
      }

      if (userSettings == null)
      {
         currentProgressPeriodID = null;
      }
      else
      {
         currentProgressPeriodID = userSettings.getInteger("CURRENT_PROGRESS_PERIOD");
      }

      if (progressPeriods != null)
      {
         Row progressPeriod;
         if (currentProgressPeriodID == null)
         {
            progressPeriods.sort(Comparator.comparing(o -> o.getInteger("ID")));

            progressPeriod = progressPeriods.get(progressPeriods.size() - 1);
         }
         else
         {
            progressPeriod = progressPeriods.stream().filter(r -> NumberHelper.equals(currentProgressPeriodID, r.getInteger("ID"))).findFirst().orElse(null);
         }

         if (progressPeriod != null)
         {
            ph.setStatusDate(progressPeriod.getDate("REPORT_DATE"));
         }
      }
   }

   /**
    * Process resources.
    *
    * @param permanentRows permanent resource data
    * @param consumableRows consumable resource data
    */
   public void processResources(List<Row> permanentRows, List<Row> consumableRows)
   {
      //
      // Process permanent resources
      //
      for (Row row : permanentRows)
      {
         Resource resource = m_project.addResource();
         resource.setType(ResourceType.WORK);
         resource.setUniqueID(row.getInteger("ID"));
         resource.setEmailAddress(row.getString("EMAIL_ADDRESS"));
         // EFFORT_TIME_UNIT
         resource.setName(row.getString("NAME"));
         resource.setCalendar(m_project.getCalendars().getByUniqueID(row.getInteger("CALENDAR")));
         resource.setGeneric(row.getBoolean("CREATED_AS_FOLDER"));
         resource.setInitials(getInitials(resource.getName()));

         resource.getAvailability().add(new Availability(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, Double.valueOf(row.getDouble("AVAILABILITY").doubleValue() * 100)));
      }

      //
      // Process groups
      //
      /*
            for (Row row : permanentRows)
            {
               Resource resource = m_project.getResourceByUniqueID(row.getInteger("ID"));
               Resource group = m_project.getResourceByUniqueID(row.getInteger("ROLE"));
               if (resource != null && group != null)
               {
                  resource.setGroup(group.getName());
               }
            }
      */
      //
      // Process consumable resources
      //
      UnitOfMeasureContainer uom = m_project.getUnitsOfMeasure();
      for (Row row : consumableRows)
      {
         Resource resource = m_project.addResource();
         resource.setType(ResourceType.MATERIAL);
         resource.setUniqueID(row.getInteger("ID"));
         resource.setName(row.getString("NAME"));
         resource.setCalendar(m_project.getCalendars().getByUniqueID(row.getInteger("CALENDAR")));
         resource.setGeneric(row.getBoolean("CREATED_AS_FOLDER"));
         resource.setUnitOfMeasure(uom.getOrCreateByAbbreviation(row.getString("MEASUREMENT")));
         resource.setInitials(getInitials(resource.getName()));

         CostRateTable table = new CostRateTable();
         table.add(new CostRateTableEntry(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, row.getDouble("COST_PER_USEDEFAULTSAMOUNT")));
         resource.setCostRateTable(0, table);

         LocalDateTime availableFrom = row.getDate("AVAILABLE_FROM");
         LocalDateTime availableTo = row.getDate("AVAILABLE_TO");
         availableFrom = availableFrom == null ? LocalDateTimeHelper.START_DATE_NA : availableFrom;
         availableTo = availableTo == null ? LocalDateTimeHelper.END_DATE_NA : availableTo;
         resource.getAvailability().add(new Availability(availableFrom, availableTo, Double.valueOf(row.getDouble("AVAILABILITY").doubleValue() * 100)));
      }
   }

   /**
    * Organises the data from Asta into a hierarchy and converts this into tasks.
    *
    * @param bars bar data
    * @param expandedTasks expanded task data
    * @param tasks task data
    * @param milestones milestone data
    * @param hammocks hammock data
    * @param completedSections completed section data
    */
   public void processTasks(List<Row> bars, List<Row> expandedTasks, List<Row> tasks, List<Row> milestones, List<Row> hammocks, List<Row> completedSections)
   {
      List<Row> parentBars = buildRowHierarchy(bars, expandedTasks, tasks, milestones, hammocks);
      createTasks(m_project, "", parentBars);
      populateCompletedSections(completedSections);
      deriveProjectCalendar();
      updateUniqueIDs();
      updateStructure();
      updateDates();
      calculatePercentComplete();
   }

   /**
    * Builds the task hierarchy.
    *
    * Note that there are two distinct levels of organisation going on here. The first is the
    * Asta "summary" organisation, where the user organises bars into summary groups. We are using this
    * to create our hierarchy of tasks.
    *
    * The second level displayed within a summary group (or at the project level if the user has not
    * created summary groups) is the WBS. At the moment we are not including the WBS in the hierarchy.
    *
    * @param bars bar data
    * @param expandedTasks expanded task data
    * @param tasks task data
    * @param milestones milestone data
    * @param hammocks hammock data
    * @return list containing the top level tasks
    */
   private List<Row> buildRowHierarchy(List<Row> bars, List<Row> expandedTasks, List<Row> tasks, List<Row> milestones, List<Row> hammocks)
   {
      //
      // Create a list of leaf nodes by merging the task and milestone lists
      //
      List<Row> leaves = new ArrayList<>();
      leaves.addAll(tasks);
      leaves.addAll(milestones);
      leaves.addAll(hammocks);

      //
      // Sort the bars and the leaves
      //
      bars.sort(BAR_COMPARATOR);
      leaves.sort(LEAF_COMPARATOR);

      //
      // Map bar IDs to bars
      //
      Map<Integer, Row> barIdToBarMap = new HashMap<>();
      for (Row bar : bars)
      {
         barIdToBarMap.put(bar.getInteger("BARID"), bar);
      }

      //
      // Merge expanded task attributes with parent bars
      // and create an expanded task ID to bar map.
      //
      Map<Integer, Row> expandedTaskIdToBarMap = new HashMap<>();
      for (Row expandedTask : expandedTasks)
      {
         Row bar = barIdToBarMap.get(expandedTask.getInteger("BAR"));
         bar.merge(expandedTask, "_");
         Integer expandedTaskID = bar.getInteger("_EXPANDED_TASKID");
         expandedTaskIdToBarMap.put(expandedTaskID, bar);
      }

      //
      // Build the hierarchy
      //
      List<Row> parentBars = new ArrayList<>();
      for (Row bar : bars)
      {
         Integer expandedTaskID = bar.getInteger("EXPANDED_TASK");
         Row parentBar = expandedTaskIdToBarMap.get(expandedTaskID);
         if (parentBar == null)
         {
            parentBars.add(bar);
         }
         else
         {
            parentBar.addChild(bar);
         }
      }

      //
      // Attach the leaves
      //
      for (Row leaf : leaves)
      {
         Integer barID = leaf.getInteger("BAR");
         Row bar = barIdToBarMap.get(barID);
         bar.addChild(leaf);
      }

      //
      // Prune any "displaced items" from the top level.
      // We're using a heuristic here as this is the only thing I
      // can see which differs between bars that we want to include
      // and bars that we want to exclude.
      //
      Iterator<Row> iter = parentBars.iterator();
      while (iter.hasNext())
      {
         Row bar = iter.next();
         String barName = bar.getString("NAME");
         if (barName == null || barName.isEmpty() || barName.equals("Displaced Items"))
         {
            iter.remove();
         }
      }

      //
      // If we only have a single top level node (effectively a summary task) prune that too.
      //
      if (parentBars.size() == 1)
      {
         parentBars = parentBars.get(0).getChildRows();
      }

      return parentBars;
   }

   /**
    * Recursively descend through  the hierarchy creating tasks.
    *
    * @param parent parent task
    * @param parentName parent name
    * @param rows rows to add as tasks to this parent
    */
   private void createTasks(ChildTaskContainer parent, String parentName, List<Row> rows)
   {
      for (Row row : rows)
      {
         boolean rowIsBar = (row.getInteger("BARID") != null);

         //
         // Don't export hammock tasks.
         //
         if (rowIsBar && childRowsAreHammocks(row))
         {
            continue;
         }

         Task task = parent.addTask();

         if (row.getInteger("_EXPANDED_TASKID") != null)
         {
            m_expandedTaskMap.put(row.getInteger("_EXPANDED_TASKID"), task);
         }

         //
         // Do we have a bar, task, or milestone?
         //
         if (rowIsBar)
         {
            //
            // If the bar only has one child task, we skip it and add the task directly
            //
            if (skipBar(row))
            {
               populateLeaf(row.getString("NAME"), row.getChildRows().get(0), task);
            }
            else
            {
               populateBar(row, task);
               createTasks(task, task.getName(), row.getChildRows());
            }

            m_barMap.put(row.getInteger("BARID"), task);
         }
         else
         {
            populateLeaf(parentName, row, task);
         }

         m_eventManager.fireTaskReadEvent(task);
      }
   }

   /**
    * Populate completed sections map.
    *
    * @param rows completed section rows
    */
   private void populateCompletedSections(List<Row> rows)
   {
      for (Row section : rows)
      {
         Task task = getTaskByAstaID(section.getInteger("TASK"));
         if (task == null)
         {
            continue;
         }
         m_completedSectionMap.put(section.getInteger("ID"), task);
      }
   }

   /**
    * Returns true if we should skip this bar, i.e. the bar only has a single child task.
    *
    * @param row bar row to test
    * @return true if this bar should be skipped
    */
   private boolean skipBar(Row row)
   {
      List<Row> childRows = row.getChildRows();
      if (childRows.size() != 1)
      {
         return false;
      }

      Row childRow = childRows.get(0);
      return (childRow.getInteger("TASKID") != null || childRow.getInteger("MILESTONEID") != null) && childRow.getChildRows().isEmpty();
   }

   /**
    * Returns true if all children of this row are hammock tasks.
    *
    * @param row parent row
    * @return true if all children are hammocks
    */
   private boolean childRowsAreHammocks(Row row)
   {
      int childCount = row.getChildRows().size();
      if (childCount == 0)
      {
         return false;
      }

      int count = (int) row.getChildRows().stream().filter(r -> r.getInteger("HAMMOCK_TASKID") != null).count();
      return count == childCount;
   }

   /**
    * Adds a leaf node, which could be a task or a milestone.
    *
    * @param parentName parent bar name
    * @param row row to add
    * @param task task to populate with data from the row
    */
   private void populateLeaf(String parentName, Row row, Task task)
   {
      if (row.getInteger("TASKID") != null)
      {
         populateTask(row, task);
         m_taskMap.put(row.getInteger("TASKID"), task);
      }
      else
      {
         if (row.getInteger("MILESTONEID") != null)
         {
            populateMilestone(row, task);
            m_milestoneMap.put(row.getInteger("MILESTONEID"), task);
         }
         else
         {
            // Bar with no linked row
            task.setUniqueID(row.getInteger("BARID"));
            task.setName(row.getString("NAME"));
         }
      }

      String name = task.getName();
      if (name == null || name.isEmpty())
      {
         task.setName(parentName);
      }
      task.setBarName(parentName);
   }

   /**
    * Populate a task from a Row instance.
    *
    * @param row Row instance
    * @param task Task instance
    */
   private void populateTask(Row row, Task task)
   {
      //"PROJID"
      task.setUniqueID(row.getInteger("TASKID"));
      //GIVEN_DURATIONTYPF
      //GIVEN_DURATIONELA_MONTHS

      // This does not appear to be accurate
      //task.setDuration(row.getDuration("GIVEN_DURATION"));

      task.setResume(row.getDate("RESUME"));
      //task.setStart(row.getDate("GIVEN_START"));
      //LATEST_PROGRESS_PERIOD
      //TASK_WORK_RATE_TIME_UNIT
      //TASK_WORK_RATE
      //PLACEMENT
      //BEEN_SPLIT
      //INTERRUPTIBLE
      //HOLDING_PIN
      //ACTUAL_DURATIONTYPF
      //ACTUAL_DURATIONELA_MONTHS
      task.setActualDuration(row.getDuration("ACTUAL_DURATION"));
      task.setEarlyStart(row.getDate("EARLY_START_DATE"));
      task.setLateStart(row.getDate("LATE_START_DATE"));
      task.setEarlyFinish(row.getDate("EARLY_END_DATE_RS"));
      task.setLateFinish(row.getDate("LATE_END_DATE_RS"));
      //FREE_START_DATE
      //START_CONSTRAINT_DATE
      //END_CONSTRAINT_DATE
      //task.setBaselineWork(row.getDuration("EFFORT_BUDGET"));
      //NATURAL_ORDER
      //LOGICAL_PRECEDENCE
      //SPAVE_INTEGER
      //SWIM_LANE
      //USER_PERCENT_COMPLETE
      //OVERALL_PERCENT_COMPL_WEIGHT
      task.setName(row.getString("NAME"));
      task.setNotes(getNotes(row));
      task.setActivityID(row.getString("UNIQUE_TASK_ID"));
      task.setCalendar(m_project.getCalendarByUniqueID(row.getInteger("CALENDAR")));
      //EFFORT_TIMI_UNIT
      //WORL_UNIT
      //LATEST_ALLOC_PROGRESS_PERIOD
      //WORN
      //BAR
      //CONSTRAINT_FLAG
      //PRIORITB
      //CRITICAM
      //USE_PARENU_CALENDAR
      //BUFFER_TASK
      //MARK_FOS_HIDING
      //OWNED_BY_TIMESHEEV_X
      //START_ON_NEX_DAY
      //LONGEST_PATH
      //DURATIOTTYPF
      //DURATIOTELA_MONTHS
      //DURATIOTHOURS
      task.setStart(row.getDate("LINKABLE_START"));
      task.setFinish(row.getDate("LINKABLE_FINISH"));
      //DURATION_TIME_UNIT
      //UNSCHEDULABLG
      //SUBPROJECT_ID
      //ALT_ID
      //LAST_EDITED_DATE
      //LAST_EDITED_BY

      //
      // Overall Percent Complete
      //
      Double overallPercentComplete = row.getPercent("OVERALL_PERCENT_COMPLETE");
      task.setOverallPercentComplete(overallPercentComplete);
      m_weights.put(task, row.getDouble("OVERALL_PERCENT_COMPL_WEIGHT"));
      boolean taskIsComplete = overallPercentComplete != null && overallPercentComplete.doubleValue() > 99.0;

      //
      // The attribute we thought contained the duration appears to be unreliable.
      // To match what we see in Asta the best way to determine the duration appears
      // to be to calculate it from the start and finish dates.
      //
      Duration remainingDuration = null;
      if (!taskIsComplete)
      {
         LocalDateTime startDate = task.getResume();
         if (startDate == null)
         {
            startDate = task.getStart();
         }

         if (timeUnitIsElapsed(row.getInt("DURATION_TIME_UNIT")))
         {
            remainingDuration = Duration.getInstance(startDate.until(task.getFinish(), ChronoUnit.HOURS), TimeUnit.HOURS);
         }
         else
         {
            remainingDuration = task.getEffectiveCalendar().getWork(startDate, task.getFinish(), TimeUnit.HOURS);
         }
      }

      if (remainingDuration == null)
      {
         remainingDuration = Duration.getInstance(0, TimeUnit.HOURS);
      }
      task.setRemainingDuration(remainingDuration);

      Duration actualDuration = task.getActualDuration();
      Duration durationAtCompletion = Duration.getInstance(actualDuration.getDuration() + remainingDuration.getDuration(), TimeUnit.HOURS);
      task.setDuration(durationAtCompletion);

      //
      // Duration Percent Complete
      //
      if (overallPercentComplete != null && overallPercentComplete.doubleValue() > 99.0)
      {
         task.setActualDuration(task.getDuration());
         task.setActualStart(task.getStart());
         task.setActualFinish(task.getFinish());
         task.setPercentageComplete(COMPLETE);
      }
      else
      {
         if (durationAtCompletion != null && durationAtCompletion.getDuration() > 0 && actualDuration.getDuration() > 0)
         {
            // We have an actual duration, so we must have an actual start date
            task.setActualStart(task.getStart());

            double percentComplete = (actualDuration.getDuration() / durationAtCompletion.getDuration()) * 100.0;
            task.setPercentageComplete(Double.valueOf(percentComplete));
            if (percentComplete > 99.0)
            {
               task.setActualFinish(task.getFinish());
            }
         }
         else
         {
            task.setPercentageComplete(INCOMPLETE);
         }
      }

      if (task.getEarlyStart() != null && task.getEarlyFinish() == null)
      {
         task.setEarlyFinish(task.getEffectiveCalendar().getDate(task.getEarlyStart(), task.getDuration()));
      }

      if (task.getLateStart() != null && task.getLateFinish() == null)
      {
         task.setLateFinish(task.getEffectiveCalendar().getDate(task.getLateStart(), task.getDuration()));
      }

      processConstraints(row, task);
   }

   /**
    * Uses data from a bar to populate a task.
    *
    * @param row bar data
    * @param task task to populate
    */
   private void populateBar(Row row, Task task)
   {
      Integer calendarID = row.getInteger("_CALENDAR");
      if (calendarID == null)
      {
         if (!row.getChildRows().isEmpty())
         {
            calendarID = row.getChildRows().get(0).getInteger("CALENDAR");
         }
      }

      String name = row.getString("NAME");
      if (name == null || name.isEmpty())
      {
         String extendedTaskName = row.getString("_NAME");
         if (extendedTaskName != null && !extendedTaskName.isEmpty())
         {
            name = extendedTaskName;
         }
      }

      ProjectCalendar calendar = m_project.getCalendarByUniqueID(calendarID);

      //PROJID
      task.setUniqueID(row.getInteger("BARID"));
      task.setStart(row.getDate("BAR_START"));
      task.setFinish(row.getDate("BAR_FINISH"));
      //NATURAL_ORDER
      //SPARI_INTEGER
      task.setName(name);
      task.setActivityID(row.getString("_UNIQUE_TASK_ID"));
      //EXPANDED_TASK
      //PRIORITY
      //UNSCHEDULABLE
      //MARK_FOR_HIDING
      //TASKS_MAY_OVERLAP
      //SUBPROJECT_ID
      //ALT_ID
      //LAST_EDITED_DATE
      //LAST_EDITED_BY
      //Proc_Approve
      //Proc_Design_info
      //Proc_Proc_Dur
      //Proc_Procurement
      //Proc_SC_design
      //Proc_Select_SC
      //Proc_Tender
      //QA Checked
      //Related_Documents
      task.setCalendar(calendar);

      Duration durationAtCompletion = deriveEffectiveCalendar(task).getWork(task.getStart(), task.getFinish(), TimeUnit.HOURS);
      task.setDuration(durationAtCompletion);
   }

   /**
    * This is a hacky solution to the problem that at the point in the process we have not
    * determined the default calendar. We need a better way to understand how date calculations
    * should be carried out for summary tasks.
    *
    * @param task task from which to derive the effective calendar
    * @return effective calendar
    */
   private ProjectCalendar deriveEffectiveCalendar(Task task)
   {
      ProjectCalendar result = task.getEffectiveCalendar();
      if (result == null)
      {
         if (m_project.getCalendars().isEmpty())
         {
            result = m_project.addDefaultBaseCalendar();
         }
         else
         {
            result = m_project.getCalendars().get(0);
         }
      }
      return result;
   }

   /**
    * Populate a milestone from a Row instance.
    *
    * @param row Row instance
    * @param task Task instance
    */
   private void populateMilestone(Row row, Task task)
   {
      task.setMilestone(true);
      //PROJID
      task.setUniqueID(row.getInteger("MILESTONEID"));
      task.setStart(row.getDate("GIVEN_DATE_TIME"));
      task.setFinish(row.getDate("GIVEN_DATE_TIME"));
      //PROGREST_PERIOD
      //SYMBOL_APPEARANCE
      //MILESTONE_TYPE
      task.setActivityType(getMilestoneType(row));
      //PLACEMENU
      //INTERRUPTIBLE_X
      //ACTUAL_DURATIONTYPF
      //ACTUAL_DURATIONELA_MONTHS
      //ACTUAL_DURATION
      task.setEarlyStart(row.getDate("EARLY_START_DATE"));
      task.setLateStart(row.getDate("LATE_START_DATE"));
      task.setEarlyFinish(row.getDate("EARLY_END_DATE_RS"));
      task.setLateFinish(row.getDate("LATE_END_DATE_RS"));
      //FREE_START_DATE
      //START_CONSTRAINT_DATE
      //END_CONSTRAINT_DATE
      //EFFORT_BUDGET
      //NATURAL_ORDER
      //LOGICAL_PRECEDENCE
      //SPAVE_INTEGER
      //SWIM_LANE
      //USER_PERCENT_COMPLETE
      //OVERALL_PERCENT_COMPLETE
      //OVERALL_PERCENT_COMPL_WEIGHT
      task.setName(row.getString("NAME"));
      //NOTES
      task.setActivityID(row.getString("UNIQUE_TASK_ID"));
      task.setCalendar(m_project.getCalendarByUniqueID(row.getInteger("CALENDAR")));
      //EFFORT_TIMI_UNIT
      //WORL_UNIT
      //LATEST_ALLOC_PROGRESS_PERIOD
      //WORN
      //CONSTRAINT_FLAG
      //PRIORITB
      //CRITICAM
      //USE_PARENU_CALENDAR
      //BUFFER_TASK
      //MARK_FOS_HIDING
      //OWNED_BY_TIMESHEEV_X
      //START_ON_NEX_DAY
      //LONGEST_PATH
      //DURATIOTTYPF
      //DURATIOTELA_MONTHS
      //DURATIOTHOURS
      //LINKABLE_START
      //LINKABLE_FINISH
      //DURATION_TIME_UNIT
      //UNSCHEDULABLG
      //SUBPROJECT_ID
      //ALT_ID
      //LAST_EDITED_DATE
      //LAST_EDITED_BY
      task.setDuration(Duration.getInstance(0, TimeUnit.HOURS));

      if (row.getBoolean("COMPLETED"))
      {
         task.setPercentageComplete(COMPLETE);
         task.setActualStart(task.getStart());
         task.setActualFinish(task.getFinish());
      }
      else
      {
         task.setPercentageComplete(INCOMPLETE);
      }

      // Asta files may not have explicit values for early finish and late finish.
      // If not present, use the values from  early start and late start respectively.
      if (task.getEarlyFinish() == null)
      {
         task.setEarlyFinish(task.getEarlyStart());
      }

      if (task.getLateFinish() == null)
      {
         task.setLateFinish(task.getLateStart());
      }

      processConstraints(row, task);

      m_weights.put(task, row.getDouble("OVERALL_PERCENT_COMPL_WEIGHT"));
   }

   /**
    * Retrieve the milestone type.
    *
    * @param row row
    * @return milestone type
    */
   private ActivityType getMilestoneType(Row row)
   {
      Integer value = row.getInteger("MILESTONE_TYPE");
      if (value == null)
      {
         return ActivityType.FINISH_MILESTONE;
      }

      return value.intValue() == 1 ? ActivityType.FINISH_MILESTONE : ActivityType.START_MILESTONE;
   }

   /**
    * Ensure all tasks have a unique ID.
    */
   private void updateUniqueIDs()
   {
      m_project.getTasks().stream().filter(t -> t.getUniqueID() == null).forEach(t -> t.setUniqueID(m_project.getUniqueIdObjectSequence(Task.class).getNext()));
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values.
    */
   private void updateStructure()
   {
      int id = 1;
      Integer outlineLevel = Integer.valueOf(1);
      for (Task task : m_project.getChildTasks())
      {
         id = updateStructure(id, task, outlineLevel);
      }
   }

   /**
    * Iterates through the tasks setting the correct
    * outline level and ID values.
    *
    * @param id current ID value
    * @param task current task
    * @param outlineLevel current outline level
    * @return next ID value
    */
   private int updateStructure(int id, Task task, Integer outlineLevel)
   {
      task.setID(Integer.valueOf(id++));
      task.setOutlineLevel(outlineLevel);
      outlineLevel = Integer.valueOf(outlineLevel.intValue() + 1);
      for (Task childTask : task.getChildTasks())
      {
         id = updateStructure(id, childTask, outlineLevel);
      }
      return id;
   }

   /**
    * Summary task percent complete calculations.
    */
   private void calculatePercentComplete()
   {
      List<Task> childTasks = new ArrayList<>();

      for (Task task : m_project.getTasks())
      {
         if (task.hasChildTasks())
         {
            if (task.getActualFinish() != null)
            {
               task.setPercentageComplete(COMPLETE);
               continue;
            }

            childTasks.clear();
            gatherChildTasks(childTasks, task);

            double totalPercentComplete = 0;
            double totalOverallPercentComplete = 0;
            double totalWeight = 0;
            double totalActualDuration = 0;
            double totalDuration = 0;

            for (Task child : childTasks)
            {
               totalPercentComplete += NumberHelper.getDouble(child.getPercentageComplete());
               totalOverallPercentComplete += NumberHelper.getDouble(child.getOverallPercentComplete());
               totalWeight += NumberHelper.getDouble(m_weights.get(child));

               Duration actualDuration = child.getActualDuration();
               if (actualDuration != null)
               {
                  totalActualDuration += actualDuration.getDuration();
               }

               Duration duration = child.getDuration();
               if (duration != null)
               {
                  totalDuration += duration.getDuration();
               }
            }

            if (totalWeight == 0)
            {
               totalWeight = 1.0;
            }

            // Calculating Overall Percent Complete seems to work in some cases
            // but for others it's not clear how the percent completes and weights are being
            // combined in Powerproject to determine the value shown.
            double overallPercentComplete = totalOverallPercentComplete / totalWeight;
            task.setOverallPercentComplete(Double.valueOf(overallPercentComplete));

            //
            // Duration percent complete
            //
            if (totalDuration == 0)
            {
               if (totalPercentComplete != 0)
               {
                  // If the total duration is zero, but we have percent complete values,
                  // we must just have milestones, a different approach is required as we won't have durations
                  double durationPercentComplete = totalPercentComplete / childTasks.size();
                  task.setPercentageComplete(Double.valueOf(durationPercentComplete));
               }
            }
            else
            {
               TimeUnit units = task.getDuration().getUnits();
               double durationPercentComplete = (totalActualDuration / totalDuration) * 100.0;
               double duration = task.getDuration().getDuration();
               double actualDuration = (duration * durationPercentComplete) / 100.0;
               double remainingDuration = duration - actualDuration;

               task.setPercentageComplete(Double.valueOf(durationPercentComplete));
               task.setActualDuration(Duration.getInstance(actualDuration, units));
               task.setRemainingDuration(Duration.getInstance(remainingDuration, units));
            }
         }
      }
   }

   /**
    * Retrieve all child tasks below this task to the bottom of the hierarchy.
    * If the task has no child tasks then just add it to the array.
    *
    * @param tasks array to collect child tasks
    * @param task current task
    */
   private void gatherChildTasks(List<Task> tasks, Task task)
   {
      if (task.hasChildTasks())
      {
         task.getChildTasks().forEach(child -> gatherChildTasks(tasks, child));
      }
      else
      {
         tasks.add(task);
      }
   }

   /**
    * Populate summary task dates.
    */
   private void updateDates()
   {
      m_project.getChildTasks().forEach(this::updateDates);
   }

   /**
    * Populate summary task dates.
    *
    * @param parentTask summary task
    */
   private void updateDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         int finished = 0;
         LocalDateTime actualStartDate = parentTask.getActualStart();
         LocalDateTime actualFinishDate = parentTask.getActualFinish();
         LocalDateTime earlyStartDate = parentTask.getEarlyStart();
         LocalDateTime earlyFinishDate = parentTask.getEarlyFinish();
         LocalDateTime lateStartDate = parentTask.getLateStart();
         LocalDateTime lateFinishDate = parentTask.getLateFinish();

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            actualStartDate = LocalDateTimeHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = LocalDateTimeHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getEarlyFinish());
            lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getLateFinish());

            if (task.getActualFinish() != null)
            {
               ++finished;
            }
         }

         parentTask.setActualStart(actualStartDate);
         parentTask.setEarlyStart(earlyStartDate);
         parentTask.setEarlyFinish(earlyFinishDate);
         parentTask.setLateStart(lateStartDate);
         parentTask.setLateFinish(lateFinishDate);

         //
         // Only if all child tasks have actual finish dates do we
         // set the actual finish date on the parent task.
         //
         if (finished == parentTask.getChildTasks().size())
         {
            parentTask.setActualFinish(actualFinishDate);
         }
      }
   }

   /**
    * Processes predecessor data.
    *
    * @param rows predecessor data
    */
   public void processPredecessors(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task startTask = getTaskByAstaID(row.getInteger("START_TASK"));
         if (startTask == null)
         {
            continue;
         }

         Task endTask = getTaskByAstaID(row.getInteger("END_TASK"));
         if (endTask == null)
         {
            continue;
         }

         RelationType type = getRelationType(row.getInt("LINK_KIND"));
         Duration startLag = row.getDuration("START_LAG_TIME");
         Duration endLag = row.getDuration("END_LAG_TIME");
         Duration lag;

         double startLagDuration = startLag.getDuration();
         double endLagDuration = endLag.getDuration();

         if (startLagDuration == 0.0 && endLagDuration == 0.0)
         {
            lag = Duration.getInstance(0, TimeUnit.HOURS);
         }
         else
         {
            if (startLagDuration != 0.0 && endLagDuration == 0.0)
            {
               lag = startLag;
            }
            else
            {
               if (startLagDuration == 0.0 && endLagDuration != 0.0)
               {
                  lag = Duration.getInstance(startLagDuration - endLagDuration, endLag.getUnits());
               }
               else
               {
                  // For the moment we're assuming if both a start lag and an end lag are supplied, they both use the same units.
                  // If I'm given an example where they are different I'll revisit this code.
                  lag = Duration.getInstance(startLagDuration - endLagDuration, startLag.getUnits());
               }
            }
         }

         endTask.addPredecessor(new Relation.Builder()
            .predecessorTask(startTask)
            .type(type)
            .lag(lag)
            .uniqueID(row.getInteger("ID")));

         // resolve indeterminate constraint for successor tasks
         if (m_deferredConstraintType.contains(endTask.getUniqueID()))
         {
            endTask.setConstraintType(ConstraintType.AS_LATE_AS_POSSIBLE);
            endTask.setConstraintDate(null);
         }
      }
   }

   /**
    * Process assignment data.
    *
    * @param allocationRows assignment data
    * @param skillRows skill data
    */
   public void processAssignments(List<Row> allocationRows, List<Row> skillRows)
   {
      // TODO: add support for consumable resource assignments
      Map<Integer, Row> skillMap = skillRows.stream().collect(Collectors.toMap(t -> t.getInteger("ID"), t -> t));
      for (Row row : allocationRows)
      {
         Task task = getTaskByAstaID(row.getInteger("ALLOCATED_TO"));
         if (task == null)
         {
            continue;
         }

         Row skill = skillMap.get(row.getInteger("ALLOCATION_OF"));
         if (skill == null)
         {
            continue;
         }

         Resource resource = m_project.getResourceByUniqueID(skill.getInteger("PLAYER"));
         if (resource == null)
         {
            continue;
         }

         Double percentComplete = row.getPercent("PERCENT_COMPLETE");
         Duration work = row.getWork("EFFORT");
         double actualWork = (work.getDuration() * percentComplete.doubleValue()) / 100.0;
         double remainingWork = work.getDuration() - actualWork;

         ResourceAssignment assignment = task.addResourceAssignment(resource);
         assignment.setUniqueID(row.getInteger("ID"));
         assignment.setStart(row.getDate("LINKABLE_START"));
         assignment.setFinish(row.getDate("LINKABLE_FINISH"));
         assignment.setUnits(Double.valueOf(row.getDouble("GIVEN_ALLOCATION").doubleValue() * 100));
         assignment.setDelay(row.getDuration("DELAY"));
         assignment.setWork(work);
         assignment.setActualWork(Duration.getInstance(actualWork, work.getUnits()));
         assignment.setRemainingWork(Duration.getInstance(remainingWork, work.getUnits()));
         assignment.setPercentageWorkComplete(percentComplete);
      }
   }

   /**
    * Convert an integer into a RelationType instance.
    *
    * @param index integer value
    * @return RelationType instance
    */
   private RelationType getRelationType(int index)
   {
      if (index < 0 || index > RELATION_TYPES.length)
      {
         index = 0;
      }

      return RELATION_TYPES[index];
   }

   /**
    * Convert a name into initials.
    *
    * @param name source name
    * @return initials
    */
   private String getInitials(String name)
   {
      String result = null;

      if (name != null && !name.isEmpty())
      {
         StringBuilder sb = new StringBuilder();
         sb.append(name.charAt(0));
         int index = 1;
         while (true)
         {
            index = name.indexOf(' ', index);
            if (index == -1)
            {
               break;
            }

            ++index;
            if (index < name.length() && name.charAt(index) != ' ')
            {
               sb.append(name.charAt(index));
            }

            ++index;
         }

         result = sb.toString();
      }

      return result;
   }

   /**
    * Asta Powerproject assigns an explicit calendar for each task. This method
    * is used to find the most common calendar and use this as the default project
    * calendar. This allows the explicitly assigned task calendars to be removed.
    */
   private void deriveProjectCalendar()
   {
      //
      // Count the number of times each calendar is used
      //
      Map<ProjectCalendar, Integer> map = new HashMap<>();
      for (Task task : m_project.getTasks())
      {
         ProjectCalendar calendar = task.getCalendar();
         map.compute(calendar, (k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1));
      }

      //
      // Find the most frequently used calendar
      //
      int maxCount = 0;
      ProjectCalendar defaultCalendar = null;

      for (Entry<ProjectCalendar, Integer> entry : map.entrySet())
      {
         if (entry.getValue().intValue() > maxCount)
         {
            maxCount = entry.getValue().intValue();
            defaultCalendar = entry.getKey();
         }
      }

      //
      // Set the default calendar for the project
      // and remove its use as a task-specific calendar.
      //
      if (defaultCalendar == null)
      {
         defaultCalendar = m_project.getCalendars().findOrCreateDefaultCalendar();
      }
      else
      {
         for (Task task : m_project.getTasks())
         {
            if (task.getCalendar() == defaultCalendar)
            {
               task.setCalendar(null);
            }
         }
      }

      m_project.setDefaultCalendar(defaultCalendar);
   }

   /**
    * Determines the constraints relating to a task.
    *
    * @param row row data
    * @param task Task instance
    */
   private void processConstraints(Row row, Task task)
   {
      ConstraintType constraintType = ConstraintType.AS_SOON_AS_POSSIBLE;
      LocalDateTime constraintDate = null;

      switch (row.getInt("CONSTRAINT_FLAG"))
      {
         case 0:
         {
            // 0 = ASAP, 1 = ALAP, 2 = ASAP Force Critical
            if (row.getInt("PLACEMENT") == 1)
            {
               // If the task has no predecessors, the constraint type will be START_NO_EARLIER_THAN.
               // If the task has predecessors, the constraint type will be AS_LATE_AS_POSSIBLE.
               // We don't have the predecessor information at this point, so we note the task Unique ID
               // to allow us to update the constraint type later if necessary.
               // https://github.com/joniles/mpxj/issues/161
               m_deferredConstraintType.add(task.getUniqueID());
               constraintType = ConstraintType.START_NO_EARLIER_THAN;
               constraintDate = row.getDate("START_CONSTRAINT_DATE");
            }
            else
            {
               constraintType = ConstraintType.AS_SOON_AS_POSSIBLE;
            }
            break;
         }

         case 1:
         {
            constraintType = ConstraintType.MUST_START_ON;
            constraintDate = row.getDate("START_CONSTRAINT_DATE");
            break;
         }

         case 2:
         {
            constraintType = ConstraintType.START_NO_LATER_THAN;
            constraintDate = row.getDate("START_CONSTRAINT_DATE");
            break;
         }

         case 3:
         {
            constraintType = ConstraintType.START_NO_EARLIER_THAN;
            constraintDate = row.getDate("START_CONSTRAINT_DATE");
            break;
         }

         case 4:
         {
            constraintType = ConstraintType.MUST_FINISH_ON;
            constraintDate = row.getDate("END_CONSTRAINT_DATE");
            break;
         }

         case 5:
         {
            constraintType = ConstraintType.FINISH_NO_LATER_THAN;
            constraintDate = row.getDate("END_CONSTRAINT_DATE");
            break;
         }

         case 6:
         {
            constraintType = ConstraintType.FINISH_NO_EARLIER_THAN;
            constraintDate = row.getDate("END_CONSTRAINT_DATE");
            break;
         }

         case 8:
         {
            task.setDeadline(row.getDate("END_CONSTRAINT_DATE"));
            break;
         }
      }

      task.setConstraintType(constraintType);
      task.setConstraintDate(constraintDate);
   }

   /**
    * Creates a mapping between exception ID values and working/non-working days.
    *
    * @param rows rows from the exceptions table
    * @return exception map
    */
   public Map<Integer, DayType> createExceptionTypeMap(List<Row> rows)
   {
      Map<Integer, DayType> map = new HashMap<>();
      for (Row row : rows)
      {
         Integer id = row.getInteger("ID");
         DayType result;

         switch (row.getInt("UNIQUE_BIT_FIELD"))
         {
            case 8: // Working
            case 32: // Overtime
            case 128: //Weekend Working
            {
               result = DayType.WORKING;
               break;
            }

            case 4: // Non Working
            case 16: // Holiday
            case 64: // Weather
            case -2147483648: // Weekend
            default:
            {
               result = DayType.NON_WORKING;
               break;
            }
         }

         map.put(id, result);
      }
      return map;
   }

   /**
    * Creates a map of work pattern rows indexed by the primary key.
    *
    * @param rows work pattern rows
    * @return work pattern map
    */
   public Map<Integer, Row> createWorkPatternMap(List<Row> rows)
   {
      Map<Integer, Row> map = new HashMap<>();
      for (Row row : rows)
      {
         map.put(row.getInteger("ID"), row);
      }
      return map;
   }

   /**
    * Creates a map between a calendar ID and a list of
    * work pattern assignment rows.
    *
    * @param rows work pattern assignment rows
    * @return work pattern assignment map
    */
   public Map<Integer, List<Row>> createWorkPatternAssignmentMap(List<Row> rows)
   {
      Map<Integer, List<Row>> map = new HashMap<>();
      for (Row row : rows)
      {
         Integer calendarID = row.getInteger("WORK_PATTERN_ASSIGNMENTID");
         List<Row> list = map.computeIfAbsent(calendarID, k -> new ArrayList<>());
         list.add(row);
      }
      return map;
   }

   /**
    * Creates a map between a calendar ID and a list of exception assignment rows.
    *
    * @param rows exception assignment rows
    * @return exception assignment map
    */
   public Map<Integer, List<Row>> createExceptionAssignmentMap(List<Row> rows)
   {
      Map<Integer, List<Row>> map = new HashMap<>();
      for (Row row : rows)
      {
         Integer calendarID = row.getInteger("EXCEPTION_ASSIGNMENTID");
         List<Row> list = map.computeIfAbsent(calendarID, k -> new ArrayList<>());
         list.add(row);
      }
      return map;
   }

   /**
    * Creates a map between a work pattern ID and a list of time entry rows.
    *
    * @param rows time entry rows
    * @return time entry map
    */
   public Map<Integer, List<Row>> createTimeEntryMap(List<Row> rows)
   {
      Map<Integer, List<Row>> map = new HashMap<>();
      for (Row row : rows)
      {
         Integer workPatternID = row.getInteger("TIME_ENTRYID");
         List<Row> list = map.computeIfAbsent(workPatternID, k -> new ArrayList<>());
         list.add(row);
      }
      return map;
   }

   /**
    * Creates a ProjectCalendar instance from the Asta data.
    *
    * @param calendarRow basic calendar data
    * @param workPatternMap work pattern map
    * @param workPatternAssignmentMap work pattern assignment map
    * @param exceptionAssignmentMap exception assignment map
    * @param timeEntryMap time entry map
    * @param exceptionTypeMap exception type map
    */
   public void processCalendar(Row calendarRow, Map<Integer, Row> workPatternMap, Map<Integer, List<Row>> workPatternAssignmentMap, Map<Integer, List<Row>> exceptionAssignmentMap, Map<Integer, List<Row>> timeEntryMap, Map<Integer, DayType> exceptionTypeMap)
   {
      //
      // Create the calendar and add the default working hours
      //
      ProjectCalendar calendar = m_project.addCalendar();
      Integer dominantWorkPatternID = calendarRow.getInteger("DOMINANT_WORK_PATTERN");
      calendar.setUniqueID(calendarRow.getInteger("ID"));
      calendar.setName(calendarRow.getString("NAME"));

      boolean defaultWeekSet = workPatternMap.get(dominantWorkPatternID) != null;
      if (defaultWeekSet)
      {
         processWorkPattern(calendar, dominantWorkPatternID, workPatternMap, timeEntryMap, exceptionTypeMap);
      }

      //
      // Add any additional working weeks
      //
      List<Row> rows = workPatternAssignmentMap.get(calendar.getUniqueID());
      if (rows != null)
      {
         for (Row row : rows)
         {
            Integer workPatternID = row.getInteger("WORK_PATTERN");
            if (!workPatternID.equals(dominantWorkPatternID))
            {
               ProjectCalendarDays week;
               if (defaultWeekSet)
               {
                  ProjectCalendarWeek newWeek = calendar.addWorkWeek();
                  newWeek.setDateRange(new LocalDateRange(LocalDateHelper.getLocalDate(row.getDate("START_DATE")), LocalDateHelper.getLocalDate(row.getDate("END_DATE"))));
                  week = newWeek;
               }
               else
               {
                  week = calendar;
                  defaultWeekSet = true;
               }

               processWorkPattern(week, workPatternID, workPatternMap, timeEntryMap, exceptionTypeMap);
            }
         }
      }

      //
      // Add exceptions - not sure how exceptions which turn non-working days into working days are handled by Asta - if at all?
      //
      rows = exceptionAssignmentMap.get(calendar.getUniqueID());
      if (rows != null)
      {
         List<LocalDateRange> ranges = new ArrayList<>();

         for (Row row : rows)
         {
            LocalDateTime startDate = row.getDate("START_DATE");
            LocalDateTime endDate = row.getDate("END_DATE");

            // special case - when the exception end time is midnight, it really finishes at the end of the previous day
            if (endDate.equals(LocalDateTimeHelper.getDayStartDate(endDate)))
            {
               endDate = endDate.plusDays(-1);
            }

            ranges.add(new LocalDateRange(LocalDateHelper.getLocalDate(startDate), LocalDateHelper.getLocalDate(endDate)));
         }

         ranges.stream().distinct().forEach(r -> calendar.addCalendarException(r.getStart(), r.getEnd()));
      }

      //
      // Populate WORKING or NON_WORKING days with calendar hours if they are missing.
      //
      for (DayOfWeek day : DayOfWeek.values())
      {
         if (calendar.getCalendarHours(day) == null)
         {
            DayType dayType = calendar.getCalendarDayType(day);
            if (dayType != DayType.DEFAULT)
            {
               ProjectCalendarHours hours = calendar.addCalendarHours(day);
               if (dayType == DayType.WORKING)
               {
                  hours.add(ProjectCalendarDays.DEFAULT_WORKING_MORNING);
                  hours.add(ProjectCalendarDays.DEFAULT_WORKING_AFTERNOON);
               }
            }
         }
      }

      calendar.setParent(m_project.getCalendarByUniqueID(calendarRow.getInteger("CALENDAR")));

      m_eventManager.fireCalendarReadEvent(calendar);
   }

   /**
    * Populates a ProjectCalendarWeek instance from Asta work pattern data.
    *
    * @param week target ProjectCalendarWeek instance
    * @param workPatternID target work pattern ID
    * @param workPatternMap work pattern data
    * @param timeEntryMap time entry map
    * @param exceptionTypeMap exception type map
    */
   private void processWorkPattern(ProjectCalendarDays week, Integer workPatternID, Map<Integer, Row> workPatternMap, Map<Integer, List<Row>> timeEntryMap, Map<Integer, DayType> exceptionTypeMap)
   {
      Row workPatternRow = workPatternMap.get(workPatternID);
      if (workPatternRow == null)
      {
         return;
      }

      // Don't apply the name to the top level calendar
      if (!(week instanceof ProjectCalendar))
      {
         week.setName(workPatternRow.getString("NAME"));
      }

      List<Row> timeEntryRows = timeEntryMap.get(workPatternID);
      if (timeEntryRows == null)
      {
         return;
      }

      DayOfWeek currentDay = DayOfWeek.SATURDAY;
      Arrays.stream(DayOfWeek.values()).forEach(d -> week.setCalendarDayType(d, DayType.NON_WORKING));
      ProjectCalendarHours hours = null;

      for (Row row : timeEntryRows)
      {
         LocalTime startTime = LocalTimeHelper.getLocalTime(row.getDate("START_TIME"));
         LocalTime endTime = LocalTimeHelper.getLocalTime(row.getDate("END_TIME"));

         if (startTime == null)
         {
            startTime = LocalTime.MIDNIGHT;
         }

         if (endTime == null)
         {
            endTime = LocalTime.MIDNIGHT;
         }

         if (startTime == LocalTime.MIDNIGHT)
         {
            currentDay = currentDay.plus(1);
            hours = week.addCalendarHours(currentDay);
         }

         DayType type = exceptionTypeMap.get(row.getInteger("EXCEPTION"));
         if (hours != null && type == DayType.WORKING)
         {
            hours.add(new LocalTimeRange(startTime, endTime));
            week.setCalendarDayType(currentDay, DayType.WORKING);
         }
      }
   }

   /**
    * Extract note text.
    *
    * @param row task data
    * @return note text
    */
   private String getNotes(Row row)
   {
      String notes = row.getString("NOTES");
      if (notes != null)
      {
         if (notes.isEmpty())
         {
            notes = null;
         }
         else
         {
            if (notes.contains(LINE_BREAK))
            {
               notes = notes.replace(LINE_BREAK, "\n");
            }
         }
      }
      return notes;
   }

   /**
    * Extract user defined field data.
    *
    * @param definitions user defined field definitions
    * @param data user defined  field data
    */
   public void processUserDefinedFields(List<Row> definitions, List<Row> data)
   {
      Map<Integer, ObjectType> objectTypeMap = new HashMap<>();
      Map<Integer, FieldType> fieldMap = new HashMap<>();

      processUserDefinedFieldDefinitions(definitions, objectTypeMap, fieldMap);
      processUserDefinedFieldData(data, objectTypeMap, fieldMap);
   }

   /**
    * Process user defined field definitions.
    *
    * @param definitions field definitions
    * @param objectTypeMap id to object type map
    * @param fieldMap id to user defined field map
    */
   private void processUserDefinedFieldDefinitions(List<Row> definitions, Map<Integer, ObjectType> objectTypeMap, Map<Integer, FieldType> fieldMap)
   {
      UserDefinedFieldContainer userDefinedFields = m_project.getUserDefinedFields();
      CustomFieldContainer customFields = m_project.getCustomFields();

      for (Row row : definitions)
      {
         ObjectType objectType = ObjectType.getInstance(row.getInteger("OBJ_TYPE"));
         if (objectType == null)
         {
            continue;
         }

         FieldTypeClass fieldTypeClass = getFieldTypeClass(objectType);
         if (fieldTypeClass == null)
         {
            continue;
         }

         DataType dataType = DATA_TYPE_MAP.get(row.getInteger("DATA_TYPE"));
         if (dataType == null)
         {
            continue;
         }

         UserDefinedField field = new UserDefinedField.Builder(m_project)
            .uniqueID(row.getInteger("UDF_ID"))
            .externalName(row.getString("UDF_NAME"))
            .fieldTypeClass(fieldTypeClass)
            .dataType(dataType)
            .build();

         userDefinedFields.add(field);
         customFields.add(field).setAlias(field.getName());

         objectTypeMap.put(field.getUniqueID(), objectType);
         fieldMap.put(field.getUniqueID(), field);
      }
   }

   private FieldTypeClass getFieldTypeClass(ObjectType objectType)
   {
      FieldTypeClass fieldTypeClass = null;

      switch (objectType)
      {
         case BAR_OBJECT_TYPE:
         case TASK_OBJECT_TYPE:
         case MILESTONE_OBJECT_TYPE:
         {
            fieldTypeClass = FieldTypeClass.TASK;
            break;
         }

         case PERMANENT_RESOURCE_OBJECT_TYPE:
         case CONSUMABLE_RESOURCE_OBJECT_TYPE:
         {
            fieldTypeClass = FieldTypeClass.RESOURCE;
            break;
         }

         case PERMANENT_SCHEDULE_ALLOCATION_OBJECT_TYPE:
         {
            fieldTypeClass = FieldTypeClass.ASSIGNMENT;
            break;
         }
      }

      return fieldTypeClass;
   }

   /**
    * Process user defined field data.
    *
    * @param data user defined field data
    * @param objectTypeMap id to object type map
    * @param fieldMap id to field map
    */
   private void processUserDefinedFieldData(List<Row> data, Map<Integer, ObjectType> objectTypeMap, Map<Integer, FieldType> fieldMap)
   {
      for (Row row : data)
      {
         Integer id = row.getInteger("UDF_ID");

         FieldType field = fieldMap.get(id);
         if (field == null)
         {
            continue;
         }

         ObjectType objectType = objectTypeMap.get(id);
         if (objectType == null)
         {
            continue;
         }

         Function<Integer, FieldContainer> mapper = getFieldContainerMapper(objectType);
         if (mapper == null)
         {
            continue;
         }

         FieldContainer container = mapper.apply(row.getInteger("OBJ_ID"));
         if (container == null)
         {
            continue;
         }

         container.set(field, getUserDefinedFieldValue(field.getDataType(), row));
      }
   }

   private Function<Integer, FieldContainer> getFieldContainerMapper(ObjectType objectType)
   {
      Function<Integer, FieldContainer> mapper = null;
      switch (objectType)
      {
         case BAR_OBJECT_TYPE:
         {
            mapper = m_barMap::get;
            break;
         }

         case TASK_OBJECT_TYPE:
         {
            mapper = m_taskMap::get;
            break;
         }

         case MILESTONE_OBJECT_TYPE:
         {
            mapper = m_milestoneMap::get;
            break;
         }

         case PERMANENT_RESOURCE_OBJECT_TYPE:
         case CONSUMABLE_RESOURCE_OBJECT_TYPE:
         {
            mapper = m_project::getResourceByUniqueID;
            break;
         }

         // TODO: add support for consumable resource assignments
         case PERMANENT_SCHEDULE_ALLOCATION_OBJECT_TYPE:
         {
            mapper = i -> m_project.getResourceAssignments().getByUniqueID(i);
            break;
         }
      }

      return mapper;
   }

   private Object getUserDefinedFieldValue(DataType type, Row row)
   {
      // Ideally we'd just retrieve the correct type from the result set.
      // Although the table metadata is correct, it appears that Asta is
      // writing inconsistent data types in the records themselves, hence
      // we have to work to ensure that we can convert what we get into
      // the expected type.
      Object value;
      switch (type)
      {
         case BOOLEAN:
         {
            value = getUserDefinedFieldBoolean(row);
            break;
         }

         case INTEGER:
         {
            value = getUserDefinedFieldInteger(row);
            break;
         }

         case NUMERIC:
         {
            value = getUserDefinedFieldDouble(row);
            break;
         }

         case DATE:
         {
            value = getUserDefinedFieldDate(row);
            break;
         }

         case DURATION:
         {
            value = getUserDefinedFieldDuration(row);
            break;
         }

         default:
         {
            value = getUserDefinedFieldString(row);
            break;
         }
      }
      return value;
   }

   /**
    * Retrieve a value and handle inconsistent types.
    *
    * @param row result set row
    * @return value
    */
   private Integer getUserDefinedFieldInteger(Row row)
   {
      Integer result;
      Object value = row.getObject("DATA_AS_NUMBER");
      if (value instanceof Number)
      {
         result = Integer.valueOf(((Number) value).intValue());
      }
      else
      {
         if (value instanceof String)
         {
            try
            {
               result = Integer.valueOf((String) value);
            }
            catch (NumberFormatException ex)
            {
               result = null;
            }
         }
         else
         {
            result = null;
         }
      }
      return result;
   }

   /**
    * Retrieve a value and handle inconsistent types.
    *
    * @param row result set row
    * @return value
    */
   private Double getUserDefinedFieldDouble(Row row)
   {
      Double result;
      Object value = row.getObject("DATA_AS_NUMBER");
      if (value instanceof Number)
      {
         result = Double.valueOf(((Number) value).doubleValue());
      }
      else
      {
         if (value instanceof String)
         {
            try
            {
               result = Double.valueOf((String) value);
            }
            catch (NumberFormatException ex)
            {
               result = null;
            }
         }
         else
         {
            result = null;
         }
      }
      return result;
   }

   /**
    * Retrieve a value and handle inconsistent types.
    *
    * @param row result set row
    * @return value
    */
   private Boolean getUserDefinedFieldBoolean(Row row)
   {
      Integer result = getUserDefinedFieldInteger(row);
      return Boolean.valueOf(result != null && result.intValue() == 1);
   }

   /**
    * Retrieve a value and handle inconsistent types.
    *
    * @param row result set row
    * @return value
    */
   private LocalDateTime getUserDefinedFieldDate(Row row)
   {
      Object value = row.getObject("DATA_AS_DATE");
      if (!(value instanceof LocalDateTime))
      {
         value = null;
      }
      return (LocalDateTime) value;
   }

   /**
    * Retrieve a value and handle inconsistent types.
    *
    * @param row result set row
    * @return value
    */
   private Duration getUserDefinedFieldDuration(Row row)
   {
      // TODO: displayed time units defined by DATA_AS_ID
      return Duration.getInstance(NumberHelper.getDouble(getUserDefinedFieldDouble(row)), TimeUnit.HOURS);
   }

   /**
    * Retrieve a value and handle inconsistent types.
    *
    * @param row result set row
    * @return value
    */
   private String getUserDefinedFieldString(Row row)
   {
      Object value = row.getObject("DATA_AS_NOTE");
      return value == null ? null : value.toString();
   }

   public void processCodeLibraries(List<Row> types, List<Row> typeValues, List<Row> assignments)
   {
      ActivityCodeContainer container = m_project.getActivityCodes();
      Map<Integer, ActivityCode> codeMap = new HashMap<>();
      Map<Integer, ActivityCodeValue> valueMap = new HashMap<>();

      for (Row row : types)
      {
         ActivityCode code = new ActivityCode.Builder(m_project)
            .uniqueID(row.getInteger("ID"))
            .sequenceNumber(Integer.valueOf(codeMap.size() + 1))
            .name(row.getString("NAME"))
            .build();
         container.add(code);
         codeMap.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, r -> r.getInteger("ID"), r -> r.getInteger("CODE_LIBRARY_ENTRY"), Comparator.comparing(r -> r.getString("SHORT_NAME")));
      Map<ActivityCode, ObjectSequence> sequences = new HashMap<>();

      for (Row row : typeValues)
      {
         ActivityCode code = codeMap.get(row.getInteger("CODE_LIBRARY"));
         if (code == null)
         {
            continue;
         }

         Integer id = row.getInteger("ID");
         // Note: this is a user-supplied value, there can be multiple rows with the same sort order.
         // This doesn't appear to be the same concept as the sequence number.
         //Integer sequenceNumber = row.getInteger("SORT_ORDER");
         String name = row.getString("SHORT_NAME");
         String description = row.getString("NAME");

         if (name == null || name.isEmpty())
         {
            name = description;
         }

         ObjectSequence sequence = sequences.computeIfAbsent(code, x -> new ObjectSequence(1));
         ActivityCodeValue value = new ActivityCodeValue.Builder(m_project)
            .activityCode(code)
            .uniqueID(id)
            .sequenceNumber(sequence.getNext())
            .name(name)
            .description(description)
            .parentValue(valueMap.get(row.getInteger("CODE_LIBRARY_ENTRY")))
            .build();
         code.addValue(value);
         valueMap.put(value.getUniqueID(), value);
      }

      for (Row row : assignments)
      {
         ActivityCodeValue value = valueMap.get(row.getInteger("ASSIGNED_TO"));
         if (value == null)
         {
            continue;
         }

         Task task = getTaskByAstaID(row.getInteger("CODES"));
         if (task == null)
         {
            // Task will be null here for hammock tasks
            continue;
         }

         task.addActivityCodeValue(value);
      }
   }

   /**
    * Given an Asta unique identifier, try to identify the Asta entity this relates to
    * and retrieve the equivalent MPXJ Task instance.
    *
    * @param id Asta unique ID
    * @return Task instance or null
    */
   private Task getTaskByAstaID(Integer id)
   {
      Task task = m_taskMap.get(id);
      if (task != null)
      {
         return task;
      }

      task = m_milestoneMap.get(id);
      if (task != null)
      {
         return task;
      }

      task = m_barMap.get(id);
      if (task != null)
      {
         return task;
      }

      task = m_expandedTaskMap.get(id);
      if (task != null)
      {
         return task;
      }

      return m_completedSectionMap.get(id);
   }

   /**
    * Returns true if the Asta time unit is an elapsed unit.
    *
    * @param timeUnit Asta time unit value
    * @return true if elapsed
    */
   private boolean timeUnitIsElapsed(int timeUnit)
   {
      // 10 Elapsed Year
      // 11 Elapsed Quarter
      // 12 Elapsed Month
      // 13 Elapsed Week
      // 14 Elapsed Day
      // 15 Elapsed Half Day
      // 16 Elapsed Hours
      // 17 Elapsed Minutes
      // 18 Elapsed Seconds
      // 19 Year
      // 20 Quarter
      // 21 Month
      // 22 Week
      // 23 Day
      // 24 Half Day
      // 25 Hours
      // 26 Minutes
      // 27 Seconds

      return timeUnit >= 10 && timeUnit <= 18;
   }

   private final ProjectFile m_project;
   private final EventManager m_eventManager;
   private final Map<Task, Double> m_weights = new HashMap<>();
   private final Set<Integer> m_deferredConstraintType = new HashSet<>();
   private final Map<Integer, Task> m_barMap = new HashMap<>();
   private final Map<Integer, Task> m_taskMap = new HashMap<>();
   private final Map<Integer, Task> m_milestoneMap = new HashMap<>();
   private final Map<Integer, Task> m_expandedTaskMap = new HashMap<>();
   private final Map<Integer, Task> m_completedSectionMap = new HashMap<>();

   private static final Double COMPLETE = Double.valueOf(100);
   private static final Double INCOMPLETE = Double.valueOf(0);
   private static final String LINE_BREAK = "|@|||";
   private static final RowComparator LEAF_COMPARATOR = new RowComparator("NATURAL_ORDER", "NATURAL_ORDER");
   private static final RowComparator BAR_COMPARATOR = new RowComparator("EXPANDED_TASK", "NATURAL_ORDER");

   private static final RelationType[] RELATION_TYPES =
   {
      RelationType.FINISH_START,
      RelationType.START_START,
      RelationType.FINISH_FINISH,
      RelationType.START_FINISH
   };

   private static final Map<Integer, DataType> DATA_TYPE_MAP = new HashMap<>();
   static
   {
      DATA_TYPE_MAP.put(Integer.valueOf(0), DataType.BOOLEAN); // Boolean
      DATA_TYPE_MAP.put(Integer.valueOf(6), DataType.INTEGER); // Integer
      DATA_TYPE_MAP.put(Integer.valueOf(8), DataType.NUMERIC); // Float
      DATA_TYPE_MAP.put(Integer.valueOf(9), DataType.STRING); // String
      DATA_TYPE_MAP.put(Integer.valueOf(13), DataType.DATE); // Date
      DATA_TYPE_MAP.put(Integer.valueOf(15), DataType.DURATION); // Duration
      DATA_TYPE_MAP.put(Integer.valueOf(24), DataType.STRING); // URL
   }
}