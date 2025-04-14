/*
 * file:       ProjectPropertiesOnlyTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       08/12/2014
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

package org.mpxj.junit.project;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.mpp.MPPReader;

/**
 * Validate the behaviour of the "properties only" MPPReader flag.
 */
public class ProjectPropertiesOnlyTest
{
   /**
    * Test to validate that we only read the project properties when the "properties only" flag is set on the reader.
    * We'll hijack the existing generated task text sample files.
    */
   @Test public void testPropertiesOnly() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/task-text", "task-text"))
      {
         if (file.getName().endsWith(".mpp"))
         {
            testPropertiesOnly(file);
         }
      }
   }

   /**
    * Test a single file to ensure that tasks are read by default, and are not read
    * when the properties only flag is set.
    *
    * @param file file to test
    */
   private void testPropertiesOnly(File file) throws MPXJException
   {
      MPPReader reader = new MPPReader();
      ProjectFile project = reader.read(file);
      assertFalse(project.getTasks().isEmpty());
      assertEquals("Project User", project.getProjectProperties().getAuthor());

      reader.setReadPropertiesOnly(true);
      project = reader.read(file);
      assertEquals(0, project.getTasks().size());
      assertEquals("Project User", project.getProjectProperties().getAuthor());
   }
}
