/*
 * file:       TimephasedHelper.java
 * author:     Jon Iles
 * date:       2026-10-31
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

package org.mpxj.common;

import org.mpxj.ProjectCalendar;
import org.mpxj.ResourceAssignment;

/**
 * Common methods for working with timephased data.
 */
public final class TimephasedHelper
{
   /**
    * Determine the calendar to use when working with actual timephased work.
    *
    * @param assignment target resource assignment
    * @return ProjectCalendar instance
    */
   public static ProjectCalendar getEffectiveCalendarForActualTimephasedWork(ResourceAssignment assignment)
   {
      ProjectCalendar workingCalendar;
      if (assignment.getTask().getCalendar() == null)
      {
         if (assignment.getResource() == null)
         {
            workingCalendar = assignment.getTask().getEffectiveCalendar();
         }
         else
         {
            workingCalendar = assignment.getResource().getCalendar();
         }
      }
      else
      {
         workingCalendar = assignment.getTask().getCalendar();
      }

      if (workingCalendar == null)
      {
         workingCalendar = assignment.getTask().getEffectiveCalendar();
      }

      return workingCalendar;
   }
}
