
package net.sf.mpxj.primavera.suretrak;

import net.sf.mpxj.primavera.p3.AbstractShortColumn;

/**
 * Extract column data from a table.
 */
public class AnnualColumn extends AbstractShortColumn
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param offset offset within data
    */
   public AnnualColumn(String name, int offset)
   {
      super(name, offset);
   }

   @Override public Boolean read(int offset, byte[] data)
   {
      int days = readShort(offset, data);
      return Boolean.valueOf(days > DateColumn.RECURRING_OFFSET);
   }
}
