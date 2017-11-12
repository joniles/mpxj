/*
 * file:       RecurringDataTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       2017/11/11
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

package net.sf.mpxj.junit;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import net.sf.mpxj.Day;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringData;

/**
 * Test recurring data functionality.
 */
public class RecurringDataTest
{
   /**
    * Test the getEntryByDate method.
    */
   @Test public void testGetDates() throws Exception
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      RecurringData data = new RecurringData();

      //
      // Daily
      //
      data.setRecurrenceType(RecurrenceType.DAILY);
      data.setStartDate(df.parse("01/11/2017"));
      data.setFrequency(Integer.valueOf(2));
      data.setOccurrences(Integer.valueOf(3));

      Date[] dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("01/11/2017", df.format(dates[0]));
      assertEquals("03/11/2017", df.format(dates[1]));
      assertEquals("05/11/2017", df.format(dates[2]));

      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("01/11/2017", df.format(dates[0]));
      assertEquals("02/11/2017", df.format(dates[1]));
      assertEquals("03/11/2017", df.format(dates[2]));

      //
      // Weekly
      //
      data.setRecurrenceType(RecurrenceType.WEEKLY);
      data.setFrequency(Integer.valueOf(2));
      data.setOccurrences(Integer.valueOf(3));
      data.setWeeklyDay(Day.MONDAY, true);

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("06/11/2017", df.format(dates[0]));
      assertEquals("20/11/2017", df.format(dates[1]));
      assertEquals("04/12/2017", df.format(dates[2]));

      data.setWeeklyDay(Day.MONDAY, false);
      data.setWeeklyDay(Day.TUESDAY, true);

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("07/11/2017", df.format(dates[0]));
      assertEquals("21/11/2017", df.format(dates[1]));
      assertEquals("05/12/2017", df.format(dates[2]));

      data.setWeeklyDay(Day.MONDAY, true);
      data.setWeeklyDay(Day.TUESDAY, true);

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("06/11/2017", df.format(dates[0]));
      assertEquals("07/11/2017", df.format(dates[1]));
      assertEquals("20/11/2017", df.format(dates[2]));

      data.setWeeklyDay(Day.MONDAY, true);
      data.setWeeklyDay(Day.TUESDAY, false);
      data.setFrequency(Integer.valueOf(1));

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("06/11/2017", df.format(dates[0]));
      assertEquals("13/11/2017", df.format(dates[1]));
      assertEquals("20/11/2017", df.format(dates[2]));

      //
      // Monthly relative
      //
      data.setRecurrenceType(RecurrenceType.MONTHLY);
      data.setRelative(true);
      data.setDayNumber(Integer.valueOf(1));
      data.setDayOfWeek(Day.MONDAY);
      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("06/11/2017", df.format(dates[0]));
      assertEquals("04/12/2017", df.format(dates[1]));
      assertEquals("01/01/2018", df.format(dates[2]));

      data.setStartDate(df.parse("07/11/2017"));

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("04/12/2017", df.format(dates[0]));
      assertEquals("01/01/2018", df.format(dates[1]));
      assertEquals("05/02/2018", df.format(dates[2]));

      data.setDayNumber(Integer.valueOf(3));
      data.setDayOfWeek(Day.WEDNESDAY);
      data.setFrequency(Integer.valueOf(2));
      data.setOccurrences(Integer.valueOf(3));

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("15/11/2017", df.format(dates[0]));
      assertEquals("17/01/2018", df.format(dates[1]));
      assertEquals("21/03/2018", df.format(dates[2]));

      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(5));
      data.setDayOfWeek(Day.MONDAY);
      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));

      dates = data.getDates();
      assertEquals(3, dates.length);
      assertEquals("27/11/2017", df.format(dates[0]));
      assertEquals("25/12/2017", df.format(dates[1]));
      assertEquals("29/01/2018", df.format(dates[2]));

   }
}
