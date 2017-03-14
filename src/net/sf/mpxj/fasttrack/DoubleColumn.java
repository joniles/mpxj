
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class DoubleColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 16;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      m_data = new Double[FastTrackUtility.getInt(buffer, offset)];
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         m_data[index] = FastTrackUtility.getDouble(buffer, offset);
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
