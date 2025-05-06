/*
 * file:       ProjectDateFormat.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       04/01/2005
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
 * Instances of this class represent enumerated date format values.
 */
public enum ProjectDateFormat implements MpxjEnum
{
   /**
    * This format represents dates in the form 25/12/98 12:56.
    */
   DD_MM_YY_HH_MM(0),

   /**
    * This format represents dates in the form 25/05/98.
    */
   DD_MM_YY(1),

   /**
    * This format represents dates in the form 13 December 2002 12:56.
    */
   DD_MMMMM_YYYY_HH_MM(2),

   /**
    * This format represents dates in the form 13 December 2002.
    */
   DD_MMMMM_YYYY(3),

   /**
    * This format represents dates in the form 24 Nov 12:56.
    */
   DD_MMM_HH_MM(4),

   /**
    * This format represents dates in the form 25 Aug '98.
    */
   DD_MMM_YY(5),

   /**
    * This format represents dates in the form 25 September.
    */
   DD_MMMMM(6),

   /**
    * This format represents dates in the form 25 Aug.
    */
   DD_MMM(7),

   /**
    * This format represents dates in the form Thu 25/05/98 12:56.
    */
   EEE_DD_MM_YY_HH_MM(8),

   /**
    * This format represents dates in the form Wed 25/05/98.
    */
   EEE_DD_MM_YY(9),

   /**
    * This format represents dates in the form Wed 25 Mar '98.
    */
   EEE_DD_MMM_YY(10),

   /**
    * This format represents dates in the form Wed 12:56.
    */
   EEE_HH_MM(11),

   /**
    * This format represents dates in the form 25/5.
    */
   DD_MM(12),

   /**
    * This format represents dates in the form 23.
    */
   DD(13),

   /**
    * This format represents dates in the form 12:56.
    */
   HH_MM(14),

   /**
    * This format represents dates in the form Wed 23 Mar.
    */
   EEE_DD_MMM(15),

   /**
    * This format represents dates in the form Wed 25/5.
    */
   EEE_DD_MM(16),

   /**
    * This format represents dates in the form Wed 05.
    */
   EEE_DD(17),

   /**
    * This format represents dates in the form 5/W25.
    */
   DD_WWW(18),

   /**
    * This format represents dates in the form 5/W25/98 12:56.
    */
   DD_WWW_YY_HH_MM(19),

   /**
    * This format represents dates in the form 25/05/1998.
    */
   DD_MM_YYYY(20);

   /**
    * Private constructor.
    *
    * @param type int version of the enum
    */
   ProjectDateFormat(int type)
   {
      m_value = type;
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ProjectDateFormat getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = DD_MM_YYYY.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ProjectDateFormat getInstance(Number type)
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
    * Returns a string representation of the date format type
    * to be used as part of an MPX file.
    *
    * @return string representation
    */
   @Override public String toString()
   {
      return (Integer.toString(m_value));
   }

   /**
    * Array mapping int types to enums.
    */
   private static final ProjectDateFormat[] TYPE_VALUES = EnumHelper.createTypeArray(ProjectDateFormat.class);

   /**
    * Internal representation of the enum int type.
    */
   private final int m_value;
}
