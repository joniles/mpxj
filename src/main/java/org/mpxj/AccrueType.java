/*
 * file:       AccrueType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       05/02/2003
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
 * This class is used to represent an accrue type. It provides a mapping
 * between the textual description of an accrue type found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programmatically.
 */
public enum AccrueType implements MpxjEnum
{
   START(1),
   END(2),
   PRORATED(3);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   AccrueType(int type)
   {
      m_value = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static AccrueType getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = PRORATED.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static AccrueType getInstance(Number type)
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
   private static final AccrueType[] TYPE_VALUES = EnumHelper.createTypeArray(AccrueType.class, 1);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
}
