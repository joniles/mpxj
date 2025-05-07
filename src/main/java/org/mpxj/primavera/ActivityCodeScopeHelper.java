/*
 * file:       ActivityCodeScopeHelper.java
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

import org.mpxj.ActivityCodeScope;

/**
 * Provides methods to convert to and from Primavera's representation
 * of activity code scopes.
 */
final class ActivityCodeScopeHelper
{
   /**
    * Retrieve an activity code scope by its value from a PMXML file.
    *
    * @param value activity code scope value
    * @return ActivityCodeScope instance
    */
   public static ActivityCodeScope getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve an activity code scope by its value from an XER file or P6 database.
    *
    * @param value activity code scope value
    * @return ActivityCodeScope instance
    */
   public static ActivityCodeScope getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an activity code scope in a PMXML file.
    *
    * @param value ActivityCodeScope instance
    * @return string value
    */
   public static String getXmlFromInstance(ActivityCodeScope value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an activity code scope in an XER file.
    *
    * @param value ActivityCodeScope instance
    * @return string value
    */
   public static String getXerFromInstance(ActivityCodeScope value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, ActivityCodeScope> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Global", ActivityCodeScope.GLOBAL);
      XML_TYPE_MAP.put("EPS", ActivityCodeScope.EPS);
      XML_TYPE_MAP.put("Project", ActivityCodeScope.PROJECT);
   }

   private static final Map<String, ActivityCodeScope> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("AS_Global", ActivityCodeScope.GLOBAL);
      XER_TYPE_MAP.put("AS_EPS", ActivityCodeScope.EPS);
      XER_TYPE_MAP.put("AS_Project", ActivityCodeScope.PROJECT);
   }

   private static final Map<ActivityCodeScope, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(ActivityCodeScope.GLOBAL, "Global");
      TYPE_XML_MAP.put(ActivityCodeScope.EPS, "EPS");
      TYPE_XML_MAP.put(ActivityCodeScope.PROJECT, "Project");
   }

   private static final Map<ActivityCodeScope, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(ActivityCodeScope.GLOBAL, "AS_Global");
      TYPE_XER_MAP.put(ActivityCodeScope.EPS, "AS_EPS");
      TYPE_XER_MAP.put(ActivityCodeScope.PROJECT, "AS_Project");
   }
}
