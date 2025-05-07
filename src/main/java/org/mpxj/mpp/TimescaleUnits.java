/*
 * file:       TimescaleUnits.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Apr 7, 2005
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

package org.mpxj.mpp;

import org.mpxj.MpxjEnum;
import org.mpxj.common.EnumHelper;
import org.mpxj.common.NumberHelper;

/**
 * Class representing the units which may be shown on a Gantt chart timescale.
 */
public enum TimescaleUnits implements MpxjEnum
{
   NONE(-1, "None"),
   MINUTES(0, "Minutes"),
   HOURS(1, "Hours"),
   DAYS(2, "Days"),
   WEEKS(3, "Weeks"),
   THIRDS_OF_MONTHS(4, "Thirds of Months"),
   MONTHS(5, "Months"),
   QUARTERS(6, "Quarters"),
   HALF_YEARS(7, "Half Years"),
   YEARS(8, "Years");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name enum name
    */
   TimescaleUnits(int type, String name)
   {
      m_value = type;
      m_name = name;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TimescaleUnits getInstance(int type)
   {
      TimescaleUnits result;
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         result = NONE;
      }
      else
      {
         result = TYPE_VALUES[type];
      }
      return (result);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static TimescaleUnits getInstance(Number type)
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
    * Retrieve the name of this time unit. Note that this is not
    * localised.
    *
    * @return name of this timescale unit
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      return (getName());
   }

   /**
    * Array mapping int types to enums.
    */
   private static final TimescaleUnits[] TYPE_VALUES = EnumHelper.createTypeArray(TimescaleUnits.class, -1);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
