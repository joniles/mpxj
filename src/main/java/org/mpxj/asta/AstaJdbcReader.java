/*
 * file:       AstaJdbcReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       07/04/2011
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.JdbcOdbcHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ResultSetHelper;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public class AstaJdbcReader extends AbstractAstaDatabaseReader
{

   /**
    * Set the data source. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param dataSource data source
    */
   public void setDataSource(DataSource dataSource)
   {
      m_dataSource = dataSource;
   }

   /**
    * Sets the connection. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param connection database connection
    */
   public void setConnection(Connection connection)
   {
      m_connection = connection;
   }

   @Override protected List<Row> getRows(String tableName, Map<String, Integer> keys) throws AstaDatabaseException
   {
      return getRows(tableName, keys, Collections.emptyMap());
   }

   @Override protected List<Row> getRows(String table, Map<String, Integer> keys, Map<String, String> nameMap) throws AstaDatabaseException
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
                  result.add(new MdbResultSetRow(nameMap, rs, meta));
               }
               return result;
            }
         }
      }

      catch (SQLException ex)
      {
         throw new AstaDatabaseException(ex);
      }
   }

   @Override protected void allocateResources(File file) throws AstaDatabaseException
   {
      try
      {
         String url = JdbcOdbcHelper.getMicrosoftAccessJdbcUrl(file);
         Properties props = new Properties();
         props.put("charSet", "Cp1252");
         m_connection = DriverManager.getConnection(url, props);
         m_allocatedConnection = true;
      }

      catch (SQLException ex)
      {
         throw new AstaDatabaseException(ex);
      }
   }

   @Override protected void releaseResources()
   {
      if (m_allocatedConnection)
      {
         AutoCloseableHelper.closeQuietly(m_connection);
      }
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
      }
   }

   private DataSource m_dataSource;
   private Connection m_connection;
   private boolean m_allocatedConnection;
}