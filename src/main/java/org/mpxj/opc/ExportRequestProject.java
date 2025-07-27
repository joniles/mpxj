/*
 * file:       ExportRequestProject.java
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
 * Represents the JSON payload used to request export of a specific project.
 */
class ExportRequestProject
{
   /**
    * Retrieve the ID of the project to export.
    *
    * @return project ID
    */
   public long getProjectId()
   {
      return m_projectId;
   }

   /**
    * Set the ID of the project to export.
    *
    * @param projectId project ID
    */
   public void setProjectId(long projectId)
   {
      m_projectId = projectId;
   }

   /**
    * Retrieve an array representing details of the baselines to be exported.
    *
    * @return array of baseline details
    */
   public ExportRequestBaseline[] getBaselineProjectInfo()
   {
      return m_baselineProjectInfo;
   }

   /**
    * Set the array of baselines to be exported.
    *
    * @param baselineProjectInfo baselines to be exported
    */
   public void setBaselineProjectInfo(ExportRequestBaseline[] baselineProjectInfo)
   {
      m_baselineProjectInfo = baselineProjectInfo;
   }

   private long m_projectId;
   private ExportRequestBaseline[] m_baselineProjectInfo;
}
