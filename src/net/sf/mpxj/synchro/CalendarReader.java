
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

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
      map.put("UNKNOWN2", stream.readBytes(4));
      map.put("SUNDAY_DAY_TYPE", stream.readUUID());
      map.put("MONDAY_DAY_TYPE", stream.readUUID());
      map.put("TUESDAY_DAY_TYPE", stream.readUUID());
      map.put("WEDNESDAY_DAY_TYPE", stream.readUUID());
      map.put("THURSDAY_DAY_TYPE", stream.readUUID());
      map.put("FRIDAY_DAY_TYPE", stream.readUUID());
      map.put("SATURDAY_DAY_TYPE", stream.readUUID());
      map.put("UNKNOWN3", stream.readBytes(4));
      map.put("DAY_TYPE_ASSIGNMENTS", stream.readTable(DayTypeAssignmentReader.class));
      map.put("DAY_TYPES", stream.readTable(DayTypeReader.class));
      map.put("UNKNOWN4", stream.readBytes(8));
   }

   @Override protected void postTrailer(StreamReader stream) throws IOException
   {
      m_defaultCalendarUUID = stream.readUUID();
   }

   @Override protected int rowMagicNumber()
   {
      return 0x7FEC261D;
   }

   public UUID getDefaultCalendarUUID()
   {
      return m_defaultCalendarUUID;
   }

   private UUID m_defaultCalendarUUID;
}
