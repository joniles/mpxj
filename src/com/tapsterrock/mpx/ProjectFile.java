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

package com.tapsterrock.mpx;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



/**
 * This class represents a project plan.
 */
public class ProjectFile
{
   /**
    * Default constructor.
    * 
    * Note that we force the locale of the file to be English, ignoring
    * the system default locale value. We do this as the vast majority of
    * MPX file users will have international versions of MS Project,
    * not localised ones. Users of localised MPX file versions must call
    * the setLocale method explicitly.
    */
   public ProjectFile ()
   {
      setLocale(Locale.ENGLISH);
   }
   
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
   void updateUniqueIdentifiers ()
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
    * This is a convenience method provided to allow an empty record
    * of a specified type to be added to the file.
    *
    * @param recordNumber type of record to be added
    * @return object representing the new record
    * @throws MPXException Thrown on parse errors
    */
   private MPXRecord add (int recordNumber)
      throws MPXException
   {
      return (add(String.valueOf(recordNumber), Record.EMPTY_RECORD));
   }

   /**
    * This method adds a new record of the specified type and populates
    * it with data read from an MPX file.
    *
    * @param recordNumber type of record to add
    * @param record data from MPX file
    * @return new object representing record from MPX file
    * @throws MPXException normally thrown on parsing errors
    */
   MPXRecord add (String recordNumber, Record record)
      throws MPXException
   {
      MPXRecord current = null;

      switch (Integer.parseInt(recordNumber))
      {
         case MPXConstants.COMMENTS_RECORD_NUMBER:
         {
            // silently ignored
            break;
         }

         case MPXConstants.CURRENCY_SETTINGS_RECORD_NUMBER:
         {
            m_projectHeader.updateCurrencySettings(record);
            current = m_projectHeader;
            break;
         }

         case MPXConstants.DEFAULT_SETTINGS_RECORD_NUMBER:
         {
            m_projectHeader.updateDefaultSettings(record);
            current = m_projectHeader;
            break;
         }

         case MPXConstants.DATE_TIME_SETTINGS_RECORD_NUMBER:
         {
            m_projectHeader.updateDateTimeSettings(record);
            current = m_projectHeader;
            break;
         }

         case MPXConstants.BASE_CALENDAR_RECORD_NUMBER:
         {
            m_lastBaseCalendar = new MPXCalendar(this, record, true);
            current = m_lastBaseCalendar;
            m_baseCalendars.add(current);
            break;
         }

         case MPXConstants.BASE_CALENDAR_HOURS_RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               current = m_lastBaseCalendar.addCalendarHours(record);
            }

            break;
         }

         case MPXConstants.BASE_CALENDAR_EXCEPTION_RECORD_NUMBER:
         {
            if (m_lastBaseCalendar != null)
            {
               current = m_lastBaseCalendar.addCalendarException(record);
            }

            break;
         }

         case MPXConstants.PROJECT_HEADER_RECORD_NUMBER:
         {
            m_projectHeader.updateProjectHeader(record);
            current = m_projectHeader;
            break;
         }

         case MPXConstants.RESOURCE_MODEL_TEXT_RECORD_NUMBER:
         {
            if ((m_resourceTableDefinition == false) && (m_ignoreTextModels == false))
            {
               current = m_resourceModel;
               m_resourceModel.update(record, true);
               m_resourceTableDefinition = true;
            }

            break;
         }

         case MPXConstants.RESOURCE_MODEL_NUMERIC_RECORD_NUMBER:
         {
            if (m_resourceTableDefinition == false)
            {
               current = m_resourceModel;
               m_resourceModel.update(record, false);
               m_resourceTableDefinition = true;
            }

            break;
         }

         case MPXConstants.RESOURCE_RECORD_NUMBER:
         {
            m_lastResource = new Resource(this, record);
            current = m_lastResource;
            m_allResources.add(current);
            if (record != Record.EMPTY_RECORD)
            {
               fireResourceReadEvent(m_lastResource);
            }
            break;
         }

