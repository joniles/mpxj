/*
 * file:       LocalTimeHelper.java
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

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Common code for working with LocalTime instances.
 */
public final class LocalTimeHelper
{
   /**
    * Convert a LocalDateTime instance to a LocalTime instance, handling null values.
    *
    * @param date LocalDateTime instance
    * @return LocalTime instance or null
    */
   public static LocalTime getLocalTime(LocalDateTime date)
   {
      if (date == null)
      {
         return null;
      }

      return date.toLocalTime();
   }

   /**
    * Compare two LocalTime instances, handling null values.
    *
    * @param d1 LocalTime instance
    * @param d2 LocalTime instance
    * @return comparison result
    */
   public static int compare(LocalTime d1, LocalTime d2)
   {
      int result;
      if (d1 == null || d2 == null)
      {
         result = (d1 == d2 ? 0 : (d1 == null ? 1 : -1));
      }
      else
      {
         result = d1.compareTo(d2);
      }
      return result;
   }

   /**
    * Return a LocalDateTime instance which composed of the LocalDate
    * instance from the date argument and the LocalTime instance of
    * the time argument, handling null values.
    *
    * @param date LocalDateTime instance
    * @param time LocalTime instance
    * @return LocalDateTime instance
    */
   public static LocalDateTime setTime(LocalDateTime date, LocalTime time)
   {
      if (time == null)
      {
         return date;
      }

      return LocalDateTime.of(date.toLocalDate(), time);
   }

   /**
    * Return a LocalDateTime instance which composed of the LocalDate
    * instance from the date argument and the LocalTime instance of
    * the time argument, handling null values. This method deals with midnight
    * representing the end of the day (i.e the start of the following day).
    *
    * @param date LocalDateTime instance
    * @param time LocalTime instance
    * @return LocalDateTime instance
    */
   public static LocalDateTime setEndTime(LocalDateTime date, LocalTime time)
   {
      if (time == null)
      {
         return date;
      }

      date = LocalDateTime.of(date.toLocalDate(), time);
      if (time == LocalTime.MIDNIGHT)
      {
         date = date.plusDays(1);
      }

      return date;
   }

   /**
    * Calculate the number of milliseconds between two LocalTime instances,
    * handling null values.
    *
    * @param rangeStart LocalTime instance
    * @param rangeEnd LocalTime instance
    * @return number of milliseconds
    */
   public static long getMillisecondsInRange(LocalTime rangeStart, LocalTime rangeEnd)
   {
      if (rangeStart == null || rangeEnd == null)
      {
         return 0;
      }

      return rangeEnd == LocalTime.MIDNIGHT ? MS_PER_DAY - (rangeStart.toSecondOfDay() * 1000L) : (rangeEnd.toSecondOfDay() - rangeStart.toSecondOfDay()) * 1000L;
   }

   /**
    * Number of milliseconds per day.
    */
   private static final long MS_PER_DAY = 24 * 60 * 60 * 1000;
}