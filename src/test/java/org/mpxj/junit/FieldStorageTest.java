/*
 * file:       FieldStorageTest.java
 * author:     Petr Janeček
 * date:       2026-10-06
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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.AssignmentField;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.UserDefinedField;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests how entities store their field values: each kind's own fields in slots shared across a ProjectContext, any
 * other field beside them.
 */
public class FieldStorageTest
{
   /**
    * A value set is read back, a value never set reads as null, and a null removes a value.
    */
   @Test public void testSetGetAndRemove()
   {
      ProjectFile file = new ProjectFile();
      Task task = file.addTask();
      task.set(TaskField.TEXT1, "Pour slab");
      task.set(TaskField.DURATION, Duration.getInstance(5, TimeUnit.DAYS));

      assertEquals("Pour slab", task.getCachedValue(TaskField.TEXT1));
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getCachedValue(TaskField.DURATION));
      assertNull(task.getCachedValue(TaskField.TEXT2));
      assertNull(task.getCachedValue(null));

      task.set(TaskField.TEXT1, null);
      assertNull(task.getCachedValue(TaskField.TEXT1));
      task.set(TaskField.TEXT30, null); // never set, nothing to remove
      assertNull(task.getCachedValue(TaskField.TEXT30));
      assertEquals(Duration.getInstance(5, TimeUnit.DAYS), task.getCachedValue(TaskField.DURATION));
   }

   /**
    * A change is reported with the value it replaced, and setting the value already held reports nothing.
    */
   @Test public void testChangeEventsCarryTheReplacedValue()
   {
      ProjectFile file = new ProjectFile();
      Task task = file.addTask();
      List<String> events = new ArrayList<>();
      task.addFieldListener((container, type, oldValue, newValue) -> events.add(type + ": " + oldValue + " -> " + newValue));

      task.set(TaskField.TEXT1, "a");
      task.set(TaskField.TEXT1, "a");
      task.set(TaskField.TEXT1, "b");
      task.set(TaskField.TEXT1, null);

      assertEquals("[Text1: null -> a, Text1: a -> b, Text1: b -> null]", events.toString());
   }

   /**
    * Tasks of one file share where a field is kept, not its value: a task set after its siblings have taken more
    * fields reads only its own, and theirs are unchanged by it.
    */
   @Test public void testSiblingsKeepTheirOwnValues()
   {
      ProjectFile file = new ProjectFile();
      Task first = file.addTask();
      first.set(TaskField.TEXT1, "first");
      Task second = file.addTask();
      for (int index = 1; index <= 30; index++)
      {
         second.setText(index, "second " + index);
      }
      Task third = file.addTask();
      third.set(TaskField.TEXT30, "third");

      assertEquals("first", first.getCachedValue(TaskField.TEXT1));
      assertNull(first.getCachedValue(TaskField.TEXT30));
      assertEquals("second 30", second.getCachedValue(TaskField.TEXT30));
      assertEquals("third", third.getCachedValue(TaskField.TEXT30));
      assertNull(third.getCachedValue(TaskField.TEXT1));
   }

   /**
    * Two files keep their fields apart, whatever order each sets them in.
    */
   @Test public void testFilesAreIndependent()
   {
      ProjectFile one = new ProjectFile();
      Task a = one.addTask();
      a.set(TaskField.TEXT1, "one");
      a.set(TaskField.TEXT2, "two");

      ProjectFile other = new ProjectFile();
      Task b = other.addTask();
      b.set(TaskField.TEXT2, "two");
      b.set(TaskField.TEXT1, "one");

      assertEquals("one", a.getCachedValue(TaskField.TEXT1));
      assertEquals("two", a.getCachedValue(TaskField.TEXT2));
      assertEquals("one", b.getCachedValue(TaskField.TEXT1));
      assertEquals("two", b.getCachedValue(TaskField.TEXT2));
   }

   /**
    * User defined fields, and fields of another kind's enum, are stored beside a task's own fields: set, read,
    * replaced and removed one at a time without disturbing the others.
    */
   @Test public void testOtherFieldsBesideTheOwnFields()
   {
      ProjectFile file = new ProjectFile();
      UserDefinedField first = udf(file, "First");
      UserDefinedField second = udf(file, "Second");
      UserDefinedField third = udf(file, "Third");
      Task task = file.addTask();
      task.set(TaskField.TEXT1, "own");
      task.set(first, "1");
      task.set(second, "2");
      task.set(third, "3");
      task.set(ResourceField.TEXT1, "foreign");

      task.set(second, null);
      task.set(third, "three");

      assertEquals("1", task.getCachedValue(first));
      assertNull(task.getCachedValue(second));
      assertEquals("three", task.getCachedValue(third));
      assertEquals("foreign", task.getCachedValue(ResourceField.TEXT1));
      assertEquals("own", task.getCachedValue(TaskField.TEXT1));
      assertNull(file.addTask().getCachedValue(first));
   }

   /**
    * Resources, resource assignments and the project properties store their fields the same way.
    */
   @Test public void testOtherEntities()
   {
      ProjectFile file = new ProjectFile();
      Resource resource = file.addResource();
      resource.set(ResourceField.TEXT1, "crew");
      Task task = file.addTask();
      ResourceAssignment assignment = task.addResourceAssignment(resource);
      assignment.set(AssignmentField.TEXT1, "assignment");
      file.getProjectProperties().set(ProjectField.PROJECT_TITLE, "Project");

      assertEquals("crew", resource.getCachedValue(ResourceField.TEXT1));
      assertEquals("assignment", assignment.getCachedValue(AssignmentField.TEXT1));
      assertEquals("Project", file.getProjectProperties().getCachedValue(ProjectField.PROJECT_TITLE));
      assertNull(task.getCachedValue(TaskField.TEXT1));
   }

   private static UserDefinedField udf(ProjectFile file, String name)
   {
      UserDefinedField field = new UserDefinedField.Builder(file).externalName(name).fieldTypeClass(FieldTypeClass.TASK).dataType(DataType.STRING).build();
      file.getUserDefinedFields().add(field);
      return field;
   }
}
