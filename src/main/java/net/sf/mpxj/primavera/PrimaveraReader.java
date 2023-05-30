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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeContainer;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.Availability;
import net.sf.mpxj.CostAccount;
import net.sf.mpxj.CostAccountContainer;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.CriticalActivityType;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DataType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.ExpenseCategory;
import net.sf.mpxj.ExpenseCategoryContainer;
import net.sf.mpxj.ExpenseItem;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.HtmlNotes;
import net.sf.mpxj.Location;
import net.sf.mpxj.LocationContainer;
import net.sf.mpxj.Notes;
import net.sf.mpxj.NotesTopic;
import net.sf.mpxj.NotesTopicContainer;
import net.sf.mpxj.ParentNotes;
import net.sf.mpxj.PercentCompleteType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
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
import net.sf.mpxj.Step;
import net.sf.mpxj.StructuredNotes;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeRange;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.UserDefinedField;
import net.sf.mpxj.UserDefinedFieldContainer;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.ColorHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.SlackHelper;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
final class PrimaveraReader
{
   /**
    * Constructor.
    *
    * @param resourceFields resource field mapping
    * @param wbsFields wbs field mapping
    * @param taskFields task field mapping
    * @param assignmentFields assignment field mapping
    * @param roleFields role field mapping
    * @param matchPrimaveraWBS determine WBS behaviour
    * @param wbsIsFullPath determine the WBS attribute structure
    */
   public PrimaveraReader(Map<FieldType, String> resourceFields, Map<FieldType, String> roleFields, Map<FieldType, String> wbsFields, Map<FieldType, String> taskFields, Map<FieldType, String> assignmentFields, boolean matchPrimaveraWBS, boolean wbsIsFullPath)
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoAssignmentUniqueID(false);
      config.setAutoWBS(false);
      config.setAutoRelationUniqueID(false);
      config.setBaselineStrategy(PrimaveraBaselineStrategy.PLANNED_DATES);

      m_resourceFields = resourceFields;
      m_roleFields = roleFields;
      m_wbsFields = wbsFields;
      m_taskFields = taskFields;
      m_assignmentFields = assignmentFields;

      m_matchPrimaveraWBS = matchPrimaveraWBS;
      m_wbsIsFullPath = wbsIsFullPath;

      m_relationObjectID = new ObjectSequence(1);
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
   public List<ExternalRelation> getExternalRelations()
   {
      return m_externalRelations;
   }

