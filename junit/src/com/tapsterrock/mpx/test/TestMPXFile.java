/*
 * file:       TestMPXFile.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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


package com.tapsterrock.mpx.test;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import junit.framework.TestCase;

import com.tapsterrock.mpp.MPPFile;
import com.tapsterrock.mpx.MPXCalendar;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXFile;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.RelationList;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;
import com.tapsterrock.mspdi.MSPDIFile;

/**
 * This class contains a small set of tests to exercise the MPX library.
 */
public class TestMPXFile extends TestCase
{
   /**
    * Constructor. Note that the system property mpxj.junit.datadir must
    * be defined to allow the test code to find the required sample files.
    *
    * @param s JUnit test name
    */
   public TestMPXFile (String s)
      throws Exception
   {
      super(s);
      m_basedir = System.getProperty ("mpxj.junit.datadir");
      if (m_basedir == null || m_basedir.length() == 0)
      {
         throw new Exception ("missing datadir property");
      }
   }

   /**
    * This method performs a simple data driven test to read then write
    * the contents of a single MPX file. Assuming the MPX file contains
    * at least one example of each type of record, this test will be able
    * to exercise a large part of the MPX library.
    */
   public void testRewrite1 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File (m_basedir + "/sample.mpx");
         MPXFile mpx = new MPXFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpx.write (out);
         success = compareFiles (in, out);
         assertTrue ("Files are not identical", success);
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
   public void testRewrite2 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File (m_basedir + "/sample1.xml");
         MSPDIFile xml = new MSPDIFile (in);
         out = File.createTempFile ("junit", ".xml");
         xml.write (out);
         success = compareFiles (in, out);
         assertTrue ("Files are not identical", success);
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
   public void testRewrite3 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File (m_basedir + "/sample1.mpx");
         MPXFile mpx = new MPXFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpx.write (out);
         success = compareFiles (in, out);
         assertTrue ("Files are not identical", success);
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
    * Test German localisation
    *
    * @throws Exception
    */
   public void testRewrite4 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         File in = new File (m_basedir + "/sample.mpx");
         MPXFile mpx = new MPXFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpx.setLocale(Locale.GERMAN);
         mpx.write (out);

         mpx = new MPXFile ();
         mpx.setLocale(Locale.GERMAN);
         mpx.read(out);
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
    * Test Swedish localisation.
    *
    * @throws Exception
    */
   public void testRewrite5 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         Locale swedish = new Locale ("sv");

         File in = new File (m_basedir + "/sample.mpx");
         MPXFile mpx = new MPXFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpx.setLocale(swedish);
         mpx.write (out);

