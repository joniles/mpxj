/*
 * file:       TimephasedCostPerUseTest.java
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
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.mpp.MPPReader;
import org.mpxj.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test calculation of timephased cost per use.
 */
@SuppressWarnings("boxing")  public class TimephasedCostPerUseTest
{
   /**
    * Test calculation of timephased cost per use.
    */
   @Test public void testCostPerUse() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 10, 0, 0), 9, TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-cost-per-use.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 82.0, 72.0, 72.0, null, null, 72.0, 72.0, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 82.0, 72.0, 72.0, null, null, 72.0, 72.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 370.0, 0.0, 0.0, null, null, 0.0, 0.0, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 370.0, 0.0, 0.0, null, null, 0.0, 0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 370.0, null});

      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 370.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 28.0, 18.0, 18.0, null, null, 18.0, 18.0, null});

      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 28.0, 18.0, 18.0, null, null, 18.0, 18.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, 0.0, 0.0, null});

      task = file.getTaskByID(10);
      assertEquals("Task 10", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 100.0, 0.0, 0.0, null, null, 0.0, 0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});

      task = file.getTaskByID(11);
      assertEquals("Task 11", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 100.0, null});

      task = file.getTaskByID(12);
      assertEquals("Task 12", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[] {null, 0.0, 0.0, 0.0, null, null, 0.0, 100.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedRemainingCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, null, null, null, null});
   }
}
