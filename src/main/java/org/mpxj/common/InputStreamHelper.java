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

package org.mpxj.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

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
      File file = Files.createTempFile("mpxj", tempFileSuffix).toFile();
      writeInputStreamToOutputStream(inputStream, Files.newOutputStream(file.toPath()));
      return file;
   }

   /**
    * Copy the data from an InputStream to and OutputStream.
    *
    * @param inputStream data source
    * @param outputStream target stream
    */
   public static void writeInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException
   {
      try
      {
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
      }

      finally
      {
         outputStream.close();
      }
   }

   /**
    * Expands a zip file input stream into a temporary directory.
    *
    * @param inputStream zip file input stream
    * @return File instance representing the temporary directory
    */
   public static File writeZipStreamToTempDir(InputStream inputStream) throws IOException
   {
      File dir = FileHelper.createTempDir();

      try
      {
         processZipStream(dir, inputStream);
      }

      catch (ZipException ex)
      {
         // Java doesn't support zip files with zero byte entries.
         // We could use a different library which does handle these zip files, but
         // I'm reluctant to add new dependencies just for this. Rather than
         // propagating the error, we'll just stop at this point and see if we
         // can make sense of anything we have extracted from the zip file so far.
         // For what it's worth I haven't come across a valid compressed schedule file
         // which includes zero bytes files.
         if (!ex.getMessage().equals("only DEFLATED entries can have EXT descriptor"))
         {
            throw ex;
         }
      }

      return dir;
   }

   /**
    * Reda the available bytes from the input stream and populate
    * a new byte array.
    *
    * @param is InputStream instance
    * @return new byte array instance
    */
   public static byte[] readAvailable(InputStream is) throws IOException
   {
      return read(is, new byte[is.available()]);
   }

   /**
    * Reads a specified number of bytes from the input stream and populates
    * a new byte array. If the required number of bytes can't be read
    * an exception will be raised.
    *
    * @param is InputStream instance
    * @param size number of bytes to read
    * @return new byte array instance
    */
   public static byte[] read(InputStream is, int size) throws IOException
   {
      return read(is, new byte[size]);
   }

   /**
    * Read bytes from the input stream to fill the provided array.
    * If the array can't be filled an exception will be raised.
    *
    * @param is InputStream instance
    * @param data array to fill
    * @return filled byte array
    */
   public static byte[] read(InputStream is, byte[] data) throws IOException
   {
      return read(is, data, data.length);
   }

   /**
    * Read bytes from the input stream to fill the provided array.
    * If the array can't be filled with the requested number of bytes,
    * an exception will be raised.
    *
    * @param is InputStream instance
    * @param data array to fill
    * @param size number of bytes to read
    * @return filled byte array
    */
   public static byte[] read(InputStream is, byte[] data, int size) throws IOException
   {
      int bytesRead = is.read(data, 0, size);
      if (bytesRead != size)
      {
         throw new RuntimeException("Unable to read the required number of bytes");
      }
      return data;
   }

   /**
    * Expands a zip file input stream into a temporary directory.
    *
    * @param dir temporary directory
    * @param inputStream zip file input stream
    */
   private static void processZipStream(File dir, InputStream inputStream) throws IOException
   {
      ZipInputStream zip = new ZipInputStream(inputStream);
      while (true)
      {
         ZipEntry entry = zip.getNextEntry();
         if (entry == null)
         {
            break;
         }

         File file = new File(dir, entry.getName());

         // https://snyk.io/research/zip-slip-vulnerability
         if (!file.getCanonicalFile().toPath().startsWith(dir.getCanonicalFile().toPath()))
         {
            throw new IOException("Entry is outside of the target dir: " + entry.getName());
         }

         if (entry.isDirectory())
         {
            FileHelper.mkdirsQuietly(file);
            continue;
         }

         File parent = file.getParentFile();
         if (parent != null)
         {
            FileHelper.mkdirsQuietly(parent);
         }

         FileOutputStream fos = new FileOutputStream(file);
         byte[] bytes = new byte[1024];
         int length;
         while ((length = zip.read(bytes)) >= 0)
         {
            fos.write(bytes, 0, length);
         }
         fos.close();
      }
   }

   /**
    * The documentation for {@code InputStream.skip} indicates that it can bail out early, and not skip
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
         long skipped = stream.skip(count);
         if (skipped == 0)
         {
            throw new IOException("Cannot skip forward within InputStream");
         }
         count -= skipped;
      }
   }
}
