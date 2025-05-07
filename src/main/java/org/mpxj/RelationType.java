/*
 * file:       RelationType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       10/05/2005
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
 * This class is used to represent a relation type. It provides a mapping
 * between the textual description of a relation type found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programmatically.
 */
public enum RelationType implements MpxjEnum
{
   FINISH_FINISH(0, "FF"),
   FINISH_START(1, "FS"),
   START_FINISH(2, "SF"),
   START_START(3, "SS");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name enum name
    */
   RelationType(int type, String name)
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
   public static RelationType getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = FINISH_START.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static RelationType getInstance(Number type)
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

   @Override public String toString()
   {
      return (m_name);
   }

   /**
    * Array mapping int types to enums.
    */
   private static final RelationType[] TYPE_VALUES = EnumHelper.createTypeArray(RelationType.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
