/*
 * file:       XmlReaderState.java
 * author:     Jon Iles
 * date:       2025-11-12
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

import java.util.ArrayList;
import java.util.List;

import org.mpxj.ProjectContext;
import org.mpxj.primavera.schema.APIBusinessObjects;

/**
 * State data shared between PMXML readers.
 */
class XmlReaderState
{
   /**
    * Constructor.
    *
    * @param apibo root node of PMXML file
    */
   public XmlReaderState(APIBusinessObjects apibo)
   {
      m_apibo = apibo;
   }

   /**
    * Retrieve the root node.
    *
    * @return root node
    */
   public APIBusinessObjects getApibo()
   {
      return m_apibo;
   }

   /**
    * Retrieve the ProjectContext instance.
    *
    * @return project context
    */
   public ProjectContext getContext()
   {
      return m_context;
   }

   /**
    * Retrieve the role clash map.
    *
    * @return role clash map
    */
   public ClashMap getRoleClashMap()
   {
      return m_roleClashMap;
   }

   /**
    * Retrieve the external relations list.
    *
    * @return external relations list
    */
   public List<ExternalRelation> getExternalRelations()
   {
      return m_externalRelations;
   }

   private final APIBusinessObjects m_apibo;
   private final ProjectContext m_context = new ProjectContext();
   private final ClashMap m_roleClashMap = new ClashMap();
   private final List<ExternalRelation> m_externalRelations = new ArrayList<>();
}
