/*
 * file:       SynchroReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mpxj.ChildResourceContainer;
import org.mpxj.ChildTaskContainer;
import org.mpxj.ConstraintType;
import java.time.DayOfWeek;
import org.mpxj.EventManager;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.LocalTimeRange;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * Reads Synchro SP files.
 */
public final class SynchroReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try
      {
         SynchroLogger.openLogFile();

         m_calendarMap = new HashMap<>();
         m_taskMap = new HashMap<>();
         m_predecessorMap = new LinkedHashMap<>();
         m_resourceMap = new HashMap<>();

         m_data = new SynchroData();
         m_data.process(inputStream);
         return read();
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }

      finally
      {
         SynchroLogger.closeLogFile();

         m_data = null;
         m_calendarMap = null;
         m_taskMap = null;
         m_predecessorMap = null;
         m_resourceMap = null;
      }
   }

   /**
    * Reads data from the SP file.
    *
    * @return Project File instance
    */
   private ProjectFile read() throws Exception
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      m_project.getProjectProperties().setFileApplication("Synchro");
      m_project.getProjectProperties().setFileType("SP");

      addListenersToProject(m_project);

      processCalendars();
      processResources();
      processTasks();
      processPredecessors();
      m_project.readComplete();

      return m_project;
   }

   /**
    * Extract calendar data.
    */
   private void processCalendars() throws IOException
   {
      CalendarReader reader = new CalendarReader(m_data.getTableData("Calendars"));
      reader.read();

      for (MapRow row : reader.getRows())
      {
         processCalendar(row);
      }

      ProjectCalendar calendar = m_calendarMap.get(reader.getDefaultCalendarUUID());
      if (calendar == null)
      {
         calendar = m_project.getCalendars().findOrCreateDefaultCalendar();
      }
      m_project.setDefaultCalendar(calendar);
   }

   /**
    * Extract data for a single calendar.
    *
    * @param row calendar data
    */
   private void processCalendar(MapRow row)
   {
      ProjectCalendar calendar = m_project.addCalendar();

      Map<UUID, List<LocalTimeRange>> dayTypeMap = processDayTypes(row.getRows("DAY_TYPES"));

      calendar.setName(row.getString("NAME"));

      processRanges(dayTypeMap.get(row.getUUID("SUNDAY_DAY_TYPE")), calendar.addCalendarHours(DayOfWeek.SUNDAY));
      processRanges(dayTypeMap.get(row.getUUID("MONDAY_DAY_TYPE")), calendar.addCalendarHours(DayOfWeek.MONDAY));
      processRanges(dayTypeMap.get(row.getUUID("TUESDAY_DAY_TYPE")), calendar.addCalendarHours(DayOfWeek.TUESDAY));
      processRanges(dayTypeMap.get(row.getUUID("WEDNESDAY_DAY_TYPE")), calendar.addCalendarHours(DayOfWeek.WEDNESDAY));
      processRanges(dayTypeMap.get(row.getUUID("THURSDAY_DAY_TYPE")), calendar.addCalendarHours(DayOfWeek.THURSDAY));
      processRanges(dayTypeMap.get(row.getUUID("FRIDAY_DAY_TYPE")), calendar.addCalendarHours(DayOfWeek.FRIDAY));
      processRanges(dayTypeMap.get(row.getUUID("SATURDAY_DAY_TYPE")), calendar.addCalendarHours(DayOfWeek.SATURDAY));

      for (DayOfWeek day : DayOfWeek.values())
      {
         calendar.setWorkingDay(day, !calendar.getCalendarHours(day).isEmpty());
      }

      for (MapRow assignment : row.getRows("DAY_TYPE_ASSIGNMENTS"))
      {
         LocalDate date = LocalDateHelper.getLocalDate(assignment.getDate("DATE"));
         processRanges(dayTypeMap.get(assignment.getUUID("DAY_TYPE_UUID")), calendar.addCalendarException(date));
      }

      m_calendarMap.put(row.getUUID("UUID"), calendar);
      m_eventManager.fireCalendarReadEvent(calendar);
   }

   /**
    * Populate time ranges.
    *
    * @param ranges time ranges from a Synchro table
    * @param container time range container
    */
   private void processRanges(List<LocalTimeRange> ranges, ProjectCalendarHours container)
   {
      if (ranges != null)
      {
         container.addAll(ranges);
      }
   }

   /**
    * Extract day type definitions.
    *
    * @param types Synchro day type rows
    * @return Map of day types by UUID
    */
   private Map<UUID, List<LocalTimeRange>> processDayTypes(List<MapRow> types)
   {
      Map<UUID, List<LocalTimeRange>> map = new HashMap<>();
      for (MapRow row : types)
      {
         List<LocalTimeRange> ranges = new ArrayList<>();
         for (MapRow range : row.getRows("TIME_RANGES"))
         {
            ranges.add(new LocalTimeRange(range.getTime("START"), range.getTime("END")));
         }
         map.put(row.getUUID("UUID"), ranges);
      }

      return map;
   }

   /**
    * Extract resource data.
    */
   private void processResources() throws IOException
   {
      if (m_data.getVersion().atLeast(Synchro.VERSION_6_2_0))
      {
         process62Resources();
      }
      else
      {
         process50Resources();
      }
   }

   private void process50Resources() throws IOException
   {
      CompanyReader reader = new CompanyReader(m_data.getTableData("Companies"));
      reader.read();
      for (MapRow companyRow : reader.getRows())
      {
         // TODO: need to sort by type as well as by name!
         for (MapRow resourceRow : sort(companyRow.getRows("RESOURCES"), "NAME"))
         {
            processResource(m_project, resourceRow);
         }
      }
   }

   private void process62Resources() throws IOException
   {
      ResourceReader reader = new ResourceReader(m_data.getTableData("Resources"));
      reader.read();

      // TODO: need to sort by type as well as by name!
      for (MapRow resourceRow : sort(reader.getRows(), "NAME"))
      {
         processResource(m_project, resourceRow);
      }
   }

   /**
    * Extract data for a single resource.
    *
    * @param parent parent resource container
    * @param row Synchro resource data
    */
   private void processResource(ChildResourceContainer parent, MapRow row)
   {
      Resource resource = parent.addResource();
      resource.setName(row.getString("NAME"));
      resource.setGUID(row.getUUID("UUID"));
      resource.setEmailAddress(row.getString("EMAIL"));
      resource.setHyperlink(row.getString("URL"));
      resource.setNotes(getNotes(row.getRows("COMMENTARY")));
      resource.setDescription(row.getString("DESCRIPTION"));
      resource.setSupplyReference(row.getString("SUPPLY_REFERENCE"));
      resource.setActive(true);
      resource.setResourceID(row.getString("ID"));

      List<MapRow> resources = row.getRows("RESOURCES");
      if (resources != null)
      {
         for (MapRow childResource : sort(resources, "NAME"))
         {
            processResource(resource, childResource);
         }
      }

      m_resourceMap.put(resource.getGUID(), resource);
      m_eventManager.fireResourceReadEvent(resource);
   }

   /**
    * Extract task data.
    */
   private void processTasks() throws IOException
   {
      TaskReader reader = new TaskReader(m_data.getTableData("Tasks"));
      reader.read();
      for (MapRow row : reader.getRows())
      {
         processTask(m_project, row);
      }
      updateDates();
   }

   /**
    * Extract data for a single task.
    *
    * @param parent task parent
    * @param row Synchro task data
    */
   private void processTask(ChildTaskContainer parent, MapRow row) throws IOException
   {
      Task task = parent.addTask();
      task.setName(row.getString("NAME"));
      task.setGUID(row.getUUID("UUID"));
      task.setActivityID(row.getString("ID"));
      task.setDuration(row.getDuration("PLANNED_DURATION"));
      task.setRemainingDuration(row.getDuration("REMAINING_DURATION"));
      task.setHyperlink(row.getString("URL"));
      task.setPercentageComplete(row.getDouble("PERCENT_COMPLETE"));
      task.setNotes(getNotes(row.getRows("COMMENTARY")));
      task.setMilestone(task.getDuration() != null && task.getDuration().getDuration() == 0);

      ProjectCalendar calendar = m_calendarMap.get(row.getUUID("CALENDAR_UUID"));
      if (calendar != m_project.getDefaultCalendar())
      {
         task.setCalendar(calendar);
      }

      switch (row.getInteger("STATUS").intValue())
      {
         case 1: // Planned
         {
            task.setStart(row.getDate("PLANNED_START"));
            if (task.getStart() != null && task.getDuration() != null)
            {
               task.setFinish(task.getEffectiveCalendar().getDate(task.getStart(), task.getDuration()));
            }
            break;
         }

         case 2: // Started
         {
            task.setActualStart(row.getDate("ACTUAL_START"));
            task.setStart(task.getActualStart());
            task.setFinish(row.getDate("ESTIMATED_FINISH"));
            if (task.getFinish() == null)
            {
               task.setFinish(row.getDate("PLANNED_FINISH"));
            }
            break;
         }

         case 3: // Finished
         {
            task.setActualStart(row.getDate("ACTUAL_START"));
            task.setActualFinish(row.getDate("ACTUAL_FINISH"));
            task.setPercentageComplete(Double.valueOf(100.0));
            task.setStart(task.getActualStart());
            task.setFinish(task.getActualFinish());
            break;
         }
      }

      setConstraints(task, row);

      processChildTasks(task, row);

      m_taskMap.put(task.getGUID(), task);
      m_eventManager.fireTaskReadEvent(task);

      List<MapRow> predecessors = row.getRows("PREDECESSORS");
      if (predecessors != null && !predecessors.isEmpty())
      {
         m_predecessorMap.put(task, predecessors);
      }

      List<MapRow> resourceAssignmnets = row.getRows("RESOURCE_ASSIGNMENTS");
      if (resourceAssignmnets != null && !resourceAssignmnets.isEmpty())
      {
         processResourceAssignments(task, resourceAssignmnets);
      }
   }

   /**
    * Extract child task data.
    *
    * @param task MPXJ task
    * @param row Synchro task data
    */
   private void processChildTasks(Task task, MapRow row) throws IOException
   {
      List<MapRow> tasks = row.getRows("TASKS");
      if (tasks != null)
      {
         for (MapRow childTask : tasks)
         {
            processTask(task, childTask);
         }
      }
   }

   /**
    * Extract predecessor data.
    */
   private void processPredecessors()
   {
      for (Map.Entry<Task, List<MapRow>> entry : m_predecessorMap.entrySet())
      {
         Task task = entry.getKey();
         List<MapRow> predecessors = entry.getValue();
         for (MapRow predecessor : predecessors)
         {
            processPredecessor(task, predecessor);
         }
      }
   }

   /**
    * Extract data for a single predecessor.
    *
    * @param task parent task
    * @param row Synchro predecessor data
    */
   private void processPredecessor(Task task, MapRow row)
   {
      Task predecessor = m_taskMap.get(row.getUUID("PREDECESSOR_UUID"));
      if (predecessor != null)
      {
         Relation relation = task.addPredecessor(new Relation.Builder()
            .predecessorTask(predecessor)
            .type(row.getRelationType("RELATION_TYPE"))
            .lag(row.getDuration("LAG")));
         m_eventManager.fireRelationReadEvent(relation);
      }
   }

   /**
    * Extract resource assignments for a task.
    *
    * @param task parent task
    * @param assignments list of Synchro resource assignment data
    */
   private void processResourceAssignments(Task task, List<MapRow> assignments)
   {
      for (MapRow row : assignments)
      {
         processResourceAssignment(task, row);
      }
   }

   /**
    * Extract data for a single resource assignment.
    *
    * @param task parent task
    * @param row Synchro resource assignment
    */
   private void processResourceAssignment(Task task, MapRow row)
   {
      Resource resource = m_resourceMap.get(row.getUUID("RESOURCE_UUID"));
      ResourceAssignment assignment = task.addResourceAssignment(resource);
      m_eventManager.fireAssignmentReadEvent(assignment);
   }

   /**
    * Map Synchro constraints to MPXJ constraints.
    *
    * @param task task
    * @param row Synchro constraint data
    */
   private void setConstraints(Task task, MapRow row)
   {
      ConstraintType constraintType = null;
      LocalDateTime constraintDate = null;
      LocalDateTime lateDate = row.getDate("CONSTRAINT_LATE_DATE");
      LocalDateTime earlyDate = row.getDate("CONSTRAINT_EARLY_DATE");

      switch (row.getInteger("CONSTRAINT_TYPE").intValue())
      {
         case 2: // Cannot Reschedule
         {
            constraintType = ConstraintType.MUST_START_ON;
            constraintDate = task.getStart();
            break;
         }

         case 12: //Finish Between
         {
            constraintType = ConstraintType.MUST_FINISH_ON;
            constraintDate = lateDate;
            break;
         }

         case 10: // Finish On or After
         {
            constraintType = ConstraintType.FINISH_NO_EARLIER_THAN;
            constraintDate = earlyDate;
            break;
         }

         case 11: // Finish On or Before
         {
            constraintType = ConstraintType.FINISH_NO_LATER_THAN;
            constraintDate = lateDate;
            break;
         }

         case 13: // Mandatory Start
         case 5: // Start On
         case 9: // Finish On
         {
            constraintType = ConstraintType.MUST_START_ON;
            constraintDate = earlyDate;
            break;
         }

         case 14: // Mandatory Finish
         {
            constraintType = ConstraintType.MUST_FINISH_ON;
            constraintDate = earlyDate;
            break;
         }

         case 4: // Start As Late As Possible
         {
            constraintType = ConstraintType.AS_LATE_AS_POSSIBLE;
            break;
         }

         case 3: // Start As Soon As Possible
         {
            constraintType = ConstraintType.AS_SOON_AS_POSSIBLE;
            break;
         }

         case 8: // Start Between
         {
            constraintType = ConstraintType.AS_SOON_AS_POSSIBLE;
            constraintDate = earlyDate;
            break;
         }

         case 6: // Start On or Before
         {
            constraintType = ConstraintType.START_NO_LATER_THAN;
            constraintDate = earlyDate;
            break;
         }

         case 15: // Work Between
         {
            constraintType = ConstraintType.START_NO_EARLIER_THAN;
            constraintDate = earlyDate;
            break;
         }
      }
      task.setConstraintType(constraintType);
      task.setConstraintDate(constraintDate);
   }

   /**
    * Common mechanism to convert Synchro commentary records into notes.
    *
    * @param rows commentary table rows
    * @return note text
    */
   private String getNotes(List<MapRow> rows)
   {
      String result = null;
      if (rows != null && !rows.isEmpty())
      {
         StringBuilder sb = new StringBuilder();
         for (MapRow row : rows)
         {
            sb.append(row.getString("TITLE"));
            sb.append('\n');
            sb.append(row.getString("TEXT"));
            sb.append("\n\n");
         }
         result = sb.toString();
      }
      return result;
   }

   /**
    * Sort MapRows based on a named attribute.
    *
    * @param rows map rows to sort
    * @param attribute attribute to sort on
    * @return list argument (allows method chaining)
    */
   private List<MapRow> sort(List<MapRow> rows, final String attribute)
   {
      rows.sort((o1, o2) -> {
         String value1 = o1.getString(attribute);
         String value2 = o2.getString(attribute);
         return value1.compareTo(value2);
      });
      return rows;
   }

   /**
    * Recursively update parent task dates.
    */
   private void updateDates()
   {
      for (Task task : m_project.getChildTasks())
      {
         updateDates(task);
      }
   }

   /**
    * Recursively update parent task dates.
    *
    * @param parentTask parent task
    */
   private void updateDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         LocalDateTime plannedStartDate = null;
         LocalDateTime plannedFinishDate = null;

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);
            plannedStartDate = LocalDateTimeHelper.min(plannedStartDate, task.getStart());
            plannedFinishDate = LocalDateTimeHelper.max(plannedFinishDate, task.getFinish());
         }

         parentTask.setStart(plannedStartDate);
         parentTask.setFinish(plannedFinishDate);
      }
   }

   private SynchroData m_data;
   private ProjectFile m_project;
   private EventManager m_eventManager;
   private Map<UUID, ProjectCalendar> m_calendarMap;
   private Map<UUID, Task> m_taskMap;
   private Map<Task, List<MapRow>> m_predecessorMap;
   private Map<UUID, Resource> m_resourceMap;
}
