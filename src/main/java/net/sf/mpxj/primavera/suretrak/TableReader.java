/*
 * file:       TableReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package net.sf.mpxj.primavera.suretrak;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.common.InputStreamHelper;
import net.sf.mpxj.primavera.common.ColumnDefinition;
import net.sf.mpxj.primavera.common.Table;
import net.sf.mpxj.primavera.common.TableDefinition;

/**
 * Handles reading a table from a SureTrak file.
 */
class TableReader
{
   /**
    * Constructor.
    *
    * @param definition table structure definition
    */
   public TableReader(TableDefinition definition)
   {
      m_definition = definition;
   }

   /**
    * Read the table from the file and populate the supplied Table instance.
    *
    * @param file database file
    * @param table Table instance
    */
   public void read(File file, Table table) throws IOException
   {
      //System.out.println("Reading " + file.getName());
      InputStream is = null;
      try
      {
         is = Files.newInputStream(file.toPath());
         read(is, table);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(is);
      }
   }

   /**
    * Read the table from an input stream and populate the supplied Table instance.
    *
    * @param is input stream from table file
    * @param table Table instance
    */
   private void read(InputStream is, Table table) throws IOException
   {
      InputStreamHelper.skip(is, 6); // header
      InputStreamHelper.skip(is, 2); // record count

      byte[] buffer = new byte[m_definition.getRecordSize()];
      while (true)
      {
         int bytesRead = is.read(buffer);
         if (bytesRead == -1)
         {
            break;
         }

         if (bytesRead != buffer.length)
         {
            throw new IOException("Unexpected end of file");
         }

         if (buffer[0] == 0)
         {
            readRecord(buffer, table);
         }
      }
   }

   /**
    * Reads a single record from the table.
    *
    * @param buffer record data
    * @param table parent table
    */
   private void readRecord(byte[] buffer, Table table)
   {
      //System.out.println(ByteArrayHelper.hexdump(buffer, true, 16, ""));
      int deletedFlag = getShort(buffer, 0);
      if (deletedFlag != 0)
      {
         Map<String, Object> row = new HashMap<>();
         for (ColumnDefinition column : m_definition.getColumns())
         {
            Object value = column.read(0, buffer);
            //System.out.println(column.getName() + ": " + value);
            row.put(column.getName(), value);
         }

         table.addRow(m_definition.getPrimaryKeyColumnName(), row);
      }
   }

   /**
    * Read a two byte integer from a byte array.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return int value
    */
   private int getShort(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   private final TableDefinition m_definition;
}
