/*
 * file:       MPXReader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
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
import net.sf.mpxj.Duration;
import net.sf.mpxj.FileCreationRecord;
import net.sf.mpxj.FileVersion;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.RecurringTask;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceAssignmentWorkgroupFields;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.reader.AbstractProjectReader;
import net.sf.mpxj.utility.NumberUtility;


/**
 * This class creates a new MPXFile instance by reading an MPX file.
 */
public final class MPXReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   public ProjectFile read (InputStream is)
      throws MPXJException
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
         data[0] = (byte)bis.read();
         bis.mark(1024);
         data[1] = (byte)bis.read();
         data[2] = (byte)bis.read();
         data[3] = (byte)bis.read();
   
         if ((data[0] != 'M') || (data[1] != 'P') || (data[2] != 'X'))
         {
            throw new MPXJException(MPXJException.INVALID_FILE);
         }
   
         m_projectFile = new ProjectFile ();
         LocaleUtility.setLocale(m_projectFile, m_locale);
         m_delimiter = (char)data[3];
         m_projectFile.setDelimiter(m_delimiter);
         m_taskModel = new TaskModel(m_projectFile, m_locale);
         m_taskModel.setLocale(m_locale);
         m_resourceModel = new ResourceModel(m_projectFile, m_locale);
         m_resourceModel.setLocale(m_locale);
         m_baseOutlineLevel = -1;
         m_formats = new MPXFormats(m_locale, m_projectFile);
         
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
         InputStreamReader reader = new InputStreamReader(bis, m_projectFile.getFileCreationRecord().getCodePage().getCharset());
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
   
         //
         // Ensure that all tasks and resources have valid Unique IDs
         //
         m_projectFile.updateUniqueIdentifiers();
         
         //
         // Ensure that the structure is consistent
         //
         m_projectFile.updateStructure();
         
         //
         // Ensure that the unique ID counters are correct
         //
         m_projectFile.updateUniqueCounters();
         
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
      }
   }
     
   /**
    * Parse an MPX record.
    * 
    * @param recordNumber record number
    * @param record record data
    * @throws MPXJException
    */
   private void parseRecord (String recordNumber, Record record)
      throws MPXJException
   {
      switch (Integer.parseInt(recordNumber))
      {
         case MPXConstants.COMMENTS_RECORD_NUMBER:
         {
            // silently ignored
            break;
         }
   
         case MPXConstants.CURRENCY_SETTINGS_RECORD_NUMBER:
         {
            populateCurrencySettings(record, m_projectFile.getProjectHeader());
            m_formats.update();
            break;
         }
   
         case MPXConstants.DEFAULT_SETTINGS_RECORD_NUMBER:
         {
            populateDefaultSettings(record, m_projectFile.getProjectHeader());
            m_formats.update();
            break;
         }
   
         case MPXConstants.DATE_TIME_SETTINGS_RECORD_NUMBER:
         {
            populateDateTimeSettings(record, m_projectFile.getProjectHeader());
            m_formats.update();
            break;
         }
   
         case MPXConstants.BASE_CALENDAR_RECORD_NUMBER:
         {
            m_lastBaseCalendar = m_projectFile.addBaseCalendar();
            populateCalendar(record, m_lastBaseCalendar);
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
               ProjectCalendarException exception = m_lastBaseCalendar.addCalendarException();
               populateCalendarException(record, exception);
            }
   
            break;
         }
   
         case MPXConstants.PROJECT_HEADER_RECORD_NUMBER:
         {
            populateProjectHeader(record, m_projectFile.getProjectHeader());
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
            m_projectFile.fireResourceReadEvent(m_lastResource);            
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
               populateCalendar(record, m_lastResourceCalendar);
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
               ProjectCalendarException exception = m_lastResourceCalendar.addCalendarException();
               populateCalendarException(record, exception);
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
   
            int outlineLevel = NumberUtility.getInt(m_lastTask.getOutlineLevel());
   
            if (m_baseOutlineLevel == -1)
            {
               m_baseOutlineLevel = outlineLevel;
            }
   
            List childTasks = m_projectFile.getChildTasks();
            if (outlineLevel == m_baseOutlineLevel)
            {
               childTasks.add(m_lastTask);
            }
            else
            {
               if (childTasks.isEmpty() == true)
               {
                  throw new MPXJException(MPXJException.INVALID_OUTLINE);
               }
   
               ((Task)childTasks.get(childTasks.size()-1)).addChildTask(m_lastTask, outlineLevel);
            }
   
            m_projectFile.fireTaskReadEvent(m_lastTask);
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
               RecurringTask task = m_lastTask.addRecurringTask();
               populateRecurringTask(record, task);
            }
   
            break;
         }
   
         case MPXConstants.RESOURCE_ASSIGNMENT_RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               m_lastResourceAssignment = m_lastTask.addResourceAssignment();
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
   
         case MPXConstants.PROJECT_NAMES_RECORD_NUMBER:
         {
            // silently ignored
            break;
         }
   
         case MPXConstants.DDE_OLE_CLIENT_LINKS_RECORD_NUMBER:
         {
            // silently ignored
            break;
         }
   
         case MPXConstants.FILE_CREATION_RECORD_NUMBER:
         {
            populateFileCreationRecord(record, m_projectFile.getFileCreationRecord());
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
    * @param projectHeader project header
    */
   private void populateCurrencySettings (Record record, ProjectHeader projectHeader)
   {
      projectHeader.setCurrencySymbol (record.getString(0));
      projectHeader.setSymbolPosition (record.getCurrencySymbolPosition(1));
      projectHeader.setCurrencyDigits (record.getInteger(2));
      
      Character c = record.getCharacter(3);
      if (c != null)
      {
         projectHeader.setThousandsSeparator (c.charValue());
      }
      
      c = record.getCharacter(4);
      if (c != null)
      {
         projectHeader.setDecimalSeparator (c.charValue());
      }
   }
   
   /**
    * Populates default settings.
    * 
    * @param record MPX record
    * @param projectHeader project header
    * @throws MPXJException
    */
   private void populateDefaultSettings (Record record, ProjectHeader projectHeader)
      throws MPXJException
   {
      projectHeader.setDefaultDurationUnits(record.getTimeUnit(0));
      projectHeader.setDefaultDurationIsFixed(record.getNumericBoolean(1));
      projectHeader.setDefaultWorkUnits(record.getTimeUnit(2));
      projectHeader.setDefaultHoursInDay(record.getFloat(3));
      projectHeader.setDefaultHoursInWeek(record.getFloat(4));
      projectHeader.setDefaultStandardRate(record.getRate(5));
      projectHeader.setDefaultOvertimeRate(record.getRate(6));
      projectHeader.setUpdatingTaskStatusUpdatesResourceStatus(record.getNumericBoolean(7));
      projectHeader.setSplitInProgressTasks(record.getNumericBoolean(8));
   }
   
   /**
    * Populates date time settings.
    * 
    * @param record MPX record
    * @param projectHeader project header instance
    */
   private void populateDateTimeSettings (Record record, ProjectHeader projectHeader)
   {
      projectHeader.setDateOrder(record.getDateOrder(0));
      projectHeader.setTimeFormat(record.getTimeFormat(1));   
      
      Date time = getTimeFromInteger(record.getInteger(2));
      if (time != null)
      {
         projectHeader.setDefaultStartTime(time);
      }
      
      Character c = record.getCharacter(3);
      if (c != null)
      {
         projectHeader.setDateSeparator(c.charValue());
      }
      
      projectHeader.setTimeSeparator(record.getCharacter(4));
      projectHeader.setAMText(record.getString(5));
      projectHeader.setPMText(record.getString(6));
      projectHeader.setDateFormat(record.getDateFormat(7));
      projectHeader.setBarTextDateFormat(record.getDateFormat(8));
   }
   
   /**
    * Converts a time represented as an integer to a Date instance.
    * 
    * @param time integer time
    * @return Date instance
    */
   private Date getTimeFromInteger (Integer time)
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
    * @param projectHeader project header instance
    * @throws MPXJException
    */
   private void populateProjectHeader (Record record, ProjectHeader projectHeader)
      throws MPXJException
   {
      projectHeader.setProjectTitle(record.getString(0));
      projectHeader.setCompany(record.getString(1));
      projectHeader.setManager(record.getString(2));
      projectHeader.setCalendarName(record.getString(3));
      projectHeader.setStartDate(record.getDateTime(4));
      projectHeader.setFinishDate(record.getDateTime(5));
      projectHeader.setScheduleFrom(record.getScheduleFrom(6));
      projectHeader.setCurrentDate(record.getDateTime(7));
      projectHeader.setComments(record.getString(8));
      projectHeader.setCost(record.getCurrency(9));
      projectHeader.setBaselineCost(record.getCurrency(10));
      projectHeader.setActualCost(record.getCurrency(11));
      projectHeader.setWork(record.getDuration(12));
      projectHeader.setBaselineWork(record.getDuration(13));
      projectHeader.setActualWork(record.getDuration(14));
      projectHeader.setWork2(record.getPercentage(15));
      projectHeader.setDuration(record.getDuration(16));
      projectHeader.setBaselineDuration(record.getDuration(17));
      projectHeader.setActualDuration(record.getDuration(18));
      projectHeader.setPercentageComplete(record.getPercentage(19));
      projectHeader.setBaselineStart(record.getDateTime(20));
      projectHeader.setBaselineFinish(record.getDateTime(21));
      projectHeader.setActualStart(record.getDateTime(22));
      projectHeader.setActualFinish(record.getDateTime(23));
      projectHeader.setStartVariance(record.getDuration(24));
      projectHeader.setFinishVariance(record.getDuration(25));
      projectHeader.setSubject(record.getString(26));
      projectHeader.setAuthor(record.getString(27));
      projectHeader.setKeywords(record.getString(28));
   }
   
   /**
    * Populates a calendar hours instance.
    * 
    * @param record MPX record
    * @param hours calendar hours instance
    * @throws MPXJException
    */
   private void populateCalendarHours (Record record, ProjectCalendarHours hours)
      throws MPXJException
   {
      hours.setDay(Day.getInstance(NumberUtility.getInt(record.getInteger(0))));
      hours.addDateRange(new DateRange(record.getTime(1), record.getTime(2)));
      hours.addDateRange(new DateRange(record.getTime(3), record.getTime(4)));
      hours.addDateRange(new DateRange(record.getTime(5), record.getTime(6)));      
   }
   
   /**
    * Populates a calendar exception instance.
    * 
    * @param record MPX record
    * @param exception calendar exception instance
    * @throws MPXJException
    */
   private void populateCalendarException(Record record, ProjectCalendarException exception)
      throws MPXJException
   {
      exception.setFromDate(record.getDate(0));
      exception.setToDate(record.getDate(1));
      exception.setWorking(record.getNumericBoolean(2));
      exception.setFromTime1(record.getTime(3));
      exception.setToTime1(record.getTime(4));
      exception.setFromTime2(record.getTime(5));
      exception.setToTime2(record.getTime(6));
      exception.setFromTime3(record.getTime(7));
      exception.setToTime3(record.getTime(8));
   }
   
   /**
    * Populates a calendar instance.
    * 
    * @param record MPX record
    * @param calendar calendar instance
    */
   private void populateCalendar(Record record, ProjectCalendar calendar)
   {
      if (calendar.isBaseCalendar() == true)
      {
         calendar.setName(record.getString(0));
      }
      else
      {         
         calendar.setBaseCalendar (m_projectFile.getBaseCalendar(record.getString(0)));
      }

      calendar.setWorkingDay(Day.SUNDAY, record.getInteger(1));
      calendar.setWorkingDay(Day.MONDAY, record.getInteger(2));
      calendar.setWorkingDay(Day.TUESDAY, record.getInteger(3));
      calendar.setWorkingDay(Day.WEDNESDAY, record.getInteger(4));
      calendar.setWorkingDay(Day.THURSDAY, record.getInteger(5));
      calendar.setWorkingDay(Day.FRIDAY, record.getInteger(6));
      calendar.setWorkingDay(Day.SATURDAY, record.getInteger(7));
   }
   
   /**
    * Populates a resource.
    * 
    * @param resource resource instance
    * @param record MPX record
    * @throws MPXJException
    */
   private void populateResource (Resource resource, Record record)
      throws MPXJException
   {      
      String falseText = LocaleData.getString(m_locale, LocaleData.NO);
      
      int length = record.getLength();
      int[] model = m_resourceModel.getModel();
   
      for (int i=0; i < length; i++)
      {
         int x = model[i];
         if (x == -1)
         {
            break;
         }
   
         String field = record.getString (i);
   
         if (field == null || field.length() == 0)
         {
            continue;
         }
   
         switch (x)
         {
            case Resource.OBJECTS:
            {
               resource.set(x,record.getInteger(i));
               break;
            }
   
            case Resource.ID:
            {
               resource.setID(record.getInteger(i));
               break;
            }
   
            case Resource.UNIQUE_ID:
            {
               resource.setUniqueID(record.getInteger(i));
               break;
            }
   
            case Resource.MAX_UNITS:
            {
               resource.set (x, record.getUnits(i));
               break;
            }
   
            case Resource.PERCENT_WORK_COMPLETE:
            case Resource.PEAK_UNITS:
            {
               resource.set(x, record.getPercentage(i));
               break;
            }
   
            case Resource.COST:
            case Resource.COST_PER_USE:
            case Resource.COST_VARIANCE:
            case Resource.BASELINE_COST:
            case Resource.ACTUAL_COST:
            case Resource.REMAINING_COST:
            {
               resource.set(x, record.getCurrency(i));
               break;
            }
   
            case Resource.OVERTIME_RATE:
            case Resource.STANDARD_RATE:
            {
               resource.set (x, record.getRate(i));
               break;
            }
   
            case Resource.REMAINING_WORK:
            case Resource.OVERTIME_WORK:
            case Resource.BASELINE_WORK:
            case Resource.ACTUAL_WORK:
            case Resource.WORK:
            case Resource.WORK_VARIANCE:
            {
               resource.set (x, record.getDuration(i));
               break;
            }
   
            case Resource.ACCRUE_AT:
            {
               resource.set (x, record.getAccrueType(i));
               break;
            }
   
            case Resource.OVERALLOCATED:
            {
               resource.set (x, record.getBoolean(i, falseText));
               break;
            }
   
            default:
            {
               resource.set (x, field);
               break;
            }
         }
      }
   
      if (m_projectFile.getAutoResourceUniqueID() == true)
      {
         resource.setUniqueID (new Integer(m_projectFile.getResourceUniqueID ()));
      }
   
      if (m_projectFile.getAutoResourceID() == true)
      {
         resource.setID (new Integer(m_projectFile.getResourceID ()));
      }
   }
   
   /**
    * Populates a relation list.
    * 
    * @param data MPX relation list data
    * @return relation list
    * @throws MPXJException
    */
   private List populateRelationList (String data)
      throws MPXJException
   {
      List list = new LinkedList ();
      
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
   
            Relation relation = new Relation (m_projectFile);
            populateRelation(data.substring(start, end).trim(), relation);
            list.add(relation);
   
            start = end + 1;
         }
      }
      
      return (list);
   }

   /**
    * Populates an individual relation.
    * 
    * @param relationship relationship string
    * @param relation relation instance
    * @throws MPXJException
    */
   private void populateRelation (String relationship, Relation relation)
      throws MPXJException
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
   
      try
      {
         relation.setTaskID(new Integer(relationship.substring(0, index)));         
      }
   
      catch (NumberFormatException ex)
      {
         throw new MPXJException(MPXJException.INVALID_FORMAT + " '" + relationship + "'");
      }
   
      //
      // Now find the task, so we can extract the unique ID      
      //
      Task task = m_projectFile.getTaskByID(relation.getTaskID());
      if (task != null)
      {
         relation.setTaskUniqueID(task.getUniqueID());
      }
      
      //
      // If we haven't reached the end, we next expect to find
      // SF, SS, FS, FF
      //
      if (index == length)
      {
         relation.setType(RelationType.FINISH_START);
         relation.setDuration(Duration.getInstance(0, TimeUnit.DAYS));
      }
      else
      {
         if ((index + 1) == length)
         {
            throw new MPXJException(MPXJException.INVALID_FORMAT + " '" + relationship + "'");
         }
   
         String relationType = relationship.substring(index, index + 2);
         relation.setType(RelationTypeUtility.getInstance(m_locale, relationship.substring(index, index + 2)));         
         if (relation.getType() == null)
         {
            throw new MPXJException(MPXJException.INVALID_FORMAT + " '" + relationType + "'");
         }
   
         index += 2;
   
         if (index == length)
         {
            relation.setDuration(Duration.getInstance(0, TimeUnit.DAYS));
         }
         else
         {
            if (relationship.charAt(index) == '+')
            {
               ++index;
            }
   
            relation.setDuration(DurationUtility.getInstance(relationship.substring(index), m_formats.getDurationDecimalFormat(), m_locale));
         }
      }
   }

   /**
    * Populates a task instance.
    * 
    * @param record MPX record
    * @param task task instance
    * @throws MPXJException
    */
   private void populateTask (Record record, Task task)
      throws MPXJException
   {
      String falseText = LocaleData.getString(m_locale, LocaleData.NO);
   
      int x = 0;
      String field;
   
      int i = 0;
      int length = record.getLength();
      int[] model = m_taskModel.getModel();
   
      while (i < length)
      {
         x = model[i];
   
         if (x == -1)
         {
            break;
         }
   
         field = record.getString(i++);
   
         if ((field == null) || (field.length() == 0))
         {
            continue;
         }
   
         switch (x)
         {
            case Task.PREDECESSORS:
            case Task.SUCCESSORS:
            case Task.UNIQUE_ID_PREDECESSORS:
            case Task.UNIQUE_ID_SUCCESSORS:
            {
               task.set(x, populateRelationList(field));
               break;
            }
   
            case Task.PERCENTAGE_COMPLETE:
            case Task.PERCENTAGE_WORK_COMPLETE:
            {
               try
               {
                  task.set(x, m_formats.getPercentageDecimalFormat().parse(field));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXJException ("Failed to parse percentage", ex);
               }
               break;
            }
   
            case Task.ACTUAL_COST:
            case Task.BASELINE_COST:
            case Task.BCWP:
            case Task.BCWS:
            case Task.COST:
            case Task.COST1:
            case Task.COST2:
            case Task.COST3:
            case Task.COST_VARIANCE:
            case Task.CV:
            case Task.FIXED_COST:
            case Task.REMAINING_COST:
            case Task.SV:
            {
               try
               {
                  task.set(x, m_formats.getCurrencyFormat().parse(field));
               }
                
               catch (ParseException ex)
               {
                  throw new MPXJException ("Failed to parse currency", ex);
               }               
               break;
            }
   
            case Task.ACTUAL_DURATION:
            case Task.ACTUAL_WORK:
            case Task.BASELINE_DURATION:
            case Task.BASELINE_WORK:
            case Task.DURATION:
            case Task.DURATION1:
            case Task.DURATION2:
            case Task.DURATION3:
            case Task.DURATION_VARIANCE:
            case Task.FINISH_VARIANCE:
            case Task.FREE_SLACK:
            case Task.REMAINING_DURATION:
            case Task.REMAINING_WORK:
            case Task.START_VARIANCE:
            case Task.TOTAL_SLACK:
            case Task.WORK:
            case Task.WORK_VARIANCE:
            case Task.DELAY:
            {
               task.set(x, DurationUtility.getInstance(field, m_formats.getDurationDecimalFormat(), m_locale));
               break;
            }
   
            case Task.ACTUAL_FINISH:
            case Task.ACTUAL_START:
            case Task.BASELINE_FINISH:
            case Task.BASELINE_START:
            case Task.CONSTRAINT_DATE:
            case Task.CREATE_DATE:
            case Task.EARLY_FINISH:
            case Task.EARLY_START:
            case Task.FINISH:
            case Task.FINISH1:
            case Task.FINISH2:
            case Task.FINISH3:
            case Task.FINISH4:
            case Task.FINISH5:
            case Task.LATE_FINISH:
            case Task.LATE_START:
            case Task.RESUME:
            case Task.RESUME_NO_EARLIER_THAN:
            case Task.START:
            case Task.START1:
            case Task.START2:
            case Task.START3:
            case Task.START4:
            case Task.START5:
            case Task.STOP:
            {
               try
               {
                  task.set(x, m_formats.getDateTimeFormat().parse(field));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXJException ("Failed to parse date time", ex);
               }
               break;
            }
   
            case Task.CONFIRMED:
            case Task.CRITICAL:
            case Task.FIXED:
            case Task.FLAG1:
            case Task.FLAG2:
            case Task.FLAG3:
            case Task.FLAG4:
            case Task.FLAG5:
            case Task.FLAG6:
            case Task.FLAG7:
            case Task.FLAG8:
            case Task.FLAG9:
            case Task.FLAG10:
            case Task.HIDE_BAR:
            case Task.LINKED_FIELDS:
            case Task.MARKED:
            case Task.MILESTONE:
            case Task.ROLLUP:
            case Task.SUMMARY:
            case Task.UPDATE_NEEDED:
            {
               task.set(x, ((field.equalsIgnoreCase(falseText) == true) ? Boolean.FALSE : Boolean.TRUE));
               break;
            }
   
            case Task.CONSTRAINT_TYPE:
            {
               task.set(x, ConstraintTypeUtility.getInstance(m_locale, field));
               break;
            }
   
            case Task.OBJECTS:
            case Task.OUTLINE_LEVEL:
            {
               task.set(x, Integer.valueOf(field));
               break;
            }
   
            case Task.ID:
            {
               task.setID(Integer.valueOf(field));
               break;
            }
   
            case Task.UNIQUE_ID:
            {
               task.setUniqueID(Integer.valueOf(field));
               break;
            }
   
            case Task.NUMBER1:
            case Task.NUMBER2:
            case Task.NUMBER3:
            case Task.NUMBER4:
            case Task.NUMBER5:
            {
               try
               {
                  task.set(x, m_formats.getDecimalFormat().parse(field));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXJException ("Failed to parse number", ex);
               }
               
               break;
            }
   
            case Task.PRIORITY:
            {
               task.set(x, PriorityUtility.getInstance(m_locale, field));
               break;
            }
   
            default:
            {
               task.set(x, field);
               break;
            }
         }
      }

      if (m_projectFile.getAutoWBS() == true)
      {
         task.generateWBS(null);
      }
   
      if (m_projectFile.getAutoOutlineNumber() == true)
      {
         task.generateOutlineNumber(null);
      }
   
      if (m_projectFile.getAutoOutlineLevel() == true)
      {
         task.setOutlineLevel(new Integer(1));
      }
   
      if (m_projectFile.getAutoTaskUniqueID() == true)
      {
         task.setUniqueID(new Integer(m_projectFile.getTaskUniqueID()));
      }
   
      if (m_projectFile.getAutoTaskID() == true)
      {
         task.setID(new Integer(m_projectFile.getTaskID()));
      }
   
      task.setType(task.getFixed()?TaskType.FIXED_DURATION:TaskType.FIXED_UNITS);
   }
   
   /**
    * Populates a recurring task.
    * 
    * @param record MPX record
    * @param task recurring task
    * @throws MPXJException
    */
   private void populateRecurringTask (Record record, RecurringTask task)
      throws MPXJException
   {
      task.setTaskUniqueID(record.getInteger(0));
      task.setStartDate(record.getDateTime(1));
      task.setFinishDate(record.getDateTime(2));
      task.setDuration(record.getInteger(3));
      task.setDurationType(record.getString(4));
      task.setNumberOfOccurances(record.getInteger(5));
      task.setRecurranceType(record.getInteger(6));
      task.setNotSureIndex(record.getInteger(7));      
      task.setLengthRadioIndex(record.getInteger(8));
      task.setDailyBoxRadioIndex(record.getInteger(9));
      task.setWeeklyBoxDayOfWeekIndex(record.getString(10));
      task.setMonthlyBoxRadioIndex(record.getInteger(11));
      task.setYearlyBoxRadioIndex(record.getInteger(12));
      task.setDailyBoxComboIndex(record.getInteger(13));
      task.setWeeklyBoxComboIndex(record.getInteger(14));
      task.setMonthlyBoxFirstLastComboIndex(record.getInteger(15));
      task.setMonthlyBoxDayComboIndex(record.getInteger(16));
      task.setMonthlyBoxBottomRadioFrequencyComboIndex(record.getInteger(17));
      task.setMonthlyBoxDayIndex(record.getInteger(18));
      task.setMonthlyBoxTopRadioFrequencyComboIndex(record.getInteger(19));
      task.setYearlyBoxFirstLastComboIndex(record.getInteger(20));
      task.setYearlyBoxDayComboIndex(record.getInteger(21));
      task.setYearlyBoxMonthComboIndex(record.getInteger(22));
      task.setYearlyBoxDate(record.getDateTime(23));
   }

   /**
    * Populate a resource assignment.
    * 
    * @param record MPX record
    * @param assignment resource assignment
    * @throws MPXJException
    */
   private void populateResourceAssignment (Record record, ResourceAssignment assignment)
      throws MPXJException
   {
      assignment.setResourceID(record.getInteger(0));
      assignment.setUnits(record.getUnits(1));
      assignment.setWork(record.getDuration(2));
      assignment.setPlannedWork(record.getDuration(3));
      assignment.setActualWork(record.getDuration(4));
      assignment.setOvertimeWork(record.getDuration(5));
      assignment.setCost(record.getCurrency(6));
      assignment.setPlannedCost(record.getCurrency(7));
      assignment.setActualCost(record.getCurrency(8));
      assignment.setStart(record.getDateTime(9));
      assignment.setFinish(record.getDateTime(10));
      assignment.setDelay(record.getDuration(11));
      assignment.setResourceUniqueID(record.getInteger(12));
   
      //
      // Calculate the remaining work
      //
      Duration work = assignment.getWork();
      Duration actualWork = assignment.getActualWork();
      if (work != null && actualWork != null)
      {
         if (work.getUnits() != actualWork.getUnits())
         {
            actualWork = actualWork.convertUnits(work.getUnits(), m_projectFile.getProjectHeader());
         }
         
         assignment.setRemainingWork(Duration.getInstance(work.getDuration() - actualWork.getDuration(), work.getUnits()));
      }      
      
      Resource resource = assignment.getResource();
      if (resource != null)
      {
         resource.addResourceAssignment(assignment);
      }      
   }
   
   /**
    * Populate a resource assignment workgroup instance.
    * 
    * @param record MPX record
    * @param workgroup workgroup instance
    * @throws MPXJException
    */
   private void populateResourceAssignmentWorkgroupFields (Record record, ResourceAssignmentWorkgroupFields workgroup)
      throws MPXJException
   {
      workgroup.setMessageUniqueID(record.getString(0));
      workgroup.setConfirmed(NumberUtility.getInt(record.getInteger(1))==1);
      workgroup.setResponsePending(NumberUtility.getInt(record.getInteger(1))==1);
      workgroup.setUpdateStart(record.getDateTime(3));
      workgroup.setUpdateFinish(record.getDateTime(4));
      workgroup.setScheduleID(record.getString(5));
   }

   /**
    * Populate a file creation record.
    * 
    * @param record MPX record
    * @param fcr file creation record instance
    */
   static void populateFileCreationRecord (Record record, FileCreationRecord fcr)
   {
      fcr.setProgramName(record.getString(0));
      fcr.setFileVersion(FileVersion.getInstance(record.getString(1)));
      fcr.setCodePage(record.getCodePage(2));
   }
   
   /**
    * This method returns the locale used by this MPX file.
    *
    * @return current locale
    */
   public Locale getLocale ()
   {
      return (m_locale);
   }

   /**
    * This method sets the locale to be used by this MPX file.
    *
    * @param locale locale to be used
    */
   public void setLocale (Locale locale)
   {
      m_locale = locale;
   }

   /**
    * This method sets the flag indicating that the text version of the
    * Task and Resource Table Definition records should be ignored. Ignoring
    * these records gets around the problem where MPX files have been generated
    * with incorrect taks or resource field names, but correct task or resource
    * field numbers in the numeric version of the record.
    *
    * @param flag Boolean flag
    */
   public void setIgnoreTextModels (boolean flag)
   {
      m_ignoreTextModels = flag;
   }

   /**
    * Retrieves the flag indicating that the text version of the Task and
    * Resource Table Definition records should be ignored.
    *
    * @return Boolean flag
    */
   public boolean getIgnoreTextModels ()
   {
      return (m_ignoreTextModels);
   }

   private Locale m_locale = Locale.ENGLISH;   
   private boolean m_ignoreTextModels = true;

   /**
    * Transient working data.
    */
   private ProjectFile m_projectFile;
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
   private MPXFormats m_formats;
   
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
