/*
 * file:       AbstractTimephasedResourceAssignmentNormaliser.java
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

package net.sf.mpxj;

import java.util.Date;
import java.util.LinkedList;

import net.sf.mpxj.utility.NumberUtility;

/**
 * Common implementation detail for normalisation.
 */
public abstract class AbstractTimephasedResourceAssignmentNormaliser implements TimephasedResourceAssignmentNormaliser
{
   /**
    * This method converts the internal representation of timephased 
    * resource assignment data used by MS Project into a standardised
    * format to make it easy to work with. 
    * 
    * @param calendar current calendar
    * @param list list of assignment data
    */
   public abstract void normalise(ProjectCalendar calendar, LinkedList<TimephasedResourceAssignment> list);

   /**
    * Merges individual days together into time spans where the
    * same work is undertaken each day.
    * 
    * @param list assignment data
    */
   protected void mergeSameWork(LinkedList<TimephasedResourceAssignment> list)
   {
      LinkedList<TimephasedResourceAssignment> result = new LinkedList<TimephasedResourceAssignment>();

      TimephasedResourceAssignment previousAssignment = null;
      for (TimephasedResourceAssignment assignment : list)
      {
         if (previousAssignment == null)
         {
            assignment.setWorkPerDay(assignment.getTotalWork());
            result.add(assignment);
         }
         else
         {
            Duration previousAssignmentWork = previousAssignment.getWorkPerDay();
            Duration assignmentWork = assignment.getTotalWork();

            if (NumberUtility.equals(previousAssignmentWork.getDuration(), assignmentWork.getDuration(), 0.01))
            {
               Date assignmentStart = previousAssignment.getStart();
               Date assignmentFinish = assignment.getFinish();
               double total = previousAssignment.getTotalWork().getDuration();
               total += assignmentWork.getDuration();
               Duration totalWork = Duration.getInstance(total, TimeUnit.MINUTES);

               TimephasedResourceAssignment merged = new TimephasedResourceAssignment();
               merged.setStart(assignmentStart);
               merged.setFinish(assignmentFinish);
               merged.setWorkPerDay(assignmentWork);
               merged.setTotalWork(totalWork);

               result.removeLast();
               assignment = merged;
            }
            else
            {
               assignment.setWorkPerDay(assignment.getTotalWork());
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
   protected void convertToHours(LinkedList<TimephasedResourceAssignment> list)
   {
      for (TimephasedResourceAssignment assignment : list)
      {
         Duration totalWork = assignment.getTotalWork();
         Duration workPerDay = assignment.getWorkPerDay();
         totalWork = Duration.getInstance(totalWork.getDuration() / 60, TimeUnit.HOURS);
         workPerDay = Duration.getInstance(workPerDay.getDuration() / 60, TimeUnit.HOURS);
         assignment.setTotalWork(totalWork);
         assignment.setWorkPerDay(workPerDay);
      }
   }

}