   /**
    * Process project properties.
    *
    * @param projectID project ID
    * @param rows project properties data.
    */
   public void processProjectProperties(Integer projectID, List<Row> rows)
   {
      ProjectProperties properties = m_project.getProjectProperties();
      properties.setUniqueID(projectID);
      populateUserDefinedFieldValues("PROJECT", FieldTypeClass.PROJECT, properties, projectID);

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
         properties.setFiscalYearStartMonth(row.getInteger("fy_start_month_num"));
         properties.setExportFlag(row.getBoolean("export_flag"));
         properties.setPlannedStart(row.getDate("plan_start_date"));
         properties.setScheduledFinish(row.getDate("scd_end_date"));
         properties.setMustFinishBy(row.getDate("plan_end_date"));
         properties.setCriticalSlackLimit(Duration.getInstance(row.getInt("critical_drtn_hr_cnt"), TimeUnit.HOURS));
         properties.setLocationUniqueID(row.getInteger("location_id"));

         // cannot assign actual calendar yet as it has not been read yet
         m_defaultCalendarID = row.getInteger("clndr_id");
      }
   }

   /**
    * Process locations.
    *
    * @param locations locations data
    */
   public void processLocations(List<Row> locations)
   {
      LocationContainer container = m_project.getLocations();
      locations.forEach(
         row -> container.add(
            new Location.Builder()
               .uniqueID(row.getInteger("location_id"))
               .name(row.getString("location_name"))
               .addressLine1(row.getString("address_line1"))
               .addressLine2(row.getString("address_line2"))
               .addressLine3(row.getString("address_line3"))
               .city(row.getString("city_name"))
               .municipality(row.getString("municipality_name"))
               .state(row.getString("state_name"))
               .stateCode(row.getString("state_code"))
               .country(row.getString("country_name"))
               .countryCode(row.getString("country_code"))
               .postalCode(row.getString("postal_code"))
               .latitude(row.getDouble("latitude"))
               .longitude(row.getDouble("longitude"))
               .build()
         )
      );
   }

   /**
    * Process expense categories.
    *
    * @param categories expense categories
    */
   public void processExpenseCategories(List<Row> categories)
   {
      ExpenseCategoryContainer container = m_project.getExpenseCategories();
      categories.forEach(row -> container.add(new ExpenseCategory(row.getInteger("cost_type_id"), row.getString("cost_type"), row.getInteger("seq_num"))));
   }

   /**
    * Process cost accounts.
    *
    * @param accounts cost accounts
    */
   public void processCostAccounts(List<Row> accounts)
   {
      CostAccountContainer container = m_project.getCostAccounts();
      accounts.forEach(row -> container.add(new CostAccount(row.getInteger("acct_id"), row.getString("acct_short_name"), row.getString("acct_name"), row.getString("acct_descr"), row.getInteger("acct_seq_num"))));
      accounts.forEach(row -> container.getByUniqueID(row.getInteger("acct_id")).setParent(container.getByUniqueID(row.getInteger("parent_acct_id"))));
   }

   /**
    * Read activity code types and values.
    *
    * @param types activity code type data
    * @param typeValues activity code value data
    * @param assignments activity code task assignments
    */
   public void processActivityCodes(List<Row> types, List<Row> typeValues, List<Row> assignments)
   {
      ActivityCodeContainer container = m_project.getActivityCodes();
      Map<Integer, ActivityCode> map = new HashMap<>();

      for (Row row : types)
      {
         ActivityCode code = new ActivityCode(m_project, row.getInteger("actv_code_type_id"), ActivityCodeScopeHelper.getInstanceFromXer(row.getString("actv_code_type_scope")), row.getInteger("wbs_id"), row.getInteger("proj_id"), row.getInteger("seq_num"), row.getString("actv_code_type"), row.getBoolean("super_flag"), row.getInteger("actv_short_len"));
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      for (Row row : typeValues)
      {
         ActivityCode code = map.get(row.getInteger("actv_code_type_id"));
         if (code != null)
         {
            ActivityCodeValue value = code.addValue(row.getInteger("actv_code_id"), row.getInteger("seq_num"), row.getString("short_name"), row.getString("actv_code_name"), ColorHelper.parseHexColor(row.getString("color")));
            m_activityCodeMap.put(value.getUniqueID(), value);
         }
      }

      for (Row row : typeValues)
      {
         ActivityCodeValue child = m_activityCodeMap.get(row.getInteger("actv_code_id"));
         ActivityCodeValue parent = m_activityCodeMap.get(row.getInteger("parent_actv_code_id"));
         if (parent != null && child != null)
         {
            child.setParent(parent);
         }
      }

      for (Row row : assignments)
      {
         Integer taskID = row.getInteger("task_id");
         List<Integer> list = m_activityCodeAssignments.computeIfAbsent(taskID, k -> new ArrayList<>());
         list.add(row.getInteger("actv_code_id"));
      }
   }

   /**
    * Process User Defined Fields (UDF).
    *
    * @param fields field definitions
    * @param values field values
    */
   public void processUserDefinedFields(List<Row> fields, List<Row> values)
   {
      // Process fields
      Map<Integer, String> tableNameMap = new HashMap<>();
      UserDefinedFieldContainer container = m_project.getUserDefinedFields();

      for (Row row : fields)
      {
         Integer fieldId = row.getInteger("udf_type_id");
         String tableName = row.getString("table_name");
         tableNameMap.put(fieldId, tableName);

         FieldTypeClass fieldTypeClass = FieldTypeClassHelper.getInstanceFromXer(tableName);
         if (fieldTypeClass == null)
         {
            continue;
         }

         boolean summaryTaskOnly = tableName.equals("PROJWBS");
         String internalName = row.getString("udf_type_name");
         String externalName = row.getString("udf_type_label");
         DataType dataType = UdfHelper.getDataTypeFromXer(row.getString("logical_data_type"));
         UserDefinedField fieldType = new UserDefinedField(fieldId, internalName, externalName, fieldTypeClass, summaryTaskOnly, dataType);
         container.add(fieldType);

         m_udfFields.put(fieldId, fieldType);
         m_project.getCustomFields().add(fieldType).setAlias(externalName).setUniqueID(fieldId);
      }

      // Process values
      for (Row row : values)
      {
         Integer typeID = row.getInteger("udf_type_id");
         String tableName = tableNameMap.get(typeID);
         Map<Integer, List<Row>> tableData = m_udfValues.computeIfAbsent(tableName, k -> new HashMap<>());

         Integer id = row.getInteger("fk_id");
         List<Row> list = tableData.computeIfAbsent(id, k -> new ArrayList<>());
         list.add(row);
      }
   }

   /**
    * Process project calendars.
    *
    * @param rows project calendar data
    */
   public void processCalendars(List<Row> rows)
   {
      //
      // First pass: read calendar definitions
      //
      Map<ProjectCalendar, Integer> baseCalendarMap = new HashMap<>();
      for (Row row : rows)
      {
         ProjectCalendar calendar = processCalendar(row);
         Integer baseCalendarID = row.getInteger("base_clndr_id");
         if (baseCalendarID != null)
         {
            baseCalendarMap.put(calendar, baseCalendarID);
         }
      }

      //
      // Second pass: create calendar hierarchy
      //
      for (Map.Entry<ProjectCalendar, Integer> entry : baseCalendarMap.entrySet())
      {
         ProjectCalendar baseCalendar = m_project.getCalendarByUniqueID(entry.getValue());
         if (baseCalendar != null)
         {
            entry.getKey().setParent(baseCalendar);
         }
      }

      //
      // We've used Primavera's unique ID values for the calendars we've read so far.
      // At this point any new calendars we create must be auto number. We also need to
      // ensure that the auto numbering starts from an appropriate value.
      //
      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoCalendarUniqueID(true);
      config.updateCalendarUniqueCounter();

      ProjectCalendar defaultCalendar = m_project.getCalendarByUniqueID(m_defaultCalendarID);
      if (defaultCalendar == null)
      {
         defaultCalendar = m_project.getCalendars().findOrCreateDefaultCalendar();
      }
      m_project.setDefaultCalendar(defaultCalendar);
   }

   /**
    * Process data for an individual calendar.
    *
    * @param row calendar data
    * @return ProjectCalendar instance
    */
   public ProjectCalendar processCalendar(Row row)
   {
      ProjectCalendar calendar = m_project.addCalendar();

      Integer id = row.getInteger("clndr_id");
      calendar.setUniqueID(id);
      calendar.setName(row.getString("clndr_name"));
      calendar.setType(CalendarTypeHelper.getInstanceFromXer(row.getString("clndr_type")));
      calendar.setPersonal(row.getBoolean("rsrc_private"));

      if (row.getBoolean("default_flag") && m_defaultCalendarID == null)
      {
         // We don't have a default calendar set for the project, use the global default
         m_defaultCalendarID = id;
      }

      // Process data
      String calendarData = row.getString("clndr_data");
      if (calendarData != null && !calendarData.isEmpty())
      {
         StructuredTextParser parser = new StructuredTextParser();
         parser.setRaiseExceptionOnParseError(false);
         StructuredTextRecord root = parser.parse(calendarData);
         StructuredTextRecord daysOfWeek = root.getChild("DaysOfWeek");
         StructuredTextRecord exceptions = root.getChild("Exceptions");

         if (daysOfWeek != null)
         {
            processCalendarDays(calendar, daysOfWeek);
         }

         if (exceptions != null)
         {
            processCalendarExceptions(calendar, exceptions);
         }
      }

      ProjectCalendarHelper.ensureWorkingTime(calendar);

      //
      // Try and extract minutes per period from the calendar row
      //
      Double rowHoursPerDay = getHoursPerPeriod(row, "day_hr_cnt");
      Double rowHoursPerWeek = getHoursPerPeriod(row, "week_hr_cnt");
      Double rowHoursPerMonth = getHoursPerPeriod(row, "month_hr_cnt");
      Double rowHoursPerYear = getHoursPerPeriod(row, "year_hr_cnt");

      calendar.setCalendarMinutesPerDay(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerDay) * 60)));
      calendar.setCalendarMinutesPerWeek(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerWeek) * 60)));
      calendar.setCalendarMinutesPerMonth(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerMonth) * 60)));
      calendar.setCalendarMinutesPerYear(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerYear) * 60)));

      //
      // If we're missing any of these figures, generate them.
      // Note that P6 allows users to enter arbitrary hours per period,
      // as far as I can see they aren't validated to see if they make sense,
      // so the figures here won't necessarily match what you'd see in P6.
      //
      if (rowHoursPerDay == null || rowHoursPerWeek == null || rowHoursPerMonth == null || rowHoursPerYear == null)
      {
         int minutesPerWeek = 0;
         int workingDays = 0;

         for (Day day : Day.values())
         {
            ProjectCalendarHours hours = calendar.getCalendarHours(day);
            if (hours == null)
            {
               continue;
            }

            if (hours.size() > 0)
            {
               ++workingDays;
               for (TimeRange range : hours)
               {
                  long milliseconds = range.getEnd().getTime() - range.getStart().getTime();
                  minutesPerWeek += (milliseconds / (1000 * 60));
               }
            }
         }

         int minutesPerDay = minutesPerWeek / workingDays;
         int minutesPerMonth = minutesPerWeek * 4;
         int minutesPerYear = minutesPerMonth * 12;

         if (rowHoursPerDay == null)
         {
            calendar.setCalendarMinutesPerDay(Integer.valueOf(minutesPerDay));
         }

         if (rowHoursPerWeek == null)
         {
            calendar.setCalendarMinutesPerWeek(Integer.valueOf(minutesPerWeek));
         }

         if (rowHoursPerMonth == null)
         {
            calendar.setCalendarMinutesPerMonth(Integer.valueOf(minutesPerMonth));
         }

         if (rowHoursPerYear == null)
         {
            calendar.setCalendarMinutesPerYear(Integer.valueOf(minutesPerYear));
         }
      }

      m_eventManager.fireCalendarReadEvent(calendar);

      return calendar;
   }

   private Double getHoursPerPeriod(Row row, String name)
   {
      try
      {
         return row.getDouble(name);
      }

      catch (ClassCastException ex)
      {
         // We have seen examples of malformed calendar data where fields have been missing
         // from the record. We'll typically get a class cast exception here as we're trying
         // to process something which isn't a double.
         return null;
      }
   }

   /**
    * Process calendar days of the week.
    *
    * @param calendar project calendar
    * @param daysOfWeek calendar data
    */
   private void processCalendarDays(ProjectCalendar calendar, StructuredTextRecord daysOfWeek)
   {
      Map<Day, StructuredTextRecord> days = daysOfWeek.getChildren().stream().filter(d -> Day.getInstance(Integer.parseInt(d.getRecordName())) != null).collect(Collectors.toMap(d -> Day.getInstance(Integer.parseInt(d.getRecordName())), d -> d));

      for (Day day : Day.values())
      {
         StructuredTextRecord dayRecord = days.get(day);
         processCalendarHours(day, calendar, dayRecord == null ? StructuredTextRecord.EMPTY : dayRecord);
      }
   }

   /**
    * Process hours in a working day.
    *
    * @param day day to process
    * @param calendar project calendar
    * @param dayRecord working day data
    */
   private void processCalendarHours(Day day, ProjectCalendar calendar, StructuredTextRecord dayRecord)
   {
      // Get hours
      ProjectCalendarHours hours = calendar.addCalendarHours(day);
      List<StructuredTextRecord> recHours = dayRecord.getChildren();
      if (recHours.size() == 0)
      {
         // No data -> not working
         calendar.setWorkingDay(day, false);
      }
      else
      {
         calendar.setWorkingDay(day, true);
         // Read hours
         for (StructuredTextRecord recWorkingHours : recHours)
         {
            addHours(hours, recWorkingHours);
         }
      }
   }

   /**
    * Parses a record containing hours and add them to a container.
    *
    * @param ranges hours container
    * @param hoursRecord hours record
    */
   private void addHours(ProjectCalendarHours ranges, StructuredTextRecord hoursRecord)
   {
      String startText = hoursRecord.getAttribute("s");
      String endText = hoursRecord.getAttribute("f");

      // Ignore incomplete records
      if (startText == null || endText == null || startText.isEmpty() || endText.isEmpty())
      {
         return;
      }

      // for end time treat midnight as midnight next day
      if (endText.equals("00:00"))
      {
         endText = "24:00";
      }

      try
      {
         Date start = m_calendarTimeFormat.parse(startText);
         Date end = m_calendarTimeFormat.parse(endText);
         ranges.add(new TimeRange(start, end));
      }

      catch (ParseException e)
      {
         // silently ignore date parse exceptions
      }
   }

   /**
    * Process calendar exceptions.
    *
    * @param calendar project calendar
    * @param exceptions calendar data
    */
   private void processCalendarExceptions(ProjectCalendar calendar, StructuredTextRecord exceptions)
   {
      for (StructuredTextRecord exception : exceptions.getChildren())
      {
         long daysFromEpoch = Integer.parseInt(exception.getAttribute("d"));
         Date startEx = DateHelper.getDateFromLong(EXCEPTION_EPOCH + (daysFromEpoch * DateHelper.MS_PER_DAY));

         ProjectCalendarException pce = calendar.addCalendarException(startEx, startEx);
         for (StructuredTextRecord exceptionHours : exception.getChildren())
         {
            addHours(pce, exceptionHours);
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
         resource.setCalendar(m_project.getCalendars().getByUniqueID(row.getInteger("clndr_id")));

         // Add User Defined Fields
         populateUserDefinedFieldValues("RSRC", FieldTypeClass.RESOURCE, resource, resource.getUniqueID());

         resource.setNotesObject(getNotes(resource.getNotes()));

         Number defaultUnitsPerTime = row.getDouble("def_qty_per_hr");
         resource.setMaxUnits(defaultUnitsPerTime == null ? null : NumberHelper.getDouble(defaultUnitsPerTime.doubleValue() * 100));

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Process roles.
    *
    * @param rows resource data
    */
   public void processRoles(List<Row> rows)
   {
      for (Row row : rows)
      {
         Resource resource = m_project.addResource();
         processFields(m_roleFields, row, resource);
         resource.setRole(Boolean.TRUE);
         resource.setUniqueID(m_roleClashMap.addID(resource.getUniqueID()));
         resource.setNotesObject(getNotes(resource.getNotes()));
      }
   }

   private Notes getNotes(String text)
   {
      Notes notes = getHtmlNote(text);
      return notes == null || notes.isEmpty() ? null : notes;
   }

   /**
    * Process resource rates.
    *
    * @param rows resource rate data
    */
   public void processResourceRates(List<Row> rows)
   {
      // Primavera defines resource cost tables by start dates so sort and define end by next
      rows.sort((r1, r2) -> {
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
      });

      Resource resource = null;

      for (int i = 0; i < rows.size(); ++i)
      {
         Row row = rows.get(i);

         Integer resourceID = row.getInteger("rsrc_id");
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_project.getResourceByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            readRate(row.getDouble("cost_per_qty")),
            readRate(row.getDouble("cost_per_qty2")),
            readRate(row.getDouble("cost_per_qty3")),
            readRate(row.getDouble("cost_per_qty4")),
            readRate(row.getDouble("cost_per_qty5")),
         };

         Double costPerUse = NumberHelper.getDouble(0.0);
         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getDouble("max_qty_per_hr")) * 100); // adjust to be % as in MS Project
         Date startDate = row.getDate("start_date");
         Date endDate = DateHelper.END_DATE_NA;

         if (i + 1 < rows.size())
         {
            Row nextRow = rows.get(i + 1);
            int nextResourceID = nextRow.getInt("rsrc_id");
            if (resourceID.intValue() == nextResourceID)
            {
               Calendar cal = DateHelper.popCalendar(nextRow.getDate("start_date"));
               cal.add(Calendar.MINUTE, -1);
               endDate = cal.getTime();
               DateHelper.pushCalendar(cal);
            }
         }

         if (startDate == null || startDate.getTime() < DateHelper.START_DATE_NA.getTime())
         {
            startDate = DateHelper.START_DATE_NA;
         }

         if (endDate == null || endDate.getTime() > DateHelper.END_DATE_NA.getTime())
         {
            endDate = DateHelper.END_DATE_NA;
         }

         resource.getCostRateTable(0).add(new CostRateTableEntry(startDate, endDate, costPerUse, values));
         resource.getAvailability().add(new Availability(startDate, endDate, maxUnits));
      }
   }

   /**
    * Read a rate value, handle null.
    *
    * @param value rate as a double
    * @return new Rate instance
    */
   private Rate readRate(Double value)
   {
      if (value == null)
      {
         return null;
      }

      return new Rate(value, TimeUnit.HOURS);
   }

   /**
    * Process role rates.
    *
    * @param rows role rate data
    */
   public void processRoleRates(List<Row> rows)
   {
      // Primavera defines resource cost tables by start dates so sort and define end by next
      rows.sort((r1, r2) -> {
         Integer id1 = r1.getInteger("role_id");
         Integer id2 = r2.getInteger("role_id");
         int cmp = NumberHelper.compare(id1, id2);
         if (cmp != 0)
         {
            return cmp;
         }
         Date d1 = r1.getDate("start_date");
         Date d2 = r2.getDate("start_date");
         return DateHelper.compare(d1, d2);
      });

      Resource resource = null;

      for (int i = 0; i < rows.size(); ++i)
      {
         Row row = rows.get(i);

         Integer resourceID = m_roleClashMap.getID(row.getInteger("role_id"));
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_project.getResourceByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            readRate(row.getDouble("cost_per_qty")),
            readRate(row.getDouble("cost_per_qty2")),
            readRate(row.getDouble("cost_per_qty3")),
            readRate(row.getDouble("cost_per_qty4")),
            readRate(row.getDouble("cost_per_qty5")),
         };

         Double costPerUse = NumberHelper.getDouble(0.0);
         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getDouble("max_qty_per_hr")) * 100); // adjust to be % as in MS Project
         Date startDate = row.getDate("start_date");
         Date endDate = DateHelper.END_DATE_NA;

         if (i + 1 < rows.size())
         {
            Row nextRow = rows.get(i + 1);
            if (NumberHelper.equals(row.getInteger("role_id"), nextRow.getInteger("role_id")))
            {
               Calendar cal = DateHelper.popCalendar(nextRow.getDate("start_date"));
               cal.add(Calendar.MINUTE, -1);
               endDate = cal.getTime();
               DateHelper.pushCalendar(cal);
            }
         }

         if (startDate == null || startDate.getTime() < DateHelper.START_DATE_NA.getTime())
         {
            startDate = DateHelper.START_DATE_NA;
         }

         if (endDate == null || endDate.getTime() > DateHelper.END_DATE_NA.getTime())
         {
            endDate = DateHelper.END_DATE_NA;
         }

         resource.getCostRateTable(0).add(new CostRateTableEntry(startDate, endDate, costPerUse, values));
         resource.getAvailability().add(new Availability(startDate, endDate, maxUnits));
      }
   }

   /**
    * Process tasks.
    *
    * @param wbs WBS task data
    * @param tasks task data
    * @param wbsNotes WBS note data
    * @param taskNotes task note data
    */
   public void processTasks(List<Row> wbs, List<Row> tasks, Map<Integer, Notes> wbsNotes, Map<Integer, Notes> taskNotes)
   {
      ProjectProperties projectProperties = m_project.getProjectProperties();
      String projectName = projectProperties.getName();
      Set<Task> wbsTasks = new HashSet<>();
      boolean baselineFromCurrentProject = m_project.getProjectProperties().getBaselineProjectUniqueID() == null;

      //
      // We set the project name when we read the project properties, but that's just
      // the short name. The full project name lives on the first WBS item. Rather than
      // querying twice, we'll just set it here where we have access to the WBS items.
      // We'll leave the short name in place if there is no WBS.
      //
      if (!wbs.isEmpty())
      {
         projectProperties.setName(wbs.get(0).getString("wbs_name"));
      }

      //
      // Read WBS entries and create tasks.
      // Note that the wbs list is supplied to us in the correct order.
      //
      for (Row row : wbs)
      {
         Task task = m_project.addTask();
         task.setProject(projectName); // P6 task always belongs to project
         task.setSummary(true);
         processFields(m_wbsFields, row, task);
         populateUserDefinedFieldValues("PROJWBS", FieldTypeClass.TASK, task, task.getUniqueID());
         task.setNotesObject(wbsNotes.get(task.getUniqueID()));
         m_activityClashMap.addID(task.getUniqueID());
         wbsTasks.add(task);
         m_eventManager.fireTaskReadEvent(task);
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

      if (m_wbsIsFullPath)
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

         processFields(m_taskFields, row, task);

         task.setMilestone(BooleanHelper.getBoolean(MILESTONE_MAP.get(row.getString("task_type"))));
         task.setActivityStatus(ActivityStatusHelper.getInstanceFromXer(row.getString("status_code")));
         task.setActivityType(ActivityTypeHelper.getInstanceFromXer(row.getString("task_type")));

         // Only "Resource Dependent" activities consider resource calendars during scheduling in P6.
         task.setIgnoreResourceCalendar(!"TT_Rsrc".equals(row.getString("task_type")));

         task.setPercentCompleteType(PercentCompleteTypeHelper.getInstanceFromXer(row.getString("complete_pct_type")));
         task.setPercentageWorkComplete(calculateUnitsPercentComplete(row));
         task.setPercentageComplete(calculateDurationPercentComplete(row));
         task.setPhysicalPercentComplete(calculatePhysicalPercentComplete(row));

         if (m_matchPrimaveraWBS && parentTask != null)
         {
            task.setWBS(parentTask.getWBS());
         }

         Integer uniqueID = task.getUniqueID();

         // Add User Defined Fields - before we handle ID clashes
         populateUserDefinedFieldValues("TASK", FieldTypeClass.TASK, task, uniqueID);

         populateActivityCodes(task);

         task.setNotesObject(taskNotes.get(uniqueID));

         task.setUniqueID(m_activityClashMap.addID(uniqueID));

         Integer calId = row.getInteger("clndr_id");
         ProjectCalendar cal = m_project.getCalendarByUniqueID(calId);
         task.setCalendar(cal);

         populateField(task, TaskField.START, TaskField.ACTUAL_START, TaskField.REMAINING_EARLY_START, TaskField.PLANNED_START);
         populateField(task, TaskField.FINISH, TaskField.ACTUAL_FINISH, TaskField.REMAINING_EARLY_FINISH, TaskField.PLANNED_FINISH);
         Duration work = Duration.add(task.getActualWork(), task.getRemainingWork(), task.getEffectiveCalendar());
         task.setWork(work);

         // Calculate actual duration
         Date actualStart = task.getActualStart();
         if (actualStart != null)
         {
            Date finish = task.getActualFinish();
            if (finish == null)
            {
               finish = m_project.getProjectProperties().getStatusDate();

               // Handle the case where the actual start is after the status date
               if (finish != null && finish.getTime() < actualStart.getTime())
               {
                  finish = actualStart;
               }
            }

            if (finish != null)
            {
               cal = task.getEffectiveCalendar();
               task.setActualDuration(cal.getWork(actualStart, finish, TimeUnit.HOURS));
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
            populateBaselineFromCurrentProject(task);
         }

         //
         // The schedule only includes total slack. We'll assume this value is correct and backfill start and finish slack values.
         //
         SlackHelper.inferSlack(task);

         m_eventManager.fireTaskReadEvent(task);
      }

      new ActivitySorter(wbsTasks).sort(m_project);

      updateStructure();
   }

   private void populateWBS(Task parent, Task task)
   {
      if (parent != null)
      {
         task.setWBS(parent.getWBS() + DEFAULT_WBS_SEPARATOR + task.getWBS());
         task.setActivityID(task.getWBS());
      }
      task.getChildTasks().forEach(t -> populateWBS(task, t));
   }

   private void populateBaselineFromCurrentProject(Task task)
   {
      task.setBaselineCost(task.getPlannedCost());
      task.setBaselineDuration(task.getPlannedDuration());
      task.setBaselineFinish(task.getPlannedFinish());
      task.setBaselineStart(task.getPlannedStart());
      task.setBaselineWork(task.getPlannedWork());
   }

   /**
    * Read details of any activity codes assigned to this task.
    *
    * @param task parent task
    */
   private void populateActivityCodes(Task task)
   {
      List<Integer> list = m_activityCodeAssignments.get(task.getUniqueID());
      if (list != null)
      {
         for (Integer id : list)
         {
            ActivityCodeValue value = m_activityCodeMap.get(id);
            if (value != null)
            {
               task.addActivityCode(value);
            }
         }
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
      Integer fieldId = row.getInteger("udf_type_id");
      FieldType field = m_udfFields.get(fieldId);

      Object value;
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

            case INTEGER:
            {
               value = row.getInteger("udf_number");
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
    * Populate the UDF values for this entity.
    *
    * @param tableName parent table name
    * @param type entity type
    * @param container entity
    * @param uniqueID entity Unique ID
    */
   private void populateUserDefinedFieldValues(String tableName, FieldTypeClass type, FieldContainer container, Integer uniqueID)
   {
      Map<Integer, List<Row>> tableData = m_udfValues.get(tableName);
      if (tableData != null)
      {
         List<Row> udf = tableData.get(uniqueID);
         if (udf != null)
         {
            for (Row r : udf)
            {
               addUDFValue(type, container, r);
            }
         }
      }
   }

   /**
    * Populate notebook topics.
    *
    * @param rows notebook topic rows
    */
   public void processNotebookTopics(List<Row> rows)
   {
      rows.forEach(this::processNotebookTopic);
   }

   /**
    * Populate an individual notebook topic.
    *
    * @param row notebook topic row
    */
   private void processNotebookTopic(Row row)
   {
      Integer uniqueID = row.getInteger("memo_type_id");
      Integer sequenceNumber = row.getInteger("seq_num");
      boolean epsFlag = row.getBoolean("eps_flag");
      boolean projectFlag = row.getBoolean("proj_flag");
      boolean wbsFlag = row.getBoolean("wbs_flag");
      boolean activityFlag = row.getBoolean("task_flag");
      String name = row.getString("memo_type");

      m_project.getNotesTopics().add(new NotesTopic(uniqueID, sequenceNumber, name, epsFlag, projectFlag, wbsFlag, activityFlag));
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
   public Map<Integer, Notes> getNotes(List<Row> rows, String uniqueIDColumn, String entityIdColumn, String textColumn)
   {
      Map<Integer, List<Row>> map = rows.stream().sorted(Comparator.comparing(r -> r.getInteger(uniqueIDColumn))).collect(Collectors.groupingBy(r -> r.getInteger(entityIdColumn), Collectors.mapping(r -> r, Collectors.toList())));
      NotesTopicContainer topics = m_project.getNotesTopics();
      Map<Integer, Notes> result = new HashMap<>();

      for (Map.Entry<Integer, List<Row>> entry : map.entrySet())
      {
         List<Notes> list = new ArrayList<>();
         for (Row row : entry.getValue())
         {
            HtmlNotes notes = getHtmlNote(row.getString(textColumn));
            if (notes == null || notes.isEmpty())
            {
               continue;
            }

            NotesTopic topic = topics.getByUniqueID(row.getInteger("memo_type_id"));
            if (topic == null)
            {
               topic = topics.getDefaultTopic();
            }

            list.add(new StructuredNotes(row.getInteger(uniqueIDColumn), topic, notes));
         }

         result.put(entry.getKey(), new ParentNotes(list));
      }

      return result;
   }

   /**
    * Create an HtmlNote instance.
    *
    * @param text note text
    * @return HtmlNote instance
    */
   private HtmlNotes getHtmlNote(String text)
   {
      if (text == null)
      {
         return null;
      }

      // Remove BOM and NUL characters
      String html = text.replaceAll("[\\uFEFF\\uFFFE\\x00]", "");

      // Replace newlines
      html = html.replaceAll("\\x7F\\x7F", "\n");

      HtmlNotes result = new HtmlNotes(html);

      return result.isEmpty() ? null : result;
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
    * This method sets the calendar used by a WBS entry. In P6 if all activities
    * under a WBS entry use the same calendar, the WBS entry uses this calendar
    * for date calculation. If the activities use different calendars, the WBS
    * entry will use the project's default calendar.
    *
    * @param task task to validate
    * @return calendar used by this task
    */
   private ProjectCalendar rollupCalendars(Task task)
   {
      ProjectCalendar result = null;

      if (task.hasChildTasks())
      {
         List<ProjectCalendar> calendars = task.getChildTasks().stream().map(this::rollupCalendars).distinct().collect(Collectors.toList());

         if (calendars.size() == 1)
         {
            ProjectCalendar firstCalendar = calendars.get(0);
            if (firstCalendar != null && firstCalendar != m_project.getDefaultCalendar())
            {
               result = firstCalendar;
               task.setCalendar(result);
            }
         }
      }
      else
      {
         result = task.getCalendar();
      }

      return result;
   }

   /**
    * The Primavera WBS entries we read in as tasks have user-entered start and end dates
    * which aren't calculated or adjusted based on the child task dates. We try
    * to compensate for this by using these user-entered dates as baseline dates, and
    * deriving the planned start, actual start, planned finish and actual finish from
    * the child tasks. This method recursively descends through the tasks to do this.
    *
    * @param parentTask parent task.
    */
   private void rollupDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         int finished = 0;
         Date startDate = parentTask.getStart();
         Date finishDate = parentTask.getFinish();
         Date plannedStartDate = parentTask.getPlannedStart();
         Date plannedFinishDate = parentTask.getPlannedFinish();
         Date actualStartDate = parentTask.getActualStart();
         Date actualFinishDate = parentTask.getActualFinish();
         Date earlyStartDate = parentTask.getEarlyStart();
         Date earlyFinishDate = parentTask.getEarlyFinish();
         Date lateStartDate = parentTask.getLateStart();
         Date lateFinishDate = parentTask.getLateFinish();
         Date baselineStartDate = parentTask.getBaselineStart();
         Date baselineFinishDate = parentTask.getBaselineFinish();
         Date remainingEarlyStartDate = parentTask.getRemainingEarlyStart();
         Date remainingEarlyFinishDate = parentTask.getRemainingEarlyFinish();
         Date remainingLateStartDate = parentTask.getRemainingLateStart();
         Date remainingLateFinishDate = parentTask.getRemainingLateFinish();
         boolean critical = false;

         for (Task task : parentTask.getChildTasks())
         {
            rollupDates(task);

            // the child tasks can have null dates (e.g. for nested wbs elements with no task children) so we
            // still must protect against some children having null dates

            startDate = DateHelper.min(startDate, task.getStart());
            finishDate = DateHelper.max(finishDate, task.getFinish());
            plannedStartDate = DateHelper.min(plannedStartDate, task.getPlannedStart());
            plannedFinishDate = DateHelper.max(plannedFinishDate, task.getPlannedFinish());
            actualStartDate = DateHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = DateHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = DateHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = DateHelper.max(earlyFinishDate, task.getEarlyFinish());
            remainingEarlyStartDate = DateHelper.min(remainingEarlyStartDate, task.getRemainingEarlyStart());
            remainingEarlyFinishDate = DateHelper.max(remainingEarlyFinishDate, task.getRemainingEarlyFinish());
            lateStartDate = DateHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = DateHelper.max(lateFinishDate, task.getLateFinish());
            remainingLateStartDate = DateHelper.min(remainingLateStartDate, task.getRemainingLateStart());
            remainingLateFinishDate = DateHelper.max(remainingLateFinishDate, task.getRemainingLateFinish());
            baselineStartDate = DateHelper.min(baselineStartDate, task.getBaselineStart());
            baselineFinishDate = DateHelper.max(baselineFinishDate, task.getBaselineFinish());

            if (task.getActualFinish() != null)
            {
               ++finished;
            }

            critical = critical || task.getCritical();
         }

         parentTask.setStart(startDate);
         parentTask.setFinish(finishDate);
         parentTask.setPlannedStart(plannedStartDate);
         parentTask.setPlannedFinish(plannedFinishDate);
         parentTask.setActualStart(actualStartDate);
         parentTask.setEarlyStart(earlyStartDate);
         parentTask.setEarlyFinish(earlyFinishDate);
         parentTask.setRemainingEarlyStart(remainingEarlyStartDate);
         parentTask.setRemainingEarlyFinish(remainingEarlyFinishDate);
         parentTask.setLateStart(lateStartDate);
         parentTask.setLateFinish(lateFinishDate);
         parentTask.setRemainingLateStart(remainingLateStartDate);
         parentTask.setRemainingLateFinish(remainingLateFinishDate);
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

         Duration plannedDuration = null;
         if (plannedStartDate != null && plannedFinishDate != null)
         {
            plannedDuration = parentTask.getEffectiveCalendar().getWork(plannedStartDate, plannedFinishDate, TimeUnit.HOURS);
            parentTask.setPlannedDuration(plannedDuration);
         }

         Duration actualDuration = null;
         Duration remainingDuration = null;
         if (parentTask.getActualFinish() == null)
         {
            Date taskStartDate = parentTask.getRemainingEarlyStart();
            if (taskStartDate == null)
            {
               taskStartDate = parentTask.getEarlyStart();
               if (taskStartDate == null)
               {
                  taskStartDate = plannedStartDate;
               }
            }

            Date taskFinishDate = parentTask.getRemainingEarlyFinish();
            if (taskFinishDate == null)
            {
               taskFinishDate = parentTask.getEarlyFinish();
               if (taskFinishDate == null)
               {
                  taskFinishDate = plannedFinishDate;
               }
            }

            if (taskStartDate != null)
            {
               if (parentTask.getActualStart() != null)
               {
                  actualDuration = parentTask.getEffectiveCalendar().getWork(parentTask.getActualStart(), taskStartDate, TimeUnit.HOURS);
               }

               if (taskFinishDate != null)
               {
                  remainingDuration = parentTask.getEffectiveCalendar().getWork(taskStartDate, taskFinishDate, TimeUnit.HOURS);
               }
            }
         }
         else
         {
            actualDuration = parentTask.getEffectiveCalendar().getWork(parentTask.getActualStart(), parentTask.getActualFinish(), TimeUnit.HOURS);
            remainingDuration = Duration.getInstance(0, TimeUnit.HOURS);
         }

         if (actualDuration != null && actualDuration.getDuration() < 0)
         {
            actualDuration = null;
         }

         if (remainingDuration != null && remainingDuration.getDuration() < 0)
         {
            remainingDuration = null;
         }

         parentTask.setActualDuration(actualDuration);
         parentTask.setRemainingDuration(remainingDuration);
         parentTask.setDuration(Duration.add(actualDuration, remainingDuration, parentTask.getEffectiveCalendar()));

         if (plannedDuration != null && remainingDuration != null && plannedDuration.getDuration() != 0)
         {
            double durationPercentComplete = ((plannedDuration.getDuration() - remainingDuration.getDuration()) / plannedDuration.getDuration()) * 100.0;
            if (durationPercentComplete < 0)
            {
               durationPercentComplete = 0;
            }
            else
            {
               if (durationPercentComplete > 100)
               {
                  durationPercentComplete = 100;
               }
            }
            parentTask.setPercentageComplete(Double.valueOf(durationPercentComplete));
            parentTask.setPercentCompleteType(PercentCompleteType.DURATION);
         }

         // Force total slack calculation to avoid overwriting the critical flag
         parentTask.getTotalSlack();
         parentTask.setCritical(critical);
      }
   }

   /**
    * The Primavera WBS entries we read in as tasks don't have work entered. We try
    * to compensate for this by summing the child tasks' work. This method recursively
    * descends through the tasks to do this.
    *
    * @param parentTask parent task.
    */
   private void rollupWork(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         ProjectCalendar calendar = parentTask.getEffectiveCalendar();

         Duration actualWork = null;
         Duration plannedWork = null;
         Duration remainingWork = null;
         Duration work = null;

         for (Task task : parentTask.getChildTasks())
         {
            rollupWork(task);

            actualWork = Duration.add(actualWork, task.getActualWork(), calendar);
            plannedWork = Duration.add(plannedWork, task.getPlannedWork(), calendar);
            remainingWork = Duration.add(remainingWork, task.getRemainingWork(), calendar);
            work = Duration.add(work, task.getWork(), calendar);
         }

         parentTask.setActualWork(actualWork);
         parentTask.setPlannedWork(plannedWork);
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

         if (successorTask != null && predecessorTask != null)
         {
            Relation relation = successorTask.addPredecessor(predecessorTask, type, lag);
            relation.setUniqueID(uniqueID);
            m_eventManager.fireRelationReadEvent(relation);
         }
         else
         {
            // If we're missing the predecessor or successor we assume they are external relations
            if (successorTask != null && predecessorTask == null)
            {
               ExternalRelation relation = new ExternalRelation(predecessorID, successorTask, type, lag, true);
               m_externalRelations.add(relation);
               relation.setUniqueID(uniqueID);
            }
            else
            {
               if (successorTask == null && predecessorTask != null)
               {
                  ExternalRelation relation = new ExternalRelation(successorID, predecessorTask, type, lag, false);
                  m_externalRelations.add(relation);
                  relation.setUniqueID(uniqueID);
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
   public void processAssignments(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(m_activityClashMap.getID(row.getInteger("task_id")));

         Integer roleID = m_roleClashMap.getID(row.getInteger("role_id"));
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
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            processFields(m_assignmentFields, row, assignment);

            assignment.setWorkContour(m_project.getWorkContours().getByUniqueID(row.getInteger("curv_id")));
            assignment.setRateIndex(RateTypeHelper.getInstanceFromXer(row.getString("rate_type")));
            assignment.setRole(m_project.getResourceByUniqueID(roleID));
            assignment.setOverrideRate(readRate(row.getDouble("cost_per_qty")));
            assignment.setRateSource(RateSourceHelper.getInstanceFromXer(row.getString("cost_per_qty_source_type")));

            populateField(assignment, AssignmentField.START, AssignmentField.ACTUAL_START, AssignmentField.PLANNED_START);
            populateField(assignment, AssignmentField.FINISH, AssignmentField.ACTUAL_FINISH, AssignmentField.PLANNED_FINISH);

            // calculate work
            Duration remainingWork = assignment.getRemainingWork();
            Duration actualRegularWork = row.getDuration("act_reg_qty");
            Duration actualOvertimeWork = assignment.getActualOvertimeWork();
            Duration actualWork = Duration.add(actualRegularWork, actualOvertimeWork, task.getEffectiveCalendar());
            assignment.setActualWork(actualWork);
            Duration totalWork = Duration.add(actualWork, remainingWork, task.getEffectiveCalendar());
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
            populateUserDefinedFieldValues("TASKRSRC", FieldTypeClass.ASSIGNMENT, assignment, assignment.getUniqueID());

            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Sets task cost fields by summing the resource assignment costs. The "projcost" table isn't
    * necessarily available in XER files so we do this instead to back into task costs. Costs for
    * the summary tasks constructed from Primavera WBS entries are calculated by recursively
    * summing child costs.
    */
   public void rollupValues()
   {
      m_project.getChildTasks().forEach(this::rollupCalendars);
      m_project.getChildTasks().forEach(this::rollupDates);
      m_project.getChildTasks().forEach(this::rollupWork);
      m_project.getChildTasks().forEach(this::rollupCosts);

      if (m_project.getProjectProperties().getBaselineProjectUniqueID() == null)
      {
         m_project.getTasks().stream().filter(Task::getSummary).forEach(this::populateBaselineFromCurrentProject);
      }
   }

   /**
    * See the notes above.
    *
    * @param parentTask parent task
    */
   private void rollupCosts(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         double plannedCost = 0;
         double actualCost = 0;
         double remainingCost = 0;
         double cost = 0;

         //process children first before adding their costs
         for (Task child : parentTask.getChildTasks())
         {
            rollupCosts(child);
            plannedCost += NumberHelper.getDouble(child.getPlannedCost());
            actualCost += NumberHelper.getDouble(child.getActualCost());
            remainingCost += NumberHelper.getDouble(child.getRemainingCost());
            cost += NumberHelper.getDouble(child.getCost());
         }

         parentTask.setPlannedCost(NumberHelper.getDouble(plannedCost));
         parentTask.setActualCost(NumberHelper.getDouble(actualCost));
         parentTask.setRemainingCost(NumberHelper.getDouble(remainingCost));
         parentTask.setCost(NumberHelper.getDouble(cost));
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
    * Extract expense items and add to a task.
    *
    * @param rows expense item rows
    */
   public void processExpenseItems(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(m_activityClashMap.getID(row.getInteger("task_id")));
         if (task != null)
         {
            List<ExpenseItem> items = task.getExpenseItems();
            ExpenseItem ei = new ExpenseItem(task);
            items.add(ei);

            ei.setAccount(m_project.getCostAccounts().getByUniqueID(row.getInteger("acct_id")));
            ei.setAccrueType(AccrueTypeHelper.getInstanceFromXer(row.getString("cost_load_type")));
            ei.setActualCost(row.getDouble("act_cost"));
            ei.setAutoComputeActuals(row.getBoolean("auto_compute_act_flag"));
            ei.setCategory(m_project.getExpenseCategories().getByUniqueID(row.getInteger("cost_type_id")));
            ei.setDescription(row.getString("cost_descr"));
            ei.setDocumentNumber(row.getString("po_number"));
            ei.setName(row.getString("cost_name"));
            ei.setPlannedCost(row.getDouble("target_cost"));
            ei.setPlannedUnits(row.getDouble("target_qty"));
            ei.setPricePerUnit(row.getDouble("cost_per_qty"));
            ei.setRemainingCost(row.getDouble("remain_cost"));
            ei.setUniqueID(row.getInteger("cost_item_id"));
            ei.setUnitOfMeasure(row.getString("qty_name"));
            ei.setVendor(row.getString("vendor_name"));

            ei.setAtCompletionCost(NumberHelper.sumAsDouble(ei.getActualCost(), ei.getRemainingCost()));

            double pricePerUnit = NumberHelper.getDouble(ei.getPricePerUnit());
            if (pricePerUnit != 0.0)
            {
               ei.setActualUnits(Double.valueOf(NumberHelper.getDouble(ei.getActualCost()) / pricePerUnit));
               ei.setRemainingUnits(Double.valueOf(NumberHelper.getDouble(ei.getRemainingCost()) / pricePerUnit));
               ei.setAtCompletionUnits(NumberHelper.sumAsDouble(ei.getActualUnits(), ei.getRemainingUnits()));
            }

            // Roll up to parent task
            task.setPlannedCost(NumberHelper.sumAsDouble(task.getPlannedCost(), ei.getPlannedCost()));
            task.setActualCost(NumberHelper.sumAsDouble(task.getActualCost(), ei.getActualCost()));
            task.setRemainingCost(NumberHelper.sumAsDouble(task.getRemainingCost(), ei.getRemainingCost()));
            task.setCost(NumberHelper.sumAsDouble(task.getCost(), ei.getAtCompletionCost()));
         }
      }
   }

   /**
    * Extract activity steps and add to their parent task.
    *
    * @param rows expense item rows
    */
   public void processActivitySteps(List<Row> rows)
   {
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(m_activityClashMap.getID(row.getInteger("task_id")));
         if (task == null)
         {
            continue;
         }

         Step step = new Step(task);
         task.getSteps().add(step);
         step.setUniqueID(row.getInteger("proc_id"));
         step.setName(row.getString("proc_name"));
         step.setPercentComplete(row.getDouble("complete_pct"));
         step.setSequenceNumber(row.getInteger("seq_num"));
         step.setWeight(row.getDouble("proc_wt"));
         step.setDescriptionObject(getNotes(row.getString("proc_descr")));
      }
   }

   /**
    * Extract schedule options.
    *
    * @param row schedule options row
    */
   public void processScheduleOptions(Row row)
   {
      Map<String, Object> customProperties = new TreeMap<>();

      //
      // Leveling Options
      //
      // Automatically level resources when scheduling
      customProperties.put("ConsiderAssignmentsInOtherProjects", Boolean.valueOf(row.getBoolean("level_outer_assign_flag")));
      customProperties.put("ConsiderAssignmentsInOtherProjectsWithPriorityEqualHigherThan", row.getString("level_outer_assign_priority"));
      customProperties.put("PreserveScheduledEarlyAndLateDates", Boolean.valueOf(row.getBoolean("level_keep_sched_date_flag")));
      // Recalculate assignment costs after leveling
      customProperties.put("LevelAllResources", Boolean.valueOf(row.getBoolean("level_all_rsrc_flag")));
      customProperties.put("LevelResourcesOnlyWithinActivityTotalFloat", Boolean.valueOf(row.getBoolean("level_within_float_flag")));
      customProperties.put("PreserveMinimumFloatWhenLeveling", row.getString("level_float_thrs_cnt"));
      customProperties.put("MaxPercentToOverallocateResources", row.getString("level_over_alloc_pct"));
      customProperties.put("LevelingPriorities", row.getString("levelprioritylist"));

      //
      // Schedule
      //
      customProperties.put("SetDataDateAndPlannedStartToProjectForecastStart", Boolean.valueOf(row.getBoolean("sched_setplantoforecast")));

      //
      // Schedule Options - General
      //
      customProperties.put("IgnoreRelationshipsToAndFromOtherProjects", row.getString("sched_outer_depend_type"));
      customProperties.put("MakeOpenEndedActivitiesCritical", Boolean.valueOf(row.getBoolean("sched_open_critical_flag")));
      customProperties.put("UseExpectedFinishDates", Boolean.valueOf(row.getBoolean("sched_use_expect_end_flag")));
      // Schedule automatically when a change affects dates
      // Level resources during scheduling
      customProperties.put("WhenSchedulingProgressedActivitiesUseRetainedLogic", Boolean.valueOf(row.getBoolean("sched_retained_logic")));
      customProperties.put("WhenSchedulingProgressedActivitiesUseProgressOverride", Boolean.valueOf(row.getBoolean("sched_progress_override")));
      customProperties.put("ComputeStartToStartLagFromEarlyStart", Boolean.valueOf(row.getBoolean("sched_lag_early_start_flag")));
      // Define critical activities as
      customProperties.put("CalculateFloatBasedOnFishDateOfEachProject", Boolean.valueOf(row.getBoolean("sched_use_project_end_date_for_float")));
      customProperties.put("ComputeTotalFloatAs", row.getString("sched_float_type"));
      customProperties.put("CalendarForSchedulingRelationshipLag", row.getString("sched_calendar_on_relationship_lag"));

      //
      // Schedule Options - Advanced
      //
      customProperties.put("CalculateMultipleFloatPaths", Boolean.valueOf(row.getBoolean("enable_multiple_longest_path_calc")));
      customProperties.put("CalculateMultiplePathsUsingTotalFloat", Boolean.valueOf(row.getBoolean("use_total_float_multiple_longest_paths")));
      customProperties.put("DisplayMultipleFloatPathsEndingWithActivity", row.getString("key_activity_for_multiple_longest_paths"));
      customProperties.put("LimitNumberOfPathsToCalculate", Boolean.valueOf(row.getBoolean("limit_multiple_longest_path_calc")));
      customProperties.put("NumberofPathsToCalculate", row.getString("max_multiple_longest_path"));

      m_project.getProjectProperties().setCustomProperties(customProperties);
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
               value = ResourceTypeHelper.getInstanceFromXer(row.getString(name));
               break;
            }

            case TASK_TYPE:
            {
               value = TaskTypeHelper.getInstanceFromXer(row.getString(name));
               break;
            }

            case CONSTRAINT:
            {
               value = ConstraintTypeHelper.getInstanceFromXer(row.getString(name));
               break;
            }

            case PRIORITY:
            {
               value = PriorityHelper.getInstanceFromXer(row.getString(name));
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
    * Retrieve the default mapping between MPXJ resource fields and Primavera resource field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultResourceFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<>();

      map.put(ResourceField.UNIQUE_ID, "rsrc_id");
      map.put(ResourceField.GUID, "guid");
      map.put(ResourceField.NAME, "rsrc_name");
      map.put(ResourceField.CODE, "employee_code");
      map.put(ResourceField.EMAIL_ADDRESS, "email_addr");
      map.put(ResourceField.NOTES, "rsrc_notes");
      map.put(ResourceField.CREATED, "create_date");
      map.put(ResourceField.TYPE, "rsrc_type");
      map.put(ResourceField.PARENT_ID, "parent_rsrc_id");
      map.put(ResourceField.RESOURCE_ID, "rsrc_short_name");
      map.put(ResourceField.CALCULATE_COSTS_FROM_UNITS, "def_cost_qty_link_flag");
      map.put(ResourceField.SEQUENCE_NUMBER, "rsrc_seq_num");
      map.put(ResourceField.ACTIVE, "active_flag");
      map.put(ResourceField.LOCATION_UNIQUE_ID, "location_id");

      return map;
   }

   /**
    * Retrieve the default mapping between MPXJ resource fields and Primavera role field names.
    *
    * @return mapping
    */
   public static Map<FieldType, String> getDefaultRoleFieldMap()
   {
      Map<FieldType, String> map = new LinkedHashMap<>();

      map.put(ResourceField.UNIQUE_ID, "role_id");
      map.put(ResourceField.NAME, "role_name");
      map.put(ResourceField.RESOURCE_ID, "role_short_name");
      map.put(ResourceField.NOTES, "role_descr");
      map.put(ResourceField.PARENT_ID, "parent_role_id");
      map.put(ResourceField.CALCULATE_COSTS_FROM_UNITS, "def_cost_qty_link_flag");
      map.put(ResourceField.SEQUENCE_NUMBER, "seq_num");

      return map;
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

      map.put(TaskField.UNIQUE_ID, "task_id");
      map.put(TaskField.GUID, "guid");
      map.put(TaskField.NAME, "task_name");
      map.put(TaskField.REMAINING_DURATION, "remain_drtn_hr_cnt");
      map.put(TaskField.ACTUAL_WORK, "act_work_qty");
      map.put(TaskField.REMAINING_WORK, "remain_work_qty");
      map.put(TaskField.PLANNED_WORK, "target_work_qty");
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
      map.put(TaskField.PRIMARY_RESOURCE_ID, "rsrc_id");
      map.put(TaskField.SUSPEND_DATE, "suspend_date");
      map.put(TaskField.RESUME, "resume_date");
      map.put(TaskField.EXTERNAL_EARLY_START, "external_early_start_date");
      map.put(TaskField.EXTERNAL_LATE_FINISH, "external_late_end_date");
      map.put(TaskField.LONGEST_PATH, "driving_path_flag");
      map.put(TaskField.LOCATION_UNIQUE_ID, "location_id");

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

      return map;
   }

   private final ProjectFile m_project;
   private final EventManager m_eventManager;
   private final ClashMap m_activityClashMap = new ClashMap();
   private final ClashMap m_roleClashMap = new ClashMap();
   private final DateFormat m_calendarTimeFormat = new SimpleDateFormat("HH:mm");
   private Integer m_defaultCalendarID;
   private final Map<FieldType, String> m_resourceFields;
   private final Map<FieldType, String> m_roleFields;
   private final Map<FieldType, String> m_wbsFields;
   private final Map<FieldType, String> m_taskFields;
   private final Map<FieldType, String> m_assignmentFields;
   private final List<ExternalRelation> m_externalRelations = new ArrayList<>();
   private final boolean m_matchPrimaveraWBS;
   private final boolean m_wbsIsFullPath;

   private final Map<Integer, FieldType> m_udfFields = new HashMap<>();
   private final Map<String, Map<Integer, List<Row>>> m_udfValues = new HashMap<>();

   private final Map<Integer, ActivityCodeValue> m_activityCodeMap = new HashMap<>();
   private final Map<Integer, List<Integer>> m_activityCodeAssignments = new HashMap<>();

   private final ObjectSequence m_relationObjectID;

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

   static final long EXCEPTION_EPOCH = -2209161599935L;

   static final String DEFAULT_WBS_SEPARATOR = ".";
}