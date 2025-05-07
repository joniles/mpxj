/*
 * file:       TaskPercentCompleteTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       25/02/2015
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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.primavera.PrimaveraPMFileReader;
import org.mpxj.primavera.PrimaveraXERFileReader;

/**
 * Tests to ensure task baseline values are correctly handled.
 */
public class TaskPercentCompleteTest
{
   /**
    * Test to validate Primavera percent complete values.
    */
   @Test public void testPrimaveraPercentComplete() throws MPXJException
   {
      File testDataDir = new File(MpxjTestData.filePath("generated/task-percentcomplete"));
      ProjectFile project = new PrimaveraXERFileReader().read(new File(testDataDir, "percent-complete-8.4.xer"));
      testPrimaveraPercentComplete(project, 2);

      project = new PrimaveraPMFileReader().read(new File(testDataDir, "percent-complete-8.4.pmxml"));
      testPrimaveraPercentComplete(project, 1);
   }

   /**
    * Validate the percent complete values.
    *
    * @param project parent project
    * @param startTaskID initial task ID
    */
   private void testPrimaveraPercentComplete(ProjectFile project, int startTaskID)
   {
      int taskID = startTaskID;

      testPercentComplete(project, taskID++, "Duration 0%", TaskField.PERCENT_COMPLETE, 0);
      testPercentComplete(project, taskID++, "Duration 25%", TaskField.PERCENT_COMPLETE, 25);
      testPercentComplete(project, taskID++, "Duration 75%", TaskField.PERCENT_COMPLETE, 75);
      testPercentComplete(project, taskID++, "Duration 100%", TaskField.PERCENT_COMPLETE, 100);

      testPercentComplete(project, taskID++, "Physical 0%", TaskField.PHYSICAL_PERCENT_COMPLETE, 0);
      testPercentComplete(project, taskID++, "Physical 25%", TaskField.PHYSICAL_PERCENT_COMPLETE, 25);
      testPercentComplete(project, taskID++, "Physical 75%", TaskField.PHYSICAL_PERCENT_COMPLETE, 75);
      testPercentComplete(project, taskID++, "Physical 100%", TaskField.PHYSICAL_PERCENT_COMPLETE, 100);

      testPercentComplete(project, taskID++, "Units 0%", TaskField.PERCENT_WORK_COMPLETE, 0);
      testPercentComplete(project, taskID++, "Units 25%", TaskField.PERCENT_WORK_COMPLETE, 25);
      testPercentComplete(project, taskID++, "Units 75%", TaskField.PERCENT_WORK_COMPLETE, 75);
      testPercentComplete(project, taskID, "Units 100%", TaskField.PERCENT_WORK_COMPLETE, 100);
   }

   /**
    * Test an individual percent complete value.
    *
    * @param project parent project
    * @param taskID ID of task to test
    * @param expectedTaskName expected task name
    * @param field percent complete field to read
    * @param expectedPercentCompleteValue expected percent complete value
    */
   private void testPercentComplete(ProjectFile project, int taskID, String expectedTaskName, TaskField field, int expectedPercentCompleteValue)
   {
      Task task = project.getTaskByID(Integer.valueOf(taskID));
      assertEquals(expectedTaskName, task.getName());
      assertEquals(expectedPercentCompleteValue, ((Number) task.getCachedValue(field)).intValue());
   }
}