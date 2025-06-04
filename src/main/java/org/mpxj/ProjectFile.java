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

package org.mpxj;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mpxj.common.NumberHelper;
import org.mpxj.common.ObjectSequence;

/**
 * This class represents a project plan.
 */
public final class ProjectFile implements ChildTaskContainer, ChildResourceContainer, UniqueIdObjectSequenceProvider
{
   /**
    * Default constructor.
    */
   public ProjectFile()
   {
      m_shared = new ProjectFileSharedData();
   }

   /**
    * Constructor allowing a ProjectFileSharedData instance to be passed.
    *
    * @param shared ProjectFileSharedData instance
    */
   public ProjectFile(ProjectFileSharedData shared)
   {
      m_shared = shared;
   }

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
      return m_tasks.stream().map(this::getStartDate).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
   }

   /**
    * Calculate the actual start date for the project.
    *
    * @return project actual start date
    */
   LocalDateTime getActualStart()
   {
      return m_tasks.stream().map(Task::getActualStart).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
   }

   /**
    * Calculate the actual finish date for the project.
    *
    * @return project actual finish date
    */
   LocalDateTime getActualFinish()
   {
      if (m_tasks.stream().map(Task::getActualFinish).anyMatch(Objects::isNull))
      {
         return null;
      }

      return m_tasks.stream().map(Task::getActualFinish).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
   }

   /**
    * Find the latest task finish date.
    *
    * @return finish date
    */
   public LocalDateTime getLatestFinishDate()
   {
      return m_tasks.stream().map(this::getFinishDate).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
   }

   /**
    * Retrieve the start date for a task.
    *
    * @param task task
    * @return start date
    */
   private LocalDateTime getStartDate(Task task)
   {
      //
      // If a hidden "summary" task is present we ignore it
      //
      if (NumberHelper.getInt(task.getUniqueID()) == 0)
      {
         return null;
      }

      //
      // Select the actual or planned start date. Note that the
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
      return taskStartDate;
   }

   /**
    * Retrieve the finish date for a task.
    *
    * @param task task
    * @return finish date
    */
   private LocalDateTime getFinishDate(Task task)
   {
      //
      // If a hidden "summary" task is present we ignore it
      //
      if (NumberHelper.getInt(task.getUniqueID()) == 0)
      {
         return null;
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
      return taskFinishDate;
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
      return m_shared.getCustomFields();
   }

   /**
    * Retrieves the activity code configuration for this project.
    *
    * @return activity codes
    */
   public ActivityCodeContainer getActivityCodes()
   {
      return m_shared.getActivityCodes();
   }

   /**
    * Retrieves the project code configuration for this project.
    *
    * @return project codes
    */
   public ProjectCodeContainer getProjectCodes()
   {
      return m_shared.getProjectCodes();
   }

   /**
    * Retrieves the resource code configuration for this project.
    *
    * @return resource codes
    */
   public ResourceCodeContainer getResourceCodes()
   {
      return m_shared.getResourceCodes();
   }

   /**
    * Retrieves the role code configuration for this project.
    *
    * @return role codes
    */
   public RoleCodeContainer getRoleCodes()
   {
      return m_shared.getRoleCodes();
   }

   /**
    * Retrieves the resource assignment code configuration for this project.
    *
    * @return resource assignment codes
    */
   public ResourceAssignmentCodeContainer getResourceAssignmentCodes()
   {
      return m_shared.getResourceAssignmentCodes();
   }

   /**
    * Retrieves the shifts for this project.
    *
    * @return shifts
    */
   public ShiftContainer getShifts()
   {
      return m_shared.getShifts();
   }

   /**
    * Retrieves the shift periods for this project.
    *
    * @return shift periods
    */
   public ShiftPeriodContainer getShiftPeriods()
   {
      return m_shared.getShiftPeriods();
   }

   /**
    * Retrieves the currencies for this project.
    *
    * @return currencies
    */
   public CurrencyContainer getCurrencies()
   {
      return m_shared.getCurrencies();
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
      return m_shared.getExpenseCategories();
   }

   /**
    * Retrieves the cost accounts available for this schedule.
    *
    * @return cost accounts
    */
   public CostAccountContainer getCostAccounts()
   {
      return m_shared.getCostAccounts();
   }

   /**
    * Retrieves the user defined fields available for this schedule.
    *
    * @return user defined fields
    */
   public UserDefinedFieldContainer getUserDefinedFields()
   {
      return m_shared.getUserDefinedFields();
   }

   /**
    * Retrieves the work contours available for this schedule.
    *
    * @return work contours
    */
   public WorkContourContainer getWorkContours()
   {
      return m_shared.getWorkContours();
   }

   /**
    * Retrieves the notes topics available for this schedule.
    *
    * @return notes topics
    */
   public NotesTopicContainer getNotesTopics()
   {
      return m_shared.getNotesTopics();
   }

   /**
    * Retrieve the locations available for this schedule.
    *
    * @return locations
    */
   public LocationContainer getLocations()
   {
      return m_shared.getLocations();
   }

   /**
    * Retrieve the units of measure available for this schedule.
    *
    * @return units of measure
    */
   public UnitOfMeasureContainer getUnitsOfMeasure()
   {
      return m_shared.getUnitsOfMeasure();
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
    * Retrieve the default baseline project.
    *
    * @return ProjectFile instance or null
    */
   public ProjectFile getBaseline()
   {
      return getBaseline(0);
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
      if (index == 0)
      {
         m_properties.setBaselineDate(baseline.getProjectProperties().getCreationDate());
      }
      else
      {
         m_properties.setBaselineDate(index, baseline.getProjectProperties().getCreationDate());
      }

      m_config.getBaselineStrategy().populateBaseline(this, baseline, index);
   }

   /**
    * Retrieve baselineN from Baseline, Baseline1, Baseline2 ... Baseline10.
    * Returns null if the specified baseline has not been set.
    *
    * @param index 0-10 representing Baseline, Baseline1, Baseline2 ... Baseline10
    * @return ProjectFile instance or null
    */
   public ProjectFile getBaseline(int index)
   {
      if (index < 0 || index >= m_baselines.length)
      {
         throw new IllegalArgumentException(index + " is not a valid baseline index");
      }
      return m_baselines[index];
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
      m_config.getBaselineStrategy().clearBaseline(this, index);
   }

   /**
    * Set a map of tasks in this project to tasks in baseline project.
    * Populated when a baseline is added.
    *
    * @param index baseline index
    * @param map map of current project tasks to baseline project tasks
    */
   void setBaselineTaskMap(int index, Map<Task, Task> map)
   {
      m_baselineTaskMap.put(Integer.valueOf(index), map);
   }

   /**
    * Map of tasks in this project to tasks in a baseline project.
    * Populated when a baseline is added.
    *
    * @param index baseline index
    * @return map of current project tasks to baseline project tasks
    */
   Map<Task, Task> getBaselineTaskMap(int index)
   {
      return m_baselineTaskMap.getOrDefault(Integer.valueOf(index), Collections.emptyMap());
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
      getTasks().stream().map(Task::expandSubproject).filter(Objects::nonNull).forEach(p -> p.expandSubprojects(replaceExternalTasks));
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
      Set<Task> replacedTasks = externalTasks.stream().map(this::replaceRelations).filter(Objects::nonNull).collect(Collectors.toSet());
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
      List<Relation> successors = new ArrayList<>(relations.getSuccessors(externalTask));
      List<Relation> predecessors = new ArrayList<>(relations.getPredecessors(externalTask));

      for (Relation originalRelation : successors)
      {
         relations.remove(originalRelation);
         originalRelation.getSuccessorTask().addPredecessor(new Relation.Builder().from(originalRelation).predecessorTask(originalTask));
      }

      for (Relation originalRelation : predecessors)
      {
         relations.remove(originalRelation);
         originalTask.addPredecessor(new Relation.Builder().from(originalRelation));
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
      externalTasks.addAll(tasks.stream().filter(Task::getExternalTask).collect(Collectors.toList()));
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
      tasks.removeIf(replacedTasks::contains);
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
   @Override public ObjectSequence getUniqueIdObjectSequence(Class<?> c)
   {
      return ProjectFileSharedData.contains(c) ? m_shared.getUniqueIdObjectSequence(c) : m_uniqueIdObjectSequences.computeIfAbsent(c.getName(), x -> new ObjectSequence(1));
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

   private final ProjectConfig m_config = new ProjectConfig();
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
   private final DataLinkContainer m_dataLinks = new DataLinkContainer();
   private final ExternalProjectContainer m_externalProjects = new ExternalProjectContainer(this);
   private final ProjectFile[] m_baselines = new ProjectFile[11];
   private final Map<Integer, Map<Task, Task>> m_baselineTaskMap = new HashMap<>();
   private final List<Exception> m_ignoredErrors = new ArrayList<>();
   private final Map<String, ObjectSequence> m_uniqueIdObjectSequences = new HashMap<>();
   private final ProjectFileSharedData m_shared;
}
