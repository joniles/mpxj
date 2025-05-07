/*
 * file:       MppBaselineTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       31/01/2008
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

import java.time.format.DateTimeFormatter;

import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppBaselineTest
{

   /**
    * Test baseline data read from an MPP9 file.
    */
   @Test public void testMpp9BaselineFields() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9baselines.mpp"));
      testBaselineFields(mpp);
   }

   /**
    * Test baseline data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9BaselineFieldsFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9baselines-from12.mpp"));
      testBaselineFields(mpp);
   }

   /**
    * Test baseline data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9BaselineFieldsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9baselines-from14.mpp"));
      testBaselineFields(mpp);
   }

   /**
    * Test baseline data read from an MSPDI file.
    */
   @Test public void testMspdiBaselineFields() throws Exception
   {
      ProjectFile mpp = new MSPDIReader().read(MpxjTestData.filePath("baselines.xml"));
      testBaselineFields(mpp);
   }

   /**
    * Test baseline data read from an MPP12 file.
    */
   @Test public void testMpp12BaselineFields() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12baselines.mpp"));
      testBaselineFields(mpp);
   }

   /**
    * Test baseline data read from an MPP1 file saved by Project 2010.
    */
   @Test public void testMpp12BaselineFieldsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12baselines-from14.mpp"));
      testBaselineFields(mpp);
   }

   /**
    * Test baseline data read from an MPP14 file.
    */
   @Test public void testMpp14BaselineFields() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14baselines.mpp"));
      testBaselineFields(mpp);
   }

   /**
    * Tests baseline fields.
    *
    * @param mpp The ProjectFile being tested.
    */
   private void testBaselineFields(ProjectFile mpp)
   {
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      Task task = mpp.getTaskByID(Integer.valueOf(1));

      assertEquals(1, task.getBaselineCost(1).intValue());
      assertEquals(2, task.getBaselineCost(2).intValue());
      assertEquals(3, task.getBaselineCost(3).intValue());
      assertEquals(4, task.getBaselineCost(4).intValue());
      assertEquals(5, task.getBaselineCost(5).intValue());
      assertEquals(6, task.getBaselineCost(6).intValue());
      assertEquals(7, task.getBaselineCost(7).intValue());
      assertEquals(8, task.getBaselineCost(8).intValue());
      assertEquals(9, task.getBaselineCost(9).intValue());
      assertEquals(10, task.getBaselineCost(10).intValue());

      assertEquals(1, (int) task.getBaselineDuration(1).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(1).getUnits());
      assertEquals(2, (int) task.getBaselineDuration(2).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(2).getUnits());
      assertEquals(3, (int) task.getBaselineDuration(3).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(3).getUnits());
      assertEquals(4, (int) task.getBaselineDuration(4).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(4).getUnits());
      assertEquals(5, (int) task.getBaselineDuration(5).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(5).getUnits());
      assertEquals(6, (int) task.getBaselineDuration(6).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(6).getUnits());
      assertEquals(7, (int) task.getBaselineDuration(7).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(7).getUnits());
      assertEquals(8, (int) task.getBaselineDuration(8).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(8).getUnits());
      assertEquals(9, (int) task.getBaselineDuration(9).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(9).getUnits());
      assertEquals(10, (int) task.getBaselineDuration(10).getDuration());
      assertEquals(TimeUnit.DAYS, task.getBaselineDuration(10).getUnits());

      assertEquals("01/01/2000", df.format(task.getBaselineFinish(1)));
      assertEquals("02/01/2000", df.format(task.getBaselineFinish(2)));
      assertEquals("03/01/2000", df.format(task.getBaselineFinish(3)));
      assertEquals("04/01/2000", df.format(task.getBaselineFinish(4)));
      assertEquals("05/01/2000", df.format(task.getBaselineFinish(5)));
      assertEquals("06/01/2000", df.format(task.getBaselineFinish(6)));
      assertEquals("07/01/2000", df.format(task.getBaselineFinish(7)));
      assertEquals("08/01/2000", df.format(task.getBaselineFinish(8)));
      assertEquals("09/01/2000", df.format(task.getBaselineFinish(9)));
      assertEquals("10/01/2000", df.format(task.getBaselineFinish(10)));

      assertEquals("01/01/2001", df.format(task.getBaselineStart(1)));
      assertEquals("02/01/2001", df.format(task.getBaselineStart(2)));
      assertEquals("03/01/2001", df.format(task.getBaselineStart(3)));
      assertEquals("04/01/2001", df.format(task.getBaselineStart(4)));
      assertEquals("05/01/2001", df.format(task.getBaselineStart(5)));
      assertEquals("06/01/2001", df.format(task.getBaselineStart(6)));
      assertEquals("07/01/2001", df.format(task.getBaselineStart(7)));
      assertEquals("08/01/2001", df.format(task.getBaselineStart(8)));
      assertEquals("09/01/2001", df.format(task.getBaselineStart(9)));
      assertEquals("10/01/2001", df.format(task.getBaselineStart(10)));

      assertEquals(1, (int) task.getBaselineWork(1).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(1).getUnits());
      assertEquals(2, (int) task.getBaselineWork(2).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(2).getUnits());
      assertEquals(3, (int) task.getBaselineWork(3).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(3).getUnits());
      assertEquals(4, (int) task.getBaselineWork(4).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(4).getUnits());
      assertEquals(5, (int) task.getBaselineWork(5).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(5).getUnits());
      assertEquals(6, (int) task.getBaselineWork(6).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(6).getUnits());
      assertEquals(7, (int) task.getBaselineWork(7).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(7).getUnits());
      assertEquals(8, (int) task.getBaselineWork(8).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(8).getUnits());
      assertEquals(9, (int) task.getBaselineWork(9).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(9).getUnits());
      assertEquals(10, (int) task.getBaselineWork(10).getDuration());
      assertEquals(TimeUnit.HOURS, task.getBaselineWork(10).getUnits());

      Resource resource = mpp.getResourceByID(Integer.valueOf(1));

      assertEquals(1, (int) resource.getBaselineWork(1).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(1).getUnits());
      assertEquals(2, (int) resource.getBaselineWork(2).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(2).getUnits());
      assertEquals(3, (int) resource.getBaselineWork(3).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(3).getUnits());
      assertEquals(4, (int) resource.getBaselineWork(4).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(4).getUnits());
      assertEquals(5, (int) resource.getBaselineWork(5).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(5).getUnits());
      assertEquals(6, (int) resource.getBaselineWork(6).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(6).getUnits());
      assertEquals(7, (int) resource.getBaselineWork(7).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(7).getUnits());
      assertEquals(8, (int) resource.getBaselineWork(8).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(8).getUnits());
      assertEquals(9, (int) resource.getBaselineWork(9).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(9).getUnits());
      assertEquals(10, (int) resource.getBaselineWork(10).getDuration());
      assertEquals(TimeUnit.HOURS, resource.getBaselineWork(10).getUnits());

      assertEquals(1, resource.getBaselineCost(1).intValue());
      assertEquals(2, resource.getBaselineCost(2).intValue());
      assertEquals(3, resource.getBaselineCost(3).intValue());
      assertEquals(4, resource.getBaselineCost(4).intValue());
      assertEquals(5, resource.getBaselineCost(5).intValue());
      assertEquals(6, resource.getBaselineCost(6).intValue());
      assertEquals(7, resource.getBaselineCost(7).intValue());
      assertEquals(8, resource.getBaselineCost(8).intValue());
      assertEquals(9, resource.getBaselineCost(9).intValue());
      assertEquals(10, resource.getBaselineCost(10).intValue());
   }
}
