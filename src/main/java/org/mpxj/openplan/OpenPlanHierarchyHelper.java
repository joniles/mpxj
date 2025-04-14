/*
 * file:       OpenPlanHierarchyHelper.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

/**
 * Implements common methods to work with hierarchical identifiers.
 */
class OpenPlanHierarchyHelper
{
   /**
    * Extract the parent ID from a hierarchical identifier.
    *
    * @param id hierarchical identifier
    * @return parent ID, or null
    */
   public static String getParentID(String id)
   {
      int index = id.lastIndexOf('.');
      if (index == -1)
      {
         return null;
      }
      return id.substring(0, index);
   }
}
