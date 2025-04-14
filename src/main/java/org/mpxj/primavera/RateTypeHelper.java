/*
 * file:       RateTypeHelper.java
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

/**
 * Provides methods to convert to and from Primavera's representation
 * of rate types.
 */
final class RateTypeHelper
{
   /**
    * Retrieve a rate type index by its value from a PMXML file.
    *
    * @param value rate type value
    * @return rate type index
    */
   public static Integer getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.getOrDefault(value, DEFAULT_VALUE);
   }

   /**
    * Retrieve a rate type index by its value from an XER file or P6 database.
    *
    * @param value rate type value
    * @return rate type index
    */
   public static Integer getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.getOrDefault(value, DEFAULT_VALUE);
   }

   /**
    * Retrieve the string value representing a rate type in a PMXML file.
    *
    * @param value rate type index
    * @return string value
    */
   public static String getXmlFromInstance(Integer value)
   {
      if (value == null || value.intValue() < 0 || value.intValue() >= TYPE_XML_ARRAY.length)
      {
         value = DEFAULT_VALUE;
      }

      return TYPE_XML_ARRAY[value.intValue()];
   }

   /**
    * Retrieve the string value representing a rate type in an XER file.
    *
    * @param value rate type index
    * @return string value
    */
   public static String getXerFromInstance(Integer value)
   {
      if (value == null || value.intValue() < 0 || value.intValue() >= TYPE_XER_ARRAY.length)
      {
         value = DEFAULT_VALUE;
      }

      return TYPE_XER_ARRAY[value.intValue()];
   }

   private static final Integer DEFAULT_VALUE = Integer.valueOf(0);

   private static final Map<String, Integer> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Price / Unit", Integer.valueOf(0));
      XML_TYPE_MAP.put("Price / Unit 2", Integer.valueOf(1));
      XML_TYPE_MAP.put("Price / Unit 3", Integer.valueOf(2));
      XML_TYPE_MAP.put("Price / Unit 4", Integer.valueOf(3));
      XML_TYPE_MAP.put("Price / Unit 5", Integer.valueOf(4));
   }

   private static final Map<String, Integer> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("COST_PER_QTY", Integer.valueOf(0));
      XER_TYPE_MAP.put("COST_PER_QTY2", Integer.valueOf(1));
      XER_TYPE_MAP.put("COST_PER_QTY3", Integer.valueOf(2));
      XER_TYPE_MAP.put("COST_PER_QTY4", Integer.valueOf(3));
      XER_TYPE_MAP.put("COST_PER_QTY5", Integer.valueOf(4));
   }

   private static final String[] TYPE_XML_ARRAY = new String[]
   {
      "Price / Unit",
      "Price / Unit 2",
      "Price / Unit 3",
      "Price / Unit 4",
      "Price / Unit 5"
   };

   private static final String[] TYPE_XER_ARRAY = new String[]
   {
      "COST_PER_QTY",
      "COST_PER_QTY2",
      "COST_PER_QTY3",
      "COST_PER_QTY4",
      "COST_PER_QTY5",
   };
}
