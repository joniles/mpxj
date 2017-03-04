
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 48;
   }

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, startIndex, offset);
      offset = data.getOffset();

      Calendar cal = Calendar.getInstance();
      byte[][] rawData = data.getData();
      m_data = new Date[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         int value = FastTrackUtility.getShort(rawData[index], 0);
         cal.set(Calendar.HOUR_OF_DAY, (value / 60));
         cal.set(Calendar.MINUTE, (value % 60));
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         m_data[index] = cal.getTime();
      }

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      DateFormat df = new SimpleDateFormat("HH:mm:ss");
      pw.println("  [Data");
      for (Object item : m_data)
      {
         pw.println("    " + df.format((Date) item));
      }
      pw.println("  ]");
   }
}