         mpx = new MPXFile ();
         mpx.setLocale(swedish);
         mpx.read(out);
      }

      finally
      {
         if (out != null && success == true)
         {
            //out.delete();
         }
      }
   }

   /**
    * This test exercises the automatic generation of WBS and outline levels.
    */
   public void testAutomaticGeneration ()
      throws Exception
   {
      MPXFile file = new MPXFile();

      file.setAutoWBS(true);
      file.setAutoOutlineLevel(true);
      file.setAutoOutlineNumber(true);
      file.setAutoTaskID(true);
      file.setAutoTaskUniqueID(true);
      file.setAutoResourceID(true);
      file.setAutoResourceUniqueID(true);

      Resource resource1 = file.addResource();
      resource1.setName("R1");
      assertEquals (resource1.getUniqueIDValue(), 1);
      assertEquals (resource1.getIDValue(), 1);

      Resource resource2 = file.addResource();
      resource2.setName("R2");
      assertEquals (resource2.getUniqueIDValue(), 2);
      assertEquals (resource2.getIDValue(), 2);

      Task task1 = file.addTask();
      task1.setName("1.0");
      assertEquals (task1.getWBS(), "1.0");
      assertEquals (task1.getOutlineLevelValue(), 1);
      assertEquals (task1.getOutlineNumber(), "1.0");
      assertEquals (task1.getIDValue(), 1);
      assertEquals (task1.getUniqueIDValue(), 1);
      assertEquals (task1.getSummaryValue(), false);

      task1 = file.addTask();
      task1.setName("2.0");
      assertEquals (task1.getWBS(), "2.0");
      assertEquals (task1.getOutlineLevelValue(), 1);
      assertEquals (task1.getOutlineNumber(), "2.0");
      assertEquals (task1.getIDValue(), 2);
      assertEquals (task1.getUniqueIDValue(), 2);
      assertEquals (task1.getSummaryValue(), false);

      task1 = file.addTask();
      task1.setName("3.0");
      assertEquals (task1.getWBS(), "3.0");
      assertEquals (task1.getOutlineLevelValue(), 1);
      assertEquals (task1.getOutlineNumber(), "3.0");
      assertEquals (task1.getIDValue(), 3);
      assertEquals (task1.getUniqueIDValue(), 3);
      assertEquals (task1.getSummaryValue(), false);

      Task task2 = task1.addTask();
      task2.setName("3.1");
      assertEquals (task2.getWBS(), "3.1");
      assertEquals (task2.getOutlineLevelValue(), 2);
      assertEquals (task2.getOutlineNumber(), "3.1");
      assertEquals (task2.getIDValue(), 4);
      assertEquals (task2.getUniqueIDValue(), 4);
      assertEquals (task1.getSummaryValue(), true);
      assertEquals (task2.getSummaryValue(), false);

      task2 = task1.addTask();
      task2.setName("3.2");
      assertEquals (task2.getWBS(), "3.2");
      assertEquals (task2.getOutlineLevelValue(), 2);
      assertEquals (task2.getOutlineNumber(), "3.2");
      assertEquals (task2.getIDValue(), 5);
      assertEquals (task2.getUniqueIDValue(), 5);
      assertEquals (task1.getSummaryValue(), true);
      assertEquals (task2.getSummaryValue(), false);

      Task task3 = task2.addTask();
      task3.setName("3.2.1");
      assertEquals (task3.getWBS(), "3.2.1");
      assertEquals (task3.getOutlineLevelValue(), 3);
      assertEquals (task3.getOutlineNumber(), "3.2.1");
      assertEquals (task3.getIDValue(), 6);
      assertEquals (task3.getUniqueIDValue(), 6);
      assertEquals (task1.getSummaryValue(), true);
      assertEquals (task2.getSummaryValue(), true);
      assertEquals (task3.getSummaryValue(), false);

      task3 = task2.addTask();
      task3.setName("3.2.2");
      assertEquals (task3.getWBS(), "3.2.2");
      assertEquals (task3.getOutlineLevelValue(), 3);
      assertEquals (task3.getOutlineNumber(), "3.2.2");
      assertEquals (task3.getIDValue(), 7);
      assertEquals (task3.getUniqueIDValue(), 7);
      assertEquals (task1.getSummaryValue(), true);
      assertEquals (task2.getSummaryValue(), true);
      assertEquals (task3.getSummaryValue(), false);
   }

   /**
    * Test to ensure that the basic task hierarchy is
    * represented correctly.
    *
    * @throws Exception
    */
   public void testStructure ()
      throws Exception
   {
      MPXFile file = new MPXFile();

      file.setAutoWBS(true);
      file.setAutoOutlineLevel(true);
      file.setAutoTaskID(true);
      file.setAutoTaskUniqueID(true);

      Task task1 = file.addTask();
      assertNull (task1.getParentTask());

      Task task2 = task1.addTask();
      assertEquals (task2.getParentTask(), task1);

      task1.addTask();
      LinkedList children = task1.getChildTasks();
      assertEquals (children.size(), 2);

      LinkedList toplevel = file.getChildTasks();
      assertEquals (toplevel.size(), 1);
   }

   /**
    * Exercise the MPP8 import code.
    */
   public void testConversion1 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/sample98.mpp");
         MPPFile mpp = new MPPFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpp.write (out);
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
   public void testConversion2 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/sample.mpp");
         MPPFile mpp = new MPPFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpp.write (out);
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
   public void testConversion3 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/sample.xml");
         MSPDIFile xml = new MSPDIFile (in);
         out = File.createTempFile ("junit", ".mpx");
         xml.write (out);
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
    * This method tests two stages of conversion, MPP->MPX->MSPDI. This
    * jhas been designed to exercise bug 896189, which was exhibited
    * when an MSPDI file was generated from an MPX file which did not
    * have the same set of attributes as a native MPP file.
    *
    * @throws Exception
    */
   public void testConversion4 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/sample.mpp");
         MPPFile mpp = new MPPFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpp.write (out);

         MPXFile mpx = new MPXFile (out);
         out.delete();
         MSPDIFile mspdi = new MSPDIFile (mpx);
         out = File.createTempFile ("junit", ".xml");
         mspdi.write(out);
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
   public void testRelationList ()
      throws Exception
   {
      File in = new File (m_basedir + "/sample.mpx");
      MPXFile mpx = new MPXFile (in);
      LinkedList tasks = mpx.getAllTasks();
      Iterator taskIter = tasks.iterator();

      while (taskIter.hasNext() == true)
      {
         Task task = (Task)taskIter.next();
         RelationList rels = task.getPredecessors();
         if (rels != null)
         {
            Iterator relIter = rels.iterator();

            while (relIter.hasNext() == true)
            {
               Relation rel = (Relation)relIter.next();
               mpx.getTaskByUniqueID(rel.getTaskIDValue());
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
   public void testTaskNotes ()
      throws Exception
   {
      File out = null;

      try
      {
         String notes1 = "Notes, containing a comma. Done.";
         String notes2 = "Notes \"containing embedded quotes\" Done.";
         String notes3 = "Notes, \"containing embedded quotes, and comma's too.\" Done.";
         String notes4 = "\"Notes containing embedded quotes as first and last chars. Done.\"";
         String notes5 = "Normal unquoted notes. Done.";

         MPXFile file1 = new MPXFile();

         file1.setAutoWBS(true);
         file1.setAutoOutlineLevel(true);
         file1.setAutoTaskID(true);
         file1.setAutoTaskUniqueID(true);

         Task task1 = file1.addTask();
         task1.setName("Test Task 1");
         task1.setDuration(new MPXDuration (10, TimeUnit.DAYS));
         task1.setStart(new Date());
         task1.setNotes(notes1);

         Task task2 = file1.addTask();
         task2.setName("Test Task 2");
         task2.setDuration(new MPXDuration (10, TimeUnit.DAYS));
         task2.setStart(new Date());
         task2.setNotes(notes2);

         Task task3 = file1.addTask();
         task3.setName("Test Task 3");
         task3.setDuration(new MPXDuration (10, TimeUnit.DAYS));
         task3.setStart(new Date());
         task3.setNotes(notes3);

         Task task4 = file1.addTask();
         task4.setName("Test Task 4");
         task4.setDuration(new MPXDuration (10, TimeUnit.DAYS));
         task4.setStart(new Date());
         task4.setNotes(notes4);

         Task task5 = file1.addTask();
         task5.setName("Test Task 5");
         task5.setDuration(new MPXDuration (10, TimeUnit.DAYS));
         task5.setStart(new Date());
         task5.setNotes(notes5);

         out = File.createTempFile ("junit", ".mpx");
         file1.write (out);

         MPXFile file2 = new MPXFile (out);
         String notes;
         Task task1a = file2.getTaskByUniqueID(task1.getUniqueIDValue());
         notes = task1a.getNotes();
         assertEquals (notes1, notes);

         Task task2a = file2.getTaskByUniqueID(task2.getUniqueIDValue());
         notes = task2a.getNotes();
         assertEquals (notes2, notes);

         Task task3a = file2.getTaskByUniqueID(task3.getUniqueIDValue());
         notes = task3a.getNotes();
         assertEquals (notes3, notes);

         Task task4a = file2.getTaskByUniqueID(task4.getUniqueIDValue());
         notes = task4a.getNotes();
         assertEquals (notes4, notes);

         Task task5a = file2.getTaskByUniqueID(task5.getUniqueIDValue());
         notes = task5a.getNotes();
         assertEquals (notes5, notes);
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
   public void testResourceNotes ()
      throws Exception
   {
      File out = null;

      try
      {
         String notes1 = "Notes, containing a comma. Done.";
         String notes2 = "Notes \"containing embedded quotes\" Done.";
         String notes3 = "Notes, \"containing embedded quotes, and comma's too.\" Done.";
         String notes4 = "\"Notes containing embedded quotes as first and last chars. Done.\"";
         String notes5 = "Normal unquoted notes. Done.";

         MPXFile file1 = new MPXFile();

         file1.setAutoWBS(true);
         file1.setAutoOutlineLevel(true);
         file1.setAutoResourceID(true);
         file1.setAutoResourceUniqueID(true);

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

         out = File.createTempFile ("junit", ".mpx");
         file1.write (out);

         MPXFile file2 = new MPXFile (out);
         String notes;
         Resource resource1a = file2.getResourceByUniqueID(resource1.getUniqueIDValue());
         notes = resource1a.getNotes();
         assertEquals (notes1, notes);

         Resource resource2a = file2.getResourceByUniqueID(resource2.getUniqueIDValue());
         notes = resource2a.getNotes();
         assertEquals (notes2, notes);

         Resource resource3a = file2.getResourceByUniqueID(resource3.getUniqueIDValue());
         notes = resource3a.getNotes();
         assertEquals (notes3, notes);

         Resource resource4a = file2.getResourceByUniqueID(resource4.getUniqueIDValue());
         notes = resource4a.getNotes();
         assertEquals (notes4, notes);

         Resource resource5a = file2.getResourceByUniqueID(resource5.getUniqueIDValue());
         notes = resource5a.getNotes();
         assertEquals (notes5, notes);
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
   public void testBug1 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/bug1.mpp");
         MPPFile mpp = new MPPFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpp.write (out);
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
   public void testBug2 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/bug2.mpp");
         MPPFile mpp = new MPPFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpp.write (out);
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
    * set up to reflect the outline level
    */
   public void testBug3 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/bug3.mpp");
         MPPFile mpp = new MPPFile (in);
         LinkedList tasks = mpp.getAllTasks();
         Iterator iter = tasks.iterator();
         Task task;

         while (iter.hasNext() == true)
         {
            task = (Task)iter.next();
            assertEquals("Outline levels do not match", task.getOutlineLevelValue(), calculateOutlineLevel(task));
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
    * Read an MPP8 file with a non-standard task fixed data block size
    */
   public void testBug4 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/bug4.mpp");
         MPPFile mpp = new MPPFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpp.write (out);
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
   private int calculateOutlineLevel (Task task)
   {
      int level = 0;

      while (task != null)
      {
         task = task.getParentTask();
         ++level;
      }

      return (level);
   }

   /**
    * Utility function to ensure that two files contain identical data.
    *
    * @param file1 File object
    * @param file2 File object
    * @return boolean flag
    */
   private boolean compareFiles (File file1, File file2)
      throws Exception
   {
      boolean result;

      if (file1.length() != file2.length())
      {
         result = false;
      }
      else
      {
         result = true;

         FileInputStream input1 = new FileInputStream (file1);
         FileInputStream input2 = new FileInputStream (file2);
         int c1;
         int c2;

         while (true)
         {
            c1 = input1.read();
            c2 = input2.read();

            if (c1 != c2)
            {
               result = false;
               break;
            }

            if (c1 == -1)
            {
               break;
            }
         }

         input1.close();
         input2.close();
      }

      return (result);
   }

   /**
    * Simple tests to exercise the BaseCalendar.getDate method
    *
    * @throws Exception
    */
   public void testBaseCalendarGetDate ()
      throws Exception
   {
      MPXFile file = new MPXFile ();
      MPXDuration duration;
      MPXCalendar cal = file.addDefaultBaseCalendar();
      SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
      Date startDate = df.parse("09/10/2003");

      duration = new MPXDuration (1, TimeUnit.DAYS);
      Date endDate = cal.getDate(startDate, duration);
      assertEquals(endDate.getTime(), df.parse("10/10/2003").getTime());

      duration = new MPXDuration (7, TimeUnit.DAYS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(endDate.getTime(), df.parse("18/10/2003").getTime());

      duration = new MPXDuration (1, TimeUnit.WEEKS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(endDate.getTime(), df.parse("18/10/2003").getTime());

      duration = new MPXDuration (-1, TimeUnit.DAYS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(endDate.getTime(), df.parse("08/10/2003").getTime());
   }

   /**
    * Ensure that we are reading MPP8 flags correctly. This test reads a
    * file where the tasks alternately have values of either all true, or
    * all false. Each pair of tasks increases by one in outline level.
    *
    * @throws Exception
    */
   public void testMPP8Flags1 ()
      throws Exception
   {
      File in = new File (m_basedir + "/mpp8flags1.mpp");
      MPPFile mpp = new MPPFile (in);
      LinkedList tasks = mpp.getAllTasks();
      assertTrue ("Not enough tasks", (tasks.size() > 0));
      assertTrue ("Not an even number of tasks", (tasks.size()%2 == 0));

      Iterator iter = tasks.iterator();
      Task task;
      while (iter.hasNext())
      {
         task = (Task)iter.next();
         assertFalse(task.getName(), task.getFlag1Value());
         assertFalse(task.getName(), task.getFlag2Value());
         assertFalse(task.getName(), task.getFlag3Value());
         assertFalse(task.getName(), task.getFlag4Value());
         assertFalse(task.getName(), task.getFlag5Value());
         assertFalse(task.getName(), task.getFlag6Value());
         assertFalse(task.getName(), task.getFlag7Value());
         assertFalse(task.getName(), task.getFlag8Value());
         assertFalse(task.getName(), task.getFlag9Value());
         assertFalse(task.getName(), task.getFlag10Value());
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

         task = (Task)iter.next();
         assertTrue(task.getName(), task.getFlag1Value());
         assertTrue(task.getName(), task.getFlag2Value());
         assertTrue(task.getName(), task.getFlag3Value());
         assertTrue(task.getName(), task.getFlag4Value());
         assertTrue(task.getName(), task.getFlag5Value());
         assertTrue(task.getName(), task.getFlag6Value());
         assertTrue(task.getName(), task.getFlag7Value());
         assertTrue(task.getName(), task.getFlag8Value());
         assertTrue(task.getName(), task.getFlag9Value());
         assertTrue(task.getName(), task.getFlag10Value());
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
    * a sngle flag from 1-20 set. The next set of 20 tasks increases by
    * one outline level.
    *
    * @throws Exception
    */
   public void testMPP8Flags2 ()
      throws Exception
   {
      File in = new File (m_basedir + "/mpp8flags2.mpp");
      MPPFile mpp = new MPPFile (in);
      LinkedList tasks = mpp.getAllTasks();

      Iterator iter = tasks.iterator();
      Task task;
      int index = 0;
      boolean[] flags;

      while (iter.hasNext())
      {
         task = (Task)iter.next();
         if (task.getName().startsWith("Parent") == false)
         {
            flags = getFlagArray(task);
            assertTrue ("Incorrect flag set in task " + task.getName(), testSingleFlagTrue(flags, index));
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
   public void testMPP9Flags1 ()
      throws Exception
   {
      File in = new File (m_basedir + "/mpp9flags1.mpp");
      MPPFile mpp = new MPPFile (in);
      LinkedList tasks = mpp.getAllTasks();
      assertTrue ("Not enough tasks", (tasks.size() > 0));
      assertTrue ("Not an even number of tasks", (tasks.size()%2 == 0));

      Iterator iter = tasks.iterator();
      Task task;
      while (iter.hasNext())
      {
         task = (Task)iter.next();
         assertFalse(task.getName(), task.getFlag1Value());
         assertFalse(task.getName(), task.getFlag2Value());
         assertFalse(task.getName(), task.getFlag3Value());
         assertFalse(task.getName(), task.getFlag4Value());
         assertFalse(task.getName(), task.getFlag5Value());
         assertFalse(task.getName(), task.getFlag6Value());
         assertFalse(task.getName(), task.getFlag7Value());
         assertFalse(task.getName(), task.getFlag8Value());
         assertFalse(task.getName(), task.getFlag9Value());
         assertFalse(task.getName(), task.getFlag10Value());
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

         task = (Task)iter.next();
         assertTrue(task.getName(), task.getFlag1Value());
         assertTrue(task.getName(), task.getFlag2Value());
         assertTrue(task.getName(), task.getFlag3Value());
         assertTrue(task.getName(), task.getFlag4Value());
         assertTrue(task.getName(), task.getFlag5Value());
         assertTrue(task.getName(), task.getFlag6Value());
         assertTrue(task.getName(), task.getFlag7Value());
         assertTrue(task.getName(), task.getFlag8Value());
         assertTrue(task.getName(), task.getFlag9Value());
         assertTrue(task.getName(), task.getFlag10Value());
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
    * a sngle flag from 1-20 set. The next set of 20 tasks increases by
    * one outline level.
    *
    * @throws Exception
    */
   public void testMPP9Flags2 ()
      throws Exception
   {
      File in = new File (m_basedir + "/mpp8flags2.mpp");
      MPPFile mpp = new MPPFile (in);
      LinkedList tasks = mpp.getAllTasks();

      Iterator iter = tasks.iterator();
      Task task;
      int index = 0;
      boolean[] flags;

      while (iter.hasNext())
      {
         task = (Task)iter.next();
         if (task.getName().startsWith("Parent") == false)
         {
            flags = getFlagArray(task);
            assertTrue ("Incorrect flag set in task " + task.getName(), testSingleFlagTrue(flags, index));
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
   private boolean[] getFlagArray (Task task)
   {
      boolean[] flags = new boolean[20];

      flags[0] = task.getFlag1Value();
      flags[1] = task.getFlag2Value();
      flags[2] = task.getFlag3Value();
      flags[3] = task.getFlag4Value();
      flags[4] = task.getFlag5Value();
      flags[5] = task.getFlag6Value();
      flags[6] = task.getFlag7Value();
      flags[7] = task.getFlag8Value();
      flags[8] = task.getFlag9Value();
      flags[9] = task.getFlag10Value();
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
   private boolean testSingleFlagTrue (boolean[] flags, int index)
   {
      boolean result = true;

      for (int loop=0; loop < flags.length; loop++)
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
   public void testViews ()
      throws Exception
   {
      MPPFile mpp = new MPPFile (m_basedir + "/sample98.mpp");
      ArrayList views = mpp.getViews();
      assertEquals("Incorrect number of views", 1, views.size());

      mpp = new MPPFile (m_basedir + "/sample.mpp");
      views = mpp.getViews();
      assertEquals("Incorrect number of views", 3, views.size());
   }

   /**
    * Test retrieval of table information.
    *
    * @throws Exception
    */
   public void testTables ()
      throws Exception
   {
      MPPFile mpp = new MPPFile (m_basedir + "/sample98.mpp");
      ArrayList tables = mpp.getTables();
//      Iterator iter = tables.iterator();
//      while (iter.hasNext() == true)
//      {
//         System.out.println(iter.next());
//      }

      assertEquals("Incorrect number of tables", 1, tables.size());

      mpp = new MPPFile (m_basedir + "/sample.mpp");
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
   public void testTaskCalendars ()
      throws Exception
   {
      File out = null;

      try
      {
         //
         // Read in the MPP file. The task names should
         // match the calendar names.
         //
         File in = new File (m_basedir + "/sample1.mpp");
         MPPFile mpp = new MPPFile (in);
         Iterator iter = mpp.getAllTasks().iterator();
         Task task;
         MPXCalendar cal;

         while (iter.hasNext() == true)
         {
            task = (Task)iter.next();
            cal = task.getCalendar();
            if (cal != null)
            {
               assertEquals(task.getName(), cal.getName());
            }
         }

         //
         // Write this out as an MSPDI file
         //
         MSPDIFile mspdi = new MSPDIFile (mpp);
         out = File.createTempFile ("junit", ".xml");
         mspdi.write (out);

         //
         // Read the MSPDI file in again, and check the
         // calendar names to ensure consistency
         //
         mspdi = new MSPDIFile (out.getCanonicalPath());
         iter = mspdi.getAllTasks().iterator();

         while (iter.hasNext() == true)
         {
            task = (Task)iter.next();
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

   private String m_basedir;
}

