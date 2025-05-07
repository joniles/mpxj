/*
 * file:       Scheduler.java
 * author:     Jon Iles
 * date:       2025-04-02
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

package org.mpxj.cpm;

import java.time.LocalDateTime;

import org.mpxj.ProjectFile;

/**
 * Interface implemented by classes which will schedule a project using the Critical Path Method (CPM).
 */
public interface Scheduler
{
   /**
    * Calling this method schedules the supplied project using CPM, with the tasks
    * in the project starting from the supplied start date.
    *
    * @param file project to schedule
    * @param startDate start date
    */
   void schedule(ProjectFile file, LocalDateTime startDate) throws CpmException;
}
