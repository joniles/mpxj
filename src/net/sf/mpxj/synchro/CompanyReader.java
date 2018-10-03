
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class CompanyReader extends TableReader
{
   public CompanyReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      map.put("UNKNOWN1", stream.readBytes(20));
      map.put("RESOURCES", stream.readTable(ResourceReader.class));
      map.put("NAME", stream.readString());
      map.put("ADDRESS", stream.readString());
      map.put("PHONE", stream.readString());
      map.put("FAX", stream.readString());
      map.put("EMAIL", stream.readString());
      map.put("UNKNOWN2", stream.readBytes(12));
      map.put("URL", stream.readString());
      map.put("UNKNOWN3", stream.readBytes(8));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x0598BFDA;
   }
}
