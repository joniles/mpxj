
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

public class DurationColumn extends AbstractColumn
{

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      // Skip bytes
      offset += 18;

      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, startIndex, offset);
      offset = data.getOffset();
      int timeUnitValue = FastTrackUtility.getByte(buffer, startIndex + offset);
      TimeUnit unit = FastTrackUtility.getTimeUnit(timeUnitValue);

      byte[][] rawData = data.getData();
      m_data = new Duration[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         double durationValue = FastTrackUtility.getDouble(rawData[index], 0);
         if (timeUnitValue == 10)
         {
            durationValue *= 3;
         }
         m_data[index] = Duration.getInstance(durationValue, unit);
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + item);
      }
      pw.println("  ]");
   }
}
