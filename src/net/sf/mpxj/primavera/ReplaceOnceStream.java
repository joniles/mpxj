/*
 * file:       ReplaceOnceStream.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2013
 * date:       02/08/2013
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

package net.sf.mpxj.primavera;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import net.sf.mpxj.MPXJException;

/**
 * This trivial stream wrapper class finds the first instance of a
 * given regular expression in the first N bytes of a stream, and replaces
 * it with the supplied replacement. Once it has done this, it empties its
 * internal buffer, then delegates all further read() calls straight to the
 * parent stream.
 */
class ReplaceOnceStream extends InputStream
{
   /**
    * Constructor.
    *
    * @param parent the input stream we're wrapping
    * @param regex pattern to match
    * @param replacement replacement for the pattern
    * @param scope number of bytes to test for the pattern
    * @param charset encoding to use when converting from byte[] to string
    * @throws MPXJException
    */
   public ReplaceOnceStream(InputStream parent, String regex, String replacement, int scope, Charset charset)
      throws MPXJException
   {
      try
      {
         m_parent = parent;
         m_buffer = new byte[scope];
         int bytesRead = parent.read(m_buffer);
         String scopeString = new String(m_buffer, 0, bytesRead, charset);
         String resultString = scopeString.replaceFirst(regex, replacement);
         m_buffer = resultString.getBytes(charset);
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to read file header", ex);
      }
   }

   @Override public int read() throws IOException
   {
      int result;

      if (m_buffer == null)
      {
         result = m_parent.read();
      }
      else
      {
         result = m_buffer[m_bufferIndex++];
         if (m_bufferIndex == m_buffer.length)
         {
            m_buffer = null;
         }
      }
      return result;
   }

   private final InputStream m_parent;
   private int m_bufferIndex;
   private byte[] m_buffer;
}
