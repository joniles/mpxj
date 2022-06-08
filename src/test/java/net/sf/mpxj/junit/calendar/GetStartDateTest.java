/*
 * file:       GetStartDateTest.java
 * author:     Jon Iles
 * date:       2022-06-08
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

package net.sf.mpxj.junit.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.TimeUnit;

/**
 * Tests to ensure basic calendar details are read correctly.
 */
public class GetStartDateTest
{
   /**
    * Test to validate calendars in files saved by different versions of MS Project.
    */
   @Test public void testGetStartDate()
   {
      ProjectFile file = new ProjectFile();
      ProjectProperties props = file.getProjectProperties();
      props.setStartDate(getDate("01/05/2022 08:00"));

      //
      // Ensure that we can calculate a start date after skipping
      // a lengthy non-working period.
      //
      ProjectCalendar calendar = file.addDefaultBaseCalendar();
      ProjectCalendarException exception = calendar.addCalendarException(getDate("01/06/2022 00:00"), getDate("14/06/2022 23:59"));
      Date startDate = calendar.getStartDate(getDate("16/06/2022 17:00"), Duration.getInstance(5, TimeUnit.DAYS));
      assertEquals("27/05/2022 08:00", m_df.format(startDate));

      //
      // Ensure that we bail out if there are not enough working days
      // available to calculate a start date.
      //
      calendar.removeCalendarException(exception);
      calendar.setWorkingDay(Day.MONDAY, false);
      calendar.setWorkingDay(Day.TUESDAY, false);
      calendar.setWorkingDay(Day.WEDNESDAY, false);
      calendar.setWorkingDay(Day.THURSDAY, false);
      calendar.setWorkingDay(Day.FRIDAY, false);

      startDate = calendar.getStartDate(getDate("16/06/2022 17:00"), Duration.getInstance(5, TimeUnit.DAYS));
      assertNull(startDate);
   }

   private Date getDate(String date)
   {
      try
      {
         return m_df.parse(date);
      }
      catch (ParseException e)
      {
         throw new RuntimeException(e);
      }
   }

   private final DateFormat m_df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
}
