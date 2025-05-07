/*
 * file:       MppResourceTypeTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       21/09/2014
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
import java.util.List;

import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceType;
import org.mpxj.common.NumberHelper;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.mpp.MPPReader;

/**
 * Tests to ensure resource type is correctly handled.
 */
public class MppResourceTypeTest
{
   /**
    * Test to exercise the test case provided for SourceForge bug #235.
    * https://sourceforge.net/p/mpxj/bugs/235/
    */
   @Test public void testSourceForge235() throws MPXJException
   {
      File file = new File(MpxjTestData.filePath("resource/resource-type/sf235.mpp"));
      ProjectFile project = new MPPReader().read(file);
      testResource(file, project, 1, "Programmer 1", ResourceType.WORK);
      testResource(file, project, 2, "Programmer 2", ResourceType.WORK);
   }

   /**
    * Test to exercise the test case provided for SourceForge bug #235.
    * https://sourceforge.net/p/mpxj/bugs/256/
    */
   @Test public void testSourceForge256() throws MPXJException
   {
      File file = new File(MpxjTestData.filePath("resource/resource-type/sf256.mpp"));
      ProjectFile project = new MPPReader().read(file);
      testResource(file, project, 1, "Cost", ResourceType.COST);
      testResource(file, project, 2, "Work", ResourceType.WORK);
      testResource(file, project, 3, "Material", ResourceType.MATERIAL);
   }

   /**
    * Test to validate the resource types in an MPP file saved by different versions of MS Project.
    */
   @Test public void testResourceType() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("resource/resource-type", "resource-type"))
      {
         testResourceType(file);
      }
   }

   /**
    * Test the resource types present in an individual MPP file.
    *
    * @param file MPP file to test
    */
   private void testResourceType(File file) throws MPXJException
   {
      ProjectFile project = new MPPReader().read(file);
      List<Resource> resources = project.getResources();
      assertEquals(10, resources.size());

      testResource(file, project, 1, "Work 1", ResourceType.WORK);
      testResource(file, project, 2, "Work 2", ResourceType.WORK);
      testResource(file, project, 3, "Work 3", ResourceType.WORK);
      testResource(file, project, 4, "Material 1", ResourceType.MATERIAL);
      testResource(file, project, 5, "Material 2", ResourceType.MATERIAL);
      testResource(file, project, 6, "Material 3", ResourceType.MATERIAL);

      //
      // The cost resource type was introduced in MPP12
      //
      ResourceType expectedType = NumberHelper.getInt(project.getProjectProperties().getMppFileType()) > 9 ? ResourceType.COST : ResourceType.MATERIAL;
      testResource(file, project, 7, "Cost 1", expectedType);
      testResource(file, project, 8, "Cost 2", expectedType);
      testResource(file, project, 9, "Cost 3", expectedType);
   }

   /**
    * Validate the name and type of an individual resource.
    *
    * @param file MPP file
    * @param project project read from MPP file
    * @param id resource ID
    * @param expectedName expected name
    * @param expectedType expected type
    */
   private void testResource(File file, ProjectFile project, int id, String expectedName, ResourceType expectedType)
   {
      Resource resource = project.getResourceByID(Integer.valueOf(id));
      assertEquals(file.getName(), expectedName, resource.getName());
      assertEquals(file.getName(), expectedType, resource.getType());
   }
}
