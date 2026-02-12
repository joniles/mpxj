package org.mpxj.junit;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimephasedWorkResourceTest
{
   @Test public void testSingleRateCost() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 3, 0, 0), TimescaleUnits.DAYS, 5);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 3, 0, 0), TimescaleUnits.DAYS, 3);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 5, 0, 0), TimescaleUnits.DAYS, 3);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-single-rate.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      Resource resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {null, null, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 140.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 22.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 162.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 140.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 22.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 162.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 162.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 162.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 162.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 162.0, 0.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});

      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 210.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 33.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 243.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, 210.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, 33.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 243.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 243.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 243.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 243.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 243.0, null});


      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 7.333333333333333, 7.333333333333333, 7.333333333333333, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 87.33333333333333, 87.33333333333333, 87.33333333333333, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 7.333333333333333, 7.333333333333333});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 87.33333333333333, 87.33333333333333});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {7.333333333333333, 7.333333333333333, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {87.33333333333333, 87.33333333333333, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 87.33333333333333, 87.33333333333333, 87.33333333333333, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 87.33333333333333, 87.33333333333333});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {87.33333333333333, 87.33333333333333, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 87.33333333333333, 87.33333333333333, 87.33333333333333, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 87.33333333333333, 87.33333333333333});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {87.33333333333333, 87.33333333333333, null});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
   }

   @Test public void testMultiRateCost() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 3, 0, 0), TimescaleUnits.DAYS, 5);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 3, 0, 0), TimescaleUnits.DAYS, 3);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 5, 0, 0), TimescaleUnits.DAYS, 3);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-multi-rate.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, 88.0, 88.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 84.0, 88.0, 88.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 84.0, null, null, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 172.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 172.0, 0.0, null, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 86.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 86.0, 0.0, null, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 150.0, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 23.42857421875, 0.0, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 173.42857421875, 0.0, null, null});

      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 260.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 260.0, null});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 227.00000244140625, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 35.42857177734375, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 262.42857421875, null});

      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, 88.0, 88.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 7.666666666666666, 8.0, 8.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 91.66666666666667, 96.0, 96.0, null});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 42.0, 44.0, 44.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 42.0, 44.0, 44.0, null});

      task = file.getTaskByID(10);
      assertEquals("Task 10", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, 0.0, 88.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 84.0, 0.0, 88.0, null});

      task = file.getTaskByID(11);
      assertEquals("Task 11", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 88.0, 88.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 88.0, 88.0, null});

      task = file.getTaskByID(12);
      assertEquals("Task 12", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, 88.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, 88.0, null});

      assertEquals(0, assignment.getTimephasedActualRegularCost(Collections.emptyList()).size());
      assertEquals(0, assignment.getTimephasedActualOvertimeCost(Collections.emptyList()).size());
      assertEquals(0, assignment.getTimephasedRemainingRegularCost(Collections.emptyList()).size());
      assertEquals(0, assignment.getTimephasedRemainingOvertimeCost(Collections.emptyList()).size());
      assertEquals(0, assignment.getTimephasedCost(Collections.emptyList()).size());

      assertEquals(0, assignment.getTimephasedActualRegularCost(null).size());
      assertEquals(0, assignment.getTimephasedActualOvertimeCost(null).size());
      assertEquals(0, assignment.getTimephasedRemainingRegularCost(null).size());
      assertEquals(0, assignment.getTimephasedRemainingOvertimeCost(null).size());
      assertEquals(0, assignment.getTimephasedCost(null).size());
   }

   @Test public void testActualCost() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 4, 0, 0), TimescaleUnits.DAYS, 7);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 4, 0, 0), TimescaleUnits.DAYS, 3);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 6, 0, 0), TimescaleUnits.DAYS, 5);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-actual-work-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 240.0, 0.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 240.0, 0.0, null, null, 0.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 230.0, 0.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 11.0, 0.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 241.0, 0.0, null, null, 0.0, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 11.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 251.0, null});

      List<LocalDateTimeRange> rangeWithoutOverlap = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 1, 1, 0, 0), TimescaleUnits.DAYS, 5);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualRegularCost(rangeWithoutOverlap), new Double[] {null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualOvertimeCost(rangeWithoutOverlap), new Double[] {null, null, null, null, null});
   }

   @Test public void testActualOvertimeWork() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 1, 27, 0, 0), TimescaleUnits.DAYS, 8);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 1, 27, 0, 0), TimescaleUnits.DAYS, 3);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 1, 29, 0, 0), TimescaleUnits.DAYS, 6);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-actual-overtime-work.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 4.0, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 1.0, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 5.0, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, 8.0, 8.0, null, null, 4.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, 2.0, 2.0, null, null, 1.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 5.0, 10.0, 10.0, null, null, 5.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 4.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 1.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, null, 8.0});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, null, 2.0});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 5.0, 10.0});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {8.0, 8.0, null, null, 4.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {2.0, 2.0, null, null, 1.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {10.0, 10.0, null, null, 5.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 5.0, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 0.8, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 5.8, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 3.0, 8.0, 8.0, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 0.8210526315789475, 2.1894736842105265, 2.1894736842105265, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 9.621052631578948, 10.189473684210526, 10.189473684210526, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 5.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 0.8, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 5.8, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 3.0, 8.0});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 0.8210526315789475, 2.1894736842105265});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 9.621052631578948, 10.189473684210526});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {8.0, 8.0, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {2.1894736842105265, 2.1894736842105265, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {10.189473684210526, 10.189473684210526, null, null, null, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 4.0, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 0.0, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 4.0, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 2.0, 2.0, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 6.0, 2.0, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 4.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 0.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 4.0, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 2.0, 2.0});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeOverlapsStart, TimeUnit.HOURS), new Double[] {null, 6.0, 2.0});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualRegularWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualOvertimeWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedActualWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingRegularWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {2.0, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedRemainingOvertimeWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedWork(rangeOverlapsEnd, TimeUnit.HOURS), new Double[] {2.0, null, null, null, null, null});
   }
}
