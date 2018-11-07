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

import java.util.Date;
import java.util.LinkedList;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;

/**
 * Common implementation detail for normalisation.
 */
public abstract class AbstractTimephasedWorkNormaliser implements TimephasedWorkNormaliser
{
   /**
    * This method converts the internal representation of timephased
    * resource assignment data used by MS Project into a standardised
    * format to make it easy to work with.
    *
    * @param calendar current calendar
    * @param list list of assignment data
    */
   @Override public abstract void normalise(ProjectCalendar calendar, LinkedList<TimephasedWork> list);

   /**
    * Merges individual days together into time spans where the
    * same work is undertaken each day.
    *
    * @param list assignment data
    */
   protected void mergeSameWork(LinkedList<TimephasedWork> list)
   {
      LinkedList<TimephasedWork> result = new LinkedList<TimephasedWork>();

      TimephasedWork previousAssignment = null;
      for (TimephasedWork assignment : list)
      {
         if (previousAssignment == null)
         {
            assignment.setAmountPerDay(assignment.getTotalAmount());
            result.add(assignment);
         }
         else
         {
            Duration previousAssignmentWork = previousAssignment.getAmountPerDay();
            Duration assignmentWork = assignment.getTotalAmount();

            if (NumberHelper.equals(previousAssignmentWork.getDuration(), assignmentWork.getDuration(), 0.01))
            {
               Date assignmentStart = previousAssignment.getStart();
               Date assignmentFinish = assignment.getFinish();
               double total = previousAssignment.getTotalAmount().getDuration();
               total += assignmentWork.getDuration();
               Duration totalWork = Duration.getInstance(total, TimeUnit.MINUTES);

               TimephasedWork merged = new TimephasedWork();
               merged.setStart(assignmentStart);
               merged.setFinish(assignmentFinish);
               merged.setAmountPerDay(assignmentWork);
               merged.setTotalAmount(totalWork);

               result.removeLast();
               assignment = merged;
            }
            else
            {
               assignment.setAmountPerDay(assignment.getTotalAmount());
            }
            result.add(assignment);
         }

         previousAssignment = assignment;
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * Converts assignment duration values from minutes to hours.
    *
    * @param list assignment data
    */
   protected void convertToHours(LinkedList<TimephasedWork> list)
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
