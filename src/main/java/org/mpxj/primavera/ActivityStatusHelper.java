/*
 * file:       ActivityStatusHelper.java
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

import org.mpxj.ActivityStatus;
import org.mpxj.Task;

/**
 * Provides methods to convert to and from Primavera's representation
 * of activity status.
 */
final class ActivityStatusHelper
{
   /**
    * Retrieve an activity status.
    *
    * @param mpxj MPXJ Task instance
    * @return activity status
    */
   public static ActivityStatus getActivityStatus(Task mpxj)
   {
      ActivityStatus result;
      if (mpxj.getActualStart() == null)
      {
         result = ActivityStatus.NOT_STARTED;
      }
      else
      {
         if (mpxj.getActualFinish() == null)
         {
            result = ActivityStatus.IN_PROGRESS;
         }
         else
         {
            result = ActivityStatus.COMPLETED;
         }
      }
      return result;
   }

   /**
    * Retrieve an activity status by its value from a PMXML file.
    *
    * @param value activity status value
    * @return ActivityStatus instance
    */
   public static ActivityStatus getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve an activity status by its value from an XER file or P6 database.
    *
    * @param value activity status value
    * @return ActivityStatus instance
    */
   public static ActivityStatus getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an activity status in a PMXML file.
    *
    * @param value ActivityStatus instance
    * @return string value
    */
   public static String getXmlFromInstance(ActivityStatus value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an activity status in an XER file.
    *
    * @param value ActivityStatus instance
    * @return string value
    */
   public static String getXerFromInstance(ActivityStatus value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, ActivityStatus> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Not Started", ActivityStatus.NOT_STARTED);
      XML_TYPE_MAP.put("In Progress", ActivityStatus.IN_PROGRESS);
      XML_TYPE_MAP.put("Completed", ActivityStatus.COMPLETED);
   }

   private static final Map<ActivityStatus, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(ActivityStatus.NOT_STARTED, "Not Started");
      TYPE_XML_MAP.put(ActivityStatus.IN_PROGRESS, "In Progress");
      TYPE_XML_MAP.put(ActivityStatus.COMPLETED, "Completed");
   }

   private static final Map<String, ActivityStatus> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("TK_NotStart", ActivityStatus.NOT_STARTED);
      XER_TYPE_MAP.put("TK_Active", ActivityStatus.IN_PROGRESS);
      XER_TYPE_MAP.put("TK_Complete", ActivityStatus.COMPLETED);
   }

   private static final Map<ActivityStatus, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(ActivityStatus.NOT_STARTED, "TK_NotStart");
      TYPE_XER_MAP.put(ActivityStatus.IN_PROGRESS, "TK_Active");
      TYPE_XER_MAP.put(ActivityStatus.COMPLETED, "TK_Complete");
   }
}
