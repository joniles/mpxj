/*
 * file:       CustomFieldLookupTable.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-20015
 * date:       28/04/2015
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

import net.sf.mpxj.mpp.CustomFieldValueItem;

/**
 * Lookup table defined for a custom field.
 */
public class CustomFieldLookupTable extends ListWithCallbacks<CustomFieldValueItem>
{
   /**
    * Retrieve the lookup table GUID.
    *
    * @return lookup table GUID
    */
   public UUID getGUID()
   {
      return m_guid;
   }

   /**
    * Set the lookup table GUID.
    * 
    * @param guid lookup table GUID.
    */
   public void setGUID(UUID guid)
   {
      m_guid = guid;
   }
   
   public UUID getGuid()
   {
      return m_guid;
   }

   public void setGuid(UUID guid)
   {
      m_guid = guid;
   }

   public boolean getEnterprise()
   {
      return m_enterprise;
   }

   public void setEnterprise(boolean enterprise)
   {
      m_enterprise = enterprise;
   }

   public boolean getShowIndent()
   {
      return m_showIndent;
   }

   public void setShowIndent(boolean showIndent)
   {
      m_showIndent = showIndent;
   }

   public boolean getResourceSubstitutionEnabled()
   {
      return m_resourceSubstitutionEnabled;
   }

   public void setResourceSubstitutionEnabled(boolean resourceSubstitutionEnabled)
   {
      m_resourceSubstitutionEnabled = resourceSubstitutionEnabled;
   }

   public boolean getLeafOnly()
   {
      return m_leafOnly;
   }

   public void setLeafOnly(boolean leafOnly)
   {
      m_leafOnly = leafOnly;
   }

   public boolean getAllLevelsRequired()
   {
      return m_allLevelsRequired;
   }

   public void setAllLevelsRequired(boolean allLevelsRequired)
   {
      m_allLevelsRequired = allLevelsRequired;
   }

   public boolean getOnlyTableValuesAllowed()
   {
      return m_onlyTableValuesAllowed;
   }

   public void setOnlyTableValuesAllowed(boolean onlyTableValuesAllowed)
   {
      m_onlyTableValuesAllowed = onlyTableValuesAllowed;
   }

   private UUID m_guid;
   private boolean m_enterprise;
   private boolean m_showIndent = true;
   private boolean m_resourceSubstitutionEnabled;
   private boolean m_leafOnly;
   private boolean m_allLevelsRequired;
   private boolean m_onlyTableValuesAllowed = true;
}
