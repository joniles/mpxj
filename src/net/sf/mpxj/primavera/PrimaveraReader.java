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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
final class PrimaveraReader
{
   /**
    * Constructor.
    */
   public PrimaveraReader()
   {
      m_project = new ProjectFile();

      m_project.setAutoTaskUniqueID(false);
      m_project.setAutoResourceUniqueID(false);
      m_project.setAutoCalendarUniqueID(true);
      m_project.setAutoWBS(false);

      m_project.setTaskFieldAlias(TaskField.DATE1, "Suspend Date");
      m_project.setTaskFieldAlias(TaskField.DATE2, "Resume Date");
      m_project.setTaskFieldAlias(TaskField.TEXT1, "Code");
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
    * Process project header.
    * 
    * @param rows project header data.
    */
   public void processProjectHeader(List<Row> rows)
   {
      if (rows.isEmpty() == false)
      {
         Row row = rows.get(0);
         ProjectHeader header = m_project.getProjectHeader();
         header.setCreationDate(row.getDate("create_date"));
         header.setFinishDate(row.getDate("plan_end_date"));
         header.setName(row.getString("proj_short_name"));
         header.setStartDate(row.getDate("plan_start_date"));
         header.setProjectTitle(row.getString("proj_short_name"));
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
         ProjectCalendar calendar;
         String calendarType = row.getString("clndr_type");
         if (calendarType != null && calendarType.toUpperCase().equals("CA_RSRC"))
         {
            calendar = m_project.addResourceCalendar();
         }
         else
         {
            calendar = m_project.addBaseCalendar();
         }

         Integer id = row.getInteger("clndr_id");
         m_calMap.put(id, calendar);
         calendar.setName(row.getString("clndr_name"));

         // Process data
         String calendarData = row.getString("clndr_data");
         if (calendarData != null && !calendarData.isEmpty())
         {
            Record root = new Record(calendarData);
            // Retrieve working hours ...
            Record daysOfWeek = root.getChild("DaysOfWeek");
            if (daysOfWeek != null)
            {
               for (Record recDay : daysOfWeek.getChildren())
               {
                  // ... for each day of the week
                  Day day = Day.getInstance(Integer.parseInt(recDay.getField()));
                  // Get hours
                  List<Record> recHours = recDay.getChildren();
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
            }

            // Retrieve exceptions
            Record exceptions = root.getChild("Exceptions");
            if (exceptions == null)
            {
               continue;
            }

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

         m_project.fireCalendarReadEvent(calendar);
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
         //
         // Retrieve the core resource data
         //
         Resource resource = m_project.addResource();
         resource.setUniqueID(row.getInteger("rsrc_id"));
         resource.setName(row.getString("rsrc_name"));
         resource.setCode(row.getString("employee_code"));
         resource.setEmailAddress(row.getString("email_addr"));
         resource.setNotes(row.getString("rsrc_notes"));
         resource.setCreationDate(row.getDate("create_date"));
         resource.setType(RESOURCE_TYPE_MAP.get(row.getString("rsrc_type")));

         //
         // Attempt to locate a calendar for this resource
         //
         Integer calendarID = row.getInteger("clndr_id");
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
                  ProjectCalendar resourceCalendar = m_project.addResourceCalendar();
                  resourceCalendar.setParent(calendar);
                  resourceCalendar.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);
                  resourceCalendar.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
                  resource.setResourceCalendar(resourceCalendar);
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
                     resource.setResourceCalendar(calendar);
                  }
                  else
                  {
                     ProjectCalendar copy = m_project.addResourceCalendar();
                     copy.copy(calendar);
                     resource.setResourceCalendar(copy);
                  }
               }
            }
         }

