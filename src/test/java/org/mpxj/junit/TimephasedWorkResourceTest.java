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
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {null, null, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 160.0, 0.0});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, 0.0});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 140.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 22.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 162.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 140.0, 0.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 22.0, 0.0});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 162.0, 0.0});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 162.0, 0.0, null, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 162.0, 0.0});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 162.0, 0.0, null, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 162.0, 0.0});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, null, null});

      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 240.0, null});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 210.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 33.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 243.0, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {0.0, 210.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {0.0, 33.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 243.0, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 243.0, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 243.0, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 243.0, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 0.0, 0.0});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {0.0, 243.0, null});


      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 7.333333333333333, 7.333333333333333, 7.333333333333333, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 87.33333333333333, 87.33333333333333, 87.33333333333333, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, 80.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 7.333333333333333, 7.333333333333333});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 87.33333333333333, 87.33333333333333});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {80.0, 80.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {7.333333333333333, 7.333333333333333, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {87.33333333333333, 87.33333333333333, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 87.33333333333333, 87.33333333333333, 87.33333333333333, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 87.33333333333333, 87.33333333333333});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {87.33333333333333, 87.33333333333333, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 87.33333333333333, 87.33333333333333, 87.33333333333333, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 87.33333333333333, 87.33333333333333});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {87.33333333333333, 87.33333333333333, null});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      resource = assignment.getResource();
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
      testCostSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      testCostSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      testCostSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
      testCostSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 40.0, 40.0});
      testCostSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[] {40.0, 40.0, null});
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
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, 88.0, 88.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 84.0, 88.0, 88.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 84.0, null, null, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 172.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 172.0, 0.0, null, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 86.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 86.0, 0.0, null, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 150.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 23.42857421875, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 173.42857421875, 0.0, null, null});

      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 260.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 260.0, null});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 227.00000244140625, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 35.42857177734375, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 262.42857421875, null});

      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, 88.0, 88.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 7.666666666666666, 8.0, 8.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 91.66666666666667, 96.0, 96.0, null});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 42.0, 44.0, 44.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 42.0, 44.0, 44.0, null});

      task = file.getTaskByID(10);
      assertEquals("Task 10", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 84.0, 0.0, 88.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 84.0, 0.0, 88.0, null});

      task = file.getTaskByID(11);
      assertEquals("Task 11", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 88.0, 88.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 88.0, 88.0, null});

      task = file.getTaskByID(12);
      assertEquals("Task 12", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, 88.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, 88.0, null});

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
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 240.0, 0.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 240.0, 0.0, null, null, 0.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 230.0, 0.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 11.0, 0.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 241.0, 0.0, null, null, 0.0, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 240.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 0.0, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 240.0, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 11.0, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null, 251.0, null});

      List<LocalDateTimeRange> rangeWithoutOverlap = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 1, 1, 0, 0), TimescaleUnits.DAYS, 5);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeWithoutOverlap), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeWithoutOverlap), new Double[] {null, null, null, null, null});

//            Task finalTask = task;
//            dumpExpectedData(task, rangeCoversAssignment, "getTimephasedActualRegularCost", true, () -> finalTask.getResourceAssignments().get(0).getTimephasedActualRegularCost(rangeCoversAssignment));
//            dumpExpectedData(task, rangeCoversAssignment, "getTimephasedActualOvertimeCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedActualOvertimeCost(rangeCoversAssignment));
//            dumpExpectedData(task, rangeCoversAssignment, "getTimephasedRemainingRegularCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedRemainingRegularCost(rangeCoversAssignment));
//            dumpExpectedData(task, rangeCoversAssignment, "getTimephasedRemainingOvertimeCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedRemainingOvertimeCost(rangeCoversAssignment));
//            dumpExpectedData(task, rangeCoversAssignment, "getTimephasedCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedCost(rangeCoversAssignment));
   }

   private void testCostSegments(List<Number> costList, Double[] expected)
   {
      assertEquals(expected.length, costList.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         if (expected[loop] == null)
         {
            assertNull(costList.get(loop), "Failed at index " + loop);
         }
         else
         {
            assertNotNull(costList.get(loop), "Failed at index " + loop);
            assertEquals(expected[loop], costList.get(loop).doubleValue(), 0.02, "Failed at index " + loop);
         }
      }
   }

//   private void dumpExpectedData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Number>> fn)
//   {
//      if (includeAsserts)
//      {
//         System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
//         System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
//         System.out.println("assignment = task.getResourceAssignments().get(0);");
//      }
//      System.out.print("testCostSegments(assignment."+method+"(rangeCoversAssignment), ");
//
//      System.out.print("new Double[] {");
//      boolean first = true;
//      for(Number d : fn.get())
//      {
//         if (!first)
//         {
//            System.out.print(", ");
//         }
//         else
//         {
//            first = false;
//         }
//         System.out.print(d);
//      }
//      System.out.println("});");
//   }
}
