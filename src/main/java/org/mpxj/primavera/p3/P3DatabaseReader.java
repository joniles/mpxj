/*
 * file:       P3DatabaseReader.java
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

package org.mpxj.primavera.p3;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.ChildTaskContainer;
import org.mpxj.ConstraintType;
import org.mpxj.EventManager;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.common.AlphanumComparator;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.SlackHelper;
import org.mpxj.primavera.common.MapRow;
import org.mpxj.primavera.common.Table;
import org.mpxj.reader.AbstractProjectFileReader;

/**
 * Reads schedule data from a P3 multi-file Btrieve database in a directory.
 */
public final class P3DatabaseReader extends AbstractProjectFileReader
{
   /**
    * Convenience method which locates the first P3 database in a directory
    * and opens it.
    *
    * @param directory directory containing a P3 database
    * @return ProjectFile instance
    */
   public static final ProjectFile setProjectNameAndRead(File directory) throws MPXJException
   {
      List<String> projects = listProjectNames(directory);
      if (projects.isEmpty())
      {
         return null;
      }

      P3DatabaseReader reader = new P3DatabaseReader();
      reader.setProjectName(projects.get(0));
      return reader.read(directory);
   }

   /**
    * Retrieve a list of the available P3 project names from a directory.
    *
    * @param directory name of the directory containing P3 files
    * @return list of project names
    */
   public static final List<String> listProjectNames(String directory)
   {
      return listProjectNames(new File(directory));
   }

