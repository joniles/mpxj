/*
 * file:       TimephasedBudgetTest.java
 * author:     Jon Iles
 * date:       2026-03-26
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
import org.mpxj.AssignmentField;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.TimescaleUnits;
import org.mpxj.common.TimescaleHelper;
import org.mpxj.mpp.MPPReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test timephased data from cost resources.
 */
@SuppressWarnings("boxing") public class TimephasedBudgetTest
{
   /**
    * Test retrieval of timephased budget data.
    */
   @Test public void testBudget() throws Exception
   {
      List<LocalDateTimeRange> ranges = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 3, 26, 0, 0), LocalDateTime.of(2026, 4, 24, 0, 0), TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-budget.mpp"));

      Task task = file.getTaskByID(0);
      assertEquals(4, task.getResourceAssignments().size());

      TimephasedTestHelper.testNumericSegments(task.getTimephasedBudgetCost(ranges), new Double[]{24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedNumericValues(TaskField.BUDGET_COST, ranges), new Double[]{24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00});

      TimephasedTestHelper.testDurationSegments(task.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedDurationValues(TaskField.BUDGET_WORK, ranges, TimeUnit.HOURS), new Double[]{24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0});

      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0});
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedDurationValues(AssignmentField.BUDGET_WORK, ranges, TimeUnit.HOURS), new Double[]{8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0});

      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0});

      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00});
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedNumericValues(AssignmentField.BUDGET_COST, ranges), new Double[]{8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00});

      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{16.00, 16.00, null, null, 16.0, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00});
   }

   /**
    * Test retrieval of timephased budget data with calendar exceptions.
    */
   @Test public void testBudgetWithCalendarExceptions() throws Exception
   {
      List<LocalDateTimeRange> ranges = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 3, 26, 0, 0), LocalDateTime.of(2026, 4, 25, 0, 0), TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-budget-calendar-exceptions.mpp"));

      Task task = file.getTaskByID(0);
      assertEquals(4, task.getResourceAssignments().size());

      TimephasedTestHelper.testNumericSegments(task.getTimephasedBudgetCost(ranges), new Double[]{18.00, 18.00, null, null, null, 18.00, 18.00, 18.00, 18.00, null, null, 18.00, 18.00, 18.00, 18.00, 18.00, null, null, 18.00, 18.00, 18.00, 18.00, 18.00, null, null, 18.00, 18.00, 18.00, 18.00, 18.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{24.0, 24.0, null, null, null, 24.0, 16.0, 8.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0});

      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{8.0, 8.0, null, null, null, 8.0, null, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0});

      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{16.0, 16.0, null, null, null, 16.0, 16.0, null, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0});

      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{6.00, 6.00, null, null, null, 6.00, 6.00, 6.00, 6.00, null, null, 6.00, 6.00, 6.00, 6.00, 6.00, null, null, 6.00, 6.00, 6.00, 6.00, 6.00, null, null, 6.00, 6.00, 6.00, 6.00, 6.00});

      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{12.00, 12.00, null, null, null, 12.00, 12.00, 12.00, 12.00, null, null, 12.00, 12.00, 12.00, 12.00, 12.00, null, null, 12.00, 12.00, 12.00, 12.00, 12.00, null, null, 12.00, 12.00, 12.00, 12.00, 12.00});
   }

   /**
    * Test retrieval of timephased budget data with manual edits.
    */
   @Test public void testBudgetWithManualEdits() throws Exception
   {
      List<LocalDateTimeRange> ranges = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 3, 26, 0, 0), LocalDateTime.of(2026, 4, 24, 0, 0), TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-budget-manual-edits.mpp"));

      Task task = file.getTaskByID(0);
      assertEquals(4, task.getResourceAssignments().size());

      TimephasedTestHelper.testNumericSegments(task.getTimephasedBudgetCost(ranges), new Double[]{26.00, 26.00, null, null, 26.00, 24.00, 24.00, 24.00, 24.00, 8.00, 8.00, 24.00, 24.00, 16.00, 16.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{26.0, 26.0, null, null, 26.0, 24.0, 24.0, 24.0, 24.0, 8.0, 8.0, 24.0, 24.0, 16.0, 16.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0, 24.0, null, null, 24.0, 24.0, 24.0, 24.0});

      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{10.0, 10.0, null, null, 10.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 0.0, 0.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0, 8.0, null, null, 8.0, 8.0, 8.0, 8.0});

      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0, 16.0, null, null, 16.0, 16.0, 16.0, 16.0});

      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{10.00, 10.00, null, null, 10.00, 8.00, 8.00, 8.00, 8.00, 8.00, 8.00, 8.00, 8.00, 0.00, 0.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00});

      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{16.00, 16.00, null, null, 16.0, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00});
   }

   /**
    * Test retrieval of timephased budget data with manual edits and calendar exceptions.
    */
   @Test public void testBudgetWithManualEditsAndCalendarExceptions() throws Exception
   {
      List<LocalDateTimeRange> ranges = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 3, 26, 0, 0), LocalDateTime.of(2026, 4, 25, 0, 0), TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-budget-manual-exceptions.mpp"));

      Task task = file.getTaskByID(0);
      assertEquals(4, task.getResourceAssignments().size());

      TimephasedTestHelper.testNumericSegments(task.getTimephasedBudgetCost(ranges), new Double[]{26.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{27.2, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});

      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{10.4, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, null, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});

      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});

      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{10.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});

      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBudgetCost(ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});
   }

   /**
    * Test retrieval of baseline timephaesd budget data.
    */
   @Test public void testBudgetBaselines() throws Exception
   {
      List<LocalDateTimeRange> ranges = new TimescaleHelper().createTimescale(LocalDateTime.of(2026, 3, 26, 0, 0), LocalDateTime.of(2026, 4, 25, 0, 0), TimescaleUnits.DAYS);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-budget-baseline.mpp"));

      Task task = file.getTaskByID(0);
      assertEquals(4, task.getResourceAssignments().size());

      int baseline = 0;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{26.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{27.2, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      ResourceAssignment assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{10.4, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{10.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 1;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{27.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{27.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{11.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{11.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 2;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{28.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{28.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{12.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{12.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 3;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{29.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{29.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{13.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{13.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 4;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{30.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{30.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{14.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{14.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 5;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{31.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{31.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{15.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{15.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 6;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{32.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{32.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 7;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{33.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{33.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{17.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{17.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 8;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{34.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{34.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{18.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{18.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 9;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{35.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{35.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{19.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{19.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});

      baseline = 10;
      TimephasedTestHelper.testNumericSegments(task.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{36.00, 26.00, null, null, 26.00, 24.00, null, 24.00, 24.00, 5.00, null, 16.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00, null, null, 24.00, 24.00, 24.00, 24.00, 24.00});
      TimephasedTestHelper.testDurationSegments(task.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{36.8, 27.2, null, null, 27.2, 25.2, null, 25.2, 16.8, null, 5.0, 25.2, 16.8, 8.4, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 25.2, null, null, 25.2, 25.2, 25.2, 25.2, 16.8});
      assignment = task.getResourceAssignments().get(0);
      assertEquals("Concrete Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{20.0, 10.4, null, null, 10.4, 8.4, null, 8.4, 0.0, null, 5.0, 8.4, 0.0, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, 8.4, null, null, 8.4, 8.4, 8.4, 8.4, null});
      assignment = task.getResourceAssignments().get(1);
      assertEquals("Steel Labor", assignment.getResource().getName());
      TimephasedTestHelper.testDurationSegments(assignment.getTimephasedBaselineBudgetWork(baseline, ranges, TimeUnit.HOURS), new Double[]{16.8, 16.8, null, null, 16.8, 16.8, null, 16.8, 16.8, null, null, 16.8, 16.8, 0.0, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8, null, null, 16.8, 16.8, 16.8, 16.8, 16.8});
      assignment = task.getResourceAssignments().get(2);
      assertEquals("Concrete Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{20.00, 10.00, null, null, 10.00, 8.00, null, 8.00, 8.00, 5.00, null, 0.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00, null, null, 8.00, 8.00, 8.00, 8.00, 8.00});
      assignment = task.getResourceAssignments().get(3);
      assertEquals("Steel Cost", assignment.getResource().getName());
      TimephasedTestHelper.testNumericSegments(assignment.getTimephasedBaselineBudgetCost(baseline, ranges), new Double[]{16.00, 16.00, null, null, 16.00, 16.00, null, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00, null, null, 16.00, 16.00, 16.00, 16.00, 16.00});
   }
}
