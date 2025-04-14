/*
 * file:       CostRateTableTest.java
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

import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.TimeUnit;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * The tests contained in this class exercise cost rate table functionality.
 */
public class CostRateTableTest
{
   /**
    * Test MPP9 file cost rate tables.
    */
   @Test public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9costratetable.mpp"));
      testCostRateTable(file);
   }

   /**
    * Test MPP9 file cost rate tables saved by Project 2007.
    */
   @Test public void testMpp9From12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9costratetable-from12.mpp"));
      testCostRateTable(file);
   }

   /**
    * Test MPP9 file cost rate tables saved by Project 2010.
    */
   @Test public void testMpp9From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp9costratetable-from14.mpp"));
      testCostRateTable(file);
   }

   /**
    * Test MPP12 file cost rate tables.
    */
   @Test public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12costratetable.mpp"));
      testCostRateTable(file);
   }

   /**
    * Test MPP12 file cost rate tables saved by Project 2010.
    */
   @Test public void testMpp12From14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp12costratetable-from14.mpp"));
      testCostRateTable(file);
   }

   /**
    * Test MPP14 file cost rate tables.
    */
   @Test public void testMpp14() throws Exception
   {
      ProjectFile file = new MPPReader().read(MpxjTestData.filePath("mpp14costratetable.mpp"));
      testCostRateTable(file);
   }

   /**
    * Test MSPDI file cost rate tables.
    */
   @Test public void testMspdi() throws Exception
   {
      ProjectFile file = new MSPDIReader().read(MpxjTestData.filePath("mspdicostratetable.xml"));
      testCostRateTable(file);
   }

   /**
    * Common cost rate table tests.
    *
    * @param file project file
    */
   private void testCostRateTable(ProjectFile file)
   {
      //
      // Resource with default tables
      //
      Resource resource = file.getResourceByID(Integer.valueOf(1));
      assertEquals("Resource One", resource.getName());

      // Table A
      CostRateTable table = resource.getCostRateTable(0);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table B
      table = resource.getCostRateTable(1);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table C
      table = resource.getCostRateTable(2);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table D
      table = resource.getCostRateTable(3);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table E
      table = resource.getCostRateTable(4);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      //
      // Resource with default tables, but non-default values
      //
      resource = file.getResourceByID(Integer.valueOf(2));
      assertEquals("Resource Two", resource.getName());

      // Table A
      table = resource.getCostRateTable(0);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(5, TimeUnit.HOURS, 10, TimeUnit.HOURS, 15, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table B
      table = resource.getCostRateTable(1);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(20, TimeUnit.HOURS, 25, TimeUnit.HOURS, 30, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table C
      table = resource.getCostRateTable(2);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(35, TimeUnit.HOURS, 40, TimeUnit.HOURS, 45, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table D
      table = resource.getCostRateTable(3);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(50, TimeUnit.HOURS, 55, TimeUnit.HOURS, 60, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      // Table E
      table = resource.getCostRateTable(4);
      assertNotNull(table);
      assertEquals(1, table.size());
      assertRateEquals(65, TimeUnit.HOURS, 70, TimeUnit.HOURS, 75, "01/01/1984 00:00", "31/12/2049 23:59", table, 0);

      //
      // Resource with multiple values
      //
      resource = file.getResourceByID(Integer.valueOf(3));
      assertEquals("Resource Three", resource.getName());

      // Table A
      table = resource.getCostRateTable(0);
      assertNotNull(table);
      assertEquals(2, table.size());
      assertRateEquals(5, TimeUnit.HOURS, 10, TimeUnit.HOURS, 15, "01/01/1984 00:00", "15/06/2009 07:59", table, 0);
      assertRateEquals(20, TimeUnit.MINUTES, 25, TimeUnit.HOURS, 30, "15/06/2009 08:00", "31/12/2049 23:59", table, 1);

      // Table B
      table = resource.getCostRateTable(1);
      assertNotNull(table);
      assertEquals(2, table.size());
      assertRateEquals(35, TimeUnit.HOURS, 40, TimeUnit.HOURS, 45, "01/01/1984 00:00", "16/06/2009 07:59", table, 0);
      assertRateEquals(50, TimeUnit.DAYS, 55, TimeUnit.WEEKS, 60, "16/06/2009 08:00", "31/12/2049 23:59", table, 1);

      // Table C
      table = resource.getCostRateTable(2);
      assertNotNull(table);
      assertEquals(2, table.size());
      assertRateEquals(65, TimeUnit.HOURS, 70, TimeUnit.HOURS, 75, "01/01/1984 00:00", "17/06/2009 07:59", table, 0);
      assertRateEquals(80, TimeUnit.MONTHS, 85, TimeUnit.YEARS, 90, "17/06/2009 08:00", "31/12/2049 23:59", table, 1);

      // Table D
      table = resource.getCostRateTable(3);
      assertNotNull(table);
      assertEquals(2, table.size());
      assertRateEquals(95, TimeUnit.HOURS, 100, TimeUnit.HOURS, 105, "01/01/1984 00:00", "18/06/2009 07:59", table, 0);
      assertRateEquals(110, TimeUnit.HOURS, 115, TimeUnit.HOURS, 120, "18/06/2009 08:00", "31/12/2049 23:59", table, 1);

      // Table E
      table = resource.getCostRateTable(4);
      assertNotNull(table);
      assertEquals(2, table.size());
      assertRateEquals(125, TimeUnit.HOURS, 130, TimeUnit.HOURS, 135, "01/01/1984 00:00", "19/06/2009 07:59", table, 0);
      assertRateEquals(140, TimeUnit.HOURS, 145, TimeUnit.HOURS, 150, "19/06/2009 08:00", "31/12/2049 23:59", table, 1);

      //
      // Validate date-based row selection
      //
      CostRateTableEntry entry = table.getEntryByDate(LocalDateTime.of(2009, 6, 18, 7, 0));
      assertRateEquals(125, TimeUnit.HOURS, 130, TimeUnit.HOURS, 135, "01/01/1984 00:00", "19/06/2009 07:59", entry);
      entry = table.getEntryByDate(LocalDateTime.of(2009, 6, 19, 10, 0));
      assertRateEquals(140, TimeUnit.HOURS, 145, TimeUnit.HOURS, 150, "19/06/2009 08:00", "31/12/2049 23:59", entry);
   }

   /**
    * Test a single row from a cost rate table.
    *
    * @param standardRate expected standard rate
    * @param standardRateFormat expected standard rate format
    * @param overtimeRate expected overtime rate
    * @param overtimeRateFormat expected overtime rate format
    * @param perUseRate expected per use rate
    * @param startDate expected start date
    * @param endDate expected end date
    * @param table table instance under test
    * @param index index of table row under test
    */
   private void assertRateEquals(double standardRate, TimeUnit standardRateFormat, double overtimeRate, TimeUnit overtimeRateFormat, double perUseRate, String startDate, String endDate, CostRateTable table, int index)
   {
      assertRateEquals(standardRate, standardRateFormat, overtimeRate, overtimeRateFormat, perUseRate, startDate, endDate, table.get(index));
   }

   /**
    * Test a single row from a cost rate table.
    *
    * @param standardRate expected standard rate
    * @param standardRateFormat expected standard rate format
    * @param overtimeRate expected overtime rate
    * @param overtimeRateFormat expected overtime rate format
    * @param costPerUse expected cost per use
    * @param startDate expected start date
    * @param endDate expected end date
    * @param entry table entry instance under test
    */
   private void assertRateEquals(double standardRate, TimeUnit standardRateFormat, double overtimeRate, TimeUnit overtimeRateFormat, double costPerUse, String startDate, String endDate, CostRateTableEntry entry)
   {
      assertEquals(standardRate, entry.getStandardRate().getAmount(), 0.009);
      assertEquals(overtimeRate, entry.getOvertimeRate().getAmount(), 0.009);
      assertEquals(costPerUse, entry.getCostPerUse().doubleValue(), 0);
      assertEquals(startDate, m_df.format(entry.getStartDate()));
      assertEquals(endDate, m_df.format(entry.getEndDate()));
      assertEquals(standardRateFormat, entry.getStandardRate().getUnits());
      assertEquals(overtimeRateFormat, entry.getOvertimeRate().getUnits());
   }

   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
