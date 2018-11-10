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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.common.AbstractTimephasedWorkNormaliser;
import net.sf.mpxj.common.DateHelper;

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
    * @param calendar current calendar
    * @param list list of assignment data
    */
   @Override public void normalise(ProjectCalendar calendar, LinkedList<TimephasedWork> list)
   {
      if (!list.isEmpty())
      {
         //dumpList(list);
         splitDays(calendar, list);
         //dumpList(list);
         mergeSameDay(calendar, list);
         //dumpList(list);
         mergeSameWork(list);
         //dumpList(list);
         convertToHours(list);
         //dumpList(list);
      }
   }

   /**
    * This method breaks down spans of time into individual days.
    *
    * @param calendar current project calendar
    * @param list list of assignment data
    */
   private void splitDays(ProjectCalendar calendar, LinkedList<TimephasedWork> list)
   {
      LinkedList<TimephasedWork> result = new LinkedList<TimephasedWork>();
      boolean remainderInserted = false;
      Calendar cal = Calendar.getInstance();

      for (TimephasedWork assignment : list)
      {
         if (remainderInserted)
         {
            cal.setTime(assignment.getStart());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            assignment.setStart(cal.getTime());
            remainderInserted = false;
         }

         while (assignment != null)
         {
            Date startDay = DateHelper.getDayStartDate(assignment.getStart());
            Date finishDay = DateHelper.getDayStartDate(assignment.getFinish());

            // special case - when the finishday time is midnight, it's really the previous day...
            if (assignment.getFinish().getTime() == finishDay.getTime())
            {
               cal.setTime(finishDay);
               cal.add(Calendar.DAY_OF_YEAR, -1);
               finishDay = cal.getTime();
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

                  cal.setTime(finishDay);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  Date remainderStart = cal.getTime();
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  Date remainderFinish = cal.getTime();

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

            TimephasedWork[] split = splitFirstDay(calendar, assignment);
            if (split[0] != null)
            {
               result.add(split[0]);
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
    * @return first day and remainder assignments
    */
   private TimephasedWork[] splitFirstDay(ProjectCalendar calendar, TimephasedWork assignment)
   {
      TimephasedWork[] result = new TimephasedWork[2];

      //
      // Retrieve data used to calculate the pro-rata work split
      //
      Date assignmentStart = assignment.getStart();
      Date assignmentFinish = assignment.getFinish();
      Duration calendarWork = calendar.getWork(assignmentStart, assignmentFinish, TimeUnit.MINUTES);
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
            Date splitStart = assignmentStart;
            Date splitFinishTime = calendar.getFinishTime(splitStart);
            splitFinish = DateHelper.setTime(splitStart, splitFinishTime);

            Duration calendarSplitWork = calendar.getWork(splitStart, splitFinish, TimeUnit.MINUTES);
            Duration calendarWorkPerDay = calendar.getWork(splitStart, TimeUnit.MINUTES);
            Duration assignmentWorkPerDay = assignment.getAmountPerDay();
            Duration splitWork;

            if (calendarSplitWork.durationComponentEquals(calendarWorkPerDay))
            {
               {
                  if (calendarSplitWork.durationComponentEquals(assignmentWorkPerDay))
                  {
                     splitWork = assignmentWorkPerDay;
                     splitMinutes = splitWork.getDuration();
                  }
                  else
                  {
                     splitMinutes = assignmentWorkPerDay.getDuration();
                     splitMinutes *= calendarSplitWork.getDuration();
                     splitMinutes /= (8 * 60); // this appears to be a fixed value
                     splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
                  }
               }
            }
            else
            {
               splitMinutes = assignmentWorkPerDay.getDuration();
               splitMinutes *= calendarSplitWork.getDuration();
               splitMinutes /= (8 * 60); // this appears to be a fixed value
               splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
            }

            TimephasedWork split = new TimephasedWork();
            split.setStart(splitStart);
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
   protected abstract void mergeSameDay(ProjectCalendar calendar, LinkedList<TimephasedWork> list);

   /**
    * Retrieves the pro-rata work carried out on a given day.
    *
    * @param calendar current calendar
    * @param assignment current assignment.
    * @return assignment work duration
    */
   private Duration getAssignmentWork(ProjectCalendar calendar, TimephasedWork assignment)
   {
      Date assignmentStart = assignment.getStart();

      Date splitStart = assignmentStart;
      Date splitFinishTime = calendar.getFinishTime(splitStart);
      Date splitFinish = DateHelper.setTime(splitStart, splitFinishTime);

      Duration calendarSplitWork = calendar.getWork(splitStart, splitFinish, TimeUnit.MINUTES);
      Duration assignmentWorkPerDay = assignment.getAmountPerDay();
      Duration splitWork;

      double splitMinutes = assignmentWorkPerDay.getDuration();
      splitMinutes *= calendarSplitWork.getDuration();
      splitMinutes /= (8 * 60); // this appears to be a fixed value
      splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
      return splitWork;
   }

   /*
      private void dumpList(LinkedList<TimephasedWork> list)
      {
         System.out.println();
         for (TimephasedWork assignment : list)
         {
            System.out.println(assignment);
         }
      }
   */

   private static final double EQUALITY_DELTA = 0.1;
}
