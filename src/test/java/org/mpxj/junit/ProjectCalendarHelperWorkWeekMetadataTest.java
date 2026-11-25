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

package org.mpxj.junit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.DayType;
import org.mpxj.LocalDateRange;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectFile;
import org.mpxj.common.ProjectCalendarHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests preservation of a local work week's metadata when flattening a derived calendar.
 */
public class ProjectCalendarHelperWorkWeekMetadataTest
{
   /**
    * The source week is complete and can be expanded before flattening.
    */
   @Test public void testSourceWorkWeek()
   {
      ProjectCalendar calendar = createCalendar();
      assertTrue(calendar.isDerived());
      assertTrue(calendar.getParent().getWorkWeeks().isEmpty());
      assertEquals(1, calendar.getWorkWeeks().size());
      ProjectCalendarWeek week = calendar.getWorkWeeks().get(0);
      assertEquals(WEEK_NAME, week.getName());
      assertEquals(WEEK_DATES, week.getDateRange());
      for (DayOfWeek day : DayOfWeek.values())
      {
         assertEquals(day == DayOfWeek.MONDAY ? DayType.WORKING : DayType.NON_WORKING, week.getCalendarDayType(day));
         assertEquals(day == DayOfWeek.MONDAY ? Collections.singletonList(MONDAY_HOURS) : Collections.emptyList(), week.getCalendarHours(day));
      }
      assertExpandedWeek(calendar.getExpandedCalendarExceptionsWithWorkWeeks());
   }

   /**
    * Flattening should retain the local work week's name.
    */
   @Test public void testFlattenedWorkWeekName()
   {
      ProjectCalendar flattened = flattenCalendar(createCalendar());
      assertEquals(WEEK_NAME, flattened.getWorkWeeks().get(0).getName());
   }

   /**
    * Flattening should retain the local work week's date range.
    */
   @Test public void testFlattenedWorkWeekDateRange()
   {
      ProjectCalendar flattened = flattenCalendar(createCalendar());
      assertEquals(WEEK_DATES, flattened.getWorkWeeks().get(0).getDateRange());
   }

   /**
    * Flattening should retain Monday's explicit type and hours.
    */
   @Test public void testFlattenedWorkWeekMonday()
   {
      ProjectCalendar flattened = flattenCalendar(createCalendar());
      ProjectCalendarWeek week = flattened.getWorkWeeks().get(0);
      assertEquals(DayType.WORKING, week.getCalendarDayType(DayOfWeek.MONDAY));
      assertEquals(Collections.singletonList(MONDAY_HOURS), week.getCalendarHours(DayOfWeek.MONDAY));
   }

   /**
    * The flattened week should still expand into dated calendar exceptions.
    */
   @Test public void testFlattenedWorkWeekExpansion()
   {
      ProjectCalendar flattened = flattenCalendar(createCalendar());
      assertExpandedWeek(flattened.getExpandedCalendarExceptionsWithWorkWeeks());
   }

   /**
    * Create a base calendar and a derived calendar with one local work week.
    * All seven week days are explicit to keep inherited work weeks and DEFAULT outside this test.
    *
    * @return derived calendar
    */
   private ProjectCalendar createCalendar()
   {
      ProjectFile project = new ProjectFile();
      ProjectCalendar base = project.addDefaultBaseCalendar();
      ProjectCalendar derived = project.addDefaultDerivedCalendar();
      derived.setParent(base);

      ProjectCalendarWeek week = derived.addWorkWeek();
      week.setName(WEEK_NAME);
      week.setDateRange(WEEK_DATES);
      for (DayOfWeek day : DayOfWeek.values())
      {
         week.setCalendarDayType(day, DayType.NON_WORKING);
      }
      week.setCalendarDayType(DayOfWeek.MONDAY, DayType.WORKING);
      week.getCalendarHours(DayOfWeek.MONDAY).add(MONDAY_HOURS);
      return derived;
   }

   /**
    * Verify that the helper actually creates a flattened calendar with one copied week.
    *
    * @param source derived source calendar
    * @return flattened calendar
    */
   private ProjectCalendar flattenCalendar(ProjectCalendar source)
   {
      ProjectCalendar flattened = ProjectCalendarHelper.createTemporaryFlattenedCalendar(source);
      assertNotSame(source, flattened);
      assertNull(flattened.getParent());
      assertEquals(1, flattened.getWorkWeeks().size());
      assertNotSame(source.getWorkWeeks().get(0), flattened.getWorkWeeks().get(0));
      return flattened;
   }

   /**
    * Verify the seven dated exceptions and Monday's hours.
    *
    * @param exceptions expanded exceptions
    */
   private void assertExpandedWeek(List<ProjectCalendarException> exceptions)
   {
      assertEquals(7, exceptions.size());
      for (int index = 0; index < 7; index++)
      {
         ProjectCalendarException exception = exceptions.get(index);
         LocalDate date = WEEK_DATES.getStart().plusDays(index);
         assertEquals(date, exception.getFromDate());
         assertEquals(date, exception.getToDate());
         assertEquals(index == 0 ? Collections.singletonList(MONDAY_HOURS) : Collections.emptyList(), exception);
      }
   }

   private static final String WEEK_NAME = "Local work week";
   private static final LocalDateRange WEEK_DATES = new LocalDateRange(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 8));
   private static final LocalTimeRange MONDAY_HOURS = new LocalTimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0));
}
