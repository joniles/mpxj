/*
 * file:       TimescaleUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       2011-02-12
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

package org.mpxj.utility;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.mpp.TimescaleUnits;

/**
 * This class contains methods related to creation of timescale data.
 */
public final class TimescaleUtility
{
   /**
    * Given a start date, a timescale unit, and a number of segments, this
    * method creates an array of date ranges. For example, if "Months" is
    * selected as the timescale units, this method will create an array of
    * ranges, each one representing a month. The number of entries in the
    * array is determined by the segment count.
    *
    * Each of these date ranges is equivalent one of the columns displayed by
    * MS Project when viewing data with a "timescale" at the top of the page.
    *
    * @param startDate start date
    * @param segmentUnit units to be represented by each segment (column)
    * @param segmentCount number of segments (columns) required
    * @return list of date ranges
    */
   public final ArrayList<LocalDateTimeRange> createTimescale(LocalDateTime startDate, TimescaleUnits segmentUnit, int segmentCount)
   {
      ArrayList<LocalDateTimeRange> result = new ArrayList<>(segmentCount);

      LocalDateTime cal = LocalDateTime.of(startDate.toLocalDate(), LocalTime.MIDNIGHT);

      TemporalUnit calendarIncrementUnit;
      int calendarIncrementAmount;

      switch (segmentUnit)
      {
         case MINUTES:
         {
            calendarIncrementUnit = ChronoUnit.MINUTES;
            calendarIncrementAmount = 1;
            break;
         }

         case HOURS:
         {
            calendarIncrementUnit = ChronoUnit.HOURS;
            calendarIncrementAmount = 1;
            break;
         }

         case WEEKS:
         {
            cal = cal.plusDays(m_weekStartDay.getValue() - cal.getDayOfWeek().getValue());
            calendarIncrementUnit = ChronoUnit.DAYS;
            calendarIncrementAmount = 7;
            break;
         }

         case THIRDS_OF_MONTHS:
         {
            cal = LocalDateTime.of(cal.getYear(), cal.getMonth(), 1, 0, 0, 0);
            calendarIncrementUnit = ChronoUnit.DAYS;
            calendarIncrementAmount = 10;
            break;
         }

         case MONTHS:
         {
            cal = LocalDateTime.of(cal.getYear(), cal.getMonth(), 1, 0, 0, 0);
            calendarIncrementUnit = ChronoUnit.MONTHS;
            calendarIncrementAmount = 1;
            break;
         }

         case QUARTERS:
         {
            int currentMonth = cal.getMonthValue() - 1;
            int currentQuarter = currentMonth / 3;
            int startMonth = (currentQuarter * 3) + 1;
            cal = LocalDateTime.of(cal.getYear(), startMonth, 1, 0, 0, 0);
            calendarIncrementUnit = ChronoUnit.MONTHS;
            calendarIncrementAmount = 3;
            break;
         }

         case HALF_YEARS: // align to jan, jun
         {
            int currentMonth = cal.getMonthValue() - 1;
            int currentHalf = currentMonth / 6;
            int startMonth = (currentHalf * 6) + 1;
            cal = LocalDateTime.of(cal.getYear(), startMonth, 1, 0, 0, 0);
            calendarIncrementUnit = ChronoUnit.MONTHS;
            calendarIncrementAmount = 6;
            break;
         }

         case YEARS: // align to 1 jan
         {
            cal = LocalDateTime.of(cal.getYear(), 1, 1, 0, 0, 0);
            calendarIncrementUnit = ChronoUnit.YEARS;
            calendarIncrementAmount = 1;
            break;
         }

         case DAYS:
         default:
         {
            calendarIncrementUnit = ChronoUnit.DAYS;
            calendarIncrementAmount = 1;
            break;
         }
      }

      for (int loop = 0; loop < segmentCount; loop++)
      {
         LocalDateTime rangeStart = cal;

         if (segmentUnit == TimescaleUnits.THIRDS_OF_MONTHS && (loop + 1) % 3 == 0)
         {
            cal = LocalDateTime.of(cal.getYear(), cal.getMonth(), 1, 0, 0, 0).plusMonths(1);
         }
         else
         {
            cal = cal.plus(calendarIncrementAmount, calendarIncrementUnit);
         }

         LocalDateTime rangeEnd = cal.minus(1, ChronoUnit.MILLIS);
         result.add(new LocalDateTimeRange(rangeStart, rangeEnd));
      }

      return result;
   }

   /**
    * Set the day on which the week starts. Defaults to Calendar.MONDAY.
    *
    * @param weekStartDay week start day
    */
   public void setWeekStartDay(DayOfWeek weekStartDay)
   {
      m_weekStartDay = weekStartDay;
   }

   /**
    * Retrieves the day on which the week starts.
    *
    * @return week start day
    */
   public DayOfWeek getWeekStartDay()
   {
      return m_weekStartDay;
   }

   private DayOfWeek m_weekStartDay = DayOfWeek.MONDAY;
}
