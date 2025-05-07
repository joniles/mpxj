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

package org.mpxj.common;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.List;

import org.mpxj.Duration;
import org.mpxj.TimePeriodEntity;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;

/**
 * Common implementation detail for normalisation.
 */
public abstract class AbstractTimephasedWorkNormaliser implements TimephasedNormaliser<TimephasedWork>
{
   /**
    * Merges individual days together into time spans where the
    * same work is undertaken each day.
    *
    * @param calendar effective calendar for the timephased data
    * @param parent parent entity
    * @param list timephased data
    */
   protected void mergeSameWork(ProjectCalendar calendar, TimePeriodEntity parent, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();

      TimephasedWork previousTimephasedWork = null;
      for (TimephasedWork item : list)
      {
         if (previousTimephasedWork == null)
         {
            item.setAmountPerDay(item.getTotalAmount());
            result.add(item);
         }
         else
         {
            if (workCanBeMerged(calendar, parent, previousTimephasedWork, item))
            {
               Duration currentWork = item.getTotalAmount();

               LocalDateTime timephasedWorkStart = previousTimephasedWork.getStart();
               LocalDateTime timephasedWorkFinish = item.getFinish();
               double total = previousTimephasedWork.getTotalAmount().getDuration();
               total += currentWork.getDuration();
               Duration totalWork = Duration.getInstance(total, TimeUnit.MINUTES);

               TimephasedWork merged = new TimephasedWork();
               merged.setStart(timephasedWorkStart);
               merged.setFinish(timephasedWorkFinish);
               merged.setAmountPerDay(currentWork);
               merged.setTotalAmount(totalWork);

               result.remove(result.size() - 1);
               item = merged;
            }
            else
            {
               item.setAmountPerDay(item.getTotalAmount());
            }
            result.add(item);
         }

         previousTimephasedWork = item;
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * Determine if two Timephased Work instances can be merged.
    * They can be merged if they represent the same amount of work, and if they represent a non-zero amount of
    * work, if their start and end point align with the start and end of the day (or of the parent entity).
    *
    * @param calendar effective calendar for the timephased data
    * @param parent parent entity
    * @param previousTimephasedWork TimephasedWork instance
    * @param currentTimephasedWork TimephasedWork instance
    * @return true if the TimephasedWork instances can be merged.
    */
   private boolean workCanBeMerged(ProjectCalendar calendar, TimePeriodEntity parent, TimephasedWork previousTimephasedWork, TimephasedWork currentTimephasedWork)
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

      return timephasedWorkHasStandardHours(calendar, parent, previousTimephasedWork) && timephasedWorkHasStandardHours(calendar, parent, currentTimephasedWork);
   }

   /**
    * Determine if the TimephasedWork instance aligns with the start and end of a day or the parent entity.
    * If this is the case we're assuming that we don't have a custom start or end point for the work which
    * we need to preserve.
    *
    * @param calendar effective calendar for the timephased data
    * @param parent parent entity
    * @param timephasedWork TimephasedWork instance
    * @return true if the TimephasedWork instance aligns with the start and end of the day
    */
   private boolean timephasedWorkHasStandardHours(ProjectCalendar calendar, TimePeriodEntity parent, TimephasedWork timephasedWork)
   {
      ProjectCalendarHours hours = calendar.getHours(timephasedWork.getStart());

      LocalTime calendarStart = hours.get(0).getStart();
      LocalTime timephasedStart = LocalTimeHelper.getLocalTime(timephasedWork.getStart());
      if (LocalDateTimeHelper.compare(parent.getStart(), timephasedWork.getStart()) != 0 && LocalTimeHelper.compare(calendarStart, timephasedStart) != 0)
      {
         return false;
      }

      LocalTime calendarEnd = hours.get(hours.size() - 1).getEnd();
      LocalTime timephasedEnd = LocalTimeHelper.getLocalTime(timephasedWork.getFinish());
      return LocalDateTimeHelper.compare(parent.getFinish(), timephasedWork.getFinish()) == 0 || LocalTimeHelper.compare(calendarEnd, timephasedEnd) == 0;
   }

   /**
    * Converts duration values from minutes to hours.
    *
    * @param list timephased data
    */
   protected void convertToHours(List<TimephasedWork> list)
   {
      for (TimephasedWork item : list)
      {
         Duration totalWork = item.getTotalAmount();
         Duration workPerDay = item.getAmountPerDay();
         totalWork = Duration.getInstance(totalWork.getDuration() / 60, TimeUnit.HOURS);
         workPerDay = Duration.getInstance(workPerDay.getDuration() / 60, TimeUnit.HOURS);
         item.setTotalAmount(totalWork);
         item.setAmountPerDay(workPerDay);
      }
   }
}
