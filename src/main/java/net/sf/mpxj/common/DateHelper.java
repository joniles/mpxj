/*
 * file:       DateHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       Jan 18, 2006
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

package net.sf.mpxj.common;

import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.TimeZone;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

/**
 * Utility methods for manipulating dates.
 */
public final class DateHelper
{
   /**
    * Constructor.
    */
   private DateHelper()
   {
      // private constructor to prevent instantiation
   }

   /**
    * Returns a new Date instance whose value
    * represents the start of the day (i.e. the time of day is 00:00:00.000)
    *
    * @param date date to convert
    * @return day start date
    */
   public static Date getDayStartDate(Date date)
   {
      if (date != null)
      {
         Calendar cal = popCalendar(date);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         date = cal.getTime();
         pushCalendar(cal);
      }
      return (date);
   }

   /**
    * Returns a new Date instance whose value
    * represents the end of the day (i.e. the time of days is 11:59:59.999)
    *
    * @param date date to convert
    * @return day start date
    */
   public static Date getDayEndDate(Date date)
   {
      if (date != null)
      {
         Calendar cal = popCalendar(date);
         cal.set(Calendar.MILLISECOND, 999);
         cal.set(Calendar.SECOND, 59);
         cal.set(Calendar.MINUTE, 59);
         cal.set(Calendar.HOUR_OF_DAY, 23);
         date = cal.getTime();
         pushCalendar(cal);
      }
      return (date);
   }

   /**
    * This method resets the date part of a date time value to
    * a standard date (1/1/1). This is used to allow times to
    * be compared and manipulated.
    *
    * @param date date time value
    * @return date time with date set to a standard value
    */
   public static Date getCanonicalTime(Date date)
   {
      if (date != null)
      {
         Calendar cal = popCalendar(date);
         cal.set(Calendar.DAY_OF_YEAR, 1);
         cal.set(Calendar.YEAR, 1);
         cal.set(Calendar.MILLISECOND, 0);
         date = cal.getTime();
         pushCalendar(cal);
      }
      return (date);
   }

   /**
    * Assuming two timestamps representing a time range on a single day,
    * convert the timestamps to a canonical date, and adjust the end
    * timestamp to handle the case where the time range ends at midnight.
    *
    * @param rangeStart start timestamp
    * @param rangeFinish finish timestamp
    * @return canonical end date
    */
   public static Date getCanonicalEndTime(Date rangeStart, Date rangeFinish)
   {
      Date startDay = DateHelper.getDayStartDate(rangeStart);
      Date finishDay = DateHelper.getDayStartDate(rangeFinish);

      Date result = DateHelper.getCanonicalTime(rangeFinish);

      //
      // Handle the case where the end of the range is at midnight -
      // this will show up as the start and end days not matching
      //
      if (startDay != null && finishDay != null && startDay.getTime() != finishDay.getTime())
      {
         result = DateHelper.addDays(result, 1);
      }

      return result;
   }

   /**
    * This method compares a target date with a date range. The method will
    * return 0 if the date is within the range, less than zero if the date
    * is before the range starts, and greater than zero if the date is after
    * the range ends.
    *
    * @param startDate range start date
    * @param endDate range end date
    * @param targetDate target date
    * @return comparison result
    */
   public static int compare(Date startDate, Date endDate, Date targetDate)
   {
      return (compare(startDate, endDate, targetDate.getTime()));
   }

   /**
    * This method compares a target date with a date range. The method will
    * return 0 if the date is within the range, less than zero if the date
    * is before the range starts, and greater than zero if the date is after
    * the range ends.
    *
    * @param startDate range start date
    * @param endDate range end date
    * @param targetDate target date in milliseconds
    * @return comparison result
    */
   public static int compare(Date startDate, Date endDate, long targetDate)
   {
      int result = 0;
      if (targetDate < startDate.getTime())
      {
         result = -1;
      }
      else
      {
         if (targetDate > endDate.getTime())
         {
            result = 1;
         }
      }
      return (result);
   }

