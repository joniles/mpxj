/*
 * file:       StreamHelper
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       19/01/2018
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

package net.sf.mpxj.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * Common helper methods for working with streams.
 */
public final class StreamHelper
{
   /**
    * The documentation for InputStream.skip indicates that it can bail out early, and not skip
    * the requested number of bytes. I've encountered this in practice, hence this helper method.
    *
    * @param stream InputStream instance
    * @param skip number of bytes to skip
    */
   public static void skip(InputStream stream, long skip) throws IOException
   {
      long count = skip;
      while (count > 0)
      {
         count -= stream.skip(count);
      }
   }

   /**
    * Close a stream without raising an exception on error.
    *
    * @param stream stream to close
    */
   public static void closeQuietly(InputStream stream)
   {
      if (stream != null)
      {
         try
         {
            stream.close();
         }

         catch (IOException ex)
         {
            // Silently ignored
         }
      }
   }
}
