
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class DoubleBlock extends AbstractBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 16;

      m_data = new Double[FastTrackUtility.getInt(buffer, startIndex + offset)];
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         m_data[index] = Double.valueOf(FastTrackUtility.getDouble(buffer, startIndex + offset));
         offset += 8;
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }
}
