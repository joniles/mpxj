/*
 * file:       AbstractTimephasedWorkNormaliser.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       09/01/2009
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;

/**
 * Common implementation detail for normalisation.
 */
public abstract class AbstractTimephasedWorkNormaliser implements TimephasedNormaliser<TimephasedWork>
{
   /**
    * Merges individual days together into time spans where the
    * same work is undertaken each day.
    *
    * @param calendar effective calendar for the assignment
    * @param assignment resource assignment
    * @param list assignment data
    */
   protected void mergeSameWork(ProjectCalendar calendar, ResourceAssignment assignment, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();

      TimephasedWork previousTimephasedWork = null;
      for (TimephasedWork currentTimephasedWork : list)
      {
         if (previousTimephasedWork == null)
         {
            currentTimephasedWork.setAmountPerDay(currentTimephasedWork.getTotalAmount());
            result.add(currentTimephasedWork);
         }
         else
         {
            if (workCanBeMerged(calendar, assignment, previousTimephasedWork, currentTimephasedWork))
            {
               Duration assignmentWork = currentTimephasedWork.getTotalAmount();

               LocalDateTime assignmentStart = previousTimephasedWork.getStart();
               LocalDateTime assignmentFinish = currentTimephasedWork.getFinish();
               double total = previousTimephasedWork.getTotalAmount().getDuration();
               total += assignmentWork.getDuration();
               Duration totalWork = Duration.getInstance(total, TimeUnit.MINUTES);

               TimephasedWork merged = new TimephasedWork();
               merged.setStart(assignmentStart);
               merged.setFinish(assignmentFinish);
               merged.setAmountPerDay(assignmentWork);
               merged.setTotalAmount(totalWork);

               result.remove(result.size() - 1);
               currentTimephasedWork = merged;
            }
            else
            {
               currentTimephasedWork.setAmountPerDay(currentTimephasedWork.getTotalAmount());
            }
            result.add(currentTimephasedWork);
         }

         previousTimephasedWork = currentTimephasedWork;
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * Determine if two Timephased Work instance can be merged.
    * They can be merged if they represent the same amount of work, and if they represent a non-zero amount of
    * work, if their strat and end pint align with the start and end of the day (or of the assignment).
    *
    * @param calendar effective calendar for the resource assignment
    * @param assignment resource assignment
    * @param previousTimephasedWork TimephasedWork instance
    * @param currentTimephasedWork TimephasedWork instance
    * @return true if the TimephasedWork instances can be merged.
    */
   private boolean workCanBeMerged(ProjectCalendar calendar, ResourceAssignment assignment, TimephasedWork previousTimephasedWork, TimephasedWork currentTimephasedWork)
   {
      Duration previousAmount = previousTimephasedWork.getAmountPerDay();
      Duration currentAmount = currentTimephasedWork.getTotalAmount();

      boolean sameDuration = NumberHelper.equals(previousAmount.getDuration(), currentAmount.getDuration(), 0.01);
      if (!sameDuration)
      {
         return false;
      }

      boolean zeroDuration = NumberHelper.equals(previousAmount.getDuration(), 0, 0.01);
      if (zeroDuration)
      {
         return true;
      }

      return timephasedWorkHasStandardHours(calendar, assignment, previousTimephasedWork) && timephasedWorkHasStandardHours(calendar, assignment, currentTimephasedWork);
   }

   /**
    * Determine if the TimephasedWork instance aligns with the start and end of a day or the assignment.
    * If this is the case we're assuming that we don't have a custom start or end point for the work which
    * we need to preserve.
    *
    * @param calendar effective calendar for the resource assignment
    * @param assignment resource assignment
    * @param timephasedWork TimephasedWork instance
    * @return true if the TimephasedWork instance aligns with the start and end of the day
    */
   private boolean timephasedWorkHasStandardHours(ProjectCalendar calendar, ResourceAssignment assignment, TimephasedWork timephasedWork)
   {
      ProjectCalendarHours hours = calendar.getHours(LocalDateHelper.getLocalDate(timephasedWork.getStart()));

      LocalTime calendarStart = hours.get(0).getStartAsLocalTime();
      LocalTime timephasedStart = LocalTimeHelper.getLocalTime(timephasedWork.getStart());
      if (LocalDateTimeHelper.compare(assignment.getStart(), timephasedWork.getStart()) != 0 && LocalTimeHelper.compare(calendarStart, timephasedStart) != 0)
      {
         return false;
      }

      LocalTime calendarEnd = hours.get(hours.size() - 1).getEndAsLocalTime();
      LocalTime timephasedEnd = LocalTimeHelper.getLocalTime(timephasedWork.getFinish());
      return LocalDateTimeHelper.compare(assignment.getFinish(), timephasedWork.getFinish()) == 0 || LocalTimeHelper.compare(calendarEnd, timephasedEnd) == 0;
   }

   /**
    * Converts assignment duration values from minutes to hours.
    *
    * @param list assignment data
    */
   protected void convertToHours(List<TimephasedWork> list)
   {
      for (TimephasedWork assignment : list)
      {
         Duration totalWork = assignment.getTotalAmount();
         Duration workPerDay = assignment.getAmountPerDay();
         totalWork = Duration.getInstance(totalWork.getDuration() / 60, TimeUnit.HOURS);
         workPerDay = Duration.getInstance(workPerDay.getDuration() / 60, TimeUnit.HOURS);
         assignment.setTotalAmount(totalWork);
         assignment.setAmountPerDay(workPerDay);
      }
   }
}
