/*
 * file:       TimescaleUtility.java
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

package org.mpxj.utility;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.mpp.TimescaleUnits;

/**
 * This class contains methods related to creation of timescale data.
 */
public final class TimescaleUtility
{
   /**
    * Given a start date, a timescale unit, and a number of ranges, this
    * method creates an array of date ranges. For example, if "Months" is
    * selected as the timescale units, this method will create an array of
    * ranges, each one representing a month. The number of entries in the
    * array is determined by the segment count.
    * <p/>
    * Each of these date ranges is equivalent one of the columns displayed by
    * MS Project when viewing data with a "timescale" at the top of the page.
    * <p/>
    * Note that the ranges returned by this method are "half open". So for time
    * t to be within a range the following must be true: start <= t < end.
    *
    * @param startDate start date
    * @param count number of ranges (columns) required
    * @param units units to be represented by each range (column)
    * @return list of date ranges
    */
   public final List<LocalDateTimeRange> createTimescale(LocalDateTime startDate, int count, TimescaleUnits units)
   {
      configureStartDate(startDate, units);
      configureIncrements(units);

      LocalDateTime rangeStart = m_startDate;
      List<LocalDateTimeRange> result = new ArrayList<>(count);
      for (int index = 0; index < count; index++)
      {
         LocalDateTime rangeEnd;
         if (units == TimescaleUnits.THIRDS_OF_MONTHS && (index + 1) % 3 == 0)
         {
            rangeEnd = LocalDateTime.of(rangeStart.getYear(), rangeStart.getMonth(), 1, 0, 0, 0).plusMonths(1);
         }
         else
         {
            rangeEnd = rangeStart.plus(m_incrementAmount, m_incrementUnit);
         }

         result.add(new LocalDateTimeRange(rangeStart, rangeEnd));
         rangeStart = rangeEnd;
      }

      return result;
   }

   public final List<LocalDateTimeRange> createTimescale(LocalDateTime startDate, LocalDateTime endDate, TimescaleUnits units)
   {
      configureStartDate(startDate, units);
      configureIncrements(units);

      LocalDateTime rangeStart = m_startDate;
      LocalDateTime rangeEnd;
      int index = 0;
      List<LocalDateTimeRange> result = new ArrayList<>();

      do
      {
         if (units == TimescaleUnits.THIRDS_OF_MONTHS && (index + 1) % 3 == 0)
         {
            rangeEnd = LocalDateTime.of(rangeStart.getYear(), rangeStart.getMonth(), 1, 0, 0, 0).plusMonths(1);
         }
         else
         {
            rangeEnd = rangeStart.plus(m_incrementAmount, m_incrementUnit);
         }

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

   private void configureStartDate(LocalDateTime startDate, TimescaleUnits units)
   {
      switch (units)
      {
         case MINUTES:
         {
            // Align minutes to the nearest minute
            m_startDate =  LocalDateTime.of(startDate.toLocalDate(), LocalTime.of(startDate.getHour(), startDate.getMinute(), 0));
            break;
         }

         case HOURS:
         {
            // Align hours to the nearest hour
            m_startDate =  LocalDateTime.of(startDate.toLocalDate(), LocalTime.of(startDate.getHour(), 0, 0));
            break;
         }

         default:
         {
            // Everything else is aligned to the start of the day
            m_startDate = LocalDateTime.of(startDate.toLocalDate(), LocalTime.MIDNIGHT);
            break;
         }
      }
   }

   private void configureIncrements(TimescaleUnits units)
   {
      switch (units)
      {
         case MINUTES:
         {
            m_incrementUnit = ChronoUnit.MINUTES;
            m_incrementAmount = 1;
            break;
         }

         case HOURS:
         {
            m_incrementUnit = ChronoUnit.HOURS;
            m_incrementAmount = 1;
            break;
         }

         case WEEKS:
         {
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
            m_incrementUnit = ChronoUnit.DAYS;
            m_incrementAmount = 1;
            break;
         }
      }
   }

   private DayOfWeek m_weekStartDay = DayOfWeek.MONDAY;
   private TemporalUnit m_incrementUnit;
   private int m_incrementAmount;
   private LocalDateTime m_startDate;
}
