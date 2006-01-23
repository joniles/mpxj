/*
 * file:       TestMPXFile.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.utility.NumberUtility;


/**
 * This class contains a small set of tests to exercise the MPXJ library.
 */
public class MPXJTest extends TestCase
{
   /**
    * Constructor. Note that the system property mpxj.junit.datadir must
    * be defined to allow the test code to find the required sample files.
    *
    * @param s JUnit test name
    */
   public MPXJTest (String s)
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
         ProjectFile mpx = new MPXReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write(mpx, out, false);
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
         ProjectFile xml = new MSPDIReader().read(in);
         out = File.createTempFile ("junit", ".xml");
         new MSPDIWriter().write(xml, out);
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
         ProjectFile mpx = new MPXReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write(mpx, out, false);
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
    * Test German localisation.
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
         MPXReader reader = new MPXReader();
         MPXWriter writer = new MPXWriter ();
         
         File in = new File (m_basedir + "/sample.mpx");
         ProjectFile mpx = reader.read (in);
         out = File.createTempFile ("junit", ".mpx");
                  
         writer.setLocale(Locale.GERMAN);
         writer.write(mpx, out);

         reader.setLocale(Locale.GERMAN);
         mpx = reader.read(out);
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
         MPXReader reader = new MPXReader();
         MPXWriter writer = new MPXWriter ();         
         Locale swedish = new Locale ("sv");

         File in = new File (m_basedir + "/sample.mpx");
         ProjectFile mpx = reader.read(in);
         out = File.createTempFile("junit", ".mpx");
         writer.setLocale(swedish);
         writer.write(mpx, out);

         mpx = new ProjectFile ();
         reader.setLocale(swedish);
         mpx = reader.read(out);
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
    * Test Portuguese localisation.
    *
    * @throws Exception
    */
   public void testRewrite6 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         MPXReader reader = new MPXReader();
         MPXWriter writer = new MPXWriter ();                  
         Locale portuguese = new Locale ("pt");

         File in = new File (m_basedir + "/sample.mpx");
         ProjectFile mpx = reader.read(in);
         out = File.createTempFile ("junit", ".mpx");
         writer.setLocale(portuguese);
         writer.write (mpx, out);

         mpx = new ProjectFile ();
         reader.setLocale(portuguese);
         mpx = reader.read(out);
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
    * Test French localisation.
    *
    * @throws Exception
    */
   public void testRewrite7 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         MPXReader reader = new MPXReader();
         MPXWriter writer = new MPXWriter ();                           
         Locale french = new Locale ("fr");

         File in = new File (m_basedir + "/sample.mpx");
         ProjectFile mpx = reader.read(in);
         out = File.createTempFile ("junit", ".mpx");
         writer.setLocale(french);
         writer.write (mpx, out);

         reader.setLocale(french);
         mpx = reader.read(out);
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
    * Test Italian localisation.
    *
    * @throws Exception
    */
   public void testRewrite8 ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         MPXReader reader = new MPXReader();
         MPXWriter writer = new MPXWriter ();                                    
         Locale italian = new Locale ("it");

         File in = new File (m_basedir + "/sample.mpx");
         ProjectFile mpx = reader.read(in);
         out = File.createTempFile ("junit", ".mpx");
         writer.setLocale(italian);
         writer.write (mpx, out);

         reader.setLocale(italian);
         mpx = reader.read(out);
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
    * Read a file created by a German version of MS Project 98.
    *
    * @throws Exception
    */
   public void testRead1 ()
      throws Exception
   {
      File in = new File (m_basedir + "/sample.de.mpx");
      MPXReader reader = new MPXReader();
      reader.setLocale(Locale.GERMAN);
      reader.read(in);
   }

