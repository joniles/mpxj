/*
 * file:       MppFilterTest.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2006
 * date:       7-November-2006
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
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TestOperator;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppFilterTest extends MPXJTestCase 
{
   
   /**
    * Test filter data read from an MPP9 file.
    * 
    * @throws Exception
    */   
    public void testMpp9Filters() 
       throws Exception 
    {
       ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp9filter.mpp");        
       testFilters(mpp);
    }

    /**
     * Test filter data read from an MPP12 file.
     * 
     * @throws Exception
     */       
    public void testMpp12Filters() 
       throws Exception 
    {
       ProjectFile mpp = new MPPReader().read (m_basedir + "/mpp12filter.mpp");
       testFilters(mpp);
    }

    /**
     * Test filter data.
     * 
     * @param mpp ProjectFile instance
     */
    private void testFilters(ProjectFile mpp) 
    {
       DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
       
       //
       // Test all data types
       //
       Filter filter = mpp.getFilterByName("Filter 1");
       FilterCriteria criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.DURATION1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(45, (int)((Duration)criteria.getValue()).getDuration());
       assertEquals(TimeUnit.DAYS, ((Duration)criteria.getValue()).getUnits());
       
       filter = mpp.getFilterByName("Filter 2");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.NUMBER1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(99, ((Double)criteria.getValue()).intValue());
       
       filter = mpp.getFilterByName("Filter 3");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.PERCENT_COMPLETE, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(10, ((Integer)criteria.getValue()).intValue());

       filter = mpp.getFilterByName("Filter 4");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.COST1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(99, ((Double)criteria.getValue()).intValue());

       filter = mpp.getFilterByName("Filter 5");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.TEXT1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals("Hello", (String)criteria.getValue());
       
       filter = mpp.getFilterByName("Filter 6");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.FLAG1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(true, ((Boolean)criteria.getValue()).booleanValue());
       
       filter = mpp.getFilterByName("Filter 7");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.DATE1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals("18/07/2006", df.format((Date)criteria.getValue()));       
       
       //
       // Test all operators
       //
       filter = mpp.getFilterByName("Filter 8");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       
       criteria = (FilterCriteria)filter.getCriteria().get(1);
       assertEquals(TestOperator.DOES_NOT_EQUAL, criteria.getOperator());
       
       criteria = (FilterCriteria)filter.getCriteria().get(2);
       assertEquals(TestOperator.IS_GREATER_THAN, criteria.getOperator());
       
       criteria = (FilterCriteria)filter.getCriteria().get(3);
       assertEquals(TestOperator.IS_GREATER_THAN_OR_EQUAL_TO, criteria.getOperator());
       
       criteria = (FilterCriteria)filter.getCriteria().get(4);
       assertEquals(TestOperator.IS_LESS_THAN, criteria.getOperator());
       
       criteria = (FilterCriteria)filter.getCriteria().get(5);
       assertEquals(TestOperator.IS_LESS_THAN_OR_EQUAL_TO, criteria.getOperator());
       
       criteria = (FilterCriteria)filter.getCriteria().get(6);
       assertEquals(TestOperator.IS_WITHIN, criteria.getOperator());
       
       criteria = (FilterCriteria)filter.getCriteria().get(7);
       assertEquals(TestOperator.IS_NOT_WITHIN, criteria.getOperator());
    }
}
