/*
 * file:       ResourceMiscTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       08/-03/2017
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

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure task custom costs are correctly handled.
 */
public class ResourceMiscTest
{
   /**
    * Test to validate the custom costs in files saved by different versions of MS Project.
    */
   @Test public void testResourceMisc() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/resource-misc", "resource-misc"))
      {
         testResourceMisc(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testResourceMisc(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);

      Resource resource1 = project.getResourceByID(Integer.valueOf(1));
      assertEquals("Resource 1", resource1.getName());
      assertEquals("Code1", resource1.getCode());
      assertEquals(Double.valueOf(1.23), resource1.getCostPerUse());
      assertEquals("resource1@example.com", resource1.getEmailAddress());
      assertEquals("Group1", resource1.getGroup());
      assertEquals("R1", resource1.getInitials());
      assertEquals("Notes1", resource1.getNotes());

      Resource resource2 = project.getResourceByID(Integer.valueOf(2));
      assertEquals("Resource 2", resource2.getName());
      assertEquals("Code2", resource2.getCode());
      assertEquals(Double.valueOf(4.56), resource2.getCostPerUse());
      assertEquals("resource2@example.com", resource2.getEmailAddress());
      assertEquals("Group2", resource2.getGroup());
      assertEquals("R2", resource2.getInitials());
      assertEquals("Notes2", resource2.getNotes());
   }
}
