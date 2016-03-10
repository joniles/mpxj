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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.sf.mpxj.common.AssignmentFieldLists;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.DefaultTimephasedWorkContainer;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.listener.FieldListener;

/**
 * This class represents a resource assignment record from an MPX file.
 */
public final class ResourceAssignment extends ProjectEntity implements ProjectEntityWithUniqueID, FieldContainer
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

      if (file.getProjectConfig().getAutoAssignmentUniqueID() == true)
      {
         setUniqueID(Integer.valueOf(file.getProjectConfig().getNextAssignmentUniqueID()));
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
      return (Integer) getCachedValue(AssignmentField.UNIQUE_ID);
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
      Date result = (Date) getCachedValue(AssignmentField.START);
      if (result == null)
      {
         result = getTask().getStart();
      }
      return result;
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
      Date result = (Date) getCachedValue(AssignmentField.FINISH);
      if (result == null)
      {
         result = getTask().getFinish();
      }
      return result;
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
      getParentFile().getAllResourceAssignments().remove(this);
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
   public void setTimephasedWork(DefaultTimephasedWorkContainer data)
   {
      m_timephasedWork = data;
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

         m_timephasedOvertimeWork = new DefaultTimephasedWorkContainer(m_timephasedWork, perDayFactor, totalFactor);
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
      return m_timephasedCost;
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

      return m_timephasedActualCost;
   }

   /**
    * Generates timephased costs from timephased work where a single cost rate
    * applies to the whole assignment.
    *
    * @param standardWorkList timephased work
    * @param overtimeWorkList timephased work
    * @return timephased cost
    */
   private List<TimephasedCost> getTimephasedCostSingleRate(List<TimephasedWork> standardWorkList, List<TimephasedWork> overtimeWorkList)
   {
      List<TimephasedCost> result = new LinkedList<TimephasedCost>();

      //just return an empty list if there is no timephased work passed in
      if (standardWorkList == null)
      {
         return result;
      }

      //takes care of the situation where there is no timephased overtime work
      Iterator<TimephasedWork> overtimeIterator = overtimeWorkList == null ? java.util.Collections.<TimephasedWork> emptyList().iterator() : overtimeWorkList.iterator();

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
            result.addAll(splitCostProrated(getCalendar(), totalCost, costPerDay, standardWork.getStart()));
         }

      }

      return result;
   }

   /**
    * Generates timephased costs from timephased work where multiple cost rates
    * apply to the assignment.
    *
    * @param standardWorkList timephased work
    * @param overtimeWorkList timephased work
    * @return timephased cost
    */
   private List<TimephasedCost> getTimephasedCostMultipleRates(List<TimephasedWork> standardWorkList, List<TimephasedWork> overtimeWorkList)
   {
      List<TimephasedWork> standardWorkResult = new LinkedList<TimephasedWork>();
      List<TimephasedWork> overtimeWorkResult = new LinkedList<TimephasedWork>();
      CostRateTable table = getCostRateTable();
      ProjectCalendar calendar = getCalendar();

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
   private List<TimephasedCost> getTimephasedCostFixedAmount()
   {
      List<TimephasedCost> result = new LinkedList<TimephasedCost>();

      ProjectCalendar cal = getCalendar();

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
               //for prorated, we have to deal with it differently depending on whether or not
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
                  Date actualWorkFinish = cal.getDate(getStart(), Duration.getInstance(numActualDaysUsed, TimeUnit.DAYS), false);

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

      return result;
   }

   /**
    * Generates timephased actual costs from the assignment's cost value. Used for Cost type Resources.
    *
    * @return timephased cost
    */
   private List<TimephasedCost> getTimephasedActualCostFixedAmount()
   {
      List<TimephasedCost> result = new LinkedList<TimephasedCost>();

      double actualCost = getActualCost().doubleValue();

      if (actualCost > 0)
      {
         AccrueType accrueAt = getResource().getAccrueAt();

         if (accrueAt == AccrueType.START)
         {
            result.add(splitCostStart(getCalendar(), actualCost, getActualStart()));
         }
         else
            if (accrueAt == AccrueType.END)
            {
               result.add(splitCostEnd(getCalendar(), actualCost, getActualFinish()));
            }
            else
            {
               //for prorated, we have to deal with it differently; have to 'fill up' each
               //day with the standard amount before going to the next one
               double numWorkingDays = getCalendar().getWork(getStart(), getFinish(), TimeUnit.DAYS).getDuration();
               double standardAmountPerDay = getCost().doubleValue() / numWorkingDays;

               result.addAll(splitCostProrated(getCalendar(), actualCost, standardAmountPerDay, getActualStart()));
            }
      }

      return result;
   }

   /**
    * Used for Cost type Resources.
    *
    * Generates a TimphasedCost block for the total amount on the start date. This is useful
    * for Cost resources that have an AccrueAt value of Start.
    *
    * @param calendar calendar used by this assignment
    * @param totalAmount cost amount for this block
    * @param start start date of the timephased cost block
    * @return timephased cost
    */
   private TimephasedCost splitCostStart(ProjectCalendar calendar, double totalAmount, Date start)
   {
      TimephasedCost cost = new TimephasedCost();
      cost.setStart(start);
      cost.setFinish(calendar.getDate(start, Duration.getInstance(1, TimeUnit.DAYS), false));
      cost.setAmountPerDay(Double.valueOf(totalAmount));
      cost.setTotalAmount(Double.valueOf(totalAmount));

      return cost;
   }

   /**
    * Used for Cost type Resources.
    *
    * Generates a TimphasedCost block for the total amount on the finish date. This is useful
    * for Cost resources that have an AccrueAt value of End.
    *
    * @param calendar calendar used by this assignment
    * @param totalAmount cost amount for this block
    * @param finish finish date of the timephased cost block
    * @return timephased cost
    */
   private TimephasedCost splitCostEnd(ProjectCalendar calendar, double totalAmount, Date finish)
   {
      TimephasedCost cost = new TimephasedCost();
      cost.setStart(calendar.getStartDate(finish, Duration.getInstance(1, TimeUnit.DAYS)));
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
   private List<TimephasedCost> splitCostProrated(ProjectCalendar calendar, double totalAmount, double standardAmountPerDay, Date start)
   {
      List<TimephasedCost> result = new LinkedList<TimephasedCost>();

      double numStandardAmountDays = Math.floor(totalAmount / standardAmountPerDay);
      double amountForLastDay = totalAmount % standardAmountPerDay;

      //first block contains all the normal work at the beginning of the assignments life, if any

      if (numStandardAmountDays > 0)
      {
         Date finishStandardBlock = calendar.getDate(start, Duration.getInstance(numStandardAmountDays, TimeUnit.DAYS), false);

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
         nextBlock.setFinish(calendar.getDate(start, Duration.getInstance(1, TimeUnit.DAYS), false));

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
      List<TimephasedWork> result = new LinkedList<TimephasedWork>();
      work.setTotalAmount(Duration.getInstance(0, work.getAmountPerDay().getUnits()));

      while (true)
      {
         CostRateTableEntry rate = table.get(rateIndex);
         Date splitDate = rate.getEndDate();
         if (splitDate.getTime() >= work.getFinish().getTime())
         {
            result.add(work);
            break;
         }

         Date currentPeriodEnd = calendar.getPreviousWorkFinish(splitDate);

         TimephasedWork currentPeriod = new TimephasedWork(work);
         currentPeriod.setFinish(currentPeriodEnd);
         result.add(currentPeriod);

         Date nextPeriodStart = calendar.getNextWorkStart(splitDate);
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
      if (table != null)
      {
         //
         // We assume here that if there is just one entry in the cost rate
         // table, this is an open ended rate which covers any work, it won't
         // have specific dates attached to it.
         //
         if (table.size() > 1)
         {
            //
            // If we have multiple rates in the table, see if the same rate
            // is in force at the start and the end of the aaaignment.
            //
            CostRateTableEntry startEntry = table.getEntryByDate(getStart());
            CostRateTableEntry finishEntry = table.getEntryByDate(getFinish());
            result = (startEntry != finishEntry);
         }
      }
      return result;
   }

   /**
    * Retrieves the cost rate table entry active on a given date.
    *
    * @param date target date
    * @return cost rate table entry
    */
   private CostRateTableEntry getCostRateTableEntry(Date date)
   {
      CostRateTableEntry result;

      CostRateTable table = getCostRateTable();
      if (table == null)
      {
         Resource resource = getResource();
         result = new CostRateTableEntry(resource.getStandardRate(), TimeUnit.HOURS, resource.getOvertimeRate(), TimeUnit.HOURS, resource.getCostPerUse(), null);
      }
      else
      {
         if (table.size() == 1)
         {
            result = table.get(0);
         }
         else
         {
            result = table.getEntryByDate(date);
         }
      }

      return result;
   }

   /**
    * Retrieves the index of a cost rate table entry active on a given date.
    *
    * @param date target date
    * @return cost rate table entry index
    */
   private int getCostRateTableEntryIndex(Date date)
   {
      int result = -1;

      CostRateTable table = getCostRateTable();
      if (table != null)
      {
         if (table.size() == 1)
         {
            result = 0;
         }
         else
         {
            result = table.getIndexByDate(date);
         }
      }

      return result;
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
         calendar = getParentFile().getDefaultCalendar();
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
      return (Integer) getCachedValue(AssignmentField.TASK_UNIQUE_ID);
   }

   /**
    * Retrieves the budget cost.
    *
    * @return budget cost
    */
   public Number getBudgetCost()
   {
      return (Number) getCachedValue(AssignmentField.BUDGET_COST);
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
      return (Duration) getCachedValue(AssignmentField.BUDGET_WORK);
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
      return (Number) getCachedValue(AssignmentField.BASELINE_BUDGET_COST);
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
      return (Duration) getCachedValue(AssignmentField.BASELINE_BUDGET_WORK);
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
      return ((Duration) getCachedValue(selectField(AssignmentFieldLists.BASELINE_WORKS, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Number getBaselineCost(int baselineNumber)
   {
      return ((Number) getCachedValue(selectField(AssignmentFieldLists.BASELINE_COSTS, baselineNumber)));
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineStart(int baselineNumber, Date value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_STARTS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Date getBaselineStart(int baselineNumber)
   {
      return (Date) getCachedValue(selectField(AssignmentFieldLists.BASELINE_STARTS, baselineNumber));
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineFinish(int baselineNumber, Date value)
   {
      set(selectField(AssignmentFieldLists.BASELINE_FINISHES, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Date getBaselineFinish(int baselineNumber)
   {
      return (Date) getCachedValue(selectField(AssignmentFieldLists.BASELINE_FINISHES, baselineNumber));
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
      return ((Duration) getCachedValue(selectField(AssignmentFieldLists.BASELINE_BUDGET_WORKS, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Number getBaselineBudgetCost(int baselineNumber)
   {
      return ((Number) getCachedValue(selectField(AssignmentFieldLists.BASELINE_BUDGET_COSTS, baselineNumber)));
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
      return (String) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_TEXT, index));
   }

   /**
    * Set a start value.
    *
    * @param index start index (1-10)
    * @param value start value
    */
   public void setStart(int index, Date value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_START, index), value);
   }

   /**
    * Retrieve a start value.
    *
    * @param index start index (1-10)
    * @return start value
    */
   public Date getStart(int index)
   {
      return (Date) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_START, index));
   }

   /**
    * Set a finish value.
    *
    * @param index finish index (1-10)
    * @param value finish value
    */
   public void setFinish(int index, Date value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_FINISH, index), value);
   }

   /**
    * Retrieve a finish value.
    *
    * @param index finish index (1-10)
    * @return finish value
    */
   public Date getFinish(int index)
   {
      return (Date) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_FINISH, index));
   }

   /**
    * Set a date value.
    *
    * @param index date index (1-10)
    * @param value date value
    */
   public void setDate(int index, Date value)
   {
      set(selectField(AssignmentFieldLists.CUSTOM_DATE, index), value);
   }

   /**
    * Retrieve a date value.
    *
    * @param index date index (1-10)
    * @return date value
    */
   public Date getDate(int index)
   {
      return (Date) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_DATE, index));
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
      return (Number) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_NUMBER, index));
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
      return (Duration) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_DURATION, index));
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
      return (Number) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_COST, index));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(selectField(AssignmentFieldLists.CUSTOM_FLAG, index)));
   }

   /**
    * Set an enterprise cost value.
    *
    * @param index cost index (1-30)
    * @param value cost value
    */
   public void setEnterpriseCost(int index, Number value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_COST, index), value);
   }

   /**
    * Retrieve an enterprise cost value.
    *
    * @param index cost index (1-30)
    * @return cost value
    */
   public Number getEnterpriseCost(int index)
   {
      return (Number) getCachedValue(selectField(AssignmentFieldLists.ENTERPRISE_COST, index));
   }

   /**
    * Set an enterprise date value.
    *
    * @param index date index (1-30)
    * @param value date value
    */
   public void setEnterpriseDate(int index, Date value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_DATE, index), value);
   }

   /**
    * Retrieve an enterprise date value.
    *
    * @param index date index (1-30)
    * @return date value
    */
   public Date getEnterpriseDate(int index)
   {
      return (Date) getCachedValue(selectField(AssignmentFieldLists.ENTERPRISE_DATE, index));
   }

   /**
    * Set an enterprise duration value.
    *
    * @param index duration index (1-30)
    * @param value duration value
    */
   public void setEnterpriseDuration(int index, Duration value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_DURATION, index), value);
   }

   /**
    * Retrieve an enterprise duration value.
    *
    * @param index duration index (1-30)
    * @return duration value
    */
   public Duration getEnterpriseDuration(int index)
   {
      return (Duration) getCachedValue(selectField(AssignmentFieldLists.ENTERPRISE_DURATION, index));
   }

   /**
    * Set an enterprise flag value.
    *
    * @param index flag index (1-20)
    * @param value flag value
    */
   public void setEnterpriseFlag(int index, boolean value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_FLAG, index), value);
   }

   /**
    * Retrieve an enterprise flag value.
    *
    * @param index flag index (1-20)
    * @return flag value
    */
   public boolean getEnterpriseFlag(int index)
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(selectField(AssignmentFieldLists.ENTERPRISE_FLAG, index)));
   }

   /**
    * Set an enterprise number value.
    *
    * @param index number index (1-40)
    * @param value number value
    */
   public void setEnterpriseNumber(int index, Number value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_NUMBER, index), value);
   }

   /**
    * Retrieve an enterprise number value.
    *
    * @param index number index (1-40)
    * @return number value
    */
   public Number getEnterpriseNumber(int index)
   {
      return (Number) getCachedValue(selectField(AssignmentFieldLists.ENTERPRISE_NUMBER, index));
   }

   /**
    * Set an enterprise text value.
    *
    * @param index text index (1-40)
    * @param value text value
    */
   public void setEnterpriseText(int index, String value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_TEXT, index), value);
   }

   /**
    * Retrieve an enterprise text value.
    *
    * @param index text index (1-40)
    * @return text value
    */
   public String getEnterpriseText(int index)
   {
      return (String) getCachedValue(selectField(AssignmentFieldLists.ENTERPRISE_TEXT, index));
   }

   /**
    * Retrieve an enterprise custom field value.
    *
    * @param index field index
    * @return field value
    */
   public String getEnterpriseCustomField(int index)
   {
      return ((String) getCachedValue(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_FIELD, index)));
   }

   /**
    * Set an enterprise custom field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseCustomField(int index, String value)
   {
      set(selectField(AssignmentFieldLists.ENTERPRISE_CUSTOM_FIELD, index), value);
   }

   /**
    * Returns the regular work of this resource assignment.
    *
    * @return work
    */
   public Duration getRegularWork()
   {
      return ((Duration) getCachedValue(AssignmentField.REGULAR_WORK));
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
      return ((Duration) getCachedValue(AssignmentField.ACTUAL_OVERTIME_WORK));
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
      return ((Duration) getCachedValue(AssignmentField.REMAINING_OVERTIME_WORK));
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
      Number cost = (Number) getCachedValue(AssignmentField.OVERTIME_COST);
      if (cost == null)
      {
         Number actual = getActualOvertimeCost();
         Number remaining = getRemainingOvertimeCost();
         if (actual != null && remaining != null)
         {
            cost = NumberHelper.getDouble(actual.doubleValue() + remaining.doubleValue());
            set(AssignmentField.OVERTIME_COST, cost);
         }
      }
      return (cost);
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
      return ((Number) getCachedValue(AssignmentField.REMAINING_COST));
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
      return ((Number) getCachedValue(AssignmentField.ACTUAL_OVERTIME_COST));
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
      return ((Number) getCachedValue(AssignmentField.REMAINING_OVERTIME_COST));
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
    * date or todays
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
      return ((Number) getCachedValue(AssignmentField.BCWP));
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
      return ((Number) getCachedValue(AssignmentField.BCWS));
   }

   /**
    * Retrieve the ACWP value.
    *
    * @return ACWP value
    */
   public Number getACWP()
   {
      return ((Number) getCachedValue(AssignmentField.ACWP));
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
      Number variance = (Number) getCachedValue(AssignmentField.SV);
      if (variance == null)
      {
         Number bcwp = getBCWP();
         Number bcws = getBCWS();
         if (bcwp != null && bcws != null)
         {
            variance = NumberHelper.getDouble(bcwp.doubleValue() - bcws.doubleValue());
            set(AssignmentField.SV, variance);
         }
      }
      return (variance);
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
      Number variance = (Number) getCachedValue(AssignmentField.CV);
      if (variance == null)
      {
         variance = Double.valueOf(NumberHelper.getDouble(getBCWP()) - NumberHelper.getDouble(getACWP()));
         set(AssignmentField.CV, variance);
      }
      return (variance);
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
      Number variance = (Number) getCachedValue(AssignmentField.COST_VARIANCE);
      if (variance == null)
      {
         Number cost = getCost();
         Number baselineCost = getBaselineCost();
         if (cost != null && baselineCost != null)
         {
            variance = NumberHelper.getDouble(cost.doubleValue() - baselineCost.doubleValue());
            set(AssignmentField.COST_VARIANCE, variance);
         }
      }
      return (variance);
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
      Number pct = (Number) getCachedValue(AssignmentField.PERCENT_WORK_COMPLETE);
      if (pct == null)
      {
         Duration actualWork = getActualWork();
         Duration work = getWork();
         if (actualWork != null && work != null && work.getDuration() != 0)
         {
            pct = Double.valueOf((actualWork.getDuration() * 100) / work.convertUnits(actualWork.getUnits(), getParentFile().getProjectProperties()).getDuration());
            set(AssignmentField.PERCENT_WORK_COMPLETE, pct);
         }
      }
      return pct;
   }

   /**
    * This method is used to add notes to the current task.
    *
    * @param notes notes to be added
    */
   public void setNotes(String notes)
   {
      set(AssignmentField.NOTES, notes);
   }

   /**
    * The Notes field contains notes that you can enter about a task.
    * You can use task notes to help maintain a history for a task.
    *
    * @return notes
    */
   public String getNotes()
   {
      String notes = (String) getCachedValue(AssignmentField.NOTES);
      return (notes == null ? "" : notes);
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
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(AssignmentField.CONFIRMED)));
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
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(AssignmentField.UPDATE_NEEDED)));
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
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(AssignmentField.LINKED_FIELDS)));
   }

   /**
    * Retrieves the task hyperlink attribute.
    *
    * @return hyperlink attribute
    */
   public String getHyperlink()
   {
      return ((String) getCachedValue(AssignmentField.HYPERLINK));
   }

   /**
    * Retrieves the task hyperlink address attribute.
    *
    * @return hyperlink address attribute
    */
   public String getHyperlinkAddress()
   {
      return ((String) getCachedValue(AssignmentField.HYPERLINK_ADDRESS));
   }

   /**
    * Retrieves the task hyperlink sub-address attribute.
    *
    * @return hyperlink sub address attribute
    */
   public String getHyperlinkSubAddress()
   {
      return ((String) getCachedValue(AssignmentField.HYPERLINK_SUBADDRESS));
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
      Duration variance = (Duration) getCachedValue(AssignmentField.WORK_VARIANCE);
      if (variance == null)
      {
         Duration work = getWork();
         Duration baselineWork = getBaselineWork();
         if (work != null && baselineWork != null)
         {
            variance = Duration.getInstance(work.getDuration() - baselineWork.convertUnits(work.getUnits(), getParentFile().getProjectProperties()).getDuration(), work.getUnits());
            set(AssignmentField.WORK_VARIANCE, variance);
         }
      }
      return (variance);
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
      Duration variance = (Duration) getCachedValue(AssignmentField.START_VARIANCE);
      if (variance == null)
      {
         TimeUnit format = getParentFile().getProjectProperties().getDefaultDurationUnits();
         variance = DateHelper.getVariance(getTask(), getBaselineStart(), getStart(), format);
         set(AssignmentField.START_VARIANCE, variance);
      }
      return (variance);
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
      Duration variance = (Duration) getCachedValue(AssignmentField.FINISH_VARIANCE);
      if (variance == null)
      {
         TimeUnit format = getParentFile().getProjectProperties().getDefaultDurationUnits();
         variance = DateHelper.getVariance(getTask(), getBaselineFinish(), getFinish(), format);
         set(AssignmentField.FINISH_VARIANCE, variance);
      }
      return (variance);
   }

   /**
    * The Created field contains the date and time when a task was added
    * to the project.
    *
    * @return Date
    */
   public Date getCreateDate()
   {
      return ((Date) getCachedValue(AssignmentField.CREATED));
   }

   /**
    * The Created field contains the date and time when a task was
    * added to the project.
    *
    * @param val date
    */
   public void setCreateDate(Date val)
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
      return (UUID) getCachedValue(AssignmentField.GUID);
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
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(AssignmentField.RESPONSE_PENDING)));
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
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(AssignmentField.TEAM_STATUS_PENDING)));
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
      return ((Number) getCachedValue(AssignmentField.VAC));
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
      Integer value = (Integer) getCachedValue(AssignmentField.COST_RATE_TABLE);
      return value == null ? 0 : value.intValue();
   }

   /**
    * Returns the cost rate table for this assignment.
    *
    * @return cost rate table index
    */
   public CostRateTable getCostRateTable()
   {
      return getResource() == null ? null : getResource().getCostRateTable(getCostRateTableIndex());
   }

   /**
    * Retrieves the hyperlink screen tip attribute.
    *
    * @return hyperlink screen tip attribute
    */
   public String getHyperlinkScreenTip()
   {
      return ((String) getCachedValue(AssignmentField.HYPERLINK_SCREEN_TIP));
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
      return (ResourceRequestType) getCachedValue(AssignmentField.RESOURCE_REQUEST_TYPE);
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
   public Date getStop()
   {
      return ((Date) getCachedValue(AssignmentField.STOP));
   }

   /**
    * Set the stop date.
    *
    * @param stop stop date
    */
   public void setStop(Date stop)
   {
      set(AssignmentField.STOP, stop);
   }

   /**
    * Retrieve the resume date.
    *
    * @return resume date
    */
   public Date getResume()
   {
      return ((Date) getCachedValue(AssignmentField.RESUME));
   }

   /**
    * Set the resume date.
    *
    * @param resume resume date
    */
   public void setResume(Date resume)
   {
      set(AssignmentField.RESUME, resume);
   }

   /**
    * Maps a field index to an AssignmentField instance.
    *
    * @param fields array of fields used as the basis for the mapping.
    * @param index required field index
    * @return AssignmnetField instance
    */
   private AssignmentField selectField(AssignmentField[] fields, int index)
   {
      if (index < 1 || index > fields.length)
      {
         throw new IllegalArgumentException(index + " is not a valid field index");
      }
      return (fields[index - 1]);
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
   @Override public void set(FieldType field, Object value)
   {
      if (field != null)
      {
         int index = field.getValue();
         if (m_eventsEnabled)
         {
            fireFieldChangeEvent((AssignmentField) field, m_array[index], value);
         }
         m_array[index] = value;
      }
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
    * Handle the change in a field value. Reset any cached calculated
    * values affected by this change, pass on the event to any external
    * listeners.
    *
    * @param field field changed
    * @param oldValue old field value
    * @param newValue new field value
    */
   private void fireFieldChangeEvent(AssignmentField field, Object oldValue, Object newValue)
   {
      //
      // Internal event handling
      //
      switch (field)
      {
         case START:
         case BASELINE_START:
         {
            m_array[AssignmentField.START_VARIANCE.getValue()] = null;
            break;
         }

         case FINISH:
         case BASELINE_FINISH:
         {
            m_array[AssignmentField.FINISH_VARIANCE.getValue()] = null;
            break;
         }

         case BCWP:
         case ACWP:
         {
            m_array[AssignmentField.CV.getValue()] = null;
            m_array[AssignmentField.SV.getValue()] = null;
            break;
         }

         case COST:
         case BASELINE_COST:
         {
            m_array[AssignmentField.COST_VARIANCE.getValue()] = null;
            break;
         }

         case WORK:
         case BASELINE_WORK:
         {
            m_array[AssignmentField.WORK_VARIANCE.getValue()] = null;
            break;
         }

         case ACTUAL_OVERTIME_COST:
         case REMAINING_OVERTIME_COST:
         {
            m_array[AssignmentField.OVERTIME_COST.getValue()] = null;
            break;
         }

         default:
         {
            break;
         }
      }

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
   @Override public void addFieldListener(FieldListener listener)
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
   @Override public void removeFieldListener(FieldListener listener)
   {
      if (m_listeners != null)
      {
         m_listeners.remove(listener);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public Object getCachedValue(FieldType field)
   {
      return (field == null ? null : m_array[field.getValue()]);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Object getCurrentValue(FieldType field)
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

   private DefaultTimephasedWorkContainer m_timephasedWork;
   private List<TimephasedCost> m_timephasedCost;

   private TimephasedWorkContainer m_timephasedActualWork;
   private List<TimephasedCost> m_timephasedActualCost;

   private TimephasedWorkContainer m_timephasedOvertimeWork;
   private TimephasedWorkContainer m_timephasedActualOvertimeWork;

   private List<FieldListener> m_listeners;
   private TimephasedWorkContainer[] m_timephasedBaselineWork = new TimephasedWorkContainer[11];
   private TimephasedCostContainer[] m_timephasedBaselineCost = new TimephasedCostContainer[11];

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
