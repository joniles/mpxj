/*
 * file:       EncryptedDocumentInputStream.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2007
 * date:       20/10/2007
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

import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * This class wraps the POI {@link DocumentInputStream} class
 * to allow data to be decrypted before passing
 * it back to the caller.
 */
final class EncryptedDocumentInputStream extends InputStream
{
   /**
    * Constructor.
    *
    * @param entry file entry
    * @param mask the mask used to decrypt the stream.
    */
   public EncryptedDocumentInputStream(DocumentEntry entry, int mask)
      throws IOException
   {
      m_dis = new DocumentInputStream(entry);
      m_mask = mask;
   }

   @Override public int read() throws IOException
   {
      int value = m_dis.read();
      value ^= m_mask;
      return (value);
   }

   @Override public int read(byte[] b, int off, int len) throws IOException
   {
      int result = m_dis.read(b, off, len);
      for (int loop = 0; loop < len; loop++)
      {
         b[loop + off] ^= m_mask;
      }
      return result;
   }

   @Override public int available()
   {
      return m_dis.available();
   }

   @Override public void close()
   {
      m_dis.close();
   }

   @Override public synchronized void mark(int readlimit)
   {
      m_dis.mark(readlimit);
   }

   @Override public boolean markSupported()
   {
      return m_dis.markSupported();
   }

   @Override public synchronized void reset()
   {
      m_dis.reset();
   }

   @Override public long skip(long n) throws IOException
   {
      return m_dis.skip(n);
   }

   private final DocumentInputStream m_dis;
   private final int m_mask;
}
