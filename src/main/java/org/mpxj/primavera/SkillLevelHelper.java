/*
 * file:       SkillLevelHelper.java
 * author:     Jon Iles
 * date:       2024-10-11
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

import org.mpxj.SkillLevel;

/**
 * Provides methods to convert to and from Primavera's representation
 * of skill level.
 */
final class SkillLevelHelper
{
   /**
    * Retrieve a skill level by its value from a PMXML file.
    *
    * @param value skill level value
    * @return skill level
    */
   public static SkillLevel getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.getOrDefault(value, DEFAULT_VALUE);
   }

   /**
    * Retrieve a skill level by its value from an XER file or P6 database.
    *
    * @param value skill level value
    * @return skill level
    */
   public static SkillLevel getInstanceFromXer(Integer value)
   {
      if (value == null || value.intValue() < 0 || value.intValue() >= XER_TYPE_ARRAY.length)
      {
         return DEFAULT_VALUE;
      }

      return XER_TYPE_ARRAY[value.intValue()];
   }

   /**
    * Retrieve the value representing a skill level in a PMXML file.
    *
    * @param value rate type index
    * @return string value
    */
   public static String getXmlFromInstance(SkillLevel value)
   {
      if (value == null)
      {
         value = DEFAULT_VALUE;
      }

      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a rate type in an XER file.
    *
    * @param value rate type index
    * @return string value
    */
   public static String getXerFromInstance(SkillLevel value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final SkillLevel DEFAULT_VALUE = SkillLevel.SKILLED;

   private static final SkillLevel[] XER_TYPE_ARRAY = new SkillLevel[]
   {
      null,
      SkillLevel.MASTER,
      SkillLevel.EXPERT,
      SkillLevel.SKILLED,
      SkillLevel.PROFICIENT,
      SkillLevel.INEXPERIENCED
   };

   private static final Map<SkillLevel, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(SkillLevel.MASTER, "1");
      TYPE_XER_MAP.put(SkillLevel.EXPERT, "2");
      TYPE_XER_MAP.put(SkillLevel.SKILLED, "3");
      TYPE_XER_MAP.put(SkillLevel.PROFICIENT, "4");
      TYPE_XER_MAP.put(SkillLevel.INEXPERIENCED, "5");
   }

   private static final Map<String, SkillLevel> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("1 - Master", SkillLevel.MASTER);
      XML_TYPE_MAP.put("2 - Expert", SkillLevel.EXPERT);
      XML_TYPE_MAP.put("3 - Skilled", SkillLevel.SKILLED);
      XML_TYPE_MAP.put("4 - Proficient", SkillLevel.PROFICIENT);
      XML_TYPE_MAP.put("5 - Inexperienced", SkillLevel.INEXPERIENCED);
   }

   private static final Map<SkillLevel, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(SkillLevel.MASTER, "1 - Master");
      TYPE_XML_MAP.put(SkillLevel.EXPERT, "2 - Expert");
      TYPE_XML_MAP.put(SkillLevel.SKILLED, "3 - Skilled");
      TYPE_XML_MAP.put(SkillLevel.PROFICIENT, "4 - Proficient");
      TYPE_XML_MAP.put(SkillLevel.INEXPERIENCED, "5 - Inexperienced");
   }
}
