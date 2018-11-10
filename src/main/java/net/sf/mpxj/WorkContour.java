/*
 * file:       WorkContour.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       12/02/2005
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

import net.sf.mpxj.common.EnumHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * Instances of this class represent enumerated work contour values.
 */
public enum WorkContour implements MpxjEnum
{
   FLAT(0),
   BACK_LOADED(1),
   FRONT_LOADED(2),
   DOUBLE_PEAK(3),
   EARLY_PEAK(4),
   LATE_PEAK(5),
   BELL(6),
   TURTLE(7),
   CONTOURED(8);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   private WorkContour(int type)
   {
      m_value = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static WorkContour getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = FLAT.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static WorkContour getInstance(Number type)
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
   private static final WorkContour[] TYPE_VALUES = EnumHelper.createTypeArray(WorkContour.class);

   /**
    * Internal representation of the enum int type.
    */
   private int m_value;
}
