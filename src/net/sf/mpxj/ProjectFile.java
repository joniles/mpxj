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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.mpp.CustomFieldValueItem;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class represents a project plan.
 */
public final class ProjectFile
{
   /**
    * Accessor method to retrieve the current file delimiter character.
    *
    * @return delimiter character
    */
   public char getDelimiter()
   {
      return (m_delimiter);
   }

   /**
    * Modifier method used to set the delimiter character.
    *
    * @param delimiter delimiter character
    */
   public void setDelimiter(char delimiter)
   {
      m_delimiter = delimiter;
   }

   /**
    * This method is provided to allow child tasks that have been created
    * programmatically to be added as a record to the main file.
    *
    * @param task task created as a child of another task
    */
   void addTask(Task task)
   {
      m_allTasks.add(task);
   }

   /**
    * This method allows a task to be added to the file programatically.
    *
    * @return new task object
    */
   public Task addTask()
   {
      Task task = new Task(this, (Task) null);
      m_allTasks.add(task);
      m_childTasks.add(task);
      return (task);
   }

   /**
    * This method is used to remove a task from the project.
    *
    * @param task task to be removed
    */
   public void removeTask(Task task)
   {
      //
      // Remove the task from the file and its parent task
      //
      m_allTasks.remove(task);
      m_taskUniqueIDMap.remove(task.getUniqueID());
      m_taskIDMap.remove(task.getID());

      Task parentTask = task.getParentTask();
      if (parentTask != null)
      {
         parentTask.removeChildTask(task);
      }
      else
      {
         m_childTasks.remove(task);
      }

      //
      // Remove all resource assignments
      //
      Iterator<ResourceAssignment> iter = m_allResourceAssignments.iterator();
      while (iter.hasNext() == true)
      {
         ResourceAssignment assignment = iter.next();
         if (assignment.getTask() == task)
         {
            Resource resource = assignment.getResource();
            if (resource != null)
            {
               resource.removeResourceAssignment(assignment);
            }
            iter.remove();
         }
      }

      //
      // Recursively remove any child tasks
      //
      while (true)
      {
         List<Task> childTaskList = task.getChildTasks();
         if (childTaskList.isEmpty() == true)
         {
            break;
         }

         removeTask(childTaskList.get(0));
      }
   }

   /**
    * This method can be called to ensure that the IDs of all
    * tasks in this project are sequential, and start from an
    * appropriate point. If tasks are added to and removed from
    * the list of tasks, then the project is loaded into Microsoft
    * project, if the ID values have gaps in the sequence, there will
    * be blank task rows shown.
    */
   public void synchronizeTaskIDs()
   {
      if (m_allTasks.isEmpty() == false)
      {
         Collections.sort(m_allTasks);
         Task firstTask = m_allTasks.get(0);
         int id = NumberUtility.getInt(firstTask.getID());
         if (id != 0)
         {
            id = 1;
         }

         for (Task task : m_allTasks)
         {
            task.setID(Integer.valueOf(id++));
         }
      }
   }

   /**
    * This method can be called to ensure that the IDs of all
    * resources in this project are sequential, and start from an
    * appropriate point. If resources are added to and removed from
    * the list of resources, then the project is loaded into Microsoft
    * project, if the ID values have gaps in the sequence, there will
    * be blank resource rows shown.
    */
   public void synchronizeResourceIDs()
   {
      if (m_allResources.isEmpty() == false)
      {
         Collections.sort(m_allResources);
         int id = 1;

         for (Resource resource : m_allResources)
         {
            resource.setID(Integer.valueOf(id++));
         }
      }
   }

   /**
    * This method is used to retrieve a list of all of the top level tasks
    * that are defined in this MPX file.
    *
    * @return list of tasks
    */
   public List<Task> getChildTasks()
   {
      return (m_childTasks);
   }

   /**
    * This method is used to retrieve a list of all of the tasks
    * that are defined in this MPX file.
    *
    * @return list of all tasks
    */
   public List<Task> getAllTasks()
   {
      return (m_allTasks);
   }

   /**
    * Used to set whether WBS numbers are automatically created.
    *
    * @param flag true if automatic WBS required.
    */
   public void setAutoWBS(boolean flag)
   {
      m_autoWBS = flag;
   }

   /**
    * Used to set whether outline level numbers are automatically created.
    *
    * @param flag true if automatic outline level required.
    */
   public void setAutoOutlineLevel(boolean flag)
   {
      m_autoOutlineLevel = flag;
   }

   /**
    * Used to set whether outline numbers are automatically created.
    *
    * @param flag true if automatic outline number required.
    */
   public void setAutoOutlineNumber(boolean flag)
   {
      m_autoOutlineNumber = flag;
   }

