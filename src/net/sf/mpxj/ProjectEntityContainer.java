/*
 * file:       ProjectEntityContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       20/04/2015
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sf.mpxj.common.NumberHelper;

/**
 * Common implementation shared by project entities, providing storage, iteration and lookup.
 * 
 * @param <T> concrete entity type
 */
public abstract class ProjectEntityContainer<T extends ProjectEntityWithUniqueID> implements List<T>
{
   /**
    * Constructor.
    * 
    * @param projectFile parent project
    */
   public ProjectEntityContainer(ProjectFile projectFile)
   {
      m_projectFile = projectFile;
   }

   /**
    * Add a new project entity.
    * 
    * @return new entity instance
    */
   public abstract T add();

   /**
    * Returns the value of the first Unique ID to use when renumbering Unique IDs.
    * 
    * @return first Unique ID value
    */
   protected int firstUniqueID()
   {
      return 1;
   }

   /**
    * Renumbers all entity unique IDs.
    */
   public void renumberUniqueIDs()
   {
      int uid = firstUniqueID();
      for (T entity : m_list)
      {
         entity.setUniqueID(Integer.valueOf(uid++));
      }
   }

   /**
    * Validate that the Unique IDs for the entities in this container are valid for MS Project.
    * If they are not valid, i.e one or more of them are too large, renumber them.
    */
   public void validateUniqueIDsForMicrosoftProject()
   {
      if (!m_list.isEmpty())
      {
         for (T entity : m_list)
         {
            if (NumberHelper.getInt(entity.getUniqueID()) > MS_PROJECT_MAX_UNIQUE_ID)
            {
               renumberUniqueIDs();
               break;
            }
         }
      }
   }

   /**
    * Retrieve an entity by its Unique ID.
    * 
    * @param id entity Unique ID
    * @return entity instance or null
    */
   public T getByUniqueID(Integer id)
   {
      return m_uniqueIDMap.get(id);
   }

   /**
    * Remove the Unique ID to instance mapping.
    * 
    * @param id Unique ID to remove
    */
   public void unmapUniqueID(Integer id)
   {
      m_uniqueIDMap.remove(id);
   }

   /**
    * Add a Unique ID to instance mapping.
    * 
    * @param id Unique ID
    * @param entity instance
    */
   public void mapUniqueID(Integer id, T entity)
   {
      m_uniqueIDMap.put(id, entity);
   }

   @Override public int size()
   {
      return m_list.size();
   }

   @Override public boolean isEmpty()
   {
      return m_list.isEmpty();
   }

   @Override public boolean contains(Object o)
   {
      return m_list.contains(o);
   }

   @Override public Iterator<T> iterator()
   {
      return m_list.iterator();
   }

   @Override public Object[] toArray()
   {
      return m_list.toArray();
   }

   @Override public <A> A[] toArray(A[] a)
   {
      return m_list.toArray(a);
   }

   @Override public boolean add(T e)
   {
      return m_list.add(e);
   }

   @Override public boolean containsAll(Collection<?> c)
   {
      return m_list.containsAll(c);
   }

   @Override public boolean addAll(Collection<? extends T> c)
   {
      return m_list.addAll(c);
   }

   @Override public boolean removeAll(Collection<?> c)
   {
      return m_list.removeAll(c);
   }

   @Override public boolean retainAll(Collection<?> c)
   {
      return m_list.retainAll(c);
   }

   @Override public void clear()
   {
      m_list.clear();
   }

   @Override public T get(int index)
   {
      return m_list.get(index);
   }

   @Override public T set(int index, T element)
   {
      return m_list.set(index, element);
   }

   @Override public void add(int index, T element)
   {
      m_list.add(index, element);
   }

   @Override public T remove(int index)
   {
      return m_list.remove(index);
   }

   @Override public int indexOf(Object o)
   {
      return m_list.indexOf(o);
   }

   @Override public int lastIndexOf(Object o)
   {
      return m_list.lastIndexOf(o);
   }

   @Override public ListIterator<T> listIterator()
   {
      return m_list.listIterator();
   }

   @Override public ListIterator<T> listIterator(int index)
   {
      return m_list.listIterator(index);
   }

   @Override public List<T> subList(int fromIndex, int toIndex)
   {
      return m_list.subList(fromIndex, toIndex);
   }

   @Override public boolean addAll(int index, Collection<? extends T> c)
   {
      return m_list.addAll(index, c);
   }

   protected final ProjectFile m_projectFile;
   protected final List<T> m_list = new LinkedList<T>();
   protected Map<Integer, T> m_uniqueIDMap = new HashMap<Integer, T>();

   /**
    * Maximum unique ID value MS Project will accept.
    */
   private static final int MS_PROJECT_MAX_UNIQUE_ID = 0x1FFFFF;
}
