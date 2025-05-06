/*
 * file:       P3WbsFormat.java
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

import org.mpxj.primavera.common.AbstractWbsFormat;
import org.mpxj.primavera.common.MapRow;

/**
 * Reads the WBS format definition from a P3 database, and allows
 * that format to be applied to WBS values.
 */
class P3WbsFormat extends AbstractWbsFormat
{
   /**
    * Constructor. Reads the format definition.
    *
    * @param row database row containing WBS format
    */
   public P3WbsFormat(MapRow row)
   {
      int index = 1;
      while (true)
      {
         String suffix = String.format("%02d", Integer.valueOf(index++));
         Integer length = row.getInteger("WBSW_" + suffix);
         if (length == null || length.intValue() == 0)
         {
            break;
         }
         m_lengths.add(length);
         m_separators.add(row.getString("WBSS_" + suffix));
      }
   }
}
