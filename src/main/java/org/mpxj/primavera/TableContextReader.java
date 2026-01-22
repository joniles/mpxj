/*
 * file:       TableContextReader.java
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeContainer;
import org.mpxj.ActivityCodeValue;
import org.mpxj.Availability;
import org.mpxj.CostAccount;
import org.mpxj.CostAccountContainer;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Currency;
import org.mpxj.CurrencyContainer;
import org.mpxj.ExpenseCategory;
import org.mpxj.ExpenseCategoryContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.LocalTimeRange;
import org.mpxj.Location;
import org.mpxj.LocationContainer;
import org.mpxj.NotesTopic;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCode;
import org.mpxj.ProjectCodeContainer;
import org.mpxj.ProjectCodeValue;
import org.mpxj.ProjectConfig;
import org.mpxj.Rate;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignmentCode;
import org.mpxj.ResourceAssignmentCodeContainer;
import org.mpxj.ResourceAssignmentCodeValue;
import org.mpxj.ResourceCode;
import org.mpxj.ResourceCodeContainer;
import org.mpxj.ResourceCodeValue;
import org.mpxj.ResourceField;
import org.mpxj.RoleCode;
import org.mpxj.RoleCodeContainer;
import org.mpxj.RoleCodeValue;
import org.mpxj.Shift;
import org.mpxj.ShiftContainer;
import org.mpxj.ShiftPeriod;
import org.mpxj.ShiftPeriodContainer;
import org.mpxj.TimeUnit;
import org.mpxj.UnitOfMeasure;
import org.mpxj.UnitOfMeasureContainer;
import org.mpxj.UserDefinedField;
import org.mpxj.UserDefinedFieldContainer;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;

/**
 * Populate a ProjectContext instance from tabular P6 data.
 */
