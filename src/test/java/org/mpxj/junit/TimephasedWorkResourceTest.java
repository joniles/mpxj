package org.mpxj.junit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
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


      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, null, null, null});

      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsStart), new Double[] {null, 0.0, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsStart), new Double[] {null, 80.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsStart), new Double[] {null, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 80.0, null});

      testCostSegments(assignment.getTimephasedActualRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeOverlapsEnd), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {null, null, null});


      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 160.0, 0.0, null, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 80.0, 0.0, null, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 140.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 22.0, 0.0, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 162.0, 0.0, null, null});

      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 240.0, null});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 210.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 33.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 243.0, null});

      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 80.0, 80.0, 80.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, 7.333333333333333, 7.333333333333333, 7.333333333333333, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 87.33333333333333, 87.33333333333333, 87.33333333333333, null});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedActualOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});
      testCostSegments(assignment.getTimephasedRemainingOvertimeCost(rangeCoversAssignment), new Double[] {null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 40.0, 40.0, 40.0, null});

//      Task finalTask = task;
//      dumpExpectedData(task, rangeCoversAssignment, "getTimephasedActualRegularCost", true, () -> finalTask.getResourceAssignments().get(0).getTimephasedActualRegularCost(rangeCoversAssignment));
//      dumpExpectedData(task, rangeCoversAssignment, "getTimephasedActualOvertimeCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedActualOvertimeCost(rangeCoversAssignment));
//      dumpExpectedData(task, rangeCoversAssignment, "getTimephasedRemainingRegularCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedRemainingRegularCost(rangeCoversAssignment));
//      dumpExpectedData(task, rangeCoversAssignment, "getTimephasedRemainingOvertimeCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedRemainingOvertimeCost(rangeCoversAssignment));
//      dumpExpectedData(task, rangeCoversAssignment, "getTimephasedCost", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedCost(rangeCoversAssignment));
   }

   private void dumpExpectedData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Number>> fn)
   {
      if (includeAsserts)
      {
         System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
         System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
         System.out.println("assignment = task.getResourceAssignments().get(0);");
      }
      System.out.print("testCostSegments(assignment."+method+"(rangeCoversAssignment), ");

      System.out.print("new Double[] {");
      boolean first = true;
      for(Number d : fn.get())
      {
         if (!first)
         {
            System.out.print(", ");
         }
         else
         {
            first = false;
         }
         System.out.print(d);
      }
      System.out.println("});");
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

}
