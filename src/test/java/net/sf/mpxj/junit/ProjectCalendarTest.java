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

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.LocalTimeRange;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * This class contains tests used to exercise ProjectCalendar functionality.
 */
public class ProjectCalendarTest
{
   /**
    * Test get getWork method.
    */
   @Test public void testGetWork()
   {
      ProjectFile project = new ProjectFile();
      ProjectCalendar projectCalendar = project.addDefaultBaseCalendar();

      LocalDateTime startDate = LocalDateTime.of(2006, 3, 14, 8, 0);
      LocalDateTime endDate = LocalDateTime.of(2006, 3, 15, 8, 0);
      Duration variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 13, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(-1.0, variance.getDuration(), 0.01);

      startDate = LocalDateTime.of(2006, 3, 14, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 15, 9, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.13, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 15, 9, 30);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.19, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 15, 12, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.5, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 15, 13, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.5, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 15, 14, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.63, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 15, 16, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.88, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 15, 17, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 16, 7, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 16, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 18, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(4.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 19, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(4.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 20, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(4.0, variance.getDuration(), 0.01);

      startDate = LocalDateTime.of(2006, 3, 18, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 19, 17, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.0, variance.getDuration(), 0.01);

      startDate = LocalDateTime.of(2006, 3, 18, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 20, 17, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      startDate = LocalDateTime.of(2006, 3, 17, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 20, 17, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(2.0, variance.getDuration(), 0.01);

      startDate = LocalDateTime.of(2006, 3, 17, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 18, 17, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      //
      // Try a date in BST
      //
      startDate = LocalDateTime.of(2006, 7, 10, 8, 0);
      endDate = LocalDateTime.of(2006, 7, 11, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      //
      // Try a date crossing GMT to BST
      //
      startDate = LocalDateTime.of(2006, 3, 13, 8, 0);
      endDate = LocalDateTime.of(2006, 7, 11, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(86.0, variance.getDuration(), 0.01);

      //
      // Same date tests
      //
      startDate = LocalDateTime.of(2006, 3, 14, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 14, 8, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 9, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.13, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 10, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.25, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 11, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.38, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 12, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.5, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 13, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.5, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 16, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.88, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 17, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      endDate = LocalDateTime.of(2006, 3, 14, 18, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(1.0, variance.getDuration(), 0.01);

      //
      // Same date non-working day
      //
      startDate = LocalDateTime.of(2006, 3, 12, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 12, 17, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.0, variance.getDuration(), 0.01);

      //
      // Exception tests
      //
      startDate = LocalDateTime.of(2006, 3, 13, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 24, 16, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(9.88, variance.getDuration(), 0.01);

      projectCalendar.addCalendarException(LocalDate.of(2006, 3, 14), LocalDate.of(2006, 3, 14));

      startDate = LocalDateTime.of(2006, 3, 13, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 24, 16, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(8.88, variance.getDuration(), 0.01);

      ProjectCalendarException exception = projectCalendar.addCalendarException(LocalDate.of(206, 3, 18), LocalDate.of(2006, 3, 18));
      exception.add(new LocalTimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0)));

      startDate = LocalDateTime.of(2006, 3, 18, 8, 0);
      endDate = LocalDateTime.of(2006, 3, 18, 16, 0);
      variance = projectCalendar.getWork(startDate, endDate, TimeUnit.DAYS);
      assertEquals(0.5, variance.getDuration(), 0.01);
   }

   /**
    * Exercise various duration variance calculations.
    */
   @Test public void testVarianceCalculations9() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile file = reader.read(MpxjTestData.filePath("DurationTest9.mpp"));
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
    */
   @Test public void testVarianceCalculations8() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile file = reader.read(MpxjTestData.filePath("DurationTest8.mpp"));
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
    */
   @Test public void testGetDate()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar cal = file.addDefaultBaseCalendar();
      LocalDateTime startDate = LocalDateTime.of(2003, 10, 9, 8, 0);

      //
      // Add one 8 hour day
      //
      Duration duration = Duration.getInstance(8, TimeUnit.HOURS);
      LocalDateTime endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 10, 8, 0), endDate);

      //
      // Add two 8 hour days
      //
      duration = Duration.getInstance(16, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 10, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 13, 8, 0), endDate);

      //
      // Add three 8 hour days which span a weekend
      //
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 13, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 14, 8, 0), endDate);

      //
      // Add 9 hours from the start of a day
      //
      duration = Duration.getInstance(9, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 10, 9, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 10, 9, 0), endDate);

      //
      // Add 1 hour from the start of a day
      //
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 9, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 9, 9, 0), endDate);

      //
      // Add 1 hour offset by 1 hour from the start of a day
      //
      startDate = LocalDateTime.of(2003, 10, 9, 9, 0);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 10, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 9, 10, 0), endDate);

      //
      // Add 1 hour which crosses a date ranges
      //
      startDate = LocalDateTime.of(2003, 10, 9, 11, 30);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 13, 30), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 9, 13, 30), endDate);

      //
      // Add 1 hour at the start of the second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 13, 0);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 14, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 9, 14, 0), endDate);

      //
      // Add 1 hour offset by 1 hour from the start of the second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 14, 0);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 15, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 9, 15, 0), endDate);

      //
      // Full first range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 8, 0);
      duration = Duration.getInstance(4, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 12, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 9, 13, 0), endDate);

      //
      // Full second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 13, 0);
      duration = Duration.getInstance(4, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 10, 8, 0), endDate);

      //
      // Offset full first range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 9, 0);
      duration = Duration.getInstance(3, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 12, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 9, 13, 0), endDate);

      //
      // Offset full second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 14, 0);
      duration = Duration.getInstance(3, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 9, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 10, 8, 0), endDate);

      //
      // Cross weekend
      //
      startDate = LocalDateTime.of(2003, 10, 9, 8, 0);
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 13, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 14, 8, 0), endDate);

      //
      // Make Friday 10th a non-working day
      //
      cal.addCalendarException(LocalDate.of(2003, 10, 10), LocalDate.of(2003, 10, 10));

      //
      // Cross weekend with a non-working day exception
      //
      startDate = LocalDateTime.of(2003, 10, 9, 8, 0);
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 14, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 15, 8, 0), endDate);

      //
      // Make Saturday 11th a working day
      //
      ProjectCalendarException ex = cal.addCalendarException(LocalDate.of(2003, 10, 11), LocalDate.of(2003, 10, 11));
      ex.add(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(13, 0)));

      //
      // Cross weekend with a non-working day exception and a working day exception
      //
      startDate = LocalDateTime.of(2003, 10, 9, 8, 0);
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 14, 12, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 14, 13, 0), endDate);

      //
      // Make the start date a non-working day
      //
      startDate = LocalDateTime.of(2003, 10, 12, 8, 0);
      duration = Duration.getInstance(8, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration, false);
      assertEquals(LocalDateTime.of(2003, 10, 13, 17, 0), endDate);
      endDate = cal.getDate(startDate, duration, true);
      assertEquals(LocalDateTime.of(2003, 10, 14, 8, 0), endDate);
   }

   /**
    * Simple tests to exercise the ProjectCalendar.getStartTime method.
    */
   @Test public void testStartTime()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar cal = file.addDefaultBaseCalendar();

      //
      // Working day
      //
      assertEquals(LocalTime.of(8, 0), cal.getStartTime(LocalDate.of(2003, 10, 9)));

      //
      // Non-working day
      //
      assertNull(cal.getStartTime(LocalDate.of(2003, 10, 11)));
   }
}
