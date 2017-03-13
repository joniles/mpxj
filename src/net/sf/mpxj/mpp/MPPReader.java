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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;
import net.sf.mpxj.listener.ProjectListener;
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
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {

      try
      {

         //
         // Open the file system
         //
         POIFSFileSystem fs = new POIFSFileSystem(is);

         return read(fs);

      }
      catch (IOException ex)
      {

         throw new MPXJException(MPXJException.READ_ERROR, ex);

      }
   }

   /**
    * Alternative entry point allowing an MPP file to be read from
    * a user-supplied POI file stream.
    *
    * @param fs POI file stream
    * @return ProjectFile instance
    * @throws MPXJException
    */
   public ProjectFile read(POIFSFileSystem fs) throws MPXJException
   {

      try
      {
         ProjectFile projectFile = new ProjectFile();
         ProjectConfig config = projectFile.getProjectConfig();

         config.setAutoTaskID(false);
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceID(false);
         config.setAutoResourceUniqueID(false);
         config.setAutoOutlineLevel(false);
         config.setAutoOutlineNumber(false);
         config.setAutoWBS(false);
         config.setAutoCalendarUniqueID(false);
         config.setAutoAssignmentUniqueID(false);

         projectFile.getEventManager().addProjectListeners(m_projectListeners);

         //
         // Open the file system and retrieve the root directory
         //
         DirectoryEntry root = fs.getRoot();

         //
         // Retrieve the CompObj data, validate the file format and process
         //
         CompObj compObj = new CompObj(new DocumentInputStream((DocumentEntry) root.getEntry("\1CompObj")));
         projectFile.getProjectProperties().setFullApplicationName(compObj.getApplicationName());
         projectFile.getProjectProperties().setApplicationVersion(compObj.getApplicationVersion());
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
         config.setAutoOutlineNumber(true);
         projectFile.updateStructure();
         config.setAutoOutlineNumber(false);

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
            validationRelations(task);
         }

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

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
    * This method validates all relationships for a task, removing
    * any which have been incorrectly read from the MPP file and
    * point to a parent task.
    *
    * @param task task under test
    */
   private void validationRelations(Task task)
   {
      List<Relation> predecessors = task.getPredecessors();
      if (!predecessors.isEmpty())
      {
         ArrayList<Relation> invalid = new ArrayList<Relation>();
         for (Relation relation : predecessors)
         {
            Task sourceTask = relation.getSourceTask();
            Task targetTask = relation.getTargetTask();

            String sourceOutlineNumber = sourceTask.getOutlineNumber();
            String targetOutlineNumber = targetTask.getOutlineNumber();

            if (sourceOutlineNumber != null && targetOutlineNumber != null && sourceOutlineNumber.startsWith(targetOutlineNumber + '.'))
            {
               invalid.add(relation);
            }
         }

         for (Relation relation : invalid)
         {
            relation.getSourceTask().removePredecessor(relation.getTargetTask(), relation.getType(), relation.getLag());
         }
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
    * Flag to determine if the reader should only read the project properties.
    * This allows for rapid access to the document properties, without the
    * cost of reading the entire contents of the project file.
    *
    * @return true if the reader should only read the project properties
    */
   public boolean getReadPropertiesOnly()
   {
      return m_readPropertiesOnly;
   }

   /**
    * Flag to determine if the reader should only read the project properties.
    * This allows for rapid access to the document properties, without the
    * cost of reading the entire contents of the project file.
    *
    * @param readPropertiesOnly true if the reader should only read the project properties
    */
   public void setReadPropertiesOnly(boolean readPropertiesOnly)
   {
      m_readPropertiesOnly = readPropertiesOnly;
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
   private boolean m_readPropertiesOnly;

   private String m_readPassword;
   private String m_writePassword;
   private List<ProjectListener> m_projectListeners;

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
