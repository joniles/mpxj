/*
 * file:       UserConfiguredFieldContainer.java
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.sf.mpxj.common.Pair;
import net.sf.mpxj.mpp.UserConfiguredFieldValueItem;

/**
 * Container holding configuration details for all user configured fields.
 */
public class UserConfiguredFieldContainer implements Iterable<UserConfiguredField>
{
   /**
    * Retrieve configuration details for a given field.
    *
    * @param field required field
    * @return configuration detail
    * @deprecated use getOrCreate
    */
   @Deprecated public UserConfiguredField getUserConfiguredField(FieldType field)
   {
      return getOrCreate(field);
   }

   /**
    * Retrieve configuration details for a given field.
    * Return null if the field has not been configured.
    *
    * @param field target field type
    * @return field configuration, or null if not configured
    */
   public UserConfiguredField get(FieldType field)
   {
      return m_configMap.get(field);
   }

   /**
    * Retrieve configuration details for a given field,
    * create a new UserConfiguredField entry if one does not exist.
    *
    * @param field required field
    * @return configuration detail
    */
   public UserConfiguredField getOrCreate(FieldType field)
   {
      return m_configMap.computeIfAbsent(field, k -> new UserConfiguredField(field, this));
   }

   /**
    * Add a new UserConfiguredField field. Overwrite any previous UserConfiguredField definition.
    *
    * @param field field type
    * @return new UserConfiguredField instance
    */
   public UserConfiguredField add(FieldType field)
   {
      UserConfiguredField result = new UserConfiguredField(field, this);
      m_configMap.put(field, result);
      return result;
   }

   /**
    * Retrieve a field type from a particular entity using its alias.
    *
    * @param typeClass the type of entity we are interested in
    * @param alias the alias
    * @return the field type referred to be the alias, or null if not found
    * @deprecated use getFieldTypeByAlias
    */
   @Deprecated public FieldType getFieldByAlias(FieldTypeClass typeClass, String alias)
   {
      return getFieldTypeByAlias(typeClass, alias);
   }

   /**
    * Retrieve a field type from a particular entity using its alias.
    *
    * @param typeClass the type of entity we are interested in
    * @param alias the alias
    * @return the field type referred to be the alias, or null if not found
    */
   public FieldType getFieldTypeByAlias(FieldTypeClass typeClass, String alias)
   {
      return m_aliasMap.get(new Pair<>(typeClass, alias));
   }

   /**
    * Retrieve a list of user configured fields by type class.
    *
    * @param typeClass required type class
    * @return list of UserConfiguredField instances
    */
   public List<UserConfiguredField> getUserConfiguredFieldsByFieldTypeClass(FieldTypeClass typeClass)
   {
      return stream().filter(f -> f.getFieldType().getFieldTypeClass() == typeClass).collect(Collectors.toList());
   }

   /**
    * Return the number of user configured fields.
    *
    * @return number of user configured fields
    */
   public int size()
   {
      return m_configMap.values().size();
   }

   @Override public Iterator<UserConfiguredField> iterator()
   {
      return m_configMap.values().iterator();
   }

   /**
    * Retrieve a user configured field value item by its unique ID.
    *
    * @param uniqueID user configured field value unique ID
    * @return user configured field value item
    */
   public UserConfiguredFieldValueItem getUserConfiguredFieldValueItemByUniqueID(int uniqueID)
   {
      return m_valueMap.get(Integer.valueOf(uniqueID));
   }

   /**
    * Retrieve a user configured field value item by its guid.
    *
    * @param guid user configured field value guid
    * @return user configured field value item
    */
   public UserConfiguredFieldValueItem getUserConfiguredFieldValueItemByGuid(UUID guid)
   {
      return m_guidMap.get(guid);
   }

   /**
    * Add a value to the user configured field value index.
    *
    * @param item user configured field value item
    */
   public void registerValue(UserConfiguredFieldValueItem item)
   {
      m_valueMap.put(item.getUniqueID(), item);
      if (item.getGUID() != null)
      {
         m_guidMap.put(item.getGUID(), item);
      }
   }

   /**
    * Remove a value from the user configured field value index.
    *
    * @param item user configured field value item
    */
   public void deregisterValue(UserConfiguredFieldValueItem item)
   {
      m_valueMap.remove(item.getUniqueID());
      if (item.getGUID() != null)
      {
         m_guidMap.remove(item.getGUID());
      }
   }

   /**
    * When an alias for a field is added, index it here to allow lookup by alias and type.
    *
    * @param type field type
    * @param alias field alias
    */
   void registerAlias(FieldType type, String alias)
   {
      m_aliasMap.put(new Pair<>(type.getFieldTypeClass(), alias), type);
   }

   /**
    * Return a stream of UserConfiguredField instances.
    *
    * @return Stream instance
    */
   public Stream<UserConfiguredField> stream()
   {
      return StreamSupport.stream(spliterator(), false);
   }

   private final Map<FieldType, UserConfiguredField> m_configMap = new HashMap<>();
   private final Map<Integer, UserConfiguredFieldValueItem> m_valueMap = new HashMap<>();
   private final Map<UUID, UserConfiguredFieldValueItem> m_guidMap = new HashMap<>();
   private final Map<Pair<FieldTypeClass, String>, FieldType> m_aliasMap = new HashMap<>();
}
