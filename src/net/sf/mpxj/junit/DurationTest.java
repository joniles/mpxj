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

package net.sf.mpxj.junit;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;

/**
 * Tests to exercise reading duration values. 
 */
public class DurationTest extends MPXJTestCase
{
   /**
    * Test duration data read from an MPP8 file.
    * 
    * @throws Exception
    */
   //   public void testMpp8() throws Exception
   //   {
   //      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp8duration.mpp");
   //      testDurations(mpp);
   //   }
   /**
    * Test duration data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9duration.mpp");
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12duration.mpp");
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14duration.mpp");
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MSPDI file.
    * 
    * @throws Exception
    */
   public void testMspdi() throws Exception
   {
      ProjectFile mpp = new MSPDIReader().read(m_basedir + "/mspdiduration.xml");
      testDurations(mpp);
   }

   /**
    * Test duration data read from an MPD file.
    * 
    * @throws Exception
    */
   public void testMpd() throws Exception
   {
      try
      {
         ProjectFile mpp = new MPDDatabaseReader().read(m_basedir + "/mpdduration.mpd");
         testDurations(mpp);
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
