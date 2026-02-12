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

public class TimephasedCostRollupTest
{
   @Test public void testCostRollup() throws Exception
   {
      List<LocalDateTimeRange> rangeCoversAssignment = new TimescaleUtility().createTimescale(LocalDateTime.of(2026, 2, 11, 0, 0), TimescaleUnits.DAYS, 10);

      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("timephased-cost-rollup.mpp"));

      Task task = file.getTaskByID(1);
      assertEquals("Task 1", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {21.0, 21.0, 21.0, null, null, 21.0, 21.0, null, null, null});

      task = file.getTaskByID(2);
      assertEquals("Task 2", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {22.0, 22.0, 22.0, null, null, 45.0, 45.0, 23.0, 23.0, 23.0});

      task = file.getTaskByID(3);
      assertEquals("Task 3", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {22.0, 22.0, 22.0, null, null, 22.0, 22.0, null, null, null});

      task = file.getTaskByID(4);
      assertEquals("Task 4", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 23.0, 23.0, 23.0, 23.0, 23.0});

      task = file.getTaskByID(5);
      assertEquals("Task 5", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {297.0, 297.0, 297.0, null, null, 351.6249953556315, 350.99999541666665, 301.99999541666665, 301.99999541666665, 301.99999541666665});

      task = file.getTaskByID(6);
      assertEquals("Task 6", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {272.0, 272.0, 272.0, null, null, 326.6249953556315, 325.99999541666665, 301.99999541666665, 301.99999541666665, 301.99999541666665});

      task = file.getTaskByID(7);
      assertEquals("Task 7", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {24.0, 24.0, 24.0, null, null, 24.0, 24.0, null, null, null});

      task = file.getTaskByID(8);
      assertEquals("Task 8", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getResourceAssignments().get(0).getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 10.999998854166666, 10.999998854166666, 10.999998854166666, 10.999998854166666, 10.999998854166666});
      TimephasedTestHelper.testNumericSegments(task.getResourceAssignments().get(1).getTimephasedRemainingRegularCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 33.624996501464835, 32.999996562499994, 32.999996562499994, 32.999996562499994, 32.999996562499994});
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {null, null, null, null, null, 54.6249953556315, 53.999995416666664, 53.999995416666664, 53.999995416666664, 53.999995416666664});

      task = file.getTaskByID(9);
      assertEquals("Task 9", task.getName());
      TimephasedTestHelper.testNumericSegments(task.getTimephasedCost(rangeCoversAssignment), new Double[] {25.0, 25.0, 25.0, null, null, 25.0, 25.0, null, null, null});
   }
}