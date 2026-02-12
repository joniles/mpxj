package org.mpxj.junit;

import java.util.List;
import java.util.function.Supplier;

import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.Task;
import org.mpxj.common.NumberHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimephasedTestHelper
{
   public static void testNumericSegments(List<Number> list, Double[] expected)
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
            assertEquals(NumberHelper.getDouble(expected[loop]), list.get(loop).doubleValue(), 0.02, "Failed at index " + loop);
         }
      }
   }

   public static void testDurationSegments(List<Duration> list, Double[] expected)
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
            assertEquals(expected[loop].doubleValue(), list.get(loop).getDuration(), 0.02, "Failed at index " + loop);
         }
      }
   }

   public static void dumpExpectedDurationData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Duration>> fn)
   {
      if (includeAsserts)
      {
         System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
         System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
         System.out.println("assignment = task.getResourceAssignments().get(0);");
      }
      System.out.print("testWorkSegments(assignment." + method + "(rangeCoversAssignment, TimeUnit.HOURS), ");

      System.out.print("new Double[] {");
      boolean first = true;
      for (Duration d : fn.get())
      {
         if (!first)
         {
            System.out.print(", ");
         }
         else
         {
            first = false;
         }
         System.out.print(d == null ? "null" : String.valueOf(d.getDuration()));
      }
      System.out.println("});");
   }

   public static void dumpExpectedNumericData(Task task, List<LocalDateTimeRange> ranges, String method, boolean includeAsserts, Supplier<List<Number>> fn)
   {
      if (includeAsserts)
      {
         System.out.println("assertEquals(\"" + task.getName() + "\", task.getName());");
         System.out.println("assertEquals(" + task.getResourceAssignments().size() + ", task.getResourceAssignments().size());");
         System.out.println("assignment = task.getResourceAssignments().get(0);");
      }
      System.out.print("testNumericSegments(assignment." + method + "(rangeCoversAssignment), ");

      System.out.print("new Double[] {");
      boolean first = true;
      for (Number d : fn.get())
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
