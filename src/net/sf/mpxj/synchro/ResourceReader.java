
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

      int recordHeader = SynchroUtility.getInt(m_stream);
      if (recordHeader != 0x57A85C31)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      System.out.println("RESOURCE");

      byte[] block1 = new byte[32];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      String resourceName = SynchroUtility.getString(m_stream);
      System.out.println("Resource name: " + resourceName);

      String resourceDescription = SynchroUtility.getString(m_stream);
      System.out.println("Resource description: " + resourceDescription);

      int supplyReferenceFlag = SynchroUtility.getInt(m_stream);
      if (supplyReferenceFlag != 0)
      {
         String supplyReference = SynchroUtility.getString(m_stream);
         System.out.println("Supply reference: " + supplyReference);
      }

      byte[] block3 = new byte[48];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      UnknownTableReader unknownTable1 = new UnknownTableReader(m_stream);
      unknownTable1.read();

      byte[] block4 = new byte[20];
      m_stream.read(block4);
      System.out.println("BLOCK4");
      System.out.println(MPPUtility.hexdump(block4, true, 16, ""));

      String url = SynchroUtility.getString(m_stream);
      System.out.println("Url: " + url);

      if (SynchroUtility.getBoolean(m_stream))
      {
         UserFieldReader userFieldReader = new UserFieldReader(m_stream);
         userFieldReader.read();
      }

      String unknownString = SynchroUtility.getString(m_stream);
      System.out.println("Unknown string:" + unknownString);

      String emailAddress = SynchroUtility.getString(m_stream);
      System.out.println("Email address:" + emailAddress);

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
      }

      byte[] block6 = new byte[48];
      m_stream.read(block6);
      System.out.println("BLOCK6");
      System.out.println(MPPUtility.hexdump(block6, true, 16, ""));

      int block7Flag = SynchroUtility.getInt(m_stream);
      if (block7Flag != 0)
      {
         byte[] block7 = new byte[76];
         m_stream.read(block7);
         System.out.println("BLOCK7");
         System.out.println(MPPUtility.hexdump(block7, true, 16, ""));
      }

      byte[] block8 = new byte[16];
      m_stream.read(block8);
      System.out.println("BLOCK8");
      System.out.println(MPPUtility.hexdump(block8, true, 16, ""));

      map.put("NAME", resourceName);
      m_rows.add(new MapRow(map));
   }
}
