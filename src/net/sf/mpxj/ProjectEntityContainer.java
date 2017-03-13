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

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.common.NumberHelper;

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
    */
   public void renumberUniqueIDs()
   {
      int uid = firstUniqueID();
      for (T entity : this)
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
      if (!isEmpty())
      {
         for (T entity : this)
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

   protected final ProjectFile m_projectFile;
   protected Map<Integer, T> m_uniqueIDMap = new HashMap<Integer, T>();

   /**
    * Maximum unique ID value MS Project will accept.
    */
   private static final int MS_PROJECT_MAX_UNIQUE_ID = 0x1FFFFF;
}
