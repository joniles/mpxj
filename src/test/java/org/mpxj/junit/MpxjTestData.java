/*
 * file:       MpxjTestData.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       20/08/2014
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

/**
 * Simple utility class to provide access to named test data files.
 */
public class MpxjTestData
{
   public static final String DATA_DIR;
   static
   {
      String dataDirValue = null;
      File dataDir = new File("junit/data");
      if (dataDir.exists() && dataDir.isDirectory())
      {
         try
         {
            dataDirValue = dataDir.getCanonicalPath();
         }
         catch (IOException ex)
         {
            // Ignore this
         }
      }
      else
      {
         dataDirValue = System.getProperty("mpxj.junit.datadir");
      }

      if (dataDirValue == null || dataDirValue.isEmpty())
      {
         fail("missing datadir property");
      }

      DATA_DIR = dataDirValue;
   }

   /**
    * Retrieve the path to a test data file.
    *
    * @param fileName test data file name
    * @return file path
    */
   public static String filePath(String fileName)
   {
      return DATA_DIR + "/" + fileName;
   }

   /**
    * Helper method used to retrieve a list of test files.
    *
    * @param path path to test files
    * @param name file name prefix
    * @return array of files
    */
   public static File[] listFiles(String path, final String name)
   {
      File testDataDir = new File(filePath(path));
      File[] result = testDataDir.listFiles(pathname -> pathname.getName().startsWith(name));
      if (result == null)
      {
         result = new File[0];
      }
      return result;
   }

}
