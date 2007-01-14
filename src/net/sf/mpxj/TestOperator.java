/*
 * file:       TestOperator.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2006
 * date:       15/02/2006
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

package net.sf.mpxj;



/**
 * This class represents the set of operators used to perform a test
 * between two or more operands.
 */
public final class TestOperator
{
   /**
    * This constructor takes the numeric enumerated representation of a
    * test operator and populates the class instance appropriately.
    *
    * @param type int version of the test operator
    */
   private TestOperator (int type)
   {
      m_type = type;
   }

   /**
    * This method takes the integer enumeration of a test operator
    * and returns an appropriate class instance.
    *
    * @param type integer test operator enumeration
    * @return TestOperator instance
    */
   public static TestOperator getInstance (int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = 0;
      }

      return (TYPE_VALUES[type]);
   }

   /**
    * Accessor method used to retrieve the numeric representation of the
    * constraint type.
    *
    * @return int representation of the constraint type
    */
   public int getType ()
   {
      return (m_type);
   }

   /**
    * This method applies the operator represented by this class to the
    * supplied operands. Note that the RHS operand can be a list, allowing
    * range operators like "within" to operate.
    * 
    * @param lhs operand
    * @param rhs operand
    * @return boolean result
    */
   public boolean evaluate (Object lhs, Object rhs)
   {
      boolean result = false;
      
      switch (m_type)
      {
         case IS_ANY_VALUE_VALUE:
         {
            result = true;
            break;
         }
         
         case IS_WITHIN_VALUE:
         {
            result = evaluateWithin(lhs, rhs);
            break;
         }
         
         case IS_GREATER_THAN_VALUE:
         {
            result = evaluateCompareTo(lhs, rhs) > 0;
            break;
         }
         
         case IS_LESS_THAN_VALUE:
         {
            result = evaluateCompareTo(lhs, rhs) < 0;
            break;
         }
         
         case IS_GREATER_THAN_OR_EQUAL_TO_VALUE:
         {
            result = evaluateCompareTo(lhs, rhs) >= 0;            
            break;
         }
         
         case IS_LESS_THAN_OR_EQUAL_TO_VALUE:
         {
            result = evaluateCompareTo(lhs, rhs) <= 0;
            break;
         }
         
         case EQUALS_VALUE:
         {
            if (lhs == null)
            {
               result = (lhs == getSingleOperand(rhs));
            }
            else
            {
               result = lhs.equals(getSingleOperand(rhs));
            }
            break;
         }
         
         case DOES_NOT_EQUAL_VALUE:
         {
            if (lhs == null)
            {
               result = (lhs != getSingleOperand(rhs));
            }
            else
            {
               result = !lhs.equals(getSingleOperand(rhs));  
            }
            break;
         }
         
         case CONTAINS_VALUE:
         {
            result = evaluateContains(lhs, rhs);
            break;
         }
         
         case IS_NOT_WITHIN_VALUE:
         {
            result = !evaluateWithin(lhs, rhs);
            break;
         }
         
         case DOES_NOT_CONTAIN_VALUE:
         {
            result = !evaluateContains(lhs, rhs);
            break;
         }
         
         case CONTAINS_EXACTLY_VALUE:
         {
            result = evaluateContainsExactly(lhs, rhs);
            break;
         }         
      }
      
      return (result);
   }
   
   /**
    * This method is used to ensure that if a list of operand values has been
    * suppied, that a single operand is extracted.
    * 
    * @param operand operand value
    * @return single operand value
    */
   private Object getSingleOperand (Object operand)
   {
      if (operand instanceof Object[])
      {
         Object[] list = (Object[])operand;
         operand = list[0];
      }
      
      return (operand);
   }
   
   /**
    * Determine if the supplied value falls within the specified range.
    * 
    * @param lhs single value operand
    * @param rhs range operand
    * @return boolean result
    */
   private boolean evaluateWithin (Object lhs, Object rhs)
   {
      boolean result = false;
      
      if (lhs != null && rhs instanceof Object[])
      {
         Comparable lhsComparable = (Comparable)lhs;
         Object[] rhsList = (Object[])rhs;
         result = (lhsComparable.compareTo(rhsList[0]) >=0 && lhsComparable.compareTo(rhsList[1]) <= 0);
      }
               
      return (result);
   }
   
   /**
    * Implements a simple compare-to operation. Assumes that the LHS
    * operand implements the Comparable interface.
    * 
    * @param lhs operand
    * @param rhs operand
    * @return boolean result
    */
   private int evaluateCompareTo (Object lhs, Object rhs)
   {
      int result;
      
      rhs = getSingleOperand(rhs);
      
      if (lhs == null)
      {
         result = 1;
      }
      else
      {
         result = ((Comparable)lhs).compareTo(rhs);
      }
      
      return (result);
   }
   
   /**
    * Assuming the supplied arguments are both Strings, this method
    * determines if rhs is contained within lhs. This test is case insenstive.
    * 
    * @param lhs operand
    * @param rhs operand
    * @return boolean result
    */
   private boolean evaluateContains (Object lhs, Object rhs)
   {
      boolean result = false;
      
      rhs = getSingleOperand(rhs);
      
      if (lhs instanceof String && rhs instanceof String)
      {
         result = ((String)lhs).toUpperCase().indexOf(((String)rhs).toUpperCase()) != -1;
      }
      
      return (result);
   }

   /**
    * Assuming the supplied arguments are both Strings, this method
    * determines if rhs is contained within lhs. This test is case senstive.
    * 
    * @param lhs operand
    * @param rhs operand
    * @return boolean result
    */   
   private boolean evaluateContainsExactly (Object lhs, Object rhs)
   {
      boolean result = false;
      
      rhs = getSingleOperand(rhs);
      
      if (lhs instanceof String && rhs instanceof String)
      {
         result = ((String)lhs).indexOf(((String)rhs)) != -1;
      }
      
      return (result);
   }
   
   /**
    * {@inheritDoc}
    */
   public String toString ()
   {
      return (NAME_VALUES[m_type]);
   }
   
   public static final int IS_ANY_VALUE_VALUE = 0;      
   public static final int IS_WITHIN_VALUE = 1;
   public static final int IS_GREATER_THAN_VALUE = 2;
   public static final int IS_LESS_THAN_VALUE = 3;
   public static final int IS_GREATER_THAN_OR_EQUAL_TO_VALUE = 4;
   public static final int IS_LESS_THAN_OR_EQUAL_TO_VALUE = 5;   
   public static final int EQUALS_VALUE = 6;
   public static final int DOES_NOT_EQUAL_VALUE = 7;
   public static final int CONTAINS_VALUE = 8;
   public static final int IS_NOT_WITHIN_VALUE = 9;
   public static final int DOES_NOT_CONTAIN_VALUE = 10;
   public static final int CONTAINS_EXACTLY_VALUE = 11;
   public static final int MAX_TYPE_VALUES = 12;

   public static final TestOperator EQUALS = new TestOperator (EQUALS_VALUE);
   public static final TestOperator DOES_NOT_EQUAL = new TestOperator (DOES_NOT_EQUAL_VALUE);
   public static final TestOperator IS_GREATER_THAN = new TestOperator (IS_GREATER_THAN_VALUE);
   public static final TestOperator IS_GREATER_THAN_OR_EQUAL_TO = new TestOperator (IS_GREATER_THAN_OR_EQUAL_TO_VALUE);
   public static final TestOperator IS_LESS_THAN = new TestOperator (IS_LESS_THAN_VALUE);
   public static final TestOperator IS_LESS_THAN_OR_EQUAL_TO = new TestOperator (IS_LESS_THAN_OR_EQUAL_TO_VALUE);
   public static final TestOperator IS_WITHIN = new TestOperator (IS_WITHIN_VALUE);
   public static final TestOperator IS_NOT_WITHIN = new TestOperator (IS_NOT_WITHIN_VALUE);
   public static final TestOperator CONTAINS = new TestOperator (CONTAINS_VALUE);
   public static final TestOperator DOES_NOT_CONTAIN = new TestOperator (DOES_NOT_CONTAIN_VALUE);
   public static final TestOperator CONTAINS_EXACTLY = new TestOperator (CONTAINS_EXACTLY_VALUE);
   public static final TestOperator IS_ANY_VALUE = new TestOperator (IS_ANY_VALUE_VALUE);
   
   /**
    * Array of type values matching the above constants.
    */
   private static final TestOperator[] TYPE_VALUES =
   {
      IS_ANY_VALUE,
      IS_WITHIN,      
      IS_GREATER_THAN,
      IS_LESS_THAN,
      IS_GREATER_THAN_OR_EQUAL_TO,
      IS_LESS_THAN_OR_EQUAL_TO,      
      EQUALS,
      DOES_NOT_EQUAL,
      CONTAINS,      
      IS_NOT_WITHIN,
      DOES_NOT_CONTAIN,
      CONTAINS_EXACTLY
   };

   private static final String[] NAME_VALUES =
   {
      "IS_ANY_VALUE",
      "IS_WITHIN",
      "IS_GREATER_THAN",
      "IS_LESS_THAN",
      "IS_GREATER_THAN_OR_EQUAL_TO",
      "IS_LESS_THAN_OR_EQUAL_TO",
      "EQUALS",
      "DOES_NOT_EQUAL",
      "CONTAINS",
      "IS_NOT_WITHIN",
      "DOES_NOT_CONTAIN",
      "CONTAINS_EXACTLY"
   };
   
   /**
    * Internal representation.
    */
   private int m_type;
}
