package net.sf.mpxj.common;

import java.util.Calendar;
import java.util.Date;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.TimeUnit;

public class CombinedCalendar extends ProjectCalendar
{
   public CombinedCalendar(ProjectCalendar calendar1, ProjectCalendar calendar2)
   {
      super(calendar1.getParentFile());
      m_calendar1 = calendar1;
      m_calendar2 = calendar2;
   }

   protected ProjectCalendarHours getRanges(Date date, Calendar cal, Day day)
   {
      ProjectCalendarHours result = new ProjectCalendarHours();
      ProjectCalendarHours hours1 = date == null ? m_calendar1.getHours(day) : m_calendar1.getHours(date);
      ProjectCalendarHours hours2 = date == null ? m_calendar2.getHours(day) : m_calendar2.getHours(date);

      for (DateRange range1 : hours1)
      {
         Date range1Start = DateHelper.getCanonicalTime(range1.getStart());
         Date range1End = DateHelper.getCanonicalTime(range1.getEnd());

         for (DateRange range2 : hours2)
         {
            Date range2Start = DateHelper.getCanonicalTime(range2.getStart());
            if (DateHelper.compare(range1End, range2Start) <= 0)
            {
               // range1 finishes before range2 starts so there is no overlap, get the next range1
               break;
            }

            Date range2End = DateHelper.getCanonicalTime(range2.getEnd());
            if (DateHelper.compare(range1Start, range2End) >= 0)
            {
               // range1 starts after range2 so there is no overlap, get the next range2
               continue;
            }

            Date start = DateHelper.max(range1Start, range2Start);
            Date end = DateHelper.min(range1End, range2End);
            result.add(new DateRange(start, end));
         }
      }

      return result;
   }

   private final ProjectCalendar m_calendar1;
   private final ProjectCalendar m_calendar2;
}