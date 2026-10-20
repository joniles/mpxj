package org.mpxj.asta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.mpxj.DayType;
import org.mpxj.LocalTimeRange;

class CalendarExceptionHours
{
   public CalendarExceptionHours(LocalDateTime start, LocalDateTime finish, DayType type)
   {
      m_start = start;
      m_finish = finish;
      m_type = type;
   }

   public LocalDateTime getStart()
   {
      return m_start;
   }

   public LocalDateTime getFinish()
   {
      return m_finish;
   }

   public DayType getType()
   {
      return m_type;
   }

   public boolean isSameDay()
   {
      LocalDate startDay = m_start.toLocalDate();
      LocalDate endDay = m_finish.toLocalDate();
      if (m_finish.toLocalTime().equals(LocalTime.MIDNIGHT))
      {
         endDay = endDay.minusDays(1);
      }

      return startDay.equals(endDay);
   }

   public LocalTimeRange toLocalTimeRange()
   {
      return new LocalTimeRange(m_start.toLocalTime(), m_finish.toLocalTime());
   }

   @Override public String toString()
   {
      return m_start + " " + m_finish + " " + m_type;
   }

   private final LocalDateTime m_start;
   private final LocalDateTime m_finish;
   private final DayType m_type;
}
