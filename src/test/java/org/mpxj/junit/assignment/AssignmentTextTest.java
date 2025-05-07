/*
 * file:       AssignmentTextTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       2018-10-18
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

package org.mpxj.junit.assignment;

import static org.junit.Assert.*;

import java.io.File;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure assignment text fields are read correctly.
 */
public class AssignmentTextTest
{
   /**
    * Test to validate calendars in files saved by different versions of MS Project.
    */
   @Test public void testAssignments() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/assignment-text", "assignment-text"))
      {
         testAssignmentText(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testAssignmentText(File file) throws MPXJException
   {
      int maxIndex = 30;
      ProjectFile project = new UniversalProjectReader().read(file);
      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Task " + index, task.getName());
         testAssignmentText(file, task, index, maxIndex);
      }
   }

   /**
    * Test the text field values for a task.
    *
    * @param file parent file
    * @param task task
    * @param textFieldIndex index of text field expected to be populated
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testAssignmentText(File file, Task task, int textFieldIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         String expectedValue = index == textFieldIndex ? "Text" + index : null;
         String actualValue = task.getResourceAssignments().get(0).getText(index);
         assertEquals(file.getName() + " Text" + index, expectedValue, actualValue);
      }
   }
}
