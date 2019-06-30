
package net.sf.mpxj.sdef;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

public class DurationField extends IntegerField
{
   public DurationField(String name, int length)
   {
      super(name, length);
   }

   @Override public Object read(String line, int offset)
   {
      Object result;
      Integer value = ((Integer)super.read(line, offset));
      if (value == null)
      {
         result = null;
      }
      else
      {
         result = Duration.getInstance(value.intValue(), TimeUnit.DAYS);
      }
      return result;
   }
}
