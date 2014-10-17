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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

import org.junit.Test;

/**
 * Tests to ensure task custom durations are correctly handled.
 */
public class TaskDurationsTest
{
   /**
    * Test to validate the custom durations in files saved by different versions of MS Project.
    */
   @Test public void testTaskNumbers() throws MPXJException
   {
      File testDataDir = new File(MpxjTestData.filePath("generated/task-durations"));
      for (File file : testDataDir.listFiles(new FileFilter()
      {
         @Override public boolean accept(File pathname)
         {
            return pathname.getName().startsWith("task-durations");
         }
      }))
      {
         testTaskDurations(file);
      }
   }

   /**
    * Test an individual project.
    * 
    * @param file project file
    */
   private void testTaskDurations(File file) throws MPXJException
   {
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      int maxDurations = reader instanceof MPXReader ? 3 : 10;

      ProjectFile project = reader.read(file);
      for (int index = 1; index <= maxDurations; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Duration" + index, task.getName());
         testTaskDurations(file, task, index);
      }
   }

   /**
    * Test the duration values for a task.
    * 
    * @param file parent file
    * @param task task
    * @param testIndex index of number being tested
    */
   private void testTaskDurations(File file, Task task, int testIndex)
   {
      for (int index = 1; index <= 10; index++)
      {
         String expectedValue = testIndex == index ? index + ".0d" : "0.0d";
         String actualValue = task.getDuration(index) == null ? "0.0d" : task.getDuration(index).toString();

         assertEquals(file.getName() + " " + task.getName() + " Duration" + index, expectedValue, actualValue);
      }
   }
}
