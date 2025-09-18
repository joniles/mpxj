/*
 * file:       SureTrakDatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package org.mpxj.primavera.suretrak;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.ChildTaskContainer;
import java.time.DayOfWeek;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.RecurrenceType;
import org.mpxj.RecurringData;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.common.AlphanumComparator;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.primavera.common.MapRow;
import org.mpxj.primavera.common.Table;
import org.mpxj.reader.AbstractProjectFileReader;

/**
 * Reads schedule data from a SureTrak multi-file database in a directory.
 */
public final class SureTrakDatabaseReader extends AbstractProjectFileReader
{
   /**
    * Convenience method which locates the first SureTrak database in a directory
    * and opens it.
    *
    * @param directory directory containing a SureTrak database
    * @return ProjectFile instance
    */
   public static final ProjectFile setProjectNameAndRead(File directory) throws MPXJException
   {
      List<String> projects = listProjectNames(directory);
      if (projects.isEmpty())
      {
         return null;
      }

      SureTrakDatabaseReader reader = new SureTrakDatabaseReader();
      reader.setProjectName(projects.get(0));
      return reader.read(directory);
   }

   /**
    * Retrieve a list of the available SureTrak project names from a directory.
    *
    * @param directory name of the directory containing SureTrak files
    * @return list of project names
    */
   public static final List<String> listProjectNames(String directory)
   {
      return listProjectNames(new File(directory));
   }

   /**
    * Retrieve a list of the available SureTrak project names from a directory.
    *
    * @param directory directory containing SureTrak files
    * @return list of project names
    */
   public static final List<String> listProjectNames(File directory)
   {
      List<String> result = new ArrayList<>();

      File[] files = directory.listFiles((dir, name) -> name.toUpperCase().endsWith(".DIR"));

      if (files != null)
      {
         for (File file : files)
         {
            String fileName = file.getName();
            String prefix = fileName.substring(0, fileName.length() - 4);
            result.add(prefix);
         }
      }

      Collections.sort(result);

      return result;
   }

   /**
    * Set the project name (file name prefix) used to identify which database is read from the directory.
    * There may potentially be more than one database in a directory.
    *
    * @param projectName project name
    */
   public void setProjectName(String projectName)
   {
      m_projectName = projectName;
   }

   @Override public ProjectFile read(File directory) throws MPXJException
   {
      if (!directory.isDirectory())
      {
         throw new MPXJException("Directory expected");
      }

      try
      {
         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceID(true);
         config.setAutoResourceUniqueID(true);
         config.setAutoTaskID(true);
         config.setAutoTaskUniqueID(true);
         config.setAutoOutlineLevel(true);
         config.setAutoOutlineNumber(true);
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("SureTrak");
         m_projectFile.getProjectProperties().setFileType("STW");

         addListenersToProject(m_projectFile);

         m_tables = new DatabaseReader().process(directory, m_projectName);
         m_definitions = new HashMap<>();
         m_calendarMap = new HashMap<>();
         m_resourceMap = new HashMap<>();
         m_wbsMap = new HashMap<>();
         m_activityMap = new HashMap<>();

         readProjectHeader();
         readDefinitions();
         readCalendars();
         readHolidays();
         readResources();
         readTasks();
         readRelationships();
         readResourceAssignments();
         m_projectFile.readComplete();

         return m_projectFile;
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_tables = null;
         m_definitions = null;
         m_wbsFormat = null;
         m_calendarMap = null;
         m_resourceMap = null;
         m_wbsMap = null;
         m_activityMap = null;
      }
   }

   @Override public List<ProjectFile> readAll(File directory) throws MPXJException
   {
      List<ProjectFile> projects = new ArrayList<>();

      for (String name : listProjectNames(directory))
      {
         SureTrakDatabaseReader reader = new SureTrakDatabaseReader();
         reader.setProjectName(name);
         projects.add(reader.read(directory));
      }

      return projects;
   }

   /**
    * Read general project properties.
    */
   private void readProjectHeader()
   {
      // No header data read
   }

