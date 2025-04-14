/*
 * file:       DateColumn.java
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

package org.mpxj.primavera.p3;

import java.time.LocalDateTime;

import org.mpxj.primavera.common.AbstractColumn;

/**
 * Extract column data from a table.
 */
class DateColumn extends AbstractColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public DateColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public LocalDateTime read(int offset, byte[] data)
   {
      LocalDateTime result = null;

      int intValue = 0;
      int i = offset + m_offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         intValue |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }

      if (intValue != 0)
      {
         String stringValue = Integer.toString(intValue);
         if (stringValue.length() == 10)
         {
            int year = Integer.parseInt(stringValue.substring(0, 4));
            int month = Integer.parseInt(stringValue.substring(4, 6));
            int day = Integer.parseInt(stringValue.substring(6, 8));

            if (year > 0 && month > 0 && month <= 12 && day > 0 && day <= 31)
            {
               result = LocalDateTime.of(year, month, day, 0, 0);
               if (result.isBefore(DatabaseReader.EPOCH))
               {
                  result = null;
               }
            }
         }
      }

      return result;
   }
}
