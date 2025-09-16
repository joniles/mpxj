/*
 * file:       MapRow.java
 * author:     Jon Iles
 * date:       2025-08-19
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

package org.mpxj.pwa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mpxj.AccrueType;
import org.mpxj.BookingType;
import org.mpxj.ConstraintType;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.Notes;
import org.mpxj.Priority;
import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.ScheduleFrom;
import org.mpxj.TaskMode;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.mpp.TaskTypeHelper;
import org.mpxj.mpp.WorkContourHelper;

/**
 * Represents data deserialized from JSON. Provides method to return correctly typed values.
 */
class MapRow extends LinkedHashMap<String, Object>
{
   /**
    * Set the current project this data is being used with.
    *
    * @param project current project
    */
   public void setProject(ProjectFile project)
   {
      m_project = project;
   }

   /**
    * Retrieve a deserialized list as a list of MapRow instances.
    *
    * @param key map key
    * @return list of MapRow instances
    */
   public List<MapRow> getList(String key)
   {
      @SuppressWarnings("unchecked")
      List<MapRow> row = (List<MapRow>) get(key);
      return row == null ? Collections.emptyList() : row;
   }

   /**
    * Retrieve a deserialized object as a MapRow instance.
    *
    * @param key map key
    * @return MapRow instance
    */
   public MapRow getMapRow(String key)
   {
      return (MapRow) get(key);
   }

   /**
    * Retrieve a string value.
    *
    * @param key map key
    * @return string value
    */
   public String getString(String key)
   {
      return String.valueOf(get(key));
   }

   /**
    * Retrieve a UUID value.
    *
    * @param key map key
    * @return UUID value
    */
   public UUID getUUID(String key)
   {
      return (UUID) getObject(key, DataType.GUID);
   }

   /**
    * Retrieve a LocalDate value.
    *
    * @param key map key
    * @return LocalDate value
    */
   public LocalDate getLocalDate(String key)
   {
      LocalDateTime result = (LocalDateTime) getObject(key, DataType.DATE);
      return result == null ? null : result.toLocalDate();
   }

   /**
    * Retrieve an Integer value.
    *
    * @param key map key
    * @return Integer value
    */
   public Integer getInteger(String key)
   {
      return (Integer) getObject(key, DataType.INTEGER);
   }

   /**
    * Retrieve an int value.
    *
    * @param key map key
    * @return int value
    */
   public int getInt(String key)
   {
      return NumberHelper.getInt(getInteger(key));
   }

   /**
    * Retrieve a boolean value.
    *
    * @param key map key
    * @return boolean value
    */
   public boolean getBool(String key)
   {
      return BooleanHelper.getBoolean((Boolean) get(key));
   }

