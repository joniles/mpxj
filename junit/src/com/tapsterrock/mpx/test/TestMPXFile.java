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

import java.text.ParseException;
import java.io.FileNotFoundException;
import java.io.IOException;
import junit.framework.*;
import com.tapsterrock.mpx.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;
import java.io.FileInputStream;


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
   public void testRewrite ()
      throws Exception
   {
      File out = null;

      try
      {
         File in = new File (m_basedir + "/sample.mpx");
         MPXFile mpx = new MPXFile (in);
         out = File.createTempFile ("junit", ".mpx");
         mpx.write (out);
         assertTrue ("Files are not identical", compareFiles (in, out));
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
    * This test exercises the automatic generation of WBS and outline levels.
    */
   public void testAutomaticGeneration ()
      throws Exception
   {
      MPXFile file = new MPXFile();

      file.setAutoWBS(true);
      file.setAutoOutlineLevel(true);

      Task task1 = file.addTask();
      assertEquals (task1.getWBS(), "1.0");
      assertEquals (task1.getOutlineLevel().intValue(), 1);

      task1 = file.addTask();
      assertEquals (task1.getWBS(), "2.0");
      assertEquals (task1.getOutlineLevel().intValue(), 1);

      task1 = file.addTask();
      assertEquals (task1.getWBS(), "3.0");
      assertEquals (task1.getOutlineLevel().intValue(), 1);

      Task task2 = task1.addTask();
      assertEquals (task2.getWBS(), "3.1");
      assertEquals (task2.getOutlineLevel().intValue(), 2);

      task2 = task1.addTask();
      assertEquals (task2.getWBS(), "3.2");
      assertEquals (task2.getOutlineLevel().intValue(), 2);

      Task task3 = task2.addTask();
      assertEquals (task3.getWBS(), "3.2.1");
      assertEquals (task3.getOutlineLevel().intValue(), 3);

      task3 = task2.addTask();
      assertEquals (task3.getWBS(), "3.2.2");
      assertEquals (task3.getOutlineLevel().intValue(), 3);
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

