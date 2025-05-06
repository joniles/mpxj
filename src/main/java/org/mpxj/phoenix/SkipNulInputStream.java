/*
 * file:       SkipNulInputStream.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28 November 2015
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

package org.mpxj.phoenix;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream used to handle Phoenix XML files.
 * These files may have a trailing NUL character which XML parsers object to.
 */
public class SkipNulInputStream extends InputStream
{
   /**
    * Constructor.
    *
    * @param stream input stream we're wrapping
    */
   public SkipNulInputStream(InputStream stream)
   {
      m_stream = stream;
   }

   @Override public int read() throws IOException
   {
      while (true)
      {
         int c = m_stream.read();
         if (c != 0)
         {
            return c;
         }
      }
   }

   private final InputStream m_stream;
}