         m_project.fireResourceReadEvent(resource);
      }
   }

   /**
    * Process tasks.
    * 
    * @param wbs WBS task data
    * @param tasks task data
    */
   public void processTasks(List<Row> wbs, List<Row> tasks)
   {
      Set<Integer> uniqueIDs = new HashSet<Integer>();

      //
      // Read WBS entries and create tasks
      //

      for (Row row : wbs)
      {
         Task task = m_project.addTask();
         Integer uniqueID = row.getInteger("wbs_id");
         uniqueIDs.add(uniqueID);

         task.setUniqueID(uniqueID);
         task.setName(row.getString("wbs_name"));
         task.setBaselineCost(row.getDouble("orig_cost"));
         task.setRemainingCost(row.getDouble("indep_remain_total_cost"));
         task.setRemainingWork(row.getDuration("indep_remain_work_qty"));
         task.setStart(row.getDate("anticip_start_date"));
         task.setFinish(row.getDate("anticip_end_date"));
         task.setDate1(row.getDate("suspend_date"));
         task.setDate2(row.getDate("resume_date"));
         task.setText1(row.getString("task_code"));
         task.setWBS(row.getString("wbs_short_name"));
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
            m_project.getChildTasks().remove(task);
            parentTask.addChildTask(task);
            task.setWBS(parentTask.getWBS() + "." + task.getWBS());
         }
      }

      //
      // Read Task entries and create tasks
      //
      int nextID = 1;
      m_clashMap.clear();
      for (Row row : tasks)
      {
         Integer uniqueID = row.getInteger("task_id");
         if (uniqueIDs.contains(uniqueID))
         {
            while (uniqueIDs.contains(Integer.valueOf(nextID)))
            {
               ++nextID;
            }
            Integer newUniqueID = Integer.valueOf(nextID);
            m_clashMap.put(uniqueID, newUniqueID);
            uniqueID = newUniqueID;
         }
         uniqueIDs.add(uniqueID);

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

         task.setUniqueID(uniqueID);
         task.setPercentageComplete(row.getDouble("phys_complete_pct"));
         task.setName(row.getString("task_name"));
         task.setRemainingDuration(row.getDuration("remain_drtn_hr_cnt"));
         task.setActualWork(row.getDuration("act_work_qty"));
         task.setRemainingWork(row.getDuration("remain_work_qty"));
         task.setBaselineWork(row.getDuration("target_work_qty"));
         task.setBaselineDuration(row.getDuration("target_drtn_hr_cnt"));
         task.setConstraintDate(row.getDate("cstr_date"));
         task.setActualStart(row.getDate("act_start_date"));
         task.setActualFinish(row.getDate("act_end_date"));
         task.setLateStart(row.getDate("late_start_date"));
         task.setLateFinish(row.getDate("late_end_date"));
         task.setFinish(row.getDate("expect_end_date"));
         task.setEarlyStart(row.getDate("early_start_date"));
         task.setEarlyFinish(row.getDate("early_end_date"));
         task.setBaselineStart(row.getDate("target_start_date"));
         task.setBaselineFinish(row.getDate("target_end_date"));
         task.setConstraintType(CONSTRAINT_TYPE_MAP.get(row.getString("cstr_type")));
         task.setPriority(PRIORITY_MAP.get(row.getString("priority_type")));
         task.setCreateDate(row.getDate("create_date"));

         Integer calId = row.getInteger("clndr_id");
         ProjectCalendar cal = m_calMap.get(calId);
         task.setCalendar(cal);

         populateField(task, TaskField.START, TaskField.BASELINE_START, TaskField.ACTUAL_START);
         populateField(task, TaskField.FINISH, TaskField.BASELINE_FINISH, TaskField.ACTUAL_FINISH);
         populateField(task, TaskField.WORK, TaskField.BASELINE_WORK, TaskField.ACTUAL_WORK);

         m_project.fireTaskReadEvent(task);
      }

      updateStructure();
   }

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
            m_project.fireRelationReadEvent(relation);
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
            assignment.setRemainingWork(row.getDuration("remain_qty"));
            assignment.setBaselineWork(row.getDuration("target_qty"));
            assignment.setActualWork(row.getDuration("act_reg_qty"));
            assignment.setBaselineCost(row.getDouble("target_cost"));
            assignment.setActualCost(row.getDouble("act_reg_cost"));
            assignment.setActualStart(row.getDate("act_start_date"));
            assignment.setActualFinish(row.getDate("act_end_date"));
            assignment.setBaselineStart(row.getDate("target_start_date"));
            assignment.setBaselineFinish(row.getDate("target_end_date"));

            populateField(assignment, AssignmentField.WORK, AssignmentField.BASELINE_WORK, AssignmentField.ACTUAL_WORK);
            populateField(assignment, AssignmentField.COST, AssignmentField.BASELINE_COST, AssignmentField.ACTUAL_COST);
            populateField(assignment, AssignmentField.START, AssignmentField.BASELINE_START, AssignmentField.ACTUAL_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.BASELINE_FINISH, AssignmentField.ACTUAL_FINISH);

            m_project.fireAssignmentReadEvent(assignment);
         }
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

   private ProjectFile m_project;
   private Map<Integer, Integer> m_clashMap = new HashMap<Integer, Integer>();
   private Map<Integer, ProjectCalendar> m_calMap = new HashMap<Integer, ProjectCalendar>();
   private DateFormat m_calendarTimeFormat = new SimpleDateFormat("HH:mm");

   private static final Map<String, ResourceType> RESOURCE_TYPE_MAP = new HashMap<String, ResourceType>();
   static
   {
      RESOURCE_TYPE_MAP.put(null, ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("RT_Labor", ResourceType.WORK);
      RESOURCE_TYPE_MAP.put("RT_Mat", ResourceType.MATERIAL);
      RESOURCE_TYPE_MAP.put("RT_Equip", ResourceType.MATERIAL);
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
}