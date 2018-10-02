
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class ResourceReader extends TableReader
{
   public ResourceReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      StreamReader stream = new StreamReader(m_stream);
      map.put("NAME", stream.readString());
      map.put("DESCRIPTION", stream.readString());
      Integer supplyReferenceFlag = stream.readInteger();
      map.put("SUPPLY_REFERENCE_FLAG", supplyReferenceFlag);
      if (supplyReferenceFlag.intValue() != 0)
      {
         map.put("SUPPLY_REFERENCE", stream.readString());
      }
      map.put("UNKNOWN1", stream.readBytes(48));
      map.put("RESOURCES", stream.readTable(ResourceReader.class));
      map.put("UNKNOWN2", stream.readBytes(20));
      map.put("URL", stream.readString());
      map.put("USER_FIELDS", stream.readTableConditional(UserFieldReader.class));
      map.put("ID", stream.readString());
      map.put("EMAIL", stream.readString());
      // NOTE: this contains nested tables
      map.put("UNKNOWN3", stream.readUnknownTable(64, 0x701BAFBD));
      map.put("UNKNOWN4", stream.readBytes(30));
      map.put("COMMENTARY", stream.readTableConditional(CommentaryReader.class));
      map.put("UNKNOWN5", stream.readBytes(48));
      Integer unknown6Flag = stream.readInteger();
      map.put("UNKNOWN6_FLAG", unknown6Flag);
      if (unknown6Flag.intValue() != 0)
      {
         map.put("UNKNOWN6", stream.readBytes(76));
      }
      map.put("UNKNOWN7", stream.readBytes(12));
      map.put("UNIQUE_ID", stream.readInteger());
   }

   @Override protected int rowMagicNumber()
   {
      return 0x57A85C31;
   }
}
