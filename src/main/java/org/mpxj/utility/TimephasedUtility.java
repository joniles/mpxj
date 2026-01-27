/*
 * file:       TimephasedUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       2011-02-12
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

package org.mpxj.utility;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedItem;
import org.mpxj.TimephasedWork;
import org.mpxj.mpp.TimescaleUnits;

/**
 * This class contains methods relating to manipulating timephased data.
 */
public final class TimephasedUtility
{
   /**
    * This is the main entry point used to convert the internal representation
    * of timephased work into an external form which can
    * be displayed to the user.
    *
    * @param calendar calendar used by the resource assignment
    * @param work timephased resource assignment data
    * @param ranges timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public static List<Duration> segmentWork(ProjectCalendar calendar, List<TimephasedWork> work, List<LocalDateTimeRange> ranges)
   {
      validateTimephased(work);
      validateRanges(ranges);

      if (work.isEmpty())
      {
         return Arrays.asList(new Duration[ranges.size()]);
      }

      // We use -1 to represent null and map this later when we generate
      double[] result = new double[ranges.size()];
      Arrays.fill(result, -1);

      int currentItemIndex = 0;
      TimephasedWork currentItem = work.get(0);
      TimeUnit units = currentItem.getAmountPerHour().getUnits();
      boolean currentItemIsNonWorking = itemIsNonWorking(calendar, currentItem);
      double currentItemWorkPerHour = currentItem.getAmountPerHour().getDuration();

      int currentRangeIndex = 0;
      LocalDateTimeRange currentRange = ranges.get(0);

      while(true)
      {
         if (!currentRange.getEnd().isAfter(currentItem.getStart()))
         {
            // The range is before the current timephased item: there is no work for this range.
            currentRangeIndex++;
            if (currentRangeIndex == ranges.size())
            {
               break;
            }
            currentRange = ranges.get(currentRangeIndex);
            continue;
         }

         while (!currentRange.getStart().isBefore(currentItem.getFinish()))
         {
            // We are at the last work item
            if (currentItemIndex+1 == work.size())
            {
               // There are no more work items, so there is no work for this range
               // or any subsequent ranges.
               currentRangeIndex = -1;
               break;
            }

            // Try the next work item
            currentItem = work.get(++currentItemIndex);
            currentItemIsNonWorking = itemIsNonWorking(calendar, currentItem);
            currentItemWorkPerHour = currentItem.getAmountPerHour().getDuration();
         }

         if (currentRangeIndex == -1)
         {
            break;
         }

         // Our range intersects with this work
         LocalDateTime workStart = currentRange.getStart().isAfter(currentItem.getStart()) ? currentRange.getStart() : currentItem.getStart();
         LocalDateTime workFinish = currentRange.getEnd().isAfter(currentItem.getFinish()) ? currentItem.getFinish() : currentRange.getEnd();
         double workHours = currentItemIsNonWorking ? workStart.until(workFinish, ChronoUnit.HOURS) : calendar.getWork(workStart, workFinish, TimeUnit.HOURS).getDuration();
         if (workHours != 0.0)
         {
            double workAmount = currentItemWorkPerHour * workHours;
            double currentRangeWork = result[currentRangeIndex] == -1 ? 0 : result[currentRangeIndex];
            result[currentRangeIndex] = currentRangeWork + workAmount;
         }

         if (workFinish.isBefore(currentRange.getEnd()))
         {
            // We still have some time to account for in the current range
            currentRange = new LocalDateTimeRange(workFinish, currentRange.getEnd());
         }
         else
         {
            // We have completed the current range, move forward
            currentRangeIndex++;
            if (currentRangeIndex == ranges.size())
            {
               break;
            }
            currentRange = ranges.get(currentRangeIndex);
         }
      }

      return Arrays.stream(result).mapToObj(d -> d == -1 ? null : Duration.getInstance(d, units)).collect(Collectors.toList());
   }

   /**
    * This is the main entry point used to convert the internal representation
    * of timephased baseline work into an external form which can
    * be displayed to the user.
    *
    * @param calendar calendar to use for calculations
    * @param work timephased resource assignment data
    * @param rangeUnits timescale units
    * @param dateList timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public ArrayList<Duration> segmentBaselineWork(ProjectCalendar calendar, List<TimephasedWork> work, TimescaleUnits rangeUnits, List<LocalDateTimeRange> dateList)
   {
      throw new UnsupportedOperationException();
      //return segmentWork(calendar, work, rangeUnits, dateList);
   }

   /**
    * This is the main entry point used to convert the internal representation
    * of timephased cost into an external form which can
    * be displayed to the user.
    *
    * @param projectCalendar calendar used by the resource assignment
    * @param cost timephased resource assignment data
    * @param rangeUnits timescale units
    * @param dateList timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public ArrayList<Double> segmentCost(ProjectCalendar projectCalendar, List<TimephasedCost> cost, TimescaleUnits rangeUnits, List<LocalDateTimeRange> dateList)
   {
      throw new UnsupportedOperationException();
/*
      ArrayList<Double> result = new ArrayList<>(dateList.size());
      int lastStartIndex = 0;

      //
      // Iterate through the list of dates range we are interested in.
      // Each date range in this list corresponds to a column
      // shown on the "timescale" view by MS Project
      //
      for (LocalDateTimeRange range : dateList)
      {
         //
         // If the current date range does not intersect with any of the
         // assignment date ranges in the list, then we show a zero
         // duration for this date range.
         //
         int startIndex = lastStartIndex == -1 ? -1 : getStartIndex(range, cost, lastStartIndex);
         if (startIndex == -1)
         {
            result.add(NumberHelper.DOUBLE_ZERO);
         }
         else
         {
            //
            // We have found an assignment which intersects with the current
            // date range, call the method below to determine how
            // much time from this resource assignment can be allocated
            // to the current date range.
            //
            result.add(getRangeCost(projectCalendar, rangeUnits, range, cost, startIndex));
            lastStartIndex = startIndex;
         }
      }

      return result;
 */
   }

   /**
    * This is the main entry point used to convert the internal representation
    * of timephased baseline cost into an external form which can
    * be displayed to the user.
    *
    * @param calendar calendar to use for calculations
    * @param cost timephased resource assignment data
    * @param rangeUnits timescale units
    * @param dateList timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public ArrayList<Double> segmentBaselineCost(ProjectCalendar calendar, List<TimephasedCost> cost, TimescaleUnits rangeUnits, List<LocalDateTimeRange> dateList)
   {
      throw new UnsupportedOperationException();
      //return segmentCost(calendar, cost, rangeUnits, dateList);
   }

   /**
    * Used to locate the first timephased resource assignment block which
    * intersects with the target date range.
    *
    * @param <T> payload type
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start the search
    * @return index of timephased resource assignment which intersects with the target date range
    */
