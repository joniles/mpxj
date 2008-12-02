/*
 * file:       TimephasedResourceAssignmentFactory
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2008
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedResourceAssignment;
import net.sf.mpxj.utility.DateUtility;

/**
 * This class contains methods to create lists of TimephasedResourceAssignment
 * instances for both complete and planned work relating to as resource
 * assignment.
 */
final class TimephasedResourceAssignmentFactory
{
   /**
    * Given a block of data representing completed work, this method will
    * retrieve a set of TimephasedResourceAssignment instances which represent
    * the day by day work carried out for a specific resource assignment.
    *
    * @param calendar calendar on which date calculations are based
    * @param startDate assignment start date 
    * @param data completed work data block
    * @return list of TimephasedResourceAssignment instances
    */
   public List<TimephasedResourceAssignment> getCompleteWork(ProjectCalendar calendar, Date startDate, byte[] data)
   {
      LinkedList<TimephasedResourceAssignment> list = new LinkedList<TimephasedResourceAssignment>();

      if (data != null)
      {
         int blockCount = MPPUtility.getShort(data, 0);
         double previousCumulativeWork = 0;
         TimephasedResourceAssignment previousAssignment = null;

         int index = 32;
         int currentBlock = 0;
         while (currentBlock < blockCount && index + 20 <= data.length)
         {
            double time = MPPUtility.getInt(data, index + 0);
            time /= 4800;
            Duration startWork = Duration.getInstance(time, TimeUnit.HOURS);

            double currentCumulativeWork = (long) MPPUtility.getDouble(data, index + 4);
            double assignmentDuration = currentCumulativeWork - previousCumulativeWork;
            previousCumulativeWork = currentCumulativeWork;
            assignmentDuration /= 60000;
            Duration totalWork = Duration.getInstance(assignmentDuration, TimeUnit.HOURS);
            time = (long) MPPUtility.getDouble(data, index + 12);
            time /= 1250;
            Duration workPerDay = Duration.getInstance(time, TimeUnit.HOURS);

            Date start = calendar.getDate(startDate, startWork, true);

            TimephasedResourceAssignment assignment = new TimephasedResourceAssignment();
            assignment.setStart(start);
            assignment.setWorkPerDay(workPerDay);
            assignment.setTotalWork(totalWork);

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
            time /= 4800;
            Duration finishWork = Duration.getInstance(time, TimeUnit.HOURS);
            Date finish = calendar.getDate(startDate, finishWork, false);
            previousAssignment.setFinish(finish);
            if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
            {
               list.removeLast();
            }
         }
      }

      coalesceAssignments(calendar, list);

      return list;
   }

   /**
    * Given a block of data representing planned work, this method will
    * retrieve a set of TimephasedResourceAssignment instances which represent
    * the day by day work planned for a specific resource assignment.
    *
    * @param calendar calendar on which date calculations are based
    * @param startDate assignment start date 
    * @param data planned work data block
    * @param timephasedComplete list of complete work 
    * @return list of TimephasedResourceAssignment instances 
    */
   public List<TimephasedResourceAssignment> getPlannedWork(ProjectCalendar calendar, Date startDate, byte[] data, List<TimephasedResourceAssignment> timephasedComplete)
   {
      LinkedList<TimephasedResourceAssignment> list = new LinkedList<TimephasedResourceAssignment>();

      if (data != null)
      {
         int blockCount = MPPUtility.getShort(data, 0);
         if (blockCount == 0)
         {
            if (!timephasedComplete.isEmpty())
            {
               TimephasedResourceAssignment lastComplete = timephasedComplete.get(timephasedComplete.size() - 1);

               Date startWork = calendar.getNextWorkStart(lastComplete.getFinish());
               double time = MPPUtility.getInt(data, 24);
               time /= 4800;
               Duration totalWork = Duration.getInstance(time, TimeUnit.HOURS);
               Date finish = calendar.getDate(startWork, totalWork, false);

               time = MPPUtility.getDouble(data, 8);
               time /= 20000;
               Duration workPerDay = Duration.getInstance(time, TimeUnit.HOURS);

               TimephasedResourceAssignment assignment = new TimephasedResourceAssignment();
               assignment.setStart(startWork);
               assignment.setWorkPerDay(workPerDay);
               assignment.setModified(false);
               assignment.setFinish(finish);
               assignment.setTotalWork(totalWork);

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
               TimephasedResourceAssignment lastComplete = timephasedComplete.get(timephasedComplete.size() - 1);
               offset = lastComplete.getFinish();
            }

            int index = 40;
            double previousCumulativeWork = 0;
            TimephasedResourceAssignment previousAssignment = null;
            int currentBlock = 0;

            while (currentBlock < blockCount && index + 28 <= data.length)
            {
               double time = MPPUtility.getInt(data, index);
               time /= 4800;
               Duration blockDuration = Duration.getInstance(time, TimeUnit.HOURS);
               Date start = calendar.getDate(offset, blockDuration, true);

               double currentCumulativeWork = MPPUtility.getDouble(data, index + 4);
               double assignmentDuration = currentCumulativeWork - previousCumulativeWork;
               assignmentDuration /= 60000;
               Duration totalWork = Duration.getInstance(assignmentDuration, TimeUnit.HOURS);
               previousCumulativeWork = currentCumulativeWork;

               time = MPPUtility.getDouble(data, index + 12);
               time /= 20000;
               Duration workPerDay = Duration.getInstance(time, TimeUnit.HOURS);

               int modifiedFlag = MPPUtility.getShort(data, index + 22);
               boolean modified = (modifiedFlag == 0 && currentBlock != 0) || ((modifiedFlag & 0x3000) != 0);

               TimephasedResourceAssignment assignment = new TimephasedResourceAssignment();
               assignment.setStart(start);
               assignment.setWorkPerDay(workPerDay);
               assignment.setModified(modified);
               assignment.setTotalWork(totalWork);

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
               time /= 4800;
               Duration blockDuration = Duration.getInstance(time, TimeUnit.HOURS);
               Date finish = calendar.getDate(offset, blockDuration, false);
               previousAssignment.setFinish(finish);
               if (previousAssignment.getStart().getTime() == previousAssignment.getFinish().getTime())
               {
                  list.removeLast();
               }
            }
         }
      }

      coalesceAssignments(calendar, list);

      return list;
   }

