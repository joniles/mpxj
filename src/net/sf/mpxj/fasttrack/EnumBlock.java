
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class EnumBlock extends AbstractBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 34;

      StringsWithLengthBlock options = new StringsWithLengthBlock().read(buffer, startIndex, offset, false);
      m_options = options.getData();
      offset = options.getOffset();

      // Skip bytes
      offset += 4;

      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, startIndex, offset);
      offset = data.getOffset();

      byte[][] rawData = data.getData();
      m_data = new String[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         int optionIndex = FastTrackUtility.getShort(rawData[index], 0) - 1;
         if (optionIndex >= 0 && optionIndex < m_options.length)
         {
            m_data[index] = m_options[optionIndex];
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
