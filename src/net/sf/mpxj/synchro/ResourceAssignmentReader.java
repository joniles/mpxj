
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class ResourceAssignmentReader extends TableReader
{
   public ResourceAssignmentReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      System.out.println("RESOURCE ASSIGNMENT");

      byte[] data = new byte[211];
      m_stream.read(data);
      System.out.println(MPPUtility.hexdump(data, true, 16, ""));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x4623D899;
   }
}
