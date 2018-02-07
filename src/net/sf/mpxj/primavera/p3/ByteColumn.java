
package net.sf.mpxj.primavera.p3;

public class ByteColumn extends AbstractColumn
{
   public ByteColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Integer read(int offset, byte[] data)
   {
      int result = 0;
      int i = offset + m_offset;
      for (int shiftBy = 0; shiftBy < 8; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return Integer.valueOf(result);
   }
}
