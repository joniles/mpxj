/*
 * file:       ProjectCalendarWeek.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       08/11/2011
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.common.DateHelper;

/**
 * This class represents a basic working week, with no exceptions.
 */
public class ProjectCalendarWeek extends ProjectCalendarDays implements Comparable<ProjectCalendarWeek>
{
   /**
    * Retrieves the data range for which this week is valid.
    * Returns null if this is the default week.
    *
    * @return date range, or null
    */
   public DateRange getDateRange()
   {
      return m_dateRange;
   }

   /**
    * Sets the date range for this week. Set this to null to indicate
    * the default week.
    *
    * @param range date range, or null
    */
   public void setDateRange(DateRange range)
   {
      m_dateRange = range;
   }

   /**
    * Converts this working week into a set of equivalent recurring exceptions.
    * Note that this can't be applied to the default working week.
    *
    * @param calendar calendar used for context in the conversion
    * @return recurring exceptions equivalent to this working week
    */
   public List<ProjectCalendarException> convertToRecurringExceptions(ProjectCalendar calendar)
   {
      // We can't expand the default week
      if (m_dateRange == null)
      {
         throw new UnsupportedOperationException();
      }

      // Avoid generating exceptions beyond the bounds of the project
      List<ProjectCalendarException> result = new ArrayList<>();
      Date earliestStartDate = calendar.getParentFile().getEarliestStartDate();
      Date fromDate = m_dateRange.getStart();
      if (DateHelper.compare(earliestStartDate, fromDate) > 0)
      {
         fromDate = earliestStartDate;
      }

      Date latestFinishDate = calendar.getParentFile().getLatestFinishDate();
      Date toDate = m_dateRange.getEnd();
      if (DateHelper.compare(toDate, latestFinishDate) > 0)
      {
         toDate = latestFinishDate;
      }

      //
      // Generate a recurring exception for each day
      //
      for (Day day : Day.values())
      {
         if (getCalendarDayType(day) == DayType.DEFAULT)
         {
            continue;
         }

         RecurringData recurrence = new RecurringData();
         recurrence.setRecurrenceType(RecurrenceType.WEEKLY);
         recurrence.setStartDate(fromDate);
         recurrence.setFinishDate(toDate);
         recurrence.setUseEndDate(true);
         recurrence.setWeeklyDay(day, true);

         ProjectCalendarException ex = new ProjectCalendarException(recurrence);
         ProjectCalendarHours hours = getCalendarHours(day);
         // TODO: for consistency this should never be null, just empty?
         if (hours != null)
         {
            ex.addAll(hours);
         }

         result.add(ex);
      }

      return result;
   }

   @Override public int compareTo(ProjectCalendarWeek o)
   {
      long fromTime1 = m_dateRange.getStart().getTime();
      long fromTime2 = o.m_dateRange.getStart().getTime();
      return (Long.compare(fromTime1, fromTime2));
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[ProjectCalendarWeek");
      pw.println("   name=" + getName());
      pw.println("   date_range=" + getDateRange());

      for (Day day : Day.values())
      {
         pw.println("   [Day " + day);
         pw.println("      type=" + getCalendarDayType(day));
         pw.println("      hours=" + getCalendarHours(day));
         pw.println("   ]");
      }

      pw.println("]");
      pw.flush();
      return (os.toString());
   }

   /**
    * Date range for which this week is valid, null if this is the default week.
    */
   private DateRange m_dateRange;
}
