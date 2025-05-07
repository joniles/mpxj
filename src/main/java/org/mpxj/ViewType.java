/*
 * file:       ViewType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2006
 * date:       27/01/2006
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
 * This class represents the enumeration of the valid types of view.
 */
public enum ViewType implements MpxjEnum
{
   UNKNOWN(0, "UNKNOWN"),
   GANTT_CHART(1, "GANTT_CHART"),
   NETWORK_DIAGRAM(2, "NETWORK_DIAGRAM"),
   RELATIONSHIP_DIAGRAM(3, "RELATIONSHIP_DIAGRAM"),
   TASK_FORM(4, "TASK_FORM"),
   TASK_SHEET(5, "TASK_SHEET"),
   RESOURCE_FORM(6, "RESOURCE_FORM"),
   RESOURCE_SHEET(7, "RESOURCE_SHEET"),
   RESOURCE_GRAPH(8, "RESOURCE_GRAPH"),
   TASK_DETAILS_FORM(10, "TASK_DETAILS_FORM"),
   TASK_NAME_FORM(11, "TASK_NAME_FORM"),
   RESOURCE_NAME_FORM(12, "RESOURCE_NAME_FORM"),
   CALENDAR(13, "CALENDAR"),
   TASK_USAGE(14, "TASK_USAGE"),
   RESOURCE_USAGE(15, "RESOURCE_USAGE");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name enum name
    */
   ViewType(int type, String name)
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
   public static ViewType getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = UNKNOWN.getValue();
      }
      ViewType result = TYPE_VALUES[type];
      if (result == null)
      {
         result = UNKNOWN;
      }
      return (result);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ViewType getInstance(Number type)
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
    * Retrieve the name of this enum.
    *
    * @return enum name
    */
   public String getName()
   {
      return (m_name);
   }

   @Override public String toString()
   {
      return (m_name);
   }

   /**
    * Array mapping int types to enums.
    */
   private static final ViewType[] TYPE_VALUES = EnumHelper.createTypeArray(ViewType.class, 1);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
