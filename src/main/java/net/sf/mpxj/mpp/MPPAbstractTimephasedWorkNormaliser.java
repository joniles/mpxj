/*
 * file:       MppAbstractTimephasedWorkNormaliser.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       02/12/2011
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

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.common.AbstractTimephasedWorkNormaliser;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.LocalDateHelper;
import net.sf.mpxj.common.LocalTimeHelper;

/**
 * Normalise timephased resource assignment data from an MPP file.
 */
public abstract class MPPAbstractTimephasedWorkNormaliser extends AbstractTimephasedWorkNormaliser
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
      if (list.isEmpty())
      {
         return;
      }

      ProjectCalendar calendar = getCalendar(assignment);
      //dumpList(list);
      splitDays(calendar, list);
      //dumpList(list);
      mergeSameDay(calendar, list);
      //dumpList(list);
      mergeSameWork(calendar, assignment, list);
      //dumpList(list);
      convertToHours(list);
      //dumpList(list);
   }

   /**
    * Retrieve the calendar to be used by this normaliser.
    *
    * @param assignment resource assignment
    * @return calendar to use when normalising
    */
   protected abstract ProjectCalendar getCalendar(ResourceAssignment assignment);

   /**
    * This method breaks down spans of time into individual days.
    *
    * @param calendar current project calendar
    * @param list list of assignment data
    */
   private void splitDays(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();
      boolean remainderInserted = false;

      for (TimephasedWork assignment : list)
      {
         if (remainderInserted)
         {
            assignment.setStart(DateHelper.addDays(assignment.getStart(), 1));
            remainderInserted = false;
         }

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
               Duration totalWork = assignment.getTotalAmount();
               Duration assignmentWork = getAssignmentWork(calendar, assignment);
               if ((totalWork.getDuration() - assignmentWork.getDuration()) > EQUALITY_DELTA)
               {
                  assignment.setTotalAmount(assignmentWork);
                  result.add(assignment);
                  Duration remainingWork = Duration.getInstance(totalWork.getDuration() - assignmentWork.getDuration(), TimeUnit.MINUTES);

                  Date remainderStart = DateHelper.addDays(finishDay, 1);
                  Date remainderFinish = DateHelper.addDays(remainderStart, 1);

                  TimephasedWork remainder = new TimephasedWork();
                  remainder.setStart(remainderStart);
                  remainder.setFinish(remainderFinish);
                  remainder.setTotalAmount(remainingWork);
                  result.add(remainder);

                  remainderInserted = true;
               }
               else
               {
                  result.add(assignment);
               }
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

            if (assignment.equals(split[1]))
            {
               break;
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
         LocalDate assignmentStartAsLocalDate = LocalDateHelper.getLocalDate(assignmentStart);
         if (calendar.isWorkingDate(assignmentStartAsLocalDate))
         {
            splitFinish = LocalTimeHelper.setEndTime(assignmentStart, calendar.getFinishTime(assignmentStartAsLocalDate));

            Duration calendarSplitWork = calendar.getWork(assignmentStart, splitFinish, TimeUnit.MINUTES);
            Duration assignmentWorkPerDay = assignment.getAmountPerDay();
            Duration splitWork;

            splitMinutes = assignmentWorkPerDay.getDuration();
            splitMinutes *= calendarSplitWork.getDuration();
            splitMinutes /= (8 * 60); // this appears to be a fixed value
            splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);

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
            split.setAmountPerDay(assignment.getAmountPerDay());
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
   protected abstract void mergeSameDay(ProjectCalendar calendar, List<TimephasedWork> list);

   /**
    * Retrieves the pro-rata work carried out on a given day.
    *
    * @param calendar current calendar
    * @param assignment current assignment.
    * @return assignment work duration
    */
   private Duration getAssignmentWork(ProjectCalendar calendar, TimephasedWork assignment)
   {
      Date splitFinish = LocalTimeHelper.setEndTime(assignment.getStart(), calendar.getFinishTime(LocalDateHelper.getLocalDate(assignment.getStart())));

      Duration calendarSplitWork = calendar.getWork(assignment.getStart(), splitFinish, TimeUnit.MINUTES);
      Duration assignmentWorkPerDay = assignment.getAmountPerDay();
      Duration splitWork;

      double splitMinutes = assignmentWorkPerDay.getDuration();
      splitMinutes *= calendarSplitWork.getDuration();
      splitMinutes /= (8 * 60); // this appears to be a fixed value
      splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
      return splitWork;
   }

   /*
      private void dumpList(List<TimephasedWork> list)
      {
         System.out.println();
         for (TimephasedWork assignment : list)
         {
            System.out.println(assignment);
         }
      }
   */

   private static final double EQUALITY_DELTA = 0.2;
}
