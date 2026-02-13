/*
 * file:       TimephasedTestHelper.java
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

import java.util.List;
import java.util.function.Supplier;

import org.mpxj.Duration;
import org.mpxj.Task;
import org.mpxj.common.NumberHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Helper methods for timephased tests.
 */
public final class TimephasedTestHelper
{
   /**
    * Validate numeric segments.
    *
    * @param actual actual values
    * @param expected expected values
    */
   public static void testNumericSegments(List<Number> actual, Double[] expected)
   {
      assertEquals(expected.length, actual.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         if (expected[loop] == null)
         {
            assertNull(actual.get(loop), "Failed at index " + loop);
         }
         else
         {
            assertNotNull(actual.get(loop), "Failed at index " + loop);
            assertEquals(NumberHelper.getDouble(expected[loop]), actual.get(loop).doubleValue(), 0.02, "Failed at index " + loop);
         }
      }
   }

   /**
    * Validate duration segments.
    *
    * @param actual actual values
    * @param expected expected values
    */
   public static void testDurationSegments(List<Duration> actual, Double[] expected)
   {
      assertEquals(expected.length, actual.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         if (expected[loop] == null)
         {
            assertNull(actual.get(loop), "Failed at index " + loop);
         }
         else
         {
            assertNotNull(actual.get(loop), "Failed at index " + loop);
            assertEquals(expected[loop].doubleValue(), actual.get(loop).getDuration(), 0.02, "Failed at index " + loop);
         }
      }
   }

   /**
    * Dump timephased data ready to be used as a test case.
    *
    * @param task task data
    * @param method method under test
    * @param includeAsserts include task assertions if true
    * @param fn function to generate data
    */
   public static void dumpExpectedDurationData(Task task, String method, boolean includeAsserts, Supplier<List<Duration>> fn)
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

   /**
    * Dump timephased data ready to be used as a test case.
    *
    * @param task task data
    * @param method method under test
    * @param includeAsserts include task assertions if true
    * @param fn function to generate data
    */
   public static void dumpExpectedNumericData(Task task, String method, boolean includeAsserts, Supplier<List<Number>> fn)
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
