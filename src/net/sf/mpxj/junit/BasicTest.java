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

package net.sf.mpxj.junit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Table;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.View;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class contains a small set of tests to exercise the MPXJ library.
 */
public class BasicTest extends MPXJTestCase
{
   /**
    * This method performs a simple data driven test to read then write
    * the contents of a single MPX file. Assuming the MPX file contains
    * at least one example of each type of record, this test will be able
    * to exercise a large part of the MPX library.
    */
   public void testRewrite1() throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File(m_basedir + "/sample.mpx");
         ProjectFile mpx = new MPXReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         MPXWriter writer = new MPXWriter();
         writer.setUseLocaleDefaults(false);
         writer.write(mpx, out);
         success = FileUtility.equals(in, out);
         assertTrue("Files are not identical", success);
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }

   /**
    * This method performs a simple data driven test to read then write
    * the contents of a single MPX file. Assuming the MPX file contains
    * at least one example of each type of record, this test will be able
    * to exercise a large part of the MPX library.
    */
   public void testRewrite2() throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File(m_basedir + "/sample1.xml");
         ProjectFile xml = new MSPDIReader().read(in);
         out = File.createTempFile("junit", ".xml");
         new MSPDIWriter().write(xml, out);
         success = FileUtility.equals(in, out);
         assertTrue("Files are not identical", success);
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }

   /**
    * This method performs a simple data driven test to read then write
    * the contents of a single MPX file. The difference between this test
    * and testRewrite1 is that the sample MPX file uses alternative
    * field separators, decimal separators and thousands separators.
    */
   public void testRewrite3() throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File(m_basedir + "/sample1.mpx");
         ProjectFile mpx = new MPXReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         MPXWriter writer = new MPXWriter();
         writer.setUseLocaleDefaults(false);
         writer.write(mpx, out);
         success = FileUtility.equals(in, out);
         assertTrue("Files are not identical", success);
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }

   /**
    * Test to ensure that files without tasks or resources generate
    * correct MPX files.
    * 
    * @throws Exception
    */
   public void testRewrite4() throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File(m_basedir + "/empty.mpp");
         ProjectFile mpx = new MPPReader().read(in);
         mpx.getProjectHeader().setCurrentDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/03/2006"));
         out = File.createTempFile("junit", ".mpx");
         MPXWriter writer = new MPXWriter();
         writer.setUseLocaleDefaults(false);
         writer.write(mpx, out);
         success = FileUtility.equals(new File(m_basedir + "/empty.mpx"), out);
         assertTrue("Files are not identical", success);
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }

   /**
    * Exercise PlannerWriter.
    * 
    * @throws Exception
    */
   public void testRewrite5() throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File(m_basedir + "/sample.mpx");
         ProjectFile mpx = new MPXReader().read(in);
         out = File.createTempFile("junit", ".planner");
         new PlannerWriter().write(mpx, out);
         //success = FileUtility.equals (in, out);
         //assertTrue ("Files are not identical", success);
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }

   /**
    * This test exercises the automatic generation of WBS and outline levels.
    */
   public void testAutomaticGeneration() throws Exception
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
      assertEquals(false, task1.getSummary());

      task1 = file.addTask();
      task1.setName("2.0");
      assertEquals("2", task1.getWBS());
      assertEquals(1, task1.getOutlineLevel().intValue());
      assertEquals("2", task1.getOutlineNumber());
      assertEquals(2, task1.getID().intValue());
      assertEquals(2, task1.getUniqueID().intValue());
      assertEquals(false, task1.getSummary());

      task1 = file.addTask();
      task1.setName("3.0");
      assertEquals("3", task1.getWBS());
      assertEquals(1, task1.getOutlineLevel().intValue());
      assertEquals("3", task1.getOutlineNumber());
      assertEquals(3, task1.getID().intValue());
      assertEquals(3, task1.getUniqueID().intValue());
      assertEquals(false, task1.getSummary());

      Task task2 = task1.addTask();
      task2.setName("3.1");
      assertEquals("3.1", task2.getWBS());
      assertEquals(2, task2.getOutlineLevel().intValue());
      assertEquals("3.1", task2.getOutlineNumber());
      assertEquals(4, task2.getID().intValue());
      assertEquals(4, task2.getUniqueID().intValue());
      assertEquals(true, task1.getSummary());
      assertEquals(false, task2.getSummary());

      task2 = task1.addTask();
      task2.setName("3.2");
      assertEquals("3.2", task2.getWBS());
      assertEquals(2, task2.getOutlineLevel().intValue());
      assertEquals("3.2", task2.getOutlineNumber());
      assertEquals(5, task2.getID().intValue());
      assertEquals(5, task2.getUniqueID().intValue());
      assertEquals(true, task1.getSummary());
      assertEquals(false, task2.getSummary());

      Task task3 = task2.addTask();
      task3.setName("3.2.1");
      assertEquals("3.2.1", task3.getWBS());
      assertEquals(3, task3.getOutlineLevel().intValue());
      assertEquals("3.2.1", task3.getOutlineNumber());
      assertEquals(6, task3.getID().intValue());
      assertEquals(6, task3.getUniqueID().intValue());
      assertEquals(true, task1.getSummary());
      assertEquals(true, task2.getSummary());
      assertEquals(false, task3.getSummary());

      task3 = task2.addTask();
      task3.setName("3.2.2");
      assertEquals("3.2.2", task3.getWBS());
      assertEquals(3, task3.getOutlineLevel().intValue());
      assertEquals("3.2.2", task3.getOutlineNumber());
      assertEquals(7, task3.getID().intValue());
      assertEquals(7, task3.getUniqueID().intValue());
      assertEquals(true, task1.getSummary());
      assertEquals(true, task2.getSummary());
      assertEquals(false, task3.getSummary());
   }

   /**
    * Test to ensure that the basic task hierarchy is
    * represented correctly.
    *
    * @throws Exception
    */
   public void testStructure() throws Exception
   {
      ProjectFile file = new ProjectFile();

      Task task1 = file.addTask();
      assertNull(task1.getParentTask());

      Task task2 = task1.addTask();
      assertEquals(task2.getParentTask(), task1);

      task1.addTask();
      List<Task> children = task1.getChildTasks();
      assertEquals(children.size(), 2);

      List<Task> toplevel = file.getChildTasks();
      assertEquals(toplevel.size(), 1);
   }

   /**
    * Exercise the MPP8 import code.
    */
   public void testConversion1() throws Exception
   {
      File out = null;

      try
      {
         File in = new File(m_basedir + "/sample98.mpp");
         ProjectFile mpp = new MPPReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpp, out);
         commonTests(mpp);
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Exercise the MPP9 import code.
    */
   public void testConversion2() throws Exception
   {
      File out = null;

      try
      {
         File in = new File(m_basedir + "/sample.mpp");
         ProjectFile mpp = new MPPReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpp, out);
         commonTests(mpp);
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Exercise the XML import code.
    */
   public void testConversion3() throws Exception
   {
      File out = null;

      try
      {
         File in = new File(m_basedir + "/sample.xml");
         ProjectFile xml = new MSPDIReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(xml, out);
         commonTests(xml);
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
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
         switch (NumberUtility.getInt(assignment.getResource().getID()))
         {
            case 1 :
            {
               assertEquals(200, (int) assignment.getRemainingWork().getDuration());
               assertEquals(TimeUnit.HOURS, assignment.getRemainingWork().getUnits());
               break;
            }

            case 2 :
            {
               assertEquals(300, (int) assignment.getRemainingWork().getDuration());
               assertEquals(TimeUnit.HOURS, assignment.getRemainingWork().getUnits());
               break;
            }

            default :
            {
               assertTrue("Unexpected resource", false);
               break;
            }
         }
      }
   }

   /**
    * This method tests two stages of conversion, MPP->MPX->MSPDI. This
    * has been designed to exercise bug 896189, which was exhibited
    * when an MSPDI file was generated from an MPX file which did not
    * have the same set of attributes as a native MPP file.
    *
    * @throws Exception
    */
   public void testConversion4() throws Exception
   {
      File out = null;

      try
      {
         File in = new File(m_basedir + "/sample.mpp");
         ProjectFile mpp = new MPPReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpp, out);

         ProjectFile mpx = new MPXReader().read(out);
         out.delete();
         out = File.createTempFile("junit", ".xml");
         new MSPDIWriter().write(mpx, out);
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Simple test to exercise iterating through the task predecessors.
    *
    * @throws Exception
    */
   public void testRelationList() throws Exception
   {
      File in = new File(m_basedir + "/sample.mpx");
      ProjectFile mpx = new MPXReader().read(in);

      for (Task task : mpx.getAllTasks())
      {
         List<Relation> rels = task.getPredecessors();
         if (rels != null)
         {
            for (Relation rel : rels)
            {
               mpx.getTaskByUniqueID(rel.getTargetTask().getUniqueID());
            }
         }
      }
   }

   /**
    * This method exercises task notes, ensuring that
    * embedded commas and quotes are handled correctly.
    *
    * @throws Exception
    */
   public void testTaskNotes() throws Exception
   {
      File out = null;

      try
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
         task1.setStart(new Date());
         task1.setNotes(notes1);

         Task task2 = file1.addTask();
         task2.setName("Test Task 2");
         task2.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
         task2.setStart(new Date());
         task2.setNotes(notes2);

         Task task3 = file1.addTask();
         task3.setName("Test Task 3");
         task3.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
         task3.setStart(new Date());
         task3.setNotes(notes3);

         Task task4 = file1.addTask();
         task4.setName("Test Task 4");
         task4.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
         task4.setStart(new Date());
         task4.setNotes(notes4);

         Task task5 = file1.addTask();
         task5.setName("Test Task 5");
         task5.setDuration(Duration.getInstance(10, TimeUnit.DAYS));
         task5.setStart(new Date());
         task5.setNotes(notes5);

         out = File.createTempFile("junit", ".mpx");
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
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * This method exercises resource notes, ensuring that
    * embedded commas and quotes are handled correctly.
    *
    * @throws Exception
    */
   public void testResourceNotes() throws Exception
   {
      File out = null;

      try
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

         out = File.createTempFile("junit", ".mpx");
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
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Read an MPP file that caused problems.
    */
   public void testBug1() throws Exception
   {
      File out = null;

      try
      {
         File in = new File(m_basedir + "/bug1.mpp");
         ProjectFile mpp = new MPPReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpp, out);
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Read an MPP file that caused problems.
    */
   public void testBug2() throws Exception
   {
      File out = null;

      try
      {
         File in = new File(m_basedir + "/bug2.mpp");
         ProjectFile mpp = new MPPReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpp, out);
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Read an MPP file where the structure was not being correctly
    * set up to reflect the outline level.
    */
   public void testBug3() throws Exception
   {
      File in = new File(m_basedir + "/bug3.mpp");
      ProjectFile mpp = new MPPReader().read(in);

      for (Task task : mpp.getAllTasks())
      {
         assertEquals("Outline levels do not match", task.getOutlineLevel().intValue(), calculateOutlineLevel(task));
      }
   }

   /**
    * Read an MPP8 file with a non-standard task fixed data block size.
    */
   public void testBug4() throws Exception
   {
      File out = null;

      try
      {
         File in = new File(m_basedir + "/bug4.mpp");
         ProjectFile mpp = new MPPReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpp, out.getAbsolutePath());
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
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
    *
    * @throws Exception
    */
   public void testMPP8Flags1() throws Exception
   {
      File in = new File(m_basedir + "/mpp8flags1.mpp");
      ProjectFile mpp = new MPPReader().read(in);
      List<Task> tasks = mpp.getAllTasks();
      assertTrue("Not enough tasks", (tasks.size() > 0));
      assertTrue("Not an even number of tasks", (tasks.size() % 2 == 0));

      Iterator<Task> iter = tasks.iterator();
      Task task;
      while (iter.hasNext())
      {
         task = iter.next();
         assertFalse(task.getName(), task.getFlag1());
         assertFalse(task.getName(), task.getFlag2());
         assertFalse(task.getName(), task.getFlag3());
         assertFalse(task.getName(), task.getFlag4());
         assertFalse(task.getName(), task.getFlag5());
         assertFalse(task.getName(), task.getFlag6());
         assertFalse(task.getName(), task.getFlag7());
         assertFalse(task.getName(), task.getFlag8());
         assertFalse(task.getName(), task.getFlag9());
         assertFalse(task.getName(), task.getFlag10());
         assertFalse(task.getName(), task.getFlag11());
         assertFalse(task.getName(), task.getFlag12());
         assertFalse(task.getName(), task.getFlag13());
         assertFalse(task.getName(), task.getFlag14());
         assertFalse(task.getName(), task.getFlag15());
         assertFalse(task.getName(), task.getFlag16());
         assertFalse(task.getName(), task.getFlag17());
         assertFalse(task.getName(), task.getFlag18());
         assertFalse(task.getName(), task.getFlag19());
         //assertFalse(task.getName(), task.getFlag20());

         task = iter.next();
         assertTrue(task.getName(), task.getFlag1());
         assertTrue(task.getName(), task.getFlag2());
         assertTrue(task.getName(), task.getFlag3());
         assertTrue(task.getName(), task.getFlag4());
         assertTrue(task.getName(), task.getFlag5());
         assertTrue(task.getName(), task.getFlag6());
         assertTrue(task.getName(), task.getFlag7());
         assertTrue(task.getName(), task.getFlag8());
         assertTrue(task.getName(), task.getFlag9());
         assertTrue(task.getName(), task.getFlag10());
         assertTrue(task.getName(), task.getFlag11());
         assertTrue(task.getName(), task.getFlag12());
         assertTrue(task.getName(), task.getFlag13());
         assertTrue(task.getName(), task.getFlag14());
         assertTrue(task.getName(), task.getFlag15());
         assertTrue(task.getName(), task.getFlag16());
         assertTrue(task.getName(), task.getFlag17());
         assertTrue(task.getName(), task.getFlag18());
         assertTrue(task.getName(), task.getFlag19());
         //assertTrue(task.getName(), task.getFlag20());
      }
   }

   /**
    * This test reads flags from an MPP8 file where each set of 20 tasks has
    * a single flag from 1-20 set. The next set of 20 tasks increases by
    * one outline level.
    *
    * @throws Exception
    */
   public void testMPP8Flags2() throws Exception
   {
      File in = new File(m_basedir + "/mpp8flags2.mpp");
      ProjectFile mpp = new MPPReader().read(in);
      int index = 0;
      boolean[] flags;

      for (Task task : mpp.getAllTasks())
      {
         if (task.getName().startsWith("Parent") == false)
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
    *
    * @throws Exception
    */
   public void testMPP9Flags1() throws Exception
   {
      File in = new File(m_basedir + "/mpp9flags1.mpp");
      ProjectFile mpp = new MPPReader().read(in);
      Task parentTask = mpp.getTaskByID(Integer.valueOf(0));
      assertNotNull("Parent task missing", parentTask);
      List<Task> tasks = parentTask.getChildTasks();
      assertTrue("Not enough tasks", (tasks.size() > 0));
      assertTrue("Not an even number of tasks", (tasks.size() % 2 == 0));

      Iterator<Task> iter = tasks.iterator();
      Task task;
      while (iter.hasNext())
      {
         task = iter.next();
         assertFalse(task.getName(), task.getFlag1());
         assertFalse(task.getName(), task.getFlag2());
         assertFalse(task.getName(), task.getFlag3());
         assertFalse(task.getName(), task.getFlag4());
         assertFalse(task.getName(), task.getFlag5());
         assertFalse(task.getName(), task.getFlag6());
         assertFalse(task.getName(), task.getFlag7());
         assertFalse(task.getName(), task.getFlag8());
         assertFalse(task.getName(), task.getFlag9());
         assertFalse(task.getName(), task.getFlag10());
         assertFalse(task.getName(), task.getFlag11());
         assertFalse(task.getName(), task.getFlag12());
         assertFalse(task.getName(), task.getFlag13());
         assertFalse(task.getName(), task.getFlag14());
         assertFalse(task.getName(), task.getFlag15());
         assertFalse(task.getName(), task.getFlag16());
         assertFalse(task.getName(), task.getFlag17());
         assertFalse(task.getName(), task.getFlag18());
         assertFalse(task.getName(), task.getFlag19());
         assertFalse(task.getName(), task.getFlag20());

         task = iter.next();
         assertTrue(task.getName(), task.getFlag1());
         assertTrue(task.getName(), task.getFlag2());
         assertTrue(task.getName(), task.getFlag3());
         assertTrue(task.getName(), task.getFlag4());
         assertTrue(task.getName(), task.getFlag5());
         assertTrue(task.getName(), task.getFlag6());
         assertTrue(task.getName(), task.getFlag7());
         assertTrue(task.getName(), task.getFlag8());
         assertTrue(task.getName(), task.getFlag9());
         assertTrue(task.getName(), task.getFlag10());
         assertTrue(task.getName(), task.getFlag11());
         assertTrue(task.getName(), task.getFlag12());
         assertTrue(task.getName(), task.getFlag13());
         assertTrue(task.getName(), task.getFlag14());
         assertTrue(task.getName(), task.getFlag15());
         assertTrue(task.getName(), task.getFlag16());
         assertTrue(task.getName(), task.getFlag17());
         assertTrue(task.getName(), task.getFlag18());
         assertTrue(task.getName(), task.getFlag19());
         assertTrue(task.getName(), task.getFlag20());
      }
   }

   /**
    * This test reads flags from an MPP9 file where each set of 20 tasks has
    * a single flag from 1-20 set. The next set of 20 tasks increases by
    * one outline level.
    *
    * @throws Exception
    */
   public void testMPP9Flags2() throws Exception
   {
      File in = new File(m_basedir + "/mpp8flags2.mpp");
      ProjectFile mpp = new MPPReader().read(in);
      int index = 0;
      boolean[] flags;

      for (Task task : mpp.getAllTasks())
      {
         if (task.getName().startsWith("Parent") == false)
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

      flags[0] = task.getFlag1();
      flags[1] = task.getFlag2();
      flags[2] = task.getFlag3();
      flags[3] = task.getFlag4();
      flags[4] = task.getFlag5();
      flags[5] = task.getFlag6();
      flags[6] = task.getFlag7();
      flags[7] = task.getFlag8();
      flags[8] = task.getFlag9();
      flags[9] = task.getFlag10();
      flags[10] = task.getFlag11();
      flags[11] = task.getFlag12();
      flags[12] = task.getFlag13();
      flags[13] = task.getFlag14();
      flags[14] = task.getFlag15();
      flags[15] = task.getFlag16();
      flags[16] = task.getFlag17();
      flags[17] = task.getFlag18();
      flags[18] = task.getFlag19();
      flags[19] = task.getFlag20();

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
         if (flags[loop] == true && loop != index)
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
    *
    * @throws Exception
    */
   public void testViews() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/sample98.mpp");
      List<View> views = mpp.getViews();
      assertEquals("Incorrect number of views", 1, views.size());

      mpp = new MPPReader().read(m_basedir + "/sample.mpp");
      views = mpp.getViews();
      assertEquals("Incorrect number of views", 3, views.size());
   }

   /**
    * Test retrieval of table information.
    *
    * @throws Exception
    */
   public void testTables() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/sample98.mpp");
      List<Table> tables = mpp.getTables();
      //      Iterator iter = tables.iterator();
      //      while (iter.hasNext() == true)
      //      {
      //         System.out.println(iter.next());
      //      }

      assertEquals("Incorrect number of tables", 1, tables.size());

      mpp = new MPPReader().read(m_basedir + "/sample.mpp");
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
    *
    * @throws Exception
    */
   public void testTaskCalendars() throws Exception
   {
      File out = null;

      try
      {
         //
         // Read in the MPP file. The task names should
         // match the calendar names.
         //
         File in = new File(m_basedir + "/sample1.mpp");
         ProjectFile mpp = new MPPReader().read(in);
         ProjectCalendar cal;

         for (Task task : mpp.getAllTasks())
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
         out = File.createTempFile("junit", ".xml");
         new MSPDIWriter().write(mpp, out);

         //
         // Read the MSPDI file in again, and check the
         // calendar names to ensure consistency
         //
         ProjectFile mspdi = new MSPDIReader().read(out.getCanonicalPath());
         for (Task task : mspdi.getAllTasks())
         {
            cal = task.getCalendar();
            if (cal != null)
            {
               assertEquals(task.getName(), cal.getName());
            }
         }
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Exercise field alias code for MSPDI files.
    *
    * @throws Exception
    */
   public void testMSPDIAliases() throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         MSPDIReader reader = new MSPDIReader();
         MSPDIWriter writer = new MSPDIWriter();

         File in = new File(m_basedir + "/alias.xml");
         ProjectFile xml = reader.read(in);
         validateAliases(xml);

         out = File.createTempFile("junit", ".xml");
         writer.write(xml, out);

         xml = reader.read(out);
         validateAliases(xml);

         success = true;
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }

   /**
    * Exercise field alias code for MPP9 files.
    *
    * @throws Exception
    */
   public void testMPP9Aliases() throws Exception
   {
      File in = new File(m_basedir + "/alias.mpp");
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
      assertEquals("Text1t", mpx.getTaskFieldAlias(TaskField.TEXT1));
      assertEquals("Text2t", mpx.getTaskFieldAlias(TaskField.TEXT2));
      assertEquals("Text3t", mpx.getTaskFieldAlias(TaskField.TEXT3));
      assertEquals("Text4t", mpx.getTaskFieldAlias(TaskField.TEXT4));
      assertEquals("Text5t", mpx.getTaskFieldAlias(TaskField.TEXT5));
      assertEquals("Text6t", mpx.getTaskFieldAlias(TaskField.TEXT6));
      assertEquals("Text7t", mpx.getTaskFieldAlias(TaskField.TEXT7));
      assertEquals("Text8t", mpx.getTaskFieldAlias(TaskField.TEXT8));
      assertEquals("Text9t", mpx.getTaskFieldAlias(TaskField.TEXT9));
      assertEquals("Text10t", mpx.getTaskFieldAlias(TaskField.TEXT10));
      assertEquals("Text11t", mpx.getTaskFieldAlias(TaskField.TEXT11));
      assertEquals("Text12t", mpx.getTaskFieldAlias(TaskField.TEXT12));
      assertEquals("Text13t", mpx.getTaskFieldAlias(TaskField.TEXT13));
      assertEquals("Text14t", mpx.getTaskFieldAlias(TaskField.TEXT14));
      assertEquals("Text15t", mpx.getTaskFieldAlias(TaskField.TEXT15));
      assertEquals("Text16t", mpx.getTaskFieldAlias(TaskField.TEXT16));
      assertEquals("Text17t", mpx.getTaskFieldAlias(TaskField.TEXT17));
      assertEquals("Text18t", mpx.getTaskFieldAlias(TaskField.TEXT18));
      assertEquals("Text19t", mpx.getTaskFieldAlias(TaskField.TEXT19));
      assertEquals("Text20t", mpx.getTaskFieldAlias(TaskField.TEXT20));
      assertEquals("Text21t", mpx.getTaskFieldAlias(TaskField.TEXT21));
      assertEquals("Text22t", mpx.getTaskFieldAlias(TaskField.TEXT22));
      assertEquals("Text23t", mpx.getTaskFieldAlias(TaskField.TEXT23));
      assertEquals("Text24t", mpx.getTaskFieldAlias(TaskField.TEXT24));
      assertEquals("Text25t", mpx.getTaskFieldAlias(TaskField.TEXT25));
      assertEquals("Text26t", mpx.getTaskFieldAlias(TaskField.TEXT26));
      assertEquals("Text27t", mpx.getTaskFieldAlias(TaskField.TEXT27));
      assertEquals("Text28t", mpx.getTaskFieldAlias(TaskField.TEXT28));
      assertEquals("Text29t", mpx.getTaskFieldAlias(TaskField.TEXT29));
      assertEquals("Text30t", mpx.getTaskFieldAlias(TaskField.TEXT30));
      assertEquals("Start1t", mpx.getTaskFieldAlias(TaskField.START1));
      assertEquals("Start2t", mpx.getTaskFieldAlias(TaskField.START2));
      assertEquals("Start3t", mpx.getTaskFieldAlias(TaskField.START3));
      assertEquals("Start4t", mpx.getTaskFieldAlias(TaskField.START4));
      assertEquals("Start5t", mpx.getTaskFieldAlias(TaskField.START5));
      assertEquals("Start6t", mpx.getTaskFieldAlias(TaskField.START6));
      assertEquals("Start7t", mpx.getTaskFieldAlias(TaskField.START7));
      assertEquals("Start8t", mpx.getTaskFieldAlias(TaskField.START8));
      assertEquals("Start9t", mpx.getTaskFieldAlias(TaskField.START9));
      assertEquals("Start10t", mpx.getTaskFieldAlias(TaskField.START10));
      assertEquals("Finish1t", mpx.getTaskFieldAlias(TaskField.FINISH1));
      assertEquals("Finish2t", mpx.getTaskFieldAlias(TaskField.FINISH2));
      assertEquals("Finish3t", mpx.getTaskFieldAlias(TaskField.FINISH3));
      assertEquals("Finish4t", mpx.getTaskFieldAlias(TaskField.FINISH4));
      assertEquals("Finish5t", mpx.getTaskFieldAlias(TaskField.FINISH5));
      assertEquals("Finish6t", mpx.getTaskFieldAlias(TaskField.FINISH6));
      assertEquals("Finish7t", mpx.getTaskFieldAlias(TaskField.FINISH7));
      assertEquals("Finish8t", mpx.getTaskFieldAlias(TaskField.FINISH8));
      assertEquals("Finish9t", mpx.getTaskFieldAlias(TaskField.FINISH9));
      assertEquals("Finish10t", mpx.getTaskFieldAlias(TaskField.FINISH10));
      assertEquals("Cost1t", mpx.getTaskFieldAlias(TaskField.COST1));
      assertEquals("Cost2t", mpx.getTaskFieldAlias(TaskField.COST2));
      assertEquals("Cost3t", mpx.getTaskFieldAlias(TaskField.COST3));
      assertEquals("Cost4t", mpx.getTaskFieldAlias(TaskField.COST4));
      assertEquals("Cost5t", mpx.getTaskFieldAlias(TaskField.COST5));
      assertEquals("Cost6t", mpx.getTaskFieldAlias(TaskField.COST6));
      assertEquals("Cost7t", mpx.getTaskFieldAlias(TaskField.COST7));
      assertEquals("Cost8t", mpx.getTaskFieldAlias(TaskField.COST8));
      assertEquals("Cost9t", mpx.getTaskFieldAlias(TaskField.COST9));
      assertEquals("Cost10t", mpx.getTaskFieldAlias(TaskField.COST10));
      assertEquals("Date1t", mpx.getTaskFieldAlias(TaskField.DATE1));
      assertEquals("Date2t", mpx.getTaskFieldAlias(TaskField.DATE2));
      assertEquals("Date3t", mpx.getTaskFieldAlias(TaskField.DATE3));
      assertEquals("Date4t", mpx.getTaskFieldAlias(TaskField.DATE4));
      assertEquals("Date5t", mpx.getTaskFieldAlias(TaskField.DATE5));
      assertEquals("Date6t", mpx.getTaskFieldAlias(TaskField.DATE6));
      assertEquals("Date7t", mpx.getTaskFieldAlias(TaskField.DATE7));
      assertEquals("Date8t", mpx.getTaskFieldAlias(TaskField.DATE8));
      assertEquals("Date9t", mpx.getTaskFieldAlias(TaskField.DATE9));
      assertEquals("Date10t", mpx.getTaskFieldAlias(TaskField.DATE10));
      assertEquals("Flag1t", mpx.getTaskFieldAlias(TaskField.FLAG1));
      assertEquals("Flag2t", mpx.getTaskFieldAlias(TaskField.FLAG2));
      assertEquals("Flag3t", mpx.getTaskFieldAlias(TaskField.FLAG3));
      assertEquals("Flag4t", mpx.getTaskFieldAlias(TaskField.FLAG4));
      assertEquals("Flag5t", mpx.getTaskFieldAlias(TaskField.FLAG5));
      assertEquals("Flag6t", mpx.getTaskFieldAlias(TaskField.FLAG6));
      assertEquals("Flag7t", mpx.getTaskFieldAlias(TaskField.FLAG7));
      assertEquals("Flag8t", mpx.getTaskFieldAlias(TaskField.FLAG8));
      assertEquals("Flag9t", mpx.getTaskFieldAlias(TaskField.FLAG9));
      assertEquals("Flag10t", mpx.getTaskFieldAlias(TaskField.FLAG10));
      assertEquals("Flag11t", mpx.getTaskFieldAlias(TaskField.FLAG11));
      assertEquals("Flag12t", mpx.getTaskFieldAlias(TaskField.FLAG12));
      assertEquals("Flag13t", mpx.getTaskFieldAlias(TaskField.FLAG13));
      assertEquals("Flag14t", mpx.getTaskFieldAlias(TaskField.FLAG14));
      assertEquals("Flag15t", mpx.getTaskFieldAlias(TaskField.FLAG15));
      assertEquals("Flag16t", mpx.getTaskFieldAlias(TaskField.FLAG16));
      assertEquals("Flag17t", mpx.getTaskFieldAlias(TaskField.FLAG17));
      assertEquals("Flag18t", mpx.getTaskFieldAlias(TaskField.FLAG18));
      assertEquals("Flag19t", mpx.getTaskFieldAlias(TaskField.FLAG19));
      assertEquals("Flag20t", mpx.getTaskFieldAlias(TaskField.FLAG20));
      assertEquals("Number1t", mpx.getTaskFieldAlias(TaskField.NUMBER1));
      assertEquals("Number2t", mpx.getTaskFieldAlias(TaskField.NUMBER2));
      assertEquals("Number3t", mpx.getTaskFieldAlias(TaskField.NUMBER3));
      assertEquals("Number4t", mpx.getTaskFieldAlias(TaskField.NUMBER4));
      assertEquals("Number5t", mpx.getTaskFieldAlias(TaskField.NUMBER5));
      assertEquals("Number6t", mpx.getTaskFieldAlias(TaskField.NUMBER6));
      assertEquals("Number7t", mpx.getTaskFieldAlias(TaskField.NUMBER7));
      assertEquals("Number8t", mpx.getTaskFieldAlias(TaskField.NUMBER8));
      assertEquals("Number9t", mpx.getTaskFieldAlias(TaskField.NUMBER9));
      assertEquals("Number10t", mpx.getTaskFieldAlias(TaskField.NUMBER10));
      assertEquals("Number11t", mpx.getTaskFieldAlias(TaskField.NUMBER11));
      assertEquals("Number12t", mpx.getTaskFieldAlias(TaskField.NUMBER12));
      assertEquals("Number13t", mpx.getTaskFieldAlias(TaskField.NUMBER13));
      assertEquals("Number14t", mpx.getTaskFieldAlias(TaskField.NUMBER14));
      assertEquals("Number15t", mpx.getTaskFieldAlias(TaskField.NUMBER15));
      assertEquals("Number16t", mpx.getTaskFieldAlias(TaskField.NUMBER16));
      assertEquals("Number17t", mpx.getTaskFieldAlias(TaskField.NUMBER17));
      assertEquals("Number18t", mpx.getTaskFieldAlias(TaskField.NUMBER18));
      assertEquals("Number19t", mpx.getTaskFieldAlias(TaskField.NUMBER19));
      assertEquals("Number20t", mpx.getTaskFieldAlias(TaskField.NUMBER20));
      assertEquals("Duration1t", mpx.getTaskFieldAlias(TaskField.DURATION1));
      assertEquals("Duration2t", mpx.getTaskFieldAlias(TaskField.DURATION2));
      assertEquals("Duration3t", mpx.getTaskFieldAlias(TaskField.DURATION3));
      assertEquals("Duration4t", mpx.getTaskFieldAlias(TaskField.DURATION4));
      assertEquals("Duration5t", mpx.getTaskFieldAlias(TaskField.DURATION5));
      assertEquals("Duration6t", mpx.getTaskFieldAlias(TaskField.DURATION6));
      assertEquals("Duration7t", mpx.getTaskFieldAlias(TaskField.DURATION7));
      assertEquals("Duration8t", mpx.getTaskFieldAlias(TaskField.DURATION8));
      assertEquals("Duration9t", mpx.getTaskFieldAlias(TaskField.DURATION9));
      assertEquals("Duration10t", mpx.getTaskFieldAlias(TaskField.DURATION10));
      assertEquals("Outline Code1t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE1));
      assertEquals("Outline Code2t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE2));
      assertEquals("Outline Code3t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE3));
      assertEquals("Outline Code4t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE4));
      assertEquals("Outline Code5t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE5));
      assertEquals("Outline Code6t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE6));
      assertEquals("Outline Code7t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE7));
      assertEquals("Outline Code8t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE8));
      assertEquals("Outline Code9t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE9));
      assertEquals("Outline Code10t", mpx.getTaskFieldAlias(TaskField.OUTLINE_CODE10));

      assertEquals("Text1r", mpx.getResourceFieldAlias(ResourceField.TEXT1));
      assertEquals("Text2r", mpx.getResourceFieldAlias(ResourceField.TEXT2));
      assertEquals("Text3r", mpx.getResourceFieldAlias(ResourceField.TEXT3));
      assertEquals("Text4r", mpx.getResourceFieldAlias(ResourceField.TEXT4));
      assertEquals("Text5r", mpx.getResourceFieldAlias(ResourceField.TEXT5));
      assertEquals("Text6r", mpx.getResourceFieldAlias(ResourceField.TEXT6));
      assertEquals("Text7r", mpx.getResourceFieldAlias(ResourceField.TEXT7));
      assertEquals("Text8r", mpx.getResourceFieldAlias(ResourceField.TEXT8));
      assertEquals("Text9r", mpx.getResourceFieldAlias(ResourceField.TEXT9));
      assertEquals("Text10r", mpx.getResourceFieldAlias(ResourceField.TEXT10));
      assertEquals("Text11r", mpx.getResourceFieldAlias(ResourceField.TEXT11));
      assertEquals("Text12r", mpx.getResourceFieldAlias(ResourceField.TEXT12));
      assertEquals("Text13r", mpx.getResourceFieldAlias(ResourceField.TEXT13));
      assertEquals("Text14r", mpx.getResourceFieldAlias(ResourceField.TEXT14));
      assertEquals("Text15r", mpx.getResourceFieldAlias(ResourceField.TEXT15));
      assertEquals("Text16r", mpx.getResourceFieldAlias(ResourceField.TEXT16));
      assertEquals("Text17r", mpx.getResourceFieldAlias(ResourceField.TEXT17));
      assertEquals("Text18r", mpx.getResourceFieldAlias(ResourceField.TEXT18));
      assertEquals("Text19r", mpx.getResourceFieldAlias(ResourceField.TEXT19));
      assertEquals("Text20r", mpx.getResourceFieldAlias(ResourceField.TEXT20));
      assertEquals("Text21r", mpx.getResourceFieldAlias(ResourceField.TEXT21));
      assertEquals("Text22r", mpx.getResourceFieldAlias(ResourceField.TEXT22));
      assertEquals("Text23r", mpx.getResourceFieldAlias(ResourceField.TEXT23));
      assertEquals("Text24r", mpx.getResourceFieldAlias(ResourceField.TEXT24));
      assertEquals("Text25r", mpx.getResourceFieldAlias(ResourceField.TEXT25));
      assertEquals("Text26r", mpx.getResourceFieldAlias(ResourceField.TEXT26));
      assertEquals("Text27r", mpx.getResourceFieldAlias(ResourceField.TEXT27));
      assertEquals("Text28r", mpx.getResourceFieldAlias(ResourceField.TEXT28));
      assertEquals("Text29r", mpx.getResourceFieldAlias(ResourceField.TEXT29));
      assertEquals("Text30r", mpx.getResourceFieldAlias(ResourceField.TEXT30));
      assertEquals("Start1r", mpx.getResourceFieldAlias(ResourceField.START1));
      assertEquals("Start2r", mpx.getResourceFieldAlias(ResourceField.START2));
      assertEquals("Start3r", mpx.getResourceFieldAlias(ResourceField.START3));
      assertEquals("Start4r", mpx.getResourceFieldAlias(ResourceField.START4));
      assertEquals("Start5r", mpx.getResourceFieldAlias(ResourceField.START5));
      assertEquals("Start6r", mpx.getResourceFieldAlias(ResourceField.START6));
      assertEquals("Start7r", mpx.getResourceFieldAlias(ResourceField.START7));
      assertEquals("Start8r", mpx.getResourceFieldAlias(ResourceField.START8));
      assertEquals("Start9r", mpx.getResourceFieldAlias(ResourceField.START9));
      assertEquals("Start10r", mpx.getResourceFieldAlias(ResourceField.START10));
      assertEquals("Finish1r", mpx.getResourceFieldAlias(ResourceField.FINISH1));
      assertEquals("Finish2r", mpx.getResourceFieldAlias(ResourceField.FINISH2));
      assertEquals("Finish3r", mpx.getResourceFieldAlias(ResourceField.FINISH3));
      assertEquals("Finish4r", mpx.getResourceFieldAlias(ResourceField.FINISH4));
      assertEquals("Finish5r", mpx.getResourceFieldAlias(ResourceField.FINISH5));
      assertEquals("Finish6r", mpx.getResourceFieldAlias(ResourceField.FINISH6));
      assertEquals("Finish7r", mpx.getResourceFieldAlias(ResourceField.FINISH7));
      assertEquals("Finish8r", mpx.getResourceFieldAlias(ResourceField.FINISH8));
      assertEquals("Finish9r", mpx.getResourceFieldAlias(ResourceField.FINISH9));
      assertEquals("Finish10r", mpx.getResourceFieldAlias(ResourceField.FINISH10));
      assertEquals("Cost1r", mpx.getResourceFieldAlias(ResourceField.COST1));
      assertEquals("Cost2r", mpx.getResourceFieldAlias(ResourceField.COST2));
      assertEquals("Cost3r", mpx.getResourceFieldAlias(ResourceField.COST3));
      assertEquals("Cost4r", mpx.getResourceFieldAlias(ResourceField.COST4));
      assertEquals("Cost5r", mpx.getResourceFieldAlias(ResourceField.COST5));
      assertEquals("Cost6r", mpx.getResourceFieldAlias(ResourceField.COST6));
      assertEquals("Cost7r", mpx.getResourceFieldAlias(ResourceField.COST7));
      assertEquals("Cost8r", mpx.getResourceFieldAlias(ResourceField.COST8));
      assertEquals("Cost9r", mpx.getResourceFieldAlias(ResourceField.COST9));
      assertEquals("Cost10r", mpx.getResourceFieldAlias(ResourceField.COST10));
      assertEquals("Date1r", mpx.getResourceFieldAlias(ResourceField.DATE1));
      assertEquals("Date2r", mpx.getResourceFieldAlias(ResourceField.DATE2));
      assertEquals("Date3r", mpx.getResourceFieldAlias(ResourceField.DATE3));
      assertEquals("Date4r", mpx.getResourceFieldAlias(ResourceField.DATE4));
      assertEquals("Date5r", mpx.getResourceFieldAlias(ResourceField.DATE5));
      assertEquals("Date6r", mpx.getResourceFieldAlias(ResourceField.DATE6));
      assertEquals("Date7r", mpx.getResourceFieldAlias(ResourceField.DATE7));
      assertEquals("Date8r", mpx.getResourceFieldAlias(ResourceField.DATE8));
      assertEquals("Date9r", mpx.getResourceFieldAlias(ResourceField.DATE9));
      assertEquals("Date10r", mpx.getResourceFieldAlias(ResourceField.DATE10));
      assertEquals("Flag1r", mpx.getResourceFieldAlias(ResourceField.FLAG1));
      assertEquals("Flag2r", mpx.getResourceFieldAlias(ResourceField.FLAG2));
      assertEquals("Flag3r", mpx.getResourceFieldAlias(ResourceField.FLAG3));
      assertEquals("Flag4r", mpx.getResourceFieldAlias(ResourceField.FLAG4));
      assertEquals("Flag5r", mpx.getResourceFieldAlias(ResourceField.FLAG5));
      assertEquals("Flag6r", mpx.getResourceFieldAlias(ResourceField.FLAG6));
      assertEquals("Flag7r", mpx.getResourceFieldAlias(ResourceField.FLAG7));
      assertEquals("Flag8r", mpx.getResourceFieldAlias(ResourceField.FLAG8));
      assertEquals("Flag9r", mpx.getResourceFieldAlias(ResourceField.FLAG9));
      assertEquals("Flag10r", mpx.getResourceFieldAlias(ResourceField.FLAG10));
      assertEquals("Flag11r", mpx.getResourceFieldAlias(ResourceField.FLAG11));
      assertEquals("Flag12r", mpx.getResourceFieldAlias(ResourceField.FLAG12));
      assertEquals("Flag13r", mpx.getResourceFieldAlias(ResourceField.FLAG13));
      assertEquals("Flag14r", mpx.getResourceFieldAlias(ResourceField.FLAG14));
      assertEquals("Flag15r", mpx.getResourceFieldAlias(ResourceField.FLAG15));
      assertEquals("Flag16r", mpx.getResourceFieldAlias(ResourceField.FLAG16));
      assertEquals("Flag17r", mpx.getResourceFieldAlias(ResourceField.FLAG17));
      assertEquals("Flag18r", mpx.getResourceFieldAlias(ResourceField.FLAG18));
      assertEquals("Flag19r", mpx.getResourceFieldAlias(ResourceField.FLAG19));
      assertEquals("Flag20r", mpx.getResourceFieldAlias(ResourceField.FLAG20));
      assertEquals("Number1r", mpx.getResourceFieldAlias(ResourceField.NUMBER1));
      assertEquals("Number2r", mpx.getResourceFieldAlias(ResourceField.NUMBER2));
      assertEquals("Number3r", mpx.getResourceFieldAlias(ResourceField.NUMBER3));
      assertEquals("Number4r", mpx.getResourceFieldAlias(ResourceField.NUMBER4));
      assertEquals("Number5r", mpx.getResourceFieldAlias(ResourceField.NUMBER5));
      assertEquals("Number6r", mpx.getResourceFieldAlias(ResourceField.NUMBER6));
      assertEquals("Number7r", mpx.getResourceFieldAlias(ResourceField.NUMBER7));
      assertEquals("Number8r", mpx.getResourceFieldAlias(ResourceField.NUMBER8));
      assertEquals("Number9r", mpx.getResourceFieldAlias(ResourceField.NUMBER9));
      assertEquals("Number10r", mpx.getResourceFieldAlias(ResourceField.NUMBER10));
      assertEquals("Number11r", mpx.getResourceFieldAlias(ResourceField.NUMBER11));
      assertEquals("Number12r", mpx.getResourceFieldAlias(ResourceField.NUMBER12));
      assertEquals("Number13r", mpx.getResourceFieldAlias(ResourceField.NUMBER13));
      assertEquals("Number14r", mpx.getResourceFieldAlias(ResourceField.NUMBER14));
      assertEquals("Number15r", mpx.getResourceFieldAlias(ResourceField.NUMBER15));
      assertEquals("Number16r", mpx.getResourceFieldAlias(ResourceField.NUMBER16));
      assertEquals("Number17r", mpx.getResourceFieldAlias(ResourceField.NUMBER17));
      assertEquals("Number18r", mpx.getResourceFieldAlias(ResourceField.NUMBER18));
      assertEquals("Number19r", mpx.getResourceFieldAlias(ResourceField.NUMBER19));
      assertEquals("Number20r", mpx.getResourceFieldAlias(ResourceField.NUMBER20));
      assertEquals("Duration1r", mpx.getResourceFieldAlias(ResourceField.DURATION1));
      assertEquals("Duration2r", mpx.getResourceFieldAlias(ResourceField.DURATION2));
      assertEquals("Duration3r", mpx.getResourceFieldAlias(ResourceField.DURATION3));
      assertEquals("Duration4r", mpx.getResourceFieldAlias(ResourceField.DURATION4));
      assertEquals("Duration5r", mpx.getResourceFieldAlias(ResourceField.DURATION5));
      assertEquals("Duration6r", mpx.getResourceFieldAlias(ResourceField.DURATION6));
      assertEquals("Duration7r", mpx.getResourceFieldAlias(ResourceField.DURATION7));
      assertEquals("Duration8r", mpx.getResourceFieldAlias(ResourceField.DURATION8));
      assertEquals("Duration9r", mpx.getResourceFieldAlias(ResourceField.DURATION9));
      assertEquals("Duration10r", mpx.getResourceFieldAlias(ResourceField.DURATION10));
      assertEquals("Outline Code1r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE1));
      assertEquals("Outline Code2r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE2));
      assertEquals("Outline Code3r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE3));
      assertEquals("Outline Code4r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE4));
      assertEquals("Outline Code5r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE5));
      assertEquals("Outline Code6r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE6));
      assertEquals("Outline Code7r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE7));
      assertEquals("Outline Code8r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE8));
      assertEquals("Outline Code9r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE9));
      assertEquals("Outline Code10r", mpx.getResourceFieldAlias(ResourceField.OUTLINE_CODE10));
   }

   /**
    * Write a file with embedded line break (\r and \n) characters in
    * various text fields. Ensure that a valid file is written,
    * and that it can be read successfully.
    *
    * @throws Exception
    */
   public void testEmbeddedLineBreaks() throws Exception
   {
      File out = null;
      boolean success = false;

      try
      {
         //
         // Create a simple MPX file
         //
         SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
         ProjectFile file = new ProjectFile();
         file.addDefaultBaseCalendar();

         ProjectHeader header = file.getProjectHeader();
         header.setComments("Project Header Comments: Some\rExample\nText\r\nWith\n\rBreaks");
         header.setStartDate(df.parse("01/01/2003"));

         Resource resource1 = file.addResource();
         resource1.setName("Resource1: Some\rExample\nText\r\nWith\n\rBreaks");
         resource1.setNotes("Resource1 Notes: Some\rExample\nText\r\nWith\n\rBreaks");

         Task task1 = file.addTask();
         task1.setName("Task1: Some\rExample\nText\r\nWith\n\rBreaks");
         task1.setNotes("Task1 Notes: Some\rExample\nText\r\nWith\n\rBreaks");

         //
         // Write the file
         //
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(file, out);

         //
         // Ensure we can read it successfully
         //
         file = new MPXReader().read(out);
         assertEquals(1, file.getAllTasks().size());
         assertEquals(1, file.getAllResources().size());

         header = file.getProjectHeader();
         assertEquals("Project Header Comments: Some\nExample\nText\nWith\nBreaks", header.getComments());

         task1 = file.getTaskByUniqueID(Integer.valueOf(1));
         assertEquals("Task1: Some\nExample\nText\nWith\nBreaks", task1.getName());
         assertEquals("Task1 Notes: Some\nExample\nText\nWith\nBreaks", task1.getNotes());

         resource1 = file.getResourceByUniqueID(Integer.valueOf(1));
         assertEquals("Resource1: Some\nExample\nText\nWith\nBreaks", resource1.getName());
         assertEquals("Resource1 Notes: Some\nExample\nText\nWith\nBreaks", resource1.getNotes());
         success = true;
      }

      finally
      {
         if (success == true && out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Exercise the code which handles password protected files.
    *
    * @throws Exception
    */
   public void testPasswordProtection() throws Exception
   {
      File in;

      //
      // Read password (password1)
      //
      try
      {
         in = new File(m_basedir + "/readpassword9.mpp");
         new MPPReader().read(in);
         assertTrue(false);
      }

      catch (MPXJException ex)
      {
         assertEquals(MPXJException.PASSWORD_PROTECTED_ENTER_PASSWORD, ex.getMessage());
      }

      //
      // Write password (password2)
      //
      in = new File(m_basedir + "/writepassword9.mpp");
      new MPPReader().read(in);

      //
      // Read password
      //
      try
      {
         in = new File(m_basedir + "/bothpassword9.mpp");
         new MPPReader().read(in);
         assertTrue(false);
      }

      catch (MPXJException ex)
      {
         assertEquals(MPXJException.PASSWORD_PROTECTED_ENTER_PASSWORD, ex.getMessage());
      }
   }

   /**
    * This test ensures that the task and resource extended attributes are
    * read and written correctly for MSPDI files.
    *
    * @throws Exception
    */
   public void testMspdiExtendedAttributes() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      MSPDIWriter writer = new MSPDIWriter();

      ProjectFile xml = reader.read(m_basedir + "/mspextattr.xml");
      commonMspdiExtendedAttributeTests(xml);

      File out = File.createTempFile("junit", ".xml");
      writer.write(xml, out);

      xml = reader.read(out);
      commonMspdiExtendedAttributeTests(xml);

      out.delete();
   }

   /**
    * Common tests for MSPDI fileExtended Attribute values.
    *
    * @param xml MSPDI file
    */
   private void commonMspdiExtendedAttributeTests(ProjectFile xml)
   {
      List<Task> tasks = xml.getAllTasks();
      assertEquals(2, tasks.size());
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      Task task = tasks.get(1);
      assertEquals("Task Text One", task.getText1());
      assertEquals("01/01/2004", df.format(task.getStart1()));
      assertEquals("31/12/2004", df.format(task.getFinish1()));
      assertEquals(99.95, task.getCost1().doubleValue(), 0.0);
      assertEquals("18/07/2004", df.format(task.getDate1()));
      assertTrue(task.getFlag1());
      assertEquals(55.56, task.getNumber1().doubleValue(), 0.0);
      assertEquals(13.0, task.getDuration1().getDuration(), 0.0);
      assertEquals(TimeUnit.DAYS, task.getDuration1().getUnits());

      List<Resource> resources = xml.getAllResources();
      assertEquals(2, resources.size());

      Resource resource = resources.get(1);
      assertEquals("Resource Text One", resource.getText1());
      assertEquals("01/01/2003", df.format(resource.getStart1()));
      assertEquals("31/12/2003", df.format(resource.getFinish1()));
      assertEquals(29.99, resource.getCost1().doubleValue(), 0.0);
      assertEquals("18/07/2003", df.format(resource.getDate1()));
      assertTrue(resource.getFlag1());
      assertEquals(5.99, resource.getNumber1().doubleValue(), 0.0);
      assertEquals(22.0, resource.getDuration1().getDuration(), 0.0);
      assertEquals(TimeUnit.DAYS, resource.getDuration1().getUnits());
   }

   /**
    * This ensures that values in the project header are read and written
    * as expected.
    *
    * @throws Exception
    */
   public void testProjectHeader() throws Exception
   {
      File out = null;

      try
      {
         MPXReader reader = new MPXReader();
         MPXWriter writer = new MPXWriter();

         //
         // Read the MPX file and ensure that the project header fields
         // have the expected values.
         //
         ProjectFile mpx = reader.read(m_basedir + "/headertest.mpx");
         testHeaderFields(mpx);

         //
         // Write the file, re-read it and test to ensure that
         // the project header fields have the expected values
         //
         out = File.createTempFile("junit", ".mpx");
         writer.write(mpx, out);
         mpx = reader.read(out);
         testHeaderFields(mpx);
         out.delete();
         out = null;

         //
         // Read the MPP8 file and ensure that the project header fields
         // have the expected values.
         //
         mpx = new MPPReader().read(m_basedir + "/headertest8.mpp");
         testHeaderFields(mpx);

         //
         // Read the MPP9 file and ensure that the project header fields
         // have the expected values.
         //
         mpx = new MPPReader().read(m_basedir + "/headertest9.mpp");
         testHeaderFields(mpx);

         //
         // Read the MSPDI file and ensure that the project header fields
         // have the expected values.
         //
         mpx = new MSPDIReader().read(m_basedir + "/headertest.xml");
         testHeaderFields(mpx);

         //
         // Write the file, re-read it and test to ensure that
         // the project header fields have the expected values
         //
         out = File.createTempFile("junit", ".xml");
         new MSPDIWriter().write(mpx, out);

         mpx = new MSPDIReader().read(out);
         testHeaderFields(mpx);
         out.delete();
         out = null;
      }

      catch (Exception ex)
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Implements common project header tests.
    *
    * @param file target project file
    */
   private void testHeaderFields(ProjectFile file)
   {
      ProjectHeader header = file.getProjectHeader();
      assertEquals("Project Title Text", header.getProjectTitle());
      assertEquals("Author Text", header.getAuthor());
      assertEquals("Comments Text", header.getComments());
      assertEquals("Company Text", header.getCompany());
      assertEquals("Keywords Text", header.getKeywords());
      assertEquals("Manager Text", header.getManager());
      assertEquals("Subject Text", header.getSubject());
   }

   /**
    * Test retrieval of WBS information.
    *
    * @throws Exception
    */
   public void testWBS() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/sample98.mpp");
      Task task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals("Second Task", task.getName());
      assertEquals("1.1", task.getWBS());

      mpp = new MPPReader().read(m_basedir + "/sample.mpp");
      task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals("Second Task", task.getName());
      assertEquals("1.1", task.getWBS());
   }

   /**
    * Test read and write of priority information.
    *
    * @throws Exception
    */
   public void testPriority() throws Exception
   {
      ProjectFile mpx = new MPXReader().read(m_basedir + "/mpxpriority.mpx");
      validatePriority(mpx);

      ProjectFile mpp8 = new MPPReader().read(m_basedir + "/mpp8priority.mpp");
      validatePriority(mpp8);

      ProjectFile mpp9 = new MPPReader().read(m_basedir + "/mpp9priority.mpp");
      validatePriority(mpp9);

      ProjectFile xml = new MSPDIReader().read(m_basedir + "/mspdipriority.xml");
      validatePriority(xml);

      File out = null;
      boolean success = false;
      try
      {
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpx, out);
         ProjectFile mpx2 = new MPXReader().read(out);
         validatePriority(mpx2);
         success = true;
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }

      out = null;
      success = false;
      try
      {
         out = File.createTempFile("junit", ".xml");
         new MSPDIWriter().write(mpx, out);

         ProjectFile xml3 = new MSPDIReader().read(out);
         validatePriority(xml3);
         success = true;
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
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
    *
    * @throws Exception
    */
   public void testCalendars() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/caltest98.mpp");
      validateResourceCalendars(mpp);

      ProjectFile mpx = new MPXReader().read(m_basedir + "/caltest98.mpx");
      validateResourceCalendars(mpx);

      ProjectFile mpp9 = new MPPReader().read(m_basedir + "/caltest.mpp");
      validateResourceCalendars(mpp9);
      validateTaskCalendars(mpp9);

      ProjectFile xml = new MSPDIReader().read(m_basedir + "/caltest.xml");
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
      ProjectCalendar calendar = resource.getResourceCalendar();
      assertEquals("Resource One", calendar.getName());
      assertFalse(calendar.isBaseCalendar());
      assertEquals("Standard", calendar.getBaseCalendar().getName());
      assertTrue(calendar.getCalendarExceptions().isEmpty());

      //
      // Resource calendar based on base calendar
      //
      resource = mpx.getResourceByUniqueID(Integer.valueOf(2));
      calendar = resource.getResourceCalendar();
      assertEquals("Resource Two", calendar.getName());
      assertFalse(calendar.isBaseCalendar());
      assertEquals("Base Calendar", calendar.getBaseCalendar().getName());
      assertTrue(calendar.getCalendarExceptions().isEmpty());

      //
      // Resource calendar based on modified base calendar
      //
      resource = mpx.getResourceByUniqueID(Integer.valueOf(3));
      calendar = resource.getResourceCalendar();
      assertEquals("Resource Three", calendar.getName());
      assertFalse(calendar.isBaseCalendar());
      assertEquals("Base Calendar", calendar.getBaseCalendar().getName());
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
      assertTrue(calendar.isBaseCalendar());

      task = mpx.getTaskByUniqueID(Integer.valueOf(4));
      calendar = task.getCalendar();
      assertEquals("Base Calendar", calendar.getName());
      assertTrue(calendar.isBaseCalendar());
   }

   /**
    * Test to exercise task, resource, and assignment removal code.
    *
    * @throws Exception
    */
   public void testRemoval() throws Exception
   {
      //
      // Load the file and validate the number of
      // tasks, resources, and assignments.
      //
      ProjectFile mpp = new MPPReader().read(m_basedir + "/remove.mpp");
      assertEquals(10, mpp.getAllTasks().size());
      assertEquals(8, mpp.getAllResources().size());
      assertEquals(8, mpp.getAllResourceAssignments().size());

      //
      // Remove a task with no assignments
      //
      Task task = mpp.getTaskByUniqueID(Integer.valueOf(1));
      assertEquals("Task One", task.getName());
      task.remove();
      assertEquals(9, mpp.getAllTasks().size());
      assertEquals(8, mpp.getAllResources().size());
      assertEquals(7, mpp.getAllResourceAssignments().size());

      //
      // Remove a resource with no assignments
      //
      Resource resource = mpp.getResourceByUniqueID(Integer.valueOf(1));
      assertEquals("Resource One", resource.getName());
      resource.remove();
      assertEquals(9, mpp.getAllTasks().size());
      assertEquals(7, mpp.getAllResources().size());
      assertEquals(7, mpp.getAllResourceAssignments().size());

      //
      // Remove a task with a single assignment
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(2));
      assertEquals("Task Two", task.getName());
      task.remove();
      assertEquals(8, mpp.getAllTasks().size());
      assertEquals(7, mpp.getAllResources().size());
      assertEquals(6, mpp.getAllResourceAssignments().size());

      //
      // Remove a resource with a single assignment
      //
      resource = mpp.getResourceByUniqueID(Integer.valueOf(3));
      assertEquals("Resource Three", resource.getName());
      resource.remove();
      assertEquals(8, mpp.getAllTasks().size());
      assertEquals(6, mpp.getAllResources().size());
      assertEquals(5, mpp.getAllResourceAssignments().size());

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
      assertEquals(8, mpp.getAllTasks().size());
      assertEquals(6, mpp.getAllResources().size());
      assertEquals(4, mpp.getAllResourceAssignments().size());

      //
      // Remove a task with child tasks - the child tasks will also be removed
      //
      task = mpp.getTaskByUniqueID(Integer.valueOf(8));
      assertEquals("Task Eight", task.getName());
      task.remove();
      assertEquals(6, mpp.getAllTasks().size());
      assertEquals(6, mpp.getAllResources().size());
      assertEquals(3, mpp.getAllResourceAssignments().size());

      //
      // As we have removed tasks and resources, call the synchronize methods
      // to generate ID sequences without gaps. This will allow MS Project
      // to display the tasks and resources without blank rows.
      //
      mpp.synchronizeTaskIDs();
      mpp.synchronizeResourceIDs();

      //
      // Write the file and re-read it to ensure we get consistent results.
      //
      File out = null;

      try
      {
         out = File.createTempFile("junit", ".mpx");
         new MPXWriter().write(mpp, out);

         ProjectFile mpx = new MPXReader().read(out);
         assertEquals(6, mpx.getAllTasks().size());
         assertEquals(6, mpx.getAllResources().size());
         assertEquals(3, mpx.getAllResourceAssignments().size());
      }

      finally
      {
         if (out != null)
         {
            out.delete();
         }
      }
   }

   /**
    * Basic rewrite test to exercise the MPX calendar exception read/write code.
    *
    * @throws Exception
    */
   public void testProjectCalendarExceptions() throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File(m_basedir + "/calendarExceptions.mpx");
         ProjectFile mpx = new MPXReader().read(in);
         out = File.createTempFile("junit", ".mpx");
         MPXWriter writer = new MPXWriter();
         writer.setUseLocaleDefaults(false);
         writer.write(mpx, out);
         success = FileUtility.equals(in, out);
         assertTrue("Files are not identical", success);
      }

      finally
      {
         if (out != null && success == true)
         {
            out.delete();
         }
      }
   }
}
