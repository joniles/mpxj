/*
 * file:       LocalDateTimeRange.java
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.mpxj.common.LocalDateTimeHelper;

/**
 * This class represents a period of time.
 */
public final class LocalDateTimeRange implements Comparable<LocalDateTimeRange>
{
   /**
    * Constructor.
    *
    * @param startDate start date
    * @param endDate end date
    */
   public LocalDateTimeRange(LocalDateTime startDate, LocalDateTime endDate)
   {
      m_start = startDate;
      m_end = endDate;
   }

   /**
    * Retrieve the date at the start of the range.
    *
    * @return start date
    */
   public LocalDateTime getStart()
   {
      return m_start;
   }

   /**
    * Retrieve the date at the end of the range.
    *
    * @return end date
    */
   public LocalDateTime getEnd()
   {
      return m_end;
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
   public int compareTo(LocalDateTime date)
   {
      return LocalDateTimeHelper.compare(m_start, m_end, date);
   }

   @Override public int compareTo(LocalDateTimeRange o)
   {
      int result = LocalDateTimeHelper.compare(m_start, o.m_start);
      if (result == 0)
      {
         result = LocalDateTimeHelper.compare(m_end, o.m_end);
      }
      return result;
   }

   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof LocalDateTimeRange)
      {
         LocalDateTimeRange rhs = (LocalDateTimeRange) o;
         result = (compareTo(rhs) == 0);
      }
      return result;
   }

   @Override public int hashCode()
   {
      long start = m_start == null ? 0 : m_start.toEpochSecond(ZoneOffset.UTC);
      long end = m_end == null ? 0 : m_end.toEpochSecond(ZoneOffset.UTC);
      return ((int) start ^ (int) (start >> 32)) ^ ((int) end ^ (int) (end >> 32));
   }

   @Override public String toString()
   {
      return ("[LocalDateTimeRange start=" + m_start + " end=" + m_end + "]");
   }

   private final LocalDateTime m_start;
   private final LocalDateTime m_end;
}
