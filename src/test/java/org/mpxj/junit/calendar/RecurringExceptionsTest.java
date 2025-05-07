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

package org.mpxj.junit.calendar;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import java.time.DayOfWeek;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectFile;
import org.mpxj.RecurrenceType;
import org.mpxj.RecurringData;
import org.mpxj.junit.MpxjTestData;

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
      ProjectFile project = new UniversalProjectReader().read(file);
      ProjectCalendar calendar = project.getCalendarByName("Standard");
      List<ProjectCalendarException> exceptions = calendar.getCalendarExceptions();

      // The definition of this in MS Project is _technically_ a recurring
      // exception, but we're flattening it as it doesn't actually recur.
      ProjectCalendarException exception = exceptions.get(0);
      assertEquals("Daily 1", exception.getName());
      assertFalse(exception.getWorking());
      assertNull(exception.getRecurring());
      assertEquals(1, exception.getExpandedExceptions().size());
      assertEquals(LocalDate.of(2000, 1, 1), exception.getFromDate());

      exception = exceptions.get(1);
      assertEquals("Daily 2", exception.getName());
      assertFalse(exception.getWorking());
      RecurringData data = exception.getRecurring();
      assertEquals(RecurrenceType.DAILY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(3), data.getFrequency());
      assertEquals(LocalDate.of(2000, 2, 1), data.getStartDate());
      assertEquals(Integer.valueOf(4), data.getOccurrences());
      assertEquals(4, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("01/02/2000", "04/02/2000", "07/02/2000", "10/02/2000"), getExpandedDates(exception));

      exception = exceptions.get(2);
      assertEquals("Daily 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.DAILY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertEquals(LocalDate.of(2000, 3, 1), data.getStartDate());
      assertEquals(Integer.valueOf(5), data.getOccurrences());
      assertEquals(5, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("01/03/2000", "06/03/2000", "11/03/2000", "16/03/2000", "21/03/2000"), getExpandedDates(exception));

      exception = exceptions.get(3);
      assertEquals("Daily 4", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.DAILY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(7), data.getFrequency());
      assertEquals(LocalDate.of(2000, 4, 1), data.getStartDate());
      assertEquals(Integer.valueOf(6), data.getOccurrences());
      assertEquals(6, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("01/04/2000", "08/04/2000", "15/04/2000", "22/04/2000", "29/04/2000", "06/05/2000"), getExpandedDates(exception));

      exception = exceptions.get(4);
      assertEquals("Weekly 1 Monday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(1), data.getFrequency());
      assertFalse(data.getWeeklyDay(DayOfWeek.SUNDAY));
      assertTrue(data.getWeeklyDay(DayOfWeek.MONDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.TUESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.WEDNESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.THURSDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.FRIDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.SATURDAY));
      assertEquals(LocalDate.of(2001, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(3), data.getOccurrences());
      assertEquals(3, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("01/01/2001", "08/01/2001", "15/01/2001"), getExpandedDates(exception));

      exception = exceptions.get(5);
      assertEquals("Weekly 2 Tuesday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(2), data.getFrequency());
      assertFalse(data.getWeeklyDay(DayOfWeek.SUNDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.MONDAY));
      assertTrue(data.getWeeklyDay(DayOfWeek.TUESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.WEDNESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.THURSDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.FRIDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.SATURDAY));
      assertEquals(LocalDate.of(2001, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(4), data.getOccurrences());
      assertEquals(4, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("02/01/2001", "16/01/2001", "30/01/2001", "13/02/2001"), getExpandedDates(exception));

      exception = exceptions.get(6);
      assertEquals("Weekly 3 Wednesday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(3), data.getFrequency());
      assertFalse(data.getWeeklyDay(DayOfWeek.SUNDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.MONDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.TUESDAY));
      assertTrue(data.getWeeklyDay(DayOfWeek.WEDNESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.THURSDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.FRIDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.SATURDAY));
      assertEquals(LocalDate.of(2001, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(5), data.getOccurrences());
      assertEquals(5, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("03/01/2001", "24/01/2001", "14/02/2001", "07/03/2001", "28/03/2001"), getExpandedDates(exception));

      exception = exceptions.get(7);
      assertEquals("Weekly 4 Thursday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(4), data.getFrequency());
      assertFalse(data.getWeeklyDay(DayOfWeek.SUNDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.MONDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.TUESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.WEDNESDAY));
      assertTrue(data.getWeeklyDay(DayOfWeek.THURSDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.FRIDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.SATURDAY));
      assertEquals(LocalDate.of(2001, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(6), data.getOccurrences());
      assertEquals(6, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("04/01/2001", "01/02/2001", "01/03/2001", "29/03/2001", "26/04/2001", "24/05/2001"), getExpandedDates(exception));

      exception = exceptions.get(8);
      assertEquals("Weekly 5 Friday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertFalse(data.getWeeklyDay(DayOfWeek.SUNDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.MONDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.TUESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.WEDNESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.THURSDAY));
      assertTrue(data.getWeeklyDay(DayOfWeek.FRIDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.SATURDAY));
      assertEquals(LocalDate.of(2001, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(7), data.getOccurrences());
      assertEquals(7, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("05/01/2001", "09/02/2001", "16/03/2001", "20/04/2001", "25/05/2001", "29/06/2001", "03/08/2001"), getExpandedDates(exception));

      exception = exceptions.get(9);
      assertEquals("Weekly 6 Saturday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(6), data.getFrequency());
      assertFalse(data.getWeeklyDay(DayOfWeek.SUNDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.MONDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.TUESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.WEDNESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.THURSDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.FRIDAY));
      assertTrue(data.getWeeklyDay(DayOfWeek.SATURDAY));
      assertEquals(LocalDate.of(2001, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(8), data.getOccurrences());
      assertEquals(8, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("06/01/2001", "17/02/2001", "31/03/2001", "12/05/2001", "23/06/2001", "04/08/2001", "15/09/2001", "27/10/2001"), getExpandedDates(exception));

      exception = exceptions.get(10);
      assertEquals("Weekly 7 Sunday", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.WEEKLY, data.getRecurrenceType());
      assertEquals(Integer.valueOf(7), data.getFrequency());
      assertTrue(data.getWeeklyDay(DayOfWeek.SUNDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.MONDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.TUESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.WEDNESDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.THURSDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.FRIDAY));
      assertFalse(data.getWeeklyDay(DayOfWeek.SATURDAY));
      assertEquals(LocalDate.of(2001, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(9), data.getOccurrences());
      assertEquals(9, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("18/02/2001", "08/04/2001", "27/05/2001", "15/07/2001", "02/09/2001", "21/10/2001", "09/12/2001", "27/01/2002", "17/03/2002"), getExpandedDates(exception));

      exception = exceptions.get(11);
      assertEquals("Monthly Relative 6", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(DayOfWeek.SATURDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(7), data.getFrequency());
      assertEquals(LocalDate.of(2002, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(8), data.getOccurrences());
      assertEquals(8, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("05/01/2002", "03/08/2002", "01/03/2003", "04/10/2003", "01/05/2004", "04/12/2004", "02/07/2005", "04/02/2006"), getExpandedDates(exception));

      exception = exceptions.get(12);
      assertEquals("Monthly Relative 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(DayOfWeek.MONDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(2), data.getFrequency());
      assertEquals(LocalDate.of(2002, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(3), data.getOccurrences());
      assertEquals(3, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("07/01/2002", "04/03/2002", "06/05/2002"), getExpandedDates(exception));

      exception = exceptions.get(13);
      assertEquals("Monthly Relative 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(DayOfWeek.TUESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(3), data.getFrequency());
      assertEquals(LocalDate.of(2002, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(4), data.getOccurrences());
      assertEquals(4, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("08/01/2002", "09/04/2002", "09/07/2002", "08/10/2002"), getExpandedDates(exception));

      exception = exceptions.get(14);
      assertEquals("Monthly Relative 7", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(DayOfWeek.SUNDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(8), data.getFrequency());
      assertEquals(LocalDate.of(2002, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(9), data.getOccurrences());
      assertEquals(9, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("13/01/2002", "08/09/2002", "11/05/2003", "11/01/2004", "12/09/2004", "08/05/2005", "08/01/2006", "10/09/2006", "13/05/2007"), getExpandedDates(exception));

      exception = exceptions.get(15);
      assertEquals("Monthly Relative 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(3), data.getDayNumber());
      assertEquals(DayOfWeek.WEDNESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(4), data.getFrequency());
      assertEquals(LocalDate.of(2002, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(5), data.getOccurrences());
      assertEquals(5, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("16/01/2002", "15/05/2002", "18/09/2002", "15/01/2003", "21/05/2003"), getExpandedDates(exception));

      exception = exceptions.get(16);
      assertEquals("Monthly Relative 4", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(4), data.getDayNumber());
      assertEquals(DayOfWeek.THURSDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertEquals(LocalDate.of(2002, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(6), data.getOccurrences());
      assertEquals(6, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("24/01/2002", "27/06/2002", "28/11/2002", "24/04/2003", "25/09/2003", "26/02/2004"), getExpandedDates(exception));

      exception = exceptions.get(17);
      assertEquals("Monthly Relative 5", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(5), data.getDayNumber());
      assertEquals(DayOfWeek.FRIDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(6), data.getFrequency());
      assertEquals(LocalDate.of(2002, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(7), data.getOccurrences());
      assertEquals(7, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("25/01/2002", "26/07/2002", "31/01/2003", "25/07/2003", "30/01/2004", "30/07/2004", "28/01/2005"), getExpandedDates(exception));

      exception = exceptions.get(18);
      assertEquals("Monthly Absolute 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Integer.valueOf(2), data.getFrequency());
      assertEquals(LocalDate.of(2003, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(3), data.getOccurrences());
      assertEquals(3, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("01/01/2003", "01/03/2003", "01/05/2003"), getExpandedDates(exception));

      exception = exceptions.get(19);
      assertEquals("Monthly Absolute 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(4), data.getDayNumber());
      assertEquals(Integer.valueOf(5), data.getFrequency());
      assertEquals(LocalDate.of(2003, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(6), data.getOccurrences());
      assertEquals(6, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("04/01/2003", "04/06/2003", "04/11/2003", "04/04/2004", "04/09/2004", "04/02/2005"), getExpandedDates(exception));

      exception = exceptions.get(20);
      assertEquals("Yearly Relative 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(DayOfWeek.TUESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(3), data.getMonthNumber());
      assertEquals(LocalDate.of(2004, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(4), data.getOccurrences());
      assertEquals(4, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("02/03/2004", "01/03/2005", "07/03/2006", "06/03/2007"), getExpandedDates(exception));

      exception = exceptions.get(21);
      assertEquals("Yearly Relative 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(DayOfWeek.WEDNESDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(4), data.getMonthNumber());
      assertEquals(LocalDate.of(2004, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(5), data.getOccurrences());
      assertEquals(Arrays.asList("14/04/2004", "13/04/2005", "12/04/2006", "11/04/2007", "09/04/2008"), getExpandedDates(exception));

      exception = exceptions.get(22);
      assertEquals("Yearly Relative 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(3), data.getDayNumber());
      assertEquals(DayOfWeek.THURSDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(5), data.getMonthNumber());
      assertEquals(LocalDate.of(2004, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(6), data.getOccurrences());
      assertEquals(6, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("20/05/2004", "19/05/2005", "18/05/2006", "17/05/2007", "15/05/2008", "21/05/2009"), getExpandedDates(exception));

      exception = exceptions.get(23);
      assertEquals("Yearly Absolute 1", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(Integer.valueOf(2), data.getMonthNumber());
      assertEquals(LocalDate.of(2005, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(3), data.getOccurrences());
      assertEquals(3, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("01/02/2005", "01/02/2006", "01/02/2007"), getExpandedDates(exception));

      exception = exceptions.get(24);
      assertEquals("Yearly Absolute 2", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(2), data.getDayNumber());
      assertEquals(Integer.valueOf(3), data.getMonthNumber());
      assertEquals(LocalDate.of(2005, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(4), data.getOccurrences());
      assertEquals(4, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("02/03/2005", "02/03/2006", "02/03/2007", "02/03/2008"), getExpandedDates(exception));

      exception = exceptions.get(25);
      assertEquals("Yearly Absolute 3", exception.getName());
      assertFalse(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.YEARLY, data.getRecurrenceType());
      assertFalse(data.getRelative());
      assertEquals(Integer.valueOf(3), data.getDayNumber());
      assertEquals(Integer.valueOf(4), data.getMonthNumber());
      assertEquals(LocalDate.of(2005, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(5), data.getOccurrences());
      assertEquals(5, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("03/04/2005", "03/04/2006", "03/04/2007", "03/04/2008", "03/04/2009"), getExpandedDates(exception));

      exception = exceptions.get(26);
      assertEquals("Recurring Working", exception.getName());
      assertTrue(exception.getWorking());
      data = exception.getRecurring();
      assertEquals(RecurrenceType.MONTHLY, data.getRecurrenceType());
      assertTrue(data.getRelative());
      assertEquals(Integer.valueOf(1), data.getDayNumber());
      assertEquals(DayOfWeek.SATURDAY, data.getDayOfWeek());
      assertEquals(Integer.valueOf(1), data.getFrequency());
      assertEquals(LocalDate.of(2010, 1, 1), data.getStartDate());
      assertEquals(Integer.valueOf(3), data.getOccurrences());
      assertEquals(3, exception.getExpandedExceptions().size());
      assertEquals(Arrays.asList("02/01/2010", "06/02/2010", "06/03/2010"), getExpandedDates(exception));
   }

   private List<String> getExpandedDates(ProjectCalendarException exception)
   {
      return exception.getExpandedExceptions().stream().map(e -> m_localDateFormat.format(e.getFromDate())).collect(Collectors.toList());
   }

   private final DateTimeFormatter m_localDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
