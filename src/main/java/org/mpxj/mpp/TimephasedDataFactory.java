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

import java.util.List;
import java.util.stream.Collectors;

import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedCostContainer;
import org.mpxj.TimephasedWork;
import org.mpxj.TimephasedWorkContainer;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DefaultTimephasedCostContainer;
import org.mpxj.common.DefaultTimephasedWorkContainer;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.TimephasedNormaliser;

/**
 * This class contains methods to create lists of TimephasedWork
 * and TimephasedCost instances.
 */
final class TimephasedDataFactory
{
   /**
    * The TimephasedItem class hierarchy will be more like this
    * once the big refactoring is done!
    */
   private static class NewTimephasedWork
   {
      public LocalDateTime getStart()
      {
         return m_start;
      }

      public void setStart(LocalDateTime start)
      {
         m_start = start;
      }

      public LocalDateTime getEnd()
      {
         return m_end;
      }

      public void setEnd(LocalDateTime end)
      {
         m_end = end;
      }

      public Duration getWork()
      {
         return m_work;
      }

      public void setWork(Duration work)
      {
         m_work = work;
      }

      public Duration getWorkPerHour()
      {
         return m_workPerHour;
      }

      public void setWorkPerHour(Duration workPerHour)
      {
         m_workPerHour = workPerHour;
      }

      @Override public String toString()
      {
         return "NewTimephasedWork[" +
            "start=" + m_start +
            ", end=" + m_end +
            ", work=" + m_work +
            ", workPerHour=" + m_workPerHour +
            ']';
      }

      private LocalDateTime m_start;
      private LocalDateTime m_end;
      private Duration m_work;
      private Duration m_workPerHour;
   }

