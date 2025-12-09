/*
 * file:       TableProjectReader.java
 * author:     Jon Iles
 * date:       2025-11-12
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

package org.mpxj.primavera;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeValue;
import org.mpxj.AssignmentField;
import org.mpxj.CriticalActivityType;
import org.mpxj.CurrencySymbolPosition;

import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeValue;
import org.mpxj.Rate;
import org.mpxj.ResourceAssignmentCode;
import org.mpxj.ResourceAssignmentCodeValue;
import org.mpxj.SchedulingProgressedActivities;
import org.mpxj.Duration;
import org.mpxj.ExpenseItem;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.HtmlNotes;
import org.mpxj.Notes;
import org.mpxj.NotesTopic;
import org.mpxj.NotesTopicContainer;
import org.mpxj.ParentNotes;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Step;
import org.mpxj.StructuredNotes;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ObjectSequence;
import org.mpxj.common.SlackHelper;

/**
 * Populate a ProjectFile instance from tabular P6 data.
 */
abstract class TableProjectReader
{
   /**
    * Retrieves a list of external predecessors relationships.
    *
    * @return list of external predecessors
    */
   public List<ExternalRelation> getExternalRelations()
   {
      return m_externalRelations;
   }

   /**
    * Process project properties.
    *
    * @param rows project properties data.
    */
   protected void processProjectProperties(List<Row> rows)
   {
      ProjectProperties properties = m_project.getProjectProperties();
      TableReaderHelper.populateUserDefinedFieldValues(m_state, "PROJECT", FieldTypeClass.PROJECT, properties, m_project.getProjectProperties().getUniqueID());

      if (!rows.isEmpty())
      {
         Row row = rows.get(0);
         properties.setBaselineProjectUniqueID(row.getInteger("sum_base_proj_id"));
         properties.setCreationDate(row.getDate("create_date"));
         properties.setCriticalActivityType(CriticalActivityTypeHelper.getInstanceFromXer(row.getString("critical_path_type")));
         properties.setGUID(row.getUUID("guid"));
         properties.setProjectID(row.getString("proj_short_name"));
         properties.setName(row.getString("proj_short_name")); // Temporary, updated later from the WBS
         properties.setDefaultTaskType(TaskTypeHelper.getInstanceFromXer(row.getString("def_duration_type")));
         properties.setStatusDate(row.getDate("last_recalc_date"));
         properties.setLastScheduledDate(row.getDate("last_schedule_date"));
         properties.setActivityPercentCompleteBasedOnActivitySteps(row.getBoolean("step_complete_flag"));
         properties.setFiscalYearStartMonth(row.getInteger("fy_start_month_num"));
         properties.setExportFlag(row.getBoolean("export_flag"));
         properties.setPlannedStart(row.getDate("plan_start_date"));
         properties.setScheduledFinish(row.getDate("scd_end_date"));
         properties.setMustFinishBy(row.getDate("plan_end_date"));
         properties.setCriticalSlackLimit(Duration.getInstance(row.getInt("critical_drtn_hr_cnt"), TimeUnit.HOURS));
         properties.setLocationUniqueID(row.getInteger("location_id"));
         properties.setWbsCodeSeparator(row.getString("name_sep_char"));
         properties.setActivityDefaultCalendarUniqueID(row.getInteger("clndr_id"));
         properties.setActivityIdPrefix(row.getString("task_code_prefix"));
         properties.setActivityIdSuffix(row.getInteger("task_code_base"));
         properties.setActivityIdIncrement(row.getInteger("task_code_step"));
         properties.setActivityIdIncrementBasedOnSelectedActivity(row.getBoolean("task_code_prefix_flag"));
         properties.setProjectWebsiteUrl(row.getString("proj_url"));

         if (properties.getDefaultCalendar() == null)
         {
            m_project.getProjectProperties().setDefaultCalendarUniqueID(properties.getActivityDefaultCalendarUniqueID());
         }
      }

      // Ensure we have a default calendar
      if (m_project.getDefaultCalendar() == null)
      {
         m_project.setDefaultCalendar(m_project.getCalendars().findOrCreateDefaultCalendar());
      }
   }

   /**
    * Process project code assignments.
    *
    * @param rows project code assignments
    */
   protected void processProjectCodeAssignments(List<Row> rows)
   {
      ProjectProperties properties = m_project.getProjectProperties();

      for (Row row : rows)
      {
         ProjectCode code = m_project.getProjectCodes().getByUniqueID(row.getInteger("proj_catg_type_id"));
         if (code == null)
         {
            continue;
         }

         ProjectCodeValue value = code.getValueByUniqueID(row.getInteger("proj_catg_id"));
         if (value == null)
         {
            continue;
         }

         properties.addProjectCodeValue(value);
      }
   }

   /**
    * Process activity code assignments.
    *
    * @param assignments activity code assignments
    */
   protected void processActivityCodeAssignments(List<Row> assignments)
   {
      for (Row row : assignments)
      {
         Integer taskID = row.getInteger("task_id");
         List<Row> list = m_activityCodeAssignments.computeIfAbsent(taskID, k -> new ArrayList<>());
         list.add(row);
      }
   }

   /**
    * Process resource assignment code assignments.
    *
    * @param assignments resource assignment code assignments
    */
   protected void processResourceAssignmentCodeAssignments(List<Row> assignments)
   {
      for (Row row : assignments)
      {
         Integer resourceAssignmentID = row.getInteger("taskrsrc_id");
         List<Row> list = m_resourceAssignmentCodeAssignments.computeIfAbsent(resourceAssignmentID, k -> new ArrayList<>());
         list.add(row);
      }
   }

