/*
 * file:       TimescaleHelper.java
 * author:     Jon Iles
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

package org.mpxj.common;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.TimescaleUnits;

/**
 * This class contains methods related to creation of timescale data.
 */
public final class TimescaleHelper
{
   /**
    * Given a start date, the number of ranges required, and a timescale unit, this
    * method creates a list of date ranges. For example, if "Months" is
    * selected as the timescale units, this method will create a list of
    * ranges, each one representing a month. The number of entries in the
    * list is determined by the count.
    * <p/>
    * Each of these date ranges is equivalent one of the columns displayed by
    * MS Project when viewing data with a "timescale" at the top of the page.
    * <p/>
    * Note: that the ranges returned by this method are "half open". So for time
    * t to be within a range the following must be true: start <= t < end.
    * <p/>
    * Note: the first range start date will be aligned as follows based on the requested units:
    * MINUTES: first second of the supplied minute,
    * HOURS: first minute of the supplied hour,
    * DAYS: midnight on the supplied start date,
    * WEEKS: midnight on the week start day before the supplied start date,
    * THIRDS_OF_MONTHS: midnight on the first day of the supplied month,
    * MONTHS: midnight on the first day of the supplied month,
    * QUARTERS: midnight on the first day of the quarter containing the supplied date,
    * HALF_YEARS: midnight on the first day of the half year containing the supplied date,
    * YEARS: midnight on the 1st of January in the year containing the supplied date.
    *
    * @param startDate start date
    * @param count number of ranges required
    * @param units units to be represented by each range
    * @return list of ranges
    */
   public final List<LocalDateTimeRange> createTimescale(LocalDateTime startDate, int count, TimescaleUnits units)
   {
      m_startDate = startDate;
      configureStartDateAndIncrements(units);

      LocalDateTime rangeStart = m_startDate;
      List<LocalDateTimeRange> result = new ArrayList<>(count);
      for (int index = 0; index < count; index++)
      {
         LocalDateTime rangeEnd = calculateRangeEnd(rangeStart, index, units);
         result.add(new LocalDateTimeRange(rangeStart, rangeEnd));
         rangeStart = rangeEnd;
      }

      return result;
   }

   /**
    * Given a start date, an end date, and a timescale unit, this
    * method creates a list of date ranges. For example, if "Months" is
    * selected as the timescale units, this method will create a list of
    * ranges, each one representing a month. The number of entries in the
    * will be sufficient to ensure that the supplied end date falls within
    * the final range.
    * <p/>
    * Each of these date ranges is equivalent one of the columns displayed by
    * MS Project when viewing data with a "timescale" at the top of the page.
    * <p/>
    * Note: that the ranges returned by this method are "half open". So for time
    * t to be within a range the following must be true: start <= t < end.
    * <p/>
    * Note: the first range start date will be aligned as follows based on the requested units:
    * MINUTES: first second of the supplied minute,
    * HOURS: first minute of the supplied hour,
    * DAYS: midnight on the supplied start date,
    * WEEKS: midnight on the week start day before the supplied start date,
    * THIRDS_OF_MONTHS: midnight on the first day of the supplied month,
    * MONTHS: midnight on the first day of the supplied month,
    * QUARTERS: midnight on the first day of the quarter containing the supplied date,
    * HALF_YEARS: midnight on the first day of the half year containing the supplied date,
    * YEARS: midnight on the 1st of January in the year containing the supplied date.
    *
    * @param startDate start date
    * @param endDate end date
    * @param units units to be represented by each range
    * @return list of ranges
    */
   public final List<LocalDateTimeRange> createTimescale(LocalDateTime startDate, LocalDateTime endDate, TimescaleUnits units)
   {
      m_startDate = startDate;
      configureStartDateAndIncrements(units);

      LocalDateTime rangeStart = m_startDate;
      LocalDateTime rangeEnd;
      int index = 0;
      List<LocalDateTimeRange> result = new ArrayList<>();

      do
      {
         rangeEnd = calculateRangeEnd(rangeStart, index, units);
         result.add(new LocalDateTimeRange(rangeStart, rangeEnd));
         rangeStart = rangeEnd;
         ++index;
      }
      while (endDate.isAfter(rangeEnd));

      return result;
   }

