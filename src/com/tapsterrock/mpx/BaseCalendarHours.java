/*
 * file:       BaseCalendarHour.java
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
 * This class is used to represent the records in an MPX file that define
 * working hours.
 */
public final class BaseCalendarHours extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @throws MPXException Thrown on parse errors
    */
   BaseCalendarHours (MPXFile file)
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
    * @throws MPXException Thrown on parse errors
    */
   BaseCalendarHours (MPXFile file, Record record)
      throws MPXException
   {
      super(file, MAX_FIELDS);

      setDay(record.getInteger(0));
      setFromTime1(record.getTime(1));
      setToTime1(record.getTime(2));
      setFromTime2(record.getTime(3));
      setToTime2(record.getTime(4));
      setFromTime3(record.getTime(5));
      setToTime3(record.getTime(6));
   }


   /**
    * Get day (1=Sunday 7=Saturday)
    *
    * @return day number
    */
   public int getDayValue ()
   {
      return (getIntValue (DAY));
   }

   /**
    * Get day (1=Sunday 7=Saturday)
    *
    * @return day number
    */
   public Integer getDay ()
   {
      return ((Integer)get (DAY));
   }

   /**
    * Set day (1=Sunday 7=Saturday)
    *
    * @param d day number
    */
   void setDay (int d)
   {
      put (DAY,d);
   }

   /**
    * Set day (1=Sunday 7=Saturday)
    *
    * @param d day number
    */
   private void setDay (Integer d)
   {
      put (DAY, d);
   }

   /**
    * Get FromTime1
    *
    * @return Time
    */
   public Date getFromTime1()
   {
      return ((Date)get(FROM_TIME_1));
   }

   /**
    * Sets from time 1
    *
    * @param from Time
    */
   public void setFromTime1(Date from)
   {
      putTime (FROM_TIME_1,from);
   }

   /**
    * Get ToTime1
    *
    * @return Time
    */
   public Date getToTime1()
   {
      return ((Date)get(TO_TIME_1));
   }

   /**
    * Sets to time 1
    *
    * @param to Time
    */
   public void setToTime1 (Date to)
   {
      putTime (TO_TIME_1,to);
   }

   /**
    * Get FromTime2
    *
    * @return Time
    */
   public Date getFromTime2 ()
   {
      return ((Date)get(FROM_TIME_2));
   }

   /**
    * Sets from time 2
    *
    * @param from Time
    */
   public void setFromTime2 (Date from)
   {
      putTime (FROM_TIME_2,from);
   }

   /**
    * Get ToTime2
    *
    * @return Time
    */
   public Date getToTime2 ()
   {
      return ((Date)get(TO_TIME_2));
   }

   /**
    * Sets to time 2
    *
    * @param to Time
    */
   public void setToTime2 (Date to)
   {
      putTime (TO_TIME_2,to);
   }

   /**
    * Get FromTime3
    *
    * @return Time
    */
   public Date getFromTime3 ()
   {
      return ((Date)get(FROM_TIME_3));
   }

   /**
    * Sets from time 3
    *
    * @param from Time
    */
   public void setFromTime3 (Date from)
   {
      putTime (FROM_TIME_3,from);
   }

   /**
    * Get ToTime3
    *
    * @return Time
    */
   public Date getToTime3 ()
   {
      return ((Date)get(TO_TIME_3));
   }

   /**
    * Sets to time 3
    *
    * @param to Time
    */
   public void setToTime3 (Date to)
   {
      putTime (TO_TIME_3,to);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
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
   private static final int DAY = 0;

   /**
    * Constant representing From Time 1 field.
    */
   private static final int FROM_TIME_1 = 1;

   /**
    * Constant representing To Time 1 field.
    */
   private static final int TO_TIME_1 = 2;

   /**
    * Constant representing From Time 2 field.
    */
   private static final int FROM_TIME_2 = 3;

   /**
    * Constant representing To Time 2 field.
    */
   private static final int TO_TIME_2 = 4;

   /**
    * Constant representing From Time 3 field.
    */
   private static final int FROM_TIME_3 = 5;

   /**
    * Constant representing To Time 3 field.
    */
   private static final int TO_TIME_3 = 6;

   /**
    * Maximum number of fields in this record.
    */
   private static final int MAX_FIELDS = 7;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 25;
}