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

package net.sf.mpxj.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.mpp.TimescaleUnits;

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
    * @return array of date ranges
    */
   public final ArrayList<DateRange> createTimescale(Date startDate, TimescaleUnits segmentUnit, int segmentCount)
   {
      ArrayList<DateRange> result = new ArrayList<DateRange>(segmentCount);

      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      int calendarIncrementUnit;
      int calendarIncrementAmount;

      switch (segmentUnit)
      {
         case MINUTES:
         {
            calendarIncrementUnit = Calendar.MINUTE;
            calendarIncrementAmount = 1;
            break;
         }

         case HOURS:
         {
            calendarIncrementUnit = Calendar.HOUR_OF_DAY;
            calendarIncrementAmount = 1;
            break;
         }

         case WEEKS:
         {
            cal.set(Calendar.DAY_OF_WEEK, m_weekStartDay);
            calendarIncrementUnit = Calendar.DAY_OF_YEAR;
            calendarIncrementAmount = 7;
            break;
         }

         case THIRDS_OF_MONTHS:
         {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            calendarIncrementUnit = Calendar.DAY_OF_YEAR;
            calendarIncrementAmount = 10;
            break;
         }

         case MONTHS:
         {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            calendarIncrementUnit = Calendar.MONTH;
            calendarIncrementAmount = 1;
            break;
         }

         case QUARTERS:
         {
            int currentMonth = cal.get(Calendar.MONTH);
            int currentQuarter = currentMonth / 3;
            int startMonth = currentQuarter * 3;
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, startMonth);
            calendarIncrementUnit = Calendar.MONTH;
            calendarIncrementAmount = 3;
            break;
         }

         case HALF_YEARS: // align to jan, jun
         {
            int currentMonth = cal.get(Calendar.MONTH);
            int currentHalf = currentMonth / 6;
            int startMonth = currentHalf * 6;
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, startMonth);
            calendarIncrementUnit = Calendar.MONTH;
            calendarIncrementAmount = 6;
            break;
         }

         case YEARS: // align to 1 jan
         {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            calendarIncrementUnit = Calendar.YEAR;
            calendarIncrementAmount = 1;
            break;
         }

         default:
         case DAYS:
         {
            calendarIncrementUnit = Calendar.DAY_OF_YEAR;
            calendarIncrementAmount = 1;
            break;
         }
      }

      for (int loop = 0; loop < segmentCount; loop++)
      {
         Date rangeStart = cal.getTime();

         if (segmentUnit == TimescaleUnits.THIRDS_OF_MONTHS && (loop + 1) % 3 == 0)
         {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, 1);
         }
         else
         {
            cal.add(calendarIncrementUnit, calendarIncrementAmount);
         }

         cal.add(Calendar.MILLISECOND, -1);
         result.add(new DateRange(rangeStart, cal.getTime()));
         cal.add(Calendar.MILLISECOND, 1);
      }

      return result;
   }

   /**
    * Set the day on which the week starts. Defaults to Calendar.MONDAY.
    *
    * @param weekStartDay week start day
    */
   public void setWeekStartDay(int weekStartDay)
   {
      m_weekStartDay = weekStartDay;
   }

   /**
    * Retrieves the day on which the week starts.
    *
    * @return week start day
    */
   public int getWeekStartDay()
   {
      return m_weekStartDay;
   }

   private int m_weekStartDay = Calendar.MONDAY;
}
