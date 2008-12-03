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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedResourceAssignment;
import net.sf.mpxj.mpp.MPPReader;

/**
 * The tests contained in this class exercise the timephased
 * resource assignment functionality.
 */
public class TimephasedTest extends MPXJTestCase
{
   /**
    * Test MPP9 file timephased resource assignments.
    * 
    * @throws Exception
    */
   public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp9timephased.mpp");
      testTimephased(file);
   }

   /**
    * Test MPP12 file timephased resource assignments.
    * 
    * @throws Exception
    */
   public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp12timephased.mpp");
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
      List<TimephasedResourceAssignment> timephasedPlanned = assignment.getTimephasedPlanned();
      assertEquals(0, timephasedPlanned.size());
      List<TimephasedResourceAssignment> timephasedComplete = assignment.getTimephasedComplete();
      assertEquals(0, timephasedComplete.size());

      //
      // Front loaded assignment
      //
      task = file.getTaskByID(Integer.valueOf(2));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedPlanned();
      assertEquals(11, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedComplete();
      assertEquals(0, timephasedComplete.size());
      TimephasedResourceAssignment timephased = timephasedPlanned.get(0);
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

      //
      // Back loaded assignment
      //
      task = file.getTaskByID(Integer.valueOf(3));
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignment = assignments.get(0);
      timephasedPlanned = assignment.getTimephasedPlanned();
      assertEquals(11, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedComplete();
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
      timephasedComplete = assignment.getTimephasedComplete();
      assertEquals(3, timephasedComplete.size());
      timephasedPlanned = assignment.getTimephasedPlanned();
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
      timephasedPlanned = assignment.getTimephasedPlanned();
      assertEquals(7, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedComplete();
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
      timephasedComplete = assignment.getTimephasedComplete();
      assertEquals(6, timephasedComplete.size());
      timephasedPlanned = assignment.getTimephasedPlanned();
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
      timephasedPlanned = assignment.getTimephasedPlanned();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedComplete();
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
      timephasedPlanned = assignment.getTimephasedPlanned();
      assertEquals(0, timephasedPlanned.size());
      timephasedComplete = assignment.getTimephasedComplete();
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

   }

   /**
    * Utility method to test the attributes of a timephased resource
    * assignment.
    * 
    * @param assignment TimephasedResourceAssignment instance to test
    * @param start start date for this assignment
    * @param finish finish date for this assignment
    * @param totalWork total work for this assignment
    * @param workPerDay work per day for this assignment
    */
   private void testTimephased(TimephasedResourceAssignment assignment, String start, String finish, double totalWork, double workPerDay)
   {
      assertEquals(start, m_df.format(assignment.getStart()));
      assertEquals(finish, m_df.format(assignment.getFinish()));
      assertEquals(totalWork, assignment.getTotalWork().getDuration(), 0.0);
      assertEquals(TimeUnit.HOURS, assignment.getTotalWork().getUnits());
      assertEquals(workPerDay, assignment.getWorkPerDay().getDuration(), 0.0);
      assertEquals(TimeUnit.HOURS, assignment.getWorkPerDay().getUnits());
   }

   //createTest("timephasedPlanned", timephasedPlanned);
   //createTest("timephasedComplete", timephasedComplete);      
   /*
      private void createTest(String name, List<TimephasedResourceAssignment> assignments)
      {
         int index = 0;
         for (TimephasedResourceAssignment assignment : assignments)
         {
            System.out.println("timephased = " + name + ".get(" + index + ");");
            System.out.println("testTimephased(timephased, \"" + m_df.format(assignment.getStart()) + "\", \"" + m_df.format(assignment.getFinish()) + "\", " + assignment.getTotalWork().getDuration() + ", " + assignment.getWorkPerDay().getDuration() + ");");
            ++index;
         }
      }
   */

   private DateFormat m_df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
}
