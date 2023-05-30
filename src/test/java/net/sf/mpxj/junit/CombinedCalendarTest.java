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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TimeRange;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.CombinedCalendar;
import net.sf.mpxj.common.DateHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CombinedCalendarTest
{
   /**
    * Test the CombinedCalendar class.
    */
   @Test public void testCombinedCalendar() throws Exception
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar calendar1 = file.addDefaultBaseCalendar();
      ProjectCalendar calendar2 = file.addDefaultBaseCalendar();
      CombinedCalendar combined = new CombinedCalendar(calendar1, calendar2);
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      // Ensure that for an unmodified working day the calendars have an 8-hour overlap
      Duration work = combined.getWork(Day.MONDAY, TimeUnit.HOURS);
      assertEquals(8.0, work.getDuration(), 0.0);
      work = combined.getWork(df.parse("31/10/2022"), TimeUnit.HOURS);
      assertEquals(8.0, work.getDuration(), 0.0);

      // Ensure that for an unmodified non-working day the calendars have no overlap
      work = combined.getWork(Day.SATURDAY, TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);
      work = combined.getWork(df.parse("29/10/2022"), TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);

      // Modify calendar1 so Tuesdays are working from 09:00 to 13:00
      ProjectCalendarHours hours = calendar1.getCalendarHours(Day.TUESDAY);
      hours.clear();
      hours.add(new TimeRange(DateHelper.getTime(9, 0), DateHelper.getTime(13, 0)));

      // Ensure that Tuesday only has 3 working hours
      work = combined.getWork(Day.TUESDAY, TimeUnit.HOURS);
      assertEquals(3.0, work.getDuration(), 0.0);
      work = combined.getWork(df.parse("01/11/2022"), TimeUnit.HOURS);
      assertEquals(3.0, work.getDuration(), 0.0);

      // Modify calendar1 so Wednesdays are working from 00:00 to 08:00
      hours = calendar1.getCalendarHours(Day.WEDNESDAY);
      hours.clear();
      hours.add(new TimeRange(DateHelper.getTime(0, 0), DateHelper.getTime(8, 0)));

      // Ensure Wednesday shows no working hours as there is no overlap
      work = combined.getWork(Day.WEDNESDAY, TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);
      work = combined.getWork(df.parse("02/11/2022"), TimeUnit.HOURS);
      assertEquals(0.0, work.getDuration(), 0.0);

      // Add an exception for a specific Thursday
      Date exceptionDate = df.parse("03/11/2022");
      ProjectCalendarException exception = calendar1.addCalendarException(exceptionDate);
      exception.add(new TimeRange(DateHelper.getTime(9, 0), DateHelper.getTime(13, 0)));

      work = combined.getWork(exceptionDate, TimeUnit.HOURS);
      assertEquals(3.0, work.getDuration(), 0.0);
   }
}