abstract class TableContextReader
{
   /**
    * Populate the project configuration.
    */
   protected void configure()
   {
      ProjectConfig config = m_state.getContext().getProjectConfig();
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);
      config.setAutoAssignmentUniqueID(false);
      config.setAutoWBS(false);
      config.setAutoRelationUniqueID(false);
      config.setBaselineStrategy(PrimaveraBaselineStrategy.PLANNED_ATTRIBUTES);
   }

   /**
    * Process currencies.
    *
    * @param currencies currency data
    */
   protected void processCurrencies(List<Row> currencies)
   {
      CurrencyContainer container = m_state.getContext().getCurrencies();
      currencies.forEach(
         row -> container.add(
            new Currency.Builder(m_state.getContext())
               .uniqueID(row.getInteger("curr_id"))
               .numberOfDecimalPlaces(row.getInteger("decimal_digit_cnt"))
               .symbol(row.getString("curr_symbol"))
               .decimalSymbol(row.getString("decimal_symbol"))
               .digitGroupingSymbol(row.getString("digit_group_symbol"))
               .positiveCurrencyFormat(row.getString("pos_curr_fmt_type"))
               .negativeCurrencyFormat(row.getString("neg_curr_fmt_type"))
               .name(row.getString("curr_type"))
               .currencyID(row.getString("curr_short_name"))
               // group_digit_cnt
               .exchangeRate(row.getDouble("base_exch_rate"))
               .build()));
   }

   /**
    * Process locations.
    *
    * @param locations locations data
    */
   protected void processLocations(List<Row> locations)
   {
      LocationContainer container = m_state.getContext().getLocations();
      locations.forEach(
         row -> container.add(
            new Location.Builder(m_state.getContext())
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
               .build()));
   }

   /**
    * Process shifts.
    *
    * @param shifts shift data
    * @param periods shift period data
    */
   protected void processShifts(List<Row> shifts, List<Row> periods)
   {
      ShiftContainer shiftContainer = m_state.getContext().getShifts();
      shifts.forEach(r -> shiftContainer.add(
         new Shift.Builder(m_state.getContext())
            .uniqueID(r.getInteger("shift_id"))
            .name(r.getString("shift_name"))
            .build()));

      ShiftPeriodContainer shiftPeriodContainer = m_state.getContext().getShiftPeriods();
      for (Row row : periods)
      {
         Shift shift = shiftContainer.getByUniqueID(row.getInteger("shift_id"));
         if (shift == null)
         {
            continue;
         }

         ShiftPeriod period = new ShiftPeriod.Builder(m_state.getContext(), shift)
            .uniqueID(row.getInteger("shift_period_id"))
            .start(row.getInteger("shift_start_hr_num"))
            .build();
         shiftPeriodContainer.add(period);
      }
   }

   /**
    * Process units of measure.
    *
    * @param units units of measure
    */
   protected void processUnitsOfMeasure(List<Row> units)
   {
      UnitOfMeasureContainer container = m_state.getContext().getUnitsOfMeasure();
      units.forEach(row -> container.add(processUnitOfMeasure(row)));
   }

   /**
    * Create a unit of measure instance.
    *
    * @param row unit of measure data
    * @return UnitOfMeasure instance
    */
   private UnitOfMeasure processUnitOfMeasure(Row row)
   {
      return new UnitOfMeasure.Builder(m_state.getContext())
         .uniqueID(row.getInteger("unit_id"))
         .abbreviation(row.getString("unit_abbrev"))
         .name(row.getString("unit_name"))
         .sequenceNumber(row.getInteger("seq_num"))
         .build();
   }

   /**
    * Process expense categories.
    *
    * @param categories expense categories
    */
   protected void processExpenseCategories(List<Row> categories)
   {
      ExpenseCategoryContainer container = m_state.getContext().getExpenseCategories();
      categories.forEach(row -> container.add(new ExpenseCategory.Builder(m_state.getContext())
         .uniqueID(row.getInteger("cost_type_id"))
         .name(row.getString("cost_type"))
         .sequenceNumber(row.getInteger("seq_num"))
         .build()));
   }

   /**
    * Process cost accounts.
    *
    * @param accounts cost accounts
    */
   protected void processCostAccounts(List<Row> accounts)
   {
      CostAccountContainer container = m_state.getContext().getCostAccounts();
      HierarchyHelper.sortHierarchy(accounts, v -> v.getInteger("acct_id"), v -> v.getInteger("parent_acct_id")).forEach(row -> container.add(
         new CostAccount.Builder(m_state.getContext())
            .uniqueID(row.getInteger("acct_id"))
            .id(row.getString("acct_short_name"))
            .name(row.getString("acct_name"))
            .notes(NotesHelper.getNotes(row.getString("acct_descr")))
            .sequenceNumber(row.getInteger("acct_seq_num"))
            .parent(container.getByUniqueID(row.getInteger("parent_acct_id")))
            .build()));
   }

   /**
    * Populate notebook topics.
    *
    * @param rows notebook topic rows
    */
   protected void processNotebookTopics(List<Row> rows)
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
      NotesTopic topic = new NotesTopic.Builder(m_state.getContext())
         .uniqueID(row.getInteger("memo_type_id"))
         .sequenceNumber(row.getInteger("seq_num"))
         .availableForEPS(row.getBoolean("eps_flag"))
         .availableForProject(row.getBoolean("proj_flag"))
         .availableForWBS(row.getBoolean("wbs_flag"))
         .availableForActivity(row.getBoolean("task_flag"))
         .name(row.getString("memo_type"))
         .build();

      m_state.getContext().getNotesTopics().add(topic);
   }

   /**
    * Process User Defined Field (UDF) definitions.
    *
    * @param fields field definitions
    */
   protected void processUdfDefinitions(List<Row> fields)
   {
      UserDefinedFieldContainer container = m_state.getContext().getUserDefinedFields();

      for (Row row : fields)
      {
         Integer fieldId = row.getInteger("udf_type_id");
         String tableName = row.getString("table_name");

         FieldTypeClass fieldTypeClass = FieldTypeClassHelper.getInstanceFromXer(tableName);
         if (fieldTypeClass == null)
         {
            continue;
         }

         UserDefinedField fieldType = new UserDefinedField.Builder(m_state.getContext())
            .uniqueID(fieldId)
            .internalName(row.getString("udf_type_name"))
            .externalName(row.getString("udf_type_label"))
            .fieldTypeClass(fieldTypeClass)
            .summaryTaskOnly(tableName.equals("PROJWBS"))
            .dataType(UdfHelper.getDataTypeFromXer(row.getString("logical_data_type")))
            .build();

         container.add(fieldType);
         m_state.getContext().getCustomFields().add(fieldType).setAlias(fieldType.getName()).setUniqueID(fieldId);
      }
   }

   /**
    * Read project code definitions.
    *
    * @param types project code type data
    * @param typeValues project code value data
    */
   protected void processProjectCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ProjectCodeContainer container = m_state.getContext().getProjectCodes();
      Map<Integer, ProjectCode> map = new HashMap<>();

      for (Row row : types)
      {
         ProjectCode code = new ProjectCode.Builder(m_state.getContext())
            .uniqueID(row.getInteger("proj_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("proj_catg_type"))
            .maxLength(row.getInteger("proj_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("proj_catg_id"), v -> v.getInteger("parent_proj_catg_id"));
      for (Row row : typeValues)
      {
         ProjectCode code = map.get(row.getInteger("proj_catg_type_id"));
         if (code != null)
         {
            ProjectCodeValue value = new ProjectCodeValue.Builder(m_state.getContext())
               .projectCode(code)
               .uniqueID(row.getInteger("proj_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("proj_catg_short_name"))
               .description(row.getString("proj_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_proj_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read resource code definitions.
    *
    * @param types resource code type data
    * @param typeValues resource code value data
    */
   protected void processResourceCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ResourceCodeContainer container = m_state.getContext().getResourceCodes();
      Map<Integer, ResourceCode> map = new HashMap<>();

      for (Row row : types)
      {
         ResourceCode code = new ResourceCode.Builder(m_state.getContext())
            .uniqueID(row.getInteger("rsrc_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("rsrc_catg_type"))
            .maxLength(row.getInteger("rsrc_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("rsrc_catg_id"), v -> v.getInteger("parent_rsrc_catg_id"));
      for (Row row : typeValues)
      {
         ResourceCode code = map.get(row.getInteger("rsrc_catg_type_id"));
         if (code != null)
         {
            ResourceCodeValue value = new ResourceCodeValue.Builder(m_state.getContext())
               .resourceCode(code)
               .uniqueID(row.getInteger("rsrc_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("rsrc_catg_short_name"))
               .description(row.getString("rsrc_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_rsrc_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read role code definitions.
    *
    * @param types role code type data
    * @param typeValues role code value data
    */
   protected void processRoleCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      RoleCodeContainer container = m_state.getContext().getRoleCodes();
      Map<Integer, RoleCode> map = new HashMap<>();

      for (Row row : types)
      {
         RoleCode code = new RoleCode.Builder(m_state.getContext())
            .uniqueID(row.getInteger("role_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("role_catg_type"))
            .maxLength(row.getInteger("role_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("role_catg_id"), v -> v.getInteger("parent_role_catg_id"));
      for (Row row : typeValues)
      {
         RoleCode code = map.get(row.getInteger("role_catg_type_id"));
         if (code != null)
         {
            RoleCodeValue value = new RoleCodeValue.Builder(m_state.getContext())
               .roleCode(code)
               .uniqueID(row.getInteger("role_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("role_catg_short_name"))
               .description(row.getString("role_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_role_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read resource assignment code definitions.
    *
    * @param types resource assignment code type data
    * @param typeValues resource assignment code value data
    */
   protected void processResourceAssignmentCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ResourceAssignmentCodeContainer container = m_state.getContext().getResourceAssignmentCodes();
      Map<Integer, ResourceAssignmentCode> map = new HashMap<>();

      for (Row row : types)
      {
         ResourceAssignmentCode code = new ResourceAssignmentCode.Builder(m_state.getContext())
            .uniqueID(row.getInteger("asgnmnt_catg_type_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("asgnmnt_catg_type"))
            .maxLength(row.getInteger("asgnmnt_catg_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("asgnmnt_catg_id"), v -> v.getInteger("parent_asgnmnt_catg_id"));
      for (Row row : typeValues)
      {
         ResourceAssignmentCode code = map.get(row.getInteger("asgnmnt_catg_type_id"));
         if (code != null)
         {
            ResourceAssignmentCodeValue value = new ResourceAssignmentCodeValue.Builder(m_state.getContext())
               .resourceAssignmentCode(code)
               .uniqueID(row.getInteger("asgnmnt_catg_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("asgnmnt_catg_short_name"))
               .description(row.getString("asgnmnt_catg_name"))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_asgnmnt_catg_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Read activity code definitions.
    *
    * @param types activity code type data
    * @param typeValues activity code value data
    */
   protected void processActivityCodeDefinitions(List<Row> types, List<Row> typeValues)
   {
      ActivityCodeContainer container = m_state.getContext().getActivityCodes();
      Map<Integer, ActivityCode> map = new HashMap<>();

      for (Row row : types)
      {
         ActivityCode code = new ActivityCode.Builder(m_state.getContext())
            .uniqueID(row.getInteger("actv_code_type_id"))
            .scope(ActivityCodeScopeHelper.getInstanceFromXer(row.getString("actv_code_type_scope")))
            .scopeEpsUniqueID(row.getInteger("wbs_id"))
            .scopeProjectUniqueID(row.getInteger("proj_id"))
            .sequenceNumber(row.getInteger("seq_num"))
            .name(row.getString("actv_code_type"))
            .secure(row.getBoolean("super_flag"))
            .maxLength(row.getInteger("actv_short_len"))
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, v -> v.getInteger("actv_code_id"), v -> v.getInteger("parent_actv_code_id"));
      for (Row row : typeValues)
      {
         ActivityCode code = map.get(row.getInteger("actv_code_type_id"));
         if (code != null)
         {
            ActivityCodeValue value = new ActivityCodeValue.Builder(m_state.getContext())
               .activityCode(code)
               .uniqueID(row.getInteger("actv_code_id"))
               .sequenceNumber(row.getInteger("seq_num"))
               .name(row.getString("short_name"))
               .description(row.getString("actv_code_name"))
               .color(ColorHelper.parseHexColor(row.getString("color")))
               .parentValue(code.getValueByUniqueID(row.getInteger("parent_actv_code_id")))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Process project calendars.
    *
    * @param rows project calendar data
    */
   protected void processCalendars(List<Row> rows)
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
         ProjectCalendar baseCalendar = m_state.getContext().getCalendars().getByUniqueID(entry.getValue());
         if (baseCalendar != null)
         {
            entry.getKey().setParent(baseCalendar);
         }
      }
   }

   /**
    * Process data for an individual calendar.
    *
    * @param row calendar data
    * @return ProjectCalendar instance
    */
   protected ProjectCalendar processCalendar(Row row)
   {
      ProjectCalendar calendar = m_state.getContext().getCalendars().add();
      calendar.setUniqueID(row.getInteger("clndr_id"));
      calendar.setName(row.getString("clndr_name"));
      calendar.setType(CalendarTypeHelper.getInstanceFromXer(row.getString("clndr_type")));
      calendar.setProjectUniqueID(row.getInteger("proj_id"));
      calendar.setPersonal(row.getBoolean("rsrc_private"));

      // We may override this later with project properties
      if (row.getBoolean("default_flag") && m_state.getContext().getCalendars().getDefaultCalendarUniqueID() == null)
      {
         calendar.setDefault();
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
      Double rowHoursPerDay = row.getDouble("day_hr_cnt");
      Double rowHoursPerWeek = row.getDouble("week_hr_cnt");
      Double rowHoursPerMonth = row.getDouble("month_hr_cnt");
      Double rowHoursPerYear = row.getDouble("year_hr_cnt");

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

         for (DayOfWeek day : DayOfWeek.values())
         {
            ProjectCalendarHours hours = calendar.getCalendarHours(day);
            if (hours == null)
            {
               continue;
            }

            if (!hours.isEmpty())
            {
               ++workingDays;
               for (LocalTimeRange range : hours)
               {
                  minutesPerWeek += (range.getDurationAsMilliseconds() / (1000 * 60));
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

      return calendar;
   }

   /**
    * Process calendar days of the week.
    *
    * @param calendar project calendar
    * @param daysOfWeek calendar data
    */
   private void processCalendarDays(ProjectCalendar calendar, StructuredTextRecord daysOfWeek)
   {
      Map<DayOfWeek, StructuredTextRecord> days = daysOfWeek.getChildren().stream().filter(d -> DayOfWeekHelper.getInstance(Integer.parseInt(d.getRecordName())) != null).collect(Collectors.toMap(d -> DayOfWeekHelper.getInstance(Integer.parseInt(d.getRecordName())), d -> d));

      for (DayOfWeek day : DayOfWeek.values())
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
   private void processCalendarHours(DayOfWeek day, ProjectCalendar calendar, StructuredTextRecord dayRecord)
   {
      // Get hours
      ProjectCalendarHours hours = calendar.addCalendarHours(day);
      List<StructuredTextRecord> recHours = dayRecord.getChildren();
      if (recHours.isEmpty())
      {
         // No data -> not working
         calendar.setWorkingDay(day, false);
      }
      else
      {
         // Read hours
         for (StructuredTextRecord recWorkingHours : recHours)
         {
            addHours(hours, recWorkingHours);
         }
         calendar.setWorkingDay(day, !calendar.getHours(day).isEmpty());
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

      DateTimeFormatter formatter = startText.indexOf(' ') == -1 ? m_twentyFourHourTimeFormat : m_twelveHourTimeFormat;
      try
      {
         LocalTime start = LocalTime.parse(startText, formatter);
         LocalTime end = LocalTime.parse(endText, formatter);
         ranges.add(new LocalTimeRange(start, end));
      }

      catch (DateTimeParseException ex)
      {
         if (m_state.getIgnoreErrors())
         {
            m_state.getContext().addIgnoredError(ex);
         }
         else
         {
            throw ex;
         }
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
         long daysFromEpoch;

         try
         {
            daysFromEpoch = Integer.parseInt(exception.getAttribute("d"));
         }

         catch (NumberFormatException ex)
         {
            if (m_state.getIgnoreErrors())
            {
               m_state.getContext().addIgnoredError(ex);
               continue;
            }
            throw ex;
         }

         LocalDate startEx = EXCEPTION_EPOCH.plusDays(daysFromEpoch);

         ProjectCalendarException pce = calendar.addCalendarException(startEx, startEx);
         for (StructuredTextRecord exceptionHours : exception.getChildren())
         {
            addHours(pce, exceptionHours);
         }
      }
   }

   /**
    * Process User Defined Field (UDF) values.
    *
    * @param values field values
    */
   protected void processUdfValues(List<Row> values)
   {
      for (Row row : values)
      {
         FieldType fieldType = m_state.getContext().getUserDefinedFields().getByUniqueID(row.getInteger("udf_type_id"));
         if (fieldType == null)
         {
            // UDF values for entities we don't currently support
            continue;
         }

         String tableName = FieldTypeClassHelper.getXerFromInstance(fieldType);
         Map<Integer, List<Row>> tableData = m_state.getUdfValues().computeIfAbsent(tableName, k -> new HashMap<>());

         Integer id = row.getInteger("fk_id");
         List<Row> list = tableData.computeIfAbsent(id, k -> new ArrayList<>());
         list.add(row);
      }
   }

   /**
    * Process resource code assignments.
    *
    * @param assignments resource code assignments
    */
   protected void processResourceCodeAssignments(List<Row> assignments)
   {
      for (Row row : assignments)
      {
         Integer resourceID = row.getInteger("rsrc_id");
         List<Row> list = m_resourceCodeAssignments.computeIfAbsent(resourceID, k -> new ArrayList<>());
         list.add(row);
      }
   }

   /**
    * Process resources.
    *
    * @param rows resource data
    */
   protected void processResources(List<Row> rows)
   {
      for (Row row : rows)
      {
         Resource resource = m_state.getContext().getResources().addResource();
         TableReaderHelper.processFields(m_state.getResourceFields(), row, resource);
         resource.setCalendar(m_state.getContext().getCalendars().getByUniqueID(row.getInteger("clndr_id")));

         // Add User Defined Fields
         TableReaderHelper.populateUserDefinedFieldValues(m_state, "RSRC", FieldTypeClass.RESOURCE, resource, resource.getUniqueID());

         resource.setNotesObject(NotesHelper.getNotes(resource.getNotes()));

         // Note: if default units per time is an empty field, this represents a value of zero in P6
         Number defaultUnitsPerTime = row.getDouble("def_qty_per_hr");
         defaultUnitsPerTime = defaultUnitsPerTime == null ? NumberHelper.DOUBLE_ZERO : Double.valueOf(defaultUnitsPerTime.doubleValue() * 100.0);
         resource.setDefaultUnits(defaultUnitsPerTime);

         populateResourceCodeValues(resource);

         m_state.getContext().getEventManager().fireResourceReadEvent(resource);
      }
   }

   /**
    * Read details of any resource codes assigned to this resource.
    *
    * @param resource parent resource
    */
   private void populateResourceCodeValues(Resource resource)
   {
      List<Row> list = m_resourceCodeAssignments.get(resource.getUniqueID());
      if (list == null)
      {
         return;
      }

      for (Row row : list)
      {
         ResourceCode code = m_state.getContext().getResourceCodes().getByUniqueID(row.getInteger("rsrc_catg_type_id"));
         if (code == null)
         {
            continue;
         }

         ResourceCodeValue value = code.getValueByUniqueID(row.getInteger("rsrc_catg_id"));
         if (value != null)
         {
            resource.addResourceCodeValue(value);
         }
      }
   }

   /**
    * Process resource rates.
    *
    * @param rows resource rate data
    */
   protected void processResourceRates(List<Row> rows)
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
         LocalDateTime d1 = r1.getDate("start_date");
         LocalDateTime d2 = r2.getDate("start_date");
         return LocalDateTimeHelper.compare(d1, d2);
      });

      Resource resource = null;

      for (int i = 0; i < rows.size(); ++i)
      {
         Row row = rows.get(i);

         Integer resourceID = row.getInteger("rsrc_id");
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_state.getContext().getResources().getByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            Rate.valueOf(row.getDouble("cost_per_qty"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty2"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty3"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty4"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty5"), TimeUnit.HOURS),
         };

         Double costPerUse = NumberHelper.getDouble(0.0);
         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getDouble("max_qty_per_hr")) * 100); // adjust to be % as in MS Project
         ShiftPeriod period = m_state.getContext().getShiftPeriods().getByUniqueID(row.getInteger("shift_period_id"));

         LocalDateTime startDate = row.getDate("start_date");
         LocalDateTime endDate = LocalDateTimeHelper.END_DATE_NA;

         if (i + 1 < rows.size())
         {
            Row nextRow = rows.get(i + 1);
            int nextResourceID = nextRow.getInt("rsrc_id");
            if (resourceID.intValue() == nextResourceID)
            {
               endDate = nextRow.getDate("start_date").minusMinutes(1);
            }
         }

         if (startDate == null || startDate.isBefore(LocalDateTimeHelper.START_DATE_NA))
         {
            startDate = LocalDateTimeHelper.START_DATE_NA;
         }

         if (endDate == null || endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
         {
            endDate = LocalDateTimeHelper.END_DATE_NA;
         }

         resource.getCostRateTable(0).add(new CostRateTableEntry(startDate, endDate, costPerUse, period, values));
         resource.getAvailability().add(new Availability(startDate, endDate, maxUnits));
      }
   }

   /**
    * Process role code assignments.
    *
    * @param assignments role code assignments
    */
   protected void processRoleCodeAssignments(List<Row> assignments)
   {
      for (Row row : assignments)
      {
         Integer resourceID = row.getInteger("role_id");
         List<Row> list = m_roleCodeAssignments.computeIfAbsent(resourceID, k -> new ArrayList<>());
         list.add(row);
      }
   }

   /**
    * Process roles.
    *
    * @param rows resource data
    */
   protected void processRoles(List<Row> rows)
   {
      for (Row row : rows)
      {
         Resource resource = m_state.getContext().getResources().add();
         TableReaderHelper.processFields(m_state.getRoleFields(), row, resource);
         resource.setRole(true);
         resource.setUniqueID(m_state.getRoleClashMap().addID(resource.getUniqueID()));
         resource.setNotesObject(NotesHelper.getNotes(resource.getNotes()));

         populateRoleCodeValues(resource);
      }
   }

   /**
    * Read details of any role codes assigned to this role.
    *
    * @param role parent role
    */
   private void populateRoleCodeValues(Resource role)
   {
      List<Row> list = m_roleCodeAssignments.get(role.getUniqueID());
      if (list == null)
      {
         return;
      }

      for (Row row : list)
      {
         RoleCode code = m_state.getContext().getRoleCodes().getByUniqueID(row.getInteger("role_catg_type_id"));
         if (code == null)
         {
            continue;
         }

         RoleCodeValue value = code.getValueByUniqueID(row.getInteger("role_catg_id"));
         if (value != null)
         {
            role.addRoleCodeValue(value);
         }
      }
   }

   /**
    * Process role rates.
    *
    * @param rows role rate data
    */
   protected void processRoleRates(List<Row> rows)
   {
      sortRoleTableRows(rows);

      Resource resource = null;

      for (int i = 0; i < rows.size(); ++i)
      {
         Row row = rows.get(i);

         Integer resourceID = m_state.getRoleClashMap().getID(row.getInteger("role_id"));
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_state.getContext().getResources().getByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getCostRateTable(0).clear();
         }

         Rate[] values = new Rate[]
         {
            Rate.valueOf(row.getDouble("cost_per_qty"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty2"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty3"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty4"), TimeUnit.HOURS),
            Rate.valueOf(row.getDouble("cost_per_qty5"), TimeUnit.HOURS),
         };

         Double costPerUse = NumberHelper.getDouble(0.0);
         LocalDateTime startDate = row.getDate("start_date");
         LocalDateTime endDate = LocalDateTimeHelper.END_DATE_NA;

         if (i + 1 < rows.size())
         {
            Row nextRow = rows.get(i + 1);
            if (NumberHelper.equals(row.getInteger("role_id"), nextRow.getInteger("role_id")))
            {
               endDate = nextRow.getDate("start_date").minusMinutes(1);
            }
         }

         if (startDate == null || startDate.isBefore(LocalDateTimeHelper.START_DATE_NA))
         {
            startDate = LocalDateTimeHelper.START_DATE_NA;
         }

         if (endDate == null || endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
         {
            endDate = LocalDateTimeHelper.END_DATE_NA;
         }

         resource.getCostRateTable(0).add(new CostRateTableEntry(startDate, endDate, costPerUse, values));
      }
   }

   /**
    * Process role availability.
    *
    * @param rows role availability data
    */
   protected void processRoleAvailability(List<Row> rows)
   {
      sortRoleTableRows(rows);

      Resource resource = null;

      for (int i = 0; i < rows.size(); ++i)
      {
         Row row = rows.get(i);

         Integer resourceID = m_state.getRoleClashMap().getID(row.getInteger("role_id"));
         if (resource == null || !resource.getUniqueID().equals(resourceID))
         {
            resource = m_state.getContext().getResources().getByUniqueID(resourceID);
            if (resource == null)
            {
               continue;
            }
            resource.getAvailability().clear();
         }

         Double maxUnits = NumberHelper.getDouble(NumberHelper.getDouble(row.getDouble("max_qty_per_hr")) * 100); // adjust to be % as in MS Project
         LocalDateTime startDate = row.getDate("start_date");
         LocalDateTime endDate = LocalDateTimeHelper.END_DATE_NA;

         if (i + 1 < rows.size())
         {
            Row nextRow = rows.get(i + 1);
            if (NumberHelper.equals(row.getInteger("role_id"), nextRow.getInteger("role_id")))
            {
               endDate = nextRow.getDate("start_date").minusMinutes(1);
            }
         }

         if (startDate == null || startDate.isBefore(LocalDateTimeHelper.START_DATE_NA))
         {
            startDate = LocalDateTimeHelper.START_DATE_NA;
         }

         if (endDate == null || endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
         {
            endDate = LocalDateTimeHelper.END_DATE_NA;
         }

         resource.getAvailability().add(new Availability(startDate, endDate, maxUnits));
      }
   }

   /**
    * Primavera defines role tables by role and start dates so sort by start date
    * to allow us to determine the end date of each entry.
    *
    * @param rows role table rows
    */
   private void sortRoleTableRows(List<Row> rows)
   {
      //
      rows.sort((r1, r2) -> {
         Integer id1 = r1.getInteger("role_id");
         Integer id2 = r2.getInteger("role_id");
         int cmp = NumberHelper.compare(id1, id2);
         if (cmp != 0)
         {
            return cmp;
         }
         LocalDateTime d1 = r1.getDate("start_date");
         LocalDateTime d2 = r2.getDate("start_date");
         return LocalDateTimeHelper.compare(d1, d2);
      });
   }

   protected TableReaderState m_state;
   private final DateTimeFormatter m_twentyFourHourTimeFormat = DateTimeFormatter.ofPattern("H:mm");
   private final DateTimeFormatter m_twelveHourTimeFormat = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mm a").toFormatter();

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
      map.put(ResourceField.UNIT_OF_MEASURE_UNIQUE_ID, "unit_id");
      map.put(ResourceField.SHIFT_UNIQUE_ID, "shift_id");
      map.put(ResourceField.PRIMARY_ROLE_UNIQUE_ID, "role_id");
      map.put(ResourceField.CURRENCY_UNIQUE_ID, "curr_id");

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

   private final Map<Integer, List<Row>> m_resourceCodeAssignments = new HashMap<>();
   private final Map<Integer, List<Row>> m_roleCodeAssignments = new HashMap<>();

   static final LocalDate EXCEPTION_EPOCH = LocalDate.of(1899, 12, 30);
}
