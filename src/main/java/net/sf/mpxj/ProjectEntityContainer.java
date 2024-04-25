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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.common.ObjectSequence;

/**
 * Common implementation shared by project entities, providing storage, iteration and lookup.
 *
 * @param <T> concrete entity type
 */
public abstract class ProjectEntityContainer<T extends ProjectEntityWithUniqueID> extends ListWithCallbacks<T>
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
    *
    * @deprecated no longer required as the MSPDI and MPX writers handle this dynamically without changing the original schedule
    */
   @Deprecated public void renumberUniqueIDs()
   {
      int uid = firstUniqueID();
      for (T entity : this)
      {
         // TODO: remove from interface when this method is deleted
         entity.setUniqueID(Integer.valueOf(uid++));
      }
   }

   /**
    * Validate that the Unique IDs for the entities in this container are valid for MS Project.
    * If they are not valid, i.e. one or more of them are too large, renumber them.
    * @deprecated no longer required as the MSPDI and MPX writers handle this dynamically without changing the original schedule
    */
   @Deprecated public void validateUniqueIDsForMicrosoftProject()
   {
      // Deprecated
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

   @Override protected void added(T element)
   {
      if (element.getUniqueID() == null)
      {
         return;
      }

      Integer uniqueID = element.getUniqueID();
      T currentElement = m_uniqueIDMap.get(uniqueID);
      if (currentElement == element)
      {
         return;
      }

      if (currentElement != null)
      {
         m_uniqueIDClashList.add(element);
      }

      m_uniqueIDMap.put(element.getUniqueID(), element);
   }

   /**
    * Called to notify subclasses of item removal.
    *
    * @param element removed item
    */
   @Override protected void removed(T element)
   {
      m_uniqueIDMap.remove(element.getUniqueID());
   }

   /**
    * Updates an entry in the unique ID map when a unique ID is changed.
    *
    * @param element entity whose unique ID is changing
    * @param oldUniqueID old unique ID value
    * @param newUniqueID new unique ID value
    */
   public void updateUniqueID(T element, Integer oldUniqueID, Integer newUniqueID)
   {
      if (oldUniqueID != null)
      {
         m_uniqueIDMap.remove(oldUniqueID);
      }

      T currentElement = m_uniqueIDMap.get(newUniqueID);
      if (currentElement == element)
      {
         return;
      }

      if (currentElement != null)
      {
         m_uniqueIDClashList.add(element);
      }

      m_uniqueIDMap.put(newUniqueID, element);
      m_projectFile.getUniqueIdObjectSequence(element.getClass()).sync(newUniqueID);
   }

   /**
    * Retrieve the next Unique ID value for this entity.
    *
    * @return next Unique ID value
    * @deprecated use ProjectFile.getUniqueIdObjectSequence(T.class).getNext()
    */
   @Deprecated public Integer getNextUniqueID()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Update the Unique ID counter to ensure it produces
    * values which start after the highest Unique ID
    * currently in use for this entity.
    *
    * @deprecated no longer required
    */
   @Deprecated public void updateUniqueIdCounter()
   {
      // Deprecated
   }

   /**
    * Provide new Unique ID values for entity instances
    * which were found to be duplicated.
    */
   public void fixUniqueIdClashes()
   {
      if (m_uniqueIDClashList.isEmpty())
      {
         return;
      }

      ObjectSequence sequence = m_projectFile.getUniqueIdObjectSequence(m_uniqueIDClashList.get(0).getClass());
      m_uniqueIDClashList.forEach(i -> i.setUniqueID(sequence.getNext()));
      m_uniqueIDClashList.clear();
      m_uniqueIDMap.clear();
      forEach(i -> m_uniqueIDMap.put(i.getUniqueID(), i));
   }

   protected final ProjectFile m_projectFile;
   private final Map<Integer, T> m_uniqueIDMap = new HashMap<>();
   private final List<T> m_uniqueIDClashList = new ArrayList<>();
}
