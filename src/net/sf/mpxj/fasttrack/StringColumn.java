
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class StringColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 0;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      offset = FastTrackUtility.skipTo(buffer, offset, 0x000F);

      m_data = new String[FastTrackUtility.getInt(buffer, offset)];
      offset += 4;

      // Offset to data
      offset += 4;

      int[] blockOffsets = new int[m_data.length + 1];
      for (int index = 0; index < blockOffsets.length; index++)
      {
         int offsetInBlock = FastTrackUtility.getInt(buffer, offset);
         blockOffsets[index] = offsetInBlock;
         offset += 4;
      }

      // Data size
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         int itemNameLength = blockOffsets[index + 1] - blockOffsets[index];
         m_data[index] = new String(buffer, offset, itemNameLength, FastTrackUtility.UTF16LE);
         offset += itemNameLength;
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
