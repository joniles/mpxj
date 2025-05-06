/*
 * file:       BytesReadInputStream.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       2019-01-28
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
import java.io.InputStream;

/**
 * Input stream wrapper which counts the number of bytes read.
 */
class BytesReadInputStream extends InputStream
{
   /**
    * Constructor.
    *
    * @param stream wrapped input stream
    */
   public BytesReadInputStream(InputStream stream)
   {
      m_stream = stream;
   }

   @Override public int read() throws IOException
   {
      ++m_bytesRead;
      return m_stream.read();
   }

   /**
    * Retrieve the number of bytes read.
    *
    * @return number of bytes read.
    */
   public int getBytesRead()
   {
      return m_bytesRead;
   }

   private final InputStream m_stream;
   private int m_bytesRead;
}
