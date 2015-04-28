/*
 * file:       CustomFieldContainer.java
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

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.CustomFieldValueItem;

/**
 * Container holding configuration details for all custom fields.
 */
public class CustomFieldContainer
{
   /**
    * Retrieve configuration details for a given custom field.
    * 
    * @param field required custom field
    * @return configuration detail
    */
   public CustomField getCustomField(FieldType field)
   {
      CustomField result = m_configMap.get(field);
      if (result == null)
      {
         result = new CustomField(this);
         m_configMap.put(field, result);
      }
      return result;
   }

   /**
    * Retrieve a custom field value by its unique ID.
    * 
    * @param uniqueID custom field value unique ID
    * @return custom field value
    */
   public CustomFieldValueItem getCustomFieldValueItemByUniqueID(int uniqueID)
   {
      return m_valueMap.get(Integer.valueOf(uniqueID));
   }

   /**
    * Add a value to the custom field value index.
    * 
    * @param item custom field value
    */
   void addValue(CustomFieldValueItem item)
   {
      m_valueMap.put(item.getUniqueID(), item);
   }

   /**
    * Remove a value from the custom field value index.
    * 
    * @param item custom field value
    */
   void removeValue(CustomFieldValueItem item)
   {
      m_valueMap.remove(item.getUniqueID());
   }

   private Map<FieldType, CustomField> m_configMap = new HashMap<FieldType, CustomField>();
   private Map<Integer, CustomFieldValueItem> m_valueMap = new HashMap<Integer, CustomFieldValueItem>();
}
