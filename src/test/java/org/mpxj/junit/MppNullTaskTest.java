/*
 * file:       MppNullTaskTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       05/11/2008
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
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppNullTaskTest
{

   /**
    * Test null task data read from an MPP9 file.
    */
   @Test public void testMpp9NullTasks() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp9nulltasks.mpp"));
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9NullTasksFrom12() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp9nulltasks-from12.mpp"));
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9NullTasksFrom14() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp9nulltasks-from14.mpp"));
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPP12 file.
    */
   @Test public void testMpp12NullTasks() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp12nulltasks.mpp"));
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12NullTasksFrom14() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp12nulltasks-from14.mpp"));
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPP14 file.
    */
   @Test public void testMpp14NullTasks() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp14nulltasks.mpp"));
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPD9 file.
    */
   @Test public void testMpd9NullTasks() throws Exception
   {
      ProjectFile project = new MPDFileReader().read(MpxjTestData.filePath("mpp9nulltasks.mpd"));
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MSPDI file.
    */
   @Test public void testMspdiNullTasks() throws Exception
   {
      ProjectFile project = new MSPDIReader().read(MpxjTestData.filePath("mspdinulltasks.xml"));
      testNullTasks(project);
   }

   /**
    * Tests a project containing null tasks.
    *
    * @param project The ProjectFile instance being tested.
    */
   private void testNullTasks(ProjectFile project)
   {
      Task task = project.getTaskByID(Integer.valueOf(1));
      assertNotNull(task);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getOutlineLevel().intValue());
      assertEquals("1", task.getOutlineNumber());
      assertEquals("1", task.getWBS());
      assertTrue(task.getSummary());

      task = project.getTaskByID(Integer.valueOf(2));
      if (task != null)
      {
         assertNull(task.getName());
         assertNull(task.getOutlineLevel());
         assertNull(task.getOutlineNumber());
         assertNull(task.getWBS());
         assertFalse(task.getSummary());
      }

      task = project.getTaskByID(Integer.valueOf(3));
      assertNotNull(task);
      assertEquals("Task 2", task.getName());
      assertEquals(2, task.getOutlineLevel().intValue());
      assertEquals("1.1", task.getOutlineNumber());
      assertEquals("1.1", task.getWBS());
      assertFalse(task.getSummary());

      task = project.getTaskByID(Integer.valueOf(4));
      assertNotNull(task);
      assertEquals("Task 3", task.getName());
      assertEquals(2, task.getOutlineLevel().intValue());
      assertEquals("1.2", task.getOutlineNumber());
      assertEquals("1.2", task.getWBS());
      assertFalse(task.getSummary());

      task = project.getTaskByID(Integer.valueOf(5));
      if (task != null)
      {
         assertNull(task.getName());
         assertNull(task.getOutlineLevel());
         assertNull(task.getOutlineNumber());
         assertNull(task.getWBS());
         assertFalse(task.getSummary());
      }

      task = project.getTaskByID(Integer.valueOf(6));
      assertNotNull(task);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getOutlineLevel().intValue());
      assertEquals("2", task.getOutlineNumber());
      assertEquals("2", task.getWBS());
      assertTrue(task.getSummary());

      task = project.getTaskByID(Integer.valueOf(7));
      if (task != null)
      {
         assertNull(task.getName());
         assertNull(task.getOutlineLevel());
         assertNull(task.getOutlineNumber());
         assertNull(task.getWBS());
         assertFalse(task.getSummary());
      }

      task = project.getTaskByID(Integer.valueOf(8));
      assertNotNull(task);
      assertEquals("Task 5", task.getName());
      assertEquals(2, task.getOutlineLevel().intValue());
      assertEquals("2.1", task.getOutlineNumber());
      assertEquals("2.1", task.getWBS());
      assertFalse(task.getSummary());

   }
}
