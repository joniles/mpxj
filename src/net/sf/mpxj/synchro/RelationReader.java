
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class RelationReader extends TableReader
{
   public RelationReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      Map<String, Object> map = new HashMap<String, Object>();

      byte[] block1 = new byte[92];
      m_stream.read(block1);
      System.out.println("RELATION");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      m_rows.add(new MapRow(map));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x04E7E3D1;
   }
}
