/*
 * file:       Day.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       26/03/2005
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

/**
 * Instances of this class represent enumerated day values.
 */
public final class Day
{
   /**
    * Private constructor.
    *
    * @param value day value
    */
   private Day (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the day.
    *
    * @return task type value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve a Day instance representing the supplied value.
    *
    * @param value task type value
    * @return Day instance
    */
   public static Day getInstance (int value)
   {
      Day result = null;

      if (value >= 0 && value < DAY_ARRAY.length)
      {
         result = DAY_ARRAY[value];
      }
      
      return (result);
   }

   /**
    * Retrieves the string representation of this instance.
    * 
    * @return string representation
    */
   public String toString ()
   {
      return (Integer.toString(m_value));
   }
   
   private int m_value;

   public static final Day SUNDAY = new Day(1);
   public static final Day MONDAY = new Day(2);
   public static final Day TUESDAY = new Day(3);
   public static final Day WEDNESDAY = new Day(4);
   public static final Day THURSDAY = new Day(5);
   public static final Day FRIDAY = new Day(6);
   public static final Day SATURDAY = new Day(7);
   
   private static final Day[] DAY_ARRAY =
   {
      null,
      SUNDAY,
      MONDAY,
      TUESDAY,
      WEDNESDAY,
      THURSDAY,
      FRIDAY,
      SATURDAY
   };
}
