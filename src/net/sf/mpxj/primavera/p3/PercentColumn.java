
package net.sf.mpxj.primavera.p3;

public class PercentColumn extends AbstractColumn
{
   public PercentColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Double read(int offset, byte[] data)
   {
      int result = 0;
      int i = offset + m_offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return Double.valueOf(result / 10.0);
   }
}
