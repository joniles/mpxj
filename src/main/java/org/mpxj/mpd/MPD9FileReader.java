/*
 * file:       MPD9FileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-07-01
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

package org.mpxj.mpd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

/**
 * This class reads project data from an MPD9 format database.
 */
final class MPD9FileReader extends MPD9AbstractReader
{
   /**
    * Set the location of the database to read.
    *
    * @param file database file
    */
   public void setDatabaseFile(File file)
   {
      m_databaseFile = file;
   }

   @Override protected List<Row> getRows(String tableName, Map<String, Integer> keys) throws MpdException
   {
      try
      {
         openDatabase();

         List<Row> result = new ArrayList<>();
         Table table = m_database.getTable(tableName);
         List<? extends Column> columns = table.getColumns();
         Cursor cursor = CursorBuilder.createCursor(table);
         if (cursor.findFirstRow(keys))
         {
            do
            {
               result.add(new JackcessResultSetRow(cursor.getCurrentRow(), columns));
            }
            while (cursor.findNextRow(keys));
         }

         return result;
      }

      catch (IOException ex)
      {
         throw new MpdException(ex);
      }
   }

   @Override protected void releaseResources()
   {
      closeDatabase();
   }

   /**
    * Allocates a database connection.
    */
   private void openDatabase() throws IOException
   {
      if (m_database == null)
      {
         m_database = DatabaseBuilder.open(m_databaseFile);
         queryDatabaseMetaData();
      }
   }

   /**
    * Releases a database connection.
    */
   private void closeDatabase()
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

   /**
    * Queries database metadata to check for the existence of
    * specific tables.
    */
   private void queryDatabaseMetaData()
   {
      try
      {
         Set<String> tables = new HashSet<>(m_database.getTableNames());
         m_hasResourceBaselines = tables.contains("MSP_RESOURCE_BASELINES");
         m_hasTaskBaselines = tables.contains("MSP_TASK_BASELINES");
         m_hasAssignmentBaselines = tables.contains("MSP_ASSIGNMENT_BASELINES");
      }

      catch (Exception ex)
      {
         // Ignore errors when reading metadata
      }
   }

   private File m_databaseFile;
   private Database m_database;
}