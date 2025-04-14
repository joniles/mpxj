/*
 * file:       LocalDateTimeHelper.java
 * author:     Jon Iles
 * date:       2023-06-26
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

package org.mpxj.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.TimeUnit;

/**
 * Common code for working with LocalDateTime instances.
 */
public final class LocalDateTimeHelper
{
   /**
    * Returns a new Date instance whose value
    * represents the start of the day (i.e. the time of day is 00:00:00.000)
    *
    * @param date date to convert
    * @return day start date
    */
   public static LocalDateTime getDayStartDate(LocalDateTime date)
   {
      if (date == null)
      {
         return null;
      }

      return LocalDateTime.of(date.toLocalDate(), LocalTime.of(0, 0));
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
   public static int compare(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime targetDate)
   {
      int result = 0;
      if (targetDate.isBefore(startDate))
      {
         result = -1;
      }
      else
      {
         if (targetDate.isAfter(endDate))
         {
            result = 1;
         }
      }
      return result;
   }

   /**
    * Compare two dates, handling null values.
    * TODO: correct the comparison order to align with Date.compareTo
    *
    * @param d1 Date instance
    * @param d2 Date instance
    * @return int comparison result
    */
   public static int compare(LocalDateTime d1, LocalDateTime d2)
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
   public static LocalDateTime min(LocalDateTime d1, LocalDateTime d2)
   {
      LocalDateTime result;
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
            result = (d1.isBefore(d2)) ? d1 : d2;
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
   public static LocalDateTime max(LocalDateTime d1, LocalDateTime d2)
   {
      LocalDateTime result;
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
            result = (d1.isAfter(d2)) ? d1 : d2;
         }
      return result;
   }

   /**
    * Use the parseBest method of the formatter to retrieve a LocalDateTime instance
    * handling the case where the formatter returns a LocalDate instance.
    *
    * @param format DateTimeFormatter instance
    * @param value value to parse
    * @return LocalDateTime instance
    */
   public static LocalDateTime parseBest(DateTimeFormatter format, String value)
   {
      TemporalAccessor parsed = format.parseBest(value, LocalDateTime::from, LocalDate::from);
      if (parsed instanceof LocalDate)
      {
         return ((LocalDate) parsed).atStartOfDay();
      }
      return (LocalDateTime) parsed;
   }

   /**
    * This utility method calculates the difference in working
    * time between two dates, given the context of a calendar.
    *
    * @param calendar calendar
    * @param date1 first date
    * @param date2 second date
    * @param format required format for the resulting duration
    * @return difference in working time between the two dates
    */
   public static Duration getVariance(ProjectCalendar calendar, LocalDateTime date1, LocalDateTime date2, TimeUnit format)
   {
      if (date1 != null && date2 != null && calendar != null)
      {
         return calendar.getWork(date1, date2, format);
      }
      return Duration.getInstance(0, format);
   }

   /**
    * Date representing NA at the start of a date range: January 01 00:00:00 1984.
    */
   public static final LocalDateTime START_DATE_NA = LocalDateTime.of(1984, 1, 1, 0, 0);

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
   public static final LocalDateTime END_DATE_NA = LocalDateTime.of(2049, 12, 31, 23, 59);
}
