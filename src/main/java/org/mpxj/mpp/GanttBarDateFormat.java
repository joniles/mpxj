/*
 * file:       GanttBarDateFormat.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       20/05/2010
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
 * Enumeration representing the formats which may be shown on a Gantt chart timescale.
 */
public enum GanttBarDateFormat implements MpxjEnum
{
   DEFAULT(0, "Default"),
   DDMMYY_MMSS(1, "28/01/02 12:33"),
   DDMMYY(2, "28/01/02"),
   DDMMYYYY(21, "28/01/2002"),
   DD_MMMM_YYYY_HHMM(3, "28 January 2002 12:33"),
   DD_MMMM_YYYY(4, "28 January 2002"),
   DD_MMM_HHMM(5, "28 Jan 12:33"),
   DD_MMM_YY(6, "28 Jan '02"),
   DD_MMMM(7, "28 January"),
   DD_MMM(8, "28 Jan"),
   DDD_DDMMYY_HHMM(9, "Mon 28/01/02 12:33"),
   DDD_DDMMYY(10, "Mon 28/01/02"),
   DDD_DD_MMM_YY(11, "Mon 28 Jan '02"),
   DDD_HHMM(12, "Mon 12:33"),
   DDD_DD_MMM(16, "Mon 28 Jan"),
   DDD_DDMM(17, "Mon 28/01"),
   DDD_DD(18, "Mon 28"),
   DDMM(13, "28/01"),
   DD(14, "28"),
   HHMM(15, "12:33"),
   MWW(19, "1/W05"),
   MWWYY_HHMM(20, "1/W05/02 12:33");

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    * @param name enum name
    */
   GanttBarDateFormat(int type, String name)
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
   public static GanttBarDateFormat getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = DEFAULT.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static GanttBarDateFormat getInstance(Number type)
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
   private static final GanttBarDateFormat[] TYPE_VALUES = EnumHelper.createTypeArray(GanttBarDateFormat.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
   private final String m_name;
}
