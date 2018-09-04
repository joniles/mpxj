
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class UserFieldReader extends TableReader
{
   public UserFieldReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      Map<String, Object> map = new HashMap<String, Object>();

      int taskRecordHeader = SynchroUtility.getInt(m_stream);
      if (taskRecordHeader != 0x440A7BA3)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      byte[] block1 = new byte[16];
      m_stream.read(block1);
      String value = SynchroUtility.getString(m_stream);
      byte[] block2 = new byte[26];
      m_stream.read(block2);

      System.out.println("USER FIELD");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));
      System.out.println("Value: " + value);
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));
      m_rows.add(new MapRow(map));
   }
}
