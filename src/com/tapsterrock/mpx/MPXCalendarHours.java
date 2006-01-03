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

import java.util.Iterator;
import java.util.LinkedList;

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
   MPXCalendarHours (ProjectFile file, MPXCalendar parentCalendar)
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
   MPXCalendarHours (ProjectFile file, MPXCalendar parentCalendar, Record record)
      throws MPXException
   {
      super(file, 0);

      m_parentCalendar = parentCalendar;
    
      if (record != Record.EMPTY_RECORD)
      {
         setDay(Day.getInstance(NumberUtility.getInt(record.getInteger(0))));
         addDateRange(new DateRange(record.getTime(1), record.getTime(2)));
         addDateRange(new DateRange(record.getTime(3), record.getTime(4)));
         addDateRange(new DateRange(record.getTime(5), record.getTime(6)));      
      }
   }

   /**
    * Get day.
    *
    * @return day instance
    */
   public Day getDay ()
   {
      return (m_day);
   }

   /**
    * Set day.
    *
    * @param d day instance
    */
   void setDay (Day d)
   {
      m_day = d;
   }

   /**
    * Add a date range to the list of date ranges.
    * 
    * @param range date range
    */
   public void addDateRange (DateRange range)
   {
      m_dateRanges.add(range);
   }
   
   /**
    * Retrieve the date range at the specified index.
    * The index is zero based, and this method will return
    * null if the requested date range does not exist.
    * 
    * @param index range index
    * @return date range instance
    */
   public DateRange getDateRange (int index)
   {
      DateRange result = null;
      
      if (index >= 0 && index < m_dateRanges.size())
      {
         result = (DateRange)m_dateRanges.get(index);
      }
      
      return (result);
   }
   
   /**
    * Retrieve an iterator to allow the list of date ranges to be traversed.
    * 
    * @return iterator.
    */
   public Iterator iterator ()
   {
      return (m_dateRanges.iterator());
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
      
      DateRange range1 = getDateRange(0);
      if (range1 == null)
      {
         range1 = DateRange.EMPTY_RANGE;
      }

      DateRange range2 = getDateRange(1);
      if (range2 == null)
      {
         range2 = DateRange.EMPTY_RANGE;
      }
      
      DateRange range3 = getDateRange(2);
      if (range3 == null)
      {
         range3 = DateRange.EMPTY_RANGE;
      }
      
      buffer.append (recordNumber);
      buffer.append (delimiter);
      buffer.append(format(delimiter, m_day));
      buffer.append (delimiter);
      buffer.append(format(delimiter, toTime(range1.getStartDate())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, toTime(range1.getEndDate())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, toTime(range2.getStartDate())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, toTime(range2.getEndDate())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, toTime(range3.getStartDate())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, toTime(range3.getEndDate())));
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (ProjectFile.EOL);
      
      return (buffer.toString());
   }

   /**
    * Reference to the parent calendar of this exception.
    */
   private MPXCalendar m_parentCalendar;
   private Day m_day;
   private LinkedList m_dateRanges = new LinkedList ();
   
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
