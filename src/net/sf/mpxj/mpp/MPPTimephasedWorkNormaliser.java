/*
 * file:       MppTimephasedWorkNormaliser.java
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

package net.sf.mpxj.mpp;

import java.util.Date;
import java.util.LinkedList;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.common.DateHelper;

/**
 * Normalise timephased resource assignment data from an MPP file.
 */
public class MPPTimephasedWorkNormaliser extends MPPAbstractTimephasedWorkNormaliser
{
   /**
    * This method merges together assignment data for the same day.
    *
    * @param calendar current calendar
    * @param list assignment data
    */
   @Override protected void mergeSameDay(ProjectCalendar calendar, LinkedList<TimephasedWork> list)
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
            Date previousAssignmentStart = previousAssignment.getStart();
            Date previousAssignmentStartDay = DateHelper.getDayStartDate(previousAssignmentStart);
            Date assignmentStart = assignment.getStart();
            Date assignmentStartDay = DateHelper.getDayStartDate(assignmentStart);

            if (previousAssignmentStartDay.getTime() == assignmentStartDay.getTime())
            {
               Duration previousAssignmentWork = previousAssignment.getTotalAmount();
               Duration assignmentWork = assignment.getTotalAmount();

               if (previousAssignmentWork.getDuration() != 0 && assignmentWork.getDuration() == 0)
               {
                  continue;
               }

               Date previousAssignmentFinish = previousAssignment.getFinish();

               if (previousAssignmentFinish.getTime() == assignmentStart.getTime() || calendar.getNextWorkStart(previousAssignmentFinish).getTime() == assignmentStart.getTime())
               {
                  result.removeLast();

                  if (previousAssignmentWork.getDuration() != 0 && assignmentWork.getDuration() != 0)
                  {
                     double work = previousAssignment.getTotalAmount().getDuration();
                     work += assignment.getTotalAmount().getDuration();
                     Duration totalWork = Duration.getInstance(work, TimeUnit.MINUTES);

                     TimephasedWork merged = new TimephasedWork();
                     merged.setStart(previousAssignment.getStart());
                     merged.setFinish(assignment.getFinish());
                     merged.setTotalAmount(totalWork);
                     assignment = merged;
                  }
                  else
                  {
                     if (assignmentWork.getDuration() == 0)
                     {
                        assignment = previousAssignment;
                     }
                  }
               }
            }

            assignment.setAmountPerDay(assignment.getTotalAmount());
            result.add(assignment);
         }

         Duration calendarWork = calendar.getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.MINUTES);
         Duration assignmentWork = assignment.getTotalAmount();
         if (calendarWork.getDuration() == 0 && assignmentWork.getDuration() == 0)
         {
            result.removeLast();
         }
         else
         {
            previousAssignment = assignment;
         }
      }

      list.clear();
      list.addAll(result);
   }
}
