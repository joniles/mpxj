/*
 * file:       ResourceCalendarException.java
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
 * This class represents resource calendar exception records from an MPX file.
 */
public class ResourceCalendarException extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ResourceCalendarException (MPXFile file)
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
    */
   ResourceCalendarException (MPXFile file, Record record)
      throws MPXException
   {
      super(file);

      setFromDate(record.getDate(0));
      setToDate(record.getDate(1));
      setWorking(record.getByte(2));
      setFromTime1(record.getTime(3));
      setToTime1(record.getTime(4));
      setFromTime2(record.getTime(5));
      setToTime2(record.getTime(6));
      setFromTime3(record.getTime(7));
      setToTime3(record.getTime(8));
   }

   /**
    * Retrieves whether this is a working or non working period.
    *
    * @return 0=non-working, 1=working, 2=default
    */
   public byte getWorking ()
   {
      return (getByteValue (WORKING));
   }


   /**
    * Sets whether this is a working or non working period.
    *
    * @param val 0=non-working, 1=working, 2=default
    */
   public void setWorking (Byte val)
   {
      put (WORKING, val);
   }

   /**
    * Returns the from date for the period
    *
    * @return - Date
    */
   public Date getFromDate ()
   {
      return ((Date)get(FROM_DATE));
   }

   /**
    * Sets from date.
    *
    * @param from - date
    */
   public void setFromDate (Date from)
   {
      put (FROM_DATE, from);
   }

   /**
    * Get to date
    *
    * @return - Date
    */
   public Date getToDate ()
   {
      return ((Date)get(TO_DATE));
   }

   /**
    * Sets To Date
    *
    * @param to - Date
    */
   public void setToDate (Date to)
   {
      put (TO_DATE, to);
   }

   /**
    * Get FromTime1
    *
    * @return - Time
    */
   public Date getFromTime1 ()
   {
      return ((Date)get(FROM_TIME_1));
   }

   /**
    * Sets from time 1
    *
    * @param from - Time
    */
   public void setFromTime1 (Date from)
   {
      putTime (FROM_TIME_1, from);
   }

   /**
    * Get ToTime1
    *
    * @return - Time
    */
   public Date getToTime1 ()
   {
      return ((Date)get(TO_TIME_1));
   }

   /**
    * Sets to time 1
    *
    * @param to - Time
    */
   public void setToTime1 (Date to)
   {
      putTime (TO_TIME_1, to);
   }

   /**
    * Get FromTime2
    *
    * @return - Time
    */
   public Date getFromTime2 ()
   {
      return ((Date)get(FROM_TIME_2));
   }

   /**
    * Sets from time 2
    *
    * @param from - Time
    */
   public void setFromTime2 (Date from)
   {
      putTime (FROM_TIME_2, from);
   }

   /**
    * Get ToTime2
    *
    * @return - Time
    */
   public Date getToTime2 ()
   {
      return ((Date)get(TO_TIME_2));
   }

   /**
    * Sets to time 2
    *
    * @param to - Time
    */
   public void setToTime2 (Date to)
   {
      putTime (TO_TIME_2, to);
   }

   /**
    * Get ToTime3
    *
    * @return - Time
    */
   public Date getFromTime3 ()
   {
      return ((Date)get(FROM_TIME_3));
   }

   /**
    * Sets from time 3
    *
    * @param from - Time
    */
   public void setFromTime3 (Date from)
   {
      putTime (FROM_TIME_3, from);
   }

   /**
    * Get ToTime3
    *
    * @return - Time
    */
   public Date getToTime3 ()
   {
      return ((Date)get(TO_TIME_3));
   }

   /**
    * Sets to time 3
    *
    * @param to - Time
    */
   public void setToTime3 (Date to)
   {
      putTime (TO_TIME_3, to);
   }


   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (toString (RECORD_NUMBER));
   }

   /**
    * Constant identifier for the FromDate field
    */
   private static final Integer FROM_DATE = new Integer(0);

   /**
    * Constant identifier for the ToDate field
    */
   private static final Integer TO_DATE = new Integer(1);

   /**
    * Constant identifier for the Working field
    */
   private static final Integer WORKING = new Integer(2);

   /**
    * Constant identifier for the FromTime 1 field
    */
   private static final Integer FROM_TIME_1 = new Integer(3);

   /**
    * Constant identifier for the To Time 1 field
    */
   private static final Integer TO_TIME_1 = new Integer(4);

   /**
    * Constant identifier for the From Time 2 field
    */
   private static final Integer FROM_TIME_2 = new Integer(5);

   /**
    * Constant identifier for the To Time 2 field
    */
   private static final Integer TO_TIME_2 = new Integer(6);

   /**
    * Constant identifier for the From Time 3 field
    */
   private static final Integer FROM_TIME_3 = new Integer(7);

   /**
    * Constant identifier for the To Time 3 field
    */
   private static final Integer TO_TIME_3 = new Integer(8);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 57;
}
