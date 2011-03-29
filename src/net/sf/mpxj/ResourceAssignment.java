/*
 * file:       ResourceAssignment.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.listener.FieldListener;

/**
 * This class represents a resource assignment record from an MPX file.
 */
public final class ResourceAssignment extends ProjectEntity implements FieldContainer
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
      return ((Number) getCachedValue(ResourceField.ASSIGNMENT_UNITS));
   }

   /**
    * Sets the units for this resource assignment.
    *
    * @param val units
    */
   public void setUnits(Number val)
   {
      set(ResourceField.ASSIGNMENT_UNITS, val);
   }

   /**
    * Returns the work of this resource assignment.
    *
    * @return work
    */
   public Duration getWork()
   {
      return ((Duration) getCachedValue(ResourceField.WORK));
   }

   /**
    * Sets the work for this resource assignment.
    *
    * @param dur work
    */
   public void setWork(Duration dur)
   {
      set(ResourceField.WORK, dur);
   }

   /**
    * Retrieve the baseline start date.
    * 
    * @return baseline start date
    */
   public Date getBaselineStart()
   {
      return ((Date) getCachedValue(ResourceField.BASELINE_START));
   }

   /**
    * Set the baseline start date.
    * 
    * @param start baseline start date
    */
   public void setBaselineStart(Date start)
   {
      set(ResourceField.BASELINE_START, start);
   }

   /**
    * Retrieve the actual start date.
    * 
    * @return actual start date
    */
   public Date getActualStart()
   {
      return ((Date) getCachedValue(ResourceField.ACTUAL_START));
   }

   /**
    * Set the actual start date.
    * 
    * @param start actual start date
    */
   public void setActualStart(Date start)
   {
      set(ResourceField.ACTUAL_START, start);
   }

   /**
    * Retrieve the baseline finish date.
    * 
    * @return baseline finish date
    */
   public Date getBaselineFinish()
   {
      return ((Date) getCachedValue(ResourceField.BASELINE_FINISH));
   }

   /**
    * Set the baseline finish date.
    * 
    * @param finish baseline finish
    */
   public void setBaselineFinish(Date finish)
   {
      set(ResourceField.BASELINE_FINISH, finish);
   }

   /**
    * Retrieve the actual finish date.
    * 
    * @return actual finish date
    */
   public Date getActualFinish()
   {
      return ((Date) getCachedValue(ResourceField.ACTUAL_FINISH));
   }

   /**
    * Set the actual finish date.
    * 
    * @param finish actual finish
    */
   public void setActualFinish(Date finish)
   {
      set(ResourceField.ACTUAL_FINISH, finish);
   }

   /**
    * Returns the baseline work of this resource assignment.
    *
    * @return planned work
    */
   public Duration getBaselineWork()
   {
      return ((Duration) getCachedValue(ResourceField.BASELINE_WORK));
   }

   /**
    * Sets the baseline work for this resource assignment.
    *
    * @param val planned work
    */
   public void setBaselineWork(Duration val)
   {
      set(ResourceField.BASELINE_WORK, val);
   }

   /**
    * Returns the actual completed work of this resource assignment.
    *
    * @return completed work
    */
   public Duration getActualWork()
   {
      return ((Duration) getCachedValue(ResourceField.ACTUAL_WORK));
   }

   /**
    * Sets the actual completed work for this resource assignment.
    *
    * @param val actual completed work
    */
   public void setActualWork(Duration val)
   {
      set(ResourceField.ACTUAL_WORK, val);
   }

   /**
    * Returns the overtime work done of this resource assignment.
    *
    * @return overtime work
    */
   public Duration getOvertimeWork()
   {
      return ((Duration) getCachedValue(ResourceField.OVERTIME_WORK));
   }

   /**
    * Sets the overtime work for this resource assignment.
    *
    * @param overtimeWork overtime work
    */
   public void setOvertimeWork(Duration overtimeWork)
   {
      set(ResourceField.OVERTIME_WORK, overtimeWork);
   }

   /**
    * Returns the cost  of this resource assignment.
    *
    * @return cost
    */
   public Number getCost()
   {
      return ((Number) getCachedValue(ResourceField.COST));
   }

   /**
    * Sets the cost for this resource assignment.
    *
    * @param cost cost
    */
   public void setCost(Number cost)
   {
      set(ResourceField.COST, cost);
   }

   /**
    * Returns the planned cost for this resource assignment.
    *
    * @return planned cost
    */
   public Number getBaselineCost()
   {
      return ((Number) getCachedValue(ResourceField.BASELINE_COST));
   }

   /**
    * Sets the planned cost for this resource assignment.
    *
    * @param val planned cost
    */
   public void setBaselineCost(Number val)
   {
      set(ResourceField.BASELINE_COST, val);
   }

   /**
    * Returns the actual cost for this resource assignment.
    *
    * @return actual cost
    */
   public Number getActualCost()
   {
      return ((Number) getCachedValue(ResourceField.ACTUAL_COST));
   }

   /**
    * Sets the actual cost so far incurred for this resource assignment.
    *
    * @param actualCost actual cost
    */
   public void setActualCost(Number actualCost)
   {
      set(ResourceField.ACTUAL_COST, actualCost);
   }

   /**
    * Returns the start of this resource assignment.
    *
    * @return start date
    */
   public Date getStart()
   {
      return ((Date) getCachedValue(ResourceField.START));
   }

   /**
    * Sets the start date for this resource assignment.
    *
    * @param val start date
    */
   public void setStart(Date val)
   {
      set(ResourceField.START, val);
   }

   /**
    * Returns the finish date for this resource assignment.
    *
    * @return finish date
    */
   public Date getFinish()
   {
      return ((Date) getCachedValue(ResourceField.FINISH));
   }

   /**
    * Sets the finish date for this resource assignment.
    *
    * @param val finish date
    */
   public void setFinish(Date val)
   {
      set(ResourceField.FINISH, val);
   }

   /**
    * Returns the delay for this resource assignment.
    *
    * @return delay
    */
   public Duration getDelay()
   {
      return ((Duration) getCachedValue(ResourceField.ASSIGNMENT_DELAY));
   }

   /**
    * Sets the delay for this resource assignment.
    *
    * @param dur delay
    */
   public void setDelay(Duration dur)
   {
      set(ResourceField.ASSIGNMENT_DELAY, dur);
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
      return ((WorkContour) getCachedValue(ResourceField.WORK_CONTOUR));
   }

   /**
    * This method sets the Work Contour type of this Assignment.
    *
    * @param workContour the Work Contour type
    */
   public void setWorkContour(WorkContour workContour)
   {
      set(ResourceField.WORK_CONTOUR, workContour);
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
      return ((Duration) getCachedValue(ResourceField.REMAINING_WORK));
   }

   /**
    * Sets the remaining work for this resource assignment.
    *
    * @param remainingWork remaining work
    */
   public void setRemainingWork(Duration remainingWork)
   {
      set(ResourceField.REMAINING_WORK, remainingWork);
   }

   /**
    * Retrieves the leveling delay for this resource assignment.
    * 
    * @return leveling delay
    */
   public Duration getLevelingDelay()
   {
      return ((Duration) getCachedValue(ResourceField.LEVELING_DELAY));
   }

   /**
    * Sets the leveling delay for this resource assignment.
    * 
    * @param levelingDelay leveling delay
    */
   public void setLevelingDelay(Duration levelingDelay)
   {
      set(ResourceField.LEVELING_DELAY, levelingDelay);
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
         m_timephasedNormaliser.normalise(getCalendar(), m_timephasedComplete);
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
      if (timephasedComplete instanceof LinkedList<?>)
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
         m_timephasedNormaliser.normalise(getCalendar(), m_timephasedPlanned);
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
      if (timephasedPlanned instanceof LinkedList<?>)
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
    * Set the class instance used to normalise timephased data.
    * 
    * @param normaliser class instance used to normalise timephased data
    */
   public void setTimephasedNormaliser(TimephasedResourceAssignmentNormaliser normaliser)
   {
      m_timephasedNormaliser = normaliser;
   }

   /**
    * Retrieve a flag indicating if this resource assignment has timephased 
    * data associated with it.
    * 
    * @return true if this resource assignment has timephased data
    */
   public boolean getHasTimephasedData()
   {
      return (m_timephasedPlanned != null && !m_timephasedPlanned.isEmpty()) || (m_timephasedComplete != null && !m_timephasedComplete.isEmpty());
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

      Task task = getTask();
      if (calendar == null || task.getIgnoreResourceCalendar())
      {
         calendar = task.getCalendar();
      }

      if (calendar == null)
      {
         calendar = getParentFile().getCalendar();
      }

      return calendar;
   }

   /**
    * Retrieve the variable rate time units, null if fixed rate.
    * 
    * @return variable rate time units
    */
   public TimeUnit getVariableRateUnits()
   {
      return m_variableRateUnits;
   }

   /**
    * Set the variable rate time units, null if fixed rate.
    * 
    * @param variableRateUnits variable rate units
    */
   public void setVariableRateUnits(TimeUnit variableRateUnits)
   {
      m_variableRateUnits = variableRateUnits;
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[Resource Assignment task=" + m_task.getName() + " resource=" + (getResource() == null ? "Unassigned" : getResource().getName()) + " start=" + getStart() + " finish=" + getFinish() + " duration=" + getWork() + " workContour=" + getWorkContour() + "]");
   }

   /**
    * {@inheritDoc}
    */
   public void set(FieldType field, Object value)
   {
      if (field != null)
      {
         int index = field.getValue();
         fireFieldChangeEvent(field, m_array[index], value);
         m_array[index] = value;
      }
   }

   /**
    * Handle the change in a field value. Reset any cached calculated
    * values affected by this change, pass on the event to any external
    * listeners.
    * 
    * @param field field changed
    * @param oldValue old field value
    * @param newValue new field value
    */
   private void fireFieldChangeEvent(FieldType field, Object oldValue, Object newValue)
   {
      //
      // External event handling
      //
      if (m_listeners != null)
      {
         for (FieldListener listener : m_listeners)
         {
            listener.fieldChange(this, field, oldValue, newValue);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void addFieldListener(FieldListener listener)
   {
      if (m_listeners == null)
      {
         m_listeners = new LinkedList<FieldListener>();
      }
      m_listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   public void removeFieldListener(FieldListener listener)
   {
      if (m_listeners != null)
      {
         m_listeners.remove(listener);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Object getCachedValue(FieldType field)
   {
      return (field == null ? null : m_array[field.getValue()]);
   }

   /**
    * {@inheritDoc}
    */
   public Object getCurrentValue(FieldType field)
   {
      Object result = null;

      if (field != null)
      {
         int fieldValue = field.getValue();

         result = m_array[fieldValue];
      }

      return (result);
   }

   /**
    * Array of field values.
    */
   private Object[] m_array = new Object[ResourceField.MAX_VALUE];

   private Integer m_resourceUniqueID;
   private LinkedList<TimephasedResourceAssignment> m_timephasedComplete;
   private LinkedList<TimephasedResourceAssignment> m_timephasedPlanned;
   private boolean m_timephasedCompleteRaw;
   private boolean m_timephasedPlannedRaw;
   private TimephasedResourceAssignmentNormaliser m_timephasedNormaliser;
   private List<FieldListener> m_listeners;
   private TimeUnit m_variableRateUnits;

   /**
    * Reference to the parent task of this assignment.
    */
   private Task m_task;

   /**
    *  Child record for Workgroup fields.
    */
   private ResourceAssignmentWorkgroupFields m_workgroup;

   /**
    * Default units value: 100%.
    */
   public static final Double DEFAULT_UNITS = Double.valueOf(100);
}
