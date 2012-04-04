/*
 * file:       SubProject.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
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

package net.sf.mpxj;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a sub project.
 */
public final class SubProject
{
   /**
    * Retrieves the DOS file name.
    *
    * @return DOS file name
    */
   public String getDosFileName()
   {
      return (m_dosFileName);
   }

   /**
    * Sets the DOS file name.
    *
    * @param dosFileName DOS file name
    */
   public void setDosFileName(String dosFileName)
   {
      m_dosFileName = dosFileName;
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
    * Sets the DOS full path.
    *
    * @param dosFullPath DOS full path
    */
   public void setDosFullPath(String dosFullPath)
   {
      m_dosFullPath = dosFullPath;
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
    * Sets the file name.
    *
    * @param fileName file name
    */
   public void setFileName(String fileName)
   {
      m_fileName = fileName;
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
    * Sets the full path.
    *
    * @param fullPath full path
    */
   public void setFullPath(String fullPath)
   {
      m_fullPath = fullPath;
   }

   /**
    * Retrieves the offset applied to task unique IDs
    * from the sub project.
    *
    * @return unique ID offset
    */
   public Integer getUniqueIDOffset()
   {
      return (m_uniqueIDOffset);
   }

   /**
    * Set the the offset applied to task unique IDs
    * from the sub project.
    *
    * @param uniqueIDOffset unique ID offset
    */
   public void setUniqueIDOffset(Integer uniqueIDOffset)
   {
      m_uniqueIDOffset = uniqueIDOffset;
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
    * Set the unique ID of the task to which this subproject relates.
    *
    * @param taskUniqueID task unique ID
    */
   public void setTaskUniqueID(Integer taskUniqueID)
   {
      m_taskUniqueID = taskUniqueID;
   }

   /**
    * Check to see if the given task is an external task from this subproject.
    *
    * @param taskUniqueID task unique ID
    * @return true if the task is external
    */
   public boolean isExternalTask(Integer taskUniqueID)
   {
      return m_externalTaskUniqueIDs.contains(taskUniqueID);
   }

   /**
    * This package-private method is used to add external task unique id.
    *
    * @param externalTaskUniqueID external task unique id
    */
   public void addExternalTaskUniqueID(Integer externalTaskUniqueID)
   {
      m_externalTaskUniqueIDs.add(externalTaskUniqueID);
   }

   /**
    * Retrieves all the external task unique ids for this project file.
    *
    * @return all sub project details
    */
   public List<Integer> getAllExternalTaskUniqueIDs()
   {
      return (m_externalTaskUniqueIDs);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return ("[SubProject taskUniqueID=" + m_taskUniqueID + " uniqueIDOffset=" + m_uniqueIDOffset + " path=" + m_fullPath + "]");
   }

   private Integer m_taskUniqueID;
   private Integer m_uniqueIDOffset;
   private List<Integer> m_externalTaskUniqueIDs = new LinkedList<Integer>();
   private String m_dosFullPath;
   private String m_fullPath;
   private String m_dosFileName;
   private String m_fileName;
}
