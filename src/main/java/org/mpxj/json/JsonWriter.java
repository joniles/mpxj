/*
 * file:       JsonWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       18/02/2015
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

package org.mpxj.json;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.mpxj.Availability;
import org.mpxj.Code;
import org.mpxj.CodeValue;
import org.mpxj.Column;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Currency;
import org.mpxj.CustomFieldLookupTable;
import org.mpxj.CustomFieldValueMask;
import org.mpxj.GenericCriteria;
import org.mpxj.GraphicalIndicator;
import org.mpxj.GraphicalIndicatorCriteria;
import org.mpxj.SkillLevel;
import org.mpxj.UnitOfMeasure;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.ExpenseItem;
import org.mpxj.ProjectCalendarDays;
import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeValue;
import org.mpxj.AssignmentField;
import org.mpxj.CustomField;
import org.mpxj.DataType;
import org.mpxj.LocalDateTimeRange;
import java.time.DayOfWeek;
import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.EarnedValueMethod;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.RecurringData;
import org.mpxj.Step;
import org.mpxj.Table;
import org.mpxj.TaskMode;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnitDefaultsContainer;
import org.mpxj.Priority;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.ResourceRequestType;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TaskType;
import org.mpxj.TimeUnit;
import org.mpxj.UserDefinedField;
import org.mpxj.View;
import org.mpxj.WorkContour;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.mpp.CustomFieldValueItem;
import org.mpxj.mpp.GanttBarStyle;
import org.mpxj.mpp.GanttBarStyleException;
import org.mpxj.mpp.GanttChartView;
import org.mpxj.mpp.TableFontStyle;
import org.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new JSON file from the contents of
 * a ProjectFile instance.
 */
public final class JsonWriter extends AbstractProjectWriter
{
   /**
    * Retrieve the pretty-print flag.
    *
    * @return true if pretty printing is enabled
    */
   public boolean getPretty()
   {
      return m_pretty;
   }

   /**
    * Set the pretty-print flag.
    *
    * @param pretty true if pretty printing is enabled
    */
   public void setPretty(boolean pretty)
   {
      m_pretty = pretty;
   }

   /**
    * Retrieve a flag indicating if layout data should be included in the output.
    * Defaults to false.
    *
    * @return true if layout data is included
    */
   public boolean getIncludeLayoutData()
   {
      return m_includeLayoutData;
   }

   /**
    * Set a flag indicating if layout data should be included in the output.
    *
    * @param includeLayoutData true if layout data is included
    */
   public void setIncludeLayoutData(boolean includeLayoutData)
   {
      m_includeLayoutData = includeLayoutData;
   }

   /**
    * Retrieve the charset to used when writing the JSON file.
    *
    * @return charset
    */
   public Charset getCharset()
   {
      return m_charset;
   }

   /**
    * Set the charset to used when writing the JSON file.
    *
    * @param charset charset to use
    */
   public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   /**
    * Returns true of attribute type information is written to the JSON file.
    *
    * @return true if attribute types written
    */
   public boolean getWriteAttributeTypes()
   {
      return m_writeAttributeTypes;
   }

   /**
    * Sets the flag used to determine if attribute types are written to the JSON file.
    *
    * @param writeAttributeTypes set to true to write attribute types
    */
   public void setWriteAttributeTypes(boolean writeAttributeTypes)
   {
      m_writeAttributeTypes = writeAttributeTypes;
   }

   /**
    * Set the time units to use for durations. Defaults to seconds.
    *
    * @param value time units
    */
   public void setTimeUnits(TimeUnit value)
   {
      m_timeUnits = value;
   }

   /**
    * Retrieve the time units used for durations. null indicates
    * durations will be written in seconds.
    *
    * @return time units
    */
   public TimeUnit getTimeUnits()
   {
      return m_timeUnits;
   }

   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         m_projectFile = projectFile;

         JsonFactory factory = new JsonFactory();
         m_writer = factory.createGenerator(stream);
         if (m_pretty)
         {
            m_writer.setPrettyPrinter(new DefaultPrettyPrinter());
         }

         m_writer.writeStartObject();
         writeProperties();
         writeCurrencies();
         writeUserDefinedFields();
         writeCustomFields();
         writeWorkContours();
         writeCodes("project_codes", m_projectFile.getProjectCodes());
         writeCodes("resource_codes", m_projectFile.getResourceCodes());
         writeCodes("role_codes", m_projectFile.getRoleCodes());
         writeCodes("resource_assignment_codes", m_projectFile.getResourceAssignmentCodes());
         writeActivityCodes();
         writeUnitsOfMeasure();
         writeCalendars();
         writeResources();
         writeTasks();
         writeAssignments();

         if (m_includeLayoutData)
         {
            writeTables();
            writeViews();
         }

         m_writer.writeEndObject();

