/*
 * file:       OpcProject.java
 * author:     Jon Iles
 * date:       2025-07-09
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

package org.mpxj.opc;

/**
 * Represents an OPC project. Returned when projects in OPC are listed,
 * and used when requesting a project export.
 */
public class OpcProject
{
   /**
    * Retrieve the project ID.
    *
    * @return project ID
    */
   public long getProjectId()
   {
      return m_projectId;
   }

   /**
    * Set the project ID.
    *
    * @param projectId project ID
    */
   public void setProjectId(long projectId)
   {
      m_projectId = projectId;
   }

   /**
    * Retrieve the workspace ID.
    *
    * @return workspace ID
    */
   public long getWorkspaceId()
   {
      return m_workspaceId;
   }

   /**
    * Set the workspace ID.
    *
    * @param workspaceId workspace ID
    */
   public void setWorkspaceId(long workspaceId)
   {
      m_workspaceId = workspaceId;
   }

   /**
    * Retrieve the project name.
    *
    * @return project name
    */
   public String getProjectName()
   {
      return m_projectName;
   }

   /**
    * Set the project name.
    *
    * @param projectName project name
    */
   public void setProjectName(String projectName)
   {
      this.m_projectName = projectName;
   }

   @Override public String toString()
   {
      return "[OpcProject projectId=" + m_projectId + ", workspaceId=" + m_workspaceId + ", projectName=" + m_projectName + "]";
   }

   private long m_projectId;
   private long m_workspaceId;
   private String m_projectName;
}