   /**
    * Extract definition records from the table and divide into groups.
    */
   private void readDefinitions()
   {
      for (MapRow row : m_tables.get("TTL"))
      {
         Integer id = row.getInteger("DEFINITION_ID");
         List<MapRow> list = m_definitions.computeIfAbsent(id, k -> new ArrayList<>());
         list.add(row);
      }

      List<MapRow> rows = m_definitions.get(WBS_FORMAT_ID);
      if (rows != null)
      {
         m_wbsFormat = new SureTrakWbsFormat(rows.get(0));
      }
   }

   /**
    * Read project calendars.
    */
   private void readCalendars()
   {
      Table cal = m_tables.get("CAL");
      for (MapRow row : cal)
      {
         ProjectCalendar calendar = m_projectFile.addCalendar();
         m_calendarMap.put(row.getInteger("CALENDAR_ID"), calendar);
         Integer[] days =
         {
            row.getInteger("SUNDAY_HOURS"),
            row.getInteger("MONDAY_HOURS"),
            row.getInteger("TUESDAY_HOURS"),
            row.getInteger("WEDNESDAY_HOURS"),
            row.getInteger("THURSDAY_HOURS"),
            row.getInteger("FRIDAY_HOURS"),
            row.getInteger("SATURDAY_HOURS")
         };

         calendar.setName(row.getString("NAME"));
         readHours(calendar, DayOfWeek.SUNDAY, days[0]);
         readHours(calendar, DayOfWeek.MONDAY, days[1]);
         readHours(calendar, DayOfWeek.TUESDAY, days[2]);
         readHours(calendar, DayOfWeek.WEDNESDAY, days[3]);
         readHours(calendar, DayOfWeek.THURSDAY, days[4]);
         readHours(calendar, DayOfWeek.FRIDAY, days[5]);
         readHours(calendar, DayOfWeek.SATURDAY, days[6]);

         int workingDaysPerWeek = 0;
         for (DayOfWeek day : DayOfWeek.values())
         {
            if (calendar.isWorkingDay(day))
            {
               ++workingDaysPerWeek;
            }
         }

         Integer workingHours = null;
         for (int index = 0; index < 7; index++)
         {
            if (days[index].intValue() != 0)
            {
               workingHours = days[index];
               break;
            }
         }

         if (workingHours != null)
         {
            int workingHoursPerDay = countHours(workingHours);
            int minutesPerDay = workingHoursPerDay * 60;
            int minutesPerWeek = minutesPerDay * workingDaysPerWeek;
            int minutesPerMonth = 4 * minutesPerWeek;
            int minutesPerYear = 52 * minutesPerWeek;

            calendar.setCalendarMinutesPerDay(Integer.valueOf(minutesPerDay));
            calendar.setCalendarMinutesPerWeek(Integer.valueOf(minutesPerWeek));
            calendar.setCalendarMinutesPerMonth(Integer.valueOf(minutesPerMonth));
            calendar.setCalendarMinutesPerYear(Integer.valueOf(minutesPerYear));
         }

         m_eventManager.fireCalendarReadEvent(calendar);
      }

      m_projectFile.setDefaultCalendar(m_projectFile.getCalendars().findOrCreateDefaultCalendar());
   }

   /**
    * Reads the integer representation of calendar hours for a given
    * day and populates the calendar.
    *
    * @param calendar parent calendar
    * @param day target day
    * @param hours working hours
    */
   private void readHours(ProjectCalendar calendar, DayOfWeek day, Integer hours)
   {
      int value = hours.intValue();
      int startHour = 0;
      ProjectCalendarHours calendarHours = calendar.addCalendarHours(day);
      calendar.setWorkingDay(day, false);

      while (value != 0)
      {
         // Move forward until we find a working hour
         while (startHour < 24 && (value & 0x1) == 0)
         {
            value = value >> 1;
            ++startHour;
         }

         // No more working hours, bail out
         if (startHour >= 24)
         {
            break;
         }

         // Move forward until we find the end of the working hours
         int endHour = startHour;
         while (endHour < 24 && (value & 0x1) != 0)
         {
            value = value >> 1;
            ++endHour;
         }

         calendar.setWorkingDay(day, true);
         calendarHours.add(new LocalTimeRange(LocalTime.of(startHour, 0), LocalTime.of(endHour == 24 ? 0 : endHour, 0)));
         startHour = endHour;
      }
   }

