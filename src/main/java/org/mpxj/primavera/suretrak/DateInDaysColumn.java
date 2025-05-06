/*
 * file:       DateInDaysColumn.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package org.mpxj.primavera.suretrak;

import java.time.LocalDateTime;

import org.mpxj.primavera.common.AbstractShortColumn;

/**
 * Extract column data from a table.
 */
class DateInDaysColumn extends AbstractShortColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public DateInDaysColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public LocalDateTime read(int offset, byte[] data)
   {
      int days = readShort(offset, data);
      if (days > RECURRING_OFFSET)
      {
         days -= RECURRING_OFFSET;
      }

      return EPOCH.plusDays(days);
   }

   static final int RECURRING_OFFSET = 25463;

   // 31/12/1979
   private static final LocalDateTime EPOCH = LocalDateTime.of(1979, 12, 31, 0, 0);
}
