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

package net.sf.mpxj.primavera.suretrak;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringData;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.AlphanumComparator;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.primavera.common.MapRow;
import net.sf.mpxj.primavera.common.Table;
import net.sf.mpxj.reader.ProjectReader;

/**
 * Reads schedule data from a SureTrak multi-file database in a directory.
 */
public final class SureTrakDatabaseReader implements ProjectReader
{
   /**
    * Convenience method which locates the first SureTrak database in a directory
    * and opens it.
    *
    * @param directory directory containing a SureTrak database
    * @return ProjectFile instance
    *
    * @deprecated Use setProjectNameAndRead
    */
   @Deprecated public static final ProjectFile setPrefixAndRead(File directory) throws MPXJException
   {
      return setProjectNameAndRead(directory);
   }

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

      if (!projects.isEmpty())
      {
         SureTrakDatabaseReader reader = new SureTrakDatabaseReader();
         reader.setProjectName(projects.get(0));
         return reader.read(directory);
      }

      return null;
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
      List<String> result = new ArrayList<String>();

      File[] files = directory.listFiles(new FilenameFilter()
      {
         @Override public boolean accept(File dir, String name)
         {
            return name.toUpperCase().endsWith(".DIR");
         }
      });

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

   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      return read(new File(fileName));
   }

   @Override public ProjectFile read(InputStream inputStream)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Set the prefix used to identify which database is read from the directory.
    * There may potentially be more than one database in a directory.
    *
    * @param prefix file name prefix
    *
    * @deprecated Use setProjectName
    */
   @Deprecated public void setPrefix(String prefix)
   {
      m_projectName = prefix;
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

         // Activity ID
         CustomFieldContainer customFields = m_projectFile.getCustomFields();
         customFields.getCustomField(TaskField.TEXT1).setAlias("Code");
         customFields.getCustomField(TaskField.TEXT2).setAlias("Department");
         customFields.getCustomField(TaskField.TEXT3).setAlias("Manager");
         customFields.getCustomField(TaskField.TEXT4).setAlias("Section");
         customFields.getCustomField(TaskField.TEXT5).setAlias("Mail");

         m_projectFile.getProjectProperties().setFileApplication("SureTrak");
         m_projectFile.getProjectProperties().setFileType("STW");

         m_eventManager.addProjectListeners(m_projectListeners);

         m_tables = new DatabaseReader().process(directory, m_projectName);
         m_definitions = new HashMap<Integer, List<MapRow>>();
         m_calendarMap = new HashMap<Integer, ProjectCalendar>();
         m_resourceMap = new HashMap<String, Resource>();
         m_wbsMap = new HashMap<String, Task>();
         m_activityMap = new HashMap<String, Task>();

         readProjectHeader();
         readDefinitions();
         readCalendars();
         readHolidays();
         readResources();
         readTasks();
         readRelationships();
         readResourceAssignments();

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
         m_projectListeners = null;
         m_tables = null;
         m_definitions = null;
         m_wbsFormat = null;
         m_calendarMap = null;
         m_resourceMap = null;
         m_wbsMap = null;
         m_activityMap = null;
      }
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
         List<MapRow> list = m_definitions.get(id);
         if (list == null)
         {
            list = new ArrayList<MapRow>();
            m_definitions.put(id, list);
         }
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
         readHours(calendar, Day.SUNDAY, days[0]);
         readHours(calendar, Day.MONDAY, days[1]);
         readHours(calendar, Day.TUESDAY, days[2]);
         readHours(calendar, Day.WEDNESDAY, days[3]);
         readHours(calendar, Day.THURSDAY, days[4]);
         readHours(calendar, Day.FRIDAY, days[5]);
         readHours(calendar, Day.SATURDAY, days[6]);

         int workingDaysPerWeek = 0;
         for (Day day : Day.values())
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

            calendar.setMinutesPerDay(Integer.valueOf(minutesPerDay));
            calendar.setMinutesPerWeek(Integer.valueOf(minutesPerWeek));
            calendar.setMinutesPerMonth(Integer.valueOf(minutesPerMonth));
            calendar.setMinutesPerYear(Integer.valueOf(minutesPerYear));
         }
      }
   }

   /**
    * Reads the integer representation of calendar hours for a given
    * day and populates the calendar.
    *
    * @param calendar parent calendar
    * @param day target day
    * @param hours working hours
    */
   private void readHours(ProjectCalendar calendar, Day day, Integer hours)
   {
      int value = hours.intValue();
      int startHour = 0;
      ProjectCalendarHours calendarHours = null;

      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

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

         cal.set(Calendar.HOUR_OF_DAY, startHour);
         Date startDate = cal.getTime();
         cal.set(Calendar.HOUR_OF_DAY, endHour);
         Date endDate = cal.getTime();

         if (calendarHours == null)
         {
            calendarHours = calendar.addCalendarHours(day);
            calendar.setWorkingDay(day, true);
         }
         calendarHours.addRange(new DateRange(startDate, endDate));
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
            Date date = row.getDate("DATE");
            ProjectCalendarException exception = calendar.addCalendarException(date, date);
            if (row.getBoolean("ANNUAL"))
            {
               RecurringData recurring = new RecurringData();
               recurring.setRecurrenceType(RecurrenceType.YEARLY);
               recurring.setYearlyAbsoluteFromDate(date);
               recurring.setStartDate(date);
               exception.setRecurring(recurring);
               // TODO set end date based on project end date
            }
         }
      }
   }

   /**
    * Read resources.
    */
   private void readResources()
   {
      m_resourceMap = new HashMap<String, Resource>();
      for (MapRow row : m_tables.get("RLB"))
      {
         Resource resource = m_projectFile.addResource();
         setFields(RESOURCE_FIELDS, row, resource);
         ProjectCalendar calendar = m_calendarMap.get(row.getInteger("CALENDAR_ID"));
         if (calendar != null)
         {
            ProjectCalendar baseCalendar = m_calendarMap.get(row.getInteger("BASE_CALENDAR_ID"));
            calendar.setParent(baseCalendar);
            resource.setResourceCalendar(calendar);
         }
         m_resourceMap.put(resource.getCode(), resource);
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
      Map<Integer, List<MapRow>> levelMap = new HashMap<Integer, List<MapRow>>();
      List<MapRow> table = m_definitions.get(WBS_ENTRIES_ID);
      if (table != null)
      {
         for (MapRow row : table)
         {
            m_wbsFormat.parseRawValue(row.getString("TEXT1"));
            Integer level = m_wbsFormat.getLevel();
            List<MapRow> items = levelMap.get(level);
            if (items == null)
            {
               items = new ArrayList<MapRow>();
               levelMap.put(level, items);
            }
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
            Collections.sort(items, new Comparator<MapRow>()
            {
               @Override public int compare(MapRow o1, MapRow o2)
               {
                  return comparator.compare(o1.getString("WBS"), o2.getString("WBS"));
               }
            });

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
               m_wbsMap.put(wbs, task);
            }
         }
      }
   }

   /**
    * Read activities.
    */
   private void readActivities()
   {
      List<MapRow> items = new ArrayList<MapRow>();
      for (MapRow row : m_tables.get("ACT"))
      {
         items.add(row);
      }
      final AlphanumComparator comparator = new AlphanumComparator();
      Collections.sort(items, new Comparator<MapRow>()
      {
         @Override public int compare(MapRow o1, MapRow o2)
         {
            return comparator.compare(o1.getString("ACTIVITY_ID"), o2.getString("ACTIVITY_ID"));
         }
      });

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
            Duration lag = row.getDuration("LAG");
            RelationType type = row.getRelationType("TYPE");

            successor.addPredecessor(predecessor, type, lag);
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
            task.addResourceAssignment(resource);
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
      if (parentTask.getSummary())
      {
         int finished = 0;
         Date startDate = parentTask.getStart();
         Date finishDate = parentTask.getFinish();
         Date actualStartDate = parentTask.getActualStart();
         Date actualFinishDate = parentTask.getActualFinish();
         Date earlyStartDate = parentTask.getEarlyStart();
         Date earlyFinishDate = parentTask.getEarlyFinish();
         Date lateStartDate = parentTask.getLateStart();
         Date lateFinishDate = parentTask.getLateFinish();

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            startDate = DateHelper.min(startDate, task.getStart());
            finishDate = DateHelper.max(finishDate, task.getFinish());
            actualStartDate = DateHelper.min(actualStartDate, task.getActualStart());
            actualFinishDate = DateHelper.max(actualFinishDate, task.getActualFinish());
            earlyStartDate = DateHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = DateHelper.max(earlyFinishDate, task.getEarlyFinish());
            lateStartDate = DateHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = DateHelper.max(lateFinishDate, task.getLateFinish());

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
      defineField(container, name, type, null);
   }

   /**
    * Configure the mapping between a database column and a field, including definition of
    * an alias.
    *
    * @param container column to field map
    * @param name column name
    * @param type field type
    * @param alias field alias
    */
   private static void defineField(Map<String, FieldType> container, String name, FieldType type, String alias)
   {
      container.put(name, type);
      //      if (alias != null)
      //      {
      //         ALIASES.put(type, alias);
      //      }
   }

   private String m_projectName;
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private Map<String, Table> m_tables;
   private SureTrakWbsFormat m_wbsFormat;
   private Map<Integer, List<MapRow>> m_definitions;
   private Map<Integer, ProjectCalendar> m_calendarMap;
   private Map<String, Resource> m_resourceMap;
   private Map<String, Task> m_wbsMap;
   private Map<String, Task> m_activityMap;

   private static final Integer WBS_FORMAT_ID = Integer.valueOf(0x79);
   private static final Integer WBS_ENTRIES_ID = Integer.valueOf(0x7A);

   private static final Map<String, FieldType> RESOURCE_FIELDS = new HashMap<String, FieldType>();
   private static final Map<String, FieldType> TASK_FIELDS = new HashMap<String, FieldType>();

   static
   {
      defineField(RESOURCE_FIELDS, "NAME", ResourceField.NAME);
      defineField(RESOURCE_FIELDS, "CODE", ResourceField.CODE);

      defineField(TASK_FIELDS, "NAME", TaskField.NAME);
      defineField(TASK_FIELDS, "ACTIVITY_ID", TaskField.TEXT1);
      defineField(TASK_FIELDS, "DEPARTMENT", TaskField.TEXT2);
      defineField(TASK_FIELDS, "MANAGER", TaskField.TEXT3);
      defineField(TASK_FIELDS, "SECTION", TaskField.TEXT4);
      defineField(TASK_FIELDS, "MAIL", TaskField.TEXT5);

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
