/*
 * file:       TimephasedSegmentTest2.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       2011-12-07
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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This example shows an MPP, MPX or MSPDI file being read, and basic
 * task and resource data being extracted.
 */
@Disabled public class TimephasedWorkCostSegmentTest
{
   /**
    * Timephased segment test for MPP9 files.
    */
   @Test public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephased2.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP9 files saved by Project 2007.
    */
   @Test public void testMpp9From12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephased2-from12.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP9 files saved by Project 2010.
    */
   @Test public void testMpp9From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephased2-from14.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP12 files.
    */
   @Test public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12timephased2.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP12 files saved by Project 2010.
    */
   @Test public void testMpp12From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12timephased2-from14.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP14 files.
    */
   @Test public void testMpp14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp14timephased2.mpp"));
      testSegments(file);
   }

   /**
    * Suite of tests common to all file types.
    *
    * @param file ProjectFile instance
    */
   private void testSegments(ProjectFile file)
   {
      //
      // Set the start date
      //
      LocalDateTime startDate = LocalDateTime.of(2011, 12, 7, 0, 0);

      //
      // Test each task
      //
      Task task = file.getTaskByID(Integer.valueOf(1));
      assertEquals("Planned task", task.getName());
      List<ResourceAssignment> assignments = task.getResourceAssignments();
      ResourceAssignment assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         88.0,
         88.0,
         88.0,
         null,
         null,
         88.0,
         88.0,
         88.0,
         88.0,
         88.0,
         null,
         null,
         88.0,
         88.0,
         null
      });
      testRemainingRegularCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         88.0,
         88.0,
         88.0,
         null,
         null,
         88.0,
         88.0,
         88.0,
         88.0,
         88.0,
         null,
         null,
         88.0,
         88.0,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         0.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(2));
      assertEquals("Partially complete task", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         96.0,
         96.0,
         96.0,
         null,
         null,
         96.0,
         96.0,
         96.0,
         96.0,
         96.0,
         null,
         null,
         96.0,
         96.0,
         null
      });
      testRemainingRegularCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         null,
         96.0,
         96.0,
         null,
         null,
         96.0,
         96.0,
         96.0,
         96.0,
         96.0,
         null,
         null,
         96.0,
         96.0,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         96.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(3));
      assertEquals("Complete task", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         104.0,
         104.0,
         104.0,
         null,
         null,
         104.0,
         104.0,
         104.0,
         104.0,
         104.0,
         null,
         null,
         104.0,
         104.0,
         null
      });
      testRemainingRegularCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         null,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         104.0,
         104.0,
         104.0,
         0.0,
         0.0,
         104.0,
         104.0,
         104.0,
         104.0,
         104.0,
         0.0,
         0.0,
         104.0,
         104.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(4));
      assertEquals("Planned task with resource holiday", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         0.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         8.0,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         112.0,
         112.0,
         112.0,
         null,
         null,
         112.0,
         112.0,
         0.0,
         112.0,
         112.0,
         null,
         null,
         112.0,
         112.0,
         112.0,
         null
      });
      testRemainingRegularCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         112.0,
         112.0,
         112.0,
         null,
         null,
         112.0,
         112.0,
         null,
         112.0,
         112.0,
         null,
         null,
         112.0,
         112.0,
         112.0,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         0.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(5));
      assertEquals("Partially complete task with resource holiday", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         0.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         8.0,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         120.0,
         120.0,
         120.0,
         null,
         null,
         120.0,
         120.0,
         0.0,
         120.0,
         120.0,
         null,
         null,
         120.0,
         120.0,
         120.0,
         null
      });
      testRemainingRegularCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         null,
         120.0,
         120.0,
         null,
         null,
         120.0,
         120.0,
         null,
         120.0,
         120.0,
         null,
         null,
         120.0,
         120.0,
         120.0,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         120.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(6));
      assertEquals("Complete task with resource holiday", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         0.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         8.0,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         128.0,
         128.0,
         128.0,
         null,
         null,
         128.0,
         128.0,
         0.0,
         128.0,
         128.0,
         null,
         null,
         128.0,
         128.0,
         128.0,
         null
      });
      testRemainingRegularCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         null,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         128.0,
         128.0,
         128.0,
         0.0,
         0.0,
         128.0,
         128.0,
         0.0,
         128.0,
         128.0,
         0.0,
         0.0,
         128.0,
         128.0,
         128.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(7));
      assertEquals("Planned task with overtime", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         9.15,
         9.15,
         9.15,
         null,
         null,
         9.15,
         9.15,
         9.15,
         9.15,
         9.15,
         null,
         null,
         6.85,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         166.86,
         166.86,
         166.86,
         null,
         null,
         166.86,
         166.86,
         166.86,
         166.86,
         166.86,
         null,
         null,
         125.14,
         null
      });
      testRemainingCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         166.86,
         166.86,
         166.86,
         null,
         null,
         166.86,
         166.86,
         166.86,
         166.86,
         166.86,
         null,
         null,
         125.14,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         0.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(8));
      assertEquals("Partially complete task with overtime", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         9.15,
         9.15,
         9.15,
         null,
         null,
         9.15,
         9.15,
         9.15,
         9.15,
         9.15,
         null,
         null,
         6.85,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         176.0,
         176.0,
         176.0,
         null,
         null,
         176.0,
         176.0,
         176.0,
         176.0,
         176.0,
         null,
         null,
         132.0,
         null
      });
      testRemainingCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         null,
         44.0,
         176.0,
         null,
         null,
         176.0,
         176.0,
         176.0,
         176.0,
         176.0,
         null,
         null,
         132.0,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         176.0,
         132.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(9));
      assertEquals("Partially complete task with overtime added", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(assignment, startDate, new Double[]
      {
         8.0,
         8.0,
         9.48,
         null,
         null,
         9.48,
         9.48,
         9.48,
         9.48,
         9.48,
         null,
         null,
         7.12,
         null
      });
      testBaselineCostSegments(assignment, startDate, new Double[]
      {
         152.00,
         152.00,
         194.96,
         null,
         null,
         194.96,
         194.96,
         194.96,
         194.96,
         194.96,
         null,
         null,
         146.22,
         null
      });
      testRemainingCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         null,
         null,
         194.96,
         null,
         null,
         194.96,
         194.96,
         194.96,
         194.96,
         194.96,
         null,
         null,
         146.22,
         null
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new Double[]
      {
         152.0,
         152.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      });
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param expected array of expected durations for each segment
    */
   private void testBaselineWorkSegments(ResourceAssignment assignment, LocalDateTime startDate, Double[] expected)
   {
      List<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, TimescaleUnits.DAYS, expected.length);
      //System.out.println(dateList);
      List<Duration> durationList = assignment.getTimephasedBaselineWork(0, dateList, TimeUnit.HOURS);
      //dumpExpectedData(assignment, durationList);
      assertEquals(expected.length, durationList.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         if (expected[loop] == null)
         {
            assertNull(durationList.get(loop));
         }
         else
         {
            assertEquals(expected[loop], durationList.get(loop).getDuration(), 0.009, "Failed at index " + loop);
         }
      }
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param expected array of expected durations for each segment
    */
   private void testBaselineCostSegments(ResourceAssignment assignment, LocalDateTime startDate, Double[] expected)
   {
      List<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, TimescaleUnits.DAYS, expected.length);
      List<Number> costList = assignment.getTimephasedBaselineCost(0, dateList);
      testCostSegments(costList, expected);
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    */
   private void testRemainingRegularCostSegments(ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, Double[] expected)
   {
      List<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, TimescaleUnits.DAYS, expected.length);
      List<Number> costList = assignment.getTimephasedRemainingRegularCost(dateList);
      testCostSegments(costList, expected);
   }

   private void testRemainingCostSegments(ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, Double[] expected)
   {
      List<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, TimescaleUnits.DAYS, expected.length);
      List<Number> costList = assignment.getTimephasedRemainingCost(dateList);
      testCostSegments(costList, expected);
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    */
   private void testActualCostSegments(ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, Double[] expected)
   {
      System.out.println("TEST SKIPPED");
//      List<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, TimescaleUnits.DAYS, expected.length);
//      List<Number> costList = assignment.getSegmentedTimephasedActualCost(dateList);
//      testCostSegments(costList, expected);
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param costList segmented timephased costs
    * @param expected array of expected durations for each segment
    */
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
            assertEquals(expected[loop], costList.get(loop).doubleValue(), 0.02, "Failed at index " + loop);
         }
      }
   }

   /*
    * Method used to print segment durations as an array - useful for
    * creating new test cases.
    *
    * @param assignment parent assignment
    * @param list a list of durations
    */
   /*
      private void dumpExpectedData(ResourceAssignment assignment, ArrayList<Duration> list)
      {
         //System.out.println(assignment);
         System.out.print("new Double[]{");
         boolean first = true;
         for (Duration d : list)
         {
            if (!first)
            {
               System.out.print(", ");
            }
            else
            {
               first = false;
            }
            System.out.print(d.getDuration());
         }
         System.out.println("}");
      }
   
      private void dumpExpectedData(ResourceAssignment assignment, ArrayList<Double> list)
      {
         //System.out.println(assignment);
         System.out.print("new Double[]{");
         boolean first = true;
         for (Double d : list)
         {
            if (!first)
            {
               System.out.print(", ");
            }
            else
            {
               first = false;
            }
            System.out.print(d.doubleValue());
         }
         System.out.println("}");
      }
   */
   private final TimescaleUtility m_timescale = new TimescaleUtility();
}
