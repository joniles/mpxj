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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.Duration;
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
    * @param list assignment data
    */
   protected void mergeSameWork(ResourceAssignment assignment, List<TimephasedWork> list)
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
            if (workCanBeMerged(assignment, previousTimephasedWork, currentTimephasedWork))
            {
               Duration assignmentWork = currentTimephasedWork.getTotalAmount();

               Date assignmentStart = previousTimephasedWork.getStart();
               Date assignmentFinish = currentTimephasedWork.getFinish();
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

   private boolean workCanBeMerged(ResourceAssignment assignment, TimephasedWork previousTimephasedWork, TimephasedWork currentTimephasedWork)
   {
      Duration previousAmount = previousTimephasedWork.getAmountPerDay();
      Duration currentAmount = currentTimephasedWork.getTotalAmount();

      boolean sameDuration = NumberHelper.equals(previousAmount.getDuration(), currentAmount.getDuration(), 0.01);
      if (!sameDuration)
      {
         return false;
      }

      if (sameDuration && previousAmount.getDuration() == 0)
      {
         return true;
      }

      return timephasedWorkHasStandardHours(assignment, previousTimephasedWork) && timephasedWorkHasStandardHours(assignment, currentTimephasedWork);
   }

   private boolean timephasedWorkHasStandardHours(ResourceAssignment assignment, TimephasedWork timephasedWork)
   {
      ProjectCalendarHours hours = assignment.getCalendar().getHours(timephasedWork.getStart());
      Date hoursStart = DateHelper.getCanonicalTime(hours.get(0).getStart());
      Date workStart = DateHelper.getCanonicalTime(timephasedWork.getStart());
      if (DateHelper.compare(hoursStart, workStart) != 0)
      {
         return false;
      }

      Date hoursEnd = DateHelper.getCanonicalTime(hours.get(hours.size()-1).getEnd());
      Date workEnd = DateHelper.getCanonicalTime(timephasedWork.getFinish());

      return DateHelper.compare(hoursEnd, workEnd) == 0;
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
