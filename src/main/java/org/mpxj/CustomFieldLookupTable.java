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

package org.mpxj;

import java.util.UUID;

import org.mpxj.mpp.CustomFieldValueItem;

/**
 * Lookup table definition for a custom field.
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

   /**
    *
    * Retrieve the enterprise flag.
    *
    * @return enterprise flag
    */
   public boolean getEnterprise()
   {
      return m_enterprise;
   }

   /**
    * Set the enterprise flag.
    *
    * @param enterprise enterprise flag
    */
   public void setEnterprise(boolean enterprise)
   {
      m_enterprise = enterprise;
   }

   /**
    * Retrieve the show indent flag.
    *
    * @return show indent flag
    */
   public boolean getShowIndent()
   {
      return m_showIndent;
   }

   /**
    * Set the show indent flag.
    *
    * @param showIndent show indent flag
    */
   public void setShowIndent(boolean showIndent)
   {
      m_showIndent = showIndent;
   }

   /**
    * Retrieve the resource substitution enabled flag.
    *
    * @return resource substitution enabled flag
    */
   public boolean getResourceSubstitutionEnabled()
   {
      return m_resourceSubstitutionEnabled;
   }

   /**
    * Set the resource substitution enabled flag.
    *
    * @param resourceSubstitutionEnabled resource substitution enabled flag
    */
   public void setResourceSubstitutionEnabled(boolean resourceSubstitutionEnabled)
   {
      m_resourceSubstitutionEnabled = resourceSubstitutionEnabled;
   }

   /**
    * Retrieve the leaf only flag.
    *
    * @return leaf only flag
    */
   public boolean getLeafOnly()
   {
      return m_leafOnly;
   }

   /**
    * Set the leaf only flag.
    *
    * @param leafOnly leaf only flag
    */
   public void setLeafOnly(boolean leafOnly)
   {
      m_leafOnly = leafOnly;
   }

   /**
    * Retrieve the all levels required flag.
    *
    * @return all levels required flag
    */
   public boolean getAllLevelsRequired()
   {
      return m_allLevelsRequired;
   }

   /**
    * Set the all levels required flag.
    *
    * @param allLevelsRequired all levels required flag
    */
   public void setAllLevelsRequired(boolean allLevelsRequired)
   {
      m_allLevelsRequired = allLevelsRequired;
   }

   /**
    * Retrieve the only table values allowed flag.
    *
    * @return only table values allowed flag
    */
   public boolean getOnlyTableValuesAllowed()
   {
      return m_onlyTableValuesAllowed;
   }

   /**
    * Set the only table values allowed flag.
    *
    * @param onlyTableValuesAllowed only table values allowed flag
    */
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
