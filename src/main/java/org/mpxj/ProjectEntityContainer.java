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

package org.mpxj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.common.ObjectSequence;

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
    * @param sequenceProvider sequence provider
    */
   public ProjectEntityContainer(UniqueIdObjectSequenceProvider sequenceProvider)
   {
      m_sequenceProvider = sequenceProvider;
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

      if (currentElement instanceof ProjectEntityWithMutableUniqueID)
      {
         m_uniqueIDClashList.add((ProjectEntityWithMutableUniqueID) element);
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

      if (currentElement instanceof ProjectEntityWithMutableUniqueID)
      {
         m_uniqueIDClashList.add((ProjectEntityWithMutableUniqueID) element);
      }

      m_uniqueIDMap.put(newUniqueID, element);
      m_sequenceProvider.getUniqueIdObjectSequence(element.getClass()).sync(newUniqueID);
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

      ObjectSequence sequence = m_sequenceProvider.getUniqueIdObjectSequence(m_uniqueIDClashList.get(0).getClass());
      m_uniqueIDClashList.forEach(i -> i.setUniqueID(sequence.getNext()));
      m_uniqueIDClashList.clear();
      m_uniqueIDMap.clear();
      forEach(i -> m_uniqueIDMap.put(i.getUniqueID(), i));
   }

   protected final UniqueIdObjectSequenceProvider m_sequenceProvider;
   private final Map<Integer, T> m_uniqueIDMap = new HashMap<>();
   private final List<ProjectEntityWithMutableUniqueID> m_uniqueIDClashList = new ArrayList<>();
}
