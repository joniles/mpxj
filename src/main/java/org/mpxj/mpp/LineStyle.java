/*
 * file:       GridLineStyle.java
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
 * This class represents the grid line styles used by Microsoft Project.
 */
public enum LineStyle implements MpxjEnum
{
   NONE(0, "None"),
   SOLID(1, "Solid"),
   DOTTED1(2, "Dotted1"),
   DOTTED2(3, "Dotted2"),
   DASHED(4, "Dashed");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name name of the enum
    */
   LineStyle(int type, String name)
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
   public static LineStyle getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = NONE.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static LineStyle getInstance(Number type)
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
    * Retrieve the line style name. Currently this is not localised.
    *
    * @return style name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Retrieve the String representation of this line style.
    *
    * @return String representation of this line style
    */
   @Override public String toString()
   {
      return (getName());
   }

   /**
    * Array mapping int types to enums.
    */
   private static final LineStyle[] TYPE_VALUES = EnumHelper.createTypeArray(LineStyle.class, 1);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
