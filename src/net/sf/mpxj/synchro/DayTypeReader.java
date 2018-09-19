
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class DayTypeReader extends TableReader
{
   public DayTypeReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      System.out.println("DAY TYPE");

      byte[] block1 = new byte[16];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      String dayTypeName = SynchroUtility.getString(m_stream);
      System.out.println("Day type name: " + dayTypeName);

      byte[] block2 = new byte[16];
      m_stream.read(block2);
      System.out.println("BLOCK2");
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));

      int timeRangeCount = SynchroUtility.getInt(m_stream);
      System.out.println("Time range count: " + timeRangeCount);
      byte[] timeRange = new byte[16];
      for (int index = 0; index < timeRangeCount; index++)
      {
         m_stream.read(timeRange);
         System.out.println("TIME RANGE");
         System.out.println(MPPUtility.hexdump(timeRange, true, 16, ""));
      }

      byte[] block3 = new byte[8];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));
   }

   @Override protected int rowMagicNumber()
   {
      return 0xC4F4C21D;
   }
}
