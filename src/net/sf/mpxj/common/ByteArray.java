
package net.sf.mpxj.common;

public class ByteArray
{
   public ByteArray(byte[] data)
   {
      m_data = data;
   }

   public byte[] getData()
   {
      return m_data;
   }

   @Override public String toString()
   {
      return ByteArrayHelper.hexdump(m_data, false);
   }

   private final byte[] m_data;
}
