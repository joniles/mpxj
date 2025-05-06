/*
 * file:       MultiDayExceptionsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       09/06/2020
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

package org.mpxj.junit.calendar;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.time.LocalDateTime;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.TimeUnit;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure working day calculations operate as expected across multi-day calendar exceptions.
 * <a href="https://github.com/joniles/mpxj/issues/125">https://github.com/joniles/mpxj/issues/125</a>
 */
public class MultiDayExceptionsTest
{
   /**
    * Test to validate calendars in files saved by different versions of MS Project.
    */
   @Test public void testMultiDayExceptions() throws Exception
   {
      for (File file : MpxjTestData.listFiles("calendar/multi-day-exceptions", "multi-day-exceptions"))
      {
         testMultiDayExceptions(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testMultiDayExceptions(File file) throws Exception
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      ProjectCalendar calendar = project.getCalendarByName("Standard");

      LocalDateTime startDate = LocalDateTime.of(2019, 12, 23, 0, 0, 0);
      LocalDateTime endDate = LocalDateTime.of(2020, 1, 8, 23, 59, 59);

      Duration duration = calendar.getWork(startDate, endDate, TimeUnit.DAYS);

      assertEquals("9.0d", duration.toString());
   }
}
