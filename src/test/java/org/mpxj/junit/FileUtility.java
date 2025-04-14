/*
 * file:       FileUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       07-Mar-2006
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

package org.mpxj.junit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Utility methods for handling files.
 */
public final class FileUtility
{
   /**
    * Utility method to ensure that two files contain identical data.
    *
    * @param file1 File object
    * @param file2 File object
    * @return boolean flag
    */
   public static boolean equals(File file1, File file2) throws Exception
   {
      boolean result;

      result = true;

      InputStream input1 = new BufferedInputStream(Files.newInputStream(file1.toPath()));
      InputStream input2 = new BufferedInputStream(Files.newInputStream(file2.toPath()));
      int c1;
      int c2;

      while (true)
      {
         // Ignore line endings: dropping all \r character should ensure that
         // both files just have \n line endings.
         do
         {
            c1 = input1.read();
         }
         while (c1 == '\r');

         do
         {
            c2 = input2.read();
         }
         while (c2 == '\r');

         if (c1 != c2)
         {
            result = false;
            break;
         }

         if (c1 == -1)
         {
            break;
         }
      }

      input1.close();
      input2.close();

      return (result);
   }

}
