/*
 * file:       ResourceFlagsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       08/03/2017
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

package org.mpxj.junit.resource;

import static org.junit.Assert.*;

import java.io.File;

import org.mpxj.junit.ProjectUtility;
import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure task custom flags are correctly handled.
 */
public class ResourceFlagsTest
{
   /**
    * Test to validate the custom flags in files saved by different versions of MS Project.
    */
   @Test public void testResourceFlags() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/resource-flags", "resource-flags"))
      {
         testResourceFlags(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testResourceFlags(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      int maxIndex = ProjectUtility.projectIs(project, "MPX") ? 10 : 20;

      for (int index = 1; index <= maxIndex; index++)
      {
         Resource resource = project.getResourceByID(Integer.valueOf(index));
         assertEquals("Flag" + index, resource.getName());
         testResourceFlags(file, resource, index, maxIndex);
      }
   }

   /**
    * Test the flag values for a resource.
    *
    * @param file parent file
    * @param resource resource
    * @param trueFlagIndex index of flag which is expected to be true
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testResourceFlags(File file, Resource resource, int trueFlagIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         assertEquals(file.getName() + " Flag" + index + "(true=" + trueFlagIndex + ")", Boolean.valueOf(index == trueFlagIndex), Boolean.valueOf(resource.getFlag(index)));
      }
   }
}
