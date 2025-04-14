/*
 * file:       TimephasedSegmentTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       2011-02-12
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
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
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
public class TimephasedWorkSegmentTest
{
   /**
    * Timephased segment test for MPP9 files.
    */
   @Test public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephasedsegments.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP9 files saved by Project 2007.
    */
   @Test public void testMpp9From12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephasedsegments-from12.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP9 files saved by Project 2010.
    */
   @Test public void testMpp9From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephasedsegments-from14.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP12 files.
    */
   @Test public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12timephasedsegments.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP12 files saved by Project 2010.
    */
   @Test public void testMpp12From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12timephasedsegments-from14.mpp"));
      testSegments(file);
   }

   /**
    * Timephased segment test for MPP14 files.
    */
   @Test public void testMpp14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp14timephasedsegments.mpp"));
      testSegments(file);
   }

   /*
    * Timephased segment test for MSPDI files.
    *
    * @throws Exception
    */
   //   @Test public void testMspdi () throws Exception
   //   {
   //      ProjectFile file = new MSPDIReader().read(MpxjTestData.filePath("mspditimephasedsegments.xml");
   //      testSegments(file);
   //   }

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
      LocalDateTime startDate = LocalDateTime.of(2011, 2, 7, 0, 0);

      //
      // Task One - 5 day assignment at 100% utilisation
      //
      Task task = file.getTaskByID(Integer.valueOf(1));
      assertEquals("Task One", task.getName());
      List<ResourceAssignment> assignments = task.getResourceAssignments();
      ResourceAssignment assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
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
      }, true);

      //
      // Task Two - 5 day assignment at 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(2));
      assertEquals("Task Two", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         4.0,
         4.0,
         4.0,
         4.0,
         4.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
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
      }, true);

      //
      // Task Three - 5 day assignment at 100% utilisation, 50% complete
      //
      task = file.getTaskByID(Integer.valueOf(3));
      assertEquals("Task Three", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0,
         4.0,
         8.0,
         8.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         4.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, true);

      //
      // Task Four - 5 day assignment at 50% utilisation, 50% complete
      //
      task = file.getTaskByID(Integer.valueOf(4));
      assertEquals("Task Four", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.0,
         0.0,
         2.0,
         4.0,
         4.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         4.0,
         4.0,
         2.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, true);

      //
      // Task Five - 10 day assignment at 100% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(5));
      assertEquals("Task Five", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
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
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
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
         0.0,
         0.0,
         0.0,
         0.0
      }, true);

      //
      // Task Six - 10 day assignment at 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(6));
      assertEquals("Task Six", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         4.0,
         4.0,
         4.0,
         4.0,
         4.0,
         0.0,
         0.0,
         4.0,
         4.0,
         4.0,
         4.0,
         4.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
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
         0.0,
         0.0,
         0.0,
         0.0
      }, true);

      //
      // Task Seven - 10 day assignment at 100% utilisation with a resource calendar non-working day and a non-default working day
      //
      task = file.getTaskByID(Integer.valueOf(7));
      assertEquals("Task Seven", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         0.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
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
         0.0,
         0.0,
         0.0,
         0.0
      }, true);

      //
      // Task Eight - 10 day assignment at 100% utilisation with a task calendar, ignoring resource calendar
      //
      task = file.getTaskByID(Integer.valueOf(8));
      assertEquals("Task Eight", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         0.0,
         8.0,
         0.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
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
         0.0,
         0.0,
         0.0,
         0.0
      }, true);

      //
      // Task Nine - 10 day assignment at 100% utilisation front loaded
      //
      task = file.getTaskByID(Integer.valueOf(9));
      assertEquals("Task Nine", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         0.0,
         0.0,
         6.0,
         6.0,
         6.0,
         4.67,
         4.0,
         0.0,
         0.0,
         4.0,
         3.33,
         2.0,
         1.47,
         1.2,
         0.0,
         0.0,
         0.8,
         0.53,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
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
         0.00,
         0.0,
         0.0,
         0.0,
         0.0,
         0.00,
         0.0,
         0.00,
         0.0,
         0.0,
         0.0,
         0.0,
         0.00,
         0.0,
         0.0
      }, true);

      //
      // Task Ten - 10 day assignment at 100% utilisation back loaded
      //
      task = file.getTaskByID(Integer.valueOf(10));
      assertEquals("Task Ten", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.80,
         0.93,
         1.20,
         1.73,
         2.00,
         0.00,
         0.00,
         4.00,
         4.00,
         4.00,
         5.33,
         6.00,
         0.00,
         0.00,
         6.00,
         6.67,
         8.00,
         8.00,
         8.00,
         0.00,
         0.00,
         8.00,
         5.33,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Task Eleven - 10 day assignment at 100% utilisation double peak
      //
      task = file.getTaskByID(Integer.valueOf(11));
      assertEquals("Task Eleven", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         2.00,
         2.00,
         4.00,
         4.00,
         8.00,
         0.00,
         0.00,
         8.00,
         4.00,
         4.00,
         2.00,
         2.00,
         0.00,
         0.00,
         2.00,
         2.00,
         4.00,
         4.00,
         8.00,
         0.00,
         0.00,
         8.00,
         4.00,
         4.00,
         2.00,
         2.00,
         0.00,
         0.00,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Task Twelve - 10 day assignment at 100% utilisation early peak
      //
      task = file.getTaskByID(Integer.valueOf(12));
      assertEquals("Task Twelve", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         2.00,
         2.00,
         4.00,
         4.00,
         8.00,
         0.00,
         0.00,
         8.00,
         8.00,
         8.00,
         6.00,
         6.00,
         0.00,
         0.00,
         4.00,
         4.00,
         4.00,
         4.00,
         2.00,
         0.00,
         0.00,
         2.00,
         1.20,
         1.20,
         0.80,
         0.80,
         0.00,
         0.00,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Task Thirteen - 10 day assignment at 100% utilisation late peak
      //
      task = file.getTaskByID(Integer.valueOf(13));
      assertEquals("Task Thirteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.80,
         0.80,
         1.20,
         1.20,
         2.00,
         0.00,
         0.00,
         2.00,
         4.00,
         4.00,
         4.00,
         4.00,
         0.00,
         0.00,
         6.00,
         6.00,
         8.00,
         8.00,
         8.00,
         0.00,
         0.00,
         8.00,
         4.00,
         4.00,
         2.00,
         2.00,
         0.00,
         0.00,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Task Fourteen - 10 day assignment at 100% utilisation bell
      //
      task = file.getTaskByID(Integer.valueOf(14));
      assertEquals("Task Fourteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.80,
         0.80,
         1.60,
         1.60,
         3.20,
         0.00,
         0.00,
         3.20,
         6.40,
         6.40,
         8.00,
         8.00,
         0.00,
         0.00,
         8.00,
         8.00,
         6.40,
         6.40,
         3.20,
         0.00,
         0.00,
         3.20,
         1.60,
         1.60,
         0.80,
         0.80,
         0.00,
         0.00,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Task Fifteen - 10 day assignment at 100% utilisation turtle
      //
      task = file.getTaskByID(Integer.valueOf(15));
      assertEquals("Task Fifteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         2.00,
         3.15,
         4.28,
         6.00,
         7.43,
         0.00,
         0.00,
         8.00,
         8.00,
         8.00,
         8.00,
         8.00,
         0.00,
         0.00,
         6.00,
         4.85,
         3.72,
         2.00,
         0.57,
         0.00,
         0.00,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Task Sixteen - 10 day assignment at 100% utilisation hand edited
      //
      task = file.getTaskByID(Integer.valueOf(16));
      assertEquals("Task Sixteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         1.00,
         2.00,
         3.00,
         4.00,
         5.00,
         0.00,
         0.00,
         6.00,
         7.00,
         8.00,
         9.00,
         10.00,
         0.00,
         0.00,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         00.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Task Seventeen - 10 day assignment at 100% utilisation contoured with a resource calendar non-working day
      //
      task = file.getTaskByID(Integer.valueOf(17));
      assertEquals("Task Seventeen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         8.00,
         8.00,
         0.00,
         8.00,
         8.00,
         0.00,
         0.00,
         8.00,
         6.00,
         6.00,
         6.00,
         4.67,
         0.00,
         0.00,
         4.00,
         4.00,
         3.33,
         2.00,
         1.47,
         0.00,
         0.00,
         1.20,
         0.80,
         0.53,
         0.00,
         0.00,
         0.00
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00,
         0.00
      }, true);

      //
      // Tests of timescale units
      //
      task = file.getTaskByID(Integer.valueOf(18));
      assertEquals("Task Eighteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.WEEKS, new double[]
      {
         40,
         40,
         40,
         40,
         40,
         40,
         0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.THIRDS_OF_MONTHS, new double[]
      {
         32,
         48,
         48,
         64,
         48,
         0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.MONTHS, new double[]
      {
         128,
         112,
         0
      }, false);

      task = file.getTaskByID(Integer.valueOf(19));
      assertEquals("Task Nineteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.QUARTERS, new double[]
      {
         312,
         520,
         528,
         160,
         0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.HALF_YEARS, new double[]
      {
         832,
         688,
         0
      }, false);

      task = file.getTaskByID(Integer.valueOf(20));
      assertEquals("Task Twenty", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.YEARS, new double[]
      {
         1880,
         1160,
         0
      }, false);
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param expected array of expected durations for each segment
    * @param complete flag indicating if planned or complete work is required
    */
   private void testSegments(ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, double[] expected, boolean complete)
   {
      ArrayList<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, units, expected.length);
      //System.out.println(dateList);
      ProjectCalendar calendar = assignment.getEffectiveCalendar();
      List<TimephasedWork> assignments = (complete ? assignment.getTimephasedActualWork() : assignment.getTimephasedWork());
      ArrayList<Duration> durationList = m_timephased.segmentWork(calendar, assignments, units, dateList);
      //dumpExpectedData(assignment, durationList);
      assertEquals(expected.length, durationList.size());
      for (int loop = 0; loop < expected.length; loop++)
      {
         assertEquals("Failed at index " + loop, expected[loop], durationList.get(loop).getDuration(), 0.009);
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
         for(Duration d : list)
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
   */

   private final TimescaleUtility m_timescale = new TimescaleUtility();
   private final TimephasedUtility m_timephased = new TimephasedUtility();
}
