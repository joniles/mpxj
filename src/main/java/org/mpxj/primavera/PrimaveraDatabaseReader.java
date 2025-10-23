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

package org.mpxj.primavera;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mpxj.EPS;
import org.mpxj.EpsNode;
import org.mpxj.EpsProjectNode;
import org.mpxj.ProjectContext;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.Notes;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.WorkContour;
import org.mpxj.WorkContourContainer;
import org.mpxj.reader.AbstractProjectReader;

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

         List<Row> rows = m_database.getRows("select proj_id, proj_short_name from " + m_schema + "project where delete_date is null");
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
    * Retrieve an instance of the EPS class, allowing the hierarchy of EpsNode
    * and EpsProjectNodes to be traversed.
    *
    * @return EPS instance
    */
   public EPS listEps() throws MPXJException
   {
      try
      {
         List<Row> rows = m_database.getRows("select project.project_flag, projwbs.* from " + m_schema + "projwbs join " + m_schema + "project on project.proj_id = projwbs.proj_id where proj_node_flag='Y' order by seq_num");
         return processEps(rows);
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
      ProjectContext context = new PrimaveraDatabaseContextReader(m_database, m_schema, m_ignoreErrors, m_projectID).read();
      ProjectFile result = read(context);

      //
      // We've used Primavera's unique ID values for the calendars we've read so far.
      // At this point any new calendars we create must be auto numbered. We also need to
      // ensure that the auto numbering starts from an appropriate value.
      //
      context.getProjectConfig().setAutoCalendarUniqueID(true);

      return result;
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
      List<ProjectFile> result = new ArrayList<>(projects.size());
      ProjectContext context = new PrimaveraDatabaseContextReader(m_database, m_schema, m_ignoreErrors, null).read();
      for (Integer id : projects.keySet())
      {
         setProjectID(id.intValue());
         result.add(read(context));
      }

      //
      // We've used Primavera's unique ID values for the calendars we've read so far.
      // At this point any new calendars we create must be auto numbered. We also need to
      // ensure that the auto numbering starts from an appropriate value.
      //
      context.getProjectConfig().setAutoCalendarUniqueID(true);

      return result;
   }

   /**
    * Read a project from the current data source.
    *
    * @param context shared data to use when reading this project
    * @return ProjectFile instance
    */
   private ProjectFile read(ProjectContext context) throws MPXJException
   {
      try
      {
         m_reader = new PrimaveraReader(context, m_resourceFields, m_roleFields, m_wbsFields, m_taskFields, m_assignmentFields, m_matchPrimaveraWBS, m_wbsIsFullPath, m_ignoreErrors);

         ProjectFile project = m_reader.getProject();
         addListenersToProject(project);
         processAnalytics();
         project.getProjectProperties().setUniqueID(m_projectID);

         processActivityCodeAssignments();
         processResourceCodeAssignments();
         processRoleCodeAssignments();
         processResourceAssignmentCodeAssignments();
         processUdfValues();
         processCalendars();
         processResources();
         processRoles();
         processRoleAssignments();
         processResourceRates();
         processRoleRates();
         processRoleAvailability();

         processProjectProperties();
         processTasks();
         processPredecessors();
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
         m_database.close();
      }
   }

   /**
    * Populate data for analytics.
    */
   private void processAnalytics() throws SQLException
   {
      ProjectProperties properties = m_reader.getProject().getProjectProperties();
      properties.setFileApplication("Primavera");
      properties.setFileType(m_database.getProductName());
   }

   /**
    * Select the project properties from the database.
    */
   private void processProjectProperties() throws SQLException
   {
      //
      // Process common attributes
      //
      List<Row> rows = m_database.getRows("select * from " + m_schema + "project where proj_id=?", m_projectID);
      m_reader.processProjectProperties(rows);

      rows = m_database.getRows("select * from " + m_schema + "projpcat where proj_id=?", m_projectID);
      m_reader.processProjectCodeAssignments(rows);

      //
      // Process PMDB-specific attributes
      //
      rows = m_database.getRows("select * from " + m_schema + "prefer where prefer.delete_date is null");
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
    * Select the expense items from the database.
    */
   private void processExpenseItems() throws SQLException
   {
      m_reader.processExpenseItems(m_database.getRows("select * from " + m_schema + "projcost where proj_id=?", m_projectID));
   }

   /**
    * Select the activity steps from the database.
    */
   private void processActivitySteps() throws SQLException
   {
      m_reader.processActivitySteps(m_database.getRows("select * from " + m_schema + "taskproc where proj_id=?", m_projectID));
   }

   /**
    * Process activity code assignments.
    */
   private void processActivityCodeAssignments() throws SQLException
   {
      if (m_database.hasTable("TASKACTV"))
      {
         List<Row> assignments = m_database.getRows("select * from " + m_schema + "taskactv where proj_id=?", m_projectID);
         m_reader.processActivityCodeAssignments(assignments);
      }
   }

   /**
    * Process resource code assignments.
    */
   private void processResourceCodeAssignments() throws SQLException
   {
      if (m_database.hasTable("RSRCRCAT"))
      {
         List<Row> assignments = m_database.getRows("select * from " + m_schema + "rsrcrcat where rsrc_id in (select distinct rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null)", m_projectID);
         m_reader.processResourceCodeAssignments(assignments);
      }
   }

   /**
    * Process role code assignments.
    */
   private void processRoleCodeAssignments() throws SQLException
   {
      if (m_database.hasTable("ROLERCAT"))
      {
         List<Row> assignments = m_database.getRows("select * from " + m_schema + "rolercat where role_id in (select distinct role_id from " + m_schema + "rsrcrole where delete_date is null and rsrc_id in (select distinct rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null))", m_projectID);
         m_reader.processRoleCodeAssignments(assignments);
      }
   }

   /**
    * Process resource assignment code assignments.
    */
   private void processResourceAssignmentCodeAssignments() throws SQLException
   {
      if (m_database.hasTable("ASGNMNTACAT"))
      {
         List<Row> assignments = m_database.getRows("select * from " + m_schema + "asgnmntacat where proj_id=?", m_projectID);
         m_reader.processResourceAssignmentCodeAssignments(assignments);
      }
   }

   /**
    * Process user defined field values.
    */
   private void processUdfValues() throws SQLException
   {
      List<Row> values = m_database.getRows("select * from " + m_schema + "udfvalue where proj_id=? or proj_id is null", m_projectID);
      m_reader.processUdfValues(values);
   }

   /**
    * Process the scheduling project property from PROJPROP. This is represented
    * as the schedoptions table in an XER file.
    */
   private void processSchedulingProjectProperties() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "projprop where proj_id=? and prop_name='scheduling'", m_projectID);
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
      List<Row> rows = m_database.getRows("select * from " + m_schema + "currtype where curr_id=?", currencyID);
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
      List<Row> rows = m_database.getRows("select * from " + m_schema + "rsrc where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by rsrc_seq_num", m_projectID);
      m_reader.processResources(rows);
   }

   /**
    * Process roles.
    */
   private void processRoles() throws SQLException
   {
      // TODO: handle exporting parent roles
      List<Row> rows = m_database.getRows("select * from " + m_schema + "roles where delete_date is null and role_id in (select distinct role_id from " + m_schema + "taskrsrc where proj_id=? and delete_date is null union select distinct role_id from " + m_schema + "rsrc where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc where proj_id=? and delete_date is null)) order by seq_num", m_projectID, m_projectID);
      m_reader.processRoles(rows);
   }

   /**
    * Process role assignments.
    */
   private void processRoleAssignments() throws SQLException
   {
      if (m_database.hasTable("RSRCROLE"))
      {
         List<Row> rows = m_database.getRows("select * from " + m_schema + "rsrcrole where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null)", m_projectID);
         m_reader.processRoleAssignments(rows);
      }
   }

   /**
    * Process resource rates.
    */
   private void processResourceRates() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "rsrcrate where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by rsrc_rate_id", m_projectID);
      m_reader.processResourceRates(rows);
   }

   /**
    * Process role rates.
    */
   private void processRoleRates() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "rolerate where delete_date is null and role_id in (select role_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by role_rate_id", m_projectID);
      m_reader.processRoleRates(rows);
   }

   /**
    * Process role availability.
    */
   private void processRoleAvailability() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "rolelimit where delete_date is null and role_id in (select role_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null) order by rolelimit_id", m_projectID);
      m_reader.processRoleAvailability(rows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws SQLException
   {
      List<Row> wbs = m_database.getRows("select * from " + m_schema + "projwbs where proj_id=? and delete_date is null order by parent_wbs_id,seq_num", m_projectID);
      List<Row> tasks = m_database.getRows("select * from " + m_schema + "task where proj_id=? and delete_date is null", m_projectID);
      Map<Integer, Notes> wbsNotes = m_reader.getNotes(m_database.getRows("select * from " + m_schema + "wbsmemo where proj_id=?", m_projectID), "wbs_memo_id", "wbs_id", "wbs_memo");
      Map<Integer, Notes> taskNotes = m_reader.getNotes(m_database.getRows("select * from " + m_schema + "taskmemo where proj_id=?", m_projectID), "memo_id", "task_id", "task_memo");

      m_reader.processTasks(wbs, tasks, wbsNotes, taskNotes);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "taskpred where proj_id=? and delete_date is null", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process calendars.
    */
   private void processCalendars() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "calendar where (proj_id is null or proj_id=?) and delete_date is null", m_projectID);
      m_reader.processCalendars(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "taskrsrc where proj_id=? and delete_date is null", m_projectID);
      m_reader.processAssignments(rows);
   }

   /**
    * Process resource curves.
    */
   private void processWorkContours() throws SQLException
   {
      WorkContourContainer contours = m_reader.getProject().getWorkContours();
      List<Row> rows = m_database.getRows("select * from " + m_schema + "rsrccurv");
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
      m_database = new PrimaveraDatabaseConnection(dataSource);
   }

   /**
    * Sets the connection. A DataSource or a Connection can be supplied
    * to this class to allow connection to the database.
    *
    * @param connection database connection
    */
   public void setConnection(Connection connection)
   {
      m_database = new PrimaveraDatabaseConnection(connection);
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

   /**
    * Create an EPS instance and add nodes to it
    * using the supplied rows.
    *
    * @param rows EPS data
    * @return EPS instance
    */
   private EPS processEps(List<Row> rows)
   {
      EPS eps = new EPS();
      rows.forEach(r -> addEpsNode(eps, r));
      return eps;
   }

   /**
    * Determine if we have a project on an EPS node and add the
    * appropriate node type to the EPS.
    *
    * @param eps EPS instance
    * @param row EPS data
    */
   private void addEpsNode(EPS eps, Row row)
   {
      if (row.getBoolean("project_flag"))
      {
         addEpsProjectNode(eps, row);
      }
      else
      {
         addEpsChildNode(eps, row);
      }
   }

   /**
    * Add an EPS node to the EPS.
    *
    * @param eps EPS instance
    * @param row node data
    */
   private void addEpsChildNode(EPS eps, Row row)
   {
      new EpsNode(eps,
         row.getInteger("wbs_id"),
         row.getInteger("parent_wbs_id"),
         row.getString("wbs_name"),
         row.getString("wbs_short_name"));
   }

   /**
    * Add an EPS project node to the EPS.
    *
    * @param eps EPS instance
    * @param row node data
    */
   private void addEpsProjectNode(EPS eps, Row row)
   {
      new EpsProjectNode(eps,
         row.getInteger("proj_id"),
         row.getInteger("parent_wbs_id"),
         row.getString("wbs_short_name"),
         row.getString("wbs_name"));
   }

   private PrimaveraReader m_reader;
   private Integer m_projectID;
   private String m_schema = "";
   private boolean m_matchPrimaveraWBS = true;
   private boolean m_wbsIsFullPath = true;
   private boolean m_ignoreErrors = true;
   private PrimaveraDatabaseConnection m_database ;

   private final Map<FieldType, String> m_resourceFields = PrimaveraReader.getDefaultResourceFieldMap();
   private final Map<FieldType, String> m_roleFields = PrimaveraReader.getDefaultRoleFieldMap();
   private final Map<FieldType, String> m_wbsFields = PrimaveraReader.getDefaultWbsFieldMap();
   private final Map<FieldType, String> m_taskFields = PrimaveraReader.getDefaultTaskFieldMap();
   private final Map<FieldType, String> m_assignmentFields = PrimaveraReader.getDefaultAssignmentFieldMap();
}