/*
 * file:       WriterEdgeCaseTest.java
 * author:     Petr Janeček
 * date:       2026-10-17
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

package org.mpxj.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeValue;
import org.mpxj.CalendarType;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.json.JsonWriter;
import org.mpxj.mspdi.MSPDIReader;
import org.mpxj.mspdi.MSPDIWriter;
import org.mpxj.primavera.PrimaveraPMFileReader;
import org.mpxj.primavera.PrimaveraPMFileWriter;
import org.mpxj.primavera.PrimaveraXERFileReader;
import org.mpxj.primavera.PrimaveraXERFileWriter;
import org.mpxj.reader.ProjectReader;
import org.mpxj.writer.ProjectWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mpxj.junit.MpxjAssert.assumeJvm;

/**
 * Tests that writers cope with values that files from other tools leave unset.
 */
public class WriterEdgeCaseTest
{
   /**
    * Check that we're not in IKVM.
    */
   @BeforeEach public void beforeMethod()
   {
      assumeJvm();
   }

   /**
    * Test that activity code values which state no sequence number can be written to P6 XML.
    */
   @Test public void activityCodeValuesWithoutSequenceNumber() throws Exception
   {
      ProjectFile file = createProject();
      ActivityCode code = new ActivityCode.Builder(file).name("Phase").sequenceNumber(Integer.valueOf(1)).build();
      code.addValue(new ActivityCodeValue.Builder(file).activityCode(code).name("A").sequenceNumber(Integer.valueOf(1)).build());
      code.addValue(new ActivityCodeValue.Builder(file).activityCode(code).name("B").build());
      code.addValue(new ActivityCodeValue.Builder(file).activityCode(code).name("C").build());
      file.getActivityCodes().add(code);

      ProjectFile result = writeAndRead(file, new PrimaveraPMFileWriter(), new PrimaveraPMFileReader());

      ActivityCode resultCode = result.getActivityCodes().stream().filter(c -> "Phase".equals(c.getName())).findFirst().orElse(null);
      assertNotNull(resultCode);
      Map<String, Integer> sequenceNumbers = new HashMap<>();
      resultCode.getValues().forEach(v -> sequenceNumbers.put(v.getName(), v.getSequenceNumber()));
      Map<String, Integer> expected = new HashMap<>();
      expected.put("A", Integer.valueOf(1));
      expected.put("B", null);
      expected.put("C", null);
      assertEquals(expected, sequenceNumbers);
   }

   /**
    * Test that a project calendar which names no project belongs to the project, and that writers
    * keep it for the tasks that use it. P6 files can hold such calendars.
    */
   @Test public void projectCalendarWithoutProject() throws Exception
   {
      ProjectFile file = createProject();
      ProjectCalendar global = file.getDefaultCalendar();
      ProjectCalendar own = addProjectCalendar(file, "Own", file.getProjectProperties().getUniqueID());
      addProjectCalendar(file, "Other", Integer.valueOf(99));
      ProjectCalendar none = addProjectCalendar(file, "None", null);
      assertEquals(Arrays.asList(global, own, none), file.getCalendarsForProject());

      Task task = file.addTask();
      task.setName("Task");
      task.setCalendar(none);

      assertEquals("None", writeAndRead(file, new PrimaveraPMFileWriter(), new PrimaveraPMFileReader()).getTaskByUniqueID(task.getUniqueID()).getCalendar().getName());
      assertEquals("None", writeAndRead(file, new PrimaveraXERFileWriter(), new PrimaveraXERFileReader()).getTaskByUniqueID(task.getUniqueID()).getCalendar().getName());
      assertEquals("None", writeAndRead(file, new MSPDIWriter(), new MSPDIReader()).getTaskByUniqueID(task.getUniqueID()).getCalendar().getName());
      write(file, new JsonWriter());
   }

   /**
    * Test that calendar days which defer to a base calendar the calendar doesn't have can be
    * written to P6 formats, as days without working time. MSPDI files can hold such calendars.
    */
   @Test public void calendarDaysWithoutHours() throws Exception
   {
      // Every day but Monday defers to a base calendar, which is never set. Monday has hours of
      // its own because P6 readers give a week with no working time at all P6's default hours.
      ProjectFile file = createProject();
      ProjectCalendar calendar = file.addDefaultDerivedCalendar();
      calendar.setName("Monday");
      calendar.setWorkingDay(DayOfWeek.MONDAY, true);
      calendar.addCalendarHours(DayOfWeek.MONDAY).add(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)));

      assertMondayOnly(writeAndRead(file, new PrimaveraPMFileWriter(), new PrimaveraPMFileReader()).getCalendarByName("Monday"));
      assertMondayOnly(writeAndRead(file, new PrimaveraXERFileWriter(), new PrimaveraXERFileReader()).getCalendarByName("Monday"));
   }

   /**
    * Create a project with a unique ID and a default calendar, as the writers expect.
    *
    * @return new project
    */
   private ProjectFile createProject()
   {
      ProjectFile file = new ProjectFile();
      file.getProjectProperties().setUniqueID(Integer.valueOf(1));
      file.setDefaultCalendar(file.addDefaultBaseCalendar());
      return file;
   }

   /**
    * Check that a calendar works Mondays only, from 09:00 to 17:00.
    *
    * @param calendar calendar to check
    */
   private void assertMondayOnly(ProjectCalendar calendar)
   {
      assertNotNull(calendar);
      for (DayOfWeek day : DayOfWeek.values())
      {
         assertEquals(Boolean.valueOf(day == DayOfWeek.MONDAY), Boolean.valueOf(calendar.isWorkingDay(day)), day.toString());
      }
      assertEquals(Collections.singletonList(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0))), new ArrayList<>(calendar.getHours(DayOfWeek.MONDAY)));
   }

   /**
    * Add a project calendar.
    *
    * @param file parent project
    * @param name calendar name
    * @param projectUniqueID unique ID of the project the calendar names, or null
    * @return new calendar
    */
   private ProjectCalendar addProjectCalendar(ProjectFile file, String name, Integer projectUniqueID)
   {
      ProjectCalendar calendar = file.addDefaultDerivedCalendar();
      calendar.setName(name);
      calendar.setParent(file.getDefaultCalendar());
      calendar.setType(CalendarType.PROJECT);
      calendar.setProjectUniqueID(projectUniqueID);
      return calendar;
   }

   /**
    * Write a project.
    *
    * @param file project to write
    * @param writer writer to use
    * @return written bytes
    */
   private byte[] write(ProjectFile file, ProjectWriter writer) throws Exception
   {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      writer.write(file, stream);
      return stream.toByteArray();
   }

   /**
    * Write a project and read it back.
    *
    * @param file project to write
    * @param writer writer to use
    * @param reader reader to use
    * @return project read back
    */
   private ProjectFile writeAndRead(ProjectFile file, ProjectWriter writer, ProjectReader reader) throws Exception
   {
      return reader.read(new ByteArrayInputStream(write(file, writer)));
   }
}
