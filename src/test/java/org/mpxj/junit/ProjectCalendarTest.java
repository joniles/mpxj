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

package org.mpxj.junit;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;

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
      LocalDateTime endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 17, 0), endDate);

      //
      // Add two 8 hour days
      //
      duration = Duration.getInstance(16, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 10, 17, 0), endDate);

      //
      // Add three 8 hour days which span a weekend
      //
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 13, 17, 0), endDate);

      //
      // Add 9 hours from the start of a day
      //
      duration = Duration.getInstance(9, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 10, 9, 0), endDate);

      //
      // Add 1 hour from the start of a day
      //
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 9, 0), endDate);

      //
      // Add 1 hour offset by 1 hour from the start of a day
      //
      startDate = LocalDateTime.of(2003, 10, 9, 9, 0);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 10, 0), endDate);

      //
      // Add 1 hour which crosses a date ranges
      //
      startDate = LocalDateTime.of(2003, 10, 9, 11, 30);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 13, 30), endDate);

      //
      // Add 1 hour at the start of the second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 13, 0);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 14, 0), endDate);

      //
      // Add 1 hour offset by 1 hour from the start of the second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 14, 0);
      duration = Duration.getInstance(1, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 15, 0), endDate);

      //
      // Full first range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 8, 0);
      duration = Duration.getInstance(4, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 12, 0), endDate);

      //
      // Full second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 13, 0);
      duration = Duration.getInstance(4, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 17, 0), endDate);

      //
      // Offset full first range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 9, 0);
      duration = Duration.getInstance(3, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 12, 0), endDate);

      //
      // Offset full second range
      //
      startDate = LocalDateTime.of(2003, 10, 9, 14, 0);
      duration = Duration.getInstance(3, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 17, 0), endDate);

      //
      // Cross weekend
      //
      startDate = LocalDateTime.of(2003, 10, 9, 8, 0);
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 13, 17, 0), endDate);

      //
      // Make Friday 10th a non-working day
      //
      cal.addCalendarException(LocalDate.of(2003, 10, 10), LocalDate.of(2003, 10, 10));

      //
      // Cross weekend with a non-working day exception
      //
      startDate = LocalDateTime.of(2003, 10, 9, 8, 0);
      duration = Duration.getInstance(24, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 14, 17, 0), endDate);

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
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 14, 12, 0), endDate);

      //
      // Make the start date a non-working day
      //
      startDate = LocalDateTime.of(2003, 10, 12, 8, 0);
      duration = Duration.getInstance(8, TimeUnit.HOURS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 13, 17, 0), endDate);
   }

   /**
    * Simple tests to exercise the ProjectCalendar.getDate method with a negative duration.
    */
   @Test public void testGetDateWithNegativeDuration()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar cal = file.addDefaultBaseCalendar();
      LocalDateTime endDate = LocalDateTime.of(2003, 10, 9, 17, 0);

      //
      // Subtract one 8 hour day
      //
      Duration duration = Duration.getInstance(-8, TimeUnit.HOURS);
      LocalDateTime startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 8, 0), startDate);

      //
      // Subtract two 8 hour days
      //
      duration = Duration.getInstance(-16, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 8, 8, 0), startDate);

      //
      // Subtract five 8 hour days which span a weekend
      //
      duration = Duration.getInstance(-40, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 3, 8, 0), startDate);

      //
      // Subtract 9 hours from the end of a day
      //
      duration = Duration.getInstance(-9, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 8, 16, 0), startDate);

      //
      // Subtract 1 hour from the end of a day
      //
      duration = Duration.getInstance(-1, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 16, 0), startDate);

      //
      // Subtract 1 hour offset by 1 hour from the end of a day
      //
      endDate = LocalDateTime.of(2003, 10, 9, 16, 0);
      duration = Duration.getInstance(-1, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 15, 0), startDate);

      //
      // Subtract 1 hour which crosses a date ranges
      //
      endDate = LocalDateTime.of(2003, 10, 9, 13, 30);
      duration = Duration.getInstance(-1, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 11, 30), startDate);

      //
      // Subtract 1 hour at the start of the first range
      //
      endDate = LocalDateTime.of(2003, 10, 9, 12, 0);
      duration = Duration.getInstance(-1, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 11, 0), startDate);

      //
      // Subtract 1 hour offset by 1 hour from the end of the first range
      //
      endDate = LocalDateTime.of(2003, 10, 9, 11, 0);
      duration = Duration.getInstance(-1, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 10, 0), startDate);

      //
      // Full first range
      //
      endDate = LocalDateTime.of(2003, 10, 9, 12, 0);
      duration = Duration.getInstance(-4, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 8, 0), startDate);

      //
      // Full second range
      //
      endDate = LocalDateTime.of(2003, 10, 9, 17, 0);
      duration = Duration.getInstance(-4, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 13, 0), startDate);

      //
      // Offset full first range
      //
      endDate = LocalDateTime.of(2003, 10, 9, 11, 0);
      duration = Duration.getInstance(-3, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 8, 0), startDate);

      //
      // Offset full second range
      //
      endDate = LocalDateTime.of(2003, 10, 9, 16, 0);
      duration = Duration.getInstance(-3, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 9, 13, 0), startDate);

      //
      // Cross weekend
      //
      endDate = LocalDateTime.of(2003, 10, 6, 9, 0);
      duration = Duration.getInstance(-24, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 1, 9, 0), startDate);

      //
      // Make Friday 3rd a non-working day
      //
      cal.addCalendarException(LocalDate.of(2003, 10, 3), LocalDate.of(2003, 10, 3));

      //
      // Cross weekend with a non-working day exception
      //
      endDate = LocalDateTime.of(2003, 10, 6, 9, 0);
      duration = Duration.getInstance(-24, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 9, 30, 9, 0), startDate);

      //
      // Make Saturday 4th a working day
      //
      ProjectCalendarException ex = cal.addCalendarException(LocalDate.of(2003, 10, 4), LocalDate.of(2003, 10, 4));
      ex.add(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(13, 0)));

      //
      // Cross weekend with a non-working day exception and a working day exception
      //
      endDate = LocalDateTime.of(2003, 10, 6, 9, 0);
      duration = Duration.getInstance(-24, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 9, 30, 14, 0), startDate);

      //
      // Make the end date a non-working day
      //
      endDate = LocalDateTime.of(2003, 10, 12, 8, 0);
      duration = Duration.getInstance(-8, TimeUnit.HOURS);
      startDate = cal.getDate(endDate, duration);
      assertEquals(LocalDateTime.of(2003, 10, 10, 8, 0), startDate);
   }

   /**
    * Simple tests to exercise the ProjectCalendar.getDate method with a negative duration including midnight.
    */
   @Test public void testMidnightNegativeDuration()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar calendar = new ProjectCalendar(file);

      List<LocalTimeRange> ranges = Arrays.asList(
         new LocalTimeRange(LocalTime.of(0, 0), LocalTime.of(4, 30)),
         new LocalTimeRange(LocalTime.of(8, 30), LocalTime.of(0, 0)));

      Arrays.stream(DayOfWeek.values()).forEach(d -> calendar.setCalendarDayType(d, DayType.WORKING));

      calendar.addCalendarHours(DayOfWeek.MONDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.TUESDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.WEDNESDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.THURSDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.FRIDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.SATURDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.SUNDAY).addAll(ranges);

      // Within first range
      LocalDateTime result = calendar.getDate(LocalDateTime.of(2024, 2, 28, 4, 0), Duration.getInstance(-2, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 2, 0), result);

      // From end of first range
      result = calendar.getDate(LocalDateTime.of(2024, 2, 28, 4, 30), Duration.getInstance(-2, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 2, 30), result);

      // All of second range
      result = calendar.getDate(LocalDateTime.of(2024, 2, 28, 4, 30), Duration.getInstance(-4.5, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 0, 0), result);

      // From end of second range
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 0, 0), Duration.getInstance(-1, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 23, 0), result);

      // All of the second range
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 0, 0), Duration.getInstance(-15.5, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 8, 30), result);

      // Overlap both ranges
      result = calendar.getDate(LocalDateTime.of(2024, 2, 28, 23, 0), Duration.getInstance(-15, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 4, 0), result);

      // All of both ranges
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 0, 0), Duration.getInstance(-20, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 0, 0), result);

      // Overlap across midnight
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 4, 0), Duration.getInstance(-6, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 22, 0), result);

      // Overlap across midnight from non-working time
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 5, 0), Duration.getInstance(-6, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 22, 30), result);

      // 1 full working day, from end of day
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 0, 0), Duration.getInstance(-20, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 0, 0), result);

      // 1 full working day with offset
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 1, 0), Duration.getInstance(-20, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 1, 0), result);

      // 2 full working days
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 0, 0), Duration.getInstance(-40, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 27, 0, 0), result);
   }

   /**
    * Simple tests to exercise the ProjectCalendar.getDate method with a negative duration using a 24x7 calendar.
    */
   @Test public void test247()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar calendar = new ProjectCalendar(file);

      List<LocalTimeRange> ranges = Collections.singletonList(new LocalTimeRange(LocalTime.of(0, 0), LocalTime.of(0, 0)));

      Arrays.stream(DayOfWeek.values()).forEach(d -> calendar.setCalendarDayType(d, DayType.WORKING));

      calendar.addCalendarHours(DayOfWeek.MONDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.TUESDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.WEDNESDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.THURSDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.FRIDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.SATURDAY).addAll(ranges);
      calendar.addCalendarHours(DayOfWeek.SUNDAY).addAll(ranges);

      // Within range
      LocalDateTime result = calendar.getDate(LocalDateTime.of(2024, 2, 28, 4, 0), Duration.getInstance(-2, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 2, 0), result);

      // From end of range
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 0, 0), Duration.getInstance(-2, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 22, 0), result);

      // All range
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 0, 0), Duration.getInstance(-24, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 0, 0), result);

      // Across days
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 8, 0), Duration.getInstance(-24, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 8, 0), result);

      // Across days
      result = calendar.getDate(LocalDateTime.of(2024, 2, 29, 4, 0), Duration.getInstance(-8, TimeUnit.HOURS));
      assertEquals(LocalDateTime.of(2024, 2, 28, 20, 0), result);
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
