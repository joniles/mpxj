/*
 * file:       ResourceAssignment.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2003
 * date:       15/08/2002
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

package net.sf.mpxj;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.utility.DateUtility;

/**
 * This class represents a resource assignment record from an MPX file.
 */
public final class ResourceAssignment extends ProjectEntity
{
   /**
    * Default constructor.
    *
    * @param file The parent file to which this record belongs.
    * @param task The task to which this assignment is being made
    */
   ResourceAssignment(ProjectFile file, Task task)
   {
      super(file);

      m_task = task;
   }

   /**
    * This method allows a resource assignment workgroup fields record
    * to be added to the current resource assignment. A maximum of
    * one of these records can be added to a resource assignment record.
    *
    * @return ResourceAssignmentWorkgroupFields object
    * @throws MPXJException if MSP defined limit of 1 is exceeded
    */
   public ResourceAssignmentWorkgroupFields addWorkgroupAssignment() throws MPXJException
   {
      if (m_workgroup != null)
      {
         throw new MPXJException(MPXJException.MAXIMUM_RECORDS);
      }

      m_workgroup = new ResourceAssignmentWorkgroupFields();

      return (m_workgroup);
   }

   /**
    * Returns the units of this resource assignment.
    *
    * @return units
    */
   public Number getUnits()
   {
      return (m_units);
   }

   /**
    * Sets the units for this resource assignment.
    *
    * @param val units
    */
   public void setUnits(Number val)
   {
      m_units = val;
   }

   /**
    * Returns the work of this resource assignment.
    *
    * @return work
    */
   public Duration getWork()
   {
      return (m_work);
   }

   /**
    * Sets the work for this resource assignment.
    *
    * @param dur work
    */
   public void setWork(Duration dur)
   {
      m_work = dur;
   }

   /**
    * Returns the planned work of this resource assignment.
    *
    * @return planned work
    */
   public Duration getPlannedWork()
   {
      return (m_plannedWork);
   }

   /**
    * Sets the planned work for this resource assignment.
    *
    * @param dur planned work
    */
   public void setPlannedWork(Duration dur)
   {
      m_plannedWork = dur;
   }

   /**
    * Returns the actual completed work of this resource assignment.
    *
    * @return completed work
    */
   public Duration getActualWork()
   {
      return (m_actualWork);
   }

   /**
    * Sets the actual completed work for this resource assignment.
    *
    * @param dur actual completed work
    */
   public void setActualWork(Duration dur)
   {
      m_actualWork = dur;
   }

   /**
    * Returns the overtime work done of this resource assignment.
    *
    * @return overtime work
    */
   public Duration getOvertimeWork()
   {
      return (m_overtimeWork);
   }

   /**
    * Sets the overtime work for this resource assignment.
    *
    * @param dur overtime work
    */
   public void setOvertimeWork(Duration dur)
   {
      m_overtimeWork = dur;
   }

   /**
    * Returns the cost  of this resource assignment.
    *
    * @return cost
    */
   public Number getCost()
   {
      return (m_cost);
   }

   /**
    * Sets the cost for this resource assignment.
    *
    * @param val cost
    */
   public void setCost(Number val)
   {
      m_cost = val;
   }

   /**
    * Returns the planned cost for this resource assignment.
    *
    * @return planned cost
    */
   public Number getPlannedCost()
   {
      return (m_plannedCost);
   }

   /**
    * Sets the planned cost for this resource assignment.
    *
    * @param val planned cost
    */
   public void setPlannedCost(Number val)
   {
      m_plannedCost = val;
   }

   /**
    * Returns the actual cost for this resource assignment.
    *
    * @return actual cost
    */
   public Number getActualCost()
   {
      return (m_actualCost);
   }

   /**
    * Sets the actual cost so far incurred for this resource assignment.
    *
    * @param val actual cost
    */
   public void setActualCost(Number val)
   {
      m_actualCost = val;
   }

