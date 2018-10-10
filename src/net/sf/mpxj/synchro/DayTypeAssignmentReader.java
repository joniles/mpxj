
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class DayTypeAssignmentReader extends TableReader
{
   public DayTypeAssignmentReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("DATE", stream.readDate());
      map.put("UNKNOWN1", stream.readBytes(4));
      map.put("DAY_TYPE_UUID", stream.readUUID());
      map.put("UNKNOWN2", stream.readBytes(4));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return 0xD1A3D6C;
   }
}
