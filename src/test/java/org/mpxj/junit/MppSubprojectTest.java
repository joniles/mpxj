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

package org.mpxj.junit;

import static org.junit.Assert.*;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppSubprojectTest
{
   /**
    * Test subproject data read from an MPP9 file.
    */
   @Test public void testMpp9Subproject() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9subproject.mpp"));
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9SubprojectFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9subproject-from12.mpp"));
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9SubprojectFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9subproject-from14.mpp"));
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPP12 file.
    */
   @Test public void testMpp12Subproject() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12subproject.mpp"));
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12SubprojectFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12subproject-from14.mpp"));
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPP14 file.
    */
   @Test public void testMpp14Subproject() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14subproject.mpp"));
      testSubprojects(mpp, true);
   }

   /**
    * Test subproject data read from an MPD9 file.
    */
   @Test public void testMpd9Subproject() throws Exception
   {
      ProjectFile mpp = new MPDFileReader().read(MpxjTestData.filePath("mpp9subproject.mpd"));
      testSubprojects(mpp, false);
   }

   /**
    * Tests the various fields needed to read in subprojects.
    *
    * @param mpp The ProjectFile being tested.
    * @param isMPP is the source an MPP file
    */
   private void testSubprojects(ProjectFile mpp, boolean isMPP)
   {
      Task taskNormal = mpp.getTaskByUniqueID(Integer.valueOf(1));
      Task taskSubprojectA = mpp.getTaskByUniqueID(Integer.valueOf(2));
      Task taskSubprojectB = mpp.getTaskByUniqueID(Integer.valueOf(3));

      assertEquals("Normal Task", taskNormal.getName());
      assertEquals("SubprojectA-9", taskSubprojectA.getName());
      assertEquals("SubprojectB-9", taskSubprojectB.getName());

      // Subproject A
      final String expectedFilenameA = "\\SubprojectA-9.mpp";
      assertTrue(taskSubprojectA.getSubprojectFile().contains(expectedFilenameA));
      assertFalse(taskSubprojectA.getSubprojectReadOnly());

      if (isMPP)
      {
         assertEquals(Integer.valueOf(8388608), taskSubprojectA.getSubprojectTasksUniqueIDOffset());
         assertEquals(Integer.valueOf(0), taskSubprojectA.getSubprojectTaskUniqueID());
      }
   }
}
