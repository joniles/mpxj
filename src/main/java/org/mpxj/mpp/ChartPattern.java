/*
 * file:       ChartPattern.java
 * author:     Tom Ollar
 * copyright:  (c) Packwood Software 2009
 * date:       04/04/2009
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
 * Represents the pattern used to fill the middle section of a Gantt bar.
 */
public enum ChartPattern implements MpxjEnum
{
   TRANSPARENT(0, "Transparent"),
   SOLID(1, "Solid"),
   LIGHTDOTTED(2, "Light Dotted"),
   DOTTED(3, "Dotted"),
   HEAVYDOTTED(4, "Heavy Dotted"),
   BACKSLASH(5, "Back Slash"),
   FORWARDSLASH(6, "Forward Slash"),
   CHECKERED(7, "Checkered"),
   VERTICALSTRIPE(8, "Vertical Stripe"),
   HORIZONTALSTRIPE(9, "Horizontal Stripe"),
   GRID(10, "Grid"),
   SOLIDHAIRY(11, "Solid Hairy");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name name of the enum
    */
   ChartPattern(int type, String name)
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
   public static ChartPattern getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = TRANSPARENT.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ChartPattern getInstance(Number type)
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
   private static final ChartPattern[] TYPE_VALUES = EnumHelper.createTypeArray(ChartPattern.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
