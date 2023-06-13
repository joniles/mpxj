/*
 * file:       DateColumn.java
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

package net.sf.mpxj.fasttrack;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import net.sf.mpxj.common.DateHelper;

/**
 * Column containing dates.
 */
class DateColumn extends AbstractColumn
{
   @Override protected int postHeaderSkipBytes()
   {
      return 0;
   }

   @Override protected int readData(byte[] buffer, int offset)
   {
      // Unknown
      offset += 6;

      // Structure flags? See StringColumn...
      offset += 4;

      // Originally I though that there was a fixed 48 byte offset from the end of
      // the header to the start of the data. In fact there appears to be an optional
      // block of string data after the header, but before the binary version of the dates.
      // The string dates in this optional block don't appear to match the actual dates, so
      // we skip past them. We're looking for a byte pattern which we expect at the start
      // of the block of binary dates... it's fragile, but the best we can do at the moment.
      offset = FastTrackUtility.skipToNextMatchingShort(buffer, offset, 0x000A) - 2;

      FixedSizeItemsBlock data = new FixedSizeItemsBlock().read(buffer, offset);
      offset = data.getOffset();

      Calendar cal = DateHelper.popCalendar();
      byte[][] rawData = data.getData();
      m_data = new Date[rawData.length];
      for (int index = 0; index < rawData.length; index++)
      {
         byte[] rawValue = rawData[index];
         if (rawValue != null && rawValue.length >= 4)
         {
            int value = FastTrackUtility.getInt(rawValue, 0);
            if (value > 0)
            {
               cal.setTimeInMillis(DATE_EPOCH);
               cal.add(Calendar.DAY_OF_YEAR, value);
               int year = cal.get(Calendar.YEAR);
               // Sanity test: ignore dates with obviously incorrect years
               if (year > 1980 && year < 2100)
               {
                  m_data[index] = cal.getTime();
               }
            }
         }
      }
      DateHelper.pushCalendar(cal);

      return offset;
   }

   @Override protected void dumpData(PrintWriter pw)
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      pw.println("  [Data");
      for (Object item : m_data)
      {
         Object value = item == null ? "" : df.format((Date) item);
         pw.println("    " + value);
      }
      pw.println("  ]");
   }

   /**
    * 31/12/1979 00:00.
    */
   private static final long DATE_EPOCH = 315446400000L;
}