   /**
    * Given a block of data representing completed work, this method will
    * retrieve a set of TimephasedWork instances which represent
    * the day by day work carried out for a specific resource assignment.
    *
    * @param calendar calendar on which date calculations are based
    * @param resourceAssignment resource assignment
    * @param regularData completed work data block
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

      List<NewTimephasedWork> regularList = new ArrayList<>();
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
         if (count+1 == regularBlockCount && resourceAssignment.getActualFinish() != null)
         {
            calendarPeriodEnd = resourceAssignment.getActualFinish();
         }
         else
         {
            calendarPeriodEnd = calendar.getDate(calendarPeriodStart, Duration.getInstance(elapsedMinutesThisPeriod, TimeUnit.MINUTES));
         }

         double calculatedWorkPerHour = (totalWorkMinutesAtPeriodEnd * 60.0) / (elapsedMinutesAtPeriodEnd * 1000);

         NewTimephasedWork item = new NewTimephasedWork();
         item.setStart(calendarPeriodStart);
         item.setEnd(calendarPeriodEnd);
         item.setWork(Duration.getInstance(totalWorkMinutesThisPeriod, TimeUnit.MINUTES));
         item.setWorkPerHour(Duration.getInstance(calculatedWorkPerHour, TimeUnit.MINUTES));
         regularList.add(item);

         if (!irregularRanges.isEmpty())
         {
            while (item != null && !irregularRanges.isEmpty())
            {
               LocalDateTimeRange nextIrregularRange = irregularRanges.get(0);

               if (item.getStart().equals(nextIrregularRange.getStart()) && item.getEnd().equals(nextIrregularRange.getEnd()))
               {
                  irregularRanges.remove(0);
                  item = null;
                  continue;
               }

               if (!item.getStart().isAfter(nextIrregularRange.getStart()) && !item.getEnd().isBefore(nextIrregularRange.getEnd()))
               {
                  item = splitItem(calendar, regularList, irregularRanges);
               }
               else
               {
                  if (!item.getStart().isBefore(nextIrregularRange.getEnd()))
                  {
                     long itemDuration = item.getStart().until(item.getEnd(), ChronoUnit.MINUTES);
                     long rangeDuration = nextIrregularRange.getStart().until(nextIrregularRange.getEnd(), ChronoUnit.MINUTES);
                     if (itemDuration == rangeDuration)
                     {
                        irregularRanges.remove(0);
                        regularList.remove(regularList.size() - 1);
                        item.setStart(nextIrregularRange.getStart());
                        item.setEnd(nextIrregularRange.getEnd());
                        regularList.add(item);
                        item = null;
                     }
                     else
                     {
                        // I think in this case there will be multiple irregular ranges applying to this item as moves
                        // so we'll need to work through them all.
                        Duration itemMinutes = calendar.getWork(item.getStart(), item.getEnd(), TimeUnit.MINUTES);
                        Duration itemWorkPerHour = Duration.getInstance(item.getWork().getDuration() * 60.0 / itemMinutes.getDuration(), TimeUnit.MINUTES);

                        // We're setting this here as the original value seems unreliable for irregular blocks?
                        item.setWorkPerHour(itemWorkPerHour);

                        while (item != null && !irregularRanges.isEmpty())
                        {
                           irregularRanges.remove(0);
                           regularList.remove(regularList.size() - 1);

                           NewTimephasedWork startItem = new NewTimephasedWork();
                           startItem.setStart(nextIrregularRange.getStart());
                           startItem.setEnd(nextIrregularRange.getEnd());
                           startItem.setWorkPerHour(item.getWorkPerHour());
                           long startItemMinutes = startItem.getStart().until(startItem.getEnd(), ChronoUnit.MINUTES);
                           double startItemWork = (startItemMinutes * startItem.getWorkPerHour().getDuration()) / 60.0;
                           startItem.setWork(Duration.getInstance(startItemWork, TimeUnit.MINUTES));
                           regularList.add(startItem);

                           double remainingWork = item.getWork().getDuration() - startItemWork;
                           if (remainingWork > 0)
                           {
                              item.setStart(startItem.getStart().plusMinutes(startItemMinutes));
                              item.setWork(Duration.getInstance(remainingWork, TimeUnit.MINUTES));
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
         calendarPeriodStart = calendar.getNextWorkStart(regularList.get(regularList.size()-1).getEnd());

         offset += 20;
      }

      return regularList.stream().map(w -> populateTimephasedWork(calendar, w)).collect(Collectors.toList());
   }

   /**
    * This method transforms our new timephased item representation back to the original representation.
    *
    * @param calendar effective calendar
    * @param item new timephased work item
    * @return TimephasedWork instance
    */
   private TimephasedWork populateTimephasedWork(ProjectCalendar calendar, NewTimephasedWork item)
   {
      TimephasedWork work = new TimephasedWork();
      work.setStart(item.getStart());
      work.setFinish(item.getEnd());
      work.setTotalAmount(item.getWork());

      // from calculateAmountPerDay
      Duration amountPerDay;
      if (work.getTotalAmount().getDuration() == 0)
      {
         amountPerDay = Duration.getInstance(0, TimeUnit.MINUTES);
      }
      else
      {
         Duration totalWorkInMinutes = work.getTotalAmount();
         Duration calculatedTotalWorkInMinutes = calendar.getWork(work.getStart(), work.getFinish(), TimeUnit.MINUTES);
         double minutesPerDay = 8.0 * 60.0;
         double calculatedAmountPerDay = (minutesPerDay * totalWorkInMinutes.getDuration()) / calculatedTotalWorkInMinutes.getDuration();
         amountPerDay = Duration.getInstance(calculatedAmountPerDay, TimeUnit.MINUTES);
      }
      work.setAmountPerDay(amountPerDay);

      return work;
   }

