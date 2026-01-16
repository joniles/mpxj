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
   class WorkTest
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
         return "WorkTest[" +
            "m_start=" + m_start +
            ", m_end=" + m_end +
            ", m_work=" + m_work +
            ", m_workPerHour=" + m_workPerHour +
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
      List<TimephasedWork> list = new ArrayList<>();
      if (calendar == null || regularData == null || regularData.length <= 26 || ByteArrayHelper.getShort(regularData, 0) == 0 || resourceAssignment.getTask().getDuration() == null || resourceAssignment.getTask().getDuration().getDuration() == 0)
      {
         return list;
      }

      LocalDateTime startDate = resourceAssignment.getStart();
      double finishTime = ByteArrayHelper.getInt(regularData, 24);

      int blockCount = ByteArrayHelper.getShort(regularData, 0);
      double previousCumulativeWork = 0;
      TimephasedWork previousAssignment = null;
      LocalDateTime calendarPeriodStart = resourceAssignment.getStart();

//      System.out.println();

//      System.out.println();

//      System.out.println(ByteArrayHelper.hexdump(regularData, 0, 16, false));
//      for(int test=16; test < regularData.length; test +=20)
//      {
//         System.out.println(ByteArrayHelper.hexdump(regularData, test, 20, false));
//      }

      List<LocalDateTimeRange> irregularRanges = new ArrayList<>();
      if (irregularData != null)
      {
//         System.out.println();
         //System.out.println("Irregular data");
         //System.out.println(ByteArrayHelper.hexdump(irregularData, 0, 16, false));
         int irregularDataCount = ByteArrayHelper.getShort(irregularData, 0);
         int test = 16;
         for (int count = 0; count < irregularDataCount; count++)
         {
            LocalDateTime start = MPPUtility.getTimestamp(irregularData, test);
            LocalDateTime end = MPPUtility.getTimestamp(irregularData, test+4);
//            System.out.println(calendarPeriodStart.until(start, ChronoUnit.MINUTES) + ": " + start + " " + end);
            irregularRanges.add(new LocalDateTimeRange(start, end));
            test += 8;
         }
         //System.out.println();
      }
      boolean hasIrregularData = !irregularRanges.isEmpty();

      // Dump everything
