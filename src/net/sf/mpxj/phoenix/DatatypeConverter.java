/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28/11/2015
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

package net.sf.mpxj.phoenix;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.TimeUnit;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read and write Phoenix files.
 */
public final class DatatypeConverter
{
   /**
    * Convert the Phoenix representation of a UUID into a Java UUID instance.
    * 
    * @param value Phoenix UUID
    * @return Java UUID instance
    */
   public static final UUID parseUUID(String value)
   {
      return UUID.fromString(value);
   }

   /**
    * Retrieve a UUID in the form required by Phoenix.
    * 
    * @param guid UUID instance
    * @return formatted UUID
    */
   public static String printUUID(UUID guid)
   {
      return guid.toString();
   }

   public static final String printInteger(Integer value)
   {
      return value == null ? null : value.toString();
   }

   public static final Integer parseInteger(String value)
   {
      return Integer.valueOf(value);
   }

   public static final ResourceType parseResourceType(String value)
   {
      return STRING_TO_RESOURCE_TYPE_MAP.get(value);
   }

   public static final String printResourceType(ResourceType type)
   {
      return RESOURCE_TYPE_TO_STRING_MAP.get(type);
   }

   public static final RelationType parseRelationType(String value)
   {
      return NAME_TO_RELATION_TYPE.get(value);
   }

   public static final String printRelationType(RelationType type)
   {
      return RELATION_TYPE_TO_NAME.get(type);
   }

   public static final TimeUnit parseTimeUnits(String value)
   {
      return STRING_TO_TIME_UNITS_MAP.get(value);
   }

   public static final String printTimeUnits(TimeUnit type)
   {
      return TIME_UNITS_TO_STRING_MAP.get(type);
   }

   public static final String printDateTime(Date value)
   {
      return (value == null ? null : getDateFormat().format(value));
   }

   public static final Date parseDateTime(String value)
   {
      Date result = null;

      if (value != null && value.length() != 0)
      {
         try
         {
            result = getDateFormat().parse(value);
         }

         catch (ParseException ex)
         {
            // Ignored
         }
      }

      return result;
   }

   private static final DateFormat getDateFormat()
   {
      DateFormat df = DATE_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
         df.setLenient(false);
      }
      return (df);

   }

   public static final Duration parseDuration(String value)
   {
      Duration result = null;
      if (value != null)
      {
         int split = value.indexOf(' ');
         if (split != -1)
         {
            double durationValue = Double.parseDouble(value.substring(0, split));
            TimeUnit durationUnits = parseTimeUnits(value.substring(split + 1));

            result = Duration.getInstance(durationValue, durationUnits);

         }
      }
      return result;
   }

   public static final String printDuration(Duration duration)
   {
      String result = null;
      if (duration != null)
      {
         result = duration.getDuration() + " " + printTimeUnits(duration.getUnits());
      }
      return result;
   }

   private static final Map<String, ResourceType> STRING_TO_RESOURCE_TYPE_MAP = new HashMap<String, ResourceType>();
   static
   {
      STRING_TO_RESOURCE_TYPE_MAP.put("Labor", ResourceType.WORK);
      STRING_TO_RESOURCE_TYPE_MAP.put("Non-Labor", ResourceType.MATERIAL);
   }

   private static final Map<ResourceType, String> RESOURCE_TYPE_TO_STRING_MAP = new EnumMap<ResourceType, String>(ResourceType.class);
   static
   {
      RESOURCE_TYPE_TO_STRING_MAP.put(ResourceType.WORK, "Labor");
      RESOURCE_TYPE_TO_STRING_MAP.put(ResourceType.MATERIAL, "Non-Labor");
      RESOURCE_TYPE_TO_STRING_MAP.put(ResourceType.COST, "Non-Labor");
   }

   private static final Map<String, TimeUnit> STRING_TO_TIME_UNITS_MAP = new HashMap<String, TimeUnit>();
   static
   {
      STRING_TO_TIME_UNITS_MAP.put("Days", TimeUnit.DAYS);
      STRING_TO_TIME_UNITS_MAP.put("days", TimeUnit.DAYS);
      STRING_TO_TIME_UNITS_MAP.put("day", TimeUnit.DAYS);
   }

   private static final Map<TimeUnit, String> TIME_UNITS_TO_STRING_MAP = new EnumMap<TimeUnit, String>(TimeUnit.class);
   static
   {
      TIME_UNITS_TO_STRING_MAP.put(TimeUnit.DAYS, "Days");
   }

   private static Map<String, RelationType> NAME_TO_RELATION_TYPE = new HashMap<String, RelationType>();
   static
   {
      NAME_TO_RELATION_TYPE.put("FinishToFinish", RelationType.FINISH_FINISH);
      NAME_TO_RELATION_TYPE.put("FinishToStart", RelationType.FINISH_START);
      NAME_TO_RELATION_TYPE.put("StartToFinish", RelationType.START_FINISH);
      NAME_TO_RELATION_TYPE.put("StartToStart", RelationType.START_START);
   }

   private static Map<RelationType, String> RELATION_TYPE_TO_NAME = new HashMap<RelationType, String>();
   static
   {
      RELATION_TYPE_TO_NAME.put(RelationType.FINISH_FINISH, "FinishToFinish");
      RELATION_TYPE_TO_NAME.put(RelationType.FINISH_START, "FinishToStart");
      RELATION_TYPE_TO_NAME.put(RelationType.START_FINISH, "StartToFinish");
      RELATION_TYPE_TO_NAME.put(RelationType.START_START, "StartToStart");
   }

   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>();

}
