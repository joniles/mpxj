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

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPPResourceField14;
import net.sf.mpxj.MPPTaskField14;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Table;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.View;
import net.sf.mpxj.utility.DateUtility;
import net.sf.mpxj.utility.Pair;
import net.sf.mpxj.utility.RTFUtility;

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
   public void process(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException
   {
      try
      {
         //
         // Retrieve the high level document properties
         //
         Props14 props14 = new Props14(new DocumentInputStream(((DocumentEntry) root.getEntry("Props14"))));
         //System.out.println(props14);

         file.setProjectFilePath(props14.getUnicodeString(Props.PROJECT_FILE_PATH));
         file.setEncoded(props14.getByte(Props.PASSWORD_FLAG) != 0);
         file.setEncryptionCode(props14.getByte(Props.ENCRYPTION_CODE));

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

            // File is password protected for reading, let's read the password
            // and see if the correct read password was given to us.
            //byte[] data = props14.getByteArray(Props.READ_PASSWORD);
            //MPPUtility.decodeBuffer(data, file.getEncryptionCode());
            //System.out.println(MPPUtility.hexdump(data, true, 16, ""));

            //System.out.println();
            //data = props14.getByteArray(Props.WRITE_PASSWORD);
            //MPPUtility.decodeBuffer(data, file.getEncryptionCode());
            //System.out.println(MPPUtility.hexdump(data, true, 16, ""));

            //String readPassword = MPPUtility.decodePassword(props14.getByteArray(Props.READ_PASSWORD), file.getEncryptionCode());
            //System.out.println(readPassword);
            //String writePassword = MPPUtility.decodePassword(props14.getByteArray(Props.WRITE_PASSWORD), file.getEncryptionCode());
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
         m_projectDir = (DirectoryEntry) root.getEntry("   114");
         m_viewDir = (DirectoryEntry) root.getEntry("   214");
         DirectoryEntry outlineCodeDir = (DirectoryEntry) m_projectDir.getEntry("TBkndOutlCode");
         m_outlineCodeVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("VarMeta"))));
         m_outlineCodeVarData = new Var2Data(m_outlineCodeVarMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Var2Data"))));
         m_projectProps = new Props14(getEncryptableInputStream(m_projectDir, "Props"));
         //MPPUtility.fileDump("c:\\temp\\props.txt", m_projectProps.toString().getBytes());
         //FieldMap fm = new FieldMap14(m_file);
         //fm.dumpKnownFieldMaps(m_projectProps);

         m_fontBases = new HashMap<Integer, FontBase>();
         m_taskSubProjects = new HashMap<Integer, SubProject>();
         m_parentTasks = new HashMap<Integer, Integer>();
         m_taskOrder = new TreeMap<Long, Integer>();
         m_nullTaskOrder = new TreeMap<Integer, Integer>();

         m_file.setMppFileType(14);
         m_file.setAutoFilter(props14.getBoolean(Props.AUTO_FILTER));

         processCustomValueLists();
         processPropertyData();
         processCalendarData();
         processResourceData();
         processTaskData();
         processConstraintData();
         processAssignmentData();

         //
         // MPP14 files seem to exhibit some occasional weirdness
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
               case SUBPROJECT_TASKUNIQUEID6 :
               case SUBPROJECT_TASKUNIQUEID7 :
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
      Props14 props = new Props14(getEncryptableInputStream(m_viewDir, "Props"));
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
               m_file.setTaskFieldAlias(MPPTaskField14.getInstance(field), alias);
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
               m_file.setResourceFieldAlias(MPPResourceField14.getInstance(field), alias);
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
    * @param fieldMap field map
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @return Mapping between task identifiers and block position
    */
   private TreeMap<Integer, Integer> createTaskMap(FieldMap fieldMap, FixedMeta taskFixedMeta, FixedData taskFixedData)
   {
      TreeMap<Integer, Integer> taskMap = new TreeMap<Integer, Integer>();
      int itemCount = taskFixedMeta.getItemCount();
      int uniqueID;
      Integer key;

      // First three items are not tasks, so let's skip them
      for (int loop = 3; loop < itemCount; loop++)
      {
         byte[] data = taskFixedData.getByteArrayValue(loop);
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
               //
               // We apply a heuristic here - if we have more than 75% of the data, we assume 
               // the task is valid.
               //
               if (data.length == 16 || ((data.length * 100) / fieldMap.getMaxFixedDataOffset(0)) > 75)
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
    * @param fieldMap field map
    * @param rscFixedMeta resource fixed meta data
    * @param rscFixedData resource fixed data
    * @return map of resource IDs to resource data
    */
   private TreeMap<Integer, Integer> createResourceMap(FieldMap fieldMap, FixedMeta rscFixedMeta, FixedData rscFixedData)
   {
      TreeMap<Integer, Integer> resourceMap = new TreeMap<Integer, Integer>();
      int itemCount = rscFixedMeta.getItemCount();

      for (int loop = 0; loop < itemCount; loop++)
      {
         byte[] data = rscFixedData.getByteArrayValue(loop);
         if (data == null || data.length <= fieldMap.getMaxFixedDataOffset(0))
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

      //FixedMeta calFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("Fixed2Meta"))), 9);
      //FixedData calFixed2Data = new FixedData(calFixed2Meta, getEncryptableInputStream(calDir, "Fixed2Data"), 48);
      //System.out.println (calFixed2Meta);
      //System.out.println (calFixed2Data);

      HashMap<Integer, ProjectCalendar> calendarMap = new HashMap<Integer, ProjectCalendar>();
      int items = calFixedData.getItemCount();
      List<Pair<ProjectCalendar, Integer>> baseCalendars = new LinkedList<Pair<ProjectCalendar, Integer>>();
      byte[] defaultCalendarData = m_projectProps.getByteArray(Props.DEFAULT_CALENDAR_HOURS);

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
                  m_file.fireCalendarReadEvent(cal);
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
                  hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
                  hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
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
      FieldMap fieldMap = new FieldMap14(m_file);
      fieldMap.createTaskFieldMap(m_projectProps);

      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      VarMeta taskVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data(taskVarMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData(taskFixedMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedData"))), fieldMap.getMaxFixedDataOffset(0));
      FixedMeta taskFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Meta"))), 92);
      FixedData taskFixed2Data = new FixedData(taskFixed2Meta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Fixed2Data"))));

      Props14 props = new Props14(getEncryptableInputStream(taskDir, "Props"));
      //System.out.println(taskFixedMeta);
      //System.out.println(taskFixedData);
      //System.out.println(taskVarMeta);
      //System.out.println(taskVarData);
      //System.out.println(taskFixed2Meta);
      //System.out.println(taskFixed2Data);
      //System.out.println(m_outlineCodeVarData.getVarMeta());
      //System.out.println(m_outlineCodeVarData);
      //System.out.println(props);

      // Process aliases      
      processTaskFieldNameAliases(props.getByteArray(TASK_FIELD_NAME_ALIASES));

      TreeMap<Integer, Integer> taskMap = createTaskMap(fieldMap, taskFixedMeta, taskFixedData);
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

         if (data.length < fieldMap.getMaxFixedDataOffset(0))
         {
            byte[] newData = new byte[fieldMap.getMaxFixedDataOffset(0) + 8];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
         }

         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
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

         byte[] recurringData = taskVarData.getByteArray(id, fieldMap.getVarDataKey(TaskField.RECURRING_DATA));

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

         task.disableEvents();
         fieldMap.populateContainer(task, id, new byte[][]
         {
            data,
            data2
         }, taskVarData);
         task.enableEvents();

         task.setActive((metaData2[8] & 0x04) != 0);

         task.setEffortDriven((metaData[11] & 0x10) != 0);
         task.setEstimated(getDurationEstimated(MPPUtility.getShort(data, fieldMap.getFixedDataOffset(TaskField.ACTUAL_DURATION_UNITS))));
         task.setExpanded(((metaData[12] & 0x02) == 0));

         Integer externalTaskID = task.getSubprojectTaskID();
         if (externalTaskID != null && externalTaskID.intValue() != 0)
         {
            task.setExternalTask(true);
            externalTasks.add(task);
         }

         task.setFlag1((metaData[35] & 0x40) != 0);
         task.setFlag2((metaData[35] & 0x80) != 0);
         task.setFlag3((metaData[36] & 0x01) != 0);
         task.setFlag4((metaData[36] & 0x02) != 0);
         task.setFlag5((metaData[36] & 0x04) != 0);
         task.setFlag6((metaData[36] & 0x08) != 0);
         task.setFlag7((metaData[36] & 0x10) != 0);
         task.setFlag8((metaData[36] & 0x20) != 0);
         task.setFlag9((metaData[36] & 0x40) != 0);
         task.setFlag10((metaData[36] & 0x80) != 0);
         task.setFlag11((metaData[37] & 0x01) != 0);
         task.setFlag12((metaData[37] & 0x02) != 0);
         task.setFlag13((metaData[37] & 0x04) != 0);
         task.setFlag14((metaData[37] & 0x08) != 0);
         task.setFlag15((metaData[37] & 0x10) != 0);
         task.setFlag16((metaData[37] & 0x20) != 0);
         task.setFlag17((metaData[37] & 0x40) != 0);
         task.setFlag18((metaData[37] & 0x80) != 0);
         task.setFlag19((metaData[38] & 0x01) != 0);
         task.setFlag20((metaData[38] & 0x02) != 0);

         task.setHideBar((metaData[10] & 0x80) != 0);

         processHyperlinkData(task, taskVarData.getByteArray(id, fieldMap.getVarDataKey(TaskField.HYPERLINK_DATA)));

         task.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));
         task.setIgnoreResourceCalendar((metaData[10] & 0x02) != 0);
         task.setLevelAssignments((metaData[13] & 0x04) != 0);
         task.setLevelingCanSplit((metaData[13] & 0x02) != 0);
         task.setMarked((metaData[9] & 0x40) != 0);
         task.setMilestone((metaData[8] & 0x20) != 0);

         task.setOutlineCode1(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE1_INDEX)));
         task.setOutlineCode2(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE2_INDEX)));
         task.setOutlineCode3(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE3_INDEX)));
         task.setOutlineCode4(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE4_INDEX)));
         task.setOutlineCode5(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE5_INDEX)));
         task.setOutlineCode6(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE6_INDEX)));
         task.setOutlineCode7(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE7_INDEX)));
         task.setOutlineCode8(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE8_INDEX)));
         task.setOutlineCode9(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE9_INDEX)));
         task.setOutlineCode10(getCustomFieldOutlineCodeValue(taskVarData, m_outlineCodeVarData, id, fieldMap.getVarDataKey(TaskField.OUTLINE_CODE10_INDEX)));

         task.setRecurring(MPPUtility.getShort(data, 40) == 2);
         task.setRollup((metaData[10] & 0x08) != 0);
         task.setTaskMode((metaData2[8] & 0x08) == 0 ? TaskMode.AUTO_SCHEDULED : TaskMode.MANUALLY_SCHEDULED);
         task.setUniqueID(id);

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
            task.setRecurring(true);
         }

         //
         // Retrieve the task notes.
         //
         notes = task.getNotes();
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
         Integer calendarID = (Integer) task.getCachedValue(TaskField.CALENDAR_UNIQUE_ID);
         if (calendarID != null && calendarID.intValue() != -1)
         {
            ProjectCalendar calendar = m_file.getBaseCalendarByUniqueID(calendarID);
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

         //System.out.println(task + " " + MPPUtility.getShort(data2, 22)); // JPI - looks like this value determines the ID order! Validate and check other versions!
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
                           Relation relation = task2.addPredecessor(task1, type, lag);
                           m_file.fireRelationReadEvent(relation);
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
      FieldMap fieldMap = new FieldMap14(m_file);
      fieldMap.createResourceFieldMap(m_projectProps);

      DirectoryEntry rscDir = (DirectoryEntry) m_projectDir.getEntry("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data(rscVarMeta, new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData(rscFixedMeta, getEncryptableInputStream(rscDir, "FixedData"));
      FixedMeta rscFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Fixed2Meta"))), 50);
      FixedData rscFixed2Data = new FixedData(rscFixed2Meta, getEncryptableInputStream(rscDir, "Fixed2Data"));
      Props14 props = new Props14(getEncryptableInputStream(rscDir, "Props"));
      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      //System.out.println(rscFixedMeta);
      //System.out.println(rscFixedData);
      //System.out.println(rscFixed2Meta);
      //System.out.println(rscFixed2Data);
      //System.out.println(props);

      // Process aliases      
      processResourceFieldNameAliases(props.getByteArray(RESOURCE_FIELD_NAME_ALIASES));

      TreeMap<Integer, Integer> resourceMap = createResourceMap(fieldMap, rscFixedMeta, rscFixedData);
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

         resource.disableEvents();
         fieldMap.populateContainer(resource, id, new byte[][]
         {
            data,
            data2
         }, rscVarData);
         resource.enableEvents();

         resource.setBudget((metaData2[8] & 0x20) != 0);

         processHyperlinkData(resource, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.HYPERLINK_DATA)));

         resource.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));

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

         resource.setType((MPPUtility.getShort(data, 14) == 0 ? ResourceType.WORK : ((metaData2[8] & 0x10) == 0) ? ResourceType.MATERIAL : ResourceType.COST));
         resource.setUniqueID(id);

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

         notes = resource.getNotes();
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
         resource.setCostRateTable(0, crt.process(rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_A))));
         resource.setCostRateTable(1, crt.process(rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_B))));
         resource.setCostRateTable(2, crt.process(rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_C))));
         resource.setCostRateTable(3, crt.process(rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_D))));
         resource.setCostRateTable(4, crt.process(rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.COST_RATE_E))));

         //
         // Process availability table
         //
         AvailabilityFactory af = new AvailabilityFactory();
         af.process(resource.getAvailability(), rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.AVAILABILITY_DATA)));

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
      FieldMap fieldMap = new FieldMap14(m_file);
      fieldMap.createAssignmentFieldMap(m_projectProps);

      DirectoryEntry assnDir = (DirectoryEntry) m_projectDir.getEntry("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta12(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data(assnVarMeta, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData(110, getEncryptableInputStream(assnDir, "FixedData"));
      FixedData assnFixedData2 = new FixedData(48, getEncryptableInputStream(assnDir, "Fixed2Data"));
      //FixedMeta assnFixedMeta2 = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Fixed2Meta"))), 53);
      //Props props = new Props14(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Props"))));

      ResourceAssignmentFactoryCommon factory = new ResourceAssignmentFactoryCommon();
      factory.process(m_file, fieldMap, m_reader.getUseRawTimephasedData(), m_reader.getPreserveNoteFormatting(), assnVarMeta, assnVarData, assnFixedMeta, assnFixedData, assnFixedData2);
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

      TableFactory14 factory = new TableFactory14(TABLE_COLUMN_DATA_STANDARD, TABLE_COLUMN_DATA_ENTERPRISE, TABLE_COLUMN_DATA_BASELINE);
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

      FilterReader reader = new FilterReader14();
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
   private Props14 m_projectProps;
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
   private static final int SUBPROJECT_TASKUNIQUEID6 = 0x067F0000;
   private static final int SUBPROJECT_TASKUNIQUEID7 = 0x067D0000;

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

   // Custom value list types
   private static final Integer VALUE_LIST_VALUE = Integer.valueOf(22);
   private static final Integer VALUE_LIST_DESCRIPTION = Integer.valueOf(8);
   private static final Integer VALUE_LIST_UNKNOWN = Integer.valueOf(23);

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
}
