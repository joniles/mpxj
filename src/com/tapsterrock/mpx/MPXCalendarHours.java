/*
 * file:       MPXCalendarHours.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       28/11/2003
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
 * working hours in a calendar.
 */
public final class MPXCalendarHours extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @param parentCalendar parent calendar to which this record belongs.
    * @throws MPXException Thrown on parse errors
    */
   MPXCalendarHours (MPXFile file, MPXCalendar parentCalendar)
      throws MPXException
   {
      this (file, parentCalendar, Record.EMPTY_RECORD);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param parentCalendar parent calendar to which this record belongs.
    * @param record record containing the data for  this object.
    * @throws MPXException Thrown on parse errors
    */
   MPXCalendarHours (MPXFile file, MPXCalendar parentCalendar, Record record)
      throws MPXException
   {
      super(file, 0);

      m_parentCalendar = parentCalendar;

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
   public Integer getDay ()
   {
      return (m_day);
   }

   /**
    * Set day (1=Sunday 7=Saturday)
    *
    * @param d day number
    */
   void setDay (int d)
   {
      setDay (new Integer (d));
   }

   /**
    * Set day (1=Sunday 7=Saturday)
    *
    * @param d day number
    */
   private void setDay (Integer d)
   {
      m_day = d;
   }

   /**
    * Get FromTime1
    *
    * @return Time
    */
   public Date getFromTime1()
   {
      return (m_fromTime1);
   }

   /**
    * Sets from time 1
    *
    * @param from Time
    */
   public void setFromTime1(Date from)
   {
      m_fromTime1 = toTime(from);
   }

   /**
    * Get ToTime1
    *
    * @return Time
    */
   public Date getToTime1()
   {
      return (m_toTime1);
   }

   /**
    * Sets to time 1
    *
    * @param to Time
    */
   public void setToTime1 (Date to)
   {
      m_toTime1 = toTime(to);
   }

   /**
    * Get FromTime2
    *
    * @return Time
    */
   public Date getFromTime2 ()
   {
      return (m_fromTime2);
   }

   /**
    * Sets from time 2
    *
    * @param from Time
    */
   public void setFromTime2 (Date from)
   {
      m_fromTime2 = toTime(from);
   }

   /**
    * Get ToTime2
    *
    * @return Time
    */
   public Date getToTime2 ()
   {
      return (m_toTime2);
   }

   /**
    * Sets to time 2
    *
    * @param to Time
    */
   public void setToTime2 (Date to)
   {
      m_toTime2 = toTime(to);
   }

   /**
    * Get FromTime3
    *
    * @return Time
    */
   public Date getFromTime3 ()
   {      
      return (m_fromTime3);
   }

   /**
    * Sets from time 3
    *
    * @param from Time
    */
   public void setFromTime3 (Date from)
   {
      m_fromTime3 = toTime(from);
   }

   /**
    * Get ToTime3
    *
    * @return Time
    */
   public Date getToTime3 ()
   {
      return (m_toTime3);
   }

   /**
    * Sets to time 3
    *
    * @param to Time
    */
   public void setToTime3 (Date to)
   {
      m_toTime3 = toTime(to);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      int recordNumber;

      if (m_parentCalendar.isBaseCalendar() == true)
      {
         recordNumber = BASE_CALENDAR_HOURS_RECORD_NUMBER;
      }
      else
      {
         recordNumber = RESOURCE_CALENDAR_HOURS_RECORD_NUMBER;
      }

      StringBuffer buffer = new StringBuffer ();
      char delimiter = getParentFile().getDelimiter();

      buffer.append (recordNumber);
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_day));
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_fromTime1));
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_toTime1));
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_fromTime2));
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_toTime2));
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_fromTime3));
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_toTime3));      
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);
      
      return (buffer.toString());
   }

   /**
    * Reference to the parent calendar of this exception.
    */
   private MPXCalendar m_parentCalendar;

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

   private Integer m_day;
   private Date m_fromTime1;
   private Date m_toTime1;
   private Date m_fromTime2;
   private Date m_toTime2;
   private Date m_fromTime3;
   private Date m_toTime3;
   
   /**
    * Constant containing the record number associated with this record if
    * this instance represents base calendar hours.
    */
   static final int BASE_CALENDAR_HOURS_RECORD_NUMBER = 25;

   /**
    * Constant containing the record number associated with this record if
    * this instance represents resource calendar hours.
    */
   static final int RESOURCE_CALENDAR_HOURS_RECORD_NUMBER = 56;
}
