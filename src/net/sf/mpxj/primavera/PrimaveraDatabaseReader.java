/*
 * file:       PrimaveraDatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       22/03/2010
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

package net.sf.mpxj.primavera;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class PrimaveraDatabaseReader implements ProjectReader
{
   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current database.
    * 
    * @return Map instance containing ID and name pairs
    * @throws MPXJException
    */
   public Map<Integer, String> listProjects() throws MPXJException
   {
      try
      {
         Map<Integer, String> result = new HashMap<Integer, String>();

         List<ResultSetRow> rows = getRows("select proj_id, proj_short_name from " + m_schema + "project");
         for (ResultSetRow row : rows)
         {
            Integer id = row.getInteger("proj_id");
            String name = row.getString("proj_short_name");
            result.put(id, name);
         }

         return result;
      }

      catch (SQLException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * Read a project from the current data source.
    * 
    * @return ProjectFile instance
    * @throws MPXJException
    */
   public ProjectFile read() throws MPXJException
   {
      try
      {
         m_reader = new PrimaveraReader();

         processProjectHeader();
         //processCalendars();
         processResources();
         processTasks();
         processPredecessors();
         processAssignments();

         ProjectFile project = m_reader.getProject();
         m_reader = null;
         project.updateStructure();

         return (project);
      }

      catch (SQLException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         if (m_allocatedConnection && m_connection != null)
         {
            try
            {
               m_connection.close();
            }

            catch (SQLException ex)
            {
               // silently ignore errors on close
            }

            m_connection = null;
         }
      }
   }

   /**
    * Select the project header row from the database.
    * 
    * @throws SQLException
    */
   private void processProjectHeader() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "project where proj_id=?", m_projectID);
      m_reader.processProjectHeader(rows);
   }

   /**
    * Process resources.
    * 
    * @throws SQLException
    */
   private void processResources() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "rsrc where rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=?) order by rsrc_seq_num", m_projectID);
      m_reader.processResources(rows);
   }

   /**
    * Process tasks.
    * 
    * @throws SQLException
    */
   private void processTasks() throws SQLException
   {
      List<Row> wbs = getRows("select * from " + m_schema + "projwbs where proj_id=? order by seq_num", m_projectID);
      List<Row> tasks = getRows("select * from " + m_schema + "task where proj_id=?", m_projectID);
      m_reader.processTasks(wbs, tasks);
   }

   /**
    * Process predecessors.
    * 
    * @throws SQLException
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "taskpred where proj_id=?", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    * 
    * @throws SQLException
    */
   private void processAssignments() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "taskrsrc where proj_id=?", m_projectID);
      m_reader.processAssignments(rows);
   }

   /**
    * Set the ID of the project to be read.
    * 
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectID = Integer.valueOf(projectID);
   }

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

   /**
    * {@inheritDoc}
    */
   public ProjectFile read(String fileName)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public ProjectFile read(File file)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public ProjectFile read(InputStream inputStream)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Retrieve a number of rows matching the supplied query. 
    * 
    * @param sql query statement
    * @return result set
    * @throws SQLException
    */
   private List<ResultSetRow> getRows(String sql) throws SQLException
   {
      allocateConnection();

      try
      {
         List<ResultSetRow> result = new LinkedList<ResultSetRow>();

         m_ps = m_connection.prepareStatement(sql);
         m_rs = m_ps.executeQuery();
         populateMetaData();
         while (m_rs.next())
         {
            result.add(new ResultSetRow(m_rs, m_meta));
         }

         return (result);
      }

      finally
      {
         releaseConnection();
      }
   }

   /**
    * Retrieve a number of rows matching the supplied query 
    * which takes a single parameter.
    * 
    * @param sql query statement
    * @param var bind variable value
    * @return result set
    * @throws SQLException
    */
   private List<Row> getRows(String sql, Integer var) throws SQLException
   {
      allocateConnection();

      try
      {
         List<Row> result = new LinkedList<Row>();

         m_ps = m_connection.prepareStatement(sql);
         m_ps.setInt(1, NumberUtility.getInt(var));
         m_rs = m_ps.executeQuery();
         populateMetaData();
         while (m_rs.next())
         {
            result.add(new ResultSetRow(m_rs, m_meta));
         }

         return (result);
      }

      finally
      {
         releaseConnection();
      }
   }

   /**
    * Allocates a database connection.
    * 
    * @throws SQLException
    */
   private void allocateConnection() throws SQLException
   {
      if (m_connection == null)
      {
         m_connection = m_dataSource.getConnection();
         m_allocatedConnection = true;
      }
   }

   /**
    * Releases a database connection, and cleans up any resources
    * associated with that connection.
    */
   private void releaseConnection()
   {
      if (m_rs != null)
      {
         try
         {
            m_rs.close();
         }

         catch (SQLException ex)
         {
            // silently ignore errors on close
         }

         m_rs = null;
      }

      if (m_ps != null)
      {
         try
         {
            m_ps.close();
         }

         catch (SQLException ex)
         {
            // silently ignore errors on close
         }

         m_ps = null;
      }
   }

   /**
    * Retrieves basic meta data from the result set.
    * 
    * @throws SQLException
    */
   private void populateMetaData() throws SQLException
   {
      m_meta.clear();

      ResultSetMetaData meta = m_rs.getMetaData();
      int columnCount = meta.getColumnCount() + 1;
      for (int loop = 1; loop < columnCount; loop++)
      {
         String name = meta.getColumnName(loop);
         Integer type = Integer.valueOf(meta.getColumnType(loop));
         m_meta.put(name, type);
      }
   }

   /**
    * Set the name of the schema containing the Primavera tables.
    * 
    * @param schema schema name.
    */
   public void setSchema(String schema)
   {
      if (schema.charAt(schema.length() - 1) != '.')
      {
         schema = schema + '.';
      }
      m_schema = schema;
   }

   /**
    * Retrieve the name of the schema containing the Primavera tables.
    * 
    * @return schema name
    */
   public String getSchema()
   {
      return m_schema;
   }

   private PrimaveraReader m_reader;
   private Integer m_projectID;
   private String m_schema = "";
   private DataSource m_dataSource;
   private Connection m_connection;
   private boolean m_allocatedConnection;
   private PreparedStatement m_ps;
   private ResultSet m_rs;
   private Map<String, Integer> m_meta = new HashMap<String, Integer>();
}