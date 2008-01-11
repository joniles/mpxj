/*
 * file:       MPP9Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2006
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

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Table;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.View;
import net.sf.mpxj.WorkContour;
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
   public void process (MPPReader reader, ProjectFile file, DirectoryEntry root)
      throws MPXJException, IOException
   {
      try
      {
         //
         // Retrieve the high level document properties
         //
         Props9 props9 = new Props9 (new DocumentInputStream (((DocumentEntry)root.getEntry("Props9"))));
         //System.out.println(props9);
   
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
            m_passwordProtected = true;
            throw new MPXJException (MPXJException.PASSWORD_PROTECTED);
         }
   
         m_reader = reader;
         m_file = file;
         m_root = root;
         m_resourceMap = new HashMap<Integer, ProjectCalendar> ();
         m_projectDir = (DirectoryEntry)root.getEntry ("   19");
         m_viewDir = (DirectoryEntry)root.getEntry ("   29");
         DirectoryEntry outlineCodeDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndOutlCode");
         VarMeta outlineCodeVarMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("VarMeta"))));
         m_outlineCodeVarData = new Var2Data (outlineCodeVarMeta, new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("Var2Data"))));
         m_fontBases = new HashMap<Integer, FontBase>();
         m_taskSubProjects = new HashMap<Integer, SubProject> ();
         
         m_file.setMppFileType(9);
         m_file.setAutoFilter(props9.getBoolean(Props.AUTO_FILTER));
         
         processPropertyData ();
         processCalendarData ();
         processResourceData ();
         processTaskData ();
         processConstraintData ();
         processAssignmentData ();
         
         processViewPropertyData();
         processTableData ();
         processViewData ();
         processFilterData();
         processGroupData();
         processSavedViewState();
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
   private void processPropertyData ()
      throws IOException, MPXJException
   {
      Props9 props = new Props9 (getEncryptableInputStream(m_projectDir, "Props"));
      //MPPUtility.fileDump("c:\\temp\\props.txt", props.toString().getBytes());
      
      //
      // Process the project header
      //
      ProjectHeaderReader projectHeaderReader = new ProjectHeaderReader();
      projectHeaderReader.process(m_file, props, m_root);

      //
      // Process aliases
      //
      processTaskFieldNameAliases(props.getByteArray(Props.TASK_FIELD_NAME_ALIASES));
      processResourceFieldNameAliases(props.getByteArray(Props.RESOURCE_FIELD_NAME_ALIASES));

      //
      // Process subproject data
      //
      processSubProjectData(props);
      
      //
      // Process graphical indicators
      //
      GraphicalIndicatorReader reader = new GraphicalIndicatorReader();
      reader.process(m_file, props);
   }

   /**
    * Read sub project data from the file, and add it to a hash map
    * indexed by task ID.
    *
    * @param props file properties
    */
   private void processSubProjectData (Props9 props)
   {
      byte[] subProjData = props.getByteArray(Props.SUBPROJECT_DATA);

      //System.out.println (MPPUtility.hexdump(subProjData, true, 16, ""));
      //MPPUtility.fileHexDump("c:\\temp\\dump.txt", subProjData);

      if (subProjData != null)
      {
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
               // Project name or file name strings, repeated twice
               //
               case 0x00:
               {
                  offset += 8;
                  break;
               }

               //
               // task unique ID, 8 bytes, path, file name
               //
               case (byte)0x99:               
               case 0x09:
               case 0x0D:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // sometimes offset of a task ID?
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }

               //
               // task unique ID, 8 bytes, path, file name
               //
               case (byte)0x91:              
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // Unknown offset
                  offset += 4;
                  
                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }

               case 0x11:
               case 0x03:                  
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;
                  
                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }

               //
               // task unique ID, path, unknown, file name
               //
               case (byte)0x81:
               case 0x41:   
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // unknown offset to 2 bytes of data?
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }

               //
               // task unique ID, path, file name
               //
               case 0x01:
               case 0x08:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }

               //
               // task unique ID, path, file name
               //
               case (byte)0xC0:
               {
                  uniqueIDOffset = itemHeaderOffset;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  // unknown offset
                  offset += 4;
                  
                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);
                  m_taskSubProjects.put(sp.getTaskUniqueID(), sp);
                  break;
               }
               
               //
               // resource unique ID, path, file name
               //               
               case 0x05:
               {
                  uniqueIDOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, uniqueIDOffset, filePathOffset, fileNameOffset);
                  m_file.setResourceSubProject(sp);
                  break;
               }

               //
               // path, file name
               //
               case 0x02:
               case 0x04:
               {
                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset);
                  m_file.setResourceSubProject(sp);
                  break;
               }

               //
               // Appears when a subproject is collapsed
               //
               case (byte)0x80:
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
               case (byte)0x44:                                                                        
               {
                  filePathOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;

                  offset += 4;
                  
                  fileNameOffset = MPPUtility.getShort(subProjData, offset);
                  offset += 4;
                  
                  sp = readSubProject(subProjData, -1, filePathOffset, fileNameOffset);
                  m_file.setResourceSubProject(sp);
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
    * Method used to read the sub project details from a byte array.
    *
    * @param data byte array
    * @param uniqueIDOffset offset of unique ID
    * @param filePathOffset offset of file path
    * @param fileNameOffset offset of file name
    * @return new SubProject instance
    */
   private SubProject readSubProject (byte[] data, int uniqueIDOffset, int filePathOffset, int fileNameOffset)
   {
      SubProject sp = new SubProject ();

      if (uniqueIDOffset != -1)
      {
         int value = MPPUtility.getInt(data, uniqueIDOffset);
         sp.setTaskUniqueID(new Integer(value));
         
         if (value < 1000)
         {
            value = 0x01000000 + ((value-1) * 0x00400000);
         }
         sp.setUniqueIDOffset(new Integer(value));        
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
      filePathOffset += (sp.getDosFullPath().length()+1);

      //
      // 24 byte block
      //
      filePathOffset += 24;

      //
      // 4 byte block size
      //
      int size = MPPUtility.getInt(data, filePathOffset);
      filePathOffset +=4;
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
         filePathOffset += size;
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
      fileNameOffset += (sp.getDosFileName().length()+1);

      //
      // 24 byte block
      //
      fileNameOffset += 24;

      //
      // 4 byte block size
      //
      size = MPPUtility.getInt(data, fileNameOffset);
      fileNameOffset +=4;

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
         fileNameOffset += size;
      }

      //System.out.println(sp.toString());

      return (sp);
   }

   /**
    * This method process the data held in the props file specific to the
    * visual appearance of the project data.
    */
   private void processViewPropertyData ()
      throws IOException
   {
      Props9 props = new Props9 (getEncryptableInputStream(m_viewDir, "Props"));
      //System.out.println(props);
      byte[] data = props.getByteArray(Props.FONT_BASES);
      if (data != null)
      {
         processBaseFonts (data);
      }
   }

   /**
    * Create an index of base font numbers and their associated base
    * font instances.
    * @param data property data
    */
   private void processBaseFonts (byte[] data)
   {
      int offset = 0;

      int blockCount = MPPUtility.getShort(data, 0);
      offset +=2;

      int size;
      String name;

      for (int loop=0; loop < blockCount; loop++)
      {
         /*unknownAttribute = MPPUtility.getShort(data, offset);*/
         offset += 2;

         size = MPPUtility.getShort(data, offset);
         offset += 2;

         name = MPPUtility.getUnicodeString(data, offset);
         offset += 64;

         if (name.length() != 0)
         {
            FontBase fontBase = new FontBase(new Integer(loop), name, size);
            m_fontBases.put(fontBase.getIndex(), fontBase);
         }
      }
   }

   /**
    * Retrieve any task field aliases defined in the MPP file.
    *
    * @param data task field name alias data
    */
   private void processTaskFieldNameAliases (byte[] data)
   {
      if (data != null)
      {
         int offset = 0;
         ArrayList<String> aliases = new ArrayList<String>(300);

         while (offset < data.length)
         {
            String alias = MPPUtility.getUnicodeString(data, offset);
            aliases.add(alias);
            offset += (alias.length()+1)*2;
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
         m_file.setTaskFieldAlias(TaskField.TEXT10, aliases.get(127 ));
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
    * Retrieve any resource field aliases defined in the MPP file.
    *
    * @param data resource field name alias data
    */
   private void processResourceFieldNameAliases (byte[] data)
   {
      if (data != null)
      {
         int offset = 0;
         ArrayList<String> aliases = new ArrayList<String>(250);

         while (offset < data.length)
         {
            String alias = MPPUtility.getUnicodeString(data, offset);
            aliases.add(alias);
            offset += (alias.length()+1)*2;
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
    * @param taskFixedMeta Fixed meta data for this task
    * @param taskFixedData Fixed data for this task
    * @return Mapping between task identifiers and block position
    */
   private TreeMap<Integer, Integer> createTaskMap (FixedMeta taskFixedMeta, FixedData taskFixedData)
   {
      TreeMap<Integer, Integer> taskMap = new TreeMap<Integer, Integer> ();
      int itemCount = taskFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;
      Integer key;
      
      for (int loop=0; loop < itemCount; loop++)
      {
         data = taskFixedData.getByteArrayValue(loop);
         if (data != null && data.length >= MINIMUM_EXPECTED_TASK_SIZE)
         {
            uniqueID = MPPUtility.getInt(data, 0);
            key = new Integer(uniqueID);
            if (taskMap.containsKey(key) == false)
            {
               taskMap.put(key, new Integer (loop));
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
   private TreeMap<Integer, Integer> createResourceMap (FixedMeta rscFixedMeta, FixedData rscFixedData)
   {
      TreeMap<Integer, Integer> resourceMap = new TreeMap<Integer, Integer> ();
      int itemCount = rscFixedMeta.getItemCount();
      byte[] data;
      int uniqueID;

      for (int loop=0; loop < itemCount; loop++)
      {
         data = rscFixedData.getByteArrayValue(loop);
         if (data != null && data.length > 4)
         {
            uniqueID = MPPUtility.getShort (data, 0);
            resourceMap.put(new Integer (uniqueID), new Integer (loop));
         }
      }

      return (resourceMap);
   }

   /**
    * The format of the calendar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    *
    * @throws IOException
    */
   private void processCalendarData ()
      throws MPXJException, IOException
   {
      DirectoryEntry calDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndCal");
      VarMeta calVarMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));
      Var2Data calVarData = new Var2Data (calVarMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("Var2Data"))));
      FixedMeta calFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixedMeta"))), 10);
      FixedData calFixedData = new FixedData (calFixedMeta, getEncryptableInputStream(calDir, "FixedData"));

      HashMap<Integer, ProjectCalendar> calendarMap = new HashMap<Integer, ProjectCalendar> ();
      int items = calFixedData.getItemCount();
      byte[] fixedData;
      byte[] varData;
      Integer calendarID;
      int baseCalendarID;
      Integer resourceID;
      int offset;
      ProjectCalendar cal;
      List<Pair<ProjectCalendar, Integer>> baseCalendars = new LinkedList<Pair<ProjectCalendar, Integer>>();

      for (int loop=0; loop < items; loop++)
      {
         fixedData = calFixedData.getByteArrayValue(loop);
         if (fixedData.length >= 8)
         {
            offset = 0;

            //
            // Bug 890909, here we ensure that we have a complete 12 byte
            // block before attempting to process the data.
            //
            while (offset+12 <= fixedData.length)
            {
               calendarID = new Integer (MPPUtility.getInt (fixedData, offset+0));

               if (calendarMap.containsKey(calendarID) == false)
               {
                  baseCalendarID = MPPUtility.getInt(fixedData, offset+4);
                  varData = calVarData.getByteArray (calendarID, CALENDAR_DATA);

                  if (baseCalendarID == -1)
                  {
                     if (varData != null)
                     {
                        cal = m_file.addBaseCalendar();
                     }
                     else
                     {
                        cal = m_file.addDefaultBaseCalendar();
                     }

                     cal.setName(calVarData.getUnicodeString (calendarID, CALENDAR_NAME));
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

                     baseCalendars.add(new Pair<ProjectCalendar, Integer>(cal, new Integer(baseCalendarID)));
                     resourceID = new Integer (MPPUtility.getInt(fixedData, offset+8));
                     m_resourceMap.put (resourceID, cal);
                  }

                  cal.setUniqueID(calendarID);

                  if (varData != null)
                  {
                     processCalendarHours (varData, cal, baseCalendarID == -1);
                     processCalendarExceptions (varData, cal);
                  }

                  calendarMap.put (calendarID, cal);
               }

               offset += 12;
            }
         }
      }

      updateBaseCalendarNames (baseCalendars, calendarMap);
   }

   /**
    * For a given set of calendar data, this method sets the working
    * day status for each day, and if present, sets the hours for that
    * day.
    *
    * @param data calendar data block
    * @param cal calendar instance
    * @param isBaseCalendar true if this is a base calendar
    * @throws MPXJException
    */
   private void processCalendarHours (byte[] data, ProjectCalendar cal, boolean isBaseCalendar)
      throws MPXJException
   {
      int offset;
      ProjectCalendarHours hours;
      int periodCount;
      int periodIndex;
      int index;
      int defaultFlag;
      Date start;
      long duration;
      Day day;

      //
      // Configure default time ranges
      //
      SimpleDateFormat df = new SimpleDateFormat ("HH:mm");
      Date defaultStart1;
      Date defaultEnd1;
      Date defaultStart2;
      Date defaultEnd2;

      try
      {
         defaultStart1 = df.parse ("08:00");
         defaultEnd1 = df.parse ("12:00");
         defaultStart2 = df.parse ("13:00");
         defaultEnd2 = df.parse ("17:00");
      }

      catch (ParseException ex)
      {
         throw new MPXJException (MPXJException.INVALID_FORMAT, ex);
      }

      for (index=0; index < 7; index++)
      {
         offset = 4 + (60 * index);
         defaultFlag = MPPUtility.getShort (data, offset);
         day = Day.getInstance(index+1);

         if (defaultFlag == 1)
         {
            if (isBaseCalendar == true)
            {
               cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
               if (cal.isWorkingDay(day) == true)
               {
                  hours = cal.addCalendarHours(Day.getInstance(index+1));
                  hours.addDateRange(new DateRange(defaultStart1, defaultEnd1));
                  hours.addDateRange(new DateRange(defaultStart2, defaultEnd2));
               }
            }
            else
            {
               cal.setWorkingDay(day, ProjectCalendar.DEFAULT);
            }
         }
         else
         {
            periodCount = MPPUtility.getShort (data, offset+2);
            if (periodCount == 0)
            {
               cal.setWorkingDay(day, false);
            }
            else
            {
               cal.setWorkingDay(day, true);
               hours = cal.addCalendarHours(Day.getInstance(index+1));

               for (periodIndex=0; periodIndex < periodCount; periodIndex++)
               {
                  start = MPPUtility.getTime (data, offset + 8 + (periodIndex * 2));
                  duration = MPPUtility.getDuration (data, offset + 20 + (periodIndex * 4));
                  hours.addDateRange(new DateRange (start, new Date (start.getTime()+duration)));
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
   private void processCalendarExceptions (byte[] data, ProjectCalendar cal)
   {
      //
      // Handle any exceptions
      //
      int exceptionCount = MPPUtility.getShort (data, 0);

      if (exceptionCount != 0)
      {
         int index;
         int offset;
         ProjectCalendarException exception;
         long duration;
         int periodCount;
         Date start;

         for (index=0; index < exceptionCount; index++)
         {
            offset = 4 + (60 * 7) + (index * 64);
            exception = cal.addCalendarException();
            exception.setFromDate(MPPUtility.getDate (data, offset));
            exception.setToDate(MPPUtility.getDate (data, offset+2));

            periodCount = MPPUtility.getShort (data, offset+6);
            if (periodCount == 0)
            {
               exception.setWorking (false);
            }
            else
            {
               exception.setWorking (true);

               start = MPPUtility.getTime (data, offset+12);
               duration = MPPUtility.getDuration (data, offset+24);
               exception.setFromTime1(start);
               exception.setToTime1(new Date (start.getTime() + duration));

               if (periodCount > 1)
               {
                  start = MPPUtility.getTime (data, offset+14);
                  duration = MPPUtility.getDuration (data, offset+28);
                  exception.setFromTime2(start);
                  exception.setToTime2(new Date (start.getTime() + duration));

                  if (periodCount > 2)
                  {
                     start = MPPUtility.getTime (data, offset+16);
                     duration = MPPUtility.getDuration (data, offset+32);
                     exception.setFromTime3(start);
                     exception.setToTime3(new Date (start.getTime() + duration));
                  }
               }
               //
               // Note that MPP defines 5 time ranges rather than 3
               //
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
   private void updateBaseCalendarNames (List<Pair<ProjectCalendar, Integer>> baseCalendars, HashMap<Integer, ProjectCalendar> map)
   {
      for (Pair<ProjectCalendar, Integer> pair : baseCalendars) 
      {
         ProjectCalendar cal = pair.getFirst();
         Integer baseCalendarID = pair.getSecond();
         ProjectCalendar baseCal = map.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setBaseCalendar(baseCal);
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
   private void processTaskData ()
      throws IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndTask");
      VarMeta taskVarMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data (taskVarMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData (taskFixedMeta, getEncryptableInputStream(taskDir, "FixedData"), 768);
      //System.out.println(taskFixedData);
      //System.out.println(taskVarMeta);
      //System.out.println(taskVarData);

      
      TreeMap<Integer, Integer> taskMap = createTaskMap (taskFixedMeta, taskFixedData);
      Integer[] uniqueid = taskVarMeta.getUniqueIdentifierArray();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Task task;
      boolean autoWBS = true;
      LinkedList<Task> externalTasks = new LinkedList<Task>();
      
      RTFUtility rtf = new RTFUtility ();
      String notes;

      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];

         offset = taskMap.get(id);
         if (taskFixedData.isValidOffset(offset) == false)
         {
            continue;
         }

         data = taskFixedData.getByteArrayValue(offset.intValue());
         if (data.length < MINIMUM_EXPECTED_TASK_SIZE)
         {
            continue;
         }

         metaData = taskFixedMeta.getByteArrayValue(offset.intValue());
         //System.out.println (MPPUtility.hexdump(data, false, 16, ""));
         //System.out.println (MPPUtility.hexdump(metaData, false, 16, ""));

         task = m_file.addTask();
         task.setActualCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 216) / 100));
         task.setActualDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 66), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         task.setActualFinish(MPPUtility.getTimestamp (data, 100));
         task.setActualOvertimeCost (NumberUtility.getDouble(taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_COST)));
         task.setActualOvertimeWork(Duration.getInstance (taskVarData.getDouble(id, TASK_ACTUAL_OVERTIME_WORK)/60000, TimeUnit.HOURS));
         task.setActualStart(MPPUtility.getTimestamp (data, 96));
         task.setActualWork(Duration.getInstance (MPPUtility.getDouble (data, 184)/60000, TimeUnit.HOURS));
         //task.setACWP(); // Calculated value
         //task.setAssignment(); // Calculated value
         //task.setAssignmentDelay(); // Calculated value
         //task.setAssignmentUnits(); // Calculated value
         task.setBaselineCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 232) / 100));
         task.setBaselineDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationTimeUnits (MPPUtility.getShort (data, 78))));
         task.setBaselineFinish(MPPUtility.getTimestamp (data, 108));
         task.setBaselineStart(MPPUtility.getTimestamp (data, 104));
         task.setBaselineWork(Duration.getInstance (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));

// From MS Project 2003
//         task.setBaseline1Cost(NumberUtility.getDouble (MPPUtility.getDouble (data, 232) / 100));
//         task.setBaseline1Duration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationTimeUnits (MPPUtility.getShort (data, 78))));
//         task.setBaseline1Finish(MPPUtility.getTimestamp (data, 108));
//         task.setBaseline1Start(MPPUtility.getTimestamp (data, 104));
//         task.setBaseline1Work(Duration.getInstance (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));
// to...
//         task.setBaseline10Cost(NumberUtility.getDouble (MPPUtility.getDouble (data, 232) / 100));
//         task.setBaseline10Duration(MPPUtility.getDuration (MPPUtility.getInt (data, 74), MPPUtility.getDurationTimeUnits (MPPUtility.getShort (data, 78))));
//         task.setBaseline10Finish(MPPUtility.getTimestamp (data, 108));
//         task.setBaseline10Start(MPPUtility.getTimestamp (data, 104));
//         task.setBaseline10Work(Duration.getInstance (MPPUtility.getDouble (data, 176)/60000, TimeUnit.HOURS));


         //task.setBCWP(); // Calculated value
         //task.setBCWS(); // Calculated value
         //task.setConfirmed(); // Calculated value
         task.setConstraintDate (MPPUtility.getTimestamp (data, 112));
         task.setConstraintType (ConstraintType.getInstance (MPPUtility.getShort (data, 80)));
         task.setContact(taskVarData.getUnicodeString (id, TASK_CONTACT));
         task.setCost(NumberUtility.getDouble (MPPUtility.getDouble(data, 200) / 100));
         //task.setCostRateTable(); // Calculated value
         //task.setCostVariance(); // Populated below
         task.setCost1(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST1) / 100));
         task.setCost2(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST2) / 100));
         task.setCost3(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST3) / 100));
         task.setCost4(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST4) / 100));
         task.setCost5(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST5) / 100));
         task.setCost6(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST6) / 100));
         task.setCost7(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST7) / 100));
         task.setCost8(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST8) / 100));
         task.setCost9(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST9) / 100));
         task.setCost10(NumberUtility.getDouble (taskVarData.getDouble (id, TASK_COST10) / 100));

