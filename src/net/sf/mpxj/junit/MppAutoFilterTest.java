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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.mpxj.Duration;
import net.sf.mpxj.Filter;
import net.sf.mpxj.FilterCriteria;
import net.sf.mpxj.FilterCriteriaLogicType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TestOperator;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.GanttChartView;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppAutoFilterTest extends MPXJTestCase
{

   /**
    * Test auto filter data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9autofilter.mpp");
      testFilters(mpp);
   }

   /**
    * Test filter data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12autofilter.mpp");
      testFilters(mpp);
   }

   /**
    * Test filter data.
    * 
    * @param mpp ProjectFile instance
    */
   private void testFilters(ProjectFile mpp)
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      GanttChartView view = (GanttChartView) mpp.getViews().get(0);

      //
      // Test all data types
      //
      Filter filter = view.getAutoFilterByType(TaskField.DURATION);
      FilterCriteria criteria = filter.getCriteria().get(0);
      assertEquals(TaskField.DURATION, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(9, (int) ((Duration) criteria.getValue(0)).getDuration());
      assertEquals(TimeUnit.DAYS, ((Duration) criteria.getValue(0)).getUnits());
      assertEquals(FilterCriteriaLogicType.BETWEEN_BLOCK_AND, criteria.getCriteriaLogic());
      criteria = filter.getCriteria().get(1);
      assertEquals(TaskField.DURATION, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(99, (int) ((Duration) criteria.getValue(0)).getDuration());
      assertEquals(TimeUnit.DAYS, ((Duration) criteria.getValue(0)).getUnits());

      filter = view.getAutoFilterByType(TaskField.NUMBER1);
      criteria = filter.getCriteria().get(0);
      assertEquals(TaskField.NUMBER1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(9, ((Double) criteria.getValue(0)).intValue());
      assertEquals(FilterCriteriaLogicType.BETWEEN_BLOCK_OR, criteria.getCriteriaLogic());
      criteria = filter.getCriteria().get(1);
      assertEquals(TaskField.NUMBER1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(99, ((Double) criteria.getValue(0)).intValue());

      filter = view.getAutoFilterByType(TaskField.PERCENT_COMPLETE);
      criteria = filter.getCriteria().get(0);
      assertEquals(TaskField.PERCENT_COMPLETE, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(9, ((Double) criteria.getValue(0)).intValue());
      assertEquals(FilterCriteriaLogicType.BETWEEN_BLOCK_OR, criteria.getCriteriaLogic());
      criteria = filter.getCriteria().get(1);
      assertEquals(TaskField.PERCENT_COMPLETE, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(99, ((Double) criteria.getValue(0)).intValue());

      filter = view.getAutoFilterByType(TaskField.COST1);
      criteria = filter.getCriteria().get(0);
      assertEquals(TaskField.COST1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(9, ((Double) criteria.getValue(0)).intValue());
      assertEquals(FilterCriteriaLogicType.BETWEEN_BLOCK_OR, criteria.getCriteriaLogic());
      criteria = filter.getCriteria().get(1);
      assertEquals(TaskField.COST1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(99, ((Double) criteria.getValue(0)).intValue());

      filter = view.getAutoFilterByType(TaskField.TEXT1);
      criteria = filter.getCriteria().get(0);
      assertEquals(TaskField.TEXT1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals("9", criteria.getValue(0));
      assertEquals(FilterCriteriaLogicType.BETWEEN_BLOCK_OR, criteria.getCriteriaLogic());
      criteria = filter.getCriteria().get(1);
      assertEquals(TaskField.TEXT1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals("99", criteria.getValue(0));

      filter = view.getAutoFilterByType(TaskField.FLAG1);
      criteria = filter.getCriteria().get(0);
      assertEquals(TaskField.FLAG1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(Boolean.TRUE, criteria.getValue(0));
      assertEquals(FilterCriteriaLogicType.BETWEEN_BLOCK_OR, criteria.getCriteriaLogic());
      criteria = filter.getCriteria().get(1);
      assertEquals(TaskField.FLAG1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals(Boolean.FALSE, criteria.getValue(0));

      filter = view.getAutoFilterByType(TaskField.DATE1);
      criteria = filter.getCriteria().get(0);
      assertEquals(TaskField.DATE1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals("09/09/1999", df.format((Date) criteria.getValue(0)));
      assertEquals(FilterCriteriaLogicType.BETWEEN_BLOCK_OR, criteria.getCriteriaLogic());
      criteria = filter.getCriteria().get(1);
      assertEquals(TaskField.DATE1, criteria.getField());
      assertEquals(TestOperator.EQUALS, criteria.getOperator());
      assertEquals("09/09/2009", df.format((Date) criteria.getValue(0)));

      //
      // Test all operators
      //
      filter = view.getAutoFilterByType(TaskField.NUMBER2);
      criteria = filter.getCriteria().get(0);
      assertEquals(TestOperator.DOES_NOT_EQUAL, criteria.getOperator());

      filter = view.getAutoFilterByType(TaskField.NUMBER3);
      criteria = filter.getCriteria().get(0);
      assertEquals(TestOperator.IS_GREATER_THAN, criteria.getOperator());

      filter = view.getAutoFilterByType(TaskField.NUMBER4);
      criteria = filter.getCriteria().get(0);
      assertEquals(TestOperator.IS_GREATER_THAN_OR_EQUAL_TO, criteria.getOperator());

      filter = view.getAutoFilterByType(TaskField.NUMBER5);
      criteria = filter.getCriteria().get(0);
      assertEquals(TestOperator.IS_LESS_THAN, criteria.getOperator());

      filter = view.getAutoFilterByType(TaskField.NUMBER6);
      criteria = filter.getCriteria().get(0);
      assertEquals(TestOperator.IS_LESS_THAN_OR_EQUAL_TO, criteria.getOperator());

      filter = view.getAutoFilterByType(TaskField.NUMBER7);
      criteria = filter.getCriteria().get(0);
      assertEquals(TestOperator.IS_WITHIN, criteria.getOperator());

      filter = view.getAutoFilterByType(TaskField.NUMBER8);
      criteria = filter.getCriteria().get(0);
      assertEquals(TestOperator.IS_NOT_WITHIN, criteria.getOperator());
   }
}
