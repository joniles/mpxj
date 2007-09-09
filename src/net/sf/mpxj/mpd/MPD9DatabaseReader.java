/*
 * file:       MPD9DatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2007
 * date:       2006-02-02
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

package net.sf.mpxj.mpd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Task;
import net.sf.mpxj.utility.NumberUtility;


/**
 * This class reads project data from an MPD9 format database.
 */
public final class MPD9DatabaseReader extends MPD9AbstractReader
{
   /**
    * Read a project from the current data source.
    * 
    * @return ProjectFile instance
    * @throws MPXJException
    */
   public ProjectFile read ()
      throws MPXJException
   {
      try
      {
         m_project = new ProjectFile ();
         
         processProjectHeader();
         processCalendars();
         processResources();
         processTasks();
         processLinks();
         processAssignments();
         processExtendedAttributes();
         processSubProjects();
         postProcessing();
         
         return (m_project);
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
   private void processProjectHeader ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_projects where proj_id=?", m_projectID);
      if (rows.isEmpty() == false)
      {
         processProjectHeader (rows.get(0));
      }            
   }
      

   /**
    * Select calendar data from the database.
    * 
    * @throws SQLException
    */
   private void processCalendars ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_calendars where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processCalendar(iter.next());
      }
      
      updateBaseCalendarNames();
      
