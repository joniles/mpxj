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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedCost;
import net.sf.mpxj.TimephasedCostContainer;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.TimephasedWorkContainer;
import net.sf.mpxj.common.DefaultTimephasedCostContainer;
import net.sf.mpxj.common.DefaultTimephasedWorkContainer;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.TimephasedCostNormaliser;
import net.sf.mpxj.common.TimephasedWorkNormaliser;

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
    * @param startDate assignment start date
    * @param data completed work data block
    * @return list of TimephasedWork instances
    */
   public List<TimephasedWork> getCompleteWork(ProjectCalendar calendar, Date startDate, byte[] data)
   {
      LinkedList<TimephasedWork> list = new LinkedList<TimephasedWork>();

      if (calendar != null && data != null && data.length > 0)
      {
         int blockCount = MPPUtility.getShort(data, 0);
         double previousCumulativeWork = 0;
         TimephasedWork previousAssignment = null;

         int index = 32;
         int currentBlock = 0;
         while (currentBlock < blockCount && index + 20 <= data.length)
         {
            double time = MPPUtility.getInt(data, index + 0);
            time /= 80;
            Duration startWork = Duration.getInstance(time, TimeUnit.MINUTES);

            double currentCumulativeWork = (long) MPPUtility.getDouble(data, index + 4);
            double assignmentDuration = currentCumulativeWork - previousCumulativeWork;
            previousCumulativeWork = currentCumulativeWork;
            assignmentDuration /= 1000;
            Duration totalWork = Duration.getInstance(assignmentDuration, TimeUnit.MINUTES);
            time = (long) MPPUtility.getDouble(data, index + 12);
            time /= 125;
            time *= 6;
            Duration workPerDay = Duration.getInstance(time, TimeUnit.MINUTES);

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
            assignment.setAmountPerDay(workPerDay);
            assignment.setTotalAmount(totalWork);

            if (previousAssignment != null)
            {
               Date finish = calendar.getDate(startDate, startWork, false);
               previousAssignment.setFinish(finish);
               if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
               {
                  list.removeLast();
               }
            }

            list.add(assignment);
            previousAssignment = assignment;

            index += 20;
            ++currentBlock;
         }

         if (previousAssignment != null)
         {
            double time = MPPUtility.getInt(data, 24);
            time /= 80;
            Duration finishWork = Duration.getInstance(time, TimeUnit.MINUTES);
            Date finish = calendar.getDate(startDate, finishWork, false);
            previousAssignment.setFinish(finish);
            if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
            {
               list.removeLast();
            }
         }
      }

      return list;
   }

   /**
    * Given a block of data representing planned work, this method will
    * retrieve a set of TimephasedWork instances which represent
    * the day by day work planned for a specific resource assignment.
    *
    * @param calendar calendar on which date calculations are based
    * @param startDate assignment start date
    * @param units assignment units
    * @param data planned work data block
    * @param timephasedComplete list of complete work
    * @return list of TimephasedWork instances
    */
   public List<TimephasedWork> getPlannedWork(ProjectCalendar calendar, Date startDate, double units, byte[] data, List<TimephasedWork> timephasedComplete)
   {
      LinkedList<TimephasedWork> list = new LinkedList<TimephasedWork>();

      if (calendar != null && data != null && data.length > 0)
      {
         int blockCount = MPPUtility.getShort(data, 0);
         if (blockCount == 0)
         {
            if (!timephasedComplete.isEmpty() && units != 0)
            {
               TimephasedWork lastComplete = timephasedComplete.get(timephasedComplete.size() - 1);

               Date startWork = calendar.getNextWorkStart(lastComplete.getFinish());
               double time = MPPUtility.getDouble(data, 16);
               time /= 1000;
               Duration totalWork = Duration.getInstance(time, TimeUnit.MINUTES);
               Duration adjustedTotalWork = Duration.getInstance((time * 100) / units, TimeUnit.MINUTES);
               Date finish = calendar.getDate(startWork, adjustedTotalWork, false);

               time = MPPUtility.getDouble(data, 8);
               time /= 2000;
               time *= 6;
               Duration workPerDay = Duration.getInstance(time, TimeUnit.MINUTES);

               TimephasedWork assignment = new TimephasedWork();
               assignment.setStart(startWork);
               assignment.setAmountPerDay(workPerDay);
               assignment.setModified(false);
               assignment.setFinish(finish);
               assignment.setTotalAmount(totalWork);

               if (assignment.getStart().getTime() != assignment.getFinish().getTime())
               {
                  list.add(assignment);
               }
            }
         }
         else
         {
            Date offset = startDate;

            if (!timephasedComplete.isEmpty())
            {
               TimephasedWork lastComplete = timephasedComplete.get(timephasedComplete.size() - 1);
               offset = lastComplete.getFinish();
            }

            int index = 40;
            double previousCumulativeWork = 0;
            TimephasedWork previousAssignment = null;
            int currentBlock = 0;

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

               time = MPPUtility.getDouble(data, index + 12);
               time /= 2000;
               time *= 6;
               Duration workPerDay = Duration.getInstance(time, TimeUnit.MINUTES);

               int modifiedFlag = MPPUtility.getShort(data, index + 22);
               boolean modified = (modifiedFlag == 0 && currentBlock != 0) || ((modifiedFlag & 0x3000) != 0);

               TimephasedWork assignment = new TimephasedWork();
               assignment.setStart(start);
               assignment.setAmountPerDay(workPerDay);
               assignment.setModified(modified);
               assignment.setTotalAmount(totalWork);

               if (previousAssignment != null)
               {
                  Date finish = calendar.getDate(offset, blockDuration, false);
                  previousAssignment.setFinish(finish);
                  if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
                  {
                     list.removeLast();
                  }
               }

               list.add(assignment);
               previousAssignment = assignment;

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
                  list.removeLast();
               }
            }
         }
      }

      return list;
   }

   /**
    * Test the list of TimephasedWork instances to see
    * if any of them have been modified.
    *
    * @param list list of TimephasedWork instances
    * @return boolean flag
    */
   public boolean getWorkModified(List<TimephasedWork> list)
   {
      boolean result = false;
      for (TimephasedWork assignment : list)
      {
         result = assignment.getModified();
         if (result)
         {
            break;
         }
      }
      return result;
   }

   /**
    * Extracts baseline work from the MPP file for a specific baseline.
    * Returns null if no baseline work is present, otherwise returns
    * a list of timephased work items.
    *
    * @param assignment parent assignment
    * @param calendar baseline calendar
    * @param normaliser normaliser associated with this data
    * @param data timephased baseline work data block
    * @param raw flag indicating if this data is to be treated as raw
    * @return timephased work
    */
   public TimephasedWorkContainer getBaselineWork(ResourceAssignment assignment, ProjectCalendar calendar, TimephasedWorkNormaliser normaliser, byte[] data, boolean raw)
   {
      TimephasedWorkContainer result = null;

      if (data != null && data.length > 0)
      {
         LinkedList<TimephasedWork> list = null;

         //System.out.println(MPPUtility.hexdump(data, false));
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
                  list = new LinkedList<TimephasedWork>();
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
            result = new DefaultTimephasedWorkContainer(calendar, normaliser, list, raw);
         }
      }

      return result;
   }

   /**
    * Extracts baseline cost from the MPP file for a specific baseline.
    * Returns null if no baseline cost is present, otherwise returns
    * a list of timephased work items.
    *
    * @param calendar baseline calendar
    * @param normaliser normaliser associated with this data
    * @param data timephased baseline work data block
    * @param raw flag indicating if this data is to be treated as raw
    * @return timephased work
    */
   public TimephasedCostContainer getBaselineCost(ProjectCalendar calendar, TimephasedCostNormaliser normaliser, byte[] data, boolean raw)
   {
      TimephasedCostContainer result = null;

      if (data != null && data.length > 0)
      {
         LinkedList<TimephasedCost> list = null;

         //System.out.println(MPPUtility.hexdump(data, false));
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
                  list = new LinkedList<TimephasedCost>();
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
            result = new DefaultTimephasedCostContainer(calendar, normaliser, list, raw);
         }
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

}
