/*
 * file:       ClashMap.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       02/05/2021
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

package org.mpxj.primavera;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maintains a mapping of clashes and resulting renumbering
 * where we are bringing together unique ID values from two
 * different sources.
 */
class ClashMap
{
   /**
    * Adds an ID. Returns the renumbered ID is there is a clash.
    *
    * @param id original ID
    * @return renumbered ID
    */
   public Integer addID(Integer id)
   {
      if (m_set.contains(id))
      {
         while (m_set.contains(Integer.valueOf(m_next)))
         {
            ++m_next;
         }
         Integer newID = Integer.valueOf(m_next);
         m_set.add(newID);
         m_map.put(id, newID);
         id = newID;
      }
      else
      {
         m_set.add(id);
      }

      return id;
   }

   /**
    * If there has been a clash this method retrieves the renumbered version of the ID.
    * If there has been no clash, the original ID value is returned.
    *
    * @param id ID to test
    * @return renumbered ID value
    */
   public Integer getID(Integer id)
   {
      Integer result = m_map.get(id);
      return result == null ? id : result;
   }

   private int m_next = 1;
   private final Set<Integer> m_set = new HashSet<>();
   private final Map<Integer, Integer> m_map = new HashMap<>();
}
