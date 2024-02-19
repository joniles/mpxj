/*
 * file:       ProjectFile.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2006
 * date:       15/08/2002
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

package net.sf.mpxj;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ObjectSequence;

/**
 * This class represents a project plan.
 */
public final class ProjectFile implements ChildTaskContainer, ChildResourceContainer
{
   /**
    * Retrieve project configuration data.
    *
    * @return ProjectConfig instance.
    */
   public ProjectConfig getProjectConfig()
   {
      return m_config;
   }

   /**
    * This method allows a task to be added to the file programmatically.
    *
    * @return new task object
    */
   @Override public Task addTask()
   {
      return m_tasks.add();
   }

   /**
    * This method is used to remove a task from the project.
    *
    * @param task task to be removed
    */
   public void removeTask(Task task)
   {
      m_tasks.remove(task);
   }

   /**
    * This method is called to ensure that all unique ID values
    * held by MPXJ are within the range supported by MS Project.
    * If any of these values fall outside of this range, the unique IDs
    * of the relevant entities are renumbered.
    * @deprecated no longer required as the MSPDI and MPX writers handle this dynamically without changing the original schedule
    */
   @Deprecated public void validateUniqueIDsForMicrosoftProject()
   {
      // Deprecated
   }

   /**
    * This method is used to retrieve a list of all top level tasks
    * defined in this project file.
    *
    * @return list of tasks
    */
   @Override public List<Task> getChildTasks()
   {
      return m_childTasks;
   }

   /**
    * This method is used to retrieve a list of all top level resources
    * defined in this project file.
    *
    * @return list of resources
    */
   @Override public List<Resource> getChildResources()
   {
      return m_childResources;
   }

   /**
    * This method is used to retrieve a list of all tasks
    * defined in this project file.
    *
    * @return list of all tasks
    */
   public TaskContainer getTasks()
   {
      return m_tasks;
   }

   /**
    * This method is used to add a new calendar to the file.
    *
    * @return new calendar object
    */
   public ProjectCalendar addCalendar()
   {
      return m_calendars.add();
   }

   /**
    * Removes a calendar.
    *
    * @param calendar calendar to be removed
    */
   public void removeCalendar(ProjectCalendar calendar)
   {
      m_calendars.remove(calendar);
   }

   /**
    * This is a convenience method used to add a calendar called
    * "Standard" to the file, and populate it with a default working week
    * and default working hours.
    *
    * @return a new default calendar
    */
   public ProjectCalendar addDefaultBaseCalendar()
   {
      return m_calendars.addDefaultBaseCalendar();
   }

   /**
    * This is a convenience method to add a default derived
    * calendar.
    *
    * @return new ProjectCalendar instance
    */
   public ProjectCalendar addDefaultDerivedCalendar()
   {
      return m_calendars.addDefaultDerivedCalendar();
   }

   /**
    * This method retrieves the list of calendars defined in
    * this file.
    *
    * @return list of calendars
    */
   public ProjectCalendarContainer getCalendars()
   {
      return m_calendars;
   }

   /**
    * This method is used to retrieve the project properties.
    *
    * @return project properties
    */
   public ProjectProperties getProjectProperties()
   {
      return m_properties;
   }

   /**
    * This method is used to add a new resource to the file.
    *
    * @return new resource object
    */
   @Override public Resource addResource()
   {
      return m_resources.add();
   }

   /**
    * This method is used to remove a resource from the project.
    *
    * @param resource resource to be removed
    */
   public void removeResource(Resource resource)
   {
      m_resources.remove(resource);
   }

   /**
    * Retrieves a list of all resources in this project.
    *
    * @return list of all resources
    */
   public ResourceContainer getResources()
   {
      return m_resources;
   }

   /**
    * Retrieves a list of all resource assignments in this project.
    *
    * @return list of all resources
    */
   public ResourceAssignmentContainer getResourceAssignments()
   {
      return m_assignments;
   }

   /**
    * Retrieves a list of all relations in this project.
    *
    * @return list of all relations
    */
   public RelationContainer getRelations()
   {
      return m_relations;
   }

