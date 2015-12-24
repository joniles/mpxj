/*
 * file:       DateRange.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       25/03/2005
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
 * This class represents a period of time.
 */
public final class DateRange implements Comparable<DateRange>
{
   /**
    * Constructor.
    *
    * @param startDate start date
    * @param endDate end date
    */
   public DateRange(Date startDate, Date endDate)
   {
      m_start = startDate;
      m_end = endDate;
   }

   /**
    * Retrieve the date at the start of the range.
    *
    * @return start date
    */
   public Date getStart()
   {
      return (m_start);
   }

   /**
    * Retrieve the date at the end of the range.
    *
    * @return end date
    */
   public Date getEnd()
   {
      return (m_end);
   }

   /**
    * This method compares a target date with a date range. The method will
    * return 0 if the date is within the range, less than zero if the date
    * is before the range starts, and greater than zero if the date is after
    * the range ends.
    *
    * @param date target date
    * @return comparison result
    */
   public int compareTo(Date date)
   {
      return DateHelper.compare(m_start, m_end, date);
   }

   /**
    * {@inheritDoc}
    */
   @Override public int compareTo(DateRange o)
   {
      int result = net.sf.mpxj.common.DateHelper.compare(m_start, o.m_start);
      if (result == 0)
      {
         result = net.sf.mpxj.common.DateHelper.compare(m_end, o.m_end);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof DateRange)
      {
         DateRange rhs = (DateRange) o;
         result = (compareTo(rhs) == 0);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public int hashCode()
   {
      long start = m_start.getTime();
      long end = m_end.getTime();
      int result = ((int) start ^ (int) (start >> 32)) ^ ((int) end ^ (int) (end >> 32));
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[DateRange start=" + m_start + " end=" + m_end + "]");
   }

   public static final DateRange EMPTY_RANGE = new DateRange(null, null);

   private Date m_start;
   private Date m_end;
}
