/*
 * file:       TableReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Common functionality to support reading Synchro tables.
 */
abstract class TableReader
{
   /**
    * Constructor.
    *
    * @param stream input stream
    */
   public TableReader(InputStream stream)
   {
      m_stream = new StreamReader(stream);
   }

   /**
    * Retrieve the rows read from the table.
    *
    * @return table rows
    */
   public List<MapRow> getRows()
   {
      return m_rows;
   }

   /**
    * Read data from the table. Return a reference to the current
    * instance to allow method chaining.
    *
    * @return reader instance
    */
   public TableReader read() throws IOException
   {
      int tableHeader = m_stream.readInt();
      if (tableHeader != 0x39AF547A)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      int recordCount = m_stream.readInt();
      for (int loop = 0; loop < recordCount; loop++)
      {
         int rowMagicNumber = m_stream.readInt();
         if (rowMagicNumber != rowMagicNumber())
         {
            throw new IllegalArgumentException("Unexpected file format");
         }

         // We use a LinkedHashMap to preserve insertion order in iteration
         // Useful when debugging the file format.
         Map<String, Object> map = new LinkedHashMap<String, Object>();

         if (hasUUID())
         {
            map.put("UNKNOWN0", m_stream.readBytes(16));
            map.put("UUID", m_stream.readUUID());
         }

         readRow(m_stream, map);

         SynchroLogger.log("READER", getClass(), map);

         m_rows.add(new MapRow(map));
      }

      int tableTrailer = m_stream.readInt();
      if (tableTrailer != 0x6F99E416)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      postTrailer(m_stream);

      return this;
   }

   /**
    * Overridden by child classes to indicate to the reader that the typical UUID structure
    * is not present for some types of table.
    *
    * @return true if the table starts with an expected UUID structure
    */
   protected boolean hasUUID()
   {
      return true;
   }

   /**
    * Allows additional behaviour once the main table data has been read.
    *
    * @param stream input stream
    */
   @SuppressWarnings("unused") protected void postTrailer(StreamReader stream) throws IOException
   {
      // Default implementation
   }

   /**
    * Overridden by child classes to define their row magic number.
    *
    * @return row magic number
    */
   protected abstract int rowMagicNumber();

   /**
    * Overridden by child classes to extract data from a single row.
    *
    * @param stream input stream
    * @param map map to store data from row
    */
   protected abstract void readRow(StreamReader stream, Map<String, Object> map) throws IOException;

   protected final StreamReader m_stream;
   private final List<MapRow> m_rows = new ArrayList<MapRow>();
}
