/*
 * file:       DayType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       09/09/2010
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

import net.sf.mpxj.common.EnumHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class is used to represent the day type used by the project calendar.
 */
public enum DayType implements MpxjEnum
{
   NON_WORKING(0),
   WORKING(1),
   DEFAULT(2);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   DayType(int type)
   {
      m_value = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static DayType getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = NON_WORKING.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static DayType getInstance(Number type)
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
    * Array mapping int types to enums.
    */
   private static final DayType[] TYPE_VALUES = EnumHelper.createTypeArray(DayType.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
}
