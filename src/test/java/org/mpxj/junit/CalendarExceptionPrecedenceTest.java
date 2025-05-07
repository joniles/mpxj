/*
 * file:       CalendarExceptionPrecedenceTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       11/05/2023
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

import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.mpp.MPPReader;
import org.junit.Assert;
import org.junit.Test;

public class CalendarExceptionPrecedenceTest
{
   /**
    * Recurring exceptions should be overridden by non-recurring exceptions.
    * Based on a test case contributed by Wesley Lund.
    */
   @Test public void testCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");

      // Fridays in July
      Assert.assertFalse(calendar.isWorkingDate(LocalDate.of(2022, 7, 8)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 7, 15)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 7, 22)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 7, 29)));

      // Fridays in August
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 8, 5)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 8, 12)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 8, 19)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 8, 26)));

      // Fridays in September
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 9, 2)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 9, 9)));
      Assert.assertFalse(calendar.isWorkingDate(LocalDate.of(2022, 9, 16)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 9, 23)));
      Assert.assertTrue(calendar.isWorkingDate(LocalDate.of(2022, 9, 30)));
   }

   /**
    * Test daily recurring exceptions.
    */
   @Test public void testDailyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-daily.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(LocalDate.of(2023, 5, 12)));
   }

   /**
    * Test weekly recurring exceptions.
    */
   @Test public void testWeeklyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-weekly.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(LocalDate.of(2023, 5, 12)));
   }

   /**
    * Test monthly recurring exceptions.
    */
   @Test public void testMonthlyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-monthly.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(LocalDate.of(2023, 5, 12)));
   }

   /**
    * Test yearly recurring exceptions.
    */
   @Test public void testYearlyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-yearly.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(LocalDate.of(2023, 5, 12)));
   }
}