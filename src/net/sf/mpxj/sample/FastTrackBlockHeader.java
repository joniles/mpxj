
package net.sf.mpxj.sample;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class FastTrackBlockHeader
{

   public FastTrackBlockHeader read(byte[] buffer, int startIndex)
   {
      m_offset = 0;

      System.arraycopy(buffer, startIndex + m_offset, m_header, 0, 8);
      m_offset += 8;

      int nameLength = FastTrackUtility.getInt(buffer, startIndex + m_offset);
      m_offset += 4;

      if (nameLength > 0 && nameLength < 255)
      {
         m_name = new String(buffer, startIndex + m_offset, nameLength, FastTrackUtility.UTF16LE);
         m_offset += nameLength;

         m_indexNumber = FastTrackUtility.getInt(buffer, startIndex + m_offset);
         m_offset += 4;
      }

      return this;
   }

   public int getOffset()
   {
      return m_offset;
   }

   public String getName()
   {
      return m_name;
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[BlockHeader");
      pw.print("  Header: " + FastTrackUtility.hexdump(m_header, 0, m_header.length, false, 16, ""));
      pw.println("  Name: " + m_name);
      pw.println("  Index: " + m_indexNumber);
      pw.println("]");
      pw.flush();
      return (os.toString());

   }

   private byte[] m_header = new byte[8];
   private int m_offset;
   private String m_name;
   private int m_indexNumber;
}
