
package net.sf.mpxj.primavera.suretrak;

import java.util.Date;

import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.primavera.p3.AbstractIntColumn;

/**
 * Extract column data from a table.
 */
class DateInHoursColumn extends AbstractIntColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public DateInHoursColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Date read(int offset, byte[] data)
   {
      int hours = readInt(offset, data);
      return hours == 0 ? null : DateHelper.getDateFromLong(EPOCH + (hours * DateHelper.MS_PER_HOUR));
   }

   // 31/12/1979
   private static final long EPOCH = 315446400000l;
}
