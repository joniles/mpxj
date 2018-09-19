
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class CostReader extends TableReader
{
   public CostReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      System.out.println("COST");

      byte[] block1 = new byte[32];
      m_stream.read(block1);
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      String costName = SynchroUtility.getString(m_stream);
      System.out.println("Cost name: " + costName);

      byte[] block2 = new byte[20];
      m_stream.read(block2);
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04E30E9C;
   }
}