   /**
    * This test exercises the automatic generation of WBS and outline levels.
    */
   public void testAutomaticGeneration ()
      throws Exception
   {
      ProjectFile file = new ProjectFile();

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
      assertEquals (resource1.getID().intValue(), 1);

      Resource resource2 = file.addResource();
      resource2.setName("R2");
      assertEquals (resource2.getUniqueIDValue(), 2);
      assertEquals (resource2.getID().intValue(), 2);

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
      ProjectFile file = new ProjectFile();

      file.setAutoWBS(true);
      file.setAutoOutlineLevel(true);
      file.setAutoTaskID(true);
      file.setAutoTaskUniqueID(true);

      Task task1 = file.addTask();
      assertNull (task1.getParentTask());

      Task task2 = task1.addTask();
      assertEquals (task2.getParentTask(), task1);

      task1.addTask();
      List children = task1.getChildTasks();
      assertEquals (children.size(), 2);

      List toplevel = file.getChildTasks();
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
         ProjectFile mpp = new MPPReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpp, out);
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
   public void testConversion2 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/sample.mpp");
         ProjectFile mpp = new MPPReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpp, out);
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
   public void testConversion3 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/sample.xml");
         ProjectFile xml = new MSPDIReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (xml, out);
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
    * @param file MPXFile instance
    */
   private void commonTests (ProjectFile file)
   {
      //
      // Test the remaining work attribute
      //
      Task task = file.getTaskByUniqueID(new Integer(2));
      List assignments = task.getResourceAssignments();
      assertEquals(2, assignments.size());

      Iterator iter = assignments.iterator();
      ResourceAssignment assignment;

      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();

         switch (NumberUtility.getInt(assignment.getResourceID()))
         {
            case 1:
            {
               assertEquals(200, (int)assignment.getRemainingWork().getDuration());
               assertEquals(TimeUnit.HOURS, assignment.getRemainingWork().getUnits());
               break;
            }

            case 2:
            {
               assertEquals(300, (int)assignment.getRemainingWork().getDuration());
               assertEquals(TimeUnit.HOURS, assignment.getRemainingWork().getUnits());
               break;
            }

            default:
            {
               assertTrue("Unexpected resource", false);
               break;
            }
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
         ProjectFile mpp = new MPPReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpp, out);

         ProjectFile mpx = new MPXReader().read (out);
         out.delete();
         out = File.createTempFile ("junit", ".xml");
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
   public void testRelationList ()
      throws Exception
   {
      File in = new File (m_basedir + "/sample.mpx");
      ProjectFile mpx = new MPXReader().read (in);
      List tasks = mpx.getAllTasks();
      Iterator taskIter = tasks.iterator();

      while (taskIter.hasNext() == true)
      {
         Task task = (Task)taskIter.next();
         List rels = task.getPredecessors();
         if (rels != null)
         {
            Iterator relIter = rels.iterator();

            while (relIter.hasNext() == true)
            {
               Relation rel = (Relation)relIter.next();
               mpx.getTaskByUniqueID(rel.getTaskUniqueID());
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

         ProjectFile file1 = new ProjectFile();

         file1.setAutoWBS(true);
         file1.setAutoOutlineLevel(true);
         file1.setAutoTaskID(true);
         file1.setAutoTaskUniqueID(true);

         Task task1 = file1.addTask();
         task1.setName("Test Task 1");
         task1.setDuration(Duration.getInstance (10, TimeUnit.DAYS));
         task1.setStart(new Date());
         task1.setNotes(notes1);

         Task task2 = file1.addTask();
         task2.setName("Test Task 2");
         task2.setDuration(Duration.getInstance (10, TimeUnit.DAYS));
         task2.setStart(new Date());
         task2.setNotes(notes2);

         Task task3 = file1.addTask();
         task3.setName("Test Task 3");
         task3.setDuration(Duration.getInstance (10, TimeUnit.DAYS));
         task3.setStart(new Date());
         task3.setNotes(notes3);

         Task task4 = file1.addTask();
         task4.setName("Test Task 4");
         task4.setDuration(Duration.getInstance (10, TimeUnit.DAYS));
         task4.setStart(new Date());
         task4.setNotes(notes4);

         Task task5 = file1.addTask();
         task5.setName("Test Task 5");
         task5.setDuration(Duration.getInstance (10, TimeUnit.DAYS));
         task5.setStart(new Date());
         task5.setNotes(notes5);

         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (file1, out);

         ProjectFile file2 = new MPXReader().read(out);
         String notes;
         Task task1a = file2.getTaskByUniqueID(task1.getUniqueID());
         notes = task1a.getNotes();
         assertEquals (notes1, notes);

         Task task2a = file2.getTaskByUniqueID(task2.getUniqueID());
         notes = task2a.getNotes();
         assertEquals (notes2, notes);

         Task task3a = file2.getTaskByUniqueID(task3.getUniqueID());
         notes = task3a.getNotes();
         assertEquals (notes3, notes);

         Task task4a = file2.getTaskByUniqueID(task4.getUniqueID());
         notes = task4a.getNotes();
         assertEquals (notes4, notes);

         Task task5a = file2.getTaskByUniqueID(task5.getUniqueID());
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

         ProjectFile file1 = new ProjectFile();

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
         new MPXWriter().write (file1, out);

         ProjectFile file2 = new MPXReader().read (out);
         String notes;
         Resource resource1a = file2.getResourceByUniqueID(resource1.getUniqueID());
         notes = resource1a.getNotes();
         assertEquals (notes1, notes);

         Resource resource2a = file2.getResourceByUniqueID(resource2.getUniqueID());
         notes = resource2a.getNotes();
         assertEquals (notes2, notes);

         Resource resource3a = file2.getResourceByUniqueID(resource3.getUniqueID());
         notes = resource3a.getNotes();
         assertEquals (notes3, notes);

         Resource resource4a = file2.getResourceByUniqueID(resource4.getUniqueID());
         notes = resource4a.getNotes();
         assertEquals (notes4, notes);

         Resource resource5a = file2.getResourceByUniqueID(resource5.getUniqueID());
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
         ProjectFile mpp = new MPPReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpp, out);
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
         ProjectFile mpp = new MPPReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpp, out);
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
   public void testBug3 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/bug3.mpp");
         ProjectFile mpp = new MPPReader().read (in);
         List tasks = mpp.getAllTasks();
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
    * Read an MPP8 file with a non-standard task fixed data block size.
    */
   public void testBug4 ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/bug4.mpp");
         ProjectFile mpp = new MPPReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpp, out);
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
    * Simple tests to exercise the BaseCalendar.getDate method.
    *
    * @throws Exception
    */
   public void testBaseCalendarGetDate ()
      throws Exception
   {
      ProjectFile file = new ProjectFile ();
      Duration duration;
      ProjectCalendar cal = file.addDefaultBaseCalendar();
      SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
      Date startDate = df.parse("09/10/2003");

      duration = Duration.getInstance (1, TimeUnit.DAYS);
      Date endDate = cal.getDate(startDate, duration);
      assertEquals(df.parse("10/10/2003").getTime(), endDate.getTime());

      duration = Duration.getInstance (7, TimeUnit.DAYS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(df.parse("18/10/2003").getTime(), endDate.getTime());

      duration = Duration.getInstance (1, TimeUnit.WEEKS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(df.parse("16/10/2003").getTime(), endDate.getTime());

      duration = Duration.getInstance (-1, TimeUnit.DAYS);
      endDate = cal.getDate(startDate, duration);
      assertEquals(df.parse("08/10/2003").getTime(), endDate.getTime());
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
      ProjectFile mpp = new MPPReader().read (in);
      List tasks = mpp.getAllTasks();
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
      ProjectFile mpp = new MPPReader().read (in);
      List tasks = mpp.getAllTasks();

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
      ProjectFile mpp = new MPPReader().read (in);
      List tasks = mpp.getAllTasks();
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
      ProjectFile mpp = new MPPReader().read (in);
      List tasks = mpp.getAllTasks();

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
      ProjectFile mpp = new MPPReader().read (m_basedir + "/sample98.mpp");
      List views = mpp.getViews();
      assertEquals("Incorrect number of views", 1, views.size());

      mpp = new MPPReader().read (m_basedir + "/sample.mpp");
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
      ProjectFile mpp = new MPPReader().read (m_basedir + "/sample98.mpp");
      List tables = mpp.getTables();
//      Iterator iter = tables.iterator();
//      while (iter.hasNext() == true)
//      {
//         System.out.println(iter.next());
//      }

      assertEquals("Incorrect number of tables", 1, tables.size());

      mpp = new MPPReader().read (m_basedir + "/sample.mpp");
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
         ProjectFile mpp = new MPPReader().read (in);
         Iterator iter = mpp.getAllTasks().iterator();
         Task task;
         ProjectCalendar cal;

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
         out = File.createTempFile ("junit", ".xml");
         new MSPDIWriter().write(mpp, out);

         //
         // Read the MSPDI file in again, and check the
         // calendar names to ensure consistency
         //
         ProjectFile mspdi = new MSPDIReader().read (out.getCanonicalPath());
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

   /**
    * Exercise field alias code for MSPDI files.
    *
    * @throws Exception
    */
   public void testMSPDIAliases ()
      throws Exception
   {
      File out = null;
      boolean success = true;

      try
      {
         MSPDIReader reader = new MSPDIReader();
         MSPDIWriter writer = new MSPDIWriter();
         
         File in = new File (m_basedir + "/alias.xml");
         ProjectFile xml = reader.read (in);
         validateAliases(xml);

         out = File.createTempFile ("junit", ".xml");
         writer.write (xml, out);

         xml = reader.read (out);
         validateAliases(xml);

         success=true;
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
   public void testMPP9Aliases ()
      throws Exception
   {
      File in = new File (m_basedir + "/alias.mpp");
      ProjectFile mpp = new MPPReader().read (in);
      validateAliases(mpp);
   }

   /**
    * Test to ensure that we are seeing the expected field
    * aliases.
    *
    * @param mpx MPX file
    */
   private void validateAliases (ProjectFile mpx)
   {
      assertEquals ("Text1t", mpx.getTaskFieldAlias(Task.TEXT1));
      assertEquals ("Text2t", mpx.getTaskFieldAlias(Task.TEXT2));
      assertEquals ("Text3t", mpx.getTaskFieldAlias(Task.TEXT3));
      assertEquals ("Text4t", mpx.getTaskFieldAlias(Task.TEXT4));
      assertEquals ("Text5t", mpx.getTaskFieldAlias(Task.TEXT5));
      assertEquals ("Text6t", mpx.getTaskFieldAlias(Task.TEXT6));
      assertEquals ("Text7t", mpx.getTaskFieldAlias(Task.TEXT7));
      assertEquals ("Text8t", mpx.getTaskFieldAlias(Task.TEXT8));
      assertEquals ("Text9t", mpx.getTaskFieldAlias(Task.TEXT9));
      assertEquals ("Text10t", mpx.getTaskFieldAlias(Task.TEXT10));
      assertEquals ("Text11t", mpx.getTaskFieldAlias(Task.TEXT11));
      assertEquals ("Text12t", mpx.getTaskFieldAlias(Task.TEXT12));
      assertEquals ("Text13t", mpx.getTaskFieldAlias(Task.TEXT13));
      assertEquals ("Text14t", mpx.getTaskFieldAlias(Task.TEXT14));
      assertEquals ("Text15t", mpx.getTaskFieldAlias(Task.TEXT15));
      assertEquals ("Text16t", mpx.getTaskFieldAlias(Task.TEXT16));
      assertEquals ("Text17t", mpx.getTaskFieldAlias(Task.TEXT17));
      assertEquals ("Text18t", mpx.getTaskFieldAlias(Task.TEXT18));
      assertEquals ("Text19t", mpx.getTaskFieldAlias(Task.TEXT19));
      assertEquals ("Text20t", mpx.getTaskFieldAlias(Task.TEXT20));
      assertEquals ("Text21t", mpx.getTaskFieldAlias(Task.TEXT21));
      assertEquals ("Text22t", mpx.getTaskFieldAlias(Task.TEXT22));
      assertEquals ("Text23t", mpx.getTaskFieldAlias(Task.TEXT23));
      assertEquals ("Text24t", mpx.getTaskFieldAlias(Task.TEXT24));
      assertEquals ("Text25t", mpx.getTaskFieldAlias(Task.TEXT25));
      assertEquals ("Text26t", mpx.getTaskFieldAlias(Task.TEXT26));
      assertEquals ("Text27t", mpx.getTaskFieldAlias(Task.TEXT27));
      assertEquals ("Text28t", mpx.getTaskFieldAlias(Task.TEXT28));
      assertEquals ("Text29t", mpx.getTaskFieldAlias(Task.TEXT29));
      assertEquals ("Text30t", mpx.getTaskFieldAlias(Task.TEXT30));
      assertEquals ("Start1t", mpx.getTaskFieldAlias(Task.START1));
      assertEquals ("Start2t", mpx.getTaskFieldAlias(Task.START2));
      assertEquals ("Start3t", mpx.getTaskFieldAlias(Task.START3));
      assertEquals ("Start4t", mpx.getTaskFieldAlias(Task.START4));
      assertEquals ("Start5t", mpx.getTaskFieldAlias(Task.START5));
      assertEquals ("Start6t", mpx.getTaskFieldAlias(Task.START6));
      assertEquals ("Start7t", mpx.getTaskFieldAlias(Task.START7));
      assertEquals ("Start8t", mpx.getTaskFieldAlias(Task.START8));
      assertEquals ("Start9t", mpx.getTaskFieldAlias(Task.START9));
      assertEquals ("Start10t", mpx.getTaskFieldAlias(Task.START10));
      assertEquals ("Finish1t", mpx.getTaskFieldAlias(Task.FINISH1));
      assertEquals ("Finish2t", mpx.getTaskFieldAlias(Task.FINISH2));
      assertEquals ("Finish3t", mpx.getTaskFieldAlias(Task.FINISH3));
      assertEquals ("Finish4t", mpx.getTaskFieldAlias(Task.FINISH4));
      assertEquals ("Finish5t", mpx.getTaskFieldAlias(Task.FINISH5));
      assertEquals ("Finish6t", mpx.getTaskFieldAlias(Task.FINISH6));
      assertEquals ("Finish7t", mpx.getTaskFieldAlias(Task.FINISH7));
      assertEquals ("Finish8t", mpx.getTaskFieldAlias(Task.FINISH8));
      assertEquals ("Finish9t", mpx.getTaskFieldAlias(Task.FINISH9));
      assertEquals ("Finish10t", mpx.getTaskFieldAlias(Task.FINISH10));
      assertEquals ("Cost1t", mpx.getTaskFieldAlias(Task.COST1));
      assertEquals ("Cost2t", mpx.getTaskFieldAlias(Task.COST2));
      assertEquals ("Cost3t", mpx.getTaskFieldAlias(Task.COST3));
      assertEquals ("Cost4t", mpx.getTaskFieldAlias(Task.COST4));
      assertEquals ("Cost5t", mpx.getTaskFieldAlias(Task.COST5));
      assertEquals ("Cost6t", mpx.getTaskFieldAlias(Task.COST6));
      assertEquals ("Cost7t", mpx.getTaskFieldAlias(Task.COST7));
      assertEquals ("Cost8t", mpx.getTaskFieldAlias(Task.COST8));
      assertEquals ("Cost9t", mpx.getTaskFieldAlias(Task.COST9));
      assertEquals ("Cost10t", mpx.getTaskFieldAlias(Task.COST10));
      assertEquals ("Date1t", mpx.getTaskFieldAlias(Task.DATE1));
      assertEquals ("Date2t", mpx.getTaskFieldAlias(Task.DATE2));
      assertEquals ("Date3t", mpx.getTaskFieldAlias(Task.DATE3));
      assertEquals ("Date4t", mpx.getTaskFieldAlias(Task.DATE4));
      assertEquals ("Date5t", mpx.getTaskFieldAlias(Task.DATE5));
      assertEquals ("Date6t", mpx.getTaskFieldAlias(Task.DATE6));
      assertEquals ("Date7t", mpx.getTaskFieldAlias(Task.DATE7));
      assertEquals ("Date8t", mpx.getTaskFieldAlias(Task.DATE8));
      assertEquals ("Date9t", mpx.getTaskFieldAlias(Task.DATE9));
      assertEquals ("Date10t", mpx.getTaskFieldAlias(Task.DATE10));
      assertEquals ("Flag1t", mpx.getTaskFieldAlias(Task.FLAG1));
      assertEquals ("Flag2t", mpx.getTaskFieldAlias(Task.FLAG2));
      assertEquals ("Flag3t", mpx.getTaskFieldAlias(Task.FLAG3));
      assertEquals ("Flag4t", mpx.getTaskFieldAlias(Task.FLAG4));
      assertEquals ("Flag5t", mpx.getTaskFieldAlias(Task.FLAG5));
      assertEquals ("Flag6t", mpx.getTaskFieldAlias(Task.FLAG6));
      assertEquals ("Flag7t", mpx.getTaskFieldAlias(Task.FLAG7));
      assertEquals ("Flag8t", mpx.getTaskFieldAlias(Task.FLAG8));
      assertEquals ("Flag9t", mpx.getTaskFieldAlias(Task.FLAG9));
      assertEquals ("Flag10t", mpx.getTaskFieldAlias(Task.FLAG10));
      assertEquals ("Flag11t", mpx.getTaskFieldAlias(Task.FLAG11));
      assertEquals ("Flag12t", mpx.getTaskFieldAlias(Task.FLAG12));
      assertEquals ("Flag13t", mpx.getTaskFieldAlias(Task.FLAG13));
      assertEquals ("Flag14t", mpx.getTaskFieldAlias(Task.FLAG14));
      assertEquals ("Flag15t", mpx.getTaskFieldAlias(Task.FLAG15));
      assertEquals ("Flag16t", mpx.getTaskFieldAlias(Task.FLAG16));
      assertEquals ("Flag17t", mpx.getTaskFieldAlias(Task.FLAG17));
      assertEquals ("Flag18t", mpx.getTaskFieldAlias(Task.FLAG18));
      assertEquals ("Flag19t", mpx.getTaskFieldAlias(Task.FLAG19));
      assertEquals ("Flag20t", mpx.getTaskFieldAlias(Task.FLAG20));
      assertEquals ("Number1t", mpx.getTaskFieldAlias(Task.NUMBER1));
      assertEquals ("Number2t", mpx.getTaskFieldAlias(Task.NUMBER2));
      assertEquals ("Number3t", mpx.getTaskFieldAlias(Task.NUMBER3));
      assertEquals ("Number4t", mpx.getTaskFieldAlias(Task.NUMBER4));
      assertEquals ("Number5t", mpx.getTaskFieldAlias(Task.NUMBER5));
      assertEquals ("Number6t", mpx.getTaskFieldAlias(Task.NUMBER6));
      assertEquals ("Number7t", mpx.getTaskFieldAlias(Task.NUMBER7));
      assertEquals ("Number8t", mpx.getTaskFieldAlias(Task.NUMBER8));
      assertEquals ("Number9t", mpx.getTaskFieldAlias(Task.NUMBER9));
      assertEquals ("Number10t", mpx.getTaskFieldAlias(Task.NUMBER10));
      assertEquals ("Number11t", mpx.getTaskFieldAlias(Task.NUMBER11));
      assertEquals ("Number12t", mpx.getTaskFieldAlias(Task.NUMBER12));
      assertEquals ("Number13t", mpx.getTaskFieldAlias(Task.NUMBER13));
      assertEquals ("Number14t", mpx.getTaskFieldAlias(Task.NUMBER14));
      assertEquals ("Number15t", mpx.getTaskFieldAlias(Task.NUMBER15));
      assertEquals ("Number16t", mpx.getTaskFieldAlias(Task.NUMBER16));
      assertEquals ("Number17t", mpx.getTaskFieldAlias(Task.NUMBER17));
      assertEquals ("Number18t", mpx.getTaskFieldAlias(Task.NUMBER18));
      assertEquals ("Number19t", mpx.getTaskFieldAlias(Task.NUMBER19));
      assertEquals ("Number20t", mpx.getTaskFieldAlias(Task.NUMBER20));
      assertEquals ("Duration1t", mpx.getTaskFieldAlias(Task.DURATION1));
      assertEquals ("Duration2t", mpx.getTaskFieldAlias(Task.DURATION2));
      assertEquals ("Duration3t", mpx.getTaskFieldAlias(Task.DURATION3));
      assertEquals ("Duration4t", mpx.getTaskFieldAlias(Task.DURATION4));
      assertEquals ("Duration5t", mpx.getTaskFieldAlias(Task.DURATION5));
      assertEquals ("Duration6t", mpx.getTaskFieldAlias(Task.DURATION6));
      assertEquals ("Duration7t", mpx.getTaskFieldAlias(Task.DURATION7));
      assertEquals ("Duration8t", mpx.getTaskFieldAlias(Task.DURATION8));
      assertEquals ("Duration9t", mpx.getTaskFieldAlias(Task.DURATION9));
      assertEquals ("Duration10t", mpx.getTaskFieldAlias(Task.DURATION10));
      assertEquals ("Outline Code1t", mpx.getTaskFieldAlias(Task.OUTLINECODE1));
      assertEquals ("Outline Code2t", mpx.getTaskFieldAlias(Task.OUTLINECODE2));
      assertEquals ("Outline Code3t", mpx.getTaskFieldAlias(Task.OUTLINECODE3));
      assertEquals ("Outline Code4t", mpx.getTaskFieldAlias(Task.OUTLINECODE4));
      assertEquals ("Outline Code5t", mpx.getTaskFieldAlias(Task.OUTLINECODE5));
      assertEquals ("Outline Code6t", mpx.getTaskFieldAlias(Task.OUTLINECODE6));
      assertEquals ("Outline Code7t", mpx.getTaskFieldAlias(Task.OUTLINECODE7));
      assertEquals ("Outline Code8t", mpx.getTaskFieldAlias(Task.OUTLINECODE8));
      assertEquals ("Outline Code9t", mpx.getTaskFieldAlias(Task.OUTLINECODE9));
      assertEquals ("Outline Code10t", mpx.getTaskFieldAlias(Task.OUTLINECODE10));

      assertEquals ("Text1r", mpx.getResourceFieldAlias(Resource.TEXT1));
      assertEquals ("Text2r", mpx.getResourceFieldAlias(Resource.TEXT2));
      assertEquals ("Text3r", mpx.getResourceFieldAlias(Resource.TEXT3));
      assertEquals ("Text4r", mpx.getResourceFieldAlias(Resource.TEXT4));
      assertEquals ("Text5r", mpx.getResourceFieldAlias(Resource.TEXT5));
      assertEquals ("Text6r", mpx.getResourceFieldAlias(Resource.TEXT6));
      assertEquals ("Text7r", mpx.getResourceFieldAlias(Resource.TEXT7));
      assertEquals ("Text8r", mpx.getResourceFieldAlias(Resource.TEXT8));
      assertEquals ("Text9r", mpx.getResourceFieldAlias(Resource.TEXT9));
      assertEquals ("Text10r", mpx.getResourceFieldAlias(Resource.TEXT10));
      assertEquals ("Text11r", mpx.getResourceFieldAlias(Resource.TEXT11));
      assertEquals ("Text12r", mpx.getResourceFieldAlias(Resource.TEXT12));
      assertEquals ("Text13r", mpx.getResourceFieldAlias(Resource.TEXT13));
      assertEquals ("Text14r", mpx.getResourceFieldAlias(Resource.TEXT14));
      assertEquals ("Text15r", mpx.getResourceFieldAlias(Resource.TEXT15));
      assertEquals ("Text16r", mpx.getResourceFieldAlias(Resource.TEXT16));
      assertEquals ("Text17r", mpx.getResourceFieldAlias(Resource.TEXT17));
      assertEquals ("Text18r", mpx.getResourceFieldAlias(Resource.TEXT18));
      assertEquals ("Text19r", mpx.getResourceFieldAlias(Resource.TEXT19));
      assertEquals ("Text20r", mpx.getResourceFieldAlias(Resource.TEXT20));
      assertEquals ("Text21r", mpx.getResourceFieldAlias(Resource.TEXT21));
      assertEquals ("Text22r", mpx.getResourceFieldAlias(Resource.TEXT22));
      assertEquals ("Text23r", mpx.getResourceFieldAlias(Resource.TEXT23));
      assertEquals ("Text24r", mpx.getResourceFieldAlias(Resource.TEXT24));
      assertEquals ("Text25r", mpx.getResourceFieldAlias(Resource.TEXT25));
      assertEquals ("Text26r", mpx.getResourceFieldAlias(Resource.TEXT26));
      assertEquals ("Text27r", mpx.getResourceFieldAlias(Resource.TEXT27));
      assertEquals ("Text28r", mpx.getResourceFieldAlias(Resource.TEXT28));
      assertEquals ("Text29r", mpx.getResourceFieldAlias(Resource.TEXT29));
      assertEquals ("Text30r", mpx.getResourceFieldAlias(Resource.TEXT30));
      assertEquals ("Start1r", mpx.getResourceFieldAlias(Resource.START1));
      assertEquals ("Start2r", mpx.getResourceFieldAlias(Resource.START2));
      assertEquals ("Start3r", mpx.getResourceFieldAlias(Resource.START3));
      assertEquals ("Start4r", mpx.getResourceFieldAlias(Resource.START4));
      assertEquals ("Start5r", mpx.getResourceFieldAlias(Resource.START5));
      assertEquals ("Start6r", mpx.getResourceFieldAlias(Resource.START6));
      assertEquals ("Start7r", mpx.getResourceFieldAlias(Resource.START7));
      assertEquals ("Start8r", mpx.getResourceFieldAlias(Resource.START8));
      assertEquals ("Start9r", mpx.getResourceFieldAlias(Resource.START9));
      assertEquals ("Start10r", mpx.getResourceFieldAlias(Resource.START10));
      assertEquals ("Finish1r", mpx.getResourceFieldAlias(Resource.FINISH1));
      assertEquals ("Finish2r", mpx.getResourceFieldAlias(Resource.FINISH2));
      assertEquals ("Finish3r", mpx.getResourceFieldAlias(Resource.FINISH3));
      assertEquals ("Finish4r", mpx.getResourceFieldAlias(Resource.FINISH4));
      assertEquals ("Finish5r", mpx.getResourceFieldAlias(Resource.FINISH5));
      assertEquals ("Finish6r", mpx.getResourceFieldAlias(Resource.FINISH6));
      assertEquals ("Finish7r", mpx.getResourceFieldAlias(Resource.FINISH7));
      assertEquals ("Finish8r", mpx.getResourceFieldAlias(Resource.FINISH8));
      assertEquals ("Finish9r", mpx.getResourceFieldAlias(Resource.FINISH9));
      assertEquals ("Finish10r", mpx.getResourceFieldAlias(Resource.FINISH10));
      assertEquals ("Cost1r", mpx.getResourceFieldAlias(Resource.COST1));
      assertEquals ("Cost2r", mpx.getResourceFieldAlias(Resource.COST2));
      assertEquals ("Cost3r", mpx.getResourceFieldAlias(Resource.COST3));
      assertEquals ("Cost4r", mpx.getResourceFieldAlias(Resource.COST4));
      assertEquals ("Cost5r", mpx.getResourceFieldAlias(Resource.COST5));
      assertEquals ("Cost6r", mpx.getResourceFieldAlias(Resource.COST6));
      assertEquals ("Cost7r", mpx.getResourceFieldAlias(Resource.COST7));
      assertEquals ("Cost8r", mpx.getResourceFieldAlias(Resource.COST8));
      assertEquals ("Cost9r", mpx.getResourceFieldAlias(Resource.COST9));
      assertEquals ("Cost10r", mpx.getResourceFieldAlias(Resource.COST10));
      assertEquals ("Date1r", mpx.getResourceFieldAlias(Resource.DATE1));
      assertEquals ("Date2r", mpx.getResourceFieldAlias(Resource.DATE2));
      assertEquals ("Date3r", mpx.getResourceFieldAlias(Resource.DATE3));
      assertEquals ("Date4r", mpx.getResourceFieldAlias(Resource.DATE4));
      assertEquals ("Date5r", mpx.getResourceFieldAlias(Resource.DATE5));
      assertEquals ("Date6r", mpx.getResourceFieldAlias(Resource.DATE6));
      assertEquals ("Date7r", mpx.getResourceFieldAlias(Resource.DATE7));
      assertEquals ("Date8r", mpx.getResourceFieldAlias(Resource.DATE8));
      assertEquals ("Date9r", mpx.getResourceFieldAlias(Resource.DATE9));
      assertEquals ("Date10r", mpx.getResourceFieldAlias(Resource.DATE10));
      assertEquals ("Flag1r", mpx.getResourceFieldAlias(Resource.FLAG1));
      assertEquals ("Flag2r", mpx.getResourceFieldAlias(Resource.FLAG2));
      assertEquals ("Flag3r", mpx.getResourceFieldAlias(Resource.FLAG3));
      assertEquals ("Flag4r", mpx.getResourceFieldAlias(Resource.FLAG4));
      assertEquals ("Flag5r", mpx.getResourceFieldAlias(Resource.FLAG5));
      assertEquals ("Flag6r", mpx.getResourceFieldAlias(Resource.FLAG6));
      assertEquals ("Flag7r", mpx.getResourceFieldAlias(Resource.FLAG7));
      assertEquals ("Flag8r", mpx.getResourceFieldAlias(Resource.FLAG8));
      assertEquals ("Flag9r", mpx.getResourceFieldAlias(Resource.FLAG9));
      assertEquals ("Flag10r", mpx.getResourceFieldAlias(Resource.FLAG10));
      assertEquals ("Flag11r", mpx.getResourceFieldAlias(Resource.FLAG11));
      assertEquals ("Flag12r", mpx.getResourceFieldAlias(Resource.FLAG12));
      assertEquals ("Flag13r", mpx.getResourceFieldAlias(Resource.FLAG13));
      assertEquals ("Flag14r", mpx.getResourceFieldAlias(Resource.FLAG14));
      assertEquals ("Flag15r", mpx.getResourceFieldAlias(Resource.FLAG15));
      assertEquals ("Flag16r", mpx.getResourceFieldAlias(Resource.FLAG16));
      assertEquals ("Flag17r", mpx.getResourceFieldAlias(Resource.FLAG17));
      assertEquals ("Flag18r", mpx.getResourceFieldAlias(Resource.FLAG18));
      assertEquals ("Flag19r", mpx.getResourceFieldAlias(Resource.FLAG19));
      assertEquals ("Flag20r", mpx.getResourceFieldAlias(Resource.FLAG20));
      assertEquals ("Number1r", mpx.getResourceFieldAlias(Resource.NUMBER1));
      assertEquals ("Number2r", mpx.getResourceFieldAlias(Resource.NUMBER2));
      assertEquals ("Number3r", mpx.getResourceFieldAlias(Resource.NUMBER3));
      assertEquals ("Number4r", mpx.getResourceFieldAlias(Resource.NUMBER4));
      assertEquals ("Number5r", mpx.getResourceFieldAlias(Resource.NUMBER5));
      assertEquals ("Number6r", mpx.getResourceFieldAlias(Resource.NUMBER6));
      assertEquals ("Number7r", mpx.getResourceFieldAlias(Resource.NUMBER7));
      assertEquals ("Number8r", mpx.getResourceFieldAlias(Resource.NUMBER8));
      assertEquals ("Number9r", mpx.getResourceFieldAlias(Resource.NUMBER9));
      assertEquals ("Number10r", mpx.getResourceFieldAlias(Resource.NUMBER10));
      assertEquals ("Number11r", mpx.getResourceFieldAlias(Resource.NUMBER11));
      assertEquals ("Number12r", mpx.getResourceFieldAlias(Resource.NUMBER12));
      assertEquals ("Number13r", mpx.getResourceFieldAlias(Resource.NUMBER13));
      assertEquals ("Number14r", mpx.getResourceFieldAlias(Resource.NUMBER14));
      assertEquals ("Number15r", mpx.getResourceFieldAlias(Resource.NUMBER15));
      assertEquals ("Number16r", mpx.getResourceFieldAlias(Resource.NUMBER16));
      assertEquals ("Number17r", mpx.getResourceFieldAlias(Resource.NUMBER17));
      assertEquals ("Number18r", mpx.getResourceFieldAlias(Resource.NUMBER18));
      assertEquals ("Number19r", mpx.getResourceFieldAlias(Resource.NUMBER19));
      assertEquals ("Number20r", mpx.getResourceFieldAlias(Resource.NUMBER20));
      assertEquals ("Duration1r", mpx.getResourceFieldAlias(Resource.DURATION1));
      assertEquals ("Duration2r", mpx.getResourceFieldAlias(Resource.DURATION2));
      assertEquals ("Duration3r", mpx.getResourceFieldAlias(Resource.DURATION3));
      assertEquals ("Duration4r", mpx.getResourceFieldAlias(Resource.DURATION4));
      assertEquals ("Duration5r", mpx.getResourceFieldAlias(Resource.DURATION5));
      assertEquals ("Duration6r", mpx.getResourceFieldAlias(Resource.DURATION6));
      assertEquals ("Duration7r", mpx.getResourceFieldAlias(Resource.DURATION7));
      assertEquals ("Duration8r", mpx.getResourceFieldAlias(Resource.DURATION8));
      assertEquals ("Duration9r", mpx.getResourceFieldAlias(Resource.DURATION9));
      assertEquals ("Duration10r", mpx.getResourceFieldAlias(Resource.DURATION10));
      assertEquals ("Outline Code1r", mpx.getResourceFieldAlias(Resource.OUTLINECODE1));
      assertEquals ("Outline Code2r", mpx.getResourceFieldAlias(Resource.OUTLINECODE2));
      assertEquals ("Outline Code3r", mpx.getResourceFieldAlias(Resource.OUTLINECODE3));
      assertEquals ("Outline Code4r", mpx.getResourceFieldAlias(Resource.OUTLINECODE4));
      assertEquals ("Outline Code5r", mpx.getResourceFieldAlias(Resource.OUTLINECODE5));
      assertEquals ("Outline Code6r", mpx.getResourceFieldAlias(Resource.OUTLINECODE6));
      assertEquals ("Outline Code7r", mpx.getResourceFieldAlias(Resource.OUTLINECODE7));
      assertEquals ("Outline Code8r", mpx.getResourceFieldAlias(Resource.OUTLINECODE8));
      assertEquals ("Outline Code9r", mpx.getResourceFieldAlias(Resource.OUTLINECODE9));
      assertEquals ("Outline Code10r", mpx.getResourceFieldAlias(Resource.OUTLINECODE10));
   }
   
   /**
    * Write a file with embedded line break (\r and \n) characters in
    * various text fields. Ensure that a valid file is written,
    * and that it can be read successfully.
    *
    * @throws Exception
    */
   public void testEmbeddedLineBreaks ()
      throws Exception
   {
      File out = null;
      boolean success = false;

      try
      {
         //
         // Create a simple MPX file
         //
         SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
         ProjectFile file = new ProjectFile ();
         file.setAutoTaskID(true);
         file.setAutoTaskUniqueID(true);
         file.setAutoResourceID(true);
         file.setAutoResourceUniqueID(true);
         file.setAutoOutlineLevel(true);
         file.setAutoOutlineNumber(true);
         file.setAutoWBS(true);
         file.setAutoCalendarUniqueID(true);
         file.addDefaultBaseCalendar();

         ProjectHeader header = file.getProjectHeader();
         header.setComments("Project Header Comments: Some\rExample\nText\r\nWith\n\rBreaks");
         header.setStartDate(df.parse("01/01/2003"));

         Resource resource1 = file.addResource();
         resource1.setName("Resource1: Some\rExample\nText\r\nWith\n\rBreaks");
         resource1.setNotes("Resource1 Notes: Some\rExample\nText\r\nWith\n\rBreaks");

         Task task1 = file.addTask();
         task1.setName ("Task1: Some\rExample\nText\r\nWith\n\rBreaks");
         task1.setNotes("Task1 Notes: Some\rExample\nText\r\nWith\n\rBreaks");

         //
         // Write the file
         //
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write(file, out);

         //
         // Ensure we can read it successfully
         //
         file = new MPXReader().read(out);
         assertEquals(1, file.getAllTasks().size());
         assertEquals(1, file.getAllResources().size());

         header = file.getProjectHeader();
         assertEquals("Project Header Comments: Some\nExample\nText\nWith\nBreaks", header.getComments());

         task1 = file.getTaskByUniqueID(new Integer(1));
         assertEquals("Task1: Some\nExample\nText\nWith\nBreaks", task1.getName());
         assertEquals("Task1 Notes: Some\nExample\nText\nWith\nBreaks", task1.getNotes());

         resource1 = file.getResourceByUniqueID(new Integer(1));
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
   public void testPasswordProtection ()
      throws Exception
   {
      File in;

      //
      // Read password (password1)
      //
      try
      {
         in = new File (m_basedir + "/readpassword9.mpp");
         new MPPReader().read (in);
         assertTrue(false);
      }

      catch (MPXJException ex)
      {
         assertEquals(MPXJException.PASSWORD_PROTECTED, ex.getMessage());
      }

      //
      // Write password (password2)
      //
      in = new File (m_basedir + "/writepassword9.mpp");
      new MPPReader().read (in);

      //
      // Read password
      //
      try
      {
         in = new File (m_basedir + "/bothpassword9.mpp");
         new MPPReader().read (in);
         assertTrue(false);
      }

      catch (MPXJException ex)
      {
         assertEquals(MPXJException.PASSWORD_PROTECTED, ex.getMessage());
      }
   }

   /**
    * This test ensures that the task and resource extended attributes are
    * read and writen correctly for MSPDI files.
    *
    * @throws Exception
    */
   public void testMspdiExtendedAttributes ()
      throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      MSPDIWriter writer = new MSPDIWriter();
      
      ProjectFile xml = reader.read (m_basedir + "/mspextattr.xml");
      commonMspdiExtendedAttributeTests (xml);

      File out = File.createTempFile ("junit", ".xml");
      writer.write(xml, out);

      xml = reader.read (out);
      commonMspdiExtendedAttributeTests (xml);

      out.delete();
   }

   /**
    * Common tests for MSPDI fileExtended Attribute values.
    *
    * @param xml MSPDI file
    */
   private void commonMspdiExtendedAttributeTests (ProjectFile xml)
   {
      List tasks = xml.getAllTasks();
      assertEquals (2, tasks.size());
      SimpleDateFormat df = new SimpleDateFormat ("dd/MM/yyyy");

      Task task = (Task)tasks.get(1);
      assertEquals("Task Text One", task.getText1());
      assertEquals("01/01/2004", df.format(task.getStart1()));
      assertEquals("31/12/2004", df.format(task.getFinish1()));
      assertEquals(99.95, task.getCost1().doubleValue(), 0.0);
      assertEquals("18/07/2004", df.format(task.getDate1()));
      assertTrue(task.getFlag1Value());
      assertEquals(55.56, task.getNumber1Value(), 0.0);
      assertEquals(13.0, task.getDuration1().getDuration(), 0.0);
      assertEquals(TimeUnit.DAYS, task.getDuration1().getUnits());

      List resources = xml.getAllResources();
      assertEquals(2, resources.size());

      Resource resource = (Resource)resources.get(1);
      assertEquals("Resource Text One", resource.getText1());
      assertEquals("01/01/2003", df.format(resource.getStart1()));
      assertEquals("31/12/2003", df.format(resource.getFinish1()));
      assertEquals(29.99, resource.getCost1().doubleValue(), 0.0);
      assertEquals("18/07/2003", df.format(resource.getDate1()));
      assertTrue(resource.getFlag1());
      assertEquals(5.99, resource.getNumber1Value(), 0.0);
      assertEquals(22.0, resource.getDuration1().getDuration(), 0.0);
      assertEquals(TimeUnit.DAYS, resource.getDuration1().getUnits());
   }

   /**
    * This ensures that values in the project header are read and written
    * as expected.
    *
    * @throws Exception
    */
   public void testProjectHeader ()
      throws Exception
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
         ProjectFile mpx = reader.read (m_basedir + "/headertest.mpx");
         testHeaderFields(mpx);

         //
         // Write the file, re-read it and test to ensure that
         // the project header fields have the expected values
         //
         out = File.createTempFile ("junit", ".mpx");
         writer.write (mpx,out);
         mpx = reader.read(out);
         testHeaderFields(mpx);
         out.delete();
         out = null;

         //
         // Read the MPP8 file and ensure that the project header fields
         // have the expected values.
         //
         mpx = new MPPReader().read (m_basedir + "/headertest8.mpp");
         testHeaderFields(mpx);

         //
         // Read the MPP9 file and ensure that the project header fields
         // have the expected values.
         //
         mpx = new MPPReader().read (m_basedir + "/headertest9.mpp");
         testHeaderFields(mpx);

         //
         // Read the MSPDI file and ensure that the project header fields
         // have the expected values.
         //
         mpx = new MSPDIReader().read (m_basedir + "/headertest.xml");
         testHeaderFields(mpx);

         //
         // Write the file, re-read it and test to ensure that
         // the project header fields have the expected values
         //
         out = File.createTempFile ("junit", ".xml");
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
   private void testHeaderFields (ProjectFile file)
   {
      ProjectHeader header = file.getProjectHeader();
      assertEquals ("Project Title Text", header.getProjectTitle());
      assertEquals ("Author Text", header.getAuthor());
      assertEquals ("Comments Text", header.getComments());
      assertEquals ("Company Text", header.getCompany());
      assertEquals ("Keywords Text", header.getKeywords());
      assertEquals ("Manager Text", header.getManager());
      assertEquals ("Subject Text", header.getSubject());
   }

   /**
    * Test retrieval of WBS information.
    *
    * @throws Exception
    */
   public void testWBS ()
      throws Exception
   {
      ProjectFile mpp = new MPPReader().read (m_basedir + "/sample98.mpp");
      Task task = mpp.getTaskByUniqueID(new Integer(2));
      assertEquals("Second Task", task.getName());
      assertEquals("1.1", task.getWBS());

      mpp = new MPPReader().read (m_basedir + "/sample.mpp");
      task = mpp.getTaskByUniqueID(new Integer(2));
      assertEquals("Second Task", task.getName());
      assertEquals("1.1", task.getWBS());
   }

   /**
    * Test read and write of priority information.
    *
    * @throws Exception
    */   
   public void testPriority ()
      throws Exception
   {
      ProjectFile mpx = new MPXReader().read (m_basedir + "/mpxpriority.mpx");
      validatePriority(mpx);
      
      ProjectFile mpp8 = new MPPReader().read (m_basedir + "/mpp8priority.mpp");
      validatePriority(mpp8);
      
      ProjectFile mpp9 = new MPPReader().read (m_basedir + "/mpp9priority.mpp");
      validatePriority(mpp9);
      
      ProjectFile xml = new MSPDIReader().read (m_basedir + "/mspdipriority.xml");
      validatePriority(xml);

      File out = null;
      boolean success = false;
      try
      {
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpx, out);
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
         out = File.createTempFile ("junit", ".xml");
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
   private void validatePriority (ProjectFile file)
   {
      assertEquals(Priority.DO_NOT_LEVEL, file.getTaskByUniqueID(new Integer(1)).getPriority().getValue());
      assertEquals(Priority.HIGHEST, file.getTaskByUniqueID(new Integer(2)).getPriority().getValue());      
      assertEquals(Priority.VERY_HIGH, file.getTaskByUniqueID(new Integer(3)).getPriority().getValue());
      assertEquals(Priority.HIGHER, file.getTaskByUniqueID(new Integer(4)).getPriority().getValue());
      assertEquals(Priority.HIGH, file.getTaskByUniqueID(new Integer(5)).getPriority().getValue());
      assertEquals(Priority.MEDIUM, file.getTaskByUniqueID(new Integer(6)).getPriority().getValue());
      assertEquals(Priority.LOW, file.getTaskByUniqueID(new Integer(7)).getPriority().getValue());
      assertEquals(Priority.LOWER, file.getTaskByUniqueID(new Integer(8)).getPriority().getValue());
      assertEquals(Priority.VERY_LOW, file.getTaskByUniqueID(new Integer(9)).getPriority().getValue());
      assertEquals(Priority.LOWEST, file.getTaskByUniqueID(new Integer(10)).getPriority().getValue());      
   }
   
   /**
    * Tests to exercise calendar functionality.
    * 
    * @throws Exception
    */
   public void testCalendars ()
      throws Exception
   {
      ProjectFile mpp = new MPPReader().read (m_basedir + "/caltest98.mpp");
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
   private void validateResourceCalendars (ProjectFile mpx)
   {
      //
      // Resource calendar based on standard calendar
      //
      Resource resource = mpx.getResourceByUniqueID(new Integer(1));      
      ProjectCalendar calendar = resource.getResourceCalendar();
      assertEquals("Resource One", calendar.getName());
      assertFalse(calendar.isBaseCalendar());
      assertEquals("Standard", calendar.getBaseCalendar().getName());
      assertTrue(calendar.getCalendarExceptions().isEmpty());
      
      //
      // Resource calendar based on base calendar
      //
      resource = mpx.getResourceByUniqueID(new Integer(2));
      calendar = resource.getResourceCalendar();
      assertEquals("Resource Two", calendar.getName());
      assertFalse(calendar.isBaseCalendar());
      assertEquals("Base Calendar", calendar.getBaseCalendar().getName());
      assertTrue(calendar.getCalendarExceptions().isEmpty());
      
      //
      // Resource calendar based on modified base calendar
      //
      resource = mpx.getResourceByUniqueID(new Integer(3));
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
   private void validateTaskCalendars (ProjectFile mpx)
   {
      Task task = mpx.getTaskByUniqueID(new Integer(2));
      ProjectCalendar calendar = task.getCalendar();
      assertNull(calendar);
      
      task = mpx.getTaskByUniqueID(new Integer(3));
      calendar = task.getCalendar();      
      assertEquals("Standard", calendar.getName());
      assertTrue(calendar.isBaseCalendar());
      
      task = mpx.getTaskByUniqueID(new Integer(4));
      calendar = task.getCalendar();      
      assertEquals("Base Calendar", calendar.getName());
      assertTrue(calendar.isBaseCalendar());      
   }

   /**
    * Test to exercise task, resource, and assignment removal code.
    * 
    * @throws Exception
    */
   public void testRemoval ()
      throws Exception
   {      
      //
      // Load the file and validate the number of
      // tasks, resources, and assignments.
      //
      ProjectFile mpp = new MPPReader().read (m_basedir + "/remove.mpp");
      assertEquals(9, mpp.getAllTasks().size());
      assertEquals(8, mpp.getAllResources().size());
      assertEquals(6, mpp.getAllResourceAssignments().size());
      
      //
      // Remove a task with no assignments
      //
      Task task = mpp.getTaskByUniqueID(new Integer(1));
      assertEquals("Task One", task.getName());
      task.remove();
      assertEquals(8, mpp.getAllTasks().size());
      assertEquals(8, mpp.getAllResources().size());
      assertEquals(6, mpp.getAllResourceAssignments().size());
      
      //
      // Remove a resource with no assignments
      //
      Resource resource = mpp.getResourceByUniqueID(new Integer(1));
      assertEquals("Resource One", resource.getName());
      resource.remove();
      assertEquals(8, mpp.getAllTasks().size());
      assertEquals(7, mpp.getAllResources().size());
      assertEquals(6, mpp.getAllResourceAssignments().size());

      //
      // Remove a task with a single assignment
      //
      task = mpp.getTaskByUniqueID(new Integer(2));
      assertEquals("Task Two", task.getName());
      task.remove();
      assertEquals(7, mpp.getAllTasks().size());
      assertEquals(7, mpp.getAllResources().size());
      assertEquals(5, mpp.getAllResourceAssignments().size());      
      
      //
      // Remove a resource with a single assignment
      //
      resource = mpp.getResourceByUniqueID(new Integer(3));
      assertEquals("Resource Three", resource.getName());
      resource.remove();
      assertEquals(7, mpp.getAllTasks().size());
      assertEquals(6, mpp.getAllResources().size());
      assertEquals(4, mpp.getAllResourceAssignments().size());      
      
      //
      // Remove an assignment
      //
      task = mpp.getTaskByUniqueID(new Integer(5));
      assertEquals("Task Five", task.getName());
      List assignments = task.getResourceAssignments();
      assertEquals(2, assignments.size());
      ResourceAssignment assignment = (ResourceAssignment)assignments.get(0);
      resource = assignment.getResource();
      assertEquals("Resource Six", resource.getName());
      assignments = resource.getTaskAssignments();
      assertEquals(1, assignments.size());
      assignment.remove();
      assignments = task.getResourceAssignments();      
      assertEquals(1, assignments.size());
      assignments = resource.getTaskAssignments();
      assertEquals(0, assignments.size());      
      assertEquals(7, mpp.getAllTasks().size());
      assertEquals(6, mpp.getAllResources().size());
      assertEquals(3, mpp.getAllResourceAssignments().size());    

      //
      // Remove a task with child tasks - the child tasks will also be removed
      //
      task = mpp.getTaskByUniqueID(new Integer(8));
      assertEquals("Task Eight", task.getName());
      task.remove();
      assertEquals(5, mpp.getAllTasks().size());
      assertEquals(6, mpp.getAllResources().size());
      assertEquals(3, mpp.getAllResourceAssignments().size());    
      
      //
      // As we have removed tasks and resources, call the synchronize methods
      // to generate ID seqwuences without gaps. This will allow MS Project
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
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write (mpp, out);
         
         ProjectFile mpx = new MPXReader().read(out);         
         assertEquals(5, mpx.getAllTasks().size());
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
    * Exercise split task functionality.
    * 
    * @throws Exception
    */
   public void testSplits ()
      throws Exception
   {
      List splits = new LinkedList();
      splits.add(Duration.getInstance(32, TimeUnit.HOURS));
      splits.add(Duration.getInstance(40, TimeUnit.HOURS));      
      splits.add(Duration.getInstance(88, TimeUnit.HOURS));
      
      ProjectFile mpp = new MPPReader().read (m_basedir + "/splits9.mpp");
      Task task = mpp.getTaskByUniqueID(new Integer(1));
      assertNull(task.getSplits());

      task = mpp.getTaskByUniqueID(new Integer(2));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(3));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(4));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(5));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(6));      
      assertEquals(splits, task.getSplits());
      
      splits.clear();
      splits.add(Duration.getInstance(16, TimeUnit.HOURS));      
      splits.add(Duration.getInstance(48, TimeUnit.HOURS));      
      splits.add(Duration.getInstance(88, TimeUnit.HOURS));      
      splits.add(Duration.getInstance(104, TimeUnit.HOURS));      
      splits.add(Duration.getInstance(128, TimeUnit.HOURS));      
      
      task = mpp.getTaskByUniqueID(new Integer(7));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(8));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(9));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(10));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(11));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(12));      
      assertEquals(splits, task.getSplits());
      
      task = mpp.getTaskByUniqueID(new Integer(13));      
      assertEquals(splits, task.getSplits());                              
   }
   
   /**
    * Basic rewrite test to exercise th MPX calendar exception read/write code.
    * 
    * @throws Exception
    */
   public void testMPXCalendarExceptions ()
      throws Exception
   {
      File out = null;
      boolean success = true;
   
      try
      {
         File in = new File (m_basedir + "/calendarExceptions.mpx");
         ProjectFile mpx = new MPXReader().read (in);
         out = File.createTempFile ("junit", ".mpx");
         new MPXWriter().write(mpx, out, false);
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
    * As part of the bug reports that are submitted for MPXJ I am passed a
    * number of confidential project files, which for obvious reasons cannot
    * be redistributed as test cases. These files reside in a directory on
    * my development machine, and asuming that this directory exists, this
    * test will attempt of read each of the files in turn.
    *
    * @throws Exception
    */
   public void testCustomerData ()
      throws Exception
   {
      MPPReader mppReader = new MPPReader();
      MPXReader mpxReader = new MPXReader();
      MSPDIReader mspdiReader = new MSPDIReader();
      
      File dir = new File ("c:\\tapsterrock\\mpxj\\data");
      if (dir.exists() == true && dir.isDirectory() == true)
      {
         ProjectFile mpxj;
         int failures = 0;
         File[] files = dir.listFiles();
         File file;
         String name;
         for (int loop=0; loop < files.length; loop++)
         {
            file = files[loop];
            name = file.getName().toUpperCase();

            try
            {
               if (name.endsWith(".MPP") == true)
               {
                  mpxj = mppReader.read(file);
                  validateMpp (file.getCanonicalPath(), mpxj);
               }
               else
               {
                  if (name.endsWith(".MPX") == true)
                  {
                     mpxReader.setLocale(Locale.ENGLISH);
                     
                     if (name.indexOf(".DE.") != -1)
                     {
                        mpxReader.setLocale(Locale.GERMAN);
                     }

                     if (name.indexOf(".SV.") != -1)
                     {
                        mpxReader.setLocale(new Locale ("sv"));
                     }

                     mpxj = mpxReader.read(file);
                  }
                  else
                  {
                     if (name.endsWith(".XML") == true && 
                         name.indexOf(".MPP.") == -1)
                     {
                        mpxj = mspdiReader.read(file);
                     }
                  }
               }
            }

            catch (Exception ex)
            {
               System.out.println ("Failed to read " + name);
               ex.printStackTrace();
               ++failures;
            }
         }

         assertEquals("Failed to read " + failures + " files", 0, failures);
      }
   }

   /**
    * As part of the regression test process, I save customer's MPP files
    * as MSPDI files using a version of MS Project. This method allows these
    * two versions to be compared in order to ensure that MPXJ is
    * correctly reading the data from both file formats.
    * 
    * @param name file name
    * @param mpp MPP file data structure
    * @throws Exception
    */
   private void validateMpp (String name, ProjectFile mpp)
      throws Exception
   {
      File xmlFile = new File (name + ".xml");
      if (xmlFile.exists() == true)
      {
         ProjectFile xml = new MSPDIReader().read(xmlFile);
         MppXmlCompare compare = new MppXmlCompare();
         compare.process(xml, mpp);
      }
   }
   
   private String m_basedir;
}