   /**
    * Count the number of working hours in a day, based in the
    * integer representation of the working hours.
    *
    * @param hours working hours
    * @return number of hours
    */
   private int countHours(Integer hours)
   {
      int value = hours.intValue();
      int hoursPerDay = 0;
      int hour = 0;
      while (value > 0)
      {
         // Move forward until we find a working hour
         while (hour < 24)
         {
            if ((value & 0x1) != 0)
            {
               ++hoursPerDay;
            }
            value = value >> 1;
            ++hour;
         }
      }
      return hoursPerDay;
   }

   /**
    * Read holidays from the database and create calendar exceptions.
    */
   private void readHolidays()
   {
      for (MapRow row : m_tables.get("HOL"))
      {
         ProjectCalendar calendar = m_calendarMap.get(row.getInteger("CALENDAR_ID"));
         if (calendar != null)
         {
            LocalDate date = LocalDateHelper.getLocalDate(row.getDate("DATE"));

            if (row.getBoolean("ANNUAL"))
            {
               // TODO set end date based on project end date?
               RecurringData recurring = new RecurringData();
               recurring.setRecurrenceType(RecurrenceType.YEARLY);
               recurring.setYearlyAbsoluteFromDate(date);
               recurring.setStartDate(date);
               calendar.addCalendarException(recurring);
            }
            else
            {
               calendar.addCalendarException(date);
            }
         }
      }
   }

