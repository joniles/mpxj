package org.mpxj.junit;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimephasedFixedCostTest
{
   @Test public void testCostPerUse() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 10, 0, 0), TimescaleUnits.DAYS, 9);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-fixed-cost.mpp"));

      // No fixed cost
      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      // Fixed cost prorated
      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, 20.0, 20.0, 20.0, null, null, 20.0, 20.0, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 20.0, 20.0, 20.0, null, null, 20.0, 20.0, null});


      // Fixed cost accrued at start
      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, 0.0, 0.0, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, 0.0, 0.0, null});


      // Fixed cost accrued at end
      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 100.0, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 0.0, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 100.0, null});

      // Fixed cost prorated 50% complete
      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 20.0, 20.0, 10.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, 10.0, null, null, 20.0, 20.0, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 20.0, 20.0, 10.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, 10.0, null, null, 20.0, 20.0, null});

      // Fixed cost prorated 100% complete
      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 20.0, 20.0, 20.0, null, null, 20.0, 20.0, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 20.0, 20.0, 20.0, null, null, 20.0, 20.0, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, 0.0, null, null, 0.0, 0.0, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, 0.0, null, null, 0.0, 0.0, null});

      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, 0.0, 0.0, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, 0.0, 0.0, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, 0.0, null, null, 0.0, 100.0, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, 0.0, null, null, 0.0, 100.0, null});

      task = file.getTaskByID(10);
      assertEquals("Task 10", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 100.0, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 100.0, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(11);
      assertEquals("Task 11", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      testNumericSegments(task.getTimephasedActualFixedCost(rangeCoversAssignment), new Double[] {null, 20.0, 20.0, 10.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingFixedCost(rangeCoversAssignment), new Double[] {null, null, null, 10.0, null, null, 20.0, 20.0, null});
      testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 129.0, 124.0, 62.0, null, null, null, null, null});
      testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, 62.0, null, null, 124.0, 124.0, null});
      testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, 129.0, 124.0, 124.0, null, null, 124.0, 124.0, null});

//      Task finalTask = task;
//      dumpExpectedNumericlData(task, rangeCoversAssignment, "getTimephasedActualFixedCost", true, () -> finalTask.getTimephasedActualFixedCost(rangeCoversAssignment));
//      dumpExpectedNumericlData(task, rangeCoversAssignment, "getTimephasedRemainingFixedCost", false, () -> finalTask.getTimephasedRemainingFixedCost(rangeCoversAssignment));
//      dumpExpectedNumericlData(task, rangeCoversAssignment, "getTimephasedActualCost", false, () -> finalTask.getTimephasedActualCost(rangeCoversAssignment));
//      dumpExpectedNumericlData(task, rangeCoversAssignment, "getTimephasedRemainingCost", false, () -> finalTask.getTimephasedRemainingCost(rangeCoversAssignment));
//      dumpExpectedNumericlData(task, rangeCoversAssignment, "getTimephasedCost", false, () -> finalTask.getTimephasedCost(rangeCoversAssignment));
   }

   private void testNumericSegments(List<Number> list, Double[] expected)
   {
      assertEquals(expected.length, list.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         if (expected[loop] == null)
         {
            assertNull(list.get(loop), "Failed at index " + loop);
         }
         else
         {
            assertNotNull(list.get(loop), "Failed at index " + loop);
            assertEquals(expected[loop], list.get(loop).doubleValue(), 0.02, "Failed at index " + loop);
         }
      }
   }

//   private void dumpExpectedNumericlData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Number>> fn)
//   {
//      if (includeAsserts)
//      {
//         System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
//         System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
//      }
//      System.out.print("testNumericSegments(task." + method + "(rangeCoversAssignment), ");
//
//      System.out.print("new Double[] {");
//      boolean first = true;
//      for (Number d : fn.get())
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
