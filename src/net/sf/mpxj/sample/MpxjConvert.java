/*
 * file:       MpxjConvert.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2005
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

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerReader;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.writer.ProjectWriter;


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
   public static void main (String[] args)
   {
      try
      {
         if (args.length != 2)
         {
            System.out.println ("Usage: MpxjConvert <input file name> <output file name>");
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
   public void process (String inputFile, String outputFile)
      throws Exception
   {
      System.out.println ("Reading input file started.");
      long start = System.currentTimeMillis();
      ProjectReader reader = getReaderObject(inputFile);
      ProjectFile projectFile = reader.read(inputFile);
      long elapsed = System.currentTimeMillis() - start;
      System.out.println ("Reading input file completed in " + elapsed + "ms.");

      System.out.println ("Writing output file started.");
      start = System.currentTimeMillis();
      ProjectWriter writer = getWriterObject(outputFile);
      writer.write(projectFile, outputFile);
      elapsed = System.currentTimeMillis() - start;
      System.out.println ("Writing output completed in " + elapsed + "ms.");
   }

   /**
    * Create a ProjectReader instance of the appropriate type.
    *
    * @param name file name
    * @return ProjectReader instance
    * @throws Exception
    */
   private ProjectReader getReaderObject (String name)
      throws Exception
   {
      int index = name.lastIndexOf('.');
      if (index == -1)
      {
         throw new Exception ("Filename has no extension: " + name);
      }

      String extension = name.substring(index+1).toUpperCase();

      Class fileClass = (Class)READER_MAP.get(extension);
      if (fileClass == null)
      {
         throw new Exception ("Cannot read files of type: " + name);
      }

      ProjectReader file = (ProjectReader)fileClass.newInstance();

      return (file);
   }

   /**
    * Create a ProjectWriter instance of the appropriate type.
    *
    * @param name file name
    * @return ProjectWriter instance
    * @throws Exception
    */
   private ProjectWriter getWriterObject (String name)
      throws Exception
   {
      int index = name.lastIndexOf('.');
      if (index == -1)
      {
         throw new Exception ("Filename has no extension: " + name);
      }

      String extension = name.substring(index+1).toUpperCase();

      Class fileClass = (Class)WRITER_MAP.get(extension);
      if (fileClass == null)
      {
         throw new Exception ("Cannot write files of type: " + name);
      }

      ProjectWriter file = (ProjectWriter)fileClass.newInstance();

      return (file);
   }

   private static final Map READER_MAP = new HashMap ();
   static
   {
      READER_MAP.put("MPP", MPPReader.class);
      READER_MAP.put("MPT", MPPReader.class);
      READER_MAP.put("MPX", MPXReader.class);
      READER_MAP.put("XML", MSPDIReader.class);
      READER_MAP.put("PLANNER", PlannerReader.class);
   }

   private static final Map WRITER_MAP = new HashMap ();
   static
   {
      WRITER_MAP.put("MPX", MPXWriter.class);
      WRITER_MAP.put("XML", MSPDIWriter.class);
   }
}

