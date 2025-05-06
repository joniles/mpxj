/*
 * file:       StringColumn2.java
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
 * Column containing text values.
 */
class StringColumn2 extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 0;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      // Unknown
      offset += 2;

      // The presence of a non-zero value here determines what structure we expect next
      int structureFlags = FastTrackUtility.getByte(buffer, offset);
      offset += 1;

      if (structureFlags == 0x00)
      {
         offset += 33;
      }
      else
      {
         offset = FastTrackUtility.skipToNextMatchingShort(buffer, offset, 0x000F);
      }

      int numberOfItems = FastTrackUtility.getInt(buffer, offset);
      FastTrackUtility.validateSize(numberOfItems);
      m_data = new String[numberOfItems];
      offset += 4;

      // Offset to data
      offset += 4;

      int[] blockOffsets = new int[m_data.length + 1];
      for (int index = 0; index < blockOffsets.length; index++)
      {
         int offsetInBlock = FastTrackUtility.getInt(buffer, offset);
         blockOffsets[index] = offsetInBlock;
         offset += 4;
      }

      // Data size
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         int itemNameLength = blockOffsets[index + 1] - blockOffsets[index];
         FastTrackUtility.validateSize(itemNameLength);
         m_data[index] = FastTrackUtility.getString(buffer, offset, itemNameLength);
         offset += itemNameLength;
      }
      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Data");
      if (m_data != null)
      {
         for (Object item : m_data)
         {
            pw.println("    " + item);
         }
      }
      pw.println("  ]");
   }
}
