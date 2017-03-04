
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class UnknownColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 0;
   }

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      m_data = new Object[0];
      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
   }
}
