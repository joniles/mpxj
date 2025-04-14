/*
 * file:       AssignmentColumn1.java
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
 * Represents resource assignments.
 */
class AssignmentColumn1 extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 14;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      if (FastTrackUtility.getByte(buffer, offset) == 0x01)
      {
         offset += 2;
      }
      else
      {
         offset += 20;
         StringsWithLengthBlock options = new StringsWithLengthBlock().read(buffer, offset, false);
         m_options = options.getData();
         offset = options.getOffset();

         // Handle unknown string structure seen in a couple of examples from v11/2020
         if (FastTrackUtility.getByte(buffer, offset + 1) == 0x01)
         {
            offset += 4;
            int stringLength = FastTrackUtility.getInt(buffer, offset);
            offset += 4;
            // FastTrackUtility.getString(buffer, offset, stringLength);
            offset += stringLength;
         }

         offset += 8;
      }

      StringsWithLengthBlock data = new StringsWithLengthBlock().read(buffer, offset, true);
      m_data = data.getData();
      offset = data.getOffset();

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      if (m_options != null)
      {
         pw.println("  [Options");
         for (String item : m_options)
         {
            pw.println("    " + item);
         }
         pw.println("  ]");
      }
      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private String[] m_options;
}
