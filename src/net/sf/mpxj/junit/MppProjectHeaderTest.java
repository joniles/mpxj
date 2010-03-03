/*
 * file:       MppProjectHeaderTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       23-August-2006
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Test reading project header data from MPP files.
 */
public class MppProjectHeaderTest extends MPXJTestCase
{
   /**
    * Test project header data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9header.mpp");
      testHeader(mpp, true);
   }

   /**
    * Test project header data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12header.mpp");
      testHeader(mpp, true);
   }

   /**
    * Test project header data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14header.mpp");
      testHeader(mpp, true);
   }

   /**
    * Test project header data read from an MPD9 file.
    * 
    * @throws Exception
    */
   public void testMpd9() throws Exception
   {
      try
      {
         ProjectFile mpp = new MPDDatabaseReader().read(m_basedir + "/mpp9header.mpd");
         testHeader(mpp, false);
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
    * Test the contents of the project header as read from an MPP file.
    * 
    * @param mpp project file
    * @param isMPP is the source an MPP file
    */
   private void testHeader(ProjectFile mpp, boolean isMPP)
   {
      //
      // Create time and date formatters
      //
      DateFormat tf = new SimpleDateFormat("HH:mm");
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      //
      // Check the values of project header attributes.
      // The order of these tests should be the same as the order 
      // in which the attributes are read from the MPP file
      // for ease of reference.
      //
      ProjectHeader ph = mpp.getProjectHeader();
      assertEquals(ScheduleFrom.FINISH, ph.getScheduleFrom());
      assertEquals("24 Hours", ph.getCalendarName());
      assertEquals("08:35", tf.format(ph.getDefaultStartTime()));
      assertEquals("17:35", tf.format(ph.getDefaultEndTime()));
      assertEquals("01/08/2006", df.format(ph.getStatusDate()));

      assertEquals(TimeUnit.HOURS, ph.getDefaultDurationUnits());
      assertEquals(7 * 60, ph.getMinutesPerDay().intValue());
      assertEquals(41 * 60, ph.getMinutesPerWeek().intValue());
      assertEquals(2.0, ph.getDefaultOvertimeRate().getAmount(), 0);
      assertEquals(TimeUnit.HOURS, ph.getDefaultOvertimeRate().getUnits());
      assertEquals(1.0, ph.getDefaultStandardRate().getAmount(), 0);
      assertEquals(TimeUnit.HOURS, ph.getDefaultStandardRate().getUnits());
      assertEquals(TimeUnit.WEEKS, ph.getDefaultWorkUnits());
      assertEquals(false, ph.getSplitInProgressTasks());
      assertEquals(false, ph.getUpdatingTaskStatusUpdatesResourceStatus());

      assertEquals(1, ph.getCurrencyDigits().intValue());
      assertEquals("X", ph.getCurrencySymbol());
      assertEquals(CurrencySymbolPosition.AFTER, ph.getSymbolPosition());

      assertEquals("title", ph.getProjectTitle());
      assertEquals("subject", ph.getSubject());
      assertEquals("author", ph.getAuthor());
      assertEquals("keywords", ph.getKeywords());
      assertEquals("company", ph.getCompany());
      assertEquals("manager", ph.getManager());
      assertEquals("category", ph.getCategory());

      // MPP only
      if (isMPP)
      {
         assertEquals("comments", ph.getComments());
         assertEquals(true, ph.getCalculateMultipleCriticalPaths());
      }
   }
}
