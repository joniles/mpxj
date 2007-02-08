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
import net.sf.mpxj.Task;
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
       testFilterEvaluation(mpp);
       testLogicalOperatorEvaluation(mpp);
       testParameters(mpp);
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
       testFilterEvaluation(mpp);
       testLogicalOperatorEvaluation(mpp);
       testParameters(mpp);
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
       assertEquals(45, (int)((Duration)criteria.getValue(0)).getDuration());
       assertEquals(TimeUnit.DAYS, ((Duration)criteria.getValue(0)).getUnits());
       
       filter = mpp.getFilterByName("Filter 2");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.NUMBER1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(99, ((Double)criteria.getValue(0)).intValue());
       
       filter = mpp.getFilterByName("Filter 3");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.PERCENT_COMPLETE, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(10, ((Double)criteria.getValue(0)).intValue());

       filter = mpp.getFilterByName("Filter 4");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.COST1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(99, ((Double)criteria.getValue(0)).intValue());

       filter = mpp.getFilterByName("Filter 5");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.TEXT1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals("Hello", (String)criteria.getValue(0));
       
       filter = mpp.getFilterByName("Filter 6");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.FLAG1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals(true, ((Boolean)criteria.getValue(0)).booleanValue());
       
       filter = mpp.getFilterByName("Filter 7");
       criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertEquals(TaskField.DATE1, criteria.getField());
       assertEquals(TestOperator.EQUALS, criteria.getOperator());
       assertEquals("18/07/2006", df.format((Date)criteria.getValue(0)));       
       
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
    
    /**
     * Validate filter evaluation.
     * 
     * @param mpp project file
     */
    private void testFilterEvaluation (ProjectFile mpp)
    {
       Task task1 = mpp.getTaskByID(new Integer(1));
       Task task2 = mpp.getTaskByID(new Integer(2));
       
       //
       // Test different data types
       //
       Filter filter = mpp.getFilterByName("Filter 1");
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));
       
       filter = mpp.getFilterByName("Filter 2");
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));
       
       filter = mpp.getFilterByName("Filter 3");
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));

       filter = mpp.getFilterByName("Filter 4");
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));

       filter = mpp.getFilterByName("Filter 5");
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));

       filter = mpp.getFilterByName("Filter 6");
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));

       filter = mpp.getFilterByName("Filter 7");
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));

       //       
       // Test different operator types
       //
       Task task3 = mpp.getTaskByID(new Integer(3));
       Task task4 = mpp.getTaskByID(new Integer(4));
       Task task5 = mpp.getTaskByID(new Integer(5));
       Task task6 = mpp.getTaskByID(new Integer(6));
       Task task7 = mpp.getTaskByID(new Integer(7));
       
       // Number1 != 10
       filter = mpp.getFilterByName("Filter 9");
       assertTrue(filter.evaluate(task3));
       assertFalse(filter.evaluate(task4));
       assertTrue(filter.evaluate(task5));

       // Number1 > 10
       filter = mpp.getFilterByName("Filter 10");
       assertFalse(filter.evaluate(task3));
       assertFalse(filter.evaluate(task4));
       assertTrue(filter.evaluate(task5));

       // Number1 >= 10
       filter = mpp.getFilterByName("Filter 11");
       assertFalse(filter.evaluate(task3));
       assertTrue(filter.evaluate(task4));
       assertTrue(filter.evaluate(task5));

       // Number1 < 10
       filter = mpp.getFilterByName("Filter 12");
       assertTrue(filter.evaluate(task3));
       assertFalse(filter.evaluate(task4));
       assertFalse(filter.evaluate(task5));
       
       // Number1 <= 10
       filter = mpp.getFilterByName("Filter 13");
       assertTrue(filter.evaluate(task3));
       assertTrue(filter.evaluate(task4));
       assertFalse(filter.evaluate(task5));
       
       // Number1 is within 10, 12
       filter = mpp.getFilterByName("Filter 14");
       assertFalse(filter.evaluate(task3));
       assertTrue(filter.evaluate(task4));
       assertTrue(filter.evaluate(task5));
       assertTrue(filter.evaluate(task6));
       assertFalse(filter.evaluate(task7));
       
       // Number1 is not within 10, 12
       filter = mpp.getFilterByName("Filter 15");
       assertTrue(filter.evaluate(task3));
       assertFalse(filter.evaluate(task4));
       assertFalse(filter.evaluate(task5));
       assertFalse(filter.evaluate(task6));
       assertTrue(filter.evaluate(task7));
       
       // Text1 contains aaa
       filter = mpp.getFilterByName("Filter 16");
       assertFalse(filter.evaluate(task3));
       assertFalse(filter.evaluate(task4));
       assertTrue(filter.evaluate(task5));
       assertTrue(filter.evaluate(task6));
       assertFalse(filter.evaluate(task7));

       // Text1 does not contain aaa
       filter = mpp.getFilterByName("Filter 17");
       assertTrue(filter.evaluate(task3));
       assertTrue(filter.evaluate(task4));
       assertFalse(filter.evaluate(task5));
       assertFalse(filter.evaluate(task6));
       assertTrue(filter.evaluate(task7));

       // Text1 contains exactly aaa
       filter = mpp.getFilterByName("Filter 18");
       assertFalse(filter.evaluate(task3));
       assertFalse(filter.evaluate(task4));
       assertTrue(filter.evaluate(task5));
       assertFalse(filter.evaluate(task6));
       assertFalse(filter.evaluate(task7));       
       
       // Create and test an "is any value" filter
       filter = new Filter ();
       FilterCriteria criteria = new FilterCriteria(mpp);
       filter.addCriteria(criteria);
       criteria.setField(TaskField.DEADLINE);
       criteria.setOperator(TestOperator.IS_ANY_VALUE);
       assertTrue(filter.evaluate(task1));
       
       // Create and test a boolean filter
       filter = new Filter ();
       criteria = new FilterCriteria(mpp);
       filter.addCriteria(criteria);
       criteria.setField(TaskField.FLAG1);
       criteria.setOperator(TestOperator.EQUALS);
       criteria.setValue(0, Boolean.TRUE);
       assertTrue(filter.evaluate(task1));
       assertFalse(filter.evaluate(task2));       
    }

    /**
     * Validate filter logical operator evaluation.
     * 
     * @param mpp project file
     */
    private void testLogicalOperatorEvaluation (ProjectFile mpp)
    {
       Task task6 = mpp.getTaskByID(new Integer(6));
       Task task7 = mpp.getTaskByID(new Integer(7));
       
       // Number1==13 && Number2==7
       Filter filter = mpp.getFilterByName("Filter 19");
       assertFalse(filter.evaluate(task6));
       assertTrue(filter.evaluate(task7));       

       // Number1==12 || Number1==13
       filter = mpp.getFilterByName("Filter 20");
       assertTrue(filter.evaluate(task6));
       assertTrue(filter.evaluate(task7));       
       
       // Duration==10d && Number1==13 && Number2==7
       filter = mpp.getFilterByName("Filter 21");
       assertFalse(filter.evaluate(task6));
       assertTrue(filter.evaluate(task7));       

       // Duration==10d || Number1==12 || Number2==7
       filter = mpp.getFilterByName("Filter 22");
       assertTrue(filter.evaluate(task6));
       assertTrue(filter.evaluate(task7));       

       // Duration==10d && Number1==12 || Number1==13
       filter = mpp.getFilterByName("Filter 23");
       assertTrue(filter.evaluate(task6));
       assertTrue(filter.evaluate(task7));       
    }
    
    
    /**
     * Validate the prompts for user supplied values.
     * 
     * @param mpp project file
     */
    private void testParameters(ProjectFile mpp) 
    {
       Filter filter = mpp.getFilterByName("Filter 24");
       assertNotNull(filter);

       FilterCriteria criteria = (FilterCriteria)filter.getCriteria().get(0);
       assertNull(criteria.getValue(0));
       assertEquals("Duration1", criteria.getPromptText(0));
       
       criteria = (FilterCriteria)filter.getCriteria().get(1);
       assertNull(criteria.getValue(0));
       assertEquals("Number1", criteria.getPromptText(0));
       
       criteria = (FilterCriteria)filter.getCriteria().get(2);
       assertNull(criteria.getValue(0));
       assertEquals("%Complete", criteria.getPromptText(0));       
       
       criteria = (FilterCriteria)filter.getCriteria().get(3);
       assertNull(criteria.getValue(0));
       assertEquals("Cost1", criteria.getPromptText(0));       
       
       criteria = (FilterCriteria)filter.getCriteria().get(4);
       assertNull(criteria.getValue(0));
       assertEquals("Text1", criteria.getPromptText(0));       
       
       criteria = (FilterCriteria)filter.getCriteria().get(5);
       assertNull(criteria.getValue(0));
       assertEquals("Flag1", criteria.getPromptText(0));       
       
       criteria = (FilterCriteria)filter.getCriteria().get(6);
       assertNull(criteria.getValue(0));
       assertEquals("Date1", criteria.getPromptText(0));       
       
       criteria = (FilterCriteria)filter.getCriteria().get(7);
       assertNull(criteria.getValue(0));
       assertNull(criteria.getValue(1));
       assertEquals("LHS", criteria.getPromptText(0));       
       assertEquals("RHS", criteria.getPromptText(1));       
       
       criteria = (FilterCriteria)filter.getCriteria().get(8);
       assertNull(criteria.getValue(0));
       assertNotNull(criteria.getValue(1));
       assertEquals("LHS", criteria.getPromptText(0));       
       assertNull(criteria.getPromptText(1));       

       criteria = (FilterCriteria)filter.getCriteria().get(9);
       assertNotNull(criteria.getValue(0));
       assertNull(criteria.getValue(1));
       assertNull(criteria.getPromptText(0));       
       assertEquals("RHS", criteria.getPromptText(1));       
    }
    
    /**
     * Test null value handling.
     */
    public void testNullValueTestOperators ()
    {
       TestOperator operator = TestOperator.CONTAINS;
       assertFalse(operator.evaluate(null, null));
       assertFalse(operator.evaluate("", null));
       assertFalse(operator.evaluate(null, ""));
       
       operator = TestOperator.CONTAINS_EXACTLY;
       assertFalse(operator.evaluate(null, null));
       assertFalse(operator.evaluate("", null));
       assertFalse(operator.evaluate(null, ""));
       
       operator = TestOperator.DOES_NOT_CONTAIN;
       assertTrue(operator.evaluate(null, null));
       assertTrue(operator.evaluate("", null));
       assertTrue(operator.evaluate(null, ""));
       
       operator = TestOperator.DOES_NOT_EQUAL;
       assertFalse(operator.evaluate(null, null));
       assertTrue(operator.evaluate("", null));
       assertTrue(operator.evaluate(null, ""));

       operator = TestOperator.EQUALS;
       assertTrue(operator.evaluate(null, null));
       assertFalse(operator.evaluate("", null));
       assertFalse(operator.evaluate(null, ""));
       
       operator = TestOperator.IS_GREATER_THAN;
       assertFalse(operator.evaluate(null, null));
       assertFalse(operator.evaluate("", null));
       assertTrue(operator.evaluate(null, "")); 
       
       operator = TestOperator.IS_GREATER_THAN_OR_EQUAL_TO;
       assertTrue(operator.evaluate(null, null));
       assertFalse(operator.evaluate("", null));
       assertTrue(operator.evaluate(null, "")); 
       
       operator = TestOperator.IS_LESS_THAN;
       assertFalse(operator.evaluate(null, null));
       assertTrue(operator.evaluate("", null));
       assertFalse(operator.evaluate(null, "")); 
       
       operator = TestOperator.IS_LESS_THAN_OR_EQUAL_TO;
       assertTrue(operator.evaluate(null, null));
       assertTrue(operator.evaluate("", null));
       assertFalse(operator.evaluate(null, "")); 
       
       Object[] allNull = new Object[]{null, null};
       Object[] lhsNull = new Object[]{null, new Integer(10)};
       Object[] rhsNull = new Object[]{new Integer(1), null};
       
       operator = TestOperator.IS_NOT_WITHIN;
       assertTrue(operator.evaluate(null, allNull));
       assertTrue(operator.evaluate(null, lhsNull));
       assertTrue(operator.evaluate(null, rhsNull));       
       assertTrue(operator.evaluate(new Integer(5), allNull));
       assertTrue(operator.evaluate(new Integer(5), lhsNull));
       assertTrue(operator.evaluate(new Integer(5), rhsNull));       
       assertTrue(operator.evaluate(allNull, new Integer(5))); 
       assertTrue(operator.evaluate(lhsNull, new Integer(5))); 
       assertTrue(operator.evaluate(rhsNull, new Integer(5))); 
       
       operator = TestOperator.IS_WITHIN;
       assertFalse(operator.evaluate(null, allNull));
       assertFalse(operator.evaluate(null, lhsNull));
       assertFalse(operator.evaluate(null, rhsNull));       
       assertFalse(operator.evaluate(new Integer(5), allNull));
       assertFalse(operator.evaluate(new Integer(5), lhsNull));
       assertFalse(operator.evaluate(new Integer(5), rhsNull));       
       assertFalse(operator.evaluate(allNull, new Integer(5))); 
       assertFalse(operator.evaluate(lhsNull, new Integer(5))); 
       assertFalse(operator.evaluate(rhsNull, new Integer(5)));               
    }
}
