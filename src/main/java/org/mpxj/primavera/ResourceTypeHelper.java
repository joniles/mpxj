/*
 * file:       ResourceTypeHelper.java
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

import org.mpxj.ResourceType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of resource types.
 */
final class ResourceTypeHelper
{
   /**
    * Retrieve a resource type by its value from a PMXML file.
    *
    * @param value resource type value
    * @return ResourceType instance
    */
   public static ResourceType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a resource type by its value from an XER file or P6 database.
    *
    * @param value resource type value
    * @return ResourceType instance
    */
   public static ResourceType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a resource type in a PMXML file.
    *
    * @param value ResourceType instance
    * @return string value
    */
   public static String getXmlFromInstance(ResourceType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a resource type in an XER file.
    *
    * @param value ResourceType instance
    * @return string value
    */
   public static String getXerFromInstance(ResourceType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, ResourceType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put(null, ResourceType.WORK);
      XML_TYPE_MAP.put("Labor", ResourceType.WORK);
      XML_TYPE_MAP.put("Material", ResourceType.MATERIAL);
      XML_TYPE_MAP.put("Nonlabor", ResourceType.COST);
   }

   private static final Map<ResourceType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(ResourceType.WORK, "Labor");
      TYPE_XML_MAP.put(ResourceType.MATERIAL, "Material");
      TYPE_XML_MAP.put(ResourceType.COST, "Nonlabor");
   }

   private static final Map<String, ResourceType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put(null, ResourceType.WORK);
      XER_TYPE_MAP.put("RT_Labor", ResourceType.WORK);
      XER_TYPE_MAP.put("RT_Mat", ResourceType.MATERIAL);
      XER_TYPE_MAP.put("RT_Equip", ResourceType.COST);
   }

   private static final Map<ResourceType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(ResourceType.WORK, "RT_Labor");
      TYPE_XER_MAP.put(ResourceType.MATERIAL, "RT_Mat");
      TYPE_XER_MAP.put(ResourceType.COST, "RT_Equip");
   }
}
