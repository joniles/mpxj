/*
 * file:       CostRateTableFactory.java
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

import java.util.Collections;
import java.util.Date;

import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.Rate;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.utility.NumberUtility;

/**
 * Common code to read resource cost rate tables from MPP files.
 */
final class CostRateTableFactory
{
   /**
    * Creates a CostRateTable instance from a block of data.
    * 
    * @param data data block
    * @return CostRateTable instance
    */
   public CostRateTable process(byte[] data)
   {
      CostRateTable result = new CostRateTable();

      if (data != null)
      {
         for (int i = 16; i + 44 <= data.length; i += 44)
         {
            Rate standardRate = new Rate(MPPUtility.getDouble(data, i), TimeUnit.HOURS);
            TimeUnit standardRateFormat = getFormat(MPPUtility.getShort(data, i + 8));
            Rate overtimeRate = new Rate(MPPUtility.getDouble(data, i + 16), TimeUnit.HOURS);
            TimeUnit overtimeRateFormat = getFormat(MPPUtility.getShort(data, i + 24));
            Double costPerUse = NumberUtility.getDouble(MPPUtility.getDouble(data, i + 32) / 100.0);
            Date endDate = MPPUtility.getTimestampFromTenths(data, i + 40);
            CostRateTableEntry entry = new CostRateTableEntry(standardRate, standardRateFormat, overtimeRate, overtimeRateFormat, costPerUse, endDate);
            result.add(entry);
         }
         Collections.sort(result);
      }
      else
      {
         result.add(CostRateTableEntry.DEFAULT_ENTRY);
      }

      return result;
   }

   /**
    * Converts an integer into a time format.
    * 
    * @param format integer format value
    * @return TimeUnit instance
    */
   private TimeUnit getFormat(int format)
   {
      TimeUnit result;
      if (format == 0xFFFF)
      {
         result = TimeUnit.HOURS;
      }
      else
      {
         result = MPPUtility.getWorkTimeUnits(format);
      }
      return result;
   }
}
