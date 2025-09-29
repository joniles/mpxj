/*
 * file:       WebServicesProject.java
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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a project exposed via the P6 Web Services API. Returned when projects in P6 are listed,
 * and used when requesting a project export.
 */
public class WebServicesProject
{
   /**
    * Retrieve the user-visible unique identifier for a project in P6.
    *
    * @return user-visible unique identifier
    */
   public String getId()
   {
      return m_id;
   }

   /**
    * Sets the user-visible unique identifier of a project in P6.
    *
    * @param id user-visible unique identifier
    */
   public void setId(String id)
   {
      m_id = id;
   }

   /**
    * Retrieves the project name.
    *
    * @return project name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Sets the project name.
    *
    * @param name project name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Retrieves the project unique ID.
    *
    * @return project unique ID
    */
   public Integer getObjectId()
   {
      return m_objectId;
   }

   /**
    * Sets the project unique ID.
    *
    * @param objectId project unique ID
    */
   public void setObjectId(Integer objectId)
   {
      m_objectId = objectId;
   }

   /**
    * Retrieves the project data date.
    *
    * @return project data date
    */
   public String getDataDate()
   {
      return m_dataDate;
   }

   /**
    * Sets the project data date.
    *
    * @param dataDate project data date
    */
   public void setDataDate(String dataDate)
   {
      m_dataDate = dataDate;
   }

   /**
    * Retrieves the unique ID of the project's current baseline.
    *
    * @return current baseline unique ID
    */
   public Integer getCurrentBaselineProjectObjectId()
   {
      return m_currentBaselineProjectObjectId;
   }

   /**
    * Sets the unique ID of the project's current baseline.
    *
    * @param currentBaselineProjectObjectId current baseline unique ID
    */
   public void setCurrentBaselineProjectObjectId(Integer currentBaselineProjectObjectId)
   {
      m_currentBaselineProjectObjectId = currentBaselineProjectObjectId;
   }

   @JsonProperty("Id") private String m_id;
   @JsonProperty("Name") private String m_name;
   @JsonProperty("ObjectId") private Integer m_objectId;
   @JsonProperty("DataDate") private String m_dataDate;
   @JsonProperty("CurrentBaselineProjectObjectId") private Integer m_currentBaselineProjectObjectId;
}
