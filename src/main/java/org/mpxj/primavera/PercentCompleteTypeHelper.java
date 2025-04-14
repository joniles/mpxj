/*
 * file:       PercentCompleteTypeHelper.java
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

import org.mpxj.PercentCompleteType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of percent complete types.
 */
final class PercentCompleteTypeHelper
{
   /**
    * Retrieve a percent complete type by its value from a PMXML file.
    *
    * @param value task type value
    * @return PercentCompleteType instance
    */
   public static PercentCompleteType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a percent complete type by its value from an XER file or P6 database.
    *
    * @param value percent complete type value
    * @return PercentCompleteType instance
    */
   public static PercentCompleteType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a percent complete type in a PMXML file.
    *
    * @param value PercentCompleteType instance
    * @return string value
    */
   public static String getXmlFromInstance(PercentCompleteType value)
   {
      if (value == null)
      {
         value = PercentCompleteType.DURATION;
      }

      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a percent complete type in an XER file.
    *
    * @param value PercentCompleteType instance
    * @return string value
    */
   public static String getXerFromInstance(PercentCompleteType value)
   {
      if (value == null)
      {
         value = PercentCompleteType.DURATION;
      }

      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, PercentCompleteType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Physical", PercentCompleteType.PHYSICAL);
      XML_TYPE_MAP.put("Duration", PercentCompleteType.DURATION);
      XML_TYPE_MAP.put("Units", PercentCompleteType.UNITS);
      XML_TYPE_MAP.put("Scope", PercentCompleteType.SCOPE);
   }

   private static final Map<PercentCompleteType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(PercentCompleteType.PHYSICAL, "Physical");
      TYPE_XML_MAP.put(PercentCompleteType.DURATION, "Duration");
      TYPE_XML_MAP.put(PercentCompleteType.UNITS, "Units");
      TYPE_XML_MAP.put(PercentCompleteType.SCOPE, "Scope");
   }

   private static final Map<String, PercentCompleteType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("CP_Phys", PercentCompleteType.PHYSICAL);
      XER_TYPE_MAP.put("CP_Drtn", PercentCompleteType.DURATION);
      XER_TYPE_MAP.put("CP_Units", PercentCompleteType.UNITS);
   }

   private static final Map<PercentCompleteType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(PercentCompleteType.PHYSICAL, "CP_Phys");
      TYPE_XER_MAP.put(PercentCompleteType.DURATION, "CP_Drtn");
      TYPE_XER_MAP.put(PercentCompleteType.UNITS, "CP_Units");
   }
}
