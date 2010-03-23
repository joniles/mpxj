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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.sf.mpxj.listener.FieldListener;
import net.sf.mpxj.utility.BooleanUtility;
import net.sf.mpxj.utility.DateUtility;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class represents the Resource record as found in an MPX file.
 */
public final class Resource extends ProjectEntity implements Comparable<Resource>, FieldContainer
{
   /**
    * Default constructor.
    * 
    * @param file the parent file to which this record belongs.
    */
   Resource(ProjectFile file)
   {
      super(file);

      if (file.getAutoResourceUniqueID() == true)
      {
         setUniqueID(Integer.valueOf(file.getResourceUniqueID()));
      }

      if (file.getAutoResourceID() == true)
      {
         setID(Integer.valueOf(file.getResourceID()));
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
      m_ntAccount = ntAccount;
   }

   /**
    * Retrieves the Windows account name for a resource.
    * 
    * @return windows account name
    */
   public String getNtAccount()
   {
      return (m_ntAccount);
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
         overallocated = Boolean.valueOf(NumberUtility.getDouble(peakUnits) > NumberUtility.getDouble(maxUnits));
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
         if (result == null || DateUtility.compare(result, assignment.getStart()) > 0)
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
         if (result == null || DateUtility.compare(result, assignment.getFinish()) < 0)
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
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.CAN_LEVEL)));
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
    * to the cost of a task. The options are: Start, End and Proraetd (Default)
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
    */
   public void setStandardRate(Rate val)
   {
      set(ResourceField.STANDARD_RATE, val);
   }

   /**
    * Gets Standard Rate field value.
    * 
    * @return Rate
    */
   public Rate getStandardRate()
   {
      return ((Rate) getCachedValue(ResourceField.STANDARD_RATE));
   }

   /**
    * Sets the format of the standard rate.
    * 
    * @param format standard rate format
    */
   public void setStandardRateFormat(TimeUnit format)
   {
      m_standardRateFormat = format;
   }

   /**
    * Retrieves the format of the standard rate.
    * 
    * @return standard rate format
    */
   public TimeUnit getStandardRateFormat()
   {
      return (m_standardRateFormat);
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
    */
   public void setOvertimeRate(Rate overtimeRate)
   {
      set(ResourceField.OVERTIME_RATE, overtimeRate);
   }

   /**
    * Retrieves the overtime rate for this resource.
    * 
    * @return overtime rate
    */
   public Rate getOvertimeRate()
   {
      return ((Rate) getCachedValue(ResourceField.OVERTIME_RATE));
   }

   /**
    * Sets the format of the overtime rate.
    * 
    * @param format overtime rate format
    */
   public void setOvertimeRateFormat(TimeUnit format)
   {
      m_overtimeRateFormat = format;
   }

   /**
    * Retrieves the format of the overtime rate.
    * 
    * @return overtime rate format
    */
   public TimeUnit getOvertimeRateFormat()
   {
      return (m_overtimeRateFormat);
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
    */
   public void setCostPerUse(Number costPerUse)
   {
      set(ResourceField.COST_PER_USE, costPerUse);
   }

   /**
    * Retrieve the cost per use.
    * 
    * @return cost per use
    */
   public Number getCostPerUse()
   {
      return ((Number) getCachedValue(ResourceField.COST_PER_USE));
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
            variance = Duration.getInstance(work.getDuration() - baselineWork.convertUnits(work.getUnits(), getParentFile().getProjectHeader()).getDuration(), work.getUnits());
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
            variance = NumberUtility.getDouble(cost.doubleValue() - baselineCost.doubleValue());
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
            variance = NumberUtility.getDouble(bcwp.doubleValue() - bcws.doubleValue());
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
         variance = Double.valueOf(NumberUtility.getDouble(getBCWP()) - NumberUtility.getDouble(getACWP()));
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
      set(ResourceField.NOTES, notes);
   }

   /**
    * Retrieves the notes text for this resource.
    * 
    * @return notes text
    */
   public String getNotes()
   {
      String notes = (String) getCachedValue(ResourceField.NOTES);
      return (notes == null ? "" : notes);
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
    * Retrievesthe budgeted cost of work performed.
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
    * @param isGeneric generic flag
    */
   public void setIsGeneric(boolean isGeneric)
   {
      m_generic = isGeneric;
   }

   /**
    * Retrieves the generic flag.
    * 
    * @return generic flag
    */
   public boolean getGeneric()
   {
      return (m_generic);
   }

   /**
    * Sets the inactive flag.
    * 
    * @param isInactive inactive flag
    */
   public void setIsInactive(boolean isInactive)
   {
      m_inactive = isInactive;
   }

   /**
    * Retrieves the inactive flag.
    * 
    * @return inactive flag
    */
   public boolean getInactive()
   {
      return (m_inactive);
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
      m_actualOvertimeWorkProtected = duration;
   }

   /**
    * Retrieves the actual overtime work protected duration.
    * 
    * @return actual overtime work protected
    */
   public Duration getActualOvertimeWorkProtected()
   {
      return (m_actualOvertimeWorkProtected);
   }

   /**
    * Sets the actual work protected duration.
    * 
    * @param duration actual work protected
    */
   public void setActualWorkProtected(Duration duration)
   {
      m_actualWorkProtected = duration;
   }

   /**
    * Retrieves the actual work protected duration.
    * 
    * @return actual work protected
    */
   public Duration getActualWorkProtected()
   {
      return (m_actualWorkProtected);
   }

   /**
    * Sets the booking type.
    * 
    * @param bookingType booking type
    */
   public void setBookingType(BookingType bookingType)
   {
      m_bookingType = bookingType;
   }

   /**
    * Retrieves the booking type.
    * 
    * @return booking type
    */
   public BookingType getBookingType()
   {
      return (m_bookingType);
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
   public void setIsEnterprise(boolean enterprise)
   {
      m_enterprise = enterprise;
   }

   /**
    * Retrieves a flag indicating that a resource is an enterprise resource.
    * 
    * @return boolean flag
    */
   public boolean getEnterprise()
   {
      return (m_enterprise);
   }

   /**
    * This method retrieves the calendar associated with this resource.
    * 
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getResourceCalendar()
   {
      return (m_calendar);
   }

   /**
    * This method allows a pre-existing resource calendar to be attached to a
    * resource.
    * 
    * @param calendar resource calendar
    */
   public void setResourceCalendar(ProjectCalendar calendar)
   {
      m_calendar = calendar;
      if (calendar != null)
      {
         calendar.setResource(this);
      }
   }

   /**
    * This method allows a resource calendar to be added to a resource.
    * 
    * @return ResourceCalendar
    * @throws MPXJException if more than one calendar is added
    */
   public ProjectCalendar addResourceCalendar() throws MPXJException
   {
      if (m_calendar != null)
      {
         throw new MPXJException(MPXJException.MAXIMUM_RECORDS);
      }

      m_calendar = new ProjectCalendar(getParentFile());
      m_calendar.setResource(this);
      return (m_calendar);
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
   public void setID(Integer val)
   {
      ProjectFile parent = getParentFile();
      Integer previous = getID();
      if (previous != null)
      {
         parent.unmapResourceID(previous);
      }
      parent.mapResourceID(val, this);

      set(ResourceField.ID, val);
   }

   /**
    * This field is ignored on import into MS Project.
    * 
    * @param val - value to be set
    */
   public void setLinkedFields(String val)
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
    * Additional text.
    * 
    * @param val text to set
    */
   public void setText1(String val)
   {
      set(ResourceField.TEXT1, val);
   }

   /**
    * Additional text.
    * 
    * @param val text to set
    */
   public void setText2(String val)
   {
      set(ResourceField.TEXT2, val);
   }

   /**
    * Additional text.
    * 
    * @param val text to set
    */
   public void setText3(String val)
   {
      set(ResourceField.TEXT3, val);
   }

   /**
    * Additional text.
    * 
    * @param val text to set
    */
   public void setText4(String val)
   {
      set(ResourceField.TEXT4, val);
   }

   /**
    * Additional text.
    * 
    * @param val text to set
    */
   public void setText5(String val)
   {
      set(ResourceField.TEXT5, val);
   }

   /**
    * Sets Unique ID of this resource.
    * 
    * @param val Unique ID
    */
   public void setUniqueID(Integer val)
   {
      ProjectFile parent = getParentFile();
      Integer previous = getUniqueID();
      if (previous != null)
      {
         parent.unmapResourceUniqueID(previous);
      }
      parent.mapResourceUniqueID(val, this);

      set(ResourceField.UNIQUE_ID, val);

      if (m_assignments.isEmpty() == false)
      {
         for (ResourceAssignment assignment : m_assignments)
         {
            assignment.setResourceUniqueID(val);
         }
      }
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
   public Integer getID()
   {
      return ((Integer) getCachedValue(ResourceField.ID));
   }

   /**
    * Gets Linked Fields field value.
    * 
    * @return value
    */
   public String getLinkedFields()
   {
      return ((String) getCachedValue(ResourceField.LINKED_FIELDS));
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
    * Gets Text 1 field value.
    * 
    * @return value
    */
   public String getText1()
   {
      return ((String) getCachedValue(ResourceField.TEXT1));
   }

   /**
    * Gets Text 2 field value.
    * 
    * @return value
    */
   public String getText2()
   {
      return ((String) getCachedValue(ResourceField.TEXT2));
   }

   /**
    * Gets Text3 field value.
    * 
    * @return value
    */
   public String getText3()
   {
      return ((String) getCachedValue(ResourceField.TEXT3));
   }

   /**
    * Gets Text 4 field value.
    * 
    * @return value
    */
   public String getText4()
   {
      return ((String) getCachedValue(ResourceField.TEXT4));
   }

   /**
    * Gets Text 5 field value.
    * 
    * @return value
    */
   public String getText5()
   {
      return ((String) getCachedValue(ResourceField.TEXT5));
   }

   /**
    * Gets Unique ID field value.
    * 
    * @return value
    */
   public Integer getUniqueID()
   {
      return ((Integer) getCachedValue(ResourceField.UNIQUE_ID));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText6()
   {
      return ((String) getCachedValue(ResourceField.TEXT6));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText7()
   {
      return ((String) getCachedValue(ResourceField.TEXT7));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText8()
   {
      return ((String) getCachedValue(ResourceField.TEXT8));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText9()
   {
      return ((String) getCachedValue(ResourceField.TEXT9));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText10()
   {
      return ((String) getCachedValue(ResourceField.TEXT10));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText11()
   {
      return ((String) getCachedValue(ResourceField.TEXT11));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText12()
   {
      return ((String) getCachedValue(ResourceField.TEXT12));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText13()
   {
      return ((String) getCachedValue(ResourceField.TEXT13));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText14()
   {
      return ((String) getCachedValue(ResourceField.TEXT14));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText15()
   {
      return ((String) getCachedValue(ResourceField.TEXT15));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText16()
   {
      return ((String) getCachedValue(ResourceField.TEXT16));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText17()
   {
      return ((String) getCachedValue(ResourceField.TEXT17));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText18()
   {
      return ((String) getCachedValue(ResourceField.TEXT18));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText19()
   {
      return ((String) getCachedValue(ResourceField.TEXT19));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText20()
   {
      return ((String) getCachedValue(ResourceField.TEXT20));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText21()
   {
      return ((String) getCachedValue(ResourceField.TEXT21));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText22()
   {
      return ((String) getCachedValue(ResourceField.TEXT22));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText23()
   {
      return ((String) getCachedValue(ResourceField.TEXT23));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText24()
   {
      return ((String) getCachedValue(ResourceField.TEXT24));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText25()
   {
      return ((String) getCachedValue(ResourceField.TEXT25));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText26()
   {
      return ((String) getCachedValue(ResourceField.TEXT26));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText27()
   {
      return ((String) getCachedValue(ResourceField.TEXT27));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText28()
   {
      return ((String) getCachedValue(ResourceField.TEXT28));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText29()
   {
      return ((String) getCachedValue(ResourceField.TEXT29));
   }

   /**
    * Retrieves a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Text value
    */
   public String getText30()
   {
      return ((String) getCachedValue(ResourceField.TEXT30));
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText6(String string)
   {
      set(ResourceField.TEXT6, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText7(String string)
   {
      set(ResourceField.TEXT7, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText8(String string)
   {
      set(ResourceField.TEXT8, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText9(String string)
   {
      set(ResourceField.TEXT9, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText10(String string)
   {
      set(ResourceField.TEXT10, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText11(String string)
   {
      set(ResourceField.TEXT11, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText12(String string)
   {
      set(ResourceField.TEXT12, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText13(String string)
   {
      set(ResourceField.TEXT13, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText14(String string)
   {
      set(ResourceField.TEXT14, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText15(String string)
   {
      set(ResourceField.TEXT15, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText16(String string)
   {
      set(ResourceField.TEXT16, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText17(String string)
   {
      set(ResourceField.TEXT17, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText18(String string)
   {
      set(ResourceField.TEXT18, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText19(String string)
   {
      set(ResourceField.TEXT19, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText20(String string)
   {
      set(ResourceField.TEXT20, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText21(String string)
   {
      set(ResourceField.TEXT21, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText22(String string)
   {
      set(ResourceField.TEXT22, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText23(String string)
   {
      set(ResourceField.TEXT23, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText24(String string)
   {
      set(ResourceField.TEXT24, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText25(String string)
   {
      set(ResourceField.TEXT25, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText26(String string)
   {
      set(ResourceField.TEXT26, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText27(String string)
   {
      set(ResourceField.TEXT27, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText28(String string)
   {
      set(ResourceField.TEXT28, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText29(String string)
   {
      set(ResourceField.TEXT29, string);
   }

   /**
    * Sets a text value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param string Text value
    */
   public void setText30(String string)
   {
      set(ResourceField.TEXT30, string);
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart1()
   {
      return ((Date) getCachedValue(ResourceField.START1));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart2()
   {
      return ((Date) getCachedValue(ResourceField.START2));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart3()
   {
      return ((Date) getCachedValue(ResourceField.START3));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart4()
   {
      return ((Date) getCachedValue(ResourceField.START4));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart5()
   {
      return ((Date) getCachedValue(ResourceField.START5));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart6()
   {
      return ((Date) getCachedValue(ResourceField.START6));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart7()
   {
      return ((Date) getCachedValue(ResourceField.START7));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart8()
   {
      return ((Date) getCachedValue(ResourceField.START8));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart9()
   {
      return ((Date) getCachedValue(ResourceField.START9));
   }

   /**
    * Retrieves a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date start date
    */
   public Date getStart10()
   {
      return ((Date) getCachedValue(ResourceField.START10));
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart1(Date date)
   {
      set(ResourceField.START1, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart2(Date date)
   {
      set(ResourceField.START2, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart3(Date date)
   {
      set(ResourceField.START3, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart4(Date date)
   {
      set(ResourceField.START4, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart5(Date date)
   {
      set(ResourceField.START5, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart6(Date date)
   {
      set(ResourceField.START6, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart7(Date date)
   {
      set(ResourceField.START7, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart8(Date date)
   {
      set(ResourceField.START8, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart9(Date date)
   {
      set(ResourceField.START9, date);
   }

   /**
    * Sets a start date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Start date
    */
   public void setStart10(Date date)
   {
      set(ResourceField.START10, date);
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish1()
   {
      return ((Date) getCachedValue(ResourceField.FINISH1));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish2()
   {
      return ((Date) getCachedValue(ResourceField.FINISH2));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish3()
   {
      return ((Date) getCachedValue(ResourceField.FINISH3));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish4()
   {
      return ((Date) getCachedValue(ResourceField.FINISH4));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish5()
   {
      return ((Date) getCachedValue(ResourceField.FINISH5));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish6()
   {
      return ((Date) getCachedValue(ResourceField.FINISH6));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish7()
   {
      return ((Date) getCachedValue(ResourceField.FINISH7));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish8()
   {
      return ((Date) getCachedValue(ResourceField.FINISH8));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish9()
   {
      return ((Date) getCachedValue(ResourceField.FINISH9));
   }

   /**
    * Retrieves a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date finish date
    */
   public Date getFinish10()
   {
      return ((Date) getCachedValue(ResourceField.FINISH10));
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish1(Date date)
   {
      set(ResourceField.FINISH1, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish2(Date date)
   {
      set(ResourceField.FINISH2, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish3(Date date)
   {
      set(ResourceField.FINISH3, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish4(Date date)
   {
      set(ResourceField.FINISH4, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish5(Date date)
   {
      set(ResourceField.FINISH5, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish6(Date date)
   {
      set(ResourceField.FINISH6, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish7(Date date)
   {
      set(ResourceField.FINISH7, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish8(Date date)
   {
      set(ResourceField.FINISH8, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish9(Date date)
   {
      set(ResourceField.FINISH9, date);
   }

   /**
    * Sets a finish date. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Finish date
    */
   public void setFinish10(Date date)
   {
      set(ResourceField.FINISH10, date);
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber1(Number val)
   {
      set(ResourceField.NUMBER1, val);
   }

   /**
    * Retrieves a numeric value Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber1()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER1));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber2(Number val)
   {
      set(ResourceField.NUMBER2, val);
   }

   /**
    * Retrieves a numeric value Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber2()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER2));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber3(Number val)
   {
      set(ResourceField.NUMBER3, val);
   }

   /**
    * Retrieves a numeric value Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber3()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER3));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber4(Number val)
   {
      set(ResourceField.NUMBER4, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber4()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER4));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber5(Number val)
   {
      set(ResourceField.NUMBER5, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber5()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER5));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber6(Number val)
   {
      set(ResourceField.NUMBER6, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber6()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER6));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber7(Number val)
   {
      set(ResourceField.NUMBER7, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber7()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER7));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber8(Number val)
   {
      set(ResourceField.NUMBER8, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber8()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER8));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber9(Number val)
   {
      set(ResourceField.NUMBER9, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber9()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER9));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber10(Number val)
   {
      set(ResourceField.NUMBER10, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber10()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER10));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber11(Number val)
   {
      set(ResourceField.NUMBER11, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber11()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER11));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber12(Number val)
   {
      set(ResourceField.NUMBER12, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber12()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER12));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber13(Number val)
   {
      set(ResourceField.NUMBER13, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber13()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER13));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber14(Number val)
   {
      set(ResourceField.NUMBER14, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber14()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER14));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber15(Number val)
   {
      set(ResourceField.NUMBER15, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber15()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER15));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber16(Number val)
   {
      set(ResourceField.NUMBER16, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber16()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER16));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber17(Number val)
   {
      set(ResourceField.NUMBER17, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber17()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER17));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber18(Number val)
   {
      set(ResourceField.NUMBER18, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber18()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER18));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber19(Number val)
   {
      set(ResourceField.NUMBER19, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber19()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER19));
   }

   /**
    * Sets a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param val Numeric value
    */
   public void setNumber20(Number val)
   {
      set(ResourceField.NUMBER20, val);
   }

   /**
    * Retrieves a numeric value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Numeric value
    */
   public Number getNumber20()
   {
      return ((Number) getCachedValue(ResourceField.NUMBER20));
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration1()
   {
      return (Duration) getCachedValue(ResourceField.DURATION1);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration2()
   {
      return (Duration) getCachedValue(ResourceField.DURATION2);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration3()
   {
      return (Duration) getCachedValue(ResourceField.DURATION3);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration4()
   {
      return (Duration) getCachedValue(ResourceField.DURATION4);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration5()
   {
      return (Duration) getCachedValue(ResourceField.DURATION5);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration6()
   {
      return (Duration) getCachedValue(ResourceField.DURATION6);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration7()
   {
      return (Duration) getCachedValue(ResourceField.DURATION7);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration8()
   {
      return (Duration) getCachedValue(ResourceField.DURATION8);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration9()
   {
      return (Duration) getCachedValue(ResourceField.DURATION9);
   }

   /**
    * Retrieves a duration. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Duration
    */
   public Duration getDuration10()
   {
      return (Duration) getCachedValue(ResourceField.DURATION10);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration1(Duration duration)
   {
      set(ResourceField.DURATION1, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration2(Duration duration)
   {
      set(ResourceField.DURATION2, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration3(Duration duration)
   {
      set(ResourceField.DURATION3, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration4(Duration duration)
   {
      set(ResourceField.DURATION4, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration5(Duration duration)
   {
      set(ResourceField.DURATION5, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration6(Duration duration)
   {
      set(ResourceField.DURATION6, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration7(Duration duration)
   {
      set(ResourceField.DURATION7, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration8(Duration duration)
   {
      set(ResourceField.DURATION8, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration9(Duration duration)
   {
      set(ResourceField.DURATION9, duration);
   }

   /**
    * Sets a duration value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param duration Duration value
    */
   public void setDuration10(Duration duration)
   {
      set(ResourceField.DURATION10, duration);
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate1()
   {
      return ((Date) getCachedValue(ResourceField.DATE1));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate10()
   {
      return ((Date) getCachedValue(ResourceField.DATE10));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate2()
   {
      return ((Date) getCachedValue(ResourceField.DATE2));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate3()
   {
      return ((Date) getCachedValue(ResourceField.DATE3));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate4()
   {
      return ((Date) getCachedValue(ResourceField.DATE4));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate5()
   {
      return ((Date) getCachedValue(ResourceField.DATE5));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate6()
   {
      return ((Date) getCachedValue(ResourceField.DATE6));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate7()
   {
      return ((Date) getCachedValue(ResourceField.DATE7));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate8()
   {
      return ((Date) getCachedValue(ResourceField.DATE8));
   }

   /**
    * Retrieves a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Date value
    */
   public Date getDate9()
   {
      return ((Date) getCachedValue(ResourceField.DATE9));
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate1(Date date)
   {
      set(ResourceField.DATE1, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate10(Date date)
   {
      set(ResourceField.DATE10, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate2(Date date)
   {
      set(ResourceField.DATE2, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate3(Date date)
   {
      set(ResourceField.DATE3, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate4(Date date)
   {
      set(ResourceField.DATE4, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate5(Date date)
   {
      set(ResourceField.DATE5, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate6(Date date)
   {
      set(ResourceField.DATE6, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate7(Date date)
   {
      set(ResourceField.DATE7, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate8(Date date)
   {
      set(ResourceField.DATE8, date);
   }

   /**
    * Sets a date value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param date Date value
    */
   public void setDate9(Date date)
   {
      set(ResourceField.DATE9, date);
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost1()
   {
      return ((Number) getCachedValue(ResourceField.COST1));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost2()
   {
      return ((Number) getCachedValue(ResourceField.COST2));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost3()
   {
      return ((Number) getCachedValue(ResourceField.COST3));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost4()
   {
      return ((Number) getCachedValue(ResourceField.COST4));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost5()
   {
      return ((Number) getCachedValue(ResourceField.COST5));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost6()
   {
      return ((Number) getCachedValue(ResourceField.COST6));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost7()
   {
      return ((Number) getCachedValue(ResourceField.COST7));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost8()
   {
      return ((Number) getCachedValue(ResourceField.COST8));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost9()
   {
      return ((Number) getCachedValue(ResourceField.COST9));
   }

   /**
    * Retrieves a cost. Note that this value is an extension to the MPX
    * specification.
    * 
    * @return Cost value
    */
   public Number getCost10()
   {
      return ((Number) getCachedValue(ResourceField.COST10));
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost1(Number number)
   {
      set(ResourceField.COST1, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost2(Number number)
   {
      set(ResourceField.COST2, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost3(Number number)
   {
      set(ResourceField.COST3, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost4(Number number)
   {
      set(ResourceField.COST4, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost5(Number number)
   {
      set(ResourceField.COST5, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost6(Number number)
   {
      set(ResourceField.COST6, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost7(Number number)
   {
      set(ResourceField.COST7, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost8(Number number)
   {
      set(ResourceField.COST8, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost9(Number number)
   {
      set(ResourceField.COST9, number);
   }

   /**
    * Sets a cost value. Note that this value is an extension to the MPX
    * specification.
    * 
    * @param number Cost value
    */
   public void setCost10(Number number)
   {
      set(ResourceField.COST10, number);
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag1()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG1)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag2()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG2)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag3()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG3)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag4()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG4)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag5()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG5)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag6()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG6)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag7()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG7)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag8()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG8)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag9()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG9)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag10()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG10)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag11()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG11)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag12()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG12)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag13()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG13)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag14()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG14)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag15()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG15)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag16()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG16)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag17()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG17)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag18()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG18)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag19()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG19)));
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag20()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.FLAG20)));
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag1(boolean b)
   {
      set(ResourceField.FLAG1, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag2(boolean b)
   {
      set(ResourceField.FLAG2, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag3(boolean b)
   {
      set(ResourceField.FLAG3, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag4(boolean b)
   {
      set(ResourceField.FLAG4, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag5(boolean b)
   {
      set(ResourceField.FLAG5, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag6(boolean b)
   {
      set(ResourceField.FLAG6, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag7(boolean b)
   {
      set(ResourceField.FLAG7, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag8(boolean b)
   {
      set(ResourceField.FLAG8, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag9(boolean b)
   {
      set(ResourceField.FLAG9, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag10(boolean b)
   {
      set(ResourceField.FLAG10, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag11(boolean b)
   {
      set(ResourceField.FLAG11, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag12(boolean b)
   {
      set(ResourceField.FLAG12, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag13(boolean b)
   {
      set(ResourceField.FLAG13, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag14(boolean b)
   {
      set(ResourceField.FLAG14, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag15(boolean b)
   {
      set(ResourceField.FLAG15, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag16(boolean b)
   {
      set(ResourceField.FLAG16, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag17(boolean b)
   {
      set(ResourceField.FLAG17, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag18(boolean b)
   {
      set(ResourceField.FLAG18, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag19(boolean b)
   {
      set(ResourceField.FLAG19, b);
   }

   /**
    * Sets the flag value.
    * 
    * @param b flag value
    */
   public void setFlag20(boolean b)
   {
      set(ResourceField.FLAG20, b);
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode1(String value)
   {
      set(ResourceField.OUTLINE_CODE1, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode1()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE1));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode2(String value)
   {
      set(ResourceField.OUTLINE_CODE2, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode2()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE2));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode3(String value)
   {
      set(ResourceField.OUTLINE_CODE3, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode3()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE3));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode4(String value)
   {
      set(ResourceField.OUTLINE_CODE4, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode4()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE4));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode5(String value)
   {
      set(ResourceField.OUTLINE_CODE5, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode5()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE5));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode6(String value)
   {
      set(ResourceField.OUTLINE_CODE6, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode6()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE6));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode7(String value)
   {
      set(ResourceField.OUTLINE_CODE7, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode7()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE7));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode8(String value)
   {
      set(ResourceField.OUTLINE_CODE8, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode8()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE8));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode9(String value)
   {
      set(ResourceField.OUTLINE_CODE9, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode9()
   {
      return ((String) getCachedValue(ResourceField.OUTLINE_CODE9));
   }

   /**
    * Sets the value of an outline code field.
    * 
    * @param value outline code value
    */
   public void setOutlineCode10(String value)
   {
      set(ResourceField.OUTLINE_CODE10, value);
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode10()
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
      return (getCachedValue(getParentFile().getAliasResourceField(alias)));
   }

   /**
    * Set the value of a field using its alias.
    * 
    * @param alias field alias
    * @param value field value
    */
   public void setFieldByAlias(String alias, Object value)
   {
      set(getParentFile().getAliasResourceField(alias), value);
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
    * 
    * resourceID = (subprojectUniqueID & 0xFFFF)
    * 
    * @return sub project unique resource ID
    */
   public Integer getSubprojectResourceUniqueID()
   {
      return (m_subprojectResourceUniqueID);
   }

   /**
    * Sets the sub project unique resource ID.
    * 
    * @param subprojectUniqueResourceID subproject unique resource ID
    */
   public void setSubprojectResourceUniqueID(Integer subprojectUniqueResourceID)
   {
      m_subprojectResourceUniqueID = subprojectUniqueResourceID;
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Number getEnterpriseCost(int index)
   {
      return ((Number) getCachedValue(selectResourceField(ENTERPRISE_COST_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseCost(int index, Number value)
   {
      set(selectResourceField(ENTERPRISE_COST_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Date getEnterpriseDate(int index)
   {
      return ((Date) getCachedValue(selectResourceField(ENTERPRISE_DATE_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDate(int index, Date value)
   {
      set(selectResourceField(ENTERPRISE_DATE_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Duration getEnterpriseDuration(int index)
   {
      return ((Duration) getCachedValue(selectResourceField(ENTERPRISE_DURATION_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDuration(int index, Duration value)
   {
      set(selectResourceField(ENTERPRISE_DURATION_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public boolean getEnterpriseFlag(int index)
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(selectResourceField(ENTERPRISE_FLAG_FIELDS, index))));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseFlag(int index, boolean value)
   {
      set(selectResourceField(ENTERPRISE_FLAG_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public Number getEnterpriseNumber(int index)
   {
      return ((Number) getCachedValue(selectResourceField(ENTERPRISE_NUMBER_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseNumber(int index, Number value)
   {
      set(selectResourceField(ENTERPRISE_NUMBER_FIELDS, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    * 
    * @param index field index
    * @return field value
    */
   public String getEnterpriseText(int index)
   {
      return ((String) getCachedValue(selectResourceField(ENTERPRISE_TEXT_FIELDS, index)));
   }

   /**
    * Set an enterprise field value.
    * 
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseText(int index, String value)
   {
      set(selectResourceField(ENTERPRISE_TEXT_FIELDS, index), value);
   }

   /**
    * Set a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineCost(int baselineNumber, Number value)
   {
      set(selectResourceField(BASELINE_COSTS, baselineNumber), value);
   }

   /**
    * Set a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineWork(int baselineNumber, Duration value)
   {
      set(selectResourceField(BASELINE_WORKS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Number getBaselineCost(int baselineNumber)
   {
      return ((Number) getCachedValue(selectResourceField(BASELINE_COSTS, baselineNumber)));
   }

   /**
    * Retrieve a baseline value.
    * 
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Duration getBaselineWork(int baselineNumber)
   {
      return ((Duration) getCachedValue(selectResourceField(BASELINE_WORKS, baselineNumber)));
   }

   /**
    * Retrieve the budget flag.
    * 
    * @return budget flag
    */
   public boolean getBudget()
   {
      return (BooleanUtility.getBoolean((Boolean) getCachedValue(ResourceField.BUDGET)));
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
      return m_costRateTables[index];
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
   private ResourceField selectResourceField(ResourceField[] fields, int index)
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
         ResourceField resourceField = (ResourceField) field;

         switch (resourceField)
         {
            case COST_VARIANCE :
            {
               result = getCostVariance();
               break;
            }

            case WORK_VARIANCE :
            {
               result = getWorkVariance();
               break;
            }

            case CV :
            {
               result = getCV();
               break;
            }

            case SV :
            {
               result = getSV();
               break;
            }

            case OVERALLOCATED :
            {
               result = Boolean.valueOf(getOverAllocated());
               break;
            }

            default :
            {
               result = m_array[field.getValue()];
               break;
            }
         }
      }

      return (result);
   }

   /**
    * {@inheritDoc}
    */
   public void set(FieldType field, Object value)
   {
      if (field != null)
      {
         int index = field.getValue();
         fireFieldChangeEvent((ResourceField) field, m_array[index], value);
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
         case COST :
         case BASELINE_COST :
         {
            m_array[ResourceField.COST_VARIANCE.getValue()] = null;
            break;
         }

         case WORK :
         case BASELINE_WORK :
         {
            m_array[ResourceField.WORK_VARIANCE.getValue()] = null;
            break;
         }

         case BCWP :
         case ACWP :
         {
            m_array[ResourceField.CV.getValue()] = null;
            m_array[ResourceField.SV.getValue()] = null;
            break;
         }

         case BCWS :
         {
            m_array[ResourceField.SV.getValue()] = null;
            break;
         }

         case PEAK :
         case MAX_UNITS :
         {
            m_array[ResourceField.OVERALLOCATED.getValue()] = null;
            break;
         }

         default :
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
   public int compareTo(Resource o)
   {
      int id1 = NumberUtility.getInt(getID());
      int id2 = NumberUtility.getInt(o.getID());
      return ((id1 < id2) ? (-1) : ((id1 == id2) ? 0 : 1));
   }

   /**
    * {@inheritDoc}
    */
   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof Resource)
      {
         result = (compareTo((Resource) o) == 0);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public int hashCode()
   {
      return (NumberUtility.getInt(getID()));
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[Resource id=" + getID() + " uniqueID=" + getUniqueID() + " name=" + getName() + "]");
   }

   private static final ResourceField[] ENTERPRISE_COST_FIELDS =
   {
      ResourceField.ENTERPRISE_COST1,
      ResourceField.ENTERPRISE_COST2,
      ResourceField.ENTERPRISE_COST3,
      ResourceField.ENTERPRISE_COST4,
      ResourceField.ENTERPRISE_COST5,
      ResourceField.ENTERPRISE_COST6,
      ResourceField.ENTERPRISE_COST7,
      ResourceField.ENTERPRISE_COST8,
      ResourceField.ENTERPRISE_COST9,
      ResourceField.ENTERPRISE_COST10
   };

   private static final ResourceField[] ENTERPRISE_DATE_FIELDS =
   {
      ResourceField.ENTERPRISE_DATE1,
      ResourceField.ENTERPRISE_DATE2,
      ResourceField.ENTERPRISE_DATE3,
      ResourceField.ENTERPRISE_DATE4,
      ResourceField.ENTERPRISE_DATE5,
      ResourceField.ENTERPRISE_DATE6,
      ResourceField.ENTERPRISE_DATE7,
      ResourceField.ENTERPRISE_DATE8,
      ResourceField.ENTERPRISE_DATE9,
      ResourceField.ENTERPRISE_DATE10,
      ResourceField.ENTERPRISE_DATE11,
      ResourceField.ENTERPRISE_DATE12,
      ResourceField.ENTERPRISE_DATE13,
      ResourceField.ENTERPRISE_DATE14,
      ResourceField.ENTERPRISE_DATE15,
      ResourceField.ENTERPRISE_DATE16,
      ResourceField.ENTERPRISE_DATE17,
      ResourceField.ENTERPRISE_DATE18,
      ResourceField.ENTERPRISE_DATE19,
      ResourceField.ENTERPRISE_DATE20,
      ResourceField.ENTERPRISE_DATE21,
      ResourceField.ENTERPRISE_DATE22,
      ResourceField.ENTERPRISE_DATE23,
      ResourceField.ENTERPRISE_DATE24,
      ResourceField.ENTERPRISE_DATE25,
      ResourceField.ENTERPRISE_DATE26,
      ResourceField.ENTERPRISE_DATE27,
      ResourceField.ENTERPRISE_DATE28,
      ResourceField.ENTERPRISE_DATE29,
      ResourceField.ENTERPRISE_DATE30
   };

   private static final ResourceField[] ENTERPRISE_DURATION_FIELDS =
   {
      ResourceField.ENTERPRISE_DURATION1,
      ResourceField.ENTERPRISE_DURATION2,
      ResourceField.ENTERPRISE_DURATION3,
      ResourceField.ENTERPRISE_DURATION4,
      ResourceField.ENTERPRISE_DURATION5,
      ResourceField.ENTERPRISE_DURATION6,
      ResourceField.ENTERPRISE_DURATION7,
      ResourceField.ENTERPRISE_DURATION8,
      ResourceField.ENTERPRISE_DURATION9,
      ResourceField.ENTERPRISE_DURATION10
   };

   private static final ResourceField[] ENTERPRISE_FLAG_FIELDS =
   {
      ResourceField.ENTERPRISE_FLAG1,
      ResourceField.ENTERPRISE_FLAG2,
      ResourceField.ENTERPRISE_FLAG3,
      ResourceField.ENTERPRISE_FLAG4,
      ResourceField.ENTERPRISE_FLAG5,
      ResourceField.ENTERPRISE_FLAG6,
      ResourceField.ENTERPRISE_FLAG7,
      ResourceField.ENTERPRISE_FLAG8,
      ResourceField.ENTERPRISE_FLAG9,
      ResourceField.ENTERPRISE_FLAG10,
      ResourceField.ENTERPRISE_FLAG11,
      ResourceField.ENTERPRISE_FLAG12,
      ResourceField.ENTERPRISE_FLAG13,
      ResourceField.ENTERPRISE_FLAG14,
      ResourceField.ENTERPRISE_FLAG15,
      ResourceField.ENTERPRISE_FLAG16,
      ResourceField.ENTERPRISE_FLAG17,
      ResourceField.ENTERPRISE_FLAG18,
      ResourceField.ENTERPRISE_FLAG19,
      ResourceField.ENTERPRISE_FLAG20
   };

   private static final ResourceField[] ENTERPRISE_NUMBER_FIELDS =
   {
      ResourceField.ENTERPRISE_NUMBER1,
      ResourceField.ENTERPRISE_NUMBER2,
      ResourceField.ENTERPRISE_NUMBER3,
      ResourceField.ENTERPRISE_NUMBER4,
      ResourceField.ENTERPRISE_NUMBER5,
      ResourceField.ENTERPRISE_NUMBER6,
      ResourceField.ENTERPRISE_NUMBER7,
      ResourceField.ENTERPRISE_NUMBER8,
      ResourceField.ENTERPRISE_NUMBER9,
      ResourceField.ENTERPRISE_NUMBER10,
      ResourceField.ENTERPRISE_NUMBER11,
      ResourceField.ENTERPRISE_NUMBER12,
      ResourceField.ENTERPRISE_NUMBER13,
      ResourceField.ENTERPRISE_NUMBER14,
      ResourceField.ENTERPRISE_NUMBER15,
      ResourceField.ENTERPRISE_NUMBER16,
      ResourceField.ENTERPRISE_NUMBER17,
      ResourceField.ENTERPRISE_NUMBER18,
      ResourceField.ENTERPRISE_NUMBER19,
      ResourceField.ENTERPRISE_NUMBER20,
      ResourceField.ENTERPRISE_NUMBER21,
      ResourceField.ENTERPRISE_NUMBER22,
      ResourceField.ENTERPRISE_NUMBER23,
      ResourceField.ENTERPRISE_NUMBER24,
      ResourceField.ENTERPRISE_NUMBER25,
      ResourceField.ENTERPRISE_NUMBER26,
      ResourceField.ENTERPRISE_NUMBER27,
      ResourceField.ENTERPRISE_NUMBER28,
      ResourceField.ENTERPRISE_NUMBER29,
      ResourceField.ENTERPRISE_NUMBER30,
      ResourceField.ENTERPRISE_NUMBER31,
      ResourceField.ENTERPRISE_NUMBER32,
      ResourceField.ENTERPRISE_NUMBER33,
      ResourceField.ENTERPRISE_NUMBER34,
      ResourceField.ENTERPRISE_NUMBER35,
      ResourceField.ENTERPRISE_NUMBER36,
      ResourceField.ENTERPRISE_NUMBER37,
      ResourceField.ENTERPRISE_NUMBER38,
      ResourceField.ENTERPRISE_NUMBER39,
      ResourceField.ENTERPRISE_NUMBER40
   };

   private static final ResourceField[] ENTERPRISE_TEXT_FIELDS =
   {
      ResourceField.ENTERPRISE_TEXT1,
      ResourceField.ENTERPRISE_TEXT2,
      ResourceField.ENTERPRISE_TEXT3,
      ResourceField.ENTERPRISE_TEXT4,
      ResourceField.ENTERPRISE_TEXT5,
      ResourceField.ENTERPRISE_TEXT6,
      ResourceField.ENTERPRISE_TEXT7,
      ResourceField.ENTERPRISE_TEXT8,
      ResourceField.ENTERPRISE_TEXT9,
      ResourceField.ENTERPRISE_TEXT10,
      ResourceField.ENTERPRISE_TEXT11,
      ResourceField.ENTERPRISE_TEXT12,
      ResourceField.ENTERPRISE_TEXT13,
      ResourceField.ENTERPRISE_TEXT14,
      ResourceField.ENTERPRISE_TEXT15,
      ResourceField.ENTERPRISE_TEXT16,
      ResourceField.ENTERPRISE_TEXT17,
      ResourceField.ENTERPRISE_TEXT18,
      ResourceField.ENTERPRISE_TEXT19,
      ResourceField.ENTERPRISE_TEXT20,
      ResourceField.ENTERPRISE_TEXT21,
      ResourceField.ENTERPRISE_TEXT22,
      ResourceField.ENTERPRISE_TEXT23,
      ResourceField.ENTERPRISE_TEXT24,
      ResourceField.ENTERPRISE_TEXT25,
      ResourceField.ENTERPRISE_TEXT26,
      ResourceField.ENTERPRISE_TEXT27,
      ResourceField.ENTERPRISE_TEXT28,
      ResourceField.ENTERPRISE_TEXT29,
      ResourceField.ENTERPRISE_TEXT30,
      ResourceField.ENTERPRISE_TEXT31,
      ResourceField.ENTERPRISE_TEXT32,
      ResourceField.ENTERPRISE_TEXT33,
      ResourceField.ENTERPRISE_TEXT34,
      ResourceField.ENTERPRISE_TEXT35,
      ResourceField.ENTERPRISE_TEXT36,
      ResourceField.ENTERPRISE_TEXT37,
      ResourceField.ENTERPRISE_TEXT38,
      ResourceField.ENTERPRISE_TEXT39,
      ResourceField.ENTERPRISE_TEXT40
   };

   private static final ResourceField[] BASELINE_COSTS =
   {
      ResourceField.BASELINE1_COST,
      ResourceField.BASELINE2_COST,
      ResourceField.BASELINE3_COST,
      ResourceField.BASELINE4_COST,
      ResourceField.BASELINE5_COST,
      ResourceField.BASELINE6_COST,
      ResourceField.BASELINE7_COST,
      ResourceField.BASELINE8_COST,
      ResourceField.BASELINE9_COST,
      ResourceField.BASELINE10_COST
   };

   private static final ResourceField[] BASELINE_WORKS =
   {
      ResourceField.BASELINE1_WORK,
      ResourceField.BASELINE2_WORK,
      ResourceField.BASELINE3_WORK,
      ResourceField.BASELINE4_WORK,
      ResourceField.BASELINE5_WORK,
      ResourceField.BASELINE6_WORK,
      ResourceField.BASELINE7_WORK,
      ResourceField.BASELINE8_WORK,
      ResourceField.BASELINE9_WORK,
      ResourceField.BASELINE10_WORK
   };

   /**
    * Array of field values.
    */
   private Object[] m_array = new Object[ResourceField.MAX_VALUE];

   /**
    * Resource calendar for this resource.
    */
   private ProjectCalendar m_calendar;

   /**
    * List of all assignments for this resource.
    */
   private List<ResourceAssignment> m_assignments = new LinkedList<ResourceAssignment>();

   private boolean m_null;
   private String m_ntAccount;
   private TimeUnit m_standardRateFormat;
   private TimeUnit m_overtimeRateFormat;
   private boolean m_generic;
   private boolean m_inactive;
   private String m_activeDirectoryGUID;
   private Duration m_actualOvertimeWorkProtected;
   private Duration m_actualWorkProtected;
   private BookingType m_bookingType;
   private boolean m_enterprise;
   private Integer m_subprojectResourceUniqueID;
   private CostRateTable[] m_costRateTables = new CostRateTable[5];
   private AvailabilityTable m_availability = new AvailabilityTable();
   private List<FieldListener> m_listeners;
}
