
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class CalendarReader extends TableReader
{
   public CalendarReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      System.out.println("CALENDAR");

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
      UnknownTableReader unknown2 = new UnknownTableReader(m_stream, 28, 0xD1A3D6C);
      unknown2.read();

      DayTypeReader dayTypeReader = new DayTypeReader(m_stream);
      dayTypeReader.read();

      byte[] block3 = new byte[8];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      map.put("NAME", calendarName);
   }

   @Override protected int rowMagicNumber()
   {
      return 0x7FEC261D;
   }
}
