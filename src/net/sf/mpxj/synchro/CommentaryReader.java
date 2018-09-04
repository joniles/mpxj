
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class CommentaryReader extends TableReader
{
   public CommentaryReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      Map<String, Object> map = new HashMap<String, Object>();

      if (SynchroUtility.getInt(m_stream) != 0x05972BB6)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      System.out.println("COMMENTARY");

      byte[] block1 = new byte[32];
      m_stream.read(block1);
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      map.put("TEXT", SynchroUtility.getString(m_stream));
      System.out.println("Text: " + map.get("TEXT"));

      byte[] block2 = new byte[48];
      m_stream.read(block2);
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));

      map.put("TITLE", SynchroUtility.getString(m_stream));
      System.out.println("Title: " + map.get("TITLE"));

      byte[] block3 = new byte[8];
      m_stream.read(block3);
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      m_rows.add(new MapRow(map));
   }
}
