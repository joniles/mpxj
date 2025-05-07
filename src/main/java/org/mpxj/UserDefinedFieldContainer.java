/*
 * file:       UserDefinedFieldContainer.java
 * author:     Jon Iles
 * copyright:  (c) Timephased Ltd 2023
 * date:       2023-02-04
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Manages the collection of user defined fields belonging to a project.
 */
public class UserDefinedFieldContainer implements Collection<UserDefinedField>
{
   /**
    * Constructor.
    *
    * @param customFields custom fields container
    */
   public UserDefinedFieldContainer(CustomFieldContainer customFields)
   {
      m_customFields = customFields;
   }

   @Override public Iterator<UserDefinedField> iterator()
   {
      return m_uniqueIDMap.values().iterator();
   }

   @Override public Object[] toArray()
   {
      return m_uniqueIDMap.values().toArray();
   }

   @Override public <T> T[] toArray(T[] a)
   {
      return m_uniqueIDMap.values().toArray(a);
   }

   @Override public boolean add(UserDefinedField field)
   {
      Map<Integer, UserDefinedField> map = getMapFromFieldTypeClass(field);
      if (map == null)
      {
         return false;
      }

      map.put(field.getUniqueID(), field);
      addField(field);
      return true;
   }

   private Map<Integer, UserDefinedField> getMapFromFieldTypeClass(UserDefinedField field)
   {
      Map<Integer, UserDefinedField> map;

      switch (field.getFieldTypeClass())
      {
         case TASK:
         {
            map = m_taskFields;
            break;
         }

         case RESOURCE:
         {
            map = m_resourceFields;
            break;
         }

         case ASSIGNMENT:
         {
            map = m_assignmentFields;
            break;
         }

         case PROJECT:
         {
            map = m_projectFields;
            break;
         }

         default:
         {
            map = null;
            break;
         }
      }

      return map;
   }

   @Override public boolean remove(Object o)
   {
      m_customFields.remove((UserDefinedField) o);
      m_taskFields.remove(o);
      m_resourceFields.remove(o);
      m_assignmentFields.remove(o);
      m_projectFields.remove(o);
      return m_uniqueIDMap.remove(((UserDefinedField) o).getUniqueID(), o);
   }

   @Override public boolean containsAll(Collection<?> c)
   {
      return m_uniqueIDMap.values().containsAll(c);
   }

   @Override public boolean addAll(Collection<? extends UserDefinedField> c)
   {
      throw new UnsupportedOperationException();
   }

   @Override public boolean removeAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   @Override public boolean retainAll(Collection<?> c)
   {
      throw new UnsupportedOperationException();
   }

   @Override public void clear()
   {
      m_taskFields.clear();
      m_resourceFields.clear();
      m_assignmentFields.clear();
      m_projectFields.clear();
   }

   @Override public void forEach(Consumer<? super UserDefinedField> action)
   {
      m_uniqueIDMap.values().forEach(action);
   }

   @Override public Spliterator<UserDefinedField> spliterator()
   {
      return m_uniqueIDMap.values().spliterator();
   }

   @Override public int size()
   {
      return m_uniqueIDMap.size();
   }

   @Override public boolean isEmpty()
   {
      return m_uniqueIDMap.isEmpty();
   }

   @Override public boolean contains(Object o)
   {
      return m_uniqueIDMap.containsValue(o);
   }

   /**
    * Retrieve a collection fo task user defined fields.
    *
    * @return task user defined fields
    */
   public Collection<UserDefinedField> getTaskFields()
   {
      return m_taskFields.values();
   }

   /**
    * Retrieve a collection of resource user defined fields.
    *
    * @return resource user defined fields
    */
   public Collection<UserDefinedField> getResourceFields()
   {
      return m_resourceFields.values();
   }

   /**
    * Retrieve a collection of resource assignment user defined fields.
    *
    * @return resource assignment user defined fields
    */
   public Collection<UserDefinedField> getAssignmentFields()
   {
      return m_assignmentFields.values();
   }

   /**
    * Retrieve a collection of project user defined fields.
    *
    * @return project user defined fields
    */
   public Collection<UserDefinedField> getProjectFields()
   {
      return m_projectFields.values();
   }

   /**
    * Retrieve or create a user defined field by ID.
    *
    * @param id field ID
    * @param createFunction function to create a user defined field
    * @return user defined field
    */
   public UserDefinedField getOrCreateTaskField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_taskFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }

   /**
    * Retrieve or create a user defined field by ID.
    *
    * @param id field ID
    * @param createFunction function to create a user defined field
    * @return user defined field
    */
   public UserDefinedField getOrCreateResourceField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_resourceFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }

   /**
    * Retrieve or create a user defined field by ID.
    *
    * @param id field ID
    * @param createFunction function to create a user defined field
    * @return user defined field
    */
   public UserDefinedField getOrCreateAssignmentField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_assignmentFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }

   /**
    * Retrieve or create a user defined field by ID.
    *
    * @param id field ID
    * @param createFunction function to create a user defined field
    * @return user defined field
    */
   public UserDefinedField getOrCreateProjectField(Integer id, Function<Integer, UserDefinedField> createFunction)
   {
      return m_projectFields.computeIfAbsent(id, (i) -> addField(createFunction.apply(i)));
   }

   /**
    * Retrieve a user defined field by its Unique ID.
    *
    * @param id entity Unique ID
    * @return user defined field or null
    */
   public UserDefinedField getByUniqueID(Integer id)
   {
      return m_uniqueIDMap.get(id);
   }

   private UserDefinedField addField(UserDefinedField field)
   {
      m_uniqueIDMap.put(field.getUniqueID(), field);
      return field;
   }

   private final CustomFieldContainer m_customFields;
   private final Map<Integer, UserDefinedField> m_taskFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_resourceFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_assignmentFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_projectFields = new HashMap<>();
   private final Map<Integer, UserDefinedField> m_uniqueIDMap = new HashMap<>();
}
