/*
 * file:       MPP12Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2005
 * date:       05/12/2005
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

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Table;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.View;
import net.sf.mpxj.utility.DateUtility;
import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.utility.Pair;
import net.sf.mpxj.utility.RTFUtility;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * This class is used to represent a Microsoft Project MPP12 file. This
 * implementation allows the file to be read, and the data it contains
 * exported as a set of MPX objects. These objects can be interrogated
 * to retrieve any required data, or stored as an MPX file.
 */
final class MPP12Reader implements MPPVariantReader
{
   /**
    * This method is used to process an MPP12 file. This is the file format
    * used by Project 12.
    *
    * @param reader parent file reader
    * @param file parent MPP file
    * @param root Root of the POI file system.
    */
   public void process(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException
   {
      try
      {
         //
         // Retrieve the high level document properties
         //
         Props12 props12 = new Props12(new DocumentInputStream(((DocumentEntry) root.getEntry("Props12"))));
         //System.out.println(props12);

         file.setProjectFilePath(props12.getUnicodeString(Props.PROJECT_FILE_PATH));
         file.setEncoded(props12.getByte(Props.PASSWORD_FLAG) != 0);
         file.setEncryptionCode(props12.getByte(Props.ENCRYPTION_CODE));

         //
         // Test for password protection. In the single byte retrieved here:
         //
         // 0x00 = no password
         // 0x01 = protection password has been supplied
         // 0x02 = write reservation password has been supplied
         // 0x03 = both passwords have been supplied
         //  
         if ((props12.getByte(Props.PASSWORD_FLAG) & 0x01) != 0)
         {
            // Couldn't figure out how to get the password for MPP12 files so for now we just need to block the reading

            // File is password protected for reading, let's read the password
            // and see if the correct read password was given to us.
            //byte[] data = props12.getByteArray(Props.READ_PASSWORD);
            //MPPUtility.decodeBuffer(data, file.getEncryptionCode());
            //System.out.println(MPPUtility.hexdump(data, true, 16, ""));

            //System.out.println();
            //data = props12.getByteArray(Props.WRITE_PASSWORD);
            //MPPUtility.decodeBuffer(data, file.getEncryptionCode());
            //System.out.println(MPPUtility.hexdump(data, true, 16, ""));

            //String readPassword = MPPUtility.decodePassword(props12.getByteArray(Props.READ_PASSWORD), file.getEncryptionCode());
            //System.out.println(readPassword);
            //String writePassword = MPPUtility.decodePassword(props12.getByteArray(Props.WRITE_PASSWORD), file.getEncryptionCode());
            //System.out.println(writePassword);
            // See if the correct read password was given
            //if (readPassword == null)
            //{
            // Couldn't read password, so no chance to ask the user
            throw new MPXJException(MPXJException.PASSWORD_PROTECTED);
            //}
            //if (reader.getReadPassword() == null || reader.getReadPassword().matches(readPassword) == false)
            //{    	      	
            // Passwords don't match
            //	  throw new MPXJException (MPXJException.PASSWORD_PROTECTED_ENTER_PASSWORD);
            //}
            // Passwords matched so let's allow the reading to continue.
         }

         m_reader = reader;
         m_file = file;
         m_root = root;
         m_resourceMap = new HashMap<Integer, ProjectCalendar>();
         m_projectDir = (DirectoryEntry) root.getEntry("   112");
         m_viewDir = (DirectoryEntry) root.getEntry("   212");
         DirectoryEntry outlineCodeDir = (DirectoryEntry) m_projectDir.getEntry("TBkndOutlCode");
         m_outlineCodeVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("VarMeta"))));
         m_outlineCodeVarData = new Var2Data(m_outlineCodeVarMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Var2Data"))));
         m_projectProps = new Props12(getEncryptableInputStream(m_projectDir, "Props"));
         //MPPUtility.fileDump("c:\\temp\\props.txt", props.toString().getBytes());

         m_fontBases = new HashMap<Integer, FontBase>();
         m_taskSubProjects = new HashMap<Integer, SubProject>();
         m_parentTasks = new HashMap<Integer, Integer>();
         m_taskOrder = new TreeMap<Long, Integer>();
         m_nullTaskOrder = new TreeMap<Integer, Integer>();

         m_file.setMppFileType(12);
         m_file.setAutoFilter(props12.getBoolean(Props.AUTO_FILTER));

         processCustomValueLists();
         processPropertyData();
         processCalendarData();
         processResourceData();
         processTaskData();
         processConstraintData();
         processAssignmentData();

         //
         // MPP12 files seem to exhibit some occasional weirdness
         // with duplicate ID values which leads to the task structure
         // being reported incorrectly. The following method
         // attempts to correct this.
         //
         fixTaskOrder();

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

      finally
      {
         m_reader = null;
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
   }

   /**
    * This method extracts and collates the value list information 
    * for custom column value lists.
    */
   private void processCustomValueLists()
   {
      Integer[] uniqueid = m_outlineCodeVarMeta.getUniqueIdentifierArray();
      Integer id;
      CustomFieldValueItem item;

      for (int loop = 0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];

         item = new CustomFieldValueItem(id);
         item.setValue(m_outlineCodeVarData.getByteArray(id, VALUE_LIST_VALUE));
         item.setDescription(m_outlineCodeVarData.getUnicodeString(id, VALUE_LIST_DESCRIPTION));
         item.setUnknown(m_outlineCodeVarData.getByteArray(id, VALUE_LIST_UNKNOWN));

         m_file.addCustomFieldValueItem(item);
      }
   }

