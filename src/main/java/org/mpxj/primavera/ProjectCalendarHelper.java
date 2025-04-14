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

package org.mpxj.primavera;

import java.time.LocalTime;

import org.mpxj.CalendarType;
import java.time.DayOfWeek;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.LocalTimeRange;

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
         result = org.mpxj.common.ProjectCalendarHelper.createTemporaryFlattenedCalendar(calendar);
      }
      return result;
   }

   /**
    * Ensure that a calendar has some working time. If it does not,
    * update the calendar using default values.
    *
    * @param calendar calendar to check
    */
   public static void ensureWorkingTime(ProjectCalendar calendar)
   {
      // Check for working time
      boolean hasWorkingTime = false;
      for (DayOfWeek day : DayOfWeek.values())
      {
         ProjectCalendarHours hours = calendar.getCalendarHours(day);
         hasWorkingTime = hours != null && !hours.isEmpty();
         if (hasWorkingTime)
         {
            break;
         }
      }

      if (!hasWorkingTime)
      {
         // if there is not DaysOfWeek data, Primavera seems to default to Mon-Fri, 8:00-16:00
         LocalTimeRange defaultHourRange = getDefaultCalendarHours();
         for (DayOfWeek day : DayOfWeek.values())
         {
            ProjectCalendarHours hours = calendar.addCalendarHours(day);
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
            {
               calendar.setWorkingDay(day, true);
               hours.add(defaultHourRange);
            }
            else
            {
               calendar.setWorkingDay(day, false);
            }
         }
      }
   }

   /**
    * Create a new DareRange instance containing the default calendar hours used by P6.
    *
    * @return default calendar hours
    */
   public static LocalTimeRange getDefaultCalendarHours()
   {
      return new LocalTimeRange(LocalTime.of(8, 0), LocalTime.of(16, 0));
   }
}
