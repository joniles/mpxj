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

public class TimephasedCostResourceTest
{
   @Test public void testProratedCostResource() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), TimescaleUnits.DAYS, 7);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), TimescaleUnits.DAYS, 3);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 2, 0, 0), TimescaleUnits.DAYS, 2);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-prorated-cost-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("No Progress - No Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 30.0, 30.0, null, null, 30.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 30.0, 30.0, null, null, 30.0, null});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[] {null, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[] {30.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {30.0, null});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[] {null, 30.0, 30.0});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 30.0, 30.0});

      task = file.getTaskByID(2);
      assertEquals("No Progress - Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, 30.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 30.0, 30.0});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, null, 30.0, null, null, 30.0, 30.0});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[] {null, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[] {30.0, 30.0});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {30.0, 30.0});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[] {null, null, 30.0});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, null, 30.0});

      task = file.getTaskByID(3);
      assertEquals("Progress - Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 30.0, 30.0, null, null, null, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 30.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 30.0, 30.0, null, null, 30.0, null});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[] {null, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[] {30.0, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {30.0, null});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[] {null, 30.0, 30.0});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 30.0, 30.0});

      task = file.getTaskByID(4);
      assertEquals("Progress - Complete", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      testCostSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 30.0, 30.0, null, null, 30.0, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 30.0, 30.0, null, null, 30.0, null});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[] {30.0, null});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[] {null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[] {30.0, null});
      testCostSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[] {null, 30.0, 30.0});
      testCostSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[] {null, null, null});
      testCostSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[] {null, 30.0, 30.0});
   }

   private void dumpExpectedData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Number>> fn)
   {
      if (includeAsserts)
      {
         System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
         System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
         System.out.println("assignment = task.getResourceAssignments().get(0);");
      }
      System.out.print("testCostSegments(assignment."+method+"(ranges), ");

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
