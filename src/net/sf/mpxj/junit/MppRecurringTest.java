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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringTask;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;

/**
 * Tests to exercise MPP file read functionality for various versions of MPP
 * file.
 */
public class MppRecurringTest extends MPXJTestCase
{
   /**
    * Test recurring task data read from an MPX file.
    * 
    * @throws Exception
    */
   public void testMpxRecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPXReader().read(m_basedir + "/mpxrecurring.mpx");
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP8 file.
    * 
    * @throws Exception
    */
   public void testMpp8RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp8recurring.mpp");
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9recurring.mpp");
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12recurring.mpp");
      testRecurringTasks(mpp);
   }

   /**
    * Test recurring task data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14RecurringTasks() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14recurring.mpp");
      testRecurringTasks(mpp);
   }

   /**
    * Tests recurring tasks.
    * 
    * @param mpp The ProjectFile being tested.
    * @throws Exception
    */
   private void testRecurringTasks(ProjectFile mpp)
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      DateFormat dm = new SimpleDateFormat("dd/MM");

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
      assertEquals(1, rt.getDailyFrequency().intValue());
      assertFalse(rt.getDailyWorkday());
      assertEquals("15/06/2008", df.format(rt.getStartDate()));
      assertEquals("16/06/2008", df.format(rt.getFinishDate()));
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
      assertEquals(2, rt.getDailyFrequency().intValue());
      assertTrue(rt.getDailyWorkday());
      assertEquals("15/06/2008", df.format(rt.getStartDate()));
      assertEquals("23/06/2008", df.format(rt.getFinishDate()));
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
      assertEquals(1, rt.getWeeklyFrequency().intValue());
      assertEquals(0x2A, rt.getWeeklyDays().intValue());
      assertEquals("15/06/2008", df.format(rt.getStartDate()));
      assertEquals("20/06/2008", df.format(rt.getFinishDate()));
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
      assertFalse(rt.getMonthlyRelative());
      assertEquals(15, rt.getMonthlyAbsoluteDay().intValue());
      assertEquals(1, rt.getMonthlyAbsoluteFrequency().intValue());
      assertEquals("15/06/2008", df.format(rt.getStartDate()));
      assertEquals("15/08/2008", df.format(rt.getFinishDate()));
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
      assertTrue(rt.getMonthlyRelative());
      assertEquals(3, rt.getMonthlyRelativeOrdinal().intValue());
      assertEquals(Day.MONDAY, rt.getMonthlyRelativeDay());
      assertEquals(1, rt.getMonthlyRelativeFrequency().intValue());
      assertEquals("15/06/2008", df.format(rt.getStartDate()));
      assertEquals("18/08/2008", df.format(rt.getFinishDate()));
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
      assertTrue(rt.getYearlyAbsolute());
      assertEquals("15/06", dm.format(rt.getYearlyAbsoluteDate()));
      assertEquals("15/06/2008", df.format(rt.getStartDate()));
      assertEquals("15/06/2010", df.format(rt.getFinishDate()));
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
      assertFalse(rt.getYearlyAbsolute());
      assertEquals(3, rt.getYearlyRelativeOrdinal().intValue());
      assertEquals(Day.MONDAY, rt.getYearlyRelativeDay());
      assertEquals(6, rt.getYearlyRelativeMonth().intValue());
      assertEquals("15/06/2008", df.format(rt.getStartDate()));
      assertEquals("21/06/2010", df.format(rt.getFinishDate()));
      assertEquals(3, rt.getOccurrences().intValue());
      assertFalse(rt.getUseEndDate());
   }
}