/*
   private <T extends TimephasedItem<?>> int getStartIndex(LocalDateTimeRange range, List<T> assignments, int startIndex)
   {
      int result = -1;
      if (assignments != null)
      {
         LocalDateTime rangeStart = range.getStart();
         LocalDateTime rangeEnd = range.getEnd();

         for (int loop = startIndex; loop < assignments.size(); loop++)
         {
            T assignment = assignments.get(loop);
            int compareResult = LocalDateTimeHelper.compare(assignment.getStart(), assignment.getFinish(), rangeStart);

            //
            // The start of the target range falls after the assignment end -
            // move on to test the next assignment.
            //
            if (compareResult > 0)
            {
               continue;
            }

            //
            // The start of the target range  falls within the assignment -
            // return the index of this assignment to the caller.
            //
            if (compareResult == 0)
            {
               result = loop;
               break;
            }

            //
            // At this point, we know that the start of the target range is before
            // the assignment start. We need to determine if the end of the
            // target range overlaps the assignment.
            //
            compareResult = LocalDateTimeHelper.compare(assignment.getStart(), assignment.getFinish(), rangeEnd);
            if (compareResult >= 0)
            {
               result = loop;
               break;
            }
         }
      }
      return result;
   }
*/
   /**
    * For a given date range, determine the duration of work, based on the
    * timephased resource assignment data.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
/*
   private Duration getRangeDuration(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, LocalDateTimeRange range, List<TimephasedWork> assignments, int startIndex)
   {
      Duration result;

      switch (rangeUnits)
      {
         case MINUTES:
         case HOURS:
         {
            result = getRangeDurationSubDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }

         default:
         {
            result = getRangeDurationWholeDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }
      }

      return result;
   }
*/
   /**
    * For a given date range, determine the duration of work, based on the
    * timephased resource assignment data.
    *
    * This method deals with timescale units of less than a day.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
/*
   private Duration getRangeDurationSubDay(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, LocalDateTimeRange range, List<TimephasedWork> assignments, int startIndex)
   {
      throw new UnsupportedOperationException("Please request this functionality from the MPXJ maintainer");
   }
*/
   /**
    * For a given date range, determine the duration of work, based on the
    * timephased resource assignment data.
    *
    * This method deals with timescale units of one day or more.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
/*
   private Duration getRangeDurationWholeDay(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, LocalDateTimeRange range, List<TimephasedWork> assignments, int startIndex)
   {
      // option 1:
      // Our date range starts before the start of the TRA at the start index.
      // We can guarantee that we don't need to look at any earlier TRA blocks so just start here

      // option 2:
      // Our date range starts at the same point as the first TRA: do nothing...

      // option 3:
      // Our date range starts somewhere inside the first TRA...

      // if it's option 1 just set the start date to the start of the TRA block
      // for everything else we just use the start date of our date range.
      // start counting forwards one day at a time until we reach the end of
      // the date range, or until we reach the end of the block.

      // if we have not reached the end of the range, move to the next block and
      // see if the date range overlaps it. if it does not overlap, then we're
      // done.

      // if it does overlap, then move to the next block and repeat

      int totalDays = 0;
      double totalWork = 0;
      TimephasedWork assignment = assignments.get(startIndex);
      boolean done;

      do
      {
         //
         // Select the correct start date
         //
         LocalDateTime startDate = range.getStart();
         LocalDateTime assignmentStart = assignment.getStart();
         if (startDate.isBefore(assignmentStart))
         {
            startDate = assignmentStart;
         }

         LocalDateTime rangeEndDate = range.getEnd();
         LocalDateTime traEndDate = assignment.getFinish();
         LocalDateTime calendarDate = startDate;

         //
         // Start counting forwards
         //
         while (startDate.isBefore(rangeEndDate) && startDate.isBefore(traEndDate))
         {
            if (projectCalendar == null || projectCalendar.isWorkingDate(LocalDateHelper.getLocalDate(calendarDate)))
            {
               ++totalDays;
            }
            startDate = startDate.plusDays(1);
            calendarDate = startDate;
         }

         //
         // If we still haven't reached the end of our range
         // check to see if the next TRA can be used.
         //
         done = true;
         totalWork += (assignment.getAmountPerDay().getDuration() * totalDays);
         if (startDate.isBefore(rangeEndDate))
         {
            ++startIndex;
            if (startIndex < assignments.size())
            {
               assignment = assignments.get(startIndex);
               totalDays = 0;
               done = false;
            }
         }
      }
      while (!done);

      return Duration.getInstance(totalWork, assignment.getAmountPerDay().getUnits());
   }
*/
   /**
    * For a given date range, determine the cost, based on the
    * timephased resource assignment data.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
/*
   private Double getRangeCost(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, LocalDateTimeRange range, List<TimephasedCost> assignments, int startIndex)
   {
      Double result;

      switch (rangeUnits)
      {
         case MINUTES:
         case HOURS:
         {
            result = getRangeCostSubDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }

         default:
         {
            result = getRangeCostWholeDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }
      }

      return result;
   }
*/


   private static void validateRanges(List<LocalDateTimeRange> ranges)
   {
      LocalDateTimeRange previousRange = null;
      for (LocalDateTimeRange range : ranges)
      {
         if (!range.getStart().isBefore(range.getEnd()))
         {
            throw new IllegalArgumentException("Range start must be before range end: " + range);
         }

         if (previousRange != null && !previousRange.getEnd().isBefore(range.getStart()))
         {
            throw new IllegalArgumentException("Ranges must be non-overlapping and in order: " + previousRange + " " + range);
         }
      }
   }

   private static void validateTimephased(List<TimephasedWork> items)
   {
      if (items.isEmpty())
      {
         return;
      }

      TimephasedWork previousItem = null;
      TimeUnit amountPerHourUnits = items.get(0).getAmountPerHour().getUnits();

      for (TimephasedWork item : items)
      {
         if (amountPerHourUnits != item.getAmountPerHour().getUnits())
         {
            throw new IllegalArgumentException("Timephased work per hour expressed in different units");
         }

         if (!item.getStart().isBefore(item.getFinish()))
         {
            throw new IllegalArgumentException("Item start must be before item end: " + item);
         }

         if (previousItem != null && !previousItem.getFinish().isBefore(item.getStart()))
         {
            throw new IllegalArgumentException("Items must be non-overlapping and in order: " + previousItem + " " + item);
         }
      }
   }

   private static <T extends TimephasedItem<?>> boolean itemIsNonWorking(ProjectCalendar calendar, T item)
   {
      return calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.HOURS).getDuration() == 0.0;
   }
}
