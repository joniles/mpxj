/*
 * file:       ProjectEntityWithIDContainer.java
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.common.NumberHelper;
import org.mpxj.common.ObjectSequence;

/**
 * Common implementation shared by project entities, providing storage, iteration and lookup.
 *
 * @param <T> concrete entity type
 */
public abstract class ProjectEntityWithIDContainer<T extends ProjectEntityWithID & Comparable<T>> extends ProjectEntityContainer<T>
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public ProjectEntityWithIDContainer(ProjectFile projectFile)
   {
      super(projectFile);
   }

   /**
    * This method can be called to ensure that the IDs of all
    * entities are sequential, and start from an
    * appropriate point. If entities are added to and removed from
    * this list, then the project is loaded into Microsoft
    * project, if the ID values have gaps in the sequence, there will
    * be blank rows shown.
    */
   public void renumberIDs()
   {
      if (!isEmpty())
      {
         Collections.sort(this);
         T firstEntity = get(0);
         int id = NumberHelper.getInt(firstEntity.getID());
         if (id != 0)
         {
            id = 1;
         }

         for (T entity : this)
         {
            entity.setID(Integer.valueOf(id++));
         }
      }
   }

   /**
    * Retrieve an entity by its ID.
    *
    * @param id entity ID
    * @return entity instance or null
    */
   public T getByID(Integer id)
   {
      return m_idMap.get(id);
   }

   /**
    * Remove the ID to instance mapping.
    *
    * @param id ID to remove
    */
   public void unmapID(Integer id)
   {
      m_idMap.remove(id);
   }

   /**
    * Add an ID to instance mapping.
    *
    * @param id ID
    * @param entity instance
    */
   public void mapID(Integer id, T entity)
   {
      m_idMap.put(id, entity);
   }

   /**
    * Retrieve the next ID value for this entity.
    *
    * @return next ID value
    */
   public Integer getNextID()
   {
      return m_idSequence.getNext();
   }

   private final ObjectSequence m_idSequence = new ObjectSequence(1);
   protected final Map<Integer, T> m_idMap = new HashMap<>();
}
