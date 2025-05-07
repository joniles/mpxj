/*
 * file:       MpxjBatchConvert.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2017
 * date:       19/07/2017
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

import java.io.File;

/**
 * This is a general utility designed to multiple files in one directory
 * into a different file format.
 */
public final class MpxjBatchConvert
{
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length != 4)
         {
            System.out.println("Usage: MpxjBatchConvert <source directory> <source suffix> <target directory> <target suffix>");
         }
         else
         {
            File sourceDirectory = new File(args[0]);
            final String sourceSuffix = args[1];
            String targetDirectory = args[2];
            String targetSuffix = args[3];

            File[] fileList = sourceDirectory.listFiles(pathname -> pathname.getName().endsWith(sourceSuffix));

            if (fileList != null)
            {
               MpxjConvert convert = new MpxjConvert();
               for (File file : fileList)
               {
                  String oldName = file.getName();
                  String newName = oldName.substring(0, oldName.length() - sourceSuffix.length()) + targetSuffix;
                  File newFile = new File(targetDirectory, newName);
                  convert.process(file.getCanonicalPath(), newFile.getCanonicalPath());
               }
            }
         }

         System.exit(0);
      }

      catch (Exception ex)
      {
         System.out.println();
         System.out.print("Conversion Error: ");
         ex.printStackTrace(System.out);
         System.out.println();
         System.exit(1);
      }
   }
}
