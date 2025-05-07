/*
 * file:       MppGraphIndTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       24-Feb-2006
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

import java.util.List;

import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.GraphicalIndicator;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * The tests contained in this class exercise the graphical indicator
 * evaluation code.
 */
public class MppGraphIndTest
{
   /**
    * Test the graphical indicator evaluation code for an MPP9 file.
    */
   @Test public void testMpp9GraphInd() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp9graphind.mpp"));
      testGraphicalIndicators(project);
   }

   /**
    * Test the graphical indicator evaluation code for an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9GraphIndFrom12() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp9graphind-from12.mpp"));
      testGraphicalIndicators(project);
   }

   /**
    * Test the graphical indicator evaluation code for an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9GraphIndFrom14() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp9graphind-from14.mpp"));
      testGraphicalIndicators(project);
   }

   /**
    * Test the graphical indicator evaluation code for an MPP12 file.
    */
   @Test public void testMpp12GraphInd() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp12graphind.mpp"));
      testGraphicalIndicators(project);
   }

   /**
    * Test the graphical indicator evaluation code for an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12GraphIndFrom14() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp12graphind-from14.mpp"));
      testGraphicalIndicators(project);
   }

   /**
    * Test the graphical indicator evaluation code for an MPP14 file.
    */
   @Test public void testMpp14GraphInd() throws Exception
   {
      ProjectFile project = new MPPReader().read(MpxjTestData.filePath("mpp14graphind.mpp"));
      testGraphicalIndicators(project);
   }

   /**
    * Common graphical indicator tests.
    *
    * @param project project to test
    */
   private void testGraphicalIndicators(ProjectFile project)
   {
      List<Task> taskList = project.getTasks();
      Task[] tasks = taskList.toArray(new Task[0]);

      testIndicator(project, TaskField.COST1, tasks, COST1_RESULTS);
      testIndicator(project, TaskField.COST2, tasks, COST2_RESULTS);
      testIndicator(project, TaskField.COST3, tasks, COST3_RESULTS);
      testIndicator(project, TaskField.COST4, tasks, COST4_RESULTS);

      testIndicator(project, TaskField.DATE1, tasks, DATE1_RESULTS);
      testIndicator(project, TaskField.DATE2, tasks, DATE2_RESULTS);
      testIndicator(project, TaskField.DATE3, tasks, DATE3_RESULTS);
      testIndicator(project, TaskField.DATE4, tasks, DATE4_RESULTS);
      testIndicator(project, TaskField.DATE5, tasks, DATE5_RESULTS);

      testIndicator(project, TaskField.DURATION1, tasks, DURATION1_RESULTS);
      testIndicator(project, TaskField.DURATION2, tasks, DURATION2_RESULTS);
      testIndicator(project, TaskField.DURATION3, tasks, DURATION3_RESULTS);
      testIndicator(project, TaskField.DURATION4, tasks, DURATION4_RESULTS);

      testIndicator(project, TaskField.FLAG1, tasks, FLAG_RESULTS);
      testIndicator(project, TaskField.FLAG2, tasks, FLAG_RESULTS);
      testIndicator(project, TaskField.FLAG3, tasks, FLAG_RESULTS);

      testIndicator(project, TaskField.NUMBER1, tasks, NUMBER1_RESULTS);
      testIndicator(project, TaskField.NUMBER2, tasks, NUMBER2_RESULTS);
      testIndicator(project, TaskField.NUMBER3, tasks, NUMBER3_RESULTS);
      testIndicator(project, TaskField.NUMBER4, tasks, NUMBER4_RESULTS);

      testIndicator(project, TaskField.TEXT1, tasks, TEXT1_RESULTS);
      testIndicator(project, TaskField.TEXT2, tasks, TEXT2_RESULTS);
      testIndicator(project, TaskField.TEXT3, tasks, TEXT3_RESULTS);
      testIndicator(project, TaskField.TEXT4, tasks, TEXT4_RESULTS);
      testIndicator(project, TaskField.TEXT5, tasks, TEXT5_RESULTS);
      testIndicator(project, TaskField.TEXT6, tasks, TEXT6_RESULTS);
      testIndicator(project, TaskField.TEXT7, tasks, TEXT7_RESULTS);
   }

   /**
    * For a particular field type, ensure that the correct set of graphical
    * indicators are being generated.
    *
    * @param project parent project
    * @param fieldType target field type
    * @param rows array of rows containing field data
    * @param expectedResults array of expected results
    */
   private void testIndicator(ProjectFile project, FieldType fieldType, FieldContainer[] rows, int[] expectedResults)
   {
      GraphicalIndicator indicator = project.getCustomFields().get(fieldType).getGraphicalIndicator();
      for (int loop = 0; loop < expectedResults.length; loop++)
      {
         int value = indicator.evaluate(rows[loop]);
         assertEquals("Testing " + fieldType + " row " + loop, expectedResults[loop], value);
      }
   }

   private static final int NONE = 0;
   private static final int GREEN_BALL = 1;
   private static final int AMBER_BALL = 2;
   private static final int RED_BALL = 3;
   private static final int BLACK_BALL = 4;
   private static final int WHITE_BALL = 5;

   private static final int[] COST1_RESULTS =
   {
      BLACK_BALL,
      GREEN_BALL,
      AMBER_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL
   };

   private static final int[] COST2_RESULTS =
   {
      WHITE_BALL,
      AMBER_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      BLACK_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL
   };

   private static final int[] COST3_RESULTS =
   {
      RED_BALL,
      RED_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      NONE,
      NONE,
      RED_BALL,
      RED_BALL
   };

   private static final int[] COST4_RESULTS =
   {
      GREEN_BALL,
      AMBER_BALL,
      GREEN_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      GREEN_BALL
   };

   private static final int[] DATE1_RESULTS =
   {
      RED_BALL,
      RED_BALL,
      GREEN_BALL,
      NONE,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL
   };

   private static final int[] DATE2_RESULTS =
   {
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      AMBER_BALL,
      NONE,
      NONE,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL
   };

   private static final int[] DATE3_RESULTS =
   {
      AMBER_BALL,
      AMBER_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      AMBER_BALL,
      AMBER_BALL,
      NONE,
      NONE,
      NONE,
      NONE,
      AMBER_BALL,
      AMBER_BALL
   };

   private static final int[] DATE4_RESULTS =
   {
      GREEN_BALL,
      RED_BALL,
      GREEN_BALL,
      RED_BALL,
      AMBER_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      GREEN_BALL
   };

   private static final int[] DATE5_RESULTS =
   {
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL
   };

   private static final int[] DURATION1_RESULTS =
   {
      AMBER_BALL,
      GREEN_BALL,
      AMBER_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL
   };

   private static final int[] DURATION2_RESULTS =
   {
      WHITE_BALL,
      AMBER_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      BLACK_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL
   };

   private static final int[] DURATION3_RESULTS =
   {
      RED_BALL,
      RED_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      NONE,
      NONE,
      RED_BALL,
      RED_BALL
   };

   private static final int[] DURATION4_RESULTS =
   {
      GREEN_BALL,
      AMBER_BALL,
      GREEN_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      GREEN_BALL
   };

   private static final int[] FLAG_RESULTS =
   {
      RED_BALL,
      GREEN_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL
   };

   private static final int[] NUMBER1_RESULTS =
   {
      AMBER_BALL,
      GREEN_BALL,
      AMBER_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL
   };

   private static final int[] NUMBER2_RESULTS =
   {
      WHITE_BALL,
      AMBER_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      BLACK_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL
   };

   private static final int[] NUMBER3_RESULTS =
   {
      RED_BALL,
      RED_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      NONE,
      NONE,
      RED_BALL,
      RED_BALL
   };

   private static final int[] NUMBER4_RESULTS =
   {
      GREEN_BALL,
      AMBER_BALL,
      GREEN_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      GREEN_BALL
   };

   private static final int[] TEXT1_RESULTS =
   {
      AMBER_BALL,
      GREEN_BALL,
      AMBER_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL
   };

   private static final int[] TEXT2_RESULTS =
   {
      WHITE_BALL,
      AMBER_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      BLACK_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL,
      WHITE_BALL
   };

   private static final int[] TEXT3_RESULTS =
   {
      RED_BALL,
      RED_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      RED_BALL,
      RED_BALL,
      NONE,
      NONE,
      NONE,
      NONE,
      RED_BALL,
      RED_BALL
   };

   private static final int[] TEXT4_RESULTS =
   {
      GREEN_BALL,
      AMBER_BALL,
      GREEN_BALL,
      RED_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      AMBER_BALL,
      GREEN_BALL
   };

   private static final int[] TEXT5_RESULTS =
   {
      RED_BALL,
      GREEN_BALL,
      GREEN_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL,
      RED_BALL
   };

   private static final int[] TEXT6_RESULTS =
   {
      NONE,
      GREEN_BALL,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE,
      NONE
   };

   private static final int[] TEXT7_RESULTS =
   {
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL,
      GREEN_BALL
   };

}
