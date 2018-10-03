
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class UserFieldReader extends TableReader
{
   public UserFieldReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("UNKNOWN1", stream.readBytes(16));
      map.put("VALUE", stream.readString());
      map.put("UNKNOWN2", stream.readBytes(26));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return 0x440A7BA3;
   }
}
