/*
 * file:       GanttBarStartEndShape.java
 * author:     Tom Ollar
 * copyright:  (c) Packwood Software 2009
 * date:       26/03/2009
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
 * Represents the shape at the start end end of a Gantt bar.
 */
public enum GanttBarStartEndShape implements MpxjEnum
{
   NONE(0, "None"),
   NORTHHOMEPLATE(1, "North Home Plate"),
   SOUTHHOMEPLATE(2, "South Home Plate"),
   DIAMOND(3, "Diamond"),
   UPARROW(4, "Up Arrow"),
   DOWNARROW(5, "Down Arrow"),
   RIGHTARROW(6, "Right Arrow"),
   LEFTARROW(7, "Left Arrow"),
   UPPOINTER(8, "Up Pointer"),
   SOUTHMINIHOMEPLATE(9, "South Mini Home Plate"),
   NORTHMINIHOMEPLATE(10, "North Mini Home Plate"),
   VERTICALBAR(11, "Vertical Bar"),
   SQUARE(12, "Square"),
   DIAMONDCIRCLED(13, "Diamond Circled"),
   DOWNPOINTER(14, "Down Pointer"),
   UPARROWCIRCLED(15, "Up Arrow Circled"),
   DOWNARROWCIRCLED(16, "Down Arrow Circled"),
   UPPOINTERCIRCLED(17, "Up Pointer Circled"),
   DOWNPOINTERCIRCLED(18, "Down Pointer Circled"),
   CIRCLE(19, "Circle"),
   STAR(20, "Star"),
   LEFTBRACKET(21, "Left Bracket"),
   RIGHTBRACKET(22, "Right Bracket"),
   LEFTGRADIENT(23, "Left Gradient"),
   RIGHTGRADIENT(24, "Right Gradient");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name name of the enum
    */
   GanttBarStartEndShape(int type, String name)
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
   public static GanttBarStartEndShape getInstance(int type)
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
   public static GanttBarStartEndShape getInstance(Number type)
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
   private static final GanttBarStartEndShape[] TYPE_VALUES = EnumHelper.createTypeArray(GanttBarStartEndShape.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
