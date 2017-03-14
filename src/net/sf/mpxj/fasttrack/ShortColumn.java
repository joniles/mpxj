
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class ShortColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 18;
   }

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, startIndex + offset);
      offset = data.getOffset();

      byte[][] rawData = data.getData();
      m_data = new Integer[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         m_data[index] = Integer.valueOf(FastTrackUtility.getShort(rawData[index], 0));
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
