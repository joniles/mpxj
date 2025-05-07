/*
 * file:       TaskOutlineCodeTest.java
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

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure task custom outline codes are correctly handled.
 */
public class TaskOutlineCodesTest
{
   /**
    * Test to validate the custom outline codes in files saved by different versions of MS Project.
    */
   @Test public void testTaskOutlineCodes() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-outlinecodes", "task-outlinecodes"))
      {
         testTaskOutlineCodes(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskOutlineCodes(File file) throws MPXJException
   {
      int maxIndex = 10;
      ProjectFile project = new UniversalProjectReader().read(file);
      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Outline Code" + index, task.getName());
         testFlatTaskOutlineCodes(file, task, index, maxIndex);
      }

      int taskOffset = 10;
      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index + taskOffset));
         assertEquals("Outline Code" + index, task.getName());
         testHierarchicalTaskOutlineCodes(file, task, index, maxIndex);
      }
   }

   /**
    * Test flat outline code values for a task.
    *
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testFlatTaskOutlineCodes(File file, Task task, int testIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         String expectedValue = testIndex == index ? "OC" + index + "A" : null;
         String actualValue = task.getOutlineCode(index);

         assertEquals(file.getName() + " Outline Code" + index, expectedValue, actualValue);
      }
   }

   /**
    * Test hierarchical outline code values for a task.
    *
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testHierarchicalTaskOutlineCodes(File file, Task task, int testIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         String expectedValue = testIndex == index ? "OC" + index + "A.OC" + index + "B" : null;
         String actualValue = task.getOutlineCode(index);

         assertEquals(file.getName() + " Outline Code" + index, expectedValue, actualValue);
      }
   }

}
