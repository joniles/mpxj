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
public class ResourceCalendar extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ResourceCalendar (MPXFile file)
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
   ResourceCalendar (MPXFile file, Record record)
      throws MPXException
   {
      super(file);

      setBaseCalendarName(record.getString(0));
      setSunday(record.getInteger(1));
      setMonday(record.getInteger(2));
      setTuesday(record.getInteger(3));
      setWednesday(record.getInteger(4));
      setThursday(record.getInteger(5));
      setFriday(record.getInteger(6));
      setSaturday(record.getInteger(7));
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
    * @return ResourceCalendarException object
    * @throws MPXException thrown if the maximum number of hours has been added
    */
   public ResourceCalendarHours addResourceCalendarHours()
      throws MPXException
   {
      if (m_hours.size() == MAX_HOURS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      ResourceCalendarHours rch = new ResourceCalendarHours (getParentFile());

      m_hours.add (rch);

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
      if (m_hours.size() == MAX_HOURS)
      {
         throw new MPXException (MPXException.MAXIMUM_RECORDS);
      }

      ResourceCalendarHours rch = new ResourceCalendarHours (getParentFile(), record);

      m_hours.add (rch);

      return (rch);
   }


   /**
    * Returns the name of the BaseCalendar used by the resource
    *
    * @return calendar name
    */
   public String getBaseCalendarName ()
   {
      return ((String)get(BASE_CALENDAR_NAME));
   }

   /**
    * Sets the name of the BaseCalendar used by the resource
    *
    * @param baseCalName calendar name
    */
   public void setBaseCalendarName (String baseCalName)
   {
      put (BASE_CALENDAR_NAME,(baseCalName==null||baseCalName.equals("")?"Standard":baseCalName));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public int getMondayValue ()
   {
      return (getIntValue (MONDAY));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public Integer getMonday ()
   {
      return ((Integer)get (MONDAY));
   }

   /**
    * Sets working status.
    *
    * @param mon - int- 0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setMonday (int mon)
   {
      put (MONDAY, mon);
   }

   /**
    * Sets working status.
    *
    * @param mon - int- 0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setMonday (Integer mon)
   {
      put (MONDAY, mon);
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public int getTuesdayValue ()
   {
       return (getIntValue (TUESDAY));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public Integer getTuesday ()
   {
       return ((Integer)get (TUESDAY));
   }

   /**
    * Sets working status.
    *
    * @param mon - int- 0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setTuesday (int mon)
   {
      put (TUESDAY, mon);
   }

   /**
    * Sets working status.
    *
    * @param mon - int- 0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setTuesday (Integer mon)
   {
      put (TUESDAY, mon);
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public int getWednesdayValue ()
   {
      return (getIntValue (WEDNESDAY));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public Integer getWednesday ()
   {
      return ((Integer)get (WEDNESDAY));
   }

   /**
    * Sets working status.
    *
    * @param wed - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setWednesday (int wed)
   {
      put (WEDNESDAY, wed);
   }

   /**
    * Sets working status.
    *
    * @param wed - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setWednesday (Integer wed)
   {
      put (WEDNESDAY, wed);
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public int getThursdayValue ()
   {
       return (getIntValue (THURSDAY));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public Integer getThursday ()
   {
       return ((Integer)get (THURSDAY));
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setThursday (int mon)
   {
      put (THURSDAY, mon);
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setThursday (Integer mon)
   {
      put (THURSDAY, mon);
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public int getFridayValue ()
   {
      return (getIntValue(FRIDAY));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public Integer getFriday ()
   {
      return ((Integer)get (FRIDAY));
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setFriday (int mon)
   {
      put (FRIDAY, mon);
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setFriday (Integer mon)
   {
      put (FRIDAY, mon);
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public int getSaturdayValue ()
   {
      return (getIntValue (SATURDAY));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public Integer getSaturday ()
   {
      return ((Integer)get (SATURDAY));
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setSaturday (int mon)
   {
      put (SATURDAY, mon);
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setSaturday (Integer mon)
   {
      put (SATURDAY, mon);
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public int getSundayValue ()
   {
       return (getIntValue (SUNDAY));
   }

   /**
    * Gets working status.
    *
    * @return - 0 - non-working, 1 - Working, 2 - use dafault
    */
   public Integer getSunday ()
   {
       return ((Integer)get (SUNDAY));
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setSunday (int mon)
   {
      put (SUNDAY, mon);
   }

   /**
    * Sets working status.
    *
    * @param mon - int  0 - non-working, 1 - Working, 2 - use dafault
    */
   public void setSunday (Integer mon)
   {
      put (SUNDAY, mon);
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

      Iterator iter = m_hours.iterator();
      while (iter.hasNext() == true)
      {
         buf.append ((iter.next()).toString());
      }

      iter = m_exceptions.iterator();
      while (iter.hasNext() == true)
      {
         buf.append ((iter.next()).toString());
      }

      return (buf.toString());
   }


   /**
    * List maintaining children of type <tt>ResourceCalendarHours</tt>.
    */
   private LinkedList m_hours = new LinkedList();

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
   private static final Integer BASE_CALENDAR_NAME = new Integer(0);

   /**
    * Constant value representing day of week - Sunday
    */
   private static final Integer SUNDAY = new Integer(1);

   /**
    * Constant value representing day of week - Monday
    */
   private static final Integer MONDAY = new Integer(2);

   /**
    * Constant value representing day of week - Tuesday
    */
   private static final Integer TUESDAY = new Integer(3);

   /**
    * Constant value representing day of week - Wednesday
    */
   private static final Integer WEDNESDAY = new Integer(4);

   /**
    * Constant value representing day of week - Thursday
    */
   private static final Integer THURSDAY = new Integer(5);

   /**
    * Constant value representing day of week - Friday
    */
   private static final Integer FRIDAY = new Integer(6);

   /**
    * Constant value representing day of week - Saturday
    */
   private static final Integer SATURDAY = new Integer(7);

   /**
    * Constant representing maximum number of ResourceCalendarException
    * children per ResourceCalendar.
    */
   public static final int MAX_EXCEPTIONS = 250;

   /**
    * Constant representing maximum number of ResourceCalendarHours
    * children per ResourceCalendar.
    */
   public static final int MAX_HOURS = 7;

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 55;
}
