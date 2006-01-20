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

package com.tapsterrock.mpx;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * This class creates a new MPXFile instance by reading an MPX file.
 */
public final class MPXReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   public ProjectFile read (InputStream is)
      throws MPXException
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
            throw new MPXException(MPXException.INVALID_FILE);
         }
   
         m_projectFile = new ProjectFile ();
         m_projectFile.setLocale(m_locale);
         m_projectFile.setDelimiter((char)data[3]);
         
         bis.reset();
   
         //
         // Read the file creation record. At this point we are reading
         // directly from an input stream so no character set decoding is
         // taking place. We assume that any text in this record will not
         // require decoding.
         //
         Tokenizer tk = new InputStreamTokenizer(bis);
         tk.setDelimiter(m_projectFile.getDelimiter());
   
         Record record;
         String number;
   
         //
         // Add the header record
         //
         m_projectFile.add(Integer.toString(MPXConstants.FILE_CREATION_RECORD_NUMBER), new Record(m_projectFile, tk));
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
         tk.setDelimiter(m_projectFile.getDelimiter());
   
         //
         // Read the remainder of the records
         //
         while (tk.getType() != Tokenizer.TT_EOF)
         {
            record = new Record(m_projectFile, tk);
            number = record.getRecordNumber();
   
            if (number != null)
            {
               m_projectFile.add(number, record);
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
         throw new MPXException(MPXException.READ_ERROR + " (failed at line " + line + ")", ex);
      }      
   }
     
   /**
    * Populates currency settings.
    * 
    * @param record MPX record
    * @param projectHeader project header
    */
   static void populateCurrencySettings (Record record, ProjectHeader projectHeader)
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
    * @throws MPXException
    */
   static void populateDefaultSettings (Record record, ProjectHeader projectHeader)
      throws MPXException
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
   static void populateDateTimeSettings (Record record, ProjectHeader projectHeader)
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
   private static Date getTimeFromInteger (Integer time)
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
    * @throws MPXException
    */
   static void populateProjectHeader (Record record, ProjectHeader projectHeader)
      throws MPXException
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
    * @throws MPXException
    */
   static void populateCalendarHours (Record record, MPXCalendarHours hours)
      throws MPXException
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
    * @throws MPXException
    */
   static void populateCalendarException(Record record, MPXCalendarException exception)
      throws MPXException
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
    * @param file parent file
    * @param record MPX record
    * @param calendar calendar instance
    */
   static void populateCalendar(ProjectFile file, Record record, MPXCalendar calendar)
   {
      if (calendar.isBaseCalendar() == true)
      {
         calendar.setName(record.getString(0));
      }
      else
      {         
         calendar.setBaseCalendar (file.getBaseCalendar(record.getString(0)));
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
    * @param file project file instance
    * @param resource resource instance
    * @param record MPX record
    * @throws MPXException
    */
   static void populateResource (ProjectFile file, Resource resource, Record record)
      throws MPXException
   {
      ResourceModel resourceModel = file.getResourceModel();
   
      int i = 0;
      int length = record.getLength();
      int[] model = resourceModel.getModel();
   
      while (i < length)
      {
         int x = model[i];
         if (x == -1)
         {
            break;
         }
   
         String field = record.getString (i++);
   
         if (field == null || field.length() == 0)
         {
            continue;
         }
   
         switch (x)
         {
            case Resource.OBJECTS:
            {
               resource.set(x,Integer.valueOf(field));
               break;
            }
   
            case Resource.ID:
            {
               resource.setID(Integer.valueOf(field));
               break;
            }
   
            case Resource.UNIQUE_ID:
            {
               resource.setUniqueID(Integer.valueOf(field));
               break;
            }
   
            case Resource.MAX_UNITS:
            {
               try
               {
                  resource.set (x, new Double(file.getUnitsDecimalFormat().parse(field).doubleValue() * 100));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXException ("Failed to parse units", ex);
               }
               
               break;
            }
   
            case Resource.PERCENT_WORK_COMPLETE:
            case Resource.PEAK_UNITS:
            {
               try
               {
                  resource.set(x, file.getPercentageDecimalFormat().parse(field));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXException ("Failed to parse percentage", ex);
               }
               break;
            }
   
            case Resource.COST:
            case Resource.COST_PER_USE:
            case Resource.COST_VARIANCE:
            case Resource.BASELINE_COST:
            case Resource.ACTUAL_COST:
            case Resource.REMAINING_COST:
            {
               try
               {
                  resource.set(x, file.getCurrencyFormat().parse(field));
               }
                
               catch (ParseException ex)
               {
                  throw new MPXException ("Failed to parse currency", ex);
               }               
               break;
            }
   
            case Resource.OVERTIME_RATE:
            case Resource.STANDARD_RATE:
            {
               resource.set (x, new MPXRate(file.getCurrencyFormat(), field, file.getLocale()));
               break;
            }
   
            case Resource.REMAINING_WORK:
            case Resource.OVERTIME_WORK:
            case Resource.BASELINE_WORK:
            case Resource.ACTUAL_WORK:
            case Resource.WORK:
            case Resource.WORK_VARIANCE:
            {
               resource.set (x, MPXDuration.getInstance (field, file.getDurationDecimalFormat(), file.getLocale()));
               break;
            }
   
            case Resource.ACCRUE_AT:
            {
               resource.set (x, AccrueType.getInstance (field, file.getLocale()));
               break;
            }
   
            case Resource.OVERALLOCATED:
            {
               resource.set (x, (field.equals("No")==true?Boolean.FALSE:Boolean.TRUE));
               break;
            }
   
            default:
            {
               resource.set (x, field);
               break;
            }
         }
      }
   
      if (file.getAutoResourceUniqueID() == true)
      {
         resource.setUniqueID (file.getResourceUniqueID ());
      }
   
      if (file.getAutoResourceID() == true)
      {
         resource.setID (file.getResourceID ());
      }
   }
   
   /**
    * Populates a relation list.
    * 
    * @param data MPX relation list data
    * @param file parent file
    * @return relation list
    * @throws MPXException
    */
   private static List populateRelationList (String data, ProjectFile file)
      throws MPXException
   {
      List list = new LinkedList ();
      
      int length = data.length();
      char sepchar = file.getDelimiter();
      
      if (length != 0)
      {
         int start = 0;
         int end = 0;
   
         while (end != length)
         {
            end = data.indexOf(sepchar, start);
   
            if (end == -1)
            {
               end = length;
            }
   
            list.add(new Relation(data.substring(start, end).trim(), file));
   
            start = end + 1;
         }
      }
      
      return (list);
   }
   
   /**
    * Populates a task instance.
    * 
    * @param file parent file
    * @param record MPX record
    * @param task task instance
    * @throws MPXException
    */
   static void populateTask (ProjectFile file, Record record, Task task)
      throws MPXException
   {
      String falseText = LocaleData.getString(file.getLocale(), LocaleData.NO);
   
      TaskModel taskModel = file.getTaskModel();
   
      int x = 0;
      String field;
   
      int i = 0;
      int length = record.getLength();
      int[] model = taskModel.getModel();
   
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
               task.set(x, populateRelationList(field, file));
               break;
            }
   
            case Task.PERCENTAGE_COMPLETE:
            case Task.PERCENTAGE_WORK_COMPLETE:
            {
               try
               {
                  task.set(x, file.getPercentageDecimalFormat().parse(field));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXException ("Failed to parse percentage", ex);
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
                  task.set(x, file.getCurrencyFormat().parse(field));
               }
                
               catch (ParseException ex)
               {
                  throw new MPXException ("Failed to parse currency", ex);
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
               task.set(x, MPXDuration.getInstance(field, file.getDurationDecimalFormat(), file.getLocale()));
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
                  task.set(x, file.getDateTimeFormat().parse(field));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXException ("Failed to parse date time", ex);
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
               task.set(x, ConstraintType.getInstance(file.getLocale(), field));
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
                  task.set(x, file.getDecimalFormat().parse(field));
               }
               
               catch (ParseException ex)
               {
                  throw new MPXException ("Failed to parse number", ex);
               }
               
               break;
            }
   
            case Task.PRIORITY:
            {
               task.set(x, Priority.getInstance(file.getLocale(), field));
               break;
            }
   
            default:
            {
               task.set(x, field);
               break;
            }
         }
      }

      if (file.getAutoWBS() == true)
      {
         task.generateWBS(null);
      }
   
      if (file.getAutoOutlineNumber() == true)
      {
         task.generateOutlineNumber(null);
      }
   
      if (file.getAutoOutlineLevel() == true)
      {
         task.setOutlineLevel(1);
      }
   
      if (file.getAutoTaskUniqueID() == true)
      {
         task.setUniqueID(file.getTaskUniqueID());
      }
   
      if (file.getAutoTaskID() == true)
      {
         task.setID(file.getTaskID());
      }
   
      if (task.getFixedValue() == true)
      {
         task.setType(TaskType.FIXED_DURATION);
      }
      else
      {
         task.setType(TaskType.FIXED_UNITS);
      }
   }
   
   /**
    * Populates a recurring task.
    * 
    * @param file parent file
    * @param record MPX record
    * @param task recurring task
    * @throws MPXException
    */
   static void populateRecurringTask (ProjectFile file, Record record, RecurringTask task)
      throws MPXException
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
    * @param file parent file
    * @param record MPX record
    * @param assignment resource assignment
    * @throws MPXException
    */
   static void populateResourceAssignment (ProjectFile file, Record record, ResourceAssignment assignment)
      throws MPXException
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
      MPXDuration work = assignment.getWork();
      MPXDuration actualWork = assignment.getActualWork();
      if (work != null && actualWork != null)
      {
         if (work.getUnits() != actualWork.getUnits())
         {
            actualWork = actualWork.convertUnits(work.getUnits(), file.getProjectHeader());
         }
         
         assignment.setRemainingWork(MPXDuration.getInstance(work.getDuration() - actualWork.getDuration(), work.getUnits()));
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
    * @param file parent file
    * @param record MPX record
    * @param workgroup workgroup instance
    * @throws MPXException
    */
   static void populateResourceAssignmentWorkgroupFields (ProjectFile file, Record record, ResourceAssignmentWorkgroupFields workgroup)
      throws MPXException
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

   private Locale m_locale = Locale.ENGLISH;   
   private ProjectFile m_projectFile;
}