// From MS Project 2003
//         task.setCPI();

         task.setCreateDate(MPPUtility.getTimestamp (data, 130));
         //task.setCritical(); // Calculated value
         //task.setCV(); // Calculated value
         //task.setCVPercent(); // Calculate value
         task.setDate1(taskVarData.getTimestamp (id, TASK_DATE1));
         task.setDate2(taskVarData.getTimestamp (id, TASK_DATE2));
         task.setDate3(taskVarData.getTimestamp (id, TASK_DATE3));
         task.setDate4(taskVarData.getTimestamp (id, TASK_DATE4));
         task.setDate5(taskVarData.getTimestamp (id, TASK_DATE5));
         task.setDate6(taskVarData.getTimestamp (id, TASK_DATE6));
         task.setDate7(taskVarData.getTimestamp (id, TASK_DATE7));
         task.setDate8(taskVarData.getTimestamp (id, TASK_DATE8));
         task.setDate9(taskVarData.getTimestamp (id, TASK_DATE9));
         task.setDate10(taskVarData.getTimestamp (id, TASK_DATE10));
         task.setDeadline (MPPUtility.getTimestamp (data, 164));
         //task.setDelay(); // No longer supported by MS Project?
         task.setDuration (MPPUtility.getAdjustedDuration (m_file, MPPUtility.getInt (data, 60), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         //task.setDurationVariance(); // Calculated value
         task.setDuration1(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION1), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION1_UNITS))));
         task.setDuration2(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION2), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION2_UNITS))));
         task.setDuration3(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION3), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION3_UNITS))));
         task.setDuration4(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION4), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION4_UNITS))));
         task.setDuration5(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION5), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION5_UNITS))));
         task.setDuration6(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION6), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION6_UNITS))));
         task.setDuration7(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION7), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION7_UNITS))));
         task.setDuration8(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION8), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION8_UNITS))));
         task.setDuration9(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION9), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION9_UNITS))));
         task.setDuration10(MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_DURATION10), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_DURATION10_UNITS))));
