/*
 * file:       MPXReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jan 3, 2006
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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FileVersion;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
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
import net.sf.mpxj.common.InputStreamTokenizer;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ReaderTokenizer;
import net.sf.mpxj.common.Tokenizer;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * This class creates a new ProjectFile instance by reading an MPX file.
 */
public final class MPXReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      int line = 1;

      try
      {
         //
         // Test the header and extract the separator. If this is successful,
         // we reset the stream back as far as we can. The design of the
         // BufferedInputStream class means that we can't get back to character
         // zero, so the first record we will read will get "PX" rather than
         // "MPX" in the first field position.
         //
         BufferedInputStream bis = new BufferedInputStream(is);
         byte[] data = new byte[4];
         data[0] = (byte) bis.read();
         bis.mark(1024);
         data[1] = (byte) bis.read();
         data[2] = (byte) bis.read();
         data[3] = (byte) bis.read();

         if ((data[0] != 'M') || (data[1] != 'P') || (data[2] != 'X'))
         {
            throw new MPXJException(MPXJException.INVALID_FILE);
         }

         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();

         m_projectConfig = m_projectFile.getProjectConfig();
         m_projectConfig.setAutoTaskID(false);
         m_projectConfig.setAutoTaskUniqueID(false);
         m_projectConfig.setAutoResourceID(false);
         m_projectConfig.setAutoResourceUniqueID(false);
         m_projectConfig.setAutoOutlineLevel(false);
         m_projectConfig.setAutoOutlineNumber(false);
         m_projectConfig.setAutoWBS(false);

         m_eventManager.addProjectListeners(m_projectListeners);

         LocaleUtility.setLocale(m_projectFile.getProjectProperties(), m_locale);
         m_delimiter = (char) data[3];
         m_projectFile.getProjectProperties().setMpxDelimiter(m_delimiter);
         m_taskModel = new TaskModel(m_projectFile, m_locale);
         m_taskModel.setLocale(m_locale);
         m_resourceModel = new ResourceModel(m_projectFile, m_locale);
         m_resourceModel.setLocale(m_locale);
         m_baseOutlineLevel = -1;
         m_formats = new MPXJFormats(m_locale, LocaleData.getString(m_locale, LocaleData.NA), m_projectFile);
         m_deferredRelationships = new LinkedList<DeferredRelationship>();

         bis.reset();

         //
         // Read the file creation record. At this point we are reading
         // directly from an input stream so no character set decoding is
         // taking place. We assume that any text in this record will not
         // require decoding.
         //
         Tokenizer tk = new InputStreamTokenizer(bis);
         tk.setDelimiter(m_delimiter);

         Record record;
         String number;

         //
         // Add the header record
         //
         parseRecord(Integer.toString(MPXConstants.FILE_CREATION_RECORD_NUMBER), new Record(m_locale, tk, m_formats));
         ++line;

         //
         // Now process the remainder of the file in full. As we have read the
         // file creation record we have access to the field which specifies the
         // codepage used to encode the character set in this file. We set up
         // an input stream reader using the appropriate character set, and
         // create a new tokenizer to read from this Reader instance.
         //
         InputStreamReader reader = new InputStreamReader(bis, m_projectFile.getProjectProperties().getMpxCodePage().getCharset());
         tk = new ReaderTokenizer(reader);
         tk.setDelimiter(m_delimiter);

         //
         // Read the remainder of the records
         //
         while (tk.getType() != Tokenizer.TT_EOF)
         {
            record = new Record(m_locale, tk, m_formats);
            number = record.getRecordNumber();

            if (number != null)
            {
               parseRecord(number, record);
            }

            ++line;
         }

         processDeferredRelationships();

         //
         // Ensure that the structure is consistent
         //
         m_projectFile.updateStructure();

         //
         // Ensure that the unique ID counters are correct
         //
         m_projectConfig.updateUniqueCounters();

         m_projectConfig.setAutoCalendarUniqueID(false);

         return (m_projectFile);
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR + " (failed at line " + line + ")", ex);
      }

      finally
      {
         m_projectFile = null;
         m_lastTask = null;
         m_lastResource = null;
         m_lastResourceCalendar = null;
         m_lastResourceAssignment = null;
         m_lastBaseCalendar = null;
         m_resourceTableDefinition = false;
         m_taskTableDefinition = false;
         m_taskModel = null;
         m_resourceModel = null;
         m_formats = null;
         m_deferredRelationships = null;
      }
   }

   /**
    * Parse an MPX record.
    *
    * @param recordNumber record number
    * @param record record data
    * @throws MPXJException
    */
   private void parseRecord(String recordNumber, Record record) throws MPXJException
   {
      switch (Integer.parseInt(recordNumber))
      {
         case MPXConstants.PROJECT_NAMES_RECORD_NUMBER:
         case MPXConstants.DDE_OLE_CLIENT_LINKS_RECORD_NUMBER:
         case MPXConstants.COMMENTS_RECORD_NUMBER:
         {
            // silently ignored
            break;
         }

         case MPXConstants.CURRENCY_SETTINGS_RECORD_NUMBER:
         {
            populateCurrencySettings(record, m_projectFile.getProjectProperties());
            m_formats.update();
            break;
         }

         case MPXConstants.DEFAULT_SETTINGS_RECORD_NUMBER:
         {
            populateDefaultSettings(record, m_projectFile.getProjectProperties());
            m_formats.update();
            break;
         }

         case MPXConstants.DATE_TIME_SETTINGS_RECORD_NUMBER:
         {
            populateDateTimeSettings(record, m_projectFile.getProjectProperties());
            m_formats.update();
            break;
         }

         case MPXConstants.BASE_CALENDAR_RECORD_NUMBER:
         {
            m_lastBaseCalendar = m_projectFile.addCalendar();
            populateCalendar(record, m_lastBaseCalendar, true);
            break;
         }

         case MPXConstants.BASE_CALENDAR_HOURS_RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               ProjectCalendarHours hours = m_lastBaseCalendar.addCalendarHours();
               populateCalendarHours(record, hours);
            }

            break;
         }

         case MPXConstants.BASE_CALENDAR_EXCEPTION_RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               populateCalendarException(record, m_lastBaseCalendar);
            }

            break;
         }

         case MPXConstants.PROJECT_HEADER_RECORD_NUMBER:
         {
            populateProjectHeader(record, m_projectFile.getProjectProperties());
            m_formats.update();
            break;
         }

         case MPXConstants.RESOURCE_MODEL_TEXT_RECORD_NUMBER:
         {
            if ((m_resourceTableDefinition == false) && (m_ignoreTextModels == false))
            {
               m_resourceModel.update(record, true);
               m_resourceTableDefinition = true;
            }

            break;
         }

         case MPXConstants.RESOURCE_MODEL_NUMERIC_RECORD_NUMBER:
         {
            if (m_resourceTableDefinition == false)
            {
               m_resourceModel.update(record, false);
               m_resourceTableDefinition = true;
            }

            break;
         }

         case MPXConstants.RESOURCE_RECORD_NUMBER:
         {
            m_lastResource = m_projectFile.addResource();
            populateResource(m_lastResource, record);
            m_eventManager.fireResourceReadEvent(m_lastResource);
            break;
         }

         case MPXConstants.RESOURCE_NOTES_RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               m_lastResource.setNotes(record.getString(0));
            }

            break;
         }

         case MPXConstants.RESOURCE_CALENDAR_RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               m_lastResourceCalendar = m_lastResource.addResourceCalendar();
               populateCalendar(record, m_lastResourceCalendar, false);
            }

            break;
         }

         case MPXConstants.RESOURCE_CALENDAR_HOURS_RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               ProjectCalendarHours hours = m_lastResourceCalendar.addCalendarHours();
               populateCalendarHours(record, hours);
            }

            break;
         }

         case MPXConstants.RESOURCE_CALENDAR_EXCEPTION_RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               populateCalendarException(record, m_lastResourceCalendar);
            }

            break;
         }

         case MPXConstants.TASK_MODEL_TEXT_RECORD_NUMBER:
         {
            if ((m_taskTableDefinition == false) && (m_ignoreTextModels == false))
            {
               m_taskModel.update(record, true);
               m_taskTableDefinition = true;
            }

            break;
         }

         case MPXConstants.TASK_MODEL_NUMERIC_RECORD_NUMBER:
         {
            if (m_taskTableDefinition == false)
            {
               m_taskModel.update(record, false);
               m_taskTableDefinition = true;
            }

            break;
         }

         case MPXConstants.TASK_RECORD_NUMBER:
         {
            m_lastTask = m_projectFile.addTask();
            populateTask(record, m_lastTask);

            int outlineLevel = NumberHelper.getInt(m_lastTask.getOutlineLevel());

            if (m_baseOutlineLevel == -1)
            {
               m_baseOutlineLevel = outlineLevel;
            }

            if (outlineLevel != m_baseOutlineLevel)
            {
               List<Task> childTasks = m_projectFile.getChildTasks();
               if (childTasks.isEmpty() == true)
               {
                  throw new MPXJException(MPXJException.INVALID_OUTLINE);
               }
               childTasks.get(childTasks.size() - 1).addChildTask(m_lastTask, outlineLevel);
            }

            m_eventManager.fireTaskReadEvent(m_lastTask);
            break;
         }

         case MPXConstants.TASK_NOTES_RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               m_lastTask.setNotes(record.getString(0));
            }

            break;
         }

         case MPXConstants.RECURRING_TASK_RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               m_lastTask.setRecurring(true);
               RecurringTask task = m_lastTask.addRecurringTask();
               populateRecurringTask(record, task);
            }

            break;
         }

         case MPXConstants.RESOURCE_ASSIGNMENT_RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               m_lastResourceAssignment = m_lastTask.addResourceAssignment((Resource) null);
               populateResourceAssignment(record, m_lastResourceAssignment);
            }

            break;
         }

         case MPXConstants.RESOURCE_ASSIGNMENT_WORKGROUP_FIELDS_RECORD_NUMBER:
         {
            if (m_lastResourceAssignment != null)
            {
               ResourceAssignmentWorkgroupFields workgroup = m_lastResourceAssignment.addWorkgroupAssignment();
               populateResourceAssignmentWorkgroupFields(record, workgroup);
            }

            break;
         }

         case MPXConstants.FILE_CREATION_RECORD_NUMBER:
         {
            populateFileCreationRecord(record, m_projectFile.getProjectProperties());
            break;
         }

         default:
         {
            throw new MPXJException(MPXJException.INVALID_RECORD);
         }
      }
   }

   /**
    * Populates currency settings.
    *
    * @param record MPX record
    * @param properties project properties
    */
   private void populateCurrencySettings(Record record, ProjectProperties properties)
   {
      properties.setCurrencySymbol(record.getString(0));
      properties.setSymbolPosition(record.getCurrencySymbolPosition(1));
      properties.setCurrencyDigits(record.getInteger(2));

      Character c = record.getCharacter(3);
      if (c != null)
      {
         properties.setThousandsSeparator(c.charValue());
      }

      c = record.getCharacter(4);
      if (c != null)
      {
         properties.setDecimalSeparator(c.charValue());
      }
   }

   /**
    * Populates default settings.
    *
    * @param record MPX record
    * @param properties project properties
    * @throws MPXJException
    */
   private void populateDefaultSettings(Record record, ProjectProperties properties) throws MPXJException
   {
      properties.setDefaultDurationUnits(record.getTimeUnit(0));
      properties.setDefaultDurationIsFixed(record.getNumericBoolean(1));
      properties.setDefaultWorkUnits(record.getTimeUnit(2));
      properties.setMinutesPerDay(Double.valueOf(NumberHelper.getDouble(record.getFloat(3)) * 60));
      properties.setMinutesPerWeek(Double.valueOf(NumberHelper.getDouble(record.getFloat(4)) * 60));
      properties.setDefaultStandardRate(record.getRate(5));
      properties.setDefaultOvertimeRate(record.getRate(6));
      properties.setUpdatingTaskStatusUpdatesResourceStatus(record.getNumericBoolean(7));
      properties.setSplitInProgressTasks(record.getNumericBoolean(8));
   }

   /**
    * Populates date time settings.
    *
    * @param record MPX record
    * @param properties project properties
    */
   private void populateDateTimeSettings(Record record, ProjectProperties properties)
   {
      properties.setDateOrder(record.getDateOrder(0));
      properties.setTimeFormat(record.getTimeFormat(1));

      Date time = getTimeFromInteger(record.getInteger(2));
      if (time != null)
      {
         properties.setDefaultStartTime(time);
      }

      Character c = record.getCharacter(3);
      if (c != null)
      {
         properties.setDateSeparator(c.charValue());
      }

      c = record.getCharacter(4);
      if (c != null)
      {
         properties.setTimeSeparator(c.charValue());
      }

      properties.setAMText(record.getString(5));
      properties.setPMText(record.getString(6));
      properties.setDateFormat(record.getDateFormat(7));
      properties.setBarTextDateFormat(record.getDateFormat(8));
   }

   /**
    * Converts a time represented as an integer to a Date instance.
    *
    * @param time integer time
    * @return Date instance
    */
   private Date getTimeFromInteger(Integer time)
   {
      Date result = null;

      if (time != null)
      {
         int minutes = time.intValue();
         int hours = minutes / 60;
         minutes -= (hours * 60);

         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MINUTE, minutes);
         cal.set(Calendar.HOUR_OF_DAY, hours);

         result = cal.getTime();
      }

      return (result);
   }

   /**
    * Populates the project header.
    *
    * @param record MPX record
    * @param properties project properties
    * @throws MPXJException
    */
   private void populateProjectHeader(Record record, ProjectProperties properties) throws MPXJException
   {
      properties.setProjectTitle(record.getString(0));
      properties.setCompany(record.getString(1));
      properties.setManager(record.getString(2));
      properties.setDefaultCalendarName(record.getString(3));
      properties.setStartDate(record.getDateTime(4));
      properties.setFinishDate(record.getDateTime(5));
      properties.setScheduleFrom(record.getScheduleFrom(6));
      properties.setCurrentDate(record.getDateTime(7));
      properties.setComments(record.getString(8));
      properties.setCost(record.getCurrency(9));
      properties.setBaselineCost(record.getCurrency(10));
      properties.setActualCost(record.getCurrency(11));
      properties.setWork(record.getDuration(12));
      properties.setBaselineWork(record.getDuration(13));
      properties.setActualWork(record.getDuration(14));
      properties.setWork2(record.getPercentage(15));
      properties.setDuration(record.getDuration(16));
      properties.setBaselineDuration(record.getDuration(17));
      properties.setActualDuration(record.getDuration(18));
      properties.setPercentageComplete(record.getPercentage(19));
      properties.setBaselineStart(record.getDateTime(20));
      properties.setBaselineFinish(record.getDateTime(21));
      properties.setActualStart(record.getDateTime(22));
      properties.setActualFinish(record.getDateTime(23));
      properties.setStartVariance(record.getDuration(24));
      properties.setFinishVariance(record.getDuration(25));
      properties.setSubject(record.getString(26));
      properties.setAuthor(record.getString(27));
      properties.setKeywords(record.getString(28));
   }

   /**
    * Populates a calendar hours instance.
    *
    * @param record MPX record
    * @param hours calendar hours instance
    * @throws MPXJException
    */
   private void populateCalendarHours(Record record, ProjectCalendarHours hours) throws MPXJException
   {
      hours.setDay(Day.getInstance(NumberHelper.getInt(record.getInteger(0))));
      addDateRange(hours, record.getTime(1), record.getTime(2));
      addDateRange(hours, record.getTime(3), record.getTime(4));
      addDateRange(hours, record.getTime(5), record.getTime(6));
   }

   /**
    * Get a date range that correctly handles the cae where the end time
    * is midnight. In this instance the end time should be the start of the
    * next day.
    *
    * @param hours calendar hours
    * @param start start date
    * @param end end date
    */
   private void addDateRange(ProjectCalendarHours hours, Date start, Date end)
   {
      if (start != null && end != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(end);
         // If the time ends on midnight, the date should be the next day. Otherwise problems occur.
         if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0 && cal.get(Calendar.SECOND) == 0 && cal.get(Calendar.MILLISECOND) == 0)
         {
            cal.add(Calendar.DAY_OF_YEAR, 1);
         }
         end = cal.getTime();

         hours.addRange(new DateRange(start, end));
      }
   }

   /**
    * Populates a calendar exception instance.
    *
    * @param record MPX record
    * @param calendar calendar to which the exception will be added
    * @throws MPXJException
    */
   private void populateCalendarException(Record record, ProjectCalendar calendar) throws MPXJException
   {
      Date fromDate = record.getDate(0);
      Date toDate = record.getDate(1);
      boolean working = record.getNumericBoolean(2);

      ProjectCalendarException exception = calendar.addCalendarException(fromDate, toDate);
      if (working)
      {
         exception.addRange(new DateRange(record.getTime(3), record.getTime(4)));
         exception.addRange(new DateRange(record.getTime(5), record.getTime(6)));
         exception.addRange(new DateRange(record.getTime(7), record.getTime(8)));
      }
   }

   /**
    * Populates a calendar instance.
    *
    * @param record MPX record
    * @param calendar calendar instance
    * @param isBaseCalendar true if this is a base calendar
    */
   private void populateCalendar(Record record, ProjectCalendar calendar, boolean isBaseCalendar)
   {
      if (isBaseCalendar == true)
      {
         calendar.setName(record.getString(0));
      }
      else
      {
         calendar.setParent(m_projectFile.getCalendarByName(record.getString(0)));
      }

      calendar.setWorkingDay(Day.SUNDAY, DayType.getInstance(record.getInteger(1)));
      calendar.setWorkingDay(Day.MONDAY, DayType.getInstance(record.getInteger(2)));
      calendar.setWorkingDay(Day.TUESDAY, DayType.getInstance(record.getInteger(3)));
      calendar.setWorkingDay(Day.WEDNESDAY, DayType.getInstance(record.getInteger(4)));
      calendar.setWorkingDay(Day.THURSDAY, DayType.getInstance(record.getInteger(5)));
      calendar.setWorkingDay(Day.FRIDAY, DayType.getInstance(record.getInteger(6)));
      calendar.setWorkingDay(Day.SATURDAY, DayType.getInstance(record.getInteger(7)));

      m_eventManager.fireCalendarReadEvent(calendar);
   }

   /**
    * Populates a resource.
    *
    * @param resource resource instance
    * @param record MPX record
    * @throws MPXJException
    */
   private void populateResource(Resource resource, Record record) throws MPXJException
   {
      String falseText = LocaleData.getString(m_locale, LocaleData.NO);

      int length = record.getLength();
      int[] model = m_resourceModel.getModel();

      for (int i = 0; i < length; i++)
      {
         int mpxFieldType = model[i];
         if (mpxFieldType == -1)
         {
            break;
         }

         String field = record.getString(i);

         if (field == null || field.length() == 0)
         {
            continue;
         }

         ResourceField resourceField = MPXResourceField.getMpxjField(mpxFieldType);
         switch (resourceField)
         {
            case OBJECTS:
            {
               resource.set(resourceField, record.getInteger(i));
               break;
            }

            case ID:
            {
               resource.setID(record.getInteger(i));
               break;
            }

            case UNIQUE_ID:
            {
               resource.setUniqueID(record.getInteger(i));
               break;
            }

            case MAX_UNITS:
            {
               resource.set(resourceField, record.getUnits(i));
               break;
            }

            case PERCENT_WORK_COMPLETE:
            case PEAK:
            {
               resource.set(resourceField, record.getPercentage(i));
               break;
            }

            case COST:
            case COST_PER_USE:
            case COST_VARIANCE:
            case BASELINE_COST:
            case ACTUAL_COST:
            case REMAINING_COST:
            {
               resource.set(resourceField, record.getCurrency(i));
               break;
            }

            case OVERTIME_RATE:
            case STANDARD_RATE:
            {
               resource.set(resourceField, record.getRate(i));
               break;
            }

            case REMAINING_WORK:
            case OVERTIME_WORK:
            case BASELINE_WORK:
            case ACTUAL_WORK:
            case WORK:
            case WORK_VARIANCE:
            {
               resource.set(resourceField, record.getDuration(i));
               break;
            }

            case ACCRUE_AT:
            {
               resource.set(resourceField, record.getAccrueType(i));
               break;
            }

            case LINKED_FIELDS:
            case OVERALLOCATED:
            {
               resource.set(resourceField, record.getBoolean(i, falseText));
               break;
            }

            default:
            {
               resource.set(resourceField, field);
               break;
            }
         }
      }

      if (m_projectConfig.getAutoResourceUniqueID() == true)
      {
         resource.setUniqueID(Integer.valueOf(m_projectConfig.getNextResourceUniqueID()));
      }

      if (m_projectConfig.getAutoResourceID() == true)
      {
         resource.setID(Integer.valueOf(m_projectConfig.getNextResourceID()));
      }

      //
      // Handle malformed MPX files - ensure we have a unique ID
      //
      if (resource.getUniqueID() == null)
      {
         resource.setUniqueID(resource.getID());
      }
   }

   /**
    * Populates a relation list.
    *
    * @param task parent task
    * @param field target task field
    * @param data MPX relation list data
    */
   private void populateRelationList(Task task, TaskField field, String data)
   {
      DeferredRelationship dr = new DeferredRelationship();
      dr.setTask(task);
      dr.setField(field);
      dr.setData(data);
      m_deferredRelationships.add(dr);
   }

   /**
    * This method iterates through the deferred relationships,
    * parsing the data and setting up relationships between tasks.
    *
    * @throws MPXJException
    */
   private void processDeferredRelationships() throws MPXJException
   {
      for (DeferredRelationship dr : m_deferredRelationships)
      {
         processDeferredRelationship(dr);
      }
   }

   /**
    * This method processes a single deferred relationship list.
    *
    * @param dr deferred relationship list data
    * @throws MPXJException
    */
   private void processDeferredRelationship(DeferredRelationship dr) throws MPXJException
   {
      String data = dr.getData();
      Task task = dr.getTask();

      int length = data.length();

      if (length != 0)
      {
         int start = 0;
         int end = 0;

         while (end != length)
         {
            end = data.indexOf(m_delimiter, start);

            if (end == -1)
            {
               end = length;
            }

            populateRelation(dr.getField(), task, data.substring(start, end).trim());

            start = end + 1;
         }
      }
   }

   /**
    * Creates and populates a new task relationship.
    *
    * @param field which task field source of data
    * @param sourceTask relationship source task
    * @param relationship relationship string
    * @throws MPXJException
    */
   private void populateRelation(TaskField field, Task sourceTask, String relationship) throws MPXJException
   {
      int index = 0;
      int length = relationship.length();

      //
      // Extract the identifier
      //
      while ((index < length) && (Character.isDigit(relationship.charAt(index)) == true))
      {
         ++index;
      }

      Integer taskID;
      try
      {
         taskID = Integer.valueOf(relationship.substring(0, index));
      }

      catch (NumberFormatException ex)
      {
         throw new MPXJException(MPXJException.INVALID_FORMAT + " '" + relationship + "'");
      }

      //
      // Now find the task, so we can extract the unique ID
      //
      Task targetTask;
      if (field == TaskField.PREDECESSORS)
      {
         targetTask = m_projectFile.getTaskByID(taskID);
      }
      else
      {
         targetTask = m_projectFile.getTaskByUniqueID(taskID);
      }

      //
      // If we haven't reached the end, we next expect to find
      // SF, SS, FS, FF
      //
      RelationType type = null;
      Duration lag = null;

      if (index == length)
      {
         type = RelationType.FINISH_START;
         lag = Duration.getInstance(0, TimeUnit.DAYS);
      }
      else
      {
         if ((index + 1) == length)
         {
            throw new MPXJException(MPXJException.INVALID_FORMAT + " '" + relationship + "'");
         }

         type = RelationTypeUtility.getInstance(m_locale, relationship.substring(index, index + 2));

         index += 2;

         if (index == length)
         {
            lag = Duration.getInstance(0, TimeUnit.DAYS);
         }
         else
         {
            if (relationship.charAt(index) == '+')
            {
               ++index;
            }

            lag = DurationUtility.getInstance(relationship.substring(index), m_formats.getDurationDecimalFormat(), m_locale);
         }
      }

      if (type == null)
      {
         throw new MPXJException(MPXJException.INVALID_FORMAT + " '" + relationship + "'");
      }

      Relation relation = sourceTask.addPredecessor(targetTask, type, lag);
      m_eventManager.fireRelationReadEvent(relation);
   }

   /**
    * Populates a task instance.
    *
    * @param record MPX record
    * @param task task instance
    * @throws MPXJException
    */
   private void populateTask(Record record, Task task) throws MPXJException
   {
      String falseText = LocaleData.getString(m_locale, LocaleData.NO);

      int mpxFieldID = 0;
      String field;

      int i = 0;
      int length = record.getLength();
      int[] model = m_taskModel.getModel();

      while (i < length)
      {
         mpxFieldID = model[i];

         if (mpxFieldID == -1)
         {
            break;
         }

         field = record.getString(i++);

         if ((field == null) || (field.length() == 0))
         {
            continue;
         }

         TaskField taskField = MPXTaskField.getMpxjField(mpxFieldID);
         if (taskField == null)
         {
            System.out.println("Null Task Field " + mpxFieldID);
            continue;
         }

         switch (taskField)
         {
            case PREDECESSORS:
            case UNIQUE_ID_PREDECESSORS:
            {
               populateRelationList(task, taskField, field);
               break;
            }

            case PERCENT_COMPLETE:
            case PERCENT_WORK_COMPLETE:
            {
               try
               {
                  task.set(taskField, m_formats.getPercentageDecimalFormat().parse(field));
               }

               catch (ParseException ex)
               {
                  throw new MPXJException("Failed to parse percentage", ex);
               }
               break;
            }

            case ACTUAL_COST:
            case BASELINE_COST:
            case BCWP:
            case BCWS:
            case COST:
            case COST1:
            case COST2:
            case COST3:
            case COST_VARIANCE:
            case CV:
            case FIXED_COST:
            case REMAINING_COST:
            case SV:
            {
               try
               {
                  task.set(taskField, m_formats.getCurrencyFormat().parse(field));
               }

               catch (ParseException ex)
               {
                  throw new MPXJException("Failed to parse currency", ex);
               }
               break;
            }

            case ACTUAL_DURATION:
            case ACTUAL_WORK:
            case BASELINE_DURATION:
            case BASELINE_WORK:
            case DURATION:
            case DURATION1:
            case DURATION2:
            case DURATION3:
            case DURATION_VARIANCE:
            case FINISH_VARIANCE:
            case FREE_SLACK:
            case REMAINING_DURATION:
            case REMAINING_WORK:
            case START_VARIANCE:
            case TOTAL_SLACK:
            case WORK:
            case WORK_VARIANCE:
            case LEVELING_DELAY:
            {
               task.set(taskField, DurationUtility.getInstance(field, m_formats.getDurationDecimalFormat(), m_locale));
               break;
            }

            case ACTUAL_FINISH:
            case ACTUAL_START:
            case BASELINE_FINISH:
            case BASELINE_START:
            case CONSTRAINT_DATE:
            case CREATED:
            case EARLY_FINISH:
            case EARLY_START:
            case FINISH:
            case FINISH1:
            case FINISH2:
            case FINISH3:
            case FINISH4:
            case FINISH5:
            case LATE_FINISH:
            case LATE_START:
            case RESUME:
            case START:
            case START1:
            case START2:
            case START3:
            case START4:
            case START5:
            case STOP:
            {
               try
               {
                  task.set(taskField, m_formats.getDateTimeFormat().parse(field));
               }

               catch (ParseException ex)
               {
                  throw new MPXJException("Failed to parse date time", ex);
               }
               break;
            }

            case CONFIRMED:
            case CRITICAL:
            case FLAG1:
            case FLAG2:
            case FLAG3:
            case FLAG4:
            case FLAG5:
            case FLAG6:
            case FLAG7:
            case FLAG8:
            case FLAG9:
            case FLAG10:
            case HIDE_BAR:
            case LINKED_FIELDS:
            case MARKED:
            case MILESTONE:
            case ROLLUP:
            case SUMMARY:
            case UPDATE_NEEDED:
            {
               task.set(taskField, ((field.equalsIgnoreCase(falseText) == true) ? Boolean.FALSE : Boolean.TRUE));
               break;
            }

            case CONSTRAINT_TYPE:
            {
               task.set(taskField, ConstraintTypeUtility.getInstance(m_locale, field));
               break;
            }

            case OBJECTS:
            case OUTLINE_LEVEL:
            {
               task.set(taskField, Integer.valueOf(field));
               break;
            }

            case ID:
            {
               task.setID(Integer.valueOf(field));
               break;
            }

            case UNIQUE_ID:
            {
               task.setUniqueID(Integer.valueOf(field));
               break;
            }

            case NUMBER1:
            case NUMBER2:
            case NUMBER3:
            case NUMBER4:
            case NUMBER5:
            {
               try
               {
                  task.set(taskField, m_formats.getDecimalFormat().parse(field));
               }

               catch (ParseException ex)
               {
                  throw new MPXJException("Failed to parse number", ex);
               }

               break;
            }

            case PRIORITY:
            {
               task.set(taskField, PriorityUtility.getInstance(m_locale, field));
               break;
            }

            case TYPE:
            {
               boolean fixed = !field.equalsIgnoreCase(falseText);
               task.setType(fixed ? TaskType.FIXED_DURATION : TaskType.FIXED_UNITS);
               break;
            }

            default:
            {
               task.set(taskField, field);
               break;
            }
         }
      }

      if (m_projectConfig.getAutoWBS() == true)
      {
         task.generateWBS(null);
      }

      if (m_projectConfig.getAutoOutlineNumber() == true)
      {
         task.generateOutlineNumber(null);
      }

      if (m_projectConfig.getAutoOutlineLevel() == true)
      {
         task.setOutlineLevel(Integer.valueOf(1));
      }

      if (m_projectConfig.getAutoTaskUniqueID() == true)
      {
         task.setUniqueID(Integer.valueOf(m_projectConfig.getNextTaskUniqueID()));
      }

      if (task.getID() == null || m_projectConfig.getAutoTaskID() == true)
      {
         task.setID(Integer.valueOf(m_projectConfig.getNextTaskID()));
      }

      //
      // Handle malformed MPX files - ensure we have a unique ID
      //
      if (task.getUniqueID() == null)
      {
         task.setUniqueID(task.getID());
      }

      //
      // Some applications (I'm looking at you SureTrak) don't write start and finish
      // attributes. If you open an MPX file like this in MS Project, it will use
      // the early start and early finish values (if present) to populate
      // the start and finish attributes.

      if (task.getStart() == null && task.getEarlyStart() != null)
      {
         task.setStart(task.getEarlyStart());
      }

      if (task.getFinish() == null && task.getEarlyFinish() != null)
      {
         task.setFinish(task.getEarlyFinish());
      }
   }

   /**
    * Populates a recurring task.
    *
    * @param record MPX record
    * @param task recurring task
    * @throws MPXJException
    */
   private void populateRecurringTask(Record record, RecurringTask task) throws MPXJException
   {
      //System.out.println(record);
      task.setStartDate(record.getDateTime(1));
      task.setFinishDate(record.getDateTime(2));
      task.setDuration(RecurrenceUtility.getDuration(m_projectFile.getProjectProperties(), record.getInteger(3), record.getInteger(4)));
      task.setOccurrences(record.getInteger(5));
      task.setRecurrenceType(RecurrenceUtility.getRecurrenceType(record.getInteger(6)));
      task.setUseEndDate(NumberHelper.getInt(record.getInteger(8)) == 1);
      task.setDailyWorkday(NumberHelper.getInt(record.getInteger(9)) == 1);
      task.setWeeklyDays(RecurrenceUtility.getDays(record.getString(10)));
      task.setMonthlyRelative(NumberHelper.getInt(record.getInteger(11)) == 1);
      task.setYearlyAbsolute(NumberHelper.getInt(record.getInteger(12)) == 1);
      task.setDailyFrequency(record.getInteger(13));
      task.setWeeklyFrequency(record.getInteger(14));
      task.setMonthlyRelativeOrdinal(record.getInteger(15));
      task.setMonthlyRelativeDay(RecurrenceUtility.getDay(record.getInteger(16)));
      task.setMonthlyRelativeFrequency(record.getInteger(17));
      task.setMonthlyAbsoluteDay(record.getInteger(18));
      task.setMonthlyAbsoluteFrequency(record.getInteger(19));
      task.setYearlyRelativeOrdinal(record.getInteger(20));
      task.setYearlyRelativeDay(RecurrenceUtility.getDay(record.getInteger(21)));
      task.setYearlyRelativeMonth(record.getInteger(22));
      task.setYearlyAbsoluteDate(record.getDateTime(23));
      //System.out.println(task);
   }

   /**
    * Populate a resource assignment.
    *
    * @param record MPX record
    * @param assignment resource assignment
    * @throws MPXJException
    */
   private void populateResourceAssignment(Record record, ResourceAssignment assignment) throws MPXJException
   {
      //
      // Handle malformed MPX files - ensure that we can locate the resource
      // using either the Unique ID attribute or the ID attribute.
      //
      Resource resource = m_projectFile.getResourceByUniqueID(record.getInteger(12));
      if (resource == null)
      {
         resource = m_projectFile.getResourceByID(record.getInteger(0));
      }

      assignment.setUnits(record.getUnits(1));
      assignment.setWork(record.getDuration(2));
      assignment.setBaselineWork(record.getDuration(3));
      assignment.setActualWork(record.getDuration(4));
      assignment.setOvertimeWork(record.getDuration(5));
      assignment.setCost(record.getCurrency(6));
      assignment.setBaselineCost(record.getCurrency(7));
      assignment.setActualCost(record.getCurrency(8));
      assignment.setStart(record.getDateTime(9));
      assignment.setFinish(record.getDateTime(10));
      assignment.setDelay(record.getDuration(11));

      //
      // Calculate the remaining work
      //
      Duration work = assignment.getWork();
      Duration actualWork = assignment.getActualWork();
      if (work != null && actualWork != null)
      {
         if (work.getUnits() != actualWork.getUnits())
         {
            actualWork = actualWork.convertUnits(work.getUnits(), m_projectFile.getProjectProperties());
         }

         assignment.setRemainingWork(Duration.getInstance(work.getDuration() - actualWork.getDuration(), work.getUnits()));
      }

      if (resource != null)
      {
         assignment.setResourceUniqueID(resource.getUniqueID());
         resource.addResourceAssignment(assignment);
      }

      m_eventManager.fireAssignmentReadEvent(assignment);
   }

   /**
    * Populate a resource assignment workgroup instance.
    *
    * @param record MPX record
    * @param workgroup workgroup instance
    * @throws MPXJException
    */
   private void populateResourceAssignmentWorkgroupFields(Record record, ResourceAssignmentWorkgroupFields workgroup) throws MPXJException
   {
      workgroup.setMessageUniqueID(record.getString(0));
      workgroup.setConfirmed(NumberHelper.getInt(record.getInteger(1)) == 1);
      workgroup.setResponsePending(NumberHelper.getInt(record.getInteger(1)) == 1);
      workgroup.setUpdateStart(record.getDateTime(3));
      workgroup.setUpdateFinish(record.getDateTime(4));
      workgroup.setScheduleID(record.getString(5));
   }

   /**
    * Populate a file creation record.
    *
    * @param record MPX record
    * @param properties project properties
    */
   static void populateFileCreationRecord(Record record, ProjectProperties properties)
   {
      properties.setMpxProgramName(record.getString(0));
      properties.setMpxFileVersion(FileVersion.getInstance(record.getString(1)));
      properties.setMpxCodePage(record.getCodePage(2));
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
    * Retrieves an array of locales supported by this class.
    *
    * @return array of supported locales
    */
   public Locale[] getSupportedLocales()
   {
      return (LocaleUtility.getSupportedLocales());
   }

   /**
    * This method sets the flag indicating that the text version of the
    * Task and Resource Table Definition records should be ignored. Ignoring
    * these records gets around the problem where MPX files have been generated
    * with incorrect task or resource field names, but correct task or resource
    * field numbers in the numeric version of the record.
    *
    * @param flag Boolean flag
    */
   public void setIgnoreTextModels(boolean flag)
   {
      m_ignoreTextModels = flag;
   }

   /**
    * Retrieves the flag indicating that the text version of the Task and
    * Resource Table Definition records should be ignored.
    *
    * @return Boolean flag
    */
   public boolean getIgnoreTextModels()
   {
      return (m_ignoreTextModels);
   }

   private Locale m_locale = Locale.ENGLISH;
   private boolean m_ignoreTextModels = true;

   /**
    * Transient working data.
    */

   /**
    * This class is used to collect relationship data awaiting
    * deferred processing. We do this to allow forward references
    * between tasks.
    */
   protected static class DeferredRelationship
   {
      /**
       * Retrieve the parent task.
       *
       * @return parent Task instance
       */
      public Task getTask()
      {
         return m_task;
      }

      /**
       * Set the parent task instance.
       *
       * @param task parent Task instance
       */
      public void setTask(Task task)
      {
         m_task = task;
      }

      /**
       * Retrieve the target task field.
       *
       * @return TaskField instance
       */
      public TaskField getField()
      {
         return m_field;
      }

      /**
       * Set the target task field.
       *
       * @param field TaskField instance
       */
      public void setField(TaskField field)
      {
         m_field = field;
      }

      /**
       * Retrieve the relationship data.
       *
       * @return relationship data
       */
      public String getData()
      {
         return m_data;
      }

      /**
       * Set the relationship data.
       *
       * @param data relationship data
       */
      public void setData(String data)
      {
         m_data = data;
      }

      private Task m_task;
      private TaskField m_field;
      private String m_data;
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private ProjectConfig m_projectConfig;
   private Task m_lastTask;
   private Resource m_lastResource;
   private ProjectCalendar m_lastResourceCalendar;
   private ResourceAssignment m_lastResourceAssignment;
   private ProjectCalendar m_lastBaseCalendar;
   private boolean m_resourceTableDefinition;
   private boolean m_taskTableDefinition;
   private TaskModel m_taskModel;
   private ResourceModel m_resourceModel;
   private char m_delimiter;
   private MPXJFormats m_formats;
   private List<DeferredRelationship> m_deferredRelationships;
   private List<ProjectListener> m_projectListeners;

   /**
    * This member data is used to hold the outline level number of the
    * first outline level used in the MPX file. When data from
    * Microsoft Project is saved in MPX format, MSP creates an invisible
    * task with an outline level as zero, which acts as an umbrella
    * task for all of the other tasks defined in the file. This is not
    * a strict requirement, and an MPX file could be generated from another
    * source that only contains "visible" tasks that have outline levels
    * >= 1.
    */
   private int m_baseOutlineLevel;
}
