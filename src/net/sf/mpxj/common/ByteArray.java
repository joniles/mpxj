
package net.sf.mpxj.common;

import net.sf.mpxj.mpp.MPPUtility;

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
      return MPPUtility.hexdump(m_data, false);
   }

   private final byte[] m_data;
}