   /**
    * Split an existing regular item to apply an irregular item.
    *
    * @param calendar effective calendar
    * @param regularList regular item list
    * @param irregularRanges irregular range list
    * @return last timephased item after the split
    */
   private NewTimephasedWork splitItem(ProjectCalendar calendar, List<NewTimephasedWork> regularList, List<LocalDateTimeRange> irregularRanges)
   {
      NewTimephasedWork item = regularList.remove(regularList.size()-1);

      double allocatedWorkInMinutes = 0;
      LocalDateTimeRange range = irregularRanges.remove(0);

      // Start Range
      if (item.getStart().isBefore(range.getStart()))
      {
         LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(range.getStart());
         LocalDateTime finish = calendar.getWork(previousWorkFinish, range.getStart(), TimeUnit.MINUTES).getDuration() == 0 ? previousWorkFinish : range.getStart();

         NewTimephasedWork startItem = new NewTimephasedWork();
         startItem.setStart(item.getStart());
         startItem.setEnd(finish);
         startItem.setWorkPerHour(item.getWorkPerHour());
         double workHours = calendar.getWork(startItem.getStart(), startItem.getEnd(), TimeUnit.HOURS).getDuration();
         startItem.setWork(Duration.getInstance(workHours * item.getWorkPerHour().getDuration(), TimeUnit.MINUTES));
         allocatedWorkInMinutes += startItem.getWork().getDuration();
         regularList.add(startItem);
      }

      // Inserted Range
      NewTimephasedWork insertedItem =  new NewTimephasedWork();
      insertedItem.setStart(range.getStart());
      insertedItem.setEnd(range.getEnd());
      insertedItem.setWorkPerHour(item.getWorkPerHour());
      double insertedRangeWorkingHours = range.getStart().until(range.getEnd(), ChronoUnit.HOURS); // expecting this to always be 1
      insertedItem.setWork(Duration.getInstance(insertedRangeWorkingHours * item.getWorkPerHour().getDuration(), TimeUnit.MINUTES));
      allocatedWorkInMinutes += insertedItem.getWork().getDuration();
      regularList.add(insertedItem);

      // End Range
      if (item.getEnd().isAfter(range.getEnd()))
      {
         NewTimephasedWork endItem = new NewTimephasedWork();
         endItem.setStart(range.getEnd());
         endItem.setWorkPerHour(item.getWorkPerHour());
         double workMinutes = item.getWork().getDuration() - allocatedWorkInMinutes;
         endItem.setWork(Duration.getInstance(workMinutes, TimeUnit.MINUTES));
         //endItem.setEnd(item.getEnd());
         endItem.setEnd(calendar.getDate(endItem.getStart(), endItem.getWork()));

         regularList.add(endItem);
      }

      return regularList.get(regularList.size()-1);
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
      List<TimephasedWork> list = new ArrayList<>();
      if (data == null || data.length < 24 || assignment.getTask().getDuration() == null || assignment.getTask().getDuration().getDuration() == 0)
      {
         return list;
      }


      List<TimephasedWork> newList2;

      //System.out.println(ByteArrayHelper.hexdump(data, false, 16, " "));

//      System.out.println(ByteArrayHelper.hexdump(data, 0, 16, false, 16, " "));
//      System.out.println(ByteArrayHelper.hexdump(data, 16, data.length-16, false, 28, " "));
//
//      System.out.println(ByteArrayHelper.getShort(data, 0));
//      System.out.println(ByteArrayHelper.getShort(data, 2));
//      System.out.println(ByteArrayHelper.getShort(data, 4));
//      System.out.println(ByteArrayHelper.getInt(data, 2));
//      System.out.println(MPPUtility.getDouble(data, 8));

      {
         List<NewTimephasedWork> newList = new ArrayList<>();
         int blockCount = ByteArrayHelper.getShort(data, 0);

//         System.out.println();
//         int offset = 16 + 28; // skip the summary block
//         double previousWorkMinutes = 0;
//         double previousElapsedMinutes = 0;
//
//         for (int count=0; count < blockCount; count++)
//         {
//            double cumulativeWorkMinutes = MPPUtility.getDouble(data, offset) / 1000.0;
//            double workMinutesThisPeriod = cumulativeWorkMinutes - previousWorkMinutes;
//            double cumulativeElapsedMinutes = ByteArrayHelper.getInt(data, offset+24) / 80.0;
//            double elapsedMinutesThisPeriod = cumulativeElapsedMinutes - previousElapsedMinutes;
//            double workPerHour = workMinutesThisPeriod * 60.0 / elapsedMinutesThisPeriod;
//
//            System.out.println(cumulativeWorkMinutes + "\t" // Cumulative work minutes at block end
//               + (MPPUtility.getDouble(data, offset+8) / 20000.0) + "\t" // Hours per day - unreliable
//               + MPPUtility.getDouble(data, offset+16) + "\t" // Unknown
//               + cumulativeElapsedMinutes + "\t" // Cumulative elapsed minutes at block end
//               + workMinutesThisPeriod + "\t"
//               + elapsedMinutesThisPeriod + "\t"
//               + workPerHour);
//
//            previousWorkMinutes = cumulativeWorkMinutes;
//            previousElapsedMinutes = cumulativeElapsedMinutes;
//
//            offset += 28;
//         }


         if (blockCount == 0)
         {
            double totalWorkInMinutes = MPPUtility.getDouble(data, 16) / 1000.0;
            if (totalWorkInMinutes != 0.0)
            {
               LocalDateTime start = timephasedComplete.isEmpty() ? assignment.getStart() : assignment.getResume();
               LocalDateTime end = assignment.getFinish();
               Duration work = Duration.getInstance(totalWorkInMinutes, TimeUnit.MINUTES);
               double assignmentWork = calendar.getWork(start, end , TimeUnit.MINUTES).getDuration();
               Duration workPerHour = Duration.getInstance((totalWorkInMinutes * 60.0) / assignmentWork, TimeUnit.MINUTES);

               NewTimephasedWork item = new NewTimephasedWork();
               item.setStart(start);
               item.setEnd(end);
               item.setWork(work);
               item.setWorkPerHour(workPerHour);
               newList.add(item);
            }
         }
         else
         {
            int offset = 16 + 28; // skip the summary block
            double previousWorkMinutes = 0;
            double previousElapsedMinutes = 0;
            LocalDateTime start = timephasedComplete.isEmpty() ? assignment.getStart() : assignment.getResume();

            for (int count=0; count < blockCount; count++)
            {
               if (offset + 28 > data.length)
               {
                  // Bail out if we don't have all the data for the block
                  break;
               }

               double cumulativeWorkMinutes = MPPUtility.getDouble(data, offset) / 1000.0;
               double workMinutesThisPeriod = cumulativeWorkMinutes - previousWorkMinutes;
               double cumulativeElapsedMinutes = ByteArrayHelper.getInt(data, offset+24) / 80.0;
               double elapsedMinutesThisPeriod = cumulativeElapsedMinutes - previousElapsedMinutes;
               Duration workPerHour = Duration.getInstance(workMinutesThisPeriod * 60.0 / elapsedMinutesThisPeriod, TimeUnit.MINUTES);
               LocalDateTime end = calendar.getDate(start, Duration.getInstance(elapsedMinutesThisPeriod, TimeUnit.MINUTES));
               Duration work =  Duration.getInstance(workMinutesThisPeriod, TimeUnit.MINUTES);

               // Occasionally we appear to have blocks which represent zero work and zero time - we skip these
               if (workMinutesThisPeriod >= 1 || !start.isEqual(end))
               {
                  NewTimephasedWork item = new NewTimephasedWork();
                  item.setStart(start);
                  item.setEnd(end);
                  item.setWork(work);
                  item.setWorkPerHour(workPerHour);
                  newList.add(item);
               }

               start = calendar.getNextWorkStart(end);
               previousWorkMinutes = cumulativeWorkMinutes;
               previousElapsedMinutes = cumulativeElapsedMinutes;

               offset += 28;
            }
         }

         //newList.forEach(System.out::println);
         newList2 = newList.stream().map(w -> populateTimephasedWork(calendar, w)).collect(Collectors.toList());
      }

      int blockCount = ByteArrayHelper.getShort(data, 0);
      if (blockCount == 0)
      {
         if (data.length >= 24)
         {
            double time = MPPUtility.getDouble(data, 16);
            if (time != 0.0)
            {
               time /= 1000;
               Duration totalWork = Duration.getInstance(time, TimeUnit.MINUTES);

               // Originally this value was used to calculate the amount per day,
               // but the value proved to be unreliable in some circumstances resulting
               // in negative durations.
               // MPPUtility.getDouble(data, 8);

               TimephasedWork work = new TimephasedWork();
               work.setStart(timephasedComplete.isEmpty() ? assignment.getStart() : assignment.getResume());

               work.setFinish(assignment.getFinish());
               work.setTotalAmount(totalWork);
               list.add(work);
            }
         }
      }
      else
      {
         LocalDateTime offset = timephasedComplete.isEmpty() ? assignment.getStart() : assignment.getResume();
         int index = 40;
         double previousCumulativeWork = 0;
         TimephasedWork previousAssignment = null;
         int currentBlock = 0;
         int previousModifiedFlag = 0;

         while (currentBlock < blockCount && index + 28 <= data.length)
         {
            double time = ByteArrayHelper.getInt(data, index);
            time /= 80;
            Duration blockDuration = Duration.getInstance(time, TimeUnit.MINUTES);
            LocalDateTime start;
            if (blockDuration.getDuration() == 0)
            {
               start = offset;
            }
            else
            {
               start = calendar.getNextWorkStart(calendar.getDate(offset, blockDuration));
            }

            double currentCumulativeWork = MPPUtility.getDouble(data, index + 4);
            double assignmentDuration = currentCumulativeWork - previousCumulativeWork;
            assignmentDuration /= 1000;
            Duration totalWork = Duration.getInstance(assignmentDuration, TimeUnit.MINUTES);
            previousCumulativeWork = currentCumulativeWork;

            // Originally this value was used to calculate the amount per day,
            // but the value proved to be unreliable in some circumstances resulting
            // in negative durations.
            // MPPUtility.getDouble(data, index + 12);

            int currentModifiedFlag = ByteArrayHelper.getShort(data, index + 22);
            boolean modified = (currentBlock > 0 && previousModifiedFlag != 0 && currentModifiedFlag == 0) || ((currentModifiedFlag & 0x3000) != 0);
            previousModifiedFlag = currentModifiedFlag;

            TimephasedWork work = new TimephasedWork();
            work.setStart(start);
            work.setModified(modified);
            work.setTotalAmount(totalWork);

            if (previousAssignment != null)
            {
               LocalDateTime finish = calendar.getDate(offset, blockDuration);
               previousAssignment.setFinish(finish);
               if (previousAssignment.getStart().equals(previousAssignment.getFinish()))
               {
                  list.remove(list.size() - 1);
               }
            }

            list.add(work);
            previousAssignment = work;

            index += 28;
            ++currentBlock;
         }

         if (previousAssignment != null)
         {
            double time = ByteArrayHelper.getInt(data, 24);
            time /= 80;
            Duration blockDuration = Duration.getInstance(time, TimeUnit.MINUTES);
            LocalDateTime finish = calendar.getDate(offset, blockDuration);
            previousAssignment.setFinish(finish);
            if (previousAssignment.getStart().equals(previousAssignment.getFinish()))
            {
               list.remove(list.size() - 1);
            }
         }
      }

      calculateAmountPerDay(calendar, list);

      return newList2;
   }

