/*
 * file:       TimephasedMaterialResourceTest.java
 * author:     Jon Iles
 * date:       2026-02-13
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

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.AssignmentField;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test timephased data for material resources.
 */
@SuppressWarnings("boxing") public class TimephasedMaterialResourceTest
{
   /**
    * Test timephased data for material resources.
    */
   @Test public void testMaterialResource() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 9, 0, 0), 9, TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-material-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(2, task.getResourceAssignments().size());

      // ensure only work is rolled up to the task
      TimephasedTestHelper.testDurationSegments(task.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedDurationValues(TaskField.ACTUAL_WORK, rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedDurationValues(TaskField.REMAINING_WORK, rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedDurationValues(TaskField.WORK, rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedNumericValues(TaskField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedNumericValues(TaskField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedNumericValues(TaskField.COST, rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});

      // work assignment
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedDurationValues(AssignmentField.ACTUAL_WORK, rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedDurationValues(AssignmentField.REMAINING_WORK, rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedDurationValues(AssignmentField.WORK,rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, 80.0, null, null, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.COST, rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, 80.0, null, null, 80.0, null});

      // work resource
      Resource resource = assignment.getResource();
      TimephasedTestHelper.testDurationSegments(resource.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(resource.getTimephasedDurationValues(ResourceField.ACTUAL_WORK, rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(resource.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(resource.getTimephasedDurationValues(ResourceField.REMAINING_WORK, rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(resource.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(resource.getTimephasedDurationValues(ResourceField.WORK,rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, 80.0, null, null, 80.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedNumericValues(ResourceField.COST, rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, 80.0, null, null, 80.0, null});

      // material assignment
      assignment = task.getResourceAssignments().get(1);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_MATERIAL, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.COST, rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});

      // material resource
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedNumericValues(ResourceField.ACTUAL_MATERIAL, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedNumericValues(ResourceField.REMAINING_MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedNumericValues(ResourceField.MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedNumericValues(ResourceField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedNumericValues(ResourceField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedNumericValues(ResourceField.COST, rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});

      // Task with progress
      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(2, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});

      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 1.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 1.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, null, 1.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_MATERIAL, rangeCoversAssignment), new Double[] {null, null, 1.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 24.0, 12.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, 24.0, 12.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, 12.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, null, 12.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.COST, rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});

      // Material accrued at start, no progress
      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_MATERIAL, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.MATERIAL, rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 120.0, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, 120.0, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 120.0, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.COST, rangeCoversAssignment), new Double[] {null, 120.0, 0.0, 0.0, 0.0, null, null, 0.0, null});

      // Material accrued at start, progress
      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, 3.0, 1.5, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_MATERIAL, rangeCoversAssignment), new Double[] {null, 3.0, 1.5, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, null, 1.5, 3.0, 3.0, null, null, 3.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_MATERIAL, rangeCoversAssignment), new Double[] {null, null, 1.5, 3.0, 3.0, null, null, 3.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 3.0, 3.0, 3.0, 3.0, null, null, 3.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.MATERIAL, rangeCoversAssignment), new Double[] {null, 3.0, 3.0, 3.0, 3.0, null, null, 3.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 180.0, 0.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, 180.0, 0.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, null, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 180.0, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.COST, rangeCoversAssignment), new Double[] {null, 180.0, 0.0, 0.0, 0.0, null, null, 0.0, null});

      // Material accrued at end, no progress
      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_MATERIAL, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_MATERIAL, rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.MATERIAL, rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.COST, rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});

      // Material accrued at end, progress
      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 2.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_MATERIAL, rangeCoversAssignment), new Double[] {null, 4.0, 2.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, null, 2.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_MATERIAL, rangeCoversAssignment), new Double[] {null, null, 2.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.MATERIAL, rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.ACTUAL_COST, rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.REMAINING_COST, rangeCoversAssignment), new Double[] {null, null, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.COST, rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});
   }
}
