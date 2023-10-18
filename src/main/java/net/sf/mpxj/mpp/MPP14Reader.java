/*
 * file:       MPP14Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2010
 * date:       20/01/2010
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.InputStreamHelper;
import net.sf.mpxj.common.LocalDateTimeHelper;
import net.sf.mpxj.common.MicrosoftProjectConstants;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Table;
import net.sf.mpxj.TableContainer;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.View;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class is used to represent a Microsoft Project MPP14 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP14Reader implements MPPVariantReader
{
   /**
    * This method is used to process an MPP14 file. This is the file format
    * used by Project 14.
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
   private void populateMemberData(MPPReader reader, ProjectFile file, DirectoryEntry root) throws IOException, MPXJException
   {
      m_reader = reader;
      m_file = file;
      m_eventManager = file.getEventManager();
      m_root = root;

      //
      // Retrieve the high level document properties
      //
      Props props = new Props14(m_file, new DocumentInputStream(((DocumentEntry) root.getEntry("Props14"))));
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
      boolean encryptionXmlPresent = props.getByteArray(Props.PROTECTION_PASSWORD_HASH) != null;

      //
      // I've come across an example where the password flag was set, but the encryption XML
      // was missing. In this case the file is unencrypted and MS Project opens it without
      // prompting for a password. Checking for encryptionXmlPresent allows us to open
      // this file successfully.
      //
      if (passwordRequiredToRead && encryptionXmlPresent)
      {
         // Couldn't figure out how to get the password for MPP14 files so for now we just need to block the reading
         throw new MPXJException(MPXJException.PASSWORD_PROTECTED);
      }

      m_resourceMap = new HashMap<>();
      m_projectDir = (DirectoryEntry) root.getEntry("   114");
      m_viewDir = (DirectoryEntry) root.getEntry("   214");
      DirectoryEntry outlineCodeDir = (DirectoryEntry) m_projectDir.getEntry("TBkndOutlCode");
      m_outlineCodeVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("VarMeta"))));
      m_outlineCodeVarData = new Var2Data(file, m_outlineCodeVarMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Var2Data"))));
      FixedMeta outlineCodeFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("FixedMeta"))), 10);
      m_outlineCodeFixedData = new FixedData(outlineCodeFixedMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("FixedData"))));
      FixedMeta outlineCodeFixedMeta2 = new FixedMeta(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Fixed2Meta"))), 10);
      m_outlineCodeFixedData2 = new FixedData(outlineCodeFixedMeta2, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Fixed2Data"))));
      m_projectProps = new Props14(m_file, m_inputStreamFactory.getInstance(m_projectDir, "Props"));
      //MPPUtility.fileDump("c:\\temp\\props.txt", m_projectProps.toString().getBytes());

      m_fontBases = new HashMap<>();
      m_taskSubProjects = new HashMap<>();
      m_externalTasks = new HashSet<>();
      m_taskOrder = new TreeMap<>();
      m_nullTaskOrder = new TreeMap<>();
      m_parentTasks = new HashMap<>();

      m_file.getProjectProperties().setMppFileType(Integer.valueOf(14));
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
      m_outlineCodeVarMeta = null;
      m_projectProps = null;
      m_inputStreamFactory = null;
      m_parentTasks = null;
   }

   /**
    * This method extracts and collates the value list information
    * for custom column value lists.
    */
   private void processCustomValueLists() throws IOException
   {
      processCustomValueLists((DirectoryEntry) m_projectDir.getEntry("TBkndTask"));
      processCustomValueLists((DirectoryEntry) m_projectDir.getEntry("TBkndRsc"));
   }

   /**
    * This method extracts and collates the value list information
    * for custom column value lists for a specific entity.
    *
    * @param dir entity directory
    */
   private void processCustomValueLists(DirectoryEntry dir) throws IOException
   {
      if (dir.hasEntry("Props"))
      {
         Props taskProps = new Props14(m_file, m_inputStreamFactory.getInstance(dir, "Props"));

         CustomFieldValueReader14 reader = new CustomFieldValueReader14(m_file, m_outlineCodeVarMeta, m_outlineCodeVarData, m_outlineCodeFixedData, m_outlineCodeFixedData2, taskProps);
         reader.process();
      }
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

      int itemCountOffset = MPPUtility.getInt(subProjData, offset);
      offset += 4;

      while (offset < itemCountOffset)
      {
         index++;
         itemHeaderOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
               //
               // deleted entry?
               //
            case 0x10:
            {
               offset += 8;
               break;
            }

            //
            // task unique ID, 8 bytes, path, file name
            //
            case 0x0b:
            case (byte) 0x99:
            case 0x09:
            case 0x0D:
            {
               uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               // sometimes offset of a task ID?
               offset += 4;

               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, 8 bytes, path, file name
            //
            case 0x03:
            case 0x11:
            case (byte) 0x91:
            {
               uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               // Unknown offset
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, path, unknown, file name
            //
            case (byte) 0x81:
            case (byte) 0x83:
            case 0x41:
            {
               uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               // unknown offset to 2 bytes of data?
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, path, file name
            //
            case 0x01:
            case 0x08:
            {
               uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, path, file name
            //
            case (byte) 0xC0:
            {
               uniqueIDOffset = itemHeaderOffset;

               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               // unknown offset
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // resource, task unique ID, path, file name
            //
            case 0x05:
            {
               uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            case 0x45:
            {
               uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // path, file name
            //
            case 0x02:
            {
               //filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               //fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               //sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset, index);
               // 0x02 looks to be the link FROM the resource pool to a project that is using it.
               break;
            }

            case 0x04:
            {
               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               readSubProject(subProjData, itemHeaderOffset, -1, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, 4 bytes, path, 4 bytes, file name
            //
            case (byte) 0x89:
            case (byte) 0x8D:
            {
               uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
               offset += 8;

               filePathOffset = MPPUtility.getShort(subProjData, offset);
               offset += 8;

               fileNameOffset = MPPUtility.getShort(subProjData, offset);
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            //
            // task unique ID, path, file name
            //
            case 0x0A:
            {
               uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
               offset += 4;

               filePathOffset = MPPUtility.getShort(subProjData, offset);
               offset += 4;

               fileNameOffset = MPPUtility.getShort(subProjData, offset);
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, index);
               break;
            }

            // new resource pool entry
            case (byte) 0x44:
            {
               filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               offset += 4;

               fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
               offset += 4;

               readSubProjects(subProjData, itemHeaderOffset, -1, filePathOffset, fileNameOffset, index);
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
    * Read a list of sub projects.
    *
    * @param data byte array
    * @param itemHeaderOffset header offset
    * @param uniqueIDOffset offset of unique ID
    * @param filePathOffset offset of file path
    * @param fileNameOffset offset of file name
    * @param subprojectIndex index of the subproject, used to calculate unique id offset
    */
   private void readSubProjects(byte[] data, int itemHeaderOffset, int uniqueIDOffset, int filePathOffset, int fileNameOffset, int subprojectIndex)
   {
      while (uniqueIDOffset < filePathOffset)
      {
         readSubProject(data, itemHeaderOffset, uniqueIDOffset, filePathOffset, fileNameOffset, subprojectIndex++);
         uniqueIDOffset += 4;
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
         int type = uniqueIDOffset == -1 ? SUBPROJECT_TASKUNIQUEID0 : MPPUtility.getInt(data, uniqueIDOffset + 4);

         // Generate the unique id offset for this subproject
         //int offset = 0x00800000 + ((subprojectIndex - 1) * 0x00400000);

         if (type == SUBPROJECT_TASKUNIQUEID4)
         {
            sp = MPPUtility.getUnicodeString(data, filePathOffset);
         }
         else
         {
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
            int size = MPPUtility.getInt(data, filePathOffset);
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
               size = MPPUtility.getInt(data, filePathOffset);
               filePathOffset += 4;

               //
               // 2 byte data
               //
               filePathOffset += 2;

               //
               // Unicode string
               //
               sp = MPPUtility.getUnicodeString(data, filePathOffset, size);
            }
         }

         processUniqueIdValues(sp, data, uniqueIDOffset);
      }

      //
      // Admit defeat at this point - we have probably stumbled
      // upon a data format we don't understand, so we'll fail
      // gracefully here. This will now be reported as a missing
      // subproject error by end users of the library, rather
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

      int value = MPPUtility.getInt(data, uniqueIDOffset);
      int type = MPPUtility.getInt(data, uniqueIDOffset + 4);
      Integer taskUniqueID = value == 0 || value > MicrosoftProjectConstants.MAX_UNIQUE_ID ? null : Integer.valueOf(value);
      if (taskUniqueID != null)
      {
         switch (type)
         {
            case SUBPROJECT_TASKUNIQUEID0:
            case SUBPROJECT_TASKUNIQUEID1:
            case SUBPROJECT_TASKUNIQUEID2:
            case SUBPROJECT_TASKUNIQUEID3:
            case SUBPROJECT_TASKUNIQUEID4:
            case SUBPROJECT_TASKUNIQUEID5:
            case SUBPROJECT_TASKUNIQUEID6:
            case SUBPROJECT_TASKUNIQUEID7:
            case SUBPROJECT_TASKUNIQUEID8:
            {
               m_taskSubProjects.put(taskUniqueID, sp);
               break;
            }

            default:
            {
               if (value != 0)
               {
                  m_externalTasks.add(taskUniqueID);
                  m_taskSubProjects.put(taskUniqueID, sp);
               }
               break;
            }
         }
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
         Props props = new Props14(m_file, m_inputStreamFactory.getInstance(m_viewDir, "Props"));
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

      int blockCount = MPPUtility.getShort(data, 0);
      offset += 2;

      int size;
      String name;

      for (int loop = 0; loop < blockCount; loop++)
      {
         /*unknownAttribute = MPPUtility.getShort(data, offset);*/
         offset += 2;

         size = MPPUtility.getShort(data, offset);
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
    * This method maps the task unique identifiers to their index number
    * within the FixedData block.
    *
    * @param fieldMap field map
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @param taskFixed2Data Fixed data for this task
    * @param taskVarData Variable task data
    * @return Mapping between task identifiers and block position
    */
   private TreeMap<Integer, Integer> createTaskMap(FieldMap fieldMap, FixedMeta taskFixedMeta, FixedData taskFixedData, FixedData taskFixed2Data, Var2Data taskVarData)
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
         byte[] data2 = taskFixed2Data.getByteArrayValue(loop);

         if (data != null && data2 != null)
         {
            byte[] metaData = taskFixedMeta.getByteArrayValue(loop);

            //
            // Check for the deleted task flag
            //
            int flags = MPPUtility.getInt(metaData, 0);
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
               uniqueID = MPPUtility.getShort(data, TASK_UNIQUE_ID_FIXED_OFFSET); // Only a short stored for deleted tasks?
               key = Integer.valueOf(uniqueID);
               if (!taskMap.containsKey(key))
               {
                  taskMap.put(key, null); // use null so we can easily ignore this later
               }
            }
            else
            {
               //
               // Do we have a null task?
               //
               if (data.length == NULL_TASK_BLOCK_SIZE)
               {
                  uniqueID = MPPUtility.getInt(data, TASK_UNIQUE_ID_FIXED_OFFSET);
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
                     uniqueID = MPPUtility.getInt(data, uniqueIdOffset);
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
      int maxFixedDataSize = fieldMap.getMaxFixedDataSize(0);
      int uniqueIdOffset = fieldMap.getFixedDataOffset(ResourceField.UNIQUE_ID);

      for (int loop = 0; loop < itemCount; loop++)
      {
         byte[] data = rscFixedData.getByteArrayValue(loop);
         if (data == null || data.length < maxFixedDataSize)
         {
            continue;
         }

         Integer uniqueID = Integer.valueOf(MPPUtility.getShort(data, uniqueIdOffset));
         if (!resourceMap.containsKey(uniqueID))
         {
            resourceMap.put(uniqueID, Integer.valueOf(loop));
         }
      }

      return (resourceMap);
   }

   /**
    * The format of the calendar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    */
   private void processCalendarData() throws IOException
   {
      CalendarFactory factory = new MPP14CalendarFactory(m_file);
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
      FieldMap fieldMap = new FieldMap14(m_file);
      fieldMap.createTaskFieldMap(m_projectProps);

      FieldMap enterpriseCustomFieldMap = new FieldMap14(m_file);
      enterpriseCustomFieldMap.createEnterpriseCustomFieldMap(m_projectProps, FieldTypeClass.TASK);

      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      VarMeta taskVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data(m_file, taskVarMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData(taskFixedMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedData"))), fieldMap.getMaxFixedDataSize(0));
      FixedMeta taskFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Meta"))), taskFixedData, 92, 93, 94, 95, 96);
      FixedData taskFixed2Data = new FixedData(taskFixed2Meta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Data"))));

      //      System.out.println(taskFixedMeta);
      //      System.out.println(taskFixedData);
      //      System.out.println(taskVarMeta);
      //      System.out.println(taskVarData);
      //      System.out.println(taskFixed2Meta);
      //      System.out.println(taskFixed2Data);
      //      System.out.println(m_outlineCodeVarData.getVarMeta());
      //      System.out.println(m_outlineCodeVarData);

      // Process aliases
      if (taskDir.hasEntry("Props"))
      {
         Props14 props = new Props14(m_file, m_inputStreamFactory.getInstance(taskDir, "Props"));
         new CustomFieldReader14(m_file, props.getByteArray(TASK_FIELD_NAME_ALIASES)).process();
      }

      TreeMap<Integer, Integer> taskMap = createTaskMap(fieldMap, taskFixedMeta, taskFixedData, taskFixed2Data, taskVarData);
      // The var data may not contain all the tasks as tasks with no var data assigned will
      // not be saved in there. Most notably these are tasks with no name. So use the task map
      // which contains all the tasks.
      Integer[] uniqueIdArray = taskMap.keySet().toArray(new Integer[0]); //taskVarMeta.getUniqueIdentifierArray();
      Integer offset;
      byte[] data;
      byte[] metaData;
      byte[] metaData2;
      Task task;
      boolean autoWBS = true;
      List<Task> externalTasks = new ArrayList<>();
      RecurringTaskReader recurringTaskReader = null;
      HyperlinkReader hyperlinkReader = new HyperlinkReader();

      //
      // Select the correct meta data locations depending on
      // which version of Microsoft project generated this file
      //
      MppBitFlag[] metaDataBitFlags;
      MppBitFlag[] metaData2BitFlags;
      if (NumberHelper.getInt(m_file.getProjectProperties().getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
      {
         metaDataBitFlags = PROJECT2013_TASK_META_DATA_BIT_FLAGS;
         metaData2BitFlags = PROJECT2013_TASK_META_DATA2_BIT_FLAGS;
      }
      else
      {
         metaDataBitFlags = PROJECT2010_TASK_META_DATA_BIT_FLAGS;
         metaData2BitFlags = PROJECT2010_TASK_META_DATA2_BIT_FLAGS;
      }

      for (Integer uniqueID : uniqueIdArray)
      {
         offset = taskMap.get(uniqueID);
         if (!taskFixedData.isValidOffset(offset))
         {
            continue;
         }

         data = taskFixedData.getByteArrayValue(offset.intValue());
         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
         Integer id = Integer.valueOf(MPPUtility.getInt(data, fieldMap.getFixedDataOffset(TaskField.ID)));

         if (data.length == NULL_TASK_BLOCK_SIZE)
         {
            Integer nullTaskID = Integer.valueOf(MPPUtility.getInt(data, TASK_ID_FIXED_OFFSET));
            if (!m_nullTaskOrder.containsKey(nullTaskID))
            {
               task = m_file.addTask();
               task.setNull(true);
               task.setUniqueID(Integer.valueOf(MPPUtility.getInt(data, TASK_UNIQUE_ID_FIXED_OFFSET)));
               task.setID(nullTaskID);
               m_nullTaskOrder.put(task.getID(), task.getUniqueID());
            }
            continue;
         }

         if (data.length < fieldMap.getMaxFixedDataSize(0))
         {
            byte[] newData = new byte[fieldMap.getMaxFixedDataSize(0) + 8];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
         }

         //System.out.println (ByteArrayHelper.hexdump(data, false, 16, ""));
         //System.out.println (ByteArrayHelper.hexdump(data,false));
         //System.out.println (ByteArrayHelper.hexdump(metaData, false, 16, ""));
         //MPPUtility.dataDump(m_file, data, false, false, false, true, false, false, false, false);
         //MPPUtility.dataDump(metaData, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(taskVarData, id, true, true, true, true, true, true);

         metaData2 = taskFixed2Meta.getByteArrayValue(offset.intValue());
         byte[] data2 = taskFixed2Data.getByteArrayValue(offset.intValue());
         //System.out.println (ByteArrayHelper.hexdump(metaData2, false, 16, ""));
         //System.out.println(ByteArrayHelper.hexdump(data2, false, 16, ""));
         //System.out.println (ByteArrayHelper.hexdump(metaData2,false));

         byte[] recurringData = taskVarData.getByteArray(uniqueID, fieldMap.getVarDataKey(TaskField.RECURRING_DATA));

         Task temp = m_file.getTaskByID(id);
         if (temp != null)
         {
            // Task with this id already exists... determine if this is the 'real' task by seeing
            // if this task has some var data. This is sort of hokey, but it's the best method i have
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
            data,
            data2
         }, taskVarData);

         enterpriseCustomFieldMap.populateContainer(FieldTypeClass.TASK, task, uniqueID, null, taskVarData);

         task.enableEvents();

         task.setEstimated(getDurationEstimated(MPPUtility.getShort(data, fieldMap.getFixedDataOffset(TaskField.ACTUAL_DURATION_UNITS))));

         if (NumberHelper.getInt(task.getSubprojectTaskID()) != 0)
         {
            task.setExternalTask(true);
            externalTasks.add(task);
         }

         hyperlinkReader.read(task, taskVarData.getByteArray(uniqueID, fieldMap.getVarDataKey(TaskField.HYPERLINK_DATA)));

         task.setID(id);

         task.setOutlineCode(1, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE1_INDEX)));
         task.setOutlineCode(2, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE2_INDEX)));
         task.setOutlineCode(3, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE3_INDEX)));
         task.setOutlineCode(4, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE4_INDEX)));
         task.setOutlineCode(5, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE5_INDEX)));
         task.setOutlineCode(6, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE6_INDEX)));
         task.setOutlineCode(7, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE7_INDEX)));
         task.setOutlineCode(8, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE8_INDEX)));
         task.setOutlineCode(9, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE9_INDEX)));
         task.setOutlineCode(10, getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, uniqueID, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE10_INDEX)));

         task.setRecurring(MPPUtility.getShort(data, 40) == 2);

         task.setUniqueID(uniqueID);

         task.setExpanded(((metaData[12] & 0x02) == 0));
         readBitFields(metaDataBitFlags, task, metaData);
         readBitFields(metaData2BitFlags, task, metaData2);

         task.setTaskMode(BooleanHelper.getBoolean((Boolean) task.getCachedValue(TaskField.TASK_MODE)) ? TaskMode.MANUALLY_SCHEDULED : TaskMode.AUTO_SCHEDULED);

         m_parentTasks.put(task.getUniqueID(), (Integer) task.getCachedValue(TaskField.PARENT_TASK_UNIQUE_ID));

         if (task.getStart() == null || (task.getCachedValue(TaskField.SCHEDULED_START) != null && task.getTaskMode() == TaskMode.AUTO_SCHEDULED))
         {
            task.setStart((LocalDateTime) task.getCachedValue(TaskField.SCHEDULED_START));
         }

         if (task.getFinish() == null || (task.getCachedValue(TaskField.SCHEDULED_FINISH) != null && task.getTaskMode() == TaskMode.AUTO_SCHEDULED))
         {
            task.setFinish((LocalDateTime) task.getCachedValue(TaskField.SCHEDULED_FINISH));
         }

         if (task.getDuration() == null || (task.getCachedValue(TaskField.SCHEDULED_DURATION) != null && task.getTaskMode() == TaskMode.AUTO_SCHEDULED))
         {
            task.setDuration((Duration) task.getCachedValue(TaskField.SCHEDULED_DURATION));
         }

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

         if (task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
         {
            // The duration attribute for manual tasks seems to be inaccurate
            // occasionally. The duration as seen in MS Project appears
            // to be the value present in the manual duration attribute
            // so we'll use this instead.
            task.setDuration(task.getManualDuration());
         }
         else
         {
            // If we're not a manually scheduled task, we'll ignore the
            // manual duration attribute.
            task.setManualDuration(null);
         }

         //
         // Process any enterprise columns
         //
         processTaskEnterpriseColumns(task, metaData2);

         // Unfortunately it looks like 'null' tasks sometimes make it through. So let's check for to see if we
         // need to mark this task as a null task after all.
         if (task.getName() == null && ((task.getStart() == null || task.getStart().equals(MPPUtility.EPOCH_DATE)) || (task.getFinish() == null || task.getFinish().equals(MPPUtility.EPOCH_DATE)) || (task.getCreateDate() == null || task.getCreateDate().equals(MPPUtility.EPOCH_DATE))))
         {
            m_file.removeTask(task);
            Integer nullTaskID = Integer.valueOf(MPPUtility.getInt(data, TASK_ID_FIXED_OFFSET));
            if (!m_nullTaskOrder.containsKey(nullTaskID))
            {
               task = m_file.addTask();
               task.setNull(true);
               task.setUniqueID(Integer.valueOf(MPPUtility.getInt(data, TASK_UNIQUE_ID_FIXED_OFFSET)));
               task.setID(nullTaskID);
               m_nullTaskOrder.put(task.getID(), task.getUniqueID());
            }
            continue;
         }

         if (data2 == null || data2.length < 24)
         {
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());
         }
         else
         {
            Long key = Long.valueOf(MPPUtility.getLong(data2, 16));
            m_taskOrder.put(key, task.getUniqueID());
         }

         m_eventManager.fireTaskReadEvent(task);
         //dumpUnknownData(task.getUniqueID().toString(), UNKNOWN_TASK_DATA, data);
         //System.out.println(task);
      }

      //
      // Enable auto WBS if necessary
      //
      m_file.getProjectConfig().setAutoWBS(autoWBS);

      //
      // We have now read all of the task, so we are in a position
      // to perform post-processing to set up the relevant details
      // for each external task.
      //
      if (!externalTasks.isEmpty())
      {
         processExternalTasks(externalTasks);
      }
   }

   /**
    * MPP14 files seem to exhibit some occasional weirdness
    * with duplicate ID values which leads to the task structure
    * being reported incorrectly. The following method attempts to correct this.
    * The method uses ordering data embedded in the file to reconstruct
    * the correct ID order of the tasks.
    */
   private void postProcessTasks() throws MPXJException
   {
      //
      // Renumber ID values using a large increment to allow
      // space for later inserts.
      //
      TreeMap<Integer, Integer> taskMap = new TreeMap<>();
      int nextIDIncrement = ((m_nullTaskOrder.size() / 1000) + 1) * 2000;
      int nextID = (m_file.getTaskByUniqueID(Integer.valueOf(0)) == null ? nextIDIncrement : 0);
      for (Map.Entry<Long, Integer> entry : m_taskOrder.entrySet())
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

   /**
    * Extracts task enterprise column values.
    *
    * @param task task instance
    * @param metaData2 task meta data
    */
   private void processTaskEnterpriseColumns(Task task, byte[] metaData2)
   {
      if (metaData2 != null)
      {
         int bits = MPPUtility.getInt(metaData2, 29);
         task.set(TaskField.ENTERPRISE_FLAG1, Boolean.valueOf((bits & 0x0000800) != 0));
         task.set(TaskField.ENTERPRISE_FLAG2, Boolean.valueOf((bits & 0x0001000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG3, Boolean.valueOf((bits & 0x0002000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG4, Boolean.valueOf((bits & 0x0004000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG5, Boolean.valueOf((bits & 0x0008000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG6, Boolean.valueOf((bits & 0x0001000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG7, Boolean.valueOf((bits & 0x0002000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG8, Boolean.valueOf((bits & 0x0004000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG9, Boolean.valueOf((bits & 0x0008000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG10, Boolean.valueOf((bits & 0x0010000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG11, Boolean.valueOf((bits & 0x0020000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG12, Boolean.valueOf((bits & 0x0040000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG13, Boolean.valueOf((bits & 0x0080000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG14, Boolean.valueOf((bits & 0x0100000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG15, Boolean.valueOf((bits & 0x0200000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG16, Boolean.valueOf((bits & 0x0400000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG17, Boolean.valueOf((bits & 0x0800000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG18, Boolean.valueOf((bits & 0x1000000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG19, Boolean.valueOf((bits & 0x2000000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG20, Boolean.valueOf((bits & 0x4000000) != 0));
      }
   }

   /**
    * Extracts resource enterprise column data.
    *
    * @param resource resource instance
    * @param metaData2 resource meta data
    */
   private void processResourceEnterpriseColumns(Resource resource, byte[] metaData2)
   {
      if (metaData2 != null)
      {
         int bits = MPPUtility.getInt(metaData2, 16);
         resource.set(ResourceField.ENTERPRISE_FLAG1, Boolean.valueOf((bits & 0x00010) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG2, Boolean.valueOf((bits & 0x00020) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG3, Boolean.valueOf((bits & 0x00040) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG4, Boolean.valueOf((bits & 0x00080) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG5, Boolean.valueOf((bits & 0x00100) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG6, Boolean.valueOf((bits & 0x00200) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG7, Boolean.valueOf((bits & 0x00400) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG8, Boolean.valueOf((bits & 0x00800) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG9, Boolean.valueOf((bits & 0x01000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG10, Boolean.valueOf((bits & 0x02000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG11, Boolean.valueOf((bits & 0x04000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG12, Boolean.valueOf((bits & 0x08000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG13, Boolean.valueOf((bits & 0x10000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG14, Boolean.valueOf((bits & 0x20000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG15, Boolean.valueOf((bits & 0x40000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG16, Boolean.valueOf((bits & 0x80000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG17, Boolean.valueOf((bits & 0x100000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG18, Boolean.valueOf((bits & 0x200000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG19, Boolean.valueOf((bits & 0x400000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG20, Boolean.valueOf((bits & 0x800000) != 0));

         bits = MPPUtility.getShort(metaData2, 48);
         resource.set(ResourceField.ENTERPRISE, Boolean.valueOf((bits & 0x20) != 0));
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
      FieldMap fieldMap = new FieldMap14(m_file);
      fieldMap.createResourceFieldMap(m_projectProps);

      FieldMap enterpriseCustomFieldMap = new FieldMap14(m_file);
      enterpriseCustomFieldMap.createEnterpriseCustomFieldMap(m_projectProps, FieldTypeClass.RESOURCE);

      DirectoryEntry rscDir = (DirectoryEntry) m_projectDir.getEntry("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data(m_file, rscVarMeta, new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData(rscFixedMeta, m_inputStreamFactory.getInstance(rscDir, "FixedData"));
      FixedMeta rscFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Fixed2Meta"))), rscFixedData, 50, 51);
      FixedData rscFixed2Data = new FixedData(rscFixed2Meta, m_inputStreamFactory.getInstance(rscDir, "Fixed2Data"));

      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      //System.out.println(rscFixedMeta);
      //System.out.println(rscFixedData);
      //System.out.println(rscFixed2Meta);
      //System.out.println(rscFixed2Data);

      // Process aliases
      if (rscDir.hasEntry("Props"))
      {
         Props14 props = new Props14(m_file, m_inputStreamFactory.getInstance(rscDir, "Props"));
         new CustomFieldReader14(m_file, props.getByteArray(RESOURCE_FIELD_NAME_ALIASES)).process();
      }

      TreeMap<Integer, Integer> resourceMap = createResourceMap(fieldMap, rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifierArray();
      Integer offset;
      byte[] data;
      byte[] metaData;
      Resource resource;
      HyperlinkReader hyperlinkReader = new HyperlinkReader();

      //
      // Select the correct meta data locations depending on
      // which version of Microsoft project generated this file
      //
      MppBitFlag[] metaDataBitFlags;
      MppBitFlag[] metaData2BitFlags;
      int resourceTypeOffset;
      int resourceTypeMask;

      if (NumberHelper.getInt(m_file.getProjectProperties().getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
      {
         resourceTypeOffset = 12;
         resourceTypeMask = 0x10;
         metaDataBitFlags = PROJECT2013_RESOURCE_META_DATA_BIT_FLAGS;
         metaData2BitFlags = PROJECT2013_RESOURCE_META_DATA2_BIT_FLAGS;
      }
      else
      {
         resourceTypeOffset = 9;
         resourceTypeMask = 0x02;
         metaDataBitFlags = PROJECT2010_RESOURCE_META_DATA_BIT_FLAGS;
         metaData2BitFlags = PROJECT2010_RESOURCE_META_DATA2_BIT_FLAGS;
      }

      for (Integer id : uniqueid)
      {
         offset = resourceMap.get(id);
         if (offset == null)
         {
            continue;
         }

         data = rscFixedData.getByteArrayValue(offset.intValue());
         byte[] metaData2 = rscFixed2Meta.getByteArrayValue(offset.intValue());
         byte[] data2 = rscFixed2Data.getByteArrayValue(offset.intValue());
         //metaData = rscFixedMeta.getByteArrayValue(offset.intValue());
         //MPPUtility.dataDump(data, true, true, true, true, true, true, true);
         //MPPUtility.dataDump(metaData, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(rscVarData, id, true, true, true, true, true, true);

         resource = m_file.addResource();

         resource.disableEvents();

         fieldMap.populateContainer(FieldTypeClass.RESOURCE, resource, id, new byte[][]
         {
            data,
            data2
         }, rscVarData);

         enterpriseCustomFieldMap.populateContainer(FieldTypeClass.RESOURCE, resource, id, null, rscVarData);

         resource.enableEvents();

         hyperlinkReader.read(resource, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.HYPERLINK_DATA)));

         resource.setID(Integer.valueOf(MPPUtility.getInt(data, fieldMap.getFixedDataOffset(ResourceField.ID))));

         resource.setOutlineCode(1, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE1_INDEX)));
         resource.setOutlineCode(2, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE2_INDEX)));
         resource.setOutlineCode(3, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE3_INDEX)));
         resource.setOutlineCode(4, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE4_INDEX)));
         resource.setOutlineCode(5, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE5_INDEX)));
         resource.setOutlineCode(6, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE6_INDEX)));
         resource.setOutlineCode(7, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE7_INDEX)));
         resource.setOutlineCode(8, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE8_INDEX)));
         resource.setOutlineCode(9, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE9_INDEX)));
         resource.setOutlineCode(10, getCustomFieldOutlineCodeValue(rscVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE10_INDEX)));

         metaData = rscFixedMeta.getByteArrayValue(offset.intValue());
         readBitFields(metaDataBitFlags, resource, metaData);
         readBitFields(metaData2BitFlags, resource, metaData2);

         resource.setUniqueID(id);

         //
         // Configure the resource calendar
         //
         resource.setCalendar(m_resourceMap.get(id));

         //
         // Process any enterprise columns
         //
         processResourceEnterpriseColumns(resource, metaData2);

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
         if ((metaData[resourceTypeOffset] & resourceTypeMask) != 0)
         {
            resource.setType(ResourceType.WORK);
         }
         else
         {
            if ((metaData2[8] & 0x10) != 0)
            {
               resource.setType(ResourceType.COST);
            }
            else
            {
               resource.setType(ResourceType.MATERIAL);
            }
         }

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * This method extracts and collates resource assignment data.
    */
   private void processAssignmentData() throws IOException
   {
      FieldMap fieldMap = new FieldMap14(m_file);
      fieldMap.createAssignmentFieldMap(m_projectProps);

      FieldMap enterpriseCustomFieldMap = new FieldMap14(m_file);
      enterpriseCustomFieldMap.createEnterpriseCustomFieldMap(m_projectProps, FieldTypeClass.ASSIGNMENT);

      DirectoryEntry assnDir = (DirectoryEntry) m_projectDir.getEntry("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data(m_file, assnVarMeta, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData(110, m_inputStreamFactory.getInstance(assnDir, "FixedData"));
      FixedData assnFixedData2 = new FixedData(48, m_inputStreamFactory.getInstance(assnDir, "Fixed2Data"));
      //FixedMeta assnFixedMeta2 = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Fixed2Meta"))), 53);

      // Process aliases
      if (assnDir.hasEntry("Props"))
      {
         Props props = new Props14(m_file, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Props"))));
         new CustomFieldReader14(m_file, props.getByteArray(ASSIGNMENT_FIELD_NAME_ALIASES)).process();
      }

      ResourceAssignmentFactory factory = new ResourceAssignmentFactory();
      factory.process(m_file, fieldMap, enterpriseCustomFieldMap, m_reader.getUseRawTimephasedData(), assnVarMeta, assnVarData, assnFixedMeta, assnFixedData, assnFixedData2, assnFixedMeta.getItemCount());
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
         VarMeta viewVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data viewVarData = new Var2Data(m_file, viewVarMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
         FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
         FixedData fixedData = new FixedData(138, m_inputStreamFactory.getInstance(dir, "FixedData"));

         int items = fixedMeta.getAdjustedItemCount();
         View view;
         ViewFactory factory = new ViewFactory14();

         int lastOffset = -1;
         for (int loop = 0; loop < items; loop++)
         {
            byte[] fm = fixedMeta.getByteArrayValue(loop);
            int offset = MPPUtility.getShort(fm, 4);
            if (offset > lastOffset)
            {
               byte[] fd = fixedData.getByteArrayValue(fixedData.getIndexFromOffset(offset));
               if (fd != null)
               {
                  view = factory.createView(m_file, fm, fd, viewVarData, m_fontBases);
                  m_file.getViews().add(view);
               }
               lastOffset = offset;
            }
         }
      }
   }

   /**
    * This method extracts table data from the MPP file.
    */
   private void processTableData() throws IOException
   {
      if (m_viewDir.hasEntry("CTable"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CTable");

         VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
         FixedData fixedData = new FixedData(230, new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedData"))));
         //System.out.println(varMeta);
         //System.out.println(varData);
         //System.out.println(fixedData);

         TableContainer container = m_file.getTables();
         TableFactory14 factory = new TableFactory14(TABLE_COLUMN_DATA_STANDARD, TABLE_COLUMN_DATA_ENTERPRISE, TABLE_COLUMN_DATA_BASELINE);
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
    */
   private void processFilterData() throws IOException
   {
      if (m_viewDir.hasEntry("CFilter"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CFilter");

         FixedMeta fixedMeta;
         FixedData fixedData;
         VarMeta varMeta;
         Var2Data varData;

         try
         {
            fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
            fixedData = new FixedData(fixedMeta, m_inputStreamFactory.getInstance(dir, "FixedData"));
            varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
            varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
         }

         // NoSuchElementException, IndexOutOfBoundsException: From a sample files where the stream reports an available number of bytes,
         // but attempting to read that number of bytes raises an exception.
         // IOException: I've come across an unusual sample where the VarMeta magic number is zero, which throws this exception.
         // MS Project opens the file fine. If we get into this state, we'll just ignore the filter definitions.
         catch (NoSuchElementException | IndexOutOfBoundsException | IOException ex)
         {
            m_file.addIgnoredError(ex);
            return;
         }

         //System.out.println(fixedMeta);
         //System.out.println(fixedData);
         //System.out.println(varMeta);
         //System.out.println(varData);

         FilterReader reader = new FilterReader14();
         reader.process(m_file, fixedData, varData);
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
         VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
         //System.out.println(varMeta);
         //System.out.println(varData);

         InputStream is = new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedData")));
         byte[] fixedData = InputStreamHelper.read(is, is.available());
         is.close();
         //System.out.println(ByteArrayHelper.hexdump(fixedData, false, 16, ""));

         ViewStateReader reader = new ViewStateReader12();
         reader.process(m_file, varData, fixedData);
      }
   }

   /**
    * Read group definitions.
    */
   private void processGroupData() throws IOException
   {
      if (m_viewDir.hasEntry("CGrouping"))
      {
         DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CGrouping");
         FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
         FixedData fixedData = new FixedData(fixedMeta, m_inputStreamFactory.getInstance(dir, "FixedData"));
         VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

         //System.out.println(fixedMeta);
         //System.out.println(fixedData);
         //System.out.println(varMeta);
         //System.out.println(varData);

         GroupReader14 reader = new GroupReader14();
         reader.process(m_file, fixedData, varData, m_fontBases);
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
         FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 11);
         FixedData fixedData = new FixedData(fixedMeta, m_inputStreamFactory.getInstance(dir, "FixedData"));
         VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
         Var2Data varData = new Var2Data(m_file, varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

         DataLinkFactory factory = new DataLinkFactory(m_file, fixedData, varData);
         factory.process();
      }
   }

   /**
    * Retrieve custom field value.
    *
    * @param varData var data block
    * @param outlineCodeVarData var data block
    * @param id item ID
    * @param type item type
    * @return item value
    */
   private String getCustomFieldOutlineCodeValue(Var2Data varData, Var2Data outlineCodeVarData, Integer id, Integer type)
   {
      String result = null;

      int mask = varData.getShort(id, type);
      if ((mask & 0xFF00) != VALUE_LIST_MASK)
      {
         result = outlineCodeVarData.getUnicodeString(Integer.valueOf(varData.getInt(id, 2, type)), OUTLINECODE_DATA);
      }
      else
      {
         int uniqueId = varData.getInt(id, 2, type);
         CustomFieldValueItem item = m_file.getCustomFields().getCustomFieldValueItemByUniqueID(uniqueId);
         if (item != null)
         {
            Object value = item.getValue();
            if (value instanceof String)
            {
               result = (String) value;
            }

            String result2 = getCustomFieldOutlineCodeValue(varData, outlineCodeVarData, item.getParent());
            if (result != null && result2 != null && !result2.isEmpty())
            {
               result = result2 + "." + result;
            }
         }
      }
      return result;
   }

   /**
    * Retrieve custom field value.
    *
    * @param varData var data block
    * @param outlineCodeVarData var data block
    * @param id parent item ID
    * @return item value
    */
   private String getCustomFieldOutlineCodeValue(Var2Data varData, Var2Data outlineCodeVarData, Integer id)
   {
      String result = null;

      int uniqueId = id.intValue();
      if (uniqueId == 0)
      {
         return "";
      }

      CustomFieldValueItem item = m_file.getCustomFields().getCustomFieldValueItemByUniqueID(uniqueId);
      if (item != null)
      {
         Object value = item.getValue();
         if (value instanceof String)
         {
            result = (String) value;
         }

         if (result != null && !NumberHelper.equals(id, item.getParent()))
         {
            String result2 = getCustomFieldOutlineCodeValue(varData, outlineCodeVarData, item.getParent());
            if (result2 != null && !result2.isEmpty())
            {
               result = result2 + "." + result;
            }
         }
      }

      return result;
   }

   /**
    * Iterate through a set of bit field flags and set the value for each one
    * in the supplied container.
    *
    * @param flags bit field flags
    * @param container field container
    * @param data source data
    */
   private void readBitFields(MppBitFlag[] flags, FieldContainer container, byte[] data)
   {
      for (MppBitFlag flag : flags)
      {
         flag.setValue(container, data);
      }
   }

   private MPPReader m_reader;
   private ProjectFile m_file;
   private EventManager m_eventManager;
   private DirectoryEntry m_root;
   private HashMap<Integer, ProjectCalendar> m_resourceMap;
   private Var2Data m_outlineCodeVarData;
   private VarMeta m_outlineCodeVarMeta;
   private FixedData m_outlineCodeFixedData;
   private FixedData m_outlineCodeFixedData2;
   private Props m_projectProps;
   private Map<Integer, FontBase> m_fontBases;
   private Map<Integer, String> m_taskSubProjects;
   private Set<Integer> m_externalTasks;
   private DirectoryEntry m_projectDir;
   private DirectoryEntry m_viewDir;
   private Map<Long, Integer> m_taskOrder;
   private Map<Integer, Integer> m_nullTaskOrder;
   private DocumentInputStreamFactory m_inputStreamFactory;
   private Map<Integer, Integer> m_parentTasks;

   // Signals the end of the list of subproject task unique ids
   //private static final int SUBPROJECT_LISTEND = 0x00000303;

   // Signals that the previous value was for the subproject task unique id
   private static final int SUBPROJECT_TASKUNIQUEID0 = 0x00000000;
   private static final int SUBPROJECT_TASKUNIQUEID1 = 0x0B340000;
   private static final int SUBPROJECT_TASKUNIQUEID2 = 0x0ABB0000;
   private static final int SUBPROJECT_TASKUNIQUEID3 = 0x05A10000;
   private static final int SUBPROJECT_TASKUNIQUEID4 = 0x0BD50000;
   private static final int SUBPROJECT_TASKUNIQUEID5 = 0x03D60000;
   private static final int SUBPROJECT_TASKUNIQUEID6 = 0x067F0000;
   private static final int SUBPROJECT_TASKUNIQUEID7 = 0x067D0000;
   private static final int SUBPROJECT_TASKUNIQUEID8 = 0x00540000;

   /**
    * Resource data types.
    */
   private static final Integer TABLE_COLUMN_DATA_STANDARD = Integer.valueOf(6);
   private static final Integer TABLE_COLUMN_DATA_ENTERPRISE = Integer.valueOf(7);
   private static final Integer TABLE_COLUMN_DATA_BASELINE = Integer.valueOf(8);
   private static final Integer OUTLINECODE_DATA = Integer.valueOf(22);

   /**
    * Custom value list types.
    */
   private static final int VALUE_LIST_MASK = 0x0700;

   /**
    * Mask used to isolate confirmed flag from the duration units field.
    */
   private static final int DURATION_CONFIRMED_MASK = 0x20;

   private static final Integer RESOURCE_FIELD_NAME_ALIASES = Integer.valueOf(71303169);
   private static final Integer TASK_FIELD_NAME_ALIASES = Integer.valueOf(71303169);
   private static final Integer ASSIGNMENT_FIELD_NAME_ALIASES = Integer.valueOf(71303169);

   /**
    * Deleted and null tasks have their ID and UniqueID attributes at fixed offsets.
    */
   private static final int TASK_UNIQUE_ID_FIXED_OFFSET = 0;
   private static final int TASK_ID_FIXED_OFFSET = 4;
   private static final int NULL_TASK_BLOCK_SIZE = 16;

   private static final MppBitFlag[] PROJECT2010_TASK_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(TaskField.FLAG1, 35, 0x0000040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG2, 35, 0x0000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG3, 35, 0x0000100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG4, 35, 0x0000200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG5, 35, 0x0000400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG6, 35, 0x0000800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG7, 35, 0x0001000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG8, 35, 0x0002000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG9, 35, 0x0004000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG10, 35, 0x0008000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG11, 35, 0x0010000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG12, 35, 0x0020000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG13, 35, 0x0040000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG14, 35, 0x0080000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG15, 35, 0x0100000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG16, 35, 0x0200000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG17, 35, 0x0400000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG18, 35, 0x0800000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG19, 35, 0x1000000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG20, 35, 0x2000000, Boolean.FALSE, Boolean.TRUE),

      new MppBitFlag(TaskField.MILESTONE, 8, 0x20, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.MARKED, 9, 0x40, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.IGNORE_RESOURCE_CALENDAR, 10, 0x02, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.ROLLUP, 10, 0x08, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.HIDE_BAR, 10, 0x80, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.EFFORT_DRIVEN, 11, 0x10, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.LEVEL_ASSIGNMENTS, 13, 0x04, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.LEVELING_CAN_SPLIT, 13, 0x02, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] PROJECT2013_TASK_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(TaskField.FLAG1, 24, 0x0002, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG2, 24, 0x0004, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG3, 24, 0x0008, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG4, 24, 0x0010, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG5, 24, 0x0020, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG6, 24, 0x0040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG7, 24, 0x0080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG8, 24, 0x0100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG9, 24, 0x0200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG10, 24, 0x0400, Boolean.FALSE, Boolean.TRUE),

      new MppBitFlag(TaskField.FLAG11, 33, 0x002, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG12, 33, 0x004, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG13, 33, 0x008, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG14, 33, 0x010, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG15, 33, 0x020, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG16, 33, 0x040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG17, 33, 0x080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG18, 33, 0x100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG19, 33, 0x200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.FLAG20, 33, 0x400, Boolean.FALSE, Boolean.TRUE),

      new MppBitFlag(TaskField.MILESTONE, 10, 0x02, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.MARKED, 12, 0x02, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.ROLLUP, 12, 0x04, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.HIDE_BAR, 12, 0x80, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.EFFORT_DRIVEN, 13, 0x08, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.LEVEL_ASSIGNMENTS, 16, 0x04, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.LEVELING_CAN_SPLIT, 16, 0x02, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.IGNORE_RESOURCE_CALENDAR, 17, 0x20, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] PROJECT2010_TASK_META_DATA2_BIT_FLAGS =
   {
      new MppBitFlag(TaskField.ACTIVE, 8, 0x04, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.TASK_MODE, 8, 0x08, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] PROJECT2013_TASK_META_DATA2_BIT_FLAGS =
   {
      new MppBitFlag(TaskField.ACTIVE, 8, 0x40, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(TaskField.TASK_MODE, 8, 0x80, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] PROJECT2010_RESOURCE_META_DATA2_BIT_FLAGS =
   {
      new MppBitFlag(ResourceField.BUDGET, 8, 0x20, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.TYPE, 8, 0x10, ResourceType.MATERIAL, ResourceType.COST),
      new MppBitFlag(ResourceField.GENERIC, 32, 0x04000000, Boolean.FALSE, Boolean.TRUE),
   };

   private static final MppBitFlag[] PROJECT2013_RESOURCE_META_DATA2_BIT_FLAGS =
   {
      new MppBitFlag(ResourceField.BUDGET, 8, 0x20, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.TYPE, 8, 0x10, ResourceType.MATERIAL, ResourceType.COST),
      new MppBitFlag(ResourceField.GENERIC, 32, 0x10000000, Boolean.FALSE, Boolean.TRUE),
   };

   private static final MppBitFlag[] PROJECT2010_RESOURCE_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(ResourceField.FLAG10, 28, 0x0000020, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG1, 28, 0x0000040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG2, 28, 0x0000080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG3, 28, 0x0000100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG4, 28, 0x0000200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG5, 28, 0x0000400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG6, 28, 0x0000800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG7, 28, 0x0001000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG8, 28, 0x0002000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG9, 28, 0x0004000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG11, 28, 0x0008000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG12, 28, 0x0010000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG13, 28, 0x0020000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG14, 28, 0x0040000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG15, 28, 0x0080000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG16, 28, 0x0100000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG17, 28, 0x0200000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG18, 28, 0x0400000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG19, 28, 0x0800000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG20, 28, 0x1000000, Boolean.FALSE, Boolean.TRUE)
   };

   private static final MppBitFlag[] PROJECT2013_RESOURCE_META_DATA_BIT_FLAGS =
   {
      new MppBitFlag(ResourceField.FLAG10, 19, 0x0040, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG1, 19, 0x0080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG2, 19, 0x0100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG3, 19, 0x0200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG4, 19, 0x0400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG5, 19, 0x0800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG6, 19, 0x1000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG7, 19, 0x2000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG8, 19, 0x4000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG9, 19, 0x8000, Boolean.FALSE, Boolean.TRUE),

      new MppBitFlag(ResourceField.FLAG11, 24, 0x00080, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG12, 24, 0x00100, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG13, 24, 0x00200, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG14, 24, 0x00400, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG15, 24, 0x00800, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG16, 24, 0x01000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG17, 24, 0x02000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG18, 24, 0x04000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG19, 24, 0x08000, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.FLAG20, 24, 0x10000, Boolean.FALSE, Boolean.TRUE)
   };
}
