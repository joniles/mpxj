/*
 * file:       ExportRequestBaseline.java
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
 * Represents the JSON payload used when request the export of a baseline.
 */
class ExportRequestBaseline
{
   /**
    * Constructor.
    *
    * @param projectBaselineId ID of the baseline to export
    */
   public ExportRequestBaseline(long projectBaselineId)
   {
      m_projectBaselineId = projectBaselineId;
   }

   /**
    * Retrieve the ID of the baseline to export.
    *
    * @return baseline ID
    */
   public long getProjectBaselineId()
   {
      return m_projectBaselineId;
   }

   private final long m_projectBaselineId;
}
