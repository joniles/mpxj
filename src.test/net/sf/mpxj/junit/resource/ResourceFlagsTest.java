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

package net.sf.mpxj.junit.resource;

import static net.sf.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

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
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      if (reader instanceof MPDDatabaseReader)
      {
         assumeJvm();
      }

      int maxIndex = reader instanceof MPXReader ? 10 : 20;
      ProjectFile project = reader.read(file);
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
