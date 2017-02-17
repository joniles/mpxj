
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class AssignmentBlock extends AbstractBlock
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 34;

      StringsWithLengthBlock options = new StringsWithLengthBlock().read(buffer, startIndex, offset, true);
      m_options = options.getData();
      offset = options.getOffset();

      // Skip bytes
      offset += 2;

      StringsWithLengthBlock data = new StringsWithLengthBlock().read(buffer, startIndex, offset, true);
      m_data = data.getData();
      offset = data.getOffset();

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
      for (String item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private String[] m_options;
   private String[] m_data;
}
