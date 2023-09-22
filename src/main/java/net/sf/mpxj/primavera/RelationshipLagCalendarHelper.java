package net.sf.mpxj.primavera;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.RelationshipLagCalendar;

final class RelationshipLagCalendarHelper
{
   public static RelationshipLagCalendar getInstanceFromXml(String value) {
      return XML_TYPE_MAP.getOrDefault(value, RelationshipLagCalendar.PREDECESSOR);
   }

   public static RelationshipLagCalendar getInstanceFromXer(String value) {
      return XER_TYPE_MAP.getOrDefault(value, RelationshipLagCalendar.PREDECESSOR);
   }

   public static String getXmlFromInstance(RelationshipLagCalendar value) {
      return TYPE_XML_MAP.get(value);
   }

   public static String getXerFromInstance(RelationshipLagCalendar value) {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, RelationshipLagCalendar> XML_TYPE_MAP = new HashMap<>();
   static {
      XML_TYPE_MAP.put("Predecessor Activity Calendar", RelationshipLagCalendar.PREDECESSOR);
      XML_TYPE_MAP.put("Successor Activity Calendar", RelationshipLagCalendar.SUCCESSOR);
      XML_TYPE_MAP.put("Project Default Calendar", RelationshipLagCalendar.PROJECT_DEFAULT);
      XML_TYPE_MAP.put("24 Hour Calendar", RelationshipLagCalendar.TWENTY_FOUR_HOUR);
   }

   private static final Map<RelationshipLagCalendar, String> TYPE_XML_MAP = new HashMap<>();
   static {
      TYPE_XML_MAP.put(RelationshipLagCalendar.PREDECESSOR, "Predecessor Activity Calendar");
      TYPE_XML_MAP.put(RelationshipLagCalendar.SUCCESSOR, "Successor Activity Calendar");
      TYPE_XML_MAP.put(RelationshipLagCalendar.PROJECT_DEFAULT, "Project Default Calendar");
      TYPE_XML_MAP.put(RelationshipLagCalendar.TWENTY_FOUR_HOUR, "24 Hour Calendar");
   }

   private static final Map<String, RelationshipLagCalendar> XER_TYPE_MAP = new HashMap<>();
   static {
      XER_TYPE_MAP.put("rcal_Predecessor", RelationshipLagCalendar.PREDECESSOR);
      XER_TYPE_MAP.put("rcal_Successor", RelationshipLagCalendar.SUCCESSOR);
      XER_TYPE_MAP.put("rcal_ProjDefault", RelationshipLagCalendar.PROJECT_DEFAULT);
      XER_TYPE_MAP.put("rcal_24hour", RelationshipLagCalendar.TWENTY_FOUR_HOUR);
   }

   private static final Map<RelationshipLagCalendar, String> TYPE_XER_MAP = new HashMap<>();
   static {
      TYPE_XER_MAP.put(RelationshipLagCalendar.PREDECESSOR, "rcal_Predecessor");
      TYPE_XER_MAP.put(RelationshipLagCalendar.SUCCESSOR, "rcal_Successor");
      TYPE_XER_MAP.put(RelationshipLagCalendar.PROJECT_DEFAULT, "rcal_ProjDefault");
      TYPE_XER_MAP.put(RelationshipLagCalendar.TWENTY_FOUR_HOUR, "rcal_24hour");
   }
}
