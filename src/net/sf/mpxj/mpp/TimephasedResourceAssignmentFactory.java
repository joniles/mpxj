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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedResourceAssignment;

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

               Date startWork = lastComplete.getFinish();
               double time = MPPUtility.getInt(data, 24);
               time /= 4800;
               Duration totalWork = Duration.getInstance(time, TimeUnit.HOURS);
               Date finish = calendar.getDate(startWork, totalWork, false);

               TimephasedResourceAssignment assignment = new TimephasedResourceAssignment();
               assignment.setStart(startWork);
               assignment.setWorkPerDay(lastComplete.getWorkPerDay());
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
   /*
      private void coalesceAssignments(ProjectCalendar calendar, List<TimephasedResourceAssignment> list)
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
                     assignment = splitFirstDay(calendar, assignment, result);
                  }
               }
            }
            else
            {
               Date previousAssignmentFinish = previousAssignment.getFinish();
               Date assignmentStart = assignment.getStart();
               Date previousAssignmentFinishDay = DateUtility.getDayStartDate(previousAssignmentFinish);
               Date assignmentStartDay = DateUtility.getDayEndDate(assignmentStart);

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
                  TimephasedResourceAssignment merge;
                  // we need to trim a bit off of the previous assignment.
                  // first of all, remove it from the result list
                  result.removeLast();

                  if (previousAssignmentStartDay.getTime() == previousAssignmentFinishDay.getTime())
                  {
                     merge = previousAssignment;
                  }
                  else
                  {
                     // find the previous working day
                     Calendar cal = Calendar.getInstance();
                     Date currentDate = previousAssignmentFinishDay;
                     cal.setTime(currentDate);
                     while (calendar.isWorkingDate(currentDate) == false)
                     {
                        cal.add(Calendar.DAY_OF_YEAR, -11);
                        currentDate = cal.getTime();
                     }
                     Date finishTime = calendar.getFinishTime(currentDate);
                     currentDate = DateUtility.setTime(currentDate, finishTime);
                     Duration splitWork = calendar.getWork(currentDate, previousAssignmentFinish, TimeUnit.HOURS);
                     Duration totalWork = Duration.getInstance(previousAssignment.getTotalWork().getDuration() - splitWork.getDuration(), TimeUnit.HOURS);

                     // now we can create the new assignment                     
                     TimephasedResourceAssignment split = new TimephasedResourceAssignment();
                     split.setModified(previousAssignment.getModified());
                     split.setStart(previousAssignment.getStart());
                     split.setFinish(currentDate);
                     split.setTotalWork(totalWork);
                     split.setWorkPerDay(previousAssignment.getWorkPerDay());
                     result.add(split);

                     // now we can create the new assignment we are going to
                     // merge with the following one
                     Date mergeStart = calendar.getNextWorkStart(finishTime);
                     totalWork = calendar.getWork(mergeStart, previousAssignment.getFinish(), TimeUnit.HOURS);

                     merge = new TimephasedResourceAssignment();
                     merge.setModified(previousAssignment.getModified());
                     merge.setStart(mergeStart);
                     merge.setFinish(previousAssignment.getFinish());
                     merge.setTotalWork(totalWork);
                     merge.setWorkPerDay(totalWork);
                  }

                  // at this point, the result list should be correct,
                  // and we should have an assignment called "merge"
                  // into which we are going to merge the start of the
                  // next block. Our next step is therefore that we will
                  // chop off the overlapping day from the following block.

                  // then we do the merge the two blocks and add them to the result
                  // list

                  // finally, we add the truncated block to the end o fthe result list
                  // job done!
               }
            }

            previousAssignment = assignment;
         }
      }

      private TimephasedResourceAssignment splitFirstDay (ProjectCalendar calendar, TimephasedResourceAssignment assignment, List<TimephasedResourceAssignment> result)
      {
         Date startDate = DateUtility.getDayStartDate(assignment.getStart());
         Date workStartTime = calendar.getStartTime(startDate);
         Date endDate = DateUtility.getDayStartDate(assignment.getFinish());
         
         Date workFinishTime = calendar.getFinishTime(assignment.getStart());
         Date splitStart = DateUtility.setTime(startDate, workStartTime);
         Date splitFinish = DateUtility.setTime(startDate, workFinishTime);
         Duration work = calendar.getWork(startDate, endDate, TimeUnit.HOURS);

         // split off the first day
         TimephasedResourceAssignment split = new TimephasedResourceAssignment();
         split.setModified(assignment.getModified());
         split.setStart(splitStart);
         split.setFinish(splitFinish);
         split.setTotalWork(work);
         split.setWorkPerDay(work);
         result.add(split);

         // now create a second split representing the rest
         // of the assignment
         Duration remainingWork = Duration.getInstance(assignment.getTotalWork().getDuration() - work.getDuration(), TimeUnit.HOURS);
         splitStart = calendar.getNextWorkStart(splitFinish);

         split = new TimephasedResourceAssignment();
         split.setModified(assignment.getModified());
         split.setStart(splitStart);
         split.setFinish(splitFinish);
         split.setTotalWork(remainingWork);
         split.setWorkPerDay(work);
         result.add(split);
         
         return split;
      }
   */
}
