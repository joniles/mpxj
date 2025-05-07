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

package org.mpxj.phoenix;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.time.DayOfWeek;
import org.mpxj.Duration;
import org.mpxj.RelationType;
import org.mpxj.ResourceType;
import org.mpxj.TimeUnit;
import org.mpxj.common.LocalDateTimeHelper;

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

   /**
    * Retrieve an integer in the form required by Phoenix.
    *
    * @param value integer value
    * @return formatted integer
    */
   public static final String printInteger(Integer value)
   {
      return value == null ? null : value.toString();
   }

   /**
    * Convert the Phoenix representation of an integer into a Java Integer instance.
    *
    * @param value Phoenix integer
    * @return Java Integer instance
    */
   public static final Integer parseInteger(String value)
   {
      return Integer.valueOf(value);
   }

   /**
    * Convert the Phoenix representation of a resource type into a ResourceType instance.
    *
    * @param value Phoenix resource type
    * @return ResourceType instance
    */
   public static final ResourceType parseResourceType(String value)
   {
      return STRING_TO_RESOURCE_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a resource type in the form required by Phoenix.
    *
    * @param type ResourceType instance
    * @return formatted resource type
    */
   public static final String printResourceType(ResourceType type)
   {
      return RESOURCE_TYPE_TO_STRING_MAP.get(type);
   }

   /**
    * Convert the Phoenix representation of a task relationship type into a RelationType instance.
    *
    * @param value Phoenix relationship type
    * @return RelationType instance
    */
   public static final RelationType parseRelationType(String value)
   {
      return NAME_TO_RELATION_TYPE.get(value);
   }

   /**
    * Retrieve a relation type in the form required by Phoenix.
    *
    * @param type RelationType instance
    * @return formatted relation type
    */
   public static final String printRelationType(RelationType type)
   {
      return RELATION_TYPE_TO_NAME.get(type);
   }

   /**
    * Convert the Phoenix representation of a time unit into a TimeUnit instance.
    *
    * @param value Phoenix time unit
    * @return TimeUnit instance
    */
   public static final TimeUnit parseTimeUnits(String value)
   {
      return STRING_TO_TIME_UNITS_MAP.get(value);
   }

   /**
    * Retrieve a time unit in the form required by Phoenix.
    *
    * @param type TimeUnit instance
    * @return formatted time unit
    */
   public static final String printTimeUnits(TimeUnit type)
   {
      return TIME_UNITS_TO_STRING_MAP.get(type);
   }

   /**
    * Retrieve a date time in the form required by Phoenix.
    *
    * @param value Date instance
    * @return formatted date time
    */
   public static final String printDateTime(LocalDateTime value)
   {
      return (value == null ? null : DATE_FORMAT.format(value));
   }

   /**
    * Convert the Phoenix representation of a date time into a Date instance.
    *
    * @param value Phoenix date time
    * @return Date instance
    */
   public static final LocalDateTime parseDateTime(String value)
   {
      if (value == null || value.isEmpty() || value.equals(NOT_A_DATE_TIME))
      {
         return null;
      }

      if (value.equals(PLUS_INFINITY))
      {
         return LocalDateTimeHelper.END_DATE_NA;
      }

      LocalDateTime result = null;

      try
      {
         result = LocalDateTime.parse(value, DATE_FORMAT);
      }

      catch (DateTimeParseException ex)
      {
         // Ignored
      }

      return result;
   }

   /**
    * Convert the Phoenix representation of a duration into a Duration instance.
    *
    * @param value Phoenix duration
    * @return Duration instance
    */
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

   /**
    * Retrieve a duration in the form required by Phoenix.
    *
    * @param duration Duration instance
    * @return formatted duration
    */
   public static final String printDuration(Duration duration)
   {
      String result = null;
      if (duration != null)
      {
         result = duration.getDuration() + " " + printTimeUnits(duration.getUnits());
      }
      return result;
   }

   /**
    * Convert the Phoenix representation of a day into a Day instance.
    *
    * @param value Phoenix day
    * @return Day instance
    */
   public static final DayOfWeek parseDay(String value)
   {
      return NAME_TO_DAY.get(value);
   }

   /**
    * Retrieve a finish date time in the form required by Phoenix.
    *
    * @param value Date instance
    * @return formatted date time
    */
   public static final String printFinishDateTime(LocalDateTime value)
   {
      if (value != null)
      {
         value = value.plusDays(1);
      }
      return (value == null ? null : DATE_FORMAT.format(value));
   }

   /**
    * Convert the Phoenix representation of a finish date time into a Date instance.
    *
    * @param value Phoenix date time
    * @return Date instance
    */
   public static final LocalDateTime parseFinishDateTime(String value)
   {
      LocalDateTime result = parseDateTime(value);
      if (result != null)
      {
         result = result.minusDays(1);
      }
      return result;
   }

   /**
    * Retrieve a day in the form required by Phoenix.
    *
    * @param value Day instance
    * @return formatted day
    */
   public static final String printDay(DayOfWeek value)
   {
      return DAY_TO_NAME.get(value);
   }

   private static final Map<String, ResourceType> STRING_TO_RESOURCE_TYPE_MAP = new HashMap<>();
   static
   {
      STRING_TO_RESOURCE_TYPE_MAP.put("Labor", ResourceType.WORK);
      STRING_TO_RESOURCE_TYPE_MAP.put("Non-Labor", ResourceType.MATERIAL);
   }

   private static final Map<ResourceType, String> RESOURCE_TYPE_TO_STRING_MAP = new EnumMap<>(ResourceType.class);
   static
   {
      RESOURCE_TYPE_TO_STRING_MAP.put(ResourceType.WORK, "Labor");
      RESOURCE_TYPE_TO_STRING_MAP.put(ResourceType.MATERIAL, "Non-Labor");
      RESOURCE_TYPE_TO_STRING_MAP.put(ResourceType.COST, "Non-Labor");
   }

   private static final Map<String, TimeUnit> STRING_TO_TIME_UNITS_MAP = new HashMap<>();
   static
   {
      STRING_TO_TIME_UNITS_MAP.put("Days", TimeUnit.DAYS);
      STRING_TO_TIME_UNITS_MAP.put("days", TimeUnit.DAYS);
      STRING_TO_TIME_UNITS_MAP.put("day", TimeUnit.DAYS);
   }

   private static final Map<TimeUnit, String> TIME_UNITS_TO_STRING_MAP = new EnumMap<>(TimeUnit.class);
   static
   {
      TIME_UNITS_TO_STRING_MAP.put(TimeUnit.DAYS, "Days");
   }

   private static final Map<String, RelationType> NAME_TO_RELATION_TYPE = new HashMap<>();
   static
   {
      NAME_TO_RELATION_TYPE.put("FinishToFinish", RelationType.FINISH_FINISH);
      NAME_TO_RELATION_TYPE.put("FinishToStart", RelationType.FINISH_START);
      NAME_TO_RELATION_TYPE.put("StartToFinish", RelationType.START_FINISH);
      NAME_TO_RELATION_TYPE.put("StartToStart", RelationType.START_START);
   }

   private static final Map<RelationType, String> RELATION_TYPE_TO_NAME = new HashMap<>();
   static
   {
      RELATION_TYPE_TO_NAME.put(RelationType.FINISH_FINISH, "FinishToFinish");
      RELATION_TYPE_TO_NAME.put(RelationType.FINISH_START, "FinishToStart");
      RELATION_TYPE_TO_NAME.put(RelationType.START_FINISH, "StartToFinish");
      RELATION_TYPE_TO_NAME.put(RelationType.START_START, "StartToStart");
   }

   private static final Map<String, DayOfWeek> NAME_TO_DAY = new HashMap<>();
   static
   {
      NAME_TO_DAY.put("Mon", DayOfWeek.MONDAY);
      NAME_TO_DAY.put("Tue", DayOfWeek.TUESDAY);
      NAME_TO_DAY.put("Wed", DayOfWeek.WEDNESDAY);
      NAME_TO_DAY.put("Thu", DayOfWeek.THURSDAY);
      NAME_TO_DAY.put("Fri", DayOfWeek.FRIDAY);
      NAME_TO_DAY.put("Sat", DayOfWeek.SATURDAY);
      NAME_TO_DAY.put("Sun", DayOfWeek.SUNDAY);
   }

   private static final Map<DayOfWeek, String> DAY_TO_NAME = new HashMap<>();
   static
   {
      DAY_TO_NAME.put(DayOfWeek.MONDAY, "Mon");
      DAY_TO_NAME.put(DayOfWeek.TUESDAY, "Tue");
      DAY_TO_NAME.put(DayOfWeek.WEDNESDAY, "Wed");
      DAY_TO_NAME.put(DayOfWeek.THURSDAY, "Thu");
      DAY_TO_NAME.put(DayOfWeek.FRIDAY, "Fri");
      DAY_TO_NAME.put(DayOfWeek.SATURDAY, "Sat");
      DAY_TO_NAME.put(DayOfWeek.SUNDAY, "Sun");
   }

   private static final String NOT_A_DATE_TIME = "not-a-date-time";
   private static final String PLUS_INFINITY = "+infinity";

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss[.SSSSSS]");
}
