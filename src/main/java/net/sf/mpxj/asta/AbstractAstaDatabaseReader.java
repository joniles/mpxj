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

package net.sf.mpxj.asta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import net.sf.mpxj.DayType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.AbstractProjectFileReader;

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

         List<Row> rows = getRows("project_summary", Collections.emptyMap());
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
      List<Row> projectSummaryRows = getRows("project_summary", m_projectKey);
      List<Row> progressPeriodRows = getRows("progress_period", m_projectKey);
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
      List<Row> rows = getRows("exceptionn", Collections.emptyMap());
      Map<Integer, DayType> exceptionMap = m_reader.createExceptionTypeMap(rows);

      rows = getRows("work_pattern", Collections.emptyMap());
      Map<Integer, Row> workPatternMap = m_reader.createWorkPatternMap(rows);

      rows = getRows("work_pattern_assignment", Collections.emptyMap());
      Map<Integer, List<Row>> workPatternAssignmentMap = m_reader.createWorkPatternAssignmentMap(rows);

      rows = sortRows(getRows("exception_assignment", Collections.emptyMap()), "EXCEPTION_ASSIGNMENT_ID", "ORDF");
      Map<Integer, List<Row>> exceptionAssignmentMap = m_reader.createExceptionAssignmentMap(rows);

      rows = sortRows(getRows("time_entry", Collections.emptyMap()), "TIME_ENTRYID", "ORDF");
      Map<Integer, List<Row>> timeEntryMap = m_reader.createTimeEntryMap(rows);

      rows = sortRows(getRows("calendar", m_projectKey), "CALENDARID");
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
   }

   /**
    * Process resources.
    */
   private void processResources() throws AstaDatabaseException
   {
      List<Row> permanentRows = sortRows(getRows("permanent_resource", m_projectKey), "PERMANENT_RESOURCEID");
      List<Row> consumableRows = sortRows(getRows("consumable_resource", m_projectKey), "CONSUMABLE_RESOURCEID");
      m_reader.processResources(permanentRows, consumableRows);
   }

   /**
    * Process tasks.
    */
   private void processTasks() throws AstaDatabaseException
   {
      List<Row> bars = getRows("bar", m_projectKey);
      List<Row> expandedTasks = getRows("expanded_task", m_projectKey);
      List<Row> tasks = getRows("task", m_projectKey);
      List<Row> milestones = getRows("milestone", m_projectKey);
      List<Row> hammocks = getRows("hammock_task", m_projectKey);
      m_reader.processTasks(bars, expandedTasks, tasks, milestones, hammocks);
   }

   /**
    * Process predecessors.
    */
   private void processPredecessors() throws AstaDatabaseException
   {
      List<Row> rows = sortRows(getRows("link", m_projectKey), "LINKID");
      List<Row> completedSections = getRows("task_completed_section", m_projectKey);
      m_reader.processPredecessors(rows, completedSections);
   }

   /**
    * Process resource assignments.
    */
   private void processAssignments() throws AstaDatabaseException
   {
      List<Row> allocationRows = getRows("permanent_schedul_allocation", m_projectKey);
      List<Row> skillRows = getRows("perm_resource_skill", m_projectKey);
      List<Row> permanentAssignments = sortRows(joinRows(allocationRows, "ALLOCATIOP_OF", "PERM_RESOURCE_SKILL", skillRows, "PERM_RESOURCE_SKILLID"), "PERMANENT_SCHEDUL_ALLOCATIONID");
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
      m_projectKey = Collections.singletonMap("PROJID", m_projectID);
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

   /**
    * Very basic implementation of an inner join between two result sets.
    *
    * @param leftRows left result set
    * @param leftColumn left foreign key column
    * @param rightTable right table name
    * @param rightRows right result set
    * @param rightColumn right primary key column
    * @return joined result set
    */
   private List<Row> joinRows(List<Row> leftRows, String leftColumn, String rightTable, List<Row> rightRows, String rightColumn)
   {
      List<Row> result = new ArrayList<>();

      RowComparator leftComparator = new RowComparator(leftColumn);
      RowComparator rightComparator = new RowComparator(rightColumn);
      leftRows.sort(leftComparator);
      rightRows.sort(rightComparator);

      ListIterator<Row> rightIterator = rightRows.listIterator();
      Row rightRow = rightIterator.hasNext() ? rightIterator.next() : null;

      for (Row leftRow : leftRows)
      {
         Integer leftValue = leftRow.getInteger(leftColumn);
         boolean match = false;

         while (rightRow != null)
         {
            Integer rightValue = rightRow.getInteger(rightColumn);
            int comparison = leftValue.compareTo(rightValue);
            if (comparison == 0)
            {
               match = true;
               break;
            }

            if (comparison < 0)
            {
               if (rightIterator.hasPrevious())
               {
                  rightRow = rightIterator.previous();
               }
               break;
            }

            rightRow = rightIterator.next();
         }

         if (match && rightRow != null)
         {
            Map<String, Object> newMap = new HashMap<>(((MapRow) leftRow).getMap());

            for (Map.Entry<String, Object> entry : ((MapRow) rightRow).getMap().entrySet())
            {
               String key = entry.getKey();
               if (newMap.containsKey(key))
               {
                  key = rightTable + "." + key;
               }
               newMap.put(key, entry.getValue());
            }

            result.add(new MapRow(newMap));
         }
      }

      return result;
   }

   private AstaReader m_reader;
   private Integer m_projectID;
   private Map<String, Integer> m_projectKey;
}