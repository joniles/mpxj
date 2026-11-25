/*
 * file:       TimephasedAdditionalHoursHelper
 * author:     Jon Iles
 * date:       2026-11-19
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.TemporaryCalendar;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.LocalDateTimeHelper;

/**
 * Helper methods used when handling the additional working
 * hours required to process actual timephased data.
 */
class TimephasedAdditionalHoursHelper
{
   /**
    * Read additional working hours and if necessary create a new
    * temporary calendar and add the additional hours as exceptions.
    *
    * @param originalCalendar original calendar
    * @param additionalWorkingHoursData additional working hours data
    * @param additionalWorkingHours list populated with additional working hours added to the calendar
    * @return new calendar instance (or orignal calendar if no hours added)
    */
   public static ProjectCalendar populateAdditionalWorkingHours(ProjectCalendar originalCalendar, byte[] additionalWorkingHoursData, List<LocalDateTimeRange> additionalWorkingHours)
   {
      // The additional working hours data block contains a 16 byte header, followed by 8 byte
      // records. The first two bytes of the header contain a count of the number of
      // records. Each Record contains two 8 byte timestamps representing the start
      // and end of a period of work carried out outside of standard working hours.
      int additionalWorkingHoursBlockCount = ByteArrayHelper.getShort(additionalWorkingHoursData, 0);
      int offset = 16;
      for (int blockIndex = 0; blockIndex < additionalWorkingHoursBlockCount; blockIndex++)
      {
         LocalDateTime start = MPPUtility.getTimestamp(additionalWorkingHoursData, offset);
         LocalDateTime end = MPPUtility.getTimestamp(additionalWorkingHoursData, offset + 4);
         List<LocalDateTimeRange> ranges = splitIntoDayRanges(start, end);
         ranges.forEach(r -> populateAdditionalWorkingHours(originalCalendar.getHours(r.getStart()), additionalWorkingHours, r));
         offset += 8;
      }

      Collections.sort(additionalWorkingHours);

      // Use the data we have just read to create a temporary calendar
      // and add exceptions to extend the working hours.
      return populateCalendar(originalCalendar, additionalWorkingHours);
   }

