
package net.sf.mpxj.sample;

import java.io.PrintWriter;

public class FastTrackDoubleBlock extends FastTrackBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 16;

      m_data = new double[FastTrackUtility.getInt(buffer, startIndex + offset)];
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         m_data[index] = FastTrackUtility.getDouble(buffer, startIndex + offset);
         offset += 8;
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Data");
      for (double item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private double[] m_data;
}
