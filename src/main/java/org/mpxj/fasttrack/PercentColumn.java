/*
 * file:       PercentColumn.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2017
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

package org.mpxj.fasttrack;

import java.io.PrintWriter;

/**
 * Column containing percentage values.
 */
class PercentColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 0;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      // Unknown
      offset += 6;

      // Structure flags? See StringColumn...
      offset += 4;

      // See DateColumn...
      offset = FastTrackUtility.skipToNextMatchingShort(buffer, offset, 0x000A) - 2;

      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, offset);
      offset = data.getOffset();

      byte[][] rawData = data.getData();
      m_data = new Double[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         m_data[index] = FastTrackUtility.getDouble(rawData[index], 0);
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }
}
