/*
 * file:       MppCalendarTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       5-October-2006
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

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import java.time.DayOfWeek;
import org.mpxj.DayType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.LocalTimeRange;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppCalendarTest
{
   /**
    * Test calendar data read from an MPP9 file.
    */
   @Test public void testMpp9Calendar() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9calendar.mpp"));
      testCalendars(mpp);
   }

   /**
    * Test calendar data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9CalendarFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9calendar-from12.mpp"));
      testCalendars(mpp);
   }

   /**
    * Test calendar data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9CalendarFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9calendar-from14.mpp"));
      testCalendars(mpp);
   }

   /**
    * Test calendar data read from an MPP12 file.
    */
   @Test public void testMpp12Calendar() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12calendar.mpp"));
      testCalendars(mpp);
   }

   /**
    * Test calendar data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12CalendarFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14calendar.mpp"));
      testCalendars(mpp);
   }

   /**
    * Test calendar data read from an MPP14 file.
    */
   @Test public void testMpp14Calendar() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14calendar.mpp"));
      testCalendars(mpp);
   }

   /**
    * Test calendar data read from an MPD9 file.
    */
   @Test public void testMpd9Calendar() throws Exception
   {
      ProjectFile mpp = new MPDFileReader().read(MpxjTestData.filePath("mpp9calendar.mpd"));
      testCalendars(mpp);
   }

   /**
    * Test calendar exception data read from an MPP9 file.
    */
   @Test public void testMpp9CalendarExceptions() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9exceptions.mpp"));
      testExceptions(mpp);
   }

   /**
    * Test calendar exception data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9CalendarExceptionsFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9exceptions-from12.mpp"));
      testExceptions(mpp);
   }

   /**
    * Test calendar exception data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9CalendarExceptionsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9exceptions-from14.mpp"));
      testExceptions(mpp);
   }

   /**
    * Test calendar exception data read from an MPP12 file.
    */
   @Test public void testMpp12CalendarExceptions() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12exceptions.mpp"));
      testExceptions(mpp);
   }

   /**
    * Test calendar exception data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12CalendarExceptionsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12exceptions-from14.mpp"));
      testExceptions(mpp);
   }

   /**
    * Test calendar exception data read from an MPP14 file.
    */
   @Test public void testMpp14CalendarExceptions() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14exceptions.mpp"));
      testExceptions(mpp);
   }

   /**
    * Test calendar data.
    *
    * @param mpp ProjectFile instance
    */
   private void testCalendars(ProjectFile mpp)
   {
      ProjectCalendar cal = mpp.getCalendarByUniqueID(Integer.valueOf(1));
      assertNotNull(cal);
      assertEquals("Standard", cal.getName());
      assertNull(cal.getParent());
      assertFalse(cal.isDerived());
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.MONDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.TUESDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.WEDNESDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.THURSDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.FRIDAY));

      assertEquals(DayType.NON_WORKING, cal.getCalendarDayType(DayOfWeek.SATURDAY));
      assertEquals(DayType.NON_WORKING, cal.getCalendarDayType(DayOfWeek.SUNDAY));

      assertEquals(0, cal.getCalendarExceptions().size());

      ProjectCalendarHours hours = cal.getCalendarHours(DayOfWeek.MONDAY);
      assertEquals(2, hours.size());

      LocalTimeRange range = hours.get(0);
      assertEquals(LocalTime.of(8, 0), range.getStart());
      assertEquals(LocalTime.of(12, 0), range.getEnd());
      range = cal.getCalendarHours(DayOfWeek.MONDAY).get(1);
      assertEquals(LocalTime.of(13, 0), range.getStart());
      assertEquals(LocalTime.of(17, 0), range.getEnd());
   }

   /**
    * Test calendar exceptions.
    *
    * @param mpp ProjectFile instance
    */
   private void testExceptions(ProjectFile mpp)
   {
      ProjectCalendar cal = mpp.getCalendarByUniqueID(Integer.valueOf(1));
      assertNotNull(cal);
      assertEquals("Standard", cal.getName());
      assertNull(cal.getParent());
      assertFalse(cal.isDerived());
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.MONDAY));
      assertEquals(DayType.NON_WORKING, cal.getCalendarDayType(DayOfWeek.TUESDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.WEDNESDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.THURSDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.FRIDAY));
      assertEquals(DayType.WORKING, cal.getCalendarDayType(DayOfWeek.SATURDAY));
      assertEquals(DayType.NON_WORKING, cal.getCalendarDayType(DayOfWeek.SUNDAY));

      List<ProjectCalendarException> exceptions = cal.getCalendarExceptions();
      assertEquals(3, exceptions.size());

      ProjectCalendarException exception = exceptions.get(0);
      assertFalse(exception.getWorking());
      assertEquals(LocalDate.of(2008, 3, 5), exception.getFromDate());
      assertEquals(LocalDate.of(2008, 3, 5), exception.getToDate());
      assertNull(exception.get(0).getStart());
      assertNull(exception.get(0).getEnd());
      assertNull(exception.get(1).getStart());
      assertNull(exception.get(1).getEnd());
      assertNull(exception.get(2).getStart());
      assertNull(exception.get(2).getEnd());
      assertNull(exception.get(3).getStart());
      assertNull(exception.get(3).getEnd());
      assertNull(exception.get(4).getStart());
      assertNull(exception.get(4).getEnd());

      exception = exceptions.get(1);
      assertTrue(exception.getWorking());
      assertEquals(LocalDate.of(2008, 3, 9), exception.getFromDate());
      assertEquals(LocalDate.of(2008, 3, 9), exception.getToDate());
      assertEquals(LocalTime.of(8, 0), exception.get(0).getStart());
      assertEquals(LocalTime.of(12, 0), exception.get(0).getEnd());
      assertEquals(LocalTime.of(13, 0), exception.get(1).getStart());
      assertEquals(LocalTime.of(17, 0), exception.get(1).getEnd());
      assertNull(exception.get(2).getStart());
      assertNull(exception.get(2).getEnd());
      assertNull(exception.get(3).getStart());
      assertNull(exception.get(3).getEnd());
      assertNull(exception.get(4).getStart());
      assertNull(exception.get(4).getEnd());

      exception = exceptions.get(2);
      assertTrue(exception.getWorking());
      assertEquals(LocalDate.of(2008, 3, 16), exception.getFromDate());
      assertEquals(LocalDate.of(2008, 3, 16), exception.getToDate());
      assertEquals(LocalTime.of(8, 0), exception.get(0).getStart());
      assertEquals(LocalTime.of(9, 0), exception.get(0).getEnd());
      assertEquals(LocalTime.of(11, 0), exception.get(1).getStart());
      assertEquals(LocalTime.of(12, 0), exception.get(1).getEnd());
      assertEquals(LocalTime.of(14, 0), exception.get(2).getStart());
      assertEquals(LocalTime.of(15, 0), exception.get(2).getEnd());
      assertEquals(LocalTime.of(16, 0), exception.get(3).getStart());
      assertEquals(LocalTime.of(17, 0), exception.get(3).getEnd());
      assertEquals(LocalTime.of(18, 0), exception.get(4).getStart());
      assertEquals(LocalTime.of(19, 0), exception.get(4).getEnd());
   }
}
