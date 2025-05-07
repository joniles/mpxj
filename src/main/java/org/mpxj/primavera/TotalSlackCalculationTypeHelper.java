/*
 * file:       TotalSlackCalculationTypeHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       18/08/2023
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

import org.mpxj.TotalSlackCalculationType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of total slack calculation types.
 */
final class TotalSlackCalculationTypeHelper
{
   /**
    * Retrieve a calculation type by its value from a PMXML file.
    *
    * @param value constraint type value
    * @return ConstraintType instance
    */
   public static TotalSlackCalculationType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a calculation type by its value from an XER file or P6 database.
    *
    * @param value constraint type value
    * @return ConstraintType instance
    */
   public static TotalSlackCalculationType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a calculation type in a PMXML file.
    *
    * @param value ConstraintType instance
    * @return string value
    */
   public static String getXmlFromInstance(TotalSlackCalculationType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a calculation type in an XER file.
    *
    * @param value ConstraintType instance
    * @return string value
    */
   public static String getXerFromInstance(TotalSlackCalculationType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, TotalSlackCalculationType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Smallest of Start Float and Finish Float", TotalSlackCalculationType.SMALLEST_SLACK);
      XML_TYPE_MAP.put("Start Float = Late Start - Early Start", TotalSlackCalculationType.START_SLACK);
      XML_TYPE_MAP.put("Finish Float = Late Finish - Early Finish", TotalSlackCalculationType.FINISH_SLACK);
   }

   private static final Map<TotalSlackCalculationType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(TotalSlackCalculationType.SMALLEST_SLACK, "Smallest of Start Float and Finish Float");
      TYPE_XML_MAP.put(TotalSlackCalculationType.START_SLACK, "Start Float = Late Start - Early Start");
      TYPE_XML_MAP.put(TotalSlackCalculationType.FINISH_SLACK, "Finish Float = Late Finish - Early Finish");
   }

   private static final Map<String, TotalSlackCalculationType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("FT_Min", TotalSlackCalculationType.SMALLEST_SLACK);
      XER_TYPE_MAP.put("FT_SS", TotalSlackCalculationType.START_SLACK);
      XER_TYPE_MAP.put("FT_FF", TotalSlackCalculationType.FINISH_SLACK);
   }

   private static final Map<TotalSlackCalculationType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(TotalSlackCalculationType.SMALLEST_SLACK, "FT_Min");
      TYPE_XER_MAP.put(TotalSlackCalculationType.START_SLACK, "FT_SS");
      TYPE_XER_MAP.put(TotalSlackCalculationType.FINISH_SLACK, "FT_FF");
   }
}