         case MPXConstants.RESOURCE_NOTES_RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               current = m_lastResource.addResourceNotes(record);
            }

            break;
         }

         case MPXConstants.RESOURCE_CALENDAR_RECORD_NUMBER:
         {
            if (m_lastResource != null)
            {
               m_lastResourceCalendar = m_lastResource.addResourceCalendar(record);
               current = m_lastResourceCalendar;
            }

            break;
         }

         case MPXConstants.RESOURCE_CALENDAR_HOURS_RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               current = m_lastResourceCalendar.addCalendarHours(record);
            }

            break;
         }

         case MPXConstants.RESOURCE_CALENDAR_EXCEPTION_RECORD_NUMBER:
         {
            if (m_lastResourceCalendar != null)
            {
               current = m_lastResourceCalendar.addCalendarException(record);
            }

            break;
         }

         case MPXConstants.TASK_MODEL_TEXT_RECORD_NUMBER:
         {
            if ((m_taskTableDefinition == false) && (m_ignoreTextModels == false))
            {
               current = m_taskModel;
               m_taskModel.update(record, true);
               m_taskTableDefinition = true;
            }

            break;
         }

         case MPXConstants.TASK_MODEL_NUMERIC_RECORD_NUMBER:
         {
            if (m_taskTableDefinition == false)
            {
               current = m_taskModel;
               m_taskModel.update(record, false);
               m_taskTableDefinition = true;
            }

            break;
         }

         case MPXConstants.TASK_RECORD_NUMBER:
         {
            m_lastTask = new Task(this, record);
            current = m_lastTask;
            m_allTasks.add(current);

            int outlineLevel = m_lastTask.getOutlineLevelValue();

            if (m_baseOutlineLevel == -1)
            {
               m_baseOutlineLevel = outlineLevel;
            }

            if (outlineLevel == m_baseOutlineLevel)
            {
               m_childTasks.add(m_lastTask);
            }
            else
            {
               if (m_childTasks.isEmpty() == true)
               {
                  throw new MPXException(MPXException.INVALID_OUTLINE);
               }

               ((Task)m_childTasks.get(m_childTasks.size()-1)).addChildTask(m_lastTask, outlineLevel);
            }

            if (record != Record.EMPTY_RECORD)
            {
               fireTaskReadEvent(m_lastTask);
            }
            break;
         }

         case MPXConstants.TASK_NOTES_RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               current = m_lastTask.addTaskNotes(record);
            }

            break;
         }

         case MPXConstants.RECURRING_TASK_RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               current = m_lastTask.addRecurringTask(record);
            }

            break;
         }

         case MPXConstants.RESOURCE_ASSIGNMENT_RECORD_NUMBER:
         {
            if (m_lastTask != null)
            {
               m_lastResourceAssignment = m_lastTask.addResourceAssignment(record);
               current = m_lastResourceAssignment;
               m_allResourceAssignments.add(m_lastResourceAssignment);
            }

            break;
         }

         case MPXConstants.RESOURCE_ASSIGNMENT_WORKGROUP_FIELDS_RECORD_NUMBER:
         {
            if (m_lastResourceAssignment != null)
            {
               current = m_lastResourceAssignment.addWorkgroupAssignment(record);
            }

            break;
         }

         case MPXConstants.PROJECT_NAMES_RECORD_NUMBER:
         {
            // silently ignored
            break;
         }

         case MPXConstants.DDE_OLE_CLIENT_LINKS_RECORD_NUMBER:
         {
            // silently ignored
            break;
         }

         case MPXConstants.FILE_CREATION_RECORD_NUMBER:
         {
            current = getFileCreationRecord();
            ((FileCreationRecord)current).setValues(record);
            break;
         }

         default:
            throw new MPXException(MPXException.INVALID_RECORD);
      }

      return (current);
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
    * @throws MPXException normally thrown on parse errors
    */
   public Task addTask ()
      throws MPXException
   {
      return ((Task)add(MPXConstants.TASK_RECORD_NUMBER));
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
         int id = firstTask.getIDValue();
         if (id != 0)
         {
            id = 1;
         }
         
         Iterator iter = m_allTasks.iterator();
         while (iter.hasNext() == true)
         {
            ((Task)iter.next()).setID(id++);
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
            resource.setID(id++);
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
    * Method for accessing the Task Model.
    *
    * @return task model
    */
   TaskModel getTaskModel ()
   {
      return (m_taskModel);
   }

   /**
    * Method for accessing the Resource Model.
    *
    * @return resource model
    */
   ResourceModel getResourceModel ()
   {
      return (m_resourceModel);
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
   int getTaskUniqueID ()
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
   int getTaskID ()
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
    * This method sets the flag indicating that the text version of the
    * Task and Resource Table Definition records should be ignored. Ignoring
    * these records gets around the problem where MPX files have been generated
    * with incorrect taks or resource field names, but correct task or resource
    * field numbers in the numeric version of the record.
    *
    * @param flag Boolean flag
    */
   public void setIgnoreTextModels (boolean flag)
   {
      m_ignoreTextModels = flag;
   }

   /**
    * Retrieves the flag indicating that the text version of the Task and
    * Resource Table Definition records should be ignored.
    *
    * @return Boolean flag
    */
   public boolean getIgnoreTextModels ()
   {
      return (m_ignoreTextModels);
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
   int getResourceUniqueID ()
   {
      return (++m_resourceUniqueID);
   }

   /**
    * This method is used to retrieve the next ID for a resource.
    *
    * @return next ID
    */
   int getResourceID ()
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
    * @return new MPXCalendar instance
    */
   public MPXCalendar getResourceCalendar ()
   {
      MPXCalendar calendar = new MPXCalendar(this, false);
      m_resourceCalendars.add(calendar);
      return (calendar);
   }

   /**
    * This method is used to add a new base calendar to the file.
    *
    * @return new base calendar object
    * @throws MPXException normally thrown on parse errors
    */
   public MPXCalendar addBaseCalendar ()
      throws MPXException
   {
      return ((MPXCalendar)add(MPXConstants.BASE_CALENDAR_RECORD_NUMBER));
   }

   /**
    * Removes a base calendar.
    * 
    * @param calendar calendar to be removed
    */
   public void removeCalendar (MPXCalendar calendar)
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
    * @throws MPXException normally thrown when a parse error occurs
    */
   public MPXCalendar addDefaultBaseCalendar ()
      throws MPXException
   {
      MPXCalendar calendar = (MPXCalendar)add(MPXConstants.BASE_CALENDAR_RECORD_NUMBER);

      calendar.setName(MPXCalendar.DEFAULT_BASE_CALENDAR_NAME);

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
    * @return new MPXCalendar instance
    */
   public MPXCalendar getDefaultResourceCalendar ()
   {
      MPXCalendar calendar = new MPXCalendar(this, false);

      calendar.setWorkingDay(Day.SUNDAY, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(Day.MONDAY, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(Day.TUESDAY, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(Day.WEDNESDAY, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(Day.THURSDAY, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(Day.FRIDAY, MPXCalendar.DEFAULT);
      calendar.setWorkingDay(Day.SATURDAY, MPXCalendar.DEFAULT);

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
    * @throws MPXException normally thrown on parse errors
    */
   public Resource addResource ()
      throws MPXException
   {
      return ((Resource)add(MPXConstants.RESOURCE_RECORD_NUMBER));
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
      int resourceUniqueID = resource.getUniqueIDValue();
      while (iter.hasNext() == true)
      {
         assignment = (ResourceAssignment)iter.next();
         if (assignment.getResourceUniqueID().intValue() == resourceUniqueID)
         {
            assignment.getTask().removeResourceAssignment(assignment);
            iter.remove();
         }
      }
      
      MPXCalendar calendar = resource.getResourceCalendar();
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
    * @throws MPXException
    */
   public ResourceAssignment newResourceAssignment (Task task)
      throws MPXException
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
   public MPXCalendar getBaseCalendar (String calendarName)
   {
      MPXCalendar calendar = null;
      
      if (calendarName != null && calendarName.length() != 0)
      {
         String name;
         Iterator iter = m_baseCalendars.iterator();
   
         while (iter.hasNext() == true)
         {
            calendar = (MPXCalendar)iter.next();
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
    * @return MPXCalendar instance
    */
   public MPXCalendar getBaseCalendarByUniqueID (int calendarID)
   {
      MPXCalendar calendar = null;
      Iterator iter = m_baseCalendars.iterator();

      while (iter.hasNext() == true)
      {
         calendar = (MPXCalendar)iter.next();

         if (calendar.getUniqueID() == calendarID)
         {
            break;
         }

         calendar = null;
      }

      return (calendar);
   }

   /**
    * This method retrieves the time formatter.
    *
    * @return time formatter
    */
   public DateFormat getTimeFormat ()
   {
      return (m_timeFormat);
   }

   /**
    * This method retrieves the date time formatter.
    *
    * @return date time formatter
    */
   public DateFormat getDateTimeFormat ()
   {
      return (m_dateTimeFormat);
   }

   /**
    * This method retrieves the date formatter.
    *
    * @return date formatter
    */
   public DateFormat getDateFormat ()
   {
      return (m_dateFormat);
   }

   /**
    * This method retrieves the currency formatter.
    *
    * @return currency formatter
    */
   public NumberFormat getCurrencyFormat ()
   {
      return (m_currencyFormat);
   }

   /**
    * This package-private method is called internally to update
    * the currency format. Note that we also create an appropriate
    * singleton at this point to represent a zero currency value.
    * 
    * @param primaryPattern new format pattern
    * @param alternativePatterns alternative format patterns
    * @param decimalSeparator Locale specific decimal separator to replace placeholder
    * @param groupingSeparator Locale specific grouping separator to replace placeholder
    */
   void setCurrencyFormat (String primaryPattern, String[] alternativePatterns, char decimalSeparator, char groupingSeparator)
   {
      m_currencyFormat.applyPattern(primaryPattern, alternativePatterns, getDecimalSeparator(), getThousandsSeparator());
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
    * @return new MPXDuration object
    * @throws MPXException normally when no Standard calendar is available
    */
   public MPXDuration getDuration (Date startDate, Date endDate)
      throws MPXException
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
    * @return new MPXDuration object
    * @throws MPXException normally when no Standard calendar is available
    */
   public MPXDuration getDuration (String calendarName, Date startDate, Date endDate)
      throws MPXException
   {
      MPXCalendar calendar = getBaseCalendar(calendarName);

      if (calendar == null)
      {
         throw new MPXException(MPXException.CALENDAR_ERROR + ": " + calendarName);
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
   public Task getTaskByID (int id)
   {
      return ((Task)m_taskIDMap.get(new Integer(id)));
   }

   /**
    * This method allows an arbitrary task to be retrieved based
    * on its UniqueID field.
    *
    * @param id task identified
    * @return the requested task, or null if not found
    */
   public Task getTaskByUniqueID (int id)
   {
      return ((Task)m_taskUniqueIDMap.get(new Integer(id)));
   }

   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its ID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByID (int id)
   {
      return ((Resource)m_resourceIDMap.get(new Integer(id)));
   }

   /**
    * This method allows an arbitrary resource to be retrieved based
    * on its UniqueID field.
    *
    * @param id resource identified
    * @return the requested resource, or null if not found
    */
   public Resource getResourceByUniqueID (int id)
   {
      return ((Resource)m_resourceUniqueIDMap.get(new Integer(id)));
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
            level = task.getOutlineLevelValue();
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

                        lastLevel = parent.getOutlineLevelValue();
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
         int uniqueID = task.getUniqueIDValue();
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
         int uniqueID = resource.getUniqueIDValue();
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
         MPXCalendar calendar = (MPXCalendar)iter.next();
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
         MPXCalendar calendar = (MPXCalendar)iter.next();
         int uniqueID = calendar.getUniqueID();
         if (uniqueID > m_calendarUniqueID)
         {
            m_calendarUniqueID = uniqueID;
         }
      }      
   }
   
   /**
    * Accessor method used to retrieve the decimal separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @return decimal separator character
    */
   public char getDecimalSeparator ()
   {
      return (m_decimalSeparator);
   }

   /**
    * Modifier method used to set the decimal separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @param separator decimal separator character
    */
   public void setDecimalSeparator (char separator)
   {
      m_decimalSeparator = separator;

      if ((m_projectHeader != null) && (m_projectHeader.getDecimalSeparator() != separator))
      {
         m_projectHeader.setDecimalSeparator(separator);
      }
   }

   /**
    * Accessor method used to retrieve the thousands separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @return thousands separator character
    */
   public char getThousandsSeparator ()
   {
      return (m_thousandsSeparator);
   }

   /**
    * Modifier method used to set the thousands separator character.
    * Note that this value is synchronized with the same value in the
    * currency settings record. This value affects all decimal numbers
    * that appear in the MPX file.
    *
    * @param separator thousands separator character
    */
   public void setThousandsSeparator (char separator)
   {
      m_thousandsSeparator = separator;

      if ((m_projectHeader != null) && (m_projectHeader.getThousandsSeparator() != separator))
      {
         m_projectHeader.setThousandsSeparator(separator);
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
         if (task.getUniqueIDValue() == 0)
         {
            continue;
         }
         
         //
         // Select the actual or forecast start date. Note that the
         // behaviour is different for milestones. The milestone end date
         // is alway correct, the milestone start date may be different
         // to reflect a missed deadline.
         //
         if (BooleanUtility.getBoolean(task.getMilestone()) == true)
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
         if (task.getUniqueIDValue() == 0)
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
    * Package private method used to retrieve the standard decimal format
    * used for writing MPX records.
    *
    * @return MPXNumberFormat instance
    */
   NumberFormat getDecimalFormat ()
   {
      return (new MPXNumberFormat("0.00#", m_decimalSeparator, m_thousandsSeparator));
   }

   /**
    * Package private method used to retrieve the standard decimal format
    * used for writing MPXDuration values.
    *
    * @return MPXNumberFormat instance
    */
   NumberFormat getDurationDecimalFormat ()
   {
      return (new MPXNumberFormat(MPXDuration.DECIMAL_FORMAT_STRING, m_decimalSeparator, m_thousandsSeparator));
   }

   /**
    * Package private method used to retrieve the standard decimal format
    * used for writing MPXPercentage values.
    *
    * @return MPXNumberFormat instance
    */
   NumberFormat getPercentageDecimalFormat ()
   {
      return (new MPXNumberFormat("##0.##", m_decimalSeparator, m_thousandsSeparator));
   }

   /**
    * Package private method used to retrieve the standard decimal format
    * used for writing MPXUnits values.
    *
    * @return MPXNumberFormat instance
    */
   NumberFormat getUnitsDecimalFormat ()
   {
      return (new MPXNumberFormat("#.##", m_decimalSeparator, m_thousandsSeparator));
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
    * This method returns the locale used by this MPX file.
    *
    * @return current locale
    */
   public Locale getLocale ()
   {
      return (m_locale);
   }

   /**
    * Locale used for this MPX file. Defaults to English.
    */
   private Locale m_locale = Locale.ENGLISH;

   /**
    * This method sets the locale to be used by this MPX file.
    *
    * @param locale locale to be used
    */
   public void setLocale (Locale locale)
   {
      m_locale = locale;

      m_delimiter = LocaleData.getChar(m_locale, LocaleData.FILE_DELIMITER);
      m_thousandsSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR);
      m_decimalSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR);
      m_fileCreationRecord.setLocale(locale);
      m_projectHeader.setLocale(locale);
      m_dateTimeFormat.setLocale(locale);
      m_dateFormat.setLocale(locale);
      m_timeFormat.setLocale(locale);
      m_taskModel.setLocale(locale);
      m_resourceModel.setLocale(locale);
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
    * Allows derived classes to determine if task field aliases have
    * been defined.
    *
    * @return true if task field aliases have been defined
    */
   protected boolean taskFieldAliasesDefined ()
   {
      return (!m_taskFieldAlias.isEmpty());
   }

   /**
    * Allows derived classes to determine if resource field aliases have
    * been defined.
    *
    * @return true if resource field aliases have been defined
    */
   protected boolean resourceFieldAliasesDefined ()
   {
      return (!m_resourceFieldAlias.isEmpty());
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
    * Date time formatter.
    */
   private MPXDateFormat m_dateTimeFormat = new MPXDateFormat();

   /**
    * Date formatter.
    */
   private MPXDateFormat m_dateFormat = new MPXDateFormat();

   /**
    * Time formatter.
    */
   private MPXTimeFormat m_timeFormat = new MPXTimeFormat();

   /**
    * Currency formatter.
    */
   private MPXNumberFormat m_currencyFormat = new MPXNumberFormat();

   /**
    * File creation record.
    */
   private FileCreationRecord m_fileCreationRecord = new FileCreationRecord(this);

   /**
    * Project header record.
    */
   private ProjectHeader m_projectHeader = new ProjectHeader(this);

   /**
    * Task model.
    */
   private TaskModel m_taskModel = new TaskModel(this);

   /**
    * Resource model.
    */
   private ResourceModel m_resourceModel = new ResourceModel(this);

   /**
    * Reference to the last task added to the file.
    */
   private Task m_lastTask;

   /**
    * Reference to the last resource added to the file.
    */
   private Resource m_lastResource;

   /**
    * Reference to the last resource calendar added to the file.
    */
   private MPXCalendar m_lastResourceCalendar;

   /**
    * Reference to the last resource assignment added to the file.
    */
   private ResourceAssignment m_lastResourceAssignment;

   /**
    * Reference to the last base calendar added to the file.
    */
   private MPXCalendar m_lastBaseCalendar;

   /**
    * Flag indicating the existence of a resource model record.
    */
   private boolean m_resourceTableDefinition;

   /**
    * Flag indicating the existence of a task model record.
    */
   private boolean m_taskTableDefinition;

   /**
    * Character to be used as delimiter throughout this file.
    */
   private char m_delimiter = LocaleData.getChar(m_locale, LocaleData.FILE_DELIMITER);

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
    * Flag indicating that the text form of the task and resource
    * models should be ignored.
    */
   private boolean m_ignoreTextModels = true;

   /**
    * This member data is used to hold the outline level number of the
    * first outline level used in the MPX file. When data from
    * Microsoft Project is saved in MPX format, MSP creates an invisible
    * task with an outline level as zero, which acts as an umbrella
    * task for all of the other tasks defined in the file. This is not
    * a strict requirement, and an MPX file could be generated from another
    * source that only contains "visible" tasks that have outline levels
    * >= 1.
    */
   private int m_baseOutlineLevel = -1;

   /**
    * Default thousands separator character. Despite the fact that this
    * value appears as part of the CurrencySettings, it is in fact a global
    * setting, which is why this attribute is defined here.
    */
   private char m_thousandsSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR);

   /**
    * Default decimal separator character. Despite the fact that this
    * value appears as part of the CurrencySettings, it is in fact a global
    * setting, which is why this attribute is defined here.
    */
   private char m_decimalSeparator = LocaleData.getChar(m_locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR);

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
