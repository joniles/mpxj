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
import java.util.Iterator;
import java.util.Map;

import net.sf.mpxj.common.Pair;
import net.sf.mpxj.mpp.CustomFieldValueItem;

/**
 * Container holding configuration details for all custom fields.
 */
public class CustomFieldContainer implements Iterable<CustomField>
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
         result = new CustomField(field, this);
         m_configMap.put(field, result);
      }
      return result;
   }

   /**
    * Return the number of custom fields.
    *
    * @return number of custom fields
    */
   public int size()
   {
      return m_configMap.values().size();
   }

   @Override public Iterator<CustomField> iterator()
   {
      return m_configMap.values().iterator();
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
   public void registerValue(CustomFieldValueItem item)
   {
      m_valueMap.put(item.getUniqueID(), item);
   }

   /**
    * Remove a value from the custom field value index.
    *
    * @param item custom field value
    */
   public void deregisterValue(CustomFieldValueItem item)
   {
      m_valueMap.remove(item.getUniqueID());
   }

   /**
    * When an alias for a field is added, index it here to allow lookup by alias and type.
    *
    * @param type field type
    * @param alias field alias
    */
   void registerAlias(FieldType type, String alias)
   {
      m_aliasMap.put(new Pair<FieldTypeClass, String>(type.getFieldTypeClass(), alias), type);
   }

   /**
    * Retrieve a field from a particular entity using its alias.
    *
    * @param typeClass the type of entity we are interested in
    * @param alias the alias
    * @return the field type referred to be the alias, or null if not found
    */
   public FieldType getFieldByAlias(FieldTypeClass typeClass, String alias)
   {
      return m_aliasMap.get(new Pair<FieldTypeClass, String>(typeClass, alias));
   }

   /**
    * Because there seemingly is no deterministic method of mapping UDF ObjectIds from Primavera PM to FieldTypes,
    * the aliasValueMap will store UDF values to be used by something that knows what alias maps to which FieldType.
    * @author lsong
    * @param alias custom field alias
    * @param uid field container unique id
    * @param value field value
    */
   public void registerAliasValue(String alias, Integer uid, Object value)
   {
      if (!m_aliasValueMap.containsKey(alias))
      {
         m_aliasValueMap.put(alias, new HashMap<Integer, Object>());
      }
      m_aliasValueMap.get(alias).put(uid, value);
   }

   /**
    * Importers with access to the ProjectFile containing this can determine how to
    * use the values in the UDFAssignmentTypes in UDF containers.
    * @author lsong
    * @param alias custom field alias
    * @param uid field container unique id
    * @return field value
    */
   public Object getAliasValue(String alias, Integer uid)
   {
      if (m_aliasValueMap.containsKey(alias))
      {
         return m_aliasValueMap.get(alias).get(uid);
      }
      return null;
   }

   private Map<FieldType, CustomField> m_configMap = new HashMap<FieldType, CustomField>();
   private Map<Integer, CustomFieldValueItem> m_valueMap = new HashMap<Integer, CustomFieldValueItem>();
   private Map<Pair<FieldTypeClass, String>, FieldType> m_aliasMap = new HashMap<Pair<FieldTypeClass, String>, FieldType>();
   private Map<String, Map<Integer, Object>> m_aliasValueMap = new HashMap<String, Map<Integer, Object>>();
}
