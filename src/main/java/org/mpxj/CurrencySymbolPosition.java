/*
 * file:       CurrencySymbolPosition.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       07/01/2005
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
 * Instances of this class represent enumerated currency symbol position values.
 */
public enum CurrencySymbolPosition implements MpxjEnum
{
   AFTER(0),
   BEFORE(1),
   AFTER_WITH_SPACE(2),
   BEFORE_WITH_SPACE(3);

   /**
    * Private constructor.
    *
    * @param value currency symbol position value
    */
   CurrencySymbolPosition(int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the currency symbol position value.
    *
    * @return currency symbol position value
    */
   @Override public int getValue()
   {
      return (m_value);
   }

   /**
    * Retrieve a CurrencySymbolPosition instance representing the supplied value.
    *
    * @param value currency symbol position value
    * @return CurrencySymbolPosition instance
    */
   public static CurrencySymbolPosition getInstance(int value)
   {
      if (value < 0 || value >= TYPE_VALUES.length)
      {
         value = BEFORE.getValue();
      }
      return (TYPE_VALUES[value]);
   }

   /**
    * Returns a string representation of the currency symbol position type
    * to be used as part of an MPX file.
    *
    * @return string representation
    */
   @Override public String toString()
   {
      return (Integer.toString(m_value));
   }

   private static final CurrencySymbolPosition[] TYPE_VALUES = EnumHelper.createTypeArray(CurrencySymbolPosition.class);

   private final int m_value;
}