         m_writer.flush();
      }

      finally
      {
         m_projectFile = null;
      }
   }

   /**
    * Write a list of user defined fields.
    */
   private void writeUserDefinedFields() throws IOException
   {
      List<UserDefinedField> sortedFieldsList = m_projectFile.getUserDefinedFields().stream().sorted(Comparator.comparing(FieldTypeHelper::getFieldID)).collect(Collectors.toList());
      if (sortedFieldsList.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("user_defined_fields");
      for (UserDefinedField field : sortedFieldsList)
      {
         writeUserDefinedField(field);
      }
      m_writer.writeEndArray();
   }

   /**
    * Write a list of custom fields.
    */
   private void writeCustomFields() throws IOException
   {
      List<CustomField> sortedFieldsList = m_projectFile.getCustomFields().stream().filter(f -> f.getFieldType() != null).sorted().collect(Collectors.toList());
      if (sortedFieldsList.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("custom_fields");
      for (CustomField field : sortedFieldsList)
      {
         writeCustomField(field);
      }
      m_writer.writeEndArray();
   }

   /**
    * Write a list of work contours.
    */
   private void writeWorkContours() throws IOException
   {
      List<WorkContour> contours = m_projectFile.getWorkContours().stream().filter(w -> !w.isContourFlat()).sorted(Comparator.comparing(WorkContour::getUniqueID)).collect(Collectors.toList());
      if (contours.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("work_contours");
      for (WorkContour contour : contours)
      {
         m_writer.writeStartObject();
         writeIntegerField("unique_id", contour.getUniqueID());
         writeOptionalStringField("name", contour.getName());
         writeBooleanField("default", Boolean.valueOf(contour.isContourDefault()));
         if (contour.getCurveValues() != null)
         {
            writeDoubleList("curve_values", Arrays.stream(contour.getCurveValues()).boxed().collect(Collectors.toList()));
         }
         m_writer.writeEndObject();
      }

      m_writer.writeEndArray();
   }

   /**
    * Write a list of activity codes.
    */
   private void writeActivityCodes() throws IOException
   {
      if (m_projectFile.getActivityCodes().isEmpty())
      {
         return;
      }

      List<ActivityCode> sortedActivityCodeList = new ArrayList<>(m_projectFile.getActivityCodes());
      sortedActivityCodeList.sort(Comparator.comparing(ActivityCode::getName));

      m_writer.writeArrayFieldStart("activity_codes");
      for (ActivityCode code : sortedActivityCodeList)
      {
         writeActivityCode(code);
      }
      m_writer.writeEndArray();
   }

   /**
    * Write a list of codes.
    *
    * @param attributeName attribute name
    * @param codes list of codes
    */
   private void writeCodes(String attributeName, List<? extends Code> codes) throws IOException
   {
      if (codes.isEmpty())
      {
         return;
      }

      List<? extends Code> sortedCodeList = new ArrayList<>(codes);
      sortedCodeList.sort(Comparator.comparing(Code::getName));
      m_writer.writeArrayFieldStart(attributeName);
      for (Code code : sortedCodeList)
      {
         writeCode(code);
      }
      m_writer.writeEndArray();
   }

   /**
    * Write attributes for an individual custom field.
    * Note that at present we are only writing a subset of the
    * available data... in this instance the field alias.
    * If the field does not have an alias we won't write an
    * entry.
    *
    * @param field field to write
    */
   private void writeCustomField(CustomField field) throws IOException
   {
      if (field.getAlias() == null)
      {
         return;
      }

      m_writer.writeStartObject();

      Integer uniqueID = field.getUniqueID();
      if (uniqueID.intValue() != FieldTypeHelper.getFieldID(field.getFieldType()))
      {
         // Only write this attribute is we have a non-default value
         m_writer.writeNumberField("unique_id", field.getUniqueID().intValue());
      }

      writeFieldType("", field.getFieldType());
      writeStringField("field_alias", field.getAlias());
      writeGraphicalIndicator(field.getGraphicalIndicator());
      writeLookupTable(field);
      writeCustomFieldValueMasks(field);
      m_writer.writeEndObject();
   }

   /**
    * Writes a lookup table for a custom field.
    *
    * @param field custom field
    */
   private void writeLookupTable(CustomField field) throws IOException
   {
      CustomFieldLookupTable table = field.getLookupTable();
      if (table.isEmpty())
      {
         return;
      }

      m_writer.writeObjectFieldStart("lookup_table");
      writeOptionalStringField("guid", table.getGUID());
      writeBooleanField("enterprise", Boolean.valueOf(table.getEnterprise()));
      writeBooleanField("show_indent", Boolean.valueOf(table.getShowIndent()));
      writeBooleanField("resource_substitution_enabled", Boolean.valueOf(table.getResourceSubstitutionEnabled()));
      writeBooleanField("leaf_only", Boolean.valueOf(table.getLeafOnly()));
      writeBooleanField("all_levels_required", Boolean.valueOf(table.getAllLevelsRequired()));
      writeBooleanField("only_table_values_allowed", Boolean.valueOf(table.getOnlyTableValuesAllowed()));

      m_writer.writeArrayFieldStart("values");
      for (CustomFieldValueItem item : table)
      {
         writeCustomFieldValueItem(item);
      }
      m_writer.writeEndArray();
      m_writer.writeEndObject();
   }

   /**
    * Writes a single value from a lookup table.
    *
    * @param item lookup table value.
    */
   private void writeCustomFieldValueItem(CustomFieldValueItem item) throws IOException
   {
      m_writer.writeStartObject();
      writeIntegerField("unique_id", item.getUniqueID());
      writeOptionalStringField("guid", item.getGUID());
      writeOptionalStringField("value", item.getValue());
      writeOptionalStringField("description", item.getDescription());
      writeIntegerField("parent_unique_id", item.getParentUniqueID());
      writeOptionalStringField("type", item.getType() == null ? null : item.getType().name());
      writeBooleanField("collapsed", Boolean.valueOf(item.getCollapsed()));
      m_writer.writeEndObject();
   }

   /**
    * Writes a graphical indicator definition.
    *
    * @param indicator graphical indicator.
    */
   private void writeGraphicalIndicator(GraphicalIndicator indicator) throws IOException
   {
      if (!indicator.getDisplayGraphicalIndicators())
      {
         return;
      }

      m_writer.writeObjectFieldStart("graphical_indicator");
      writeBooleanField("summary_rows_inherit_from_non_summary_rows", Boolean.valueOf(indicator.getSummaryRowsInheritFromNonSummaryRows()));
      writeBooleanField("project_summary_inherits_from_summary_rows", Boolean.valueOf(indicator.getProjectSummaryInheritsFromSummaryRows()));
      writeBooleanField("show_data_values_in_tooltips", Boolean.valueOf(indicator.getShowDataValuesInToolTips()));
      writeGraphicalIndicatorCriteria("project_summary_criteria", indicator.getProjectSummaryCriteria());
      writeGraphicalIndicatorCriteria("summary_row_criteria", indicator.getSummaryRowCriteria());
      writeGraphicalIndicatorCriteria("non_sumary_row_criteria", indicator.getNonSummaryRowCriteria());
      m_writer.writeEndObject();
   }

   /**
    * Writes the graphical indicator criteria.
    *
    * @param name criteria name
    * @param list list of criteria items
    */
   private void writeGraphicalIndicatorCriteria(String name, List<GraphicalIndicatorCriteria> list) throws IOException
   {
      if (list.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart(name);
      for (GraphicalIndicatorCriteria criteria : list)
      {
         m_writer.writeStartObject();
         writeIntegerField("indicator", Integer.valueOf(criteria.getIndicator()));
         writeGenericCriteriaAttributes(criteria);
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Write generic criteria attributes.
    *
    * @param criteria criteria item
    */
   private void writeGenericCriteriaAttributes(GenericCriteria criteria) throws IOException
   {
      writeOptionalStringField("left_value", criteria.getLeftValue().name());
      writeOptionalStringField("operator", criteria.getOperator().name());
      writeOptionalStringField("right_value_1", criteria.getValue(0));
      writeOptionalStringField("right_value_2", criteria.getValue(1));

      List<GenericCriteria> list = criteria.getCriteriaList();
      if (list.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("criteria_list");
      for (GenericCriteria child : list)
      {
         m_writer.writeStartObject();
         writeGenericCriteriaAttributes(child);
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Write the value masks for a custom field.
    *
    * @param field custom field
    */
   private void writeCustomFieldValueMasks(CustomField field) throws IOException
   {
      if (field.getMasks().isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("masks");
      for (CustomFieldValueMask mask : field.getMasks())
      {
         m_writer.writeStartObject();
         writeIntegerField("length", Integer.valueOf(mask.getLength()));
         writeIntegerField("level", Integer.valueOf(mask.getLevel()));
         writeOptionalStringField("separator", mask.getSeparator());
         writeOptionalStringField("type", mask.getType().name());
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Write an individual user defined field.
    *
    * @param field user defined field
    */
   private void writeUserDefinedField(UserDefinedField field) throws IOException
   {
      m_writer.writeStartObject();
      writeIntegerField("unique_id", Integer.valueOf(FieldTypeHelper.getFieldID(field)));
      writeOptionalStringField("field_type_class", field.getFieldTypeClass().toString().toLowerCase());
      writeBooleanField("summary_task_only", Boolean.valueOf(field.getSummaryTaskOnly()));
      writeOptionalStringField("data_type", field.getDataType().toString().toLowerCase());
      writeOptionalStringField("internal_name", field.name().toLowerCase());
      writeOptionalStringField("external_name", field.getName());
      m_writer.writeEndObject();
   }

   /**
    * Write units of measure.
    */
   private void writeUnitsOfMeasure() throws IOException
   {
      if (m_projectFile.getUnitsOfMeasure().isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("units_of_measure");
      for (UnitOfMeasure uom : m_projectFile.getUnitsOfMeasure())
      {
         writeUnitOfMeasure(uom);
      }
      m_writer.writeEndArray();
   }

   /**
    * Write an individual unit of measure.
    *
    * @param uom unit of measure
    */
   private void writeUnitOfMeasure(UnitOfMeasure uom) throws IOException
   {
      m_writer.writeStartObject();
      writeMandatoryIntegerField("unique_id", uom.getUniqueID());
      writeOptionalStringField("abbreviation", uom.getAbbreviation());
      writeOptionalStringField("name", uom.getName());
      writeMandatoryIntegerField("sequence_number", uom.getSequenceNumber());
      m_writer.writeEndObject();
   }

   /**
    * Write currencies.
    */
   private void writeCurrencies() throws IOException
   {
      if (m_projectFile.getCurrencies().isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("currencies");
      for (Currency currency : m_projectFile.getCurrencies())
      {
         writeCurrency(currency);
      }
      m_writer.writeEndArray();
   }

   /**
    * Write an individual currency.
    *
    * @param currency currency
    */
   private void writeCurrency(Currency currency) throws IOException
   {
      m_writer.writeStartObject();
      writeMandatoryIntegerField("unique_id", currency.getUniqueID());
      writeOptionalStringField("currency_id", currency.getCurrencyID());
      writeOptionalStringField("name", currency.getName());
      writeOptionalStringField("symbol", currency.getSymbol());
      writeDoubleField("exchange_rate", currency.getExchangeRate());
      writeOptionalStringField("decimal_symbol", currency.getDecimalSymbol());
      writeMandatoryIntegerField("number_of_decimal_places", currency.getNumberOfDecimalPlaces());
      writeOptionalStringField("digit_grouping_symbol", currency.getDigitGroupingSymbol());
      writeOptionalStringField("positive_currency_format", currency.getPositiveCurrencyFormat());
      writeOptionalStringField("negative_currency_format", currency.getNegativeCurrencyFormat());
      m_writer.writeEndObject();
   }

   /**
    * This method writes project property data to a JSON file.
    */
   private void writeProperties() throws IOException
   {
      writeAttributeTypes("property_types", ProjectField.values());

      m_writer.writeObjectFieldStart("property_values");
      writeFields(m_projectFile.getProjectProperties(), ProjectField.values());
      writeFields(m_projectFile.getProjectProperties(), m_projectFile.getUserDefinedFields().getProjectFields().toArray(new FieldType[0]));
      m_writer.writeEndObject();
   }

   /**
    * This method writes resource data to a JSON file.
    */
   private void writeResources() throws IOException
   {
      writeAttributeTypes("resource_types", ResourceField.values());

      m_writer.writeArrayFieldStart("resources");
      for (Resource resource : m_projectFile.getResources())
      {
         m_writer.writeStartObject();
         writeFields(resource, ResourceField.values());
         writeFields(resource, m_projectFile.getUserDefinedFields().getResourceFields().toArray(new FieldType[0]));
         writeCostRateTables(resource);
         writeAvailabilityTable(resource);
         writeRoleAssignments(resource);
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Writes calendar data to a JSON file.
    */
   private void writeCalendars() throws IOException
   {
      m_writer.writeArrayFieldStart("calendars");
      for (ProjectCalendar calendar : m_projectFile.getCalendars())
      {
         writeCalendar(calendar);
      }
      m_writer.writeEndArray();
   }

   /**
    * Writes an individual calendar to a JSON file.
    *
    * @param calendar calendar to write
    */
   private void writeCalendar(ProjectCalendar calendar) throws IOException
   {
      m_writer.writeStartObject();
      writeMandatoryIntegerField("unique_id", calendar.getUniqueID());
      writeOptionalStringField("guid", calendar.getGUID());
      writeMandatoryIntegerField("parent_unique_id", calendar.getParentUniqueID());
      writeOptionalStringField("name", calendar.getName());
      writeOptionalStringField("type", calendar.getType().toString());
      writeBooleanField("personal", Boolean.valueOf(calendar.getPersonal()));
      writeIntegerField("minutes_per_day", calendar.getCalendarMinutesPerDay());
      writeIntegerField("minutes_per_week", calendar.getCalendarMinutesPerWeek());
      writeIntegerField("minutes_per_month", calendar.getCalendarMinutesPerMonth());
      writeIntegerField("minutes_per_year", calendar.getCalendarMinutesPerYear());
      writeCalendarDays(calendar);
      writeCalendarWeeks(calendar);
      writeCalendarExceptions(calendar);
      m_writer.writeEndObject();
   }

   /**
    * Write any working week definitions.
    *
    * @param calendar calendar to write
    */
   private void writeCalendarWeeks(ProjectCalendar calendar) throws IOException
   {
      if (!calendar.getWorkWeeks().isEmpty())
      {
         m_writer.writeArrayFieldStart("working_weeks");
         for (ProjectCalendarWeek week : calendar.getWorkWeeks())
         {
            writeCalendarWeek(week);
         }
         m_writer.writeEndArray();
      }
   }

   /**
    * Write an individual working week definition.
    *
    * @param week working week definition
    */
   private void writeCalendarWeek(ProjectCalendarWeek week) throws IOException
   {
      m_writer.writeStartObject();
      writeOptionalStringField("name", week.getName());
      writeDateField("effective_from", week.getDateRange().getStart());
      writeDateField("effective_to", week.getDateRange().getEnd());
      writeCalendarDays(week);
      m_writer.writeEndObject();
   }

   /**
    * Write day definitions for a working week.
    *
    * @param week working week
    */
   private void writeCalendarDays(ProjectCalendarDays week) throws IOException
   {
      for (DayOfWeek day : DayOfWeekHelper.ORDERED_DAYS)
      {
         m_writer.writeObjectFieldStart(day.name().toLowerCase());
         writeOptionalStringField("type", week.getCalendarDayType(day).toString().toLowerCase());
         writeCalendarHours(week.getCalendarHours(day));
         m_writer.writeEndObject();
      }
   }

   /**
    * Write working hours definition.
    *
    * @param hours working hours
    */
   private void writeCalendarHours(ProjectCalendarHours hours) throws IOException
   {
      if (hours != null && !hours.isEmpty())
      {
         m_writer.writeArrayFieldStart("hours");
         for (LocalTimeRange range : hours)
         {
            m_writer.writeStartObject();
            writeTimeField("from", range.getStart());
            writeTimeField("to", range.getEnd());
            m_writer.writeEndObject();
         }
         m_writer.writeEndArray();
      }
   }

   private void writeCalendarExceptions(ProjectCalendar calendar) throws IOException
   {
      if (!calendar.getCalendarExceptions().isEmpty())
      {
         m_writer.writeArrayFieldStart("exceptions");
         for (ProjectCalendarException ex : calendar.getCalendarExceptions())
         {
            writeCalendarException(ex);
         }
         m_writer.writeEndArray();
      }
   }

   /**
    * Write a calendar exception.
    *
    * @param ex calendar exception
    */
   private void writeCalendarException(ProjectCalendarException ex) throws IOException
   {
      m_writer.writeStartObject();
      writeCalendarExceptionDetails(ex);
      writeCalendarHours(ex);
      writeRecurringData(ex.getRecurring());
      m_writer.writeEndObject();
   }

   /**
    * Write basic header details for a calendar exception.
    *
    * @param ex calendar exception
    */
   private void writeCalendarExceptionDetails(ProjectCalendarException ex) throws IOException
   {
      DayType type = ex.getWorking() ? DayType.WORKING : DayType.NON_WORKING;
      writeOptionalStringField("name", ex.getName());
      if (ex.getRecurring() == null)
      {
         writeDateField("from", ex.getFromDate());
         writeDateField("to", ex.getToDate());
      }
      writeOptionalStringField("type", type.toString().toLowerCase());
   }

   /**
    * Write recurring data.
    *
    * @param data recurring data
    */
   private void writeRecurringData(RecurringData data) throws IOException
   {
      if (data != null)
      {
         m_writer.writeObjectFieldStart("recurrence");
         writeOptionalStringField("type", data.getRecurrenceType().toString().toLowerCase());
         writeDateField("start_date", data.getStartDate());
         writeDateField("finish_date", data.getFinishDate());
         writeIntegerField("occurrences", data.getOccurrences());
         writeIntegerField("frequency", data.getFrequency());
         writeBooleanField("relative", Boolean.valueOf(data.getRelative()));
         writeIntegerField("day_number", data.getDayNumber());
         writeIntegerField("month_number", data.getMonthNumber());
         writeBooleanField("use_end_date", Boolean.valueOf(data.getUseEndDate()));

         List<String> weeklyDays = Arrays.stream(DayOfWeekHelper.ORDERED_DAYS).filter(data::getWeeklyDay).map(d -> d.toString().toLowerCase()).collect(Collectors.toList());
         if (!weeklyDays.isEmpty())
         {
            writeStringList("weekly_days", weeklyDays);
         }
         m_writer.writeEndObject();
      }
   }

   /**
    * This method writes task data to a JSON file.
    * Note that we write the task hierarchy in order to make rebuilding the hierarchy easier.
    */
   private void writeTasks() throws IOException
   {
      writeAttributeTypes("task_types", TaskField.values());

      m_writer.writeArrayFieldStart("tasks");
      for (Task task : m_projectFile.getChildTasks())
      {
         writeTask(task);
      }
      m_writer.writeEndArray();
   }

   /**
    * This method is called recursively to write a task and its child tasks
    * to the JSON file.
    *
    * @param task task to write
    */
   private void writeTask(Task task) throws IOException
   {
      m_writer.writeStartObject();
      writeFields(task, TaskField.values());
      writeFields(task, m_projectFile.getUserDefinedFields().getTaskFields().toArray(new FieldType[0]));
      m_writer.writeEndObject();
      for (Task child : task.getChildTasks())
      {
         writeTask(child);
      }
   }

   /**
    * This method writes assignment data to a JSON file.
    */
   private void writeAssignments() throws IOException
   {
      writeAttributeTypes("assignment_types", AssignmentField.values());

      m_writer.writeArrayFieldStart("assignments");
      for (ResourceAssignment assignment : m_projectFile.getResourceAssignments())
      {
         m_writer.writeStartObject();
         writeFields(assignment, AssignmentField.values());
         writeFields(assignment, m_projectFile.getUserDefinedFields().getAssignmentFields().toArray(new FieldType[0]));
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Generates a mapping between attribute names and data types.
    *
    * @param name name of the map
    * @param types types to write
    */
   private void writeAttributeTypes(String name, FieldType[] types) throws IOException
   {
      if (m_writeAttributeTypes)
      {
         m_writer.writeObjectFieldStart(name);
         m_writer.writeStartObject();
         for (FieldType field : types)
         {
            m_writer.writeNumberField(field.name().toLowerCase(), field.getDataType().getValue());
         }
         m_writer.writeEndObject();
      }
   }

   /**
    * Write cost rate tables for a resource.
    *
    * @param resource parent resource
    */
   private void writeCostRateTables(Resource resource) throws IOException
   {
      boolean tablesArePopulated = false;
      for (int index = 0; index < CostRateTable.MAX_TABLES; index++)
      {
         tablesArePopulated = resource.getCostRateTable(index).tableIsPopulated();
         if (tablesArePopulated)
         {
            break;
         }
      }

      if (tablesArePopulated)
      {
         m_writer.writeObjectFieldStart("cost_rate_tables");
         for (int index = 0; index < CostRateTable.MAX_TABLES; index++)
         {
            writeCostRateTable(index, resource.getCostRateTable(index));
         }
         m_writer.writeEndObject();
      }
   }

   /**
    * Write the availability table for a resource.
    *
    * @param resource parent resource
    */
   private void writeAvailabilityTable(Resource resource) throws IOException
   {
      List<Availability> availability = resource.getAvailability();
      if (availability.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("availability_table");
      for (Availability entry : availability)
      {
         m_writer.writeStartObject();
         writeTimestampField("start", entry.getRange().getStart());
         writeTimestampField("end", entry.getRange().getEnd());
         writeDoubleField("units", entry.getUnits());
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Write the role assignments for a resource.
    *
    * @param resource resource
    */
   private void writeRoleAssignments(Resource resource) throws IOException
   {
      Map<Resource, SkillLevel> map = resource.getRoleAssignments();
      if (map.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("role_assignments");
      for (Map.Entry<Resource, SkillLevel> entry : map.entrySet().stream().sorted(Comparator.comparing(o -> o.getKey().getUniqueID())).collect(Collectors.toList()))
      {
         m_writer.writeStartObject();
         writeIntegerField("resource_id", entry.getKey().getUniqueID());
         writeOptionalStringField("skill_level", entry.getValue());
         m_writer.writeEndObject();
      }

      m_writer.writeEndArray();
   }

   /**
    * Write a cost rate table.
    *
    * @param index index of this table
    * @param table cost rate table to write
    */
   private void writeCostRateTable(int index, CostRateTable table) throws IOException
   {
      if (table.tableIsPopulated())
      {
         m_writer.writeArrayFieldStart(Integer.toString(index));
         for (CostRateTableEntry entry : table)
         {
            LocalDateTime startDate = entry.getStartDate();
            if (startDate != null && LocalDateTimeHelper.compare(startDate, LocalDateTimeHelper.START_DATE_NA) <= 0)
            {
               startDate = null;
            }

            LocalDateTime endDate = entry.getEndDate();
            if (endDate != null && LocalDateTimeHelper.compare(LocalDateTimeHelper.END_DATE_NA, endDate) <= 0)
            {
               endDate = null;
            }

            m_writer.writeStartObject();
            writeTimestampField("start_date", startDate);
            writeTimestampField("end_date", endDate);
            writeDoubleField("cost_per_use", entry.getCostPerUse());

            m_writer.writeObjectFieldStart("rates");
            for (int rateIndex = 0; rateIndex < CostRateTableEntry.MAX_RATES; rateIndex++)
            {
               writeCostRate(rateIndex, entry.getRate(rateIndex));
            }
            m_writer.writeEndObject();

            m_writer.writeEndObject();
         }
         m_writer.writeEndArray();
      }
   }

   /**
    * Write a cost rate.
    *
    * @param index cost rate number
    * @param rate rate value
    */
   private void writeCostRate(int index, Rate rate) throws IOException
   {
      if (rate != null && rate.getAmount() != 0.0)
      {
         writeRateField(Integer.toString(index), rate);
      }
   }

   /**
    * Write a set of fields from a field container to a JSON file.
    *
    * @param container field container
    * @param fields fields to write
    */
   private void writeFields(FieldContainer container, FieldType[] fields) throws IOException
   {
      for (FieldType field : fields)
      {
         Object value = container.get(field);
         if (value != null)
         {
            writeField(container, field, value);
         }
      }
   }

   /**
    * Write the appropriate data for a field to the JSON file based on its type.
    *
    * @param container field container
    * @param field field type
    * @param value field value
    */
   private void writeField(FieldContainer container, FieldType field, Object value) throws IOException
   {
      if (!IGNORED_FIELDS.contains(field))
      {
         writeField(container, field, field.name().toLowerCase(), field.getDataType(), value);
      }
   }

   /**
    * Write the appropriate data for a field to the JSON file based on its type.
    *
    * @param container field container
    * @param fieldType field type
    * @param fieldName field name
    * @param dataType field type
    * @param value field value
    */
   private void writeField(FieldContainer container, FieldType fieldType, String fieldName, DataType dataType, Object value) throws IOException
   {
      switch (dataType)
      {
         case SHORT:
         case INTEGER:
         {
            writeIntegerField(fieldType, fieldName, value);
            break;
         }

         case PERCENTAGE:
         case CURRENCY:
         case NUMERIC:
         case UNITS:
         {
            writeDoubleField(fieldName, value);
            break;
         }

         case BOOLEAN:
         {
            writeBooleanField(fieldName, value);
            break;
         }

         case DELAY:
         case WORK:
         case DURATION:
         {
            writeDurationField(container, fieldName, value);
            break;
         }

         case DATE:
         {
            writeTimestampField(fieldName, value);
            break;
         }

         case TIME:
         {
            writeTimeField(fieldName, value);
            break;
         }

         case RATE_UNITS:
         case TIME_UNITS:
         {
            writeTimeUnitsField(fieldName, value);
            break;
         }

         case PRIORITY:
         {
            writePriorityField(fieldName, value);
            break;
         }

         case RELATION_LIST:
         {
            writeRelationList(fieldName, value);
            break;
         }

         case MAP:
         {
            writeMap(fieldName, value);
            break;
         }

         case DATE_RANGE_LIST:
         {
            writeDateRangeList(fieldName, value);
            break;
         }

         case RATE:
         {
            writeRateField(fieldName, value);
            break;
         }

         case RESOURCE_REQUEST_TYPE:
         {
            writeResourceRequestTypeField(fieldName, value);
            break;
         }

         case WORK_CONTOUR:
         {
            writeWorkContourField(fieldName, value);
            break;
         }

         case EARNED_VALUE_METHOD:
         {
            writeEarnedValueMethodField(container, fieldName, value);
            break;
         }

         case TASK_TYPE:
         {
            writeTaskTypeField(container, fieldName, value);
            break;
         }

         case ACTIVITY_CODE_VALUES:
         case CODE_VALUES:
         {
            writeCodeValues(fieldName, value);
            break;
         }

         case TASK_MODE:
         {
            writeTaskModeField(fieldName, value);
            break;
         }

         case BINARY:
         {
            // Don't write binary data
            break;
         }

         case EXPENSE_ITEM_LIST:
         {
            writeExpenseItemList(fieldName, value);
            break;
         }

         case STEP_LIST:
         {
            writeStepList(fieldName, value);
            break;
         }

         default:
         {
            // If we have an enum, ensure we write the name as it appears in the code.
            // We have a various enums which currently override toString to produce
            // file-format-specific values, which is not helpful in the JSON file.
            if (value instanceof Enum)
            {
               value = ((Enum<?>) value).name();
            }

            writeOptionalStringField(fieldName, value);
            break;
         }
      }
   }

   /**
    * Write an integer field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeIntegerField(String fieldName, Object value) throws IOException
   {
      writeIntegerField(null, fieldName, value);
   }

   /**
    * Write an integer field to the JSON file. Always write this field even if it is zero.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeMandatoryIntegerField(String fieldName, Number value) throws IOException
   {
      if (value != null)
      {
         m_writer.writeNumberField(fieldName, value.intValue());
      }
   }

   /**
    * Write an integer field to the JSON file.
    *
    * @param fieldType field type
    * @param fieldName field name
    * @param value field value
    */
   private void writeIntegerField(FieldType fieldType, String fieldName, Object value) throws IOException
   {
      if (!(value instanceof Number))
      {
         return;
      }

      int val = ((Number) value).intValue();
      if (val != 0 || MANDATORY_FIELDS.contains(fieldType))
      {
         m_writer.writeNumberField(fieldName, val);
      }
   }

   /**
    * Write a double field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeDoubleField(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof Number))
      {
         return;
      }

      double val = ((Number) value).doubleValue();
      if (val != 0)
      {
         // Round to 4 decimal places
         val = Math.round(val * 10000.0) / 10000.0;
         m_writer.writeNumberField(fieldName, val);
      }
   }

   /**
    * Write a boolean field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeBooleanField(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof Boolean))
      {
         return;
      }

      boolean val = ((Boolean) value).booleanValue();
      if (val)
      {
         m_writer.writeBooleanField(fieldName, val);
      }
   }

   /**
    * Write a duration field to the JSON file.
    *
    * @param container field container
    * @param fieldName field name
    * @param value field value
    */
   private void writeDurationField(FieldContainer container, String fieldName, Object value) throws IOException
   {
      if (value == null)
      {
         return;
      }

      if (value instanceof String)
      {
         writeStringField(fieldName + "_text", (String) value);
         return;
      }

      if (!(value instanceof Duration))
      {
         return;
      }

      Duration val = (Duration) value;
      if (val.getDuration() != 0)
      {
         // If we have a calendar associated with this container,
         // we'll use any defaults it supplies to handle the time
         // units conversion.
         TimeUnitDefaultsContainer defaults = null;
         if (container instanceof Task)
         {
            defaults = ((Task) container).getEffectiveCalendar();
         }
         else
         {
            if (container instanceof Resource)
            {
               defaults = ((Resource) container).getCalendar();
            }
         }

         if (defaults == null)
         {
            defaults = m_projectFile.getProjectProperties();
         }

         // If a specific TimeUnit hasn't been provided, we default
         // to writing seconds for backward compatibility.
         if (m_timeUnits == null)
         {
            Duration minutes = val.convertUnits(TimeUnit.MINUTES, defaults);
            long seconds = (long) (minutes.getDuration() * 60.0);
            m_writer.writeNumberField(fieldName, seconds);
         }
         else
         {
            TimeUnit targetUnits = m_timeUnits;
            if (val.getUnits().isElapsed())
            {
               // We have an elapsed time unit value. It doesn't make sense
               // to convert this to a "working" units (e.g. elapsed days to days).
               // Instead, we'll select the appropriate elapsed units, so
               // if the caller asks for days, we'll convert the value to elapsed days.
               // TODO: we need to make the original units available as part of the JSON output, and an elapsed flag?
               targetUnits = ELAPSED_TIME_UNIT_MAP.getOrDefault(targetUnits, targetUnits);
            }

            Duration duration = val.convertUnits(targetUnits, defaults);
            m_writer.writeNumberField(fieldName, duration.getDuration());
         }
      }
   }

   /**
    * Write a date field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeTimestampField(String fieldName, Object value) throws IOException
   {
      if (value == null)
      {
         return;
      }

      if (value instanceof String)
      {
         writeStringField(fieldName + "_text", (String) value);
         return;
      }

      if (!(value instanceof LocalDateTime))
      {
         return;
      }

      writeStringField(fieldName, TIMESTAMP_FORMAT.format((LocalDateTime) value));
   }

   /**
    * Write a date field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeDateField(String fieldName, Object value) throws IOException
   {
      if (value instanceof LocalDate)
      {
         writeStringField(fieldName, DATE_FORMAT.format(((LocalDate) value)));
      }
   }

   /**
    * Write a time field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeTimeField(String fieldName, Object value) throws IOException
   {
      if (value instanceof LocalTime)
      {
         writeStringField(fieldName, TIME_FORMAT.format((LocalTime) value));
      }
   }

   /**
    * Write a time units field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeTimeUnitsField(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof TimeUnit))
      {
         return;
      }

      TimeUnit val = (TimeUnit) value;
      if (val != m_projectFile.getProjectProperties().getDefaultDurationUnits())
      {
         writeStringField(fieldName, val.toString());
      }
   }

   /**
    * Write a priority field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writePriorityField(String fieldName, Object value) throws IOException
   {
      if (value instanceof Priority)
      {
         m_writer.writeNumberField(fieldName, ((Priority) value).getValue());
      }
   }

   /**
    * Write a priority field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeRateField(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof Rate))
      {
         return;
      }

      Rate val = (Rate) value;
      if (val.getAmount() != 0.0)
      {
         writeStringField(fieldName, val.getAmount() + "/" + val.getUnits());
      }
   }

   /**
    * Write a map field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeMap(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof Map))
      {
         return;
      }

      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) value;
      if (map.isEmpty())
      {
         return;
      }

      m_writer.writeObjectFieldStart(fieldName);
      for (Map.Entry<String, Object> entry : map.entrySet())
      {
         Object entryValue = entry.getValue();
         if (entryValue != null && !(entryValue instanceof byte[]))
         {
            DataType type = TYPE_MAP.get(entryValue.getClass().getName());
            if (type == null)
            {
               type = DataType.STRING;
               entryValue = entryValue.toString();
            }
            writeField(null, null, entry.getKey(), type, entryValue);
         }
      }
      m_writer.writeEndObject();
   }

   /**
    * Write a list of date ranges to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeDateRangeList(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof List))
      {
         return;
      }

      @SuppressWarnings("unchecked")
      List<LocalDateTimeRange> list = (List<LocalDateTimeRange>) value;
      m_writer.writeArrayFieldStart(fieldName);
      for (LocalDateTimeRange entry : list)
      {
         m_writer.writeStartObject();
         writeTimestampField("start", entry.getStart());
         writeTimestampField("end", entry.getEnd());
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Write an activity code to the JSON file.
    *
    * @param code ActivityCode.
    */
   private void writeActivityCode(ActivityCode code) throws IOException
   {
      m_writer.writeStartObject();

      writeMandatoryIntegerField("unique_id", code.getUniqueID());
      writeOptionalStringField("scope", code.getScope());
      writeIntegerField("scope_eps_unique_id", code.getScopeEpsUniqueID());
      writeIntegerField("scope_project_unique_id", code.getScopeProjectUniqueID());
      writeMandatoryIntegerField("sequence_number", code.getSequenceNumber());
      writeOptionalStringField("name", code.getName());
      if (!code.getValues().isEmpty())
      {
         m_writer.writeArrayFieldStart("values");
         for (ActivityCodeValue value : code.getValues().stream().sorted(Comparator.comparing(ActivityCodeValue::getUniqueID)).collect(Collectors.toList()))
         {
            writeActivityCodeValue(value);
         }
         m_writer.writeEndArray();
      }
      m_writer.writeEndObject();
   }

   /**
    * Write a code to the JSON file.
    *
    * @param code code
    */
   private void writeCode(Code code) throws IOException
   {
      m_writer.writeStartObject();

      writeMandatoryIntegerField("unique_id", code.getUniqueID());
      writeMandatoryIntegerField("sequence_number", code.getSequenceNumber());
      writeOptionalStringField("name", code.getName());
      if (!code.getValues().isEmpty())
      {
         m_writer.writeArrayFieldStart("values");
         for (CodeValue value : code.getValues().stream().sorted(Comparator.comparing(CodeValue::getUniqueID)).collect(Collectors.toList()))
         {
            writeCodeValue(value);
         }
         m_writer.writeEndArray();
      }
      m_writer.writeEndObject();
   }

   /**
    * Write an activity code value to the JSON file.
    *
    * @param value ActivityCodeValue.
    */
   private void writeActivityCodeValue(ActivityCodeValue value) throws IOException
   {
      m_writer.writeStartObject();
      writeMandatoryIntegerField("unique_id", value.getUniqueID());
      writeMandatoryIntegerField("sequence_number", value.getSequenceNumber());
      writeOptionalStringField("name", value.getName());
      writeOptionalStringField("description", value.getDescription());
      writeColorField("color", value.getColor());
      writeIntegerField("parent_value_unique_id", value.getParentValueUniqueID());
      m_writer.writeEndObject();
   }

   /**
    * Write a code value to the JSON file.
    *
    * @param value code value
    */
   private void writeCodeValue(CodeValue value) throws IOException
   {
      m_writer.writeStartObject();
      writeMandatoryIntegerField("unique_id", value.getUniqueID());
      writeMandatoryIntegerField("sequence_number", value.getSequenceNumber());
      writeOptionalStringField("name", value.getName());
      writeOptionalStringField("description", value.getDescription());
      writeIntegerField("parent_value_unique_id", value.getParentValueUniqueID());
      m_writer.writeEndObject();
   }

   /**
    * Write a string field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeOptionalStringField(String fieldName, Object value) throws IOException
   {
      if (value != null)
      {
         String val = value.toString();
         if (!val.isEmpty())
         {
            writeStringField(fieldName, val);
         }
      }
   }

   private void writeStringField(String fieldName, String val) throws IOException
   {
      m_writer.writeStringField(fieldName, stripControlCharacters(val));
   }

   private String stripControlCharacters(String value)
   {
      m_buffer.setLength(0);
      for (int index = 0; index < value.length(); index++)
      {
         char c = value.charAt(index);
         switch (c)
         {
            case '\b':
            case '\f':
            case '\n':
            case '\r':
            case '\t':
            {
               m_buffer.append(c);
               break;
            }

            default:
            {
               if (c > 0x1f)
               {
                  m_buffer.append(c);
               }
            }
         }
      }
      return m_buffer.toString();
   }

   /**
    * Write a relation list field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeRelationList(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof List))
      {
         return;
      }

      @SuppressWarnings("unchecked")
      List<Relation> list = (List<Relation>) value;
      if (!list.isEmpty())
      {
         m_writer.writeArrayFieldStart(fieldName);
         for (Relation relation : list)
         {
            m_writer.writeStartObject();
            writeIntegerField("unique_id", relation.getUniqueID());
            writeIntegerField("predecessor_task_unique_id", relation.getPredecessorTask().getUniqueID());
            writeIntegerField("successor_task_unique_id", relation.getSuccessorTask().getUniqueID());
            writeDurationField(m_projectFile.getProjectProperties(), "lag", relation.getLag());
            if (relation.getLag().getDuration() != 0.0)
            {
               writeTimeUnitsField("lag_units", relation.getLag().getUnits());
            }
            writeOptionalStringField("type", relation.getType());
            writeOptionalStringField("notes", relation.getNotes());
            m_writer.writeEndObject();
         }
         m_writer.writeEndArray();
      }
   }

   /**
    * Write a resource request type field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeResourceRequestTypeField(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof ResourceRequestType))
      {
         return;
      }

      ResourceRequestType type = (ResourceRequestType) value;
      if (type != ResourceRequestType.NONE)
      {
         writeStringField(fieldName, type.name());
      }
   }

   /**
    * Write a work contour field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeWorkContourField(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof WorkContour))
      {
         return;
      }

      WorkContour type = (WorkContour) value;
      if (!type.isContourFlat())
      {
         writeStringField(fieldName, type.toString());
      }
   }

   /**
    * Write an earned value method field to the JSON file.
    *
    * @param container field container
    * @param fieldName field name
    * @param value field value
    */
   private void writeEarnedValueMethodField(FieldContainer container, String fieldName, Object value) throws IOException
   {
      if (!(value instanceof EarnedValueMethod))
      {
         return;
      }

      EarnedValueMethod method = (EarnedValueMethod) value;
      if (container instanceof ProjectProperties || method != m_projectFile.getProjectProperties().getDefaultTaskEarnedValueMethod())
      {
         writeStringField(fieldName, method.name());
      }
   }

   /**
    * Write a task type field to the JSON file.
    *
    * @param container field container
    * @param fieldName field name
    * @param value field value
    */
   private void writeTaskTypeField(FieldContainer container, String fieldName, Object value) throws IOException
   {
      if (!(value instanceof TaskType))
      {
         return;
      }

      TaskType type = (TaskType) value;
      if (container instanceof ProjectProperties || type != m_projectFile.getProjectProperties().getDefaultTaskType())
      {
         writeStringField(fieldName, type.name());
      }
   }

   private void writeColorField(String name, Color value) throws IOException
   {
      if (value != null)
      {
         writeStringField(name, ColorHelper.getHtmlColor(value));
      }
   }

   private void writeCodeValues(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof Map))
      {
         return;
      }

      @SuppressWarnings("unchecked")
      Map<? extends Code, ? extends CodeValue> map = (Map<? extends Code, ? extends CodeValue>) value;
      if (!map.isEmpty())
      {
         writeIntegerList(fieldName, map.values().stream().map(CodeValue::getUniqueID).sorted().collect(Collectors.toList()));
      }
   }

   private void writeExpenseItemList(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof List))
      {
         return;
      }

      @SuppressWarnings("unchecked")
      List<ExpenseItem> list = (List<ExpenseItem>) value;
      if (list.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart(fieldName);
      for (ExpenseItem item : list)
      {
         m_writer.writeStartObject();
         writeIntegerField("unique_id", item.getUniqueID());
         writeOptionalStringField("name", item.getName());
         writeOptionalStringField("description", item.getDescription());
         writeIntegerField("account_unique_id", item.getAccountUniqueID());
         writeIntegerField("category_unique_id", item.getCategoryUniqueID());
         writeOptionalStringField("document_number", item.getDocumentNumber());
         writeOptionalStringField("vendor", item.getVendor());
         writeDoubleField("at_completion_cost", item.getAtCompletionCost());
         writeDoubleField("at_completion_units", item.getAtCompletionUnits());
         writeDoubleField("actual_cost", item.getActualCost());
         writeDoubleField("actual_units", item.getActualUnits());
         writeDoubleField("price_per_unit", item.getPricePerUnit());
         writeDoubleField("remaining_cost", item.getRemainingCost());
         writeDoubleField("remaining_units", item.getRemainingUnits());
         writeDoubleField("planned_cost", item.getPlannedCost());
         writeDoubleField("planned_units", item.getPlannedUnits());
         writeOptionalStringField("accrue_type", item.getAccrueType().name().toLowerCase());
         writeBooleanField("auto_compute_actuals", Boolean.valueOf(item.getAutoComputeActuals()));
         writeOptionalStringField("unit_of_measure", item.getUnitOfMeasure());
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   private void writeStepList(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof List))
      {
         return;
      }

      @SuppressWarnings("unchecked")
      List<Step> list = (List<Step>) value;
      if (list.isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart(fieldName);
      for (Step item : list)
      {
         m_writer.writeStartObject();
         writeIntegerField("unique_id", item.getUniqueID());
         writeOptionalStringField("name", item.getName());
         writeIntegerField("sequence_number", item.getSequenceNumber());
         writeDoubleField("percent_complete", item.getPercentComplete());
         writeDoubleField("weight", item.getWeight());
         writeOptionalStringField("description", item.getDescription());
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   private void writeTables() throws IOException
   {
      if (m_projectFile.getTables().isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("tables");
      for (Table table : m_projectFile.getTables())
      {
         m_writer.writeStartObject();
         writeIntegerField("id", Integer.valueOf(table.getID()));
         writeOptionalStringField("name", table.getName());
         writeBooleanField("resource", Boolean.valueOf(table.getResourceFlag()));
         writeTableColumns(table);
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   private void writeTableColumns(Table table) throws IOException
   {
      if (table.getColumns().isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("columns");
      for (Column column : table.getColumns())
      {
         m_writer.writeStartObject();
         writeFieldType("", column.getFieldType());
         writeOptionalStringField("title", column.getTitle());
         writeIntegerField("width", Integer.valueOf(column.getWidth()));
         writeIntegerField("align_data", Integer.valueOf(column.getAlignData()));
         writeIntegerField("align_title", Integer.valueOf(column.getAlignTitle()));
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   private void writeViews() throws IOException
   {
      if (m_projectFile.getViews().isEmpty())
      {
         return;
      }

      m_writer.writeArrayFieldStart("views");
      for (View view : m_projectFile.getViews())
      {
         m_writer.writeStartObject();
         writeIntegerField("id", view.getID());
         writeOptionalStringField("name", view.getName());
         writeOptionalStringField("type", view.getType().name().toLowerCase());
         writeOptionalStringField("table_name", view.getTableName());
         writeViewTableFontStyles(view);
         writeBarStyles(view);
         writeBarStyleExceptions(view);
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   private void writeViewTableFontStyles(View view) throws IOException
   {
      if (!(view instanceof GanttChartView))
      {
         return;
      }

      GanttChartView ganttChartView = (GanttChartView) view;
      if (ganttChartView.getTableFontStyles() == null)
      {
         return;
      }

      m_writer.writeArrayFieldStart("table_font_styles");
      for (TableFontStyle style : ganttChartView.getTableFontStyles())
      {
         m_writer.writeStartObject();
         writeIntegerField("row_unique_id", Integer.valueOf(style.getRowUniqueID()));
         writeFieldType("", style.getFieldType());
         // TODO: add more of the style attributes as needed
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   private void writeBarStyles(View view) throws IOException
   {
      if (!(view instanceof GanttChartView))
      {
         return;
      }

      GanttBarStyle[] styles = ((GanttChartView) view).getBarStyles();
      if (styles == null || styles.length == 0)
      {
         return;
      }

      m_writer.writeArrayFieldStart("bar_styles");
      for (GanttBarStyle style : styles)
      {
         m_writer.writeStartObject();
         writeIntegerField("id", style.getID());
         writeOptionalStringField("name", style.getName());
         writeIntegerField("row", Integer.valueOf(style.getRow()));
         writeFieldType("from_", style.getFromField());
         writeFieldType("to_", style.getToField());
         writeFieldType("top_", style.getTopText());
         writeFieldType("bottom_", style.getBottomText());
         writeFieldType("left_", style.getLeftText());
         writeFieldType("right_", style.getRightText());
         writeFieldType("inside_", style.getInsideText());
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   private void writeBarStyleExceptions(View view) throws IOException
   {
      if (!(view instanceof GanttChartView))
      {
         return;
      }

      GanttBarStyleException[] exceptions = ((GanttChartView) view).getBarStyleExceptions();
      if (exceptions == null || exceptions.length == 0)
      {
         return;
      }

      m_writer.writeArrayFieldStart("bar_style_exceptions");
      for (GanttBarStyleException style : exceptions)
      {
         m_writer.writeStartObject();
         writeIntegerField("task_unique_id", Integer.valueOf(style.getTaskUniqueID()));
         writeIntegerField("bar_style_id", style.getGanttBarStyleID());
         writeFieldType("top_", style.getTopText());
         writeFieldType("bottom_", style.getBottomText());
         writeFieldType("left_", style.getLeftText());
         writeFieldType("right_", style.getRightText());
         writeFieldType("inside_", style.getInsideText());
         m_writer.writeEndObject();
      }
      m_writer.writeEndArray();
   }

   /**
    * Write a TaskMode field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeTaskModeField(String fieldName, Object value) throws IOException
   {
      if (!(value instanceof TaskMode))
      {
         return;
      }

      TaskMode type = (TaskMode) value;
      if (type != TaskMode.AUTO_SCHEDULED)
      {
         writeStringField(fieldName, type.name());
      }
   }

   private void writeFieldType(String prefix, FieldType fieldType) throws IOException
   {
      if (fieldType != null)
      {
         writeOptionalStringField(prefix + "field_type_class", fieldType.getFieldTypeClass().name().toLowerCase());
         writeOptionalStringField(prefix + "field_type", fieldType.name().toLowerCase());
      }
   }

   private void writeStringList(String name, List<String> list) throws IOException
   {
      m_writer.writeArrayFieldStart(name);
      for (String value : list)
      {
         m_writer.writeObject(value);
      }
      m_writer.writeEndArray();
   }

   private void writeDoubleList(String name, List<Double> list) throws IOException
   {
      m_writer.writeArrayFieldStart(name);
      for (Double value : list)
      {
         m_writer.writeObject(value);
      }
      m_writer.writeEndArray();
   }

   private void writeIntegerList(String name, List<Integer> list) throws IOException
   {
      m_writer.writeArrayFieldStart(name);
      for (Integer value : list)
      {
         m_writer.writeObject(value);
      }
      m_writer.writeEndArray();
   }

   private ProjectFile m_projectFile;
   private JsonGenerator m_writer;
   private boolean m_pretty;
   private boolean m_includeLayoutData;
   private Charset m_charset = DEFAULT_CHARSET;
   private boolean m_writeAttributeTypes;
   private TimeUnit m_timeUnits;
   private final StringBuilder m_buffer = new StringBuilder();

   private static final Charset DEFAULT_CHARSET = CharsetHelper.UTF8;

   private static final Map<String, DataType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put(Boolean.class.getName(), DataType.BOOLEAN);
      TYPE_MAP.put(LocalDateTime.class.getName(), DataType.DATE);
      TYPE_MAP.put(Double.class.getName(), DataType.NUMERIC);
      TYPE_MAP.put(Duration.class.getName(), DataType.DURATION);
      TYPE_MAP.put(Integer.class.getName(), DataType.INTEGER);
   }

   private static final Map<TimeUnit, TimeUnit> ELAPSED_TIME_UNIT_MAP = new HashMap<>();
   static
   {
      ELAPSED_TIME_UNIT_MAP.put(TimeUnit.MINUTES, TimeUnit.ELAPSED_MINUTES);
      ELAPSED_TIME_UNIT_MAP.put(TimeUnit.HOURS, TimeUnit.ELAPSED_HOURS);
      ELAPSED_TIME_UNIT_MAP.put(TimeUnit.DAYS, TimeUnit.ELAPSED_DAYS);
      ELAPSED_TIME_UNIT_MAP.put(TimeUnit.WEEKS, TimeUnit.ELAPSED_WEEKS);
      ELAPSED_TIME_UNIT_MAP.put(TimeUnit.MONTHS, TimeUnit.ELAPSED_MONTHS);
   }

   private static final Set<FieldType> IGNORED_FIELDS = new HashSet<>(Arrays.asList(AssignmentField.ASSIGNMENT_TASK_GUID, AssignmentField.ASSIGNMENT_RESOURCE_GUID, ResourceField.CALENDAR_GUID, ResourceField.STANDARD_RATE_UNITS, ResourceField.OVERTIME_RATE_UNITS));
   private static final Set<FieldType> MANDATORY_FIELDS = new HashSet<>(Arrays.asList(TaskField.UNIQUE_ID, TaskField.PARENT_TASK_UNIQUE_ID, ProjectField.DEFAULT_CALENDAR_UNIQUE_ID));

   private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S");
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
}
