/*
 * file:       PrimaveraXERFileWriterTest.java
 * author:     Jon Iles
 * date:       2026-11-24
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
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mpxj.ActivityType;
import org.mpxj.Duration;
import org.mpxj.HtmlNotes;
import org.mpxj.ParentNotes;
import org.mpxj.PercentCompleteType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.primavera.PrimaveraXERFileReader;
import org.mpxj.primavera.PrimaveraXERFileWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests exercising PrimaveraXERFileWriter.
 */
public class PrimaveraXERFileWriterTest
{
   /**
    * Ensure that line breaks in notes with variations on \r\n, \r, and \n
    * for line breaks are all handled correctly.
    */
   @Test public void testLineBreaksInNotes() throws Exception
   {
      String expected = "Notes\nLine one\n\nline two";
      roundtripNotes("<HTML><BODY>\r<P>Line one</P>\r<P>line two</P>\r</BODY></HTML>", expected);
      roundtripNotes("<HTML><BODY>\n<P>Line one</P>\n<P>line two</P>\n</BODY></HTML>", expected);
      roundtripNotes("<HTML><BODY>\r\n<P>Line one</P>\r\n<P>line two</P>\r\n</BODY></HTML>", expected);
      roundtripNotes("<HTML><BODY>\r\n<P>Line one</P>\n<P>line two</P>\r</BODY></HTML>", expected);
   }

   /**
    * Write and re-read an XER file to ensure that a note survives the round trip as expected.
    *
    * @param html note content as HTML
    * @param expected expected plain test read back from the XER file
    */
   private void roundtripNotes(String html, String expected) throws Exception
   {
      ProjectFile project = new ProjectFile();
      ProjectCalendar cal = project.addDefaultBaseCalendar();
      project.getProjectProperties().setDefaultCalendar(cal);
      project.getProjectProperties().setStartDate(LocalDateTime.of(2026, 1, 5, 8, 0));

      Integer uniqueID = Integer.valueOf(1);
      Duration duration = Duration.getInstance(40, TimeUnit.HOURS);

      Task task = project.addTask();
      task.setUniqueID(uniqueID);
      task.setName("A1000");
      task.setActivityID("A1000");
      task.setActivityType(ActivityType.TASK_DEPENDENT);
      task.setCalendar(cal);
      task.setPercentCompleteType(PercentCompleteType.DURATION);
      task.setDuration(duration);
      task.setRemainingDuration(duration);
      task.setPlannedDuration(duration);
      task.setNotesObject(new HtmlNotes(html));

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      new PrimaveraXERFileWriter().write(project, out);

      ProjectFile newProject = new PrimaveraXERFileReader().read(new ByteArrayInputStream(out.toByteArray()));
      Task newTask = newProject.getTaskByUniqueID(uniqueID);

      assertEquals(expected, newTask.getNotes());
   }
}
