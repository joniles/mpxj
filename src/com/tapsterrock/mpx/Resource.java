/*
 * file:       Resource.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpx;

import java.util.Date;


/**
 * This class represents the Resource record as found in an MPX file.
 */
public final class Resource extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @throws MPXException normally thrown for paring errors
    */
   Resource (MPXFile file)
      throws MPXException
   {
      this (file, Record.EMPTY_RECORD);
   }

   /**
    * This constructor populates an instance of the Resource class
    * using values read in from an MPXFile record.
    *
    * @param file parent MPX file
    * @param record record from MPX file
    * @throws MPXException normally thrown for paring errors
    */
   Resource (MPXFile file, Record record)
      throws MPXException
   {
      super (file, MAX_FIELDS);

      m_model = getParentFile().getResourceModel();

      int i = 0;
      int length = record.getLength();
      int[] model = m_model.getModel();

      while (i < length)
      {
         int x = model[i];
         if (x == -1)
         {
            break;
         }

         String field = record.getString (i++);

         if (field == null || field.length() == 0)
         {
            continue;
         }

         switch (x)
         {
            case ID:
            case UNIQUE_ID:
            case OBJECTS:
            {
               set(x,Integer.valueOf(field));
               break;
            }

            case MAX_UNITS:
            {
               set(x, new MPXUnits(field, getParentFile().getUnitsDecimalFormat()));
               break;
            }

            case PERCENTAGE_WORK_COMPLETE:
            case PEAK:
            {
               set(x, new MPXPercentage (field, getParentFile().getPercentageDecimalFormat()));
               break;
            }

            case COST:
            case COST_PER_USE:
            case COST_VARIANCE:
            case BASELINE_COST:
            case ACTUAL_COST:
            case REMAINING_COST:
            {
               set(x, new MPXCurrency(getParentFile().getCurrencyFormat(), field));
               break;
            }

            case OVERTIME_RATE:
            case STANDARD_RATE:
            {
               set (x, new MPXRate(getParentFile().getCurrencyFormat(), field));
               break;
            }

            case REMAINING_WORK:
            case OVERTIME_WORK:
            case BASELINE_WORK:
            case ACTUAL_WORK:
            case WORK:
            case WORK_VARIANCE:
            {
               set (x, new MPXDuration (field, getParentFile().getDurationDecimalFormat()));
               break;
            }

            case ACCRUE_AT:
            {
               set (x, AccrueType.getInstance (field));
               break;
            }

            default:
            {
               set (x, field);
               break;
            }
         }
      }

      if (file.getAutoResourceUniqueID() == true)
      {
         setUniqueID (file.getResourceUniqueID ());
      }

      if (file.getAutoResourceID() == true)
      {
         setID (file.getResourceID ());
      }
   }


   /**
    * This method allows a resource note to be added to a resource.
    *
    * @param notes notes to be added
    * @return ResourceNotes
    * @throws MPXException  if MSP defined limit of 1 is exceeded
    */
   public ResourceNotes addResourceNotes (String notes)
   {
      if (m_notes == null)
      {
         m_notes = new ResourceNotes(getParentFile());
      }
      
      m_notes.setNotes(notes);

      return (m_notes);
   }

   /**
    * This method allows a resource note to be added to a resource.
    *
    * @return ResourceNotes
    * @throws MPXException  if MSP defined limit of 1 is exceeded
    */
   public ResourceNotes addResourceNotes ()
      throws MPXException
   {
      return (addResourceNotes (""));
   }

   /**
    * This method allows a resource note to be added to a resource.
    * The data to populate the resource note comes from a record
    * read from an MPX file.
    *
    * @param record Record containing the data for this object.
    * @return ResourceNotes
    * @throws MPXException If MSP defined limit of 1 is exceeded
    */
   ResourceNotes addResourceNotes (Record record)
      throws MPXException
   {
      if (m_notes != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_notes = new ResourceNotes(getParentFile(), record);

      return (m_notes);
   }

   /**
    * This method retrieves the calendar associated with this resource.
    * 
    * @return MPXCalendar instance
    */
   public MPXCalendar getResourceCalendar ()
   {
      return (m_calendar);   
   }
   
   /**
    * This package private method allows a pre-existing resource calendar
    * to be attched to a resource.
    * 
    * @param calendar resource calendar
    */
   void attachResourceCalendar (MPXCalendar calendar)
   {
      m_calendar = calendar;   
   }
      
   /**
    * This method allows a resource calendar to be added to a resource.
    *
    * @return ResourceCalendar
    * @throws MPXException if more than one calendar is added
    */
   public MPXCalendar addResourceCalendar ()
      throws MPXException
   {
      if (m_calendar != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_calendar = new MPXCalendar(getParentFile(), false);

      return (m_calendar);
   }

   /**
    * This method allows a resource calendar to be added to a resource.
    * The data to populate the resource calendar comes from a record.
    *
    * @param record Record containing the data for this object.
    * @return ResourceCalendar
    * @throws MPXException if more than one calendar is added
    */
   MPXCalendar addResourceCalendar (Record record)
     throws MPXException
   {
      if (m_calendar != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_calendar = new MPXCalendar(getParentFile(), record, false);

      return (m_calendar);
   }

   /**
    * This method is used to set the value of a field in the resource,
    * and also to ensure that the field exists in the resource model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void set (int field, Object val)
   {
      m_model.add (field);
      put (field, val);
   }

   /**
    * This method is used to set the value of a field in the resource,
    * and also to ensure that the field exists in the resource model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void set (int field, int val)
   {
      m_model.add (field);
      put (field, val);
   }

   /**
    * This method is used to set the value of a currency field in the resource,
    * and also to ensure that the field exists in the resource model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void setCurrency (int field, Number val)
   {
      m_model.add (field);
      putCurrency (field, val);
   }

   /**
    * This method is used to set the value of a units field in the resource,
    * and also to ensure that the field exists in the resource model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void setUnits (int field, Number val)
   {
      m_model.add (field);
      putUnits (field, val);
   }

   /**
    * This method is used to set the value of a percentage field in the resource,
    * and also to ensure that the field exists in the resource model
    * record.
    *
    * @param field field to be added or updated.
    * @param val new value for field.
    */
   private void setPercentage (int field, Number val)
   {
      m_model.add (field);
      putPercentage (field, val);
   }

   /**
    * Sets the percentage work Complete
    *
    * @param val percentage value
    */
   public void setPercentageWorkComplete (double val)
   {
      setPercentage (PERCENTAGE_WORK_COMPLETE, new MPXPercentage (val));
   }

   /**
    * Sets the percentage work Complete
    *
    * @param val percentage value
    */
   public void setPercentageWorkComplete (Number val)
   {
      setPercentage (PERCENTAGE_WORK_COMPLETE, val);
   }

   /**
    * Sets the Accrue at type.The Accrue At field provides choices for how
    * and when resource standard
    * and overtime costs are to be charged, or accrued, to the cost of a task.
    * The options are: Start, End and Prorated (Default)
    *
    * @param type accrue type
    */
   public void setAccrueAt (AccrueType type)
   {
      set (ACCRUE_AT, type);
   }

   /**
    * The Actual Cost field shows the sum of costs incurred for the work
    * already performed by a
    * resource for all assigned tasks.
    *
    * @param val financial value
    */
   public void setActualCost (Number val)
   {
      setCurrency (ACTUAL_COST, val);
   }

   /**
    * Sets the Actual Work field contains the amount of work that has already
    * been done for all
    * assignments assigned to a resource.
    *
    * @param val duration value
    */
   public void setActualWork (MPXDuration val)
   {
      set (ACTUAL_WORK, val);
   }

   /**
    * Sets the Base Calendar field indicates which calendar is the base
    * calendar for a resource calendar.
    * The list includes the three built-in calendars, as well as any new base
    * calendars you have
    * created in the Change Working Time dialog box.
    *
    * @param val calendar name
    */
   public void setBaseCalendar (String val)
   {
      set (BASE_CALENDAR,val==null||val.length()==0?"Standard":val);
   }

   /**
    * Sets the baseline cost.
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setBaselineCost (Number val)
   {
      setCurrency (BASELINE_COST, val);
   }

   /**
    * Sets the baseline work duration.
    * This field is ignored on import into MS Project.
    *
    * @param val - value to be set
    */
   public void setBaselineWork (MPXDuration val)
   {
      set (BASELINE_WORK, val);
   }

   /**
    * Sets code field value
    *
    * @param val value
    */
   public void setCode (String val)
   {
      set (CODE, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - val to be set
    */
   public void setCost (Number val)
   {
      setCurrency (COST, val);
   }

   /**
    * Sets cost per use field value
    *
    * @param val value
    */
   public void setCostPerUse (Number val)
   {
      setCurrency (COST_PER_USE, val);
   }

   /**
    * Sets the cost variance value. This value is calculated by MS Project, 
    * and is ignored on import.
    *
    * @param val Cost variance value
    */
   public void setCostVariance (double val)
   {
      set (COST_VARIANCE, new MPXCurrency (getParentFile().getCurrencyFormat(), val));
   }

   /**
    * Sets the cost variance value. This value is calculated by MS Project, 
    * and is ignored on import.
    *
    * @param val Cost variance value
    */
   public void setCostVariance (Number val)
   {
      setCurrency (COST_VARIANCE, val);
   }

   /**
    * Sets E--mail Address field value
    *
    * @param val value
    */
   public void setEmailAddress (String val)
   {
      set (EMAIL_ADDRESS, val);
   }

   /**
    * Sets Group field value
    *
    * @param val value
    */
   public void setGroup (String val)
   {
      set (GROUP, val);
   }

   /**
    * Sets ID field value
    *
    * @param val value
    */
   public void setID (int val)
   {
      set (ID, val);
   }

   /**
    * Sets ID field value
    *
    * @param val value
    */
   public void setID (Integer val)
   {
      set (ID, val);
   }

   /**
    * Sets Initials field value
    *
    * @param val value
    */
   public void setInitials (String val)
   {
      set (INITIALS, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setLinkedFields (String val)
   {
      set (LINKED_FIELDS, val);
   }

   /**
    * Sets Max Units field value
    *
    * @param val value
    */
   public void setMaxUnits (Number val)
   {
      setUnits (MAX_UNITS, val);
   }

   /**
    * Sets Max Units field value
    *
    * @param val value
    */
   public void setMaxUnits (double val)
   {
      set (MAX_UNITS, new MPXUnits(val));
   }

   /**
    * Sets Name field value
    *
    * @param val value
    */
   public void setName (String val)
   {
      set (NAME, val);
   }

   /**
    * Sets Notes field value
    *
    * @param val value
    */
   public void setNotes (String val)
   {
      addResourceNotes (val);
   }

   /**
    * Set objects.
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setObjects (int val)
   {
      set (OBJECTS, val);
   }

   /**
    * Set overallocated.
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setOverallocated (String val)
   {
      set (OVERALLOCATED, val);
   }

   /**
    * Sets overtime rate for this resource
    *
    * @param val value
    */
   public void setOvertimeRate (MPXRate val)
   {
      set (OVERTIME_RATE, val);
   }

   /**
    * Set overtimework duration.
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setOvertimeWork (MPXDuration val)
   {
      set (OVERTIME_WORK, val);
   }

   /**
    * Set peak.
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setPeak (double val)
   {
      setPercentage (PEAK, new MPXPercentage (val));
   }

   /**
    * Set peak.
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setPeak (Number val)
   {
      setPercentage (PEAK, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - val to be set
    */
   public void setRemainingCost (Number val)
   {
      setCurrency (REMAINING_COST, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setRemainingWork (MPXDuration val)
   {
      set (REMAINING_WORK, val);
   }

   /**
    * Sets standard rate for this resource
    *
    * @param val value
    */
   public void setStandardRate (MPXRate val)
   {
      set (STANDARD_RATE, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    */
   public void setText1 (String val)
   {
      set (TEXT1, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    */
   public void setText2 (String val)
   {
      set (TEXT2, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    */
   public void setText3 (String val)
   {
      set (TEXT3, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    */
   public void setText4 (String val)
   {
      set (TEXT4, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    */
   public void setText5 (String val)
   {
      set (TEXT5, val);
   }

   /**
    * Sets Unique ID of this resource
    *
    * @param val Unique ID
    */
   public void setUniqueID (int val)
   {
      set (UNIQUE_ID, val);
   }

   /**
    * Sets Unique ID of this resource
    *
    * @param val Unique ID
    */
   public void setUniqueID (Integer val)
   {
      set (UNIQUE_ID, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setWork (MPXDuration val)
   {
      set (WORK, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    */
   public void setWorkVariance (MPXDuration val)
   {
      set (WORK_VARIANCE, val);
   }

   /**
    * get Percentage of work completed
    *
    * @return percentage value
    */
   public double getPercentageWorkCompleteValue ()
   {
      return (getDoubleValue(PERCENTAGE_WORK_COMPLETE));
   }

   /**
    * get Percentage of work completed
    *
    * @return percentage value
    */
   public Number getPercentageWorkComplete ()
   {
      return ((Number)get(PERCENTAGE_WORK_COMPLETE));
   }

   /**
    * Gets the Accrue at type.The Accrue At field provides choices for how
    * and when resource standard
    * and overtime costs are to be charged, or accrued, to the cost of a task.
    * The options are: Start, End and Prorated (Default)
    *
    * @return accrue type
    */
   public int getAccrueAtValue ()
   {
      int result;
      AccrueType accrue = (AccrueType)get(ACCRUE_AT);
      if (accrue == null)
      {
         result = AccrueType.PRORATED;
      }
      else
      {
         result = accrue.getType();
      }

      return (result);
   }

   /**
    * Gets the Accrue at type.The Accrue At field provides choices for how
    * and when resource standard
    * and overtime costs are to be charged, or accrued, to the cost of a task.
    * The options are: Start, End and Proraetd (Default)
    *
    * @return accrue type
    */
   public AccrueType getAccrueAt ()
   {
      return ((AccrueType)get(ACCRUE_AT));
   }

   /**
    * Retrieves the Actual Cost value, which shows the sum of 
    * costs incurred for the work already performed by a
    * resource for all assigned tasks.
    * 
    * @return Actual cost value
    */
   public Number getActualCost ()
   {
      return ((Number)get(ACTUAL_COST));
   }

   /**
    * Retrieves the Actual Work field contains the amount of work that has 
    * already been done for all assignments assigned to a resource.
    * 
    * @return Actual work value
    */
   public MPXDuration getActualWork ()
   {
      return ((MPXDuration)get(ACTUAL_WORK));
   }

   /**
    * Retrieves Base Calendar name associated with this resource.
    * This field indicates which calendar is the base
    * calendar for a resource calendar.
    *
    * @return Base calendar name
    */
   public String getBaseCalendar ()
   {
      return (String)get(BASE_CALENDAR);
   }

   /**
    * Retrieves the Baseline Cost value. This value is the total planned 
    * cost for a resource for all assigned tasks. Baseline cost is also 
    * referred to as budget at completion (BAC).
    *
    * @return Baseline cost value
    */
   public Number getBaselineCost ()
   {
      return ((Number)get(BASELINE_COST));
   }

   /**
    * Retrieves the Baseline Work value.
    *
    * @return Baseline work value
    */
   public MPXDuration getBaselineWork ()
   {
      return ((MPXDuration)get(BASELINE_WORK));
   }

   /**
    * Gets code field value
    *
    * @return value
    */
   public String getCode ()
   {
      return ((String)get(CODE));
   }

   /**
    * Gets Cost field value
    *
    * @return value
    */
   public Number getCost ()
   {
      return ((Number)get(COST));
   }

   /**
    * Gets Cost Per Use field value
    *
    * @return value
    */
   public Number getCostPerUse ()
   {
      return ((Number)get(COST_PER_USE));
   }

   /**
    * Gets Cost Variance field value
    *
    * @return value
    */
   public double getCostVarianceValue ()
   {
      return (getDoubleValue(COST_VARIANCE));
   }

   /**
    * Gets Cost Variance field value
    *
    * @return value
    */
   public Number getCostVariance ()
   {
      return ((Number)get(COST_VARIANCE));
   }

   /**
    * Gets E-mail Address field value
    *
    * @return value
    */
   public String getEmailAddress ()
   {
      return ((String)get(EMAIL_ADDRESS));
   }

   /**
    * Gets Group field value
    *
    * @return value
    */
   public String getGroup ()
   {
      return ((String)get(GROUP));
   }

   /**
    * Gets ID field value
    *
    * @return value
    */
   public int getIDValue ()
   {
      return (getIntValue(ID));
   }

   /**
    * Gets ID field value
    *
    * @return value
    */
   public Integer getID ()
   {
      return ((Integer) get(ID));
   }

   /**
    * Gets Initials of name field value
    *
    * @return value
    */
   public String getInitials ()
   {
      return ((String)get(INITIALS));
   }

   /**
    * Gets Linked Fields field value
    *
    * @return value
    */
   public String getLinkedFields ()
   {
      return ((String)get(LINKED_FIELDS));
   }

   /**
    * Gets Max Units field value
    *
    * @return value
    */
   public double getMaxUnitsValue ()
   {
      return (getDoubleValue (MAX_UNITS));
   }

   /**
    * Gets Max Units field value
    *
    * @return value
    */
   public Number getMaxUnits ()
   {
      return ((Number)get(MAX_UNITS));
   }

   /**
    * Gets Resource Name field value
    *
    * @return value
    */
   public String getName ()
   {
      return ((String)get(NAME));
   }

   /**
    * Gets Notes field value
    *
    * @return value
    */
   public String getNotes ()
   {
      String result;
      if (m_notes != null)
      {
         result = m_notes.getNotes();
      }
      else
      {
         result = "";           
      }         
      
      return (result);
   }

   /**
    * Gets objects field value
    *
    * @return value
    */
   public int getObjectsValue ()
   {
      return (getIntValue(OBJECTS));
   }

   /**
    * Gets objects field value
    *
    * @return value
    */
   public Integer getObjects ()
   {
      return ((Integer)get (OBJECTS));
   }

   /**
    * Gets Overallocated field value
    *
    * @return value
    */
   public String getOverallocated ()
   {
      return ((String)get(OVERALLOCATED));
   }

   /**
    * Gets Overtime Rate field value
    *
    * @return value
    */
   public MPXRate getOvertimeRate ()
   {
      return ((MPXRate)get(OVERTIME_RATE));
   }

   /**
    * Gets Overtime Work field value
    *
    * @return value
    */
   public MPXDuration getOvertimeWork ()
   {
      return ((MPXDuration)get(OVERTIME_WORK));
   }

   /**
    * Gets Peak field value
    *
    * @return value
    */
   public double getPeakValue ()
   {
      return (getDoubleValue (PEAK));
   }

   /**
    * Gets Peak field value
    *
    * @return value
    */
   public Number getPeak ()
   {
      return ((Number)get(PEAK));
   }

   /**
    * Gets remaining Cost field value
    *
    * @return value
    */
   public Number getRemainingCost ()
   {
      return ((Number)get(REMAINING_COST));
   }

   /**
    * Gets Remaining Work field value
    *
    * @return value
    */
   public MPXDuration getRemainingWork ()
   {
      return ((MPXDuration)get(REMAINING_WORK));
   }

   /**
    * Gets Standard Rate field value
    *
    * @return MPXRate
    */
   public MPXRate getStandardRate ()
   {
      return ((MPXRate)get(STANDARD_RATE));
   }

   /**
    * Gets Text 1 field value
    *
    * @return value
    */
   public String getText1 ()
   {
      return ((String)get(TEXT1));
   }

   /**
    * Gets Text 2 field value
    *
    * @return value
    */
   public String getText2 ()
   {
      return ((String)get(TEXT2));
   }

   /**
    * Gets Text3 field value
    *
    * @return value
    */
   public String getText3 ()
   {
      return ((String)get(TEXT3));
   }

   /**
    * Gets Text 4 field value
    *
    * @return value
    */
   public String getText4 ()
   {
      return ((String)get(TEXT4));
   }

   /**
    * Gets Text 5 field value
    *
    * @return value
    */
   public String getText5 ()
   {
      return ((String)get(TEXT5));
   }

   /**
    * Gets Unique ID field value
    *
    * @return value
    */
   public int getUniqueIDValue ()
   {
      return (getIntValue(UNIQUE_ID));
   }

   /**
    * Gets Unique ID field value
    *
    * @return value
    */
   public Integer getUniqueID ()
   {
      return ((Integer)get (UNIQUE_ID));
   }

   /**
    * Gets Work field value
    *
    * @return value
    */
   public MPXDuration getWork ()
   {
      return ((MPXDuration)get(WORK));
   }

   /**
    * Gets work variance field value
    *
    * @return value
    */
   public MPXDuration getWorkVariance ()
   {
      return ((MPXDuration)get(WORK_VARIANCE));
   }

	/**
	 * Retrieve the value of the regular work field.
	 * Note that this value is an extension to the MPX specification.
	 * 
	 * @return Regular work value
	 */
	public MPXDuration getRegularWork ()
	{
		return m_regularWork;
	}

	/**
	 * Set the value of the regular work field.
	 * Note that this value is an extension to the MPX specification.

	 * @param duration Regular work value
	 */
	public void setRegularWork (MPXDuration duration)
	{
		m_regularWork = duration;
	}

	/**
	 * Retrieve the value of the overtime cost field.
	 * Note that this value is an extension to the MPX specification.
    *
	 * @return Overtime cost value
	 */
	public Number getOvertimeCost()
	{
		return m_overtimeCost;
	}

	/**
	 * Set the value of the overtime cost field.
	 * Note that this value is an extension to the MPX specification.
	 *
	 * @param currency Overtime cost
	 */
	public void setOvertimeCost (Number currency)
	{
		m_overtimeCost = currency;
	}

	/**
	 * Retrieve the value of the actual overtime cost field.
	 * Note that this value is an extension to the MPX specification.
	 *
	 * @return Actual overtime cost value
	 */
	public Number getActualOvertimeCost()
	{
		return m_actualOvertimeCost;
	}

	/**
	 * Set the value of the actual overtime cost field.
	 * Note that this value is an extension to the MPX specification.

	 * @param number Actual overtime cost
	 */
	public void setActualOvertimeCost(Number number)
	{
		m_actualOvertimeCost = number;
	}

	/**
	 * Retrieve the value of the remaining overtime cost field.
	 * Note that this value is an extension to the MPX specification.
	 *
	 * @return Remaining overtime cost value
	 */
	public Number getRemainingOvertimeCost()
	{
		return m_remainingOvertimeCost;
	}

	/**
	 * Set the value of the remaining overtime cost field.
	 * Note that this value is an extension to the MPX specification.
    * 
	 * @param number Remaining overtime cost
	 */
	public void setRemainingOvertimeCost(Number number)
	{
		m_remainingOvertimeCost = number;
	}

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText6()
   {
      return m_text6;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText7()
   {
      return m_text7;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText8()
   {
      return m_text8;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText9()
   {
      return m_text9;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText10()
   {
      return m_text10;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText11()
   {
      return m_text11;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText12()
   {
      return m_text12;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText13()
   {
      return m_text13;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText14()
   {
      return m_text14;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText15()
   {
      return m_text15;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText16()
   {
      return m_text16;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText17()
   {
      return m_text17;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText18()
   {
      return m_text18;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText19()
   {
      return m_text19;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText20()
   {
      return m_text20;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText21()
   {
      return m_text21;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText22()
   {
      return m_text22;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText23()
   {
      return m_text23;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText24()
   {
      return m_text24;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText25()
   {
      return m_text25;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText26()
   {
      return m_text26;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText27()
   {
      return m_text27;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText28()
   {
      return m_text28;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText29()
   {
      return m_text29;
   }

   /**
    * Retrieves a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Text value
    */
   public String getText30()
   {
      return m_text30;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText6(String string)
   {
      m_text6 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText7(String string)
   {
      m_text7 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText8(String string)
   {
      m_text8 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText9(String string)
   {
      m_text9 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText10(String string)
   {
      m_text10 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText11(String string)
   {
      m_text11 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText12(String string)
   {
      m_text12 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText13(String string)
   {
      m_text13 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText14(String string)
   {
      m_text14 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText15(String string)
   {
      m_text15 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText16(String string)
   {
      m_text16 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText17(String string)
   {
      m_text17 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText18(String string)
   {
      m_text18 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText19(String string)
   {
      m_text19 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText20(String string)
   {
      m_text20 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText21(String string)
   {
      m_text21 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText22(String string)
   {
      m_text22 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText23(String string)
   {
      m_text23 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText24(String string)
   {
      m_text24 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText25(String string)
   {
      m_text25 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText26(String string)
   {
      m_text26 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText27(String string)
   {
      m_text27 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText28(String string)
   {
      m_text28 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText29(String string)
   {
      m_text29 = string;
   }

   /**
    * Sets a text value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param string Text value
    */
   public void setText30(String string)
   {
      m_text30 = string;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart1()
   {
      return m_start1;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart2()
   {
      return m_start2;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart3()
   {
      return m_start3;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart4()
   {
      return m_start4;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart5()
   {
      return m_start5;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart6()
   {
      return m_start6;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart7()
   {
      return m_start7;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart8()
   {
      return m_start8;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart9()
   {
      return m_start9;
   }

   /**
    * Retrieves a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date start date
    */
   public Date getStart10()
   {
      return m_start10;
   }
   
   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart1(Date date)
   {
      m_start1 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart2(Date date)
   {
      m_start2 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart3(Date date)
   {
      m_start3 = date;
   }
   
   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart4(Date date)
   {
      m_start4 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart5(Date date)
   {
      m_start5 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart6(Date date)
   {
      m_start6 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart7(Date date)
   {
      m_start7 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart8(Date date)
   {
      m_start8 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart9(Date date)
   {
      m_start9 = date;
   }

   /**
    * Sets a start date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Start date
    */
   public void setStart10(Date date)
   {
      m_start10 = date;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish1()
   {
      return m_finish1;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish2()
   {
      return m_finish2;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish3()
   {
      return m_finish3;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish4()
   {
      return m_finish4;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish5()
   {
      return m_finish5;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish6()
   {
      return m_finish6;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish7()
   {
      return m_finish7;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish8()
   {
      return m_finish8;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish9()
   {
      return m_finish9;
   }

   /**
    * Retrieves a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date finish date
    */
   public Date getFinish10()
   {
      return m_finish10;
   }
   
   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish1(Date date)
   {
      m_finish1 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish2(Date date)
   {
      m_finish2 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish3(Date date)
   {
      m_finish3 = date;
   }
   
   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish4(Date date)
   {
      m_finish4 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish5(Date date)
   {
      m_finish5 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish6(Date date)
   {
      m_finish6 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish7(Date date)
   {
      m_finish7 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish8(Date date)
   {
      m_finish8 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish9(Date date)
   {
      m_finish9 = date;
   }

   /**
    * Sets a finish date.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Finish date
    */
   public void setFinish10(Date date)
   {
      m_finish10 = date;
   }
   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber1 (Double val)
   {
      m_number1 = val;
   }

   /**
    * Retrieves a numeric value
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber1Value ()
   {
      return (getDoubleValue (m_number1));
   }

   /**
    * Retrieves a numeric value
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber1 ()
   {
      return (m_number1);
   }

   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber2 (Double val)
   {
      m_number2 = val;
   }

   /**
    * Retrieves a numeric value
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber2Value ()
   {
      return (getDoubleValue (m_number2));
   }

   /**
    * Retrieves a numeric value
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber2 ()
   {
      return (m_number2);
   }

   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber3 (Double val)
   {
      m_number3 = val;
   }

   /**
    * Retrieves a numeric value
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber3Value ()
   {
      return (getDoubleValue (m_number3));
   }

   /**
    * Retrieves a numeric value
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber3 ()
   {
      return (m_number3);
   }

   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber4 (Double val)
   {
      m_number4 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber4Value ()
   {
      return (getDoubleValue (m_number4));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber4 ()
   {
      return (m_number4);
   }

   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber5 (Double val)
   {
      m_number5 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber5Value ()
   {
      return (getDoubleValue (m_number5));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber5 ()
   {
      return (m_number5);
   }


   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber6 (Double val)
   {
      m_number6 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber6Value ()
   {
      return (getDoubleValue (m_number6));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber6 ()
   {
      return (m_number6);
   }

   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber7 (Double val)
   {
      m_number7 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber7Value ()
   {
      return (getDoubleValue (m_number7));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber7 ()
   {
      return (m_number7);
   }
      
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber8 (Double val)
   {
      m_number8 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber8Value ()
   {
      return (getDoubleValue (m_number8));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber8 ()
   {
      return (m_number8);
   }

        
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber9 (Double val)
   {
      m_number9 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber9Value ()
   {
      return (getDoubleValue (m_number9));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber9 ()
   {
      return (m_number9);
   }
               
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber10 (Double val)
   {
      m_number10 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber10Value ()
   {
      return (getDoubleValue (m_number10));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber10 ()
   {
      return (m_number10);
   }

   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber11 (Double val)
   {
      m_number11 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber11Value ()
   {
      return (getDoubleValue (m_number11));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber11 ()
   {
      return (m_number11);
   }
        
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber12 (Double val)
   {
      m_number12 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber12Value ()
   {
      return (getDoubleValue (m_number12));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber12 ()
   {
      return (m_number12);
   }
        
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber13 (Double val)
   {
      m_number13 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber13Value ()
   {
      return (getDoubleValue (m_number13));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber13 ()
   {
      return (m_number13);
   }
        
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber14 (Double val)
   {
      m_number14 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber14Value ()
   {
      return (getDoubleValue (m_number14));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber14 ()
   {
      return (m_number14);
   }
   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber15 (Double val)
   {
      m_number15 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber15Value ()
   {
      return (getDoubleValue (m_number15));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber15 ()
   {
      return (m_number15);
   }
   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber16 (Double val)
   {
      m_number16 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber16Value ()
   {
      return (getDoubleValue (m_number16));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber16 ()
   {
      return (m_number16);
   }
   
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber17 (Double val)
   {
      m_number17 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber17Value ()
   {
      return (getDoubleValue (m_number17));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber17 ()
   {
      return (m_number17);
   }
        
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber18 (Double val)
   {
      m_number18 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber18Value ()
   {
      return (getDoubleValue (m_number18));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber18 ()
   {
      return (m_number18);
   }
        
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber19 (Double val)
   {
      m_number19 = val;
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public double getNumber19Value ()
   {
      return (getDoubleValue (m_number19));
   }

   /**
    * Retrieves a numeric value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Numeric value
    */               
   public Double getNumber19 ()
   {
      return (m_number19);
   }
       
   /**
    * Sets a numeric value.
    * Note that this value is an extension to the MPX specification.
    *
    * @param val Numeric value
    */
   public void setNumber20 (Double val)
   {
      m_number20 = val;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration1()
   {
      return m_duration1;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration2()
   {
      return m_duration2;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration3()
   {
      return m_duration3;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration4()
   {
      return m_duration4;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration5()
   {
      return m_duration5;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration6()
   {
      return m_duration6;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration7()
   {
      return m_duration7;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration8()
   {
      return m_duration8;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration9()
   {
      return m_duration9;
   }

   /**
    * Retrieves a duration.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return MPXDuration
    */
   public MPXDuration getDuration10()
   {
      return m_duration10;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration1(MPXDuration duration)
   {
      m_duration1 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration2(MPXDuration duration)
   {
      m_duration2 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration3(MPXDuration duration)
   {
      m_duration3 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration4(MPXDuration duration)
   {
      m_duration4 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration5(MPXDuration duration)
   {
      m_duration5 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration6(MPXDuration duration)
   {
      m_duration6 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration7(MPXDuration duration)
   {
      m_duration7 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration8(MPXDuration duration)
   {
      m_duration8 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration9(MPXDuration duration)
   {
      m_duration9 = duration;
   }

   /**
    * Sets a duration value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param duration Duration value
    */
   public void setDuration10(MPXDuration duration)
   {
      m_duration10 = duration;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate1()
   {
      return m_date1;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate10()
   {
      return m_date10;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate2()
   {
      return m_date2;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate3()
   {
      return m_date3;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate4()
   {
      return m_date4;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate5()
   {
      return m_date5;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate6()
   {
      return m_date6;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate7()
   {
      return m_date7;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate8()
   {
      return m_date8;
   }

   /**
    * Retrieves a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Date value
    */
   public Date getDate9()
   {
      return m_date9;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate1(Date date)
   {
      m_date1 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate10(Date date)
   {
      m_date10 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate2(Date date)
   {
      m_date2 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate3(Date date)
   {
      m_date3 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate4(Date date)
   {
      m_date4 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate5(Date date)
   {
      m_date5 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate6(Date date)
   {
      m_date6 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate7(Date date)
   {
      m_date7 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate8(Date date)
   {
      m_date8 = date;
   }

   /**
    * Sets a date value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param date Date value
    */
   public void setDate9(Date date)
   {
      m_date9 = date;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost1()
   {
      return m_cost1;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost2()
   {
      return m_cost2;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost3()
   {
      return m_cost3;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost4()
   {
      return m_cost4;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost5()
   {
      return m_cost5;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost6()
   {
      return m_cost6;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost7()
   {
      return m_cost7;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost8()
   {
      return m_cost8;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost9()
   {
      return m_cost9;
   }

   /**
    * Retrieves a cost.
    * Note that this value is an extension to the MPX specification.
    * 
    * @return Cost value
    */
   public Number getCost10()
   {
      return m_cost10;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost1(Number number)
   {
      m_cost1 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost2(Number number)
   {
      m_cost2 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost3(Number number)
   {
      m_cost3 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost4(Number number)
   {
      m_cost4 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost5(Number number)
   {
      m_cost5 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost6(Number number)
   {
      m_cost6 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost7(Number number)
   {
      m_cost7 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost8(Number number)
   {
      m_cost8 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost9(Number number)
   {
      m_cost9 = number;
   }

   /**
    * Sets a cost value.
    * Note that this value is an extension to the MPX specification.
    * 
    * @param number Cost value
    */
   public void setCost10(Number number)
   {
      m_cost10 = number;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag1()
   {
      return m_flag1;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag2()
   {
      return m_flag2;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag3()
   {
      return m_flag3;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag4()
   {
      return m_flag4;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag5()
   {
      return m_flag5;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag6()
   {
      return m_flag6;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag7()
   {
      return m_flag7;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag8()
   {
      return m_flag8;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag9()
   {
      return m_flag9;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag10()
   {
      return m_flag10;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag11()
   {
      return m_flag11;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag12()
   {
      return m_flag12;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag13()
   {
      return m_flag13;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag14()
   {
      return m_flag14;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag15()
   {
      return m_flag15;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag16()
   {
      return m_flag16;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag17()
   {
      return m_flag17;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag18()
   {
      return m_flag18;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag19()
   {
      return m_flag19;
   }

   /**
    * Retrieves the flag value.
    * 
    * @return flag value
    */
   public boolean getFlag20()
   {
      return m_flag20;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag1(boolean b)
   {
      m_flag1 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag2(boolean b)
   {
      m_flag2 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag3(boolean b)
   {
      m_flag3 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag4(boolean b)
   {
      m_flag4 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag5(boolean b)
   {
      m_flag5 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag6(boolean b)
   {
      m_flag6 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag7(boolean b)
   {
      m_flag7 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag8(boolean b)
   {
      m_flag8 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag9(boolean b)
   {
      m_flag9 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag10(boolean b)
   {
      m_flag10 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag11(boolean b)
   {
      m_flag11 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag12(boolean b)
   {
      m_flag12 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag13(boolean b)
   {
      m_flag13 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag14(boolean b)
   {
      m_flag14 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag15(boolean b)
   {
      m_flag15 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag16(boolean b)
   {
      m_flag16 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag17(boolean b)
   {
      m_flag17 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag18(boolean b)
   {
      m_flag18 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag19(boolean b)
   {
      m_flag19 = b;
   }

   /**
    * Sets the flag value
    * 
    * @param b flag value
    */
   public void setFlag20(boolean b)
   {
      m_flag20 = b;
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode1 (String value)
   {
      m_outlineCode1 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode1 ()
   {
      return (m_outlineCode1);         
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode2 (String value)
   {
      m_outlineCode2 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode2 ()
   {
      return (m_outlineCode2);         
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode3 (String value)
   {
      m_outlineCode3 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode3 ()
   {
      return (m_outlineCode3);         
   }

   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode4 (String value)
   {
      m_outlineCode4 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode4 ()
   {
      return (m_outlineCode4);         
   }
                  
   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode5 (String value)
   {
      m_outlineCode5 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode5 ()
   {
      return (m_outlineCode5);         
   }
                  
   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode6 (String value)
   {
      m_outlineCode6 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode6 ()
   {
      return (m_outlineCode6);         
   }
                  
   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode7 (String value)
   {
      m_outlineCode7 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode7 ()
   {
      return (m_outlineCode7);         
   }
                  
   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode8 (String value)
   {
      m_outlineCode8 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode8 ()
   {
      return (m_outlineCode8);         
   }
                  
   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode9 (String value)
   {
      m_outlineCode9 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode9 ()
   {
      return (m_outlineCode9);         
   }
                  
   /**
    * Sets the value of an outline code field.
    *
    * @param value outline code value
    */
   public void setOutlineCode10 (String value)
   {
      m_outlineCode10 = value;
   }

   /**
    * Retrieves the value of an outline code field.
    * 
    * @return outline code value
    */
   public String getOutlineCode10 ()
   {
      return (m_outlineCode10);         
   }

   /**
    * Set the "available from" date
    * 
    * @param date available from date
    */
   public void setAvailableFrom (Date date)
   {
      m_availableFrom = date;   
   }
   
   /**
    * Retrieves the "available from" date
    * 
    * @return available from date
    */
   public Date getAvailableFrom ()
   {
      return (m_availableFrom);   
   }

   /**
    * Set the "available to" date
    * 
    * @param date available to date
    */   
   public void setAvailableTo (Date date)
   {
      m_availableTo = date;   
   }

   /**
    * Retrieves the "available to" date
    * 
    * @return available from date
    */   
   public Date getAvailableTo ()
   {
      return (m_availableTo);   
   }
   
   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer();

      /** @todo review this and add reset to MPXFile */
      if (m_model.getWritten() == false)
      {
         buf.append(m_model.toString());
         m_model.setWritten (true);
      }

      //
      // Write the resource record
      //
      buf.append (toString (RECORD_NUMBER, m_model.getModel()));

      //
      // Write the resource notes
      //
      if (m_notes != null)
      {
         buf.append (m_notes.toString());
      }

      //
      // Write the resource calendar
      //
      if (m_calendar != null)
      {
         buf.append (m_calendar.toString());
      }

      return (buf.toString());
   }


   /**
    * Resource Model record controlling fields written to resource record
    */
   private ResourceModel m_model;

   /**
    * Resource calendar for this resource
    */
   private MPXCalendar m_calendar;

   /**
    * Resource notes for this resource.
    */
   private ResourceNotes m_notes;

	/**
	 * The following member variables are extended attributes. They are
	 * do not form part of the MPX file format definition, and are neither
	 * loaded from an MPX file, or saved to an MPX file. Their purpose
	 * is to provide storage for attributes which are defined by later versions
	 * of Microsoft Project. This allows these attributes to be manipulated
	 * when they have been retrieved from file formats other than MPX.
	 */
	private MPXDuration m_regularWork;
	private Number m_overtimeCost;
	private Number m_actualOvertimeCost;
	private Number m_remainingOvertimeCost;
   private String m_text6;
   private String m_text7;
   private String m_text8;
   private String m_text9;
   private String m_text10;   
   private String m_text11;
   private String m_text12;
   private String m_text13;
   private String m_text14;
   private String m_text15;
   private String m_text16;
   private String m_text17;
   private String m_text18;
   private String m_text19;
   private String m_text20;                                                               
   private String m_text21;   
   private String m_text22;
   private String m_text23;
   private String m_text24;
   private String m_text25;
   private String m_text26;
   private String m_text27;
   private String m_text28;
   private String m_text29;
   private String m_text30;
   private Date m_start1;
   private Date m_start2;
   private Date m_start3;
   private Date m_start4;
   private Date m_start5;
   private Date m_start6;
   private Date m_start7;
   private Date m_start8;
   private Date m_start9;
   private Date m_start10;
   private Date m_finish1;
   private Date m_finish2;
   private Date m_finish3;
   private Date m_finish4;
   private Date m_finish5;
   private Date m_finish6;
   private Date m_finish7;
   private Date m_finish8;
   private Date m_finish9;
   private Date m_finish10;
   private Double m_number1;
   private Double m_number2;
   private Double m_number3;
   private Double m_number4;   
   private Double m_number5;
   private Double m_number6;
   private Double m_number7;                                    
   private Double m_number8;   
   private Double m_number9;   
   private Double m_number10;   
   private Double m_number11;      
   private Double m_number12;   
   private Double m_number13;   
   private Double m_number14;   
   private Double m_number15;   
   private Double m_number16;   
   private Double m_number17;   
   private Double m_number18;   
   private Double m_number19;   
   private Double m_number20;   
   private MPXDuration m_duration1;
   private MPXDuration m_duration2;
   private MPXDuration m_duration3;			
   private MPXDuration m_duration4;
   private MPXDuration m_duration5;
   private MPXDuration m_duration6;
   private MPXDuration m_duration7;
   private MPXDuration m_duration8;
   private MPXDuration m_duration9;                  
   private MPXDuration m_duration10;   
   private Date m_date1;
   private Date m_date2;
   private Date m_date3;
   private Date m_date4;
   private Date m_date5;
   private Date m_date6;
   private Date m_date7;
   private Date m_date8;
   private Date m_date9;
   private Date m_date10;
   private Number m_cost1;
   private Number m_cost2;
   private Number m_cost3;
   private Number m_cost4;
   private Number m_cost5;
   private Number m_cost6;
   private Number m_cost7;
   private Number m_cost8;
   private Number m_cost9;
   private Number m_cost10;
   private boolean m_flag1;
   private boolean m_flag2;
   private boolean m_flag3;
   private boolean m_flag4;
   private boolean m_flag5;
   private boolean m_flag6;
   private boolean m_flag7;
   private boolean m_flag8;
   private boolean m_flag9;
   private boolean m_flag10;         	
   private boolean m_flag11;
   private boolean m_flag12;
   private boolean m_flag13;
   private boolean m_flag14;
   private boolean m_flag15;
   private boolean m_flag16;
   private boolean m_flag17;
   private boolean m_flag18;
   private boolean m_flag19;
   private boolean m_flag20;
   private String m_outlineCode1;
   private String m_outlineCode2;
   private String m_outlineCode3;
   private String m_outlineCode4;
   private String m_outlineCode5;
   private String m_outlineCode6;
   private String m_outlineCode7;
   private String m_outlineCode8;
   private String m_outlineCode9;
   private String m_outlineCode10;
   private Date m_availableFrom;
   private Date m_availableTo;
   
   /**
    * The % Work Complete field contains the current status of all tasks
    * assigned to a resource,
    * expressed as the total percentage of the resource's work that has
    * been completed.
    */
   private static final int PERCENTAGE_WORK_COMPLETE = 26;

   /**
    * The Accrue At field provides choices for how and when resource
    * standard and overtime costs
    * are to be charged, or accrued, to the cost of a task. The options are:
    * - Start
    * - End
    * - Prorated (default)
    */
   private static final int ACCRUE_AT = 45;

   /**
    * The Actual Cost field shows the sum of costs incurred for the work
    * already performed
    * by a resource for all assigned tasks.
    */
   private static final int ACTUAL_COST = 32;

   /**
    * The Actual Work field contains the amount of work that has already
    * been done for all
    * assignments assigned to a resource.
    */
   private static final int ACTUAL_WORK = 22;

   /**
    * The Base Calendar field indicates which calendar is the base calendar
    * for a resource calendar.
    * The list includes the three built-in calendars, as well as any new base
    * calendars you have
    * created in the Change Working Time dialog box.
    */
   private static final int BASE_CALENDAR = 48;

   /**
    * The Baseline Cost field shows the total planned cost for a resource
    * for all assigned tasks.
    * Baseline cost is also referred to as budget at completion (BAC).
    */
   private static final int BASELINE_COST = 31;

   /**
    * The Baseline Work field shows the originally planned amount of work to
    * be performed for all
    * assignments assigned to a resource. This field shows the planned
    * person-hours scheduled for
    * a resource. Information in the Baseline Work field becomes available
    * when you set a baseline
    * for the project.
    */
   private static final int BASELINE_WORK = 21;

   /**
    * The Code field contains any code, abbreviation, or number you want to
    * enter as part of a
    * resource's information.
    */
   private static final int CODE = 4;

   /**
    * The Cost field shows the total scheduled cost for a resource for all
    * assigned tasks.
    *  Cost is based on costs already incurred for work performed by the
    *  resource on all
    * assigned tasks, in addition to the costs planned for the remaining work.
    */
   private static final int COST = 30;

   /**
    * The Cost Per Use field shows the cost that accrues each time a
    * resource is used.
    */
   private static final int COST_PER_USE = 44;

   /**
    * The Cost Variance field shows the difference between the baseline cost
    * and total cost for
    * a resource. This is also referred to as variance at completion (VAC).
    */
   private static final int COST_VARIANCE = 34;

   /**
    * The Email Address field contains the e-mail address of a resource.
    * If the Email Address
    * field is blank, Microsoft Project uses the name in the Name field as
    * the e-mail address.
    */
   private static final int EMAIL_ADDRESS = 11;

   /**
    * The Group field contains the name of the group to which
    * a resource belongs.
    */
   private static final int GROUP = 3;

   /**
    * The ID field contains the identifier number that Microsoft Project
    * automatically assigns
    * to each resource. The ID indicates the position of a resource in
    * relation to the other resources.
    */
   private static final int ID = 40;

   /**
    * The Initials field shows the abbreviation for a resource name.
    */
   private static final int INITIALS = 2;

   /**
    * The Linked Fields field indicates whether there are OLE links
    * to the resource,
    * either from elsewhere in the active project, another Microsoft Project
    * file, or from another program.
    */
   private static final int LINKED_FIELDS = 51;

   /**
    * The Max Units field contains the maximum percentage or number of units
    * representing the maximum
    * capacity for which a resource is available to accomplish any tasks.
    * The default for the Max Units
    * field is 100 percent.
    */
   private static final int MAX_UNITS = 41;

   /**
    * The Name field contains the name of a resource.
    */
   private static final int NAME = 1;

   /**
    * The Notes field contains notes that you can enter about a resource.
    * You can use resource
    * notes to help maintain information about a resource.
    */
   //private static final int NOTES = 10;

   /**
    * The Objects field contains the number of objects associated
    * with a resource.
    */
   private static final int OBJECTS = 50;

   /**
    * The Overallocated field indicates whether a resource is assigned to do
    * more work on
    * all assigned tasks than can be done within the resource's
    * normal work capacity.
    */
   private static final int OVERALLOCATED = 46;

   /**
    * The Overtime Rate field shows the rate of pay for overtime work
    * performed by a resource.
    */
   private static final int OVERTIME_RATE = 43;

   /**
    * The Overtime Work field contains the amount of overtime to be
    * performed for all
    * tasks assigned to a resource and charged at the resource's overtime rate.
    */
   private static final int OVERTIME_WORK = 24;

   /**
    * The Peak field contains the maximum percentage or number of units
    * for which a resource
    * is assigned at any one time for all tasks assigned to the resource.
    */
   private static final int PEAK = 47;

   /**
    * The Remaining Cost field shows the remaining scheduled expense that
    * will be incurred
    * in completing the remaining work assigned to a resource.
    * This applies to all work
    * assigned to the resource for all assigned tasks.
    */
   private static final int REMAINING_COST = 33;

   /**
    * The Remaining Work field contains the amount of time, or person-hours,
    * still required by a resource to complete all assigned tasks.
    */
   private static final int REMAINING_WORK = 23;

   /**
    * The Standard Rate field shows the rate of pay for regular, nonovertime
    * work performed by a resource.
    */
   private static final int STANDARD_RATE = 42;

   /**
    * The Text fields show any custom text information you want to enter in your
    * project regarding resources.
    */
   private static final int TEXT1 = 5;

   /**
    * The Text fields show any custom text information you want to enter in your
    * project regarding resources.
    */
   private static final int TEXT2 = 6;

   /**
    * The Text fields show any custom text information you want to enter in your
    * project regarding resources.
    */
   private static final int TEXT3 = 7;

   /**
    * The Text fields show any custom text information you want to enter in your
    * project regarding resources.
    */
   private static final int TEXT4 = 8;

   /**
    * The Text fields show any custom text information you want to enter in your
    * project regarding resources.
    */
   private static final int TEXT5 = 9;

   /**
    * The Unique ID field contains the number that Microsoft Project
    * automatically
    * designates whenever a new resource is added. This number indicates
    * the sequence
    * in which the resource was added to the project, regardless of
    * placement in the sheet.
    */
   private static final int UNIQUE_ID = 49;

   /**
    * The Work field contains the total amount of work scheduled to be
    * performed by a
    * resource on all assigned tasks. This field shows the total work,
    * or person-hours, for a resource.
    */
   private static final int WORK = 20;

   /**
    * The Work Variance field contains the difference between a resource's
    * total baseline work
    * and the currently scheduled work.
    */
   private static final int WORK_VARIANCE = 25;

   /**
    * Maximum number of fields in this record. Note that this is
    * package access to allow the model to get at it.
    */
   static final int MAX_FIELDS = 52;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 50;

}