   /**
    * Return null if string is empty, otherwise return string.
    *
    * @param text string
    * @return null if empty, otherwise string
    */
   private String nullIfEmpty(String text)
   {
      return text == null || text.isEmpty() ? null : text;
   }

   /**
    * Process tasks.
    *
    * @param wbs WBS task data
    * @param tasks task data
    * @param wbsNotes WBS note data
    * @param taskNotes task note data
    */
   protected void processTasks(List<Row> wbs, List<Row> tasks, Map<Integer, Notes> wbsNotes, Map<Integer, Notes> taskNotes)
   {
      ProjectProperties projectProperties = m_project.getProjectProperties();
      String projectName = projectProperties.getName();
      Set<Task> wbsTasks = new HashSet<>();
      boolean baselineFromCurrentProject = m_project.getProjectProperties().getBaselineProjectUniqueID() == null;

      //
      // Read WBS entries and create tasks.
      // Note that the wbs list is supplied to us in the correct order.
      //
      for (Row row : wbs)
      {
         Task task = m_project.addTask();
         task.setProject(projectName); // P6 task always belongs to project
         task.setSummary(true);
         TableReaderHelper.processFields(m_state.getWbsFields(), row, task);
         TableReaderHelper.populateUserDefinedFieldValues(m_state, "PROJWBS", FieldTypeClass.TASK, task, task.getUniqueID());
         task.setNotesObject(wbsNotes.get(task.getUniqueID()));
         m_activityClashMap.addID(task.getUniqueID());
         wbsTasks.add(task);
         m_project.getEventManager().fireTaskReadEvent(task);
      }

      //
      // Create hierarchical structure
      //
      m_project.getChildTasks().clear();
      for (Row row : wbs)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger("wbs_id"));
         Task parentTask = m_project.getTaskByUniqueID(row.getInteger("parent_wbs_id"));
         if (parentTask == null)
         {
            m_project.getChildTasks().add(task);
         }
         else
         {
            parentTask.addChildTask(task);
         }

