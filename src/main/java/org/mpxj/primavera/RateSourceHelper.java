/*
 * file:       RateSourceHelper.java
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

import org.mpxj.RateSource;

/**
 * Provides methods to convert to and from Primavera's representation
 * of rate source.
 */
final class RateSourceHelper
{
   /**
    * Retrieve a rate source by its value from a PMXML file.
    *
    * @param value rate source value
    * @return RateSource instance
    */
   public static RateSource getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.getOrDefault(value, RateSource.RESOURCE);
   }

   /**
    * Retrieve a rate source by its value from an XER file or P6 database.
    *
    * @param value rate source value
    * @return RateSource instance
    */
   public static RateSource getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.getOrDefault(value, RateSource.RESOURCE);
   }

   /**
    * Retrieve the string value representing a rate source in a PMXML file.
    *
    * @param value RateSource instance
    * @return string value
    */
   public static String getXmlFromInstance(RateSource value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a rate source in an XER file.
    *
    * @param value RateSource instance
    * @return string value
    */
   public static String getXerFromInstance(RateSource value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, RateSource> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Resource", RateSource.RESOURCE);
      XML_TYPE_MAP.put("Role", RateSource.ROLE);
      XML_TYPE_MAP.put("Override", RateSource.OVERRIDE);
   }

   private static final Map<String, RateSource> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("ST_Rsrc", RateSource.RESOURCE);
      XER_TYPE_MAP.put("ST_Role", RateSource.ROLE);
      XER_TYPE_MAP.put("ST_Custom", RateSource.OVERRIDE);
   }

   private static final Map<RateSource, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(RateSource.RESOURCE, "Resource");
      TYPE_XML_MAP.put(RateSource.OVERRIDE, "Override");
      TYPE_XML_MAP.put(RateSource.ROLE, "Role");
   }

   private static final Map<RateSource, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(RateSource.RESOURCE, "ST_Rsrc");
      TYPE_XER_MAP.put(RateSource.ROLE, "ST_Role");
      TYPE_XER_MAP.put(RateSource.OVERRIDE, "ST_Custom");
   }
}
