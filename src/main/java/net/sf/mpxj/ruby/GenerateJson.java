/*
 * file:       GenerateJson.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       28/11/2021
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

package net.sf.mpxj.ruby;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

/**
 * Command line interface used by the Ruby version of MPXJ
 * to convert a project to JSON.
 */
public final class GenerateJson
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
            System.out.println("Usage: GenerateJson <input file name> <output file name>");
         }
         else
         {
            GenerateJson convert = new GenerateJson();
            convert.process(args[0], args[1]);
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
      ProjectFile projectFile = readFile(inputFile);
      long elapsed = System.currentTimeMillis() - start;
      System.out.println("Reading input file completed in " + elapsed + "ms.");

      System.out.println("Writing output file started.");
      start = System.currentTimeMillis();
      JsonWriter writer = new JsonWriter();
      writer.write(projectFile, outputFile);
      elapsed = System.currentTimeMillis() - start;
      System.out.println("Writing output completed in " + elapsed + "ms.");
   }

   /**
    * Use the universal project reader to open the file.
    * Throw an exception if we can't determine the file type.
    *
    * @param inputFile file name
    * @return ProjectFile instance
    */
   private ProjectFile readFile(String inputFile) throws MPXJException
   {
      ProjectReader reader = new UniversalProjectReader();
      ProjectFile projectFile = reader.read(inputFile);
      if (projectFile == null)
      {
         throw new IllegalArgumentException("Unsupported file type");
      }
      return projectFile;
   }
}
