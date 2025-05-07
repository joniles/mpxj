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

package org.mpxj.primavera.p3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.primavera.common.ColumnDefinition;
import org.mpxj.primavera.common.RowValidator;
import org.mpxj.primavera.common.Table;
import org.mpxj.primavera.common.TableDefinition;

/**
 * Handles reading a table from a Btrieve file.
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
      byte[] buffer = new byte[m_definition.getPageSize()];
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

         readPage(buffer, table);
      }
   }

   /**
    * Reads data from a single page of the database file.
    *
    * @param buffer page from the database file
    * @param table Table instance
    */
   private void readPage(byte[] buffer, Table table)
   {
      int magicNumber = getShort(buffer, 0);
      if (magicNumber == 0x4400)
      {
         //System.out.println(ByteArrayHelper.hexdump(buffer, 0, 6, true, 16, ""));
         int recordSize = m_definition.getRecordSize();
         RowValidator rowValidator = m_definition.getRowValidator();
         String primaryKeyColumnName = m_definition.getPrimaryKeyColumnName();

         int index = 6;
         while (index + recordSize <= buffer.length)
         {
            //System.out.println(ByteArrayHelper.hexdump(buffer, index, recordSize, true, 16, ""));
            int btrieveValue = getShort(buffer, index);
            if (btrieveValue != 0)
            {
               Map<String, Object> row = new HashMap<>();
               row.put("ROW_VERSION", Integer.valueOf(btrieveValue));
               for (ColumnDefinition column : m_definition.getColumns())
               {
                  Object value = column.read(index, buffer);
                  //System.out.println(column.getName() + ": " + value);
                  row.put(column.getName(), value);
               }

               if (rowValidator == null || rowValidator.validRow(row))
               {
                  table.addRow(primaryKeyColumnName, row);
               }
            }
            index += recordSize;
         }
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
      return ByteArrayHelper.getShort(data, offset);
   }

   private final TableDefinition m_definition;
}
