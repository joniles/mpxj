
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class BooleanColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 34;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      StringsWithLengthBlock options = new StringsWithLengthBlock().read(buffer, offset, false);
      m_options = options.getData();
      offset = options.getOffset();

      offset = FastTrackUtility.skipTo(offset, buffer, 0, 0x000F);

      m_data = new Boolean[FastTrackUtility.getInt(buffer, offset) + 1];
      offset += 4;

      // Data length
      offset += 4;

      // Offsets to data
      offset += (m_data.length * 4);

      // Data length
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         int value = FastTrackUtility.getShort(buffer, offset);
         offset += 2;
         if (value != 2)
         {
            m_data[index] = Boolean.valueOf(value == 1);
         }
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