   /**
    * Used to set whether the task unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoTaskUniqueID(boolean flag)
   {
      m_autoTaskUniqueID = flag;
   }

   /**
    * Used to set whether the calendar unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoCalendarUniqueID(boolean flag)
   {
      m_autoCalendarUniqueID = flag;
   }

   /**
    * Used to set whether the task ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoTaskID(boolean flag)
   {
      m_autoTaskID = flag;
   }

   /**
    * Retrieve the flag that determines whether WBS is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoWBS()
   {
      return (m_autoWBS);
   }

   /**
    * Retrieve the flag that determines whether outline level is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineLevel()
   {
      return (m_autoOutlineLevel);
   }

   /**
    * Retrieve the flag that determines whether outline numbers are generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineNumber()
   {
      return (m_autoOutlineNumber);
   }

   /**
    * Retrieve the flag that determines whether the task unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskUniqueID()
   {
      return (m_autoTaskUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the calendar unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoCalendarUniqueID()
   {
      return (m_autoCalendarUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the task ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskID()
   {
      return (m_autoTaskID);
   }

   /**
    * This method is used to retrieve the next unique ID for a task.
    *
    * @return next unique ID
    */
   public int getTaskUniqueID()
   {
      return (++m_taskUniqueID);
   }

   /**
    * This method is used to retrieve the next unique ID for a calendar.
    *
    * @return next unique ID
    */
   int getCalendarUniqueID()
   {
      return (++m_calendarUniqueID);
   }

   /**
    * This method is used to retrieve the next ID for a task.
    *
    * @return next ID
    */
   public int getTaskID()
   {
      return (++m_taskID);
   }

   /**
    * Used to set whether the resource unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoResourceUniqueID(boolean flag)
   {
      m_autoResourceUniqueID = flag;
   }

   /**
    * Used to set whether the resource ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoResourceID(boolean flag)
   {
      m_autoResourceID = flag;
   }

   /**
    * Retrieve the flag that determines whether the resource unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceUniqueID()
   {
      return (m_autoResourceUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the resource ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceID()
   {
      return (m_autoResourceID);
   }

   /**
    * This method is used to retrieve the next unique ID for a resource.
    *
    * @return next unique ID
    */
   public int getResourceUniqueID()
   {
      return (++m_resourceUniqueID);
   }

   /**
    * This method is used to retrieve the next ID for a resource.
    *
    * @return next ID
    */
   public int getResourceID()
   {
      return (++m_resourceID);
   }

   /**
    * Retrieves the file creation record.
    *
    * @return file creation record.
    */
   public FileCreationRecord getFileCreationRecord()
   {
      return (m_fileCreationRecord);
   }

   /**
    * This method is provided to create a resource calendar, before it
    * has been attached to a resource.
    *
    * @return new ProjectCalendar instance
    */
   public ProjectCalendar addResourceCalendar()
   {
      ProjectCalendar calendar = new ProjectCalendar(this);
      m_resourceCalendars.add(calendar);
      return (calendar);
   }

   /**
    * This method is used to add a new base calendar to the file.
    *
    * @return new base calendar object
    */
   public ProjectCalendar addBaseCalendar()
   {
      ProjectCalendar calendar = new ProjectCalendar(this);
      m_baseCalendars.add(calendar);
      return (calendar);
   }

   /**
    * Removes a base calendar.
    *
    * @param calendar calendar to be removed
    */
   public void removeCalendar(ProjectCalendar calendar)
   {
      if (m_baseCalendars.contains(calendar))
      {
         m_baseCalendars.remove(calendar);
      }
      else
         if (m_resourceCalendars.contains(calendar))
         {
            m_resourceCalendars.remove(calendar);
            Resource resource = calendar.getResource();
            if (resource != null)
            {
               resource.setResourceCalendar(null);
            }
         }
      calendar.setBaseCalendar(null);
   }

   /**
    * This is a convenience method used to add a base calendar called
    * "Standard" to the file, and populate it with a default working week
    * and default working hours.
    *
    * @return a new default base calendar
    */
   public ProjectCalendar addDefaultBaseCalendar()
   {
      ProjectCalendar calendar = addBaseCalendar();

      calendar.setName(ProjectCalendar.DEFAULT_BASE_CALENDAR_NAME);

      calendar.setWorkingDay(Day.SUNDAY, false);
      calendar.setWorkingDay(Day.MONDAY, true);
      calendar.setWorkingDay(Day.TUESDAY, true);
      calendar.setWorkingDay(Day.WEDNESDAY, true);
      calendar.setWorkingDay(Day.THURSDAY, true);
      calendar.setWorkingDay(Day.FRIDAY, true);
      calendar.setWorkingDay(Day.SATURDAY, false);

      calendar.addDefaultCalendarHours();

      return (calendar);
   }

   /**
    * This is a protected convenience method to add a default resource
    * calendar. This is used when the calendar data is available before
    * the resource data has been read, a situation which occurs with MPP
    * files.
    *
    * @return new ProjectCalendar instance
    */
   public ProjectCalendar getDefaultResourceCalendar()
   {
      ProjectCalendar calendar = new ProjectCalendar(this);

      calendar.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);

