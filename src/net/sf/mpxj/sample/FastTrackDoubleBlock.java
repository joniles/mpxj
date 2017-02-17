
package net.sf.mpxj.sample;

import java.io.PrintWriter;

public class FastTrackDoubleBlock extends FastTrackBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      m_skip = new byte[16];
      System.arraycopy(buffer, startIndex + offset, m_skip, 0, m_skip.length);
      offset += m_skip.length;

      m_numberOfItems = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;

      m_data = new double[m_numberOfItems];
      for (int index = 0; index < m_numberOfItems; index++)
      {
         m_data[index] = FastTrackUtility.getDouble(buffer, startIndex + offset);
         offset += 8;
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.print("  Skip: " + FastTrackUtility.hexdump(m_skip, 0, m_skip.length, false, 16, ""));
      pw.println("  Number of items: " + m_numberOfItems);
      pw.println("  [Data");
      for (double item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private int m_numberOfItems;
   private double[] m_data;
   private byte[] m_skip;
}
