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

package net.sf.mpxj.common;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.TimeRange;

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
      super(calendar1.getParentFile());
      m_calendar1 = calendar1;
      m_calendar2 = calendar2;
   }

   @Override protected ProjectCalendarHours getRanges(Date date, Calendar cal, Day day)
   {
      ProjectCalendarHours result = new ProjectCalendarHours();
      ProjectCalendarHours hours1 = date == null ? m_calendar1.getHours(day) : m_calendar1.getHours(date);
      ProjectCalendarHours hours2 = date == null ? m_calendar2.getHours(day) : m_calendar2.getHours(date);

      for (TimeRange range1 : hours1)
      {
         LocalTime range1Start = range1.getStartAsLocalTime();
         LocalTime range1End = range1.getEndAsLocalTime();

         for (TimeRange range2 : hours2)
         {
            LocalTime range2Start = range2.getStartAsLocalTime();

            if (range1End != LocalTime.MIDNIGHT && !range1End.isAfter(range2Start))
            {
               // range1 finishes before range2 starts so there is no overlap, get the next range1
               break;
            }

            LocalTime range2End = range2.getEndAsLocalTime();
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
            result.add(new TimeRange(start, end));
         }
      }

      return result;
   }

   private final ProjectCalendar m_calendar1;
   private final ProjectCalendar m_calendar2;
}