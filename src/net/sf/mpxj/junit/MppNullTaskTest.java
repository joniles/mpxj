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

package net.sf.mpxj.junit;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppNullTaskTest extends MPXJTestCase
{

   /**
    * Test null task data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9NullTasks() throws Exception
   {
      ProjectFile project = new MPPReader().read(m_basedir + "/mpp9nulltasks.mpp");
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPD9 file.
    * 
    * @throws Exception
    */
   public void testMpd9NullTasks() throws Exception
   {
      try
      {
         ProjectFile project = new MPDDatabaseReader().read(m_basedir + "/mpp9nulltasks.mpd");
         testNullTasks(project);
      }

      catch (Exception ex)
      {
         //
         // JDBC not supported in IKVM
         //
         if (!m_ikvm)
         {
            throw ex;
         }
      }
   }

   /**
    * Test null task data read from an MSPDI file.
    * 
    * @throws Exception
    */
   public void testMspdiNullTasks() throws Exception
   {
      ProjectFile project = new MSPDIReader().read(m_basedir + "/mspdinulltasks.xml");
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12NullTasks() throws Exception
   {
      ProjectFile project = new MPPReader().read(m_basedir + "/mpp12nulltasks.mpp");
      testNullTasks(project);
   }

   /**
    * Test null task data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14NullTasks() throws Exception
   {
      ProjectFile project = new MPPReader().read(m_basedir + "/mpp14nulltasks.mpp");
      testNullTasks(project);
   }

   /**
    * Tests a project containing null tasks.
    * 
    * @param project The ProjectFile instance being tested.
    * @throws Exception
    */
   private void testNullTasks(ProjectFile project)
   {
      Task task = project.getTaskByID(Integer.valueOf(1));
      assertNotNull(task);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getOutlineLevel().intValue());
      assertEquals("1", task.getOutlineNumber());
      assertEquals("1", task.getWBS());
      assertEquals(true, task.getSummary());

      task = project.getTaskByID(Integer.valueOf(2));
      if (task != null)
      {
         assertEquals(null, task.getName());
         assertEquals(null, task.getOutlineLevel());
         assertEquals(null, task.getOutlineNumber());
         assertEquals(null, task.getWBS());
         assertEquals(false, task.getSummary());
      }

      task = project.getTaskByID(Integer.valueOf(3));
      assertNotNull(task);
      assertEquals("Task 2", task.getName());
      assertEquals(2, task.getOutlineLevel().intValue());
      assertEquals("1.1", task.getOutlineNumber());
      assertEquals("1.1", task.getWBS());
      assertEquals(false, task.getSummary());

      task = project.getTaskByID(Integer.valueOf(4));
      assertNotNull(task);
      assertEquals("Task 3", task.getName());
      assertEquals(2, task.getOutlineLevel().intValue());
      assertEquals("1.2", task.getOutlineNumber());
      assertEquals("1.2", task.getWBS());
      assertEquals(false, task.getSummary());

      task = project.getTaskByID(Integer.valueOf(5));
      if (task != null)
      {
         assertEquals(null, task.getName());
         assertEquals(null, task.getOutlineLevel());
         assertEquals(null, task.getOutlineNumber());
         assertEquals(null, task.getWBS());
         assertEquals(false, task.getSummary());
      }

      task = project.getTaskByID(Integer.valueOf(6));
      assertNotNull(task);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getOutlineLevel().intValue());
      assertEquals("2", task.getOutlineNumber());
      assertEquals("2", task.getWBS());
      assertEquals(true, task.getSummary());

      task = project.getTaskByID(Integer.valueOf(7));
      if (task != null)
      {
         assertEquals(null, task.getName());
         assertEquals(null, task.getOutlineLevel());
         assertEquals(null, task.getOutlineNumber());
         assertEquals(null, task.getWBS());
         assertEquals(false, task.getSummary());
      }

      task = project.getTaskByID(Integer.valueOf(8));
      assertNotNull(task);
      assertEquals("Task 5", task.getName());
      assertEquals(2, task.getOutlineLevel().intValue());
      assertEquals("2.1", task.getOutlineNumber());
      assertEquals("2.1", task.getWBS());
      assertEquals(false, task.getSummary());

   }
}
