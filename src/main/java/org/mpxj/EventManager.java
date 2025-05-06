/*
 * file:       EventManager.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       27/04/2015
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

import java.util.ArrayList;
import java.util.List;

import org.mpxj.listener.ProjectListener;

/**
 * Provides subscriptions to events raised when project files are written and read.
 */
public class EventManager
{
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
    * This method is called to alert project listeners to the fact that
    * a calendar has been read from a project file.
    *
    * @param calendar calendar instance
    */
   public void fireCalendarReadEvent(ProjectCalendar calendar)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.calendarRead(calendar);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a resource assignment has been read from a project file.
    *
    * @param resourceAssignment resourceAssignment instance
    */
   public void fireAssignmentReadEvent(ResourceAssignment resourceAssignment)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.assignmentRead(resourceAssignment);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a resource assignment has been written to a project file.
    *
    * @param resourceAssignment resourceAssignment instance
    */
   public void fireAssignmentWrittenEvent(ResourceAssignment resourceAssignment)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.assignmentWritten(resourceAssignment);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a relation has been read from a project file.
    *
    * @param relation relation instance
    */
   public void fireRelationReadEvent(Relation relation)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.relationRead(relation);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a relation has been written to a project file.
    *
    * @param relation relation instance
    */
   public void fireRelationWrittenEvent(Relation relation)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.relationWritten(relation);
         }
      }
   }

   /**
    * This method is called to alert project listeners to the fact that
    * a calendar has been written to a project file.
    *
    * @param calendar calendar instance
    */
   public void fireCalendarWrittenEvent(ProjectCalendar calendar)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            listener.calendarWritten(calendar);
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
         m_projectListeners = new ArrayList<>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * Adds a collection of listeners to the current project.
    *
    * @param listeners collection of listeners
    */
   public void addProjectListeners(List<ProjectListener> listeners)
   {
      if (listeners != null)
      {
         for (ProjectListener listener : listeners)
         {
            addProjectListener(listener);
         }
      }
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
    * List of project event listeners.
    */
   private List<ProjectListener> m_projectListeners;
}
