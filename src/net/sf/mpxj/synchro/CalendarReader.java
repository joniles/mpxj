
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class CalendarReader extends TableReader
{
   public CalendarReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      Map<String, Object> map = new HashMap<String, Object>();

      int recordHeader = SynchroUtility.getInt(m_stream);
      if (recordHeader != 0x7FEC261D)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      System.out.println("CALENDAR");

      byte[] block1 = new byte[16];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      map.put("UUID", SynchroUtility.getUUID(m_stream));
      System.out.println("UUID: " + map.get("GUID"));

      String calendarName = SynchroUtility.getString(m_stream);
      System.out.println("Calendar name: " + calendarName);

      System.out.println("UNKNOWN TABLE 1");
      UnknownTableReader unknown1 = new UnknownTableReader(m_stream);
      unknown1.read();

      byte[] block2 = new byte[120];
      m_stream.read(block2);
      System.out.println("BLOCK2");
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));

      System.out.println("UNKNOWN TABLE 2");
      UnknownTableReader unknown2 = new UnknownTableReader(m_stream, 32);
      unknown2.read();

      DayTypeReader dayTypeReader = new DayTypeReader(m_stream);
      dayTypeReader.read();

      byte[] block3 = new byte[8];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      map.put("NAME", calendarName);

      m_rows.add(new MapRow(map));
   }
}
