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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import net.sf.mpxj.common.DayOfWeekHelper;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Notes;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.WorkContourContainer;
import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.common.ConnectionHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ResultSetHelper;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class PrimaveraDatabaseReader extends AbstractProjectReader
{
   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current database.
    *
    * @return Map instance containing ID and name pairs
    */
   public Map<Integer, String> listProjects() throws MPXJException
   {
      try
      {
         Map<Integer, String> result = new HashMap<>();

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
    */
   public ProjectFile read() throws MPXJException
   {
      try
      {
         m_reader = new PrimaveraReader(m_resourceFields, m_roleFields, m_wbsFields, m_taskFields, m_assignmentFields, m_matchPrimaveraWBS, m_wbsIsFullPath, m_ignoreErrors);
         ProjectFile project = m_reader.getProject();
         addListenersToProject(project);

         processTableNames();
         processAnalytics();
         processUnitsOfMeasure();
         processUserDefinedFields();
         processLocations();
         processProjectProperties();
         processActivityCodes();
         processExpenseCategories();
         processCostAccounts();
         processNotebookTopics();
         processCalendars();
         processResources();
         processRoles();
         processResourceRates();
         processRoleRates();
         processRoleAvailability();
         processTasks();
         processPredecessors();
         processWorkContours();
         processAssignments();
         processExpenseItems();
         processActivitySteps();
         m_reader.rollupValues();

         m_reader = null;
         project.updateStructure();
         project.readComplete();

         return (project);
      }

      catch (SQLException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         if (m_allocatedConnection)
         {
            AutoCloseableHelper.closeQuietly(m_connection);
            m_connection = null;
         }
      }
   }

   /**
    * Convenience method which allows all projects in the database to
    * be read in a single operation.
    *
    * @return list of ProjectFile instances
    */
   public List<ProjectFile> readAll() throws MPXJException
   {
      Map<Integer, String> projects = listProjects();
      List<ProjectFile> result = new ArrayList<>(projects.keySet().size());
      for (Integer id : projects.keySet())
      {
         setProjectID(id.intValue());
         result.add(read());
      }
      return result;
   }

   /**
    * Populate data for analytics.
    */
   private void processAnalytics() throws SQLException
   {
      allocateConnection();

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

      ProjectProperties properties = m_reader.getProject().getProjectProperties();
      properties.setFileApplication("Primavera");
      properties.setFileType(productName);
   }

   /**
    * Select the project properties from the database.
    */
   private void processProjectProperties() throws SQLException
   {
      //
      // Process common attributes
      //
      List<Row> rows = getRows("select * from " + m_schema + "project where proj_id=?", m_projectID);
      m_reader.processProjectProperties(m_projectID, rows);

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
         ph.setMinutesPerDay(Integer.valueOf((int) (row.getDouble("day_hr_cnt").doubleValue() * 60)));
         ph.setMinutesPerWeek(Integer.valueOf((int) (row.getDouble("week_hr_cnt").doubleValue() * 60)));
         ph.setMinutesPerMonth(Integer.valueOf((int) (row.getDouble("month_hr_cnt").doubleValue() * 60)));
         ph.setMinutesPerYear(Integer.valueOf((int) (row.getDouble("year_hr_cnt").doubleValue() * 60)));
         ph.setWeekStartDay(DayOfWeekHelper.getInstance(row.getInt("week_start_day_num")));

         processDefaultCurrency(row.getInteger("curr_id"));
      }

      processSchedulingProjectProperties();
   }

   /**
    * Select the locations from the database.
    */
   private void processLocations() throws SQLException
   {
      // Locations are a relative new feature - check for the presence of the table
      if (m_tableNames.contains("LOCATION"))
      {
         m_reader.processLocations(getRows("select * from " + m_schema + "location"));
      }
   }

   /**
    * Select the expense categories from the database.
    */
   private void processExpenseCategories() throws SQLException
   {
      m_reader.processExpenseCategories(getRows("select * from " + m_schema + "costtype"));
   }

   /**
    * Select the expense items from the database.
    */
   private void processExpenseItems() throws SQLException
   {
      m_reader.processExpenseItems(getRows("select * from " + m_schema + "projcost where proj_id=?", m_projectID));
   }

   /**
    * Select the activity steps from the database.
    */
   private void processActivitySteps() throws SQLException
   {
      m_reader.processActivitySteps(getRows("select * from " + m_schema + "taskproc where proj_id=?", m_projectID));
   }

   /**
    * Select the cost accounts from the database.
    */
   private void processCostAccounts() throws SQLException
   {
      m_reader.processCostAccounts(getRows("select * from " + m_schema + "account"));
   }

   /**
    * Process units of measure.
    */
   private void processUnitsOfMeasure() throws SQLException
   {
      m_reader.processUnitsOfMeasure(getRows("select * from " + m_schema + "umeasure"));
   }

   /**
    * Process notebook topics.
    */
   private void processNotebookTopics() throws SQLException
   {
      m_reader.processNotebookTopics(getRows("select * from " + m_schema + "memotype"));
   }

   /**
    * Process activity code data.
    */
   private void processActivityCodes() throws SQLException
   {
      List<Row> types = getRows("select * from " + m_schema + "actvtype where actv_code_type_id in (select distinct actv_code_type_id from taskactv where proj_id=?)", m_projectID);
      List<Row> typeValues = getRows("select * from " + m_schema + "actvcode where actv_code_id in (select distinct actv_code_id from taskactv where proj_id=?)", m_projectID);
      List<Row> assignments = getRows("select * from " + m_schema + "taskactv where proj_id=?", m_projectID);
      m_reader.processActivityCodes(types, typeValues, assignments);
   }

   /**
    * Process user defined fields.
    */
   private void processUserDefinedFields() throws SQLException
   {
      List<Row> fields = getRows("select * from " + m_schema + "udftype");
      List<Row> values = getRows("select * from " + m_schema + "udfvalue where proj_id=? or proj_id is null", m_projectID);
      m_reader.processUserDefinedFields(fields, values);
   }

   /**
    * Process the scheduling project property from PROJPROP. This is represented
    * as the schedoptions table in an XER file.
    */
   private void processSchedulingProjectProperties() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "projprop where proj_id=? and prop_name='scheduling'", m_projectID);
      if (!rows.isEmpty())
      {
         StructuredTextRecord record = new StructuredTextParser().parse(rows.get(0).getString("prop_value"));
         m_reader.processScheduleOptions(new MapRow(new HashMap<>(record.getAttributes()), false));
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
    */
   private void processResources() throws SQLException
   {
      // TODO: handle exporting parent resources
      List<Row> rows = getRows("select * from " + m_schema + "rsrc where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by rsrc_seq_num", m_projectID);
      m_reader.processResources(rows);
   }

   /**
    * Process roles.
    */
   private void processRoles() throws SQLException
   {
      // TODO: handle exporting parent roles
      List<Row> rows = getRows("select * from " + m_schema + "roles where delete_date is null and role_id in (select role_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by seq_num", m_projectID);
      m_reader.processRoles(rows);
   }

   /**
    * Process resource rates.
    */
   private void processResourceRates() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "rsrcrate where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by rsrc_rate_id", m_projectID);
      m_reader.processResourceRates(rows);
   }

   /**
    * Process role rates.
    */
   private void processRoleRates() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "rolerate where delete_date is null and role_id in (select role_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by role_rate_id", m_projectID);
      m_reader.processRoleRates(rows);
   }

   /**
    * Process role availability.
    */
   private void processRoleAvailability() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "rolelimit where delete_date is null and role_id in (select role_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by rolelimit_id", m_projectID);
      m_reader.processRoleAvailability(rows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws SQLException
   {
      List<Row> wbs = getRows("select * from " + m_schema + "projwbs where proj_id=? and delete_date is null order by parent_wbs_id,seq_num", m_projectID);
      List<Row> tasks = getRows("select * from " + m_schema + "task where proj_id=? and delete_date is null", m_projectID);
      Map<Integer, Notes> wbsNotes = m_reader.getNotes(getRows("select * from " + m_schema + "wbsmemo where proj_id=?", m_projectID), "wbs_memo_id", "wbs_id", "wbs_memo");
      Map<Integer, Notes> taskNotes = m_reader.getNotes(getRows("select * from " + m_schema + "taskmemo where proj_id=?", m_projectID), "memo_id", "task_id", "task_memo");

      m_reader.processTasks(wbs, tasks, wbsNotes, taskNotes);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "taskpred where proj_id=? and delete_date is null", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process calendars.
    */
   private void processCalendars() throws SQLException
   {
      List<Row> rows = getRows("select * from " + m_schema + "calendar where (proj_id is null or proj_id=?) and delete_date is null", m_projectID);
      m_reader.processCalendars(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws SQLException
   {
      processWorkContours();
      List<Row> rows = getRows("select * from " + m_schema + "taskrsrc where proj_id=? and delete_date is null", m_projectID);
      m_reader.processAssignments(rows);
   }

   /**
    * Process resource curves.
    */
   private void processWorkContours() throws SQLException
   {
      WorkContourContainer contours = m_reader.getProject().getWorkContours();
      List<Row> rows = getRows("select * from " + m_schema + "rsrccurv");
      for (Row row : rows)
      {
         try
         {
            Integer id = row.getInteger("curv_id");
            if (contours.getByUniqueID(id) != null)
            {
               continue;
            }
            double[] values = new StructuredTextParser().parse(row.getString("curv_data")).getChildren().stream().mapToDouble(r -> Double.parseDouble(r.getAttribute("PctUsage"))).toArray();
            contours.add(new WorkContour(id, row.getString("curv_name"), row.getBoolean("default_flag"), values));
         }

         catch (Exception ex)
         {
            if (m_ignoreErrors)
            {
               // Skip any curves we can't read
               m_reader.getProject().addIgnoredError(ex);
            }
            else
            {
               throw ex;
            }
         }
      }
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

   @Override public ProjectFile read(String fileName)
   {
      throw new UnsupportedOperationException();
   }

   @Override public List<ProjectFile> readAll(String fileName)
   {
      throw new UnsupportedOperationException();
   }

   @Override public ProjectFile read(File file)
   {
      throw new UnsupportedOperationException();
   }

   @Override public List<ProjectFile> readAll(File file)
   {
      throw new UnsupportedOperationException();
   }

   @Override public ProjectFile read(InputStream inputStream)
   {
      throw new UnsupportedOperationException();
   }

   @Override public List<ProjectFile> readAll(InputStream inputStream)
   {
      throw new UnsupportedOperationException();
   }

   private void processTableNames() throws SQLException
   {
      allocateConnection();
      m_tableNames = ConnectionHelper.getTableNames(m_connection);
   }

   /**
    * Retrieve a number of rows matching the supplied query.
    *
    * @param sql query statement
    * @return result set
    */
   private List<Row> getRows(String sql) throws SQLException
   {
      allocateConnection();

      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         try (ResultSet rs = ps.executeQuery())
         {
            List<Row> result = new ArrayList<>();
            Map<String, Integer> meta = ResultSetHelper.populateMetaData(rs);
            while (rs.next())
            {
               result.add(new ResultSetRow(rs, meta));
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
    * @param var bind variable value
    * @return result set
    */
   private List<Row> getRows(String sql, Integer var) throws SQLException
   {
      allocateConnection();

      List<Row> result = new ArrayList<>();

      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         ps.setInt(1, NumberHelper.getInt(var));
         try (ResultSet rs = ps.executeQuery())
         {
            Map<String, Integer> meta = ResultSetHelper.populateMetaData(rs);
            while (rs.next())
            {
               result.add(new ResultSetRow(rs, meta));
            }
         }
      }
      return (result);
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
   public Map<FieldType, String> getRoleFieldMap()
   {
      return m_roleFields;
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
   public Map<FieldType, String> getActivityFieldMap()
   {
      return m_taskFields;
   }

   /**
    * Customise the data retrieved by this reader by modifying the contents of this map.
    *
    * @return Primavera field name to MPXJ field type map
    */
   public Map<FieldType, String> getAssignmentFieldMap()
   {
      return m_assignmentFields;
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

   /**
    * Returns true if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @return true if WBS attribute is a hierarchy
    */
   public boolean getWbsIsFullPath()
   {
      return m_wbsIsFullPath;
   }

   /**
    * Sets a flag indicating if the WBS attribute of a summary task
    * contains a dot separated list representing the WBS hierarchy.
    *
    * @param wbsIsFullPath true if WBS attribute is a hierarchy
    */
   public void setWbsIsFullPath(boolean wbsIsFullPath)
   {
      m_wbsIsFullPath = wbsIsFullPath;
   }

   /**
    * Set a flag to determine if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @param ignoreErrors pass true to ignore errors
    */
   public void setIgnoreErrors(boolean ignoreErrors)
   {
      m_ignoreErrors = ignoreErrors;
   }

   /**
    * Retrieve the flag which determines if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @return true if datatype parse errors are ignored
    */
   public boolean getIgnoreErrors()
   {
      return m_ignoreErrors;
   }

   private PrimaveraReader m_reader;
   private Integer m_projectID;
   private String m_schema = "";
   private DataSource m_dataSource;
   private Connection m_connection;
   private boolean m_allocatedConnection;
   private boolean m_matchPrimaveraWBS = true;
   private boolean m_wbsIsFullPath = true;
   private boolean m_ignoreErrors = true;
   private Set<String> m_tableNames;

   private final Map<FieldType, String> m_resourceFields = PrimaveraReader.getDefaultResourceFieldMap();
   private final Map<FieldType, String> m_roleFields = PrimaveraReader.getDefaultRoleFieldMap();
   private final Map<FieldType, String> m_wbsFields = PrimaveraReader.getDefaultWbsFieldMap();
   private final Map<FieldType, String> m_taskFields = PrimaveraReader.getDefaultTaskFieldMap();
   private final Map<FieldType, String> m_assignmentFields = PrimaveraReader.getDefaultAssignmentFieldMap();
}