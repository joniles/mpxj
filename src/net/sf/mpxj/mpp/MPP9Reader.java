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
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
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
   public void process(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException
   {
      try
      {
         //
         // Retrieve the high level document properties (never encoded)
         //
         Props9 props9 = new Props9(new DocumentInputStream(((DocumentEntry) root.getEntry("Props9"))));
         //System.out.println(props9);

         file.setProjectFilePath(props9.getUnicodeString(Props.PROJECT_FILE_PATH));
         file.setEncoded(props9.getByte(Props.PASSWORD_FLAG) != 0);
         file.setEncryptionCode(props9.getByte(Props.ENCRYPTION_CODE));

         //
         // Test for password protection. In the single byte retrieved here:
         //
         // 0x00 = no password
         // 0x01 = protection password has been supplied
         // 0x02 = write reservation password has been supplied
         // 0x03 = both passwords have been supplied
         //  
         if ((props9.getByte(Props.PASSWORD_FLAG) & 0x01) != 0)
         {
            // File is password protected for reading, let's read the password
            // and see if the correct read password was given to us.
            String readPassword = MPPUtility.decodePassword(props9.getByteArray(Props.PROTECTION_PASSWORD_HASH), file.getEncryptionCode());
            // It looks like it is possible for a project file to have the password protection flag on without a password. In
            // this case MS Project treats the file as NOT protected. We need to do the same. It is worth noting that MS Project does
            // correct the problem if the file is re-saved (at least it did for me).
            if (readPassword != null && readPassword.length() > 0)
            {
               // See if the correct read password was given
               if (reader.getReadPassword() == null || reader.getReadPassword().matches(readPassword) == false)
               {
                  // Passwords don't match
                  throw new MPXJException(MPXJException.PASSWORD_PROTECTED_ENTER_PASSWORD);
               }
            }
            // Passwords matched so let's allow the reading to continue.
         }
         m_reader = reader;
         m_file = file;
         m_root = root;
         m_resourceMap = new HashMap<Integer, ProjectCalendar>();
         m_projectDir = (DirectoryEntry) root.getEntry("   19");
         m_viewDir = (DirectoryEntry) root.getEntry("   29");
         DirectoryEntry outlineCodeDir = (DirectoryEntry) m_projectDir.getEntry("TBkndOutlCode");
         VarMeta outlineCodeVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("VarMeta"))));
         m_outlineCodeVarData = new Var2Data(outlineCodeVarMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Var2Data"))));
         m_projectProps = new Props9(getEncryptableInputStream(m_projectDir, "Props"));
         //MPPUtility.fileDump("c:\\temp\\props.txt", m_projectProps.toString().getBytes());

         m_fontBases = new HashMap<Integer, FontBase>();
         m_taskSubProjects = new HashMap<Integer, SubProject>();

         m_file.setMppFileType(9);
         m_file.setAutoFilter(props9.getBoolean(Props.AUTO_FILTER));

         processPropertyData();
         processCalendarData();
         processResourceData();
         processTaskData();
         processConstraintData();
         processAssignmentData();
         validateTaskIDs();

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
         m_fontBases = null;
         m_taskSubProjects = null;
      }
   }

   /**
    * This method extracts and collates global property data.
    *
    * @throws IOException
    */
   private void processPropertyData() throws IOException, MPXJException
   {
      //
      // Process the project header
      //
      ProjectHeaderReader projectHeaderReader = new ProjectHeaderReader();
      projectHeaderReader.process(m_file, m_projectProps, m_root);

      //
      // Process aliases
      //
      processTaskFieldNameAliases(m_projectProps.getByteArray(Props.TASK_FIELD_NAME_ALIASES));
      processResourceFieldNameAliases(m_projectProps.getByteArray(Props.RESOURCE_FIELD_NAME_ALIASES));

      // Process custom field value lists
      processTaskFieldCustomValueLists(m_file, m_projectProps.getByteArray(Props.TASK_FIELD_CUSTOM_VALUE_LISTS));

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
            itemHeaderOffset = MPPUtility.getShort(subProjData, offset);
            offset += 4;

            MPPUtility.getByteArray(subProjData, itemHeaderOffset, itemHeader.length, itemHeader, 0);

            //            System.out.println ();
            //            System.out.println ();            
            //            System.out.println ("offset=" + offset);
            //            System.out.println ("ItemHeaderOffset=" + itemHeaderOffset);
            //            System.out.println ("type=" + MPPUtility.hexdump(itemHeader, 16, 1, false));
            //            System.out.println (MPPUtility.hexdump(itemHeader, false, 16, ""));            

            byte subProjectType = itemHeader[16];
            switch (subProjectType)
            {
               //
               // Subproject that is no longer inserted. This is a placeholder in order to be
               // able to always guarantee unique unique ids.
               //
               case 0x00 :
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
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // sometimes offset of a task ID?
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // task unique ID, 8 bytes, path, file name
                  //
               case (byte) 0x91 :
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // Unknown offset
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

               case 0x11 :
               case 0x03 :
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // Unknown offset
                  //offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset, index);
                  break;
               }

                  //
                  // task unique ID, path, unknown, file name
                  //
               case (byte) 0x81 :
               case 0x41 :
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // unknown offset to 2 bytes of data?
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
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
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
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

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
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
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
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
               case 0x04 :
               {
                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset, index);
                  // 0x02 looks to be the link FROM the resource pool to a project that uses it 
                  if (subProjectType == 0x04)
                  {
                     m_file.setResourceSubProject(sp);
                  }
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

                  //
                  // Appears when a subproject is collapsed
                  //
               case (byte) 0x80 :
               {
                  offset += 12;
                  break;
               }

                  // deleted entry?
               case 0x10 :
               {
                  offset += 8;
                  break;
               }

                  // new resource pool entry
               case (byte) 0x44 :
               {
                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset, index);
                  m_file.setResourceSubProject(sp);
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

         if (uniqueIDOffset != -1)
         {
            int prev = 0;
            int value = MPPUtility.getInt(data, uniqueIDOffset);
            while (value != SUBPROJECT_LISTEND)
            {
               switch (value)
               {
                  case SUBPROJECT_TASKUNIQUEID0 :
                  case SUBPROJECT_TASKUNIQUEID1 :
                  case SUBPROJECT_TASKUNIQUEID2 :
                  case SUBPROJECT_TASKUNIQUEID3 :
                  case SUBPROJECT_TASKUNIQUEID4 :
                  case SUBPROJECT_TASKUNIQUEID5 :
                     // The previous value was for the subproject unique task id
                     sp.setTaskUniqueID(Integer.valueOf(prev));
                     m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                     prev = 0;
                     break;

                  default :
                     if (prev != 0)
                     {
                        // The previous value was for an external task unique task id
                        sp.addExternalTaskUniqueID(Integer.valueOf(prev));
                        m_taskSubProjects.put(Integer.valueOf(prev), sp);
                     }
                     prev = value;
                     break;
               }
               // Read the next value
               uniqueIDOffset += 4;
               value = MPPUtility.getInt(data, uniqueIDOffset);
            }
            if (prev != 0)
            {
               // The previous value was for an external task unique task id
               sp.addExternalTaskUniqueID(Integer.valueOf(prev));
               m_taskSubProjects.put(Integer.valueOf(prev), sp);
            }

            // Now get the unique id offset for this subproject
            value = 0x00800000 + ((subprojectIndex - 1) * 0x00400000);
            sp.setUniqueIDOffset(Integer.valueOf(value));
         }

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
            // filePathOffset += size;
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
      Props9 props = new Props9(getEncryptableInputStream(m_viewDir, "Props"));
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
         int offset = 0;
         ArrayList<String> aliases = new ArrayList<String>(300);

         while (offset < data.length)
         {
            String alias = MPPUtility.getUnicodeString(data, offset);
            aliases.add(alias);
            offset += (alias.length() + 1) * 2;
         }

         m_file.setTaskFieldAlias(TaskField.TEXT1, aliases.get(118));
         m_file.setTaskFieldAlias(TaskField.TEXT2, aliases.get(119));
         m_file.setTaskFieldAlias(TaskField.TEXT3, aliases.get(120));
         m_file.setTaskFieldAlias(TaskField.TEXT4, aliases.get(121));
         m_file.setTaskFieldAlias(TaskField.TEXT5, aliases.get(122));
         m_file.setTaskFieldAlias(TaskField.TEXT6, aliases.get(123));
         m_file.setTaskFieldAlias(TaskField.TEXT7, aliases.get(124));
         m_file.setTaskFieldAlias(TaskField.TEXT8, aliases.get(125));
         m_file.setTaskFieldAlias(TaskField.TEXT9, aliases.get(126));
         m_file.setTaskFieldAlias(TaskField.TEXT10, aliases.get(127));
         m_file.setTaskFieldAlias(TaskField.START1, aliases.get(128));
         m_file.setTaskFieldAlias(TaskField.FINISH1, aliases.get(129));
         m_file.setTaskFieldAlias(TaskField.START2, aliases.get(130));
         m_file.setTaskFieldAlias(TaskField.FINISH2, aliases.get(131));
         m_file.setTaskFieldAlias(TaskField.START3, aliases.get(132));
         m_file.setTaskFieldAlias(TaskField.FINISH3, aliases.get(133));
         m_file.setTaskFieldAlias(TaskField.START4, aliases.get(134));
         m_file.setTaskFieldAlias(TaskField.FINISH4, aliases.get(135));
         m_file.setTaskFieldAlias(TaskField.START5, aliases.get(136));
         m_file.setTaskFieldAlias(TaskField.FINISH5, aliases.get(137));
         m_file.setTaskFieldAlias(TaskField.START6, aliases.get(138));
         m_file.setTaskFieldAlias(TaskField.FINISH6, aliases.get(139));
         m_file.setTaskFieldAlias(TaskField.START7, aliases.get(140));
         m_file.setTaskFieldAlias(TaskField.FINISH7, aliases.get(141));
         m_file.setTaskFieldAlias(TaskField.START8, aliases.get(142));
         m_file.setTaskFieldAlias(TaskField.FINISH8, aliases.get(143));
         m_file.setTaskFieldAlias(TaskField.START9, aliases.get(144));
         m_file.setTaskFieldAlias(TaskField.FINISH9, aliases.get(145));
         m_file.setTaskFieldAlias(TaskField.START10, aliases.get(146));
         m_file.setTaskFieldAlias(TaskField.FINISH10, aliases.get(147));
         m_file.setTaskFieldAlias(TaskField.NUMBER1, aliases.get(149));
         m_file.setTaskFieldAlias(TaskField.NUMBER2, aliases.get(150));
         m_file.setTaskFieldAlias(TaskField.NUMBER3, aliases.get(151));
         m_file.setTaskFieldAlias(TaskField.NUMBER4, aliases.get(152));
         m_file.setTaskFieldAlias(TaskField.NUMBER5, aliases.get(153));
         m_file.setTaskFieldAlias(TaskField.NUMBER6, aliases.get(154));
         m_file.setTaskFieldAlias(TaskField.NUMBER7, aliases.get(155));
         m_file.setTaskFieldAlias(TaskField.NUMBER8, aliases.get(156));
         m_file.setTaskFieldAlias(TaskField.NUMBER9, aliases.get(157));
         m_file.setTaskFieldAlias(TaskField.NUMBER10, aliases.get(158));
         m_file.setTaskFieldAlias(TaskField.DURATION1, aliases.get(159));
         m_file.setTaskFieldAlias(TaskField.DURATION2, aliases.get(161));
         m_file.setTaskFieldAlias(TaskField.DURATION3, aliases.get(163));
         m_file.setTaskFieldAlias(TaskField.DURATION4, aliases.get(165));
         m_file.setTaskFieldAlias(TaskField.DURATION5, aliases.get(167));
         m_file.setTaskFieldAlias(TaskField.DURATION6, aliases.get(169));
         m_file.setTaskFieldAlias(TaskField.DURATION7, aliases.get(171));
         m_file.setTaskFieldAlias(TaskField.DURATION8, aliases.get(173));
         m_file.setTaskFieldAlias(TaskField.DURATION9, aliases.get(175));
         m_file.setTaskFieldAlias(TaskField.DURATION10, aliases.get(177));
         m_file.setTaskFieldAlias(TaskField.DATE1, aliases.get(184));
         m_file.setTaskFieldAlias(TaskField.DATE2, aliases.get(185));
         m_file.setTaskFieldAlias(TaskField.DATE3, aliases.get(186));
         m_file.setTaskFieldAlias(TaskField.DATE4, aliases.get(187));
         m_file.setTaskFieldAlias(TaskField.DATE5, aliases.get(188));
         m_file.setTaskFieldAlias(TaskField.DATE6, aliases.get(189));
         m_file.setTaskFieldAlias(TaskField.DATE7, aliases.get(190));
         m_file.setTaskFieldAlias(TaskField.DATE8, aliases.get(191));
         m_file.setTaskFieldAlias(TaskField.DATE9, aliases.get(192));
         m_file.setTaskFieldAlias(TaskField.DATE10, aliases.get(193));
         m_file.setTaskFieldAlias(TaskField.TEXT11, aliases.get(194));
         m_file.setTaskFieldAlias(TaskField.TEXT12, aliases.get(195));
         m_file.setTaskFieldAlias(TaskField.TEXT13, aliases.get(196));
         m_file.setTaskFieldAlias(TaskField.TEXT14, aliases.get(197));
         m_file.setTaskFieldAlias(TaskField.TEXT15, aliases.get(198));
         m_file.setTaskFieldAlias(TaskField.TEXT16, aliases.get(199));
         m_file.setTaskFieldAlias(TaskField.TEXT17, aliases.get(200));
         m_file.setTaskFieldAlias(TaskField.TEXT18, aliases.get(201));
         m_file.setTaskFieldAlias(TaskField.TEXT19, aliases.get(202));
         m_file.setTaskFieldAlias(TaskField.TEXT20, aliases.get(203));
         m_file.setTaskFieldAlias(TaskField.TEXT21, aliases.get(204));
         m_file.setTaskFieldAlias(TaskField.TEXT22, aliases.get(205));
         m_file.setTaskFieldAlias(TaskField.TEXT23, aliases.get(206));
         m_file.setTaskFieldAlias(TaskField.TEXT24, aliases.get(207));
         m_file.setTaskFieldAlias(TaskField.TEXT25, aliases.get(208));
         m_file.setTaskFieldAlias(TaskField.TEXT26, aliases.get(209));
         m_file.setTaskFieldAlias(TaskField.TEXT27, aliases.get(210));
         m_file.setTaskFieldAlias(TaskField.TEXT28, aliases.get(211));
         m_file.setTaskFieldAlias(TaskField.TEXT29, aliases.get(212));
         m_file.setTaskFieldAlias(TaskField.TEXT30, aliases.get(213));
         m_file.setTaskFieldAlias(TaskField.NUMBER11, aliases.get(214));
         m_file.setTaskFieldAlias(TaskField.NUMBER12, aliases.get(215));
         m_file.setTaskFieldAlias(TaskField.NUMBER13, aliases.get(216));
         m_file.setTaskFieldAlias(TaskField.NUMBER14, aliases.get(217));
         m_file.setTaskFieldAlias(TaskField.NUMBER15, aliases.get(218));
         m_file.setTaskFieldAlias(TaskField.NUMBER16, aliases.get(219));
         m_file.setTaskFieldAlias(TaskField.NUMBER17, aliases.get(220));
         m_file.setTaskFieldAlias(TaskField.NUMBER18, aliases.get(221));
         m_file.setTaskFieldAlias(TaskField.NUMBER19, aliases.get(222));
         m_file.setTaskFieldAlias(TaskField.NUMBER20, aliases.get(223));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE1, aliases.get(227));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE2, aliases.get(228));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE3, aliases.get(229));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE4, aliases.get(230));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE5, aliases.get(231));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE6, aliases.get(232));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE7, aliases.get(233));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE8, aliases.get(234));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE9, aliases.get(235));
         m_file.setTaskFieldAlias(TaskField.OUTLINE_CODE10, aliases.get(236));
         m_file.setTaskFieldAlias(TaskField.FLAG1, aliases.get(237));
         m_file.setTaskFieldAlias(TaskField.FLAG2, aliases.get(238));
         m_file.setTaskFieldAlias(TaskField.FLAG3, aliases.get(239));
         m_file.setTaskFieldAlias(TaskField.FLAG4, aliases.get(240));
         m_file.setTaskFieldAlias(TaskField.FLAG5, aliases.get(241));
         m_file.setTaskFieldAlias(TaskField.FLAG6, aliases.get(242));
         m_file.setTaskFieldAlias(TaskField.FLAG7, aliases.get(243));
         m_file.setTaskFieldAlias(TaskField.FLAG8, aliases.get(244));
         m_file.setTaskFieldAlias(TaskField.FLAG9, aliases.get(245));
         m_file.setTaskFieldAlias(TaskField.FLAG10, aliases.get(246));
         m_file.setTaskFieldAlias(TaskField.FLAG11, aliases.get(247));
         m_file.setTaskFieldAlias(TaskField.FLAG12, aliases.get(248));
         m_file.setTaskFieldAlias(TaskField.FLAG13, aliases.get(249));
         m_file.setTaskFieldAlias(TaskField.FLAG14, aliases.get(250));
         m_file.setTaskFieldAlias(TaskField.FLAG15, aliases.get(251));
         m_file.setTaskFieldAlias(TaskField.FLAG16, aliases.get(252));
         m_file.setTaskFieldAlias(TaskField.FLAG17, aliases.get(253));
         m_file.setTaskFieldAlias(TaskField.FLAG18, aliases.get(254));
         m_file.setTaskFieldAlias(TaskField.FLAG19, aliases.get(255));
         m_file.setTaskFieldAlias(TaskField.FLAG20, aliases.get(256));
         m_file.setTaskFieldAlias(TaskField.COST1, aliases.get(278));
         m_file.setTaskFieldAlias(TaskField.COST2, aliases.get(279));
         m_file.setTaskFieldAlias(TaskField.COST3, aliases.get(280));
         m_file.setTaskFieldAlias(TaskField.COST4, aliases.get(281));
         m_file.setTaskFieldAlias(TaskField.COST5, aliases.get(282));
         m_file.setTaskFieldAlias(TaskField.COST6, aliases.get(283));
         m_file.setTaskFieldAlias(TaskField.COST7, aliases.get(284));
         m_file.setTaskFieldAlias(TaskField.COST8, aliases.get(285));
         m_file.setTaskFieldAlias(TaskField.COST9, aliases.get(286));
         m_file.setTaskFieldAlias(TaskField.COST10, aliases.get(287));
      }
   }

   /**
    * Retrieve any task field value lists defined in the MPP file.
    *
    * @param file Parent MPX file
    * @param data task field name alias data
    */
   private void processTaskFieldCustomValueLists(ProjectFile file, byte[] data)
   {
      if (data != null)
      {
         int index = 0;
         int offset = 0;
         // First the length
         int length = MPPUtility.getInt(data, offset);
         offset += 4;
         // Then the number of custom value lists
         int numberOfValueLists = MPPUtility.getInt(data, offset);
         offset += 4;

         // Then the value lists themselves
         TaskField field;
         int valueListOffset = 0;
         while (index < numberOfValueLists && offset < length)
         {
            // Each item consists of the Field ID (2 bytes), 40 0B marker (2 bytes), and the
            // offset to the value list (4 bytes)

            // Get the Field
            field = MPPTaskField.getInstance(MPPUtility.getShort(data, offset));
            offset += 2;
            // Go past 40 0B marker
            offset += 2;
            // Get the value list offset
            valueListOffset = MPPUtility.getInt(data, offset);
            offset += 4;
            // Read the value list itself 
            if (valueListOffset < data.length)
            {
               int tempOffset = valueListOffset;
               tempOffset += 8;
               // Get the data offset
               int dataOffset = MPPUtility.getInt(data, tempOffset) + valueListOffset;
               tempOffset += 4;
               // Get the end of the data offset
               int endDataOffset = MPPUtility.getInt(data, tempOffset) + valueListOffset;
               tempOffset += 4;
               // Get the end of the description
               int endDescriptionOffset = MPPUtility.getInt(data, tempOffset) + valueListOffset;

               // Get the values themselves
               int valuesLength = endDataOffset - dataOffset;
               byte[] values = new byte[valuesLength];
               MPPUtility.getByteArray(data, dataOffset, valuesLength, values, 0);
               file.setTaskFieldValueList(field, getTaskFieldValues(file, field, values));
               //System.out.println(MPPUtility.hexdump(values, true));

               // Get the descriptions
               int descriptionsLength = endDescriptionOffset - endDataOffset;
               byte[] descriptions = new byte[descriptionsLength];
               MPPUtility.getByteArray(data, endDataOffset, descriptionsLength, descriptions, 0);
               file.setTaskFieldDescriptionList(field, getTaskFieldDescriptions(descriptions));
               //System.out.println(MPPUtility.hexdump(descriptions, true));        		
            }
            index++;
         }
      }
      //System.out.println(file.getTaskFieldAliasMap().toString());
   }

   /**
    * Retrieves the description value list associated with a custom task field.
    * This method will return null if no descriptions for the value list has 
    * been defined for this field.
    *
    * @param data data block
    * @return list of descriptions
    */
   public List<String> getTaskFieldDescriptions(byte[] data)
   {
      if (data == null || data.length == 0)
      {
         return null;
      }
      List<String> descriptions = new LinkedList<String>();
      int offset = 0;
      while (offset < data.length)
      {
         String description = MPPUtility.getUnicodeString(data, offset);
         descriptions.add(description);
         offset += description.length() * 2 + 2;
      }
      return descriptions;
   }

   /**
    * Retrieves the description value list associated with a custom task field.
    * This method will return null if no descriptions for the value list has 
    * been defined for this field.
    *
    * @param file parent project file
    * @param field task field
    * @param data data block
    * @return list of task field values
    */
   public List<Object> getTaskFieldValues(ProjectFile file, TaskField field, byte[] data)
   {
      if (field == null || data == null || data.length == 0)
      {
         return null;
      }

      List<Object> list = new LinkedList<Object>();
      int offset = 0;

      switch (field.getDataType())
      {
         case DATE :
            while (offset + 4 <= data.length)
            {
               Date date = MPPUtility.getTimestamp(data, offset);
               list.add(date);
               offset += 4;
            }
            break;
         case CURRENCY :
            while (offset + 8 <= data.length)
            {
               Double number = NumberUtility.getDouble(MPPUtility.getDouble(data, offset) / 100.0);
               list.add(number);
               offset += 8;
            }
            break;
         case NUMERIC :
            while (offset + 8 <= data.length)
            {
               Double number = NumberUtility.getDouble(MPPUtility.getDouble(data, offset));
               list.add(number);
               offset += 8;
            }
            break;
         case DURATION :
            while (offset + 6 <= data.length)
            {
               Duration duration = MPPUtility.getAdjustedDuration(file, MPPUtility.getInt(data, offset), MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, offset + 4)));
               list.add(duration);
               offset += 6;
            }
            break;
         case STRING :
            while (offset < data.length)
            {
               String s = MPPUtility.getUnicodeString(data, offset);
               list.add(s);
               offset += s.length() * 2 + 2;
            }
            break;
         case BOOLEAN :
            while (offset + 2 <= data.length)
            {
               boolean b = (MPPUtility.getShort(data, offset) == 0x01);
               list.add(Boolean.valueOf(b));
               offset += 2;
            }
            break;
         default :
            return null;
      }

      return list;
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
         int offset = 0;
         ArrayList<String> aliases = new ArrayList<String>(250);

         while (offset < data.length)
         {
            String alias = MPPUtility.getUnicodeString(data, offset);
            aliases.add(alias);
            offset += (alias.length() + 1) * 2;
         }

         m_file.setResourceFieldAlias(ResourceField.TEXT1, aliases.get(52));
         m_file.setResourceFieldAlias(ResourceField.TEXT2, aliases.get(53));
         m_file.setResourceFieldAlias(ResourceField.TEXT3, aliases.get(54));
         m_file.setResourceFieldAlias(ResourceField.TEXT4, aliases.get(55));
         m_file.setResourceFieldAlias(ResourceField.TEXT5, aliases.get(56));
         m_file.setResourceFieldAlias(ResourceField.TEXT6, aliases.get(57));
         m_file.setResourceFieldAlias(ResourceField.TEXT7, aliases.get(58));
         m_file.setResourceFieldAlias(ResourceField.TEXT8, aliases.get(59));
         m_file.setResourceFieldAlias(ResourceField.TEXT9, aliases.get(60));
         m_file.setResourceFieldAlias(ResourceField.TEXT10, aliases.get(61));
         m_file.setResourceFieldAlias(ResourceField.TEXT11, aliases.get(62));
         m_file.setResourceFieldAlias(ResourceField.TEXT12, aliases.get(63));
         m_file.setResourceFieldAlias(ResourceField.TEXT13, aliases.get(64));
         m_file.setResourceFieldAlias(ResourceField.TEXT14, aliases.get(65));
         m_file.setResourceFieldAlias(ResourceField.TEXT15, aliases.get(66));
         m_file.setResourceFieldAlias(ResourceField.TEXT16, aliases.get(67));
         m_file.setResourceFieldAlias(ResourceField.TEXT17, aliases.get(68));
         m_file.setResourceFieldAlias(ResourceField.TEXT18, aliases.get(69));
         m_file.setResourceFieldAlias(ResourceField.TEXT19, aliases.get(70));
         m_file.setResourceFieldAlias(ResourceField.TEXT20, aliases.get(71));
         m_file.setResourceFieldAlias(ResourceField.TEXT21, aliases.get(72));
         m_file.setResourceFieldAlias(ResourceField.TEXT22, aliases.get(73));
         m_file.setResourceFieldAlias(ResourceField.TEXT23, aliases.get(74));
         m_file.setResourceFieldAlias(ResourceField.TEXT24, aliases.get(75));
         m_file.setResourceFieldAlias(ResourceField.TEXT25, aliases.get(76));
         m_file.setResourceFieldAlias(ResourceField.TEXT26, aliases.get(77));
         m_file.setResourceFieldAlias(ResourceField.TEXT27, aliases.get(78));
         m_file.setResourceFieldAlias(ResourceField.TEXT28, aliases.get(79));
         m_file.setResourceFieldAlias(ResourceField.TEXT29, aliases.get(80));
         m_file.setResourceFieldAlias(ResourceField.TEXT30, aliases.get(81));
         m_file.setResourceFieldAlias(ResourceField.START1, aliases.get(82));
         m_file.setResourceFieldAlias(ResourceField.START2, aliases.get(83));
         m_file.setResourceFieldAlias(ResourceField.START3, aliases.get(84));
         m_file.setResourceFieldAlias(ResourceField.START4, aliases.get(85));
         m_file.setResourceFieldAlias(ResourceField.START5, aliases.get(86));
         m_file.setResourceFieldAlias(ResourceField.START6, aliases.get(87));
         m_file.setResourceFieldAlias(ResourceField.START7, aliases.get(88));
         m_file.setResourceFieldAlias(ResourceField.START8, aliases.get(89));
         m_file.setResourceFieldAlias(ResourceField.START9, aliases.get(90));
         m_file.setResourceFieldAlias(ResourceField.START10, aliases.get(91));
         m_file.setResourceFieldAlias(ResourceField.FINISH1, aliases.get(92));
         m_file.setResourceFieldAlias(ResourceField.FINISH2, aliases.get(93));
         m_file.setResourceFieldAlias(ResourceField.FINISH3, aliases.get(94));
         m_file.setResourceFieldAlias(ResourceField.FINISH4, aliases.get(95));
         m_file.setResourceFieldAlias(ResourceField.FINISH5, aliases.get(96));
         m_file.setResourceFieldAlias(ResourceField.FINISH6, aliases.get(97));
         m_file.setResourceFieldAlias(ResourceField.FINISH7, aliases.get(98));
         m_file.setResourceFieldAlias(ResourceField.FINISH8, aliases.get(99));
         m_file.setResourceFieldAlias(ResourceField.FINISH9, aliases.get(100));
         m_file.setResourceFieldAlias(ResourceField.FINISH10, aliases.get(101));
         m_file.setResourceFieldAlias(ResourceField.NUMBER1, aliases.get(102));
         m_file.setResourceFieldAlias(ResourceField.NUMBER2, aliases.get(103));
         m_file.setResourceFieldAlias(ResourceField.NUMBER3, aliases.get(104));
         m_file.setResourceFieldAlias(ResourceField.NUMBER4, aliases.get(105));
         m_file.setResourceFieldAlias(ResourceField.NUMBER5, aliases.get(106));
         m_file.setResourceFieldAlias(ResourceField.NUMBER6, aliases.get(107));
         m_file.setResourceFieldAlias(ResourceField.NUMBER7, aliases.get(108));
         m_file.setResourceFieldAlias(ResourceField.NUMBER8, aliases.get(109));
         m_file.setResourceFieldAlias(ResourceField.NUMBER9, aliases.get(110));
         m_file.setResourceFieldAlias(ResourceField.NUMBER10, aliases.get(111));
         m_file.setResourceFieldAlias(ResourceField.NUMBER11, aliases.get(112));
         m_file.setResourceFieldAlias(ResourceField.NUMBER12, aliases.get(113));
         m_file.setResourceFieldAlias(ResourceField.NUMBER13, aliases.get(114));
         m_file.setResourceFieldAlias(ResourceField.NUMBER14, aliases.get(115));
         m_file.setResourceFieldAlias(ResourceField.NUMBER15, aliases.get(116));
         m_file.setResourceFieldAlias(ResourceField.NUMBER16, aliases.get(117));
         m_file.setResourceFieldAlias(ResourceField.NUMBER17, aliases.get(118));
         m_file.setResourceFieldAlias(ResourceField.NUMBER18, aliases.get(119));
         m_file.setResourceFieldAlias(ResourceField.NUMBER19, aliases.get(120));
         m_file.setResourceFieldAlias(ResourceField.NUMBER20, aliases.get(121));
         m_file.setResourceFieldAlias(ResourceField.DURATION1, aliases.get(122));
         m_file.setResourceFieldAlias(ResourceField.DURATION2, aliases.get(123));
         m_file.setResourceFieldAlias(ResourceField.DURATION3, aliases.get(124));
         m_file.setResourceFieldAlias(ResourceField.DURATION4, aliases.get(125));
         m_file.setResourceFieldAlias(ResourceField.DURATION5, aliases.get(126));
         m_file.setResourceFieldAlias(ResourceField.DURATION6, aliases.get(127));
         m_file.setResourceFieldAlias(ResourceField.DURATION7, aliases.get(128));
         m_file.setResourceFieldAlias(ResourceField.DURATION8, aliases.get(129));
         m_file.setResourceFieldAlias(ResourceField.DURATION9, aliases.get(130));
         m_file.setResourceFieldAlias(ResourceField.DURATION10, aliases.get(131));
         m_file.setResourceFieldAlias(ResourceField.DATE1, aliases.get(145));
         m_file.setResourceFieldAlias(ResourceField.DATE2, aliases.get(146));
         m_file.setResourceFieldAlias(ResourceField.DATE3, aliases.get(147));
         m_file.setResourceFieldAlias(ResourceField.DATE4, aliases.get(148));
         m_file.setResourceFieldAlias(ResourceField.DATE5, aliases.get(149));
         m_file.setResourceFieldAlias(ResourceField.DATE6, aliases.get(150));
         m_file.setResourceFieldAlias(ResourceField.DATE7, aliases.get(151));
         m_file.setResourceFieldAlias(ResourceField.DATE8, aliases.get(152));
         m_file.setResourceFieldAlias(ResourceField.DATE9, aliases.get(153));
         m_file.setResourceFieldAlias(ResourceField.DATE10, aliases.get(154));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE1, aliases.get(155));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE2, aliases.get(156));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE3, aliases.get(157));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE4, aliases.get(158));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE5, aliases.get(159));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE6, aliases.get(160));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE7, aliases.get(161));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE8, aliases.get(162));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE9, aliases.get(163));
         m_file.setResourceFieldAlias(ResourceField.OUTLINE_CODE10, aliases.get(164));
         m_file.setResourceFieldAlias(ResourceField.FLAG10, aliases.get(165));
         m_file.setResourceFieldAlias(ResourceField.FLAG1, aliases.get(166));
         m_file.setResourceFieldAlias(ResourceField.FLAG2, aliases.get(167));
         m_file.setResourceFieldAlias(ResourceField.FLAG3, aliases.get(168));
         m_file.setResourceFieldAlias(ResourceField.FLAG4, aliases.get(169));
         m_file.setResourceFieldAlias(ResourceField.FLAG5, aliases.get(170));
         m_file.setResourceFieldAlias(ResourceField.FLAG6, aliases.get(171));
         m_file.setResourceFieldAlias(ResourceField.FLAG7, aliases.get(172));
         m_file.setResourceFieldAlias(ResourceField.FLAG8, aliases.get(173));
         m_file.setResourceFieldAlias(ResourceField.FLAG9, aliases.get(174));
         m_file.setResourceFieldAlias(ResourceField.FLAG11, aliases.get(175));
         m_file.setResourceFieldAlias(ResourceField.FLAG12, aliases.get(176));
         m_file.setResourceFieldAlias(ResourceField.FLAG13, aliases.get(177));
         m_file.setResourceFieldAlias(ResourceField.FLAG14, aliases.get(178));
         m_file.setResourceFieldAlias(ResourceField.FLAG15, aliases.get(179));
         m_file.setResourceFieldAlias(ResourceField.FLAG16, aliases.get(180));
         m_file.setResourceFieldAlias(ResourceField.FLAG17, aliases.get(181));
         m_file.setResourceFieldAlias(ResourceField.FLAG18, aliases.get(182));
         m_file.setResourceFieldAlias(ResourceField.FLAG19, aliases.get(183));
         m_file.setResourceFieldAlias(ResourceField.FLAG20, aliases.get(184));
         m_file.setResourceFieldAlias(ResourceField.COST1, aliases.get(207));
         m_file.setResourceFieldAlias(ResourceField.COST2, aliases.get(208));
         m_file.setResourceFieldAlias(ResourceField.COST3, aliases.get(209));
         m_file.setResourceFieldAlias(ResourceField.COST4, aliases.get(210));
         m_file.setResourceFieldAlias(ResourceField.COST5, aliases.get(211));
         m_file.setResourceFieldAlias(ResourceField.COST6, aliases.get(212));
         m_file.setResourceFieldAlias(ResourceField.COST7, aliases.get(213));
         m_file.setResourceFieldAlias(ResourceField.COST8, aliases.get(214));
         m_file.setResourceFieldAlias(ResourceField.COST9, aliases.get(215));
         m_file.setResourceFieldAlias(ResourceField.COST10, aliases.get(216));
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
      int itemCount = taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;
      Integer key;
      m_highestEmptyTaskID = -1;
      int emptyTaskID = -1;

      // First three items are not tasks, so let's skip them
      for (int loop = 3; loop < itemCount; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);
         if (data != null)
         {
            byte[] metaData = taskFixedMeta.getByteArrayValue(loop);
            int metaDataItemSize = MPPUtility.getInt(metaData, 0);

            if (metaDataItemSize == 2 || metaDataItemSize == 6)
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
               if (metaDataItemSize == 4)
               {
                  // Empty task - task with only id and unique id information. Empty rows within Project (except for the id's)
                  emptyTaskID = MPPUtility.getInt(data, 4);
                  if (m_highestEmptyTaskID < emptyTaskID)
                  {
                     m_highestEmptyTaskID = emptyTaskID;
                  }
               }
               else
               {
                  if (data.length == 8 || data.length > fieldMap.getMaxFixedDataOffset(0))
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
         resourceMap.put(uniqueID, Integer.valueOf(loop));
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
      VarMeta calVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) calDir.getEntry("VarMeta"))));
      Var2Data calVarData = new Var2Data(calVarMeta, new DocumentInputStream(((DocumentEntry) calDir.getEntry("Var2Data"))));
      FixedMeta calFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("FixedMeta"))), 10);
      FixedData calFixedData = new FixedData(calFixedMeta, getEncryptableInputStream(calDir, "FixedData"), 12);

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

               // Ignore invalid and duplicate IDs.
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
      int periodCount;
      int periodIndex;
      int index;
      int defaultFlag;
      Date start;
      long duration;
      Day day;

      for (index = 0; index < 7; index++)
      {
         offset = 4 + (60 * index);
         defaultFlag = MPPUtility.getShort(data, offset);
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
            periodCount = MPPUtility.getShort(data, offset + 2);
            if (periodCount == 0)
            {
               cal.setWorkingDay(day, false);
            }
            else
            {
               cal.setWorkingDay(day, true);
               hours = cal.addCalendarHours(Day.getInstance(index + 1));

               for (periodIndex = 0; periodIndex < periodCount; periodIndex++)
               {
                  start = MPPUtility.getTime(data, offset + 8 + (periodIndex * 2));
                  duration = MPPUtility.getDuration(data, offset + 20 + (periodIndex * 4));
                  hours.addRange(new DateRange(start, new Date(start.getTime() + duration)));
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
      int exceptionCount = MPPUtility.getShort(data, 0);

      if (exceptionCount != 0)
      {
         int index;
         int offset;
         ProjectCalendarException exception;
         long duration;
         int periodCount;
         Date start;

         for (index = 0; index < exceptionCount; index++)
         {
            offset = 4 + (60 * 7) + (index * 64);

            Date fromDate = MPPUtility.getDate(data, offset);
            Date toDate = MPPUtility.getDate(data, offset + 2);
            exception = cal.addCalendarException(fromDate, toDate);

            periodCount = MPPUtility.getShort(data, offset + 6);
            if (periodCount != 0)
            {
               for (int exceptionPeriodIndex = 0; exceptionPeriodIndex < periodCount; exceptionPeriodIndex++)
               {
                  start = MPPUtility.getTime(data, offset + 12 + (exceptionPeriodIndex * 2));
                  duration = MPPUtility.getDuration(data, offset + 24 + (exceptionPeriodIndex * 4));
                  exception.addRange(new DateRange(start, new Date(start.getTime() + duration)));
               }
            }
         }
      }
   }

   /**
    * The way calendars are stored in an MPP9 file means that there
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
    * @throws IOException
    */
   private void processTaskData() throws IOException
   {
      FieldMap fieldMap = new FieldMap9(m_file);
      fieldMap.createTaskFieldMap(m_projectProps);

      DirectoryEntry taskDir = (DirectoryEntry) m_projectDir.getEntry("TBkndTask");
      VarMeta taskVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data(taskVarMeta, new DocumentInputStream(((DocumentEntry) taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData(taskFixedMeta, getEncryptableInputStream(taskDir, "FixedData"), 768, fieldMap.getMaxFixedDataOffset(0));
      //System.out.println(taskFixedData);
      //System.out.println(taskFixedMeta);
      //System.out.println(taskVarMeta);
      //System.out.println(taskVarData);

      TreeMap<Integer, Integer> taskMap = createTaskMap(fieldMap, taskFixedMeta, taskFixedData);
      // The var data may not contain all the tasks as tasks with no var data assigned will
      // not be saved in there. Most notably these are tasks with no name. So use the task map
      // which contains all the tasks.
      Object[] uniqueid = taskMap.keySet().toArray(); //taskVarMeta.getUniqueIdentifierArray();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
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
         if (data.length == 8)
         {
            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(id);
            task.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));
            //System.out.println(task);
            continue;
         }

         if (data.length < fieldMap.getMaxFixedDataOffset(0))
         {
            continue;
         }

         if (id.intValue() != 0 && !taskVarMeta.containsKey(id))
         {
            continue;
         }

         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
         //System.out.println (MPPUtility.hexdump(data, false, 16, ""));
         //System.out.println (MPPUtility.hexdump(metaData, 8, 4, false));
         //MPPUtility.dataDump(data, true, true, true, true, true, true, true);
         //MPPUtility.dataDump(metaData, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(taskVarData, id, true, true, true, true, true, true);
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
            data
         }, taskVarData);
         task.enableEvents();

         task.setEffortDriven((metaData[11] & 0x10) != 0);

         task.setEstimated(getDurationEstimated(MPPUtility.getShort(data, fieldMap.getFixedDataOffset(TaskField.ACTUAL_DURATION_UNITS))));

         task.setExpanded(((metaData[12] & 0x02) == 0));
         Integer externalTaskID = task.getSubprojectTaskID();
         if (externalTaskID != null && externalTaskID.intValue() != 0)
         {
            task.setSubprojectTaskID(externalTaskID);
            task.setExternalTask(true);
            externalTasks.add(task);
         }

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
         task.setHideBar((metaData[10] & 0x80) != 0);
         processHyperlinkData(task, taskVarData.getByteArray(id, fieldMap.getVarDataKey(TaskField.HYPERLINK_DATA)));

         task.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));
         task.setIgnoreResourceCalendar(((metaData[10] & 0x02) != 0));
         task.setLevelAssignments((metaData[13] & 0x04) != 0);
         task.setLevelingCanSplit((metaData[13] & 0x02) != 0);
         task.setMarked((metaData[9] & 0x40) != 0);
         task.setMilestone((metaData[8] & 0x20) != 0);

         task.setOutlineCode1(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE1_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode2(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE2_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode3(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE3_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode4(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE4_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode5(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE5_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode6(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE6_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode7(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE7_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode8(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE8_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode9(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE9_INDEX), OUTLINECODE_DATA));
         task.setOutlineCode10(m_outlineCodeVarData.getUnicodeString((Integer) task.getCachedValue(TaskField.OUTLINE_CODE10_INDEX), OUTLINECODE_DATA));

         task.setRollup((metaData[10] & 0x08) != 0);
         task.setUniqueID(id);

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
         //notes = taskVarData.getString(id, TASK_NOTES);
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
         // Unfortunately it looks like 'null' tasks sometimes make it through, 
         // so let's check for to see if we need to mark this task as a null 
         // task after all.
         //
         if (task.getName() == null && ((task.getStart() == null || task.getStart().getTime() == MPPUtility.getEpochDate().getTime()) || (task.getFinish() == null || task.getFinish().getTime() == MPPUtility.getEpochDate().getTime()) || (task.getCreateDate() == null || task.getCreateDate().getTime() == MPPUtility.getEpochDate().getTime())))
         {
            m_file.removeTask(task);
            task = m_file.addTask();
            task.setNull(true);
            task.setUniqueID(id);
            task.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));
            continue;
         }

         //
         // Process any enterprise columns
         //
         processTaskEnterpriseColumns(fieldMap, task, taskVarData);

         //
         // Fire the task read event
         //
         m_file.fireTaskReadEvent(task);
         //System.out.println(task);
         //dumpUnknownData (task.getName(), UNKNOWN_TASK_DATA, data);         
      }

      //
      // Enable auto WBS if necessary
      //
      m_file.setAutoWBS(autoWBS);

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

      if (data != null)
      {
         PropsBlock props = new PropsBlock(data);
         //System.out.println(props);

         for (Integer key : props.keySet())
         {
            int keyValue = key.intValue() - MPPTaskField.TASK_FIELD_BASE;
            TaskField field = MPPTaskField.getInstance(keyValue);

            if (field != null)
            {
               Object value = null;

               switch (field.getDataType())
               {
                  case CURRENCY :
                  {
                     value = Double.valueOf(props.getDouble(key) / 100);
                     break;
                  }

                  case DATE :
                  {
                     value = props.getTimestamp(key);
                     break;
                  }

                  case WORK :
                  {
                     double durationValueInHours = MPPUtility.getDouble(props.getByteArray(key)) / 60000;
                     value = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
                     break;
                  }

                  case DURATION :
                  {
                     byte[] durationData = props.getByteArray(key);
                     double durationValueInHours = ((double) MPPUtility.getInt(durationData, 0)) / 600;
                     TimeUnit durationUnits;
                     if (durationData.length < 6)
                     {
                        durationUnits = TimeUnit.DAYS;
                     }
                     else
                     {
                        durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(durationData, 4));
                     }
                     Duration duration = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
                     value = duration.convertUnits(durationUnits, m_file.getProjectHeader());
                     break;
                  }

                  case BOOLEAN :
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

                  case NUMERIC :
                  {
                     value = Double.valueOf(props.getDouble(key));
                     break;
                  }

                  case STRING :
                  {
                     value = props.getUnicodeString(key);
                     break;
                  }

                  case PERCENTAGE :
                  {
                     value = Integer.valueOf(props.getShort(key));
                     break;
                  }

                  default :
                  {
                     break;
                  }
               }

               task.set(field, value);
            }
         }
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

      if (data != null)
      {
         PropsBlock props = new PropsBlock(data);
         //System.out.println(props);
         resource.setCreationDate(props.getTimestamp(Props.RESOURCE_CREATION_DATE));

         for (Integer key : props.keySet())
         {
            int keyValue = key.intValue() - MPPResourceField.RESOURCE_FIELD_BASE;
            //System.out.println("Key=" + keyValue);

            ResourceField field = MPPResourceField.getInstance(keyValue);

            if (field != null)
            {
               Object value = null;

               switch (field.getDataType())
               {
                  case CURRENCY :
                  {
                     value = Double.valueOf(props.getDouble(key) / 100);
                     break;
                  }

                  case DATE :
                  {
                     value = props.getTimestamp(key);
                     break;
                  }

                  case DURATION :
                  {
                     byte[] durationData = props.getByteArray(key);
                     double durationValueInHours = ((double) MPPUtility.getInt(durationData, 0)) / 600;
                     TimeUnit durationUnits;
                     if (durationData.length < 6)
                     {
                        durationUnits = TimeUnit.DAYS;
                     }
                     else
                     {
                        durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(durationData, 4));
                     }
                     Duration duration = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
                     value = duration.convertUnits(durationUnits, m_file.getProjectHeader());
                     break;

                  }

                  case BOOLEAN :
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
                     break;
                  }

                  case NUMERIC :
                  {
                     value = Double.valueOf(props.getDouble(key));
                     break;
                  }

                  case STRING :
                  {
                     value = props.getUnicodeString(key);
                     break;
                  }

                  default :
                  {
                     break;
                  }
               }

               resource.set(field, value);
            }
         }
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
    * @throws IOException
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
    * @throws IOException
    */
   private void processResourceData() throws IOException
   {
      FieldMap fieldMap = new FieldMap9(m_file);
      fieldMap.createResourceFieldMap(m_projectProps);

      DirectoryEntry rscDir = (DirectoryEntry) m_projectDir.getEntry("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data(rscVarMeta, new DocumentInputStream(((DocumentEntry) rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData(rscFixedMeta, getEncryptableInputStream(rscDir, "FixedData"));
      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      //System.out.println(rscFixedMeta);
      //System.out.println(rscFixedData);

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

         //MPPUtility.dataDump(data, true, true, true, true, true, true, true);
         //MPPUtility.varDataDump(rscVarData, id, true, true, true, true, true, true);

         resource = m_file.addResource();

         resource.disableEvents();
         fieldMap.populateContainer(resource, id, new byte[][]
         {
            data
         }, rscVarData);
         resource.enableEvents();

         processHyperlinkData(resource, rscVarData.getByteArray(id, fieldMap.getVarDataKey(ResourceField.HYPERLINK_DATA)));
         resource.setID(Integer.valueOf(MPPUtility.getInt(data, 4)));

         resource.setOutlineCode1(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE1_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode2(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE2_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode3(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE3_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode4(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE4_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode5(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE5_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode6(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE6_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode7(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE7_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode8(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE8_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode9(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE9_INDEX), OUTLINECODE_DATA));
         resource.setOutlineCode10(m_outlineCodeVarData.getUnicodeString((Integer) resource.getCachedValue(ResourceField.OUTLINE_CODE10_INDEX), OUTLINECODE_DATA));

         resource.setType((MPPUtility.getShort(data, fieldMap.getFixedDataOffset(ResourceField.WORKGROUP)) == 0 ? ResourceType.WORK : ResourceType.MATERIAL));
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
         processResourceEnterpriseColumns(fieldMap, resource, rscVarData);

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
      FieldMap fieldMap = new FieldMap9(m_file);
      fieldMap.createAssignmentFieldMap(m_projectProps);

      DirectoryEntry assnDir = (DirectoryEntry) m_projectDir.getEntry("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data(assnVarMeta, new DocumentInputStream(((DocumentEntry) assnDir.getEntry("Var2Data"))));

      FixedMeta assnFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData(142, getEncryptableInputStream(assnDir, "FixedData"));
      if (assnFixedData.getItemCount() != assnFixedMeta.getItemCount())
      {
         assnFixedData = new FixedData(assnFixedMeta, getEncryptableInputStream(assnDir, "FixedData"));
      }

      ResourceAssignmentFactoryCommon factory = new ResourceAssignmentFactoryCommon();
      factory.process(m_file, fieldMap, m_reader.getUseRawTimephasedData(), m_reader.getPreserveNoteFormatting(), assnVarMeta, assnVarData, assnFixedMeta, assnFixedData, null);
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
    * @throws IOException
    */
   private void processViewData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CV_iew");
      VarMeta viewVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data viewVarData = new Var2Data(viewVarMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
      FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 10);
      FixedData fixedData = new FixedData(122, getEncryptableInputStream(dir, "FixedData"));

      int items = fixedMeta.getItemCount();
      View view;
      ViewFactory factory = new ViewFactory9();

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
               //System.out.print(view);
            }
            lastOffset = offset;
         }
      }
   }

   /**
    * This method extracts table data from the MPP file.
    * 
    * @todo This implementation does not deal with MPP9 files saved by later 
    * versions of MS Project
    * 
    * @throws IOException
    */
   private void processTableData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CTable");
      //FixedMeta fixedMeta = new FixedMeta(getEncryptableInputStream(dir, "FixedMeta"), 9);
      FixedData fixedData = new FixedData(110, getEncryptableInputStream(dir, "FixedData"));
      VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

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
    * @todo Doesn't work correctly with MPP9 files saved by Propject 2007 and 2010
    * @throws IOException
    */
   private void processFilterData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CFilter");
      //FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 9);
      //FixedData fixedData = new FixedData(fixedMeta, getEncryptableInputStream(dir, "FixedData"));
      FixedData fixedData = new FixedData(110, getEncryptableInputStream(dir, "FixedData"), true);
      VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

      //System.out.println(fixedMeta);
      //System.out.println(fixedData);
      //System.out.println(varMeta);
      //System.out.println(varData);

      FilterReader reader = new FilterReader9();
      reader.process(m_file, fixedData, varData);
   }

   /**
    * Read group definitions.
    * 
    * @todo Doesn't work correctly with MPP9 files saved by Propject 2007 and 2010 
    * @throws IOException
    */
   private void processGroupData() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CGrouping");
      //FixedMeta fixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) dir.getEntry("FixedMeta"))), 9);      
      FixedData fixedData = new FixedData(110, getEncryptableInputStream(dir, "FixedData"));
      VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));

      //      System.out.println(fixedMeta);
      //      System.out.println(fixedData);
      //      System.out.println(varMeta);
      //      System.out.println(varData);  

      GroupReader reader = new GroupReader9();
      reader.process(m_file, fixedData, varData, m_fontBases);
   }

   /**
    * Read saved view state from an MPP file.
    * 
    * @throws IOException
    */
   private void processSavedViewState() throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry) m_viewDir.getEntry("CEdl");
      VarMeta varMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data(varMeta, new DocumentInputStream(((DocumentEntry) dir.getEntry("Var2Data"))));
      //System.out.println(varMeta);
      //System.out.println(varData);

      InputStream is = getEncryptableInputStream(dir, "FixedData");
      byte[] fixedData = new byte[is.available()];
      is.read(fixedData);
      //System.out.println(MPPUtility.hexdump(fixedData, false, 16, ""));

      ViewStateReader reader = new ViewStateReader9();
      reader.process(m_file, varData, fixedData);
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

   /**
    * This method is called to try to catch any invalid tasks that may have sneaked past all our other checks.
    * This is done by validating the tasks by task ID.
    */
   private void validateTaskIDs()
   {
      List<Task> allTasks = m_file.getAllTasks();
      if (allTasks.size() > 1)
      {
         Collections.sort(allTasks);

         int taskID = -1;
         int lastTaskID = -1;

         for (int i = 0; i < allTasks.size(); i++)
         {
            Task task = allTasks.get(i);

            if (task.getNull())
            {
               continue; // ignore tasks already marked as invalid
            }

            taskID = NumberUtility.getInt(task.getID());
            // In Project the tasks IDs are always contiguous so we can spot invalid tasks by making sure all
            // IDs are represented.
            if (lastTaskID != -1 && taskID > lastTaskID + 1 && taskID > m_highestEmptyTaskID + 1)
            {
               // This task looks to be invalid.
               task.setNull(true);
            }
            else
            {
               lastTaskID = taskID;
            }
         }
      }
   }

   //   private static void dumpUnknownData (String name, int[][] spec, byte[] data)
   //   {
   //      System.out.println (name);
   //      for (int loop=0; loop < spec.length; loop++)
   //      {
   //         System.out.println (spec[loop][0] + ": "+ MPPUtility.hexdump(data, spec[loop][0], spec[loop][1], false));
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
   private DirectoryEntry m_root;
   private HashMap<Integer, ProjectCalendar> m_resourceMap;
   private Var2Data m_outlineCodeVarData;
   private Props9 m_projectProps;
   private Map<Integer, FontBase> m_fontBases;
   private Map<Integer, SubProject> m_taskSubProjects;
   private DirectoryEntry m_projectDir;
   private DirectoryEntry m_viewDir;
   private int m_highestEmptyTaskID;

   // Signals the end of the list of subproject task unique ids
   private static final int SUBPROJECT_LISTEND = 0x00000303;

   // Signals that the previous value was for the subproject task unique id
   // TODO: figure out why the different value exist.
   private static final int SUBPROJECT_TASKUNIQUEID0 = 0x00000000;
   private static final int SUBPROJECT_TASKUNIQUEID1 = 0x0B340000;
   private static final int SUBPROJECT_TASKUNIQUEID2 = 0x0ABB0000;
   private static final int SUBPROJECT_TASKUNIQUEID3 = 0x05A10000;
   private static final int SUBPROJECT_TASKUNIQUEID4 = 0x02F70000;
   private static final int SUBPROJECT_TASKUNIQUEID5 = 0x07010000;
   /**
    * Calendar data types.
    */
   private static final Integer CALENDAR_NAME = Integer.valueOf(1);
   private static final Integer CALENDAR_DATA = Integer.valueOf(3);

   private static final Integer TABLE_COLUMN_DATA_STANDARD = Integer.valueOf(1);
   private static final Integer TABLE_COLUMN_DATA_ENTERPRISE = Integer.valueOf(2);
   private static final Integer TABLE_COLUMN_DATA_BASELINE = null;
   private static final Integer OUTLINECODE_DATA = Integer.valueOf(1);

   /**
    * Mask used to isolate confirmed flag from the duration units field.
    */
   private static final int DURATION_CONFIRMED_MASK = 0x20;

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
