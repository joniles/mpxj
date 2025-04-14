/*
 * file:       TaskStartsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       03/11/2014
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

package org.mpxj.junit.task;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.mpxj.junit.ProjectUtility;
import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure task custom start dates are correctly handled.
 */
public class TaskStartsTest
{
   /**
    * Test to validate the custom start dates in files saved by different versions of MS Project.
    */
   @Test public void testTaskStartDates() throws Exception
   {
      for (File file : MpxjTestData.listFiles("generated/task-starts", "task-starts"))
      {
         testTaskStartDates(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskStartDates(File file) throws Exception
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      boolean isMpxFile = ProjectUtility.projectIs(project, "MPX");
      int maxIndex = isMpxFile ? 5 : 10;

      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Start" + index, task.getName());
         testTaskStartDates(file, task, index, maxIndex, isMpxFile);
      }
   }

   /**
    * Test the start date values for a task.
    *
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    * @param maxIndex highest index to test
    * @param useDateFormat true=use date-only format false=use date time format
    */
   private void testTaskStartDates(File file, Task task, int testIndex, int maxIndex, boolean useDateFormat)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         LocalDateTime expectedValue = testIndex == index ? LocalDateTime.parse(DATES[index - 1], m_dateTimeFormat) : null;
         if (useDateFormat && expectedValue != null)
         {
            expectedValue = LocalDateTime.of(expectedValue.toLocalDate(), LocalTime.MIDNIGHT);
         }
         LocalDateTime actualValue = task.getStart(index);

         assertEquals(file.getName() + " Start" + index, expectedValue, actualValue);
      }
   }

   private final DateTimeFormatter m_dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

   private static final String[] DATES = new String[]
   {
      "01/01/2014 09:00",
      "02/01/2014 10:00",
      "03/01/2014 11:00",
      "04/01/2014 12:00",
      "05/01/2014 13:00",
      "06/01/2014 14:00",
      "07/01/2014 15:00",
      "08/01/2014 16:00",
      "09/01/2014 17:00",
      "10/01/2014 18:00"
   };
}
