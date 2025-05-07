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

package org.mpxj.mpp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mpxj.CalendarType;
import org.mpxj.ProjectCalendar;
import org.mpxj.Resource;
import org.mpxj.TaskField;
import org.mpxj.UnitOfMeasure;
import org.mpxj.UnitOfMeasureContainer;
import org.mpxj.common.AutoCloseableHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.MPXJException;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.Task;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading an MPP file.
 */
public final class MPPReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         return read(new POIFSFileSystem(is));
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      POIFSFileSystem fs = null;

      try
      {
         // Note we provide this version of the read method rather than using
         // the AbstractProjectStreamReader version as we can work with the File
         // instance directly for reduced memory consumption and the ability
         // to open larger MPP files.
         fs = new POIFSFileSystem(file);
         return read(fs);
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(fs);
      }
   }

   /**
    * This method allows us to peek into the OLE compound document to extract the file format.
    * This allows the UniversalProjectReader to determine if this is an MPP file, or if
    * it is another type of OLE compound document.
    *
    * @param fs POIFSFileSystem instance
    * @return file format name
    */
   public static String getFileFormat(POIFSFileSystem fs) throws IOException
   {
      String fileFormat = "";
      DirectoryEntry root = fs.getRoot();
      if (root.getEntryNames().contains("\1CompObj"))
      {
         CompObj compObj = new CompObj(new DocumentInputStream((DocumentEntry) root.getEntry("\1CompObj")));
         fileFormat = compObj.getFileFormat();
      }
      return fileFormat;
   }

   /**
    * Alternative entry point allowing an MPP file to be read from
    * a user-supplied POI file stream.
    *
    * @param fs POI file stream
    * @return ProjectFile instance
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
         config.setAutoRelationUniqueID(false);

         addListenersToProject(projectFile);

         //
         // Open the file system and retrieve the root directory
         //
         DirectoryEntry root = fs.getRoot();

         //
         // Retrieve the CompObj data, validate the file format and process
         //
         CompObj compObj = new CompObj(new DocumentInputStream((DocumentEntry) root.getEntry("\1CompObj")));
         ProjectProperties projectProperties = projectFile.getProjectProperties();
         projectProperties.setFullApplicationName(compObj.getApplicationName());
         projectProperties.setApplicationVersion(compObj.getApplicationVersion());
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
         for (Task task : projectFile.getTasks())
         {
            task.setSummary(task.hasChildTasks() || task.getExternalProject());
            List<LocalDateTimeRange> splits = task.getSplits();
            if (splits != null && splits.isEmpty())
            {
               task.setSplits(null);
            }
            validationRelations(task);
            copyEstimatedBaselineFields(task);
         }

         //
         // Prune unused resource calendars
         //
         Map<Integer, List<Resource>> resourceCalendarMap = projectFile.getResources().stream().filter(r -> r.getCalendarUniqueID() != null).collect(Collectors.groupingBy(Resource::getCalendarUniqueID));
         projectFile.getCalendars().removeIf(c -> c.isDerived() && !resourceCalendarMap.containsKey(c.getUniqueID()));

         //
         // Resource post-processing
         //
         UnitOfMeasureContainer unitsOfMeasure = projectFile.getUnitsOfMeasure();
         for (Resource resource : projectFile.getResources())
         {
            ProjectCalendar calendar = resource.getCalendar();
            if (calendar != null)
            {
               // Configure the calendar type
               if (calendar.isDerived())
               {
                  calendar.setType(CalendarType.RESOURCE);
                  calendar.setPersonal(resourceCalendarMap.computeIfAbsent(calendar.getUniqueID(), k -> Collections.emptyList()).size() == 1);
               }

               // Resource calendars without names inherit the resource name
               if (calendar.getName() == null || calendar.getName().isEmpty())
               {
                  String name = resource.getName();
                  if (name == null || name.isEmpty())
                  {
                     name = "Unnamed Resource";
                  }
                  calendar.setName(name);
               }
            }

            UnitOfMeasure uom = unitsOfMeasure.getOrCreateByAbbreviation(resource.getMaterialLabel());
            if (uom != null)
            {
               resource.setUnitOfMeasure(uom);
            }
         }

         //
         // Add some analytics
         //
         String projectFilePath = projectFile.getProjectProperties().getProjectFilePath();
         if (projectFilePath != null && projectFilePath.startsWith("<>\\"))
         {
            projectProperties.setFileApplication("Microsoft Project Server");
         }
         else
         {
            projectProperties.setFileApplication("Microsoft");
         }
         projectProperties.setFileType("MPP");

         projectFile.readComplete();

         return (projectFile);
      }

      catch (IOException | InstantiationException | IllegalAccessException ex)
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
         ArrayList<Relation> invalid = new ArrayList<>();
         for (Relation relation : predecessors)
         {
            Task sourceTask = relation.getSuccessorTask();
            Task targetTask = relation.getPredecessorTask();

            String sourceOutlineNumber = sourceTask.getOutlineNumber();
            String targetOutlineNumber = targetTask.getOutlineNumber();

            if (sourceOutlineNumber != null && targetOutlineNumber != null && sourceOutlineNumber.startsWith(targetOutlineNumber + '.'))
            {
               invalid.add(relation);
            }
         }

         for (Relation relation : invalid)
         {
            relation.getSuccessorTask().removePredecessor(relation.getPredecessorTask(), relation.getType(), relation.getLag());
         }
      }
   }

   /**
    * If a baseline field is not populated, but the estimated version of that field is populated
    * then we fall back on using the estimated field.
    *
    * @param task task to update
    */
   private void copyEstimatedBaselineFields(Task task)
   {
      for (Map.Entry<TaskField, TaskField> entry : TASK_ESTIMATED_BASELINE_FIELDS.entrySet())
      {
         Object value = task.getCachedValue(entry.getKey());
         if (value == null)
         {
            value = task.getCachedValue(entry.getValue());
            if (value != null)
            {
               task.set(entry.getKey(), value);
            }
         }
      }
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
    * @return password text
    */
   public String getReadPassword()
   {
      return m_readPassword;
   }

   /**
    * Where supported, set to false to ignore password protection.
    *
    * @param respectPasswordProtection true if password protection is respected
    */
   public void setRespectPasswordProtection(boolean respectPasswordProtection)
   {
      m_respectPasswordProtection = respectPasswordProtection;
   }

   /**
    * Retrieve a flag indicating if password protection is respected.
    *
    * @return true if password protection is respected
    */
   public boolean getRespectPasswordProtection()
   {
      return m_respectPasswordProtection;
   }

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

   /**
    * Where supported, set to false to ignore password protection.
    */
   private boolean m_respectPasswordProtection = true;

   private String m_readPassword;

   /**
    * Populate a map of file types and file processing classes.
    */
   private static final Map<String, Class<? extends MPPVariantReader>> FILE_CLASS_MAP = new HashMap<>();
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

   private static final Map<TaskField, TaskField> TASK_ESTIMATED_BASELINE_FIELDS = new HashMap<>();
   static
   {
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE_DURATION, TaskField.BASELINE_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE_START, TaskField.BASELINE_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE_FINISH, TaskField.BASELINE_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE1_DURATION, TaskField.BASELINE1_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE1_START, TaskField.BASELINE1_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE1_FINISH, TaskField.BASELINE1_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE2_DURATION, TaskField.BASELINE2_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE2_START, TaskField.BASELINE2_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE2_FINISH, TaskField.BASELINE2_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE3_DURATION, TaskField.BASELINE3_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE3_START, TaskField.BASELINE3_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE3_FINISH, TaskField.BASELINE3_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE4_DURATION, TaskField.BASELINE4_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE4_START, TaskField.BASELINE4_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE4_FINISH, TaskField.BASELINE4_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE5_DURATION, TaskField.BASELINE5_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE5_START, TaskField.BASELINE5_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE5_FINISH, TaskField.BASELINE5_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE6_DURATION, TaskField.BASELINE6_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE6_START, TaskField.BASELINE6_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE6_FINISH, TaskField.BASELINE6_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE7_DURATION, TaskField.BASELINE7_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE7_START, TaskField.BASELINE7_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE7_FINISH, TaskField.BASELINE7_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE8_DURATION, TaskField.BASELINE8_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE8_START, TaskField.BASELINE8_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE8_FINISH, TaskField.BASELINE8_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE9_DURATION, TaskField.BASELINE9_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE9_START, TaskField.BASELINE9_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE9_FINISH, TaskField.BASELINE9_ESTIMATED_FINISH);

      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE10_DURATION, TaskField.BASELINE10_ESTIMATED_DURATION);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE10_START, TaskField.BASELINE10_ESTIMATED_START);
      TASK_ESTIMATED_BASELINE_FIELDS.put(TaskField.BASELINE10_FINISH, TaskField.BASELINE10_ESTIMATED_FINISH);
   }
}
