/*
 * file:       MaxUnits.java
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
 * Represents a max units value when writing an XER file.
 */
final class MaxUnits
{
   /**
    * Constructor.
    *
    * @param number value to be wrapped
    */
   public MaxUnits(Number number)
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

   public static final MaxUnits ZERO = new MaxUnits(Integer.valueOf(0));
}
