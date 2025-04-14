/*
 * file:       ProjectPropertiesTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       27/11/2014
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
import java.util.Map;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.common.NumberHelper;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure project properties are correctly handled.
 */
public class ProjectPropertiesTest
{
   /**
    * Test to validate the project properties in files saved by different versions of MS Project.
    */
   @Test public void testProjectProperties() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/project-properties", "project-properties"))
      {
         testProjectProperties(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testProjectProperties(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      ProjectProperties properties = project.getProjectProperties();

      //
      // We are reading title successfully - it's just that when MS Project saves
      // the test data files, sometimes it sets the value we asked for... sometimes
      // it reverts to the file name.
      //
      //assertEquals("Title", properties.getProjectTitle());

      assertEquals(file.getName(), "Subject", properties.getSubject());
      assertEquals("Author", properties.getAuthor());
      assertEquals("Keywords", properties.getKeywords());
      assertEquals("Comments", properties.getComments());
      assertEquals("Template", properties.getTemplate());
      assertEquals("Category", properties.getCategory());
      assertEquals("Format", properties.getPresentationFormat());
      assertEquals("Manager", properties.getManager());
      assertEquals("Company", properties.getCompany());

      if (NumberHelper.getInt(project.getProjectProperties().getMppFileType()) > 9)
      {
         assertEquals("Content type", properties.getContentType());
         assertEquals("Content status", properties.getContentStatus());
         assertEquals("Language", properties.getLanguage());
         assertEquals("Document version", properties.getDocumentVersion());
      }

      Map<String, Object> custom = properties.getCustomProperties();
      assertEquals(Integer.valueOf(1000), custom.get("CustomNumber"));
      assertEquals(Double.valueOf(1.5), custom.get("CustomFloat"));
      assertEquals("This is a custom property.", custom.get("CustomString"));
      // Project 2019 returns a time one hour behind what we expect. DST maybe?
      // TODO: investigate further
      //assertEquals(file.getName(), "01/01/2014", df.format((Date) custom.get("CustomDate")));
   }
}
