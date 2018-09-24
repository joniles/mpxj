
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class ResourceAssignmentReader extends TableReader
{
   public ResourceAssignmentReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      StreamReader stream = new StreamReader(m_stream);
      map.put("UNKNOWN1", stream.readBytes(179));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x4623D899;
   }
}
