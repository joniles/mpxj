
package net.sf.mpxj.primavera.p3;

import java.util.Calendar;
import java.util.Date;

public class BtrieveDateColumn extends AbstractColumn
{
   public BtrieveDateColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Date read(int offset, byte[] data)
   {
      Date result = null;

      int i = offset + m_offset;

      int day = data[i];
      int month = data[i + 1];
      int year = (data[i + 2] & 0xff) | ((data[i + 3] & 0xff) << 8);
      if (year != 0)
      {
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.YEAR, year);
         cal.set(Calendar.MONTH, month - 1);
         cal.set(Calendar.DAY_OF_MONTH, day);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);

         result = cal.getTime();
      }

      return result;
   }
}
