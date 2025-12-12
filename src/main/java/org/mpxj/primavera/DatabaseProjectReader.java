/*
 * file:       DatabaseProjectReader.java
 * author:     Jon Iles
 * date:       2025-11-12
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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.MPXJException;
import org.mpxj.Notes;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.cpm.PrimaveraSlackCalculator;

/**
 * Populate a ProjectFile instance from tables in a P6 database.
 */
class DatabaseProjectReader extends TableProjectReader
{
   /**
    * Constructor.
    *
    * @param database database connection
    * @param schema schema name
    * @param project ProjectFile instance to populate
    * @param projectID project unique ID
    * @param state common state data
    */
   public DatabaseProjectReader(PrimaveraDatabaseConnection database, String schema, ProjectFile project, Integer projectID, TableReaderState state)
   {
      m_database = database;
      m_schema = schema;
      m_project = project;
      m_projectID = projectID;
      m_state = state;
   }

   /**
    * Populate a ProjectFile instance by reading data from tables from the database.
    */
   public void read() throws MPXJException
   {
      try
      {
         ProjectProperties properties = m_project.getProjectProperties();
         properties.setFileApplication("Primavera");
         properties.setFileType(m_database.getProductName());
         properties.setUniqueID(m_projectID);
         m_project.getProjectConfig().setSlackClaculator(new PrimaveraSlackCalculator());

         processActivityCodeAssignments();
         processResourceAssignmentCodeAssignments();
         processRoleAssignments();

         processProjectProperties();
         processTasks();
         processPredecessors();
         processAssignments();
         processExpenseItems();
         processActivitySteps();

         RollupHelper.rollupValues(m_project);

         m_project.updateStructure();
         m_project.readComplete();
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
    * Process activity code assignments.
    */
   private void processActivityCodeAssignments() throws SQLException
   {
      if (m_database.hasTable("TASKACTV"))
      {
         List<Row> assignments = m_database.getRows("select * from " + m_schema + "taskactv where proj_id=?", m_projectID);
         processActivityCodeAssignments(assignments);
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
         processResourceAssignmentCodeAssignments(assignments);
      }
   }

   /**
    * Process role assignments.
    */
   private void processRoleAssignments() throws SQLException
   {
      if (m_database.hasTable("RSRCROLE"))
      {
         List<Row> rows = m_database.getRows("select * from " + m_schema + "rsrcrole where delete_date is null and rsrc_id in (select rsrc_id from " + m_schema + "taskrsrc t where proj_id=? and delete_date is null)", m_projectID);
         processRoleAssignments(rows);
      }
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
      processProjectProperties(rows);

      rows = m_database.getRows("select * from " + m_schema + "projpcat where proj_id=?", m_projectID);
      processProjectCodeAssignments(rows);

      //
      // Process PMDB-specific attributes
      //
      rows = m_database.getRows("select * from " + m_schema + "prefer where prefer.delete_date is null");
      if (!rows.isEmpty())
      {
         Row row = rows.get(0);
         ProjectProperties ph = m_project.getProjectProperties();
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
    * Select the default currency properties from the database.
    *
    * @param currencyID default currency ID
    */
   private void processDefaultCurrency(Integer currencyID) throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "currtype where curr_id=?", currencyID);
      if (!rows.isEmpty())
      {
         processDefaultCurrency(rows.get(0));
      }
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
         processScheduleOptions(new MapRow(new HashMap<>(record.getAttributes()), false));
      }
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws SQLException
   {
      List<Row> wbs = m_database.getRows("select * from " + m_schema + "projwbs where proj_id=? and delete_date is null order by parent_wbs_id,seq_num", m_projectID);
      List<Row> tasks = m_database.getRows("select * from " + m_schema + "task where proj_id=? and delete_date is null", m_projectID);
      Map<Integer, Notes> wbsNotes = getNotes(m_database.getRows("select * from " + m_schema + "wbsmemo where proj_id=?", m_projectID), "wbs_memo_id", "wbs_id", "wbs_memo");
      Map<Integer, Notes> taskNotes = getNotes(m_database.getRows("select * from " + m_schema + "taskmemo where proj_id=?", m_projectID), "memo_id", "task_id", "task_memo");

      processTasks(wbs, tasks, wbsNotes, taskNotes);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "taskpred where proj_id=? and delete_date is null", m_projectID);
      processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws SQLException
   {
      List<Row> rows = m_database.getRows("select * from " + m_schema + "taskrsrc where proj_id=? and delete_date is null", m_projectID);
      processAssignments(rows);
   }

   /**
    * Select the expense items from the database.
    */
   private void processExpenseItems() throws SQLException
   {
      processExpenseItems(m_database.getRows("select * from " + m_schema + "projcost where proj_id=?", m_projectID));
   }

   /**
    * Select the activity steps from the database.
    */
   private void processActivitySteps() throws SQLException
   {
      processActivitySteps(m_database.getRows("select * from " + m_schema + "taskproc where proj_id=?", m_projectID));
   }

   private final PrimaveraDatabaseConnection m_database;
   private final String m_schema;
   private final Integer m_projectID;
}
