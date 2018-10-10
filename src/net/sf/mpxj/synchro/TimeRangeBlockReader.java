
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class TimeRangeBlockReader extends BlockReader
{
   public TimeRangeBlockReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readBlock(Map<String, Object> map) throws IOException
   {
      map.put("START", m_stream.readTime());
      map.put("UNKNOWN1", m_stream.readBytes(4));
      map.put("END", m_stream.readTime());
      map.put("UNKNOWN2", m_stream.readBytes(4));
   }
}
