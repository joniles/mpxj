/*
 * file:       AssignmentField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       14/04/2011
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

import java.util.EnumSet;
import java.util.Locale;

/**
 * Instances of this type represent Assignment fields.
 */
public enum AssignmentField implements FieldType
{
   START(DataType.DATE), // Must always be first value

   ACTUAL_COST(DataType.CURRENCY),
   ACTUAL_WORK(DataType.WORK),
   COST(DataType.CURRENCY),
   ASSIGNMENT_DELAY(DataType.DELAY),
   VARIABLE_RATE_UNITS(DataType.WORK_UNITS),
   ASSIGNMENT_UNITS(DataType.UNITS),
   WORK(DataType.WORK),
   BASELINE_START(DataType.DATE),
   ACTUAL_START(DataType.DATE),
   BASELINE_FINISH(DataType.DATE),
   ACTUAL_FINISH(DataType.DATE),
   BASELINE_WORK(DataType.WORK),
   OVERTIME_WORK(DataType.WORK),
   BASELINE_COST(DataType.CURRENCY),
   WORK_CONTOUR(DataType.STRING),
   REMAINING_WORK(DataType.WORK),
   LEVELING_DELAY_UNITS(DataType.TIME_UNITS),
   LEVELING_DELAY(DataType.DURATION, LEVELING_DELAY_UNITS),
   UNIQUE_ID(DataType.INTEGER),
   TASK_UNIQUE_ID(DataType.INTEGER),
   RESOURCE_UNIQUE_ID(DataType.INTEGER),
   PLANNED_WORK_DATA(DataType.BINARY),
   COMPLETE_WORK_DATA(DataType.BINARY),

   FINISH(DataType.DATE); // Must always be last value

   /**
    * Constructor.
    * 
    * @param dataType field data type
    * @param unitsType units type
    */
   private AssignmentField(DataType dataType, FieldType unitsType)
   {
      m_dataType = dataType;
      m_unitsType = unitsType;
   }

   /**
    * Constructor.
    * 
    * @param dataType field data type
    */
   private AssignmentField(DataType dataType)
   {
      this(dataType, null);
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return (getName(Locale.ENGLISH));
   }

   /**
    * {@inheritDoc}
    */
   public String getName(Locale locale)
   {
      String[] titles = LocaleData.getStringArray(locale, LocaleData.ASSIGNMENT_COLUMNS);
      String result = null;

      if (m_value >= 0 && m_value < titles.length)
      {
         result = titles[m_value];
      }

      return (result);
   }

   /**
    * {@inheritDoc}
    */
   public int getValue()
   {
      return (m_value);
   }

   /**
    * {@inheritDoc}
    */
   public DataType getDataType()
   {
      return (m_dataType);
   }

   /**
    * {@inheritDoc}
    */
   public FieldType getUnitsType()
   {
      return m_unitsType;
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
    * This method takes the integer enumeration of a resource field
    * and returns an appropriate class instance.
    *
    * @param type integer resource field enumeration
    * @return ResourceField instance
    */
   public static AssignmentField getInstance(int type)
   {
      AssignmentField result = null;

      if (type >= 0 && type < MAX_VALUE)
      {
         result = TYPE_VALUES[type];
      }

      return (result);
   }

   public static final int MAX_VALUE = EnumSet.allOf(AssignmentField.class).size();
   private static final AssignmentField[] TYPE_VALUES = new AssignmentField[MAX_VALUE];
   static
   {
      int value = 0;
      for (AssignmentField e : EnumSet.allOf(AssignmentField.class))
      {
         e.m_value = value++;
         TYPE_VALUES[e.getValue()] = e;
      }
   }

   private int m_value;
   private DataType m_dataType;
   private FieldType m_unitsType;
}
