/*
 * file:       ProjectListener.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Dec 13, 2005
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

package org.mpxj.listener;

import org.mpxj.ProjectCalendar;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;

/**
 * Classes implementing this interface can be used to receive notification
 * of events occurring within the project file.
 */
public interface ProjectListener
{
   /**
    * This method is called when a task is read from a file.
    *
    * @param task task instance
    */
   void taskRead(Task task);

   /**
    * This method is called when a task is written to a file.
    *
    * @param task task instance
    */
   void taskWritten(Task task);

   /**
    * This method is called when a resource is read from a file.
    *
    * @param resource resource instance
    */
   void resourceRead(Resource resource);

   /**
    * This method is called when a resource is written to a file.
    *
    * @param resource resource instance
    */
   void resourceWritten(Resource resource);

   /**
    * This method is called when a calendar is read from a file.
    *
    * @param calendar calendar instance
    */
   void calendarRead(ProjectCalendar calendar);

   /**
    * This method is called when a calendar is written to a file.
    *
    * @param calendar calendar instance
    */
   void calendarWritten(ProjectCalendar calendar);

   /**
    * This method is called when an assignment is read from a file.
    *
    * @param assignment resource assignment
    */
   void assignmentRead(ResourceAssignment assignment);

   /**
    * This method is called when an assignment is written to a file.
    *
    * @param assignment assignment instance
    */
   void assignmentWritten(ResourceAssignment assignment);

   /**
    * This method is called when a relation is read from a file.
    *
    * @param relation relation instance
    */
   void relationRead(Relation relation);

   /**
    * This method is called when a relation is written to a file.
    *
    * @param relation relation instance
    */
   void relationWritten(Relation relation);
}
