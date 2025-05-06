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

package org.mpxj.mpp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import org.mpxj.Duration;
import org.mpxj.TimePeriodEntity;
import org.mpxj.ProjectCalendar;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedCost;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.LocalTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.TimephasedNormaliser;

/**
 * Common implementation detail for normalisation.
 */
public final class MPPTimephasedBaselineCostNormaliser implements TimephasedNormaliser<TimephasedCost>
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MPPTimephasedBaselineCostNormaliser()
   {

   }

   /**
    * This method converts the internal representation of timephased
    * data used by MS Project into a standardised
    * format to make it easy to work with.
    *
    * @param parent parent entity
    * @param list list of timephased data
    */
   @Override public void normalise(ProjectCalendar calendar, TimePeriodEntity parent, List<TimephasedCost> list)
   {
      if (!list.isEmpty())
      {
         //dumpList(list);
         splitDays(calendar, list);
         //dumpList(list);
         mergeSameDay(list);
         //dumpList(list);
         mergeSameCost(list);
         //dumpList(list);
      }
   }

   /**
    * This method breaks down spans of time into individual days.
    *
    * @param calendar current project calendar
    * @param list list of timephased data
    */
   private void splitDays(ProjectCalendar calendar, List<TimephasedCost> list)
   {
      List<TimephasedCost> result = new ArrayList<>();

      for (TimephasedCost item : list)
      {
         while (item != null)
         {
            LocalDateTime startDay = LocalDateTimeHelper.getDayStartDate(item.getStart());
            LocalDateTime finishDay = LocalDateTimeHelper.getDayStartDate(item.getFinish());

            // special case - when the finishday time is midnight, it's really the previous day...
            if (item.getFinish().equals(finishDay))
            {
               finishDay = finishDay.minusDays(1);
            }

            if (startDay.equals(finishDay))
            {
               result.add(item);
               break;
            }

            TimephasedCost[] split = splitFirstDay(calendar, item);
            if (split[0] != null)
            {
               result.add(split[0]);
            }

            if (item.equals(split[1]))
            {
               break;
            }

            item = split[1];
         }
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * This method splits the first day off of a time span.
    *
    * @param calendar current calendar
    * @param item timephased item
    * @return first day and remainder timephased items
    */
   private TimephasedCost[] splitFirstDay(ProjectCalendar calendar, TimephasedCost item)
   {
      TimephasedCost[] result = new TimephasedCost[2];

      //
      // Retrieve data used to calculate the pro-rata work split
      //
      LocalDateTime itemStart = item.getStart();
      LocalDateTime itemFinish = item.getFinish();
      Duration calendarWork = calendar.getWork(itemStart, itemFinish, TimeUnit.MINUTES);

      if (calendarWork.getDuration() != 0)
      {
         //
         // Split the first day
         //
         LocalDateTime splitFinish;
         double splitCost;
         LocalDate itemStartAsLocalDate = LocalDateHelper.getLocalDate(itemStart);
         if (calendar.isWorkingDate(itemStartAsLocalDate))
         {
            splitFinish = LocalTimeHelper.setEndTime(itemStart, calendar.getFinishTime(itemStartAsLocalDate));
            Duration calendarSplitWork = calendar.getWork(itemStart, splitFinish, TimeUnit.MINUTES);
            splitCost = (item.getTotalAmount().doubleValue() * calendarSplitWork.getDuration()) / calendarWork.getDuration();

            TimephasedCost split = new TimephasedCost();
            split.setStart(itemStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(Double.valueOf(splitCost));

            result[0] = split;
         }
         else
         {
            splitFinish = itemStart;
            splitCost = 0;
         }

         //
         // Split the remainder
         //
         LocalDateTime splitStart = calendar.getNextWorkStart(splitFinish);
         splitFinish = itemFinish;
         TimephasedCost split;
         if (splitStart.isAfter(splitFinish))
         {
            split = null;
         }
         else
         {
            splitCost = item.getTotalAmount().doubleValue() - splitCost;

            split = new TimephasedCost();
            split.setStart(splitStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(Double.valueOf(splitCost));
            split.setAmountPerDay(item.getAmountPerDay());
         }

         result[1] = split;
      }
      return result;
   }

   /**
    * This method merges together data for the same day.
    *
    * @param list timephased data
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
   private void mergeSameCost(List<TimephasedCost> list)
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

   public static final MPPTimephasedBaselineCostNormaliser INSTANCE = new MPPTimephasedBaselineCostNormaliser();
}
