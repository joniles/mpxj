
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class CompanyReader extends TableReader
{
   public CompanyReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow(Map<String, Object> map) throws IOException
   {
      System.out.println("COMPANY");

      byte[] block1 = new byte[20];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      ResourceReader resourceReader = new ResourceReader(m_stream);
      resourceReader.read();
      map.put("RESOURCES", resourceReader.getRows());

      map.put("NAME", SynchroUtility.getString(m_stream));
      System.out.println("Company name: " + map.get("NAME"));

      map.put("ADDRESS", SynchroUtility.getString(m_stream));
      System.out.println("Company address: " + map.get("ADDRESS"));

      map.put("PHONE", SynchroUtility.getString(m_stream));
      System.out.println("Company phone: " + map.get("PHONE"));

      map.put("FAX", SynchroUtility.getString(m_stream));
      System.out.println("Company fax: " + map.get("FAX"));

      map.put("EMAIL", SynchroUtility.getString(m_stream));
      System.out.println("Company email: " + map.get("EMAIL"));

      byte[] block2 = new byte[12];
      m_stream.read(block2);
      System.out.println("BLOCK2");
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));

      map.put("URL", SynchroUtility.getString(m_stream));
      System.out.println("Company url: " + map.get("URL"));

      byte[] block3 = new byte[8];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));
   }

   @Override protected int rowMagicNumber()
   {
      return 0x0598BFDA;
   }
}
