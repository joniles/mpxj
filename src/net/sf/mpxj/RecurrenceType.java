/*
 * file:       RecurrenceType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2008
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

package net.sf.mpxj;

import java.util.EnumSet;

import net.sf.mpxj.utility.MpxjEnum;

/**
 * Represents the recurrence type.
 */
public enum RecurrenceType implements MpxjEnum
{
   DAILY (1),
   WEEKLY (2),
   MONTHLY (3),
   YEARLY (4);   
   
   /**
    * Private constructor.
    * 
    * @param type int version of the enum
    */
   private RecurrenceType (int type)
   {
      m_value = type;
   }


   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static RecurrenceType getInstance (int type)
   {      
      if (type < 0 || type >= TYPE_VALUES.length)
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
   public int getValue ()
   {
      return (m_value);
   }


   /**
    * Array mapping int types to enums.
    */
   private static final RecurrenceType[] TYPE_VALUES = new RecurrenceType[4];
   static
   {      
      for (RecurrenceType e : EnumSet.range(RecurrenceType.DAILY, RecurrenceType.YEARLY))
      {
         TYPE_VALUES[e.getValue()] = e;
      }
   }


   /**
    * Internal representation of the enum int type.
    */
   private int m_value;
}
