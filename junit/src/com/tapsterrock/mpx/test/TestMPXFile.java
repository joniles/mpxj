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

import junit.framework.*;
import com.tapsterrock.mpx.*;
import com.tapsterrock.mpp.*;
import com.tapsterrock.mspdi.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;

/**
 * This class contains a small set of tests to exercise the MPX library.
 */
public class TestMPXFile extends TestCase
{
   /**
    * Constructor. Note that the system property mpxj.junit.datadir must
    * be defined to allow the test code to find the required sample files.
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

      Task task3 = task1.addTask();
      LinkedList children = task1.getChildTasks();
      assertEquals (children.size(), 2);

      LinkedList toplevel = file.getChildTasks();
      assertEquals (toplevel.size(), 1);      
   }

   /**
    * Exercise the MPP import code.
    */
   public void testConversion1 ()
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
   public void testConversion2 ()
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
    * Utility function to ensure that two files contain identical data.
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

   private String m_basedir;
}

