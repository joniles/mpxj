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

import java.util.Date;
import java.util.List;

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
      return m_timephasedComplete;
   }

   /**
    * Sets the timephased breakdown of the completed work for this
    * resource assignment.
    * 
    * @param timephasedComplete timephased completed work
    */
   public void setTimephasedComplete(List<TimephasedResourceAssignment> timephasedComplete)
   {
      m_timephasedComplete = timephasedComplete;
   }

   /**
    * Retrieves the timephased breakdown of the planned work for this
    * resource assignment. 
    * 
    * @return timephased planned work
    */
   public List<TimephasedResourceAssignment> getTimephasedPlanned()
   {
      return m_timephasedPlanned;
   }

   /**
    * Sets the timephased breakdown of the planned work for this
    * resource assignment.
    * 
    * @param timephasedPlanned timephased planned work
    */
   public void setTimephasedPlanned(List<TimephasedResourceAssignment> timephasedPlanned)
   {
      m_timephasedPlanned = timephasedPlanned;
   }

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
   private List<TimephasedResourceAssignment> m_timephasedComplete;
   private List<TimephasedResourceAssignment> m_timephasedPlanned;

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
