
package net.sf.mpxj.primavera.p3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.ProjectReader;

/**
 * This class creates a new ProjectFile instance by reading a TurboProject PEP file.
 */
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
         config.setAutoTaskID(true);
         config.setAutoResourceUniqueID(true);
         config.setAutoTaskUniqueID(true);

         //         config.setAutoCalendarUniqueID(false);
         //         config.setAutoOutlineLevel(true);
         //         config.setAutoOutlineNumber(true);
         //         config.setAutoWBS(true);

         m_projectFile.getProjectProperties().setFileApplication("P3");
         m_projectFile.getProjectProperties().setFileType("BTRIEVE");

         m_eventManager.addProjectListeners(m_projectListeners);

         m_tables = new DatabaseReader().process(directory, "APEX");

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

   private void readCalendars()
   {
   }

   private void readResources()
   {
   }

   private void readTasks()
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
      //      container.put(name, type);
      //      if (alias != null)
      //      {
      //         ALIASES.put(type, alias);
      //      }
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private Map<String, Table> m_tables;

   private static final Table EMPTY_TABLE = new Table();

   static
   {
      //      defineField(RESOURCE_FIELDS, "ID", ResourceField.ID);
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
