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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mpxj.ActivityCode;
import net.sf.mpxj.ActivityCodeValue;
import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.DataType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EarnedValueMethod;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarDateRanges;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.RecurringData;
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
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.common.CharsetHelper;
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
         writeCustomFields();
         writeActivityCodes();
         writeProperties();
         writeCalendars();
         writeResources();
         writeTasks();
         writeAssignments();
         m_writer.writeEndObject();

         m_writer.flush();
      }

      finally
      {
         m_projectFile = null;
      }
   }

   /**
    * Write a list of custom field attributes.
    */
   private void writeCustomFields() throws IOException
   {
      List<CustomField> sortedCustomFieldsList = m_projectFile.getCustomFields().stream().filter(f -> f.getFieldType() != null).sorted(this::compareCustomFields).collect(Collectors.toList());
      m_writer.writeStartList("custom_fields");
      for (CustomField field : sortedCustomFieldsList)
      {
         writeCustomField(field);
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
    * Comparator to ensure consistent ordering.
    *
    * @param f1 CustomField instance
    * @param f2 CustomField instance
    * @return comparison result
    */
   private int compareCustomFields(CustomField f1, CustomField f2)
   {
      FieldType o1 = f1.getFieldType();
      FieldType o2 = f2.getFieldType();
      String name1 = o1.getClass().getSimpleName() + "." + o1.getName() + " " + f1.getAlias();
      String name2 = o2.getClass().getSimpleName() + "." + o2.getName() + " " + f2.getAlias();
      return name1.compareTo(name2);
   }

   /**
    * Write attributes for an individual custom field.
    * Note that at present we are only writing a subset of the
    * available data... in this instance the field alias.
    * If the field does not have an alias we won't write an
    * entry.
    *
    * @param field custom field to write
    */
   private void writeCustomField(CustomField field) throws IOException
   {
      if (field.getAlias() != null)
      {
         m_writer.writeStartObject(null);
         m_writer.writeNameValuePair("field_type_class", field.getFieldType().getFieldTypeClass().name().toLowerCase());
         m_writer.writeNameValuePair("field_type", field.getFieldType().name().toLowerCase());
         m_writer.writeNameValuePair("field_alias", field.getAlias());
         m_writer.writeEndObject();
      }
   }

   /**
    * This method writes project property data to a JSON file.
    */
   private void writeProperties() throws IOException
   {
      writeAttributeTypes("property_types", ProjectField.values());
      writeFields("property_values", m_projectFile.getProjectProperties(), ProjectField.values());
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
         writeFields(null, resource, ResourceField.values());
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
      writeIntegerField("unique_id", calendar.getUniqueID());
      writeIntegerField("parent_unique_id", calendar.getParent() == null ? null : calendar.getParent().getUniqueID());
      writeStringField("name", calendar.getName());
      writeIntegerField("minutes_per_day", calendar.getCalendarMinutesPerDay());
      writeIntegerField("minutes_per_week", calendar.getCalendarMinutesPerWeek());
      writeIntegerField("minutes_per_month", calendar.getCalendarMinutesPerMonth());
      writeIntegerField("minutes_per_year", calendar.getCalendarMinutesPerYear());
      writeCalendarDays(calendar);
      writeCalendarWeeks(calendar);
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
      writeStringField("name", week.getName());
      writeDateField("effective_from", week.getDateRange().getStart());
      writeDateField("effective_to", week.getDateRange().getEnd());
      writeCalendarDays(week);
   }

   /**
    * Write day definitions for a working week.
    *
    * @param week working week
    */
   private void writeCalendarDays(ProjectCalendarWeek week) throws IOException
   {
      for (Day day : Day.values())
      {
         m_writer.writeStartObject(day.name().toLowerCase());
         writeStringField("type", week.getWorkingDay(day).toString().toLowerCase());
         writeCalendarHours(week.getHours(day));
         m_writer.writeEndObject();
      }
   }

   /**
    * Write working hours definition.
    *
    * @param hours working hours
    */
   private void writeCalendarHours(ProjectCalendarDateRanges hours) throws IOException
   {
      if (hours.getRangeCount() != 0)
      {
         m_writer.writeStartList("hours");
         for (DateRange range : hours)
         {
            m_writer.writeStartObject(null);
            writeTimeField("from", range.getStart());
            writeTimeField("to", range.getEnd());
            m_writer.writeEndObject();
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
      writeStringField("name", ex.getName()) ;
      writeDateField("from", ex.getFromDate());
      writeDateField("to", ex.getToDate());
      writeBooleanField("working", ex.getWorking());
      writeCalendarHours(ex);
      writeRecurringData(ex.getRecurring());
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
         writeBooleanField("relative", data.getRelative());
         writeIntegerField("day_number", data.getDayNumber());
         writeIntegerField("month_number", data.getMonthNumber());
         writeBooleanField("use_end_date", data.getUseEndDate());

         List<Object> weeklyDays = Arrays.stream(Day.values()).filter(d -> data.getWeeklyDay(d)).map(d -> d.toString().toLowerCase()).collect(Collectors.toList());
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
      writeFields(null, task, TaskField.values());
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
         writeFields(null, assignment, AssignmentField.values());
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
    * Write a set of fields from a field container to a JSON file.
    * @param objectName name of the object, or null if no name required
    * @param container field container
    * @param fields fields to write
    */
   private void writeFields(String objectName, FieldContainer container, FieldType[] fields) throws IOException
   {
      m_writer.writeStartObject(objectName);
      for (FieldType field : fields)
      {
         Object value = container.getCurrentValue(field);
         if (value != null)
         {
            writeField(container, field, value);
         }
      }
      m_writer.writeEndObject();
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
            writeDateField(fieldName, value);
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

         case SUBPROJECT:
         {
            writeSubproject(fieldName, value);
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

         case BINARY:
         {
            // Don't write binary data
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
                     defaults = ((Resource) container).getResourceCalendar();
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
   private void writeDateField(String fieldName, Object value) throws IOException
   {
      if (value != null)
      {
         if (value instanceof String)
         {
            m_writer.writeNameValuePair(fieldName + "_text", (String) value);
         }
         else
         {
            Date val = (Date) value;
            m_writer.writeNameValuePair(fieldName, val);
         }
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
         m_writer.writeNameValuePairAsTime(fieldName, (Date)value);
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
      List<DateRange> list = (List<DateRange>) value;
      m_writer.writeStartList(fieldName);
      for (DateRange entry : list)
      {
         m_writer.writeStartObject(null);
         writeDateField("start", entry.getStart());
         writeDateField("end", entry.getEnd());
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();
   }

   /**
    * Write a subproject to the JSON file.
    *
    * @param fieldName field name
    * @param value field value
    */
   private void writeSubproject(String fieldName, Object value) throws IOException
   {
      SubProject sp = (SubProject) value;
      m_writer.writeStartObject(fieldName);

      writeStringField("dos_file_name", sp.getDosFileName());
      writeStringField("dos_full_path", sp.getDosFullPath());
      writeStringField("file_name", sp.getFileName());
      writeStringField("full_path", sp.getFullPath());
      writeIntegerField("task_unique_id", sp.getTaskUniqueID());
      writeIntegerField("unique_id_offset", sp.getUniqueIDOffset());

      m_writer.writeStartList("all_external_task_unique_ids");
      for (Integer id : sp.getAllExternalTaskUniqueIDs())
      {
         m_writer.writeStartObject(null);
         writeIntegerField("id", id);
         m_writer.writeEndObject();
      }
      m_writer.writeEndList();

      m_writer.writeEndObject();
   }

   /**
    * Write an activity code to the JSON file.
    *
    * @param code ActivityCode.
    */
   private void writeActivityCode(ActivityCode code) throws IOException
   {
      m_writer.writeStartObject(null);

      writeIntegerField("unique_id", code.getUniqueID());
      writeStringField("name", code.getName());
      if (!code.getValues().isEmpty())
      {
         m_writer.writeStartList("values");
         for (ActivityCodeValue value : code.getValues())
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
      writeIntegerField("unique_id", value.getUniqueID());
      writeStringField("name", value.getName());
      writeStringField("desription", value.getDescription());
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
            writeIntegerField("task_unique_id", relation.getTargetTask().getUniqueID());
            writeDurationField(m_projectFile.getProjectProperties(), "lag", relation.getLag());
            writeStringField("type", relation.getType());
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

   private void writeActivityCodeList(String fieldName, Object value) throws IOException
   {
      @SuppressWarnings("unchecked")
      List<ActivityCodeValue> list = (List<ActivityCodeValue>) value;
      if (!list.isEmpty())
      {
         m_writer.writeList(fieldName, list.stream().map(ActivityCodeValue::getUniqueID).collect(Collectors.toList()));
      }
   }

   private ProjectFile m_projectFile;
   private JsonStreamWriter m_writer;
   private boolean m_pretty;
   private Charset m_encoding = DEFAULT_ENCODING;
   private boolean m_writeAttributeTypes;
   private TimeUnit m_timeUnits;

   private static final Charset DEFAULT_ENCODING = CharsetHelper.UTF8;

   private static final Map<String, DataType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put(Boolean.class.getName(), DataType.BOOLEAN);
      TYPE_MAP.put(Date.class.getName(), DataType.DATE);
      TYPE_MAP.put(Double.class.getName(), DataType.NUMERIC);
      TYPE_MAP.put(Duration.class.getName(), DataType.DURATION);
      TYPE_MAP.put(Integer.class.getName(), DataType.INTEGER);
   }

   private static final Set<FieldType> IGNORED_FIELDS = new HashSet<>(Arrays.asList(AssignmentField.ASSIGNMENT_TASK_GUID, AssignmentField.ASSIGNMENT_RESOURCE_GUID, ResourceField.CALENDAR_GUID));
   private static final Set<FieldType> MANDATORY_FIELDS = new HashSet<>(Arrays.asList(TaskField.UNIQUE_ID, TaskField.PARENT_TASK_UNIQUE_ID));
}
