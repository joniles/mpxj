/*
 * file:       MppFilterTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
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

package org.mpxj.junit;

import static org.junit.Assert.*;

import java.util.List;

import org.mpxj.Filter;
import org.mpxj.FilterContainer;
import org.mpxj.GenericCriteria;
import org.mpxj.GenericCriteriaPrompt;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TestOperator;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppFilterTest
{
   /**
    * Test filter data read from an MPP9 file.
    */
   @Test public void testMpp9Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9filter.mpp"));
      executeTests(mpp);
   }

   /**
    * Test filter data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9FiltersFrom12()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9filter-from12.mpp");
      //executeTests(mpp);
   }

   /**
    * Test filter data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9FiltersFrom14()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9filter-from14.mpp");
      //executeTests(mpp);
   }

   /**
    * Test filter data read from an MPP12 file.
    */
   @Test public void testMpp12Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12filter.mpp"));
      executeTests(mpp);
   }

   /**
    * Test filter data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12FiltersFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12filter-from14.mpp"));
      executeTests(mpp);
   }

   /**
    * Test filter data read from an MPP14 file.
    */
   @Test public void testMpp14Filters() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14filter.mpp"));
      executeTests(mpp);
   }

   /**
    * Main entry point to execute all tests.
    *
    * @param mpp project file
    */
   private void executeTests(ProjectFile mpp)
   {
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
      //
      // Test all data types
      //
      FilterContainer filters = mpp.getFilters();

      Filter filter = filters.getFilterByName("Filter 1");
      assertEquals("(Duration1 EQUALS 9.0w)", filter.getCriteria().toString());

      filter = filters.getFilterByName("Filter 2");
      assertEquals("(Number1 EQUALS 99.0)", filter.getCriteria().toString());

      filter = filters.getFilterByName("Filter 3");
      assertEquals("(% Complete EQUALS 10.0)", filter.getCriteria().toString());

      filter = filters.getFilterByName("Filter 4");
      assertEquals("(Cost1 EQUALS 99.0)", filter.getCriteria().toString());

      filter = filters.getFilterByName("Filter 5");
      assertEquals("(Text1 EQUALS Hello)", filter.getCriteria().toString());

      filter = filters.getFilterByName("Filter 6");
      assertEquals("(Flag1 EQUALS true)", filter.getCriteria().toString());

      filter = filters.getFilterByName("Filter 7");
      assertEquals("(Date1 EQUALS 18/07/2006 00:00)", filter.getCriteria().toString());

      //
      // Test all operators
      //
      filter = filters.getFilterByName("Filter 8");
      assertEquals("((Number1 EQUALS 10.0) AND (Number2 DOES_NOT_EQUAL 10.0) AND (Number3 IS_GREATER_THAN 10.0) AND (Number4 IS_GREATER_THAN_OR_EQUAL_TO 10.0) AND (Number5 IS_LESS_THAN 10.0) AND (Number6 IS_LESS_THAN_OR_EQUAL_TO 10.0) AND (Number7 IS_WITHIN 10.0,20.0) AND (Number8 IS_NOT_WITHIN 10.0,20.0))", filter.getCriteria().toString());
   }

   /**
    * Validate filter evaluation.
    *
    * @param mpp project file
    */
   private void testFilterEvaluation(ProjectFile mpp)
   {
      Task task1 = mpp.getTaskByID(Integer.valueOf(1));
      Task task2 = mpp.getTaskByID(Integer.valueOf(2));
      FilterContainer filters = mpp.getFilters();
      ProjectProperties properties = mpp.getProjectProperties();

      //
      // Test different data types
      //
      Filter filter = filters.getFilterByName("Filter 1");
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));

      filter = filters.getFilterByName("Filter 2");
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));

      filter = filters.getFilterByName("Filter 3");
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));

      filter = filters.getFilterByName("Filter 4");
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));

      filter = filters.getFilterByName("Filter 5");
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));

      filter = filters.getFilterByName("Filter 6");
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));

      filter = filters.getFilterByName("Filter 7");
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));

      //
      // Test different operator types
      //
      Task task3 = mpp.getTaskByID(Integer.valueOf(3));
      Task task4 = mpp.getTaskByID(Integer.valueOf(4));
      Task task5 = mpp.getTaskByID(Integer.valueOf(5));
      Task task6 = mpp.getTaskByID(Integer.valueOf(6));
      Task task7 = mpp.getTaskByID(Integer.valueOf(7));

      // Number1 != 10
      filter = filters.getFilterByName("Filter 9");
      assertTrue(filter.evaluate(task3, null));
      assertFalse(filter.evaluate(task4, null));
      assertTrue(filter.evaluate(task5, null));

      // Number1 > 10
      filter = filters.getFilterByName("Filter 10");
      assertFalse(filter.evaluate(task3, null));
      assertFalse(filter.evaluate(task4, null));
      assertTrue(filter.evaluate(task5, null));

      // Number1 >= 10
      filter = filters.getFilterByName("Filter 11");
      assertFalse(filter.evaluate(task3, null));
      assertTrue(filter.evaluate(task4, null));
      assertTrue(filter.evaluate(task5, null));

      // Number1 < 10
      filter = filters.getFilterByName("Filter 12");
      assertTrue(filter.evaluate(task3, null));
      assertFalse(filter.evaluate(task4, null));
      assertFalse(filter.evaluate(task5, null));

      // Number1 <= 10
      filter = filters.getFilterByName("Filter 13");
      assertTrue(filter.evaluate(task3, null));
      assertTrue(filter.evaluate(task4, null));
      assertFalse(filter.evaluate(task5, null));

      // Number1 is within 10, 12
      filter = filters.getFilterByName("Filter 14");
      assertFalse(filter.evaluate(task3, null));
      assertTrue(filter.evaluate(task4, null));
      assertTrue(filter.evaluate(task5, null));
      assertTrue(filter.evaluate(task6, null));
      assertFalse(filter.evaluate(task7, null));

      // Number1 is not within 10, 12
      filter = filters.getFilterByName("Filter 15");
      assertTrue(filter.evaluate(task3, null));
      assertFalse(filter.evaluate(task4, null));
      assertFalse(filter.evaluate(task5, null));
      assertFalse(filter.evaluate(task6, null));
      assertTrue(filter.evaluate(task7, null));

      // Text1 contains aaa
      filter = filters.getFilterByName("Filter 16");
      assertFalse(filter.evaluate(task3, null));
      assertFalse(filter.evaluate(task4, null));
      assertTrue(filter.evaluate(task5, null));
      assertTrue(filter.evaluate(task6, null));
      assertFalse(filter.evaluate(task7, null));

      // Text1 does not contain aaa
      filter = filters.getFilterByName("Filter 17");
      assertTrue(filter.evaluate(task3, null));
      assertTrue(filter.evaluate(task4, null));
      assertFalse(filter.evaluate(task5, null));
      assertFalse(filter.evaluate(task6, null));
      assertTrue(filter.evaluate(task7, null));

      // Text1 contains exactly aaa
      filter = filters.getFilterByName("Filter 18");
      assertFalse(filter.evaluate(task3, null));
      assertFalse(filter.evaluate(task4, null));
      assertTrue(filter.evaluate(task5, null));
      assertFalse(filter.evaluate(task6, null));
      assertFalse(filter.evaluate(task7, null));

      // Create and test an "is any value" filter
      filter = new Filter();
      GenericCriteria criteria = new GenericCriteria(properties);
      filter.setCriteria(criteria);
      criteria.setLeftValue(TaskField.DEADLINE);
      criteria.setOperator(TestOperator.IS_ANY_VALUE);
      assertTrue(filter.evaluate(task1, null));

      // Create and test a boolean filter
      filter = new Filter();
      criteria = new GenericCriteria(properties);
      filter.setCriteria(criteria);
      criteria.setLeftValue(TaskField.FLAG1);
      criteria.setOperator(TestOperator.EQUALS);
      criteria.setRightValue(0, Boolean.TRUE);
      assertTrue(filter.evaluate(task1, null));
      assertFalse(filter.evaluate(task2, null));
   }

   /**
    * Validate filter logical operator evaluation.
    *
    * @param mpp project file
    */
   private void testLogicalOperatorEvaluation(ProjectFile mpp)
   {
      Task task6 = mpp.getTaskByID(Integer.valueOf(6));
      Task task7 = mpp.getTaskByID(Integer.valueOf(7));
      FilterContainer filters = mpp.getFilters();

      // Number1==13 && Number2==7
      Filter filter = filters.getFilterByName("Filter 19");
      assertFalse(filter.evaluate(task6, null));
      assertTrue(filter.evaluate(task7, null));

      // Number1==12 || Number1==13
      filter = filters.getFilterByName("Filter 20");
      assertTrue(filter.evaluate(task6, null));
      assertTrue(filter.evaluate(task7, null));

      // Duration==10d && Number1==13 && Number2==7
      filter = filters.getFilterByName("Filter 21");
      assertFalse(filter.evaluate(task6, null));
      assertTrue(filter.evaluate(task7, null));

      // Duration==10d || Number1==12 || Number2==7
      filter = filters.getFilterByName("Filter 22");
      assertTrue(filter.evaluate(task6, null));
      assertTrue(filter.evaluate(task7, null));

      // Duration==10d && Number1==12 || Number1==13
      filter = filters.getFilterByName("Filter 23");
      assertTrue(filter.evaluate(task6, null));
      assertTrue(filter.evaluate(task7, null));
   }

   /**
    * Validate the prompts for user supplied values.
    *
    * @param mpp project file
    */
   private void testParameters(ProjectFile mpp)
   {
      Filter filter = mpp.getFilters().getFilterByName("Filter 24");
      assertNotNull(filter);

      List<GenericCriteriaPrompt> prompts = filter.getPrompts();
      assertEquals("Duration1", prompts.get(0).getPrompt());
      assertEquals("Number1", prompts.get(1).getPrompt());
      assertEquals("%Complete", prompts.get(2).getPrompt());
      assertEquals("Cost1", prompts.get(3).getPrompt());
      assertEquals("Text1", prompts.get(4).getPrompt());
      assertEquals("Flag1", prompts.get(5).getPrompt());
      assertEquals("Date1", prompts.get(6).getPrompt());
      assertEquals("LHS", prompts.get(7).getPrompt());
      assertEquals("RHS", prompts.get(8).getPrompt());
      assertEquals("LHS", prompts.get(9).getPrompt());
      assertEquals("RHS", prompts.get(10).getPrompt());
   }

   /**
    * Test null value handling.
    */
   @Test public void testNullValueTestOperators()
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

      Object[] allNull = new Object[]
      {
         null,
         null
      };
      Object[] lhsNull = new Object[]
      {
         null,
         Integer.valueOf(10)
      };
      Object[] rhsNull = new Object[]
      {
         Integer.valueOf(1),
         null
      };

      operator = TestOperator.IS_NOT_WITHIN;
      assertFalse(operator.evaluate(null, allNull));
      assertFalse(operator.evaluate(null, lhsNull));
      assertFalse(operator.evaluate(null, rhsNull));
      assertTrue(operator.evaluate(Integer.valueOf(5), allNull));
      assertTrue(operator.evaluate(Integer.valueOf(5), lhsNull));
      assertTrue(operator.evaluate(Integer.valueOf(5), rhsNull));
      assertTrue(operator.evaluate(allNull, Integer.valueOf(5)));
      assertTrue(operator.evaluate(lhsNull, Integer.valueOf(5)));
      assertTrue(operator.evaluate(rhsNull, Integer.valueOf(5)));

      operator = TestOperator.IS_WITHIN;
      assertTrue(operator.evaluate(null, allNull));
      assertTrue(operator.evaluate(null, lhsNull));
      assertTrue(operator.evaluate(null, rhsNull));
      assertFalse(operator.evaluate(Integer.valueOf(5), allNull));
      assertFalse(operator.evaluate(Integer.valueOf(5), lhsNull));
      assertFalse(operator.evaluate(Integer.valueOf(5), rhsNull));
      assertFalse(operator.evaluate(allNull, Integer.valueOf(5)));
      assertFalse(operator.evaluate(lhsNull, Integer.valueOf(5)));
      assertFalse(operator.evaluate(rhsNull, Integer.valueOf(5)));
   }
}
