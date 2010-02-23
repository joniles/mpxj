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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.sf.mpxj.Availability;
import net.sf.mpxj.AvailabilityTable;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;

/**
 * The tests contained in this class exercise resource availability functionality.
 */
public class AvailabilityTest extends MPXJTestCase
{
   /**
    * Test MPP9 file cost resource availability.
    * 
    * @throws Exception
    */
   public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp9availability.mpp");
      testAvailability(file);
   }

   /**
    * Test MPP12 file resource availability.
    * 
    * @throws Exception
    */
   public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp12availability.mpp");
      testAvailability(file);
   }

   /**
    * Test MPP14 file resource availability.
    * 
    * @throws Exception
    */
   public void testMpp14() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp14availability.mpp");
      testAvailability(file);
   }

   /**
    * Test MSPDI file resource availability.
    * 
    * @throws Exception
    */
   public void testMspdi() throws Exception
   {
      ProjectFile file = new MSPDIReader().read(m_basedir + "/mspdiavailability.xml");
      testAvailability(file);
   }

   /**
    * Common availability tests.
    * 
    * @param file project file
    */
   private void testAvailability(ProjectFile file) throws Exception
   {
      //
      // Resource with empty availability table
      //
      Resource resource = file.getResourceByID(Integer.valueOf(1));
      assertEquals("Resource One", resource.getName());
      AvailabilityTable table = resource.getAvailability();
      assertEquals(0, table.size());

      //
      // Resource with populated availability table
      //
      resource = file.getResourceByID(Integer.valueOf(2));
      assertEquals("Resource Two", resource.getName());
      table = resource.getAvailability();
      assertEquals(3, table.size());

      assertEquals("01/06/2009 00:00", "01/07/2009 23:59", 100.0, table, 0);
      assertEquals("02/07/2009 00:00", "01/08/2009 23:59", 60.0, table, 1);
      assertEquals("20/08/2009 00:00", "30/08/2009 23:59", 75.0, table, 2);

      //
      // Validate date-based row selection
      //
      Availability entry = table.getEntryByDate(m_df.parse("01/05/2009 12:00"));
      assertNull(entry);
      entry = table.getEntryByDate(m_df.parse("03/07/2009 12:00"));
      assertEquals("02/07/2009 00:00", "01/08/2009 23:59", 60.0, table, 1);
      entry = table.getEntryByDate(m_df.parse("02/08/2009 12:00"));
      assertNull(entry);
      entry = table.getEntryByDate(m_df.parse("21/08/2009 12:00"));
      assertEquals("20/08/2009 00:00", "30/08/2009 23:59", 75.0, table, 2);
      entry = table.getEntryByDate(m_df.parse("01/09/2009 12:00"));
      assertNull(entry);
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
   private void assertEquals(String startDate, String endDate, double units, AvailabilityTable table, int index)
   {
      Availability entry = table.get(index);
      assertEquals(startDate, endDate, units, entry);
   }

   /**
    * Test a single row from an availability table.
    * 
    * @param startDate expected start date
    * @param endDate expected end date
    * @param units expected units
    * @param entry table entry instance under test
    */
   private void assertEquals(String startDate, String endDate, double units, Availability entry)
   {
      DateRange range = entry.getRange();
      assertEquals(startDate, m_df.format(range.getStart()));
      assertEquals(endDate, m_df.format(range.getEnd()));
      assertEquals(units, entry.getUnits().doubleValue(), 0);
   }

   private DateFormat m_df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
}
