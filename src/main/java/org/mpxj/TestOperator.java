/*
 * file:       TestOperator.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2006
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

package org.mpxj;

import org.mpxj.common.EnumHelper;
import org.mpxj.common.NumberHelper;

/**
 * This class represents the set of operators used to perform a test
 * between two or more operands.
 */
public enum TestOperator implements MpxjEnum
{
   IS_ANY_VALUE(0)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (true);
      }
   },

   IS_WITHIN(1)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (evaluateWithin(lhs, rhs));
      }
   },

   IS_GREATER_THAN(2)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (evaluateCompareTo(lhs, rhs) > 0);
      }
   },

   IS_LESS_THAN(3)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (evaluateCompareTo(lhs, rhs) < 0);
      }
   },

   IS_GREATER_THAN_OR_EQUAL_TO(4)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (evaluateCompareTo(lhs, rhs) >= 0);
      }
   },

   IS_LESS_THAN_OR_EQUAL_TO(5)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (evaluateCompareTo(lhs, rhs) <= 0);
      }
   },

   EQUALS(6)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         boolean result;

         if (lhs == null)
         {
            result = (getSingleOperand(rhs) == null);
         }
         else
         {
            result = lhs.equals(getSingleOperand(rhs));
         }
         return (result);
      }
   },

   DOES_NOT_EQUAL(7)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         boolean result;
         if (lhs == null)
         {
            result = (getSingleOperand(rhs) != null);
         }
         else
         {
            result = !lhs.equals(getSingleOperand(rhs));
         }
         return (result);
      }
   },

   CONTAINS(8)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (evaluateContains(lhs, rhs));
      }
   },

   IS_NOT_WITHIN(9)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (!evaluateWithin(lhs, rhs));
      }
   },

   DOES_NOT_CONTAIN(10)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (!evaluateContains(lhs, rhs));
      }
   },

   CONTAINS_EXACTLY(11)
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         return (evaluateContainsExactly(lhs, rhs));
      }
   },

   AND(12) // Extension used by MPXJ, Not MS Project
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         throw new UnsupportedOperationException();
      }
   },

   OR(13) // Extension used by MPXJ, Not MS Project
   {
      @Override public boolean evaluate(Object lhs, Object rhs)
      {
         throw new UnsupportedOperationException();
      }
   };

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   TestOperator(int type)
   {
      m_value = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TestOperator getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = IS_ANY_VALUE.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TestOperator getInstance(Number type)
   {
      int value;
      if (type == null)
      {
         value = -1;
      }
      else
      {
         value = NumberHelper.getInt(type);
      }
      return (getInstance(value));
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum.
    *
    * @return int representation of the enum
    */
   @Override public int getValue()
   {
      return (m_value);
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
   public abstract boolean evaluate(Object lhs, Object rhs);

   /**
    * This method is used to ensure that if a list of operand values has been
    * supplied, that a single operand is extracted.
    *
    * @param operand operand value
    * @return single operand value
    */
   protected Object getSingleOperand(Object operand)
   {
      if (operand instanceof Object[])
      {
         Object[] list = (Object[]) operand;
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
   @SuppressWarnings(
   {
      "unchecked",
      "rawtypes"
   }) protected boolean evaluateWithin(Object lhs, Object rhs)
   {
      boolean result = false;

      if (rhs instanceof Object[])
      {
         Object[] rhsList = (Object[]) rhs;
         if (lhs != null)
         {
            Comparable lhsComparable = (Comparable) lhs;
            if (rhsList[0] != null && rhsList[1] != null)
            {
               // Project also tries with the values flipped
               result = (lhsComparable.compareTo(rhsList[0]) >= 0 && lhsComparable.compareTo(rhsList[1]) <= 0) || (lhsComparable.compareTo(rhsList[0]) <= 0 && lhsComparable.compareTo(rhsList[1]) >= 0);
            }
         }
         else
         {
            // Project also respects null equality (e.g. NA dates)
            result = rhsList[0] == null || rhsList[1] == null;
         }
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
   @SuppressWarnings(
   {
      "unchecked",
      "rawtypes"
   }) protected int evaluateCompareTo(Object lhs, Object rhs)
   {
      int result;

      rhs = getSingleOperand(rhs);

      if (lhs == null || rhs == null)
      {
         if (lhs == rhs)
         {
            result = 0;
         }
         else
         {
            if (lhs == null)
            {
               result = 1;
            }
            else
            {
               result = -1;
            }
         }
      }
      else
      {
         result = ((Comparable) lhs).compareTo(rhs);
      }

      return (result);
   }

   /**
    * Assuming the supplied arguments are both Strings, this method
    * determines if rhs is contained within lhs. This test is case insensitive.
    *
    * @param lhs operand
    * @param rhs operand
    * @return boolean result
    */
   protected boolean evaluateContains(Object lhs, Object rhs)
   {
      boolean result = false;

      rhs = getSingleOperand(rhs);

      if (lhs instanceof String && rhs instanceof String)
      {
         result = ((String) lhs).toUpperCase().contains(((String) rhs).toUpperCase());
      }

      return (result);
   }

   /**
    * Assuming the supplied arguments are both Strings, this method
    * determines if rhs is contained within lhs. This test is case sensitive.
    *
    * @param lhs operand
    * @param rhs operand
    * @return boolean result
    */
   protected boolean evaluateContainsExactly(Object lhs, Object rhs)
   {
      boolean result = false;

      rhs = getSingleOperand(rhs);

      if (lhs instanceof String && rhs instanceof String)
      {
         result = ((String) lhs).contains(((String) rhs));
      }

      return (result);
   }

   /**
    * Array mapping int types to enums.
    */
   private static final TestOperator[] TYPE_VALUES = EnumHelper.createTypeArray(TestOperator.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
}
