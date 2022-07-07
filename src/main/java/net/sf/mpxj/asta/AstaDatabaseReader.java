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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
public final class AstaDatabaseReader extends AbstractProjectFileReader
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

         List<Row> rows = getRows("select projid, short_name from project_summary");
         for (Row row : rows)
         {
            Integer id = row.getInteger("PROJID");
            String name = row.getString("SHORT_NAME");
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
         m_reader = new AstaReader();
         ProjectFile project = m_reader.getProject();
         addListenersToProject(project);

         processProjectProperties();
         processCalendars();
         processResources();
         processTasks();
         processPredecessors();
         processAssignments();
         // TODO: custom field support (where is udf_data?)

         m_reader = null;

         return project;
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
         }
      }
   }

   /**
    * Select the project properties row from the database.
    */
   private void processProjectProperties() throws SQLException
   {
      List<Row> projectSummaryRows = getRows("select * from project_summary where projid=?", m_projectID);
      List<Row> progressPeriodRows = getRows("select * from progress_period where projid=?", m_projectID);
      List<Row> userSettingsRows = getRows("select * from userr where projid=?", m_projectID);
      Row projectSummary = projectSummaryRows.isEmpty() ? null : projectSummaryRows.get(0);
      Row userSettings = userSettingsRows.isEmpty() ? null : userSettingsRows.get(0);
      List<Row> progressPeriods = progressPeriodRows.isEmpty() ? null : progressPeriodRows;
      m_reader.processProjectProperties(projectSummary, userSettings, progressPeriods);
   }

   /**
    * Process calendars.
    */
   private void processCalendars() throws SQLException
   {
      List<Row> rows = getRows("select * from exceptionn");
      Map<Integer, DayType> exceptionMap = m_reader.createExceptionTypeMap(rows);

      rows = getRows("select * from work_pattern");
      Map<Integer, Row> workPatternMap = m_reader.createWorkPatternMap(rows);

      rows = getRows("select * from work_pattern_assignment");
      Map<Integer, List<Row>> workPatternAssignmentMap = m_reader.createWorkPatternAssignmentMap(rows);

      rows = sortRows(getRows("select * from exception_assignment"), "EXCEPTION_ASSIGNMENT_ID", "ORDF");
      Map<Integer, List<Row>> exceptionAssignmentMap = m_reader.createExceptionAssignmentMap(rows);

      rows = sortRows(getRows("select * from time_entry"), "TIME_ENTRYID", "ORDF");
      Map<Integer, List<Row>> timeEntryMap = m_reader.createTimeEntryMap(rows);

      rows = sortRows(getRows("select * from calendar where projid=?", m_projectID), "CALENDARID");
      for (Row row : rows)
      {
         m_reader.processCalendar(row, workPatternMap, workPatternAssignmentMap, exceptionAssignmentMap, timeEntryMap, exceptionMap);
      }

      //
      // In theory the code below can be used to establish parent-child relationships between
      // calendars, however the resulting calendars aren't assigned to tasks and resources correctly, so
      // I've left this out for the moment.
      //
      /*
            for (Row row : rows)
            {
               ProjectCalendar child = m_reader.getProject().getCalendarByUniqueID(row.getInteger("CALENDARID"));
               ProjectCalendar parent = m_reader.getProject().getCalendarByUniqueID(row.getInteger("CALENDAR"));
               if (child != null && parent != null)
               {
                  child.setParent(parent);
               }
            }
      */

      //
      // Update unique counters at this point as we will be generating
      // resource calendars, and will need to auto generate IDs
      //
      m_reader.getProject().getProjectConfig().updateUniqueCounters();
   }

   /**
    * Process resources.
    */
   private void processResources() throws SQLException
   {
      List<Row> permanentRows = sortRows(getRows("select * from permanent_resource where projid=?", m_projectID), "PERMANENT_RESOURCEID");
      List<Row> consumableRows = sortRows(getRows("select * from consumable_resource where projid=?", m_projectID), "CONSUMABLE_RESOURCEID");
      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws SQLException
   {
      List<Row> bars = getRows("select * from bar where projid=?", m_projectID);
      List<Row> expandedTasks = getRows("select * from expanded_task where projid=?", m_projectID);
      List<Row> tasks = getRows("select * from task where projid=?", m_projectID);
      List<Row> milestones = getRows("select * from milestone where projid=?", m_projectID);
      m_reader.processTasks(bars, expandedTasks, tasks, milestones);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = sortRows(getRows("select * from link where projid=?", m_projectID), "LINKID");
      List<Row> completedSections = getRows("select * from task_completed_section where projid=?", m_projectID);
      m_reader.processPredecessors(rows, completedSections);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws SQLException
   {
      List<Row> permanentAssignments = getRows("select * from permanent_schedul_allocation inner join perm_resource_skill on permanent_schedul_allocation.allocatiop_of = perm_resource_skill.perm_resource_skillid where permanent_schedul_allocation.projid=? order by permanent_schedul_allocation.permanent_schedul_allocationid", m_projectID);
      m_reader.processAssignments(permanentAssignments);
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

   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         m_connection = getDatabaseConnection(file);
         m_projectID = Integer.valueOf(0);
         return read();
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(m_connection);
      }
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      try
      {
         m_connection = getDatabaseConnection(file);
         List<ProjectFile> result = new ArrayList<>();
         Set<Integer> ids = listProjects().keySet();
         for (Integer id : ids)
         {
            m_projectID = id;
            result.add(read());
         }
         return result;
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(m_connection);
      }
   }

   /**
    * Create and configure a JDBC/ODBC bridge connection.
    *
    * @param file database file to open
    * @return database connection
    */
   private Connection getDatabaseConnection(File file) throws MPXJException
   {
      try
      {
         String url = JdbcOdbcHelper.getMicrosoftAccessJdbcUrl(file);
         Properties props = new Properties();
         props.put("charSet", "Cp1252");
         return DriverManager.getConnection(url, props);
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }
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
               result.add(new MpdResultSetRow(rs, meta));
            }
            return result;
         }
      }
   }

   private List<Row> sortRows(List<Row> rows, String... columnNames)
   {
      Comparator<Row> comparator = Comparator.comparing(r -> Integer.valueOf(r.getInt(columnNames[0])));
      if (columnNames.length > 1)
      {
         for (int index = 1; index < columnNames.length; index++)
         {
            String columnName = columnNames[index];
            comparator = comparator.thenComparing(Comparator.comparing(r -> Integer.valueOf(r.getInt(columnName))));
         }
      }
      rows.sort(comparator);
      return rows;
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

      try (PreparedStatement ps = m_connection.prepareStatement(sql))
      {
         ps.setInt(1, NumberHelper.getInt(var));
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

   private AstaReader m_reader;
   private Integer m_projectID;
   private DataSource m_dataSource;
   private Connection m_connection;
   private boolean m_allocatedConnection;
}