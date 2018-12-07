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
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringData;
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

         if (exceptionCount == 0)
         {
            // align with 8 byte boundary ready to read work weeks
            offset += 4;
         }
         else
         {
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
            for (int index = 0; index < exceptionCount; index++)
            {
               if (offset + 92 > data.length)
               {
                  // Bail out if we don't have at least 92 bytes available
                  break;
               }

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

               //System.out.println(ByteArrayHelper.hexdump(data, offset, 92, false));

               RecurringData rd = new RecurringData();
               int recurrenceTypeValue = MPPUtility.getShort(data, offset + 72);
               rd.setStartDate(exception.getFromDate());
               rd.setFinishDate(exception.getToDate());
               rd.setRecurrenceType(getRecurrenceType(recurrenceTypeValue));
               rd.setRelative(getRelative(recurrenceTypeValue));
               rd.setOccurrences(Integer.valueOf(MPPUtility.getShort(data, offset + 4)));

               switch (rd.getRecurrenceType())
               {
                  case DAILY:
                  {
                     int frequency;
                     if (recurrenceTypeValue == 1)
                     {
                        frequency = 1;
                     }
                     else
                     {
                        frequency = MPPUtility.getShort(data, offset + 76);
                     }
                     rd.setFrequency(Integer.valueOf(frequency));
                     break;
                  }

                  case WEEKLY:
                  {
                     rd.setWeeklyDaysFromBitmap(Integer.valueOf(MPPUtility.getByte(data, offset + 76)), DAY_MASKS);
                     rd.setFrequency(Integer.valueOf(MPPUtility.getShort(data, offset + 78)));
                     break;
                  }

                  case MONTHLY:
                  {
                     if (rd.getRelative())
                     {
                        rd.setDayOfWeek(Day.getInstance(MPPUtility.getByte(data, offset + 77) - 2));
                        rd.setDayNumber(Integer.valueOf(MPPUtility.getByte(data, offset + 76) + 1));
                        rd.setFrequency(Integer.valueOf(MPPUtility.getShort(data, offset + 78)));
                     }
                     else
                     {
                        rd.setDayNumber(Integer.valueOf(MPPUtility.getByte(data, offset + 76)));
                        rd.setFrequency(Integer.valueOf(MPPUtility.getByte(data, offset + 78)));
                     }
                     break;
                  }

                  case YEARLY:
                  {
                     if (rd.getRelative())
                     {
                        rd.setDayOfWeek(Day.getInstance(MPPUtility.getByte(data, offset + 78) - 2));
                        rd.setDayNumber(Integer.valueOf(MPPUtility.getByte(data, offset + 77) + 1));
                     }
                     else
                     {
                        rd.setDayNumber(Integer.valueOf(MPPUtility.getByte(data, offset + 77)));
                     }
                     rd.setMonthNumber(Integer.valueOf(MPPUtility.getByte(data, offset + 76) + 1));
                     break;
                  }
               }

               //
               // The default values for a non-recurring exception are daily, with 1 occurrence.
               // Only add recurrence data if it is non-default.
               //
               if (rd.getRecurrenceType() != RecurrenceType.DAILY || rd.getOccurrences().intValue() != 1)
               {
                  exception.setRecurring(rd);
               }

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
      //      System.out.println("Calendar=" + cal.getName());
      //      System.out.println("Work week block start offset=" + offset);
      //      System.out.println(ByteArrayHelper.hexdump(data, true, 16, ""));

      // skip 4 byte header
      offset += 4;

      while (data.length >= offset + ((7 * 60) + 2 + 2 + 8 + 4))
      {
         //System.out.println("Week start offset=" + offset);
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
         //System.out.println(ByteArrayHelper.hexdump(data, offset, 8, false));
         offset += 8;

         //
         // Extract the name length - ensure that it is aligned to a 4 byte boundary
         //
         int nameLength = MPPUtility.getInt(data, offset);
         if (nameLength % 4 != 0)
         {
            nameLength = ((nameLength / 4) + 1) * 4;
         }
         offset += 4;

         if (nameLength != 0)
         {
            String name = MPPUtility.getUnicodeString(data, offset, nameLength);
            offset += nameLength;
            week.setName(name);
         }

         week.setDateRange(new DateRange(startDate, finishDate));
         // System.out.println(week);
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
      //System.out.println(ByteArrayHelper.hexdump(data, offset, 60, false));

      int dayType = MPPUtility.getShort(data, offset + 0);
      if (dayType == 1)
      {
         week.setWorkingDay(day, DayType.DEFAULT);
      }
      else
      {
         ProjectCalendarHours hours = week.addCalendarHours(day);
         int rangeCount = MPPUtility.getShort(data, offset + 2);
         if (rangeCount == 0)
         {
            week.setWorkingDay(day, DayType.NON_WORKING);
         }
         else
         {
            week.setWorkingDay(day, DayType.WORKING);
            Calendar cal = DateHelper.popCalendar();
            for (int index = 0; index < rangeCount; index++)
            {
               Date startTime = DateHelper.getCanonicalTime(MPPUtility.getTime(data, offset + 8 + (index * 2)));
               int durationInSeconds = MPPUtility.getInt(data, offset + 20 + (index * 4)) * 6;
               cal.setTime(startTime);
               cal.add(Calendar.SECOND, durationInSeconds);
               Date finishTime = DateHelper.getCanonicalTime(cal.getTime());
               hours.addRange(new DateRange(startTime, finishTime));
            }
            DateHelper.pushCalendar(cal);
         }
      }
   }

   /**
    * Retrieve the recurrence type.
    *
    * @param value integer value
    * @return RecurrenceType instance
    */
   private RecurrenceType getRecurrenceType(int value)
   {
      RecurrenceType result;
      if (value < 0 || value >= RECURRENCE_TYPES.length)
      {
         result = null;
      }
      else
      {
         result = RECURRENCE_TYPES[value];
      }

      return result;
   }

   /**
    * Determine if the exception is relative based on the recurrence type integer value.
    *
    * @param value integer value
    * @return true if the recurrence is relative
    */
   private boolean getRelative(int value)
   {
      boolean result;
      if (value < 0 || value >= RELATIVE_MAP.length)
      {
         result = false;
      }
      else
      {
         result = RELATIVE_MAP[value];
      }

      return result;
   }

   private static final RecurrenceType[] RECURRENCE_TYPES =
   {
      null,
      RecurrenceType.DAILY,
      RecurrenceType.YEARLY, // Absolute
      RecurrenceType.YEARLY, // Relative
      RecurrenceType.MONTHLY, // Absolute
      RecurrenceType.MONTHLY, // Relative
      RecurrenceType.WEEKLY,
      RecurrenceType.DAILY
   };

   private static final boolean[] RELATIVE_MAP =
   {
      false,
      false,
      false,
      true,
      false,
      true
   };

   private static final int[] DAY_MASKS =
   {
      0x00,
      0x01, // Sunday
      0x02, // Monday
      0x04, // Tuesday
      0x08, // Wednesday
      0x10, // Thursday
      0x20, // Friday
      0x40, // Saturday
   };

}