      processCalendarData(m_project.getBaseCalendars());
      processCalendarData(m_project.getResourceCalendars());      
   }
   
   /**
    * Process calendar hours and exception data from the database.
    * 
    * @param calendars all calendars for the project
    */
   private void processCalendarData (List<ProjectCalendar> calendars)
      throws SQLException
   {
      Iterator<ProjectCalendar> iter = calendars.iterator();
      while (iter.hasNext() == true)
      {
         ProjectCalendar calendar = iter.next();
         processCalendarData(calendar, getRows("select * from msp_calendar_data where proj_id=? and cal_uid=?", m_projectID, calendar.getUniqueID()));
      }
   }
   /**
    * Process the hours and exceptions for an individual calendar.
    * 
    * @param calendar project calendar
    * @param calendarData hours and exception rows for this calendar
    */
   private void processCalendarData (ProjectCalendar calendar, List<ResultSetRow> calendarData)
   {
      Iterator<ResultSetRow> iter = calendarData.iterator();
      while (iter.hasNext() == true)
      {
         processCalendarData(calendar, iter.next());
      }
   }
   
   /**
    * Process resources.
    * 
    * @throws SQLException
    */
   private void processResources ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_resources where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processResource(iter.next());
      }      
   }
   
   /**
    * Process tasks.
    * 
    * @throws SQLException
    */
   private void processTasks ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_tasks where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processTask(iter.next());
      }      
   }

   /**
    * Process links.
    * 
    * @throws SQLException
    */
   private void processLinks ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_links where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processLink(iter.next());
      }      
   }
   
   /**
    * Process resource assignments.
    * 
    * @throws SQLException
    */
   private void processAssignments ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_assignments where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processAssignment(iter.next());
      }      
   }
   
   /**
    * This method reads the extended task and resource attributes.
    * 
    * @throws SQLException
    */
   private void processExtendedAttributes ()
      throws SQLException
   {
      processTextFields();
      processNumberFields();
      processFlagFields();
      processDurationFields();
      processDateFields();
      processOutlineCodeFields();
   }
   
   /**
    * The only indication that a task is a SubProject is the contents
    * of the subproject file name field. We test these here then add a skeleton
    * subproject structure to match the way we do things with MPP files.
    */
   private void processSubProjects ()
   {
      Iterator<Task> iter = m_project.getAllTasks().iterator();
      while (iter.hasNext())
      {
         Task task = iter.next();
         String subProjectFileName = task.getSubprojectName();
         int subprojectIndex = 1;
         if (subProjectFileName != null)
         {
            String fileName = subProjectFileName;
            int offset = 0x01000000 + (subprojectIndex * 0x00400000);
            int index = subProjectFileName.lastIndexOf('\\');
            if (index != -1)
            {
               fileName = subProjectFileName.substring(index+1);
            }
            
            SubProject sp = new SubProject();
            sp.setFileName(fileName);
            sp.setFullPath(subProjectFileName);
            sp.setUniqueIDOffset(new Integer(offset));
            sp.setTaskUniqueID(task.getUniqueID());
            task.setSubProject(sp);
            
            ++subprojectIndex;
         }
      }
   }
   
   /**
    * Reads text field extended attributes.
    * 
    * @throws SQLException
    */
   private void processTextFields ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_text_fields where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processTextField(iter.next());
      }            
   }

   /**
    * Reads number field extended attributes.
    * 
    * @throws SQLException
    */
   private void processNumberFields ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows ("select * from msp_number_fields where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processNumberField(iter.next());
      }            
   }

   /**
    * Reads flag field extended attributes.
    * 
    * @throws SQLException
    */
   private void processFlagFields ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_flag_fields where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processFlagField(iter.next());
      }            
   }

   /**
    * Reads duration field extended attributes.
    *
    * @throws SQLException
    */
   private void processDurationFields ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows("select * from msp_duration_fields where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processDurationField(iter.next());
      }            
   }
   
   /**
    * Reads date field extended attributes.
    * 
    * @throws SQLException
    */
   private void processDateFields ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows ("select * from msp_date_fields where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processDateField(iter.next());
      }            
   }

   /**
    * Process outline code fields.
    * 
    * @throws SQLException
    */
   private void processOutlineCodeFields ()
      throws SQLException
   {
      List<ResultSetRow> rows = getRows ("select * from msp_code_fields where proj_id=?", m_projectID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processOutlineCodeFields(iter.next());
      }            
   }

   /**
    * Process a single outline code.
    * 
    * @param parentRow outline code to task mapping table
    * @throws SQLException
    */
   private void processOutlineCodeFields (Row parentRow)
      throws SQLException
   {
      Integer entityID = parentRow.getInteger("CODE_REF_UID");
      Integer outlineCodeEntityID = parentRow.getInteger("CODE_UID");
      
      List<ResultSetRow> rows = getRows ("select * from msp_outline_codes where code_uid=?", outlineCodeEntityID);
      Iterator<ResultSetRow> iter = rows.iterator();
      while (iter.hasNext() == true)
      {
         processOutlineCodeField(entityID, iter.next());
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
   private List<ResultSetRow> getRows (String sql, Integer var)
      throws SQLException
   {
      allocateConnection();
      
      try
      {
         List<ResultSetRow> result = new LinkedList<ResultSetRow>();
         
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
   private List<ResultSetRow> getRows (String sql, Integer var1, Integer var2)
      throws SQLException
   {
      allocateConnection();
      
      try
      {
         List<ResultSetRow> result = new LinkedList<ResultSetRow>();
         
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
   private void allocateConnection ()
      throws SQLException
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
   private void releaseConnection ()
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
    * Retrieves basic neta data from the result set.
    * 
    * @throws SQLException
    */
   private void populateMetaData ()
      throws SQLException
   {
      m_meta.clear();
      
      ResultSetMetaData meta = m_rs.getMetaData();
      int columnCount = meta.getColumnCount()+1;
      for (int loop=1; loop < columnCount; loop++)
      {
         String name = meta.getColumnName(loop);
         Integer type = new Integer(meta.getColumnType(loop));
         m_meta.put(name, type);
      }      
   }
   
   /**
    * Sets the data source used to read the project data.
    * 
    * @param dataSource data source
    */
   public void setDataSource (DataSource dataSource)
   {
      m_dataSource = dataSource;
   }
   
   /**
    * Sets the connection to be used to read the project data.
    * 
    * @param connection database connection
    */
   public void setConnection (Connection connection)
   {
      m_connection = connection;
   }
   
   private DataSource m_dataSource;
   private boolean m_allocatedConnection;
   private Connection m_connection;
   private PreparedStatement m_ps;
   private ResultSet m_rs;
   private Map<String, Integer> m_meta = new HashMap<String, Integer>();
}