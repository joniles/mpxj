/*
 * file:       LocalTimeRange.java
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

package org.mpxj;

import java.time.LocalTime;

import org.mpxj.common.LocalTimeHelper;

/**
 * This class represents a period of time.
 */
public final class LocalTimeRange implements Comparable<LocalTimeRange>
{
   /**
    * Constructor.
    *
    * @param start start time
    * @param end end time
    */
   public LocalTimeRange(LocalTime start, LocalTime end)
   {
      m_start = start;
      m_end = end;
      m_durationAsMilliseconds = LocalTimeHelper.getMillisecondsInRange(start, end);
   }

   /**
    * Retrieve the range start.
    *
    * @return range start
    */
   public LocalTime getStart()
   {
      return m_start;
   }

   /**
    * Retrieve the range end.
    *
    * @return range end
    */
   public LocalTime getEnd()
   {
      return m_end;
   }

   /**
    * Retrieve the duration of the range.
    *
    * @return duration as milliseconds
    */
   public long getDurationAsMilliseconds()
   {
      return m_durationAsMilliseconds;
   }

   @Override public int compareTo(LocalTimeRange o)
   {
      int result = LocalTimeHelper.compare(m_start, o.m_start);
      if (result == 0)
      {
         result = Long.compare(m_durationAsMilliseconds, o.m_durationAsMilliseconds);
      }
      return result;
   }

   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof LocalTimeRange)
      {
         LocalTimeRange rhs = (LocalTimeRange) o;
         result = (compareTo(rhs) == 0);
      }
      return result;
   }

   @Override public int hashCode()
   {
      long start = m_start == null ? 0 : m_start.toNanoOfDay();
      long end = m_end == null ? 0 : m_end.toNanoOfDay();
      return ((int) start ^ (int) (start >> 32)) ^ ((int) end ^ (int) (end >> 32));
   }

   @Override public String toString()
   {
      return ("[LocalTimeRange start=" + m_start + " end=" + m_end + "]");
   }

   public static final LocalTimeRange EMPTY_RANGE = new LocalTimeRange(null, null);

   private final LocalTime m_start;
   private final LocalTime m_end;
   private final long m_durationAsMilliseconds;
}
