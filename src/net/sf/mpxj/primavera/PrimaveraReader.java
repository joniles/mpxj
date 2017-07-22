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
import java.util.ArrayList;
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
import net.sf.mpxj.Availability;
import net.sf.mpxj.AvailabilityTable;
import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
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
import net.sf.mpxj.Rate;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
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
    * @param taskUdfCounters UDF counters for tasks
    * @param resourceUdfCounters UDF counters for resources
    * @param assignmentUdfCounters UDF counters for assignments
    * @param resourceFields resource field mapping
    * @param wbsFields wbs field mapping
    * @param taskFields task field mapping
    * @param assignmentFields assignment field mapping
    * @param aliases alias mapping
    * @param matchPrimaveraWBS determine WBS behaviour
    */
   public PrimaveraReader(UserFieldCounters taskUdfCounters, UserFieldCounters resourceUdfCounters, UserFieldCounters assignmentUdfCounters, Map<FieldType, String> resourceFields, Map<FieldType, String> wbsFields, Map<FieldType, String> taskFields, Map<FieldType, String> assignmentFields, Map<FieldType, String> aliases, boolean matchPrimaveraWBS)
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

      m_taskUdfCounters = taskUdfCounters;
      m_taskUdfCounters.reset();
      m_resourceUdfCounters = resourceUdfCounters;
      m_resourceUdfCounters.reset();
      m_assignmentUdfCounters = assignmentUdfCounters;
      m_assignmentUdfCounters.reset();

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
    * Retrieves a list of external predecessors relationships.
    *
    * @return list of external predecessors
    */
   public List<ExternalPredecessorRelation> getExternalPredecessors()
   {
      return m_externalPredecessors;
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
         properties.setFiscalYearStartMonth(row.getInteger("fy_start_month_num"));
         // cannot assign actual calendar yet as it has not been read yet
         m_defaultCalendarID = row.getInteger("clndr_id");
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
         parseUDF(row);
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

      if (m_defaultCalendarID != null)
      {
         ProjectCalendar defaultCalendar = m_calMap.get(m_defaultCalendarID);
         // Primavera XER files can sometimes not contain a definition of the default
         // project calendar so only try to set if we find a definition.
         if (defaultCalendar != null)
         {
            m_project.setDefaultCalendar(defaultCalendar);
         }
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

      try
      {
         calendar.setMinutesPerDay(Integer.valueOf((int) NumberHelper.getDouble(row.getDouble("day_hr_cnt")) * 60));
         calendar.setMinutesPerWeek(Integer.valueOf((int) (NumberHelper.getDouble(row.getDouble("week_hr_cnt")) * 60)));
         calendar.setMinutesPerMonth(Integer.valueOf((int) (NumberHelper.getDouble(row.getDouble("month_hr_cnt")) * 60)));
         calendar.setMinutesPerYear(Integer.valueOf((int) (NumberHelper.getDouble(row.getDouble("year_hr_cnt")) * 60)));
      }
      catch (ClassCastException ex)
      {
         // We have seen examples of malformed calendar data where fields have been missing
         // from the record. We'll typically get a class cast exception here as we're trying
         // to process something which isn't a double.
         // We'll just return at this point as it's not clear that we can salvage anything
         // sensible from this record.
         return;
      }

      // Process data
      String calendarData = row.getString("clndr_data");
      if (calendarData != null && !calendarData.isEmpty())
      {
         Record root = Record.getRecord(calendarData);
         if (root != null)
         {
            processCalendarDays(calendar, root);
            processCalendarExceptions(calendar, root);
         }
      }
      else
      {
         // if there is not DaysOfWeek data, Primavera seems to default to Mon-Fri, 8:00-16:00
         DateRange defaultHourRange = new DateRange(DateHelper.getTime(8, 0), DateHelper.getTime(16, 0));
         for (Day day : Day.values())
         {
            if (day != Day.SATURDAY && day != Day.SUNDAY)
            {
               calendar.setWorkingDay(day, true);
               ProjectCalendarHours hours = calendar.addCalendarHours(day);
               hours.addRange(defaultHourRange);
            }
            else
            {
               calendar.setWorkingDay(day, false);
            }
         }
      }

      m_eventManager.fireCalendarReadEvent(calendar);
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
                  String startText;
                  String endText;

                  if (wh[0].equals("s"))
                  {
                     startText = wh[1];
                     endText = wh[3];
                  }
                  else
                  {
                     startText = wh[3];
                     endText = wh[1];
                  }

                  // for end time treat midnight as midnight next day
                  if (endText.equals("00:00"))
                  {
                     endText = "24:00";
                  }
                  Date start = m_calendarTimeFormat.parse(startText);
                  Date end = m_calendarTimeFormat.parse(endText);

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
            int daysFrom1970 = daysFrom1900 - 25568;
            // 25568 -> Number of days from 1900 to 1970.
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
      processResources(rows, null);
   }

   /**
    * Process resources.
    *
    * @param rows resource data
    * @param udfVals User Defined Fields values data
    */
   public void processResources(List<Row> rows, List<Row> udfVals)
   {
      for (Row row : rows)
      {
         Resource resource = m_project.addResource();
         processFields(m_resourceFields, row, resource);
         resource.setResourceCalendar(getResourceCalendar(row.getInteger("clndr_id")));

         // Even though we're not filling in a rate, filling in a time unit can still be useful
         // so that we know what rate time unit was originally used in Primavera.
         TimeUnit timeUnit = TIME_UNIT_MAP.get(row.getString("cost_qty_type"));
         resource.setStandardRateUnits(timeUnit);
         resource.setOvertimeRateUnits(timeUnit);

         // Add User Defined Fields
         Integer uniqueID = resource.getUniqueID();
         List<Row> udf = getContainerUDF(uniqueID, udfVals);
         for (Row r : udf)
         {
            addUDFValue(FieldTypeClass.RESOURCE, resource, r);
         }

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
    * Process resource rates.
    *
    * @param rows resource rate data
    */
   public void processResourceRates(List<Row> rows)
   {
      // Primavera defines resource cost tables by start dates so sort and define end by next
      Collections.sort(rows, new Comparator<Row>()
      {
         @Override public int compare(Row r1, Row r2)
         {
            Integer id1 = r1.getInteger("rsrc_id");
            Integer id2 = r2.getInteger("rsrc_id");
            int cmp = NumberHelper.compare(id1, id2);
            if (cmp != 0)
            {
               return cmp;
            }
            Date d1 = r1.getDate("start_date");
            Date d2 = r2.getDate("start_date");
            return DateHelper.compare(d1, d2);
         }
      });

      for (int i = 0; i < rows.size(); ++i)
      {
         Row row = rows.get(i);

         Integer resourceID = row.getInteger("rsrc_id");
         Rate standardRate = new Rate(row.getDouble("cost_per_qty"), TimeUnit.HOURS);
         TimeUnit standardRateFormat = TimeUnit.HOURS;
         Rate overtimeRate = new Rate(0, TimeUnit.HOURS); // does this exist in Primavera?
         TimeUnit overtimeRateFormat = TimeUnit.HOURS;
         Double costPerUse = NumberHelper.getDouble(0.0);
         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getDouble("max_qty_per_hr")) * 100); // adjust to be % as in MS Project
         Date startDate = row.getDate("start_date");
         Date endDate = DateHelper.LAST_DATE;

         if (i + 1 < rows.size())
         {
            Row nextRow = rows.get(i + 1);
            int nextResourceID = nextRow.getInt("rsrc_id");
            if (resourceID.intValue() == nextResourceID)
            {
               endDate = nextRow.getDate("start_date");
            }
         }

         Resource resource = m_project.getResourceByUniqueID(resourceID);
         if (resource != null)
         {
            CostRateTable costRateTable = resource.getCostRateTable(0);
            if (costRateTable == null)
            {
               costRateTable = new CostRateTable();
               resource.setCostRateTable(0, costRateTable);
            }
            CostRateTableEntry entry = new CostRateTableEntry(standardRate, standardRateFormat, overtimeRate, overtimeRateFormat, costPerUse, endDate);
            costRateTable.add(entry);

            AvailabilityTable availabilityTable = resource.getAvailability();
            Availability newAvailability = new Availability(startDate, endDate, maxUnits);
            availabilityTable.add(newAvailability);
         }
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
      processTasks(wbs, tasks, null);
   }

   /**
    * Process tasks.
    *
    * @param wbs WBS task data
    * @param tasks task data
    * @param udfVals User Defined Fields values data
    */
   public void processTasks(List<Row> wbs, List<Row> tasks, List<Row> udfVals)
   {
      Set<Integer> uniqueIDs = new HashSet<Integer>();

      //
      // Read WBS entries and create tasks.
      // Note that the wbs list is supplied to us in the correct order.
      //
      for (Row row : wbs)
      {
         Task task = m_project.addTask();
         task.setProject(m_project.getProjectProperties().getName()); // P6 task always belongs to project
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
               task.set(activityIDField, task.getWBS());
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
         task.setProject(m_project.getProjectProperties().getName()); // P6 task always belongs to project

         processFields(m_taskFields, row, task);

         task.setMilestone(BooleanHelper.getBoolean(MILESTONE_MAP.get(row.getString("task_type"))));

         // Only "Resource Dependent" activities consider resource calendars during scheduling in P6.
         task.setIgnoreResourceCalendar(!"TT_Rsrc".equals(row.getString("task_type")));

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

         Integer calId = row.getInteger("clndr_id");
         ProjectCalendar cal = m_calMap.get(calId);
         task.setCalendar(cal);

         Date startDate = row.getDate("act_start_date") == null ? row.getDate("restart_date") : row.getDate("act_start_date");
         task.setStart(startDate);
         Date endDate = row.getDate("act_end_date") == null ? row.getDate("reend_date") : row.getDate("act_end_date");
         task.setFinish(endDate);

         Duration work = Duration.add(task.getActualWork(), task.getRemainingWork(), m_project.getProjectProperties());
         task.setWork(work);

         // Add User Defined Fields
         List<Row> udf = getContainerUDF(uniqueID, udfVals);
         for (Row r : udf)
         {
            addUDFValue(FieldTypeClass.TASK, task, r);
         }

         m_eventManager.fireTaskReadEvent(task);
      }

      sortActivities(activityIDField, m_project);
      updateStructure();
      updateDates();
      updateWork();
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
    * Configure a new user defined field.
    *
    * @param fieldType field type
    * @param dataType field data type
    * @param name field name
    */
   private void addUserDefinedField(FieldTypeClass fieldType, UserFieldDataType dataType, String name)
   {
      try
      {
         switch (fieldType)
         {
            case TASK:
               TaskField taskField;

               do
               {
                  taskField = m_taskUdfCounters.nextField(TaskField.class, dataType);
               }
               while (m_taskFields.containsKey(taskField) || m_wbsFields.containsKey(taskField));

               m_project.getCustomFields().getCustomField(taskField).setAlias(name);

               break;
            case RESOURCE:
               ResourceField resourceField;

               do
               {
                  resourceField = m_resourceUdfCounters.nextField(ResourceField.class, dataType);
               }
               while (m_resourceFields.containsKey(resourceField));

               m_project.getCustomFields().getCustomField(resourceField).setAlias(name);

               break;
            case ASSIGNMENT:
               AssignmentField assignmentField;

               do
               {
                  assignmentField = m_assignmentUdfCounters.nextField(AssignmentField.class, dataType);
               }
               while (m_assignmentFields.containsKey(assignmentField));

               m_project.getCustomFields().getCustomField(assignmentField).setAlias(name);

               break;
            default:
               break;
         }
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
   private void parseUDF(Row row)
   {
      FieldTypeClass fieldType = FIELD_TYPE_MAP.get(row.getString("table_name"));
      if (fieldType != null)
      {
         Integer fieldId = Integer.valueOf(row.getString("udf_type_id"));
         String fieldDataType = row.getString("logical_data_type");
         String fieldName = row.getString("udf_type_label");

         m_udfMap.put(fieldId, fieldName);
         addUserDefinedField(fieldType, UserFieldDataType.valueOf(fieldDataType), fieldName);
      }
   }

   /**
    * Adds a user defined field value to a task.
    *
    * @param fieldType field type
    * @param container FieldContainer instance
    * @param row UDF data
    */
   private void addUDFValue(FieldTypeClass fieldType, FieldContainer container, Row row)
   {
      Integer fieldId = Integer.valueOf(row.getString("udf_type_id"));
      String fieldName = m_udfMap.get(fieldId);
      Object value = null;

      FieldType field = m_project.getCustomFields().getFieldByAlias(fieldType, fieldName);
      if (field != null)
      {
         DataType fieldDataType = field.getDataType();

         switch (fieldDataType)
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

            case BOOLEAN:
            {
               String text = row.getString("udf_text");
               if (text != null)
               {
                  // before a normal boolean parse, we try to lookup the text as a P6 static type indicator UDF
                  value = STATICTYPE_UDF_MAP.get(text);
                  if (value == null)
                  {
                     value = Boolean.valueOf(row.getBoolean("udf_text"));
                  }
               }
               else
               {
                  value = Boolean.valueOf(row.getBoolean("udf_number"));
               }
               break;
            }

            default:
            {
               value = row.getString("udf_text");
               break;
            }
         }

         container.set(field, value);
      }
   }

   /**
    * Retrieve the user defined values for a given container.
    *
    * @param id target container ID
    * @param udfs user defined fields
    * @return user defined fields for the target container
    */
   private List<Row> getContainerUDF(Integer id, List<Row> udfs)
   {
      List<Row> udf = new LinkedList<Row>();

      if (udfs != null)
      {
         for (Row row : udfs)
         {
            if (id.equals(row.getInteger("fk_id")))
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
      task.setSummary(task.getChildTasks().size() != 0);
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
      if (parentTask.getSummary())
      {
         int finished = 0;
         Date plannedStartDate = parentTask.getStart();
         Date plannedFinishDate = parentTask.getFinish();
         Date actualStartDate = parentTask.getActualStart();
         Date actualFinishDate = parentTask.getActualFinish();
         Date earlyStartDate = parentTask.getEarlyStart();
         Date earlyFinishDate = parentTask.getEarlyFinish();
         Date lateStartDate = parentTask.getLateStart();
         Date lateFinishDate = parentTask.getLateFinish();
         Date baselineStartDate = parentTask.getBaselineStart();
         Date baselineFinishDate = parentTask.getBaselineFinish();

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            // the child tasks can have null dates (e.g. for nested wbs elements with no task children) so we
            // still must protect against some children having null dates

            plannedStartDate = DateHelper.min(plannedStartDate, task.getStart());
            plannedFinishDate = DateHelper.max(plannedFinishDate, task.getFinish());
            actualStartDate = DateHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = DateHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = DateHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = DateHelper.max(earlyFinishDate, task.getEarlyFinish());
            lateStartDate = DateHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = DateHelper.max(lateFinishDate, task.getLateFinish());
            baselineStartDate = DateHelper.min(baselineStartDate, task.getBaselineStart());
            baselineFinishDate = DateHelper.max(baselineFinishDate, task.getBaselineFinish());

            if (task.getActualFinish() != null)
            {
               ++finished;
            }
         }

         parentTask.setStart(plannedStartDate);
         parentTask.setFinish(plannedFinishDate);
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
         }

         Duration baselineDuration = null;
         if (baselineStartDate != null && baselineFinishDate != null)
         {
            baselineDuration = m_project.getDefaultCalendar().getWork(baselineStartDate, baselineFinishDate, TimeUnit.HOURS);
            parentTask.setBaselineDuration(baselineDuration);
         }

         Duration remainingDuration = null;
         if (parentTask.getActualFinish() == null)
         {
            Date startDate = parentTask.getEarlyStart();
            if (startDate == null)
            {
               startDate = baselineStartDate;
            }

            Date finishDate = parentTask.getEarlyFinish();
            if (finishDate == null)
            {
               finishDate = baselineFinishDate;
            }

            if (startDate != null && finishDate != null)
            {
               remainingDuration = m_project.getDefaultCalendar().getWork(startDate, finishDate, TimeUnit.HOURS);
            }
         }
         else
         {
            remainingDuration = Duration.getInstance(0, TimeUnit.HOURS);
         }
         parentTask.setRemainingDuration(remainingDuration);

         if (baselineDuration != null && baselineDuration.getDuration() != 0 && remainingDuration != null)
         {
            double durationPercentComplete = ((baselineDuration.getDuration() - remainingDuration.getDuration()) / baselineDuration.getDuration()) * 100.0;
            parentTask.setPercentageComplete(Double.valueOf(durationPercentComplete));
         }
      }
   }

   /**
    * The Primavera WBS entries we read in as tasks don't have work entered. We try
    * to compensate for this by summing the child tasks' work. This method recursively
    * descends through the tasks to do this.
    */
   private void updateWork()
   {
      for (Task task : m_project.getChildTasks())
      {
         updateWork(task);
      }
   }

   /**
    * See the notes above.
    *
    * @param parentTask parent task.
    */
   private void updateWork(Task parentTask)
   {
      if (parentTask.getSummary())
      {
         ProjectProperties properties = m_project.getProjectProperties();

         Duration actualWork = null;
         Duration baselineWork = null;
         Duration remainingWork = null;
         Duration work = null;

         for (Task task : parentTask.getChildTasks())
         {
            updateWork(task);

            actualWork = Duration.add(actualWork, task.getActualWork(), properties);
            baselineWork = Duration.add(baselineWork, task.getBaselineWork(), properties);
            remainingWork = Duration.add(remainingWork, task.getRemainingWork(), properties);
            work = Duration.add(work, task.getWork(), properties);
         }

         parentTask.setActualWork(actualWork);
         parentTask.setBaselineWork(baselineWork);
         parentTask.setRemainingWork(remainingWork);
         parentTask.setWork(work);
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
         Integer currentID = mapTaskID(row.getInteger("task_id"));
         Integer predecessorID = mapTaskID(row.getInteger("pred_task_id"));
         Task currentTask = m_project.getTaskByUniqueID(currentID);
         Task predecessorTask = m_project.getTaskByUniqueID(predecessorID);
         RelationType type = RELATION_TYPE_MAP.get(row.getString("pred_type"));
         Duration lag = row.getDuration("lag_hr_cnt");
         if (currentTask != null)
         {
            if (predecessorTask != null)
            {
               Relation relation = currentTask.addPredecessor(predecessorTask, type, lag);
               m_eventManager.fireRelationReadEvent(relation);
            }
            else
            {
               // if we can't find the predecessor, it must lie outside the project
               m_externalPredecessors.add(new ExternalPredecessorRelation(predecessorID, currentTask, type, lag));
            }
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
      processAssignments(rows, null);
   }

   /**
    * Process assignment data.
    *
    * @param rows assignment data
    * @param udfVals User Defined Fields values data
    */
   public void processAssignments(List<Row> rows, List<Row> udfVals)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(mapTaskID(row.getInteger("task_id")));
         Resource resource = m_project.getResourceByUniqueID(row.getInteger("rsrc_id"));
         if (task != null && resource != null)
         {
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            processFields(m_assignmentFields, row, assignment);

            populateField(assignment, AssignmentField.START, AssignmentField.BASELINE_START, AssignmentField.ACTUAL_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.BASELINE_FINISH, AssignmentField.ACTUAL_FINISH);

            // include actual overtime work in work calculations
            Duration remainingWork = row.getDuration("remain_qty");
            Duration actualOvertimeWork = row.getDuration("act_ot_qty");
            Duration actualRegularWork = row.getDuration("act_reg_qty");
            Duration actualWork = Duration.add(actualOvertimeWork, actualRegularWork, m_project.getProjectProperties());
            Duration totalWork = Duration.add(actualWork, remainingWork, m_project.getProjectProperties());
            assignment.setActualWork(actualWork);
            assignment.setWork(totalWork);

            // include actual overtime cost in cost calculations
            Double remainingCost = row.getDouble("remain_cost");
            Double actualOvertimeCost = row.getDouble("act_ot_cost");
            Double actualRegularCost = row.getDouble("act_reg_cost");
            double actualCost = NumberHelper.getDouble(actualOvertimeCost) + NumberHelper.getDouble(actualRegularCost);
            double totalCost = actualCost + NumberHelper.getDouble(remainingCost);
            assignment.setActualCost(NumberHelper.getDouble(actualCost));
            assignment.setCost(NumberHelper.getDouble(totalCost));

            double units;
            if (resource.getType() == ResourceType.MATERIAL)
            {
               units = (totalWork == null) ? 0 : totalWork.getDuration() * 100;
            }
            else // RT_Labor & RT_Equip
            {
               units = NumberHelper.getDouble(row.getDouble("target_qty_per_hr")) * 100;
            }
            assignment.setUnits(NumberHelper.getDouble(units));

            // Add User Defined Fields
            Integer uniqueID = assignment.getUniqueID();
            List<Row> udf = getContainerUDF(uniqueID, udfVals);
            for (Row r : udf)
            {
               addUDFValue(FieldTypeClass.ASSIGNMENT, assignment, r);
            }

            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }

      updateTaskCosts();
   }

   /**
    * Sets task cost fields by summing the resource assignment costs. The "projcost" table isn't
    * necessarily available in XER files so we do this instead to back into task costs. Costs for
    * the summary tasks constructed from Primavera WBS entries are calculated by recursively
    * summing child costs.
    */
   private void updateTaskCosts()
   {
      for (Task task : m_project.getChildTasks())
      {
         updateTaskCosts(task);
      }
   }

   /**
    * See the notes above.
    *
    * @param parentTask parent task
    */
   private void updateTaskCosts(Task parentTask)
   {
      double baselineCost = 0;
      double actualCost = 0;
      double remainingCost = 0;
      double cost = 0;

      //process children first before adding their costs
      for (Task child : parentTask.getChildTasks())
      {
         updateTaskCosts(child);
         baselineCost += NumberHelper.getDouble(child.getBaselineCost());
         actualCost += NumberHelper.getDouble(child.getActualCost());
         remainingCost += NumberHelper.getDouble(child.getRemainingCost());
         cost += NumberHelper.getDouble(child.getCost());
      }

      List<ResourceAssignment> resourceAssignments = parentTask.getResourceAssignments();
      for (ResourceAssignment assignment : resourceAssignments)
      {
         baselineCost += NumberHelper.getDouble(assignment.getBaselineCost());
         actualCost += NumberHelper.getDouble(assignment.getActualCost());
         remainingCost += NumberHelper.getDouble(assignment.getRemainingCost());
         cost += NumberHelper.getDouble(assignment.getCost());
      }

      parentTask.setBaselineCost(NumberHelper.getDouble(baselineCost));
      parentTask.setActualCost(NumberHelper.getDouble(actualCost));
      parentTask.setRemainingCost(NumberHelper.getDouble(remainingCost));
      parentTask.setCost(NumberHelper.getDouble(cost));
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
      map.put(TaskField.DEADLINE, "anticip_end_date");
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
      map.put(TaskField.ACTUAL_DURATION, "act_drtn_hr_cnt");
      map.put(TaskField.REMAINING_DURATION, "remain_drtn_hr_cnt");
      map.put(TaskField.ACTUAL_WORK, "act_work_qty");
      map.put(TaskField.REMAINING_WORK, "remain_work_qty");
      map.put(TaskField.BASELINE_WORK, "target_work_qty");
      map.put(TaskField.BASELINE_DURATION, "target_drtn_hr_cnt");
      map.put(TaskField.DURATION, "target_drtn_hr_cnt");
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
      map.put(AssignmentField.ACTUAL_OVERTIME_WORK, "act_ot_qty");
      map.put(AssignmentField.BASELINE_COST, "target_cost");
      map.put(AssignmentField.ACTUAL_OVERTIME_COST, "act_ot_cost");
      map.put(AssignmentField.REMAINING_COST, "remain_cost");
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
   private Integer m_defaultCalendarID;
   private Map<Integer, String> m_udfMap = new HashMap<Integer, String>();
   private final UserFieldCounters m_taskUdfCounters;
   private final UserFieldCounters m_resourceUdfCounters;
   private final UserFieldCounters m_assignmentUdfCounters;
   private Map<FieldType, String> m_resourceFields;
   private Map<FieldType, String> m_wbsFields;
   private Map<FieldType, String> m_taskFields;
   private Map<FieldType, String> m_assignmentFields;
   private List<ExternalPredecessorRelation> m_externalPredecessors = new ArrayList<ExternalPredecessorRelation>();
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

   private static final Map<String, TimeUnit> TIME_UNIT_MAP = new HashMap<String, TimeUnit>();
   static
   {
      TIME_UNIT_MAP.put("QT_Minute", TimeUnit.MINUTES);
      TIME_UNIT_MAP.put("QT_Hour", TimeUnit.HOURS);
      TIME_UNIT_MAP.put("QT_Day", TimeUnit.DAYS);
      TIME_UNIT_MAP.put("QT_Week", TimeUnit.WEEKS);
      TIME_UNIT_MAP.put("QT_Month", TimeUnit.MONTHS);
      TIME_UNIT_MAP.put("QT_Year", TimeUnit.YEARS);
   }

   private static final Map<String, CurrencySymbolPosition> CURRENCY_SYMBOL_POSITION_MAP = new HashMap<String, CurrencySymbolPosition>();
   static
   {
      CURRENCY_SYMBOL_POSITION_MAP.put("#1.1", CurrencySymbolPosition.BEFORE);
      CURRENCY_SYMBOL_POSITION_MAP.put("1.1#", CurrencySymbolPosition.AFTER);
      CURRENCY_SYMBOL_POSITION_MAP.put("# 1.1", CurrencySymbolPosition.BEFORE_WITH_SPACE);
      CURRENCY_SYMBOL_POSITION_MAP.put("1.1 #", CurrencySymbolPosition.AFTER_WITH_SPACE);
   }

   private static final Map<String, Boolean> STATICTYPE_UDF_MAP = new HashMap<String, Boolean>();
   static
   {
      // this is a judgement call on how the static type indicator values would be best translated to a flag
      STATICTYPE_UDF_MAP.put("UDF_G0", Boolean.FALSE); // no indicator
      STATICTYPE_UDF_MAP.put("UDF_G1", Boolean.FALSE); // red x
      STATICTYPE_UDF_MAP.put("UDF_G2", Boolean.FALSE); // yellow !
      STATICTYPE_UDF_MAP.put("UDF_G3", Boolean.TRUE); // green check
      STATICTYPE_UDF_MAP.put("UDF_G4", Boolean.TRUE); // blue star
   }

   private static final Map<String, FieldTypeClass> FIELD_TYPE_MAP = new HashMap<String, FieldTypeClass>();
   static
   {
      FIELD_TYPE_MAP.put("TASK", FieldTypeClass.TASK);
      FIELD_TYPE_MAP.put("RSRC", FieldTypeClass.RESOURCE);
      FIELD_TYPE_MAP.put("TASKRSRC", FieldTypeClass.ASSIGNMENT);
   }
}