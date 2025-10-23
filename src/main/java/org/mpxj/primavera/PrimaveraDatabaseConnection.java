package org.mpxj.primavera;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.ConnectionHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ResultSetHelper;

class PrimaveraDatabaseConnection
{
   public PrimaveraDatabaseConnection(Connection connection)
   {
      m_connection = connection;
   }

   public PrimaveraDatabaseConnection(DataSource dataSource)
   {
      m_dataSource = dataSource;
   }

   public boolean hasTable(String name) throws SQLException
   {
      if (m_tableNames == null)
      {
         allocateConnection();
         m_tableNames = ConnectionHelper.getTableNames(m_connection);
      }

      return m_tableNames.contains(name);
   }

   public String getProductName() throws SQLException
   {
      DatabaseMetaData meta = m_connection.getMetaData();
      String productName = meta.getDatabaseProductName();
      if (productName == null || productName.isEmpty())
      {
         productName = "DATABASE";
      }
      else
      {
         productName = productName.toUpperCase();
      }
      return productName;
   }

   /**
    * Retrieve a number of rows matching the supplied query.
    *
    * @param sql query statement
    * @return result set
    */
   public List<Row> getRows(String sql) throws SQLException
   {
      allocateConnection();

      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         try (ResultSet rs = ps.executeQuery())
         {
            List<Row> result = new ArrayList<>();
            Map<String, Integer> meta = ResultSetHelper.populateMetaData(rs);
            Map<String, Integer> index = createIndexFromMetadata(meta);

            while (rs.next())
            {
               result.add(new ResultSetRow(rs, meta, index));
            }

            return result;
         }
      }
   }

   /**
    * Retrieve a number of rows matching the supplied query
    * which takes a single parameter.
    *
    * @param sql query statement
    * @param vars bind variable values
    * @return result set
    */
   public List<Row> getRows(String sql, Integer... vars) throws SQLException
   {
      allocateConnection();

      List<Row> result = new ArrayList<>();

      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         for (int loop = 0; loop < vars.length; loop++)
         {
            ps.setInt(loop + 1, NumberHelper.getInt(vars[loop]));
         }

         try (ResultSet rs = ps.executeQuery())
         {
            Map<String, Integer> meta = ResultSetHelper.populateMetaData(rs);
            Map<String, Integer> index = createIndexFromMetadata(meta);

            while (rs.next())
            {
               result.add(new ResultSetRow(rs, meta, index));
            }
         }
      }
      return (result);
   }

   /**
    * Creates a Map to allow a column name to be mapped to an array index.
    *
    * @param meta result set metadata
    * @return index
    */
   private Map<String, Integer> createIndexFromMetadata(Map<String, Integer> meta)
   {
      HashMap<String, Integer> indexMap = new HashMap<>();
      int index = 0;
      for (Map.Entry<String, Integer> entry : meta.entrySet())
      {
         indexMap.put(entry.getKey().toLowerCase(), Integer.valueOf(index++));
      }
      return indexMap;
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

   public void close()
   {
      if (m_allocatedConnection)
      {
         AutoCloseableHelper.closeQuietly(m_connection);
         m_connection = null;
      }
   }

   private DataSource m_dataSource;
   private Connection m_connection;
   private boolean m_allocatedConnection;
   private Set<String> m_tableNames;
}
