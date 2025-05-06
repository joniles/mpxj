/*
 * file:       TaskBaselineValuesTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       07/02/2014
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
import java.time.format.DateTimeFormatter;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.AccrueType;
import org.mpxj.Duration;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Task;
import org.mpxj.common.NumberHelper;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.mpp.ApplicationVersion;

/**
 * Tests to ensure task baseline values are correctly handled.
 */
public class TaskBaselinesTest
{

   /**
    * Test to verify SourceForge issue is fixed.
    */
   @Test public void testSourceForgeIssue259() throws MPXJException
   {
      File file = new File(MpxjTestData.filePath("generated/task-baselines"), "sf259.mpp");
      ProjectFile project = new UniversalProjectReader().read(file);
      for (int index = 0; index < SF259_BASELINE_STARTS.length; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index + 1));
         assertEquals(SF259_BASELINE_STARTS[index], m_dateFormat.format(task.getBaselineStart()));
         assertEquals(SF259_BASELINE_FINISHES[index], m_dateFormat.format(task.getBaselineFinish()));
      }
   }

   /**
    * Test to validate the baseline values saved by different versions of MS Project.
    */
   @Test public void testTaskBaselineValues() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-baselines", "task-baselines"))
      {
         testTaskBaselineValues(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskBaselineValues(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);

      int startTaskID = 1;
      int maxBaselines = file.getName().contains("project98") || file.getName().contains("project2000") ? 1 : 11;
      startTaskID = testCosts(project, startTaskID, maxBaselines);
      startTaskID = testDurations(project, startTaskID, maxBaselines);
      startTaskID = testFinishes(project, startTaskID, maxBaselines);
      startTaskID = testStarts(project, startTaskID, maxBaselines);
      startTaskID = testWorks(project, startTaskID, maxBaselines);

      //
      // Handle different file content depending on which application and file version have been used
      //
      ProjectProperties properties = project.getProjectProperties();
      if (NumberHelper.getInt(properties.getApplicationVersion()) >= ApplicationVersion.PROJECT_2010 && NumberHelper.getInt(properties.getMppFileType()) >= 14)
      {
         startTaskID = testEstimatedDurations(project, startTaskID, maxBaselines);
         startTaskID = testEstimatedFinishes(project, startTaskID, maxBaselines);
         startTaskID = testEstimatedStarts(project, startTaskID, maxBaselines);
         startTaskID = testFixedCosts(project, startTaskID, maxBaselines);
         testFixedCostAccruals(project, startTaskID, maxBaselines);
      }
   }

   /**
    * Test baseline costs.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testCosts(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         Number value;

         if (index == 0)
         {
            value = task.getBaselineCost();
         }
         else
         {
            value = task.getBaselineCost(index);
         }

         assertEquals(COSTS[index], value.toString());
      }

      return taskID;
   }

   /**
    * Test baseline durations.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testDurations(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         Duration value;

         if (index == 0)
         {
            value = task.getBaselineDuration();
         }
         else
         {
            value = task.getBaselineDuration(index);
         }

         assertEquals("Baseline" + index, DURATIONS[index], value.toString());
      }

      return taskID;
   }

   /**
    * Test baseline estimated durations.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testEstimatedDurations(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         Duration value;

         if (index == 0)
         {
            value = task.getBaselineEstimatedDuration();
         }
         else
         {
            value = task.getBaselineEstimatedDuration(index);
         }

         assertEquals(ESTIMATED_DURATIONS[index], value.toString());
      }

      return taskID;
   }

   /**
    * Test baseline estimated finishes.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testEstimatedFinishes(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         LocalDateTime value;

         if (index == 0)
         {
            value = task.getBaselineEstimatedFinish();
         }
         else
         {
            value = task.getBaselineEstimatedFinish(index);
         }

         assertEquals(ESTIMATED_FINISHES[index], m_dateFormat.format(value));
      }

      return taskID;
   }

   /**
    * Test baseline estimated starts.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testEstimatedStarts(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         LocalDateTime value;

         if (index == 0)
         {
            value = task.getBaselineEstimatedStart();
         }
         else
         {
            value = task.getBaselineEstimatedStart(index);
         }

         assertEquals(ESTIMATED_STARTS[index], m_dateFormat.format(value));
      }

      return taskID;
   }

   /**
    * Test baseline finishes.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testFinishes(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         LocalDateTime value;

         if (index == 0)
         {
            value = task.getBaselineFinish();
         }
         else
         {
            value = task.getBaselineFinish(index);
         }

         assertEquals(FINISHES[index], m_dateFormat.format(value));
      }

      return taskID;
   }

   /**
    * Test baseline fixed costs.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testFixedCosts(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         Number value;

         if (index == 0)
         {
            value = task.getBaselineFixedCost();
         }
         else
         {
            value = task.getBaselineFixedCost(index);
         }

         assertEquals(FIXED_COSTS[index], value.toString());
      }

      return taskID;
   }

   /**
    * Test baseline fixed cost accruals.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testFixedCostAccruals(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         AccrueType value;

         if (index == 0)
         {
            value = task.getBaselineFixedCostAccrual();
         }
         else
         {
            value = task.getBaselineFixedCostAccrual(index);
         }

         assertEquals(FIXED_COST_ACCRUALS[index], value.toString());
      }

      return taskID;
   }

   /**
    * Test baseline starts.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testStarts(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         LocalDateTime value;

         if (index == 0)
         {
            value = task.getBaselineStart();
         }
         else
         {
            value = task.getBaselineStart(index);
         }

         assertEquals(STARTS[index], m_dateFormat.format(value));
      }

      return taskID;
   }

   /**
    * Test baseline works.
    *
    * @param project project
    * @param startTaskID initial task ID
    * @param maxBaselines maximum baselines to test
    * @return task ID for next tests
    */
   private int testWorks(ProjectFile project, int startTaskID, int maxBaselines)
   {
      int taskID = startTaskID;

      for (int index = 0; index < maxBaselines; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(taskID));
         taskID++;
         Duration value;

         if (index == 0)
         {
            value = task.getBaselineWork();
         }
         else
         {
            value = task.getBaselineWork(index);
         }

         assertEquals(WORKS[index], value.toString());
      }

      return taskID;
   }

   private final DateTimeFormatter m_dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

   private static final String[] COSTS = new String[]
   {
      "1.0",
      "2.0",
      "3.0",
      "4.0",
      "5.0",
      "6.0",
      "7.0",
      "8.0",
      "9.0",
      "10.0",
      "11.0"
   };

   private static final String[] DURATIONS = new String[]
   {
      "11.0d",
      "12.0d",
      "13.0d",
      "14.0d",
      "15.0d",
      "16.0d",
      "17.0d",
      "18.0d",
      "19.0d",
      "20.0d",
      "21.0d"
   };

   private static final String[] ESTIMATED_DURATIONS = new String[]
   {
      "31.0d",
      "32.0d",
      "33.0d",
      "34.0d",
      "35.0d",
      "36.0d",
      "37.0d",
      "38.0d",
      "39.0d",
      "40.0d",
      "41.0d"
   };

   private static final String[] ESTIMATED_FINISHES = new String[]
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
      "10/01/2014 18:00",
      "10/01/2014 19:00"
   };

   private static final String[] ESTIMATED_STARTS = new String[]
   {
      "01/02/2014 09:00",
      "02/02/2014 10:00",
      "03/02/2014 11:00",
      "04/02/2014 12:00",
      "05/02/2014 13:00",
      "06/02/2014 14:00",
      "07/02/2014 15:00",
      "08/02/2014 16:00",
      "09/02/2014 17:00",
      "10/02/2014 18:00",
      "10/02/2014 19:00"
   };

   private static final String[] FINISHES = new String[]
   {
      "01/03/2014 09:00",
      "02/03/2014 10:00",
      "03/03/2014 11:00",
      "04/03/2014 12:00",
      "05/03/2014 13:00",
      "06/03/2014 14:00",
      "07/03/2014 15:00",
      "08/03/2014 16:00",
      "09/03/2014 17:00",
      "10/03/2014 18:00",
      "10/03/2014 19:00"
   };

   private static final String[] FIXED_COSTS = new String[]
   {
      "11.0",
      "12.0",
      "13.0",
      "14.0",
      "15.0",
      "16.0",
      "17.0",
      "18.0",
      "19.0",
      "20.0",
      "21.0"
   };

   private static final String[] FIXED_COST_ACCRUALS = new String[]
   {
      "START",
      "PRORATED",
      "END",
      "START",
      "PRORATED",
      "END",
      "START",
      "PRORATED",
      "END",
      "START",
      "PRORATED"
   };

   private static final String[] STARTS = new String[]
   {
      "01/04/2014 09:00",
      "02/04/2014 10:00",
      "03/04/2014 11:00",
      "04/04/2014 12:00",
      "05/04/2014 13:00",
      "06/04/2014 14:00",
      "07/04/2014 15:00",
      "08/04/2014 16:00",
      "09/04/2014 17:00",
      "10/04/2014 18:00",
      "10/04/2014 19:00"
   };

   private static final String[] WORKS = new String[]
   {
      "51.0h",
      "52.0h",
      "53.0h",
      "54.0h",
      "55.0h",
      "56.0h",
      "57.0h",
      "58.0h",
      "59.0h",
      "60.0h",
      "61.0h"
   };

   private static final String[] SF259_BASELINE_STARTS = new String[]
   {
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 01:00",
      "01/03/2015 01:00",
      "01/03/2015 01:00",
      "01/03/2015 01:00",
      "01/03/2015 01:00",
      "01/03/2015 01:00",
      "01/03/2015 03:00"
   };

   private static final String[] SF259_BASELINE_FINISHES = new String[]
   {
      "01/03/2015 04:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 00:00",
      "01/03/2015 04:00",
      "01/03/2015 04:00",
      "01/03/2015 00:00",
      "01/03/2015 01:00",
      "01/03/2015 03:00",
      "01/03/2015 03:00",
      "01/03/2015 02:30",
      "01/03/2015 02:30",
      "01/03/2015 03:00",
      "01/03/2015 03:00",
      "01/03/2015 04:00"
   };

}
