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

package net.sf.mpxj.asta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;

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

      config.setAutoCalendarUniqueID(false);

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
    * @param projectSummary project properties data.
    * @param progressPeriods progress period data.
    */
   public void processProjectProperties(Row projectSummary, List<Row> progressPeriods)
   {
      ProjectProperties ph = m_project.getProjectProperties();

      if (projectSummary != null)
      {
         ph.setDuration(projectSummary.getDuration("DURATIONHOURS"));
         ph.setStartDate(projectSummary.getDate("STARU"));
         ph.setFinishDate(projectSummary.getDate("ENE"));
         ph.setName(projectSummary.getString("SHORT_NAME"));
         ph.setAuthor(projectSummary.getString("PROJECT_BY"));
         //DURATION_TIME_UNIT
         ph.setLastSaved(projectSummary.getDate("LAST_EDITED_DATE"));
      }

      if (progressPeriods != null)
      {
         Collections.sort(progressPeriods, new Comparator<Row>()
         {
            @Override public int compare(Row o1, Row o2)
            {
               return o1.getInteger("PROGRESS_PERIODID").compareTo(o2.getInteger("PROGRESS_PERIODID"));
            }
         });

         Row lastProgressPeriod = progressPeriods.get(progressPeriods.size() - 1);
         ph.setStatusDate(lastProgressPeriod.getDate("REPORT_DATE"));
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
         resource.setUniqueID(row.getInteger("PERMANENT_RESOURCEID"));
         resource.setEmailAddress(row.getString("EMAIL_ADDRESS"));
         // EFFORT_TIME_UNIT
         resource.setName(row.getString("NASE"));
         resource.setResourceCalendar(deriveResourceCalendar(row.getInteger("CALENDAV")));
         resource.setMaxUnits(Double.valueOf(row.getDouble("AVAILABILITY").doubleValue() * 100));
         resource.setGeneric(row.getBoolean("CREATED_AS_FOLDER"));
         resource.setInitials(getInitials(resource.getName()));
      }

      //
      // Process groups
      //
      /*
            for (Row row : permanentRows)
            {
               Resource resource = m_project.getResourceByUniqueID(row.getInteger("PERMANENT_RESOURCEID"));
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
      for (Row row : consumableRows)
      {
         Resource resource = m_project.addResource();
         resource.setType(ResourceType.MATERIAL);
         resource.setUniqueID(row.getInteger("CONSUMABLE_RESOURCEID"));
         resource.setCostPerUse(row.getDouble("COST_PER_USEDEFAULTSAMOUNT"));
         resource.setPeakUnits(Double.valueOf(row.getDouble("AVAILABILITY").doubleValue() * 100));
         resource.setName(row.getString("NASE"));
         resource.setResourceCalendar(deriveResourceCalendar(row.getInteger("CALENDAV")));
         resource.setAvailableFrom(row.getDate("AVAILABLE_FROM"));
         resource.setAvailableTo(row.getDate("AVAILABLE_TO"));
         resource.setGeneric(row.getBoolean("CREATED_AS_FOLDER"));
         resource.setMaterialLabel(row.getString("MEASUREMENT"));
         resource.setInitials(getInitials(resource.getName()));
      }
   }

   /**
    * Derive a calendar for a resource.
    *
    * @param parentCalendarID calendar from which resource calendar is derived
    * @return new calendar for a resource
    */
   private ProjectCalendar deriveResourceCalendar(Integer parentCalendarID)
   {
      ProjectCalendar calendar = m_project.addDefaultDerivedCalendar();
      calendar.setUniqueID(Integer.valueOf(m_project.getProjectConfig().getNextCalendarUniqueID()));
      calendar.setParent(m_project.getCalendarByUniqueID(parentCalendarID));
      return calendar;
   }

   /**
    * Organises the data from Asta into a hierarchy and converts this into tasks.
    *
    * @param bars bar data
    * @param expandedTasks expanded task data
    * @param tasks task data
    * @param milestones milestone data
    */
   public void processTasks(List<Row> bars, List<Row> expandedTasks, List<Row> tasks, List<Row> milestones)
   {
      List<Row> parentBars = buildRowHierarchy(bars, expandedTasks, tasks, milestones);
      createTasks(m_project, "", parentBars);
      deriveProjectCalendar();
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
    * @return list containing the top level tasks
    */
   private List<Row> buildRowHierarchy(List<Row> bars, List<Row> expandedTasks, List<Row> tasks, List<Row> milestones)
   {
      //
      // Create a list of leaf nodes by merging the task and milestone lists
      //
      List<Row> leaves = new ArrayList<>();
      leaves.addAll(tasks);
      leaves.addAll(milestones);

      //
      // Sort the bars and the leaves
      //
      Collections.sort(bars, BAR_COMPARATOR);
      Collections.sort(leaves, LEAF_COMPARATOR);

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
         String barName = bar.getString("NAMH");
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
         if (rowIsBar && row.getChildRows().isEmpty())
         {
            continue;
         }

         Task task = parent.addTask();

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
               populateLeaf(row.getString("NAMH"), row.getChildRows().get(0), task);
            }
            else
            {
               populateBar(row, task);
               createTasks(task, task.getName(), row.getChildRows());
            }
         }
         else
         {
            populateLeaf(parentName, row, task);
         }

         m_eventManager.fireTaskReadEvent(task);
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
      return childRows.size() == 1 && childRows.get(0).getChildRows().isEmpty();
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
      }
      else
      {
         populateMilestone(row, task);
      }

      String name = task.getName();
      if (name == null || name.isEmpty())
      {
         task.setName(parentName);
      }
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
      //task.setDuration(row.getDuration("GIVEN_DURATIONHOURS"));

      task.setResume(row.getDate("RESUME"));
      //task.setStart(row.getDate("GIVEN_START"));
      //LATEST_PROGRESS_PERIOD
      //TASK_WORK_RATE_TIME_UNIT
      //TASK_WORK_RATE
      //PLACEMENT
      //BEEN_SPLIT
      //INTERRUPTIBLE
      //HOLDING_PIN
      ///ACTUAL_DURATIONTYPF
      //ACTUAL_DURATIONELA_MONTHS
      task.setActualDuration(row.getDuration("ACTUAL_DURATIONHOURS"));
      task.setEarlyStart(row.getDate("EARLY_START_DATE"));
      task.setLateStart(row.getDate("LATE_START_DATE"));
      task.setEarlyFinish(row.getDate("EARLY_END_DATE_RS"));
      task.setLateFinish(row.getDate("LATE_END_DATE_RS"));
      //FREE_START_DATE
      //START_CONSTRAINT_DATE
      //END_CONSTRAINT_DATE
      //task.setBaselineWork(row.getDuration("EFFORT_BUDGET"));
      //NATURAO_ORDER
      //LOGICAL_PRECEDENCE
      //SPAVE_INTEGER
      //SWIM_LANE
      //USER_PERCENT_COMPLETE
      //OVERALL_PERCENT_COMPL_WEIGHT
      task.setName(row.getString("NARE"));
      task.setNotes(getNotes(row));
      task.setActivityID(row.getString("UNIQUE_TASK_ID"));
      task.setCalendar(m_project.getCalendarByUniqueID(row.getInteger("CALENDAU")));
      //EFFORT_TIMI_UNIT
      //WORL_UNIT
      //LATEST_ALLOC_PROGRESS_PERIOD
      //WORN
      //BAR
      //CONSTRAINU
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
      task.setStart(row.getDate("STARZ"));
      task.setFinish(row.getDate("ENJ"));
      //DURATION_TIMJ_UNIT
      //UNSCHEDULABLG
      //SUBPROJECT_ID
      //ALT_ID
      //LAST_EDITED_DATE
      //LAST_EDITED_BY

      //
      // The attribute we thought contained the duration appears to be unreliable.
      // To match what we see in Asta the best way to determine the duration appears
      // to be to calculate it from the start and finish dates.
      // Note the conversion to hours is not strictly necessary, but matches the units previously used.
      //
      Duration duration = task.getEffectiveCalendar().getDuration(task.getStart(), task.getFinish());
      duration = Duration.convertUnits(duration.getDuration(), duration.getUnits(), TimeUnit.HOURS, m_project.getProjectProperties());
      task.setDuration(duration);

      //
      // Overall Percent Complete
      //
      Double overallPercentComplete = row.getPercent("OVERALL_PERCENV_COMPLETE");
      task.setOverallPercentComplete(overallPercentComplete);
      m_weights.put(task, row.getDouble("OVERALL_PERCENT_COMPL_WEIGHT"));

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
         Duration actualDuration = task.getActualDuration();
         if (duration != null && duration.getDuration() > 0 && actualDuration != null && actualDuration.getDuration() > 0)
         {
            // We have an actual duration, so we must have an actual start date
            task.setActualStart(task.getStart());

            double percentComplete = (actualDuration.getDuration() / duration.getDuration()) * 100.0;
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
      Integer calendarID = row.getInteger("_CALENDAU");
      if (calendarID == null)
      {
         calendarID = row.getInteger("_COMMON_CALENDAR");
      }

      ProjectCalendar calendar = m_project.getCalendarByUniqueID(calendarID);

      //PROJID
      task.setUniqueID(row.getInteger("BARID"));
      task.setStart(row.getDate("STARV"));
      task.setFinish(row.getDate("ENF"));
      //NATURAL_ORDER
      //SPARI_INTEGER
      task.setName(row.getString("NAMH"));
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

      task.setDuration(task.getEffectiveCalendar().getDuration(task.getStart(), task.getFinish()));
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
      //PLACEMENU
      //INTERRUPTIBLE_X
      //ACTUAL_DURATIONTYPF
      //ACTUAL_DURATIONELA_MONTHS
      //ACTUAL_DURATIONHOURS
      task.setEarlyStart(row.getDate("EARLY_START_DATE"));
      task.setLateStart(row.getDate("LATE_START_DATE"));
      //FREE_START_DATE
      //START_CONSTRAINT_DATE
      //END_CONSTRAINT_DATE
      //EFFORT_BUDGET
      //NATURAO_ORDER
      //LOGICAL_PRECEDENCE
      //SPAVE_INTEGER
      //SWIM_LANE
      //USER_PERCENT_COMPLETE
      //OVERALL_PERCENV_COMPLETE
      //OVERALL_PERCENT_COMPL_WEIGHT
      task.setName(row.getString("NARE"));
      //NOTET
      task.setActivityID(row.getString("UNIQUE_TASK_ID"));
      task.setCalendar(m_project.getCalendarByUniqueID(row.getInteger("CALENDAU")));
      //EFFORT_TIMI_UNIT
      //WORL_UNIT
      //LATEST_ALLOC_PROGRESS_PERIOD
      //WORN
      //CONSTRAINU
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
      //STARZ
      //ENJ
      //DURATION_TIMJ_UNIT
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

      processConstraints(row, task);

      m_weights.put(task, row.getDouble("OVERALL_PERCENT_COMPL_WEIGHT"));
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
      m_project.getChildTasks().forEach(task -> updateDates(task));
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
         Date actualStartDate = parentTask.getActualStart();
         Date actualFinishDate = parentTask.getActualFinish();
         Date earlyStartDate = parentTask.getEarlyStart();
         Date earlyFinishDate = parentTask.getEarlyFinish();
         Date lateStartDate = parentTask.getLateStart();
         Date lateFinishDate = parentTask.getLateFinish();

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            actualStartDate = DateHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = DateHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = DateHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = DateHelper.max(earlyFinishDate, task.getEarlyFinish());
            lateStartDate = DateHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = DateHelper.max(lateFinishDate, task.getLateFinish());

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
    * @param completedSections completed section data
    */
   public void processPredecessors(List<Row> rows, List<Row> completedSections)
   {
      Map<Integer, Integer> completedSectionMap = new HashMap<>();
      for (Row section : completedSections)
      {
         completedSectionMap.put(section.getInteger("TASK_COMPLETED_SECTIONID"), section.getInteger("TASK"));
      }

      for (Row row : rows)
      {
         Integer startTaskID = row.getInteger("START_TASK");
         Task startTask = m_project.getTaskByUniqueID(startTaskID);
         if (startTask == null)
         {
            startTaskID = completedSectionMap.get(startTaskID);
            if (startTaskID != null)
            {
               startTask = m_project.getTaskByUniqueID(startTaskID);
            }
         }

         Integer endTaskID = row.getInteger("END_TASK");
         Task endTask = m_project.getTaskByUniqueID(endTaskID);
         if (endTask == null)
         {
            endTaskID = completedSectionMap.get(endTaskID);
            if (endTaskID != null)
            {
               endTask = m_project.getTaskByUniqueID(endTaskID);
            }
         }

         if (startTask != null && endTask != null)
         {
            RelationType type = getRelationType(row.getInt("TYPI"));

            Duration startLag = row.getDuration("START_LAG_TIMEHOURS");
            Duration endLag = row.getDuration("END_LAG_TIMEHOURS");
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

            Relation relation = endTask.addPredecessor(startTask, type, lag);
            relation.setUniqueID(row.getInteger("LINKID"));

            // resolve indeterminate constraint for successor tasks
            if (m_deferredConstraintType.contains(endTask.getUniqueID()))
            {
               endTask.setConstraintType(ConstraintType.AS_LATE_AS_POSSIBLE);
               endTask.setConstraintDate(null);
            }
         }

         //PROJID
         //LINKID
         //START_LAG_TIMETYPF
         //START_LAG_TIMEELA_MONTHS
         //START_LAG_TIMEHOURS
         //END_LAG_TIMETYPF
         //END_LAG_TIMEELA_MONTHS
         //END_LAG_TIMEHOURS
         //MAXIMUM_LAGTYPF
         //MAXIMUM_LAGELA_MONTHS
         //MAXIMUM_LAGHOURS
         //STARV_DATE
         //ENF_DATE
         //CURVATURE_PERCENTAGE
         //START_LAG_PERCENT_FLOAT
         //END_LAG_PERCENT_FLOAT
         //COMMENTS
         //LINK_CATEGORY
         //START_LAG_TIME_UNIT
         //END_LAG_TIME_UNIT
         //MAXIMUM_LAG_TIME_UNIT
         //START_TASK
         //END_TASK
         //TYPI
         //START_LAG_TYPE
         //END_LAG_TYPE
         //MAINTAIN_TASK_OFFSETS
         //UNSCHEDULABLF
         //CRITICAL
         //ON_LOOP
         //MAXIMUM_LAG_MODE
         //ANNOTATE_LEAD_LAG
         //START_REPOSITION_ON_TAS_MOVE
         //END_REPOSITION_ON_TASK_MOVE
         //DRAW_CURVED_IF_VERTICAL
         //AUTOMATIC_CURVED_LI_SETTINGS
         //DRAW_CURVED_LINK_TO_LEFT
         //LOCAL_LINK
         //DRIVING
         //ALT_ID
         //LAST_EDITED_DATE
         //LAST_EDITED_BY
      }
   }

   /**
    * Process assignment data.
    *
    * @param permanentAssignments assignment data
    */
   public void processAssignments(List<Row> permanentAssignments)
   {
      for (Row row : permanentAssignments)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger("ALLOCATEE_TO"));
         Resource resource = m_project.getResourceByUniqueID(row.getInteger("PLAYER"));
         if (task != null && resource != null)
         {
            double percentComplete = row.getPercent("PERCENT_COMPLETE").doubleValue();
            Duration work = row.getWork("EFFORW");
            double actualWork = work.getDuration() * percentComplete;
            double remainingWork = work.getDuration() - actualWork;

            ResourceAssignment assignment = task.addResourceAssignment(resource);
            assignment.setUniqueID(row.getInteger("PERMANENT_SCHEDUL_ALLOCATIONID"));
            assignment.setStart(row.getDate("STARZ"));
            assignment.setFinish(row.getDate("ENJ"));
            assignment.setUnits(Double.valueOf(row.getDouble("GIVEN_ALLOCATION").doubleValue() * 100));
            assignment.setDelay(row.getDuration("DELAAHOURS"));
            assignment.setPercentageWorkComplete(Double.valueOf(percentComplete * 100));
            assignment.setWork(work);
            assignment.setActualWork(Duration.getInstance(actualWork, work.getUnits()));
            assignment.setRemainingWork(Duration.getInstance(remainingWork, work.getUnits()));

         }

         //PROJID
         //REQUIREE_BY
         //OWNED_BY_TIMESHEET_X
         //EFFORW
         //GIVEN_EFFORT
         //WORK_FROM_TASK_FACTOR
         //ALLOCATIOO
         //GIVEN_ALLOCATION
         //ALLOCATIOP_OF
         //WORM_UNIT
         //WORK_RATE_TIMF_UNIT
         //EFFORT_TIMJ_UNIT
         //WORO
         //GIVEN_WORK
         //WORL_RATE
         //GIVEN_WORK_RATE
         //TYPV
         //CALCULATEG_PARAMETER
         //BALANCINJ_PARAMETER
         //SHAREE_EFFORT
         //CONTRIBUTES_TO_ACTIVI_EFFORT
         //DELAATYPF
         //DELAAELA_MONTHS
         //DELAAHOURS
         //GIVEO_DURATIONTYPF
         //GIVEO_DURATIONELA_MONTHS
         //GIVEO_DURATIONHOURS
         //DELAY_TIMI_UNIT
         //RATE_TYPE
         //USE_TASM_CALENDAR
         //IGNORF
         //ELAPSEE
         //MAY_BE_SHORTER_THAN_TASK
         //RESUMF
         //SPAXE_INTEGER
         //USER_PERCENU_COMPLETE
         //ALLOCATIOR_GROUP
         //PRIORITC
         //ACCOUNTED_FOR_ELSEWHERE
         //DURATIOTTYPF
         //DURATIOTELA_MONTHS
         //DURATIOTHOURS
         //DURATION_TIMJ_UNIT
         //UNSCHEDULABLG
         //SUBPROJECT_ID
         //permanent_schedul_allocation_ALT_ID
         //permanent_schedul_allocation_LAST_EDITED_DATE
         //permanent_schedul_allocation_LAST_EDITED_BY
         //perm_resource_skill_PROJID
         //PERM_RESOURCE_SKILLID
         //ARR_STOUT_STSKI_APARROW_TYPE
         //ARR_STOUT_STSKI_APLENGTH
         //ARR_STOUT_STSKI_APEDGE
         //ARR_STOUT_STSKI_APBORDET_COL
         //ARR_STOUT_STSKI_APINSIDG_COL
         //ARR_STOUT_STSKI_APPLACEMENW
         //BLI_STOUT_STSKI_APBLIP_TYPE
         //BLI_STOUT_STSKI_APSCALEY
         //BLI_STOUT_STSKI_APSCALEZ
         //BLI_STOUT_STSKI_APGAP
         //BLI_STOUT_STSKI_APBORDES_COL
         //BLI_STOUT_STSKI_APINSIDF_COL
         //BLI_STOUT_STSKI_APPLACEMENV
         //LIN_STOUT_STSKI_APSCALEX
         //LIN_STOUT_STSKI_APWIDTH
         //LIN_STOUT_STSKI_APBORDER_COL
         //LIN_STOUT_STSKI_APINSIDE_COL
         //LIN_STOUT_STSKI_APLINE_TYPE
         //SKI_APFOREGROUND_FILL_COLOUR
         //SKI_APBACKGROUND_FILL_COLOUR
         //SKI_APPATTERN
         //DURATIOODEFAULTTTYPF
         //DURATIOODEFAULTTELA_MONTHS
         //DURATIOODEFAULTTHOURS
         //DELAYDEFAULTTTYPF
         //DELAYDEFAULTTELA_MONTHS
         //DELAYDEFAULTTHOURS
         //DEFAULTTALLOCATION
         //DEFAULTTWORK_FROM_ACT_FACTOR
         //DEFAULTTEFFORT
         //DEFAULTTWORL
         //DEFAULTTWORK_RATE
         //DEFAULTTWORK_UNIT
         //DEFAULTTWORK_RATE_TIME_UNIT
         //DEFAULTTEFFORT_TIMG_UNIT
         //DEFAULTTDURATION_TIMF_UNIT
         //DEFAULTTDELAY_TIME_UNIT
         //DEFAULTTTYPL
         //DEFAULTTCALCULATED_PARAMETER
         //DEFAULTTBALANCING_PARAMETER
         //DEFAULTTWORK_RATE_TYPE
         //DEFAULTTUSE_TASK_CALENDAR
         //DEFAULTTALLOC_PROPORTIONALLY
         //DEFAULTTCAN_BE_SPLIT
         //DEFAULTTCAN_BE_DELAYED
         //DEFAULTTCAN_BE_STRETCHED
         //DEFAULTTACCOUNTED__ELSEWHERE
         //DEFAULTTCONTRIBUTES_T_EFFORT
         //DEFAULTTMAY_BE_SHORTER__TASK
         //DEFAULTTSHARED_EFFORT
         //ABILITY
         //EFFECTIVENESS
         //AVAILABLF_FROM
         //AVAILABLF_TO
         //SPARO_INTEGER
         //EFFORT_TIMF_UNIT
         //ROLE
         //CREATED_AS_FOLDER
         //perm_resource_skill_ALT_ID
         //perm_resource_skill_LAST_EDITED_DATE
         //perm_resource_skill_LAST_EDITED_BY

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

      if (name != null && name.length() != 0)
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
      // and remove it's use as a task-specific calendar.
      //
      if (defaultCalendar != null)
      {
         m_project.setDefaultCalendar(defaultCalendar);
         for (Task task : m_project.getTasks())
         {
            if (task.getCalendar() == defaultCalendar)
            {
               task.setCalendar(null);
            }
         }
      }
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
      Date constraintDate = null;

      switch (row.getInt("CONSTRAINU"))
      {
         case 0:
         {
            // 0 = ASAP, 1 = ALAP, 2 = ASAP Force Critical
            if (row.getInt("PLACEMENT") == 1)
            {
               // If the task has no predecessors, the constraint type will be START_NO_EARLIER_THAN.
               // If the task has predecessors, the constraint type will be AS_LATE_AS_POSSIBLE.
               // We don't have the predecessor information at this point so we note the task Unique ID
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
         Integer id = row.getInteger("EXCEPTIONNID");
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
         map.put(row.getInteger("WORK_PATTERNID"), row);
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
      calendar.setUniqueID(calendarRow.getInteger("CALENDARID"));
      processWorkPattern(calendar, dominantWorkPatternID, workPatternMap, timeEntryMap, exceptionTypeMap);
      calendar.setName(calendarRow.getString("NAMK"));

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
               ProjectCalendarWeek week = calendar.addWorkWeek();
               week.setDateRange(new DateRange(row.getDate("START_DATE"), row.getDate("END_DATE")));
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
         for (Row row : rows)
         {
            Date startDate = row.getDate("STARU_DATE");
            Date endDate = row.getDate("ENE_DATE");

            // special case - when the exception end time is midnight, it really finishes at the end of the previous day
            if (endDate.getTime() == DateHelper.getDayStartDate(endDate).getTime())
            {
               endDate = DateHelper.addDays(endDate, -1);
            }

            calendar.addCalendarException(startDate, endDate);
         }
      }

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
   private void processWorkPattern(ProjectCalendarWeek week, Integer workPatternID, Map<Integer, Row> workPatternMap, Map<Integer, List<Row>> timeEntryMap, Map<Integer, DayType> exceptionTypeMap)
   {
      Row workPatternRow = workPatternMap.get(workPatternID);
      if (workPatternRow != null)
      {
         week.setName(workPatternRow.getString("NAMN"));

         List<Row> timeEntryRows = timeEntryMap.get(workPatternID);
         if (timeEntryRows != null)
         {
            long lastEndTime = Long.MIN_VALUE;

            // TODO: it looks like at least one PP file we've come across doesn't start from Sunday,
            // Haven't worked out how the start day is determined.
            Day currentDay = Day.SUNDAY;
            ProjectCalendarHours hours = week.addCalendarHours(currentDay);
            Arrays.fill(week.getDays(), DayType.NON_WORKING);

            for (Row row : timeEntryRows)
            {
               Date startTime = row.getDate("START_TIME");
               Date endTime = row.getDate("END_TIME");
               if (startTime == null)
               {
                  startTime = DateHelper.getDayStartDate(new Date(0));
               }

               if (endTime == null)
               {
                  endTime = DateHelper.getDayEndDate(new Date(0));
               }

               if (startTime.getTime() > endTime.getTime())
               {
                  endTime = DateHelper.addDays(endTime, 1);
               }

               if (startTime.getTime() < lastEndTime)
               {
                  currentDay = currentDay.getNextDay();
                  hours = week.addCalendarHours(currentDay);
               }

               DayType type = exceptionTypeMap.get(row.getInteger("EXCEPTIOP"));
               if (type == DayType.WORKING)
               {
                  hours.addRange(new DateRange(startTime, endTime));
                  week.setWorkingDay(currentDay, DayType.WORKING);
               }

               lastEndTime = endTime.getTime();
            }
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
      String notes = row.getString("NOTET");
      if (notes != null)
      {
         if (notes.isEmpty())
         {
            notes = null;
         }
         else
         {
            if (notes.indexOf(LINE_BREAK) != -1)
            {
               notes = notes.replace(LINE_BREAK, "\n");
            }
         }
      }
      return notes;
   }

   private ProjectFile m_project;
   private EventManager m_eventManager;
   private final Map<Task, Double> m_weights = new HashMap<>();
   private final Set<Integer> m_deferredConstraintType = new HashSet<>();

   private static final Double COMPLETE = Double.valueOf(100);
   private static final Double INCOMPLETE = Double.valueOf(0);
   private static final String LINE_BREAK = "|@|||";
   private static final RowComparator LEAF_COMPARATOR = new RowComparator("NATURAL_ORDER", "NATURAO_ORDER");
   private static final RowComparator BAR_COMPARATOR = new RowComparator("EXPANDED_TASK", "NATURAL_ORDER");

   private static final RelationType[] RELATION_TYPES =
   {
      RelationType.FINISH_START,
      RelationType.START_START,
      RelationType.FINISH_FINISH,
      RelationType.START_FINISH
   };
}