/*
 * file:       ExportRequest.java
 * author:     Jon Iles
 * date:       2025-09-29
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

package org.mpxj.primavera.webservices;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the JSON payload used to request a project export.
 */
class ExportRequest
{
   /**
    * Sets the file type to export.
    *
    * @param fileType file type
    */
   public void setFileType(String fileType)
   {
      m_fileType = fileType;
   }

   /**
    * Sets the unique ID of the project to export.
    *
    * @param projectObjectId project unique ID
    */
   public void setProjectObjectId(List<Integer> projectObjectId)
   {
      m_projectObjectId = projectObjectId;
   }

   @JsonProperty("FileType") private String m_fileType;
   @JsonProperty("ProjectObjectId") private List<Integer> m_projectObjectId;
}
