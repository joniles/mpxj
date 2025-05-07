/*
 * file:       Currency.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

package org.mpxj.primavera;

/**
 * Represents a currency value when writing an XER file.
 */
final class CurrencyValue
{
   /**
    * Constructor.
    *
    * @param number value to be wrapped
    */
   public CurrencyValue(Number number)
   {
      m_number = number;
   }

   /**
    * Retrieve the wrapped value.
    *
    * @return wrapped value
    */
   public Number toNumber()
   {
      return m_number;
   }

   private final Number m_number;

   /**
    * Return a new Currency instance, or null if the caller passes null.
    *
    * @param number value to be wrapped
    * @return Currency instance or null
    */
   public static final CurrencyValue getInstance(Number number)
   {
      return number == null ? null : new CurrencyValue(number);
   }

   public static final CurrencyValue ZERO = new CurrencyValue(Double.valueOf(0));
}
