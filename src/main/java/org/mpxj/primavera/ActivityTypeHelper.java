/*
 * file:       ActivityTypeHelper.java
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

import org.mpxj.ActivityType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of activity types.
 */
final class ActivityTypeHelper
{
   /**
    * Retrieve an activity type by its value from a PMXML file.
    *
    * @param value activity type value
    * @return ActivityType instance
    */
   public static ActivityType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve an activity type by its value from an XER file or P6 database.
    *
    * @param value activity type value
    * @return ActivityType instance
    */
   public static ActivityType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an activity type in a PMXML file.
    *
    * @param value ActivityType instance
    * @return string value
    */
   public static String getXmlFromInstance(ActivityType value)
   {
      if (value == null)
      {
         value = EXISTING_ACTIVITY_DEFAULT_TYPE;
      }

      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an activity type in an XER file.
    *
    * @param value ActivityType instance
    * @return string value
    */
   public static String getXerFromInstance(ActivityType value)
   {
      if (value == null)
      {
         value = EXISTING_ACTIVITY_DEFAULT_TYPE;
      }

      return TYPE_XER_MAP.get(value);
   }

   public static final ActivityType NEW_ACTIVITY_DEFAULT_TYPE = ActivityType.TASK_DEPENDENT;
   public static final ActivityType EXISTING_ACTIVITY_DEFAULT_TYPE = ActivityType.RESOURCE_DEPENDENT;

   private static final Map<String, ActivityType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Task Dependent", ActivityType.TASK_DEPENDENT);
      XML_TYPE_MAP.put("Resource Dependent", ActivityType.RESOURCE_DEPENDENT);
      XML_TYPE_MAP.put("Level of Effort", ActivityType.LEVEL_OF_EFFORT);
      XML_TYPE_MAP.put("Start Milestone", ActivityType.START_MILESTONE);
      XML_TYPE_MAP.put("Finish Milestone", ActivityType.FINISH_MILESTONE);
      XML_TYPE_MAP.put("WBS Summary", ActivityType.WBS_SUMMARY);

      // Version 6.1 schema. Need access to prmbo.xsd to confirm other values
      XML_TYPE_MAP.put("0", ActivityType.TASK_DEPENDENT);
   }

   private static final Map<ActivityType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(ActivityType.TASK_DEPENDENT, "Task Dependent");
      TYPE_XML_MAP.put(ActivityType.RESOURCE_DEPENDENT, "Resource Dependent");
      TYPE_XML_MAP.put(ActivityType.LEVEL_OF_EFFORT, "Level of Effort");
      TYPE_XML_MAP.put(ActivityType.START_MILESTONE, "Start Milestone");
      TYPE_XML_MAP.put(ActivityType.FINISH_MILESTONE, "Finish Milestone");
      TYPE_XML_MAP.put(ActivityType.WBS_SUMMARY, "WBS Summary");
      TYPE_XML_MAP.put(ActivityType.START_FLAG, "Start Milestone");
      TYPE_XML_MAP.put(ActivityType.FINISH_FLAG, "Finish Milestone");
      TYPE_XML_MAP.put(ActivityType.HAMMOCK, "Resource Dependent");
   }

   private static final Map<String, ActivityType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("TT_Task", ActivityType.TASK_DEPENDENT);
      XER_TYPE_MAP.put("TT_Rsrc", ActivityType.RESOURCE_DEPENDENT);
      XER_TYPE_MAP.put("TT_LOE", ActivityType.LEVEL_OF_EFFORT);
      XER_TYPE_MAP.put("TT_Mile", ActivityType.START_MILESTONE);
      XER_TYPE_MAP.put("TT_FinMile", ActivityType.FINISH_MILESTONE);
      XER_TYPE_MAP.put("TT_WBS", ActivityType.WBS_SUMMARY);
   }

   private static final Map<ActivityType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(ActivityType.TASK_DEPENDENT, "TT_Task");
      TYPE_XER_MAP.put(ActivityType.RESOURCE_DEPENDENT, "TT_Rsrc");
      TYPE_XER_MAP.put(ActivityType.LEVEL_OF_EFFORT, "TT_LOE");
      TYPE_XER_MAP.put(ActivityType.START_MILESTONE, "TT_Mile");
      TYPE_XER_MAP.put(ActivityType.FINISH_MILESTONE, "TT_FinMile");
      TYPE_XER_MAP.put(ActivityType.WBS_SUMMARY, "TT_WBS");
   }
}
