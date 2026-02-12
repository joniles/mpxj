package org.mpxj.junit;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("boxing") public class TimephasedMaterialResourceTest
{
   @Test public void testMaterialResource() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 9, 0, 0), TimescaleUnits.DAYS, 9);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-material-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(2, task.getResourceAssignments().size());

      // ensure only work is rolled up to the task
      TimephasedTestHelper.testDurationSegments(task.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});

      // work assignment
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, 80.0, null, null, 80.0, null});

      // material assignment
      assignment = task.getResourceAssignments().get(1);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});

      // Task with progress
      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(2, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 104.0, 104.0, 104.0, 104.0, null, null, 104.0, null});

      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 1.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, null, 1.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 24.0, 12.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, 12.0, 24.0, 24.0, null, null, 24.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, null});

      // Material accrued at start, no progress
      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 120.0, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 120.0, 0.0, 0.0, 0.0, null, null, 0.0, null});

      // Material accrued at start, progress
      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, 3.0, 1.5, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, null, 1.5, 3.0, 3.0, null, null, 3.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 3.0, 3.0, 3.0, 3.0, null, null, 3.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 180.0, 0.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, 0.0, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 180.0, 0.0, 0.0, 0.0, null, null, 0.0, null});

      // Material accrued at end, no progress
      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});

      // Material accrued at end, progress
      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 2.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, null, 2.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 4.0, 4.0, 4.0, 4.0, null, null, 4.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, 0.0, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, 0.0, null, null, 240.0, null});
   }
}
