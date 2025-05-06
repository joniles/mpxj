/*
 * file:       CombinedCalendarTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       08/11/2022
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

package org.mpxj.junit;

import java.time.LocalDate;
import java.time.LocalTime;

import java.time.DayOfWeek;
import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.common.CombinedCalendar;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CombinedCalendarTest
{
   /**
    * Test the CombinedCalendar class.
    */
   @Test public void testCombinedCalendar()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar calendar1 = file.addDefaultBaseCalendar();
      ProjectCalendar calendar2 = file.addDefaultBaseCalendar();
      CombinedCalendar combined = new CombinedCalendar(calendar1, calendar2);

      // Ensure that for an unmodified working day the calendars have an 8-hour overlap
      Duration work = combined.getWork(DayOfWeek.MONDAY, TimeUnit.HOURS);
      assertEquals(8.0, work.getDuration(), 0.0);
      work = combined.getWork(LocalDate.of(2022, 10, 31), TimeUnit.HOURS);
      assertEquals(8.0, work.getDuration(), 0.0);

      // Ensure that for an unmodified non-working day the calendars have no overlap
      work = combined.getWork(DayOfWeek.SATURDAY, TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);
      work = combined.getWork(LocalDate.of(2022, 10, 29), TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);

      // Modify calendar1 so Tuesdays are working from 09:00 to 13:00
      ProjectCalendarHours hours = calendar1.getCalendarHours(DayOfWeek.TUESDAY);
      hours.clear();
      hours.add(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(13, 0)));

      // Ensure that Tuesday only has 3 working hours
      work = combined.getWork(DayOfWeek.TUESDAY, TimeUnit.HOURS);
      assertEquals(3.0, work.getDuration(), 0.0);
      work = combined.getWork(LocalDate.of(2022, 11, 1), TimeUnit.HOURS);
      assertEquals(3.0, work.getDuration(), 0.0);

      // Modify calendar1 so Wednesdays are working from 00:00 to 08:00
      hours = calendar1.getCalendarHours(DayOfWeek.WEDNESDAY);
      hours.clear();
      hours.add(new LocalTimeRange(LocalTime.of(0, 0), LocalTime.of(8, 0)));

      // Ensure Wednesday shows no working hours as there is no overlap
      work = combined.getWork(DayOfWeek.WEDNESDAY, TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);
      work = combined.getWork(LocalDate.of(2022, 11, 2), TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);

      // Add an exception for a specific Thursday
      LocalDate exceptionDate = LocalDate.of(2022, 11, 3);
      ProjectCalendarException exception = calendar1.addCalendarException(exceptionDate);
      exception.add(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(13, 0)));

      work = combined.getWork(exceptionDate, TimeUnit.HOURS);
      assertEquals(3.0, work.getDuration(), 0.0);
   }
}