//      for(int test=16; test < data.length; test +=20)
//      {
//         System.out.println(Duration.getInstance(
//            MPPUtility.getDouble(data, test) / 1000.0, TimeUnit.MINUTES) + "\t"
//            + Duration.getInstance((Math.round(MPPUtility.getDouble(data, test+8) * 60.0) / 1000.0) / 10.0, TimeUnit.MINUTES) + "\t"
//            + ByteArrayHelper.getInt(data, test+16) + "\t"
//            + ByteArrayHelper.getInt(data, test+16) / 80+ "\t");
//      }

      // Skip first block
      double totalWorkMinutes = 0;
      int elapsedMinutes = 0;

      List<WorkTest> regularList = new ArrayList<>();
      int regularDataCount = ByteArrayHelper.getShort(regularData, 0);
      int test = 36;

      for(int count=0; count < regularDataCount; count++)
      {
         double totalWorkMinutesAtPeriodEnd = MPPUtility.getDouble(regularData, test) / 1000.0;
         if (totalWorkMinutesAtPeriodEnd < 1)
         {
            // Problem! Bail out early - data probably not valid!
            break;
         }

         double totalWorkMinutesThisPeriod = totalWorkMinutesAtPeriodEnd -  totalWorkMinutes;
         double workPerHourThisPeriod = (Math.round(MPPUtility.getDouble(regularData, test+8) * 60.0) / 1000.0) / 10.0;
         int elapsedMinutesAtPeriodEnd = ByteArrayHelper.getInt(regularData, test+16) / 80;
         int elapsedMinutesThisPeriod = elapsedMinutesAtPeriodEnd - elapsedMinutes;
         LocalDateTime calendarPeriodEnd = calendar.getDate(calendarPeriodStart, Duration.getInstance(elapsedMinutesThisPeriod, TimeUnit.MINUTES));

         WorkTest item = new WorkTest();
         item.setStart(calendarPeriodStart);
         item.setEnd(calendarPeriodEnd);
         item.setWork(Duration.getInstance(totalWorkMinutesThisPeriod, TimeUnit.MINUTES));
         item.setWorkPerHour(Duration.getInstance(workPerHourThisPeriod, TimeUnit.MINUTES));
         regularList.add(item);

         if (!irregularRanges.isEmpty())
         {
            LocalDateTimeRange nextIrregularRange = irregularRanges.get(0);
            if (!item.getStart().isAfter(nextIrregularRange.getStart()) && !item.getEnd().isBefore(nextIrregularRange.getEnd()))
            {
               splitItem(calendar, regularList, irregularRanges);
               calendarPeriodEnd = regularList.get(regularList.size()-1).getEnd();
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
                     regularList.remove(regularList.size()-1);
                     item.setStart(nextIrregularRange.getStart());
                     item.setEnd(nextIrregularRange.getEnd());
                     regularList.add(item);
                     calendarPeriodEnd = item.getEnd();
                  }
                  else
                  {
                     System.out.println("item is longer than range");
                  }
               }
            }
         }

         totalWorkMinutes = totalWorkMinutesAtPeriodEnd;
         elapsedMinutes = elapsedMinutesAtPeriodEnd;
         calendarPeriodStart = calendar.getNextWorkStart(calendarPeriodEnd);

         test += 20;
      }

      if (hasIrregularData)
      {
         System.out.println(resourceAssignment);
         regularList.forEach(System.out::println);
         System.out.println();
      }

      int index = 32;
      int currentBlock = 0;
      while (currentBlock < blockCount && index + 20 <= regularData.length)
      {
         //System.out.println(ByteArrayHelper.hexdump(data, index, 20, 0, 4, 12));
         double time = ByteArrayHelper.getInt(regularData, index);
         Duration cumulativeWork = Duration.getInstance(MPPUtility.getDouble(regularData, index + 4) / 1000.0, TimeUnit.MINUTES);
         Duration workPerHourInThisPeriod = Duration.getInstance((Math.round(MPPUtility.getDouble(regularData, index + 12) * 60.0) / 1000.0) / 10.0, TimeUnit.MINUTES);
//         System.out.println(
//            time + "\t"
//               + startDate.plusMinutes((long)(time / 80.0)) + "\t"
//               + ByteArrayHelper.getLong(regularData, index) + "\t"
//               + cumulativeWork + "\t"
//               + workPerHourInThisPeriod + "\t"
//               + ByteArrayHelper.getLong(regularData, index + 12)
//         );

         // If the start of this block is before the start of the assignment, or after the end of the assignment
         // the values don't make sense, so we'll just set the start of this block to be the start of the assignment.
         // This deals with an issue where odd timephased data like this was causing an MPP file to be read
         // extremely slowly.
         if (time < 0 || time > finishTime)
         {
            time = 0;
         }
         else
         {
            time /= 80;
         }
         Duration startWork = Duration.getInstance(time, TimeUnit.MINUTES);

         double currentCumulativeWork = (long) MPPUtility.getDouble(regularData, index + 4);
         double assignmentDuration = currentCumulativeWork - previousCumulativeWork;
         previousCumulativeWork = currentCumulativeWork;
         assignmentDuration /= 1000;
         Duration totalWork = Duration.getInstance(assignmentDuration, TimeUnit.MINUTES);

         // Originally this value was used to calculate the amount per day,
         // but the value proved to be unreliable in some circumstances resulting
         // in negative durations.
         // MPPUtility.getDouble(data, index + 12);

         LocalDateTime start;
         if (startWork.getDuration() == 0)
         {
            start = startDate;
         }
         else
         {
            start = calendar.getNextWorkStart(calendar.getDate(startDate, startWork));
         }

         TimephasedWork assignment = new TimephasedWork();
         assignment.setStart(start);
         assignment.setTotalAmount(totalWork);

         if (previousAssignment != null)
         {
            LocalDateTime finish = calendar.getDate(startDate, startWork);
            previousAssignment.setFinish(finish);
            if (previousAssignment.getStart().equals(previousAssignment.getFinish()))
            {
               list.remove(list.size() - 1);
            }
         }

         list.add(assignment);
         previousAssignment = assignment;

         index += 20;
         ++currentBlock;
      }

      if (previousAssignment != null)
      {
         Duration finishWork = Duration.getInstance(finishTime / 80, TimeUnit.MINUTES);
         LocalDateTime finish = calendar.getDate(startDate, finishWork);
         previousAssignment.setFinish(finish);
         if (previousAssignment.getStart().equals(previousAssignment.getFinish()))
         {
            list.remove(list.size() - 1);
         }
      }

      calculateAmountPerDay(calendar, list);

      return list;
   }


   private void splitItem(ProjectCalendar calendar, List<WorkTest> regularList, List<LocalDateTimeRange> irregularRanges)
   {
      WorkTest item = regularList.get(regularList.size()-1);

      while (!irregularRanges.isEmpty()
         && !item.getStart().isAfter(irregularRanges.get(0).getStart())
         && !item.getEnd().isBefore(irregularRanges.get(0).getEnd()))
      {
         regularList.remove(regularList.size()-1);

         double allocatedWorkInMinutes = 0;
         LocalDateTimeRange range = irregularRanges.remove(0);

         // Start Range
         if (item.getStart().isBefore(range.getStart()))
         {
            WorkTest startItem = new WorkTest();
            startItem.setStart(item.getStart());
            startItem.setEnd(calendar.getPreviousWorkFinish(range.getStart()));
            startItem.setWorkPerHour(item.getWorkPerHour());
            double workHours = calendar.getWork(startItem.getStart(), startItem.getEnd(), TimeUnit.HOURS).getDuration();
            startItem.setWork(Duration.getInstance(workHours * item.getWorkPerHour().getDuration(), TimeUnit.MINUTES));
            allocatedWorkInMinutes += startItem.getWork().getDuration();
            regularList.add(startItem);
         }

         // Inserted Range
         WorkTest insertedItem =  new WorkTest();
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
            WorkTest endItem = new WorkTest();
            endItem.setStart(range.getEnd());
            endItem.setWorkPerHour(item.getWorkPerHour());
            double workMinutes = item.getWork().getDuration() - allocatedWorkInMinutes;
            endItem.setWork(Duration.getInstance(workMinutes, TimeUnit.MINUTES));
            //endItem.setEnd(item.getEnd());
            endItem.setEnd(calendar.getDate(endItem.getStart(), endItem.getWork()));

            regularList.add(endItem);
         }

         item = regularList.get(regularList.size()-1);
      }
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
      if (data == null || data.length == 0 || assignment.getTask().getDuration() == null || assignment.getTask().getDuration().getDuration() == 0)
      {
         return list;
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

      return list;
   }

   /**
    * Extracts baseline work from the MPP file for a specific baseline.
    * Returns null if no baseline work is present, otherwise returns
    * a list of timephased work items.
    *
    * @param calendar effective calendar for the resource assignment
    * @param assignment parent assignment
    * @param normaliser normaliser associated with this data
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
    * @param normaliser normaliser associated with this data
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
