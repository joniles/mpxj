/*
 * file:       ProjectCalendarHelper.java
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

package net.sf.mpxj.primavera;

import net.sf.mpxj.CalendarType;
import net.sf.mpxj.ProjectCalendar;

/**
 * Common methods to support working with P6 calendars.
 */
final class ProjectCalendarHelper
{
   /**
    * Tries to ensure that the calendar structure we write matches P6's expectations.
    *
    * @param calendar calendar to normalize
    * @return normalized calendar
    */
   public static ProjectCalendar normalizeCalendar(ProjectCalendar calendar)
   {
      ProjectCalendar result = calendar;
      if (calendar.getType() == CalendarType.GLOBAL && calendar.isDerived())
      {
         // Global calendars in P6 are not derived from other calendars.
         // If this calendar is marked as a global calendar, and it is
         // derived then we'll flatten it.
         result = net.sf.mpxj.common.ProjectCalendarHelper.createTemporaryFlattenedCalendar(calendar);
      }
      return result;
   }
}
