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

import java.util.Calendar;
import java.util.Date;
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
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         date = cal.getTime();
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
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         cal.set(Calendar.MILLISECOND, 999);
         cal.set(Calendar.SECOND, 59);
         cal.set(Calendar.MINUTE, 59);
         cal.set(Calendar.HOUR_OF_DAY, 23);
         date = cal.getTime();
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
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         cal.set(Calendar.DAY_OF_YEAR, 1);
         cal.set(Calendar.YEAR, 1);
         cal.set(Calendar.MILLISECOND, 0);
         date = cal.getTime();
      }
      return (date);
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
      int result;
      if (d1 == null || d2 == null)
      {
         result = (d1 == d2 ? 0 : (d1 == null ? 1 : -1));
      }
      else
      {
         long diff = d1.getTime() - d2.getTime();
         result = ((diff == 0) ? 0 : ((diff > 0) ? 1 : -1));
      }
      return (result);
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
         ProjectCalendar calendar = task.getCalendar();
         if (calendar == null)
         {
            calendar = task.getParentFile().getDefaultCalendar();
         }

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

      if (tz.inDaylightTime(result) == true)
      {
         int savings;

         if (HAS_DST_SAVINGS == true)
         {
            savings = tz.getDSTSavings();
         }
         else
         {
            savings = DEFAULT_DST_SAVINGS;
         }

         result = new Date(result.getTime() - savings);
      }
      return (result);
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
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, hour);
      cal.set(Calendar.MINUTE, minutes);
      cal.set(Calendar.SECOND, 0);
      return (cal.getTime());
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
         Calendar startCalendar = Calendar.getInstance();
         startCalendar.setTime(time);
         cal.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
         cal.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
         cal.set(Calendar.SECOND, startCalendar.get(Calendar.SECOND));
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
         result = DateHelper.getDayStartDate(date);
         long offset = canonicalTime.getTime() - CANONICAL_EPOCH.getTime();
         result = new Date(result.getTime() + offset);
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

         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MINUTE, minutes);
         cal.set(Calendar.HOUR_OF_DAY, hours);

         result = cal.getTime();
      }

      return result;
   }

   /**
    * First date supported by Microsoft Project: January 01 00:00:00 1984.
    */
   public static final Date FIRST_DATE = DateHelper.getTimestampFromLong(441763200000L);

   /**
    * Last date supported by Microsoft Project: Friday December 31 23:59:00 2049.
    */
   public static final Date LAST_DATE = DateHelper.getTimestampFromLong(2524607946000L);

   /**
    * Default value to use for DST savings if we are using a version
    * of Java < 1.4.
    */
   private static final int DEFAULT_DST_SAVINGS = 3600000;

   private static Date CANONICAL_EPOCH = getCanonicalTime(getDayStartDate(new Date()));

   /**
    * Flag used to indicate the existence of the getDSTSavings
    * method that was introduced in Java 1.4.
    */
   private static boolean HAS_DST_SAVINGS;

   static
   {
      Class<TimeZone> tz = TimeZone.class;

      try
      {
         tz.getMethod("getDSTSavings", (Class[]) null);
         HAS_DST_SAVINGS = true;
      }

      catch (NoSuchMethodException ex)
      {
         HAS_DST_SAVINGS = false;
      }
   }
}