   /**
    * Returns the start of this resource assignment.
    *
    * @return start date
    */
   public Date getStart()
   {
      return (m_start);
   }

   /**
    * Sets the start date for this resource assignment.
    *
    * @param val start date
    */
   public void setStart(Date val)
   {
      m_start = val;
   }

   /**
    * Returns the finish date for this resource assignment.
    *
    * @return finish date
    */
   public Date getFinish()
   {
      return (m_finish);
   }

   /**
    * Sets the finish date for this resource assignment.
    *
    * @param val finish date
    */
   public void setFinish(Date val)
   {
      m_finish = val;
   }

   /**
    * Returns the delay for this resource assignment.
    *
    * @return delay
    */
   public Duration getDelay()
   {
      return (m_delay);
   }

   /**
    * Sets the delay for this resource assignment.
    *
    * @param dur delay
    */
   public void setDelay(Duration dur)
   {
      m_delay = dur;
   }

   /**
    * Returns the resources unique id for this resource assignment.
    *
    * @return resources unique id
    */
   public Integer getResourceUniqueID()
   {
      return (m_resourceUniqueID);
   }

   /**
    * Sets the resources unique id for this resource assignment.
    *
    * @param val resources unique id
    */
   public void setResourceUniqueID(Integer val)
   {
      if (val != null)
      {
         m_resourceUniqueID = val;
      }
   }

   /**
    * Gets the Resource Assignment Workgroup Fields if one exists.
    *
    * @return workgroup assignment object
    */
   public ResourceAssignmentWorkgroupFields getWorkgroupAssignment()
   {
      return (m_workgroup);
   }

   /**
    * This method retrieves a reference to the task with which this
    * assignment is associated.
    *
    * @return task
    */
   public Task getTask()
   {
      return (m_task);
   }

   /**
    * This method retrieves a reference to the resource with which this
    * assignment is associated.
    *
    * @return resource
    */
   public Resource getResource()
   {
      return (getParentFile().getResourceByUniqueID(getResourceUniqueID()));
   }

   /**
    * This method returns the Work Contour type of this Assignment.
    *
    * @return the Work Contour type
    */
   public WorkContour getWorkContour()
   {
      return (m_workContour);
   }

   /**
    * This method sets the Work Contour type of this Assignment.
    *
    * @param workContour the Work Contour type
    */
   public void setWorkContour(WorkContour workContour)
   {
      m_workContour = workContour;
   }

   /**
    * Removes this resource assignment from the project.
    */
   public void remove()
   {
      getParentFile().removeResourceAssignment(this);
   }

   /**
    * Returns the remaining work for this resource assignment.
    *
    * @return remaining work
    */
   public Duration getRemainingWork()
   {
      return (m_remainingWork);
   }

   /**
    * Sets the remaining work for this resource assignment.
    *
    * @param remainingWork remaining work
    */
   public void setRemainingWork(Duration remainingWork)
   {
      m_remainingWork = remainingWork;
   }

   /**
    * Retrieves the timephased breakdown of the completed work for this
    * resource assignment. 
    * 
    * @return timephased completed work
    */
   public List<TimephasedResourceAssignment> getTimephasedComplete()
   {
      if (m_timephasedCompleteRaw)
      {
         coalesceAssignments(m_timephasedComplete);
         m_timephasedCompleteRaw = false;
      }
      return m_timephasedComplete;
   }

   /**
    * Sets the timephased breakdown of the completed work for this
    * resource assignment.
    * 
    * @param timephasedComplete timephased completed work
    * @param raw flag indicating if the assignment data is raw
    */
   public void setTimephasedComplete(List<TimephasedResourceAssignment> timephasedComplete, boolean raw)
   {
      if (timephasedComplete instanceof LinkedList)
      {
         m_timephasedComplete = (LinkedList<TimephasedResourceAssignment>) timephasedComplete;
      }
      else
      {
         m_timephasedComplete = new LinkedList<TimephasedResourceAssignment>(timephasedComplete);
      }
      m_timephasedCompleteRaw = raw;
   }

