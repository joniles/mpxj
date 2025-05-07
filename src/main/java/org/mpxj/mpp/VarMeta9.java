/*
 * file:       VarMeta9.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       03/01/2003
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.InputStreamHelper;

/**
 * This class reads in the data from a VarMeta block. This block contains
 * meta data about variable length data items stored in a Var2Data block.
 * The meta data allows the size of the Var2Data block to be determined,
 * along with the number of data items it contains, identifiers for each item,
 * and finally the offset of each item within the block.
 */
final class VarMeta9 extends AbstractVarMeta
{
   /**
    * Constructor. Extracts that makes up this block from the input stream.
    *
    * @param is Input stream from which data is read
    * @throws IOException on file read error
    */
   VarMeta9(InputStream is)
      throws IOException
   {
      int magic = readInt(is);
      if (magic != MAGIC)
      {
         throw new IOException("Bad magic number: " + magic);
      }

      /*m_unknown1 =*/readInt(is);
      m_itemCount = readInt(is);
      /*m_unknown2 =*/readInt(is);
      /*m_unknown3 =*/readInt(is);
      m_dataSize = readInt(is);

      Integer uniqueID;
      Integer type;
      Integer offset;
      Map<Integer, Integer> map;
      int[] offsets = new int[m_itemCount];

      //
      // There appears to be some Microsoft weirdness here. The unique ID
      // in this block is stored as a 3 byte integer. We use a 4 byte array
      // with the last byte permanently set to zero to read in the data
      // and convert it using the standard utility method.
      //
      byte[] uniqueIDArray = new byte[4];

      for (int loop = 0; loop < m_itemCount; loop++)
      {
         InputStreamHelper.read(is, uniqueIDArray, 3);

         uniqueID = Integer.valueOf(ByteArrayHelper.getInt(uniqueIDArray, 0));

         type = Integer.valueOf(readByte(is));
         offset = Integer.valueOf(readInt(is));

         map = m_table.computeIfAbsent(uniqueID, k -> new TreeMap<>());
         map.put(type, offset);
         offsets[loop] = offset.intValue();
      }

      Arrays.sort(offsets);
      setOffsets(offsets);
   }

   private static final int MAGIC = 0xFADFADBA;
}
