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

package net.sf.mpxj.json;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mpxj.Availability;
import net.sf.mpxj.Column;
import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.UnitOfMeasure;
import net.sf.mpxj.common.DayOfWeekHelper;
import net.sf.mpxj.ExpenseItem;
import net.sf.mpxj.ProjectCalendarDays;
import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.DataType;
import net.sf.mpxj.LocalDateTimeRange;
import java.time.DayOfWeek;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EarnedValueMethod;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.RecurringData;
import net.sf.mpxj.Step;
import net.sf.mpxj.Table;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.LocalTimeRange;
import net.sf.mpxj.TimeUnitDefaultsContainer;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceRequestType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.UserDefinedField;
import net.sf.mpxj.View;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.ColorHelper;
import net.sf.mpxj.common.FieldTypeHelper;
import net.sf.mpxj.common.LocalDateTimeHelper;
import net.sf.mpxj.mpp.GanttBarStyle;
import net.sf.mpxj.mpp.GanttBarStyleException;
import net.sf.mpxj.mpp.GanttChartView;
import net.sf.mpxj.mpp.TableFontStyle;
import net.sf.mpxj.writer.AbstractProjectWriter;

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
    * Retrieve the encoding to used when writing the JSON file.
    *
    * @return encoding
    */
   public Charset getEncoding()
   {
      return m_encoding;
   }

   /**
    * Set the encoding to used when writing the JSON file.
    *
    * @param encoding encoding to use
    */
   public void setEncoding(Charset encoding)
   {
      m_encoding = encoding;
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
         m_writer = new JsonStreamWriter(stream, m_encoding);
         m_writer.setPretty(m_pretty);

         m_writer.writeStartObject(null);
         writeProperties();
         writeUserDefinedFields();
         writeCustomFields();
         writeWorkContours();
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

      m_writer.writeStartList("user_defined_fields");
      for (UserDefinedField field : sortedFieldsList)
      {
         writeUserDefinedField(field);
      }
      m_writer.writeEndList();
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

      m_writer.writeStartList("custom_fields");
      for (CustomField field : sortedFieldsList)
      {
         writeCustomField(field);
      }
      m_writer.writeEndList();
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

      m_writer.writeStartList("work_contours");
      for (WorkContour contour : contours)
      {
         m_writer.writeStartObject(null);
         writeIntegerField("unique_id", contour.getUniqueID());
         writeStringField("name", contour.getName());
         writeBooleanField("default", Boolean.valueOf(contour.isContourDefault()));
         if (contour.getCurveValues() != null)
         {
            m_writer.writeList("curve_values", Arrays.stream(contour.getCurveValues()).mapToObj(Double::toString).collect(Collectors.toList()));
         }
         m_writer.writeEndObject();
      }

      m_writer.writeEndList();
   }

   /**
    * Write a list of activity codes.
    */
   private void writeActivityCodes() throws IOException
   {
      if (!m_projectFile.getActivityCodes().isEmpty())
      {
         List<ActivityCode> sortedActivityCodeList = new ArrayList<>(m_projectFile.getActivityCodes());
         sortedActivityCodeList.sort(Comparator.comparing(ActivityCode::getName));
         m_writer.writeStartList("activity_codes");
         for (ActivityCode code : sortedActivityCodeList)
         {
            writeActivityCode(code);
         }
         m_writer.writeEndList();
      }
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
      if (field.getAlias() != null)
      {
         m_writer.writeStartObject(null);

         Integer uniqueID = field.getUniqueID();
         if (uniqueID.intValue() != FieldTypeHelper.getFieldID(field.getFieldType()))
         {
            // Only write this attribute is we have a non-default value
            m_writer.writeNameValuePair("unique_id", field.getUniqueID().intValue());
         }

         writeFieldType("", field.getFieldType());
         m_writer.writeNameValuePair("field_alias", field.getAlias());

         m_writer.writeEndObject();
      }
   }

   /**
    * Write an individual user defined field.
    *
    * @param field user defined field
    */
   private void writeUserDefinedField(UserDefinedField field) throws IOException
   {
      m_writer.writeStartObject(null);
      writeIntegerField("unique_id", Integer.valueOf(FieldTypeHelper.getFieldID(field)));
      writeStringField("field_type_class", field.getFieldTypeClass().toString().toLowerCase());
      writeBooleanField("summary_task_only", Boolean.valueOf(field.getSummaryTaskOnly()));
      writeStringField("data_type", field.getDataType().toString().toLowerCase());
      writeStringField("internal_name", field.name().toLowerCase());
      writeStringField("external_name", field.getName());
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

      m_writer.writeStartList("units_of_measure");
      for (UnitOfMeasure uom : m_projectFile.getUnitsOfMeasure())
      {
         writeUnitOfMeasure(uom);
      }
      m_writer.writeEndList();
   }

   /**
    * Write an individual unit of measure.
    *
    * @param uom unit of measure
    */
   private void writeUnitOfMeasure(UnitOfMeasure uom) throws IOException
   {
      m_writer.writeStartObject(null);
      writeMandatoryIntegerField("unique_id", uom.getUniqueID());
      writeStringField("abbreviation", uom.getAbbreviation());
      writeStringField("name", uom.getName());
      writeMandatoryIntegerField("sequence_number", uom.getSequenceNumber());
      m_writer.writeEndObject();
   }

   /**
    * This method writes project property data to a JSON file.
    */
   private void writeProperties() throws IOException
   {
      writeAttributeTypes("property_types", ProjectField.values());
      m_writer.writeStartObject("property_values");
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

      m_writer.writeStartList("resources");
      for (Resource resource : m_projectFile.getResources())
      {
         m_writer.writeStartObject(null);
         writeFields(resource, ResourceField.values());
         writeFields(resource, m_projectFile.getUserDefinedFields().getResourceFields().toArray(new FieldType[0]));
         writeCostRateTables(resource);
         writeAvailabilityTable(resource);
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   /**
    * Writes calendar data to a JSON file.
    */
   private void writeCalendars() throws IOException
   {
      m_writer.writeStartList("calendars");
      for (ProjectCalendar calendar : m_projectFile.getCalendars())
      {
         writeCalendar(calendar);
      }
      m_writer.writeEndList();
   }

   /**
    * Writes an individual calendar to a JSON file.
    *
    * @param calendar calendar to write
    */
   private void writeCalendar(ProjectCalendar calendar) throws IOException
   {
      m_writer.writeStartObject(null);
      writeMandatoryIntegerField("unique_id", calendar.getUniqueID());
      writeStringField("guid", calendar.getGUID());
      writeMandatoryIntegerField("parent_unique_id", calendar.getParentUniqueID());
      writeStringField("name", calendar.getName());
      writeStringField("type", calendar.getType().toString());
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
         m_writer.writeStartList("working_weeks");
         for (ProjectCalendarWeek week : calendar.getWorkWeeks())
         {
            writeCalendarWeek(week);
         }
         m_writer.writeEndList();
      }
   }

   /**
    * Write an individual working week definition.
    *
    * @param week working week definition
    */
   private void writeCalendarWeek(ProjectCalendarWeek week) throws IOException
   {
      m_writer.writeStartObject(null);
      writeStringField("name", week.getName());
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
         m_writer.writeStartObject(day.name().toLowerCase());
         writeStringField("type", week.getCalendarDayType(day).toString().toLowerCase());
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
         m_writer.writeStartList("hours");
         for (LocalTimeRange range : hours)
         {
            m_writer.writeStartObject(null);
            writeTimeField("from", range.getStart());
            writeTimeField("to", range.getEnd());
            m_writer.writeEndObject();
         }
         m_writer.writeEndList();
      }
   }

   private void writeCalendarExceptions(ProjectCalendar calendar) throws IOException
   {
      if (!calendar.getCalendarExceptions().isEmpty())
      {
         m_writer.writeStartList("exceptions");
         for (ProjectCalendarException ex : calendar.getCalendarExceptions())
         {
            writeCalendarException(ex);
         }
         m_writer.writeEndList();
      }
   }

   /**
    * Write a calendar exception.
    *
    * @param ex calendar exception
    */
   private void writeCalendarException(ProjectCalendarException ex) throws IOException
   {
      m_writer.writeStartObject(null);
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
      writeStringField("name", ex.getName());
      if (ex.getRecurring() == null)
      {
         writeDateField("from", ex.getFromDate());
         writeDateField("to", ex.getToDate());
      }
      writeStringField("type", type.toString().toLowerCase());
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
         m_writer.writeStartObject("recurrence");
         writeStringField("type", data.getRecurrenceType().toString().toLowerCase());
         writeDateField("start_date", data.getStartDate());
         writeDateField("finish_date", data.getFinishDate());
         writeIntegerField("occurrences", data.getOccurrences());
         writeIntegerField("frequency", data.getFrequency());
         writeBooleanField("relative", Boolean.valueOf(data.getRelative()));
         writeIntegerField("day_number", data.getDayNumber());
         writeIntegerField("month_number", data.getMonthNumber());
         writeBooleanField("use_end_date", Boolean.valueOf(data.getUseEndDate()));

         List<Object> weeklyDays = Arrays.stream(DayOfWeekHelper.ORDERED_DAYS).filter(data::getWeeklyDay).map(d -> "\"" + d.toString().toLowerCase() + "\"").collect(Collectors.toList());
         if (!weeklyDays.isEmpty())
         {
            m_writer.writeList("weekly_days", weeklyDays);
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

      m_writer.writeStartList("tasks");
      for (Task task : m_projectFile.getChildTasks())
      {
         writeTask(task);
      }
      m_writer.writeEndList();
   }

   /**
    * This method is called recursively to write a task and its child tasks
    * to the JSON file.
    *
    * @param task task to write
    */
   private void writeTask(Task task) throws IOException
   {
      m_writer.writeStartObject(null);
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

      m_writer.writeStartList("assignments");
      for (ResourceAssignment assignment : m_projectFile.getResourceAssignments())
      {
         m_writer.writeStartObject(null);
         writeFields(assignment, AssignmentField.values());
         writeFields(assignment, m_projectFile.getUserDefinedFields().getAssignmentFields().toArray(new FieldType[0]));
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
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
         m_writer.writeStartObject(name);
         for (FieldType field : types)
         {
            m_writer.writeNameValuePair(field.name().toLowerCase(), field.getDataType().getValue());
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
         m_writer.writeStartObject("cost_rate_tables");
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

      m_writer.writeStartList("availability_table");
      for (Availability entry : availability)
      {
         m_writer.writeStartObject(null);
         writeTimestampField("start", entry.getRange().getStart());
         writeTimestampField("end", entry.getRange().getEnd());
         writeDoubleField("units", entry.getUnits());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
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
         m_writer.writeStartList(Integer.toString(index));
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

            m_writer.writeStartObject(null);
            writeTimestampField("start_date", startDate);
            writeTimestampField("end_date", endDate);
            writeDoubleField("cost_per_use", entry.getCostPerUse());
            m_writer.writeStartObject("rates");
            for (int rateIndex = 0; rateIndex < CostRateTableEntry.MAX_RATES; rateIndex++)
            {
               writeCostRate(rateIndex, entry.getRate(rateIndex));
            }
            m_writer.writeEndObject();

            m_writer.writeEndObject();
         }
         m_writer.writeEndList();
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

         case ACTIVITY_CODE_LIST:
         {
            writeActivityCodeList(fieldName, value);
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

            writeStringField(fieldName, value);
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
   private void writeMandatoryIntegerField(String fieldName, Object value) throws IOException
   {
      if (value != null)
      {
         m_writer.writeNameValuePair(fieldName, ((Number) value).intValue());
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
      if (value != null)
      {
         int val = ((Number) value).intValue();
         if (val != 0 || MANDATORY_FIELDS.contains(fieldType))
         {
            m_writer.writeNameValuePair(fieldName, val);
         }
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
      if (value != null)
      {
         double val = ((Number) value).doubleValue();
         if (val != 0)
         {
            // Round to 4 decimal places
            val = Math.round(val * 10000.0) / 10000.0;
            m_writer.writeNameValuePair(fieldName, val);
         }
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
      if (value != null)
      {
         boolean val = ((Boolean) value).booleanValue();
         if (val)
         {
            m_writer.writeNameValuePair(fieldName, val);
         }
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
      if (value != null)
      {
         if (value instanceof String)
         {
            m_writer.writeNameValuePair(fieldName + "_text", (String) value);
         }
         else
         {
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
                  m_writer.writeNameValuePair(fieldName, seconds);
               }
               else
               {
                  Duration duration = val.convertUnits(m_timeUnits, defaults);
                  m_writer.writeNameValuePair(fieldName, duration.getDuration());
               }
            }
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
      if (value != null)
      {
         if (value instanceof String)
         {
            m_writer.writeNameValuePair(fieldName + "_text", (String) value);
         }
         else
         {
            LocalDateTime val = (LocalDateTime) value;
            m_writer.writeNameValuePair(fieldName, val);
         }
      }
   }

   /**
    * Write a date field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeDateField(String fieldName, Object value) throws IOException
   {
      if (value != null)
      {
         m_writer.writeNameValuePairAsDate(fieldName, (LocalDate) value);
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
      if (value != null)
      {
         m_writer.writeNameValuePairAsTime(fieldName, (LocalTime) value);
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
      if (value != null)
      {
         TimeUnit val = (TimeUnit) value;
         if (val != m_projectFile.getProjectProperties().getDefaultDurationUnits())
         {
            m_writer.writeNameValuePair(fieldName, val.toString());
         }
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
      if (value != null)
      {
         m_writer.writeNameValuePair(fieldName, ((Priority) value).getValue());
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
      if (value != null && ((Rate) value).getAmount() != 0.0)
      {
         m_writer.writeNameValuePair(fieldName, ((Rate) value).getAmount() + "/" + ((Rate) value).getUnits());
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
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) value;
      if (map.isEmpty())
      {
         return;
      }

      m_writer.writeStartObject(fieldName);
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
      @SuppressWarnings("unchecked")
      List<LocalDateTimeRange> list = (List<LocalDateTimeRange>) value;
      m_writer.writeStartList(fieldName);
      for (LocalDateTimeRange entry : list)
      {
         m_writer.writeStartObject(null);
         writeTimestampField("start", entry.getStart());
         writeTimestampField("end", entry.getEnd());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   /**
    * Write an activity code to the JSON file.
    *
    * @param code ActivityCode.
    */
   private void writeActivityCode(ActivityCode code) throws IOException
   {
      m_writer.writeStartObject(null);

      writeMandatoryIntegerField("unique_id", code.getUniqueID());
      writeStringField("scope", code.getScope());
      writeIntegerField("scope_eps_unique_id", code.getScopeEpsUniqueID());
      writeIntegerField("scope_project_unique_id", code.getScopeProjectUniqueID());
      writeMandatoryIntegerField("sequence_number", code.getSequenceNumber());
      writeStringField("name", code.getName());
      if (!code.getValues().isEmpty())
      {
         m_writer.writeStartList("values");
         for (ActivityCodeValue value : code.getValues().stream().sorted(Comparator.comparing(ActivityCodeValue::getUniqueID)).collect(Collectors.toList()))
         {
            writeActivityCodeValue(value);
         }
         m_writer.writeEndList();
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
      m_writer.writeStartObject(null);
      writeMandatoryIntegerField("unique_id", value.getUniqueID());
      writeMandatoryIntegerField("sequence_number", value.getSequenceNumber());
      writeStringField("name", value.getName());
      writeStringField("description", value.getDescription());
      writeColorField("color", value.getColor());
      if (value.getParent() != null)
      {
         writeIntegerField("parent_unique_id", value.getParent().getUniqueID());
      }
      m_writer.writeEndObject();
   }

   /**
    * Write a string field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeStringField(String fieldName, Object value) throws IOException
   {
      if (value != null)
      {
         String val = value.toString();
         if (!val.isEmpty())
         {
            m_writer.writeNameValuePair(fieldName, val);
         }
      }
   }

   /**
    * Write a relation list field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeRelationList(String fieldName, Object value) throws IOException
   {
      @SuppressWarnings("unchecked")
      List<Relation> list = (List<Relation>) value;
      if (!list.isEmpty())
      {
         m_writer.writeStartList(fieldName);
         for (Relation relation : list)
         {
            m_writer.writeStartObject(null);
            writeIntegerField("unique_id", relation.getUniqueID());
            writeIntegerField("task_unique_id", relation.getTargetTask().getUniqueID());
            writeDurationField(m_projectFile.getProjectProperties(), "lag", relation.getLag());
            writeStringField("type", relation.getType());
            writeStringField("notes", relation.getNotes());
            m_writer.writeEndObject();
         }
         m_writer.writeEndList();
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
      if (value != null)
      {
         ResourceRequestType type = (ResourceRequestType) value;
         if (type != ResourceRequestType.NONE)
         {
            m_writer.writeNameValuePair(fieldName, type.name());
         }
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
      if (value != null)
      {
         WorkContour type = (WorkContour) value;
         if (!type.isContourFlat())
         {
            m_writer.writeNameValuePair(fieldName, type.toString());
         }
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
      if (value != null)
      {
         EarnedValueMethod method = (EarnedValueMethod) value;
         if (container instanceof ProjectProperties || method != m_projectFile.getProjectProperties().getDefaultTaskEarnedValueMethod())
         {
            m_writer.writeNameValuePair(fieldName, method.name());
         }
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
      if (value != null)
      {
         TaskType type = (TaskType) value;
         if (container instanceof ProjectProperties || type != m_projectFile.getProjectProperties().getDefaultTaskType())
         {
            m_writer.writeNameValuePair(fieldName, type.name());
         }
      }
   }

   private void writeColorField(String name, Color value) throws IOException
   {
      if (value != null)
      {
         m_writer.writeNameValuePair(name, ColorHelper.getHtmlColor(value));
      }
   }

   private void writeActivityCodeList(String fieldName, Object value) throws IOException
   {
      @SuppressWarnings("unchecked")
      List<ActivityCodeValue> list = (List<ActivityCodeValue>) value;
      if (!list.isEmpty())
      {
         m_writer.writeList(fieldName, list.stream().map(ActivityCodeValue::getUniqueID).collect(Collectors.toList()));
      }
   }

   private void writeExpenseItemList(String fieldName, Object value) throws IOException
   {
      @SuppressWarnings("unchecked")
      List<ExpenseItem> list = (List<ExpenseItem>) value;
      if (list.isEmpty())
      {
         return;
      }

      m_writer.writeStartList(fieldName);
      for (ExpenseItem item : list)
      {
         m_writer.writeStartObject(null);
         writeIntegerField("unique_id", item.getUniqueID());
         writeStringField("name", item.getName());
         writeStringField("description", item.getDescription());
         writeIntegerField("account_unique_id", item.getAccountUniqueID());
         writeIntegerField("category_unique_id", item.getCategoryUniqueID());
         writeStringField("document_number", item.getDocumentNumber());
         writeStringField("vendor", item.getVendor());
         writeDoubleField("at_completion_cost", item.getAtCompletionCost());
         writeDoubleField("at_completion_units", item.getAtCompletionUnits());
         writeDoubleField("actual_cost", item.getActualCost());
         writeDoubleField("actual_units", item.getActualUnits());
         writeDoubleField("price_per_unit", item.getPricePerUnit());
         writeDoubleField("remaining_cost", item.getRemainingCost());
         writeDoubleField("remaining_units", item.getRemainingUnits());
         writeDoubleField("planned_cost", item.getPlannedCost());
         writeDoubleField("planned_units", item.getPlannedUnits());
         writeStringField("accrue_type", item.getAccrueType().name().toLowerCase());
         writeBooleanField("auto_compute_actuals", Boolean.valueOf(item.getAutoComputeActuals()));
         writeStringField("unit_of_measure", item.getUnitOfMeasure());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   private void writeStepList(String fieldName, Object value) throws IOException
   {
      @SuppressWarnings("unchecked")
      List<Step> list = (List<Step>) value;
      if (list.isEmpty())
      {
         return;
      }

      m_writer.writeStartList(fieldName);
      for (Step item : list)
      {
         m_writer.writeStartObject(null);
         writeIntegerField("unique_id", item.getUniqueID());
         writeStringField("name", item.getName());
         writeIntegerField("sequence_number", item.getSequenceNumber());
         writeDoubleField("percent_complete", item.getPercentComplete());
         writeDoubleField("weight", item.getWeight());
         writeStringField("description", item.getDescription());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   private void writeTables() throws IOException
   {
      if (m_projectFile.getTables().isEmpty())
      {
         return;
      }

      m_writer.writeStartList("tables");
      for (Table table : m_projectFile.getTables())
      {
         m_writer.writeStartObject(null);
         writeIntegerField("id", Integer.valueOf(table.getID()));
         writeStringField("name", table.getName());
         writeBooleanField("resource", Boolean.valueOf(table.getResourceFlag()));
         writeTableColumns(table);
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   private void writeTableColumns(Table table) throws IOException
   {
      if (table.getColumns().isEmpty())
      {
         return;
      }

      m_writer.writeStartList("columns");
      for (Column column : table.getColumns())
      {
         m_writer.writeStartObject(null);
         writeFieldType("", column.getFieldType());
         writeStringField("title", column.getTitle());
         writeIntegerField("width", Integer.valueOf(column.getWidth()));
         writeIntegerField("align_data", Integer.valueOf(column.getAlignData()));
         writeIntegerField("align_title", Integer.valueOf(column.getAlignTitle()));
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   private void writeViews() throws IOException
   {
      if (m_projectFile.getViews().isEmpty())
      {
         return;
      }

      m_writer.writeStartList("views");
      for (View view : m_projectFile.getViews())
      {
         m_writer.writeStartObject(null);
         writeIntegerField("id", view.getID());
         writeStringField("name", view.getName());
         writeStringField("type", view.getType().name().toLowerCase());
         writeStringField("table_name", view.getTableName());
         writeViewTableFontStyles(view);
         writeBarStyles(view);
         writeBarStyleExceptions(view);
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
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

      m_writer.writeStartList("table_font_styles");
      for (TableFontStyle style : ganttChartView.getTableFontStyles())
      {
         m_writer.writeStartObject(null);
         writeIntegerField("row_unique_id", Integer.valueOf(style.getRowUniqueID()));
         writeFieldType("", style.getFieldType());
         // TODO: add more of the style attributes as needed
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   private void writeBarStyles(View view) throws IOException
   {
      if (!(view instanceof GanttChartView))
      {
         return;
      }

      GanttChartView ganttChartView = (GanttChartView) view;
      if (ganttChartView.getBarStyles() == null)
      {
         return;
      }

      m_writer.writeStartList("bar_styles");
      for (GanttBarStyle style : ((GanttChartView) view).getBarStyles())
      {
         m_writer.writeStartObject(null);
         writeIntegerField("row", Integer.valueOf(style.getRow()));
         writeStringField("name", style.getName());
         writeFieldType("from_", style.getFromField());
         writeFieldType("to_", style.getToField());
         writeFieldType("top_", style.getTopText());
         writeFieldType("bottom_", style.getBottomText());
         writeFieldType("left_", style.getLeftText());
         writeFieldType("right_", style.getRightText());
         writeFieldType("inside_", style.getInsideText());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   private void writeBarStyleExceptions(View view) throws IOException
   {
      if (!(view instanceof GanttChartView))
      {
         return;
      }

      GanttChartView ganttChartView = (GanttChartView) view;
      if (ganttChartView.getBarStyleExceptions() == null)
      {
         return;
      }

      m_writer.writeStartList("bar_style_exceptions");
      for (GanttBarStyleException style : ((GanttChartView) view).getBarStyleExceptions())
      {
         m_writer.writeStartObject(null);
         writeIntegerField("task_unique_id", Integer.valueOf(style.getTaskUniqueID()));
         writeIntegerField("bar_style_index", Integer.valueOf(style.getBarStyleIndex()));
         writeFieldType("top_", style.getTopText());
         writeFieldType("bottom_", style.getBottomText());
         writeFieldType("left_", style.getLeftText());
         writeFieldType("right_", style.getRightText());
         writeFieldType("inside_", style.getInsideText());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   /**
    * Write a TaskMode field to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeTaskModeField(String fieldName, Object value) throws IOException
   {
      if (value != null)
      {
         TaskMode type = (TaskMode) value;
         if (type != TaskMode.AUTO_SCHEDULED)
         {
            m_writer.writeNameValuePair(fieldName, type.name());
         }
      }
   }

   private void writeFieldType(String prefix, FieldType fieldType) throws IOException
   {
      if (fieldType != null)
      {
         writeStringField(prefix + "field_type_class", fieldType.getFieldTypeClass().name().toLowerCase());
         writeStringField(prefix + "field_type", fieldType.name().toLowerCase());
      }
   }

   private ProjectFile m_projectFile;
   private JsonStreamWriter m_writer;
   private boolean m_pretty;
   private boolean m_includeLayoutData;
   private Charset m_encoding = DEFAULT_ENCODING;
   private boolean m_writeAttributeTypes;
   private TimeUnit m_timeUnits;

   private static final Charset DEFAULT_ENCODING = CharsetHelper.UTF8;

   private static final Map<String, DataType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put(Boolean.class.getName(), DataType.BOOLEAN);
      TYPE_MAP.put(LocalDateTime.class.getName(), DataType.DATE);
      TYPE_MAP.put(Double.class.getName(), DataType.NUMERIC);
      TYPE_MAP.put(Duration.class.getName(), DataType.DURATION);
      TYPE_MAP.put(Integer.class.getName(), DataType.INTEGER);
   }

   private static final Set<FieldType> IGNORED_FIELDS = new HashSet<>(Arrays.asList(AssignmentField.ASSIGNMENT_TASK_GUID, AssignmentField.ASSIGNMENT_RESOURCE_GUID, ResourceField.CALENDAR_GUID, ResourceField.STANDARD_RATE_UNITS, ResourceField.OVERTIME_RATE_UNITS));
   private static final Set<FieldType> MANDATORY_FIELDS = new HashSet<>(Arrays.asList(TaskField.UNIQUE_ID, TaskField.PARENT_TASK_UNIQUE_ID, ProjectField.DEFAULT_CALENDAR_UNIQUE_ID));
}
