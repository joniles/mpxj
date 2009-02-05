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

package net.sf.mpxj.mpp;

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
   public void setValue(byte[] value)
   {
      m_value = value;
   }

   /**
    * Get the value of this item.
    * 
    * @return item value
    */
   public byte[] getValue()
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

   private Integer m_uniqueID;
   private byte[] m_value;
   private String m_description;
   private byte[] m_unknown;
}
