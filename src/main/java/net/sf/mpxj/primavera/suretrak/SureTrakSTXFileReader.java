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

package net.sf.mpxj.primavera.suretrak;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.FileHelper;
import net.sf.mpxj.common.FixedLengthInputStream;
import net.sf.mpxj.common.StreamHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.primavera.common.Blast;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * Reads a schedule data from a SureTrak STX file.
 */
public final class SureTrakSTXFileReader extends AbstractProjectReader
{
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      File tempDir = null;

      try
      {
         StreamHelper.skip(stream, (32768 + 4));
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
      byte[] dataSize = new byte[4];
      byte[] header = new byte[4];
      byte[] fileName = new byte[260];

      stream.read(dataSize);
      stream.read(header);
      stream.read(fileName);

      int dataSizeValue = getInt(dataSize, 0);
      String fileNameValue = getString(fileName, 0);

      File file = new File(dir, fileNameValue);
      if (dataSizeValue == 0)
      {
         FileHelper.createNewFile(file);
      }
      else
      {
         OutputStream os = new FileOutputStream(file);
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
   public int getInt(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * Retrieve a string from the byte array.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return String instance
    */
   public String getString(byte[] data, int offset)
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

   private List<ProjectListener> m_projectListeners;
}
