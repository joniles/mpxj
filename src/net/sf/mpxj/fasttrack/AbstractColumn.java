
package net.sf.mpxj.fasttrack;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

abstract class AbstractColumn implements FastTrackColumn
{
   public void read(byte[] buffer, int startIndex, int length)
   {
      m_header = new BlockHeader().read(buffer, startIndex, postHeaderSkipBytes());
      int offset = readData(buffer, startIndex, m_header.getOffset());

      if (length > offset)
      {
         m_trailer = new byte[length - offset];
         System.arraycopy(buffer, startIndex + offset, m_trailer, 0, m_trailer.length);
      }
      else
      {
         m_trailer = new byte[0];
      }
   }

   protected abstract int postHeaderSkipBytes();

   protected abstract int readData(byte[] buffer, int startIndex, int offset);

   protected abstract void dumpData(PrintWriter pw);

   @Override public String getName()
   {
      return m_header.getName();
   }

   @Override public int getIndexNumber()
   {
      return m_header.getIndexNumber();
   }

   @Override public int getFlags()
   {
      return m_header.getFlags();
   }

   @Override public Object[] getData()
   {
      return m_data;
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[" + getClass().getSimpleName());
      pw.println(m_header.toString());
      dumpData(pw);
      pw.print("  Trailer: " + FastTrackUtility.hexdump(m_trailer, 0, m_trailer.length, false, 16, ""));
      pw.println("]");
      pw.flush();
      return (os.toString());

   }

   private BlockHeader m_header;
   private byte[] m_trailer;
   protected Object[] m_data;
}
