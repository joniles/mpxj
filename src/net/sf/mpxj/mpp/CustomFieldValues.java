/*
 * file:       CustomFieldValues.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       19/03/2015
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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the set of values available for each custom field.
 */
class CustomFieldValues
{
   /**
    * Add a custom field value list item.
    * 
    * @param item CustomFieldValueItem instance
    */
   public void addItem(CustomFieldValueItem item)
   {
      m_items.put(item.getUniqueID(), item);
   }

   /**
    * Get the custom field value list item with the given unique ID.
    * 
    * @param uniqueID unique ID
    * @return CustomFieldValueItem instance
    */
   public CustomFieldValueItem getItem(Integer uniqueID)
   {
      return m_items.get(uniqueID);
   }

   /**
    * Custom field value list items.
    */
   private Map<Integer, CustomFieldValueItem> m_items = new HashMap<Integer, CustomFieldValueItem>();
}
