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
    * Constructor.
    *
    * @param file The parent file to which this record belongs.
    */
   public ResourceAssignment(ProjectFile file)
   {
      super(file);
   }

   /**
    * Constructor.
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
      return ((Number) getCachedValue(AssignmentField.ASSIGNMENT_UNITS));
   }

   /**
    * Sets the units for this resource assignment.
    *
    * @param val units
    */
   public void setUnits(Number val)
   {
      set(AssignmentField.ASSIGNMENT_UNITS, val);
   }

   /**
    * Returns the work of this resource assignment.
    *
    * @return work
    */
   public Duration getWork()
   {
      return ((Duration) getCachedValue(AssignmentField.WORK));
   }

   /**
    * Sets the work for this resource assignment.
    *
    * @param dur work
    */
   public void setWork(Duration dur)
   {
      set(AssignmentField.WORK, dur);
   }

   /**
    * Retrieve the baseline start date.
    * 
    * @return baseline start date
    */
   public Date getBaselineStart()
   {
      return ((Date) getCachedValue(AssignmentField.BASELINE_START));
   }

   /**
    * Set the baseline start date.
    * 
    * @param start baseline start date
    */
   public void setBaselineStart(Date start)
   {
      set(AssignmentField.BASELINE_START, start);
   }

   /**
    * Retrieve the actual start date.
    * 
    * @return actual start date
    */
   public Date getActualStart()
   {
      return ((Date) getCachedValue(AssignmentField.ACTUAL_START));
   }

   /**
    * Set the actual start date.
    * 
    * @param start actual start date
    */
   public void setActualStart(Date start)
   {
      set(AssignmentField.ACTUAL_START, start);
   }

   /**
    * Retrieve the baseline finish date.
    * 
    * @return baseline finish date
    */
   public Date getBaselineFinish()
   {
      return ((Date) getCachedValue(AssignmentField.BASELINE_FINISH));
   }

   /**
    * Set the baseline finish date.
    * 
    * @param finish baseline finish
    */
   public void setBaselineFinish(Date finish)
   {
      set(AssignmentField.BASELINE_FINISH, finish);
   }

   /**
    * Retrieve the actual finish date.
    * 
    * @return actual finish date
    */
   public Date getActualFinish()
   {
      return ((Date) getCachedValue(AssignmentField.ACTUAL_FINISH));
   }

   /**
    * Set the actual finish date.
    * 
    * @param finish actual finish
    */
   public void setActualFinish(Date finish)
   {
      set(AssignmentField.ACTUAL_FINISH, finish);
   }

   /**
    * Returns the baseline work of this resource assignment.
    *
    * @return planned work
    */
   public Duration getBaselineWork()
   {
      return ((Duration) getCachedValue(AssignmentField.BASELINE_WORK));
   }

   /**
    * Sets the baseline work for this resource assignment.
    *
    * @param val planned work
    */
   public void setBaselineWork(Duration val)
   {
      set(AssignmentField.BASELINE_WORK, val);
   }

   /**
    * Returns the actual completed work of this resource assignment.
    *
    * @return completed work
    */
   public Duration getActualWork()
   {
      return ((Duration) getCachedValue(AssignmentField.ACTUAL_WORK));
   }

   /**
    * Sets the actual completed work for this resource assignment.
    *
    * @param val actual completed work
    */
   public void setActualWork(Duration val)
   {
      set(AssignmentField.ACTUAL_WORK, val);
   }

   /**
    * Returns the overtime work done of this resource assignment.
    *
    * @return overtime work
    */
   public Duration getOvertimeWork()
   {
      return ((Duration) getCachedValue(AssignmentField.OVERTIME_WORK));
   }

   /**
    * Sets the overtime work for this resource assignment.
    *
    * @param overtimeWork overtime work
    */
   public void setOvertimeWork(Duration overtimeWork)
   {
      set(AssignmentField.OVERTIME_WORK, overtimeWork);
   }

   /**
    * Returns the cost  of this resource assignment.
    *
    * @return cost
    */
   public Number getCost()
   {
      return ((Number) getCachedValue(AssignmentField.COST));
   }

   /**
    * Sets the cost for this resource assignment.
    *
    * @param cost cost
    */
   public void setCost(Number cost)
   {
      set(AssignmentField.COST, cost);
   }

   /**
    * Returns the planned cost for this resource assignment.
    *
    * @return planned cost
    */
   public Number getBaselineCost()
   {
      return ((Number) getCachedValue(AssignmentField.BASELINE_COST));
   }

   /**
    * Sets the planned cost for this resource assignment.
    *
    * @param val planned cost
    */
   public void setBaselineCost(Number val)
   {
      set(AssignmentField.BASELINE_COST, val);
   }

   /**
    * Returns the actual cost for this resource assignment.
    *
    * @return actual cost
    */
   public Number getActualCost()
   {
      return ((Number) getCachedValue(AssignmentField.ACTUAL_COST));
   }

   /**
    * Sets the actual cost so far incurred for this resource assignment.
    *
    * @param actualCost actual cost
    */
   public void setActualCost(Number actualCost)
   {
      set(AssignmentField.ACTUAL_COST, actualCost);
   }

   /**
    * Returns the start of this resource assignment.
    *
    * @return start date
    */
   public Date getStart()
   {
      return ((Date) getCachedValue(AssignmentField.START));
   }

   /**
    * Sets the start date for this resource assignment.
    *
    * @param val start date
    */
   public void setStart(Date val)
   {
      set(AssignmentField.START, val);
   }

   /**
    * Returns the finish date for this resource assignment.
    *
    * @return finish date
    */
   public Date getFinish()
   {
      return ((Date) getCachedValue(AssignmentField.FINISH));
   }

   /**
    * Sets the finish date for this resource assignment.
    *
    * @param val finish date
    */
   public void setFinish(Date val)
   {
      set(AssignmentField.FINISH, val);
   }

   /**
    * Returns the delay for this resource assignment.
    *
    * @return delay
    */
   public Duration getDelay()
   {
      return ((Duration) getCachedValue(AssignmentField.ASSIGNMENT_DELAY));
   }

   /**
    * Sets the delay for this resource assignment.
    *
    * @param dur delay
    */
   public void setDelay(Duration dur)
   {
      set(AssignmentField.ASSIGNMENT_DELAY, dur);
   }

   /**
    * Returns the resources unique id for this resource assignment.
    *
    * @return resources unique id
    */
   public Integer getResourceUniqueID()
   {
      return (Integer) getCachedValue(AssignmentField.RESOURCE_UNIQUE_ID);
   }

   /**
    * Sets the resources unique id for this resource assignment.
    *
    * @param val resources unique id
    */
   public void setResourceUniqueID(Integer val)
   {
      set(AssignmentField.RESOURCE_UNIQUE_ID, val);
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
      if (m_task == null)
      {
         m_task = getParentFile().getTaskByUniqueID(getTaskUniqueID());
      }
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
      return ((WorkContour) getCachedValue(AssignmentField.WORK_CONTOUR));
   }

   /**
    * This method sets the Work Contour type of this Assignment.
    *
    * @param workContour the Work Contour type
    */
   public void setWorkContour(WorkContour workContour)
   {
      set(AssignmentField.WORK_CONTOUR, workContour);
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
      return ((Duration) getCachedValue(AssignmentField.REMAINING_WORK));
   }

   /**
    * Sets the remaining work for this resource assignment.
    *
    * @param remainingWork remaining work
    */
   public void setRemainingWork(Duration remainingWork)
   {
      set(AssignmentField.REMAINING_WORK, remainingWork);
   }

   /**
    * Retrieves the leveling delay for this resource assignment.
    * 
    * @return leveling delay
    */
   public Duration getLevelingDelay()
   {
      return ((Duration) getCachedValue(AssignmentField.LEVELING_DELAY));
   }

   /**
    * Sets the leveling delay for this resource assignment.
    * 
    * @param levelingDelay leveling delay
    */
   public void setLevelingDelay(Duration levelingDelay)
   {
      set(AssignmentField.LEVELING_DELAY, levelingDelay);
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
      return (TimeUnit) getCachedValue(AssignmentField.VARIABLE_RATE_UNITS);
   }

   /**
    * Set the variable rate time units, null if fixed rate.
    * 
    * @param variableRateUnits variable rate units
    */
   public void setVariableRateUnits(TimeUnit variableRateUnits)
   {
      set(AssignmentField.VARIABLE_RATE_UNITS, variableRateUnits);
   }

   /**
    * Set the patrent task unique ID.
    * 
    * @param id task unique ID
    */
   public void setTaskUniqueID(Integer id)
   {
      set(AssignmentField.TASK_UNIQUE_ID, id);
   }

   /**
    * Retrieve the parent task unique ID.
    * 
    * @return task unique ID
    */
   public Integer getTaskUniqueID()
   {
      return (Integer) getCachedValue(AssignmentField.TASK_UNIQUE_ID);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[Resource Assignment task=" + getTask().getName() + " resource=" + (getResource() == null ? "Unassigned" : getResource().getName()) + " start=" + getStart() + " finish=" + getFinish() + " duration=" + getWork() + " workContour=" + getWorkContour() + "]");
   }

   /**
    * {@inheritDoc}
    */
   public void set(FieldType field, Object value)
   {
      if (field != null)
      {
         int index = field.getValue();
         if (m_eventsEnabled)
         {
            fireFieldChangeEvent(field, m_array[index], value);
         }
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
    * Disable events firing when fields are updated.
    */
   public void disableEvents()
   {
      m_eventsEnabled = false;
   }

   /**
    * Enable events firing when fields are updated. This is the default state.
    */
   public void enableEvents()
   {
      m_eventsEnabled = true;
   }

   /**
    * Array of field values.
    */
   private Object[] m_array = new Object[AssignmentField.MAX_VALUE];

   private boolean m_eventsEnabled = true;
   private LinkedList<TimephasedResourceAssignment> m_timephasedComplete;
   private LinkedList<TimephasedResourceAssignment> m_timephasedPlanned;
   private boolean m_timephasedCompleteRaw;
   private boolean m_timephasedPlannedRaw;
   private TimephasedResourceAssignmentNormaliser m_timephasedNormaliser;
   private List<FieldListener> m_listeners;

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

/*
To be added in MPXJ 5.0

   TASK_ID(DataType.INTEGER),
   RESOURCE_ID(DataType.INTEGER),
   TASK_NAME(DataType.STRING),
   RESOURCE_NAME(DataType.STRING),
   REGULAR_WORK(DataType.WORK),
   ACTUAL_OVERTIME_WORK(DataType.WORK),
   REMAINING_OVERTIME_WORK(DataType.WORK),
   PEAK(DataType.UNITS),
   OVERTIME_COST(DataType.CURRENCY),
   REMAINING_COST(DataType.CURRENCY),
   ACTUAL_OVERTIME_COST(DataType.CURRENCY),
   REMAINING_OVERTIME_COST(DataType.CURRENCY),
   BCWS(DataType.CURRENCY),
   BCWP(DataType.CURRENCY),
   ACWP(DataType.CURRENCY),
   SV(DataType.CURRENCY),
   COST_VARIANCE(DataType.CURRENCY),
   PERCENT_WORK_COMPLETE(DataType.PERCENTAGE),
   PROJECT(DataType.STRING),
   NOTES(DataType.STRING),
   CONFIRMED(DataType.BOOLEAN),
   RESPONSE_PENDING(DataType.BOOLEAN),
   UPDATE_NEEDED(DataType.BOOLEAN),
   TEAM_STATUS_PENDING(DataType.BOOLEAN),
   COST_RATE_TABLE(DataType.STRING),
   TEXT1(DataType.STRING),
   TEXT2(DataType.STRING),
   TEXT3(DataType.STRING),
   TEXT4(DataType.STRING),
   TEXT5(DataType.STRING),
   TEXT6(DataType.STRING),
   TEXT7(DataType.STRING),
   TEXT8(DataType.STRING),
   TEXT9(DataType.STRING),
   TEXT10(DataType.STRING),
   START1(DataType.DATE),
   START2(DataType.DATE),
   START3(DataType.DATE),
   START4(DataType.DATE),
   START5(DataType.DATE),
   FINISH1(DataType.DATE),
   FINISH2(DataType.DATE),
   FINISH3(DataType.DATE),
   FINISH4(DataType.DATE),
   FINISH5(DataType.DATE),
   NUMBER1(DataType.NUMERIC),
   NUMBER2(DataType.NUMERIC),
   NUMBER3(DataType.NUMERIC),
   NUMBER4(DataType.NUMERIC),
   NUMBER5(DataType.NUMERIC),
   DURATION1(DataType.DURATION),
   DURATION2(DataType.DURATION),
   DURATION3(DataType.DURATION),
   COST1(DataType.CURRENCY),
   COST2(DataType.CURRENCY),
   COST3(DataType.CURRENCY),
   FLAG10(DataType.BOOLEAN),
   FLAG1(DataType.BOOLEAN),
   FLAG2(DataType.BOOLEAN),
   FLAG3(DataType.BOOLEAN),
   FLAG4(DataType.BOOLEAN),
   FLAG5(DataType.BOOLEAN),
   FLAG6(DataType.BOOLEAN),
   FLAG7(DataType.BOOLEAN),
   FLAG8(DataType.BOOLEAN),
   FLAG9(DataType.BOOLEAN),
   LINKED_FIELDS(DataType.BOOLEAN),
   OVERALLOCATED(DataType.BOOLEAN),
   TASK_SUMMARY_NAME(DataType.STRING),
   HYPERLINK(DataType.STRING),
   HYPERLINK_ADDRESS(DataType.STRING),
   HYPERLINK_SUBADDRESS(DataType.STRING),
   HYPERLINK_HREF(DataType.STRING),
   COST4(DataType.CURRENCY),
   COST5(DataType.CURRENCY),
   COST6(DataType.CURRENCY),
   COST7(DataType.CURRENCY),
   COST8(DataType.CURRENCY),
   COST9(DataType.CURRENCY),
   COST10(DataType.CURRENCY),
   DATE1(DataType.DATE),
   DATE2(DataType.DATE),
   DATE3(DataType.DATE),
   DATE4(DataType.DATE),
   DATE5(DataType.DATE),
   DATE6(DataType.DATE),
   DATE7(DataType.DATE),
   DATE8(DataType.DATE),
   DATE9(DataType.DATE),
   DATE10(DataType.DATE),
   DURATION4(DataType.DURATION),
   DURATION5(DataType.DURATION),
   DURATION6(DataType.DURATION),
   DURATION7(DataType.DURATION),
   DURATION8(DataType.DURATION),
   DURATION9(DataType.DURATION),
   DURATION10(DataType.DURATION),
   FINISH6(DataType.DATE),
   FINISH7(DataType.DATE),
   FINISH8(DataType.DATE),
   FINISH9(DataType.DATE),
   FINISH10(DataType.DATE),
   FLAG11(DataType.BOOLEAN),
   FLAG12(DataType.BOOLEAN),
   FLAG13(DataType.BOOLEAN),
   FLAG14(DataType.BOOLEAN),
   FLAG15(DataType.BOOLEAN),
   FLAG16(DataType.BOOLEAN),
   FLAG17(DataType.BOOLEAN),
   FLAG18(DataType.BOOLEAN),
   FLAG19(DataType.BOOLEAN),
   FLAG20(DataType.BOOLEAN),
   NUMBER6(DataType.NUMERIC),
   NUMBER7(DataType.NUMERIC),
   NUMBER8(DataType.NUMERIC),
   NUMBER9(DataType.NUMERIC),
   NUMBER10(DataType.NUMERIC),
   NUMBER11(DataType.NUMERIC),
   NUMBER12(DataType.NUMERIC),
   NUMBER13(DataType.NUMERIC),
   NUMBER14(DataType.NUMERIC),
   NUMBER15(DataType.NUMERIC),
   NUMBER16(DataType.NUMERIC),
   NUMBER17(DataType.NUMERIC),
   NUMBER18(DataType.NUMERIC),
   NUMBER19(DataType.NUMERIC),
   NUMBER20(DataType.NUMERIC),
   START6(DataType.DATE),
   START7(DataType.DATE),
   START8(DataType.DATE),
   START9(DataType.DATE),
   START10(DataType.DATE),
   TEXT11(DataType.STRING),
   TEXT12(DataType.STRING),
   TEXT13(DataType.STRING),
   TEXT14(DataType.STRING),
   TEXT15(DataType.STRING),
   TEXT16(DataType.STRING),
   TEXT17(DataType.STRING),
   TEXT18(DataType.STRING),
   TEXT19(DataType.STRING),
   TEXT20(DataType.STRING),
   TEXT21(DataType.STRING),
   TEXT22(DataType.STRING),
   TEXT23(DataType.STRING),
   TEXT24(DataType.STRING),
   TEXT25(DataType.STRING),
   TEXT26(DataType.STRING),
   TEXT27(DataType.STRING),
   TEXT28(DataType.STRING),
   TEXT29(DataType.STRING),
   TEXT30(DataType.STRING),
   INDEX(DataType.INTEGER),
   CV(DataType.CURRENCY),
   WORK_VARIANCE(DataType.WORK),
   START_VARIANCE(DataType.DURATION),
   FINISH_VARIANCE(DataType.DURATION),
   VAC(DataType.CURRENCY),
   FIXED_MATERIAL_ASSIGNMENT(DataType.STRING),
   RESOURCE_TYPE(DataType.RESOURCE_TYPE),
   HYPERLINK_SCREEN_TIP(DataType.STRING),
   WBS(DataType.STRING),
   BASELINE1_WORK(DataType.WORK),
   BASELINE1_COST(DataType.CURRENCY),
   BASELINE1_START(DataType.DATE),
   BASELINE1_FINISH(DataType.DATE),
   BASELINE2_WORK(DataType.WORK),
   BASELINE2_COST(DataType.CURRENCY),
   BASELINE2_START(DataType.DATE),
   BASELINE2_FINISH(DataType.DATE),
   BASELINE3_WORK(DataType.WORK),
   BASELINE3_COST(DataType.CURRENCY),
   BASELINE3_START(DataType.DATE),
   BASELINE3_FINISH(DataType.DATE),
   BASELINE4_WORK(DataType.WORK),
   BASELINE4_COST(DataType.CURRENCY),
   BASELINE4_START(DataType.DATE),
   BASELINE4_FINISH(DataType.DATE),
   BASELINE5_WORK(DataType.WORK),
   BASELINE5_COST(DataType.CURRENCY),
   BASELINE5_START(DataType.DATE),
   BASELINE5_FINISH(DataType.DATE),
   BASELINE6_WORK(DataType.WORK),
   BASELINE6_COST(DataType.CURRENCY),
   BASELINE6_START(DataType.DATE),
   BASELINE6_FINISH(DataType.DATE),
   BASELINE7_WORK(DataType.WORK),
   BASELINE7_COST(DataType.CURRENCY),
   BASELINE7_START(DataType.DATE),
   BASELINE7_FINISH(DataType.DATE),
   BASELINE8_WORK(DataType.WORK),
   BASELINE8_COST(DataType.CURRENCY),
   BASELINE8_START(DataType.DATE),
   BASELINE8_FINISH(DataType.DATE),
   BASELINE9_WORK(DataType.WORK),
   BASELINE9_COST(DataType.CURRENCY),
   BASELINE9_START(DataType.DATE),
   BASELINE9_FINISH(DataType.DATE),
   BASELINE10_WORK(DataType.WORK),
   BASELINE10_COST(DataType.CURRENCY),
   BASELINE10_START(DataType.DATE),
   BASELINE10_FINISH(DataType.DATE),
   TASK_OUTLINE_NUMBER(DataType.STRING),
   ENTERPRISE_COST1(DataType.CURRENCY),
   ENTERPRISE_COST2(DataType.CURRENCY),
   ENTERPRISE_COST3(DataType.CURRENCY),
   ENTERPRISE_COST4(DataType.CURRENCY),
   ENTERPRISE_COST5(DataType.CURRENCY),
   ENTERPRISE_COST6(DataType.CURRENCY),
   ENTERPRISE_COST7(DataType.CURRENCY),
   ENTERPRISE_COST8(DataType.CURRENCY),
   ENTERPRISE_COST9(DataType.CURRENCY),
   ENTERPRISE_COST10(DataType.CURRENCY),
   ENTERPRISE_DATE1(DataType.DATE),
   ENTERPRISE_DATE2(DataType.DATE),
   ENTERPRISE_DATE3(DataType.DATE),
   ENTERPRISE_DATE4(DataType.DATE),
   ENTERPRISE_DATE5(DataType.DATE),
   ENTERPRISE_DATE6(DataType.DATE),
   ENTERPRISE_DATE7(DataType.DATE),
   ENTERPRISE_DATE8(DataType.DATE),
   ENTERPRISE_DATE9(DataType.DATE),
   ENTERPRISE_DATE10(DataType.DATE),
   ENTERPRISE_DATE11(DataType.DATE),
   ENTERPRISE_DATE12(DataType.DATE),
   ENTERPRISE_DATE13(DataType.DATE),
   ENTERPRISE_DATE14(DataType.DATE),
   ENTERPRISE_DATE15(DataType.DATE),
   ENTERPRISE_DATE16(DataType.DATE),
   ENTERPRISE_DATE17(DataType.DATE),
   ENTERPRISE_DATE18(DataType.DATE),
   ENTERPRISE_DATE19(DataType.DATE),
   ENTERPRISE_DATE20(DataType.DATE),
   ENTERPRISE_DATE21(DataType.DATE),
   ENTERPRISE_DATE22(DataType.DATE),
   ENTERPRISE_DATE23(DataType.DATE),
   ENTERPRISE_DATE24(DataType.DATE),
   ENTERPRISE_DATE25(DataType.DATE),
   ENTERPRISE_DATE26(DataType.DATE),
   ENTERPRISE_DATE27(DataType.DATE),
   ENTERPRISE_DATE28(DataType.DATE),
   ENTERPRISE_DATE29(DataType.DATE),
   ENTERPRISE_DATE30(DataType.DATE),
   ENTERPRISE_DURATION1(DataType.DURATION),
   ENTERPRISE_DURATION2(DataType.DURATION),
   ENTERPRISE_DURATION3(DataType.DURATION),
   ENTERPRISE_DURATION4(DataType.DURATION),
   ENTERPRISE_DURATION5(DataType.DURATION),
   ENTERPRISE_DURATION6(DataType.DURATION),
   ENTERPRISE_DURATION7(DataType.DURATION),
   ENTERPRISE_DURATION8(DataType.DURATION),
   ENTERPRISE_DURATION9(DataType.DURATION),
   ENTERPRISE_DURATION10(DataType.DURATION),
   ENTERPRISE_FLAG1(DataType.BOOLEAN),
   ENTERPRISE_FLAG2(DataType.BOOLEAN),
   ENTERPRISE_FLAG3(DataType.BOOLEAN),
   ENTERPRISE_FLAG4(DataType.BOOLEAN),
   ENTERPRISE_FLAG5(DataType.BOOLEAN),
   ENTERPRISE_FLAG6(DataType.BOOLEAN),
   ENTERPRISE_FLAG7(DataType.BOOLEAN),
   ENTERPRISE_FLAG8(DataType.BOOLEAN),
   ENTERPRISE_FLAG9(DataType.BOOLEAN),
   ENTERPRISE_FLAG10(DataType.BOOLEAN),
   ENTERPRISE_FLAG11(DataType.BOOLEAN),
   ENTERPRISE_FLAG12(DataType.BOOLEAN),
   ENTERPRISE_FLAG13(DataType.BOOLEAN),
   ENTERPRISE_FLAG14(DataType.BOOLEAN),
   ENTERPRISE_FLAG15(DataType.BOOLEAN),
   ENTERPRISE_FLAG16(DataType.BOOLEAN),
   ENTERPRISE_FLAG17(DataType.BOOLEAN),
   ENTERPRISE_FLAG18(DataType.BOOLEAN),
   ENTERPRISE_FLAG19(DataType.BOOLEAN),
   ENTERPRISE_FLAG20(DataType.BOOLEAN),
   ENTERPRISE_NUMBER1(DataType.NUMERIC),
   ENTERPRISE_NUMBER2(DataType.NUMERIC),
   ENTERPRISE_NUMBER3(DataType.NUMERIC),
   ENTERPRISE_NUMBER4(DataType.NUMERIC),
   ENTERPRISE_NUMBER5(DataType.NUMERIC),
   ENTERPRISE_NUMBER6(DataType.NUMERIC),
   ENTERPRISE_NUMBER7(DataType.NUMERIC),
   ENTERPRISE_NUMBER8(DataType.NUMERIC),
   ENTERPRISE_NUMBER9(DataType.NUMERIC),
   ENTERPRISE_NUMBER10(DataType.NUMERIC),
   ENTERPRISE_NUMBER11(DataType.NUMERIC),
   ENTERPRISE_NUMBER12(DataType.NUMERIC),
   ENTERPRISE_NUMBER13(DataType.NUMERIC),
   ENTERPRISE_NUMBER14(DataType.NUMERIC),
   ENTERPRISE_NUMBER15(DataType.NUMERIC),
   ENTERPRISE_NUMBER16(DataType.NUMERIC),
   ENTERPRISE_NUMBER17(DataType.NUMERIC),
   ENTERPRISE_NUMBER18(DataType.NUMERIC),
   ENTERPRISE_NUMBER19(DataType.NUMERIC),
   ENTERPRISE_NUMBER20(DataType.NUMERIC),
   ENTERPRISE_NUMBER21(DataType.NUMERIC),
   ENTERPRISE_NUMBER22(DataType.NUMERIC),
   ENTERPRISE_NUMBER23(DataType.NUMERIC),
   ENTERPRISE_NUMBER24(DataType.NUMERIC),
   ENTERPRISE_NUMBER25(DataType.NUMERIC),
   ENTERPRISE_NUMBER26(DataType.NUMERIC),
   ENTERPRISE_NUMBER27(DataType.NUMERIC),
   ENTERPRISE_NUMBER28(DataType.NUMERIC),
   ENTERPRISE_NUMBER29(DataType.NUMERIC),
   ENTERPRISE_NUMBER30(DataType.NUMERIC),
   ENTERPRISE_NUMBER31(DataType.NUMERIC),
   ENTERPRISE_NUMBER32(DataType.NUMERIC),
   ENTERPRISE_NUMBER33(DataType.NUMERIC),
   ENTERPRISE_NUMBER34(DataType.NUMERIC),
   ENTERPRISE_NUMBER35(DataType.NUMERIC),
   ENTERPRISE_NUMBER36(DataType.NUMERIC),
   ENTERPRISE_NUMBER37(DataType.NUMERIC),
   ENTERPRISE_NUMBER38(DataType.NUMERIC),
   ENTERPRISE_NUMBER39(DataType.NUMERIC),
   ENTERPRISE_NUMBER40(DataType.NUMERIC),
   ENTERPRISE_TEXT1(DataType.STRING),
   ENTERPRISE_TEXT2(DataType.STRING),
   ENTERPRISE_TEXT3(DataType.STRING),
   ENTERPRISE_TEXT4(DataType.STRING),
   ENTERPRISE_TEXT5(DataType.STRING),
   ENTERPRISE_TEXT6(DataType.STRING),
   ENTERPRISE_TEXT7(DataType.STRING),
   ENTERPRISE_TEXT8(DataType.STRING),
   ENTERPRISE_TEXT9(DataType.STRING),
   ENTERPRISE_TEXT10(DataType.STRING),
   ENTERPRISE_TEXT11(DataType.STRING),
   ENTERPRISE_TEXT12(DataType.STRING),
   ENTERPRISE_TEXT13(DataType.STRING),
   ENTERPRISE_TEXT14(DataType.STRING),
   ENTERPRISE_TEXT15(DataType.STRING),
   ENTERPRISE_TEXT16(DataType.STRING),
   ENTERPRISE_TEXT17(DataType.STRING),
   ENTERPRISE_TEXT18(DataType.STRING),
   ENTERPRISE_TEXT19(DataType.STRING),
   ENTERPRISE_TEXT20(DataType.STRING),
   ENTERPRISE_TEXT21(DataType.STRING),
   ENTERPRISE_TEXT22(DataType.STRING),
   ENTERPRISE_TEXT23(DataType.STRING),
   ENTERPRISE_TEXT24(DataType.STRING),
   ENTERPRISE_TEXT25(DataType.STRING),
   ENTERPRISE_TEXT26(DataType.STRING),
   ENTERPRISE_TEXT27(DataType.STRING),
   ENTERPRISE_TEXT28(DataType.STRING),
   ENTERPRISE_TEXT29(DataType.STRING),
   ENTERPRISE_TEXT30(DataType.STRING),
   ENTERPRISE_TEXT31(DataType.STRING),
   ENTERPRISE_TEXT32(DataType.STRING),
   ENTERPRISE_TEXT33(DataType.STRING),
   ENTERPRISE_TEXT34(DataType.STRING),
   ENTERPRISE_TEXT35(DataType.STRING),
   ENTERPRISE_TEXT36(DataType.STRING),
   ENTERPRISE_TEXT37(DataType.STRING),
   ENTERPRISE_TEXT38(DataType.STRING),
   ENTERPRISE_TEXT39(DataType.STRING),
   ENTERPRISE_TEXT40(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE1(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE2(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE3(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE4(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE5(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE6(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE7(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE8(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE9(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE10(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE11(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE12(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE13(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE14(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE15(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE16(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE17(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE18(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE19(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE20(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE21(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE22(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE23(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE24(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE25(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE26(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE27(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE28(DataType.STRING),
   ENTERPRISE_RESOURCE_OUTLINE_CODE29(DataType.STRING),
   ENTERPRISE_RESOURCE_RBS(DataType.STRING),
   RESOURCE_REQUEST_TYPE(DataType.STRING),
   ENTERPRISE_TEAM_MEMBER(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE20(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE21(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE22(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE23(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE24(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE25(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE26(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE27(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE28(DataType.STRING),
   ENTERPRISE_RESOURCE_MULTI_VALUE29(DataType.STRING),
   ACTUAL_WORK_PROTECTED(DataType.WORK),
   ACTUAL_OVERTIME_WORK_PROTECTED(DataType.WORK),
   CREATED(DataType.DATE),
   ASSIGNMENT_GUID(DataType.BINARY),
   ASSIGNMENT_TASK_GUID(DataType.BINARY),
   ASSIGNMENT_RESOURCE_GUID(DataType.BINARY),
   SUMMARY(DataType.STRING),
   OWNER(DataType.STRING),
   BUDGET_WORK(DataType.WORK),
   BUDGET_COST(DataType.CURRENCY),
   BASELINE_BUDGET_WORK(DataType.WORK),
   BASELINE_BUDGET_COST(DataType.CURRENCY),
   BASELINE1_BUDGET_WORK(DataType.WORK),
   BASELINE1_BUDGET_COST(DataType.CURRENCY),
   BASELINE2_BUDGET_WORK(DataType.WORK),
   BASELINE2_BUDGET_COST(DataType.CURRENCY),
   BASELINE3_BUDGET_WORK(DataType.WORK),
   BASELINE3_BUDGET_COST(DataType.CURRENCY),
   BASELINE4_BUDGET_WORK(DataType.WORK),
   BASELINE4_BUDGET_COST(DataType.CURRENCY),
   BASELINE5_BUDGET_WORK(DataType.WORK),
   BASELINE5_BUDGET_COST(DataType.CURRENCY),
   BASELINE6_BUDGET_WORK(DataType.WORK),
   BASELINE6_BUDGET_COST(DataType.CURRENCY),
   BASELINE7_BUDGET_WORK(DataType.WORK),
   BASELINE7_BUDGET_COST(DataType.CURRENCY),
   BASELINE8_BUDGET_WORK(DataType.WORK),
   BASELINE8_BUDGET_COST(DataType.CURRENCY),
   BASELINE9_BUDGET_WORK(DataType.WORK),
   BASELINE9_BUDGET_COST(DataType.CURRENCY),
   BASELINE10_BUDGET_WORK(DataType.WORK),
   BASELINE10_BUDGET_COST(DataType.CURRENCY),

*/