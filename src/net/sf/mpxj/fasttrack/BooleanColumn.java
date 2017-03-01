
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class BooleanColumn extends AbstractColumn
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 34;

      StringsWithLengthBlock options = new StringsWithLengthBlock().read(buffer, startIndex, offset, false);
      m_options = options.getData();
      offset = options.getOffset();

      offset = FastTrackUtility.skipTo(offset, buffer, startIndex, 0x000F);

      m_data = new Boolean[FastTrackUtility.getInt(buffer, startIndex + offset) + 1];
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
         m_data[index] = Boolean.valueOf(value == 2);
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
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private String[] m_options;
}
