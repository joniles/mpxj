/*
 * file:       TimescaleUtilityTest.java
 * author:     Jon Iles
 * date:       2023-06-22
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

package org.mpxj.junit;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.TimescaleUnits;
import org.mpxj.utility.TimescaleUtility;

/**
 * Tests to exercise the TimescaleUtility class.
 */
public class TimescaleUtilityTest
{
   /**
    * Generate minute timescale segments.
    */
   @Test public void testMinutes()
   {
      TimescaleUtility ts = new TimescaleUtility();

      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00:30", m_basicDateFormat);
      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.MINUTES);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 09:00:00.0 22/06/2023 09:01:00.0", result.get(0));
      assertEquals("22/06/2023 09:01:00.0 22/06/2023 09:02:00.0", result.get(1));
      assertEquals("22/06/2023 09:02:00.0 22/06/2023 09:03:00.0", result.get(2));
      assertEquals("22/06/2023 09:03:00.0 22/06/2023 09:04:00.0", result.get(3));
      assertEquals("22/06/2023 09:04:00.0 22/06/2023 09:05:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("22/06/2023 09:04:30", m_basicDateFormat);
      result = ts.createTimescale(startDate,endDate, TimescaleUnits.MINUTES);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 09:00:00.0 22/06/2023 09:01:00.0", result.get(0));
      assertEquals("22/06/2023 09:01:00.0 22/06/2023 09:02:00.0", result.get(1));
      assertEquals("22/06/2023 09:02:00.0 22/06/2023 09:03:00.0", result.get(2));
      assertEquals("22/06/2023 09:03:00.0 22/06/2023 09:04:00.0", result.get(3));
      assertEquals("22/06/2023 09:04:00.0 22/06/2023 09:05:00.0", result.get(4));
   }