//       From MS Project 2003
//         task.setEAC();
         task.setEarlyFinish (MPPUtility.getTimestamp (data, 8));
         task.setEarlyStart (MPPUtility.getTimestamp (data, 88));
//       From MS Project 2003
//         task.setEarnedValueMethod();
         task.setEffortDriven((metaData[11] & 0x10) != 0);
         task.setEstimated(getDurationEstimated(MPPUtility.getShort (data, 64)));
         task.setExpanded(((metaData[12] & 0x02) == 0));
         int externalTaskID = taskVarData.getInt(id, TASK_EXTERNAL_TASK_ID);
         if (externalTaskID != 0)
         {
            task.setExternalTaskID(new Integer(externalTaskID));
            task.setExternalTask(true);
            externalTasks.add(task);
         }         
         task.setFinish (MPPUtility.getTimestamp (data, 8));
//       From MS Project 2003
         //task.setFinishVariance(); // Calculated value
         task.setFinish1(taskVarData.getTimestamp (id, TASK_FINISH1));
         task.setFinish2(taskVarData.getTimestamp (id, TASK_FINISH2));
         task.setFinish3(taskVarData.getTimestamp (id, TASK_FINISH3));
         task.setFinish4(taskVarData.getTimestamp (id, TASK_FINISH4));
         task.setFinish5(taskVarData.getTimestamp (id, TASK_FINISH5));
         task.setFinish6(taskVarData.getTimestamp (id, TASK_FINISH6));
         task.setFinish7(taskVarData.getTimestamp (id, TASK_FINISH7));
         task.setFinish8(taskVarData.getTimestamp (id, TASK_FINISH8));
         task.setFinish9(taskVarData.getTimestamp (id, TASK_FINISH9));
         task.setFinish10(taskVarData.getTimestamp (id, TASK_FINISH10));
         task.setFixedCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 208) / 100));
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
         task.setFreeSlack(MPPUtility.getAdjustedDuration (m_file, MPPUtility.getInt(data, 24), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
//       From MS Project 2003
//         task.setGroupBySummary();
         task.setHideBar((metaData[10] & 0x80) != 0);
         processHyperlinkData (task, taskVarData.getByteArray(id, TASK_HYPERLINK));
         task.setID (new Integer(MPPUtility.getInt (data, 4)));
//       From MS Project 2003
//         task.setIgnoreResourceCalendar();
         //task.setIndicators(); // Calculated value
         task.setLateFinish(MPPUtility.getTimestamp(data, 92));
         task.setLateStart(MPPUtility.getTimestamp(data, 148));
         task.setLevelAssignments((metaData[13] & 0x04) != 0);
         task.setLevelingCanSplit((metaData[13] & 0x02) != 0);
         task.setLevelingDelay (MPPUtility.getDuration (((double)MPPUtility.getInt (data, 82))/3, MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 86))));
         //task.setLinkedFields();  // Calculated value
         task.setMarked((metaData[9] & 0x40) != 0);
         task.setMilestone((metaData[8] & 0x20) != 0);
         task.setName(taskVarData.getUnicodeString (id, TASK_NAME));
         task.setNumber1(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER1)));
         task.setNumber2(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER2)));
         task.setNumber3(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER3)));
         task.setNumber4(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER4)));
         task.setNumber5(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER5)));
         task.setNumber6(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER6)));
         task.setNumber7(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER7)));
         task.setNumber8(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER8)));
         task.setNumber9(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER9)));
         task.setNumber10(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER10)));
         task.setNumber11(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER11)));
         task.setNumber12(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER12)));
         task.setNumber13(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER13)));
         task.setNumber14(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER14)));
         task.setNumber15(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER15)));
         task.setNumber16(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER16)));
         task.setNumber17(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER17)));
         task.setNumber18(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER18)));
         task.setNumber19(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER19)));
         task.setNumber20(NumberUtility.getDouble (taskVarData.getDouble(id, TASK_NUMBER20)));
         //task.setObjects(); // Calculated value
         task.setOutlineCode1(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE1)), OUTLINECODE_DATA));
         task.setOutlineCode2(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE2)), OUTLINECODE_DATA));
         task.setOutlineCode3(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE3)), OUTLINECODE_DATA));
         task.setOutlineCode4(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE4)), OUTLINECODE_DATA));
         task.setOutlineCode5(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE5)), OUTLINECODE_DATA));
         task.setOutlineCode6(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE6)), OUTLINECODE_DATA));
         task.setOutlineCode7(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE7)), OUTLINECODE_DATA));
         task.setOutlineCode8(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE8)), OUTLINECODE_DATA));
         task.setOutlineCode9(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE9)), OUTLINECODE_DATA));
         task.setOutlineCode10(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, TASK_OUTLINECODE10)), OUTLINECODE_DATA));
         task.setOutlineLevel (new Integer(MPPUtility.getShort (data, 40)));
         //task.setOutlineNumber(); // Calculated value
         //task.setOverallocated(); // Calculated value
         task.setOvertimeCost(NumberUtility.getDouble(taskVarData.getDouble(id, TASK_OVERTIME_COST)));
         //task.setOvertimeWork(); // Calculated value?
         //task.getPredecessors(); // Calculated value
         task.setPercentageComplete(NumberUtility.getDouble(MPPUtility.getShort(data, 122)));
         task.setPercentageWorkComplete(NumberUtility.getDouble(MPPUtility.getShort(data, 124)));
