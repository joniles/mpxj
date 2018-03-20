
package net.sf.mpxj.primavera.suretrak;

import net.sf.mpxj.primavera.common.AbstractColumn;

/**
 * Extract column data from a table.
 */
class RawColumn extends AbstractColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    * @param length maximum byte array length
    */
   public RawColumn(String name, int offset, int length)
   {
      super(name, offset);
      m_length = length;
   }

   @Override public byte[] read(int offset, byte[] data)
   {
      byte[] result = new byte[m_length];
      System.arraycopy(data, offset, result, 0, m_length);
      return result;
   }

   private final int m_length;
}