   /**
    * Generate hour timescale segments.
    */
   @Test public void testHours()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:01", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.HOURS);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 09:00:00.0 22/06/2023 10:00:00.0", result.get(0));
      assertEquals("22/06/2023 10:00:00.0 22/06/2023 11:00:00.0", result.get(1));
      assertEquals("22/06/2023 11:00:00.0 22/06/2023 12:00:00.0", result.get(2));
      assertEquals("22/06/2023 12:00:00.0 22/06/2023 13:00:00.0", result.get(3));
      assertEquals("22/06/2023 13:00:00.0 22/06/2023 14:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("22/06/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.HOURS);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 09:00:00.0 22/06/2023 10:00:00.0", result.get(0));
      assertEquals("22/06/2023 10:00:00.0 22/06/2023 11:00:00.0", result.get(1));
      assertEquals("22/06/2023 11:00:00.0 22/06/2023 12:00:00.0", result.get(2));
      assertEquals("22/06/2023 12:00:00.0 22/06/2023 13:00:00.0", result.get(3));
      assertEquals("22/06/2023 13:00:00.0 22/06/2023 14:00:00.0", result.get(4));
   }

   /**
    * Generate day timescale segments.
    */
   @Test public void testDays()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.DAYS);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 00:00:00.0 23/06/2023 00:00:00.0", result.get(0));
      assertEquals("23/06/2023 00:00:00.0 24/06/2023 00:00:00.0", result.get(1));
      assertEquals("24/06/2023 00:00:00.0 25/06/2023 00:00:00.0", result.get(2));
      assertEquals("25/06/2023 00:00:00.0 26/06/2023 00:00:00.0", result.get(3));
      assertEquals("26/06/2023 00:00:00.0 27/06/2023 00:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("26/06/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.DAYS);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 00:00:00.0 23/06/2023 00:00:00.0", result.get(0));
      assertEquals("23/06/2023 00:00:00.0 24/06/2023 00:00:00.0", result.get(1));
      assertEquals("24/06/2023 00:00:00.0 25/06/2023 00:00:00.0", result.get(2));
      assertEquals("25/06/2023 00:00:00.0 26/06/2023 00:00:00.0", result.get(3));
      assertEquals("26/06/2023 00:00:00.0 27/06/2023 00:00:00.0", result.get(4));
   }

   /**
    * Generate week timescale segments.
    */
   @Test public void testWeeks()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      // Week start Sunday
      ts.setWeekStartDay(DayOfWeek.SUNDAY);
      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("25/06/2023 00:00:00.0 02/07/2023 00:00:00.0", result.get(0));
      assertEquals("02/07/2023 00:00:00.0 09/07/2023 00:00:00.0", result.get(1));
      assertEquals("09/07/2023 00:00:00.0 16/07/2023 00:00:00.0", result.get(2));
      assertEquals("16/07/2023 00:00:00.0 23/07/2023 00:00:00.0", result.get(3));
      assertEquals("23/07/2023 00:00:00.0 30/07/2023 00:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("29/07/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("25/06/2023 00:00:00.0 02/07/2023 00:00:00.0", result.get(0));
      assertEquals("02/07/2023 00:00:00.0 09/07/2023 00:00:00.0", result.get(1));
      assertEquals("09/07/2023 00:00:00.0 16/07/2023 00:00:00.0", result.get(2));
      assertEquals("16/07/2023 00:00:00.0 23/07/2023 00:00:00.0", result.get(3));
      assertEquals("23/07/2023 00:00:00.0 30/07/2023 00:00:00.0", result.get(4));

      // Week start Monday
      ts.setWeekStartDay(DayOfWeek.MONDAY);
      result = ts.createTimescale(startDate, 5, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("19/06/2023 00:00:00.0 26/06/2023 00:00:00.0", result.get(0));
      assertEquals("26/06/2023 00:00:00.0 03/07/2023 00:00:00.0", result.get(1));
      assertEquals("03/07/2023 00:00:00.0 10/07/2023 00:00:00.0", result.get(2));
      assertEquals("10/07/2023 00:00:00.0 17/07/2023 00:00:00.0", result.get(3));
      assertEquals("17/07/2023 00:00:00.0 24/07/2023 00:00:00.0", result.get(4));

      endDate = LocalDateTime.parse("23/07/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("19/06/2023 00:00:00.0 26/06/2023 00:00:00.0", result.get(0));
      assertEquals("26/06/2023 00:00:00.0 03/07/2023 00:00:00.0", result.get(1));
      assertEquals("03/07/2023 00:00:00.0 10/07/2023 00:00:00.0", result.get(2));
      assertEquals("10/07/2023 00:00:00.0 17/07/2023 00:00:00.0", result.get(3));
      assertEquals("17/07/2023 00:00:00.0 24/07/2023 00:00:00.0", result.get(4));

      // Week start Tuesday
      ts.setWeekStartDay(DayOfWeek.TUESDAY);
      result = ts.createTimescale(startDate, 5, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("20/06/2023 00:00:00.0 27/06/2023 00:00:00.0", result.get(0));
      assertEquals("27/06/2023 00:00:00.0 04/07/2023 00:00:00.0", result.get(1));
      assertEquals("04/07/2023 00:00:00.0 11/07/2023 00:00:00.0", result.get(2));
      assertEquals("11/07/2023 00:00:00.0 18/07/2023 00:00:00.0", result.get(3));
      assertEquals("18/07/2023 00:00:00.0 25/07/2023 00:00:00.0", result.get(4));

      endDate = LocalDateTime.parse("24/07/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("20/06/2023 00:00:00.0 27/06/2023 00:00:00.0", result.get(0));
      assertEquals("27/06/2023 00:00:00.0 04/07/2023 00:00:00.0", result.get(1));
      assertEquals("04/07/2023 00:00:00.0 11/07/2023 00:00:00.0", result.get(2));
      assertEquals("11/07/2023 00:00:00.0 18/07/2023 00:00:00.0", result.get(3));
      assertEquals("18/07/2023 00:00:00.0 25/07/2023 00:00:00.0", result.get(4));

      // Week start Wednesday
      ts.setWeekStartDay(DayOfWeek.WEDNESDAY);
      result = ts.createTimescale(startDate, 5, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("21/06/2023 00:00:00.0 28/06/2023 00:00:00.0", result.get(0));
      assertEquals("28/06/2023 00:00:00.0 05/07/2023 00:00:00.0", result.get(1));
      assertEquals("05/07/2023 00:00:00.0 12/07/2023 00:00:00.0", result.get(2));
      assertEquals("12/07/2023 00:00:00.0 19/07/2023 00:00:00.0", result.get(3));
      assertEquals("19/07/2023 00:00:00.0 26/07/2023 00:00:00.0", result.get(4));

      endDate = LocalDateTime.parse("25/07/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("21/06/2023 00:00:00.0 28/06/2023 00:00:00.0", result.get(0));
      assertEquals("28/06/2023 00:00:00.0 05/07/2023 00:00:00.0", result.get(1));
      assertEquals("05/07/2023 00:00:00.0 12/07/2023 00:00:00.0", result.get(2));
      assertEquals("12/07/2023 00:00:00.0 19/07/2023 00:00:00.0", result.get(3));
      assertEquals("19/07/2023 00:00:00.0 26/07/2023 00:00:00.0", result.get(4));

      // Week start Thursday
      ts.setWeekStartDay(DayOfWeek.THURSDAY);
      result = ts.createTimescale(startDate, 5, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 00:00:00.0 29/06/2023 00:00:00.0", result.get(0));
      assertEquals("29/06/2023 00:00:00.0 06/07/2023 00:00:00.0", result.get(1));
      assertEquals("06/07/2023 00:00:00.0 13/07/2023 00:00:00.0", result.get(2));
      assertEquals("13/07/2023 00:00:00.0 20/07/2023 00:00:00.0", result.get(3));
      assertEquals("20/07/2023 00:00:00.0 27/07/2023 00:00:00.0", result.get(4));

      endDate = LocalDateTime.parse("26/07/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("22/06/2023 00:00:00.0 29/06/2023 00:00:00.0", result.get(0));
      assertEquals("29/06/2023 00:00:00.0 06/07/2023 00:00:00.0", result.get(1));
      assertEquals("06/07/2023 00:00:00.0 13/07/2023 00:00:00.0", result.get(2));
      assertEquals("13/07/2023 00:00:00.0 20/07/2023 00:00:00.0", result.get(3));
      assertEquals("20/07/2023 00:00:00.0 27/07/2023 00:00:00.0", result.get(4));

      // Week start Friday
      ts.setWeekStartDay(DayOfWeek.FRIDAY);
      result = ts.createTimescale(startDate, 5, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("23/06/2023 00:00:00.0 30/06/2023 00:00:00.0", result.get(0));
      assertEquals("30/06/2023 00:00:00.0 07/07/2023 00:00:00.0", result.get(1));
      assertEquals("07/07/2023 00:00:00.0 14/07/2023 00:00:00.0", result.get(2));
      assertEquals("14/07/2023 00:00:00.0 21/07/2023 00:00:00.0", result.get(3));
      assertEquals("21/07/2023 00:00:00.0 28/07/2023 00:00:00.0", result.get(4));

      endDate = LocalDateTime.parse("27/07/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("23/06/2023 00:00:00.0 30/06/2023 00:00:00.0", result.get(0));
      assertEquals("30/06/2023 00:00:00.0 07/07/2023 00:00:00.0", result.get(1));
      assertEquals("07/07/2023 00:00:00.0 14/07/2023 00:00:00.0", result.get(2));
      assertEquals("14/07/2023 00:00:00.0 21/07/2023 00:00:00.0", result.get(3));
      assertEquals("21/07/2023 00:00:00.0 28/07/2023 00:00:00.0", result.get(4));

      // Week start Saturday
      ts.setWeekStartDay(DayOfWeek.SATURDAY);
      result = ts.createTimescale(startDate, 5, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("24/06/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 08/07/2023 00:00:00.0", result.get(1));
      assertEquals("08/07/2023 00:00:00.0 15/07/2023 00:00:00.0", result.get(2));
      assertEquals("15/07/2023 00:00:00.0 22/07/2023 00:00:00.0", result.get(3));
      assertEquals("22/07/2023 00:00:00.0 29/07/2023 00:00:00.0", result.get(4));

      endDate = LocalDateTime.parse("28/07/2023 13:30", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.WEEKS);
      Assertions.assertEquals(5, result.size());
      assertEquals("24/06/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 08/07/2023 00:00:00.0", result.get(1));
      assertEquals("08/07/2023 00:00:00.0 15/07/2023 00:00:00.0", result.get(2));
      assertEquals("15/07/2023 00:00:00.0 22/07/2023 00:00:00.0", result.get(3));
      assertEquals("22/07/2023 00:00:00.0 29/07/2023 00:00:00.0", result.get(4));
   }

   /**
    * Generate third-of-month timescale segments.
    */
   @Test public void testThirdsOfMonths()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.THIRDS_OF_MONTHS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/06/2023 00:00:00.0 11/06/2023 00:00:00.0", result.get(0));
      assertEquals("11/06/2023 00:00:00.0 21/06/2023 00:00:00.0", result.get(1));
      assertEquals("21/06/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(2));
      assertEquals("01/07/2023 00:00:00.0 11/07/2023 00:00:00.0", result.get(3));
      assertEquals("11/07/2023 00:00:00.0 21/07/2023 00:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("21/07/2023 00:00", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.THIRDS_OF_MONTHS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/06/2023 00:00:00.0 11/06/2023 00:00:00.0", result.get(0));
      assertEquals("11/06/2023 00:00:00.0 21/06/2023 00:00:00.0", result.get(1));
      assertEquals("21/06/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(2));
      assertEquals("01/07/2023 00:00:00.0 11/07/2023 00:00:00.0", result.get(3));
      assertEquals("11/07/2023 00:00:00.0 21/07/2023 00:00:00.0", result.get(4));
   }

   /**
    * Generate month timescale segments.
    */
   @Test public void testMonths()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.MONTHS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/06/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 01/08/2023 00:00:00.0", result.get(1));
      assertEquals("01/08/2023 00:00:00.0 01/09/2023 00:00:00.0", result.get(2));
      assertEquals("01/09/2023 00:00:00.0 01/10/2023 00:00:00.0", result.get(3));
      assertEquals("01/10/2023 00:00:00.0 01/11/2023 00:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("01/11/2023 00:00", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.MONTHS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/06/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 01/08/2023 00:00:00.0", result.get(1));
      assertEquals("01/08/2023 00:00:00.0 01/09/2023 00:00:00.0", result.get(2));
      assertEquals("01/09/2023 00:00:00.0 01/10/2023 00:00:00.0", result.get(3));
      assertEquals("01/10/2023 00:00:00.0 01/11/2023 00:00:00.0", result.get(4));
   }

   /**
    * Generate quarter timescale segments.
    */
   @Test public void testQuarters()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.QUARTERS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/04/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 01/10/2023 00:00:00.0", result.get(1));
      assertEquals("01/10/2023 00:00:00.0 01/01/2024 00:00:00.0", result.get(2));
      assertEquals("01/01/2024 00:00:00.0 01/04/2024 00:00:00.0", result.get(3));
      assertEquals("01/04/2024 00:00:00.0 01/07/2024 00:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("01/07/2024 00:00", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.QUARTERS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/04/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 01/10/2023 00:00:00.0", result.get(1));
      assertEquals("01/10/2023 00:00:00.0 01/01/2024 00:00:00.0", result.get(2));
      assertEquals("01/01/2024 00:00:00.0 01/04/2024 00:00:00.0", result.get(3));
      assertEquals("01/04/2024 00:00:00.0 01/07/2024 00:00:00.0", result.get(4));
   }

   /**
    * Generate half-year timescale segments.
    */
   @Test public void testHalfYears()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.HALF_YEARS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/01/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 01/01/2024 00:00:00.0", result.get(1));
      assertEquals("01/01/2024 00:00:00.0 01/07/2024 00:00:00.0", result.get(2));
      assertEquals("01/07/2024 00:00:00.0 01/01/2025 00:00:00.0", result.get(3));
      assertEquals("01/01/2025 00:00:00.0 01/07/2025 00:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("01/07/2025 00:00", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.HALF_YEARS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/01/2023 00:00:00.0 01/07/2023 00:00:00.0", result.get(0));
      assertEquals("01/07/2023 00:00:00.0 01/01/2024 00:00:00.0", result.get(1));
      assertEquals("01/01/2024 00:00:00.0 01/07/2024 00:00:00.0", result.get(2));
      assertEquals("01/07/2024 00:00:00.0 01/01/2025 00:00:00.0", result.get(3));
      assertEquals("01/01/2025 00:00:00.0 01/07/2025 00:00:00.0", result.get(4));
   }

   /**
    * Generate year timescale segments.
    */
   @Test public void testYears()
   {
      LocalDateTime startDate = LocalDateTime.parse("22/06/2023 09:00", m_basicDateFormat);
      TimescaleUtility ts = new TimescaleUtility();

      List<LocalDateTimeRange> result = ts.createTimescale(startDate, 5, TimescaleUnits.YEARS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/01/2023 00:00:00.0 01/01/2024 00:00:00.0", result.get(0));
      assertEquals("01/01/2024 00:00:00.0 01/01/2025 00:00:00.0", result.get(1));
      assertEquals("01/01/2025 00:00:00.0 01/01/2026 00:00:00.0", result.get(2));
      assertEquals("01/01/2026 00:00:00.0 01/01/2027 00:00:00.0", result.get(3));
      assertEquals("01/01/2027 00:00:00.0 01/01/2028 00:00:00.0", result.get(4));

      LocalDateTime endDate = LocalDateTime.parse("01/01/2028 00:00", m_basicDateFormat);
      result = ts.createTimescale(startDate, endDate, TimescaleUnits.YEARS);
      Assertions.assertEquals(5, result.size());
      assertEquals("01/01/2023 00:00:00.0 01/01/2024 00:00:00.0", result.get(0));
      assertEquals("01/01/2024 00:00:00.0 01/01/2025 00:00:00.0", result.get(1));
      assertEquals("01/01/2025 00:00:00.0 01/01/2026 00:00:00.0", result.get(2));
      assertEquals("01/01/2026 00:00:00.0 01/01/2027 00:00:00.0", result.get(3));
      assertEquals("01/01/2027 00:00:00.0 01/01/2028 00:00:00.0", result.get(4));
   }

   private void assertEquals(String expected, LocalDateTimeRange actual)
   {
      Assertions.assertEquals(expected, formatDateRange(actual));
   }

   private String formatDateRange(LocalDateTimeRange range)
   {
      return m_fullDateFormat.format(range.getStart()) + " " + m_fullDateFormat.format(range.getEnd());
   }

   private final DateTimeFormatter m_basicDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm[:ss]");
   private final DateTimeFormatter m_fullDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.S");
}