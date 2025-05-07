/*
 * file:       LocalDateHelper.java
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

/**
 * Common code for working with LocalDate instances.
 */
public final class LocalDateHelper
{
   /**
    * Convert a LocalDate instance to a LocalDateTime instance,
    * handling null values.
    *
    * @param date LocalDate instance
    * @return LocalDateTime instance or null
    */
   public static LocalDateTime getLocalDateTime(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }

      return date.atStartOfDay();
   }

   /**
    * Convert a LocalDateTime instance to a LocalDate instance, handling null values.
    *
    * @param date LocalDateTimeInstance
    * @return LocalDate instance or null
    */
   public static LocalDate getLocalDate(LocalDateTime date)
   {
      if (date == null)
      {
         return null;
      }

      return date.toLocalDate();
   }

   /**
    * Compare two LocalDate instances, handling null values.
    *
    * @param d1 LocalDate instance
    * @param d2 LocalDate instance
    * @return negative if less, positive if greater
    */
   public static int compare(LocalDate d1, LocalDate d2)
   {
      if (d1 == null || d2 == null)
      {
         return d1 == d2 ? 0 : (d1 == null ? 1 : -1);
      }
      return d1.compareTo(d2);
   }

   /**
    * Determine if the target date is within the range specified by the start and end date.
    *
    * @param startDate range start date
    * @param endDate range end date
    * @param targetDate target date
    * @return negative if the target date is before the range, positive if the target date is after the range
    */
   public static int compare(LocalDate startDate, LocalDate endDate, LocalDate targetDate)
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
      return (result);
   }

   /**
    * Returns a new Date instance whose value
    * represents the end of the day (i.e. the time of days is 11:59:59.999)
    *
    * @param date date to convert
    * @return day start date
    */
   public static LocalDateTime getDayEndDate(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }
      return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
   }

   public static final LocalDate TWO_DIGIT_YEAR_BASE_DATE = LocalDate.now().minusYears(80);
}