   /**
    * Read resources.
    */
   private void readResources()
   {
      m_resourceMap = new HashMap<>();
      for (MapRow row : m_tables.get("RLB"))
      {
         Resource resource = m_projectFile.addResource();
         setFields(RESOURCE_FIELDS, row, resource);
         ProjectCalendar calendar = m_calendarMap.get(row.getInteger("CALENDAR_ID"));
         if (calendar != null)
         {
            ProjectCalendar baseCalendar = m_calendarMap.get(row.getInteger("BASE_CALENDAR_ID"));
            calendar.setParent(baseCalendar);
            resource.setCalendar(calendar);
         }
         m_resourceMap.put(resource.getCode(), resource);
         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Read tasks.
    */
   private void readTasks()
   {
      readWbs();
      readActivities();
      updateDates();
   }

   /**
    * Read the WBS.
    */
   private void readWbs()
   {
      Map<Integer, List<MapRow>> levelMap = new HashMap<>();
      List<MapRow> table = m_definitions.get(WBS_ENTRIES_ID);
      if (table != null && m_wbsFormat != null)
      {
         for (MapRow row : table)
         {
            m_wbsFormat.parseRawValue(row.getString("TEXT1"));
            Integer level = m_wbsFormat.getLevel();
            List<MapRow> items = levelMap.computeIfAbsent(level, k -> new ArrayList<>());
            items.add(row);
         }

         int level = 1;
         while (true)
         {
            List<MapRow> items = levelMap.get(Integer.valueOf(level++));
            if (items == null)
            {
               break;
            }

            for (MapRow row : items)
            {
               m_wbsFormat.parseRawValue(row.getString("TEXT1"));
               String parentWbsValue = m_wbsFormat.getFormattedParentValue();
               String wbsValue = m_wbsFormat.getFormattedValue();
               row.setObject("WBS", wbsValue);
               row.setObject("PARENT_WBS", parentWbsValue);
            }

            final AlphanumComparator comparator = new AlphanumComparator();
            items.sort((o1, o2) -> comparator.compare(o1.getString("WBS"), o2.getString("WBS")));

            for (MapRow row : items)
            {
               String wbs = row.getString("WBS");
               ChildTaskContainer parent = m_wbsMap.get(row.getString("PARENT_WBS"));
               if (parent == null)
               {
                  parent = m_projectFile;
               }

               Task task = parent.addTask();
               String name = row.getString("TEXT2");
               if (name == null || name.isEmpty())
               {
                  name = wbs;
               }
               task.setName(name);
               task.setWBS(wbs);
               task.setSummary(true);
               m_wbsMap.put(wbs, task);
               m_eventManager.fireTaskReadEvent(task);
            }
         }
      }
   }

   /**
    * Read activities.
    */
   private void readActivities()
   {
      List<MapRow> items = new ArrayList<>();
      for (MapRow row : m_tables.get("ACT"))
      {
         items.add(row);
      }
      final AlphanumComparator comparator = new AlphanumComparator();
      items.sort((o1, o2) -> comparator.compare(o1.getString("ACTIVITY_ID"), o2.getString("ACTIVITY_ID")));

      for (MapRow row : items)
      {
         String activityID = row.getString("ACTIVITY_ID");

         String wbs;
         if (m_wbsFormat == null)
         {
            wbs = null;
         }
         else
         {
            m_wbsFormat.parseRawValue(row.getString("WBS"));
            wbs = m_wbsFormat.getFormattedValue();
         }

         ChildTaskContainer parent = m_wbsMap.get(wbs);
         if (parent == null)
         {
            parent = m_projectFile;
         }

         Task task = parent.addTask();
         setFields(TASK_FIELDS, row, task);
         task.setStart(task.getEarlyStart());
         task.setFinish(task.getEarlyFinish());
         task.setMilestone(task.getDuration().getDuration() == 0);
         task.setWBS(wbs);
         Duration duration = task.getDuration();
         Duration remainingDuration = task.getRemainingDuration();
         task.setActualDuration(Duration.getInstance(duration.getDuration() - remainingDuration.getDuration(), TimeUnit.HOURS));
         m_activityMap.put(activityID, task);
         m_eventManager.fireTaskReadEvent(task);
      }
   }

   /**
    * Read task relationships.
    */
   private void readRelationships()
   {
      for (MapRow row : m_tables.get("REL"))
      {
         Task predecessor = m_activityMap.get(row.getString("PREDECESSOR_ACTIVITY_ID"));
         Task successor = m_activityMap.get(row.getString("SUCCESSOR_ACTIVITY_ID"));
         if (predecessor != null && successor != null)
         {
            Relation relation = successor.addPredecessor(new Relation.Builder()
               .predecessorTask(predecessor)
               .type(row.getRelationType("TYPE"))
               .lag(row.getDuration("LAG")));
            m_eventManager.fireRelationReadEvent(relation);
         }
      }
   }

   /**
    * Read resource assignments.
    */
   private void readResourceAssignments()
   {
      for (MapRow row : m_tables.get("RES"))
      {
         Task task = m_activityMap.get(row.getString("ACTIVITY_ID"));
         Resource resource = m_resourceMap.get(row.getString("RESOURCE_ID"));
         if (task != null && resource != null)
         {
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Ensure summary tasks have dates.
    */
   private void updateDates()
   {
      for (Task task : m_projectFile.getChildTasks())
      {
         updateDates(task);
      }
   }

   /**
    * See the notes above.
    *
    * @param parentTask parent task.
    */
   private void updateDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         int finished = 0;
         LocalDateTime startDate = parentTask.getStart();
         LocalDateTime finishDate = parentTask.getFinish();
         LocalDateTime actualStartDate = parentTask.getActualStart();
         LocalDateTime actualFinishDate = parentTask.getActualFinish();
         LocalDateTime earlyStartDate = parentTask.getEarlyStart();
         LocalDateTime earlyFinishDate = parentTask.getEarlyFinish();
         LocalDateTime lateStartDate = parentTask.getLateStart();
         LocalDateTime lateFinishDate = parentTask.getLateFinish();

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            startDate = LocalDateTimeHelper.min(startDate, task.getStart());
            finishDate = LocalDateTimeHelper.max(finishDate, task.getFinish());
            actualStartDate = LocalDateTimeHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = LocalDateTimeHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getEarlyFinish());
            lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getLateFinish());

            if (task.getActualFinish() != null)
            {
               ++finished;
            }
         }

         parentTask.setStart(startDate);
         parentTask.setFinish(finishDate);
         parentTask.setActualStart(actualStartDate);
         parentTask.setEarlyStart(earlyStartDate);
         parentTask.setEarlyFinish(earlyFinishDate);
         parentTask.setLateStart(lateStartDate);
         parentTask.setLateFinish(lateFinishDate);

         //
         // Only if all child tasks have actual finish dates do we
         // set the actual finish date on the parent task.
         //
         if (finished == parentTask.getChildTasks().size())
         {
            parentTask.setActualFinish(actualFinishDate);
         }
      }
   }

