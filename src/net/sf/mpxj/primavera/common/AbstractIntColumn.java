
package net.sf.mpxj.primavera.common;

/**
 * Extract column data from a table.
 */
public abstract class AbstractIntColumn extends AbstractColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public AbstractIntColumn(String name, int offset)
   {
      super(name, offset);
   }

   /**
    * Read a four byte integer from the data.
    *
    * @param offset current offset into data block
    * @param data data block
    * @return int value
    */
   public int readInt(int offset, byte[] data)
   {
      int result = 0;
      int i = offset + m_offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }
}
