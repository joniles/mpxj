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

package net.sf.mpxj.mpp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedCost;
import net.sf.mpxj.TimephasedCostContainer;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.TimephasedWorkContainer;
import net.sf.mpxj.common.DefaultTimephasedCostContainer;
import net.sf.mpxj.common.DefaultTimephasedWorkContainer;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.TimephasedNormaliser;

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
      if (calendar == null || data == null || data.length <= 26 || MPPUtility.getShort(data, 0) == 0 || resourceAssignment.getTask().getDuration() == null || resourceAssignment.getTask().getDuration().getDuration() == 0)
      {
         return list;
      }

      Date startDate = resourceAssignment.getStart();
      double finishTime = MPPUtility.getInt(data, 24);

      int blockCount = MPPUtility.getShort(data, 0);
      double previousCumulativeWork = 0;
      TimephasedWork previousAssignment = null;

      int index = 32;
      int currentBlock = 0;
      while (currentBlock < blockCount && index + 20 <= data.length)
      {
         double time = MPPUtility.getInt(data, index);

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

         Date start;
         if (startWork.getDuration() == 0)
         {
            start = startDate;
         }
         else
         {
            start = calendar.getDate(startDate, startWork, true);
         }

         TimephasedWork assignment = new TimephasedWork();
         assignment.setStart(start);
         assignment.setTotalAmount(totalWork);

         if (previousAssignment != null)
         {
            Date finish = calendar.getDate(startDate, startWork, false);
            previousAssignment.setFinish(finish);
            if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
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
         Date finish = calendar.getDate(startDate, finishWork, false);
         previousAssignment.setFinish(finish);
         if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
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

      int blockCount = MPPUtility.getShort(data, 0);
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
         Date offset = timephasedComplete.isEmpty() ? assignment.getStart() : assignment.getResume();
         int index = 40;
         double previousCumulativeWork = 0;
         TimephasedWork previousAssignment = null;
         int currentBlock = 0;
         int previousModifiedFlag = 0;

         while (currentBlock < blockCount && index + 28 <= data.length)
         {
            double time = MPPUtility.getInt(data, index);
            time /= 80;
            Duration blockDuration = Duration.getInstance(time, TimeUnit.MINUTES);
            Date start;
            if (blockDuration.getDuration() == 0)
            {
               start = offset;
            }
            else
            {
               start = calendar.getDate(offset, blockDuration, true);
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

            int currentModifiedFlag = MPPUtility.getShort(data, index + 22);
            boolean modified = (currentBlock > 0 && previousModifiedFlag != 0 && currentModifiedFlag == 0) || ((currentModifiedFlag & 0x3000) != 0);
            previousModifiedFlag = currentModifiedFlag;

            TimephasedWork work = new TimephasedWork();
            work.setStart(start);
            work.setModified(modified);
            work.setTotalAmount(totalWork);

            if (previousAssignment != null)
            {
               Date finish = calendar.getDate(offset, blockDuration, false);
               previousAssignment.setFinish(finish);
               if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
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
            double time = MPPUtility.getInt(data, 24);
            time /= 80;
            Duration blockDuration = Duration.getInstance(time, TimeUnit.MINUTES);
            Date finish = calendar.getDate(offset, blockDuration, false);
            previousAssignment.setFinish(finish);
            if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
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
    * @param assignment parent assignment
    * @param normaliser normaliser associated with this data
    * @param data timephased baseline work data block
    * @param raw flag indicating if this data is to be treated as raw
    * @return timephased work
    */
   public TimephasedWorkContainer getBaselineWork(ResourceAssignment assignment, TimephasedNormaliser<TimephasedWork> normaliser, byte[] data, boolean raw)
   {
      TimephasedWorkContainer result = null;
      if (data == null || data.length == 0)
      {
         return result;
      }

      List<TimephasedWork> list = null;

      //System.out.println(ByteArrayHelper.hexdump(data, false));
      int index = 8; // 8 byte header
      int blockSize = 40;
      double previousCumulativeWorkPerformedInMinutes = 0;

      Date blockStartDate = MPPUtility.getTimestampFromTenths(data, index + 36);
      index += blockSize;
      TimephasedWork work = null;

      while (index + blockSize <= data.length)
      {
         double cumulativeWorkInMinutes = (double) ((long) MPPUtility.getDouble(data, index + 20)) / 1000;
         if (!Duration.durationValueEquals(cumulativeWorkInMinutes, previousCumulativeWorkPerformedInMinutes))
         {
            //double unknownWorkThisPeriodInMinutes = ((long) MPPUtility.getDouble(data, index + 0)) / 1000;
            double normalActualWorkThisPeriodInMinutes = ((double) MPPUtility.getInt(data, index + 8)) / 10;
            double normalRemainingWorkThisPeriodInMinutes = ((double) MPPUtility.getInt(data, index + 28)) / 10;
            double workThisPeriodInMinutes = cumulativeWorkInMinutes - previousCumulativeWorkPerformedInMinutes;
            double overtimeWorkThisPeriodInMinutes = workThisPeriodInMinutes - (normalActualWorkThisPeriodInMinutes + normalRemainingWorkThisPeriodInMinutes);
            double overtimeFactor = overtimeWorkThisPeriodInMinutes / (normalActualWorkThisPeriodInMinutes + normalRemainingWorkThisPeriodInMinutes);

            double normalWorkPerDayInMinutes = 480;
            double overtimeWorkPerDayInMinutes = normalWorkPerDayInMinutes * overtimeFactor;

            work = new TimephasedWork();
            work.setFinish(MPPUtility.getTimestampFromTenths(data, index + 16));
            work.setStart(blockStartDate);
            work.setTotalAmount(Duration.getInstance(workThisPeriodInMinutes, TimeUnit.MINUTES));
            work.setAmountPerDay(Duration.getInstance(normalWorkPerDayInMinutes + overtimeWorkPerDayInMinutes, TimeUnit.MINUTES));

            previousCumulativeWorkPerformedInMinutes = cumulativeWorkInMinutes;

            if (list == null)
            {
               list = new ArrayList<>();
            }
            list.add(work);
            //System.out.println(work);
         }
         blockStartDate = MPPUtility.getTimestampFromTenths(data, index + 36);
         index += blockSize;
      }

      if (list != null)
      {
         if (work != null)
         {
            work.setFinish(assignment.getFinish());
         }
         result = new DefaultTimephasedWorkContainer(assignment, normaliser, list, raw);
      }

      return result;
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

      Date blockStartDate = MPPUtility.getTimestampFromTenths(data, index + 16);
      index += blockSize;

      while (index + blockSize <= data.length)
      {
         Date blockEndDate = MPPUtility.getTimestampFromTenths(data, index + 16);
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
