/*
 * file:       TimephasedCostResourceTest.java
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
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.mpp.MPPReader;
import org.mpxj.TimescaleUnits;
import org.mpxj.common.TimescaleHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test timephased data from cost resources.
 */
@SuppressWarnings("boxing") public class TimephasedCostResourceTest
{
   /**
    * Test prorated cost resources.
    */
   @Test public void testProratedCostResource() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), 7, TimescaleUnits.DAYS);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), 3, TimescaleUnits.DAYS);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 2, 2, 0, 0), 2, TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-prorated-cost-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("No Progress - No Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});

      task = file.getTaskByID(2);
      assertEquals("No Progress - Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, null, 30.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, null, null, null, null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, null, 30.0, null, null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, null, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, null, 30.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, null, 30.0, null, null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, null, 30.0});

      task = file.getTaskByID(3);
      assertEquals("Progress - Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, null, null, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});

      task = file.getTaskByID(4);
      assertEquals("Progress - Complete", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 30.0, 30.0, null, null, 30.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{30.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 30.0, 30.0});

      Resource resource = file.getResourceByUniqueID(1);
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 120.0, null, null, 120.0, 30.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 120.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[]{120.0, 30.0});
   }

   /**
    * Test accrue at start cost resources.
    */
   @Test public void testAccrueAtStartCostResource() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), 7, TimescaleUnits.DAYS);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), 3, TimescaleUnits.DAYS);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 2, 2, 0, 0), 2, TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-start-cost-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("No Progress", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);

      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 0.0, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});

      task = file.getTaskByID(2);
      assertEquals("No Progress - Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});

      task = file.getTaskByID(3);
      assertEquals("Some Progress", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 90.0, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});

      task = file.getTaskByID(4);
      assertEquals("Complete", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 90.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 90.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});

      Resource resource = file.getResourceByUniqueID(1);
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 360.0, 0.0, null, null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 360.0, 0.0});
      TimephasedTestHelper.testNumericSegments(resource.getTimephasedCost(rangeOverlapsEnd), new Double[]{0.0, null});
   }

   /**
    * Test accrue at end cost resources.
    */
   @Test public void testAccrueAtEndCostResource() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), 7, TimescaleUnits.DAYS);
      List<LocalDateTimeRange> rangeOverlapsStart = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 1, 28, 0, 0), 3, TimescaleUnits.DAYS);
      List<LocalDateTimeRange> rangeOverlapsEnd = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 2, 2, 0, 0), 2, TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-end-cost-resource.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("No Progress", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 0.0, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{90.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{90.0, null});

      task = file.getTaskByID(2);
      assertEquals("No Progress - Actual Cost", task.getName());
      assertEquals(1, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, null, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{90.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeOverlapsEnd), new Double[]{90.0, null});

      task = file.getTaskByID(3);
      assertEquals("Some Progress", task.getName());
      assertEquals(2, task.getResourceAssignments().size());
      assignment = task.getResourceAssignments().get(0);
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 0.0, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeCoversAssignment), new Double[]{null, 0.0, 0.0, null, null, 90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsStart), new Double[]{null, 0.0, 0.0});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedRemainingCost(rangeOverlapsEnd), new Double[]{90.0, null});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedCost(rangeOverlapsEnd), new Double[]{90.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeCoversAssignment), new Double[]{null, 0.0, null, null, null, null, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeOverlapsStart), new Double[]{null, 0.0, null});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedActualCost(rangeOverlapsEnd), new Double[]{null, null});
   }
}
