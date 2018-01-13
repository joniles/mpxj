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

package net.sf.mpxj.turboproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * This class creates a new ProjectFile instance by reading a GanttProject file.
 */
public final class TurboProjectReader extends AbstractProjectReader
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

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();
         m_tables = new HashMap<String, Table>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceUniqueID(false);
         config.setAutoTaskUniqueID(false);
         config.setAutoOutlineLevel(true);
         config.setAutoOutlineNumber(true);
         config.setAutoWBS(true);

         m_projectFile.getProjectProperties().setFileApplication("TurboProject");
         m_projectFile.getProjectProperties().setFileType("PEP");

         m_eventManager.addProjectListeners(m_projectListeners);

         applyAliases();

         readFile(stream);
         //         readProjectProperties(ganttProject);
         //         readCalendars(ganttProject);
         readResources();
         readTasks();
         //         readRelationships(ganttProject);
         //         readResourceAssignments(ganttProject);

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

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

   private void readFile(InputStream is) throws IOException
   {
      is.skip(64);
      int index = 64;

      ArrayList<Integer> offsetList = new ArrayList<Integer>();
      List<String> nameList = new ArrayList<String>();

      while (true)
      {
         byte[] table = new byte[32];
         is.read(table);
         index += 32;

         int offset = PEPUtility.getInt(table, 0);
         offsetList.add(Integer.valueOf(offset));
         if (offset == 0)
         {
            break;
         }

         nameList.add(PEPUtility.getString(table, 5).toUpperCase());
      }

      is.skip(offsetList.get(0).intValue() - index);

      for (int offsetIndex = 1; offsetIndex < offsetList.size() - 1; offsetIndex++)
      {
         String name = nameList.get(offsetIndex - 1);
         Class<? extends Table> tableClass = TABLE_CLASSES.get(name);
         if (tableClass == null)
         {
            tableClass = Table.class;
         }

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
    * This method extracts project properties from a GanttProject file.
    *
    * @param tpProject TurboProject file
    */
   //   private void readProjectProperties(Project tpProject)
   //   {
   //   }

   /**
    * This method extracts calendar data from a GanttProject file.
    *
    * @param ganttProject Root node of the GanttProject file
    */
   //   private void readCalendars(Project ganttProject)
   //   {
   //   }

   private void readResources()
   {
      for (MapRow row : getTable("RTAB"))
      {
         Resource resource = m_projectFile.addResource();
         for (Map.Entry<String, FieldType> entry : RESOURCE_FIELDS.entrySet())
         {
            resource.set(entry.getValue(), row.getObject(entry.getKey()));
         }
      }

      // TODO: Correctly handle calendar
   }

   private void readTasks()
   {
      for (MapRow row : getTable("A0TAB"))
      {
         Integer uniqueID = row.getInteger("UNIQUE_ID");
         System.out.println("Task: " + uniqueID + " " + row.getBoolean("DELETED"));
      }
   }

   /**
    * Read all task relationships from a GanttProject.
    *
    * @param gpProject GanttProject project
    */
   //   private void readRelationships(Project gpProject)
   //   {
   //   }

   /**
    * Read all resource assignments from a GanttProject project.
    *
    * @param gpProject GanttProject project
    */
   //   private void readResourceAssignments(Project gpProject)
   //   {
   //   }

   private Table getTable(String name)
   {
      Table table = m_tables.get(name);
      if (table == null)
      {
         table = EMPTY_TABLE;
      }
      return table;
   }

   private void applyAliases()
   {
      CustomFieldContainer fields = m_projectFile.getCustomFields();
      for (Map.Entry<FieldType, String> entry : ALIASES.entrySet())
      {
         fields.getCustomField(entry.getKey()).setAlias(entry.getValue());
      }
   }

   private static void defineField(Map<String, FieldType> container, String name, FieldType type)
   {
      defineField(container, name, type, null);
   }

   private static void defineField(Map<String, FieldType> container, String name, FieldType type, String alias)
   {
      container.put(name, type);
      if (alias != null)
      {
         ALIASES.put(type, alias);
      }
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private HashMap<String, Table> m_tables;

   private static final Table EMPTY_TABLE = new Table();

   private static final Map<String, Class<? extends Table>> TABLE_CLASSES = new HashMap<String, Class<? extends Table>>();
   static
   {
      TABLE_CLASSES.put("RTAB", TableRTAB.class);
      TABLE_CLASSES.put("A0TAB", TableA0TAB.class);
   }

   private static final Map<FieldType, String> ALIASES = new HashMap<FieldType, String>();
   private static final Map<String, FieldType> RESOURCE_FIELDS = new HashMap<String, FieldType>();

   static
   {
      defineField(RESOURCE_FIELDS, "ID", ResourceField.ID);
      defineField(RESOURCE_FIELDS, "UNIQUE_ID", ResourceField.UNIQUE_ID);
      defineField(RESOURCE_FIELDS, "NAME", ResourceField.NAME);
      defineField(RESOURCE_FIELDS, "GROUP", ResourceField.GROUP);
      defineField(RESOURCE_FIELDS, "DESCRIPTION", ResourceField.NOTES);
      defineField(RESOURCE_FIELDS, "PARENT_ID", ResourceField.PARENT_ID);

      defineField(RESOURCE_FIELDS, "RATE", ResourceField.NUMBER1, "Rate");
      defineField(RESOURCE_FIELDS, "POOL", ResourceField.NUMBER2, "Pool");
      defineField(RESOURCE_FIELDS, "PER_DAY", ResourceField.NUMBER3, "Per Day");
      defineField(RESOURCE_FIELDS, "PRIORITY", ResourceField.NUMBER4, "Priority");
      defineField(RESOURCE_FIELDS, "PERIOD_DUR", ResourceField.NUMBER5, "Period Dur");
      defineField(RESOURCE_FIELDS, "EXPENSES_ONLY", ResourceField.FLAG1, "Expenses Only");
      defineField(RESOURCE_FIELDS, "MODIFY_ON_INTEGRATE", ResourceField.FLAG2, "Modify On Integrate");
      defineField(RESOURCE_FIELDS, "UNIT", ResourceField.TEXT1, "Unit");
   }

}
