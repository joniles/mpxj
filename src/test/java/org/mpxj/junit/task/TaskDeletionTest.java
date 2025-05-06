/*
 * file:       TaskDeletionTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       11/11/2014
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

package org.mpxj.junit.task;

import static org.junit.Assert.*;

import java.io.File;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure deleted tasks, both blank and normal, are handled correctly.
 *
 * Test Data Generation:
 * 1. Create an MPP file with the following tasks:
 *       1. blank
 *       2. T1
 *       3. blank
 *       4. T2
 *       5. T3
 *       6. T4
 *       7. blank
 *       8. blank
 *       9. T5
 * 2. Save this file in the required format
 * 3. Copy the file
 * 4. Open the copy and delete tasks with IDs 1,3,5,7
 * 5. Save the file
 */
public class TaskDeletionTest
{
   /**
    * Ensure that we can see the correct pre-deletion tasks.
    */
   @Test public void testTasksPreDeletion() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("task/task-deletion", "task-deletion1"))
      {
         testTaskDeletion(file, TASK_DELETION1);
      }
   }

   /**
    * Ensure that we can see the correct post-deletion tasks.
    */
   @Test public void testTasksPostDeletion() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("task/task-deletion", "task-deletion2"))
      {
         testTaskDeletion(file, TASK_DELETION2);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    * @param expectedNames expected task names
    */
   private void testTaskDeletion(File file, String[] expectedNames) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      assertEquals(expectedNames.length + 1, project.getTasks().size());
      for (int index = 0; index < expectedNames.length; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index + 1));
         assertNotNull(file.getName() + " Task " + (index + 1), task);
         assertEquals(file.getName() + " Task " + task.getID(), expectedNames[index], task.getName());
      }
   }
   private static final String[] TASK_DELETION1 =
   {
      null,
      "T1",
      null,
      "T2",
      "T3",
      "T4",
      null,
      null,
      "T5"
   };

   private static final String[] TASK_DELETION2 =
   {
      "T1",
      "T2",
      "T4",
      null,
      "T5"
   };
}
