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

package org.mpxj.explorer;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mpxj.ChildResourceContainer;
import org.mpxj.Code;
import org.mpxj.CodeValue;
import org.mpxj.ProjectCalendarDays;
import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeValue;
import org.mpxj.ChildTaskContainer;
import org.mpxj.Column;
import org.mpxj.CustomField;
import org.mpxj.DataLink;
import java.time.DayOfWeek;
import org.mpxj.FieldType;
import org.mpxj.Filter;
import org.mpxj.Group;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Table;
import org.mpxj.Task;
import org.mpxj.LocalTimeRange;
import org.mpxj.UserDefinedField;
import org.mpxj.View;
import org.mpxj.json.JsonWriter;
import org.mpxj.mpx.MPXWriter;
import org.mpxj.mspdi.MSPDIWriter;
import org.mpxj.planner.PlannerWriter;
import org.mpxj.primavera.PrimaveraPMFileWriter;
import org.mpxj.primavera.PrimaveraXERFileWriter;
import org.mpxj.sdef.SDEFWriter;
import org.mpxj.utility.ProjectCleanUtility;
import org.mpxj.writer.ProjectWriter;

/**
 * Implements the controller component of the ProjectTree MVC.
 */
public class ProjectTreeController
{
   private static final Map<String, Class<? extends ProjectWriter>> WRITER_MAP = new HashMap<>();
   static
   {
      WRITER_MAP.put("MPX", MPXWriter.class);
      WRITER_MAP.put("MSPDI", MSPDIWriter.class);
      WRITER_MAP.put("PMXML", PrimaveraPMFileWriter.class);
      WRITER_MAP.put("PLANNER", PlannerWriter.class);
      WRITER_MAP.put("JSON", JsonWriter.class);
      WRITER_MAP.put("SDEF", SDEFWriter.class);
      WRITER_MAP.put("XER", PrimaveraXERFileWriter.class);
   }

   final DateTimeFormatter m_timeFormat = DateTimeFormatter.ofPattern("HH:mm");
   final DateTimeFormatter m_dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   private static final Set<String> FILE_EXCLUDED_METHODS = excludedMethods("getAllResourceAssignments", "getAllResources", "getAllTasks", "getChildTasks", "getCalendars", "getCustomFields", "getEventManager", "getFilters", "getGroups", "getProjectProperties", "getProjectConfig", "getViews", "getTables");
   private static final Set<String> CALENDAR_EXCLUDED_METHODS = excludedMethods("getCalendarExceptions", "getExpandedCalendarExceptions", "getDerivedCalendars", "getHours", "getDays", "getParent", "getCalendar", "getWorkWeeks");
   private static final Set<String> CALENDAR_WEEK_EXCLUDED_METHODS = excludedMethods("getCalendar", "getDays", "getHours");
   private static final Set<String> TASK_EXCLUDED_METHODS = excludedMethods("getChildTasks", "getEffectiveCalendar", "getParentTask", "getResourceAssignments", "getSubprojectObject");
   private static final Set<String> CALENDAR_EXCEPTION_EXCLUDED_METHODS = excludedMethods("get", "getRange");
   private static final Set<String> TABLE_EXCLUDED_METHODS = excludedMethods("getColumns");
   private static final Set<String> CODE_EXCLUDED_METHODS = excludedMethods("getValues");
   private static final Set<String> CODE_VALUE_EXCLUDED_METHODS = excludedMethods("getParent", "getType");

