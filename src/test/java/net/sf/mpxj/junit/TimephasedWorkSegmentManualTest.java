/*
 * file:       TimephasedWorkSegmentManualTest.java
 * author:     Fabian Schmidt
 * date:       2024-08-13
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

package net.sf.mpxj.junit;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import net.sf.mpxj.LocalDateTimeRange;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpp.TimescaleUnits;
import net.sf.mpxj.utility.TimephasedUtility;
import net.sf.mpxj.utility.TimescaleUtility;

import org.junit.Test;

/**
 * This a test for reading timephased work of manual scheduled tasks from an MPP file.
 */
public class TimephasedWorkSegmentManualTest
{
   /**
    * Timephased segment test for MPP14 files.
    */
   @Test public void testMpp14Manual() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp14timephasedsegmentsmanual.mpp"));
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
      LocalDateTime startDate = LocalDateTime.of(2011, 2, 7, 0, 0);
      Task task;
      List<ResourceAssignment> assignments;
      ResourceAssignment assignment;

      //
      // Task One - 5 day assignment at 100% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(1));
      assertEquals("Task One", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         9.0,
         8.0,
         8.0,
         8.0,
         7.0,
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
         4.5,
         4.0,
         4.0,
         4.0,
         3.5,
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
         5.0,
         8.0,
         7.0,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         9.0,
         8.0,
         3.0,
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
         2.5,
         4.0,
         3.5,
         0.0,
         0.0,
         0.0,
         0.0,
         0.0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         4.5,
         4.0,
         1.5,
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
         9.0,
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
         7.0,
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
         4.5,
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
         3.5,
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
         9.0,
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
         7.0,
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
         9.0,
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
         7.0,
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
         9.0,
         8.0,
         8.0,
         8.0,
         7.75,
         0.0,
         0.0,
         6.0,
         6.0,
         6.0,
         4.42,
         4.0,
         0.0,
         0.0,
         4.0,
         3.08,
         2.0,
         1.37,
         1.15,
         0.0,
         0.0,
         0.8,
         0.43,
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
      // Task Ten - 10 day assignment at 100% utilisation back loaded
      //
      task = file.getTaskByID(Integer.valueOf(10));
      assertEquals("Task Ten", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         0.90,
         0.98,
         1.20,
         1.83,
         2.25,
         0.00,
         0.00,
         4.00,
         4.00,
         4.00,
         5.58,
         6.00,
         0.00,
         0.00,
         6.00,
         6.92,
         8.00,
         8.00,
         8.00,
         0.00,
         0.00,
         8.00,
         4.33,
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
         2.25,
         2.25,
         4.00,
         4.50,
         8.00,
         0.00,
         0.00,
         7.50,
         4.00,
         3.75,
         2.00,
         2.00,
         0.00,
         0.00,
         2.00,
         2.25,
         4.00,
         4.50,
         8.00,
         0.00,
         0.00,
         7.50,
         4.00,
         3.75,
         2.00,
         1.75,
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
         2.25,
         2.25,
         4.00,
         4.50,
         8.00,
         0.00,
         0.00,
         8.00,
         8.00,
         7.75,
         6.00,
         5.75,
         0.00,
         0.00,
         4.00,
         4.00,
         4.00,
         3.75,
         2.00,
         0.00,
         0.00,
         1.90,
         1.20,
         1.15,
         0.80,
         0.70,
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
         0.90,
         0.85,
         1.20,
         1.30,
         2.00,
         0.00,
         0.00,
         2.25,
         4.00,
         4.00,
         4.00,
         4.25,
         0.00,
         0.00,
         6.00,
         6.25,
         8.00,
         8.00,
         8.00,
         0.00,
         0.00,
         7.50,
         4.00,
         3.75,
         2.00,
         1.75,
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
         0.90,
         0.90,
         1.60,
         1.80,
         3.20,
         0.00,
         0.00,
         3.60,
         6.40,
         6.60,
         8.00,
         8.00,
         0.00,
         0.00,
         8.00,
         7.80,
         6.40,
         6.00,
         3.20,
         0.00,
         0.00,
         3.00,
         1.60,
         1.50,
         0.80,
         0.70,
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
         2.25,
         3.39,
         4.54,
         6.00,
         7.68,
         0.00,
         0.00,
         8.00,
         8.00,
         8.00,
         8.00,
         7.75,
         0.00,
         0.00,
         6.00,
         4.61,
         3.46,
         2.00,
         0.32,
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
      // Task Sixteen - 10 day assignment at 100% utilisation hand edited and moved
      //
      task = file.getTaskByID(Integer.valueOf(16));
      assertEquals("Task Sixteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, new double[]
      {
         1.25,
         2.13,
         3.13,
         4.13,
         5.13,
         0.00,
         0.00,
         6.13,
         7.13,
         8.13,
         9.13,
         8.75,
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
         0.00
      }, true);

      //
      // Task Sixteen v2 - 10 day assignment at 100% utilisation hand edited
      //
      task = file.getTaskByID(Integer.valueOf(21));
      assertEquals("Task Sixteen v2", task.getName());
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
         0.00,
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
         9.00,
         8.00,
         0.00,
         8.00,
         8.00,
         0.00,
         0.00,
         7.75,
         6.00,
         6.00,
         6.00,
         4.42,
         0.00,
         0.00,
         4.00,
         4.00,
         3.08,
         2.00,
         1.37,
         0.00,
         0.00,
         1.15,
         0.80,
         0.43,
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
         41,
         40,
         40,
         40,
         40,
         39,
         0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.THIRDS_OF_MONTHS, new double[]
      {
         33,
         48,
         48,
         64,
         47,
         0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.MONTHS, new double[]
      {
         129,
         111,
         0
      }, false);

      task = file.getTaskByID(Integer.valueOf(19));
      assertEquals("Task Nineteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.QUARTERS, new double[]
      {
         313,
         520,
         528,
         159,
         0
      }, false);
      testSegments(assignment, startDate, TimescaleUnits.HALF_YEARS, new double[]
      {
         833,
         687,
         0
      }, false);

      task = file.getTaskByID(Integer.valueOf(20));
      assertEquals("Task Twenty", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.YEARS, new double[]
      {
         1881,
         1159,
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
         assertEquals("Failed at index " + loop + " assignment " + assignment, expected[loop], durationList.get(loop).getDuration(), 0.009);
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
