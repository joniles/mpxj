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
   public static List<Duration> segmentWork(ProjectCalendar calendar, List<TimephasedWork> work, List<LocalDateTimeRange> ranges, TimeUnit targetUnits)
   {
      validateTimephasedWork(work);
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


      return Arrays.stream(result).mapToObj(d -> d == -1 ? null : Duration.getInstance(d, units).convertUnits(targetUnits, calendar)).collect(Collectors.toList());
   }

   /**
    * This is the main entry point used to convert the internal representation
    * of timephased cost into an external form which can
    * be displayed to the user.
    *
    * @param calendar calendar used by the resource assignment
    * @param cost timephased resource assignment data
    * @param ranges timescale date ranges
    * @return list of costs, one per timescale date range
    */
   public static List<Number> segmentCost(ProjectCalendar calendar, List<TimephasedCost> cost, List<LocalDateTimeRange> ranges)
   {
      validateTimephasedCost(cost);
      validateRanges(ranges);

      if (cost.isEmpty())
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      // We use -1 to represent null and map this later when we generate
      double[] result = new double[ranges.size()];
      Arrays.fill(result, -1);

      int currentItemIndex = 0;
      TimephasedCost currentItem = cost.get(0);
      boolean currentItemIsNonWorking = itemIsNonWorking(calendar, currentItem);
      double currentItemCostPerHour = currentItem.getAmountPerHour().doubleValue();

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
            if (currentItemIndex+1 == cost.size())
            {
               // There are no more work items, so there is no work for this range
               // or any subsequent ranges.
               currentRangeIndex = -1;
               break;
            }

            // Try the next work item
            currentItem = cost.get(++currentItemIndex);
            currentItemIsNonWorking = itemIsNonWorking(calendar, currentItem);
            currentItemCostPerHour = currentItem.getAmountPerHour().doubleValue();
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
            double costAmount = currentItemCostPerHour * workHours;
            double currentRangeWork = result[currentRangeIndex] == -1 ? 0 : result[currentRangeIndex];
            result[currentRangeIndex] = currentRangeWork + costAmount;
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

      return Arrays.stream(result).mapToObj(d -> d == -1 ? null : Double.valueOf(d)).collect(Collectors.toList());
   }


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

   private static void validateTimephasedWork(List<TimephasedWork> items)
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

   private static void validateTimephasedCost(List<TimephasedCost> items)
   {
      if (items.isEmpty())
      {
         return;
      }

      TimephasedWork previousItem = null;

      for (TimephasedCost item : items)
      {
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
