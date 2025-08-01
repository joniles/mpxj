/*
 * file:       Workspace.java
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
 * Represents the JSON payload for an OPC workspace.
 */
class Workspace
{
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
    * Sets the workspace ID.
    *
    * @param workspaceId workspace ID
    */
   public void setWorkspaceId(long workspaceId)
   {
      m_workspaceId = workspaceId;
   }

   /**
    * Retrieves the workspace name.
    *
    * @return workspace name
    */
   public String getWorkspaceName()
   {
      return m_workspaceName;
   }

   /**
    * Sets the workspace name.
    *
    * @param workspaceName workspace name
    */
   public void setWorkspaceName(String workspaceName)
   {
      m_workspaceName = workspaceName;
   }

   private long m_workspaceId;
   private String m_workspaceName;
}
