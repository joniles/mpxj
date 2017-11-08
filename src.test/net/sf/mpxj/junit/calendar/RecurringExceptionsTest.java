/*
 * file:       RecurringExceptionsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       2017-11-07
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

package net.sf.mpxj.junit.calendar;

import static net.sf.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;

import net.sf.mpxj.Day;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringData;
import net.sf.mpxj.junit.MpxjTestData;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

/**
 * Tests to ensure basic calendar details are read correctly.
 */
public class RecurringExceptionsTest
{
   /**
    * Test to validate calendars in files saved by different versions of MS Project.
    */
   @Test public void testRecurringExceptions() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/calendar-recurring-exceptions", "calendar-recurring-exceptions"))
      {
         testRecurringExceptions(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testRecurringExceptions(File file) throws MPXJException
   {
      //System.out.println(file);
      ProjectReader reader = ProjectReaderUtility.getProjectReader(file.getName());
      if (reader instanceof MPDDatabaseReader)
      {
         assumeJvm();
      }

      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      ProjectFile project = reader.read(file);
      ProjectCalendar calendar = project.getCalendarByName("Standard");
      List<ProjectCalendarException> exceptions = calendar.getCalendarExceptions();

      ProjectCalendarException exception = exceptions.get(0);
      assertEquals("Daily 1", exception.getName());
      assertFalse(exception.getWorking());
      RecurringData data = exception.getRecurring();
      assertEquals(RecurrenceType.DAILY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(1), data.getFrequency());
      assertEquals("01/01/2000", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(3), data.getOccurrences());

      exception = exceptions.get(1);
      assertEquals("Daily 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.DAILY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(3), data.getFrequency());
      assertEquals("01/02/2000", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(4), data.getOccurrences());

      exception = exceptions.get(2);
      assertEquals("Daily 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.DAILY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertEquals("01/03/2000", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(5), data.getOccurrences());

      exception = exceptions.get(3);
      assertEquals("Daily 4", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.DAILY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(7), data.getFrequency());
      assertEquals("01/04/2000", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(6), data.getOccurrences());

      exception = exceptions.get(4);
      assertEquals("Weekly 1 Monday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(1), data.getFrequency());
      assertFalse(data.getWeeklyDay(Day.SUNDAY));
      assertTrue(data.getWeeklyDay(Day.MONDAY));
      assertFalse(data.getWeeklyDay(Day.TUESDAY));
      assertFalse(data.getWeeklyDay(Day.WEDNESDAY));
      assertFalse(data.getWeeklyDay(Day.THURSDAY));
      assertFalse(data.getWeeklyDay(Day.FRIDAY));
      assertFalse(data.getWeeklyDay(Day.SATURDAY));
      assertEquals("01/01/2001", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(3), data.getOccurrences());

      exception = exceptions.get(5);
      assertEquals("Weekly 2 Tuesday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(2), data.getFrequency());
      assertFalse(data.getWeeklyDay(Day.SUNDAY));
      assertFalse(data.getWeeklyDay(Day.MONDAY));
      assertTrue(data.getWeeklyDay(Day.TUESDAY));
      assertFalse(data.getWeeklyDay(Day.WEDNESDAY));
      assertFalse(data.getWeeklyDay(Day.THURSDAY));
      assertFalse(data.getWeeklyDay(Day.FRIDAY));
      assertFalse(data.getWeeklyDay(Day.SATURDAY));
      assertEquals("01/01/2001", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(4), data.getOccurrences());

      exception = exceptions.get(6);
      assertEquals("Weekly 3 Wednesday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(3), data.getFrequency());
      assertFalse(data.getWeeklyDay(Day.SUNDAY));
      assertFalse(data.getWeeklyDay(Day.MONDAY));
      assertFalse(data.getWeeklyDay(Day.TUESDAY));
      assertTrue(data.getWeeklyDay(Day.WEDNESDAY));
      assertFalse(data.getWeeklyDay(Day.THURSDAY));
      assertFalse(data.getWeeklyDay(Day.FRIDAY));
      assertFalse(data.getWeeklyDay(Day.SATURDAY));
      assertEquals("01/01/2001", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(5), data.getOccurrences());

      exception = exceptions.get(7);
      assertEquals("Weekly 4 Thursday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(4), data.getFrequency());
      assertFalse(data.getWeeklyDay(Day.SUNDAY));
      assertFalse(data.getWeeklyDay(Day.MONDAY));
      assertFalse(data.getWeeklyDay(Day.TUESDAY));
      assertFalse(data.getWeeklyDay(Day.WEDNESDAY));
      assertTrue(data.getWeeklyDay(Day.THURSDAY));
      assertFalse(data.getWeeklyDay(Day.FRIDAY));
      assertFalse(data.getWeeklyDay(Day.SATURDAY));
      assertEquals("01/01/2001", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(6), data.getOccurrences());

      exception = exceptions.get(8);
      assertEquals("Weekly 5 Friday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertFalse(data.getWeeklyDay(Day.SUNDAY));
      assertFalse(data.getWeeklyDay(Day.MONDAY));
      assertFalse(data.getWeeklyDay(Day.TUESDAY));
      assertFalse(data.getWeeklyDay(Day.WEDNESDAY));
      assertFalse(data.getWeeklyDay(Day.THURSDAY));
      assertTrue(data.getWeeklyDay(Day.FRIDAY));
      assertFalse(data.getWeeklyDay(Day.SATURDAY));
      assertEquals("01/01/2001", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(7), data.getOccurrences());

      exception = exceptions.get(9);
      assertEquals("Weekly 6 Saturday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(6), data.getFrequency());
      assertFalse(data.getWeeklyDay(Day.SUNDAY));
      assertFalse(data.getWeeklyDay(Day.MONDAY));
      assertFalse(data.getWeeklyDay(Day.TUESDAY));
      assertFalse(data.getWeeklyDay(Day.WEDNESDAY));
      assertFalse(data.getWeeklyDay(Day.THURSDAY));
      assertFalse(data.getWeeklyDay(Day.FRIDAY));
      assertTrue(data.getWeeklyDay(Day.SATURDAY));
      assertEquals("01/01/2001", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(8), data.getOccurrences());

      exception = exceptions.get(10);
      assertEquals("Weekly 7 Sunday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(7), data.getFrequency());
      assertTrue(data.getWeeklyDay(Day.SUNDAY));
      assertFalse(data.getWeeklyDay(Day.MONDAY));
      assertFalse(data.getWeeklyDay(Day.TUESDAY));
      assertFalse(data.getWeeklyDay(Day.WEDNESDAY));
      assertFalse(data.getWeeklyDay(Day.THURSDAY));
      assertFalse(data.getWeeklyDay(Day.FRIDAY));
      assertFalse(data.getWeeklyDay(Day.SATURDAY));
      assertEquals("01/01/2001", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(9), data.getOccurrences());

      exception = exceptions.get(11);
      assertEquals("Monthly Relative 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Day.MONDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(2), data.getFrequency());
      assertEquals("01/01/2002", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(3), data.getOccurrences());

      exception = exceptions.get(12);
      assertEquals("Monthly Relative 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(Day.TUESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(3), data.getFrequency());
      assertEquals("01/01/2002", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(4), data.getOccurrences());

      exception = exceptions.get(13);
      assertEquals("Monthly Relative 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(3), data.getDayNumber());
      assertEquals(Day.WEDNESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(4), data.getFrequency());
      assertEquals("01/01/2002", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(5), data.getOccurrences());

      exception = exceptions.get(14);
      assertEquals("Monthly Relative 4", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(4), data.getDayNumber());
      assertEquals(Day.THURSDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertEquals("01/01/2002", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(6), data.getOccurrences());

      exception = exceptions.get(15);
      assertEquals("Monthly Relative 5", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(5), data.getDayNumber());
      assertEquals(Day.FRIDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(6), data.getFrequency());
      assertEquals("01/01/2002", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(7), data.getOccurrences());

      exception = exceptions.get(16);
      assertEquals("Monthly Relative 6", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Day.SATURDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(7), data.getFrequency());
      assertEquals("01/01/2002", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(8), data.getOccurrences());

      exception = exceptions.get(17);
      assertEquals("Monthly Relative 7", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(Day.SUNDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(8), data.getFrequency());
      assertEquals("01/01/2002", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(9), data.getOccurrences());

      exception = exceptions.get(18);
      assertEquals("Monthly Absolute 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Integer.valueOf(2), data.getFrequency());
      assertEquals("01/01/2003", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(3), data.getOccurrences());

      exception = exceptions.get(19);
      assertEquals("Monthly Absolute 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(4), data.getDayNumber());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertEquals("01/01/2003", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(6), data.getOccurrences());

      exception = exceptions.get(20);
      assertEquals("Yearly Relative 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Day.TUESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(3), data.getMonthNumber());
      assertEquals("01/01/2004", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(4), data.getOccurrences());

      exception = exceptions.get(21);
      assertEquals("Yearly Relative 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(Day.WEDNESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(4), data.getMonthNumber());
      assertEquals("01/01/2004", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(5), data.getOccurrences());

      exception = exceptions.get(22);
      assertEquals("Yearly Relative 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(3), data.getDayNumber());
      assertEquals(Day.THURSDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(5), data.getMonthNumber());
      assertEquals("01/01/2004", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(6), data.getOccurrences());

      exception = exceptions.get(23);
      assertEquals("Yearly Absolute 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Integer.valueOf(2), data.getMonthNumber());
      assertEquals("01/01/2005", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(3), data.getOccurrences());

      exception = exceptions.get(24);
      assertEquals("Yearly Absolute 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(Integer.valueOf(3), data.getMonthNumber());
      assertEquals("01/01/2005", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(4), data.getOccurrences());

      exception = exceptions.get(25);
      assertEquals("Yearly Absolute 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(3), data.getDayNumber());
      assertEquals(Integer.valueOf(4), data.getMonthNumber());
      assertEquals("01/01/2005", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(5), data.getOccurrences());

      exception = exceptions.get(26);
      assertEquals("Recurring Working", exception.getName());
      assertTrue(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Day.SATURDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(1), data.getFrequency());
      assertEquals("01/01/2010", df.format(data.getStartDate()));
      assertEquals(Integer.valueOf(3), data.getOccurrences());
   }
}
