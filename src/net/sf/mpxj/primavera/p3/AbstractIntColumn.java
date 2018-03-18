
package net.sf.mpxj.primavera.p3;

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
