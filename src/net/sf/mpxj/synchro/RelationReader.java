
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class RelationReader extends TableReader
{
   public RelationReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("PREDECESSOR_UUID", stream.readUUID());
      map.put("RELATION_TYPE", stream.readInteger()); // FS=1 SF=2 SS=3 FF=4
      map.put("UNKNOWN1", stream.readBytes(4));
      map.put("LAG", stream.readDuration());
      map.put("UNKNOWN2", stream.readBytes(4));
      map.put("LAG_IS_NEGATIVE", Boolean.valueOf(stream.readInt() == 2));
      map.put("CALENDAR_UUID", stream.readUUID());
      map.put("UNKNOWN3", stream.readBytes(8));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04E7E3D1;
   }
}
