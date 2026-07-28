/*
 * file:       ReaderTokenizer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       15/02/2005
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

package org.mpxj.common;

import java.io.IOException;
import java.io.Reader;

/**
 * This class implements a tokenizer as per the underlying Tokenizer class,
 * with characters being read from a Reader instance.
 *
 * Characters are read in blocks: Reader.read() with no argument goes through
 * StreamDecoder per call, taking a lock and allocating for each character.
 * The Reader is therefore read ahead of the current token, so it must not be
 * shared with anything which reads from it afterwards.
 */
public class ReaderTokenizer extends Tokenizer
{
   /**
    * Constructor.
    *
    * @param r Reader instance
    */
   public ReaderTokenizer(Reader r)
   {
      m_reader = r;
   }

   @Override protected int read() throws IOException
   {
      if (m_index == m_length)
      {
         m_length = m_reader.read(m_readBuffer);
         m_index = 0;

         if (m_length < 1)
         {
            m_length = 0;
            return TT_EOF;
         }
      }

      return m_readBuffer[m_index++];
   }

   private final Reader m_reader;
   private final char[] m_readBuffer = new char[BUFFER_SIZE];
   private int m_index;
   private int m_length;

   private static final int BUFFER_SIZE = 8192;
}
