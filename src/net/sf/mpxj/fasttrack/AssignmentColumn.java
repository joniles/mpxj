
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class AssignmentColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 14;
   }

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // TODO: temp
      offset += startIndex;

      if (FastTrackUtility.getByte(buffer, offset) == 0x01)
      {
         offset += 2;
      }
      else
      {
         offset += 20;
         StringsWithLengthBlock options = new StringsWithLengthBlock().read(buffer, offset, false);
         m_options = options.getData();
         offset = options.getOffset();

         // Skip bytes
         offset += 8;
      }

      StringsWithLengthBlock data = new StringsWithLengthBlock().read(buffer, offset, true);
      m_data = data.getData();
      offset = data.getOffset();

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      if (m_options != null)
      {
         pw.println("  [Options");
         for (String item : m_options)
         {
            pw.println("    " + item);
         }
         pw.println("  ]");
      }
      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }

   private String[] m_options;

}
