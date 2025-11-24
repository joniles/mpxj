/*
 * file:       TaskTextValuesTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       14/11/2014
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



import java.io.File;

import org.junit.jupiter.api.Test;
import org.mpxj.reader.UniversalProjectReader;


import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.junit.MpxjTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to ensure the text versions of Start, Finish and Duration are read correctly.
 */
public class TaskTextValuesTest
{
   /**
    * Tests to ensure the text versions of Start, Finish and Duration are read correctly.
    */
   @Test public void testTaskTextValues() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-textvalues", "task-textvalues"))
      {
         testTaskTextValues(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testTaskTextValues(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      assertEquals(EXPECTED_VALUES.length + 1, project.getTasks().size(), file.getName() + " number of tasks");
      for (int loop = 0; loop < EXPECTED_VALUES.length; loop++)
      {
         Task task = project.getTaskByID(Integer.valueOf(loop + 1));
         assertEquals(EXPECTED_VALUES[loop][0], task.getName(), file.getName() + " task name");
         assertEquals(EXPECTED_VALUES[loop][1], task.getStartText(), file.getName() + " start text");
         assertEquals(EXPECTED_VALUES[loop][2], task.getFinishText(), file.getName() + " finish text");
         assertEquals(EXPECTED_VALUES[loop][3], task.getDurationText(), file.getName() + " duration text");
      }
   }

   private static final String[][] EXPECTED_VALUES =
   {
      {
         "Start is text",
         "AAA",
         "",
         ""
      },
      {
         "Finish is text",
         "",
         "BBB",
         ""
      },
      {
         "Duration is text",
         "",
         "",
         "CCC"
      }
   };
}
