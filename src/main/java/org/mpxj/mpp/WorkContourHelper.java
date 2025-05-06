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

package org.mpxj.mpp;

import java.util.HashMap;
import java.util.Map;

import org.mpxj.ProjectFile;
import org.mpxj.WorkContour;
import org.mpxj.WorkContourContainer;

/**
 * Helper methods for Microsoft Project representation of work contours.
 */
public class WorkContourHelper
{
   /**
    * Retrieve a WorkContour instance based on its Microsoft Project ID value.
    *
    * @param file parent file
    * @param type MS Project work contour ID
    * @return WorkContour instance
    */
   public static WorkContour getInstance(ProjectFile file, int type)
   {
      WorkContour result;
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         result = WorkContour.FLAT;
      }
      else
      {
         result = TYPE_VALUES[type];
      }

      WorkContourContainer contours = file.getWorkContours();
      if (contours.getByUniqueID(result.getUniqueID()) == null)
      {
         contours.add(result);
      }

      return result;
   }

   /**
    * Given a WorkContour instance, retrieve its Microsoft Project ID value.
    *
    * @param contour WorkContour instance
    * @return ID value
    */
   public static Integer getID(WorkContour contour)
   {
      return WORK_CONTOUR_MAP.get(contour);
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

   private static final Map<WorkContour, Integer> WORK_CONTOUR_MAP = new HashMap<>();
   static
   {
      for (int index = 0; index < TYPE_VALUES.length; index++)
      {
         WORK_CONTOUR_MAP.put(TYPE_VALUES[index], Integer.valueOf(index));
      }
   }
}
