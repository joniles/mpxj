/*
 * file:       MppEnterpriseTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       06/01/2008
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

import java.text.SimpleDateFormat;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppEnterpriseTest extends MPXJTestCase
{

   /**
    * Test enterprise data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9EnterpriseFields() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9enterprise.mpp");
      testEnterpriseFields(mpp);
   }

   /**
    * Test enterprise data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12EnterpriseFields() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12enterprise.mpp");
      testEnterpriseFields(mpp);
   }

   /**
    * Test enterprise data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14EnterpriseFields() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14enterprise.mpp");
      testEnterpriseFields(mpp);
   }

   /**
    * Test enterprise data read from an MSPDI file.
    * 
    * @throws Exception
    */
   public void testMspdiEnterpriseFields() throws Exception
   {
      ProjectFile mpp = new MSPDIReader().read(m_basedir + "/enterprise.xml");
      testEnterpriseFields(mpp);
   }

   /**
    * Tests enterprise fields.
    * 
    * @param mpp The ProjectFile being tested.
    * @throws Exception
    */
   private void testEnterpriseFields(ProjectFile mpp) throws Exception
   {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      Task task = mpp.getTaskByID(Integer.valueOf(1));

      assertEquals(1, task.getEnterpriseCost(1).intValue());
      assertEquals(10, task.getEnterpriseCost(10).intValue());
      assertEquals("01/01/1991", df.format(task.getEnterpriseDate(1)));
      assertEquals("01/01/2020", df.format(task.getEnterpriseDate(30)));
      //assertEquals(1, (int) task.getEnterpriseDuration(1).getDuration()); zero in 2010 beta
      assertEquals(TimeUnit.DAYS, task.getEnterpriseDuration(1).getUnits());
      //assertEquals(10, (int) task.getEnterpriseDuration(10).getDuration()); zero in 2010 beta
      assertEquals(TimeUnit.DAYS, task.getEnterpriseDuration(10).getUnits());
      assertEquals(1, task.getEnterpriseNumber(1).intValue());
      assertEquals(40, task.getEnterpriseNumber(40).intValue());
      assertEquals("ET1", task.getEnterpriseText(1));
      assertEquals("ET40", task.getEnterpriseText(40));
      assertEquals(true, task.getEnterpriseFlag(1));
      assertEquals(false, task.getEnterpriseFlag(20));

      Resource resource = mpp.getResourceByID(Integer.valueOf(1));
      assertEquals(1, resource.getEnterpriseCost(1).intValue());
      assertEquals(10, resource.getEnterpriseCost(10).intValue());
      assertEquals("01/01/2008", df.format(resource.getEnterpriseDate(1)));
      assertEquals("30/01/2008", df.format(resource.getEnterpriseDate(30)));
      //assertEquals(1, (int) resource.getEnterpriseDuration(1).getDuration()); zero in 2010 beta
      assertEquals(TimeUnit.DAYS, resource.getEnterpriseDuration(1).getUnits());
      //assertEquals(10, (int) resource.getEnterpriseDuration(10).getDuration()); zero in 2010 beta
      assertEquals(TimeUnit.DAYS, resource.getEnterpriseDuration(10).getUnits());
      assertEquals(1, resource.getEnterpriseNumber(1).intValue());
      assertEquals(40, resource.getEnterpriseNumber(40).intValue());
      assertEquals("RET1", resource.getEnterpriseText(1));
      assertEquals("RET40", resource.getEnterpriseText(40));
      assertEquals(false, resource.getEnterpriseFlag(1));
      assertEquals(true, resource.getEnterpriseFlag(20));
   }
}
