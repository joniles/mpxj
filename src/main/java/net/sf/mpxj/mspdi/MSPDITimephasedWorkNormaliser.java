/*
 * file:       MspdiTimephasedWorkNormaliser.java
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

package net.sf.mpxj.mspdi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.common.AbstractTimephasedWorkNormaliser;
import net.sf.mpxj.common.CombinedCalendar;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.LocalTimeHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * Normalise timephased resource assignment data from an MSPDI file.
 */
public class MSPDITimephasedWorkNormaliser extends AbstractTimephasedWorkNormaliser
{

   /**
    * This method converts the internal representation of timephased
    * resource assignment data used by MS Project into a standardised
    * format to make it easy to work with.
    *
    * @param assignment resource assignment
    * @param list list of assignment data
    */
   @Override public void normalise(ResourceAssignment assignment, List<TimephasedWork> list)
   {
      ProjectCalendar calendar = getCalendar(assignment);

      //dumpList("raw", result);
      splitDays(calendar, list);
      //dumpList("split days", result);
      mergeSameDay(calendar, list);
      //dumpList("mergeSameDay", result);
      mergeSameWork(calendar, assignment, list);
      //dumpList("mergeSameWork", result);
      validateSameDay(calendar, list);
      convertToHours(list);
   }

   private ProjectCalendar getCalendar(ResourceAssignment assignment)
   {
      ProjectCalendar taskCalendar = assignment.getTask().getCalendar();
      ProjectCalendar resourceCalendar = assignment.getResource() != null && assignment.getResource().getType() == ResourceType.WORK ? assignment.getCalendar() : null;
      ProjectCalendar result;
      if (taskCalendar != null && resourceCalendar != null)
      {
         result = new CombinedCalendar(taskCalendar, resourceCalendar);
      }
      else
      {
         result = resourceCalendar == null ? assignment.getTask().getEffectiveCalendar() : resourceCalendar;
      }
      return result;
   }

   /*
      private void dumpList(String label, List<TimephasedWork> list)
      {
         System.out.println(label);
         for (TimephasedWork assignment : list)
         {
            System.out.println(assignment);
         }
      }
      */

