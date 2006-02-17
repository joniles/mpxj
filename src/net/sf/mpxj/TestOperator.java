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
