/*
 * file:       RelationshipLagCalendar.java
 * author:     Rohit Sinha
 * date:       22/09/2023
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

import org.mpxj.RelationshipLagCalendar;

/**
 * Provides methods to convert to and from Primavera's representation
 * of the relationship lag calendar.
 */
final class RelationshipLagCalendarHelper
{
   /**
    * Retrieve the relationship lag calendar value by its value from a PMXML file.
    *
    * @param value relationship lag calendar value
    * @return RelationshipLagCalendar instance
    */
   public static RelationshipLagCalendar getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.getOrDefault(value, RelationshipLagCalendar.PREDECESSOR);
   }

   /**
    * Retrieve the relationship lag calendar value by its value from an XER file or P6 database.
    *
    * @param value relationship lag calendar value
    * @return RelationshipLagCalendar instance
    */
   public static RelationshipLagCalendar getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.getOrDefault(value, RelationshipLagCalendar.PREDECESSOR);
   }

   /**
    * Retrieve the string value representing a relationship lag calendar in a PMXML file.
    *
    * @param value relationship lag calendar value
    * @return string value
    */
   public static String getXmlFromInstance(RelationshipLagCalendar value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a relationship lag calendar in an XER file.
    *
    * @param value relationship lag calendar value
    * @return string value
    */
   public static String getXerFromInstance(RelationshipLagCalendar value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, RelationshipLagCalendar> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Predecessor Activity Calendar", RelationshipLagCalendar.PREDECESSOR);
      XML_TYPE_MAP.put("Successor Activity Calendar", RelationshipLagCalendar.SUCCESSOR);
      XML_TYPE_MAP.put("Project Default Calendar", RelationshipLagCalendar.PROJECT_DEFAULT);
      XML_TYPE_MAP.put("24 Hour Calendar", RelationshipLagCalendar.TWENTY_FOUR_HOUR);
   }

   private static final Map<RelationshipLagCalendar, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(RelationshipLagCalendar.PREDECESSOR, "Predecessor Activity Calendar");
      TYPE_XML_MAP.put(RelationshipLagCalendar.SUCCESSOR, "Successor Activity Calendar");
      TYPE_XML_MAP.put(RelationshipLagCalendar.PROJECT_DEFAULT, "Project Default Calendar");
      TYPE_XML_MAP.put(RelationshipLagCalendar.TWENTY_FOUR_HOUR, "24 Hour Calendar");
   }

   private static final Map<String, RelationshipLagCalendar> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("rcal_Predecessor", RelationshipLagCalendar.PREDECESSOR);
      XER_TYPE_MAP.put("rcal_Successor", RelationshipLagCalendar.SUCCESSOR);
      XER_TYPE_MAP.put("rcal_ProjDefault", RelationshipLagCalendar.PROJECT_DEFAULT);
      XER_TYPE_MAP.put("rcal_24Hour", RelationshipLagCalendar.TWENTY_FOUR_HOUR);
   }

   private static final Map<RelationshipLagCalendar, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(RelationshipLagCalendar.PREDECESSOR, "rcal_Predecessor");
      TYPE_XER_MAP.put(RelationshipLagCalendar.SUCCESSOR, "rcal_Successor");
      TYPE_XER_MAP.put(RelationshipLagCalendar.PROJECT_DEFAULT, "rcal_ProjDefault");
      TYPE_XER_MAP.put(RelationshipLagCalendar.TWENTY_FOUR_HOUR, "rcal_24Hour");
   }
}
