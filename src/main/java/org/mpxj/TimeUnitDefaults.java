/*
 * file:       TimeUnitDefaults.java
 * author:     Jon Iles
 * date:       2025-11-12
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

package org.mpxj;

import org.mpxj.common.NumberHelper;

/**
 * Represents default values for time units as part of the project context.
 */
public class TimeUnitDefaults implements TimeUnitDefaultsContainer
{
   @Override public Integer getMinutesPerDay()
   {
      if (m_minutesPerDay == null)
      {
         m_minutesPerDay = DEFAULT_MINUTES_PER_DAY;
      }
      return m_minutesPerDay;
   }

   /**
    * Set the default minutes per day.
    *
    * @param minutesPerDay default minutes per day
    */
   public void setMinutesPerDay(Integer minutesPerDay)
   {
      m_minutesPerDay = minutesPerDay;
   }

   @Override public Integer getMinutesPerWeek()
   {
      if (m_minutesPerWeek == null)
      {
          m_minutesPerWeek = Integer.valueOf(DEFAULT_DAYS_PER_WEEK * NumberHelper.getInt(getMinutesPerDay()));
      }
      return m_minutesPerWeek;
   }

   /**
    * Set the default minutes per week.
    *
    * @param minutesPerWeek default minutes per week
    */
   public void setMinutesPerWeek(Integer minutesPerWeek)
   {
      m_minutesPerWeek = minutesPerWeek;
   }

   @Override public Integer getMinutesPerMonth()
   {
      if (m_minutesPerMonth == null)
      {
         m_minutesPerMonth = Integer.valueOf(NumberHelper.getInt(getMinutesPerDay()) * NumberHelper.getInt(getDaysPerMonth()));
      }
      return m_minutesPerMonth;
   }

   /**
    * Set the default minutes per month.
    *
    * @param minutesPerMonth minutes per month
    */
   public void setMinutesPerMonth(Integer minutesPerMonth)
   {
      m_minutesPerMonth = minutesPerMonth;
   }

   @Override public Integer getMinutesPerYear()
   {
      if (m_minutesPerYear == null)
      {
         m_minutesPerYear = Integer.valueOf(NumberHelper.getInt(getMinutesPerDay()) * NumberHelper.getInt(getDaysPerMonth()) * 12);
      }
      return m_minutesPerYear;
   }

   /**
    * Set the default minutes per year.
    *
    * @param minutesPerYear default minutes per year
    */
   public void setMinutesPerYear(Integer minutesPerYear)
   {
      m_minutesPerYear = minutesPerYear;
   }

   @Override public Integer getDaysPerMonth()
   {
      return m_daysPerMonth;
   }

   /**
    * Set the default days per month.
    *
    * @param daysPerMonth default days per month
    */
   public void setDaysPerMonth(Integer daysPerMonth)
   {
      m_daysPerMonth = daysPerMonth;
   }

   private Integer m_minutesPerDay;
   private Integer m_minutesPerWeek;
   private Integer m_minutesPerMonth;
   private Integer m_minutesPerYear;
   private Integer m_daysPerMonth = DEFAULT_DAYS_PER_MONTH;

   private static final int DEFAULT_DAYS_PER_WEEK = 5;
   private static final Integer DEFAULT_DAYS_PER_MONTH = Integer.valueOf(20);
   private static final Integer DEFAULT_MINUTES_PER_DAY = Integer.valueOf(480);
}
