/*
 * file:       ProjectWriterUtilityTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       27/11/2008
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

package net.sf.mpxj.junit;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.writer.ProjectWriterUtility;

/**
 * The tests contained in this class exercise the ProjectWriterUtility class.
 */
public class ProjectWriterUtilityTest
{
   /**
    * Exercise the GetProjectWriter method.
    */
   @Test public void testGetProjectWriter() throws Exception
   {
      ProjectWriter writer;

      try
      {
         ProjectWriterUtility.getProjectWriter("filename.xxx");
         fail("Failed to throw exception");
      }

      catch (Exception ex)
      {
         assertEquals("Cannot write files of type: filename.xxx", ex.getMessage());
      }

      try
      {
         ProjectWriterUtility.getProjectWriter("filename");
         fail("Failed to throw exception");
      }

      catch (Exception ex)
      {
         assertEquals("Filename has no extension: filename", ex.getMessage());
      }

      writer = ProjectWriterUtility.getProjectWriter("filename.mpx");
      assertEquals(MPXWriter.class, writer.getClass());

      writer = ProjectWriterUtility.getProjectWriter("filename.xml");
      assertEquals(MSPDIWriter.class, writer.getClass());

      writer = ProjectWriterUtility.getProjectWriter("filename.planner");
      assertEquals(PlannerWriter.class, writer.getClass());
   }

   /**
    * Test to exercise the getSupportedFileExtensions method.
    */
   @Test public void testGetSupportedFileExtensions()
   {
      Set<String> extensions = ProjectWriterUtility.getSupportedFileExtensions();
      assertTrue(extensions.contains("MPX"));
   }
}
