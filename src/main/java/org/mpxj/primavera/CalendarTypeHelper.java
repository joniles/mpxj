/*
 * file:       CalendarTypeHelper.java
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

import org.mpxj.CalendarType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of calendar types.
 */
final class CalendarTypeHelper
{
   /**
    * Retrieve a calendar type by its value from a PMXML file.
    *
    * @param value calendar type value
    * @return CalendarType instance
    */
   public static CalendarType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a calendar type by its value from an XER file or P6 database.
    *
    * @param value calendar type value
    * @return CalendarType instance
    */
   public static CalendarType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a calendar type in a PMXML file.
    *
    * @param value CalendarType instance
    * @return string value
    */
   public static String getXmlFromInstance(CalendarType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a calendar type in an XER file.
    *
    * @param value CalendarType instance
    * @return string value
    */
   public static String getXerFromInstance(CalendarType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, CalendarType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Global", CalendarType.GLOBAL);
      XML_TYPE_MAP.put("Project", CalendarType.PROJECT);
      XML_TYPE_MAP.put("Resource", CalendarType.RESOURCE);
   }

   private static final Map<CalendarType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(CalendarType.GLOBAL, "Global");
      TYPE_XML_MAP.put(CalendarType.PROJECT, "Project");
      TYPE_XML_MAP.put(CalendarType.RESOURCE, "Resource");
   }

   private static final Map<String, CalendarType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("CA_Base", CalendarType.GLOBAL);
      XER_TYPE_MAP.put("CA_Project", CalendarType.PROJECT);
      XER_TYPE_MAP.put("CA_Rsrc", CalendarType.RESOURCE);
   }

   private static final Map<CalendarType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(CalendarType.GLOBAL, "CA_Base");
      TYPE_XER_MAP.put(CalendarType.PROJECT, "CA_Project");
      TYPE_XER_MAP.put(CalendarType.RESOURCE, "CA_Rsrc");
   }
}
