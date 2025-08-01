/*
 * file:       ExportRequest.java
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

import java.util.List;

/**
 * Represents the JSON payload used to request a project export.
 */
class ExportRequest
{
   /**
    * Constructor.
    *
    * @param project project to export
    * @param baselines baselines to export
    * @param compressed true if the response should be a zip file
    */
   public ExportRequest(OpcProject project, List<OpcProjectBaseline> baselines, boolean compressed)
   {
      m_project = project;
      m_projectInfo.setProjectId(project.getProjectId());
      m_projectInfo.setBaselineProjectInfo(baselines.stream().map(b -> new ExportRequestBaseline(b.getProjectBaselineId())).toArray(ExportRequestBaseline[]::new));
      m_exportCompressedFile = compressed;
   }

   /**
    * Retrieve the flag indicating if the response should be a zip file.
    *
    * @return true if a zip file will be returned
    */
   public boolean getExportCompressedFile()
   {
      return m_exportCompressedFile;
   }

   /**
    * Retrieve the workspace ID of the project being exported.
    *
    * @return workspace ID
    */
   public long getWorkspaceId()
   {
      return m_project.getWorkspaceId();
   }

   /**
    * Retrieve an array of details for the projects being exported.
    * At present, we only export one project at a time.
    *
    * @return array of project details
    */
   public ExportRequestProject[] getProjectsInfo()
   {
      return new ExportRequestProject[]
      {
         m_projectInfo
      };
   }

   private final OpcProject m_project;
   private final boolean m_exportCompressedFile;
   private final ExportRequestProject m_projectInfo = new ExportRequestProject();
}
