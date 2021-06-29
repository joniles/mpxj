/*
 * file:       PrimaveraHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       29/06/2021
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

package net.sf.mpxj.primavera;

import net.sf.mpxj.Task;

/**
 * Class containing helper methods used when working with Primavera schedules.
 */
public final class PrimaveraHelper
{
   /**
    * Private constructor to prevent instantiation.
    */
   private PrimaveraHelper()
   {

   }

   /**
    * Generate a key used to match tasks from the current schedule to tasks from a baseline schedule.
    *
    * @param task Task instance
    * @return baseline key
    */
   public static final String baselineKey(Task task)
   {
      String activityID = task.getCanonicalActivityID();

      // For Activities, the Activity ID is sufficient to uniquely identify a
      // task. For WBS entries the value in Activity ID may not be unique.
      // For WBS entries we include an additional value to get around this.
      return task.getSummary() ? activityID + " " + task.getOutlineLevel() : activityID;
   }
}
