/*
 * file:       TimescaleUnitsHelper.java
 * author:     Jon Iles
 * date:       2026-02-27
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

import org.mpxj.TimescaleUnits;

/**
 * Provides a method to convert from Microsoft Project's representation
 * of timescale units.
 */
class TimescaleUnitsHelper
{
   /**
    * Retrieve a timescale unit by its integer value.
    *
    * @param type timescale units value
    * @return TimescaleUnits instance
    */
   public static TimescaleUnits getInstance(int type)
   {
      type +=1;
      if (type < 0 || type > TYPE_VALUES.length)
      {
         return TimescaleUnits.NONE;
      }

      return TYPE_VALUES[type];
   }

   private static final TimescaleUnits[] TYPE_VALUES = new TimescaleUnits[]
   {
      TimescaleUnits.NONE,
      TimescaleUnits.MINUTES,
      TimescaleUnits.HOURS,
      TimescaleUnits.DAYS,
      TimescaleUnits.WEEKS,
      TimescaleUnits.THIRDS_OF_MONTHS,
      TimescaleUnits.MONTHS,
      TimescaleUnits.QUARTERS,
      TimescaleUnits.HALF_YEARS,
      TimescaleUnits.YEARS
   };
}
