
package net.sf.mpxj.phoenix;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

public class PhoenixInputStream extends InputStream
{
   public PhoenixInputStream(InputStream stream)
      throws IOException
   {
      m_stream = prepareInputStream(stream);
   }

   @Override public int read() throws IOException
   {
      return m_stream.read();
   }

   public String getVersion()
   {
      return m_properties.get("VERSION");
   }

   public boolean isCompressed()
   {
      String result = m_properties.get("COMPRESSION");
      return result != null && result.equals("yes");
   }

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

   private String readHeaderString(BufferedInputStream bis) throws IOException
   {
      int bufferSize = 100;
      bis.mark(bufferSize);
      byte[] buffer = new byte[bufferSize];
      bis.read(buffer);
      Charset charset = Charset.forName("UTF-8");
      String header = new String(buffer, charset);
      int prefixIndex = header.indexOf("PPX!!!!|");
      int suffixIndex = header.indexOf("|!!!!XPP");

      if (prefixIndex != 0 || suffixIndex == -1)
      {
         throw new IOException("File format not recognised");
      }

      int skip = suffixIndex + 9;
      bis.reset();
      bis.skip(skip);

      return header.substring(prefixIndex + 8, suffixIndex);
   }

   private void readHeaderProperties(BufferedInputStream bis) throws IOException
   {
      String header = readHeaderString(bis);
      for (String property : header.split("\\|"))
      {
         String[] expression = property.split("=");
         m_properties.put(expression[0], expression[1]);
      }
   }

   private final InputStream m_stream;
   private final Map<String, String> m_properties = new HashMap<String, String>();
}