   /**
    * This method extracts and collates global property data.
    *
    * @throws java.io.IOException
    */
   private void processPropertyData() throws IOException, MPXJException
   {
      //
      // Process the project header
      //
      ProjectHeaderReader projectHeaderReader = new ProjectHeaderReader();
      projectHeaderReader.process(m_file, m_projectProps, m_root);

      //
      // Process subproject data
      //
      processSubProjectData();

      //
      // Process graphical indicators
      //
      GraphicalIndicatorReader reader = new GraphicalIndicatorReader();
      reader.process(m_file, m_projectProps);
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
         SubProject sp;

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

            //System.out.println();
            //System.out.println (MPPUtility.hexdump(itemHeader, false, 16, ""));
            //System.out.println(MPPUtility.hexdump(subProjData, offset, 16, false));
            //System.out.println("Offset1: " + (MPPUtility.getInt(subProjData, offset) & 0x1FFFF));
            //System.out.println("Offset2: " + (MPPUtility.getInt(subProjData, offset+4) & 0x1FFFF));
            //System.out.println("Offset3: " + (MPPUtility.getInt(subProjData, offset+8) & 0x1FFFF));
            //System.out.println("Offset4: " + (MPPUtility.getInt(subProjData, offset+12) & 0x1FFFF));
            //System.out.println ("Offset: " + offset);
            //System.out.println ("Item Header Offset: " + itemHeaderOffset);
            byte subProjectType = itemHeader[16];
            //System.out.println("SubProjectType: " + Integer.toHexString(subProjectType));
            switch (subProjectType)
            {
               //
               // Subproject that is no longer inserted. This is a placeholder in order to be
               // able to always guarantee unique unique ids.
               //
               case 0x00 :
                  //   
                  // deleted entry?
                  //
               case 0x10 :
               {
                  offset += 8;
                  break;
               }

                  //
                  // task unique ID, 8 bytes, path, file name
                  //
               case (byte) 0x99 :
               case 0x09 :
               case 0x0D :
               {
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  // sometimes offset of a task ID?
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // task unique ID, 8 bytes, path, file name
                  //
               case 0x03 :
               case 0x11 :
               case (byte) 0x91 :
               {
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  // Unknown offset
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // task unique ID, path, unknown, file name
                  //
               case (byte) 0x81 :
               case (byte) 0x83 :
               case 0x41 :
               {
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  // unknown offset to 2 bytes of data?
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // task unique ID, path, file name
                  //
               case 0x01 :
               case 0x08 :
               {
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // task unique ID, path, file name
                  //
               case (byte) 0xC0 :
               {
                  uniqueIDOffset = itemHeaderOffset;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  // unknown offset
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // resource, task unique ID, path, file name
                  //
               case 0x05 :
               {
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  m_file.setResourceSubProject(sp);
                  break;
               }

               case 0x45 :
               {
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  offset += 4;
                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  m_file.setResourceSubProject(sp);
                  break;
               }

                  //
                  // path, file name
                  //
               case 0x02 :
               {
                  //filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  //fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  //sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset, index);
                  // 0x02 looks to be the link FROM the resource pool to a project that is using it.
                  break;
               }

               case 0x04 :
               {
                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset, index);
                  m_file.setResourceSubProject(sp);
                  break;
               }

                  //
                  // task unique ID, 4 bytes, path, 4 bytes, file name
                  //
               case (byte) 0x8D :
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 8;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 8;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // task unique ID, path, file name
                  //
               case 0x0A :
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  // new resource pool entry
               case (byte) 0x44 :
               {
                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // Appears when a subproject is collapsed
                  //
               case (byte) 0x80 :
               {
                  offset += 12;
                  break;
               }

                  //
                  // Any other value, assume 12 bytes to handle old/deleted data?
                  //
               default :
               {
                  offset += 12;
                  break;
               }
            }
         }
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
               case SUBPROJECT_TASKUNIQUEID0 :
               case SUBPROJECT_TASKUNIQUEID1 :
               case SUBPROJECT_TASKUNIQUEID2 :
               case SUBPROJECT_TASKUNIQUEID3 :
               case SUBPROJECT_TASKUNIQUEID4 :
               case SUBPROJECT_TASKUNIQUEID5 :
               {
                  sp.setTaskUniqueID(Integer.valueOf(value));
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }

               default :
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
         m_file.addSubProject(sp);

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
      Props12 props = new Props12(getEncryptableInputStream(m_viewDir, "Props"));
      byte[] data = props.getByteArray(Props.FONT_BASES);
      if (data != null)
      {
         processBaseFonts(data);
      }

      ProjectHeader header = m_file.getProjectHeader();
      header.setShowProjectSummaryTask(props.getBoolean(Props.SHOW_PROJECT_SUMMARY_TASK));
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
    * Retrieve any task field aliases defined in the MPP file.
    *
    * @param data task field name alias data
    */
   private void processTaskFieldNameAliases(byte[] data)
   {
      if (data != null)
      {
         int index = 0;
         int offset = 0;
         // First the length (repeated twice)
         int length = MPPUtility.getInt(data, offset);
         offset += 8;
         // Then the number of custom columns
         int numberOfAliases = MPPUtility.getInt(data, offset);
         offset += 4;

         // Then the aliases themselves
         String alias = "";
         int field = -1;
         int aliasOffset = 0;
         while (index < numberOfAliases && offset < length)
         {
            // Each item consists of the Field ID (2 bytes), 40 0B marker (2 bytes), and the
            // offset to the string (4 bytes)

            // Get the Field ID
            field = MPPUtility.getShort(data, offset);
            offset += 2;
            // Go past 40 0B marker
            offset += 2;
            // Get the alias offset (offset + 4 for some reason).
            aliasOffset = MPPUtility.getInt(data, offset) + 4;
            offset += 4;
            // Read the alias itself 
            if (aliasOffset < data.length)
            {
               alias = MPPUtility.getUnicodeString(data, aliasOffset);
               m_file.setTaskFieldAlias(MPPTaskField.getInstance(field), alias);
               //System.out.println(field + ": " + alias);
            }
            index++;
         }
      }
      //System.out.println(file.getTaskFieldAliasMap().toString());
   }

   /**
    * Retrieve any resource field aliases defined in the MPP file.
    *
    * @param data resource field name alias data
    */
   private void processResourceFieldNameAliases(byte[] data)
   {
      if (data != null)
      {
         int index = 0;
         int offset = 0;
         // First the length (repeated twice)
         int length = MPPUtility.getInt(data, offset);
         offset += 8;
         // Then the number of custom columns
         int numberOfAliases = MPPUtility.getInt(data, offset);
         offset += 4;

         // Then the aliases themselves
         String alias = "";
         int field = -1;
         int aliasOffset = 0;
         while (index < numberOfAliases && offset < length)
         {
            // Each item consists of the Field ID (2 bytes), 40 0B marker (2 bytes), and the
            // offset to the string (4 bytes)

            // Get the Field ID
            field = MPPUtility.getShort(data, offset);
            offset += 2;
            // Go past 40 0B marker
            offset += 2;
            // Get the alias offset (offset + 4 for some reason).
            aliasOffset = MPPUtility.getInt(data, offset) + 4;
            offset += 4;
            // Read the alias itself
            if (aliasOffset < data.length)
            {
               alias = MPPUtility.getUnicodeString(data, aliasOffset);
               m_file.setResourceFieldAlias(MPPResourceField.getInstance(field), alias);
               //System.out.println(field + ": " + alias);
            }
            index++;
         }
      }
      //System.out.println(file.getResourceFieldAliasMap().toString());
   }

   /**
    * This method maps the task unique identifiers to their index number
    * within the FixedData block.
    *
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @return Mapping between task identifiers and block position
    */
   private TreeMap<Integer, Integer> createTaskMap(FixedMeta taskFixedMeta, FixedData taskFixedData)
   {
      TreeMap<Integer, Integer> taskMap = new TreeMap<Integer, Integer>();
      int itemCount = taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;
      Integer key;

      // First three items are not tasks, so let's skip them
      for (int loop = 3; loop < itemCount; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);
         if (data != null)
         {
            byte[] metaData = taskFixedMeta.getByteArrayValue(loop);
            int metaDataItemSize = MPPUtility.getInt(metaData, 0);
            if (metaDataItemSize < 16 && data.length != 16)
            {
               // Project stores the deleted tasks unique id's into the fixed data as well
               // and at least in one case the deleted task was listed twice in the list
               // the second time with data with it causing a phantom task to be shown.
               // See CalendarErrorPhantomTasks.mpp
               //
               // So let's add the unique id for the deleted task into the map so we don't
               // accidentally include the task later.
               uniqueID = MPPUtility.getShort(data, 0); // Only a short stored for deleted tasks
               key = Integer.valueOf(uniqueID);
               if (taskMap.containsKey(key) == false)
               {
                  taskMap.put(key, null); // use null so we can easily ignore this later
               }
            }
            else
            {
               //System.out.println(data.length+ " " +MPPUtility.hexdump(data, false));
               if (data.length == 16 || data.length >= MINIMUM_EXPECTED_TASK_SIZE)
               {
                  uniqueID = MPPUtility.getInt(data, 0);
                  key = Integer.valueOf(uniqueID);
                  if (taskMap.containsKey(key) == false)
                  {
                     taskMap.put(key, Integer.valueOf(loop));
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
    * @param rscFixedMeta resource fixed meta data
    * @param rscFixedData resource fixed data
    * @return map of resource IDs to resource data
    */
   private TreeMap<Integer, Integer> createResourceMap(FixedMeta rscFixedMeta, FixedData rscFixedData)
   {
      TreeMap<Integer, Integer> resourceMap = new TreeMap<Integer, Integer>();
      int itemCount = rscFixedMeta.getItemCount();

      for (int loop = 0; loop < itemCount; loop++)
      {
         byte[] data = rscFixedData.getByteArrayValue(loop);
         if (data == null || data.length < MINIMUM_EXPECTED_RESOURCE_SIZE)
         {
            continue;
         }

         Integer uniqueID = Integer.valueOf(MPPUtility.getShort(data, 0));
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
      FixedData calFixedData = new FixedData(calFixedMeta, getEncryptableInputStream(calDir, "FixedData"), 12);

      //System.out.println (calFixedMeta);
      //System.out.println (calFixedData);

      HashMap<Integer, ProjectCalendar> calendarMap = new HashMap<Integer, ProjectCalendar>();
      int items = calFixedData.getItemCount();
      List<Pair<ProjectCalendar, Integer>> baseCalendars = new LinkedList<Pair<ProjectCalendar, Integer>>();
      byte[] defaultCalendarData = m_projectProps.getByteArray(Props.DEFAULT_CALENDAR_HOURS);

      for (int loop = 0; loop < items; loop++)
      {
         byte[] fixedData = calFixedData.getByteArrayValue(loop);
         if (fixedData.length >= 8)
         {
            int offset = 0;

            //
            // Bug 890909, here we ensure that we have a complete 12 byte
            // block before attempting to process the data.
            //
            while (offset + 12 <= fixedData.length)
            {
               Integer calendarID = Integer.valueOf(MPPUtility.getInt(fixedData, offset + 0));
               int baseCalendarID = MPPUtility.getInt(fixedData, offset + 4);

               if (calendarID.intValue() != -1 && calendarID.intValue() != 0 && baseCalendarID != 0 && calendarMap.containsKey(calendarID) == false)
               {
                  byte[] varData = calVarData.getByteArray(calendarID, CALENDAR_DATA);
                  ProjectCalendar cal;

                  if (baseCalendarID == -1)
                  {
                     if (varData != null || defaultCalendarData != null)
                     {
                        cal = m_file.addBaseCalendar();
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
                        cal = m_file.addResourceCalendar();
                     }
                     else
                     {
                        cal = m_file.getDefaultResourceCalendar();
                     }

                     baseCalendars.add(new Pair<ProjectCalendar, Integer>(cal, Integer.valueOf(baseCalendarID)));
                     Integer resourceID = Integer.valueOf(MPPUtility.getInt(fixedData, offset + 8));
                     m_resourceMap.put(resourceID, cal);
                  }

                  cal.setUniqueID(calendarID);

                  if (varData != null)
                  {
                     processCalendarHours(varData, cal, baseCalendarID == -1);
                     processCalendarExceptions(varData, cal);
                  }

                  calendarMap.put(calendarID, cal);
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
    * NOTE: MPP12 defines the concept of working weeks. MPXJ does not
    * currently support this, and thus we only read the working hours
    * for the default working week.
    * 
    * @param data calendar data block
    * @param cal calendar instance
    * @param isBaseCalendar true if this is a base calendar
    */
   private void processCalendarHours(byte[] data, ProjectCalendar cal, boolean isBaseCalendar)
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
         defaultFlag = MPPUtility.getShort(data, offset);
         periodCount = MPPUtility.getShort(data, offset + 2);
         day = Day.getInstance(index + 1);

         if (defaultFlag == 1)
         {
            if (isBaseCalendar == true)
            {
               cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
               if (cal.isWorkingDay(day) == true)
               {
                  hours = cal.addCalendarHours(Day.getInstance(index + 1));
                  hours.addRange(new DateRange(ProjectCalendar.DEFAULT_START1, ProjectCalendar.DEFAULT_END1));
                  hours.addRange(new DateRange(ProjectCalendar.DEFAULT_START2, ProjectCalendar.DEFAULT_END2));
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
    * The way calendars are stored in an MPP12 file means that there
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
            cal.setBaseCalendar(baseCal);
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
      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      VarMeta taskVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data(taskVarMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData(taskFixedMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedData"))), 768, MINIMUM_EXPECTED_TASK_SIZE);
      FixedMeta taskFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Meta"))), 86);
      FixedData taskFixed2Data = new FixedData(taskFixed2Meta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Data"))));

      Props12 props = new Props12(getEncryptableInputStream(taskDir, "Props"));
      //System.out.println(taskFixedMeta);
      //System.out.println(taskFixedData);
      //System.out.println(taskVarMeta);
      //System.out.println(taskVarData);
      //System.out.println(taskFixed2Meta);
      //System.out.println(m_outlineCodeVarData.getVarMeta());
      //System.out.println(m_outlineCodeVarData);
      //System.out.println(props);

      // Process aliases      
      processTaskFieldNameAliases(props.getByteArray(TASK_FIELD_NAME_ALIASES));

      TreeMap<Integer, Integer> taskMap = createTaskMap(taskFixedMeta, taskFixedData);
      // The var data may not contain all the tasks as tasks with no var data assigned will
      // not be saved in there. Most notably these are tasks with no name. So use the task map
      // which contains all the tasks.
      Object[] uniqueid = taskMap.keySet().toArray(); //taskVarMeta.getUniqueIdentifierArray();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      byte[] metaData2;
      Task task;
      boolean autoWBS = true;
      LinkedList<Task> externalTasks = new LinkedList<Task>();
      RecurringTaskReader recurringTaskReader = null;
      RTFUtility rtf = new RTFUtility();
      String notes;

      for (int loop = 0; loop < uniqueid.length; loop++)
      {
         id = (Integer) uniqueid[loop];

         offset = taskMap.get(id);
         if (taskFixedData.isValidOffset(offset) == false)
         {
            continue;
         }

         data = taskFixedData.getByteArrayValue(offset.intValue());
         if (data.length == 16)
         {
            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(id);
            task.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());
            //System.out.println(task);
            continue;
         }

         if (data.length < MINIMUM_EXPECTED_TASK_SIZE)
         {
            continue;
         }

         if (data.length < MINIMUM_WORKING_TASK_SIZE)
         {
            byte[] newData = new byte[MINIMUM_WORKING_TASK_SIZE];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
         }

         //System.out.println (id+": "+MPPUtility.hexdump(data, false, 16, ""));

         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
         //System.out.println (MPPUtility.hexdump(data, false, 16, ""));
         //System.out.println (MPPUtility.hexdump(metaData, false, 16, ""));         
         //MPPUtility.dataDump(data, true, true, true, true, true, true, true);
         //MPPUtility.dataDump(metaData, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(taskVarData, id, true, true, true, true, true, true);

         metaData2 = taskFixed2Meta.getByteArrayValue(offset.intValue());
         byte[] data2 = taskFixed2Data.getByteArrayValue(offset.intValue());
         //System.out.println (MPPUtility.hexdump(metaData2, false, 16, ""));         
         //System.out.println (MPPUtility.hexdump(data2, false, 16, ""));

         byte[] recurringData = taskVarData.getByteArray(id, TASK_RECURRING_DATA);

         Task temp = m_file.getTaskByID(Integer.valueOf(MPPUtility.getInt(data, 4)));
         if (temp != null)
         {
            // Task with this id already exists... determine if this is the 'real' task by seeing
            // if this task has some var data. This is sort of hokey, but it's the best method i have
            // been able to see.
            if (!taskVarMeta.getUniqueIdentifierSet().contains(id))
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

         task.setActualCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 216) / 100));
         task.setActualDuration(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 66), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 64))));
         task.setActualFinish(MPPUtility.getTimestamp(data, 100));
         task.setActualOvertimeCost(NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_COST) / 100));
         task.setActualOvertimeWork(Duration.getInstance(taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_WORK) / 60000, TimeUnit.HOURS));
         task.setActualStart(MPPUtility.getTimestamp(data, 96));
         task.setActualWork(Duration.getInstance(MPPUtility.getDouble(data, 184) / 60000, TimeUnit.HOURS));
         //task.setACWP(); // Calculated value
         //task.setAssignment(); // Calculated value
         //task.setAssignmentDelay(); // Calculated value
         //task.setAssignmentUnits(); // Calculated value

         task.setBaselineCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 232) / 100));
         task.setBaselineDuration(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 74), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 78))));
         task.setBaselineFinish(MPPUtility.getTimestamp(data, 108));
         task.setBaselineStart(MPPUtility.getTimestamp(data, 104));
         task.setBaselineWork(Duration.getInstance(MPPUtility.getDouble(data, 176) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(1, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE1_COST) / 100));
         task.setBaselineDuration(1, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE1_DURATION, TASK_BASELINE1_DURATION_UNITS));
         task.setBaselineFinish(1, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE1_FINISH));
         task.setBaselineStart(1, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE1_START));
         task.setBaselineWork(1, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE1_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(2, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE2_COST) / 100));
         task.setBaselineDuration(2, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE2_DURATION, TASK_BASELINE2_DURATION_UNITS));
         task.setBaselineFinish(2, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE2_FINISH));
         task.setBaselineStart(2, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE2_START));
         task.setBaselineWork(2, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE2_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(3, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE3_COST) / 100));
         task.setBaselineDuration(3, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE3_DURATION, TASK_BASELINE3_DURATION_UNITS));
         task.setBaselineFinish(3, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE3_FINISH));
         task.setBaselineStart(3, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE3_START));
         task.setBaselineWork(3, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE3_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(4, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE4_COST) / 100));
         task.setBaselineDuration(4, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE4_DURATION, TASK_BASELINE4_DURATION_UNITS));
         task.setBaselineFinish(4, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE4_FINISH));
         task.setBaselineStart(4, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE4_START));
         task.setBaselineWork(4, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE4_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(5, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE5_COST) / 100));
         task.setBaselineDuration(5, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE5_DURATION, TASK_BASELINE5_DURATION_UNITS));
         task.setBaselineFinish(5, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE5_FINISH));
         task.setBaselineStart(5, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE5_START));
         task.setBaselineWork(5, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE5_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(6, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE6_COST) / 100));
         task.setBaselineDuration(6, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE6_DURATION, TASK_BASELINE6_DURATION_UNITS));
         task.setBaselineFinish(6, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE6_FINISH));
         task.setBaselineStart(6, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE6_START));
         task.setBaselineWork(6, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE6_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(7, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE7_COST) / 100));
         task.setBaselineDuration(7, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE7_DURATION, TASK_BASELINE7_DURATION_UNITS));
         task.setBaselineFinish(7, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE7_FINISH));
         task.setBaselineStart(7, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE7_START));
         task.setBaselineWork(7, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE7_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(8, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE8_COST) / 100));
         task.setBaselineDuration(8, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE8_DURATION, TASK_BASELINE8_DURATION_UNITS));
         task.setBaselineFinish(8, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE8_FINISH));
         task.setBaselineStart(8, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE8_START));
         task.setBaselineWork(8, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE8_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(9, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE9_COST) / 100));
         task.setBaselineDuration(9, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE9_DURATION, TASK_BASELINE9_DURATION_UNITS));
         task.setBaselineFinish(9, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE9_FINISH));
         task.setBaselineStart(9, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE9_START));
         task.setBaselineWork(9, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE9_WORK) / 60000, TimeUnit.HOURS));

         task.setBaselineCost(10, NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_BASELINE10_COST) / 100));
         task.setBaselineDuration(10, getCustomFieldDurationValue(taskVarData, id, TASK_BASELINE10_DURATION, TASK_BASELINE10_DURATION_UNITS));
         task.setBaselineFinish(10, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE10_FINISH));
         task.setBaselineStart(10, getCustomFieldTimestampValue(taskVarData, id, TASK_BASELINE10_START));
         task.setBaselineWork(10, Duration.getInstance(taskVarData.getDouble(id, TASK_BASELINE10_WORK) / 60000, TimeUnit.HOURS));

         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate(MPPUtility.getTimestamp(data, 112));
         task.setConstraintType(ConstraintType.getInstance(MPPUtility.getShort(data, 80)));
         task.setContact(taskVarData.getUnicodeString(id, TASK_CONTACT));
         task.setCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 200) / 100));
         //task.setCostRateTable(); // Calculated value
         //task.setCostVariance(); // Populated below
         task.setCost1(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST1) / 100));
         task.setCost2(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST2) / 100));
         task.setCost3(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST3) / 100));
         task.setCost4(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST4) / 100));
         task.setCost5(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST5) / 100));
         task.setCost6(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST6) / 100));
         task.setCost7(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST7) / 100));
         task.setCost8(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST8) / 100));
         task.setCost9(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST9) / 100));
         task.setCost10(NumberUtility.getDouble(getCustomFieldDoubleValue(taskVarData, id, TASK_COST10) / 100));

         // From MS Project 2003
         //         task.setCPI();

         task.setCreateDate(MPPUtility.getTimestamp(data, 130));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         //task.setCVPercent(); // Calculate value
         task.setDate1(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE1));
         task.setDate2(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE2));
         task.setDate3(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE3));
         task.setDate4(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE4));
         task.setDate5(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE5));
         task.setDate6(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE6));
         task.setDate7(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE7));
         task.setDate8(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE8));
         task.setDate9(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE9));
         task.setDate10(getCustomFieldTimestampValue(taskVarData, id, TASK_DATE10));
         task.setDeadline(MPPUtility.getTimestamp(data, 164));
         //task.setDelay(); // No longer supported by MS Project?
         task.setDuration(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 60), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 64))));
         //task.setDurationVariance(); // Calculated value
         task.setDuration1(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION1, TASK_DURATION1_UNITS));
         task.setDuration2(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION2, TASK_DURATION2_UNITS));
         task.setDuration3(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION3, TASK_DURATION3_UNITS));
         task.setDuration4(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION4, TASK_DURATION4_UNITS));
         task.setDuration5(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION5, TASK_DURATION5_UNITS));
         task.setDuration6(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION6, TASK_DURATION6_UNITS));
         task.setDuration7(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION7, TASK_DURATION7_UNITS));
         task.setDuration8(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION8, TASK_DURATION8_UNITS));
         task.setDuration9(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION9, TASK_DURATION9_UNITS));
         task.setDuration10(getCustomFieldDurationValue(taskVarData, id, TASK_DURATION10, TASK_DURATION10_UNITS));
         //       From MS Project 2003
         //         task.setEAC();
         task.setEarlyFinish(MPPUtility.getTimestamp(data, 8));
         task.setEarlyStart(MPPUtility.getTimestamp(data, 88));
         //       From MS Project 2003
         //         task.setEarnedValueMethod();
         task.setEffortDriven((metaData[11] & 0x10) != 0);
         task.setEstimated(getDurationEstimated(MPPUtility.getShort(data, 64)));
         task.setExpanded(((metaData[12] & 0x02) == 0));
         int externalTaskID = taskVarData.getInt(id, TASK_EXTERNAL_TASK_ID);
         if (externalTaskID != 0)
         {
            task.setExternalTaskID(Integer.valueOf(externalTaskID));
            task.setExternalTask(true);
            externalTasks.add(task);
         }
         task.setFinish(MPPUtility.getTimestamp(data, 8));
         //       From MS Project 2003
         //task.setFinishVariance(); // Calculated value
         task.setFinish1(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH1));
         task.setFinish2(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH2));
         task.setFinish3(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH3));
         task.setFinish4(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH4));
         task.setFinish5(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH5));
         task.setFinish6(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH6));
         task.setFinish7(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH7));
         task.setFinish8(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH8));
         task.setFinish9(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH9));
         task.setFinish10(getCustomFieldTimestampValue(taskVarData, id, TASK_FINISH10));

         task.setFixedCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 208) / 100));
         task.setFixedCostAccrual(AccrueType.getInstance(MPPUtility.getShort(data, 128)));

         task.setFlag1((metaData[37] & 0x20) != 0);
         task.setFlag2((metaData[37] & 0x40) != 0);
         task.setFlag3((metaData[37] & 0x80) != 0);
         task.setFlag4((metaData[38] & 0x01) != 0);
         task.setFlag5((metaData[38] & 0x02) != 0);
         task.setFlag6((metaData[38] & 0x04) != 0);
         task.setFlag7((metaData[38] & 0x08) != 0);
         task.setFlag8((metaData[38] & 0x10) != 0);
         task.setFlag9((metaData[38] & 0x20) != 0);
         task.setFlag10((metaData[38] & 0x40) != 0);
         task.setFlag11((metaData[38] & 0x80) != 0);
         task.setFlag12((metaData[39] & 0x01) != 0);
         task.setFlag13((metaData[39] & 0x02) != 0);
         task.setFlag14((metaData[39] & 0x04) != 0);
         task.setFlag15((metaData[39] & 0x08) != 0);
         task.setFlag16((metaData[39] & 0x10) != 0);
         task.setFlag17((metaData[39] & 0x20) != 0);
         task.setFlag18((metaData[39] & 0x40) != 0);
         task.setFlag19((metaData[39] & 0x80) != 0);
         task.setFlag20((metaData[40] & 0x01) != 0);
         task.setFreeSlack(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 24), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 64))));
         //       From MS Project 2003
         //         task.setGroupBySummary();
         task.setGUID(MPPUtility.getGUID(data2, 0));
         task.setHideBar((metaData[10] & 0x80) != 0);
         processHyperlinkData(task, taskVarData.getByteArray(id, TASK_HYPERLINK));
         task.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));
         //       From MS Project 2003
         task.setIgnoreResourceCalendar((metaData[10] & 0x02) != 0);
         //task.setIndicators(); // Calculated value
         task.setLateFinish(MPPUtility.getTimestamp(data, 152));
         task.setLateStart(MPPUtility.getTimestamp(data, 12));
         task.setLevelAssignments((metaData[13] & 0x04) != 0);
         task.setLevelingCanSplit((metaData[13] & 0x02) != 0);
         task.setLevelingDelay(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 82), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 86))));
         //task.setLinkedFields();  // Calculated value
         task.setMarked((metaData[9] & 0x40) != 0);
         task.setMilestone((metaData[8] & 0x20) != 0);
         task.setName(taskVarData.getUnicodeString(id, TASK_NAME));

         task.setNumber1(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER1)));
         task.setNumber2(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER2)));
         task.setNumber3(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER3)));
         task.setNumber4(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER4)));
         task.setNumber5(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER5)));
         task.setNumber6(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER6)));
         task.setNumber7(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER7)));
         task.setNumber8(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER8)));
         task.setNumber9(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER9)));
         task.setNumber10(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER10)));
         task.setNumber11(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER11)));
         task.setNumber12(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER12)));
         task.setNumber13(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER13)));
         task.setNumber14(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER14)));
         task.setNumber15(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER15)));
         task.setNumber16(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER16)));
         task.setNumber17(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER17)));
         task.setNumber18(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER18)));
         task.setNumber19(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER19)));
         task.setNumber20(Double.valueOf(getCustomFieldDoubleValue(taskVarData, id, TASK_NUMBER20)));
         //task.setObjects(); // Calculated value
         task.setOutlineCode1(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE1));
         task.setOutlineCode2(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE2));
         task.setOutlineCode3(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE3));
         task.setOutlineCode4(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE4));
         task.setOutlineCode5(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE5));
         task.setOutlineCode6(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE6));
         task.setOutlineCode7(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE7));
         task.setOutlineCode8(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE8));
         task.setOutlineCode9(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE9));
         task.setOutlineCode10(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, TASK_OUTLINECODE10));
         task.setOutlineLevel(Integer.valueOf(MPPUtility.getShort(data, 40)));
         //task.setOutlineNumber(); // Calculated value
         //task.setOverallocated(); // Calculated value
         task.setOvertimeCost(NumberUtility.getDouble(taskVarData.getDouble(id, TASK_OVERTIME_COST) / 100));
         //task.setOvertimeWork(); // Calculated value?
         //task.getPredecessors(); // Calculated value
         task.setPercentageComplete(MPPUtility.getPercentage(data, 122));
         task.setPercentageWorkComplete(MPPUtility.getPercentage(data, 124));
         //       From MS Project 2003
         //         task.setPhysicalPercentComplete();
         task.setPreleveledFinish(MPPUtility.getTimestamp(data, 140));
         task.setPreleveledStart(MPPUtility.getTimestamp(data, 136));
         task.setPriority(Priority.getInstance(MPPUtility.getShort(data, 120)));
         //task.setProject(); // Calculated value
         task.setRecurring(MPPUtility.getShort(data, 134) != 0);
         //task.setRegularWork(); // Calculated value
         task.setRemainingCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 224) / 100));
         task.setRemainingDuration(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 70), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 64))));
         task.setRemainingOvertimeCost(NumberUtility.getDouble(taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_COST) / 100));
         task.setRemainingOvertimeWork(Duration.getInstance(taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_WORK) / 60000, TimeUnit.HOURS));
         task.setRemainingWork(Duration.getInstance(MPPUtility.getDouble(data, 192) / 60000, TimeUnit.HOURS));
         //task.setResourceGroup(); // Calculated value from resource
         //task.setResourceInitials(); // Calculated value from resource
         //task.setResourceNames(); // Calculated value from resource
         //task.setResourcePhonetics(); // Calculated value from resource
         //       From MS Project 2003
         //         task.setResourceType();
         //task.setResponsePending(); // Calculated value
         task.setResume(MPPUtility.getTimestamp(data, 20));
         //task.setResumeNoEarlierThan(); // No mapping in MSP2K?
         task.setRollup((metaData[10] & 0x08) != 0);
         //       From MS Project 2003
         //         task.setSPI();
         task.setStart(MPPUtility.getTimestamp(data, 88));
         //       From MS Project 2003
         task.setStartSlack(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 28), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 64))));
         //task.setStartVariance(); // Calculated value
         task.setStart1(getCustomFieldTimestampValue(taskVarData, id, TASK_START1));
         task.setStart2(getCustomFieldTimestampValue(taskVarData, id, TASK_START2));
         task.setStart3(getCustomFieldTimestampValue(taskVarData, id, TASK_START3));
         task.setStart4(getCustomFieldTimestampValue(taskVarData, id, TASK_START4));
         task.setStart5(getCustomFieldTimestampValue(taskVarData, id, TASK_START5));
         task.setStart6(getCustomFieldTimestampValue(taskVarData, id, TASK_START6));
         task.setStart7(getCustomFieldTimestampValue(taskVarData, id, TASK_START7));
         task.setStart8(getCustomFieldTimestampValue(taskVarData, id, TASK_START8));
         task.setStart9(getCustomFieldTimestampValue(taskVarData, id, TASK_START9));
         task.setStart10(getCustomFieldTimestampValue(taskVarData, id, TASK_START10));
         //       From MS Project 2003
         //         task.setStatus();
         //         task.setStatusIndicator();
         task.setStop(MPPUtility.getTimestamp(data, 16));
         //task.setSubprojectFile();
         //task.setSubprojectReadOnly();
         task.setSubprojectTasksUniqueIDOffset(Integer.valueOf(taskVarData.getInt(id, TASK_SUBPROJECT_TASKS_UNIQUEID_OFFSET)));
         task.setSubprojectTaskUniqueID(Integer.valueOf(taskVarData.getInt(id, TASK_SUBPROJECTUNIQUETASKID)));
         task.setSubprojectTaskID(Integer.valueOf(taskVarData.getInt(id, TASK_SUBPROJECTTASKID)));
         //task.setSuccessors(); // Calculated value
         //task.setSummary(); // Automatically generated by MPXJ
         task.setSummaryProgress(MPPUtility.getTimestamp(data, 156));
         //task.setSV(); // Calculated value
         //       From MS Project 2003
         //         task.setSVPercent();
         //         task.setTCPI();
         //task.setTeamStatusPending(); // Calculated value
         task.setText1(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT1));
         task.setText2(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT2));
         task.setText3(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT3));
         task.setText4(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT4));
         task.setText5(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT5));
         task.setText6(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT6));
         task.setText7(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT7));
         task.setText8(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT8));
         task.setText9(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT9));
         task.setText10(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT10));
         task.setText11(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT11));
         task.setText12(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT12));
         task.setText13(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT13));
         task.setText14(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT14));
         task.setText15(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT15));
         task.setText16(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT16));
         task.setText17(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT17));
         task.setText18(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT18));
         task.setText19(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT19));
         task.setText20(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT20));
         task.setText21(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT21));
         task.setText22(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT22));
         task.setText23(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT23));
         task.setText24(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT24));
         task.setText25(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT25));
         task.setText26(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT26));
         task.setText27(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT27));
         task.setText28(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT28));
         task.setText29(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT29));
         task.setText30(getCustomFieldUnicodeStringValue(taskVarData, id, TASK_TEXT30));
         //task.setTotalSlack(); // Calculated value
         task.setType(TaskType.getInstance(MPPUtility.getShort(data, 126)));
         task.setUniqueID(Integer.valueOf(MPPUtility.getInt(data, 0)));
         //task.setUniqueIDPredecessors(); // Calculated value
         //task.setUniqueIDSuccessors(); // Calculated value
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskVarData.getUnicodeString(id, TASK_WBS));
         //task.setWBSPredecessors(); // Calculated value
         //task.setWBSSuccessors(); // Calculated value
         task.setWork(Duration.getInstance(MPPUtility.getDouble(data, 168) / 60000, TimeUnit.HOURS));
         //task.setWorkContour(); // Calculated from resource
         //task.setWorkVariance(); // Calculated value

         task.setFinishSlack(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 32), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 64))));

         m_parentTasks.put(task.getUniqueID(), Integer.valueOf(MPPUtility.getInt(data, 36)));

         switch (task.getConstraintType())
         {
            //
            // Adjust the start and finish dates if the task
            // is constrained to start as late as possible.
            //            
            case AS_LATE_AS_POSSIBLE :
            {
               if (DateUtility.compare(task.getStart(), task.getLateStart()) < 0)
               {
                  task.setStart(task.getLateStart());
               }
               if (DateUtility.compare(task.getFinish(), task.getLateFinish()) < 0)
               {
                  task.setFinish(task.getLateFinish());
               }
               break;
            }

            case START_NO_LATER_THAN :
            case FINISH_NO_LATER_THAN :
            {
               if (DateUtility.compare(task.getFinish(), task.getStart()) < 0)
               {
                  task.setFinish(task.getLateFinish());
               }
               break;
            }

            default :
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
               recurringTaskReader = new RecurringTaskReader(m_file);
            }
            recurringTaskReader.processRecurringTask(task, recurringData);
         }

         //
         // Retrieve the task notes.
         //
         notes = taskVarData.getString(id, TASK_NOTES);
         if (notes != null)
         {
            if (m_reader.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }

            task.setNotes(notes);
         }

         //
         // Set the calendar name
         //
         int calendarID = MPPUtility.getInt(data, 160);
         if (calendarID != -1)
         {
            ProjectCalendar calendar = m_file.getBaseCalendarByUniqueID(Integer.valueOf(calendarID));
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
         // Process any enterprise columns
         //
         processTaskEnterpriseColumns(id, task, taskVarData, metaData2);

         // Unfortunately it looks like 'null' tasks sometimes make it through. So let's check for to see if we
         // need to mark this task as a null task after all.
         if (task.getName() == null && ((task.getStart() == null || task.getStart().getTime() == MPPUtility.getEpochDate().getTime()) || (task.getFinish() == null || task.getFinish().getTime() == MPPUtility.getEpochDate().getTime()) || (task.getCreateDate() == null || task.getCreateDate().getTime() == MPPUtility.getEpochDate().getTime())))
         {
            // Remove this to avoid passing bad data to the client
            m_file.removeTask(task);

            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(id);
            task.setID(new Integer(MPPUtility.getInt(data, 4)));
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());
            //System.out.println(task);
            continue;
         }

         if (data2.length < 24)
         {
            m_nullTaskOrder.put(task.getID(), task.getUniqueID());
         }
         else
         {
            Long key = Long.valueOf(MPPUtility.getLong(data2, 16));
            m_taskOrder.put(key, task.getUniqueID());
         }

         m_file.fireTaskReadEvent(task);
         //dumpUnknownData(task.getUniqueID().toString(), UNKNOWN_TASK_DATA, data);
         //System.out.println(task);
      }

      //
      // Enable auto WBS if necessary
      //
      m_file.setAutoWBS(autoWBS);

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
    * This method uses ordering data embedded in the file to reconstruct
    * the correct ID order of the tasks.
    */
   private void fixTaskOrder()
   {
      //
      // Renumber ID values using a large increment to allow
      // space for later inserts.
      //
      TreeMap<Integer, Integer> taskMap = new TreeMap<Integer, Integer>();
      int nextIDIncrement = 1000;
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
         int targetIDValue = entry.getKey().intValue();
         targetIDValue = (targetIDValue - insertionCount) * nextIDIncrement;
         ++insertionCount;
         while (taskMap.containsKey(Integer.valueOf(targetIDValue)))
         {
            --targetIDValue;
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
      task.setEnterpriseCost(1, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST1) / 100));
      task.setEnterpriseCost(2, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST2) / 100));
      task.setEnterpriseCost(3, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST3) / 100));
      task.setEnterpriseCost(4, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST4) / 100));
      task.setEnterpriseCost(5, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST5) / 100));
      task.setEnterpriseCost(6, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST6) / 100));
      task.setEnterpriseCost(7, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST7) / 100));
      task.setEnterpriseCost(8, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST8) / 100));
      task.setEnterpriseCost(9, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST9) / 100));
      task.setEnterpriseCost(10, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_COST10) / 100));

      task.setEnterpriseDate(1, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE1));
      task.setEnterpriseDate(2, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE2));
      task.setEnterpriseDate(3, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE3));
      task.setEnterpriseDate(4, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE4));
      task.setEnterpriseDate(5, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE5));
      task.setEnterpriseDate(6, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE6));
      task.setEnterpriseDate(7, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE7));
      task.setEnterpriseDate(8, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE8));
      task.setEnterpriseDate(9, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE9));
      task.setEnterpriseDate(10, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE10));
      task.setEnterpriseDate(11, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE11));
      task.setEnterpriseDate(12, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE12));
      task.setEnterpriseDate(13, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE13));
      task.setEnterpriseDate(14, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE14));
      task.setEnterpriseDate(15, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE15));
      task.setEnterpriseDate(16, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE16));
      task.setEnterpriseDate(17, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE17));
      task.setEnterpriseDate(18, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE18));
      task.setEnterpriseDate(19, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE19));
      task.setEnterpriseDate(20, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE20));
      task.setEnterpriseDate(21, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE21));
      task.setEnterpriseDate(22, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE22));
      task.setEnterpriseDate(23, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE23));
      task.setEnterpriseDate(24, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE24));
      task.setEnterpriseDate(25, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE25));
      task.setEnterpriseDate(26, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE26));
      task.setEnterpriseDate(27, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE27));
      task.setEnterpriseDate(28, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE28));
      task.setEnterpriseDate(29, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE29));
      task.setEnterpriseDate(30, taskVarData.getTimestamp(id, TASK_ENTERPRISE_DATE30));

      task.setEnterpriseDuration(1, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION1), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION1_UNITS))));
      task.setEnterpriseDuration(2, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION2), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION2_UNITS))));
      task.setEnterpriseDuration(3, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION3), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION3_UNITS))));
      task.setEnterpriseDuration(4, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION4), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION4_UNITS))));
      task.setEnterpriseDuration(5, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION5), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION5_UNITS))));
      task.setEnterpriseDuration(6, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION6), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION6_UNITS))));
      task.setEnterpriseDuration(7, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION7), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION7_UNITS))));
      task.setEnterpriseDuration(8, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION8), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION8_UNITS))));
      task.setEnterpriseDuration(9, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION9), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION9_UNITS))));
      task.setEnterpriseDuration(10, MPPUtility.getAdjustedDuration(m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION10), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION10_UNITS))));

      task.setEnterpriseNumber(1, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER1)));
      task.setEnterpriseNumber(2, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER2)));
      task.setEnterpriseNumber(3, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER3)));
      task.setEnterpriseNumber(4, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER4)));
      task.setEnterpriseNumber(5, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER5)));
      task.setEnterpriseNumber(6, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER6)));
      task.setEnterpriseNumber(7, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER7)));
      task.setEnterpriseNumber(8, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER8)));
      task.setEnterpriseNumber(9, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER9)));
      task.setEnterpriseNumber(10, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER10)));
      task.setEnterpriseNumber(11, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER11)));
      task.setEnterpriseNumber(12, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER12)));
      task.setEnterpriseNumber(13, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER13)));
      task.setEnterpriseNumber(14, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER14)));
      task.setEnterpriseNumber(15, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER15)));
      task.setEnterpriseNumber(16, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER16)));
      task.setEnterpriseNumber(17, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER17)));
      task.setEnterpriseNumber(18, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER18)));
      task.setEnterpriseNumber(19, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER19)));
      task.setEnterpriseNumber(20, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER20)));
      task.setEnterpriseNumber(21, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER21)));
      task.setEnterpriseNumber(22, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER22)));
      task.setEnterpriseNumber(23, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER23)));
      task.setEnterpriseNumber(24, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER24)));
      task.setEnterpriseNumber(25, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER25)));
      task.setEnterpriseNumber(26, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER26)));
      task.setEnterpriseNumber(27, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER27)));
      task.setEnterpriseNumber(28, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER28)));
      task.setEnterpriseNumber(29, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER29)));
      task.setEnterpriseNumber(30, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER30)));
      task.setEnterpriseNumber(31, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER31)));
      task.setEnterpriseNumber(32, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER32)));
      task.setEnterpriseNumber(33, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER33)));
      task.setEnterpriseNumber(34, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER34)));
      task.setEnterpriseNumber(35, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER35)));
      task.setEnterpriseNumber(36, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER36)));
      task.setEnterpriseNumber(37, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER37)));
      task.setEnterpriseNumber(38, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER38)));
      task.setEnterpriseNumber(39, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER39)));
      task.setEnterpriseNumber(40, NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER40)));

      task.setEnterpriseText(1, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT1));
      task.setEnterpriseText(2, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT2));
      task.setEnterpriseText(3, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT3));
      task.setEnterpriseText(4, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT4));
      task.setEnterpriseText(5, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT5));
      task.setEnterpriseText(6, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT6));
      task.setEnterpriseText(7, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT7));
      task.setEnterpriseText(8, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT8));
      task.setEnterpriseText(9, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT9));
      task.setEnterpriseText(10, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT10));
      task.setEnterpriseText(11, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT11));
      task.setEnterpriseText(12, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT12));
      task.setEnterpriseText(13, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT13));
      task.setEnterpriseText(14, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT14));
      task.setEnterpriseText(15, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT15));
      task.setEnterpriseText(16, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT16));
      task.setEnterpriseText(17, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT17));
      task.setEnterpriseText(18, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT18));
      task.setEnterpriseText(19, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT19));
      task.setEnterpriseText(20, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT20));
      task.setEnterpriseText(21, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT21));
      task.setEnterpriseText(22, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT22));
      task.setEnterpriseText(23, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT23));
      task.setEnterpriseText(24, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT24));
      task.setEnterpriseText(25, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT25));
      task.setEnterpriseText(26, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT26));
      task.setEnterpriseText(27, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT27));
      task.setEnterpriseText(28, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT28));
      task.setEnterpriseText(29, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT29));
      task.setEnterpriseText(30, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT30));
      task.setEnterpriseText(31, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT31));
      task.setEnterpriseText(32, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT32));
      task.setEnterpriseText(33, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT33));
      task.setEnterpriseText(34, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT34));
      task.setEnterpriseText(35, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT35));
      task.setEnterpriseText(36, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT36));
      task.setEnterpriseText(37, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT37));
      task.setEnterpriseText(38, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT38));
      task.setEnterpriseText(39, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT39));
      task.setEnterpriseText(40, taskVarData.getUnicodeString(id, TASK_ENTERPRISE_TEXT40));

      if (metaData2 != null)
      {
         int bits = MPPUtility.getInt(metaData2, 59);
         task.set(TaskField.ENTERPRISE_FLAG1, Boolean.valueOf((bits & 0x00001) != 0));
         task.set(TaskField.ENTERPRISE_FLAG2, Boolean.valueOf((bits & 0x00002) != 0));
         task.set(TaskField.ENTERPRISE_FLAG3, Boolean.valueOf((bits & 0x00004) != 0));
         task.set(TaskField.ENTERPRISE_FLAG4, Boolean.valueOf((bits & 0x00008) != 0));
         task.set(TaskField.ENTERPRISE_FLAG5, Boolean.valueOf((bits & 0x00010) != 0));
         task.set(TaskField.ENTERPRISE_FLAG6, Boolean.valueOf((bits & 0x00020) != 0));
         task.set(TaskField.ENTERPRISE_FLAG7, Boolean.valueOf((bits & 0x00040) != 0));
         task.set(TaskField.ENTERPRISE_FLAG8, Boolean.valueOf((bits & 0x00080) != 0));
         task.set(TaskField.ENTERPRISE_FLAG9, Boolean.valueOf((bits & 0x00100) != 0));
         task.set(TaskField.ENTERPRISE_FLAG10, Boolean.valueOf((bits & 0x00200) != 0));
         task.set(TaskField.ENTERPRISE_FLAG11, Boolean.valueOf((bits & 0x00400) != 0));
         task.set(TaskField.ENTERPRISE_FLAG12, Boolean.valueOf((bits & 0x00800) != 0));
         task.set(TaskField.ENTERPRISE_FLAG13, Boolean.valueOf((bits & 0x01000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG14, Boolean.valueOf((bits & 0x02000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG15, Boolean.valueOf((bits & 0x04000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG16, Boolean.valueOf((bits & 0x08000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG17, Boolean.valueOf((bits & 0x10000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG18, Boolean.valueOf((bits & 0x20000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG19, Boolean.valueOf((bits & 0x40000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG20, Boolean.valueOf((bits & 0x80000) != 0));
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
      resource.setEnterpriseCost(1, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST1) / 100));
      resource.setEnterpriseCost(2, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST2) / 100));
      resource.setEnterpriseCost(3, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST3) / 100));
      resource.setEnterpriseCost(4, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST4) / 100));
      resource.setEnterpriseCost(5, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST5) / 100));
      resource.setEnterpriseCost(6, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST6) / 100));
      resource.setEnterpriseCost(7, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST7) / 100));
      resource.setEnterpriseCost(8, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST8) / 100));
      resource.setEnterpriseCost(9, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST9) / 100));
      resource.setEnterpriseCost(10, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_COST10) / 100));

      resource.setEnterpriseDate(1, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE1));
      resource.setEnterpriseDate(2, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE2));
      resource.setEnterpriseDate(3, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE3));
      resource.setEnterpriseDate(4, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE4));
      resource.setEnterpriseDate(5, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE5));
      resource.setEnterpriseDate(6, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE6));
      resource.setEnterpriseDate(7, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE7));
      resource.setEnterpriseDate(8, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE8));
      resource.setEnterpriseDate(9, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE9));
      resource.setEnterpriseDate(10, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE10));
      resource.setEnterpriseDate(11, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE11));
      resource.setEnterpriseDate(12, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE12));
      resource.setEnterpriseDate(13, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE13));
      resource.setEnterpriseDate(14, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE14));
      resource.setEnterpriseDate(15, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE15));
      resource.setEnterpriseDate(16, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE16));
      resource.setEnterpriseDate(17, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE17));
      resource.setEnterpriseDate(18, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE18));
      resource.setEnterpriseDate(19, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE19));
      resource.setEnterpriseDate(20, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE20));
      resource.setEnterpriseDate(21, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE21));
      resource.setEnterpriseDate(22, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE22));
      resource.setEnterpriseDate(23, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE23));
      resource.setEnterpriseDate(24, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE24));
      resource.setEnterpriseDate(25, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE25));
      resource.setEnterpriseDate(26, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE26));
      resource.setEnterpriseDate(27, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE27));
      resource.setEnterpriseDate(28, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE28));
      resource.setEnterpriseDate(29, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE29));
      resource.setEnterpriseDate(30, resourceVarData.getTimestamp(id, RESOURCE_ENTERPRISE_DATE30));

      resource.setEnterpriseDuration(1, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION1), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION1_UNITS))));
      resource.setEnterpriseDuration(2, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION2), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION2_UNITS))));
      resource.setEnterpriseDuration(3, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION3), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION3_UNITS))));
      resource.setEnterpriseDuration(4, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION4), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION4_UNITS))));
      resource.setEnterpriseDuration(5, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION5), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION5_UNITS))));
      resource.setEnterpriseDuration(6, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION6), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION6_UNITS))));
      resource.setEnterpriseDuration(7, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION7), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION7_UNITS))));
      resource.setEnterpriseDuration(8, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION8), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION8_UNITS))));
      resource.setEnterpriseDuration(9, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION9), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION9_UNITS))));
      resource.setEnterpriseDuration(10, MPPUtility.getAdjustedDuration(m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION10), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION10_UNITS))));

      resource.setEnterpriseNumber(1, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER1)));
      resource.setEnterpriseNumber(2, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER2)));
      resource.setEnterpriseNumber(3, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER3)));
      resource.setEnterpriseNumber(4, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER4)));
      resource.setEnterpriseNumber(5, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER5)));
      resource.setEnterpriseNumber(6, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER6)));
      resource.setEnterpriseNumber(7, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER7)));
      resource.setEnterpriseNumber(8, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER8)));
      resource.setEnterpriseNumber(9, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER9)));
      resource.setEnterpriseNumber(10, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER10)));
      resource.setEnterpriseNumber(11, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER11)));
      resource.setEnterpriseNumber(12, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER12)));
      resource.setEnterpriseNumber(13, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER13)));
      resource.setEnterpriseNumber(14, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER14)));
      resource.setEnterpriseNumber(15, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER15)));
      resource.setEnterpriseNumber(16, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER16)));
      resource.setEnterpriseNumber(17, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER17)));
      resource.setEnterpriseNumber(18, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER18)));
      resource.setEnterpriseNumber(19, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER19)));
      resource.setEnterpriseNumber(20, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER20)));
      resource.setEnterpriseNumber(21, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER21)));
      resource.setEnterpriseNumber(22, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER22)));
      resource.setEnterpriseNumber(23, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER23)));
      resource.setEnterpriseNumber(24, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER24)));
      resource.setEnterpriseNumber(25, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER25)));
      resource.setEnterpriseNumber(26, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER26)));
      resource.setEnterpriseNumber(27, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER27)));
      resource.setEnterpriseNumber(28, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER28)));
      resource.setEnterpriseNumber(29, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER29)));
      resource.setEnterpriseNumber(30, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER30)));
      resource.setEnterpriseNumber(31, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER31)));
      resource.setEnterpriseNumber(32, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER32)));
      resource.setEnterpriseNumber(33, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER33)));
      resource.setEnterpriseNumber(34, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER34)));
      resource.setEnterpriseNumber(35, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER35)));
      resource.setEnterpriseNumber(36, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER36)));
      resource.setEnterpriseNumber(37, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER37)));
      resource.setEnterpriseNumber(38, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER38)));
      resource.setEnterpriseNumber(39, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER39)));
      resource.setEnterpriseNumber(40, NumberUtility.getDouble(resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER40)));

      resource.setEnterpriseText(1, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT1));
      resource.setEnterpriseText(2, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT2));
      resource.setEnterpriseText(3, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT3));
      resource.setEnterpriseText(4, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT4));
      resource.setEnterpriseText(5, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT5));
      resource.setEnterpriseText(6, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT6));
      resource.setEnterpriseText(7, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT7));
      resource.setEnterpriseText(8, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT8));
      resource.setEnterpriseText(9, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT9));
      resource.setEnterpriseText(10, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT10));
      resource.setEnterpriseText(11, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT11));
      resource.setEnterpriseText(12, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT12));
      resource.setEnterpriseText(13, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT13));
      resource.setEnterpriseText(14, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT14));
      resource.setEnterpriseText(15, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT15));
      resource.setEnterpriseText(16, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT16));
      resource.setEnterpriseText(17, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT17));
      resource.setEnterpriseText(18, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT18));
      resource.setEnterpriseText(19, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT19));
      resource.setEnterpriseText(20, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT20));
      resource.setEnterpriseText(21, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT21));
      resource.setEnterpriseText(22, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT22));
      resource.setEnterpriseText(23, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT23));
      resource.setEnterpriseText(24, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT24));
      resource.setEnterpriseText(25, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT25));
      resource.setEnterpriseText(26, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT26));
      resource.setEnterpriseText(27, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT27));
      resource.setEnterpriseText(28, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT28));
      resource.setEnterpriseText(29, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT29));
      resource.setEnterpriseText(30, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT30));
      resource.setEnterpriseText(31, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT31));
      resource.setEnterpriseText(32, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT32));
      resource.setEnterpriseText(33, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT33));
      resource.setEnterpriseText(34, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT34));
      resource.setEnterpriseText(35, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT35));
      resource.setEnterpriseText(36, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT36));
      resource.setEnterpriseText(37, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT37));
      resource.setEnterpriseText(38, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT38));
      resource.setEnterpriseText(39, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT39));
      resource.setEnterpriseText(40, resourceVarData.getUnicodeString(id, RESOURCE_ENTERPRISE_TEXT40));

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
      DirectoryEntry consDir;
      try
      {
         consDir = (DirectoryEntry) m_projectDir.getEntry("TBkndCons");
      }

      catch (FileNotFoundException ex)
      {
         consDir = null;
      }

      if (consDir != null)
      {
         FixedMeta consFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) consDir.getEntry("FixedMeta"))), 10);
         FixedData consFixedData = new FixedData(consFixedMeta, 20, getEncryptableInputStream(consDir, "FixedData"));
         int count = consFixedMeta.getItemCount();
         int lastConstraintID = -1;

         for (int loop = 0; loop < count; loop++)
         {
            byte[] metaData = consFixedMeta.getByteArrayValue(loop);

            //
            // SourceForge bug 2209477: we were reading an int here, but
            // it looks like the deleted flag is just a short.
            //
            if (MPPUtility.getShort(metaData, 0) == 0)
            {
               int index = consFixedData.getIndexFromOffset(MPPUtility.getInt(metaData, 4));
               if (index != -1)
               {
                  byte[] data = consFixedData.getByteArrayValue(index);
                  int constraintID = MPPUtility.getInt(data, 0);
                  if (constraintID > lastConstraintID)
                  {
                     lastConstraintID = constraintID;
                     int taskID1 = MPPUtility.getInt(data, 4);
                     int taskID2 = MPPUtility.getInt(data, 8);

                     if (taskID1 != taskID2)
                     {
                        Task task1 = m_file.getTaskByUniqueID(Integer.valueOf(taskID1));
                        Task task2 = m_file.getTaskByUniqueID(Integer.valueOf(taskID2));

                        if (task1 != null && task2 != null)
                        {
                           RelationType type = RelationType.getInstance(MPPUtility.getShort(data, 12));
                           TimeUnit durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, 14));
                           Duration lag = MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(data, 16), durationUnits);
                           task2.addPredecessor(task1, type, lag);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * This method extracts and collates resource data.
    *
    * @throws java.io.IOException
    */
   private void processResourceData() throws IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry) m_projectDir.getEntry("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data(rscVarMeta, new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData(rscFixedMeta, getEncryptableInputStream(rscDir, "FixedData"));
      FixedMeta rscFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Fixed2Meta"))), 49);
      FixedData rscFixed2Data = new FixedData(rscFixed2Meta, getEncryptableInputStream(rscDir, "Fixed2Data"));
      Props12 props = new Props12(getEncryptableInputStream(rscDir, "Props"));
      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      //System.out.println(rscFixedMeta);
      //System.out.println(rscFixedData);
      //System.out.println(rscFixed2Meta);
      //System.out.println(rscFixed2Data);
      //System.out.println(props);

      // Process aliases      
      processResourceFieldNameAliases(props.getByteArray(RESOURCE_FIELD_NAME_ALIASES));

      TreeMap<Integer, Integer> resourceMap = createResourceMap(rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifierArray();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Resource resource;

      RTFUtility rtf = new RTFUtility();
      String notes;

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

         resource.setAccrueAt(AccrueType.getInstance(MPPUtility.getShort(data, 12)));
         resource.setActualCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 132) / 100));
         resource.setActualOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 172) / 100));
         resource.setActualOvertimeWork(Duration.getInstance(MPPUtility.getDouble(data, 108) / 60000, TimeUnit.HOURS));
         resource.setActualWork(Duration.getInstance(MPPUtility.getDouble(data, 60) / 60000, TimeUnit.HOURS));
         resource.setAvailableFrom(MPPUtility.getTimestamp(data, 20));
         resource.setAvailableTo(MPPUtility.getTimestamp(data, 24));
         //resource.setBaseCalendar();

         resource.setBaselineCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 148) / 100));
         resource.setBaselineWork(Duration.getInstance(MPPUtility.getDouble(data, 68) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(1, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE1_COST) / 100));
         resource.setBaselineWork(1, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE1_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(2, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE2_COST) / 100));
         resource.setBaselineWork(2, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE2_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(3, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE3_COST) / 100));
         resource.setBaselineWork(3, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE3_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(4, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE4_COST) / 100));
         resource.setBaselineWork(4, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE4_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(5, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE5_COST) / 100));
         resource.setBaselineWork(5, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE5_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(6, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE6_COST) / 100));
         resource.setBaselineWork(6, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE6_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(7, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE7_COST) / 100));
         resource.setBaselineWork(7, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE7_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(8, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE8_COST) / 100));
         resource.setBaselineWork(8, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE8_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(9, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE9_COST) / 100));
         resource.setBaselineWork(9, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE9_WORK) / 60000, TimeUnit.HOURS));

         resource.setBaselineCost(10, NumberUtility.getDouble(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_BASELINE10_COST) / 100));
         resource.setBaselineWork(10, Duration.getInstance(rscVarData.getDouble(id, RESOURCE_BASELINE10_WORK) / 60000, TimeUnit.HOURS));

         resource.setBudget((metaData2[8] & 0x20) != 0);
         resource.setCode(rscVarData.getUnicodeString(id, RESOURCE_CODE));
         resource.setCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 140) / 100));

         resource.setCost1(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST1) / 100));
         resource.setCost2(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST2) / 100));
         resource.setCost3(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST3) / 100));
         resource.setCost4(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST4) / 100));
         resource.setCost5(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST5) / 100));
         resource.setCost6(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST6) / 100));
         resource.setCost7(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST7) / 100));
         resource.setCost8(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST8) / 100));
         resource.setCost9(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST9) / 100));
         resource.setCost10(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_COST10) / 100));

         resource.setCostPerUse(NumberUtility.getDouble(MPPUtility.getDouble(data, 84) / 100));
         resource.setDate1(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE1));
         resource.setDate2(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE2));
         resource.setDate3(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE3));
         resource.setDate4(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE4));
         resource.setDate5(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE5));
         resource.setDate6(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE6));
         resource.setDate7(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE7));
         resource.setDate8(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE8));
         resource.setDate9(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE9));
         resource.setDate10(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_DATE10));

         resource.setDuration1(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION1, RESOURCE_DURATION1_UNITS));
         resource.setDuration2(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION2, RESOURCE_DURATION2_UNITS));
         resource.setDuration3(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION3, RESOURCE_DURATION3_UNITS));
         resource.setDuration4(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION4, RESOURCE_DURATION4_UNITS));
         resource.setDuration5(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION5, RESOURCE_DURATION5_UNITS));
         resource.setDuration6(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION6, RESOURCE_DURATION6_UNITS));
         resource.setDuration7(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION7, RESOURCE_DURATION7_UNITS));
         resource.setDuration8(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION8, RESOURCE_DURATION8_UNITS));
         resource.setDuration9(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION9, RESOURCE_DURATION9_UNITS));
         resource.setDuration10(getCustomFieldDurationValue(rscVarData, id, RESOURCE_DURATION10, RESOURCE_DURATION10_UNITS));

         resource.setEmailAddress(rscVarData.getUnicodeString(id, RESOURCE_EMAIL));

         resource.setFinish1(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH1));
         resource.setFinish2(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH2));
         resource.setFinish3(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH3));
         resource.setFinish4(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH4));
         resource.setFinish5(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH5));
         resource.setFinish6(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH6));
         resource.setFinish7(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH7));
         resource.setFinish8(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH8));
         resource.setFinish9(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH9));
         resource.setFinish10(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_FINISH10));

         resource.setGroup(rscVarData.getUnicodeString(id, RESOURCE_GROUP));
         resource.setGUID(MPPUtility.getGUID(data2, 0));
         processHyperlinkData(resource, rscVarData.getByteArray(id, RESOURCE_HYPERLINK));
         resource.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));
         resource.setInitials(rscVarData.getUnicodeString(id, RESOURCE_INITIALS));
         //resource.setLinkedFields(); // Calculated value        
         resource.setMaterialLabel(rscVarData.getUnicodeString(id, RESOURCE_MATERIAL_LABEL));
         resource.setMaxUnits(NumberUtility.getDouble(MPPUtility.getDouble(data, 44) / 100));
         resource.setName(rscVarData.getUnicodeString(id, RESOURCE_NAME));

         resource.setNumber1(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER1)));
         resource.setNumber2(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER2)));
         resource.setNumber3(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER3)));
         resource.setNumber4(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER4)));
         resource.setNumber5(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER5)));
         resource.setNumber6(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER6)));
         resource.setNumber7(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER7)));
         resource.setNumber8(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER8)));
         resource.setNumber9(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER9)));
         resource.setNumber10(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER10)));
         resource.setNumber11(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER11)));
         resource.setNumber12(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER12)));
         resource.setNumber13(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER13)));
         resource.setNumber14(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER14)));
         resource.setNumber15(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER15)));
         resource.setNumber16(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER16)));
         resource.setNumber17(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER17)));
         resource.setNumber18(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER18)));
         resource.setNumber19(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER19)));
         resource.setNumber20(Double.valueOf(getCustomFieldDoubleValue(rscVarData, id, RESOURCE_NUMBER20)));

         //resource.setObjects(); // Calculated value
         resource.setOutlineCode1(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE1)), OUTLINECODE_DATA));
         resource.setOutlineCode2(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE2)), OUTLINECODE_DATA));
         resource.setOutlineCode3(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE3)), OUTLINECODE_DATA));
         resource.setOutlineCode4(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE4)), OUTLINECODE_DATA));
         resource.setOutlineCode5(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE5)), OUTLINECODE_DATA));
         resource.setOutlineCode6(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE6)), OUTLINECODE_DATA));
         resource.setOutlineCode7(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE7)), OUTLINECODE_DATA));
         resource.setOutlineCode8(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE8)), OUTLINECODE_DATA));
         resource.setOutlineCode9(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE9)), OUTLINECODE_DATA));
         resource.setOutlineCode10(m_outlineCodeVarData.getUnicodeString(Integer.valueOf(rscVarData.getInt(id, 2, RESOURCE_OUTLINECODE10)), OUTLINECODE_DATA));
         //resource.setOverallocated(); // Calculated value
         resource.setOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 164) / 100));
         resource.setOvertimeRate(new Rate(MPPUtility.getDouble(data, 36), TimeUnit.HOURS));
         resource.setOvertimeRateFormat(TimeUnit.getInstance(MPPUtility.getShort(data, 10) - 1));
         resource.setOvertimeWork(Duration.getInstance(MPPUtility.getDouble(data, 76) / 60000, TimeUnit.HOURS));
         resource.setPeakUnits(NumberUtility.getDouble(MPPUtility.getDouble(data, 124) / 100));
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(Duration.getInstance(MPPUtility.getDouble(data, 100) / 60000, TimeUnit.HOURS));
         resource.setRemainingCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 156) / 100));
         resource.setRemainingOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 180) / 100));
         resource.setRemainingWork(Duration.getInstance(MPPUtility.getDouble(data, 92) / 60000, TimeUnit.HOURS));
         resource.setStandardRate(new Rate(MPPUtility.getDouble(data, 28), TimeUnit.HOURS));
         resource.setStandardRateFormat(TimeUnit.getInstance(MPPUtility.getShort(data, 8) - 1));

         resource.setStart1(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START1));
         resource.setStart2(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START2));
         resource.setStart3(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START3));
         resource.setStart4(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START4));
         resource.setStart5(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START5));
         resource.setStart6(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START6));
         resource.setStart7(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START7));
         resource.setStart8(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START8));
         resource.setStart9(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START9));
         resource.setStart10(getCustomFieldTimestampValue(rscVarData, id, RESOURCE_START10));

         resource.setSubprojectResourceUniqueID(Integer.valueOf(rscVarData.getInt(id, RESOURCE_SUBPROJECTRESOURCEID)));

         resource.setText1(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT1));
         resource.setText2(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT2));
         resource.setText3(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT3));
         resource.setText4(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT4));
         resource.setText5(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT5));
         resource.setText6(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT6));
         resource.setText7(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT7));
         resource.setText8(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT8));
         resource.setText9(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT9));
         resource.setText10(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT10));
         resource.setText11(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT11));
         resource.setText12(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT12));
         resource.setText13(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT13));
         resource.setText14(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT14));
         resource.setText15(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT15));
         resource.setText16(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT16));
         resource.setText17(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT17));
         resource.setText18(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT18));
         resource.setText19(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT19));
         resource.setText20(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT20));
         resource.setText21(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT21));
         resource.setText22(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT22));
         resource.setText23(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT23));
         resource.setText24(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT24));
         resource.setText25(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT25));
         resource.setText26(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT26));
         resource.setText27(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT27));
         resource.setText28(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT28));
         resource.setText29(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT29));
         resource.setText30(getCustomFieldUnicodeStringValue(rscVarData, id, RESOURCE_TEXT30));

         resource.setType((MPPUtility.getShort(data, 14) == 0 ? ResourceType.WORK : ((metaData2[8] & 0x10) == 0) ? ResourceType.MATERIAL : ResourceType.COST));
         resource.setUniqueID(id);
         resource.setWork(Duration.getInstance(MPPUtility.getDouble(data, 52) / 60000, TimeUnit.HOURS));

         metaData = rscFixedMeta.getByteArrayValue(offset.intValue());
         resource.setFlag1((metaData[28] & 0x40) != 0);
         resource.setFlag2((metaData[28] & 0x80) != 0);
         resource.setFlag3((metaData[29] & 0x01) != 0);
         resource.setFlag4((metaData[29] & 0x02) != 0);
         resource.setFlag5((metaData[29] & 0x04) != 0);
         resource.setFlag6((metaData[29] & 0x08) != 0);
         resource.setFlag7((metaData[29] & 0x10) != 0);
         resource.setFlag8((metaData[29] & 0x20) != 0);
         resource.setFlag9((metaData[29] & 0x40) != 0);
         resource.setFlag10((metaData[28] & 0x20) != 0);
         resource.setFlag11((metaData[29] & 0x20) != 0);
         resource.setFlag12((metaData[30] & 0x01) != 0);
         resource.setFlag13((metaData[30] & 0x02) != 0);
         resource.setFlag14((metaData[30] & 0x04) != 0);
         resource.setFlag15((metaData[30] & 0x08) != 0);
         resource.setFlag16((metaData[30] & 0x10) != 0);
         resource.setFlag17((metaData[30] & 0x20) != 0);
         resource.setFlag18((metaData[30] & 0x40) != 0);
         resource.setFlag19((metaData[30] & 0x80) != 0);
         resource.setFlag20((metaData[31] & 0x01) != 0);

         notes = rscVarData.getString(id, RESOURCE_NOTES);
         if (notes != null)
         {
            if (m_reader.getPreserveNoteFormatting() == false)
            {
               notes = rtf.strip(notes);
            }

            resource.setNotes(notes);
         }

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
         resource.setCostRateTable(0, crt.process(rscVarData.getByteArray(id, RESOURCE_COST_RATE_A)));
         resource.setCostRateTable(1, crt.process(rscVarData.getByteArray(id, RESOURCE_COST_RATE_B)));
         resource.setCostRateTable(2, crt.process(rscVarData.getByteArray(id, RESOURCE_COST_RATE_C)));
         resource.setCostRateTable(3, crt.process(rscVarData.getByteArray(id, RESOURCE_COST_RATE_D)));
         resource.setCostRateTable(4, crt.process(rscVarData.getByteArray(id, RESOURCE_COST_RATE_E)));

         //
         // Process availability table
         //
         AvailabilityFactory af = new AvailabilityFactory();
         af.process(resource.getAvailability(), rscVarData.getByteArray(id, RESOURCE_AVAILABILITY));

         m_file.fireResourceReadEvent(resource);
      }
   }

   /**
    * This method extracts and collates resource assignment data.
    *
    * @throws IOException
    */
   private void processAssignmentData() throws IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry) m_projectDir.getEntry("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data(assnVarMeta, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData(142, getEncryptableInputStream(assnDir, "FixedData"));
      ResourceAssignmentFactory factory = new ResourceAssignmentFactory12();
      factory.process(m_file, m_reader.getUseRawTimephasedData(), assnVarMeta, assnVarData, assnFixedMeta, assnFixedData);
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
      FixedData fixedData = new FixedData(138, getEncryptableInputStream(dir, "FixedData"));

      int items = fixedMeta.getItemCount();
      View view;
      ViewFactory factory = new ViewFactory12();

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
               m_file.addView(view);
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

      TableFactory factory = new TableFactory(TABLE_COLUMN_DATA_STANDARD, TABLE_COLUMN_DATA_ENTERPRISE, TABLE_COLUMN_DATA_BASELINE);
      int items = fixedData.getItemCount();
      for (int loop = 0; loop < items; loop++)
      {
         byte[] data = fixedData.getByteArrayValue(loop);
         Table table = factory.createTable(m_file, data, varMeta, varData);
         m_file.addTable(table);
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
      FixedData fixedData = new FixedData(fixedMeta, getEncryptableInputStream(dir, "FixedData"));
      VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

      //System.out.println(fixedMeta);
      //System.out.println(fixedData);
      //System.out.println(varMeta);
      //System.out.println(varData);

      FilterReader reader = new FilterReader12();
      reader.process(m_file, fixedData, varData);
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
      FixedData fixedData = new FixedData(fixedMeta, getEncryptableInputStream(dir, "FixedData"));
      VarMeta varMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

      //      System.out.println(fixedMeta);
      //      System.out.println(fixedData);
      //      System.out.println(varMeta);
      //      System.out.println(varData);   

      GroupReader reader = new GroupReader12();
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
         CustomFieldValueItem item = m_file.getCustomFieldValueItem(Integer.valueOf(uniqueId));
         if (item != null && item.getValue() != null)
         {
            result = MPPUtility.getUnicodeString(item.getValue());
         }
      }
      return result;
   }

   /**
    * Retrieve custom field value.
    * 
    * @param varData var data block
    * @param id item ID
    * @param type item type
    * @return item value
    */
   private Date getCustomFieldTimestampValue(Var2Data varData, Integer id, Integer type)
   {
      Date result = null;

      int mask = varData.getShort(id, type);
      if ((mask & 0xFF00) != VALUE_LIST_MASK)
      {
         result = varData.getTimestamp(id, type);
      }
      else
      {
         int uniqueId = varData.getInt(id, 2, type);
         CustomFieldValueItem item = m_file.getCustomFieldValueItem(Integer.valueOf(uniqueId));
         if (item != null && item.getValue() != null)
         {
            result = MPPUtility.getTimestamp(item.getValue());
         }
      }
      return result;
   }

   /**
    * Retrieve custom field value.
    * 
    * @param varData var data block
    * @param id item ID
    * @param type item type
    * @param unitsType duration units type
    * @return item value
    */
   private Duration getCustomFieldDurationValue(Var2Data varData, Integer id, Integer type, Integer unitsType)
   {
      Duration result = null;

      int mask = varData.getShort(id, type);
      if ((mask & 0xFF00) != VALUE_LIST_MASK)
      {
         result = MPPUtility.getAdjustedDuration(m_file, varData.getInt(id, type), MPPUtility.getDurationTimeUnits(varData.getShort(id, unitsType)));
      }
      else
      {
         int uniqueId = varData.getInt(id, 2, type);
         CustomFieldValueItem item = m_file.getCustomFieldValueItem(Integer.valueOf(uniqueId));
         if (item != null && item.getValue() != null)
         {
            result = MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt(item.getValue()), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(item.getValue(), 4)));
         }
      }
      return result;
   }

   /**
    * Retrieve custom field value.
    * 
    * @param varData var data block
    * @param id item ID
    * @param type item type
    * @return item value
    */
   private double getCustomFieldDoubleValue(Var2Data varData, Integer id, Integer type)
   {
      double result = 0;

      int mask = varData.getShort(id, type);
      if ((mask & 0xFF00) != VALUE_LIST_MASK)
      {
         result = varData.getDouble(id, type);
      }
      else
      {
         int uniqueId = varData.getInt(id, 2, type);
         CustomFieldValueItem item = m_file.getCustomFieldValueItem(Integer.valueOf(uniqueId));
         if (item != null && item.getValue() != null)
         {
            result = MPPUtility.getDouble(item.getValue());
         }
      }
      return result;
   }

   /**
    * Retrieve custom field value.
    * 
    * @param varData var data block
    * @param id item ID
    * @param type item type
    * @return item value
    */
   private String getCustomFieldUnicodeStringValue(Var2Data varData, Integer id, Integer type)
   {
      String result = null;

      int mask = varData.getShort(id, type);
      if ((mask & 0xFF00) != VALUE_LIST_MASK)
      {
         result = varData.getUnicodeString(id, type);
      }
      else
      {
         int uniqueId = varData.getInt(id, 2, type);
         CustomFieldValueItem item = m_file.getCustomFieldValueItem(Integer.valueOf(uniqueId));
         if (item != null && item.getValue() != null)
         {
            result = MPPUtility.getUnicodeString(item.getValue());
         }
      }
      return result;
   }

   /**
    * Method used to instantiate the appropriate input stream reader,
    * a standard one, or one which can deal with "encrypted" data.
    * 
    * @param directory directory entry
    * @param name file name
    * @return new input stream
    * @throws IOException
    */
   private InputStream getEncryptableInputStream(DirectoryEntry directory, String name) throws IOException
   {
      DocumentEntry entry = (DocumentEntry) directory.getEntry(name);
      InputStream stream;
      if (m_file.getEncoded())
      {
         stream = new EncryptedDocumentInputStream(entry, m_file.getEncryptionCode());
      }
      else
      {
         stream = new DocumentInputStream(entry);
      }

      return (stream);
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
   private DirectoryEntry m_root;
   private HashMap<Integer, ProjectCalendar> m_resourceMap;
   private Var2Data m_outlineCodeVarData;
   private VarMeta m_outlineCodeVarMeta;
   private Props12 m_projectProps;
   private Map<Integer, FontBase> m_fontBases;
   private Map<Integer, SubProject> m_taskSubProjects;
   private DirectoryEntry m_projectDir;
   private DirectoryEntry m_viewDir;
   private Map<Integer, Integer> m_parentTasks;
   private Map<Long, Integer> m_taskOrder;
   private Map<Integer, Integer> m_nullTaskOrder;

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
   // TODO: figure out why the different value exist.
   private static final int SUBPROJECT_TASKUNIQUEID0 = 0x00000000;
   private static final int SUBPROJECT_TASKUNIQUEID1 = 0x0B340000;
   private static final int SUBPROJECT_TASKUNIQUEID2 = 0x0ABB0000;
   private static final int SUBPROJECT_TASKUNIQUEID3 = 0x05A10000;
   private static final int SUBPROJECT_TASKUNIQUEID4 = 0x0BD50000;
   private static final int SUBPROJECT_TASKUNIQUEID5 = 0x03D60000;

   /**
    * Calendar data types.
    */
   private static final Integer CALENDAR_NAME = Integer.valueOf(1);
   private static final Integer CALENDAR_DATA = Integer.valueOf(8);

   /**
    * Task data types.
    */

   //
   // MPP12 verified
   //
   private static final Integer TASK_NAME = Integer.valueOf(14);
   private static final Integer TASK_WBS = Integer.valueOf(16);

   private static final Integer TASK_NUMBER1 = Integer.valueOf(87);
   private static final Integer TASK_NUMBER2 = Integer.valueOf(88);
   private static final Integer TASK_NUMBER3 = Integer.valueOf(89);
   private static final Integer TASK_NUMBER4 = Integer.valueOf(90);
   private static final Integer TASK_NUMBER5 = Integer.valueOf(91);

   private static final Integer TASK_COST1 = Integer.valueOf(106);
   private static final Integer TASK_COST2 = Integer.valueOf(107);
   private static final Integer TASK_COST3 = Integer.valueOf(108);

   private static final Integer TASK_CONTACT = Integer.valueOf(112);

   private static final Integer TASK_RECURRING_DATA = Integer.valueOf(203);

   private static final Integer TASK_COST4 = Integer.valueOf(258);
   private static final Integer TASK_COST5 = Integer.valueOf(259);
   private static final Integer TASK_COST6 = Integer.valueOf(260);
   private static final Integer TASK_COST7 = Integer.valueOf(261);
   private static final Integer TASK_COST8 = Integer.valueOf(262);
   private static final Integer TASK_COST9 = Integer.valueOf(263);
   private static final Integer TASK_COST10 = Integer.valueOf(264);

   private static final Integer TASK_DATE1 = Integer.valueOf(265);
   private static final Integer TASK_DATE2 = Integer.valueOf(266);
   private static final Integer TASK_DATE3 = Integer.valueOf(267);
   private static final Integer TASK_DATE4 = Integer.valueOf(268);
   private static final Integer TASK_DATE5 = Integer.valueOf(269);
   private static final Integer TASK_DATE6 = Integer.valueOf(270);
   private static final Integer TASK_DATE7 = Integer.valueOf(271);
   private static final Integer TASK_DATE8 = Integer.valueOf(272);
   private static final Integer TASK_DATE9 = Integer.valueOf(273);
   private static final Integer TASK_DATE10 = Integer.valueOf(274);

   private static final Integer TASK_NUMBER6 = Integer.valueOf(302);
   private static final Integer TASK_NUMBER7 = Integer.valueOf(303);
   private static final Integer TASK_NUMBER8 = Integer.valueOf(304);
   private static final Integer TASK_NUMBER9 = Integer.valueOf(305);
   private static final Integer TASK_NUMBER10 = Integer.valueOf(306);

   private static final Integer TASK_DURATION1 = Integer.valueOf(103);
   private static final Integer TASK_DURATION1_UNITS = Integer.valueOf(183);
   private static final Integer TASK_DURATION2 = Integer.valueOf(104);
   private static final Integer TASK_DURATION2_UNITS = Integer.valueOf(184);
   private static final Integer TASK_DURATION3 = Integer.valueOf(105);
   private static final Integer TASK_DURATION3_UNITS = Integer.valueOf(185);
   private static final Integer TASK_DURATION4 = Integer.valueOf(275);
   private static final Integer TASK_DURATION4_UNITS = Integer.valueOf(337);
   private static final Integer TASK_DURATION5 = Integer.valueOf(276);
   private static final Integer TASK_DURATION5_UNITS = Integer.valueOf(187);
   private static final Integer TASK_DURATION6 = Integer.valueOf(277);
   private static final Integer TASK_DURATION6_UNITS = Integer.valueOf(188);
   private static final Integer TASK_DURATION7 = Integer.valueOf(278);
   private static final Integer TASK_DURATION7_UNITS = Integer.valueOf(189);
   private static final Integer TASK_DURATION8 = Integer.valueOf(279);
   private static final Integer TASK_DURATION8_UNITS = Integer.valueOf(190);
   private static final Integer TASK_DURATION9 = Integer.valueOf(280);
   private static final Integer TASK_DURATION9_UNITS = Integer.valueOf(191);
   private static final Integer TASK_DURATION10 = Integer.valueOf(281);
   private static final Integer TASK_DURATION10_UNITS = Integer.valueOf(192);

   private static final Integer TASK_START1 = Integer.valueOf(52);
   private static final Integer TASK_FINISH1 = Integer.valueOf(53);
   private static final Integer TASK_START2 = Integer.valueOf(55);
   private static final Integer TASK_FINISH2 = Integer.valueOf(56);
   private static final Integer TASK_START3 = Integer.valueOf(58);
   private static final Integer TASK_FINISH3 = Integer.valueOf(59);
   private static final Integer TASK_START4 = Integer.valueOf(61);
   private static final Integer TASK_FINISH4 = Integer.valueOf(62);
   private static final Integer TASK_START5 = Integer.valueOf(64);
   private static final Integer TASK_FINISH5 = Integer.valueOf(65);
   private static final Integer TASK_START6 = Integer.valueOf(282);
   private static final Integer TASK_FINISH6 = Integer.valueOf(283);
   private static final Integer TASK_START7 = Integer.valueOf(284);
   private static final Integer TASK_FINISH7 = Integer.valueOf(285);
   private static final Integer TASK_START8 = Integer.valueOf(286);
   private static final Integer TASK_FINISH8 = Integer.valueOf(287);
   private static final Integer TASK_START9 = Integer.valueOf(288);
   private static final Integer TASK_FINISH9 = Integer.valueOf(289);
   private static final Integer TASK_START10 = Integer.valueOf(290);
   private static final Integer TASK_FINISH10 = Integer.valueOf(291);

   private static final Integer TASK_HYPERLINK = Integer.valueOf(215);

   private static final Integer TASK_NOTES = Integer.valueOf(15);

   private static final Integer TASK_NUMBER11 = Integer.valueOf(307);
   private static final Integer TASK_NUMBER12 = Integer.valueOf(308);
   private static final Integer TASK_NUMBER13 = Integer.valueOf(309);
   private static final Integer TASK_NUMBER14 = Integer.valueOf(310);
   private static final Integer TASK_NUMBER15 = Integer.valueOf(311);
   private static final Integer TASK_NUMBER16 = Integer.valueOf(312);
   private static final Integer TASK_NUMBER17 = Integer.valueOf(313);
   private static final Integer TASK_NUMBER18 = Integer.valueOf(314);
   private static final Integer TASK_NUMBER19 = Integer.valueOf(315);
   private static final Integer TASK_NUMBER20 = Integer.valueOf(316);

   private static final Integer TASK_TEXT1 = Integer.valueOf(51);
   private static final Integer TASK_TEXT2 = Integer.valueOf(54);
   private static final Integer TASK_TEXT3 = Integer.valueOf(57);
   private static final Integer TASK_TEXT4 = Integer.valueOf(60);
   private static final Integer TASK_TEXT5 = Integer.valueOf(63);
   private static final Integer TASK_TEXT6 = Integer.valueOf(66);
   private static final Integer TASK_TEXT7 = Integer.valueOf(67);
   private static final Integer TASK_TEXT8 = Integer.valueOf(68);
   private static final Integer TASK_TEXT9 = Integer.valueOf(69);
   private static final Integer TASK_TEXT10 = Integer.valueOf(70);

   private static final Integer TASK_TEXT11 = Integer.valueOf(317);
   private static final Integer TASK_TEXT12 = Integer.valueOf(318);
   private static final Integer TASK_TEXT13 = Integer.valueOf(319);
   private static final Integer TASK_TEXT14 = Integer.valueOf(320);
   private static final Integer TASK_TEXT15 = Integer.valueOf(321);
   private static final Integer TASK_TEXT16 = Integer.valueOf(322);
   private static final Integer TASK_TEXT17 = Integer.valueOf(323);
   private static final Integer TASK_TEXT18 = Integer.valueOf(324);
   private static final Integer TASK_TEXT19 = Integer.valueOf(325);
   private static final Integer TASK_TEXT20 = Integer.valueOf(326);
   private static final Integer TASK_TEXT21 = Integer.valueOf(327);
   private static final Integer TASK_TEXT22 = Integer.valueOf(328);
   private static final Integer TASK_TEXT23 = Integer.valueOf(329);
   private static final Integer TASK_TEXT24 = Integer.valueOf(330);
   private static final Integer TASK_TEXT25 = Integer.valueOf(331);
   private static final Integer TASK_TEXT26 = Integer.valueOf(332);
   private static final Integer TASK_TEXT27 = Integer.valueOf(333);
   private static final Integer TASK_TEXT28 = Integer.valueOf(334);
   private static final Integer TASK_TEXT29 = Integer.valueOf(335);
   private static final Integer TASK_TEXT30 = Integer.valueOf(336);

   private static final Integer TASK_SUBPROJECT_TASKS_UNIQUEID_OFFSET = Integer.valueOf(458);

   private static final Integer TASK_OUTLINECODE1 = Integer.valueOf(417);
   private static final Integer TASK_OUTLINECODE2 = Integer.valueOf(419);
   private static final Integer TASK_OUTLINECODE3 = Integer.valueOf(421);
   private static final Integer TASK_OUTLINECODE4 = Integer.valueOf(423);
   private static final Integer TASK_OUTLINECODE5 = Integer.valueOf(425);
   private static final Integer TASK_OUTLINECODE6 = Integer.valueOf(427);
   private static final Integer TASK_OUTLINECODE7 = Integer.valueOf(429);
   private static final Integer TASK_OUTLINECODE8 = Integer.valueOf(431);
   private static final Integer TASK_OUTLINECODE9 = Integer.valueOf(433);
   private static final Integer TASK_OUTLINECODE10 = Integer.valueOf(435);

   private static final Integer TASK_ENTERPRISE_COST1 = Integer.valueOf(599);
   private static final Integer TASK_ENTERPRISE_COST2 = Integer.valueOf(600);
   private static final Integer TASK_ENTERPRISE_COST3 = Integer.valueOf(601);
   private static final Integer TASK_ENTERPRISE_COST4 = Integer.valueOf(602);
   private static final Integer TASK_ENTERPRISE_COST5 = Integer.valueOf(603);
   private static final Integer TASK_ENTERPRISE_COST6 = Integer.valueOf(604);
   private static final Integer TASK_ENTERPRISE_COST7 = Integer.valueOf(605);
   private static final Integer TASK_ENTERPRISE_COST8 = Integer.valueOf(606);
   private static final Integer TASK_ENTERPRISE_COST9 = Integer.valueOf(607);
   private static final Integer TASK_ENTERPRISE_COST10 = Integer.valueOf(608);

   private static final Integer TASK_ENTERPRISE_DATE1 = Integer.valueOf(609);
   private static final Integer TASK_ENTERPRISE_DATE2 = Integer.valueOf(610);
   private static final Integer TASK_ENTERPRISE_DATE3 = Integer.valueOf(611);
   private static final Integer TASK_ENTERPRISE_DATE4 = Integer.valueOf(612);
   private static final Integer TASK_ENTERPRISE_DATE5 = Integer.valueOf(613);
   private static final Integer TASK_ENTERPRISE_DATE6 = Integer.valueOf(614);
   private static final Integer TASK_ENTERPRISE_DATE7 = Integer.valueOf(615);
   private static final Integer TASK_ENTERPRISE_DATE8 = Integer.valueOf(616);
   private static final Integer TASK_ENTERPRISE_DATE9 = Integer.valueOf(617);
   private static final Integer TASK_ENTERPRISE_DATE10 = Integer.valueOf(618);
   private static final Integer TASK_ENTERPRISE_DATE11 = Integer.valueOf(619);
   private static final Integer TASK_ENTERPRISE_DATE12 = Integer.valueOf(620);
   private static final Integer TASK_ENTERPRISE_DATE13 = Integer.valueOf(621);
   private static final Integer TASK_ENTERPRISE_DATE14 = Integer.valueOf(622);
   private static final Integer TASK_ENTERPRISE_DATE15 = Integer.valueOf(623);
   private static final Integer TASK_ENTERPRISE_DATE16 = Integer.valueOf(624);
   private static final Integer TASK_ENTERPRISE_DATE17 = Integer.valueOf(625);
   private static final Integer TASK_ENTERPRISE_DATE18 = Integer.valueOf(626);
   private static final Integer TASK_ENTERPRISE_DATE19 = Integer.valueOf(627);
   private static final Integer TASK_ENTERPRISE_DATE20 = Integer.valueOf(628);
   private static final Integer TASK_ENTERPRISE_DATE21 = Integer.valueOf(629);
   private static final Integer TASK_ENTERPRISE_DATE22 = Integer.valueOf(630);
   private static final Integer TASK_ENTERPRISE_DATE23 = Integer.valueOf(631);
   private static final Integer TASK_ENTERPRISE_DATE24 = Integer.valueOf(632);
   private static final Integer TASK_ENTERPRISE_DATE25 = Integer.valueOf(633);
   private static final Integer TASK_ENTERPRISE_DATE26 = Integer.valueOf(634);
   private static final Integer TASK_ENTERPRISE_DATE27 = Integer.valueOf(635);
   private static final Integer TASK_ENTERPRISE_DATE28 = Integer.valueOf(636);
   private static final Integer TASK_ENTERPRISE_DATE29 = Integer.valueOf(637);
   private static final Integer TASK_ENTERPRISE_DATE30 = Integer.valueOf(638);

   private static final Integer TASK_ENTERPRISE_DURATION1 = Integer.valueOf(639);
   private static final Integer TASK_ENTERPRISE_DURATION2 = Integer.valueOf(640);
   private static final Integer TASK_ENTERPRISE_DURATION3 = Integer.valueOf(641);
   private static final Integer TASK_ENTERPRISE_DURATION4 = Integer.valueOf(642);
   private static final Integer TASK_ENTERPRISE_DURATION5 = Integer.valueOf(643);
   private static final Integer TASK_ENTERPRISE_DURATION6 = Integer.valueOf(644);
   private static final Integer TASK_ENTERPRISE_DURATION7 = Integer.valueOf(645);
   private static final Integer TASK_ENTERPRISE_DURATION8 = Integer.valueOf(646);
   private static final Integer TASK_ENTERPRISE_DURATION9 = Integer.valueOf(647);
   private static final Integer TASK_ENTERPRISE_DURATION10 = Integer.valueOf(648);

   private static final Integer TASK_ENTERPRISE_DURATION1_UNITS = Integer.valueOf(649);
   private static final Integer TASK_ENTERPRISE_DURATION2_UNITS = Integer.valueOf(650);
   private static final Integer TASK_ENTERPRISE_DURATION3_UNITS = Integer.valueOf(651);
   private static final Integer TASK_ENTERPRISE_DURATION4_UNITS = Integer.valueOf(652);
   private static final Integer TASK_ENTERPRISE_DURATION5_UNITS = Integer.valueOf(653);
   private static final Integer TASK_ENTERPRISE_DURATION6_UNITS = Integer.valueOf(654);
   private static final Integer TASK_ENTERPRISE_DURATION7_UNITS = Integer.valueOf(655);
   private static final Integer TASK_ENTERPRISE_DURATION8_UNITS = Integer.valueOf(656);
   private static final Integer TASK_ENTERPRISE_DURATION9_UNITS = Integer.valueOf(657);
   private static final Integer TASK_ENTERPRISE_DURATION10_UNITS = Integer.valueOf(658);

   private static final Integer TASK_ENTERPRISE_NUMBER1 = Integer.valueOf(699);
   private static final Integer TASK_ENTERPRISE_NUMBER2 = Integer.valueOf(700);
   private static final Integer TASK_ENTERPRISE_NUMBER3 = Integer.valueOf(701);
   private static final Integer TASK_ENTERPRISE_NUMBER4 = Integer.valueOf(702);
   private static final Integer TASK_ENTERPRISE_NUMBER5 = Integer.valueOf(703);
   private static final Integer TASK_ENTERPRISE_NUMBER6 = Integer.valueOf(704);
   private static final Integer TASK_ENTERPRISE_NUMBER7 = Integer.valueOf(705);
   private static final Integer TASK_ENTERPRISE_NUMBER8 = Integer.valueOf(706);
   private static final Integer TASK_ENTERPRISE_NUMBER9 = Integer.valueOf(707);
   private static final Integer TASK_ENTERPRISE_NUMBER10 = Integer.valueOf(708);
   private static final Integer TASK_ENTERPRISE_NUMBER11 = Integer.valueOf(709);
   private static final Integer TASK_ENTERPRISE_NUMBER12 = Integer.valueOf(710);
   private static final Integer TASK_ENTERPRISE_NUMBER13 = Integer.valueOf(711);
   private static final Integer TASK_ENTERPRISE_NUMBER14 = Integer.valueOf(712);
   private static final Integer TASK_ENTERPRISE_NUMBER15 = Integer.valueOf(713);
   private static final Integer TASK_ENTERPRISE_NUMBER16 = Integer.valueOf(714);
   private static final Integer TASK_ENTERPRISE_NUMBER17 = Integer.valueOf(715);
   private static final Integer TASK_ENTERPRISE_NUMBER18 = Integer.valueOf(716);
   private static final Integer TASK_ENTERPRISE_NUMBER19 = Integer.valueOf(717);
   private static final Integer TASK_ENTERPRISE_NUMBER20 = Integer.valueOf(718);
   private static final Integer TASK_ENTERPRISE_NUMBER21 = Integer.valueOf(719);
   private static final Integer TASK_ENTERPRISE_NUMBER22 = Integer.valueOf(720);
   private static final Integer TASK_ENTERPRISE_NUMBER23 = Integer.valueOf(721);
   private static final Integer TASK_ENTERPRISE_NUMBER24 = Integer.valueOf(722);
   private static final Integer TASK_ENTERPRISE_NUMBER25 = Integer.valueOf(723);
   private static final Integer TASK_ENTERPRISE_NUMBER26 = Integer.valueOf(724);
   private static final Integer TASK_ENTERPRISE_NUMBER27 = Integer.valueOf(725);
   private static final Integer TASK_ENTERPRISE_NUMBER28 = Integer.valueOf(726);
   private static final Integer TASK_ENTERPRISE_NUMBER29 = Integer.valueOf(727);
   private static final Integer TASK_ENTERPRISE_NUMBER30 = Integer.valueOf(728);
   private static final Integer TASK_ENTERPRISE_NUMBER31 = Integer.valueOf(729);
   private static final Integer TASK_ENTERPRISE_NUMBER32 = Integer.valueOf(730);
   private static final Integer TASK_ENTERPRISE_NUMBER33 = Integer.valueOf(731);
   private static final Integer TASK_ENTERPRISE_NUMBER34 = Integer.valueOf(732);
   private static final Integer TASK_ENTERPRISE_NUMBER35 = Integer.valueOf(733);
   private static final Integer TASK_ENTERPRISE_NUMBER36 = Integer.valueOf(734);
   private static final Integer TASK_ENTERPRISE_NUMBER37 = Integer.valueOf(735);
   private static final Integer TASK_ENTERPRISE_NUMBER38 = Integer.valueOf(736);
   private static final Integer TASK_ENTERPRISE_NUMBER39 = Integer.valueOf(737);
   private static final Integer TASK_ENTERPRISE_NUMBER40 = Integer.valueOf(738);

   private static final Integer TASK_ENTERPRISE_TEXT1 = Integer.valueOf(799);
   private static final Integer TASK_ENTERPRISE_TEXT2 = Integer.valueOf(800);
   private static final Integer TASK_ENTERPRISE_TEXT3 = Integer.valueOf(801);
   private static final Integer TASK_ENTERPRISE_TEXT4 = Integer.valueOf(802);
   private static final Integer TASK_ENTERPRISE_TEXT5 = Integer.valueOf(803);
   private static final Integer TASK_ENTERPRISE_TEXT6 = Integer.valueOf(804);
   private static final Integer TASK_ENTERPRISE_TEXT7 = Integer.valueOf(805);
   private static final Integer TASK_ENTERPRISE_TEXT8 = Integer.valueOf(806);
   private static final Integer TASK_ENTERPRISE_TEXT9 = Integer.valueOf(807);
   private static final Integer TASK_ENTERPRISE_TEXT10 = Integer.valueOf(808);
   private static final Integer TASK_ENTERPRISE_TEXT11 = Integer.valueOf(809);
   private static final Integer TASK_ENTERPRISE_TEXT12 = Integer.valueOf(810);
   private static final Integer TASK_ENTERPRISE_TEXT13 = Integer.valueOf(811);
   private static final Integer TASK_ENTERPRISE_TEXT14 = Integer.valueOf(812);
   private static final Integer TASK_ENTERPRISE_TEXT15 = Integer.valueOf(813);
   private static final Integer TASK_ENTERPRISE_TEXT16 = Integer.valueOf(814);
   private static final Integer TASK_ENTERPRISE_TEXT17 = Integer.valueOf(815);
   private static final Integer TASK_ENTERPRISE_TEXT18 = Integer.valueOf(816);
   private static final Integer TASK_ENTERPRISE_TEXT19 = Integer.valueOf(817);
   private static final Integer TASK_ENTERPRISE_TEXT20 = Integer.valueOf(818);
   private static final Integer TASK_ENTERPRISE_TEXT21 = Integer.valueOf(819);
   private static final Integer TASK_ENTERPRISE_TEXT22 = Integer.valueOf(820);
   private static final Integer TASK_ENTERPRISE_TEXT23 = Integer.valueOf(821);
   private static final Integer TASK_ENTERPRISE_TEXT24 = Integer.valueOf(822);
   private static final Integer TASK_ENTERPRISE_TEXT25 = Integer.valueOf(823);
   private static final Integer TASK_ENTERPRISE_TEXT26 = Integer.valueOf(824);
   private static final Integer TASK_ENTERPRISE_TEXT27 = Integer.valueOf(825);
   private static final Integer TASK_ENTERPRISE_TEXT28 = Integer.valueOf(826);
   private static final Integer TASK_ENTERPRISE_TEXT29 = Integer.valueOf(827);
   private static final Integer TASK_ENTERPRISE_TEXT30 = Integer.valueOf(828);
   private static final Integer TASK_ENTERPRISE_TEXT31 = Integer.valueOf(829);
   private static final Integer TASK_ENTERPRISE_TEXT32 = Integer.valueOf(830);
   private static final Integer TASK_ENTERPRISE_TEXT33 = Integer.valueOf(831);
   private static final Integer TASK_ENTERPRISE_TEXT34 = Integer.valueOf(832);
   private static final Integer TASK_ENTERPRISE_TEXT35 = Integer.valueOf(833);
   private static final Integer TASK_ENTERPRISE_TEXT36 = Integer.valueOf(834);
   private static final Integer TASK_ENTERPRISE_TEXT37 = Integer.valueOf(835);
   private static final Integer TASK_ENTERPRISE_TEXT38 = Integer.valueOf(836);
   private static final Integer TASK_ENTERPRISE_TEXT39 = Integer.valueOf(837);
   private static final Integer TASK_ENTERPRISE_TEXT40 = Integer.valueOf(838);

   private static final Integer TASK_EXTERNAL_TASK_ID = Integer.valueOf(255);

   private static final Integer TASK_BASELINE1_START = Integer.valueOf(482);
   private static final Integer TASK_BASELINE1_FINISH = Integer.valueOf(483);
   private static final Integer TASK_BASELINE1_COST = Integer.valueOf(484);
   private static final Integer TASK_BASELINE1_WORK = Integer.valueOf(485);
   private static final Integer TASK_BASELINE1_DURATION = Integer.valueOf(487);
   private static final Integer TASK_BASELINE1_DURATION_UNITS = Integer.valueOf(488);

   private static final Integer TASK_BASELINE2_START = Integer.valueOf(493);
   private static final Integer TASK_BASELINE2_FINISH = Integer.valueOf(494);
   private static final Integer TASK_BASELINE2_COST = Integer.valueOf(495);
   private static final Integer TASK_BASELINE2_WORK = Integer.valueOf(496);
   private static final Integer TASK_BASELINE2_DURATION = Integer.valueOf(498);
   private static final Integer TASK_BASELINE2_DURATION_UNITS = Integer.valueOf(499);

   private static final Integer TASK_BASELINE3_START = Integer.valueOf(504);
   private static final Integer TASK_BASELINE3_FINISH = Integer.valueOf(505);
   private static final Integer TASK_BASELINE3_COST = Integer.valueOf(506);
   private static final Integer TASK_BASELINE3_WORK = Integer.valueOf(507);
   private static final Integer TASK_BASELINE3_DURATION = Integer.valueOf(509);
   private static final Integer TASK_BASELINE3_DURATION_UNITS = Integer.valueOf(510);

   private static final Integer TASK_BASELINE4_START = Integer.valueOf(515);
   private static final Integer TASK_BASELINE4_FINISH = Integer.valueOf(516);
   private static final Integer TASK_BASELINE4_COST = Integer.valueOf(517);
   private static final Integer TASK_BASELINE4_WORK = Integer.valueOf(518);
   private static final Integer TASK_BASELINE4_DURATION = Integer.valueOf(520);
   private static final Integer TASK_BASELINE4_DURATION_UNITS = Integer.valueOf(521);

   private static final Integer TASK_BASELINE5_START = Integer.valueOf(526);
   private static final Integer TASK_BASELINE5_FINISH = Integer.valueOf(527);
   private static final Integer TASK_BASELINE5_COST = Integer.valueOf(528);
   private static final Integer TASK_BASELINE5_WORK = Integer.valueOf(529);
   private static final Integer TASK_BASELINE5_DURATION = Integer.valueOf(531);
   private static final Integer TASK_BASELINE5_DURATION_UNITS = Integer.valueOf(532);

   private static final Integer TASK_BASELINE6_START = Integer.valueOf(544);
   private static final Integer TASK_BASELINE6_FINISH = Integer.valueOf(545);
   private static final Integer TASK_BASELINE6_COST = Integer.valueOf(546);
   private static final Integer TASK_BASELINE6_WORK = Integer.valueOf(547);
   private static final Integer TASK_BASELINE6_DURATION = Integer.valueOf(549);
   private static final Integer TASK_BASELINE6_DURATION_UNITS = Integer.valueOf(550);

   private static final Integer TASK_BASELINE7_START = Integer.valueOf(555);
   private static final Integer TASK_BASELINE7_FINISH = Integer.valueOf(556);
   private static final Integer TASK_BASELINE7_COST = Integer.valueOf(557);
   private static final Integer TASK_BASELINE7_WORK = Integer.valueOf(558);
   private static final Integer TASK_BASELINE7_DURATION = Integer.valueOf(560);
   private static final Integer TASK_BASELINE7_DURATION_UNITS = Integer.valueOf(561);

   private static final Integer TASK_BASELINE8_START = Integer.valueOf(566);
   private static final Integer TASK_BASELINE8_FINISH = Integer.valueOf(567);
   private static final Integer TASK_BASELINE8_COST = Integer.valueOf(568);
   private static final Integer TASK_BASELINE8_WORK = Integer.valueOf(569);
   private static final Integer TASK_BASELINE8_DURATION = Integer.valueOf(571);
   private static final Integer TASK_BASELINE8_DURATION_UNITS = Integer.valueOf(572);

   private static final Integer TASK_BASELINE9_START = Integer.valueOf(577);
   private static final Integer TASK_BASELINE9_FINISH = Integer.valueOf(578);
   private static final Integer TASK_BASELINE9_COST = Integer.valueOf(579);
   private static final Integer TASK_BASELINE9_WORK = Integer.valueOf(580);
   private static final Integer TASK_BASELINE9_DURATION = Integer.valueOf(582);
   private static final Integer TASK_BASELINE9_DURATION_UNITS = Integer.valueOf(583);

   private static final Integer TASK_BASELINE10_START = Integer.valueOf(588);
   private static final Integer TASK_BASELINE10_FINISH = Integer.valueOf(589);
   private static final Integer TASK_BASELINE10_COST = Integer.valueOf(590);
   private static final Integer TASK_BASELINE10_WORK = Integer.valueOf(591);
   private static final Integer TASK_BASELINE10_DURATION = Integer.valueOf(593);
   private static final Integer TASK_BASELINE10_DURATION_UNITS = Integer.valueOf(594);

   private static final Integer TASK_OVERTIME_COST = Integer.valueOf(168);
   private static final Integer TASK_ACTUAL_OVERTIME_COST = Integer.valueOf(169);

   //
   // Unverified
   //
   private static final Integer TASK_ACTUAL_OVERTIME_WORK = Integer.valueOf(3);
   private static final Integer TASK_REMAINING_OVERTIME_WORK = Integer.valueOf(4);
   private static final Integer TASK_REMAINING_OVERTIME_COST = Integer.valueOf(7);
   private static final Integer TASK_SUBPROJECTUNIQUETASKID = Integer.valueOf(242);
   private static final Integer TASK_SUBPROJECTTASKID = Integer.valueOf(255);

   /**
    * Resource data types.
    */

   //
   // MPP12 verified
   //
   private static final Integer RESOURCE_NAME = Integer.valueOf(1);
   private static final Integer RESOURCE_INITIALS = Integer.valueOf(2);
   private static final Integer RESOURCE_GROUP = Integer.valueOf(3);
   private static final Integer RESOURCE_CODE = Integer.valueOf(10);
   private static final Integer RESOURCE_MATERIAL_LABEL = Integer.valueOf(299);

   private static final Integer RESOURCE_HYPERLINK = Integer.valueOf(136);

   private static final Integer RESOURCE_COST1 = Integer.valueOf(123);
   private static final Integer RESOURCE_COST2 = Integer.valueOf(124);
   private static final Integer RESOURCE_COST3 = Integer.valueOf(125);
   private static final Integer RESOURCE_COST4 = Integer.valueOf(166);
   private static final Integer RESOURCE_COST5 = Integer.valueOf(167);
   private static final Integer RESOURCE_COST6 = Integer.valueOf(168);
   private static final Integer RESOURCE_COST7 = Integer.valueOf(169);
   private static final Integer RESOURCE_COST8 = Integer.valueOf(170);
   private static final Integer RESOURCE_COST9 = Integer.valueOf(171);
   private static final Integer RESOURCE_COST10 = Integer.valueOf(172);

   private static final Integer RESOURCE_EMAIL = Integer.valueOf(35);

   private static final Integer RESOURCE_DATE1 = Integer.valueOf(173);
   private static final Integer RESOURCE_DATE2 = Integer.valueOf(174);
   private static final Integer RESOURCE_DATE3 = Integer.valueOf(175);
   private static final Integer RESOURCE_DATE4 = Integer.valueOf(176);
   private static final Integer RESOURCE_DATE5 = Integer.valueOf(177);
   private static final Integer RESOURCE_DATE6 = Integer.valueOf(178);
   private static final Integer RESOURCE_DATE7 = Integer.valueOf(179);
   private static final Integer RESOURCE_DATE8 = Integer.valueOf(180);
   private static final Integer RESOURCE_DATE9 = Integer.valueOf(181);
   private static final Integer RESOURCE_DATE10 = Integer.valueOf(182);

   private static final Integer RESOURCE_START1 = Integer.valueOf(102);
   private static final Integer RESOURCE_START2 = Integer.valueOf(103);
   private static final Integer RESOURCE_START3 = Integer.valueOf(104);
   private static final Integer RESOURCE_START4 = Integer.valueOf(105);
   private static final Integer RESOURCE_START5 = Integer.valueOf(106);
   private static final Integer RESOURCE_START6 = Integer.valueOf(220);
   private static final Integer RESOURCE_START7 = Integer.valueOf(221);
   private static final Integer RESOURCE_START8 = Integer.valueOf(222);
   private static final Integer RESOURCE_START9 = Integer.valueOf(223);
   private static final Integer RESOURCE_START10 = Integer.valueOf(224);

   private static final Integer RESOURCE_FINISH1 = Integer.valueOf(107);
   private static final Integer RESOURCE_FINISH2 = Integer.valueOf(108);
   private static final Integer RESOURCE_FINISH3 = Integer.valueOf(109);
   private static final Integer RESOURCE_FINISH4 = Integer.valueOf(110);
   private static final Integer RESOURCE_FINISH5 = Integer.valueOf(111);
   private static final Integer RESOURCE_FINISH6 = Integer.valueOf(190);
   private static final Integer RESOURCE_FINISH7 = Integer.valueOf(191);
   private static final Integer RESOURCE_FINISH8 = Integer.valueOf(192);
   private static final Integer RESOURCE_FINISH9 = Integer.valueOf(193);
   private static final Integer RESOURCE_FINISH10 = Integer.valueOf(194);

   private static final Integer RESOURCE_OUTLINECODE1 = Integer.valueOf(279);
   private static final Integer RESOURCE_OUTLINECODE2 = Integer.valueOf(281);
   private static final Integer RESOURCE_OUTLINECODE3 = Integer.valueOf(283);
   private static final Integer RESOURCE_OUTLINECODE4 = Integer.valueOf(285);
   private static final Integer RESOURCE_OUTLINECODE5 = Integer.valueOf(287);
   private static final Integer RESOURCE_OUTLINECODE6 = Integer.valueOf(289);
   private static final Integer RESOURCE_OUTLINECODE7 = Integer.valueOf(291);
   private static final Integer RESOURCE_OUTLINECODE8 = Integer.valueOf(293);
   private static final Integer RESOURCE_OUTLINECODE9 = Integer.valueOf(295);
   private static final Integer RESOURCE_OUTLINECODE10 = Integer.valueOf(297);

   private static final Integer RESOURCE_DURATION1 = Integer.valueOf(117);
   private static final Integer RESOURCE_DURATION2 = Integer.valueOf(118);
   private static final Integer RESOURCE_DURATION3 = Integer.valueOf(119);
   private static final Integer RESOURCE_DURATION4 = Integer.valueOf(183);
   private static final Integer RESOURCE_DURATION5 = Integer.valueOf(184);
   private static final Integer RESOURCE_DURATION6 = Integer.valueOf(185);
   private static final Integer RESOURCE_DURATION7 = Integer.valueOf(186);
   private static final Integer RESOURCE_DURATION8 = Integer.valueOf(187);
   private static final Integer RESOURCE_DURATION9 = Integer.valueOf(188);
   private static final Integer RESOURCE_DURATION10 = Integer.valueOf(189);

   private static final Integer RESOURCE_DURATION1_UNITS = Integer.valueOf(120);
   private static final Integer RESOURCE_DURATION2_UNITS = Integer.valueOf(121);
   private static final Integer RESOURCE_DURATION3_UNITS = Integer.valueOf(122);
   private static final Integer RESOURCE_DURATION4_UNITS = Integer.valueOf(245);
   private static final Integer RESOURCE_DURATION5_UNITS = Integer.valueOf(246);
   private static final Integer RESOURCE_DURATION6_UNITS = Integer.valueOf(247);
   private static final Integer RESOURCE_DURATION7_UNITS = Integer.valueOf(248);
   private static final Integer RESOURCE_DURATION8_UNITS = Integer.valueOf(249);
   private static final Integer RESOURCE_DURATION9_UNITS = Integer.valueOf(250);
   private static final Integer RESOURCE_DURATION10_UNITS = Integer.valueOf(251);

   private static final Integer RESOURCE_NUMBER1 = Integer.valueOf(112);
   private static final Integer RESOURCE_NUMBER2 = Integer.valueOf(113);
   private static final Integer RESOURCE_NUMBER3 = Integer.valueOf(114);
   private static final Integer RESOURCE_NUMBER4 = Integer.valueOf(115);
   private static final Integer RESOURCE_NUMBER5 = Integer.valueOf(116);
   private static final Integer RESOURCE_NUMBER6 = Integer.valueOf(205);
   private static final Integer RESOURCE_NUMBER7 = Integer.valueOf(206);
   private static final Integer RESOURCE_NUMBER8 = Integer.valueOf(207);
   private static final Integer RESOURCE_NUMBER9 = Integer.valueOf(208);
   private static final Integer RESOURCE_NUMBER10 = Integer.valueOf(209);
   private static final Integer RESOURCE_NUMBER11 = Integer.valueOf(210);
   private static final Integer RESOURCE_NUMBER12 = Integer.valueOf(211);
   private static final Integer RESOURCE_NUMBER13 = Integer.valueOf(212);
   private static final Integer RESOURCE_NUMBER14 = Integer.valueOf(213);
   private static final Integer RESOURCE_NUMBER15 = Integer.valueOf(214);
   private static final Integer RESOURCE_NUMBER16 = Integer.valueOf(215);
   private static final Integer RESOURCE_NUMBER17 = Integer.valueOf(216);
   private static final Integer RESOURCE_NUMBER18 = Integer.valueOf(217);
   private static final Integer RESOURCE_NUMBER19 = Integer.valueOf(218);
   private static final Integer RESOURCE_NUMBER20 = Integer.valueOf(219);

   private static final Integer RESOURCE_TEXT1 = Integer.valueOf(8);
   private static final Integer RESOURCE_TEXT2 = Integer.valueOf(9);
   private static final Integer RESOURCE_TEXT3 = Integer.valueOf(30);
   private static final Integer RESOURCE_TEXT4 = Integer.valueOf(31);
   private static final Integer RESOURCE_TEXT5 = Integer.valueOf(32);
   private static final Integer RESOURCE_TEXT6 = Integer.valueOf(97);
   private static final Integer RESOURCE_TEXT7 = Integer.valueOf(98);
   private static final Integer RESOURCE_TEXT8 = Integer.valueOf(99);
   private static final Integer RESOURCE_TEXT9 = Integer.valueOf(100);
   private static final Integer RESOURCE_TEXT10 = Integer.valueOf(101);
   private static final Integer RESOURCE_TEXT11 = Integer.valueOf(225);
   private static final Integer RESOURCE_TEXT12 = Integer.valueOf(226);
   private static final Integer RESOURCE_TEXT13 = Integer.valueOf(227);
   private static final Integer RESOURCE_TEXT14 = Integer.valueOf(228);
   private static final Integer RESOURCE_TEXT15 = Integer.valueOf(229);
   private static final Integer RESOURCE_TEXT16 = Integer.valueOf(230);
   private static final Integer RESOURCE_TEXT17 = Integer.valueOf(231);
   private static final Integer RESOURCE_TEXT18 = Integer.valueOf(232);
   private static final Integer RESOURCE_TEXT19 = Integer.valueOf(233);
   private static final Integer RESOURCE_TEXT20 = Integer.valueOf(234);
   private static final Integer RESOURCE_TEXT21 = Integer.valueOf(235);
   private static final Integer RESOURCE_TEXT22 = Integer.valueOf(236);
   private static final Integer RESOURCE_TEXT23 = Integer.valueOf(237);
   private static final Integer RESOURCE_TEXT24 = Integer.valueOf(238);
   private static final Integer RESOURCE_TEXT25 = Integer.valueOf(239);
   private static final Integer RESOURCE_TEXT26 = Integer.valueOf(240);
   private static final Integer RESOURCE_TEXT27 = Integer.valueOf(241);
   private static final Integer RESOURCE_TEXT28 = Integer.valueOf(242);
   private static final Integer RESOURCE_TEXT29 = Integer.valueOf(243);
   private static final Integer RESOURCE_TEXT30 = Integer.valueOf(244);

   private static final Integer RESOURCE_ENTERPRISE_COST1 = Integer.valueOf(446);
   private static final Integer RESOURCE_ENTERPRISE_COST2 = Integer.valueOf(447);
   private static final Integer RESOURCE_ENTERPRISE_COST3 = Integer.valueOf(448);
   private static final Integer RESOURCE_ENTERPRISE_COST4 = Integer.valueOf(449);
   private static final Integer RESOURCE_ENTERPRISE_COST5 = Integer.valueOf(450);
   private static final Integer RESOURCE_ENTERPRISE_COST6 = Integer.valueOf(451);
   private static final Integer RESOURCE_ENTERPRISE_COST7 = Integer.valueOf(452);
   private static final Integer RESOURCE_ENTERPRISE_COST8 = Integer.valueOf(453);
   private static final Integer RESOURCE_ENTERPRISE_COST9 = Integer.valueOf(454);
   private static final Integer RESOURCE_ENTERPRISE_COST10 = Integer.valueOf(455);

   private static final Integer RESOURCE_ENTERPRISE_DATE1 = Integer.valueOf(456);
   private static final Integer RESOURCE_ENTERPRISE_DATE2 = Integer.valueOf(457);
   private static final Integer RESOURCE_ENTERPRISE_DATE3 = Integer.valueOf(458);
   private static final Integer RESOURCE_ENTERPRISE_DATE4 = Integer.valueOf(459);
   private static final Integer RESOURCE_ENTERPRISE_DATE5 = Integer.valueOf(460);
   private static final Integer RESOURCE_ENTERPRISE_DATE6 = Integer.valueOf(461);
   private static final Integer RESOURCE_ENTERPRISE_DATE7 = Integer.valueOf(462);
   private static final Integer RESOURCE_ENTERPRISE_DATE8 = Integer.valueOf(463);
   private static final Integer RESOURCE_ENTERPRISE_DATE9 = Integer.valueOf(464);
   private static final Integer RESOURCE_ENTERPRISE_DATE10 = Integer.valueOf(465);
   private static final Integer RESOURCE_ENTERPRISE_DATE11 = Integer.valueOf(466);
   private static final Integer RESOURCE_ENTERPRISE_DATE12 = Integer.valueOf(467);
   private static final Integer RESOURCE_ENTERPRISE_DATE13 = Integer.valueOf(468);
   private static final Integer RESOURCE_ENTERPRISE_DATE14 = Integer.valueOf(469);
   private static final Integer RESOURCE_ENTERPRISE_DATE15 = Integer.valueOf(470);
   private static final Integer RESOURCE_ENTERPRISE_DATE16 = Integer.valueOf(471);
   private static final Integer RESOURCE_ENTERPRISE_DATE17 = Integer.valueOf(472);
   private static final Integer RESOURCE_ENTERPRISE_DATE18 = Integer.valueOf(473);
   private static final Integer RESOURCE_ENTERPRISE_DATE19 = Integer.valueOf(474);
   private static final Integer RESOURCE_ENTERPRISE_DATE20 = Integer.valueOf(475);
   private static final Integer RESOURCE_ENTERPRISE_DATE21 = Integer.valueOf(476);
   private static final Integer RESOURCE_ENTERPRISE_DATE22 = Integer.valueOf(477);
   private static final Integer RESOURCE_ENTERPRISE_DATE23 = Integer.valueOf(478);
   private static final Integer RESOURCE_ENTERPRISE_DATE24 = Integer.valueOf(479);
   private static final Integer RESOURCE_ENTERPRISE_DATE25 = Integer.valueOf(480);
   private static final Integer RESOURCE_ENTERPRISE_DATE26 = Integer.valueOf(481);
   private static final Integer RESOURCE_ENTERPRISE_DATE27 = Integer.valueOf(482);
   private static final Integer RESOURCE_ENTERPRISE_DATE28 = Integer.valueOf(483);
   private static final Integer RESOURCE_ENTERPRISE_DATE29 = Integer.valueOf(484);
   private static final Integer RESOURCE_ENTERPRISE_DATE30 = Integer.valueOf(485);

   private static final Integer RESOURCE_ENTERPRISE_DURATION1 = Integer.valueOf(486);
   private static final Integer RESOURCE_ENTERPRISE_DURATION2 = Integer.valueOf(487);
   private static final Integer RESOURCE_ENTERPRISE_DURATION3 = Integer.valueOf(488);
   private static final Integer RESOURCE_ENTERPRISE_DURATION4 = Integer.valueOf(489);
   private static final Integer RESOURCE_ENTERPRISE_DURATION5 = Integer.valueOf(490);
   private static final Integer RESOURCE_ENTERPRISE_DURATION6 = Integer.valueOf(491);
   private static final Integer RESOURCE_ENTERPRISE_DURATION7 = Integer.valueOf(492);
   private static final Integer RESOURCE_ENTERPRISE_DURATION8 = Integer.valueOf(493);
   private static final Integer RESOURCE_ENTERPRISE_DURATION9 = Integer.valueOf(494);
   private static final Integer RESOURCE_ENTERPRISE_DURATION10 = Integer.valueOf(495);

   private static final Integer RESOURCE_ENTERPRISE_DURATION1_UNITS = Integer.valueOf(496);
   private static final Integer RESOURCE_ENTERPRISE_DURATION2_UNITS = Integer.valueOf(497);
   private static final Integer RESOURCE_ENTERPRISE_DURATION3_UNITS = Integer.valueOf(498);
   private static final Integer RESOURCE_ENTERPRISE_DURATION4_UNITS = Integer.valueOf(499);
   private static final Integer RESOURCE_ENTERPRISE_DURATION5_UNITS = Integer.valueOf(500);
   private static final Integer RESOURCE_ENTERPRISE_DURATION6_UNITS = Integer.valueOf(501);
   private static final Integer RESOURCE_ENTERPRISE_DURATION7_UNITS = Integer.valueOf(502);
   private static final Integer RESOURCE_ENTERPRISE_DURATION8_UNITS = Integer.valueOf(503);
   private static final Integer RESOURCE_ENTERPRISE_DURATION9_UNITS = Integer.valueOf(504);
   private static final Integer RESOURCE_ENTERPRISE_DURATION10_UNITS = Integer.valueOf(505);

   private static final Integer RESOURCE_ENTERPRISE_NUMBER1 = Integer.valueOf(546);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER2 = Integer.valueOf(547);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER3 = Integer.valueOf(548);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER4 = Integer.valueOf(549);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER5 = Integer.valueOf(550);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER6 = Integer.valueOf(551);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER7 = Integer.valueOf(552);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER8 = Integer.valueOf(553);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER9 = Integer.valueOf(554);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER10 = Integer.valueOf(555);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER11 = Integer.valueOf(556);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER12 = Integer.valueOf(557);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER13 = Integer.valueOf(558);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER14 = Integer.valueOf(559);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER15 = Integer.valueOf(560);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER16 = Integer.valueOf(561);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER17 = Integer.valueOf(562);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER18 = Integer.valueOf(563);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER19 = Integer.valueOf(564);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER20 = Integer.valueOf(565);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER21 = Integer.valueOf(566);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER22 = Integer.valueOf(567);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER23 = Integer.valueOf(568);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER24 = Integer.valueOf(569);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER25 = Integer.valueOf(570);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER26 = Integer.valueOf(571);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER27 = Integer.valueOf(572);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER28 = Integer.valueOf(573);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER29 = Integer.valueOf(574);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER30 = Integer.valueOf(575);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER31 = Integer.valueOf(576);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER32 = Integer.valueOf(577);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER33 = Integer.valueOf(578);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER34 = Integer.valueOf(579);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER35 = Integer.valueOf(580);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER36 = Integer.valueOf(581);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER37 = Integer.valueOf(582);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER38 = Integer.valueOf(583);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER39 = Integer.valueOf(584);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER40 = Integer.valueOf(585);

   private static final Integer RESOURCE_ENTERPRISE_TEXT1 = Integer.valueOf(646);
   private static final Integer RESOURCE_ENTERPRISE_TEXT2 = Integer.valueOf(647);
   private static final Integer RESOURCE_ENTERPRISE_TEXT3 = Integer.valueOf(648);
   private static final Integer RESOURCE_ENTERPRISE_TEXT4 = Integer.valueOf(649);
   private static final Integer RESOURCE_ENTERPRISE_TEXT5 = Integer.valueOf(650);
   private static final Integer RESOURCE_ENTERPRISE_TEXT6 = Integer.valueOf(651);
   private static final Integer RESOURCE_ENTERPRISE_TEXT7 = Integer.valueOf(652);
   private static final Integer RESOURCE_ENTERPRISE_TEXT8 = Integer.valueOf(653);
   private static final Integer RESOURCE_ENTERPRISE_TEXT9 = Integer.valueOf(654);
   private static final Integer RESOURCE_ENTERPRISE_TEXT10 = Integer.valueOf(655);
   private static final Integer RESOURCE_ENTERPRISE_TEXT11 = Integer.valueOf(656);
   private static final Integer RESOURCE_ENTERPRISE_TEXT12 = Integer.valueOf(657);
   private static final Integer RESOURCE_ENTERPRISE_TEXT13 = Integer.valueOf(658);
   private static final Integer RESOURCE_ENTERPRISE_TEXT14 = Integer.valueOf(659);
   private static final Integer RESOURCE_ENTERPRISE_TEXT15 = Integer.valueOf(660);
   private static final Integer RESOURCE_ENTERPRISE_TEXT16 = Integer.valueOf(661);
   private static final Integer RESOURCE_ENTERPRISE_TEXT17 = Integer.valueOf(662);
   private static final Integer RESOURCE_ENTERPRISE_TEXT18 = Integer.valueOf(663);
   private static final Integer RESOURCE_ENTERPRISE_TEXT19 = Integer.valueOf(664);
   private static final Integer RESOURCE_ENTERPRISE_TEXT20 = Integer.valueOf(665);
   private static final Integer RESOURCE_ENTERPRISE_TEXT21 = Integer.valueOf(666);
   private static final Integer RESOURCE_ENTERPRISE_TEXT22 = Integer.valueOf(667);
   private static final Integer RESOURCE_ENTERPRISE_TEXT23 = Integer.valueOf(668);
   private static final Integer RESOURCE_ENTERPRISE_TEXT24 = Integer.valueOf(669);
   private static final Integer RESOURCE_ENTERPRISE_TEXT25 = Integer.valueOf(670);
   private static final Integer RESOURCE_ENTERPRISE_TEXT26 = Integer.valueOf(671);
   private static final Integer RESOURCE_ENTERPRISE_TEXT27 = Integer.valueOf(672);
   private static final Integer RESOURCE_ENTERPRISE_TEXT28 = Integer.valueOf(673);
   private static final Integer RESOURCE_ENTERPRISE_TEXT29 = Integer.valueOf(674);
   private static final Integer RESOURCE_ENTERPRISE_TEXT30 = Integer.valueOf(675);
   private static final Integer RESOURCE_ENTERPRISE_TEXT31 = Integer.valueOf(676);
   private static final Integer RESOURCE_ENTERPRISE_TEXT32 = Integer.valueOf(677);
   private static final Integer RESOURCE_ENTERPRISE_TEXT33 = Integer.valueOf(678);
   private static final Integer RESOURCE_ENTERPRISE_TEXT34 = Integer.valueOf(679);
   private static final Integer RESOURCE_ENTERPRISE_TEXT35 = Integer.valueOf(680);
   private static final Integer RESOURCE_ENTERPRISE_TEXT36 = Integer.valueOf(681);
   private static final Integer RESOURCE_ENTERPRISE_TEXT37 = Integer.valueOf(682);
   private static final Integer RESOURCE_ENTERPRISE_TEXT38 = Integer.valueOf(683);
   private static final Integer RESOURCE_ENTERPRISE_TEXT39 = Integer.valueOf(684);
   private static final Integer RESOURCE_ENTERPRISE_TEXT40 = Integer.valueOf(685);

   public static final Integer RESOURCE_BASELINE1_WORK = Integer.valueOf(342);
   public static final Integer RESOURCE_BASELINE1_COST = Integer.valueOf(343);
   public static final Integer RESOURCE_BASELINE2_WORK = Integer.valueOf(352);
   public static final Integer RESOURCE_BASELINE2_COST = Integer.valueOf(353);
   public static final Integer RESOURCE_BASELINE3_WORK = Integer.valueOf(362);
   public static final Integer RESOURCE_BASELINE3_COST = Integer.valueOf(363);
   public static final Integer RESOURCE_BASELINE4_WORK = Integer.valueOf(372);
   public static final Integer RESOURCE_BASELINE4_COST = Integer.valueOf(373);
   public static final Integer RESOURCE_BASELINE5_WORK = Integer.valueOf(382);
   public static final Integer RESOURCE_BASELINE5_COST = Integer.valueOf(383);
   public static final Integer RESOURCE_BASELINE6_WORK = Integer.valueOf(392);
   public static final Integer RESOURCE_BASELINE6_COST = Integer.valueOf(393);
   public static final Integer RESOURCE_BASELINE7_WORK = Integer.valueOf(402);
   public static final Integer RESOURCE_BASELINE7_COST = Integer.valueOf(403);
   public static final Integer RESOURCE_BASELINE8_WORK = Integer.valueOf(412);
   public static final Integer RESOURCE_BASELINE8_COST = Integer.valueOf(413);
   public static final Integer RESOURCE_BASELINE9_WORK = Integer.valueOf(422);
   public static final Integer RESOURCE_BASELINE9_COST = Integer.valueOf(423);
   public static final Integer RESOURCE_BASELINE10_WORK = Integer.valueOf(432);
   public static final Integer RESOURCE_BASELINE10_COST = Integer.valueOf(433);

   //
   // Unverified
   //
   private static final Integer RESOURCE_SUBPROJECTRESOURCEID = Integer.valueOf(102);
   private static final Integer RESOURCE_NOTES = Integer.valueOf(20);

   private static final Integer TABLE_COLUMN_DATA_STANDARD = Integer.valueOf(6);
   private static final Integer TABLE_COLUMN_DATA_ENTERPRISE = Integer.valueOf(7);
   private static final Integer TABLE_COLUMN_DATA_BASELINE = Integer.valueOf(8);
   private static final Integer OUTLINECODE_DATA = Integer.valueOf(22);

   // Custom value list types
   private static final Integer VALUE_LIST_VALUE = Integer.valueOf(22);
   private static final Integer VALUE_LIST_DESCRIPTION = Integer.valueOf(8);
   private static final Integer VALUE_LIST_UNKNOWN = Integer.valueOf(23);

   private static final Integer RESOURCE_COST_RATE_A = Integer.valueOf(61);
   private static final Integer RESOURCE_COST_RATE_B = Integer.valueOf(62);
   private static final Integer RESOURCE_COST_RATE_C = Integer.valueOf(63);
   private static final Integer RESOURCE_COST_RATE_D = Integer.valueOf(64);
   private static final Integer RESOURCE_COST_RATE_E = Integer.valueOf(65);

   private static final Integer RESOURCE_AVAILABILITY = Integer.valueOf(276);

   private static final int VALUE_LIST_MASK = 0x0700;

   /**
    * Mask used to isolate confirmed flag from the duration units field.
    */
   private static final int DURATION_CONFIRMED_MASK = 0x20;

   private static final Integer RESOURCE_FIELD_NAME_ALIASES = Integer.valueOf(71303169);
   private static final Integer TASK_FIELD_NAME_ALIASES = Integer.valueOf(71303169);

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

   private static final int MINIMUM_EXPECTED_TASK_SIZE = 206;
   private static final int MINIMUM_WORKING_TASK_SIZE = 240;
   private static final int MINIMUM_EXPECTED_RESOURCE_SIZE = 188;
}
