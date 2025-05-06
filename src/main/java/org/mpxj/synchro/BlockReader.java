/*
 * file:       BlockReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.synchro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read a collection of fixed size blocks.
 */
abstract class BlockReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public BlockReader(StreamReader stream)
   {
      m_stream = stream;
   }

   /**
    * Read a list of fixed sized blocks from the input stream.
    *
    * @return List of MapRow instances representing the fixed size blocks
    */
   public List<MapRow> read() throws IOException
   {
      List<MapRow> result = new ArrayList<>();
      int blockCount = m_stream.readInt();
      if (blockCount != 0)
      {
         for (int index = 0; index < blockCount; index++)
         {
            // We use a LinkedHashMap to preserve insertion order in iteration
            // Useful when debugging the file format.
            Map<String, Object> map = new LinkedHashMap<>();
            readBlock(map);
            result.add(new MapRow(map));
         }
      }
      return result;
   }

   /**
    * Implemented by child classes to determine how the fixed size blocks are
    * read and interpreted.
    *
    * @param map Map to receive attributes read from the block
    */
   protected abstract void readBlock(Map<String, Object> map) throws IOException;

   protected final StreamReader m_stream;
}
