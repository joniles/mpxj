/*
 * file:       ProjectValueListsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28/04/2015
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

package org.mpxj.junit.project;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.CustomField;
import org.mpxj.CustomFieldContainer;
import org.mpxj.CustomFieldLookupTable;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.TaskField;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure project custom field value lists are correctly handled.
 */
public class ProjectValueListsTest
{
   /**
    * Test to validate the custom value lists in files saved by different versions of MS Project.
    */
   @Test public void testProjectValueLists() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/project-valuelists", "project-valuelists"))
      {
         testProjectValueLists(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testProjectValueLists(File file) throws MPXJException
   {
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      ProjectFile project = new UniversalProjectReader().read(file);
      CustomFieldContainer container = project.getCustomFields();

      CustomField config = container.get(TaskField.COST1);
      CustomFieldLookupTable table = config.getLookupTable();
      assertEquals(3, table.size());
      assertEquals(1, ((Number) table.get(0).getValue()).intValue());
      assertEquals("Description 1", table.get(0).getDescription());
      assertEquals(2, ((Number) table.get(1).getValue()).intValue());
      assertEquals("Description 2", table.get(1).getDescription());
      assertEquals(3, ((Number) table.get(2).getValue()).intValue());
      assertEquals("Description 3", table.get(2).getDescription());

      config = container.get(TaskField.DATE1);
      table = config.getLookupTable();
      assertEquals(3, table.size());
      assertEquals("01/01/2015", df.format((LocalDateTime) table.get(0).getValue()));
      assertEquals("Description 1", table.get(0).getDescription());
      assertEquals("02/01/2015", df.format((LocalDateTime) table.get(1).getValue()));
      assertEquals("Description 2", table.get(1).getDescription());
      assertEquals("03/01/2015", df.format((LocalDateTime) table.get(2).getValue()));
      assertEquals("Description 3", table.get(2).getDescription());

      config = container.get(TaskField.DURATION1);
      table = config.getLookupTable();
      assertEquals(3, table.size());
      assertEquals("1.0d", table.get(0).getValue().toString());
      assertEquals("Description 1", table.get(0).getDescription());
      assertEquals("2.0d", table.get(1).getValue().toString());
      assertEquals("Description 2", table.get(1).getDescription());
      assertEquals("3.0d", table.get(2).getValue().toString());
      assertEquals("Description 3", table.get(2).getDescription());

      config = container.get(TaskField.NUMBER1);
      table = config.getLookupTable();
      assertEquals(3, table.size());
      assertEquals(1, ((Number) table.get(0).getValue()).intValue());
      assertEquals("Description 1", table.get(0).getDescription());
      assertEquals(2, ((Number) table.get(1).getValue()).intValue());
      assertEquals("Description 2", table.get(1).getDescription());
      assertEquals(3, ((Number) table.get(2).getValue()).intValue());
      assertEquals("Description 3", table.get(2).getDescription());

      config = container.get(TaskField.TEXT1);
      table = config.getLookupTable();
      assertEquals(3, table.size());
      assertEquals("Value 1", table.get(0).getValue());
      assertEquals("Description 1", table.get(0).getDescription());
      assertEquals("Value 2", table.get(1).getValue());
      assertEquals("Description 2", table.get(1).getDescription());
      assertEquals("Value 3", table.get(2).getValue());
      assertEquals("Description 3", table.get(2).getDescription());
   }
}
