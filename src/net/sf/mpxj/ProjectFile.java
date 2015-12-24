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

import java.util.Date;
import java.util.LinkedList;
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
    * This method allows a task to be added to the file programatically.
    *
    * @return new task object
    */
   public Task addTask()
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
    * This method can be called to ensure that the IDs of all
    * tasks in this project are sequential, and start from an
    * appropriate point. If tasks are added to and removed from
    * the list of tasks, then the project is loaded into Microsoft
    * project, if the ID values have gaps in the sequence, there will
    * be blank task rows shown.
    */
   public void renumberTaskIDs()
   {
      m_tasks.renumberIDs();
   }

   /**
    * This method can be called to ensure that the IDs of all
    * resources in this project are sequential, and start from an
    * appropriate point. If resources are added to and removed from
    * the list of resources, then the project is loaded into Microsoft
    * project, if the ID values have gaps in the sequence, there will
    * be blank resource rows shown.
    */
   public void renumberResourceIDs()
   {
      m_resources.renumberIDs();
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
    * Microsoft Project bases the order of tasks displayed on their ID
    * value. This method takes the hierarchical structure of tasks
    * represented in MPXJ and renumbers the ID values to ensure that
    * this structure is displayed as expected in Microsoft Project. This
    * is typically used to deal with the case where a hierarchical task
    * structure has been created programmatically in MPXJ.
    */
   public void synchronizeTaskIDToHierarchy()
   {
      m_tasks.synchronizeTaskIDToHierarchy();
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
   public TaskContainer getAllTasks()
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
   public ResourceContainer getAllResources()
   {
      return m_resources;
   }

   /**
    * Retrieves a list of all resource assignments in this project.
    *
    * @return list of all resources
    */
   public ResourceAssignmentContainer getAllResourceAssignments()
   {
      return m_assignments;
   }

   /**
    * This method has been provided to allow the subclasses to
    * instantiate ResourecAssignment instances.
    *
    * @param task parent task
    * @return new resource assignment instance
    */
   public ResourceAssignment newResourceAssignment(Task task)
   {
      return (new ResourceAssignment(this, task));
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
    * This method is used to calculate the duration of work between two fixed
    * dates according to the work schedule defined in the named calendar. The
    * calendar used is the "Standard" calendar. If this calendar does not exist,
    * and exception will be thrown.
    *
    * @param startDate start of the period
    * @param endDate end of the period
    * @return new Duration object
    * @throws MPXJException normally when no Standard calendar is available
    */
   public Duration getDuration(Date startDate, Date endDate) throws MPXJException
   {
      return (getDuration("Standard", startDate, endDate));
   }

   /**
    * This method is used to calculate the duration of work between two fixed
    * dates according to the work schedule defined in the named calendar.
    * The name of the calendar to be used is passed as an argument.
    *
    * @param calendarName name of the calendar to use
    * @param startDate start of the period
    * @param endDate end of the period
    * @return new Duration object
    * @throws MPXJException normally when no Standard calendar is available
    */
   public Duration getDuration(String calendarName, Date startDate, Date endDate) throws MPXJException
   {
      ProjectCalendar calendar = getCalendarByName(calendarName);

      if (calendar == null)
      {
         throw new MPXJException(MPXJException.CALENDAR_ERROR + ": " + calendarName);
      }

      return (calendar.getDuration(startDate, endDate));
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
      m_properties.setDefaultCalendarName(calendar.getName());
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
   private final List<Task> m_childTasks = new LinkedList<Task>();
   private final ResourceAssignmentContainer m_assignments = new ResourceAssignmentContainer(this);
   private final ProjectCalendarContainer m_calendars = new ProjectCalendarContainer(this);
   private final TableContainer m_tables = new TableContainer();
   private final FilterContainer m_filters = new FilterContainer();
   private final GroupContainer m_groups = new GroupContainer();
   private final SubProjectContainer m_subProjects = new SubProjectContainer();
   private final ViewContainer m_views = new ViewContainer();
   private final EventManager m_eventManager = new EventManager();
   private final CustomFieldContainer m_customFields = new CustomFieldContainer();
}
