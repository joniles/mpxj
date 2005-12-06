/*
 * file:       MPXCalendar.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       28/11/2003
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

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Calendar;

/**
 * This class represents the a Calendar Definition record. Both base calendars
 * and calendars derived from base calendars are represented by instances
 * of this class. The class is used to define the working and non-working days
 * of the week. The default calendar defines Monday to Friday as working days.
 */
public final class MPXCalendar extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @param baseCalendar flag indicating if this is a base calendar
    */
   MPXCalendar (MPXFile file, boolean baseCalendar)
   {
      this (file, Record.EMPTY_RECORD, baseCalendar);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record record containing the data for this object.
    * @param baseCalendar flag indicating if this is a base calendar
    */
   MPXCalendar (MPXFile file, Record record, boolean baseCalendar)
   {
      super (file, 0);

      m_baseCalendarFlag = baseCalendar;
      
      if (baseCalendar == true)
      {
         setName(record.getString(0));
      }
      else
      {         
         setBaseCalendar (file.getBaseCalendar(record.getString(0)));
      }

      setWorkingDay(Day.SUNDAY, record.getInteger(1));
      setWorkingDay(Day.MONDAY, record.getInteger(2));
      setWorkingDay(Day.TUESDAY, record.getInteger(3));
      setWorkingDay(Day.WEDNESDAY, record.getInteger(4));
      setWorkingDay(Day.THURSDAY, record.getInteger(5));
      setWorkingDay(Day.FRIDAY, record.getInteger(6));
      setWorkingDay(Day.SATURDAY, record.getInteger(7));

      if (file.getAutoCalendarUniqueID() == true)
      {
         setUniqueID (file.getCalendarUniqueID());
      }
   }

   /**
    * Used to add exceptions to the calendar. The MPX standard defines
    * a limit of 250 exceptions per calendar.
    *
    * @return <tt>MPXCalendarException</tt>
    * @throws MPXException if limit on number of exceptions is reached
    */
   public MPXCalendarException addCalendarException ()
      throws MPXException
   {
      return (addCalendarException (Record.EMPTY_RECORD));
   }

   /**
    * Used to add exceptions to the calendar. The MPX standard defines
    * a limit of 250 exceptions per calendar.
    *
    * @param record data from the MPX file for this object.
    * @return <tt>MPXCalendarException</tt>
    * @throws MPXException
    */
   MPXCalendarException addCalendarException (Record record)
      throws MPXException
   {
      MPXCalendarException bce = new MPXCalendarException(getParentFile(), this, record);
      m_exceptions.add(bce);
      return (bce);
   }

   /**
    * This method retrieves a list of exceptions to the current calendar.
    *
    * @return List of calendar exceptions
    */
   public LinkedList getCalendarExceptions ()
   {
      return (m_exceptions);
   }

   /**
    * Used to add working hours to the calendar. Note that the MPX file
    * definition allows a maximum of 7 calendar hours records to be added to
    * a single calendar.
    *
    * @param day day number
    * @return new MPXCalendarHours instance
    * @throws MPXException if maximum number of records is exceeded
    */
   public MPXCalendarHours addCalendarHours(Day day)
      throws MPXException
   {
      MPXCalendarHours bch = new MPXCalendarHours (getParentFile(), this, Record.EMPTY_RECORD);

      bch.setDay (day);
      m_hours[day.getValue()-1] = bch;

      return (bch);
   }

   /**
    * Used to add working hours to the calendar. Note that the MPX file
    * definition allows a maximum of 7 calendar hours records to be added to
    * a single calendar.
    *
    * @param record data from the MPX file for this object.
    * @return <tt>MPXCalendarHours</tt>
    * @throws MPXException if maximum number of records is exceeded
    */
   MPXCalendarHours addCalendarHours (Record record)
      throws MPXException
   {
      MPXCalendarHours bch = new MPXCalendarHours(getParentFile(), this, record);
      m_hours[bch.getDay().getValue()-1] = bch;
      return (bch);
   }

   /**
    * This method retrieves the calendar hours for the specified day.
    *
    * @param day Day instance
    * @return calendar hours
    */
   public MPXCalendarHours getCalendarHours (Day day)
   {
      return (m_hours[day.getValue()-1]);
   }


   /**
    * Calendar name
    *
    * @param name - string calendar name
    */
   public void setName (String name)
   {
      m_name = name;
   }

   /**
    * Calendar name
    *
    * @return - name of calendar
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Sets the MPXCalendar instance from which this calendar is derived.
    *
    * @param calendar base calendar instance
    */
   public void setBaseCalendar (MPXCalendar calendar)
   {
      m_baseCalendar = calendar;
   }

   /**
    * Retrieve the MPXCalendar instance from which this calendar is derived.
    *
    * @return MPXCalendar instance
    */
   public MPXCalendar getBaseCalendar ()
   {
      return (m_baseCalendar);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer buf = new StringBuffer();
      char delimiter = getParentFile().getDelimiter();

      if (m_baseCalendar == null)
      {
         buf.append (BASE_CALENDAR_RECORD_NUMBER);
         buf.append (delimiter);
         if (m_name != null)
         {
            buf.append (m_name);
         }
      }
      else
      {
         buf.append (RESOURCE_CALENDAR_RECORD_NUMBER);
         buf.append (delimiter);
         buf.append (m_baseCalendar.getName());
      }

      for (int loop=0; loop < m_days.length; loop++)
      {
         buf.append (delimiter);
         buf.append (m_days[loop]);
      }

      buf.append (MPXFile.EOL);

      for (int loop=0; loop < m_hours.length; loop++)
      {
         if (m_hours[loop] != null)
         {
            buf.append (m_hours[loop].toString());
         }
      }

      if (m_exceptions.isEmpty() == false)
      {
         Iterator iter = m_exceptions.iterator();
         while (iter.hasNext() == true)
         {
            buf.append((iter.next()).toString());
         }
      }

      return (buf.toString());
   }

   /**
    * Method indicating whether a day is a working or non-working day.
    *
    * @param day required day
    * @return true if this is a working day
    */
   public boolean isWorkingDay (Day day)
   {
      int value = m_days[day.getValue()-1];
      boolean result;

      if (value == DEFAULT)
      {
         MPXCalendar cal = getBaseCalendar();
         result = cal.isWorkingDay(day);
      }
      else
      {
         result = (value == WORKING);
      }

      return (result);
   }

   /**
    * This method allows the retrieval of the actual working day flag,
    * which can take the values DEFAULT, WORKING, or NONWORKING. This differs
    * from ths isWorkingDay method as it retrieves the actual flag value.
    * The isWorkingDay method will always refer back to the base calendar
    * to get a boolean value if the underlying flag value is DEFAULT. If
    * isWorkingDay were the only method available to access this flag,
    * it would not be possible to determine that a resource calendar
    * had one or moe flags set to DEFAULT.
    *
    * @param day required day
    * @return value of underlying working day flag
    */
   public int getWorkingDay (Day day)
   {
      return (m_days[day.getValue()-1]);
   }

   /**
    * This is a convenience method provided to allow a day to be set
    * as working or non-working, by using the day number to
    * identify the required day.
    *
    * @param day required day
    * @param working flag indicating if the day is working/non-working/default
    */
   public void setWorkingDay (Day day, int working)
   {
      m_days[day.getValue()-1] = working;
   }

   /**
    * convenience method for setting working or non-working days.
    *
    * @param day required day
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay (Day day, boolean working)
   {
      setWorkingDay (day, (working==true?WORKING:NON_WORKING));
   }

   /**
    * This is a convenience method provided to allow a day to be set
    * as working or non-working, by using the day number to
    * identify the required day.
    *
    * @param day required day
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay (Day day, Integer working)
   {
      int value;

      if (working == null)
      {
         if (m_baseCalendarFlag == false)
         {
            value = DEFAULT;
         }
         else
         {
            value = WORKING;
         }
      }
      else
      {
         value = working.intValue();
      }

      setWorkingDay (day, value);
   }

   /**
    * This is a convenience method used to add a default set of calendar
    * hours to a calendar.
    *
    * @throws MPXException normally thrown on parse errors
    */
   public void addDefaultCalendarHours ()
      throws MPXException
   {
      try
      {
         MPXCalendarHours hours;
         SimpleDateFormat df = new SimpleDateFormat ("HH:mm");
         Date from1 = df.parse ("08:00");
         Date to1 = df.parse ("12:00");
         Date from2 = df.parse ("13:00");
         Date to2 = df.parse ("17:00");

         hours = addCalendarHours (Day.SUNDAY);

         hours = addCalendarHours (Day.MONDAY);
         hours.addDateRange(new DateRange (from1, to1));
         hours.addDateRange(new DateRange (from2, to2));

         hours = addCalendarHours (Day.TUESDAY);
         hours.addDateRange(new DateRange (from1, to1));
         hours.addDateRange(new DateRange (from2, to2));

         hours = addCalendarHours (Day.WEDNESDAY);
         hours.addDateRange(new DateRange (from1, to1));
         hours.addDateRange(new DateRange (from2, to2));
         
         hours = addCalendarHours (Day.THURSDAY);
         hours.addDateRange(new DateRange (from1, to1));
         hours.addDateRange(new DateRange (from2, to2));

         hours = addCalendarHours (Day.FRIDAY);
         hours.addDateRange(new DateRange (from1, to1));
         hours.addDateRange(new DateRange (from2, to2));         

         hours = addCalendarHours (Day.SATURDAY);
      }

      catch (ParseException ex)
      {
         throw new MPXException (MPXException.INVALID_TIME, ex);
      }
   }

   /**
    * This method is provided to allow an absolute period of time
    * represented by start and end dates into a duration in working
    * days based on this calendar instance. This method takes account
    * of any exceptions defined for this calendar.
    *
    * @param startDate start of the period
    * @param endDate end of the period
    * @return new MPXDuration object
    */
   public MPXDuration getDuration (Date startDate, Date endDate)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      int dayIndex = cal.get(Calendar.DAY_OF_WEEK);
      int days = getDaysInRange (startDate, endDate);
      int duration = 0;

      while (days > 0)
      {
         if (isWorkingDate(cal.getTime(), Day.getInstance(dayIndex)) == true)
         {
            ++duration;
         }

         --days;

         ++dayIndex;
         if (dayIndex > 7)
         {
            dayIndex = 1;
         }

         cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
      }

      return (MPXDuration.getInstance (duration, TimeUnit.DAYS));
   }

   /**
    * This method generates an end date given a start date and a duration.
    * The underlying implementation of this method uses an <i>approximation</i>
    * in order to conver the supplied duration to a number of days. This number
    * of days is treated as the required offset in <i>working</i> days from
    * the startDate parameter. The method then steps through that number of
    * working days (as defined by this calendar), and returns the end date
    * that it finds. Note that this method can deal with both positive and
    * negative duration values.
    *
    * @param startDate start date
    * @param duration required working offset, will be converted to working days
    * @return end date
    */
   public Date getDate (Date startDate, MPXDuration duration)
   {
      MPXDuration dur = duration.convertUnits(TimeUnit.DAYS, getParentFile().getProjectHeader());
      int days = (int)dur.getDuration();
      boolean negative;

      if (days < 0)
      {
         negative = true;
         days = -days;
      }
      else
      {
         negative = false;
      }

      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      int dayIndex = cal.get(Calendar.DAY_OF_WEEK);

      while (days > 0)
      {
         if (isWorkingDate(cal.getTime(), Day.getInstance(dayIndex)) == true)
         {
            --days;
         }

         if (negative == false)
         {
            ++dayIndex;
            if (dayIndex > 7)
            {
               dayIndex = 1;
            }

            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
         }
         else
         {
            --dayIndex;
            if (dayIndex < 1)
            {
               dayIndex = 7;
            }

            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
         }
      }

      return (cal.getTime());
   }

   /**
    * This method allows the caller to determine if a given date is a
    * working day. This method takes account of calendar exceptions.
    *
    * @param date Date to be tested
    * @return boolean value
    */
   public boolean isWorkingDate (Date date)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
      return (isWorkingDate (date, day));
   }

   /**
    * This private method allows the caller to determine if a given date is a
    * working day. This method takes account of calendar exceptions. It assumes
    * that the caller has already calculated the day of the week on which
    * the given day falls.
    *
    * @param date Date to be tested
    * @param day Day of the week for the date under test
    * @return boolean flag
    */
   private boolean isWorkingDate (Date date, Day day)
   {
      boolean result = false;

      //
      // Test to see if the date is covered by an exception
      //
      Iterator iter = m_exceptions.iterator();
      MPXCalendarException exception = null;

      while (iter.hasNext() == true)
      {
         exception = (MPXCalendarException)iter.next();
         if (exception.contains(date) == true)
         {
            result = exception.getWorkingValue();
            break;
         }
         else
         {
            exception = null;
         }
      }

      //
      // If the date is not covered by an exception, use the
      // normal test for working days
      //
      if (exception == null)
      {
         result = isWorkingDay (day);
      }

      return (result);
   }


   /**
    * This method calculates the absolute number of days between two dates.
    * Note that where two date objects are provided that fall on the same
    * day, this method will return one not zero. Note also that this method
    * assumes that the dates are passed in the correct order, i.e.
    * startDate < endDate.
    *
    * @param startDate Start date
    * @param endDate End date
    * @return number of days in the date range
    */
   private int getDaysInRange (Date startDate, Date endDate)
   {
      int result;
      Calendar cal = Calendar.getInstance();
      cal.setTime(endDate);
      int endDateYear = cal.get(Calendar.YEAR);
      int endDateDayOfYear = cal.get(Calendar.DAY_OF_YEAR);

      cal.setTime(startDate);

      if (endDateYear == cal.get(Calendar.YEAR))
      {
         result = (endDateDayOfYear - cal.get(Calendar.DAY_OF_YEAR)) + 1;
      }
      else
      {
         result = 0;
         do
         {
            result += (cal.getActualMaximum(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR)) + 1;
            cal.roll(Calendar.YEAR, 1);
            cal.set(Calendar.DAY_OF_YEAR, 1);
         }
         while (cal.get(Calendar.YEAR) < endDateYear);
         result += endDateDayOfYear;
      }

      return (result);
   }

   /**
    * This method returns a flag indicating if this MPXCalendar instance
    * represents a base calendar.
    *
    * @return boolean flag
    */
   public boolean isBaseCalendar ()
   {
      return (m_baseCalendarFlag);
   }

   /**
    * Modifier method to set the unique ID of this calendar
    *
    * @param uniqueID unique identifier
    */
   public void setUniqueID (int uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   /**
    * Accessor method to retrieve the unique ID of this calendar
    *
    * @return calendar unique identifier
    */
   public int getUniqueID ()
   {
      return (m_uniqueID);
   }

   /**
    * Retrieve the resource to which this calendar is linked.
    * 
    * @return resource instance
    */
   public Resource getResource ()
   {
      return (m_resource);
   }
   
   /**
    * Sets the resource to which this calendar is linked.
    * 
    * @param resource resource instance
    */
   public void setResource (Resource resource)
   {
      m_resource = resource;
      m_name = m_resource.getName();
   }
   
   /**
    * Removes this calendar from the project.
    */
   public void remove ()
   {
      getParentFile().removeBaseCalendar(this);
   }
   
   /**
    * Unique identifier of this calendar
    */
   private int m_uniqueID;

   /**
    * Calendar name, normally only populated if this is a base calendar.
    */
   private String m_name;

   private boolean m_baseCalendarFlag;
   
   /**
    * Base calendar from which this calendar is derived.
    */
   private MPXCalendar m_baseCalendar;
   
   /**
    * Array holding working/non-working/default flags for each
    * day of the week.
    */
   private int[] m_days = new int [7];

   /**
    * List of exceptions to the base calendar.
    */
   private LinkedList m_exceptions = new LinkedList();

   /**
    * List of working hours for the base calendar.
    */
   private MPXCalendarHours[] m_hours = new MPXCalendarHours[7];

   /**
    * This resource to which this calendar is attached.
    */
   private Resource m_resource;
   
   /**
    * Default base calendar name to use when none is supplied
    */
   public static final String DEFAULT_BASE_CALENDAR_NAME = "Standard";

   /**
    * Constant used to represent a non-working day
    */
   public static final int NON_WORKING = 0;

   /**
    * Constant used to represent a working day
    */
   public static final int WORKING = 1;

   /**
    * Copnstant used to represent that a day in a derived calendar used
    * the value specified in the base calendar to indicate if it is working
    * or not.
    */
   public static final int DEFAULT = 2;


   /**
    * Constant containing the record number associated with this record
    * if this instance represents a base calendar.
    */
   static final int BASE_CALENDAR_RECORD_NUMBER = 20;

   /**
    * Constant containing the record number associated with this record
    * if this instance represents a resource calendar.
    */
   static final int RESOURCE_CALENDAR_RECORD_NUMBER = 55;
}
