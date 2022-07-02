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

package net.sf.mpxj.junit.calendar;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.junit.MpxjTestData;

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
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      ProjectFile project = new UniversalProjectReader().read(file);
      ProjectCalendar calendar = project.getCalendarByName("Standard");

      Date startDate = DateHelper.getDayStartDate(df.parse("23/12/2019"));
      Date endDate = DateHelper.getDayEndDate(df.parse("08/01/2020"));

      Duration duration = calendar.getWork(startDate, endDate, TimeUnit.DAYS);

      assertEquals("9.0d", duration.toString());
   }
}
