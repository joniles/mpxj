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

package net.sf.mpxj;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the table definitions belonging to a project.
 */
public class TableContainer extends AbstractList<Table>
{
   @Override public boolean add(Table table)
   {
      m_tables.add(table);

      if (!table.getResourceFlag())
      {
         m_taskTablesByName.put(table.getName(), table);
      }
      else
      {
         m_resourceTablesByName.put(table.getName(), table);
      }

      return true;
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

   @Override public Table get(int index)
   {
      return m_tables.get(index);
   }

   @Override public int size()
   {
      return m_tables.size();
   }

   private final List<Table> m_tables = new ArrayList<Table>();
   private Map<String, Table> m_taskTablesByName = new HashMap<String, Table>();
   private Map<String, Table> m_resourceTablesByName = new HashMap<String, Table>();
}
