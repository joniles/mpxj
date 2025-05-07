/*
 * file:       ChildTaskContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2013
 * date:       08/11/2013
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

import java.util.List;

/**
 * Interface implemented by classes which have child tasks.
 */
public interface ChildTaskContainer
{
   /**
    * Retrieve a list of child tasks held by this object.
    *
    * @return list of child tasks
    */
   List<Task> getChildTasks();

   /**
    * Creates and adds a task to the list of tasks held by this object.
    *
    * @return newly created task
    */
   Task addTask();
}
