/*
 * file:       MPPTimephasedBaselineCostNormaliser.java
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedCost;
import net.sf.mpxj.common.LocalDateHelper;
import net.sf.mpxj.common.LocalDateTimeHelper;
import net.sf.mpxj.common.LocalTimeHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.TimephasedNormaliser;

/**
 * Common implementation detail for normalisation.
 */
public class MPPTimephasedBaselineCostNormaliser implements TimephasedNormaliser<TimephasedCost>
{
   /**
    * This method converts the internal representation of timephased
    * resource assignment data used by MS Project into a standardised
    * format to make it easy to work with.
    *
    * @param assignment current resource assignment
    * @param list list of assignment data
    */
   @Override public void normalise(ResourceAssignment assignment, List<TimephasedCost> list)
   {
      if (!list.isEmpty())
      {
         //dumpList(list);
         splitDays(getCalendar(assignment), list);
         //dumpList(list);
         mergeSameDay(list);
         //dumpList(list);
         mergeSameCost(list);
         //dumpList(list);
      }
   }

   protected ProjectCalendar getCalendar(ResourceAssignment assignment)
   {
      return assignment.getParentFile().getBaselineCalendar();
   }

   /**
    * This method breaks down spans of time into individual days.
    *
    * @param calendar current project calendar
    * @param list list of assignment data
    */
   private void splitDays(ProjectCalendar calendar, List<TimephasedCost> list)
   {
      List<TimephasedCost> result = new ArrayList<>();
      boolean remainderInserted = false;

      for (TimephasedCost assignment : list)
      {
         if (remainderInserted)
         {
            assignment.setStart(assignment.getStart().plusDays(1));
            remainderInserted = false;
         }

         while (assignment != null)
         {
            LocalDateTime startDay = LocalDateTimeHelper.getDayStartDate(assignment.getStart());
            LocalDateTime finishDay = LocalDateTimeHelper.getDayStartDate(assignment.getFinish());

            // special case - when the finishday time is midnight, it's really the previous day...
            if (assignment.getFinish().equals(finishDay))
            {
               finishDay = finishDay.minusDays(1);
            }

            if (startDay.equals(finishDay))
            {
               result.add(assignment);
               break;
            }

            TimephasedCost[] split = splitFirstDay(calendar, assignment);
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
   private TimephasedCost[] splitFirstDay(ProjectCalendar calendar, TimephasedCost assignment)
   {
      TimephasedCost[] result = new TimephasedCost[2];

      //
      // Retrieve data used to calculate the pro-rata work split
      //
      LocalDateTime assignmentStart = assignment.getStart();
      LocalDateTime assignmentFinish = assignment.getFinish();
      Duration calendarWork = calendar.getWork(assignmentStart, assignmentFinish, TimeUnit.MINUTES);

      if (calendarWork.getDuration() != 0)
      {
         //
         // Split the first day
         //
         LocalDateTime splitFinish;
         double splitCost;
         LocalDate assignmentStartAsLocalDate = LocalDateHelper.getLocalDate(assignmentStart);
         if (calendar.isWorkingDate(assignmentStartAsLocalDate))
         {
            splitFinish = LocalTimeHelper.setEndTime(assignmentStart, calendar.getFinishTime(assignmentStartAsLocalDate));
            Duration calendarSplitWork = calendar.getWork(assignmentStart, splitFinish, TimeUnit.MINUTES);
            splitCost = (assignment.getTotalAmount().doubleValue() * calendarSplitWork.getDuration()) / calendarWork.getDuration();

            TimephasedCost split = new TimephasedCost();
            split.setStart(assignmentStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(Double.valueOf(splitCost));

            result[0] = split;
         }
         else
         {
            splitFinish = assignmentStart;
            splitCost = 0;
         }

         //
         // Split the remainder
         //
         LocalDateTime splitStart = calendar.getNextWorkStart(splitFinish);
         splitFinish = assignmentFinish;
         TimephasedCost split;
         if (splitStart.isAfter(splitFinish))
         {
            split = null;
         }
         else
         {
            splitCost = assignment.getTotalAmount().doubleValue() - splitCost;

            split = new TimephasedCost();
            split.setStart(splitStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(Double.valueOf(splitCost));
            split.setAmountPerDay(assignment.getAmountPerDay());
         }

         result[1] = split;
      }
      return result;
   }

   /**
    * This method merges together assignment data for the same day.
    *
    * @param list assignment data
    */
   private void mergeSameDay(List<TimephasedCost> list)
   {
      List<TimephasedCost> result = new ArrayList<>();

      TimephasedCost previousAssignment = null;
      for (TimephasedCost assignment : list)
      {
         if (previousAssignment != null)
         {
            LocalDateTime previousAssignmentStart = previousAssignment.getStart();
            LocalDateTime previousAssignmentStartDay = LocalDateTimeHelper.getDayStartDate(previousAssignmentStart);
            LocalDateTime assignmentStart = assignment.getStart();
            LocalDateTime assignmentStartDay = LocalDateTimeHelper.getDayStartDate(assignmentStart);

            if (previousAssignmentStartDay.equals(assignmentStartDay))
            {
               result.remove(result.size() - 1);

               double cost = previousAssignment.getTotalAmount().doubleValue();
               cost += assignment.getTotalAmount().doubleValue();

               TimephasedCost merged = new TimephasedCost();
               merged.setStart(previousAssignment.getStart());
               merged.setFinish(assignment.getFinish());
               merged.setTotalAmount(Double.valueOf(cost));
               assignment = merged;
            }

         }
         assignment.setAmountPerDay(assignment.getTotalAmount());
         result.add(assignment);

         previousAssignment = assignment;
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * This method merges together assignment data for the same cost.
    *
    * @param list assignment data
    */
   protected void mergeSameCost(List<TimephasedCost> list)
   {
      List<TimephasedCost> result = new ArrayList<>();

      TimephasedCost previousAssignment = null;
      for (TimephasedCost assignment : list)
      {
         if (previousAssignment == null)
         {
            assignment.setAmountPerDay(assignment.getTotalAmount());
            result.add(assignment);
         }
         else
         {
            Number previousAssignmentCost = previousAssignment.getAmountPerDay();
            Number assignmentCost = assignment.getTotalAmount();

            if (NumberHelper.equals(previousAssignmentCost.doubleValue(), assignmentCost.doubleValue(), 0.01))
            {
               LocalDateTime assignmentStart = previousAssignment.getStart();
               LocalDateTime assignmentFinish = assignment.getFinish();
               double total = previousAssignment.getTotalAmount().doubleValue();
               total += assignmentCost.doubleValue();

               TimephasedCost merged = new TimephasedCost();
               merged.setStart(assignmentStart);
               merged.setFinish(assignmentFinish);
               merged.setAmountPerDay(assignmentCost);
               merged.setTotalAmount(Double.valueOf(total));

               result.remove(result.size() - 1);
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

   /*
   private void dumpList(List<TimephasedCost> list)
   {
      System.out.println();
      for (TimephasedCost assignment : list)
      {
         System.out.println(assignment);
      }
   }
   */

}