   /**
    * Compare two dates, handling null values.
    * TODO: correct the comparison order to align with Date.compareTo
    *
    * @param d1 Date instance
    * @param d2 Date instance
    * @return int comparison result
    */
   public static int compare(Date d1, Date d2)
   {
      if (d1 == null || d2 == null)
      {
         return d1 == d2 ? 0 : (d1 == null ? 1 : -1);
      }
      return d1.compareTo(d2);
   }

   /**
    * Returns the earlier of two dates, handling null values. A non-null Date
    * is always considered to be earlier than a null Date.
    *
    * @param d1 Date instance
    * @param d2 Date instance
    * @return Date earliest date
    */
   public static Date min(Date d1, Date d2)
   {
      Date result;
      if (d1 == null)
      {
         result = d2;
      }
      else
      {
         if (d2 == null)
         {
            result = d1;
         }
         else
         {
            result = (d1.compareTo(d2) < 0) ? d1 : d2;
         }
      }
      return result;
   }

   /**
    * Returns the later of two dates, handling null values. A non-null Date
    * is always considered to be later than a null Date.
    *
    * @param d1 Date instance
    * @param d2 Date instance
    * @return Date latest date
    */
   public static Date max(Date d1, Date d2)
   {
      Date result;
      if (d1 == null)
      {
         result = d2;
      }
      else
         if (d2 == null)
         {
            result = d1;
         }
         else
         {
            result = (d1.compareTo(d2) > 0) ? d1 : d2;
         }
      return result;
   }

   /**
    * This utility method calculates the difference in working
    * time between two dates, given the context of a task.
    *
    * @param task parent task
    * @param date1 first date
    * @param date2 second date
    * @param format required format for the resulting duration
    * @return difference in working time between the two dates
    */
   public static Duration getVariance(Task task, Date date1, Date date2, TimeUnit format)
   {
      Duration variance = null;

      if (date1 != null && date2 != null)
      {
         ProjectCalendar calendar = task.getEffectiveCalendar();
         if (calendar != null)
         {
            variance = calendar.getWork(date1, date2, format);
         }
      }

      if (variance == null)
      {
         variance = Duration.getInstance(0, format);
      }

      return (variance);
   }

   /**
    * Creates a date from the equivalent long value. This conversion
    * takes account of the time zone.
    *
    * @param date date expressed as a long integer
    * @return new Date instance
    */
   public static Date getDateFromLong(long date)
   {
      TimeZone tz = TimeZone.getDefault();
      return (new Date(date - tz.getRawOffset()));
   }

   /**
    * Generates a long from a Date instance.
    * This conversion takes account of the time zone.
    *
    * @param date Date instance
    * @return date expressed as a long integer
    */
   public static long getLongFromDate(Date date)
   {
      TimeZone tz = TimeZone.getDefault();
      return date.getTime() + tz.getRawOffset();
   }

   /**
    * Creates a timestamp from the equivalent long value. This conversion
    * takes account of the time zone and any daylight savings time.
    *
    * @param timestamp timestamp expressed as a long integer
    * @return new Date instance
    */
   public static Date getTimestampFromLong(long timestamp)
   {
      TimeZone tz = TimeZone.getDefault();
      Date result = new Date(timestamp - tz.getRawOffset());

      if (tz.inDaylightTime(result))
      {
         int savings;

         if (HAS_DST_SAVINGS)
         {
            savings = tz.getDSTSavings();
         }
         else
         {
            savings = DEFAULT_DST_SAVINGS;
         }

         result = new Date(result.getTime() - savings);
      }
      return result;
   }

