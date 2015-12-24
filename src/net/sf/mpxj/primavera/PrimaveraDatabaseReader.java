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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.mpxj.Day;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.ProjectReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class PrimaveraDatabaseReader implements ProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

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

         List<Row> rows = getRows("select proj_id, proj_short_name from " + m_schema + "project where delete_date is null");
         for (Row row : rows)
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
         m_reader = new PrimaveraReader(m_udfCounters, m_resourceFields, m_wbsFields, m_taskFields, m_assignmentFields, m_aliases, m_matchPrimaveraWBS);
         ProjectFile project = m_reader.getProject();
         project.getEventManager().addProjectListeners(m_projectListeners);

         processProjectProperties();
         processCalendars();
         processResources();
         processTasks();
         processPredecessors();
         processAssignments();

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
    * Convenience method which allows all projects in the database to
    * be read in a single operation.
    *
    * @return list of ProjectFile instances
    * @throws MPXJException
    */
   public List<ProjectFile> readAll() throws MPXJException
   {
      Map<Integer, String> projects = listProjects();
      List<ProjectFile> result = new ArrayList<ProjectFile>(projects.keySet().size());
      for (Integer id : projects.keySet())
      {
         setProjectID(id.intValue());
         result.add(read());
      }
      return result;
   }

   /**
    * Select the project properties from the database.
    *
    * @throws SQLException
    */
   private void processProjectProperties() throws SQLException
   {
      //
      // Process common attributes
      //
      List<Row> rows = getRows("select * from " + m_schema + "project where proj_id=?", m_projectID);
      m_reader.processProjectProperties(rows);

      //
      // Process PMDB-specific attributes
      //
      rows = getRows("select * from " + m_schema + "prefer where prefer.delete_date is null");
      if (!rows.isEmpty())
      {
         Row row = rows.get(0);
         ProjectProperties ph = m_reader.getProject().getProjectProperties();
         ph.setCreationDate(row.getDate("create_date"));
         ph.setLastSaved(row.getDate("update_date"));
         ph.setMinutesPerDay(Double.valueOf(row.getDouble("day_hr_cnt").doubleValue() * 60));
         ph.setMinutesPerWeek(Double.valueOf(row.getDouble("week_hr_cnt").doubleValue() * 60));
         ph.setWeekStartDay(Day.getInstance(row.getInt("week_start_day_num")));

         processDefaultCurrency(row.getInteger("curr_id"));
      }
   }

   /**
    * Select the default currency properties from the database.
    *
    * @param currencyID default currency ID
    */
   private void processDefaultCurrency(Integer currencyID) throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "currtype where curr_id=?", currencyID);
      if (!rows.isEmpty())
      {
         Row row = rows.get(0);
         m_reader.processDefaultCurrency(row);
      }
   }

   /**
    * Process resources.
    *
    * @throws SQLException
    */
   private void processResources() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "rsrc where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by rsrc_seq_num", m_projectID);
      m_reader.processResources(rows);
   }

   /**
    * Process tasks.
    *
    * @throws SQLException
    */
   private void processTasks() throws SQLException
   {
      List<Row> wbs = getRows("select * from " + m_schema + "projwbs where proj_id=? and delete_date is null order by parent_wbs_id,seq_num", m_projectID);
      List<Row> tasks = getRows("select * from " + m_schema + "task where proj_id=? and delete_date is null", m_projectID);
      List<Row> costs = getRows("select * from " + m_schema + "projcost where proj_id=? and delete_date is null", m_projectID);
      m_reader.processTasks(wbs, tasks, costs);
   }

   /**
    * Process predecessors.
    *
    * @throws SQLException
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "taskpred where proj_id=? and delete_date is null", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process calendars.
    *
    * @throws SQLException
    */
   private void processCalendars() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "calendar where (proj_id is null or proj_id=?) and delete_date is null", m_projectID);
      m_reader.processCalendars(rows);
   }

   /**
    * Process resource assignments.
    *
    * @throws SQLException
    */
   private void processAssignments() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "taskrsrc where proj_id=? and delete_date is null", m_projectID);
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
   @Override public ProjectFile read(String fileName)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(File file)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream inputStream)
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
   private List<Row> getRows(String sql) throws SQLException
   {
      allocateConnection();

      try
      {
         List<Row> result = new LinkedList<Row>();

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
         m_ps.setInt(1, NumberHelper.getInt(var));
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
         String name = meta.getColumnName(loop).toLowerCase();
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
      if (schema == null)
      {
         schema = "";
      }
      else
      {
         if (!schema.isEmpty() && !schema.endsWith("."))
         {
            schema = schema + '.';
         }
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

   /**
    * Override the default field name mapping for user defined types.
    *
    * @param type target user defined data type
    * @param fieldName field name
    */
   public void setFieldNameForUdfType(UserFieldDataType type, String fieldName)
   {
      m_udfCounters.setFieldNameForType(type, fieldName);
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getResourceFieldMap()
   {
      return m_resourceFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getWbsFieldMap()
   {
      return m_wbsFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getTaskFieldMap()
   {
      return m_taskFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getAssignmentFields()
   {
      return m_assignmentFields;
   }

   /**
    * Customise the MPXJ field name aliases applied by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getAliases()
   {
      return m_aliases;
   }

   /**
    * If set to true, the WBS for each task read from Primavera will exactly match the WBS value shown in Primavera.
    * If set to false, each task will be given a unique WBS based on the WBS present in Primavera.
    * Defaults to true.
    *
    * @return flag value
    */
   public boolean getMatchPrimaveraWBS()
   {
      return m_matchPrimaveraWBS;
   }

   /**
    * If set to true, the WBS for each task read from Primavera will exactly match the WBS value shown in Primavera.
    * If set to false, each task will be given a unique WBS based on the WBS present in Primavera.
    * Defaults to true.
    *
    * @param matchPrimaveraWBS flag value
    */
   public void setMatchPrimaveraWBS(boolean matchPrimaveraWBS)
   {
      m_matchPrimaveraWBS = matchPrimaveraWBS;
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
   private List<ProjectListener> m_projectListeners;
   private UserFieldCounters m_udfCounters = new UserFieldCounters();
   private boolean m_matchPrimaveraWBS = true;

   private Map<FieldType, String> m_resourceFields = PrimaveraReader.getDefaultResourceFieldMap();
   private Map<FieldType, String> m_wbsFields = PrimaveraReader.getDefaultWbsFieldMap();
   private Map<FieldType, String> m_taskFields = PrimaveraReader.getDefaultTaskFieldMap();
   private Map<FieldType, String> m_assignmentFields = PrimaveraReader.getDefaultAssignmentFieldMap();
   private Map<FieldType, String> m_aliases = PrimaveraReader.getDefaultAliases();
}