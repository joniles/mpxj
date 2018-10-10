
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class UnknownBlockReader extends BlockReader
{
   public UnknownBlockReader(InputStream stream, int size)
   {
      super(stream);
      m_size = size;
   }

   @Override protected void readBlock(Map<String, Object> map) throws IOException
   {
      map.put("UNKNOWN", m_stream.readBytes(m_size));
   }
   private final int m_size;
}
