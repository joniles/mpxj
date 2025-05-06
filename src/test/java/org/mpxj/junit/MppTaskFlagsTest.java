
package org.mpxj.junit;

import static org.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TaskMode;
import org.mpxj.common.NumberHelper;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests reading task field bit flags from MPP files.
 */
public class MppTaskFlagsTest
{
   /**
    * Test MPP9 saved by Project 2013.
    */
   @Test public void testMpp9FromProject2013() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("taskFlags-mpp9Project2013.mpp"));
      testFlags(mpp);
   }

   /**
    * Test MPP12 saved by Project 2013.
    */
   @Test public void testMpp12FromProject2013() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("taskFlags-mpp12Project2013.mpp"));
      testFlags(mpp);
   }

   /**
    * Test MPP14 saved by Project 2013.
    */
   @Test public void testMpp14FromProject2013() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("taskFlags-mpp14Project2013.mpp"));
      testFlags(mpp);
   }

   /**
    * Test MPP9 saved by Project 2010.
    */
   @Test public void testMpp9FromProject2010() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("taskFlags-mpp9Project2010.mpp"));
      testFlags(mpp);
   }

   /**
    * Test MPP12 saved by Project 2010.
    */
   @Test public void testMpp12FromProject2010() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("taskFlags-mpp12Project2010.mpp"));
      testFlags(mpp);
   }

   /**
    * Test MPP14 saved by Project 2010.
    */
   @Test public void testMpp14FromProject2010() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("taskFlags-mpp14Project2010.mpp"));
      testFlags(mpp);
   }

   /**
    * Common code to test flag values.
    *
    * @param mpp project file to test
    */
   private void testFlags(ProjectFile mpp)
   {
      Task task;

      //
      // Ignore resource calendars
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(1));
      assertEquals("Ignore Resource Calendars: No", task.getName());
      assertFalse(task.getIgnoreResourceCalendar());

      task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals("Ignore Resource Calendars: Yes", task.getName());
      assertTrue(task.getIgnoreResourceCalendar());

      task = mpp.getTaskByUniqueID(Integer.valueOf(3));
      assertEquals("Ignore Resource Calendars: No", task.getName());
      assertFalse(task.getIgnoreResourceCalendar());

      task = mpp.getTaskByUniqueID(Integer.valueOf(4));
      assertEquals("Ignore Resource Calendars: Yes", task.getName());
      assertTrue(task.getIgnoreResourceCalendar());

      //
      // Effort driven
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(7));
      assertEquals("Effort Driven: Yes", task.getName());
      assertTrue(task.getEffortDriven());

      task = mpp.getTaskByUniqueID(Integer.valueOf(8));
      assertEquals("Effort Driven: No", task.getName());
      assertFalse(task.getEffortDriven());

      task = mpp.getTaskByUniqueID(Integer.valueOf(9));
      assertEquals("Effort Driven: Yes", task.getName());
      assertTrue(task.getEffortDriven());

      task = mpp.getTaskByUniqueID(Integer.valueOf(10));
      assertEquals("Effort Driven: No", task.getName());
      assertFalse(task.getEffortDriven());

      //
      // Hide bar
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(12));
      assertEquals("Hide Bar: Yes", task.getName());
      assertTrue(task.getHideBar());

      task = mpp.getTaskByUniqueID(Integer.valueOf(13));
      assertEquals("Hide Bar: No", task.getName());
      assertFalse(task.getHideBar());

      task = mpp.getTaskByUniqueID(Integer.valueOf(14));
      assertEquals("Hide Bar: Yes", task.getName());
      assertTrue(task.getHideBar());

      task = mpp.getTaskByUniqueID(Integer.valueOf(15));
      assertEquals("Hide Bar: No", task.getName());
      assertFalse(task.getHideBar());

      //
      // Level assignments
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(17));
      assertEquals("Level Assignments: Yes", task.getName());
      assertTrue(task.getLevelAssignments());

      task = mpp.getTaskByUniqueID(Integer.valueOf(18));
      assertEquals("Level Assignments: No", task.getName());
      assertFalse(task.getLevelAssignments());

      task = mpp.getTaskByUniqueID(Integer.valueOf(19));
      assertEquals("Level Assignments: Yes", task.getName());
      assertTrue(task.getLevelAssignments());

      task = mpp.getTaskByUniqueID(Integer.valueOf(20));
      assertEquals("Level Assignments: No", task.getName());
      assertFalse(task.getLevelAssignments());

      //
      // Levelling can split
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(22));
      assertEquals("Leveling Can Split: Yes", task.getName());
      assertTrue(task.getLevelingCanSplit());

      task = mpp.getTaskByUniqueID(Integer.valueOf(23));
      assertEquals("Leveling Can Split: No", task.getName());
      assertFalse(task.getLevelingCanSplit());

      task = mpp.getTaskByUniqueID(Integer.valueOf(24));
      assertEquals("Leveling Can Split: Yes", task.getName());
      assertTrue(task.getLevelingCanSplit());

      task = mpp.getTaskByUniqueID(Integer.valueOf(25));
      assertEquals("Leveling Can Split: Yno", task.getName());
      assertFalse(task.getLevelingCanSplit());

      //
      // Marked
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(27));
      assertEquals("Marked: Yes", task.getName());
      assertTrue(task.getMarked());

      task = mpp.getTaskByUniqueID(Integer.valueOf(28));
      assertEquals("Marked: No", task.getName());
      assertFalse(task.getMarked());

      task = mpp.getTaskByUniqueID(Integer.valueOf(29));
      assertEquals("Marked: Yes", task.getName());
      assertTrue(task.getMarked());

      task = mpp.getTaskByUniqueID(Integer.valueOf(30));
      assertEquals("Marked: No", task.getName());
      assertFalse(task.getMarked());

      //
      // Milestone
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(32));
      assertEquals("Milestone: Yes", task.getName());
      assertTrue(task.getMilestone());

      task = mpp.getTaskByUniqueID(Integer.valueOf(33));
      assertEquals("Milestone: No", task.getName());
      assertFalse(task.getMilestone());

      task = mpp.getTaskByUniqueID(Integer.valueOf(34));
      assertEquals("Milestone: Yes", task.getName());
      assertTrue(task.getMilestone());

      task = mpp.getTaskByUniqueID(Integer.valueOf(35));
      assertEquals("Milestone: No", task.getName());
      assertFalse(task.getMilestone());

      //
      // Rollup
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(37));
      assertEquals("Rollup: Yes", task.getName());
      assertTrue(task.getRollup());

      task = mpp.getTaskByUniqueID(Integer.valueOf(38));
      assertEquals("Rollup: No", task.getName());
      assertFalse(task.getRollup());

      task = mpp.getTaskByUniqueID(Integer.valueOf(39));
      assertEquals("Rollup: Yes", task.getName());
      assertTrue(task.getRollup());

      task = mpp.getTaskByUniqueID(Integer.valueOf(40));
      assertEquals("Rollup: No", task.getName());
      assertFalse(task.getRollup());

      //
      // Flags
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(42));
      assertEquals("Flag1", task.getName());
      testFlag(task, 1);

      task = mpp.getTaskByUniqueID(Integer.valueOf(43));
      assertEquals("Flag2", task.getName());
      testFlag(task, 2);

      task = mpp.getTaskByUniqueID(Integer.valueOf(44));
      assertEquals("Flag3", task.getName());
      testFlag(task, 3);

      task = mpp.getTaskByUniqueID(Integer.valueOf(45));
      assertEquals("Flag4", task.getName());
      testFlag(task, 4);

      task = mpp.getTaskByUniqueID(Integer.valueOf(46));
      assertEquals("Flag5", task.getName());
      testFlag(task, 5);

      task = mpp.getTaskByUniqueID(Integer.valueOf(47));
      assertEquals("Flag6", task.getName());
      testFlag(task, 6);

      task = mpp.getTaskByUniqueID(Integer.valueOf(48));
      assertEquals("Flag7", task.getName());
      testFlag(task, 7);

      task = mpp.getTaskByUniqueID(Integer.valueOf(49));
      assertEquals("Flag8", task.getName());
      testFlag(task, 8);

      task = mpp.getTaskByUniqueID(Integer.valueOf(50));
      assertEquals("Flag9", task.getName());
      testFlag(task, 9);

      task = mpp.getTaskByUniqueID(Integer.valueOf(51));
      assertEquals("Flag10", task.getName());
      testFlag(task, 10);

      task = mpp.getTaskByUniqueID(Integer.valueOf(52));
      assertEquals("Flag11", task.getName());
      testFlag(task, 11);

      task = mpp.getTaskByUniqueID(Integer.valueOf(53));
      assertEquals("Flag12", task.getName());
      testFlag(task, 12);

      task = mpp.getTaskByUniqueID(Integer.valueOf(54));
      assertEquals("Flag13", task.getName());
      testFlag(task, 13);

      task = mpp.getTaskByUniqueID(Integer.valueOf(55));
      assertEquals("Flag14", task.getName());
      testFlag(task, 14);

      task = mpp.getTaskByUniqueID(Integer.valueOf(56));
      assertEquals("Flag15", task.getName());
      testFlag(task, 15);

      task = mpp.getTaskByUniqueID(Integer.valueOf(57));
      assertEquals("Flag16", task.getName());
      testFlag(task, 16);

      task = mpp.getTaskByUniqueID(Integer.valueOf(58));
      assertEquals("Flag17", task.getName());
      testFlag(task, 17);

      task = mpp.getTaskByUniqueID(Integer.valueOf(59));
      assertEquals("Flag18", task.getName());
      testFlag(task, 18);

      task = mpp.getTaskByUniqueID(Integer.valueOf(60));
      assertEquals("Flag19", task.getName());
      testFlag(task, 19);

      task = mpp.getTaskByUniqueID(Integer.valueOf(61));
      assertEquals("Flag20", task.getName());
      testFlag(task, 20);

      if (NumberHelper.getInt(mpp.getProjectProperties().getMppFileType()) == 14)
      {
         //
         // Active
         //
         task = mpp.getTaskByUniqueID(Integer.valueOf(63));
         assertEquals("Active: On", task.getName());
         assertTrue(task.getActive());

         task = mpp.getTaskByUniqueID(Integer.valueOf(64));
         assertEquals("Active: Off", task.getName());
         assertFalse(task.getActive());

         task = mpp.getTaskByUniqueID(Integer.valueOf(65));
         assertEquals("Active: On", task.getName());
         assertTrue(task.getActive());

         task = mpp.getTaskByUniqueID(Integer.valueOf(66));
         assertEquals("Active: Off", task.getName());
         assertFalse(task.getActive());

         //
         // Task Mode
         //
         task = mpp.getTaskByUniqueID(Integer.valueOf(68));
         assertEquals("Mode: Auto", task.getName());
         assertEquals(TaskMode.AUTO_SCHEDULED, task.getTaskMode());

         task = mpp.getTaskByUniqueID(Integer.valueOf(69));
         assertEquals("Mode: Manual", task.getName());
         assertEquals(TaskMode.MANUALLY_SCHEDULED, task.getTaskMode());

         task = mpp.getTaskByUniqueID(Integer.valueOf(70));
         assertEquals("Mode: Auto", task.getName());
         assertEquals(TaskMode.AUTO_SCHEDULED, task.getTaskMode());

         task = mpp.getTaskByUniqueID(Integer.valueOf(71));
         assertEquals("Mode: Manual", task.getName());
         assertEquals(TaskMode.MANUALLY_SCHEDULED, task.getTaskMode());
      }
   }

   /**
    * Test all 20 custom field flags.
    *
    * @param task task to be tested
    * @param flag flag index to test
    */
   private void testFlag(Task task, int flag)
   {
      for (int loop = 0; loop < 20; loop++)
      {
         assertBooleanEquals("Flag" + (loop + 1), (flag == loop + 1), task.getFlag(loop + 1));
      }
   }
}