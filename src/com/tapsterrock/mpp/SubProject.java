/*
 * file:       SubProject.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       May 23, 2005
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

package com.tapsterrock.mpp;

/**
 * This class represents a sub project.
 */
public class SubProject
{
   /**
    * Method used to read the sub project details from a byte array.
    * 
    * @param data byte array 
    * @param taskUniqueIDOffset offset of task unique ID
    * @param filePathOffset offset of file path
    * @param fileNameOffset offset of file name
    */
   public void read (byte[] data, int taskUniqueIDOffset, int filePathOffset, int fileNameOffset)
   {
      m_taskUniqueID = new Integer(MPPUtility.getInt(data, taskUniqueIDOffset));

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
      m_dosFullPath = MPPUtility.getString(data, filePathOffset);
      filePathOffset += (m_dosFullPath.length()+1);
      
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
         m_fullPath = m_dosFullPath;
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
         m_fullPath = MPPUtility.getUnicodeString(data, filePathOffset, size);
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
      m_dosFileName = MPPUtility.getString(data, fileNameOffset);
      fileNameOffset += (m_dosFileName.length()+1);
      
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
         m_fileName = m_dosFileName;
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
         m_fileName = MPPUtility.getUnicodeString(data, fileNameOffset, size);
         fileNameOffset += size;      
      }      
   }

   /**
    * Retrieves the DOS file name
    * 
    * @return DOS file name
    */
   public String getDosFileName()
   {
      return (m_dosFileName);
   }
   
   /**
    * Retrieves the DOS full path.
    * 
    * @return DOS full path
    */
   public String getDosFullPath()
   {
      return (m_dosFullPath);
   }
   
   /**
    * Retrieve the file name.
    * 
    * @return file name
    */
   public String getFileName()
   {
      return (m_fileName);
   }
   
   /**
    * Retrieve the full path.
    * 
    * @return full path
    */
   public String getFullPath()
   {
      return (m_fullPath);
   }
   
   /**
    * Retrieve the unique ID of the task to which this subproject
    * relates.
    * 
    * @return task Unique ID
    */
   public Integer getTaskUniqueID()
   {
      return (m_taskUniqueID);
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString ()
   {
      return ("[SubProject taskUID=" + m_taskUniqueID + " path="+m_fullPath+"]");
   }
   
   private Integer m_taskUniqueID;
   private String m_dosFullPath;
   private String m_fullPath;
   private String m_dosFileName;
   private String m_fileName;
}
