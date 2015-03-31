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

package net.sf.mpxj.junit.project;

import static net.sf.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

import org.junit.Test;

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
      File testDataDir = new File(MpxjTestData.filePath("generated/project-properties"));
      for (File file : testDataDir.listFiles(new FileFilter()
      {
         @Override public boolean accept(File pathname)
         {
            return pathname.getName().startsWith("project-properties");
         }
      }))
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
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      if (reader instanceof MPDDatabaseReader)
      {
         assumeJvm();
      }

      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      ProjectFile project = reader.read(file);
      ProjectHeader header = project.getProjectHeader();

      //
      // We are reading title successfully - it's just that when MS Project saves
      // the test data files, sometimes it sets the value we asked for... sometimes 
      // it reverts to the file name.
      //
      //assertEquals("Title", header.getProjectTitle());

      assertEquals("Subject", header.getSubject());
      assertEquals("Author", header.getAuthor());
      assertEquals("Keywords", header.getKeywords());
      assertEquals("Comments", header.getComments());
      assertEquals("Template", header.getTemplate());
      assertEquals("Category", header.getCategory());
      assertEquals("Format", header.getPresentationFormat());
      assertEquals("Manager", header.getManager());
      assertEquals("Company", header.getCompany());

      if (NumberHelper.getInt(project.getProjectHeader().getMppFileType()) > 9)
      {
         assertEquals("Content type", header.getContentType());
         assertEquals("Content status", header.getContentStatus());
         assertEquals("Language", header.getLanguage());
         assertEquals("Document version", header.getDocumentVersion());
      }

      Map<String, Object> custom = header.getCustomProperties();
      assertEquals(Integer.valueOf(1000), custom.get("CustomNumber"));
      assertEquals(Double.valueOf(1.5), custom.get("CustomFloat"));
      assertEquals("This is a custom property.", custom.get("CustomString"));
      assertEquals("01/01/2014", df.format((Date) custom.get("CustomDate")));
   }
}
