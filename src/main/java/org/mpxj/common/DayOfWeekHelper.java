/*
 * file:       DayOfWeekHelper.java
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

import java.time.DayOfWeek;

/**
 * Common code for working with DayOfWeek instances.
 */
public final class DayOfWeekHelper
{
   /**
    * Retrieve a Day instance representing the supplied value.
    *
    * @param type type value
    * @return Day instance
    */
   public static DayOfWeek getInstance(int type)
   {
      DayOfWeek result;
      --type;

      if (type < 0 || type >= ORDERED_DAYS.length)
      {
         result = null;
      }
      else
      {
         result = ORDERED_DAYS[type];
      }
      return result;
   }

   /**
    * Retrieve the ordinal value for a DayOfWeek instance.
    *
    * @param day DayOfWeek instance
    * @return ordinal value
    */
   public static int getValue(DayOfWeek day)
   {
      switch (day)
      {
         case SUNDAY:
            return 1;
         case MONDAY:
            return 2;
         case TUESDAY:
            return 3;
         case WEDNESDAY:
            return 4;
         case THURSDAY:
            return 5;
         case FRIDAY:
            return 6;
         case SATURDAY:
            return 7;
      }

      return 0;
   }

   /**
    * Array mapping int types to enums.
    */
   public static final DayOfWeek[] ORDERED_DAYS =
   {
      DayOfWeek.SUNDAY,
      DayOfWeek.MONDAY,
      DayOfWeek.TUESDAY,
      DayOfWeek.WEDNESDAY,
      DayOfWeek.THURSDAY,
      DayOfWeek.FRIDAY,
      DayOfWeek.SATURDAY
   };
}
