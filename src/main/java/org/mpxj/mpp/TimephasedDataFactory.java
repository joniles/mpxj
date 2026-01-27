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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedWork;
import org.mpxj.common.ByteArrayHelper;

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
    * @param calendar calendar on which date calculations are based
    * @param resourceAssignment resource assignment
    * @param regularData completed work data block
    * @param irregularData actual work in non-working time data
    * @return list of TimephasedWork instances
    */
   public List<TimephasedWork> getCompleteWork(ProjectCalendar calendar, ResourceAssignment resourceAssignment, byte[] regularData, byte[] irregularData)
   {
      if (calendar == null || regularData == null || regularData.length <= 26 || ByteArrayHelper.getShort(regularData, 0) == 0 || resourceAssignment.getTask().getDuration() == null || resourceAssignment.getTask().getDuration().getDuration() == 0)
      {
         // Rather than Colection.emptyList() we're returning a mutable list.
         // For the moment this will allow changes to be applied to the timephased data.
         // We may look at organizing this differently later.
         return new ArrayList<>();
      }

      // Actual/Completed Work is represented by two blocks of data. The first block
      // contains "regular" work, i.e. work which has been performed within normal
      // working hours. It will also contain work carried out outside of normal working
      // hours, however the presence of this type of "irregular" work will make the
      // start/end time for each subsequent period of work incorrect. The second block
      // of data records the correct start and end time for each of the periods of
      // irregular work. Combining these two sets of data allows us to produce the
      // correct start and end time for each period of work, whether they have been
      // performed inside or outside of normal working hours.

      List<LocalDateTimeRange> irregularRanges = new ArrayList<>();
      if (irregularData != null)
      {
         // The irregular data block contains a 16 byte header, followed by 8 byte
         // records. The first two bytes of the header contain a count of the number of
         // records. Each Record contains two 8 byte timestamps representing the start
         // and end of a period of work carried out outside of standard working hours.
         int irregularDataBlockCount = ByteArrayHelper.getShort(irregularData, 0);
         int offset = 16;
         for (int blockIndex = 0; blockIndex < irregularDataBlockCount; blockIndex++)
         {
            LocalDateTime start = MPPUtility.getTimestamp(irregularData, offset);
            LocalDateTime end = MPPUtility.getTimestamp(irregularData, offset + 4);
            irregularRanges.add(new LocalDateTimeRange(start, end));
            offset += 8;
         }
      }

      // The regular data block starts with a 16 byte header. The first
      // two bytes of the header indicate the number of records. Following the
      // header are 20 byte records. Each record contains the following items.
      // Offset 0: double representing cumulative work at the end of this period (1000ths/minute)
      // Offset 8: double representing work per hour this period (10000ths/hour)
      // Offset 16: double representing elapsed minutes at period end (80ths/minute)
      // The first block appears to contain totals for the resource assignment, and is skipped.
      LocalDateTime calendarPeriodStart = resourceAssignment.getStart();

      double totalWorkMinutes = 0;
      double elapsedMinutes = 0;

      List<TimephasedWork> regularList = new ArrayList<>();
      int regularBlockCount = ByteArrayHelper.getShort(regularData, 0);
      double finishTime = ByteArrayHelper.getInt(regularData, 24);
      int offset = 36;

      for (int count = 0; count < regularBlockCount; count++)
      {
         double totalWorkMinutesAtPeriodEnd = (long) MPPUtility.getDouble(regularData, offset);
         double elapsedMinutesAtPeriodEnd = ByteArrayHelper.getInt(regularData, offset + 16);
         if (elapsedMinutesAtPeriodEnd < 0 || elapsedMinutesAtPeriodEnd > finishTime)
         {
            elapsedMinutesAtPeriodEnd = 0;
         }
         else
         {
            elapsedMinutesAtPeriodEnd = elapsedMinutesAtPeriodEnd / 80.0;
         }

         double totalWorkMinutesThisPeriod = (totalWorkMinutesAtPeriodEnd - totalWorkMinutes) / 1000;
         double elapsedMinutesThisPeriod = elapsedMinutesAtPeriodEnd - elapsedMinutes;

         LocalDateTime calendarPeriodEnd;
         if (count + 1 == regularBlockCount && resourceAssignment.getActualFinish() != null)
         {
            calendarPeriodEnd = resourceAssignment.getActualFinish();
         }
         else
         {
            calendarPeriodEnd = calendar.getDate(calendarPeriodStart, Duration.getInstance(elapsedMinutesThisPeriod, TimeUnit.MINUTES));
         }

         double calculatedWorkPerHour = totalWorkMinutesThisPeriod == 0 ? 0 : (totalWorkMinutesAtPeriodEnd * 60.0) / (elapsedMinutesAtPeriodEnd * 1000);

         TimephasedWork item = new TimephasedWork();
         item.setStart(calendarPeriodStart);
         item.setFinish(calendarPeriodEnd);
         item.setTotalAmount(Duration.getInstance(totalWorkMinutesThisPeriod, TimeUnit.MINUTES));
         item.setAmountPerHour(Duration.getInstance(calculatedWorkPerHour, TimeUnit.MINUTES));
         regularList.add(item);

         if (!irregularRanges.isEmpty())
         {
            while (item != null && !irregularRanges.isEmpty())
            {
               LocalDateTimeRange nextIrregularRange = irregularRanges.get(0);

               if (item.getStart().equals(nextIrregularRange.getStart()) && item.getFinish().equals(nextIrregularRange.getEnd()))
               {
                  irregularRanges.remove(0);
                  item = null;
                  continue;
               }

               if (!item.getStart().isAfter(nextIrregularRange.getStart()) && !item.getFinish().isBefore(nextIrregularRange.getEnd()))
               {
                  item = splitItem(calendar, regularList, irregularRanges);
               }
               else
               {
                  if (!item.getStart().isBefore(nextIrregularRange.getEnd()))
                  {
                     long itemDuration = item.getStart().until(item.getFinish(), ChronoUnit.MINUTES);
                     long rangeDuration = nextIrregularRange.getStart().until(nextIrregularRange.getEnd(), ChronoUnit.MINUTES);
                     if (itemDuration == rangeDuration)
                     {
                        irregularRanges.remove(0);
                        regularList.remove(regularList.size() - 1);
                        item.setStart(nextIrregularRange.getStart());
                        item.setFinish(nextIrregularRange.getEnd());
                        regularList.add(item);
                        item = null;
                     }
                     else
                     {
                        // I think in this case there will be multiple irregular ranges applying to this item as moves
                        // so we'll need to work through them all.
                        Duration itemMinutes = calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.MINUTES);
                        Duration itemWorkPerHour = Duration.getInstance(item.getTotalAmount().getDuration() * 60.0 / itemMinutes.getDuration(), TimeUnit.MINUTES);

                        // We're setting this here as the original value seems unreliable for irregular blocks?
                        item.setAmountPerHour(itemWorkPerHour);

                        while (item != null && !irregularRanges.isEmpty())
                        {
                           irregularRanges.remove(0);
                           regularList.remove(regularList.size() - 1);

                           TimephasedWork startItem = new TimephasedWork();
                           startItem.setStart(nextIrregularRange.getStart());
                           startItem.setFinish(nextIrregularRange.getEnd());
                           startItem.setAmountPerHour(item.getAmountPerHour());
                           long startItemMinutes = startItem.getStart().until(startItem.getFinish(), ChronoUnit.MINUTES);
                           double startItemWork = (startItemMinutes * startItem.getAmountPerHour().getDuration()) / 60.0;
                           startItem.setTotalAmount(Duration.getInstance(startItemWork, TimeUnit.MINUTES));
                           regularList.add(startItem);

                           double remainingWork = item.getTotalAmount().getDuration() - startItemWork;
                           if (remainingWork > 0)
                           {
                              item.setStart(startItem.getStart().plusMinutes(startItemMinutes));
                              item.setTotalAmount(Duration.getInstance(remainingWork, TimeUnit.MINUTES));
                              regularList.add(item);
                              if (!irregularRanges.isEmpty())
                              {
                                 nextIrregularRange = irregularRanges.get(0);
                              }
                           }
                           else
                           {
                              item = null;
                           }
                        }
                     }
                  }
                  else
                  {
                     item = null;
                  }
               }
            }
         }

         totalWorkMinutes = totalWorkMinutesAtPeriodEnd;
         elapsedMinutes = elapsedMinutesAtPeriodEnd;
         calendarPeriodStart = calendar.getNextWorkStart(regularList.get(regularList.size() - 1).getFinish());

         offset += 20;
      }

      return regularList;
   }


   /**
    * Split an existing regular item to apply an irregular item.
    *
    * @param calendar effective calendar
    * @param regularList regular item list
    * @param irregularRanges irregular range list
    * @return last timephased item after the split
    */
   private TimephasedWork splitItem(ProjectCalendar calendar, List<TimephasedWork> regularList, List<LocalDateTimeRange> irregularRanges)
   {
      TimephasedWork item = regularList.remove(regularList.size() - 1);

      double allocatedWorkInMinutes = 0;
      LocalDateTimeRange range = irregularRanges.remove(0);

      // Start Range
      if (item.getStart().isBefore(range.getStart()))
      {
         LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(range.getStart());
         LocalDateTime finish = calendar.getWork(previousWorkFinish, range.getStart(), TimeUnit.MINUTES).getDuration() == 0 ? previousWorkFinish : range.getStart();

         TimephasedWork startItem = new TimephasedWork();
         startItem.setStart(item.getStart());
         startItem.setFinish(finish);
         startItem.setAmountPerHour(item.getAmountPerHour());
         double workHours = calendar.getWork(startItem.getStart(), startItem.getFinish(), TimeUnit.HOURS).getDuration();
         startItem.setTotalAmount(Duration.getInstance(workHours * item.getAmountPerHour().getDuration(), TimeUnit.MINUTES));
         allocatedWorkInMinutes += startItem.getTotalAmount().getDuration();
         regularList.add(startItem);
      }

      // Inserted Range
      TimephasedWork insertedItem = new TimephasedWork();
      insertedItem.setStart(range.getStart());
      insertedItem.setFinish(range.getEnd());
      insertedItem.setAmountPerHour(item.getAmountPerHour());
      double insertedRangeWorkingHours = range.getStart().until(range.getEnd(), ChronoUnit.HOURS); // expecting this to always be 1
      insertedItem.setTotalAmount(Duration.getInstance(insertedRangeWorkingHours * item.getAmountPerHour().getDuration(), TimeUnit.MINUTES));
      allocatedWorkInMinutes += insertedItem.getTotalAmount().getDuration();
      regularList.add(insertedItem);

      // End Range
      if (item.getFinish().isAfter(range.getEnd()))
      {
         TimephasedWork endItem = new TimephasedWork();
         endItem.setStart(range.getEnd());
         endItem.setAmountPerHour(item.getAmountPerHour());
         double workMinutes = item.getTotalAmount().getDuration() - allocatedWorkInMinutes;
         endItem.setTotalAmount(Duration.getInstance(workMinutes, TimeUnit.MINUTES));
         Duration remainingMinutes = Duration.getInstance((workMinutes * 60.0) / item.getAmountPerHour().getDuration(),  TimeUnit.MINUTES);
         endItem.setFinish(calendar.getDate(endItem.getStart(), remainingMinutes));
         regularList.add(endItem);
      }

      return regularList.get(regularList.size() - 1);
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
         double totalWorkInMinutes = MPPUtility.getDouble(data, 16) / 1000.0;
         if (totalWorkInMinutes != 0.0)
         {
            LocalDateTime start = timephasedComplete.isEmpty() ? assignment.getStart() : assignment.getResume();
            LocalDateTime end = assignment.getFinish();
            Duration work = Duration.getInstance(totalWorkInMinutes, TimeUnit.MINUTES);
            double assignmentWork = calendar.getWork(start, end, TimeUnit.MINUTES).getDuration();
            Duration workPerHour = Duration.getInstance((totalWorkInMinutes * 60.0) / assignmentWork, TimeUnit.MINUTES);

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
         double previousWorkMinutes = 0;
         double previousElapsedMinutes = 0;
         LocalDateTime start = timephasedComplete.isEmpty() ? assignment.getStart() : assignment.getResume();

         for (int count = 0; count < blockCount; count++)
         {
            if (offset + 28 > data.length)
            {
               // Bail out if we don't have all the data for the block
               break;
            }

            double cumulativeWorkMinutes = MPPUtility.getDouble(data, offset) / 1000.0;
            double workMinutesThisPeriod = cumulativeWorkMinutes - previousWorkMinutes;
            double cumulativeElapsedMinutes = ByteArrayHelper.getInt(data, offset + 24) / 80.0;
            double elapsedMinutesThisPeriod = cumulativeElapsedMinutes - previousElapsedMinutes;
            Duration workPerHour = Duration.getInstance(workMinutesThisPeriod * 60.0 / elapsedMinutesThisPeriod, TimeUnit.MINUTES);
            LocalDateTime end = calendar.getDate(start, Duration.getInstance(elapsedMinutesThisPeriod, TimeUnit.MINUTES));
            Duration work = Duration.getInstance(workMinutesThisPeriod, TimeUnit.MINUTES);

            // Occasionally we appear to have blocks which represent zero work and zero time - we skip these
            if (workMinutesThisPeriod >= 1 || !start.isEqual(end))
            {
               TimephasedWork item = new TimephasedWork();
               item.setStart(start);
               item.setFinish(end);
               item.setTotalAmount(work);
               item.setAmountPerHour(workPerHour);
               newList.add(item);
            }

            start = calendar.getNextWorkStart(end);
            previousWorkMinutes = cumulativeWorkMinutes;
            previousElapsedMinutes = cumulativeElapsedMinutes;

            offset += 28;
         }
      }

      return newList;
   }

   /**
    * Extracts baseline work from the MPP file for a specific baseline.
    * Returns null if no baseline work is present, otherwise returns
    * a list of timephased work items.
    *
    * @param baselineCalendar baseline calendar
    * @param data timephased baseline work data block
    * @return timephased work
    */
   public List<TimephasedWork> getBaselineWork(ProjectCalendar baselineCalendar, byte[] data)
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
            double calendarWorkMinutesThisPeriod = baselineCalendar.getWork(start, end, TimeUnit.MINUTES).getDuration();
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

      return list;
   }

   /**
    * Extracts baseline cost from the MPP file for a specific baseline.
    * Returns null if no baseline cost is present, otherwise returns
    * a list of timephased work items.
    *
    * @param baselineCalendar baseline calendar
    * @param data timephased baseline work data block
    * @return timephased work
    */
   public List<TimephasedCost> getBaselineCost(ProjectCalendar baselineCalendar, byte[] data)
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
            double calendarWorkMinutesThisPeriod = baselineCalendar.getWork(start, end, TimeUnit.MINUTES).getDuration();
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
}
