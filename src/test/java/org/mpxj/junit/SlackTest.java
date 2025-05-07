/*
 * file:       SlackTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       1-April-2006
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
import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * The tests contained in this class exercise the slack duration functionality.
 */
public class SlackTest
{
   /**
    * Exercise slack duration functionality.
    */
   @Test public void testSlack() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("slack9.mpp"));
      Task task = mpp.getTaskByID(Integer.valueOf(1));
      assertEquals("Task 1", task.getName());
      assertEquals(Duration.getInstance(8, TimeUnit.HOURS), task.getDuration());
      assertEquals(Duration.getInstance(40, TimeUnit.HOURS), task.getStartSlack());
      assertEquals(Duration.getInstance(40, TimeUnit.HOURS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.HOURS), task.getFreeSlack());
      assertEquals(Duration.getInstance(40, TimeUnit.HOURS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(2));
      assertEquals("Task 2", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(3));
      assertEquals("Task 3", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(10, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(10, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(10, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(4));
      assertEquals("Task 4", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(5));
      assertEquals("Milestone 1", task.getName());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(6));
      assertEquals("Task 5", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(7));
      assertEquals("Task 6", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(8));
      assertEquals("Task 7", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(9));
      assertEquals("Task 8", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(4, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(4, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(4, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(4, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(10));
      assertEquals("Milestone 2", task.getName());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(-1, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(11));
      assertEquals("Task 9", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(12));
      assertEquals("Task 10", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(13));
      assertEquals("Task 11", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(14));
      assertEquals("Task 12", task.getName());
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(6, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(6, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(6, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(6, TimeUnit.DAYS), task.getTotalSlack());

      task = mpp.getTaskByID(Integer.valueOf(15));
      assertEquals("Milestone 3", task.getName());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getDuration());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getStartSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFinishSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getFreeSlack());
      assertEquals(Duration.getInstance(0, TimeUnit.DAYS), task.getTotalSlack());

   }
}
