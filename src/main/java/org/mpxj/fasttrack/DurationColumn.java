/*
 * file:       DurationColumn.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2017
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.fasttrack;

import java.io.PrintWriter;

/**
 * Column containing duration values.
 */
abstract class DurationColumn extends AbstractColumn
{
   @Override protected int readData(byte[] buffer, int offset)
   {
      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, offset);
      offset = data.getOffset();
      m_timeUnitValue = FastTrackUtility.getByte(buffer, offset);

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

   /**
    * Retrieve the value representing the time unit used for these durations.
    *
    * @return time unit value
    */
   public int getTimeUnitValue()
   {
      return m_timeUnitValue;
   }

   private int m_timeUnitValue;
}
