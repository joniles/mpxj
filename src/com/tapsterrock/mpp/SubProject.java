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
    * @param offset current offset in byte array
    * @return offset of next sub project data set
    */
   public int read (byte[] data, int offset)
   {
      int size;
      int originalOffset = offset;
      
      //
      // 28 byte block which contains the task unique ID
      //
      offset += 20;
      m_taskUniqueID = new Integer (MPPUtility.getInt(data, offset));
      offset +=8;
      
      //
      // There now follows two blocks in the same format,
      // an 18 byte header, followed by a string, then a unicode string.
      // The first block contains the full path, the second block
      // contains the file name.
      //
      
      //
      // First block header
      //
      offset += 18;
      
      //
      // String size as a 4 byte int
      //
      offset += 4;
      
      //
      // Full DOS path
      //
      m_dosFullPath = MPPUtility.getString(data, offset);
      offset += (m_dosFullPath.length()+1);
      
      //
      // 24 byte block
      //
      offset += 24;
      
      //
      // 4 byte block size
      //
      size = MPPUtility.getInt(data, offset);      
      offset +=4;
      if (size != 0)
      {
         //
         // 4 byte unicode string size in bytes
         //
         size = MPPUtility.getInt(data, offset);
         offset += 4;
         
         //
         // 2 byte data
         //
         offset += 2;
         
         //
         // Unicode string
         //
         m_fullPath = MPPUtility.getUnicodeString(data, offset, size);
         offset += size;
      }
      
      //
      // Second block header
      //
      offset += 18;
      
      //
      // String size as a 4 byte int
      //
      offset += 4;
      
      //
      // DOS file name
      //
      m_dosFileName = MPPUtility.getString(data, offset);
      offset += (m_dosFileName.length()+1);
      
      //
      // 24 byte block
      //
      offset += 24;
      
      //
      // 4 byte block size
      //
      size = MPPUtility.getInt(data, offset);
      offset +=4;
      
      if (size != 0)
      {
         //
         // 4 byte unicode string size in bytes
         //
         size = MPPUtility.getInt(data, offset);
         offset += 4;
   
         //
         // 2 byte data
         //
         offset += 2;
         
         //
         // Unicode string
         //
         m_fileName = MPPUtility.getUnicodeString(data, offset, size);
         offset += size;      
      }
      
      return (offset);
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
      return ("[SubProject taskUID=" + m_taskUniqueID + " path="+m_fullPath);
   }
   
   private Integer m_taskUniqueID;
   private String m_dosFullPath;
   private String m_fullPath;
   private String m_dosFileName;
   private String m_fileName;
}