   /**
    * Retrieves the named calendar. This method will return
    * null if the named calendar is not located.
    *
    * @param calendarName name of the required calendar
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getCalendarByName(String calendarName)
   {
      return m_calendars.getByName(calendarName);
   }

   /**
    * Retrieves the calendar referred to by the supplied unique ID
    * value. This method will return null if the required calendar is not
    * located.
    *
    * @param calendarID calendar unique ID
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getCalendarByUniqueID(Integer calendarID)
   {
      return m_calendars.getByUniqueID(calendarID);
   }

   /**
    * This method allows an arbitrary task to be retrieved based
    * on its ID field.
    *
    * @param id task identified
    * @return the requested task, or null if not found
    */
   public Task getTaskByID(Integer id)
   {
      return m_tasks.getByID(id);
   }

   /**
    * This method allows an arbitrary task to be retrieved based
    * on its UniqueID field.
    *
    * @param id task identified
    * @return the requested task, or null if not found
    */
   public Task getTaskByUniqueID(Integer id)
   {
      return m_tasks.getByUniqueID(id);
   }

   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its ID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByID(Integer id)
   {
      return m_resources.getByID(id);
   }

   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its UniqueID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByUniqueID(Integer id)
   {
      return m_resources.getByUniqueID(id);
   }

   /**
    * This method is used to recreate the hierarchical structure of the
    * project file from scratch. The method sorts the list of all tasks,
    * then iterates through it creating the parent-child structure defined
    * by the outline level field.
    */
   public void updateStructure()
   {
      m_tasks.updateStructure();
      m_resources.updateStructure();
   }

   /**
    * Find the earliest task start date.
    *
    * @return start date
    */
   public LocalDateTime getEarliestStartDate()
   {
      LocalDateTime startDate = null;

      for (Task task : m_tasks)
      {
         //
         // If a hidden "summary" task is present we ignore it
         //
         if (NumberHelper.getInt(task.getUniqueID()) == 0)
         {
            continue;
         }

         //
         // Select the actual or forecast start date. Note that the
         // behaviour is different for milestones. The milestone end date
         // is always correct, the milestone start date may be different
         // to reflect a missed deadline.
         //
         LocalDateTime taskStartDate;
         if (task.getMilestone())
         {
            taskStartDate = task.getActualFinish();
            if (taskStartDate == null)
            {
               taskStartDate = task.getFinish();
            }
         }
         else
         {
            taskStartDate = task.getActualStart();
            if (taskStartDate == null)
            {
               taskStartDate = task.getStart();
            }
         }

         if (taskStartDate != null)
         {
            if (startDate == null)
            {
               startDate = taskStartDate;
            }
            else
            {
               if (taskStartDate.isBefore(startDate))
               {
                  startDate = taskStartDate;
               }
            }
         }
      }

      return (startDate);
   }

   /**
    * Find the latest task finish date.
    *
    * @return finish date
    */
   public LocalDateTime getLatestFinishDate()
   {
      LocalDateTime finishDate = null;

      for (Task task : m_tasks)
      {
         //
         // If a hidden "summary" task is present we ignore it
         //
         if (NumberHelper.getInt(task.getUniqueID()) == 0)
         {
            continue;
         }

         //
         // Select the actual or forecast start date
         //
         LocalDateTime taskFinishDate;
         taskFinishDate = task.getActualFinish();
         if (taskFinishDate == null)
         {
            taskFinishDate = task.getFinish();
         }

         if (taskFinishDate != null)
         {
            if (finishDate == null)
            {
               finishDate = taskFinishDate;
            }
            else
            {
               if (taskFinishDate.isAfter(finishDate))
               {
                  finishDate = taskFinishDate;
               }
            }
         }
      }

      return (finishDate);
   }

   /**
    * This method returns a list of the views defined in this MPP file.
    *
    * @return list of views
    */
   public ViewContainer getViews()
   {
      return m_views;
   }

   /**
    * This method returns the tables defined in an MPP file.
    *
    * @return list of tables
    */
   public TableContainer getTables()
   {
      return m_tables;
   }

