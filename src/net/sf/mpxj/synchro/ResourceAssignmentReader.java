
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

public class ResourceAssignmentReader extends TableReader
{
   public ResourceAssignmentReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      System.out.println("RESOURCE ASSIGNMENT");

      Map<String, Object> map = new HashMap<String, Object>();

      int taskRecordHeader = SynchroUtility.getInt(m_stream);
      if (taskRecordHeader != 0x4623D899)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      byte[] data = new byte[211];
      m_stream.read(data);
      System.out.println(MPPUtility.hexdump(data, true, 16, ""));

      m_rows.add(new MapRow(map));
   }
}
