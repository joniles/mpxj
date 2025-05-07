/*
 * file:       TaskContainerTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       12/11/2015
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

package org.mpxj.junit.project;

import static org.junit.Assert.*;

import org.junit.Test;

import org.mpxj.ProjectFile;
import org.mpxj.Task;

/**
 * Test to exercise TaskContainer functionality.
 */
public class TaskContainerTest
{
   /**
    * Test fix for SourceForge issue 277.
    */
   @Test public void testSynchronizeTaskIDToHierarchy()
   {
      ProjectFile file = new ProjectFile();
      file.getProjectConfig().setAutoTaskID(false);

      Task task1 = file.addTask();
      Task task2 = file.addTask();
      Task task3 = task2.addTask();
      Task task4 = task3.addTask();

      assertNull(task1.getID());
      assertNull(task2.getID());
      assertNull(task3.getID());
      assertNull(task4.getID());

      assertEquals(4, file.getTasks().size());

      file.getTasks().synchronizeTaskIDToHierarchy();

      assertEquals(4, file.getTasks().size());

      assertEquals(Integer.valueOf(1), task1.getID());
      assertEquals(Integer.valueOf(2), task2.getID());
      assertEquals(Integer.valueOf(3), task3.getID());
      assertEquals(Integer.valueOf(4), task4.getID());

      assertEquals(task1, file.getChildTasks().get(0));
      assertEquals(task2, file.getChildTasks().get(1));
      assertEquals(task3, task2.getChildTasks().get(0));
      assertEquals(task4, task3.getChildTasks().get(0));
   }
}
