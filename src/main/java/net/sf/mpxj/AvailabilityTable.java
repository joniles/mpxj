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

package net.sf.mpxj;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
}
