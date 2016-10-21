/*
 * file:       PrimaveraReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       22/03/2010
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

package net.sf.mpxj.primavera;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.DataType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
final class PrimaveraReader
{
   /**
    * Constructor.
    *
    * @param udfCounters user defined field data types
    * @param resourceFields resource field mapping
    * @param wbsFields wbs field mapping
    * @param taskFields task field mapping
    * @param assignmentFields assignment field mapping
    * @param aliases alias mapping
    * @param matchPrimaveraWBS determine WBS behaviour
    */
   public PrimaveraReader(UserFieldCounters udfCounters, Map<FieldType, String> resourceFields, Map<FieldType, String> wbsFields, Map<FieldType, String> taskFields, Map<FieldType, String> assignmentFields, Map<FieldType, String> aliases, boolean matchPrimaveraWBS)
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoCalendarUniqueID(true);
      config.setAutoAssignmentUniqueID(false);
      config.setAutoWBS(false);

      m_resourceFields = resourceFields;
      m_wbsFields = wbsFields;
      m_taskFields = taskFields;
      m_assignmentFields = assignmentFields;

      applyAliases(aliases);

      m_udfCounters = udfCounters;
      m_udfCounters.reset();

      m_matchPrimaveraWBS = matchPrimaveraWBS;
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
    * @param rows project properties data.
    */
   public void processProjectProperties(List<Row> rows)
   {
      if (rows.isEmpty() == false)
      {
         Row row = rows.get(0);
         ProjectProperties properties = m_project.getProjectProperties();
         properties.setCreationDate(row.getDate("create_date"));
         properties.setFinishDate(row.getDate("plan_end_date"));
         properties.setName(row.getString("proj_short_name"));
         properties.setStartDate(row.getDate("plan_start_date")); // data_date?
         properties.setProjectTitle(row.getString("proj_short_name"));
         properties.setDefaultTaskType(TASK_TYPE_MAP.get(row.getString("def_duration_type")));
         properties.setStatusDate(row.getDate("last_recalc_date"));
      }
   }

   /**
    * Process User Defined Fields (UDF).
    *
    * @param userDefinedFields UDFs rows
    */
   public void processUserDefinedFields(List<Row> userDefinedFields)
   {
      for (Row row : userDefinedFields)
      {
         if ("TASK".equals(row.getString("table_name")))
         {
            parseTaskUDF(row);
         }
      }
   }

   /**
    * Process project calendars.
    *
    * @param rows project calendar data
    */
   public void processCalendars(List<Row> rows)
   {
      for (Row row : rows)
      {
         processCalendar(row);
      }
   }

   /**
    * Process data for an individual calendar.
    *
    * @param row calendar data
    */
   public void processCalendar(Row row)
   {
      ProjectCalendar calendar = m_project.addCalendar();

      Integer id = row.getInteger("clndr_id");
      m_calMap.put(id, calendar);
      calendar.setName(row.getString("clndr_name"));
      calendar.setMinutesPerDay(Integer.valueOf((int) NumberHelper.getDouble(row.getDouble("day_hr_cnt")) * 60));
      calendar.setMinutesPerWeek(Integer.valueOf((int) (NumberHelper.getDouble(row.getDouble("week_hr_cnt")) * 60)));
      calendar.setMinutesPerMonth(Integer.valueOf((int) (NumberHelper.getDouble(row.getDouble("month_hr_cnt")) * 60)));
      calendar.setMinutesPerYear(Integer.valueOf((int) (NumberHelper.getDouble(row.getDouble("year_hr_cnt")) * 60)));

      // Process data
      String calendarData = row.getString("clndr_data");
      if (calendarData != null && !calendarData.isEmpty())
      {
         Record root = getCalendarDataRecord(calendarData);
         if (root != null)
         {
            processCalendarDays(calendar, root);
            processCalendarExceptions(calendar, root);
         }
      }

      m_eventManager.fireCalendarReadEvent(calendar);
   }

   /**
    * Create a structured calendar Record instance from the flat calendar data.
    *
    * @param calendarData flat calendar data
    * @return calendar Record instance
    */
   private Record getCalendarDataRecord(String calendarData)
   {
      Record root;

      try
      {
         root = new Record(calendarData);
      }

      //
      // I've come across invalid calendar data in an otherwise fine Primavera
      // database belonging to a customer. We deal with this gracefully here
      // rather than propagating an exception.
      //
      catch (Exception ex)
      {
         root = null;
      }

      return root;
   }

   /**
    * Process calendar days of the week.
    *
    * @param calendar project calendar
    * @param root calendar data
    */
   private void processCalendarDays(ProjectCalendar calendar, Record root)
   {
      // Retrieve working hours ...
      Record daysOfWeek = root.getChild("DaysOfWeek");
      if (daysOfWeek != null)
      {
         for (Record dayRecord : daysOfWeek.getChildren())
         {
            processCalendarHours(calendar, dayRecord);
         }
      }
   }

   /**
    * Process hours in a working day.
    *
    * @param calendar project calendar
    * @param dayRecord working day data
    */
   private void processCalendarHours(ProjectCalendar calendar, Record dayRecord)
   {
      // ... for each day of the week
      Day day = Day.getInstance(Integer.parseInt(dayRecord.getField()));
      // Get hours
      List<Record> recHours = dayRecord.getChildren();
      if (recHours.size() == 0)
      {
         // No data -> not working
         calendar.setWorkingDay(day, false);
      }
      else
      {
         calendar.setWorkingDay(day, true);
         // Read hours
         ProjectCalendarHours hours = calendar.addCalendarHours(day);
         for (Record recWorkingHours : recHours)
         {
            if (recWorkingHours.getValue() != null)
            {
               String[] wh = recWorkingHours.getValue().split("\\|");
               try
               {
                  Date start = m_calendarTimeFormat.parse(wh[1]);
                  Date end = m_calendarTimeFormat.parse(wh[3]);
                  hours.addRange(new DateRange(start, end));
               }
               catch (ParseException e)
               {
                  // silently ignore date parse exceptions
               }
            }
         }
      }
   }

   /**
    * Process calendar exceptions.
    *
    * @param calendar project calendar
    * @param root calendar data
    */
   private void processCalendarExceptions(ProjectCalendar calendar, Record root)
   {
      // Retrieve exceptions
      Record exceptions = root.getChild("Exceptions");
      if (exceptions != null)
      {
         for (Record exception : exceptions.getChildren())
         {
            int daysFrom1900 = Integer.parseInt(exception.getValue().split("\\|")[1]);
            int daysFrom1970 = daysFrom1900 - 25567 - 2;
            // 25567 -> Number of days between 1900 and 1970.
            // During tests a 2 days offset was necessary to obtain good dates
            // However I didn't figured out why there is such a difference.
            Date startEx = new Date(daysFrom1970 * 24l * 60l * 60l * 1000);
            calendar.addCalendarException(startEx, startEx);
         }
      }
   }

   /**
    * Process resources.
    *
    * @param rows resource data
    */
   public void processResources(List<Row> rows)
   {
      for (Row row : rows)
      {
         Resource resource = m_project.addResource();
         processFields(m_resourceFields, row, resource);
         resource.setResourceCalendar(getResourceCalendar(row.getInteger("clndr_id")));
         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Retrieve the correct calendar for a resource.
    *
    * @param calendarID calendar ID
    * @return calendar for resource
    */
   private ProjectCalendar getResourceCalendar(Integer calendarID)
   {
      ProjectCalendar result = null;
      if (calendarID != null)
      {
         ProjectCalendar calendar = m_calMap.get(calendarID);
         if (calendar != null)
         {
            //
            // If the resource is linked to a base calendar, derive
            // a default calendar from the base calendar.
            //
            if (!calendar.isDerived())
            {
               ProjectCalendar resourceCalendar = m_project.addCalendar();
               resourceCalendar.setParent(calendar);
               resourceCalendar.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
               resourceCalendar.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
               resourceCalendar.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
               resourceCalendar.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
               resourceCalendar.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
               resourceCalendar.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);
               resourceCalendar.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
               result = resourceCalendar;
            }
            else
            {
               //
               // Primavera seems to allow a calendar to be shared between resources
               // whereas in the MS Project model there is a one-to-one
               // relationship. If we find a shared calendar, take a copy of it
               //
               if (calendar.getResource() == null)
               {
                  result = calendar;
               }
               else
               {
                  ProjectCalendar copy = m_project.addCalendar();
                  copy.copy(calendar);
                  result = copy;
               }
            }
         }
      }

      return result;
   }

   /**
    * Process tasks.
    *
    * @param wbs WBS task data
    * @param tasks task data
    * @param costs task costs
    */
   public void processTasks(List<Row> wbs, List<Row> tasks, List<Row> costs)
   {
      processTasks(wbs, tasks, costs, null);
   }

   /**
    * Process tasks.
    *
    * @param wbs WBS task data
    * @param tasks task data
    * @param costs task costs
    * @param udfVals User Defined Fields values data
    */
   public void processTasks(List<Row> wbs, List<Row> tasks, List<Row> costs, List<Row> udfVals)
   {
      Set<Integer> uniqueIDs = new HashSet<Integer>();
      Map<Integer, TaskCosts> taskCostsMap = processCosts(costs);

      //
      // Read WBS entries and create tasks.
      // Note that the wbs list is supplied to us in the correct order.
      //
      for (Row row : wbs)
      {
         Task task = m_project.addTask();
         processFields(m_wbsFields, row, task);
         uniqueIDs.add(task.getUniqueID());
         m_eventManager.fireTaskReadEvent(task);
      }

      //
      // Create hierarchical structure
      //
      FieldType activityIDField = getActivityIDField(m_wbsFields);
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
            m_project.getChildTasks().remove(task);
            parentTask.getChildTasks().add(task);
            task.setWBS(parentTask.getWBS() + "." + task.getWBS());
            if (activityIDField != null)
            {
               task.set(activityIDField, task.getWBS() + " " + task.getName());
            }
         }
      }

      //
      // Read Task entries and create tasks
      //
      int nextID = 1;
      m_clashMap.clear();
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

         processFields(m_taskFields, row, task);

         task.setMilestone(BooleanHelper.getBoolean(MILESTONE_MAP.get(row.getString("task_type"))));
         task.setPercentageComplete(calculatePercentComplete(row));

         if (m_matchPrimaveraWBS && parentTask != null)
         {
            task.setWBS(parentTask.getWBS());
         }

         Integer uniqueID = task.getUniqueID();
         if (uniqueIDs.contains(uniqueID))
         {
            while (uniqueIDs.contains(Integer.valueOf(nextID)))
            {
               ++nextID;
            }
            Integer newUniqueID = Integer.valueOf(nextID);
            m_clashMap.put(uniqueID, newUniqueID);
            uniqueID = newUniqueID;
            task.setUniqueID(uniqueID);
         }
         uniqueIDs.add(uniqueID);

         //
         // Apply costs if we have any
         //
         TaskCosts taskCosts = taskCostsMap.get(row.getInteger("task_id"));
         if (taskCosts != null)
         {
            task.setActualCost(taskCosts.getActual());
            task.setCost(taskCosts.getPlanned());
            task.setRemainingCost(taskCosts.getRemaining());
         }

         Integer calId = row.getInteger("clndr_id");
         ProjectCalendar cal = m_calMap.get(calId);
         task.setCalendar(cal);

         Date startDate = row.getDate("act_start_date") == null ? row.getDate("restart_date") : row.getDate("act_start_date");
         task.setStart(startDate);
         Date endDate = row.getDate("act_end_date") == null ? row.getDate("reend_date") : row.getDate("act_end_date");
         task.setFinish(endDate);

         populateField(task, TaskField.WORK, TaskField.BASELINE_WORK, TaskField.ACTUAL_WORK);

         // Add User Defined Fields
         List<Row> taskUDF = getTaskUDF(uniqueID, udfVals);
         for (Row r : taskUDF)
         {
            addTaskUDFValue(task, r);
         }

         m_eventManager.fireTaskReadEvent(task);
      }

      sortActivities(activityIDField, m_project);
      updateStructure();
      updateDates();
   }

   /**
    * Determine which field the Activity ID has been mapped to.
    *
    * @param map field map
    * @return field
    */
   private FieldType getActivityIDField(Map<FieldType, String> map)
   {
      FieldType result = null;
      for (Map.Entry<FieldType, String> entry : map.entrySet())
      {
         if (entry.getValue().equals("task_code"))
         {
            result = entry.getKey();
            break;
         }
      }
      return result;
   }

   /**
    * Summarise cost values for each task.
    *
    * @param costs list of cost rows
    * @return map of task IDs to costs
    */
   private Map<Integer, TaskCosts> processCosts(List<Row> costs)
   {
      Map<Integer, TaskCosts> map = new HashMap<Integer, TaskCosts>();
      for (Row cost : costs)
      {
         Integer taskID = cost.getInteger("task_id");
         TaskCosts taskCosts = map.get(taskID);
         if (taskCosts == null)
         {
            taskCosts = new TaskCosts();
            map.put(taskID, taskCosts);
         }

         taskCosts.addActual(cost.getDouble("act_cost"));
         taskCosts.addPlanned(cost.getDouble("target_cost"));
         taskCosts.addRemaining(cost.getDouble("remain_cost"));
      }

      return map;
   }

   /**
    * Configure a new user defined field.
    *
    * @param type field type
    * @param name field name
    */
   private void addUserDefinedField(UserFieldDataType type, String name)
   {
      try
      {
         TaskField taskField;

         do
         {
            String fieldName = m_udfCounters.nextName(type);
            taskField = TaskField.valueOf(fieldName);
         }
         while (m_taskFields.containsKey(taskField) || m_wbsFields.containsKey(taskField));

         m_project.getCustomFields().getCustomField(taskField).setAlias(name);
      }

      catch (Exception ex)
      {
         //
         // SF#227: If we get an exception thrown here... it's likely that
         // we've run out of user defined fields, for example
         // there are only 30 TEXT fields. We'll ignore this: the user
         // defined field won't be mapped to an alias, so we'll
         // ignore it when we read in the values.
         //
      }
   }

   /**
    * Parse a user defined field for a task.
    *
    * @param row UDF data
    */
   private void parseTaskUDF(Row row)
   {
      Integer fieldId = Integer.valueOf(row.getString("udf_type_id"));
      String fieldType = row.getString("logical_data_type");
      String fieldName = row.getString("udf_type_label");

      m_udfMap.put(fieldId, fieldName);
      addUserDefinedField(UserFieldDataType.valueOf(fieldType), fieldName);
   }

   /**
    * Adds a user defined field value to a task.
    *
    * @param task Task instance
    * @param row UDF data
    */
   private void addTaskUDFValue(Task task, Row row)
   {
      Integer fieldId = Integer.valueOf(row.getString("udf_type_id"));
      String fieldName = m_udfMap.get(fieldId);
      Object value = null;

      FieldType field = m_project.getCustomFields().getFieldByAlias(FieldTypeClass.TASK, fieldName);
      if (field != null)
      {
         DataType fieldType = field.getDataType();

         switch (fieldType)
         {
            case DATE:
            {
               value = row.getDate("udf_date");
               break;
            }

            case CURRENCY:
            case NUMERIC:
            {
               value = row.getDouble("udf_number");
               break;
            }

            case GUID:
            case INTEGER:
            {
               value = row.getInteger("udf_code_id");
               break;
            }

            default:
            {
               value = row.getString("udf_text");
               break;
            }
         }

         task.set(field, value);
      }
   }

   /**
    * Retrieve the user defined values for a given task.
    *
    * @param taskID target task ID
    * @param udfs user defined fields
    * @return user defined fields for the target task
    */
   private List<Row> getTaskUDF(Integer taskID, List<Row> udfs)
   {
      List<Row> udf = new LinkedList<Row>();

      if (udfs != null)
      {
         for (Row row : udfs)
         {
            if (taskID.equals(row.getInteger("fk_id")))
            {
               udf.add(row);
            }
         }
      }

      return udf;
   }

   /*
      private String getNotes(List<Row> notes, String keyField, int keyValue, String notesField)
      {
         String result = null;
         for (Row row : notes)
         {
            if (row.getInt(keyField) == keyValue)
            {
               result = row.getString(notesField);
               break;
            }
         }
         return result;
      }
   */

   /**
    * Populates a field based on baseline and actual values.
    *
    * @param container field container
    * @param target target field
    * @param baseline baseline field
    * @param actual actual field
    */
   private void populateField(FieldContainer container, FieldType target, FieldType baseline, FieldType actual)
   {
      Object value = container.getCachedValue(actual);
      if (value == null)
      {
         value = container.getCachedValue(baseline);
      }
      container.set(target, value);
   }

   /**
    * Ensure activities are sorted into Activity ID order to match Primavera.
    *
    * @param activityIDField field containing the Activity ID value
    * @param container object containing the tasks to process
    */
   private void sortActivities(final FieldType activityIDField, ChildTaskContainer container)
   {
      // Do we have any tasks?
      List<Task> tasks = container.getChildTasks();
      if (!tasks.isEmpty())
      {
         for (Task task : tasks)
         {
            //
            // Sort child activities
            //
            sortActivities(activityIDField, task);

            //
            // Sort Order:
            // 1. Activities come first
            // 2. WBS come last
            // 3. Activities ordered by activity ID
            // 4. WBS ordered by ID
            //
            Collections.sort(tasks, new Comparator<Task>()
            {
               @Override public int compare(Task t1, Task t2)
               {
                  boolean t1HasChildren = !t1.getChildTasks().isEmpty();
                  boolean t2HasChildren = !t2.getChildTasks().isEmpty();

                  // Both are WBS
                  if (t1HasChildren && t2HasChildren)
                  {
                     return t1.getID().compareTo(t2.getID());
                  }

                  // Both are activities
                  if (!t1HasChildren && !t2HasChildren)
                  {
                     String activityID1 = (String) t1.getCurrentValue(activityIDField);
                     String activityID2 = (String) t2.getCurrentValue(activityIDField);
                     return activityID1.compareTo(activityID2);
                  }

                  // One activity one WBS
                  return t1HasChildren ? 1 : -1;
               }
            });
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
    * The Primavera WBS entries we read in as tasks have user-entered start and end dates
    * which aren't calculated or adjusted based on the child task dates. We try
    * to compensate for this by using these user-entered dates as baseline dates, and
    * deriving the planned start, actual start, planned finish and actual finish from
    * the child tasks. This method recursively descends through the tasks to do this.
    */
   private void updateDates()
   {
      for (Task task : m_project.getChildTasks())
      {
         updateDates(task);
      }
   }

   /**
    * See the notes above.
    *
    * @param parentTask parent task.
    */
   private void updateDates(Task parentTask)
   {
      int finished = 0;
      Date plannedStartDate = parentTask.getStart();
      Date plannedFinishDate = parentTask.getFinish();
      Date actualStartDate = parentTask.getActualStart();
      Date actualFinishDate = parentTask.getActualFinish();

      for (Task task : parentTask.getChildTasks())
      {
         updateDates(task);

         if (plannedStartDate == null || DateHelper.compare(plannedStartDate, task.getStart()) > 0)
         {
            plannedStartDate = task.getStart();
         }

         if (actualStartDate == null || DateHelper.compare(actualStartDate, task.getActualStart()) > 0)
         {
            actualStartDate = task.getActualStart();
         }

         if (plannedFinishDate == null || DateHelper.compare(plannedFinishDate, task.getFinish()) < 0)
         {
            plannedFinishDate = task.getFinish();
         }

         if (actualFinishDate == null || DateHelper.compare(actualFinishDate, task.getActualFinish()) < 0)
         {
            actualFinishDate = task.getFinish();
         }

         if (task.getActualFinish() != null)
         {
            ++finished;
         }
      }

      parentTask.setStart(plannedStartDate);
      parentTask.setFinish(plannedFinishDate);
      parentTask.setActualStart(actualStartDate);

      //
      // Only if all child tasks have actual finish dates do we
      // set the actual finish date on the parent task.
      //
      if (finished == parentTask.getChildTasks().size())
      {
         parentTask.setActualFinish(actualFinishDate);
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
         Task currentTask = m_project.getTaskByUniqueID(mapTaskID(row.getInteger("task_id")));
         Task predecessorTask = m_project.getTaskByUniqueID(mapTaskID(row.getInteger("pred_task_id")));
         if (currentTask != null && predecessorTask != null)
         {
            RelationType type = RELATION_TYPE_MAP.get(row.getString("pred_type"));
            Duration lag = row.getDuration("lag_hr_cnt");
            Relation relation = currentTask.addPredecessor(predecessorTask, type, lag);
            m_eventManager.fireRelationReadEvent(relation);
         }
      }
   }

   /**
    * Process assignment data.
    *
    * @param rows assignment data
    */
   public void processAssignments(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(mapTaskID(row.getInteger("task_id")));
         Resource resource = m_project.getResourceByUniqueID(row.getInteger("rsrc_id"));
         if (task != null && resource != null)
         {
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            processFields(m_assignmentFields, row, assignment);

            populateField(assignment, AssignmentField.WORK, AssignmentField.BASELINE_WORK, AssignmentField.ACTUAL_WORK);
            populateField(assignment, AssignmentField.COST, AssignmentField.BASELINE_COST, AssignmentField.ACTUAL_COST);
            populateField(assignment, AssignmentField.START, AssignmentField.BASELINE_START, AssignmentField.ACTUAL_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.BASELINE_FINISH, AssignmentField.ACTUAL_FINISH);

            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Code common to both XER and database readers to extract
    * currency format data.
    *
    * @param row row containing currency data
    */
   public void processDefaultCurrency(Row row)
   {
      ProjectProperties properties = m_project.getProjectProperties();
      properties.setCurrencySymbol(row.getString("curr_symbol"));
      properties.setSymbolPosition(CURRENCY_SYMBOL_POSITION_MAP.get(row.getString("pos_curr_fmt_type")));
      properties.setCurrencyDigits(row.getInteger("decimal_digit_cnt"));
      properties.setThousandsSeparator(row.getString("digit_group_symbol").charAt(0));
      properties.setDecimalSeparator(row.getString("decimal_symbol").charAt(0));
   }

   /**
    * Generic method to extract Primavera fields and assign to MPXJ fields.
    *
    * @param map map of MPXJ field types and Primavera field names
    * @param row Primavera data container
    * @param container MPXJ data contain
    */
   private void processFields(Map<FieldType, String> map, Row row, FieldContainer container)
   {
      for (Map.Entry<FieldType, String> entry : map.entrySet())
      {
         FieldType field = entry.getKey();
         String name = entry.getValue();

         Object value;
         switch (field.getDataType())
         {
            case INTEGER:
            {
               value = row.getInteger(name);
               break;
            }

            case BOOLEAN:
            {
               value = Boolean.valueOf(row.getBoolean(name));
               break;
            }

            case DATE:
            {
               value = row.getDate(name);
               break;
            }

            case CURRENCY:
            case NUMERIC:
            case PERCENTAGE:
            {
               value = row.getDouble(name);
               break;
            }

            case DELAY:
            case WORK:
            case DURATION:
            {
               value = row.getDuration(name);
               break;
            }

            case RESOURCE_TYPE:
            {
               value = RESOURCE_TYPE_MAP.get(row.getString(name));
               break;
            }

            case TASK_TYPE:
            {
               value = TASK_TYPE_MAP.get(row.getString(name));
               break;
            }

            case CONSTRAINT:
            {
               value = CONSTRAINT_TYPE_MAP.get(row.getString(name));
               break;
            }

            case PRIORITY:
            {
               value = PRIORITY_MAP.get(row.getString(name));
               break;
            }

            case GUID:
            {
               value = row.getUUID(name);
               break;
            }

            default:
            {
               value = row.getString(name);
               break;
            }
         }

         container.set(field, value);
      }
   }

   /**
    * Deals with the case where we have had to map a task ID to a new value.
    *
    * @param id task ID from database
    * @return mapped task ID
    */
   private Integer mapTaskID(Integer id)
   {
      Integer mappedID = m_clashMap.get(id);
      if (mappedID == null)
      {
         mappedID = id;
      }
      return (mappedID);
   }

   /**
    * Apply aliases to task and resource fields.
    *
    * @param aliases map of aliases
    */
   private void applyAliases(Map<FieldType, String> aliases)
   {
      CustomFieldContainer fields = m_project.getCustomFields();
      for (Map.Entry<FieldType, String> entry : aliases.entrySet())
      {
         fields.getCustomField(entry.getKey()).setAlias(entry.getValue());
      }
   }

   /**
    * Determine which type of percent complete is used on on this task,
    * and calculate the required value.
    *
    * @param row task data
    * @return percent complete value
    */
   private Number calculatePercentComplete(Row row)
   {
      Number result;
      switch (PercentCompleteType.getInstance(row.getString("complete_pct_type")))
      {
         case UNITS:
         {
            result = calculateUnitsPercentComplete(row);
            break;
         }

         case DURATION:
         {
            result = calculateDurationPercentComplete(row);
            break;
         }

         default:
         {
            result = calculatePhysicalPercentComplete(row);
            break;
         }
      }

      return result;
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
      double targetDuration = row.getDuration("target_drtn_hr_cnt").getDuration();
      double remainingDuration = row.getDuration("remain_drtn_hr_cnt").getDuration();

      if (targetDuration == 0)
      {
         if (remainingDuration == 0)
         {
            if (row.getString("status_code").equals("TK_Complete"))
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
    * Retrieve the default mapping between MPXJ resource fields and Primavera resource field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultResourceFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<FieldType, String>();

      map.put(ResourceField.UNIQUE_ID, "rsrc_id");
      map.put(ResourceField.GUID, "guid");
      map.put(ResourceField.NAME, "rsrc_name");
      map.put(ResourceField.CODE, "employee_code");
      map.put(ResourceField.EMAIL_ADDRESS, "email_addr");
      map.put(ResourceField.NOTES, "rsrc_notes");
      map.put(ResourceField.CREATED, "create_date");
      map.put(ResourceField.TYPE, "rsrc_type");
      map.put(ResourceField.INITIALS, "rsrc_short_name");
      map.put(ResourceField.PARENT_ID, "parent_rsrc_id");

      return map;
   }

   /**
    * Retrieve the default mapping between MPXJ task fields and Primavera wbs field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultWbsFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<FieldType, String>();

      map.put(TaskField.UNIQUE_ID, "wbs_id");
      map.put(TaskField.GUID, "guid");
      map.put(TaskField.NAME, "wbs_name");
      map.put(TaskField.BASELINE_COST, "orig_cost");
      map.put(TaskField.REMAINING_COST, "indep_remain_total_cost");
      map.put(TaskField.REMAINING_WORK, "indep_remain_work_qty");
      map.put(TaskField.BASELINE_START, "anticip_start_date");
      map.put(TaskField.BASELINE_FINISH, "anticip_end_date");
      map.put(TaskField.DATE1, "suspend_date");
      map.put(TaskField.DATE2, "resume_date");
      map.put(TaskField.TEXT1, "task_code");
      map.put(TaskField.WBS, "wbs_short_name");

      return map;
   }

   /**
    * Retrieve the default mapping between MPXJ task fields and Primavera task field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultTaskFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<FieldType, String>();

      map.put(TaskField.UNIQUE_ID, "task_id");
      map.put(TaskField.GUID, "guid");
      map.put(TaskField.NAME, "task_name");
      map.put(TaskField.REMAINING_DURATION, "remain_drtn_hr_cnt");
      map.put(TaskField.ACTUAL_WORK, "act_work_qty");
      map.put(TaskField.REMAINING_WORK, "remain_work_qty");
      map.put(TaskField.BASELINE_WORK, "target_work_qty");
      map.put(TaskField.BASELINE_DURATION, "target_drtn_hr_cnt");
      map.put(TaskField.CONSTRAINT_DATE, "cstr_date");
      map.put(TaskField.ACTUAL_START, "act_start_date");
      map.put(TaskField.ACTUAL_FINISH, "act_end_date");
      map.put(TaskField.LATE_START, "late_start_date");
      map.put(TaskField.LATE_FINISH, "late_end_date");
      map.put(TaskField.EARLY_START, "early_start_date");
      map.put(TaskField.EARLY_FINISH, "early_end_date");
      map.put(TaskField.BASELINE_START, "target_start_date");
      map.put(TaskField.BASELINE_FINISH, "target_end_date");
      map.put(TaskField.CONSTRAINT_TYPE, "cstr_type");
      map.put(TaskField.PRIORITY, "priority_type");
      map.put(TaskField.CREATED, "create_date");
      map.put(TaskField.TYPE, "duration_type");
      map.put(TaskField.FREE_SLACK, "free_float_hr_cnt");
      map.put(TaskField.TOTAL_SLACK, "total_float_hr_cnt");
      map.put(TaskField.TEXT1, "task_code");
      map.put(TaskField.TEXT2, "task_type");
      map.put(TaskField.TEXT3, "status_code");
      map.put(TaskField.NUMBER1, "rsrc_id");

      return map;
   }

   /**
    * Retrieve the default mapping between MPXJ assignment fields and Primavera assignment field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultAssignmentFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<FieldType, String>();

      map.put(AssignmentField.UNIQUE_ID, "taskrsrc_id");
      map.put(AssignmentField.GUID, "guid");
      map.put(AssignmentField.REMAINING_WORK, "remain_qty");
      map.put(AssignmentField.BASELINE_WORK, "target_qty");
      map.put(AssignmentField.ACTUAL_WORK, "act_reg_qty");
      map.put(AssignmentField.BASELINE_COST, "target_cost");
      map.put(AssignmentField.ACTUAL_COST, "act_reg_cost");
      map.put(AssignmentField.ACTUAL_START, "act_start_date");
      map.put(AssignmentField.ACTUAL_FINISH, "act_end_date");
      map.put(AssignmentField.BASELINE_START, "target_start_date");
      map.put(AssignmentField.BASELINE_FINISH, "target_end_date");
      map.put(AssignmentField.ASSIGNMENT_DELAY, "target_lag_drtn_hr_cnt");

      return map;
   }

   /**
    * Retrieve the default aliases to be applied to MPXJ task and resource fields.
    *
    * @return map of aliases
    */
   public static Map<FieldType, String> getDefaultAliases()
   {
      Map<FieldType, String> map = new HashMap<FieldType, String>();

      map.put(TaskField.DATE1, "Suspend Date");
      map.put(TaskField.DATE2, "Resume Date");
      map.put(TaskField.TEXT1, "Code");
      map.put(TaskField.TEXT2, "Activity Type");
      map.put(TaskField.TEXT3, "Status");
      map.put(TaskField.NUMBER1, "Primary Resource Unique ID");

      return map;
   }

   private ProjectFile m_project;
   private EventManager m_eventManager;
   private Map<Integer, Integer> m_clashMap = new HashMap<Integer, Integer>();
   private Map<Integer, ProjectCalendar> m_calMap = new HashMap<Integer, ProjectCalendar>();
   private DateFormat m_calendarTimeFormat = new SimpleDateFormat("HH:mm");
   private Map<Integer, String> m_udfMap = new HashMap<Integer, String>();
   private final UserFieldCounters m_udfCounters;
   private Map<FieldType, String> m_resourceFields;
   private Map<FieldType, String> m_wbsFields;
   private Map<FieldType, String> m_taskFields;
   private Map<FieldType, String> m_assignmentFields;
   private final boolean m_matchPrimaveraWBS;

   private static final Map<String, ResourceType> RESOURCE_TYPE_MAP = new HashMap<String, ResourceType>();
   static
   {
      RESOURCE_TYPE_MAP.put(null, ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("RT_Labor", ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("RT_Mat", ResourceType.MATERIAL);
      RESOURCE_TYPE_MAP.put("RT_Equip", ResourceType.WORK);
   }

   private static final Map<String, ConstraintType> CONSTRAINT_TYPE_MAP = new HashMap<String, ConstraintType>();
   static
   {
      CONSTRAINT_TYPE_MAP.put("CS_MSO", ConstraintType.MUST_START_ON);
      CONSTRAINT_TYPE_MAP.put("CS_MSOB", ConstraintType.START_NO_LATER_THAN);
      CONSTRAINT_TYPE_MAP.put("CS_MSOA", ConstraintType.START_NO_EARLIER_THAN);
      CONSTRAINT_TYPE_MAP.put("CS_MEO", ConstraintType.MUST_FINISH_ON);
      CONSTRAINT_TYPE_MAP.put("CS_MEOB", ConstraintType.FINISH_NO_LATER_THAN);
      CONSTRAINT_TYPE_MAP.put("CS_MEOA", ConstraintType.FINISH_NO_EARLIER_THAN);
      CONSTRAINT_TYPE_MAP.put("CS_ALAP", ConstraintType.AS_LATE_AS_POSSIBLE);
      CONSTRAINT_TYPE_MAP.put("CS_MANDSTART", ConstraintType.MUST_START_ON);
      CONSTRAINT_TYPE_MAP.put("CS_MANDFIN", ConstraintType.MUST_FINISH_ON);
   }

   private static final Map<String, Priority> PRIORITY_MAP = new HashMap<String, Priority>();
   static
   {
      PRIORITY_MAP.put("PT_Top", Priority.getInstance(Priority.HIGHEST));
      PRIORITY_MAP.put("PT_High", Priority.getInstance(Priority.HIGH));
      PRIORITY_MAP.put("PT_Normal", Priority.getInstance(Priority.MEDIUM));
      PRIORITY_MAP.put("PT_Low", Priority.getInstance(Priority.LOW));
      PRIORITY_MAP.put("PT_Lowest", Priority.getInstance(Priority.LOWEST));
   }

   private static final Map<String, RelationType> RELATION_TYPE_MAP = new HashMap<String, RelationType>();
   static
   {
      RELATION_TYPE_MAP.put("PR_FS", RelationType.FINISH_START);
      RELATION_TYPE_MAP.put("PR_FF", RelationType.FINISH_FINISH);
      RELATION_TYPE_MAP.put("PR_SS", RelationType.START_START);
      RELATION_TYPE_MAP.put("PR_SF", RelationType.START_FINISH);
   }

   private static final Map<String, TaskType> TASK_TYPE_MAP = new HashMap<String, TaskType>();
   static
   {
      TASK_TYPE_MAP.put("DT_FixedDrtn", TaskType.FIXED_DURATION);
      TASK_TYPE_MAP.put("DT_FixedQty", TaskType.FIXED_UNITS);
      TASK_TYPE_MAP.put("DT_FixedDUR2", TaskType.FIXED_WORK);
      TASK_TYPE_MAP.put("DT_FixedRate", TaskType.FIXED_WORK);
   }

   private static final Map<String, Boolean> MILESTONE_MAP = new HashMap<String, Boolean>();
   static
   {
      MILESTONE_MAP.put("TT_Task", Boolean.FALSE);
      MILESTONE_MAP.put("TT_Rsrc", Boolean.FALSE);
      MILESTONE_MAP.put("TT_LOE", Boolean.FALSE);
      MILESTONE_MAP.put("TT_Mile", Boolean.TRUE);
      MILESTONE_MAP.put("TT_FinMile", Boolean.TRUE);
      MILESTONE_MAP.put("TT_WBS", Boolean.FALSE);
   }

   private static final Map<String, CurrencySymbolPosition> CURRENCY_SYMBOL_POSITION_MAP = new HashMap<String, CurrencySymbolPosition>();
   static
   {
      CURRENCY_SYMBOL_POSITION_MAP.put("#1.1", CurrencySymbolPosition.BEFORE);
      CURRENCY_SYMBOL_POSITION_MAP.put("1.1#", CurrencySymbolPosition.AFTER);
      CURRENCY_SYMBOL_POSITION_MAP.put("# 1.1", CurrencySymbolPosition.BEFORE_WITH_SPACE);
      CURRENCY_SYMBOL_POSITION_MAP.put("1.1 #", CurrencySymbolPosition.AFTER_WITH_SPACE);
   }
}