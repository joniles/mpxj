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

package net.sf.mpxj.mpp;

import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringTask;
import net.sf.mpxj.Task;

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
      rt.setStartDate(MPPUtility.getDate(data, 6));
      rt.setFinishDate(MPPUtility.getDate(data, 10));
      rt.setDuration(MPPUtility.getAdjustedDuration(m_properties, MPPUtility.getInt(data, 12), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 16))));
      rt.setOccurrences(Integer.valueOf(MPPUtility.getShort(data, 18)));
      rt.setRecurrenceType(RecurrenceType.getInstance(MPPUtility.getShort(data, 20)));
      rt.setUseEndDate(MPPUtility.getShort(data, 24) == 1);
      rt.setDailyWorkday(MPPUtility.getShort(data, 26) == 1);
      int days = 0;
      days += (MPPUtility.getShort(data, 28) == 1 ? 0x40 : 0x00);
      days += (MPPUtility.getShort(data, 30) == 1 ? 0x20 : 0x00);
      days += (MPPUtility.getShort(data, 32) == 1 ? 0x10 : 0x00);
      days += (MPPUtility.getShort(data, 34) == 1 ? 0x08 : 0x00);
      days += (MPPUtility.getShort(data, 36) == 1 ? 0x04 : 0x00);
      days += (MPPUtility.getShort(data, 38) == 1 ? 0x02 : 0x00);
      days += (MPPUtility.getShort(data, 40) == 1 ? 0x01 : 0x00);
      rt.setWeeklyDays(Integer.valueOf(days));
      rt.setMonthlyRelative(MPPUtility.getShort(data, 42) == 1);
      rt.setYearlyAbsolute(MPPUtility.getShort(data, 44) == 1);
      rt.setDailyFrequency(Integer.valueOf(MPPUtility.getShort(data, 46)));
      rt.setWeeklyFrequency(Integer.valueOf(MPPUtility.getShort(data, 48)));
      rt.setMonthlyRelativeOrdinal(Integer.valueOf(MPPUtility.getShort(data, 50)));
      rt.setMonthlyRelativeDay(Day.getInstance(MPPUtility.getShort(data, 52) + 1));
      rt.setMonthlyAbsoluteFrequency(Integer.valueOf(MPPUtility.getShort(data, 54)));
      rt.setMonthlyAbsoluteDay(Integer.valueOf(MPPUtility.getShort(data, 56)));
      rt.setMonthlyRelativeFrequency(Integer.valueOf(MPPUtility.getShort(data, 58)));
      rt.setYearlyRelativeOrdinal(Integer.valueOf(MPPUtility.getShort(data, 60)));
      rt.setYearlyRelativeDay(Day.getInstance(MPPUtility.getShort(data, 62) + 1));
      rt.setYearlyRelativeMonth(Integer.valueOf(MPPUtility.getShort(data, 64)));
      rt.setYearlyAbsoluteDate(MPPUtility.getDate(data, 70));
   }

   private ProjectProperties m_properties;
}
