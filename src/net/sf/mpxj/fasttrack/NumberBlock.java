
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class NumberBlock extends AbstractBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 18;

      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, startIndex, offset);
      offset = data.getOffset();

      byte[][] rawData = data.getData();
      m_data = new Double[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         m_data[index] = Double.valueOf(FastTrackUtility.getDouble(rawData[index], 0));
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
