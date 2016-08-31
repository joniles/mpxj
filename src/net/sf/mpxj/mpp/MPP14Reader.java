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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Table;
import net.sf.mpxj.TableContainer;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.View;
import net.sf.mpxj.WorkGroup;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.Pair;
import net.sf.mpxj.common.RtfHelper;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

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
   private void populateMemberData(MPPReader reader, ProjectFile file, DirectoryEntry root) throws FileNotFoundException, IOException, MPXJException
   {
      m_reader = reader;
      m_file = file;
      m_eventManager = file.getEventManager();
      m_root = root;

      //
      // Retrieve the high level document properties
      //
      Props14 props14 = new Props14(new DocumentInputStream(((DocumentEntry) root.getEntry("Props14"))));
      //System.out.println(props14);

      file.getProjectProperties().setProjectFilePath(props14.getUnicodeString(Props.PROJECT_FILE_PATH));
      m_inputStreamFactory = new DocumentInputStreamFactory(props14);

      //
      // Test for password protection. In the single byte retrieved here:
      //
      // 0x00 = no password
      // 0x01 = protection password has been supplied
      // 0x02 = write reservation password has been supplied
      // 0x03 = both passwords have been supplied
      //
      if ((props14.getByte(Props.PASSWORD_FLAG) & 0x01) != 0)
      {
         // Couldn't figure out how to get the password for MPP14 files so for now we just need to block the reading
         throw new MPXJException(MPXJException.PASSWORD_PROTECTED);
      }

      m_resourceMap = new HashMap<Integer, ProjectCalendar>();
      m_projectDir = (DirectoryEntry) root.getEntry("   114");
      m_viewDir = (DirectoryEntry) root.getEntry("   214");
      DirectoryEntry outlineCodeDir = (DirectoryEntry) m_projectDir.getEntry("TBkndOutlCode");
      m_outlineCodeVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("VarMeta"))));
      m_outlineCodeVarData = new Var2Data(m_outlineCodeVarMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Var2Data"))));
      m_outlineCodeFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("FixedMeta"))), 10);
      m_outlineCodeFixedData = new FixedData(m_outlineCodeFixedMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("FixedData"))));
      m_outlineCodeFixedMeta2 = new FixedMeta(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Fixed2Meta"))), 10);
      m_outlineCodeFixedData2 = new FixedData(m_outlineCodeFixedMeta2, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Fixed2Data"))));
      m_projectProps = new Props14(m_inputStreamFactory.getInstance(m_projectDir, "Props"));
      //MPPUtility.fileDump("c:\\temp\\props.txt", m_projectProps.toString().getBytes());
      //FieldMap fm = new FieldMap14(m_file);
      //fm.dumpKnownFieldMaps(m_projectProps);

      m_fontBases = new HashMap<Integer, FontBase>();
      m_taskSubProjects = new HashMap<Integer, SubProject>();
      m_parentTasks = new HashMap<Integer, Integer>();
      m_taskOrder = new TreeMap<Long, Integer>();
      m_nullTaskOrder = new TreeMap<Integer, Integer>();

      m_file.getProjectProperties().setMppFileType(Integer.valueOf(14));
      m_file.getProjectProperties().setAutoFilter(props14.getBoolean(Props.AUTO_FILTER));
   }

   /**
    * Clear transient member data.
    */
   private void clearMemberData()
   {
      m_reader = null;
      m_eventManager = null;
      m_file = null;
      m_root = null;
      m_resourceMap = null;
      m_projectDir = null;
      m_viewDir = null;
      m_outlineCodeVarData = null;
      m_outlineCodeVarMeta = null;
      m_projectProps = null;
      m_fontBases = null;
      m_taskSubProjects = null;
      m_parentTasks = null;
      m_taskOrder = null;
      m_nullTaskOrder = null;
   }

   /**
    * This method extracts and collates the value list information
    * for custom column value lists.
    * @throws IOException
    */
   private void processCustomValueLists() throws IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      Props taskProps = new Props14(m_inputStreamFactory.getInstance(taskDir, "Props"));

      CustomFieldValueReader14 reader = new CustomFieldValueReader14(m_file.getProjectProperties(), m_file.getCustomFields(), m_outlineCodeVarMeta, m_outlineCodeVarData, m_outlineCodeFixedData, m_outlineCodeFixedData2, taskProps);
      reader.process();
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
      graphicalIndicatorReader.process(m_file.getCustomFields(), m_file.getProjectProperties(), m_projectProps);
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

      //System.out.println (MPPUtility.hexdump(subProjData, true, 16, ""));
      //MPPUtility.fileHexDump("c:\\temp\\dump.txt", subProjData);

      if (subProjData != null)
      {
         int index = 0;
         int offset = 0;
         int itemHeaderOffset;
         int uniqueIDOffset;
         int filePathOffset;
         int fileNameOffset;

         byte[] itemHeader = new byte[20];

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

            MPPUtility.getByteArray(subProjData, itemHeaderOffset, itemHeader.length, itemHeader, 0);
            byte subProjectType = itemHeader[16];

            //            System.out.println();
            //            System.out.println (MPPUtility.hexdump(itemHeader, false, 16, ""));
            //            System.out.println(MPPUtility.hexdump(subProjData, offset, 16, false));
            //            System.out.println("Offset1: " + (MPPUtility.getInt(subProjData, offset) & 0x1FFFF));
            //            System.out.println("Offset2: " + (MPPUtility.getInt(subProjData, offset+4) & 0x1FFFF));
            //            System.out.println("Offset3: " + (MPPUtility.getInt(subProjData, offset+8) & 0x1FFFF));
            //            System.out.println("Offset4: " + (MPPUtility.getInt(subProjData, offset+12) & 0x1FFFF));
            //            System.out.println ("Offset: " + offset);
            //            System.out.println ("Item Header Offset: " + itemHeaderOffset);
            //            System.out.println("SubProjectType: " + Integer.toHexString(subProjectType));
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

                  readSubProjects(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
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

                  readSubProjects(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
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

                  readSubProjects(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
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

                  readSubProjects(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
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

                  readSubProjects(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
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

                  m_file.getSubProjects().setResourceSubProject(readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index));
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

                  m_file.getSubProjects().setResourceSubProject(readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index));
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

                  m_file.getSubProjects().setResourceSubProject(readSubProject(subProjData, -1, filePathOffset, fileNameOffset, index));
                  break;
               }

               //
               // task unique ID, 4 bytes, path, 4 bytes, file name
               //
               case (byte) 0x8D:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 8;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 8;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  readSubProjects(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
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

                  readSubProjects(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
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

                  readSubProjects(subProjData, -1, filePathOffset, fileNameOffset, index);
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
   }

   /**
    * Read a list of sub projects.
    *
    * @param data byte array
    * @param uniqueIDOffset offset of unique ID
    * @param filePathOffset offset of file path
    * @param fileNameOffset offset of file name
    * @param subprojectIndex index of the subproject, used to calculate unique id offset
    */
   private void readSubProjects(byte[] data, int uniqueIDOffset, int filePathOffset, int fileNameOffset, int subprojectIndex)
   {
      while (uniqueIDOffset < filePathOffset)
      {
         readSubProject(data, uniqueIDOffset, filePathOffset, fileNameOffset, subprojectIndex++);
         uniqueIDOffset += 4;
      }
   }

   /**
    * Method used to read the sub project details from a byte array.
    *
    * @param data byte array
    * @param uniqueIDOffset offset of unique ID
    * @param filePathOffset offset of file path
    * @param fileNameOffset offset of file name
    * @param subprojectIndex index of the subproject, used to calculate unique id offset
    * @return new SubProject instance
    */
   private SubProject readSubProject(byte[] data, int uniqueIDOffset, int filePathOffset, int fileNameOffset, int subprojectIndex)
   {
      try
      {
         SubProject sp = new SubProject();
         int type = SUBPROJECT_TASKUNIQUEID0;

         if (uniqueIDOffset != -1)
         {
            int value = MPPUtility.getInt(data, uniqueIDOffset);
            type = MPPUtility.getInt(data, uniqueIDOffset + 4);
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
                  sp.setTaskUniqueID(Integer.valueOf(value));
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }

               default:
               {
                  if (value != 0)
                  {
                     sp.addExternalTaskUniqueID(Integer.valueOf(value));
                     m_taskSubProjects.put(Integer.valueOf(value), sp);
                  }
                  break;
               }
            }

            // Now get the unique id offset for this subproject
            value = 0x00800000 + ((subprojectIndex - 1) * 0x00400000);
            sp.setUniqueIDOffset(Integer.valueOf(value));
         }

         if (type == SUBPROJECT_TASKUNIQUEID4)
         {
            sp.setFullPath(MPPUtility.getUnicodeString(data, filePathOffset));
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
            sp.setDosFullPath(MPPUtility.getString(data, filePathOffset));
            filePathOffset += (sp.getDosFullPath().length() + 1);

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
               sp.setFullPath(sp.getDosFullPath());
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
               sp.setFullPath(MPPUtility.getUnicodeString(data, filePathOffset, size));
               //filePathOffset += size;
            }

            //
            // Second block header
            //
            fileNameOffset += 18;

            //
            // String size as a 4 byte int
            //
            fileNameOffset += 4;

            //
            // DOS file name
            //
            sp.setDosFileName(MPPUtility.getString(data, fileNameOffset));
            fileNameOffset += (sp.getDosFileName().length() + 1);

            //
            // 24 byte block
            //
            fileNameOffset += 24;

            //
            // 4 byte block size
            //
            size = MPPUtility.getInt(data, fileNameOffset);
            fileNameOffset += 4;

            if (size == 0)
            {
               sp.setFileName(sp.getDosFileName());
            }
            else
            {
               //
               // 4 byte unicode string size in bytes
               //
               size = MPPUtility.getInt(data, fileNameOffset);
               fileNameOffset += 4;

               //
               // 2 byte data
               //
               fileNameOffset += 2;

               //
               // Unicode string
               //
               sp.setFileName(MPPUtility.getUnicodeString(data, fileNameOffset, size));
               //fileNameOffset += size;
            }
         }

         //System.out.println(sp.toString());

         // Add to the list of subprojects
         m_file.getSubProjects().add(sp);

         return (sp);
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
         return (null);
      }
   }

   /**
    * This method process the data held in the props file specific to the
    * visual appearance of the project data.
    */
   private void processViewPropertyData() throws IOException
   {
      Props14 props = new Props14(m_inputStreamFactory.getInstance(m_viewDir, "Props"));
      byte[] data = props.getByteArray(Props.FONT_BASES);
      if (data != null)
      {
         processBaseFonts(data);
      }

      ProjectProperties properties = m_file.getProjectProperties();
      properties.setShowProjectSummaryTask(props.getBoolean(Props.SHOW_PROJECT_SUMMARY_TASK));
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

         if (name.length() != 0)
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
    * @return Mapping between task identifiers and block position
    */
   private TreeMap<Integer, Integer> createTaskMap(FieldMap fieldMap, FixedMeta taskFixedMeta, FixedData taskFixedData)
   {
      TreeMap<Integer, Integer> taskMap = new TreeMap<Integer, Integer>();
      int uniqueIdOffset = fieldMap.getFixedDataOffset(TaskField.UNIQUE_ID);
      int itemCount = taskFixedMeta.getAdjustedItemCount();
      int uniqueID;
      Integer key;

      //
      // First three items are not tasks, so let's skip them
      //
      for (int loop = 3; loop < itemCount; loop++)
      {
         byte[] data = taskFixedData.getByteArrayValue(loop);
         if (data != null)
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
               if (taskMap.containsKey(key) == false)
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
                  if (taskMap.containsKey(key) == false)
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
                     if (taskMap.containsKey(key) == false)
                     {
                        taskMap.put(key, Integer.valueOf(loop));
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
      TreeMap<Integer, Integer> resourceMap = new TreeMap<Integer, Integer>();
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
         if (resourceMap.containsKey(uniqueID) == false)
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
      DirectoryEntry calDir = (DirectoryEntry) m_projectDir.getEntry("TBkndCal");

      //MPPUtility.fileHexDump("c:\\temp\\varmeta.txt", new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));

      VarMeta calVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) calDir.getEntry("VarMeta"))));
      Var2Data calVarData = new Var2Data(calVarMeta, new DocumentInputStream((DocumentEntry) calDir.getEntry("Var2Data")));

      //System.out.println(calVarMeta);
      //System.out.println(calVarData);

      FixedMeta calFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("FixedMeta"))), 10);
      FixedData calFixedData = new FixedData(calFixedMeta, m_inputStreamFactory.getInstance(calDir, "FixedData"), 12);

      //System.out.println (calFixedMeta);
      //System.out.println (calFixedData);

      //FixedMeta calFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("Fixed2Meta"))), 9);
      //FixedData calFixed2Data = new FixedData(calFixed2Meta, getEncryptableInputStream(calDir, "Fixed2Data"), 48);
      //System.out.println (calFixed2Meta);
      //System.out.println (calFixed2Data);

      HashMap<Integer, ProjectCalendar> calendarMap = new HashMap<Integer, ProjectCalendar>();
      int items = calFixedData.getItemCount();
      List<Pair<ProjectCalendar, Integer>> baseCalendars = new LinkedList<Pair<ProjectCalendar, Integer>>();
      byte[] defaultCalendarData = m_projectProps.getByteArray(Props.DEFAULT_CALENDAR_HOURS);
      ProjectCalendar defaultCalendar = new ProjectCalendar(m_file);
      processCalendarHours(defaultCalendarData, null, defaultCalendar, true);

      int calendarIDOffset;
      int baseIDOffset;
      int resourceIDOffset;

      // ID offsets appear to be different for 2013 files
      if (NumberHelper.getInt(m_file.getProjectProperties().getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
      {
         calendarIDOffset = 8;
         baseIDOffset = 0;
         resourceIDOffset = 4;
      }
      else
      {
         calendarIDOffset = 0;
         baseIDOffset = 4;
         resourceIDOffset = 8;
      }

      for (int loop = 0; loop < items; loop++)
      {
         byte[] fixedData = calFixedData.getByteArrayValue(loop);
         if (fixedData != null && fixedData.length >= 8)
         {
            int offset = 0;

            //
            // Bug 890909, here we ensure that we have a complete 12 byte
            // block before attempting to process the data.
            //
            while (offset + 12 <= fixedData.length)
            {
               Integer calendarID = Integer.valueOf(MPPUtility.getInt(fixedData, offset + calendarIDOffset));
               int baseCalendarID = MPPUtility.getInt(fixedData, offset + baseIDOffset);

               if (calendarID.intValue() > 0 && calendarMap.containsKey(calendarID) == false)
               {
                  byte[] varData = calVarData.getByteArray(calendarID, CALENDAR_DATA);
                  ProjectCalendar cal;

                  if (baseCalendarID == 0 || baseCalendarID == -1 || baseCalendarID == calendarID.intValue())
                  {
                     if (varData != null || defaultCalendarData != null)
                     {
                        cal = m_file.addCalendar();
                        if (varData == null)
                        {
                           varData = defaultCalendarData;
                        }
                     }
                     else
                     {
                        cal = m_file.addDefaultBaseCalendar();
                     }

                     cal.setName(calVarData.getUnicodeString(calendarID, CALENDAR_NAME));
                  }
                  else
                  {
                     if (varData != null)
                     {
                        cal = m_file.addCalendar();
                     }
                     else
                     {
                        cal = m_file.addDefaultDerivedCalendar();
                     }

                     baseCalendars.add(new Pair<ProjectCalendar, Integer>(cal, Integer.valueOf(baseCalendarID)));
                     Integer resourceID = Integer.valueOf(MPPUtility.getInt(fixedData, offset + resourceIDOffset));
                     m_resourceMap.put(resourceID, cal);
                  }

                  cal.setUniqueID(calendarID);

                  if (varData != null)
                  {
                     processCalendarHours(varData, defaultCalendar, cal, baseCalendarID == -1);
                     processCalendarExceptions(varData, cal);
                  }

                  calendarMap.put(calendarID, cal);
                  m_eventManager.fireCalendarReadEvent(cal);
               }

               offset += 12;
            }
         }
      }

      updateBaseCalendarNames(baseCalendars, calendarMap);
   }

   /**
    * For a given set of calendar data, this method sets the working
    * day status for each day, and if present, sets the hours for that
    * day.
    *
    * NOTE: MPP14 defines the concept of working weeks. MPXJ does not
    * currently support this, and thus we only read the working hours
    * for the default working week.
    *
    * @param data calendar data block
    * @param defaultCalendar calendar to use for default values
    * @param cal calendar instance
    * @param isBaseCalendar true if this is a base calendar
    */
   private void processCalendarHours(byte[] data, ProjectCalendar defaultCalendar, ProjectCalendar cal, boolean isBaseCalendar)
   {
      // Dump out the calendar related data and fields.
      //MPPUtility.dataDump(data, true, false, false, false, true, false, true);

      int offset;
      ProjectCalendarHours hours;
      int periodIndex;
      int index;
      int defaultFlag;
      int periodCount;
      Date start;
      long duration;
      Day day;
      List<DateRange> dateRanges = new ArrayList<DateRange>(5);

      for (index = 0; index < 7; index++)
      {
         offset = (60 * index);
         defaultFlag = data == null ? 1 : MPPUtility.getShort(data, offset);
         day = Day.getInstance(index + 1);

         if (defaultFlag == 1)
         {
            if (isBaseCalendar)
            {
               if (defaultCalendar == null)
               {
                  cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
                  if (cal.isWorkingDay(day))
                  {
                     hours = cal.addCalendarHours(Day.getInstance(index + 1));
                     hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
                     hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
                  }
               }
               else
               {
                  boolean workingDay = defaultCalendar.isWorkingDay(day);
                  cal.setWorkingDay(day, workingDay);
                  if (workingDay)
                  {
                     hours = cal.addCalendarHours(Day.getInstance(index + 1));
                     for (DateRange range : defaultCalendar.getHours(day))
                     {
                        hours.addRange(range);
                     }
                  }
               }
            }
            else
            {
               cal.setWorkingDay(day, DayType.DEFAULT);
            }
         }
         else
         {
            dateRanges.clear();

            periodIndex = 0;
            periodCount = MPPUtility.getShort(data, offset + 2);
            while (periodIndex < periodCount)
            {
               int startOffset = offset + 8 + (periodIndex * 2);
               start = MPPUtility.getTime(data, startOffset);
               int durationOffset = offset + 20 + (periodIndex * 4);
               duration = MPPUtility.getDuration(data, durationOffset);
               Date end = new Date(start.getTime() + duration);
               dateRanges.add(new DateRange(start, end));
               ++periodIndex;
            }

            if (dateRanges.isEmpty())
            {
               cal.setWorkingDay(day, false);
            }
            else
            {
               cal.setWorkingDay(day, true);
               hours = cal.addCalendarHours(Day.getInstance(index + 1));

               for (DateRange range : dateRanges)
               {
                  hours.addRange(range);
               }
            }
         }
      }
   }

   /**
    * This method extracts any exceptions associated with a calendar.
    *
    * @param data calendar data block
    * @param cal calendar instance
    */
   private void processCalendarExceptions(byte[] data, ProjectCalendar cal)
   {
      //
      // Handle any exceptions
      //
      if (data.length > 420)
      {
         int offset = 420; // The first 420 is for the working hours data

         int exceptionCount = MPPUtility.getShort(data, offset);

         if (exceptionCount != 0)
         {
            int index;
            ProjectCalendarException exception;
            long duration;
            int periodCount;
            Date start;

            //
            // Move to the start of the first exception
            //
            offset += 4;

            //
            // Each exception is a 92 byte block, followed by a
            // variable length text block
            //
            for (index = 0; index < exceptionCount; index++)
            {
               Date fromDate = MPPUtility.getDate(data, offset);
               Date toDate = MPPUtility.getDate(data, offset + 2);
               exception = cal.addCalendarException(fromDate, toDate);

               periodCount = MPPUtility.getShort(data, offset + 14);
               if (periodCount != 0)
               {
                  for (int exceptionPeriodIndex = 0; exceptionPeriodIndex < periodCount; exceptionPeriodIndex++)
                  {
                     start = MPPUtility.getTime(data, offset + 20 + (exceptionPeriodIndex * 2));
                     duration = MPPUtility.getDuration(data, offset + 32 + (exceptionPeriodIndex * 4));
                     exception.addRange(new DateRange(start, new Date(start.getTime() + duration)));
                  }
               }

               //
               // Extract the name length - ensure that it is aligned to a 4 byte boundary
               //
               int exceptionNameLength = MPPUtility.getInt(data, offset + 88);
               if (exceptionNameLength % 4 != 0)
               {
                  exceptionNameLength = ((exceptionNameLength / 4) + 1) * 4;
               }

               //String exceptionName = MPPUtility.getUnicodeString(data, offset+92);
               offset += (92 + exceptionNameLength);
            }
         }
      }
   }

   /**
    * The way calendars are stored in an MPP14 file means that there
    * can be forward references between the base calendar unique ID for a
    * derived calendar, and the base calendar itself. To get around this,
    * we initially populate the base calendar name attribute with the
    * base calendar unique ID, and now in this method we can convert those
    * ID values into the correct names.
    *
    * @param baseCalendars list of calendars and base calendar IDs
    * @param map map of calendar ID values and calendar objects
    */
   private void updateBaseCalendarNames(List<Pair<ProjectCalendar, Integer>> baseCalendars, HashMap<Integer, ProjectCalendar> map)
   {
      for (Pair<ProjectCalendar, Integer> pair : baseCalendars)
      {
         ProjectCalendar cal = pair.getFirst();
         Integer baseCalendarID = pair.getSecond();
         ProjectCalendar baseCal = map.get(baseCalendarID);
         if (baseCal != null && baseCal.getName() != null)
         {
            cal.setParent(baseCal);
         }
         else
         {
            // Remove invalid calendar to avoid serious problems later.
            m_file.removeCalendar(cal);
         }
      }
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
    *
    * @throws java.io.IOException
    */
   private void processTaskData() throws IOException
   {
      FieldMap fieldMap = new FieldMap14(m_file.getProjectProperties(), m_file.getCustomFields());
      fieldMap.createTaskFieldMap(m_projectProps);

      FieldMap enterpriseCustomFieldMap = new FieldMap14(m_file.getProjectProperties(), m_file.getCustomFields());
      enterpriseCustomFieldMap.createEnterpriseCustomFieldMap(m_projectProps, TaskField.class);

      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      VarMeta taskVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data(taskVarMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData(taskFixedMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedData"))), fieldMap.getMaxFixedDataSize(0));
      FixedMeta taskFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Meta"))), taskFixedData, 92, 93);
      FixedData taskFixed2Data = new FixedData(taskFixed2Meta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Data"))));

      Props14 props = new Props14(m_inputStreamFactory.getInstance(taskDir, "Props"));
      //      System.out.println(taskFixedMeta);
      //      System.out.println(taskFixedData);
      //      System.out.println(taskVarMeta);
      //      System.out.println(taskVarData);
      //      System.out.println(taskFixed2Meta);
      //      System.out.println(taskFixed2Data);
      //      System.out.println(m_outlineCodeVarData.getVarMeta());
      //      System.out.println(m_outlineCodeVarData);
      //      System.out.println(props);

      // Process aliases
      new CustomFieldAliasReader(m_file.getCustomFields(), props.getByteArray(TASK_FIELD_NAME_ALIASES)).process();

      TreeMap<Integer, Integer> taskMap = createTaskMap(fieldMap, taskFixedMeta, taskFixedData);
      // The var data may not contain all the tasks as tasks with no var data assigned will
      // not be saved in there. Most notably these are tasks with no name. So use the task map
      // which contains all the tasks.
      Object[] uniqueIdArray = taskMap.keySet().toArray(); //taskVarMeta.getUniqueIdentifierArray();
      Integer offset;
      byte[] data;
      byte[] metaData;
      byte[] metaData2;
      Task task;
      boolean autoWBS = true;
      LinkedList<Task> externalTasks = new LinkedList<Task>();
      RecurringTaskReader recurringTaskReader = null;
      String notes;

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

      for (int loop = 0; loop < uniqueIdArray.length; loop++)
      {
         Integer uniqueID = (Integer) uniqueIdArray[loop];

         offset = taskMap.get(uniqueID);
         if (taskFixedData.isValidOffset(offset) == false)
         {
            continue;
         }

         data = taskFixedData.getByteArrayValue(offset.intValue());
         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
         Integer id = Integer.valueOf(MPPUtility.getInt(data, fieldMap.getFixedDataOffset(TaskField.ID)));

         if (data.length == NULL_TASK_BLOCK_SIZE)
         {
            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(Integer.valueOf(MPPUtility.getShort(data, TASK_UNIQUE_ID_FIXED_OFFSET)));
            task.setID(Integer.valueOf(MPPUtility.getShort(data, TASK_ID_FIXED_OFFSET)));
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());
            continue;
         }

         if (data.length < fieldMap.getMaxFixedDataSize(0))
         {
            byte[] newData = new byte[fieldMap.getMaxFixedDataSize(0) + 8];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
         }

         //System.out.println (MPPUtility.hexdump(data, false, 16, ""));
         //System.out.println (MPPUtility.hexdump(data,false));
         //System.out.println (MPPUtility.hexdump(metaData, false, 16, ""));
         //MPPUtility.dataDump(m_file, data, false, false, false, true, false, false, false, false);
         //MPPUtility.dataDump(metaData, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(taskVarData, id, true, true, true, true, true, true);

         metaData2 = taskFixed2Meta.getByteArrayValue(offset.intValue());
         byte[] data2 = taskFixed2Data.getByteArrayValue(offset.intValue());
         //System.out.println (MPPUtility.hexdump(metaData2, false, 16, ""));
         //System.out.println(MPPUtility.hexdump(data2, false, 16, ""));
         //System.out.println (MPPUtility.hexdump(metaData2,false));

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

         fieldMap.populateContainer(TaskField.class, task, uniqueID, new byte[][]
         {
            data,
            data2
         }, taskVarData);

         enterpriseCustomFieldMap.populateContainer(TaskField.class, task, uniqueID, null, taskVarData);

         task.enableEvents();

         task.setEstimated(getDurationEstimated(MPPUtility.getShort(data, fieldMap.getFixedDataOffset(TaskField.ACTUAL_DURATION_UNITS))));

         Integer externalTaskID = task.getSubprojectTaskID();
         if (externalTaskID != null && externalTaskID.intValue() != 0)
         {
            task.setExternalTask(true);
            externalTasks.add(task);
         }

         processHyperlinkData(task, taskVarData.getByteArray(uniqueID, fieldMap.getVarDataKey(TaskField.HYPERLINK_DATA)));

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

         m_parentTasks.put(task.getUniqueID(), (Integer) task.getCachedValue(TaskField.PARENT_TASK_UNIQUE_ID));

         if (task.getStart() == null || (task.getCachedValue(TaskField.SCHEDULED_START) != null && task.getTaskMode() == TaskMode.AUTO_SCHEDULED))
         {
            task.setStart((Date) task.getCachedValue(TaskField.SCHEDULED_START));
         }

         if (task.getFinish() == null || (task.getCachedValue(TaskField.SCHEDULED_FINISH) != null && task.getTaskMode() == TaskMode.AUTO_SCHEDULED))
         {
            task.setFinish((Date) task.getCachedValue(TaskField.SCHEDULED_FINISH));
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
               if (DateHelper.compare(task.getStart(), task.getLateStart()) < 0)
               {
                  task.setStart(task.getLateStart());
               }
               if (DateHelper.compare(task.getFinish(), task.getLateFinish()) < 0)
               {
                  task.setFinish(task.getLateFinish());
               }
               break;
            }

            case START_NO_LATER_THAN:
            case FINISH_NO_LATER_THAN:
            {
               if (DateHelper.compare(task.getFinish(), task.getStart()) < 0)
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
         // Retrieve the task notes.
         //
         notes = task.getNotes();
         if (m_reader.getPreserveNoteFormatting() == false)
         {
            notes = RtfHelper.strip(notes);
         }

         task.setNotes(notes);

         //
         // Set the calendar name
         //
         Integer calendarID = (Integer) task.getCachedValue(TaskField.CALENDAR_UNIQUE_ID);
         if (calendarID != null && calendarID.intValue() != -1)
         {
            ProjectCalendar calendar = m_file.getCalendarByUniqueID(calendarID);
            if (calendar != null)
            {
               task.setCalendar(calendar);
            }
         }

         //
         // Set the sub project flag
         //
         SubProject sp = m_taskSubProjects.get(task.getUniqueID());
         task.setSubProject(sp);

         //
         // Set the external flag
         //
         if (sp != null)
         {
            task.setExternalTask(sp.isExternalTask(task.getUniqueID()));
            if (task.getExternalTask())
            {
               task.setExternalTaskProject(sp.getFullPath());
            }
         }

         //
         // If we have a WBS value from the MPP file, don't autogenerate
         //
         if (task.getWBS() != null)
         {
            autoWBS = false;
         }

         //
         // If this is a split task, allocate space for the split durations
         //
         if ((metaData[9] & 0x80) == 0)
         {
            task.setSplits(new LinkedList<DateRange>());
         }

         //
         // If this is a manually scheduled task, read the manual duration
         //
         if (task.getTaskMode() != TaskMode.MANUALLY_SCHEDULED)
         {
            task.setManualDuration(null);
         }

         //
         // Process any enterprise columns
         //
         processTaskEnterpriseColumns(uniqueID, task, taskVarData, metaData2);

         // Unfortunately it looks like 'null' tasks sometimes make it through. So let's check for to see if we
         // need to mark this task as a null task after all.
         if (task.getName() == null && ((task.getStart() == null || task.getStart().getTime() == MPPUtility.getEpochDate().getTime()) || (task.getFinish() == null || task.getFinish().getTime() == MPPUtility.getEpochDate().getTime()) || (task.getCreateDate() == null || task.getCreateDate().getTime() == MPPUtility.getEpochDate().getTime())))
         {
            // Remove this to avoid passing bad data to the client
            m_file.removeTask(task);

            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(uniqueID);
            task.setID(id);
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());
            //System.out.println(task);
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

         //System.out.println(task + " " + MPPUtility.getShort(data2, 22)); // JPI - looks like this value determines the ID order! Validate and check other versions!
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
      TreeMap<Integer, Integer> taskMap = new TreeMap<Integer, Integer>();

      // I've found a pathological case of an MPP file with around 16k blank tasks...
      int nextIDIncrement = 16500;
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
      for (Map.Entry<Integer, Integer> entry : m_nullTaskOrder.entrySet())
      {
         int idValue = entry.getKey().intValue();
         int baseTargetIdValue = (idValue - insertionCount) * nextIDIncrement;
         int targetIDValue = baseTargetIdValue;
         int offset = 0;
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
    * @param id task unique ID
    * @param task task instance
    * @param taskVarData task var data
    * @param metaData2 task meta data
    */
   private void processTaskEnterpriseColumns(Integer id, Task task, Var2Data taskVarData, byte[] metaData2)
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
    * @param id resource unique ID
    * @param resource resource instance
    * @param resourceVarData resource var data
    * @param metaData2 resource meta data
    */
   private void processResourceEnterpriseColumns(Integer id, Resource resource, Var2Data resourceVarData, byte[] metaData2)
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
      SubProject currentSubProject = null;

      for (Task currentTask : externalTasks)
      {
         SubProject sp = currentTask.getSubProject();
         if (sp == null)
         {
            currentTask.setSubProject(currentSubProject);

            //we need to set the external task project path now that we have
            //the subproject for this task (was skipped while processing the task earlier)
            if (currentSubProject != null)
            {
               currentTask.setExternalTaskProject(currentSubProject.getFullPath());
            }

         }
         else
         {
            currentSubProject = sp;
         }

         if (currentSubProject != null)
         {
            //System.out.println ("Task: " +currentTask.getUniqueID() + " " + currentTask.getName() + " File=" + currentSubProject.getFullPath() + " ID=" + currentTask.getExternalTaskID());
            currentTask.setProject(currentSubProject.getFullPath());
         }
      }
   }

   /**
    * This method is used to extract the task hyperlink attributes
    * from a block of data and call the appropriate modifier methods
    * to configure the specified task object.
    *
    * @param task task instance
    * @param data hyperlink data block
    */
   private void processHyperlinkData(Task task, byte[] data)
   {
      if (data != null)
      {
         int offset = 12;
         String hyperlink;
         String address;
         String subaddress;

         offset += 12;
         hyperlink = MPPUtility.getUnicodeString(data, offset);
         offset += ((hyperlink.length() + 1) * 2);

         offset += 12;
         address = MPPUtility.getUnicodeString(data, offset);
         offset += ((address.length() + 1) * 2);

         offset += 12;
         subaddress = MPPUtility.getUnicodeString(data, offset);

         task.setHyperlink(hyperlink);
         task.setHyperlinkAddress(address);
         task.setHyperlinkSubAddress(subaddress);
      }
   }

   /**
    * This method is used to extract the resource hyperlink attributes
    * from a block of data and call the appropriate modifier methods
    * to configure the specified task object.
    *
    * @param resource resource instance
    * @param data hyperlink data block
    */
   private void processHyperlinkData(Resource resource, byte[] data)
   {
      if (data != null)
      {
         int offset = 12;
         String hyperlink;
         String address;
         String subaddress;

         offset += 12;
         hyperlink = MPPUtility.getUnicodeString(data, offset);
         offset += ((hyperlink.length() + 1) * 2);

         offset += 12;
         address = MPPUtility.getUnicodeString(data, offset);
         offset += ((address.length() + 1) * 2);

         offset += 12;
         subaddress = MPPUtility.getUnicodeString(data, offset);

         resource.setHyperlink(hyperlink);
         resource.setHyperlinkAddress(address);
         resource.setHyperlinkSubAddress(subaddress);
      }
   }

   /**
    * This method extracts and collates constraint data.
    *
    * @throws java.io.IOException
    */
   private void processConstraintData() throws IOException
   {
      ConstraintFactory factory = new ConstraintFactory();
      factory.process(m_projectDir, m_file, m_inputStreamFactory);
   }

   /**
    * This method extracts and collates resource data.
    *
    * @throws java.io.IOException
    */
   private void processResourceData() throws IOException
   {
      FieldMap fieldMap = new FieldMap14(m_file.getProjectProperties(), m_file.getCustomFields());
      fieldMap.createResourceFieldMap(m_projectProps);

      FieldMap enterpriseCustomFieldMap = new FieldMap14(m_file.getProjectProperties(), m_file.getCustomFields());
      enterpriseCustomFieldMap.createEnterpriseCustomFieldMap(m_projectProps, ResourceField.class);

      DirectoryEntry rscDir = (DirectoryEntry) m_projectDir.getEntry("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data(rscVarMeta, new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData(rscFixedMeta, m_inputStreamFactory.getInstance(rscDir, "FixedData"));
      FixedMeta rscFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Fixed2Meta"))), 50);
      FixedData rscFixed2Data = new FixedData(rscFixed2Meta, m_inputStreamFactory.getInstance(rscDir, "Fixed2Data"));
      Props14 props = new Props14(m_inputStreamFactory.getInstance(rscDir, "Props"));
      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      //System.out.println(rscFixedMeta);
      //System.out.println(rscFixedData);
      //System.out.println(rscFixed2Meta);
      //System.out.println(rscFixed2Data);
      //System.out.println(props);

      // Process aliases
      new CustomFieldAliasReader(m_file.getCustomFields(), props.getByteArray(RESOURCE_FIELD_NAME_ALIASES)).process();

      TreeMap<Integer, Integer> resourceMap = createResourceMap(fieldMap, rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifierArray();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Resource resource;
      String notes;

      //
      // Select the correct meta data locations depending on
      // which version of Microsoft project generated this file
      //
      MppBitFlag[] metaDataBitFlags;
      MppBitFlag[] metaData2BitFlags;
      if (NumberHelper.getInt(m_file.getProjectProperties().getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
      {
         metaDataBitFlags = PROJECT2013_RESOURCE_META_DATA_BIT_FLAGS;
         metaData2BitFlags = PROJECT2013_RESOURCE_META_DATA2_BIT_FLAGS;
      }
      else
      {
         metaDataBitFlags = PROJECT2010_RESOURCE_META_DATA_BIT_FLAGS;
         metaData2BitFlags = PROJECT2010_RESOURCE_META_DATA2_BIT_FLAGS;
      }

      for (int loop = 0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
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

         fieldMap.populateContainer(ResourceField.class, resource, id, new byte[][]
         {
            data,
            data2
         }, rscVarData);

         enterpriseCustomFieldMap.populateContainer(ResourceField.class, resource, id, null, rscVarData);

         resource.enableEvents();

         processHyperlinkData(resource, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.HYPERLINK_DATA)));

         resource.setID(Integer.valueOf(MPPUtility.getInt(data, fieldMap.getFixedDataOffset(ResourceField.ID))));

         resource.setOutlineCode1(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE1_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode2(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE2_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode3(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE3_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode4(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE4_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode5(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE5_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode6(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE6_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode7(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE7_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode8(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE8_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode9(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE9_INDEX))), OUTLINECODE_DATA));
         resource.setOutlineCode10(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, fieldMap.getVarDataKey(ResourceField.OUTLINE_CODE10_INDEX))), OUTLINECODE_DATA));

         metaData = rscFixedMeta.getByteArrayValue(offset.intValue());
         readBitFields(metaDataBitFlags, resource, metaData);
         readBitFields(metaData2BitFlags, resource, metaData2);

         if (resource.getWorkGroup() == WorkGroup.DEFAULT)
         {
            resource.setType(ResourceType.WORK);
         }

         resource.setUniqueID(id);

         notes = resource.getNotes();
         if (m_reader.getPreserveNoteFormatting() == false)
         {
            notes = RtfHelper.strip(notes);
         }

         resource.setNotes(notes);

         //
         // Configure the resource calendar
         //
         resource.setResourceCalendar(m_resourceMap.get(id));

         //
         // Process any enterprise columns
         //
         processResourceEnterpriseColumns(id, resource, rscVarData, metaData2);

         //
         // Process cost rate tables
         //
         CostRateTableFactory crt = new CostRateTableFactory();
         crt.process(resource, 0, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_A)));
         crt.process(resource, 1, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_B)));
         crt.process(resource, 2, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_C)));
         crt.process(resource, 3, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_D)));
         crt.process(resource, 4, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_E)));

         //
         // Process availability table
         //
         AvailabilityFactory af = new AvailabilityFactory();
         af.process(resource.getAvailability(), rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.AVAILABILITY_DATA)));

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * This method extracts and collates resource assignment data.
    *
    * @throws IOException
    */
   private void processAssignmentData() throws IOException
   {
      FieldMap fieldMap = new FieldMap14(m_file.getProjectProperties(), m_file.getCustomFields());
      fieldMap.createAssignmentFieldMap(m_projectProps);

      FieldMap enterpriseCustomFieldMap = new FieldMap14(m_file.getProjectProperties(), m_file.getCustomFields());
      enterpriseCustomFieldMap.createEnterpriseCustomFieldMap(m_projectProps, AssignmentField.class);

      DirectoryEntry assnDir = (DirectoryEntry) m_projectDir.getEntry("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data(assnVarMeta, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData(110, m_inputStreamFactory.getInstance(assnDir, "FixedData"));
      FixedData assnFixedData2 = new FixedData(48, m_inputStreamFactory.getInstance(assnDir, "Fixed2Data"));
      //FixedMeta assnFixedMeta2 = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Fixed2Meta"))), 53);
      //Props props = new Props14(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Props"))));

      ResourceAssignmentFactory factory = new ResourceAssignmentFactory();
      factory.process(m_file, fieldMap, enterpriseCustomFieldMap, m_reader.getUseRawTimephasedData(), m_reader.getPreserveNoteFormatting(), assnVarMeta, assnVarData, assnFixedMeta, assnFixedData, assnFixedData2, assnFixedMeta.getItemCount());
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
    *
    * @throws java.io.IOException
    */
   private void processViewData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CV_iew");
      VarMeta viewVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data viewVarData = new Var2Data(viewVarMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
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

   /**
    * This method extracts table data from the MPP file.
    *
    * @throws java.io.IOException
    */
   private void processTableData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CTable");

      VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
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

   /**
    * Read filter definitions.
    *
    * @throws IOException
    */
   private void processFilterData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CFilter");
      FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
      FixedData fixedData = new FixedData(fixedMeta, m_inputStreamFactory.getInstance(dir, "FixedData"));
      VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

      //System.out.println(fixedMeta);
      //System.out.println(fixedData);
      //System.out.println(varMeta);
      //System.out.println(varData);

      FilterReader reader = new FilterReader14();
      reader.process(m_file.getProjectProperties(), m_file.getFilters(), fixedData, varData);
   }

   /**
    * Read saved view state from an MPP file.
    *
    * @throws IOException
    */
   private void processSavedViewState() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CEdl");
      VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
      //System.out.println(varMeta);
      //System.out.println(varData);

      InputStream is = new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedData")));
      byte[] fixedData = new byte[is.available()];
      is.read(fixedData);
      is.close();
      //System.out.println(MPPUtility.hexdump(fixedData, false, 16, ""));

      ViewStateReader reader = new ViewStateReader12();
      reader.process(m_file, varData, fixedData);
   }

   /**
    * Read group definitions.
    *
    * @throws IOException
    */
   private void processGroupData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CGrouping");
      FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
      FixedData fixedData = new FixedData(fixedMeta, m_inputStreamFactory.getInstance(dir, "FixedData"));
      VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

      //System.out.println(fixedMeta);
      //System.out.println(fixedData);
      //System.out.println(varMeta);
      //System.out.println(varData);

      GroupReader14 reader = new GroupReader14();
      reader.process(m_file, fixedData, varData, m_fontBases);
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

   //   private static void dumpUnknownData (String label, int[][] spec, byte[] data)
   //   {
   //      System.out.print (label);
   //      for (int loop=0; loop < spec.length; loop++)
   //      {
   //         int startByte = spec[loop][0];
   //         int length = spec[loop][1];
   //         if (length == -1)
   //         {
   //            length = data.length - startByte;
   //         }
   //         System.out.print ("["+spec[loop][0] + "]["+ MPPUtility.hexdump(data, startByte, length, false) + " ]");
   //      }
   //      System.out.println ();
   //   }

   //   private static final int[][] UNKNOWN_TASK_DATA = new int[][]
   //   {
   //      {42, 18},
   //      {116, 4},
   //      {134, 2},
   //      {144, 4},
   //      {144, 16},
   //      {248, 8},
   //      {256, -1}
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
   private VarMeta m_outlineCodeVarMeta;
   private FixedData m_outlineCodeFixedData;
   private FixedMeta m_outlineCodeFixedMeta;
   private FixedData m_outlineCodeFixedData2;
   private FixedMeta m_outlineCodeFixedMeta2;
   private Props14 m_projectProps;
   private Map<Integer, FontBase> m_fontBases;
   private Map<Integer, SubProject> m_taskSubProjects;
   private DirectoryEntry m_projectDir;
   private DirectoryEntry m_viewDir;
   private Map<Integer, Integer> m_parentTasks;
   private Map<Long, Integer> m_taskOrder;
   private Map<Integer, Integer> m_nullTaskOrder;
   private DocumentInputStreamFactory m_inputStreamFactory;

   //   private static final Comparator<Task> START_COMPARATOR = new Comparator<Task>()
   //   {
   //      public int compare(Task o1, Task o2)
   //      {
   //         int result = DateUtility.compare(o1.getStart(), o2.getStart());
   //         if (result == 0)
   //         {
   //            result = o1.getUniqueID().intValue() - o2.getUniqueID().intValue();
   //            //result = o1.getID().intValue() - o2.getID().intValue();
   //         }
   //         return (result);
   //      }
   //   };

   //   private static final Comparator<Task> FINISH_COMPARATOR = new Comparator<Task>()
   //   {
   //      public int compare(Task o1, Task o2)
   //      {
   //         int result = DateUtility.compare(o1.getFinish(), o2.getFinish());
   //         if (result == 0)
   //         {
   //            result = o1.getUniqueID().intValue() - o2.getUniqueID().intValue();
   //         }
   //         return (result);
   //      }
   //   };

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
    * Calendar data types.
    */
   private static final Integer CALENDAR_NAME = Integer.valueOf(1);
   private static final Integer CALENDAR_DATA = Integer.valueOf(8);

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

   /**
    * Deleted and null tasks have their ID and UniqueID attributes at fixed offsets.
    */
   private static final int TASK_UNIQUE_ID_FIXED_OFFSET = 0;
   private static final int TASK_ID_FIXED_OFFSET = 4;
   private static final int NULL_TASK_BLOCK_SIZE = 16;

   /**
    * Default working week.
    */
   private static final boolean[] DEFAULT_WORKING_WEEK =
   {
      false,
      true,
      true,
      true,
      true,
      true,
      false
   };

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
      new MppBitFlag(ResourceField.TYPE, 8, 0x10, ResourceType.MATERIAL, ResourceType.COST)
   };

   private static final MppBitFlag[] PROJECT2013_RESOURCE_META_DATA2_BIT_FLAGS =
   {
      new MppBitFlag(ResourceField.BUDGET, 8, 0x20, Boolean.FALSE, Boolean.TRUE),
      new MppBitFlag(ResourceField.TYPE, 8, 0x10, ResourceType.MATERIAL, ResourceType.COST)
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
