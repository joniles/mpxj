/*
 * file:       ProjectCalendarHours.java
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to represent the records in an MPX file that define
 * working hours in a calendar.
 */
public class ProjectCalendarHours extends AbstractList<TimeRange>
{
   @Override public boolean add(TimeRange range)
   {
      return m_ranges.add(range);
   }

   @Override public TimeRange set(int index, TimeRange value)
   {
      return m_ranges.set(index, value);
   }

   @Override public TimeRange get(int index)
   {
      TimeRange result;

      if (index >= 0 && index < m_ranges.size())
      {
         result = m_ranges.get(index);
      }
      else
      {
         result = TimeRange.EMPTY_RANGE;
      }

      return result;
   }

   /**
    * Retrieve an iterator to allow the list of date ranges to be traversed.
    *
    * @return iterator.
    */
   @Override public Iterator<TimeRange> iterator()
   {
      return m_ranges.iterator();
   }

   @Override public int size()
   {
      return m_ranges.size();
   }

   @Override public void clear()
   {
      m_ranges.clear();
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[ProjectCalendarHours ");
      for (TimeRange range : this)
      {
         sb.append(range.toString());
      }
      sb.append("]");
      return (sb.toString());
   }

   private final List<TimeRange> m_ranges = new ArrayList<>();
}
