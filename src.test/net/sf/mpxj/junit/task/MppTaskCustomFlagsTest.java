
package net.sf.mpxj.junit.task;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpp.MPPReader;

import org.junit.Test;

/*
 * file:       MppTaskCustomFlagsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       02/10/2014
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

/**
 * Tests to ensure task custom flags are correctly handled.
 */
public class MppTaskCustomFlagsTest
{
   /**
    * Test to validate the custom flags in an MPP file saved by different versions of MS Project.
    */
   @Test public void testCustomTaskFlags() throws MPXJException
   {
      File testDataDir = new File(MpxjTestData.filePath("task-custom-flags"));
      for (File file : testDataDir.listFiles(new FileFilter()
      {

         @Override public boolean accept(File pathname)
         {
            return pathname.getName().startsWith("task-flags");
         }
      }))
      {
         testTaskFlags(file);
      }
   }

   /**
    * Test an individual project.
    * 
    * @param file project file
    */
   private void testTaskFlags(File file) throws MPXJException
   {
      ProjectFile project = new MPPReader().read(file);
      for (int index = 1; index <= 20; index++)
      {
         Task task = project.getTaskByID(Integer.valueOf(index));
         assertEquals("Flag" + index, task.getName());
         testTaskFlags(task, index);
      }
   }

   /**
    * Test the flag values for a task.
    * 
    * @param task task
    * @param trueFlagIndex index of flag which is expected to be true
    */
   private void testTaskFlags(Task task, int trueFlagIndex)
   {
      for (int index = 1; index <= 20; index++)
      {
         assertEquals("Flag" + index, Boolean.valueOf(index == trueFlagIndex), Boolean.valueOf(task.getFlag(index)));
      }
   }
}
