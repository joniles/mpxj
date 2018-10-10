
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class CalendarReader extends TableReader
{
   public CalendarReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("NAME", stream.readString());
      map.put("UNKNOWN1", stream.readTable(UnknownTableReader.class));
      map.put("UNKNOWN2", stream.readBytes(120));
      map.put("DAY_TYPE_ASSIGNMENTS", stream.readTable(DayTypeAssignmentReader.class));
      map.put("DAY_TYPES", stream.readTable(DayTypeReader.class));
      map.put("UNKNOWN4", stream.readBytes(8));

      for (Map.Entry<String, Object> entry : map.entrySet())
      {
         System.out.println(entry.getKey() + ": " + entry.getValue());
      }
      System.out.println();
   }

   @Override protected int rowMagicNumber()
   {
      return 0x7FEC261D;
   }
}
