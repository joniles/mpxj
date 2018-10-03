
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class CostReader extends TableReader
{
   public CostReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("NAME", stream.readString());
      map.put("UNKNOWN1", stream.readBytes(20));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04E30E9C;
   }
}
