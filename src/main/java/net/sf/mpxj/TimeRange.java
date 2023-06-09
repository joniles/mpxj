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
    * @param start start time
    * @param end end time
    */
   public TimeRange(LocalTime start, LocalTime end)
   {
      m_startAsLocalTime = start;
      m_endAsLocalTime = end;
      m_startAsDate = LocalTimeHelper.getDate(start);
      m_endAsDate = end == LocalTime.MIDNIGHT ? LocalTimeHelper.RANGE_END_MIDNIGHT : LocalTimeHelper.getDate(end);
      long startSeconds = start == null ? 0 : start.toSecondOfDay();
      long endSeconds = end == null ? 0 : (end == LocalTime.MIDNIGHT ? 24 * 60 * 60 : end.toSecondOfDay());
      m_durationAsMilliseconds = (endSeconds - startSeconds) * 1000L;
   }

   public LocalTime getStartAsLocalTime()
   {
      return m_startAsLocalTime;
   }

   public LocalTime getEndAsLocalTime()
   {
      return m_endAsLocalTime;
   }

   public long getDurationAsMilliseconds()
   {
      return m_durationAsMilliseconds;
   }

   @Override public int compareTo(TimeRange o)
   {
      int result = LocalTimeHelper.compare(m_startAsLocalTime, o.m_startAsLocalTime);
      if (result == 0)
      {
         result = Long.compare(m_durationAsMilliseconds, o.m_durationAsMilliseconds);
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

   public static final TimeRange EMPTY_RANGE = new TimeRange(null, null);

   private final Date m_startAsDate;
   private final Date m_endAsDate;
   private final LocalTime m_startAsLocalTime;
   private final LocalTime m_endAsLocalTime;
   private final long m_durationAsMilliseconds;
}
