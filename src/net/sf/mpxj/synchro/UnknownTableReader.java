
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;

import net.sf.mpxj.mpp.MPPUtility;

class UnknownTableReader extends TableReader
{
   public UnknownTableReader(InputStream stream)
   {
      this(stream, 0);
   }

   public UnknownTableReader(InputStream stream, int rowSize)
   {
      super(stream);
      m_rowSize = rowSize;
   }

   @Override protected void readRow() throws IOException
   {
      if (m_rowSize == 0)
      {
         System.out.println("REMAINDER");
         byte[] remainder = new byte[m_stream.available()];
         m_stream.read(remainder);
         System.out.println(MPPUtility.hexdump(remainder, true, 16, ""));

         throw new IllegalArgumentException("Unexpected records!");
      }

      byte[] block1 = new byte[m_rowSize];
      m_stream.read(block1);
      System.out.println("BLOCK1");
      System.out.println(MPPUtility.hexdump(block1, true, 16, ""));
   }

   @Override protected int rowMagicNumber()
   {
      return 0;
   }
   private final int m_rowSize;
}
