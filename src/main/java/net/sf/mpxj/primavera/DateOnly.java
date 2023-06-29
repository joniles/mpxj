/*
 * file:       DateOnly.java
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

package net.sf.mpxj.primavera;

import java.time.LocalDateTime;

/**
 * Represents a date only value when writing an XER file.
 */
final class DateOnly
{
   /**
    * Constructor.
    *
    * @param date value to be wrapped
    */
   public DateOnly(LocalDateTime date)
   {
      m_date = date;
   }

   /**
    * Retrieve the wrapped value.
    *
    * @return wrapped value
    */
   public LocalDateTime toDate()
   {
      return m_date;
   }

   private final LocalDateTime m_date;
}
