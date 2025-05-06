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

package org.mpxj;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.mpxj.common.BooleanHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ResourceFieldLists;

/**
 * This class represents a resource used in a project.
 */
public final class Resource extends AbstractFieldContainer<Resource> implements Comparable<Resource>, ProjectEntityWithID, ChildResourceContainer
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   Resource(ProjectFile file)
   {
      super(file);

      ProjectConfig config = file.getProjectConfig();

      if (config.getAutoResourceUniqueID())
      {
         setUniqueID(file.getUniqueIdObjectSequence(Resource.class).getNext());
      }

      if (config.getAutoResourceID())
      {
         setID(file.getResources().getNextID());
      }

      m_costRateTables = new CostRateTable[CostRateTable.MAX_TABLES];
      for (int index = 0; index < m_costRateTables.length; index++)
      {
         CostRateTable table = new CostRateTable();
         table.add(CostRateTableEntry.DEFAULT_ENTRY);
         m_costRateTables[index] = table;
      }
   }

   @Override public Resource addResource()
   {
      ProjectFile parent = getParentFile();
      Resource resource = new Resource(parent);
      resource.setParentResource(this);
      m_children.add(resource);
      parent.getResources().add(resource);
      return resource;
   }

   /**
    * Add an existing resource as a child of the current resource.
    *
    * @param child child resource
    */
   public void addChildResource(Resource child)
   {
      child.setParentResource(this);
      m_children.add(child);
   }

   @Override public List<Resource> getChildResources()
   {
      return m_children;
   }

   /**
    * Removes a child resource.
    *
    * @param child child resource instance
    */
   public void removeChildResource(Resource child)
   {
      if (m_children.remove(child))
      {
         child.setParentResourceUniqueID(null);
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
      return (String) get(ResourceField.NAME);
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
      return (ResourceType) get(ResourceField.TYPE);
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
      return m_null;
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
      return (String) get(ResourceField.INITIALS);
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
      return (String) get(ResourceField.PHONETICS);
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
      return (String) get(ResourceField.WINDOWS_USER_ACCOUNT);
   }

   /**
    * Retrieves the units label for a material resource.
    *
    * @return material resource units label
    */
   public String getMaterialLabel()
   {
      return (String) get(ResourceField.MATERIAL_LABEL);
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
      return (String) get(ResourceField.CODE);
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
      return (String) get(ResourceField.GROUP);
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
      return (WorkGroup) get(ResourceField.WORKGROUP);
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
      return (String) get(ResourceField.EMAIL_ADDRESS);
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
      return (String) get(ResourceField.HYPERLINK);
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
      return (String) get(ResourceField.HYPERLINK_ADDRESS);
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
      return (String) get(ResourceField.HYPERLINK_SUBADDRESS);
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
      return (String) get(ResourceField.HYPERLINK_SCREEN_TIP);
   }

   /**
    * Sets the default availability of a resource.
    *
    * @param defaultUnits default availability
    */
   public void setDefaultUnits(Number defaultUnits)
   {
      set(ResourceField.DEFAULT_UNITS, defaultUnits);
   }

   /**
    * Retrieves the default availability of a resource.
    *
    * @return maximum availability
    */
   public Number getDefaultUnits()
   {
      return (Number) get(ResourceField.DEFAULT_UNITS);
   }

   /**
    * Retrieves the maximum availability of a resource on the current date.
    * Refer to the availability table to retrieve this value for other dates.
    *
    * @return maximum availability
    */
   public Number getMaxUnits()
   {
      return (Number) get(ResourceField.MAX_UNITS);
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
      return (Number) get(ResourceField.PEAK);
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
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.OVERALLOCATED));
   }

   /**
    * Retrieves the "available from" date.
    *
    * @return available from date
    */
   public LocalDateTime getAvailableFrom()
   {
      return (LocalDateTime) get(ResourceField.AVAILABLE_FROM);
   }

   /**
    * Retrieves the "available to" date.
    *
    * @return available from date
    */
   public LocalDateTime getAvailableTo()
   {
      return (LocalDateTime) get(ResourceField.AVAILABLE_TO);
   }

   /**
    * Retrieves the earliest start date for all assigned tasks.
    *
    * @return start date
    */
   public LocalDateTime getStart()
   {
      return (LocalDateTime) get(ResourceField.START);
   }

   /**
    * Retrieves the latest finish date for all assigned tasks.
    *
    * @return finish date
    */
   public LocalDateTime getFinish()
   {
      return (LocalDateTime) get(ResourceField.FINISH);
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
      return (BooleanHelper.getBoolean((Boolean) get(ResourceField.CAN_LEVEL)));
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
      return (AccrueType) get(ResourceField.ACCRUE_AT);
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
      return (Duration) get(ResourceField.WORK);
   }

   /**
    * Retrieve the value of the regular work field. Note that this value is an
    * extension to the MPX specification.
    *
    * @return Regular work value
    */
   public Duration getRegularWork()
   {
      return (Duration) get(ResourceField.REGULAR_WORK);
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
      return (Duration) get(ResourceField.ACTUAL_WORK);
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
      return (Duration) get(ResourceField.OVERTIME_WORK);
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
      return (Duration) get(ResourceField.REMAINING_WORK);
   }

   /**
    * Retrieve the value of the actual overtime work field.
    *
    * @return actual overtime work value
    */
   public Duration getActualOvertimeWork()
   {
      return (Duration) get(ResourceField.ACTUAL_OVERTIME_WORK);
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
      return (Duration) get(ResourceField.REMAINING_OVERTIME_WORK);
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
      return (Number) get(ResourceField.PERCENT_WORK_COMPLETE);
   }

   /**
    * Gets Standard Rate field value.
    *
    * @return Rate
    */
   public Rate getStandardRate()
   {
      return (Rate) get(ResourceField.STANDARD_RATE);
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
      return (Number) get(ResourceField.COST);
   }

   /**
    * Retrieves the overtime rate for this resource.
    *
    * @return overtime rate
    */
   public Rate getOvertimeRate()
   {
      return (Rate) get(ResourceField.OVERTIME_RATE);
   }

   /**
    * Retrieve the value of the overtime cost field.
    *
    * @return Overtime cost value
    */
   public Number getOvertimeCost()
   {
      return (Number) get(ResourceField.OVERTIME_COST);
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
    * Retrieve the cost per use.
    *
    * @return cost per use
    */
   public Number getCostPerUse()
   {
      return (Number) get(ResourceField.COST_PER_USE);
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
      return (Number) get(ResourceField.ACTUAL_COST);
   }

   /**
    * Retrieve actual overtime cost.
    *
    * @return actual overtime cost
    */
   public Number getActualOvertimeCost()
   {
      return (Number) get(ResourceField.ACTUAL_OVERTIME_COST);
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
      return (Number) get(ResourceField.REMAINING_COST);
   }

   /**
    * Retrieve the remaining overtime cost.
    *
    * @return remaining overtime cost
    */
   public Number getRemainingOvertimeCost()
   {
      return (Number) get(ResourceField.REMAINING_OVERTIME_COST);
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
      return (Duration) get(ResourceField.WORK_VARIANCE);
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
      return (Number) get(ResourceField.COST_VARIANCE);
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
      return (Number) get(ResourceField.SV);
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
      return (Number) get(ResourceField.CV);
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
      return (Number) get(ResourceField.ACWP);
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
      Object notes = get(ResourceField.NOTES);
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
      return (Notes) get(ResourceField.NOTES);
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
      return (Number) get(ResourceField.BCWS);
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
      return (Number) get(ResourceField.BCWP);
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
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.GENERIC));
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
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.ACTIVE));
   }

   /**
    * Sets the active directory GUID for this resource.
    *
    * @param guid active directory GUID
    */
   public void setActiveDirectoryGUID(String guid)
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
      return (Duration) get(ResourceField.ACTUAL_OVERTIME_WORK_PROTECTED);
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
      return (Duration) get(ResourceField.ACTUAL_WORK_PROTECTED);
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
      return (BookingType) get(ResourceField.BOOKING_TYPE);
   }

   /**
    * Sets the creation date.
    *
    * @param creationDate creation date
    */
   public void setCreationDate(LocalDateTime creationDate)
   {
      set(ResourceField.CREATED, creationDate);
   }

   /**
    * Retrieves the creation date.
    *
    * @return creation date
    */
   public LocalDateTime getCreationDate()
   {
      return (LocalDateTime) get(ResourceField.CREATED);
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
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.ENTERPRISE));
   }

   /**
    * Retrieve the calendar unique ID.
    *
    * @return calendar unique ID
    */
   public Integer getCalendarUniqueID()
   {
      return (Integer) get(ResourceField.CALENDAR_UNIQUE_ID);
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
    * Sets the Base Calendar field indicates which calendar is the base calendar
    * for a resource calendar. The list includes the three built-in calendars,
    * as well as any new base calendars you have created in the Change Working
    * Time dialog box.
    *
    * @param val calendar name
    */
   public void setBaseCalendar(String val)
   {
      set(ResourceField.BASE_CALENDAR, val == null || val.isEmpty() ? "Standard" : val);
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
      return (String) get(selectField(ResourceFieldLists.CUSTOM_TEXT, index));
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
    * Retrieves Base Calendar name associated with this resource. This field
    * indicates which calendar is the base calendar for a resource calendar.
    *
    * @return Base calendar name
    */
   public String getBaseCalendar()
   {
      return (String) get(ResourceField.BASE_CALENDAR);
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
      return (Number) get(ResourceField.BASELINE_COST);
   }

   /**
    * Retrieves the Baseline Work value.
    *
    * @return Baseline work value
    */
   public Duration getBaselineWork()
   {
      return (Duration) get(ResourceField.BASELINE_WORK);
   }

   /**
    * Gets ID field value.
    *
    * @return value
    */
   @Override public Integer getID()
   {
      return (Integer) get(ResourceField.ID);
   }

   /**
    * Gets Linked Fields field value.
    *
    * @return value
    */
   public boolean getLinkedFields()
   {
      return (BooleanHelper.getBoolean((Boolean) get(ResourceField.LINKED_FIELDS)));
   }

   /**
    * Gets objects field value.
    *
    * @return value
    */
   public Integer getObjects()
   {
      return (Integer) get(ResourceField.OBJECTS);
   }

   /**
    * Gets Unique ID field value.
    *
    * @return value
    */
   @Override public Integer getUniqueID()
   {
      return (Integer) get(ResourceField.UNIQUE_ID);
   }

   /**
    * Retrieve the parent resource's Unique ID.
    *
    * @return parent resource Unique ID
    */
   public Integer getParentResourceUniqueID()
   {
      return (Integer) get(ResourceField.PARENT_ID);
   }

   /**
    * Sets the parent resource's Unique ID.
    *
    * @param id parent resource unique ID
    */
   public void setParentResourceUniqueID(Integer id)
   {
      set(ResourceField.PARENT_ID, id);
   }

   /**
    * Retrieve the parent resource.
    *
    * @return parent resource
    */
   public Resource getParentResource()
   {
      return getParentFile().getResourceByUniqueID(getParentResourceUniqueID());
   }

   /**
    * Set the parent resource.
    *
    * @param resource parent resource
    */
   public void setParentResource(Resource resource)
   {
      setParentResourceUniqueID(resource == null ? null : resource.getUniqueID());
   }

   /**
    * Set a start value.
    *
    * @param index start index (1-10)
    * @param value start value
    */
   public void setStart(int index, LocalDateTime value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_START, index), value);
   }

   /**
    * Retrieve a start value.
    *
    * @param index start index (1-10)
    * @return start value
    */
   public LocalDateTime getStart(int index)
   {
      return (LocalDateTime) get(selectField(ResourceFieldLists.CUSTOM_START, index));
   }

   /**
    * Set a finish value.
    *
    * @param index finish index (1-10)
    * @param value finish value
    */
   public void setFinish(int index, LocalDateTime value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_FINISH, index), value);
   }

   /**
    * Retrieve a finish value.
    *
    * @param index finish index (1-10)
    * @return finish value
    */
   public LocalDateTime getFinish(int index)
   {
      return (LocalDateTime) get(selectField(ResourceFieldLists.CUSTOM_FINISH, index));
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
      return (Number) get(selectField(ResourceFieldLists.CUSTOM_NUMBER, index));
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
      return (Duration) get(selectField(ResourceFieldLists.CUSTOM_DURATION, index));
   }

   /**
    * Set a date value.
    *
    * @param index date index (1-10)
    * @param value date value
    */
   public void setDate(int index, LocalDateTime value)
   {
      set(selectField(ResourceFieldLists.CUSTOM_DATE, index), value);
   }

   /**
    * Retrieve a date value.
    *
    * @param index date index (1-10)
    * @return date value
    */
   public LocalDateTime getDate(int index)
   {
      return (LocalDateTime) get(selectField(ResourceFieldLists.CUSTOM_DATE, index));
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
      return (Number) get(selectField(ResourceFieldLists.CUSTOM_COST, index));
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
      return BooleanHelper.getBoolean((Boolean) get(selectField(ResourceFieldLists.CUSTOM_FLAG, index)));
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
      return (String) get(selectField(ResourceFieldLists.CUSTOM_OUTLINE_CODE, index));
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
      return get(getParentFile().getResources().getFieldTypeByAlias(alias));
   }

   /**
    * Set the value of a field using its alias.
    *
    * @param alias field alias
    * @param value field value
    */
   public void setFieldByAlias(String alias, Object value)
   {
      set(getParentFile().getResources().getFieldTypeByAlias(alias), value);
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
    * Add a role assignment, and a skill level for the role, to this resource. Replaces any existing
    * assignment for this role.
    *
    * @param role role to assign to the resource
    * @param skillLevel skill level
    */
   public void addRoleAssignment(Resource role, SkillLevel skillLevel)
   {
      m_roleAssignments.put(role, skillLevel);
   }

   /**
    * Remove a role assignment from this resource.
    *
    * @param role role to remove
    */
   public void removeRoleAssignment(Resource role)
   {
      m_roleAssignments.remove(role);
   }

   /**
    * Retrieve a map of the roles assigned to this resource.
    * The roles are represented as the keys in this map
    * with the skill level represented as the value.
    *
    * @return role assignment map
    */
   public Map<Resource, SkillLevel> getRoleAssignments()
   {
      return m_roleAssignments;
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
      return (Integer) get(ResourceField.SUBPROJECT_RESOURCE_UNIQUE_ID);
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
      return (Number) get(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_COST, index));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseCost(int index, Number value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_COST, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public LocalDateTime getEnterpriseDate(int index)
   {
      return (LocalDateTime) get(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_DATE, index));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDate(int index, LocalDateTime value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_DATE, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public Duration getEnterpriseDuration(int index)
   {
      return (Duration) get(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_DURATION, index));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseDuration(int index, Duration value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_DURATION, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public boolean getEnterpriseFlag(int index)
   {
      return (BooleanHelper.getBoolean((Boolean) get(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_FLAG, index))));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseFlag(int index, boolean value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_FLAG, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public Number getEnterpriseNumber(int index)
   {
      return (Number) get(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_NUMBER, index));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseNumber(int index, Number value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_NUMBER, index), value);
   }

   /**
    * Retrieve an enterprise field value.
    *
    * @param index field index
    * @return field value
    */
   public String getEnterpriseText(int index)
   {
      return (String) get(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_TEXT, index));
   }

   /**
    * Set an enterprise field value.
    *
    * @param index field index
    * @param value field value
    */
   public void setEnterpriseText(int index, String value)
   {
      set(selectField(ResourceFieldLists.ENTERPRISE_CUSTOM_TEXT, index), value);
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
      return (Number) get(selectField(ResourceFieldLists.BASELINE_COSTS, baselineNumber));
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Duration getBaselineWork(int baselineNumber)
   {
      return (Duration) get(selectField(ResourceFieldLists.BASELINE_WORKS, baselineNumber));
   }

   /**
    * Retrieve the budget flag.
    *
    * @return budget flag
    */
   public boolean getBudget()
   {
      return (BooleanHelper.getBoolean((Boolean) get(ResourceField.BUDGET)));
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
      return (UUID) get(ResourceField.GUID);
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
      return (String) get(ResourceField.UNIT);
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
      return (String) get(ResourceField.SUPPLY_REFERENCE);
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
      return (String) get(ResourceField.DESCRIPTION);
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
      return (String) get(ResourceField.RESOURCE_ID);
   }

   /**
    * Set the modify on integrate field.
    *
    * @param value modify on integrate value
    */
   public void setModifyOnIntegrate(boolean value)
   {
      set(ResourceField.MODIFY_ON_INTEGRATE, value);
   }

   /**
    * Retrieve the modify on integrate value.
    *
    * @return modify on integrate value
    */
   public boolean getModifyOnIntegrate()
   {
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.MODIFY_ON_INTEGRATE));
   }

   /**
    * Set the expenses only field.
    *
    * @param value expenses only value
    */
   public void setExpensesOnly(boolean value)
   {
      set(ResourceField.EXPENSES_ONLY, value);
   }

   /**
    * Retrieve the expenses only field.
    *
    * @return expenses only value
    */
   public boolean getExpensesOnly()
   {
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.EXPENSES_ONLY));
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
      return (Number) get(ResourceField.PERIOD_DUR);
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
      return (Number) get(ResourceField.PRIORITY);
   }

   /**
    * Set the rate field.
    * Note that this is a TurboProject-specific field.
    *
    * @param value rate value
    */
   public void setRate(Number value)
   {
      set(ResourceField.RATE, value);
   }

   /**
    * Retrieve the rate field.
    * Note that this is a TurboProject-specific field.
    *
    * @return rate value
    */
   public Number getRate()
   {
      return (Number) get(ResourceField.RATE);
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
      return (Number) get(ResourceField.POOL);
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
      return (Number) get(ResourceField.PER_DAY);
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
      return (String) get(ResourceField.PHONE);
   }

   /**
    * Set the role field.
    *
    * @param value role value
    */
   public void setRole(boolean value)
   {
      set(ResourceField.ROLE, value);
   }

   /**
    * Retrieve the role field.
    * Returns true if this object represents a role rather than an individual resource.
    *
    * @return role value
    */
   public boolean getRole()
   {
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.ROLE));
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
      return getCostRateTable(costRateTable).getEntryByDate(LocalDateTime.now());
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
    * Retrieve the availability table entry effective for the current date.
    *
    * @return availability table entry
    */
   public Availability getCurrentAvailabilityTableEntry()
   {
      return m_availability.getEntryByDate(LocalDateTime.now());
   }

   /**
    * Retrieve the budget cost.
    *
    * @return budget cost value
    */
   public Number getBudgetCost()
   {
      return (Number) get(ResourceField.BUDGET_COST);
   }

   /**
    * Set the budget cost.
    *
    * @param value budget cost value
    */
   public void setBudgetCost(Number value)
   {
      set(ResourceField.BUDGET_COST, value);
   }

   /**
    * Retrieve the budget work.
    *
    * @return budget work value
    */
   public Duration getBudgetWork()
   {
      return (Duration) get(ResourceField.BUDGET_WORK);
   }

   /**
    * Set the budget work.
    *
    * @param value budget work value
    */
   public void setBudgetWork(Duration value)
   {
      set(ResourceField.BUDGET_WORK, value);
   }

   /**
    * Retrieve the baseline budget cost.
    *
    * @return baseline budget cost value
    */
   public Number getBaselineBudgetCost()
   {
      return (Number) get(ResourceField.BASELINE_BUDGET_COST);
   }

   /**
    * Set the baseline budget cost.
    *
    * @param value baseline budget cost value
    */
   public void setBaselineBudgetCost(Number value)
   {
      set(ResourceField.BASELINE_BUDGET_COST, value);
   }

   /**
    * Retrieve the baseline budget work.
    *
    * @return baseline budget work value
    */
   public Duration getBaselineBudgetWork()
   {
      return (Duration) get(ResourceField.BASELINE_BUDGET_WORK);
   }

   /**
    * Set the baseline budget work.
    *
    * @param value baseline budget work value
    */
   public void setBaselineBudgetWork(Duration value)
   {
      set(ResourceField.BASELINE_BUDGET_WORK, value);
   }

   /**
    * Retrieve a baseline budget cost.
    *
    * @param baselineNumber baseline number
    * @return baseline budget cost
    */
   public Number getBaselineBudgetCost(int baselineNumber)
   {
      return (Number) get(selectField(ResourceFieldLists.BASELINE_BUDGET_COSTS, baselineNumber));
   }

   /**
    * Set a baseline budget cost.
    *
    * @param baselineNumber baseline number
    * @param value baseline budget cost value
    */
   public void setBaselineBudgetCost(int baselineNumber, Number value)
   {
      set(selectField(ResourceFieldLists.BASELINE_BUDGET_COSTS, baselineNumber), value);
   }

   /**
    * Retrieve a baseline budget work.
    *
    * @param baselineNumber baseline number
    * @return baseline budget work value
    */
   public Duration getBaselineBudgetWork(int baselineNumber)
   {
      return (Duration) get(selectField(ResourceFieldLists.BASELINE_BUDGET_WORKS, baselineNumber));
   }

   /**
    * Set a baseline budget work.
    *
    * @param baselineNumber baseline number
    * @param value baseline budget work value
    */
   public void setBaselineBudgetWork(int baselineNumber, Duration value)
   {
      set(selectField(ResourceFieldLists.BASELINE_BUDGET_WORKS, baselineNumber), value);
   }

   /**
    * Retrieve the cost center.
    *
    * @return cost center value
    */
   public String getCostCenter()
   {
      return (String) get(ResourceField.COST_CENTER);
   }

   /**
    * Set the cost center.
    *
    * @param value cost center value
    */
   public void setCostCenter(String value)
   {
      set(ResourceField.COST_CENTER, value);
   }

   /**
    * Retrieve the calculate costs from units flag.
    *
    * @return calculate costs from units flag
    */
   public boolean getCalculateCostsFromUnits()
   {
      return BooleanHelper.getBoolean((Boolean) get(ResourceField.CALCULATE_COSTS_FROM_UNITS));
   }

   /**
    * Set the calculate costs from units flag.
    *
    * @param calculateCostsFromUnits calculate costs from units flag
    */
   public void setCalculateCostsFromUnits(boolean calculateCostsFromUnits)
   {
      set(ResourceField.CALCULATE_COSTS_FROM_UNITS, calculateCostsFromUnits);
   }

   /**
    * Retrieve this resource's sequence number.
    *
    * @return sequence number
    */
   public Integer getSequenceNumber()
   {
      return (Integer) get(ResourceField.SEQUENCE_NUMBER);
   }

   /**
    * Set this resource's sequence number.
    *
    * @param sequenceNumber sequence number
    */
   public void setSequenceNumber(Integer sequenceNumber)
   {
      set(ResourceField.SEQUENCE_NUMBER, sequenceNumber);
   }

   /**
    * Retrieves the location unique ID.
    *
    * @return location unique ID
    */
   public Integer getLocationUniqueID()
   {
      return (Integer) get(ResourceField.LOCATION_UNIQUE_ID);
   }

   /**
    * Sets the location unique ID.
    *
    * @param uniqueID location unique ID
    */
   public void setLocationUniqueID(Integer uniqueID)
   {
      set(ResourceField.LOCATION_UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieves the location.
    *
    * @return location.
    */
   public Location getLocation()
   {
      return getParentFile().getLocations().getByUniqueID(getLocationUniqueID());
   }

   /**
    * Sets the location.
    *
    * @param location location
    */
   public void setLocation(Location location)
   {
      setLocationUniqueID(location == null ? null : location.getUniqueID());
   }

   /**
    * Retrieves the shift unique ID.
    *
    * @return shift unique ID
    */
   public Integer getShiftUniqueID()
   {
      return (Integer) get(ResourceField.SHIFT_UNIQUE_ID);
   }

   /**
    * Sets the shift unique ID.
    *
    * @param uniqueID shift unique ID
    */
   public void setShiftUniqueID(Integer uniqueID)
   {
      set(ResourceField.SHIFT_UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieves the shift.
    *
    * @return shift.
    */
   public Shift getShift()
   {
      return getParentFile().getShifts().getByUniqueID(getShiftUniqueID());
   }

   /**
    * Sets the shift.
    *
    * @param shift shift
    */
   public void setShift(Shift shift)
   {
      setShiftUniqueID(shift == null ? null : shift.getUniqueID());
   }

   /**
    * Retrieve the unit of measure unique ID.
    *
    * @return unit of measure unique ID
    */
   public Integer getUnitOfMeasureUniqueID()
   {
      return (Integer) get(ResourceField.UNIT_OF_MEASURE_UNIQUE_ID);
   }

   /**
    * Sets the unit of measure unique ID.
    *
    * @param uniqueID unit of measure unique ID
    */
   public void setUnitOfMeasureUniqueID(Integer uniqueID)
   {
      set(ResourceField.UNIT_OF_MEASURE_UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieves the unit of measure for this resource.
    *
    * @return unit of measure instance
    */
   public UnitOfMeasure getUnitOfMeasure()
   {
      return getParentFile().getUnitsOfMeasure().getByUniqueID(getUnitOfMeasureUniqueID());
   }

   /**
    * Sets the unit of measure instance for this resource.
    *
    * @param unitOfMeasure unit of measure instance
    */
   public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure)
   {
      setUnitOfMeasureUniqueID(unitOfMeasure == null ? null : unitOfMeasure.getUniqueID());
   }

   /**
    * Retrieves the primary role unique ID.
    *
    * @return primary role unique ID
    */
   public Integer getPrimaryRoleUniqueID()
   {
      return (Integer) get(ResourceField.PRIMARY_ROLE_UNIQUE_ID);
   }

   /**
    * Sets the primary role unique ID.
    *
    * @param uniqueID primary role unique ID
    */
   public void setPrimaryRoleUniqueID(Integer uniqueID)
   {
      set(ResourceField.PRIMARY_ROLE_UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieves the primary role.
    *
    * @return primary role
    */
   public Resource getPrimaryRole()
   {
      return getParentFile().getResources().getByUniqueID(getPrimaryRoleUniqueID());
   }

   /**
    * Sets the primary role.
    *
    * @param role primary role
    */
   public void setPrimaryRole(Resource role)
   {
      setPrimaryRoleUniqueID(role == null ? null : role.getUniqueID());
   }

   /**
    * Retrieve the resource code values associated with this resource.
    *
    * @return map of resource code values
    */
   @SuppressWarnings("unchecked") public Map<ResourceCode, ResourceCodeValue> getResourceCodeValues()
   {
      return (Map<ResourceCode, ResourceCodeValue>) get(ResourceField.RESOURCE_CODE_VALUES);
   }

   /**
    * Assign a resource code value to this resource.
    *
    * @param value resource code value
    */
   @SuppressWarnings("unchecked") public void addResourceCodeValue(ResourceCodeValue value)
   {
      ((Map<ResourceCode, ResourceCodeValue>) get(ResourceField.RESOURCE_CODE_VALUES)).put(value.getParentCode(), value);
   }

   /**
    * Retrieve the role code values associated with this resource.
    *
    * @return map of role code values
    */
   @SuppressWarnings("unchecked") public Map<RoleCode, RoleCodeValue> getRoleCodeValues()
   {
      return (Map<RoleCode, RoleCodeValue>) get(ResourceField.ROLE_CODE_VALUES);
   }

   /**
    * Assign a role code value to this resource.
    *
    * @param value resoroleurce code value
    */
   @SuppressWarnings("unchecked") public void addRoleCodeValue(RoleCodeValue value)
   {
      ((Map<RoleCode, RoleCodeValue>) get(ResourceField.ROLE_CODE_VALUES)).put(value.getParentCode(), value);
   }

   /**
    * Retrieves the unique ID of the currency associated with this resource.
    *
    * @return currency unique ID
    */
   public Integer getCurrencyUniqueID()
   {
      return (Integer) get(ResourceField.CURRENCY_UNIQUE_ID);
   }

   /**
    * Sets the unique ID of the currency associated with this resource.
    *
    * @param uniqueID currency unique ID
    */
   public void setCurrencyUniqueID(Integer uniqueID)
   {
      set(ResourceField.CURRENCY_UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieve the currency associated with this resource.
    *
    * @return Currency instance
    */
   public Currency getCurrency()
   {
      return getParentFile().getCurrencies().getByUniqueID(getCurrencyUniqueID());
   }

   /**
    * Sets the currency associated with this resource.
    *
    * @param currency Currency instance
    */
   public void setCurrency(Currency currency)
   {
      setCurrencyUniqueID(currency == null ? null : currency.getUniqueID());
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

   /**
    * Clear any cached calculated values which will be affected by this change.
    *
    * @param field modified field
    * @param newValue new value
    */
   @Override protected void handleFieldChange(FieldType field, Object oldValue, Object newValue)
   {
      if (field == ResourceField.UNIQUE_ID)
      {
         getParentFile().getResources().updateUniqueID(this, (Integer) oldValue, (Integer) newValue);

         if (!m_assignments.isEmpty())
         {
            for (ResourceAssignment assignment : m_assignments)
            {
               assignment.setResourceUniqueID((Integer) newValue);
            }
         }

         return;
      }

      clearDependentFields(DEPENDENCY_MAP, field);
   }

   @Override protected boolean getAlwaysCalculatedField(FieldType field)
   {
      return ALWAYS_CALCULATED_FIELDS.contains(field);
   }

   @Override protected Function<Resource, Object> getCalculationMethod(FieldType field)
   {
      return CALCULATED_FIELD_MAP.get(field);
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

   private Duration calculateWorkVariance()
   {
      Duration variance = null;
      Duration work = getWork();
      Duration baselineWork = getBaselineWork();
      if (work != null && baselineWork != null)
      {
         variance = Duration.getInstance(work.getDuration() - baselineWork.convertUnits(work.getUnits(), getParentFile().getProjectProperties()).getDuration(), work.getUnits());
      }
      return variance;
   }

   private Double calculateCV()
   {
      return Double.valueOf(NumberHelper.getDouble(getBCWP()) - NumberHelper.getDouble(getACWP()));
   }

   private Boolean calculateOverallocated()
   {
      Number peakUnits = getPeakUnits();
      Number maxUnits = getMaxUnits();
      return Boolean.valueOf(NumberHelper.getDouble(peakUnits) > NumberHelper.getDouble(maxUnits));
   }

   /**
    * Supply a default value for the resource type.
    *
    * @return resource type default value
    */
   private ResourceType defaultType()
   {
      return ResourceType.WORK;
   }

   /**
    * Supply a default value for the role flag.
    *
    * @return role flag default value
    */
   private Boolean defaultRoleFlag()
   {
      return Boolean.FALSE;
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
    * Supply a default value for the active flag.
    *
    * @return calculate active flag default value
    */
   private Boolean defaultActive()
   {
      return Boolean.TRUE;
   }

   /**
    * Supply a default value for the default units.
    *
    * @return default value for default units
    */
   private Number defaultDefaultUnits()
   {
      return DEFAULT_DEFAULT_UNITS;
   }

   /**
    * Supply a default value for the resource code values.
    *
    * @return default value for resource code values
    */
   private Map<ResourceCode, ResourceCodeValue> defaultResourceCodeValues()
   {
      return new HashMap<>();
   }

   /**
    * Supply a default value for the role code values.
    *
    * @return default value for role code values
    */
   private Map<RoleCode, RoleCodeValue> defaultRoleCodeValues()
   {
      return new HashMap<>();
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

   private Rate calculateStandardRate()
   {
      CostRateTableEntry entry = getCurrentCostRateTableEntry(0);
      if (entry == null)
      {
         return null;
      }
      return entry.getStandardRate();
   }

   private Rate calculateOvertimeRate()
   {
      CostRateTableEntry entry = getCurrentCostRateTableEntry(0);
      if (entry == null)
      {
         return null;
      }
      return entry.getOvertimeRate();
   }

   private Number calculateCostPerUse()
   {
      CostRateTableEntry entry = getCurrentCostRateTableEntry(0);
      if (entry == null)
      {
         return null;
      }
      return entry.getCostPerUse();
   }

   private String calculateMaterialLabel()
   {
      UnitOfMeasure uom = getUnitOfMeasure();
      return uom == null ? null : uom.getAbbreviation();
   }

   private LocalDateTime calculateStart()
   {
      return m_assignments.stream().map(ResourceAssignment::getStart).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
   }

   private LocalDateTime calculateFinish()
   {
      return m_assignments.stream().map(ResourceAssignment::getFinish).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
   }

   private Number calculateMaxUnits()
   {
      Availability entry = getCurrentAvailabilityTableEntry();
      return entry == null ? null : entry.getUnits();
   }

   private LocalDateTime calculateAvailableFrom()
   {
      return m_availability.availableFrom(LocalDateTime.now());
   }

   private LocalDateTime calculateAvailableTo()
   {
      return m_availability.availableTo(LocalDateTime.now());
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
    * List of all assignments for this resource.
    */
   private final List<ResourceAssignment> m_assignments = new ArrayList<>();
   private final Map<Resource, SkillLevel> m_roleAssignments = new HashMap<>();

   /**
    * This list holds references to all resources that are children of the
    * current resource.
    */
   private final List<Resource> m_children = new ArrayList<>();

   private boolean m_null;
   private String m_activeDirectoryGUID;

   private final CostRateTable[] m_costRateTables;
   private final AvailabilityTable m_availability = new AvailabilityTable();

   private static final Set<FieldType> ALWAYS_CALCULATED_FIELDS = new HashSet<>(Arrays.asList(ResourceField.STANDARD_RATE, ResourceField.OVERTIME_RATE, ResourceField.COST_PER_USE, ResourceField.START, ResourceField.FINISH, ResourceField.MAX_UNITS, ResourceField.AVAILABLE_FROM, ResourceField.AVAILABLE_TO));

   private static final Map<FieldType, Function<Resource, Object>> CALCULATED_FIELD_MAP = new HashMap<>();
   static
   {
      CALCULATED_FIELD_MAP.put(ResourceField.COST_VARIANCE, Resource::calculateCostVariance);
      CALCULATED_FIELD_MAP.put(ResourceField.WORK_VARIANCE, Resource::calculateWorkVariance);
      CALCULATED_FIELD_MAP.put(ResourceField.CV, Resource::calculateCV);
      CALCULATED_FIELD_MAP.put(ResourceField.SV, Resource::calculateSV);
      CALCULATED_FIELD_MAP.put(ResourceField.OVERALLOCATED, Resource::calculateOverallocated);
      CALCULATED_FIELD_MAP.put(ResourceField.STANDARD_RATE, Resource::calculateStandardRate);
      CALCULATED_FIELD_MAP.put(ResourceField.OVERTIME_RATE, Resource::calculateOvertimeRate);
      CALCULATED_FIELD_MAP.put(ResourceField.COST_PER_USE, Resource::calculateCostPerUse);
      CALCULATED_FIELD_MAP.put(ResourceField.MATERIAL_LABEL, Resource::calculateMaterialLabel);
      CALCULATED_FIELD_MAP.put(ResourceField.MAX_UNITS, Resource::calculateMaxUnits);
      CALCULATED_FIELD_MAP.put(ResourceField.AVAILABLE_FROM, Resource::calculateAvailableFrom);
      CALCULATED_FIELD_MAP.put(ResourceField.AVAILABLE_TO, Resource::calculateAvailableTo);
      CALCULATED_FIELD_MAP.put(ResourceField.START, Resource::calculateStart);
      CALCULATED_FIELD_MAP.put(ResourceField.FINISH, Resource::calculateFinish);
      CALCULATED_FIELD_MAP.put(ResourceField.TYPE, Resource::defaultType);
      CALCULATED_FIELD_MAP.put(ResourceField.ROLE, Resource::defaultRoleFlag);
      CALCULATED_FIELD_MAP.put(ResourceField.CALCULATE_COSTS_FROM_UNITS, Resource::defaultCalculateCostsFromUnits);
      CALCULATED_FIELD_MAP.put(ResourceField.ACTIVE, Resource::defaultActive);
      CALCULATED_FIELD_MAP.put(ResourceField.DEFAULT_UNITS, Resource::defaultDefaultUnits);
      CALCULATED_FIELD_MAP.put(ResourceField.RESOURCE_CODE_VALUES, Resource::defaultResourceCodeValues);
      CALCULATED_FIELD_MAP.put(ResourceField.ROLE_CODE_VALUES, Resource::defaultRoleCodeValues);
   }

   private static final Map<FieldType, List<FieldType>> DEPENDENCY_MAP = new HashMap<>();
   static
   {
      FieldContainerDependencies<FieldType> dependencies = new FieldContainerDependencies<>(DEPENDENCY_MAP);
      dependencies.calculatedField(ResourceField.COST_VARIANCE).dependsOn(ResourceField.COST, ResourceField.BASELINE_COST);
      dependencies.calculatedField(ResourceField.CV).dependsOn(ResourceField.BCWP, ResourceField.ACWP);
      dependencies.calculatedField(ResourceField.SV).dependsOn(ResourceField.BCWP, ResourceField.BCWS);
      dependencies.calculatedField(ResourceField.OVERALLOCATED).dependsOn(ResourceField.PEAK, ResourceField.MAX_UNITS);
      dependencies.calculatedField(ResourceField.MATERIAL_LABEL).dependsOn(ResourceField.UNIT_OF_MEASURE_UNIQUE_ID);
   }

   private static final Number DEFAULT_DEFAULT_UNITS = Double.valueOf(100.0);
}
