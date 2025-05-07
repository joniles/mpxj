/*
 * file:       MPXWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       03/01/2006
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

package org.mpxj.mpx;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalTime;

import org.mpxj.AccrueType;
import org.mpxj.ConstraintType;
import org.mpxj.DataType;
import java.time.DayOfWeek;

import org.mpxj.ResourceField;
import org.mpxj.TaskField;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.FieldType;
import org.mpxj.Priority;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.RecurrenceType;
import org.mpxj.RecurringTask;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceAssignmentWorkgroupFields;
import org.mpxj.Task;
import org.mpxj.TaskType;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.UserDefinedField;
import org.mpxj.common.MicrosoftProjectUniqueIDMapper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ProjectCalendarHelper;
import org.mpxj.mpp.UserDefinedFieldMap;
import org.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new MPX file from the contents of
 * a ProjectFile instance.
 */
public final class MPXWriter extends AbstractProjectWriter
{
   @Override public void write(ProjectFile projectFile, OutputStream out) throws IOException
   {
      m_projectFile = projectFile;
      m_eventManager = projectFile.getEventManager();

      if (m_useLocaleDefaults)
      {
         LocaleUtility.setLocale(m_projectFile.getProjectProperties(), m_locale);
      }

      m_delimiter = projectFile.getProjectProperties().getMpxDelimiter();
      m_writer = new OutputStreamWriter(new BufferedOutputStream(out), projectFile.getProjectProperties().getMpxCodePage().getCharset());
      m_buffer = new StringBuilder();
      m_formats = new MPXJFormats(m_locale, LocaleData.getString(m_locale, LocaleData.NA), m_projectFile);
      m_calendarNameSet = new HashSet<>();
      m_calendarNameMap = new HashMap<>();
      m_userDefinedFieldMap = new UserDefinedFieldMap(projectFile, MAPPING_TARGET_CUSTOM_FIELDS);
      projectFile.getUserDefinedFields().stream().sorted(Comparator.comparing(UserDefinedField::getUniqueID)).forEach(m_userDefinedFieldMap::generateMapping);
      m_resourceCalendarMap = m_projectFile.getResources().stream().filter(r -> r.getCalendarUniqueID() != null).collect(Collectors.groupingBy(Resource::getCalendarUniqueID));
      m_taskMapper = new MicrosoftProjectUniqueIDMapper(m_projectFile.getTasks());
      m_resourceMapper = new MicrosoftProjectUniqueIDMapper(m_projectFile.getResources());
      m_calendarMapper = new MicrosoftProjectUniqueIDMapper(m_projectFile.getCalendars());

      try
      {
         write();
      }

      finally
      {
         m_writer = null;
         m_projectFile = null;
         m_resourceModel = null;
         m_taskModel = null;
         m_buffer = null;
         m_locale = null;
         m_formats = null;
         m_calendarNameSet = null;
         m_calendarNameMap = null;
         m_userDefinedFieldMap = null;
         m_resourceCalendarMap = null;
         m_taskMapper = null;
         m_resourceMapper = null;
         m_calendarMapper = null;
      }
   }

   /**
    * Writes the contents of the project file as MPX records.
    */
   private void write() throws IOException
   {
      writeFileCreationRecord();
      writeProjectHeader(m_projectFile.getProjectProperties());

      if (!m_projectFile.getResources().isEmpty())
      {
         m_resourceModel = new ResourceModel(m_projectFile, m_locale, m_userDefinedFieldMap);
         m_writer.write(m_resourceModel.toString());
         for (Resource resource : m_projectFile.getResources())
         {
            writeResource(resource);
         }
      }

      if (!m_projectFile.getTasks().isEmpty())
      {
         m_taskModel = new TaskModel(m_projectFile, m_locale, m_userDefinedFieldMap);
         m_writer.write(m_taskModel.toString());
         writeTasks(m_projectFile.getChildTasks());
      }

      m_writer.flush();
   }

