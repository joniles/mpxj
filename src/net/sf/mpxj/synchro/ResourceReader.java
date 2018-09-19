
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class ResourceReader extends TableReader
{
   public ResourceReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      Map<String, Object> map = new HashMap<String, Object>();

      if (SynchroUtility.getInt(m_stream) != rowMagicNumber())
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      System.out.println("RESOURCE");

      byte[] block1 = new byte[16];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      map.put("UUID", SynchroUtility.getUUID(m_stream));
      System.out.println("UUID: " + map.get("UUID"));

      map.put("NAME", SynchroUtility.getString(m_stream));
      System.out.println("Resource name: " + map.get("NAME"));

      map.put("DESCRIPTION", SynchroUtility.getString(m_stream));
      System.out.println("Resource description: " + map.get("DESCRIPTION"));

      if (SynchroUtility.getInt(m_stream) != 0)
      {
         map.put("SUPPLY_REFERENCE", SynchroUtility.getString(m_stream));
         System.out.println("Supply reference: " + map.get("SUPPLY_REFERENCE"));
      }

      byte[] block3 = new byte[48];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      ResourceReader resourceReader = new ResourceReader(m_stream);
      resourceReader.read();
      map.put("RESOURCES", resourceReader.getRows());

      byte[] block4 = new byte[20];
      m_stream.read(block4);
      System.out.println("BLOCK4");
      System.out.println(MPPUtility.hexdump(block4, true, 16, ""));

      map.put("URL", SynchroUtility.getString(m_stream));
      System.out.println("Url: " + map.get("URL"));

      if (SynchroUtility.getBoolean(m_stream))
      {
         UserFieldReader userFieldReader = new UserFieldReader(m_stream);
         userFieldReader.read();
         map.put("USER_FIELDS", userFieldReader.getRows());
      }

      map.put("ID", SynchroUtility.getString(m_stream));
      System.out.println("ID:" + map.get("ID"));

      map.put("EMAIL", SynchroUtility.getString(m_stream));
      System.out.println("Email address:" + map.get("EMAIL"));

      // NOTE: this contains nested tables
      UnknownTableReader unknownTable2 = new UnknownTableReader(m_stream, 68);
      unknownTable2.read();

      byte[] block5 = new byte[30];
      m_stream.read(block5);
      System.out.println("BLOCK5");
      System.out.println(MPPUtility.hexdump(block5, true, 16, ""));

      if (SynchroUtility.getBoolean(m_stream))
      {
         CommentaryReader commentaryReader = new CommentaryReader(m_stream);
         commentaryReader.read();
         map.put("COMMENTARY", commentaryReader.getRows());
      }

      byte[] block6 = new byte[48];
      m_stream.read(block6);
      System.out.println("BLOCK6");
      System.out.println(MPPUtility.hexdump(block6, true, 16, ""));

      if (SynchroUtility.getInt(m_stream) != 0)
      {
         byte[] block7 = new byte[76];
         m_stream.read(block7);
         System.out.println("BLOCK7");
         System.out.println(MPPUtility.hexdump(block7, true, 16, ""));
      }

      byte[] block8 = new byte[12];
      m_stream.read(block8);
      System.out.println("BLOCK8");
      System.out.println(MPPUtility.hexdump(block8, true, 16, ""));

      map.put("UNIQUE_ID", SynchroUtility.getInteger(m_stream));
      System.out.println("Unique ID: " + map.get("UNIQUE_ID"));

      m_rows.add(new MapRow(map));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x57A85C31;
   }
}