         task.setActivityID(task.getWBS());
      }

      if (m_state.getWbsIsFullPath())
      {
         m_project.getChildTasks().forEach(t -> populateWBS(null, t));
      }

      //
      // Read Task entries and create tasks
      //

      // If the schedule is using longest path to determine critical activities
      // we currently don't have enough information to correctly set this attribute.
      // In this case we'll force the critical flag to false to avoid activities
      // being incorrectly marked as critical.
      boolean forceCriticalToFalse = projectProperties.getCriticalActivityType() == CriticalActivityType.LONGEST_PATH;

      for (Row row : tasks)
      {
         Task task;
         Integer parentTaskID = row.getInteger("wbs_id");
         Task parentTask = m_project.getTaskByUniqueID(parentTaskID);
         if (parentTask == null)
         {
            task = m_project.addTask();
         }
         else
         {
            task = parentTask.addTask();
         }
         task.setProject(projectName); // P6 task always belongs to project

         TableReaderHelper.processFields(m_state.getTaskFields(), row, task);

         task.setActualWork(WorkHelper.addWork(task.getActualWorkLabor(), task.getActualWorkNonlabor()));
         task.setPlannedWork(WorkHelper.addWork(task.getPlannedWorkLabor(), task.getPlannedWorkNonlabor()));
         task.setRemainingWork(WorkHelper.addWork(task.getRemainingWorkLabor(), task.getRemainingWorkNonlabor()));
         task.setWork(WorkHelper.addWork(task.getActualWork(), task.getRemainingWork()));

         task.setMilestone(BooleanHelper.getBoolean(MILESTONE_MAP.get(row.getString("task_type"))));
         task.setActivityStatus(ActivityStatusHelper.getInstanceFromXer(row.getString("status_code")));
         task.setActivityType(ActivityTypeHelper.getInstanceFromXer(row.getString("task_type")));

         // Only "Resource Dependent" activities consider resource calendars during scheduling in P6.
         task.setIgnoreResourceCalendar(!"TT_Rsrc".equals(row.getString("task_type")));

         task.setPercentCompleteType(PercentCompleteTypeHelper.getInstanceFromXer(row.getString("complete_pct_type")));
         task.setPercentageWorkComplete(calculateUnitsPercentComplete(row));
         task.setPercentageComplete(calculateDurationPercentComplete(row));
         task.setPhysicalPercentComplete(calculatePhysicalPercentComplete(row));

         if (m_state.getMatchPrimaveraWBS() && parentTask != null)
         {
            task.setWBS(parentTask.getWBS());
         }

         Integer originalUniqueID = row.getInteger("task_id");

         // Add User Defined Fields - before we handle ID clashes
         TableReaderHelper.populateUserDefinedFieldValues(m_state, "TASK", FieldTypeClass.TASK, task, originalUniqueID);

         populateActivityCodes(task, originalUniqueID);

         task.setNotesObject(taskNotes.get(originalUniqueID));

         task.setUniqueID(m_activityClashMap.addID(originalUniqueID));

         Integer calId = row.getInteger("clndr_id");
         ProjectCalendar cal = m_project.getCalendarByUniqueID(calId);
         task.setCalendar(cal);

         populateField(task, TaskField.START, TaskField.ACTUAL_START, TaskField.REMAINING_EARLY_START, TaskField.PLANNED_START, TaskField.EARLY_START);
         populateField(task, TaskField.FINISH, TaskField.ACTUAL_FINISH, TaskField.REMAINING_EARLY_FINISH, TaskField.PLANNED_FINISH, TaskField.EARLY_FINISH);

         // Calculate actual duration
         LocalDateTime actualStart = task.getActualStart();
         if (actualStart != null)
         {
            LocalDateTime finish = task.getActualFinish();
            if (finish == null)
            {
               finish = m_project.getProjectProperties().getStatusDate();

               // Handle the case where the actual start is after the status date
               if (finish != null && finish.isBefore(actualStart))
               {
                  finish = actualStart;
               }
            }

            if (finish != null)
            {
               Duration actualDuration;
               cal = task.getEffectiveCalendar();
               if (task.getSuspendDate() == null || finish.isBefore(task.getSuspendDate()))
               {
                  actualDuration = cal.getWork(actualStart, finish, TimeUnit.HOURS);
               }
               else
               {
                  double actualHours = cal.getWork(actualStart, task.getSuspendDate(), TimeUnit.HOURS).getDuration();
                  if (task.getResume() != null && finish.isAfter(task.getResume()))
                  {
                     actualHours += cal.getWork(task.getResume(), finish, TimeUnit.HOURS).getDuration();
                  }
                  actualDuration = Duration.getInstance(actualHours, TimeUnit.HOURS);
               }
               task.setActualDuration(actualDuration);
            }
         }

         // Calculate duration at completion
         Duration durationAtCompletion;
         if (task.getActualDuration() != null && task.getActualDuration().getDuration() != 0 && task.getRemainingDuration() != null && task.getRemainingDuration().getDuration() != 0)
         {
            durationAtCompletion = task.getEffectiveCalendar().getWork(task.getStart(), task.getFinish(), TimeUnit.HOURS);
         }
         else
         {
            durationAtCompletion = task.getActualDuration() != null && task.getActualDuration().getDuration() != 0 ? task.getActualDuration() : task.getRemainingDuration();
         }
         task.setDuration(durationAtCompletion);

         if (forceCriticalToFalse)
         {
            task.setCritical(false);
         }
         else
         {
            task.getCritical();
         }

         if (baselineFromCurrentProject)
         {
            BaselineHelper.populateBaselineFromCurrentProject(task);
         }

         //
         // The schedule only includes total slack. We'll assume this value is correct and backfill start and finish slack values.
         //
         SlackHelper.inferSlack(task);

         m_project.getEventManager().fireTaskReadEvent(task);
      }

      new ActivitySorter(wbsTasks).sort(m_project);

      updateStructure();

      //
      // We set the project name when we read the project properties, but that's just
      // the short name. The full project name lives on the first WBS item.
      // We'll leave the short name in place if there is no "project summary" WBS.
      //
      if (m_project.getChildTasks().size() == 1)
      {
         Task firstChildTask = m_project.getChildTasks().get(0);
         if (firstChildTask.getSummary())
         {
            projectProperties.setName(firstChildTask.getName());
         }
      }
   }

   private void populateWBS(Task parent, Task task)
   {
      if (parent != null)
      {
         task.setWBS(parent.getWBS() + m_project.getProjectProperties().getWbsCodeSeparator() + task.getWBS());
         task.setActivityID(task.getWBS());
      }
      task.getChildTasks().forEach(t -> populateWBS(task, t));
   }

   /**
    * Read details of any activity codes assigned to this task.
    *
    * @param task parent task
    * @param uniqueID task Unique ID
    */
   private void populateActivityCodes(Task task, Integer uniqueID)
   {
      List<Row> list = m_activityCodeAssignments.get(uniqueID);
      if (list == null)
      {
         return;
      }

      for (Row row : list)
      {
         ActivityCode code = m_project.getActivityCodes().getByUniqueID(row.getInteger("actv_code_type_id"));
         if (code == null)
         {
            continue;
         }

         ActivityCodeValue value = code.getValueByUniqueID(row.getInteger("actv_code_id"));
         if (value != null)
         {
            task.addActivityCodeValue(value);
         }
      }
   }

   /**
    * Read details of any resource assignment codes assigned to this resource assignment.
    *
    * @param resourceAssignment parent resource assignment
    */
   private void populateResourceAssignmentCodeValues(ResourceAssignment resourceAssignment)
   {
      List<Row> list = m_resourceAssignmentCodeAssignments.get(resourceAssignment.getUniqueID());
      if (list == null)
      {
         return;
      }

      for (Row row : list)
      {
         ResourceAssignmentCode code = m_project.getResourceAssignmentCodes().getByUniqueID(row.getInteger("asgnmnt_catg_type_id"));
         if (code == null)
         {
            continue;
         }

         ResourceAssignmentCodeValue value = code.getValueByUniqueID(row.getInteger("asgnmnt_catg_id"));
         if (value != null)
         {
            resourceAssignment.addResourceAssignmentCodeValue(value);
         }
      }
   }

   /**
    * Extract notes.
    *
    * @param rows notebook rows
    * @param uniqueIDColumn note unique ID column name
    * @param entityIdColumn entity id column name
    * @param textColumn text column name
    * @return note text
    */
   protected Map<Integer, Notes> getNotes(List<Row> rows, String uniqueIDColumn, String entityIdColumn, String textColumn)
   {
      Map<Integer, List<Row>> map = rows.stream().sorted(Comparator.comparing(r -> r.getInteger(uniqueIDColumn))).collect(Collectors.groupingBy(r -> r.getInteger(entityIdColumn), Collectors.mapping(r -> r, Collectors.toList())));
      NotesTopicContainer topics = m_project.getNotesTopics();
      Map<Integer, Notes> result = new HashMap<>();

      for (Map.Entry<Integer, List<Row>> entry : map.entrySet())
      {
         List<Notes> list = new ArrayList<>();
         for (Row row : entry.getValue())
         {
            HtmlNotes notes = NotesHelper.getHtmlNote(row.getString(textColumn));
            if (notes == null || notes.isEmpty())
            {
               continue;
            }

            NotesTopic topic = topics.getByUniqueID(row.getInteger("memo_type_id"));
            if (topic == null)
            {
               topic = topics.getDefaultTopic();
            }

            list.add(new StructuredNotes(m_state.getContext(), row.getInteger(uniqueIDColumn), topic, notes));
         }

         result.put(entry.getKey(), new ParentNotes(list));
      }

      return result;
   }

   /**
    * Populates a field based on planned and actual values.
    *
    * @param container field container
    * @param target target field
    * @param types fields to test for not-null values
    */
   private void populateField(FieldContainer container, FieldType target, FieldType... types)
   {
      for (FieldType type : types)
      {
         Object value = container.getCachedValue(type);
         if (value != null)
         {
            container.set(target, value);
            break;
         }
      }
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
    * Processes predecessor data.
    *
    * @param rows predecessor data
    */
   protected void processPredecessors(List<Row> rows)
   {
      for (Row row : rows)
      {
         Integer uniqueID = row.getInteger("task_pred_id");
         if (uniqueID == null)
         {
            uniqueID = m_relationObjectID.getNext();
         }

         Integer successorID = m_activityClashMap.getID(row.getInteger("task_id"));
         Integer predecessorID = m_activityClashMap.getID(row.getInteger("pred_task_id"));

         Task successorTask = m_project.getTaskByUniqueID(successorID);
         Task predecessorTask = m_project.getTaskByUniqueID(predecessorID);

         RelationType type = RelationTypeHelper.getInstanceFromXer(row.getString("pred_type"));
         Duration lag = row.getDuration("lag_hr_cnt");
         String comments = nullIfEmpty(row.getString("comments"));

         if (successorTask != null && predecessorTask != null)
         {
            Relation relation = successorTask.addPredecessor(new Relation.Builder()
               .predecessorTask(predecessorTask)
               .type(type)
               .lag(lag)
               .uniqueID(uniqueID)
               .notes(comments));

            m_project.getEventManager().fireRelationReadEvent(relation);
         }
         else
         {
            // If we're missing the predecessor or successor we assume they are external relations
            if (successorTask != null && predecessorTask == null)
            {
               ExternalRelation relation = new ExternalRelation(uniqueID, predecessorID, successorTask, type, lag, true, comments);
               m_externalRelations.add(relation);
            }
            else
            {
               if (successorTask == null && predecessorTask != null)
               {
                  ExternalRelation relation = new ExternalRelation(uniqueID, successorID, predecessorTask, type, lag, false, comments);
                  m_externalRelations.add(relation);
               }
            }
         }
      }
   }

   /**
    * Process assignment data.
    *
    * @param rows assignment data
    */
   protected void processAssignments(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(m_activityClashMap.getID(row.getInteger("task_id")));
         Integer roleID = m_state.getRoleClashMap().getID(row.getInteger("role_id"));
         Integer resourceID = row.getInteger("rsrc_id");

         // If we don't have a resource ID, but we do have a role ID then the task is being assigned to a role
         if (resourceID == null && roleID != null)
         {
            resourceID = roleID;
            roleID = null;
         }

         Resource resource = m_project.getResourceByUniqueID(resourceID);
         if (task != null && resource != null)
         {
            ProjectCalendar effectiveCalendar = task.getEffectiveCalendar();
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            TableReaderHelper.processFields(m_state.getAssignmentFields(), row, assignment);

            assignment.setWorkContour(CurveHelper.getWorkContour(m_project, row.getInteger("curv_id")));
            assignment.setRateIndex(RateTypeHelper.getInstanceFromXer(row.getString("rate_type")));
            assignment.setRole(m_project.getResourceByUniqueID(roleID));
            assignment.setOverrideRate(Rate.valueOf(row.getDouble("cost_per_qty"), TimeUnit.HOURS));
            assignment.setRateSource(RateSourceHelper.getInstanceFromXer(row.getString("cost_per_qty_source_type")));

            populateField(assignment, AssignmentField.START, AssignmentField.ACTUAL_START, AssignmentField.REMAINING_EARLY_START, AssignmentField.PLANNED_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.ACTUAL_FINISH, AssignmentField.REMAINING_EARLY_FINISH, AssignmentField.PLANNED_FINISH);

            // calculate work
            Duration remainingWork = assignment.getRemainingWork();
            Duration actualRegularWork = row.getDuration("act_reg_qty");
            Duration actualOvertimeWork = assignment.getActualOvertimeWork();
            Duration actualWork = Duration.add(actualRegularWork, actualOvertimeWork, effectiveCalendar);
            assignment.setActualWork(actualWork);
            Duration totalWork = Duration.add(actualWork, remainingWork, effectiveCalendar);
            assignment.setWork(totalWork);

            // calculate cost
            Number remainingCost = assignment.getRemainingCost();
            Number actualRegularCost = row.getDouble("act_reg_cost");
            Number actualOvertimeCost = assignment.getActualOvertimeCost();
            Number actualCost = NumberHelper.sumAsDouble(actualRegularCost, actualOvertimeCost);
            assignment.setActualCost(actualCost);
            Number totalCost = NumberHelper.sumAsDouble(actualCost, remainingCost);
            assignment.setCost(totalCost);

            // roll up to parent task
            task.setPlannedCost(NumberHelper.sumAsDouble(task.getPlannedCost(), assignment.getPlannedCost()));
            task.setActualCost(NumberHelper.sumAsDouble(task.getActualCost(), assignment.getActualCost()));
            task.setRemainingCost(NumberHelper.sumAsDouble(task.getRemainingCost(), assignment.getRemainingCost()));
            task.setCost(NumberHelper.sumAsDouble(task.getCost(), assignment.getCost()));

            assignment.setUnits(Double.valueOf(NumberHelper.getDouble(row.getDouble("target_qty_per_hr")) * 100));
            assignment.setRemainingUnits(Double.valueOf(NumberHelper.getDouble(row.getDouble("remain_qty_per_hr")) * 100));

            // Add User Defined Fields
            TableReaderHelper.populateUserDefinedFieldValues(m_state, "TASKRSRC", FieldTypeClass.ASSIGNMENT, assignment, assignment.getUniqueID());

            // Read timephased data
            assignment.setTimephasedPlannedWork(TimephasedHelper.read(effectiveCalendar, assignment.getPlannedStart(), row.getString("target_crv")));
            assignment.setTimephasedActualWork(TimephasedHelper.read(effectiveCalendar, assignment.getActualStart(), row.getString("actual_crv")));
            assignment.setTimephasedWork(TimephasedHelper.read(effectiveCalendar, assignment.getRemainingEarlyStart(), row.getString("remain_crv")));

            populateResourceAssignmentCodeValues(assignment);

            m_project.getEventManager().fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Code common to both XER and database readers to extract
    * currency format data.
    *
    * @param row row containing currency data
    */
   protected void processDefaultCurrency(Row row)
   {
      ProjectProperties properties = m_project.getProjectProperties();
      properties.setCurrencySymbol(row.getString("curr_symbol"));
      properties.setSymbolPosition(CURRENCY_SYMBOL_POSITION_MAP.get(row.getString("pos_curr_fmt_type")));
      properties.setCurrencyDigits(row.getInteger("decimal_digit_cnt"));
      properties.setThousandsSeparator(row.getString("digit_group_symbol").charAt(0));
      properties.setDecimalSeparator(row.getString("decimal_symbol").charAt(0));
   }

   /**
    * Extract expense items and add to a task.
    *
    * @param rows expense item rows
    */
   protected void processExpenseItems(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(m_activityClashMap.getID(row.getInteger("task_id")));
         if (task != null)
         {
            Double actualCost = row.getDouble("act_cost");
            Double remainingCost = row.getDouble("remain_cost");
            Double pricePerUnit = row.getDouble("cost_per_qty");

            ExpenseItem.Builder builder = new ExpenseItem.Builder(task)
               .account(m_project.getCostAccounts().getByUniqueID(row.getInteger("acct_id")))
               .accrueType(AccrueTypeHelper.getInstanceFromXer(row.getString("cost_load_type")))
               .actualCost(actualCost)
               .autoComputeActuals(row.getBoolean("auto_compute_act_flag"))
               .category(m_project.getExpenseCategories().getByUniqueID(row.getInteger("cost_type_id")))
               .description(row.getString("cost_descr"))
               .documentNumber(row.getString("po_number"))
               .name(row.getString("cost_name"))
               .plannedCost(row.getDouble("target_cost"))
               .plannedUnits(row.getDouble("target_qty"))
               .pricePerUnit(pricePerUnit)
               .remainingCost(remainingCost)
               .uniqueID(row.getInteger("cost_item_id"))
               .unitOfMeasure(row.getString("qty_name"))
               .vendor(row.getString("vendor_name"))
               .atCompletionCost(NumberHelper.sumAsDouble(actualCost, remainingCost));

            double pricePerUnitValue = NumberHelper.getDouble(pricePerUnit);
            if (pricePerUnitValue != 0.0)
            {
               Double actualUnits = Double.valueOf(NumberHelper.getDouble(actualCost) / pricePerUnitValue);
               Double remainingUnits = Double.valueOf(NumberHelper.getDouble(remainingCost) / pricePerUnitValue);
               builder.actualUnits(actualUnits)
                  .remainingUnits(remainingUnits)
                  .atCompletionUnits(NumberHelper.sumAsDouble(actualUnits, remainingUnits));
            }

            ExpenseItem ei = builder.build();
            task.getExpenseItems().add(ei);

            // Roll up to parent task
            task.setPlannedCost(NumberHelper.sumAsDouble(task.getPlannedCost(), ei.getPlannedCost()));
            task.setActualCost(NumberHelper.sumAsDouble(task.getActualCost(), ei.getActualCost()));
            task.setRemainingCost(NumberHelper.sumAsDouble(task.getRemainingCost(), ei.getRemainingCost()));
            task.setCost(NumberHelper.sumAsDouble(task.getCost(), ei.getAtCompletionCost()));
            task.setFixedCost(NumberHelper.sumAsDouble(task.getFixedCost(), ei.getAtCompletionCost()));
         }
      }
   }

   /**
    * Extract activity steps and add to their parent task.
    *
    * @param rows expense item rows
    */
   protected void processActivitySteps(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(m_activityClashMap.getID(row.getInteger("task_id")));
         if (task == null)
         {
            continue;
         }

         Step step = new Step.Builder(task)
            .uniqueID(row.getInteger("proc_id"))
            .name(row.getString("proc_name"))
            .percentComplete(row.getDouble("complete_pct"))
            .sequenceNumber(row.getInteger("seq_num"))
            .weight(row.getDouble("proc_wt"))
            .description(NotesHelper.getNotes(row.getString("proc_descr")))
            .build();

         task.getSteps().add(step);
      }
   }

   /**
    * Extract schedule options.
    *
    * @param row schedule options row
    */
   protected void processScheduleOptions(Row row)
   {
      ProjectProperties projectProperties = m_project.getProjectProperties();

      //
      // Leveling Options
      //

      // Automatically level resources when scheduling
      projectProperties.setConsiderAssignmentsInOtherProjects(row.getBoolean("level_outer_assign_flag"));
      projectProperties.setConsiderAssignmentsInOtherProjectsWithPriorityEqualHigherThan(NumberHelper.getInteger(row.getString("level_outer_assign_priority")));
      projectProperties.setPreserveScheduledEarlyAndLateDates(row.getBoolean("level_keep_sched_date_flag"));

      // Recalculate assignment costs after leveling
      projectProperties.setLevelAllResources(row.getBoolean("level_all_rsrc_flag"));
      projectProperties.setLevelResourcesOnlyWithinActivityTotalFloat(row.getBoolean("level_within_float_flag"));
      projectProperties.setPreserveMinimumFloatWhenLeveling(Duration.getInstance(NumberHelper.getInt(row.getString("level_float_thrs_cnt")), TimeUnit.HOURS));
      projectProperties.setMaxPercentToOverallocateResources(NumberHelper.getDoubleObject(row.getString("level_over_alloc_pct")));
      projectProperties.setLevelingPriorities(row.getString("levelprioritylist"));

      //
      // Schedule
      //
      projectProperties.setDataDateAndPlannedStartSetToProjectForecastStart(row.getBoolean("sched_setplantoforecast"));

      //
      // Schedule Options - General
      //
      projectProperties.setIgnoreRelationshipsToAndFromOtherProjects("SD_None".equals(row.getString("sched_outer_depend_type")));
      projectProperties.setMakeOpenEndedActivitiesCritical(row.getBoolean("sched_open_critical_flag"));
      projectProperties.setUseExpectedFinishDates(row.getBoolean("sched_use_expect_end_flag"));

      // Schedule automatically when a change affects dates - not in XER/database?

      // Level resources during scheduling - not in XER/database?
      projectProperties.setSchedulingProgressedActivities(row.getBoolean("sched_retained_logic") ? SchedulingProgressedActivities.RETAINED_LOGIC : (row.getBoolean("sched_progress_override") ? SchedulingProgressedActivities.PROGRESS_OVERRIDE : SchedulingProgressedActivities.ACTUAL_DATES));
      projectProperties.setComputeStartToStartLagFromEarlyStart(row.getBoolean("sched_lag_early_start_flag"));

      // Define critical activities as
      projectProperties.setCalculateFloatBasedOnFinishDateOfEachProject(row.getBoolean("sched_use_project_end_date_for_float"));
      projectProperties.setTotalSlackCalculationType(TotalSlackCalculationTypeHelper.getInstanceFromXer(row.getString("sched_float_type")));
      projectProperties.setRelationshipLagCalendar(RelationshipLagCalendarHelper.getInstanceFromXer(row.getString("sched_calendar_on_relationship_lag")));

      //
      // Schedule Options - Advanced
      //
      projectProperties.setCalculateMultipleFloatPaths(row.getBoolean("enable_multiple_longest_path_calc"));
      projectProperties.setCalculateMultipleFloatPathsUsingTotalFloat(row.getBoolean("use_total_float_multiple_longest_paths"));
      projectProperties.setDisplayMultipleFloatPathsEndingWithActivityUniqueID(NumberHelper.getInteger(row.getString("key_activity_for_multiple_longest_paths")));
      projectProperties.setLimitNumberOfFloatPathsToCalculate(row.getBoolean("limit_multiple_longest_path_calc"));
      projectProperties.setMaximumNumberOfFloatPathsToCalculate(NumberHelper.getInteger(row.getString("max_multiple_longest_path")));
   }

   protected void processRoleAssignments(List<Row> rows)
   {
      for (Row row : rows)
      {
         Integer resourceID = row.getInteger("rsrc_id");
         if (resourceID == null)
         {
            continue;
         }

         Integer roleID = row.getInteger("role_id");
         if (roleID == null)
         {
            continue;
         }

         Integer skillLevel = row.getInteger("skill_level");
         if (skillLevel == null)
         {
            continue;
         }

         Resource resource = m_project.getResourceByUniqueID(resourceID);
         if (resource == null)
         {
            continue;
         }

         Resource role = m_project.getResourceByUniqueID(roleID);
         if (role == null)
         {
            continue;
         }

         resource.addRoleAssignment(role, SkillLevelHelper.getInstanceFromXer(skillLevel));
      }
   }

   /**
    * Calculate the physical percent complete.
    *
    * @param row task data
    * @return percent complete
    */
   private Number calculatePhysicalPercentComplete(Row row)
   {
      return row.getDouble("phys_complete_pct");
   }

   /**
    * Calculate the units percent complete.
    *
    * @param row task data
    * @return percent complete
    */
   private Number calculateUnitsPercentComplete(Row row)
   {
      double result = 0;

      double actualWorkQuantity = NumberHelper.getDouble(row.getDouble("act_work_qty"));
      double actualEquipmentQuantity = NumberHelper.getDouble(row.getDouble("act_equip_qty"));
      double numerator = actualWorkQuantity + actualEquipmentQuantity;

      if (numerator != 0)
      {
         double remainingWorkQuantity = NumberHelper.getDouble(row.getDouble("remain_work_qty"));
         double remainingEquipmentQuantity = NumberHelper.getDouble(row.getDouble("remain_equip_qty"));
         double denominator = remainingWorkQuantity + actualWorkQuantity + remainingEquipmentQuantity + actualEquipmentQuantity;
         result = denominator == 0 ? 0 : ((numerator * 100) / denominator);
      }

      return NumberHelper.getDouble(result);
   }

   /**
    * Calculate the duration percent complete.
    *
    * @param row task data
    * @return percent complete
    */
   private Number calculateDurationPercentComplete(Row row)
   {
      double result = 0;
      double targetDuration = NumberHelper.getDouble(row.getDouble("target_drtn_hr_cnt"));
      double remainingDuration = NumberHelper.getDouble(row.getDouble("remain_drtn_hr_cnt"));

      if (targetDuration == 0)
      {
         if (remainingDuration == 0)
         {
            if ("TK_Complete".equals(row.getString("status_code")))
            {
               result = 100;
            }
         }
      }
      else
      {
         if (remainingDuration < targetDuration)
         {
            result = ((targetDuration - remainingDuration) * 100) / targetDuration;
         }
      }

      return NumberHelper.getDouble(result);
   }

   /**
    * Retrieve the default mapping between MPXJ task fields and Primavera wbs field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultWbsFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<>();

      map.put(TaskField.UNIQUE_ID, "wbs_id");
      map.put(TaskField.GUID, "guid");
      map.put(TaskField.NAME, "wbs_name");
      map.put(TaskField.REMAINING_COST, "indep_remain_total_cost");
      map.put(TaskField.REMAINING_WORK, "indep_remain_work_qty");
      map.put(TaskField.DEADLINE, "anticip_end_date");
      map.put(TaskField.WBS, "wbs_short_name");
      map.put(TaskField.SEQUENCE_NUMBER, "seq_num");
      map.put(TaskField.METHODOLOGY_GUID, "tmpl_guid");
      map.put(TaskField.ORIGINAL_BUDGET, "orig_cost");
      map.put(TaskField.ESTIMATED_WEIGHT, "est_wt");

      return map;
   }

   /**
    * Retrieve the default mapping between MPXJ task fields and Primavera task field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultTaskFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<>();

      map.put(TaskField.GUID, "guid");
      map.put(TaskField.NAME, "task_name");
      map.put(TaskField.REMAINING_DURATION, "remain_drtn_hr_cnt");
      map.put(TaskField.ACTUAL_WORK_LABOR, "act_work_qty");
      map.put(TaskField.ACTUAL_WORK_NONLABOR, "act_equip_qty");
      map.put(TaskField.REMAINING_WORK_LABOR, "remain_work_qty");
      map.put(TaskField.REMAINING_WORK_NONLABOR, "remain_equip_qty");
      map.put(TaskField.PLANNED_WORK_LABOR, "target_work_qty");
      map.put(TaskField.PLANNED_WORK_NONLABOR, "target_equip_qty");
      map.put(TaskField.PLANNED_DURATION, "target_drtn_hr_cnt");
      map.put(TaskField.CONSTRAINT_DATE, "cstr_date");
      map.put(TaskField.ACTUAL_START, "act_start_date");
      map.put(TaskField.ACTUAL_FINISH, "act_end_date");
      map.put(TaskField.LATE_START, "late_start_date");
      map.put(TaskField.LATE_FINISH, "late_end_date");
      map.put(TaskField.EARLY_START, "early_start_date");
      map.put(TaskField.EARLY_FINISH, "early_end_date");
      map.put(TaskField.REMAINING_EARLY_START, "restart_date");
      map.put(TaskField.REMAINING_EARLY_FINISH, "reend_date");
      map.put(TaskField.REMAINING_LATE_START, "rem_late_start_date");
      map.put(TaskField.REMAINING_LATE_FINISH, "rem_late_end_date");
      map.put(TaskField.PLANNED_START, "target_start_date");
      map.put(TaskField.PLANNED_FINISH, "target_end_date");
      map.put(TaskField.CONSTRAINT_TYPE, "cstr_type");
      map.put(TaskField.SECONDARY_CONSTRAINT_DATE, "cstr_date2");
      map.put(TaskField.SECONDARY_CONSTRAINT_TYPE, "cstr_type2");
      map.put(TaskField.PRIORITY, "priority_type");
      map.put(TaskField.CREATED, "create_date");
      map.put(TaskField.TYPE, "duration_type");
      map.put(TaskField.FREE_SLACK, "free_float_hr_cnt");
      map.put(TaskField.TOTAL_SLACK, "total_float_hr_cnt");
      map.put(TaskField.ACTIVITY_ID, "task_code");
      map.put(TaskField.PRIMARY_RESOURCE_UNIQUE_ID, "rsrc_id");
      map.put(TaskField.SUSPEND_DATE, "suspend_date");
      map.put(TaskField.RESUME, "resume_date");
      map.put(TaskField.EXTERNAL_EARLY_START, "external_early_start_date");
      map.put(TaskField.EXTERNAL_LATE_FINISH, "external_late_end_date");
      map.put(TaskField.LONGEST_PATH, "driving_path_flag");
      map.put(TaskField.LOCATION_UNIQUE_ID, "location_id");
      map.put(TaskField.EXPECTED_FINISH, "expect_end_date");
      map.put(TaskField.METHODOLOGY_GUID, "tmpl_guid");
      map.put(TaskField.FLOAT_PATH, "float_path");
      map.put(TaskField.FLOAT_PATH_ORDER, "float_path_order");
      map.put(TaskField.ESTIMATED_WEIGHT, "est_wt");
      map.put(TaskField.AUTO_COMPUTE_ACTUALS, "auto_compute_act_flag");

      return map;
   }

   /**
    * Retrieve the default mapping between MPXJ assignment fields and Primavera assignment field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultAssignmentFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<>();

      map.put(AssignmentField.UNIQUE_ID, "taskrsrc_id");
      map.put(AssignmentField.GUID, "guid");
      map.put(AssignmentField.REMAINING_WORK, "remain_qty");
      map.put(AssignmentField.PLANNED_WORK, "target_qty");
      map.put(AssignmentField.ACTUAL_OVERTIME_WORK, "act_ot_qty");
      map.put(AssignmentField.PLANNED_COST, "target_cost");
      map.put(AssignmentField.ACTUAL_OVERTIME_COST, "act_ot_cost");
      map.put(AssignmentField.REMAINING_COST, "remain_cost");
      map.put(AssignmentField.ACTUAL_START, "act_start_date");
      map.put(AssignmentField.ACTUAL_FINISH, "act_end_date");
      map.put(AssignmentField.PLANNED_START, "target_start_date");
      map.put(AssignmentField.PLANNED_FINISH, "target_end_date");
      map.put(AssignmentField.ASSIGNMENT_DELAY, "target_lag_drtn_hr_cnt");
      map.put(AssignmentField.CALCULATE_COSTS_FROM_UNITS, "cost_qty_link_flag");
      map.put(AssignmentField.COST_ACCOUNT_UNIQUE_ID, "acct_id");
      map.put(AssignmentField.REMAINING_EARLY_START, "restart_date");
      map.put(AssignmentField.REMAINING_EARLY_FINISH, "reend_date");
      map.put(AssignmentField.REMAINING_LATE_START, "rem_late_start_date");
      map.put(AssignmentField.REMAINING_LATE_FINISH, "rem_late_end_date");
      return map;
   }

   protected ProjectFile m_project;
   private final ClashMap m_activityClashMap = new ClashMap();
   protected TableReaderState m_state;
   private final List<ExternalRelation> m_externalRelations = new ArrayList<>();

   private final Map<Integer, List<Row>> m_activityCodeAssignments = new HashMap<>();
   private final Map<Integer, List<Row>> m_resourceAssignmentCodeAssignments = new HashMap<>();
   private final ObjectSequence m_relationObjectID = new ObjectSequence(1);

   private static final Map<String, Boolean> MILESTONE_MAP = new HashMap<>();
   static
   {
      MILESTONE_MAP.put("TT_Task", Boolean.FALSE);
      MILESTONE_MAP.put("TT_Rsrc", Boolean.FALSE);
      MILESTONE_MAP.put("TT_LOE", Boolean.FALSE);
      MILESTONE_MAP.put("TT_Mile", Boolean.TRUE);
      MILESTONE_MAP.put("TT_FinMile", Boolean.TRUE);
      MILESTONE_MAP.put("TT_WBS", Boolean.FALSE);
   }

   /*
   private static final Map<String, TimeUnit> TIME_UNIT_MAP = new HashMap<>();
   static
   {
      TIME_UNIT_MAP.put("QT_Minute", TimeUnit.MINUTES);
      TIME_UNIT_MAP.put("QT_Hour", TimeUnit.HOURS);
      TIME_UNIT_MAP.put("QT_Day", TimeUnit.DAYS);
      TIME_UNIT_MAP.put("QT_Week", TimeUnit.WEEKS);
      TIME_UNIT_MAP.put("QT_Month", TimeUnit.MONTHS);
      TIME_UNIT_MAP.put("QT_Year", TimeUnit.YEARS);
   }
   */

   private static final Map<String, CurrencySymbolPosition> CURRENCY_SYMBOL_POSITION_MAP = new HashMap<>();
   static
   {
      CURRENCY_SYMBOL_POSITION_MAP.put("#1.1", CurrencySymbolPosition.BEFORE);
      CURRENCY_SYMBOL_POSITION_MAP.put("1.1#", CurrencySymbolPosition.AFTER);
      CURRENCY_SYMBOL_POSITION_MAP.put("# 1.1", CurrencySymbolPosition.BEFORE_WITH_SPACE);
      CURRENCY_SYMBOL_POSITION_MAP.put("1.1 #", CurrencySymbolPosition.AFTER_WITH_SPACE);
   }
}