   /**
    * Creates a long value from a timestamp.  This conversion
    * takes account of the time zone and any daylight savings time.
    *
    * @param date timestamp as a Date instance
    * @return timestamp expressed as a long integer
    */
   public static long getLongFromTimestamp(Date date)
   {
      TimeZone tz = TimeZone.getDefault();
      long result = date.getTime();
      if (tz.inDaylightTime(date))
      {
         int savings;

         if (HAS_DST_SAVINGS)
         {
            savings = tz.getDSTSavings();
         }
         else
         {
            savings = DEFAULT_DST_SAVINGS;
         }

         result += savings;
      }
      return result;
   }

   /**
    * Create a Date instance representing a specific time.
    *
    * @param hour hour 0-23
    * @param minutes minutes 0-59
    * @return new Date instance
    */
   public static Date getTime(int hour, int minutes)
   {
      Calendar cal = popCalendar();
      cal.set(Calendar.HOUR_OF_DAY, hour);
      cal.set(Calendar.MINUTE, minutes);
      cal.set(Calendar.SECOND, 0);
      Date result = cal.getTime();
      pushCalendar(cal);
      return result;
   }

   /**
    * Given a date represented by a Calendar instance, set the time
    * component of the date based on the hours and minutes of the
    * time supplied by the Date instance.
    *
    * @param cal Calendar instance representing the date
    * @param time Date instance representing the time of day
    */
   public static void setTime(Calendar cal, Date time)
   {
      if (time != null)
      {
         Calendar startCalendar = popCalendar(time);
         cal.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
         cal.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
         cal.set(Calendar.SECOND, startCalendar.get(Calendar.SECOND));
         pushCalendar(startCalendar);
      }
   }

   /**
    * Given a date represented by a Date instance, set the time
    * component of the date based on the hours and minutes of the
    * time supplied by the Date instance.
    *
    * @param date Date instance representing the date
    * @param canonicalTime Date instance representing the time of day
    * @return new Date instance with the required time set
    */
   public static Date setTime(Date date, Date canonicalTime)
   {
      Date result;
      if (canonicalTime == null)
      {
         result = date;
      }
      else
      {
         //
         // The original naive implementation of this method generated
         // the "start of day" date (midnight) for the required day
         // then added the milliseconds from the canonical time
         // to move the time forward to the required point. Unfortunately
         // if the date we're trying to do this for is the entry or
         // exit from DST, the result is wrong, hence I've switched to
         // the approach below.
         //
         Calendar cal = popCalendar(canonicalTime);
         int dayOffset = cal.get(Calendar.DAY_OF_YEAR) - 1;
         int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
         int minute = cal.get(Calendar.MINUTE);
         int second = cal.get(Calendar.SECOND);
         int millisecond = cal.get(Calendar.MILLISECOND);

         cal.setTime(date);

         if (dayOffset != 0)
         {
            // The canonical time can be +1 day.
            // It's to do with the way we've historically
            // managed time ranges and midnight.
            cal.add(Calendar.DAY_OF_YEAR, dayOffset);
         }

         cal.set(Calendar.MILLISECOND, millisecond);
         cal.set(Calendar.SECOND, second);
         cal.set(Calendar.MINUTE, minute);
         cal.set(Calendar.HOUR_OF_DAY, hourOfDay);

         result = cal.getTime();
         pushCalendar(cal);
      }
      return result;
   }

   /**
    * This internal method is used to convert from an integer representing
    * minutes past midnight into a Date instance whose time component
    * represents the start time.
    *
    * @param time integer representing the start time in minutes past midnight
    * @return Date instance
    */
   public static Date getTimeFromMinutesPastMidnight(Integer time)
   {
      Date result = null;

      if (time != null)
      {
         int minutes = time.intValue();
         int hours = minutes / 60;
         minutes -= (hours * 60);

         Calendar cal = popCalendar();
         cal.set(Calendar.DAY_OF_YEAR, 1);
         cal.set(Calendar.YEAR, 1);
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MINUTE, minutes);
         cal.set(Calendar.HOUR_OF_DAY, hours);
         result = cal.getTime();
         pushCalendar(cal);
      }

