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

package org.mpxj;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.mpxj.common.AssignmentFieldLists;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.CombinedCalendar;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.RateHelper;

/**
 * This class represents a resource assignment record from an MPX file.
 */
public class ResourceAssignment extends AbstractFieldContainer<ResourceAssignment> implements ProjectEntityWithMutableUniqueID, TimePeriodEntity
{
   /**
    * Constructor.
    *
    * @param file The parent file to which this record belongs.
    */
   ResourceAssignment(ProjectFile file)
   {
      m_parentFile = file;
      if (file.getProjectConfig().getAutoAssignmentUniqueID())
      {
         setUniqueID(file.getUniqueIdObjectSequence(ResourceAssignment.class).getNext());
      }
   }

   /**
    * Retrieve the parent project.
    *
    * @return parent project
    */
   public ProjectFile getParentFile()
   {
      return m_parentFile;
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
    * Retrieve the unique ID of this resource assignment.
    *
    * @return resource assignment unique ID
    */
   @Override public Integer getUniqueID()
   {
      return (Integer) get(AssignmentField.UNIQUE_ID);
   }

   /**
    * Set the unique ID of this resource assignment.
    *
    * @param uniqueID resource assignment unique ID
    */
   @Override public void setUniqueID(Integer uniqueID)
   {
      set(AssignmentField.UNIQUE_ID, uniqueID);
   }

   /**
    * Returns the units of this resource assignment.
    *
    * @return units
    */
   public Number getUnits()
   {
      return (Number) get(AssignmentField.ASSIGNMENT_UNITS);
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
    * Returns the remaining units of this resource assignment.
    *
    * @return remaining units
    */
   public Number getRemainingUnits()
   {
      return (Number) get(AssignmentField.REMAINING_ASSIGNMENT_UNITS);
   }

   /**
    * Sets the remaining units for this resource assignment.
    *
    * @param val remaining units
    */
   public void setRemainingUnits(Number val)
   {
      set(AssignmentField.REMAINING_ASSIGNMENT_UNITS, val);
   }

   /**
    * Returns the work of this resource assignment.
    *
    * @return work
    */
   public Duration getWork()
   {
      return (Duration) get(AssignmentField.WORK);
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
   public LocalDateTime getBaselineStart()
   {
      return (LocalDateTime) get(AssignmentField.BASELINE_START);
   }

   /**
    * Set the baseline start date.
    *
    * @param start baseline start date
    */
   public void setBaselineStart(LocalDateTime start)
   {
      set(AssignmentField.BASELINE_START, start);
   }

   /**
    * Retrieve the actual start date.
    *
    * @return actual start date
    */
   public LocalDateTime getActualStart()
   {
      return (LocalDateTime) get(AssignmentField.ACTUAL_START);
   }

   /**
    * Set the actual start date.
    *
    * @param start actual start date
    */
   public void setActualStart(LocalDateTime start)
   {
      set(AssignmentField.ACTUAL_START, start);
   }

   /**
    * Retrieve the baseline finish date.
    *
    * @return baseline finish date
    */
   public LocalDateTime getBaselineFinish()
   {
      return (LocalDateTime) get(AssignmentField.BASELINE_FINISH);
   }

   /**
    * Set the baseline finish date.
    *
    * @param finish baseline finish
    */
   public void setBaselineFinish(LocalDateTime finish)
   {
      set(AssignmentField.BASELINE_FINISH, finish);
   }

   /**
    * Retrieve the actual finish date.
    *
    * @return actual finish date
    */
   public LocalDateTime getActualFinish()
   {
      return (LocalDateTime) get(AssignmentField.ACTUAL_FINISH);
   }

   /**
    * Set the actual finish date.
    *
    * @param finish actual finish
    */
   public void setActualFinish(LocalDateTime finish)
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
      return (Duration) get(AssignmentField.BASELINE_WORK);
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
      return (Duration) get(AssignmentField.ACTUAL_WORK);
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
      return (Duration) get(AssignmentField.OVERTIME_WORK);
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
      return (Number) get(AssignmentField.COST);
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
      return (Number) get(AssignmentField.BASELINE_COST);
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
      return (Number) get(AssignmentField.ACTUAL_COST);
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
   @Override public LocalDateTime getStart()
   {
      return (LocalDateTime) get(AssignmentField.START);
   }

   /**
    * Sets the start date for this resource assignment.
    *
    * @param val start date
    */
   public void setStart(LocalDateTime val)
   {
      set(AssignmentField.START, val);
   }

   /**
    * Returns the finish date for this resource assignment.
    *
    * @return finish date
    */
   @Override public LocalDateTime getFinish()
   {
      return (LocalDateTime) get(AssignmentField.FINISH);
   }

   /**
    * Sets the finish date for this resource assignment.
    *
    * @param val finish date
    */
   public void setFinish(LocalDateTime val)
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
      return (Duration) get(AssignmentField.ASSIGNMENT_DELAY);
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
      return (Integer) get(AssignmentField.RESOURCE_UNIQUE_ID);
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
      return m_workgroup;
   }

   /**
    * This method retrieves a reference to the task with which this
    * assignment is associated.
    *
    * @return task
    */
   public Task getTask()
   {
      return m_parentFile.getTaskByUniqueID(getTaskUniqueID());
   }

   /**
    * This method retrieves a reference to the resource with which this
    * assignment is associated.
    *
    * @return resource
    */
   public Resource getResource()
   {
      return m_parentFile.getResourceByUniqueID(getResourceUniqueID());
   }

   /**
    * This method returns the Work Contour type of this Assignment.
    *
    * @return the Work Contour type
    */
   public WorkContour getWorkContour()
   {
      return (WorkContour) get(AssignmentField.WORK_CONTOUR);
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
      m_parentFile.getResourceAssignments().remove(this);
   }

   /**
    * Returns the remaining work for this resource assignment.
    *
    * @return remaining work
    */
   public Duration getRemainingWork()
   {
      return (Duration) get(AssignmentField.REMAINING_WORK);
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
      return (Duration) get(AssignmentField.LEVELING_DELAY);
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
    * Retrieve the index of the rate in the cost rate table used
    * to calculate the cost for this resource assignment.
    *
    * @return rate index
    */
   public Integer getRateIndex()
   {
      return (Integer) get(AssignmentField.RATE_INDEX);
   }

   /**
    * Set the index of the rate in the cost rate table used
    * to calculate the cost for this resource assignment.
    *
    * @param index rate index
    */
   public void setRateIndex(Integer index)
   {
      set(AssignmentField.RATE_INDEX, index);
   }

   /**
    * Retrieve the role unique ID in which this resource assignment is being performed.
    *
    * @return Resource unique ID representing a role
    */
   public Integer getRoleUniqueID()
   {
      return (Integer) get(AssignmentField.ROLE_UNIQUE_ID);
   }

   /**
    * Set the unique ID of the role in which this resource assignment is being performed.
    *
    * @param id Resource unique ID representing a role
    */
   public void setRoleUniqueID(Integer id)
   {
      set(AssignmentField.ROLE_UNIQUE_ID, id);
   }

   /**
    * Retrieve the role in which this resource assignment is being performed.
    *
    * @return Resource instance representing a role
    */
   public Resource getRole()
   {
      return m_parentFile.getResourceByUniqueID((Integer) get(AssignmentField.ROLE_UNIQUE_ID));
   }

   /**
    * Set the role in which this resource assignment is being performed.
    *
    * @param role Resource instance representing a role
    */
   public void setRole(Resource role)
   {
      set(AssignmentField.ROLE_UNIQUE_ID, role == null ? null : role.getUniqueID());
   }

   /**
    * Retrieve the rate to use in place of the value from the cost rate table.
    *
    * @return override rate
    */
   public Rate getOverrideRate()
   {
      return (Rate) get(AssignmentField.OVERRIDE_RATE);
   }

   /**
    * Set the rate to use in place of the value from the cost rate table.
    *
    * @param rate override rate
    */
   public void setOverrideRate(Rate rate)
   {
      set(AssignmentField.OVERRIDE_RATE, rate);
   }

   /**
    * Retrieve the source of the cost rate to be used for this resource assignment.
    *
    * @return rate source
    */
   public RateSource getRateSource()
   {
      return (RateSource) get(AssignmentField.RATE_SOURCE);
   }

   /**
    * Set the source of the cost rate to be used for this resource assignment.
    *
    * @param source rate source
    */
   public void setRateSource(RateSource source)
   {
      set(AssignmentField.RATE_SOURCE, source);
   }

   /**
    * Retrieves the effective calendar used for this resource assignment.
    *
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getEffectiveCalendar()
   {
      ProjectCalendar result;

      Task task = getTask();
      Resource resource = getResource();

      ProjectCalendar explicitTaskCalendar = task.getCalendar();
      ProjectCalendar resourceCalendar = resource != null && !task.getIgnoreResourceCalendar() && resource.getType() == ResourceType.WORK ? resource.getCalendar() : null;

      if (explicitTaskCalendar == null)
      {
         if (resourceCalendar == null)
         {
            result = task.getEffectiveCalendar();
         }
         else
         {
            result = resourceCalendar;
         }
      }
      else
      {
         if (resourceCalendar == null)
         {
            result = explicitTaskCalendar;
         }
         else
         {
            result = new CombinedCalendar(explicitTaskCalendar, resourceCalendar);
         }
      }

      if (result != null && task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
      {
         result = new ManuallyScheduledTaskCalendar(result, this);
      }

      return result;
   }

   /**
    * Retrieve the variable rate time units, null if fixed rate.
    *
    * @return variable rate time units
    */
   public TimeUnit getVariableRateUnits()
   {
      return (TimeUnit) get(AssignmentField.VARIABLE_RATE_UNITS);
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
    * Set the parent task unique ID.
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
      return (Integer) get(AssignmentField.TASK_UNIQUE_ID);
   }

   /**
    * Retrieves the budget cost.
    *
    * @return budget cost
    */
   public Number getBudgetCost()
   {
      return (Number) get(AssignmentField.BUDGET_COST);
   }

   /**
    * Sets the budget cost.
    *
    * @param cost budget cost
    */
   public void setBudgetCost(Number cost)
   {
      set(AssignmentField.BUDGET_COST, cost);
   }

   /**
    * Retrieves the budget work value.
    *
    * @return budget work
    */
   public Duration getBudgetWork()
   {
      return (Duration) get(AssignmentField.BUDGET_WORK);
   }

   /**
    * Sets the budget work value.
    *
    * @param work budget work
    */
   public void setBudgetWork(Duration work)
   {
      set(AssignmentField.BUDGET_WORK, work);
   }

   /**
    * Retrieves the baseline budget cost.
    *
    * @return baseline budget cost
    */
   public Number getBaselineBudgetCost()
   {
      return (Number) get(AssignmentField.BASELINE_BUDGET_COST);
   }

   /**
    * Sets the baseline budget cost.
    *
    * @param cost baseline budget cost
    */
   public void setBaselineBudgetCost(Number cost)
   {
      set(AssignmentField.BASELINE_BUDGET_COST, cost);
   }

   /**
    * Retrieves the baseline budget work value.
    *
    * @return baseline budget work
    */
   public Duration getBaselineBudgetWork()
   {
      return (Duration) get(AssignmentField.BASELINE_BUDGET_WORK);
   }

   /**
    * Sets the baseline budget work value.
    *
    * @param work baseline budget work
    */
   public void setBaselineBudgetWork(Duration work)
   {
      set(AssignmentField.BASELINE_BUDGET_WORK, work);
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineCost(int baselineNumber, Number value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_COSTS, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineWork(int baselineNumber, Duration value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_WORKS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Duration getBaselineWork(int baselineNumber)
   {
      return (Duration) get(selectField(AssignmentFieldLists.BASELINE_WORKS, baselineNumber));
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Number getBaselineCost(int baselineNumber)
   {
      return (Number) get(selectField(AssignmentFieldLists.BASELINE_COSTS, baselineNumber));
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineStart(int baselineNumber, LocalDateTime value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_STARTS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public LocalDateTime getBaselineStart(int baselineNumber)
   {
      return (LocalDateTime) get(selectField(AssignmentFieldLists.BASELINE_STARTS, baselineNumber));
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineFinish(int baselineNumber, LocalDateTime value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_FINISHES, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public LocalDateTime getBaselineFinish(int baselineNumber)
   {
      return (LocalDateTime) get(selectField(AssignmentFieldLists.BASELINE_FINISHES, baselineNumber));
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineBudgetCost(int baselineNumber, Number value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_BUDGET_COSTS, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineBudgetWork(int baselineNumber, Duration value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_BUDGET_WORKS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Duration getBaselineBudgetWork(int baselineNumber)
   {
      return (Duration) get(selectField(AssignmentFieldLists.BASELINE_BUDGET_WORKS, baselineNumber));
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Number getBaselineBudgetCost(int baselineNumber)
   {
      return (Number) get(selectField(AssignmentFieldLists.BASELINE_BUDGET_COSTS, baselineNumber));
   }

   /**
    * Set a text value.
    *
    * @param index text index (1-30)
    * @param value text value
    */
   public void setText(int index, String value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_TEXT, index), value);
   }

   /**
    * Retrieve a text value.
    *
    * @param index text index (1-30)
    * @return text value
    */
   public String getText(int index)
   {
      return (String) get(selectField(AssignmentFieldLists.CUSTOM_TEXT, index));
   }

   /**
    * Set a start value.
    *
    * @param index start index (1-10)
    * @param value start value
    */
   public void setStart(int index, LocalDateTime value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_START, index), value);
   }

   /**
    * Retrieve a start value.
    *
    * @param index start index (1-10)
    * @return start value
    */
   public LocalDateTime getStart(int index)
   {
      return (LocalDateTime) get(selectField(AssignmentFieldLists.CUSTOM_START, index));
   }

   /**
    * Set a finish value.
    *
    * @param index finish index (1-10)
    * @param value finish value
    */
   public void setFinish(int index, LocalDateTime value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_FINISH, index), value);
   }

   /**
    * Retrieve a finish value.
    *
    * @param index finish index (1-10)
    * @return finish value
    */
   public LocalDateTime getFinish(int index)
   {
      return (LocalDateTime) get(selectField(AssignmentFieldLists.CUSTOM_FINISH, index));
   }

   /**
    * Set a date value.
    *
    * @param index date index (1-10)
    * @param value date value
    */
   public void setDate(int index, LocalDateTime value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_DATE, index), value);
   }

   /**
    * Retrieve a date value.
    *
    * @param index date index (1-10)
    * @return date value
    */
   public LocalDateTime getDate(int index)
   {
      return (LocalDateTime) get(selectField(AssignmentFieldLists.CUSTOM_DATE, index));
   }

   /**
    * Set a number value.
    *
    * @param index number index (1-20)
    * @param value number value
    */
   public void setNumber(int index, Number value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_NUMBER, index), value);
   }

   /**
    * Retrieve a number value.
    *
    * @param index number index (1-20)
    * @return number value
    */
   public Number getNumber(int index)
   {
      return (Number) get(selectField(AssignmentFieldLists.CUSTOM_NUMBER, index));
   }

   /**
    * Set a duration value.
    *
    * @param index duration index (1-10)
    * @param value duration value
    */
   public void setDuration(int index, Duration value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_DURATION, index), value);
   }

   /**
    * Retrieve a duration value.
    *
    * @param index duration index (1-10)
    * @return duration value
    */
   public Duration getDuration(int index)
   {
      return (Duration) get(selectField(AssignmentFieldLists.CUSTOM_DURATION, index));
   }

   /**
    * Set a cost value.
    *
    * @param index cost index (1-10)
    * @param value cost value
    */
   public void setCost(int index, Number value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_COST, index), value);
   }

   /**
    * Retrieve a cost value.
    *
    * @param index cost index (1-10)
    * @return cost value
    */
   public Number getCost(int index)
   {
      return (Number) get(selectField(AssignmentFieldLists.CUSTOM_COST, index));
   }

   /**
    * Set a flag value.
    *
    * @param index flag index (1-20)
    * @param value flag value
    */
   public void setFlag(int index, boolean value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_FLAG, index), value);
   }

   /**
    * Retrieve a flag value.
    *
    * @param index flag index (1-20)
    * @return flag value
    */
   public boolean getFlag(int index)
   {
      return BooleanHelper.getBoolean((Boolean) get(selectField(AssignmentFieldLists.CUSTOM_FLAG, index)));
   }

   /**
    * Set an enterprise cost value.
    *
    * @param index cost index (1-30)
    * @param value cost value
    */
   public void setEnterpriseCost(int index, Number value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_COST, index), value);
   }

   /**
    * Retrieve an enterprise cost value.
    *
    * @param index cost index (1-30)
    * @return cost value
    */
   public Number getEnterpriseCost(int index)
   {
      return (Number) get(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_COST, index));
   }

   /**
    * Set an enterprise date value.
    *
    * @param index date index (1-30)
    * @param value date value
    */
   public void setEnterpriseDate(int index, LocalDateTime value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_DATE, index), value);
   }

   /**
    * Retrieve an enterprise date value.
    *
    * @param index date index (1-30)
    * @return date value
    */
   public LocalDateTime getEnterpriseDate(int index)
   {
      return (LocalDateTime) get(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_DATE, index));
   }

   /**
    * Set an enterprise duration value.
    *
    * @param index duration index (1-30)
    * @param value duration value
    */
   public void setEnterpriseDuration(int index, Duration value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_DURATION, index), value);
   }

   /**
    * Retrieve an enterprise duration value.
    *
    * @param index duration index (1-30)
    * @return duration value
    */
   public Duration getEnterpriseDuration(int index)
   {
      return (Duration) get(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_DURATION, index));
   }

   /**
    * Set an enterprise flag value.
    *
    * @param index flag index (1-20)
    * @param value flag value
    */
   public void setEnterpriseFlag(int index, boolean value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_FLAG, index), value);
   }

   /**
    * Retrieve an enterprise flag value.
    *
    * @param index flag index (1-20)
    * @return flag value
    */
   public boolean getEnterpriseFlag(int index)
   {
      return BooleanHelper.getBoolean((Boolean) get(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_FLAG, index)));
   }

   /**
    * Set an enterprise number value.
    *
    * @param index number index (1-40)
    * @param value number value
    */
   public void setEnterpriseNumber(int index, Number value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_NUMBER, index), value);
   }

   /**
    * Retrieve an enterprise number value.
    *
    * @param index number index (1-40)
    * @return number value
    */
   public Number getEnterpriseNumber(int index)
   {
      return (Number) get(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_NUMBER, index));
   }

   /**
    * Set an enterprise text value.
    *
    * @param index text index (1-40)
    * @param value text value
    */
   public void setEnterpriseText(int index, String value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_TEXT, index), value);
   }

   /**
    * Retrieve an enterprise text value.
    *
    * @param index text index (1-40)
    * @return text value
    */
   public String getEnterpriseText(int index)
   {
      return (String) get(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_TEXT, index));
   }

   /**
    * Returns the regular work of this resource assignment.
    *
    * @return work
    */
   public Duration getRegularWork()
   {
      return (Duration) get(AssignmentField.REGULAR_WORK);
   }

   /**
    * Sets the regular work for this resource assignment.
    *
    * @param dur work
    */
   public void setRegularWork(Duration dur)
   {
      set(AssignmentField.REGULAR_WORK, dur);
   }

   /**
    * Returns the actual overtime work of this resource assignment.
    *
    * @return work
    */
   public Duration getActualOvertimeWork()
   {
      return (Duration) get(AssignmentField.ACTUAL_OVERTIME_WORK);
   }

   /**
    * Sets the actual overtime work for this resource assignment.
    *
    * @param dur work
    */
   public void setActualOvertimeWork(Duration dur)
   {
      set(AssignmentField.ACTUAL_OVERTIME_WORK, dur);
   }

   /**
    * Returns the remaining overtime work of this resource assignment.
    *
    * @return work
    */
   public Duration getRemainingOvertimeWork()
   {
      return (Duration) get(AssignmentField.REMAINING_OVERTIME_WORK);
   }

   /**
    * Sets the remaining overtime work for this resource assignment.
    *
    * @param dur work
    */
   public void setRemainingOvertimeWork(Duration dur)
   {
      set(AssignmentField.REMAINING_OVERTIME_WORK, dur);
   }

   /**
    * Returns the overtime cost of this resource assignment.
    *
    * @return cost
    */
   public Number getOvertimeCost()
   {
      return (Number) get(AssignmentField.OVERTIME_COST);
   }

   /**
    * Sets the overtime cost for this resource assignment.
    *
    * @param cost cost
    */
   public void setOvertimeCost(Number cost)
   {
      set(AssignmentField.OVERTIME_COST, cost);
   }

   /**
    * Returns the remaining cost of this resource assignment.
    *
    * @return cost
    */
   public Number getRemainingCost()
   {
      return (Number) get(AssignmentField.REMAINING_COST);
   }

   /**
    * Sets the remaining cost for this resource assignment.
    *
    * @param cost cost
    */
   public void setRemainingCost(Number cost)
   {
      set(AssignmentField.REMAINING_COST, cost);
   }

   /**
    * Returns the actual overtime cost of this resource assignment.
    *
    * @return cost
    */
   public Number getActualOvertimeCost()
   {
      return (Number) get(AssignmentField.ACTUAL_OVERTIME_COST);
   }

   /**
    * Sets the actual overtime cost for this resource assignment.
    *
    * @param cost cost
    */
   public void setActualOvertimeCost(Number cost)
   {
      set(AssignmentField.ACTUAL_OVERTIME_COST, cost);
   }

   /**
    * Returns the remaining overtime cost of this resource assignment.
    *
    * @return cost
    */
   public Number getRemainingOvertimeCost()
   {
      return (Number) get(AssignmentField.REMAINING_OVERTIME_COST);
   }

   /**
    * Sets the remaining overtime cost for this resource assignment.
    *
    * @param cost cost
    */
   public void setRemainingOvertimeCost(Number cost)
   {
      set(AssignmentField.REMAINING_OVERTIME_COST, cost);
   }

   /**
    * The BCWP (budgeted cost of work performed) field contains the
    * cumulative value
    * of the assignment's timephased percent complete multiplied by
    * the assignments
    * timephased baseline cost. BCWP is calculated up to the status
    * date or today's
    * date. This information is also known as earned value.
    *
    * @param val the amount to be set
    */
   public void setBCWP(Number val)
   {
      set(AssignmentField.BCWP, val);
   }

   /**
    * The BCWP (budgeted cost of work performed) field contains
    * the cumulative value of the assignment's timephased percent complete
    * multiplied by the assignment's timephased baseline cost.
    * BCWP is calculated up to the status date or today's date.
    * This information is also known as earned value.
    *
    * @return currency amount as float
    */
   public Number getBCWP()
   {
      return (Number) get(AssignmentField.BCWP);
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @param val the amount to set
    */
   public void setBCWS(Number val)
   {
      set(AssignmentField.BCWS, val);
   }

   /**
    * The BCWS (budgeted cost of work scheduled) field contains the cumulative
    * timephased baseline costs up to the status date or today's date.
    *
    * @return currency amount as float
    */
   public Number getBCWS()
   {
      return (Number) get(AssignmentField.BCWS);
   }

   /**
    * Retrieve the ACWP value.
    *
    * @return ACWP value
    */
   public Number getACWP()
   {
      return (Number) get(AssignmentField.ACWP);
   }

   /**
    * Set the ACWP value.
    *
    * @param acwp ACWP value
    */
   public void setACWP(Number acwp)
   {
      set(AssignmentField.ACWP, acwp);
   }

   /**
    * The SV (earned value schedule variance) field shows the difference
    * in cost terms between the current progress and the baseline plan
    * of the task up to the status date or today's date. You can use SV
    * to check costs to determine whether tasks are on schedule.
    * @param val - currency amount
    */
   public void setSV(Number val)
   {
      set(AssignmentField.SV, val);
   }

   /**
    * The SV (earned value schedule variance) field shows the difference in
    * cost terms between the current progress and the baseline plan of the
    * task up to the status date or today's date. You can use SV to
    * check costs to determine whether tasks are on schedule.
    *
    * @return -earned value schedule variance
    */
   public Number getSV()
   {
      return (Number) get(AssignmentField.SV);
   }

   /**
    * The CV (earned value cost variance) field shows the difference
    * between how much it should have cost to achieve the current level of
    * completion on the task, and how much it has actually cost to achieve the
    * current level of completion up to the status date or today's date.
    *
    * @param val value to set
    */
   public void setCV(Number val)
   {
      set(AssignmentField.CV, val);
   }

   /**
    * The CV (earned value cost variance) field shows the difference between
    * how much it should have cost to achieve the current level of completion
    * on the task, and how much it has actually cost to achieve the current
    * level of completion up to the status date or today's date.
    * How Calculated   CV is the difference between BCWP
    * (budgeted cost of work performed) and ACWP
    * (actual cost of work performed). Microsoft Project calculates
    * the CV as follows: CV = BCWP - ACWP
    *
    * @return sum of earned value cost variance
    */
   public Number getCV()
   {
      return (Number) get(AssignmentField.CV);
   }

   /**
    * The Cost Variance field shows the difference between the
    * baseline cost and total cost for a task. The total cost is the
    * current estimate of costs based on actual costs and remaining costs.
    * This is also referred to as variance at completion (VAC).
    *
    * @param val amount
    */
   public void setCostVariance(Number val)
   {
      set(AssignmentField.COST_VARIANCE, val);
   }

   /**
    * The Cost Variance field shows the difference between the baseline cost
    * and total cost for a task. The total cost is the current estimate of costs
    * based on actual costs and remaining costs. This is also referred to as
    * variance at completion (VAC).
    *
    * @return amount
    */
   public Number getCostVariance()
   {
      return (Number) get(AssignmentField.COST_VARIANCE);
   }

   /**
    * The % Work Complete field contains the current status of a task,
    * expressed as the
    * percentage of the task's work that has been completed. You can enter
    * percent work
    * complete, or you can have Microsoft Project calculate it for you
    * based on actual
    * work on the task.
    *
    * @param val value to be set
    */
   public void setPercentageWorkComplete(Number val)
   {
      set(AssignmentField.PERCENT_WORK_COMPLETE, val);
   }

   /**
    * The % Work Complete field contains the current status of a task,
    * expressed as the percentage of the task's work that has been completed.
    * You can enter percent work complete, or you can have Microsoft Project
    * calculate it for you based on actual work on the task.
    *
    * @return percentage as float
    */
   public Number getPercentageWorkComplete()
   {
      return (Number) get(AssignmentField.PERCENT_WORK_COMPLETE);
   }

   /**
    * This method is used to add notes to the current task.
    *
    * @param notes notes to be added
    */
   public void setNotes(String notes)
   {
      set(AssignmentField.NOTES, notes == null ? null : new Notes(notes));
   }

   /**
    * Retrieve the plain text representation of the assignment notes.
    * Use the getNotesObject method to retrieve an object which
    * contains both the plain text notes and, if relevant,
    * the original formatted version of the notes.
    *
    * @return notes
    */
   public String getNotes()
   {
      Object notes = get(AssignmentField.NOTES);
      return notes == null ? "" : notes.toString();
   }

   /**
    * Set the Notes instance representing the assignment notes.
    *
    * @param notes Notes instance
    */
   public void setNotesObject(Notes notes)
   {
      set(AssignmentField.NOTES, notes);
   }

   /**
    * Retrieve an object which contains both the plain text notes
    * and, if relevant, the original formatted version of the notes.
    *
    * @return Notes instance
    */
   public Notes getNotesObject()
   {
      return (Notes) get(AssignmentField.NOTES);
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task have
    * accepted or rejected the task assignment in response to a TeamAssign message
    * regarding their assignments.
    *
    * @param val boolean value
    */
   public void setConfirmed(boolean val)
   {
      set(AssignmentField.CONFIRMED, val);
   }

   /**
    * The Confirmed field indicates whether all resources assigned to a task
    * have accepted or rejected the task assignment in response to a TeamAssign
    * message regarding their assignments.
    *
    * @return boolean
    */
   public boolean getConfirmed()
   {
      return (BooleanHelper.getBoolean((Boolean) get(AssignmentField.CONFIRMED)));
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message should
    * be sent to the assigned resources because of changes to the start date,
    * finish date, or resource reassignments of the task.
    *
    * @param val - boolean
    */
   public void setUpdateNeeded(boolean val)
   {
      set(AssignmentField.UPDATE_NEEDED, val);
   }

   /**
    * The Update Needed field indicates whether a TeamUpdate message
    * should be sent to the assigned resources because of changes to the
    * start date, finish date, or resource reassignments of the task.
    *
    * @return true if needed.
    */
   public boolean getUpdateNeeded()
   {
      return (BooleanHelper.getBoolean((Boolean) get(AssignmentField.UPDATE_NEEDED)));
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project
    * file, or from another program.
    *
    * @param flag boolean value
    */
   public void setLinkedFields(boolean flag)
   {
      set(AssignmentField.LINKED_FIELDS, flag);
   }

   /**
    * The Linked Fields field indicates whether there are OLE links to the task,
    * either from elsewhere in the active project, another Microsoft Project file,
    * or from another program.
    *
    * @return boolean
    */
   public boolean getLinkedFields()
   {
      return (BooleanHelper.getBoolean((Boolean) get(AssignmentField.LINKED_FIELDS)));
   }

   /**
    * Retrieves the task hyperlink attribute.
    *
    * @return hyperlink attribute
    */
   public String getHyperlink()
   {
      return (String) get(AssignmentField.HYPERLINK);
   }

   /**
    * Retrieves the task hyperlink address attribute.
    *
    * @return hyperlink address attribute
    */
   public String getHyperlinkAddress()
   {
      return (String) get(AssignmentField.HYPERLINK_ADDRESS);
   }

   /**
    * Retrieves the task hyperlink sub-address attribute.
    *
    * @return hyperlink sub address attribute
    */
   public String getHyperlinkSubAddress()
   {
      return (String) get(AssignmentField.HYPERLINK_SUBADDRESS);
   }

   /**
    * Sets the task hyperlink attribute.
    *
    * @param text hyperlink attribute
    */
   public void setHyperlink(String text)
   {
      set(AssignmentField.HYPERLINK, text);
   }

   /**
    * Sets the task hyperlink address attribute.
    *
    * @param text hyperlink address attribute
    */
   public void setHyperlinkAddress(String text)
   {
      set(AssignmentField.HYPERLINK_ADDRESS, text);
   }

   /**
    * Sets the task hyperlink sub address attribute.
    *
    * @param text hyperlink sub address attribute
    */
   public void setHyperlinkSubAddress(String text)
   {
      set(AssignmentField.HYPERLINK_SUBADDRESS, text);
   }

   /**
    * The Work Variance field contains the difference between a task's baseline
    * work and the currently scheduled work.
    *
    * @param val - duration
    */
   public void setWorkVariance(Duration val)
   {
      set(AssignmentField.WORK_VARIANCE, val);
   }

   /**
    * The Work Variance field contains the difference between a task's
    * baseline work and the currently scheduled work.
    *
    * @return Duration representing duration.
    */
   public Duration getWorkVariance()
   {
      return (Duration) get(AssignmentField.WORK_VARIANCE);
   }

   /**
    * The Start Variance field contains the amount of time that represents the
    * difference between a task's baseline start date and its currently
    * scheduled start date.
    *
    * @param val - duration
    */
   public void setStartVariance(Duration val)
   {
      set(AssignmentField.START_VARIANCE, val);
   }

   /**
    * Calculate the start variance.
    *
    * @return start variance
    */
   public Duration getStartVariance()
   {
      return (Duration) get(AssignmentField.START_VARIANCE);
   }

   /**
    * The Finish Variance field contains the amount of time that represents the
    * difference between a task's baseline finish date and its forecast
    * or actual finish date.
    *
    * @param duration duration value
    */
   public void setFinishVariance(Duration duration)
   {
      set(AssignmentField.FINISH_VARIANCE, duration);
   }

   /**
    * Calculate the finish variance.
    *
    * @return finish variance
    */
   public Duration getFinishVariance()
   {
      return (Duration) get(AssignmentField.FINISH_VARIANCE);
   }

   /**
    * The Created field contains the date and time when a task was added
    * to the project.
    *
    * @return Date
    */
   public LocalDateTime getCreateDate()
   {
      return (LocalDateTime) get(AssignmentField.CREATED);
   }

   /**
    * The Created field contains the date and time when a task was
    * added to the project.
    *
    * @param val date
    */
   public void setCreateDate(LocalDateTime val)
   {
      set(AssignmentField.CREATED, val);
   }

   /**
    * Retrieve the task GUID.
    *
    * @return task GUID
    */
   public UUID getGUID()
   {
      return (UUID) get(AssignmentField.GUID);
   }

   /**
    * Set the task GUID.
    *
    * @param value task GUID
    */
   public void setGUID(UUID value)
   {
      set(AssignmentField.GUID, value);
   }

   /**
    * Sets a flag to indicate if a response has been received from a resource
    * assigned to a task.
    *
    * @param val boolean value
    */
   public void setResponsePending(boolean val)
   {
      set(AssignmentField.RESPONSE_PENDING, val);
   }

   /**
    * Retrieves a flag to indicate if a response has been received from a resource
    * assigned to a task.
    *
    * @return boolean value
    */
   public boolean getResponsePending()
   {
      return (BooleanHelper.getBoolean((Boolean) get(AssignmentField.RESPONSE_PENDING)));
   }

   /**
    * Sets a flag to indicate if a response has been received from a resource
    * assigned to a task.
    *
    * @param val boolean value
    */
   public void setTeamStatusPending(boolean val)
   {
      set(AssignmentField.TEAM_STATUS_PENDING, val);
   }

   /**
    * Retrieves a flag to indicate if a response has been received from a resource
    * assigned to a task.
    *
    * @return boolean value
    */
   public boolean getTeamStatusPending()
   {
      return (BooleanHelper.getBoolean((Boolean) get(AssignmentField.TEAM_STATUS_PENDING)));
   }

   /**
    * Sets VAC for this resource assignment.
    *
    * @param value VAC value
    */
   public void setVAC(Number value)
   {
      set(AssignmentField.VAC, value);
   }

   /**
    * Returns the VAC for this resource assignment.
    *
    * @return VAC value
    */
   public Number getVAC()
   {
      return (Number) get(AssignmentField.VAC);
   }

   /**
    * Sets the index of the cost rate table for this assignment.
    *
    * @param index cost rate table index
    */
   public void setCostRateTableIndex(int index)
   {
      set(AssignmentField.COST_RATE_TABLE, Integer.valueOf(index));
   }

   /**
    * Returns the cost rate table index for this assignment.
    *
    * @return cost rate table index
    */
   public int getCostRateTableIndex()
   {
      int value = NumberHelper.getInt((Integer) get(AssignmentField.COST_RATE_TABLE));
      return value < 0 || value >= CostRateTable.MAX_TABLES ? 0 : value;
   }

   /**
    * Returns the cost rate table for this assignment.
    *
    * @return cost rate table index
    */
   public CostRateTable getCostRateTable()
   {
      // If the rate source is "override" then there is no
      // cost rate table: we're just using a single rate value,
      // so we return null here.
      RateSource rateSource = getRateSource();
      if (rateSource == RateSource.OVERRIDE)
      {
         return null;
      }

      // If we can't find the resource/role we're assigned to, return null
      Resource resource = rateSource == RateSource.ROLE ? getRole() : getResource();
      if (resource == null)
      {
         return null;
      }

      return resource.getCostRateTable(getCostRateTableIndex());
   }

   /**
    * Retrieves the hyperlink screen tip attribute.
    *
    * @return hyperlink screen tip attribute
    */
   public String getHyperlinkScreenTip()
   {
      return (String) get(AssignmentField.HYPERLINK_SCREEN_TIP);
   }

   /**
    * Sets the hyperlink screen tip attribute.
    *
    * @param text hyperlink screen tip attribute
    */
   public void setHyperlinkScreenTip(String text)
   {
      set(AssignmentField.HYPERLINK_SCREEN_TIP, text);
   }

   /**
    * Retrieves the resource request type attribute.
    *
    * @return resource request type
    */
   public ResourceRequestType getResourceRequestType()
   {
      return (ResourceRequestType) get(AssignmentField.RESOURCE_REQUEST_TYPE);
   }

   /**
    * Sets the resource request type attribute.
    *
    * @param type resource request type
    */
   public void setResourceRequestType(ResourceRequestType type)
   {
      set(AssignmentField.RESOURCE_REQUEST_TYPE, type);
   }

   /**
    * Retrieve the stop date.
    *
    * @return stop date
    */
   public LocalDateTime getStop()
   {
      return (LocalDateTime) get(AssignmentField.STOP);
   }

   /**
    * Set the stop date.
    *
    * @param stop stop date
    */
   public void setStop(LocalDateTime stop)
   {
      set(AssignmentField.STOP, stop);
   }

   /**
    * Retrieve the resume date.
    *
    * @return resume date
    */
   public LocalDateTime getResume()
   {
      return (LocalDateTime) get(AssignmentField.RESUME);
   }

   /**
    * Set the resume date.
    *
    * @param resume resume date
    */
   public void setResume(LocalDateTime resume)
   {
      set(AssignmentField.RESUME, resume);
   }

   /**
    * Retrieve the planned work field.
    *
    * @return planned work value
    */
   public Duration getPlannedWork()
   {
      return (Duration) get(AssignmentField.PLANNED_WORK);
   }

   /**
    * Set the planned work field.
    *
    * @param value planned work value
    */
   public void setPlannedWork(Duration value)
   {
      set(AssignmentField.PLANNED_WORK, value);
   }

   /**
    * Retrieve the planned cost field.
    *
    * @return planned cost value
    */
   public Number getPlannedCost()
   {
      return (Number) get(AssignmentField.PLANNED_COST);
   }

   /**
    * Set the planned cost field.
    *
    * @param value planned cost value
    */
   public void setPlannedCost(Number value)
   {
      set(AssignmentField.PLANNED_COST, value);
   }

   /**
    * Set the planned start field.
    *
    * @return planned start value
    */
   public LocalDateTime getPlannedStart()
   {
      return (LocalDateTime) get(AssignmentField.PLANNED_START);
   }

   /**
    * Retrieve the planned start field.
    *
    * @param value planned start value
    */
   public void setPlannedStart(LocalDateTime value)
   {
      set(AssignmentField.PLANNED_START, value);
   }

   /**
    * Retrieve the planned finish value.
    *
    * @return planed finish value
    */
   public LocalDateTime getPlannedFinish()
   {
      return (LocalDateTime) get(AssignmentField.PLANNED_FINISH);
   }

   /**
    * Set the planned finish value.
    *
    * @param value planned finish value
    */
   public void setPlannedFinish(LocalDateTime value)
   {
      set(AssignmentField.PLANNED_FINISH, value);
   }

   /**
    * Retrieve the calculate costs from units flag.
    *
    * @return calculate costs from units flag
    */
   public boolean getCalculateCostsFromUnits()
   {
      return BooleanHelper.getBoolean((Boolean) get(AssignmentField.CALCULATE_COSTS_FROM_UNITS));
   }

   /**
    * Set the calculate costs from units flag.
    *
    * @param calculateCostsFromUnits calculate costs from units flag
    */
   public void setCalculateCostsFromUnits(boolean calculateCostsFromUnits)
   {
      set(AssignmentField.CALCULATE_COSTS_FROM_UNITS, calculateCostsFromUnits);
   }

   /**
    * Retrieve the cost account unique ID for this resource assignment.
    *
    * @return cost account unique ID
    */
   public Integer getCostAccountUniqueID()
   {
      return (Integer) get(AssignmentField.COST_ACCOUNT_UNIQUE_ID);
   }

   /**
    * Set the cost account unique ID for this resource assignment.
    *
    * @param id cost account unique ID
    */
   public void setCostAccountUniqueID(Integer id)
   {
      set(AssignmentField.COST_ACCOUNT_UNIQUE_ID, id);
   }

   /**
    * Retrieve the cost account for this resource assignment.
    *
    * @return CostAccount instance for this resource assignment
    */
   public CostAccount getCostAccount()
   {
      return m_parentFile.getCostAccounts().getByUniqueID((Integer) get(AssignmentField.COST_ACCOUNT_UNIQUE_ID));
   }

   /**
    * Set the cost account for this resource assignment.
    *
    * @param costAccount cost account for this resource assignment
    */
   public void setCostAccount(CostAccount costAccount)
   {
      set(AssignmentField.COST_ACCOUNT_UNIQUE_ID, costAccount == null ? null : costAccount.getUniqueID());
   }

   /**
    * Retrieve the remaining late finish value.
    *
    * @return remaining late finish
    */
   public LocalDateTime getRemainingLateFinish()
   {
      return (LocalDateTime) get(AssignmentField.REMAINING_LATE_FINISH);
   }

   /**
    * Set the remaining late finish value.
    *
    * @param date remaining late finish
    */
   public void setRemainingLateFinish(LocalDateTime date)
   {
      set(AssignmentField.REMAINING_LATE_FINISH, date);
   }

   /**
    * Retrieve the remaining late start value.
    *
    * @return remaining late start
    */
   public LocalDateTime getRemainingLateStart()
   {
      return (LocalDateTime) get(AssignmentField.REMAINING_LATE_START);
   }

   /**
    * Set the remaining late start value.
    *
    * @param date remaining late start
    */
   public void setRemainingLateStart(LocalDateTime date)
   {
      set(AssignmentField.REMAINING_LATE_START, date);
   }

   /**
    * Retrieve the remaining early finish value.
    *
    * @return remaining early finish
    */
   public LocalDateTime getRemainingEarlyFinish()
   {
      return (LocalDateTime) get(AssignmentField.REMAINING_EARLY_FINISH);
   }

   /**
    * Set the remaining early finish value.
    *
    * @param date remaining early finish
    */
   public void setRemainingEarlyFinish(LocalDateTime date)
   {
      set(AssignmentField.REMAINING_EARLY_FINISH, date);
   }

   /**
    * Retrieve the remaining early start value.
    *
    * @return remaining early start
    */
   public LocalDateTime getRemainingEarlyStart()
   {
      return (LocalDateTime) get(AssignmentField.REMAINING_EARLY_START);
   }

   /**
    * Set the remaining early start value.
    *
    * @param date remaining early start
    */
   public void setRemainingEarlyStart(LocalDateTime date)
   {
      set(AssignmentField.REMAINING_EARLY_START, date);
   }

   /**
    * Based on the configuration data for this resource assignment,
    * return the cost rate effective on the supplied date.
    *
    * @param date target date
    * @return cost rate effective on the target date
    */
   public Rate getEffectiveRate(LocalDateTime date)
   {
      // If the rate source is "override", return the
      // override rate value configured for this assignment.
      if (getRateSource() == RateSource.OVERRIDE)
      {
         return getOverrideRate();
      }

      // Based on the configuration from this assignment,
      // retrieve the correct cost rate table.
      CostRateTable table = getCostRateTable();
      if (table == null)
      {
         return null;
      }

      // Retrieve the active table entry for the target date
      CostRateTableEntry entry = table.getEntryByDate(date);
      if (entry == null)
      {
         return null;
      }

      // Retrieve the required rate from the table entry
      return entry.getRate(getRateIndex().intValue());
   }

   /**
    * Retrieve the resource assignment code values associated with this resource assignment.
    *
    * @return map of resource assignment code values
    */
   @SuppressWarnings("unchecked") public Map<ResourceAssignmentCode, ResourceAssignmentCodeValue> getResourceAssignmentCodeValues()
   {
      return (Map<ResourceAssignmentCode, ResourceAssignmentCodeValue>) get(AssignmentField.RESOURCE_ASSIGNMENT_CODE_VALUES);
   }

   /**
    * Assign a resource assignment code value to this resource assignment.
    *
    * @param value resource assignment code value
    */
   @SuppressWarnings("unchecked") public void addResourceAssignmentCodeValue(ResourceAssignmentCodeValue value)
   {
      ((Map<ResourceAssignmentCode, ResourceAssignmentCodeValue>) get(AssignmentField.RESOURCE_ASSIGNMENT_CODE_VALUES)).put(value.getParentCode(), value);
   }

   /**
    * Returns the actual regular cost  of this resource assignment.
    *
    * @return actual regular cost
    */
   public Number getActualRegularCost()
   {
      return (Number) get(AssignmentField.ACTUAL_REGULAR_COST);
   }

   /**
    * Returns the remaining regular cost of this resource assignment.
    *
    * @return remaining regular cost
    */
   public Number getRemainingRegularCost()
   {
      return (Number) get(AssignmentField.REMAINING_REGULAR_COST);
   }

   /**
    * Returns the actual regular work of this resource assignment.
    *
    * @return actual regular work
    */
   public Duration getActualRegularWork()
   {
      return (Duration) get(AssignmentField.ACTUAL_REGULAR_WORK);
   }

   /**
    * Returns the remaining regular work of this resource assignment.
    *
    * @return remaining regular work
    */
   public Duration getRemainingRegularWork()
   {
      return (Duration) get(AssignmentField.REMAINING_REGULAR_WORK);
   }

   /**
    * Retrieves the planned material utilization for a material resource.
    *
    * @return planned material utilization
    */
   public Number getPlannedMaterial()
   {
      return (Number) get(AssignmentField.PLANNED_MATERIAL);
   }

   /**
    * Retrieves the actual material utilization for a material resource.
    *
    * @return actual material utilization
    */
   public Number getActualMaterial()
   {
      return (Number) get(AssignmentField.ACTUAL_MATERIAL);
   }

   /**
    * Retrieves the remaining material utilization for a material resource.
    *
    * @return remaining material utilization
    */
   public Number getRemainingMaterial()
   {
      return (Number) get(AssignmentField.REMAINING_MATERIAL);
   }

   /**
    * Retrieves the material utilization for a material resource.
    *
    * @return material utilization
    */
   public Number getMaterial()
   {
      return (Number) get(AssignmentField.MATERIAL);
   }

   /**
    * Retrieves the baseline material utilization for a material resource.
    *
    * @return baseline material utilization
    */
   public Number getBaselineMaterial()
   {
      return (Number) get(AssignmentField.BASELINE_MATERIAL);
   }

   /**
    * Retrieve a baseline material utilization value for a material resource.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline material utilization value
    */
   public Number getBaselineMaterial(int baselineNumber)
   {
      return (Number) get(selectField(AssignmentFieldLists.BASELINE_MATERIALS, baselineNumber));
   }

   /**
    * Retrieve a list of LocalDateTimeRange instances representing working time over the duration
    * of this resource assignment.
    *
    * @return working time as LocalDateTimeRange instances
    */
   public List<LocalDateTimeRange> getWorkSplits()
   {
      List<TimephasedWork> actualWork = getRawTimephasedActualRegularWork();
      List<TimephasedWork> remainingWork = getRawTimephasedRemainingRegularWork();
      if (actualWork.isEmpty() && remainingWork.isEmpty())
      {
         return Collections.emptyList();
      }

      List<LocalDateTimeRange> result = new ArrayList<>();
      LocalDateTimeRange lastRange = null;
      ProjectCalendar calendar = getEffectiveCalendar();
      for (TimephasedWork work : actualWork)
      {
         // If we have a block representing non-working time, move on.
         if (work.getTotalAmount().getDuration() == 0.0)
         {
            lastRange = null;
            continue;
         }

         // We have a previous working range, and the current range can be merged with it
         if (lastRange != null && (lastRange.getEnd().isEqual(work.getStart()) || calendar.getWork(lastRange.getEnd(), work.getStart(), TimeUnit.HOURS).getDuration() == 0.0))
         {
            result.remove(result.size() - 1);
            lastRange = new LocalDateTimeRange(lastRange.getStart(), work.getFinish());
            result.add(lastRange);
            continue;
         }

         lastRange = new LocalDateTimeRange(work.getStart(), work.getFinish());
         result.add(lastRange);
      }

      for (TimephasedWork work : remainingWork)
      {
         // If we have a block representing non-working time, move on.
         if (work.getTotalAmount().getDuration() == 0.0)
         {
            lastRange = null;
            continue;
         }

         // We have a previous working range, and the current range can be merged with it
         if (lastRange != null && (lastRange.getEnd().isEqual(work.getStart()) || calendar.getWork(lastRange.getEnd(), work.getStart(), TimeUnit.HOURS).getDuration() == 0.0))
         {
            result.remove(result.size() - 1);
            lastRange = new LocalDateTimeRange(lastRange.getStart(), work.getFinish());
            result.add(lastRange);
            continue;
         }

         lastRange = new LocalDateTimeRange(work.getStart(), work.getFinish());
         result.add(lastRange);
      }

      return result;
   }

   /**
    * Retrieves raw timephased planned work for this resource assignment.
    *
    * @return raw timephased planned work
    */
   @SuppressWarnings("unchecked") public List<TimephasedWork> getRawTimephasedPlannedWork()
   {
      return (List<TimephasedWork>) get(AssignmentField.RAW_TIMEPHASED_PLANNED_WORK);
   }

   /**
    * Retrieves raw timephased actual regular work for this resource assignment.
    *
    * @return raw timephased actual regular work
    */
   @SuppressWarnings("unchecked") public List<TimephasedWork> getRawTimephasedActualRegularWork()
   {
      return (List<TimephasedWork>) get(AssignmentField.RAW_TIMEPHASED_ACTUAL_REGULAR_WORK);
   }

   /**
    * Retrieves raw timephased remaining regular work for this resource assignment.
    *
    * @return raw timephased remaining regular work
    */
   @SuppressWarnings("unchecked") public List<TimephasedWork> getRawTimephasedRemainingRegularWork()
   {
      return (List<TimephasedWork>) get(AssignmentField.RAW_TIMEPHASED_REMAINING_REGULAR_WORK);
   }

   /**
    * Retrieves raw timephased actual overtime work for this resource assignment.
    *
    * @return raw timephased actual overtime work
    */
   @SuppressWarnings("unchecked") public List<TimephasedWork> getRawTimephasedActualOvertimeWork()
   {
      return (List<TimephasedWork>) get(AssignmentField.RAW_TIMEPHASED_ACTUAL_OVERTIME_WORK);
   }

   /**
    * Retrieves raw timephased remaining overtime work for this resource assignment.
    *
    * @return raw timephased remaining overtime work
    */
   @SuppressWarnings("unchecked") public List<TimephasedWork> getRawTimephasedRemainingOvertimeWork()
   {
      return (List<TimephasedWork>) get(AssignmentField.RAW_TIMEPHASED_REMAINING_OVERTIME_WORK);
   }

   /**
    * Retrieve raw timephased baseline work for this resource assignment.
    * Note that index 0 represents "Baseline", index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @return raw timephased baseline work
    */
   @SuppressWarnings("unchecked") public List<TimephasedWork> getRawTimephasedBaselineWork(int index)
   {
      return (List<TimephasedWork>) get(AssignmentFieldLists.RAW_TIMEPHASED_BASELINE_WORKS[index]);
   }

   /**
    * Retrieve raw timephased baseline cost for this resource assignment.
    * Note that index 0 represents "Baseline", index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @return raw timephased baseline cost
    */
   @SuppressWarnings("unchecked") public List<TimephasedCost> getRawTimephasedBaselineCost(int index)
   {
      return (List<TimephasedCost>) get(AssignmentFieldLists.RAW_TIMEPHASED_BASELINE_COSTS[index]);
   }

   @Override public List<Duration> getTimephasedDurationValues(FieldType field, List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      TimephasedDurationFunction fn = TIMEPHASED_WORK_FUNCTIONS.get(field);
      return fn == null ? Arrays.asList(new Duration[ranges.size()]) : fn.apply(this, ranges, units);
   }

   @Override public List<Number> getTimephasedNumericValues(FieldType field, List<LocalDateTimeRange> ranges)
   {
      TimephasedNumericFunction fn = TIMEPHASED_NUMERIC_FUNCTIONS.get(field);
      return fn == null ? Arrays.asList(new Number[ranges.size()]) : fn.apply(this, ranges);
   }

   /**
    * Retrieve timephased planned work for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedPlannedWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.segmentWork(getEffectiveCalendar(), getOrCreateRawTimephasedPlannedWork(), ranges, units);
   }

   /**
    * Retrieve timephased actual regular work for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedActualRegularWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.segmentWork(getEffectiveCalendar(), getOrCreateRawTimephasedActualWork(getRawTimephasedActualRegularWork(), this::getActualRegularWork), ranges, units);
   }

   /**
    * Retrieve timephased actual overtime work for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedActualOvertimeWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.segmentWork(getEffectiveCalendar(), getOrCreateRawTimephasedActualWork(getRawTimephasedActualOvertimeWork(), this::getActualOvertimeWork), ranges, units);
   }

   /**
    * Retrieve timephased actual work (regular+overtime) for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedActualWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.addTimephasedDurations(getTimephasedActualRegularWork(ranges, units), getTimephasedActualOvertimeWork(ranges, units));
   }

   /**
    * Retrieve timephased remaining regular work for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedRemainingRegularWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.segmentWork(getEffectiveCalendar(), getOrCreateRawTimephasedRemainingWork(getRawTimephasedRemainingRegularWork(), this::getRemainingRegularWork), ranges, units);
   }

   /**
    * Retrieve timephased remaining overtime work for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedRemainingOvertimeWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.segmentWork(getEffectiveCalendar(), getOrCreateRawTimephasedRemainingWork(getRawTimephasedRemainingOvertimeWork(), this::getRemainingOvertimeWork), ranges, units);
   }

   /**
    * Retrieve timephased remaining work (regular+overtime) for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedRemainingWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.addTimephasedDurations(getTimephasedRemainingRegularWork(ranges, units), getTimephasedRemainingOvertimeWork(ranges, units));
   }

   /**
    * Retrieve timephased work (actual+remaining) for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedWork(List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.addTimephasedDurations(getTimephasedActualWork(ranges, units), getTimephasedRemainingWork(ranges, units));
   }

   /**
    * Retrieve the timephased baseline work for the supplied time ranges.
    * Note that index 0 represents "Baseline", index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @param ranges time ranges over which timephased work is summarized
    * @param units units in which to express the timephased work
    * @return list of Duration instances representing timephased work for the supplied ranges
    */
   public List<Duration> getTimephasedBaselineWork(int index, List<LocalDateTimeRange> ranges, TimeUnit units)
   {
      return TimephasedUtility.segmentWork(m_parentFile.getBaselineCalendar(), getRawTimephasedBaselineWork(index), ranges, units);
   }

   /**
    * Retrieve timephased remaining regular cost for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedRemainingRegularCost(List<LocalDateTimeRange> ranges)
   {
      // If we have no ranges, return an empty list.
      if (ranges == null || ranges.isEmpty())
      {
         return Collections.emptyList();
      }

      // If the ranges are outside the assignment, return null values
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      if (ranges.get(ranges.size() - 1).isBefore(assignmentRange) || ranges.get(0).isAfter(assignmentRange))
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      ResourceType type = getResource() != null ? getResource().getType() : ResourceType.WORK;
      if (type == ResourceType.COST)
      {
         return getTimephasedCostResourceRemainingCost(ranges);
      }

      List<Number> result;
      AccrueType accrueAt = getResource() != null ? getResource().getAccrueAt() : AccrueType.PRORATED;
      switch (accrueAt)
      {
         case START:
         {
            result = getTimephasedRemainingCostAccruedAtStart(ranges,
               () -> Double.valueOf(NumberHelper.getDouble(getCost()) - NumberHelper.getDouble(getOvertimeCost())),
               () -> Double.valueOf(NumberHelper.getDouble(getRemainingCost()) - NumberHelper.getDouble(getRemainingOvertimeCost())));
            break;
         }
         case END:
         {
            result = getTimephasedRemainingCostAccruedAtEnd(ranges,
               () -> Double.valueOf(NumberHelper.getDouble(getCost()) - NumberHelper.getDouble(getOvertimeCost())),
               () -> Double.valueOf(NumberHelper.getDouble(getRemainingCost()) - NumberHelper.getDouble(getRemainingOvertimeCost())));
            break;
         }

         default:
         {
            result = addTimephasedRemainingCostPerUse(ranges, getTimephasedCost(ranges, 0, (List<LocalDateTimeRange> r) -> getTimephasedRemainingRegularWork(r, TimeUnit.HOURS)));
            break;
         }
      }

      return result;
   }

   /**
    * Retrieve timephased remaining regular overtime cost for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedRemainingOvertimeCost(List<LocalDateTimeRange> ranges)
   {
      // If we have no ranges, return an empty list.
      if (ranges == null || ranges.isEmpty())
      {
         return Collections.emptyList();
      }

      // If the ranges are outside the assignment, return null values
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      if (ranges.get(ranges.size() - 1).isBefore(assignmentRange) || ranges.get(0).isAfter(assignmentRange))
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      ResourceType type = getResource() != null ? getResource().getType() : ResourceType.WORK;
      if (type == ResourceType.COST)
      {
         // Cost resources don't have overtime
         return Arrays.asList(new Number[ranges.size()]);
      }

      AccrueType accrueAt = getResource() != null ? getResource().getAccrueAt() : AccrueType.PRORATED;
      switch (accrueAt)
      {
         case START:
         {
            return getTimephasedRemainingCostAccruedAtStart(ranges, this::getOvertimeCost, this::getRemainingOvertimeCost);
         }
         case END:
         {
            return getTimephasedRemainingCostAccruedAtEnd(ranges, this::getOvertimeCost, this::getRemainingOvertimeCost);
         }

         default:
         {
            return getTimephasedCost(ranges, 1, (List<LocalDateTimeRange> r) -> getTimephasedRemainingOvertimeWork(r, TimeUnit.HOURS));
         }
      }
   }

   /**
    * Retrieve timephased remaining cost (regular+overtime) for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedRemainingCost(List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.addTimephasedNumbers(getTimephasedRemainingRegularCost(ranges), getTimephasedRemainingOvertimeCost(ranges));
   }

   /**
    * Retrieve timephased actual regular cost for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedActualRegularCost(List<LocalDateTimeRange> ranges)
   {
      // If we have no ranges, return an empty list.
      if (ranges == null || ranges.isEmpty())
      {
         return Collections.emptyList();
      }

      // If we haven't started, we have no actual cost... unless we're working with a cost resource,
      // which can have an actual cost without progress.
      ResourceType type = getResource() != null ? getResource().getType() : ResourceType.WORK;
      if (getActualStart() == null && type != ResourceType.COST)
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      // If the ranges are outside the assignment, return null values
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      if (ranges.get(ranges.size() - 1).isBefore(assignmentRange) || ranges.get(0).isAfter(assignmentRange))
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      if (type == ResourceType.COST)
      {
         return getTimephasedCostResourceActualCost(ranges);
      }

      List<Number> result;
      AccrueType accrueAt = getResource() != null ? getResource().getAccrueAt() : AccrueType.PRORATED;
      switch (accrueAt)
      {
         case START:
         {
            result = getTimephasedCostAccruedAtStart(ranges, () -> Double.valueOf(NumberHelper.getDouble(getActualCost()) - NumberHelper.getDouble(getActualOvertimeCost())), this::getTimephasedActualWork);
            break;
         }

         case END:
         {
            result = getTimephasedCostAccruedAtEnd(ranges, () -> Double.valueOf(NumberHelper.getDouble(getActualCost()) - NumberHelper.getDouble(getActualOvertimeCost())), this::getTimephasedActualWork);
            break;
         }

         default:
         {
            result = addTimephasedActualCostPerUse(ranges, getTimephasedCost(ranges, 0, (List<LocalDateTimeRange> r) -> getTimephasedActualRegularWork(r, TimeUnit.HOURS)));
            break;
         }
      }

      return result;
   }

   /**
    * Retrieve timephased actual overtime cost for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedActualOvertimeCost(List<LocalDateTimeRange> ranges)
   {
      // If we have no ranges, return an empty list.
      if (ranges == null || ranges.isEmpty())
      {
         return Collections.emptyList();
      }

      // If we haven't started, we have no actual cost... unless we're working with a cost resource,
      // which can have an actual cost without progress.
      ResourceType type = getResource() != null ? getResource().getType() : ResourceType.WORK;
      if (getActualStart() == null && type != ResourceType.COST)
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      // If the ranges are outside the assignment, return null values
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      if (ranges.get(ranges.size() - 1).isBefore(assignmentRange) || ranges.get(0).isAfter(assignmentRange))
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      if (type == ResourceType.COST)
      {
         // Cost resources don't have overtime
         return Arrays.asList(new Number[ranges.size()]);
      }

      AccrueType accrueAt = getResource() != null ? getResource().getAccrueAt() : AccrueType.PRORATED;
      switch (accrueAt)
      {
         case START:
         {
            return getTimephasedCostAccruedAtStart(ranges, this::getActualOvertimeCost, this::getTimephasedActualWork);
         }
         case END:
         {
            return getTimephasedCostAccruedAtEnd(ranges, this::getActualOvertimeCost, this::getTimephasedActualWork);
         }

         default:
         {
            return getTimephasedCost(ranges, 1, (List<LocalDateTimeRange> r) -> getTimephasedActualOvertimeWork(r, TimeUnit.HOURS));
         }
      }
   }

   public List<Number> getTimephasedPlannedCost(List<LocalDateTimeRange> ranges)
   {
      // If we have no ranges, return an empty list.
      if (ranges == null || ranges.isEmpty())
      {
         return Collections.emptyList();
      }

      // If the ranges are outside the assignment, return null values
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      if (ranges.get(ranges.size() - 1).isBefore(assignmentRange) || ranges.get(0).isAfter(assignmentRange))
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      List<Number> result;
      AccrueType accrueAt = getResource() != null ? getResource().getAccrueAt() : AccrueType.PRORATED;
      switch (accrueAt)
      {
         case START:
         {
            result = getTimephasedCostAccruedAtStart(ranges, this::getPlannedCost, this::getTimephasedPlannedWork);
            break;
         }

         case END:
         {
            result = getTimephasedCostAccruedAtEnd(ranges, this::getPlannedCost, this::getTimephasedPlannedWork);
            break;
         }

         default:
         {
            result = getTimephasedCost(ranges, 0, (List<LocalDateTimeRange> r) -> getTimephasedPlannedWork(r, TimeUnit.HOURS));
            break;
         }
      }

      return result;
   }

   /**
    * Retrieve timephased actual cost (regular+overtime) for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedActualCost(List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.addTimephasedNumbers(getTimephasedActualRegularCost(ranges), getTimephasedActualOvertimeCost(ranges));
   }

   /**
    * Retrieve timephased cost (actual+remaining) for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedCost(List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.addTimephasedNumbers(getTimephasedActualCost(ranges), getTimephasedRemainingCost(ranges));
   }

   /**
    * Retrieve the timephased baseline cost for the supplied time ranges.
    * Note that index 0 represents "Baseline", index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @param ranges time ranges over which timephased cost is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   public List<Number> getTimephasedBaselineCost(int index, List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.segmentCost(m_parentFile.getBaselineCalendar(), getRawTimephasedBaselineCost(index), ranges);
   }

   /**
    * Retrieve timephased planned material utilization for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased material utilization is summarized
    * @return list of Number instances representing timephased material utilization for the supplied ranges
    */
   public List<Number> getTimephasedPlannedMaterial(List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.segmentMaterial(this, getEffectiveCalendar(), getOrCreateRawTimephasedPlannedWork(), ranges);
   }

   /**
    * Retrieve timephased actual material utilization for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased material utilization is summarized
    * @return list of Number instances representing timephased material utilization for the supplied ranges
    */
   public List<Number> getTimephasedActualMaterial(List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.segmentMaterial(this, getEffectiveCalendar(), getRawTimephasedActualRegularWork(), ranges);
   }

   /**
    * Retrieve timephased remaining material utilization for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased material utilization is summarized
    * @return list of Number instances representing timephased material utilization for the supplied ranges
    */
   public List<Number> getTimephasedRemainingMaterial(List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.segmentMaterial(this, getEffectiveCalendar(), getRawTimephasedRemainingRegularWork(), ranges);
   }

   /**
    * Retrieve timephased material utilization (actual+remaining) for this resource assignment for the supplied time ranges.
    *
    * @param ranges time ranges over which timephased material utilization is summarized
    * @return list of Number instances representing timephased material utilization for the supplied ranges
    */
   public List<Number> getTimephasedMaterial(List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.addTimephasedNumbers(getTimephasedActualMaterial(ranges), getTimephasedRemainingMaterial(ranges));
   }

   /**
    * Retrieve the timephased baseline material utilization for the supplied time ranges.
    * Note that index 0 represents "Baseline", index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @param ranges time ranges over which timephased material utilization is summarized
    * @return list of Number instances representing timephased material utilization for the supplied ranges
    */
   public List<Number> getTimephasedBaselineMaterial(int index, List<LocalDateTimeRange> ranges)
   {
      return TimephasedUtility.segmentMaterial(this, m_parentFile.getBaselineCalendar(), getRawTimephasedBaselineWork(index), ranges);
   }

   /**
    * Retrieves timephased cost using data from the provided functions.
    *
    * @param ranges time ranges over which timephased cost is summarized
    * @param rateIndex index of the cost rate table to use
    * @param timephasedWork function providing timephased work
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedCost(List<LocalDateTimeRange> ranges, int rateIndex, Function<List<LocalDateTimeRange>, List<Duration>> timephasedWork)
   {
      Number[] result = new Number[ranges.size()];

      // If there is no work, return null values
      List<Duration> hours = timephasedWork.apply(ranges);
      if (hours == null || hours.isEmpty())
      {
         return Arrays.asList(result);
      }

      CostRateTable costRateTable = getCostRateTable();
      if (costRateTable == null)
      {
         return Arrays.asList(result);
      }

      List<CostRateTableEntry> rates = costRateTable.getEntriesByRange(new LocalDateTimeRange(ranges.get(0).getStart(), ranges.get(ranges.size() - 1).getEnd()));
      if (rates.isEmpty())
      {
         return Arrays.asList(result);
      }

      // We're assuming that the cost rate table entries are in order
      int costRateTableEntryIndex = 0;
      CostRateTableEntry currentRate = rates.get(costRateTableEntryIndex);

      for (int index = 0; index < ranges.size(); index++)
      {
         Duration work = hours.get(index);
         if (work == null)
         {
            continue;
         }

         if (work.getDuration() == 0.0)
         {
            result[index] = Double.valueOf(0);
         }

         LocalDateTimeRange range = ranges.get(index);
         while (!currentRate.getEndDate().isAfter(range.getStart()) && costRateTableEntryIndex < rates.size())
         {
            currentRate = rates.get(++costRateTableEntryIndex);
         }

         // The cost rate table entries end before our ranges end - just return what we have
         if (costRateTableEntryIndex == rates.size())
         {
            break;
         }

         // Our range doesn't pass the end of this cost rate table entry - we can perform the calculation in one go
         if (!range.getEnd().isAfter(currentRate.getEndDate()))
         {
            Rate rate = getRatePerHour(currentRate.getRate(rateIndex));
            result[index] = Double.valueOf(work.getDuration() * rate.getAmount());
            continue;
         }

         // Multiple rates are in operation over this range.
         double total = 0;
         LocalDateTimeRange subRange = new LocalDateTimeRange(range.getStart(), currentRate.getEndDate());
         while (true)
         {
            work = timephasedWork.apply(Collections.singletonList(subRange)).get(0);
            if (work != null)
            {
               Rate rate = getRatePerHour(currentRate.getRate(rateIndex));
               total += (work.getDuration() * rate.getAmount());
            }

            if (subRange.getEnd().equals(range.getEnd()))
            {
               break;
            }

            currentRate = rates.get(++costRateTableEntryIndex);
            subRange = new LocalDateTimeRange(currentRate.getStartDate(), currentRate.getEndDate().isAfter(range.getEnd()) ? range.getEnd() : currentRate.getEndDate());
         }

         result[index] = Double.valueOf(total);
      }

      return Arrays.asList(result);
   }

   /**
    * Convert a rate into a rate per hour.
    *
    * @param rate rate to convert
    * @return rate per hour
    */
   private Rate getRatePerHour(Rate rate)
   {
      if (rate.getUnits() == TimeUnit.HOURS)
      {
         return rate;
      }

      return Rate.valueOf(Double.valueOf(RateHelper.convertToHours(getEffectiveCalendar(), rate)), TimeUnit.HOURS);
   }

   /**
    * Retrieve timephased remaining cost for a cost resource.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedCostResourceRemainingCost(List<LocalDateTimeRange> ranges)
   {
      switch (getResource().getAccrueAt())
      {
         case START:
         {
            return getTimephasedRemainingCostAccruedAtStart(ranges, this::getCost, this::getRemainingCost);
         }

         case END:
         {
            return getTimephasedRemainingCostAccruedAtEnd(ranges, this::getCost, this::getRemainingCost);
         }

         default:
         {
            return getTimephasedRemainingCostProRata(ranges);
         }
      }
   }

   /**
    * Retrieve timephased actual cost for a cost resource.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedCostResourceActualCost(List<LocalDateTimeRange> ranges)
   {
      switch (getResource().getAccrueAt())
      {
         case START:
         {
            return getTimephasedCostAccruedAtStart(ranges, this::getActualCost, this::getTimephasedActualWork);
         }

         case END:
         {
            return getTimephasedCostAccruedAtEnd(ranges, this::getActualCost, this::getTimephasedActualWork);
         }

         default:
         {
            return getTimephasedActualCostProrata(ranges);
         }
      }
   }

   /**
    * Retrieve the timephased remaining cost accrued at the start of the resource assignment.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param totalCost total cost value
    * @param remainingCost remaining cost value
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedRemainingCostAccruedAtStart(List<LocalDateTimeRange> ranges, Supplier<Number> totalCost, Supplier<Number> remainingCost)
   {
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      Number[] result = new Number[ranges.size()];

      // Find the first range which intersects with the assignment
      int rangeIndex = 0;
      while (rangeIndex < ranges.size() && !ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         ++rangeIndex;
      }

      List<Duration> remainingWork = getTimephasedRemainingWork(ranges, TimeUnit.HOURS);
      if (NumberHelper.getDouble(remainingCost.get()) == 0)
      {
         // The assignment has started, so there will already
         // be actual cost timephased data for the entire cost.
         // Return zero remaining cost for the whole assignment.
         while (rangeIndex < ranges.size() && ranges.get(rangeIndex).intersectsWith(assignmentRange))
         {
            result[rangeIndex] = remainingWork.get(rangeIndex) == null ? null : Double.valueOf(0);
            rangeIndex++;
         }
         return Arrays.asList(result);
      }

      // The resource assignment has not started.
      // The cost is allocated to the first segment of the assignment.
      if (ranges.get(rangeIndex).compareTo(getStart()) == 0)
      {
         result[rangeIndex++] = totalCost.get();
      }

      // The remainder of the assignment has zero cost.
      while (rangeIndex < ranges.size() && ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         result[rangeIndex] = remainingWork.get(rangeIndex) == null ? null : Double.valueOf(0);
         rangeIndex++;
      }

      return Arrays.asList(result);
   }

   /**
    * Retrieve the timephased remaining cost accrued at the end of the resource assignment.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param totalCost total cost value
    * @param remainingCost remaining cost value
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedRemainingCostAccruedAtEnd(List<LocalDateTimeRange> ranges, Supplier<Number> totalCost, Supplier<Number> remainingCost)
   {
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      Number[] result = new Number[ranges.size()];

      // Find the first range which intersects with the assignment
      int rangeIndex = 0;
      while (rangeIndex < ranges.size() && !ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         ++rangeIndex;
      }

      // Fill the ranges covering the assignment with zero cost
      List<Duration> remainingWork = getTimephasedRemainingWork(ranges, TimeUnit.HOURS);
      while (rangeIndex < ranges.size() && ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         result[rangeIndex] = remainingWork.get(rangeIndex) == null ? null : Double.valueOf(0);
         rangeIndex++;
      }

      if (NumberHelper.getDouble(remainingCost.get()) == 0)
      {
         // The assignment has finished, so there will already
         // be actual cost timephased data for the entire cost
         // so we'll just return;
         return Arrays.asList(result);
      }

      // The last intersecting range includes the end of the assignment
      // so we populate it with the cost.
      if (ranges.get(rangeIndex - 1).compareTo(getFinish()) == 0)
      {
         result[rangeIndex - 1] = totalCost.get();
      }

      return Arrays.asList(result);
   }

   /**
    * Retrieve the timephased remaining cost accrued pro rata over the duration of the resource assignment.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedRemainingCostProRata(List<LocalDateTimeRange> ranges)
   {
      Number[] result = new Number[ranges.size()];

      if (NumberHelper.getDouble(getRemainingCost()) == 0)
      {
         return Arrays.asList(result);
      }

      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getResume(), getFinish());

      // Find the first range which intersects with the assignment
      int rangeIndex = 0;
      while (rangeIndex < ranges.size() && !ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         rangeIndex++;
      }

      ProjectCalendar cal = getEffectiveCalendar();
      double workingHours = cal.getWork(getStart(), getFinish(), TimeUnit.HOURS).getDuration();
      double amountPerHour = getCost().doubleValue() / workingHours;

      while (rangeIndex < ranges.size())
      {
         LocalDateTimeRange intersection = assignmentRange.intersection(ranges.get(rangeIndex));
         if (intersection == null)
         {
            break;
         }

         double intersectionHours = cal.getWork(intersection.getStart(), intersection.getEnd(), TimeUnit.HOURS).getDuration();
         if (intersectionHours != 0.0)
         {
            result[rangeIndex] = Double.valueOf(intersectionHours * amountPerHour);
         }
         rangeIndex++;
      }

      return Arrays.asList(result);
   }

   /**
    * Retrieve the timephased actual cost accrued at the start of the resource assignment.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param costSupplier function supplying the actual cost
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedCostAccruedAtStart(List<LocalDateTimeRange> ranges, Supplier<Number> costSupplier, BiFunction<List<LocalDateTimeRange>, TimeUnit, List<Duration>> workSupplier)
   {
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      Number[] result = new Number[ranges.size()];

      // Find the first range which intersects with the assignment
      int rangeIndex = 0;
      while (rangeIndex < ranges.size() && !ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         ++rangeIndex;
      }

      // If our first intersecting range includes the start of the assignment.
      // Assign the actual cost to this range.
      if (ranges.get(rangeIndex).compareTo(getStart()) == 0)
      {
         Number cost = costSupplier.get();
         result[rangeIndex++] = cost == null ? Double.valueOf(0) : cost;
      }

      // The remainder of the ranges which intersect with
      // the assignment have zero cost.
      List<Duration> work = workSupplier.apply(ranges, TimeUnit.HOURS);
      while (rangeIndex < ranges.size() && ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         result[rangeIndex] = work.get(rangeIndex) == null ? null : Double.valueOf(0);
         rangeIndex++;
      }

      return Arrays.asList(result);
   }

   /**
    * Retrieve the timephased actual cost accrued at the end of the resource assignment.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param costSupplier function supplying the actual cost
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedCostAccruedAtEnd(List<LocalDateTimeRange> ranges, Supplier<Number> costSupplier, BiFunction<List<LocalDateTimeRange>, TimeUnit, List<Duration>> workSupplier)
   {
      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getStart(), getFinish());
      Number[] result = new Number[ranges.size()];

      // Find the first range which intersects with the assignment
      int rangeIndex = 0;
      while (rangeIndex < ranges.size() && !ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         ++rangeIndex;
      }

      // The ranges which intersect with
      // the assignment have zero cost.
      List<Duration> work = workSupplier.apply(ranges, TimeUnit.HOURS);
      while (rangeIndex < ranges.size() && ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         boolean firstAssignmentRange = ranges.get(rangeIndex).compareTo(getStart()) == 0;
         result[rangeIndex] = !firstAssignmentRange && work.get(rangeIndex) == null ? null : Double.valueOf(0);
         rangeIndex++;
      }

      // Our last range includes the end of the assignment.
      // Assign the actual cost to this range.
      if (ranges.get(rangeIndex - 1).compareTo(getFinish()) == 0 && work.get(rangeIndex - 1) != null)
      {
         Number cost = costSupplier.get();
         result[rangeIndex - 1] = cost == null ? Double.valueOf(0) : cost;
      }

      return Arrays.asList(result);
   }

   /**
    * Retrieve the timephased actual cost accrued pro rata over the duration of the resource assignment.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @return list of Number instances representing timephased cost for the supplied ranges
    */
   private List<Number> getTimephasedActualCostProrata(List<LocalDateTimeRange> ranges)
   {
      if (NumberHelper.getDouble(getActualCost()) == 0)
      {
         return Arrays.asList(new Number[ranges.size()]);
      }

      LocalDateTimeRange assignmentRange = new LocalDateTimeRange(getActualStart(), getResume());

      // Find the start date for ranges which are aligned with the caller-supplied ranges, and cover the whole assignment
      long minutesPerRange = ranges.get(0).getStart().until(ranges.get(0).getEnd(), ChronoUnit.MINUTES);
      LocalDateTime assignmentStartRange = ranges.get(0).getStart();
      if (ranges.get(0).getStart().isAfter(getStart()))
      {
         while (assignmentStartRange.isAfter(getStart()))
         {
            assignmentStartRange = assignmentStartRange.minusMinutes(minutesPerRange);
         }
      }
      else
      {
         while (assignmentStartRange.plusMinutes(minutesPerRange).isBefore(getStart()))
         {
            assignmentStartRange = assignmentStartRange.plusMinutes(minutesPerRange);
         }
      }

      // Create the ranges
      List<LocalDateTimeRange> assignmentRanges = new ArrayList<>();
      while (true)
      {
         LocalDateTimeRange range = new LocalDateTimeRange(assignmentStartRange, assignmentStartRange.plusMinutes(minutesPerRange));
         assignmentRanges.add(range);
         if (range.compareTo(getFinish()) == 0)
         {
            break;
         }
         assignmentStartRange = range.getEnd();
      }

      // Count the number of ranges which contain calendar working hours
      ProjectCalendar calendar = getEffectiveCalendar();
      long workingRanges = assignmentRanges.stream()
         .mapToDouble(r -> calendar.getWork(r.getStart(), r.getEnd(), TimeUnit.HOURS).getDuration())
         .filter(h -> h != 0.0)
         .count();

      // Determine the pro rata cost per range with working hours
      Double costPerWorkingRange = Double.valueOf(getCost().doubleValue() / workingRanges);

      // Find the first range which intersects with the assignment
      int rangeIndex = 0;
      while (rangeIndex < ranges.size() && !ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         ++rangeIndex;
      }

      Number[] result = new Number[ranges.size()];
      while (rangeIndex < ranges.size() && ranges.get(rangeIndex).intersectsWith(assignmentRange))
      {
         LocalDateTimeRange range = ranges.get(rangeIndex);
         LocalDateTime end = range.getEnd().isAfter(assignmentRange.getEnd()) ? assignmentRange.getEnd() : range.getEnd();
         if (calendar.getWork(range.getStart(), end, TimeUnit.HOURS).getDuration() != 0.0)
         {
            result[rangeIndex] = costPerWorkingRange;
         }
         ++rangeIndex;
      }
      return Arrays.asList(result);
   }

   /**
    * Add remaining cost per use to timephased costs.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param costs timephased costs
    * @return timephased costs including remaining cost per use
    */
   private List<Number> addTimephasedRemainingCostPerUse(List<LocalDateTimeRange> ranges, List<Number> costs)
   {
      // If we have started, the cost per use will
      // be accounted for in the actual costs.
      if (getActualStart() != null)
      {
         return costs;
      }

      Resource resource = getResource();
      if (resource == null)
      {
         return costs;
      }

      double costPerUse = NumberHelper.getDouble(resource.getCostPerUse());
      if (costPerUse == 0.0)
      {
         return costs;
      }

      double units = NumberHelper.getDouble(getUnits());
      if ((getResource() == null || getResource().getType() == ResourceType.WORK) && units != 100.0)
      {
         costPerUse = (costPerUse * units) / 100.0;
      }

      for (int rangeIndex = 0; rangeIndex < ranges.size(); ++rangeIndex)
      {
         if (ranges.get(rangeIndex).compareTo(getStart()) == 0)
         {
            costs.set(rangeIndex, Double.valueOf(NumberHelper.getDouble(costs.get(rangeIndex)) + costPerUse));
            break;
         }
      }

      return costs;
   }

   /**
    * Add actual cost per use to timephased costs.
    *
    * @param ranges time ranges over which timephased work is summarized
    * @param costs timephased costs
    * @return timephased costs including actual cost per use
    */
   private List<Number> addTimephasedActualCostPerUse(List<LocalDateTimeRange> ranges, List<Number> costs)
   {
      // If we not have started, the cost per use will
      // be accounted for in the remaining costs.
      if (getActualStart() == null)
      {
         return costs;
      }

      Resource resource = getResource();
      if (resource == null)
      {
         return costs;
      }

      Number costPerUse = resource.getCostPerUse();
      if (costPerUse == null || costPerUse.doubleValue() == 0.0)
      {
         return costs;
      }

      for (int rangeIndex = 0; rangeIndex < ranges.size(); ++rangeIndex)
      {
         if (ranges.get(rangeIndex).compareTo(getStart()) == 0)
         {
            costs.set(rangeIndex, Double.valueOf(NumberHelper.getDouble(costs.get(rangeIndex)) + costPerUse.doubleValue()));
            break;
         }
      }

      return costs;
   }

   /**
    * Returns raw timephased planned work, generating it as appropriate if none is present.
    *
    * @return raw timephased planned work
    */
   private List<TimephasedWork> getOrCreateRawTimephasedPlannedWork()
   {
      List<TimephasedWork> plannedWork = getRawTimephasedPlannedWork();
      if (!plannedWork.isEmpty())
      {
         return plannedWork;
      }

      Duration work = getPlannedWork();
      if (work == null || work.getDuration() == 0.0)
      {
         return plannedWork;
      }

      ProjectCalendar calendar = getEffectiveCalendar();
      LocalDateTime start = getPlannedStart();
      LocalDateTime finish = getPlannedFinish();
      double workingHours = calendar.getWork(start, finish, TimeUnit.HOURS).getDuration();
      double plannedHours = work.convertUnits(TimeUnit.HOURS, calendar).getDuration();

      TimephasedWork item = new TimephasedWork();
      item.setStart(start);
      item.setFinish(finish);
      item.setTotalAmount(work);
      item.setAmountPerHour(Duration.getInstance(plannedHours/ workingHours, TimeUnit.HOURS));

      return Collections.singletonList(item);
   }

   /**
    * Returns raw timephased actual work, generating it as appropriate if none is present.
    *
    * @param timephasedWork raw timephased actual work
    * @param workSupplier actual work supplier
    * @return raw timephased actual work
    */
   private List<TimephasedWork> getOrCreateRawTimephasedActualWork(List<TimephasedWork> timephasedWork, Supplier<Duration> workSupplier)
   {
      if (!timephasedWork.isEmpty() || getActualStart() == null)
      {
         return timephasedWork;
      }

      Duration work = workSupplier.get();
      if (work == null || work.getDuration() == 0.0)
      {
         return timephasedWork;
      }

      ProjectCalendar calendar = getEffectiveCalendar();
      LocalDateTime start = getActualStart();
      LocalDateTime finish = getActualFinish();
      if (finish == null)
      {
         finish = calendar.getDate(start, work);
      }

      double workingHours = calendar.getWork(start, finish, TimeUnit.HOURS).getDuration();
      double actualHours = work.convertUnits(TimeUnit.HOURS, calendar).getDuration();

      TimephasedWork item = new TimephasedWork();
      item.setStart(start);
      item.setFinish(finish);
      item.setTotalAmount(work);
      item.setAmountPerHour(Duration.getInstance(actualHours / workingHours, TimeUnit.HOURS));

      return Collections.singletonList(item);
   }

   /**
    * Returns raw timephased remaining work, generating it as appropriate if none is present.
    *
    * @param timephasedWork raw timephased remaining work
    * @param workSupplier remaining work supplier
    * @return raw timephased remaining work
    */
   private List<TimephasedWork> getOrCreateRawTimephasedRemainingWork(List<TimephasedWork> timephasedWork, Supplier<Duration> workSupplier)
   {
      if (!timephasedWork.isEmpty() || getActualFinish() != null)
      {
         return timephasedWork;
      }

      Duration work = workSupplier.get();
      if (work == null || work.getDuration() == 0.0)
      {
         return timephasedWork;
      }

      ProjectCalendar calendar = getEffectiveCalendar();
      LocalDateTime start = getActualStart() == null ? getStart() : getRemainingEarlyStart();
      LocalDateTime finish = getFinish();

      double workingHours = calendar.getWork(start, finish, TimeUnit.HOURS).getDuration();
      double remainingHours = work.convertUnits(TimeUnit.HOURS, calendar).getDuration();

      TimephasedWork item = new TimephasedWork();
      item.setStart(start);
      item.setFinish(finish);
      item.setTotalAmount(work);
      item.setAmountPerHour(Duration.getInstance(remainingHours / workingHours, TimeUnit.HOURS));

      return Collections.singletonList(item);
   }

   /**
    * Retrieve the value of a field using its alias.
    *
    * @param alias field alias
    * @return field value
    */
   public Object getFieldByAlias(String alias)
   {
      return get(m_parentFile.getResourceAssignments().getFieldTypeByAlias(alias));
   }

   /**
    * Set the value of a field using its alias.
    *
    * @param alias field alias
    * @param value field value
    */
   public void setFieldByAlias(String alias, Object value)
   {
      set(m_parentFile.getResourceAssignments().getFieldTypeByAlias(alias), value);
   }

   /**
    * Maps a field index to an AssignmentField instance.
    *
    * @param fields array of fields used as the basis for the mapping.
    * @param index required field index
    * @return AssignmentField instance
    */
   private AssignmentField selectField(AssignmentField[] fields, int index)
   {
      if (index < 1 || index > fields.length)
      {
         throw new IllegalArgumentException(index + " is not a valid field index");
      }
      return (fields[index - 1]);
   }

   @Override public String toString()
   {
      String taskName = getTask() == null ? "null" : getTask().getName();
      String resourceName = getResource() == null ? "Unassigned" : getResource().getName();
      return ("[Resource Assignment task=" + taskName + " resource=" + resourceName + " start=" + getStart() + " finish=" + getFinish() + " work=" + getWork() + " units=" + getUnits() + " workContour=" + getWorkContour() + "]");
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param field task field
    * @param value attribute value
    */
   private void set(FieldType field, boolean value)
   {
      set(field, (value ? Boolean.TRUE : Boolean.FALSE));
   }

   /**
    * Clear any cached calculated values which will be affected by this change.
    *
    * @param field modified field
    */
   @Override protected void handleFieldChange(FieldType field, Object oldValue, Object newValue)
   {
      if (field == AssignmentField.UNIQUE_ID)
      {
         m_parentFile.getResourceAssignments().updateUniqueID(this, (Integer) oldValue, (Integer) newValue);
         return;
      }

      clearDependentFields(DEPENDENCY_MAP, field);
   }

   @Override boolean getAlwaysCalculatedField(FieldType field)
   {
      return ALWAYS_CALCULATED_FIELDS.contains(field);
   }

   @Override Function<ResourceAssignment, Object> getCalculationMethod(FieldType field)
   {
      return CALCULATED_FIELD_MAP.get(field);
   }

   private Number calculateOvertimeCost()
   {
      Double cost = null;
      Number actual = getActualOvertimeCost();
      Number remaining = getRemainingOvertimeCost();
      if (actual != null && remaining != null)
      {
         cost = NumberHelper.getDouble(actual.doubleValue() + remaining.doubleValue());
      }
      return cost;
   }

   private Double calculateSV()
   {
      Double variance = null;
      Number bcwp = getBCWP();
      Number bcws = getBCWS();
      if (bcwp != null && bcws != null)
      {
         variance = NumberHelper.getDouble(bcwp.doubleValue() - bcws.doubleValue());
      }
      return variance;
   }

   private Double calculateCV()
   {
      return Double.valueOf(NumberHelper.getDouble(getBCWP()) - NumberHelper.getDouble(getACWP()));
   }

   private Double calculateCostVariance()
   {
      Double variance = null;
      Number cost = getCost();
      Number baselineCost = getBaselineCost();
      if (cost != null && baselineCost != null)
      {
         variance = NumberHelper.getDouble(cost.doubleValue() - baselineCost.doubleValue());
      }
      return variance;
   }

   private Double calculatePercentWorkComplete()
   {
      Double pct = null;
      Duration actualWork = getActualWork();
      Duration work = getWork();
      if (actualWork != null && work != null && work.getDuration() != 0)
      {
         pct = Double.valueOf((actualWork.getDuration() * 100) / work.convertUnits(actualWork.getUnits(), m_parentFile.getProjectProperties()).getDuration());
      }
      return pct;
   }

   private Duration calculateWorkVariance()
   {
      Duration variance = null;
      Duration work = getWork();
      Duration baselineWork = getBaselineWork();
      if (work != null && baselineWork != null)
      {
         variance = Duration.getInstance(work.getDuration() - baselineWork.convertUnits(work.getUnits(), m_parentFile.getProjectProperties()).getDuration(), work.getUnits());
         set(AssignmentField.WORK_VARIANCE, variance);
      }
      return variance;
   }

   private Duration calculateStartVariance()
   {
      TimeUnit format = m_parentFile.getProjectProperties().getDefaultDurationUnits();
      return LocalDateTimeHelper.getVariance(getEffectiveCalendar(), getBaselineStart(), getStart(), format);
   }

   private Duration calculateFinishVariance()
   {
      TimeUnit format = m_parentFile.getProjectProperties().getDefaultDurationUnits();
      return LocalDateTimeHelper.getVariance(getEffectiveCalendar(), getBaselineFinish(), getFinish(), format);
   }

   private LocalDateTime calculateStart()
   {
      LocalDateTime result = (LocalDateTime) getCachedValue(AssignmentField.START);
      if (result == null)
      {
         Task task = getTask();
         if (task != null)
         {
            result = task.getStart();
         }
      }
      return result;
   }

   private LocalDateTime calculateFinish()
   {
      LocalDateTime result = (LocalDateTime) getCachedValue(AssignmentField.FINISH);
      if (result == null)
      {
         Task task = getTask();
         if (task != null)
         {
            result = task.getFinish();
         }
      }
      return result;
   }

   private Number calculateRemainingAssignmentUnits()
   {
      // Default to the planned units if a remaining units value is not available
      return getUnits();
   }

   private List<TimephasedWork> calculateTimephasedOvertimeWork()
   {
      Duration totalRemainingWork = getRemainingWork();
      Duration remainingOvertimeWork = getRemainingOvertimeWork();

      if (totalRemainingWork == null || remainingOvertimeWork == null)
      {
         return Collections.emptyList();
      }

      double totalRemainingMinutes = totalRemainingWork.convertUnits(TimeUnit.MINUTES, getEffectiveCalendar()).getDuration();
      double remainingOvertimeMinutes = remainingOvertimeWork.convertUnits(TimeUnit.MINUTES, getEffectiveCalendar()).getDuration();
      double remainingRegularMinutes = totalRemainingMinutes - remainingOvertimeMinutes;

      if (remainingRegularMinutes == 0 || remainingOvertimeMinutes == 0)
      {
         return Collections.emptyList();
      }

      double factor = remainingOvertimeMinutes / remainingRegularMinutes;
      return getRawTimephasedRemainingRegularWork().stream().map(i -> new TimephasedWork(i, factor)).collect(Collectors.toList());
   }

   private Number calculateRegularCost(Supplier<Number> totalCostSupplier, Supplier<Number> overtimeCostSupplier)
   {
      Number totalCost = totalCostSupplier.get();
      Number overtimeCost = overtimeCostSupplier.get();
      if (totalCost == null && overtimeCost == null)
      {
         return null;
      }

      return Double.valueOf(NumberHelper.getDouble(totalCost) - NumberHelper.getDouble(overtimeCost));
   }

   private Duration calculateRegularWork(Supplier<Duration> totalWorkSupplier, Supplier<Duration> overtimeWorkSupplier)
   {
      Duration totalWork = totalWorkSupplier.get();
      Duration overtimeWork = overtimeWorkSupplier.get();
      if (totalWork == null && overtimeWork == null)
      {
         return null;
      }

      TimeUnit units = totalWork == null ? overtimeWork.getUnits() : totalWork.getUnits();
      double total = totalWork == null ? 0 : totalWork.convertUnits(units, getEffectiveCalendar()).getDuration();
      double overtime = overtimeWork == null ? 0 : overtimeWork.convertUnits(units, getEffectiveCalendar()).getDuration();

      return Duration.getInstance(total-overtime, units);
   }

   private Number calculateMaterial(Supplier<Duration> fn)
   {
      if (getResource() == null || getResource().getType() != ResourceType.MATERIAL)
      {
         return null;
      }

      Duration work = fn.get();
      if (work == null)
      {
         return null;
      }

      return Double.valueOf(work.getDuration());
   }

   /**
    * Supply a default value for the rate index.
    *
    * @return rate index default value
    */
   private Integer defaultRateIndex()
   {
      return Integer.valueOf(0);
   }

   /**
    * Supply a default value for the rate source.
    *
    * @return rate source default value
    */
   private RateSource defaultRateSource()
   {
      return RateSource.RESOURCE;
   }

   /**
    * Supply a default value for the calculate costs from units flag.
    *
    * @return calculate costs from units flag default value
    */
   private Boolean defaultCalculateCostsFromUnits()
   {
      return Boolean.TRUE;
   }

   /**
    * Supply a default value for the resource assignment code values.
    *
    * @return default value for resource assignment code values
    */
   private Map<ResourceAssignmentCode, ResourceAssignmentCodeValue> defaultResourceAssignmentCodeValues()
   {
      return new HashMap<>();
   }

   private List<TimephasedWork> defaultTimephasedWork()
   {
      return new ArrayList<>();
   }

   private List<TimephasedCost> defaultTimephasedCost()
   {
      return new ArrayList<>();
   }

   private final ProjectFile m_parentFile;

   /**
    *  Child record for Workgroup fields.
    */
   private ResourceAssignmentWorkgroupFields m_workgroup;

   /**
    * Default units value: 100%.
    */
   public static final Double DEFAULT_UNITS = Double.valueOf(100);

   private interface TimephasedDurationFunction
   {
      List<Duration> apply(ResourceAssignment assignment, List<LocalDateTimeRange> ranges, TimeUnit units);
   }

   private interface TimephasedNumericFunction
   {
      List<Number> apply(ResourceAssignment assignment, List<LocalDateTimeRange> ranges);
   }

   private static final Map<FieldType, TimephasedDurationFunction> TIMEPHASED_WORK_FUNCTIONS = new HashMap<>();
   static
   {
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.PLANNED_WORK, ResourceAssignment::getTimephasedPlannedWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.ACTUAL_REGULAR_WORK, ResourceAssignment::getTimephasedActualRegularWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.ACTUAL_OVERTIME_WORK, ResourceAssignment::getTimephasedActualOvertimeWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.ACTUAL_WORK, ResourceAssignment::getTimephasedActualWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.REMAINING_REGULAR_WORK, ResourceAssignment::getTimephasedRemainingRegularWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.REMAINING_OVERTIME_WORK, ResourceAssignment::getTimephasedRemainingOvertimeWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.REMAINING_WORK, ResourceAssignment::getTimephasedRemainingWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.WORK, ResourceAssignment::getTimephasedWork);
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE_WORK, (a, r, t) -> a.getTimephasedBaselineWork(0, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE1_WORK,(a, r, t) -> a.getTimephasedBaselineWork(1, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE2_WORK,(a, r, t) -> a.getTimephasedBaselineWork(2, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE3_WORK,(a, r, t) -> a.getTimephasedBaselineWork(3, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE4_WORK,(a, r, t) -> a.getTimephasedBaselineWork(4, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE5_WORK,(a, r, t) -> a.getTimephasedBaselineWork(5, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE6_WORK,(a, r, t) -> a.getTimephasedBaselineWork(6, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE7_WORK,(a, r, t) -> a.getTimephasedBaselineWork(7, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE8_WORK,(a, r, t) -> a.getTimephasedBaselineWork(8, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE9_WORK,(a, r, t) -> a.getTimephasedBaselineWork(9, r, t));
      TIMEPHASED_WORK_FUNCTIONS.put(AssignmentField.BASELINE10_WORK, (a, r, t) -> a.getTimephasedBaselineWork(10, r, t));
   }

   private static final Map<FieldType, TimephasedNumericFunction> TIMEPHASED_NUMERIC_FUNCTIONS = new HashMap<>();
   static
   {
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.REMAINING_REGULAR_COST, ResourceAssignment::getTimephasedRemainingRegularCost);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.REMAINING_OVERTIME_COST, ResourceAssignment::getTimephasedRemainingOvertimeCost);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.REMAINING_COST, ResourceAssignment::getTimephasedRemainingCost);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.ACTUAL_REGULAR_COST, ResourceAssignment::getTimephasedActualRegularCost);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.ACTUAL_OVERTIME_COST, ResourceAssignment::getTimephasedActualOvertimeCost);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.ACTUAL_COST, ResourceAssignment::getTimephasedActualCost);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.COST, ResourceAssignment::getTimephasedCost);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.PLANNED_MATERIAL, ResourceAssignment::getTimephasedPlannedMaterial);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.ACTUAL_MATERIAL, ResourceAssignment::getTimephasedActualMaterial);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.REMAINING_MATERIAL, ResourceAssignment::getTimephasedRemainingMaterial);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.MATERIAL, ResourceAssignment::getTimephasedMaterial);
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE_COST, (a, r) -> a.getTimephasedBaselineCost(0, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE1_COST, (a, r) -> a.getTimephasedBaselineCost(1, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE2_COST, (a, r) -> a.getTimephasedBaselineCost(2, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE3_COST, (a, r) -> a.getTimephasedBaselineCost(3, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE4_COST, (a, r) -> a.getTimephasedBaselineCost(4, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE5_COST, (a, r) -> a.getTimephasedBaselineCost(5, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE6_COST, (a, r) -> a.getTimephasedBaselineCost(6, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE7_COST, (a, r) -> a.getTimephasedBaselineCost(7, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE8_COST, (a, r) -> a.getTimephasedBaselineCost(8, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE9_COST, (a, r) -> a.getTimephasedBaselineCost(9, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE10_COST, (a, r) -> a.getTimephasedBaselineCost(10, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(0, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE1_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(1, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE2_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(2, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE3_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(3, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE4_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(4, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE5_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(5, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE6_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(6, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE7_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(7, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE8_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(8, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE9_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(9, r));
      TIMEPHASED_NUMERIC_FUNCTIONS.put(AssignmentField.BASELINE10_MATERIAL, (a, r) -> a.getTimephasedBaselineMaterial(10, r));
   }

   private static final Set<FieldType> ALWAYS_CALCULATED_FIELDS = new HashSet<>(Arrays.asList(AssignmentField.START, AssignmentField.FINISH));

   private static final Map<FieldType, Function<ResourceAssignment, Object>> CALCULATED_FIELD_MAP = new HashMap<>();
   static
   {
      CALCULATED_FIELD_MAP.put(AssignmentField.OVERTIME_COST, ResourceAssignment::calculateOvertimeCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.COST_VARIANCE, ResourceAssignment::calculateCostVariance);
      CALCULATED_FIELD_MAP.put(AssignmentField.CV, ResourceAssignment::calculateCV);
      CALCULATED_FIELD_MAP.put(AssignmentField.SV, ResourceAssignment::calculateSV);
      CALCULATED_FIELD_MAP.put(AssignmentField.START_VARIANCE, ResourceAssignment::calculateStartVariance);
      CALCULATED_FIELD_MAP.put(AssignmentField.FINISH_VARIANCE, ResourceAssignment::calculateFinishVariance);
      CALCULATED_FIELD_MAP.put(AssignmentField.PERCENT_WORK_COMPLETE, ResourceAssignment::calculatePercentWorkComplete);
      CALCULATED_FIELD_MAP.put(AssignmentField.WORK_VARIANCE, ResourceAssignment::calculateWorkVariance);
      CALCULATED_FIELD_MAP.put(AssignmentField.START, ResourceAssignment::calculateStart);
      CALCULATED_FIELD_MAP.put(AssignmentField.FINISH, ResourceAssignment::calculateFinish);
      CALCULATED_FIELD_MAP.put(AssignmentField.REMAINING_ASSIGNMENT_UNITS, ResourceAssignment::calculateRemainingAssignmentUnits);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_REMAINING_OVERTIME_WORK, ResourceAssignment::calculateTimephasedOvertimeWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.ACTUAL_REGULAR_COST, a -> a.calculateRegularCost(a::getActualCost, a::getActualOvertimeCost));
      CALCULATED_FIELD_MAP.put(AssignmentField.REMAINING_REGULAR_COST, a -> a.calculateRegularCost(a::getRemainingCost, a::getRemainingOvertimeCost));
      CALCULATED_FIELD_MAP.put(AssignmentField.ACTUAL_REGULAR_WORK, a -> a.calculateRegularWork(a::getActualWork, a::getActualOvertimeWork));
      CALCULATED_FIELD_MAP.put(AssignmentField.REMAINING_REGULAR_WORK, a ->  a.calculateRegularWork(a::getRemainingWork, a::getRemainingOvertimeWork));
      CALCULATED_FIELD_MAP.put(AssignmentField.PLANNED_MATERIAL, a -> a.calculateMaterial(a::getPlannedWork));
      CALCULATED_FIELD_MAP.put(AssignmentField.ACTUAL_MATERIAL, a -> a.calculateMaterial(a::getActualWork));
      CALCULATED_FIELD_MAP.put(AssignmentField.REMAINING_MATERIAL, a -> a.calculateMaterial(a::getRemainingWork));
      CALCULATED_FIELD_MAP.put(AssignmentField.MATERIAL, a -> a.calculateMaterial(a::getWork));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE_MATERIAL, a -> a.calculateMaterial(a::getBaselineWork));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE1_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(1)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE2_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(2)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE3_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(3)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE4_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(4)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE5_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(5)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE6_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(6)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE7_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(7)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE8_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(8)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE9_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(9)));
      CALCULATED_FIELD_MAP.put(AssignmentField.BASELINE10_MATERIAL, a -> a.calculateMaterial(()-> a.getBaselineWork(10)));
      CALCULATED_FIELD_MAP.put(AssignmentField.RATE_INDEX, ResourceAssignment::defaultRateIndex);
      CALCULATED_FIELD_MAP.put(AssignmentField.RATE_SOURCE, ResourceAssignment::defaultRateSource);
      CALCULATED_FIELD_MAP.put(AssignmentField.CALCULATE_COSTS_FROM_UNITS, ResourceAssignment::defaultCalculateCostsFromUnits);
      CALCULATED_FIELD_MAP.put(AssignmentField.RESOURCE_ASSIGNMENT_CODE_VALUES, ResourceAssignment::defaultResourceAssignmentCodeValues);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_PLANNED_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_ACTUAL_REGULAR_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_REMAINING_REGULAR_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_ACTUAL_OVERTIME_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE1_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE2_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE3_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE4_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE5_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE6_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE7_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE8_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE9_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE10_WORK, ResourceAssignment::defaultTimephasedWork);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE1_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE2_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE3_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE4_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE5_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE6_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE7_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE8_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE9_COST, ResourceAssignment::defaultTimephasedCost);
      CALCULATED_FIELD_MAP.put(AssignmentField.RAW_TIMEPHASED_BASELINE10_COST, ResourceAssignment::defaultTimephasedCost);
   }

   private static final Map<FieldType, List<FieldType>> DEPENDENCY_MAP = new HashMap<>();
   static
   {
      FieldContainerDependencies<FieldType> dependencies = new FieldContainerDependencies<>(DEPENDENCY_MAP);
      dependencies.calculatedField(AssignmentField.OVERTIME_COST).dependsOn(AssignmentField.ACTUAL_OVERTIME_COST, AssignmentField.REMAINING_OVERTIME_COST);
      dependencies.calculatedField(AssignmentField.COST_VARIANCE).dependsOn(AssignmentField.COST, AssignmentField.BASELINE_COST);
      dependencies.calculatedField(AssignmentField.CV).dependsOn(AssignmentField.BCWP, AssignmentField.ACWP);
      dependencies.calculatedField(AssignmentField.SV).dependsOn(AssignmentField.BCWP, AssignmentField.BCWS);
      dependencies.calculatedField(AssignmentField.START_VARIANCE).dependsOn(AssignmentField.START, AssignmentField.BASELINE_START);
      dependencies.calculatedField(AssignmentField.FINISH_VARIANCE).dependsOn(AssignmentField.FINISH, AssignmentField.BASELINE_FINISH);
      dependencies.calculatedField(AssignmentField.PERCENT_WORK_COMPLETE).dependsOn(AssignmentField.ACTUAL_WORK, AssignmentField.WORK);
      dependencies.calculatedField(AssignmentField.WORK_VARIANCE).dependsOn(AssignmentField.WORK, AssignmentField.BASELINE_WORK);
      dependencies.calculatedField(AssignmentField.REMAINING_REGULAR_COST).dependsOn(AssignmentField.REMAINING_COST, AssignmentField.REMAINING_OVERTIME_COST);
      dependencies.calculatedField(AssignmentField.ACTUAL_REGULAR_COST).dependsOn(AssignmentField.ACTUAL_COST, AssignmentField.ACTUAL_OVERTIME_COST);
      dependencies.calculatedField(AssignmentField.REMAINING_REGULAR_WORK).dependsOn(AssignmentField.REMAINING_WORK, AssignmentField.REMAINING_OVERTIME_WORK);
      dependencies.calculatedField(AssignmentField.ACTUAL_REGULAR_WORK).dependsOn(AssignmentField.ACTUAL_WORK, AssignmentField.ACTUAL_OVERTIME_WORK);
      dependencies.calculatedField(AssignmentField.ACTUAL_MATERIAL).dependsOn(AssignmentField.ACTUAL_WORK);
      dependencies.calculatedField(AssignmentField.REMAINING_MATERIAL).dependsOn(AssignmentField.REMAINING_WORK);
      dependencies.calculatedField(AssignmentField.MATERIAL).dependsOn(AssignmentField.WORK);
      dependencies.calculatedField(AssignmentField.BASELINE_MATERIAL).dependsOn(AssignmentField.BASELINE_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE1_MATERIAL).dependsOn(AssignmentField.BASELINE1_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE2_MATERIAL).dependsOn(AssignmentField.BASELINE2_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE3_MATERIAL).dependsOn(AssignmentField.BASELINE3_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE4_MATERIAL).dependsOn(AssignmentField.BASELINE4_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE5_MATERIAL).dependsOn(AssignmentField.BASELINE5_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE6_MATERIAL).dependsOn(AssignmentField.BASELINE6_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE7_MATERIAL).dependsOn(AssignmentField.BASELINE7_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE8_MATERIAL).dependsOn(AssignmentField.BASELINE8_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE9_MATERIAL).dependsOn(AssignmentField.BASELINE9_WORK);
      dependencies.calculatedField(AssignmentField.BASELINE10_MATERIAL).dependsOn(AssignmentField.BASELINE10_WORK);
   }
}
