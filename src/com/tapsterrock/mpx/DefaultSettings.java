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
public class DefaultSettings extends MPXRecord
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
      setDefaultDurationType(0);
      setDefaultWorkUnits(TimeUnit.HOURS);
      setDefaultHoursInDay(new Float(8));
      setDefaultHoursInWeek(new Float (40));
      setDefaultStandardRate(new MPXRate(10, TimeUnit.HOURS));
      setDefaultOvertimeRate(new MPXRate(15, TimeUnit.HOURS));
      setUpdatingTaskStatusUpdatesResourceStatus(1);
      setSplitInProgressTasks(0);
   }

   /**
    * This method updates a default settings instance with data from
    * an MPX file.
    *
    * @param record record containing the data for  this object.
    */
   void update (Record record)
      throws MPXException
   {
      setDefaultDurationUnits(record.getInteger(0));
      setDefaultDurationType(record.getInteger(1));
      setDefaultWorkUnits(record.getInteger(2));
      setDefaultHoursInDay(record.getFloat(3));
      setDefaultHoursInWeek(record.getFloat(4));
      setDefaultStandardRate(record.getRate(5));
      setDefaultOvertimeRate(record.getRate(6));
      setUpdatingTaskStatusUpdatesResourceStatus(record.getInteger(7));
      setSplitInProgressTasks(record.getInteger(8));
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
    * Gets whether the Default Type is fixed or not
    *
    * @return  0-not fixed,1-fixed
    */
   public int getDefaultDurationTypeValue ()
   {
      return (getIntValue (DEFAULT_DURATION_TYPE));
   }

   /**
    * Gets whether the Default Type is fixed or not
    *
    * @return  0-not fixed,1-fixed
    */
   public Integer getDefaultDurationType ()
   {
      return ((Integer)get (DEFAULT_DURATION_TYPE));
   }

   /**
    * Sets whether the Default Type is fixed or not
    *
    * @param type  0-not fixed,1-fixed
    */
   public void setDefaultDurationType (int type)
   {
      put (DEFAULT_DURATION_TYPE, type);
   }

   /**
    * Sets whether the Default Type is fixed or not
    *
    * @param type  0-not fixed,1-fixed
    */
   public void setDefaultDurationType (Integer type)
   {
      put (DEFAULT_DURATION_TYPE, type);
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
   public String getDefaultOvertimeRate ()
   {
      return ((String)get(DEFAULT_OVERTIME_RATE));
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
    * @return - 0-no,1-yes
    */
   public int getUpdatingTaskStatusUpdatesResourceStatusValue ()
   {
      return (getIntValue (UPDATE));
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return - 0-no,1-yes
    */
   public Integer getUpdatingTaskStatusUpdatesResourceStatus ()
   {
      return ((Integer)get (UPDATE));
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag - int, 0-no,1-yes
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus (int flag)
   {
      put (UPDATE, flag);
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag - int, 0-no,1-yes
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus (Integer flag)
   {
      put (UPDATE, flag);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return - int, 0-no, 1-yes
    */
   public int getSplitInProgressTasksValue ()
   {
      return (getIntValue (SPLIT));
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return - int, 0-no, 1-yes
    */
   public Integer getSplitInProgressTasks ()
   {
      return ((Integer)get (SPLIT));
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag - int, 0-no, 1-yes
    */
   public void setSplitInProgressTasks (int flag)
   {
      put (SPLIT,flag);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag - int, 0-no, 1-yes
    */
   public void setSplitInProgressTasks (Integer flag)
   {
      put (SPLIT,flag);
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
   public static final int RECORD_NUMBER = 11;
}
