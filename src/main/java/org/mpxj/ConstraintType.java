/*
 * file:       ConstraintType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       01/02/2003
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
 * This class is used to represent a constraint type. It provides a mapping
 * between the textual description of a constraint type found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programmatically.
 */
public enum ConstraintType implements MpxjEnum
{
   AS_SOON_AS_POSSIBLE(0),
   AS_LATE_AS_POSSIBLE(1),
   MUST_START_ON(2),
   MUST_FINISH_ON(3),
   START_NO_EARLIER_THAN(4),
   START_NO_LATER_THAN(5),
   FINISH_NO_EARLIER_THAN(6),
   FINISH_NO_LATER_THAN(7),
   START_ON(8),
   FINISH_ON(9);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   ConstraintType(int type)
   {
      m_value = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ConstraintType getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = AS_SOON_AS_POSSIBLE.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ConstraintType getInstance(Number type)
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
   private static final ConstraintType[] TYPE_VALUES = EnumHelper.createTypeArray(ConstraintType.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
}
