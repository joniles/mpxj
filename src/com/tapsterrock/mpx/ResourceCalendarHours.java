/*
 * file:  ResourceCalendarHours.java
 * author:  Scott Melville
 * copyright:  Tapster Rock Limited
 * date:  15/8/2002
 */

/*
 * CHANGELOG
 * $Log$
 * Revision 1.1.1.1  2003/01/07 21:53:53  joniles
 * Initial revision.
 *
 *
 * $Nokeywords: $
 *
 */

package com.tapsterrock.mpx;

import java.util.Date;

/**Resource Calendar Hours - 56  These records define the working hours for the resource that
 * differ from the base calendar used by the resource. These records apply to the Resource Calendar
 * Definition record immediately preceding this record. Up to seven of these records can follow each
 * Resource Calendar Definition record.
 *
 * The fields included in this record are:
 * - Day of the Week (1-7, where 1 = Sunday and 7 = Saturday)
 * - From Time 1
 * - To Time 1
 * - From Time 2
 * - To Time 2
 * - From Time 3
 * - To Time 3
 *
 * Example: 56,3,7:00,11:00,12:00,4:00
 *
 * This example specifies that on Tuesdays, the immediately preceding resource
 * (in this case, carpenter) works from 7:00 A.M. to 4:00 P.M. with an hour off
 * from 11:00 A.M. to 12:00 P.M.
 */

public class ResourceCalendarHours extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ResourceCalendarHours (MPXFile file)
   {
      super(file);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record record containing the data for  this object.
    * @throws MPXException normallyt hrown when parsing fails
    */
   public ResourceCalendarHours (MPXFile file, Record record)
     throws MPXException
   {
      super(file);

      setDayOfTheWeek(record.getByte(0));
      setFromTime1(record.getTime(1));
      setToTime1(record.getTime(2));
      setFromTime2(record.getTime(3));
      setToTime2(record.getTime(4));
      setFromTime3(record.getTime(5));
      setToTime3(record.getTime(6));
   }

   /**
    * Get day of the week.
    * @return -byte 1 - 7 , sunday - saturday.
    */
   public byte getDayOfTheWeek()
   {
      return ((Byte)get(DAY)).byteValue();
   }
   /**
    * Set day of the week.
    * @param val - 1 - 7 , sunday - saturday.
    */
   public void setDayOfTheWeek(Byte val)
   {
      put(DAY,val);
   }

   /**
    * Returns first from time period.
    * @return - start of first period.
    */
   public Date getFromTime1()
   {
      return ((Date)get(FROM_TIME_1));
   }

   /**
    * Sets start of first time period.
    * @param val - time string, parsed by time formatting object as setup in DateTimeSettings
    */
   public void setFromTime1 (Date val)
   {
      putTime (FROM_TIME_1, val);
   }

   /**
    * Returns first from time period.
    * @return - start of first period.
    */
   public Date getToTime1()
   {
      return ((Date)get(TO_TIME_1));
   }

   /**
    * Sets start of first time period.
    * @param val - time string, parsed by time formatting object as setup in DateTimeSettings
    */
   public void setToTime1 (Date val)
   {
      putTime (TO_TIME_1, val);
   }

   /**
    * Returns first from time period.
    * @return - start of first period.
    */
   public Date getFromTime2()
   {
      return ((Date)get(FROM_TIME_2));
   }

   /**
    * Sets start of first time period.
    * @param val - time string, parsed by time formatting object as setup in DateTimeSettings
    */
   public void setFromTime2 (Date val)
   {
      putTime (FROM_TIME_2,val);
   }

   /**
    * Returns first from time period.
    * @return - start of first period.
    */
   public Date getToTime2()
   {
      return ((Date)get(TO_TIME_2));
   }
   /**
    * Sets start of first time period.
    * @param val - time string, parsed by time formatting object as setup in DateTimeSettings
    */
   public void setToTime2 (Date val)
   {
      putTime (TO_TIME_2, val);
   }

   /**
    * Returns first from time period.
    * @return - start of first period.
    */
   public Date getFromTime3()
   {
      return ((Date)get(FROM_TIME_3));
   }

   /**
    * Sets start of first time period.
    * @param val - time string, parsed by time formatting object as setup in DateTimeSettings
    */
   public void setFromTime3 (Date val)
   {
      putTime (FROM_TIME_3, val);
   }


   /**
    * Returns first from time period.
    * @return - start of first period.
    */
   public Date getToTime3()
   {
      return ((Date)get(TO_TIME_3));
   }

   /**
    * Sets start of first time period.
    * @param val - time string, parsed by time formatting object as setup in DateTimeSettings
    */
   public void setToTime3 (Date val)
   {
      putTime (TO_TIME_3, val);
   }


   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      return (toString(RECORD_NUMBER));
   }

   /**
    * Constant for Sunday
    */
   public static final byte SUNDAY = 1;
   /**
    * Constant for Monday
    */
   public static final byte MONDAY = 2;
   /**
    * Constant for Tuesday
    */
   public static final byte TUESDAY = 3;
   /**
    * Constant for Wednesday
    */
   public static final byte WEDNESDAY = 4;
   /**
    * Constant for Thursday
    */
   public static final byte THURSDAY = 5;
   /**
    * Constant for Friday
    */
   public static final byte FRIDAY = 6;
   /**
    * Constant for Saturday
    */
   public static final byte SATURDAY = 7;


   /**
    * Constant representing Day field.
    */
   private static final Integer DAY = new Integer(0);
   /**
    * Constant representing From Time 1 field.
    */
   private static final Integer FROM_TIME_1 = new Integer(1);
   /**
    * Constant representing To Time 1 field.
    */
   private static final Integer TO_TIME_1 = new Integer(2);
   /**
    * Constant representing From Time 2 field.
    */
   private static final Integer FROM_TIME_2 = new Integer(3);
   /**
    * Constant representing To Time 2 field.
    */
   private static final Integer TO_TIME_2 = new Integer(4);
   /**
    * Constant representing From Time 3 field.
    */
   private static final Integer FROM_TIME_3 = new Integer(5);
   /**
    * Constant representing To Time 3 field.
    */
   private static final Integer TO_TIME_3 = new Integer(6);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 56;
}