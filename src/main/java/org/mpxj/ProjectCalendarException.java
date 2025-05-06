/*
 * file:       ProjectCalendarException.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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

package org.mpxj;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.NumberHelper;

/**
 * This class represents instances of Calendar Exception records from
 * an MPX file. It is used to define exceptions to the working days described
 * in both base and resource calendars.
 */
public final class ProjectCalendarException extends ProjectCalendarHours implements Comparable<ProjectCalendarException>
{
   /**
    * Package private constructor.
    *
    * @param fromDate exception start date
    * @param toDate exception end date
    */
   ProjectCalendarException(LocalDate fromDate, LocalDate toDate)
   {
      this(fromDate, toDate, null);
   }

   ProjectCalendarException(RecurringData recurringData)
   {
      this(null, null, recurringData);
   }

   ProjectCalendarException(LocalDate fromDate, LocalDate toDate, RecurringData recurringData)
   {
      m_fromDate = fromDate;
      m_toDate = toDate;
      m_recurring = recurringData;
   }

   /**
    * Retrieve the name of this exception.
    *
    * @return exception name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Set the name of this exception.
    *
    * @param name exception name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Returns the from date.
    *
    * @return Date
    */
   public LocalDate getFromDate()
   {
      return m_recurring == null ? m_fromDate : m_recurring.getCalculatedFirstDate();
   }

   /**
    * Get to date.
    *
    * @return Date
    */
   public LocalDate getToDate()
   {
      return m_recurring == null ? m_toDate : m_recurring.getCalculatedLastDate();
   }

   /**
    * Retrieve any recurrence data associated with this exception.
    * This will return null if this is a default single day exception.
    *
    * @return recurrence data
    */
   public RecurringData getRecurring()
   {
      return m_recurring;
   }

   /**
    * Gets working status.
    *
    * @return boolean value
    */
   public boolean getWorking()
   {
      return size() != 0;
   }

   /**
    * Expand the current exception into a list of exception.
    * If the current exception is not recurring, or it is recurring and
    * the exceptions form a contiguous range of days, then this list will
    * only contain a single entry.
    *
    * If this is a recurring exception which covers multiple non-contiguous days
    * the returned list will include exceptions for all exception dates.
    *
    * @return list of exceptions derived from the current exception
    */
   public List<ProjectCalendarException> getExpandedExceptions()
   {
      List<ProjectCalendarException> result = new ArrayList<>();

      if (m_recurring == null || m_recurring.getRecurrenceType() == RecurrenceType.DAILY && NumberHelper.getInt(m_recurring.getFrequency()) == 1)
      {
         result.add(this);
      }
      else
      {
         for (LocalDate date : m_recurring.getDates())
         {
            ProjectCalendarException newException = new ProjectCalendarException(date, date);
            int rangeCount = size();
            for (int rangeIndex = 0; rangeIndex < rangeCount; rangeIndex++)
            {
               newException.add(get(rangeIndex));
            }
            result.add(newException);
         }
      }

      return result;
   }

   /**
    * This method determines whether the given date falls in the range of
    * dates covered by this exception. Note that this method assumes that both
    * the start and end date of this exception have been set.
    *
    * @param date Date to be tested
    * @return Boolean value
    */
   public boolean contains(LocalDateTime date)
   {
      boolean result = false;

      if (date != null)
      {
         result = (LocalDateHelper.compare(getFromDate(), getToDate(), LocalDateHelper.getLocalDate(date)) == 0);
      }

      return result;
   }

   /**
    * This method determines whether the given date falls in the range of
    * dates covered by this exception. Note that this method assumes that both
    * the start and end date of this exception have been set.
    *
    * @param date Date to be tested
    * @return Boolean value
    */
   public boolean contains(LocalDate date)
   {
      boolean result = false;

      if (date != null)
      {
         result = (LocalDateHelper.compare(getFromDate(), getToDate(), date) == 0);
      }

      return result;
   }

   /**
    * Returns true if any part of the supplied calendar exception overlaps this one.
    *
    * @param exception calendar exception to test
    * @return true if there is any overlap
    */
   public boolean contains(ProjectCalendarException exception)
   {
      return !(LocalDateHelper.compare(getToDate(), exception.getFromDate()) < 0 || LocalDateHelper.compare(exception.getToDate(), getFromDate()) < 0);
   }

   @Override public int compareTo(ProjectCalendarException o)
   {
      return getFromDate().compareTo(o.getFromDate());
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[ProjectCalendarException");
      if (m_name != null && !m_name.isEmpty())
      {
         sb.append(" name=").append(m_name);
      }
      sb.append(" working=").append(getWorking());

      if (m_recurring == null)
      {
         sb.append(" fromDate=").append(m_fromDate);
         sb.append(" toDate=").append(m_toDate);
      }
      else
      {
         sb.append(" recurring=").append(m_recurring);
      }

      for (LocalTimeRange range : this)
      {
         sb.append(range.toString());
      }

      sb.append("]");
      return (sb.toString());
   }

   private final LocalDate m_fromDate;
   private final LocalDate m_toDate;
   private final RecurringData m_recurring;
   private String m_name;
}
