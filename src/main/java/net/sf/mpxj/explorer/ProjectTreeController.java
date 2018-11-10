/*
 * file:       ProjectTreeController.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       06/07/2014
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

package net.sf.mpxj.explorer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.Column;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.Filter;
import net.sf.mpxj.Group;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarDateRanges;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Table;
import net.sf.mpxj.Task;
import net.sf.mpxj.View;
import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.sdef.SDEFWriter;
import net.sf.mpxj.writer.ProjectWriter;

/**
 * Implements the controller component of the ProjectTree MVC.
 */
public class ProjectTreeController
{
   private static final Map<String, Class<? extends ProjectWriter>> WRITER_MAP = new HashMap<String, Class<? extends ProjectWriter>>();
   static
   {
      WRITER_MAP.put("MPX", MPXWriter.class);
      WRITER_MAP.put("MSPDI", MSPDIWriter.class);
      WRITER_MAP.put("PMXML", PrimaveraPMFileWriter.class);
      WRITER_MAP.put("PLANNER", PlannerWriter.class);
      WRITER_MAP.put("JSON", JsonWriter.class);
      WRITER_MAP.put("SDEF", SDEFWriter.class);
   }

   final SimpleDateFormat m_timeFormat = new SimpleDateFormat("HH:mm");
   final SimpleDateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd");

   private static final Set<String> FILE_EXCLUDED_METHODS = excludedMethods("getAllResourceAssignments", "getAllResources", "getAllTasks", "getChildTasks", "getCalendars", "getCustomFields", "getEventManager", "getFilters", "getGroups", "getProjectProperties", "getProjectConfig", "getViews", "getTables");
   private static final Set<String> CALENDAR_EXCLUDED_METHODS = excludedMethods("getCalendarExceptions");
   private static final Set<String> TASK_EXCLUDED_METHODS = excludedMethods("getChildTasks", "getEffectiveCalendar", "getParentTask", "getResourceAssignments");
   private static final Set<String> CALENDAR_EXCEPTION_EXCLUDED_METHODS = excludedMethods("getRange");
   private static final Set<String> TABLE_EXCLUDED_METHODS = excludedMethods("getColumns");

   private final ProjectTreeModel m_model;
   private ProjectFile m_projectFile;

   /**
    * Constructor.
    *
    * @param model PoiTree model
    */
   public ProjectTreeController(ProjectTreeModel model)
   {
      m_model = model;
   }

   /**
    * Command to load a file.
    *
    * @param file file to load
    */
   public void loadFile(File file)
   {
      try
      {
         m_projectFile = new UniversalProjectReader().read(file);
         if (m_projectFile == null)
         {
            throw new IllegalArgumentException("Unsupported file type");
         }
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }

      MpxjTreeNode projectNode = new MpxjTreeNode(m_projectFile, FILE_EXCLUDED_METHODS)
      {
         @Override public String toString()
         {
            return "Project";
         }
      };

      MpxjTreeNode configNode = new MpxjTreeNode(m_projectFile.getProjectConfig())
      {
         @Override public String toString()
         {
            return "MPXJ Configuration";
         }
      };
      projectNode.add(configNode);

      MpxjTreeNode propertiesNode = new MpxjTreeNode(m_projectFile.getProjectProperties())
      {
         @Override public String toString()
         {
            return "Properties";
         }
      };
      projectNode.add(propertiesNode);

      MpxjTreeNode tasksFolder = new MpxjTreeNode("Tasks");
      projectNode.add(tasksFolder);
      addTasks(tasksFolder, m_projectFile);

      MpxjTreeNode resourcesFolder = new MpxjTreeNode("Resources");
      projectNode.add(resourcesFolder);
      addResources(resourcesFolder, m_projectFile);

      MpxjTreeNode assignmentsFolder = new MpxjTreeNode("Assignments");
      projectNode.add(assignmentsFolder);
      addAssignments(assignmentsFolder, m_projectFile);

      MpxjTreeNode calendarsFolder = new MpxjTreeNode("Calendars");
      projectNode.add(calendarsFolder);
      addCalendars(calendarsFolder, m_projectFile);

      MpxjTreeNode groupsFolder = new MpxjTreeNode("Groups");
      projectNode.add(groupsFolder);
      addGroups(groupsFolder, m_projectFile);

      MpxjTreeNode customFieldsFolder = new MpxjTreeNode("Custom Fields");
      projectNode.add(customFieldsFolder);
      addCustomFields(customFieldsFolder, m_projectFile);

      MpxjTreeNode filtersFolder = new MpxjTreeNode("Filters");
      projectNode.add(filtersFolder);

      MpxjTreeNode taskFiltersFolder = new MpxjTreeNode("Task Filters");
      filtersFolder.add(taskFiltersFolder);
      addFilters(taskFiltersFolder, m_projectFile.getFilters().getTaskFilters());

      MpxjTreeNode resourceFiltersFolder = new MpxjTreeNode("Resource Filters");
      filtersFolder.add(resourceFiltersFolder);
      addFilters(resourceFiltersFolder, m_projectFile.getFilters().getResourceFilters());

      MpxjTreeNode viewsFolder = new MpxjTreeNode("Views");
      projectNode.add(viewsFolder);
      addViews(viewsFolder, m_projectFile);

      MpxjTreeNode tablesFolder = new MpxjTreeNode("Tables");
      projectNode.add(tablesFolder);
      addTables(tablesFolder, m_projectFile);

      m_model.setRoot(projectNode);
   }

