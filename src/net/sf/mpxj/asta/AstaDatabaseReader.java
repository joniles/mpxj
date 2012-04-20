/*
 * file:       AstaDatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       07/04/2011
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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class AstaDatabaseReader implements ProjectReader
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

         List<Row> rows = getRows("select projid, short_name from project_summary");
         for (Row row : rows)
         {
            Integer id = row.getInteger("projid");
            String name = row.getString("short_name");
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
         m_reader = new AstaReader();
         ProjectFile project = m_reader.getProject();
         project.addProjectListeners(m_projectListeners);

         processProjectHeader();
         processCalendars();
         processResources();
         processTasks();
         processPredecessors();
         processAssignments();

         m_reader = null;

         updateStructure(project);

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
      List<Row> rows = getRows("select * from project_summary where projid=?", m_projectID);
      if (rows.isEmpty() == false)
      {
         m_reader.processProjectHeader(rows.get(0));
      }
   }

   /**
    * Process calendars.
    * 
    * @throws SQLException
    */
   private void processCalendars() throws SQLException
   {
      Map<Integer, DayType> exceptionMap = createExceptionMap();
      List<Row> rows = getRows("select * from calendar where projid=?", m_projectID);
      for (Row row : rows)
      {
         processCalendar(row, exceptionMap);
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
      m_reader.getProject().updateUniqueCounters();
   }

   /**
    * Process resources.
    * 
    * @throws SQLException
    */
   private void processResources() throws SQLException
   {
      List<Row> permanentRows = getRows("select * from permanent_resource inner join perm_resource_skill on permanent_resource.permanent_resourceid = perm_resource_skill.player where permanent_resource.projid=?", m_projectID);
      List<Row> consumableRows = getRows("select * from consumable_resource where projid=?", m_projectID);
      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    * 
    * @throws SQLException
    */
   private void processTasks() throws SQLException
   {
      List<Row> bars = getRows("select * from bar inner join expanded_task on bar.expanded_task = expanded_task.expanded_taskid where bar.projid=? and starv is not null order by natural_order", m_projectID);
      List<Row> tasks = getRows("select *  from task where projid=? order by wbt, naturao_order", m_projectID);
      List<Row> milestones = getRows("select * from milestone where projid=?", m_projectID);
      m_reader.processTasks(bars, tasks, milestones);
   }

   /**
    * Process predecessors.
    * 
    * @throws SQLException
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = getRows("select * from link where projid=?", m_projectID);
      m_reader.processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    * 
    * @throws SQLException
    */
   private void processAssignments() throws SQLException
   {
      List<Row> permanentAssignments = getRows("select * from permanent_schedul_allocation inner join perm_resource_skill on permanent_schedul_allocation.allocatiop_of = perm_resource_skill.perm_resource_skillid where permanent_schedul_allocation.projid=?", m_projectID);
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

   /**
    * This is a convenience method which reads the first project
    * from the named Asta MDB file using the JDBC-ODBC bridge driver.
    * 
    * @param accessDatabaseFileName access database file name
    * @return ProjectFile instance
    * @throws MPXJException
    */
   @Override public ProjectFile read(String accessDatabaseFileName) throws MPXJException
   {
      try
      {
         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
         String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + accessDatabaseFileName;
         m_connection = DriverManager.getConnection(url);
         m_projectID = Integer.valueOf(0);
         return (read());
      }

      catch (ClassNotFoundException ex)
      {
         throw new MPXJException("Failed to load JDBC driver", ex);
      }

      catch (SQLException ex)
      {
         throw new MPXJException("Failed to create connection", ex);
      }

      finally
      {
         if (m_connection != null)
         {
            try
            {
               m_connection.close();
            }

            catch (SQLException ex)
            {
               // silently ignore exceptions when closing connection
            }
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(File file) throws MPXJException
   {
      return (read(file.getAbsolutePath()));
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
    * Retrieve a number of rows matching the supplied query 
    * which takes two parameters.
    * 
    * @param sql query statement
    * @param var1 bind variable value
    * @param var2 bind variable value 
    * @return result set
    * @throws SQLException
    */
   private List<Row> getRows(String sql, Integer var1, Integer var2) throws SQLException
   {
      allocateConnection();

      try
      {
         List<Row> result = new LinkedList<Row>();

         m_ps = m_connection.prepareStatement(sql);
         m_ps.setInt(1, NumberUtility.getInt(var1));
         m_ps.setInt(2, NumberUtility.getInt(var2));
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

   /**
    * Creates a map used to translate between exception ID values
    * and the working/non-working enum values.
    * 
    * @return Map instance
    * @throws SQLException
    */
   private Map<Integer, DayType> createExceptionMap() throws SQLException
   {
      Map<Integer, DayType> map = new HashMap<Integer, DayType>();
      List<Row> rows = getRows("select * from exceptionn");
      for (Row row : rows)
      {
         Integer id = row.getInteger("EXCEPTIONNID");
         DayType result;

         switch (row.getInt("UNIQUE_BIT_FIELD"))
         {
            case 8: // Working
            case 32: // Overtime
            case 128: //Weekend Working
            {
               result = DayType.WORKING;
               break;
            }

            case 4: // Non Working            
            case 16: // Holiday                       
            case 64: // Weather            
            case -2147483648: // Weekend
            default:
            {
               result = DayType.NON_WORKING;
               break;
            }
         }

         map.put(id, result);
      }
      return map;
   }

   /**
    * Process an individual calendar.
    * 
    * @param calendarRow calendar data
    * @param exceptionMap exception ID map
    * @throws SQLException
    */
   private void processCalendar(Row calendarRow, Map<Integer, DayType> exceptionMap) throws SQLException
   {
      //
      // Create the calendar and add the default working hours
      //
      ProjectCalendar calendar = m_reader.getProject().addCalendar();
      Integer dominantWorkPatternID = calendarRow.getInteger("DOMINANT_WORK_PATTERN");
      calendar.setUniqueID(calendarRow.getInteger("CALENDARID"));
      processWorkPattern(calendar, dominantWorkPatternID, exceptionMap);
      calendar.setName(calendarRow.getString("NAMK"));

      //
      // Add any additional working weeks
      //
      List<Row> rows = getRows("select * from work_pattern_assignment where work_pattern_assignmentid=? and work_pattern<>?", calendar.getUniqueID(), dominantWorkPatternID);
      for (Row row : rows)
      {
         ProjectCalendarWeek week = calendar.addWorkWeek();
         week.setDateRange(new DateRange(row.getDate("START_DATE"), row.getDate("END_DATE")));
         processWorkPattern(week, row.getInteger("WORK_PATTERN"), exceptionMap);
      }

      //
      // Add exceptions - not sure how exceptions which turn non-working days into working days are handled by Asta - if at all?
      //
      rows = getRows("select * from exception_assignment where exception_assignmentid=? order by ordf", calendar.getUniqueID());
      for (Row row : rows)
      {
         Date startDate = row.getDate("STARU_DATE");
         Date endDate = row.getDate("ENE_DATE");
         calendar.addCalendarException(startDate, endDate);
      }
   }

   /**
    * Convert a work pattern into a ProjectCalendarWeek instance.
    * 
    * @param week ProjectCalendarWeek instance
    * @param workPatternID target work pattern ID
    * @param exceptionMap exception ID map
    * @throws SQLException
    */
   private void processWorkPattern(ProjectCalendarWeek week, Integer workPatternID, Map<Integer, DayType> exceptionMap) throws SQLException
   {
      List<Row> rows = getRows("select * from work_pattern where work_patternid=?", workPatternID);
      week.setName(rows.get(0).getString("NAMN"));

      rows = getRows("select * from time_entry where time_entryid=? order by ordf", workPatternID);
      long lastEndTime = Long.MIN_VALUE;
      Day currentDay = Day.SUNDAY;
      ProjectCalendarHours hours = week.addCalendarHours(currentDay);
      Arrays.fill(week.getDays(), DayType.NON_WORKING);

      for (Row row : rows)
      {
         Date startTime = row.getDate("START_TIME");
         Date endTime = row.getDate("END_TIME");
         if (startTime.getTime() > endTime.getTime())
         {
            Calendar cal = Calendar.getInstance();
            cal.setTime(endTime);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            endTime = cal.getTime();
         }

         if (startTime.getTime() < lastEndTime)
         {
            currentDay = currentDay.getNextDay();
            hours = week.addCalendarHours(currentDay);
         }

         DayType type = exceptionMap.get(row.getInteger("EXCEPTIOP"));
         if (type == DayType.WORKING)
         {
            hours.addRange(new DateRange(startTime, endTime));
            week.setWorkingDay(currentDay, DayType.WORKING);
         }

         lastEndTime = endTime.getTime();
      }
   }

   /**
    * Cleans up the structure, removes unnecessary summary tasks and
    * ensures tasks with blank names inherit their names from the
    * parent task.
    * 
    * @param project ProjectFile instance
    */
   private void updateStructure(ProjectFile project)
   {
      //
      // Build the hierarchy
      //
      project.updateStructure();

      //
      // Ensure tasks with blank names inherit parent task names
      //
      for (Task task : project.getChildTasks())
      {
         updateBlankNames(null, task);
      }

      //
      // Create a list of tasks to prune
      //
      List<Task> tasks = new LinkedList<Task>();
      for (Task task : project.getAllTasks())
      {
         if (task.getChildTasks().size() == 1 && task.getChildTasks().get(0).getChildTasks().size() == 0 && task.getWBS().equals("-"))
         {
            tasks.add(task);
         }
      }

      //
      // Prune these tasks
      //
      for (Task task : tasks)
      {
         Task child = task.getChildTasks().get(0);
         Task parent = task.getParentTask();

         if (parent == null)
         {
            List<Task> parentList = project.getChildTasks();
            int parentListIndex = parentList.indexOf(task);
            if (parentListIndex == -1)
            {
               parentList.add(child);
            }
            else
            {
               parentList.add(parentListIndex, child);
            }
         }
         else
         {
            parent.addChildTaskBefore(child, task);
         }
         task.getChildTasks().clear();
         task.remove();
      }

      //
      // Ensure we have no gaps in the ID sequence
      //
      project.renumberTaskIDs();

      project.updateStructure();
   }

   /**
    * Called recursively to replace blank task names 
    * with names inherited from parent tasks.
    * 
    * @param parent parent task
    * @param task current task
    */
   private void updateBlankNames(Task parent, Task task)
   {
      if (parent != null && (task.getName() == null || task.getName().length() == 0))
      {
         task.setName(parent.getName());
      }

      for (Task child : task.getChildTasks())
      {
         updateBlankNames(task, child);
      }
   }

   private AstaReader m_reader;
   private Integer m_projectID;
   private String m_schema = "";
   private DataSource m_dataSource;
   private Connection m_connection;
   private boolean m_allocatedConnection;
   private PreparedStatement m_ps;
   private ResultSet m_rs;
   private Map<String, Integer> m_meta = new HashMap<String, Integer>();
   private List<ProjectListener> m_projectListeners;
}