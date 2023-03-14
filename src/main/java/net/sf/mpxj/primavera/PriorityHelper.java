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

package net.sf.mpxj.primavera;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.Priority;

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
    * @return Priosity instance
    */
   public static Priority getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a priority by its value from an XER file or P6 database.
    *
    * @param value priority value
    * @return Priosity instance
    */
   public static Priority getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
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

   private static final Map<String, Priority> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("PT_Top", Priority.getInstance(Priority.HIGHEST));
      XER_TYPE_MAP.put("PT_High", Priority.getInstance(Priority.HIGH));
      XER_TYPE_MAP.put("PT_Normal", Priority.getInstance(Priority.MEDIUM));
      XER_TYPE_MAP.put("PT_Low", Priority.getInstance(Priority.LOW));
      XER_TYPE_MAP.put("PT_Lowest", Priority.getInstance(Priority.LOWEST));
   }
}
