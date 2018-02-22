
package net.sf.mpxj.primavera.p3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectField;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.Task;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.ProjectReader;

public final class P3Reader implements ProjectReader
{
   /**
    * {@inheritDoc}
    */
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

   public void setPrefix(String prefix)
   {
      m_prefix = prefix;
   }

   /**
    * {@inheritDoc}
    */
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

         //         config.setAutoCalendarUniqueID(false);
         //         config.setAutoOutlineLevel(true);
         //         config.setAutoOutlineNumber(true);
         config.setAutoWBS(false);

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

   private void readCalendars()
   {
   }

   private void readResources()
   {
      for (MapRow row : m_tables.get("RLB"))
      {
         Resource resource = m_projectFile.addResource();
         setFields(RESOURCE_FIELDS, row, resource);
      }
   }

   private void readTasks()
   {
      readWBS();
      readActivities();
   }

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
      Map<String, Task> wbsMap = new HashMap<String, Task>();

      while (true)
      {
         List<MapRow> items = levelMap.get(Integer.valueOf(level++));
         if (items == null)
         {
            break;
         }

         // TODO - add columns and sort???

         for (MapRow row : items)
         {
            m_wbsFormat.parseRawValue(row.getString("CODE_VALUE"));
            String parentWbsValue = m_wbsFormat.getFormattedParentValue();
            String wbsValue = m_wbsFormat.getFormatedValue();

            ChildTaskContainer parent = wbsMap.get(parentWbsValue);
            if (parent == null)
            {
               parent = m_projectFile;
            }

            Task task = parent.addTask();
            task.setName(row.getString("CODE_TITLE"));
            task.setWBS(wbsValue);

            wbsMap.put(wbsValue, task);
         }
      }
   }

   private void readActivities()
   {

   }

   private void readRelationships()
   {
   }

   private void readResourceAssignments()
   {
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

   private static final Map<String, FieldType> PROJECT_FIELDS = new HashMap<String, FieldType>();
   private static final Map<String, FieldType> RESOURCE_FIELDS = new HashMap<String, FieldType>();

   static
   {
      defineField(PROJECT_FIELDS, "PROJECT_START_DATE", ProjectField.START_DATE);
      defineField(PROJECT_FIELDS, "PROJECT_FINISH_DATE", ProjectField.FINISH_DATE);
      defineField(PROJECT_FIELDS, "CURRENT_DATA_DATE", ProjectField.STATUS_DATE);
      defineField(PROJECT_FIELDS, "COMPANY_TITLE", ProjectField.COMPANY);
      defineField(PROJECT_FIELDS, "PROJECT_TITLE", ProjectField.NAME);

      defineField(RESOURCE_FIELDS, "RES_TITLE", ResourceField.NAME);
      defineField(RESOURCE_FIELDS, "RES_ID", ResourceField.CODE);

      //      defineField(RESOURCE_FIELDS, "UNIQUE_ID", ResourceField.UNIQUE_ID);
      //      defineField(RESOURCE_FIELDS, "NAME", ResourceField.NAME);
      //      defineField(RESOURCE_FIELDS, "GROUP", ResourceField.GROUP);
      //      defineField(RESOURCE_FIELDS, "DESCRIPTION", ResourceField.NOTES);
      //      defineField(RESOURCE_FIELDS, "PARENT_ID", ResourceField.PARENT_ID);
      //
      //      defineField(RESOURCE_FIELDS, "RATE", ResourceField.NUMBER1, "Rate");
      //      defineField(RESOURCE_FIELDS, "POOL", ResourceField.NUMBER2, "Pool");
      //      defineField(RESOURCE_FIELDS, "PER_DAY", ResourceField.NUMBER3, "Per Day");
      //      defineField(RESOURCE_FIELDS, "PRIORITY", ResourceField.NUMBER4, "Priority");
      //      defineField(RESOURCE_FIELDS, "PERIOD_DUR", ResourceField.NUMBER5, "Period Dur");
      //      defineField(RESOURCE_FIELDS, "EXPENSES_ONLY", ResourceField.FLAG1, "Expenses Only");
      //      defineField(RESOURCE_FIELDS, "MODIFY_ON_INTEGRATE", ResourceField.FLAG2, "Modify On Integrate");
      //      defineField(RESOURCE_FIELDS, "UNIT", ResourceField.TEXT1, "Unit");
      //
      //      defineField(A0TAB_FIELDS, "UNIQUE_ID", TaskField.UNIQUE_ID);
      //
      //      defineField(A1TAB_FIELDS, "ORDER", TaskField.ID);
      //      defineField(A1TAB_FIELDS, "PLANNED_START", TaskField.BASELINE_START);
      //      defineField(A1TAB_FIELDS, "PLANNED_FINISH", TaskField.BASELINE_FINISH);
      //
      //      defineField(A2TAB_FIELDS, "DESCRIPTION", TaskField.TEXT1, "Description");
      //
      //      defineField(A3TAB_FIELDS, "EARLY_START", TaskField.EARLY_START);
      //      defineField(A3TAB_FIELDS, "LATE_START", TaskField.LATE_START);
      //      defineField(A3TAB_FIELDS, "EARLY_FINISH", TaskField.EARLY_FINISH);
      //      defineField(A3TAB_FIELDS, "LATE_FINISH", TaskField.LATE_FINISH);
      //
      //      defineField(A5TAB_FIELDS, "ORIGINAL_DURATION", TaskField.DURATION);
      //      defineField(A5TAB_FIELDS, "REMAINING_DURATION", TaskField.REMAINING_DURATION);
      //      defineField(A5TAB_FIELDS, "PERCENT_COMPLETE", TaskField.PERCENT_COMPLETE);
      //      defineField(A5TAB_FIELDS, "TARGET_START", TaskField.DATE1, "Target Start");
      //      defineField(A5TAB_FIELDS, "TARGET_FINISH", TaskField.DATE2, "Target Finish");
      //      defineField(A5TAB_FIELDS, "ACTUAL_START", TaskField.ACTUAL_START);
      //      defineField(A5TAB_FIELDS, "ACTUAL_FINISH", TaskField.ACTUAL_FINISH);
   }

}
