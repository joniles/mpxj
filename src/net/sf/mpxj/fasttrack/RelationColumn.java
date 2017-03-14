
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class RelationColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 16;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      StringsWithLengthBlock data = new StringsWithLengthBlock().read(buffer, offset, true);
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