   /**
    * This method breaks down spans of time into individual days.
    *
    * @param calendar current project calendar
    * @param list list of assignment data
    */
   private void splitDays(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();
      for (TimephasedWork assignment : list)
      {
         Duration calendarWork = calendar.getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.MINUTES);
         while (assignment != null)
         {
            Date startDay = DateHelper.getDayStartDate(assignment.getStart());
            Date finishDay = DateHelper.getDayStartDate(assignment.getFinish());

            // special case - when the finishday time is midnight, it's really the previous day...
            if (assignment.getFinish().getTime() == finishDay.getTime())
            {
               finishDay = DateHelper.addDays(finishDay, -1);
            }

            if (startDay.getTime() == finishDay.getTime())
            {
               result.add(assignment);
               break;
            }

            TimephasedWork[] split = splitFirstDay(calendar, assignment, calendarWork);
            if (split[0] != null)
            {
               TimephasedWork firstDayAssignment = split[0];
               result.add(firstDayAssignment);
               Duration firstDayCalendarWork = calendar.getWork(firstDayAssignment.getStart(), firstDayAssignment.getFinish(), TimeUnit.MINUTES);
               calendarWork = Duration.getInstance((calendarWork.getDuration() - firstDayCalendarWork.getDuration()), TimeUnit.MINUTES);
            }
            assignment = split[1];
         }
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * This method splits the first day off of a time span.
    *
    * @param calendar current calendar
    * @param assignment timephased assignment span
    * @param calendarWork working hours for assignment from the calendar
    * @return first day and remainder assignments
    */
   private TimephasedWork[] splitFirstDay(ProjectCalendar calendar, TimephasedWork assignment, Duration calendarWork)
   {
      TimephasedWork[] result = new TimephasedWork[2];

      //
      // Retrieve data used to calculate the pro-rata work split
      //
      Date assignmentStart = assignment.getStart();
      Date assignmentFinish = assignment.getFinish();
      Duration assignmentWork = assignment.getTotalAmount();

      if (calendarWork.getDuration() != 0)
      {
         //
         // Split the first day
         //
         Date splitFinish;
         double splitMinutes;
         if (calendar.isWorkingDate(assignmentStart))
         {
            Date splitFinishTime = calendar.getFinishTime(assignmentStart);
            splitFinish = DateHelper.setTime(assignmentStart, splitFinishTime);
            splitMinutes = calendar.getWork(assignmentStart, splitFinish, TimeUnit.MINUTES).getDuration();

            splitMinutes *= assignmentWork.getDuration();
            splitMinutes /= calendarWork.getDuration();
            splitMinutes = NumberHelper.round(splitMinutes, 2);

            Duration splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);

            TimephasedWork split = new TimephasedWork();
            split.setStart(assignmentStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(splitWork);

            result[0] = split;
         }
         else
         {
            splitFinish = assignmentStart;
            splitMinutes = 0;
         }

         //
         // Split the remainder
         //
         Date splitStart = calendar.getNextWorkStart(splitFinish);
         splitFinish = assignmentFinish;
         TimephasedWork split;
         if (splitStart.getTime() > splitFinish.getTime())
         {
            split = null;
         }
         else
         {
            splitMinutes = assignmentWork.getDuration() - splitMinutes;
            Duration splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);

            split = new TimephasedWork();
            split.setStart(splitStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(splitWork);
         }

         result[1] = split;
      }
      return result;
   }

   /**
    * This method merges together assignment data for the same day.
    *
    * @param calendar current calendar
    * @param list assignment data
    */
   private void mergeSameDay(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();

      TimephasedWork previousAssignment = null;
      for (TimephasedWork assignment : list)
      {
         if (previousAssignment != null)
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

               result.remove(result.size() - 1);

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

         Duration calendarWork = calendar.getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.MINUTES);
         Duration assignmentWork = assignment.getTotalAmount();
         if (calendarWork.getDuration() == 0 && assignmentWork.getDuration() == 0)
         {
            result.remove(result.size() - 1);
         }
         else
         {
            previousAssignment = assignment;
         }
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * Ensures that the start and end dates for ranges fit within the
    * working times for a given day.
    *
    * @param calendar current calendar
    * @param list assignment data
    */
   private void validateSameDay(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      for (TimephasedWork assignment : list)
      {
         Date assignmentStart = assignment.getStart();
         Date calendarStartTime = LocalTimeHelper.getDate(calendar.getStartTime(assignmentStart));
         Date assignmentStartTime = DateHelper.getCanonicalTime(assignmentStart);
         Date assignmentFinish = assignment.getFinish();
         Date calendarFinishTime = calendar.getFinishTime(assignmentFinish);
         Date assignmentFinishTime = DateHelper.getCanonicalTime(assignmentFinish);
         double totalWork = assignment.getTotalAmount().getDuration();

         if (assignmentStartTime != null && calendarStartTime != null)
         {
            if ((totalWork == 0 && assignmentStartTime.getTime() != calendarStartTime.getTime()) || (assignmentStartTime.getTime() < calendarStartTime.getTime()))
            {
               assignmentStart = DateHelper.setTime(assignmentStart, calendarStartTime);
               assignment.setStart(assignmentStart);
            }
         }

         if (assignmentFinishTime != null && calendarFinishTime != null)
         {
            if ((totalWork == 0 && assignmentFinishTime.getTime() != calendarFinishTime.getTime()) || (assignmentFinishTime.getTime() > calendarFinishTime.getTime()))
            {
               assignmentFinish = DateHelper.setTime(assignmentFinish, calendarFinishTime);
               assignment.setFinish(assignmentFinish);
            }
         }
      }
   }
}
