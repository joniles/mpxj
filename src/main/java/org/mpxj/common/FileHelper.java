/*
 * file:       FileHelper
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       12/03/2018
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Common helper methods for working with files.
 */
public final class FileHelper
{
   /**
    * Delete a file and raise an exception if unsuccessful.
    *
    * @param file file to delete
    */
   public static final void delete(File file) throws IOException
   {
      if (file != null)
      {
         if (!file.delete())
         {
            throw new IOException("Failed to delete file");
         }
      }
   }

   /**
    * Delete a file ignoring failures.
    *
    * @param file file to delete
    */
   public static final void deleteQuietly(File file)
   {
      if (file != null)
      {
         if (file.isDirectory())
         {
            File[] children = file.listFiles();
            if (children != null)
            {
               for (File child : children)
               {
                  deleteQuietly(child);
               }
            }
         }

         // noinspection ResultOfMethodCallIgnored
         file.delete();
      }
   }

   /**
    * Create a directory hierarchy, raise an exception in case of failure.
    *
    * @param file child file or directory
    */
   public static final void mkdirs(File file) throws IOException
   {
      if (file != null)
      {
         if (!file.mkdirs())
         {
            throw new IOException("Failed to create directories");
         }
      }
   }

   /**
    * Create a directory hierarchy, ignore failures.
    *
    * @param file child file or directory
    */
   public static final void mkdirsQuietly(File file)
   {
      if (file != null)
      {
         // noinspection ResultOfMethodCallIgnored
         file.mkdirs();
      }
   }

   /**
    * Create a temporary directory.
    *
    * @return File instance representing temporary directory
    */
   public static final File createTempDir() throws IOException
   {
      File dir = Files.createTempFile("mpxj", "tmp").toFile();
      delete(dir);
      mkdirs(dir);
      return dir;
   }

   /**
    * Create a new file. Raise an exception if the file exists.
    *
    * @param file file to create
    */
   public static final void createNewFile(File file) throws IOException
   {
      if (!file.createNewFile())
      {
         throw new IOException("Failed to create new file");
      }
   }
}
