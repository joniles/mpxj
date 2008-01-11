/*
 * file:       MPP12Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2005
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

import java.io.IOException;
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
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
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
   public void process (MPPReader reader, ProjectFile file, DirectoryEntry root)
      throws MPXJException, IOException
   {
      try
      {
         //
         // Retrieve the high level document properties
         //
         Props12 props12 = new Props12 (new DocumentInputStream (((DocumentEntry)root.getEntry("Props12"))));
         //System.out.println(props12);
   
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
            throw new MPXJException (MPXJException.PASSWORD_PROTECTED);
         }
         
         m_reader = reader;
         m_file = file;
         m_root = root;
         m_resourceMap = new HashMap<Integer, ProjectCalendar> ();
         m_projectDir = (DirectoryEntry)root.getEntry ("   112");
         m_viewDir = (DirectoryEntry)root.getEntry ("   212");
         DirectoryEntry outlineCodeDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndOutlCode");
         VarMeta outlineCodeVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("VarMeta"))));
         m_outlineCodeVarData = new Var2Data (outlineCodeVarMeta, new DocumentInputStream (((DocumentEntry)outlineCodeDir.getEntry("Var2Data"))));
         m_fontBases = new HashMap<Integer, FontBase>();
         m_taskSubProjects = new HashMap<Integer, SubProject> ();
         m_parentTasks = new HashMap<Integer, Integer> ();
         
         m_file.setMppFileType(12);
         m_file.setAutoFilter(props12.getBoolean(Props.AUTO_FILTER));
   
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
    * @throws java.io.IOException
    */
   private void processPropertyData ()
      throws IOException, MPXJException
   {
      Props12 props = new Props12 (new DocumentInputStream (((DocumentEntry)m_projectDir.getEntry("Props"))));
      //MPPUtility.fileHexDump("c:\\temp\\props.txt", props.toString().getBytes());

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
   private void processSubProjectData (Props12 props)
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
            itemHeaderOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
            offset += 4;

            MPPUtility.getByteArray(subProjData, itemHeaderOffset, itemHeader.length, itemHeader, 0);

            //System.out.println (MPPUtility.hexdump(itemHeader, false, 16, ""));
            //System.out.println ("Offset: " + offset);
            //System.out.println ("Item Header Offset: " + itemHeaderOffset);
            byte subProjectType = itemHeader[16]; 
            //System.out.println("SubProjectType: " + Integer.toHexString(subProjectType));
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
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  // sometimes offset of a task ID?
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  // unknown offset to 2 bytes of data?
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
                  uniqueIDOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
                  filePathOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
                  offset += 4;

                  offset += 4;
                  
                  fileNameOffset = MPPUtility.getInt(subProjData, offset) & 0x1FFFF;
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
      //System.out.println(sp.getTaskUniqueID()+","+sp.getFileName());
      return (sp);
   }

   /**
    * This method process the data held in the props file specific to the
    * visual appearance of the project data.
    */
   private void processViewPropertyData ()
      throws IOException
   {
      Props12 props = new Props12 (new DocumentInputStream (((DocumentEntry)m_viewDir.getEntry("Props"))));
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
    * @throws java.io.IOException
    */
   private void processCalendarData ()
      throws MPXJException, IOException
   {
      DirectoryEntry calDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndCal");

      //MPPUtility.fileHexDump("c:\\temp\\varmeta.txt", new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));

      VarMeta calVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));
      Var2Data calVarData = new Var2Data (calVarMeta, new DocumentInputStream ((DocumentEntry)calDir.getEntry("Var2Data")));

      //System.out.println(calVarMeta);
      //System.out.println(calVarData);
      
      FixedMeta calFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixedMeta"))), 10);
      FixedData calFixedData = new FixedData (calFixedMeta, new DocumentInputStream (((DocumentEntry)calDir.getEntry("FixedData"))));

      //System.out.println (calFixedMeta);
      //System.out.println (calFixedData);
      
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
                     //Incomplete
                     //processCalendarExceptions (varData, cal);
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
    * NOTE: MPP12 defines the concept of working weeks. MPXJ does not
    * currently support this, and thus we only read the working hours
    * for the default working week.
    * 
    * @param data calendar data block
    * @param cal calendar instance
    * @param isBaseCalendar true if this is a base calendar
    * @throws net.sf.mpxj.MPXJException
    */
   private void processCalendarHours (byte[] data, ProjectCalendar cal, boolean isBaseCalendar)
      throws MPXJException
   {
      //System.out.println(MPPUtility.hexdump(data, false, 16, ""));
      
      int offset;
      ProjectCalendarHours hours;
      int periodIndex;
      int index;
      //int defaultFlag;
      Date start;
      long duration;
      //Day day;
      List<DateRange> dateRanges = new ArrayList<DateRange>(5);
      
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
         offset = 60 * index;
         Day day = Day.getInstance(index+1);
         int useProjectDefault = MPPUtility.getShort (data, offset);
         if (useProjectDefault == 1)
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
            int useBaseCalendarDefault = MPPUtility.getShort (data, offset+2);
            
   
            if (useBaseCalendarDefault == 1)
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
               dateRanges.clear();
               
               periodIndex = 0;
               while (periodIndex < 5)
               {
                  int startOffset = offset + 8 + (periodIndex * 2);
                  if (MPPUtility.getShort(data, startOffset) == 0)
                  {
                     break;
                  }
                  start = MPPUtility.getTime (data, startOffset);
                  int durationOffset = offset + 20 + (periodIndex * 4);
                  duration = MPPUtility.getDuration (data, durationOffset);
                  Date end = new Date (start.getTime()+duration);
                  dateRanges.add(new DateRange (start, end));              
                  ++periodIndex;
               }
            }
            
            if (dateRanges.isEmpty())
            {
               cal.setWorkingDay(day, false);
            }
            else
            {
               cal.setWorkingDay(day, true);
               hours = cal.addCalendarHours(Day.getInstance(index+1));

               for (DateRange range : dateRanges)
               {
                  hours.addDateRange(range);
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
//   private void processCalendarExceptions (byte[] data, ProjectCalendar cal)
//   {
//      //
//      // Handle any exceptions
//      //
//      int exceptionCount = MPPUtility.getShort (data, 0);
//
//      if (exceptionCount != 0)
//      {
//         int index;
//         int offset;
//         ProjectCalendarException exception;
//         long duration;
//         int periodCount;
//         Date start;
//
//         for (index=0; index < exceptionCount; index++)
//         {
//            offset = 4 + (60 * 7) + (index * 64);
//            exception = cal.addCalendarException();
//            exception.setFromDate(MPPUtility.getDate (data, offset));
//            exception.setToDate(MPPUtility.getDate (data, offset+2));
//
//            periodCount = MPPUtility.getShort (data, offset+6);
//            if (periodCount == 0)
//            {
//               exception.setWorking (false);
//            }
//            else
//            {
//               exception.setWorking (true);
//
//               start = MPPUtility.getTime (data, offset+12);
//               duration = MPPUtility.getDuration (data, offset+24);
//               exception.setFromTime1(start);
//               exception.setToTime1(new Date (start.getTime() + duration));
//
//               if (periodCount > 1)
//               {
//                  start = MPPUtility.getTime (data, offset+14);
//                  duration = MPPUtility.getDuration (data, offset+28);
//                  exception.setFromTime2(start);
//                  exception.setToTime2(new Date (start.getTime() + duration));
//
//                  if (periodCount > 2)
//                  {
//                     start = MPPUtility.getTime (data, offset+16);
//                     duration = MPPUtility.getDuration (data, offset+32);
//                     exception.setFromTime3(start);
//                     exception.setToTime3(new Date (start.getTime() + duration));
//                  }
//               }
//               //
//               // Note that MPP defines 5 time ranges rather than 3
//               //
//            }
//         }
//      }
//   }


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
    * @throws java.io.IOException
    */
   private void processTaskData ()
      throws IOException
   {
      DirectoryEntry taskDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndTask");
      VarMeta taskVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("VarMeta"))));
      Var2Data taskVarData = new Var2Data (taskVarMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Var2Data"))));
      FixedMeta taskFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedMeta"))), 47);
      FixedData taskFixedData = new FixedData (taskFixedMeta, new DocumentInputStream (((DocumentEntry)taskDir.getEntry("FixedData"))), 768);     
      FixedMeta taskFixed2Meta = new FixedMeta (new DocumentInputStream (((DocumentEntry)taskDir.getEntry("Fixed2Meta"))), 86);
      
      //System.out.println(taskFixedMeta);
      //System.out.println(taskFixedData);
      //System.out.println(taskVarMeta);
      //System.out.println(taskVarData);
      //System.out.println(taskFixed2Meta);
      //System.out.println(outlineCodeVarData.getVarMeta());
      //System.out.println(outlineCodeVarData);
      
      TreeMap<Integer, Integer> taskMap = createTaskMap (taskFixedMeta, taskFixedData);
      Integer[] uniqueid = taskVarMeta.getUniqueIdentifierArray();
      Integer id;
      Integer offset;
      byte[] data;
      byte[] metaData;
      byte[] metaData2;
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

         metaData2 = taskFixed2Meta.getByteArrayValue(offset.intValue());
         //System.out.println (MPPUtility.hexdump(metaData2, false, 16, ""));
         
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
         task.setOutlineCode1(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE1)), OUTLINECODE_DATA));
         task.setOutlineCode2(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE2)), OUTLINECODE_DATA));
         task.setOutlineCode3(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE3)), OUTLINECODE_DATA));
         task.setOutlineCode4(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE4)), OUTLINECODE_DATA));
         task.setOutlineCode5(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE5)), OUTLINECODE_DATA));
         task.setOutlineCode6(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE6)), OUTLINECODE_DATA));
         task.setOutlineCode7(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE7)), OUTLINECODE_DATA));
         task.setOutlineCode8(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE8)), OUTLINECODE_DATA));
         task.setOutlineCode9(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE9)), OUTLINECODE_DATA));
         task.setOutlineCode10(m_outlineCodeVarData.getUnicodeString(new Integer(taskVarData.getInt (id, 2, TASK_OUTLINECODE10)), OUTLINECODE_DATA));
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
         task.setUniqueID(new Integer(MPPUtility.getInt(data, 0)));
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
         
         m_parentTasks.put(task.getUniqueID(), new Integer(MPPUtility.getInt(data, 36)));
         
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
         processTaskEnterpriseColumns(id, task, taskVarData, metaData2);
         
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
      
      //
      // MPP12 files seem to exhibit some occasional weirdness
      // with duplicate ID values which leads to the task structure
      // being reported incorrectly. The following method
      // attempts to correct this.
      //
      validateStructure();
   }

   /**
    * This method is called to validate the task hierarchy.
    * Some MPP12 files contain duplicate task ID values which
    * causes the task hierarchy to be generated incorrectly.
    */
   private void validateStructure ()
   {
      //
      // Retrieve the list of all tasks
      // an sort into ID order
      //
      List<Task> tasks = m_file.getAllTasks();
      Collections.sort(tasks);
      
      //
      // Look for duplicate ID values
      //
      int lastID = -1;
      int currentID = -2;
      for(Task task : tasks)
      {
         currentID = task.getID().intValue();
         if (currentID == lastID)
         {
            break;
         }
         lastID = currentID;
      }
      
      //
      // If we've found a duplicate, ensure
      // that the structure is correct
      //
      if (lastID == currentID)
      {
         fixStructure();
      }
   }
   
   /**
    * This method is called to fix the task hierarchy if problems
    * are detected.
    */
   private void fixStructure ()
   {
      //
      // Create the hierarchical structure
      //
      m_file.updateStructure();
      
      //
      // Validate the parent for each task
      //
      for (Task task: m_file.getAllTasks())
      {
         Task parentTask = task.getParentTask();
         Integer parentTaskID = m_parentTasks.get(task.getUniqueID());
         
         if ((parentTask == null && parentTaskID.intValue() != -1) ||
             (parentTask != null && parentTaskID.intValue() == -1) ||
             (parentTask != null && parentTask.getUniqueID().intValue() != parentTaskID.intValue()))
         {            
            if (parentTask != null)
            {
               parentTask.removeChildTask(task);
            }
            
            if (parentTaskID.intValue() != -1)
            {
               Task newParent = m_file.getTaskByUniqueID(parentTaskID);
               newParent.addChildTask(task);
               //System.out.println("Fixed: " + task);
            }
         }
      }
      
      //
      // Renumber the task ID values
      //
      int nextID = (m_file.getTaskByID(NumberUtility.INTEGER_ZERO)==null?1:0);      
      for (Task task: m_file.getChildTasks())
      {
         task.setID(new Integer(nextID++));
         nextID = renumberChildren(nextID, task);
      }     
   }

   /**
    * Renumbers child task IDs.
    * 
    * @param nextID next ID value
    * @param parent parent task
    * @return next ID value
    */
   private int renumberChildren(int nextID, Task parent)
   {
      for (Task task: parent.getChildTasks())
      {
         task.setID(new Integer(nextID++));
         nextID = renumberChildren(nextID, task);
      }      
      return (nextID);
   }
   
   /**
    * Extracts task enterprise column values. 
    * 
    * @param id task unique ID
    * @param task task instance
    * @param taskVarData task var data
    * @param metaData2 task meta data
    */
   private void processTaskEnterpriseColumns (Integer id, Task task, Var2Data taskVarData, byte[] metaData2)
   {
      task.setEnterpriseCost(1, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST1) / 100));
      task.setEnterpriseCost(2, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST2) / 100));
      task.setEnterpriseCost(3, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST3) / 100));
      task.setEnterpriseCost(4, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST4) / 100));
      task.setEnterpriseCost(5, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST5) / 100));
      task.setEnterpriseCost(6, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST6) / 100));
      task.setEnterpriseCost(7, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST7) / 100));
      task.setEnterpriseCost(8, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST8) / 100));
      task.setEnterpriseCost(9, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST9) / 100));
      task.setEnterpriseCost(10, NumberUtility.getDouble (taskVarData.getDouble (id, TASK_ENTERPRISE_COST10) / 100));   
      
      task.setEnterpriseDate(1, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE1));
      task.setEnterpriseDate(2, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE2));
      task.setEnterpriseDate(3, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE3));
      task.setEnterpriseDate(4, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE4));
      task.setEnterpriseDate(5, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE5));
      task.setEnterpriseDate(6, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE6));
      task.setEnterpriseDate(7, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE7));
      task.setEnterpriseDate(8, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE8));
      task.setEnterpriseDate(9, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE9));
      task.setEnterpriseDate(10, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE10));
      task.setEnterpriseDate(11, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE11));
      task.setEnterpriseDate(12, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE12));
      task.setEnterpriseDate(13, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE13));
      task.setEnterpriseDate(14, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE14));
      task.setEnterpriseDate(15, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE15));
      task.setEnterpriseDate(16, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE16));
      task.setEnterpriseDate(17, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE17));
      task.setEnterpriseDate(18, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE18));
      task.setEnterpriseDate(19, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE19));
      task.setEnterpriseDate(20, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE20));
      task.setEnterpriseDate(21, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE21));
      task.setEnterpriseDate(22, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE22));
      task.setEnterpriseDate(23, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE23));
      task.setEnterpriseDate(24, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE24));
      task.setEnterpriseDate(25, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE25));
      task.setEnterpriseDate(26, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE26));
      task.setEnterpriseDate(27, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE27));
      task.setEnterpriseDate(28, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE28));
      task.setEnterpriseDate(29, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE29));
      task.setEnterpriseDate(30, taskVarData.getTimestamp (id, TASK_ENTERPRISE_DATE30));
            
      task.setEnterpriseDuration(1, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION1), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION1_UNITS))));
      task.setEnterpriseDuration(2, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION2), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION2_UNITS))));
      task.setEnterpriseDuration(3, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION3), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION3_UNITS))));
      task.setEnterpriseDuration(4, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION4), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION4_UNITS))));
      task.setEnterpriseDuration(5, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION5), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION5_UNITS))));
      task.setEnterpriseDuration(6, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION6), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION6_UNITS))));
      task.setEnterpriseDuration(7, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION7), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION7_UNITS))));
      task.setEnterpriseDuration(8, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION8), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION8_UNITS))));
      task.setEnterpriseDuration(9, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION9), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION9_UNITS))));
      task.setEnterpriseDuration(10, MPPUtility.getAdjustedDuration (m_file, taskVarData.getInt(id, TASK_ENTERPRISE_DURATION10), MPPUtility.getDurationTimeUnits(taskVarData.getShort(id, TASK_ENTERPRISE_DURATION10_UNITS))));
      
      task.setEnterpriseNumber(1, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER1)));      
      task.setEnterpriseNumber(2, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER2)));
      task.setEnterpriseNumber(3, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER3)));
      task.setEnterpriseNumber(4, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER4)));
      task.setEnterpriseNumber(5, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER5)));
      task.setEnterpriseNumber(6, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER6)));
      task.setEnterpriseNumber(7, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER7)));
      task.setEnterpriseNumber(8, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER8)));
      task.setEnterpriseNumber(9, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER9)));
      task.setEnterpriseNumber(10, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER10)));      
      task.setEnterpriseNumber(11, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER11)));      
      task.setEnterpriseNumber(12, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER12)));
      task.setEnterpriseNumber(13, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER13)));
      task.setEnterpriseNumber(14, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER14)));
      task.setEnterpriseNumber(15, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER15)));
      task.setEnterpriseNumber(16, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER16)));
      task.setEnterpriseNumber(17, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER17)));
      task.setEnterpriseNumber(18, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER18)));
      task.setEnterpriseNumber(19, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER19)));
      task.setEnterpriseNumber(20, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER20)));      
      task.setEnterpriseNumber(21, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER21)));
      task.setEnterpriseNumber(22, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER22)));
      task.setEnterpriseNumber(23, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER23)));
      task.setEnterpriseNumber(24, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER24)));
      task.setEnterpriseNumber(25, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER25)));
      task.setEnterpriseNumber(26, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER26)));
      task.setEnterpriseNumber(27, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER27)));
      task.setEnterpriseNumber(28, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER28)));
      task.setEnterpriseNumber(29, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER29)));      
      task.setEnterpriseNumber(30, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER30)));
      task.setEnterpriseNumber(31, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER31)));
      task.setEnterpriseNumber(32, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER32)));
      task.setEnterpriseNumber(33, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER33)));
      task.setEnterpriseNumber(34, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER34)));
      task.setEnterpriseNumber(35, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER35)));
      task.setEnterpriseNumber(36, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER36)));
      task.setEnterpriseNumber(37, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER37)));
      task.setEnterpriseNumber(38, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER38)));
      task.setEnterpriseNumber(39, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER39)));
      task.setEnterpriseNumber(40, NumberUtility.getDouble (taskVarData.getDouble(id, TASK_ENTERPRISE_NUMBER40)));
            
      task.setEnterpriseText(1, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT1));
      task.setEnterpriseText(2, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT2));
      task.setEnterpriseText(3, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT3));
      task.setEnterpriseText(4, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT4));
      task.setEnterpriseText(5, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT5));
      task.setEnterpriseText(6, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT6));
      task.setEnterpriseText(7, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT7));
      task.setEnterpriseText(8, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT8));
      task.setEnterpriseText(9, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT9));
      task.setEnterpriseText(10, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT10));
      task.setEnterpriseText(11, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT11));
      task.setEnterpriseText(12, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT12));
      task.setEnterpriseText(13, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT13));
      task.setEnterpriseText(14, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT14));
      task.setEnterpriseText(15, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT15));
      task.setEnterpriseText(16, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT16));
      task.setEnterpriseText(17, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT17));
      task.setEnterpriseText(18, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT18));
      task.setEnterpriseText(19, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT19));
      task.setEnterpriseText(20, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT20));
      task.setEnterpriseText(21, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT21));
      task.setEnterpriseText(22, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT22));
      task.setEnterpriseText(23, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT23));
      task.setEnterpriseText(24, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT24));
      task.setEnterpriseText(25, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT25));
      task.setEnterpriseText(26, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT26));
      task.setEnterpriseText(27, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT27));
      task.setEnterpriseText(28, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT28));
      task.setEnterpriseText(29, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT29));      
      task.setEnterpriseText(30, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT30));
      task.setEnterpriseText(31, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT31));
      task.setEnterpriseText(32, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT32));
      task.setEnterpriseText(33, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT33));
      task.setEnterpriseText(34, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT34));
      task.setEnterpriseText(35, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT35));
      task.setEnterpriseText(36, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT36));
      task.setEnterpriseText(37, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT37));
      task.setEnterpriseText(38, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT38));
      task.setEnterpriseText(39, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT39));
      task.setEnterpriseText(40, taskVarData.getUnicodeString (id, TASK_ENTERPRISE_TEXT40));
      
      if (metaData2 != null)
      {
         int bits = MPPUtility.getInt(metaData2, 59);                  
         task.set(TaskField.ENTERPRISE_FLAG1, new Boolean((bits & 0x00001) != 0));
         task.set(TaskField.ENTERPRISE_FLAG2, new Boolean((bits & 0x00002) != 0));
         task.set(TaskField.ENTERPRISE_FLAG3, new Boolean((bits & 0x00004) != 0));
         task.set(TaskField.ENTERPRISE_FLAG4, new Boolean((bits & 0x00008) != 0));
         task.set(TaskField.ENTERPRISE_FLAG5, new Boolean((bits & 0x00010) != 0));
         task.set(TaskField.ENTERPRISE_FLAG6, new Boolean((bits & 0x00020) != 0));
         task.set(TaskField.ENTERPRISE_FLAG7, new Boolean((bits & 0x00040) != 0));
         task.set(TaskField.ENTERPRISE_FLAG8, new Boolean((bits & 0x00080) != 0));
         task.set(TaskField.ENTERPRISE_FLAG9, new Boolean((bits & 0x00100) != 0));
         task.set(TaskField.ENTERPRISE_FLAG10, new Boolean((bits & 0x00200) != 0));
         task.set(TaskField.ENTERPRISE_FLAG11, new Boolean((bits & 0x00400) != 0));
         task.set(TaskField.ENTERPRISE_FLAG12, new Boolean((bits & 0x00800) != 0));
         task.set(TaskField.ENTERPRISE_FLAG13, new Boolean((bits & 0x01000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG14, new Boolean((bits & 0x02000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG15, new Boolean((bits & 0x04000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG16, new Boolean((bits & 0x08000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG17, new Boolean((bits & 0x10000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG18, new Boolean((bits & 0x20000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG19, new Boolean((bits & 0x40000) != 0));
         task.set(TaskField.ENTERPRISE_FLAG20, new Boolean((bits & 0x80000) != 0));
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
   private void processResourceEnterpriseColumns (Integer id, Resource resource, Var2Data resourceVarData, byte[] metaData2)
   {
      resource.setEnterpriseCost(1, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST1) / 100));
      resource.setEnterpriseCost(2, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST2) / 100));
      resource.setEnterpriseCost(3, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST3) / 100));
      resource.setEnterpriseCost(4, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST4) / 100));
      resource.setEnterpriseCost(5, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST5) / 100));
      resource.setEnterpriseCost(6, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST6) / 100));
      resource.setEnterpriseCost(7, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST7) / 100));
      resource.setEnterpriseCost(8, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST8) / 100));
      resource.setEnterpriseCost(9, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST9) / 100));
      resource.setEnterpriseCost(10, NumberUtility.getDouble (resourceVarData.getDouble (id, RESOURCE_ENTERPRISE_COST10) / 100));   
      
      resource.setEnterpriseDate(1, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE1));
      resource.setEnterpriseDate(2, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE2));
      resource.setEnterpriseDate(3, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE3));
      resource.setEnterpriseDate(4, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE4));
      resource.setEnterpriseDate(5, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE5));
      resource.setEnterpriseDate(6, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE6));
      resource.setEnterpriseDate(7, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE7));
      resource.setEnterpriseDate(8, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE8));
      resource.setEnterpriseDate(9, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE9));
      resource.setEnterpriseDate(10, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE10));
      resource.setEnterpriseDate(11, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE11));
      resource.setEnterpriseDate(12, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE12));
      resource.setEnterpriseDate(13, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE13));
      resource.setEnterpriseDate(14, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE14));
      resource.setEnterpriseDate(15, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE15));
      resource.setEnterpriseDate(16, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE16));
      resource.setEnterpriseDate(17, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE17));
      resource.setEnterpriseDate(18, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE18));
      resource.setEnterpriseDate(19, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE19));
      resource.setEnterpriseDate(20, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE20));
      resource.setEnterpriseDate(21, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE21));
      resource.setEnterpriseDate(22, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE22));
      resource.setEnterpriseDate(23, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE23));
      resource.setEnterpriseDate(24, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE24));
      resource.setEnterpriseDate(25, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE25));
      resource.setEnterpriseDate(26, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE26));
      resource.setEnterpriseDate(27, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE27));
      resource.setEnterpriseDate(28, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE28));
      resource.setEnterpriseDate(29, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE29));
      resource.setEnterpriseDate(30, resourceVarData.getTimestamp (id, RESOURCE_ENTERPRISE_DATE30));
            
      resource.setEnterpriseDuration(1, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION1), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION1_UNITS))));
      resource.setEnterpriseDuration(2, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION2), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION2_UNITS))));
      resource.setEnterpriseDuration(3, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION3), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION3_UNITS))));
      resource.setEnterpriseDuration(4, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION4), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION4_UNITS))));
      resource.setEnterpriseDuration(5, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION5), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION5_UNITS))));
      resource.setEnterpriseDuration(6, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION6), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION6_UNITS))));
      resource.setEnterpriseDuration(7, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION7), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION7_UNITS))));
      resource.setEnterpriseDuration(8, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION8), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION8_UNITS))));
      resource.setEnterpriseDuration(9, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION9), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION9_UNITS))));
      resource.setEnterpriseDuration(10, MPPUtility.getAdjustedDuration (m_file, resourceVarData.getInt(id, RESOURCE_ENTERPRISE_DURATION10), MPPUtility.getDurationTimeUnits(resourceVarData.getShort(id, RESOURCE_ENTERPRISE_DURATION10_UNITS))));
      
      resource.setEnterpriseNumber(1, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER1)));      
      resource.setEnterpriseNumber(2, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER2)));
      resource.setEnterpriseNumber(3, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER3)));
      resource.setEnterpriseNumber(4, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER4)));
      resource.setEnterpriseNumber(5, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER5)));
      resource.setEnterpriseNumber(6, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER6)));
      resource.setEnterpriseNumber(7, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER7)));
      resource.setEnterpriseNumber(8, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER8)));
      resource.setEnterpriseNumber(9, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER9)));
      resource.setEnterpriseNumber(10, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER10)));      
      resource.setEnterpriseNumber(11, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER11)));      
      resource.setEnterpriseNumber(12, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER12)));
      resource.setEnterpriseNumber(13, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER13)));
      resource.setEnterpriseNumber(14, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER14)));
      resource.setEnterpriseNumber(15, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER15)));
      resource.setEnterpriseNumber(16, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER16)));
      resource.setEnterpriseNumber(17, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER17)));
      resource.setEnterpriseNumber(18, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER18)));
      resource.setEnterpriseNumber(19, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER19)));
      resource.setEnterpriseNumber(20, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER20)));      
      resource.setEnterpriseNumber(21, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER21)));
      resource.setEnterpriseNumber(22, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER22)));
      resource.setEnterpriseNumber(23, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER23)));
      resource.setEnterpriseNumber(24, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER24)));
      resource.setEnterpriseNumber(25, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER25)));
      resource.setEnterpriseNumber(26, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER26)));
      resource.setEnterpriseNumber(27, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER27)));
      resource.setEnterpriseNumber(28, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER28)));
      resource.setEnterpriseNumber(29, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER29)));      
      resource.setEnterpriseNumber(30, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER30)));
      resource.setEnterpriseNumber(31, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER31)));
      resource.setEnterpriseNumber(32, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER32)));
      resource.setEnterpriseNumber(33, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER33)));
      resource.setEnterpriseNumber(34, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER34)));
      resource.setEnterpriseNumber(35, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER35)));
      resource.setEnterpriseNumber(36, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER36)));
      resource.setEnterpriseNumber(37, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER37)));
      resource.setEnterpriseNumber(38, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER38)));
      resource.setEnterpriseNumber(39, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER39)));
      resource.setEnterpriseNumber(40, NumberUtility.getDouble (resourceVarData.getDouble(id, RESOURCE_ENTERPRISE_NUMBER40)));
            
      resource.setEnterpriseText(1, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT1));
      resource.setEnterpriseText(2, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT2));
      resource.setEnterpriseText(3, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT3));
      resource.setEnterpriseText(4, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT4));
      resource.setEnterpriseText(5, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT5));
      resource.setEnterpriseText(6, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT6));
      resource.setEnterpriseText(7, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT7));
      resource.setEnterpriseText(8, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT8));
      resource.setEnterpriseText(9, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT9));
      resource.setEnterpriseText(10, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT10));
      resource.setEnterpriseText(11, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT11));
      resource.setEnterpriseText(12, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT12));
      resource.setEnterpriseText(13, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT13));
      resource.setEnterpriseText(14, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT14));
      resource.setEnterpriseText(15, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT15));
      resource.setEnterpriseText(16, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT16));
      resource.setEnterpriseText(17, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT17));
      resource.setEnterpriseText(18, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT18));
      resource.setEnterpriseText(19, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT19));
      resource.setEnterpriseText(20, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT20));
      resource.setEnterpriseText(21, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT21));
      resource.setEnterpriseText(22, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT22));
      resource.setEnterpriseText(23, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT23));
      resource.setEnterpriseText(24, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT24));
      resource.setEnterpriseText(25, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT25));
      resource.setEnterpriseText(26, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT26));
      resource.setEnterpriseText(27, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT27));
      resource.setEnterpriseText(28, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT28));
      resource.setEnterpriseText(29, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT29));      
      resource.setEnterpriseText(30, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT30));
      resource.setEnterpriseText(31, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT31));
      resource.setEnterpriseText(32, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT32));
      resource.setEnterpriseText(33, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT33));
      resource.setEnterpriseText(34, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT34));
      resource.setEnterpriseText(35, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT35));
      resource.setEnterpriseText(36, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT36));
      resource.setEnterpriseText(37, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT37));
      resource.setEnterpriseText(38, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT38));
      resource.setEnterpriseText(39, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT39));
      resource.setEnterpriseText(40, resourceVarData.getUnicodeString (id, RESOURCE_ENTERPRISE_TEXT40));

      if (metaData2 != null)
      {
         int bits = MPPUtility.getInt(metaData2, 16);                  
         resource.set(ResourceField.ENTERPRISE_FLAG1, new Boolean((bits & 0x00010) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG2, new Boolean((bits & 0x00020) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG3, new Boolean((bits & 0x00040) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG4, new Boolean((bits & 0x00080) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG5, new Boolean((bits & 0x00100) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG6, new Boolean((bits & 0x00200) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG7, new Boolean((bits & 0x00400) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG8, new Boolean((bits & 0x00800) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG9, new Boolean((bits & 0x01000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG10, new Boolean((bits & 0x02000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG11, new Boolean((bits & 0x04000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG12, new Boolean((bits & 0x08000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG13, new Boolean((bits & 0x10000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG14, new Boolean((bits & 0x20000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG15, new Boolean((bits & 0x40000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG16, new Boolean((bits & 0x80000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG17, new Boolean((bits & 0x100000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG18, new Boolean((bits & 0x200000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG19, new Boolean((bits & 0x400000) != 0));
         resource.set(ResourceField.ENTERPRISE_FLAG20, new Boolean((bits & 0x800000) != 0));         
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
      
      for (Task currentTask : externalTasks)
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
    * @throws java.io.IOException
    */
   private void processConstraintData ()
      throws IOException
   {
      DirectoryEntry consDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndCons");
      FixedMeta consFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedMeta"))), 10);
      FixedData consFixedData = new FixedData (consFixedMeta, 20, new DocumentInputStream (((DocumentEntry)consDir.getEntry("FixedData"))));

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
    * @throws java.io.IOException
    */
   private void processResourceData ()
      throws IOException
   {
      DirectoryEntry rscDir = (DirectoryEntry)m_projectDir.getEntry ("TBkndRsc");
      VarMeta rscVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("VarMeta"))));
      Var2Data rscVarData = new Var2Data (rscVarMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Var2Data"))));
      FixedMeta rscFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedMeta"))), 37);
      FixedData rscFixedData = new FixedData (rscFixedMeta, new DocumentInputStream (((DocumentEntry)rscDir.getEntry("FixedData"))));
      FixedMeta rscFixed2Meta = new FixedMeta (new DocumentInputStream (((DocumentEntry)rscDir.getEntry("Fixed2Meta"))), 49);
      //System.out.println(rscVarMeta);
      //System.out.println(rscVarData);
      //System.out.println(rscFixed2Meta);
      
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
         resource.setOutlineCode1(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE1)), OUTLINECODE_DATA));
         resource.setOutlineCode2(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE2)), OUTLINECODE_DATA));
         resource.setOutlineCode3(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE3)), OUTLINECODE_DATA));
         resource.setOutlineCode4(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE4)), OUTLINECODE_DATA));
         resource.setOutlineCode5(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE5)), OUTLINECODE_DATA));
         resource.setOutlineCode6(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE6)), OUTLINECODE_DATA));
         resource.setOutlineCode7(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE7)), OUTLINECODE_DATA));
         resource.setOutlineCode8(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE8)), OUTLINECODE_DATA));
         resource.setOutlineCode9(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE9)), OUTLINECODE_DATA));
         resource.setOutlineCode10(m_outlineCodeVarData.getUnicodeString(new Integer(rscVarData.getInt (id, 2, RESOURCE_OUTLINECODE10)), OUTLINECODE_DATA));
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
         byte[] metaData2 = rscFixed2Meta.getByteArrayValue(offset.intValue());
         processResourceEnterpriseColumns(id, resource, rscVarData, metaData2);
         
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
      VarMeta assnVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("VarMeta"))));
      Var2Data assnVarData = new Var2Data (assnVarMeta, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("Var2Data"))));
      FixedMeta assnFixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedMeta"))), 34);
      FixedData assnFixedData = new FixedData (142, new DocumentInputStream (((DocumentEntry)assnDir.getEntry("FixedData"))));
      
      //System.out.println(assnVarMeta);
      //System.out.println(assnVarData);
      
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
            byte[] incompleteWork = assnVarData.getByteArray(varDataId, INCOMPLETE_WORK);
            if (task.getSplits() != null && task.getSplits().isEmpty())
            {
               byte[] completeWork = assnVarData.getByteArray(varDataId, COMPLETE_WORK);
               processSplitData(task, completeWork, incompleteWork);
            }

            Integer resourceID = new Integer(MPPUtility.getInt (data, 8));
            Resource resource = m_file.getResourceByUniqueID (resourceID);

            if (resource != null)
            {
               //System.out.println("Task: " + task.getName());
               //System.out.println("Resource: " + resource.getName());
               //System.out.println(MPPUtility.hexdump(data, false, 16, ""));
               //System.out.println(MPPUtility.hexdump(incompleteWork, false, 16, ""));
               //System.out.println(MPPUtility.hexdump(meta, false, 16, ""));               

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
    * @throws java.io.IOException
    */
   private void processViewData ()
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CV_iew");
      VarMeta viewVarMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data viewVarData = new Var2Data (viewVarMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
      FixedMeta fixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedMeta"))), 10);
      FixedData fixedData = new FixedData (138, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData"))));
      
      int items = fixedMeta.getItemCount();
      View view;
      ViewFactory factory = new ViewFactory12 ();
   
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
   private void processTableData ()
      throws IOException
   {
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CTable");
      
      VarMeta varMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
      int tableCount = varMeta.getUniqueIdentifierSet().size();
      DocumentInputStream is = new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData")));
      int fixedItemSize = is.available()/tableCount;      
      FixedData fixedData = new FixedData (fixedItemSize, is);
      
      //System.out.println(varMeta);
      //System.out.println(varData);
      //System.out.println(fixedData);
      
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
      FixedMeta fixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedMeta"))), 10);
      FixedData fixedData = new FixedData (fixedMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData"))));
      VarMeta varMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));

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
   private void processSavedViewState ()
      throws IOException
   {           
      DirectoryEntry dir = (DirectoryEntry)m_viewDir.getEntry ("CEdl");
      VarMeta varMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
   
      //System.out.println(varMeta);
      //System.out.println(varData);
      
      ViewStateReader reader = new ViewStateReader12();
      reader.process(m_file, varData);
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
      FixedMeta fixedMeta = new FixedMeta (new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedMeta"))), 10);
      FixedData fixedData = new FixedData (fixedMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("FixedData"))));
      VarMeta varMeta = new VarMeta12 (new DocumentInputStream (((DocumentEntry)dir.getEntry("VarMeta"))));
      Var2Data varData = new Var2Data (varMeta, new DocumentInputStream (((DocumentEntry)dir.getEntry("Var2Data"))));
   
//      System.out.println(fixedMeta);
//      System.out.println(fixedData);
//      System.out.println(varMeta);
//      System.out.println(varData);   
      
      GroupReader reader = new GroupReader12();
      reader.process(m_file, fixedData, varData, m_fontBases);
      
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
//
//   private static final int[][] UNKNOWN_TASK_DATA = new int[][]
//   {
//      {36, 4},
//      {42, 18},
//      {134, 14},
//      {156, 4},
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
   private Map<Integer, Integer> m_parentTasks;
   
   /**
    * Calendar data types.
    */
   private static final Integer CALENDAR_NAME = new Integer (1);
   private static final Integer CALENDAR_DATA = new Integer (8);

   /**
    * Task data types.
    */

   //
   // MPP12 verified
   //
   private static final Integer TASK_NAME = new Integer (14);

   private static final Integer TASK_NUMBER1 = new Integer (87);
   private static final Integer TASK_NUMBER2 = new Integer (88);
   private static final Integer TASK_NUMBER3 = new Integer (89);
   private static final Integer TASK_NUMBER4 = new Integer (90);
   private static final Integer TASK_NUMBER5 = new Integer (91);
   
   private static final Integer TASK_COST1 = new Integer (106);
   private static final Integer TASK_COST2 = new Integer (107);
   private static final Integer TASK_COST3 = new Integer (108);   
   
   private static final Integer TASK_CONTACT = new Integer (112);
   
   private static final Integer TASK_COST4 = new Integer (258);
   private static final Integer TASK_COST5 = new Integer (259);
   private static final Integer TASK_COST6 = new Integer (260);
   private static final Integer TASK_COST7 = new Integer (261);
   private static final Integer TASK_COST8 = new Integer (262);
   private static final Integer TASK_COST9 = new Integer (263);
   private static final Integer TASK_COST10 = new Integer (264);

   private static final Integer TASK_DATE1 = new Integer (265);
   private static final Integer TASK_DATE2 = new Integer (266);
   private static final Integer TASK_DATE3 = new Integer (267);
   private static final Integer TASK_DATE4 = new Integer (268);
   private static final Integer TASK_DATE5 = new Integer (269);
   private static final Integer TASK_DATE6 = new Integer (270);
   private static final Integer TASK_DATE7 = new Integer (271);
   private static final Integer TASK_DATE8 = new Integer (272);
   private static final Integer TASK_DATE9 = new Integer (273);
   private static final Integer TASK_DATE10 = new Integer (274);

   private static final Integer TASK_NUMBER6 = new Integer (302);
   private static final Integer TASK_NUMBER7 = new Integer (303);
   private static final Integer TASK_NUMBER8 = new Integer (304);
   private static final Integer TASK_NUMBER9 = new Integer (305);
   private static final Integer TASK_NUMBER10 = new Integer (306);
   
   
   private static final Integer TASK_DURATION1 = new Integer (103);
   private static final Integer TASK_DURATION1_UNITS = new Integer (183);
   private static final Integer TASK_DURATION2 = new Integer (104);
   private static final Integer TASK_DURATION2_UNITS = new Integer (184);
   private static final Integer TASK_DURATION3 = new Integer (105);
   private static final Integer TASK_DURATION3_UNITS = new Integer (185);
   private static final Integer TASK_DURATION4 = new Integer (275);
   private static final Integer TASK_DURATION4_UNITS = new Integer (337);
   private static final Integer TASK_DURATION5 = new Integer (276);
   private static final Integer TASK_DURATION5_UNITS = new Integer (187);
   private static final Integer TASK_DURATION6 = new Integer (277);
   private static final Integer TASK_DURATION6_UNITS = new Integer (188);
   private static final Integer TASK_DURATION7 = new Integer (278);
   private static final Integer TASK_DURATION7_UNITS = new Integer (189);
   private static final Integer TASK_DURATION8 = new Integer (279);
   private static final Integer TASK_DURATION8_UNITS = new Integer (190);
   private static final Integer TASK_DURATION9 = new Integer (280);
   private static final Integer TASK_DURATION9_UNITS = new Integer (191);
   private static final Integer TASK_DURATION10 = new Integer (281);
   private static final Integer TASK_DURATION10_UNITS = new Integer (192);


   private static final Integer TASK_START1 = new Integer (52);
   private static final Integer TASK_FINISH1 = new Integer (53);
   private static final Integer TASK_START2 = new Integer (55);
   private static final Integer TASK_FINISH2 = new Integer (56);
   private static final Integer TASK_START3 = new Integer (58);
   private static final Integer TASK_FINISH3 = new Integer (59);
   private static final Integer TASK_START4 = new Integer (61);
   private static final Integer TASK_FINISH4 = new Integer (62);
   private static final Integer TASK_START5 = new Integer (64);
   private static final Integer TASK_FINISH5 = new Integer (65);
   private static final Integer TASK_START6 = new Integer (282);
   private static final Integer TASK_FINISH6 = new Integer (283);
   private static final Integer TASK_START7 = new Integer (284);
   private static final Integer TASK_FINISH7 = new Integer (285);
   private static final Integer TASK_START8 = new Integer (286);
   private static final Integer TASK_FINISH8 = new Integer (287);
   private static final Integer TASK_START9 = new Integer (288);
   private static final Integer TASK_FINISH9 = new Integer (289);
   private static final Integer TASK_START10 = new Integer (290);
   private static final Integer TASK_FINISH10 = new Integer (291);

   private static final Integer TASK_HYPERLINK = new Integer (215);
   
   private static final Integer TASK_NOTES = new Integer (15);

   private static final Integer TASK_NUMBER11 = new Integer (307);
   private static final Integer TASK_NUMBER12 = new Integer (308);
   private static final Integer TASK_NUMBER13 = new Integer (309);
   private static final Integer TASK_NUMBER14 = new Integer (310);
   private static final Integer TASK_NUMBER15 = new Integer (311);
   private static final Integer TASK_NUMBER16 = new Integer (312);
   private static final Integer TASK_NUMBER17 = new Integer (313);
   private static final Integer TASK_NUMBER18 = new Integer (314);
   private static final Integer TASK_NUMBER19 = new Integer (315);
   private static final Integer TASK_NUMBER20 = new Integer (316);

   private static final Integer TASK_TEXT1 = new Integer (51);
   private static final Integer TASK_TEXT2 = new Integer (54);
   private static final Integer TASK_TEXT3 = new Integer (57);
   private static final Integer TASK_TEXT4 = new Integer (60);
   private static final Integer TASK_TEXT5 = new Integer (63);
   private static final Integer TASK_TEXT6 = new Integer (66);
   private static final Integer TASK_TEXT7 = new Integer (67);
   private static final Integer TASK_TEXT8 = new Integer (68);
   private static final Integer TASK_TEXT9 = new Integer (69);
   private static final Integer TASK_TEXT10 = new Integer (70);

   private static final Integer TASK_TEXT11 = new Integer (317);
   private static final Integer TASK_TEXT12 = new Integer (318);
   private static final Integer TASK_TEXT13 = new Integer (319);
   private static final Integer TASK_TEXT14 = new Integer (320);
   private static final Integer TASK_TEXT15 = new Integer (321);
   private static final Integer TASK_TEXT16 = new Integer (322);
   private static final Integer TASK_TEXT17 = new Integer (323);
   private static final Integer TASK_TEXT18 = new Integer (324);
   private static final Integer TASK_TEXT19 = new Integer (325);
   private static final Integer TASK_TEXT20 = new Integer (326);
   private static final Integer TASK_TEXT21 = new Integer (327);
   private static final Integer TASK_TEXT22 = new Integer (328);
   private static final Integer TASK_TEXT23 = new Integer (329);
   private static final Integer TASK_TEXT24 = new Integer (330);
   private static final Integer TASK_TEXT25 = new Integer (331);
   private static final Integer TASK_TEXT26 = new Integer (332);
   private static final Integer TASK_TEXT27 = new Integer (333);
   private static final Integer TASK_TEXT28 = new Integer (334);
   private static final Integer TASK_TEXT29 = new Integer (335);
   private static final Integer TASK_TEXT30 = new Integer (336);

   private static final Integer TASK_SUBPROJECT_TASKS_UNIQUEID_OFFSET = new Integer (458);

   private static final Integer TASK_OUTLINECODE1 = new Integer (417);
   private static final Integer TASK_OUTLINECODE2 = new Integer (419);
   private static final Integer TASK_OUTLINECODE3 = new Integer (421);
   private static final Integer TASK_OUTLINECODE4 = new Integer (423);
   private static final Integer TASK_OUTLINECODE5 = new Integer (425);
   private static final Integer TASK_OUTLINECODE6 = new Integer (427);
   private static final Integer TASK_OUTLINECODE7 = new Integer (429);
   private static final Integer TASK_OUTLINECODE8 = new Integer (431);
   private static final Integer TASK_OUTLINECODE9 = new Integer (433);
   private static final Integer TASK_OUTLINECODE10 = new Integer (435);

   private static final Integer TASK_EXTERNAL_TASK_ID = new Integer (255);
   
   private static final Integer TASK_ENTERPRISE_COST1 = new Integer(599);
   private static final Integer TASK_ENTERPRISE_COST2 = new Integer(600);
   private static final Integer TASK_ENTERPRISE_COST3 = new Integer(601);
   private static final Integer TASK_ENTERPRISE_COST4 = new Integer(602);
   private static final Integer TASK_ENTERPRISE_COST5 = new Integer(603);
   private static final Integer TASK_ENTERPRISE_COST6 = new Integer(604);
   private static final Integer TASK_ENTERPRISE_COST7 = new Integer(605);
   private static final Integer TASK_ENTERPRISE_COST8 = new Integer(606);
   private static final Integer TASK_ENTERPRISE_COST9 = new Integer(607);
   private static final Integer TASK_ENTERPRISE_COST10 = new Integer(608);
   
   private static final Integer TASK_ENTERPRISE_DATE1 = new Integer(609);
   private static final Integer TASK_ENTERPRISE_DATE2 = new Integer(610);
   private static final Integer TASK_ENTERPRISE_DATE3 = new Integer(611);
   private static final Integer TASK_ENTERPRISE_DATE4 = new Integer(612);
   private static final Integer TASK_ENTERPRISE_DATE5 = new Integer(613);
   private static final Integer TASK_ENTERPRISE_DATE6 = new Integer(614);
   private static final Integer TASK_ENTERPRISE_DATE7 = new Integer(615);
   private static final Integer TASK_ENTERPRISE_DATE8 = new Integer(616);
   private static final Integer TASK_ENTERPRISE_DATE9 = new Integer(617);
   private static final Integer TASK_ENTERPRISE_DATE10 = new Integer(618);
   private static final Integer TASK_ENTERPRISE_DATE11 = new Integer(619);
   private static final Integer TASK_ENTERPRISE_DATE12 = new Integer(620);
   private static final Integer TASK_ENTERPRISE_DATE13 = new Integer(621);
   private static final Integer TASK_ENTERPRISE_DATE14 = new Integer(622);
   private static final Integer TASK_ENTERPRISE_DATE15 = new Integer(623);
   private static final Integer TASK_ENTERPRISE_DATE16 = new Integer(624);
   private static final Integer TASK_ENTERPRISE_DATE17 = new Integer(625);
   private static final Integer TASK_ENTERPRISE_DATE18 = new Integer(626);
   private static final Integer TASK_ENTERPRISE_DATE19 = new Integer(627);
   private static final Integer TASK_ENTERPRISE_DATE20 = new Integer(628);
   private static final Integer TASK_ENTERPRISE_DATE21 = new Integer(629);
   private static final Integer TASK_ENTERPRISE_DATE22 = new Integer(630);
   private static final Integer TASK_ENTERPRISE_DATE23 = new Integer(631);
   private static final Integer TASK_ENTERPRISE_DATE24 = new Integer(632);
   private static final Integer TASK_ENTERPRISE_DATE25 = new Integer(633);
   private static final Integer TASK_ENTERPRISE_DATE26 = new Integer(634);
   private static final Integer TASK_ENTERPRISE_DATE27 = new Integer(635);
   private static final Integer TASK_ENTERPRISE_DATE28 = new Integer(636);
   private static final Integer TASK_ENTERPRISE_DATE29 = new Integer(637);
   private static final Integer TASK_ENTERPRISE_DATE30 = new Integer(638);
   
   private static final Integer TASK_ENTERPRISE_DURATION1 = new Integer(639);
   private static final Integer TASK_ENTERPRISE_DURATION2 = new Integer(640);
   private static final Integer TASK_ENTERPRISE_DURATION3 = new Integer(641);
   private static final Integer TASK_ENTERPRISE_DURATION4 = new Integer(642);
   private static final Integer TASK_ENTERPRISE_DURATION5 = new Integer(643);
   private static final Integer TASK_ENTERPRISE_DURATION6 = new Integer(644);
   private static final Integer TASK_ENTERPRISE_DURATION7 = new Integer(645);
   private static final Integer TASK_ENTERPRISE_DURATION8 = new Integer(646);
   private static final Integer TASK_ENTERPRISE_DURATION9 = new Integer(647);
   private static final Integer TASK_ENTERPRISE_DURATION10 = new Integer(648);
   
   private static final Integer TASK_ENTERPRISE_DURATION1_UNITS = new Integer(649);
   private static final Integer TASK_ENTERPRISE_DURATION2_UNITS = new Integer(650);
   private static final Integer TASK_ENTERPRISE_DURATION3_UNITS = new Integer(651);
   private static final Integer TASK_ENTERPRISE_DURATION4_UNITS = new Integer(652);
   private static final Integer TASK_ENTERPRISE_DURATION5_UNITS = new Integer(653);
   private static final Integer TASK_ENTERPRISE_DURATION6_UNITS = new Integer(654);
   private static final Integer TASK_ENTERPRISE_DURATION7_UNITS = new Integer(655);
   private static final Integer TASK_ENTERPRISE_DURATION8_UNITS = new Integer(656);
   private static final Integer TASK_ENTERPRISE_DURATION9_UNITS = new Integer(657);
   private static final Integer TASK_ENTERPRISE_DURATION10_UNITS = new Integer(658);
   
   private static final Integer TASK_ENTERPRISE_NUMBER1 = new Integer(699);
   private static final Integer TASK_ENTERPRISE_NUMBER2 = new Integer(700);
   private static final Integer TASK_ENTERPRISE_NUMBER3 = new Integer(701);
   private static final Integer TASK_ENTERPRISE_NUMBER4 = new Integer(702);
   private static final Integer TASK_ENTERPRISE_NUMBER5 = new Integer(703);
   private static final Integer TASK_ENTERPRISE_NUMBER6 = new Integer(704);
   private static final Integer TASK_ENTERPRISE_NUMBER7 = new Integer(705);
   private static final Integer TASK_ENTERPRISE_NUMBER8 = new Integer(706);
   private static final Integer TASK_ENTERPRISE_NUMBER9 = new Integer(707);
   private static final Integer TASK_ENTERPRISE_NUMBER10 = new Integer(708);
   private static final Integer TASK_ENTERPRISE_NUMBER11 = new Integer(709);
   private static final Integer TASK_ENTERPRISE_NUMBER12 = new Integer(710);
   private static final Integer TASK_ENTERPRISE_NUMBER13 = new Integer(711);
   private static final Integer TASK_ENTERPRISE_NUMBER14 = new Integer(712);
   private static final Integer TASK_ENTERPRISE_NUMBER15 = new Integer(713);
   private static final Integer TASK_ENTERPRISE_NUMBER16 = new Integer(714);
   private static final Integer TASK_ENTERPRISE_NUMBER17 = new Integer(715);
   private static final Integer TASK_ENTERPRISE_NUMBER18 = new Integer(716);
   private static final Integer TASK_ENTERPRISE_NUMBER19 = new Integer(717);
   private static final Integer TASK_ENTERPRISE_NUMBER20 = new Integer(718);
   private static final Integer TASK_ENTERPRISE_NUMBER21 = new Integer(719);
   private static final Integer TASK_ENTERPRISE_NUMBER22 = new Integer(720);
   private static final Integer TASK_ENTERPRISE_NUMBER23 = new Integer(721);
   private static final Integer TASK_ENTERPRISE_NUMBER24 = new Integer(722);
   private static final Integer TASK_ENTERPRISE_NUMBER25 = new Integer(723);
   private static final Integer TASK_ENTERPRISE_NUMBER26 = new Integer(724);
   private static final Integer TASK_ENTERPRISE_NUMBER27 = new Integer(725);
   private static final Integer TASK_ENTERPRISE_NUMBER28 = new Integer(726);
   private static final Integer TASK_ENTERPRISE_NUMBER29 = new Integer(727);
   private static final Integer TASK_ENTERPRISE_NUMBER30 = new Integer(728);
   private static final Integer TASK_ENTERPRISE_NUMBER31 = new Integer(729);
   private static final Integer TASK_ENTERPRISE_NUMBER32 = new Integer(730);
   private static final Integer TASK_ENTERPRISE_NUMBER33 = new Integer(731);
   private static final Integer TASK_ENTERPRISE_NUMBER34 = new Integer(732);
   private static final Integer TASK_ENTERPRISE_NUMBER35 = new Integer(733);
   private static final Integer TASK_ENTERPRISE_NUMBER36 = new Integer(734);
   private static final Integer TASK_ENTERPRISE_NUMBER37 = new Integer(735);
   private static final Integer TASK_ENTERPRISE_NUMBER38 = new Integer(736);
   private static final Integer TASK_ENTERPRISE_NUMBER39 = new Integer(737);
   private static final Integer TASK_ENTERPRISE_NUMBER40 = new Integer(738);
   
   private static final Integer TASK_ENTERPRISE_TEXT1 = new Integer(799);
   private static final Integer TASK_ENTERPRISE_TEXT2 = new Integer(800);
   private static final Integer TASK_ENTERPRISE_TEXT3 = new Integer(801);
   private static final Integer TASK_ENTERPRISE_TEXT4 = new Integer(802);
   private static final Integer TASK_ENTERPRISE_TEXT5 = new Integer(803);
   private static final Integer TASK_ENTERPRISE_TEXT6 = new Integer(804);
   private static final Integer TASK_ENTERPRISE_TEXT7 = new Integer(805);
   private static final Integer TASK_ENTERPRISE_TEXT8 = new Integer(806);
   private static final Integer TASK_ENTERPRISE_TEXT9 = new Integer(807);
   private static final Integer TASK_ENTERPRISE_TEXT10 = new Integer(808);
   private static final Integer TASK_ENTERPRISE_TEXT11 = new Integer(809);
   private static final Integer TASK_ENTERPRISE_TEXT12 = new Integer(810);
   private static final Integer TASK_ENTERPRISE_TEXT13 = new Integer(811);
   private static final Integer TASK_ENTERPRISE_TEXT14 = new Integer(812);
   private static final Integer TASK_ENTERPRISE_TEXT15 = new Integer(813);
   private static final Integer TASK_ENTERPRISE_TEXT16 = new Integer(814);
   private static final Integer TASK_ENTERPRISE_TEXT17 = new Integer(815);
   private static final Integer TASK_ENTERPRISE_TEXT18 = new Integer(816);
   private static final Integer TASK_ENTERPRISE_TEXT19 = new Integer(817);
   private static final Integer TASK_ENTERPRISE_TEXT20 = new Integer(818);
   private static final Integer TASK_ENTERPRISE_TEXT21 = new Integer(819);
   private static final Integer TASK_ENTERPRISE_TEXT22 = new Integer(820);
   private static final Integer TASK_ENTERPRISE_TEXT23 = new Integer(821);
   private static final Integer TASK_ENTERPRISE_TEXT24 = new Integer(822);
   private static final Integer TASK_ENTERPRISE_TEXT25 = new Integer(823);
   private static final Integer TASK_ENTERPRISE_TEXT26 = new Integer(824);
   private static final Integer TASK_ENTERPRISE_TEXT27 = new Integer(825);
   private static final Integer TASK_ENTERPRISE_TEXT28 = new Integer(826);
   private static final Integer TASK_ENTERPRISE_TEXT29 = new Integer(827);
   private static final Integer TASK_ENTERPRISE_TEXT30 = new Integer(828);
   private static final Integer TASK_ENTERPRISE_TEXT31 = new Integer(829);
   private static final Integer TASK_ENTERPRISE_TEXT32 = new Integer(830);
   private static final Integer TASK_ENTERPRISE_TEXT33 = new Integer(831);
   private static final Integer TASK_ENTERPRISE_TEXT34 = new Integer(832);
   private static final Integer TASK_ENTERPRISE_TEXT35 = new Integer(833);
   private static final Integer TASK_ENTERPRISE_TEXT36 = new Integer(834);
   private static final Integer TASK_ENTERPRISE_TEXT37 = new Integer(835);
   private static final Integer TASK_ENTERPRISE_TEXT38 = new Integer(836);
   private static final Integer TASK_ENTERPRISE_TEXT39 = new Integer(837);
   private static final Integer TASK_ENTERPRISE_TEXT40 = new Integer(838);

   
   //
   // Unverified
   //
   private static final Integer TASK_ACTUAL_OVERTIME_WORK = new Integer (3);
   private static final Integer TASK_REMAINING_OVERTIME_WORK = new Integer (4);
   private static final Integer TASK_OVERTIME_COST = new Integer (5);
   private static final Integer TASK_ACTUAL_OVERTIME_COST = new Integer (6);
   private static final Integer TASK_REMAINING_OVERTIME_COST = new Integer (7);   
   private static final Integer TASK_SUBPROJECTTASKID = new Integer (9);
   private static final Integer TASK_WBS = new Integer (10);
 
   

   /**
    * Resource data types.
    */   
   
   //
   // MPP12 verified
   //
   private static final Integer RESOURCE_NAME = new Integer (1);
   private static final Integer RESOURCE_INITIALS = new Integer (2);
   private static final Integer RESOURCE_GROUP = new Integer (3);   
   private static final Integer RESOURCE_CODE = new Integer (10);

   private static final Integer RESOURCE_COST1 = new Integer (123);
   private static final Integer RESOURCE_COST2 = new Integer (124);
   private static final Integer RESOURCE_COST3 = new Integer (125);
   private static final Integer RESOURCE_COST4 = new Integer (166);
   private static final Integer RESOURCE_COST5 = new Integer (167);
   private static final Integer RESOURCE_COST6 = new Integer (168);
   private static final Integer RESOURCE_COST7 = new Integer (169);
   private static final Integer RESOURCE_COST8 = new Integer (170);
   private static final Integer RESOURCE_COST9 = new Integer (171);
   private static final Integer RESOURCE_COST10 = new Integer (172);

   private static final Integer RESOURCE_EMAIL = new Integer (35);

   private static final Integer RESOURCE_DATE1 = new Integer (173);
   private static final Integer RESOURCE_DATE2 = new Integer (174);
   private static final Integer RESOURCE_DATE3 = new Integer (175);
   private static final Integer RESOURCE_DATE4 = new Integer (176);
   private static final Integer RESOURCE_DATE5 = new Integer (177);
   private static final Integer RESOURCE_DATE6 = new Integer (178);
   private static final Integer RESOURCE_DATE7 = new Integer (179);
   private static final Integer RESOURCE_DATE8 = new Integer (180);
   private static final Integer RESOURCE_DATE9 = new Integer (181);
   private static final Integer RESOURCE_DATE10 = new Integer (182);

   private static final Integer RESOURCE_START1 = new Integer (102);
   private static final Integer RESOURCE_START2 = new Integer (103);
   private static final Integer RESOURCE_START3 = new Integer (104);
   private static final Integer RESOURCE_START4 = new Integer (105);
   private static final Integer RESOURCE_START5 = new Integer (106);
   private static final Integer RESOURCE_START6 = new Integer (220);
   private static final Integer RESOURCE_START7 = new Integer (221);
   private static final Integer RESOURCE_START8 = new Integer (222);
   private static final Integer RESOURCE_START9 = new Integer (223);
   private static final Integer RESOURCE_START10 = new Integer (224);

   private static final Integer RESOURCE_FINISH1 = new Integer (107);
   private static final Integer RESOURCE_FINISH2 = new Integer (108);
   private static final Integer RESOURCE_FINISH3 = new Integer (109);
   private static final Integer RESOURCE_FINISH4 = new Integer (110);
   private static final Integer RESOURCE_FINISH5 = new Integer (111);
   private static final Integer RESOURCE_FINISH6 = new Integer (190);
   private static final Integer RESOURCE_FINISH7 = new Integer (191);
   private static final Integer RESOURCE_FINISH8 = new Integer (192);
   private static final Integer RESOURCE_FINISH9 = new Integer (193);
   private static final Integer RESOURCE_FINISH10 = new Integer (194);

   private static final Integer RESOURCE_OUTLINECODE1 = new Integer (279);
   private static final Integer RESOURCE_OUTLINECODE2 = new Integer (281);
   private static final Integer RESOURCE_OUTLINECODE3 = new Integer (283);
   private static final Integer RESOURCE_OUTLINECODE4 = new Integer (285);
   private static final Integer RESOURCE_OUTLINECODE5 = new Integer (287);
   private static final Integer RESOURCE_OUTLINECODE6 = new Integer (289);
   private static final Integer RESOURCE_OUTLINECODE7 = new Integer (291);
   private static final Integer RESOURCE_OUTLINECODE8 = new Integer (293);
   private static final Integer RESOURCE_OUTLINECODE9 = new Integer (295);
   private static final Integer RESOURCE_OUTLINECODE10 = new Integer (297);

   private static final Integer RESOURCE_DURATION1 = new Integer (117);
   private static final Integer RESOURCE_DURATION2 = new Integer (118);
   private static final Integer RESOURCE_DURATION3 = new Integer (119);
   private static final Integer RESOURCE_DURATION4 = new Integer (183);
   private static final Integer RESOURCE_DURATION5 = new Integer (184);
   private static final Integer RESOURCE_DURATION6 = new Integer (185);
   private static final Integer RESOURCE_DURATION7 = new Integer (186);
   private static final Integer RESOURCE_DURATION8 = new Integer (187);
   private static final Integer RESOURCE_DURATION9 = new Integer (188);
   private static final Integer RESOURCE_DURATION10 = new Integer (189);

   private static final Integer RESOURCE_DURATION1_UNITS = new Integer (120);
   private static final Integer RESOURCE_DURATION2_UNITS = new Integer (121);
   private static final Integer RESOURCE_DURATION3_UNITS = new Integer (122);
   private static final Integer RESOURCE_DURATION4_UNITS = new Integer (245);
   private static final Integer RESOURCE_DURATION5_UNITS = new Integer (246);
   private static final Integer RESOURCE_DURATION6_UNITS = new Integer (247);
   private static final Integer RESOURCE_DURATION7_UNITS = new Integer (248);
   private static final Integer RESOURCE_DURATION8_UNITS = new Integer (249);
   private static final Integer RESOURCE_DURATION9_UNITS = new Integer (250);
   private static final Integer RESOURCE_DURATION10_UNITS = new Integer (251);

   private static final Integer RESOURCE_NUMBER1 = new Integer (112);
   private static final Integer RESOURCE_NUMBER2 = new Integer (113);
   private static final Integer RESOURCE_NUMBER3 = new Integer (114);
   private static final Integer RESOURCE_NUMBER4 = new Integer (115);
   private static final Integer RESOURCE_NUMBER5 = new Integer (116);
   private static final Integer RESOURCE_NUMBER6 = new Integer (205);
   private static final Integer RESOURCE_NUMBER7 = new Integer (206);
   private static final Integer RESOURCE_NUMBER8 = new Integer (207);
   private static final Integer RESOURCE_NUMBER9 = new Integer (208);
   private static final Integer RESOURCE_NUMBER10 = new Integer (209);
   private static final Integer RESOURCE_NUMBER11 = new Integer (210);
   private static final Integer RESOURCE_NUMBER12 = new Integer (211);
   private static final Integer RESOURCE_NUMBER13 = new Integer (212);
   private static final Integer RESOURCE_NUMBER14 = new Integer (213);
   private static final Integer RESOURCE_NUMBER15 = new Integer (214);
   private static final Integer RESOURCE_NUMBER16 = new Integer (215);
   private static final Integer RESOURCE_NUMBER17 = new Integer (216);
   private static final Integer RESOURCE_NUMBER18 = new Integer (217);
   private static final Integer RESOURCE_NUMBER19 = new Integer (218);
   private static final Integer RESOURCE_NUMBER20 = new Integer (219);

   private static final Integer RESOURCE_TEXT1 = new Integer (8);
   private static final Integer RESOURCE_TEXT2 = new Integer (9);
   private static final Integer RESOURCE_TEXT3 = new Integer (30);
   private static final Integer RESOURCE_TEXT4 = new Integer (31);
   private static final Integer RESOURCE_TEXT5 = new Integer (32);
   private static final Integer RESOURCE_TEXT6 = new Integer (97);
   private static final Integer RESOURCE_TEXT7 = new Integer (98);
   private static final Integer RESOURCE_TEXT8 = new Integer (99);
   private static final Integer RESOURCE_TEXT9 = new Integer (100);
   private static final Integer RESOURCE_TEXT10 = new Integer (101);
   private static final Integer RESOURCE_TEXT11 = new Integer (225);
   private static final Integer RESOURCE_TEXT12 = new Integer (226);
   private static final Integer RESOURCE_TEXT13 = new Integer (227);
   private static final Integer RESOURCE_TEXT14 = new Integer (228);
   private static final Integer RESOURCE_TEXT15 = new Integer (229);
   private static final Integer RESOURCE_TEXT16 = new Integer (230);
   private static final Integer RESOURCE_TEXT17 = new Integer (231);
   private static final Integer RESOURCE_TEXT18 = new Integer (232);
   private static final Integer RESOURCE_TEXT19 = new Integer (233);
   private static final Integer RESOURCE_TEXT20 = new Integer (234);
   private static final Integer RESOURCE_TEXT21 = new Integer (235);
   private static final Integer RESOURCE_TEXT22 = new Integer (236);
   private static final Integer RESOURCE_TEXT23 = new Integer (237);
   private static final Integer RESOURCE_TEXT24 = new Integer (238);
   private static final Integer RESOURCE_TEXT25 = new Integer (239);
   private static final Integer RESOURCE_TEXT26 = new Integer (240);
   private static final Integer RESOURCE_TEXT27 = new Integer (241);
   private static final Integer RESOURCE_TEXT28 = new Integer (242);
   private static final Integer RESOURCE_TEXT29 = new Integer (243);
   private static final Integer RESOURCE_TEXT30 = new Integer (244);

   private static final Integer RESOURCE_ENTERPRISE_COST1 = new Integer(446);
   private static final Integer RESOURCE_ENTERPRISE_COST2 = new Integer(447);
   private static final Integer RESOURCE_ENTERPRISE_COST3 = new Integer(448);
   private static final Integer RESOURCE_ENTERPRISE_COST4 = new Integer(449);
   private static final Integer RESOURCE_ENTERPRISE_COST5 = new Integer(450);
   private static final Integer RESOURCE_ENTERPRISE_COST6 = new Integer(451);
   private static final Integer RESOURCE_ENTERPRISE_COST7 = new Integer(452);
   private static final Integer RESOURCE_ENTERPRISE_COST8 = new Integer(453);
   private static final Integer RESOURCE_ENTERPRISE_COST9 = new Integer(454);
   private static final Integer RESOURCE_ENTERPRISE_COST10 = new Integer(455);
   
   private static final Integer RESOURCE_ENTERPRISE_DATE1 = new Integer(456);
   private static final Integer RESOURCE_ENTERPRISE_DATE2 = new Integer(457);
   private static final Integer RESOURCE_ENTERPRISE_DATE3 = new Integer(458);
   private static final Integer RESOURCE_ENTERPRISE_DATE4 = new Integer(459);
   private static final Integer RESOURCE_ENTERPRISE_DATE5 = new Integer(460);
   private static final Integer RESOURCE_ENTERPRISE_DATE6 = new Integer(461);
   private static final Integer RESOURCE_ENTERPRISE_DATE7 = new Integer(462);
   private static final Integer RESOURCE_ENTERPRISE_DATE8 = new Integer(463);
   private static final Integer RESOURCE_ENTERPRISE_DATE9 = new Integer(464);
   private static final Integer RESOURCE_ENTERPRISE_DATE10 = new Integer(465);
   private static final Integer RESOURCE_ENTERPRISE_DATE11 = new Integer(466);
   private static final Integer RESOURCE_ENTERPRISE_DATE12 = new Integer(467);
   private static final Integer RESOURCE_ENTERPRISE_DATE13 = new Integer(468);
   private static final Integer RESOURCE_ENTERPRISE_DATE14 = new Integer(469);
   private static final Integer RESOURCE_ENTERPRISE_DATE15 = new Integer(470);
   private static final Integer RESOURCE_ENTERPRISE_DATE16 = new Integer(471);
   private static final Integer RESOURCE_ENTERPRISE_DATE17 = new Integer(472);
   private static final Integer RESOURCE_ENTERPRISE_DATE18 = new Integer(473);
   private static final Integer RESOURCE_ENTERPRISE_DATE19 = new Integer(474);
   private static final Integer RESOURCE_ENTERPRISE_DATE20 = new Integer(475);
   private static final Integer RESOURCE_ENTERPRISE_DATE21 = new Integer(476);
   private static final Integer RESOURCE_ENTERPRISE_DATE22 = new Integer(477);
   private static final Integer RESOURCE_ENTERPRISE_DATE23 = new Integer(478);
   private static final Integer RESOURCE_ENTERPRISE_DATE24 = new Integer(479);
   private static final Integer RESOURCE_ENTERPRISE_DATE25 = new Integer(480);
   private static final Integer RESOURCE_ENTERPRISE_DATE26 = new Integer(481);
   private static final Integer RESOURCE_ENTERPRISE_DATE27 = new Integer(482);
   private static final Integer RESOURCE_ENTERPRISE_DATE28 = new Integer(483);
   private static final Integer RESOURCE_ENTERPRISE_DATE29 = new Integer(484);
   private static final Integer RESOURCE_ENTERPRISE_DATE30 = new Integer(485);
   
   private static final Integer RESOURCE_ENTERPRISE_DURATION1 = new Integer(486);
   private static final Integer RESOURCE_ENTERPRISE_DURATION2 = new Integer(487);
   private static final Integer RESOURCE_ENTERPRISE_DURATION3 = new Integer(488);
   private static final Integer RESOURCE_ENTERPRISE_DURATION4 = new Integer(489);
   private static final Integer RESOURCE_ENTERPRISE_DURATION5 = new Integer(490);
   private static final Integer RESOURCE_ENTERPRISE_DURATION6 = new Integer(491);
   private static final Integer RESOURCE_ENTERPRISE_DURATION7 = new Integer(492);
   private static final Integer RESOURCE_ENTERPRISE_DURATION8 = new Integer(493);
   private static final Integer RESOURCE_ENTERPRISE_DURATION9 = new Integer(494);
   private static final Integer RESOURCE_ENTERPRISE_DURATION10 = new Integer(495);
   
   private static final Integer RESOURCE_ENTERPRISE_DURATION1_UNITS = new Integer(496);
   private static final Integer RESOURCE_ENTERPRISE_DURATION2_UNITS = new Integer(497);
   private static final Integer RESOURCE_ENTERPRISE_DURATION3_UNITS = new Integer(498);
   private static final Integer RESOURCE_ENTERPRISE_DURATION4_UNITS = new Integer(499);
   private static final Integer RESOURCE_ENTERPRISE_DURATION5_UNITS = new Integer(500);
   private static final Integer RESOURCE_ENTERPRISE_DURATION6_UNITS = new Integer(501);
   private static final Integer RESOURCE_ENTERPRISE_DURATION7_UNITS = new Integer(502);
   private static final Integer RESOURCE_ENTERPRISE_DURATION8_UNITS = new Integer(503);
   private static final Integer RESOURCE_ENTERPRISE_DURATION9_UNITS = new Integer(504);
   private static final Integer RESOURCE_ENTERPRISE_DURATION10_UNITS = new Integer(505);
   
   private static final Integer RESOURCE_ENTERPRISE_NUMBER1 = new Integer(546);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER2 = new Integer(547);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER3 = new Integer(548);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER4 = new Integer(549);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER5 = new Integer(550);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER6 = new Integer(551);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER7 = new Integer(552);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER8 = new Integer(553);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER9 = new Integer(554);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER10 = new Integer(555);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER11 = new Integer(556);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER12 = new Integer(557);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER13 = new Integer(558);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER14 = new Integer(559);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER15 = new Integer(560);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER16 = new Integer(561);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER17 = new Integer(562);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER18 = new Integer(563);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER19 = new Integer(564);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER20 = new Integer(565);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER21 = new Integer(566);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER22 = new Integer(567);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER23 = new Integer(568);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER24 = new Integer(569);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER25 = new Integer(570);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER26 = new Integer(571);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER27 = new Integer(572);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER28 = new Integer(573);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER29 = new Integer(574);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER30 = new Integer(575);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER31 = new Integer(576);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER32 = new Integer(577);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER33 = new Integer(578);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER34 = new Integer(579);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER35 = new Integer(580);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER36 = new Integer(581);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER37 = new Integer(582);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER38 = new Integer(583);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER39 = new Integer(584);
   private static final Integer RESOURCE_ENTERPRISE_NUMBER40 = new Integer(585);
   
   private static final Integer RESOURCE_ENTERPRISE_TEXT1 = new Integer(646);
   private static final Integer RESOURCE_ENTERPRISE_TEXT2 = new Integer(647);
   private static final Integer RESOURCE_ENTERPRISE_TEXT3 = new Integer(648);
   private static final Integer RESOURCE_ENTERPRISE_TEXT4 = new Integer(649);
   private static final Integer RESOURCE_ENTERPRISE_TEXT5 = new Integer(650);
   private static final Integer RESOURCE_ENTERPRISE_TEXT6 = new Integer(651);
   private static final Integer RESOURCE_ENTERPRISE_TEXT7 = new Integer(652);
   private static final Integer RESOURCE_ENTERPRISE_TEXT8 = new Integer(653);
   private static final Integer RESOURCE_ENTERPRISE_TEXT9 = new Integer(654);
   private static final Integer RESOURCE_ENTERPRISE_TEXT10 = new Integer(655);
   private static final Integer RESOURCE_ENTERPRISE_TEXT11 = new Integer(656);
   private static final Integer RESOURCE_ENTERPRISE_TEXT12 = new Integer(657);
   private static final Integer RESOURCE_ENTERPRISE_TEXT13 = new Integer(658);
   private static final Integer RESOURCE_ENTERPRISE_TEXT14 = new Integer(659);
   private static final Integer RESOURCE_ENTERPRISE_TEXT15 = new Integer(660);
   private static final Integer RESOURCE_ENTERPRISE_TEXT16 = new Integer(661);
   private static final Integer RESOURCE_ENTERPRISE_TEXT17 = new Integer(662);
   private static final Integer RESOURCE_ENTERPRISE_TEXT18 = new Integer(663);
   private static final Integer RESOURCE_ENTERPRISE_TEXT19 = new Integer(664);
   private static final Integer RESOURCE_ENTERPRISE_TEXT20 = new Integer(665);
   private static final Integer RESOURCE_ENTERPRISE_TEXT21 = new Integer(666);
   private static final Integer RESOURCE_ENTERPRISE_TEXT22 = new Integer(667);
   private static final Integer RESOURCE_ENTERPRISE_TEXT23 = new Integer(668);
   private static final Integer RESOURCE_ENTERPRISE_TEXT24 = new Integer(669);
   private static final Integer RESOURCE_ENTERPRISE_TEXT25 = new Integer(670);
   private static final Integer RESOURCE_ENTERPRISE_TEXT26 = new Integer(671);
   private static final Integer RESOURCE_ENTERPRISE_TEXT27 = new Integer(672);
   private static final Integer RESOURCE_ENTERPRISE_TEXT28 = new Integer(673);
   private static final Integer RESOURCE_ENTERPRISE_TEXT29 = new Integer(674);
   private static final Integer RESOURCE_ENTERPRISE_TEXT30 = new Integer(675);
   private static final Integer RESOURCE_ENTERPRISE_TEXT31 = new Integer(676);
   private static final Integer RESOURCE_ENTERPRISE_TEXT32 = new Integer(677);
   private static final Integer RESOURCE_ENTERPRISE_TEXT33 = new Integer(678);
   private static final Integer RESOURCE_ENTERPRISE_TEXT34 = new Integer(679);
   private static final Integer RESOURCE_ENTERPRISE_TEXT35 = new Integer(680);
   private static final Integer RESOURCE_ENTERPRISE_TEXT36 = new Integer(681);
   private static final Integer RESOURCE_ENTERPRISE_TEXT37 = new Integer(682);
   private static final Integer RESOURCE_ENTERPRISE_TEXT38 = new Integer(683);
   private static final Integer RESOURCE_ENTERPRISE_TEXT39 = new Integer(684);
   private static final Integer RESOURCE_ENTERPRISE_TEXT40 = new Integer(685);
   
   //
   // Unverified
   //
   private static final Integer RESOURCE_SUBPROJECTRESOURCEID = new Integer (102);
   private static final Integer RESOURCE_NOTES = new Integer (124);

   private static final Integer TABLE_COLUMN_DATA_STANDARD = new Integer (6);
   private static final Integer TABLE_COLUMN_DATA_ENTERPRISE = new Integer (7);   
   private static final Integer OUTLINECODE_DATA = new Integer (22);
   private static final Integer INCOMPLETE_WORK = new Integer(49);
   private static final Integer COMPLETE_WORK = new Integer(50);
   

   
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
