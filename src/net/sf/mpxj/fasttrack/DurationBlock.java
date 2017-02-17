
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class DurationBlock extends AbstractBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 18;

      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, startIndex, offset);
      offset = data.getOffset();

      byte[][] rawData = data.getData();
      m_data = new double[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         m_data[index] = FastTrackUtility.getDouble(rawData[index], 0);
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
