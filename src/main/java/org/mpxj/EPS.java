/*
 * file:       EPS.java
 * author:     Jon Iles
 * date:       2025-02-05
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Enterprise Project Structure from a P6 database.
 */
public class EPS
{
   /**
    * Retrieve a list of all EPS nodes.
    *
    * @return list of all EPS nodes
    */
   public List<EpsNode> getEpsNodes()
   {
      return new ArrayList<>(m_nodes.values());
   }

   /**
    * Retrieve the root EPS node.
    *
    * @return root EPS node
    */
   public EpsNode getRootEpsNode()
   {
      return m_nodes.values().stream().filter(n -> n.getParentUniqueID() == null).findFirst().orElse(null);
   }

   /**
    * Retrieve an EPS node by unique ID.
    *
    * @param value unique ID
    * @return EpsNode instance or null
    */
   public EpsNode getEpsNodeByUniqueID(Integer value)
   {
      return m_nodes.get(value);
   }

   /**
    * Retrieve a list of all project nodes in the EPS.
    *
    * @return list of project nodes
    */
   public List<EpsProjectNode> getEpsProjectNodes()
   {
      return new ArrayList<>(m_projectNodes.values());
   }

   /**
    * Retrieve a project node by unique ID.
    *
    * @param value unique ID
    * @return EpsProjectNode instance or null
    */
   public EpsProjectNode getProjectNodeByUniqueID(Integer value)
   {
      return m_projectNodes.get(value);
   }

   /**
    * Internal method to add a child EpsNode.
    *
    * @param uniqueID node unique ID
    * @param node EpsNode instance
    */
   void addEpsChildNode(Integer uniqueID, EpsNode node)
   {
      m_nodes.put(uniqueID, node);
   }

   /**
    * Internal method to add a child EpsProjectNode.
    *
    * @param uniqueID node unique ID
    * @param node EpsProjectNode instance
    */
   void addEpsProjectNode(Integer uniqueID, EpsProjectNode node)
   {
      m_projectNodes.put(uniqueID, node);
   }

   private final Map<Integer, EpsNode> m_nodes = new LinkedHashMap<>();
   private final Map<Integer, EpsProjectNode> m_projectNodes = new LinkedHashMap<>();
}
