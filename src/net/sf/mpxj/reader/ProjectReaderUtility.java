/*
 * file:       ProjectReaderUtility.java
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

package net.sf.mpxj.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.asta.AstaFileReader;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.planner.PlannerReader;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

/**
 * This class contains utility methods for working with ProjectReaders.
 * Note that you should probably be using the UniversalProjectReader instead
 * as it can distinguish the correct file type based on content.
 */
public final class ProjectReaderUtility
{
   /**
    * Constructor.
    */
   private ProjectReaderUtility()
   {
      // Private constructor to prevent instantiation.
   }

   /**
    * Retrieves a ProjectReader instance which can read a file of the
    * type specified by the supplied file name.
    *
    * @param name file name
    * @return ProjectReader instance
    */
   public static ProjectReader getProjectReader(String name) throws MPXJException
   {
      int index = name.lastIndexOf('.');
      if (index == -1)
      {
         throw new IllegalArgumentException("Filename has no extension: " + name);
      }

      String extension = name.substring(index + 1).toUpperCase();

      Class<? extends ProjectReader> fileClass = READER_MAP.get(extension);
      if (fileClass == null)
      {
         throw new IllegalArgumentException("Cannot read files of type: " + extension);
      }

      try
      {
         ProjectReader file = fileClass.newInstance();
         return (file);
      }

      catch (Exception ex)
      {
         throw new MPXJException("Failed to load project reader", ex);
      }
   }

   /**
    * Retrieves a set containing the file extensions supported by the
    * getProjectReader method.
    *
    * @return set of file extensions
    */
   public static Set<String> getSupportedFileExtensions()
   {
      return (READER_MAP.keySet());
   }

   private static final Map<String, Class<? extends ProjectReader>> READER_MAP = new HashMap<String, Class<? extends ProjectReader>>();
   static
   {
      READER_MAP.put("MPP", MPPReader.class);
      READER_MAP.put("MPT", MPPReader.class);
      READER_MAP.put("MPX", MPXReader.class);
      READER_MAP.put("XML", MSPDIReader.class);
      READER_MAP.put("MPD", MPDDatabaseReader.class);
      READER_MAP.put("PLANNER", PlannerReader.class);
      READER_MAP.put("XER", PrimaveraXERFileReader.class);
      READER_MAP.put("PMXML", PrimaveraPMFileReader.class);
      READER_MAP.put("PP", AstaFileReader.class);
   }
}
