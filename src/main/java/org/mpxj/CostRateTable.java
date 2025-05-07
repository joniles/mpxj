/*
 * file:       CostRateTable.java
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

/**
 * This class represents a resource's cost rate table.
 */
public class CostRateTable extends ArrayList<CostRateTableEntry>
{
   /**
    * Retrieve the table entry valid for the supplied date.
    *
    * @param date required date
    * @return cost rate table entry
    */
   public CostRateTableEntry getEntryByDate(LocalDateTime date)
   {
      CostRateTableEntry result = null;

      for (CostRateTableEntry entry : this)
      {
         if (date.isBefore(entry.getEndDate()))
         {
            result = entry;
            break;
         }
      }

      return result;
   }

   /**
    * Retrieve the index of the table entry valid for the supplied date.
    *
    * @param date required date
    * @return cost rate table entry index
    */
   public int getIndexByDate(LocalDateTime date)
   {
      int result = -1;
      int index = 0;

      for (CostRateTableEntry entry : this)
      {
         if (date.isBefore(entry.getEndDate()))
         {
            result = index;
            break;
         }
         ++index;
      }

      return result;
   }

   /**
    * Returns true if the table is populated with non-zero entries.
    *
    * @return true if table populated
    */
   public boolean tableIsPopulated()
   {
      return !(isEmpty() || (size() == 1 && allEntriesAreDefault()));
   }

   /**
    * Returns true if all entries are zero.
    *
    * @return true if all entries zero
    */
   private boolean allEntriesAreDefault()
   {
      for (CostRateTableEntry entry : this)
      {
         for (int rateIndex = 0; rateIndex < CostRateTableEntry.MAX_RATES; rateIndex++)
         {
            Rate rate = entry.getRate(rateIndex);
            if (rate != null && rate.getAmount() != 0.0)
            {
               return false;
            }
         }
      }

      return true;
   }

   public static final int MAX_TABLES = 5;
}
