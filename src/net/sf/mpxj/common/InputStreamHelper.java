/*
 * file:       InputStreamHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       06/06/2016
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper methods for dealing with InputStreams.
 */
public class InputStreamHelper
{
   /**
    * Copy the data from an InputStream to a temp file.
    *
    * @param inputStream data source
    * @param tempFileSuffix suffix to use for temp file
    * @return File instance
    */
   public static File writeStreamToTempFile(InputStream inputStream, String tempFileSuffix) throws IOException
   {
      FileOutputStream outputStream = null;

      try
      {
         File file = File.createTempFile("mpxj", tempFileSuffix);
         outputStream = new FileOutputStream(file);
         byte[] buffer = new byte[1024];
         while (true)
         {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead == -1)
            {
               break;
            }
            outputStream.write(buffer, 0, bytesRead);
         }
         return file;
      }

      finally
      {
         if (outputStream != null)
         {
            outputStream.close();
         }
      }
   }
}
