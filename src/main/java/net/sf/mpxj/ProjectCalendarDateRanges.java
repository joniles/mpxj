/*
 * file:       ProjectCalendarDateRanges.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2008
 * date:       11/11/2008
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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * An abstract class representing a collection of date ranges
 * with supporting methods.
 */
public abstract class ProjectCalendarDateRanges implements Iterable<DateRange>
{
   /**
    * Add a date range to the list of date ranges.
    *
    * @param range date range
    */
   public void addRange(DateRange range)
   {
      m_ranges.add(range);
   }

   /**
    * Retrieve the date range at the specified index.
    * The index is zero based, and this method will return
    * null if the requested date range does not exist.
    *
    * @param index range index
    * @return date range instance
    */
   public DateRange getRange(int index)
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

      return (result);
   }

   /**
    * Replace a date range at the specified index.
    *
    * @param index range index
    * @param value DateRange instance
    */
   public void setRange(int index, DateRange value)
   {
      m_ranges.set(index, value);
   }

   /**
    * Retrieve an iterator to allow the list of date ranges to be traversed.
    *
    * @return iterator.
    */
   @Override public Iterator<DateRange> iterator()
   {
      return (m_ranges.iterator());
   }

   /**
    * Returns the number of date ranges associated with this instance.
    *
    * @return number of date ranges
    */
   public int getRangeCount()
   {
      return (m_ranges.size());
   }

   private LinkedList<DateRange> m_ranges = new LinkedList<DateRange>();
}
