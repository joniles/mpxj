/*
 * file:       ResourceCalendar.java
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

import java.util.LinkedList;
import java.util.Iterator;

/**
 * This class represents the Resource Calendar Definition record. It is used to
 * define the working and non-working days of the week for a resource.
 * The default calendar defines Monday to Friday as working days.
 */
public final class ResourceCalendar extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ResourceCalendar (MPXFile file)
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
   ResourceCalendar (MPXFile file, Record record)
   {
      super(file, MAX_FIELDS);

      setName(record.getString(0));
      setWorkingDay(1, record.getInteger(1));
      setWorkingDay(2, record.getInteger(2));
      setWorkingDay(3, record.getInteger(3));
      setWorkingDay(4, record.getInteger(4));
      setWorkingDay(5, record.getInteger(5));
      setWorkingDay(6, record.getInteger(6));
      setWorkingDay(7, record.getInteger(7));
   }

   /**
    * This method is used to add an exception to a resource calendar.
    *
    * @return ResourceCalendarException object
    * @throws MPXException thrown if the maximum number of exceptions has been added
    */
   public ResourceCalendarException addResourceCalendarException()
      throws MPXException
   {
      if (m_exceptions.size() == MAX_EXCEPTIONS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      ResourceCalendarException rce = new ResourceCalendarException(getParentFile());

      m_exceptions.add (rce);

      return (rce);
   }

   /**
    * This method is used to add an exception to a resource calendar.
    * The data to populate the exception comes from an MPX file.
    *
    * @param record record data read from an MPX file
    * @return ResourceCalendarException object
    * @throws MPXException thrown if the maximum number of exceptions has been added
    */
   ResourceCalendarException addResourceCalendarException (Record record)
      throws MPXException
   {
      if (m_exceptions.size() == MAX_EXCEPTIONS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      ResourceCalendarException rce = new ResourceCalendarException (getParentFile(), record);

      m_exceptions.add (rce);

      return (rce);
   }

   /**
    * This method is used to add details of working hours to a resource calendar.
    *
    * @param day Day number
    * @return ResourceCalendarException object
    * @throws MPXException thrown if the maximum number of hours has been added
    */
   public ResourceCalendarHours addResourceCalendarHours(int day)
      throws MPXException
   {
      ResourceCalendarHours rch = new ResourceCalendarHours (getParentFile(), Record.EMPTY_RECORD);

      rch.setDayOfTheWeek (day);
      --day;

      if (day < 0 || day > m_hours.length)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_hours[day] = rch;

      return (rch);
   }

   /**
    * This method is used to add details of working hours to a resource calendar.
    * The data to populate this record is read from an MPX file.
    *
    * @param record data read from an MPX file
    * @return ResourceCalendarException object
    * @throws MPXException thrown if the maximum number of hours has been added
    */
   public ResourceCalendarHours addResourceCalendarHours (Record record)
      throws MPXException
   {
      ResourceCalendarHours rch = new ResourceCalendarHours(getParentFile(), record);
      int day = rch.getDayOfTheWeekValue()-1;

      if (day < 0 || day > m_hours.length)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      m_hours[day] = rch;

      return (rch);
   }

   /**
    * This method retrieves the resource calendar hours for the specified day.
    *
    * @param day Day number
    * @return Resource calendar hours
    */
   public ResourceCalendarHours getResourceCalendarHours (int day)
   {
      return (m_hours[day-1]);
   }

   /**
    * Returns the name of the BaseCalendar used by the resource
    *
    * @return calendar name
    */
   public String getName ()
   {
      return ((String)get(NAME));
   }

   /**
    * Sets the name of the BaseCalendar used by the resource
    *
    * @param name calendar name
    */
   public void setName (String name)
   {
      put (NAME, name);
   }

   /**
    * Method indicating whether a day is a working or non-working day.
    *
    * @param day number of required day (1=Sunday, 7=Saturday)
    * @return Working day flag
    */
   public int isWorkingDay (int day)
   {
      return (getIntValue(day));
   }

   /**
    * This is a convenience method provided to allow a day to be set
    * as working or non-working, by using the day number to
    * identify the required day.
    *
    * @param day number of required day (1=Sunday, 7=Saturday)
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay (int day, int working)
   {
      put (day, new Integer (working));
   }

   /**
    * This is a convenience method provided to allow a day to be set
    * as working or non-working, by using the day number to
    * identify the required day.
    *
    * @param day number of required day (1=Sunday, 7=Saturday)
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay (int day, Integer working)
   {
      put (day, working);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer();

      buf.append(toString(RECORD_NUMBER));

      for (int loop=0; loop < m_hours.length; loop++)
      {
         if (m_hours[loop] != null)
         {
            buf.append (m_hours[loop].toString());
         }
      }

      Iterator iter = m_exceptions.iterator();
      while (iter.hasNext() == true)
      {
         buf.append ((iter.next()).toString());
      }

      return (buf.toString());
   }


   /**
    * List of working hours for the base calendar.
    */
   private ResourceCalendarHours[] m_hours = new ResourceCalendarHours[7];

   /**
    * List maintaining children of type <tt>ResourceCalendarException</tt>.
    */
   private LinkedList m_exceptions = new LinkedList();

   /**
    * Constant signifying a Working condition (of a day)
    */
   public static final int WORKING = 1;

   /**
    * Constant signifying a Non Working condition (of a day)
    */
   public static final int NON_WORKING = 0;

   /**
    * Constant signifying the default condition.
    * (As in the default calendar 'Standard')
    */
   public static final int DEFAULT = 2;

   /**
    * Name of the base calendar used by the related resource
    */
   private static final int NAME = 0;

   /**
    * Maximum number of fields in this record.
    */
   private static final int MAX_FIELDS = 8;

   /**
    * Constant representing maximum number of ResourceCalendarException
    * children per ResourceCalendar.
    */
   private static final int MAX_EXCEPTIONS = 250;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 55;
}