   /**
    * Test the list of TimephasedResourceAssignment instances to see
    * if any of them have been modified. 
    * 
    * @param list list of TimephasedResourceAssignment instances
    * @return boolean flag
    */
   public boolean getWorkModified(List<TimephasedResourceAssignment> list)
   {
      boolean result = false;
      for (TimephasedResourceAssignment assignment : list)
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
    * This method converts the raw ranges supplied by MS project into
    * date ranges which make it straightforward to determine the number
    * of hours planned or completed on a given day. 
    * 
    * @param calendar current calendar
    * @param list list of timephased assignment ranges
    */
   private void coalesceAssignments(ProjectCalendar calendar, List<TimephasedResourceAssignment> list)
   {
      if (!list.isEmpty())
      {
         LinkedList<TimephasedResourceAssignment> result = new LinkedList<TimephasedResourceAssignment>();

         TimephasedResourceAssignment previousAssignment = null;
         for (TimephasedResourceAssignment assignment : list)
         {
            Date startDate = DateUtility.getDayStartDate(assignment.getStart());
            Date endDate = DateUtility.getDayStartDate(assignment.getFinish());

            if (previousAssignment == null)
            {
               //
               // If this is the first item in the list, the range is not
               // on one day, and the range does not start at the working
               // start of the day - then we need to split this off.
               //
               if (DateUtility.compare(startDate, endDate) == 0)
               {
                  // we don't need to do anything to this... yet
                  result.add(assignment);
               }
               else
               {
                  Date startTime = DateUtility.getCanonicalTime(assignment.getStart());
                  Date workStartTime = calendar.getStartTime(startDate);
                  if (DateUtility.compare(startTime, workStartTime) != 0)
                  {
                     // this will now become our previous assignment
                     result.addAll(splitFirstDay(calendar, assignment));
                     assignment = result.getLast();
                  }
                  else
                  {
                     result.add(assignment);
                  }
               }
            }
            else
            {
               Date previousAssignmentFinish = previousAssignment.getFinish();
               Date assignmentStart = assignment.getStart();
               Date previousAssignmentFinishDay = DateUtility.getDayStartDate(previousAssignmentFinish);
               Date assignmentStartDay = DateUtility.getDayStartDate(assignmentStart);

               if (previousAssignmentFinishDay.getTime() != assignmentStartDay.getTime())
               {
                  // the assignments don't overlap - so do nothing
                  result.add(assignment);
               }
               else
               {
                  // the assignments overlap, so we need to split the overlapping
                  // parts off of each end of the two assignments to create
                  // a new one. The exception here is when the first assignment is only
                  // one day, then we just add the extra time to it.
                  Date previousAssignmentStart = previousAssignment.getStart();
                  Date previousAssignmentStartDay = DateUtility.getDayStartDate(previousAssignmentStart);

                  // we need to trim a bit off of the previous assignment.
                  // first of all, remove it from the result list
                  result.removeLast();

                  TimephasedResourceAssignment merge1;
                  if (previousAssignmentStartDay.getTime() == previousAssignmentFinishDay.getTime())
                  {
                     merge1 = previousAssignment;
                  }
                  else
                  {
                     List<TimephasedResourceAssignment> split = splitLastDay(calendar, previousAssignment);
                     result.add(split.get(0));
                     merge1 = split.get(1);
                  }

                  List<TimephasedResourceAssignment> split = splitFirstDay(calendar, assignment);
                  TimephasedResourceAssignment merge2 = split.get(0);
                  assignment = split.get(1);

                  if (merge1.getWorkPerDay().getDuration() == 0 && merge2.getWorkPerDay().getDuration() != 0)
                  {
                     result.add(merge2);
                  }
                  else
                  {
                     if (merge1.getWorkPerDay().getDuration() != 0 && merge2.getWorkPerDay().getDuration() == 0)
                     {
                        result.add(merge1);
                     }
                     else
                     {
                        result.add(merge(merge1, merge2));
                     }
                  }
                  result.add(assignment);
               }
            }

            previousAssignment = assignment;
         }

         truncateLast(calendar, result);
         list.clear();
         list.addAll(result);
      }
   }

   /**
    * This method creates two ranges by breaking the first day off of
    * the given range.
    * 
    * @param calendar current calendar
    * @param assignment range to break up
    * @return list containing the new ranges
    */
   private List<TimephasedResourceAssignment> splitFirstDay(ProjectCalendar calendar, TimephasedResourceAssignment assignment)
   {
      List<TimephasedResourceAssignment> result = new ArrayList<TimephasedResourceAssignment>(2);

      //
      // Get the date of the first day
      //
      Date splitStart = assignment.getStart();
      Date splitStartDay = DateUtility.getDayStartDate(splitStart);

      //
      // When do we normally start and finish work on that day
      //
      Date workStartTime = calendar.getStartTime(splitStartDay);
      Date workFinishTime = calendar.getFinishTime(splitStartDay);
      Date workStart = DateUtility.setTime(splitStartDay, workStartTime);
      Date workFinish = DateUtility.setTime(splitStartDay, workFinishTime);

      //
      // How much work do we normally do if this was a full day
      //
      Duration work = calendar.getWork(workStart, workFinish, TimeUnit.MINUTES);

      //
      // How much work could we do on this actual day
      //
      Duration splitWork = calendar.getWork(splitStart, workFinish, TimeUnit.MINUTES);

      //
      // Calculate the pro-rata duration for the split
      //
      double splitHours = assignment.getWorkPerDay().getDuration();
      splitHours *= splitWork.getDuration();
      splitHours /= work.getDuration();
      splitWork = Duration.getInstance(splitHours, TimeUnit.HOURS);

      //
      // Now we have enough information to create the first split
      //
      TimephasedResourceAssignment split = new TimephasedResourceAssignment();
      split.setModified(assignment.getModified());
      split.setStart(splitStart);
      split.setFinish(workFinish);
      split.setTotalWork(splitWork);
      split.setWorkPerDay(splitWork);
      result.add(split);

      //
      // Now allocate the remainder to the second split
      //
      Duration remainingWork = Duration.getInstance(assignment.getTotalWork().getDuration() - splitWork.getDuration(), TimeUnit.HOURS);
      splitStart = calendar.getNextWorkStart(workFinish);

      split = new TimephasedResourceAssignment();
      split.setModified(assignment.getModified());
      split.setStart(splitStart);
      split.setFinish(assignment.getFinish());
      split.setTotalWork(remainingWork);
      split.setWorkPerDay(assignment.getWorkPerDay());
      result.add(split);

      return result;
   }

   /**
    * This method creates two ranges by breaking the last day off of
    * the given range.
    * 
    * @param calendar current calendar
    * @param assignment range to break up
    * @return list containing the new ranges
    */
   private List<TimephasedResourceAssignment> splitLastDay(ProjectCalendar calendar, TimephasedResourceAssignment assignment)
   {
      List<TimephasedResourceAssignment> result = new ArrayList<TimephasedResourceAssignment>(2);

      //
      // Get the date of the last day
      //
      Date splitFinish = assignment.getFinish();
      Date splitFinishDay = DateUtility.getDayStartDate(splitFinish);

      //
      // When do we normally start and finish work on that day
      //
      Date workStartTime = calendar.getStartTime(splitFinishDay);
      Date workFinishTime = calendar.getFinishTime(splitFinishDay);
      Date workStart = DateUtility.setTime(splitFinishDay, workStartTime);
      Date workFinish = DateUtility.setTime(splitFinishDay, workFinishTime);

      //
      // How much work do we normally do if this was a full day
      //
      Duration work = calendar.getWork(workStart, workFinish, TimeUnit.MINUTES);

      //
      // How much work could we do on this actual day
      //
      Duration splitWork = calendar.getWork(workStart, splitFinish, TimeUnit.MINUTES);

      //
      // Calculate the pro-rata duration for the split
      //
      double splitHours = assignment.getWorkPerDay().getDuration();
      splitHours *= splitWork.getDuration();
      splitHours /= work.getDuration();
      splitWork = Duration.getInstance(splitHours, TimeUnit.HOURS);

      //
      // Now we have enough information to create the first split
      //
      TimephasedResourceAssignment split = new TimephasedResourceAssignment();
      split.setModified(assignment.getModified());
      split.setStart(workStart);
      split.setFinish(splitFinish);
      split.setTotalWork(splitWork);
      split.setWorkPerDay(splitWork);
      result.add(split);

      //
      // Find the previous working day
      //
      Calendar cal = Calendar.getInstance();
      Date currentDate = splitFinishDay;
      cal.setTime(currentDate);
      do
      {
         cal.add(Calendar.DAY_OF_YEAR, -1);
         currentDate = cal.getTime();
      }
      while (calendar.isWorkingDate(currentDate) == false);

      Date finishTime = calendar.getFinishTime(currentDate);
      currentDate = DateUtility.setTime(currentDate, finishTime);
      Duration totalWork = Duration.getInstance(assignment.getTotalWork().getDuration() - splitWork.getDuration(), TimeUnit.HOURS);

      split = new TimephasedResourceAssignment();
      split.setModified(assignment.getModified());
      split.setStart(assignment.getStart());
      split.setFinish(currentDate);
      split.setTotalWork(totalWork);
      split.setWorkPerDay(assignment.getWorkPerDay());
      result.add(0, split);

      return result;
   }

   /**
    * Merges two ranges into one.
    * 
    * @param merge1 first range
    * @param merge2 second range
    * @return single merged range
    */
   private TimephasedResourceAssignment merge(TimephasedResourceAssignment merge1, TimephasedResourceAssignment merge2)
   {
      double hours1 = merge1.getTotalWork().getDuration();
      double hours2 = merge2.getTotalWork().getDuration();
      Duration work = Duration.getInstance(hours1 + hours2, TimeUnit.HOURS);

      TimephasedResourceAssignment split = new TimephasedResourceAssignment();
      split.setModified(merge1.getModified());
      split.setStart(merge1.getStart());
      split.setFinish(merge2.getFinish());
      split.setTotalWork(work);
      split.setWorkPerDay(work);

      return split;
   }

   /**
    * Handles the last range appropriately.
    * 
    * @param calendar current calendar
    * @param list processed list of ranges
    */
   private void truncateLast(ProjectCalendar calendar, LinkedList<TimephasedResourceAssignment> list)
   {
      if (!list.isEmpty())
      {
         TimephasedResourceAssignment assignment = list.getLast();

         Date startDay = DateUtility.getDayStartDate(assignment.getStart());
         Date finishDay = DateUtility.getDayStartDate(assignment.getFinish());
         if (startDay.getTime() == finishDay.getTime())
         {
            Duration workPerDay = assignment.getWorkPerDay();
            Duration totalWork = assignment.getTotalWork();
            if (totalWork.getDuration() < workPerDay.getDuration())
            {
               TimephasedResourceAssignment split = new TimephasedResourceAssignment();
               split.setModified(assignment.getModified());
               split.setStart(assignment.getStart());
               split.setFinish(assignment.getFinish());
               split.setTotalWork(totalWork);
               split.setWorkPerDay(totalWork);
               list.removeLast();
               list.add(split);
            }
         }
         else
         {
            Date assignmentFinish = assignment.getFinish();
            Date calendarFinishTime = calendar.getFinishTime(assignmentFinish);
            Date assignmentFinishTime = DateUtility.getCanonicalTime(assignmentFinish);
            if (assignmentFinishTime.getTime() < calendarFinishTime.getTime())
            {
               list.removeLast();
               list.addAll(splitLastDay(calendar, assignment));
            }
         }
      }
   }
}
