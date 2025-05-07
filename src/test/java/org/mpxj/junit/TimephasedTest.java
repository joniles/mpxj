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

import static org.junit.Assert.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

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
   @Test public void testMspdi() throws Exception
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
      List<TimephasedWork> timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(3, timephasedPlanned.size());
      TimephasedWork timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "02/12/2008 17:00", 64.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 12:00", 4.0, 4.0);
      List<TimephasedWork> timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());

      //
      // Front loaded assignment
      //
      task = file.getTaskByID(Integer.valueOf(2));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(11, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "25/11/2008 17:00", 24.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 17:00", 7.625, 7.625);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "27/11/2008 08:00", "28/11/2008 17:00", 12.0, 6.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "01/12/2008 08:00", "01/12/2008 17:00", 5.875, 5.875);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "02/12/2008 08:00", "04/12/2008 17:00", 12.0, 4.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "05/12/2008 08:00", "05/12/2008 17:00", 2.125, 2.125);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "08/12/2008 08:00", "08/12/2008 17:00", 1.7, 1.7);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "09/12/2008 08:00", "09/12/2008 17:00", 1.2, 1.2);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "10/12/2008 08:00", "10/12/2008 17:00", 0.875, 0.875);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "11/12/2008 08:00", "11/12/2008 15:00", 0.6, 0.6);
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());

      //
      // Back loaded assignment
      //
      task = file.getTaskByID(Integer.valueOf(3));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(11, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 0.7, 0.7);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "21/11/2008 17:00", 0.925, 0.925);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "24/11/2008 08:00", "24/11/2008 17:00", 1.2, 1.2);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "25/11/2008 08:00", "25/11/2008 17:00", 1.8, 1.8);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 17:00", 2.375, 2.375);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "27/11/2008 08:00", "28/11/2008 17:00", 8.0, 4.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "01/12/2008 08:00", "01/12/2008 17:00", 4.125, 4.125);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "02/12/2008 08:00", "04/12/2008 17:00", 18.0, 6.0);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "05/12/2008 08:00", "05/12/2008 17:00", 7.875, 7.875);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "08/12/2008 08:00", "10/12/2008 17:00", 24.0, 8.0);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "11/12/2008 08:00", "11/12/2008 15:00", 6.0, 6.0);

      //
      // 50% complete task
      //
      task = file.getTaskByID(Integer.valueOf(4));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(3, timephasedComplete.size());
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 7.0, 7.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "25/11/2008 17:00", 24.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 15:30", 6.5, 6.5);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "26/11/2008 15:30", "26/11/2008 17:00", 1.5, 1.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "27/11/2008 08:00", "02/12/2008 17:00", 32.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 12:00", 4.0, 4.0);

      //
      // Split task with no work done
      //
      task = file.getTaskByID(Integer.valueOf(5));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(7, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "25/11/2008 17:00", 24.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 09:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "27/11/2008 08:00", "28/11/2008 17:00", 0.0, 0.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "01/12/2008 09:00", "01/12/2008 17:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "02/12/2008 08:00", "05/12/2008 17:00", 32.0, 8.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "08/12/2008 08:00", "08/12/2008 12:00", 4.0, 4.0);

      //
      // Split task with some work done
      //
      task = file.getTaskByID(Integer.valueOf(6));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(6, timephasedComplete.size());
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 7.0, 7.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "25/11/2008 17:00", 24.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 09:00", 1.0, 1.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "27/11/2008 08:00", "28/11/2008 17:00", 0.0, 0.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "01/12/2008 09:00", "01/12/2008 17:00", 7.0, 7.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "02/12/2008 08:00", "02/12/2008 15:00", 6.0, 6.0);

      //
      // Normal task 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(7));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(3, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 7.0, 7.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "02/12/2008 17:00", 64.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 12:00", 4.0, 4.0);

      //
      // Split task 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(8));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(7, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 7.0, 7.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "25/11/2008 17:00", 24.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 09:00", 1.0, 1.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "27/11/2008 08:00", "28/11/2008 17:00", 0.0, 0.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "01/12/2008 09:00", "01/12/2008 17:00", 7.0, 7.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "02/12/2008 08:00", "05/12/2008 17:00", 32.0, 8.0);
      timephased = timephasedComplete.get(6);
      testTimephased(timephased, "08/12/2008 08:00", "08/12/2008 12:00", 4.0, 4.0);

      //
      // Normal task night shift
      //
      task = file.getTaskByID(Integer.valueOf(9));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(9, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "29/11/2008 00:00", 32.0, 8.0);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "29/11/2008 00:00", "29/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "02/12/2008 00:00", "04/12/2008 00:00", 16.0, 8.0);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "04/12/2008 00:00", "04/12/2008 02:00", 2.0, 2.0);
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());

      //
      // Normal task night shift front loaded
      //
      task = file.getTaskByID(Integer.valueOf(10));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(18, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "27/11/2008 00:00", 16.0, 8.0);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "27/11/2008 00:00", "28/11/2008 00:00", 7.125, 7.125);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "28/11/2008 00:00", "29/11/2008 00:00", 6.0, 6.0);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "29/11/2008 00:00", "29/11/2008 08:00", 5.25, 5.25);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 0.75, 0.75);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "02/12/2008 00:00", "03/12/2008 00:00", 5.375, 5.375);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "03/12/2008 00:00", "05/12/2008 00:00", 8.0, 4.0);
      timephased = timephasedPlanned.get(11);
      testTimephased(timephased, "05/12/2008 00:00", "06/12/2008 00:00", 3.625, 3.625);
      timephased = timephasedPlanned.get(12);
      testTimephased(timephased, "06/12/2008 00:00", "06/12/2008 08:00", 1.75, 1.75);
      timephased = timephasedPlanned.get(13);
      testTimephased(timephased, "08/12/2008 23:00", "09/12/2008 00:00", 0.25, 0.25);
      timephased = timephasedPlanned.get(14);
      testTimephased(timephased, "09/12/2008 00:00", "10/12/2008 00:00", 1.5, -1); // 0.75 from MPP, 1.5 from MSPDI
      timephased = timephasedPlanned.get(15);
      testTimephased(timephased, "10/12/2008 00:00", "11/12/2008 00:00", 1.175, 1.175);
      timephased = timephasedPlanned.get(16);
      testTimephased(timephased, "11/12/2008 00:00", "12/12/2008 00:00", 0.8, 0.8);
      timephased = timephasedPlanned.get(17);
      testTimephased(timephased, "12/12/2008 00:00", "12/12/2008 05:00", 0.4, 0.4);

      //
      // Normal task night shift back loaded
      //
      task = file.getTaskByID(Integer.valueOf(11));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(17, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 0.1, 0.1);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 0.8, 0.8);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 0.875, 0.875);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 0.15, 0.15);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "26/11/2008 00:00", 1.2, 1.2);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "26/11/2008 00:00", "27/11/2008 00:00", 2.0, 2.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "27/11/2008 00:00", "28/11/2008 00:00", 2.875, 2.875);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "28/11/2008 00:00", "29/11/2008 00:00", 4.0, 4.0);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "29/11/2008 00:00", "29/11/2008 08:00", 3.5, 3.5);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 0.5, 0.5);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "02/12/2008 00:00", "03/12/2008 00:00", 4.625, 4.625);
      timephased = timephasedPlanned.get(11);
      testTimephased(timephased, "03/12/2008 00:00", "05/12/2008 00:00", 12.0, 6.0);
      timephased = timephasedPlanned.get(12);
      testTimephased(timephased, "05/12/2008 00:00", "06/12/2008 00:00", 6.375, 6.375);
      timephased = timephasedPlanned.get(13);
      testTimephased(timephased, "06/12/2008 00:00", "06/12/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(14);
      testTimephased(timephased, "08/12/2008 23:00", "09/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(15);
      testTimephased(timephased, "09/12/2008 00:00", "12/12/2008 00:00", 24.0, 8.0);
      timephased = timephasedPlanned.get(16);
      testTimephased(timephased, "12/12/2008 00:00", "12/12/2008 05:00", 4.0, 4.0);

      //
      // Normal task 50% complete night shift
      //
      task = file.getTaskByID(Integer.valueOf(12));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(6, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(6, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "27/11/2008 00:00", 16.0, 8.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "27/11/2008 00:00", "27/11/2008 05:30", 4.5, 4.5);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "27/11/2008 05:30", "28/11/2008 00:00", 3.5, 3.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "28/11/2008 00:00", "29/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "29/11/2008 00:00", "29/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "02/12/2008 00:00", "04/12/2008 00:00", 16.0, 8.0);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "04/12/2008 00:00", "04/12/2008 02:00", 2.0, 2.0);

      //
      // Split task night shift
      //
      task = file.getTaskByID(Integer.valueOf(13));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(12, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "26/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "26/11/2008 00:00", "26/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "27/11/2008 00:00", "29/11/2008 08:00", 0.0, 0.0);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "02/12/2008 00:00", "06/12/2008 00:00", 32.0, 8.0);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "06/12/2008 00:00", "06/12/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "08/12/2008 23:00", "09/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(11);
      testTimephased(timephased, "09/12/2008 00:00", "09/12/2008 02:00", 2.0, 2.0);

      //
      // Split task night shift 50% complete
      //
      task = file.getTaskByID(Integer.valueOf(14));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(5, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(10, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "26/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "26/11/2008 00:00", "26/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(6);
      testTimephased(timephased, "27/11/2008 00:00", "29/11/2008 08:00", 0.0, 0.0);
      timephased = timephasedComplete.get(7);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(8);
      testTimephased(timephased, "02/12/2008 00:00", "03/12/2008 00:00", 8.0, 8.0);
      timephased = timephasedComplete.get(9);
      testTimephased(timephased, "03/12/2008 00:00", "03/12/2008 05:00", 4.0, 4.0);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "03/12/2008 05:00", "04/12/2008 00:00", 4.0, 4.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "04/12/2008 00:00", "06/12/2008 00:00", 16.0, 8.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "06/12/2008 00:00", "06/12/2008 08:00", 7.0, 7.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "08/12/2008 23:00", "09/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "09/12/2008 00:00", "09/12/2008 02:00", 2.0, 2.0);

      //
      // Normal task night shift 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(15));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(9, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "29/11/2008 00:00", 32.0, 8.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "29/11/2008 00:00", "29/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(6);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(7);
      testTimephased(timephased, "02/12/2008 00:00", "04/12/2008 00:00", 16.0, 8.0);
      timephased = timephasedComplete.get(8);
      testTimephased(timephased, "04/12/2008 00:00", "04/12/2008 02:00", 2.0, 2.0);

      //
      // Split task night shift 100% complete
      //
      task = file.getTaskByID(Integer.valueOf(16));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(12, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 23:00", "21/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "22/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "24/11/2008 23:00", "25/11/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "25/11/2008 00:00", "26/11/2008 00:00", 8.0, 8.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "26/11/2008 00:00", "26/11/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(6);
      testTimephased(timephased, "27/11/2008 00:00", "29/11/2008 08:00", 0.0, 0.0);
      timephased = timephasedComplete.get(7);
      testTimephased(timephased, "01/12/2008 23:00", "02/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(8);
      testTimephased(timephased, "02/12/2008 00:00", "06/12/2008 00:00", 32.0, 8.0);
      timephased = timephasedComplete.get(9);
      testTimephased(timephased, "06/12/2008 00:00", "06/12/2008 08:00", 7.0, 7.0);
      timephased = timephasedComplete.get(10);
      testTimephased(timephased, "08/12/2008 23:00", "09/12/2008 00:00", 1.0, 1.0);
      timephased = timephasedComplete.get(11);
      testTimephased(timephased, "09/12/2008 00:00", "09/12/2008 02:00", 2.0, 2.0);

      //
      // Normal task - 24 hour
      //
      task = file.getTaskByID(Integer.valueOf(17));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "23/11/2008 00:00", 48.0, 24.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "23/11/2008 00:00", "23/11/2008 12:00", 12.0, 12.0);
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());

      //
      // Normal task - front loaded - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(18));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(6, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());

      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 23.625, 23.625);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "23/11/2008 00:00", 17.875, 17.875);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "23/11/2008 00:00", "24/11/2008 00:00", 12.0, 12.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "24/11/2008 00:00", "25/11/2008 00:00", 5.025, 5.025);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "25/11/2008 00:00", "25/11/2008 14:00", 1.475, 1.475);

      //
      // Normal task - back loaded - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(19));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(6, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());

      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 1.625, 1.625);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "22/11/2008 00:00", 5.375, 5.375);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "23/11/2008 00:00", 12.125, 12.125);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "23/11/2008 00:00", "24/11/2008 00:00", 18.0, 18.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "24/11/2008 00:00", "25/11/2008 00:00", 23.875, 23.875);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "25/11/2008 00:00", "25/11/2008 14:00", 14.0, 14.0);

      //
      // Normal task - 50% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(20));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(3, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(2, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "21/11/2008 22:30", 22.5, 22.5);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "21/11/2008 22:30", "22/11/2008 00:00", 1.5, 1.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "22/11/2008 00:00", "23/11/2008 00:00", 24.0, 24.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "23/11/2008 00:00", "23/11/2008 12:00", 12.0, 12.0);

      //
      // Split task - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(21));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(6, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "21/11/2008 09:00", 9.0, 9.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "25/11/2008 00:00", 0.0, 0.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "25/11/2008 09:00", "26/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "26/11/2008 00:00", "27/11/2008 00:00", 24.0, 24.0);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "27/11/2008 00:00", "27/11/2008 12:00", 12.0, 12.0);

      //
      // Split task - 50% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(22));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(3, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(4, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "21/11/2008 09:00", 9.0, 9.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "25/11/2008 00:00", 0.0, 0.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "25/11/2008 09:00", "25/11/2008 22:30", 13.5, 13.5);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "25/11/2008 22:30", "26/11/2008 00:00", 1.5, 1.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "26/11/2008 00:00", "27/11/2008 00:00", 24.0, 24.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "27/11/2008 00:00", "27/11/2008 12:00", 12.0, 12.0);

      //
      // Normal task - 100% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(23));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(3, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "23/11/2008 00:00", 48.0, 24.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "23/11/2008 00:00", "23/11/2008 12:00", 12.0, 12.0);

      //
      // Split task - 100% complete - 24 hours
      //
      task = file.getTaskByID(Integer.valueOf(24));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(6, timephasedComplete.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "21/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 00:00", "21/11/2008 09:00", 9.0, 9.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "22/11/2008 00:00", "25/11/2008 00:00", 0.0, 0.0);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "25/11/2008 09:00", "26/11/2008 00:00", 15.0, 15.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "26/11/2008 00:00", "27/11/2008 00:00", 24.0, 24.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "27/11/2008 00:00", "27/11/2008 12:00", 12.0, 12.0);

      //
      // Basic assignment - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(25));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 3.5, 3.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "15/12/2008 17:00", 68.0, 4.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "16/12/2008 08:00", "16/12/2008 16:00", 3.5, 3.5);

      //
      // Front loaded - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(26));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(12, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 3.5, 3.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "02/12/2008 17:00", 32.0, 4.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 17:00", 3.5, 3.5);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "04/12/2008 08:00", "10/12/2008 17:00", 15.0, 3.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "11/12/2008 08:00", "11/12/2008 17:00", 2.75, 2.75);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "12/12/2008 08:00", "19/12/2008 17:00", 12.0, 2.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "22/12/2008 08:00", "24/12/2008 17:00", 3.0, 1.0);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "25/12/2008 08:00", "25/12/2008 17:00", 0.65, 0.65);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "26/12/2008 08:00", "29/12/2008 17:00", 1.2, 0.6);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "30/12/2008 08:00", "30/12/2008 17:00", 0.45, 0.45);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "31/12/2008 08:00", "01/01/2009 17:00", 0.8, 0.4);
      timephased = timephasedPlanned.get(11);
      testTimephased(timephased, "02/01/2009 08:00", "02/01/2009 11:00", 0.15, 0.15);

      //
      // Back loaded - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(27));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(12, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 0.35, 0.35);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "24/11/2008 17:00", 0.8, 0.4);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "25/11/2008 08:00", "25/11/2008 17:00", 0.55, 0.55);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "26/11/2008 08:00", "27/11/2008 17:00", 1.2, 0.6);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "28/11/2008 08:00", "28/11/2008 17:00", 0.85, 0.85);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "01/12/2008 08:00", "02/12/2008 17:00", 2.0, 1.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 17:00", 1.5, 1.5);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "04/12/2008 08:00", "10/12/2008 17:00", 10.0, 2.0);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "11/12/2008 08:00", "11/12/2008 17:00", 2.25, 2.25);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "12/12/2008 08:00", "19/12/2008 17:00", 18.0, 3.0);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "22/12/2008 08:00", "01/01/2009 17:00", 36.0, 4.0);
      timephased = timephasedPlanned.get(11);
      testTimephased(timephased, "02/01/2009 08:00", "02/01/2009 11:00", 1.5, 1.5);

      //
      // 50% Complete - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(28));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(3, timephasedComplete.size());
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 3.5, 3.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "02/12/2008 17:00", 32.0, 4.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 12:00", 2.0, 2.0);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "03/12/2008 13:00", "03/12/2008 17:00", 2.0, 2.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "04/12/2008 08:00", "15/12/2008 17:00", 32.0, 4.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "16/12/2008 08:00", "16/12/2008 16:00", 3.5, 3.5);

      //
      // Split task with no work done - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(29));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(7, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 3.5, 3.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "01/12/2008 17:00", 28.0, 4.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "02/12/2008 08:00", "02/12/2008 09:00", 0.5, 0.5);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "03/12/2008 08:00", "04/12/2008 17:00", 0.0, 0.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "05/12/2008 09:00", "05/12/2008 17:00", 3.5, 3.5);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "08/12/2008 08:00", "18/12/2008 17:00", 36.0, 4.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "19/12/2008 08:00", "19/12/2008 16:00", 3.5, 3.5);

      //
      // Split task with some work done - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(30));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(6, timephasedComplete.size());
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 3.5, 3.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "01/12/2008 17:00", 28.0, 4.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "02/12/2008 08:00", "02/12/2008 09:00", 0.5, 0.5);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "03/12/2008 08:00", "04/12/2008 17:00", 0.0, 0.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "05/12/2008 08:00", "09/12/2008 17:00", 12.0, 4.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "10/12/2008 08:00", "10/12/2008 10:00", 1.0, 1.0);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "10/12/2008 10:00", "10/12/2008 17:00", 3.0, 3.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "11/12/2008 08:00", "18/12/2008 17:00", 24.0, 4.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "19/12/2008 08:00", "19/12/2008 15:00", 3.0, 3.0);

      //
      // Normal task 100% complete - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(31));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(3, timephasedComplete.size());
      assertEquals(0, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 3.5, 3.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "15/12/2008 17:00", 68.0, 4.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "16/12/2008 08:00", "16/12/2008 16:00", 3.5, 3.5);

      //
      // Split task 100% complete - 50% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(32));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(7, timephasedComplete.size());
      assertEquals(0, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 3.5, 3.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "01/12/2008 17:00", 28.0, 4.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "02/12/2008 08:00", "02/12/2008 09:00", 0.5, 0.5);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "03/12/2008 08:00", "04/12/2008 17:00", 0.0, 0.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "05/12/2008 09:00", "05/12/2008 17:00", 3.5, 3.5);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "08/12/2008 08:00", "18/12/2008 17:00", 36.0, 4.0);
      timephased = timephasedComplete.get(6);
      testTimephased(timephased, "19/12/2008 08:00", "19/12/2008 16:00", 3.5, 3.5);

      //
      // Basic assignment - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(33));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 10.5, 10.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "27/11/2008 17:00", 60.0, 12.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "28/11/2008 08:00", "28/11/2008 11:00", 4.5, 4.5);

      //
      // Front loaded assignment - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(34));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(12, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 10.5, 10.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "26/11/2008 17:00", 48.0, 12.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "27/11/2008 08:00", "27/11/2008 17:00", 10.0, 10.0);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "28/11/2008 08:00", "01/12/2008 17:00", 18.0, 9.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "02/12/2008 08:00", "02/12/2008 17:00", 8.42, 8.42);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "03/12/2008 08:00", "05/12/2008 17:00", 18.0, 6.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "08/12/2008 08:00", "08/12/2008 17:00", 3.83, 3.83);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "09/12/2008 08:00", "09/12/2008 17:00", 3.0, 3.0);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "10/12/2008 08:00", "10/12/2008 17:00", 1.82, 1.82);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "11/12/2008 08:00", "11/12/2008 17:00", 1.65, 1.65);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "12/12/2008 08:00", "12/12/2008 17:00", 1.20, 1.20);
      timephased = timephasedPlanned.get(11);
      testTimephased(timephased, "15/12/2008 08:00", "15/12/2008 11:53", 0.58, 0.58);

      //
      // Back loaded assignment - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(35));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(12, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 1.05, 1.05);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "21/11/2008 17:00", 1.28, 1.28);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "24/11/2008 08:00", "24/11/2008 17:00", 1.80, 1.80);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "25/11/2008 08:00", "25/11/2008 17:00", 2.28, 2.28);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 17:00", 3.0, 3.0);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "27/11/2008 08:00", "27/11/2008 17:00", 5.0, 5.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "28/11/2008 08:00", "01/12/2008 17:00", 12.0, 6.0);
      timephased = timephasedPlanned.get(7);
      testTimephased(timephased, "02/12/2008 08:00", "02/12/2008 17:00", 6.58, 6.58);
      timephased = timephasedPlanned.get(8);
      testTimephased(timephased, "03/12/2008 08:00", "05/12/2008 17:00", 27.0, 9.0);
      timephased = timephasedPlanned.get(9);
      testTimephased(timephased, "08/12/2008 08:00", "08/12/2008 17:00", 11.17, 11.17);
      timephased = timephasedPlanned.get(10);
      testTimephased(timephased, "09/12/2008 08:00", "12/12/2008 17:00", 48.0, 12.0);
      timephased = timephasedPlanned.get(11);
      testTimephased(timephased, "15/12/2008 08:00", "15/12/2008 11:53", 5.83, 5.83);

      //
      // 50% complete task - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(36));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(3, timephasedComplete.size());
      assertEquals(3, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 10.5, 10.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "24/11/2008 17:00", 24.0, 12.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "25/11/2008 08:00", "25/11/2008 10:00", 3.0, 3.0);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "25/11/2008 10:00", "25/11/2008 17:00", 9.0, 9.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "26/11/2008 08:00", "27/11/2008 17:00", 24.0, 12.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "28/11/2008 08:00", "28/11/2008 11:00", 4.5, 4.5);

      //
      // Split task with no work done - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(37));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(0, timephasedComplete.size());
      assertEquals(7, timephasedPlanned.size());
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 10.5, 10.5);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "21/11/2008 17:00", 12.0, 12.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "24/11/2008 08:00", "24/11/2008 15:20", 9.5, 9.5);
      timephased = timephasedPlanned.get(3);
      testTimephased(timephased, "25/11/2008 08:00", "26/11/2008 17:00", 0.0, 0.0);
      timephased = timephasedPlanned.get(4);
      testTimephased(timephased, "27/11/2008 15:20", "27/11/2008 17:00", 2.5, 2.5);
      timephased = timephasedPlanned.get(5);
      testTimephased(timephased, "28/11/2008 08:00", "02/12/2008 17:00", 36.0, 12.0);
      timephased = timephasedPlanned.get(6);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 11:00", 4.5, 4.5);

      //
      // Split task with some work done - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(38));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(6, timephasedComplete.size());
      assertEquals(3, timephasedPlanned.size());

      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 10.5, 10.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "21/11/2008 17:00", 12.0, 12.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "24/11/2008 08:00", "24/11/2008 15:20", 9.5, 9.5);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "25/11/2008 08:00", "26/11/2008 17:00", 0.0, 0.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "27/11/2008 14:20", "27/11/2008 17:00", 4.0, 4.0);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "28/11/2008 08:00", "28/11/2008 15:00", 9.0, 9.0);
      timephased = timephasedPlanned.get(0);
      testTimephased(timephased, "28/11/2008 15:00", "28/11/2008 17:00", 3.0, 3.0);
      timephased = timephasedPlanned.get(1);
      testTimephased(timephased, "01/12/2008 08:00", "02/12/2008 17:00", 24.0, 12.0);
      timephased = timephasedPlanned.get(2);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 10:00", 3.0, 3.0);

      //
      // Normal task 100% complete - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(39));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(3, timephasedComplete.size());
      assertEquals(0, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 10.5, 10.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "02/12/2008 17:00", 96.0, 12.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "03/12/2008 08:00", "03/12/2008 12:00", 6.0, 6.0);

      //
      // Split task 100% complete - 150% utilisation
      //
      task = file.getTaskByID(Integer.valueOf(40));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedWork();
      timephasedComplete = assignment.getTimephasedActualWork();
      assertEquals(7, timephasedComplete.size());
      assertEquals(0, timephasedPlanned.size());
      timephased = timephasedComplete.get(0);
      testTimephased(timephased, "20/11/2008 09:00", "20/11/2008 17:00", 10.5, 10.5);
      timephased = timephasedComplete.get(1);
      testTimephased(timephased, "21/11/2008 08:00", "25/11/2008 17:00", 36.0, 12.0);
      timephased = timephasedComplete.get(2);
      testTimephased(timephased, "26/11/2008 08:00", "26/11/2008 09:00", 1.5, 1.5);
      timephased = timephasedComplete.get(3);
      testTimephased(timephased, "27/11/2008 08:00", "28/11/2008 17:00", 0.0, 0.0);
      timephased = timephasedComplete.get(4);
      testTimephased(timephased, "01/12/2008 09:00", "01/12/2008 17:00", 10.5, 10.5);
      timephased = timephasedComplete.get(5);
      testTimephased(timephased, "02/12/2008 08:00", "05/12/2008 17:00", 48.0, 12.0);
      timephased = timephasedComplete.get(6);
      testTimephased(timephased, "08/12/2008 08:00", "08/12/2008 12:00", 6.0, 6.0);
   }

   /**
    * Utility method to test the attributes of a timephased resource
    * assignment.
    *
    * @param assignment TimephasedWork instance to test
    * @param start start date for this assignment
    * @param finish finish date for this assignment
    * @param totalWork total work for this assignment
    * @param workPerDay work per day for this assignment
    */
   private void testTimephased(TimephasedWork assignment, String start, String finish, double totalWork, double workPerDay)
   {
      assertEquals(start, m_df.format(assignment.getStart()));
      assertEquals(finish, m_df.format(assignment.getFinish()));
      assertEquals(totalWork, assignment.getTotalAmount().getDuration(), 0.02);
      assertEquals(TimeUnit.HOURS, assignment.getTotalAmount().getUnits());
      if (workPerDay != -1)
      {
         assertEquals(workPerDay, assignment.getAmountPerDay().getDuration(), 0.02);
         assertEquals(TimeUnit.HOURS, assignment.getAmountPerDay().getUnits());
      }
   }

   //createTest("timephasedPlanned", timephasedPlanned);
   //createTest("timephasedComplete", timephasedComplete);

   /*
                  private void createTest(String name, List<TimephasedWork> assignments)
                  {
                     int index = 0;
                     for (TimephasedWork assignment : assignments)
                     {
                        System.out.println("timephased = " + name + ".get(" + index + ");");
                        System.out.println("testTimephased(timephased, \"" + m_df.format(assignment.getStart()) + "\", \"" + m_df.format(assignment.getFinish()) + "\", " + assignment.getTotalWork().getDuration() + ", " + assignment.getWorkPerDay().getDuration() + ");");
                        ++index;
                     }
                     System.out.println();
                     System.out.println();
                  }
   */

   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
