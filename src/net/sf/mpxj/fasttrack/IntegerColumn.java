
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class IntegerColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 40;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, offset);
      offset = data.getOffset();

      byte[][] rawData = data.getData();
      m_data = new Integer[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         m_data[index] = Integer.valueOf(FastTrackUtility.getInt(rawData[index], 0));
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
