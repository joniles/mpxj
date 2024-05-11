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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;
import net.sf.mpxj.primavera.PrimaveraXERFileWriter;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.sdef.SDEFWriter;
import net.sf.mpxj.writer.FileFormat;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.writer.UniversalProjectWriter;

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
    */
   public void process(String inputFile, String outputFile) throws Exception
   {
      System.out.println("Reading input file started.");
      long start = System.currentTimeMillis();
      ProjectFile projectFile = new UniversalProjectReader().read(inputFile);
      long elapsed = System.currentTimeMillis() - start;
      System.out.println("Reading input file completed in " + elapsed + "ms.");

      if (projectFile == null)
      {
         throw new IllegalArgumentException("Unsupported file type");
      }

      int index = outputFile.lastIndexOf('.');
      if (index == -1)
      {
         throw new IllegalArgumentException("Filename has no extension: " + outputFile);
      }

      String extension = outputFile.substring(index + 1).toUpperCase();
      FileFormat outputFormat = WRITER_MAP.get(extension);
      if (extension == null)
      {
         throw new IllegalArgumentException("Cannot write files of type: " + extension);
      }

      System.out.println("Writing output file started.");
      start = System.currentTimeMillis();
      new UniversalProjectWriter().withFormat(outputFormat).write(projectFile, outputFile);
      elapsed = System.currentTimeMillis() - start;
      System.out.println("Writing output completed in " + elapsed + "ms.");
   }

   private static final Map<String, FileFormat> WRITER_MAP = new HashMap<>();
   static
   {
      WRITER_MAP.put("MPX", FileFormat.MPX);
      WRITER_MAP.put("XML", FileFormat.MSPDI);
      WRITER_MAP.put("PMXML", FileFormat.PMXML);
      WRITER_MAP.put("PLANNER", FileFormat.PLANNER);
      WRITER_MAP.put("JSON", FileFormat.JSON);
      WRITER_MAP.put("SDEF", FileFormat.SDEF);
      WRITER_MAP.put("XER", FileFormat.XER);
   }
}
