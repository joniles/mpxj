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

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedWork;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimephasedUtility;
import org.mpxj.utility.TimescaleUtility;

import org.junit.Test;

/**
 * This example shows an MPP, MPX or MSPDI file being read, and basic
 * task and resource data being extracted.
 */
public class TimephasedWorkCostSegmentTest
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
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         88.0,
         88.0,
         88.0,
         0.0,
         0.0,
         88.0,
         88.0,
         88.0,
         88.0,
         88.0,
         0.0,
         0.0,
         88.0,
         88.0,
         0.0
      });
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         88.0,
         88.0,
         88.0,
         0.0,
         0.0,
         88.0,
         88.0,
         88.0,
         88.0,
         88.0,
         0.0,
         0.0,
         88.0,
         88.0,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(2));
      assertEquals("Partially complete task", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         96.0,
         96.0,
         96.0,
         0.0,
         0.0,
         96.0,
         96.0,
         96.0,
         96.0,
         96.0,
         0.0,
         0.0,
         96.0,
         96.0,
         0.0
      });
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         96.0,
         96.0,
         0.0,
         0.0,
         96.0,
         96.0,
         96.0,
         96.0,
         96.0,
         0.0,
         0.0,
         96.0,
         96.0,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         96.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(3));
      assertEquals("Complete task", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
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
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
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
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         0.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         8.0,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         112.0,
         112.0,
         112.0,
         0.0,
         0.0,
         112.0,
         112.0,
         0.0,
         112.0,
         112.0,
         0.0,
         0.0,
         112.0,
         112.0,
         112.0,
         0.0
      });
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         112.0,
         112.0,
         112.0,
         0.0,
         0.0,
         112.0,
         112.0,
         0.0,
         112.0,
         112.0,
         0.0,
         0.0,
         112.0,
         112.0,
         112.0,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(5));
      assertEquals("Partially complete task with resource holiday", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         0.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         8.0,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         120.0,
         120.0,
         120.0,
         0.0,
         0.0,
         120.0,
         120.0,
         0.0,
         120.0,
         120.0,
         0.0,
         0.0,
         120.0,
         120.0,
         120.0,
         0.0
      });
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         120.0,
         120.0,
         0.0,
         0.0,
         120.0,
         120.0,
         0.0,
         120.0,
         120.0,
         0.0,
         0.0,
         120.0,
         120.0,
         120.0,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         120.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(6));
      assertEquals("Complete task with resource holiday", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         0.0,
         8.0,
         8.0,
         0.0,
         0.0,
         8.0,
         8.0,
         8.0,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
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
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
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
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         9.15,
         9.15,
         9.15,
         0.0,
         0.0,
         9.15,
         9.15,
         9.15,
         9.15,
         9.15,
         0.0,
         0.0,
         6.85,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         166.86,
         166.86,
         166.86,
         0.0,
         0.0,
         166.86,
         166.86,
         166.86,
         166.86,
         166.86,
         0.0,
         0.0,
         125.14,
         0.0
      });
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         166.86,
         166.86,
         166.86,
         0.0,
         0.0,
         166.86,
         166.86,
         166.86,
         166.86,
         166.86,
         0.0,
         0.0,
         125.14,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0
      });

      task = file.getTaskByID(Integer.valueOf(8));
      assertEquals("Partially complete task with overtime", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         9.15,
         9.15,
         9.15,
         0.0,
         0.0,
         9.15,
         9.15,
         9.15,
         9.15,
         9.15,
         0.0,
         0.0,
         6.85,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         176.0,
         176.0,
         176.0,
         0.0,
         0.0,
         176.0,
         176.0,
         176.0,
         176.0,
         176.0,
         0.0,
         0.0,
         132.0,
         0.0
      });
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         44.0,
         176.0,
         0.0,
         0.0,
         176.0,
         176.0,
         176.0,
         176.0,
         176.0,
         0.0,
         0.0,
         132.0,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
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
      testBaselineWorkSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         9.48,
         0.0,
         0.0,
         9.48,
         9.48,
         9.48,
         9.48,
         9.48,
         0.0,
         0.0,
         7.12,
         0.0
      });
      testBaselineCostSegments(file, assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         152.00,
         152.00,
         194.96,
         0.0,
         0.0,
         194.96,
         194.96,
         194.96,
         194.96,
         194.96,
         0.0,
         0.0,
         146.22,
         0.0
      });
      testCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0,
         194.96,
         0.0,
         0.0,
         194.96,
         194.96,
         194.96,
         194.96,
         194.96,
         0.0,
         0.0,
         146.22,
         0.0
      });
      testActualCostSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
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
    * @param file parent project file
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    */
   private void testBaselineWorkSegments(ProjectFile file, ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, double[] expected)
   {
      ArrayList<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, units, expected.length);
      //System.out.println(dateList);
      List<TimephasedWork> assignments = assignment.getTimephasedBaselineWork(0);
      ArrayList<Duration> durationList = m_timephased.segmentBaselineWork(assignment.getEffectiveCalendar(), assignments, units, dateList);
      //dumpExpectedData(assignment, durationList);
      assertEquals(expected.length, durationList.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         assertEquals("Failed at index " + loop, expected[loop], durationList.get(loop).getDuration(), 0.009);
      }
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param file parent project file
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    */
   private void testBaselineCostSegments(ProjectFile file, ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, double[] expected)
   {
      ArrayList<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, units, expected.length);
      //System.out.println(dateList);
      List<TimephasedCost> assignments = assignment.getTimephasedBaselineCost(0);
      ArrayList<Double> costList = m_timephased.segmentBaselineCost(assignment.getEffectiveCalendar(), assignments, units, dateList);
      //dumpExpectedData(assignment, durationList);
      assertEquals(expected.length, costList.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         assertEquals("Failed at index " + loop, expected[loop], costList.get(loop).doubleValue(), 0.009);
      }
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    */
   private void testCostSegments(ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, double[] expected)
   {
      testCostSegments(assignment, assignment.getTimephasedCost(), startDate, units, expected);
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    */
   private void testActualCostSegments(ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, double[] expected)
   {
      testCostSegments(assignment, assignment.getTimephasedActualCost(), startDate, units, expected);
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param assignments timephased cost data
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    */
   private void testCostSegments(ResourceAssignment assignment, List<TimephasedCost> assignments, LocalDateTime startDate, TimescaleUnits units, double[] expected)
   {
      ArrayList<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, units, expected.length);
      //System.out.println(dateList);
      ArrayList<Double> costList = m_timephased.segmentCost(assignment.getEffectiveCalendar(), assignments, units, dateList);
      //dumpExpectedData(assignment, costList);
      assertEquals(expected.length, costList.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         assertEquals("Failed at index " + loop, expected[loop], costList.get(loop).doubleValue(), 0.02);
      }
   }

   /*
    * Method used to print segment durations as an array - useful for
    * creating new test cases.
    *
    * @param assignment parent assignment
    * @param list list of durations
    */
   /*
      private void dumpExpectedData(ResourceAssignment assignment, ArrayList<Duration> list)
      {
         //System.out.println(assignment);
         System.out.print("new double[]{");
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
         System.out.print("new double[]{");
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
   private final TimephasedUtility m_timephased = new TimephasedUtility();
}
