/*
 * file:       RelationTypeHelper.java
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

import org.mpxj.RelationType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of relation types.
 */
final class RelationTypeHelper
{
   /**
    * Retrieve a relation type by its value from a PMXML file.
    *
    * @param value relation type value
    * @return RelationType instance
    */
   public static RelationType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a relation type by its value from an XER file or P6 database.
    *
    * @param value relation type value
    * @return RelationType instance
    */
   public static RelationType getInstanceFromXer(String value)
   {
      RelationType result = null;
      if (value != null)
      {
         // We have examples from XER files where the relation type is in the form
         // PR_FF1, PR_FF2 and so on. We'll try to handle this by stripping off any
         // suffix to determine the original relation type.
         if (value.length() > 5)
         {
            value = value.substring(0, 5);
         }
         result = XER_TYPE_MAP.get(value);
      }

      return result == null ? RelationType.FINISH_START : result;
   }

   /**
    * Retrieve the string value representing a relation type in a PMXML file.
    *
    * @param value RelationType instance
    * @return string value
    */
   public static String getXmlFromInstance(RelationType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a relation type in an XER file.
    *
    * @param value RelationType instance
    * @return string value
    */
   public static String getXerFromInstance(RelationType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, RelationType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Finish to Start", RelationType.FINISH_START);
      XML_TYPE_MAP.put("Finish to Finish", RelationType.FINISH_FINISH);
      XML_TYPE_MAP.put("Start to Start", RelationType.START_START);
      XML_TYPE_MAP.put("Start to Finish", RelationType.START_FINISH);
   }

   private static final Map<RelationType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(RelationType.FINISH_START, "Finish to Start");
      TYPE_XML_MAP.put(RelationType.FINISH_FINISH, "Finish to Finish");
      TYPE_XML_MAP.put(RelationType.START_START, "Start to Start");
      TYPE_XML_MAP.put(RelationType.START_FINISH, "Start to Finish");
   }

   private static final Map<String, RelationType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("PR_FS", RelationType.FINISH_START);
      XER_TYPE_MAP.put("PR_FF", RelationType.FINISH_FINISH);
      XER_TYPE_MAP.put("PR_SS", RelationType.START_START);
      XER_TYPE_MAP.put("PR_SF", RelationType.START_FINISH);
   }

   private static final Map<RelationType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(RelationType.FINISH_START, "PR_FS");
      TYPE_XER_MAP.put(RelationType.FINISH_FINISH, "PR_FF");
      TYPE_XER_MAP.put(RelationType.START_START, "PR_SS");
      TYPE_XER_MAP.put(RelationType.START_FINISH, "PR_SF");
   }
}
