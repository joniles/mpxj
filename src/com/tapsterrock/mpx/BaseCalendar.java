/*
 * file:       BaseCalendar.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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
 * This class represents the Base Calendar Definition record. It is used to
 * define the working and non-working days of the week. The default calendar
 * defined Monday to Friday as working days.
 */
public class BaseCalendar extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   BaseCalendar (MPXFile file)
   {
      super (file);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record record containing the data for this object.
    */
   BaseCalendar (MPXFile file, Record record)
   {
      super (file);

      setName(record.getString(0));
      setSunday(record.getByte(1));
      setMonday(record.getByte(2));
      setTuesday(record.getByte(3));
      setWednesday(record.getByte(4));
      setThursday(record.getByte(5));
      setFriday(record.getByte(6));
      setSaturday(record.getByte(7));
   }

   /**
    * Used to add exceptions to the calendar. The MPX standard defines
    * a limit of 250 exceptions per calendar.
    *
    * @return <tt>BaseCalendarException</tt>
    * @throws MPXException if limit on number of exceptions is reached
    */
   public BaseCalendarException addBaseCalendarException ()
      throws MPXException
   {
      return (addBaseCalendarException (Record.EMPTY_RECORD));
   }

   /**
    * Used to add exceptions to the calendar. The MPX standard defines
    * a limit of 250 exceptions per calendar.
    *
    * @param record data from the MPX file for this object.
    * @return <tt>BaseCalendarException</tt>
    * @throws MPXException if limit on number of exceptions is reached
    */
   public BaseCalendarException addBaseCalendarException (Record record)
      throws MPXException
   {
      if (m_exceptions.size() == MAX_EXCEPTIONS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      BaseCalendarException bce = new BaseCalendarException(getParent(), record);
      m_exceptions.add(bce);
      return (bce);
   }

   /**
    * Used to add working hours to the calendar. Note that the MPX file
    * definitiona allows a maximum of 7 calendar hours records to be added to
    * a single calendar.
    *
    * @return <tt>BaseCalendarHours</tt>
    * @throws MPXException if maximum number of records is exceeded
    */
   public BaseCalendarHours addBaseCalendarHours()
      throws MPXException
   {
      return (addBaseCalendarHours (Record.EMPTY_RECORD));
   }

   /**
    * Used to add working hours to the calendar. Note that the MPX file
    * definitiona allows a maximum of 7 calendar hours records to be added to
    * a single calendar.
    *
    * @param record data from the MPX file for this object.
    * @return <tt>BaseCalendarHours</tt>
    * @throws MPXException if maximum number of records is exceeded
    */
   public BaseCalendarHours addBaseCalendarHours (Record record)
      throws MPXException
   {
      if (m_hours.size() == MAX_CALENDAR_HOURS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      BaseCalendarHours bch = new BaseCalendarHours(getParent(), record);
      m_hours.add(bch);
      return bch;
   }

   /**
    * Calendar name
    *
    * @return - name of calendar
    */
   public String getName()
   {
      return (String)get(NAME);
   }

   /**
    * Calendar name
    *
    * @param val - string calendar name
    */
   public void setName(String val)
   {
      put (NAME, val);
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @return non-working=0, working=1
    */
   public Byte getSunday()
   {
      return ((Byte)get(SUNDAY));
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @param val non-working=0, working=1
    */
   public void setSunday (Byte val)
   {
      put (SUNDAY, val);
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @return non-working=0, working=1
    */
   public Byte getMonday()
   {
      return ((Byte)get(MONDAY));
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @param val non-working=0, working=1
    */
   public void setMonday (Byte val)
   {
      put (MONDAY, val);
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @return non-working=0, working=1
    */
   public Byte getTuesday()
   {
      return ((Byte)get(TUESDAY));
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @param val non-working=0, working=1
    */
   public void setTuesday (Byte val)
   {
      put (TUESDAY, val);
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @return non-working=0, working=1
    */
   public Byte getWednesday()
   {
      return ((Byte)get(WEDNESDAY));
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @param val non-working=0, working=1
    */
   public void setWednesday (Byte val)
   {
      put (WEDNESDAY, val);
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @return non-working=0, working=1
    */
   public Byte getThursday()
   {
      return ((Byte)get(THURSDAY));
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @param val non-working=0, working=1
    */
   public void setThursday (Byte val)
   {
      put (THURSDAY, val);
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @return non-working=0, working=1
    */
   public Byte getFriday()
   {
      return ((Byte)get(FRIDAY));
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @param val non-working=0, working=1
    */
   public void setFriday (Byte val)
   {
      put (FRIDAY, val);
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @return non-working=0, working=1
    */
   public Byte getSaturday()
   {
      return ((Byte)get(SATURDAY));
   }

   /**
    * Flag indicating if this day is working or non-working.
    *
    * @param val non-working=0, working=1
    */
   public void setSaturday (Byte val)
   {
      put (SATURDAY, val);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer buf = new StringBuffer(super.toString(RECORD_NUMBER));

      if (m_hours.isEmpty() == false)
      {
         Iterator iter = m_hours.iterator();
         while (iter.hasNext() == true)
         {
            buf.append((iter.next()).toString());
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
    * @param day number of required day (1=Sunday, 7=Saturday)
    * @return true if this is a working day
    * @throws MPXException when an invalid day is specified
    */
   public boolean isWorkingDay (int day)
      throws MPXException
   {
      boolean result;

      Byte working = (Byte)get(getKey(day));

      if (working == null || working.intValue() == 0)
      {
         result = false;
      }
      else
      {
         result = true;
      }

      return (result);
   }

   private Integer getKey (int day)
      throws MPXException
   {
      Integer key;

      switch (day)
      {
         case 1:
         {
            key = SUNDAY;
            break;
         }

         case 2:
         {
            key = MONDAY;
            break;
         }

         case 3:
         {
            key = TUESDAY;
            break;
         }

         case 4:
         {
            key = WEDNESDAY;
            break;
         }

         case 5:
         {
            key = THURSDAY;
            break;
         }

         case 6:
         {
            key = FRIDAY;
            break;
         }

         case 7:
         {
            key = SATURDAY;
            break;
         }

         default:
         {
            throw new MPXException ("Invalid day");
         }
      }

      return (key);
   }

   /**
    * This is a convenience method used to add a default set of calendar
    * hours to a calendar.
    *
    * @throws MPXException normally thrown on parse errors
    */
   public void addDefaultBaseCalendarHours ()
      throws MPXException
   {
      try
      {
         BaseCalendarHours hours;
         SimpleDateFormat df = new SimpleDateFormat ("HH:mm");
         Date from1 = df.parse ("08:00");
         Date to1 = df.parse ("12:00");
         Date from2 = df.parse ("13:00");
         Date to2 = df.parse ("17:00");

         hours = addBaseCalendarHours ();
         hours.setDay(BaseCalendarHours.SUNDAY);

         hours = addBaseCalendarHours ();
         hours.setDay(BaseCalendarHours.MONDAY);
         hours.setFromTime1 (from1);
         hours.setToTime1 (to1);
         hours.setFromTime2 (from2);
         hours.setToTime2 (to2);

         hours = addBaseCalendarHours ();
         hours.setDay(BaseCalendarHours.TUESDAY);
         hours.setFromTime1 (from1);
         hours.setToTime1 (to1);
         hours.setFromTime2 (from2);
         hours.setToTime2 (to2);

         hours = addBaseCalendarHours ();
         hours.setDay(BaseCalendarHours.WEDNESDAY);
         hours.setFromTime1 (from1);
         hours.setToTime1 (to1);
         hours.setFromTime2 (from2);
         hours.setToTime2 (to2);

         hours = addBaseCalendarHours ();
         hours.setDay(BaseCalendarHours.THURSDAY);
         hours.setFromTime1 (from1);
         hours.setToTime1 (to1);
         hours.setFromTime2 (from2);
         hours.setToTime2 (to2);

         hours = addBaseCalendarHours ();
         hours.setDay(BaseCalendarHours.FRIDAY);
         hours.setFromTime1 (from1);
         hours.setToTime1 (to1);
         hours.setFromTime2 (from2);
         hours.setToTime2 (to2);

         hours = addBaseCalendarHours ();
         hours.setDay(BaseCalendarHours.SATURDAY);
      }

      catch (ParseException ex)
      {
         throw new MPXException (MPXException.INVALID_TIME, ex);
      }
   }

   /**
    * This method is provided to allow an absolute period of time
    * represented by start and end dates into a duration in working
    * days based on this calendar instance.
    *
    * @param startDate start of the period
    * @param endDate end of the period
    * @return new MPXDuration object
    * @throws MPXException if an invalid day is specified
    */
   public MPXDuration getDuration (Date startDate, Date endDate)
      throws MPXException
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      int day = cal.get(Calendar.DAY_OF_WEEK);
      int days = getDaysInRange (startDate, endDate);
      int duration = 0;

      while (days > 0)
      {
         if (isWorkingDay(day) == true)
         {
            ++duration;
         }

         --days;

         ++day;
         if (day > 7)
         {
            day = 1;
         }
      }

      return (new MPXDuration (duration, TimeUnit.DAYS));
   }


   /**
    * This method calculates the absolute number of days between two dates.
    * Note that where two date objects are provided that fall on the same
    * day, this method will return one not zero.
    */
   private int getDaysInRange (Date startDate, Date endDate)
   {
      long start = startDate.getTime() / MS_PER_DAY;
      long end = endDate.getTime() / MS_PER_DAY;
      long diff = end - start;
      if (diff < 0)
      {
         diff = -diff;
      }

      return ((int)(diff + 1));
   }

   /**
    * Listof exceptions to the base calendar.
    */
   private LinkedList m_exceptions = new LinkedList();

   /**
    * List of working hours for the base calendar.
    */
   private LinkedList m_hours = new LinkedList();


   private static final long MS_PER_DAY = (long)(1000 * 60 * 60 * 24);

   /**
    * Constant used to retrieve the name of the calendar
    */
   private static final Integer NAME = new Integer(0);

   /**
    * Constant used to retrieve the data for Sunday
    */
   private static final Integer SUNDAY = new Integer(1);

   /**
    * Constant used to retrieve the data for Monday
    */
   private static final Integer MONDAY = new Integer(2);

   /**
    * Constant used to retrieve the data for Tuesday
    */
   private static final Integer TUESDAY = new Integer(3);

   /**
    * Constant used to retrieve the data for Wednesday
    */
   private static final Integer WEDNESDAY = new Integer(4);

   /**
    * Constant used to retrieve the data for Thursday
    */
   private static final Integer THURSDAY = new Integer(5);

   /**
    * Constant used to retrieve the data for Friday
    */
   private static final Integer FRIDAY = new Integer(6);

   /**
    * Constant used to retrieve the data for Saturday
    */
   private static final Integer SATURDAY = new Integer(7);

   /**
    * Constant used to represent non-working days
    */
   public static final Byte NONWORKING = new Byte ((byte)0);

   /**
    * Constant used to represent working days
    */
   public static final Byte WORKING = new Byte ((byte)1);

   /**
    * Constant representing maximum number of BaseCalendarHours
    * records per BaseCalendar.
    */
   static final int MAX_CALENDAR_HOURS = 7;

   /**
    * Constant representing maximum number of BaseCalendarException records
    * per BaseCalendar.
    */
   static final int MAX_EXCEPTIONS = 250;

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 20;
}