   /**
    * Add tasks to the tree.
    *
    * @param parentNode parent tree node
    * @param parent parent task container
    */
   private void addTasks(MpxjTreeNode parentNode, ChildTaskContainer parent)
   {
      for (Task task : parent.getChildTasks())
      {
         final Task t = task;
         MpxjTreeNode childNode = new MpxjTreeNode(task, TASK_EXCLUDED_METHODS)
         {
            @Override public String toString()
            {
               return t.getName();
            }
         };
         parentNode.add(childNode);
         addTasks(childNode, task);
      }
   }

   /**
    * Add resources to the tree.
    *
    * @param parentNode parent tree node
    * @param file resource container
    */
   private void addResources(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (Resource resource : file.getResources())
      {
         final Resource r = resource;
         MpxjTreeNode childNode = new MpxjTreeNode(resource)
         {
            @Override public String toString()
            {
               return r.getName();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add calendars to the tree.
    *
    * @param parentNode parent tree node
    * @param file calendar container
    */
   private void addCalendars(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (ProjectCalendar calendar : file.getCalendars())
      {
         addCalendar(parentNode, calendar);
      }
   }

   /**
    * Add a calendar node.
    *
    * @param parentNode parent node
    * @param calendar calendar
    */
   private void addCalendar(MpxjTreeNode parentNode, final ProjectCalendar calendar)
   {
      MpxjTreeNode calendarNode = new MpxjTreeNode(calendar, CALENDAR_EXCLUDED_METHODS)
      {
         @Override public String toString()
         {
            return calendar.getName();
         }
      };
      parentNode.add(calendarNode);

      MpxjTreeNode daysFolder = new MpxjTreeNode("Days");
      calendarNode.add(daysFolder);

      for (Day day : Day.values())
      {
         addCalendarDay(daysFolder, calendar, day);
      }

      MpxjTreeNode exceptionsFolder = new MpxjTreeNode("Exceptions");
      calendarNode.add(exceptionsFolder);

      for (ProjectCalendarException exception : calendar.getCalendarExceptions())
      {
         addCalendarException(exceptionsFolder, exception);
      }
   }

   /**
    * Add a calendar day node.
    *
    * @param parentNode parent node
    * @param calendar ProjectCalendar instance
    * @param day calendar day
    */
   private void addCalendarDay(MpxjTreeNode parentNode, ProjectCalendar calendar, final Day day)
   {
      MpxjTreeNode dayNode = new MpxjTreeNode(day)
      {
         @Override public String toString()
         {
            return day.name();
         }
      };
      parentNode.add(dayNode);
      addHours(dayNode, calendar.getHours(day));
   }

   /**
    * Add hours to a parent object.
    *
    * @param parentNode parent node
    * @param hours list of ranges
    */
   private void addHours(MpxjTreeNode parentNode, ProjectCalendarDateRanges hours)
   {
      for (DateRange range : hours)
      {
         final DateRange r = range;
         MpxjTreeNode rangeNode = new MpxjTreeNode(range)
         {
            @Override public String toString()
            {
               return m_timeFormat.format(r.getStart()) + " - " + m_timeFormat.format(r.getEnd());
            }
         };
         parentNode.add(rangeNode);
      }
   }

   /**
    * Add an exception to a calendar.
    *
    * @param parentNode parent node
    * @param exception calendar exceptions
    */
   private void addCalendarException(MpxjTreeNode parentNode, final ProjectCalendarException exception)
   {
      MpxjTreeNode exceptionNode = new MpxjTreeNode(exception, CALENDAR_EXCEPTION_EXCLUDED_METHODS)
      {
         @Override public String toString()
         {
            return m_dateFormat.format(exception.getFromDate());
         }
      };
      parentNode.add(exceptionNode);
      addHours(exceptionNode, exception);
   }

   /**
    * Add groups to the tree.
    *
    * @param parentNode parent tree node
    * @param file group container
    */
   private void addGroups(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (Group group : file.getGroups())
      {
         final Group g = group;
         MpxjTreeNode childNode = new MpxjTreeNode(group)
         {
            @Override public String toString()
            {
               return g.getName();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add custom fields to the tree.
    *
    * @param parentNode parent tree node
    * @param file custom fields container
    */
   private void addCustomFields(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (CustomField field : file.getCustomFields())
      {
         final CustomField c = field;
         MpxjTreeNode childNode = new MpxjTreeNode(field)
         {
            @Override public String toString()
            {
               FieldType type = c.getFieldType();
               return type == null ? "(unknown)" : type.toString();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add views to the tree.
    *
    * @param parentNode parent tree node
    * @param file views container
    */
   private void addViews(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (View view : file.getViews())
      {
         final View v = view;
         MpxjTreeNode childNode = new MpxjTreeNode(view)
         {
            @Override public String toString()
            {
               return v.getName();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add tables to the tree.
    *
    * @param parentNode parent tree node
    * @param file tables container
    */
   private void addTables(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (Table table : file.getTables())
      {
         final Table t = table;
         MpxjTreeNode childNode = new MpxjTreeNode(table, TABLE_EXCLUDED_METHODS)
         {
            @Override public String toString()
            {
               return t.getName();
            }
         };
         parentNode.add(childNode);

         addColumns(childNode, table);
      }
   }

   /**
    * Add columns to the tree.
    *
    * @param parentNode parent tree node
    * @param table columns container
    */
   private void addColumns(MpxjTreeNode parentNode, Table table)
   {
      for (Column column : table.getColumns())
      {
         final Column c = column;
         MpxjTreeNode childNode = new MpxjTreeNode(column)
         {
            @Override public String toString()
            {
               return c.getTitle();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add filters to the tree.
    *
    * @param parentNode parent tree node
    * @param filters list of filters
    */
   private void addFilters(MpxjTreeNode parentNode, List<Filter> filters)
   {
      for (Filter field : filters)
      {
         final Filter f = field;
         MpxjTreeNode childNode = new MpxjTreeNode(field)
         {
            @Override public String toString()
            {
               return f.getName();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add assignments to the tree.
    *
    * @param parentNode parent tree node
    * @param file assignments container
    */
   private void addAssignments(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (ResourceAssignment assignment : file.getResourceAssignments())
      {
         final ResourceAssignment a = assignment;
         MpxjTreeNode childNode = new MpxjTreeNode(a)
         {
            @Override public String toString()
            {
               Resource resource = a.getResource();
               String resourceName = resource == null ? "(unknown resource)" : resource.getName();
               Task task = a.getTask();
               String taskName = task == null ? "(unknown task)" : task.getName();
               return resourceName + "->" + taskName;
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Save the current file as the given type.
    *
    * @param file target file
    * @param type file type
    */
   public void saveFile(File file, String type)
   {
      try
      {
         Class<? extends ProjectWriter> fileClass = WRITER_MAP.get(type);
         if (fileClass == null)
         {
            throw new IllegalArgumentException("Cannot write files of type: " + type);
         }

         ProjectWriter writer = fileClass.newInstance();
         writer.write(m_projectFile, file);
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Generates a set of excluded method names.
    *
    * @param methodNames method names
    * @return set of method names
    */
   private static Set<String> excludedMethods(String... methodNames)
   {
      Set<String> set = new HashSet<String>(MpxjTreeNode.DEFAULT_EXCLUDED_METHODS);
      set.addAll(Arrays.asList(methodNames));
      return set;
   }
}
