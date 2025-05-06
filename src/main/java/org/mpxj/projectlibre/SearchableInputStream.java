/*
 * file:       SearchableInputStream.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       24/04/2017
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

package org.mpxj.projectlibre;

import java.io.IOException;
import java.io.InputStream;

/**
 * Search through the input stream until the pattern is found, the acts as a normal input stream from that point.
 */
public class SearchableInputStream extends InputStream
{
   /**
    * Constructor.
    *
    * @param stream original input stream
    * @param pattern pattern to locate
    */
   public SearchableInputStream(InputStream stream, String pattern)
   {
      m_stream = stream;
      m_pattern = pattern.getBytes();
   }

   @Override public int read() throws IOException
   {
      int c;

      if (m_searching)
      {
         int index = 0;
         c = -1;
         while (m_searching)
         {
            c = m_stream.read();
            if (c == -1)
            {
               m_searchFailed = true;
               throw new IOException("Pattern not found");
            }

            if (c == m_pattern[index])
            {
               ++index;
               if (index == m_pattern.length)
               {
                  m_searching = false;
                  c = m_stream.read();
               }
            }
            else
            {
               index = 0;
            }
         }
      }
      else
      {
         c = m_stream.read();
      }

      return c;
   }

   /**
    * Returns true if the search failed.
    *
    * @return Boolean flag
    */
   public boolean getSearchFailed()
   {
      return m_searchFailed;
   }

   private final InputStream m_stream;
   private final byte[] m_pattern;
   private boolean m_searching = true;
   private boolean m_searchFailed;
}
