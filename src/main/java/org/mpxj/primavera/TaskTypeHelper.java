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

package org.mpxj.primavera;

import java.util.HashMap;
import java.util.Map;

import org.mpxj.TaskType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of task types.
 */
final class TaskTypeHelper
{
   /**
    * Retrieve a task type by its value from a PMXML file.
    *
    * @param value task type value
    * @return TaskType instance
    */
   public static TaskType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a task type by its value from an XER file or P6 database.
    *
    * @param value task type value
    * @return TaskType instance
    */
   public static TaskType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a task type in a PMXML file.
    *
    * @param value TaskType instance
    * @return string value
    */
   public static String getXmlFromInstance(TaskType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a task type in an XER file.
    *
    * @param value TaskType instance
    * @return string value
    */
   public static String getXerFromInstance(TaskType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, TaskType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Fixed Units/Time", TaskType.FIXED_UNITS);
      XML_TYPE_MAP.put("Fixed Duration and Units/Time", TaskType.FIXED_DURATION);
      XML_TYPE_MAP.put("Fixed Units", TaskType.FIXED_WORK);
      XML_TYPE_MAP.put("Fixed Duration and Units", TaskType.FIXED_DURATION_AND_UNITS);
   }

   private static final Map<String, TaskType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("DT_FixedRate", TaskType.FIXED_UNITS); // Fixed Units/Time
      XER_TYPE_MAP.put("DT_FixedDrtn", TaskType.FIXED_DURATION); // Fixed Duration and Units/Time
      XER_TYPE_MAP.put("DT_FixedQty", TaskType.FIXED_WORK); // Fixed Units
      XER_TYPE_MAP.put("DT_FixedDUR2", TaskType.FIXED_DURATION_AND_UNITS); // Fixed Duration & Units
   }

   private static final Map<TaskType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(TaskType.FIXED_UNITS, "Fixed Units/Time");
      TYPE_XML_MAP.put(TaskType.FIXED_DURATION, "Fixed Duration and Units/Time");
      TYPE_XML_MAP.put(TaskType.FIXED_WORK, "Fixed Units");
      TYPE_XML_MAP.put(TaskType.FIXED_DURATION_AND_UNITS, "Fixed Duration and Units");
   }

   private static final Map<TaskType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(TaskType.FIXED_UNITS, "DT_FixedRate"); // Fixed Units/Time
      TYPE_XER_MAP.put(TaskType.FIXED_DURATION, "DT_FixedDrtn"); // Fixed Duration and Units/Time
      TYPE_XER_MAP.put(TaskType.FIXED_WORK, "DT_FixedQty"); // Fixed Units
      TYPE_XER_MAP.put(TaskType.FIXED_DURATION_AND_UNITS, "DT_FixedDUR2"); // Fixed Duration & Units
   }
}
