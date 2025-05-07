/*
 * file:       EpsProjectNode.java
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

/**
 * Represents a project node in the Enterprise Project Structure from a P6 database.
 */
public class EpsProjectNode
{
   /**
    * Constructor.
    *
    * @param eps parent EPS container
    * @param uniqueID project unique ID
    * @param parentEpsUniqueID parent EPS node unique ID
    * @param shortName project short name
    * @param name project name
    */
   public EpsProjectNode(EPS eps, Integer uniqueID, Integer parentEpsUniqueID, String shortName, String name)
   {
      m_uniqueID = uniqueID;
      m_parentEpsUniqueID = parentEpsUniqueID;
      m_shortName = shortName;
      m_name = name;
      eps.addEpsProjectNode(m_uniqueID, this);
   }

   /**
    * Retrieve the project unique ID.
    *
    * @return project unique ID
    */
   public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the parent EPS node unique ID.
    *
    * @return parent EPS node unique ID
    */
   public Integer getParentEpsUniqueID()
   {
      return m_parentEpsUniqueID;
   }

   /**
    * Retrieve the project short name.
    *
    * @return project short name
    */
   public String getShortName()
   {
      return m_shortName;
   }

   /**
    * Retrieve the project name.
    *
    * @return project name
    */
   public String getName()
   {
      return m_name;
   }

   private final Integer m_uniqueID;
   private final Integer m_parentEpsUniqueID;
   private final String m_shortName;
   private final String m_name;
}
