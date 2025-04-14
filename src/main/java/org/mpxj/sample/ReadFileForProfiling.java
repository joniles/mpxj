/*
 * file:       ReadFileForProfiling.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       22/05/2023
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

package org.mpxj.sample;

import org.mpxj.ProjectFile;
import org.mpxj.reader.UniversalProjectReader;

/**
 * Simple test harness for profiling execution when reading a file.
 */
public class ReadFileForProfiling
{
   /**
    * Main entry point.
    *
    * @param argv arguments
    */
   public static void main(String[] argv) throws Exception
   {
      long start = System.currentTimeMillis();
      ProjectFile file = new UniversalProjectReader().read(argv[0]);
      long duration = System.currentTimeMillis() - start;

      if (file == null)
      {
         System.out.println("Failed to read file");
      }
      else
      {
         System.out.println("File read in " + duration + "ms");
      }
   }
}
