/*
 * file:       ResourceTypeTest.java
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

package net.sf.mpxj.junit.resource;

import static net.sf.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

/**
 * Tests to ensure task custom costs are correctly handled.
 */
public class ResourceTypeTest
{
   /**
    * Test to validate the custom costs in files saved by different versions of MS Project.
    */
   @Test public void testResourceType() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/resource-type", "resource-type"))
      {
         testResourceType(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testResourceType(File file) throws MPXJException
   {
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      if (reader instanceof MPDDatabaseReader)
      {
         assumeJvm();
      }

      ProjectFile project = reader.read(file);
      ResourceType expectedType;
      Integer mppFileType = project.getProjectProperties().getMppFileType();
      boolean missingCostType = (mppFileType != null && mppFileType.intValue() < 12) || file.getName().endsWith(".mpd") || file.getName().indexOf("2003-mspdi") != -1 || file.getName().indexOf("2002-mspdi") != -1;
      if (missingCostType)
      {
         expectedType = ResourceType.MATERIAL;
      }
      else
      {
         expectedType = ResourceType.COST;
      }

      assertEquals(file.getName(), expectedType, project.getResourceByID(Integer.valueOf(1)).getType());
      assertEquals(file.getName(), expectedType, project.getResourceByID(Integer.valueOf(2)).getType());
      assertEquals(file.getName(), expectedType, project.getResourceByID(Integer.valueOf(3)).getType());
      assertEquals(file.getName(), expectedType, project.getResourceByID(Integer.valueOf(4)).getType());
      assertEquals(file.getName(), expectedType, project.getResourceByID(Integer.valueOf(5)).getType());

      assertEquals(file.getName(), ResourceType.MATERIAL, project.getResourceByID(Integer.valueOf(6)).getType());
      assertEquals(file.getName(), ResourceType.MATERIAL, project.getResourceByID(Integer.valueOf(7)).getType());
      assertEquals(file.getName(), ResourceType.MATERIAL, project.getResourceByID(Integer.valueOf(8)).getType());
      assertEquals(file.getName(), ResourceType.MATERIAL, project.getResourceByID(Integer.valueOf(9)).getType());
      assertEquals(file.getName(), ResourceType.MATERIAL, project.getResourceByID(Integer.valueOf(10)).getType());

      assertEquals(file.getName(), ResourceType.WORK, project.getResourceByID(Integer.valueOf(11)).getType());
      assertEquals(file.getName(), ResourceType.WORK, project.getResourceByID(Integer.valueOf(12)).getType());
      assertEquals(file.getName(), ResourceType.WORK, project.getResourceByID(Integer.valueOf(13)).getType());
      assertEquals(file.getName(), ResourceType.WORK, project.getResourceByID(Integer.valueOf(14)).getType());
      assertEquals(file.getName(), ResourceType.WORK, project.getResourceByID(Integer.valueOf(15)).getType());
   }
}
