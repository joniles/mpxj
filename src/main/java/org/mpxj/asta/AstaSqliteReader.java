/*
 * file:       AstaSqliteReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       06/06/2016
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.DayType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ResultSetHelper;
import org.mpxj.common.SQLite;
import org.mpxj.reader.AbstractProjectFileReader;

/**
 * This class provides a generic front end to read project data from
 * a SQLite-based Asta PP file.
 */
public class AstaSqliteReader extends AbstractProjectFileReader
{
   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         m_connection = SQLite.createConnection(file, SQLite.dateFormatProperties());
         return read();
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(m_connection);
      }
   }

   /**
    * Read a project from the current data source.
    *
    * @return ProjectFile instance
    */
   public ProjectFile read() throws MPXJException
   {
      ProjectFile project = read(DEFAULT_PROJECT_ID);
      processBaseline(project, DEFAULT_PROJECT_ID);
      return project;
   }

   private ProjectFile read(Integer projectID) throws MPXJException
   {
      try
      {
         m_projectID = projectID;
         m_reader = new AstaReader();
         ProjectFile project = m_reader.getProject();
         addListenersToProject(project);

         processProjectProperties();
         processCalendars();
         processResources();
         processTasks();
         processPredecessors();
         processAssignments();
         processUserDefinedFields();
         processCodeLibraries();
         project.readComplete();

         m_reader = null;

         return project;
      }

      catch (SQLException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * Select the project properties row from the database.
    */
   private void processProjectProperties() throws SQLException
   {
      List<Row> schemaVersionRows = getRows("select * from dodschem");
      List<Row> projectSummaryRows = getRows("select * from project_summary where projid=?", m_projectID);
      List<Row> progressPeriodRows = getRows("select * from progress_period where projid=?", m_projectID);
      List<Row> userSettingsRows = getRows("select * from userr where projid=?", m_projectID);
      Integer schemaVersion = schemaVersionRows.isEmpty() ? null : schemaVersionRows.get(0).getInteger("SCHVER");
      Row projectSummary = projectSummaryRows.isEmpty() ? null : projectSummaryRows.get(0);
      Row userSettings = userSettingsRows.isEmpty() ? null : userSettingsRows.get(0);
      List<Row> progressPeriods = progressPeriodRows.isEmpty() ? null : progressPeriodRows;
      m_reader.processProjectProperties(schemaVersion, projectSummary, userSettings, progressPeriods);
   }

   /**
    * Process calendars.
    */
   private void processCalendars() throws SQLException
   {
      List<Row> rows = getRows("select * from exceptionn");
      Map<Integer, DayType> exceptionTypeMap = m_reader.createExceptionTypeMap(rows);

      rows = getRows("select * from work_pattern");
      Map<Integer, Row> workPatternMap = m_reader.createWorkPatternMap(rows);

      rows = getRows("select id, work_patterns from calendar");
      Map<Integer, List<Row>> workPatternAssignmentMap = createWorkPatternAssignmentMap(rows);

      rows = getRows("select id, exceptions from calendar");
      Map<Integer, List<Row>> exceptionAssignmentMap = createExceptionAssignmentMap(rows);

      rows = getRows("select id, shifts from work_pattern");
      Map<Integer, List<Row>> timeEntryMap = createTimeEntryMap(rows);

      rows = getRows("select * from calendar where projid=? order by id", m_projectID);
      rows = HierarchyHelper.sortHierarchy(rows, r -> r.getInteger("ID"), r -> r.getInteger("CALENDAR"));
      for (Row row : rows)
      {
         m_reader.processCalendar(row, workPatternMap, workPatternAssignmentMap, exceptionAssignmentMap, timeEntryMap, exceptionTypeMap);
      }
   }

   /**
    * Process resources.
    */
   private void processResources() throws SQLException
   {
      List<Row> permanentRows = getRows("select * from permanent_resource where projid=? order by id", m_projectID);
      List<Row> consumableRows = getRows("select * from consumable_resource where projid=? order by id", m_projectID);
      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws SQLException
   {
      List<Row> bars = getRows("select id as barid, * from bar where projid=?", m_projectID);
      List<Row> expandedTasks = getRows("select id as expanded_taskid, * from expanded_task where projid=?", m_projectID);
      List<Row> tasks = getRows("select id as taskid, * from task where projid=?", m_projectID);
      List<Row> milestones = getRows("select id as milestoneid, * from milestone where projid=?", m_projectID);
      List<Row> hammocks = getRows("select id as hammock_taskid, * from hammock_task where projid=?", m_projectID);
      List<Row> completedSections = getRows("select * from task_completed_section where projid=? order by id", m_projectID);

      m_reader.processTasks(bars, expandedTasks, tasks, milestones, hammocks, completedSections);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = getRows("select * from link where projid=? order by id", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws SQLException
   {
      List<Row> allocationRows = getRows("select * from permanent_schedul_allocation where projid=? order by id", m_projectID);
      List<Row> skillRows = getRows("select * from perm_resource_skill where projid=?", m_projectID);
      m_reader.processAssignments(allocationRows, skillRows);
   }

   /**
    * Process user defined fields.
    */
   private void processUserDefinedFields() throws SQLException
   {
      List<Row> definitions = getRows("select * from udf_defn");
      List<Row> data = getRows("select * from udf_data");
      m_reader.processUserDefinedFields(definitions, data);
   }

   /**
    * Retrieve a number of rows matching the supplied query.
    *
    * @param sql query statement
    * @return result set
    */
   private List<Row> getRows(String sql) throws SQLException
   {
      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         try (ResultSet rs = ps.executeQuery())
         {
            List<Row> result = new ArrayList<>();
            Map<String, Integer> meta = populateMetaData(rs);
            while (rs.next())
            {
               result.add(new SqliteResultSetRow(rs, meta));
            }
            return result;
         }
      }
   }

   /**
    * Normally we'd be calling ResultSetHelper.populateMetaData, but we have come
    * across some Asta SQLite databases which use the NTEXT type. These are not
    * recognised by the JDBC driver, so here we're providing an explicit mapping
    * to allow these columns to be treated correctly as text.
    *
    * @param rs ResultSet instance
    * @return map containing column names and types
    */
   private Map<String, Integer> populateMetaData(ResultSet rs) throws SQLException
   {
      Map<String, Integer> map = new HashMap<>();
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount() + 1;
      for (int loop = 1; loop < columnCount; loop++)
      {
         String name = meta.getColumnName(loop);
         String typeName = meta.getColumnTypeName(loop);
         Integer type = "NTEXT".equals(typeName) ? Integer.valueOf(1) : Integer.valueOf(meta.getColumnType(loop));
         map.put(name, type);
      }
      return map;
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
      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         ps.setInt(1, NumberHelper.getInt(var));
         try (ResultSet rs = ps.executeQuery())
         {
            List<Row> result = new ArrayList<>();
            Map<String, Integer> meta = ResultSetHelper.populateMetaData(rs);
            while (rs.next())
            {
               result.add(new SqliteResultSetRow(rs, meta));
            }
            return (result);
         }
      }
   }

   /**
    * Create the work pattern assignment map.
    *
    * @param rows calendar rows
    * @return work pattern assignment map
    */
   private Map<Integer, List<Row>> createWorkPatternAssignmentMap(List<Row> rows)
   {
      Map<Integer, List<Row>> map = new HashMap<>();
      for (Row row : rows)
      {
         Integer calendarID = row.getInteger("ID");
         String workPatterns = row.getString("WORK_PATTERNS");
         map.put(calendarID, createWorkPatternAssignmentRowList(workPatterns));
      }
      return map;
   }

   /**
    * Extract a list of work pattern assignments.
    *
    * @param workPatterns string representation of work pattern assignments
    * @return list of work pattern assignment rows
    */
   private List<Row> createWorkPatternAssignmentRowList(String workPatterns)
   {
      List<Row> list = new ArrayList<>();
      String[] patterns = workPatterns.split("[,:]");
      int index = 1;
      while (index < patterns.length)
      {
         Integer workPattern = Integer.valueOf(patterns[index + 1]);
         LocalDateTime startDate = DatatypeConverter.parseBasicTimestamp(patterns[index + 3]);
         LocalDateTime endDate = DatatypeConverter.parseBasicTimestamp(patterns[index + 4]);

         Map<String, Object> map = new HashMap<>();
         map.put("WORK_PATTERN", workPattern);
         map.put("START_DATE", startDate);
         map.put("END_DATE", endDate);

         list.add(new MapRow(map));

         index += 5;
      }

      return list;
   }

   /**
    * Create the exception assignment map.
    *
    * @param rows calendar rows
    * @return exception assignment map
    */
   private Map<Integer, List<Row>> createExceptionAssignmentMap(List<Row> rows)
   {
      Map<Integer, List<Row>> map = new HashMap<>();
      for (Row row : rows)
      {
         Integer calendarID = row.getInteger("ID");
         String exceptions = row.getString("EXCEPTIONS");
         map.put(calendarID, createExceptionAssignmentRowList(exceptions));
      }
      return map;
   }

   /**
    * Extract a list of exception assignments.
    *
    * @param exceptionData string representation of exception assignments
    * @return list of exception assignment rows
    */
   private List<Row> createExceptionAssignmentRowList(String exceptionData)
   {
      List<Row> list = new ArrayList<>();
      String[] exceptions = exceptionData.split("[,:]");
      int index = 1;
      while (index < exceptions.length)
      {
         LocalDateTime startDate = DatatypeConverter.parseEpochTimestamp(exceptions[index]);
         LocalDateTime endDate = DatatypeConverter.parseEpochTimestamp(exceptions[index + 1]);
         //Integer exceptionTypeID = Integer.valueOf(exceptions[index + 2]);

         Map<String, Object> map = new HashMap<>();
         map.put("START_DATE", startDate);
         map.put("END_DATE", endDate);

         list.add(new MapRow(map));

         index += 3;
      }

      return list;
   }

   /**
    * Create the time entry map.
    *
    * @param rows work pattern rows
    * @return time entry map
    */
   private Map<Integer, List<Row>> createTimeEntryMap(List<Row> rows)
   {
      Map<Integer, List<Row>> map = new HashMap<>();
      for (Row row : rows)
      {
         Integer workPatternID = row.getInteger("ID");
         String shifts = row.getString("SHIFTS");
         map.put(workPatternID, createTimeEntryRowList(shifts));
      }
      return map;
   }

   /**
    * Extract a list of time entries.
    *
    * @param shiftData string representation of time entries
    * @return list of time entry rows
    */
   private List<Row> createTimeEntryRowList(String shiftData)
   {
      List<Row> list = new ArrayList<>();
      String[] shifts = shiftData.split("[,:]");
      int index = 1;
      while (index < shifts.length)
      {
         index += 2;
         int entryCount = Integer.parseInt(shifts[index]);
         index++;

         for (int entryIndex = 0; entryIndex < entryCount; entryIndex++)
         {
            Integer exceptionTypeID = Integer.valueOf(shifts[index]);
            LocalDateTime startTime = DatatypeConverter.parseBasicTime(shifts[index + 1]);
            LocalDateTime endTime = DatatypeConverter.parseBasicTime(shifts[index + 2]);

            Map<String, Object> map = new HashMap<>();
            map.put("START_TIME", startTime);
            map.put("END_TIME", endTime);
            map.put("EXCEPTION", exceptionTypeID);

            list.add(new MapRow(map));

            index += 3;
         }
      }

      return list;
   }

   /**
    * Finds the current baseline ID from the USERR table and attaches this as the project's baseline.
    *
    * @param project parent project
    * @param projectID parent project unique ID
    */
   private void processBaseline(ProjectFile project, Integer projectID) throws MPXJException
   {
      try
      {
         // We don't need the project_summary join, but this ensures that we know the baseline project exists
         List<Row> baseline = getRows("select baseline_summary.baseline_project_id from baseline_summary join userr on userr.projid = baseline_summary.projid and userr.current_baseline_id = baseline_summary.baseline_id join project_summary on project_summary.projid = baseline_summary.baseline_project_id where baseline_summary.projid=?", projectID);
         if (!baseline.isEmpty())
         {
            // Ignore the value we get back if it matches the current project
            Integer baselineProjectID = baseline.get(0).getInteger("BASELINE_PROJECT_ID");
            if (baselineProjectID != null && !baselineProjectID.equals(projectID))
            {
               ProjectFile baselineProject = read(baselineProjectID);

               project.setBaseline(baselineProject);
            }
         }
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to read baseline data", ex);
      }
   }

   private void processCodeLibraries() throws SQLException
   {
      List<Row> types = getRows("select * from code_library where projid=?", m_projectID);
      List<Row> typeValues = getRows("select * from code_library_entry where projid=?", m_projectID);
      List<Row> assignments = getRows("select * from code_library_assignabl_codes where projid=?", m_projectID);

      m_reader.processCodeLibraries(types, typeValues, assignments);
   }

   private AstaReader m_reader;
   private Integer m_projectID;
   private Connection m_connection;

   private static final Integer DEFAULT_PROJECT_ID = Integer.valueOf(0);
}