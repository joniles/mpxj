/*
 * file:       CalendarRecord.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       01/07/2019
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

package org.mpxj.sdef;

import java.time.DayOfWeek;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarDays;
import org.mpxj.ProjectCalendarHours;

/**
 * SDEF Calendar Record.
 */
class CalendarRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }

   @Override public void process(Context context)
   {
      ProjectCalendar calendar = context.addCalendar(getString(0));
      calendar.setName(getString(2));

      String flags = getString(1);
      for (DayOfWeek day : DayOfWeekHelper.ORDERED_DAYS)
      {
         boolean workingDay = flags.charAt(DayOfWeekHelper.getValue(day) - 1) == 'Y';
         calendar.setWorkingDay(day, workingDay);
         ProjectCalendarHours hours = calendar.addCalendarHours(day);
         if (workingDay)
         {
            hours.add(ProjectCalendarDays.DEFAULT_WORKING_MORNING);
            hours.add(ProjectCalendarDays.DEFAULT_WORKING_AFTERNOON);
         }
      }
      context.getEventManager().fireCalendarReadEvent(calendar);
   }

   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Calendar Code", 1),
      new StringField("Workdays", 7),
      new StringField("Calendar Description", 30)
   };
}
