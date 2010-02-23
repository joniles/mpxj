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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;

/**
 * The tests contained in this class exercise cost rate table functionality.
 */
public class CostRateTableTest extends MPXJTestCase
{
   /**
    * Test MPP9 file cost rate tables.
    * 
    * @throws Exception
    */
   public void testMpp9() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp9costratetable.mpp");
      testCostRateTable(file);
   }

   /**
    * Test MPP12 file cost rate tables.
    * 
    * @throws Exception
    */
   public void testMpp12() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp12costratetable.mpp");
      testCostRateTable(file);
   }

   /**
    * Test MPP14 file cost rate tables.
    * 
    * @throws Exception
    */
   public void testMpp14() throws Exception
   {
      ProjectFile file = new MPPReader().read(m_basedir + "/mpp14costratetable.mpp");
      testCostRateTable(file);
   }

   /**
    * Test MSPDI file cost rate tables.
    * 
    * @throws Exception
    */
   public void testMspdi() throws Exception
   {
      ProjectFile file = new MSPDIReader().read(m_basedir + "/mspdicostratetable.xml");
      testCostRateTable(file);
   }

   /**
    * Common cost rate table tests.
    * 
    * @param file project file
    */
   private void testCostRateTable(ProjectFile file) throws Exception
   {
      //
      // Resource with default tables
      //
      Resource resource = file.getResourceByID(Integer.valueOf(1));
      assertEquals("Resource One", resource.getName());

      // Table A
      CostRateTable table = resource.getCostRateTable(0);
      assertEquals(1, table.size());
      assertEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "31/12/2049 23:59", table, 0);

      // Table B
      table = resource.getCostRateTable(1);
      assertEquals(1, table.size());
      assertEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "31/12/2049 23:59", table, 0);

      // Table C
      table = resource.getCostRateTable(2);
      assertEquals(1, table.size());
      assertEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "31/12/2049 23:59", table, 0);

      // Table D
      table = resource.getCostRateTable(3);
      assertEquals(1, table.size());
      assertEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "31/12/2049 23:59", table, 0);

      // Table E
      table = resource.getCostRateTable(4);
      assertEquals(1, table.size());
      assertEquals(0, TimeUnit.HOURS, 0, TimeUnit.HOURS, 0, "31/12/2049 23:59", table, 0);

      //
      // Resource with default tables, but non-default values
      //
      resource = file.getResourceByID(Integer.valueOf(2));
      assertEquals("Resource Two", resource.getName());

      // Table A
      table = resource.getCostRateTable(0);
      assertEquals(1, table.size());
      assertEquals(5, TimeUnit.HOURS, 10, TimeUnit.HOURS, 15, "31/12/2049 23:59", table, 0);

      // Table B
      table = resource.getCostRateTable(1);
      assertEquals(1, table.size());
      assertEquals(20, TimeUnit.HOURS, 25, TimeUnit.HOURS, 30, "31/12/2049 23:59", table, 0);

      // Table C
      table = resource.getCostRateTable(2);
      assertEquals(1, table.size());
      assertEquals(35, TimeUnit.HOURS, 40, TimeUnit.HOURS, 45, "31/12/2049 23:59", table, 0);

      // Table D
      table = resource.getCostRateTable(3);
      assertEquals(1, table.size());
      assertEquals(50, TimeUnit.HOURS, 55, TimeUnit.HOURS, 60, "31/12/2049 23:59", table, 0);

      // Table E
      table = resource.getCostRateTable(4);
      assertEquals(1, table.size());
      assertEquals(65, TimeUnit.HOURS, 70, TimeUnit.HOURS, 75, "31/12/2049 23:59", table, 0);

      //
      // Resource with multiple values
      //
      resource = file.getResourceByID(Integer.valueOf(3));
      assertEquals("Resource Three", resource.getName());

      // Table A
      table = resource.getCostRateTable(0);
      assertEquals(2, table.size());
      assertEquals(5, TimeUnit.HOURS, 10, TimeUnit.HOURS, 15, "15/06/2009 08:00", table, 0);
      assertEquals(1200, TimeUnit.MINUTES, 25, TimeUnit.HOURS, 30, "31/12/2049 23:59", table, 1);

      // Table B
      table = resource.getCostRateTable(1);
      assertEquals(2, table.size());
      assertEquals(35, TimeUnit.HOURS, 40, TimeUnit.HOURS, 45, "16/06/2009 08:00", table, 0);
      assertEquals(6.25, TimeUnit.DAYS, 1.375, TimeUnit.WEEKS, 60, "31/12/2049 23:59", table, 1);

      // Table C
      table = resource.getCostRateTable(2);
      assertEquals(2, table.size());
      assertEquals(65, TimeUnit.HOURS, 70, TimeUnit.HOURS, 75, "17/06/2009 08:00", table, 0);
      assertEquals(0.5, TimeUnit.MONTHS, 0.040, TimeUnit.YEARS, 90, "31/12/2049 23:59", table, 1);

      // Table D
      table = resource.getCostRateTable(3);
      assertEquals(2, table.size());
      assertEquals(95, TimeUnit.HOURS, 100, TimeUnit.HOURS, 105, "18/06/2009 08:00", table, 0);
      assertEquals(110, TimeUnit.HOURS, 115, TimeUnit.HOURS, 120, "31/12/2049 23:59", table, 1);

      // Table E
      table = resource.getCostRateTable(4);
      assertEquals(2, table.size());
      assertEquals(125, TimeUnit.HOURS, 130, TimeUnit.HOURS, 135, "19/06/2009 08:00", table, 0);
      assertEquals(140, TimeUnit.HOURS, 145, TimeUnit.HOURS, 150, "31/12/2049 23:59", table, 1);

      //
      // Validate date-based row selection
      //
      CostRateTableEntry entry = table.getEntryByDate(m_df.parse("18/06/2009 07:00"));
      assertEquals(125, TimeUnit.HOURS, 130, TimeUnit.HOURS, 135, "19/06/2009 08:00", entry);
      entry = table.getEntryByDate(m_df.parse("19/06/2009 10:00"));
      assertEquals(140, TimeUnit.HOURS, 145, TimeUnit.HOURS, 150, "31/12/2049 23:59", table, 1);
   }

   /**
    * Test a single row from a cost rate table.
    * 
    * @param standardRate expected standard rate
    * @param standardRateFormat expected standard rate format
    * @param overtimeRate expected overtime rate
    * @param overtimeRateFormat expected overtime rate format
    * @param perUseRate expected per use rate
    * @param endDate expected end date
    * @param table table instance under test
    * @param index index of table row under test
    */
   private void assertEquals(double standardRate, TimeUnit standardRateFormat, double overtimeRate, TimeUnit overtimeRateFormat, double perUseRate, String endDate, CostRateTable table, int index)
   {
      CostRateTableEntry entry = table.get(index);
      assertEquals(standardRate, standardRateFormat, overtimeRate, overtimeRateFormat, perUseRate, endDate, entry);
   }

   /**
    * Test a single row from a cost rate table.
    * 
    * @param standardRate expected standard rate
    * @param standardRateFormat expected standard rate format
    * @param overtimeRate expected overtime rate
    * @param overtimeRateFormat expected overtime rate format
    * @param costPerUse expected cost per use
    * @param endDate expected end date
    * @param entry table entry instance under test
    */
   private void assertEquals(double standardRate, TimeUnit standardRateFormat, double overtimeRate, TimeUnit overtimeRateFormat, double costPerUse, String endDate, CostRateTableEntry entry)
   {
      assertEquals(standardRate, entry.getStandardRate().getAmount(), 0.009);
      assertEquals(overtimeRate, entry.getOvertimeRate().getAmount(), 0.009);
      assertEquals(costPerUse, entry.getCostPerUse().doubleValue(), 0);
      assertEquals(endDate, m_df.format(entry.getEndDate()));
      assertEquals(standardRateFormat, entry.getStandardRateFormat());
      assertEquals(overtimeRateFormat, entry.getOvertimeRateFormat());
   }

   private DateFormat m_df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
}
