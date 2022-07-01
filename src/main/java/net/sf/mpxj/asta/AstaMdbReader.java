/*
 * file:       AstaMdbReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       01/07/2022
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import net.sf.mpxj.DayType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.JdbcOdbcHelper;
import net.sf.mpxj.reader.AbstractProjectFileReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class AstaMdbReader extends AbstractProjectFileReader
{
   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current database.
    *
    * @return Map instance containing ID and name pairs
    */
   public Map<Integer, String> listProjects(File file) throws MPXJException
   {
      try
      {
         openDatabase(file);

         Map<Integer, String> result = new HashMap<>();

         List<Row> rows = getRows("project_summary");
         for (Row row : rows)
         {
            Integer id = row.getInteger("PROJID");
            String name = row.getString("SHORT_NAME");
            result.put(id, name);
         }

         return result;
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         closeDatabase();
      }
   }

   /**
    * Read a project from the current data source.
    *
    * @return ProjectFile instance
    */
   private ProjectFile read() throws MPXJException
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

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         closeDatabase();
      }
   }

   /**
    * Select the project properties row from the database.
    */
   private void processProjectProperties() throws IOException
   {
      Map<String, Integer> keys = Collections.singletonMap("PROJID", m_projectID);
      List<Row> projectSummaryRows = getRows("project_summary", keys);
      List<Row> progressPeriodRows = getRows("progress_period", keys);
      List<Row> userSettingsRows = getRows("userr", keys);
      Row projectSummary = projectSummaryRows.isEmpty() ? null : projectSummaryRows.get(0);
      Row userSettings = userSettingsRows.isEmpty() ? null : userSettingsRows.get(0);
      List<Row> progressPeriods = progressPeriodRows.isEmpty() ? null : progressPeriodRows;
      m_reader.processProjectProperties(projectSummary, userSettings, progressPeriods);
   }

   /**
    * Process calendars.
    */
   private void processCalendars() throws IOException
   {
      List<Row> rows = getRows("exceptionn");
      Map<Integer, DayType> exceptionMap = m_reader.createExceptionTypeMap(rows);

      rows = getRows("work_pattern");
      Map<Integer, Row> workPatternMap = m_reader.createWorkPatternMap(rows);

      rows = getRows("work_pattern_assignment");
      Map<Integer, List<Row>> workPatternAssignmentMap = m_reader.createWorkPatternAssignmentMap(rows);

      //  TODO: order by exception_assignmentid, ordf
      rows = getRows("exception_assignment");
      Map<Integer, List<Row>> exceptionAssignmentMap = m_reader.createExceptionAssignmentMap(rows);

      // TODO: time_entryid, ordf
      rows = getRows("time_entry");
      Map<Integer, List<Row>> timeEntryMap = m_reader.createTimeEntryMap(rows);

      rows = getRows("calendar", m_projectKey);
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
   private void processResources() throws IOException
   {
      // TODO: order by permanent_resourceid
      List<Row> permanentRows = getRows("permanent_resource", m_projectKey);

      // TPODO: order by consumable_resourceid
      List<Row> consumableRows = getRows("consumable_resource", m_projectKey);
      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws IOException
   {
      List<Row> bars = getRows("bar", m_projectKey);
      List<Row> expandedTasks = getRows("expanded_task", m_projectKey);
      List<Row> tasks = getRows("task", m_projectKey);
      List<Row> milestones = getRows("milestone", m_projectKey);
      m_reader.processTasks(bars, expandedTasks, tasks, milestones);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws IOException
   {
      // TODO: order by linkid
      List<Row> rows = getRows("link", m_projectKey);
      List<Row> completedSections = getRows("task_completed_section", m_projectKey);
      m_reader.processPredecessors(rows, completedSections);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws IOException
   {
//      List<Row> permanentAssignments = getRows("select * from permanent_schedul_allocation inner join perm_resource_skill on permanent_schedul_allocation.allocatiop_of = perm_resource_skill.perm_resource_skillid where permanent_schedul_allocation.projid=? order by permanent_schedul_allocation.permanent_schedul_allocationid", m_projectID);
//      m_reader.processAssignments(permanentAssignments);
   }

   /**
    * Set the ID of the project to be read.
    *
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectID = Integer.valueOf(projectID);
      m_projectKey = Collections.singletonMap("proj_id", m_projectID);
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         openDatabase(file);
         m_projectID = Integer.valueOf(0);
         return read();
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         closeDatabase();
      }
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      try
      {
         List<ProjectFile> result = new ArrayList<>();
         Set<Integer> ids = listProjects(file).keySet();

         openDatabase(file);
         for (Integer id : ids)
         {
            m_projectID = id;
            result.add(read());
         }
         return result;
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         closeDatabase();
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
    * @param tableName table name
    * @return result set
    */
   private List<Row> getRows(String tableName) throws IOException
   {
      openDatabase();

      List<Row> result = new ArrayList<>();
      Table table = m_database.getTable(tableName);
      List<? extends Column> columns = table.getColumns();

      for (com.healthmarketscience.jackcess.Row row : table)
      {
         result.add(new JackcessResultSetRow(row, columns));
      }

      return result;
   }

   /**
    * Retrieve a number of rows matching the supplied query
    * which takes a single parameter.
    *
    * @param tableName table name
    * @param keys where clause keys
    * @return result set
    */
   private List<Row> getRows(String tableName, Map<String, Integer> keys) throws IOException
   {
      openDatabase();

      List<Row> result = new ArrayList<>();
      Table table = m_database.getTable(tableName);
      List<? extends Column> columns = table.getColumns();
      Cursor cursor = CursorBuilder.createCursor(table);
      if (cursor.findFirstRow(keys))
      {
         result.add(new JackcessResultSetRow(cursor.getCurrentRow(), columns));
         while(cursor.findNextRow(keys))
         {
            result.add(new JackcessResultSetRow(cursor.getCurrentRow(), columns));
         }
      }

      return result;
   }

   /**
    * Allocates a database connection.
    */
   private void openDatabase() throws IOException
   {
      if (m_database == null)
      {
         m_database = DatabaseBuilder.open(m_databaseFile);
      }
   }

   private void openDatabase(File file) throws IOException
   {
      m_databaseFile = file;
      m_database = DatabaseBuilder.open(m_databaseFile);
   }

   private void closeDatabase()
   {
      try
      {
         if (m_database != null)
         {
            m_database.close();
            m_database = null;
         }
      }

      catch (IOException ex)
      {
         // Ignore errors closing the database
      }
   }

   private AstaReader m_reader;
   private Integer m_projectID;
   private Map<String, Integer> m_projectKey;
   private File m_databaseFile;
   private Database m_database;
}