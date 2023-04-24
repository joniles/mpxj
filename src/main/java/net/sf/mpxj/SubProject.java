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

import java.util.UUID;

/**
 * This class represents a subproject.
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
    * Set the subproject GUID.
    *
    * @param guid subproject GUID
    */
   public void setProjectGUID(UUID guid)
   {
      m_projectGUID = guid;
   }

   /**
    * Retrieve the subproject GUID.
    *
    * @return subproject GUID
    */
   public UUID getProjectGUID()
   {
      return m_projectGUID;
   }

   @Override public String toString()
   {
      return ("[SubProject  path=" + m_fullPath + "]");
   }

   private String m_dosFullPath;
   private String m_fullPath;
   private String m_dosFileName;
   private String m_fileName;
   private UUID m_projectGUID;
}
