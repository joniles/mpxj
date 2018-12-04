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

package net.sf.mpxj.mpx;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DataType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringTask;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceAssignmentWorkgroupFields;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new MPX file from the contents of
 * a ProjectFile instance.
 */
public final class MPXWriter extends AbstractProjectWriter
{
   /**
    * {@inheritDoc}
    */
   @Override public void write(ProjectFile projectFile, OutputStream out) throws IOException
   {
      m_projectFile = projectFile;
      m_eventManager = projectFile.getEventManager();

      if (m_useLocaleDefaults == true)
      {
         LocaleUtility.setLocale(m_projectFile.getProjectProperties(), m_locale);
      }

      m_delimiter = projectFile.getProjectProperties().getMpxDelimiter();
      m_writer = new OutputStreamWriter(new BufferedOutputStream(out), projectFile.getProjectProperties().getMpxCodePage().getCharset());
      m_buffer = new StringBuilder();
      m_formats = new MPXJFormats(m_locale, LocaleData.getString(m_locale, LocaleData.NA), m_projectFile);

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
      }
   }

   /**
    * Writes the contents of the project file as MPX records.
    *
    * @throws IOException
    */
   private void write() throws IOException
   {
      m_projectFile.validateUniqueIDsForMicrosoftProject();

      writeFileCreationRecord();
      writeProjectHeader(m_projectFile.getProjectProperties());

      if (m_projectFile.getResources().isEmpty() == false)
      {
         m_resourceModel = new ResourceModel(m_projectFile, m_locale);
         m_writer.write(m_resourceModel.toString());
         for (Resource resource : m_projectFile.getResources())
         {
            writeResource(resource);
         }
      }

      if (m_projectFile.getTasks().isEmpty() == false)
      {
         m_taskModel = new TaskModel(m_projectFile, m_locale);
         m_writer.write(m_taskModel.toString());
         writeTasks(m_projectFile.getChildTasks());
      }

      m_writer.flush();
   }

   /**
    * Write file creation record.
    *
    * @throws IOException
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
    * @throws IOException
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
         writeCalendar(cal);
      }

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
      m_buffer.append(format(properties.getDefaultCalendarName()));
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
    * Write a calendar.
    *
    * @param record calendar instance
    * @throws IOException
    */
   private void writeCalendar(ProjectCalendar record) throws IOException
   {
      //
      // Test used to ensure that we don't write the default calendar used for the "Unassigned" resource
      //
      if (record.getParent() == null || record.getResource() != null)
      {
         m_buffer.setLength(0);

         if (record.getParent() == null)
         {
            m_buffer.append(MPXConstants.BASE_CALENDAR_RECORD_NUMBER);
            m_buffer.append(m_delimiter);
            if (record.getName() != null)
            {
               m_buffer.append(record.getName());
            }
         }
         else
         {
            m_buffer.append(MPXConstants.RESOURCE_CALENDAR_RECORD_NUMBER);
            m_buffer.append(m_delimiter);
            m_buffer.append(record.getParent().getName());
         }

         for (DayType day : record.getDays())
         {
            if (day == null)
            {
               day = DayType.DEFAULT;
            }
            m_buffer.append(m_delimiter);
            m_buffer.append(day.getValue());
         }

         m_buffer.append(MPXConstants.EOL);
         m_writer.write(m_buffer.toString());

         ProjectCalendarHours[] hours = record.getHours();
         for (int loop = 0; loop < hours.length; loop++)
         {
            if (hours[loop] != null)
            {
               writeCalendarHours(record, hours[loop]);
            }
         }

         if (!record.getCalendarExceptions().isEmpty())
         {
            //
            // A quirk of MS Project is that these exceptions must be
            // in date order in the file, otherwise they are ignored.
            // The getCalendarExceptions method now guarantees that
            // the exceptions list is sorted when retrieved.
            //
            for (ProjectCalendarException ex : record.getCalendarExceptions())
            {
               writeCalendarException(record, ex);
            }
         }

         m_eventManager.fireCalendarWrittenEvent(record);
      }
   }

   /**
    * Write calendar hours.
    *
    * @param parentCalendar parent calendar instance
    * @param record calendar hours instance
    * @throws IOException
    */
   private void writeCalendarHours(ProjectCalendar parentCalendar, ProjectCalendarHours record) throws IOException
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

      DateRange range1 = record.getRange(0);
      if (range1 == null)
      {
         range1 = DateRange.EMPTY_RANGE;
      }

      DateRange range2 = record.getRange(1);
      if (range2 == null)
      {
         range2 = DateRange.EMPTY_RANGE;
      }

      DateRange range3 = record.getRange(2);
      if (range3 == null)
      {
         range3 = DateRange.EMPTY_RANGE;
      }

      m_buffer.append(recordNumber);
      m_buffer.append(m_delimiter);
      m_buffer.append(format(record.getDay()));
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
    * @throws IOException
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
      m_buffer.append(format(formatTime(record.getRange(0).getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.getRange(0).getEnd())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.getRange(1).getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.getRange(1).getEnd())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.getRange(2).getStart())));
      m_buffer.append(m_delimiter);
      m_buffer.append(format(formatTime(record.getRange(2).getEnd())));
      stripTrailingDelimiters(m_buffer);
      m_buffer.append(MPXConstants.EOL);

      m_writer.write(m_buffer.toString());
   }

   /**
    * Write a resource.
    *
    * @param record resource instance
    * @throws IOException
    */
   private void writeResource(Resource record) throws IOException
   {
      m_buffer.setLength(0);

      //
      // Write the resource record
      //
      int[] fields = m_resourceModel.getModel();

      m_buffer.append(MPXConstants.RESOURCE_RECORD_NUMBER);
      for (int loop = 0; loop < fields.length; loop++)
      {
         int mpxFieldType = fields[loop];
         if (mpxFieldType == -1)
         {
            break;
         }

         ResourceField resourceField = MPXResourceField.getMpxjField(mpxFieldType);
         Object value = record.getCachedValue(resourceField);
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
      if (notes.length() != 0)
      {
         writeNotes(MPXConstants.RESOURCE_NOTES_RECORD_NUMBER, notes);
      }

      //
      // Write the resource calendar
      //
      if (record.getResourceCalendar() != null)
      {
         writeCalendar(record.getResourceCalendar());
      }

      m_eventManager.fireResourceWrittenEvent(record);
   }

   /**
    * Write notes.
    *
    * @param recordNumber record number
    * @param text note text
    * @throws IOException
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

         if (quote == true)
         {
            m_buffer.append('"');
         }

         for (int loop = 0; loop < length; loop++)
         {
            c = note.charAt(loop);

            switch (c)
            {
               case '"':
               {
                  m_buffer.append("\"\"");
                  break;
               }

               default:
               {
                  m_buffer.append(c);
                  break;
               }
            }
         }

         if (quote == true)
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
    * @throws IOException
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
      for (int loop = 0; loop < fields.length; loop++)
      {
         field = fields[loop];
         if (field == -1)
         {
            break;
         }

         TaskField taskField = MPXTaskField.getMpxjField(field);
         Object value = record.getCachedValue(taskField);
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
      if (notes.length() != 0)
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
      if (record.getResourceAssignments().isEmpty() == false)
      {
         for (ResourceAssignment assignment : record.getResourceAssignments())
         {
            writeResourceAssignment(assignment);
         }
      }

      m_eventManager.fireTaskWrittenEvent(record);
   }

   /**
    * Write a recurring task.
    *
    * @param record recurring task instance
    * @throws IOException
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
         m_buffer.append(record.isWorkingDaysOnly() ? "1" : "0");
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
         m_buffer.append(format(RecurrenceUtility.getDay(monthlyRelative ? record.getDayOfWeek() : Day.MONDAY)));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(monthlyRelative ? record.getFrequency() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(monthlyAbsolute ? record.getDayNumber() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(monthlyAbsolute ? record.getFrequency() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(yearlyRelative ? record.getDayNumber() : "1"));
         m_buffer.append(m_delimiter);
         m_buffer.append(format(RecurrenceUtility.getDay(yearlyRelative ? record.getDayOfWeek() : Day.MONDAY)));
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
    * @throws IOException
    */
   private void writeResourceAssignment(ResourceAssignment record) throws IOException
   {
      m_buffer.setLength(0);

      m_buffer.append(MPXConstants.RESOURCE_ASSIGNMENT_RECORD_NUMBER);
      m_buffer.append(m_delimiter);
      m_buffer.append(formatResource(record.getResource()));
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
      m_buffer.append(format(record.getResourceUniqueID()));
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
    * @throws IOException
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
    * @throws IOException
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
    * @param date date instance
    * @return minutes past midnight as an integer
    */
   private Integer getIntegerTimeInMinutes(Date date)
   {
      Integer result = null;
      if (date != null)
      {
         Calendar cal = DateHelper.popCalendar(date);
         int time = cal.get(Calendar.HOUR_OF_DAY) * 60;
         time += cal.get(Calendar.MINUTE);
         DateHelper.pushCalendar(cal);
         result = Integer.valueOf(time);         
      }
      return (result);
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
         if (o instanceof Boolean == true)
         {
            result = LocaleData.getString(m_locale, (((Boolean) o).booleanValue() == true ? LocaleData.YES : LocaleData.NO));
         }
         else
         {
            if (o instanceof Float == true || o instanceof Double == true)
            {
               result = (m_formats.getDecimalFormat().format(((Number) o).doubleValue()));
            }
            else
            {
               if (o instanceof Day)
               {
                  result = Integer.toString(((Day) o).getValue());
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
         // Finally we check to ensure that there are no embedded
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
    * @param buffer input sring buffer
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
   private String formatTime(Date value)
   {
      return (value == null ? null : m_formats.getTimeFormat().format(value));
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

   /**
    * This method is called to format a date.
    *
    * @param value date value
    * @return formatted date value
    */
   private String formatDateTime(Object value)
   {
      String result = null;
      if (value instanceof Date)
      {
         result = m_formats.getDateTimeFormat().format(value);
      }
      return result;
   }

   /**
    * This method is called to format a date. It will return the null text
    * if a null value is supplied.
    *
    * @param value date value
    * @return formatted date value
    */
   private String formatDateTimeNull(Date value)
   {
      return (value == null ? m_formats.getNullText() : m_formats.getDateTimeFormat().format(value));
   }

   /**
    * This method is called to format a date.
    *
    * @param value date value
    * @return formatted date value
    */
   private String formatDate(Date value)
   {
      return (value == null ? null : m_formats.getDateFormat().format(value));
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
      return (type == null ? null : LocaleData.getStringArray(m_locale, LocaleData.CONSTRAINT_TYPES)[type.getValue()]);
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
         StringBuilder buffer = new StringBuilder(m_formats.getCurrencyFormat().format(value.getAmount()));
         buffer.append("/");
         buffer.append(formatTimeUnit(value.getUnits()));
         result = buffer.toString();
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
      return (LocaleData.getString(m_locale, (value == TaskType.FIXED_DURATION ? LocaleData.YES : LocaleData.NO)));
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

      if (value != null && value.size() != 0)
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
         StringBuilder sb = new StringBuilder(relation.getTargetTask().getID().toString());

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
    * Formats a resource, taking into account that the resource reference
    * may be null.
    *
    * @param resource Resource instance
    * @return formatted value
    */
   private String formatResource(Resource resource)
   {
      return (resource == null ? "-65535" : format(resource.getID()));
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
   public Locale[] getSupportedLocales()
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
}
