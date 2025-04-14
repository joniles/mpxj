/*
 * file:       FastTrackTable.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2017
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

package org.mpxj.fasttrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mpxj.TimeUnit;

/**
 * Represents a table of data from an FTS file, made up of a set of MapRow instances.
 */
class FastTrackTable implements Iterable<MapRow>
{
   /**
    * Constructor.
    * @param type table type
    * @param data raw data read from the FTS file
    */
   public FastTrackTable(FastTrackTableType type, FastTrackData data)
   {
      m_data = data;
      m_type = type;
   }

   /**
    * Retrieve the type of this table.
    *
    * @return table type
    */
   public FastTrackTableType getType()
   {
      return m_type;
   }

   /**
    * Retrieve the duration time units used in this table.
    *
    * @return duration time units
    */
   public TimeUnit getDurationTimeUnit()
   {
      return m_data.getDurationTimeUnit();
   }

   /**
    * Retrieve the work time units used in this table.
    *
    * @return work time units
    */
   public TimeUnit getWorkTimeUnit()
   {
      return m_data.getWorkTimeUnit();
   }

   /**
    * Add data for a column to this table.
    *
    * @param column column data
    */
   public void addColumn(FastTrackColumn column)
   {
      FastTrackField type = column.getType();
      Object[] data = column.getData();
      for (int index = 0; index < data.length; index++)
      {
         MapRow row = getRow(index);
         row.getMap().put(type, data[index]);
      }
   }

   @Override public Iterator<MapRow> iterator()
   {
      return m_rows.iterator();
   }

   /**
    * Retrieve a specific row by index number, creating a blank row if this row does not exist.
    *
    * @param index index number
    * @return MapRow instance
    */
   private MapRow getRow(int index)
   {
      MapRow result;

      if (index == m_rows.size())
      {
         result = new MapRow(this, new HashMap<>());
         m_rows.add(result);
      }
      else
      {
         result = m_rows.get(index);
      }

      return result;
   }

   private final FastTrackData m_data;
   private final FastTrackTableType m_type;
   private final ArrayList<MapRow> m_rows = new ArrayList<>();
}
