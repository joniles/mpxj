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
      super(file);

      setDefaultDurationUnits((byte)2);
      setDefaultDurationType((byte)0);
      setDefaultWorkUnits((byte)1);
      setDefaultHoursInDay(new Float(8));
      setDefaultHoursInWeek(new Float (40));
      setDefaultStandardRate(new MPXRate(10, TimeUnit.HOURS));
      setDefaultOvertimeRate(new MPXRate(15, TimeUnit.HOURS));
      setUpdatingTaskStatusUpdatesResourceStatus(new Byte((byte)1));
      setSplitInProgressTasks(new Byte((byte)0));
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
      setDefaultDurationUnits(record.getByteValue(0));
      setDefaultDurationType(record.getByteValue(1));
      setDefaultWorkUnits(record.getByteValue(2));
      setDefaultHoursInDay(record.getFloat(3));
      setDefaultHoursInWeek(record.getFloat(4));
      setDefaultStandardRate(record.getRate(5));
      setDefaultOvertimeRate(record.getRate(6));
      setUpdatingTaskStatusUpdatesResourceStatus(record.getByte(7));
      setSplitInProgressTasks(record.getByte(8));
   }

   /**
    * Gets Default Duration units
    *
    * @return byte constant
    */
   public byte getDefaultDurationUnits ()
   {
      return (((Byte)get(DEFAULT_DURATION_UNITS)).byteValue()); /** @todo potential NPE */
   }

   /**
    * Default duration units
    *
    * @param units default time units
    */
   public void setDefaultDurationUnits (byte units)
   {
      put (DEFAULT_DURATION_UNITS, new Byte(units));
   }

   /**
    * Gets whether the Default Type is fixed or not
    *
    * @return  0-not fixed,1-fixed
    */
   public byte getDefaultDurationType ()
   {
      return (((Byte)get(DEFAULT_DURATION_TYPE)).byteValue());
   }

   /**
    * Sets whether the Default Type is fixed or not
    *
    * @param type  0-not fixed,1-fixed
    */
   public void setDefaultDurationType (byte type)
   {
      put (DEFAULT_DURATION_TYPE, new Byte(type));
   }

   /**
    * Default work units. Values, 0-minutes,1-hours,2-days,3-weeks
    *
    * @return  byte representing default
    */
   public byte getDefaultWorkUnits ()
   {
      return (((Byte)get(DEFAULT_WORK_UNITS)).byteValue());
   }

   /**
    * Default work units. Values, 0-minutes,1-hours,2-days,3-weeks
    *
    * @param units  byte representing default
    */
   public void setDefaultWorkUnits (byte units)
   {
      put (DEFAULT_WORK_UNITS, new Byte(units));
   }

   /**
    * Gets the default number of hours in a day
    *
    * @return number of hours
    */
   public float getDefaultHoursInDay ()
   {
      return (((Float)get(DEFAULT_HOURS_IN_DAY)).floatValue());
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
   public float getDefaultHoursInWeek ()
   {
      return (((Float)get(DEFAULT_HOURS_IN_WEEK)).floatValue());
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
   public byte getUpdatingTaskStatusUpdatesResourceStatus ()
   {
      return (((Byte)get(UPDATE)).byteValue());
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag - byte, 0-no,1-yes
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus (Byte flag)
   {
      put (UPDATE, flag);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return - byte, 0-no, 1-yes
    */
   public byte getSplitInProgressTasks ()
   {
      return (((Byte)get(SPLIT)).byteValue());
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag - byte, 0-no, 1-yes
    */
   public void setSplitInProgressTasks (Byte flag)
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
   public static final Integer DEFAULT_DURATION_UNITS = new Integer(0);

   /**
    * Constant value representing Default Duration Type. eg 'w','m','d','h'
    */
   public static final Integer DEFAULT_DURATION_TYPE = new Integer(1);

   /**
    * Constant value representing Default Work Units. eg 'w','m','d','h'
    */
   public static final Integer DEFAULT_WORK_UNITS = new Integer(2);

   /**
    * Constant value representing Hours in Day field.
    */
   public static final Integer DEFAULT_HOURS_IN_DAY = new Integer(3);

   /**
    * Constant value representing Default hours In Week field.
    */
   public static final Integer DEFAULT_HOURS_IN_WEEK = new Integer(4);

   /**
    * Constant value representing Default Standard Rate  field.
    */
   public static final Integer DEFAULT_STANDARD_RATE = new Integer(5);

   /**
    * Constant value representing Default Overtine Rate field.
    */
   public static final Integer DEFAULT_OVERTIME_RATE = new Integer(6);

   /**
    * Constant value representing Updating Task Status Updates Resource Status field.
    */
   public static final Integer UPDATE = new Integer(7);

   /**
    * Constant value representing Split Tasks field.
    */
   public static final Integer SPLIT = new Integer(8);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 11;
}