   /**
    * Retrieves the timephased breakdown of the planned work for this
    * resource assignment. 
    * 
    * @return timephased planned work
    */
   public List<TimephasedResourceAssignment> getTimephasedPlanned()
   {
      if (m_timephasedPlannedRaw)
      {
         coalesceAssignments(m_timephasedPlanned);
         m_timephasedPlannedRaw = false;
      }
      return m_timephasedPlanned;
   }

   /**
    * Sets the timephased breakdown of the planned work for this
    * resource assignment.
    * 
    * @param timephasedPlanned timephased planned work
    * @param raw flag indicating if the assignment data is raw 
    */
   public void setTimephasedPlanned(List<TimephasedResourceAssignment> timephasedPlanned, boolean raw)
   {
      if (timephasedPlanned instanceof LinkedList)
      {
         m_timephasedPlanned = (LinkedList<TimephasedResourceAssignment>) timephasedPlanned;
      }
      else
      {
         m_timephasedPlanned = new LinkedList<TimephasedResourceAssignment>(timephasedPlanned);
      }
      m_timephasedPlannedRaw = raw;
   }

   /**
    * Retrieves the calendar used for this resource assignment.
    * 
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getCalendar()
   {
      ProjectCalendar calendar = null;
      Resource resource = getResource();
      if (resource != null)
      {
         calendar = resource.getResourceCalendar();
      }

      if (calendar == null)
      {
         Task task = getTask();
         task.getCalendar();
      }

      if (calendar == null)
      {
         ProjectFile file = getParentFile();
         String calendarName = file.getProjectHeader().getCalendarName();
         calendar = file.getBaseCalendar(calendarName);
      }

      return calendar;
   }

   /**
    * This method converts the internal representation of timephased 
    * resource assignment data used by MS Project into a standardised
    * format to make it easy to work with. 
    * 
    * @param list list of assignment data
    */
   private void coalesceAssignments(LinkedList<TimephasedResourceAssignment> list)
   {
      if (!list.isEmpty())
      {
         ProjectCalendar calendar = getCalendar();
         //dumpList(list);
         splitDays(calendar, list);
         //dumpList(list);
         mergeSameDay(calendar, list);
         //dumpList(list);
         mergeSameWork(list);
         //dumpList(list);         
         convertToHours(list);
         //dumpList(list);
      }
   }

