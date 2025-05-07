/*
 * file:       BooleanColumn.java
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
 * Column containing Boolean values.
 */
class BooleanColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 34;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      StringsWithLengthBlock options = new StringsWithLengthBlock().read(buffer, offset, false);
      m_options = options.getData();
      offset = options.getOffset();

      offset = FastTrackUtility.skipToNextMatchingShort(buffer, offset, 0x000F);

      int numberOfItems = FastTrackUtility.getInt(buffer, offset) + 1;
      FastTrackUtility.validateSize(numberOfItems);
      m_data = new Boolean[numberOfItems];
      offset += 4;

      // Data length
      offset += 4;

      // Offsets to data
      offset += (m_data.length * 4);

      // Data length
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         int value = FastTrackUtility.getShort(buffer, offset);
         offset += 2;
         if (value != 2)
         {
            m_data[index] = Boolean.valueOf(value == 1);
         }
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Options");
      for (String item : m_options)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");

      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private String[] m_options;
}