   /**
    * This method returns the filters defined in an MPP file.
    *
    * @return filters
    */
   public FilterContainer getFilters()
   {
      return m_filters;
   }

   /**
    * Retrieves a list of all groups.
    *
    * @return list of all groups
    */
   public GroupContainer getGroups()
   {
      return m_groups;
   }

   /**
    * Retrieve the event manager for this project.
    *
    * @return event manager
    */
   public EventManager getEventManager()
   {
      return m_eventManager;
   }

   /**
    * Retrieves the custom fields for this project.
    *
    * @return custom fields
    */
   public CustomFieldContainer getCustomFields()
   {
      return m_customFields;
   }

   /**
    * Retrieves the activity code configuration for this project.
    *
    * @return activity codes
    */
   public ActivityCodeContainer getActivityCodes()
   {
      return m_activityCodes;
   }

   /**
    * Retrieves the data link configuration for this project.
    *
    * @return data links
    */
   public DataLinkContainer getDataLinks()
   {
      return m_dataLinks;
   }

   /**
    * Retrieves the expense categories available for this schedule.
    *
    * @return expense categories
    */
   public ExpenseCategoryContainer getExpenseCategories()
   {
      return m_expenseCategories;
   }

   /**
    * Retrieves the cost accounts available for this schedule.
    *
    * @return cost accounts
    */
   public CostAccountContainer getCostAccounts()
   {
      return m_costAccounts;
   }

   /**
    * Retrieves the user defined fields available for this schedule.
    *
    * @return user defined fields
    */
   public UserDefinedFieldContainer getUserDefinedFields()
   {
      return m_userDefinedFields;
   }

   /**
    * Retrieves the work contours available for this schedule.
    *
    * @return work contours
    */
   public WorkContourContainer getWorkContours()
   {
      return m_workContours;
   }

   /**
    * Retrieves the notes topics available for this schedule.
    *
    * @return notes topics
    */
   public NotesTopicContainer getNotesTopics()
   {
      return m_notesTopics;
   }

   /**
    * Retrieve the locations available for this schedule.
    *
    * @return locations
    */
   public LocationContainer getLocations()
   {
      return m_locations;
   }

   /**
    * Retrieve the units of measure available for this schedule.
    *
    * @return units of measure
    */
   public UnitOfMeasureContainer getUnitsOfMeasure()
   {
      return m_unitsOfMeasure;
   }

   /**
    * Retrieves the default calendar for this project based on the calendar name
    * given in the project properties. If a calendar of this name cannot be found, then
    * the first calendar listed for the project will be returned. If the
    * project contains no calendars, then a default calendar is added.
    *
    * @return default projectCalendar instance
    */
   public ProjectCalendar getDefaultCalendar()
   {
      return getProjectProperties().getDefaultCalendar();
   }

   /**
    * Sets the default calendar for this project.
    *
    * @param calendar default calendar instance
    */
   public void setDefaultCalendar(ProjectCalendar calendar)
   {
      if (calendar != null)
      {
         m_properties.setDefaultCalendar(calendar);
      }
   }

   /**
    * Retrieve the calendar used internally for timephased baseline calculation.
    * All baseline timephased data is relative to this calendar.
    * The calendar is created at the point the first baseline is taken and is
    * a copy of the default calendar at that time.
    *
    * @return baseline calendar
    */
   public ProjectCalendar getBaselineCalendar()
   {
      //
      // Attempt to locate the calendar normally used by baselines
      // If this isn't present, fall back to using the default
      // project calendar.
      //
      ProjectCalendar result = getCalendarByName(m_properties.getBaselineCalendarName());
      if (result == null)
      {
         result = getDefaultCalendar();
      }
      return result;
   }

   /**
    * Retrieve the baselines linked to this project.
    * The baseline at index zero is the default baseline,
    * the values at the remaining indexes (1-10) are the
    * numbered baselines. The list will contain null
    * if a particular baseline has not been set.
    *
    * @return list of baselines
    */
   public List<ProjectFile> getBaselines()
   {
      return Arrays.asList(m_baselines);
   }

