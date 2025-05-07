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
import java.util.ArrayList;

import java.util.List;

import org.mpxj.Duration;
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
    * Given a block of data representing completed work, this method will
    * retrieve a set of TimephasedWork instances which represent
    * the day by day work carried out for a specific resource assignment.
    *
    * @param calendar calendar on which date calculations are based
    * @param resourceAssignment resource assignment
    * @param data completed work data block
    * @return list of TimephasedWork instances
    */
   public List<TimephasedWork> getCompleteWork(ProjectCalendar calendar, ResourceAssignment resourceAssignment, byte[] data)
   {
      List<TimephasedWork> list = new ArrayList<>();
      if (calendar == null || data == null || data.length <= 26 || ByteArrayHelper.getShort(data, 0) == 0 || resourceAssignment.getTask().getDuration() == null || resourceAssignment.getTask().getDuration().getDuration() == 0)
      {
         return list;
      }

      LocalDateTime startDate = resourceAssignment.getStart();
      double finishTime = ByteArrayHelper.getInt(data, 24);

      int blockCount = ByteArrayHelper.getShort(data, 0);
      double previousCumulativeWork = 0;
      TimephasedWork previousAssignment = null;

      int index = 32;
      int currentBlock = 0;
      while (currentBlock < blockCount && index + 20 <= data.length)
      {
         double time = ByteArrayHelper.getInt(data, index);

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

         double currentCumulativeWork = (long) MPPUtility.getDouble(data, index + 4);
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
