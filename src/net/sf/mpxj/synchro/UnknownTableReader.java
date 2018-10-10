
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class UnknownTableReader extends TableReader
{
   public UnknownTableReader(InputStream stream)
   {
      this(stream, 0, 0);
   }

   public UnknownTableReader(InputStream stream, int rowSize, int rowMagicNumber)
   {
      super(stream);
      m_rowSize = rowSize;
      m_rowMagicNumber = rowMagicNumber;
   }

   @Override protected void readRow(StreamReader stream, Map<String, Object> map) throws IOException
   {
      if (m_rowSize == 0)
      {
         //         System.out.println("REMAINDER");
         //         byte[] remainder = new byte[m_stream.available()];
         //         m_stream.read(remainder);
         //         System.out.println(ByteArrayHelper.hexdump(remainder, true, 16, ""));
         throw new IllegalArgumentException("Unexpected records!");
      }

      map.put("UNKNOWN1", stream.readBytes(m_rowSize));
   }

   @Override protected boolean hasUUID()
   {
      return false;
   }

   @Override protected int rowMagicNumber()
   {
      return m_rowMagicNumber;
   }

   private final int m_rowSize;
   private final int m_rowMagicNumber;
}
