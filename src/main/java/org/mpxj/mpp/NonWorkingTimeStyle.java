/*
 * file:       NonWorkingTimeStyle.java
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
 * Class representing how non-working time is shown on a Gantt chart.
 */
public enum NonWorkingTimeStyle implements MpxjEnum
{
   BEHIND(0, "Behind"),
   IN_FRONT(1, "In Front"),
   DO_NOT_DRAW(2, "Do Not Draw");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name enum name
    */
   NonWorkingTimeStyle(int type, String name)
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
   public static NonWorkingTimeStyle getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = IN_FRONT.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static NonWorkingTimeStyle getInstance(Number type)
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
    * Retrieve the name of this alignment. Note that this is not
    * localised.
    *
    * @return name of this alignment type
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
   private static final NonWorkingTimeStyle[] TYPE_VALUES = EnumHelper.createTypeArray(NonWorkingTimeStyle.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
