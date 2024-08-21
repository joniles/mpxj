/*
 * file:       ManuallyScheduledTaskCalendar.java
 * author:     Fabian Schmidt
 * date:       2024-08-16
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a Calendar Definition record for an MPP Manually Scheduled task.
 */
public class ManuallyScheduledTaskCalendar extends ProjectCalendar
{
   /**
    * Constructor.
    *
    * @param calendar effective calendar for resource assignment
    * @param assignment resource assignment
    */
   public ManuallyScheduledTaskCalendar(ProjectCalendar calendar, ResourceAssignment assignment)
   {
      super(calendar.getParentFile(), true);
      m_calendar = calendar;
      m_assignment = assignment;
      m_assignmentStartDate = m_assignment.getStart().toLocalDate();
      m_assignmentEndDate = m_assignment.getFinish().toLocalDate();
   }

   /**
    * Retrieves the working hours on the given date.
    *
    * @param date required date
    * @return working hours
    */
   @Override protected ProjectCalendarHours getRanges(LocalDate date)
   {
      ProjectCalendarHours effectiveRanges = m_calendar.getRanges(date);
      // If today is not a working day then find first ProjectCalendarRange with working time.
      if (effectiveRanges.isEmpty() && (date.equals(m_assignmentStartDate) || date.equals(m_assignmentEndDate)))
      {
         // Date is not a working day.
         // Find first ProjectCalendarRange with working time. Starting on Tuesday(!).
         // Using [Default] calendar - ignoring exceptions and work week rules. Uses this a basis for all calculations.
         DayOfWeek firstWorkingDay = WEEK_DAYS.stream().filter(d -> m_calendar.getDayType(d) == DayType.WORKING).findFirst().orElse(null);
         if (firstWorkingDay != null)
         {
            effectiveRanges = m_calendar.getHours(firstWorkingDay);
         }
      }

      // In case the calendar has no working days. Normally Project blocks the creation of such calendar.
      if (effectiveRanges.isEmpty())
      {
         return effectiveRanges;
      }

      if (date.equals(m_assignmentStartDate))
      {
         LocalTime assignment_start_time = m_assignment.getStart().toLocalTime();
         LocalTime firstRangeStart = effectiveRanges.get(0).getStart();
         if (assignment_start_time.isBefore(firstRangeStart))
         {
            // Create a new temp ranges.
            // First range is from assignment start to regular end of range.
            ProjectCalendarHours newRanges = new ProjectCalendarHours();
            newRanges.addAll(effectiveRanges);
            LocalTime firstRangeEnd = effectiveRanges.get(0).getEnd();
            newRanges.set(0, new LocalTimeRange(assignment_start_time, firstRangeEnd));
            effectiveRanges = newRanges;
         }
         else
         {
            LocalTime lastRangeEnd = effectiveRanges.get(effectiveRanges.size() - 1).getEnd();
            if (assignment_start_time.isAfter(lastRangeEnd))
            {
               // Create a new temp ranges.
               // Only one range from assignment start to end of day.
               ProjectCalendarHours newRanges = new ProjectCalendarHours();
               newRanges.add(new LocalTimeRange(assignment_start_time, LocalTime.MIDNIGHT));
               effectiveRanges = newRanges;
            }
         }
      }
      
      if (date.equals(m_assignmentEndDate))
      {
         LocalTime assignment_end_time = m_assignment.getFinish().toLocalTime();
         LocalTime firstRangeStart = effectiveRanges.get(0).getStart();

         if (assignment_end_time.isBefore(firstRangeStart))
         {
            // Create a new temp ranges.
            // Only one range from start of day to assignment end.
            ProjectCalendarHours newRanges = new ProjectCalendarHours();
            newRanges.add(new LocalTimeRange(LocalTime.MIDNIGHT, assignment_end_time));
            effectiveRanges = newRanges;
         }
         else
         {
            LocalTime lastRangeEnd = effectiveRanges.get(effectiveRanges.size() - 1).getEnd();
            if (assignment_end_time.isAfter(lastRangeEnd))
            {
               // Create a new temp ranges.
               // Last range is from regular range end to assignment end.
               ProjectCalendarHours newRanges = new ProjectCalendarHours();
               newRanges.addAll(effectiveRanges);
               LocalTime lastRangeStart = effectiveRanges.get(effectiveRanges.size() - 1).getStart();
               newRanges.set(effectiveRanges.size() - 1, new LocalTimeRange(lastRangeStart, assignment_end_time));
               effectiveRanges = newRanges;
            }
         }
      }

      return effectiveRanges;
   }

   private final ProjectCalendar m_calendar;
   private final ResourceAssignment m_assignment;
   private final LocalDate m_assignmentStartDate;
   private final LocalDate m_assignmentEndDate;
   
   private static final List<DayOfWeek> WEEK_DAYS = Arrays.asList(
      DayOfWeek.TUESDAY,
      DayOfWeek.WEDNESDAY,
      DayOfWeek.THURSDAY,
      DayOfWeek.FRIDAY,
      DayOfWeek.SATURDAY,
      DayOfWeek.SUNDAY,
      DayOfWeek.MONDAY
   );
}
