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
import java.util.Date;
import java.util.List;

import net.sf.mpxj.common.NumberHelper;

/**
 * This class represents a project plan.
 */
public final class ProjectFile implements ChildTaskContainer
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
      m_tasks.validateUniqueIDsForMicrosoftProject();
      m_resources.validateUniqueIDsForMicrosoftProject();
      m_assignments.validateUniqueIDsForMicrosoftProject();
      m_calendars.validateUniqueIDsForMicrosoftProject();
   }

   /**
    * This method is used to retrieve a list of all of the top level tasks
    * that are defined in this project file.
    *
    * @return list of tasks
    */
   @Override public List<Task> getChildTasks()
   {
      return m_childTasks;
   }

   /**
    * This method is used to retrieve a list of all of the tasks
    * that are defined in this project file.
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
   public Resource addResource()
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
   }

   /**
    * Find the earliest task start date. We treat this as the
    * start date for the project.
    *
    * @return start date
    */
   public Date getStartDate()
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
         if (task.getMilestone() == true)
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
    * Find the latest task finish date. We treat this as the
    * finish date for the project.
    *
    * @return finish date
    */
   public Date getFinishDate()
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
    * @return all sub project details
    */
   public SubProjectContainer getSubProjects()
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
    * Retrieves the custom field configuration for this project.
    *
    * @return custom field configuration
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
    * Retrieves the default calendar for this project based on the calendar name
    * given in the project properties. If a calendar of this name cannot be found, then
    * the first calendar listed for the project will be returned. If the
    * project contains no calendars, then a default calendar is added.
    *
    * @return default projectCalendar instance
    */
   public ProjectCalendar getDefaultCalendar()
   {
      String calendarName = m_properties.getDefaultCalendarName();
      ProjectCalendar calendar = getCalendarByName(calendarName);
      if (calendar == null)
      {
         if (m_calendars.isEmpty())
         {
            calendar = addDefaultBaseCalendar();
         }
         else
         {
            calendar = m_calendars.get(0);
         }
      }
      return calendar;
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
         m_properties.setDefaultCalendarName(calendar.getName());
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

   private final ProjectConfig m_config = new ProjectConfig(this);
   private final ProjectProperties m_properties = new ProjectProperties(this);
   private final ResourceContainer m_resources = new ResourceContainer(this);
   private final TaskContainer m_tasks = new TaskContainer(this);
   private final List<Task> m_childTasks = new ArrayList<>();
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
}