   /**
    * Extracts baseline work from the MPP file for a specific baseline.
    * Returns null if no baseline work is present, otherwise returns
    * a list of timephased work items.
    *
    * @param calendar effective calendar for the resource assignment
    * @param assignment parent assignment
    * @param normaliser normalizer associated with this data
    * @param data timephased baseline work data block
    * @param raw flag indicating if this data is to be treated as raw
    * @return timephased work
    */
   public TimephasedWorkContainer getBaselineWork(ProjectCalendar calendar, ResourceAssignment assignment, TimephasedNormaliser<TimephasedWork> normaliser, byte[] data, boolean raw)
   {
      if (data == null || data.length == 0)
      {
         return null;
      }

      // 8 byte header
      int blockCount = ByteArrayHelper.getShort(data, 0);
      //int timephasedDataType = MPPUtility.getShort(data, 2);
      int offset = ByteArrayHelper.getShort(data, 4);
      //int unknown = MPPUtility.getShort(data, 6);

      // We need at least 3 blocks
      if (blockCount < 3)
      {
         return null;
      }

      // 20 byte blocks
      // Each block contains the block end date, which is also then the start of the next block
      // First block only used to give us the start of the first timephased data
      // Last block is ignored
      //
      // 0 - 7: cumulative work; double 1/1000 minute
      // 8 - 11: expected work this period; int 1/10 minute
      // 12 - 15: unknown
      // 16 - 19: block end date; int timestamp in 1/10 minute

      LocalDateTime blockEndDate = null;
      long previousTotalWorkInMinutes = 0;
      List<TimephasedWork> list = new ArrayList<>();
      long totalWork = 0;

      for (int blockIndex = 0; blockIndex < blockCount - 1; blockIndex++)
      {
         if (offset + 20 > data.length)
         {
            break;
         }

         if (blockIndex == 0)
         {
            blockEndDate = MPPUtility.getTimestampFromTenths(data, offset + 16);
         }
         else
         {
            LocalDateTime blockStartDate = blockEndDate;
            long currentCumulativeWorkInMinutes = (long) (MPPUtility.getDouble(data, offset) / 1000.0);
            int expectedWorkThisPeriodInMinutes = ByteArrayHelper.getInt(data, offset + 8) / 10;
            //int unknown = MPPUtility.getInt(data, offset + 12);
            blockEndDate = MPPUtility.getTimestampFromTenths(data, offset + 16);

            long workThisPeriodInMinutes = currentCumulativeWorkInMinutes - previousTotalWorkInMinutes;
            totalWork += workThisPeriodInMinutes;

            TimephasedWork work = new TimephasedWork();
            work.setStart(blockStartDate);
            work.setFinish(blockEndDate);
            work.setTotalAmount(Duration.getInstance(workThisPeriodInMinutes, TimeUnit.MINUTES));
            work.setModified(workThisPeriodInMinutes != expectedWorkThisPeriodInMinutes);
            list.add(work);

            previousTotalWorkInMinutes = currentCumulativeWorkInMinutes;
         }

         offset += 20;
      }

      if (totalWork == 0)
      {
         return null;
      }

      calculateAmountPerDay(calendar, list);

      return new DefaultTimephasedWorkContainer(assignment, normaliser, list, true);
   }

