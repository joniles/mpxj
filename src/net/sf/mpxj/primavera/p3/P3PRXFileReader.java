
package net.sf.mpxj.primavera.p3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.Blast;
import net.sf.mpxj.common.FixedLengthInputStream;
import net.sf.mpxj.common.StreamHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * Reads a schedule data from a P3 multi-file Btrieve database in a directory.
 */
public final class P3PRXFileReader extends AbstractProjectReader
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
         StreamHelper.skip(stream, 27000);
         tempDir = createTempDir();

         while (stream.available() > 0)
         {
            extractFile(stream, tempDir);
         }

         return P3Reader.setPrefixAndRead(tempDir);
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         if (tempDir != null)
         {
            tempDir.delete();
         }
      }
   }

   private void extractFile(InputStream stream, File dir) throws IOException
   {
      byte[] header = new byte[8];
      byte[] fileName = new byte[13];
      byte[] dataSize = new byte[4];

      stream.read(header);
      stream.read(fileName);
      stream.read(dataSize);

      int dataSizeValue = getInt(dataSize, 0);
      String fileNameValue = getString(fileName, 0);

      File file = new File(dir, fileNameValue);
      OutputStream os = new FileOutputStream(file);
      FixedLengthInputStream inputStream = new FixedLengthInputStream(stream, dataSizeValue);
      Blast blast = new Blast();
      blast.blast(inputStream, os);
      os.close();
   }

   private File createTempDir() throws IOException
   {
      File dir = File.createTempFile("mpxj", "tmp");
      dir.delete();
      dir.mkdirs();
      return dir;
   }

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
