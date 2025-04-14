/*
 * file:       DeletedAssignmentTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       20/09/2014
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
import java.util.List;

import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.mpp.MPPReader;

/**
 * Tests to ensure delete resource assignments are correctly handled.
 */
public class DeletedAssignmentTest
{
   /**
    * Test to exercise the test case provided for SourceForge bug #248.
    * <a href="https://sourceforge.net/p/mpxj/bugs/248/">https://sourceforge.net/p/mpxj/bugs/248/</a>
    */
   @Test public void testSourceForge248() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("assignment/assignment-deletion/sf248.mpp"));

      List<ResourceAssignment> assignments = file.getResourceAssignments();
      assertEquals(2, assignments.size());
      testAssignment(assignments, 0, "Task2", "Vijay");
      testAssignment(assignments, 1, "Task1", "Anil");
   }

   /**
    * This test relates to SourceForge bug #248, where it appears that MPXJ was reading deleted
    * resource assignments.
    *
    * 1. Create a file in the appropriate format with 10 resource assignments
    * 2. Save to a new name
    * 3. Delete every other assignment (2,4,6,...)
    * 4. Save again
    *
    * These steps should ensure that MS Project doesn't rewrite the whole file
    * (which it probably would when doing a "save as..."), and hence preserves the deleted assignments.
    */
   @Test public void testDeletedResourceAssignments() throws Exception
   {
      for (File file : MpxjTestData.listFiles("assignment/assignment-deletion", "deleted-resource-assignments"))
      {
         testDeletedResourceAssignments(file);
      }
   }

   /**
    * Test a project file to ensure that deleted resource assignments are not included.
    *
    * @param file project file to test
    */
   private void testDeletedResourceAssignments(File file) throws MPXJException
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(file);
      List<ResourceAssignment> assignments = mpp.getResourceAssignments();
      assertEquals(file.getName() + " does not contain 5 resource assignments", 5, assignments.size());

      testAssignment(assignments, 0, "Task 1", "Resource 1");
      testAssignment(assignments, 1, "Task 1", "Resource 3");
      testAssignment(assignments, 2, "Task 1", "Resource 5");
      testAssignment(assignments, 3, "Task 1", "Resource 7");
      testAssignment(assignments, 4, "Task 1", "Resource 9");
   }

   /**
    * Validate that a resource assignment task and resource names match a given value.
    *
    * @param assignments list of assignments
    * @param index index number of the resource to test
    * @param expectedTaskName expected task name
    * @param expectedResourceName expected resource name
    */
   private void testAssignment(List<ResourceAssignment> assignments, int index, String expectedTaskName, String expectedResourceName)
   {
      ResourceAssignment assignment = assignments.get(index);
      assertEquals(expectedTaskName, assignment.getTask().getName());
      assertEquals(expectedResourceName, assignment.getResource().getName());
   }
}
