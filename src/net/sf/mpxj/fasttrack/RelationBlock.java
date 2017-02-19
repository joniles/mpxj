
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class RelationBlock extends AbstractBlock
{
   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 16;

      StringsWithLengthBlock data = new StringsWithLengthBlock().read(buffer, startIndex, offset, true);
      m_data = data.getData();
      offset = data.getOffset();

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
