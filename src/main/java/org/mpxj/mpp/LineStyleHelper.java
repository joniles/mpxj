/*
 * file:       LineStyleHelper.java
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
 * Helper class for the LineStyle enumeration.
 */
final class LineStyleHelper
{
   /**
    * Retrieve a LineStyle based on an int value.
    *
    * @param value int representation of a LineStyle value
    * @return LineStyle instance or null
    */
   public static LineStyle getInstance(int value)
   {
      if (value < 0 || value >= TYPE_VALUES.length)
      {
         return null;
      }

      return TYPE_VALUES[value];
   }

   private static final LineStyle[] TYPE_VALUES = {
      LineStyle.NONE,
      LineStyle.SOLID,
      LineStyle.DOTTED1,
      LineStyle.DOTTED2,
      LineStyle.DASHED
   };
}