   /**
    * Store the supplied project as the default baseline, and use it to set the
    * baseline cost, duration, finish, fixed cost accrual, fixed cost, start and
    * work attributes for the tasks in the current project.
    *
    * @param baseline baseline project
    */
   public void setBaseline(ProjectFile baseline)
   {
      setBaseline(baseline, 0);
   }

   /**
    * Store the supplied project as baselineN, and use it to set the
    * baselineN cost, duration, finish, fixed cost accrual, fixed cost, start and
    * work attributes for the tasks in the current project.
    * The index argument selects which of the 10 baselines to populate. Passing
    * an index of 0 populates the default baseline.
    *
    * @param baseline baseline project
    * @param index baseline to populate (0-10)
    */
   public void setBaseline(ProjectFile baseline, int index)
   {
      if (index < 0 || index >= m_baselines.length)
      {
         throw new IllegalArgumentException(index + " is not a valid baseline index");
      }

      m_baselines[index] = baseline;
      m_config.getBaselineStrategy().populateBaseline(this, baseline, index);
   }

   /**
    * Clear the default baseline for this project.
    */
   public void clearBaseline()
   {
      clearBaseline(0);
   }

   /**
    * Clear baselineN (1-10) for this project.
    *
    * @param index baseline index
    */
   public void clearBaseline(int index)
   {
      new DefaultBaselineStrategy().clearBaseline(this, index);
   }

   /**
    * A convenience method used to retrieve a set of FieldType instances representing
    * all populated fields in the project.
    *
    * @return set of all populated fields
    */
   public Set<FieldType> getPopulatedFields()
   {
      return Stream.of(m_tasks.getPopulatedFields(), m_resources.getPopulatedFields(), m_assignments.getPopulatedFields(), m_properties.getPopulatedFields()).flatMap(Collection::stream).collect(Collectors.toSet());
   }

   /**
    * Calling this method will recursively expand any subprojects
    * in the current file and in turn any subprojects those files contain.
    * The tasks from the subprojects will be attached
    * to what was originally the subproject task. Assuming all subproject
    * files can be located and loaded correctly, this will present
    * a complete view of the project.
    * <p/>
    * Note that the current project and any subprojects are still independent
    * projects, so while you can recursively descend through the hierarchy
    * of tasks to visit all tasks from all files, the {@code ProjectFile.getTasks()}
    * collection will still only contain the tasks from the original project,
    * not  all the subprojects.
    *
    * @deprecated use the new version of this method which accepts a Boolean flag
    */
   @Deprecated public void expandSubprojects()
   {
      getTasks().stream().map(Task::expandSubproject).filter(Objects::nonNull).forEach(ProjectFile::expandSubprojects);
   }

   /**
    * Calling this method will recursively expand any subprojects
    * in the current file and in turn any subprojects those files contain.
    * The tasks from the subprojects will be attached
    * to what was originally the subproject task. Assuming all subproject
    * files can be located and loaded correctly, this will present
    * a complete view of the project.
    * <p/>
    * Note that the current project and any subprojects are still independent
    * projects, so while you can recursively descend through the hierarchy
    * of tasks to visit all tasks from all files, the {@code ProjectFile.getTasks()}
    * collection will still only contain the tasks from the original project,
    * not  all the subprojects.
    * <p/>
    * Passing {@code true} for the {@code replaceExternalTasks} flag will
    * replace any predecessor or successor relationships with external tasks
    * with new relationships which link to the original tasks. For each
    * external task where this is successful, the external task itself will
    * be removed as it is just a placeholder and is no longer required.
    *
    * @param replaceExternalTasks flag indicating if external tasks should be replaced
    */
   public void expandSubprojects(boolean replaceExternalTasks)
   {
      getTasks().stream().map(Task::expandSubproject).filter(Objects::nonNull).forEach(ProjectFile::expandSubprojects);
      if (replaceExternalTasks)
      {
         replaceExternalTasks();
      }
   }

