/*
 * file:       ResourceTextTest.java
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
 * Tests to ensure resource custom text fields are correctly handled.
 */
public class ResourceTextTest
{
   /**
    * Test to validate the custom text fields in files saved by different versions of MS Project.
    */
   @Test public void testResourceText() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/resource-text", "resource-text"))
      {
         testResourceText(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testResourceText(File file) throws MPXJException
   {
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      if (reader instanceof MPDDatabaseReader)
      {
         assumeJvm();
      }

      int maxIndex = reader instanceof MPXReader ? 10 : 30;
      ProjectFile project = reader.read(file);
      for (int index = 1; index <= maxIndex; index++)
      {
         Resource resource = project.getResourceByID(Integer.valueOf(index));
         assertEquals("Text" + index, resource.getName());
         testResourceText(file, resource, index, maxIndex);
      }
   }

   /**
    * Test the text field values for a resource.
    *
    * @param file parent file
    * @param resource resource
    * @param testIndex index of number being tested
    * @param maxIndex maximum number of custom fields to expect in this file
    */
   private void testResourceText(File file, Resource resource, int testIndex, int maxIndex)
   {
      for (int index = 1; index <= maxIndex; index++)
      {
         String expectedValue = testIndex == index ? Integer.toString(index) : null;
         String actualValue = resource.getText(index);

         assertEquals(file.getName() + " Text" + index, expectedValue, actualValue);
      }
   }
}
