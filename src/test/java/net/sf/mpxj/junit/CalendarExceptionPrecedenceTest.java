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
package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;
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
      Assert.assertFalse(calendar.isWorkingDate(m_df.parse("08/07/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("15/07/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("22/07/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("29/07/2022")));

      // Fridays in August
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("05/08/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("05/12/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("05/19/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("05/16/2022")));

      // Fridays in September
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("02/09/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("09/09/2022")));
      Assert.assertFalse(calendar.isWorkingDate(m_df.parse("16/09/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("23/09/2022")));
      Assert.assertTrue(calendar.isWorkingDate(m_df.parse("30/09/2022")));
   }

   /**
    * Test daily recurring exceptions.
    */
   @Test public void testDailyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-daily.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(m_df.parse("12/05/2023")));
   }

   /**
    * Test weekly recurring exceptions.
    */
   @Test public void testWeeklyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-weekly.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(m_df.parse("12/05/2023")));
   }

   /**
    * Test monthly recurring exceptions.
    */
   @Test public void testMonthlyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-monthly.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(m_df.parse("12/05/2023")));
   }

   /**
    * Test yearly recurring exceptions.
    */
   @Test public void testYearlyCalendarExceptionPredecedence() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("calendar-exception-precedence-yearly.mpp"));
      ProjectCalendar calendar = project.getCalendarByName("TEST");
      Assert.assertFalse(calendar.isWorkingDate(m_df.parse("12/05/2023")));
   }

   private final DateFormat m_df = new SimpleDateFormat("dd/MM/yyyy");
}