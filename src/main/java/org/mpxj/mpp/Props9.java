/*
 * file:       Props9.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2003
 * date:       07/11/2003
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

package org.mpxj.mpp;

//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.InputStreamHelper;

/**
 * This class represents the Props files found in Microsoft Project MPP9 files.
 */
final class Props9 extends Props
{
   /**
    * Constructor, reads the property data from an input stream.
    *
    * @param is input stream for reading props data
    */
   Props9(InputStream is)
      throws IOException
   {
      //FileOutputStream fos = new FileOutputStream ("c:\\temp\\props9." + System.currentTimeMillis() + ".txt");
      //PrintWriter pw = new PrintWriter (fos);
      if (is.available() < 16)
      {
         return;
      }

      byte[] header = InputStreamHelper.read(is, 16);
      int headerCount = ByteArrayHelper.getShort(header, 12);
      int foundCount = 0;
      int availableBytes = is.available();

      while (foundCount < headerCount)
      {
         int itemSize = readInt(is);
         int itemKey = readInt(is);
         /*int attrib3 = */readInt(is);
         availableBytes -= 12;

         if (availableBytes < itemSize || itemSize < 1)
         {
            break;
         }

         byte[] data = InputStreamHelper.read(is, itemSize);
         availableBytes -= itemSize;

         m_map.put(Integer.valueOf(itemKey), data);
         //pw.println(foundCount + " "+ attrib2 + ": " + ByteArrayHelper.hexdump(data, true));
         ++foundCount;

         //
         // Align to two byte boundary
         //
         if (data.length % 2 != 0)
         {
            InputStreamHelper.skip(is, 1);
         }
      }

      //pw.flush();
      //pw.close();
   }
}
