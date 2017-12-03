/*
 * file:       TaskDurationsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       17/10/2014
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

import org.junit.Test;

import net.sf.mpxj.Duration;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

/**
 * Tests to ensure task custom durations are correctly handled.
 */
public class TaskDurationsTest
{
   /**
    * Test to validate the custom durations in files saved by different versions of MS Project.
    */
   @Test public void testTaskDurations() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-durations", "task-durations"))
      {
         ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
         if (reader instanceof MPDDatabaseReader)
         {
            assumeJvm();
         }

         ProjectFile project = reader.read(file);
         testDurationValues(file, reader, project);
         testDurationUnits(file, reader, project);
      }
   }

   /**
    * Test duration values.
    *
    * @param file project file
    * @param reader reader used to parse the file
    * @param project project file
    */
   private void testDurationValues(File file, ProjectReader reader, ProjectFile project)
   {
      int maxIndex = reader instanceof MPXReader ? 3 : 10;
      for (int index = 1; index <= maxIndex; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Duration" + index, task.getName());
         testTaskDurations(file, task, index, maxIndex);
      }
   }

   /**
    * Test the duration values for a task.
    *
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testTaskDurations(File file, Task task, int testIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         String expectedValue = testIndex == index ? index + ".0d" : "0.0d";
         String actualValue = task.getDuration(index) == null ? "0.0d" : task.getDuration(index).toString();

         assertEquals(file.getName() + " " + task.getName() + " Duration" + index, expectedValue, actualValue);
      }
   }

   /**
    * Test duration units.
    *
    * @param file project file
    * @param reader reader used to parse the file
    * @param project project file
    */
   private void testDurationUnits(File file, ProjectReader reader, ProjectFile project)
   {
      TimeUnit[] units = (NumberHelper.getInt(project.getProjectProperties().getMppFileType()) == 8 || reader instanceof MPXReader) ? UNITS_PROJECT98 : UNITS_PROJECT2000;
      int maxIndex = reader instanceof MPXReader ? 3 : 10;

      int taskID = 11;
      for (int fieldIndex = 1; fieldIndex <= maxIndex; fieldIndex++)
      {
         for (int unitsIndex = 0; unitsIndex < units.length; unitsIndex++)
         {
            Task task = project.getTaskByID(Integer.valueOf(taskID));
            String expectedTaskName = "Duration" + fieldIndex + " - Task " + unitsIndex;
            assertEquals(expectedTaskName, task.getName());
            Duration duration = task.getDuration(fieldIndex);
            assertEquals(file.getName() + " " + expectedTaskName, units[unitsIndex], duration.getUnits());
            ++taskID;
         }
      }
   }

   private static final TimeUnit[] UNITS_PROJECT98 =
   {
      TimeUnit.MINUTES,
      TimeUnit.HOURS,
      TimeUnit.DAYS,
      TimeUnit.WEEKS,
      TimeUnit.ELAPSED_MINUTES,
      TimeUnit.ELAPSED_HOURS,
      TimeUnit.ELAPSED_DAYS,
      TimeUnit.ELAPSED_WEEKS
   };
   private static final TimeUnit[] UNITS_PROJECT2000 =
   {
      TimeUnit.MINUTES,
      TimeUnit.HOURS,
      TimeUnit.DAYS,
      TimeUnit.WEEKS,
      TimeUnit.MONTHS,
      TimeUnit.ELAPSED_MINUTES,
      TimeUnit.ELAPSED_HOURS,
      TimeUnit.ELAPSED_DAYS,
      TimeUnit.ELAPSED_WEEKS,
      TimeUnit.ELAPSED_MONTHS
   };
}
