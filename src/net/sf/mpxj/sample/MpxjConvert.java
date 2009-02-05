/*
 * file:       MpxjConvert.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2005
 * date:       07/12/2005
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

package net.sf.mpxj.sample;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.writer.ProjectWriterUtility;

/**
 * This is a general utility designed to convert from one project file format
 * to another. It will typically be used as a command line utility. The
 * user passes in two arguments, the input file name and the output file name.
 * The type of each file is determined by its extension.
 */
public final class MpxjConvert
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
         if (args.length != 2)
         {
            System.out.println("Usage: MpxjConvert <input file name> <output file name>");
         }
         else
         {
            MpxjConvert convert = new MpxjConvert();
            convert.process(args[0], args[1]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }

   /**
    * Convert one project file format to another.
    *
    * @param inputFile input file
    * @param outputFile output file
    * @throws Exception
    */
   public void process(String inputFile, String outputFile) throws Exception
   {
      System.out.println("Reading input file started.");
      long start = System.currentTimeMillis();
      ProjectReader reader = ProjectReaderUtility.getProjectReader(inputFile);
      ProjectFile projectFile = reader.read(inputFile);
      long elapsed = System.currentTimeMillis() - start;
      System.out.println("Reading input file completed in " + elapsed + "ms.");

      System.out.println("Writing output file started.");
      start = System.currentTimeMillis();
      ProjectWriter writer = ProjectWriterUtility.getProjectWriter(outputFile);
      writer.write(projectFile, outputFile);
      elapsed = System.currentTimeMillis() - start;
      System.out.println("Writing output completed in " + elapsed + "ms.");
   }
}