   /**
    * Extracts baseline cost from the MPP file for a specific baseline.
    * Returns null if no baseline cost is present, otherwise returns
    * a list of timephased work items.
    *
    * @param assignment resource assignment
    * @param normaliser normalizer associated with this data
    * @param data timephased baseline work data block
    * @param raw flag indicating if this data is to be treated as raw
    * @return timephased work
    */
   public TimephasedCostContainer getBaselineCost(ResourceAssignment assignment, TimephasedNormaliser<TimephasedCost> normaliser, byte[] data, boolean raw)
   {
      TimephasedCostContainer result = null;
      if (data == null || data.length == 0)
      {
         return result;
      }

      List<TimephasedCost> list = null;

      //System.out.println(ByteArrayHelper.hexdump(data, false));
      int index = 16; // 16 byte header
      int blockSize = 20;
      double previousTotalCost = 0;

      LocalDateTime blockStartDate = MPPUtility.getTimestampFromTenths(data, index + 16);
      index += blockSize;

      while (index + blockSize <= data.length)
      {
         LocalDateTime blockEndDate = MPPUtility.getTimestampFromTenths(data, index + 16);
         double currentTotalCost = (double) ((long) MPPUtility.getDouble(data, index + 8)) / 100;
         if (!costEquals(previousTotalCost, currentTotalCost))
         {
            TimephasedCost cost = new TimephasedCost();
            cost.setStart(blockStartDate);
            cost.setFinish(blockEndDate);
            cost.setTotalAmount(Double.valueOf(currentTotalCost - previousTotalCost));

            if (list == null)
            {
               list = new ArrayList<>();
            }
            list.add(cost);
            //System.out.println(cost);

            previousTotalCost = currentTotalCost;
         }

         blockStartDate = blockEndDate;
         index += blockSize;
      }

      if (list != null)
      {
         result = new DefaultTimephasedCostContainer(assignment, normaliser, list, raw);
      }

      return result;
   }

