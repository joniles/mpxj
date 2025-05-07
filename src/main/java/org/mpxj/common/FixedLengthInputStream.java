/**
 * Java Web Archive Toolkit - Software to read and validate ARC, WARC
 * and GZip files. (http://jwat.org/)
 * Copyright 2011-2012 Netarkivet.dk (http://netarkivet.dk/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mpxj.common;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@code InputStream} with a fixed amount of bytes available to read.
 * When the stream is closed the remaining bytes that have not been read are
 * read or skipped.
 *
 * @author lbihanic, selghissassi, nicl
 */
public final class FixedLengthInputStream extends FilterInputStream
{
   /** Remaining bytes available. */
   private long m_remaining;

   /**
    * Create a new input stream with a fixed number of bytes available from
    * the underlying stream.
    *
    * @param in the input stream to wrap
    * @param length fixed number of bytes available through this stream
    */
   public FixedLengthInputStream(@SuppressWarnings("hiding") InputStream in, long length)
   {
      super(in);
      this.m_remaining = length;
   }

   /**
    * Closing will only skip to the end of this fixed length input stream and
    * not call the parent's close method.
    *
    * @throws IOException if an I/O error occurs while closing stream
    */
   @Override public void close() throws IOException
   {
      long skippedLast;
      if (m_remaining > 0)
      {
         do
         {
            skippedLast = skip(m_remaining);
         }
         while (m_remaining > 0 && skippedLast > 0);
      }
   }

   @Override public int available()
   {
      return (m_remaining > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) (m_remaining);
   }

   @Override public boolean markSupported()
   {
      return false;
   }

   @Override public synchronized void mark(int readlimit)
   {
      // Not supported
   }

   @Override public synchronized void reset()
   {
      throw new UnsupportedOperationException();
   }

   @Override public int read() throws IOException
   {
      int b = -1;
      if (m_remaining > 0)
      {
         b = in.read();
         if (b != -1)
         {
            --m_remaining;
         }
      }
      return b;
   }

   @Override public int read(byte[] b) throws IOException
   {
      return read(b, 0, b.length);
   }

   @Override public int read(byte[] b, int off, int len) throws IOException
   {
      int bytesRead = -1;
      if (m_remaining > 0)
      {
         bytesRead = in.read(b, off, (int) Math.min(len, m_remaining));
         if (bytesRead > 0)
         {
            m_remaining -= bytesRead;
         }
      }
      return bytesRead;
   }

   @Override public long skip(long n) throws IOException
   {
      long bytesSkipped = 0;
      if (m_remaining > 0)
      {
         bytesSkipped = in.skip(Math.min(n, m_remaining));
         m_remaining -= bytesSkipped;
      }
      return bytesSkipped;
   }
}