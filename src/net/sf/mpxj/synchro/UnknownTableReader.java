
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;

import net.sf.mpxj.mpp.MPPUtility;

public class UnknownTableReader extends TableReader
{
   public UnknownTableReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      System.out.println("REMAINDER");
      byte[] remainder = new byte[m_stream.available()];
      m_stream.read(remainder);
      System.out.println(MPPUtility.hexdump(remainder, true, 16, ""));

      throw new IllegalArgumentException("Unexpected records!");
   }
}
