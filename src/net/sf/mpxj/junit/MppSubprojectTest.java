/*
 * file:       MppSubprojectTest.java
 * author:     Wade Golden
 * copyright:  (c) Packwood Software 2006
 * date:       19-September-2006
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

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Task;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Testsb to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppSubprojectTest extends MPXJTestCase
{

   /**
    * Test subproject data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9Subproject() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9subproject.mpp");
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12Subproject() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12subproject.mpp");
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14Subproject() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14subproject.mpp");
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPD9 file.
    * 
    * @throws Exception
    */
   public void testMpd9Subproject() throws Exception
   {
      try
      {
         ProjectFile mpp = new MPDDatabaseReader().read(m_basedir + "/mpp9subproject.mpd");
         testSubprojects(mpp, false);
      }

      catch (Exception ex)
      {
         //
         // JDBC not supported in IKVM
         //
         if (!m_ikvm)
         {
            throw ex;
         }
      }
   }

   /**
    * Tests the various fields needed to read in subprojects.
    * 
    * @param mpp The ProjectFile being tested.
    * @param isMPP is the source an MPP file
    * @throws Exception
    */
   private void testSubprojects(ProjectFile mpp, boolean isMPP) throws Exception
   {
      Task taskNormal = mpp.getTaskByUniqueID(Integer.valueOf(1));
      Task taskSubprojectA = mpp.getTaskByUniqueID(Integer.valueOf(2));
      Task taskSubprojectB = mpp.getTaskByUniqueID(Integer.valueOf(3));

      assertEquals("Normal Task", taskNormal.getName());
      assertEquals("SubprojectA-9", taskSubprojectA.getName());
      assertEquals("SubprojectB-9", taskSubprojectB.getName());

      // Subproject A
      SubProject subprojectA = taskSubprojectA.getSubProject();
      assertNotNull(subprojectA);
      final String expectedFilenameA = "\\SubprojectA-9.mpp";
      //assertEquals(expectedFilenameA, subprojectA.getDosFileName());
      assertTrue(expectedFilenameA.indexOf(subprojectA.getFileName()) != -1);
      //subprojectA.getDosFullPath(); don't need to test
      assertTrue(subprojectA.getFullPath().indexOf(expectedFilenameA) != -1);
      assertEquals(Integer.valueOf(2), subprojectA.getTaskUniqueID());

      //assertEquals(null, taskSubprojectA.getSubprojectName());  // TODO: why is this null?
      assertEquals(false, taskSubprojectA.getSubprojectReadOnly());

      if (isMPP)
      {
         assertEquals(Integer.valueOf(8388608), subprojectA.getUniqueIDOffset()); // MPD needs to be fixed
         assertEquals(Integer.valueOf(8388608), taskSubprojectA.getSubprojectTasksUniqueIDOffset());
         assertEquals(Integer.valueOf(0), taskSubprojectA.getSubprojectTaskUniqueID());
      }
   }
}
