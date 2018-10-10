
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

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("UNKNOWN1", stream.readBytes(57));
      map.put("UNKNOWN2", stream.readDouble());
      map.put("UNKNOWN3", stream.readBytes(10));
      map.put("UNKNOWN4", stream.readUUID());
      map.put("RESOURCE_UUID", stream.readUUID());
      map.put("UNKNOWN5", stream.readBytes(16));
      map.put("PLANNED_UNITS", stream.readDouble());
      map.put("PLANNED_UNITS_TIME", stream.readDouble());
      map.put("ACTUAL_UNITS", stream.readDouble());
      map.put("ACTUAL_UNITS_TIME", stream.readDouble());
      map.put("UNKNOWN6", stream.readDouble());
      map.put("DRIVING", stream.readBoolean());
      map.put("UNKNOWN7", stream.readByte());
      map.put("FIXED_UNITS", Boolean.valueOf(!stream.readBoolean().booleanValue()));
      map.put("UNKNOWN8", stream.readBytes(13));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x4623D899;
   }
}
