/*
 * file:       CustomFieldValueItem.java
 * author:     Jari Niskala
 * copyright:  (c) Packwood Software 2008
 * date:       17/01/2008
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

package org.mpxj.mpp;

import java.util.UUID;

import org.mpxj.CustomFieldValueDataType;

/**
 * Instances of this type represent a possible value for a custom field that is
 * using value lists.
 */
public final class CustomFieldValueItem
{
   /**
    * Constructor.
    *
    * @param uniqueID item unique ID
    */
   public CustomFieldValueItem(Integer uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   /**
    * Get the unique id for this item.
    *
    * @return item unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Set the value of this item.
    *
    * @param value item value
    */
   public void setValue(Object value)
   {
      m_value = value;
   }

   /**
    * Get the value of this item.
    *
    * @return item value
    */
   public Object getValue()
   {
      return m_value;
   }

   /**
    * Set the description for this item.
    *
    * @param description item description
    */
   public void setDescription(String description)
   {
      m_description = description;
   }

   /**
    * Get the description for this item.
    *
    * @return item description
    */
   public String getDescription()
   {
      return m_description;
   }

   /**
    * Set an Unknown property for this item.
    *
    * @param unknown unknown data block
    */
   public void setUnknown(byte[] unknown)
   {
      m_unknown = unknown;
   }

   /**
    * Get an unknown property for this item.
    *
    * @return unknown data block
    */
   public byte[] getUnknown()
   {
      return m_unknown;
   }

   /**
    * Set the parent unique ID.
    *
    * @param id parent ID
    */
   public void setParentUniqueID(Integer id)
   {
      m_parentUniqueID = id;
   }

   /**
    * Retrieve the parent unique ID.
    *
    * @return parent IDs
    */
   public Integer getParentUniqueID()
   {
      return m_parentUniqueID;
   }

   /**
    * Retrieve the GUID for this value.
    *
    * @return value GUID
    */
   public UUID getGUID()
   {
      return m_guid;
   }

   /**
    * Set the GUID for this value.
    *
    * @param guid value GUID
    */
   public void setGUID(UUID guid)
   {
      m_guid = guid;
   }

   /**
    * Retrieve the value type.
    *
    * @return value type
    */
   public CustomFieldValueDataType getType()
   {
      return m_type;
   }

   /**
    * Set the value type.
    *
    * @param type value type
    */
   public void setType(CustomFieldValueDataType type)
   {
      m_type = type;
   }

   /**
    * Retrieve the collapsed flag.
    *
    * @return collapsed flag
    */
   public boolean getCollapsed()
   {
      return m_collapsed;
   }

   /**
    * Set the collapsed flag.
    *
    * @param collapsed collapsed flag
    */
   public void setCollapsed(boolean collapsed)
   {
      m_collapsed = collapsed;
   }

   @Override public String toString()
   {
      return String.format("[CustomFieldValueItem uniqueID=%d guid=%s parentId=%d value=%s description=%s]", m_uniqueID, m_guid, m_parentUniqueID, m_value, m_description);
   }

   private final Integer m_uniqueID;
   private UUID m_guid;
   private Object m_value;
   private String m_description;
   private byte[] m_unknown;
   private Integer m_parentUniqueID;
   private CustomFieldValueDataType m_type;
   private boolean m_collapsed;
}