      return result;
   }

   /**
    * Add a number of days to the supplied date.
    *
    * @param date start date
    * @param days number of days to add
    * @return  new date
    */
   public static Date addDays(Date date, int days)
   {
      Calendar cal = popCalendar(date);
      cal.add(Calendar.DAY_OF_YEAR, days);
      Date result = cal.getTime();
      pushCalendar(cal);
      return result;
   }

   public static boolean isSameDay(Date d1, Date d2)
   {
      if (d1 == null || d2 == null)
      {
         return false;
      }

      Calendar cal1 = popCalendar(d1);
      Calendar cal2 = popCalendar(d2);
      boolean result = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
      pushCalendar(cal1);
      pushCalendar(cal2);

      return result;
   }

   /**
    * Acquire a calendar instance.
    *
    * @return Calendar instance
    */
   public static Calendar popCalendar()
   {
      Calendar result;
      Deque<Calendar> calendars = CALENDARS.get();
      if (calendars.isEmpty())
      {
         result = Calendar.getInstance();
      }
      else
      {
         result = calendars.pop();
      }
      return result;
   }

   /**
    * Acquire a Calendar instance and set the initial date.
    *
    * @param date initial date
    * @return Calendar instance
    */
   public static Calendar popCalendar(Date date)
   {
      Calendar calendar = popCalendar();
      calendar.setTime(date);
      return calendar;
   }

   /**
    * Acquire a Calendar instance and set the initial date.
    *
    * @param timeInMillis initial date
    * @return Calendar instance
    */
   public static Calendar popCalendar(long timeInMillis)
   {
      Calendar calendar = popCalendar();
      calendar.setTimeInMillis(timeInMillis);
      return calendar;
   }

   /**
    * Return a Calendar instance.
    *
    * @param cal Calendar instance to return
    */
   public static void pushCalendar(Calendar cal)
   {
      CALENDARS.get().push(cal);
   }

   /**
    * Date representing NA at the start of a date range: January 01 00:00:00 1984.
    */
   public static final Date START_DATE_NA = DateHelper.getTimestampFromLong(441763200000L);

   /**
    * Date representing NA at the end of a date range: Friday December 31 23:59:00 2049.
    * That's actually the value used by older versions of MS Project. The most recent version
    * of MS Project uses Friday December 31 23:59:06 2049 (note the six extra seconds).
    * The problem with using this value to represent NA at the end of a date range is it
    * isn't interpreted correctly by older versions of MS Project. The compromise here is that
    * we'll use the value recognised by older versions of MS Project, which will work as expected
    * and display NA as the end date. For the current version of MS Project this will display a
    * the end date as 2049, rather than NA, but this should still be interpreted correctly.
    * TODO: consider making this behaviour configurable.
    */
   public static final Date END_DATE_NA = DateHelper.getTimestampFromLong(2524607940000L);

   /**
    * Number of milliseconds per minute.
    */
   public static final long MS_PER_MINUTE = 60 * 1000;

   /**
    * Number of milliseconds per minute.
    */
   public static final long MS_PER_HOUR = 60 * MS_PER_MINUTE;

   /**
    * Number of milliseconds per day.
    */
   public static final long MS_PER_DAY = 24 * MS_PER_HOUR;

   /**
    * Default value to use for DST savings if we are using a version
    * of Java < 1.4.
    */
   private static final int DEFAULT_DST_SAVINGS = 3600000;

   /**
    * Flag used to indicate the existence of the getDSTSavings
    * method that was introduced in Java 1.4.
    */
   private static boolean HAS_DST_SAVINGS;

   private static final ThreadLocal<Deque<Calendar>> CALENDARS = ThreadLocal.withInitial(ArrayDeque::new);

   static
   {
      Class<TimeZone> tz = TimeZone.class;

      try
      {
         tz.getMethod("getDSTSavings", (Class<?>[]) null);
         HAS_DST_SAVINGS = true;
      }

      catch (NoSuchMethodException ex)
      {
         HAS_DST_SAVINGS = false;
      }
   }
}