   /**
    * Given a start and end timestamp which defines a range potentially spanning multiple days,
    * split into a list of ranges each covering a single day.
    *
    * @param start start timestamp
    * @param end end timestamp
    * @return list of ranges
    */
   public static List<LocalDateTimeRange> splitIntoDayRanges(LocalDateTime start, LocalDateTime end)
   {
      List<LocalDateTimeRange> result = new ArrayList<>();

      LocalDateTime currentStart = start;
      while (!LocalDateTimeHelper.isSameDay(currentStart, end))
      {
         LocalDateTime currentEnd = LocalDateTime.of(currentStart.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
         result.add(new LocalDateTimeRange(currentStart, currentEnd));
         currentStart = currentEnd;
      }

      if (!currentStart.isEqual(end))
      {
         result.add(new LocalDateTimeRange(currentStart, end));
      }

      return result;
   }

   /**
    * Populates a list with time ranges representing additional working hours.
    * The goal of this method is to ensure that any additional working hours we
    * add to the list represent just non-working time during which work has been
    * performed. MS Project does give us this, but it also gives us ranges which
    * overlap working and non-working time, and ranges which are already working
    * time in the calendar.
    *
    * @param hours project calendar hours for the target day
    * @param list list to populate
    * @param newRange candidate range
    */
   public static void populateAdditionalWorkingHours(ProjectCalendarHours hours, List<LocalDateTimeRange> list, LocalDateTimeRange newRange)
   {
      if (!LocalDateTimeHelper.isSameDay(newRange.getStart(), newRange.getEnd()))
      {
         // We should not a range which cover multiple days
         throw new IllegalArgumentException("Additional working hours range covers multiple days: " + newRange);
      }

      if (hours.isEmpty())
      {
         // We have no hours, we can simply add the supplied range.
         list.add(newRange);
         return;
      }

      // There must be working time ranges for this day,
      // so ensure that our target range does not overlap
      // with any of these.
      LocalDate day = newRange.getStart().toLocalDate();
      LocalTime start = newRange.getStart().toLocalTime();
      LocalTime end = newRange.getEnd().toLocalTime();

      int index = 0;

      while (index < hours.size())
      {
         LocalTimeRange range = hours.get(index);

         // The start-end range is before the current working hours range, so we can add it and finish
         if (!end.equals(LocalTime.MIDNIGHT) && !end.isAfter(range.getStart()))
         {
            list.add(new LocalDateTimeRange(LocalDateTime.of(day, start), LocalDateTime.of(day, end)));
            break;
         }

         // The start-end range is after the current working hours range, so we'll find the next range
         if (!start.isBefore(range.getEnd()) && !range.getEnd().equals(LocalTime.MIDNIGHT))
         {
            ++index;
            if (index == hours.size())
            {
               // start-end range is after the last working hours range, so we can add it and finish
               list.add(new LocalDateTimeRange(LocalDateTime.of(day, start), LocalDateTime.of(day, end)));
               break;
            }
            continue;
         }

         // The start-end range exactly matches the working hours range, so no action is required
         if (start.equals(range.getStart()) && end.equals(range.getEnd()))
         {
            break;
         }

         // The start-end range overlaps the start of a working hours range, add a new range and update start.
         if (start.isBefore(range.getStart()) && (end.equals(LocalTime.MIDNIGHT) || end.isAfter(range.getStart())))
         {
            list.add(new LocalDateTimeRange(LocalDateTime.of(day, start), LocalDateTime.of(day, range.getStart())));
            start = range.getStart();
            continue;
         }

         if (!start.isBefore(range.getStart())
            && ((end.equals(LocalTime.MIDNIGHT) && !range.getEnd().equals(LocalTime.MIDNIGHT))
            || !end.equals(LocalTime.MIDNIGHT) && !range.getEnd().equals(LocalTime.MIDNIGHT) && end.isAfter(range.getEnd())))
         {
            // The additional working hours overlap the end of the range
            start = range.getEnd();
            ++index;
            if (index == hours.size())
            {
               // start-end range is after the last working hours range, so we can add it and finish
               list.add(new LocalDateTimeRange(LocalDateTime.of(day, start), LocalDateTime.of(day, end)));
               break;
            }
            continue;
         }

         // The start-end range is entirely within an existing working hours range, so we don't need to add them.
         break;
      }
   }

   /**
    * Create a temporary calendar derived from the supplied calendar,
    * and add working time exceptions based on the supplied ranges.
    * NOTE: the supplied additional working hours ranges should not
    * overlap with existing working hours.
    *
    * @param parent target calendar
    * @param ranges new working time ranges
    * @return new temporary calendar
    */
   public static ProjectCalendar populateCalendar(ProjectCalendar parent, List<LocalDateTimeRange> ranges)
   {
      if (ranges.isEmpty())
      {
         return parent;
      }

      ProjectCalendar calendar = new TemporaryCalendar(parent.getProjectContext());
      calendar.setParent(parent);

      Map<LocalDate, List<LocalTimeRange>> map = createHoursMap(ranges);
      for (Map.Entry<LocalDate, List<LocalTimeRange>> entry : map.entrySet())
      {
         LocalDate date = entry.getKey();
         List<LocalTimeRange> hours = entry.getValue();

         ProjectCalendarHours existingHours = calendar.getHours(date);
         if (existingHours != null)
         {
            hours.addAll(existingHours);
         }

         sortAndMergeOverlappingHours(hours);

         ProjectCalendarException exception = calendar.addCalendarException(date);
         exception.addAll(hours);
      }

      return calendar;
   }

   /**
    * Given a list of date ranges representing non-working time
    * on which actual work has been recorded for a resource assignment,
    * create a Map which give a list of date ranges for each day on
    * which these additional working time periods are present.
    *
    * @param ranges wotking time ranges
    * @return map of working time ranges by day
    */
   public static Map<LocalDate, List<LocalTimeRange>> createHoursMap(List<LocalDateTimeRange> ranges)
   {
      Map<LocalDate, List<LocalTimeRange>> map = new HashMap<>();

      for (LocalDateTimeRange range : ranges)
      {
         if (!LocalDateTimeHelper.isSameDay(range.getStart(), range.getEnd()))
         {
            // We should not have ranges which cover multiple days
            throw new IllegalArgumentException("Additional working hours range covers multiple days: " + range);
         }

         List<LocalTimeRange> hours = map.computeIfAbsent(range.getStart().toLocalDate(), k -> new ArrayList<>());
         hours.add(new LocalTimeRange(range.getStart().toLocalTime(), range.getEnd().toLocalTime()));
      }

      return map;
   }

   /**
    * Takes a list of time ranges representing calendar working hours and additional working hours.
    * Ensure that any overlapping ranges are merged.
    *
    * @param hours list of time ranges
    */
   public static void sortAndMergeOverlappingHours(List<LocalTimeRange> hours)
   {
      if (hours.size() < 2)
      {
         return;
      }

      Collections.sort(hours);

      int index = 0;
      while (index < hours.size() - 1)
      {
         LocalTimeRange range1 = hours.get(index);
         LocalTimeRange range2 = hours.get(index + 1);
         if (range2.getStart().isAfter(range1.getEnd()) && !range1.getEnd().equals(LocalTime.MIDNIGHT))
         {
            ++index;
            continue;
         }

         LocalTime start = range1.getStart().isBefore(range2.getStart()) ? range1.getStart() : range2.getStart();
         LocalTime end = range1.getEnd().equals(LocalTime.MIDNIGHT) || range2.getEnd().equals(LocalTime.MIDNIGHT) ? LocalTime.MIDNIGHT : (range1.getEnd().isAfter(range2.getEnd()) ? range1.getEnd() : range2.getEnd());

         hours.remove(index);
         hours.remove(index);
         hours.add(index, new LocalTimeRange(start, end));
      }
   }

   /**
    * Split TimephasedWork items so that any work being carried out during time which is normally
    * non-working is represented as separate TimephasedWork items.
    *
    * @param originalCalendar calendar which will be used when working with this timephased data
    * @param items timewphased work items
    * @param ranges non-working time ranges on which work may have been performed
    * @return new list of TimephasedWork items, split to allow it to be used originalCalendar
    */
   public static List<TimephasedWork> splitItems(ProjectCalendar originalCalendar, List<TimephasedWork> items, List<LocalDateTimeRange> ranges)
   {
      if (ranges.isEmpty()
         || items.get(0).getStart().isAfter(ranges.get(ranges.size() - 1).getEnd())
         || items.get(items.size() - 1).getFinish().isBefore(ranges.get(0).getStart()))
      {
         return items;
      }

      List<TimephasedWork> result = new ArrayList<>();

      int currentItemIndex = 0;
      int currentRangeIndex = 0;

      LocalDateTimeRange range = ranges.get(currentRangeIndex);
      TimephasedWork item = items.get(currentItemIndex);

      while (currentItemIndex < items.size() && currentRangeIndex < ranges.size())
      {
         if (!item.getFinish().isAfter(range.getStart()))
         {
            // The current item finishes before the current range, move to the next item.
            result.add(item);
            ++currentItemIndex;
            if (currentItemIndex < items.size())
            {
               item = items.get(currentItemIndex);
            }
            else
            {
               item = null;
            }
            continue;
         }

         if (!item.getStart().isBefore(range.getEnd()))
         {
            // The current item is after the current range, move to the next range.
            ++currentRangeIndex;
            if (currentRangeIndex < ranges.size())
            {
               range = ranges.get(currentRangeIndex);
            }
            continue;
         }

         if (item.getStart().isBefore(range.getStart()))
         {
            // We have an overlap at the start of the range.
            // The work for the overlap is calculated using
            // the original calendar is this is outside
            // the additional hours.
            double newDurationInMinutes = originalCalendar.getWork(item.getStart(), range.getStart(), TimeUnit.MINUTES).getDuration();
            Duration newTotalAmount = Duration.getInstance((newDurationInMinutes * item.getAmountPerHour().getDuration()) / 60.0, TimeUnit.MINUTES);

            TimephasedWork newItem = new TimephasedWork();
            newItem.setStart(item.getStart());
            newItem.setFinish(range.getStart());
            newItem.setAmountPerHour(item.getAmountPerHour());
            newItem.setTotalAmount(newTotalAmount);
            result.add(newItem);

            Duration remainingTotalAmount = Duration.getInstance(item.getTotalAmount().getDuration() - newTotalAmount.getDuration(), TimeUnit.MINUTES);
            if (remainingTotalAmount.getDuration() < 0)
            {
               remainingTotalAmount = Duration.getInstance(0, TimeUnit.MINUTES);
            }
            item.setStart(range.getStart());
            item.setTotalAmount(remainingTotalAmount);

            continue;
         }

         if (item.getFinish().isAfter(range.getEnd()))
         {
            // We have an overlap at the end of the range.
            // The work for the overlap is calculated using
            // the elapsed time in the range as this is
            // non-working time in the original calendar.
            double newDurationInMinutes = item.getStart().until(range.getEnd(), ChronoUnit.SECONDS) / 60.0;
            Duration newTotalAmount = Duration.getInstance((newDurationInMinutes * item.getAmountPerHour().getDuration()) / 60.0, TimeUnit.MINUTES);

            TimephasedWork newItem = new TimephasedWork();
            newItem.setStart(item.getStart());
            newItem.setFinish(range.getEnd());
            newItem.setAmountPerHour(item.getAmountPerHour());
            newItem.setTotalAmount(newTotalAmount);
            result.add(newItem);

            Duration remainingTotalAmount = Duration.getInstance(item.getTotalAmount().getDuration() - newTotalAmount.getDuration(), TimeUnit.MINUTES);
            if (remainingTotalAmount.getDuration() < 0)
            {
               remainingTotalAmount = Duration.getInstance(0, TimeUnit.MINUTES);
            }

            item.setStart(range.getEnd());
            item.setTotalAmount(remainingTotalAmount);

            continue;
         }

         // The current item must fall withing the range
         result.add(item);
         ++currentItemIndex;
         if (currentItemIndex < items.size())
         {
            item = items.get(currentItemIndex);
         }
         else
         {
            item = null;
         }
      }

      if (item != null)
      {
         result.add(item);
         ++currentItemIndex;
      }

      while (currentItemIndex < items.size())
      {
         result.add(items.get(currentItemIndex++));
      }

      return result;
   }
}
