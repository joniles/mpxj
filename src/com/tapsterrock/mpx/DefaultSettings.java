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
      super(file, MAX_FIELDS);

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
      setDefaultDurationUnits(record.getInteger(0));
      setDefaultDurationIsFixed(record.getNumericBoolean(1));
      setDefaultWorkUnits(record.getInteger(2));
      setDefaultHoursInDay(record.getFloat(3));
      setDefaultHoursInWeek(record.getFloat(4));
      setDefaultStandardRate(record.getRate(5));
      setDefaultOvertimeRate(record.getRate(6));
      setUpdatingTaskStatusUpdatesResourceStatus(record.getNumericBoolean(7));
      setSplitInProgressTasks(record.getNumericBoolean(8));
   }

   /**
    * Gets Default Duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @return int constant
    * @see TimeUnit
    */
   public int getDefaultDurationUnitsValue ()
   {
      return (getIntValue (DEFAULT_DURATION_UNITS));
   }

   /**
    * Gets Default Duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @return int constant
    * @see TimeUnit
    */
   public Integer getDefaultDurationUnits ()
   {
      return ((Integer)get (DEFAULT_DURATION_UNITS));
   }

   /**
    * Default duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @param units default time units
    * @see TimeUnit
    */
   public void setDefaultDurationUnits (int units)
   {
      put (DEFAULT_DURATION_UNITS, units);
   }

   /**
    * Default duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @param units default time units
    * @see TimeUnit
    */
   public void setDefaultDurationUnits (Integer units)
   {
      put (DEFAULT_DURATION_UNITS, units);
   }

   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   public boolean getDefaultDurationIsFixedValue ()
   {
      return (getNumericBooleanValue (DEFAULT_DURATION_TYPE));
   }

   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   public NumericBoolean getDefaultDurationIsFixed ()
   {
      return ((NumericBoolean)get (DEFAULT_DURATION_TYPE));
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   public void setDefaultDurationIsFixed (boolean fixed)
   {
      put (DEFAULT_DURATION_TYPE, NumericBoolean.getInstance(fixed));
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   public void setDefaultDurationIsFixed (NumericBoolean fixed)
   {
      put (DEFAULT_DURATION_TYPE, fixed);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @return int representing default
    * @see TimeUnit
    */
   public int getDefaultWorkUnitsValue ()
   {
      return (getIntValue (DEFAULT_WORK_UNITS));
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @return int representing default
    * @see TimeUnit
    */
   public Integer getDefaultWorkUnits ()
   {
      return ((Integer)get (DEFAULT_WORK_UNITS));
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @param units  int representing default
    * @see TimeUnit
    */
   public void setDefaultWorkUnits (int units)
   {
      put (DEFAULT_WORK_UNITS, units);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @param units  int representing default
    * @see TimeUnit
    */
   public void setDefaultWorkUnits (Integer units)
   {
      put (DEFAULT_WORK_UNITS, units);
   }

   /**
    * Gets the default number of hours in a day
    *
    * @return number of hours
    */
   public float getDefaultHoursInDayValue ()
   {
      return (getFloatValue (DEFAULT_HOURS_IN_DAY));
   }

   /**
    * Gets the default number of hours in a day
    *
    * @return number of hours
    */
   public Float getDefaultHoursInDay ()
   {
      return ((Float)get (DEFAULT_HOURS_IN_DAY));
   }

   /**
    * Sets the default number of hours in a day
    *
    * @param hours number of hours
    */
   public void setDefaultHoursInDay (Float hours)
   {
      put (DEFAULT_HOURS_IN_DAY, hours);
   }

   /**
    * Gets the default number of hours in a week
    *
    * @return number of hours
    */
   public float getDefaultHoursInWeekValue ()
   {
      return (getFloatValue (DEFAULT_HOURS_IN_WEEK));
   }

   /**
    * Gets the default number of hours in a week
    *
    * @return number of hours
    */
   public Float getDefaultHoursInWeek ()
   {
      return ((Float)get (DEFAULT_HOURS_IN_WEEK));
   }

   /**
    * Sets the default number of hours in a week
    *
    * @param hours number of hours
    */
   public void setDefaultHoursInWeek (Float hours)
   {
      put (DEFAULT_HOURS_IN_WEEK, hours);
   }

   /**
    * Get rate
    *
    * @return rate
    */
   public MPXRate getDefaultStandardRate ()
   {
      return ((MPXRate)get(DEFAULT_STANDARD_RATE));
   }

   /**
    * Set rate
    *
    * @param rate default standard rate
    */
   public void setDefaultStandardRate (MPXRate rate)
   {
      put (DEFAULT_STANDARD_RATE, rate);
   }

   /**
    * Get overtime rate
    *
    * @return rate
    */
   public MPXRate getDefaultOvertimeRate ()
   {
      return ((MPXRate)get(DEFAULT_OVERTIME_RATE));
   }

   /**
    * Set default overtime rate
    *
    * @param rate default overtime rate
    */
   public void setDefaultOvertimeRate (MPXRate rate)
   {
      put (DEFAULT_OVERTIME_RATE, rate);
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   public boolean getUpdatingTaskStatusUpdatesResourceStatusValue ()
   {
      return (getNumericBooleanValue (UPDATE));
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   public NumericBoolean getUpdatingTaskStatusUpdatesResourceStatus ()
   {
      return ((NumericBoolean)get (UPDATE));
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus (NumericBoolean flag)
   {
      put (UPDATE, flag);
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus (boolean flag)
   {
      put (UPDATE, NumericBoolean.getInstance (flag));
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return boolean value
    */
   public boolean getSplitInProgressTasksValue ()
   {
      return (getNumericBooleanValue (SPLIT));
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return Boolean value
    */
   public NumericBoolean getSplitInProgressTasks ()
   {
      return ((NumericBoolean)get (SPLIT));
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag boolean value
    */
   public void setSplitInProgressTasks (boolean flag)
   {
      put (SPLIT, NumericBoolean.getInstance (flag));
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag boolean value
    */
   public void setSplitInProgressTasks (NumericBoolean flag)
   {
      put (SPLIT, flag);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      return (toString (RECORD_NUMBER));
   }


   /**
    * Constant value representing Default Duration Units.  eg 'w','m','d','h'
    */
   private static final int DEFAULT_DURATION_UNITS = 0;

   /**
    * Constant value representing Default Duration Type. eg 'w','m','d','h'
    */
   private static final int DEFAULT_DURATION_TYPE = 1;

   /**
    * Constant value representing Default Work Units. eg 'w','m','d','h'
    */
   private static final int DEFAULT_WORK_UNITS = 2;

   /**
    * Constant value representing Hours in Day field.
    */
   private static final int DEFAULT_HOURS_IN_DAY = 3;

   /**
    * Constant value representing Default hours In Week field.
    */
   private static final int DEFAULT_HOURS_IN_WEEK = 4;

   /**
    * Constant value representing Default Standard Rate  field.
    */
   private static final int DEFAULT_STANDARD_RATE = 5;

   /**
    * Constant value representing Default Overtine Rate field.
    */
   private static final int DEFAULT_OVERTIME_RATE = 6;

   /**
    * Constant value representing Updating Task Status Updates Resource Status field.
    */
   private static final int UPDATE = 7;

   /**
    * Constant value representing Split Tasks field.
    */
   private static final int SPLIT = 8;

   /**
    * Maximum number of fields in this record.
    */
   private static final int MAX_FIELDS = 9;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 11;
}
