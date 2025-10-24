package org.mpxj;

import org.mpxj.common.NumberHelper;

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

   public void setMinutesPerYear(Integer minutesPerYear)
   {
      m_minutesPerYear = minutesPerYear;
   }

   @Override public Integer getDaysPerMonth()
   {
      return m_daysPerMonth;
   }

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