      return (calendar);
   }

   /**
    * This method retrieves the list of base calendars defined in
    * this file.
    *
    * @return list of base calendars
    */
   public List<ProjectCalendar> getBaseCalendars()
   {
      return (m_baseCalendars);
   }

   /**
    * This method retrieves the list of resource calendars defined in
    * this file.
    *
    * @return list of resource calendars
    */
   public List<ProjectCalendar> getResourceCalendars()
   {
      return (m_resourceCalendars);
   }

   /**
    * This method is used to retrieve the project header record.
    *
    * @return project header object
    */
   public ProjectHeader getProjectHeader()
   {
      return (m_projectHeader);
   }

   /**
    * This method is used to add a new resource to the file.
    *
    * @return new resource object
    */
   public Resource addResource()
   {
      Resource resource = new Resource(this);
      m_allResources.add(resource);
      return (resource);
   }

   /**
    * This method is used to remove a resource from the project.
    *
    * @param resource resource to be removed
    */
   public void removeResource(Resource resource)
   {
      m_allResources.remove(resource);
      m_resourceUniqueIDMap.remove(resource.getUniqueID());
      m_resourceIDMap.remove(resource.getID());

      Iterator<ResourceAssignment> iter = m_allResourceAssignments.iterator();
      Integer resourceUniqueID = resource.getUniqueID();
      while (iter.hasNext() == true)
      {
         ResourceAssignment assignment = iter.next();
         if (NumberUtility.equals(assignment.getResourceUniqueID(), resourceUniqueID))
         {
            assignment.getTask().removeResourceAssignment(assignment);
            iter.remove();
         }
      }

      ProjectCalendar calendar = resource.getResourceCalendar();
      if (calendar != null)
      {
         calendar.remove();
      }
   }

   /**
    * This method is used to retrieve a list of all of the resources
    * that are defined in this MPX file.
    *
    * @return list of all resources
    */
   public List<Resource> getAllResources()
   {
      return (m_allResources);
   }

   /**
    * This method is used to retrieve a list of all of the resource assignments
    * that are defined in this MPX file.
    *
    * @return list of all resources
    */
   public List<ResourceAssignment> getAllResourceAssignments()
   {
      return (m_allResourceAssignments);
   }

   /**
    * This method is provided to allow resource assignments that have been
    * created programatically to be added as a record to the main file.
    *
    * @param assignment Resource assignment created as part of a task
    */
   void addResourceAssignment(ResourceAssignment assignment)
   {
      m_allResourceAssignments.add(assignment);
   }

   /**
    * This method removes a resource assignment from the internal storage
    * maintained by the project file.
    *
    * @param assignment resource assignment to remove
    */
   void removeResourceAssignment(ResourceAssignment assignment)
   {
      m_allResourceAssignments.remove(assignment);
      assignment.getTask().removeResourceAssignment(assignment);
      Resource resource = assignment.getResource();
      if (resource != null)
      {
         resource.removeResourceAssignment(assignment);
      }
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
    * Retrieves the named base calendar. This method will return
    * null if the named base calendar is not located.
    *
    * @param calendarName name of the required base calendar
    * @return base calendar object
    */
   public ProjectCalendar getBaseCalendar(String calendarName)
   {
      ProjectCalendar calendar = null;

      if (calendarName != null && calendarName.length() != 0)
      {
         Iterator<ProjectCalendar> iter = m_baseCalendars.iterator();
         while (iter.hasNext() == true)
         {
            calendar = iter.next();
            String name = calendar.getName();

            if ((name != null) && (name.equalsIgnoreCase(calendarName) == true))
            {
               break;
            }

            calendar = null;
         }
      }

      return (calendar);
   }

   /**
    * Retrieves the base calendar referred to by the supplied unique ID
    * value. This method will return null if the required calendar is not
    * located.
    *
    * @param calendarID calendar unique ID
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getBaseCalendarByUniqueID(Integer calendarID)
   {
      return (m_calendarUniqueIDMap.get(calendarID));
   }

   /**
    * This method is used to retrieve the number of child tasks associated
    * with this parent task. This method is used as part of the process
    * of automatically generating the WBS.
    *
    * @return Number of child tasks
    */
   int getChildTaskCount()
   {
      return (m_childTasks.size());
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
      ProjectCalendar calendar = getBaseCalendar(calendarName);

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
      return (m_taskIDMap.get(id));
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
      return (m_taskUniqueIDMap.get(id));
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
      return (m_resourceIDMap.get(id));
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
      return (m_resourceUniqueIDMap.get(id));
   }

   /**
    * This method is used to recreate the hierarchical structure of the
    * MPX file from scratch. The method sorts the list of all tasks,
    * then iterates through it creating the parent-child structure defined
    * by the outline level field.
    */
   public void updateStructure()
   {
      if (m_allTasks.size() > 1)
      {
         Collections.sort(m_allTasks);
         m_childTasks.clear();

         Task lastTask = null;
         int lastLevel = -1;

         for (Task task : m_allTasks)
         {
            task.clearChildTasks();
            Task parent = null;
            if (!task.getNull())
            {
               int level = NumberUtility.getInt(task.getOutlineLevel());

               if (lastTask != null)
               {
                  if (level == lastLevel || task.getNull())
                  {
                     parent = lastTask.getParentTask();
                     level = lastLevel;
                  }
                  else
                  {
                     if (level > lastLevel)
                     {
                        parent = lastTask;
                     }
                     else
                     {
                        while (level <= lastLevel)
                        {
                           parent = lastTask.getParentTask();

                           if (parent == null)
                           {
                              break;
                           }

                           lastLevel = NumberUtility.getInt(parent.getOutlineLevel());
                           lastTask = parent;
                        }
                     }
                  }
               }

               lastTask = task;
               lastLevel = level;

               if (getAutoWBS() || task.getWBS() == null)
               {
                  task.generateWBS(parent);
               }

               if (getAutoOutlineNumber())
               {
                  task.generateOutlineNumber(parent);
               }
            }

            if (parent == null)
            {
               m_childTasks.add(task);
            }
            else
            {
               parent.addChildTask(task);
            }
         }
      }
   }

   /**
    * This method is called to ensure that after a project file has been
    * read, the cached unique ID values used to generate new unique IDs
    * start after the end of the existing set of unique IDs.
    */
   public void updateUniqueCounters()
   {
      //
      // Update task unique IDs
      //
      for (Task task : m_allTasks)
      {
         int uniqueID = NumberUtility.getInt(task.getUniqueID());
         if (uniqueID > m_taskUniqueID)
         {
            m_taskUniqueID = uniqueID;
         }
      }

      //
      // Update resource unique IDs
      //
      for (Resource resource : m_allResources)
      {
         int uniqueID = NumberUtility.getInt(resource.getUniqueID());
         if (uniqueID > m_resourceUniqueID)
         {
            m_resourceUniqueID = uniqueID;
         }
      }

      //
      // Update base calendar unique IDs
      //
      for (ProjectCalendar calendar : m_baseCalendars)
      {
         int uniqueID = NumberUtility.getInt(calendar.getUniqueID());
         if (uniqueID > m_calendarUniqueID)
         {
            m_calendarUniqueID = uniqueID;
         }
      }

      //
      // Update resource calendar unique IDs
      //
      for (ProjectCalendar calendar : m_resourceCalendars)
      {
         int uniqueID = NumberUtility.getInt(calendar.getUniqueID());
         if (uniqueID > m_calendarUniqueID)
         {
            m_calendarUniqueID = uniqueID;
         }
      }
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

      for (Task task : m_allTasks)
      {
         //
         // If a hidden "summary" task is present we ignore it
         //
         if (NumberUtility.getInt(task.getUniqueID()) == 0)
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

      for (Task task : m_allTasks)
      {
         //
         // If a hidden "summary" task is present we ignore it
         //
         if (NumberUtility.getInt(task.getUniqueID()) == 0)
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
    * This method is called to alert project listeners to the fact that
    * a task has been read from a project file.
    *
    * @param task task instance
    */
   public void fireTaskReadEvent(Task task)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.taskRead(task);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a task has been written to a project file.
    *
    * @param task task instance
    */
   public void fireTaskWrittenEvent(Task task)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.taskWritten(task);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a resource has been read from a project file.
    *
    * @param resource resource instance
    */
   public void fireResourceReadEvent(Resource resource)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.resourceRead(resource);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a resource has been written to a project file.
    *
    * @param resource resource instance
    */
   public void fireResourceWrittenEvent(Resource resource)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.resourceWritten(resource);
         }
      }
   }

   /**
    * Adds a listener to this project file.
    *
    * @param listener listener instance
    */
   public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * Removes a listener from this project file.
    *
    * @param listener listener instance
    */
   public void removeProjectListener(ProjectListener listener)
   {
      if (m_projectListeners != null)
      {
         m_projectListeners.remove(listener);
      }
   }

   /**
    * Associates an alias with a custom task field number.
    *
    * @param field custom field number
    * @param alias alias text
    */
   public void setTaskFieldAlias(TaskField field, String alias)
   {
      if ((alias != null) && (alias.length() != 0))
      {
         m_taskFieldAlias.put(field, alias);
         m_aliasTaskField.put(alias, field);
      }
   }

   /**
    * Retrieves the alias associated with a custom task field.
    * This method will return null if no alias has been defined for
    * this field.
    *
    * @param field task field instance
    * @return alias text
    */
   public String getTaskFieldAlias(TaskField field)
   {
      return (m_taskFieldAlias.get(field));
   }

   /**
    * Retrieves a task field instance based on its alias. If the
    * alias is not recognised, this method will return null.
    *
    * @param alias alias text
    * @return task field instance
    */
   public TaskField getAliasTaskField(String alias)
   {
      return (m_aliasTaskField.get(alias));
   }

   /**
    * Associates a value list with a custom task field number.
    *
    * @param field custom field number
    * @param values values for the value list
    */
   public void setTaskFieldValueList(TaskField field, List<Object> values)
   {
      if ((values != null) && (values.size() != 0))
      {
         m_taskFieldValueList.put(field, values);
      }
   }

   /**
    * Retrieves the value list associated with a custom task field.
    * This method will return null if no value list has been defined for
    * this field.
    *
    * @param field task field instance
    * @return alias text
    */
   public List<Object> getTaskFieldValueList(TaskField field)
   {
      return m_taskFieldValueList.get(field);
   }

   /**
    * Associates a descriptions for value list with a custom task field number.
    *
    * @param field custom field number
    * @param descriptions descriptions for the value list
    */
   public void setTaskFieldDescriptionList(TaskField field, List<String> descriptions)
   {
      if ((descriptions != null) && (descriptions.size() != 0))
      {
         m_taskFieldDescriptionList.put(field, descriptions);
      }
   }

   /**
    * Retrieves the description value list associated with a custom task field.
    * This method will return null if no descriptions for the value list has been defined for
    * this field.
    *
    * @param field task field instance
    * @return alias text
    */
   public List<String> getTaskFieldDescriptionList(TaskField field)
   {
      return m_taskFieldDescriptionList.get(field);
   }

   /**
    * Associates an alias with a custom resource field number.
    *
    * @param field custom field number
    * @param alias alias text
    */
   public void setResourceFieldAlias(ResourceField field, String alias)
   {
      if ((alias != null) && (alias.length() != 0))
      {
         m_resourceFieldAlias.put(field, alias);
         m_aliasResourceField.put(alias, field);
      }
   }

   /**
    * Retrieves the alias associated with a custom resource field.
    * This method will return null if no alias has been defined for
    * this field.
    *
    * @param field field number
    * @return alias text
    */
   public String getResourceFieldAlias(ResourceField field)
   {
      return (m_resourceFieldAlias.get(field));
   }

   /**
    * Retrieves a resource field based on its alias. If the
    * alias is not recognised, this method will return null.
    *
    * @param alias alias text
    * @return resource field instance
    */
   public ResourceField getAliasResourceField(String alias)
   {
      return (m_aliasResourceField.get(alias));
   }

   /**
    * Allows derived classes to gain access to the mapping between
    * MPX task field numbers and aliases.
    *
    * @return task field to alias map
    */
   public Map<TaskField, String> getTaskFieldAliasMap()
   {
      return (m_taskFieldAlias);
   }

   /**
    * Allows derived classes to gain access to the mapping between
    * MPX resource field numbers and aliases.
    *
    * @return resource field to alias map
    */
   public Map<ResourceField, String> getResourceFieldAliasMap()
   {
      return (m_resourceFieldAlias);
   }

   /**
    * Removes an id-to-task mapping.
    *
    * @param id task unique ID
    */
   void unmapTaskUniqueID(Integer id)
   {
      m_taskUniqueIDMap.remove(id);
   }

   /**
    * Adds an id-to-task mapping.
    *
    * @param id task unique ID
    * @param task task instance
    */
   void mapTaskUniqueID(Integer id, Task task)
   {
      m_taskUniqueIDMap.put(id, task);
   }

   /**
    * Removes an id-to-task mapping.
    *
    * @param id task ID
    */
   void unmapTaskID(Integer id)
   {
      m_taskIDMap.remove(id);
   }

   /**
    * Adds an id-to-task mapping.
    *
    * @param id task ID
    * @param task task instance
    */
   void mapTaskID(Integer id, Task task)
   {
      m_taskIDMap.put(id, task);
   }

   /**
    * Removes an id-to-resource mapping.
    *
    * @param id resource unique ID
    */
   void unmapResourceUniqueID(Integer id)
   {
      m_resourceUniqueIDMap.remove(id);
   }

   /**
    * Adds an id-to-resource mapping.
    *
    * @param id resource unique ID
    * @param resource resource instance
    */
   void mapResourceUniqueID(Integer id, Resource resource)
   {
      m_resourceUniqueIDMap.put(id, resource);
   }

   /**
    * Removes an id-to-resource mapping.
    *
    * @param id resource ID
    */
   void unmapResourceID(Integer id)
   {
      m_resourceIDMap.remove(id);
   }

   /**
    * Adds an id-to-resource mapping.
    *
    * @param id resource ID
    * @param resource resource instance
    */
   void mapResourceID(Integer id, Resource resource)
   {
      m_resourceIDMap.put(id, resource);
   }

   /**
    * Removes an id-to-calendar mapping.
    *
    * @param id calendar unique ID
    */
   void unmapCalendarUniqueID(Integer id)
   {
      m_calendarUniqueIDMap.remove(id);
   }

   /**
    * Adds an id-to-calendar mapping.
    *
    * @param id calendar unique ID
    * @param calendar calendar instance
    */
   void mapCalendarUniqueID(Integer id, ProjectCalendar calendar)
   {
      m_calendarUniqueIDMap.put(id, calendar);
   }

   /**
    * This method retrieves a value representing the type of MPP file
    * that has been read. Currently this method will return the value 8 for
    * an MPP8 file (Project 98), 9 for an MPP9 file (Project 2000 and
    * Project 2002) and 12 for an MPP12 file (Project 12).
    *
    * @return integer representing the file type
    */
   public int getMppFileType()
   {
      return (m_mppFileType);
   }

   /**
    * Used internally to set the file type.
    *
    * @param fileType file type
    */
   public void setMppFileType(int fileType)
   {
      m_mppFileType = fileType;
   }

   /**
    * Package-private method used to add views to this MPP file.
    *
    * @param view view data
    */
   public void addView(View view)
   {
      m_views.add(view);
   }

   /**
    * This method returns a list of the views defined in this MPP file.
    *
    * @return list of views
    */
   public List<View> getViews()
   {
      return (m_views);
   }

   /**
    * Package-private method used to add tables to this MPP file.
    *
    * @param table table data
    */
   public void addTable(Table table)
   {
      m_tables.add(table);
      if (table.getResourceFlag() == false)
      {
         m_taskTablesByName.put(table.getName(), table);
      }
      else
      {
         m_resourceTablesByName.put(table.getName(), table);
      }
   }

   /**
    * This method returns a list of the tables defined in this MPP file.
    *
    * @return list of tables
    */
   public List<Table> getTables()
   {
      return (m_tables);
   }

   /**
    * Adds a filter definition to this project file.
    * 
    * @param filter filter definition
    */
   public void addFilter(Filter filter)
   {
      if (filter.isTaskFilter())
      {
         m_taskFilters.add(filter);
      }

      if (filter.isResourceFilter())
      {
         m_resourceFilters.add(filter);
      }

      m_filtersByName.put(filter.getName(), filter);
      m_filtersByID.put(filter.getID(), filter);
   }

   /**
    * Removes a filter from this project file.
    *
    * @param filterName The name of the filter
    */
   public void removeFilter(String filterName)
   {
      Filter filter = getFilterByName(filterName);
      if (filter != null)
      {
         if (filter.isTaskFilter())
         {
            m_taskFilters.remove(filter);
         }

         if (filter.isResourceFilter())
         {
            m_resourceFilters.remove(filter);
         }
         m_filtersByName.remove(filterName);
         m_filtersByID.remove(filter.getID());
      }
   }

   /**
    * Retrieves a list of all resource filters.
    * 
    * @return list of all resource filters
    */
   public List<Filter> getAllResourceFilters()
   {
      return (m_resourceFilters);
   }

   /**
    * Retrieves a list of all task filters.
    * 
    * @return list of all task filters
    */
   public List<Filter> getAllTaskFilters()
   {
      return (m_taskFilters);
   }

   /**
    * Retrieve a given filter by name.
    * 
    * @param name filter name
    * @return filter instance
    */
   public Filter getFilterByName(String name)
   {
      return (m_filtersByName.get(name));
   }

   /**
    * Retrieve a given filter by ID.
    * 
    * @param id filter ID
    * @return filter instance
    */
   public Filter getFilterByID(Integer id)
   {
      return (m_filtersByID.get(id));
   }

   /**
    * Retrieves a list of all groups.
    * 
    * @return list of all groups
    */
   public List<Group> getAllGroups()
   {
      return (m_groups);
   }

   /**
    * Retrieve a given group by name.
    * 
    * @param name group name
    * @return Group instance
    */
   public Group getGroupByName(String name)
   {
      return (m_groupsByName.get(name));
   }

   /**
    * Adds a group definition to this project file.
    * 
    * @param group group definition
    */
   public void addGroup(Group group)
   {
      m_groups.add(group);
      m_groupsByName.put(group.getName(), group);
   }

   /**
    * Adds the definition of a graphical indicator for a field type.
    * 
    * @param field field type
    * @param indicator graphical indicator definition
    */
   public void addGraphicalIndicator(FieldType field, GraphicalIndicator indicator)
   {
      m_graphicalIndicators.put(field, indicator);
   }

   /**
    * Retrieves the definition of any graphical indicators used for the
    * given field type.
    * 
    * @param field field type
    * @return graphical indicator definition
    */
   public GraphicalIndicator getGraphicalIndicator(FieldType field)
   {
      return (m_graphicalIndicators.get(field));
   }

   /**
    * Utility method to retrieve the definition of a task table by name.
    * This method will return null if the table name is not recognised.
    *
    * @param name table name
    * @return table instance
    */
   public Table getTaskTableByName(String name)
   {
      return (m_taskTablesByName.get(name));
   }

   /**
    * Utility method to retrieve the definition of a resource table by name.
    * This method will return null if the table name is not recognised.
    *
    * @param name table name
    * @return table instance
    */
   public Table getResourceTableByName(String name)
   {
      return (m_resourceTablesByName.get(name));
   }

   /**
    * This package-private method is used to add resource sub project details.
    *
    * @param project sub project
    */
   public void setResourceSubProject(SubProject project)
   {
      m_resourceSubProject = project;
   }

   /**
    * Retrieves details of the sub project file used as a resource pool.
    *
    * @return sub project details
    */
   public SubProject getResourceSubProject()
   {
      return (m_resourceSubProject);
   }

   /**
    * This package-private method is used to add sub project details.
    *
    * @param project sub project
    */
   public void addSubProject(SubProject project)
   {
      m_allSubProjects.add(project);
   }

   /**
    * Retrieves all the subprojects for this MPX file.
    *
    * @return all sub project details
    */
   public List<SubProject> getAllSubProjects()
   {
      return (m_allSubProjects);
   }

   /**
    * Retrieve a flag indicating if auto filter is enabled.
    * 
    * @return auto filter flag
    */
   public boolean getAutoFilter()
   {
      return (m_autoFilter);
   }

   /**
    * Sets a flag indicating if auto filter is enabled.
    * 
    * @param autoFilter boolean flag
    */
   public void setAutoFilter(boolean autoFilter)
   {
      m_autoFilter = autoFilter;
   }

   /**
    * Set the saved view state associated with this file.
    * 
    * @param viewState view state
    */
   public void setViewState(ViewState viewState)
   {
      m_viewState = viewState;
   }

   /**
    * Retrieve the saved view state associated with this file.
    * 
    * @return view state
    */
   public ViewState getViewState()
   {
      return (m_viewState);
   }

   /**
    * Set whether the data in this file is encoded.
    * 
    * @param encoded True if the data is encoded in the file
    */
   public void setEncoded(boolean encoded)
   {
      m_encoded = encoded;
   }

   /**
    * Get whether the data in this file is encoded.
    * 
    * @return encoded
    */
   public boolean getEncoded()
   {
      return (m_encoded);
   }

   /**
    * Set the key with which this data is encrypted (can be decrypted) with.
    * 
    * @param encryptionKey Encryption key
    */
   public void setEncryptionCode(byte encryptionKey)
   {
      if (encryptionKey != 0x00)
      {
         m_encryptionKey = (byte) (0xFF - encryptionKey);
      }
      else
      {
         m_encryptionKey = (byte) 0x00;
      }
   }

   /**
    * Get the key with which this data is encrypted (can be decrypted) with.
    * 
    * @return m_encryptionKey
    */
   public byte getEncryptionCode()
   {
      return (m_encryptionKey);
   }

   /**
    * Sets the project file path.
    *
    * @param projectFilePath project file path
    */
   public void setProjectFilePath(String projectFilePath)
   {
      m_projectFilePath = projectFilePath;
   }

   /**
    * Gets the project file path.
    *
    * @return project file path
    */
   public String getProjectFilePath()
   {
      return (m_projectFilePath);
   }

   /**
    * Add a custom field value list item.
    * 
    * @param item CustomFieldValueItem instance
    */
   public void addCustomFieldValueItem(CustomFieldValueItem item)
   {
      m_customFieldValueItems.put(item.getUniqueID(), item);
   }

   /**
    * Get the custom field value list item with the given unique ID.
    * 
    * @param uniqueID unique ID
    * @return CustomFieldValueItem instance
    */
   public CustomFieldValueItem getCustomFieldValueItem(Integer uniqueID)
   {
      return m_customFieldValueItems.get(uniqueID);
   }

   /**
    * Retrieves the default calendar for this project.
    * 
    * @return default projectCalendar instance
    */
   public ProjectCalendar getCalendar()
   {
      String calendarName = m_projectHeader.getCalendarName();
      ProjectCalendar calendar = getBaseCalendar(calendarName);
      return calendar;
   }

   /**
    * Sets the default calendar for this project.
    * 
    * @param calendar default calendar instance
    */
   public void setCalendar(ProjectCalendar calendar)
   {
      m_projectHeader.setCalendarName(calendar.getName());
   }

   private String m_projectFilePath;

   /**
    * Counter used to populate the unique ID field of a task.
    */
   private int m_taskUniqueID;

   /**
    * Counter used to populate the unique ID field of a calendar.
    */
   private int m_calendarUniqueID;

   /**
    * Counter used to populate the ID field of a task.
    */
   private int m_taskID;

   /**
    * Counter used to populate the unique ID field of a resource.
    */
   private int m_resourceUniqueID;

   /**
    * Counter used to populate the ID field of a resource.
    */
   private int m_resourceID;

   /**
    * This list holds a reference to all resources defined in the
    * MPX file.
    */
   private List<Resource> m_allResources = new LinkedList<Resource>();

   /**
    * This list holds a reference to all tasks defined in the
    * MPX file.
    */
   private List<Task> m_allTasks = new LinkedList<Task>();

   /**
    * List holding references to the top level tasks
    * as defined by the outline level.
    */
   private List<Task> m_childTasks = new LinkedList<Task>();

   /**
    * This list holds a reference to all resource assignments defined in the
    * MPX file.
    */
   private List<ResourceAssignment> m_allResourceAssignments = new LinkedList<ResourceAssignment>();

   /**
    * List holding references to all base calendars.
    */
   private List<ProjectCalendar> m_baseCalendars = new LinkedList<ProjectCalendar>();

   /**
    * List holding references to all resource calendars.
    */
   private List<ProjectCalendar> m_resourceCalendars = new LinkedList<ProjectCalendar>();

   /**
    * File creation record.
    */
   private FileCreationRecord m_fileCreationRecord = new FileCreationRecord(this);

   /**
    * Project header record.
    */
   private ProjectHeader m_projectHeader = new ProjectHeader(this);

   /**
    * Character to be used as delimiter throughout this file.
    */
   private char m_delimiter = ',';

   /**
    * Key with which this data is encrypted (can be decrypted) with.
    */
   private byte m_encryptionKey;

   /**
    * Indicating whether the project data is encoded due to password protection.
    */
   private boolean m_encoded;

   /**
    * Indicating whether WBS value should be calculated on creation, or will
    * be manually set.
    */
   private boolean m_autoWBS = true;

   /**
    * Indicating whether the Outline Level value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineLevel = true;

   /**
    * Indicating whether the Outline Number value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineNumber = true;

   /**
    * Indicating whether the unique ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskUniqueID = true;

   /**
    * Indicating whether the unique ID of a calendar should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoCalendarUniqueID = true;

   /**
    * Indicating whether the ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskID = true;

   /**
    * Indicating whether the unique ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceUniqueID = true;

   /**
    * Indicating whether the ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceID = true;

   /**
    * Maps from a task field number to a task alias.
    */
   private Map<TaskField, String> m_taskFieldAlias = new HashMap<TaskField, String>();

   /**
    * Maps from a task field number to a value list.
    */
   private Map<TaskField, List<Object>> m_taskFieldValueList = new HashMap<TaskField, List<Object>>();

   /**
    * Maps from a task field number to a description list.
    */
   private Map<TaskField, List<String>> m_taskFieldDescriptionList = new HashMap<TaskField, List<String>>();

   /**
    * Maps from a task field alias to a task field number.
    */
   private Map<String, TaskField> m_aliasTaskField = new HashMap<String, TaskField>();

   /**
    * Maps from a resource field number to a resource alias.
    */
   private Map<ResourceField, String> m_resourceFieldAlias = new HashMap<ResourceField, String>();

   /**
    * Maps from a resource field alias to a resource field number.
    */
   private Map<String, ResourceField> m_aliasResourceField = new HashMap<String, ResourceField>();

   /**
    * Maps from a task unique ID to a task instance.
    */
   private Map<Integer, Task> m_taskUniqueIDMap = new HashMap<Integer, Task>();

   /**
    * Maps from a task ID to a task instance.
    */
   private Map<Integer, Task> m_taskIDMap = new HashMap<Integer, Task>();

   /**
    * Maps from a resource unique ID to a resource instance.
    */
   private Map<Integer, Resource> m_resourceUniqueIDMap = new HashMap<Integer, Resource>();

   /**
    * Maps from a resource ID to a resource instance.
    */
   private Map<Integer, Resource> m_resourceIDMap = new HashMap<Integer, Resource>();

   /**
    * Maps from a calendar unique ID to a calendar instance.
    */
   private Map<Integer, ProjectCalendar> m_calendarUniqueIDMap = new HashMap<Integer, ProjectCalendar>();

   /**
    * List of project event listeners.
    */
   private List<ProjectListener> m_projectListeners;

   /**
    * This value is used to represent the type of MPP file that
    * has been read.
    */
   private int m_mppFileType;

   /**
    * List of views defined in this file.
    */
   private List<View> m_views = new ArrayList<View>();

   /**
    * List of tables defined in this file.
    */
   private List<Table> m_tables = new ArrayList<Table>();

   /**
    * Map of graphical indicator data.
    */
   private Map<FieldType, GraphicalIndicator> m_graphicalIndicators = new HashMap<FieldType, GraphicalIndicator>();

   /**
    * Index of task tables by name.
    */
   private Map<String, Table> m_taskTablesByName = new HashMap<String, Table>();

   /**
    * Index of resource tables by name.
    */
   private Map<String, Table> m_resourceTablesByName = new HashMap<String, Table>();

   /**
    * List of all task filters.
    */
   private List<Filter> m_taskFilters = new ArrayList<Filter>();

   /**
    * List of all resource filters.
    */
   private List<Filter> m_resourceFilters = new ArrayList<Filter>();

   /**
    * Index of filters by name.
    */
   private Map<String, Filter> m_filtersByName = new HashMap<String, Filter>();

   /**
    * Index of filters by ID.
    */
   private Map<Integer, Filter> m_filtersByID = new HashMap<Integer, Filter>();

   /**
    * List of all groups.
    */
   private List<Group> m_groups = new ArrayList<Group>();

   /**
    * Index of groups by name.
    */
   private Map<String, Group> m_groupsByName = new HashMap<String, Group>();

   /**
    * Resource sub project.
    */
   private SubProject m_resourceSubProject;

   /**
    * This list holds a reference to all subprojects defined in the
    * MPX file.
    */
   private List<SubProject> m_allSubProjects = new LinkedList<SubProject>();

   /**
    * Flag indicating if auto filter is enabled.
    */
   private boolean m_autoFilter;

   /**
    * Saved view state.
    */
   private ViewState m_viewState;

   /***
    * Custom field value list items.
    */
   private Map<Integer, CustomFieldValueItem> m_customFieldValueItems = new HashMap<Integer, CustomFieldValueItem>();
}
