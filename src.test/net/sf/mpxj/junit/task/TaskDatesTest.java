/*
 * file:       TaskDatesTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       31/10/2014
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

package net.sf.mpxj.junit.task;

import static net.sf.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

/**
 * Tests to ensure task custom dates are correctly handled.
 */
public class TaskDatesTest
{
   /**
    * Test to validate the custom dates in files saved by different versions of MS Project.
    */
   @Test public void testTaskDates() throws Exception
   {
      for (File file : MpxjTestData.listFiles("generated/task-dates", "task-dates"))
      {
         testTaskDates(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskDates(File file) throws Exception
   {
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      if (reader instanceof MPDDatabaseReader)
      {
         assumeJvm();
      }

      ProjectFile project = reader.read(file);
      int maxIndex = 10;
      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Date" + index, task.getName());
         testTaskDates(file, task, index, maxIndex);
      }
   }

   /**
    * Test the date values for a task.
    *
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testTaskDates(File file, Task task, int testIndex, int maxIndex) throws ParseException
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         Date expectedValue = testIndex == index ? m_dateFormat.parse(DATES[index - 1]) : null;
         Date actualValue = task.getDate(index);

         assertEquals(file.getName() + " Date" + index, expectedValue, actualValue);
      }
   }

   private DateFormat m_dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
