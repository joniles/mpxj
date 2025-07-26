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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.mpxj.common.AssignmentFieldLists;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.CombinedCalendar;
import org.mpxj.common.DefaultTimephasedCostContainer;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.LocalTimeHelper;
import org.mpxj.common.NumberHelper;

/**
 * This class represents a resource assignment record from an MPX file.
 */
public final class ResourceAssignment extends AbstractFieldContainer<ResourceAssignment> implements ProjectEntityWithMutableUniqueID, TimePeriodEntity
{
   /**
    * Constructor.
    *
    * @param file The parent file to which this record belongs.
    * @param task The task to which this assignment is being made
    */
   public ResourceAssignment(ProjectFile file, Task task)
   {
      super(file);

      if (file.getProjectConfig().getAutoAssignmentUniqueID())
      {
         setUniqueID(file.getUniqueIdObjectSequence(ResourceAssignment.class).getNext());
      }

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
      getParentFile().getResourceAssignments().remove(this);
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
      return getParentFile().getResourceByUniqueID((Integer) get(AssignmentField.ROLE_UNIQUE_ID));
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
    * Retrieves the timephased breakdown of the planned work for this
    * resource assignment.
    *
    * @return timephased planned work
    */
   public List<TimephasedWork> getTimephasedPlannedWork()
   {
      return m_timephasedPlannedWork == null ? null : m_timephasedPlannedWork.getData();
   }

   /**
    * Sets the timephased breakdown of the planned work for this
    * resource assignment.
    *
    * @param data timephased data
    */
   public void setTimephasedPlannedWork(TimephasedWorkContainer data)
   {
      m_timephasedPlannedWork = data;
   }

   /**
    * Sets the timephased breakdown of the planned work for this
    * resource assignment.
    *
    * @param data timephased data
    */
   public void setTimephasedPlannedWork(List<TimephasedWork> data)
   {
      m_timephasedPlannedWork = createTimephasedWorkContainer(data);
   }

   /**
    * Retrieves the timephased breakdown of the completed work for this
    * resource assignment.
    *
    * @return timephased completed work
    */
   public List<TimephasedWork> getTimephasedActualWork()
   {
      return m_timephasedActualWork == null ? null : m_timephasedActualWork.getData();
   }

   /**
    * Sets the timephased breakdown of the completed work for this
    * resource assignment.
    *
    * @param data timephased data
    */
   public void setTimephasedActualWork(TimephasedWorkContainer data)
   {
      m_timephasedActualWork = data;
   }

   /**
    * Sets the timephased breakdown of the completed work for this
    * resource assignment.
    *
    * @param data timephased data
    */
   public void setTimephasedActualWork(List<TimephasedWork> data)
   {
      m_timephasedActualWork = createTimephasedWorkContainer(data);
   }

   /**
    * Retrieves the timephased breakdown of the planned work for this
    * resource assignment.
    *
    * @return timephased planned work
    */
   public List<TimephasedWork> getTimephasedWork()
   {
      return m_timephasedWork == null ? null : m_timephasedWork.getData();
   }

   /**
    * Sets the timephased breakdown of the planned work for this
    * resource assignment.
    *
    * @param data timephased data
    */
   public void setTimephasedWork(TimephasedWorkContainer data)
   {
      m_timephasedWork = data;
   }

   /**
    * Sets the timephased breakdown of the planned work for this
    * resource assignment.
    *
    * @param data timephased data
    */
   public void setTimephasedWork(List<TimephasedWork> data)
   {
      m_timephasedWork = createTimephasedWorkContainer(data);
   }

   /**
    * Retrieves the timephased breakdown of the planned overtime work for this
    * resource assignment.
    *
    * @return timephased planned work
    */
   public List<TimephasedWork> getTimephasedOvertimeWork()
   {
      if (m_timephasedOvertimeWork == null && m_timephasedWork != null && getOvertimeWork() != null)
      {
         double perDayFactor = getRemainingOvertimeWork().getDuration() / (getRemainingWork().getDuration() - getRemainingOvertimeWork().getDuration());
         double totalFactor = getRemainingOvertimeWork().getDuration() / getRemainingWork().getDuration();

         perDayFactor = Double.isNaN(perDayFactor) ? 0 : perDayFactor;
         totalFactor = Double.isNaN(totalFactor) ? 0 : totalFactor;

         m_timephasedOvertimeWork = m_timephasedWork.applyFactor(perDayFactor, totalFactor);
      }
      return m_timephasedOvertimeWork == null ? null : m_timephasedOvertimeWork.getData();
   }

   /**
    * Sets the timephased breakdown of the actual overtime work
    * for this assignment.
    *
    * @param data timephased work
    */
   public void setTimephasedActualOvertimeWork(TimephasedWorkContainer data)
   {
      m_timephasedActualOvertimeWork = data;
   }

   /**
    * Sets the timephased breakdown of the actual overtime work
    * for this assignment.
    *
    * @param data timephased work
    */
   public void setTimephasedActualOvertimeWork(List<TimephasedWork> data)
   {
      m_timephasedActualOvertimeWork = createTimephasedWorkContainer(data);
   }

   /**
    * Retrieves the timephased breakdown of the actual overtime work for this
    * resource assignment.
    *
    * @return timephased planned work
    */
   public List<TimephasedWork> getTimephasedActualOvertimeWork()
   {
      return m_timephasedActualOvertimeWork == null ? null : m_timephasedActualOvertimeWork.getData();
   }

   /**
    * Retrieves the timephased breakdown of cost.
    *
    * @return timephased cost
    */
   public List<TimephasedCost> getTimephasedCost()
   {
      if (m_timephasedCost == null)
      {
         Resource r = getResource();
         ResourceType type = r != null ? r.getType() : ResourceType.WORK;

         //for Work and Material resources, we will calculate in the normal way
         if (type != ResourceType.COST)
         {
            if (m_timephasedWork != null && m_timephasedWork.hasData())
            {
               if (hasMultipleCostRates())
               {
                  m_timephasedCost = getTimephasedCostMultipleRates(getTimephasedWork(), getTimephasedOvertimeWork());
               }
               else
               {
                  m_timephasedCost = getTimephasedCostSingleRate(getTimephasedWork(), getTimephasedOvertimeWork());
               }
            }
         }
         else
         {
            m_timephasedCost = getTimephasedCostFixedAmount();
         }

      }
      return m_timephasedCost == null ? null : m_timephasedCost.getData();
   }

   /**
    * Retrieves the timephased breakdown of actual cost.
    *
    * @return timephased actual cost
    */
   public List<TimephasedCost> getTimephasedActualCost()
   {
      if (m_timephasedActualCost == null)
      {
         Resource r = getResource();
         ResourceType type = r != null ? r.getType() : ResourceType.WORK;

         //for Work and Material resources, we will calculate in the normal way
         if (type != ResourceType.COST)
         {
            if (m_timephasedActualWork != null && m_timephasedActualWork.hasData())
            {
               if (hasMultipleCostRates())
               {
                  m_timephasedActualCost = getTimephasedCostMultipleRates(getTimephasedActualWork(), getTimephasedActualOvertimeWork());
               }
               else
               {
                  m_timephasedActualCost = getTimephasedCostSingleRate(getTimephasedActualWork(), getTimephasedActualOvertimeWork());
               }
            }
         }
         else
         {
            m_timephasedActualCost = getTimephasedActualCostFixedAmount();
         }

      }

      return m_timephasedActualCost == null ? null : m_timephasedActualCost.getData();
   }

   /**
    * Generates timephased costs from timephased work where a single cost rate
    * applies to the whole assignment.
    *
    * @param standardWorkList timephased work
    * @param overtimeWorkList timephased work
    * @return timephased cost
    */
   private TimephasedCostContainer getTimephasedCostSingleRate(List<TimephasedWork> standardWorkList, List<TimephasedWork> overtimeWorkList)
   {
      //just return an empty list if there is no timephased work passed in
      if (standardWorkList == null)
      {
         return new DefaultTimephasedCostContainer(this, null, Collections.emptyList(), false);
      }

      List<TimephasedCost> result = new ArrayList<>();

      //takes care of the situation where there is no timephased overtime work
      Iterator<TimephasedWork> overtimeIterator = overtimeWorkList == null ? Collections.emptyIterator() : overtimeWorkList.iterator();

      for (TimephasedWork standardWork : standardWorkList)
      {
         CostRateTableEntry rate = getCostRateTableEntry(standardWork.getStart());
         double standardRateValue = rate.getStandardRate().getAmount();
         TimeUnit standardRateUnits = rate.getStandardRate().getUnits();
         double overtimeRateValue = 0;
         TimeUnit overtimeRateUnits = standardRateUnits;

         if (rate.getOvertimeRate() != null)
         {
            overtimeRateValue = rate.getOvertimeRate().getAmount();
            overtimeRateUnits = rate.getOvertimeRate().getUnits();
         }

         TimephasedWork overtimeWork = overtimeIterator.hasNext() ? overtimeIterator.next() : null;

         Duration standardWorkPerDay = standardWork.getAmountPerDay();
         if (standardWorkPerDay.getUnits() != standardRateUnits)
         {
            standardWorkPerDay = standardWorkPerDay.convertUnits(standardRateUnits, getParentFile().getProjectProperties());
         }

         Duration totalStandardWork = standardWork.getTotalAmount();
         if (totalStandardWork.getUnits() != standardRateUnits)
         {
            totalStandardWork = totalStandardWork.convertUnits(standardRateUnits, getParentFile().getProjectProperties());
         }

         Duration overtimeWorkPerDay;
         Duration totalOvertimeWork;

         if (overtimeWork == null || overtimeWork.getTotalAmount().getDuration() == 0)
         {
            overtimeWorkPerDay = Duration.getInstance(0, standardWorkPerDay.getUnits());
            totalOvertimeWork = Duration.getInstance(0, standardWorkPerDay.getUnits());
         }
         else
         {
            overtimeWorkPerDay = overtimeWork.getAmountPerDay();
            if (overtimeWorkPerDay.getUnits() != overtimeRateUnits)
            {
               overtimeWorkPerDay = overtimeWorkPerDay.convertUnits(overtimeRateUnits, getParentFile().getProjectProperties());
            }

            totalOvertimeWork = overtimeWork.getTotalAmount();
            if (totalOvertimeWork.getUnits() != overtimeRateUnits)
            {
               totalOvertimeWork = totalOvertimeWork.convertUnits(overtimeRateUnits, getParentFile().getProjectProperties());
            }
         }

         double costPerDay = (standardWorkPerDay.getDuration() * standardRateValue) + (overtimeWorkPerDay.getDuration() * overtimeRateValue);
         double totalCost = (totalStandardWork.getDuration() * standardRateValue) + (totalOvertimeWork.getDuration() * overtimeRateValue);

         //if the overtime work does not span the same number of days as the work,
         //then we have to split this into two TimephasedCost values
         if (overtimeWork == null || (overtimeWork.getFinish().equals(standardWork.getFinish())))
         {
            //normal way
            TimephasedCost cost = new TimephasedCost();
            cost.setStart(standardWork.getStart());
            cost.setFinish(standardWork.getFinish());
            cost.setModified(standardWork.getModified());
            cost.setAmountPerDay(Double.valueOf(costPerDay));
            cost.setTotalAmount(Double.valueOf(totalCost));
            result.add(cost);

         }
         else
         {
            //prorated way
            result.addAll(splitCostProrated(getEffectiveCalendar(), totalCost, costPerDay, standardWork.getStart()));
         }

      }

      return new DefaultTimephasedCostContainer(this, null, result, false);
   }

   /**
    * Generates timephased costs from timephased work where multiple cost rates
    * apply to the assignment.
    *
    * @param standardWorkList timephased work
    * @param overtimeWorkList timephased work
    * @return timephased cost
    */
   private TimephasedCostContainer getTimephasedCostMultipleRates(List<TimephasedWork> standardWorkList, List<TimephasedWork> overtimeWorkList)
   {
      List<TimephasedWork> standardWorkResult = new ArrayList<>();
      List<TimephasedWork> overtimeWorkResult = new ArrayList<>();
      CostRateTable table = getCostRateTable();
      ProjectCalendar calendar = getEffectiveCalendar();

      Iterator<TimephasedWork> iter = overtimeWorkList.iterator();
      for (TimephasedWork standardWork : standardWorkList)
      {
         TimephasedWork overtimeWork = iter.hasNext() ? iter.next() : null;

         int startIndex = getCostRateTableEntryIndex(standardWork.getStart());
         int finishIndex = getCostRateTableEntryIndex(standardWork.getFinish());

         if (startIndex == finishIndex)
         {
            standardWorkResult.add(standardWork);
            if (overtimeWork != null)
            {
               overtimeWorkResult.add(overtimeWork);
            }
         }
         else
         {
            standardWorkResult.addAll(splitWork(table, calendar, standardWork, startIndex));
            if (overtimeWork != null)
            {
               overtimeWorkResult.addAll(splitWork(table, calendar, overtimeWork, startIndex));
            }
         }
      }

      return getTimephasedCostSingleRate(standardWorkResult, overtimeWorkResult);
   }

   /**
    * Generates timephased costs from the assignment's cost value. Used for Cost type Resources.
    *
    * @return timephased cost
    */
   private TimephasedCostContainer getTimephasedCostFixedAmount()
   {
      List<TimephasedCost> result = new ArrayList<>();

      ProjectCalendar cal = getEffectiveCalendar();

      double remainingCost = getRemainingCost().doubleValue();

      if (remainingCost > 0)
      {
         AccrueType accrueAt = getResource().getAccrueAt();

         if (accrueAt == AccrueType.START)
         {
            result.add(splitCostStart(cal, remainingCost, getStart()));
         }
         else
            if (accrueAt == AccrueType.END)
            {
               result.add(splitCostEnd(cal, remainingCost, getFinish()));
            }
            else
            {
               //for prorated, we have to deal with it differently depending on whether
               //any actual has been entered, since we want to mimic the other timephased data
               //where planned and actual values do not overlap
               double numWorkingDays = cal.getWork(getStart(), getFinish(), TimeUnit.DAYS).getDuration();
               double standardAmountPerDay = getCost().doubleValue() / numWorkingDays;

               if (getActualCost().intValue() > 0)
               {
                  //need to get three possible blocks of data: one for the possible partial amount
                  //overlap with timephased actual cost; one with all the standard amount days
                  //that happen after the actual cost stops; and one with any remaining
                  //partial day cost amount

                  int numActualDaysUsed = (int) Math.ceil(getActualCost().doubleValue() / standardAmountPerDay);
                  LocalDateTime actualWorkFinish = cal.getDate(getStart(), Duration.getInstance(numActualDaysUsed, TimeUnit.DAYS));

                  double partialDayActualAmount = getActualCost().doubleValue() % standardAmountPerDay;

                  if (partialDayActualAmount > 0)
                  {
                     double dayAmount = standardAmountPerDay < remainingCost ? standardAmountPerDay - partialDayActualAmount : remainingCost;

                     result.add(splitCostEnd(cal, dayAmount, actualWorkFinish));

                     remainingCost -= dayAmount;
                  }

                  //see if there's anything left to work with
                  if (remainingCost > 0)
                  {
                     //have to split up the amount into standard prorated amount days and whatever is left
                     result.addAll(splitCostProrated(cal, remainingCost, standardAmountPerDay, cal.getNextWorkStart(actualWorkFinish)));
                  }

               }
               else
               {
                  //no actual cost to worry about, so just a standard split from the beginning of the assignment
                  result.addAll(splitCostProrated(cal, remainingCost, standardAmountPerDay, getStart()));
               }
            }
      }

      return new DefaultTimephasedCostContainer(this, null, result, false);
   }

   /**
    * Generates timephased actual costs from the assignment's cost value. Used for Cost type Resources.
    *
    * @return timephased cost
    */
   private TimephasedCostContainer getTimephasedActualCostFixedAmount()
   {
      List<TimephasedCost> result = new ArrayList<>();

      double actualCost = getActualCost().doubleValue();

      if (actualCost > 0)
      {
         AccrueType accrueAt = getResource().getAccrueAt();

         if (accrueAt == AccrueType.START)
         {
            result.add(splitCostStart(getEffectiveCalendar(), actualCost, getActualStart()));
         }
         else
            if (accrueAt == AccrueType.END)
            {
               result.add(splitCostEnd(getEffectiveCalendar(), actualCost, getActualFinish()));
            }
            else
            {
               //for prorated, we have to deal with it differently; have to 'fill up' each
               //day with the standard amount before going to the next one
               double numWorkingDays = getEffectiveCalendar().getWork(getStart(), getFinish(), TimeUnit.DAYS).getDuration();
               double standardAmountPerDay = getCost().doubleValue() / numWorkingDays;

               result.addAll(splitCostProrated(getEffectiveCalendar(), actualCost, standardAmountPerDay, getActualStart()));
            }
      }

      return new DefaultTimephasedCostContainer(this, null, result, false);
   }

   /**
    * Used for Cost type Resources.
    *
    * Generates a TimephasedCost block for the total amount on the start date. This is useful
    * for Cost resources that have an AccrueAt value of Start.
    *
    * @param calendar calendar used by this assignment
    * @param totalAmount cost amount for this block
    * @param start start date of the timephased cost block
    * @return timephased cost
    */
   private TimephasedCost splitCostStart(ProjectCalendar calendar, double totalAmount, LocalDateTime start)
   {
      TimephasedCost cost = new TimephasedCost();
      cost.setStart(start);
      cost.setFinish(calendar.getDate(start, Duration.getInstance(1, TimeUnit.DAYS)));
      cost.setAmountPerDay(Double.valueOf(totalAmount));
      cost.setTotalAmount(Double.valueOf(totalAmount));

      return cost;
   }

   /**
    * Used for Cost type Resources.
    *
    * Generates a TimephasedCost block for the total amount on the finish date. This is useful
    * for Cost resources that have an AccrueAt value of End.
    *
    * @param calendar calendar used by this assignment
    * @param totalAmount cost amount for this block
    * @param finish finish date of the timephased cost block
    * @return timephased cost
    */
   private TimephasedCost splitCostEnd(ProjectCalendar calendar, double totalAmount, LocalDateTime finish)
   {
      TimephasedCost cost = new TimephasedCost();
      ProjectCalendarHours hours = calendar.getHours(finish);
      cost.setStart(LocalTimeHelper.setTime(finish, hours.get(0).getStart()));
      cost.setFinish(finish);
      cost.setAmountPerDay(Double.valueOf(totalAmount));
      cost.setTotalAmount(Double.valueOf(totalAmount));

      return cost;
   }

   /**
    * Used for Cost type Resources.
    *
    * Generates up to two TimephasedCost blocks for a cost amount. The first block will contain
    * all the days using the standardAmountPerDay, and a second block will contain any
    * final amount that is not enough for a complete day. This is useful for Cost resources
    * who have an AccrueAt value of Prorated.
    *
    * @param calendar calendar used by this assignment
    * @param totalAmount cost amount to be prorated
    * @param standardAmountPerDay cost amount for a normal working day
    * @param start date of the first timephased cost block
    * @return timephased cost
    */
   private List<TimephasedCost> splitCostProrated(ProjectCalendar calendar, double totalAmount, double standardAmountPerDay, LocalDateTime start)
   {
      List<TimephasedCost> result = new ArrayList<>();

      double numStandardAmountDays = Math.floor(totalAmount / standardAmountPerDay);
      double amountForLastDay = totalAmount % standardAmountPerDay;

      //first block contains all the normal work at the beginning of the assignment's life, if any

      if (numStandardAmountDays > 0)
      {
         LocalDateTime finishStandardBlock = calendar.getDate(start, Duration.getInstance(numStandardAmountDays, TimeUnit.DAYS));

         TimephasedCost standardBlock = new TimephasedCost();
         standardBlock.setAmountPerDay(Double.valueOf(standardAmountPerDay));
         standardBlock.setStart(start);
         standardBlock.setFinish(finishStandardBlock);
         standardBlock.setTotalAmount(Double.valueOf(numStandardAmountDays * standardAmountPerDay));

         result.add(standardBlock);

         start = calendar.getNextWorkStart(finishStandardBlock);
      }

      //next block contains the partial day amount, if any
      if (amountForLastDay > 0)
      {
         TimephasedCost nextBlock = new TimephasedCost();
         nextBlock.setAmountPerDay(Double.valueOf(amountForLastDay));
         nextBlock.setTotalAmount(Double.valueOf(amountForLastDay));
         nextBlock.setStart(start);
         nextBlock.setFinish(calendar.getDate(start, Duration.getInstance(1, TimeUnit.DAYS)));

         result.add(nextBlock);
      }

      return result;
   }

   /**
    * Splits timephased work segments in line with cost rates. Note that this is
    * an approximation - where a rate changes during a working day, the second
    * rate is used for the whole day.
    *
    * @param table cost rate table
    * @param calendar calendar used by this assignment
    * @param work timephased work segment
    * @param rateIndex rate applicable at the start of the timephased work segment
    * @return list of segments which replace the one supplied by the caller
    */
   private List<TimephasedWork> splitWork(CostRateTable table, ProjectCalendar calendar, TimephasedWork work, int rateIndex)
   {
      List<TimephasedWork> result = new ArrayList<>();
      work.setTotalAmount(Duration.getInstance(0, work.getAmountPerDay().getUnits()));

      while (true)
      {
         CostRateTableEntry rate = table.get(rateIndex);
         LocalDateTime splitDate = rate.getEndDate();
         if (!splitDate.isBefore(work.getFinish()))
         {
            result.add(work);
            break;
         }

         LocalDateTime currentPeriodEnd = calendar.getPreviousWorkFinish(splitDate);

         TimephasedWork currentPeriod = new TimephasedWork(work);
         currentPeriod.setFinish(currentPeriodEnd);
         result.add(currentPeriod);

         LocalDateTime nextPeriodStart = calendar.getNextWorkStart(splitDate);
         work.setStart(nextPeriodStart);

         ++rateIndex;
      }

      return result;
   }

   /**
    * Used to determine if multiple cost rates apply to this assignment.
    *
    * @return true if multiple cost rates apply to this assignment
    */
   private boolean hasMultipleCostRates()
   {
      boolean result = false;
      CostRateTable table = getCostRateTable();

      //
      // We assume here that if there is just one entry in the cost rate
      // table, this is an open-ended rate which covers any work, it won't
      // have specific dates attached to it.
      //
      if (table != null && table.size() > 1)
      {
         //
         // If we have multiple rates in the table, see if the same rate
         // is in force at the start and the end of the assignment.
         //
         CostRateTableEntry startEntry = table.getEntryByDate(getStart());
         CostRateTableEntry finishEntry = table.getEntryByDate(getFinish());
         result = (startEntry != finishEntry);
      }

      return result;
   }

   /**
    * Retrieves the cost rate table entry active on a given date.
    *
    * @param date target date
    * @return cost rate table entry
    */
   private CostRateTableEntry getCostRateTableEntry(LocalDateTime date)
   {
      CostRateTable table = getCostRateTable();
      if (table == null)
      {
         return CostRateTableEntry.DEFAULT_ENTRY;
      }

      CostRateTableEntry entry = table.size() == 1 ? table.get(0) : table.getEntryByDate(date);
      if (entry == null)
      {
         entry = CostRateTableEntry.DEFAULT_ENTRY;
      }
      return entry;
   }

   /**
    * Retrieves the index of a cost rate table entry active on a given date.
    *
    * @param date target date
    * @return cost rate table entry index
    */
   private int getCostRateTableEntryIndex(LocalDateTime date)
   {
      CostRateTable table = getCostRateTable();
      return table.size() == 1 ? 0 : table.getIndexByDate(date);
   }

   /**
    * Retrieve a flag indicating if this resource assignment has timephased
    * data associated with it.
    *
    * @return true if this resource assignment has timephased data
    */
   public boolean getHasTimephasedData()
   {
      return (m_timephasedWork != null && m_timephasedWork.hasData()) || (m_timephasedActualWork != null && m_timephasedActualWork.hasData());
   }

   /**
    * Set timephased baseline work. Note that index 0 represents "Baseline",
    * index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @param data timephased data
    */
   public void setTimephasedBaselineWork(int index, TimephasedWorkContainer data)
   {
      m_timephasedBaselineWork[index] = data;
   }

   /**
    * Set timephased baseline work. Note that index 0 represents "Baseline",
    * index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @param data timephased data
    */
   public void setTimephasedBaselineWork(int index, List<TimephasedWork> data)
   {
      m_timephasedBaselineWork[index] = createTimephasedWorkContainer(data);
   }

   /**
    * Set timephased baseline cost. Note that index 0 represents "Baseline",
    * index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @param data timephased data
    */
   public void setTimephasedBaselineCost(int index, TimephasedCostContainer data)
   {
      m_timephasedBaselineCost[index] = data;
   }

   /**
    * Set timephased baseline cost. Note that index 0 represents "Baseline",
    * index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @param data timephased data
    */
   public void setTimephasedBaselineCost(int index, List<TimephasedCost> data)
   {
      m_timephasedBaselineCost[index] = createTimephasedCostContainer(data);
   }

   /**
    * Retrieve timephased baseline work. Note that index 0 represents "Baseline",
    * index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @return timephased work, or null if no baseline is present
    */
   public List<TimephasedWork> getTimephasedBaselineWork(int index)
   {
      return m_timephasedBaselineWork[index] == null ? null : m_timephasedBaselineWork[index].getData();
   }

   /**
    * Retrieve timephased baseline cost. Note that index 0 represents "Baseline",
    * index 1 represents "Baseline1" and so on.
    *
    * @param index baseline index
    * @return timephased work, or null if no baseline is present
    */
   public List<TimephasedCost> getTimephasedBaselineCost(int index)
   {
      return m_timephasedBaselineCost[index] == null ? null : m_timephasedBaselineCost[index].getData();
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
      return getParentFile().getCostAccounts().getByUniqueID((Integer) get(AssignmentField.COST_ACCOUNT_UNIQUE_ID));
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
    * Retrieve the value of a field using its alias.
    *
    * @param alias field alias
    * @return field value
    */
   public Object getFieldByAlias(String alias)
   {
      return get(getParentFile().getResourceAssignments().getFieldTypeByAlias(alias));
   }

   /**
    * Set the value of a field using its alias.
    *
    * @param alias field alias
    * @param value field value
    */
   public void setFieldByAlias(String alias, Object value)
   {
      set(getParentFile().getResourceAssignments().getFieldTypeByAlias(alias), value);
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
         getParentFile().getResourceAssignments().updateUniqueID(this, (Integer) oldValue, (Integer) newValue);
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
         pct = Double.valueOf((actualWork.getDuration() * 100) / work.convertUnits(actualWork.getUnits(), getParentFile().getProjectProperties()).getDuration());
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
         variance = Duration.getInstance(work.getDuration() - baselineWork.convertUnits(work.getUnits(), getParentFile().getProjectProperties()).getDuration(), work.getUnits());
         set(AssignmentField.WORK_VARIANCE, variance);
      }
      return variance;
   }

   private Duration calculateStartVariance()
   {
      TimeUnit format = getParentFile().getProjectProperties().getDefaultDurationUnits();
      return LocalDateTimeHelper.getVariance(getEffectiveCalendar(), getBaselineStart(), getStart(), format);
   }

   private Duration calculateFinishVariance()
   {
      TimeUnit format = getParentFile().getProjectProperties().getDefaultDurationUnits();
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

   /**
    * Wrap a list of TimephasedWork instances in a TimephasedWorkContainer.
    *
    * @param data timephased work data
    * @return TimephasedWorkContainer instance
    */
   private TimephasedWorkContainer createTimephasedWorkContainer(List<TimephasedWork> data)
   {
      if (data == null)
      {
         return null;
      }

      return new TimephasedWorkContainer()
      {
         @Override public List<TimephasedWork> getData()
         {
            return data;
         }

         @Override public boolean hasData()
         {
            return true;
         }

         @Override public TimephasedWorkContainer applyFactor(double perDayFactor, double totalFactor)
         {
            throw new UnsupportedOperationException();
         }
      };
   }

   /**
    * Wrap a list of TimephasedCost instances in a TimephasedCostContainer.
    *
    * @param data timephased cost data
    * @return TimephasedCostContainer instance
    */
   private TimephasedCostContainer createTimephasedCostContainer(List<TimephasedCost> data)
   {
      if (data == null)
      {
         return null;
      }

      return new TimephasedCostContainer()
      {
         @Override public List<TimephasedCost> getData()
         {
            return data;
         }

         @Override public boolean hasData()
         {
            return true;
         }
      };
   }

   private TimephasedWorkContainer m_timephasedPlannedWork;

   private TimephasedWorkContainer m_timephasedActualWork;
   private TimephasedCostContainer m_timephasedActualCost;

   private TimephasedWorkContainer m_timephasedWork;
   private TimephasedCostContainer m_timephasedCost;

   private TimephasedWorkContainer m_timephasedOvertimeWork;
   private TimephasedWorkContainer m_timephasedActualOvertimeWork;

   private final TimephasedWorkContainer[] m_timephasedBaselineWork = new TimephasedWorkContainer[11];
   private final TimephasedCostContainer[] m_timephasedBaselineCost = new TimephasedCostContainer[11];

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
      CALCULATED_FIELD_MAP.put(AssignmentField.RATE_INDEX, ResourceAssignment::defaultRateIndex);
      CALCULATED_FIELD_MAP.put(AssignmentField.RATE_SOURCE, ResourceAssignment::defaultRateSource);
      CALCULATED_FIELD_MAP.put(AssignmentField.CALCULATE_COSTS_FROM_UNITS, ResourceAssignment::defaultCalculateCostsFromUnits);
      CALCULATED_FIELD_MAP.put(AssignmentField.RESOURCE_ASSIGNMENT_CODE_VALUES, ResourceAssignment::defaultResourceAssignmentCodeValues);
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
   }
}
