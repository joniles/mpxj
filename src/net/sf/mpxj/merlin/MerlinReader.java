/*
 * file:       MerlinReaders.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       17/11/2016
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

package net.sf.mpxj.merlin;

import java.io.File;
import java.io.IOException;
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
import java.util.Properties;

import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.common.InputStreamHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.ProjectReader;

/**
 * This class reads Merlin Project files. As Merlin is a Mac application, the "file"
 * seen by the user is actually a directory. The file in this directory we are interested
 * in is a SQLite database. You can either point the read methods directly to this database
 * file, or the read methods that accept a file name or a File object can be pointed at
 * the top level directory.
 */
public class MerlinReader implements ProjectReader
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
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      File file = null;
      try
      {
         file = InputStreamHelper.writeStreamToTempFile(stream, ".sqlite");
         return read(file);
      }

      catch (IOException ex)
      {
         throw new MPXJException("", ex);
      }

      finally
      {
         if (file != null)
         {
            file.delete();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      return read(new File(fileName));
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(File file) throws MPXJException
   {
      File databaseFile;
      if (file.isDirectory())
      {
         databaseFile = new File(file, "state.sql");
      }
      else
      {
         databaseFile = file;
      }
      return readFile(databaseFile);
   }

   /**
    * By the time we reach this method, we should be looking at the SQLite
    * database file itself.
    *
    * @param file SQLite database file
    * @return ProjectFile instance
    */
   private ProjectFile readFile(File file) throws MPXJException
   {
      try
      {
         String url = "jdbc:sqlite:" + file.getAbsolutePath();
         Properties props = new Properties();
         m_connection = org.sqlite.JDBC.createConnection(url, props);
         return read();
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
    * Read the project data and return a ProjectFile instance.
    *
    * @return ProjectFile instance
    */
   private ProjectFile read() throws SQLException
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoCalendarUniqueID(false);
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);

      m_eventManager.addProjectListeners(m_projectListeners);

      processCalendars();
      processResources();
      processTasks();
      processAssignments();
      processDependencies();

      return m_project;
   }

   /**
    * Read calendar data.
    */
   private void processCalendars() throws SQLException
   {
      List<Row> rows = getRows("select * from zcalendar where zproject=?", m_projectID);
      for (Row row : rows)
      {
         ProjectCalendar calendar = m_project.addCalendar();
         calendar.setUniqueID(row.getInteger("Z_PK"));
         calendar.setName(row.getString("ZTITLE"));
         // TODO: populate the calendar detail
         m_eventManager.fireCalendarReadEvent(calendar);
      }
   }

   /**
    * Read resource data.
    */
   private void processResources() throws SQLException
   {
      List<Row> rows = getRows("select * from zresource where zproject=? order by zorderinproject", m_projectID);
      for (Row row : rows)
      {
         Resource resource = m_project.addResource();
         resource.setUniqueID(row.getInteger("Z_PK"));
         resource.setEmailAddress(row.getString("ZEMAIL"));
         resource.setInitials(row.getString("ZINITIALS"));
         resource.setName(row.getString("ZTITLE_"));
         resource.setGUID(row.getUUID("ZUNIQUEID"));

         Integer calendarID = row.getInteger("ZRESOURCECALENDAR");
         if (calendarID != null)
         {
            ProjectCalendar calendar = m_project.getCalendarByUniqueID(calendarID);
            if (calendar != null)
            {
               calendar.setName(resource.getName());
               resource.setResourceCalendar(calendar);
            }
         }

         // TODO: populate more attributes

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Read all top level tasks.
    */
   private void processTasks() throws SQLException
   {
      //
      // Yes... we could probably read this in one query in the right order
      // using a CTE... but life's too short.
      //
      List<Row> rows = getRows("select * from zscheduleitem where zproject=? and zparentactivity_ is null and z_ent=45 order by zorderinparentactivity", m_projectID);
      for (Row row : rows)
      {
         Task task = m_project.addTask();
         populateTask(row, task);
         processChildTasks(task);
      }
   }

   /**
    * Read all child tasks for a given parent.
    *
    * @param parentTask parent task
    */
   private void processChildTasks(Task parentTask) throws SQLException
   {
      List<Row> rows = getRows("select * from zscheduleitem where zparentactivity_=? and z_ent=45 order by zorderinparentactivity", parentTask.getUniqueID());
      for (Row row : rows)
      {
         Task task = parentTask.addTask();
         populateTask(row, task);
         processChildTasks(task);
      }
   }

   /**
    * Read data for an individual task.
    *
    * @param row task data from database
    * @param task Task instance
    */
   private void populateTask(Row row, Task task)
   {
      task.setUniqueID(row.getInteger("Z_PK"));
      task.setName(row.getString("ZTITLE"));
      task.setPriority(Priority.getInstance(row.getInt("ZPRIORITY")));
      task.setMilestone(row.getBoolean("ZISMILESTONE"));
      task.setLateFinish(row.getDate("ZGIVENENDDATEMAX_"));
      task.setEarlyFinish(row.getDate("ZGIVENENDDATEMIN_"));
      task.setLateStart(row.getDate("ZGIVENSTARTDATEMAX_"));
      task.setEarlyStart(row.getDate("ZGIVENSTARTDATEMIN_"));
      task.setActualFinish(row.getDate("ZGIVENACTUALENDDATE_"));
      task.setActualStart(row.getDate("ZGIVENACTUALSTARTDATE_"));
      task.setNotes(row.getString("ZOBJECTDESCRIPTION"));
      task.setDuration(row.getDuration("ZGIVENDURATION_"));
      task.setOvertimeWork(row.getWork("ZGIVENWORKOVERTIME_"));
      task.setWork(row.getWork("ZGIVENWORK_"));
      task.setLevelingDelay(row.getDuration("ZLEVELINGDELAY_"));
      task.setActualOvertimeWork(row.getWork("ZGIVENACTUALWORKOVERTIME_"));
      task.setActualWork(row.getWork("ZGIVENACTUALWORK_"));
      task.setRemainingWork(row.getWork("ZGIVENACTUALWORK_"));
      task.setGUID(row.getUUID("ZUNIQUEID"));

      Integer calendarID = row.getInteger("ZGIVENCALENDAR");
      if (calendarID != null)
      {
         ProjectCalendar calendar = m_project.getCalendarByUniqueID(calendarID);
         if (calendar != null)
         {
            task.setCalendar(calendar);
         }
      }

      // TODO: populate more attributes

      m_eventManager.fireTaskReadEvent(task);
   }

   /**
    * Read assignment data.
    */
   private void processAssignments() throws SQLException
   {
      List<Row> rows = getRows("select * from zscheduleitem where zproject=? and z_ent=47 order by zorderinactivity", m_projectID);
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger("ZACTIVITY_"));
         Resource resource = m_project.getResourceByUniqueID(row.getInteger("ZRESOURCE"));
         if (task != null && resource != null)
         {
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            assignment.setGUID(row.getUUID("ZUNIQUEID"));
            assignment.setActualFinish(row.getDate("ZGIVENACTUALENDDATE_"));
            assignment.setActualStart(row.getDate("ZGIVENACTUALSTARTDATE_"));
            //ZGIVENWORK_ -> Units?
            // TODO: populate more attributes
         }
      }
   }

   /**
    * Read relation data.
    */
   private void processDependencies() throws SQLException
   {
      List<Row> rows = getRows("select * from zdependency where zproject=?", m_projectID);
      for (Row row : rows)
      {
         Task nextTask = m_project.getTaskByUniqueID(row.getInteger("ZNEXTACTIVITY_"));
         Task prevTask = m_project.getTaskByUniqueID(row.getInteger("ZPREVIOUSACTIVITY_"));
         Duration lag = row.getDuration("ZLAG_");
         RelationType type = row.getRelationType("ZTYPE");
         nextTask.addPredecessor(prevTask, type, lag);
         // TODO: populate more attributes
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
      List<Row> result = new LinkedList<Row>();

      m_ps = m_connection.prepareStatement(sql);
      m_ps.setInt(1, NumberHelper.getInt(var));
      m_rs = m_ps.executeQuery();
      populateMetaData();
      while (m_rs.next())
      {
         result.add(new SqliteResultSetRow(m_rs, m_meta));
      }

      return (result);
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

   private ProjectFile m_project;
   private EventManager m_eventManager;
   private Integer m_projectID = Integer.valueOf(1);
   private Connection m_connection;
   private PreparedStatement m_ps;
   private ResultSet m_rs;
   private Map<String, Integer> m_meta = new HashMap<String, Integer>();
   private List<ProjectListener> m_projectListeners;
}
