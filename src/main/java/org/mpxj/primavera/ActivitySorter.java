/*
 * file:       ActivitySorter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       06/06/2018
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

package org.mpxj.primavera;

import java.util.List;
import java.util.Set;

import org.mpxj.ChildTaskContainer;
import org.mpxj.Task;

/**
 * Ensures correct activity order within.
 */
class ActivitySorter
{
   /**
    * Constructor.
    *
    * @param wbsTasks set of WBS tasks
    */
   public ActivitySorter(Set<Task> wbsTasks)
   {
      m_wbsTasks = wbsTasks;
   }

   /**
    * Recursively sort the supplied child tasks.
    *
    * @param container child tasks
    */
   public void sort(ChildTaskContainer container)
   {
      // Do we have any tasks?
      List<Task> tasks = container.getChildTasks();
      if (!tasks.isEmpty())
      {
         //
         // Sort child activities
         //
         tasks.forEach(this::sort);

         //
         // Sort Order:
         // 1. Activities come first
         // 2. WBS come last
         // 3. Activities ordered by activity ID
         // 4. WBS ordered by ID
         //
         tasks.sort((t1, t2) -> {
            boolean t1IsWbs = m_wbsTasks.contains(t1);
            boolean t2IsWbs = m_wbsTasks.contains(t2);

            // Both are WBS
            if (t1IsWbs && t2IsWbs)
            {
               return t1.getID().compareTo(t2.getID());
            }

            // Both are activities
            if (!t1IsWbs && !t2IsWbs)
            {
               String activityID1 = t1.getActivityID();
               String activityID2 = t2.getActivityID();

               if (activityID1 == null || activityID2 == null)
               {
                  return (activityID1 == null && activityID2 == null ? 0 : (activityID1 == null ? 1 : -1));
               }

               return activityID1.compareTo(activityID2);
            }

            // One activity one WBS
            return t1IsWbs ? 1 : -1;
         });
      }
   }

   final Set<Task> m_wbsTasks;
}
