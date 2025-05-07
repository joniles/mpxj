/*
 * file:       RecurrenceType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2008
 * date:       12/06/2008
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

/**
 * Represents the recurrence type.
 */
public enum RecurrenceType implements MpxjEnum
{
   DAILY(1, "Daily"),
   WEEKLY(4, "Weekly"),
   MONTHLY(8, "Monthly"),
   YEARLY(16, "Yearly");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name English name used for debugging
    */
   RecurrenceType(int type, String name)
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
   public static RecurrenceType getInstance(int type)
   {
      if (type < 1 || type >= TYPE_VALUES.length)
      {
         type = DAILY.getValue();
      }
      return (TYPE_VALUES[type]);
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
   private static final RecurrenceType[] TYPE_VALUES = EnumHelper.createTypeArray(RecurrenceType.class, 13);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
