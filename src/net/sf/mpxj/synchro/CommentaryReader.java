
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class CommentaryReader extends TableReader
{
   public CommentaryReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      StreamReader stream = new StreamReader(m_stream);

      map.put("TEXT", stream.readString());
      map.put("UNKNOWN1", stream.readBytes(48));
      map.put("TITLE", stream.readString());
      map.put("UNKNOWN2", stream.readBytes(8));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x05972BB6;
   }
}
