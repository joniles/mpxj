/*
 * file:       GanttBarDateFormatHelper.java
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
 * Helper class for the GanttBarDateFormat enumeration.
 */
final class GanttBarDateFormatHelper
{
   /**
    * Retrieve a GanttBarDateFormat based on an int value.
    *
    * @param value int representation of a GanttBarDateFormat value
    * @return GanttBarDateFormat instance or null
    */
   public static GanttBarDateFormat getInstance(int value)
   {
      if (value < 0 || value >= TYPE_VALUES.length)
      {
         return GanttBarDateFormat.DEFAULT;
      }

      return TYPE_VALUES[value];
   }

   private static final GanttBarDateFormat[] TYPE_VALUES = {
      GanttBarDateFormat.DEFAULT,
      GanttBarDateFormat.DDMMYY_MMSS,
      GanttBarDateFormat.DDMMYY,
      GanttBarDateFormat.DD_MMMM_YYYY_HHMM,
      GanttBarDateFormat.DD_MMMM_YYYY,
      GanttBarDateFormat.DD_MMM_HHMM,
      GanttBarDateFormat.DD_MMM_YY,
      GanttBarDateFormat.DD_MMMM,
      GanttBarDateFormat.DD_MMM,
      GanttBarDateFormat.DDD_DDMMYY_HHMM,
      GanttBarDateFormat.DDD_DDMMYY,
      GanttBarDateFormat.DDD_DD_MMM_YY,
      GanttBarDateFormat.DDD_HHMM,
      GanttBarDateFormat.DDMM,
      GanttBarDateFormat.DD,
      GanttBarDateFormat.HHMM,
      GanttBarDateFormat.DDD_DD_MMM,
      GanttBarDateFormat.DDD_DDMM,
      GanttBarDateFormat.DDD_DD,
      GanttBarDateFormat.MWW,
      GanttBarDateFormat.MWWYY_HHMM,
      GanttBarDateFormat.DDMMYYYY
   };
}
