/*
 * file:       Context.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       01/07/2019
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

package org.mpxj.sdef;

import java.util.HashMap;
import java.util.Map;

import org.mpxj.EventManager;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.Task;

/**
 * Context required to process individual records.
 */
class Context
{
   /**
    * Retrieve the current project.
    *
    * @return current project
    */
   public ProjectFile getProject()
   {
      return m_project;
   }

   /**
    * Retrieve the event manager for the current project.
    *
    * @return current project's event manager
    */
   public EventManager getEventManager()
   {
      return m_project.getEventManager();
   }

   /**
    * Add a calendar to the project.
    *
    * @param code calendar unique identifier
    * @return new calendar
    */
   public ProjectCalendar addCalendar(String code)
   {
      ProjectCalendar calendar = m_project.addCalendar();
      m_calendars.put(code, calendar);
      return calendar;
   }

   /**
    * Retrieve a calendar based on its unique identifier.
    *
    * @param code calendar unique identifier
    * @return calendar instance
    */
   public ProjectCalendar getCalendar(String code)
   {
      return m_calendars.get(code);
   }

   /**
    * Add a new task.
    *
    * @param activityID task unique identifier
    * @return new task
    */
   public Task addTask(String activityID)
   {
      Task task = m_project.addTask();
      m_tasks.put(activityID, task);
      return task;
   }

   /**
    * Retrieve a task based on its unique identifier.
    *
    * @param activityID task unique identifier
    * @return task instance
    */
   public Task getTask(String activityID)
   {
      return m_tasks.get(activityID);
   }

   private final ProjectFile m_project = new ProjectFile();
   private final Map<String, ProjectCalendar> m_calendars = new HashMap<>();
   private final Map<String, Task> m_tasks = new HashMap<>();
}
