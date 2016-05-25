/*
 * file:       ProjectWriterUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       28/01/2008
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

package net.sf.mpxj.writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;
import net.sf.mpxj.sdef.SDEFWriter;

/**
 * This class contains utility methods for working with ProjectWriters.
 */
public final class ProjectWriterUtility
{
   /**
    * Constructor.
    */
   private ProjectWriterUtility()
   {
      // Private constructor to prevent instantiation.
   }

   /**
    * Retrieves a ProjectWriter instance which can write a file of the
    * type specified by the supplied file name.
    *
    * @param name file name
    * @return ProjectWriter instance
    */
   public static ProjectWriter getProjectWriter(String name) throws InstantiationException, IllegalAccessException
   {
      int index = name.lastIndexOf('.');
      if (index == -1)
      {
         throw new IllegalArgumentException("Filename has no extension: " + name);
      }

      String extension = name.substring(index + 1).toUpperCase();

      Class<? extends ProjectWriter> fileClass = WRITER_MAP.get(extension);
      if (fileClass == null)
      {
         throw new IllegalArgumentException("Cannot write files of type: " + name);
      }

      ProjectWriter file = fileClass.newInstance();

      return (file);
   }

   /**
    * Retrieves a set containing the file extensions supported by the
    * getProjectWriter method.
    *
    * @return set of file extensions
    */
   public static Set<String> getSupportedFileExtensions()
   {
      return (WRITER_MAP.keySet());
   }

   private static final Map<String, Class<? extends ProjectWriter>> WRITER_MAP = new HashMap<String, Class<? extends ProjectWriter>>();
   static
   {
      WRITER_MAP.put("MPX", MPXWriter.class);
      WRITER_MAP.put("XML", MSPDIWriter.class);
      WRITER_MAP.put("PMXML", PrimaveraPMFileWriter.class);
      WRITER_MAP.put("PLANNER", PlannerWriter.class);
      WRITER_MAP.put("JSON", JsonWriter.class);
      WRITER_MAP.put("SDEF", SDEFWriter.class);
   }
}
