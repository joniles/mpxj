/*
 * file:       CalendarExceptionHours.java
 * author:     Jon Iles
 * date:       2026-10-21
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


package org.mpxj.asta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.mpxj.DayType;
import org.mpxj.LocalTimeRange;

/**
 * Represents a calendar exception.
 */
class CalendarExceptionHours
{
   /**
    * Constructor.
    *
    * @param start start time
    * @param finish finish time
    * @param type exception type
    */
   public CalendarExceptionHours(LocalDateTime start, LocalDateTime finish, DayType type)
   {
      m_start = start;
      m_finish = finish;
      m_type = type;
   }

   /**
    * Retrieve the start time.
    *
    * @return start time
    */
   public LocalDateTime getStart()
   {
      return m_start;
   }

   /**
    * Retrieve the finish time.
    *
    * @return finish time
    */
   public LocalDateTime getFinish()
   {
      return m_finish;
   }

   /**
    * Retrieve the exception type.
    *
    * @return exception type
    */
   public DayType getType()
   {
      return m_type;
   }

   /**
    * Returns true if the exception starts and finishes on the same day.
    *
    * @return true if the exception starts and finishes on the same day.
    */
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

   /**
    * Convert this exception to a LocalTimeRange instance.
    * Note that this assumes the exception does not span multiple days.
    *
    * @return LocalTimeRange instance
    */
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
