/*
 * file:       ResourceCalendarHours.java
 * author:     Scott Melville
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
 * This class represents working hours in a resource calendar.
 */
public class ResourceCalendarHours extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ResourceCalendarHours (MPXFile file)
      throws MPXException
   {
      this (file, Record.EMPTY_RECORD);
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

      setDayOfTheWeek(record.getInteger(0));
      setFromTime1(record.getTime(1));
      setToTime1(record.getTime(2));
      setFromTime2(record.getTime(3));
      setToTime2(record.getTime(4));
      setFromTime3(record.getTime(5));
      setToTime3(record.getTime(6));
   }

   /**
    * Get day of the week.
    * @return -int 1 - 7 , sunday - saturday.
    */
   public int getDayOfTheWeekValue()
   {
      return (getIntValue (DAY));
   }

   /**
    * Get day of the week.
    * @return -int 1 - 7 , sunday - saturday.
    */
   public Integer getDayOfTheWeek()
   {
      return ((Integer)get (DAY));
   }

   /**
    * Set day of the week.
    * @param val - 1 - 7 , sunday - saturday.
    */
   public void setDayOfTheWeek(int  val)
   {
      put (DAY,val);
   }

   /**
    * Set day of the week.
    * @param val - 1 - 7 , sunday - saturday.
    */
   public void setDayOfTheWeek (Integer  val)
   {
      put (DAY,val);
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
   public static final int SUNDAY = 1;
   /**
    * Constant for Monday
    */
   public static final int MONDAY = 2;
   /**
    * Constant for Tuesday
    */
   public static final int TUESDAY = 3;
   /**
    * Constant for Wednesday
    */
   public static final int WEDNESDAY = 4;
   /**
    * Constant for Thursday
    */
   public static final int THURSDAY = 5;
   /**
    * Constant for Friday
    */
   public static final int FRIDAY = 6;
   /**
    * Constant for Saturday
    */
   public static final int SATURDAY = 7;


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