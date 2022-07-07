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

package net.sf.mpxj.asta;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import net.sf.mpxj.DayType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.common.JdbcOdbcHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ResultSetHelper;
import net.sf.mpxj.reader.AbstractProjectFileReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class AstaDatabaseReader extends AbstractDatabaseReader
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

   protected List<Row> getRows(String table, Map<String, Integer> keys) throws SQLException
   {
      String sql = "select * from " + table;
      if (!keys.isEmpty())
      {
         sql = sql + " where " + keys.entrySet().stream().map(e -> e.getKey() + "=?").collect(Collectors.joining(" and "));
      }

      allocateConnection();

      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         int index = 1;
         for (Map.Entry<String, Integer> entry : keys.entrySet())
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

   @Override protected void releaseResources()
   {
      AutoCloseableHelper.closeQuietly(m_connection);
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