   /**
    * Equality test cost values.
    *
    * @param lhs cost value
    * @param rhs cost value
    * @return true if costs are equal, within the allowable delta
    */
   private boolean costEquals(double lhs, double rhs)
   {
      return NumberHelper.equals(lhs, rhs, 0.00001);
   }

   /**
    * Calculate the amount per day attribute for each TimephasedWork instance.
    *
    * @param calendar effective calendar to use for the calculation
    * @param list list of TimephasedWork instances
    */
   private void calculateAmountPerDay(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      for (TimephasedWork work : list)
      {
         Duration amountPerDay;
         if (work.getTotalAmount().getDuration() == 0)
         {
            amountPerDay = Duration.getInstance(0, TimeUnit.MINUTES);
         }
         else
         {
            Duration totalWorkInMinutes = work.getTotalAmount();
            Duration calculatedTotalWorkInMinutes = calendar.getWork(work.getStart(), work.getFinish(), TimeUnit.MINUTES);
            double minutesPerDay = 8.0 * 60.0;
            double calculatedAmountPerDay = (minutesPerDay * totalWorkInMinutes.getDuration()) / calculatedTotalWorkInMinutes.getDuration();
            amountPerDay = Duration.getInstance(calculatedAmountPerDay, TimeUnit.MINUTES);
         }
         work.setAmountPerDay(amountPerDay);
      }
   }
}
