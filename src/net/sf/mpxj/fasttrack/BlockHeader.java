
package net.sf.mpxj.fasttrack;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class BlockHeader
{

   public BlockHeader read(byte[] buffer, int startIndex, int skipBytes)
   {
      m_offset = 0;

      System.arraycopy(buffer, startIndex + m_offset, m_header, 0, 8);
      m_offset += 8;

      int nameLength = FastTrackUtility.getInt(buffer, startIndex + m_offset);
      m_offset += 4;

      if (nameLength < 1 || nameLength > 255)
      {
         throw new UnexpectedStructureException();
      }

      m_name = new String(buffer, startIndex + m_offset, nameLength, FastTrackUtility.UTF16LE);
      m_offset += nameLength;

      m_indexNumber = FastTrackUtility.getShort(buffer, startIndex + m_offset);
      m_offset += 2;

      m_flags = FastTrackUtility.getShort(buffer, startIndex + m_offset);
      m_offset += 2;

      m_skip = new byte[skipBytes];
      System.arraycopy(buffer, startIndex + m_offset, m_skip, 0, skipBytes);
      m_offset += skipBytes;

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

   public int getIndexNumber()
   {
      return m_indexNumber;
   }

   public int getFlags()
   {
      return m_flags;
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("  [BlockHeader");
      pw.print("    Header: " + FastTrackUtility.hexdump(m_header, 0, m_header.length, false, 16, ""));
      pw.println("    Name: " + m_name);
      pw.println("    Index: " + m_indexNumber);
      pw.println("    Flags: " + m_flags);
      pw.print("    Skip:\n" + FastTrackUtility.hexdump(m_skip, 0, m_skip.length, false, 16, "      "));
      pw.println("  ]");
      pw.flush();
      return (os.toString());

   }

   private byte[] m_header = new byte[8];
   private byte[] m_skip;
   private int m_offset;
   private String m_name;
   private int m_indexNumber;
   private int m_flags;
}
