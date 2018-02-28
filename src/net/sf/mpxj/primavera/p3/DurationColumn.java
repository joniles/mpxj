
package net.sf.mpxj.primavera.p3;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

public class DurationColumn extends AbstractColumn
{
   public DurationColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Duration read(int offset, byte[] data)
   {
      int result = 0;
      int i = offset + m_offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return Duration.getInstance(result, TimeUnit.DAYS);
   }
}
