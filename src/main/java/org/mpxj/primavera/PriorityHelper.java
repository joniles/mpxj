/*
 * file:       PriorityHelper.java
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

import org.mpxj.Priority;

/**
 * Provides methods to convert to and from Primavera's representation
 * of activity priority values.
 */
final class PriorityHelper
{
   /**
    * Retrieve a priority by its value from a PMXML file.
    *
    * @param value priority value
    * @return Priority instance
    */
   public static Priority getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a priority by its value from an XER file or P6 database.
    *
    * @param value priority value
    * @return Priority instance
    */
   public static Priority getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a priority in a PMXML file.
    *
    * @param value Priority instance
    * @return string value
    */
   public static String getXmlFromInstance(Priority value)
   {
      return TYPE_XML_MAP.get(getNamedInstance(value));
   }

   /**
    * Retrieve the string value representing a priority in an XER file.
    *
    * @param value Priority instance
    * @return string value
    */
   public static String getXerFromInstance(Priority value)
   {
      return TYPE_XER_MAP.get(value);
   }

   /**
    * Convert an arbitrary integer priority value into one of the named priority values.
    *
    * @param priority Priority instance
    * @return name Priority instance
    */
   private static Priority getNamedInstance(Priority priority)
   {
      if (priority == null)
      {
         return Priority.getInstance(Priority.MEDIUM);
      }

      int value = ((priority.getValue() + 50) / 100) * 100;
      if (value > Priority.HIGHEST)
      {
         value = Priority.HIGHEST;
      }

      return Priority.getInstance(value);
   }

   private static final Map<String, Priority> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Top", Priority.getInstance(Priority.HIGHEST));
      XML_TYPE_MAP.put("High", Priority.getInstance(Priority.HIGH));
      XML_TYPE_MAP.put("Normal", Priority.getInstance(Priority.MEDIUM));
      XML_TYPE_MAP.put("Low", Priority.getInstance(Priority.LOW));
      XML_TYPE_MAP.put("Lowest", Priority.getInstance(Priority.LOWEST));
   }

   private static final Map<Priority, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(Priority.getInstance(Priority.HIGHEST), "Top");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.VERY_HIGH), "Top");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.HIGHER), "High");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.HIGH), "High");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.MEDIUM), "Normal");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.LOW), "Low");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.LOWER), "Low");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.VERY_LOW), "Lowest");
      TYPE_XML_MAP.put(Priority.getInstance(Priority.LOWEST), "Lowest");
   }

   private static final Map<String, Priority> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("PT_Top", Priority.getInstance(Priority.HIGHEST));
      XER_TYPE_MAP.put("PT_High", Priority.getInstance(Priority.HIGH));
      XER_TYPE_MAP.put("PT_Normal", Priority.getInstance(Priority.MEDIUM));
      XER_TYPE_MAP.put("PT_Low", Priority.getInstance(Priority.LOW));
      XER_TYPE_MAP.put("PT_Lowest", Priority.getInstance(Priority.LOWEST));
   }

   private static final Map<Priority, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(Priority.getInstance(Priority.HIGHEST), "PT_Top");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.VERY_HIGH), "PT_Top");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.HIGHER), "PT_High");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.HIGH), "PT_High");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.MEDIUM), "PT_Normal");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.LOW), "PT_Low");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.LOWER), "PT_Low");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.VERY_LOW), "PT_Lowest");
      TYPE_XER_MAP.put(Priority.getInstance(Priority.LOWEST), "PT_Lowest");
   }
}