   /**
    * This method breaks down spans of time into individual days.
    * 
    * @param calendar current project calendar
    * @param list list of assignment data
    */
   private void splitDays(ProjectCalendar calendar, LinkedList<TimephasedResourceAssignment> list)
   {
      LinkedList<TimephasedResourceAssignment> result = new LinkedList<TimephasedResourceAssignment>();
      boolean remainderInserted = false;
      Calendar cal = Calendar.getInstance();

      for (TimephasedResourceAssignment assignment : list)
      {
         if (remainderInserted)
         {
            cal.setTime(assignment.getStart());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            assignment.setStart(cal.getTime());
            remainderInserted = false;
         }

         while (assignment != null)
         {
            Date startDay = DateUtility.getDayStartDate(assignment.getStart());
            Date finishDay = DateUtility.getDayStartDate(assignment.getFinish());

            // special case - when the finishday time is midnight, it's really the previous day...                 
            if (assignment.getFinish().getTime() == finishDay.getTime())
            {
               cal.setTime(finishDay);
               cal.add(Calendar.DAY_OF_YEAR, -1);
               finishDay = cal.getTime();
            }

            if (startDay.getTime() == finishDay.getTime())
            {
               Duration totalWork = assignment.getTotalWork();
               Duration assignmentWork = getAssignmentWork(calendar, assignment);
               if (totalWork.getDuration() > assignmentWork.getDuration())
               {
                  assignment.setTotalWork(assignmentWork);
                  result.add(assignment);
                  Duration remainingWork = Duration.getInstance(totalWork.getDuration() - assignmentWork.getDuration(), TimeUnit.MINUTES);

                  cal.setTime(finishDay);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  Date remainderStart = cal.getTime();
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  Date remainderFinish = cal.getTime();

                  TimephasedResourceAssignment remainder = new TimephasedResourceAssignment();
                  remainder.setStart(remainderStart);
                  remainder.setFinish(remainderFinish);
                  remainder.setTotalWork(remainingWork);
                  result.add(remainder);

                  remainderInserted = true;
               }
               else
               {
                  result.add(assignment);
               }
               break;
            }

            TimephasedResourceAssignment[] split = splitFirstDay(calendar, assignment);
            if (split[0] != null)
            {
               result.add(split[0]);
            }
            assignment = split[1];
         }
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * This method splits the first day off of a time span.
    * 
    * @param calendar current calendar
    * @param assignment timephased assignment span
    * @return first day and remainder assignments
    */
   private TimephasedResourceAssignment[] splitFirstDay(ProjectCalendar calendar, TimephasedResourceAssignment assignment)
   {
      TimephasedResourceAssignment[] result = new TimephasedResourceAssignment[2];

      //
      // Retrieve data used to calculate the pro-rata work split
      //
      Date assignmentStart = assignment.getStart();
      Date assignmentFinish = assignment.getFinish();
      Duration calendarWork = calendar.getWork(assignmentStart, assignmentFinish, TimeUnit.MINUTES);
      Duration assignmentWork = assignment.getTotalWork();

      if (calendarWork.getDuration() != 0)
      {
         //
         // Split the first day
         //
         Date splitFinish;
         double splitMinutes;
         if (calendar.isWorkingDate(assignmentStart))
         {
            Date splitStart = assignmentStart;
            Date splitFinishTime = calendar.getFinishTime(splitStart);
            splitFinish = DateUtility.setTime(splitStart, splitFinishTime);

            Duration calendarSplitWork = calendar.getWork(splitStart, splitFinish, TimeUnit.MINUTES);
            Duration calendarWorkPerDay = calendar.getWork(splitStart, TimeUnit.MINUTES);
            Duration assignmentWorkPerDay = assignment.getWorkPerDay();
            Duration splitWork;

            if (calendarSplitWork.getDuration() == calendarWorkPerDay.getDuration())
            {
               {
                  if (calendarSplitWork.getDuration() == assignmentWorkPerDay.getDuration())
                  {
                     splitWork = assignmentWorkPerDay;
                     splitMinutes = splitWork.getDuration();
                  }
                  else
                  {
                     splitMinutes = assignmentWorkPerDay.getDuration();
                     splitMinutes *= calendarSplitWork.getDuration();
                     splitMinutes /= (8 * 60); // this appears to be a fixed value
                     splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
                  }
               }
            }
            else
            {
               splitMinutes = assignmentWorkPerDay.getDuration();
               splitMinutes *= calendarSplitWork.getDuration();
               splitMinutes /= (8 * 60); // this appears to be a fixed value
               splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
            }

            TimephasedResourceAssignment split = new TimephasedResourceAssignment();
            split.setStart(splitStart);
            split.setFinish(splitFinish);
            split.setTotalWork(splitWork);

            result[0] = split;
         }
         else
         {
            splitFinish = assignmentStart;
            splitMinutes = 0;
         }

         //
         // Split the remainder
         //
         Date splitStart = calendar.getNextWorkStart(splitFinish);
         splitFinish = assignmentFinish;
         TimephasedResourceAssignment split;
         if (splitStart.getTime() > splitFinish.getTime())
         {
            split = null;
         }
         else
         {
            splitMinutes = assignmentWork.getDuration() - splitMinutes;
            Duration splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);

            split = new TimephasedResourceAssignment();
            split.setStart(splitStart);
            split.setFinish(splitFinish);
            split.setTotalWork(splitWork);
            split.setWorkPerDay(assignment.getWorkPerDay());
         }

         result[1] = split;
      }
      return result;
   }

   /**
    * This method merges together assignment data for the same day.
    * 
    * @param calendar current calendar
    * @param list assignment data
    */
   private void mergeSameDay(ProjectCalendar calendar, LinkedList<TimephasedResourceAssignment> list)
   {
      LinkedList<TimephasedResourceAssignment> result = new LinkedList<TimephasedResourceAssignment>();

      TimephasedResourceAssignment previousAssignment = null;
      for (TimephasedResourceAssignment assignment : list)
      {
         if (previousAssignment == null)
         {
            assignment.setWorkPerDay(assignment.getTotalWork());
            result.add(assignment);
         }
         else
         {
            Date previousAssignmentStart = previousAssignment.getStart();
            Date previousAssignmentStartDay = DateUtility.getDayStartDate(previousAssignmentStart);
            Date assignmentStart = assignment.getStart();
            Date assignmentStartDay = DateUtility.getDayStartDate(assignmentStart);

            if (previousAssignmentStartDay.getTime() == assignmentStartDay.getTime())
            {
               Duration previousAssignmentWork = previousAssignment.getTotalWork();
               Duration assignmentWork = assignment.getTotalWork();

               if (previousAssignmentWork.getDuration() != 0 && assignmentWork.getDuration() == 0)
               {
                  continue;
               }

               Date previousAssignmentFinish = previousAssignment.getFinish();

               if (previousAssignmentFinish.getTime() == assignmentStart.getTime())
               {
                  result.removeLast();

                  if (previousAssignmentWork.getDuration() != 0 && assignmentWork.getDuration() != 0)
                  {
                     double work = previousAssignment.getTotalWork().getDuration();
                     work += assignment.getTotalWork().getDuration();
                     Duration totalWork = Duration.getInstance(work, TimeUnit.MINUTES);

                     TimephasedResourceAssignment merged = new TimephasedResourceAssignment();
                     merged.setStart(previousAssignment.getStart());
                     merged.setFinish(assignment.getFinish());
                     merged.setTotalWork(totalWork);
                     assignment = merged;
                  }
                  else
                  {
                     if (assignmentWork.getDuration() == 0)
                     {
                        assignment = previousAssignment;
                     }
                  }
               }
            }

            assignment.setWorkPerDay(assignment.getTotalWork());
            result.add(assignment);
         }

         Duration calendarWork = calendar.getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.MINUTES);
         Duration assignmentWork = assignment.getTotalWork();
         if (calendarWork.getDuration() == 0 && assignmentWork.getDuration() == 0)
         {
            result.removeLast();
         }
         else
         {
            previousAssignment = assignment;
         }
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * Merges individual days together into time spans where the
    * same work is undertaken each day.
    * 
    * @param list assignment data
    */
   private void mergeSameWork(LinkedList<TimephasedResourceAssignment> list)
   {
      LinkedList<TimephasedResourceAssignment> result = new LinkedList<TimephasedResourceAssignment>();

      TimephasedResourceAssignment previousAssignment = null;
      for (TimephasedResourceAssignment assignment : list)
      {
         if (previousAssignment == null)
         {
            assignment.setWorkPerDay(assignment.getTotalWork());
            result.add(assignment);
         }
         else
         {
            Duration previousAssignmentWork = previousAssignment.getWorkPerDay();
            Duration assignmentWork = assignment.getTotalWork();
            if (previousAssignmentWork.getDuration() == assignmentWork.getDuration())
            {
               Date assignmentStart = previousAssignment.getStart();
               Date assignmentFinish = assignment.getFinish();
               double total = previousAssignment.getTotalWork().getDuration();
               total += assignmentWork.getDuration();
               Duration totalWork = Duration.getInstance(total, TimeUnit.MINUTES);

               TimephasedResourceAssignment merged = new TimephasedResourceAssignment();
               merged.setStart(assignmentStart);
               merged.setFinish(assignmentFinish);
               merged.setWorkPerDay(assignmentWork);
               merged.setTotalWork(totalWork);

               result.removeLast();
               assignment = merged;
            }
            else
            {
               assignment.setWorkPerDay(assignment.getTotalWork());
            }
            result.add(assignment);
         }

         previousAssignment = assignment;
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * Converts assignment duration values from minutes to hours.
    * 
    * @param list assignment data
    */
   private void convertToHours(LinkedList<TimephasedResourceAssignment> list)
   {
      for (TimephasedResourceAssignment assignment : list)
      {
         Duration totalWork = assignment.getTotalWork();
         Duration workPerDay = assignment.getWorkPerDay();
         totalWork = Duration.getInstance(totalWork.getDuration() / 60, TimeUnit.HOURS);
         workPerDay = Duration.getInstance(workPerDay.getDuration() / 60, TimeUnit.HOURS);
         assignment.setTotalWork(totalWork);
         assignment.setWorkPerDay(workPerDay);
      }
   }

   /**
    * Retrieves the pro-rata work carried out on a given day.
    * 
    * @param calendar current calendar
    * @param assignment current assignment.
    * @return assignment work duration
    */
   private Duration getAssignmentWork(ProjectCalendar calendar, TimephasedResourceAssignment assignment)
   {
      Date assignmentStart = assignment.getStart();

      Date splitStart = assignmentStart;
      Date splitFinishTime = calendar.getFinishTime(splitStart);
      Date splitFinish = DateUtility.setTime(splitStart, splitFinishTime);

      Duration calendarSplitWork = calendar.getWork(splitStart, splitFinish, TimeUnit.MINUTES);
      Duration assignmentWorkPerDay = assignment.getWorkPerDay();
      Duration splitWork;

      double splitMinutes = assignmentWorkPerDay.getDuration();
      splitMinutes *= calendarSplitWork.getDuration();
      splitMinutes /= (8 * 60); // this appears to be a fixed value
      splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
      return splitWork;
   }

   /*
      private void dumpList(LinkedList<TimephasedResourceAssignment> list)
      {
         System.out.println();
         for (TimephasedResourceAssignment assignment : list)
         {
            System.out.println(assignment);
         }
      }
   */

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[Resource Assignment task=" + m_task.getName() + " resource=" + getResource().getName() + " start=" + m_start + " finish=" + m_finish + " duration=" + m_work + " workContour=" + m_workContour + "]");
   }

   private Number m_units;
   private Duration m_work;
   private Duration m_plannedWork;
   private Duration m_actualWork;
   private Duration m_overtimeWork;
   private Number m_cost;
   private Number m_plannedCost;
   private Number m_actualCost;
   private Date m_start;
   private Date m_finish;
   private Duration m_delay;
   private Integer m_resourceUniqueID;
   private LinkedList<TimephasedResourceAssignment> m_timephasedComplete;
   private LinkedList<TimephasedResourceAssignment> m_timephasedPlanned;
   private boolean m_timephasedCompleteRaw;
   private boolean m_timephasedPlannedRaw;

   /**
    * The following member variables are extended attributes. They are
    * do not form part of the MPX file format definition, and are neither
    * loaded from an MPX file, or saved to an MPX file. Their purpose
    * is to provide storage for attributes which are defined by later versions
    * of Microsoft Project. This allows these attributes to be manipulated
    * when they have been retrieved from file formats other than MPX.
    */
   private Duration m_remainingWork;

   /**
    * Reference to the parent task of this assignment.
    */
   private Task m_task;

   /**
    *  Child record for Workgroup fields.
    */
   private ResourceAssignmentWorkgroupFields m_workgroup;

   /**
    * Work Contour type of this assignment.
    */
   private WorkContour m_workContour;

   /**
    * Default units value: 100%.
    */
   public static final Double DEFAULT_UNITS = Double.valueOf(100);
}
