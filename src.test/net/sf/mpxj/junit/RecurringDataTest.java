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
import java.text.ParseException;
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
      testDates(data, "01/11/2017", "03/11/2017", "05/11/2017");

      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "01/11/2017", "02/11/2017", "03/11/2017");

      //
      // Weekly
      //
      data.setRecurrenceType(RecurrenceType.WEEKLY);
      data.setFrequency(Integer.valueOf(2));
      data.setOccurrences(Integer.valueOf(3));
      data.setWeeklyDay(Day.MONDAY, true);
      testDates(data, "06/11/2017", "20/11/2017", "04/12/2017");

      data.setWeeklyDay(Day.MONDAY, false);
      data.setWeeklyDay(Day.TUESDAY, true);
      testDates(data, "07/11/2017", "21/11/2017", "05/12/2017");

      data.setWeeklyDay(Day.MONDAY, true);
      data.setWeeklyDay(Day.TUESDAY, true);
      testDates(data, "06/11/2017", "07/11/2017", "20/11/2017");

      data.setWeeklyDay(Day.MONDAY, true);
      data.setWeeklyDay(Day.TUESDAY, false);
      data.setFrequency(Integer.valueOf(1));
      testDates(data, "06/11/2017", "13/11/2017", "20/11/2017");

      //
      // Monthly relative
      //
      data.setRecurrenceType(RecurrenceType.MONTHLY);
      data.setRelative(true);
      data.setDayNumber(Integer.valueOf(1));
      data.setDayOfWeek(Day.MONDAY);
      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "06/11/2017", "04/12/2017", "01/01/2018");

      data.setStartDate(df.parse("07/11/2017"));
      testDates(data, "04/12/2017", "01/01/2018", "05/02/2018");

      data.setDayNumber(Integer.valueOf(3));
      data.setDayOfWeek(Day.WEDNESDAY);
      data.setFrequency(Integer.valueOf(2));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "15/11/2017", "17/01/2018", "21/03/2018");

      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(5));
      data.setDayOfWeek(Day.MONDAY);
      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "27/11/2017", "25/12/2017", "29/01/2018");

      //
      // Monthly absolute
      //
      data.setRecurrenceType(RecurrenceType.MONTHLY);
      data.setRelative(false);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(11));
      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "11/11/2017", "11/12/2017", "11/01/2018");

      data.setRecurrenceType(RecurrenceType.MONTHLY);
      data.setRelative(false);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(31));
      data.setFrequency(Integer.valueOf(1));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "30/11/2017", "31/12/2017", "31/01/2018");

      data.setRecurrenceType(RecurrenceType.MONTHLY);
      data.setRelative(false);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(31));
      data.setFrequency(Integer.valueOf(2));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "30/11/2017", "31/01/2018", "31/03/2018");

      //
      // Yearly relative
      //
      data.setRecurrenceType(RecurrenceType.YEARLY);
      data.setRelative(true);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(3));
      data.setDayOfWeek(Day.WEDNESDAY);
      data.setMonthNumber(Integer.valueOf(12));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "20/12/2017", "19/12/2018", "18/12/2019");

      data.setRecurrenceType(RecurrenceType.YEARLY);
      data.setRelative(true);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(3));
      data.setDayOfWeek(Day.WEDNESDAY);
      data.setMonthNumber(Integer.valueOf(9));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "19/09/2018", "18/09/2019", "16/09/2020");

      data.setRecurrenceType(RecurrenceType.YEARLY);
      data.setRelative(true);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(5));
      data.setDayOfWeek(Day.WEDNESDAY);
      data.setMonthNumber(Integer.valueOf(6));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "27/06/2018", "26/06/2019", "24/06/2020");

      //
      // Yearly absolute
      //
      data.setRecurrenceType(RecurrenceType.YEARLY);
      data.setRelative(false);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(15));
      data.setMonthNumber(Integer.valueOf(12));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "15/12/2017", "15/12/2018", "15/12/2019");

      data.setRecurrenceType(RecurrenceType.YEARLY);
      data.setRelative(false);
      data.setStartDate(df.parse("01/11/2017"));
      data.setDayNumber(Integer.valueOf(15));
      data.setMonthNumber(Integer.valueOf(06));
      data.setOccurrences(Integer.valueOf(3));
      testDates(data, "15/06/2018", "15/06/2019", "15/06/2020");
   }

   /**
    * Validate the generated dates. Note that the test data sets the number of occurrences,
    * but no finish date. This method swaps those around to ensure that both methods of
    * calculating the finish point of the recurrence produce the same result.
    *
    * @param data recurrence data
    * @param expectedDates expected dates    */
   private void testDates(RecurringData data, String... expectedDates) throws ParseException
   {
      //
      // First validate that the date sequence bounded by occurrences matches the expected data
      //
      Date[] dates = data.getDates();

      assertEquals(expectedDates.length, dates.length);
      for (int index = 0; index < expectedDates.length; index++)
      {
         assertEquals(expectedDates[index], m_df.format(dates[index]));
      }

      //
      // Now validate that the date sequence bounded by the finish date matches the expected data
      //
      Integer occurrences = data.getOccurrences();
      data.setOccurrences(null);
      data.setFinishDate(m_df.parse(expectedDates[expectedDates.length - 1]));

      dates = data.getDates();

      assertEquals(expectedDates.length, dates.length);
      for (int index = 0; index < expectedDates.length; index++)
      {
         assertEquals(expectedDates[index], m_df.format(dates[index]));
      }
      data.setOccurrences(occurrences);
      data.setFinishDate(null);
   }

   private DateFormat m_df = new SimpleDateFormat("dd/MM/yyyy");
}
