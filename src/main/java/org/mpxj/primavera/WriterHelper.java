/*
 * file:       WriterHelper.java
 * author:     Jon Iles
 * date:       2025-02-26
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

import java.time.LocalDateTime;
import java.util.Optional;

import org.mpxj.Code;
import org.mpxj.Duration;
import org.mpxj.ProjectProperties;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;

/**
 * Common code shared between XER and PMXML writers.
 */
class WriterHelper
{
   /**
    * Populate project planned start date.
    *
    * @param props project properties
    * @return planned start date
    */
   public static LocalDateTime getProjectPlannedStart(ProjectProperties props)
   {
      return Optional.ofNullable(Optional.ofNullable(props.getPlannedStart())
         .orElseGet(props::getStartDate))
         .orElseGet(props::getCurrentDate);
   }

   /**
    * Populate activity planned duration.
    *
    * @param task activity
    * @return planned duration
    */
   public static Duration getActivityPlannedDuration(Task task)
   {
      return Optional.ofNullable(Optional.ofNullable(task.getPlannedDuration())
         .orElseGet(task::getDuration))
         .orElseGet(() -> Duration.getInstance(0, TimeUnit.HOURS));
   }

   /**
    * Populate the activity ID.
    *
    * @param task activity
    * @return activity ID
    */
   public static String getActivityID(Task task)
   {
      return task.getActivityID() == null || task.getActivityID().trim().isEmpty() ? task.getWBS() : task.getActivityID();
   }

   /**
    * Populate the resource ID.
    *
    * @param resource resource
    * @return resource ID
    */
   public static String getResourceID(Resource resource)
   {
      String id = resource.getResourceID();
      return id == null || id.isEmpty() ? RESOURCE_ID_PREFIX + resource.getUniqueID() : id;
   }

   /**
    * Populate the role ID.
    *
    * @param role role
    * @return role ID
    */
   public static String getRoleID(Resource role)
   {
      String id = role.getResourceID();
      return id == null || id.isEmpty() ? ROLE_ID_PREFIX + role.getResourceID() : id;
   }

   /**
    * Determine if a resource assignment is valid to appear in the file.
    * This avoids writing assignments which do not have a task or a resource
    * and also avoids writing assignments for the "unknown" resource
    * used by Microsoft Project.
    *
    * @param assignment assignment to test
    * @return true if this assignment can be written to an XER file
    */
   public static boolean isValidAssignment(ResourceAssignment assignment)
   {
      Task task = assignment.getTask();
      return assignment.getResource() != null && task != null && task.getUniqueID().intValue() != 0 && !task.getSummary();
   }

   /**
    * Retrieve the max length value for a code, or generate an appropriate value.
    *
    * @param code code definition
    * @return max length value
    */
   public static Integer getCodeMaxLength(Code code)
   {
      Integer maxLength = code.getMaxLength();
      return maxLength == null ? code.getValues().stream().map(v -> Integer.valueOf(v.getName().length())).max(Integer::compareTo).orElse(DEFAULT_CODE_MAX_LENGTH) : maxLength;
   }

   private static final String RESOURCE_ID_PREFIX = "RESOURCE-";
   private static final String ROLE_ID_PREFIX = "ROLE-";
   private static final Integer DEFAULT_CODE_MAX_LENGTH = Integer.valueOf(7);
}
