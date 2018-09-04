
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

class CompanyReader extends TableReader
{
   public CompanyReader(InputStream stream)
   {
      super(stream);
   }

   @Override protected void readRow() throws IOException
   {
      Map<String, Object> map = new HashMap<String, Object>();

      int recordHeader = SynchroUtility.getInt(m_stream);
      if (recordHeader != 0x0598BFDA)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      System.out.println("COMPANY");

      byte[] block1 = new byte[52];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

      ResourceReader resourceReader = new ResourceReader(m_stream);
      resourceReader.read();

      String companyName = SynchroUtility.getString(m_stream);
      System.out.println("Company name: " + companyName);

      String companyAddress = SynchroUtility.getString(m_stream);
      System.out.println("Company address: " + companyAddress);

      String companyPhone = SynchroUtility.getString(m_stream);
      System.out.println("Company phone: " + companyPhone);

      String companyFax = SynchroUtility.getString(m_stream);
      System.out.println("Company fax: " + companyFax);

      String companyEmail = SynchroUtility.getString(m_stream);
      System.out.println("Company email: " + companyEmail);

      byte[] block2 = new byte[12];
      m_stream.read(block2);
      System.out.println("BLOCK2");
      System.out.println(MPPUtility.hexdump(block2, true, 16, ""));

      String companyUrl = SynchroUtility.getString(m_stream);
      System.out.println("Company url: " + companyUrl);

      byte[] block3 = new byte[8];
      m_stream.read(block3);
      System.out.println("BLOCK3");
      System.out.println(MPPUtility.hexdump(block3, true, 16, ""));

      m_rows.add(new MapRow(map));
   }
}
