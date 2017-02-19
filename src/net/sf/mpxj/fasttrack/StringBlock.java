
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class StringBlock extends AbstractBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      offset = FastTrackUtility.skipTo(offset, buffer, startIndex, 0x000F);

      m_data = new String[FastTrackUtility.getInt(buffer, startIndex + offset)];
      offset += 4;

      // Offset to data
      offset += 4;

      int[] blockOffsets = new int[m_data.length + 1];
      for (int index = 0; index < blockOffsets.length; index++)
      {
         int offsetInBlock = FastTrackUtility.getInt(buffer, startIndex + offset);
         blockOffsets[index] = offsetInBlock;
         offset += 4;
      }

      // Data size
      offset += 4;

      for (int index = 0; index < m_data.length; index++)
      {
         int itemNameLength = blockOffsets[index + 1] - blockOffsets[index];
         m_data[index] = new String(buffer, startIndex + offset, itemNameLength, FastTrackUtility.UTF16LE);
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
