/*
 * file:       TaskTextTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       10/11/2014
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

import org.mpxj.junit.ProjectUtility;
import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure task custom text fields are correctly handled.
 */
public class TaskTextTest
{
   /**
    * Test to validate the custom text fields in files saved by different versions of MS Project.
    */
   @Test public void testTaskText() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-text", "task-text"))
      {
         testTaskText(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskText(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      int maxIndex = ProjectUtility.projectIs(project, "MPX") ? 10 : 30;

      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Text" + index, task.getName());
         testTaskText(file, task, index, maxIndex);
      }
   }

   /**
    * Test the text field values for a task.
    *
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testTaskText(File file, Task task, int testIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         String expectedValue = testIndex == index ? Integer.toString(index) : null;
         String actualValue = task.getText(index);

         assertEquals(file.getName() + " Text" + index, expectedValue, actualValue);
      }
   }
}
