/*
 * file:       P3Reader.java
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ChildTaskContainer;
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
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.ProjectReader;

/**
 * Reads a schedule data from a P3 multi-file Btrieve database in a directory.
 */
public final class P3Reader implements ProjectReader
{
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
    */
   public void setPrefix(String prefix)
   {
      m_prefix = prefix;
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

         m_tables = new DatabaseReader().process(directory, m_prefix);

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
         m_wbsFormat = new WbsFormat(row);
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
      m_resourceMap = new HashMap<String, Resource>();
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
      m_wbsMap = new HashMap<String, Task>();

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
            String wbsValue = m_wbsFormat.getFormatedValue();
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
    * Read tasks representin activities.
    */
   private void readActivities()
   {
      Map<String, ChildTaskContainer> parentMap = new HashMap<String, ChildTaskContainer>();
      for (MapRow row : m_tables.get("WBS"))
      {
         String activityID = row.getString("ACTIVITY_ID");
         m_wbsFormat.parseRawValue(row.getString("CODE_VALUE"));
         String parentWBS = m_wbsFormat.getFormatedValue();

         ChildTaskContainer parent = m_wbsMap.get(parentWBS);
         if (parent != null)
         {
            parentMap.put(activityID, parent);
         }
      }

      m_activityMap = new HashMap<String, Task>();
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
            continue;
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

   private String m_prefix;
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private Map<String, Table> m_tables;
   private WbsFormat m_wbsFormat;
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
