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

import java.io.File;
import java.util.Locale;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.primavera.PrimaveraFileReader;

/**
 * The tests contained in this class exercise MPXJ
 * using customer supplied data.
 */
public class CustomerDataTest extends MPXJTestCase
{
   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData1() throws Exception
   {
      testCustomerData(1, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData2() throws Exception
   {
      testCustomerData(2, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData3() throws Exception
   {
      testCustomerData(3, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData4() throws Exception
   {
      testCustomerData(4, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData5() throws Exception
   {
      testCustomerData(5, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData6() throws Exception
   {
      testCustomerData(6, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData7() throws Exception
   {
      testCustomerData(7, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData8() throws Exception
   {
      testCustomerData(8, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData9() throws Exception
   {
      testCustomerData(9, 10);
   }

   /**
    * Test customer data.
    * 
    * @throws Exception
    */
   public void testCustomerData10() throws Exception
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
         if (dir.exists() == true && dir.isDirectory() == true)
         {
            MPPReader mppReader = new MPPReader();
            MPXReader mpxReader = new MPXReader();
            MSPDIReader mspdiReader = new MSPDIReader();
            PrimaveraFileReader primaveraReader = new PrimaveraFileReader();

            ProjectFile mpxj;
            int failures = 0;
            File[] files = dir.listFiles();

            int interval = files.length / max;
            int startIndex = (index - 1) * interval;
            int endIndex;
            if (index == max)
            {
               endIndex = files.length;
            }
            else
            {
               endIndex = startIndex + interval;
            }

            //System.out.println(startIndex + " " + (endIndex-1));

            for (int loop = startIndex; loop < endIndex; loop++)
            {
               File file = files[loop];
               String name = file.getName().toUpperCase();

               try
               {
                  if (name.endsWith(".MPP") == true)
                  {
                     mpxj = mppReader.read(file);
                     validateMpp(file.getCanonicalPath(), mpxj);
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
                           mpxReader.setLocale(new Locale("sv"));
                        }

                        mpxj = mpxReader.read(file);
                     }
                     else
                     {
                        if (name.endsWith(".XML") == true && name.indexOf(".MPP.") == -1)
                        {
                           mpxj = mspdiReader.read(file);
                        }
                        else
                        {
                           if (name.endsWith(".XER") == true)
                           {
                              mpxj = primaveraReader.read(file);
                           }
                        }
                     }
                  }
               }

               catch (Exception ex)
               {
                  System.out.println("Failed to read " + name);
                  ex.printStackTrace();
                  ++failures;
               }
            }

            assertEquals("Failed to read " + failures + " files", 0, failures);
         }
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
}
