/*
 * file:       TurboProjectReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       09/01/2018
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

package org.mpxj.turboproject;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.ChildTaskContainer;
import java.time.DayOfWeek;

import org.mpxj.EventManager;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.Notes;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarDays;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceField;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a TurboProject PEP file.
 */
public final class TurboProjectReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();
         m_tables = new HashMap<>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceID(false);
         config.setAutoCalendarUniqueID(false);
         config.setAutoResourceUniqueID(false);
         config.setAutoTaskID(false);
         config.setAutoTaskUniqueID(false);
         config.setAutoOutlineLevel(true);
         config.setAutoOutlineNumber(true);
         config.setAutoWBS(true);

         m_projectFile.getProjectProperties().setFileApplication("TurboProject");
         m_projectFile.getProjectProperties().setFileType("PEP");

         addListenersToProject(m_projectFile);

         readFile(stream);
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
      }
   }

   /**
    * Reads a PEP file from the input stream.
    *
    * @param is input stream representing a PEP file
    */
   private void readFile(InputStream is) throws IOException
   {
      InputStreamHelper.skip(is, 64);
      int index = 64;

      ArrayList<Integer> offsetList = new ArrayList<>();
      List<String> nameList = new ArrayList<>();

      while (true)
      {
         byte[] table = InputStreamHelper.read(is, 32);
         index += 32;

         int offset = PEPUtility.getInt(table, 0);
         offsetList.add(Integer.valueOf(offset));
         if (offset == 0)
         {
            break;
         }

         nameList.add(PEPUtility.getString(table, 5).toUpperCase());
      }

      InputStreamHelper.skip(is, offsetList.get(0).intValue() - index);

      for (int offsetIndex = 1; offsetIndex < offsetList.size() - 1; offsetIndex++)
      {
         String name = nameList.get(offsetIndex - 1);
         Class<? extends Table> tableClass = TABLE_CLASSES.getOrDefault(name, Table.class);

         Table table;
         try
         {
            table = tableClass.newInstance();
         }

         catch (Exception ex)
         {
            throw new RuntimeException(ex);
         }

         m_tables.put(name, table);
         table.read(is);
      }
   }

   /**
    * Read calendar data from a PEP file.
    */
   private void readCalendars()
   {
      //
      // Create the calendars
      //
      for (MapRow row : getTable("NCALTAB"))
      {
         ProjectCalendar calendar = m_projectFile.addCalendar();
         calendar.setUniqueID(row.getInteger("UNIQUE_ID"));
         calendar.setName(row.getString("NAME"));
         calendar.setWorkingDay(DayOfWeek.SUNDAY, row.getBoolean("SUNDAY"));
         calendar.setWorkingDay(DayOfWeek.MONDAY, row.getBoolean("MONDAY"));
         calendar.setWorkingDay(DayOfWeek.TUESDAY, row.getBoolean("TUESDAY"));
         calendar.setWorkingDay(DayOfWeek.WEDNESDAY, row.getBoolean("WEDNESDAY"));
         calendar.setWorkingDay(DayOfWeek.THURSDAY, row.getBoolean("THURSDAY"));
         calendar.setWorkingDay(DayOfWeek.FRIDAY, row.getBoolean("FRIDAY"));
         calendar.setWorkingDay(DayOfWeek.SATURDAY, row.getBoolean("SATURDAY"));

         for (DayOfWeek day : DayOfWeek.values())
         {
            ProjectCalendarHours hours = calendar.addCalendarHours(day);
            if (calendar.isWorkingDay(day))
            {
               hours.add(ProjectCalendarDays.DEFAULT_WORKING_MORNING);
               hours.add(ProjectCalendarDays.DEFAULT_WORKING_AFTERNOON);
            }
         }
      }

      //
      // Set up the hierarchy and add exceptions
      //
      Table exceptionsTable = getTable("CALXTAB");
      for (MapRow row : getTable("NCALTAB"))
      {
         ProjectCalendar child = m_projectFile.getCalendarByUniqueID(row.getInteger("UNIQUE_ID"));
         ProjectCalendar parent = m_projectFile.getCalendarByUniqueID(row.getInteger("BASE_CALENDAR_ID"));
         if (child != null && parent != null)
         {
            child.setParent(parent);
         }

         addCalendarExceptions(exceptionsTable, child, row.getInteger("FIRST_CALENDAR_EXCEPTION_ID"));

         m_eventManager.fireCalendarReadEvent(child);
      }

      m_projectFile.setDefaultCalendar(m_projectFile.getCalendars().findOrCreateDefaultCalendar());
   }

   /**
    * Read exceptions for a calendar.
    *
    * @param table calendar exception data
    * @param calendar calendar
    * @param exceptionID first exception ID
    */
   private void addCalendarExceptions(Table table, ProjectCalendar calendar, Integer exceptionID)
   {
      Integer currentExceptionID = exceptionID;
      while (true)
      {
         MapRow row = table.find(currentExceptionID);
         if (row == null)
         {
            break;
         }

         LocalDate date = LocalDateHelper.getLocalDate(row.getDate("DATE"));
         ProjectCalendarException exception = calendar.addCalendarException(date);
         if (row.getBoolean("WORKING"))
         {
            exception.add(ProjectCalendarDays.DEFAULT_WORKING_MORNING);
            exception.add(ProjectCalendarDays.DEFAULT_WORKING_AFTERNOON);
         }

         currentExceptionID = row.getInteger("NEXT_CALENDAR_EXCEPTION_ID");
      }
   }

   /**
    * Read resource data from a PEP file.
    */
   private void readResources()
   {
      for (MapRow row : getTable("RTAB"))
      {
         Resource resource = m_projectFile.addResource();
         setFields(RESOURCE_FIELDS, row, resource);
         resource.setNotesObject(new Notes(resource.getNotes()));
         ProjectCalendar calendar = m_projectFile.getCalendars().getByUniqueID(row.getInteger("CALENDAR_ID"));
         if (calendar != null)
         {
            calendar.setName(resource.getName());
         }
         resource.setCalendar(calendar);

         m_eventManager.fireResourceReadEvent(resource);
      }
   }

   /**
    * Read task data from a PEP file.
    */
   private void readTasks()
   {
      Integer rootID = Integer.valueOf(1);
      readWBS(m_projectFile, rootID);
      readTasks(rootID);
      m_projectFile.getTasks().synchronizeTaskIDToHierarchy();
   }

   /**
    * Recursively read the WBS structure from a PEP file.
    *
    * @param parent parent container for tasks
    * @param id initial WBS ID
    */
   private void readWBS(ChildTaskContainer parent, Integer id)
   {
      Integer currentID = id;
      Table table = getTable("WBSTAB");

      while (currentID.intValue() != 0)
      {
         MapRow row = table.find(currentID);
         Integer taskID = row.getInteger("TASK_ID");
         Task task = readTask(parent, taskID);
         Integer childID = row.getInteger("CHILD_ID");
         if (childID.intValue() != 0)
         {
            readWBS(task, childID);
         }
         currentID = row.getInteger("NEXT_ID");
      }
   }

   /**
    * Read leaf tasks attached to the WBS.
    *
    * @param id initial WBS ID
    */
   private void readTasks(Integer id)
   {
      Integer currentID = id;
      Table table = getTable("WBSTAB");

      while (currentID.intValue() != 0)
      {
         MapRow row = table.find(currentID);
         Task task = m_projectFile.getTaskByUniqueID(row.getInteger("TASK_ID"));
         readLeafTasks(task, row.getInteger("FIRST_CHILD_TASK_ID"));
         Integer childID = row.getInteger("CHILD_ID");
         if (childID.intValue() != 0)
         {
            readTasks(childID);
         }
         currentID = row.getInteger("NEXT_ID");
      }
   }

   /**
    * Read the leaf tasks for an individual WBS node.
    *
    * @param parent parent task
    * @param id first task ID
    */
   private void readLeafTasks(Task parent, Integer id)
   {
      Integer currentID = id;
      Table table = getTable("A1TAB");
      while (currentID.intValue() != 0)
      {
         if (m_projectFile.getTaskByUniqueID(currentID) == null)
         {
            readTask(parent, currentID);
         }
         currentID = table.find(currentID).getInteger("NEXT_TASK_ID");
      }
   }

   /**
    * Read data for an individual task from the tables in a PEP file.
    *
    * @param parent parent task
    * @param id task ID
    * @return task instance
    */
   private Task readTask(ChildTaskContainer parent, Integer id)
   {
      Table a0 = getTable("A0TAB");
      Table a1 = getTable("A1TAB");
      Table a2 = getTable("A2TAB");
      Table a3 = getTable("A3TAB");
      Table a4 = getTable("A4TAB");

      Task task = parent.addTask();
      MapRow a1Row = a1.find(id);
      MapRow a2Row = a2.find(id);

      setFields(A0TAB_FIELDS, a0.find(id), task);
      setFields(A1TAB_FIELDS, a1Row, task);
      setFields(A2TAB_FIELDS, a2Row, task);
      setFields(A3TAB_FIELDS, a3.find(id), task);
      setFields(A5TAB_FIELDS, a4.find(id), task);

      task.setStart(task.getEarlyStart());
      task.setFinish(task.getEarlyFinish());
      if (task.getName() == null)
      {
         task.setName(task.getNotes());
         task.setNotes(null);
      }
      else
      {
         task.setNotesObject(new Notes(task.getNotes()));
      }

      m_eventManager.fireTaskReadEvent(task);

      return task;
   }

   /**
    * Read relationship data from a PEP file.
    */
   private void readRelationships()
   {
      for (MapRow row : getTable("CONTAB"))
      {
         Task task1 = m_projectFile.getTaskByUniqueID(row.getInteger("TASK_ID_1"));
         Task task2 = m_projectFile.getTaskByUniqueID(row.getInteger("TASK_ID_2"));

         if (task1 != null && task2 != null)
         {
            Relation relation = task2.addPredecessor(new Relation.Builder()
               .predecessorTask(task1)
               .type(row.getRelationType("TYPE"))
               .lag(row.getDuration("LAG")));

            m_eventManager.fireRelationReadEvent(relation);
         }
      }
   }

   /**
    * Read resource assignment data from a PEP file.
    */
   private void readResourceAssignments()
   {
      for (MapRow row : getTable("USGTAB"))
      {
         Task task = m_projectFile.getTaskByUniqueID(row.getInteger("TASK_ID"));
         Resource resource = m_projectFile.getResourceByUniqueID(row.getInteger("RESOURCE_ID"));
         if (task != null && resource != null)
         {
            ResourceAssignment assignment = task.addResourceAssignment(resource);
            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }
   }

   /**
    * Retrieve a table by name.
    *
    * @param name table name
    * @return Table instance
    */
   private Table getTable(String name)
   {
      return m_tables.getOrDefault(name, EMPTY_TABLE);
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

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private HashMap<String, Table> m_tables;

   private static final Table EMPTY_TABLE = new Table();

   private static final Map<String, Class<? extends Table>> TABLE_CLASSES = new HashMap<>();
   static
   {
      TABLE_CLASSES.put("RTAB", TableRTAB.class);
      TABLE_CLASSES.put("A0TAB", TableA0TAB.class);
      TABLE_CLASSES.put("A1TAB", TableA1TAB.class);
      TABLE_CLASSES.put("A2TAB", TableA2TAB.class);
      TABLE_CLASSES.put("A3TAB", TableA3TAB.class);
      TABLE_CLASSES.put("A5TAB", TableA5TAB.class);
      TABLE_CLASSES.put("CONTAB", TableCONTAB.class);
      TABLE_CLASSES.put("USGTAB", TableUSGTAB.class);
      TABLE_CLASSES.put("NCALTAB", TableNCALTAB.class);
      TABLE_CLASSES.put("CALXTAB", TableCALXTAB.class);
      TABLE_CLASSES.put("WBSTAB", TableWBSTAB.class);
   }

   private static final Map<String, FieldType> RESOURCE_FIELDS = new HashMap<>();
   private static final Map<String, FieldType> A0TAB_FIELDS = new HashMap<>();
   private static final Map<String, FieldType> A1TAB_FIELDS = new HashMap<>();
   private static final Map<String, FieldType> A2TAB_FIELDS = new HashMap<>();
   private static final Map<String, FieldType> A3TAB_FIELDS = new HashMap<>();
   private static final Map<String, FieldType> A5TAB_FIELDS = new HashMap<>();

   static
   {
      defineField(RESOURCE_FIELDS, "ID", ResourceField.ID);
      defineField(RESOURCE_FIELDS, "UNIQUE_ID", ResourceField.UNIQUE_ID);
      defineField(RESOURCE_FIELDS, "NAME", ResourceField.NAME);
      defineField(RESOURCE_FIELDS, "GROUP", ResourceField.GROUP);
      defineField(RESOURCE_FIELDS, "DESCRIPTION", ResourceField.NOTES);
      defineField(RESOURCE_FIELDS, "PARENT_ID", ResourceField.PARENT_ID);

      defineField(RESOURCE_FIELDS, "RATE", ResourceField.RATE);
      defineField(RESOURCE_FIELDS, "POOL", ResourceField.POOL);
      defineField(RESOURCE_FIELDS, "PER_DAY", ResourceField.PER_DAY);
      defineField(RESOURCE_FIELDS, "PRIORITY", ResourceField.PRIORITY);
      defineField(RESOURCE_FIELDS, "PERIOD_DUR", ResourceField.PERIOD_DUR);
      defineField(RESOURCE_FIELDS, "EXPENSES_ONLY", ResourceField.EXPENSES_ONLY);
      defineField(RESOURCE_FIELDS, "MODIFY_ON_INTEGRATE", ResourceField.MODIFY_ON_INTEGRATE);
      defineField(RESOURCE_FIELDS, "UNIT", ResourceField.UNIT);

      defineField(A0TAB_FIELDS, "UNIQUE_ID", TaskField.UNIQUE_ID);

      defineField(A1TAB_FIELDS, "ORDER", TaskField.ID);
      defineField(A1TAB_FIELDS, "PLANNED_START", TaskField.BASELINE_START);
      defineField(A1TAB_FIELDS, "PLANNED_FINISH", TaskField.BASELINE_FINISH);

      defineField(A2TAB_FIELDS, "DESCRIPTION", TaskField.NOTES);

      defineField(A3TAB_FIELDS, "EARLY_START", TaskField.EARLY_START);
      defineField(A3TAB_FIELDS, "LATE_START", TaskField.LATE_START);
      defineField(A3TAB_FIELDS, "EARLY_FINISH", TaskField.EARLY_FINISH);
      defineField(A3TAB_FIELDS, "LATE_FINISH", TaskField.LATE_FINISH);

      defineField(A5TAB_FIELDS, "ORIGINAL_DURATION", TaskField.DURATION);
      defineField(A5TAB_FIELDS, "REMAINING_DURATION", TaskField.REMAINING_DURATION);
      defineField(A5TAB_FIELDS, "PERCENT_COMPLETE", TaskField.PERCENT_COMPLETE);
      defineField(A5TAB_FIELDS, "TARGET_START", TaskField.PLANNED_START);
      defineField(A5TAB_FIELDS, "TARGET_FINISH", TaskField.PLANNED_FINISH);
      defineField(A5TAB_FIELDS, "ACTUAL_START", TaskField.ACTUAL_START);
      defineField(A5TAB_FIELDS, "ACTUAL_FINISH", TaskField.ACTUAL_FINISH);
   }
}
