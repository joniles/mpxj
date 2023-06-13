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

import java.time.DayOfWeek;

import net.sf.mpxj.common.EnumHelper;

/**
 * Instances of this class represent enumerated day values.
 */
public enum Day
{
   SUNDAY,
   MONDAY,
   TUESDAY,
   WEDNESDAY,
   THURSDAY,
   FRIDAY,
   SATURDAY;

   /**
    * This method provides a simple mechanism to retrieve
    * the next day in correct sequence, including the transition
    * from Sunday to Monday.
    *
    * @return Day instance
    */

   public Day plus(int i)
   {
      int value = DayOfWeekHelper.getValue(this) + i;
      if (value > 7)
      {
         value = 1;
      }
      return (DayOfWeekHelper.getInstance(value));
   }
}
