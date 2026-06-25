/*
 * file:       NonWorkingTimeStyleHelper.java
 * author:     Jon Iles
 * date:       2026-06-07
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

package org.mpxj.mpp;

/**
 * Helper class for the NonWorkingTimeStyle enumeration.
 */
final class NonWorkingTimeStyleHelper
{
   /**
    * Retrieve a NonWorkingTimeStyle based on an int value.
    *
    * @param value int representation of a NonWorkingTimeStyle value
    * @return NonWorkingTimeStyle instance or null
    */
   public static NonWorkingTimeStyle getInstance(int value)
   {
      if (value < 0 || value >= TYPE_VALUES.length)
      {
         return NonWorkingTimeStyle.IN_FRONT;
      }

      return TYPE_VALUES[value];
   }

   private static final NonWorkingTimeStyle[] TYPE_VALUES = {
      NonWorkingTimeStyle.BEHIND,
      NonWorkingTimeStyle.IN_FRONT,
      NonWorkingTimeStyle.DO_NOT_DRAW
   };
}
