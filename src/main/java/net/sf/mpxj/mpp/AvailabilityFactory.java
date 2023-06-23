/*
 * file:       AvailabilityFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       09/06/2009
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

package net.sf.mpxj.mpp;

import java.time.LocalDateTime;
import java.util.Collections;


import net.sf.mpxj.Availability;
import net.sf.mpxj.AvailabilityTable;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * Common code to read resource availability tables from MPP files.
 */
final class AvailabilityFactory
{
   /**
    * Populates a resource availability table.
    *
    * @param table resource availability table
    * @param data file data
    */
   public void process(AvailabilityTable table, byte[] data)
   {
      if (data != null)
      {
         int items = MPPUtility.getShort(data, 0);
         int offset = 12;

         for (int loop = 0; loop < items; loop++)
         {
            double unitsValue = MPPUtility.getDouble(data, offset + 4);
            if (unitsValue != 0)
            {
               LocalDateTime startDate = MPPUtility.getTimestampFromTenths(data, offset);
               LocalDateTime endDate = MPPUtility.getTimestampFromTenths(data, offset + 20);
               endDate = endDate.minusMinutes(1);
               Double units = NumberHelper.getDouble(unitsValue / 100);

               if (startDate.isBefore(DateHelper.START_DATE_NA))
               {
                  startDate = DateHelper.START_DATE_NA;
               }

               if (endDate.isAfter(DateHelper.END_DATE_NA))
               {
                  endDate = DateHelper.END_DATE_NA;
               }

               Availability item = new Availability(startDate, endDate, units);
               table.add(item);
            }
            offset += 20;
         }
         Collections.sort(table);
      }
   }

}
