/*
 * file:       ProgressLineDay.java
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.Day;

/**
 * Instances of this class represent enumerated day values used as to
 * define when progress lines are drawn.
 */
public final class ProgressLineDay extends Day
{
   /**
    * Private constructor.
    *
    * @param value day value
    */
   protected ProgressLineDay(int value)
   {
      super(value);
   }

   /**
    * Retrieve a Day instance representing the supplied value.
    *
    * @param value task type value
    * @return Day instance
    */
   public static Day getInstance(int value)
   {
      Day result = null;

      if (value < 8)
      {
         result = Day.getInstance(value);
      }
      else
      {
         value -= 8;

         if (value >= 0 && value < DAY_ARRAY.length)
         {
            result = DAY_ARRAY[value];
         }
      }

      return (result);
   }

   public static final Day DAY = new ProgressLineDay(8);
   public static final Day WORKINGDAY = new ProgressLineDay(9);
   public static final Day NONWORKINGDAY = new ProgressLineDay(10);

   private static final Day[] DAY_ARRAY =
   {
      DAY,
      WORKINGDAY,
      NONWORKINGDAY
   };
}
