/*
 * file:       ProjectCalendarException.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2003
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

import net.sf.mpxj.utility.DateUtility;

/**
 * This class represents instances of Calendar Exception records from
 * an MPX file. It is used to define exceptions to the working days described
 * in both base and resource calendars.
 */
public final class ProjectCalendarException extends ProjectCalendarDateRanges implements Comparable<ProjectCalendarException>
{
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
    * Sets from date.
    *
    * @param from date
    */
   public void setFromDate(Date from)
   {
      m_fromDate = DateUtility.getDayStartDate(from);
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
    * Sets To Date.
    *
    * @param to Date
    */
   public void setToDate(Date to)
   {
      m_toDate = DateUtility.getDayEndDate(to);
   }

   /**
    * Gets working status.
    *
    * @return boolean value
    */
   public boolean getWorking()
   {
      return (m_working);
   }

   /**
    * Sets working status of this exception.
    *
    * @param flag Boolean flag
    */
   public void setWorking(boolean flag)
   {
      m_working = flag;
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
         result = (DateUtility.compare(getFromDate(), getToDate(), date) == 0);
      }

      return (result);
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(ProjectCalendarException o)
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
      StringBuffer sb = new StringBuffer();
      sb.append("[ProjectCalendarException");
      sb.append(" working=" + m_working);
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
   private boolean m_working;
}