   /**
    * Retrieve a value expressed as a specific type.
    *
    * @param key map key
    * @param type required type
    * @return value as the required type
    */
   public Object getObject(String key, DataType type)
   {
      Object value = get(key);
      if (value == null)
      {
         return null;
      }

      switch (type)
      {
         case STRING:
         {
            return String.valueOf(value);
         }

         case DATE:
         {
            return getDateFromString(String.valueOf(value));
         }

         case GUID:
         {
            return getUuidFromString(String.valueOf(value));
         }

         case DURATION:
         {
            if (key.startsWith("LocalCustom"))
            {
               return getDurationFromString(String.valueOf(value));
            }

            double time = Double.parseDouble(String.valueOf(value));
            if (key.endsWith("Milliseconds"))
            {
               time = time / (1000.0 * 60.0 * 60.0);
            }

            return Duration.getInstance(time, TimeUnit.HOURS);
         }

         case WORK:
         case DELAY:
         {
            double time = Double.parseDouble(String.valueOf(value));
            if (key.endsWith("Milliseconds"))
            {
               time = time / (1000.0 * 60.0 * 60.0);
            }

            return Duration.getInstance(time, TimeUnit.HOURS);
         }

         case CURRENCY:
         {
            return Double.valueOf(String.valueOf(value));
         }

         case SCHEDULE_FROM:
         {
            return ((Boolean) value).booleanValue() ? ScheduleFrom.START : ScheduleFrom.FINISH;
         }

         case ACCRUE:
         {
            return AccrueType.getInstance((Integer) value);
         }

         case DAY:
         {
            return DayOfWeekHelper.getInstance(NumberHelper.getInt((Integer) value) + 1);
         }

         case TIME:
         {
            return LocalTime.parse(String.valueOf(value), DATE_TIME_FORMAT);
         }

         case TASK_TYPE:
         {
            return TaskTypeHelper.getInstance(NumberHelper.getInt((Integer) value));
         }

         case CURRENCY_SYMBOL_POSITION:
         {
            return CurrencySymbolPosition.getInstance(NumberHelper.getInt((Integer) value));
         }

         case RATE:
         {
            return new Rate((Number) value, TimeUnit.HOURS);
         }

         case UNITS:
         {
            return Double.valueOf(((Number) value).doubleValue() * 100);
         }

         case BOOKING_TYPE:
         {
            return BookingType.getInstance(NumberHelper.getInt((Integer) value));
         }

         case RATE_UNITS:
         {
            return TimeUnit.getInstance(NumberHelper.getInt((Integer) value) - 1);
         }

         case TASK_MODE:
         {
            return ((Boolean) value).booleanValue() ? TaskMode.MANUALLY_SCHEDULED : TaskMode.AUTO_SCHEDULED;
         }

         case PRIORITY:
         {
            return Priority.getInstance(NumberHelper.getInt((Integer) value));
         }

         case CONSTRAINT:
         {
            return ConstraintType.getInstance(NumberHelper.getInt((Integer) value) - 1);
         }

         case NUMERIC:
         {
            if (value instanceof String)
            {
               return Double.valueOf((String) value);
            }
            return value;
         }

         case WORK_CONTOUR:
         {
            return WorkContourHelper.getInstance(m_project, ((Integer) value).intValue());
         }

         case NOTES:
         {
            return new Notes(String.valueOf(value));
         }

         case INTEGER:
         case PERCENTAGE:
         case BOOLEAN:
         case SHORT:
         {
            return value;
         }

         default:
         {
            throw new PwaException(type + " not handled");
         }
      }
   }

   /**
    * Retrieve a LocalDateTime instance from a string representation.
    *
    * @param value string value
    * @return LocalDateTime instance
    */
   private LocalDateTime getDateFromString(String value)
   {
      if (value.equals("0001-01-01T00:00:00"))
      {
         return null;
      }

      return LocalDateTimeHelper.parseBest(DATE_TIME_FORMAT, value);
   }

   /**
    * Retrieve a UUID from a string representation.
    *
    * @param value string value
    * @return UUID instance
    */
   private UUID getUuidFromString(String value)
   {
      if (value.equals("00000000-0000-0000-0000-000000000000"))
      {
         return null;
      }

      return UUID.fromString(value);
   }

   /**
    * Retrieve a Duration instance from a string representation.
    *
    * @param value string value
    * @return Duration instance
    */
   private Object getDurationFromString(String value)
   {
      Matcher match = DURATION_REGEX.matcher(value);
      if (!match.matches())
      {
         return value;
      }

      double duration = Double.parseDouble(match.group(1));
      TimeUnit unit = TIME_UNIT_MAP.get(match.group(2));
      if (unit == null)
      {
         return value;
      }

      return Duration.getInstance(duration, unit);
   }

   private transient ProjectFile m_project;

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'[HH:mm:ss.SSS][HH:mm:ss.SS][HH:mm:ss.S][HH:mm:ss]");
   private static final Pattern DURATION_REGEX = Pattern.compile("(-?\\d+\\.\\d+|-?\\d+)(emo|mo|em|eh|ed|ew|ey|e%|m|h|d|w|%|y)");
   private static final Map<String, TimeUnit> TIME_UNIT_MAP = Arrays.stream(TimeUnit.values()).collect(Collectors.toMap(TimeUnit::getName, t -> t));
}
