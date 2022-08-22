/*
 * file:      Resource.java
 * author:    Jon Iles
 *            Scott Melville
 * copyright: (c) Packwood Software 2002-2003
 * date:      15/08/2002
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ResourceFieldLists;
import net.sf.mpxj.listener.FieldListener;

/**
 * This class represents a resource used in a project.
 */
public final class Resource extends ProjectEntity implements Comparable<Resource>, ProjectEntityWithID, FieldContainer
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   Resource(ProjectFile file)
   {
      super(file);

      setType(ResourceType.WORK);
      setRole(Boolean.FALSE);

      ProjectConfig config = file.getProjectConfig();

      if (config.getAutoResourceUniqueID())
      {
         setUniqueID(Integer.valueOf(config.getNextResourceUniqueID()));
      }

      if (config.getAutoResourceID())
      {
         setID(Integer.valueOf(config.getNextResourceID()));
      }

      m_costRateTables = new CostRateTable[CostRateTable.MAX_TABLES];
      for (int index=0; index < m_costRateTables.length; index++)
      {
         CostRateTable table = new CostRateTable();
         table.add(CostRateTableEntry.DEFAULT_ENTRY);
         m_costRateTables[index] = table;
      }
   }

   /**
    * Sets Name field value.
    *
    * @param val value
    */
   public void setName(String val)
   {
      set(ResourceField.NAME, val);
   }

   /**
    * Gets Resource Name field value.
    *
    * @return value
    */
   public String getName()
   {
      return ((String) getCachedValue(ResourceField.NAME));
   }

   /**
    * Set the resource type. Can be TYPE_MATERIAL, or TYPE_WORK.
    *
    * @param type resource type
    */
   public void setType(ResourceType type)
   {
      set(ResourceField.TYPE, type);
   }

   /**
    * Retrieves the resource type. Can return TYPE_MATERIAL, or TYPE_WORK.
    *
    * @return resource type
    */
   public ResourceType getType()
   {
      return ((ResourceType) getCachedValue(ResourceField.TYPE));
   }

   /**
    * Set the flag indicating that this is a null resource.
    *
    * @param isNull null resource flag
    */
   public void setIsNull(boolean isNull)
   {
      m_null = isNull;
   }

   /**
    * Retrieve a flag indicating if this is a null resource.
    *
    * @return boolean flag
    */
   public boolean getNull()
   {
      return (m_null);
   }

   /**
    * Sets Initials field value.
    *
    * @param val value
    */
   public void setInitials(String val)
   {
      set(ResourceField.INITIALS, val);
   }

   /**
    * Gets Initials of name field value.
    *
    * @return value
    */
   public String getInitials()
   {
      return ((String) getCachedValue(ResourceField.INITIALS));
   }

   /**
    * Sets phonetic information for the Japanese version of MS Project.
    *
    * @param phonetics Japanese phonetic information
    */
   public void setPhonetics(String phonetics)
   {
      set(ResourceField.PHONETICS, phonetics);
   }

   /**
    * Retrieves phonetic information for the Japanese version of MS Project.
    *
    * @return Japanese phonetic information
    */
   public String getPhonetics()
   {
      return ((String) getCachedValue(ResourceField.PHONETICS));
   }

   /**
    * Sets the Windows account name for a resource.
    *
    * @param ntAccount windows account name
    */
   public void setNtAccount(String ntAccount)
   {
      set(ResourceField.WINDOWS_USER_ACCOUNT, ntAccount);
   }

   /**
    * Retrieves the Windows account name for a resource.
    *
    * @return windows account name
    */
   public String getNtAccount()
   {
      return (String) getCachedValue(ResourceField.WINDOWS_USER_ACCOUNT);
   }

   /**
    * Set the units label for a material resource.
    *
    * @param materialLabel material resource units label
    */
   public void setMaterialLabel(String materialLabel)
   {
      set(ResourceField.MATERIAL_LABEL, materialLabel);
   }

   /**
    * Retrieves the units label for a material resource.
    *
    * @return material resource units label
    */
   public String getMaterialLabel()
   {
      return ((String) getCachedValue(ResourceField.MATERIAL_LABEL));
   }

   /**
    * Sets code field value.
    *
    * @param val value
    */
   public void setCode(String val)
   {
      set(ResourceField.CODE, val);
   }

   /**
    * Gets code field value.
    *
    * @return value
    */
   public String getCode()
   {
      return ((String) getCachedValue(ResourceField.CODE));
   }

   /**
    * Sets Group field value.
    *
    * @param val value
    */
   public void setGroup(String val)
   {
      set(ResourceField.GROUP, val);
   }

   /**
    * Gets Group field value.
    *
    * @return value
    */
   public String getGroup()
   {
      return ((String) getCachedValue(ResourceField.GROUP));
   }

   /**
    * Set the messaging method used to communicate with a project team.
    *
    * @param workGroup messaging method
    */
   public void setWorkGroup(WorkGroup workGroup)
   {
      set(ResourceField.WORKGROUP, workGroup);
   }

   /**
    * Retrieve the messaging method used to communicate with a project team.
    *
    * @return messaging method
    */
   public WorkGroup getWorkGroup()
   {
      return ((WorkGroup) getCachedValue(ResourceField.WORKGROUP));
   }

   /**
    * Set the resource's email address.
    *
    * @param emailAddress email address
    */
   public void setEmailAddress(String emailAddress)
   {
      set(ResourceField.EMAIL_ADDRESS, emailAddress);
   }

   /**
    * Retrieves the resource's email address.
    *
    * @return email address
    */
   public String getEmailAddress()
   {
      return ((String) getCachedValue(ResourceField.EMAIL_ADDRESS));
   }

   /**
    * Sets the hyperlink text.
    *
    * @param hyperlink hyperlink text
    */
   public void setHyperlink(String hyperlink)
   {
      set(ResourceField.HYPERLINK, hyperlink);
   }

   /**
    * Retrieves the hyperlink text.
    *
    * @return hyperlink text
    */
   public String getHyperlink()
   {
      return ((String) getCachedValue(ResourceField.HYPERLINK));
   }

   /**
    * Sets the hyperlink address.
    *
    * @param hyperlinkAddress hyperlink address
    */
   public void setHyperlinkAddress(String hyperlinkAddress)
   {
      set(ResourceField.HYPERLINK_ADDRESS, hyperlinkAddress);
   }

   /**
    * Retrieves the hyperlink address.
    *
    * @return hyperlink address
    */
   public String getHyperlinkAddress()
   {
      return ((String) getCachedValue(ResourceField.HYPERLINK_ADDRESS));
   }

   /**
    * Sets the hyperlink sub-address.
    *
    * @param hyperlinkSubAddress hyperlink sub-address
    */
   public void setHyperlinkSubAddress(String hyperlinkSubAddress)
   {
      set(ResourceField.HYPERLINK_SUBADDRESS, hyperlinkSubAddress);
   }

   /**
    * Retrieves the hyperlink sub-address.
    *
    * @return hyperlink sub-address
    */
   public String getHyperlinkSubAddress()
   {
      return ((String) getCachedValue(ResourceField.HYPERLINK_SUBADDRESS));
   }

   /**
    * Sets the hyperlink screen tip attribute.
    *
    * @param text hyperlink screen tip attribute
    */
   public void setHyperlinkScreenTip(String text)
   {
      set(ResourceField.HYPERLINK_SCREEN_TIP, text);
   }

   /**
    * Retrieves the hyperlink screen tip attribute.
    *
    * @return hyperlink screen tip attribute
    */
   public String getHyperlinkScreenTip()
   {
      return ((String) getCachedValue(ResourceField.HYPERLINK_SCREEN_TIP));
   }

   /**
    * Sets the maximum availability of a resource.
    *
    * @param maxUnits maximum availability
    */
   public void setMaxUnits(Number maxUnits)
   {
      set(ResourceField.MAX_UNITS, maxUnits);
   }

   /**
    * Retrieves the maximum availability of a resource.
    *
    * @return maximum availability
    */
   public Number getMaxUnits()
   {
      return ((Number) getCachedValue(ResourceField.MAX_UNITS));
   }

   /**
    * Sets peak resource utilisation.
    *
    * @param peakUnits peak resource utilisation
    */
   public void setPeakUnits(Number peakUnits)
   {
      set(ResourceField.PEAK, peakUnits);
   }

   /**
    * Retrieves the peak resource utilisation.
    *
    * @return peak resource utilisation
    */
   public Number getPeakUnits()
   {
      return ((Number) getCachedValue(ResourceField.PEAK));
   }

   /**
    * Set the overallocated flag.
    *
    * @param overallocated overallocated flag
    */
   public void setOverAllocated(boolean overallocated)
   {
      set(ResourceField.OVERALLOCATED, overallocated);
   }

   /**
    * Retrieves the overallocated flag.
    *
    * @return overallocated flag
    */
   public boolean getOverAllocated()
   {
      Boolean overallocated = (Boolean) getCachedValue(ResourceField.OVERALLOCATED);
      if (overallocated == null)
      {
         Number peakUnits = getPeakUnits();
         Number maxUnits = getMaxUnits();
         overallocated = Boolean.valueOf(NumberHelper.getDouble(peakUnits) > NumberHelper.getDouble(maxUnits));
         set(ResourceField.OVERALLOCATED, overallocated);
      }
      return (overallocated.booleanValue());
   }

   /**
    * Retrieves the "available from" date.
    *
    * @return available from date
    */
   public Date getAvailableFrom()
   {
      return ((Date) getCachedValue(ResourceField.AVAILABLE_FROM));
   }

   /**
    * Set the "available from" date.
    *
    * @param date available from date
    */
   public void setAvailableFrom(Date date)
   {
      set(ResourceField.AVAILABLE_FROM, date);
   }

   /**
    * Retrieves the "available to" date.
    *
    * @return available from date
    */
   public Date getAvailableTo()
   {
      return ((Date) getCachedValue(ResourceField.AVAILABLE_TO));
   }

   /**
    * Set the "available to" date.
    *
    * @param date available to date
    */
   public void setAvailableTo(Date date)
   {
      set(ResourceField.AVAILABLE_TO, date);
   }

   /**
    * Retrieves the earliest start date for all assigned tasks.
    *
    * @return start date
    */
   public Date getStart()
   {
      Date result = null;
      for (ResourceAssignment assignment : m_assignments)
      {
         if (result == null || DateHelper.compare(result, assignment.getStart()) > 0)
         {
            result = assignment.getStart();
         }
      }
      return (result);
   }

   /**
    * Retrieves the latest finish date for all assigned tasks.
    *
    * @return finish date
    */
   public Date getFinish()
   {
      Date result = null;
      for (ResourceAssignment assignment : m_assignments)
      {
         if (result == null || DateHelper.compare(result, assignment.getFinish()) < 0)
         {
            result = assignment.getFinish();
         }
      }
      return (result);
   }

   /**
    * Sets the flag indicating if the resource levelling can be applied to this
    * resource.
    *
    * @param canLevel boolean flag
    */
   public void setCanLevel(boolean canLevel)
   {
      set(ResourceField.CAN_LEVEL, canLevel);
   }

   /**
    * Retrieves the flag indicating if the resource levelling can be applied to
    * this resource.
    *
    * @return boolean flag
    */
   public boolean getCanLevel()
   {
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(ResourceField.CAN_LEVEL)));
   }

   /**
    * Sets the Accrue at type.The Accrue At field provides choices for how and
    * when resource standard and overtime costs are to be charged, or accrued,
    * to the cost of a task. The options are: Start, End and Prorated (Default)
    *
    * @param type accrue type
    */
   public void setAccrueAt(AccrueType type)
   {
      set(ResourceField.ACCRUE_AT, type);
   }

   /**
    * Gets the Accrue at type.The Accrue At field provides choices for how and
    * when resource standard and overtime costs are to be charged, or accrued,
    * to the cost of a task. The options are: Start, End and Prorated (Default)
    *
    * @return accrue type
    */
   public AccrueType getAccrueAt()
   {
      return ((AccrueType) getCachedValue(ResourceField.ACCRUE_AT));
   }

   /**
    * This field is ignored on import into MS Project.
    *
    * @param val - value to be set
    */
   public void setWork(Duration val)
   {
      set(ResourceField.WORK, val);
   }

   /**
    * Gets Work field value.
    *
    * @return value
    */
   public Duration getWork()
   {
      return ((Duration) getCachedValue(ResourceField.WORK));
   }

   /**
    * Retrieve the value of the regular work field. Note that this value is an
    * extension to the MPX specification.
    *
    * @return Regular work value
    */
   public Duration getRegularWork()
   {
      return ((Duration) getCachedValue(ResourceField.REGULAR_WORK));
   }

   /**
    * Set the value of the regular work field. Note that this value is an
    * extension to the MPX specification.
    *
    * @param duration Regular work value
    */
   public void setRegularWork(Duration duration)
   {
      set(ResourceField.REGULAR_WORK, duration);
   }

   /**
    * Sets the Actual Work field contains the amount of work that has already
    * been done for all assignments assigned to a resource.
    *
    * @param val duration value
    */
   public void setActualWork(Duration val)
   {
      set(ResourceField.ACTUAL_WORK, val);
   }

   /**
    * Retrieves the Actual Work field contains the amount of work that has
    * already been done for all assignments assigned to a resource.
    *
    * @return Actual work value
    */
   public Duration getActualWork()
   {
      return ((Duration) getCachedValue(ResourceField.ACTUAL_WORK));
   }

   /**
    * Sets the amount of overtime work.
    *
    * @param overtimeWork overtime work
    */
   public void setOvertimeWork(Duration overtimeWork)
   {
      set(ResourceField.OVERTIME_WORK, overtimeWork);
   }

   /**
    * Retrieves the amount of overtime work.
    *
    * @return overtime work
    */
   public Duration getOvertimeWork()
   {
      return ((Duration) getCachedValue(ResourceField.OVERTIME_WORK));
   }

   /**
    * This field is ignored on import into MS Project.
    *
    * @param val - value to be set
    */
   public void setRemainingWork(Duration val)
   {
      set(ResourceField.REMAINING_WORK, val);
   }

   /**
    * Gets Remaining Work field value.
    *
    * @return value
    */
   public Duration getRemainingWork()
   {
      return ((Duration) getCachedValue(ResourceField.REMAINING_WORK));
   }

   /**
    * Retrieve the value of the actual overtime work field.
    *
    * @return actual overtime work value
    */
   public Duration getActualOvertimeWork()
   {
      return ((Duration) getCachedValue(ResourceField.ACTUAL_OVERTIME_WORK));
   }

   /**
    * Sets the value of the actual overtime work field.
    *
    * @param duration actual overtime work value
    */
   public void setActualOvertimeWork(Duration duration)
   {
      set(ResourceField.ACTUAL_OVERTIME_WORK, duration);
   }

   /**
    * Retrieve the value of the remaining overtime work field.
    *
    * @return remaining overtime work value
    */
   public Duration getRemainingOvertimeWork()
   {
      return ((Duration) getCachedValue(ResourceField.REMAINING_OVERTIME_WORK));
   }

   /**
    * Sets the value of the remaining overtime work field.
    *
    * @param duration remaining overtime work value
    */
   public void setRemainingOvertimeWork(Duration duration)
   {
      set(ResourceField.REMAINING_OVERTIME_WORK, duration);
   }

   /**
    * Sets the value of the percent work complete field.
    *
    * @param percentWorkComplete percent work complete
    */
   public void setPercentWorkComplete(Number percentWorkComplete)
   {
      set(ResourceField.PERCENT_WORK_COMPLETE, percentWorkComplete);
   }

   /**
    * Retrieves the value of the percent work complete field.
    *
    * @return percent work complete
    */
   public Number getPercentWorkComplete()
   {
      return ((Number) getCachedValue(ResourceField.PERCENT_WORK_COMPLETE));
   }

   /**
    * Sets standard rate for this resource.
    *
    * @param val value
    * @deprecated configure this value in a cost rate table
    */
   @Deprecated public void setStandardRate(Rate val)
   {
      // Deprecated method
   }

   /**
    * Gets Standard Rate field value.
    *
    * @return Rate
    */
   public Rate getStandardRate()
   {
      CostRateTableEntry entry = getCurrentCostRateTableEntry(0);
      if (entry == null)
      {
         return null;
      }
      return entry.getStandardRate();
   }

   /**
    * Sets the format of the standard rate.
    *
    * @param units standard rate format
    */
   @Deprecated public void setStandardRateUnits(TimeUnit units)
   {
      // Deprecated method
   }

   /**
    * Retrieves the format of the standard rate.
    *
    * @return standard rate format
    */
   @Deprecated public TimeUnit getStandardRateUnits()
   {
      Rate rate = getStandardRate();
      return rate == null ? TimeUnit.HOURS : rate.getUnits();
   }

   /**
    * Sets the cost field value.
    *
    * @param cost cost field value
    */
   public void setCost(Number cost)
   {
      set(ResourceField.COST, cost);
   }

   /**
    * Retrieves the cost field value.
    *
    * @return cost field value
    */
   public Number getCost()
   {
      return ((Number) getCachedValue(ResourceField.COST));
   }

   /**
    * Sets the overtime rate for this resource.
    *
    * @param overtimeRate overtime rate value
    * @deprecated configure this value in a cost rate table
    */
   @Deprecated public void setOvertimeRate(Rate overtimeRate)
   {
      // Deprecated method
   }

   /**
    * Retrieves the overtime rate for this resource.
    *
    * @return overtime rate
    */
   public Rate getOvertimeRate()
   {
      CostRateTableEntry entry = getCurrentCostRateTableEntry(0);
      if (entry == null)
      {
         return null;
      }
      return entry.getOvertimeRate();
   }

   /**
    * Sets the format of the overtime rate.
    *
    * @param units overtime rate format
    */
   @Deprecated public void setOvertimeRateUnits(TimeUnit units)
   {
      // Deprecated method
   }

   /**
    * Retrieves the format of the overtime rate.
    *
    * @return overtime rate format
    */
   @Deprecated public TimeUnit getOvertimeRateUnits()
   {
      Rate rate = getOvertimeRate();
      return rate == null ? TimeUnit.HOURS : rate.getUnits();
   }

   /**
    * Retrieve the value of the overtime cost field.
    *
    * @return Overtime cost value
    */
   public Number getOvertimeCost()
   {
      return ((Number) getCachedValue(ResourceField.OVERTIME_COST));
   }

   /**
    * Set the value of the overtime cost field.
    *
    * @param currency Overtime cost
    */
   public void setOvertimeCost(Number currency)
   {
      set(ResourceField.OVERTIME_COST, currency);
   }

   /**
    * Set the cost per use.
    *
    * @param costPerUse cost per use
    * @deprecated configure this value in a cost rate table
    */
   @Deprecated public void setCostPerUse(Number costPerUse)
   {
      // Deprecated method
   }

   /**
    * Retrieve the cost per use.
    *
    * @return cost per use
    */
   public Number getCostPerUse()
   {
      CostRateTableEntry entry = getCurrentCostRateTableEntry(0);
      if (entry == null)
      {
         return null;
      }
      return entry.getCostPerUse();
   }

   /**
    * Set the actual cost for the work already performed by this resource.
    *
    * @param actualCost actual cost
    */
   public void setActualCost(Number actualCost)
   {
      set(ResourceField.ACTUAL_COST, actualCost);
   }

   /**
    * Retrieves the actual cost for the work already performed by this resource.
    *
    * @return actual cost
    */
   public Number getActualCost()
   {
      return ((Number) getCachedValue(ResourceField.ACTUAL_COST));
   }

   /**
    * Retrieve actual overtime cost.
    *
    * @return actual overtime cost
    */
   public Number getActualOvertimeCost()
   {
      return ((Number) getCachedValue(ResourceField.ACTUAL_OVERTIME_COST));
   }

   /**
    * Sets the actual overtime cost.
    *
    * @param actualOvertimeCost actual overtime cost
    */
   public void setActualOvertimeCost(Number actualOvertimeCost)
   {
      set(ResourceField.ACTUAL_OVERTIME_COST, actualOvertimeCost);
   }

   /**
    * Sets the remaining cost for this resource.
    *
    * @param remainingCost remaining cost
    */
   public void setRemainingCost(Number remainingCost)
   {
      set(ResourceField.REMAINING_COST, remainingCost);
   }

   /**
    * Retrieves the remaining cost for this resource.
    *
    * @return remaining cost
    */
   public Number getRemainingCost()
   {
      return ((Number) getCachedValue(ResourceField.REMAINING_COST));
   }

   /**
    * Retrieve the remaining overtime cost.
    *
    * @return remaining overtime cost
    */
   public Number getRemainingOvertimeCost()
   {
      return ((Number) getCachedValue(ResourceField.REMAINING_OVERTIME_COST));
   }

   /**
    * Set the remaining overtime cost.
    *
    * @param remainingOvertimeCost remaining overtime cost
    */
   public void setRemainingOvertimeCost(Number remainingOvertimeCost)
   {
      set(ResourceField.REMAINING_OVERTIME_COST, remainingOvertimeCost);
   }

   /**
    * Sets the work variance.
    *
    * @param workVariance work variance
    */
   public void setWorkVariance(Duration workVariance)
   {
      set(ResourceField.WORK_VARIANCE, workVariance);
   }

   /**
    * Retrieves the work variance.
    *
    * @return work variance
    */
   public Duration getWorkVariance()
   {
      Duration variance = (Duration) getCachedValue(ResourceField.WORK_VARIANCE);
      if (variance == null)
      {
         Duration work = getWork();
         Duration baselineWork = getBaselineWork();
         if (work != null && baselineWork != null)
         {
            variance = Duration.getInstance(work.getDuration() - baselineWork.convertUnits(work.getUnits(), getParentFile().getProjectProperties()).getDuration(), work.getUnits());
            set(ResourceField.WORK_VARIANCE, variance);
         }
      }
      return (variance);
   }

   /**
    * Sets the cost variance.
    *
    * @param costVariance cost variance
    */
   public void setCostVariance(Number costVariance)
   {
      set(ResourceField.COST_VARIANCE, costVariance);
   }

   /**
    * Retrieves the cost variance.
    *
    * @return cost variance
    */
   public Number getCostVariance()
   {
      Number variance = (Number) getCachedValue(ResourceField.COST_VARIANCE);
      if (variance == null)
      {
         Number cost = getCost();
         Number baselineCost = getBaselineCost();
         if (cost != null && baselineCost != null)
         {
            variance = NumberHelper.getDouble(cost.doubleValue() - baselineCost.doubleValue());
            set(ResourceField.COST_VARIANCE, variance);
         }
      }
      return (variance);
   }

   /**
    * Set the schedule variance.
    *
    * @param sv schedule variance
    */
   public void setSV(Number sv)
   {
      set(ResourceField.SV, sv);
   }

   /**
    * Retrieve the schedule variance.
    *
    * @return schedule variance
    */
   public Number getSV()
   {
      Number variance = (Number) getCachedValue(ResourceField.SV);
      if (variance == null)
      {
         Number bcwp = getBCWP();
         Number bcws = getBCWS();
         if (bcwp != null && bcws != null)
         {
            variance = NumberHelper.getDouble(bcwp.doubleValue() - bcws.doubleValue());
            set(ResourceField.SV, variance);
         }
      }
      return (variance);
   }

   /**
    * Set the cost variance.
    *
    * @param cv cost variance
    */
   public void setCV(Number cv)
   {
      set(ResourceField.CV, cv);
   }

   /**
    * Retrieve the cost variance.
    *
    * @return cost variance
    */
   public Number getCV()
   {
      Number variance = (Number) getCachedValue(ResourceField.CV);
      if (variance == null)
      {
         variance = Double.valueOf(NumberHelper.getDouble(getBCWP()) - NumberHelper.getDouble(getACWP()));
         set(ResourceField.CV, variance);
      }
      return (variance);
   }

   /**
    * Set the actual cost of work performed.
    *
    * @param acwp actual cost of work performed
    */
   public void setACWP(Number acwp)
   {
      set(ResourceField.ACWP, acwp);
   }

   /**
    * Set the actual cost of work performed.
    *
    * @return actual cost of work performed
    */
   public Number getACWP()
   {
      return ((Number) getCachedValue(ResourceField.ACWP));
   }

   /**
    * Sets the notes text for this resource.
    *
    * @param notes notes to be added
    */
   public void setNotes(String notes)
   {
      set(ResourceField.NOTES, notes == null ? null : new Notes(notes));
   }

   /**
    * Retrieve the plain text representation of the resource notes.
    * Use the getNotesObject method to retrieve an object which
    * contains both the plain text notes and, if relevant,
    * the original formatted version of the notes.
    *
    * @return notes
    */
   public String getNotes()
   {
      Object notes = getCachedValue(ResourceField.NOTES);
      return notes == null ? "" : notes.toString();
   }

   /**
    * Set the Notes instance representing the resource notes.
    *
    * @param notes Notes instance
    */
   public void setNotesObject(Notes notes)
   {
      set(ResourceField.NOTES, notes);
   }

   /**
    * Retrieve an object which contains both the plain text notes
    * and, if relevant, the original formatted version of the notes.
    *
    * @return Notes instance
    */
   public Notes getNotesObject()
   {
      return (Notes) getCachedValue(ResourceField.NOTES);
   }

   /**
    * Sets the budgeted cost of work scheduled.
    *
    * @param bcws budgeted cost of work scheduled
    */
   public void setBCWS(Number bcws)
   {
      set(ResourceField.BCWS, bcws);
   }

   /**
    * Retrieves the budgeted cost of work scheduled.
    *
    * @return budgeted cost of work scheduled
    */
   public Number getBCWS()
   {
      return ((Number) getCachedValue(ResourceField.BCWS));
   }

   /**
    * Sets the budgeted cost of work performed.
    *
    * @param bcwp budgeted cost of work performed
    */
   public void setBCWP(Number bcwp)
   {
      set(ResourceField.BCWP, bcwp);
   }

   /**
    * Retrieves the budgeted cost of work performed.
    *
    * @return budgeted cost of work performed
    */
   public Number getBCWP()
   {
      return ((Number) getCachedValue(ResourceField.BCWP));
   }

   /**
    * Sets the generic flag.
    *
    * @param value generic flag
    */
   public void setGeneric(boolean value)
   {
      set(ResourceField.GENERIC, value);
   }

   /**
    * Retrieves the generic flag.
    *
    * @return generic flag
    */
   public boolean getGeneric()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ResourceField.GENERIC));
   }

   /**
    * Sets the active flag.
    *
    * @param value generic flag
    */
   public void setActive(boolean value)
   {
      set(ResourceField.ACTIVE, value);
   }

   /**
    * Retrieves the active flag.
    *
    * @return generic flag
    */
   public boolean getActive()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ResourceField.ACTIVE));
   }

   /**
    * Sets the active directory GUID for this resource.
    *
    * @param guid active directory GUID
    */
   public void setActveDirectoryGUID(String guid)
   {
      m_activeDirectoryGUID = guid;
   }

   /**
    * Retrieves the active directory GUID for this resource.
    *
    * @return active directory GUID
    */
   public String getActiveDirectoryGUID()
   {
      return (m_activeDirectoryGUID);
   }

   /**
    * Sets the actual overtime work protected duration.
    *
    * @param duration actual overtime work protected
    */
   public void setActualOvertimeWorkProtected(Duration duration)
   {
      set(ResourceField.ACTUAL_OVERTIME_WORK_PROTECTED, duration);
   }

   /**
    * Retrieves the actual overtime work protected duration.
    *
    * @return actual overtime work protected
    */
   public Duration getActualOvertimeWorkProtected()
   {
      return (Duration) getCachedValue(ResourceField.ACTUAL_OVERTIME_WORK_PROTECTED);
   }

   /**
    * Sets the actual work protected duration.
    *
    * @param duration actual work protected
    */
   public void setActualWorkProtected(Duration duration)
   {
      set(ResourceField.ACTUAL_WORK_PROTECTED, duration);
   }

   /**
    * Retrieves the actual work protected duration.
    *
    * @return actual work protected
    */
   public Duration getActualWorkProtected()
   {
      return (Duration) getCachedValue(ResourceField.ACTUAL_WORK_PROTECTED);
   }

   /**
    * Sets the booking type.
    *
    * @param bookingType booking type
    */
   public void setBookingType(BookingType bookingType)
   {
      set(ResourceField.BOOKING_TYPE, bookingType);
   }

   /**
    * Retrieves the booking type.
    *
    * @return booking type
    */
   public BookingType getBookingType()
   {
      return (BookingType) getCachedValue(ResourceField.BOOKING_TYPE);
   }

   /**
    * Sets the creation date.
    *
    * @param creationDate creation date
    */
   public void setCreationDate(Date creationDate)
   {
      set(ResourceField.CREATED, creationDate);
   }

   /**
    * Retrieves the creation date.
    *
    * @return creation date
    */
   public Date getCreationDate()
   {
      return ((Date) getCachedValue(ResourceField.CREATED));
   }

   /**
    * Sets a flag indicating that a resource is an enterprise resource.
    *
    * @param enterprise boolean flag
    */
   public void setEnterprise(boolean enterprise)
   {
      set(ResourceField.ENTERPRISE, enterprise);
   }

   /**
    * Retrieves a flag indicating that a resource is an enterprise resource.
    *
    * @return boolean flag
    */
   public boolean getEnterprise()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ResourceField.ENTERPRISE));
   }

   /**
    * Retrieve the calendar unique ID.
    *
    * @return calendar unique ID
    */
   public Integer getCalendarUniqueID()
   {
      return (Integer) getCachedValue(ResourceField.CALENDAR_UNIQUE_ID);
   }

   /**
    * Set the calendar unique ID.
    *
    * @param id calendar unique ID
    */
   public void setCalendarUniqueID(Integer id)
   {
      set(ResourceField.CALENDAR_UNIQUE_ID, id);
   }

   /**
    * This method retrieves the calendar associated with this resource.
    *
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getCalendar()
   {
      return getParentFile().getCalendars().getByUniqueID(getCalendarUniqueID());
   }

   /**
    * This method allows a pre-existing resource calendar to be attached to a
    * resource.
    *
    * @param calendar resource calendar
    */
   public void setCalendar(ProjectCalendar calendar)
   {
      if (calendar == null)
      {
         setCalendarUniqueID(null);
      }
      else
      {
         setCalendarUniqueID(calendar.getUniqueID());
      }
   }

   /**
    * This method retrieves the calendar associated with this resource.
    *
    * @return ProjectCalendar instance
    * @deprecated use getCalendar
    */
   @Deprecated public ProjectCalendar getResourceCalendar()
   {
      return getCalendar();
   }

   /**
    * This method allows a pre-existing resource calendar to be attached to a
    * resource.
    *
    * @param calendar resource calendar
    * @deprecated use setCalendar
    */
   @Deprecated public void setResourceCalendar(ProjectCalendar calendar)
   {
      setCalendar(calendar);
   }

   /**
    * Set the calendar unique ID.
    *
    * @param id calendar unique ID
    * @deprecated use setCalendarUniqueID
    */
   @Deprecated public void setResourceCalendarUniqueID(Integer id)
   {
      setCalendarUniqueID(id);
   }

   /**
    * Retrieve the calendar unique ID.
    *
    * @return calendar unique ID
    * @deprecated use getCalendarUniqueID
    */
   @Deprecated public Integer getResourceCalendarUniqueID()
   {
      return getCalendarUniqueID();
   }

   /**
    * This method allows a calendar to be added to a resource.
    *
    * @return ResourceCalendar
    * @throws MPXJException if more than one calendar is added
    */
   public ProjectCalendar addCalendar() throws MPXJException
   {
      if (getCalendar() != null)
      {
         throw new MPXJException(MPXJException.MAXIMUM_RECORDS);
      }

      ProjectCalendar calendar = getParentFile().addCalendar();
      String name = getName();
      if (name == null || name.isEmpty())
      {
         name = "Unnamed Resource";
      }

      calendar.setName(name);
      setCalendar(calendar);
      return calendar;
   }

   /**
    * This method allows a calendar to be added to a resource.
    *
    * @return ResourceCalendar
    * @throws MPXJException if more than one calendar is added
    * @deprecated use addCalendar
    */
   @Deprecated public ProjectCalendar addResourceCalendar() throws MPXJException
   {
      return addCalendar();
   }

   /**
    * Sets the Base Calendar field indicates which calendar is the base calendar
    * for a resource calendar. The list includes the three built-in calendars,
    * as well as any new base calendars you have created in the Change Working
    * Time dialog box.
    *
    * @param val calendar name
    */
   public void setBaseCalendar(String val)
   {
      set(ResourceField.BASE_CALENDAR, val == null || val.length() == 0 ? "Standard" : val);
   }

   /**
    * Sets the baseline cost. This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setBaselineCost(Number val)
   {
      set(ResourceField.BASELINE_COST, val);
   }

   /**
    * Sets the baseline work duration. This field is ignored on import into MS
    * Project.
    *
    * @param val - value to be set
    */
   public void setBaselineWork(Duration val)
   {
      set(ResourceField.BASELINE_WORK, val);
   }

   /**
    * Sets ID field value.
    *
    * @param val value
    */
   @Override public void setID(Integer val)
   {
      ProjectFile parent = getParentFile();
      Integer previous = getID();
      if (previous != null)
      {
         parent.getResources().unmapID(previous);
      }
      parent.getResources().mapID(val, this);

      set(ResourceField.ID, val);
   }

   /**
    * This field is ignored on import into MS Project.
    *
    * @param val - value to be set
    */
   public void setLinkedFields(boolean val)
   {
      set(ResourceField.LINKED_FIELDS, val);
   }

   /**
    * Set objects.
    *
    * @param val - value to be set
    */
   public void setObjects(Integer val)
   {
      set(ResourceField.OBJECTS, val);
   }

   /**
    * Set a text value.
    *
    * @param index text index (1-30)
    * @param value text value
    */
   public void setText(int index, String value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_TEXT, index), value);
   }

   /**
    * Retrieve a text value.
    *
    * @param index text index (1-30)
    * @return text value
    */
   public String getText(int index)
   {
      return (String) getCachedValue(selectField(ResourceFieldLists.CUSTOM_TEXT, index));
   }

   /**
    * Sets Unique ID of this resource.
    *
    * @param val Unique ID
    */
   @Override public void setUniqueID(Integer val)
   {
      set(ResourceField.UNIQUE_ID, val);
   }

   /**
    * Sets Parent ID of this resource.
    *
    * @param val Parent ID
    */
   public void setParentID(Integer val)
   {
      set(ResourceField.PARENT_ID, val);
   }

   /**
    * Retrieves Base Calendar name associated with this resource. This field
    * indicates which calendar is the base calendar for a resource calendar.
    *
    * @return Base calendar name
    */
   public String getBaseCalendar()
   {
      return (String) getCachedValue(ResourceField.BASE_CALENDAR);
   }

   /**
    * Retrieves the Baseline Cost value. This value is the total planned cost
    * for a resource for all assigned tasks. Baseline cost is also referred to
    * as budget at completion (BAC).
    *
    * @return Baseline cost value
    */
   public Number getBaselineCost()
   {
      return ((Number) getCachedValue(ResourceField.BASELINE_COST));
   }

   /**
    * Retrieves the Baseline Work value.
    *
    * @return Baseline work value
    */
   public Duration getBaselineWork()
   {
      return ((Duration) getCachedValue(ResourceField.BASELINE_WORK));
   }

   /**
    * Gets ID field value.
    *
    * @return value
    */
   @Override public Integer getID()
   {
      return ((Integer) getCachedValue(ResourceField.ID));
   }

   /**
    * Gets Linked Fields field value.
    *
    * @return value
    */
   public boolean getLinkedFields()
   {
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(ResourceField.LINKED_FIELDS)));
   }

   /**
    * Gets objects field value.
    *
    * @return value
    */
   public Integer getObjects()
   {
      return ((Integer) getCachedValue(ResourceField.OBJECTS));
   }

   /**
    * Gets Unique ID field value.
    *
    * @return value
    */
   @Override public Integer getUniqueID()
   {
      return ((Integer) getCachedValue(ResourceField.UNIQUE_ID));
   }

   /**
    * Gets Parent ID field value.
    *
    * @return value
    */
   public Integer getParentID()
   {
      return (Integer) getCachedValue(ResourceField.PARENT_ID);
   }

   /**
    * Set a start value.
    *
    * @param index start index (1-10)
    * @param value start value
    */
   public void setStart(int index, Date value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_START, index), value);
   }

   /**
    * Retrieve a start value.
    *
    * @param index start index (1-10)
    * @return start value
    */
   public Date getStart(int index)
   {
      return (Date) getCachedValue(selectField(ResourceFieldLists.CUSTOM_START, index));
   }

   /**
    * Set a finish value.
    *
    * @param index finish index (1-10)
    * @param value finish value
    */
   public void setFinish(int index, Date value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_FINISH, index), value);
   }

   /**
    * Retrieve a finish value.
    *
    * @param index finish index (1-10)
    * @return finish value
    */
   public Date getFinish(int index)
   {
      return (Date) getCachedValue(selectField(ResourceFieldLists.CUSTOM_FINISH, index));
   }

   /**
    * Set a number value.
    *
    * @param index number index (1-20)
    * @param value number value
    */
   public void setNumber(int index, Number value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_NUMBER, index), value);
   }

   /**
    * Retrieve a number value.
    *
    * @param index number index (1-20)
    * @return number value
    */
   public Number getNumber(int index)
   {
      return (Number) getCachedValue(selectField(ResourceFieldLists.CUSTOM_NUMBER, index));
   }

   /**
    * Set a duration value.
    *
    * @param index duration index (1-10)
    * @param value duration value
    */
   public void setDuration(int index, Duration value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_DURATION, index), value);
   }

   /**
    * Retrieve a duration value.
    *
    * @param index duration index (1-10)
    * @return duration value
    */
   public Duration getDuration(int index)
   {
      return (Duration) getCachedValue(selectField(ResourceFieldLists.CUSTOM_DURATION, index));
   }

   /**
    * Set a date value.
    *
    * @param index date index (1-10)
    * @param value date value
    */
   public void setDate(int index, Date value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_DATE, index), value);
   }

   /**
    * Retrieve a date value.
    *
    * @param index date index (1-10)
    * @return date value
    */
   public Date getDate(int index)
   {
      return (Date) getCachedValue(selectField(ResourceFieldLists.CUSTOM_DATE, index));
   }

   /**
    * Set a cost value.
    *
    * @param index cost index (1-10)
    * @param value cost value
    */
   public void setCost(int index, Number value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_COST, index), value);
   }

   /**
    * Retrieve a cost value.
    *
    * @param index cost index (1-10)
    * @return cost value
    */
   public Number getCost(int index)
   {
      return (Number) getCachedValue(selectField(ResourceFieldLists.CUSTOM_COST, index));
   }

   /**
    * Set a flag value.
    *
    * @param index flag index (1-20)
    * @param value flag value
    */
   public void setFlag(int index, boolean value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_FLAG, index), value);
   }

   /**
    * Retrieve a flag value.
    *
    * @param index flag index (1-20)
    * @return flag value
    */
   public boolean getFlag(int index)
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(selectField(ResourceFieldLists.CUSTOM_FLAG, index)));
   }

   /**
    * Set an outline code value.
    *
    * @param index outline code index (1-10)
    * @param value outline code value
    */
   public void setOutlineCode(int index, String value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_OUTLINE_CODE, index), value);
   }

   /**
    * Retrieve an outline code value.
    *
    * @param index outline code index (1-10)
    * @return outline code value
    */
   public String getOutlineCode(int index)
   {
      return (String) getCachedValue(selectField(ResourceFieldLists.CUSTOM_OUTLINE_CODE, index));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(1, value)
    */
   @Deprecated public void setOutlineCode1(String value)
   {
      set(ResourceField.OUTLINE_CODE1, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(1)
    */
   @Deprecated public String getOutlineCode1()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE1));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(2, value)
    */
   @Deprecated public void setOutlineCode2(String value)
   {
      set(ResourceField.OUTLINE_CODE2, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(2)
    */
   @Deprecated public String getOutlineCode2()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE2));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(3, value)
    */
   @Deprecated public void setOutlineCode3(String value)
   {
      set(ResourceField.OUTLINE_CODE3, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(3)
    */
   @Deprecated public String getOutlineCode3()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE3));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(4, value)
    */
   @Deprecated public void setOutlineCode4(String value)
   {
      set(ResourceField.OUTLINE_CODE4, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(4)
    */
   @Deprecated public String getOutlineCode4()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE4));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(5, value)
    */
   @Deprecated public void setOutlineCode5(String value)
   {
      set(ResourceField.OUTLINE_CODE5, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(5)
    */
   @Deprecated public String getOutlineCode5()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE5));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(6, value)
    */
   @Deprecated public void setOutlineCode6(String value)
   {
      set(ResourceField.OUTLINE_CODE6, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(6)
    */
   @Deprecated public String getOutlineCode6()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE6));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(7, value)
    */
   @Deprecated public void setOutlineCode7(String value)
   {
      set(ResourceField.OUTLINE_CODE7, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(7)
    */
   @Deprecated public String getOutlineCode7()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE7));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(8, value)
    */
   @Deprecated public void setOutlineCode8(String value)
   {
      set(ResourceField.OUTLINE_CODE8, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(8)
    */
   @Deprecated public String getOutlineCode8()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE8));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(9, value)
    */
   @Deprecated public void setOutlineCode9(String value)
   {
      set(ResourceField.OUTLINE_CODE9, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(9)
    */
   @Deprecated public String getOutlineCode9()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE9));
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    * @deprecated use setOutlineCode(10, value)
    */
   @Deprecated public void setOutlineCode10(String value)
   {
      set(ResourceField.OUTLINE_CODE10, value);
   }

   /**
    * Retrieves the value of an outline code field.
    *
    * @return outline code value
    * @deprecated use getOutlineCode(10)
    */
   @Deprecated public String getOutlineCode10()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE10));
   }

   /**
    * Removes this resource from the project.
    */
   public void remove()
   {
      getParentFile().removeResource(this);
   }

   /**
    * Retrieve the value of a field using its alias.
    *
    * @param alias field alias
    * @return field value
    */
   public Object getFieldByAlias(String alias)
   {
      return (getCachedValue(getParentFile().getCustomFields().getFieldByAlias(FieldTypeClass.RESOURCE, alias)));
   }

   /**
    * Set the value of a field using its alias.
    *
    * @param alias field alias
    * @param value field value
    */
   public void setFieldByAlias(String alias, Object value)
   {
      set(getParentFile().getCustomFields().getFieldByAlias(FieldTypeClass.RESOURCE, alias), value);
   }

   /**
    * This method is used internally within MPXJ to track tasks which are
    * assigned to a particular resource.
    *
    * @param assignment resource assignment instance
    */
   public void addResourceAssignment(ResourceAssignment assignment)
   {
      m_assignments.add(assignment);
   }

   /**
    * Internal method used as part of the process of removing a resource
    * assignment.
    *
    * @param assignment resource assignment to be removed
    */
   void removeResourceAssignment(ResourceAssignment assignment)
   {
      m_assignments.remove(assignment);
   }

   /**
    * Retrieve a list of tasks assigned to this resource. Note that if this
    * project data has been read from an MPX file which declared some or all of
    * the resources assignments before the tasks and resources to which the
    * assignments relate, then these assignments may not appear in this list.
    * Caveat emptor!
    *
    * @return list of tasks assigned to this resource
    */
   public List<ResourceAssignment> getTaskAssignments()
   {
      return (m_assignments);
   }

   /**
    * Where a resource in an MPP file represents a resource from a subproject,
    * this value will be non-zero. The value itself is the unique ID value shown
    * in the parent project. To retrieve the value of the resource unique ID in
    * the child project, remove the top two bytes:
    * <p>
    * resourceID = (subprojectUniqueID &amp; 0xFFFF)
    *
    * @return sub project unique resource ID
    */
   public Integer getSubprojectResourceUniqueID()
   {
      return ((Integer) getCachedValue(ResourceField.SUBPROJECT_RESOURCE_UNIQUE_ID));
   }

   /**
    * Sets the sub project unique resource ID.
    *
    * @param subprojectUniqueResourceID subproject unique resource ID
    */
   public void setSubprojectResourceUniqueID(Integer subprojectUniqueResourceID)
   {
      set(ResourceField.SUBPROJECT_RESOURCE_UNIQUE_ID, subprojectUniqueResourceID);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public Number getEnterpriseCost(int index)
   {
      return ((Number) getCachedValue(selectField(ResourceFieldLists.ENTERPRISE_COST, index)));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseCost(int index, Number value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_COST, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public Date getEnterpriseDate(int index)
   {
      return ((Date) getCachedValue(selectField(ResourceFieldLists.ENTERPRISE_DATE, index)));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDate(int index, Date value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_DATE, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public Duration getEnterpriseDuration(int index)
   {
      return ((Duration) getCachedValue(selectField(ResourceFieldLists.ENTERPRISE_DURATION, index)));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDuration(int index, Duration value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_DURATION, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public boolean getEnterpriseFlag(int index)
   {
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(selectField(ResourceFieldLists.ENTERPRISE_FLAG, index))));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseFlag(int index, boolean value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_FLAG, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public Number getEnterpriseNumber(int index)
   {
      return ((Number) getCachedValue(selectField(ResourceFieldLists.ENTERPRISE_NUMBER, index)));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseNumber(int index, Number value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_NUMBER, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public String getEnterpriseText(int index)
   {
      return ((String) getCachedValue(selectField(ResourceFieldLists.ENTERPRISE_TEXT, index)));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseText(int index, String value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_TEXT, index), value);
   }

   /**
    * Retrieve an enterprise custom field value.
    *
    * @param index field index
    * @return field value
    */
   public Object getEnterpriseCustomField(int index)
   {
      return getCachedValue(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_FIELD, index));
   }

   /**
    * Set an enterprise custom field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseCustomField(int index, byte[] value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_FIELD, index), value);
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineCost(int baselineNumber, Number value)
   {
      set(selectField(ResourceFieldLists.BASELINE_COSTS, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineWork(int baselineNumber, Duration value)
   {
      set(selectField(ResourceFieldLists.BASELINE_WORKS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Number getBaselineCost(int baselineNumber)
   {
      return ((Number) getCachedValue(selectField(ResourceFieldLists.BASELINE_COSTS, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Duration getBaselineWork(int baselineNumber)
   {
      return ((Duration) getCachedValue(selectField(ResourceFieldLists.BASELINE_WORKS, baselineNumber)));
   }

   /**
    * Retrieve the budget flag.
    *
    * @return budget flag
    */
   public boolean getBudget()
   {
      return (BooleanHelper.getBoolean((Boolean) getCachedValue(ResourceField.BUDGET)));
   }

   /**
    * Set the budget flag.
    *
    * @param budget budget flag
    */
   public void setBudget(boolean budget)
   {
      set(ResourceField.BUDGET, budget);
   }

   /**
    * Retrieves the resource GUID.
    *
    * @return resource GUID.
    */
   public UUID getGUID()
   {
      return (UUID) getCachedValue(ResourceField.GUID);
   }

   /**
    * Set the unit field.
    *
    * @param value unit value
    */
   public void setUnit(String value)
   {
      set(ResourceField.UNIT, value);
   }

   /**
    * Retrieve the unit field.
    *
    * @return unit value
    */
   public String getUnit()
   {
      return (String) getCachedValue(ResourceField.UNIT);
   }

   /**
    * Set the supply reference field.
    *
    * @param value supply reference value
    */
   public void setSupplyReference(String value)
   {
      set(ResourceField.SUPPLY_REFERENCE, value);
   }

   /**
    * Retrieve the supply reference field.
    *
    * @return supply reference value
    */
   public String getSupplyReference()
   {
      return (String) getCachedValue(ResourceField.SUPPLY_REFERENCE);
   }

   /**
    * Set the description field.
    *
    * @param value description field
    */
   public void setDescription(String value)
   {
      set(ResourceField.DESCRIPTION, value);
   }

   /**
    * Retrieve the description field.
    *
    * @return description value
    */
   public String getDescription()
   {
      return (String) getCachedValue(ResourceField.DESCRIPTION);
   }

   /**
    * Set the resource ID field.
    *
    * @param value resource ID value
    */
   public void setResourceID(String value)
   {
      set(ResourceField.RESOURCE_ID, value);
   }

   /**
    * Retrieve the resource ID field.
    *
    * @return resource ID value
    */
   public String getResourceID()
   {
      return (String) getCachedValue(ResourceField.RESOURCE_ID);
   }

   /**
    * Set the modify on integrate field.
    *
    * @param value modify on integrate value
    */
   public void setModifyOnIntegrate(Boolean value)
   {
      set(ResourceField.MODIFY_ON_INTEGRATE, value);
   }

   /**
    * Retrieve the modify on integrate value.
    *
    * @return modify on integrate value
    */
   public Boolean getModifyOnIntegrate()
   {
      return (Boolean) getCachedValue(ResourceField.MODIFY_ON_INTEGRATE);
   }

   /**
    * Set the expenses only field.
    *
    * @param value expenses only value
    */
   public void setExpensesOnly(Boolean value)
   {
      set(ResourceField.EXPENSES_ONLY, value);
   }

   /**
    * Retrieve the expenses only field.
    *
    * @return expenses only value
    */
   public Boolean getExpensesOnly()
   {
      return (Boolean) getCachedValue(ResourceField.EXPENSES_ONLY);
   }

   /**
    * Set the period dur field.
    *
    * @param value period dur value
    */
   public void setPeriodDur(Number value)
   {
      set(ResourceField.PERIOD_DUR, value);
   }

   /**
    * Retrieve the period dur field.
    *
    * @return period dur value
    */
   public Number getPeriodDur()
   {
      return (Number) getCachedValue(ResourceField.PERIOD_DUR);
   }

   /**
    * Set the priority field.
    *
    * @param value priority value
    */
   public void setPriority(Number value)
   {
      set(ResourceField.PRIORITY, value);
   }

   /**
    * Retrieve the priority field.
    *
    * @return priority value
    */
   public Number getPriority()
   {
      return (Number) getCachedValue(ResourceField.PRIORITY);
   }

   /**
    * Set the rate field.
    *
    * @param value rate value
    */
   public void setRate(Number value)
   {
      set(ResourceField.RATE, value);
   }

   /**
    * Retrieve the rate field.
    *
    * @return rate value
    */
   public Number getRate()
   {
      return (Number) getCachedValue(ResourceField.RATE);
   }

   /**
    * Set the pool field.
    *
    * @param value pool value
    */
   public void setPool(Number value)
   {
      set(ResourceField.POOL, value);
   }

   /**
    * Retrieve the pool field.
    *
    * @return pool value
    */
   public Number getPool()
   {
      return (Number) getCachedValue(ResourceField.POOL);
   }

   /**
    * Set the per day field.
    *
    * @param value per day value
    */
   public void setPerDay(Number value)
   {
      set(ResourceField.PER_DAY, value);
   }

   /**
    * Retrieve the per day field.
    *
    * @return per day value
    */
   public Number getPerDay()
   {
      return (Number) getCachedValue(ResourceField.PER_DAY);
   }

   /**
    * Set the phone field.
    *
    * @param value phone value
    */
   public void setPhone(String value)
   {
      set(ResourceField.PHONE, value);
   }

   /**
    * Retrieve the phone field.
    *
    * @return phone value
    */
   public String getPhone()
   {
      return (String) getCachedValue(ResourceField.PHONE);
   }

   /**
    * Set the role field.
    *
    * @param value role value
    */
   public void setRole(Boolean value)
   {
      set(ResourceField.ROLE, value);
   }

   /**
    * Retrieve the role field.
    * Returns true if this object represents a role rather than an individual resource.
    *
    * @return role value
    */
   public Boolean getRole()
   {
      return (Boolean) getCachedValue(ResourceField.ROLE);
   }

   /**
    * Sets the resource GUID.
    *
    * @param value resource GUID
    */
   public void setGUID(UUID value)
   {
      set(ResourceField.GUID, value);
   }

   /**
    * Associates a complete cost rate table with the
    * current resource. Note that the index corresponds with the
    * letter label used by MS Project to identify each table.
    * For example 0=Table A, 1=Table B, 2=Table C, and so on.
    *
    * @param index table index
    * @param crt table instance
    */
   public void setCostRateTable(int index, CostRateTable crt)
   {
      m_costRateTables[index] = crt;
   }

   /**
    * Retrieves a cost rate table associated with a resource.
    * Note that the index corresponds with the
    * letter label used by MS Project to identify each table.
    * For example 0=Table A, 1=Table B, 2=Table C, and so on.
    *
    * @param index table index
    * @return table instance
    */
   public CostRateTable getCostRateTable(int index)
   {
      return index < 0 || index >= CostRateTable.MAX_TABLES ? null : m_costRateTables[index];
   }

   /**
    * Retrieve the cost rate table entry effective for the current date.
    *
    * @param costRateTable cost rate table index
    * @return cost rate table entry
    */
   public CostRateTableEntry getCurrentCostRateTableEntry(int costRateTable)
   {
      return getCostRateTable(costRateTable).getEntryByDate(new Date());
   }

   /**
    * Retrieve the availability table for this resource.
    *
    * @return availability table
    */
   public AvailabilityTable getAvailability()
   {
      return m_availability;
   }

   /**
    * Maps a field index to a ResourceField instance.
    *
    * @param fields array of fields used as the basis for the mapping.
    * @param index required field index
    * @return ResourceField instance
    */
   private ResourceField selectField(ResourceField[] fields, int index)
   {
      if (index < 1 || index > fields.length)
      {
         throw new IllegalArgumentException(index + " is not a valid field index");
      }
      return (fields[index - 1]);
   }

   @Override public Object getCachedValue(FieldType field)
   {
      return (field == null ? null : m_array[field.getValue()]);
   }

   @Override public Object getCurrentValue(FieldType field)
   {
      Object result = null;

      if (field != null)
      {
         ResourceField resourceField = (ResourceField) field;

         switch (resourceField)
         {
            case COST_VARIANCE:
            {
               result = getCostVariance();
               break;
            }

            case WORK_VARIANCE:
            {
               result = getWorkVariance();
               break;
            }

            case CV:
            {
               result = getCV();
               break;
            }

            case SV:
            {
               result = getSV();
               break;
            }

            case OVERALLOCATED:
            {
               result = Boolean.valueOf(getOverAllocated());
               break;
            }

            case STANDARD_RATE:
            {
               result = getStandardRate();
               break;
            }

            case OVERTIME_RATE:
            {
               result = getOvertimeRate();
               break;
            }

            case COST_PER_USE:
            {
               result = getCostPerUse();
               break;
            }

            default:
            {
               result = m_array[field.getValue()];
               break;
            }
         }
      }

      return (result);
   }

   @Override public void set(FieldType field, Object value)
   {
      if (field != null)
      {
         int index = field.getValue();
         if (m_eventsEnabled)
         {
            fireFieldChangeEvent((ResourceField) field, m_array[index], value);
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
   private void fireFieldChangeEvent(ResourceField field, Object oldValue, Object newValue)
   {
      //
      // Internal event handling
      //
      switch (field)
      {
         case UNIQUE_ID:
         {
            getParentFile().getResources().clearUniqueIDMap();

            if (!m_assignments.isEmpty())
            {
               for (ResourceAssignment assignment : m_assignments)
               {
                  assignment.setResourceUniqueID((Integer) newValue);
               }
            }
            break;
         }

         case COST:
         case BASELINE_COST:
         {
            m_array[ResourceField.COST_VARIANCE.getValue()] = null;
            break;
         }

         case WORK:
         case BASELINE_WORK:
         {
            m_array[ResourceField.WORK_VARIANCE.getValue()] = null;
            break;
         }

         case BCWP:
         case ACWP:
         {
            m_array[ResourceField.CV.getValue()] = null;
            m_array[ResourceField.SV.getValue()] = null;
            break;
         }

         case BCWS:
         {
            m_array[ResourceField.SV.getValue()] = null;
            break;
         }

         case PEAK:
         case MAX_UNITS:
         {
            m_array[ResourceField.OVERALLOCATED.getValue()] = null;
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

   @Override public void addFieldListener(FieldListener listener)
   {
      if (m_listeners == null)
      {
         m_listeners = new ArrayList<>();
      }
      m_listeners.add(listener);
   }

   @Override public void removeFieldListener(FieldListener listener)
   {
      if (m_listeners != null)
      {
         m_listeners.remove(listener);
      }
   }

   /**
    * This method is used to set the value of a field in the resource.
    *
    * @param field field to be set
    * @param value new value for field.
    */
   private void set(FieldType field, boolean value)
   {
      set(field, (value ? Boolean.TRUE : Boolean.FALSE));
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
    * This method implements the only method in the Comparable interface. This
    * allows Resources to be compared and sorted based on their ID value. Note
    * that if the MPX/MPP file has been generated by MSP, the ID value will
    * always be in the correct sequence. The Unique ID value will not
    * necessarily be in the correct sequence as task insertions and deletions
    * will change the order.
    *
    * @param o object to compare this instance with
    * @return result of comparison
    */
   @Override public int compareTo(Resource o)
   {
      int id1 = NumberHelper.getInt(getID());
      int id2 = NumberHelper.getInt(o.getID());
      return (Integer.compare(id1, id2));
   }

   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof Resource)
      {
         result = (compareTo((Resource) o) == 0);
      }
      return result;
   }

   @Override public int hashCode()
   {
      return (NumberHelper.getInt(getID()));
   }

   @Override public String toString()
   {
      return ("[Resource id=" + getID() + " uniqueID=" + getUniqueID() + " name=" + getName() + "]");
   }

   /**
    * Array of field values.
    */
   private final Object[] m_array = new Object[ResourceField.MAX_VALUE];

   /**
    * List of all assignments for this resource.
    */
   private final List<ResourceAssignment> m_assignments = new ArrayList<>();

   private boolean m_eventsEnabled = true;
   private boolean m_null;
   private String m_activeDirectoryGUID;

   private final CostRateTable[] m_costRateTables;
   private final AvailabilityTable m_availability = new AvailabilityTable();
   private List<FieldListener> m_listeners;
}
