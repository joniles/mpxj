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
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.FileHelper;
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
public final class MerlinReader implements ProjectReader
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
         FileHelper.deleteQuietly(file);
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

         m_documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

         XPathFactory xPathfactory = XPathFactory.newInstance();
         XPath xpath = xPathfactory.newXPath();
         m_dayTimeIntervals = xpath.compile("/array/dayTimeInterval");
         m_entityMap = new HashMap<String, Integer>();
         return read();
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FORMAT, ex);
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

         m_documentBuilder = null;
         m_dayTimeIntervals = null;
         m_entityMap = null;
      }
   }

   /**
    * Read the project data and return a ProjectFile instance.
    *
    * @return ProjectFile instance
    */
   private ProjectFile read() throws Exception
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      config.setAutoCalendarUniqueID(false);
      config.setAutoTaskUniqueID(false);
      config.setAutoResourceUniqueID(false);

      m_project.getProjectProperties().setFileApplication("Merlin");
      m_project.getProjectProperties().setFileType("SQLITE");

      m_eventManager.addProjectListeners(m_projectListeners);

      populateEntityMap();
      processProject();
      processCalendars();
      processResources();
      processTasks();
      processAssignments();
      processDependencies();

      return m_project;
   }

   /**
    * Create a mapping from entity names to entity ID values.
    */
   private void populateEntityMap() throws SQLException
   {
      for (Row row : getRows("select * from z_primarykey"))
      {
         m_entityMap.put(row.getString("Z_NAME"), row.getInteger("Z_ENT"));
      }
   }

   /**
    * Read project properties.
    */
   private void processProject() throws SQLException
   {
      ProjectProperties props = m_project.getProjectProperties();
      Row row = getRows("select * from zproject where z_pk=?", m_projectID).get(0);
      props.setWeekStartDay(Day.getInstance(row.getInt("ZFIRSTDAYOFWEEK") + 1));
      props.setScheduleFrom(row.getInt("ZSCHEDULINGDIRECTION") == 1 ? ScheduleFrom.START : ScheduleFrom.FINISH);
      props.setMinutesPerDay(Integer.valueOf(row.getInt("ZHOURSPERDAY") * 60));
      props.setDaysPerMonth(row.getInteger("ZDAYSPERMONTH"));
      props.setMinutesPerWeek(Integer.valueOf(row.getInt("ZHOURSPERWEEK") * 60));
      props.setStatusDate(row.getTimestamp("ZGIVENSTATUSDATE"));
      props.setCurrencySymbol(row.getString("ZCURRENCYSYMBOL"));
      props.setName(row.getString("ZTITLE"));
      props.setUniqueID(row.getUUID("ZUNIQUEID").toString());
   }

   /**
    * Read calendar data.
    */
   private void processCalendars() throws Exception
   {
      List<Row> rows = getRows("select * from zcalendar where zproject=?", m_projectID);
      for (Row row : rows)
      {
         ProjectCalendar calendar = m_project.addCalendar();
         calendar.setUniqueID(row.getInteger("Z_PK"));
         calendar.setName(row.getString("ZTITLE"));
         processDays(calendar);
         processExceptions(calendar);
         m_eventManager.fireCalendarReadEvent(calendar);
      }
   }

   /**
    * Process normal calendar working and non-working days.
    *
    * @param calendar parent calendar
    */
   private void processDays(ProjectCalendar calendar) throws Exception
   {
      // Default all days to non-working
      for (Day day : Day.values())
      {
         calendar.setWorkingDay(day, false);
      }

      List<Row> rows = getRows("select * from zcalendarrule where zcalendar1=? and z_ent=?", calendar.getUniqueID(), m_entityMap.get("CalendarWeekDayRule"));
      for (Row row : rows)
      {
         Day day = row.getDay("ZWEEKDAY");
         String timeIntervals = row.getString("ZTIMEINTERVALS");
         if (timeIntervals == null)
         {
            calendar.setWorkingDay(day, false);
         }
         else
         {
            ProjectCalendarHours hours = calendar.addCalendarHours(day);
            NodeList nodes = getNodeList(timeIntervals, m_dayTimeIntervals);
            calendar.setWorkingDay(day, nodes.getLength() > 0);

            for (int loop = 0; loop < nodes.getLength(); loop++)
            {
               NamedNodeMap attributes = nodes.item(loop).getAttributes();
               Date startTime = m_calendarTimeFormat.parse(attributes.getNamedItem("startTime").getTextContent());
               Date endTime = m_calendarTimeFormat.parse(attributes.getNamedItem("endTime").getTextContent());

               if (startTime.getTime() >= endTime.getTime())
               {
                  endTime = DateHelper.addDays(endTime, 1);
               }

               hours.addRange(new DateRange(startTime, endTime));
            }
         }
      }
   }

   /**
    * Process calendar exceptions.
    *
    * @param calendar parent calendar.
    */
   private void processExceptions(ProjectCalendar calendar) throws Exception
   {
      List<Row> rows = getRows("select * from zcalendarrule where zcalendar=? and z_ent=?", calendar.getUniqueID(), m_entityMap.get("CalendarExceptionRule"));
      for (Row row : rows)
      {
         Date startDay = row.getDate("ZSTARTDAY");
         Date endDay = row.getDate("ZENDDAY");
         ProjectCalendarException exception = calendar.addCalendarException(startDay, endDay);

         String timeIntervals = row.getString("ZTIMEINTERVALS");
         if (timeIntervals != null)
         {
            NodeList nodes = getNodeList(timeIntervals, m_dayTimeIntervals);
            for (int loop = 0; loop < nodes.getLength(); loop++)
            {
               NamedNodeMap attributes = nodes.item(loop).getAttributes();
               Date startTime = m_calendarTimeFormat.parse(attributes.getNamedItem("startTime").getTextContent());
               Date endTime = m_calendarTimeFormat.parse(attributes.getNamedItem("endTime").getTextContent());

               if (startTime.getTime() >= endTime.getTime())
               {
                  endTime = DateHelper.addDays(endTime, 1);
               }

               exception.addRange(new DateRange(startTime, endTime));
            }
         }
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
         resource.setType(row.getResourceType("ZTYPE"));
         resource.setMaterialLabel(row.getString("ZMATERIALUNIT"));

         if (resource.getType() == ResourceType.WORK)
         {
            resource.setMaxUnits(Double.valueOf(NumberHelper.getDouble(row.getDouble("ZAVAILABLEUNITS_")) * 100.0));
         }

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
      List<Row> rows = getRows("select * from zscheduleitem where zproject=? and zparentactivity_ is null and z_ent=? order by zorderinparentactivity", m_projectID, m_entityMap.get("Activity"));
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
      List<Row> rows = getRows("select * from zscheduleitem where zparentactivity_=? and z_ent=? order by zorderinparentactivity", parentTask.getUniqueID(), m_entityMap.get("Activity"));
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
      task.setActualFinish(row.getTimestamp("ZGIVENACTUALENDDATE_"));
      task.setActualStart(row.getTimestamp("ZGIVENACTUALSTARTDATE_"));
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

      populateConstraints(row, task);

      // Percent complete is calculated bottom up from assignments and actual work vs. planned work

      m_eventManager.fireTaskReadEvent(task);
   }

   /**
    * Populate the constraint type and constraint date.
    * Note that Merlin allows both start and end constraints simultaneously.
    * As we can't have both, we'll prefer the start constraint.
    *
    * @param row task data from database
    * @param task Task instance
    */
   private void populateConstraints(Row row, Task task)
   {
      Date endDateMax = row.getTimestamp("ZGIVENENDDATEMAX_");
      Date endDateMin = row.getTimestamp("ZGIVENENDDATEMIN_");
      Date startDateMax = row.getTimestamp("ZGIVENSTARTDATEMAX_");
      Date startDateMin = row.getTimestamp("ZGIVENSTARTDATEMIN_");

      ConstraintType constraintType = null;
      Date constraintDate = null;

      if (endDateMax != null)
      {
         constraintType = ConstraintType.FINISH_NO_LATER_THAN;
         constraintDate = endDateMax;
      }

      if (endDateMin != null)
      {
         constraintType = ConstraintType.FINISH_NO_EARLIER_THAN;
         constraintDate = endDateMin;
      }

      if (endDateMin != null && endDateMin == endDateMax)
      {
         constraintType = ConstraintType.MUST_FINISH_ON;
         constraintDate = endDateMin;
      }

      if (startDateMax != null)
      {
         constraintType = ConstraintType.START_NO_LATER_THAN;
         constraintDate = startDateMax;
      }

      if (startDateMin != null)
      {
         constraintType = ConstraintType.START_NO_EARLIER_THAN;
         constraintDate = startDateMin;
      }

      if (startDateMin != null && startDateMin == endDateMax)
      {
         constraintType = ConstraintType.MUST_START_ON;
         constraintDate = endDateMin;
      }

      task.setConstraintType(constraintType);
      task.setConstraintDate(constraintDate);
   }

   /**
    * Read assignment data.
    */
   private void processAssignments() throws SQLException
   {
      List<Row> rows = getRows("select * from zscheduleitem where zproject=? and z_ent=? order by zorderinactivity", m_projectID, m_entityMap.get("Assignment"));
      for (Row row : rows)
      {
         Task task = m_project.getTaskByUniqueID(row.getInteger("ZACTIVITY_"));
         Resource resource = m_project.getResourceByUniqueID(row.getInteger("ZRESOURCE"));
         if (task != null && resource != null)
         {
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            assignment.setGUID(row.getUUID("ZUNIQUEID"));
            assignment.setActualFinish(row.getTimestamp("ZGIVENACTUALENDDATE_"));
            assignment.setActualStart(row.getTimestamp("ZGIVENACTUALSTARTDATE_"));

            assignment.setWork(assignmentDuration(task, row.getWork("ZGIVENWORK_")));
            assignment.setOvertimeWork(assignmentDuration(task, row.getWork("ZGIVENWORKOVERTIME_")));
            assignment.setActualWork(assignmentDuration(task, row.getWork("ZGIVENACTUALWORK_")));
            assignment.setActualOvertimeWork(assignmentDuration(task, row.getWork("ZGIVENACTUALWORKOVERTIME_")));
            assignment.setRemainingWork(assignmentDuration(task, row.getWork("ZGIVENREMAININGWORK_")));

            assignment.setLevelingDelay(row.getDuration("ZLEVELINGDELAY_"));

            if (assignment.getRemainingWork() == null)
            {
               assignment.setRemainingWork(assignment.getWork());
            }

            if (resource.getType() == ResourceType.WORK)
            {
               assignment.setUnits(Double.valueOf(NumberHelper.getDouble(row.getDouble("ZRESOURCEUNITS_")) * 100.0));
            }
         }
      }
   }

   /**
    * Extract a duration amount from the assignment, converting a percentage
    * into an actual duration.
    *
    * @param task parent task
    * @param work duration from assignment
    * @return Duration instance
    */
   private Duration assignmentDuration(Task task, Duration work)
   {
      Duration result = work;

      if (result != null)
      {
         if (result.getUnits() == TimeUnit.PERCENT)
         {
            Duration taskWork = task.getWork();
            if (taskWork != null)
            {
               result = Duration.getInstance(taskWork.getDuration() * result.getDuration(), taskWork.getUnits());
            }
         }
      }
      return result;
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
         Relation relation = nextTask.addPredecessor(prevTask, type, lag);
         relation.setUniqueID(row.getInteger("Z_PK"));
      }
   }

   /**
    * Retrieve a number of rows matching the supplied query
    * which takes a single parameter.
    *
    * @param sql query statement
    * @param values bind variable values
    * @return result set
    * @throws SQLException
    */
   private List<Row> getRows(String sql, Integer... values) throws SQLException
   {
      List<Row> result = new LinkedList<Row>();

      m_ps = m_connection.prepareStatement(sql);
      int bindIndex = 1;
      for (Integer value : values)
      {
         m_ps.setInt(bindIndex++, NumberHelper.getInt(value));
      }
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

   /**
    * Retrieve a node list based on an XPath expression.
    *
    * @param document XML document to process
    * @param expression compiled XPath expression
    * @return node list
    */
   private NodeList getNodeList(String document, XPathExpression expression) throws Exception
   {
      Document doc = m_documentBuilder.parse(new InputSource(new StringReader(document)));
      return (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
   }

   private ProjectFile m_project;
   private EventManager m_eventManager;
   private Integer m_projectID = Integer.valueOf(1);
   private Connection m_connection;
   private PreparedStatement m_ps;
   private ResultSet m_rs;
   private Map<String, Integer> m_meta = new HashMap<String, Integer>();
   private List<ProjectListener> m_projectListeners;
   private DocumentBuilder m_documentBuilder;
   private DateFormat m_calendarTimeFormat = new SimpleDateFormat("HH:mm:ss");
   private XPathExpression m_dayTimeIntervals;
   private Map<String, Integer> m_entityMap;
}
