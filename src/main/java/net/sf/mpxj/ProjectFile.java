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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.common.NumberHelper;

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
    */
   public void validateUniqueIDsForMicrosoftProject()
   {
      // The default calendar is a special case as we hold
      // a reference to its ID in the project properties.
      // We'll grab a copy of it here then set it again
      // after we've renumbered, so the ID value is correct.
      ProjectCalendar defaultCalendar = getDefaultCalendar();
      m_tasks.validateUniqueIDsForMicrosoftProject();
      m_resources.validateUniqueIDsForMicrosoftProject();
      m_assignments.validateUniqueIDsForMicrosoftProject();
      m_calendars.validateUniqueIDsForMicrosoftProject();
      setDefaultCalendar(defaultCalendar);
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
   public Date getEarliestStartDate()
   {
      Date startDate = null;

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
         Date taskStartDate;
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
               if (taskStartDate.getTime() < startDate.getTime())
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
   public Date getLatestFinishDate()
   {
      Date finishDate = null;

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
         Date taskFinishDate;
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
               if (taskFinishDate.getTime() > finishDate.getTime())
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
    * Retrieves all the subprojects for this project.
    *
    * @return all subproject details
    * @deprecated use the attributes on individual tasks to retrieve subproject details
    */
   @Deprecated public SubProjectContainer getSubProjects()
   {
      return m_subProjects;
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
      ProjectCalendar result = getCalendarByName("Used for Microsoft Project 98 Baseline Calendar");
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

   private final ProjectConfig m_config = new ProjectConfig(this);
   private final ProjectProperties m_properties = new ProjectProperties(this);
   private final ResourceContainer m_resources = new ResourceContainer(this);
   private final TaskContainer m_tasks = new TaskContainer(this);
   private final List<Task> m_childTasks = new ArrayList<>();
   private final List<Resource> m_childResources = new ArrayList<>();
   private final ResourceAssignmentContainer m_assignments = new ResourceAssignmentContainer(this);
   private final ProjectCalendarContainer m_calendars = new ProjectCalendarContainer(this);
   private final TableContainer m_tables = new TableContainer();
   private final FilterContainer m_filters = new FilterContainer();
   private final GroupContainer m_groups = new GroupContainer();
   private final SubProjectContainer m_subProjects = new SubProjectContainer();
   private final ViewContainer m_views = new ViewContainer();
   private final EventManager m_eventManager = new EventManager();
   private final CustomFieldContainer m_customFields = new CustomFieldContainer();
   private final ActivityCodeContainer m_activityCodes = new ActivityCodeContainer();
   private final DataLinkContainer m_dataLinks = new DataLinkContainer();
   private final ExpenseCategoryContainer m_expenseCategories = new ExpenseCategoryContainer(this);
   private final CostAccountContainer m_costAccounts = new CostAccountContainer(this);
   private final UserDefinedFieldContainer m_userDefinedFields = new UserDefinedFieldContainer();
   private final WorkContourContainer m_workContours = new WorkContourContainer(this);
   private final NotesTopicContainer m_notesTopics = new NotesTopicContainer(this);
   private final LocationContainer m_locations = new LocationContainer(this);
   private final ProjectFile[] m_baselines = new ProjectFile[11];
}
