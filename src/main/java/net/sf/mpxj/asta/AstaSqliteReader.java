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

package net.sf.mpxj.asta;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.DayType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ResultSetHelper;
import net.sf.mpxj.common.SQLite;
import net.sf.mpxj.reader.AbstractProjectFileReader;

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
      List<Row> schemaVersionRows = getRows("select* from dodschem");
      List<Row> projectSummaryRows = getRows("select duration as durationhours, project_start as staru, project_end as ene, * from project_summary where projid=?", m_projectID);
      List<Row> progressPeriodRows = getRows("select id as progress_periodid, * from progress_period where projid=?", m_projectID);
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
      List<Row> rows = getRows("select id as exceptionnid, * from exceptionn");
      Map<Integer, DayType> exceptionTypeMap = m_reader.createExceptionTypeMap(rows);

      rows = getRows("select id as work_patternid, name as namn, * from work_pattern");
      Map<Integer, Row> workPatternMap = m_reader.createWorkPatternMap(rows);

      rows = getRows("select id, work_patterns from calendar");
      Map<Integer, List<Row>> workPatternAssignmentMap = createWorkPatternAssignmentMap(rows);

      rows = getRows("select id, exceptions from calendar");
      Map<Integer, List<Row>> exceptionAssignmentMap = createExceptionAssignmentMap(rows);

      rows = getRows("select id, shifts from work_pattern");
      Map<Integer, List<Row>> timeEntryMap = createTimeEntryMap(rows);

      rows = getRows("select id as calendarid, name as namk, * from calendar where projid=? order by id", m_projectID);
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
      List<Row> permanentRows = getRows("select id as permanent_resourceid, name as nase, calendar as calendav, * from permanent_resource where projid=? order by id", m_projectID);
      List<Row> consumableRows = getRows("select id as consumable_resourceid, name as nase, calendar as calendav, * from consumable_resource where projid=? order by id", m_projectID);
      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws SQLException
   {
      List<Row> bars = getRows("select id as barid, bar_start as starv, bar_finish as enf, name as namh, * from bar where projid=?", m_projectID);

      List<Row> expandedTasks = getRows("select id as expanded_taskid, constraint_flag as constrainu, calendar as calendau, * from expanded_task where projid=?", m_projectID);
      List<Row> tasks = getRows("select id as taskid, given_duration as given_durationhours, actual_duration as actual_durationhours, overall_percent_complete as overall_percenv_complete, name as nare, calendar as calendau, linkable_start as starz, linkable_finish as enj, notes as notet, wbs as wbt, natural_order as naturao_order, constraint_flag as constrainu, duration_time_unit as duration_timj_unit, * from task where projid=?", m_projectID);
      List<Row> milestones = getRows("select id as milestoneid, name as nare, calendar as calendau, wbs as wbt, natural_order as naturao_order, constraint_flag as constrainu, * from milestone where projid=?", m_projectID);
      List<Row> hammocks = getRows("select id as hammock_taskid, * from hammock_task where projid=?", m_projectID);

      m_reader.processTasks(bars, expandedTasks, tasks, milestones, hammocks);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = getRows("select id as linkid, start_lag_time as start_lag_timehours, end_lag_time as end_lag_timehours, link_kind as typi, * from link where projid=? order by id", m_projectID);
      List<Row> completedSections = getRows("select id as task_completed_sectionid, * from task_completed_section where projid=? order by id", m_projectID);
      m_reader.processPredecessors(rows, completedSections);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws SQLException
   {
      List<Row> permanentAssignments = getRows("select allocated_to as allocatee_to, player, percent_complete, effort as efforw, permanent_schedul_allocation.id as permanent_schedul_allocationid, linkable_start as starz, linkable_finish as enj, given_allocation, delay as delaahours from permanent_schedul_allocation inner join perm_resource_skill on perm_resource_skill.id = permanent_schedul_allocation.allocation_of and perm_resource_skill.projid=permanent_schedul_allocation.projid where permanent_schedul_allocation.projid=? order by permanent_schedul_allocation.id", m_projectID);
      m_reader.processAssignments(permanentAssignments);
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
            Map<String, Integer> meta = ResultSetHelper.populateMetaData(rs);
            while (rs.next())
            {
               result.add(new SqliteResultSetRow(rs, meta));
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
         map.put("STARU_DATE", startDate);
         map.put("ENE_DATE", endDate);

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
            map.put("EXCEPTIOP", exceptionTypeID);

            list.add(new MapRow(map));

            index += 3;
         }
      }

      return list;
   }

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