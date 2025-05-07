/*
 * file:       UnknownBlockReader.java
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
import java.util.List;
import java.util.Map;

/**
 * Generic reader allowing blocks whose content is unknown to be read.
 */
class UnknownBlockReader extends BlockReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    * @param size block size
    */
   public UnknownBlockReader(StreamReader stream, int size)
   {
      super(stream);
      m_size = size;
   }

   @Override public List<MapRow> read() throws IOException
   {
      int header = m_stream.readInt();
      if (header != 0x06A1BCD0)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      List<MapRow> blocks = super.read();

      m_stream.readInt();

      return blocks;
   }

   @Override protected void readBlock(Map<String, Object> map) throws IOException
   {
      map.put("UNKNOWN", m_stream.readBytes(m_size));
   }

   private final int m_size;
}
