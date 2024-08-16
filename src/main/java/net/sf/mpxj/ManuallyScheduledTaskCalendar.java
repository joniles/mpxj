/*
 * file:       ProjectCalendar.java
 * author:     Fabian Schmidt
 * date:       16/08/2024
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

package net.sf.mpxj;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class represents a Calendar Definition record for an MPP Manually Scheduled task.
 */
public class ManuallyScheduledTaskCalendar extends ProjectCalendar
{
   public ManuallyScheduledTaskCalendar(ProjectCalendar calendar, ResourceAssignment assignment)
   {
      super(calendar.getParentFile(), true);
      m_calendar = calendar;
      m_assignment = assignment;
   }

   /**
    * Retrieves the working hours on the given date.
    *
    * @param date required date
    * @return working hours
    */
   @Override protected ProjectCalendarHours getRanges(LocalDate date)
   {
      // If today is not a working day then find first ProjectCalendarRange with working time.
      ProjectCalendarHours effectiveRanges = m_calendar.getRanges(date);
      if (effectiveRanges.isEmpty())
      {
         // Date is not a working day.
         // Find first ProjectCalendarRange with working time. Starting on Tuesday(!).
         // Using [Default] calendar - ignoring exceptions and work week rules. Uses this a basis for all calculations.
         if (m_calendar.getDayType(DayOfWeek.TUESDAY) == DayType.WORKING)
         {
            effectiveRanges = m_calendar.getHours(DayOfWeek.TUESDAY);
         }
         else
            if (m_calendar.getDayType(DayOfWeek.WEDNESDAY) == DayType.WORKING)
            {
               effectiveRanges = m_calendar.getHours(DayOfWeek.WEDNESDAY);
            }
            else
               if (m_calendar.getDayType(DayOfWeek.THURSDAY) == DayType.WORKING)
               {
                  effectiveRanges = m_calendar.getHours(DayOfWeek.THURSDAY);
               }
               else
                  if (m_calendar.getDayType(DayOfWeek.FRIDAY) == DayType.WORKING)
                  {
                     effectiveRanges = m_calendar.getHours(DayOfWeek.FRIDAY);
                  }
                  else
                     if (m_calendar.getDayType(DayOfWeek.SATURDAY) == DayType.WORKING)
                     {
                        effectiveRanges = m_calendar.getHours(DayOfWeek.SATURDAY);
                     }
                     else
                        if (m_calendar.getDayType(DayOfWeek.SUNDAY) == DayType.WORKING)
                        {
                           effectiveRanges = m_calendar.getHours(DayOfWeek.SUNDAY);
                        }
                        else
                           if (m_calendar.getDayType(DayOfWeek.MONDAY) == DayType.WORKING)
                           {
                              effectiveRanges = m_calendar.getHours(DayOfWeek.MONDAY);
                           }
      }
      return effectiveRanges;
   }

   @Override public LocalDateTime getDate(LocalDateTime date, Duration duration)
   {
      throw new UnsupportedOperationException();
   }

   @Override public Duration getWork(LocalDateTime startDate, LocalDateTime endDate, TimeUnit format)
   {
      throw new UnsupportedOperationException();
   }

   @Override public LocalTime getFinishTime(LocalDate date)
   {
      throw new UnsupportedOperationException();
   }

   @Override public LocalDateTime getNextWorkStart(LocalDateTime date)
   {
      throw new UnsupportedOperationException();
   }

   private final ProjectCalendar m_calendar;
   private final ResourceAssignment m_assignment;
}
