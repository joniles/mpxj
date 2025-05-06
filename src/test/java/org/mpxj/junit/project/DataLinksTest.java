/*
 * file:       DataLinksTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       15/06/2019
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import org.mpxj.DataLinkContainer;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.reader.UniversalProjectReader;

/**
 * Very basic test to ensure data links are being read.
 */
public class DataLinksTest
{
   /**
    * Test to validate the custom value lists in files saved by different versions of MS Project.
    */
   @Test public void testProjectValueLists() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/data-links", "data-links"))
      {
         testDataLinks(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testDataLinks(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      assertNotNull(project);
      DataLinkContainer dataLinks = project.getDataLinks();
      assertEquals(3, dataLinks.size());
   }
}
