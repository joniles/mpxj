
package net.sf.mpxj.primavera.suretrak;

import java.util.Date;

import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.primavera.p3.AbstractShortColumn;

/**
 * Extract column data from a table.
 */
public class DateColumn extends AbstractShortColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public DateColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Date read(int offset, byte[] data)
   {
      int days = readShort(offset, data);
      if (days > RECURRING_OFFSET)
      {
         days -= RECURRING_OFFSET;
      }

      return DateHelper.getDateFromLong(EPOCH + (days * DateHelper.MS_PER_DAY));
   }

   static final int RECURRING_OFFSET = 25463;

   // 31/12/1979
   private static final long EPOCH = 315446400000l;
}
