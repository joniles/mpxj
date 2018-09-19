
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class CommentaryReader extends TableReader
{
   public CommentaryReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      System.out.println("COMMENTARY");

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
   }

   @Override protected int rowMagicNumber()
   {
      return 0x05972BB6;
   }
}
