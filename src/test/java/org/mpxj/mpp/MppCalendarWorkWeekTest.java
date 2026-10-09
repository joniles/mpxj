/*
 * file:       MppCalendarWorkWeekTest.java
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package org.mpxj.mpp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.mpxj.DayType;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MppCalendarWorkWeekTest
{
   @Test public void testInvalidDurationsAreDiscarded()
   {
      int[] invalid = { Integer.MIN_VALUE, -1, 0, 14401, Integer.MAX_VALUE };

      for (int duration : invalid)
      {
         ProjectCalendarWeek week = readSunday(new int[] { 0 }, new int[] { duration });

         assertEquals(DayType.NON_WORKING, week.getCalendarDayType(DayOfWeek.SUNDAY));
         assertTrue(week.getCalendarHours(DayOfWeek.SUNDAY).isEmpty());
      }
   }

   @Test public void testValidRangesSurviveInvalidRanges()
   {
      ProjectCalendarWeek week = readSunday(
         new int[] { 0, 4800, 0, 7800 },
         new int[] { 0, 2400, Integer.MAX_VALUE, 2400 });

      assertEquals(DayType.WORKING, week.getCalendarDayType(DayOfWeek.SUNDAY));

      ProjectCalendarHours hours = week.getCalendarHours(DayOfWeek.SUNDAY);
      assertEquals(2, hours.size());
      assertEquals(LocalTime.of(8, 0), hours.get(0).getStart());
      assertEquals(LocalTime.of(12, 0), hours.get(0).getEnd());
      assertEquals(LocalTime.of(13, 0), hours.get(1).getStart());
      assertEquals(LocalTime.of(17, 0), hours.get(1).getEnd());
   }

   @Test public void testMinimumValidDuration()
   {
      ProjectCalendarWeek week = readSunday(
         new int[] { 4800 }, new int[] { 1 });

      assertEquals(DayType.WORKING, week.getCalendarDayType(DayOfWeek.SUNDAY));

      LocalTimeRange range = week.getCalendarHours(DayOfWeek.SUNDAY).get(0);
      assertEquals(LocalTime.of(8, 0), range.getStart());
      assertEquals(LocalTime.of(8, 0, 6), range.getEnd());
   }

   @Test public void testFullDayDuration()
   {
      ProjectCalendarWeek week = readSunday(
         new int[] { 0 }, new int[] { 14400 });

      assertEquals(DayType.WORKING, week.getCalendarDayType(DayOfWeek.SUNDAY));

      LocalTimeRange range = week.getCalendarHours(DayOfWeek.SUNDAY).get(0);
      assertEquals(LocalTime.MIDNIGHT, range.getStart());
      assertEquals(LocalTime.MIDNIGHT, range.getEnd());
      assertEquals(86400000L, range.getDurationAsMilliseconds());
   }

   @Test public void testNoDeclaredRanges()
   {
      ProjectCalendarWeek week = readSunday(new int[0], new int[0]);

      assertEquals(DayType.NON_WORKING, week.getCalendarDayType(DayOfWeek.SUNDAY));
      assertTrue(week.getCalendarHours(DayOfWeek.SUNDAY).isEmpty());
      assertEquals(DayType.DEFAULT, week.getCalendarDayType(DayOfWeek.MONDAY));
   }

   private ProjectCalendarWeek readSunday(int[] starts, int[] durations)
   {
      // 420 bytes of normal hours, 4 bytes of exception metadata,
      // 4 bytes of work week header, and 436 bytes per work week.
      byte[] data = new byte[864];
      ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

      // Other days inherit their normal calendar hours.
      for (int day = 0; day < 7; day++)
      {
         buffer.putShort(428 + day * 60, (short) 1);
      }

      // Sunday explicitly defines the ranges under test.
      buffer.putShort(428, (short) 0);
      buffer.putShort(430, (short) durations.length);

      for (int i = 0; i < durations.length; i++)
      {
         buffer.putShort(436 + i * 2, (short) starts[i]);
         buffer.putInt(448 + i * 4, durations[i]);
      }

      ProjectFile file = new ProjectFile();
      ProjectCalendar calendar = new ProjectCalendar(file);
      new MPP14CalendarFactory(file).processCalendarExceptions(data, calendar);

      assertEquals(1, calendar.getWorkWeeks().size());
      return calendar.getWorkWeeks().get(0);
   }
}
