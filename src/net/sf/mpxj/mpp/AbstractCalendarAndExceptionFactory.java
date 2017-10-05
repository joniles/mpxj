/*
 * file:       AbstractCalendarAndExceptionFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       2017-10-04
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

import java.util.Calendar;
import java.util.Date;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.DateHelper;

/**
 * Shared code used to read calendar data from MPP files.
 */
abstract class AbstractCalendarAndExceptionFactory extends AbstractCalendarFactory
{
   /**
    * Constructor.
    *
    * @param file parent ProjectFile instance
    */
   public AbstractCalendarAndExceptionFactory(ProjectFile file)
   {
      super(file);
   }

   /**
    * This method extracts any exceptions associated with a calendar.
    *
    * @param data calendar data block
    * @param cal calendar instance
    */
   @Override protected void processCalendarExceptions(byte[] data, ProjectCalendar cal)
   {
      //
      // Handle any exceptions
      //
      if (data.length > 420)
      {
         int offset = 420; // The first 420 is for the working hours data

         int exceptionCount = MPPUtility.getShort(data, offset);

         if (exceptionCount != 0)
         {
            int index;
            ProjectCalendarException exception;
            long duration;
            int periodCount;
            Date start;

            //
            // Move to the start of the first exception
            //
            offset += 4;

            //
            // Each exception is a 92 byte block, followed by a
            // variable length text block
            //
            for (index = 0; index < exceptionCount; index++)
            {

               Date fromDate = MPPUtility.getDate(data, offset);
               Date toDate = MPPUtility.getDate(data, offset + 2);
               exception = cal.addCalendarException(fromDate, toDate);

               periodCount = MPPUtility.getShort(data, offset + 14);
               if (periodCount != 0)
               {
                  for (int exceptionPeriodIndex = 0; exceptionPeriodIndex < periodCount; exceptionPeriodIndex++)
                  {
                     start = MPPUtility.getTime(data, offset + 20 + (exceptionPeriodIndex * 2));
                     duration = MPPUtility.getDuration(data, offset + 32 + (exceptionPeriodIndex * 4));
                     exception.addRange(new DateRange(start, new Date(start.getTime() + duration)));
                  }
               }

               //
               // Extract the name length - ensure that it is aligned to a 4 byte boundary
               //
               int exceptionNameLength = MPPUtility.getInt(data, offset + 88);
               if (exceptionNameLength % 4 != 0)
               {
                  exceptionNameLength = ((exceptionNameLength / 4) + 1) * 4;
               }

               if (exceptionNameLength != 0)
               {
                  exception.setName(MPPUtility.getUnicodeString(data, offset + 92));
               }

               //System.out.println(MPPUtility.hexdump(data, offset, 92, false));
               //System.out.println(MPPUtility.hexdump(data, offset + 92, exceptionNameLength, true));

               offset += (92 + exceptionNameLength);
            }
         }

         processWorkWeeks(data, offset, cal);
      }
   }

   /**
    * Read the work weeks.
    *
    * @param data calendar data
    * @param offset current offset into data
    * @param cal parent calendar
    */
   private void processWorkWeeks(byte[] data, int offset, ProjectCalendar cal)
   {
      //      System.out.println(offset);
      //      System.out.println(cal.getName());
      //      System.out.println(MPPUtility.hexdump(data, true, 16, ""));

      // skip 6 byte header
      offset += 6;

      while (data.length >= offset + ((7 * 60) + 4 + 4 + 8 + 4))
      {
         // skip unknown w bytes
         offset += 2;

         ProjectCalendarWeek week = cal.addWorkWeek();
         for (Day day : Day.values())
         {
            // 60 byte block per day
            processWorkWeekDay(data, offset, week, day);
            offset += 60;
         }

         Date startDate = DateHelper.getDayStartDate(MPPUtility.getDate(data, offset));
         offset += 2;

         Date finishDate = DateHelper.getDayEndDate(MPPUtility.getDate(data, offset));
         offset += 2;

         // skip unknown 8 bytes
         //System.out.println(MPPUtility.hexdump(data, offset, 8, false));
         offset += 8;

         int nameLength = MPPUtility.getInt(data, offset);
         offset += 4;

         String name = MPPUtility.getUnicodeString(data, offset, nameLength);
         offset += nameLength;

         week.setName(name);
         week.setDateRange(new DateRange(startDate, finishDate));
      }
   }

   /**
    * Process an individual work week day.
    *
    * @param data calendar data
    * @param offset current offset into data
    * @param week parent week
    * @param day current day
    */
   private void processWorkWeekDay(byte[] data, int offset, ProjectCalendarWeek week, Day day)
   {
      //System.out.println(MPPUtility.hexdump(data, offset, 60, false));

      int dayType = MPPUtility.getShort(data, offset + 0);
      if (dayType == 1)
      {
         week.setWorkingDay(day, DayType.DEFAULT);
      }
      else
      {
         int rangeCount = MPPUtility.getShort(data, offset + 2);
         if (rangeCount == 0)
         {
            week.setWorkingDay(day, DayType.NON_WORKING);
         }
         else
         {
            week.setWorkingDay(day, DayType.WORKING);
            ProjectCalendarHours hours = week.addCalendarHours(day);

            Calendar cal = Calendar.getInstance();
            for (int index = 0; index < rangeCount; index++)
            {
               Date startTime = DateHelper.getCanonicalTime(MPPUtility.getTime(data, offset + 8 + (index * 2)));
               int durationInSeconds = MPPUtility.getInt(data, offset + 20 + (index * 4)) * 6;
               cal.setTime(startTime);
               cal.add(Calendar.SECOND, durationInSeconds);
               Date finishTime = DateHelper.getCanonicalTime(cal.getTime());
               hours.addRange(new DateRange(startTime, finishTime));
            }
         }
      }
   }
}
