/*
 * file:       AstaMdbReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       07/07/2022
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

package org.mpxj.asta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class AstaMdbReader extends AbstractAstaDatabaseReader
{
   @Override protected List<Row> getRows(String tableName, Map<String, Integer> keys) throws AstaDatabaseException
   {
      return getRows(tableName, keys, Collections.emptyMap());
   }

   @Override protected List<Row> getRows(String tableName, Map<String, Integer> keys, Map<String, String> nameMap) throws AstaDatabaseException
   {
      try
      {
         if (m_database == null)
         {
            m_database = DatabaseBuilder.open(m_databaseFile);
         }

         List<Row> result = new ArrayList<>();
         Table table = m_database.getTable(tableName);
         List<? extends Column> columns = table.getColumns();

         if (keys.isEmpty())
         {
            for (com.healthmarketscience.jackcess.Row row : table)
            {
               result.add(new JackcessResultSetRow(nameMap, row, columns));
            }
         }
         else
         {
            Cursor cursor = CursorBuilder.createCursor(table);
            if (cursor.findFirstRow(keys))
            {
               do
               {
                  result.add(new JackcessResultSetRow(nameMap, cursor.getCurrentRow(), columns));
               }
               while (cursor.findNextRow(keys));
            }
         }

         return result;
      }

      catch (IOException ex)
      {
         throw new AstaDatabaseException(ex);
      }
   }

   @Override protected void allocateResources(File file)
   {
      m_databaseFile = file;
   }

   @Override protected void releaseResources()
   {
      try
      {
         if (m_database != null)
         {
            m_database.close();
            m_database = null;
         }
      }

      catch (IOException ex)
      {
         // Ignore errors closing the database
      }
   }

   private File m_databaseFile;
   private Database m_database;
}