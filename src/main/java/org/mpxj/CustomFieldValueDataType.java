/*
 * file:       CustomFieldValueDataType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       22/09/2019
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

/**
 * Enumeration used  by custom field value items to represent their data type.
 */
public enum CustomFieldValueDataType implements MpxjEnum
{
   DATE(4, 4, DataType.DATE),
   DURATION(6, 6, DataType.DURATION),
   COST(9, 5, DataType.CURRENCY),
   NUMBER(15, 7, DataType.NUMERIC),
   FLAG(17, 17, DataType.BOOLEAN),
   TEXT(21, 3, DataType.STRING),
   FINISH_DATE(27, 9, DataType.DATE);

   /**
    * Private constructor.
    *
    * @param value int version of the enum
    * @param maskValue data type used in mask definition
    * @param type data type
    */
   CustomFieldValueDataType(int value, int maskValue, DataType type)
   {
      m_value = value;
      m_maskValue = maskValue;
      m_type = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static CustomFieldValueDataType getInstance(int type)
   {
      if (type >= 0 && type < TYPE_VALUES.length)
      {
         return TYPE_VALUES[type];
      }
      return null;
   }

   /**
    * Retrieve an instance of the enum based on its mask value.
    *
    * @param type mask value
    * @return enum instance
    */
   public static CustomFieldValueDataType getInstanceByMaskValue(int type)
   {
      if (type >= 0 && type < MASK_VALUES.length)
      {
         return MASK_VALUES[type];
      }
      return null;
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum.
    *
    * @return int representation of the enum
    */
   @Override public int getValue()
   {
      return m_value;
   }

   /**
    * Retrieve the MPXJ data type.
    *
    * @return MPXJ data type
    */
   public DataType getDataType()
   {
      return m_type;
   }

   /**
    * Retrieve the mask value.
    *
    * @return mask value
    */
   public int getMaskValue()
   {
      return m_maskValue;
   }

   /**
    * Array mapping int types to enums.
    */
   private static final CustomFieldValueDataType[] TYPE_VALUES = EnumHelper.createTypeArray(CustomFieldValueDataType.class, 21);

   private static final CustomFieldValueDataType[] MASK_VALUES = new CustomFieldValueDataType[28];
   static
   {
      for (CustomFieldValueDataType value : values())
      {
         MASK_VALUES[value.getMaskValue()] = value;
      }
   }

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;

   private final int m_maskValue;

   private final DataType m_type;
}
