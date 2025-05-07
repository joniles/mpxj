/*
 * file:       DurationTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       25/03/2009
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
import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * Tests to exercise reading duration values.
 */
public class DurationTest
{
   /**
    * Test duration data read from an MPP9 file.
    */
   @Test public void testMpp9() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9duration.mpp"));
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9From12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9duration-from12.mpp"));
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9From14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9duration-from14.mpp"));
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPP12 file.
    */
   @Test public void testMpp12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12duration.mpp"));
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12From14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12duration-from14.mpp"));
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPP14 file.
    */
   @Test public void testMpp14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14duration.mpp"));
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MSPDI file.
    */
   @Test public void testMspdi() throws Exception
   {
      ProjectFile mpp = new MSPDIReader().read(MpxjTestData.filePath("mspdiduration.xml"));
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPD file.
    */
   @Test public void testMpd() throws Exception
   {
      ProjectFile mpp = new MPDFileReader().read(MpxjTestData.filePath("mpdduration.mpd"));
      testDurations(mpp);
   }

   /**
    * Validates duration values.
    *
    * @param mpp project file
    */
   private void testDurations(ProjectFile mpp)
   {
      Task task = mpp.getTaskByID(Integer.valueOf(1));
      assertEquals(Duration.getInstance(1, TimeUnit.MINUTES), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(2));
      assertEquals(Duration.getInstance(1, TimeUnit.HOURS), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(3));
      assertEquals(Duration.getInstance(1, TimeUnit.DAYS), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(4));
      assertEquals(Duration.getInstance(1, TimeUnit.WEEKS), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(5));
      assertEquals(Duration.getInstance(1, TimeUnit.MONTHS), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(6));
      assertEquals(Duration.getInstance(1, TimeUnit.ELAPSED_MINUTES), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(7));
      assertEquals(Duration.getInstance(1, TimeUnit.ELAPSED_HOURS), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(8));
      assertEquals(Duration.getInstance(1, TimeUnit.ELAPSED_DAYS), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(9));
      assertEquals(Duration.getInstance(1, TimeUnit.ELAPSED_WEEKS), task.getDuration());

      task = mpp.getTaskByID(Integer.valueOf(10));
      assertEquals(Duration.getInstance(1, TimeUnit.ELAPSED_MONTHS), task.getDuration());
   }

}
