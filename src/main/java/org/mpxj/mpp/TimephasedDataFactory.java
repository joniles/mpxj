/*
 * file:       TimephasedDataFactory
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       25/10/2008
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
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.TemporaryCalendar;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedWork;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.TimephasedHelper;

/**
 * This class contains methods to create lists of TimephasedWork
 * and TimephasedCost instances.
 */
final class TimephasedDataFactory
{
   /**
    * Given a block of data representing completed work, this method will
    * retrieve a set of TimephasedWork instances which represent
    * the day by day work carried out for a specific resource assignment.
    *
    * @param resourceAssignment resource assignment
    * @param regularData completed work data block
    * @param additionalWorkingHoursData actual work in non-working time data
    * @return list of TimephasedWork instances
    */
   public List<TimephasedWork> getCompleteWork(ResourceAssignment resourceAssignment, byte[] regularData, byte[] additionalWorkingHoursData)
   {
      ProjectCalendar workingCalendar = TimephasedHelper.getEffectiveCalendarForActualTimephasedWork(resourceAssignment);
      if (workingCalendar == null || regularData == null || regularData.length <= 26 || ByteArrayHelper.getShort(regularData, 0) == 0 || resourceAssignment.getTask().getDuration() == null || resourceAssignment.getTask().getDuration().getDuration() == 0)
      {
         // Rather than Colection.emptyList() we're returning a mutable list.
         // For the moment this will allow changes to be applied to the timephased data.
         // We may look at organizing this differently later.
         return new ArrayList<>();
      }

      // Actual/Completed Work is represented by two blocks of data. The first block
      // contains working time, the second block contains additions to the normal
      // calendar to add working time for hours working during what would normally
      // be non-working time. These additional allow the working time in the first
      // block to be interpreted correctly.
      ProjectCalendar originalCalendar = workingCalendar;
      List<LocalDateTimeRange> additionalWorkingHours = new ArrayList<>();
      if (additionalWorkingHoursData != null)
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
            additionalWorkingHours.add(new LocalDateTimeRange(start, end));
            offset += 8;
         }

         Collections.sort(additionalWorkingHours);

