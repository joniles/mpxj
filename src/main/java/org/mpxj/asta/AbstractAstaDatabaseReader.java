/*
 * file:       AbstractDatabaseReader.java
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

package org.mpxj.asta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mpxj.DayType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.reader.AbstractProjectFileReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
abstract class AbstractAstaDatabaseReader extends AbstractProjectFileReader
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

         List<Row> rows = getRows("project_summary", Collections.emptyMap(), PROJECT_SUMMARY_NAME_MAP);
         for (Row row : rows)
         {
            Integer id = row.getInteger("PROJID");
            String name = row.getString("SHORT_NAME");
            result.put(id, name);
         }

         return result;
      }

      catch (AstaDatabaseException ex)
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
         // TODO: user defined field support (where is udf_data?)
         project.readComplete();

         m_reader = null;

         return project;
      }

      catch (AstaDatabaseException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         releaseResources();
      }
   }

   /**
    * Select the project properties row from the database.
    */
   private void processProjectProperties() throws AstaDatabaseException
   {
      List<Row> schemaVersionRows = getRows("dodschem", Collections.emptyMap());
      List<Row> projectSummaryRows = getRows("project_summary", m_projectKey, PROJECT_SUMMARY_NAME_MAP);
      List<Row> progressPeriodRows = getRows("progress_period", m_projectKey, PROGRESS_PERIOD_NAME_MAP);
      List<Row> userSettingsRows = getRows("userr", m_projectKey);
      Integer schemaVersion = schemaVersionRows.isEmpty() ? null : schemaVersionRows.get(0).getInteger("SCHVER");
      Row projectSummary = projectSummaryRows.isEmpty() ? null : projectSummaryRows.get(0);
      Row userSettings = userSettingsRows.isEmpty() ? null : userSettingsRows.get(0);
      List<Row> progressPeriods = progressPeriodRows.isEmpty() ? null : progressPeriodRows;
      m_reader.processProjectProperties(schemaVersion, projectSummary, userSettings, progressPeriods);
   }

   /**
    * Process calendars.
    */
   private void processCalendars() throws AstaDatabaseException
   {
      List<Row> rows = getRows("exceptionn", Collections.emptyMap(), EXCEPTION_NAME_MAP);
      Map<Integer, DayType> exceptionMap = m_reader.createExceptionTypeMap(rows);

      rows = getRows("work_pattern", Collections.emptyMap(), WORK_PATTERN_NAME_MAP);
      Map<Integer, Row> workPatternMap = m_reader.createWorkPatternMap(rows);

      rows = getRows("work_pattern_assignment", Collections.emptyMap());
      Map<Integer, List<Row>> workPatternAssignmentMap = m_reader.createWorkPatternAssignmentMap(rows);

      rows = sortRows(getRows("exception_assignment", Collections.emptyMap(), EXCEPTION_ASSIGNMENT_MAP), "EXCEPTION_ASSIGNMENT_ID", "ORDF");
      Map<Integer, List<Row>> exceptionAssignmentMap = m_reader.createExceptionAssignmentMap(rows);

      rows = sortRows(getRows("time_entry", Collections.emptyMap(), TIME_ENTRY_NAME_MAP), "TIME_ENTRYID", "ORDF");
      Map<Integer, List<Row>> timeEntryMap = m_reader.createTimeEntryMap(rows);

      rows = getRows("calendar", m_projectKey, CALENDAR_NAME_MAP);
      rows = HierarchyHelper.sortHierarchy(rows, r -> r.getInteger("ID"), r -> r.getInteger("CALENDAR"));
      for (Row row : rows)
      {
         m_reader.processCalendar(row, workPatternMap, workPatternAssignmentMap, exceptionAssignmentMap, timeEntryMap, exceptionMap);
      }
   }

   /**
    * Process resources.
    */
   private void processResources() throws AstaDatabaseException
   {
      List<Row> permanentRows = sortRows(getRows("permanent_resource", m_projectKey, PERMANENT_RESOURCE_NAME_MAP), "ID");
      List<Row> consumableRows = sortRows(getRows("consumable_resource", m_projectKey, CONSUMABLE_RESOURCE_RESOURCE_NAME_MAP), "ID");
      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws AstaDatabaseException
   {
      List<Row> bars = getRows("bar", m_projectKey, BAR_NAME_MAP);
      List<Row> expandedTasks = getRows("expanded_task", m_projectKey, EXPANDED_TASK_NAME_MAP);
      List<Row> tasks = getRows("task", m_projectKey, TASK_NAME_MAP);
      List<Row> milestones = getRows("milestone", m_projectKey, MILESTONE_NAME_MAP);
      List<Row> hammocks = getRows("hammock_task", m_projectKey);
      List<Row> completedSections = getRows("task_completed_section", m_projectKey, COMPLETED_SECTION_NAME_MAP);

      m_reader.processTasks(bars, expandedTasks, tasks, milestones, hammocks, completedSections);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws AstaDatabaseException
   {
      List<Row> rows = sortRows(getRows("link", m_projectKey, LINK_NAME_MAP), "ID");
      m_reader.processPredecessors(rows);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws AstaDatabaseException
   {
      List<Row> allocationRows = getRows("permanent_schedul_allocation", m_projectKey, ALLOCATION_NAME_MAP);
      List<Row> skillRows = getRows("perm_resource_skill", m_projectKey, SKILL_NAME_MAP);
      allocationRows.sort(ALLOCATION_COMPARATOR);
      m_reader.processAssignments(allocationRows, skillRows);
   }

   /**
    * Set the ID of the project to be read.
    *
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectKey = Collections.singletonMap("PROJID", Integer.valueOf(projectID));
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         allocateResources(file);
         setProjectID(0);
         return read();
      }

      catch (AstaDatabaseException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         releaseResources();
      }
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      try
      {
         allocateResources(file);
         List<ProjectFile> result = new ArrayList<>();
         Set<Integer> ids = listProjects().keySet();
         for (Integer id : ids)
         {
            setProjectID(id.intValue());
            result.add(read());
         }
         return result;
      }

      catch (AstaDatabaseException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         releaseResources();
      }
   }

   /**
    * Retrieve a set of rows from a named table matching the supplied keys.
    *
    * @param table table to retrieve rows from
    * @param keys name and integer value keys
    * @return list of rows
    */
   protected abstract List<Row> getRows(String table, Map<String, Integer> keys) throws AstaDatabaseException;

   /**
    * Retrieve a set of rows from a named table matching the supplied keys.
    *
    * @param table table to retrieve rows from
    * @param keys name and integer value keys
    * @param nameMap column name map
    * @return list of rows
    */
   protected abstract List<Row> getRows(String table, Map<String, Integer> keys, Map<String, String> nameMap) throws AstaDatabaseException;

   /**
    * Allocate any resources necessary to work with the database before we start reading.
    *
    * @param file database file
    */
   protected abstract void allocateResources(File file) throws AstaDatabaseException;

   /**
    * Release any resources once we've finished reading.
    */
   protected abstract void releaseResources();

   /**
    * Sort rows by the named integer columns.
    *
    * @param rows list of rows to sort
    * @param columnNames columns to sort by
    * @return sorted rows
    */
   private List<Row> sortRows(List<Row> rows, String... columnNames)
   {
      Comparator<Row> comparator = Comparator.comparing(r -> Integer.valueOf(r.getInt(columnNames[0])));
      if (columnNames.length > 1)
      {
         for (int index = 1; index < columnNames.length; index++)
         {
            String columnName = columnNames[index];
            comparator = comparator.thenComparing(r -> Integer.valueOf(r.getInt(columnName)));
         }
      }
      rows.sort(comparator);
      return rows;
   }

   private AstaReader m_reader;
   private Map<String, Integer> m_projectKey;

   private static final RowComparator ALLOCATION_COMPARATOR = new RowComparator("ID");

   private static final Map<String, String> PROJECT_SUMMARY_NAME_MAP = new HashMap<>();
   static
   {
      PROJECT_SUMMARY_NAME_MAP.put("STARU", "PROJECT_START");
      PROJECT_SUMMARY_NAME_MAP.put("ENE", "PROJECT_END");
      PROJECT_SUMMARY_NAME_MAP.put("DURATIONHOURS", "DURATION");
   }

   private static final Map<String, String> BAR_NAME_MAP = new HashMap<>();
   static
   {
      BAR_NAME_MAP.put("NAMH", "NAME");
      BAR_NAME_MAP.put("STARV", "BAR_START");
      BAR_NAME_MAP.put("ENF", "BAR_FINISH");
   }

   private static final Map<String, String> TASK_NAME_MAP = new HashMap<>();
   static
   {
      TASK_NAME_MAP.put("NARE", "NAME");
      TASK_NAME_MAP.put("OVERALL_PERCENV_COMPLETE", "OVERALL_PERCENT_COMPLETE");
      TASK_NAME_MAP.put("CONSTRAINU", "CONSTRAINT_FLAG");
      TASK_NAME_MAP.put("CALENDAU", "CALENDAR");
      TASK_NAME_MAP.put("GIVEN_DURATIONHOURS", "GIVEN_DURATION");
      TASK_NAME_MAP.put("WBT", "WBS");
      TASK_NAME_MAP.put("STARZ", "LINKABLE_START");
      TASK_NAME_MAP.put("ENJ", "LINKABLE_FINISH");
      TASK_NAME_MAP.put("ACTUAL_DURATIONHOURS", "ACTUAL_DURATION");
      TASK_NAME_MAP.put("NOTET", "NOTES");
      TASK_NAME_MAP.put("DURATION_TIMJ_UNIT", "DURATION_TIME_UNIT");
      TASK_NAME_MAP.put("NATURAO_ORDER", "NATURAL_ORDER");
   }

   private static final Map<String, String> EXPANDED_TASK_NAME_MAP = new HashMap<>();
   static
   {
      EXPANDED_TASK_NAME_MAP.put("NARE", "NAME");
      EXPANDED_TASK_NAME_MAP.put("OVERALL_PERCENV_COMPLETE", "OVERALL_PERCENT_COMPLETE");
      EXPANDED_TASK_NAME_MAP.put("CONSTRAINU", "CONSTRAINT_FLAG");
      EXPANDED_TASK_NAME_MAP.put("CALENDAU", "CALENDAR");
   }

   private static final Map<String, String> MILESTONE_NAME_MAP = new HashMap<>();
   static
   {
      MILESTONE_NAME_MAP.put("NARE", "NAME");
      MILESTONE_NAME_MAP.put("OVERALL_PERCENV_COMPLETE", "OVERALL_PERCENT_COMPLETE");
      MILESTONE_NAME_MAP.put("CONSTRAINU", "CONSTRAINT_FLAG");
      MILESTONE_NAME_MAP.put("CALENDAU", "CALENDAR");
      MILESTONE_NAME_MAP.put("WBT", "WBS");
      MILESTONE_NAME_MAP.put("NATURAO_ORDER", "NATURAL_ORDER");
   }

   private static final Map<String, String> WORK_PATTERN_NAME_MAP = new HashMap<>();
   static
   {
      WORK_PATTERN_NAME_MAP.put("WORK_PATTERNID", "ID");
      WORK_PATTERN_NAME_MAP.put("NAMN", "NAME");
   }

   private static final Map<String, String> CALENDAR_NAME_MAP = new HashMap<>();
   static
   {
      CALENDAR_NAME_MAP.put("CALENDARID", "ID");
      CALENDAR_NAME_MAP.put("NAMK", "NAME");
   }

   private static final Map<String, String> PERMANENT_RESOURCE_NAME_MAP = new HashMap<>();
   static
   {
      PERMANENT_RESOURCE_NAME_MAP.put("PERMANENT_RESOURCEID", "ID");
      PERMANENT_RESOURCE_NAME_MAP.put("NASE", "NAME");
      PERMANENT_RESOURCE_NAME_MAP.put("CALENDAV", "CALENDAR");
   }

   private static final Map<String, String> CONSUMABLE_RESOURCE_RESOURCE_NAME_MAP = new HashMap<>();
   static
   {
      CONSUMABLE_RESOURCE_RESOURCE_NAME_MAP.put("CONSUMABLE_RESOURCEID", "ID");
      CONSUMABLE_RESOURCE_RESOURCE_NAME_MAP.put("NASE", "NAME");
      CONSUMABLE_RESOURCE_RESOURCE_NAME_MAP.put("CALENDAV", "CALENDAR");
   }

   private static final Map<String, String> LINK_NAME_MAP = new HashMap<>();
   static
   {
      LINK_NAME_MAP.put("LINKID", "ID");
      LINK_NAME_MAP.put("START_LAG_TIMEHOURS", "START_LAG_TIME");
      LINK_NAME_MAP.put("END_LAG_TIMEHOURS", "END_LAG_TIME");
      LINK_NAME_MAP.put("TYPI", "LINK_KIND");
   }

   private static final Map<String, String> ALLOCATION_NAME_MAP = new HashMap<>();
   static
   {
      ALLOCATION_NAME_MAP.put("PERMANENT_SCHEDUL_ALLOCATIONID", "ID");
      ALLOCATION_NAME_MAP.put("STARZ", "LINKABLE_START");
      ALLOCATION_NAME_MAP.put("ENJ", "LINKABLE_FINISH");
      ALLOCATION_NAME_MAP.put("ALLOCATEE_TO", "ALLOCATED_TO");
      ALLOCATION_NAME_MAP.put("EFFORW", "EFFORT");
      ALLOCATION_NAME_MAP.put("DELAAHOURS", "DELAY");
      ALLOCATION_NAME_MAP.put("ALLOCATIOP_OF", "ALLOCATION_OF");
   }

   private static final Map<String, String> PROGRESS_PERIOD_NAME_MAP = new HashMap<>();
   static
   {
      PROGRESS_PERIOD_NAME_MAP.put("PROGRESS_PERIODID", "ID");
   }

   private static final Map<String, String> EXCEPTION_NAME_MAP = new HashMap<>();
   static
   {
      EXCEPTION_NAME_MAP.put("EXCEPTIONNID", "ID");
   }

   private static final Map<String, String> COMPLETED_SECTION_NAME_MAP = new HashMap<>();
   static
   {
      COMPLETED_SECTION_NAME_MAP.put("TASK_COMPLETED_SECTIONID", "ID");
   }

   private static final Map<String, String> SKILL_NAME_MAP = new HashMap<>();
   static
   {
      SKILL_NAME_MAP.put("PERM_RESOURCE_SKILLID", "ID");
   }

   private static final Map<String, String> EXCEPTION_ASSIGNMENT_MAP = new HashMap<>();
   static
   {
      EXCEPTION_ASSIGNMENT_MAP.put("STARU_DATE", "START_DATE");
      EXCEPTION_ASSIGNMENT_MAP.put("ENE_DATE", "END_DATE");
   }

   private static final Map<String, String> TIME_ENTRY_NAME_MAP = new HashMap<>();
   static
   {
      TIME_ENTRY_NAME_MAP.put("EXCEPTIOP", "EXCEPTION");
   }
}