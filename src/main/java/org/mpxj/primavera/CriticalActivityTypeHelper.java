/*
 * file:       CriticalActivityTypeHelper.java
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

import org.mpxj.CriticalActivityType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of critical activity types.
 */
final class CriticalActivityTypeHelper
{
   /**
    * Retrieve a critical activity type by its value from a PMXML file.
    *
    * @param value critical activity type value
    * @return CriticalActivityType instance
    */
   public static CriticalActivityType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.getOrDefault(value, CriticalActivityType.TOTAL_FLOAT);
   }

   /**
    * Retrieve a critical activity type by its value from an XER file or P6 database.
    *
    * @param value critical activity type value
    * @return CriticalActivityType instance
    */
   public static CriticalActivityType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.getOrDefault(value, CriticalActivityType.TOTAL_FLOAT);
   }

   /**
    * Retrieve the string value representing a critical activity type in a PMXML file.
    *
    * @param value CriticalActivityType instance
    * @return string value
    */
   public static String getXmlFromInstance(CriticalActivityType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a critical activity type in an XER file.
    *
    * @param value CriticalActivityType instance
    * @return string value
    */
   public static String getXerFromInstance(CriticalActivityType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, CriticalActivityType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Critical Float", CriticalActivityType.TOTAL_FLOAT);
      XML_TYPE_MAP.put("Longest Path", CriticalActivityType.LONGEST_PATH);
   }

   private static final Map<CriticalActivityType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(CriticalActivityType.TOTAL_FLOAT, "Critical Float");
      TYPE_XML_MAP.put(CriticalActivityType.LONGEST_PATH, "Longest Path");
   }

   private static final Map<String, CriticalActivityType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("CT_TotFloat", CriticalActivityType.TOTAL_FLOAT);
      XER_TYPE_MAP.put("CT_DrivPath", CriticalActivityType.LONGEST_PATH);
   }

   private static final Map<CriticalActivityType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(CriticalActivityType.TOTAL_FLOAT, "CT_TotFloat");
      TYPE_XER_MAP.put(CriticalActivityType.LONGEST_PATH, "CT_DrivPath");
   }
}
