/*
 * file:       TimephasedResourceAssignmentFactory
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

      if (data != null && data.length > 0)
      {
         int blockCount = MPPUtility.getShort(data, 0);
         double previousCumulativeWork = 0;
         TimephasedResourceAssignment previousAssignment = null;

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
    * retrieve a set of TimephasedResourceAssignment instances which represent
    * the day by day work planned for a specific resource assignment.
    *
    * @param calendar calendar on which date calculations are based
    * @param startDate assignment start date
    * @param units assignment units 
    * @param data planned work data block
    * @param timephasedComplete list of complete work 
    * @return list of TimephasedResourceAssignment instances 
    */
   public List<TimephasedResourceAssignment> getPlannedWork(ProjectCalendar calendar, Date startDate, double units, byte[] data, List<TimephasedResourceAssignment> timephasedComplete)
   {
      LinkedList<TimephasedResourceAssignment> list = new LinkedList<TimephasedResourceAssignment>();

      if (data != null && data.length > 0)
      {
         int blockCount = MPPUtility.getShort(data, 0);
         if (blockCount == 0)
         {
            if (!timephasedComplete.isEmpty())
            {
               TimephasedResourceAssignment lastComplete = timephasedComplete.get(timephasedComplete.size() - 1);

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
}
