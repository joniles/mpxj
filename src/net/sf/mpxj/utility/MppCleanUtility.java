/*
 * file:       MppCleanUtility.java
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.mpp.MPPReader;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * This class allows the caller to replace the content of an MPP file
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
public class MppCleanUtility
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
            System.out.println("Usage: MppClean <input mpp file name> <output mpp file name>");
         }
         else
         {
            System.out.println("Clean started.");
            long start = System.currentTimeMillis();
            MppCleanUtility clean = new MppCleanUtility();
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
    * Process an MPP file to make it anonymous.
    *
    * @param input input file name
    * @param output output file name
    * @throws Exception
    */
   private void process(String input, String output) throws MPXJException, IOException
   {
      //
      // Extract the project data
      //
      MPPReader reader = new MPPReader();
      m_project = reader.read(input);

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
      Map<String, String> replacements = new HashMap<String, String>();
      for (Task task : m_project.getAllTasks())
      {
         mapText(task.getName(), replacements);
      }
      processReplacements(((DirectoryEntry) m_projectDir.getEntry("TBkndTask")), varDataFileName, replacements, true);

      //
      // Process Resources
      //
      replacements.clear();
      for (Resource resource : m_project.getAllResources())
      {
         mapText(resource.getName(), replacements);
         mapText(resource.getInitials(), replacements);
      }
      processReplacements((DirectoryEntry) m_projectDir.getEntry("TBkndRsc"), varDataFileName, replacements, true);

      //
      // Process project properties
      //
      replacements.clear();
      ProjectProperties properties = m_project.getProjectProperties();
      mapText(properties.getProjectTitle(), replacements);
      processReplacements(m_projectDir, "Props", replacements, true);

      replacements.clear();
      mapText(properties.getProjectTitle(), replacements);
      mapText(properties.getSubject(), replacements);
      mapText(properties.getAuthor(), replacements);
      mapText(properties.getKeywords(), replacements);
      mapText(properties.getComments(), replacements);
      processReplacements(root, "\005SummaryInformation", replacements, false);

      replacements.clear();
      mapText(properties.getManager(), replacements);
      mapText(properties.getCompany(), replacements);
      mapText(properties.getCategory(), replacements);
      processReplacements(root, "\005DocumentSummaryInformation", replacements, false);

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
    * Extracts a block of data from the MPP file, and iterates through the map
    * of find/replace pairs to make the data anonymous.
    *
    * @param parentDirectory parent directory object
    * @param fileName target file name
    * @param replacements find/replace data
    * @param unicode true for double byte text
    * @throws IOException
    */
   private void processReplacements(DirectoryEntry parentDirectory, String fileName, Map<String, String> replacements, boolean unicode) throws IOException
   {
      //
      // Populate a list of keys and sort into descending order of length
      //
      List<String> keys = new ArrayList<String>(replacements.keySet());
      Collections.sort(keys, new Comparator<String>()
      {
         @Override public int compare(String o1, String o2)
         {
            return (o2.length() - o1.length());
         }
      });

      //
      // Extract the raw file data
      //
      DocumentEntry targetFile = (DocumentEntry) parentDirectory.getEntry(fileName);
      DocumentInputStream dis = new DocumentInputStream(targetFile);
      int dataSize = dis.available();
      byte[] data = new byte[dataSize];
      dis.read(data);
      dis.close();

      //
      // Replace the text
      //
      for (String findText : keys)
      {
         String replaceText = replacements.get(findText);
         replaceData(data, findText, replaceText, unicode);
      }

      //
      // Remove the document entry
      //
      targetFile.delete();

      //
      // Replace it with a new one
      //
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
    */
   private void replaceData(byte[] data, String findText, String replaceText, boolean unicode)
   {
      boolean replaced = false;
      byte[] findBytes = getBytes(findText, unicode);
      byte[] replaceBytes = getBytes(replaceText, unicode);
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
    * @return byte array representing the supplied text
    */
   private byte[] getBytes(String value, boolean unicode)
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
         result = new byte[value.length() + 1];
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
