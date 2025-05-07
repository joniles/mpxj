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

package org.mpxj.turboproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.mpxj.common.InputStreamHelper;

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
   @Override public Iterator<MapRow> iterator()
   {
      return m_rows.values().iterator();
   }

   /**
    * Reads the table data from an input stream and breaks
    * it down into rows.
    *
    * @param is input stream
    */
   public void read(InputStream is) throws IOException
   {
      byte[] headerBlock = InputStreamHelper.read(is, 20);

      int headerLength = PEPUtility.getShort(headerBlock, 8);
      int recordCount = PEPUtility.getInt(headerBlock, 10);
      int recordLength = PEPUtility.getInt(headerBlock, 16);
      InputStreamHelper.skip(is, headerLength - headerBlock.length);

      byte[] record = new byte[recordLength];
      for (int recordIndex = 1; recordIndex <= recordCount; recordIndex++)
      {
         readRow(recordIndex, InputStreamHelper.read(is, record));
      }
   }

   /**
    * Retrieve a row based on its primary key.
    *
    * @param uniqueID unique ID of the required row
    * @return MapRow instance, or null if the row is not found
    */
   public MapRow find(Integer uniqueID)
   {
      return m_rows.get(uniqueID);
   }

   /**
    * Implemented by subclasses to extract data from the
    * byte array representing a row.
    *
    * @param uniqueID the unique ID for this row
    * @param data row data as a byte array
    */
   protected void readRow(int uniqueID, byte[] data)
   {
      // Implemented by subclasses
   }

   /**
    * Adds a row to the internal storage, indexed by primary key.
    *
    * @param uniqueID unique ID of the row
    * @param map row data as a simple map
    */
   protected void addRow(int uniqueID, Map<String, Object> map)
   {
      m_rows.put(Integer.valueOf(uniqueID), new MapRow(map));
   }

   private final Map<Integer, MapRow> m_rows = new TreeMap<>();
}
