/*
 * file:       Day.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
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

package net.sf.mpxj;

import net.sf.mpxj.common.EnumHelper;

/**
 * Instances of this class represent enumerated day values.
 */
public enum Day implements MpxjEnum
{
   SUNDAY(1),
   MONDAY(2),
   TUESDAY(3),
   WEDNESDAY(4),
   THURSDAY(5),
   FRIDAY(6),
   SATURDAY(7);

   /**
    * Protected constructor.
    *
    * @param value day value
    */
   private Day(int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the day.
    *
    * @return task type value
    */
   @Override public int getValue()
   {
      return (m_value);
   }

   /**
    * This method provides a simple mechanism to retrieve
    * the next day in correct sequence, including the transition
    * from Sunday to Monday.
    *
    * @return Day instance
    */
   public Day getNextDay()
   {
      int value = m_value + 1;
      if (value > 7)
      {
         value = 1;
      }
      return (getInstance(value));
   }

   /**
    * Retrieve a Day instance representing the supplied value.
    *
    * @param type type value
    * @return Day instance
    */
   public static Day getInstance(int type)
   {
      Day result;

      if (type < 0 || type >= TYPE_VALUES.length)
      {
         result = null;
      }
      else
      {
         result = TYPE_VALUES[type];
      }
      return result;
   }

   /**
    * Array mapping int types to enums.
    */
   private static final Day[] TYPE_VALUES = EnumHelper.createTypeArray(Day.class, 1);

   private int m_value;
}
