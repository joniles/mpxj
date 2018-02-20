/*
 * file:       Table.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       12/01/2018
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

package net.sf.mpxj.primavera.p3;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents a table read from a PEP file.
 * The class is responsible for breaking down the raw
 * data into individual byte arrays representing each
 * row. Subclasses can then process these rows
 * and create MapRow instances to represent the data.
 * Users of this table can either iterate through the
 * rows, or select individual rows by their primary key.
 */
class Table implements Iterable<MapRow>
{
   /**
    * {@inheritDoc}
    */
   @Override public Iterator<MapRow> iterator()
   {
      return m_rows.values().iterator();
   }

   /**
    * Retrieve a row based on its primary key.
    *
    * @param uniqueID unique ID of the required row
    * @return MapRow instance, or null if the row is not found
    */
   //   public MapRow find(Integer uniqueID)
   //   {
   //      return m_rows.get(uniqueID);
   //   }

   public void addRow(String primaryKeyColumnName, Map<String, Object> map)
   {
      Integer rowNumber = Integer.valueOf(m_rowNumber++);
      map.put("ROW_NUMBER", rowNumber);
      Object primaryKey;
      if (primaryKeyColumnName == null)
      {
         primaryKey = rowNumber;
      }
      else
      {
         primaryKey = map.get(primaryKeyColumnName);
      }

      MapRow newRow = new MapRow(map);
      MapRow oldRow = m_rows.get(primaryKey);
      if (oldRow == null)
      {
         m_rows.put(primaryKey, newRow);
      }
      else
      {
         int oldVersion = oldRow.getInteger("ROW_VERSION").intValue();
         int newVersion = newRow.getInteger("ROW_VERSION").intValue();
         if (newVersion > oldVersion)
         {
            m_rows.put(primaryKey, newRow);
         }
      }
   }

   private final Map<Object, MapRow> m_rows = new TreeMap<Object, MapRow>();
   private int m_rowNumber = 1;
}
