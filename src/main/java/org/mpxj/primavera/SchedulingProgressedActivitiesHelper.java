/*
 * file:       SchedulingProgressedActivitiesHelper.java
 * author:     Jon Iles
 * date:       2023-12-05
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

import org.mpxj.SchedulingProgressedActivities;

/**
 * Provides methods to convert to and from Primavera's representation
 * of the method used to schedule progressed activities.
 */
final class SchedulingProgressedActivitiesHelper
{
   /**
    * Retrieve an activity code scope by its value from a PMXML file.
    *
    * @param value activity code scope value
    * @return ActivityCodeScope instance
    */
   public static SchedulingProgressedActivities getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an activity code scope in a PMXML file.
    *
    * @param value ActivityCodeScope instance
    * @return string value
    */
   public static String getXmlFromInstance(SchedulingProgressedActivities value)
   {
      return TYPE_XML_MAP.get(value);
   }

   private static final Map<String, SchedulingProgressedActivities> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Retained Logic", SchedulingProgressedActivities.RETAINED_LOGIC);
      XML_TYPE_MAP.put("Progress Override", SchedulingProgressedActivities.PROGRESS_OVERRIDE);
      XML_TYPE_MAP.put("Actual Dates", SchedulingProgressedActivities.ACTUAL_DATES);
   }

   private static final Map<SchedulingProgressedActivities, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(SchedulingProgressedActivities.RETAINED_LOGIC, "Retained Logic");
      TYPE_XML_MAP.put(SchedulingProgressedActivities.PROGRESS_OVERRIDE, "Progress Override");
      TYPE_XML_MAP.put(SchedulingProgressedActivities.ACTUAL_DATES, "Actual Dates");
   }
}
