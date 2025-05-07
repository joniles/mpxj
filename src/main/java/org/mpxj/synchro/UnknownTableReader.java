/*
 * file:       UnknownTableReader.java
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
import java.util.Map;

/**
 * Read raw data from a table with unknown structure.
 */
class UnknownTableReader extends TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public UnknownTableReader(StreamReader stream)
   {
      this(stream, 0, 0);
   }

   /**
    * Constructor used where we know the row size and magic number.
    *
    * @param stream input stream
    * @param rowSize row size
    * @param rowMagicNumber row magic number
    */
   public UnknownTableReader(StreamReader stream, int rowSize, int rowMagicNumber)
   {
      super(stream);
      m_rowSize = rowSize;
      m_rowMagicNumber = rowMagicNumber;
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      if (m_rowSize == 0)
      {
         //         System.out.println("REMAINDER");
         //         byte[] remainder = new byte[m_stream.available()];
         //         m_stream.read(remainder);
         //         System.out.println(ByteArrayHelper.hexdump(remainder, true, 16, ""));
         throw new IllegalArgumentException("Unexpected records!");
      }

      map.put("UNKNOWN1", stream.readBytes(m_rowSize));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return m_rowMagicNumber;
   }

   private final int m_rowSize;
   private final int m_rowMagicNumber;
}
