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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.sf.mpxj.common.AssignmentFieldLists;
import net.sf.mpxj.common.Pair;
import net.sf.mpxj.common.ResourceFieldLists;
import net.sf.mpxj.common.TaskFieldLists;
import net.sf.mpxj.mpp.CustomFieldValueItem;

/**
 * Container holding configuration details for all custom fields.
 */
public class CustomFieldContainer implements Iterable<CustomField>
{
   /**
    * Constructor.
    *
    * @param parent parent project file
    */
   public CustomFieldContainer(ProjectFile parent)
   {
      m_parent = parent;
   }

   /**
    * Retrieve configuration details for a given custom field.
    *
    * @param field required custom field
    * @return configuration detail
    * @deprecated use getOrCreate
    */
   @Deprecated public CustomField getCustomField(FieldType field)
   {
      return getOrCreate(field);
   }

   /**
    * Retrieve configuration details for a given custom field.
    * Return null if the field has not been configured.
    *
    * @param field target field type
    * @return field configuration, or null if not configured
    */
   public CustomField get(FieldType field)
   {
      return m_configMap.get(field);
   }

   /**
    * Retrieve configuration details for a given custom field,
    * create a new CustomField entry if one does not exist.
    *
    * @param field required custom field
    * @return configuration detail
    */
   public CustomField getOrCreate(FieldType field)
   {
      return m_configMap.computeIfAbsent(field, k -> new CustomField(field, this));
   }

   /**
    * Add a new custom field. Overwrite any previous CustomField definition.
    *
    * @param field field type
    * @return new CustomField instance
    */
   public CustomField add(FieldType field)
   {
      CustomField result = new CustomField(field, this);
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
    * Retrieve a list of custom fields by type class.
    *
    * @param typeClass required type class
    * @return list of CustomField instances
    */
   public List<CustomField> getCustomFieldsByFieldTypeClass(FieldTypeClass typeClass)
   {
      return stream().filter(f -> f.getFieldType().getFieldTypeClass() == typeClass).collect(Collectors.toList());
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
    * Retrieve a custom field value by its guid.
    *
    * @param guid custom field value guid
    * @return custom field value
    */
   public CustomFieldValueItem getCustomFieldValueItemByGuid(UUID guid)
   {
      return m_guidMap.get(guid);
   }

   /**
    * Add a value to the custom field value index.
    *
    * @param item custom field value
    */
   public void registerValue(CustomFieldValueItem item)
   {
      m_valueMap.put(item.getUniqueID(), item);
      if (item.getGUID() != null)
      {
         m_guidMap.put(item.getGUID(), item);
      }
   }

   /**
    * Remove a value from the custom field value index.
    *
    * @param item custom field value
    */
   public void deregisterValue(CustomFieldValueItem item)
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
    * Return a stream of CustomFields.
    *
    * @return Stream instance
    */
   public Stream<CustomField> stream()
   {
      return StreamSupport.stream(spliterator(), false);
   }

   /**
    * This method combines two sets of information: the list
    * of configured custom fields (from this class) plus
    * a lst of the custom fields which do not have configuration
    * but are in use in the schedule.
    *
    * @return set of FieldTypes representing configured and in use fields
    */
   public Set<FieldType> getConfiguredAndPopulatedCustomFieldTypes()
   {
      // Configured custom fields
      Set<FieldType> result = stream()
               .map(CustomField::getFieldType)
               .filter(Objects::nonNull)
               .collect(Collectors.toSet());

      /// Populated task custom fields
      Set<TaskField> populatedTaskFields = m_parent.getTasks().getPopulatedFields();
      populatedTaskFields.retainAll(TaskFieldLists.EXTENDED_FIELDS);
      result.addAll(populatedTaskFields);

      // Populated resource custom fields
      Set<ResourceField> populatedResourceFields = m_parent.getResources().getPopulatedFields();
      populatedResourceFields.retainAll(ResourceFieldLists.EXTENDED_FIELDS);
      result.addAll(populatedResourceFields);

      // Populated assignment custom fields
      Set<AssignmentField> populatedAssignmentFields = m_parent.getResourceAssignments().getPopulatedFields();
      populatedAssignmentFields.retainAll(AssignmentFieldLists.EXTENDED_FIELDS);
      result.addAll(populatedAssignmentFields);

      return result;
   }

   private final ProjectFile m_parent;
   private final Map<FieldType, CustomField> m_configMap = new HashMap<>();
   private final Map<Integer, CustomFieldValueItem> m_valueMap = new HashMap<>();
   private final Map<UUID, CustomFieldValueItem> m_guidMap = new HashMap<>();
   private final Map<Pair<FieldTypeClass, String>, FieldType> m_aliasMap = new HashMap<>();
}
