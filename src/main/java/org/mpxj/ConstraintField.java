/*
 * file:       ConstraintField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       24/10/2014
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

import java.util.EnumSet;
import java.util.Locale;

/**
 * Instances of this type represent constraint fields.
 */
public enum ConstraintField implements FieldType
{
   UNIQUE_ID(DataType.INTEGER),
   TASK1(DataType.INTEGER),
   TASK2(DataType.INTEGER);

   /**
    * Constructor.
    *
    * @param dataType field data type
    */
   ConstraintField(DataType dataType)
   {
      m_dataType = dataType;
   }

   @Override public FieldTypeClass getFieldTypeClass()
   {
      return FieldTypeClass.CONSTRAINT;
   }

   @Override public String getName()
   {
      return (getName(Locale.ENGLISH));
   }

   @Override public String getName(Locale locale)
   {
      String[] titles = LocaleData.getStringArray(locale, LocaleData.CONSTRAINT_COLUMNS);
      String result = null;

      if (m_value >= 0 && m_value < titles.length)
      {
         result = titles[m_value];
      }

      return (result);
   }

   @Override public int getValue()
   {
      return (m_value);
   }

   @Override public DataType getDataType()
   {
      return (m_dataType);
   }

   @Override public FieldType getUnitsType()
   {
      return null;
   }

   /**
    * Retrieves the string representation of this instance.
    *
    * @return string representation
    */
   @Override public String toString()
   {
      return (getName());
   }

   /**
    * This method takes the integer enumeration of a constraint field
    * and returns an appropriate class instance.
    *
    * @param type integer constraint field enumeration
    * @return ConstraintField instance
    */
   public static ConstraintField getInstance(int type)
   {
      ConstraintField result = null;

      if (type >= 0 && type < MAX_VALUE)
      {
         result = TYPE_VALUES[type];
      }

      return (result);
   }

   public static final int MAX_VALUE = EnumSet.allOf(ConstraintField.class).size();
   private static final ConstraintField[] TYPE_VALUES = new ConstraintField[MAX_VALUE];
   static
   {
      int value = 0;
      for (ConstraintField e : EnumSet.allOf(ConstraintField.class))
      {
         e.m_value = value++;
         TYPE_VALUES[e.getValue()] = e;
      }
   }

   private int m_value;
   private final DataType m_dataType;
}
