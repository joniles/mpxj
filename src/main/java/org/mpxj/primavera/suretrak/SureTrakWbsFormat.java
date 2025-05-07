/*
 * file:       SureTrakWbsFormatColumn.java
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

import org.mpxj.primavera.common.AbstractWbsFormat;
import org.mpxj.primavera.common.MapRow;

/**
 * Reads the WBS format definition from a SureTrak database, and allows
 * that format to be applied to WBS values.
 */
public class SureTrakWbsFormat extends AbstractWbsFormat
{
   /**
    * Constructor. Reads the format definition.
    *
    * @param row database row containing WBS format
    */
   public SureTrakWbsFormat(MapRow row)
   {
      byte[] data = row.getRaw("DATA");
      int index = 1;
      while (true)
      {
         Integer length = Integer.valueOf(data[index++]);
         if (length.intValue() == 0)
         {
            break;
         }
         String separator = new String(data, index++, 1);

         m_lengths.add(length);
         m_separators.add(separator);
      }
   }
}
