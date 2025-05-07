/*
 * file:       TaskTypeHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

package org.mpxj.mpp;

import java.util.HashMap;
import java.util.Map;

import org.mpxj.TaskType;

/**
 * Provides methods to convert to and from Microsoft Project's representation
 * of task types.
 */
public final class TaskTypeHelper
{
   /**
    * Retrieve a task type by its integer value.
    *
    * @param type task type value
    * @return TaskType instance
    */
   public static TaskType getInstance(int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         return TaskType.FIXED_WORK;
      }
      return TYPE_VALUES[type];
   }

   /**
    * Retrieve the integer value representing a task type.
    *
    * @param type TaskType instance
    * @return integer value
    */
   public static int getValue(TaskType type)
   {
      if (type == null)
      {
         type = TaskType.FIXED_UNITS;
      }

      return TYPE_MAP.get(type).intValue();
   }

   private static final TaskType[] TYPE_VALUES =
   {
      TaskType.FIXED_UNITS,
      TaskType.FIXED_DURATION,
      TaskType.FIXED_WORK
   };

   private static final Map<TaskType, Integer> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put(TaskType.FIXED_UNITS, Integer.valueOf(0));
      TYPE_MAP.put(TaskType.FIXED_DURATION, Integer.valueOf(1));
      TYPE_MAP.put(TaskType.FIXED_WORK, Integer.valueOf(2));
      TYPE_MAP.put(TaskType.FIXED_DURATION_AND_UNITS, Integer.valueOf(1));
   }
}
