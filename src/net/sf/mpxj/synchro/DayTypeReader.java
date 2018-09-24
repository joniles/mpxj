
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class DayTypeReader extends TableReader
{
   public DayTypeReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      StreamReader stream = new StreamReader(m_stream);

      map.put("UNKNOWN1", stream.readBytes(16));
      map.put("NAME", stream.readString());
      map.put("UNKNOWN2", stream.readBytes(16));
      map.put("TIME_RANGES", stream.readBlocks(16));
      map.put("UNKNOWN3", stream.readBytes(8));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return 0xC4F4C21D;
   }
}