//       From MS Project 2003
//         task.setPhysicalPercentComplete();
         task.setPreleveledFinish(MPPUtility.getTimestamp(data, 140));
         task.setPreleveledStart(MPPUtility.getTimestamp(data, 136));
         task.setPriority(Priority.getInstance(MPPUtility.getShort (data, 120)));
         //task.setProject(); // Calculated value
         //task.setRecurring(); // Calculated value
         //task.setRegularWork(); // Calculated value
         task.setRemainingCost(NumberUtility.getDouble (MPPUtility.getDouble (data, 224)/100));
         task.setRemainingDuration(MPPUtility.getDuration (MPPUtility.getInt (data, 70), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         task.setRemainingOvertimeCost(NumberUtility.getDouble(taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_COST)));
         task.setRemainingOvertimeWork(Duration.getInstance (taskVarData.getDouble(id, TASK_REMAINING_OVERTIME_WORK)/60000, TimeUnit.HOURS));
         task.setRemainingWork(Duration.getInstance (MPPUtility.getDouble (data, 192)/60000, TimeUnit.HOURS));
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
         task.setStart (MPPUtility.getTimestamp (data, 88));
//       From MS Project 2003
         task.setStartSlack(MPPUtility.getAdjustedDuration (m_file, MPPUtility.getInt(data, 28), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         //task.setStartVariance(); // Calculated value
         task.setStart1(taskVarData.getTimestamp (id, TASK_START1));
         task.setStart2(taskVarData.getTimestamp (id, TASK_START2));
         task.setStart3(taskVarData.getTimestamp (id, TASK_START3));
         task.setStart4(taskVarData.getTimestamp (id, TASK_START4));
         task.setStart5(taskVarData.getTimestamp (id, TASK_START5));
         task.setStart6(taskVarData.getTimestamp (id, TASK_START6));
         task.setStart7(taskVarData.getTimestamp (id, TASK_START7));
         task.setStart8(taskVarData.getTimestamp (id, TASK_START8));
         task.setStart9(taskVarData.getTimestamp (id, TASK_START9));
         task.setStart10(taskVarData.getTimestamp (id, TASK_START10));
//       From MS Project 2003
//         task.setStatus();
//         task.setStatusIndicator();
         task.setStop(MPPUtility.getTimestamp (data, 16));
         //task.setSubprojectFile();
         //task.setSubprojectReadOnly();
         task.setSubprojectTasksUniqueIDOffset(new Integer (taskVarData.getInt(id, TASK_SUBPROJECT_TASKS_UNIQUEID_OFFSET)));
         task.setSubprojectTaskUniqueID(new Integer (taskVarData.getInt(id, TASK_SUBPROJECTTASKID)));
         //task.setSuccessors(); // Calculated value
         //task.setSummary(); // Automatically generated by MPXJ
         //task.setSV(); // Calculated value
//       From MS Project 2003
//         task.setSVPercent();
//         task.setTCPI();
         //task.setTeamStatusPending(); // Calculated value
         task.setText1(taskVarData.getUnicodeString (id, TASK_TEXT1));
         task.setText2(taskVarData.getUnicodeString (id, TASK_TEXT2));
         task.setText3(taskVarData.getUnicodeString (id, TASK_TEXT3));
         task.setText4(taskVarData.getUnicodeString (id, TASK_TEXT4));
         task.setText5(taskVarData.getUnicodeString (id, TASK_TEXT5));
         task.setText6(taskVarData.getUnicodeString (id, TASK_TEXT6));
         task.setText7(taskVarData.getUnicodeString (id, TASK_TEXT7));
         task.setText8(taskVarData.getUnicodeString (id, TASK_TEXT8));
         task.setText9(taskVarData.getUnicodeString (id, TASK_TEXT9));
         task.setText10(taskVarData.getUnicodeString (id, TASK_TEXT10));
         task.setText11(taskVarData.getUnicodeString (id, TASK_TEXT11));
         task.setText12(taskVarData.getUnicodeString (id, TASK_TEXT12));
         task.setText13(taskVarData.getUnicodeString (id, TASK_TEXT13));
         task.setText14(taskVarData.getUnicodeString (id, TASK_TEXT14));
         task.setText15(taskVarData.getUnicodeString (id, TASK_TEXT15));
         task.setText16(taskVarData.getUnicodeString (id, TASK_TEXT16));
         task.setText17(taskVarData.getUnicodeString (id, TASK_TEXT17));
         task.setText18(taskVarData.getUnicodeString (id, TASK_TEXT18));
         task.setText19(taskVarData.getUnicodeString (id, TASK_TEXT19));
         task.setText20(taskVarData.getUnicodeString (id, TASK_TEXT20));
         task.setText21(taskVarData.getUnicodeString (id, TASK_TEXT21));
         task.setText22(taskVarData.getUnicodeString (id, TASK_TEXT22));
         task.setText23(taskVarData.getUnicodeString (id, TASK_TEXT23));
         task.setText24(taskVarData.getUnicodeString (id, TASK_TEXT24));
         task.setText25(taskVarData.getUnicodeString (id, TASK_TEXT25));
         task.setText26(taskVarData.getUnicodeString (id, TASK_TEXT26));
         task.setText27(taskVarData.getUnicodeString (id, TASK_TEXT27));
         task.setText28(taskVarData.getUnicodeString (id, TASK_TEXT28));
         task.setText29(taskVarData.getUnicodeString (id, TASK_TEXT29));
         task.setText30(taskVarData.getUnicodeString (id, TASK_TEXT30));
         //task.setTotalSlack(); // Calculated value
         task.setType(TaskType.getInstance(MPPUtility.getShort(data, 126)));
         task.setUniqueID(id);
         //task.setUniqueIDPredecessors(); // Calculated value
         //task.setUniqueIDSuccessors(); // Calculated value
         //task.setUpdateNeeded(); // Calculated value
         task.setWBS(taskVarData.getUnicodeString (id, TASK_WBS));
         //task.setWBSPredecessors(); // Calculated value
         //task.setWBSSuccessors(); // Calculated value
         task.setWork(Duration.getInstance (MPPUtility.getDouble (data, 168)/60000, TimeUnit.HOURS));
         //task.setWorkContour(); // Calculated from resource
         //task.setWorkVariance(); // Calculated value

         task.setFinishSlack(MPPUtility.getAdjustedDuration (m_file, MPPUtility.getInt(data, 32), MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 64))));
         
         switch (task.getConstraintType())
         {
            //
            // Adjust the start and finish dates if the task
            // is constrained to start as late as possible.
            //            
            case AS_LATE_AS_POSSIBLE:
            {
               if (task.getStart().getTime() < task.getLateStart().getTime())
               {
                  task.setStart(task.getLateStart());
               }
               if (task.getFinish().getTime() < task.getLateFinish().getTime())
               {
                  task.setFinish(task.getLateFinish());
               }
               break;
            }
            
            case START_NO_LATER_THAN:
            {
               if (task.getFinish().getTime() < task.getStart().getTime())
               {
                  task.setFinish(task.getLateFinish());                  
               }
               break;
            }
            
            case FINISH_NO_LATER_THAN:
            {
               if (task.getFinish().getTime() < task.getStart().getTime())
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
         // Retrieve the task notes.
         //
         notes = taskVarData.getString (id, TASK_NOTES);
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
            ProjectCalendar calendar = m_file.getBaseCalendarByUniqueID(new Integer(calendarID));
            if (calendar != null)
            {
               task.setCalendar(calendar);
            }
         }
                  
         //
         // Set the sub project flag
         //
         task.setSubProject(m_taskSubProjects.get(task.getUniqueID()));

         //
         // If we have a WBS value from the MPP file, don't auto generate
         //
         if (task.getWBS() != null)
         {
            autoWBS = false;
         }
         
         //
         // If this is a split task, allocate space for the split durations
         //
         if ((metaData[9]&0x80) == 0)
         {
            task.setSplits(new LinkedList<Duration>());
         }
         
         //
         // Process any enterprise columns
         //
         processTaskEnterpriseColumns(task, taskVarData);
         
         //
         // Fire the task read event
         //
         m_file.fireTaskReadEvent(task);

         //dumpUnknownData (task.getName(), UNKNOWN_TASK_DATA, data);         
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
         processExternalTasks (externalTasks);
      }
   }

   /**
    * Extracts task enterprise column values. 
    * 
    * @param task task instance
    * @param taskVarData task var data
    */
   private void processTaskEnterpriseColumns (Task task, Var2Data taskVarData)
   {
      byte[] data = taskVarData.getByteArray(task.getUniqueID(), TASK_ENTERPRISE_COLUMNS);
      if (data != null)
      {
         PropsBlock props = new PropsBlock (data);
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
                  case CURRENCY:
                  {
                     value = new Double (props.getDouble(key) / 100);
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
                     double durationValueInHours = ((double)MPPUtility.getInt(durationData, 0)) / 600; 
                     TimeUnit durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getInt(durationData, 4));
                     Duration duration = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
                     value = duration.convertUnits(durationUnits, m_file.getProjectHeader());
                     break;
                  }
                  
                  case BOOLEAN:               
                  {
                     field = null;                  
                     int bits = props.getInt(key);                  
                     task.set(TaskField.ENTERPRISE_FLAG1, new Boolean((bits & 0x00002) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG2, new Boolean((bits & 0x00004) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG3, new Boolean((bits & 0x00008) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG4, new Boolean((bits & 0x00010) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG5, new Boolean((bits & 0x00020) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG6, new Boolean((bits & 0x00040) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG7, new Boolean((bits & 0x00080) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG8, new Boolean((bits & 0x00100) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG9, new Boolean((bits & 0x00200) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG10, new Boolean((bits & 0x00400) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG11, new Boolean((bits & 0x00800) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG12, new Boolean((bits & 0x01000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG13, new Boolean((bits & 0x02000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG14, new Boolean((bits & 0x04000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG15, new Boolean((bits & 0x08000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG16, new Boolean((bits & 0x10000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG17, new Boolean((bits & 0x20000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG18, new Boolean((bits & 0x40000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG19, new Boolean((bits & 0x80000) != 0));
                     task.set(TaskField.ENTERPRISE_FLAG20, new Boolean((bits & 0x100000) != 0));
                     break;
                  }
                  
                  case NUMERIC:
                  {
                     value = new Double(props.getDouble(key));
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
               
               task.set(field, value);
            }
         }
      }
   }

   /**
    * Extracts resource enterprise column data.
    * 
    * @param resource resource instance
    * @param resourceVarData resource var data
    */
   private void processResourceEnterpriseColumns (Resource resource, Var2Data resourceVarData)
   {
      byte[] data = resourceVarData.getByteArray(resource.getUniqueID(), RESOURCE_ENTERPRISE_COLUMNS);
      if (data != null)
      {
         PropsBlock props = new PropsBlock (data);
         //System.out.println(props);
         
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
                  case CURRENCY:
                  {
                     value = new Double (props.getDouble(key) / 100);
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
                     double durationValueInHours = ((double)MPPUtility.getInt(durationData, 0)) / 600; 
                     TimeUnit durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getInt(durationData, 4));
                     Duration duration = Duration.getInstance(durationValueInHours, TimeUnit.HOURS);
                     value = duration.convertUnits(durationUnits, m_file.getProjectHeader());
                     break;
                  }
                  
                  case BOOLEAN:               
                  {
                     field = null;                  
                     int bits = props.getInt(key);                  
                     resource.set(ResourceField.ENTERPRISE_FLAG1, new Boolean((bits & 0x00002) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG2, new Boolean((bits & 0x00004) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG3, new Boolean((bits & 0x00008) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG4, new Boolean((bits & 0x00010) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG5, new Boolean((bits & 0x00020) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG6, new Boolean((bits & 0x00040) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG7, new Boolean((bits & 0x00080) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG8, new Boolean((bits & 0x00100) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG9, new Boolean((bits & 0x00200) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG10, new Boolean((bits & 0x00400) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG11, new Boolean((bits & 0x00800) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG12, new Boolean((bits & 0x01000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG13, new Boolean((bits & 0x02000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG14, new Boolean((bits & 0x04000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG15, new Boolean((bits & 0x08000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG16, new Boolean((bits & 0x10000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG17, new Boolean((bits & 0x20000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG18, new Boolean((bits & 0x40000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG19, new Boolean((bits & 0x80000) != 0));
                     resource.set(ResourceField.ENTERPRISE_FLAG20, new Boolean((bits & 0x100000) != 0));
                     break;
                  }
                  
                  case NUMERIC:
                  {
                     value = new Double(props.getDouble(key));
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
   private void processExternalTasks (List<Task> externalTasks)
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
      
      for(Task currentTask : externalTasks)
      {
         SubProject sp = currentTask.getSubProject();
         if (sp == null)
         {
            currentTask.setSubProject(currentSubProject);
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
   private void processHyperlinkData (Task task, byte[] data)
   {
      if (data != null)
      {
         int offset = 12;
         String hyperlink;
         String address;
         String subaddress;

         offset += 12;
         hyperlink = MPPUtility.getUnicodeString(data, offset);
         offset += ((hyperlink.length()+1) * 2);

         offset += 12;
         address = MPPUtility.getUnicodeString(data, offset);
         offset += ((address.length()+1) * 2);

         offset += 12;
         subaddress = MPPUtility.getUnicodeString(data, offset);

         task.setHyperlink(hyperlink);
         task.setHyperlinkAddress(address);
         task.setHyperlinkSubAddress(subaddress);
      }
   }

   /**
    * This method extracts and collates constraint data.
    *
    * @throws IOException
    */
   private void processConstraintData ()
      throws IOException
   {
      DirectoryEntry consDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndCons");
      FixedMeta consFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedMeta"))), 10);
      FixedData consFixedData = new FixedData (consFixedMeta, 20, getEncryptableInputStream(consDir, "FixedData"));
      
      int count = consFixedMeta.getItemCount();
      int index;
      byte[] data;
      Task task1;
      Task task2;
      Relation rel;
      TimeUnit durationUnits;
      int constraintID;
      int lastConstraintID = -1;
      byte[] metaData;

      for (int loop=0; loop < count; loop++)
      {
         metaData = consFixedMeta.getByteArrayValue(loop);

         if (MPPUtility.getInt(metaData, 0) == 0)
         {
            index = consFixedData.getIndexFromOffset(MPPUtility.getInt(metaData, 4));
            if (index != -1)
            {
               data = consFixedData.getByteArrayValue(index);
               constraintID = MPPUtility.getInt (data, 0);
               if (constraintID > lastConstraintID)
               {
                  lastConstraintID = constraintID;
                  int taskID1 = MPPUtility.getInt (data, 4);
                  int taskID2 = MPPUtility.getInt (data, 8);

                  if (taskID1 != taskID2)
                  {
                     task1 = m_file.getTaskByUniqueID (new Integer(taskID1));
                     task2 = m_file.getTaskByUniqueID (new Integer(taskID2));

                     if (task1 != null && task2 != null)
                     {
                        rel = task2.addPredecessor(task1);
                        rel.setType (RelationType.getInstance(MPPUtility.getShort(data, 12)));
                        durationUnits = MPPUtility.getDurationTimeUnits(MPPUtility.getShort (data, 14));
                        rel.setDuration(MPPUtility.getAdjustedDuration(m_file, MPPUtility.getInt (data, 16), durationUnits));
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
   private void processResourceData ()
      throws IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data (rscVarMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData (rscFixedMeta, getEncryptableInputStream(rscDir, "FixedData"));
      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      
      TreeMap<Integer, Integer> resourceMap = createResourceMap (rscFixedMeta, rscFixedData);
      Integer[] uniqueid = rscVarMeta.getUniqueIdentifierArray();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      Resource resource;

      RTFUtility rtf = new RTFUtility ();
      String notes;
      
      for (int loop=0; loop < uniqueid.length; loop++)
      {
         id = uniqueid[loop];
         offset = resourceMap.get(id);
         if (rscFixedData.isValidOffset(offset) == false)
         {
            continue;
         }

         data = rscFixedData.getByteArrayValue(offset.intValue());
         if (data.length < MINIMUM_EXPECTED_RESOURCE_SIZE)
         {
            continue;
         }
         
         resource = m_file.addResource();

         resource.setAccrueAt(AccrueType.getInstance (MPPUtility.getShort (data, 12)));
         resource.setActualCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 132)/100));
         resource.setActualOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 172)/100));
         resource.setActualWork(Duration.getInstance (MPPUtility.getDouble (data, 60)/60000, TimeUnit.HOURS));
         resource.setAvailableFrom(MPPUtility.getTimestamp(data, 20));
         resource.setAvailableTo(MPPUtility.getTimestamp(data, 24));
         //resource.setBaseCalendar();
         resource.setBaselineCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 148)/100));
         resource.setBaselineWork(Duration.getInstance (MPPUtility.getDouble (data, 68)/60000, TimeUnit.HOURS));
         resource.setCode (rscVarData.getUnicodeString (id, RESOURCE_CODE));
         resource.setCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 140)/100));
         resource.setCost1(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST1) / 100));
         resource.setCost2(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST2) / 100));
         resource.setCost3(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST3) / 100));
         resource.setCost4(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST4) / 100));
         resource.setCost5(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST5) / 100));
         resource.setCost6(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST6) / 100));
         resource.setCost7(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST7) / 100));
         resource.setCost8(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST8) / 100));
         resource.setCost9(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST9) / 100));
         resource.setCost10(NumberUtility.getDouble (rscVarData.getDouble (id, RESOURCE_COST10) / 100));
         resource.setCostPerUse(NumberUtility.getDouble(MPPUtility.getDouble(data, 84)/100));
         resource.setDate1(rscVarData.getTimestamp (id, RESOURCE_DATE1));
         resource.setDate2(rscVarData.getTimestamp (id, RESOURCE_DATE2));
         resource.setDate3(rscVarData.getTimestamp (id, RESOURCE_DATE3));
         resource.setDate4(rscVarData.getTimestamp (id, RESOURCE_DATE4));
         resource.setDate5(rscVarData.getTimestamp (id, RESOURCE_DATE5));
         resource.setDate6(rscVarData.getTimestamp (id, RESOURCE_DATE6));
         resource.setDate7(rscVarData.getTimestamp (id, RESOURCE_DATE7));
         resource.setDate8(rscVarData.getTimestamp (id, RESOURCE_DATE8));
         resource.setDate9(rscVarData.getTimestamp (id, RESOURCE_DATE9));
         resource.setDate10(rscVarData.getTimestamp (id, RESOURCE_DATE10));
         resource.setDuration1(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION1), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION1_UNITS))));
         resource.setDuration2(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION2), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION2_UNITS))));
         resource.setDuration3(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION3), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION3_UNITS))));
         resource.setDuration4(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION4), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION4_UNITS))));
         resource.setDuration5(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION5), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION5_UNITS))));
         resource.setDuration6(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION6), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION6_UNITS))));
         resource.setDuration7(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION7), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION7_UNITS))));
         resource.setDuration8(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION8), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION8_UNITS))));
         resource.setDuration9(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION9), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION9_UNITS))));
         resource.setDuration10(MPPUtility.getDuration (rscVarData.getInt(id, RESOURCE_DURATION10), MPPUtility.getDurationTimeUnits(rscVarData.getShort(id, RESOURCE_DURATION10_UNITS))));
         resource.setEmailAddress(rscVarData.getUnicodeString (id, RESOURCE_EMAIL));
         resource.setFinish1(rscVarData.getTimestamp (id, RESOURCE_FINISH1));
         resource.setFinish2(rscVarData.getTimestamp (id, RESOURCE_FINISH2));
         resource.setFinish3(rscVarData.getTimestamp (id, RESOURCE_FINISH3));
         resource.setFinish4(rscVarData.getTimestamp (id, RESOURCE_FINISH4));
         resource.setFinish5(rscVarData.getTimestamp (id, RESOURCE_FINISH5));
         resource.setFinish6(rscVarData.getTimestamp (id, RESOURCE_FINISH6));
         resource.setFinish7(rscVarData.getTimestamp (id, RESOURCE_FINISH7));
         resource.setFinish8(rscVarData.getTimestamp (id, RESOURCE_FINISH8));
         resource.setFinish9(rscVarData.getTimestamp (id, RESOURCE_FINISH9));
         resource.setFinish10(rscVarData.getTimestamp (id, RESOURCE_FINISH10));
         resource.setGroup(rscVarData.getUnicodeString (id, RESOURCE_GROUP));
         resource.setID (new Integer(MPPUtility.getInt (data, 4)));
         resource.setInitials (rscVarData.getUnicodeString (id, RESOURCE_INITIALS));
         //resource.setLinkedFields(); // Calculated value
         resource.setMaxUnits(NumberUtility.getDouble(MPPUtility.getDouble(data, 44)/100));
         resource.setName (rscVarData.getUnicodeString (id, RESOURCE_NAME));
         resource.setNumber1(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER1)));
         resource.setNumber2(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER2)));
         resource.setNumber3(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER3)));
         resource.setNumber4(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER4)));
         resource.setNumber5(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER5)));
         resource.setNumber6(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER6)));
         resource.setNumber7(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER7)));
         resource.setNumber8(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER8)));
         resource.setNumber9(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER9)));
         resource.setNumber10(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER10)));
         resource.setNumber11(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER11)));
         resource.setNumber12(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER12)));
         resource.setNumber13(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER13)));
         resource.setNumber14(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER14)));
         resource.setNumber15(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER15)));
         resource.setNumber16(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER16)));
         resource.setNumber17(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER17)));
         resource.setNumber18(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER18)));
         resource.setNumber19(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER19)));
         resource.setNumber20(NumberUtility.getDouble (rscVarData.getDouble(id, RESOURCE_NUMBER20)));
         //resource.setObjects(); // Calculated value
         resource.setOutlineCode1(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE1)), OUTLINECODE_DATA));
         resource.setOutlineCode2(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE2)), OUTLINECODE_DATA));
         resource.setOutlineCode3(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE3)), OUTLINECODE_DATA));
         resource.setOutlineCode4(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE4)), OUTLINECODE_DATA));
         resource.setOutlineCode5(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE5)), OUTLINECODE_DATA));
         resource.setOutlineCode6(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE6)), OUTLINECODE_DATA));
         resource.setOutlineCode7(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE7)), OUTLINECODE_DATA));
         resource.setOutlineCode8(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE8)), OUTLINECODE_DATA));
         resource.setOutlineCode9(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE9)), OUTLINECODE_DATA));
         resource.setOutlineCode10(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, RESOURCE_OUTLINECODE10)), OUTLINECODE_DATA));
         //resource.setOverallocated(); // Calculated value
         resource.setOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 164)/100));
         resource.setOvertimeRate(new Rate (MPPUtility.getDouble(data, 36), TimeUnit.HOURS));
         resource.setOvertimeWork(Duration.getInstance (MPPUtility.getDouble (data, 76)/60000, TimeUnit.HOURS));
         resource.setPeakUnits(NumberUtility.getDouble(MPPUtility.getDouble(data, 124)/100));
         //resource.setPercentageWorkComplete(); // Calculated value
         resource.setRegularWork(Duration.getInstance (MPPUtility.getDouble (data, 100)/60000, TimeUnit.HOURS));
         resource.setRemainingCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 156)/100));
         resource.setRemainingOvertimeCost(NumberUtility.getDouble(MPPUtility.getDouble(data, 180)/100));
         resource.setRemainingWork(Duration.getInstance (MPPUtility.getDouble (data, 92)/60000, TimeUnit.HOURS));
         resource.setStandardRate(new Rate (MPPUtility.getDouble(data, 28), TimeUnit.HOURS));
         resource.setStart1(rscVarData.getTimestamp (id, RESOURCE_START1));
         resource.setStart2(rscVarData.getTimestamp (id, RESOURCE_START2));
         resource.setStart3(rscVarData.getTimestamp (id, RESOURCE_START3));
         resource.setStart4(rscVarData.getTimestamp (id, RESOURCE_START4));
         resource.setStart5(rscVarData.getTimestamp (id, RESOURCE_START5));
         resource.setStart6(rscVarData.getTimestamp (id, RESOURCE_START6));
         resource.setStart7(rscVarData.getTimestamp (id, RESOURCE_START7));
         resource.setStart8(rscVarData.getTimestamp (id, RESOURCE_START8));
         resource.setStart9(rscVarData.getTimestamp (id, RESOURCE_START9));
         resource.setStart10(rscVarData.getTimestamp (id, RESOURCE_START10));
         resource.setSubprojectResourceUniqueID(new Integer (rscVarData.getInt(id, RESOURCE_SUBPROJECTRESOURCEID)));
         resource.setText1(rscVarData.getUnicodeString (id, RESOURCE_TEXT1));
         resource.setText2(rscVarData.getUnicodeString (id, RESOURCE_TEXT2));
         resource.setText3(rscVarData.getUnicodeString (id, RESOURCE_TEXT3));
         resource.setText4(rscVarData.getUnicodeString (id, RESOURCE_TEXT4));
         resource.setText5(rscVarData.getUnicodeString (id, RESOURCE_TEXT5));
         resource.setText6(rscVarData.getUnicodeString (id, RESOURCE_TEXT6));
         resource.setText7(rscVarData.getUnicodeString (id, RESOURCE_TEXT7));
         resource.setText8(rscVarData.getUnicodeString (id, RESOURCE_TEXT8));
         resource.setText9(rscVarData.getUnicodeString (id, RESOURCE_TEXT9));
         resource.setText10(rscVarData.getUnicodeString (id, RESOURCE_TEXT10));
         resource.setText11(rscVarData.getUnicodeString (id, RESOURCE_TEXT11));
         resource.setText12(rscVarData.getUnicodeString (id, RESOURCE_TEXT12));
         resource.setText13(rscVarData.getUnicodeString (id, RESOURCE_TEXT13));
         resource.setText14(rscVarData.getUnicodeString (id, RESOURCE_TEXT14));
         resource.setText15(rscVarData.getUnicodeString (id, RESOURCE_TEXT15));
         resource.setText16(rscVarData.getUnicodeString (id, RESOURCE_TEXT16));
         resource.setText17(rscVarData.getUnicodeString (id, RESOURCE_TEXT17));
         resource.setText18(rscVarData.getUnicodeString (id, RESOURCE_TEXT18));
         resource.setText19(rscVarData.getUnicodeString (id, RESOURCE_TEXT19));
         resource.setText20(rscVarData.getUnicodeString (id, RESOURCE_TEXT20));
         resource.setText21(rscVarData.getUnicodeString (id, RESOURCE_TEXT21));
         resource.setText22(rscVarData.getUnicodeString (id, RESOURCE_TEXT22));
         resource.setText23(rscVarData.getUnicodeString (id, RESOURCE_TEXT23));
         resource.setText24(rscVarData.getUnicodeString (id, RESOURCE_TEXT24));
         resource.setText25(rscVarData.getUnicodeString (id, RESOURCE_TEXT25));
         resource.setText26(rscVarData.getUnicodeString (id, RESOURCE_TEXT26));
         resource.setText27(rscVarData.getUnicodeString (id, RESOURCE_TEXT27));
         resource.setText28(rscVarData.getUnicodeString (id, RESOURCE_TEXT28));
         resource.setText29(rscVarData.getUnicodeString (id, RESOURCE_TEXT29));
         resource.setText30(rscVarData.getUnicodeString (id, RESOURCE_TEXT30));
         resource.setType((MPPUtility.getShort(data, 14)==0?ResourceType.WORK:ResourceType.MATERIAL));
         resource.setUniqueID(id);
         resource.setWork(Duration.getInstance (MPPUtility.getDouble (data, 52)/60000, TimeUnit.HOURS));

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

         notes = rscVarData.getString (id, RESOURCE_NOTES);
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
         processResourceEnterpriseColumns(resource, rscVarData);
         
         m_file.fireResourceReadEvent(resource);
      }
   }


   /**
    * This method extracts and collates resource assignment data.
    *
    * @throws IOException
    */
   private void processAssignmentData ()
      throws IOException
   {
      DirectoryEntry assnDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndAssn");
      VarMeta assnVarMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data (assnVarMeta, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData (142, getEncryptableInputStream(assnDir, "FixedData"));
      Set<Integer> set = assnVarMeta.getUniqueIdentifierSet();
      int count = assnFixedMeta.getItemCount();

      for (int loop=0; loop < count; loop++)
      {
         byte[] meta = assnFixedMeta.getByteArrayValue(loop);
         if (meta[0] != 0)
         {
            continue;
         }

         int offset = MPPUtility.getInt(meta, 4);
         byte[] data = assnFixedData.getByteArrayValue(assnFixedData.getIndexFromOffset(offset));
         if (data == null)
         {
            continue;
         }

         int id = MPPUtility.getInt(data, 0);
         final Integer varDataId = new Integer(id);
         if (set.contains(varDataId) == false)
         {
            continue;
         }

         Integer taskID = new Integer(MPPUtility.getInt (data, 4));
         Task task = m_file.getTaskByUniqueID (taskID);

         if (task != null)
         {
            byte[] incompleteWork = assnVarData.getByteArray(assnVarMeta.getOffset(varDataId, INCOMPLETE_WORK));            
            if (task.getSplits() != null && task.getSplits().isEmpty())
            {
               byte[] completeWork = assnVarData.getByteArray(assnVarMeta.getOffset(varDataId, COMPLETE_WORK));
               processSplitData(task, completeWork, incompleteWork);
            }
            
            Integer resourceID = new Integer(MPPUtility.getInt (data, 8));
            Resource resource = m_file.getResourceByUniqueID (resourceID);

            if (resource != null)
            {
               ResourceAssignment assignment = task.addResourceAssignment (resource);
               assignment.setActualCost(NumberUtility.getDouble (MPPUtility.getDouble(data, 110)/100));
               assignment.setActualWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 70))/100, TimeUnit.HOURS));
               assignment.setCost(NumberUtility.getDouble (MPPUtility.getDouble(data, 102)/100));
               assignment.setDelay(MPPUtility.getDuration(MPPUtility.getShort(data, 24), TimeUnit.HOURS));
               assignment.setFinish(MPPUtility.getTimestamp(data, 16));
               //assignment.setOvertimeWork(); // Can't find in data block
               //assignment.setPlannedCost(); // Not sure what this field maps on to in MSP
               //assignment.setPlannedWork(); // Not sure what this field maps on to in MSP
               assignment.setRemainingWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 86))/100, TimeUnit.HOURS));
               assignment.setStart(MPPUtility.getTimestamp(data, 12));
               assignment.setUnits(new Double((MPPUtility.getDouble(data, 54))/100));
               assignment.setWork(MPPUtility.getDuration((MPPUtility.getDouble(data, 62))/100, TimeUnit.HOURS));

               if (incompleteWork != null)
               {
                  assignment.setWorkContour(WorkContour.getInstance(MPPUtility.getShort(incompleteWork, 28)));
               }
            }
         }
      }
   }

   /**
    * The task split data is represented in two blocks of data, one representing
    * the completed time, and the other representing the incomplete time.
    *
    * The completed task split data is stored in a block with a 32 byte header,
    * followed by 20 byte blocks, each representing one split. The first two
    * bytes of the header contains a count of the number of 20 byte blocks.
    *
    * The incomplete task split data is represented as a 44 byte header
    * (which also contains unrelated assignment information, such as the
    * work contour) followed by a list of 28 byte blocks, each block representing
    * one split. The first two bytes of the header contains a count of the
    * number of 28 byte blocks.
    *
    * @param task parent task
    * @param completeHours completed split data
    * @param incompleteHours incomplete split data
    */
   private void processSplitData (Task task, byte[] completeHours, byte[] incompleteHours)
   {            
      LinkedList<Duration> splits = new LinkedList<Duration> ();

      if (completeHours != null)
      {
         int splitCount = MPPUtility.getShort(completeHours, 0);
         if (splitCount != 0)
         {
            int offset = 32;
            for (int loop=0; loop < splitCount; loop++)
            {
               double splitTime = MPPUtility.getInt(completeHours, offset);
               if (splitTime != 0)
               {
                  splitTime /= 4800;
                  Duration splitDuration = Duration.getInstance(splitTime, TimeUnit.HOURS);
                  splits.add(splitDuration);
               }
               offset += 20;
            }

            double splitTime = MPPUtility.getInt(completeHours, 24);
            splitTime /= 4800;
            Duration splitDuration = Duration.getInstance(splitTime, TimeUnit.HOURS);
            splits.add(splitDuration);
         }
      }

      if (incompleteHours != null)
      {
         int splitCount = MPPUtility.getShort(incompleteHours, 0);
         
         //
         // Deal with the case where the final task split is partially complete
         //
         if (splitCount == 0)
         {
            double splitTime = MPPUtility.getInt(incompleteHours, 24);
            splitTime /= 4800;
            double timeOffset = 0;
            if (splits.isEmpty() == false)
            {
               timeOffset = splits.removeLast().getDuration();
            }
            splitTime += timeOffset;
            Duration splitDuration = Duration.getInstance(splitTime, TimeUnit.HOURS);
            splits.add(splitDuration);            
         }
         else
         {
            double timeOffset = 0;
            if (splits.isEmpty() == false)
            {
               if (splitCount % 2 != 0)
               {
                  timeOffset = splits.removeLast().getDuration();
               }
               else
               {
                  timeOffset = splits.getLast().getDuration();
               }
            }

            int offset = 44;
            for (int loop=0; loop < splitCount; loop++)
            {
               double splitTime = MPPUtility.getInt(incompleteHours, offset+24);
               splitTime /= 4800;
               splitTime += timeOffset;
               Duration splitDuration = Duration.getInstance(splitTime, TimeUnit.HOURS);
               splits.add(splitDuration);
               offset += 28;
            }
         }
      }

      //
      // We must have a minimum of 3 entries for this to be a valid split task
      //
      if (splits.size() > 2)
      {
         task.getSplits().addAll(splits);
      }
      else
      {
         task.setSplits(null);
      }
   }

   /**
    * This method is used to determine if a duration is estimated.
    *
    * @param type Duration units value
    * @return boolean Estimated flag
    */
   private boolean getDurationEstimated (int type)
   {
      return ((type & DURATION_CONFIRMED_MASK) != 0);
   }

   /**
    * This method extracts view data from the MPP file.
    *
    * @throws IOException
    */
   private void processViewData ()
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CV_iew");
      VarMeta viewVarMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data viewVarData = new Var2Data (viewVarMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
      FixedMeta fixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedMeta"))), 10);
      FixedData fixedData = new FixedData (122, getEncryptableInputStream(dir, "FixedData"));
      
      int items = fixedMeta.getItemCount();
      View view;
      ViewFactory factory = new ViewFactory9 ();

      int lastOffset = -1;
      for (int loop=0; loop < items; loop++)
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
    * @throws IOException
    */
   private void processTableData ()
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CTable");
      FixedData fixedData = new FixedData (110, getEncryptableInputStream(dir, "FixedData"));
      VarMeta varMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));

      TableFactory factory = new TableFactory(TABLE_COLUMN_DATA_STANDARD, TABLE_COLUMN_DATA_ENTERPRISE);
      int items = fixedData.getItemCount();
      for (int loop=0; loop < items; loop++)
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
   private void processFilterData ()
      throws IOException
   {           
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CFilter");
      FixedData fixedData = new FixedData (110, getEncryptableInputStream(dir, "FixedData"), true);
      VarMeta varMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));

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
    * @throws IOException
    */
   private void processGroupData ()
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CGrouping");
      FixedData fixedData = new FixedData (110, getEncryptableInputStream(dir, "FixedData"));
      VarMeta varMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
   
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
   private void processSavedViewState ()
      throws IOException
   {           
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CEdl");
      VarMeta varMeta = new VarMeta9 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
   
      //System.out.println(varMeta);
      //System.out.println(varData);
      
      ViewStateReader reader = new ViewStateReader9();
      reader.process(m_file, varData);
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
   private InputStream getEncryptableInputStream (DirectoryEntry directory, String name)
      throws IOException
   {
      DocumentEntry entry = (DocumentEntry)directory.getEntry(name);
      InputStream stream;
      if (m_passwordProtected)
      {
         stream = new EncryptedDocumentInputStream(entry);
      }
      else
      {
         stream = new DocumentInputStream (entry);
      }
      
      return (stream);
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
   private Map<Integer, FontBase> m_fontBases;
   private Map<Integer, SubProject> m_taskSubProjects;
   private DirectoryEntry m_projectDir;
   private DirectoryEntry m_viewDir;
   private boolean m_passwordProtected;
   
   /**
    * Calendar data types.
    */
   private static final Integer CALENDAR_NAME = new Integer (1);
   private static final Integer CALENDAR_DATA = new Integer (3);

   /**
    * Task data types.
    */
   private static final Integer TASK_ACTUAL_OVERTIME_WORK = new Integer (3);
   private static final Integer TASK_REMAINING_OVERTIME_WORK = new Integer (4);
   private static final Integer TASK_OVERTIME_COST = new Integer (5);
   private static final Integer TASK_ACTUAL_OVERTIME_COST = new Integer (6);
   private static final Integer TASK_REMAINING_OVERTIME_COST = new Integer (7);

   private static final Integer TASK_SUBPROJECT_TASKS_UNIQUEID_OFFSET = new Integer (8);
   private static final Integer TASK_SUBPROJECTTASKID = new Integer (9);

   private static final Integer TASK_WBS = new Integer (10);
   private static final Integer TASK_NAME = new Integer (11);
   private static final Integer TASK_CONTACT = new Integer (12);

   private static final Integer TASK_TEXT1 = new Integer (14);
   private static final Integer TASK_TEXT2 = new Integer (15);
   private static final Integer TASK_TEXT3 = new Integer (16);
   private static final Integer TASK_TEXT4 = new Integer (17);
   private static final Integer TASK_TEXT5 = new Integer (18);
   private static final Integer TASK_TEXT6 = new Integer (19);
   private static final Integer TASK_TEXT7 = new Integer (20);
   private static final Integer TASK_TEXT8 = new Integer (21);
   private static final Integer TASK_TEXT9 = new Integer (22);
   private static final Integer TASK_TEXT10 = new Integer (23);

   private static final Integer TASK_START1 = new Integer (24);
   private static final Integer TASK_FINISH1 = new Integer (25);
   private static final Integer TASK_START2 = new Integer (26);
   private static final Integer TASK_FINISH2 = new Integer (27);
   private static final Integer TASK_START3 = new Integer (28);
   private static final Integer TASK_FINISH3 = new Integer (29);
   private static final Integer TASK_START4 = new Integer (30);
   private static final Integer TASK_FINISH4 = new Integer (31);
   private static final Integer TASK_START5 = new Integer (32);
   private static final Integer TASK_FINISH5 = new Integer (33);
   private static final Integer TASK_START6 = new Integer (34);
   private static final Integer TASK_FINISH6 = new Integer (35);
   private static final Integer TASK_START7 = new Integer (36);
   private static final Integer TASK_FINISH7 = new Integer (37);
   private static final Integer TASK_START8 = new Integer (38);
   private static final Integer TASK_FINISH8 = new Integer (39);
   private static final Integer TASK_START9 = new Integer (40);
   private static final Integer TASK_FINISH9 = new Integer (41);
   private static final Integer TASK_START10 = new Integer (42);
   private static final Integer TASK_FINISH10 = new Integer (43);

   private static final Integer TASK_NUMBER1 = new Integer (45);
   private static final Integer TASK_NUMBER2 = new Integer (46);
   private static final Integer TASK_NUMBER3 = new Integer (47);
   private static final Integer TASK_NUMBER4 = new Integer (48);
   private static final Integer TASK_NUMBER5 = new Integer (49);
   private static final Integer TASK_NUMBER6 = new Integer (50);
   private static final Integer TASK_NUMBER7 = new Integer (51);
   private static final Integer TASK_NUMBER8 = new Integer (52);
   private static final Integer TASK_NUMBER9 = new Integer (53);
   private static final Integer TASK_NUMBER10 = new Integer (54);

   private static final Integer TASK_DURATION1 = new Integer (55);
   private static final Integer TASK_DURATION1_UNITS = new Integer (56);
   private static final Integer TASK_DURATION2 = new Integer (57);
   private static final Integer TASK_DURATION2_UNITS = new Integer (58);
   private static final Integer TASK_DURATION3 = new Integer (59);
   private static final Integer TASK_DURATION3_UNITS = new Integer (60);
   private static final Integer TASK_DURATION4 = new Integer (61);
   private static final Integer TASK_DURATION4_UNITS = new Integer (62);
   private static final Integer TASK_DURATION5 = new Integer (63);
   private static final Integer TASK_DURATION5_UNITS = new Integer (64);
   private static final Integer TASK_DURATION6 = new Integer (65);
   private static final Integer TASK_DURATION6_UNITS = new Integer (66);
   private static final Integer TASK_DURATION7 = new Integer (67);
   private static final Integer TASK_DURATION7_UNITS = new Integer (68);
   private static final Integer TASK_DURATION8 = new Integer (69);
   private static final Integer TASK_DURATION8_UNITS = new Integer (70);
   private static final Integer TASK_DURATION9 = new Integer (71);
   private static final Integer TASK_DURATION9_UNITS = new Integer (72);
   private static final Integer TASK_DURATION10 = new Integer (73);
   private static final Integer TASK_DURATION10_UNITS = new Integer (74);

   private static final Integer TASK_EXTERNAL_TASK_ID = new Integer (79);
   
   private static final Integer TASK_DATE1 = new Integer (80);
   private static final Integer TASK_DATE2 = new Integer (81);
   private static final Integer TASK_DATE3 = new Integer (82);
   private static final Integer TASK_DATE4 = new Integer (83);
   private static final Integer TASK_DATE5 = new Integer (84);
   private static final Integer TASK_DATE6 = new Integer (85);
   private static final Integer TASK_DATE7 = new Integer (86);
   private static final Integer TASK_DATE8 = new Integer (87);
   private static final Integer TASK_DATE9 = new Integer (88);
   private static final Integer TASK_DATE10 = new Integer (89);

   private static final Integer TASK_TEXT11 = new Integer (90);
   private static final Integer TASK_TEXT12 = new Integer (91);
   private static final Integer TASK_TEXT13 = new Integer (92);
   private static final Integer TASK_TEXT14 = new Integer (93);
   private static final Integer TASK_TEXT15 = new Integer (94);
   private static final Integer TASK_TEXT16 = new Integer (95);
   private static final Integer TASK_TEXT17 = new Integer (96);
   private static final Integer TASK_TEXT18 = new Integer (97);
   private static final Integer TASK_TEXT19 = new Integer (98);
   private static final Integer TASK_TEXT20 = new Integer (99);
   private static final Integer TASK_TEXT21 = new Integer (100);
   private static final Integer TASK_TEXT22 = new Integer (101);
   private static final Integer TASK_TEXT23 = new Integer (102);
   private static final Integer TASK_TEXT24 = new Integer (103);
   private static final Integer TASK_TEXT25 = new Integer (104);
   private static final Integer TASK_TEXT26 = new Integer (105);
   private static final Integer TASK_TEXT27 = new Integer (106);
   private static final Integer TASK_TEXT28 = new Integer (107);
   private static final Integer TASK_TEXT29 = new Integer (108);
   private static final Integer TASK_TEXT30 = new Integer (109);

   private static final Integer TASK_NUMBER11 = new Integer (110);
   private static final Integer TASK_NUMBER12 = new Integer (111);
   private static final Integer TASK_NUMBER13 = new Integer (112);
   private static final Integer TASK_NUMBER14 = new Integer (113);
   private static final Integer TASK_NUMBER15 = new Integer (114);
   private static final Integer TASK_NUMBER16 = new Integer (115);
   private static final Integer TASK_NUMBER17 = new Integer (116);
   private static final Integer TASK_NUMBER18 = new Integer (117);
   private static final Integer TASK_NUMBER19 = new Integer (118);
   private static final Integer TASK_NUMBER20 = new Integer (119);

   private static final Integer TASK_OUTLINECODE1 = new Integer (123);
   private static final Integer TASK_OUTLINECODE2 = new Integer (124);
   private static final Integer TASK_OUTLINECODE3 = new Integer (125);
   private static final Integer TASK_OUTLINECODE4 = new Integer (126);
   private static final Integer TASK_OUTLINECODE5 = new Integer (127);
   private static final Integer TASK_OUTLINECODE6 = new Integer (128);
   private static final Integer TASK_OUTLINECODE7 = new Integer (129);
   private static final Integer TASK_OUTLINECODE8 = new Integer (130);
   private static final Integer TASK_OUTLINECODE9 = new Integer (131);
   private static final Integer TASK_OUTLINECODE10 = new Integer (132);

   private static final Integer TASK_HYPERLINK = new Integer (133);

   private static final Integer TASK_COST1 = new Integer (134);
   private static final Integer TASK_COST2 = new Integer (135);
   private static final Integer TASK_COST3 = new Integer (136);
   private static final Integer TASK_COST4 = new Integer (137);
   private static final Integer TASK_COST5 = new Integer (138);
   private static final Integer TASK_COST6 = new Integer (139);
   private static final Integer TASK_COST7 = new Integer (140);
   private static final Integer TASK_COST8 = new Integer (141);
   private static final Integer TASK_COST9 = new Integer (142);
   private static final Integer TASK_COST10 = new Integer (143);

   private static final Integer TASK_NOTES = new Integer (144);

   private static final Integer TASK_ENTERPRISE_COLUMNS = new Integer(163);
   

   /**
    * Resource data types.
    */
   private static final Integer RESOURCE_NAME = new Integer (1);
   private static final Integer RESOURCE_INITIALS = new Integer (3);
   private static final Integer RESOURCE_GROUP = new Integer (4);
   private static final Integer RESOURCE_CODE = new Integer (5);
   private static final Integer RESOURCE_EMAIL = new Integer (6);

   private static final Integer RESOURCE_TEXT1 = new Integer (10);
   private static final Integer RESOURCE_TEXT2 = new Integer (11);
   private static final Integer RESOURCE_TEXT3 = new Integer (12);
   private static final Integer RESOURCE_TEXT4 = new Integer (13);
   private static final Integer RESOURCE_TEXT5 = new Integer (14);
   private static final Integer RESOURCE_TEXT6 = new Integer (15);
   private static final Integer RESOURCE_TEXT7 = new Integer (16);
   private static final Integer RESOURCE_TEXT8 = new Integer (17);
   private static final Integer RESOURCE_TEXT9 = new Integer (18);
   private static final Integer RESOURCE_TEXT10 = new Integer (19);
   private static final Integer RESOURCE_TEXT11 = new Integer (20);
   private static final Integer RESOURCE_TEXT12 = new Integer (21);
   private static final Integer RESOURCE_TEXT13 = new Integer (22);
   private static final Integer RESOURCE_TEXT14 = new Integer (23);
   private static final Integer RESOURCE_TEXT15 = new Integer (24);
   private static final Integer RESOURCE_TEXT16 = new Integer (25);
   private static final Integer RESOURCE_TEXT17 = new Integer (26);
   private static final Integer RESOURCE_TEXT18 = new Integer (27);
   private static final Integer RESOURCE_TEXT19 = new Integer (28);
   private static final Integer RESOURCE_TEXT20 = new Integer (29);
   private static final Integer RESOURCE_TEXT21 = new Integer (30);
   private static final Integer RESOURCE_TEXT22 = new Integer (31);
   private static final Integer RESOURCE_TEXT23 = new Integer (32);
   private static final Integer RESOURCE_TEXT24 = new Integer (33);
   private static final Integer RESOURCE_TEXT25 = new Integer (34);
   private static final Integer RESOURCE_TEXT26 = new Integer (35);
   private static final Integer RESOURCE_TEXT27 = new Integer (36);
   private static final Integer RESOURCE_TEXT28 = new Integer (37);
   private static final Integer RESOURCE_TEXT29 = new Integer (38);
   private static final Integer RESOURCE_TEXT30 = new Integer (39);

   private static final Integer RESOURCE_START1 = new Integer (40);
   private static final Integer RESOURCE_START2 = new Integer (41);
   private static final Integer RESOURCE_START3 = new Integer (42);
   private static final Integer RESOURCE_START4 = new Integer (43);
   private static final Integer RESOURCE_START5 = new Integer (44);
   private static final Integer RESOURCE_START6 = new Integer (45);
   private static final Integer RESOURCE_START7 = new Integer (46);
   private static final Integer RESOURCE_START8 = new Integer (47);
   private static final Integer RESOURCE_START9 = new Integer (48);
   private static final Integer RESOURCE_START10 = new Integer (49);

   private static final Integer RESOURCE_FINISH1 = new Integer (50);
   private static final Integer RESOURCE_FINISH2 = new Integer (51);
   private static final Integer RESOURCE_FINISH3 = new Integer (52);
   private static final Integer RESOURCE_FINISH4 = new Integer (53);
   private static final Integer RESOURCE_FINISH5 = new Integer (54);
   private static final Integer RESOURCE_FINISH6 = new Integer (55);
   private static final Integer RESOURCE_FINISH7 = new Integer (56);
   private static final Integer RESOURCE_FINISH8 = new Integer (57);
   private static final Integer RESOURCE_FINISH9 = new Integer (58);
   private static final Integer RESOURCE_FINISH10 = new Integer (59);

   private static final Integer RESOURCE_NUMBER1 = new Integer (60);
   private static final Integer RESOURCE_NUMBER2 = new Integer (61);
   private static final Integer RESOURCE_NUMBER3 = new Integer (62);
   private static final Integer RESOURCE_NUMBER4 = new Integer (63);
   private static final Integer RESOURCE_NUMBER5 = new Integer (64);
   private static final Integer RESOURCE_NUMBER6 = new Integer (65);
   private static final Integer RESOURCE_NUMBER7 = new Integer (66);
   private static final Integer RESOURCE_NUMBER8 = new Integer (67);
   private static final Integer RESOURCE_NUMBER9 = new Integer (68);
   private static final Integer RESOURCE_NUMBER10 = new Integer (69);
   private static final Integer RESOURCE_NUMBER11 = new Integer (70);
   private static final Integer RESOURCE_NUMBER12 = new Integer (71);
   private static final Integer RESOURCE_NUMBER13 = new Integer (72);
   private static final Integer RESOURCE_NUMBER14 = new Integer (73);
   private static final Integer RESOURCE_NUMBER15 = new Integer (74);
   private static final Integer RESOURCE_NUMBER16 = new Integer (75);
   private static final Integer RESOURCE_NUMBER17 = new Integer (76);
   private static final Integer RESOURCE_NUMBER18 = new Integer (77);
   private static final Integer RESOURCE_NUMBER19 = new Integer (78);
   private static final Integer RESOURCE_NUMBER20 = new Integer (79);

   private static final Integer RESOURCE_DURATION1 = new Integer (80);
   private static final Integer RESOURCE_DURATION2 = new Integer (81);
   private static final Integer RESOURCE_DURATION3 = new Integer (82);
   private static final Integer RESOURCE_DURATION4 = new Integer (83);
   private static final Integer RESOURCE_DURATION5 = new Integer (84);
   private static final Integer RESOURCE_DURATION6 = new Integer (85);
   private static final Integer RESOURCE_DURATION7 = new Integer (86);
   private static final Integer RESOURCE_DURATION8 = new Integer (87);
   private static final Integer RESOURCE_DURATION9 = new Integer (88);
   private static final Integer RESOURCE_DURATION10 = new Integer (89);

   private static final Integer RESOURCE_DURATION1_UNITS = new Integer (90);
   private static final Integer RESOURCE_DURATION2_UNITS = new Integer (91);
   private static final Integer RESOURCE_DURATION3_UNITS = new Integer (92);
   private static final Integer RESOURCE_DURATION4_UNITS = new Integer (93);
   private static final Integer RESOURCE_DURATION5_UNITS = new Integer (94);
   private static final Integer RESOURCE_DURATION6_UNITS = new Integer (95);
   private static final Integer RESOURCE_DURATION7_UNITS = new Integer (96);
   private static final Integer RESOURCE_DURATION8_UNITS = new Integer (97);
   private static final Integer RESOURCE_DURATION9_UNITS = new Integer (98);
   private static final Integer RESOURCE_DURATION10_UNITS = new Integer (99);

   private static final Integer RESOURCE_SUBPROJECTRESOURCEID = new Integer (102);

   private static final Integer RESOURCE_DATE1 = new Integer (103);
   private static final Integer RESOURCE_DATE2 = new Integer (104);
   private static final Integer RESOURCE_DATE3 = new Integer (105);
   private static final Integer RESOURCE_DATE4 = new Integer (106);
   private static final Integer RESOURCE_DATE5 = new Integer (107);
   private static final Integer RESOURCE_DATE6 = new Integer (108);
   private static final Integer RESOURCE_DATE7 = new Integer (109);
   private static final Integer RESOURCE_DATE8 = new Integer (110);
   private static final Integer RESOURCE_DATE9 = new Integer (111);
   private static final Integer RESOURCE_DATE10 = new Integer (112);

   private static final Integer RESOURCE_OUTLINECODE1 = new Integer (113);
   private static final Integer RESOURCE_OUTLINECODE2 = new Integer (114);
   private static final Integer RESOURCE_OUTLINECODE3 = new Integer (115);
   private static final Integer RESOURCE_OUTLINECODE4 = new Integer (116);
   private static final Integer RESOURCE_OUTLINECODE5 = new Integer (117);
   private static final Integer RESOURCE_OUTLINECODE6 = new Integer (118);
   private static final Integer RESOURCE_OUTLINECODE7 = new Integer (119);
   private static final Integer RESOURCE_OUTLINECODE8 = new Integer (120);
   private static final Integer RESOURCE_OUTLINECODE9 = new Integer (121);
   private static final Integer RESOURCE_OUTLINECODE10 = new Integer (122);

   private static final Integer RESOURCE_NOTES = new Integer (124);

   private static final Integer RESOURCE_COST1 = new Integer (125);
   private static final Integer RESOURCE_COST2 = new Integer (126);
   private static final Integer RESOURCE_COST3 = new Integer (127);
   private static final Integer RESOURCE_COST4 = new Integer (128);
   private static final Integer RESOURCE_COST5 = new Integer (129);
   private static final Integer RESOURCE_COST6 = new Integer (130);
   private static final Integer RESOURCE_COST7 = new Integer (131);
   private static final Integer RESOURCE_COST8 = new Integer (132);
   private static final Integer RESOURCE_COST9 = new Integer (133);
   private static final Integer RESOURCE_COST10 = new Integer (134);

   private static final Integer RESOURCE_ENTERPRISE_COLUMNS = new Integer(143);
   
   private static final Integer TABLE_COLUMN_DATA_STANDARD = new Integer (1);
   private static final Integer TABLE_COLUMN_DATA_ENTERPRISE = new Integer (2);   
   private static final Integer OUTLINECODE_DATA = new Integer (1);
   private static final Integer INCOMPLETE_WORK = new Integer(7);
   private static final Integer COMPLETE_WORK = new Integer(9);

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

   private static final int MINIMUM_EXPECTED_TASK_SIZE = 240;
   private static final int MINIMUM_EXPECTED_RESOURCE_SIZE = 188;
}