   /**
    * Retrieve a list of the available P3 project names from a directory.
    *
    * @param directory directory containing P3 files
    * @return list of project names
    */
   public static final List<String> listProjectNames(File directory)
   {
      List<String> result = new ArrayList<>();

      File[] files = directory.listFiles((dir, name) -> name.toUpperCase().endsWith("STR.P3"));

      if (files != null)
      {
         for (File file : files)
         {
            String fileName = file.getName();
            String prefix = fileName.substring(0, fileName.length() - 6);
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

         m_projectFile.getProjectProperties().setFileApplication("P3");
         m_projectFile.getProjectProperties().setFileType("BTRIEVE");

         addListenersToProject(m_projectFile);

         m_tables = new DatabaseReader().process(directory, m_projectName);
         m_resourceMap = new HashMap<>();
         m_wbsMap = new HashMap<>();
         m_activityMap = new HashMap<>();

         readProjectHeader();
         readCalendars();
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
         m_resourceMap = null;
         m_wbsFormat = null;
         m_wbsMap = null;
         m_activityMap = null;
      }
   }

   @Override public List<ProjectFile> readAll(File directory) throws MPXJException
   {
      List<ProjectFile> projects = new ArrayList<>();

      for (String name : listProjectNames(directory))
      {
         P3DatabaseReader reader = new P3DatabaseReader();
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
      Table table = m_tables.get("DIR");
      MapRow row = table.find("");
      if (row != null)
      {
         setFields(PROJECT_FIELDS, row, m_projectFile.getProjectProperties());
         m_wbsFormat = new P3WbsFormat(row);
      }
   }

   /**
    * Read project calendars.
    */
   private void readCalendars()
   {
      // TODO: understand the calendar data representation.
      ProjectCalendar defaultCalendar = m_projectFile.addDefaultBaseCalendar();
      m_projectFile.getProjectProperties().setDefaultCalendar(defaultCalendar);
   }

   /**
    * Read resources.
    */
   private void readResources()
   {
      for (MapRow row : m_tables.get("RLB"))
      {
         Resource resource = m_projectFile.addResource();
         setFields(RESOURCE_FIELDS, row, resource);
         m_resourceMap.put(resource.getCode(), resource);
      }
   }

   /**
    * Read tasks.
    */
   private void readTasks()
   {
      readWBS();
      readActivities();
      updateDates();
   }

   /**
    * Read tasks representing the WBS.
    */
   private void readWBS()
   {
      Map<Integer, List<MapRow>> levelMap = new HashMap<>();
      for (MapRow row : m_tables.get("STR"))
      {
         Integer level = row.getInteger("LEVEL_NUMBER");
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
            m_wbsFormat.parseRawValue(row.getString("CODE_VALUE"));
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
            if (wbs != null && !wbs.isEmpty())
            {
               ChildTaskContainer parent = m_wbsMap.get(row.getString("PARENT_WBS"));
               if (parent == null)
               {
                  parent = m_projectFile;
               }

               Task task = parent.addTask();
               String name = row.getString("CODE_TITLE");
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
    * Read tasks representing activities.
    */
   private void readActivities()
   {
      Map<String, ChildTaskContainer> parentMap = new HashMap<>();
      for (MapRow row : m_tables.get("WBS"))
      {
         String activityID = row.getString("ACTIVITY_ID");
         m_wbsFormat.parseRawValue(row.getString("CODE_VALUE"));
         String parentWBS = m_wbsFormat.getFormattedValue();

         ChildTaskContainer parent = m_wbsMap.get(parentWBS);
         if (parent != null)
         {
            parentMap.put(activityID, parent);
         }
      }

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
         ChildTaskContainer parent = parentMap.get(activityID);
         if (parent == null)
         {
            parent = m_projectFile;
         }
         Task task = parent.addTask();
         setFields(TASK_FIELDS, row, task);
         task.setStart(task.getEarlyStart());
         task.setFinish(task.getEarlyFinish());
         task.setMilestone(task.getDuration().getDuration() == 0);
         if (parent instanceof Task)
         {
            task.setWBS(((Task) parent).getWBS());
         }

         int percentComplete = task.getPercentageComplete().intValue();
         if (percentComplete < 0)
         {
            task.setPercentageComplete(Integer.valueOf(0));
         }
         else
         {
            if (percentComplete > 100)
            {
               task.setPercentageComplete(Integer.valueOf(100));
            }
         }

         int flag = row.getInteger("ACTUAL_START_OR_CONSTRAINT_FLAG").intValue();
         if (flag != 0)
         {
            LocalDateTime date = row.getDate("AS_OR_ED_CONSTRAINT");
            switch (flag)
            {
               case 1:
               {
                  task.setConstraintType(ConstraintType.START_NO_EARLIER_THAN);
                  task.setConstraintDate(date);
                  break;
               }

               case 3:
               {
                  task.setConstraintType(ConstraintType.FINISH_NO_EARLIER_THAN);
                  task.setConstraintDate(date);
                  break;
               }

               case 99:
               {
                  task.setActualStart(date);
                  break;
               }
            }
         }

         flag = row.getInteger("ACTUAL_FINISH_OR_CONSTRAINT_FLAG").intValue();
         if (flag != 0)
         {
            LocalDateTime date = row.getDate("AF_OR_LD_CONSTRAINT");
            switch (flag)
            {
               case 2:
               {
                  task.setConstraintType(ConstraintType.START_NO_LATER_THAN);
                  task.setConstraintDate(date);
                  break;
               }

               case 4:
               {
                  task.setConstraintType(ConstraintType.FINISH_NO_LATER_THAN);
                  task.setConstraintDate(date);
                  break;
               }

               case 99:
               {
                  task.setActualFinish(date);
                  break;
               }
            }
         }

         //
         // The schedule only includes total slack. We'll assume this value is correct and backfill start and finish slack values.
         //
         SlackHelper.inferSlack(task);

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
               .type(row.getRelationType("LAG_TYPE"))
               .lag(row.getDuration("LAG_VALUE")));

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
   private P3WbsFormat m_wbsFormat;
   private Map<String, Resource> m_resourceMap;
   private Map<String, Task> m_wbsMap;
   private Map<String, Task> m_activityMap;

   private static final Map<String, FieldType> PROJECT_FIELDS = new LinkedHashMap<>();
   private static final Map<String, FieldType> RESOURCE_FIELDS = new LinkedHashMap<>();
   private static final Map<String, FieldType> TASK_FIELDS = new LinkedHashMap<>();

   static
   {
      defineField(PROJECT_FIELDS, "PROJECT_START_DATE", ProjectField.START_DATE);
      defineField(PROJECT_FIELDS, "PROJECT_FINISH_DATE", ProjectField.FINISH_DATE);
      defineField(PROJECT_FIELDS, "CURRENT_DATA_DATE", ProjectField.STATUS_DATE);
      defineField(PROJECT_FIELDS, "COMPANY_TITLE", ProjectField.COMPANY);
      defineField(PROJECT_FIELDS, "PROJECT_TITLE", ProjectField.NAME);

      defineField(RESOURCE_FIELDS, "RES_TITLE", ResourceField.NAME);
      defineField(RESOURCE_FIELDS, "RES_ID", ResourceField.CODE);

      defineField(TASK_FIELDS, "ACTIVITY_TITLE", TaskField.NAME);
      defineField(TASK_FIELDS, "ACTIVITY_ID", TaskField.ACTIVITY_ID);
      defineField(TASK_FIELDS, "ORIGINAL_DURATION", TaskField.DURATION);
      defineField(TASK_FIELDS, "REMAINING_DURATION", TaskField.REMAINING_DURATION);
      defineField(TASK_FIELDS, "PERCENT_COMPLETE", TaskField.PERCENT_COMPLETE);
      defineField(TASK_FIELDS, "EARLY_START", TaskField.EARLY_START);
      defineField(TASK_FIELDS, "LATE_START", TaskField.LATE_START);
      defineField(TASK_FIELDS, "EARLY_FINISH", TaskField.EARLY_FINISH);
      defineField(TASK_FIELDS, "LATE_FINISH", TaskField.LATE_FINISH);
      defineField(TASK_FIELDS, "FREE_FLOAT", TaskField.FREE_SLACK);
      defineField(TASK_FIELDS, "TOTAL_FLOAT", TaskField.TOTAL_SLACK);
   }
}
