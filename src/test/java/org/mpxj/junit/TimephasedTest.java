/*
 * file:       TimephasedTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       20/11/2008
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

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The tests contained in this class exercise the timephased
 * resource assignment functionality.
 */
public class TimephasedTest
{
   /**
    * Test MPP9 file timephased resource assignments.
    */
   @Test public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephased.mpp"));
      testTimephased(file);
   }

   /**
    * Test MPP9 file timephased resource assignments saved by Project 2007.
    */
   @Test public void testMpp9From12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephased-from12.mpp"));
      testTimephased(file);
   }

   /**
    * Test MPP9 file timephased resource assignments saved by Project 2010.
    */
   @Test public void testMpp9From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9timephased-from14.mpp"));
      testTimephased(file);
   }

   /**
    * Test MPP12 file timephased resource assignments.
    */
   @Test public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12timephased.mpp"));
      testTimephased(file);
   }

   /**
    * Test MPP12 file timephased resource assignments saved by Project 2010.
    */
   @Test public void testMpp12From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12timephased-from14.mpp"));
      testTimephased(file);
   }

   /**
    * Test MPP14 file timephased resource assignments.
    */
   @Test public void testMpp14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp14timephased.mpp"));
      testTimephased(file);
   }

   /**
    * Test MSPDI file timephased resource assignments.
    */
   @Disabled @Test public void testMspdi() throws Exception
   {
      ProjectFile file = new MSPDIReader().read(MpxjTestData.filePath("mspditimephased.xml"));
      testTimephased(file);
   }

   /**
    * Common timephased resource assignment tests.
    *
    * @param file project file
    */
   private void testTimephased(ProjectFile file)
   {
      //
      // Basic assignment
      //
      Task task = file.getTaskByID(Integer.valueOf(1));
      List<ResourceAssignment> assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      ResourceAssignment assignment = assignments.get(0);
      List<TimephasedWork> timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "03/12/2008 12:00", 4500.0, 60.0);
      List<TimephasedWork> timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      //
      // Front loaded assignment
      //
      task = file.getTaskByID(Integer.valueOf(2));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "26/11/2008 15:30", 2250.0, 60.0);
      testTimephased(timephasedPlanned, 1, "26/11/2008 15:30", "01/12/2008 16:30", 1125.0, 45.0);
      testTimephased(timephasedPlanned, 2, "01/12/2008 16:30", "05/12/2008 08:30", 750.0, 30.0);
      testTimephased(timephasedPlanned, 3, "05/12/2008 08:30", "08/12/2008 14:00", 187.5, 15.0);
      testTimephased(timephasedPlanned, 4, "08/12/2008 14:00", "10/12/2008 09:30", 112.5, 9.0);
      testTimephased(timephasedPlanned, 5, "10/12/2008 09:30", "11/12/2008 15:00", 75.0, 6.0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      //
      // Back loaded assignment
      //
      task = file.getTaskByID(Integer.valueOf(3));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();

      assertEquals(6, timephasedPlanned.size());
      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "21/11/2008 14:30", 75.0, 6.0);
      testTimephased(timephasedPlanned, 1, "21/11/2008 14:30", "25/11/2008 10:00", 112.5, 9.0);
      testTimephased(timephasedPlanned, 2, "25/11/2008 10:00", "26/11/2008 15:30", 187.5, 15.0);
      testTimephased(timephasedPlanned, 3, "26/11/2008 15:30", "01/12/2008 16:30", 750.0, 30.0);
      testTimephased(timephasedPlanned, 4, "01/12/2008 16:30", "05/12/2008 08:30", 1125.0, 45.0);
      testTimephased(timephasedPlanned, 5, "05/12/2008 08:30", "11/12/2008 15:00", 2250.0, 60.0);

      //
      // 50% complete task
      //
      task = file.getTaskByID(Integer.valueOf(4));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "26/11/2008 15:30", 2250.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "26/11/2008 15:30", "03/12/2008 12:00", 2250.0, 60.0);

      //
      // Split task with no work done
      //
      task = file.getTaskByID(Integer.valueOf(5));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(3, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "26/11/2008 09:00", 1920.0, 60.0);
      testTimephased(timephasedPlanned, 1, "26/11/2008 09:00", "01/12/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedPlanned, 2, "01/12/2008 09:00", "08/12/2008 12:00", 2580.0, 60.0);

      //
      // Split task with some work done
      //
      task = file.getTaskByID(Integer.valueOf(6));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "26/11/2008 09:00", 1920.0, 60.0);
      testTimephased(timephasedComplete, 1, "26/11/2008 09:00", "01/12/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "01/12/2008 09:00", "02/12/2008 15:00", 780.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "02/12/2008 15:00", "08/12/2008 12:00", 1800.0, 60.0);

      //
      // Normal task 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(7));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "03/12/2008 12:00", 4500.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Split task 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(8));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "26/11/2008 09:00", 1920.0, 60.0);
      testTimephased(timephasedComplete, 1, "26/11/2008 09:00", "01/12/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "01/12/2008 09:00", "08/12/2008 12:00", 2580.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Normal task night shift
      //
      task = file.getTaskByID(Integer.valueOf(9));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 23:00", "04/12/2008 02:00", 4500.0, 60.0);

      //
      // Normal task night shift front loaded
      //
      task = file.getTaskByID(Integer.valueOf(10));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);


      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 23:00", "27/11/2008 05:30", 2250.0, 60.0);
      testTimephased(timephasedPlanned, 1, "27/11/2008 05:30", "02/12/2008 06:30", 1125.0, 45.0);
      testTimephased(timephasedPlanned, 2, "02/12/2008 06:30", "05/12/2008 07:30", 750.0, 30.0);
      testTimephased(timephasedPlanned, 3, "05/12/2008 07:30", "09/12/2008 03:00", 187.5, 15.0);
      testTimephased(timephasedPlanned, 4, "09/12/2008 04:00", "10/12/2008 23:30", 112.5, 9.0);
      testTimephased(timephasedPlanned, 5, "10/12/2008 23:30", "12/12/2008 05:00", 75.0, 6.0);

      //
      // Normal task night shift back loaded
      //
      task = file.getTaskByID(Integer.valueOf(11));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 23:00", "22/11/2008 04:30", 75.0, 6.0);
      testTimephased(timephasedPlanned, 1, "22/11/2008 04:30", "26/11/2008 00:00", 112.5, 9.0);
      testTimephased(timephasedPlanned, 2, "26/11/2008 00:00", "27/11/2008 05:30", 187.5, 15.0);
      testTimephased(timephasedPlanned, 3, "27/11/2008 05:30", "02/12/2008 06:30", 750.0, 30.0);
      testTimephased(timephasedPlanned, 4, "02/12/2008 06:30", "05/12/2008 07:30", 1125.0, 45.0);
      testTimephased(timephasedPlanned, 5, "05/12/2008 07:30", "12/12/2008 05:00", 2250.0, 60.0);

      //
      // Normal task 50% complete night shift
      //
      task = file.getTaskByID(Integer.valueOf(12));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);


      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 23:00", "27/11/2008 05:30", 2250.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "27/11/2008 05:30", "04/12/2008 02:00", 2250.0, 60.0);

      //
      // Split task night shift
      //
      task = file.getTaskByID(Integer.valueOf(13));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(3, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 23:00", "26/11/2008 08:00", 1920.0, 60.0);
      testTimephased(timephasedPlanned, 1, "26/11/2008 23:00", "29/11/2008 08:00", 0.0, 0.0);
      testTimephased(timephasedPlanned, 2, "01/12/2008 23:00", "09/12/2008 02:00", 2580.0, 60.0);

      //
      // Split task night shift 50% complete
      //
      task = file.getTaskByID(Integer.valueOf(14));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 23:00", "26/11/2008 08:00", 1920.0, 60.0);
      testTimephased(timephasedComplete, 1, "26/11/2008 23:00", "29/11/2008 08:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "01/12/2008 23:00", "03/12/2008 05:00", 780.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "03/12/2008 05:00", "09/12/2008 02:00", 1800.0, 60.0);

      //
      // Normal task night shift 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(15));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);


      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 23:00", "04/12/2008 02:00", 4500.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Split task night shift 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(16));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 23:00", "26/11/2008 08:00", 1920.0, 60.0);
      testTimephased(timephasedComplete, 1, "26/11/2008 23:00", "29/11/2008 08:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "01/12/2008 23:00", "09/12/2008 02:00", 2580.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Normal task - 24 hour
      //
      task = file.getTaskByID(Integer.valueOf(17));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "23/11/2008 12:00", 4500.0, 60.0);

      //
      // Normal task - front loaded - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(18));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "21/11/2008 22:30", 2250.0, 60.0);
      testTimephased(timephasedPlanned, 1, "21/11/2008 22:30", "22/11/2008 23:30", 1125.0, 45.0);
      testTimephased(timephasedPlanned, 2, "22/11/2008 23:30", "24/11/2008 00:30", 750.0, 30.0);
      testTimephased(timephasedPlanned, 3, "24/11/2008 00:30", "24/11/2008 13:00", 187.5, 15.0);
      testTimephased(timephasedPlanned, 4, "24/11/2008 13:00", "25/11/2008 01:30", 112.5, 9.0);
      testTimephased(timephasedPlanned, 5, "25/11/2008 01:30", "25/11/2008 14:00", 75.0, 6.0);

      //
      // Normal task - back loaded - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(19));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "20/11/2008 21:30", 75.0, 6.0);
      testTimephased(timephasedPlanned, 1, "20/11/2008 21:30", "21/11/2008 10:00", 112.5, 9.0);
      testTimephased(timephasedPlanned, 2, "21/11/2008 10:00", "21/11/2008 22:30", 187.5, 15.0);
      testTimephased(timephasedPlanned, 3, "21/11/2008 22:30", "22/11/2008 23:30", 750.0, 30.0);
      testTimephased(timephasedPlanned, 4, "22/11/2008 23:30", "24/11/2008 00:30", 1125.0, 45.0);
      testTimephased(timephasedPlanned, 5, "24/11/2008 00:30", "25/11/2008 14:00", 2250.0, 60.0);

      //
      // Normal task - 50% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(20));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "21/11/2008 22:30", 2250.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "21/11/2008 22:30", "23/11/2008 12:00", 2250.0, 60.0);


      //
      // Split task - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(21));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);


      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(3, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "21/11/2008 09:00", 1440.0, 60.0);
      testTimephased(timephasedPlanned, 1, "21/11/2008 09:00", "25/11/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedPlanned, 2, "25/11/2008 09:00", "27/11/2008 12:00", 3060.0, 60.0);

      //
      // Split task - 50% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(22));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "21/11/2008 09:00", 1440.0, 60.0);
      testTimephased(timephasedComplete, 1, "21/11/2008 09:00", "25/11/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "25/11/2008 09:00", "25/11/2008 22:30", 810.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "25/11/2008 22:30", "27/11/2008 12:00", 2250.0, 60.0);

      //
      // Normal task - 100% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(23));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "23/11/2008 12:00", 4500.0, 60.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Split task - 100% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(24));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "21/11/2008 09:00", 1440.0, 60.0);
      testTimephased(timephasedComplete, 1, "21/11/2008 09:00", "25/11/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "25/11/2008 09:00", "27/11/2008 12:00", 3060.0, 60);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Basic assignment - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(25));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "16/12/2008 16:00", 4500.0, 30.0);


      //
      // Front loaded - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(26));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "03/12/2008 12:00", 2250.0, 30.0);
      testTimephased(timephasedPlanned, 1, "03/12/2008 13:00", "11/12/2008 15:00", 1125.0, 22.5);
      testTimephased(timephasedPlanned, 2, "11/12/2008 15:00", "19/12/2008 17:00", 750.0, 15.0);
      testTimephased(timephasedPlanned, 3, "22/12/2008 08:00", "25/12/2008 09:00", 187.5, 7.5);
      testTimephased(timephasedPlanned, 4, "25/12/2008 09:00", "30/12/2008 10:00", 112.5, 4.5);
      testTimephased(timephasedPlanned, 5, "30/12/2008 10:00", "02/01/2009 11:00", 75.0, 3.0);

      //
      // Back loaded - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(27));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "25/11/2008 10:00", 75.0, 3.0);
      testTimephased(timephasedPlanned, 1, "25/11/2008 10:00", "28/11/2008 11:00", 112.5, 4.5);
      testTimephased(timephasedPlanned, 2, "28/11/2008 11:00", "03/12/2008 12:00", 187.5, 7.5);
      testTimephased(timephasedPlanned, 3, "03/12/2008 13:00", "11/12/2008 15:00", 750.0, 15.0);
      testTimephased(timephasedPlanned, 4, "11/12/2008 15:00", "19/12/2008 17:00", 1125.0, 22.5);
      testTimephased(timephasedPlanned, 5, "22/12/2008 08:00", "02/01/2009 11:00", 2250.0, 30.0);

      //
      // 50% Complete - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(28));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "03/12/2008 12:00", 2250.0, 30.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "03/12/2008 13:00", "16/12/2008 16:00", 2250.0, 30.0);

      //
      // Split task with no work done - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(29));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(3, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "02/12/2008 09:00", 1920.0, 30.0);
      testTimephased(timephasedPlanned, 1, "02/12/2008 09:00", "05/12/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedPlanned, 2, "05/12/2008 09:00", "19/12/2008 16:00", 2580.0, 30.0);

      //
      // Split task with some work done - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(30));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "02/12/2008 09:00", 1920.0, 30.0);
      testTimephased(timephasedComplete, 1, "02/12/2008 09:00", "04/12/2008 17:00", 0.0, 0);
      testTimephased(timephasedComplete, 2, "05/12/2008 08:00", "10/12/2008 10:00", 780.0, 30.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "10/12/2008 10:00", "19/12/2008 15:00", 1800.0, 30.0);

      //
      // Normal task 100% complete - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(31));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "16/12/2008 16:00", 4500.0, 30.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Split task 100% complete - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(32));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "02/12/2008 09:00", 1920.0, 30.0);
      testTimephased(timephasedComplete, 1, "02/12/2008 09:00", "05/12/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "05/12/2008 09:00", "19/12/2008 16:00", 2580.0, 30.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Basic assignment - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(33));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "28/11/2008 11:00", 4500.0, 90.0);

      //
      // Front loaded assignment - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(34));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "27/11/2008 10:40", 3750.0, 90.0);
      testTimephased(timephasedPlanned, 1, "27/11/2008 10:40", "02/12/2008 15:26", 1875.0, 67.5027001080043);
      testTimephased(timephasedPlanned, 2, "02/12/2008 15:26", "08/12/2008 10:13", 1250.0, 45.001800072002894);
      testTimephased(timephasedPlanned, 3, "08/12/2008 10:13", "10/12/2008 08:06", 312.5, 22.495500899820037);
      testTimephased(timephasedPlanned, 4, "10/12/2008 08:06", "11/12/2008 15:00", 187.5, 13.50054002160086);
      testTimephased(timephasedPlanned, 5, "11/12/2008 15:00", "15/12/2008 11:53", 125.0, 9.000360014400584);

      //
      // Back loaded assignment - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(35));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(6, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "21/11/2008 15:53", 125.0, 9.000360014400577);
      testTimephased(timephasedPlanned, 1, "21/11/2008 15:53", "25/11/2008 13:46", 187.5, 13.500540021600864);
      testTimephased(timephasedPlanned, 2, "25/11/2008 13:46", "27/11/2008 10:39", 312.5, 22.500900036001436);
      testTimephased(timephasedPlanned, 3, "27/11/2008 10:39", "02/12/2008 15:26", 1250.0, 45.00180007200288);
      testTimephased(timephasedPlanned, 4, "02/12/2008 15:26", "08/12/2008 10:13", 1875.0, 67.50675067506751);
      testTimephased(timephasedPlanned, 5, "08/12/2008 10:13", "15/12/2008 11:53", 3750.0, 90.0);

      //
      // 50% complete task - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(36));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "25/11/2008 10:00", 2250.0, 90.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "25/11/2008 10:00", "28/11/2008 11:00", 2250.0, 90.0);

      //
      // Split task with no work done - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(37));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);


      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(0, timephasedComplete.size());

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(3, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "20/11/2008 09:00", "24/11/2008 15:20", 1920.0, 90.0);
      testTimephased(timephasedPlanned, 1, "24/11/2008 15:20", "27/11/2008 15:20", 0.0, 0.0);
      testTimephased(timephasedPlanned, 2, "27/11/2008 15:20", "03/12/2008 11:00", 2580.0, 90.0);

      //
      // Split task with some work done - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(38));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "24/11/2008 15:20", 1920.0, 90.0);
      testTimephased(timephasedComplete, 1, "24/11/2008 15:20", "27/11/2008 14:20", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "27/11/2008 14:20", "28/11/2008 15:00", 780.0, 90.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(1, timephasedPlanned.size());
      testTimephased(timephasedPlanned, 0, "28/11/2008 15:00", "03/12/2008 10:00", 1800.0, 90.0);

      //
      // Normal task 100% complete - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(39));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(1, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "03/12/2008 12:00", 6750.0, 90.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());

      //
      // Split task 100% complete - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(40));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);

      timephasedComplete = assignment.getRawTimephasedActualRegularWork();
      assertEquals(3, timephasedComplete.size());
      testTimephased(timephasedComplete, 0, "20/11/2008 09:00", "26/11/2008 09:00", 2880.0, 90.0);
      testTimephased(timephasedComplete, 1, "26/11/2008 09:00", "01/12/2008 09:00", 0.0, 0.0);
      testTimephased(timephasedComplete, 2, "01/12/2008 09:00", "08/12/2008 12:00", 3870.0, 90.0);

      timephasedPlanned = assignment.getRawTimephasedRemainingRegularWork();
      assertEquals(0, timephasedPlanned.size());
   }

   /**
    * Utility method to test the attributes of a timephased resource
    * assignment.
    *
    * @param items TimephasedWork instance to test
    * @param start start date for this assignment
    * @param finish finish date for this assignment
    * @param totalWork total work for this assignment
    * @param workPerHour work per day for this assignment
    */
   private void testTimephased(List<TimephasedWork> items, int index, String start, String finish, double totalWork, double workPerHour)
   {
      TimephasedWork item = items.get(index);
      assertEquals(start, DATE_FORMAT.format(item.getStart()));
      assertEquals(finish, DATE_FORMAT.format(item.getFinish()));
      assertEquals(totalWork, item.getTotalAmount().getDuration(), 0.02);
      assertEquals(TimeUnit.MINUTES, item.getTotalAmount().getUnits());
      if (workPerHour != -1)
      {
         assertEquals(workPerHour, item.getAmountPerHour().getDuration(), 0.02);
         assertEquals(TimeUnit.MINUTES, item.getAmountPerHour().getUnits());
      }
   }

//   createTest("timephasedComplete", timephasedComplete);
//   createTest("timephasedPlanned", timephasedPlanned);

//   private void createTest(String name, List<TimephasedWork> items)
//   {
//      int index = 0;
//      for (TimephasedWork item : items)
//      {
//         System.out.println(
//            "testTimephased("
//               + name + ", "
//               + index + ", \""
//               + DATE_FORMAT.format(item.getStart()) + "\", \""
//               + DATE_FORMAT.format(item.getFinish()) + "\", "
//               + item.getTotalAmount().getDuration() + ", "
//               + item.getAmountPerHour().getDuration() + ");");
//         ++index;
//      }
//      System.out.println();
//      System.out.println();
//   }

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
