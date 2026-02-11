package org.mpxj.junit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimephasedMaterialResourceTest
{
   @Test public void testMaterialResource() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 9, 0, 0), TimescaleUnits.DAYS, 9);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-material-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(2, task.getResourceAssignments().size());

      // ensure only work is rolled up to the task
      testWorkSegments(task.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      testWorkSegments(task.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      testWorkSegments(task.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});

      // work assignment
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      testWorkSegments(assignment.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});
      testWorkSegments(assignment.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      testWorkSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});
      testMaterialSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      // material assignment
      assignment = task.getResourceAssignments().get(1);
      testMaterialSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      testMaterialSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      testMaterialSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      testWorkSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(2, task.getResourceAssignments().size());

      assignment = task.getResourceAssignments().get(0);
      testMaterialSegments(assignment.getTimephasedActualMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 1.0, null, null, null, null, null, null});
      testMaterialSegments(assignment.getTimephasedRemainingMaterial(rangeCoversAssignment), new Double[] {null, null, 1.0, 2.0, 2.0, null, null, 2.0, null});
      testMaterialSegments(assignment.getTimephasedMaterial(rangeCoversAssignment), new Double[] {null, 2.0, 2.0, 2.0, 2.0, null, null, 2.0, null});
      testWorkSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, null, null, null, null, null, null, null});

      assignment = task.getResourceAssignments().get(1);
      testWorkSegments(assignment.getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 4.0, null, null, null, null, null, null});
      testWorkSegments(assignment.getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, null, 4.0, 8.0, 8.0, null, null, 8.0, null});
      testWorkSegments(assignment.getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS), new Double[] {null, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, null});

      // TODO: accrued at start and end

//      Task finalTask = task;
//      dumpExpectedWorkData(task, rangeCoversAssignment, "getTimephasedActualWork", true, () -> finalTask.getResourceAssignments().get(1).getTimephasedActualWork(rangeCoversAssignment, TimeUnit.HOURS));
//      dumpExpectedWorkData(task, rangeCoversAssignment, "getTimephasedRemainingWork", false, () -> finalTask.getResourceAssignments().get(1).getTimephasedRemainingWork(rangeCoversAssignment, TimeUnit.HOURS));
//      dumpExpectedWorkData(task, rangeCoversAssignment, "getTimephasedWork", false, () -> finalTask.getResourceAssignments().get(1).getTimephasedWork(rangeCoversAssignment, TimeUnit.HOURS));
//      dumpExpectedMaterialData(task, rangeCoversAssignment, "getTimephasedActualMaterial", true, () -> finalTask.getResourceAssignments().get(0).getTimephasedActualMaterial(rangeCoversAssignment));
//      dumpExpectedMaterialData(task, rangeCoversAssignment, "getTimephasedRemainingMaterial", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedRemainingMaterial(rangeCoversAssignment));
//      dumpExpectedMaterialData(task, rangeCoversAssignment, "getTimephasedMaterial", false, () -> finalTask.getResourceAssignments().get(0).getTimephasedMaterial(rangeCoversAssignment));
   }

   private void testWorkSegments(List<Duration> workList, Double[] expected)
   {
      assertEquals(expected.length, workList.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         if (expected[loop] == null)
         {
            assertNull(workList.get(loop), "Failed at index " + loop);
         }
         else
         {
            assertNotNull(workList.get(loop), "Failed at index " + loop);
            assertEquals(expected[loop], workList.get(loop).getDuration(), 0.02, "Failed at index " + loop);
         }
      }
   }

   private void testMaterialSegments(List<Number> costList, Double[] expected)
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

   private void dumpExpectedWorkData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Duration>> fn)
   {
      if (includeAsserts)
      {
         System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
         System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
         System.out.println("assignment = task.getResourceAssignments().get(0);");
      }
      System.out.print("testWorkSegments(assignment."+method+"(rangeCoversAssignment, TimeUnit.HOURS), ");

      System.out.print("new Double[] {");
      boolean first = true;
      for(Duration d : fn.get())
      {
         if (!first)
         {
            System.out.print(", ");
         }
         else
         {
            first = false;
         }
         System.out.print(d == null ? "null" : d.getDuration());
      }
      System.out.println("});");
   }

      private void dumpExpectedMaterialData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Number>> fn)
      {
         if (includeAsserts)
         {
            System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
            System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
            System.out.println("assignment = task.getResourceAssignments().get(0);");
         }
         System.out.print("testMaterialSegments(assignment."+method+"(rangeCoversAssignment), ");

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
}
