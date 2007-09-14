/*
 * file:       DataType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Jan 18, 2006
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

import net.sf.mpxj.utility.MpxjEnum;

/**
 * This class represents the data type of an attribute.
 */
public final class DataType implements MpxjEnum
{
   /**
    * This constructor takes the numeric enumerated representation of a
    * data type and populates the class instance appropriately.
    *
    * @param type int version of the data type
    */
   private DataType (int type)
   {
      m_value = type;
   }

   /**
    * Accessor method used to retrieve the numeric representation of the
    * data type.
    *
    * @return int representation of the data type
    */
   public int getValue ()
   {
      return (m_value);
   }

   public static final int STRING_VALUE = 1;
   public static final int DATE_VALUE = 2;
   public static final int CURRENCY_VALUE = 3;
   public static final int BOOLEAN_VALUE = 4;
   public static final int NUMERIC_VALUE = 5;
   public static final int DURATION_VALUE = 6;      
   public static final int UNITS_VALUE = 7;
   public static final int PERCENTAGE_VALUE = 8;
   public static final int ACCRUE_VALUE = 9;
   public static final int CONSTRAINT_VALUE = 10;   
   public static final int RATE_VALUE = 11;
   public static final int PRIORITY_VALUE = 12;
   public static final int RELATION_LIST_VALUE = 13;
   public static final int TASK_TYPE_VALUE = 14;
   public static final int RESOURCE_TYPE_VALUE = 15;
   
   public static final DataType STRING = new DataType(STRING_VALUE);   
   public static final DataType DATE = new DataType(DATE_VALUE);
   public static final DataType CURRENCY = new DataType(CURRENCY_VALUE);
   public static final DataType BOOLEAN = new DataType(BOOLEAN_VALUE);
   public static final DataType NUMERIC = new DataType(NUMERIC_VALUE);
   public static final DataType DURATION = new DataType(DURATION_VALUE);   
   public static final DataType UNITS = new DataType(UNITS_VALUE);
   public static final DataType PERCENTAGE = new DataType(PERCENTAGE_VALUE);
   public static final DataType ACCRUE = new DataType(ACCRUE_VALUE);
   public static final DataType CONSTRAINT = new DataType(CONSTRAINT_VALUE);
   public static final DataType RATE = new DataType(RATE_VALUE);
   public static final DataType PRIORITY = new DataType(PRIORITY_VALUE);
   public static final DataType RELATION_LIST = new DataType(RELATION_LIST_VALUE);
   public static final DataType TASK_TYPE = new DataType(TASK_TYPE_VALUE);
   public static final DataType RESOURCE_TYPE = new DataType(RESOURCE_TYPE_VALUE);
   
   /**
    * Internal representation of the data type.
    */
   private int m_value;
}
