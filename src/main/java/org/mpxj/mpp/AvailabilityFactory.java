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

package org.mpxj.mpp;

import java.time.LocalDateTime;
import java.util.Collections;

import org.mpxj.Availability;
import org.mpxj.AvailabilityTable;
import org.mpxj.Resource;
import org.mpxj.ResourceField;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;

/**
 * Common code to read resource availability tables from MPP files.
 */
final class AvailabilityFactory
{
   /**
    * Populates a resource availability table.
    *
    * @param resource parent resource
    * @param data file data
    */
   public void process(Resource resource, byte[] data)
   {
      AvailabilityTable table = resource.getAvailability();
      if (data == null)
      {
         // If we don't have an availability table, we'll construct one.
         // Note the use of getCachedValue to ensure we use the values read from
         // the file rather than attempting to calculate them.
         LocalDateTime availableFrom = (LocalDateTime) resource.getCachedValue(ResourceField.AVAILABLE_FROM);
         LocalDateTime availableTo = (LocalDateTime) resource.getCachedValue(ResourceField.AVAILABLE_TO);
         availableFrom = availableFrom == null ? LocalDateTimeHelper.START_DATE_NA : availableFrom;
         availableTo = availableTo == null ? LocalDateTimeHelper.END_DATE_NA : availableTo;
         table.add(new Availability(availableFrom, availableTo, (Number) resource.getCachedValue(ResourceField.MAX_UNITS)));
      }
      else
      {
         int items = ByteArrayHelper.getShort(data, 0);
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

               if (startDate.isBefore(LocalDateTimeHelper.START_DATE_NA))
               {
                  startDate = LocalDateTimeHelper.START_DATE_NA;
               }

               if (endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
               {
                  endDate = LocalDateTimeHelper.END_DATE_NA;
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
