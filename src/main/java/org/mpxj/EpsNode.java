/*
 * file:       EpsNode.java
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a node in the Enterprise Project Structure from a P6 database.
 */
public class EpsNode
{
   /**
    * Constructor.
    *
    * @param eps parent EPS container
    * @param uniqueID node unique ID
    * @param parentUniqueID parent node unique ID
    * @param name node name
    * @param shortName node short name
    */
   public EpsNode(EPS eps, Integer uniqueID, Integer parentUniqueID, String name, String shortName)
   {
      m_eps = eps;
      m_uniqueID = uniqueID;
      m_parentUniqueID = parentUniqueID;
      m_name = name;
      m_shortName = shortName;
      eps.addEpsChildNode(m_uniqueID, this);
   }

   /**
    * Retrieve the node unique ID.
    *
    * @return node unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the parent node unique ID.
    *
    * @return parent node unique ID
    */
   public Integer getParentUniqueID()
   {
      return m_parentUniqueID;
   }

   /**
    * Retrieve the node name.
    *
    * @return node name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the node short name.
    *
    * @return node short name
    */
   public String getShortName()
   {
      return m_shortName;
   }

   /**
    * Retrieve the child nodes of this node.
    *
    * @return list of child nodes
    */
   public List<EpsNode> getChildEpsNodes()
   {
      return m_eps.getEpsNodes().stream().filter(n -> m_uniqueID.equals(n.getParentUniqueID())).collect(Collectors.toList());
   }

   /**
    * Retrieve the child project nodes of this node.
    *
    * @return list of child project nodes
    */
   public List<EpsProjectNode> getEpsProjectNodes()
   {
      return m_eps.getEpsProjectNodes().stream().filter(n -> n.getParentEpsUniqueID().equals(m_uniqueID)).collect(Collectors.toList());
   }

   private final EPS m_eps;
   private final Integer m_uniqueID;
   private final Integer m_parentUniqueID;
   private final String m_name;
   private final String m_shortName;
}
