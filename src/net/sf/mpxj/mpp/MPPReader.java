/*
 * file:       MPPReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
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
import java.util.List;
import java.util.Map;

import net.sf.mpxj.DateRange;
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
   public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         ProjectFile projectFile = new ProjectFile();

         projectFile.setAutoTaskID(false);
         projectFile.setAutoTaskUniqueID(false);
         projectFile.setAutoResourceID(false);
         projectFile.setAutoResourceUniqueID(false);
         projectFile.setAutoOutlineLevel(false);
         projectFile.setAutoOutlineNumber(false);
         projectFile.setAutoWBS(false);
         projectFile.setAutoCalendarUniqueID(false);

         //
         // Open the file system and retrieve the root directory
         //
         POIFSFileSystem fs = new POIFSFileSystem(is);
         DirectoryEntry root = fs.getRoot();

         //
         // Retrieve the CompObj data, validate the file format and process
         //
         CompObj compObj = new CompObj(new DocumentInputStream((DocumentEntry) root.getEntry("\1CompObj")));
         String format = compObj.getFileFormat();
         Class<? extends MPPVariantReader> readerClass = FILE_CLASS_MAP.get(format);
         if (readerClass == null)
         {
            throw new MPXJException(MPXJException.INVALID_FILE + ": " + format);
         }
         MPPVariantReader reader = readerClass.newInstance();
         reader.process(this, projectFile, root);

         //
         // Update the internal structure. We'll take this opportunity to
         // generate outline numbers for the tasks as they don't appear to
         // be present in the MPP file.
         //
         projectFile.setAutoOutlineNumber(true);
         projectFile.updateStructure();
         projectFile.setAutoOutlineNumber(false);

         //
         // Perform post-processing to set the summary flag and clean
         // up any instances where a task has an empty splits list.
         //
         for (Task task : projectFile.getAllTasks())
         {
            task.setSummary(task.getChildTasks().size() != 0);
            List<DateRange> splits = task.getSplits();
            if (splits != null && splits.isEmpty())
            {
               task.setSplits(null);
            }
         }

         //
         // Ensure that the unique ID counters are correct
         //
         projectFile.updateUniqueCounters();

         return (projectFile);
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      catch (IllegalAccessException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      catch (InstantiationException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
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
   public void setPreserveNoteFormatting(boolean preserveNoteFormatting)
   {
      m_preserveNoteFormatting = preserveNoteFormatting;
   }

   /**
    * If this flag is true, raw timephased data will be retrieved
    * from MS Project: no normalisation will take place.
    * 
    * @return boolean flag
    */
   public boolean getUseRawTimephasedData()
   {
      return m_useRawTimephasedData;
   }

   /**
    * If this flag is true, raw timephased data will be retrieved
    * from MS Project: no normalisation will take place. 
    * 
    * @param useRawTimephasedData boolean flag
    */
   public void setUseRawTimephasedData(boolean useRawTimephasedData)
   {
      m_useRawTimephasedData = useRawTimephasedData;
   }

   /**
    * Retrieves a flag which indicates whether presentation data will
    * be read from the MPP file. Not reading this data saves time and memory. 
    * 
    * @return presentation data flag
    */
   public boolean getReadPresentationData()
   {
      return m_readPresentationData;
   }

   /**
    * Flag to allow time and memory to be saved by not reading
    * presentation data from the MPP file. 
    * 
    * @param readPresentationData set to false to prevent presentation data being read
    */
   public void setReadPresentationData(boolean readPresentationData)
   {
      m_readPresentationData = readPresentationData;
   }

   /**
    * Set the read password for this Project file. This is needed in order to
    * be allowed to read a read-protected Project file.
    * 
    * Note: Set this each time before calling the read method.
    * 
    * @param password password text
    */
   public void setReadPassword(String password)
   {
      m_readPassword = password;
   }

   /**
    * Internal only. Get the read password for this Project file. This is 
    * needed in order to be allowed to read a read-protected Project file.
    * 
    * @return password password text
    */
   public String getReadPassword()
   {
      return m_readPassword;
   }

   /**
    * Set the write password for this Project file. Currently not used.
    * 
    * Note: Set this each time before calling the read method.
    *
    * @param password password text
    */
   public void setWritePassword(String password)
   {
      m_writePassword = password;
   }

   /**
    * Internal only. Get the write password for this Project file. 
    * Currently not used.
    * 
    * @return password
    */
   public String getWritePassword()
   {
      return m_writePassword;
   }

   /**
    * Flag used to indicate whether RTF formatting in notes should
    * be preserved. The default value for this flag is false.
    */
   private boolean m_preserveNoteFormatting;

   /**
    * Setting this flag to true allows raw timephased data to be retrieved. 
    */
   private boolean m_useRawTimephasedData;

   /**
    * Flag to allow time and memory to be saved by not reading
    * presentation data from the MPP file.
    */
   private boolean m_readPresentationData = true;

   private String m_readPassword;
   private String m_writePassword;

   /**
    * Populate a map of file types and file processing classes.
    */
   private static final Map<String, Class<? extends MPPVariantReader>> FILE_CLASS_MAP = new HashMap<String, Class<? extends MPPVariantReader>>();
   static
   {
      FILE_CLASS_MAP.put("MSProject.MPP9", MPP9Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPT9", MPP9Reader.class);
      FILE_CLASS_MAP.put("MSProject.GLOBAL9", MPP9Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPP8", MPP8Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPT8", MPP8Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPP12", MPP12Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPT12", MPP12Reader.class);
      FILE_CLASS_MAP.put("MSProject.GLOBAL12", MPP12Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPP14", MPP14Reader.class);
      FILE_CLASS_MAP.put("MSProject.MPT14", MPP14Reader.class);
      FILE_CLASS_MAP.put("MSProject.GLOBAL14", MPP14Reader.class);
   }
}
