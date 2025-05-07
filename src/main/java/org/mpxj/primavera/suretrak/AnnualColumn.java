/*
 * file:       AnnualColumn.java
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

import org.mpxj.primavera.common.AbstractShortColumn;

/**
 * Extract column data from a table.
 */
class AnnualColumn extends AbstractShortColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public AnnualColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Boolean read(int offset, byte[] data)
   {
      int days = readShort(offset, data);
      return Boolean.valueOf(days > DateInDaysColumn.RECURRING_OFFSET);
   }
}
