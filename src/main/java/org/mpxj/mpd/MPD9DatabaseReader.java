/*
 * file:       MPD9DatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       2006-02-02
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.ConnectionHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ResultSetHelper;

/**
 * This class reads project data from an MPD9 format database.
 */
final class MPD9DatabaseReader extends MPD9AbstractReader
{
   @Override protected List<Row> getRows(String table, Map<String, Integer> keys) throws MpdException
   {
      // Copy entries to a list to ensure order is consistent when iterating
      List<Map.Entry<String, Integer>> keyList = new ArrayList<>(keys.entrySet());

      String sql = "select * from " + table;
      if (!keys.isEmpty())
      {
         sql = sql + " where " + keyList.stream().map(e -> e.getKey() + "=?").collect(Collectors.joining(" and "));
      }

      try
      {
         allocateConnection();

         try (PreparedStatement ps = m_connection.prepareStatement(sql))
         {
            int index = 1;
            for (Map.Entry<String, Integer> entry : keyList)
            {
               ps.setInt(index++, NumberHelper.getInt(entry.getValue()));
            }

            try (ResultSet rs = ps.executeQuery())
            {
               List<Row> result = new ArrayList<>();
               Map<String, Integer> meta = ResultSetHelper.populateMetaData(rs);
               while (rs.next())
               {
                  result.add(new MpdResultSetRow(rs, meta));
               }
               return result;
            }
         }
      }

      catch (SQLException ex)
      {
         throw new MpdException(ex);
      }
   }

   @Override protected void releaseResources()
   {
      releaseConnection();
   }

   /**
    * Allocates a database connection.
    */
   private void allocateConnection() throws SQLException
   {
      if (m_connection == null)
      {
         m_connection = m_dataSource.getConnection();
         m_allocatedConnection = true;
         queryDatabaseMetaData();
      }
   }

   private void releaseConnection()
   {
      if (m_allocatedConnection)
      {
         AutoCloseableHelper.closeQuietly(m_connection);
         m_connection = null;
      }
   }

   /**
    * Sets the data source used to read the project data.
    *
    * @param dataSource data source
    */
   public void setDataSource(DataSource dataSource)
   {
      m_dataSource = dataSource;
   }

   /**
    * Sets the connection to be used to read the project data.
    *
    * @param connection database connection
    */
   public void setConnection(Connection connection)
   {
      m_connection = connection;
      queryDatabaseMetaData();
   }

   /**
    * Queries database metadata to check for the existence of
    * specific tables.
    */
   private void queryDatabaseMetaData()
   {
      try
      {
         Set<String> tables = ConnectionHelper.getTableNames(m_connection);
         m_hasResourceBaselines = tables.contains("MSP_RESOURCE_BASELINES");
         m_hasTaskBaselines = tables.contains("MSP_TASK_BASELINES");
         m_hasAssignmentBaselines = tables.contains("MSP_ASSIGNMENT_BASELINES");
      }

      catch (Exception ex)
      {
         // Ignore errors when reading metadata
      }
   }

   private DataSource m_dataSource;
   private boolean m_allocatedConnection;
   private Connection m_connection;
}