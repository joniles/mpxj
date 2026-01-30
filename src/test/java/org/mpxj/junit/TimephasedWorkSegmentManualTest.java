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

package org.mpxj.junit;

import java.time.LocalDateTime;

import java.util.List;

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
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         9.0,
         8.0,
         8.0,
         8.0,
         7.0,
         null,
         null,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Two - 5 day assignment at 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(2));
      assertEquals("Task Two", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         4.5,
         4.0,
         4.0,
         4.0,
         3.5,
         null,
         null,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Three - 5 day assignment at 100% utilisation, 50% complete
      //
      task = file.getTaskByID(Integer.valueOf(3));
      assertEquals("Task Three", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         null,
         null,
         5.0,
         8.0,
         7.0,
         null,
         null,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         9.0,
         8.0,
         3.0,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Four - 5 day assignment at 50% utilisation, 50% complete
      //
      task = file.getTaskByID(Integer.valueOf(4));
      assertEquals("Task Four", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         null,
         null,
         2.5,
         4.0,
         3.5,
         null,
         null,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         4.5,
         4.0,
         1.5,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Five - 10 day assignment at 100% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(5));
      assertEquals("Task Five", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         9.0,
         8.0,
         8.0,
         8.0,
         8.0,
         null,
         null,
         8.0,
         8.0,
         8.0,
         8.0,
         7.0,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Six - 10 day assignment at 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(6));
      assertEquals("Task Six", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         4.5,
         4.0,
         4.0,
         4.0,
         4.0,
         null,
         null,
         4.0,
         4.0,
         4.0,
         4.0,
         3.5,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Seven - 10 day assignment at 100% utilisation with a resource calendar non-working day and a non-default working day
      //
      task = file.getTaskByID(Integer.valueOf(7));
      assertEquals("Task Seven", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         9.0,
         null,
         8.0,
         8.0,
         8.0,
         8.0,
         null,
         8.0,
         8.0,
         8.0,
         8.0,
         7.0,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Eight - 10 day assignment at 100% utilisation with a task calendar, ignoring resource calendar
      //
      task = file.getTaskByID(Integer.valueOf(8));
      assertEquals("Task Eight", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         9.0,
         null,
         8.0,
         null,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         8.0,
         7.0,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Nine - 10 day assignment at 100% utilisation front loaded
      //
      task = file.getTaskByID(Integer.valueOf(9));
      assertEquals("Task Nine", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         9.0,
         8.0,
         8.0,
         8.0,
         7.75,
         null,
         null,
         6.0,
         6.0,
         6.0,
         4.42,
         4.0,
         null,
         null,
         4.0,
         3.08,
         2.0,
         1.37,
         1.15,
         null,
         null,
         0.8,
         0.43,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Ten - 10 day assignment at 100% utilisation back loaded
      //
      task = file.getTaskByID(Integer.valueOf(10));
      assertEquals("Task Ten", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         0.90,
         0.98,
         1.20,
         1.83,
         2.25,
         null,
         null,
         4.00,
         4.00,
         4.00,
         5.58,
         6.00,
         null,
         null,
         6.00,
         6.92,
         8.00,
         8.00,
         8.00,
         null,
         null,
         8.00,
         4.33,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Eleven - 10 day assignment at 100% utilisation double peak
      //
      task = file.getTaskByID(Integer.valueOf(11));
      assertEquals("Task Eleven", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         2.25,
         2.25,
         4.00,
         4.50,
         8.00,
         null,
         null,
         7.50,
         4.00,
         3.75,
         2.00,
         2.00,
         null,
         null,
         2.00,
         2.25,
         4.00,
         4.50,
         8.00,
         null,
         null,
         7.50,
         4.00,
         3.75,
         2.00,
         1.75,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Twelve - 10 day assignment at 100% utilisation early peak
      //
      task = file.getTaskByID(Integer.valueOf(12));
      assertEquals("Task Twelve", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         2.25,
         2.25,
         4.00,
         4.50,
         8.00,
         null,
         null,
         8.00,
         8.00,
         7.75,
         6.00,
         5.75,
         null,
         null,
         4.00,
         4.00,
         4.00,
         3.75,
         2.00,
         null,
         null,
         1.90,
         1.20,
         1.15,
         0.80,
         0.70,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Thirteen - 10 day assignment at 100% utilisation late peak
      //
      task = file.getTaskByID(Integer.valueOf(13));
      assertEquals("Task Thirteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         0.90,
         0.85,
         1.20,
         1.30,
         2.00,
         null,
         null,
         2.25,
         4.00,
         4.00,
         4.00,
         4.25,
         null,
         null,
         6.00,
         6.25,
         8.00,
         8.00,
         8.00,
         null,
         null,
         7.50,
         4.00,
         3.75,
         2.00,
         1.75,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Fourteen - 10 day assignment at 100% utilisation bell
      //
      task = file.getTaskByID(Integer.valueOf(14));
      assertEquals("Task Fourteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         0.90,
         0.90,
         1.60,
         1.80,
         3.20,
         null,
         null,
         3.60,
         6.40,
         6.60,
         8.00,
         8.00,
         null,
         null,
         8.00,
         7.80,
         6.40,
         6.00,
         3.20,
         null,
         null,
         3.00,
         1.60,
         1.50,
         0.80,
         0.70,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Fifteen - 10 day assignment at 100% utilisation turtle
      //
      task = file.getTaskByID(Integer.valueOf(15));
      assertEquals("Task Fifteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         2.25,
         3.39,
         4.54,
         6.00,
         7.68,
         null,
         null,
         8.00,
         8.00,
         8.00,
         8.00,
         7.75,
         null,
         null,
         6.00,
         4.61,
         3.46,
         2.00,
         0.32,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Sixteen - 10 day assignment at 100% utilisation hand edited and moved
      //
      task = file.getTaskByID(Integer.valueOf(16));
      assertEquals("Task Sixteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         1.25,
         2.13,
         3.13,
         4.13,
         5.13,
         null,
         null,
         6.13,
         7.13,
         8.13,
         9.13,
         8.75,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Sixteen v2 - 10 day assignment at 100% utilisation hand edited
      //
      task = file.getTaskByID(Integer.valueOf(21));
      assertEquals("Task Sixteen v2", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         1.00,
         2.00,
         3.00,
         4.00,
         5.00,
         null,
         null,
         6.00,
         7.00,
         8.00,
         9.00,
         10.00,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Task Seventeen - 10 day assignment at 100% utilisation contoured with a resource calendar non-working day
      //
      task = file.getTaskByID(Integer.valueOf(17));
      assertEquals("Task Seventeen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.DAYS, false, new Double[]
      {
         9.00,
         8.00,
         null,
         8.00,
         8.00,
         null,
         null,
         7.75,
         6.00,
         6.00,
         6.00,
         4.42,
         null,
         null,
         4.00,
         4.00,
         3.08,
         2.00,
         1.37,
         null,
         null,
         1.15,
         0.80,
         0.43,
         null,
         null,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.DAYS, true, new Double[]
      {
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null
      });

      //
      // Tests of timescale units
      //
      task = file.getTaskByID(Integer.valueOf(18));
      assertEquals("Task Eighteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.WEEKS, false, new Double[]
      {
         41.0,
         40.0,
         40.0,
         40.0,
         40.0,
         39.0,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.THIRDS_OF_MONTHS, false, new Double[]
      {
         33.0,
         48.0,
         48.0,
         64.0,
         47.0,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.MONTHS, false, new Double[]
      {
         129.0,
         111.0,
         null
      });

      task = file.getTaskByID(Integer.valueOf(19));
      assertEquals("Task Nineteen", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.QUARTERS, false, new Double[]
      {
         313.0,
         520.0,
         528.0,
         159.0,
         null
      });
      testSegments(assignment, startDate, TimescaleUnits.HALF_YEARS, false, new Double[]
      {
         833.0,
         687.0,
         null
      });

      task = file.getTaskByID(Integer.valueOf(20));
      assertEquals("Task Twenty", task.getName());
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      testSegments(assignment, startDate, TimescaleUnits.YEARS, false, new Double[]
      {
         1881.0,
         1159.0,
         null
      });
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param startDate start date for segments
    * @param units units of duration for each segment
    * @param complete flag indicating if planned or complete work is required
    * @param expected array of expected durations for each segment
    */
   private void testSegments(ResourceAssignment assignment, LocalDateTime startDate, TimescaleUnits units, boolean complete, Double[] expected)
   {
      List<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, units, expected.length);
      //System.out.println(dateList);
      List<Duration> durationList = complete ? assignment.getTimephasedActualWork(dateList, TimeUnit.HOURS) : assignment.getTimephasedWork(dateList, TimeUnit.HOURS);
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
            assertEquals(expected[loop], durationList.get(loop).getDuration(), 0.009, "Failed at index " + loop + " assignment " + assignment);
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
}
