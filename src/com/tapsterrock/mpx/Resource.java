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


/**
 * This class represents the Resource record as found in an MPX file.
 */
public class Resource extends MPXRecord
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
               set(x, new MPXUnits(field));
               break;
            }

            case PERCENTAGE_WORK_COMPLETE:
            case PEAK:
            {
               set(x, new MPXPercentage (field));
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
               set (x, new MPXDuration (field));
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
      throws MPXException
   {
      if (m_notes != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_notes = new ResourceNotes(getParentFile());

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
    * This method allows a resource calendar to be added to a resource.
    *
    * @return ResourceCalendar
    * @throws MPXException if more than one calendar is added
    */
   public ResourceCalendar addResourceCalendar ()
      throws MPXException
   {
      if (m_calendar != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_calendar = new ResourceCalendar(getParentFile());

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
   ResourceCalendar addResourceCalendar (Record record)
     throws MPXException
   {
      if (m_calendar != null)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_calendar = new ResourceCalendar(getParentFile(), record);

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
    * @see #PERCENTAGE_WORK_COMPLETE Constants for explanation
    */
   public void setPercentageWorkComplete (double val)
   {
      setPercentage (PERCENTAGE_WORK_COMPLETE, new MPXPercentage (val));
   }

   /**
    * Sets the percentage work Complete
    *
    * @param val percentage value
    * @see #PERCENTAGE_WORK_COMPLETE Constants for explanation
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
    * @see #ACTUAL_COST Constants for explanation
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
    * @see #ACTUAL_WORK Constants for explanation
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
    * @see #BASE_CALENDAR Constants for explanation
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
    * @see #BASELINE_COST for explanation
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
    * @see #BASELINE_WORK
    */
   public void setBaselineWork (MPXDuration val)
   {
      set (BASELINE_WORK, val);
   }

   /**
    * Sets code field value
    *
    * @param val value
    * @see #CODE for explanation
    */
   public void setCode (String val)
   {
      set (CODE, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - val to be set
    * @see #COST Constants for explanation
    */
   public void setCost (Number val)
   {
      setCurrency (COST, val);
   }

   /**
    * Sets cost per use field value
    *
    * @param val value
    * @see #COST_PER_USE Constants for explanation
    */
   public void setCostPerUse (Number val)
   {
      setCurrency (COST_PER_USE, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - val to be set
    * @see #COST_VARIANCE Constants for explanation
    */
   public void setCostVariance (double val)
   {
      set (COST_VARIANCE, new MPXCurrency (getParentFile().getCurrencyFormat(), val));
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - val to be set
    * @see #COST_VARIANCE Constants for explanation
    */
   public void setCostVariance (Number val)
   {
      setCurrency (COST_VARIANCE, val);
   }

   /**
    * Sets E--mail Address field value
    *
    * @param val value
    * @see #EMAIL_ADDRESS Constants for explanation
    */
   public void setEmailAddress (String val)
   {
      set (EMAIL_ADDRESS, val);
   }

   /**
    * Sets Group field value
    *
    * @param val value
    * @see #GROUP Constants for explanation
    */
   public void setGroup (String val)
   {
      set (GROUP, val);
   }

   /**
    * Sets ID field value
    *
    * @param val value
    * @see #ID Constants for explanation
    */
   public void setID (int val)
   {
      set (ID, val);
   }

   /**
    * Sets ID field value
    *
    * @param val value
    * @see #ID Constants for explanation
    */
   public void setID (Integer val)
   {
      set (ID, val);
   }

   /**
    * Sets Initials field value
    *
    * @param val value
    * @see #INITIALS Constants for explanation
    */
   public void setInitials (String val)
   {
      set (INITIALS, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    * @see #LINKED_FIELDS Constants for explanation
    */
   public void setLinkedFields (String val)
   {
      set (LINKED_FIELDS, val);
   }

   /**
    * Sets Max Units field value
    *
    * @param val value
    * @see #MAX_UNITS Constants for explanation
    */
   public void setMaxUnits (Number val)
   {
      setUnits (MAX_UNITS, val);
   }

   /**
    * Sets Max Units field value
    *
    * @param val value
    * @see #MAX_UNITS Constants for explanation
    */
   public void setMaxUnits (double val)
   {
      set (MAX_UNITS, new MPXUnits(val));
   }

   /**
    * Sets Name field value
    *
    * @param val value
    * @see #NAME Constants for explanation
    */
   public void setName (String val)
   {
      set (NAME, val);
   }

   /**
    * Sets Notes field value
    *
    * @param val value
    * @see #NOTES Constants for explanation
    */
   public void setNotes (String val)
   {
      set (NOTES, val);
   }

   /**
    * Set objects.
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    * @see #OBJECTS Constants for explanation
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
    * @see #OVERALLOCATED Constants for explanation
    */
   public void setOverallocated (String val)
   {
      set (OVERALLOCATED, val);
   }

   /**
    * Sets overtime rate for this resource
    *
    * @param val value
    * @see #OVERTIME_RATE Constants for explanation
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
    * @see #OVERTIME_WORK Constants for explanation
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
    * @see #PEAK Constants for explanation
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
    * @see #PEAK Constants for explanation
    */
   public void setPeak (Number val)
   {
      setPercentage (PEAK, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - val to be set
    * @see #REMAINING_COST Constants for explanation
    */
   public void setRemainingCost (Number val)
   {
      setCurrency (REMAINING_COST, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    * @see #REMAINING_COST Constants for explanation
    */
   public void setRemainingWork (MPXDuration val)
   {
      set (REMAINING_WORK, val);
   }

   /**
    * Sets standard rate for this resource
    *
    * @param val value
    * @see #STANDARD_RATE Constants for explanation
    */
   public void setStandardRate (MPXRate val)
   {
      set (STANDARD_RATE, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    * @see #TEXT1 for explanation
    */
   public void setText1 (String val)
   {
      set (TEXT1, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    * @see #TEXT1 for explanation
    */
   public void setText2 (String val)
   {
      set (TEXT2, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    * @see #TEXT1 for explanation
    */
   public void setText3 (String val)
   {
      set (TEXT3, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    * @see #TEXT1 for explanation
    */
   public void setText4 (String val)
   {
      set (TEXT4, val);
   }

   /**
    * Additional text
    *
    * @param val text to set
    * @see #TEXT1 for explanation
    */
   public void setText5 (String val)
   {
      set (TEXT5, val);
   }

   /**
    * Sets Unique ID of this resource
    *
    * @param val UID
    * @see #UNIQUE_ID Constants for explanation
    */
   public void setUniqueID (int val)
   {
      set (UNIQUE_ID, val);
   }

   /**
    * Sets Unique ID of this resource
    *
    * @param val UID
    * @see #UNIQUE_ID Constants for explanation
    */
   public void setUniqueID (Integer val)
   {
      set (UNIQUE_ID, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    * @see #WORK Constants for explanation
    */
   public void setWork (MPXDuration val)
   {
      set (WORK, val);
   }

   /**
    * This field is ignored on import into MS Project
    *
    * @param val - value to be set
    * @see #WORK_VARIANCE Constants for explanation
    */
   public void setWorkVariance (MPXDuration val)
   {
      set (WORK_VARIANCE, val);
   }

   /**
    * get Percentage of work completed
    *
    * @return percentage value
    * @see #PERCENTAGE_WORK_COMPLETE Constants for explanation
    */
   public double getPercentageWorkCompleteValue ()
   {
      return (getDoubleValue(PERCENTAGE_WORK_COMPLETE));
   }

   /**
    * get Percentage of work completed
    *
    * @return percentage value
    * @see #PERCENTAGE_WORK_COMPLETE Constants for explanation
    */
   public Number getPercentageWorkComplete ()
   {
      return ((Number)get(PERCENTAGE_WORK_COMPLETE));
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
    * returns the Actual Cost field shows the sum of costs incurred for
    * the work already performed by a
    * resource for all assigned tasks.
    * @return financial value
    * @see #ACTUAL_COST Constants
    */
   public Number getActualCost ()
   {
      return ((Number)get(ACTUAL_COST));
   }

   /**
    * Gets the Actual Work field contains the amount of work that has already
    * been done for all
    * assignments assigned to a resource.
    * @return duration value
    * @see #ACTUAL_WORK Constants for explanation
    */
   public MPXDuration getActualWork ()
   {
      return ((MPXDuration)get(ACTUAL_WORK));
   }

   /**
    * Gets the Base Calendar field indicates which calendar is the base
    * calendar for a resource calendar.
    * The list includes the three built-in calendars, as well as any new base
    * calendars you have
    * created in the Change Working Time dialog box.
    *
    * @return calendar name
    * @see #BASE_CALENDAR Constants for explanation
    */
   public String getBaseCalendar ()
   {
      return (String)get(BASE_CALENDAR);
   }

   /**
    * Gets the Baseline Cost field shows the total planned cost for a
    * resource for all assigned tasks.
    * Baseline cost is also referred to as budget at completion (BAC).
    *
    * @return currency value
    * @see #BASELINE_COST Constants for explanation
    */
   public Number getBaselineCost ()
   {
      return ((Number)get(BASELINE_COST));
   }

   /**
    * Get baseline work.
    * Field ignored on import to Microsoft Project.
    *
    * @return duration
    * @see #BASELINE_WORK Constants for explanation
    */
   public MPXDuration getBaselineWork ()
   {
      return ((MPXDuration)get(BASELINE_WORK));
   }

   /**
    * Gets code field value
    *
    * @return value
    * @see #CODE Constants for explanation
    */
   public String getCode ()
   {
      return ((String)get(CODE));
   }

   /**
    * Gets Cost field value
    *
    * @return value
    * @see #COST Constants for explanation
    */
   public Number getCost ()
   {
      return ((Number)get(COST));
   }

   /**
    * Gets Cost Per Use field value
    *
    * @return value
    * @see #COST_PER_USE Constants for explanation
    */
   public Number getCostPerUse ()
   {
      return ((Number)get(COST_PER_USE));
   }

   /**
    * Gets Cost Variance field value
    *
    * @return value
    * @see #COST_VARIANCE Constants for explanation
    */
   public Number getCostVariance ()
   {
      return ((Number)get(COST_VARIANCE));
   }

   /**
    * Gets E-mail Address field value
    *
    * @return value
    * @see #EMAIL_ADDRESS Constants for explanation
    */
   public String getEmailAddress ()
   {
      return ((String)get(EMAIL_ADDRESS));
   }

   /**
    * Gets Group field value
    *
    * @return value
    * @see #GROUP Constants for explanation
    */
   public String getGroup ()
   {
      return ((String)get(GROUP));
   }

   /**
    * Gets ID field value
    *
    * @return value
    * @see #ID Constants for explanation
    */
   public int getIDValue ()
   {
      return (getIntValue(ID));
   }

   /**
    * Gets ID field value
    *
    * @return value
    * @see #ID Constants for explanation
    */
   public Integer getID ()
   {
      return ((Integer) get(ID));
   }

   /**
    * Gets Initials of name field value
    *
    * @return value
    * @see #INITIALS Constants for explanation
    */
   public String getInitials ()
   {
      return ((String)get(INITIALS));
   }

   /**
    * Gets Linked Fields field value
    *
    * @return value
    * @see #LINKED_FIELDS Constants for explanation
    */
   public String getLinkedFields ()
   {
      return ((String)get(LINKED_FIELDS));
   }

   /**
    * Gets Max Units field value
    *
    * @return value
    * @see #MAX_UNITS Constants for explanation
    */
   public Number getMaxUnits ()
   {
      return ((Number)get(MAX_UNITS));
   }

   /**
    * Gets Resource Name field value
    *
    * @return value
    * @see #NAME Constants for explanation
    */
   public String getName ()
   {
      return ((String)get(NAME));
   }

   /**
    * Gets Notes field value
    *
    * @return value
    * @see #NOTES Constants for explanation
    */
   public String getNotes ()
   {
      return ((String)get(NOTES));
   }

   /**
    * Gets objects field value
    *
    * @return value
    * @see #OBJECTS Constants for explanation
    */
   public int getObjectsValue ()
   {
      return (getIntValue(OBJECTS));
   }

   /**
    * Gets objects field value
    *
    * @return value
    * @see #OBJECTS Constants for explanation
    */
   public Integer getObjects ()
   {
      return ((Integer)get (OBJECTS));
   }

   /**
    * Gets Overallocated field value
    *
    * @return value
    * @see #OVERALLOCATED Constants for explanation
    */
   public String getOverallocated ()
   {
      return ((String)get(OVERALLOCATED));
   }

   /**
    * Gets Overtime Rate field value
    *
    * @return value
    * @see #OVERTIME_RATE Constants for explanation
    */
   public MPXRate getOvertimeRate ()
   {
      return ((MPXRate)get(OVERTIME_RATE));
   }

   /**
    * Gets Overtime Work field value
    *
    * @return value
    * @see #OVERTIME_WORK Constants for explanation
    */
   public MPXDuration getOvertimeWork ()
   {
      return ((MPXDuration)get(OVERTIME_WORK));
   }

   /**
    * Gets Peak field value
    *
    * @return value
    * @see #PEAK Constants for explanation
    */
   public String getPeak ()
   {
      return ((String)get(PEAK));
   }

   /**
    * Gets remaining Cost field value
    *
    * @return value
    * @see #REMAINING_COST Constants for explanation
    */
   public Number getRemainingCost ()
   {
      return ((Number)get(REMAINING_COST));
   }

   /**
    * Gets Remaining Work field value
    *
    * @return value
    * @see #REMAINING_WORK Constants for explanation
    */
   public MPXDuration getRemainingWork ()
   {
      return ((MPXDuration)get(REMAINING_WORK));
   }

   /**
    * Gets Standard Rate field value
    *
    * @return MPXRate
    * @see #STANDARD_RATE Constants for explanation
    */
   public MPXRate getStandardRate ()
   {
      return ((MPXRate)get(STANDARD_RATE));
   }

   /**
    * Gets Text 1 field value
    *
    * @return value
    * @see #TEXT1 Constants for explanation
    */
   public String getText1 ()
   {
      return ((String)get(TEXT1));
   }

   /**
    * Gets Text 2 field value
    *
    * @return value
    * @see #TEXT1 Constants for explanation
    */
   public String getText2 ()
   {
      return ((String)get(TEXT2));
   }

   /**
    * Gets Text3 field value
    *
    * @return value
    * @see #TEXT1  for explanation
    */
   public String getText3 ()
   {
      return ((String)get(TEXT3));
   }

   /**
    * Gets Text 4 field value
    *
    * @return value
    * @see #TEXT1  for explanation
    */
   public String getText4 ()
   {
      return ((String)get(TEXT4));
   }

   /**
    * Gets Text 5 field value
    *
    * @return value
    * @see #TEXT1  for explanation
    */
   public String getText5 ()
   {
      return ((String)get(TEXT5));
   }

   /**
    * Gets Unique ID field value
    *
    * @return value
    * @see #UNIQUE_ID for explanation
    */
   public int getUniqueIDValue ()
   {
      return (getIntValue(UNIQUE_ID));
   }

   /**
    * Gets Unique ID field value
    *
    * @return value
    * @see #UNIQUE_ID for explanation
    */
   public Integer getUniqueID ()
   {
      return ((Integer)get (UNIQUE_ID));
   }

   /**
    * Gets Work field value
    *
    * @return value
    * @see #WORK for explanation
    */
   public MPXDuration getWork ()
   {
      return ((MPXDuration)get(WORK));
   }

   /**
    * Gets work variance field value
    *
    * @return value
    * @see #WORK_VARIANCE for explanation
    */
   public MPXDuration getWorkVariance ()
   {
      return ((MPXDuration)get(WORK_VARIANCE));
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
   private ResourceCalendar m_calendar;

   /**
    * Resource notes for this resource.
    */
   private ResourceNotes m_notes;

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
   private static final int NOTES = 10;

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
   public static final int RECORD_NUMBER = 50;
}
