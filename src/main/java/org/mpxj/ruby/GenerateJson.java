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

package org.mpxj.ruby;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.TimeUnit;
import org.mpxj.json.JsonWriter;
import org.mpxj.reader.ProjectReader;
import org.mpxj.reader.UniversalProjectReader;

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
         if (args.length != 3)
         {
            System.out.println("Usage: GenerateJson <input file name> <output file name> <time units>");
         }
         else
         {
            GenerateJson convert = new GenerateJson();
            convert.process(args[0], args[1], args[2]);
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
    * @param timeUnits time units for durations
    */
   public void process(String inputFile, String outputFile, String timeUnits) throws Exception
   {
      System.out.println("Reading input file started.");
      long start = System.currentTimeMillis();
      ProjectFile projectFile = readFile(inputFile);
      long elapsed = System.currentTimeMillis() - start;
      System.out.println("Reading input file completed in " + elapsed + "ms.");

      System.out.println("Writing output file started.");
      start = System.currentTimeMillis();
      JsonWriter writer = new JsonWriter();
      writer.setTimeUnits(TIME_UNIT_MAP.get(timeUnits.toUpperCase()));
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

   private static final Map<String, TimeUnit> TIME_UNIT_MAP = Arrays.stream(TimeUnit.values()).collect(Collectors.toMap(Enum::name, t -> t));
}
