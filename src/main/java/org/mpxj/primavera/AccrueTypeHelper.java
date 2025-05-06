/*
 * file:       AccrueTypeHelper.java
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

import org.mpxj.AccrueType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of accrue types.
 */
final class AccrueTypeHelper
{
   /**
    * Retrieve an accrue type by its value from a PMXML file.
    *
    * @param value accrue type value
    * @return AccrueType instance
    */
   public static AccrueType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve an accrue type by its value from an XER file or P6 database.
    *
    * @param value accrue type value
    * @return AccrueType instance
    */
   public static AccrueType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an accrue type in a PMXML file.
    *
    * @param value AccrueType instance
    * @return string value
    */
   public static String getXmlFromInstance(AccrueType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing an accrue type in an XER file.
    *
    * @param value AccrueType instance
    * @return string value
    */
   public static String getXerFromInstance(AccrueType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, AccrueType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Uniform Over Activity", AccrueType.PRORATED);
      XML_TYPE_MAP.put("End of Activity", AccrueType.END);
      XML_TYPE_MAP.put("Start of Activity", AccrueType.START);
   }

   private static final Map<String, AccrueType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("CL_Uniform", AccrueType.PRORATED);
      XER_TYPE_MAP.put("CL_End", AccrueType.END);
      XER_TYPE_MAP.put("CL_Start", AccrueType.START);
   }

   private static final Map<AccrueType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(AccrueType.PRORATED, "Uniform Over Activity");
      TYPE_XML_MAP.put(AccrueType.END, "End of Activity");
      TYPE_XML_MAP.put(AccrueType.START, "Start of Activity");
   }

   private static final Map<AccrueType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(AccrueType.PRORATED, "CL_Uniform");
      TYPE_XER_MAP.put(AccrueType.END, "CL_End");
      TYPE_XER_MAP.put(AccrueType.START, "CL_Start");
   }
}
