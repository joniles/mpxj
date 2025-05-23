/*
 * file:       TaskNumbersTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       16/10/2014
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
import org.mpxj.common.NumberHelper;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure task custom numbers are correctly handled.
 */
public class TaskNumbersTest
{
   /**
    * Test to validate the custom numbers in files saved by different versions of MS Project.
    */
   @Test public void testTaskNumbers() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-numbers", "task-numbers"))
      {
         testTaskNumbers(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskNumbers(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      int maxIndex = ProjectUtility.projectIs(project, "MPX") ? 5 : 20;

      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Number" + index, task.getName());
         testTaskNumbers(file, task, index, maxIndex);
      }
   }

   /**
    * Test the number values for a task.
    *
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testTaskNumbers(File file, Task task, int testIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         int expectedValue = testIndex == index ? index : 0;
         int actualValue = NumberHelper.getInt(task.getNumber(index));

         assertEquals(file.getName() + " Number" + index, expectedValue, actualValue);
      }
   }
}
