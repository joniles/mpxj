/*
 * file:       AssignmentAssignmentsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       10/03/2016
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

package net.sf.mpxj.junit.assignment;

import static net.sf.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.Test;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

/**
 * Tests to ensure basic assignment details are read correctly.
 */
public class AssignmentAssignmentsTest
{
   /**
    * Test to validate calendars in files saved by different versions of MS Project.
    */
   @Test public void testAssignments() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/assignment-assignments", "assignment-assignments"))
      {
         testAssignments(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testAssignments(File file) throws MPXJException
   {
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      if (reader instanceof MPDDatabaseReader)
      {
         assumeJvm();
      }

      ProjectFile project = reader.read(file);

      Task task1 = project.getTaskByID(Integer.valueOf(1));
      assertEquals("Task 1", task1.getName());
      assertEquals(1, task1.getResourceAssignments().size());
      ResourceAssignment assignment1 = task1.getResourceAssignments().get(0);

      if (assignment1.getStop() != null)
      {
         assertEquals("04/01/2016 08:00", m_dateFormat.format(assignment1.getStop()));
      }

      if (assignment1.getResume() != null)
      {
         assertEquals("04/01/2016 08:00", m_dateFormat.format(assignment1.getResume()));
      }

      Task task2 = project.getTaskByID(Integer.valueOf(2));
      assertEquals("Task 2", task2.getName());
      assertEquals(1, task2.getResourceAssignments().size());
      ResourceAssignment assignment2 = task2.getResourceAssignments().get(0);
      assertEquals("06/01/2016 12:00", m_dateFormat.format(assignment2.getStop()));
      assertEquals("06/01/2016 13:00", m_dateFormat.format(assignment2.getResume()));

   }

   private DateFormat m_dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
}
