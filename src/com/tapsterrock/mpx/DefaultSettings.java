/*
 * file:       DefaultSettings.java
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
 * This class represents the record in an MPX file that holds a range of
 * default value relating to the current project plan.
 */
public final class DefaultSettings extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   DefaultSettings (MPXFile file)
   {
      super(file, 0);

      setDefaultDurationUnits(TimeUnit.DAYS);
      setDefaultDurationIsFixed(false);
      setDefaultWorkUnits(TimeUnit.HOURS);
      setDefaultHoursInDay(new Float(8));
      setDefaultHoursInWeek(new Float (40));
      setDefaultStandardRate(new MPXRate(10, TimeUnit.HOURS));
      setDefaultOvertimeRate(new MPXRate(15, TimeUnit.HOURS));
      setUpdatingTaskStatusUpdatesResourceStatus(true);
      setSplitInProgressTasks(false);
   }

   /**
    * This method updates a default settings instance with data from
    * an MPX file.
    *
    * @param record record containing the data for  this object.
    * @throws MPXException Thrown on parse errors
    */
   void update (Record record)
      throws MPXException
   {
      setDefaultDurationUnits(record.getTimeUnit(0));
      setNumericBooleanDefaultDurationIsFixed(record.getNumericBoolean(1));
      setDefaultWorkUnits(record.getTimeUnit(2));
      setDefaultHoursInDay(record.getFloat(3));
      setDefaultHoursInWeek(record.getFloat(4));
      setDefaultStandardRate(record.getRate(5));
      setDefaultOvertimeRate(record.getRate(6));
      setNumericBooleanUpdatingTaskStatusUpdatesResourceStatus(record.getNumericBoolean(7));
      setNumericBooleanSplitInProgressTasks(record.getNumericBoolean(8));
   }

   /**
    * Gets Default Duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @return int constant
    * @see TimeUnit
    */
   public TimeUnit getDefaultDurationUnits ()
   {
      return (m_defaultDurationUnits);
   }

   /**
    * Default duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @param units default time units
    * @see TimeUnit
    */
   public void setDefaultDurationUnits (TimeUnit units)
   {
      m_defaultDurationUnits = units;
   }

   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   private NumericBoolean getNumericBooleanDefaultDurationIsFixed ()
   {
      return (m_defaultDurationIsFixed);
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   private void setNumericBooleanDefaultDurationIsFixed (NumericBoolean fixed)
   {
      m_defaultDurationIsFixed = fixed;
   }
   
   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   public boolean getDefaultDurationIsFixed ()
   {
      return (m_defaultDurationIsFixed.booleanValue());
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   public void setDefaultDurationIsFixed (boolean fixed)
   {
      m_defaultDurationIsFixed = NumericBoolean.getInstance(fixed);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @return int representing default
    * @see TimeUnit
    */
   public TimeUnit getDefaultWorkUnits ()
   {
      return (m_defaultWorkUnits);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @param units  int representing default
    * @see TimeUnit
    */
   public void setDefaultWorkUnits (TimeUnit units)
   {
      m_defaultWorkUnits = units;
   }

   /**
    * Gets the default number of hours in a day
    *
    * @return number of hours
    */
   public Float getDefaultHoursInDay ()
   {
      return (m_defaultHoursInDay);
   }

   /**
    * Sets the default number of hours in a day
    *
    * @param hours number of hours
    */
   public void setDefaultHoursInDay (Float hours)
   {
      m_defaultHoursInDay = hours;
   }

   /**
    * Gets the default number of hours in a week
    *
    * @return number of hours
    */
   public Float getDefaultHoursInWeek ()
   {
      return (m_defaultHoursInWeek);
   }

   /**
    * Sets the default number of hours in a week
    *
    * @param hours number of hours
    */
   public void setDefaultHoursInWeek (Float hours)
   {
      m_defaultHoursInWeek = hours;
   }

   /**
    * Get rate
    *
    * @return rate
    */
   public MPXRate getDefaultStandardRate ()
   {
      return (m_defaultStandardRate);
   }

   /**
    * Set rate
    *
    * @param rate default standard rate
    */
   public void setDefaultStandardRate (MPXRate rate)
   {
      m_defaultStandardRate = rate;
   }

   /**
    * Get overtime rate
    *
    * @return rate
    */
   public MPXRate getDefaultOvertimeRate ()
   {
      return (m_defaultOvertimeRate);
   }

   /**
    * Set default overtime rate
    *
    * @param rate default overtime rate
    */
   public void setDefaultOvertimeRate (MPXRate rate)
   {
      m_defaultOvertimeRate = rate;
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   private NumericBoolean getNumericBooleanUpdatingTaskStatusUpdatesResourceStatus ()
   {
      return (m_updatingTaskStatusUpdatesResourceStatus);
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setNumericBooleanUpdatingTaskStatusUpdatesResourceStatus (NumericBoolean flag)
   {
      m_updatingTaskStatusUpdatesResourceStatus = flag;
   }
   
   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   public boolean getUpdatingTaskStatusUpdatesResourceStatus ()
   {
      return (m_updatingTaskStatusUpdatesResourceStatus.booleanValue());
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus (boolean flag)
   {
      m_updatingTaskStatusUpdatesResourceStatus = NumericBoolean.getInstance(flag);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return Boolean value
    */
   private NumericBoolean getNumericBooleanSplitInProgressTasks ()
   {
      return (m_splitInProgressTasks);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag boolean value
    */
   private void setNumericBooleanSplitInProgressTasks (NumericBoolean flag)
   {
      m_splitInProgressTasks = flag;
   }
   
   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return Boolean value
    */
   public boolean getSplitInProgressTasks ()
   {
      return (m_splitInProgressTasks.booleanValue());
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag boolean value
    */
   public void setSplitInProgressTasks (boolean flag)
   {
      m_splitInProgressTasks = NumericBoolean.getInstance(flag);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer buffer = new StringBuffer ();
      char delimiter = getParentFile().getDelimiter();

      buffer.append (RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append(format(getDefaultDurationUnits()));
      buffer.append (delimiter);
      buffer.append(format(getNumericBooleanDefaultDurationIsFixed()));
      buffer.append (delimiter);
      buffer.append(format(getDefaultWorkUnits()));
      buffer.append (delimiter);
      buffer.append(format(getDefaultHoursInDay()));
      buffer.append (delimiter);
      buffer.append(format(getDefaultHoursInWeek()));
      buffer.append (delimiter);
      buffer.append(format(getDefaultStandardRate()));
      buffer.append (delimiter);
      buffer.append(format(getDefaultOvertimeRate()));
      buffer.append (delimiter);
      buffer.append(format(getNumericBooleanUpdatingTaskStatusUpdatesResourceStatus()));
      buffer.append (delimiter);
      buffer.append(format(getNumericBooleanSplitInProgressTasks()));      
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);
                  
      return (buffer.toString());      
   }
   
   private TimeUnit m_defaultDurationUnits;
   private NumericBoolean m_defaultDurationIsFixed;
   private TimeUnit m_defaultWorkUnits;
   private Float m_defaultHoursInDay;
   private Float m_defaultHoursInWeek;
   private MPXRate m_defaultStandardRate;
   private MPXRate m_defaultOvertimeRate;
   private NumericBoolean m_updatingTaskStatusUpdatesResourceStatus;
   private NumericBoolean m_splitInProgressTasks; 
   
   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 11;
}
