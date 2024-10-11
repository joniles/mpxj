package net.sf.mpxj.primavera;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.SkillLevel;

final class SkillLevelHelper
{
   /**
    * Retrieve a skill level by its value from a PMXML file.
    *
    * @param value skill level value
    * @return skill level
    */
//   public static SkillLevel getInstanceFromXml(String value)
//   {
//      return XML_TYPE_MAP.getOrDefault(value, DEFAULT_VALUE);
//   }

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
//   public static String getXmlFromInstance(SkillLevel value)
//   {
//      if (value == null || value.intValue() < 0 || value.intValue() >= TYPE_XML_ARRAY.length)
//      {
//         value = DEFAULT_VALUE;
//      }
//
//      return TYPE_XML_ARRAY[value.intValue()];
//   }

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
}
