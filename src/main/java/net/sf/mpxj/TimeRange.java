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

import java.time.LocalTime;
import java.util.Date;

import net.sf.mpxj.common.LocalTimeHelper;

/**
 * This class represents a period of time.
 */
public final class TimeRange implements Comparable<TimeRange>
{
   /**
    * Constructor.
    *
    * @param start start time
    * @param end end time
    */
   public TimeRange(LocalTime start, LocalTime end)
   {
      m_start = start;
      m_end = end;
      m_startDate = LocalTimeHelper.getDate(start);
      m_endDate = LocalTimeHelper.getDate(end);
   }

   public TimeRange(Date start, Date end)
   {
      m_start = LocalTimeHelper.getLocalTime(start);
      m_end = LocalTimeHelper.getLocalTime(end);
      m_startDate = start;
      m_endDate = end;
   }

   /**
    * Retrieve the date at the start of the range.
    *
    * @return start date
    */
   public LocalTime getStart()
   {
      return m_start;
   }

   public Date getStartAsDate()
   {
      return m_startDate;
   }

   /**
    * Retrieve the date at the end of the range.
    *
    * @return end date
    */
   public LocalTime getEnd()
   {
      return m_end;
   }

   public Date getEndAsDate()
   {
      return m_endDate;
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
   public int compareTo(LocalTime date)
   {
      return LocalTimeHelper.compare(m_start, m_end, date);
   }

   @Override public int compareTo(TimeRange o)
   {
      int result = LocalTimeHelper.compare(m_start, o.m_start);
      if (result == 0)
      {
         result = LocalTimeHelper.compare(m_end, o.m_end);
      }
      return result;
   }

   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof TimeRange)
      {
         TimeRange rhs = (TimeRange) o;
         result = (compareTo(rhs) == 0);
      }
      return result;
   }

   @Override public int hashCode()
   {
      long start = m_start == null ? 0 : m_start.getNano();
      long end = m_end == null ? 0 : m_end.getNano();
      return ((int) start ^ (int) (start >> 32)) ^ ((int) end ^ (int) (end >> 32));
   }

   @Override public String toString()
   {
      return ("[DateRange start=" + m_start + " end=" + m_end + "]");
   }

   public static final TimeRange EMPTY_RANGE = new TimeRange(null, (LocalTime)null);

   private final LocalTime m_start;
   private final LocalTime m_end;
   private final Date m_startDate;
   private final Date m_endDate;
}