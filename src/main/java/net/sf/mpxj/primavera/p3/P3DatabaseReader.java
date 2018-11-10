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

package net.sf.mpxj.primavera.p3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.common.AlphanumComparator;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.primavera.common.MapRow;
import net.sf.mpxj.primavera.common.Table;
import net.sf.mpxj.reader.ProjectReader;

/**
 * Reads schedule data from a P3 multi-file Btrieve database in a directory.
 */
public final class P3DatabaseReader implements ProjectReader
{
   /**
    * Convenience method which locates the first P3 database in a directory
    * and opens it.
    *
    * @param directory directory containing a P3 database
    * @return ProjectFile instance
    *
    * @deprecated Use setProjectAndRead
    */
   @Deprecated public static final ProjectFile setPrefixAndRead(File directory) throws MPXJException
   {
      return setProjectNameAndRead(directory);
   }

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

      if (!projects.isEmpty())
      {
         P3DatabaseReader reader = new P3DatabaseReader();
         reader.setProjectName(projects.get(0));
         return reader.read(directory);
      }

      return null;
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
      List<String> result = new ArrayList<String>();

      File[] files = directory.listFiles(new FilenameFilter()
      {
         @Override public boolean accept(File dir, String name)
         {
            return name.toUpperCase().endsWith("STR.P3");
         }
      });

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
         m_projectFile.getCustomFields().getCustomField(TaskField.TEXT1).setAlias("Code");

         m_projectFile.getProjectProperties().setFileApplication("P3");
         m_projectFile.getProjectProperties().setFileType("BTRIEVE");

         m_eventManager.addProjectListeners(m_projectListeners);

         m_tables = new DatabaseReader().process(directory, m_projectName);
         m_resourceMap = new HashMap<String, Resource>();
         m_wbsMap = new HashMap<String, Task>();
         m_activityMap = new HashMap<String, Task>();

         readProjectHeader();
         readCalendars();
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
         m_resourceMap = null;
         m_wbsFormat = null;
         m_wbsMap = null;
         m_activityMap = null;
      }
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
      Map<Integer, List<MapRow>> levelMap = new HashMap<Integer, List<MapRow>>();
      for (MapRow row : m_tables.get("STR"))
      {
         Integer level = row.getInteger("LEVEL_NUMBER");
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
            m_wbsFormat.parseRawValue(row.getString("CODE_VALUE"));
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
               m_wbsMap.put(wbs, task);
            }
         }
      }
   }

   /**
    * Read tasks representing activities.
    */
   private void readActivities()
   {
      Map<String, ChildTaskContainer> parentMap = new HashMap<String, ChildTaskContainer>();
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

         int flag = row.getInteger("ACTUAL_START_OR_CONSTRAINT_FLAG").intValue();
         if (flag != 0)
         {
            Date date = row.getDate("AS_OR_ED_CONSTRAINT");
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
            Date date = row.getDate("AF_OR_LD_CONSTRAINT");
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
            Duration lag = row.getDuration("LAG_VALUE");
            RelationType type = row.getRelationType("LAG_TYPE");

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
   private P3WbsFormat m_wbsFormat;
   private Map<String, Resource> m_resourceMap;
   private Map<String, Task> m_wbsMap;
   private Map<String, Task> m_activityMap;

   private static final Map<String, FieldType> PROJECT_FIELDS = new HashMap<String, FieldType>();
   private static final Map<String, FieldType> RESOURCE_FIELDS = new HashMap<String, FieldType>();
   private static final Map<String, FieldType> TASK_FIELDS = new HashMap<String, FieldType>();

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
      defineField(TASK_FIELDS, "ACTIVITY_ID", TaskField.TEXT1);
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
