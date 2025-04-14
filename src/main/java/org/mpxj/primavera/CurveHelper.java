/*
 * file:       CurveHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

package org.mpxj.primavera;

import org.mpxj.ProjectFile;
import org.mpxj.WorkContour;

/**
 * Common methods for working with resource curves.
 */
final class CurveHelper
{
   public static Integer getCurveID(WorkContour contour)
   {
      if (contour == null || contour.isContourFlat())
      {
         return null;
      }

      return contour.getUniqueID();
   }

   public static WorkContour getWorkContour(ProjectFile file, Integer id)
   {
      if (id == null)
      {
         return null;
      }

      // Special case: the "manual" curve type won't be present in an exported file, but the ID is a fixed value
      if (id.equals(WorkContour.CONTOURED.getUniqueID()))
      {
         return WorkContour.CONTOURED;
      }

      return file.getWorkContours().getByUniqueID(id);
   }
}
