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
import net.sf.mpxj.CurrencySymbolPosition;
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
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.utility.BooleanUtility;

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
      m_project.setAutoAssignmentUniqueID(false);
      m_project.setAutoWBS(false);

      m_project.setTaskFieldAlias(TaskField.DATE1, "Suspend Date");
      m_project.setTaskFieldAlias(TaskField.DATE2, "Resume Date");
      m_project.setTaskFieldAlias(TaskField.TEXT1, "Code");

      m_project.setResourceFieldAlias(ResourceField.NUMBER1, "Parent Resource Unique ID");
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
         header.setStartDate(row.getDate("plan_start_date")); // data_date?
         header.setProjectTitle(row.getString("proj_short_name"));
         header.setDefaultTaskType(TASK_TYPE_MAP.get(row.getString("def_duration_type")));
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
         resource.setInitials(row.getString("rsrc_short_name"));
         //resource.setMaxUnits(maxUnits); // RSRCRATE.max_qty_per_hr?
         //resource.setStandardRate(val); // RSRCRATE.cost_per_qty?
         //resource.setOvertimeRate(overtimeRate); // RSRCRATE.cost_per_qty * RSRC.ot_factor?
         //resource.setGroup(val); parent resource name?
         resource.setNumber(1, row.getInteger("parent_rsrc_id"));

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
   public void processTasks(List<Row> wbs, List<Row> tasks/*, List<Row> wbsmemos, List<Row> taskmemos*/)
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
         task.setDate(1, row.getDate("suspend_date"));
         task.setDate(2, row.getDate("resume_date"));
         task.setText(1, row.getString("task_code"));
         task.setWBS(row.getString("wbs_short_name"));
         //task.setNotes(getNotes(wbsmemos, "wbs_id", uniqueID.intValue(), "wbs_memo"));

         //
         // When reading from the database it appears that we could join
         // the PROJWBS table to the TASKSUM table to retrieve the 
         // additional summarise values noted below. Before adding this 
         // functionality it would be helpful to understand from a real
         // Primavera user if this is a valid thing to do?
         //
         //task.setActualDuration(val); // act_drtn_hr_cnt
         //task.setActualWork(val); // act_work_qty
         //task.setDuration(val); // total_drtn_hr_cnt
         //task.setBaselineDuration(val); // base_drtn_hr_cnt
         //task.setBaselineWork(val); // base_work_qty
         //task.setRemainingDuration(val); // remain_drtn_hr_cnt
         //task.setRemainingWork(val); // remain_work_qty
         //task.setTotalSlack(val); // total_float_hr_cnt
         //task.setBCWP(val); // bcwp
         //task.setBCWS(val); // bcws
         //task.setActualCost(val); // act_expense_cost+act_work_cost+ act_equip_cost+act_mat_cost
         //task.setBaselineCost(val); // base_expense_cost+base_work_cost+base_equip_cost+base_mat_cost
         //task.setRemainingCost(val); // remain_expense_cost+remain_work_cost+remain_equip_cost+remain_mat_cost          
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
            parentTask.getChildTasks().add(task);
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
         task.setType(TASK_TYPE_MAP.get(row.getString("duration_type")));
         //task.setNotes(getNotes(taskmemos, "task_id", uniqueID.intValue(), "task_memo"));
         task.setMilestone(BooleanUtility.getBoolean(MILESTONE_MAP.get(row.getString("task_type"))));
         task.setFreeSlack(row.getDuration("free_float_hr_cnt"));
         task.setTotalSlack(row.getDuration("total_float_hr_cnt"));
         task.setText(1, row.getString("task_code"));
         
         //
         // The Primavera field names listed below come from Oracle 
         // documentation, but do not appear in the XER file or
         // in the TASK table.
         // 
         //task.setDuration(val); total_drtn_hr_cnt ?
         //task.setActualDuration(val); act_drtn_hr_cnt ?                   
         //task.setStart(val); start_date ?         
         //task.setFinish(date); finish_date ?
         //task.setPercentageComplete(val); // drtn_complete_pct ?
         //task.setWork(val); // total_work_qty ?
         //task.setCost(); // EAC ?
         //task.setFixedCost(val); // total_expense_cost ?
         //task.setBaselineCost(val); // bac ?
         //task.setActualCost(val); // acwp ?
         //task.setRemainingCost(val); // etc ?

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
            assignment.setUniqueID(row.getInteger("taskrsrc_id"));
            assignment.setRemainingWork(row.getDuration("remain_qty"));
            assignment.setBaselineWork(row.getDuration("target_qty"));
            assignment.setActualWork(row.getDuration("act_reg_qty"));
            assignment.setBaselineCost(row.getDouble("target_cost"));
            assignment.setActualCost(row.getDouble("act_reg_cost")); // act_cost?
            assignment.setActualStart(row.getDate("act_start_date"));
            assignment.setActualFinish(row.getDate("act_end_date"));
            assignment.setBaselineStart(row.getDate("target_start_date"));
            assignment.setBaselineFinish(row.getDate("target_end_date"));
            assignment.setDelay(row.getDuration("target_lag_drtn_hr_cnt"));

            // Calculation below only relevant for RT_Labor?
            //assignment.setUnits(Double.valueOf(row.getDouble("target_qty_per_hr").doubleValue()*100));

            //assignment.setWork(dur); // total_qty ?

            // Calculation below only relevant for RT_Labor?
            //assignment.setRemainingWork(row.getDuration("target_qty")); // target_qty

            // Calculation below only relevant for RT_Labor?
            //assignment.setActualWork(row.getDuration("act_reg_qty")); // act_reg_qty

            // Calculation below only relevant for RT_Labor?
            //assignment.setOvertimeWork(row.getDuration("act_ot_qty")); // act_ot_qty

            //assignment.setCost(cost); // total_cost ?
            //assignment.setStart(val); // start_date ?
            //assignment.setFinish(val); // end_date ?            

            populateField(assignment, AssignmentField.WORK, AssignmentField.BASELINE_WORK, AssignmentField.ACTUAL_WORK);
            populateField(assignment, AssignmentField.COST, AssignmentField.BASELINE_COST, AssignmentField.ACTUAL_COST);
            populateField(assignment, AssignmentField.START, AssignmentField.BASELINE_START, AssignmentField.ACTUAL_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.BASELINE_FINISH, AssignmentField.ACTUAL_FINISH);

            m_project.fireAssignmentReadEvent(assignment);
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
      ProjectHeader header = m_project.getProjectHeader();
      header.setCurrencySymbol(row.getString("curr_symbol"));
      header.setSymbolPosition(CURRENCY_SYMBOL_POSITION_MAP.get(row.getString("pos_curr_fmt_type")));
      header.setCurrencyDigits(row.getInteger("decimal_digit_cnt"));
      header.setThousandsSeparator(row.getString("digit_group_symbol").charAt(0));
      header.setDecimalSeparator(row.getString("decimal_symbol").charAt(0));
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