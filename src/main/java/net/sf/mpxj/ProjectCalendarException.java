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

package net.sf.mpxj;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class represents instances of Calendar Exception records from
 * an MPX file. It is used to define exceptions to the working days described
 * in both base and resource calendars.
 */
public final class ProjectCalendarException extends ProjectCalendarDateRanges implements Comparable<ProjectCalendarException>
{
   /**
    * Package private constructor.
    *
    * @param fromDate exception start date
    * @param toDate exception end date
    */
   ProjectCalendarException(Date fromDate, Date toDate)
   {
      m_fromDate = DateHelper.getDayStartDate(fromDate);
      m_toDate = DateHelper.getDayEndDate(toDate);
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
   public Date getFromDate()
   {
      return (m_fromDate);
   }

   /**
    * Get to date.
    *
    * @return Date
    */
   public Date getToDate()
   {
      return (m_toDate);
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
    * Set the recurrence data associated with this exception.
    * Set this to null if this is a default single day exception.
    *
    * @param recurring recurrence data
    */
   public void setRecurring(RecurringData recurring)
   {
      m_recurring = recurring;
   }

   /**
    * Gets working status.
    *
    * @return boolean value
    */
   public boolean getWorking()
   {
      return (getRangeCount() != 0);
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

      if (m_recurring == null)
      {
         result.add(this);
      }
      else
      {
         // TODO: fold into statement above once we have updated the test data
         if (m_recurring.getRecurrenceType() == RecurrenceType.DAILY && NumberHelper.getInt(m_recurring.getFrequency()) == 1)
         {
            result.add(this);
         }
         else
         {
            for (Date date : m_recurring.getDates())
            {
               Date startDate = DateHelper.getDayStartDate(date);
               Date endDate = DateHelper.getDayEndDate(date);
               ProjectCalendarException newException = new ProjectCalendarException(startDate, endDate);
               int rangeCount = getRangeCount();
               for (int rangeIndex = 0; rangeIndex < rangeCount; rangeIndex++)
               {
                  newException.addRange(getRange(rangeIndex));
               }
               result.add(newException);
            }
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
   public boolean contains(Date date)
   {
      boolean result = false;

      if (date != null)
      {
         result = (DateHelper.compare(getFromDate(), getToDate(), date) == 0);
      }

      return (result);
   }

   @Override public int compareTo(ProjectCalendarException o)
   {
      long fromTime1 = m_fromDate.getTime();
      long fromTime2 = o.m_fromDate.getTime();
      return (Long.compare(fromTime1, fromTime2));
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
      sb.append(" fromDate=").append(m_fromDate);
      sb.append(" toDate=").append(m_toDate);

      if (m_recurring != null)
      {
         sb.append(" recurring=").append(m_recurring);
      }

      for (DateRange range : this)
      {
         sb.append(range.toString());
      }

      sb.append("]");
      return (sb.toString());
   }

   private final Date m_fromDate;
   private final Date m_toDate;
   private String m_name;
   private RecurringData m_recurring;
}
