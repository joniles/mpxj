/*
 * file:       SureTrakSTXFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       11/03/2018
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

package org.mpxj.primavera.suretrak;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FileHelper;
import org.mpxj.common.FixedLengthInputStream;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.primavera.common.Blast;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * Reads a schedule data from a SureTrak STX file.
 */
public final class SureTrakSTXFileReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      File tempDir = null;

      try
      {
         InputStreamHelper.skip(stream, (32768 + 4));
         tempDir = FileHelper.createTempDir();

         while (stream.available() > 0)
         {
            extractFile(stream, tempDir);
         }

         return SureTrakDatabaseReader.setProjectNameAndRead(tempDir);
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         FileHelper.deleteQuietly(tempDir);
      }
   }

   /**
    * Extracts the data for a single file from the input stream and writes
    * it to a target directory.
    *
    * @param stream input stream
    * @param dir target directory
    */
   private void extractFile(InputStream stream, File dir) throws IOException
   {
      byte[] dataSize = InputStreamHelper.read(stream, 4);
      InputStreamHelper.skip(stream, 4); // header
      byte[] fileName = InputStreamHelper.read(stream, 260);

      int dataSizeValue = getInt(dataSize, 0);
      String fileNameValue = getString(fileName, 0);

      File file = new File(dir, fileNameValue);
      if (dataSizeValue == 0)
      {
         FileHelper.createNewFile(file);
      }
      else
      {
         OutputStream os = Files.newOutputStream(file.toPath());
         FixedLengthInputStream inputStream = new FixedLengthInputStream(stream, dataSizeValue);
         Blast blast = new Blast();
         blast.blast(inputStream, os);
         os.close();
      }
   }

   /**
    * Retrieve a four byte integer.
    *
    * @param data byte array
    * @param offset offset into array
    * @return int value
    */
   private int getInt(byte[] data, int offset)
   {
      return ByteArrayHelper.getInt(data, offset);
   }

   /**
    * Retrieve a string from the byte array.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return String instance
    */
   private String getString(byte[] data, int offset)
   {
      StringBuilder buffer = new StringBuilder();
      char c;

      for (int loop = 0; offset + loop < data.length; loop++)
      {
         c = (char) data[offset + loop];

         if (c == 0)
         {
            break;
         }

         buffer.append(c);
      }

      return buffer.toString();
   }
}
