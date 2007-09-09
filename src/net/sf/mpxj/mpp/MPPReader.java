/*
 * file:       MPPReader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       2005-12-21
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

package net.sf.mpxj.mpp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.reader.AbstractProjectReader;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


/**
 * This class creates a new ProjectFile instance by reading an MPP file.
 */
public final class MPPReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   public ProjectFile read (InputStream is)
      throws MPXJException
   {
      try
      {
         ProjectFile projectFile = new ProjectFile();

         //
         // Open the file system and retrieve the root directory
         //
         POIFSFileSystem fs = new POIFSFileSystem (is);
         DirectoryEntry root = fs.getRoot ();

         //
         // Retrieve the CompObj data, validate the file format and process
         //
         CompObj compObj = new CompObj (new DocumentInputStream ((DocumentEntry)root.getEntry("\1CompObj")));
         String format = compObj.getFileFormat();
         Class<? extends MPPVariantReader> readerClass = FILE_CLASS_MAP.get(format);
         if (readerClass == null)
         {
            throw new MPXJException (MPXJException.INVALID_FILE + ": " + format);
         }
         MPPVariantReader reader = readerClass.newInstance();
         reader.process (this, projectFile, root);

         //
         // Update the internal structure. We'll take this opportunity to
         // generate outline numbers for the tasks as they don't appear to
         // be present in the MPP file.
         //
         projectFile.setAutoOutlineNumber(true);
         projectFile.updateStructure ();
         projectFile.setAutoOutlineNumber(false);

         //
         // Perform post-processing to set the summary flag
         //
         List<Task> tasks = projectFile.getAllTasks();
         Iterator<Task> iter = tasks.iterator();
         Task task;

         while (iter.hasNext() == true)
         {
            task = iter.next();
            task.setSummary(task.getChildTasks().size() != 0);
         }

         //
         // Ensure that the unique ID counters are correct
         //
         projectFile.updateUniqueCounters();

         return (projectFile);
      }

      catch (IOException ex)
      {
         throw new MPXJException (MPXJException.READ_ERROR, ex);
      }

      catch (IllegalAccessException ex)
      {
         throw new MPXJException (MPXJException.READ_ERROR, ex);
      }

      catch (InstantiationException ex)
      {
         throw new MPXJException (MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * This method retrieves the state of the preserve note formatting flag.
    *
    * @return boolean flag
    */
   public boolean getPreserveNoteFormatting()
   {
      return (m_preserveNoteFormatting);
   }

   /**
    * This method sets a flag to indicate whether the RTF formatting associated
    * with notes should be preserved or removed. By default the formatting
    * is removed.
    *
    * @param preserveNoteFormatting boolean flag
    */
   public void setPreserveNoteFormatting (boolean preserveNoteFormatting)
   {
      m_preserveNoteFormatting = preserveNoteFormatting;
   }


   /**
    * Flag used to indicate whether RTF formatting in notes should
    * be preserved. The default value for this flag is false.
    */
   private boolean m_preserveNoteFormatting;


   /**
    * Populate a map of file types and file processing classes.
    */
   private static final Map<String, Class<? extends MPPVariantReader>> FILE_CLASS_MAP = new HashMap<String, Class<? extends MPPVariantReader>> ();
   static
   {
      FILE_CLASS_MAP.put("MSProject.MPP9", MPP9Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPT9", MPP9Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPP8", MPP8Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPT8", MPP8Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPP12", MPP12Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPT12", MPP12Reader.class);
   }
}
