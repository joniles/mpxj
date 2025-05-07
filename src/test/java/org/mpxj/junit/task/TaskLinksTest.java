/*
 * file:       TaskLinksTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       20/10/2014
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
import java.util.List;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.common.NumberHelper;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure task links are correctly handled.
 */
public class TaskLinksTest
{
   /**
    * Test to validate links between tasks in files saved by different versions of MS Project.
    */
   @Test public void testTaskLinks() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-links", "task-links"))
      {
         testTaskLinks(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskLinks(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);

      //
      // Test different durations and time units
      //
      testTaskLinks(project, 1, 2, "Task 1", "Task 2", RelationType.FINISH_START, 0, TimeUnit.DAYS);
      testTaskLinks(project, 3, 4, "Task 1", "Task 2", RelationType.FINISH_START, 1, TimeUnit.DAYS);
      testTaskLinks(project, 5, 6, "Task 1", "Task 2", RelationType.FINISH_START, 2, TimeUnit.DAYS);
      testTaskLinks(project, 7, 8, "Task 1", "Task 2", RelationType.FINISH_START, 1, TimeUnit.WEEKS);
      testTaskLinks(project, 9, 10, "Task 1", "Task 2", RelationType.FINISH_START, 2, TimeUnit.WEEKS);

      //
      // Test different relation types
      //
      testTaskLinks(project, 11, 12, "Task 1", "Task 2", RelationType.START_FINISH, 2, TimeUnit.DAYS);
      testTaskLinks(project, 13, 14, "Task 1", "Task 2", RelationType.START_START, 2, TimeUnit.DAYS);
      testTaskLinks(project, 15, 16, "Task 1", "Task 2", RelationType.FINISH_FINISH, 2, TimeUnit.DAYS);
   }

   /**
    * Test a relationship between two tasks.
    *
    * @param project parent project
    * @param taskID1 first task
    * @param taskID2 second task
    * @param name1 expected task name 1
    * @param name2 expected task name 1
    * @param type expected relation type
    * @param lagDuration expected lag duration
    * @param lagUnits expected lag units
    */
   private void testTaskLinks(ProjectFile project, int taskID1, int taskID2, String name1, String name2, RelationType type, double lagDuration, TimeUnit lagUnits)
   {
      Task task1 = project.getTaskByID(Integer.valueOf(taskID1));
      Task task2 = project.getTaskByID(Integer.valueOf(taskID2));

      assertEquals(name1, task1.getName());
      assertEquals(name2, task2.getName());

      List<Relation> relations = task2.getPredecessors();
      assertEquals(1, relations.size());
      Relation relation = relations.get(0);
      assertEquals(task2, relation.getSuccessorTask());
      assertEquals(task1, relation.getPredecessorTask());
      assertEquals(type, relation.getType());
      assertEquals(lagUnits, relation.getLag().getUnits());
      assertTrue(NumberHelper.equals(lagDuration, relation.getLag().getDuration(), 0.0001));
   }
}
