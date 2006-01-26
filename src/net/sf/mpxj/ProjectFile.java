/*
 * file:       ProjectFile.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2006
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
   public char getDelimiter ()
   {
      return (m_delimiter);
   }

   /**
    * Modifier method used to set the delimiter character.
    *
    * @param delimiter delimiter character
    */
   public void setDelimiter (char delimiter)
   {
      m_delimiter = delimiter;
   }


   /**
    * This method post-processes tasks and resources read from an MPX
    * file to ensure that they all have valid unique ID fields. This is
    * designed to cope with poorly formed MPX files where tasks and resources
    * have ID values, but not unique ID values.
    */
   public void updateUniqueIdentifiers ()
   {
      Iterator iter = m_allTasks.iterator();
      Task task;
      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         if (task.getUniqueID() == null)
         {
            task.setUniqueID(task.getID());
         }
      }
      
      iter = m_allResources.iterator();
      Resource resource;
      while (iter.hasNext() == true)
      {
         resource = (Resource)iter.next();
         if (resource.getUniqueID() == null)
         {
            resource.setUniqueID(resource.getID());
         }
      }
   }
   
   /**
    * This method is provided to allow child tasks that have been created
    * programatically to be added as a record to the main file.
    *
    * @param task task created as a child of another task
    */
   void addTask (Task task)
   {
      m_allTasks.add(task);
   }

   /**
    * This method allows a task to be added to the file programatically.
    *
    * @return new task object
    */
   public Task addTask ()
   {
      Task task = new Task(this, (Task)null);
      m_allTasks.add(task);
      m_childTasks.add(task);
      return (task);
   }

   /**
    * This method is used to remove a task from the project.
    * 
    * @param task task to be removed
    */
   public void removeTask (Task task)
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
         parentTask.removeChildTask (task);
      }
      else
      {
         m_childTasks.remove(task);
      }

      //
      // Remove all resource assignments
      //
      Iterator iter = m_allResourceAssignments.iterator();
      ResourceAssignment assignment;
      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();
         if (assignment.getTask() == task)
         {
            assignment.getResource().removeResourceAssignment(assignment);
            iter.remove();
         }
      }
      
      //
      // Recursively remove any child tasks
      //
      while (true)
      {
         List childTaskList = task.getChildTasks();
         if (childTaskList.isEmpty() == true)
         {
            break;
         }
         
         removeTask((Task)childTaskList.get(0)); 
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
   public void synchronizeTaskIDs ()
   {
      if (m_allTasks.isEmpty() == false)
      {
         Collections.sort(m_allTasks);
         Task firstTask = (Task)m_allTasks.get(0);
         int id = NumberUtility.getInt(firstTask.getID());
         if (id != 0)
         {
            id = 1;
         }
         
         Iterator iter = m_allTasks.iterator();
         while (iter.hasNext() == true)
         {
            ((Task)iter.next()).setID(new Integer(id++));
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
   public void synchronizeResourceIDs ()
   {
      if (m_allResources.isEmpty() == false)
      {
         Collections.sort(m_allResources);
         int id = 1;
         
         Iterator iter = m_allResources.iterator();
         while (iter.hasNext() == true)
         {
            Resource resource = (Resource)iter.next();
            resource.setID(new Integer(id++));
         }
      }      
   }
   
   /**
    * This method is used to retrieve a list of all of the top level tasks
    * that are defined in this MPX file.
    *
    * @return list of tasks
    */
   public List getChildTasks ()
   {
      return (m_childTasks);
   }

   /**
    * This method is used to retrieve a list of all of the tasks
    * that are defined in this MPX file.
    *
    * @return list of all tasks
    */
   public List getAllTasks ()
   {
      return (m_allTasks);
   }

   /**
    * Used to set whether WBS numbers are automatically created.
    *
    * @param flag true if automatic WBS required.
    */
   public void setAutoWBS (boolean flag)
   {
      m_autoWBS = flag;
   }

   /**
    * Used to set whether outline level numbers are automatically created.
    *
    * @param flag true if automatic outline level required.
    */
   public void setAutoOutlineLevel (boolean flag)
   {
      m_autoOutlineLevel = flag;
   }

   /**
    * Used to set whether outline numbers are automatically created.
    *
    * @param flag true if automatic outline number required.
    */
   public void setAutoOutlineNumber (boolean flag)
   {
      m_autoOutlineNumber = flag;
   }

   /**
    * Used to set whether the task unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoTaskUniqueID (boolean flag)
   {
      m_autoTaskUniqueID = flag;
   }

   /**
    * Used to set whether the calendar unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoCalendarUniqueID (boolean flag)
   {
      m_autoCalendarUniqueID = flag;
   }

   /**
    * Used to set whether the task ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoTaskID (boolean flag)
   {
      m_autoTaskID = flag;
   }

   /**
    * Retrieve the flag that determines whether WBS is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoWBS ()
   {
      return (m_autoWBS);
   }

   /**
    * Retrieve the flag that determines whether outline level is generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineLevel ()
   {
      return (m_autoOutlineLevel);
   }

   /**
    * Retrieve the flag that determines whether outline numbers are generated
    * automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoOutlineNumber ()
   {
      return (m_autoOutlineNumber);
   }

   /**
    * Retrieve the flag that determines whether the task unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskUniqueID ()
   {
      return (m_autoTaskUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the calendar unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoCalendarUniqueID ()
   {
      return (m_autoCalendarUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the task ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoTaskID ()
   {
      return (m_autoTaskID);
   }

   /**
    * This method is used to retrieve the next unique ID for a task.
    *
    * @return next unique ID
    */
   public int getTaskUniqueID ()
   {
      return (++m_taskUniqueID);
   }

   /**
    * This method is used to retrieve the next unique ID for a calendar.
    *
    * @return next unique ID
    */
   int getCalendarUniqueID ()
   {
      return (++m_calendarUniqueID);
   }

   /**
    * This method is used to retrieve the next ID for a task.
    *
    * @return next ID
    */
   public int getTaskID ()
   {
      return (++m_taskID);
   }

   /**
    * Used to set whether the resource unique ID field is automatically populated.
    *
    * @param flag true if automatic unique ID required.
    */
   public void setAutoResourceUniqueID (boolean flag)
   {
      m_autoResourceUniqueID = flag;
   }

   /**
    * Used to set whether the resource ID field is automatically populated.
    *
    * @param flag true if automatic ID required.
    */
   public void setAutoResourceID (boolean flag)
   {
      m_autoResourceID = flag;
   }

   /**
    * Retrieve the flag that determines whether the resource unique ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceUniqueID ()
   {
      return (m_autoResourceUniqueID);
   }

   /**
    * Retrieve the flag that determines whether the resource ID
    * is generated automatically.
    *
    * @return boolean, default is false.
    */
   public boolean getAutoResourceID ()
   {
      return (m_autoResourceID);
   }

   /**
    * This method is used to retrieve the next unique ID for a resource.
    *
    * @return next unique ID
    */
   public int getResourceUniqueID ()
   {
      return (++m_resourceUniqueID);
   }

   /**
    * This method is used to retrieve the next ID for a resource.
    *
    * @return next ID
    */
   public int getResourceID ()
   {
      return (++m_resourceID);
   }

   /**
    * Retrieves the file creation record.
    *
    * @return file creation record.
    */
   public FileCreationRecord getFileCreationRecord ()
   {
      return (m_fileCreationRecord);
   }

   /**
    * This method is provided to create a resource calendar, before it
    * has been attached to a resource.
    *
    * @return new ProjectCalendar instance
    */
   public ProjectCalendar getResourceCalendar ()
   {
      ProjectCalendar calendar = new ProjectCalendar(this, false);
      m_resourceCalendars.add(calendar);
      return (calendar);
   }

   /**
    * This method is used to add a new base calendar to the file.
    *
    * @return new base calendar object
    */
   public ProjectCalendar addBaseCalendar ()
   {
      ProjectCalendar calendar = new ProjectCalendar(this, true);
      m_baseCalendars.add(calendar);      
      return (calendar);
   }

   /**
    * Removes a base calendar.
    * 
    * @param calendar calendar to be removed
    */
   public void removeCalendar (ProjectCalendar calendar)
   {
      Resource resource = calendar.getResource();
      if (resource == null)
      {
         m_baseCalendars.remove(calendar);         
      }
      else
      {
         m_resourceCalendars.remove(calendar);         
         resource.setResourceCalendar(null);
      }
   }
   
   /**
    * This is a convenience method used to add a base calendar called
    * "Standard" to the file, and populate it with a default working week
    * and default working hours.
    *
    * @return a new default base calendar
    * @throws MPXJException normally thrown when a parse error occurs
    */
   public ProjectCalendar addDefaultBaseCalendar ()
      throws MPXJException
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
   public ProjectCalendar getDefaultResourceCalendar ()
   {
      ProjectCalendar calendar = new ProjectCalendar(this, false);

      calendar.setWorkingDay(Day.SUNDAY, ProjectCalendar.DEFAULT);
      calendar.setWorkingDay(Day.MONDAY, ProjectCalendar.DEFAULT);
      calendar.setWorkingDay(Day.TUESDAY, ProjectCalendar.DEFAULT);
      calendar.setWorkingDay(Day.WEDNESDAY, ProjectCalendar.DEFAULT);
      calendar.setWorkingDay(Day.THURSDAY, ProjectCalendar.DEFAULT);
      calendar.setWorkingDay(Day.FRIDAY, ProjectCalendar.DEFAULT);
      calendar.setWorkingDay(Day.SATURDAY, ProjectCalendar.DEFAULT);

      return (calendar);
   }

   /**
    * This method retrieves the list of base calendars defined in
    * this file.
    *
    * @return list of base calendars
    */
   public List getBaseCalendars ()
   {
      return (m_baseCalendars);
   }

   /**
    * This method retrieves the list of resource calendars defined in
    * this file.
    *
    * @return list of resource calendars
    */
   public List getResourceCalendars ()
   {
      return (m_resourceCalendars);
   }
   
   /**
    * This method is used to retrieve the project header record.
    *
    * @return project header object
    */
   public ProjectHeader getProjectHeader ()
   {
      return (m_projectHeader);
   }

   /**
    * This method is used to add a new resource to the file.
    *
    * @return new resource object
    */
   public Resource addResource ()
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
   public void removeResource (Resource resource)
   {
      m_allResources.remove(resource);
      m_resourceUniqueIDMap.remove(resource.getUniqueID());
      m_resourceIDMap.remove(resource.getID());
      
      Iterator iter = m_allResourceAssignments.iterator();
      ResourceAssignment assignment;
      Integer resourceUniqueID = resource.getUniqueID();
      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();
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
   public List getAllResources ()
   {
      return (m_allResources);
   }

   /**
    * This method is used to retrieve a list of all of the resource assignments
    * that are defined in this MPX file.
    *
    * @return list of all resources
    */
   public List getAllResourceAssignments ()
   {
      return (m_allResourceAssignments);
   }

   /**
    * This method is provided to allow resource assignments that have been 
    * created programatically to be added as a record to the main file.
    *
    * @param assignment Resource assignment created as part of a task
    */
   void addResourceAssignment (ResourceAssignment assignment)
   {
      m_allResourceAssignments.add(assignment);
   }

   /**
    * This method removes a resource assignment from the internal storage
    * maintained by the project file.
    * 
    * @param assignment resource assignment to remove
    */
   void removeResourceAssignment (ResourceAssignment assignment)
   {
      m_allResourceAssignments.remove(assignment);
      assignment.getTask().removeResourceAssignment(assignment);
      assignment.getResource().removeResourceAssignment(assignment);
   }
   
   /**
    * This method has been provided to allow the subclasses to
    * instantiate ResourecAssignment instances.
    * 
    * @param task parent task
    * @return new resource assignment instance
    */
   public ResourceAssignment newResourceAssignment (Task task)
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
   public ProjectCalendar getBaseCalendar (String calendarName)
   {
      ProjectCalendar calendar = null;
      
      if (calendarName != null && calendarName.length() != 0)
      {
         String name;
         Iterator iter = m_baseCalendars.iterator();
   
         while (iter.hasNext() == true)
         {
            calendar = (ProjectCalendar)iter.next();
            name = calendar.getName();
   
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
   public ProjectCalendar getBaseCalendarByUniqueID (int calendarID)
   {
      ProjectCalendar calendar = null;
      Iterator iter = m_baseCalendars.iterator();

      while (iter.hasNext() == true)
      {
         calendar = (ProjectCalendar)iter.next();

         if (calendar.getUniqueID() == calendarID)
         {
            break;
         }

         calendar = null;
      }

      return (calendar);
   }

   /**
    * This method is used to retrieve the number of child tasks associated
    * with this parent task. This method is used as part of the process
    * of automatically generating the WBS.
    *
    * @return Number of child tasks
    */
   int getChildTaskCount ()
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
   public Duration getDuration (Date startDate, Date endDate)
      throws MPXJException
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
   public Duration getDuration (String calendarName, Date startDate, Date endDate)
      throws MPXJException
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
   public Task getTaskByID (Integer id)
   {
      return ((Task)m_taskIDMap.get(id));
   }
   
   /**
    * This method allows an arbitrary task to be retrieved based
    * on its UniqueID field.
    *
    * @param id task identified
    * @return the requested task, or null if not found
    */
   public Task getTaskByUniqueID (Integer id)
   {
      return ((Task)m_taskUniqueIDMap.get(id));
   }
   
   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its ID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByID (Integer id)
   {
      return ((Resource)m_resourceIDMap.get(id));
   }

   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its UniqueID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByUniqueID (Integer id)
   {
      return ((Resource)m_resourceUniqueIDMap.get(id));
   }

   /**
    * This method is used to recreate the hierarchical structure of the
    * MPX file from scratch. The method sorts the list of all tasks,
    * then iterates through it creating the parent-child structure defined
    * by the outline level field.
    */
   public void updateStructure ()
   {
      if (m_allTasks.size() > 1)
      {
         Collections.sort(m_allTasks);
         m_childTasks.clear();

         Task task;
         Task lastTask = null;
         Task parent;
         int level;
         int lastLevel = -1;

         Iterator iter = m_allTasks.iterator();

         while (iter.hasNext() == true)
         {
            task = (Task)iter.next();
            task.clearChildTasks();
            level = NumberUtility.getInt(task.getOutlineLevel());
            parent = null;

            if (lastTask != null)
            {
               if (level == lastLevel)
               {
                  parent = lastTask.getParentTask();
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

            if (getAutoWBS() == true)
            {
               task.generateWBS(parent);
            }

            if (getAutoOutlineNumber() == true)
            {
               task.generateOutlineNumber(parent);
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
   public void updateUniqueCounters ()
   {
      //
      // Update task unique IDs
      //
      for(Iterator iter=m_allTasks.iterator(); iter.hasNext();)
      {
         Task task = (Task)iter.next();
         int uniqueID = NumberUtility.getInt(task.getUniqueID());
         if (uniqueID > m_taskUniqueID)
         {
            m_taskUniqueID = uniqueID;
         }
      }

      //
      // Update resource unique IDs
      //      
      for(Iterator iter=m_allResources.iterator(); iter.hasNext();)
      {
         Resource resource = (Resource)iter.next();
         int uniqueID = NumberUtility.getInt(resource.getUniqueID());
         if (uniqueID > m_resourceUniqueID)
         {
            m_resourceUniqueID = uniqueID;
         }
      }
      
      //
      // Update base calendar unique IDs
      //      
      for(Iterator iter=m_baseCalendars.iterator(); iter.hasNext();)
      {
         ProjectCalendar calendar = (ProjectCalendar)iter.next();
         int uniqueID = calendar.getUniqueID();
         if (uniqueID > m_calendarUniqueID)
         {
            m_calendarUniqueID = uniqueID;
         }
      }

      //
      // Update resource calendar unique IDs
      //      
      for(Iterator iter=m_resourceCalendars.iterator(); iter.hasNext();)
      {
         ProjectCalendar calendar = (ProjectCalendar)iter.next();
         int uniqueID = calendar.getUniqueID();
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
   public Date getStartDate ()
   {
      Date startDate = null;

      Iterator iter = m_allTasks.iterator();
      Task task;
      Date taskStartDate;
     
      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         
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
         // is alway correct, the milestone start date may be different
         // to reflect a missed deadline.
         //
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
   public Date getFinishDate ()
   {
      Date finishDate = null;

      Iterator iter = m_allTasks.iterator();
      Task task;
      Date taskFinishDate;

      while (iter.hasNext() == true)
      {
         task = (Task)iter.next();
         
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
   public void fireTaskReadEvent (Task task)
   {
      if (m_projectListeners != null)
      {
         for (Iterator iter=m_projectListeners.iterator(); iter.hasNext();)
         {
            ((ProjectListener)iter.next()).taskRead(task);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a task has been written to a project file.
    * 
    * @param task task instance
    */   
   public void fireTaskWrittenEvent (Task task)
   {
      if (m_projectListeners != null)
      {
         for (Iterator iter=m_projectListeners.iterator(); iter.hasNext();)
         {
            ((ProjectListener)iter.next()).taskWritten(task);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a resource has been read from a project file.
    * 
    * @param resource resource instance
    */   
   public void fireResourceReadEvent (Resource resource)
   {
      if (m_projectListeners != null)
      {
         for (Iterator iter=m_projectListeners.iterator(); iter.hasNext();)
         {
            ((ProjectListener)iter.next()).resourceRead(resource);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a resource has been written to a project file.
    * 
    * @param resource resource instance
    */      
   public void fireResourceWrittenEvent (Resource resource)
   {
      if (m_projectListeners != null)
      {
         for (Iterator iter=m_projectListeners.iterator(); iter.hasNext();)
         {
            ((ProjectListener)iter.next()).resourceWritten(resource);
         }
      }
   }

   /**
    * Adds a listener to this project file.
    * 
    * @param listener listener instance
    */
   public void addProjectListener (ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList();
      }
      m_projectListeners.add(listener);      
   }

   /**
    * Removes a listener from this project file.
    * 
    * @param listener listener instance
    */
   public void removeProjectListener (ProjectListener listener)
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
   public void setTaskFieldAlias (int field, String alias)
   {
      if ((alias != null) && (alias.length() != 0))
      {
         Integer id = new Integer(field);
         m_taskFieldAlias.put(id, alias);
         m_aliasTaskField.put(alias, id);
      }
   }

   /**
    * Retrieves the alias associated with a custom task field.
    * This method will return null if no alias has been defined for
    * this field.
    *
    * @param field field number
    * @return alias text
    */
   public String getTaskFieldAlias (int field)
   {
      return ((String)m_taskFieldAlias.get(new Integer(field)));
   }

   /**
    * Retrieves the field number of a task field based on its alias. If the
    * alias is not recognised, this method will return -1.
    *
    * @param alias alias text
    * @return task field number
    */
   public int getAliasTaskField (String alias)
   {
      Integer result = (Integer)m_aliasTaskField.get(alias);
      int field;

      if (result == null)
      {
         field = -1;
      }
      else
      {
         field = result.intValue();
      }

      return (field);
   }

   /**
    * Associates an alias with a custom resource field number.
    *
    * @param field custom field number
    * @param alias alias text
    */
   public void setResourceFieldAlias (int field, String alias)
   {
      if ((alias != null) && (alias.length() != 0))
      {
         Integer id = new Integer(field);
         m_resourceFieldAlias.put(id, alias);
         m_aliasResourceField.put(alias, id);
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
   public String getResourceFieldAlias (int field)
   {
      return ((String)m_resourceFieldAlias.get(new Integer(field)));
   }

   /**
    * Retrieves the field number of a resource field based on its alias. If the
    * alias is not recognised, this method will return -1.
    *
    * @param alias alias text
    * @return task field number
    */
   public int getAliasResourceField (String alias)
   {
      Integer result = (Integer)m_aliasResourceField.get(alias);
      int field;

      if (result == null)
      {
         field = -1;
      }
      else
      {
         field = result.intValue();
      }

      return (field);
   }

   /**
    * Allows derived classes to gain access to the mapping between
    * MPX task field numbers and aliases.
    *
    * @return task field to alias map
    */
   public Map getTaskFieldAliasMap ()
   {
      return (m_taskFieldAlias);
   }

   /**
    * Allows derived classes to gain access to the mapping between
    * MPX resource field numbers and aliases.
    *
    * @return resource field to alias map
    */
   public Map getResourceFieldAliasMap ()
   {
      return (m_resourceFieldAlias);
   }

   /**
    * Removes an id-to-task mapping.
    *
    * @param id task unique ID
    */
   void unmapTaskUniqueID (Integer id)
   {
      m_taskUniqueIDMap.remove(id);
   }

   /**
    * Adds an id-to-task mapping.
    *
    * @param id task unique ID
    * @param task task instance
    */
   void mapTaskUniqueID (Integer id, Task task)
   {
      m_taskUniqueIDMap.put(id, task);
   }

   /**
    * Removes an id-to-task mapping.
    *
    * @param id task ID
    */
   void unmapTaskID (Integer id)
   {
      m_taskIDMap.remove(id);
   }

   /**
    * Adds an id-to-task mapping.
    *
    * @param id task ID
    * @param task task instance
    */
   void mapTaskID (Integer id, Task task)
   {
      m_taskIDMap.put(id, task);
   }

   /**
    * Removes an id-to-resource mapping.
    *
    * @param id resource unique ID
    */
   void unmapResourceUniqueID (Integer id)
   {
      m_resourceUniqueIDMap.remove(id);
   }

   /**
    * Adds an id-to-resource mapping.
    *
    * @param id resource unique ID
    * @param resource resource instance
    */
   void mapResourceUniqueID (Integer id, Resource resource)
   {
      m_resourceUniqueIDMap.put(id, resource);
   }

   /**
    * Removes an id-to-resource mapping.
    *
    * @param id resource ID
    */
   void unmapResourceID (Integer id)
   {
      m_resourceIDMap.remove(id);
   }

   /**
    * Adds an id-to-resource mapping.
    *
    * @param id resource ID
    * @param resource resource instance
    */
   void mapResourceID (Integer id, Resource resource)
   {
      m_resourceIDMap.put(id, resource);
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
   public void setMppFileType (int fileType)
   {
      m_mppFileType = fileType;
   }
   
   /**
    * Package-private method used to add views to this MPP file.
    *
    * @param view view data
    */
   public void addView (View view)
   {
      m_views.add(view);
   }

   /**
    * This method returns a list of the views defined in this MPP file.
    *
    * @return list of views
    */
   public List getViews ()
   {
      return (m_views);
   }

   /**
    * Package-private method used to add tables to this MPP file.
    *
    * @param table table data
    */
   public void addTable (Table table)
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
   public List getTables ()
   {
      return (m_tables);
   }

   /**
    * Utility method to retrieve the definition of a task table by name.
    * This method will return null if the table name is not recognised.
    * 
    * @param name table name
    * @return table instance
    */
   public Table getTaskTableByName (String name)
   {
      return ((Table)m_taskTablesByName.get(name));
   }

   /**
    * Utility method to retrieve the definition of a resource table by name.
    * This method will return null if the table name is not recognised.
    * 
    * @param name table name
    * @return table instance
    */
   public Table getResourceTableByName (String name)
   {
      return ((Table)m_resourceTablesByName.get(name));
   }
   
   /**
    * This package-private method is used to add resource sub project details.
    * 
    * @param project sub project
    */
   public void setResourceSubProject (SubProject project)
   {
      m_resourceSubProject = project;
   }
   
   /**
    * Retrieves details of the sub project file used as a resource pool.
    * 
    * @return sub project details
    */
   public SubProject getResourceSubProject ()
   {
      return (m_resourceSubProject);
   }
   
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
   private List m_allResources = new LinkedList();

   /**
    * This list holds a reference to all tasks defined in the
    * MPX file.
    */
   private List m_allTasks = new LinkedList();

   /**
    * List holding references to the top level tasks
    * as defined by the outline level.
    */
   private List m_childTasks = new LinkedList();

   /**
    * This list holds a reference to all resource assignments defined in the
    * MPX file.
    */
   private List m_allResourceAssignments = new LinkedList();

   /**
    * List holding references to all base calendars.
    */
   private List m_baseCalendars = new LinkedList();

   /**
    * List holding references to all resource calendars.
    */
   private List m_resourceCalendars = new LinkedList();
   
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
    * Indicating whether WBS value should be calculated on creation, or will
    * be manually set.
    */
   private boolean m_autoWBS;

   /**
    * Indicating whether the Outline Level value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineLevel;

   /**
    * Indicating whether the Outline Number value should be calculated on
    * creation, or will be manually set.
    */
   private boolean m_autoOutlineNumber;

   /**
    * Indicating whether the unique ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskUniqueID;

   /**
    * Indicating whether the unique ID of a calendar should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoCalendarUniqueID;

   /**
    * Indicating whether the ID of a task should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoTaskID;

   /**
    * Indicating whether the unique ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceUniqueID;

   /**
    * Indicating whether the ID of a resource should be
    * calculated on creation, or will be manually set.
    */
   private boolean m_autoResourceID;

   /**
    * Maps from a task field number to a task alias.
    */
   private Map m_taskFieldAlias = new HashMap();

   /**
    * Maps from a task field alias to a task field number.
    */
   private Map m_aliasTaskField = new HashMap();

   /**
    * Maps from a resource field number to a resource alias.
    */
   private Map m_resourceFieldAlias = new HashMap();

   /**
    * Maps from a resource field alias to a resource field number.
    */
   private Map m_aliasResourceField = new HashMap();

   /**
    * Maps from a task unique ID to a task instance.
    */
   private Map m_taskUniqueIDMap = new HashMap();

   /**
    * Maps from a task ID to a task instance.
    */
   private Map m_taskIDMap = new HashMap();

   /**
    * Maps from a resource unique ID to a resource instance.
    */
   private Map m_resourceUniqueIDMap = new HashMap();

   /**
    * Maps from a resource ID to a resource instance.
    */
   private Map m_resourceIDMap = new HashMap();

   /**
    * List of project event listeners.
    */
   private List m_projectListeners;
   
   /**
    * This value is used to represent the type of MPP file that
    * has been read.
    */
   private int m_mppFileType;
   
   /**
    * List of views defined in this file.
    */
   private List m_views = new ArrayList();

   /**
    * List of tables defined in this file.
    */
   private List m_tables = new ArrayList();
   
   /**
    * Index of task tables by name.
    */
   private Map m_taskTablesByName = new HashMap();
   
   /**
    * Index of resource tables by name.
    */
   private Map m_resourceTablesByName = new HashMap();

   /**
    * Resource sub project.
    */
   private SubProject m_resourceSubProject;   
}