         // Use the data we have just read to create a temporary calendar
         // and add exceptions to extend the working hours.
         workingCalendar = populateCalendar(workingCalendar, additionalWorkingHours);
      }

      // The regular data block starts with a 16 byte header. The first
      // two bytes of the header indicate the number of records. Following the
      // header are 20 byte records. Each record contains the following items.
      // Offset 0: double representing cumulative work at the end of this period (1000ths/minute)
      // Offset 8: double representing work per hour this period (10000ths/hour)
      // Offset 16: int representing elapsed minutes at period end (80ths/minute)
      // The first block appears to contain totals for the resource assignment, and is skipped.
      LocalDateTime calendarPeriodStart = resourceAssignment.getStart();
      LocalDateTime assignmentLatestDate = getAssignmentLatestDate(resourceAssignment);

      long totalWorkSeconds = 0;
      long elapsedSeconds = 0;

      List<TimephasedWork> regularList = new ArrayList<>();
      int regularBlockCount = ByteArrayHelper.getShort(regularData, 0);
      double totalElapsedTime = ByteArrayHelper.getInt(regularData, 24);
      long totalElapsedTimeInSeconds = Math.round((totalElapsedTime * 60.0)/ 80.0);
      int offset = 36;

      for (int count = 0; count < regularBlockCount; count++)
      {
         double totalAtPeriodEnd = MPPUtility.getDouble(regularData, offset);
         double workPerHour = MPPUtility.getDouble(regularData, offset + 8);
         double elapsedAtPeriodEnd = ByteArrayHelper.getInt(regularData, offset + 16);

         double workPerHourInMinutes = (workPerHour * 60.0) / 1000.0;
         long totalWorkSecondsAtPeriodEnd = Math.round((totalAtPeriodEnd * 60.0)/ 1000.0);
         long elapsedSecondsAtPeriodEnd = Math.round((elapsedAtPeriodEnd * 60.0)/ 80.0);
         if (elapsedSecondsAtPeriodEnd < 0 || elapsedSecondsAtPeriodEnd > totalElapsedTimeInSeconds)
         {
            elapsedSecondsAtPeriodEnd = 0;
         }

         long totalWorkSecondsThisPeriod = totalWorkSecondsAtPeriodEnd - totalWorkSeconds;
         long elapsedSecondsThisPeriod = elapsedSecondsAtPeriodEnd - elapsedSeconds;

         LocalDateTime calendarPeriodEnd = workingCalendar.getDate(calendarPeriodStart, Duration.getInstance(elapsedSecondsThisPeriod / 60.0, TimeUnit.MINUTES));
         if (calendarPeriodEnd.isAfter(assignmentLatestDate))
         {
            calendarPeriodEnd = assignmentLatestDate;
         }

         double calculatedWorkPerHour = (totalWorkSecondsThisPeriod * 60.0) / elapsedSecondsThisPeriod;

         TimephasedWork item = new TimephasedWork();
         item.setStart(calendarPeriodStart);
         item.setFinish(calendarPeriodEnd);
         item.setTotalAmount(Duration.getInstance(totalWorkSecondsThisPeriod / 60.0, TimeUnit.MINUTES));
         item.setAmountPerHour(Duration.getInstance(calculatedWorkPerHour, TimeUnit.MINUTES));
         regularList.add(item);

         if (calendarPeriodEnd.isEqual(assignmentLatestDate))
         {
            break;
         }

         totalWorkSeconds = totalWorkSecondsAtPeriodEnd;
         elapsedSeconds = elapsedSecondsAtPeriodEnd;
         calendarPeriodStart = workingCalendar.getNextWorkStart(regularList.get(regularList.size() - 1).getFinish());

         // Stop reading if the timephased data runs past the end of the assignment.
         if (calendarPeriodStart.isAfter(assignmentLatestDate))
         {
            break;
         }

         offset += 20;
      }

      // return an empty list if we have no actual work
      double total = regularList.stream().mapToDouble(w -> w.getTotalAmount().getDuration()).sum();
      if (total == 0.0)
      {
         regularList.clear();
         return regularList;
      }

      // At this point our timephased items require the temporary calendar we have
      // created which includes exceptional working hours to allow work in what
      // would otherwise be non-working time. To avoid having to maintain a copy
      // of this calendar, we'll split the data so that it works
      // with the calendars we already have on the project. This also ensures that the code we use
      // to segment this data will work correctly.
      regularList = splitItems(originalCalendar, regularList, additionalWorkingHours);

      // Remove any items with show zero hours work in non-working time.
      regularList.removeIf(w -> w.getTotalAmount().getDuration() == 0.0 && originalCalendar.getWork(w.getStart(), w.getFinish(), TimeUnit.MINUTES).getDuration() == 0.0);

      // Remove any items with the same start and end dates
      return removeEmptyItems(regularList);
   }

   /**
    * Retune the latest date we'd expect to find actual work
    * for this resource assignment. Typically, this will be the
    * Resume date for the assignment, but in some cases this is
    * not present so we'll fall back on the actual finish and
    * finish dates.
    *
    * @param assignment target resource assignment
    * @return latest date for actual work
    */
   private LocalDateTime getAssignmentLatestDate(ResourceAssignment assignment)
   {
      LocalDateTime result = assignment.getResume();
      if (result != null)
      {
         return result;
      }

      result = assignment.getActualFinish();
      if (result != null)
      {
         return result;
      }

      return assignment.getFinish();
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
   private Map<LocalDate, List<LocalTimeRange>> createHoursMap(List<LocalDateTimeRange> ranges)
   {
      Map<LocalDate, List<LocalTimeRange>> map = new HashMap<>();

      for (LocalDateTimeRange range : ranges)
      {
         LocalDateTime startDate = range.getStart();
         LocalDateTime endDate = range.getEnd();

         while (true)
         {
            LocalTime startTime = startDate.toLocalTime();
            LocalTime endTime;
            if (endDate.toLocalDate().isEqual(startDate.toLocalDate()))
            {
               endTime = endDate.toLocalTime();
            }
            else
            {
               endTime = LocalTime.MIDNIGHT;
            }

            List<LocalTimeRange> hours = map.computeIfAbsent(startDate.toLocalDate(), k -> new ArrayList<>());
            hours.add(new LocalTimeRange(startTime, endTime));

            if (endDate.toLocalDate().isEqual(startDate.toLocalDate()))
            {
               break;
            }

            startDate = LocalDateTime.of(startDate.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
         }
      }

      return map;
   }

   /**
    * Create a temporary calendar derived from the supplied calendar,
    * and add working time exceptions based on the supplied ranges.
    *
    * @param parent target calendar
    * @param ranges new working time ranges
    * @return new temporary calendar
    */
   private ProjectCalendar populateCalendar(ProjectCalendar parent, List<LocalDateTimeRange> ranges)
   {
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
         Collections.sort(hours);

         // Check for overlaps
         if (hours.size() > 1)
         {
            int index = 0;
            while(index + 1 < hours.size())
            {
               LocalTimeRange hours1 = hours.get(index);
               LocalTimeRange hours2 = hours.get(index+1);

               if (!hours2.getStart().isBefore(hours1.getEnd()))
               {
                  ++index;
                  continue;
               }

               LocalTimeRange newHours = new LocalTimeRange(hours1.getStart(), hours2.getEnd());
               hours.remove(index);
               hours.remove(index);
               hours.add(index, newHours);
            }
         }

         ProjectCalendarException exception = calendar.addCalendarException(date);
         exception.addAll(hours);
      }

      return calendar;
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
   private List<TimephasedWork> splitItems(ProjectCalendar originalCalendar, List<TimephasedWork> items, List<LocalDateTimeRange> ranges)
   {
      if (ranges.isEmpty()
         || items.get(0).getStart().isAfter(ranges.get(ranges.size()-1).getEnd())
         || items.get(items.size()-1).getFinish().isBefore(ranges.get(0).getStart()))
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

            Duration remainingTotalAmount = Duration.getInstance(item.getTotalAmount().getDuration()-newTotalAmount.getDuration(), TimeUnit.MINUTES);
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

            Duration remainingTotalAmount = Duration.getInstance(item.getTotalAmount().getDuration()-newTotalAmount.getDuration(), TimeUnit.MINUTES);
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

   /**
    * Given a block of data representing planned work, this method will
    * retrieve a set of TimephasedWork instances which represent
    * the day by day work planned for a specific resource assignment.
    *
    * @param calendar calendar on which date calculations are based
    * @param assignment resource assignment
    * @param data planned work data block
    * @param timephasedComplete list of complete work
    * @param resourceType resource type
    * @return list of TimephasedWork instances
    */
   public List<TimephasedWork> getPlannedWork(ProjectCalendar calendar, ResourceAssignment assignment, byte[] data, List<TimephasedWork> timephasedComplete, ResourceType resourceType)
   {
      if (data == null || data.length < 24 || assignment.getTask().getDuration() == null || assignment.getTask().getDuration().getDuration() == 0)
      {
         return new ArrayList<>();
      }

      // The timephased planned work data has a 16 byte header, followed by 28 byte blocks.
      // The first two bytes of the header are a count of the number of blocks. The first
      // block is a summary block, which is not counted, so a count of N means there are N+1 blocks.
      // Each 28 byte block contains the following:
      // Offset 0: 8 byte double - cumulative work (1000th/minute)
      // Offset 8: 8 byte double - hours per day (20000th/hour) unreliable value, not used
      // Offset 16: 8 byte double? - unknown
      // Offset 24: 4 byte int - cumulative elapsed minutes (80th/minute)
      List<TimephasedWork> newList = new ArrayList<>();
      int blockCount = ByteArrayHelper.getShort(data, 0);

      if (blockCount == 0)
      {
         // If we have a block count of zero, there is just one entry for the whole assignment.
         // We read the values for this entry from this summary block.
         // If the total work for the block is zero it's not valid so we skip it.
         long totalWorkInSeconds = Math.round(MPPUtility.getDouble(data, 16) * 60.0 / 1000.0);
         if (totalWorkInSeconds != 0.0)
         {
            // MS Project always populates Resume, even for unstarted tasks
            LocalDateTime end = assignment.getFinish();
            LocalDateTime start = assignment.getResume();
            if (end.isBefore(start))
            {
               // In some cases Resume doesn't make sense, so fall back on Start
               start = assignment.getStart();
            }

            Duration work = Duration.getInstance(totalWorkInSeconds / 60.0, TimeUnit.MINUTES);
            long assignmentElapsedSeconds = Math.round(calendar.getWork(start, end, TimeUnit.MINUTES).getDuration() * 60.0);
            double calculatedWorkPerHour = (totalWorkInSeconds * 60.0) / assignmentElapsedSeconds;
            Duration workPerHour = Duration.getInstance(calculatedWorkPerHour, TimeUnit.MINUTES);

            TimephasedWork item = new TimephasedWork();
            item.setStart(start);
            item.setFinish(end);
            item.setTotalAmount(work);
            item.setAmountPerHour(workPerHour);
            newList.add(item);
         }
      }
      else
      {
         // We have block data, we ignore the summary block and generate an entry for each subsequent block.
         int offset = 16 + 28; // skip the summary block
         long previousWorkSeconds = 0;
         long previousElapsedSeconds = 0;

         // MS Project always populates Resume, even for unstarted tasks
         LocalDateTime start = assignment.getResume();

         for (int count = 0; count < blockCount; count++)
         {
            if (offset + 28 > data.length)
            {
               // Bail out if we don't have all the data for the block
               break;
            }

            long cumulativeWorkSeconds = Math.round((MPPUtility.getDouble(data, offset) * 60.0)/ 1000.0);
            long workSecondsThisPeriod = cumulativeWorkSeconds - previousWorkSeconds;
            long cumulativeElapsedSeconds = Math.round((ByteArrayHelper.getInt(data, offset + 24) * 60.0)/ 80.0);
            long elapsedSecondsThisPeriod = cumulativeElapsedSeconds - previousElapsedSeconds;
            Duration workPerHour = Duration.getInstance(workSecondsThisPeriod * 60.0 / elapsedSecondsThisPeriod, TimeUnit.MINUTES);
            LocalDateTime end = calendar.getDate(start, Duration.getInstance(elapsedSecondsThisPeriod / 60.0, TimeUnit.MINUTES));
            Duration work = Duration.getInstance(workSecondsThisPeriod / 60.0, TimeUnit.MINUTES);

            // Occasionally we appear to have blocks which represent zero work and zero time - we skip these
            if (workSecondsThisPeriod >= 1 || !start.isEqual(end))
            {
               TimephasedWork item = new TimephasedWork();
               item.setStart(start);
               item.setFinish(end);
               item.setTotalAmount(work);
               item.setAmountPerHour(workPerHour);
               newList.add(item);
            }

            start = calendar.getNextWorkStart(end);
            previousWorkSeconds = cumulativeWorkSeconds;
            previousElapsedSeconds = cumulativeElapsedSeconds;

            offset += 28;
         }
      }

      return removeEmptyItems(newList);
   }

   /**
    * Read timephased work data which is not split between complete and planned.
    * Returns null if no timephased work is present, otherwise returns
    * a list of timephased work items.
    *
    * @param calendar calendar for date calculations
    * @param data timephased work data block
    * @return timephased work
    */
   public List<TimephasedWork> getBaselineWork(ProjectCalendar calendar, byte[] data)
   {
      if (data == null || data.length == 0)
      {
         return Collections.emptyList();
      }

      // Baseline work data is represented by an 8 byte header, followed by 20 byte blocks.
      // The first and last block appear to be summary blocks, and are ignored. The first
      // block after the summary is only used to provide the start timestamp for the data,
      // the remaining block up to the trailing summary block provide the timephased items.
      // The header contains the block count at offset 0.
      // The 20 byte blocks following the header contain:
      // Offset 0: cumulative work, including overtime, as a double (1000th/minute)
      // Offset 8: work in minutes for this period, excluding overtime, as an int (10th/minute)
      // Offset 12: unknown int, possibly a flag
      // Offset 16: end of period timestamp (10th/minute)
      // Note that the baseline timephased data is all relative to the project's baseline
      // calendar, and not the original resource assignment calendar.
      List<TimephasedWork> list = new ArrayList<>();
      int blockCount = ByteArrayHelper.getShort(data, 0);
      LocalDateTime start = MPPUtility.getTimestampFromTenths(data, 44);
      int offset = 48; // skip header and first two blocks
      double cumulativeWorkInMinutes = 0;

      for (int index = 0; index < blockCount - 2; index++)
      {
         double currentCumulativeWorkInMinutes = MPPUtility.getDouble(data, offset) / 1000.0;
         double workMinutesThisPeriod = currentCumulativeWorkInMinutes - cumulativeWorkInMinutes;
         LocalDateTime end = MPPUtility.getTimestampFromTenths(data, offset + 16);

         double workPerHour;
         if (workMinutesThisPeriod == 0)
         {
            workPerHour = 0;
         }
         else
         {
            double calendarWorkMinutesThisPeriod = calendar.getWork(start, end, TimeUnit.MINUTES).getDuration();
            if (calendarWorkMinutesThisPeriod == 0)
            {
               calendarWorkMinutesThisPeriod = start.until(end, ChronoUnit.MINUTES);
            }
            workPerHour = (workMinutesThisPeriod * 60.0) / calendarWorkMinutesThisPeriod;
         }

         TimephasedWork item = new TimephasedWork();
         item.setStart(start);
         item.setFinish(end);
         item.setTotalAmount(Duration.getInstance(workMinutesThisPeriod, TimeUnit.MINUTES));
         item.setAmountPerHour(Duration.getInstance(workPerHour, TimeUnit.MINUTES));
         list.add(item);

         start = end;
         cumulativeWorkInMinutes = currentCumulativeWorkInMinutes;
         offset += 20;
      }

      if (cumulativeWorkInMinutes == 0)
      {
         return Collections.emptyList();
      }

      return removeEmptyItems(list);
   }

   /**
    * Read timephased cost data which is not split between complete and planned.
    * Returns null if no timephased cost is present, otherwise returns
    * a list of timephased cost items.
    *
    * @param calendar calendar for date calculations
    * @param data timephased cost data block
    * @return timephased cost
    */
   public List<TimephasedCost> getCost(ProjectCalendar calendar, byte[] data)
   {
      if (data == null || data.length == 0)
      {
         return Collections.emptyList();
      }

      // Timephased baseline cost date is represented by a 16 byte header, followed by 20 byte blocks
      // The number of blocks is stored as a short at offset 0 in the header.
      // Each 20 byte block consists of:
      // Offset 0: flag? short int
      // Offset 2: unknown 6 bytes
      // Offset 8: cumulative cost as a double (100th/current unit)
      // Offset 16: end of period timestamp (10th/minute)
      // The first and last blocks contain summary data and are ignored. The start date is
      // obtained from the first summary block.
      List<TimephasedCost> list = new ArrayList<>();
      int blockCount = ByteArrayHelper.getShort(data, 0);
      LocalDateTime start = MPPUtility.getTimestampFromTenths(data, 32);
      double cumulativeCost = 0;
      int offset = 16 + 20; // skip header and first block

      for (int count = 0; count < blockCount - 2; count++)
      {
         LocalDateTime end = MPPUtility.getTimestampFromTenths(data, offset + 16);
         double cumulativeCostThisPeriod = MPPUtility.getDouble(data, offset + 8);
         double costThisPeriod = cumulativeCostThisPeriod - cumulativeCost;
         double costPerHour;

         if (costThisPeriod == 0)
         {
            costPerHour = 0;
         }
         else
         {
            double calendarWorkMinutesThisPeriod = calendar.getWork(start, end, TimeUnit.MINUTES).getDuration();
            if (calendarWorkMinutesThisPeriod == 0)
            {
               calendarWorkMinutesThisPeriod = start.until(end, ChronoUnit.MINUTES);
            }
            costPerHour = (costThisPeriod * 60.0) / calendarWorkMinutesThisPeriod;
         }

         TimephasedCost item = new TimephasedCost();
         item.setStart(start);
         item.setFinish(end);
         item.setTotalAmount(Double.valueOf(costThisPeriod / 100.0));
         item.setAmountPerHour(Double.valueOf(costPerHour / 100.0));
         list.add(item);

         cumulativeCost = cumulativeCostThisPeriod;
         offset += 20;
         start = end;
      }

      if (cumulativeCost == 0)
      {
         return Collections.emptyList();
      }

      return list;
   }

   /**
    * Remove items whose start and finish timestamps are the same.
    *
    * @param work list of timephased work items
    * @return list with empty items removed
    */
   private List<TimephasedWork> removeEmptyItems(List<TimephasedWork> work)
   {
      work.removeIf(w -> w.getStart().isEqual(w.getFinish()));
      return work;
   }
}
