/*
 * file:       CombinedCalendar.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       08/11/2022
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

import java.time.LocalDate;
import java.time.LocalTime;

import java.time.DayOfWeek;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.LocalTimeRange;

/**
 * A calendar which represents the intersection of working time between
 * two other calendars.
 */
public class CombinedCalendar extends ProjectCalendar
{
   /**
    * Constructor.
    *
    * @param calendar1 source calendar
    * @param calendar2 source calendar
    */
   public CombinedCalendar(ProjectCalendar calendar1, ProjectCalendar calendar2)
   {
      super(calendar1.getParentFile(), true);
      m_calendar1 = calendar1;
      m_calendar2 = calendar2;
   }

   @Override protected ProjectCalendarHours getRanges(LocalDate date)
   {
      return getRanges(m_calendar1.getHours(date), m_calendar2.getHours(date));
   }

   @Override protected ProjectCalendarHours getRanges(DayOfWeek day)
   {
      return getRanges(m_calendar1.getHours(day), m_calendar2.getHours(day));
   }

   private ProjectCalendarHours getRanges(ProjectCalendarHours hours1, ProjectCalendarHours hours2)
   {
      ProjectCalendarHours result = new ProjectCalendarHours();
      for (LocalTimeRange range1 : hours1)
      {
         LocalTime range1Start = range1.getStart();
         LocalTime range1End = range1.getEnd();

         for (LocalTimeRange range2 : hours2)
         {
            LocalTime range2Start = range2.getStart();

            if (range1End != LocalTime.MIDNIGHT && !range1End.isAfter(range2Start))
            {
               // range1 finishes before range2 starts so there is no overlap, get the next range1
               break;
            }

            LocalTime range2End = range2.getEnd();
            if (range2End != LocalTime.MIDNIGHT && !range1Start.isBefore(range2End))
            {
               // range1 starts after range2 so there is no overlap, get the next range2
               continue;
            }

            LocalTime start = range1Start.isAfter(range2Start) ? range1Start : range2Start;
            LocalTime end;
            if (range1End == LocalTime.MIDNIGHT)
            {
               end = range2End;
            }
            else
            {
               if (range2End == LocalTime.MIDNIGHT)
               {
                  end = range1End;
               }
               else
               {
                  end = range1End.isBefore(range2End) ? range1End : range2End;
               }
            }
            result.add(new LocalTimeRange(start, end));
         }
      }

      return result;
   }

   private final ProjectCalendar m_calendar1;
   private final ProjectCalendar m_calendar2;
}