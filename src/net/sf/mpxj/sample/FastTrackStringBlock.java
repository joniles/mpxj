
package net.sf.mpxj.sample;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class FastTrackStringBlock
{

   public FastTrackStringBlock read(byte[] buffer, int startIndex, int length)
   {
      m_header = new FastTrackBlockHeader().read(buffer, startIndex);
      int offset = m_header.getOffset();
      int newOffset = FastTrackUtility.skipTo(offset, buffer, startIndex, 0x000F);
      m_skip = new byte[newOffset - offset];
      System.arraycopy(buffer, startIndex + offset, m_skip, 0, m_skip.length);
      offset = newOffset;

      m_numberOfItems = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;

      m_offsetToData = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;

      int[] blockOffsets = new int[m_numberOfItems + 1];
      for (int index = 0; index <= m_numberOfItems; index++)
      {
         int offsetInBlock = FastTrackUtility.getInt(buffer, startIndex + offset);
         blockOffsets[index] = offsetInBlock;
         offset += 4;
      }

      m_dataSize = FastTrackUtility.getInt(buffer, startIndex + offset);
      offset += 4;

      m_data = new String[m_numberOfItems];

      for (int index = 0; index < m_numberOfItems; index++)
      {
         int itemNameLength = blockOffsets[index + 1] - blockOffsets[index];
         m_data[index] = new String(buffer, startIndex + offset, itemNameLength, FastTrackUtility.UTF16LE);
         offset += itemNameLength;
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
      pw.println("[StringBlock");
      pw.println(m_header.toString());
      pw.print("  Skipr: " + FastTrackUtility.hexdump(m_trailer, 0, m_trailer.length, false, 16, ""));
      pw.println("  Number of items: " + m_numberOfItems);
      pw.println("  Offset to data: " + m_offsetToData);
      pw.println("  Data size: " + m_dataSize);
      pw.println("  [Data");
      for (String item : m_data)
      {
         pw.println("    " + item);
      }
      pw.print("  Trailer: " + FastTrackUtility.hexdump(m_trailer, 0, m_trailer.length, false, 16, ""));
      pw.println("  ]");
      pw.println("]");
      pw.flush();
      return (os.toString());

   }

   private FastTrackBlockHeader m_header;
   private int m_numberOfItems;
   private int m_offsetToData;
   private int m_dataSize;
   private String[] m_data;
   private byte[] m_trailer;
   private byte[] m_skip;

}
