/*
 * file:       MPP9Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2006
 * date:       22/05/2003
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mpxj.FieldTypeClass;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import org.mpxj.CustomFieldContainer;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Resource;
import org.mpxj.ResourceField;
import org.mpxj.ResourceType;
import org.mpxj.Table;
import org.mpxj.TableContainer;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.View;

/**
 * This class is used to represent a Microsoft Project MPP9 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP9Reader implements MPPVariantReader
{
   /**
    * This method is used to process an MPP9 file. This is the file format
    * used by Project 2000, 2002, and 2003.
    *
    * @param reader parent file reader
    * @param file parent MPP file
    * @param root Root of the POI file system.
    */
   @Override public void process(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException
   {
      try
      {
         populateMemberData(reader, file, root);
         processProjectProperties();

         if (!reader.getReadPropertiesOnly())
         {
            processSubProjectData();
            processGraphicalIndicators();
            processCustomValueLists();
            processCalendarData();
            processResourceData();
            processTaskData();
            processConstraintData();
            processAssignmentData();
            postProcessTasks();
            processDataLinks();

            if (reader.getReadPresentationData())
            {
               processViewPropertyData();
               processTableData();
               processViewData();
               processFilterData();
               processGroupData();
               processSavedViewState();
            }
         }
      }

      finally
      {
         clearMemberData();
      }
   }

   /**
    * Populate member data used by the rest of the reader.
    *
    * @param reader parent file reader
    * @param file parent MPP file
    * @param root Root of the POI file system.
    */
   private void populateMemberData(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException
   {
      m_reader = reader;
      m_file = file;
      m_eventManager = file.getEventManager();
      m_root = root;

      //
      // Retrieve the high level document properties (never encoded)
      //
      Props props = new Props9(new DocumentInputStream(((DocumentEntry) root.getEntry("Props9"))));
      //System.out.println(props);

      file.getProjectProperties().setProjectFilePath(props.getUnicodeString(Props.PROJECT_FILE_PATH));
      m_inputStreamFactory = new DocumentInputStreamFactory(props);

      //
      // Test for password protection. In the single byte retrieved here:
      //
      // 0x00 = no password
      // 0x01 = protection password has been supplied
      // 0x02 = write reservation password has been supplied
      // 0x03 = both passwords have been supplied
      //
      byte passwordProtectionFlag = props.getByte(Props.PASSWORD_FLAG);
      boolean passwordRequiredToRead = (passwordProtectionFlag & 0x1) != 0;
      //boolean passwordRequiredToWrite = (passwordProtectionFlag & 0x2) != 0;

      if (passwordRequiredToRead && reader.getRespectPasswordProtection())
      {
         // File is password protected for reading, let's read the password
         // and see if the correct read password was given to us.
         String readPassword = MPPUtility.decodePassword(props.getByteArray(Props.PROTECTION_PASSWORD_HASH), m_inputStreamFactory.getEncryptionCode());
         // It looks like it is possible for a project file to have the password protection flag on without a password. In
         // this case MS Project treats the file as NOT protected. We need to do the same. It is worth noting that MS Project does
         // correct the problem if the file is re-saved (at least it did for me).
         if (readPassword != null && !readPassword.isEmpty())
         {
            // See if the correct read password was given
            if (reader.getReadPassword() == null || !reader.getReadPassword().matches(readPassword))
            {
               // Passwords don't match
               throw new MPXJException(MPXJException.PASSWORD_PROTECTED_ENTER_PASSWORD);
            }
         }
         // Passwords matched so let's allow the reading to continue.
      }

      m_resourceMap = new HashMap<>();
      m_projectDir = (DirectoryEntry) root.getEntry("   19");
      m_viewDir = (DirectoryEntry) root.getEntry("   29");
      DirectoryEntry outlineCodeDir = (DirectoryEntry) m_projectDir.getEntry("TBkndOutlCode");
      VarMeta outlineCodeVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("VarMeta"))));
      m_outlineCodeVarData = new Var2Data(m_file, outlineCodeVarMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Var2Data"))));
      m_projectProps = new Props9(m_inputStreamFactory.getInstance(m_projectDir, "Props"));
      //MPPUtility.fileDump("c:\\temp\\props.txt", m_projectProps.toString().getBytes());

      m_fontBases = new HashMap<>();
      m_taskSubProjects = new HashMap<>();
      m_externalTasks = new HashSet<>();
      m_taskOrder = new TreeMap<>();
      m_nullTaskOrder = new TreeMap<>();

      m_file.getProjectProperties().setMppFileType(Integer.valueOf(9));
      m_file.getProjectProperties().setAutoFilter(props.getBoolean(Props.AUTO_FILTER));
   }

   /**
    * Clear transient member data.
    */
   private void clearMemberData()
   {
      m_reader = null;
      m_file = null;
      m_eventManager = null;
      m_root = null;
      m_resourceMap = null;
      m_projectDir = null;
      m_viewDir = null;
      m_outlineCodeVarData = null;
      m_fontBases = null;
      m_taskOrder = null;
      m_nullTaskOrder = null;
      m_taskSubProjects = null;
      m_externalTasks = null;
   }

   /**
    * Process the project properties data.
    */
   private void processProjectProperties() throws MPXJException
   {
      ProjectPropertiesReader reader = new ProjectPropertiesReader();
      reader.process(m_file, m_projectProps, m_root);
   }

   /**
    * Process the graphical indicator data.
    */
   private void processGraphicalIndicators()
   {
      GraphicalIndicatorReader graphicalIndicatorReader = new GraphicalIndicatorReader();
      graphicalIndicatorReader.process(m_file, m_projectProps);
   }

   /**
    * Read sub project data from the file, and add it to a hash map
    * indexed by task ID.
    *
    * Project stores all subprojects that have ever been inserted into this project
    * in sequence and that is what used to count unique id offsets for each of the
    * subprojects.
    */
   private void processSubProjectData()
   {
      byte[] subProjData = m_projectProps.getByteArray(Props.SUBPROJECT_DATA);
      if (subProjData == null)
      {
         return;
      }

      //System.out.println (ByteArrayHelper.hexdump(subProjData, true, 16, ""));
      //MPPUtility.fileHexDump("c:\\temp\\dump.txt", subProjData);

      int index = 0;
      int offset = 0;
      int itemHeaderOffset;
      int uniqueIDOffset;
      int filePathOffset;
      int fileNameOffset;

      /*int blockSize = MPPUtility.getInt(subProjData, offset);*/
      offset += 4;

      /*int unknown = MPPUtility.getInt(subProjData, offset);*/
      offset += 4;

      int itemCountOffset = ByteArrayHelper.getInt(subProjData, offset);
      offset += 4;

      while (offset < itemCountOffset)
      {
         index++;
         itemHeaderOffset = ByteArrayHelper.getShort(subProjData, offset);
         offset += 4;

         // 20 byte header: 16 bytes GUID, 4 bytes flags
         //System.out.println(ByteArrayHelper.hexdump(subProjData, itemHeaderOffset+16, 4, false));
         byte subProjectType = subProjData[itemHeaderOffset + 16];

         switch (subProjectType)
         {
            //
            // Subproject that is no longer inserted. This is a placeholder in order to be
            // able to always guarantee unique unique ids.
            //
            case 0x00:
            {
               offset += 8;
               break;
            }

            //
            // task unique ID, 8 bytes, path, file name
            //
            case (byte) 0x99:
            case 0x09:
            case 0x0D:
            {
               uniqueIDOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               // sometimes offset of a task ID?
               offset += 4;

               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, 8 bytes, path, file name
            //
            case (byte) 0x91:
            {
               uniqueIDOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               // Unknown offset
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, path, file name
            //
            case 0x01:
            case 0x03:
            case 0x05:
            case 0x08:
            case 0x0A:
            case 0x11:
            {
               uniqueIDOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, path, unknown, file name
            //
            case (byte) 0x81:
            case 0x41:
            {
               uniqueIDOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               // unknown offset to 2 bytes of data?
               offset += 4;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, path, file name
            //
            case (byte) 0xC0:
            {
               uniqueIDOffset = itemHeaderOffset;

               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               // unknown offset
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            case 0x45:
            {
               uniqueIDOffset = ByteArrayHelper.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               filePathOffset = ByteArrayHelper.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = ByteArrayHelper.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               offset += 4;
               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // path, file name
            //
            case 0x02:
            case 0x04:
            {
               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, -1, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, 4 bytes, path, 4 bytes, file name
            //
            case (byte) 0x8D:
            {
               uniqueIDOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 8;

               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 8;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // Appears when a subproject is collapsed
            //
            case (byte) 0x80:
            {
               offset += 12;
               break;
            }

            // deleted entry?
            case 0x10:
            {
               offset += 8;
               break;
            }

            // new resource pool entry
            case (byte) 0x44:
            {
               filePathOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               offset += 4;

               fileNameOffset = ByteArrayHelper.getShort(subProjData, offset);
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, -1, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // Any other value, assume 12 bytes to handle old/deleted data?
            //
            default:
            {
               offset += 12;
               break;
            }
         }
      }
   }

   /**
    * Method used to read the sub project details from a byte array.
    *
    * @param data byte array
    * @param headerOffset header offset
    * @param uniqueIDOffset offset of unique ID
    * @param filePathOffset offset of file path
    * @param fileNameOffset offset of file name
    * @param subprojectIndex index of the subproject, used to calculate unique id offset
    */
   private void readSubProject(byte[] data, int headerOffset, int uniqueIDOffset, int filePathOffset, int fileNameOffset, int subprojectIndex)
   {
      try
      {
         String sp;

         // We have a 20 byte header.
         // First 16 bytes are (most of the time) the GUID of the target project
         // Remaining 4 bytes are believed to be flags
         // NOTE: actually for MPP9 files this is not the project GUID.
         // MPP9 files don't appear to store the GUID of the subproject.

         // Generate the unique id offset for this subproject
         //int offset = 0x00800000 + ((subprojectIndex - 1) * 0x00400000);

         //
         // First block header
         //
         filePathOffset += 18;

         //
         // String size as a 4 byte int
         //
         filePathOffset += 4;

         //
         // Full DOS path
         //
         String dosFullPath = MPPUtility.getString(data, filePathOffset);
         filePathOffset += (dosFullPath.length() + 1);

         //
         // 24 byte block
         //
         filePathOffset += 24;

         //
         // 4 byte block size
         //
         int size = ByteArrayHelper.getInt(data, filePathOffset);
         filePathOffset += 4;
         if (size == 0)
         {
            sp = dosFullPath;
         }
         else
         {
            //
            // 4 byte unicode string size in bytes
            //
            size = ByteArrayHelper.getInt(data, filePathOffset);
            filePathOffset += 4;

            //
            // 2 byte data
            //
            filePathOffset += 2;

            //
            // Unicode string
            //
            sp = MPPUtility.getUnicodeString(data, filePathOffset, size);
            // filePathOffset += size;
         }

         processUniqueIdValues(sp, data, uniqueIDOffset);
      }

      //
      // Admit defeat at this point - we have probably stumbled
      // upon a data format we don't understand, so we'll fail
      // gracefully here. This will now be reported as a missing
      // sub project error by end users of the library, rather
      // than as an exception being thrown.
      //
      catch (ArrayIndexOutOfBoundsException ex)
      {
         // Do nothing
         m_file.addIgnoredError(ex);
      }
   }

   private void processUniqueIdValues(String sp, byte[] data, int uniqueIDOffset)
   {
      if (uniqueIDOffset == -1)
      {
         return;
      }

      Integer prev = Integer.valueOf(0);
      int value = ByteArrayHelper.getInt(data, uniqueIDOffset);
      while (value != SUBPROJECT_LISTEND)
      {
         switch (value)
         {
            case SUBPROJECT_TASKUNIQUEID0:
            case SUBPROJECT_TASKUNIQUEID1:
            case SUBPROJECT_TASKUNIQUEID2:
            case SUBPROJECT_TASKUNIQUEID3:
            case SUBPROJECT_TASKUNIQUEID4:
            case SUBPROJECT_TASKUNIQUEID5:
            {
               m_taskSubProjects.put(prev, sp);
               prev = Integer.valueOf(0);
               break;
            }

            default:
            {
               if (prev.intValue() != 0)
               {
                  // The previous value was for an external task unique task id
                  m_externalTasks.add(prev);
                  m_taskSubProjects.put(prev, sp);
               }
               prev = Integer.valueOf(value);
               break;
            }
         }

         // Read the next value
         uniqueIDOffset += 4;
         value = ByteArrayHelper.getInt(data, uniqueIDOffset);
      }

      if (prev.intValue() != 0)
      {
         // The previous value was for an external task unique task id
         m_externalTasks.add(prev);
         m_taskSubProjects.put(prev, sp);
      }
   }

   /**
    * This method process the data held in the props file specific to the
    * visual appearance of the project data.
    */
   private void processViewPropertyData() throws IOException
   {
      if (m_viewDir.hasEntry("Props"))
      {
         Props props = new Props9(m_inputStreamFactory.getInstance(m_viewDir, "Props"));
         byte[] data = props.getByteArray(Props.FONT_BASES);
         if (data != null)
         {
            processBaseFonts(data);
         }

         ProjectProperties properties = m_file.getProjectProperties();
         properties.setShowProjectSummaryTask(props.getBoolean(Props.SHOW_PROJECT_SUMMARY_TASK));
      }
   }

   /**
    * Create an index of base font numbers and their associated base
    * font instances.
    * @param data property data
    */
   private void processBaseFonts(byte[] data)
   {
      int offset = 0;

      int blockCount = ByteArrayHelper.getShort(data, 0);
      offset += 2;

      int size;
      String name;

      for (int loop = 0; loop < blockCount; loop++)
      {
         /*unknownAttribute = MPPUtility.getShort(data, offset);*/
         offset += 2;

         size = ByteArrayHelper.getShort(data, offset);
         offset += 2;

         name = MPPUtility.getUnicodeString(data, offset);
         offset += 64;

         if (!name.isEmpty())
         {
            FontBase fontBase = new FontBase(Integer.valueOf(loop), name, size);
            m_fontBases.put(fontBase.getIndex(), fontBase);
         }
      }
   }

   /**
    * Retrieve any task field value lists defined in the MPP file.
    */
   private void processCustomValueLists() throws IOException
   {
      CustomFieldValueReader9 reader = new CustomFieldValueReader9(m_projectDir, m_file, m_projectProps);
      reader.process();
   }

   /**
    * Retrieve any resource field aliases defined in the MPP file.
    *
    * @param map index to field map
    * @param data resource field name alias data
    */
   private void processFieldNameAliases(Map<Integer, FieldType> map, byte[] data)
   {
      if (data != null)
      {
         int offset = 0;
         int index = 0;
         CustomFieldContainer fields = m_file.getCustomFields();
         while (offset < data.length)
         {
            String alias = MPPUtility.getUnicodeString(data, offset);
            if (!alias.isEmpty())
            {
               FieldType field = map.get(Integer.valueOf(index));
               if (field != null)
               {
                  fields.getOrCreate(field).setAlias(alias);
               }
            }
            offset += (alias.length() + 1) * 2;
            index++;
         }
      }
   }

   /**
    * This method maps the task unique identifiers to their index number
    * within the FixedData block.
    *
    * @param fieldMap field map
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @param taskVarData Variable task data
    * @return Mapping between task identifiers and block position
    */
   private TreeMap<Integer, Integer> createTaskMap(FieldMap fieldMap, FixedMeta taskFixedMeta, FixedData taskFixedData, Var2Data taskVarData)
   {
      TreeMap<Integer, Integer> taskMap = new TreeMap<>();
      int uniqueIdOffset = fieldMap.getFixedDataOffset(TaskField.UNIQUE_ID);
      int itemCount = taskFixedMeta.getAdjustedItemCount();
      int uniqueID;
      Integer key;

      //
      // First three items are not tasks, so let's skip them.
      // Note we're working backwards: where we have duplicate tasks the later ones
      // appear to be the correct versions (https://github.com/joniles/mpxj/issues/152)
      //
      for (int loop = itemCount - 1; loop > 2; loop--)
      {
         byte[] data = taskFixedData.getByteArrayValue(loop);
         if (data != null)
         {
            byte[] metaData = taskFixedMeta.getByteArrayValue(loop);

            //
            // Check for the deleted task flag
            //
            int flags = ByteArrayHelper.getInt(metaData, 0);
            if ((flags & 0x02) != 0)
            {
               // Project stores the deleted tasks unique id's into the fixed data as well
               // and at least in one case the deleted task was listed twice in the list
               // the second time with data with it causing a phantom task to be shown.
               // See CalendarErrorPhantomTasks.mpp
               //
               // So let's add the unique id for the deleted task into the map so we don't
               // accidentally include the task later.
               //
               uniqueID = ByteArrayHelper.getShort(data, TASK_UNIQUE_ID_FIXED_OFFSET); // Only a short stored for deleted tasks?
               key = Integer.valueOf(uniqueID);
               taskMap.put(key, null); // use null so we can easily ignore this later
            }
            else
            {
               //
               // Do we have a null task?
               //
               if (data.length == NULL_TASK_BLOCK_SIZE)
               {
                  uniqueID = ByteArrayHelper.getInt(data, TASK_UNIQUE_ID_FIXED_OFFSET);
                  key = Integer.valueOf(uniqueID);
                  if (!taskMap.containsKey(key))
                  {
                     taskMap.put(key, Integer.valueOf(loop));
                  }
               }
               else
               {
                  //
                  // We apply a heuristic here - if we have more than 75% of the data, we assume
                  // the task is valid.
                  //
                  int maxSize = fieldMap.getMaxFixedDataSize(0);
                  if (maxSize == 0 || ((data.length * 100) / maxSize) > 75)
                  {
                     uniqueID = ByteArrayHelper.getInt(data, uniqueIdOffset);
                     key = Integer.valueOf(uniqueID);

                     // Accept this task if it does not have a deleted unique ID or it has a deleted unique ID but it has var data entries
                     if (!taskMap.containsKey(key) || !taskVarData.getVarMeta().getTypes(key).isEmpty())
                     {
                        // If a task is already in the map, overwrite it as long as flags is not 0x04.
                        // TODO: gain a better understanding of why this works.
                        if (!taskMap.containsKey(key) || (flags & 0x04) == 0)
                        {
                           taskMap.put(key, Integer.valueOf(loop));
                        }
                     }
                  }
               }
            }
         }
      }

      return (taskMap);
   }

   /**
    * This method maps the resource unique identifiers to their index number
    * within the FixedData block.
    *
    * @param fieldMap field map
    * @param rscFixedMeta resource fixed meta data
    * @param rscFixedData resource fixed data
    * @return map of resource IDs to resource data
    */
   private TreeMap<Integer, Integer> createResourceMap(FieldMap fieldMap, FixedMeta rscFixedMeta, FixedData rscFixedData)
   {
      TreeMap<Integer, Integer> resourceMap = new TreeMap<>();
      int itemCount = rscFixedMeta.getAdjustedItemCount();

      for (int loop = 0; loop < itemCount; loop++)
      {
         byte[] data = rscFixedData.getByteArrayValue(loop);
         if (data == null || data.length < fieldMap.getMaxFixedDataSize(0))
         {
            continue;
         }

         // Check for the deleted resource flag
         byte[] metaData = rscFixedMeta.getByteArrayValue(loop);
         int flags = ByteArrayHelper.getInt(metaData, 0);
         if ((flags & 0x02) != 0)
         {
            continue;
         }

         Integer uniqueID = Integer.valueOf(ByteArrayHelper.getShort(data, 0));
         resourceMap.put(uniqueID, Integer.valueOf(loop));
      }

      return resourceMap;
   }

   /**
    * The format of the calendar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    */
   private void processCalendarData() throws IOException
   {
      CalendarFactory factory = new MPP9CalendarFactory(m_file);
      factory.processCalendarData(m_projectDir, m_projectProps, m_inputStreamFactory, m_resourceMap);
   }

   /**
    * This method extracts and collates task data. The code below
    * goes through the modifier methods of the Task class in alphabetical
    * order extracting the data from the MPP file. Where there is no
    * mapping (e.g. the field is calculated on the fly, or we can't
    * find it in the data) the line is commented out.
    *
    * The missing boolean attributes are probably represented in the Props
    * section of the task data, which we have yet to decode.
    */
   private void processTaskData() throws IOException
   {
      FieldMap fieldMap = new FieldMap9(m_file);
      fieldMap.createTaskFieldMap(m_projectProps);

      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      VarMeta taskVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data(m_file, taskVarMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData(taskFixedMeta, m_inputStreamFactory.getInstance(taskDir, "FixedData"), 768, fieldMap.getMaxFixedDataSize(0));
      //System.out.println(taskFixedData);
      //System.out.println(taskFixedMeta);
      //System.out.println(taskVarMeta);
      //System.out.println(taskVarData);

      processFieldNameAliases(TASK_FIELD_ALIASES, m_projectProps.getByteArray(Props.TASK_FIELD_NAME_ALIASES));

      TreeMap<Integer, Integer> taskMap = createTaskMap(fieldMap, taskFixedMeta, taskFixedData, taskVarData);
      // The var data may not contain all the tasks as tasks with no var data assigned will
      // not be saved in there. Most notably these are tasks with no name. So use the task map
      // which contains all the tasks.
      Integer[] uniqueIdArray = taskMap.keySet().toArray(new Integer[0]); //taskVarMeta.getUniqueIdentifierArray();
      Integer offset;
      byte[] data;
      byte[] metaData;
      Task task;
      boolean autoWBS = true;
      List<Task> externalTasks = new ArrayList<>();
      RecurringTaskReader recurringTaskReader = null;
      HyperlinkReader hyperlinkReader = new HyperlinkReader();

      for (Integer uniqueID : uniqueIdArray)
      {
         offset = taskMap.get(uniqueID);
         if (!taskFixedData.isValidOffset(offset))
         {
            continue;
         }

         data = taskFixedData.getByteArrayValue(offset.intValue());
         Integer id = Integer.valueOf(ByteArrayHelper.getInt(data, fieldMap.getFixedDataOffset(TaskField.ID)));
         if (data.length == NULL_TASK_BLOCK_SIZE)
         {
            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(Integer.valueOf(ByteArrayHelper.getShort(data, TASK_UNIQUE_ID_FIXED_OFFSET)));
            task.setID(Integer.valueOf(ByteArrayHelper.getShort(data, TASK_ID_FIXED_OFFSET)));
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());
            continue;
         }

         if (data.length < fieldMap.getMaxFixedDataSize(0))
         {
            continue;
         }

         if (uniqueID.intValue() != 0 && !taskVarMeta.containsKey(uniqueID))
         {
            continue;
         }

         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
         //System.out.println (ByteArrayHelper.hexdump(data, false, 16, ""));
         //System.out.println (ByteArrayHelper.hexdump(metaData, 8, 4, false));
         //MPPUtility.dataDump(data, true, true, true, true, true, true, true);
         //MPPUtility.dataDump(metaData, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(taskVarData, id, true, true, true, true, true, true);
         byte[] recurringData = taskVarData.getByteArray(uniqueID, fieldMap.getVarDataKey(TaskField.RECURRING_DATA));

         Task temp = m_file.getTaskByID(id);
         if (temp != null)
         {
            // Task with this id already exists... determine if this is the 'real' task by seeing
            // if this task has some var data. This is sort of hokey, but it's the best method I have
            // been able to see.
            if (!taskVarMeta.getUniqueIdentifierSet().contains(uniqueID))
            {
               // Sometimes Project contains phantom tasks that coexist on the same id as a valid
               // task. In this case don't want to include the phantom task. Seems to be a very rare case.
               continue;
            }
            else
               if (temp.getName() == null)
               {
                  // Ok, this looks valid. Remove the previous instance since it is most likely not a valid task.
                  // At worst case this removes a task with an empty name.
                  m_file.removeTask(temp);
               }
         }

         task = m_file.addTask();

         task.disableEvents();
         fieldMap.populateContainer(FieldTypeClass.TASK, task, uniqueID, new byte[][]
         {
            data
         }, taskVarData);
         task.enableEvents();

         task.setEffortDriven((metaData[11] & 0x10) != 0);

         task.setEstimated(getDurationEstimated(ByteArrayHelper.getShort(data, fieldMap.getFixedDataOffset(TaskField.ACTUAL_DURATION_UNITS))));

         task.setExpanded(((metaData[12] & 0x02) == 0));

         if (NumberHelper.getInt(task.getSubprojectTaskID()) != 0)
         {
            task.setExternalTask(true);
            externalTasks.add(task);
         }

         task.setFlag(1, (metaData[37] & 0x20) != 0);
         task.setFlag(2, (metaData[37] & 0x40) != 0);
         task.setFlag(3, (metaData[37] & 0x80) != 0);
         task.setFlag(4, (metaData[38] & 0x01) != 0);
         task.setFlag(5, (metaData[38] & 0x02) != 0);
         task.setFlag(6, (metaData[38] & 0x04) != 0);
         task.setFlag(7, (metaData[38] & 0x08) != 0);
         task.setFlag(8, (metaData[38] & 0x10) != 0);
         task.setFlag(9, (metaData[38] & 0x20) != 0);
         task.setFlag(10, (metaData[38] & 0x40) != 0);
         task.setFlag(11, (metaData[38] & 0x80) != 0);
         task.setFlag(12, (metaData[39] & 0x01) != 0);
         task.setFlag(13, (metaData[39] & 0x02) != 0);
         task.setFlag(14, (metaData[39] & 0x04) != 0);
         task.setFlag(15, (metaData[39] & 0x08) != 0);
         task.setFlag(16, (metaData[39] & 0x10) != 0);
         task.setFlag(17, (metaData[39] & 0x20) != 0);
         task.setFlag(18, (metaData[39] & 0x40) != 0);
         task.setFlag(19, (metaData[39] & 0x80) != 0);
         task.setFlag(20, (metaData[40] & 0x01) != 0);
         task.setHideBar((metaData[10] & 0x80) != 0);
         hyperlinkReader.read(task, taskVarData.getByteArray(uniqueID, fieldMap.getVarDataKey(TaskField.HYPERLINK_DATA)));

         task.setID(id);
         task.setIgnoreResourceCalendar(((metaData[10] & 0x02) != 0));
         task.setLevelAssignments((metaData[13] & 0x04) != 0);
         task.setLevelingCanSplit((metaData[13] & 0x02) != 0);
         task.setMarked((metaData[9] & 0x40) != 0);
         task.setMilestone((metaData[8] & 0x20) != 0);

         task.setOutlineCode(1, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE1_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(2, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE2_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(3, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE3_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(4, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE4_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(5, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE5_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(6, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE6_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(7, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE7_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(8, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE8_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(9, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE9_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode(10, m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE10_INDEX), OUTLINECODE_DATA));

         task.setRollup((metaData[10] & 0x08) != 0);
         task.setUniqueID(uniqueID);

         switch (task.getConstraintType())
         {
            //
            // Adjust the start and finish dates if the task
            // is constrained to start as late as possible.
            //
            case AS_LATE_AS_POSSIBLE:
            {
               if (LocalDateTimeHelper.compare(task.getStart(), task.getLateStart()) < 0)
               {
                  task.setStart(task.getLateStart());
               }
               if (LocalDateTimeHelper.compare(task.getFinish(), task.getLateFinish()) < 0)
               {
                  task.setFinish(task.getLateFinish());
               }
               break;
            }

            case START_NO_LATER_THAN:
            case FINISH_NO_LATER_THAN:
            {
               if (LocalDateTimeHelper.compare(task.getFinish(), task.getStart()) < 0)
               {
                  task.setFinish(task.getLateFinish());
               }
               break;
            }

            default:
            {
               break;
            }
         }

         //
         // Retrieve task recurring data
         //
         if (recurringData != null)
         {
            if (recurringTaskReader == null)
            {
               recurringTaskReader = new RecurringTaskReader(m_file.getProjectProperties());
            }
            recurringTaskReader.processRecurringTask(task, recurringData);
            task.setRecurring(true);
         }

         //
         // Configure the calendar
         //
         Integer calendarID = (Integer) task.getCachedValue(TaskField.CALENDAR_UNIQUE_ID);
         if (calendarID != null)
         {
            if (calendarID.intValue() == -1)
            {
               task.setCalendarUniqueID(null);
            }
            else
            {
               ProjectCalendar calendar = m_file.getCalendarByUniqueID(calendarID);
               if (calendar != null)
               {
                  task.setCalendar(calendar);
               }
            }
         }

         //
         // Set the subproject and external task flag
         //
         String sp = m_taskSubProjects.get(task.getUniqueID());
         if (sp != null)
         {
            task.setSubprojectFile(sp);
            Integer subprojectTaskUniqueID = task.getSubprojectTaskUniqueID();
            if (subprojectTaskUniqueID != null)
            {
               task.setSubprojectTaskUniqueID(Integer.valueOf(subprojectTaskUniqueID.intValue() & 0xFFFF));
            }
         }

         if (m_externalTasks.contains(task.getUniqueID()) && NumberHelper.getInt(task.getSubprojectTaskUniqueID()) != 0)
         {
            // The condition preserves external tasks which no longer have an associated subproject filename
            task.setExternalTask(m_externalTasks.contains(task.getUniqueID()));
         }

         //
         // If we have a WBS value from the MPP file, don't autogenerate
         //
         if (task.getWBS() != null)
         {
            autoWBS = false;
         }

         //
         // Process any enterprise columns
         //
         processTaskEnterpriseColumns(fieldMap, task, taskVarData);

         //
         // Unfortunately it looks like 'null' tasks sometimes make it through,
         // so let's check for to see if we need to mark this task as a null
         // task after all.
         //
         if (task.getName() == null && ((task.getStart() == null || task.getStart().equals(MPPUtility.EPOCH_DATE)) || (task.getFinish() == null || task.getFinish().equals(MPPUtility.EPOCH_DATE)) || (task.getCreateDate() == null || task.getCreateDate().equals(MPPUtility.EPOCH_DATE))))
         {
            m_file.removeTask(task);
            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(Integer.valueOf(ByteArrayHelper.getInt(data, TASK_UNIQUE_ID_FIXED_OFFSET)));
            task.setID(Integer.valueOf(ByteArrayHelper.getInt(data, TASK_ID_FIXED_OFFSET)));
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());

            continue;
         }

         m_taskOrder.put(task.getID(), task.getUniqueID());

         //
         // Fire the task read event
         //
         m_eventManager.fireTaskReadEvent(task);
         //System.out.println(task);
         //dumpUnknownData (task.getName(), UNKNOWN_TASK_DATA, data);
      }

      //
      // Enable auto WBS if necessary
      //
      m_file.getProjectConfig().setAutoWBS(autoWBS);

      //
      // We have now read all of the tasks, so we are in a position
      // to perform post-processing to set up the relevant details
      // for each external task.
      //
      if (!externalTasks.isEmpty())
      {
         processExternalTasks(externalTasks);
      }
   }

   /**
    * Extracts task enterprise column values.
    *
    * @param fieldMap fieldMap
    * @param task task instance
    * @param taskVarData task var data
    */
   private void processTaskEnterpriseColumns(FieldMap fieldMap, Task task, Var2Data taskVarData)
   {
      byte[] data = null;
      Integer varDataKey = fieldMap.getVarDataKey(TaskField.ENTERPRISE_DATA);

      if (varDataKey != null)
      {
         data = taskVarData.getByteArray(task.getUniqueID(), varDataKey);
      }

      if (data == null)
      {
         return;
      }

      PropsBlock props = new PropsBlock(data);
      //System.out.println(props);

      for (Integer key : props.keySet())
      {
         FieldType field = FieldTypeHelper.getInstance(m_file, key.intValue());
         if (field == null || field.getDataType() == null)
         {
            continue;
         }

         Object value = null;

         switch (field.getDataType())
         {
            case CURRENCY:
            {
               value = Double.valueOf(props.getDouble(key) / 100);
               break;
            }

            case DATE:
            {
               value = props.getTimestamp(key);
               break;
            }

            case WORK:
            {
               double durationValueInHours = MPPUtility.getDouble(props.getByteArray(key), 0) / 60000;
               value = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
               break;
            }

            case DURATION:
            {
               byte[] durationData = props.getByteArray(key);
               double durationValueInHours = ((double) ByteArrayHelper.getInt(durationData, 0)) / 600;
               TimeUnit durationUnits;
               if (durationData.length < 6)
               {
                  durationUnits = TimeUnit.DAYS;
               }
               else
               {
                  durationUnits = MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(durationData, 4));
               }
               Duration duration = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
               value = duration.convertUnits(durationUnits, m_file.getProjectProperties());
               break;
            }

            case BOOLEAN:
            {
               field = null;
               int bits = props.getInt(key);
               task.set(TaskField.ENTERPRISE_FLAG1, Boolean.valueOf((bits & 0x00002) != 0));
               task.set(TaskField.ENTERPRISE_FLAG2, Boolean.valueOf((bits & 0x00004) != 0));
               task.set(TaskField.ENTERPRISE_FLAG3, Boolean.valueOf((bits & 0x00008) != 0));
               task.set(TaskField.ENTERPRISE_FLAG4, Boolean.valueOf((bits & 0x00010) != 0));
               task.set(TaskField.ENTERPRISE_FLAG5, Boolean.valueOf((bits & 0x00020) != 0));
               task.set(TaskField.ENTERPRISE_FLAG6, Boolean.valueOf((bits & 0x00040) != 0));
               task.set(TaskField.ENTERPRISE_FLAG7, Boolean.valueOf((bits & 0x00080) != 0));
               task.set(TaskField.ENTERPRISE_FLAG8, Boolean.valueOf((bits & 0x00100) != 0));
               task.set(TaskField.ENTERPRISE_FLAG9, Boolean.valueOf((bits & 0x00200) != 0));
               task.set(TaskField.ENTERPRISE_FLAG10, Boolean.valueOf((bits & 0x00400) != 0));
               task.set(TaskField.ENTERPRISE_FLAG11, Boolean.valueOf((bits & 0x00800) != 0));
               task.set(TaskField.ENTERPRISE_FLAG12, Boolean.valueOf((bits & 0x01000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG13, Boolean.valueOf((bits & 0x02000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG14, Boolean.valueOf((bits & 0x04000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG15, Boolean.valueOf((bits & 0x08000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG16, Boolean.valueOf((bits & 0x10000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG17, Boolean.valueOf((bits & 0x20000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG18, Boolean.valueOf((bits & 0x40000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG19, Boolean.valueOf((bits & 0x80000) != 0));
               task.set(TaskField.ENTERPRISE_FLAG20, Boolean.valueOf((bits & 0x100000) != 0));
               break;
            }

            case NUMERIC:
            {
               value = Double.valueOf(props.getDouble(key));
               break;
            }

            case STRING:
            {
               value = props.getUnicodeString(key);
               break;
            }

            case PERCENTAGE:
            {
               value = Integer.valueOf(props.getShort(key));
               break;
            }

            default:
            {
               break;
            }
         }

         task.set(field, value);
      }
   }

   /**
    * Extracts resource enterprise column data.
    *
    * @param fieldMap field map
    * @param resource resource instance
    * @param resourceVarData resource var data
    */
   private void processResourceEnterpriseColumns(FieldMap fieldMap, Resource resource, Var2Data resourceVarData)
   {
      byte[] data = null;
      Integer varDataKey = fieldMap.getVarDataKey(ResourceField.ENTERPRISE_DATA);
      if (varDataKey != null)
      {
         data = resourceVarData.getByteArray(resource.getUniqueID(), varDataKey);
      }

      if (data == null)
      {
         return;
      }

      PropsBlock props = new PropsBlock(data);
      //System.out.println(props);
      resource.setCreationDate(props.getTimestamp(Props.RESOURCE_CREATION_DATE));

      for (Integer key : props.keySet())
      {
         FieldType field = FieldTypeHelper.getInstance(m_file, key.intValue());
         if (field == null || field.getDataType() == null)
         {
            continue;
         }
         Object value = null;

         switch (field.getDataType())
         {
            case CURRENCY:
            {
               value = Double.valueOf(props.getDouble(key) / 100);
               break;
            }

            case DATE:
            {
               value = props.getTimestamp(key);
               break;
            }

            case DURATION:
            {
               byte[] durationData = props.getByteArray(key);
               double durationValueInHours = ((double) ByteArrayHelper.getInt(durationData, 0)) / 600;
               TimeUnit durationUnits;
               if (durationData.length < 6)
               {
                  durationUnits = TimeUnit.DAYS;
               }
               else
               {
                  durationUnits = MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(durationData, 4));
               }
               Duration duration = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
               value = duration.convertUnits(durationUnits, m_file.getProjectProperties());
               break;

            }

            case BOOLEAN:
            {
               if (field == ResourceField.FLAG1)
               {
                  field = null;
                  int bits = props.getInt(key);
                  resource.set(ResourceField.ENTERPRISE_FLAG1, Boolean.valueOf((bits & 0x00002) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG2, Boolean.valueOf((bits & 0x00004) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG3, Boolean.valueOf((bits & 0x00008) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG4, Boolean.valueOf((bits & 0x00010) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG5, Boolean.valueOf((bits & 0x00020) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG6, Boolean.valueOf((bits & 0x00040) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG7, Boolean.valueOf((bits & 0x00080) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG8, Boolean.valueOf((bits & 0x00100) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG9, Boolean.valueOf((bits & 0x00200) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG10, Boolean.valueOf((bits & 0x00400) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG11, Boolean.valueOf((bits & 0x00800) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG12, Boolean.valueOf((bits & 0x01000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG13, Boolean.valueOf((bits & 0x02000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG14, Boolean.valueOf((bits & 0x04000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG15, Boolean.valueOf((bits & 0x08000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG16, Boolean.valueOf((bits & 0x10000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG17, Boolean.valueOf((bits & 0x20000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG18, Boolean.valueOf((bits & 0x40000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG19, Boolean.valueOf((bits & 0x80000) != 0));
                  resource.set(ResourceField.ENTERPRISE_FLAG20, Boolean.valueOf((bits & 0x100000) != 0));
               }

               if (field == ResourceField.GENERIC)
               {
                  field = null;
                  resource.setGeneric(props.getShort(key) != 0);
               }

               break;
            }

            case NUMERIC:
            {
               value = Double.valueOf(props.getDouble(key));
               break;
            }

            case STRING:
            {
               value = props.getUnicodeString(key);
               break;
            }

            default:
            {
               break;
            }
         }

         resource.set(field, value);
      }
   }

   /**
    * The project files to which external tasks relate appear not to be
    * held against each task, instead there appears to be the concept
    * of the "current" external task file, i.e. the last one used.
    * This method iterates through the list of tasks marked as external
    * and attempts to ensure that the correct external project data (in the
    * form of a SubProject object) is linked to the task.
    *
    * @param externalTasks list of tasks marked as external
    */
   private void processExternalTasks(List<Task> externalTasks)
   {
      //
      // Sort the list of tasks into ID order
      //
      Collections.sort(externalTasks);

      //
      // Find any external tasks which don't have a sub project
      // object, and set this attribute using the most recent
      // value.
      //
      String currentSubProject = null;

      for (Task currentTask : externalTasks)
      {
         String sp = currentTask.getSubprojectFile();
         if (sp == null)
         {
            if (currentSubProject != null)
            {
               currentTask.setSubprojectFile(currentSubProject);
            }
         }
         else
         {
            currentSubProject = sp;
         }

         if (currentSubProject != null)
         {
            //System.out.println ("Task: " +currentTask.getUniqueID() + " " + currentTask.getName() + " File=" + currentSubProject.getFullPath() + " ID=" + currentTask.getExternalTaskID());
            currentTask.setProject(currentSubProject);
         }
      }
   }

   /**
    * This method extracts and collates constraint data.
    */
   private void processConstraintData() throws IOException
   {
      ConstraintFactory factory = new ConstraintFactory();
      factory.process(m_projectDir, m_file, m_inputStreamFactory);
   }

   /**
    * This method extracts and collates resource data.
    */
   private void processResourceData() throws IOException
   {
      FieldMap fieldMap = new FieldMap9(m_file);
      fieldMap.createResourceFieldMap(m_projectProps);

      DirectoryEntry rscDir = (DirectoryEntry) m_projectDir.getEntry("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data(m_file, rscVarMeta, new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData(rscFixedMeta, m_inputStreamFactory.getInstance(rscDir, "FixedData"));
      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      //System.out.println(rscFixedMeta);
      //System.out.println(rscFixedData);

      processFieldNameAliases(RESOURCE_FIELD_ALIASES, m_projectProps.getByteArray(Props.RESOURCE_FIELD_NAME_ALIASES));

      TreeMap<Integer, Integer> resourceMap = createResourceMap(fieldMap, rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifierArray();
      Integer offset;
      byte[] data;
      byte[] metaData;
      Resource resource;
      HyperlinkReader hyperlinkReader = new HyperlinkReader();

      for (Integer id : uniqueid)
      {
         offset = resourceMap.get(id);
         if (offset == null)
         {
            continue;
         }
         data = rscFixedData.getByteArrayValue(offset.intValue());

         //MPPUtility.dataDump(data, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(rscVarData, id, true, true, true, true, true, true);

         resource = m_file.addResource();

         resource.disableEvents();
         fieldMap.populateContainer(FieldTypeClass.RESOURCE, resource, id, new byte[][]
         {
            data
         }, rscVarData);
         resource.enableEvents();

         hyperlinkReader.read(resource, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.HYPERLINK_DATA)));
         resource.setID(Integer.valueOf(ByteArrayHelper.getInt(data, 4)));

         resource.setOutlineCode(1, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE1_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(2, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE2_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(3, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE3_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(4, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE4_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(5, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE5_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(6, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE6_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(7, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE7_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(8, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE8_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(9, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE9_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode(10, m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE10_INDEX), OUTLINECODE_DATA));

         resource.setUniqueID(id);

         metaData = rscFixedMeta.getByteArrayValue(offset.intValue());
         resource.setFlag(1, (metaData[28] & 0x40) != 0);
         resource.setFlag(2, (metaData[28] & 0x80) != 0);
         resource.setFlag(3, (metaData[29] & 0x01) != 0);
         resource.setFlag(4, (metaData[29] & 0x02) != 0);
         resource.setFlag(5, (metaData[29] & 0x04) != 0);
         resource.setFlag(6, (metaData[29] & 0x08) != 0);
         resource.setFlag(7, (metaData[29] & 0x10) != 0);
         resource.setFlag(8, (metaData[29] & 0x20) != 0);
         resource.setFlag(9, (metaData[29] & 0x40) != 0);
         resource.setFlag(10, (metaData[28] & 0x20) != 0);
         resource.setFlag(11, (metaData[29] & 0x80) != 0);
         resource.setFlag(12, (metaData[30] & 0x01) != 0);
         resource.setFlag(13, (metaData[30] & 0x02) != 0);
         resource.setFlag(14, (metaData[30] & 0x04) != 0);
         resource.setFlag(15, (metaData[30] & 0x08) != 0);
         resource.setFlag(16, (metaData[30] & 0x10) != 0);
         resource.setFlag(17, (metaData[30] & 0x20) != 0);
         resource.setFlag(18, (metaData[30] & 0x40) != 0);
         resource.setFlag(19, (metaData[30] & 0x80) != 0);
         resource.setFlag(20, (metaData[31] & 0x01) != 0);

         //
         // Configure the resource calendar
         //
         resource.setCalendar(m_resourceMap.get(id));

         //
         // Process any enterprise columns
         //
         processResourceEnterpriseColumns(fieldMap, resource, rscVarData);

         //
         // Convert rate units
         //
         MPPUtility.convertRateFromHours(m_file, resource, ResourceField.STANDARD_RATE, ResourceField.STANDARD_RATE_UNITS);
         MPPUtility.convertRateFromHours(m_file, resource, ResourceField.OVERTIME_RATE, ResourceField.OVERTIME_RATE_UNITS);

         //
         // Process cost rate tables
         //
         CostRateTableFactory crt = new CostRateTableFactory(m_file);
         crt.process(resource, 0, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_A)));
         crt.process(resource, 1, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_B)));
         crt.process(resource, 2, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_C)));
         crt.process(resource, 3, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_D)));
         crt.process(resource, 4, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_E)));

         //
         // Process availability table
         //
         AvailabilityFactory af = new AvailabilityFactory();
         af.process(resource, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.AVAILABILITY_DATA)));

         //
         // Process resource type
         //
         if ((metaData[9] & 0x02) != 0)
         {
            resource.setType(ResourceType.WORK);
         }
         else
         {
            resource.setType(ResourceType.MATERIAL);
         }

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * This method extracts and collates resource assignment data.
    */
   private void processAssignmentData() throws IOException
   {
      FieldMap fieldMap = new FieldMap9(m_file);
      fieldMap.createAssignmentFieldMap(m_projectProps);

      DirectoryEntry assnDir = (DirectoryEntry) m_projectDir.getEntry("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data(m_file, assnVarMeta, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Var2Data"))));

      FixedMeta assnFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData(142, m_inputStreamFactory.getInstance(assnDir, "FixedData"));
      if (assnFixedData.getItemCount() != assnFixedMeta.getAdjustedItemCount())
      {
         assnFixedData = new FixedData(assnFixedMeta, m_inputStreamFactory.getInstance(assnDir, "FixedData"));
      }

      ResourceAssignmentFactory factory = new ResourceAssignmentFactory();
      factory.process(m_file, fieldMap, null, m_reader.getUseRawTimephasedData(), assnVarMeta, assnVarData, assnFixedMeta, assnFixedData, null, assnFixedMeta.getAdjustedItemCount());
   }

   /**
    * This method is used to determine if a duration is estimated.
    *
    * @param type Duration units value
    * @return boolean Estimated flag
    */
   private boolean getDurationEstimated(int type)
   {
      return ((type & DURATION_CONFIRMED_MASK) != 0);
   }

   /**
    * This method extracts view data from the MPP file.
    */
   private void processViewData() throws IOException
   {
      if (m_viewDir.hasEntry("CV_iew"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CV_iew");
         VarMeta viewVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data viewVarData = new Var2Data(m_file, viewVarMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
         FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
         FixedData fixedData = new FixedData(122, m_inputStreamFactory.getInstance(dir, "FixedData"));

         int items = fixedMeta.getAdjustedItemCount();
         View view;
         ViewFactory factory = new ViewFactory9();

         int lastOffset = -1;
         for (int loop = 0; loop < items; loop++)
         {
            byte[] fm = fixedMeta.getByteArrayValue(loop);
            int offset = ByteArrayHelper.getShort(fm, 4);
            if (offset > lastOffset)
            {
               byte[] fd = fixedData.getByteArrayValue(fixedData.getIndexFromOffset(offset));
               if (fd != null)
               {
                  view = factory.createView(m_file, fm, fd, viewVarData, m_fontBases);
                  m_file.getViews().add(view);
                  //System.out.print(view);
               }
               lastOffset = offset;
            }
         }
      }
   }

   /**
    * This method extracts table data from the MPP file.
    *
    * TODO: This implementation does not deal with MPP9 files saved by later versions of MS Project
    */
   private void processTableData() throws IOException
   {
      if (m_viewDir.hasEntry("CTable"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CTable");
         //FixedMeta fixedMeta = new FixedMeta(getEncryptableInputStream(dir, "FixedMeta"), 9);
         InputStream stream = m_inputStreamFactory.getInstance(dir, "FixedData");
         int blockSize = stream.available() % 115 == 0 ? 115 : 110;
         FixedData fixedData = new FixedData(blockSize, stream);
         VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

         TableContainer container = m_file.getTables();
         TableFactory factory = new TableFactory(TABLE_COLUMN_DATA_STANDARD, TABLE_COLUMN_DATA_ENTERPRISE, TABLE_COLUMN_DATA_BASELINE);
         int items = fixedData.getItemCount();
         for (int loop = 0; loop < items; loop++)
         {
            byte[] data = fixedData.getByteArrayValue(loop);
            Table table = factory.createTable(m_file, data, varMeta, varData);
            container.add(table);
            //System.out.println(table);
         }
      }
   }

   /**
    * Read filter definitions.
    *
    * TODO: Doesn't work correctly with MPP9 files saved by Project 2007 and 2010
    */
   private void processFilterData() throws IOException
   {
      if (m_viewDir.hasEntry("CFilter"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CFilter");
         //FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 9);
         //FixedData fixedData = new FixedData(fixedMeta, getEncryptableInputStream(dir, "FixedData"));
         InputStream stream = m_inputStreamFactory.getInstance(dir, "FixedData");
         int blockSize = stream.available() % 115 == 0 ? 115 : 110;
         FixedData fixedData = new FixedData(blockSize, stream, true);
         VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

         //System.out.println(fixedMeta);
         //System.out.println(fixedData);
         //System.out.println(varMeta);
         //System.out.println(varData);

         FilterReader reader = new FilterReader9();
         reader.process(m_file, fixedData, varData);
      }
   }

   /**
    * Read group definitions.
    *
    * TODO: Doesn't work correctly with MPP9 files saved by Project 2007 and 2010
    */
   private void processGroupData() throws IOException
   {
      if (m_viewDir.hasEntry("CGrouping"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CGrouping");
         //FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 9);
         FixedData fixedData = new FixedData(110, m_inputStreamFactory.getInstance(dir, "FixedData"));
         VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

         //      System.out.println(fixedMeta);
         //      System.out.println(fixedData);
         //      System.out.println(varMeta);
         //      System.out.println(varData);

         GroupReader reader = new GroupReader9();
         reader.process(m_file, fixedData, varData, m_fontBases);
      }
   }

   /**
    * Read saved view state from an MPP file.
    */
   private void processSavedViewState() throws IOException
   {
      if (m_viewDir.hasEntry("CEdl"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CEdl");
         VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
         //System.out.println(varMeta);
         //System.out.println(varData);

         InputStream is = m_inputStreamFactory.getInstance(dir, "FixedData");
         byte[] fixedData = InputStreamHelper.readAvailable(is);
         //System.out.println(ByteArrayHelper.hexdump(fixedData, false, 16, ""));

         ViewStateReader reader = new ViewStateReader9();
         reader.process(m_file, varData, fixedData);
      }
   }

   /**
    * Read data link definitions.
    */
   private void processDataLinks() throws IOException
   {
      if (m_viewDir.hasEntry("CEdl"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CEdl");
         FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
         FixedData fixedData = new FixedData(fixedMeta, m_inputStreamFactory.getInstance(dir, "FixedData"));
         VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

         DataLinkFactory factory = new DataLinkFactory(m_file, fixedData, varData);
         factory.process();
      }
   }

   /**
    * This method is called to try to catch any invalid tasks that may have sneaked past all our other checks.
    * This is done by validating the tasks by task ID.
    */
   //   private void postProcessTasks()
   //   {
   //      List<Task> allTasks = m_file.getTasks();
   //      if (allTasks.size() > 1)
   //      {
   //         Collections.sort(allTasks);
   //
   //         int taskID = -1;
   //         int lastTaskID = -1;
   //
   //         for (int i = 0; i < allTasks.size(); i++)
   //         {
   //            Task task = allTasks.get(i);
   //            taskID = NumberHelper.getInt(task.getID());
   //            // In Project the tasks IDs are always contiguous so we can spot invalid tasks by making sure all
   //            // IDs are represented.
   //            if (!task.getNull() && lastTaskID != -1 && taskID > lastTaskID + 1)
   //            {
   //               // This task looks to be invalid.
   //               task.setNull(true);
   //            }
   //            else
   //            {
   //               lastTaskID = taskID;
   //            }
   //         }
   //      }
   //   }

   private void postProcessTasks() throws MPXJException
   {
      //
      // Renumber ID values using a large increment to allow
      // space for later inserts.
      //
      TreeMap<Integer, Integer> taskMap = new TreeMap<>();
      int nextIDIncrement = ((m_nullTaskOrder.size() / 1000) + 1) * 2000;
      int nextID = (m_file.getTaskByUniqueID(Integer.valueOf(0)) == null ? nextIDIncrement : 0);
      for (Map.Entry<Integer, Integer> entry : m_taskOrder.entrySet())
      {
         taskMap.put(Integer.valueOf(nextID), entry.getValue());
         nextID += nextIDIncrement;
      }

      //
      // Insert any null tasks into the correct location
      //
      int insertionCount = 0;
      Map<Integer, Integer> offsetMap = new HashMap<>();
      for (Map.Entry<Integer, Integer> entry : m_nullTaskOrder.entrySet())
      {
         int idValue = entry.getKey().intValue();
         int baseTargetIdValue = (idValue - insertionCount) * nextIDIncrement;
         int targetIDValue = baseTargetIdValue;
         Integer previousOffsetKey = Integer.valueOf(baseTargetIdValue);
         Integer previousOffset = offsetMap.get(previousOffsetKey);
         int offset = previousOffset == null ? 0 : previousOffset.intValue() + 1;
         ++insertionCount;

         while (taskMap.containsKey(Integer.valueOf(targetIDValue)))
         {
            ++offset;
            if (offset == nextIDIncrement)
            {
               throw new MPXJException("Unable to fix task order");
            }
            targetIDValue = baseTargetIdValue - (nextIDIncrement - offset);
         }

         offsetMap.put(previousOffsetKey, Integer.valueOf(offset));
         taskMap.put(Integer.valueOf(targetIDValue), entry.getValue());
      }

      //
      // Finally, we can renumber the tasks
      //
      nextID = (m_file.getTaskByUniqueID(Integer.valueOf(0)) == null ? 1 : 0);
      for (Map.Entry<Integer, Integer> entry : taskMap.entrySet())
      {
         Task task = m_file.getTaskByUniqueID(entry.getValue());
         if (task != null)
         {
            task.setID(Integer.valueOf(nextID));
         }
         nextID++;
      }
   }

   //   private static void dumpUnknownData (String name, int[][] spec, byte[] data)
   //   {
   //      System.out.println (name);
   //      for (int loop=0; loop < spec.length; loop++)
   //      {
   //         System.out.println (spec[loop][0] + ": "+ ByteArrayHelper.hexdump(data, spec[loop][0], spec[loop][1], false));
   //      }
   //      System.out.println ();
   //   }

   //   private static final int[][] UNKNOWN_TASK_DATA = new int[][]
   //   {
   //      {36, 4},
   //      {42, 18},
   //      {116, 4},
   //      {134, 14},
   //      {144, 4},
   //      {148, 4},
   //      {152, 4},
   //      {156, 4},
   //      {248, 8},
   //   };

   //   private static final int[][] UNKNOWN_RESOURCE_DATA = new int[][]
   //   {
   //      {14, 6},
   //      {108, 16},
   //   };

   private MPPReader m_reader;
   private ProjectFile m_file;
   private EventManager m_eventManager;
   private DirectoryEntry m_root;
   private HashMap<Integer, ProjectCalendar> m_resourceMap;
   private Var2Data m_outlineCodeVarData;
   private Props9 m_projectProps;
   private Map<Integer, FontBase> m_fontBases;
   private Map<Integer, String> m_taskSubProjects;
   private Set<Integer> m_externalTasks;
   private DirectoryEntry m_projectDir;
   private DirectoryEntry m_viewDir;
   private Map<Integer, Integer> m_taskOrder;
   private Map<Integer, Integer> m_nullTaskOrder;
   private DocumentInputStreamFactory m_inputStreamFactory;

   // Signals the end of the list of subproject task unique ids
   private static final int SUBPROJECT_LISTEND = 0x00000303;

   // Signals that the previous value was for the subproject task unique id
   private static final int SUBPROJECT_TASKUNIQUEID0 = 0x00000000;
   private static final int SUBPROJECT_TASKUNIQUEID1 = 0x0B340000;
   private static final int SUBPROJECT_TASKUNIQUEID2 = 0x0ABB0000;
   private static final int SUBPROJECT_TASKUNIQUEID3 = 0x05A10000;
   private static final int SUBPROJECT_TASKUNIQUEID4 = 0x02F70000;
   private static final int SUBPROJECT_TASKUNIQUEID5 = 0x07010000;

   private static final Integer TABLE_COLUMN_DATA_STANDARD = Integer.valueOf(1);
   private static final Integer TABLE_COLUMN_DATA_ENTERPRISE = Integer.valueOf(2);
   private static final Integer TABLE_COLUMN_DATA_BASELINE = null;
   private static final Integer OUTLINECODE_DATA = Integer.valueOf(1);

   /**
    * Mask used to isolate confirmed flag from the duration units field.
    */
   private static final int DURATION_CONFIRMED_MASK = 0x20;

   /**
    * Deleted and null tasks have their ID and UniqueID attributes at fixed offsets.
    */
   private static final int TASK_UNIQUE_ID_FIXED_OFFSET = 0;
   private static final int TASK_ID_FIXED_OFFSET = 4;
   private static final int NULL_TASK_BLOCK_SIZE = 8;

   private static final Map<Integer, FieldType> RESOURCE_FIELD_ALIASES = new HashMap<>();
   static
   {
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(52), ResourceField.TEXT1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(53), ResourceField.TEXT2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(54), ResourceField.TEXT3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(55), ResourceField.TEXT4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(56), ResourceField.TEXT5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(57), ResourceField.TEXT6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(58), ResourceField.TEXT7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(59), ResourceField.TEXT8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(60), ResourceField.TEXT9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(61), ResourceField.TEXT10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(62), ResourceField.TEXT11);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(63), ResourceField.TEXT12);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(64), ResourceField.TEXT13);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(65), ResourceField.TEXT14);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(66), ResourceField.TEXT15);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(67), ResourceField.TEXT16);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(68), ResourceField.TEXT17);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(69), ResourceField.TEXT18);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(70), ResourceField.TEXT19);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(71), ResourceField.TEXT20);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(72), ResourceField.TEXT21);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(73), ResourceField.TEXT22);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(74), ResourceField.TEXT23);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(75), ResourceField.TEXT24);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(76), ResourceField.TEXT25);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(77), ResourceField.TEXT26);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(78), ResourceField.TEXT27);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(79), ResourceField.TEXT28);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(80), ResourceField.TEXT29);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(81), ResourceField.TEXT30);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(82), ResourceField.START1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(83), ResourceField.START2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(84), ResourceField.START3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(85), ResourceField.START4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(86), ResourceField.START5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(87), ResourceField.START6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(88), ResourceField.START7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(89), ResourceField.START8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(90), ResourceField.START9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(91), ResourceField.START10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(92), ResourceField.FINISH1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(93), ResourceField.FINISH2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(94), ResourceField.FINISH3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(95), ResourceField.FINISH4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(96), ResourceField.FINISH5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(97), ResourceField.FINISH6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(98), ResourceField.FINISH7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(99), ResourceField.FINISH8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(100), ResourceField.FINISH9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(101), ResourceField.FINISH10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(102), ResourceField.NUMBER1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(103), ResourceField.NUMBER2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(104), ResourceField.NUMBER3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(105), ResourceField.NUMBER4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(106), ResourceField.NUMBER5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(107), ResourceField.NUMBER6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(108), ResourceField.NUMBER7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(109), ResourceField.NUMBER8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(110), ResourceField.NUMBER9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(111), ResourceField.NUMBER10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(112), ResourceField.NUMBER11);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(113), ResourceField.NUMBER12);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(114), ResourceField.NUMBER13);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(115), ResourceField.NUMBER14);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(116), ResourceField.NUMBER15);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(117), ResourceField.NUMBER16);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(118), ResourceField.NUMBER17);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(119), ResourceField.NUMBER18);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(120), ResourceField.NUMBER19);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(121), ResourceField.NUMBER20);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(122), ResourceField.DURATION1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(123), ResourceField.DURATION2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(124), ResourceField.DURATION3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(125), ResourceField.DURATION4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(126), ResourceField.DURATION5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(127), ResourceField.DURATION6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(128), ResourceField.DURATION7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(129), ResourceField.DURATION8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(130), ResourceField.DURATION9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(131), ResourceField.DURATION10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(145), ResourceField.DATE1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(146), ResourceField.DATE2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(147), ResourceField.DATE3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(148), ResourceField.DATE4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(149), ResourceField.DATE5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(150), ResourceField.DATE6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(151), ResourceField.DATE7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(152), ResourceField.DATE8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(153), ResourceField.DATE9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(154), ResourceField.DATE10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(155), ResourceField.OUTLINE_CODE1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(156), ResourceField.OUTLINE_CODE2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(157), ResourceField.OUTLINE_CODE3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(158), ResourceField.OUTLINE_CODE4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(159), ResourceField.OUTLINE_CODE5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(160), ResourceField.OUTLINE_CODE6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(161), ResourceField.OUTLINE_CODE7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(162), ResourceField.OUTLINE_CODE8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(163), ResourceField.OUTLINE_CODE9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(164), ResourceField.OUTLINE_CODE10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(165), ResourceField.FLAG10);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(166), ResourceField.FLAG1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(167), ResourceField.FLAG2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(168), ResourceField.FLAG3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(169), ResourceField.FLAG4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(170), ResourceField.FLAG5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(171), ResourceField.FLAG6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(172), ResourceField.FLAG7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(173), ResourceField.FLAG8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(174), ResourceField.FLAG9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(175), ResourceField.FLAG11);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(176), ResourceField.FLAG12);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(177), ResourceField.FLAG13);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(178), ResourceField.FLAG14);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(179), ResourceField.FLAG15);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(180), ResourceField.FLAG16);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(181), ResourceField.FLAG17);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(182), ResourceField.FLAG18);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(183), ResourceField.FLAG19);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(184), ResourceField.FLAG20);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(207), ResourceField.COST1);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(208), ResourceField.COST2);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(209), ResourceField.COST3);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(210), ResourceField.COST4);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(211), ResourceField.COST5);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(212), ResourceField.COST6);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(213), ResourceField.COST7);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(214), ResourceField.COST8);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(215), ResourceField.COST9);
      RESOURCE_FIELD_ALIASES.put(Integer.valueOf(216), ResourceField.COST10);
   }

   private static final Map<Integer, FieldType> TASK_FIELD_ALIASES = new HashMap<>();
   static
   {
      TASK_FIELD_ALIASES.put(Integer.valueOf(118), TaskField.TEXT1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(119), TaskField.TEXT2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(120), TaskField.TEXT3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(121), TaskField.TEXT4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(122), TaskField.TEXT5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(123), TaskField.TEXT6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(124), TaskField.TEXT7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(125), TaskField.TEXT8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(126), TaskField.TEXT9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(127), TaskField.TEXT10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(128), TaskField.START1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(129), TaskField.FINISH1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(130), TaskField.START2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(131), TaskField.FINISH2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(132), TaskField.START3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(133), TaskField.FINISH3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(134), TaskField.START4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(135), TaskField.FINISH4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(136), TaskField.START5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(137), TaskField.FINISH5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(138), TaskField.START6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(139), TaskField.FINISH6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(140), TaskField.START7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(141), TaskField.FINISH7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(142), TaskField.START8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(143), TaskField.FINISH8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(144), TaskField.START9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(145), TaskField.FINISH9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(146), TaskField.START10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(147), TaskField.FINISH10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(149), TaskField.NUMBER1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(150), TaskField.NUMBER2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(151), TaskField.NUMBER3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(152), TaskField.NUMBER4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(153), TaskField.NUMBER5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(154), TaskField.NUMBER6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(155), TaskField.NUMBER7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(156), TaskField.NUMBER8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(157), TaskField.NUMBER9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(158), TaskField.NUMBER10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(159), TaskField.DURATION1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(161), TaskField.DURATION2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(163), TaskField.DURATION3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(165), TaskField.DURATION4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(167), TaskField.DURATION5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(169), TaskField.DURATION6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(171), TaskField.DURATION7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(173), TaskField.DURATION8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(175), TaskField.DURATION9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(177), TaskField.DURATION10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(184), TaskField.DATE1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(185), TaskField.DATE2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(186), TaskField.DATE3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(187), TaskField.DATE4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(188), TaskField.DATE5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(189), TaskField.DATE6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(190), TaskField.DATE7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(191), TaskField.DATE8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(192), TaskField.DATE9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(193), TaskField.DATE10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(194), TaskField.TEXT11);
      TASK_FIELD_ALIASES.put(Integer.valueOf(195), TaskField.TEXT12);
      TASK_FIELD_ALIASES.put(Integer.valueOf(196), TaskField.TEXT13);
      TASK_FIELD_ALIASES.put(Integer.valueOf(197), TaskField.TEXT14);
      TASK_FIELD_ALIASES.put(Integer.valueOf(198), TaskField.TEXT15);
      TASK_FIELD_ALIASES.put(Integer.valueOf(199), TaskField.TEXT16);
      TASK_FIELD_ALIASES.put(Integer.valueOf(200), TaskField.TEXT17);
      TASK_FIELD_ALIASES.put(Integer.valueOf(201), TaskField.TEXT18);
      TASK_FIELD_ALIASES.put(Integer.valueOf(202), TaskField.TEXT19);
      TASK_FIELD_ALIASES.put(Integer.valueOf(203), TaskField.TEXT20);
      TASK_FIELD_ALIASES.put(Integer.valueOf(204), TaskField.TEXT21);
      TASK_FIELD_ALIASES.put(Integer.valueOf(205), TaskField.TEXT22);
      TASK_FIELD_ALIASES.put(Integer.valueOf(206), TaskField.TEXT23);
      TASK_FIELD_ALIASES.put(Integer.valueOf(207), TaskField.TEXT24);
      TASK_FIELD_ALIASES.put(Integer.valueOf(208), TaskField.TEXT25);
      TASK_FIELD_ALIASES.put(Integer.valueOf(209), TaskField.TEXT26);
      TASK_FIELD_ALIASES.put(Integer.valueOf(210), TaskField.TEXT27);
      TASK_FIELD_ALIASES.put(Integer.valueOf(211), TaskField.TEXT28);
      TASK_FIELD_ALIASES.put(Integer.valueOf(212), TaskField.TEXT29);
      TASK_FIELD_ALIASES.put(Integer.valueOf(213), TaskField.TEXT30);
      TASK_FIELD_ALIASES.put(Integer.valueOf(214), TaskField.NUMBER11);
      TASK_FIELD_ALIASES.put(Integer.valueOf(215), TaskField.NUMBER12);
      TASK_FIELD_ALIASES.put(Integer.valueOf(216), TaskField.NUMBER13);
      TASK_FIELD_ALIASES.put(Integer.valueOf(217), TaskField.NUMBER14);
      TASK_FIELD_ALIASES.put(Integer.valueOf(218), TaskField.NUMBER15);
      TASK_FIELD_ALIASES.put(Integer.valueOf(219), TaskField.NUMBER16);
      TASK_FIELD_ALIASES.put(Integer.valueOf(220), TaskField.NUMBER17);
      TASK_FIELD_ALIASES.put(Integer.valueOf(221), TaskField.NUMBER18);
      TASK_FIELD_ALIASES.put(Integer.valueOf(222), TaskField.NUMBER19);
      TASK_FIELD_ALIASES.put(Integer.valueOf(223), TaskField.NUMBER20);
      TASK_FIELD_ALIASES.put(Integer.valueOf(227), TaskField.OUTLINE_CODE1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(228), TaskField.OUTLINE_CODE2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(229), TaskField.OUTLINE_CODE3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(230), TaskField.OUTLINE_CODE4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(231), TaskField.OUTLINE_CODE5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(232), TaskField.OUTLINE_CODE6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(233), TaskField.OUTLINE_CODE7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(234), TaskField.OUTLINE_CODE8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(235), TaskField.OUTLINE_CODE9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(236), TaskField.OUTLINE_CODE10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(237), TaskField.FLAG1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(238), TaskField.FLAG2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(239), TaskField.FLAG3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(240), TaskField.FLAG4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(241), TaskField.FLAG5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(242), TaskField.FLAG6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(243), TaskField.FLAG7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(244), TaskField.FLAG8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(245), TaskField.FLAG9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(246), TaskField.FLAG10);
      TASK_FIELD_ALIASES.put(Integer.valueOf(247), TaskField.FLAG11);
      TASK_FIELD_ALIASES.put(Integer.valueOf(248), TaskField.FLAG12);
      TASK_FIELD_ALIASES.put(Integer.valueOf(249), TaskField.FLAG13);
      TASK_FIELD_ALIASES.put(Integer.valueOf(250), TaskField.FLAG14);
      TASK_FIELD_ALIASES.put(Integer.valueOf(251), TaskField.FLAG15);
      TASK_FIELD_ALIASES.put(Integer.valueOf(252), TaskField.FLAG16);
      TASK_FIELD_ALIASES.put(Integer.valueOf(253), TaskField.FLAG17);
      TASK_FIELD_ALIASES.put(Integer.valueOf(254), TaskField.FLAG18);
      TASK_FIELD_ALIASES.put(Integer.valueOf(255), TaskField.FLAG19);
      TASK_FIELD_ALIASES.put(Integer.valueOf(256), TaskField.FLAG20);
      TASK_FIELD_ALIASES.put(Integer.valueOf(278), TaskField.COST1);
      TASK_FIELD_ALIASES.put(Integer.valueOf(279), TaskField.COST2);
      TASK_FIELD_ALIASES.put(Integer.valueOf(280), TaskField.COST3);
      TASK_FIELD_ALIASES.put(Integer.valueOf(281), TaskField.COST4);
      TASK_FIELD_ALIASES.put(Integer.valueOf(282), TaskField.COST5);
      TASK_FIELD_ALIASES.put(Integer.valueOf(283), TaskField.COST6);
      TASK_FIELD_ALIASES.put(Integer.valueOf(284), TaskField.COST7);
      TASK_FIELD_ALIASES.put(Integer.valueOf(285), TaskField.COST8);
      TASK_FIELD_ALIASES.put(Integer.valueOf(286), TaskField.COST9);
      TASK_FIELD_ALIASES.put(Integer.valueOf(287), TaskField.COST10);
   }

}
