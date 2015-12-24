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

import java.util.Date;

import net.sf.mpxj.common.DateHelper;

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
    * Gets working status.
    *
    * @return boolean value
    */
   public boolean getWorking()
   {
      return (getRangeCount() != 0);
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

   /**
    * {@inheritDoc}
    */
   @Override public int compareTo(ProjectCalendarException o)
   {
      long fromTime1 = m_fromDate.getTime();
      long fromTime2 = o.m_fromDate.getTime();
      return ((fromTime1 < fromTime2) ? (-1) : ((fromTime1 == fromTime2) ? 0 : 1));
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[ProjectCalendarException");
      sb.append(" working=" + getWorking());
      sb.append(" fromDate=" + m_fromDate);
      sb.append(" toDate=" + m_toDate);

      for (DateRange range : this)
      {
         sb.append(range.toString());
      }

      sb.append("]");
      return (sb.toString());
   }

   private Date m_fromDate;
   private Date m_toDate;
}
