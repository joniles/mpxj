/*
 * file:       CustomerDataTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       27/11/2008
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.FileHelper;
import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.writer.ProjectWriter;

/**
 * The tests contained in this class exercise MPXJ
 * using customer supplied data.
 */
public class CustomerDataTest
{
   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData1() throws Exception
   {
      testCustomerData(1, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData2() throws Exception
   {
      testCustomerData(2, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData3() throws Exception
   {
      testCustomerData(3, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData4() throws Exception
   {
      testCustomerData(4, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData5() throws Exception
   {
      testCustomerData(5, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData6() throws Exception
   {
      testCustomerData(6, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData7() throws Exception
   {
      testCustomerData(7, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData8() throws Exception
   {
      testCustomerData(8, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData9() throws Exception
   {
      testCustomerData(9, 10);
   }

   /**
    * Test customer data.
    *
    * @throws Exception
    */
   @Test public void testCustomerData10() throws Exception
   {
      testCustomerData(10, 10);
   }

   /**
    * As part of the bug reports that are submitted for MPXJ I am passed a
    * number of confidential project files, which for obvious reasons cannot
    * be redistributed as test cases. These files reside in a directory on
    * my development machine, and assuming that this directory exists, this
    * test will attempt of read each of the files in turn.
    *
    * @param index current chunk
    * @param max maximum number of chunks
    * @throws Exception
    */
   private void testCustomerData(int index, int max) throws Exception
   {
      String dirName = System.getProperty("mpxj.junit.privatedir");
      if (dirName != null && dirName.length() != 0)
      {
         File dir = new File(dirName);
         if (dir.exists() && dir.isDirectory())
         {
            List<File> files = new ArrayList<File>();
            listFiles(files, dir);

            int interval = files.size() / max;
            int startIndex = (index - 1) * interval;
            int endIndex;
            if (index == max)
            {
               endIndex = files.size();
            }
            else
            {
               endIndex = startIndex + interval;
            }

            testFiles(files.subList(startIndex, endIndex));
         }
      }
   }

   /**
    * Recursively descend through the test data directory adding files to the list.
    *
    * @param list file list
    * @param parent parent directory
    */
   private void listFiles(List<File> list, File parent)
   {
      String runtime = System.getProperty("java.runtime.name");
      boolean isIKVM = runtime != null && runtime.indexOf("IKVM") != -1;
      File[] fileList = parent.listFiles();
      assertNotNull(fileList);

      for (File file : fileList)
      {
         if (file.isDirectory())
         {
            listFiles(list, file);
         }
         else
         {
            String name = file.getName().toLowerCase();
            if (isIKVM && (name.endsWith(".mpd") || name.endsWith(".mdb")))
            {
               continue;
            }
            list.add(file);
         }
      }
   }

   /**
    * Validate that all of the files in the list can be read by MPXJ.
    *
    * @param files file list
    */
   private void testFiles(List<File> files)
   {
      UniversalProjectReader reader = new UniversalProjectReader();
      MPXReader mpxReader = new MPXReader();
      PrimaveraXERFileReader xerReader = new PrimaveraXERFileReader();

      int failures = 0;
      for (File file : files)
      {
         String name = file.getName().toUpperCase();
         ProjectFile mpxj = null;
         //System.out.println(name);

         try
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
                  mpxReader.setLocale(new Locale("sv"));
               }

               mpxj = mpxReader.read(file);
            }
            else
            {
               mpxj = reader.read(file);
               if (name.endsWith(".MPP"))
               {
                  validateMpp(file.getCanonicalPath(), mpxj);
               }

               // If we have an XER file, exercise the "readAll" functionality
               // For now, ignore files with non-standard encodings.
               if (name.endsWith(".XER") && !name.endsWith(".ENCODING.XER"))
               {
                  xerReader.readAll(new FileInputStream(file), true);
               }
            }

            if (mpxj == null)
            {
               System.err.println("Failed to read " + name);
               ++failures;
            }
            else
            {
               for (Class<? extends ProjectWriter> c : WRITER_CLASSES)
               {
                  File outputFile = File.createTempFile("writer_test", ".dat");
                  outputFile.deleteOnExit();
                  ProjectWriter p = c.newInstance();
                  p.write(mpxj, outputFile);
                  FileHelper.deleteQuietly(outputFile);
               }
            }
         }
         
         catch (Exception ex)
         {
            System.err.println("Failed to read " + name);
            ex.printStackTrace();
            ++failures;
         }
      }

      assertEquals("Failed to read " + failures + " files", 0, failures);
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
   private void validateMpp(String name, ProjectFile mpp) throws Exception
   {
      File xmlFile = new File(name + ".xml");
      if (xmlFile.exists() == true)
      {
         ProjectFile xml = new MSPDIReader().read(xmlFile);
         MppXmlCompare compare = new MppXmlCompare();
         compare.process(xml, mpp);
      }
   }

   private static final List<Class<? extends ProjectWriter>> WRITER_CLASSES = new ArrayList<Class<? extends ProjectWriter>>();

   static
   {
      WRITER_CLASSES.add(JsonWriter.class);
      WRITER_CLASSES.add(MPXWriter.class);
      WRITER_CLASSES.add(MSPDIWriter.class);
      WRITER_CLASSES.add(PlannerWriter.class);
      WRITER_CLASSES.add(PrimaveraPMFileWriter.class);
      //      WRITER_CLASSES.add(SDEFWriter.class);
   }
}
