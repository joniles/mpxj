/*
 * file:       AvailabilityTable.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       08/06/2009
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
import java.util.ArrayList;
import java.util.Comparator;

import org.mpxj.common.LocalDateTimeHelper;

/**
 * This class represents a resource's availability table.
 */
public final class AvailabilityTable extends ArrayList<Availability>
{
   /**
    * Retrieve the table entry valid for the supplied date.
    *
    * @param date required date
    * @return cost rate table entry
    */
   public Availability getEntryByDate(LocalDateTime date)
   {
      Availability result = null;

      for (Availability entry : this)
      {
         LocalDateTimeRange range = entry.getRange();
         int comparisonResult = range.compareTo(date);
         if (comparisonResult >= 0)
         {
            if (comparisonResult == 0)
            {
               result = entry;
               break;
            }
         }
         else
         {
            break;
         }
      }

      return result;
   }

   /**
    * Determine if this table only contains a single entry with empty start and end dates.
    *
    * @return true if the table just has the default entry
    */
   public boolean hasDefaultDateRange()
   {
      // No ranges
      if (isEmpty())
      {
         return true;
      }

      // More than one range
      if (size() != 1)
      {
         return false;
      }

      // One range with NA start and end dates
      LocalDateTimeRange range = get(0).getRange();
      return (range.getStart() == null || range.getStart().equals(LocalDateTimeHelper.START_DATE_NA)) && (range.getEnd() == null || range.getEnd().isAfter(END_DATE_NA));
   }

   /**
    * Calculate the Available From attribute for the given date.
    *
    * @param date target date
    * @return available from attribute
    */
   public LocalDateTime availableFrom(LocalDateTime date)
   {
      if (hasDefaultDateRange())
      {
         return null;
      }

      // Let's ensure we are sorted
      sort(Comparator.naturalOrder());

      LocalDateTimeRange previousRange = null;
      for (Availability availability : this)
      {
         LocalDateTimeRange currentRange = availability.getRange();

         LocalDateTime rangeStart = currentRange.getStart();
         if (rangeStart == null)
         {
            rangeStart = LocalDateTimeHelper.START_DATE_NA;
         }

         // Is our date is before this range?
         if (date.isBefore(rangeStart))
         {
            return previousRange == null ? null : previousRange.getEnd().plusMinutes(1);
         }

         // Is our date within this range?
         LocalDateTime rangeEnd = currentRange.getEnd();
         if (rangeEnd == null || rangeEnd.isAfter(END_DATE_NA) || date.isBefore(rangeEnd) || date.isEqual(rangeEnd))
         {
            return LocalDateTimeHelper.START_DATE_NA.isEqual(rangeStart) ? null : rangeStart;
         }

         // Our date is after this range
         previousRange = currentRange;
      }

      return previousRange == null ? null : previousRange.getEnd().plusMinutes(1);
   }

   /**
    * Calculate the Available To attribute for the given date.
    *
    * @param date target date
    * @return available to attribute
    */
   public LocalDateTime availableTo(LocalDateTime date)
   {
      if (hasDefaultDateRange())
      {
         return null;
      }

      // Let's ensure we are sorted
      sort(Comparator.naturalOrder());

      for (Availability availability : this)
      {
         LocalDateTimeRange currentRange = availability.getRange();

         LocalDateTime rangeEnd = currentRange.getEnd();
         if (rangeEnd == null)
         {
            rangeEnd = LocalDateTimeHelper.END_DATE_NA;
         }

         // Is our date after this range?
         if (date.isAfter(rangeEnd))
         {
            continue;
         }

         LocalDateTime rangeStart = currentRange.getStart();
         if (rangeStart == null)
         {
            rangeStart = LocalDateTimeHelper.START_DATE_NA;
         }

         // Is our date before this range?
         if (date.isBefore(rangeStart))
         {
            return rangeStart.minusMinutes(1);
         }

         // Our date is within the range
         return rangeEnd.isAfter(END_DATE_NA) ? null : rangeEnd;
      }

      return null;
   }

   /**
    * This value differs from the one in LocalDateTimeHelper to allow us to deal with inconsistent values
    * used by Microsoft Project.
    */
   public static final LocalDateTime END_DATE_NA = LocalDateTime.of(2049, 12, 31, 23, 58);
}
