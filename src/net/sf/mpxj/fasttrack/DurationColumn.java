
package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;

public class DurationColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 18;
   }

   @Override protected int readData(byte[] buffer, int startIndex, int offset)
   {
      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, startIndex, offset);
      offset = data.getOffset();
      m_timeUnitValue = FastTrackUtility.getByte(buffer, startIndex + offset);

      byte[][] rawData = data.getData();
      m_data = new Double[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         Double durationValue = FastTrackUtility.getDouble(rawData[index], 0);
         if (durationValue != null && m_timeUnitValue == 10)
         {
            durationValue = Double.valueOf(durationValue.doubleValue() * 3);
         }
         m_data[index] = durationValue;
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

   public int getTimeUnitValue()
   {
      return m_timeUnitValue;
   }

   private int m_timeUnitValue;
}
