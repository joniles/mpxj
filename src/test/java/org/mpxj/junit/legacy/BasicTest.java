/*
 * file:       BasicTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2006
 * date:       15/08/2005
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

package org.mpxj.junit.legacy;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import org.mpxj.CustomFieldContainer;
import org.mpxj.Duration;
import org.mpxj.MPXJException;
import org.mpxj.Priority;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Table;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.View;
import org.mpxj.common.NumberHelper;
import org.mpxj.junit.FileUtility;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpx.MPXReader;
import org.mpxj.mpx.MPXWriter;
import org.mpxj.mspdi.MSPDIReader;
import org.mpxj.mspdi.MSPDIWriter;
import org.mpxj.planner.PlannerWriter;

/**
 * This class contains a small set of tests to exercise the MPXJ library.
 */
public class BasicTest
{
   /**
    * This method performs a simple data driven test to read then write
    * the contents of a single MPX file. Assuming the MPX file contains
    * at least one example of each type of record, this test will be able
    * to exercise a large part of the MPX library.
    */
   @Test public void testRewrite1() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample.mpx"));
      ProjectFile mpx = new MPXReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      MPXWriter writer = new MPXWriter();
      writer.setUseLocaleDefaults(false);
      writer.write(mpx, out);
      boolean success = FileUtility.equals(in, out);
      assertTrue("Files are not identical", success);
      out.deleteOnExit();
   }

   /**
    * This method performs a simple data driven test to read then write
    * the contents of a single MPX file. Assuming the MPX file contains
    * at least one example of each type of record, this test will be able
    * to exercise a large part of the MPX library.
    */
   @Test public void testRewrite2() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample1.xml"));
      ProjectFile xml = new MSPDIReader().read(in);
      File out = Files.createTempFile("junit", ".xml").toFile();
      new MSPDIWriter().write(xml, out);
      boolean success = FileUtility.equals(in, out);
      assertTrue("Files are not identical", success);
      out.deleteOnExit();
   }

   /**
    * This method performs a simple data driven test to read then write
    * the contents of a single MPX file. The difference between this test
    * and testRewrite1 is that the sample MPX file uses alternative
    * field separators, decimal separators and thousands separators.
    */
   @Test public void testRewrite3() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample1.mpx"));
      ProjectFile mpx = new MPXReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      MPXWriter writer = new MPXWriter();
      writer.setUseLocaleDefaults(false);
      writer.write(mpx, out);
      boolean success = FileUtility.equals(in, out);
      assertTrue("Files are not identical", success);
      out.deleteOnExit();
   }

   /**
    * Test to ensure that files without tasks or resources generate
    * correct MPX files.
    */
   @Test public void testRewrite4() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/empty.mpp"));
      ProjectFile mpx = new MPPReader().read(in);
      mpx.getProjectProperties().setCurrentDate(LocalDateTime.of(2006, 3, 1, 0, 0));
      File out = Files.createTempFile("junit", ".mpx").toFile();
      MPXWriter writer = new MPXWriter();
      writer.setUseLocaleDefaults(false);
      writer.write(mpx, out);
      boolean success = FileUtility.equals(new File(MpxjTestData.filePath("empty.mpx")), out);
      assertTrue("Files are not identical", success);
      out.deleteOnExit();
   }

   /**
    * Exercise PlannerWriter.
    */
   @Test public void testRewrite5() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample.mpx"));
      ProjectFile mpx = new MPXReader().read(in);
      File out = Files.createTempFile("junit", ".planner").toFile();
      new PlannerWriter().write(mpx, out);
      //success = FileUtility.equals (in, out);
      //assertTrue ("Files are not identical", success);
      out.deleteOnExit();
   }

   /**
    * This test exercises the automatic generation of WBS and outline levels.
    */
   @Test public void testAutomaticGeneration()
   {
      ProjectFile file = new ProjectFile();

      Resource resource1 = file.addResource();
      resource1.setName("R1");
      assertEquals(1, resource1.getUniqueID().intValue());
      assertEquals(1, resource1.getID().intValue());

      Resource resource2 = file.addResource();
      resource2.setName("R2");
      assertEquals(2, resource2.getUniqueID().intValue());
      assertEquals(2, resource2.getID().intValue());

      Task task1 = file.addTask();
      task1.setName("1.0");
      assertEquals("1", task1.getWBS());
      assertEquals(1, task1.getOutlineLevel().intValue());
      assertEquals("1", task1.getOutlineNumber());
      assertEquals(1, task1.getID().intValue());
      assertEquals(1, task1.getUniqueID().intValue());
      assertFalse(task1.getSummary());

      task1 = file.addTask();
      task1.setName("2.0");
      assertEquals("2", task1.getWBS());
      assertEquals(1, task1.getOutlineLevel().intValue());
      assertEquals("2", task1.getOutlineNumber());
      assertEquals(2, task1.getID().intValue());
      assertEquals(2, task1.getUniqueID().intValue());
      assertFalse(task1.getSummary());

      task1 = file.addTask();
      task1.setName("3.0");
      assertEquals("3", task1.getWBS());
      assertEquals(1, task1.getOutlineLevel().intValue());
      assertEquals("3", task1.getOutlineNumber());
      assertEquals(3, task1.getID().intValue());
      assertEquals(3, task1.getUniqueID().intValue());
      assertFalse(task1.getSummary());

      Task task2 = task1.addTask();
      task2.setName("3.1");
      assertEquals("3.1", task2.getWBS());
      assertEquals(2, task2.getOutlineLevel().intValue());
      assertEquals("3.1", task2.getOutlineNumber());
      assertEquals(4, task2.getID().intValue());
      assertEquals(4, task2.getUniqueID().intValue());
      assertTrue(task1.getSummary());
      assertFalse(task2.getSummary());

      task2 = task1.addTask();
      task2.setName("3.2");
      assertEquals("3.2", task2.getWBS());
      assertEquals(2, task2.getOutlineLevel().intValue());
      assertEquals("3.2", task2.getOutlineNumber());
      assertEquals(5, task2.getID().intValue());
      assertEquals(5, task2.getUniqueID().intValue());
      assertTrue(task1.getSummary());
      assertFalse(task2.getSummary());

      Task task3 = task2.addTask();
      task3.setName("3.2.1");
      assertEquals("3.2.1", task3.getWBS());
      assertEquals(3, task3.getOutlineLevel().intValue());
      assertEquals("3.2.1", task3.getOutlineNumber());
      assertEquals(6, task3.getID().intValue());
      assertEquals(6, task3.getUniqueID().intValue());
      assertTrue(task1.getSummary());
      assertTrue(task2.getSummary());
      assertFalse(task3.getSummary());

      task3 = task2.addTask();
      task3.setName("3.2.2");
      assertEquals("3.2.2", task3.getWBS());
      assertEquals(3, task3.getOutlineLevel().intValue());
      assertEquals("3.2.2", task3.getOutlineNumber());
      assertEquals(7, task3.getID().intValue());
      assertEquals(7, task3.getUniqueID().intValue());
      assertTrue(task1.getSummary());
      assertTrue(task2.getSummary());
      assertFalse(task3.getSummary());
   }

   /**
    * Test to ensure that the basic task hierarchy is
    * represented correctly.
    */
   @Test public void testStructure()
   {
      ProjectFile file = new ProjectFile();

      Task task1 = file.addTask();
      assertNull(task1.getParentTask());

      Task task2 = task1.addTask();
      assertEquals(task2.getParentTask(), task1);

      task1.addTask();
      List<Task> children = task1.getChildTasks();
      assertEquals(2, children.size());

      List<Task> toplevel = file.getChildTasks();
      assertEquals(1, toplevel.size());
   }

   /**
    * Exercise the MPP8 import code.
    */
   @Test public void testConversion1() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample98.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpp, out);
      commonTests(mpp);
      out.deleteOnExit();
   }

   /**
    * Exercise the MPP9 import code.
    */
   @Test public void testConversion2() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpp, out);
      commonTests(mpp);
      out.deleteOnExit();
   }

   /**
    * Exercise the XML import code.
    */
   @Test public void testConversion3() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample.xml"));
      ProjectFile xml = new MSPDIReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(xml, out);
      commonTests(xml);
      out.deleteOnExit();
   }

   /**
    * The files sample.mpp, sample98.mpp and sample.xml contain identical
    * data produced by MS Project. This method contains validation tests
    * on that data to ensure that the three file formats are being read
    * consistently.
    *
    * @param file ProjectFile instance
    */
   private void commonTests(ProjectFile file)
   {
      //
      // Test the remaining work attribute
      //
      Task task = file.getTaskByUniqueID(Integer.valueOf(2));
      List<ResourceAssignment> assignments = task.getResourceAssignments();
      assertEquals(2, assignments.size());

      for (ResourceAssignment assignment : assignments)
      {
         switch (NumberHelper.getInt(assignment.getResource().getID()))
         {
            case 1:
            {
               assertEquals(200, (int) assignment.getRemainingWork().getDuration());
               assertEquals(TimeUnit.HOURS, assignment.getRemainingWork().getUnits());
               break;
            }

            case 2:
            {
               assertEquals(300, (int) assignment.getRemainingWork().getDuration());
               assertEquals(TimeUnit.HOURS, assignment.getRemainingWork().getUnits());
               break;
            }

            default:
            {
               fail("Unexpected resource");
               break;
            }
         }
      }
   }

   /**
    * This method tests two stages of conversion, MPP to MPX to MSPDI. This
    * has been designed to exercise bug 896189, which was exhibited
    * when an MSPDI file was generated from an MPX file which did not
    * have the same set of attributes as a native MPP file.
    */
   @Test public void testConversion4() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpp, out);

      ProjectFile mpx = new MPXReader().read(out);
      out.deleteOnExit();
      out = Files.createTempFile("junit", ".xml").toFile();
      new MSPDIWriter().write(mpx, out);
      out.deleteOnExit();
   }

   /**
    * Simple test to exercise iterating through the task predecessors.
    */
   @Test public void testRelationList() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/sample.mpx"));
      ProjectFile mpx = new MPXReader().read(in);

      for (Task task : mpx.getTasks())
      {
         List<Relation> rels = task.getPredecessors();
         if (rels != null)
         {
            for (Relation rel : rels)
            {
               assertNotNull(mpx.getTaskByUniqueID(rel.getPredecessorTask().getUniqueID()));
            }
         }
      }
   }

   /**
    * This method exercises task notes, ensuring that
    * embedded commas and quotes are handled correctly.
    */
   @Test public void testTaskNotes() throws Exception
   {
      String notes1 = "Notes, containing a comma. Done.";
      String notes2 = "Notes \"containing embedded quotes\" Done.";
      String notes3 = "Notes, \"containing embedded quotes, and comma's too.\" Done.";
      String notes4 = "\"Notes containing embedded quotes as first and last chars. Done.\"";
      String notes5 = "Normal unquoted notes. Done.";

      ProjectFile file1 = new ProjectFile();

      Task task1 = file1.addTask();
      task1.setName("Test Task 1");
      task1.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
      task1.setStart(LocalDateTime.now());
      task1.setNotes(notes1);

      Task task2 = file1.addTask();
      task2.setName("Test Task 2");
      task2.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
      task2.setStart(LocalDateTime.now());
      task2.setNotes(notes2);

      Task task3 = file1.addTask();
      task3.setName("Test Task 3");
      task3.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
      task3.setStart(LocalDateTime.now());
      task3.setNotes(notes3);

      Task task4 = file1.addTask();
      task4.setName("Test Task 4");
      task4.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
      task4.setStart(LocalDateTime.now());
      task4.setNotes(notes4);

      Task task5 = file1.addTask();
      task5.setName("Test Task 5");
      task5.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
      task5.setStart(LocalDateTime.now());
      task5.setNotes(notes5);

      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(file1, out);

      ProjectFile file2 = new MPXReader().read(out);
      String notes;
      Task task1a = file2.getTaskByUniqueID(task1.getUniqueID());
      notes = task1a.getNotes();
      assertEquals(notes1, notes);

      Task task2a = file2.getTaskByUniqueID(task2.getUniqueID());
      notes = task2a.getNotes();
      assertEquals(notes2, notes);

      Task task3a = file2.getTaskByUniqueID(task3.getUniqueID());
      notes = task3a.getNotes();
      assertEquals(notes3, notes);

      Task task4a = file2.getTaskByUniqueID(task4.getUniqueID());
      notes = task4a.getNotes();
      assertEquals(notes4, notes);

      Task task5a = file2.getTaskByUniqueID(task5.getUniqueID());
      notes = task5a.getNotes();
      assertEquals(notes5, notes);

      out.deleteOnExit();
   }

   /**
    * This method exercises resource notes, ensuring that
    * embedded commas and quotes are handled correctly.
    */
   @Test public void testResourceNotes() throws Exception
   {
      String notes1 = "Notes, containing a comma. Done.";
      String notes2 = "Notes \"containing embedded quotes\" Done.";
      String notes3 = "Notes, \"containing embedded quotes, and comma's too.\" Done.";
      String notes4 = "\"Notes containing embedded quotes as first and last chars. Done.\"";
      String notes5 = "Normal unquoted notes. Done.";

      ProjectFile file1 = new ProjectFile();

      Resource resource1 = file1.addResource();
      resource1.setName("Test Resource 1");
      resource1.setNotes(notes1);

      Resource resource2 = file1.addResource();
      resource2.setName("Test Resource 2");
      resource2.setNotes(notes2);

      Resource resource3 = file1.addResource();
      resource3.setName("Test Resource 3");
      resource3.setNotes(notes3);

      Resource resource4 = file1.addResource();
      resource4.setName("Test Resource 4");
      resource4.setNotes(notes4);

      Resource resource5 = file1.addResource();
      resource5.setName("Test Resource 5");
      resource5.setNotes(notes5);

      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(file1, out);

      ProjectFile file2 = new MPXReader().read(out);
      String notes;
      Resource resource1a = file2.getResourceByUniqueID(resource1.getUniqueID());
      notes = resource1a.getNotes();
      assertEquals(notes1, notes);

      Resource resource2a = file2.getResourceByUniqueID(resource2.getUniqueID());
      notes = resource2a.getNotes();
      assertEquals(notes2, notes);

      Resource resource3a = file2.getResourceByUniqueID(resource3.getUniqueID());
      notes = resource3a.getNotes();
      assertEquals(notes3, notes);

      Resource resource4a = file2.getResourceByUniqueID(resource4.getUniqueID());
      notes = resource4a.getNotes();
      assertEquals(notes4, notes);

      Resource resource5a = file2.getResourceByUniqueID(resource5.getUniqueID());
      notes = resource5a.getNotes();
      assertEquals(notes5, notes);
      out.deleteOnExit();
   }

   /**
    * Read an MPP file that caused problems.
    */
   @Test public void testBug1() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/bug1.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpp, out);
      out.deleteOnExit();
   }

   /**
    * Read an MPP file that caused problems.
    */
   @Test public void testBug2() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/bug2.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpp, out);
      out.deleteOnExit();
   }

   /**
    * Read an MPP file where the structure was not being correctly
    * set up to reflect the outline level.
    */
   @Test public void testBug3() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/bug3.mpp"));
      ProjectFile mpp = new MPPReader().read(in);

      for (Task task : mpp.getTasks())
      {
         assertEquals("Outline levels do not match", task.getOutlineLevel().intValue(), calculateOutlineLevel(task));
      }
   }

   /**
    * Read an MPP8 file with a non-standard task fixed data block size.
    */
   @Test public void testBug4() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/bug4.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpp, out.getAbsolutePath());
      out.deleteOnExit();
   }

   /**
    * This method is used to calculate the outline level of a task. This
    * can then be compared to the outline level attribute of a task
    * to ensure that both values agree.
    *
    * @param task task object
    * @return outline level
    */
   private int calculateOutlineLevel(Task task)
   {
      int level = 0;

      while (task != null)
      {
         task = task.getParentTask();
         ++level;
      }

      return (level - 1);
   }

   /**
    * Ensure that we are reading MPP8 flags correctly. This test reads a
    * file where the tasks alternately have values of either all true, or
    * all false. Each pair of tasks increases by one in outline level.
    */
   @Test public void testMPP8Flags1() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/mpp8flags1.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      List<Task> tasks = mpp.getTasks();
      assertTrue("Not enough tasks", (!tasks.isEmpty()));
      assertTrue("Not an even number of tasks", (tasks.size() % 2 == 0));

      Iterator<Task> iter = tasks.iterator();
      Task task;
      while (iter.hasNext())
      {
         task = iter.next();
         assertFalse(task.getName(), task.getFlag(1));
         assertFalse(task.getName(), task.getFlag(2));
         assertFalse(task.getName(), task.getFlag(3));
         assertFalse(task.getName(), task.getFlag(4));
         assertFalse(task.getName(), task.getFlag(5));
         assertFalse(task.getName(), task.getFlag(6));
         assertFalse(task.getName(), task.getFlag(7));
         assertFalse(task.getName(), task.getFlag(8));
         assertFalse(task.getName(), task.getFlag(9));
         assertFalse(task.getName(), task.getFlag(10));
         assertFalse(task.getName(), task.getFlag(11));
         assertFalse(task.getName(), task.getFlag(12));
         assertFalse(task.getName(), task.getFlag(13));
         assertFalse(task.getName(), task.getFlag(14));
         assertFalse(task.getName(), task.getFlag(15));
         assertFalse(task.getName(), task.getFlag(16));
         assertFalse(task.getName(), task.getFlag(17));
         assertFalse(task.getName(), task.getFlag(18));
         assertFalse(task.getName(), task.getFlag(19));
         //assertFalse(task.getName(), task.getFlag(20));

         task = iter.next();
         assertTrue(task.getName(), task.getFlag(1));
         assertTrue(task.getName(), task.getFlag(2));
         assertTrue(task.getName(), task.getFlag(3));
         assertTrue(task.getName(), task.getFlag(4));
         assertTrue(task.getName(), task.getFlag(5));
         assertTrue(task.getName(), task.getFlag(6));
         assertTrue(task.getName(), task.getFlag(7));
         assertTrue(task.getName(), task.getFlag(8));
         assertTrue(task.getName(), task.getFlag(9));
         assertTrue(task.getName(), task.getFlag(10));
         assertTrue(task.getName(), task.getFlag(11));
         assertTrue(task.getName(), task.getFlag(12));
         assertTrue(task.getName(), task.getFlag(13));
         assertTrue(task.getName(), task.getFlag(14));
         assertTrue(task.getName(), task.getFlag(15));
         assertTrue(task.getName(), task.getFlag(16));
         assertTrue(task.getName(), task.getFlag(17));
         assertTrue(task.getName(), task.getFlag(18));
         assertTrue(task.getName(), task.getFlag(19));
         //assertTrue(task.getName(), task.getFlag(20));
      }
   }

   /**
    * This test reads flags from an MPP8 file where each set of 20 tasks has
    * a single flag from 1-20 set. The next set of 20 tasks increases by
    * one outline level.
    */
   @Test public void testMPP8Flags2() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/mpp8flags2.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      int index = 0;
      boolean[] flags;

      for (Task task : mpp.getTasks())
      {
         if (!task.getName().startsWith("Parent"))
         {
            flags = getFlagArray(task);
            assertTrue("Incorrect flag set in task " + task.getName(), testSingleFlagTrue(flags, index));
            ++index;
            if (index == 20)
            {
               index = 0;
            }
         }
      }
   }

   /**
    * Ensure that we are reading MPP9 flags correctly. This test reads a
    * file where the tasks alternately have values of either all true, or
    * all false. Each pair of tasks increases by one in outline level.
    */
   @Test public void testMPP9Flags1() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/mpp9flags1.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      Task parentTask = mpp.getTaskByID(Integer.valueOf(0));
      assertNotNull("Parent task missing", parentTask);
      List<Task> tasks = parentTask.getChildTasks();
      assertTrue("Not enough tasks", (!tasks.isEmpty()));
      assertTrue("Not an even number of tasks", (tasks.size() % 2 == 0));

      Iterator<Task> iter = tasks.iterator();
      Task task;
      while (iter.hasNext())
      {
         task = iter.next();
         assertFalse(task.getName(), task.getFlag(1));
         assertFalse(task.getName(), task.getFlag(2));
         assertFalse(task.getName(), task.getFlag(3));
         assertFalse(task.getName(), task.getFlag(4));
         assertFalse(task.getName(), task.getFlag(5));
         assertFalse(task.getName(), task.getFlag(6));
         assertFalse(task.getName(), task.getFlag(7));
         assertFalse(task.getName(), task.getFlag(8));
         assertFalse(task.getName(), task.getFlag(9));
         assertFalse(task.getName(), task.getFlag(10));
         assertFalse(task.getName(), task.getFlag(11));
         assertFalse(task.getName(), task.getFlag(12));
         assertFalse(task.getName(), task.getFlag(13));
         assertFalse(task.getName(), task.getFlag(14));
         assertFalse(task.getName(), task.getFlag(15));
         assertFalse(task.getName(), task.getFlag(16));
         assertFalse(task.getName(), task.getFlag(17));
         assertFalse(task.getName(), task.getFlag(18));
         assertFalse(task.getName(), task.getFlag(19));
         assertFalse(task.getName(), task.getFlag(20));

         task = iter.next();
         assertTrue(task.getName(), task.getFlag(1));
         assertTrue(task.getName(), task.getFlag(2));
         assertTrue(task.getName(), task.getFlag(3));
         assertTrue(task.getName(), task.getFlag(4));
         assertTrue(task.getName(), task.getFlag(5));
         assertTrue(task.getName(), task.getFlag(6));
         assertTrue(task.getName(), task.getFlag(7));
         assertTrue(task.getName(), task.getFlag(8));
         assertTrue(task.getName(), task.getFlag(9));
         assertTrue(task.getName(), task.getFlag(10));
         assertTrue(task.getName(), task.getFlag(11));
         assertTrue(task.getName(), task.getFlag(12));
         assertTrue(task.getName(), task.getFlag(13));
         assertTrue(task.getName(), task.getFlag(14));
         assertTrue(task.getName(), task.getFlag(15));
         assertTrue(task.getName(), task.getFlag(16));
         assertTrue(task.getName(), task.getFlag(17));
         assertTrue(task.getName(), task.getFlag(18));
         assertTrue(task.getName(), task.getFlag(19));
         assertTrue(task.getName(), task.getFlag(20));
      }
   }

   /**
    * This test reads flags from an MPP9 file where each set of 20 tasks has
    * a single flag from 1-20 set. The next set of 20 tasks increases by
    * one outline level.
    */
   @Test public void testMPP9Flags2() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/mpp9flags2.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      int index = 0;
      boolean[] flags;

      for (Task task : mpp.getTasks())
      {
         if (task.getUniqueID().intValue() != 0 && !task.getName().startsWith("Parent"))
         {
            flags = getFlagArray(task);
            assertTrue("Incorrect flag set in task " + task.getName(), testSingleFlagTrue(flags, index));
            ++index;
            if (index == 20)
            {
               index = 0;
            }
         }
      }
   }

   /**
    * This method is used to populate an array of boolean values with the
    * flag data from a task object.
    *
    * @param task task object
    * @return boolean array of flags
    */
   private boolean[] getFlagArray(Task task)
   {
      boolean[] flags = new boolean[20];

      flags[0] = task.getFlag(1);
      flags[1] = task.getFlag(2);
      flags[2] = task.getFlag(3);
      flags[3] = task.getFlag(4);
      flags[4] = task.getFlag(5);
      flags[5] = task.getFlag(6);
      flags[6] = task.getFlag(7);
      flags[7] = task.getFlag(8);
      flags[8] = task.getFlag(9);
      flags[9] = task.getFlag(10);
      flags[10] = task.getFlag(11);
      flags[11] = task.getFlag(12);
      flags[12] = task.getFlag(13);
      flags[13] = task.getFlag(14);
      flags[14] = task.getFlag(15);
      flags[15] = task.getFlag(16);
      flags[16] = task.getFlag(17);
      flags[17] = task.getFlag(18);
      flags[18] = task.getFlag(19);
      flags[19] = task.getFlag(20);

      return (flags);
   }

   /**
    * This method tests to ensure that a single flag is set to true,
    * and that all other flags are set to false.
    *
    * @param flags array of boolean flag values
    * @param index array index of flag which should be true
    * @return boolean flag indicating success or failure
    */
   private boolean testSingleFlagTrue(boolean[] flags, int index)
   {
      boolean result = true;

      for (int loop = 0; loop < flags.length; loop++)
      {
         if (flags[loop] && loop != index)
         {
            //System.out.println ("found flag at " + loop);
            result = false;
            break;
         }
      }

      return (result);
   }

   /**
    * Test retrieval of view information.
    */
   @Test public void testViews() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("legacy/sample98.mpp"));
      List<View> views = mpp.getViews();
      assertEquals("Incorrect number of views", 1, views.size());

      mpp = new MPPReader().read(MpxjTestData.filePath("legacy/sample.mpp"));
      views = mpp.getViews();
      assertEquals("Incorrect number of views", 3, views.size());
   }

   /**
    * Test retrieval of table information.
    */
   @Test public void testTables() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("legacy/sample98.mpp"));
      List<Table> tables = mpp.getTables();
      //      Iterator iter = tables.iterator();
      //      while (iter.hasNext() == true)
      //      {
      //         System.out.println(iter.next());
      //      }

      assertEquals("Incorrect number of tables", 1, tables.size());

      mpp = new MPPReader().read(MpxjTestData.filePath("legacy/sample.mpp"));
      tables = mpp.getTables();
      //      iter = tables.iterator();
      //      while (iter.hasNext() == true)
      //      {
      //         System.out.println(iter.next());
      //      }

      assertEquals("Incorrect number of tables", 2, tables.size());
   }

   /**
    * Test use of task calendars.
    */
   @Test public void testTaskCalendars() throws Exception
   {
      //
      // Read in the MPP file. The task names should
      // match the calendar names.
      //
      File in = new File(MpxjTestData.filePath("legacy/sample1.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      ProjectCalendar cal;

      for (Task task : mpp.getTasks())
      {
         cal = task.getCalendar();
         if (cal != null)
         {
            assertEquals(task.getName(), cal.getName());
         }
      }

      //
      // Write this out as an MSPDI file
      //
      File out = Files.createTempFile("junit", ".xml").toFile();
      new MSPDIWriter().write(mpp, out);

      //
      // Read the MSPDI file in again, and check the
      // calendar names to ensure consistency
      //
      ProjectFile mspdi = new MSPDIReader().read(out.getCanonicalPath());
      for (Task task : mspdi.getTasks())
      {
         cal = task.getCalendar();
         if (cal != null)
         {
            assertEquals(task.getName(), cal.getName());
         }
      }
      out.deleteOnExit();
   }

   /**
    * Exercise field alias code for MSPDI files.
    */
   @Test public void testMSPDIAliases() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      MSPDIWriter writer = new MSPDIWriter();

      File in = new File(MpxjTestData.filePath("legacy/alias.xml"));
      ProjectFile xml = reader.read(in);
      validateAliases(xml);

      File out = Files.createTempFile("junit", ".xml").toFile();
      writer.write(xml, out);

      xml = reader.read(out);
      validateAliases(xml);
      out.deleteOnExit();
   }

   /**
    * Exercise field alias code for MPP9 files.
    */
   @Test public void testMPP9Aliases() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/alias.mpp"));
      ProjectFile mpp = new MPPReader().read(in);
      validateAliases(mpp);
   }

   /**
    * Test to ensure that we are seeing the expected field
    * aliases.
    *
    * @param mpx MPX file
    */
   private void validateAliases(ProjectFile mpx)
   {
      CustomFieldContainer fields = mpx.getCustomFields();

      assertEquals("Text1t", fields.get(TaskField.TEXT1).getAlias());
      assertEquals("Text2t", fields.get(TaskField.TEXT2).getAlias());
      assertEquals("Text3t", fields.get(TaskField.TEXT3).getAlias());
      assertEquals("Text4t", fields.get(TaskField.TEXT4).getAlias());
      assertEquals("Text5t", fields.get(TaskField.TEXT5).getAlias());
      assertEquals("Text6t", fields.get(TaskField.TEXT6).getAlias());
      assertEquals("Text7t", fields.get(TaskField.TEXT7).getAlias());
      assertEquals("Text8t", fields.get(TaskField.TEXT8).getAlias());
      assertEquals("Text9t", fields.get(TaskField.TEXT9).getAlias());
      assertEquals("Text10t", fields.get(TaskField.TEXT10).getAlias());
      assertEquals("Text11t", fields.get(TaskField.TEXT11).getAlias());
      assertEquals("Text12t", fields.get(TaskField.TEXT12).getAlias());
      assertEquals("Text13t", fields.get(TaskField.TEXT13).getAlias());
      assertEquals("Text14t", fields.get(TaskField.TEXT14).getAlias());
      assertEquals("Text15t", fields.get(TaskField.TEXT15).getAlias());
      assertEquals("Text16t", fields.get(TaskField.TEXT16).getAlias());
      assertEquals("Text17t", fields.get(TaskField.TEXT17).getAlias());
      assertEquals("Text18t", fields.get(TaskField.TEXT18).getAlias());
      assertEquals("Text19t", fields.get(TaskField.TEXT19).getAlias());
      assertEquals("Text20t", fields.get(TaskField.TEXT20).getAlias());
      assertEquals("Text21t", fields.get(TaskField.TEXT21).getAlias());
      assertEquals("Text22t", fields.get(TaskField.TEXT22).getAlias());
      assertEquals("Text23t", fields.get(TaskField.TEXT23).getAlias());
      assertEquals("Text24t", fields.get(TaskField.TEXT24).getAlias());
      assertEquals("Text25t", fields.get(TaskField.TEXT25).getAlias());
      assertEquals("Text26t", fields.get(TaskField.TEXT26).getAlias());
      assertEquals("Text27t", fields.get(TaskField.TEXT27).getAlias());
      assertEquals("Text28t", fields.get(TaskField.TEXT28).getAlias());
      assertEquals("Text29t", fields.get(TaskField.TEXT29).getAlias());
      assertEquals("Text30t", fields.get(TaskField.TEXT30).getAlias());
      assertEquals("Start1t", fields.get(TaskField.START1).getAlias());
      assertEquals("Start2t", fields.get(TaskField.START2).getAlias());
      assertEquals("Start3t", fields.get(TaskField.START3).getAlias());
      assertEquals("Start4t", fields.get(TaskField.START4).getAlias());
      assertEquals("Start5t", fields.get(TaskField.START5).getAlias());
      assertEquals("Start6t", fields.get(TaskField.START6).getAlias());
      assertEquals("Start7t", fields.get(TaskField.START7).getAlias());
      assertEquals("Start8t", fields.get(TaskField.START8).getAlias());
      assertEquals("Start9t", fields.get(TaskField.START9).getAlias());
      assertEquals("Start10t", fields.get(TaskField.START10).getAlias());
      assertEquals("Finish1t", fields.get(TaskField.FINISH1).getAlias());
      assertEquals("Finish2t", fields.get(TaskField.FINISH2).getAlias());
      assertEquals("Finish3t", fields.get(TaskField.FINISH3).getAlias());
      assertEquals("Finish4t", fields.get(TaskField.FINISH4).getAlias());
      assertEquals("Finish5t", fields.get(TaskField.FINISH5).getAlias());
      assertEquals("Finish6t", fields.get(TaskField.FINISH6).getAlias());
      assertEquals("Finish7t", fields.get(TaskField.FINISH7).getAlias());
      assertEquals("Finish8t", fields.get(TaskField.FINISH8).getAlias());
      assertEquals("Finish9t", fields.get(TaskField.FINISH9).getAlias());
      assertEquals("Finish10t", fields.get(TaskField.FINISH10).getAlias());
      assertEquals("Cost1t", fields.get(TaskField.COST1).getAlias());
      assertEquals("Cost2t", fields.get(TaskField.COST2).getAlias());
      assertEquals("Cost3t", fields.get(TaskField.COST3).getAlias());
      assertEquals("Cost4t", fields.get(TaskField.COST4).getAlias());
      assertEquals("Cost5t", fields.get(TaskField.COST5).getAlias());
      assertEquals("Cost6t", fields.get(TaskField.COST6).getAlias());
      assertEquals("Cost7t", fields.get(TaskField.COST7).getAlias());
      assertEquals("Cost8t", fields.get(TaskField.COST8).getAlias());
      assertEquals("Cost9t", fields.get(TaskField.COST9).getAlias());
      assertEquals("Cost10t", fields.get(TaskField.COST10).getAlias());
      assertEquals("Date1t", fields.get(TaskField.DATE1).getAlias());
      assertEquals("Date2t", fields.get(TaskField.DATE2).getAlias());
      assertEquals("Date3t", fields.get(TaskField.DATE3).getAlias());
      assertEquals("Date4t", fields.get(TaskField.DATE4).getAlias());
      assertEquals("Date5t", fields.get(TaskField.DATE5).getAlias());
      assertEquals("Date6t", fields.get(TaskField.DATE6).getAlias());
      assertEquals("Date7t", fields.get(TaskField.DATE7).getAlias());
      assertEquals("Date8t", fields.get(TaskField.DATE8).getAlias());
      assertEquals("Date9t", fields.get(TaskField.DATE9).getAlias());
      assertEquals("Date10t", fields.get(TaskField.DATE10).getAlias());
      assertEquals("Flag1t", fields.get(TaskField.FLAG1).getAlias());
      assertEquals("Flag2t", fields.get(TaskField.FLAG2).getAlias());
      assertEquals("Flag3t", fields.get(TaskField.FLAG3).getAlias());
      assertEquals("Flag4t", fields.get(TaskField.FLAG4).getAlias());
      assertEquals("Flag5t", fields.get(TaskField.FLAG5).getAlias());
      assertEquals("Flag6t", fields.get(TaskField.FLAG6).getAlias());
      assertEquals("Flag7t", fields.get(TaskField.FLAG7).getAlias());
      assertEquals("Flag8t", fields.get(TaskField.FLAG8).getAlias());
      assertEquals("Flag9t", fields.get(TaskField.FLAG9).getAlias());
      assertEquals("Flag10t", fields.get(TaskField.FLAG10).getAlias());
      assertEquals("Flag11t", fields.get(TaskField.FLAG11).getAlias());
      assertEquals("Flag12t", fields.get(TaskField.FLAG12).getAlias());
      assertEquals("Flag13t", fields.get(TaskField.FLAG13).getAlias());
      assertEquals("Flag14t", fields.get(TaskField.FLAG14).getAlias());
      assertEquals("Flag15t", fields.get(TaskField.FLAG15).getAlias());
      assertEquals("Flag16t", fields.get(TaskField.FLAG16).getAlias());
      assertEquals("Flag17t", fields.get(TaskField.FLAG17).getAlias());
      assertEquals("Flag18t", fields.get(TaskField.FLAG18).getAlias());
      assertEquals("Flag19t", fields.get(TaskField.FLAG19).getAlias());
      assertEquals("Flag20t", fields.get(TaskField.FLAG20).getAlias());
      assertEquals("Number1t", fields.get(TaskField.NUMBER1).getAlias());
      assertEquals("Number2t", fields.get(TaskField.NUMBER2).getAlias());
      assertEquals("Number3t", fields.get(TaskField.NUMBER3).getAlias());
      assertEquals("Number4t", fields.get(TaskField.NUMBER4).getAlias());
      assertEquals("Number5t", fields.get(TaskField.NUMBER5).getAlias());
      assertEquals("Number6t", fields.get(TaskField.NUMBER6).getAlias());
      assertEquals("Number7t", fields.get(TaskField.NUMBER7).getAlias());
      assertEquals("Number8t", fields.get(TaskField.NUMBER8).getAlias());
      assertEquals("Number9t", fields.get(TaskField.NUMBER9).getAlias());
      assertEquals("Number10t", fields.get(TaskField.NUMBER10).getAlias());
      assertEquals("Number11t", fields.get(TaskField.NUMBER11).getAlias());
      assertEquals("Number12t", fields.get(TaskField.NUMBER12).getAlias());
      assertEquals("Number13t", fields.get(TaskField.NUMBER13).getAlias());
      assertEquals("Number14t", fields.get(TaskField.NUMBER14).getAlias());
      assertEquals("Number15t", fields.get(TaskField.NUMBER15).getAlias());
      assertEquals("Number16t", fields.get(TaskField.NUMBER16).getAlias());
      assertEquals("Number17t", fields.get(TaskField.NUMBER17).getAlias());
      assertEquals("Number18t", fields.get(TaskField.NUMBER18).getAlias());
      assertEquals("Number19t", fields.get(TaskField.NUMBER19).getAlias());
      assertEquals("Number20t", fields.get(TaskField.NUMBER20).getAlias());
      assertEquals("Duration1t", fields.get(TaskField.DURATION1).getAlias());
      assertEquals("Duration2t", fields.get(TaskField.DURATION2).getAlias());
      assertEquals("Duration3t", fields.get(TaskField.DURATION3).getAlias());
      assertEquals("Duration4t", fields.get(TaskField.DURATION4).getAlias());
      assertEquals("Duration5t", fields.get(TaskField.DURATION5).getAlias());
      assertEquals("Duration6t", fields.get(TaskField.DURATION6).getAlias());
      assertEquals("Duration7t", fields.get(TaskField.DURATION7).getAlias());
      assertEquals("Duration8t", fields.get(TaskField.DURATION8).getAlias());
      assertEquals("Duration9t", fields.get(TaskField.DURATION9).getAlias());
      assertEquals("Duration10t", fields.get(TaskField.DURATION10).getAlias());
      assertEquals("Outline Code1t", fields.get(TaskField.OUTLINE_CODE1).getAlias());
      assertEquals("Outline Code2t", fields.get(TaskField.OUTLINE_CODE2).getAlias());
      assertEquals("Outline Code3t", fields.get(TaskField.OUTLINE_CODE3).getAlias());
      assertEquals("Outline Code4t", fields.get(TaskField.OUTLINE_CODE4).getAlias());
      assertEquals("Outline Code5t", fields.get(TaskField.OUTLINE_CODE5).getAlias());
      assertEquals("Outline Code6t", fields.get(TaskField.OUTLINE_CODE6).getAlias());
      assertEquals("Outline Code7t", fields.get(TaskField.OUTLINE_CODE7).getAlias());
      assertEquals("Outline Code8t", fields.get(TaskField.OUTLINE_CODE8).getAlias());
      assertEquals("Outline Code9t", fields.get(TaskField.OUTLINE_CODE9).getAlias());
      assertEquals("Outline Code10t", fields.get(TaskField.OUTLINE_CODE10).getAlias());

      assertEquals("Text1r", fields.get(ResourceField.TEXT1).getAlias());
      assertEquals("Text2r", fields.get(ResourceField.TEXT2).getAlias());
      assertEquals("Text3r", fields.get(ResourceField.TEXT3).getAlias());
      assertEquals("Text4r", fields.get(ResourceField.TEXT4).getAlias());
      assertEquals("Text5r", fields.get(ResourceField.TEXT5).getAlias());
      assertEquals("Text6r", fields.get(ResourceField.TEXT6).getAlias());
      assertEquals("Text7r", fields.get(ResourceField.TEXT7).getAlias());
      assertEquals("Text8r", fields.get(ResourceField.TEXT8).getAlias());
      assertEquals("Text9r", fields.get(ResourceField.TEXT9).getAlias());
      assertEquals("Text10r", fields.get(ResourceField.TEXT10).getAlias());
      assertEquals("Text11r", fields.get(ResourceField.TEXT11).getAlias());
      assertEquals("Text12r", fields.get(ResourceField.TEXT12).getAlias());
      assertEquals("Text13r", fields.get(ResourceField.TEXT13).getAlias());
      assertEquals("Text14r", fields.get(ResourceField.TEXT14).getAlias());
      assertEquals("Text15r", fields.get(ResourceField.TEXT15).getAlias());
      assertEquals("Text16r", fields.get(ResourceField.TEXT16).getAlias());
      assertEquals("Text17r", fields.get(ResourceField.TEXT17).getAlias());
      assertEquals("Text18r", fields.get(ResourceField.TEXT18).getAlias());
      assertEquals("Text19r", fields.get(ResourceField.TEXT19).getAlias());
      assertEquals("Text20r", fields.get(ResourceField.TEXT20).getAlias());
      assertEquals("Text21r", fields.get(ResourceField.TEXT21).getAlias());
      assertEquals("Text22r", fields.get(ResourceField.TEXT22).getAlias());
      assertEquals("Text23r", fields.get(ResourceField.TEXT23).getAlias());
      assertEquals("Text24r", fields.get(ResourceField.TEXT24).getAlias());
      assertEquals("Text25r", fields.get(ResourceField.TEXT25).getAlias());
      assertEquals("Text26r", fields.get(ResourceField.TEXT26).getAlias());
      assertEquals("Text27r", fields.get(ResourceField.TEXT27).getAlias());
      assertEquals("Text28r", fields.get(ResourceField.TEXT28).getAlias());
      assertEquals("Text29r", fields.get(ResourceField.TEXT29).getAlias());
      assertEquals("Text30r", fields.get(ResourceField.TEXT30).getAlias());
      assertEquals("Start1r", fields.get(ResourceField.START1).getAlias());
      assertEquals("Start2r", fields.get(ResourceField.START2).getAlias());
      assertEquals("Start3r", fields.get(ResourceField.START3).getAlias());
      assertEquals("Start4r", fields.get(ResourceField.START4).getAlias());
      assertEquals("Start5r", fields.get(ResourceField.START5).getAlias());
      assertEquals("Start6r", fields.get(ResourceField.START6).getAlias());
      assertEquals("Start7r", fields.get(ResourceField.START7).getAlias());
      assertEquals("Start8r", fields.get(ResourceField.START8).getAlias());
      assertEquals("Start9r", fields.get(ResourceField.START9).getAlias());
      assertEquals("Start10r", fields.get(ResourceField.START10).getAlias());
      assertEquals("Finish1r", fields.get(ResourceField.FINISH1).getAlias());
      assertEquals("Finish2r", fields.get(ResourceField.FINISH2).getAlias());
      assertEquals("Finish3r", fields.get(ResourceField.FINISH3).getAlias());
      assertEquals("Finish4r", fields.get(ResourceField.FINISH4).getAlias());
      assertEquals("Finish5r", fields.get(ResourceField.FINISH5).getAlias());
      assertEquals("Finish6r", fields.get(ResourceField.FINISH6).getAlias());
      assertEquals("Finish7r", fields.get(ResourceField.FINISH7).getAlias());
      assertEquals("Finish8r", fields.get(ResourceField.FINISH8).getAlias());
      assertEquals("Finish9r", fields.get(ResourceField.FINISH9).getAlias());
      assertEquals("Finish10r", fields.get(ResourceField.FINISH10).getAlias());
      assertEquals("Cost1r", fields.get(ResourceField.COST1).getAlias());
      assertEquals("Cost2r", fields.get(ResourceField.COST2).getAlias());
      assertEquals("Cost3r", fields.get(ResourceField.COST3).getAlias());
      assertEquals("Cost4r", fields.get(ResourceField.COST4).getAlias());
      assertEquals("Cost5r", fields.get(ResourceField.COST5).getAlias());
      assertEquals("Cost6r", fields.get(ResourceField.COST6).getAlias());
      assertEquals("Cost7r", fields.get(ResourceField.COST7).getAlias());
      assertEquals("Cost8r", fields.get(ResourceField.COST8).getAlias());
      assertEquals("Cost9r", fields.get(ResourceField.COST9).getAlias());
      assertEquals("Cost10r", fields.get(ResourceField.COST10).getAlias());
      assertEquals("Date1r", fields.get(ResourceField.DATE1).getAlias());
      assertEquals("Date2r", fields.get(ResourceField.DATE2).getAlias());
      assertEquals("Date3r", fields.get(ResourceField.DATE3).getAlias());
      assertEquals("Date4r", fields.get(ResourceField.DATE4).getAlias());
      assertEquals("Date5r", fields.get(ResourceField.DATE5).getAlias());
      assertEquals("Date6r", fields.get(ResourceField.DATE6).getAlias());
      assertEquals("Date7r", fields.get(ResourceField.DATE7).getAlias());
      assertEquals("Date8r", fields.get(ResourceField.DATE8).getAlias());
      assertEquals("Date9r", fields.get(ResourceField.DATE9).getAlias());
      assertEquals("Date10r", fields.get(ResourceField.DATE10).getAlias());
      assertEquals("Flag1r", fields.get(ResourceField.FLAG1).getAlias());
      assertEquals("Flag2r", fields.get(ResourceField.FLAG2).getAlias());
      assertEquals("Flag3r", fields.get(ResourceField.FLAG3).getAlias());
      assertEquals("Flag4r", fields.get(ResourceField.FLAG4).getAlias());
      assertEquals("Flag5r", fields.get(ResourceField.FLAG5).getAlias());
      assertEquals("Flag6r", fields.get(ResourceField.FLAG6).getAlias());
      assertEquals("Flag7r", fields.get(ResourceField.FLAG7).getAlias());
      assertEquals("Flag8r", fields.get(ResourceField.FLAG8).getAlias());
      assertEquals("Flag9r", fields.get(ResourceField.FLAG9).getAlias());
      assertEquals("Flag10r", fields.get(ResourceField.FLAG10).getAlias());
      assertEquals("Flag11r", fields.get(ResourceField.FLAG11).getAlias());
      assertEquals("Flag12r", fields.get(ResourceField.FLAG12).getAlias());
      assertEquals("Flag13r", fields.get(ResourceField.FLAG13).getAlias());
      assertEquals("Flag14r", fields.get(ResourceField.FLAG14).getAlias());
      assertEquals("Flag15r", fields.get(ResourceField.FLAG15).getAlias());
      assertEquals("Flag16r", fields.get(ResourceField.FLAG16).getAlias());
      assertEquals("Flag17r", fields.get(ResourceField.FLAG17).getAlias());
      assertEquals("Flag18r", fields.get(ResourceField.FLAG18).getAlias());
      assertEquals("Flag19r", fields.get(ResourceField.FLAG19).getAlias());
      assertEquals("Flag20r", fields.get(ResourceField.FLAG20).getAlias());
      assertEquals("Number1r", fields.get(ResourceField.NUMBER1).getAlias());
      assertEquals("Number2r", fields.get(ResourceField.NUMBER2).getAlias());
      assertEquals("Number3r", fields.get(ResourceField.NUMBER3).getAlias());
      assertEquals("Number4r", fields.get(ResourceField.NUMBER4).getAlias());
      assertEquals("Number5r", fields.get(ResourceField.NUMBER5).getAlias());
      assertEquals("Number6r", fields.get(ResourceField.NUMBER6).getAlias());
      assertEquals("Number7r", fields.get(ResourceField.NUMBER7).getAlias());
      assertEquals("Number8r", fields.get(ResourceField.NUMBER8).getAlias());
      assertEquals("Number9r", fields.get(ResourceField.NUMBER9).getAlias());
      assertEquals("Number10r", fields.get(ResourceField.NUMBER10).getAlias());
      assertEquals("Number11r", fields.get(ResourceField.NUMBER11).getAlias());
      assertEquals("Number12r", fields.get(ResourceField.NUMBER12).getAlias());
      assertEquals("Number13r", fields.get(ResourceField.NUMBER13).getAlias());
      assertEquals("Number14r", fields.get(ResourceField.NUMBER14).getAlias());
      assertEquals("Number15r", fields.get(ResourceField.NUMBER15).getAlias());
      assertEquals("Number16r", fields.get(ResourceField.NUMBER16).getAlias());
      assertEquals("Number17r", fields.get(ResourceField.NUMBER17).getAlias());
      assertEquals("Number18r", fields.get(ResourceField.NUMBER18).getAlias());
      assertEquals("Number19r", fields.get(ResourceField.NUMBER19).getAlias());
      assertEquals("Number20r", fields.get(ResourceField.NUMBER20).getAlias());
      assertEquals("Duration1r", fields.get(ResourceField.DURATION1).getAlias());
      assertEquals("Duration2r", fields.get(ResourceField.DURATION2).getAlias());
      assertEquals("Duration3r", fields.get(ResourceField.DURATION3).getAlias());
      assertEquals("Duration4r", fields.get(ResourceField.DURATION4).getAlias());
      assertEquals("Duration5r", fields.get(ResourceField.DURATION5).getAlias());
      assertEquals("Duration6r", fields.get(ResourceField.DURATION6).getAlias());
      assertEquals("Duration7r", fields.get(ResourceField.DURATION7).getAlias());
      assertEquals("Duration8r", fields.get(ResourceField.DURATION8).getAlias());
      assertEquals("Duration9r", fields.get(ResourceField.DURATION9).getAlias());
      assertEquals("Duration10r", fields.get(ResourceField.DURATION10).getAlias());
      assertEquals("Outline Code1r", fields.get(ResourceField.OUTLINE_CODE1).getAlias());
      assertEquals("Outline Code2r", fields.get(ResourceField.OUTLINE_CODE2).getAlias());
      assertEquals("Outline Code3r", fields.get(ResourceField.OUTLINE_CODE3).getAlias());
      assertEquals("Outline Code4r", fields.get(ResourceField.OUTLINE_CODE4).getAlias());
      assertEquals("Outline Code5r", fields.get(ResourceField.OUTLINE_CODE5).getAlias());
      assertEquals("Outline Code6r", fields.get(ResourceField.OUTLINE_CODE6).getAlias());
      assertEquals("Outline Code7r", fields.get(ResourceField.OUTLINE_CODE7).getAlias());
      assertEquals("Outline Code8r", fields.get(ResourceField.OUTLINE_CODE8).getAlias());
      assertEquals("Outline Code9r", fields.get(ResourceField.OUTLINE_CODE9).getAlias());
      assertEquals("Outline Code10r", fields.get(ResourceField.OUTLINE_CODE10).getAlias());
   }

   /**
    * Write a file with embedded line break (\r and \n) characters in
    * various text fields. Ensure that a valid file is written,
    * and that it can be read successfully.
    */
   @Test public void testEmbeddedLineBreaks() throws Exception
   {
      //
      // Create a simple MPX file
      //
      ProjectFile file = new ProjectFile();
      file.addDefaultBaseCalendar();

      ProjectProperties properties = file.getProjectProperties();
      properties.setComments("Project Header Comments: Some\rExample\nText\r\nWith\n\rBreaks");
      properties.setStartDate(LocalDateTime.of(2003, 1, 1, 0, 0));

      Resource resource1 = file.addResource();
      resource1.setName("Resource1: Some\rExample\nText\r\nWith\n\rBreaks");
      resource1.setNotes("Resource1 Notes: Some\rExample\nText\r\nWith\n\rBreaks");

      Task task1 = file.addTask();
      task1.setName("Task1: Some\rExample\nText\r\nWith\n\rBreaks");
      task1.setNotes("Task1 Notes: Some\rExample\nText\r\nWith\n\rBreaks");

      //
      // Write the file
      //
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(file, out);

      //
      // Ensure we can read it successfully
      //
      file = new MPXReader().read(out);
      assertEquals(1, file.getTasks().size());
      assertEquals(1, file.getResources().size());

      properties = file.getProjectProperties();
      assertEquals("Project Header Comments: Some\nExample\nText\nWith\nBreaks", properties.getComments());

      task1 = file.getTaskByUniqueID(Integer.valueOf(1));
      assertEquals("Task1: Some\nExample\nText\nWith\nBreaks", task1.getName());
      assertEquals("Task1 Notes: Some\nExample\nText\nWith\nBreaks", task1.getNotes());

      resource1 = file.getResourceByUniqueID(Integer.valueOf(1));
      assertEquals("Resource1: Some\nExample\nText\nWith\nBreaks", resource1.getName());
      assertEquals("Resource1 Notes: Some\nExample\nText\nWith\nBreaks", resource1.getNotes());
      out.deleteOnExit();
   }

   /**
    * Exercise the code which handles password protected files.
    */
   @Test public void testPasswordProtection() throws Exception
   {
      File in;
      MPPReader reader;

      //
      // Read password (password1)
      //
      try
      {
         in = new File(MpxjTestData.filePath("legacy/readpassword9.mpp"));
         new MPPReader().read(in);
         fail();
      }

      catch (MPXJException ex)
      {
         assertEquals(MPXJException.PASSWORD_PROTECTED_ENTER_PASSWORD, ex.getMessage());
      }

      // Ignore password mode.
      in = new File(MpxjTestData.filePath("legacy/readpassword9.mpp"));
      reader = new MPPReader();
      reader.setRespectPasswordProtection(false);
      reader.read(in);

      //
      // Write password (password2)
      //
      in = new File(MpxjTestData.filePath("legacy/writepassword9.mpp"));
      new MPPReader().read(in);

      //
      // Read password
      //
      try
      {
         in = new File(MpxjTestData.filePath("legacy/bothpassword9.mpp"));
         new MPPReader().read(in);
         fail();
      }

      catch (MPXJException ex)
      {
         assertEquals(MPXJException.PASSWORD_PROTECTED_ENTER_PASSWORD, ex.getMessage());
      }

      in = new File(MpxjTestData.filePath("legacy/bothpassword9.mpp"));
      reader = new MPPReader();
      reader.setRespectPasswordProtection(false);
      reader.read(in);
   }

   /**
    * This test ensures that the task and resource extended attributes are
    * read and written correctly for MSPDI files.
    */
   @Test public void testMspdiExtendedAttributes() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      MSPDIWriter writer = new MSPDIWriter();

      ProjectFile xml = reader.read(MpxjTestData.filePath("legacy/mspextattr.xml"));
      commonMspdiExtendedAttributeTests(xml);

      File out = Files.createTempFile("junit", ".xml").toFile();
      writer.write(xml, out);

      xml = reader.read(out);
      commonMspdiExtendedAttributeTests(xml);

      out.deleteOnExit();
   }

   /**
    * Common tests for MSPDI fileExtended Attribute values.
    *
    * @param xml MSPDI file
    */
   private void commonMspdiExtendedAttributeTests(ProjectFile xml)
   {
      List<Task> tasks = xml.getTasks();
      assertEquals(2, tasks.size());

      Task task = tasks.get(1);
      assertEquals("Task Text One", task.getText(1));
      assertEquals(LocalDateTime.of(2004, 1, 1, 8, 0), task.getStart(1));
      assertEquals(LocalDateTime.of(2004, 12, 31, 17, 0), task.getFinish(1));
      assertEquals(99.95, task.getCost(1).doubleValue(), 0.0);
      assertEquals(LocalDateTime.of(2004, 7, 18, 8, 0), task.getDate(1));
      assertTrue(task.getFlag(1));
      assertEquals(55.56, task.getNumber(1).doubleValue(), 0.0);
      assertEquals(13.0, task.getDuration(1).getDuration(), 0.0);
      assertEquals(TimeUnit.DAYS, task.getDuration(1).getUnits());

      List<Resource> resources = xml.getResources();
      assertEquals(2, resources.size());

      Resource resource = resources.get(1);
      assertEquals("Resource Text One", resource.getText(1));
      assertEquals(LocalDateTime.of(2003, 1, 1, 8, 0), resource.getStart(1));
      assertEquals(LocalDateTime.of(2003, 12, 31, 17, 0), resource.getFinish(1));
      assertEquals(29.99, resource.getCost(1).doubleValue(), 0.0);
      assertEquals(LocalDateTime.of(2003, 7, 18, 8, 0), resource.getDate(1));
      assertTrue(resource.getFlag(1));
      assertEquals(5.99, resource.getNumber(1).doubleValue(), 0.0);
      assertEquals(22.0, resource.getDuration(1).getDuration(), 0.0);
      assertEquals(TimeUnit.DAYS, resource.getDuration(1).getUnits());
   }

   /**
    * This ensures that values in the project properties are read and written
    * as expected.
    */
   @Test public void testProjectProperties() throws Exception
   {
      MPXReader reader = new MPXReader();
      MPXWriter writer = new MPXWriter();

      //
      // Read the MPX file and ensure that the project properties
      // have the expected values.
      //
      ProjectFile mpx = reader.read(MpxjTestData.filePath("legacy/headertest.mpx"));
      testProperties(mpx);

      //
      // Write the file, re-read it and test to ensure that
      // the project properties have the expected values
      //
      File out = Files.createTempFile("junit", ".mpx").toFile();
      writer.write(mpx, out);
      mpx = reader.read(out);
      testProperties(mpx);
      out.deleteOnExit();

      //
      // Read the MPP8 file and ensure that the project properties
      // have the expected values.
      //
      mpx = new MPPReader().read(MpxjTestData.filePath("legacy/headertest8.mpp"));
      testProperties(mpx);

      //
      // Read the MPP9 file and ensure that the project properties
      // have the expected values.
      //
      mpx = new MPPReader().read(MpxjTestData.filePath("legacy/headertest9.mpp"));
      testProperties(mpx);

      //
      // Read the MSPDI file and ensure that the project properties
      // have the expected values.
      //
      mpx = new MSPDIReader().read(MpxjTestData.filePath("legacy/headertest9.xml"));
      testMspdiProperties(mpx);

      //
      // Write the file, re-read it and test to ensure that
      // the project properties have the expected values
      //
      out = Files.createTempFile("junit", ".xml").toFile();
      new MSPDIWriter().write(mpx, out);

      mpx = new MSPDIReader().read(out);
      testMspdiProperties(mpx);
      out.deleteOnExit();
   }

   /**
    * Implements common project properties tests.
    *
    * @param file target project file
    */
   private void testProperties(ProjectFile file)
   {
      ProjectProperties properties = file.getProjectProperties();
      assertEquals("Project Title Text", properties.getProjectTitle());
      assertEquals("Author Text", properties.getAuthor());
      assertEquals("Comments Text", properties.getComments());
      assertEquals("Company Text", properties.getCompany());
      assertEquals("Keywords Text", properties.getKeywords());
      assertEquals("Manager Text", properties.getManager());
      assertEquals("Subject Text", properties.getSubject());
   }

   /**
    * Implements common project properties tests.
    *
    * @param file target project file
    */
   private void testMspdiProperties(ProjectFile file)
   {
      ProjectProperties properties = file.getProjectProperties();
      assertEquals("Project Title Text", properties.getProjectTitle());
      assertEquals("Author Text", properties.getAuthor());
      // Looks like an oversight in the schema - the Notes field is present in files, but not in the schema
      //assertEquals("Comments Text", properties.getComments());
      assertEquals("Company Text", properties.getCompany());
      // Doesn't look like keywords is present in MSPDI files at all
      //assertEquals("Keywords Text", properties.getKeywords());
      assertEquals("Manager Text", properties.getManager());
      assertEquals("Subject Text", properties.getSubject());
   }

   /**
    * Test retrieval of WBS information.
    */
   @Test public void testWBS() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("legacy/sample98.mpp"));
      Task task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals("Second Task", task.getName());
      assertEquals("1.1", task.getWBS());

      mpp = new MPPReader().read(MpxjTestData.filePath("legacy/sample.mpp"));
      task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals("Second Task", task.getName());
      assertEquals("1.1", task.getWBS());
   }

   /**
    * Test read and write of priority information.
    */
   @Test public void testPriority() throws Exception
   {
      ProjectFile mpx = new MPXReader().read(MpxjTestData.filePath("legacy/mpxpriority.mpx"));
      validatePriority(mpx);

      ProjectFile mpp8 = new MPPReader().read(MpxjTestData.filePath("legacy/mpp8priority.mpp"));
      validatePriority(mpp8);

      ProjectFile mpp9 = new MPPReader().read(MpxjTestData.filePath("legacy/mpp9priority.mpp"));
      validatePriority(mpp9);

      ProjectFile xml = new MSPDIReader().read(MpxjTestData.filePath("legacy/mspdipriority.xml"));
      validatePriority(xml);

      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpx, out);
      ProjectFile mpx2 = new MPXReader().read(out);
      validatePriority(mpx2);
      out.deleteOnExit();

      out = Files.createTempFile("junit", ".xml").toFile();
      new MSPDIWriter().write(mpx, out);
      ProjectFile xml3 = new MSPDIReader().read(out);
      validatePriority(xml3);
      out.deleteOnExit();
   }

   /**
    * Common tests to validate the priority values read from the file.
    *
    * @param file project file
    */
   private void validatePriority(ProjectFile file)
   {
      assertEquals(Priority.DO_NOT_LEVEL, file.getTaskByUniqueID(Integer.valueOf(1)).getPriority().getValue());
      assertEquals(Priority.HIGHEST, file.getTaskByUniqueID(Integer.valueOf(2)).getPriority().getValue());
      assertEquals(Priority.VERY_HIGH, file.getTaskByUniqueID(Integer.valueOf(3)).getPriority().getValue());
      assertEquals(Priority.HIGHER, file.getTaskByUniqueID(Integer.valueOf(4)).getPriority().getValue());
      assertEquals(Priority.HIGH, file.getTaskByUniqueID(Integer.valueOf(5)).getPriority().getValue());
      assertEquals(Priority.MEDIUM, file.getTaskByUniqueID(Integer.valueOf(6)).getPriority().getValue());
      assertEquals(Priority.LOW, file.getTaskByUniqueID(Integer.valueOf(7)).getPriority().getValue());
      assertEquals(Priority.LOWER, file.getTaskByUniqueID(Integer.valueOf(8)).getPriority().getValue());
      assertEquals(Priority.VERY_LOW, file.getTaskByUniqueID(Integer.valueOf(9)).getPriority().getValue());
      assertEquals(Priority.LOWEST, file.getTaskByUniqueID(Integer.valueOf(10)).getPriority().getValue());
   }

   /**
    * Tests to exercise calendar functionality.
    */
   @Test public void testCalendars() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("legacy/caltest98.mpp"));
      validateResourceCalendars(mpp);

      ProjectFile mpx = new MPXReader().read(MpxjTestData.filePath("legacy/caltest98.mpx"));
      validateResourceCalendars(mpx);

      ProjectFile mpp9 = new MPPReader().read(MpxjTestData.filePath("legacy/caltest.mpp"));
      validateResourceCalendars(mpp9);
      validateTaskCalendars(mpp9);

      ProjectFile xml = new MSPDIReader().read(MpxjTestData.filePath("legacy/caltest.xml"));
      validateResourceCalendars(xml);
      validateTaskCalendars(xml);
   }

   /**
    * Common resource calendar tests.
    *
    * @param mpx project file
    */
   private void validateResourceCalendars(ProjectFile mpx)
   {
      //
      // Resource calendar based on standard calendar
      //
      Resource resource = mpx.getResourceByUniqueID(Integer.valueOf(1));
      ProjectCalendar calendar = resource.getCalendar();
      assertEquals("Resource One", calendar.getName());
      assertTrue(calendar.isDerived());
      assertEquals("Standard", calendar.getParent().getName());
      assertTrue(calendar.getCalendarExceptions().isEmpty());

      //
      // Resource calendar based on base calendar
      //
      resource = mpx.getResourceByUniqueID(Integer.valueOf(2));
      calendar = resource.getCalendar();
      assertEquals("Resource Two", calendar.getName());
      assertTrue(calendar.isDerived());
      assertEquals("Base Calendar", calendar.getParent().getName());
      assertTrue(calendar.getCalendarExceptions().isEmpty());

      //
      // Resource calendar based on modified base calendar
      //
      resource = mpx.getResourceByUniqueID(Integer.valueOf(3));
      calendar = resource.getCalendar();
      assertEquals("Resource Three", calendar.getName());
      assertTrue(calendar.isDerived());
      assertEquals("Base Calendar", calendar.getParent().getName());
      assertFalse(calendar.getCalendarExceptions().isEmpty());
   }

   /**
    * Common task calendar tests.
    *
    * @param mpx project file
    */
   private void validateTaskCalendars(ProjectFile mpx)
   {
      Task task = mpx.getTaskByUniqueID(Integer.valueOf(2));
      ProjectCalendar calendar = task.getCalendar();
      assertNull(calendar);

      task = mpx.getTaskByUniqueID(Integer.valueOf(3));
      calendar = task.getCalendar();
      assertEquals("Standard", calendar.getName());
      assertFalse(calendar.isDerived());

      task = mpx.getTaskByUniqueID(Integer.valueOf(4));
      calendar = task.getCalendar();
      assertEquals("Base Calendar", calendar.getName());
      assertFalse(calendar.isDerived());
   }

   /**
    * Test to exercise task, resource, and assignment removal code.
    */
   @Test public void testRemoval() throws Exception
   {
      //
      // Load the file and validate the number of
      // tasks, resources, and assignments.
      //
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("legacy/remove.mpp"));
      assertEquals(10, mpp.getTasks().size());
      assertEquals(8, mpp.getResources().size());
      assertEquals(8, mpp.getResourceAssignments().size());

      //
      // Remove a task with no explicit assignments
      //
      Task task = mpp.getTaskByUniqueID(Integer.valueOf(1));
      assertEquals("Task One", task.getName());
      task.remove();
      assertEquals(9, mpp.getTasks().size());
      assertEquals(8, mpp.getResources().size());
      assertEquals(7, mpp.getResourceAssignments().size());

      //
      // Remove a resource with no assignments
      //
      Resource resource = mpp.getResourceByUniqueID(Integer.valueOf(1));
      assertEquals("Resource One", resource.getName());
      resource.remove();
      assertEquals(9, mpp.getTasks().size());
      assertEquals(7, mpp.getResources().size());
      assertEquals(7, mpp.getResourceAssignments().size());

      //
      // Remove a task with a single assignment
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals("Task Two", task.getName());
      task.remove();
      assertEquals(8, mpp.getTasks().size());
      assertEquals(7, mpp.getResources().size());
      assertEquals(6, mpp.getResourceAssignments().size());

      //
      // Remove a resource with a single assignment
      //
      resource = mpp.getResourceByUniqueID(Integer.valueOf(3));
      assertEquals("Resource Three", resource.getName());
      resource.remove();
      assertEquals(8, mpp.getTasks().size());
      assertEquals(6, mpp.getResources().size());
      assertEquals(5, mpp.getResourceAssignments().size());

      //
      // Remove an assignment
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(5));
      assertEquals("Task Five", task.getName());
      List<ResourceAssignment> assignments = task.getResourceAssignments();
      assertEquals(2, assignments.size());
      ResourceAssignment assignment = assignments.get(0);
      resource = assignment.getResource();
      assertEquals("Resource Six", resource.getName());
      assignments = resource.getTaskAssignments();
      assertEquals(1, assignments.size());
      assignment.remove();
      assignments = task.getResourceAssignments();
      assertEquals(1, assignments.size());
      assignments = resource.getTaskAssignments();
      assertEquals(0, assignments.size());
      assertEquals(8, mpp.getTasks().size());
      assertEquals(6, mpp.getResources().size());
      assertEquals(4, mpp.getResourceAssignments().size());

      //
      // Remove a task with child tasks - the child tasks will also be removed
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(8));
      assertEquals("Task Eight", task.getName());
      task.remove();
      assertEquals(6, mpp.getTasks().size());
      assertEquals(6, mpp.getResources().size());
      assertEquals(3, mpp.getResourceAssignments().size());

      //
      // As we have removed tasks and resources, call the synchronize methods
      // to generate ID sequences without gaps. This will allow MS Project
      // to display the tasks and resources without blank rows.
      //
      mpp.getTasks().renumberIDs();
      mpp.getResources().renumberIDs();

      //
      // Write the file and re-read it to ensure we get consistent results.
      //
      File out = Files.createTempFile("junit", ".mpx").toFile();
      new MPXWriter().write(mpp, out);

      ProjectFile mpx = new MPXReader().read(out);
      assertEquals(6, mpx.getTasks().size());
      assertEquals(6, mpx.getResources().size());
      assertEquals(3, mpx.getResourceAssignments().size());
      out.deleteOnExit();
   }

   /**
    * Basic rewrite test to exercise the MPX calendar exception read/write code.
    */
   @Test public void testProjectCalendarExceptions() throws Exception
   {
      File in = new File(MpxjTestData.filePath("legacy/calendarExceptions.mpx"));
      ProjectFile mpx = new MPXReader().read(in);
      File out = Files.createTempFile("junit", ".mpx").toFile();
      MPXWriter writer = new MPXWriter();
      writer.setUseLocaleDefaults(false);
      writer.write(mpx, out);
      boolean success = FileUtility.equals(in, out);
      assertTrue("Files are not identical", success);
      out.deleteOnExit();
   }
}