   /**
    * Write file creation record.
    */
   private void writeFileCreationRecord() throws IOException
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      m_buffer.setLength(0);
      m_buffer.append("MPX");
      m_buffer.append(m_delimiter);
      m_buffer.append(properties.getMpxProgramName());
      m_buffer.append(m_delimiter);
      m_buffer.append(properties.getMpxFileVersion());
      m_buffer.append(m_delimiter);
      m_buffer.append(properties.getMpxCodePage());
      m_buffer.append(MPXConstants.EOL);
      m_writer.write(m_buffer.toString());
   }

   /**
    * Write project header.
    *
    * @param properties project properties
    */
   private void writeProjectHeader(ProjectProperties properties) throws IOException
   {
      m_buffer.setLength(0);

      //
      // Currency Settings Record
      //
      m_buffer.append(MPXConstants.CURRENCY_SETTINGS_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getCurrencySymbol()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getSymbolPosition()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getCurrencyDigits()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(Character.valueOf(properties.getThousandsSeparator())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(Character.valueOf(properties.getDecimalSeparator())));
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);

      //
      // Default Settings Record
      //
      m_buffer.append(MPXConstants.DEFAULT_SETTINGS_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(Integer.valueOf(properties.getDefaultDurationUnits().getValue())));
      m_buffer.append(m_delimiter);
      m_buffer.append(properties.getDefaultDurationIsFixed() ? "1" : "0");
      m_buffer.append(m_delimiter);
      m_buffer.append(format(Integer.valueOf(properties.getDefaultWorkUnits().getValue())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDecimal(NumberHelper.getDouble(properties.getMinutesPerDay()) / 60)));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDecimal(NumberHelper.getDouble(properties.getMinutesPerWeek()) / 60)));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatRate(properties.getDefaultStandardRate())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatRate(properties.getDefaultOvertimeRate())));
      m_buffer.append(m_delimiter);
      m_buffer.append(properties.getUpdatingTaskStatusUpdatesResourceStatus() ? "1" : "0");
      m_buffer.append(m_delimiter);
      m_buffer.append(properties.getSplitInProgressTasks() ? "1" : "0");
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);

      //
      // Date Time Settings Record
      //
      m_buffer.append(MPXConstants.DATE_TIME_SETTINGS_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getDateOrder()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getTimeFormat()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(getIntegerTimeInMinutes(properties.getDefaultStartTime())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(Character.valueOf(properties.getDateSeparator())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(Character.valueOf(properties.getTimeSeparator())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getAMText()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getPMText()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getDateFormat()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getBarTextDateFormat()));
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);
      m_writer.write(m_buffer.toString());

      //
      // Write project calendars
      //
      for (ProjectCalendar cal : m_projectFile.getCalendars())
      {
         if (!isResourceCalendar(cal))
         {
            writeBaseCalendar(normalizeBaseCalendar(cal));
         }
      }

      ProjectCalendar defaultCalendar = m_projectFile.getDefaultCalendar();
      String defaultCalendarName = defaultCalendar == null ? null : m_calendarNameMap.get(m_calendarMapper.getUniqueID(defaultCalendar));

      //
      // Project Header Record
      //
      m_buffer.setLength(0);
      m_buffer.append(MPXConstants.PROJECT_HEADER_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getProjectTitle()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getCompany()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getManager()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(defaultCalendarName));
      m_buffer.append(m_delimiter);

      m_buffer.append(format(formatDateTime(properties.getStartDate())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(properties.getFinishDate())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getScheduleFrom()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(properties.getCurrentDate())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getComments()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatCurrency(properties.getCost())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatCurrency(properties.getBaselineCost())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatCurrency(properties.getActualCost())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getWork())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getBaselineWork())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getActualWork())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatPercentage(properties.getWork2())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getDuration())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getBaselineDuration())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getActualDuration())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatPercentage(properties.getPercentageComplete())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(properties.getBaselineStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(properties.getBaselineFinish())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(properties.getActualStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(properties.getActualFinish())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getStartVariance())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(properties.getFinishVariance())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getSubject()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getAuthor()));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(properties.getKeywords()));
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);

      m_writer.write(m_buffer.toString());
   }

   /**
    * Write a base calendar.
    *
    * @param record calendar instance
    */
   private void writeBaseCalendar(ProjectCalendar record) throws IOException
   {
      writeCalendarDetail(MPXConstants.BASE_CALENDAR_RECORD_NUMBER, record);
   }

   /**
    * Write a resource calendar.
    *
    * @param record calendar instance
    */
   private void writeResourceCalendar(ProjectCalendar record) throws IOException
   {
      writeCalendarDetail(MPXConstants.RESOURCE_CALENDAR_RECORD_NUMBER, record);
   }

   /**
    * Write a calendar.
    *
    * @param recordNumber record number rep[resenting calendar type
    * @param record calendar data
    */
   private void writeCalendarDetail(int recordNumber, ProjectCalendar record) throws IOException
   {
      String name = m_calendarNameMap.get(record.getParent() == null ? m_calendarMapper.getUniqueID(record) : m_calendarMapper.getUniqueID(record.getParent()));

      m_buffer.setLength(0);
      m_buffer.append(recordNumber);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(name));

      for (DayOfWeek day : DayOfWeekHelper.ORDERED_DAYS)
      {
         DayType type = record.getCalendarDayType(day);
         if (type == null)
         {
            type = DayType.DEFAULT;
         }
         m_buffer.append(m_delimiter);
         m_buffer.append(type.getValue());
      }

      m_buffer.append(MPXConstants.EOL);
      m_writer.write(m_buffer.toString());

      for (DayOfWeek day : DayOfWeekHelper.ORDERED_DAYS)
      {
         ProjectCalendarHours hours = record.getCalendarHours(day);
         if (hours != null)
         {
            writeCalendarHours(record, day, hours);
         }
      }

      List<ProjectCalendarException> expandedExceptions = record.getExpandedCalendarExceptionsWithWorkWeeks();
      if (!expandedExceptions.isEmpty())
      {
         //
         // A quirk of MS Project is that these exceptions must be
         // in date order in the file, otherwise they are ignored.
         //
         for (ProjectCalendarException ex : expandedExceptions)
         {
            writeCalendarException(record, ex);
         }
      }

      m_eventManager.fireCalendarWrittenEvent(record);
   }

   /**
    * Write calendar hours.
    *
    * @param parentCalendar parent calendar instance
    * @param day day to which these hours are attached
    * @param record calendar hours instance
    */
   private void writeCalendarHours(ProjectCalendar parentCalendar, DayOfWeek day, ProjectCalendarHours record) throws IOException
   {
      m_buffer.setLength(0);

      int recordNumber;

      if (!parentCalendar.isDerived())
      {
         recordNumber = MPXConstants.BASE_CALENDAR_HOURS_RECORD_NUMBER;
      }
      else
      {
         recordNumber = MPXConstants.RESOURCE_CALENDAR_HOURS_RECORD_NUMBER;
      }

      LocalTimeRange range1 = record.get(0);
      if (range1 == null)
      {
         range1 = LocalTimeRange.EMPTY_RANGE;
      }

      LocalTimeRange range2 = record.get(1);
      if (range2 == null)
      {
         range2 = LocalTimeRange.EMPTY_RANGE;
      }

      LocalTimeRange range3 = record.get(2);
      if (range3 == null)
      {
         range3 = LocalTimeRange.EMPTY_RANGE;
      }

      m_buffer.append(recordNumber);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(day));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(range1.getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(range1.getEnd())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(range2.getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(range2.getEnd())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(range3.getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(range3.getEnd())));
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);

      m_writer.write(m_buffer.toString());
   }

   /**
    * Write a calendar exception.
    *
    * @param parentCalendar parent calendar instance
    * @param record calendar exception instance
    */
   private void writeCalendarException(ProjectCalendar parentCalendar, ProjectCalendarException record) throws IOException
   {
      m_buffer.setLength(0);

      if (!parentCalendar.isDerived())
      {
         m_buffer.append(MPXConstants.BASE_CALENDAR_EXCEPTION_RECORD_NUMBER);
      }
      else
      {
         m_buffer.append(MPXConstants.RESOURCE_CALENDAR_EXCEPTION_RECORD_NUMBER);
      }
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDate(record.getFromDate())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDate(record.getToDate())));
      m_buffer.append(m_delimiter);
      m_buffer.append(record.getWorking() ? "1" : "0");
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.get(0).getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.get(0).getEnd())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.get(1).getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.get(1).getEnd())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.get(2).getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.get(2).getEnd())));
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);

      m_writer.write(m_buffer.toString());
   }

   /**
    * Write a resource.
    *
    * @param record resource instance
    */
   private void writeResource(Resource record) throws IOException
   {
      m_buffer.setLength(0);

      //
      // Write the resource record
      //
      int[] fields = m_resourceModel.getModel();

      m_buffer.append(MPXConstants.RESOURCE_RECORD_NUMBER);
      for (int mpxFieldType : fields)
      {
         if (mpxFieldType == -1)
         {
            break;
         }

         FieldType resourceField = m_userDefinedFieldMap.getSource(MPXResourceField.getMpxjField(mpxFieldType));
         Object value = resourceField == ResourceField.UNIQUE_ID ? m_resourceMapper.getUniqueID(record) : record.get(resourceField);
         value = formatType(resourceField.getDataType(), value);

         m_buffer.append(m_delimiter);
         m_buffer.append(format(value));
      }

      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);
      m_writer.write(m_buffer.toString());

      //
      // Write the resource notes
      //
      String notes = record.getNotes();
      if (!notes.isEmpty())
      {
         writeNotes(MPXConstants.RESOURCE_NOTES_RECORD_NUMBER, notes);
      }

      //
      // Write the resource calendar
      //
      if (record.getCalendar() != null)
      {
         writeResourceCalendar(normalizeResourceCalendar(record, record.getCalendar()));
      }

      m_eventManager.fireResourceWrittenEvent(record);
   }

   /**
    * Determine if this is a valid resource calendar.
    *
    * @param calendar calendar to test
    * @return true if this is a valid resource calendar
    */
   private boolean isResourceCalendar(ProjectCalendar calendar)
   {
      // We treat this as a resource calendar if:
      // 1. It is a derived calendar
      // 2. It's not the base calendar for any other derived calendars
      // 3. It is associated with exactly one resource
      return calendar != null && calendar.isDerived() && calendar.getDerivedCalendars().isEmpty() && m_resourceCalendarMap.computeIfAbsent(m_calendarMapper.getUniqueID(calendar), k -> Collections.emptyList()).size() == 1;
   }

   /**
    * A base calendar cannot be derived from another calendar.
    * If the current calendar is derived, create a temporary flattened
    * version which is functionally equivalent.
    *
    * @param calendar base calendar
    * @return normalized base calendar
    */
   private ProjectCalendar normalizeBaseCalendar(ProjectCalendar calendar)
   {
      //
      // Ensure all base calendars have a name
      //
      String name = calendar.getName();
      if (name == null || name.isEmpty())
      {
         name = "Calendar";
      }

      //
      // Ensure all base calendar names are unique
      //
      if (m_calendarNameSet.contains(name))
      {
         int index = 1;
         String newName;
         do
         {
            newName = name + " " + (index++);
         }
         while (m_calendarNameSet.contains(newName));

         name = newName;
      }

      m_calendarNameSet.add(name);
      m_calendarNameMap.put(m_calendarMapper.getUniqueID(calendar), name);

      //
      // Flatten calendar if required
      //
      ProjectCalendar result;
      if (calendar.isDerived())
      {
         result = ProjectCalendarHelper.createTemporaryFlattenedCalendar(calendar);
         m_calendarNameMap.put(m_calendarMapper.getUniqueID(result), name);
      }
      else
      {
         result = calendar;
      }

      return result;
   }

   /**
    * If we have a resource which shares a calendar with other resources,
    * * or the resource uses a base calendar directly, then
    * we need to create a temporary derived calendar to ensure the generated
    * MPX file conforms to MS Project's expectations.
    *
    * @param resource Resource instance
    * @param calendar calendar linked to the resource
    * @return normalized resource calendar
    */
   private ProjectCalendar normalizeResourceCalendar(Resource resource, ProjectCalendar calendar)
   {
      ProjectCalendar result;
      if (isResourceCalendar(calendar))
      {
         // We have a derived calendar associated with just one resource
         result = calendar;
      }
      else
      {
         result = ProjectCalendarHelper.createTemporaryDerivedCalendar(calendar, resource);
      }
      return result;
   }

   /**
    * Write notes.
    *
    * @param recordNumber record number
    * @param text note text
    */
   private void writeNotes(int recordNumber, String text) throws IOException
   {
      m_buffer.setLength(0);

      m_buffer.append(recordNumber);
      m_buffer.append(m_delimiter);

      if (text != null)
      {
         String note = stripLineBreaks(text, MPXConstants.EOL_PLACEHOLDER_STRING);
         boolean quote = (note.indexOf(m_delimiter) != -1 || note.indexOf('"') != -1);
         int length = note.length();
         char c;

         if (quote)
         {
            m_buffer.append('"');
         }

         for (int loop = 0; loop < length; loop++)
         {
            c = note.charAt(loop);

            if (c == '"')
            {
               m_buffer.append("\"\"");
            }
            else
            {
               m_buffer.append(c);
            }
         }

         if (quote)
         {
            m_buffer.append('"');
         }
      }

      m_buffer.append(MPXConstants.EOL);

      m_writer.write(m_buffer.toString());
   }

   /**
    * Write a task.
    *
    * @param record task instance
    */
   private void writeTask(Task record) throws IOException
   {
      m_buffer.setLength(0);

      //
      // Write the task
      //
      int[] fields = m_taskModel.getModel();
      int field;

      m_buffer.append(MPXConstants.TASK_RECORD_NUMBER);
      for (int i : fields)
      {
         field = i;
         if (field == -1)
         {
            break;
         }

         FieldType taskField = m_userDefinedFieldMap.getSource(MPXTaskField.getMpxjField(field));
         Object value = taskField == TaskField.UNIQUE_ID ? m_taskMapper.getUniqueID(record) : record.get(taskField);
         value = formatType(taskField.getDataType(), value);

         m_buffer.append(m_delimiter);
         m_buffer.append(format(value));
      }

      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);
      m_writer.write(m_buffer.toString());

      //
      // Write the task notes
      //
      String notes = record.getNotes();
      if (!notes.isEmpty())
      {
         writeNotes(MPXConstants.TASK_NOTES_RECORD_NUMBER, notes);
      }

      //
      // Write the recurring task
      //
      if (record.getRecurringTask() != null)
      {
         writeRecurringTask(record.getRecurringTask());
      }

      //
      // Write any resource assignments
      //
      if (!record.getResourceAssignments().isEmpty())
      {
         Set<Integer> resources = new HashSet<>();
         for (ResourceAssignment assignment : record.getResourceAssignments())
         {
            if (assignment.getResource() != null)
            {
               // As we now allow a resource to be assigned multiple times to a task
               // we need to handle this for file formats which allow a resource to be
               // assigned only once. The code below attempts to preserve the original
               // behaviour when we ignored multiple assignments of the same resource.
               // TODO: implement more intelligent rollup of multiple resource assignments
               if (!resources.add(assignment.getResourceUniqueID()))
               {
                  continue;
               }
               writeResourceAssignment(assignment);
            }
         }
      }

      m_eventManager.fireTaskWrittenEvent(record);
   }

   /**
    * Write a recurring task.
    *
    * @param record recurring task instance
    */
   private void writeRecurringTask(RecurringTask record) throws IOException
   {
      m_buffer.setLength(0);

      m_buffer.append(MPXConstants.RECURRING_TASK_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append("1");

      if (record.getRecurrenceType() != null)
      {
         boolean monthlyRelative = record.getRecurrenceType() == RecurrenceType.MONTHLY && record.getRelative();
         boolean monthlyAbsolute = record.getRecurrenceType() == RecurrenceType.MONTHLY && !record.getRelative();
         boolean yearlyRelative = record.getRecurrenceType() == RecurrenceType.YEARLY && record.getRelative();
         boolean yearlyAbsolute = record.getRecurrenceType() == RecurrenceType.YEARLY && !record.getRelative();

         m_buffer.append(m_delimiter);
         m_buffer.append(format(formatDateTime(record.getStartDate())));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(formatDateTime(record.getFinishDate())));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(RecurrenceUtility.getDurationValue(m_projectFile.getProjectProperties(), record.getDuration())));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(RecurrenceUtility.getDurationUnits(record)));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(record.getOccurrences()));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(RecurrenceUtility.getRecurrenceValue(record.getRecurrenceType())));
         m_buffer.append(m_delimiter);
         m_buffer.append("0");
         m_buffer.append(m_delimiter);
         m_buffer.append(record.getUseEndDate() ? "1" : "0");
         m_buffer.append(m_delimiter);
         m_buffer.append(record.getWorkingDaysOnly() ? "1" : "0");
         m_buffer.append(m_delimiter);
         m_buffer.append(format(RecurrenceUtility.getDays(record)));
         m_buffer.append(m_delimiter);
         m_buffer.append(monthlyRelative ? "1" : "0");
         m_buffer.append(m_delimiter);
         m_buffer.append(yearlyAbsolute ? "1" : "0");
         m_buffer.append(m_delimiter);
         m_buffer.append(format(record.getRecurrenceType() == RecurrenceType.DAILY ? record.getFrequency() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(record.getRecurrenceType() == RecurrenceType.WEEKLY ? record.getFrequency() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(monthlyRelative ? record.getDayNumber() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(RecurrenceUtility.getDay(monthlyRelative ? record.getDayOfWeek() : DayOfWeek.MONDAY)));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(monthlyRelative ? record.getFrequency() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(monthlyAbsolute ? record.getDayNumber() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(monthlyAbsolute ? record.getFrequency() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(yearlyRelative ? record.getDayNumber() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(RecurrenceUtility.getDay(yearlyRelative ? record.getDayOfWeek() : DayOfWeek.MONDAY)));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(record.getMonthNumber()));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(formatDateTime(RecurrenceUtility.getYearlyAbsoluteAsDate(record))));

         stripTrailingDelimiters(m_buffer);
      }
      m_buffer.append(MPXConstants.EOL);

      m_writer.write(m_buffer.toString());
   }

   /**
    * Write resource assignment.
    *
    * @param record resource assignment instance
    */
   private void writeResourceAssignment(ResourceAssignment record) throws IOException
   {
      m_buffer.setLength(0);

      m_buffer.append(MPXConstants.RESOURCE_ASSIGNMENT_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append(record.getResource().getID());
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatUnits(record.getUnits())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(record.getWork())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(record.getBaselineWork())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(record.getActualWork())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(record.getOvertimeWork())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatCurrency(record.getCost())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatCurrency(record.getBaselineCost())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatCurrency(record.getActualCost())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(record.getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTime(record.getFinish())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDuration(record.getDelay())));
      m_buffer.append(m_delimiter);
      m_buffer.append(m_resourceMapper.getUniqueID(record.getResource()));
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);
      m_writer.write(m_buffer.toString());

      ResourceAssignmentWorkgroupFields workgroup = record.getWorkgroupAssignment();
      if (workgroup == null)
      {
         workgroup = ResourceAssignmentWorkgroupFields.EMPTY;
      }
      writeResourceAssignmentWorkgroupFields(workgroup);

      m_eventManager.fireAssignmentWrittenEvent(record);
   }

   /**
    * Write resource assignment workgroup.
    *
    * @param record resource assignment workgroup instance
    */
   private void writeResourceAssignmentWorkgroupFields(ResourceAssignmentWorkgroupFields record) throws IOException
   {
      m_buffer.setLength(0);

      m_buffer.append(MPXConstants.RESOURCE_ASSIGNMENT_WORKGROUP_FIELDS_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(record.getMessageUniqueID()));
      m_buffer.append(m_delimiter);
      m_buffer.append(record.getConfirmed() ? "1" : "0");
      m_buffer.append(m_delimiter);
      m_buffer.append(record.getResponsePending() ? "1" : "0");
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTimeNull(record.getUpdateStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatDateTimeNull(record.getUpdateFinish())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(record.getScheduleID()));

      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);

      m_writer.write(m_buffer.toString());
   }

   /**
    * Recursively write tasks.
    *
    * @param tasks list of tasks
    */
   private void writeTasks(List<Task> tasks) throws IOException
   {
      for (Task task : tasks)
      {
         writeTask(task);
         writeTasks(task.getChildTasks());
      }
   }

   /**
    * This internal method is used to convert from a Date instance to an
    * integer representing the number of minutes past midnight.
    *
    * @param time date instance
    * @return minutes past midnight as an integer
    */
   private Integer getIntegerTimeInMinutes(LocalTime time)
   {
      Integer result = null;
      if (time != null)
      {
         result = Integer.valueOf(time.toSecondOfDay() / 60);
      }
      return result;
   }

   /**
    * This method is called when double quotes are found as part of
    * a value. The quotes are escaped by adding a second quote character
    * and the entire value is quoted.
    *
    * @param value text containing quote characters
    * @return escaped and quoted text
    */
   private String escapeQuotes(String value)
   {
      StringBuilder sb = new StringBuilder();
      int length = value.length();
      char c;

      sb.append('"');
      for (int index = 0; index < length; index++)
      {
         c = value.charAt(index);
         sb.append(c);

         if (c == '"')
         {
            sb.append('"');
         }
      }
      sb.append('"');

      return (sb.toString());
   }

   /**
    * This method removes line breaks from a piece of text, and replaces
    * them with the supplied text.
    *
    * @param text source text
    * @param replacement line break replacement text
    * @return text with line breaks removed.
    */
   private String stripLineBreaks(String text, String replacement)
   {
      if (text.indexOf('\r') != -1 || text.indexOf('\n') != -1)
      {
         StringBuilder sb = new StringBuilder(text);

         int index;

         while ((index = sb.indexOf("\r\n")) != -1)
         {
            sb.replace(index, index + 2, replacement);
         }

         while ((index = sb.indexOf("\n\r")) != -1)
         {
            sb.replace(index, index + 2, replacement);
         }

         while ((index = sb.indexOf("\r")) != -1)
         {
            sb.replace(index, index + 1, replacement);
         }

         while ((index = sb.indexOf("\n")) != -1)
         {
            sb.replace(index, index + 1, replacement);
         }

         text = sb.toString();
      }

      return (text);
   }

   /**
    * This method returns the string representation of an object. In most
    * cases this will simply involve calling the normal toString method
    * on the object, but a couple of exceptions are handled here.
    *
    * @param o the object to formatted
    * @return formatted string representing input Object
    */
   private String format(Object o)
   {
      String result;

      if (o == null)
      {
         result = "";
      }
      else
      {
         if ((o instanceof Boolean))
         {
            result = LocaleData.getString(m_locale, (((Boolean) o).booleanValue() ? LocaleData.YES : LocaleData.NO));
         }
         else
         {
            if ((o instanceof Float) || (o instanceof Double))
            {
               result = (m_formats.getDecimalFormat().format(((Number) o).doubleValue()));
            }
            else
            {
               if (o instanceof DayOfWeek)
               {
                  result = Integer.toString(DayOfWeekHelper.getValue((DayOfWeek) o));
               }
               else
               {
                  result = o.toString();
               }
            }
         }

         //
         // At this point there should be no line break characters in
         // the file. If we find any, replace them with spaces
         //
         result = stripLineBreaks(result, MPXConstants.EOL_PLACEHOLDER_STRING);

         //
         // Finally, we check to ensure that there are no embedded
         // quotes or separator characters in the value. If there are, then
         // we quote the value and escape any existing quote characters.
         //
         if (result.indexOf('"') != -1)
         {
            result = escapeQuotes(result);
         }
         else
         {
            if (result.indexOf(m_delimiter) != -1)
            {
               result = '"' + result + '"';
            }
         }
      }

      return (result);
   }

   /**
    * This method removes trailing delimiter characters.
    *
    * @param buffer input string buffer
    */
   private void stripTrailingDelimiters(StringBuilder buffer)
   {
      int index = buffer.length() - 1;

      while (index > 0 && buffer.charAt(index) == m_delimiter)
      {
         --index;
      }

      buffer.setLength(index + 1);
   }

   /**
    * This method is called to format a time value.
    *
    * @param value time value
    * @return formatted time value
    */
   private String formatTime(LocalTime value)
   {
      return m_formats.printTime(value);
   }

   /**
    * This method is called to format a currency value.
    *
    * @param value numeric value
    * @return currency value
    */
   private String formatCurrency(Number value)
   {
      return (value == null ? null : m_formats.getCurrencyFormat().format(value));
   }

   /**
    * This method is called to format a units value.
    *
    * @param value numeric value
    * @return currency value
    */
   private String formatUnits(Number value)
   {
      return (value == null ? null : m_formats.getUnitsDecimalFormat().format(value.doubleValue() / 100));
   }

   private String formatDateTime(Object value)
   {
      if (value instanceof LocalDateTime)
      {
         return formatDateTime((LocalDateTime) value);
      }

      if (value instanceof LocalDate)
      {
         return formatDateTime((LocalDate) value);
      }

      return null;
   }

   /**
    * This method is called to format a date.
    *
    * @param value date value
    * @return formatted date value
    */
   private String formatDateTime(TemporalAccessor value)
   {
      return m_formats.printDateTime(value);
   }

   /**
    * This method is called to format a date. It will return the null text
    * if a null value is supplied.
    *
    * @param value date value
    * @return formatted date value
    */
   private String formatDateTimeNull(LocalDateTime value)
   {
      return value == null ? m_formats.getNullText() : m_formats.printDateTime(value);
   }

   /**
    * This method is called to format a date.
    *
    * @param value date value
    * @return formatted date value
    */
   private String formatDate(LocalDate value)
   {
      return m_formats.printDate(value);
   }

   /**
    * This method is called to format a percentage value.
    *
    * @param value numeric value
    * @return percentage value
    */
   private String formatPercentage(Number value)
   {
      return (value == null ? null : m_formats.getPercentageDecimalFormat().format(value) + "%");
   }

   /**
    * This method is called to format an accrue type value.
    *
    * @param type accrue type
    * @return formatted accrue type
    */
   private String formatAccrueType(AccrueType type)
   {
      return (type == null ? null : LocaleData.getStringArray(m_locale, LocaleData.ACCRUE_TYPES)[type.getValue() - 1]);
   }

   /**
    * This method is called to format a constraint type.
    *
    * @param type constraint type
    * @return formatted constraint type
    */
   private String formatConstraintType(ConstraintType type)
   {
      if (type == null)
      {
         return null;
      }

      switch (type)
      {
         case START_ON:
         {
            type = ConstraintType.MUST_START_ON;
            break;
         }

         case FINISH_ON:
         {
            type = ConstraintType.MUST_FINISH_ON;
            break;
         }

         default:
         {
            break;
         }
      }

      return LocaleData.getStringArray(m_locale, LocaleData.CONSTRAINT_TYPES)[type.getValue()];
   }

   /**
    * This method is called to format a duration.
    *
    * @param value duration value
    * @return formatted duration value
    */
   private String formatDuration(Object value)
   {
      String result = null;
      if (value instanceof Duration)
      {
         Duration duration = (Duration) value;
         result = m_formats.getDurationDecimalFormat().format(duration.getDuration()) + formatTimeUnit(duration.getUnits());
      }
      return result;
   }

   /**
    * This method is called to format a rate.
    *
    * @param value rate value
    * @return formatted rate
    */
   private String formatRate(Rate value)
   {
      String result = null;
      if (value != null)
      {
         result = m_formats.getCurrencyFormat().format(value.getAmount()) + "/" + formatTimeUnit(value.getUnits());
      }
      return (result);
   }

   /**
    * This method is called to format a priority.
    *
    * @param value priority instance
    * @return formatted priority value
    */
   private String formatPriority(Priority value)
   {
      String result = null;

      if (value != null)
      {
         String[] priorityTypes = LocaleData.getStringArray(m_locale, LocaleData.PRIORITY_TYPES);
         int priority = value.getValue();
         if (priority < Priority.LOWEST)
         {
            priority = Priority.LOWEST;
         }
         else
         {
            if (priority > Priority.DO_NOT_LEVEL)
            {
               priority = Priority.DO_NOT_LEVEL;
            }
         }

         priority /= 100;

         result = priorityTypes[priority - 1];
      }

      return (result);
   }

   /**
    * This method is called to format a task type.
    *
    * @param value task type value
    * @return formatted task type
    */
   private String formatTaskType(TaskType value)
   {
      return (LocaleData.getString(m_locale, (value == TaskType.FIXED_DURATION || value == TaskType.FIXED_DURATION_AND_UNITS ? LocaleData.YES : LocaleData.NO)));
   }

   /**
    * This method is called to format a relation list.
    *
    * @param value relation list instance
    * @return formatted relation list
    */
   private String formatRelationList(List<Relation> value)
   {
      String result = null;

      if (value != null && !value.isEmpty())
      {
         StringBuilder sb = new StringBuilder();
         for (Relation relation : value)
         {
            if (sb.length() != 0)
            {
               sb.append(m_delimiter);
            }

            sb.append(formatRelation(relation));
         }

         result = sb.toString();
      }

      return (result);
   }

   /**
    * This method is called to format a relation.
    *
    * @param relation relation instance
    * @return formatted relation instance
    */
   private String formatRelation(Relation relation)
   {
      String result = null;

      if (relation != null)
      {
         StringBuilder sb = new StringBuilder(relation.getPredecessorTask().getID().toString());

         Duration duration = relation.getLag();
         RelationType type = relation.getType();
         double durationValue = duration.getDuration();

         if ((durationValue != 0) || (type != RelationType.FINISH_START))
         {
            String[] typeNames = LocaleData.getStringArray(m_locale, LocaleData.RELATION_TYPES);
            sb.append(typeNames[type.getValue()]);
         }

         if (durationValue != 0)
         {
            if (durationValue > 0)
            {
               sb.append('+');
            }

            sb.append(formatDuration(duration));
         }

         result = sb.toString();
      }

      m_eventManager.fireRelationWrittenEvent(relation);
      return (result);
   }

   /**
    * This method formats a time unit.
    *
    * @param timeUnit time unit instance
    * @return formatted time unit instance
    */
   private String formatTimeUnit(TimeUnit timeUnit)
   {
      int units = timeUnit.getValue();
      String result;
      String[][] unitNames = LocaleData.getStringArrays(m_locale, LocaleData.TIME_UNITS_ARRAY);

      if (units < 0 || units >= unitNames.length)
      {
         result = "";
      }
      else
      {
         result = unitNames[units][0];
      }

      return (result);
   }

   /**
    * This method formats a decimal value.
    *
    * @param value value
    * @return formatted value
    */
   private String formatDecimal(double value)
   {
      return (m_formats.getDecimalFormat().format(value));
   }

   /**
    * Converts a value to the appropriate type.
    *
    * @param type target type
    * @param value input value
    * @return output value
    */
   @SuppressWarnings("unchecked") private Object formatType(DataType type, Object value)
   {
      switch (type)
      {
         case DATE:
         {
            value = formatDateTime(value);
            break;
         }

         case CURRENCY:
         {
            value = formatCurrency((Number) value);
            break;
         }

         case UNITS:
         {
            value = formatUnits((Number) value);
            break;
         }

         case PERCENTAGE:
         {
            value = formatPercentage((Number) value);
            break;
         }

         case ACCRUE:
         {
            value = formatAccrueType((AccrueType) value);
            break;
         }

         case CONSTRAINT:
         {
            value = formatConstraintType((ConstraintType) value);
            break;
         }

         case WORK:
         case DURATION:
         {
            value = formatDuration(value);
            break;
         }

         case RATE:
         {
            value = formatRate((Rate) value);
            break;
         }

         case PRIORITY:
         {
            value = formatPriority((Priority) value);
            break;
         }

         case RELATION_LIST:
         {
            value = formatRelationList((List<Relation>) value);
            break;
         }

         case TASK_TYPE:
         {
            value = formatTaskType((TaskType) value);
            break;
         }

         default:
         {
            break;
         }
      }

      return (value);
   }

   /**
    * This method returns the locale used by this MPX file.
    *
    * @return current locale
    */
   public Locale getLocale()
   {
      return (m_locale);
   }

   /**
    * This method sets the locale to be used by this MPX file.
    *
    * @param locale locale to be used
    */
   public void setLocale(Locale locale)
   {
      m_locale = locale;
   }

   /**
    * Retrieves a flag indicating if the default settings for the locale should
    * override any project settings.
    *
    * @return boolean flag.
    */
   public boolean getUseLocaleDefaults()
   {
      return m_useLocaleDefaults;
   }

   /**
    * Sets a flag indicating if the default settings for the locale should
    * override any project settings.
    *
    * @param useLocaleDefaults boolean flag
    */
   public void setUseLocaleDefaults(boolean useLocaleDefaults)
   {
      m_useLocaleDefaults = useLocaleDefaults;
   }

   /**
    * Retrieves an array of locales supported by this class.
    *
    * @return array of supported locales
    */
   public static Locale[] getSupportedLocales()
   {
      return (LocaleUtility.getSupportedLocales());
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private OutputStreamWriter m_writer;
   private ResourceModel m_resourceModel;
   private TaskModel m_taskModel;
   private char m_delimiter;
   private Locale m_locale = Locale.ENGLISH;
   private boolean m_useLocaleDefaults = true;
   private StringBuilder m_buffer;
   private MPXJFormats m_formats;

   private Set<String> m_calendarNameSet;
   private Map<Integer, String> m_calendarNameMap;
   private UserDefinedFieldMap m_userDefinedFieldMap;
   private Map<Integer, List<Resource>> m_resourceCalendarMap;

   private MicrosoftProjectUniqueIDMapper m_taskMapper;
   private MicrosoftProjectUniqueIDMapper m_resourceMapper;
   private MicrosoftProjectUniqueIDMapper m_calendarMapper;

   private static final List<FieldType> MAPPING_TARGET_CUSTOM_FIELDS = new ArrayList<>();
   static
   {
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(MPXTaskField.CUSTOM_FIELDS);
      MAPPING_TARGET_CUSTOM_FIELDS.addAll(MPXResourceField.CUSTOM_FIELDS);
   }
}
