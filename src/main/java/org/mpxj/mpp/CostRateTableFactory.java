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

package org.mpxj.mpp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.Resource;
import org.mpxj.ResourceField;
import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.RateHelper;

/**
 * Common code to read resource cost rate tables from MPP files.
 */
final class CostRateTableFactory
{
   public CostRateTableFactory(ProjectFile file)
   {
      m_file = file;
   }

   /**
    * Creates a CostRateTable instance from a block of data.
    *
    * @param resource parent resource
    * @param index cost rate table index
    * @param data data block
    */
   public void process(Resource resource, int index, byte[] data)
   {
      List<CostRateTableEntry> entries = new ArrayList<>();

      //
      // Extract core data
      //
      if (data == null)
      {
         //
         // MS Project economises by not actually storing the first cost rate
         // table if it doesn't need to, so we take this into account here.
         //
         if (index == 0)
         {
            Rate standardRate = resource.getStandardRate() == null ? Rate.ZERO : (Rate) resource.getCachedValue(ResourceField.STANDARD_RATE);
            Rate overtimeRate = resource.getOvertimeRate() == null ? Rate.ZERO : (Rate) resource.getCachedValue(ResourceField.OVERTIME_RATE);

            Number costPerUse = resource.getCostPerUse() == null ? NumberHelper.DOUBLE_ZERO : (Number) resource.getCachedValue(ResourceField.COST_PER_USE);
            LocalDateTime endDate = CostRateTableEntry.DEFAULT_ENTRY.getEndDate();

            entries.add(new CostRateTableEntry(null, endDate, costPerUse, standardRate, overtimeRate));
         }
         else
         {
            entries.add(CostRateTableEntry.DEFAULT_ENTRY);
         }
      }
      else
      {
         for (int i = 16; i + 44 <= data.length; i += 44)
         {
            TimeUnit standardRateFormat = getFormat(ByteArrayHelper.getShort(data, i + 8));
            Rate standardRate = RateHelper.convertFromHours(m_file.getProjectProperties(), MPPUtility.getDouble(data, i), standardRateFormat);

            TimeUnit overtimeRateFormat = getFormat(ByteArrayHelper.getShort(data, i + 24));
            Rate overtimeRate = RateHelper.convertFromHours(m_file.getProjectProperties(), MPPUtility.getDouble(data, i + 16), overtimeRateFormat);

            Double costPerUse = NumberHelper.getDouble(MPPUtility.getDouble(data, i + 32) / 100.0);
            LocalDateTime endDate = MPPUtility.getTimestampFromTenths(data, i + 40);

            if (endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
            {
               endDate = LocalDateTimeHelper.END_DATE_NA;
            }
            else
            {
               //
               // MPP files only store the end date of the range, and typically this
               // will be represented as the last minute of the range, e,g, 07:59,
               // so the next range starts at 08:00. Occasionally we see the start time of the
               // next range stored here, so this heuristic is used to identify what looks
               // like a start time (minutes divisible by 10) and subtracts one minute so that
               // the next range starts at the correct time.
               //
               int minutes = endDate.getMinute();
               if ((minutes % 5) == 0)
               {
                  endDate = endDate.minusMinutes(1);
               }
            }

            // Heuristic to weed out invalid entries: if the timestamp has seconds
            // the whole entry is likely to be invalid and can be ignored.
            if (endDate.getSecond() != 0)
            {
               continue;
            }

            entries.add(new CostRateTableEntry(null, endDate, costPerUse, standardRate, overtimeRate));
         }
      }

      //
      // Populate start dates
      //
      Collections.sort(entries);
      CostRateTable result = new CostRateTable();

      for (int i = 0; i < entries.size(); i++)
      {
         LocalDateTime startDate;
         if (i == 0)
         {
            startDate = LocalDateTimeHelper.START_DATE_NA;
         }
         else
         {
            startDate = entries.get(i - 1).getEndDate().plusMinutes(1);
         }

         CostRateTableEntry entry = entries.get(i);
         result.add(new CostRateTableEntry(startDate, entry.getEndDate(), entry.getCostPerUse(), entry.getStandardRate(), entry.getOvertimeRate()));
      }

      resource.setCostRateTable(index, result);
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

         // For "flat" rates (for example, for cost or material resources) where there is
         // no time component, the MPP file stores a time unit which we recognise
         // as elapsed minutes. If we encounter this, reset the time units to hours
         // so we don't try to change the value.
         // TODO: improve handling of  cost and material rates
         if (result == TimeUnit.ELAPSED_MINUTES)
         {
            result = TimeUnit.HOURS;
         }
      }
      return result;
   }

   private final ProjectFile m_file;
}
