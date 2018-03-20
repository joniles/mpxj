
package net.sf.mpxj.primavera.suretrak;

import net.sf.mpxj.primavera.common.AbstractWbsFormat;
import net.sf.mpxj.primavera.common.MapRow;

/**
 * Reads the WBS format definition from a P3 database, and allows
 * that format to be applied to WBS values.
 */
public class SureTrakWbsFormat extends AbstractWbsFormat
{
   /**
    * Constructor. Reads the format definition.
    *
    * @param row database row containing WBS format
    */
   public SureTrakWbsFormat(MapRow row)
   {
      byte[] data = row.getRaw("DATA");
      int index = 1;
      while (true)
      {
         Integer length = Integer.valueOf(data[index++]);
         if (length.intValue() == 0)
         {
            break;
         }
         String separator = new String(data, index++, 1);

         m_lengths.add(length);
         m_separators.add(separator);
      }
   }
}
