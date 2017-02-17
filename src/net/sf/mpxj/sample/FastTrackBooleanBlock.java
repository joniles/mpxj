
package net.sf.mpxj.sample;

import java.io.PrintWriter;

public class FastTrackBooleanBlock extends FastTrackBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 34;
      m_options = FastTrackUtility.getStringsWithLengths(offset, buffer, startIndex, false);

      offset = FastTrackUtility.skipTo(offset, buffer, startIndex, 0x000F);

      m_data = new boolean[FastTrackUtility.getInt(buffer, startIndex + offset) + 1];
      offset += 4;

      // Data length
      offset += 4;

      // Offsets to data
      offset += (m_data.length * 4);

      // Data length
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         int value = FastTrackUtility.getShort(buffer, startIndex + offset);
         offset += 2;
         m_data[index] = (value == 2);
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Options");
      for (String item : m_options)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");

      pw.println("  [Data");
      for (boolean item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private String[] m_options;
   private boolean[] m_data;
}