   /**
    * Calling this method will replace any predecessors or successors which link to external tasks with new predecessors or successors
    * which link to the correct tasks across projects. As the external task instance are just placeholders,
    * these are now removed as they serve no further purpose.
    */
   private void replaceExternalTasks()
   {
      List<Task> externalTasks = new ArrayList<>();
      findExternalTasks(getChildTasks(), externalTasks);
      Set<Task> replacedTasks = externalTasks.stream().map(t -> replaceRelations(t)).filter(t -> t != null).collect(Collectors.toSet());
      removeExternalTasks(getChildTasks(), replacedTasks);
   }

   /**
    * Replaces any predecessor or successor relations for this external task.
    *
    * @param externalTask external task to replace
    * @return the external task if relations successfully replaced, or null if not replaced
    */
   private Task replaceRelations(Task externalTask)
   {
      ProjectFile originalProjectFile = findProject(externalTask.getSubprojectFile());
      if (originalProjectFile == null)
      {
         return null;
      }

      Task originalTask = findTask(originalProjectFile, externalTask);
      if (originalTask == null)
      {
         return null;
      }

      replaceRelations(externalTask, originalTask);

      return externalTask;
   }

   /**
    * Given a project's filename, find the relevant ProjectFile instance.
    *
    * @param name project filename
    * @return ProjectFile instance or null if the project can't be found
    */
   private ProjectFile findProject(String name)
   {
      if (name.equals(m_properties.getProjectFilePath()))
      {
         return this;
      }
      return m_externalProjects.read(name);
   }

   /**
    * Find the original task in a ProjectFile instance which is represented by
    * an external task.
    *
    * @param file project containing the original task
    * @param externalTask external task representing the original task
    * @return Task instance, or null if we can't find the original task
    */
   private Task findTask(ProjectFile file, Task externalTask)
   {
      Integer id = externalTask.getSubprojectTaskUniqueID();
      if (id != null)
      {
         Task result = file.getTaskByUniqueID(id);
         if (result != null)
         {
            return result;
         }
      }

      id = externalTask.getSubprojectTaskID();
      if (id != null)
      {
         return file.getTaskByID(id);
      }

      return null;
   }

   /**
    * Where we have predecessor or successor Relation instances which link to external tasks,
    * replace these with new Relation instance which link to the original task.
    *
    * @param externalTask external Task instance
    * @param originalTask original Task instance
    */
   private void replaceRelations(Task externalTask, Task originalTask)
   {
      RelationContainer relations = externalTask.getParentFile().getRelations();

      // create copies to avoid concurrent modification
      List<Relation> successors = new ArrayList<>(relations.getRawSuccessors(externalTask));
      List<Relation> predecessors = new ArrayList<>(relations.getPredecessors(externalTask));

      for (Relation originalRelation : successors)
      {
         relations.remove(originalRelation);
         originalRelation.getSourceTask().addPredecessor(Relation.Builder.from(originalRelation).targetTask(originalTask));
      }

      for (Relation originalRelation : predecessors)
      {
         relations.remove(originalRelation);
         originalTask.addPredecessor(Relation.Builder.from(originalRelation));
      }
   }

   /**
    * Recursively descend through the hierarchy of tasks to identify external tasks,
    * and return them in the supplied list.
    *
    * @param tasks list of tasks to examine
    * @param externalTasks list of external tasks
    */
   private void findExternalTasks(List<Task> tasks, List<Task> externalTasks)
   {
      externalTasks.addAll(tasks.stream().filter(t -> t.getExternalTask()).collect(Collectors.toList()));
      tasks.forEach(t -> findExternalTasks(t.getChildTasks(), externalTasks));
   }

   /**
    * This method recursively descends through the hierarchy of tasks
    * to remove any external tasks which are no longer required.
    *
    * @param tasks list of tasks to examine
    * @param replacedTasks set of external tasks to remove
    */
   private void removeExternalTasks(List<Task> tasks, Set<Task> replacedTasks)
   {
      tasks.removeIf(t -> replacedTasks.contains(t));
      for (Task task : tasks)
      {
         removeExternalTasks(task.getChildTasks(), replacedTasks);
      }
   }