   /**
    * Set the value of one or more fields based on the contents of a database row.
    *
    * @param map column to field map
    * @param row database row
    * @param container field container
    */
   private void setFields(Map<String, FieldType> map, MapRow row, FieldContainer container)
   {
      if (row != null)
      {
         for (Map.Entry<String, FieldType> entry : map.entrySet())
         {
            container.set(entry.getValue(), row.getObject(entry.getKey()));
         }
      }
   }

   /**
    * Configure the mapping between a database column and a field.
    *
    * @param container column to field map
    * @param name column name
    * @param type field type
    */
   private static void defineField(Map<String, FieldType> container, String name, FieldType type)
   {
      container.put(name, type);
   }

   private String m_projectName;
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private Map<String, Table> m_tables;
   private SureTrakWbsFormat m_wbsFormat;
   private Map<Integer, List<MapRow>> m_definitions;
   private Map<Integer, ProjectCalendar> m_calendarMap;
   private Map<String, Resource> m_resourceMap;
   private Map<String, Task> m_wbsMap;
   private Map<String, Task> m_activityMap;

   private static final Integer WBS_FORMAT_ID = Integer.valueOf(0x79);
   private static final Integer WBS_ENTRIES_ID = Integer.valueOf(0x7A);

   private static final Map<String, FieldType> RESOURCE_FIELDS = new LinkedHashMap<>();
   private static final Map<String, FieldType> TASK_FIELDS = new LinkedHashMap<>();

   static
   {
      defineField(RESOURCE_FIELDS, "NAME", ResourceField.NAME);
      defineField(RESOURCE_FIELDS, "CODE", ResourceField.CODE);

      defineField(TASK_FIELDS, "NAME", TaskField.NAME);
      defineField(TASK_FIELDS, "ACTIVITY_ID", TaskField.ACTIVITY_ID);
      defineField(TASK_FIELDS, "DEPARTMENT", TaskField.DEPARTMENT);
      defineField(TASK_FIELDS, "MANAGER", TaskField.MANAGER);
      defineField(TASK_FIELDS, "SECTION", TaskField.SECTION);
      defineField(TASK_FIELDS, "MAIL", TaskField.MAIL);

      defineField(TASK_FIELDS, "PERCENT_COMPLETE", TaskField.PERCENT_COMPLETE);
      defineField(TASK_FIELDS, "EARLY_START", TaskField.EARLY_START);
      defineField(TASK_FIELDS, "LATE_START", TaskField.LATE_START);
      defineField(TASK_FIELDS, "EARLY_FINISH", TaskField.EARLY_FINISH);
      defineField(TASK_FIELDS, "LATE_FINISH", TaskField.LATE_FINISH);
      defineField(TASK_FIELDS, "ACTUAL_START", TaskField.ACTUAL_START);
      defineField(TASK_FIELDS, "ACTUAL_FINISH", TaskField.ACTUAL_FINISH);
      defineField(TASK_FIELDS, "ORIGINAL_DURATION", TaskField.DURATION);
      defineField(TASK_FIELDS, "REMAINING_DURATION", TaskField.REMAINING_DURATION);
   }
}
