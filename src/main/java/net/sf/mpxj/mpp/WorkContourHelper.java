/*
 * file:       WorkContourHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       2023-01-25
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
import net.sf.mpxj.WorkContour;

/**
 * Helper methods for Microsoft Project representation of work contours.
 */
public class WorkContourHelper
{
   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static WorkContour getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         return WorkContour.FLAT;
      }
      return TYPE_VALUES[type];
   }

   /**
    * Array mapping int types to WorkContour instances.
    */
   private static final WorkContour[] TYPE_VALUES =
   {
      WorkContour.FLAT,
      WorkContour.BACK_LOADED,
      WorkContour.FRONT_LOADED,
      WorkContour.DOUBLE_PEAK,
      WorkContour.EARLY_PEAK,
      WorkContour.LATE_PEAK,
      WorkContour.BELL,
      WorkContour.TURTLE,
      WorkContour.CONTOURED
   };
}
