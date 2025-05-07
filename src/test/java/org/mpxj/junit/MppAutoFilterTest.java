/*
 * file:       MppAutoFilterTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       28-November-2006
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
import org.mpxj.Filter;
import org.mpxj.ProjectFile;
import org.mpxj.TaskField;
import org.mpxj.mpp.GanttChartView;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppAutoFilterTest
{

   /**
    * Test auto filter data read from an MPP9 file.
    */
   @Test public void testMpp9Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9autofilter.mpp"));
      testFilters(mpp);
   }

   /**
    * Test auto filter data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9FiltersFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9autofilter-from12.mpp"));
      testFilters(mpp);
   }

   /**
    * Test auto filter data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9FiltersFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9autofilter-from14.mpp"));
      testFilters(mpp);
   }

   /**
    * Test filter data read from an MPP12 file.
    */
   @Test public void testMpp12Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12autofilter.mpp"));
      testFilters(mpp);
   }

   /**
    * Test filter data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12FiltersFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12autofilter-from14.mpp"));
      testFilters(mpp);
   }

   /**
    * Test filter data read from an MPP14 file.
    */
   @Test public void testMpp14Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14autofilter.mpp"));
      testFilters(mpp);
   }

   /**
    * Test filter data.
    *
    * @param mpp ProjectFile instance
    */
   private void testFilters(ProjectFile mpp)
   {
      //DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      GanttChartView view = (GanttChartView) mpp.getViews().get(0);
      assertEquals("Gantt Chart", view.getName());

      //
      // Test all data types
      //
      Filter filter = view.getAutoFilterByType(TaskField.DURATION);
      assertEquals("((Duration EQUALS 9.0d) AND (Duration EQUALS 99.0d))", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.NUMBER1);
      assertEquals("((Number1 EQUALS 9.0) OR (Number1 EQUALS 99.0))", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.PERCENT_COMPLETE);
      assertEquals("((% Complete EQUALS 9.0) OR (% Complete EQUALS 99.0))", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.COST1);
      assertEquals("((Cost1 EQUALS 9.0) OR (Cost1 EQUALS 99.0))", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.TEXT1);
      assertEquals("((Text1 EQUALS 9) OR (Text1 EQUALS 99))", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.FLAG1);
      assertEquals("((Flag1 EQUALS true) OR (Flag1 EQUALS false))", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.DATE1);
      assertEquals("((Date1 EQUALS 09/09/1999 00:00) OR (Date1 EQUALS 09/09/2009 00:00))", filter.getCriteria().toString());

      //
      // Test all operators
      //
      filter = view.getAutoFilterByType(TaskField.NUMBER2);
      assertEquals("(Number2 DOES_NOT_EQUAL 1.0)", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.NUMBER3);
      assertEquals("(Number3 IS_GREATER_THAN 1.0)", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.NUMBER4);
      assertEquals("(Number4 IS_GREATER_THAN_OR_EQUAL_TO 1.0)", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.NUMBER5);
      assertEquals("(Number5 IS_LESS_THAN 1.0)", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.NUMBER6);
      assertEquals("(Number6 IS_LESS_THAN_OR_EQUAL_TO 1.0)", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.NUMBER7);
      assertEquals("(Number7 IS_WITHIN 1.0,2.0)", filter.getCriteria().toString());

      filter = view.getAutoFilterByType(TaskField.NUMBER8);
      assertEquals("(Number8 IS_NOT_WITHIN 1.0,2.0)", filter.getCriteria().toString());
   }
}
