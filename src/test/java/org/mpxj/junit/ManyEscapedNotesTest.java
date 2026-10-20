/*
 * file:       ManyEscapedNotesTest.java
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.mspdi.MSPDIReader;
import org.mpxj.mspdi.MSPDIWriter;
import org.mpxj.primavera.PrimaveraPMFileReader;
import org.mpxj.primavera.PrimaveraPMFileWriter;
import org.mpxj.reader.ProjectReader;
import org.mpxj.writer.ProjectWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mpxj.junit.MpxjAssert.assumeJvm;

/**
 * Tests that a file whose notes escape into many entity references reads back. Since Java 24 the
 * JDK's parser limits how many references to predefined entities such as &amp;lt; a document may
 * hold, however they are spread.
 */
public class ManyEscapedNotesTest
{
   /**
    * Check that we're not in IKVM.
    */
   @BeforeEach public void beforeMethod()
   {
      assumeJvm();
   }

   /**
    * Test a P6 XML file, which writes notes as HTML, escaped.
    */
   @Test public void primaveraXml() throws Exception
   {
      testRoundTrip(new PrimaveraPMFileWriter(), new PrimaveraPMFileReader());
   }

   /**
    * Test an MSPDI file, which writes notes as plain text, escaped.
    */
   @Test public void mspdi() throws Exception
   {
      testRoundTrip(new MSPDIWriter(), new MSPDIReader());
   }

   /**
    * Write tasks whose short notes together escape into more entity references than the JDK
    * allows by default, and check every note reads back.
    *
    * @param writer writer to use
    * @param reader reader to use
    */
   private void testRoundTrip(ProjectWriter writer, ProjectReader reader) throws Exception
   {
      // Two references per repeat, 60 per note, 120,000 across the file
      StringBuilder buffer = new StringBuilder();
      for (int index = 0; index < 30; index++)
      {
         buffer.append(" x < y & z");
      }
      String notes = buffer.toString().trim();

      ProjectFile file = new ProjectFile();
      file.getProjectProperties().setUniqueID(Integer.valueOf(1));
      file.setDefaultCalendar(file.addDefaultBaseCalendar());
      for (int index = 0; index < 2_000; index++)
      {
         Task task = file.addTask();
         task.setName("Task " + index);
         task.setNotes(notes);
      }

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      writer.write(file, stream);
      ProjectFile result = reader.read(new ByteArrayInputStream(stream.toByteArray()));

      // P6 XML notes read back headed by their notebook topic
      assertEquals(2_000, result.getTasks().stream().filter(t -> t.getNotes() != null && t.getNotes().endsWith(notes)).count());
   }
}
