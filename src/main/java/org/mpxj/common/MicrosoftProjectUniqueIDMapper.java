/*
 * file:       MicrosoftProjectUniqueIDMapper.java
 * author:     Jon Iles
 * date:       2023-10-18
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

package org.mpxj.common;

import java.util.HashMap;
import java.util.Map;

import org.mpxj.ProjectEntityContainer;
import org.mpxj.ProjectEntityWithUniqueID;

/**
 * This class is used by writers generating file formats which can be read by Microsoft Project.
 * Microsoft Project has a maximum unique ID value. When writing unique IDs we try to preserve the original
 * value, but if it is greater than the maximum Project can support, we create a mapping to renumber
 * it to a value Project can handle. This process leaves the original schedule unmodified.
 */
public class MicrosoftProjectUniqueIDMapper
{
   /**
    * Constructor.
    *
    * @param container existing entities of the type we are renumbering
    */
   public MicrosoftProjectUniqueIDMapper(ProjectEntityContainer<? extends ProjectEntityWithUniqueID> container)
   {
      m_container = container;
   }

   /**
    * Retrieve the unique ID value for an entity, mapping it if required.
    *
    * @param entity target entity
    * @return unique ID
    */
   public Integer getUniqueID(ProjectEntityWithUniqueID entity)
   {
      Integer currentUniqueID = entity.getUniqueID();

      if (currentUniqueID == null || currentUniqueID.intValue() < MicrosoftProjectConstants.MAX_UNIQUE_ID)
      {
         return currentUniqueID;
      }

      Integer newUniqueID = m_map.get(currentUniqueID);
      if (newUniqueID != null)
      {
         return newUniqueID;
      }

      do
      {
         newUniqueID = m_sequence.getNext();
      }
      while (m_container.getByUniqueID(newUniqueID) != null);

      m_map.put(currentUniqueID, newUniqueID);

      return newUniqueID;
   }

   private final ProjectEntityContainer<? extends ProjectEntityWithUniqueID> m_container;
   private final Map<Integer, Integer> m_map = new HashMap<>();
   private final ObjectSequence m_sequence = new ObjectSequence(1);
}
