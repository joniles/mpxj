/*
 * file:       RecurringTaskReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       23/06/2008
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

import java.time.DayOfWeek;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.ProjectProperties;
import org.mpxj.RecurrenceType;
import org.mpxj.RecurringTask;
import org.mpxj.Task;
import org.mpxj.common.LocalDateHelper;

/**
 * This class allows recurring task definitions to be read from an MPP file.
 */
final class RecurringTaskReader
{
   /**
    * Constructor.
    *
    * @param properties project properties
    */
   public RecurringTaskReader(ProjectProperties properties)
   {
      m_properties = properties;
   }

   /**
    * Reads recurring task data.
    *
    * @param task Task instance
    * @param data recurring task data
    */
   public void processRecurringTask(Task task, byte[] data)
   {
      RecurringTask rt = task.addRecurringTask();
      rt.setStartDate(LocalDateHelper.getLocalDate(MPPUtility.getDate(data, 6)));
      rt.setFinishDate(LocalDateHelper.getLocalDate(MPPUtility.getDate(data, 10)));
      rt.setDuration(MPPUtility.getAdjustedDuration(m_properties, ByteArrayHelper.getInt(data, 12), MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, 16))));
      rt.setOccurrences(Integer.valueOf(ByteArrayHelper.getShort(data, 18)));
      rt.setRecurrenceType(RecurrenceType.getInstance(ByteArrayHelper.getShort(data, 20)));
      rt.setUseEndDate(ByteArrayHelper.getShort(data, 24) == 1);
      rt.setWorkingDaysOnly(ByteArrayHelper.getShort(data, 26) == 1);
      rt.setWeeklyDay(DayOfWeek.SUNDAY, ByteArrayHelper.getShort(data, 28) == 1);
      rt.setWeeklyDay(DayOfWeek.MONDAY, ByteArrayHelper.getShort(data, 30) == 1);
      rt.setWeeklyDay(DayOfWeek.TUESDAY, ByteArrayHelper.getShort(data, 32) == 1);
      rt.setWeeklyDay(DayOfWeek.WEDNESDAY, ByteArrayHelper.getShort(data, 34) == 1);
      rt.setWeeklyDay(DayOfWeek.THURSDAY, ByteArrayHelper.getShort(data, 36) == 1);
      rt.setWeeklyDay(DayOfWeek.FRIDAY, ByteArrayHelper.getShort(data, 38) == 1);
      rt.setWeeklyDay(DayOfWeek.SATURDAY, ByteArrayHelper.getShort(data, 40) == 1);

      int frequencyOffset = 0;
      int dayOfWeekOffset = 0;
      int dayNumberOffset = 0;
      int monthNumberOffset = 0;
      int dateOffset = 0;

      switch (rt.getRecurrenceType())
      {
         case DAILY:
         {
            frequencyOffset = 46;
            break;
         }

         case WEEKLY:
         {
            frequencyOffset = 48;
            break;
         }

         case MONTHLY:
         {
            rt.setRelative(ByteArrayHelper.getShort(data, 42) == 1);
            if (rt.getRelative())
            {
               frequencyOffset = 58;
               dayNumberOffset = 50;
               dayOfWeekOffset = 52;
            }
            else
            {
               frequencyOffset = 54;
               dayNumberOffset = 56;
            }

            break;
         }

         case YEARLY:
         {
            rt.setRelative(ByteArrayHelper.getShort(data, 44) != 1);
            if (rt.getRelative())
            {
               dayNumberOffset = 60;
               dayOfWeekOffset = 62;
               monthNumberOffset = 64;
            }
            else
            {
               dateOffset = 70;
            }
            break;
         }
      }

      if (frequencyOffset != 0)
      {
         rt.setFrequency(Integer.valueOf(ByteArrayHelper.getShort(data, frequencyOffset)));
      }

      if (dayOfWeekOffset != 0)
      {
         rt.setDayOfWeek(DayOfWeekHelper.getInstance(ByteArrayHelper.getShort(data, dayOfWeekOffset) + 1));
      }

      if (dayNumberOffset != 0)
      {
         rt.setDayNumber(Integer.valueOf(ByteArrayHelper.getShort(data, dayNumberOffset)));
      }

      if (monthNumberOffset != 0)
      {
         rt.setMonthNumber(Integer.valueOf(ByteArrayHelper.getShort(data, monthNumberOffset)));
      }

      if (dateOffset != 0)
      {
         rt.setYearlyAbsoluteFromDate(LocalDateHelper.getLocalDate(MPPUtility.getDate(data, dateOffset)));
      }
   }

   private final ProjectProperties m_properties;
}
