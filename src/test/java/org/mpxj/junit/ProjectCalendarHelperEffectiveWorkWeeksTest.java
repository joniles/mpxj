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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mpxj.DayType;
import org.mpxj.LocalDateRange;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectFile;
import org.mpxj.TimeUnit;
import org.mpxj.common.ProjectCalendarHelper;
import org.mpxj.mspdi.MSPDIReader;
import org.mpxj.mspdi.MSPDIWriter;
import org.mpxj.mspdi.SaveVersion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regression tests for effective work weeks in a parentless temporary calendar. */
public class ProjectCalendarHelperEffectiveWorkWeeksTest
{
   @Test public void testLocalDefaultUsesParentOrdinaryHours()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar parent = file.addDefaultBaseCalendar();
      week(parent, 2, 8, 2);
      ProjectCalendar child = child(file, parent);
      ProjectCalendarWeek local = week(child, 2, 8, 1);
      local.setCalendarDayType(DayOfWeek.MONDAY, DayType.DEFAULT);
      // DEFAULT is resolved against the parent's ordinary day, even with a local override.
      child.setCalendarDayType(DayOfWeek.MONDAY, DayType.WORKING);
      child.addCalendarHours(DayOfWeek.MONDAY).add(range(5));
      assertEquals(8.0, child.getWork(date(2), TimeUnit.HOURS).getDuration());
      ProjectCalendar flat = ProjectCalendarHelper.createTemporaryFlattenedCalendar(child);
      assertEquals(parent.getHours(DayOfWeek.MONDAY), flat.getHours(date(2)));
      assertEquivalent(child, flat, date(2), date(8));
   }

   @Test public void testInheritedDefaultUsesImmediateParentOrdinaryHours()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar base = file.addDefaultBaseCalendar();
      ProjectCalendarWeek inherited = week(base, 2, 8, 2);
      inherited.setCalendarDayType(DayOfWeek.MONDAY, DayType.DEFAULT);
      ProjectCalendar middle = child(file, base);
      middle.setCalendarDayType(DayOfWeek.MONDAY, DayType.WORKING);
      middle.addCalendarHours(DayOfWeek.MONDAY).add(range(3));
      ProjectCalendar leaf = child(file, middle);
      assertEquals(Collections.singletonList(range(3)), leaf.getHours(date(2)));
      assertEquivalent(leaf, ProjectCalendarHelper.createTemporaryFlattenedCalendar(leaf), date(1), date(20));
   }

   @Test public void testParentWeekIsSplitAroundLocalWeek()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar parent = file.addDefaultBaseCalendar();
      week(parent, 2, 22, 2);
      ProjectCalendar child = child(file, parent);
      week(child, 9, 15, 3);
      ProjectCalendar flat = ProjectCalendarHelper.createTemporaryFlattenedCalendar(child);
      assertEquals(3, flat.getWorkWeeks().size());
      assertEquals(new LocalDateRange(date(2), date(8)), flat.getWorkWeeks().get(0).getDateRange());
      assertEquals(new LocalDateRange(date(9), date(15)), flat.getWorkWeeks().get(1).getDateRange());
      assertEquals(new LocalDateRange(date(16), date(22)), flat.getWorkWeeks().get(2).getDateRange());
      assertEquivalent(child, flat, date(1), date(30));
   }

   @Test public void testNativeSelectionWithOverlapsAndGaps()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar base = file.addDefaultBaseCalendar();
      week(base, 1, 27, 1);
      week(base, 8, 12, 2);
      week(base, 8, 20, 3);
      week(base, 24, 25, 4);
      ProjectCalendar middle = child(file, base);
      week(middle, 3, 4, 5);
      week(middle, 11, 11, 6);
      ProjectCalendar leaf = child(file, middle);
      week(leaf, 6, 15, 7);
      week(leaf, 13, 14, 8);
      // The native binary search (including any missed overlapping week) is the oracle.
      assertEquivalent(leaf, ProjectCalendarHelper.createTemporaryFlattenedCalendar(leaf), date(1).minusDays(14), date(30).plusDays(14));
   }

   @Test public void testExceptionsDoNotVetoWorkWeeks()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar parent = file.addDefaultBaseCalendar();
      week(parent, 2, 22, 2);
      parent.addCalendarException(date(2));
      parent.addCalendarException(date(16));
      ProjectCalendar child = child(file, parent);
      child.addCalendarException(date(9)).add(range(1));
      ProjectCalendar flat = ProjectCalendarHelper.createTemporaryFlattenedCalendar(child);
      assertEquals(1, flat.getWorkWeeks().size());
      assertEquals(3, flat.getCalendarExceptions().size());
      assertEquivalent(child, flat, date(1), date(30));
   }

   @Test public void testFullLocalDateRange()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar base = file.addDefaultBaseCalendar();
      ProjectCalendarWeek week = week(base, 2, 8, 2);
      week.setDateRange(new LocalDateRange(LocalDate.MIN, LocalDate.MAX));
      ProjectCalendar child = child(file, base);
      ProjectCalendar flat = ProjectCalendarHelper.createTemporaryFlattenedCalendar(child);
      assertEquals(week.getDateRange(), flat.getWorkWeeks().get(0).getDateRange());
      for (LocalDate date : Arrays.asList(LocalDate.MIN, date(2), LocalDate.MAX))
      {
         assertEquals(child.getHours(date), flat.getHours(date));
      }
   }

   @Test public void testInvalidPeriodsAreReportedRatherThanDropped()
   {
      for (LocalDateRange period : Arrays.asList(null, new LocalDateRange(null, date(8)), new LocalDateRange(date(2), null), new LocalDateRange(date(8), date(2))))
      {
         ProjectFile file = new ProjectFile();
         ProjectCalendar base = file.addDefaultBaseCalendar();
         week(base, 2, 8, 2).setDateRange(period);
         IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () -> ProjectCalendarHelper.createTemporaryFlattenedCalendar(child(file, base)));
         assertTrue(failure.getMessage().contains("work week"));
      }
   }

   @Test public void testMspdiRoundTripOfSplitWeeksAndDefault() throws Exception
   {
      for (SaveVersion version : Arrays.asList(SaveVersion.Project2003, SaveVersion.Project2016))
      {
         ProjectFile file = new ProjectFile();
         ProjectCalendar base = file.addDefaultBaseCalendar();
         week(base, 2, 22, 2);
         ProjectCalendar child = child(file, base);
         child.setName("Effective child");
         week(child, 9, 15, 3).setCalendarDayType(DayOfWeek.MONDAY, DayType.DEFAULT);
         base.addCalendarException(date(16));
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         MSPDIWriter writer = new MSPDIWriter();
         writer.setSaveVersion(version);
         writer.write(file, output);
         ProjectFile read = new MSPDIReader().read(new ByteArrayInputStream(output.toByteArray()));
         ProjectCalendar after = read.getCalendarByName("Effective child");
         assertNotNull(after);
         assertNull(after.getParent());
         assertEquivalent(child, after, date(1), date(30));
      }
   }

   private static ProjectCalendar child(ProjectFile file, ProjectCalendar parent)
   {
      ProjectCalendar calendar = file.addDefaultDerivedCalendar();
      calendar.setParent(parent);
      return calendar;
   }

   private static ProjectCalendarWeek week(ProjectCalendar calendar, int start, int end, int hours)
   {
      ProjectCalendarWeek week = calendar.addWorkWeek();
      week.setName("Week " + start + " " + end);
      week.setDateRange(new LocalDateRange(date(start), date(end)));
      for (DayOfWeek day : DayOfWeek.values())
      {
         week.setCalendarDayType(day, DayType.WORKING);
         week.addCalendarHours(day).add(range(hours));
      }
      return week;
   }

   private static LocalDate date(int day)
   {
      return LocalDate.of(2026, 11, day);
   }

   private static LocalTimeRange range(int hours)
   {
      return new LocalTimeRange(LocalTime.of(8, 0), LocalTime.of(8 + hours, 0));
   }

   private static void assertEquivalent(ProjectCalendar source, ProjectCalendar target, LocalDate start, LocalDate end)
   {
      for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1))
      {
         assertEquals(source.getHours(date), target.getHours(date), "Ranges " + date);
         assertEquals(source.getWork(date, TimeUnit.HOURS).getDuration(), target.getWork(date, TimeUnit.HOURS).getDuration(), "Work " + date);
      }
   }
}
