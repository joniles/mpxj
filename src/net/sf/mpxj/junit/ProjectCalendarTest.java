/*
 * file:       ProjectCalendarTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       17-Mar-2006
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
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;

/**
 * This class contains tests used to exercise ProjectCalendar functionality.
 */
public class ProjectCalendarTest extends MPXJTestCase
{
   /**
    * Test get getWork method.
    * 
    * @throws Exception
    */
   public void testGetWork() throws Exception
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      Date startDate;
      Date endDate;
      Duration variance;

      ProjectFile project = new ProjectFile();
      ProjectCalendar projectCalendar = project.addDefaultBaseCalendar();

      startDate = df.parse("14/03/2006 08:00");
      endDate = df.parse("15/03/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      endDate = df.parse("13/03/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(-1.0, variance.getDuration(), 0.01);

      startDate = df.parse("14/03/2006 08:00");
      endDate = df.parse("15/03/2006 09:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.13, variance.getDuration(), 0.01);

      endDate = df.parse("15/03/2006 09:30");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.19, variance.getDuration(), 0.01);

      endDate = df.parse("15/03/2006 12:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.5, variance.getDuration(), 0.01);

      endDate = df.parse("15/03/2006 13:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.5, variance.getDuration(), 0.01);

      endDate = df.parse("15/03/2006 14:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.63, variance.getDuration(), 0.01);

      endDate = df.parse("15/03/2006 16:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.88, variance.getDuration(), 0.01);

      endDate = df.parse("15/03/2006 17:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      endDate = df.parse("16/03/2006 07:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      endDate = df.parse("16/03/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      endDate = df.parse("18/03/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(4.0, variance.getDuration(), 0.01);

      endDate = df.parse("19/03/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(4.0, variance.getDuration(), 0.01);

      endDate = df.parse("20/03/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(4.0, variance.getDuration(), 0.01);

      startDate = df.parse("18/03/2006 08:00");
      endDate = df.parse("19/03/2006 17:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.0, variance.getDuration(), 0.01);

      startDate = df.parse("18/03/2006 08:00");
      endDate = df.parse("20/03/2006 17:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      startDate = df.parse("17/03/2006 08:00");
      endDate = df.parse("20/03/2006 17:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      startDate = df.parse("17/03/2006 08:00");
      endDate = df.parse("18/03/2006 17:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      //
      // Try a date in BST
      //
      startDate = df.parse("10/07/2006 08:00");
      endDate = df.parse("11/07/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      //
      // Try a date crossing GMT to BST
      //
      startDate = df.parse("13/03/2006 08:00");
      endDate = df.parse("11/07/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(86.0, variance.getDuration(), 0.01);

      //
      // Same date tests
      //
      startDate = df.parse("14/03/2006 08:00");
      endDate = df.parse("14/03/2006 08:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.0, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 09:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.13, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 10:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.25, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 11:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.38, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 12:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.5, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 13:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.5, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 16:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.88, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 17:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      endDate = df.parse("14/03/2006 18:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      //
      // Same date non-working day
      //
      startDate = df.parse("12/03/2006 08:00");
      endDate = df.parse("12/03/2006 17:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.0, variance.getDuration(), 0.01);

      //
      // Exception tests
      //
      startDate = df.parse("13/03/2006 08:00");
      endDate = df.parse("24/03/2006 16:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(9.88, variance.getDuration(), 0.01);

      projectCalendar.addCalendarException(df.parse("14/03/2006 00:00"), df.parse("14/03/2006 23:59"));

      startDate = df.parse("13/03/2006 08:00");
      endDate = df.parse("24/03/2006 16:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(8.88, variance.getDuration(), 0.01);

      ProjectCalendarException exception = projectCalendar.addCalendarException(df.parse("18/03/2006 00:00"), df.parse("18/03/2006 23:59"));
      exception.addRange(new DateRange(df.parse("18/03/2006 08:00"), df.parse("18/03/2006 12:00")));

      startDate = df.parse("18/03/2006 08:00");
      endDate = df.parse("18/03/2006 16:00");
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.5, variance.getDuration(), 0.01);
   }

   /**
    * Exercise various duration variance calculations.
    * 
    * @throws Exception
    */
   public void testVarianceCalculations9() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile file = reader.read(m_basedir + "/DurationTest9.mpp");
      Task task;
      Duration duration;

      //
      // Task 1
      //
      task = file.getTaskByID(Integer.valueOf(1));

      duration = task.getDurationVariance();
      assertEquals(-59.0, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.MINUTES, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(-1.09, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getFinishVariance();
      assertEquals(-1.97, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 2
      //
      task = file.getTaskByID(Integer.valueOf(2));

      duration = task.getDurationVariance();
      assertEquals(0.98, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.HOURS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(0.94, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getFinishVariance();
      assertEquals(0.13, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 3
      //
      task = file.getTaskByID(Integer.valueOf(3));

      duration = task.getDurationVariance();
      assertEquals(-4, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(0.88, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getFinishVariance();
      assertEquals(-1, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 4
      //
      task = file.getTaskByID(Integer.valueOf(4));

      duration = task.getDurationVariance();
      assertEquals(0.8, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.WEEKS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(0, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 5
      //
      task = file.getTaskByID(Integer.valueOf(5));

      duration = task.getDurationVariance();
      assertEquals(-1.45, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.MONTHS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(0, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 6
      //
      task = file.getTaskByID(Integer.valueOf(6));

      duration = task.getDurationVariance();
      assertEquals(-59, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.MINUTES, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(-5, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 7
      //
      task = file.getTaskByID(Integer.valueOf(7));

      duration = task.getDurationVariance();
      assertEquals(0.98, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.HOURS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(-5, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 8
      //
      task = file.getTaskByID(Integer.valueOf(8));

      duration = task.getDurationVariance();
      assertEquals(-4, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(-5, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 9
      //
      task = file.getTaskByID(Integer.valueOf(9));

      duration = task.getDurationVariance();
      assertEquals(0.8, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.WEEKS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(-6, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 10
      //
      task = file.getTaskByID(Integer.valueOf(10));

      duration = task.getDurationVariance();
      assertEquals(-1.5, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.MONTHS, duration.getUnits());

      //
      // Task 11
      //
      task = file.getTaskByID(Integer.valueOf(11));

      duration = task.getDurationVariance();
      assertEquals(-59, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.ELAPSED_MINUTES, duration.getUnits());

      //
      // Task 12
      //
      task = file.getTaskByID(Integer.valueOf(12));

      duration = task.getDurationVariance();
      assertEquals(0.98, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.ELAPSED_HOURS, duration.getUnits());

      //
      // Task 13
      //
      task = file.getTaskByID(Integer.valueOf(13));

      duration = task.getDurationVariance();
      assertEquals(-0.67, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.ELAPSED_DAYS, duration.getUnits());

      //
      // Task 14
      //
      task = file.getTaskByID(Integer.valueOf(14));

      duration = task.getDurationVariance();
      assertEquals(0.95, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.ELAPSED_WEEKS, duration.getUnits());

      //
      // Task 15
      //
      task = file.getTaskByID(Integer.valueOf(15));

      duration = task.getDurationVariance();
      assertEquals(0.44, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.ELAPSED_MONTHS, duration.getUnits());
   }

   /**
    * Exercise various duration variance calculations.
    * 
    * @throws Exception
    */
   public void testVarianceCalculations8() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile file = reader.read(m_basedir + "/DurationTest8.mpp");
      Task task;
      Duration duration;

      //
      // Task 1
      //
      task = file.getTaskByID(Integer.valueOf(1));

      duration = task.getDurationVariance();
      assertEquals(-59.0, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.MINUTES, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(-1.09, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getFinishVariance();
      assertEquals(-1.97, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 2
      //
      task = file.getTaskByID(Integer.valueOf(2));

      duration = task.getDurationVariance();
      assertEquals(0.98, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.HOURS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(0.94, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getFinishVariance();
      assertEquals(0.13, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 3
      //
      task = file.getTaskByID(Integer.valueOf(3));

      duration = task.getDurationVariance();
      assertEquals(-4, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(0.88, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      duration = task.getFinishVariance();
      assertEquals(-1, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());

      //
      // Task 4
      //
      task = file.getTaskByID(Integer.valueOf(4));

      duration = task.getDurationVariance();
      assertEquals(0.8, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.WEEKS, duration.getUnits());

      duration = task.getStartVariance();
      assertEquals(0, duration.getDuration(), 0.01);
      assertEquals(TimeUnit.DAYS, duration.getUnits());
   }

   /**
    * Simple tests to exercise the ProjectCalendar.getDate method.
    *
    * @throws Exception
    */
   public void testGetDate() throws Exception
   {
      ProjectFile file = new ProjectFile();
      Duration duration;
      ProjectCalendar cal = file.addDefaultBaseCalendar();
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      Date startDate = df.parse("09/10/2003 08:00");

      //
      // Add one 8 hour day
      //
      duration = Duration.getInstance(8, TimeUnit.HOURS);
      Date endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("10/10/2003 08:00", df.format(endDate));

      //
      // Add two 8 hour days
      //
      duration = Duration.getInstance(16, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("10/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("13/10/2003 08:00", df.format(endDate));

      //
      // Add three 8 hour days which span a weekend
      //
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("13/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("14/10/2003 08:00", df.format(endDate));

      //
      // Add 9 hours from the start of a day
      //
      duration = Duration.getInstance(9, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("10/10/2003 09:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("10/10/2003 09:00", df.format(endDate));

      //
      // Add 1 hour from the start of a day
      //
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 09:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("09/10/2003 09:00", df.format(endDate));

      //
      // Add 1 hour offset by 1 hour from the start of a day
      //
      startDate = df.parse("09/10/2003 09:00");
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 10:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("09/10/2003 10:00", df.format(endDate));

      //
      // Add 1 hour which crosses a date ranges
      //
      startDate = df.parse("09/10/2003 11:30");
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 13:30", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("09/10/2003 13:30", df.format(endDate));

      //
      // Add 1 hour at the start of the second range
      //     
      startDate = df.parse("09/10/2003 13:00");
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 14:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("09/10/2003 14:00", df.format(endDate));

      //
      // Add 1 hour offset by 1 hour from the start of the second range
      //     
      startDate = df.parse("09/10/2003 14:00");
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 15:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("09/10/2003 15:00", df.format(endDate));

      //
      // Full first range
      //
      startDate = df.parse("09/10/2003 08:00");
      duration = Duration.getInstance(4, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 12:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("09/10/2003 13:00", df.format(endDate));

      //
      // Full second range
      //
      startDate = df.parse("09/10/2003 13:00");
      duration = Duration.getInstance(4, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("10/10/2003 08:00", df.format(endDate));

      //
      // Offset full first range
      //
      startDate = df.parse("09/10/2003 09:00");
      duration = Duration.getInstance(3, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 12:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("09/10/2003 13:00", df.format(endDate));

      //
      // Offset full second range
      //
      startDate = df.parse("09/10/2003 14:00");
      duration = Duration.getInstance(3, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("09/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("10/10/2003 08:00", df.format(endDate));

      //
      // Cross weekend
      //
      startDate = df.parse("09/10/2003 8:00");
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("13/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("14/10/2003 08:00", df.format(endDate));

      //
      // Make Friday 10th a non-working day
      //
      cal.addCalendarException(df.parse("10/10/2003 00:00"), df.parse("10/10/2003 23:59"));

      //
      // Cross weekend with a non-working day exception
      //
      startDate = df.parse("09/10/2003 8:00");
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("14/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("15/10/2003 08:00", df.format(endDate));

      //
      // Make Saturday 11th a working day
      //
      ProjectCalendarException ex = cal.addCalendarException(df.parse("11/10/2003 00:00"), df.parse("11/10/2003 23:59"));
      ex.addRange(new DateRange(df.parse("11/10/2003 09:00"), df.parse("11/10/2003 13:00")));

      //
      // Cross weekend with a non-working day exception and a working day exception
      //
      startDate = df.parse("09/10/2003 8:00");
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("14/10/2003 12:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("14/10/2003 13:00", df.format(endDate));

      //
      // Make the start date a non-working day
      //
      startDate = df.parse("12/10/2003 8:00");
      duration = Duration.getInstance(8, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals("13/10/2003 17:00", df.format(endDate));
      endDate = cal.getDate(startDate, duration, true);
      assertEquals("14/10/2003 08:00", df.format(endDate));
   }

   /**
    * Simple tests to exercise the ProjectCalendar.getStartTime method. 
    * 
    * @throws Exception
    */
   public void testStartTime() throws Exception
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar cal = file.addDefaultBaseCalendar();
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

      //
      // Working day
      //
      assertEquals("01/01/0001 08:00", df.format(cal.getStartTime(df.parse("09/10/2003 00:00"))));

      //
      // Non-working day
      //
      assertNull(cal.getStartTime(df.parse("11/10/2003 00:00")));
   }
}
