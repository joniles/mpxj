
package net.sf.mpxj.sample;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class FastTrackDoubleBlock
{

   public FastTrackDoubleBlock read(byte[] buffer, int startIndex, int length)
   {
      m_header = new FastTrackBlockHeader().read(buffer, startIndex);
      int offset = m_header.getOffset();

      m_skip = new byte[16];
      System.arraycopy(buffer, startIndex + offset, m_skip, 0, m_skip.length);
      offset += m_skip.length;

      m_numberOfItems = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;

      m_data = new double[m_numberOfItems];
      for (int index = 0; index < m_numberOfItems; index++)
      {
         m_data[index] = FastTrackUtility.getDouble(buffer, startIndex + offset);
         offset += 8;
      }

      if (length > offset)
      {
         m_trailer = new byte[length - offset];
         System.arraycopy(buffer, startIndex + offset, m_trailer, 0, m_trailer.length);
      }
      else
      {
         m_trailer = new byte[0];
      }

      return this;
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[DoubleBlock");
      pw.println(m_header.toString());
      pw.print("  Skip: " + FastTrackUtility.hexdump(m_trailer, 0, m_trailer.length, false, 16, ""));
      pw.println("  Number of items: " + m_numberOfItems);
      pw.println("  [Data");
      for (double item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
      pw.print("  Trailer: " + FastTrackUtility.hexdump(m_trailer, 0, m_trailer.length, false, 16, ""));
      pw.println("]");
      pw.flush();
      return (os.toString());

   }

   private FastTrackBlockHeader m_header;
   private int m_numberOfItems;
   private double[] m_data;
   private byte[] m_trailer;
   private byte[] m_skip;
}
