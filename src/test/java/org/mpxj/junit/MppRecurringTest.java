/*
 * file:       MppRecurringTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       15/06/2008
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

import org.junit.Test;

import java.time.DayOfWeek;
import org.mpxj.ProjectFile;
import org.mpxj.RecurrenceType;
import org.mpxj.RecurringTask;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpx.MPXReader;

/**
 * Tests to exercise MPP file read functionality for various versions of MPP
 * file.
 */
public class MppRecurringTest
{
   /**
    * Test recurring task data read from an MPX file.
    */
   @Test public void testMpxRecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPXReader().read(MpxjTestData.filePath("mpxrecurring.mpx"));
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP8 file.
    */
   @Test public void testMpp8RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp8recurring.mpp"));
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP9 file.
    */
   @Test public void testMpp9RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9recurring.mpp"));
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9RecurringTasksFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9recurring-from12.mpp"));
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9RecurringTasksFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9recurring-from14.mpp"));
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP12 file.
    */
   @Test public void testMpp12RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12recurring.mpp"));
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12RecurringTasksFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12recurring-from14.mpp"));
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP14 file.
    */
   @Test public void testMpp14RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14recurring.mpp"));
      testRecurringTasks(mpp);
   }

   /**
    * Tests recurring tasks.
    *
    * @param mpp The ProjectFile being tested.
    */
   private void testRecurringTasks(ProjectFile mpp)
   {
      //
      // Task 1
      //
      Task task = mpp.getTaskByID(Integer.valueOf(1));
      assertEquals("Daily Every Day", task.getName());
      assertTrue(task.getRecurring());
      RecurringTask rt = task.getRecurringTask();
      assertEquals(1, (int) rt.getDuration().getDuration());
      assertEquals(TimeUnit.HOURS, rt.getDuration().getUnits());
      assertEquals(RecurrenceType.DAILY, rt.getRecurrenceType());
      assertEquals(1, rt.getFrequency().intValue());
      assertFalse(rt.getWorkingDaysOnly());
      assertEquals(LocalDate.of(2008, 6, 15), rt.getStartDate());
      assertEquals(LocalDate.of(2008, 6, 16), rt.getFinishDate());
      assertEquals(2, rt.getOccurrences().intValue());
      assertTrue(rt.getUseEndDate());

      //
      // Task 4
      //
      task = mpp.getTaskByID(Integer.valueOf(4));
      assertEquals("Daily Every Other Workday", task.getName());
      assertTrue(task.getRecurring());
      rt = task.getRecurringTask();
      assertEquals(1, (int) rt.getDuration().getDuration());
      assertEquals(TimeUnit.HOURS, rt.getDuration().getUnits());
      assertEquals(RecurrenceType.DAILY, rt.getRecurrenceType());
      assertEquals(2, rt.getFrequency().intValue());
      assertTrue(rt.getWorkingDaysOnly());
      assertEquals(LocalDate.of(2008, 6, 15), rt.getStartDate());
      assertEquals(LocalDate.of(2008, 6, 23), rt.getFinishDate());
      assertEquals(3, rt.getOccurrences().intValue());
      assertFalse(rt.getUseEndDate());

      //
      // Task 8
      //
      task = mpp.getTaskByID(Integer.valueOf(8));
      assertEquals("Weekly Every Week", task.getName());
      assertTrue(task.getRecurring());
      rt = task.getRecurringTask();
      assertEquals(1, (int) rt.getDuration().getDuration());
      assertEquals(TimeUnit.HOURS, rt.getDuration().getUnits());
      assertEquals(RecurrenceType.WEEKLY, rt.getRecurrenceType());
      assertEquals(1, rt.getFrequency().intValue());
      assertEquals(Boolean.FALSE, Boolean.valueOf(rt.getWeeklyDay(DayOfWeek.SUNDAY)));
      assertEquals(Boolean.TRUE, Boolean.valueOf(rt.getWeeklyDay(DayOfWeek.MONDAY)));
      assertEquals(Boolean.FALSE, Boolean.valueOf(rt.getWeeklyDay(DayOfWeek.TUESDAY)));
      assertEquals(Boolean.TRUE, Boolean.valueOf(rt.getWeeklyDay(DayOfWeek.WEDNESDAY)));
      assertEquals(Boolean.FALSE, Boolean.valueOf(rt.getWeeklyDay(DayOfWeek.THURSDAY)));
      assertEquals(Boolean.TRUE, Boolean.valueOf(rt.getWeeklyDay(DayOfWeek.FRIDAY)));
      assertEquals(Boolean.FALSE, Boolean.valueOf(rt.getWeeklyDay(DayOfWeek.SATURDAY)));
      assertEquals(LocalDate.of(2008, 6, 15), rt.getStartDate());
      assertEquals(LocalDate.of(2008, 6, 20), rt.getFinishDate());
      assertEquals(3, rt.getOccurrences().intValue());
      assertFalse(rt.getUseEndDate());

      //
      // Task 12
      //
      task = mpp.getTaskByID(Integer.valueOf(12));
      assertEquals("Monthly 15th of Every Month", task.getName());
      assertTrue(task.getRecurring());
      rt = task.getRecurringTask();
      assertEquals(1, (int) rt.getDuration().getDuration());
      assertEquals(TimeUnit.HOURS, rt.getDuration().getUnits());
      assertEquals(RecurrenceType.MONTHLY, rt.getRecurrenceType());
      assertFalse(rt.getRelative());
      assertEquals(15, rt.getDayNumber().intValue());
      assertEquals(1, rt.getFrequency().intValue());
      assertEquals(LocalDate.of(2008, 6, 15), rt.getStartDate());
      assertEquals(LocalDate.of(2008, 8, 15), rt.getFinishDate());
      assertEquals(3, rt.getOccurrences().intValue());
      assertFalse(rt.getUseEndDate());

      //
      // Task 16
      //
      task = mpp.getTaskByID(Integer.valueOf(16));
      assertEquals("Monthly Third Monday of Every Month", task.getName());
      assertTrue(task.getRecurring());
      rt = task.getRecurringTask();
      assertEquals(1, (int) rt.getDuration().getDuration());
      assertEquals(TimeUnit.HOURS, rt.getDuration().getUnits());
      assertEquals(RecurrenceType.MONTHLY, rt.getRecurrenceType());
      assertTrue(rt.getRelative());
      assertEquals(3, rt.getDayNumber().intValue());
      assertEquals(DayOfWeek.MONDAY, rt.getDayOfWeek());
      assertEquals(1, rt.getFrequency().intValue());
      assertEquals(LocalDate.of(2008, 6, 15), rt.getStartDate());
      assertEquals(LocalDate.of(2008, 8, 18), rt.getFinishDate());
      assertEquals(3, rt.getOccurrences().intValue());
      assertFalse(rt.getUseEndDate());

      //
      // Task 20
      //
      task = mpp.getTaskByID(Integer.valueOf(20));
      assertEquals("Yearly 15th June", task.getName());
      assertTrue(task.getRecurring());
      rt = task.getRecurringTask();
      assertEquals(1, (int) rt.getDuration().getDuration());
      assertEquals(TimeUnit.HOURS, rt.getDuration().getUnits());
      assertEquals(RecurrenceType.YEARLY, rt.getRecurrenceType());
      assertFalse(rt.getRelative());
      assertEquals(15, rt.getDayNumber().intValue());
      assertEquals(6, rt.getMonthNumber().intValue());
      assertEquals(LocalDate.of(2008, 6, 15), rt.getStartDate());
      assertEquals(LocalDate.of(2010, 6, 15), rt.getFinishDate());
      assertEquals(3, rt.getOccurrences().intValue());
      assertFalse(rt.getUseEndDate());

      //
      // Task 24
      //
      task = mpp.getTaskByID(Integer.valueOf(24));
      assertEquals("Yearly Third Monday of June", task.getName());
      assertTrue(task.getRecurring());
      rt = task.getRecurringTask();
      assertEquals(1, (int) rt.getDuration().getDuration());
      assertEquals(TimeUnit.HOURS, rt.getDuration().getUnits());
      assertEquals(RecurrenceType.YEARLY, rt.getRecurrenceType());
      assertTrue(rt.getRelative());
      assertEquals(3, rt.getDayNumber().intValue());
      assertEquals(DayOfWeek.MONDAY, rt.getDayOfWeek());
      assertEquals(6, rt.getMonthNumber().intValue());
      assertEquals(LocalDate.of(2008, 6, 15), rt.getStartDate());
      assertEquals(LocalDate.of(2010, 6, 21), rt.getFinishDate());
      assertEquals(3, rt.getOccurrences().intValue());
      assertFalse(rt.getUseEndDate());
   }
}
