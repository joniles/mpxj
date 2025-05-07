/*
 * file:       AvailabilityTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       08/06/2009
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mpxj.Availability;
import org.mpxj.AvailabilityTable;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * The tests contained in this class exercise resource availability functionality.
 */
public class AvailabilityTest
{
   /**
    * Test MPP9 file cost resource availability.
    */
   @Test public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9availability.mpp"));
      testAvailability(file);
   }

   /**
    * Test MPP9 file cost resource availability saved from Project 2007.
    */
   @Test public void testMpp9From12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9availability-from12.mpp"));
      testAvailability(file);
   }

   /**
    * Test MPP9 file cost resource availability saved from Project 2010.
    */
   @Test public void testMpp9From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9availability-from14.mpp"));
      testAvailability(file);
   }

   /**
    * Test MPP12 file resource availability.
    */
   @Test public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12availability.mpp"));
      testAvailability(file);
   }

   /**
    * Test MPP12 file resource availability saved by Project 2010.
    */
   @Test public void testMpp12From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12availability-from14.mpp"));
      testAvailability(file);
   }

   /**
    * Test MPP14 file resource availability.
    */
   @Test public void testMpp14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp14availability.mpp"));
      testAvailability(file);
   }

   /**
    * Test MSPDI file resource availability.
    */
   @Test public void testMspdi() throws Exception
   {
      ProjectFile file = new MSPDIReader().read(MpxjTestData.filePath("mspdiavailability.xml"));
      testAvailability(file);
   }

   /**
    * Common availability tests.
    *
    * @param file project file
    */
   private void testAvailability(ProjectFile file)
   {
      //
      // Resource with empty availability table
      //
      Resource resource = file.getResourceByID(Integer.valueOf(1));
      assertEquals("Resource One", resource.getName());
      AvailabilityTable table = resource.getAvailability();
      assertTrue(table.hasDefaultDateRange());

      //
      // Resource with populated availability table
      //
      resource = file.getResourceByID(Integer.valueOf(2));
      assertEquals("Resource Two", resource.getName());
      table = resource.getAvailability();
      assertEquals(3, table.size());

      assertAvailabilityEquals("01/06/2009 00:00", "01/07/2009 23:59", 100.0, table, 0);
      assertAvailabilityEquals("02/07/2009 00:00", "01/08/2009 23:59", 60.0, table, 1);
      assertAvailabilityEquals("20/08/2009 00:00", "30/08/2009 23:59", 75.0, table, 2);

      //
      // Validate date-based row selection
      //
      assertNull(table.getEntryByDate(LocalDateTime.of(2009, 5, 1, 12, 0)));
      assertAvailabilityEquals("02/07/2009 00:00", "01/08/2009 23:59", 60.0, table, 1);
      assertNull(table.getEntryByDate(LocalDateTime.of(2009, 8, 2, 12, 0)));
      assertAvailabilityEquals("20/08/2009 00:00", "30/08/2009 23:59", 75.0, table, 2);
      assertNull(table.getEntryByDate(LocalDateTime.of(2009, 9, 1, 12, 0)));
   }

   /**
    * Test a single row from an availability table.
    *
    * @param startDate expected start date
    * @param endDate expected end date
    * @param units expected units
    * @param table table instance under test
    * @param index index of table row under test
    */
   private void assertAvailabilityEquals(String startDate, String endDate, double units, AvailabilityTable table, int index)
   {
      Availability entry = table.get(index);
      assertAvailabilityEquals(startDate, endDate, units, entry);
   }

   /**
    * Test a single row from an availability table.
    *
    * @param startDate expected start date
    * @param endDate expected end date
    * @param units expected units
    * @param entry table entry instance under test
    */
   private void assertAvailabilityEquals(String startDate, String endDate, double units, Availability entry)
   {
      LocalDateTimeRange range = entry.getRange();
      assertEquals(startDate, m_df.format(range.getStart()));
      assertEquals(endDate, m_df.format(range.getEnd()));
      assertEquals(units, entry.getUnits().doubleValue(), 0);
   }

   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
