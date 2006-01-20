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