   /**
    * Set the day on which the week starts. Defaults to Calendar.MONDAY.
    *
    * @param weekStartDay week start day
    */
   public void setWeekStartDay(DayOfWeek weekStartDay)
   {
      if (weekStartDay != null)
      {
         m_weekStartDay = weekStartDay;
      }
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

   /**
    * Determine the start date and increments based on the supplied units.
    *
    * @param units required units
    */
   private void configureStartDateAndIncrements(TimescaleUnits units)
   {
      switch (units)
      {
         case MINUTES:
         {
            m_startDate = LocalDateTime.of(m_startDate.toLocalDate(), LocalTime.of(m_startDate.getHour(), m_startDate.getMinute(), 0));
            m_incrementUnit = ChronoUnit.MINUTES;
            m_incrementAmount = 1;
            break;
         }

         case HOURS:
         {
            m_startDate = LocalDateTime.of(m_startDate.toLocalDate(), LocalTime.of(m_startDate.getHour(), 0, 0));
            m_incrementUnit = ChronoUnit.HOURS;
            m_incrementAmount = 1;
            break;
         }

         case WEEKS:
         {
            m_startDate = LocalDateTime.of(m_startDate.toLocalDate(), LocalTime.MIDNIGHT);
            m_startDate = m_startDate.plusDays(m_weekStartDay.getValue() - m_startDate.getDayOfWeek().getValue());
            m_incrementUnit = ChronoUnit.DAYS;
            m_incrementAmount = 7;
            break;
         }

         case THIRDS_OF_MONTHS:
         {
            m_startDate = LocalDateTime.of(m_startDate.getYear(), m_startDate.getMonth(), 1, 0, 0, 0);
            m_incrementUnit = ChronoUnit.DAYS;
            m_incrementAmount = 10;
            break;
         }

         case MONTHS:
         {
            m_startDate = LocalDateTime.of(m_startDate.getYear(), m_startDate.getMonth(), 1, 0, 0, 0);
            m_incrementUnit = ChronoUnit.MONTHS;
            m_incrementAmount = 1;
            break;
         }

         case QUARTERS:
         {
            int currentMonth = m_startDate.getMonthValue() - 1;
            int currentQuarter = currentMonth / 3;
            int startMonth = (currentQuarter * 3) + 1;
            m_startDate = LocalDateTime.of(m_startDate.getYear(), startMonth, 1, 0, 0, 0);
            m_incrementUnit = ChronoUnit.MONTHS;
            m_incrementAmount = 3;
            break;
         }

         case HALF_YEARS: // align to jan, jun
         {
            int currentMonth = m_startDate.getMonthValue() - 1;
            int currentHalf = currentMonth / 6;
            int startMonth = (currentHalf * 6) + 1;
            m_startDate = LocalDateTime.of(m_startDate.getYear(), startMonth, 1, 0, 0, 0);
            m_incrementUnit = ChronoUnit.MONTHS;
            m_incrementAmount = 6;
            break;
         }

         case YEARS: // align to 1 jan
         {
            m_startDate = LocalDateTime.of(m_startDate.getYear(), 1, 1, 0, 0, 0);
            m_incrementUnit = ChronoUnit.YEARS;
            m_incrementAmount = 1;
            break;
         }

         case DAYS:
         default:
         {
            m_startDate = LocalDateTime.of(m_startDate.toLocalDate(), LocalTime.MIDNIGHT);
            m_incrementUnit = ChronoUnit.DAYS;
            m_incrementAmount = 1;
            break;
         }
      }
   }

   /**
    * Calculate the range end.
    *
    * @param rangeStart range start date
    * @param index index of the current range
    * @param units required units
    * @return range end date
    */
   private LocalDateTime calculateRangeEnd(LocalDateTime rangeStart, int index, TimescaleUnits units)
   {
      if (units == TimescaleUnits.THIRDS_OF_MONTHS && (index + 1) % 3 == 0)
      {
         return LocalDateTime.of(rangeStart.getYear(), rangeStart.getMonth(), 1, 0, 0, 0).plusMonths(1);
      }
      return rangeStart.plus(m_incrementAmount, m_incrementUnit);
   }

   private DayOfWeek m_weekStartDay = DayOfWeek.MONDAY;
   private TemporalUnit m_incrementUnit;
   private int m_incrementAmount;
   private LocalDateTime m_startDate;
}
