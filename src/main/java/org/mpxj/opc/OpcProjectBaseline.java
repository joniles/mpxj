/*
 * file:       OpcProjectBaseline.java
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
 * Represents a baseline in OPC when baselines for a project are listed,
 * and used to request export of a baseline as part of a project export.
 */
public class OpcProjectBaseline
{
   /**
    * Retrieve the baseline ID.
    *
    * @return baseline ID
    */
   public long getProjectBaselineId()
   {
      return m_projectBaselineId;
   }

   /**
    * Sets the baseline ID.
    *
    * @param projectBaselineId baseline ID
    */
   public void setProjectBaselineId(long projectBaselineId)
   {
      m_projectBaselineId = projectBaselineId;
   }

   /**
    * Retrieve the baseline name.
    *
    * @return baseline name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Sets the baseline name.
    *
    * @param name baseline name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   @Override public String toString()
   {
      return "[OpcProjectBaseline project BaselineId=" + m_projectBaselineId + ", name=" + m_name + "]";
   }

   private long m_projectBaselineId;
   private String m_name;
}
