
package net.sf.mpxj.primavera.p3;

import java.util.Calendar;
import java.util.Date;

public class DateColumn extends AbstractColumn
{
   public DateColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Date read(int offset, byte[] data)
   {
      Date result = null;

      int intValue = 0;
      int i = offset + m_offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         intValue |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }

      if (intValue != 0)
      {
         String stringValue = Integer.toString(intValue);
         if (stringValue.length() == 10)
         {
            int year = Integer.parseInt(stringValue.substring(0, 4));
            int month = Integer.parseInt(stringValue.substring(4, 6));
            int day = Integer.parseInt(stringValue.substring(6, 8));

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
      }

      return result;
   }
}