   /**
    * Called by a reader class when reading a schedule is complete.
    */
   public void readComplete()
   {
      fixUniqueIdClashes();
   }

   /**
    * This method is called to ensure that after a project file has been
    * read, the cached unique ID values used to generate new unique IDs
    * start after the end of the existing set of unique IDs.
    *
    * @deprecated no longer required
    */
   @Deprecated public void updateUniqueIdCounters()
   {
      // Deprecated
   }

   /**
    * This method is called to renumber any Unique ID values which
    * were found to have duplicates.
    */
   public void fixUniqueIdClashes()
   {
      getTasks().fixUniqueIdClashes();
      getResources().fixUniqueIdClashes();
      getCalendars().fixUniqueIdClashes();
      getResourceAssignments().fixUniqueIdClashes();
      getRelations().fixUniqueIdClashes();
   }

   /**
    * Retrieve the ObjectSequence instance used to generate Unique ID values for a given class.
    *
    * @param c target class
    * @return ObjectSequence instance
    */
   public ObjectSequence getUniqueIdObjectSequence(Class<?> c)
   {
      return m_uniqueIdObjectSequences.computeIfAbsent(c, x -> new ObjectSequence(1));
   }

   /**
    * Add an error which has been ignored while reading this schedule.
    *
    * @param ex ignored error
    */
   public void addIgnoredError(Exception ex)
   {
      m_ignoredErrors.add(ex);
   }

   /**
    * Retrieve a list of errors ignored when reading this schedule.
    *
    * @return list of errors
    */
   public List<Exception> getIgnoredErrors()
   {
      return m_ignoredErrors;
   }

   void addExternalProject(String fileName, ProjectFile projectFile)
   {
      m_externalProjects.add(fileName, projectFile);
   }

   ProjectFile readExternalProject(String fileName)
   {
      return m_externalProjects.read(fileName);
   }

   private final ProjectConfig m_config = new ProjectConfig(this);
   private final ProjectProperties m_properties = new ProjectProperties(this);
   private final ResourceContainer m_resources = new ResourceContainer(this);
   private final TaskContainer m_tasks = new TaskContainer(this);
   private final List<Task> m_childTasks = new ArrayList<>();
   private final List<Resource> m_childResources = new ArrayList<>();
   private final ResourceAssignmentContainer m_assignments = new ResourceAssignmentContainer(this);
   private final RelationContainer m_relations = new RelationContainer(this);
   private final ProjectCalendarContainer m_calendars = new ProjectCalendarContainer(this);
   private final TableContainer m_tables = new TableContainer();
   private final FilterContainer m_filters = new FilterContainer();
   private final GroupContainer m_groups = new GroupContainer();
   private final ViewContainer m_views = new ViewContainer();
   private final EventManager m_eventManager = new EventManager();
   private final CustomFieldContainer m_customFields = new CustomFieldContainer();
   private final ActivityCodeContainer m_activityCodes = new ActivityCodeContainer();
   private final DataLinkContainer m_dataLinks = new DataLinkContainer();
   private final ExpenseCategoryContainer m_expenseCategories = new ExpenseCategoryContainer(this);
   private final CostAccountContainer m_costAccounts = new CostAccountContainer(this);
   private final UserDefinedFieldContainer m_userDefinedFields = new UserDefinedFieldContainer(this);
   private final WorkContourContainer m_workContours = new WorkContourContainer(this);
   private final NotesTopicContainer m_notesTopics = new NotesTopicContainer(this);
   private final LocationContainer m_locations = new LocationContainer(this);
   private final UnitOfMeasureContainer m_unitsOfMeasure = new UnitOfMeasureContainer(this);
   private final ExternalProjectContainer m_externalProjects = new ExternalProjectContainer(this);
   private final ProjectFile[] m_baselines = new ProjectFile[11];
   private final List<Exception> m_ignoredErrors = new ArrayList<>();
   private final Map<Class<?>, ObjectSequence> m_uniqueIdObjectSequences = new HashMap<>();
}
