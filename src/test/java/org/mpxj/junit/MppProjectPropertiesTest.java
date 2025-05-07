/*
 * file:       MppProjectPropertiesTest.java
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

package org.mpxj.junit;

import static org.junit.Assert.*;

import java.time.format.DateTimeFormatter;

import org.mpxj.CurrencySymbolPosition;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.ScheduleFrom;
import org.mpxj.TimeUnit;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Test reading project properties from MPP files.
 */
public class MppProjectPropertiesTest
{
   /**
    * Test project properties read from an MPP9 file.
    */
   @Test public void testMpp9() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9header.mpp"));
      testProperties(mpp, true);
   }

   /**
    * Test project properties read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9From12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9header-from12.mpp"));
      testProperties(mpp, true);
   }

   /**
    * Test project properties read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9From14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9header-from14.mpp"));
      testProperties(mpp, true);
   }

   /**
    * Test project properties read from an MPP12 file.
    */
   @Test public void testMpp12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12header.mpp"));
      testProperties(mpp, true);
   }

   /**
    * Test project properties read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12From14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12header-from14.mpp"));
      testProperties(mpp, true);
   }

   /**
    * Test project properties read from an MPP14 file.
    */
   @Test public void testMpp14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14header.mpp"));
      testProperties(mpp, true);
   }

   /**
    * Test project properties read from an MPD9 file.
    */
   @Test public void testMpd9() throws Exception
   {
      ProjectFile mpp = new MPDFileReader().read(MpxjTestData.filePath("mpp9header.mpd"));
      testProperties(mpp, false);
   }

   /**
    * Test the project properties as read from an MPP file.
    *
    * @param mpp project file
    * @param isMPP is the source an MPP file
    */
   private void testProperties(ProjectFile mpp, boolean isMPP)
   {
      //
      // Create time and date formatters
      //
      DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      //
      // Check the values of project properties.
      // The order of these tests should be the same as the order
      // in which the attributes are read from the MPP file
      // for ease of reference.
      //
      ProjectProperties ph = mpp.getProjectProperties();
      assertEquals(ScheduleFrom.FINISH, ph.getScheduleFrom());
      assertEquals("24 Hours", ph.getDefaultCalendar().getName());
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
      assertFalse(ph.getSplitInProgressTasks());
      assertFalse(ph.getUpdatingTaskStatusUpdatesResourceStatus());

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
         assertTrue(ph.getMultipleCriticalPaths());
      }
   }
}
