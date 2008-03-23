/*
 * file:       MppCalendarTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2006
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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppCalendarTest extends MPXJTestCase
{
   /**
    * Test calendar data read from an MPP9 file.
    *
    * @throws Exception
    */
    public void testMpp9Calendar()
       throws Exception
    {
        ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp9calendar.mpp");
        testCalendars(mpp);
    }

    /**
     * Test calendar data read from an MPP12 file.
     *
     * @throws Exception
     */
    public void testMpp12Calendar()
       throws Exception
    {
       ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp12calendar.mpp");
       testCalendars(mpp);
    }

    /**
     * Test calendar data read from an MPD9 file.
     *
     * @throws Exception
     */
    public void testMpd9Calendar()
       throws Exception
    {
       ProjectFile mpp = new MPDDatabaseReader().read (m_basedir + "/mpp9calendar.mpd");
       testCalendars(mpp);
    }

    /**
     * Test calendar exception data read from an MPP9 file.
     *
     * @throws Exception
     */
     public void testMpp9CalendarExceptions()
        throws Exception
     {
         ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp9exceptions.mpp");
         testExceptions(mpp);
     }

     /**
      * Test calendar exception data read from an MPP12 file.
      *
      * @throws Exception
      */
      public void testMpp12CalendarExceptions()
         throws Exception
      {
          ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp12exceptions.mpp");
          testExceptions(mpp);
      }

    /**
     * Test calendar data.
     *
     * @param mpp ProjectFile instance
     */
    private void testCalendars(ProjectFile mpp)
    {
       DateFormat tf = new SimpleDateFormat ("HH:mm");

       List<ProjectCalendar> baseCalendars = mpp.getBaseCalendars();
       assertEquals(5, baseCalendars.size());

       ProjectCalendar cal = mpp.getBaseCalendarByUniqueID(new Integer(1));
       assertNotNull(cal);
       assertEquals("Standard", cal.getName());
       assertNull(cal.getBaseCalendar());
       assertTrue(cal.isBaseCalendar());
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.MONDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.TUESDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.WEDNESDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.THURSDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.FRIDAY));

       assertEquals(ProjectCalendar.NON_WORKING, cal.getWorkingDay(Day.SATURDAY));
       assertEquals(ProjectCalendar.NON_WORKING, cal.getWorkingDay(Day.SUNDAY));

       assertEquals(0, cal.getCalendarExceptions().size());

       ProjectCalendarHours hours = cal.getCalendarHours(Day.MONDAY);
       assertEquals(2, hours.getDateRangeCount());

       DateRange range = hours.getDateRange(0);
       assertEquals("08:00", tf.format(range.getStartDate()));
       assertEquals("12:00", tf.format(range.getEndDate()));
       range = cal.getCalendarHours(Day.MONDAY).getDateRange(1);
       assertEquals("13:00", tf.format(range.getStartDate()));
       assertEquals("17:00", tf.format(range.getEndDate()));

       List<ProjectCalendar> resourceCalendars = mpp.getResourceCalendars();
       assertEquals(2, resourceCalendars.size());
    }

    /**
     * Test calendar exceptions.
     *
     * @param mpp ProjectFile instance
     */
    private void testExceptions(ProjectFile mpp)
    {
       DateFormat df = new SimpleDateFormat ("dd/MM/yyyy HH:mm");
       DateFormat tf = new SimpleDateFormat ("HH:mm");

       List<ProjectCalendar> baseCalendars = mpp.getBaseCalendars();
       assertEquals(1, baseCalendars.size());

       ProjectCalendar cal = mpp.getBaseCalendarByUniqueID(new Integer(1));
       assertNotNull(cal);
       assertEquals("Standard", cal.getName());
       assertNull(cal.getBaseCalendar());
       assertTrue(cal.isBaseCalendar());
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.MONDAY));
       assertEquals(ProjectCalendar.NON_WORKING, cal.getWorkingDay(Day.TUESDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.WEDNESDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.THURSDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.FRIDAY));
       assertEquals(ProjectCalendar.WORKING, cal.getWorkingDay(Day.SATURDAY));
       assertEquals(ProjectCalendar.NON_WORKING, cal.getWorkingDay(Day.SUNDAY));

       List<net.sf.mpxj.ProjectCalendarException> exceptions = cal.getCalendarExceptions();
       assertEquals(3, exceptions.size());

       ProjectCalendarException exception = exceptions.get(0);
       assertFalse(exception.getWorking());
       assertEquals("05/03/2008 00:00", df.format(exception.getFromDate()));
       assertEquals("05/03/2008 23:59", df.format(exception.getToDate()));
       assertNull(exception.getFromTime1());
       assertNull(exception.getToTime1());
       assertNull(exception.getFromTime2());
       assertNull(exception.getToTime2());
       assertNull(exception.getFromTime3());
       assertNull(exception.getToTime3());
       assertNull(exception.getFromTime4());
       assertNull(exception.getToTime4());
       assertNull(exception.getFromTime5());
       assertNull(exception.getToTime5());

       exception = exceptions.get(1);
       assertTrue(exception.getWorking());
       assertEquals("09/03/2008 00:00", df.format(exception.getFromDate()));
       assertEquals("09/03/2008 23:59", df.format(exception.getToDate()));
       assertEquals("08:00", tf.format(exception.getFromTime1()));
       assertEquals("12:00", tf.format(exception.getToTime1()));
       assertEquals("13:00", tf.format(exception.getFromTime2()));
       assertEquals("17:00", tf.format(exception.getToTime2()));
       assertNull(exception.getFromTime3());
       assertNull(exception.getToTime3());
       assertNull(exception.getFromTime4());
       assertNull(exception.getToTime4());
       assertNull(exception.getFromTime5());
       assertNull(exception.getToTime5());

       exception = exceptions.get(2);
       assertTrue(exception.getWorking());
       assertEquals("16/03/2008 00:00", df.format(exception.getFromDate()));
       assertEquals("16/03/2008 23:59", df.format(exception.getToDate()));
       assertEquals("08:00", tf.format(exception.getFromTime1()));
       assertEquals("09:00", tf.format(exception.getToTime1()));
       assertEquals("11:00", tf.format(exception.getFromTime2()));
       assertEquals("12:00", tf.format(exception.getToTime2()));
       assertEquals("14:00", tf.format(exception.getFromTime3()));
       assertEquals("15:00", tf.format(exception.getToTime3()));
       assertEquals("16:00", tf.format(exception.getFromTime4()));
       assertEquals("17:00", tf.format(exception.getToTime4()));
       assertEquals("18:00", tf.format(exception.getFromTime5()));
       assertEquals("19:00", tf.format(exception.getToTime5()));
    }
}
