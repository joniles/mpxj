/*
 * file:       TimeRange.java
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

import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.LocalTimeHelper;

/**
 * This class represents a period of time.
 */
public final class TimeRange implements Comparable<TimeRange>
{
   /**
    * Constructor.
    *
    * @param startDate start date
    * @param endDate end date
    */
   public TimeRange(Date startDate, Date endDate)
   {
      m_startAsDate = DateHelper.getCanonicalTime(startDate);
      m_endAsDate = DateHelper.getCanonicalEndTime(startDate, endDate);
      m_startAsMilliseconds = m_startAsDate == null ? 0 : m_startAsDate.getTime() - LocalTimeHelper.RANGE_START_MIDNIGHT.getTime();
      m_endAsMilliseconds = m_endAsDate == null ? 0 : m_endAsDate.getTime() - LocalTimeHelper.RANGE_START_MIDNIGHT.getTime();
      m_durationAsMilliseconds = m_endAsMilliseconds - m_startAsMilliseconds;
   }

   public TimeRange(LocalTime start, LocalTime end)
   {
      m_startAsDate = LocalTimeHelper.getDate(start);
      m_endAsDate = end == LocalTime.MIDNIGHT ? LocalTimeHelper.RANGE_END_MIDNIGHT : LocalTimeHelper.getDate(end);
      m_startAsMilliseconds = start.toSecondOfDay() * 1000L;
      m_endAsMilliseconds = end == LocalTime.MIDNIGHT ? LocalTimeHelper.RANGE_END_MIDNIGHT.getTime() - LocalTimeHelper.RANGE_START_MIDNIGHT.getTime() : end.toSecondOfDay() * 1000L;
      m_durationAsMilliseconds = m_endAsMilliseconds - m_startAsMilliseconds;
   }

   /**
    * Retrieve the date at the start of the range.
    *
    * @return start date
    */
   public Date getStartAsDate()
   {
      return m_startAsDate;
   }

   /**
    * Retrieve the date at the end of the range.
    *
    * @return end date
    */
   public Date getEndAsDate()
   {
      return m_endAsDate;
   }

   public long getDurationAsMilliseconds()
   {
      return m_durationAsMilliseconds;
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
      return DateHelper.compare(m_startAsDate, m_endAsDate, date);
   }

   @Override public int compareTo(TimeRange o)
   {
      int result = DateHelper.compare(m_startAsDate, o.m_startAsDate);
      if (result == 0)
      {
         result = DateHelper.compare(m_endAsDate, o.m_endAsDate);
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
      long start = m_startAsDate == null ? 0 : m_startAsDate.getTime();
      long end = m_endAsDate == null ? 0 : m_endAsDate.getTime();
      return ((int) start ^ (int) (start >> 32)) ^ ((int) end ^ (int) (end >> 32));
   }

   @Override public String toString()
   {
      return ("[DateRange start=" + m_startAsDate + " end=" + m_endAsDate + "]");
   }

   public static final TimeRange EMPTY_RANGE = new TimeRange((Date)null, (Date)null);

   private final Date m_startAsDate;
   private final Date m_endAsDate;
   private final long m_startAsMilliseconds;
   private final long m_endAsMilliseconds;
   private final long m_durationAsMilliseconds;
}