   private final ProjectTreeModel m_model;
   private ProjectFile m_projectFile;
   private File m_file;

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
    * @param file original file
    * @param projectFile parsed project file
    */
   public void loadFile(File file, ProjectFile projectFile)
   {
      m_file = file;
      m_projectFile = projectFile;

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

      MpxjTreeNode rolesFolder = new MpxjTreeNode("Roles");
      projectNode.add(rolesFolder);
      addRoles(rolesFolder, m_projectFile);

      MpxjTreeNode assignmentsFolder = new MpxjTreeNode("Assignments");
      projectNode.add(assignmentsFolder);
      addAssignments(assignmentsFolder, m_projectFile);

      MpxjTreeNode relationsFolder = new MpxjTreeNode("Predecessors");
      projectNode.add(relationsFolder);
      addRelations(relationsFolder, m_projectFile);

      MpxjTreeNode calendarsFolder = new MpxjTreeNode("Calendars");
      projectNode.add(calendarsFolder);
      addCalendars(calendarsFolder, m_projectFile);

      MpxjTreeNode groupsFolder = new MpxjTreeNode("Groups");
      projectNode.add(groupsFolder);
      addGroups(groupsFolder, m_projectFile);

      MpxjTreeNode userDefinedFields = new MpxjTreeNode("User Defined Fields");
      projectNode.add(userDefinedFields);
      addUserDefinedFields(userDefinedFields, m_projectFile);

      MpxjTreeNode customFields = new MpxjTreeNode("Custom Fields");
      projectNode.add(customFields);
      addCustomFields(customFields, m_projectFile);

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

      MpxjTreeNode dataLinksFolder = new MpxjTreeNode("Data Links");
      projectNode.add(dataLinksFolder);
      addDataLinks(dataLinksFolder, m_projectFile);

      MpxjTreeNode activityCodesFolder = new MpxjTreeNode("Activity Codes");
      projectNode.add(activityCodesFolder);
      addActivityCodes(activityCodesFolder);

      MpxjTreeNode projectCodesFolder = new MpxjTreeNode("Project Codes");
      projectNode.add(projectCodesFolder);
      addCodes(projectCodesFolder, m_projectFile.getProjectCodes());

      MpxjTreeNode resourceCodesFolder = new MpxjTreeNode("Resource Codes");
      projectNode.add(resourceCodesFolder);
      addCodes(resourceCodesFolder, m_projectFile.getResourceCodes());

      MpxjTreeNode roleCodesFolder = new MpxjTreeNode("Role Codes");
      projectNode.add(roleCodesFolder);
      addCodes(roleCodesFolder, m_projectFile.getRoleCodes());

      MpxjTreeNode resourceAssignmentCodesFolder = new MpxjTreeNode("Resource Assignment Codes");
      projectNode.add(resourceAssignmentCodesFolder);
      addCodes(resourceAssignmentCodesFolder, m_projectFile.getResourceAssignmentCodes());

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
               return getTaskName(t);
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
   private void addResources(MpxjTreeNode parentNode, ChildResourceContainer file)
   {
      for (Resource resource : file.getChildResources())
      {
         if (resource.getRole())
         {
            continue;

         }
         final Resource r = resource;
         MpxjTreeNode childNode = new MpxjTreeNode(resource)
         {
            @Override public String toString()
            {
               return r.getName();
            }
         };
         parentNode.add(childNode);
         addResources(childNode, resource);
      }
   }

   /**
    * Add roles to the tree.
    *
    * @param parentNode parent tree node
    * @param file resource container
    */
   private void addRoles(MpxjTreeNode parentNode, ChildResourceContainer file)
   {
      for (Resource role : file.getChildResources())
      {
         if (!role.getRole())
         {
            continue;

         }
         final Resource r = role;
         MpxjTreeNode childNode = new MpxjTreeNode(role)
         {
            @Override public String toString()
            {
               return r.getName();
            }
         };
         parentNode.add(childNode);
         addRoles(childNode, role);
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
      addCalendars(parentNode, file.getCalendars().stream().filter(c -> c.getParent() == null).collect(Collectors.toList()));
   }

   private void addCalendars(MpxjTreeNode parentNode, List<ProjectCalendar> calendars)
   {
      calendars.forEach(c -> addCalendar(parentNode, c));
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

      for (DayOfWeek day : DayOfWeek.values())
      {
         addCalendarDay(daysFolder, calendar, day);
      }

      List<ProjectCalendarException> exceptions = calendar.getCalendarExceptions();
      if (!exceptions.isEmpty())
      {
         MpxjTreeNode exceptionsFolder = new MpxjTreeNode("Exceptions");
         calendarNode.add(exceptionsFolder);

         for (ProjectCalendarException exception : exceptions)
         {
            addCalendarException(exceptionsFolder, exception);
         }
      }

      List<ProjectCalendarWeek> weeks = calendar.getWorkWeeks();
      if (!weeks.isEmpty())
      {
         MpxjTreeNode workingWeeksFolder = new MpxjTreeNode("Working Weeks");
         calendarNode.add(workingWeeksFolder);
         addWorkingWeeks(workingWeeksFolder, weeks);
      }

      List<ProjectCalendar> derivedCalendars = calendar.getDerivedCalendars();
      if (!derivedCalendars.isEmpty())
      {
         MpxjTreeNode derivedCalendarsFolder = new MpxjTreeNode("Derived Calendars");
         calendarNode.add(derivedCalendarsFolder);
         addCalendars(derivedCalendarsFolder, derivedCalendars);
      }
   }

   private void addWorkingWeeks(MpxjTreeNode parentNode, List<ProjectCalendarWeek> weeks)
   {
      weeks.forEach(w -> addWorkingWeek(parentNode, w));
   }

   private void addWorkingWeek(MpxjTreeNode parentNode, ProjectCalendarWeek week)
   {
      MpxjTreeNode weekNode = new MpxjTreeNode(week, CALENDAR_WEEK_EXCLUDED_METHODS)
      {
         @Override public String toString()
         {
            String name = week.getName();
            return name == null || name.isEmpty() ? "Unnamed Week" : name;
         }
      };

      parentNode.add(weekNode);

      MpxjTreeNode daysFolder = new MpxjTreeNode("Days");
      weekNode.add(daysFolder);

      for (DayOfWeek day : DayOfWeek.values())
      {
         addCalendarDay(daysFolder, week, day);
      }

   }

   /**
    * Add a calendar day node.
    *
    * @param parentNode parent node
    * @param calendar ProjectCalendar instance
    * @param day calendar day
    */
   private void addCalendarDay(MpxjTreeNode parentNode, ProjectCalendarDays calendar, final DayOfWeek day)
   {
      MpxjTreeNode dayNode = new MpxjTreeNode(day)
      {
         @Override public String toString()
         {
            return day.name() + " (" + calendar.getCalendarDayType(day) + ")";
         }
      };
      parentNode.add(dayNode);
      addHours(dayNode, calendar.getCalendarHours(day));
   }

   /**
    * Add hours to a parent object.
    *
    * @param parentNode parent node
    * @param hours list of ranges
    */
   private void addHours(MpxjTreeNode parentNode, ProjectCalendarHours hours)
   {
      if (hours == null)
      {
         return;
      }

      for (LocalTimeRange range : hours)
      {
         final LocalTimeRange r = range;
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
    * @param file parent project
    */
   private void addCustomFields(MpxjTreeNode parentNode, ProjectFile file)
   {
      // Function to generate a name for each custom field
      Function<CustomField, String> name = f -> {
         FieldType type = f.getFieldType();
         String result = type == null ? "(unknown)" : type.getFieldTypeClass() + "." + type;
         result = f.getAlias() == null || f.getAlias().isEmpty() ? result : result + " (" + f.getAlias() + ")";
         return result;
      };

      // Use a TreeMap to sort by name
      Map<String, CustomField> map = file.getCustomFields().stream().collect(Collectors.toMap(name, Function.identity(), (u, v) -> u, TreeMap::new));
      for (Map.Entry<String, CustomField> entry : map.entrySet())
      {
         MpxjTreeNode childNode = new MpxjTreeNode(entry.getValue())
         {
            @Override public String toString()
            {
               return entry.getKey();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add user defined fields to the tree.
    *
    * @param parentNode parent tree node
    * @param file parent project
    */
   private void addUserDefinedFields(MpxjTreeNode parentNode, ProjectFile file)
   {
      // Function to generate a name for each user defined field
      Function<UserDefinedField, String> name = f -> f.getFieldTypeClass().name() + " " + f.getName() + " (" + f.name() + " " + f.getDataType().name() + ")";

      // Use a TreeMap to sort by name
      Map<String, UserDefinedField> map = file.getUserDefinedFields().stream().collect(Collectors.toMap(name, Function.identity(), (u, v) -> u, TreeMap::new));
      for (Map.Entry<String, UserDefinedField> entry : map.entrySet())
      {
         MpxjTreeNode childNode = new MpxjTreeNode(entry.getValue())
         {
            @Override public String toString()
            {
               return entry.getKey();
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
               return resourceName + "->" + getTaskName(task);
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add relations to the tree.
    *
    * @param parentNode parent tree node
    * @param file parent file
    */
   private void addRelations(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (Relation relation : file.getRelations())
      {
         final Relation r = relation;
         MpxjTreeNode childNode = new MpxjTreeNode(r)
         {
            @Override public String toString()
            {
               return getTaskName(r.getPredecessorTask()) + "->" + getTaskName(r.getSuccessorTask()) + " " + r.getType() + " " + r.getLag();
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add data links to the tree.
    *
    * @param parentNode parent tree node
    * @param file data links container
    */
   private void addDataLinks(MpxjTreeNode parentNode, ProjectFile file)
   {
      for (DataLink dataLink : file.getDataLinks())
      {
         final DataLink d = dataLink;
         MpxjTreeNode childNode = new MpxjTreeNode(dataLink, TABLE_EXCLUDED_METHODS)
         {
            @Override public String toString()
            {
               String name = d.getID();
               if (name == null)
               {
                  name = "";
               }

               int index = name.lastIndexOf('!');
               if (index == -1 || index == name.length() - 1)
               {
                  return "(none)";
               }
               return name.substring(index + 1);
            }
         };
         parentNode.add(childNode);
      }
   }

   /**
    * Add activity codes to the tree.
    *
    * @param parentNode parent tree node
    */
   private void addActivityCodes(MpxjTreeNode parentNode)
   {
      for (ActivityCode code : m_projectFile.getActivityCodes())
      {
         final ActivityCode c = code;
         MpxjTreeNode childNode = new MpxjTreeNode(code, CODE_EXCLUDED_METHODS)
         {
            @Override public String toString()
            {
               return c.getName();
            }
         };
         parentNode.add(childNode);
         addActivityCodeValues(childNode, code);
      }
   }

   /**
    * Add codes to the tree.
    *
    * @param parentNode parent tree node
    * @param codes list of codes
    */
   private void addCodes(MpxjTreeNode parentNode, List<? extends Code> codes)
   {
      for (Code code : codes)
      {
         final Code c = code;
         MpxjTreeNode childNode = new MpxjTreeNode(code, CODE_EXCLUDED_METHODS)
         {
            @Override public String toString()
            {
               return c.getName();
            }
         };
         parentNode.add(childNode);
         addCodeValues(childNode, code);
      }
   }

   private void addActivityCodeValues(MpxjTreeNode parentNode, ActivityCode code)
   {
      code.getChildValues().forEach(v -> addActivityCodeValues(parentNode, v));
   }

   private void addCodeValues(MpxjTreeNode parentNode, Code code)
   {
      code.getChildValues().forEach(v -> addCodeValues(parentNode, v));
   }

   private void addActivityCodeValues(MpxjTreeNode parentNode, ActivityCodeValue value)
   {
      MpxjTreeNode node = new MpxjTreeNode(value, CODE_VALUE_EXCLUDED_METHODS);
      parentNode.add(node);
      value.getChildValues().forEach(v -> addActivityCodeValues(node, v));
   }

   private void addCodeValues(MpxjTreeNode parentNode, CodeValue value)
   {
      MpxjTreeNode node = new MpxjTreeNode(value, CODE_VALUE_EXCLUDED_METHODS);
      parentNode.add(node);
      value.getChildValues().forEach(v -> addCodeValues(node, v));
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
         if (fileClass == JsonWriter.class)
         {
            ((JsonWriter) writer).setPretty(true);
         }

         if (fileClass == MSPDIWriter.class)
         {
            ((MSPDIWriter) writer).setWriteTimephasedData(m_model.getWriteOptions().getWriteTimephasedData());
            ((MSPDIWriter) writer).setSplitTimephasedAsDays(m_model.getWriteOptions().getSplitTimephasedDataAsDays());
         }

         writer.write(m_projectFile, file);
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Create an anonymized version of the original file.
    *
    * @param file output file
    */
   public void cleanFile(File file)
   {
      try
      {
         new ProjectCleanUtility().process(m_file.getCanonicalPath(), file.getCanonicalPath());
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Retrieve the task name, decorated to indicate if it is an external task or project.
    *
    * @param task Task instance
    * @return decorated task name
    */
   private String getTaskName(Task task)
   {
      if (task == null)
      {
         return "(unknown task)";
      }

      String externalTaskLabel = task.getExternalTask() ? " [EXTERNAL TASK]" : "";
      String externalProjectLabel = task.getExternalProject() ? " [EXTERNAL PROJECT]" : "";

      return task.getName() + externalTaskLabel + externalProjectLabel;
   }

   /**
    * Generates a set of excluded method names.
    *
    * @param methodNames method names
    * @return set of method names
    */
   private static Set<String> excludedMethods(String... methodNames)
   {
      Set<String> set = new HashSet<>(MpxjTreeNode.DEFAULT_EXCLUDED_METHODS);
      set.addAll(Arrays.asList(methodNames));
      return set;
   }
}
