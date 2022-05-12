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
public class ProjectCalendarHours extends AbstractList<DateRange>
{
   @Override public boolean add(DateRange range)
   {
      return m_ranges.add(range);
   }

   /**
    * Add a date range to the list of date ranges.
    *
    * @param range date range
    * @deprecated use {@code add}
    */
   @Deprecated public void addRange(DateRange range)
   {
      add(range);
   }

   /**
    * Retrieve the date range at the specified index.
    * The index is zero based, and this method will return
    * null if the requested date range does not exist.
    *
    * @param index range index
    * @return date range instance
    * @deprecated use {@code get}
    */
   @Deprecated public DateRange getRange(int index)
   {
      return get(index);
   }

   /**
    * Replace a date range at the specified index.
    *
    * @param index range index
    * @param value DateRange instance
    * @deprecated use {@code set}
    */
   @Deprecated public void setRange(int index, DateRange value)
   {
      set(index, value);
   }

   @Override public DateRange get(int index)
   {
      DateRange result;

      if (index >= 0 && index < m_ranges.size())
      {
         result = m_ranges.get(index);
      }
      else
      {
         result = DateRange.EMPTY_RANGE;
      }

      return result;
   }

   /**
    * Retrieve an iterator to allow the list of date ranges to be traversed.
    *
    * @return iterator.
    */
   @Override public Iterator<DateRange> iterator()
   {
      return m_ranges.iterator();
   }

   @Override public int size()
   {
      return m_ranges.size();
   }

   /**
    * Returns the number of date ranges associated with this instance.
    *
    * @return number of date ranges
    * @deprecated use {@code size}
    */
   @Deprecated public int getRangeCount()
   {
      return size();
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[ProjectCalendarHours ");
      for (DateRange range : this)
      {
         sb.append(range.toString());
      }
      sb.append("]");
      return (sb.toString());
   }

   private final List<DateRange> m_ranges = new ArrayList<>();
}
