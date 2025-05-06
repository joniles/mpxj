/*
 * file:       TableContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       23/04/2015
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

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the table definitions belonging to a project.
 */
public class TableContainer extends ListWithCallbacks<Table>
{
   @Override protected void added(Table table)
   {
      getIndex(table).put(table.getName(), table);
   }

   @Override protected void removed(Table table)
   {
      getIndex(table).remove(table.getName());
   }

   /**
    * Utility method to retrieve the definition of a task table by name.
    * This method will return null if the table name is not recognised.
    *
    * @param name table name
    * @return table instance
    */
   public Table getTaskTableByName(String name)
   {
      return m_taskTablesByName.get(name);
   }

   /**
    * Utility method to retrieve the definition of a resource table by name.
    * This method will return null if the table name is not recognised.
    *
    * @param name table name
    * @return table instance
    */
   public Table getResourceTableByName(String name)
   {
      return m_resourceTablesByName.get(name);
   }

   /**
    * Retrieve the correct index for the supplied Table instance.
    *
    * @param table Table instance
    * @return index
    */
   private Map<String, Table> getIndex(Table table)
   {
      Map<String, Table> result;

      if (!table.getResourceFlag())
      {
         result = m_taskTablesByName;
      }
      else
      {
         result = m_resourceTablesByName;
      }
      return result;
   }

   private final Map<String, Table> m_taskTablesByName = new HashMap<>();
   private final Map<String, Table> m_resourceTablesByName = new HashMap<>();
}
