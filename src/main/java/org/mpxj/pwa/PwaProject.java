/*
 * file:       PwaProject.java
 * author:     Jon Iles
 * date:       2025-08-19
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

package org.mpxj.pwa;

import java.util.UUID;

/**
 * Represents a PWA project. Returned when projects in PWA are listed,
 * and can be used when requesting a project export.
 */
public class PwaProject
{
   /**
    * Constructor.
    */
   public PwaProject()
   {

   }

   /**
    * Constructor.
    *
    * @param id project unique ID
    * @param name project name
    */
   public PwaProject(UUID id, String name)
   {
      m_projectId = id;
      m_projectName = name;
   }

   /**
    * Retrieve the project's unique ID.
    *
    * @return unique ID
    */
   public UUID getProjectId()
   {
      return m_projectId;
   }

   /**
    * Set the project's unique ID.
    *
    * @param projectId unique ID
    */
   public void setProjectId(UUID projectId)
   {
      m_projectId = projectId;
   }

   /**
    * Retrieve the project's name.
    *
    * @return project name
    */
   public String getProjectName()
   {
      return m_projectName;
   }

   /**
    * Set the project's name.
    *
    * @param projectName project name
    */
   public void setProjectName(String projectName)
   {
      m_projectName = projectName;
   }

   @Override public String toString()
   {
      return "[PwaProject projectId=" + m_projectId + ", projectName=" + m_projectName + "]";
   }

   private UUID m_projectId;
   private String m_projectName;
}
