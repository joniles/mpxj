/*
 * file:       PhoenixInputStream.java
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import org.mpxj.common.CharsetHelper;
import org.mpxj.common.InputStreamHelper;

/**
 * Input stream used to handle compressed Phoenix files.
 */
public class PhoenixInputStream extends InputStream
{
   /**
    * Constructor.
    *
    * @param stream input stream we're wrapping
    */
   public PhoenixInputStream(InputStream stream)
      throws IOException
   {
      m_stream = prepareInputStream(stream);
      //Files.copy(m_stream, new File("c:/temp/project1.ppx").toPath(), StandardCopyOption.REPLACE_EXISTING);
   }

   @Override public int read() throws IOException
   {
      return m_stream.read();
   }

   /**
    * Retrieve the file format version from the Phoenix header.
    *
    * @return file format version
    */
   public String getVersion()
   {
      return m_properties.get("VERSION");
   }

   /**
    * Read the compression flag from the Phoenix file header.
    *
    * @return true if the file is compressed
    */
   public boolean isCompressed()
   {
      String result = m_properties.get("COMPRESSION");
      return result != null && result.equals("yes");
   }

   /**
    * If the file is compressed, handle this so that the stream is ready to read.
    *
    * @param stream input stream
    * @return uncompressed input stream
    */
   private InputStream prepareInputStream(InputStream stream) throws IOException
   {
      InputStream result;
      BufferedInputStream bis = new BufferedInputStream(stream);
      readHeaderProperties(bis);
      if (isCompressed())
      {
         result = new InflaterInputStream(bis);
      }
      else
      {
         result = bis;
      }
      return result;
   }

   /**
    * Read the header from the Phoenix file.
    *
    * @param stream input stream
    * @return raw header data
    */
   private String readHeaderString(BufferedInputStream stream) throws IOException
   {
      int bufferSize = 100;
      stream.mark(bufferSize);
      byte[] buffer = InputStreamHelper.read(stream, bufferSize);
      Charset charset = CharsetHelper.UTF8;
      String header = new String(buffer, charset);
      int prefixIndex = header.indexOf("PPX!!!!|");
      int suffixIndex = header.indexOf("|!!!!XPP");

      if (prefixIndex != 0 || suffixIndex == -1)
      {
         throw new IOException("File format not recognised");
      }

      int skip = suffixIndex + 9;
      stream.reset();
      InputStreamHelper.skip(stream, skip);

      return header.substring(prefixIndex + 8, suffixIndex);
   }

   /**
    * Read properties from the raw header data.
    *
    * @param stream input stream
    */
   private void readHeaderProperties(BufferedInputStream stream) throws IOException
   {
      String header = readHeaderString(stream);
      for (String property : header.split("\\|"))
      {
         String[] expression = property.split("=");
         m_properties.put(expression[0], expression[1]);
      }
   }

   private final InputStream m_stream;
   private final Map<String, String> m_properties = new HashMap<>();
}
