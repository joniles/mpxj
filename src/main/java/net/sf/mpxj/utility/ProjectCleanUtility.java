/*
 * file:       ProjectCleanUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       07/02/2008
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

package net.sf.mpxj.utility;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.reader.UniversalProjectReader;

/**
 * This class allows the caller to replace the content of a schedule file
 * to make it anonymous, in such a way that the structure of the project
 * is maintained unchanged. The point of this exercise is to allow end
 * customers who use MPXJ functionality to submit problematic project files
 * obtain support. The fact that the structure of the file is maintained
 * unchanged means that it is likely that the problem with the file will
 * still be apparent. It also means that end users are more likely to
 * submit these files as, along with the removal of sensitive information, this
 * utility means that no user effort is required to modify the file
 * before it is sent to the organisation providing support.
 *
 * Note the following items are made anonymous:
 * - Task Names
 * - Resource Names
 * - Resource Initials
 * - Project Summary Data
 */
public class ProjectCleanUtility
{
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length != 2)
         {
            System.out.println("Usage: ProjectCleanUtility <input file name> <output file name>");
         }
         else
         {
            System.out.println("Clean started.");
            long start = System.currentTimeMillis();
            ProjectCleanUtility clean = new ProjectCleanUtility();
            clean.process(args[0], args[1]);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Clean completed in " + elapsed + "ms");
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Process a project file to make it anonymous.
    *
    * @param input input file name
    * @param output output file name
    */
   private void process(String input, String output) throws MPXJException, IOException
   {
      //
      // Extract the project data
      //
      m_project = new UniversalProjectReader().read(input);
      if (m_project.getProjectProperties().getFileType().equals("MPP"))
      {
         processMPP(input, output);
      }
      else
      {
         processFile(input, output);
      }
   }

   /**
    * Process a project file to make it anonymous.
    *
    * @param input input file name
    * @param output output file name
    */
   private void processFile(String input, String output) throws IOException
   {
      FileInputStream is = new FileInputStream(input);
      byte[] data = new byte[is.available()];
      is.read(data);
      is.close();

      processReplacements(data, m_project.getTasks(), false, false, TaskField.NAME);
      processReplacements(data, m_project.getResources(), false, false, ResourceField.NAME);

      FileOutputStream os = new FileOutputStream(output);
      os.write(data);
      os.flush();
      os.close();
   }

   /**
    * Process a project file to make it anonymous.
    *
    * @param input input file name
    * @param output output file name
    */
   private void processMPP(String input, String output) throws IOException
   {
      String varDataFileName;
      String projectDirName;
      int mppFileType = NumberHelper.getInt(m_project.getProjectProperties().getMppFileType());
      switch (mppFileType)
      {
         case 8:
         {
            projectDirName = "   1";
            varDataFileName = "FixDeferFix   0";
            break;
         }

         case 9:
         {
            projectDirName = "   19";
            varDataFileName = "Var2Data";
            break;
         }

         case 12:
         {
            projectDirName = "   112";
            varDataFileName = "Var2Data";
            break;
         }

         case 14:
         {
            projectDirName = "   114";
            varDataFileName = "Var2Data";
            break;
         }

         default:
         {
            throw new IllegalArgumentException("Unsupported file type " + mppFileType);
         }
      }

      //
      // Load the raw file
      //
      FileInputStream is = new FileInputStream(input);
      POIFSFileSystem fs = new POIFSFileSystem(is);
      is.close();

      //
      // Locate the root of the project file system
      //
      DirectoryEntry root = fs.getRoot();
      m_projectDir = (DirectoryEntry) root.getEntry(projectDirName);

      //
      // Process Tasks
      //
      processFile((DirectoryEntry) m_projectDir.getEntry("TBkndTask"), varDataFileName, m_project.getTasks(), true, TaskField.NAME);

      //
      // Process Resources
      //
      processFile((DirectoryEntry) m_projectDir.getEntry("TBkndRsc"), varDataFileName, m_project.getResources(), true, ResourceField.NAME, ResourceField.INITIALS);

      //
      // Process project properties
      //
      List<ProjectProperties> projectProperties = Arrays.asList(m_project.getProjectProperties());

      processFile(m_projectDir, "Props", projectProperties, true, ProjectField.PROJECT_TITLE);
      processFile(root, "\005SummaryInformation", projectProperties, false, ProjectField.PROJECT_TITLE, ProjectField.SUBJECT, ProjectField.AUTHOR, ProjectField.KEYWORDS, ProjectField.COMMENTS, ProjectField.LAST_AUTHOR);
      processFile(root, "\005DocumentSummaryInformation", projectProperties, false, ProjectField.MANAGER, ProjectField.COMPANY, ProjectField.CATEGORY);

      //
      // Write the replacement raw file
      //
      FileOutputStream os = new FileOutputStream(output);
      fs.writeFilesystem(os);
      os.flush();
      os.close();
      fs.close();
   }

   /**
    * Takes file contents represented as a byte array, finds specific field values within that file
    * and replaces them with anonymous text.
    *
    * @param data file data
    * @param items items to extract field values from
    * @param unicode true if replacing unicode text
    * @param nulTerminated true if a nul terminator should be included with the string
    * @param fields list of fields to extract
    */
   private void processReplacements(byte[] data, List<? extends FieldContainer> items, boolean unicode, boolean nulTerminated, FieldType... fields)
   {
      //
      // Build a map of the replacements required
      //
      Map<String, String> replacements = new HashMap<>();
      for (FieldContainer item : items)
      {
         for (FieldType field : fields)
         {
            mapText((String) item.getCachedValue(field), replacements);
         }
      }

      //
      // Populate a list of keys and sort into descending order of length
      //
      List<String> keys = new ArrayList<>(replacements.keySet());
      Collections.sort(keys, new Comparator<String>()
      {
         @Override public int compare(String o1, String o2)
         {
            return (o2.length() - o1.length());
         }
      });

      //
      // Perform the replacement
      //
      for (String findText : keys)
      {
         String replaceText = replacements.get(findText);
         replaceData(data, findText, replaceText, unicode, nulTerminated);
      }
   }

   /**
    * Extract a file from within an MPP file.
    *
    * @param parentDirectory parent directory
    * @param fileName file name
    * @return file data
    */
   private byte[] extractFile(DirectoryEntry parentDirectory, String fileName) throws IOException
   {
      DocumentEntry targetFile = (DocumentEntry) parentDirectory.getEntry(fileName);
      DocumentInputStream dis = new DocumentInputStream(targetFile);
      int dataSize = dis.available();
      byte[] data = new byte[dataSize];
      dis.read(data);
      dis.close();
      targetFile.delete();
      return data;
   }

   /**
    * Perform the replacement on a file within an MPP file.
    *
    * @param parentDirectory parent directory
    * @param fileName target file name
    * @param items items to extract field values from
    * @param unicode true if replacing unicode text
    * @param fields list of fields to extract
    */
   private void processFile(DirectoryEntry parentDirectory, String fileName, List<? extends FieldContainer> items, boolean unicode, FieldType... fields) throws IOException
   {
      byte[] data = extractFile(parentDirectory, fileName);
      processReplacements(data, items, unicode, true, fields);
      parentDirectory.createDocument(fileName, new ByteArrayInputStream(data));
   }

   /**
    * Converts plan text into anonymous text. Preserves upper case, lower case,
    * punctuation, whitespace and digits while making the text unreadable.
    *
    * @param oldText text to replace
    * @param replacements map of find/replace pairs
    */
   private void mapText(String oldText, Map<String, String> replacements)
   {
      char c2 = 0;
      if (oldText != null && oldText.length() != 0 && !replacements.containsKey(oldText))
      {
         StringBuilder newText = new StringBuilder(oldText.length());
         for (int loop = 0; loop < oldText.length(); loop++)
         {
            char c = oldText.charAt(loop);
            if (Character.isUpperCase(c))
            {
               newText.append('X');
            }
            else
            {
               if (Character.isLowerCase(c))
               {
                  newText.append('x');
               }
               else
               {
                  if (Character.isDigit(c))
                  {
                     newText.append('0');
                  }
                  else
                  {
                     if (Character.isLetter(c))
                     {
                        // Handle other codepages etc. If possible find a way to
                        // maintain the same code page as original.
                        // E.g. replace with a character from the same alphabet.
                        // This 'should' work for most cases
                        if (c2 == 0)
                        {
                           c2 = c;
                        }
                        newText.append(c2);
                     }
                     else
                     {
                        newText.append(c);
                     }
                  }
               }
            }
         }

         replacements.put(oldText, newText.toString());
      }
   }

   /**
    * For a given find/replace pair, iterate through the supplied block of data
    * and perform a find and replace.
    *
    * @param data data block
    * @param findText text to find
    * @param replaceText replacement text
    * @param unicode true if text is double byte
    * @param nulTerminated true if a nul terminator should be included with the string
    */
   private void replaceData(byte[] data, String findText, String replaceText, boolean unicode, boolean nulTerminated)
   {
      boolean replaced = false;
      byte[] findBytes = getBytes(findText, unicode, nulTerminated);
      byte[] replaceBytes = getBytes(replaceText, unicode, nulTerminated);
      int endIndex = data.length - findBytes.length;
      for (int index = 0; index <= endIndex; index++)
      {
         if (compareBytes(findBytes, data, index))
         {
            System.arraycopy(replaceBytes, 0, data, index, replaceBytes.length);
            index += replaceBytes.length;
            System.out.println(findText + " -> " + replaceText);
            replaced = true;
         }
      }
      if (!replaced)
      {
         System.out.println("Failed to find " + findText);
      }
   }

   /**
    * Convert a Java String instance into the equivalent array of single or
    * double bytes.
    *
    * @param value Java String instance representing text
    * @param unicode true if double byte characters are required
    * @param nulTerminated true if a nul terminator should be included with the string
    * @return byte array representing the supplied text
    */
   private byte[] getBytes(String value, boolean unicode, boolean nulTerminated)
   {
      byte[] result;
      if (unicode)
      {
         int start = 0;
         // Get the bytes in UTF-16
         byte[] bytes;

         try
         {
            bytes = value.getBytes("UTF-16");
         }
         catch (UnsupportedEncodingException e)
         {
            bytes = value.getBytes();
         }

         if (bytes.length > 2 && bytes[0] == -2 && bytes[1] == -1)
         {
            // Skip the unicode identifier
            start = 2;
         }
         result = new byte[bytes.length - start];
         for (int loop = start; loop < bytes.length - 1; loop += 2)
         {
            // Swap the order here
            result[loop - start] = bytes[loop + 1];
            result[loop + 1 - start] = bytes[loop];
         }
      }
      else
      {
         int length = nulTerminated ? value.length() + 1 : value.length();
         result = new byte[length];
         System.arraycopy(value.getBytes(), 0, result, 0, value.length());
      }
      return (result);
   }

   /**
    * Compare an array of bytes with a subsection of a larger array of bytes.
    *
    * @param lhs small array of bytes
    * @param rhs large array of bytes
    * @param rhsOffset offset into larger array of bytes
    * @return true if a match is found
    */
   private boolean compareBytes(byte[] lhs, byte[] rhs, int rhsOffset)
   {
      boolean result = true;
      for (int loop = 0; loop < lhs.length; loop++)
      {
         if (lhs[loop] != rhs[rhsOffset + loop])
         {
            result = false;
            break;
         }
      }
      return (result);
   }

   private ProjectFile m_project;
   private DirectoryEntry m_projectDir;
}
