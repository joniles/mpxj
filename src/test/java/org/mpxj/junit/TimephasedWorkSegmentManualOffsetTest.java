/*
 * file:       TimephasedWorkSegmentManualOffsetTest.java
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

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.TimeUnit;
import org.mpxj.TimeUnitDefaultsContainer;
import org.mpxj.TimephasedWork;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.TimescaleUnits;
import org.mpxj.utility.TimephasedUtility;
import org.mpxj.utility.TimescaleUtility;

import org.junit.Test;

/**
 * This a test for reading timephased work of manual scheduled tasks from an MPP file.
 * It validates the data against JSON within the Note field for each assignment.
 * The JSON is created by a VBA inside the MPP file.
 */
public class TimephasedWorkSegmentManualOffsetTest
{
   /**
    * Timephased segment test for MPP14 files.
    */
   @Test public void testMpp14ManualOffset() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp14timephasedsegmentsmanualoffsets.mpp"));
      testSegments(file);
   }

   /**
    * Suite of tests common to all file types.
    *
    * @param file ProjectFile instance
    */
   private void testSegments(ProjectFile file)
   {
      assertEquals(25, file.getResourceAssignments().size());

      //
      // Set the start date
      //
      LocalDateTime startDate = LocalDateTime.of(2024, 7, 26, 0, 0);
      int segmentCount = 12;
      int assignmentIndex = 0;

      for (ResourceAssignment assignment : file.getResourceAssignments())
      {
         //if (assignmentIndex == 22)
         {
            testSegments(assignment, assignmentIndex, startDate, segmentCount, TimescaleUnits.DAYS, false);
         }
         assignmentIndex++;
      }
   }

   /**
    * Common method used to test timephased assignment segments against expected data.
    *
    * @param assignment parent resource assignment
    * @param assignmentIndex index number of current assignment
    * @param startDate start date for segments
    * @param segmentCount number of segments to create
    * @param units units of duration for each segment
    * @param complete flag indicating if planned or complete work is required
    */
   private void testSegments(ResourceAssignment assignment, int assignmentIndex, LocalDateTime startDate, int segmentCount, TimescaleUnits units, boolean complete)
   {
      String jsonString = assignment.getNotes();
      {
         String errorMsg = "Invalid or Missing JSON for " + assignment;
         assertNotNull(errorMsg, jsonString);
         assertFalse(errorMsg, jsonString.isEmpty());
         assertTrue(errorMsg, jsonString.length() > 3);
         assertEquals(errorMsg, '[', jsonString.charAt(0));
         assertEquals(errorMsg, ']', jsonString.charAt(jsonString.length() - 1));
      }
      jsonString = jsonString.substring(1, jsonString.length() - 1);

      ArrayList<LocalDateTimeRange> dateList = m_timescale.createTimescale(startDate, units, segmentCount);
      //System.out.println(dateList);
      ProjectCalendar calendar = assignment.getEffectiveCalendar();
      List<TimephasedWork> assignments = (complete ? assignment.getTimephasedActualWork() : assignment.getTimephasedWork());
      ArrayList<Duration> durationList = m_timephased.segmentWork(calendar, assignments, units, dateList);
      //dumpExpectedData(assignment, durationList);
      assertEquals(segmentCount, durationList.size());
      TimeUnitDefaultsContainer unitDefaults = assignment.getParentFile().getProjectProperties();

      int loop = 0;
      while (!jsonString.isEmpty())
      {
         assertTrue("JSON time scaled data does more data for " + assignment, segmentCount > loop);
         String jsonValue = jsonString;
         if (jsonValue.indexOf(',') > 0)
         {
            jsonValue = jsonValue.substring(0, jsonValue.indexOf(','));
         }
         jsonString = jsonString.substring(jsonValue.length());
         if (jsonString.indexOf(',') == 0)
         {
            jsonString = jsonString.substring(1);
         }
         double expected = 0;
         if (!jsonValue.equals("\"\""))
         {
            expected = Double.parseDouble(jsonValue);
         }
         assertEquals("Failed at index " + loop + " assignment index " + assignmentIndex + "=>" + assignment, expected, durationList.get(loop).convertUnits(TimeUnit.MINUTES, unitDefaults).getDuration(), 0.009);

         loop++;
      }
      assertTrue("No Json data found for " + assignment, loop > 0);
      assertEquals("JSON time scaled data does not contain enough data for " + assignment, segmentCount, loop);
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
